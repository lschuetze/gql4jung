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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.time.DurationFormatUtils;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.io.QueryResultsExporter2CSV;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResults.Cursor;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/**
 * Stand alone user interface to run queries and visualise results.
 * The graph visualisation is based on JUNG. 
 * @author Jens Dietrich
 */
public class ResultBrowser extends JFrame {
	// model
	private DirectedGraph<Vertex,Edge> data = null;
	private Motif query = null;
	private QueryResults results = new QueryResults();
	private GQL engine = new GQLImpl();
	private Thread queryThread = null;
	private long computationStarted = -1;
	
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
	private JTable table = null;
	private JTabbedPane tabbedPane = null;
	private JPanel graphPane = null;
	private JMenuBar menuBar = null;
	
	// actions
	private AbstractAction actExit;
	private AbstractAction actLoadData;
	private AbstractAction actLoadQuery;
	private AbstractAction actRunQuery;
	private AbstractAction actCancelQuery;
	private AbstractAction actNextMinorInstance;
	private AbstractAction actPreviousMinorInstance;
	private AbstractAction actNextMajorInstance;
	private AbstractAction actPreviousMajorInstance;
	private AbstractAction actExport2CSV;
	private List<AbstractAction> actLoadBuiltInQueries = new ArrayList<AbstractAction>();
	
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
		this.setTitle("Architectural smells explorer");
				
		mainPanel = new JPanel(new BorderLayout(5,5));
		this.tabbedPane = new JTabbedPane();
		mainPanel.add(tabbedPane,BorderLayout.CENTER);
		// panel for graph
		graphPane = new JPanel(new GridLayout(1,1));
		this.tabbedPane.add("result as graph",graphPane);
		// panel for table
		this.table = new JTable();
		JScrollPane sTable = new JScrollPane(table);
		addBorder(sTable);
		this.tabbedPane.add("result as table",sTable);
		
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
		this.loadData(new File("exampledata/ant.jar.graphml"));
		this.loadQuery(new File("queries/awd.xml"));
		
