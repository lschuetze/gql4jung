/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang.time.DurationFormatUtils;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.resultviews.GraphBasedResultView;
import nz.ac.massey.cs.gql4jung.browser.resultviews.TableBasedResultView;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.io.JarReader;
import nz.ac.massey.cs.gql4jung.io.ODEMReader;
import nz.ac.massey.cs.gql4jung.io.QueryResultsExporter2CSV;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResults.Cursor;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Stand alone user interface to run queries and visualise results.
 * The graph visualisation is based on JUNG. 
 * @author Jens Dietrich
 */
public class ResultBrowser extends JFrame {
	private static final String TITLE = "Architectural smells explorer";
	// model
	private DirectedGraph<Vertex,Edge> data = null;
	private Motif query = null;
	private QueryResults results = new QueryResults();
	private GQL engine = new GQLImpl();
	private Thread queryThread = null;
	private long computationStarted = -1;
	private boolean computeVariants = false;
	
	// parts
	private JToolBar toolbar;
	private JLabel cursorField = null;
	private JLabel dataField = null;
	private JLabel queryField = null;
	private JLabel timeField = null;
	private JProgressBar statusField = null;
	private JPanel mainPanel = null;
	private JPopupMenu popup;
	private MouseAdapter popupListener;
	private JTabbedPane tabbedPane = null;
	private JMenuBar menuBar = null;
	
	// actions
	private AbstractAction actExit;
	private AbstractAction actLoadDataFromXML;
	private AbstractAction actLoadDataFromJars;
	private AbstractAction actLoadQuery;
	private AbstractAction actRunQuery;
	private AbstractAction actCancelQuery;
	private AbstractAction actNextMinorInstance;
	private AbstractAction actPreviousMinorInstance;
	private AbstractAction actNextMajorInstance;
	private AbstractAction actPreviousMajorInstance;
	private AbstractAction actExport2CSV;
	private AbstractAction actAbout;
	private List<AbstractAction> actLoadBuiltInQueries = new ArrayList<AbstractAction>();
	
	private ResultView[] resultViewers = {
		new GraphBasedResultView(),
		new TableBasedResultView()
	};
	
	
	private enum Status {
		waiting,computing,finished,cancelled
	}
	private Status status = Status.waiting;

	public static void main(String[] args) {
		ResultBrowser browser = new ResultBrowser();
		browser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		browser.setLocation(100,100);
		browser.setSize(800,500);
		browser.setVisible(true);
	}
	
