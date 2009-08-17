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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.queryviews.GraphBasedQueryView;
import nz.ac.massey.cs.gql4jung.browser.queryviews.XMLSourceQueryView;
import nz.ac.massey.cs.gql4jung.browser.resultviews.GraphBasedResultView;
import nz.ac.massey.cs.gql4jung.browser.resultviews.TableBasedResultView;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.io.JarReader;
import nz.ac.massey.cs.gql4jung.io.ODEMReader;
import nz.ac.massey.cs.gql4jung.io.ProgressListener;
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
	
	static {
		// initialise log4j
		Logger.getRootLogger().removeAllAppenders();
		String config = "log4j.properties";
		org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger(ResultBrowser.class).info("Log4j configured from "+config);
	}
	
	private static final String TITLE = "Architectural smells explorer";
	// model
	private DirectedGraph<Vertex,Edge> data = null;
	private Motif query = null;
	private String querySource = null;
	private QueryResults results = new QueryResults();
	private GQL engine = new GQLImpl();
	private Thread queryThread = null;
	private long computationStarted = -1;
	private boolean computeVariants = false;
	
	private static Logger LOG = Logger.getLogger(ResultBrowser.class);
	
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
	private Action actExit;
	private Action actLoadData;
	private Action actLoadDataFromODEM;
	private Action actLoadDataFromGraphML;
	private Action actLoadDataFromJars;
	private Action actLoadDataFromJar;
	private Action actLoadQuery;
	private Action actAnalyseMe;
	private Action actRunQuery;
	private Action actCancelQuery;
	private Action actNextMinorInstance;
	private Action actPreviousMinorInstance;
	private Action actNextMajorInstance;
	private Action actPreviousMajorInstance;
	private Action actExport2CSV;
	private Action actViewGraphData;
	private Action actAbout;
	private List<Action> actLoadBuiltInQueries = new ArrayList<Action>();
	private List<Action> actConfigureViews = new ArrayList<Action>();
	private List<Action> loadActions = new ArrayList<Action>();
	
	private ResultView[] resultViewers = {
		new GraphBasedResultView(),
		new TableBasedResultView()
	};
	private QueryView[] queryViewers = {
		new GraphBasedQueryView(),
		new XMLSourceQueryView()
	};
	
	
	private enum Status {
		waiting,computing,finished,cancelled,loading
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
		// set look and feel
		try {
			Plastic3DLookAndFeel.setPlasticTheme(new com.jgoodies.looks.plastic.theme.Silver());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e) {}
			   
		this.setTitle(TITLE);
				
		mainPanel = new JPanel(new BorderLayout(5,5));
		this.tabbedPane = new JTabbedPane();
		mainPanel.add(tabbedPane,BorderLayout.CENTER);
		// result viewers
		for (QueryView view:this.queryViewers) {
			view.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
			this.tabbedPane.add(view.getName(),view);
		}	
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
		
		final String text = "<html><a href=\"actloadquery\">click here to load query</a></html>";
		this.queryField.setText(text);
		this.queryField.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (queryField.getText().equals(text)) {
							actLoadQuery();
						}
					}
			
				}
		);
		
		final String text2 = "<html><a href=\"actloadquery\">click here to load graph</a></html>";
		this.dataField.setText(text2);
		this.dataField.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (dataField.getText().equals(text2)) {
							actLoadData();
						}
					}
			
				}
		);
		
		// load sample data
		// TODO remove
		// this.loadDataFromGraphML(new File("exampledata/ant.jar.graphml"));
		// this.loadQuery(new File("queries/awd.xml"));
		
		updateActions();
		updateStatus();
		
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				if (query!=null) displayMotif(query,querySource);
			}
			@Override
			public void componentShown(ComponentEvent e) {
			}			
		});

		


	}
	private void initMenubar() {
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		JMenu menuLoad = new JMenu("Load program to analyse");	
		menuLoad.add(actLoadDataFromJar);
		menuLoad.add(actLoadDataFromJars);
		menuLoad.add(actAnalyseMe);
		menu.add(menuLoad);
		JMenu menuImport = new JMenu("Import dependency graph");
		menuImport.add(actLoadDataFromGraphML);
		menuImport.add(actLoadDataFromODEM);
		menu.add(menuImport);
		
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
		for (Action act:this.actConfigureViews) {
			menu.add(act);
		}
		menu.add(this.actViewGraphData);
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
		statusField.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
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
		
		actLoadData = new AbstractAction("load graph",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadData();
			}
		};
		actLoadData.putValue(Action.SHORT_DESCRIPTION, "load graph");
		loadActions.add(actLoadData);
		
		actLoadDataFromODEM = new AbstractAction("load graph from ODEM file",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromODEM();
			}
		};
		actLoadDataFromODEM.putValue(Action.SHORT_DESCRIPTION, "load a program dependency graph from an XML/ODEM file");
		loadActions.add(actLoadDataFromODEM);
		
		actLoadDataFromGraphML = new AbstractAction("load graph from GraphML file",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromGraphML();
			}
		};
		actLoadDataFromGraphML.putValue(Action.SHORT_DESCRIPTION, "load a program dependency graph from an XML/GraphML file");
		loadActions.add(actLoadDataFromGraphML);
		
		actLoadDataFromJars = new AbstractAction("open multiple jars or folders",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromJars();
			}
		};
		actLoadDataFromJars.putValue(Action.SHORT_DESCRIPTION, "load dependency graph from multiple jar files and/or folders");
		loadActions.add(actLoadDataFromJars);
		
		actLoadDataFromJar = new AbstractAction("open jar or folder",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadDataFromJar();
			}
		};
		actLoadDataFromJar.putValue(Action.SHORT_DESCRIPTION, "load dependency graph from single jar file and/or folder");
		loadActions.add(actLoadDataFromJar);
		
		actAnalyseMe = new AbstractAction("analyse me",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actAnalyseMe();
			}
		};
		actAnalyseMe.putValue(Action.SHORT_DESCRIPTION, "load this program as a graph");
		loadActions.add(actAnalyseMe);
		
		actLoadQuery = new AbstractAction("load query",getIcon("Import16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadQuery();
			}
		};
		actLoadQuery.putValue(Action.SHORT_DESCRIPTION, "load a query from a xml file");
		loadActions.add(actLoadQuery);
		
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
		
		actViewGraphData = new AbstractAction("show graph properties") {
			@Override
			public void actionPerformed(ActionEvent e) {
				actShowGraphProperties();
			}
		};	
		actViewGraphData.putValue(Action.SHORT_DESCRIPTION, "show graph properties");
			
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

		class EditResultViewSettingsAction extends AbstractAction {
			
			public EditResultViewSettingsAction(String name,PropertyBean settings) {
				super(name);
				this.settings = settings;
				this.name = name;
			}
			private PropertyBean settings = null;
			private String name = null;
			@Override
			public void actionPerformed(ActionEvent e) {
				PropertyBeanEditor.edit(ResultBrowser.this,settings,name);
				// refresh view
				try {
					Cursor cursor = results.getCursor();
					MotifInstance instance = results.getInstance(cursor);
					displayInstance(instance);	
				}
				catch (Exception x){}
			}
		};
		class EditQueryViewSettingsAction extends AbstractAction {
			
			public EditQueryViewSettingsAction(String name,PropertyBean settings) {
				super(name);
				this.settings = settings;
				this.name = name;
			}
			private PropertyBean settings = null;
			private String name = null;
			private boolean editQuerySettings = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				PropertyBeanEditor.edit(ResultBrowser.this,settings,name);
				// refresh view
				try {
					displayMotif(query,querySource);	
				}
				catch (Exception x){}
			}
		};
		for (QueryView view:this.queryViewers) {
			PropertyBean settings = view.getSettings();
			if (settings!=null) {
				Action act = new EditQueryViewSettingsAction("configure view: "+view.getName(),settings);
				this.actConfigureViews.add(act);
			}
		}
		for (ResultView view:this.resultViewers) {
			PropertyBean settings = view.getSettings();
			if (settings!=null) {
				Action act = new EditResultViewSettingsAction("configure view: "+view.getName(),settings);
				this.actConfigureViews.add(act);
			}
		}
		
		initBuiltInQueries();
	}

	private void initBuiltInQueries() {
		File queries = new File("queries");
		if (!queries.exists() || !queries.isDirectory()) {
			LOG.warn("Cannot find query folder " + queries);
		}
		File[] files = queries.listFiles();
		Arrays.sort(files);
		for (File file:files) {
			if (!file.isDirectory()&&file.getName().endsWith(".xml")) {
				initBuildInQuery(file);
			}
		}
	}

	private void initBuildInQuery(final File file) {
        try {
        	InputStream in = new FileInputStream(file);
            Motif motif = new XMLMotifReader().read(in);
            String name = motif.getName();
            in.close();
			AbstractAction act = new AbstractAction(name) {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadQuery(file);
				}			
			};
			this.actLoadBuiltInQueries.add(act);
			this.loadActions.add(act);
			log("added action to load query from file "+file);
            
        }
        catch (Exception x) {
        	handleException("Error initialising action for built-in query "+file,x);
        }

	}

	private void initToolbar() {
		int offset = 5;
		Border b = BorderFactory.createEmptyBorder(offset,offset,offset,offset);
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		mainPanel.add(toolbar,BorderLayout.NORTH);
		toolbar.add(actLoadData).setBorder(b);
		//toolbar.add(actLoadDataFromXML).setBorder(b);
		//toolbar.add(actLoadDataFromJars).setBorder(b);
		toolbar.add(actLoadQuery).setBorder(b);
		toolbar.addSeparator();
		toolbar.add(actRunQuery).setBorder(b);
		toolbar.add(actCancelQuery).setBorder(b);
		toolbar.addSeparator();
		toolbar.add(actPreviousMajorInstance).setBorder(b);	
		toolbar.add(actNextMajorInstance).setBorder(b);	
		toolbar.addSeparator();
		toolbar.add(actPreviousMinorInstance).setBorder(b);
		toolbar.add(actNextMinorInstance).setBorder(b);
		toolbar.addSeparator();
		toolbar.add(actExport2CSV).setBorder(b);
	}
	
	private void initPopupMenu() {
		popup = new JPopupMenu();
		popup.add(this.actRunQuery);
		popup.add(this.actCancelQuery);
		popup.addSeparator();
		popup.add(this.actLoadDataFromODEM);
		popup.add(this.actLoadDataFromGraphML);
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
		this.displayInstance(instance);		
	}
	private void actRunQuery() {
		this.results.reset();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				computationStarted = System.currentTimeMillis();
				engine.reset();
				results.reset();
				displayInstance(null);
				engine = new GQLImpl(); // create new engine in case the old one has been cancelled 
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
	
	private void actShowGraphProperties() {
		PropertyBeanEditor.show(this,new GraphData(data),"Graph properties");
	}
	
	private void log(String string) {
		LOG.info(string);
	}
	private void actLoadDataFromODEM() {
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				String s = f.getAbsolutePath();
				return f.isDirectory() || s.endsWith(".odem");
			}
			@Override
			public String getDescription() {
				return "odem files";
			}			
		};
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(fileFilter);
		fc.setCurrentDirectory(new File("./exampledata"));
		fc.setDialogTitle("Load graph from XML/ODEM file");
		int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadDataFromODEM(file);
        }
	}
	private void actLoadDataFromGraphML() {
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				String s = f.getAbsolutePath();
				return f.isDirectory() || s.endsWith(".graphml");
			}
			@Override
			public String getDescription() {
				return "graphml files";
			}			
		};
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(fileFilter);
		fc.setCurrentDirectory(new File("./exampledata"));
		fc.setDialogTitle("Load graph XML/GraphML file");
		int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadDataFromGraphML(file);
        }
	}
	private void actLoadData() {
		String[] possibleValues = {
			"<html>single jar<br/>or folder</html>",
			"<html>multiple jars<br/>or folders</html>",
			"<html>GraphML<br/> (XML) file<br/></html>", 
			"<html>ODEM<br/> (XML) file</html>",
			"<html><b><tt>this</tt></b> program -<br/>all jars used</html>"
		};
		int selectedValue = JOptionPane.showOptionDialog(
				this,
				"Import or extract graph from one of the following sources:",
				"",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				possibleValues,
				possibleValues[0]
				
		);    
		switch (selectedValue) {
			case 0:actLoadDataFromJar();break;
			case 1:actLoadDataFromJars();break;
			case 2:actLoadDataFromGraphML();break;
			case 3:actLoadDataFromODEM();break;
			case 4:actAnalyseMe();break;
		}
		
	}
	
	private void actLoadDataFromJars() {
		List<File> files = MultiFileChooserPane.selectFiles(new JFrame(),"Select libraries and class files folders");
        loadDataFromJars(files);      
	}
	
	private void actLoadDataFromJar() {				
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
		fc.setMultiSelectionEnabled(true);
		fc.setCurrentDirectory(MultiFileChooserPane.initFolder);
		fc.setDialogTitle("Select jar file or class folder");
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
           File[] selection = fc.getSelectedFiles();
           List<File> files = new ArrayList<File>();
           for (File f:selection) files.add(f);
           loadDataFromJars(files);   
	    }
	
           
	}
	
	private void actAnalyseMe() {
		List<File> files = new ArrayList<File>();
		File bin = new File("bin");
		if (bin.exists() && bin.isDirectory()) {
			files.add(bin);
		}
		// search root folder for jars
		File root = new File(".");
		if (root.exists() && root.isDirectory()) {
			for (File f:root.listFiles()) {
				if (f.getName().endsWith(".jar")) {
					files.add(f);
				}
			}
		}
		File lib = new File("lib");
		if (lib.exists() && lib.isDirectory()) {
			for (File f:lib.listFiles()) {
				if (f.getName().endsWith(".jar")) {
					files.add(f);
				}
			}
		}
		loadDataFromJars(files);  
		
	}
	
	private void loadQuery(File file) {
		resetViews();
        try {
        	InputStream in = new FileInputStream(file);
            Motif motif = new XMLMotifReader().read(in);
            this.query = motif;
            this.computationStarted = -1;
            this.status = Status.waiting;
            this.queryField.setText(file.getAbsolutePath());
            in.close();
            
            // read source
            log("Motif imported from " + file.getAbsolutePath());
            FileReader reader = new FileReader(file);
            BufferedReader sin = new BufferedReader(reader);
            String line = null;
            StringBuffer b = new StringBuffer();
            while ((line=sin.readLine())!=null) {
            	if (b.length()>0) b.append('\n');
            	b.append(line);
            }
            this.querySource = b.toString();
            sin.close();
            
            displayMotif(query,querySource);
        }
        catch (Exception x) {
        	handleException("Error loading query",x);
        }
	}
	private void loadDataFromODEM(final File file) {		
		Runnable r = new Runnable() {
			public void run() {
		        try {
		            Reader reader = new FileReader(file);
		            ODEMReader input = new ODEMReader(reader);
		            DirectedGraph<Vertex, Edge>	g =	input.readGraph();
		            reader.close();
		            data = g;
			        status = Status.waiting;
			        dataField.setText(file.getAbsolutePath());
			        log("Data imported from " + file.getAbsolutePath());
			        computationStarted = -1;
		        }
		        catch (Exception x) {
		        	handleException("Error loading data file",x);
		        }
		        finally {
		        	finishLoadingGraph();
		        }
			}
		};
		new Thread(r).start();
		startLoadingGraph();
	}
	private void loadDataFromGraphML(final File file) {		
		Runnable r = new Runnable() {
			public void run() {
		        try {
		            Reader reader = new FileReader(file);
		            GraphMLReader input = new GraphMLReader(reader);
		            DirectedGraph<Vertex, Edge> g =	input.readGraph();
		            reader.close();
		            data = g;
			        status = Status.waiting;
			        dataField.setText(file.getAbsolutePath());
			        log("Data imported from " + file.getAbsolutePath());
			        computationStarted = -1;
		        }
		        catch (Exception x) {
		        	handleException("Error loading data file",x);
		        }
		        finally {
		        	finishLoadingGraph();
		        }
			}
		};
		new Thread(r).start();
		startLoadingGraph();
	}
	
	private void loadDataFromJars(final List<File> files) {
		Runnable r = new Runnable() {
			public void run() {
		        try {
		            DirectedGraph<Vertex, Edge> g = null;
		            JarReader input = new JarReader(files);
		            ProgressListener l = new ProgressListener() {
						@Override
						public void progressMade(int progress, int total) {
							statusField.setMaximum(total);
							statusField.setValue(progress);
						}
		            };
		            input.addProgressListener(l);
		            g =	input.readGraph();
		            if (g!=null) {
			            data = g;
			            status = Status.waiting;
			            if (files.size()==1) {
			            	dataField.setText(files.get(0).getAbsolutePath());
			            }
			            else if (files.size()==0) {
			            	dataField.setText("no file selected");
			            }
			            else {
			            	dataField.setText("multiple input files");
			            }
			            log("Data imported from " + files);
			            computationStarted = -1;
		            }
		            else {
			            data = null;
			            status = Status.waiting;
			            dataField.setText("-");
			            computationStarted = -1;
		            	handleException("Cannot open file",null);
		            }
		        }
		        catch (Exception x) {
		        	handleException("Error loading data file",x);
		        }
		        finally {
		        	finishLoadingGraph();
		        }
			}
		};

		new Thread(r).start();
		startLoadingGraph();
	}
	
	private void startLoadingGraph() {
		this.status = Status.loading;
		//this.statusField.setIndeterminate(true);
		this.results.reset();
		this.computationStarted=-1;
		this.updateStatus();
		this.updateComputationTime();
		this.updateActions();
		this.resetViews();
	}

	private void resetViews() {
		for (ResultView view:this.resultViewers) {
			view.display(null,null);
		}
	}

	private void finishLoadingGraph() {
		this.status = Status.waiting;
		//this.statusField.setIndeterminate(false);
		
		this.updateStatus();
		this.updateActions();
	}

	private void handleException(String message, Exception x) {
		if (x==null) LOG.error(message);
		else LOG.error(message,x);
		JOptionPane.showMessageDialog(this, message, "Error",JOptionPane.ERROR_MESSAGE);
	}

	private void nyi() {
		JOptionPane.showMessageDialog(this,"this function is not yet implemented");
	}

	private void updateActions() {

		boolean querying = queryThread!=null;
		boolean loading = status==Status.loading;
		actCancelQuery.setEnabled(!loading&&querying);
		actRunQuery.setEnabled(!loading&&!querying&&this.query!=null&&this.data!=null);
		actExport2CSV.setEnabled(!loading&&!querying && results.hasResults());
		actNextMajorInstance.setEnabled(!loading&&results.hasNextMajorInstance());
		actNextMinorInstance.setEnabled(!loading&&results.hasNextMinorInstance());
		actPreviousMajorInstance.setEnabled(!loading&&results.hasPreviousMajorInstance());
		actPreviousMinorInstance.setEnabled(!loading&&results.hasPreviousMinorInstance());
		for (Action act:loadActions) {
			act.setEnabled(!loading&&!querying);
		}
		actViewGraphData.setEnabled(this.data!=null);

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
	
	private void displayInstance(final MotifInstance instance) {
		switchToResultView();
		for (ResultView view:this.resultViewers) {
			view.display(instance,this.data);
		}	
	}
	private void displayMotif(Motif query,String source) {
		switchToQueryView();
		for (QueryView view:this.queryViewers) {
			view.display(query,source);
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

	private boolean isShowingQuery() {
		return this.tabbedPane.getSelectedIndex()<this.queryViewers.length;
	}
	private void switchToQueryView() {
		if (!isShowingQuery()) {
			this.tabbedPane.setSelectedIndex(0);
		}
	}
	private void switchToResultView() {
		if (isShowingQuery()) {
			this.tabbedPane.setSelectedIndex(this.queryViewers.length);
		}
	}
}
