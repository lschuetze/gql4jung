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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;


/**
 * File dialog to select multiple files in different folders.
 * @author Jens Dietrich
 */

public class MultiFileChooserPane extends JDialog {
	private DefaultListModel files = new DefaultListModel();
	private JList list = new JList();
	private JToolBar toolbar = new JToolBar();
	private Action actCancel = null;
	private Action actOK= null;
	private Action actAdd = null;
	private Action actRemove= null;
	static File initFolder = new File("./exampledata");
	
	public MultiFileChooserPane(Frame owner, boolean modal) {
		super(owner, modal);
		initialize();
	}

	/**
	 * For testing only.
	 */
	public static void main(String[] args) {
		List<File> files = selectFiles(new JFrame(),"select files");
		for (File f:files) {
			System.out.println(f);
		}
	}
	public static List<File> selectFiles(JFrame parent,String title) {
		MultiFileChooserPane f = new MultiFileChooserPane(parent, true);
		f.setTitle(title);
		f.setSize(500,300);
		f.setLocation(100,100);
		f.setVisible(true);		
		// collect and return files
		java.util.List<File> files = new ArrayList<File>();
		for (int i=0;i<f.files.getSize();i++) {
			files.add((File)f.files.get(i));
		}
		return files;
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		JComponent main = new JScrollPane(list);
		this.add(main,BorderLayout.CENTER);
		main.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		this.add(toolbar,BorderLayout.SOUTH);
		toolbar.setAlignmentX(RIGHT_ALIGNMENT);
		toolbar.setFloatable(false);
		list.setModel(this.files);
		
		actOK = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}			
		};
		
		actCancel = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				files.clear();
				setVisible(false);
			}			
		};
		
		actAdd = new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
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
				fc.setCurrentDirectory(initFolder);
				fc.setDialogTitle("Select jar files or class folders");
				int returnVal = fc.showOpenDialog(MultiFileChooserPane.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		           File[] selection = fc.getSelectedFiles();
		           for (File file:selection) {
			           if (!files.contains(file)) {
			        	   files.addElement(file);
			           }
		           }
		           // set new init folder
		           if (selection.length>0) {
		        	   File f = selection[0];
		        	   if (f.isDirectory() && f.exists()) {
		        		   initFolder = f;
		        	   }
		        	   else {
		        		   f = f.getParentFile();
			        	   if (f.isDirectory() && f.exists()) {
			        		   initFolder = f;
			        	   }
		        	   }
		           }
			    }
			}			
		};
		
		actRemove = new AbstractAction("Remove") {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				Object file = files.get(list.getSelectedIndex());
		        if (file!=null) {
		           files.removeElement(file);
			    }
			}			
		};
		actRemove.setEnabled(false);
		
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				actRemove.setEnabled(list.getSelectedIndex()>-1);
			}
		});
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolbar.add(actAdd);
		toolbar.add(actRemove);
		toolbar.addSeparator();
		toolbar.add(actCancel);
		toolbar.add(actOK);
	}


}
