/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser.resultviews;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.ResultView;
import edu.uci.ics.jung.graph.DirectedGraph;
/**
 * Simple table view for results.
 * @author jens dietrich
 */
public class TableBasedResultView extends ResultView {
	
	private JTable table = null;
	
	

	public TableBasedResultView() {
		super();
		this.table = new JTable();
		this.setLayout(new GridLayout(1,1));
		this.add(new JScrollPane(table));
	}

	@Override
	public void display(final MotifInstance<TypeNode,TypeReference> instance,	DirectedGraph<TypeNode,TypeReference> graph) {
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
		    	TypeNode v = instance.getVertex(role);
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
