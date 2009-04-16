package nz.ac.massey.cs.gql4jung.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.lang.time.DurationFormatUtils;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.impl.GQLImpl;
import nz.ac.massey.cs.gpl4jung.impl.MotifInstance2Graphml;
import nz.ac.massey.cs.gpl4jung.xml.XMLMotifReader;
import nz.ac.massey.cs.gql4jung.browser.QueryResults.Cursor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
/**
 * Stand alone user interface to run queries and visualise results.
 * The graph visualisation is based on JUNG. 
 * @author Jens Dietrich
 */
public class ResultBrowser extends JFrame {
	// model
	private Graph data = null;
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
	private JLabel statusField = null;
	private JPanel mainPanel = null;
	private JPopupMenu popup;
	private MouseAdapter popupListener;
	protected GraphZoomScrollPane graphPaneContainer;
	private VisualizationViewer visualizationViewer = null;
	private JTable table = null;
	private JTabbedPane tabbedPane = null;
	private JPanel graphPane = null;
	
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
				updateStatus();
				updateComputationTime();
			}
		};
		this.results.addListener(listener);
		
		// graph
		visualizationViewer = new VisualizationViewer(new FRLayout(new SparseGraph()),new PluggableRenderer());
		initActions();
		initPopupMenu();
		initToolbar();
		initStatusBar();

		
		// load sample data
		// TODO remove
		this.loadData(new File("exampledata/ant.jar.graphml"));
		this.loadQuery(new File("exampledata/query1.xml"));
		
		updateActions();
		updateStatus();
	}
	private void initStatusBar() {
		this.cursorField = new JLabel();
		this.queryField = new JLabel();
		this.dataField = new JLabel();
		this.statusField = new JLabel();
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
		/*
		actExit = new AbstractAction("exit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		toolbar.add(actExit);
		*/
		
		actLoadData = new AbstractAction("load data",getIcon("Open16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadData();
			}
		};
		actLoadQuery = new AbstractAction("load query",getIcon("Import16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actLoadQuery();
			}
		};
		actRunQuery = new AbstractAction("run query",getIcon("Play16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actRunQuery();
			}
		};
		actCancelQuery = new AbstractAction("cancel",getIcon("Stop16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actCancelQuery();
			}
		};
		actExport2CSV = new AbstractAction("export to csv",getIcon("Export16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actExport2CSV();
			}
		};
		actPreviousMinorInstance = new AbstractAction("previous minor instance",getIcon("PreviousMinor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actPreviousMinorInstance();
			}
		};		
		actNextMinorInstance = new AbstractAction("next minor instance",getIcon("NextMinor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actNextMinorInstance();
			}
		};
		actPreviousMajorInstance = new AbstractAction("previous major instance",getIcon("PreviousMajor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actPreviousMajorInstance();
			}
		};		
		actNextMajorInstance = new AbstractAction("next major instance",getIcon("NextMajor16.gif")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				actNextMajorInstance();
			}
		};		
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
		GraphMLFile input = new GraphMLFile();
        try {
            Reader reader = new FileReader(file);
            Graph g = new DirectedSparseGraph();
            g =	input.load(reader);
            reader.close();
            this.data = g;
            this.status = Status.waiting;
            this.dataField.setText(file.getAbsolutePath());
            log("Data imported from " + file.getAbsolutePath());
            this.computationStarted = -1;
        }
        catch (IOException x) {
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
		statusField.setText(this.status.toString());
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
		        	default: return null;
		        }
		    }
		    public int getRowCount() { 
		    	return roles.size(); 
		    }
		    public int getColumnCount() { 
		    	return 4; 
		    }
		    public Object getValueAt(int row, int col) {
		    	String role = roles.get(row);
		    	if (instance==null) return "";
		    	Vertex v = instance.getVertex(role);
		        switch (col) {
	        		case 0: return role;
	        		case 1: return v.getUserDatum("name");
	        		case 2: return v.getUserDatum("namespace");
	        		case 3: return v.getUserDatum("container");
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
		MotifInstance2Graphml converter = new MotifInstance2Graphml();
		Graph g = instance==null?new SparseGraph():converter.asGraph(instance);
		visualizationViewer.removeMouseListener(popupListener);
		if (graphPaneContainer!=null) {
			graphPaneContainer.remove(visualizationViewer);
			graphPane.remove(graphPaneContainer);
		}
		visualizationViewer = new VisualizationViewer(new FRLayout(g),GraphRenderer.getRenderer());
		visualizationViewer.setBackground(Color.WHITE);
		graphPaneContainer = new GraphZoomScrollPane(visualizationViewer);
		addBorder(graphPaneContainer);
		graphPane.add(graphPaneContainer,BorderLayout.CENTER);
		graphPane.revalidate();
		visualizationViewer.addMouseListener(popupListener);
	}


	private Graph asGraph(MotifInstance instance) {
		Graph g = new SparseGraph();
		Motif motif = instance.getMotif();
		Map<String,Vertex> nodes = new HashMap<String,Vertex>();
		for (String role:motif.getRoles()) {
			Vertex v = instance.getVertex(role);
			Vertex vertex = (Vertex) g.addVertex(new DirectedSparseVertex());
			vertex.addUserDatum("role",role,UserData.SHARED);
			for (Iterator<String> iter = v.getUserDatumKeyIterator();iter.hasNext();) {
				String key = iter.next();
				vertex.addUserDatum(key,v.getUserDatum(key),UserData.SHARED);
			}
			nodes.put(role,vertex);
		};
		for (Object obj:motif.getConstraints()) {
			if (obj instanceof LinkConstraint) {
				LinkConstraint lc = (LinkConstraint)obj;
				Vertex target = nodes.get(lc.getTarget());
				Vertex source = nodes.get(lc.getSource());
				DirectedSparseEdge edge = (DirectedSparseEdge) g.addEdge(new DirectedSparseEdge(source,target));

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
