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

/**
 * Dynamic editor for settings.
 * @author Jens Dietrich
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class PropertyBeanEditor extends JDialog {
	private PropertySheetPanel sheet = new PropertySheetPanel();
	private JToolBar toolbar = new JToolBar();
	private Action actOK;
	private Action actReset;
	private PropertyBean model = null;
	public PropertyBeanEditor(Frame owner, String title) {
		super(owner, title,true);
		initialize();
	}

	public static void edit(JFrame parent,PropertyBean bean,String title) {
		PropertyBeanEditor f = new PropertyBeanEditor(parent,title);
		f.model = bean;
		f.sheet.setProperties(bean.getProperties());
		f.sheet.readFromObject(bean);
		f.setTitle(title);
		f.setSize(600,300);
		f.setLocation(100,100);
		f.setVisible(true);		
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		JComponent main = new JScrollPane(sheet);
		this.add(main,BorderLayout.CENTER);
		main.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		this.add(toolbar,BorderLayout.SOUTH);
		toolbar.setFloatable(false);
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		actOK = new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				setVisible(false);
			}			
		};
		actReset = new AbstractAction("Reset to default values") {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.reset();
				save();
				sheet.readFromObject(model);
			}			
		};

		toolbar.add(actReset);
		toolbar.add(actOK);
		
	    PropertyChangeListener listener = new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent evt) {
	          Property prop = (Property)evt.getSource();
	          prop.writeToObject(model);
	        }
	      };
	      sheet.addPropertySheetChangeListener(listener);
	}

	protected void save() {
		try {
			model.save();
		} catch (IOException x) {
			Logger.getLogger(this.getClass()).error("Error saving properties "+model,x);
			JOptionPane.showMessageDialog(this, "Error saving properties "+model, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	
}