		updateActions();
		updateStatus();
	}
	private void initMenubar() {
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.add(actLoadData);
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
		
		actLoadData = new AbstractAction("load data",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadData();
			}
		};
		actLoadData.putValue(Action.SHORT_DESCRIPTION, "load a program dependency graph from a graphml file");

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
		toolbar.add(actLoadData);
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
		popup.add(this.actLoadData);
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
	
	private Icon getIcon(String string) {
		return new ImageIcon("icons/"+string);
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
				engine.query(data,query,results);
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
		fc.setCurrentDirectory(new File("."));
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
	private void actLoadData() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("Load graph");
		int returnVal = fc.showOpenDialog(this);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".graphml");
			}
			@Override
			public String getDescription() {
				return "graphml files";
			}			
		};
		fc.setFileFilter(filter);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadData(file);
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
	private void loadData(File file) {
		
        try {
            Reader reader = new FileReader(file);
            GraphMLReader input = new GraphMLReader(reader);
            DirectedGraph<Vertex, Edge> g =	input.readGraph();
            reader.close();
            this.data = g;
            this.status = Status.waiting;
            this.dataField.setText(file.getAbsolutePath());
            log("Data imported from " + file.getAbsolutePath());
            this.computationStarted = -1;
        }
        catch (Exception x) {
        	handleException("Eddor loading data file",x);
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
		/*
		Runnable runner = new Runnable() {
			public void run() {
				displayTable(instance);
				displayGraph(instance);				
			}
		};
		new Thread(runner).start();*/
		if (this.tabbedPane.getSelectedIndex()==0) {
			displayGraph(instance);	
			displayTable(instance);
		}
		else {
			displayTable(instance);
			displayGraph(instance);
		}
		
	}
	private void displayTable(final MotifInstance instance) {
		final List<String> roles = (instance==null)?new ArrayList<String>():instance.getMotif().getRoles();
		TableModel model = new AbstractTableModel() {
		    public String getColumnName(int col) {
		        switch (col) {
		        	case 0: return "role";
		        	case 1: return "name";
		        	case 2: return "namespace";
		        	case 3: return "container";
		        	case 4: return "is abstract";
		        	case 5: return "type";
		        	case 6: return "cluster";
		        	default: return null;
		        }
		    }
		    public int getRowCount() { 
		    	return roles.size(); 
		    }
		    public int getColumnCount() { 
		    	return 7; 
		    }
		    public Object getValueAt(int row, int col) {
		    	String role = roles.get(row);
		    	if (instance==null) return "";
		    	Vertex v = instance.getVertex(role);
		    	Object cluster = "TODO";
		    	if (cluster==null) cluster="n/a";
		        switch (col) {
	        		case 0: return role;
	        		case 1: return v.getName();
	        		case 2: return v.getNamespace();
	        		case 3: return v.getContainer();
	        		case 4: return v.isAbstract();
	        		case 5: return v.getType();
	        		case 6: return v.getCluster();
	        		default: return null;
		        }
		    }
		    public boolean isCellEditable(int row, int col){ 
		    	return false; 
		    }
		    public void setValueAt(Object value, int row, int col) {
		        // nothing to do - table is read only
		    }
		};
		this.table.setModel(model);

	}
	
	private void displayGraph(final MotifInstance instance) {	
		DirectedGraph<Vertex,Edge> g = instance==null?new DirectedSparseGraph<Vertex,Edge>():this.asGraph(instance);
		//SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Vertex,Edge> layout = new FRLayout<Vertex,Edge>(g);
		layout.setSize(graphPane.getSize());
		VisualizationViewer<Vertex,Edge> vv = new VisualizationViewer<Vertex,Edge>(layout);
		configureRenderer(vv.getRenderContext(),instance);
		vv.setPreferredSize(graphPane.getSize()); //Sets the viewing area size
		graphPane.removeAll();
		graphPane.add(vv);
		graphPane.revalidate();
		//vv.addMouseListener(popupListener);
		// Create a graph mouse and add it to the visualization component
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
	}
	
	private void configureRenderer (RenderContext context,final MotifInstance instance) {
		final Map<Vertex,String> revMap = new HashMap<Vertex,String>();
		if (instance!=null) {
			for (String role:instance.getMotif().getRoles()) {
				revMap.put(instance.getVertex(role),role);
			}
		}
		context.setVertexLabelTransformer(
			new Transformer<Vertex,String>(){
				@Override
				public String transform(Vertex v) {
					String role = revMap.get(v);
					StringBuffer b = new StringBuffer()
						.append("<html>");
					if (role!=null) {
						b.append("&lt;&lt;")
						.append(role==null?"?":role)
						.append("&gt;&gt")
						.append("<br/>");					
					}
					b.append(v.getNamespace())
						.append('.')
						.append(v.getName())
						.append("</html>");
					return b.toString();
				}
			}
		);
		context.setEdgeLabelTransformer(
			new Transformer<Edge,String>(){
				@Override
				public String transform(Edge e) {
					return "<<"+e.getType()+">>";
				}
			}
		);
		context.setVertexFillPaintTransformer(
			new Transformer<Vertex,Paint>() {
				@Override
				public Paint transform(Vertex v) {
					String t = v.getType();
					if ("class".equals(t) && !v.isAbstract()) return Color.GREEN;
					else if ("class".equals(t) && v.isAbstract()) return Color.BLUE;
					else if ("interface".equals(t)) return new Color(255,0,255); // purble
					else return Color.WHITE;
				}
			}
		);
		final Stroke strokeUses = new BasicStroke(1);
		final Stroke strokeInherits = new BasicStroke(2);		
		context.setEdgeStrokeTransformer(
			new Transformer<Edge, Stroke>() {
				public Stroke transform(Edge e) {
					if ("uses".equals(e.getType())) return strokeUses;
					else return strokeInherits;
				}
			}
		);	
		context.setVertexIconTransformer(
			new Transformer<Vertex,Icon>() {
				@Override
				public Icon transform(Vertex v) {
					boolean hasRole = revMap.containsKey(v);
					if (v.isAbstract()) {
						return hasRole?GraphRenderer.ICON_INTERFACE_C:GraphRenderer.ICON_INTERFACE_BW;
					}
					else {
						return hasRole?GraphRenderer.ICON_CLASS_C:GraphRenderer.ICON_CLASS_BW;
					}
				}
			}
		);
		context.setVertexFontTransformer(
			new Transformer<Vertex,Font>(){
				@Override
				public Font transform(Vertex v) {
					boolean hasRole = revMap.containsKey(v);
					return hasRole?GraphRenderer.CORE:GraphRenderer.NON_CORE;
				}
			}
		);
		context.setEdgeFontTransformer(
			new Transformer<Edge,Font>(){
				@Override
				public Font transform(Edge e) {
					return GraphRenderer.CORE;
				}
			}
		);
		
	}
	


	private DirectedGraph asGraph(MotifInstance instance) {
		DirectedGraph<Vertex,Edge> g = new DirectedSparseGraph<Vertex,Edge>();
		Motif motif = instance.getMotif();
		// vertices
		Set<Vertex> vertices = new HashSet<Vertex>();
		for (String role:motif.getRoles()) {
			Vertex v = instance.getVertex(role);
			if (v==null) {
				g.addVertex(v);
				vertices.add(v);
			}
		};
		// edges
		for (String role:motif.getPathRoles()) {
			Path p = instance.getPath(role);
			if (p!=null) {
				for (Edge e:p.getEdges()) {
					Vertex v1 = e.getStart();
					Vertex v2 = e.getEnd();
					if (!vertices.contains(v1)) vertices.add(v1);
					if (!vertices.contains(v2)) vertices.add(v2);
					g.addEdge(e,v1,v2);
				}
			}
		}
		return g;
	}

	@Override
	public void dispose() {
		try {
			this.actCancelQuery();
		}
		catch (Exception x){}
		super.dispose();
	}
	private void addBorder(JComponent c) {
		c.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
	}
}
