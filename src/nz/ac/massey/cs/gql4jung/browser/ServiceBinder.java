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


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
/**
 * Utility to bind services.
 * @author jens dietrich
 */
public class ServiceBinder {
	private static Properties settings = new Properties();
	private static final String PLUGIN_DEFS = "plugins.properties";
	private static final Logger LOG = Logger.getLogger(ServiceBinder.class); 
	private static final Map<Class<?>,List<?>> plugins = new HashMap<Class<?>,List<?>>();
	static {
		try {
			FileReader reader = new FileReader(PLUGIN_DEFS);
			settings.load(reader);
		} catch (IOException x) {
			Logger.getLogger(ServiceBinder.class).error("cannot read plugin registry "+PLUGIN_DEFS, x);
		}
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T> getServices(Class<T> type) {
		List<T> p = (List<T>) plugins.get(type);
		if (p==null) {
			p = new ArrayList<T>();
			String defs = settings.getProperty(type.getName());
			if (defs==null) {
				LOG.warn("No plugin definitions found for type " + type.getName());
			}
			else {
				for (StringTokenizer tok = new StringTokenizer(defs,",");tok.hasMoreTokens();) {
					String def = tok.nextToken().trim();
					Object service = null;
					try {
						Class clazz = Class.forName(def);
						service = clazz.newInstance();
					} catch (ClassNotFoundException e) {
						LOG.warn("Cannot find plugin class " + def + " for service " + type);
					} catch (InstantiationException e) {
						LOG.warn("Cannot instantiate plugin class " + def + " for service " + type);
					} catch (IllegalAccessException e) {
						LOG.warn("Cannot instantiate plugin class " + def + " for service " + type);
					}
					if (service!=null) {
						if (type.isAssignableFrom(service.getClass())) {
							p.add((T)service);
						}
						else {
							LOG.warn("Plugin class " + def + " does not implement service " + type);
						}
					}
					
				}
				
			}
			plugins.put(type,p);
		}
		return p;
	}
}
