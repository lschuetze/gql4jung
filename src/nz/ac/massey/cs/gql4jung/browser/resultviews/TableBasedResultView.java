package nz.ac.massey.cs.gql4jung.browser.resultviews;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.ResultView;
import edu.uci.ics.jung.graph.DirectedGraph;

public class TableBasedResultView extends ResultView {
	
	private JTable table = null;
	
	

	public TableBasedResultView() {
		super();
		this.table = new JTable();
		this.setLayout(new GridLayout(1,1));
		this.add(new JScrollPane(table));
	}

	@Override
	public void display(final MotifInstance instance,	DirectedGraph<Vertex, Edge> graph) {
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

	@Override
	public String getName() {
		return "query results as table";
	}
	public PropertyBean getSettings() {
		return null;
	}
}