	public ResultBrowser() throws HeadlessException {
		super();
		init();
	}
	public ResultBrowser(GraphicsConfiguration gc) {
		super(gc);
		init();
	}
	public ResultBrowser(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
	}
	public ResultBrowser(String title) throws HeadlessException {
		super(title);
		init();
	}
	private void init() {
		this.setTitle(TITLE);
				
		mainPanel = new JPanel(new BorderLayout(5,5));
		this.tabbedPane = new JTabbedPane();
		mainPanel.add(tabbedPane,BorderLayout.CENTER);
		// result viewers
		for (ResultView view:this.resultViewers) {
			view.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
			this.tabbedPane.add(view.getName(),view);
		}		
		this.setContentPane(mainPanel);
		
		// start listening to events
		QueryResults.QueryResultListener listener = new QueryResults.QueryResultListener() {
			@Override
			public void resultsChanged(QueryResults source) {
				if (results.getCursor().major==-1 && results.hasResults()) {
					actNextMajorInstance();
				}
				updateStatus();
				updateComputationTime();
			}

			@Override
			public void progressMade(int progress, int total) {
				statusField.setMaximum(total);
				statusField.setValue(progress);
				updateComputationTime();
			}
		};
		this.results.addListener(listener);
		
		// graph
		initActions();
		initPopupMenu();
		initToolbar();
		initMenubar();
		initStatusBar();

		
		// load sample data
		// TODO remove
		this.loadDataFromXML(new File("exampledata/ant.jar.graphml"));
		this.loadQuery(new File("queries/awd.xml"));
		
		updateActions();
		updateStatus();
	}
	private void initMenubar() {
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.add(actLoadDataFromXML);
		menu.add(actLoadDataFromJars);
		menu.add(actLoadQuery);
		if (this.actLoadBuiltInQueries.size()>0) {
			JMenu menu2 = new JMenu("Built-in queries");
			for (Action act:actLoadBuiltInQueries) {
				menu2.add(act);
			}
			menu.add(menu2);
		}
		menu.addSeparator();
		menu.add(actExport2CSV);
		menu.addSeparator();
		menu.add(actExit);
		menuBar.add(menu);
		
		menu = new JMenu("Query");
		menu.setMnemonic(KeyEvent.VK_Q);
		menu.add(actRunQuery);
		menu.add(actCancelQuery);
		menuBar.add(menu);
		
		menu = new JMenu("Explore");
		menu.setMnemonic(KeyEvent.VK_X);
		menu.add(actPreviousMajorInstance);	
		menu.add(actNextMajorInstance);	
		menu.addSeparator();
		menu.add(actPreviousMinorInstance);
		menu.add(actNextMinorInstance);
		menuBar.add(menu);
		
		menu = new JMenu("Options");
		final JCheckBoxMenuItem chkVariants = new JCheckBoxMenuItem("compute all variants (slower)");
		chkVariants.setSelected(this.computeVariants);
		chkVariants.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				computeVariants = chkVariants.isSelected();
			}});
		menu.add(chkVariants);
		menuBar.add(menu);
		
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.add(this.actAbout);
		menuBar.add(menu);
	}

	private void initStatusBar() {
		this.cursorField = new JLabel();
		this.queryField = new JLabel();
		this.dataField = new JLabel();
		this.statusField = new JProgressBar();
		statusField.setStringPainted(true);
		statusField.setBorderPainted(true);

		this.timeField = new JLabel();
		Font font = new Font("monospaced",Font.PLAIN,this.cursorField.getFont().getSize());
		this.cursorField.setFont(font);
		this.dataField.setFont(font);
		this.queryField.setFont(font);
		this.timeField.setFont(font);
		this.statusField.setFont(font);
		
		JPanel statusBar = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int cols = 6;
		c.insets = new Insets(0,3,0,3);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = c.NONE;
		c.weightx = 0;
		c.anchor = c.EAST;
		c.gridwidth = 1;
		statusBar.add(new JLabel("data:",JLabel.RIGHT),c);
		c.gridx = 1;
		c.fill = c.HORIZONTAL;
		c.weightx = 1;
		c.anchor = c.WEST;
		c.gridwidth = cols-1;
		statusBar.add(dataField,c);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = c.NONE;
		c.weightx = 0;
		c.anchor = c.EAST;
		c.gridwidth = 1;
		statusBar.add(new JLabel("query:",JLabel.RIGHT),c);
		c.gridx = 1;
		c.fill = c.HORIZONTAL;
		c.weightx = 1;
		c.anchor = c.WEST;
		c.gridwidth = cols-1;
		statusBar.add(queryField,c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.fill = c.NONE;
		c.weightx = 0;
		c.anchor = c.EAST;
		c.gridwidth = 1;
		statusBar.add(new JLabel("status:",JLabel.RIGHT),c);
		c.gridx = 1;
		c.fill = c.HORIZONTAL;
		c.weightx = 1;
		c.anchor = c.WEST;
		statusBar.add(statusField,c);
		
		c.gridx = 2;
		c.gridy = 2;
		c.fill = c.NONE;
		c.weightx = 0;
		c.anchor = c.EAST;
		c.gridwidth = 1;
		statusBar.add(new JLabel("cursor:",JLabel.RIGHT),c);
		c.gridx = 3;
		c.fill = c.HORIZONTAL;
		c.weightx = 1;
		c.anchor = c.WEST;
		statusBar.add(cursorField,c);
		
		c.gridx = 4;
		c.gridy = 2;
		c.fill = c.NONE;
		c.weightx = 0;
		c.anchor = c.EAST;
		c.gridwidth = 1;
		statusBar.add(new JLabel("computation time:",JLabel.RIGHT),c);
		c.gridx = 5;
		c.fill = c.HORIZONTAL;
		c.weightx = 1;
		c.anchor = c.WEST;
		statusBar.add(timeField,c);
		
		mainPanel.add(statusBar,BorderLayout.SOUTH);
		statusBar.setBorder(BorderFactory.createEtchedBorder());		
	}

	private void initActions() {
		
		actExit = new AbstractAction("exit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		
		actLoadDataFromXML = new AbstractAction("load graph from XML",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromXML();
			}
		};
		actLoadDataFromXML.putValue(Action.SHORT_DESCRIPTION, "load a program dependency graph from a graphml file");

		actLoadDataFromJars = new AbstractAction("load graph from byte code",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromJars();
			}
		};
		actLoadDataFromJars.putValue(Action.SHORT_DESCRIPTION, "load graph from jar files and/or folders");

		
		actLoadQuery = new AbstractAction("load query",getIcon("Import16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadQuery();
			}
		};
		actLoadQuery.putValue(Action.SHORT_DESCRIPTION, "load a query from a xml file");
		
		actRunQuery = new AbstractAction("run query",getIcon("Play16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actRunQuery();
			}
		};
		actRunQuery.putValue(Action.SHORT_DESCRIPTION, "execute the query");
		
		actCancelQuery = new AbstractAction("cancel",getIcon("Stop16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actCancelQuery();
			}
		};
		actCancelQuery.putValue(Action.SHORT_DESCRIPTION, "cancel the currently running query");
		
		actExport2CSV = new AbstractAction("export to csv",getIcon("Export16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actExport2CSV();
			}
		};
		actExport2CSV.putValue(Action.SHORT_DESCRIPTION, "export the query results to a CSV (spreadsheet) file");
		
		actPreviousMinorInstance = new AbstractAction("previous minor instance",getIcon("PreviousMinor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actPreviousMinorInstance();
			}
		};	
		actPreviousMinorInstance.putValue(Action.SHORT_DESCRIPTION, "show the previous variant of the selected instance");
		
		actNextMinorInstance = new AbstractAction("next minor instance",getIcon("NextMinor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actNextMinorInstance();
			}
		};
		actNextMinorInstance.putValue(Action.SHORT_DESCRIPTION, "show the next variant of the selected instance");
		
		actPreviousMajorInstance = new AbstractAction("previous major instance",getIcon("PreviousMajor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actPreviousMajorInstance();
			}
		};	
		actPreviousMajorInstance.putValue(Action.SHORT_DESCRIPTION, "show the previous instance");
		
		actNextMajorInstance = new AbstractAction("next major instance",getIcon("NextMajor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actNextMajorInstance();
			}
		};	
		actNextMajorInstance.putValue(Action.SHORT_DESCRIPTION, "show the next instance");
		
		actAbout = new AbstractAction("about") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = 
					"<html><b>"+TITLE+"</b><br/><br/>" +
					"Copyright 2009 Jens Dietrich<br/>" + 
					"Massey University, NZ<br/>" + 
					"Licensed under the Apache License, Version 2.0</html>";
				JOptionPane.showMessageDialog(ResultBrowser.this,msg);
			}
		};	
		actAbout.putValue(Action.SHORT_DESCRIPTION, "about this software");
		
		initBuiltInQueries();
	}
	
	private void initBuiltInQueries() {
		initBuildInQuery("missing decoupling by abstraction","queries/awd.xml");
		initBuildInQuery("circular dependencies between packages","queries/cd.xml");
		initBuildInQuery("db 2 ui layer dependencies","queries/db2ui.xml");
		initBuildInQuery("multiple dependency clusters in same package","queries/cns.xml");
	}

	private void initBuildInQuery(String name, final String file) {
		final File f = new File(file);
		if (f.exists()) {
			AbstractAction act = new AbstractAction(name) {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadQuery(f);
				}			
			};
			this.actLoadBuiltInQueries.add(act);
			log("added action to load query from file "+f);
		}
		else {
			log("cannot add action to load query from file "+f);
		}
	}

	private void initToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		mainPanel.add(toolbar,BorderLayout.NORTH);
		toolbar.add(actLoadDataFromXML);
		toolbar.add(actLoadDataFromJars);
		toolbar.add(actLoadQuery);
		toolbar.addSeparator();
		toolbar.add(actRunQuery);
		toolbar.add(actCancelQuery);
		toolbar.addSeparator();
		toolbar.add(actPreviousMajorInstance);	
		toolbar.add(actNextMajorInstance);	
		toolbar.addSeparator();
		toolbar.add(actPreviousMinorInstance);
		toolbar.add(actNextMinorInstance);
		toolbar.addSeparator();
		toolbar.add(actExport2CSV);
	}
	
	private void initPopupMenu() {
		popup = new JPopupMenu();
		popup.add(this.actRunQuery);
		popup.add(this.actCancelQuery);
		popup.addSeparator();
		popup.add(this.actLoadDataFromXML);
		popup.add(this.actLoadDataFromJars);
		popup.add(this.actLoadQuery);
		popup.addSeparator();
		popup.add(this.actNextMajorInstance);
		popup.add(this.actPreviousMajorInstance);
		popup.addSeparator();
		popup.add(this.actNextMinorInstance);
		popup.add(this.actPreviousMinorInstance);
		popupListener = new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        showPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        showPopup(e);
		    }
		    private void showPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		};
		this.mainPanel.addMouseListener(popupListener);		
	}
	
	private Icon getIcon(String name) {
		URL url = this.getClass().getResource("/nz/ac/massey/cs/gql4jung/browser/icons/"+name);
		return new ImageIcon(url);
	}

	private void actCancelQuery() {
		this.engine.cancel();
		this.queryThread = null;
		this.status = Status.cancelled;
		updateActions();
		updateStatus();
		this.computationStarted = -1;
		log("query cancelled");
	}

	private void actPreviousMajorInstance() {
		Cursor cursor = this.results.previousMajorInstance();
		this.selectAndDisplay(cursor);
	}
	private void actNextMajorInstance() {
		Cursor cursor = this.results.nextMajorInstance();
		this.selectAndDisplay(cursor);
	}

	private void actPreviousMinorInstance() {
		Cursor cursor = this.results.previousMinorInstance();
		this.selectAndDisplay(cursor);
	}
	private void actNextMinorInstance() {
		Cursor cursor = this.results.nextMinorInstance();
		this.selectAndDisplay(cursor);
	}
	private void actExport2CSV() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".csv");
			}
			@Override
			public String getDescription() {
				return "csv files";
			}			
		};
		fc.setFileFilter(filter);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
            try {
				exporter.export(this.results,file);
				log("results exported to " + file.getAbsolutePath());
				JOptionPane.showMessageDialog(this,"Results have been exported to\n" + file.getAbsolutePath());
			} catch (IOException x) {
				this.handleException("Error exporting file", x);
			}
        }
	}
	private void selectAndDisplay(Cursor cursor) {
		this.updateActions();
		this.updateStatus();
		MotifInstance instance = results.getInstance(cursor);
		this.display(instance);		
	}
	private void actRunQuery() {
		this.results.reset();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				computationStarted = System.currentTimeMillis();
				engine.reset();
				results.reset();
				display(null);
				engine.query(data,query,results,!computeVariants);
				queryThread = null;
				status = Status.finished;
				updateComputationTime();
				updateStatus();
			}			
		};
		this.queryThread = new Thread(runnable);
		this.status = Status.computing;
		updateStatus();
		this.queryThread.start();
		updateActions();
		
	}

	private void actLoadQuery() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./queries"));
		fc.setDialogTitle("Load query");
		int returnVal = fc.showOpenDialog(this);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".xml");
			}
			@Override
			public String getDescription() {
				return "xml";
			}			
		};
		fc.setFileFilter(filter);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadQuery(file);
        }
        updateActions();
        updateStatus();
	}
	private void log(String string) {
		System.out.println(string);
	}
	private void actLoadDataFromXML() {
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				String s = f.getAbsolutePath();
				return s.endsWith(".odem") || s.endsWith(".graphml");
			}
			@Override
			public String getDescription() {
				return "odem or graphml files";
			}			
		};
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(fileFilter);
		fc.setCurrentDirectory(new File("./exampledata"));
		fc.setDialogTitle("Load graph from XML/ODEM or XML/GraphML file");
		int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadDataFromXML(file);
        }
        updateActions();
        updateStatus();
	}
	private void actLoadDataFromJars() {
		FileFilter fileFilter = new FileFilter() {
			String[] extensions = {"jar","zip","war","ear"};
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String s = f.getAbsolutePath();
				for (String x:extensions) {
					if (s.endsWith("."+x)) return true;
				}
				return false;
			}
			@Override
			public String getDescription() {
				return "jar files or class file folders";
			}			
		};
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(fileFilter);
		fc.setCurrentDirectory(new File("./exampledata"));
		fc.setDialogTitle("Load graph from jar files or class folders");
		int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadDataFromJars(file);
        }
        updateActions();
        updateStatus();
        
	}
	private void loadQuery(File file) {
        try {
        	InputStream in = new FileInputStream(file);
            Motif motif = new XMLMotifReader().read(in);
            this.query = motif;
            this.computationStarted = -1;
            this.status = Status.waiting;
            this.queryField.setText(file.getAbsolutePath());
            in.close();
            log("Motif imported from " + file.getAbsolutePath());
            
        }
        catch (Exception x) {
        	handleException("Error loading query",x);
        }
	}
	private void loadDataFromXML(File file) {
		
        try {
            Reader reader = new FileReader(file);
            DirectedGraph<Vertex, Edge> g = null;
            if (file.getAbsolutePath().endsWith(".graphml") || file.getAbsolutePath().endsWith(".xml")) {
            	GraphMLReader input = new GraphMLReader(reader);
            	g =	input.readGraph();
            }
            else if (file.getAbsolutePath().endsWith(".odem")) {
            	ODEMReader input = new ODEMReader(reader);
            	g =	input.readGraph();
            }
            reader.close();
            if (g!=null) {
	            this.data = g;
	            this.status = Status.waiting;
	            this.dataField.setText(file.getAbsolutePath());
	            log("Data imported from " + file.getAbsolutePath());
	            this.computationStarted = -1;
            }
            else {
            	System.err.println("Cannot open file " + file + " - can only read .graphml, .odem and .xml files");
            }
        }
        catch (Exception x) {
        	handleException("Error loading data file",x);
        }
	}
	
	private void loadDataFromJars(File file) {
		
        try {
            DirectedGraph<Vertex, Edge> g = null;
            if (file.getAbsolutePath().endsWith(".jar") || file.isDirectory()) {
            	JarReader input = new JarReader(new File[]{file});
            	g =	input.readGraph();
            }
            if (g!=null) {
	            this.data = g;
	            this.status = Status.waiting;
	            this.dataField.setText(file.getAbsolutePath());
	            log("Data imported from " + file.getAbsolutePath());
	            this.computationStarted = -1;
            }
            else {
            	System.err.println("Cannot open file " + file + " - can only read .graphml, .odem and .xml files");
            }
        }
        catch (Exception x) {
        	handleException("Error loading data file",x);
        }
	}
	
	private void handleException(String string, Exception x) {
		System.err.println(string);
		x.printStackTrace();
	}

	private void nyi() {
		JOptionPane.showMessageDialog(this,"this function is not yet implemented");
	}

	private void updateActions() {
		boolean queryIsRunning = this.queryThread!=null;
		this.actCancelQuery.setEnabled(queryIsRunning);
		this.actRunQuery.setEnabled(!queryIsRunning);
		this.actExport2CSV.setEnabled(!queryIsRunning && results.hasResults());
		this.actNextMajorInstance.setEnabled(this.results.hasNextMajorInstance());
		this.actNextMinorInstance.setEnabled(this.results.hasNextMinorInstance());
		this.actPreviousMajorInstance.setEnabled(this.results.hasPreviousMajorInstance());
		this.actPreviousMinorInstance.setEnabled(this.results.hasPreviousMinorInstance());
	}
	private void updateComputationTime() {
		if (this.computationStarted==-1) {
			this.timeField.setText("");
		}
		else {
			String dur = DurationFormatUtils.formatDuration(System.currentTimeMillis()-this.computationStarted,"H:m:s.S",true);
			this.timeField.setText(dur);
		}
	}
	
	private void updateStatus() {
		// log("update UI");
		Cursor cursor = results.getCursor();
		int majI = cursor.major+1;
		int maxMajI = results.getNumberOfGroups();	
		StringBuffer b = new StringBuffer();
		if (maxMajI>0) {
			b.append("instance ")
			.append(majI)
			.append('/')
			.append(maxMajI);
			if (majI>0) {
				int minI = cursor.minor+1;
				int maxMinI = results.getNumberOfInstances(majI-1);
				b.append(", ")
					.append("variant ")
					.append(minI)
					.append('/')
					.append(maxMinI);
				
			}
			b.append(' ');
		}
		cursorField.setText(b.toString());
		statusField.setString(this.status.toString());
		if (this.status!=Status.computing) statusField.setValue(0);
		//statusField.setIndeterminate(this.status==Status.computing);
		updateActions();
	};
	
	private void display(final MotifInstance instance) {
		for (ResultView view:this.resultViewers) {
			view.display(instance,this.data);
		}	
	}

	
	@Override
	public void dispose() {
		try {
			this.actCancelQuery();
		}
		catch (Exception x){}
		super.dispose();
	}

}
