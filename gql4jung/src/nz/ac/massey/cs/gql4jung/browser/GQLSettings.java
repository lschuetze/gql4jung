/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser;

import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;

/**
 * Properties of the GQL.
 * @author jens dietrich
 */
public class GQLSettings implements PropertyBean {
	
	private static String[] gqlFactories = {
		DefaultGQLFactory.class.getName(),
		MultiThreadedGQLFactory2.class.getName(),
		MultiThreadedGQLFactory4.class.getName()
	};
	
	
	public GQLSettings() {
		super();
		reset();
	}
	
	private String gqlFactoryName = null;

	
	public String getGQLFactoryName() {
		return gqlFactoryName;
	}

	public void setGQLFactoryName(String gqlFactoryName) {
		this.gqlFactoryName = gqlFactoryName;
	}


	
	public final static GQLSettings DEFAULT_INSTANCE = load();
	public static File getStorage()  {
		return new File(".GQLSettings.xml"); 
	}


	
	public static class GQLFactoryEditor extends ComboBoxPropertyEditor {
		public GQLFactoryEditor() {
			super();	    
		    setAvailableValues(gqlFactories);
		}
	}
	
	
	public GQLFactory getGQLFactory() {
		try {
			Class clazz = Class.forName(this.gqlFactoryName);
			return (GQLFactory)clazz.newInstance();
		}
		catch (Exception x){
			Logger.getLogger(this.getClass()).error("Cannot instantiate GQL factory "+this.gqlFactoryName,x);
			return new DefaultGQLFactory();
		}
	}

	public static GQLSettings load() {
		try {
			GQLSettings settings = null;
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(getStorage())));
			settings = (GQLSettings)decoder.readObject();
			decoder.close();
			Logger.getLogger(GQLSettings.class).info("Loading settings from " + getStorage());
			return settings;
		}
		catch (Exception x) {
			Logger.getLogger(GQLSettings.class).info("Cannot load settings");
		}
		return new GQLSettings();
	}



	@Override
	public void save() throws IOException {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getStorage())));
		encoder.writeObject(this);
		encoder.close();
	}

	@Override
	public void reset() {
		this.gqlFactoryName = this.gqlFactories[0];
	}
	@Override
	public PropertyDescriptor[] getProperties() {
		try {
			PropertyDescriptor[] properties = {
				new PropertyDescriptor("gqlFactoryName",GQLSettings.class,"getGQLFactoryName","setGQLFactoryName")
			};	
			properties[0].setPropertyEditorClass(GQLFactoryEditor.class);			
			return properties;
		}
		catch (Exception x) {
			Logger.getLogger(this.getClass()).error("Exception initializing settings",x);
			return new PropertyDescriptor[0];
		}
	}

}
