/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.codeanalysis.io;

import static org.junit.Assert.*;
import java.util.Iterator;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Abstract superclass for reader tests.
 * @author jens dietrich
 */

public abstract class AbstractReaderTests {

	protected abstract DirectedGraph<TypeNode, TypeReference> loadGraph(String name) throws Exception ;
	
	protected void doTestVertices(String graph,int expectedNumberOfInstances,final String id,final String name,final String namespace,final boolean isAbstract,final String type) throws Exception {
		DirectedGraph<TypeNode, TypeReference> g = loadGraph(graph);
		Iterator<TypeNode> iter = g.getVertices().iterator();
		iter = Iterators.filter(iter,new Predicate<TypeNode>() {
			@Override
			public boolean apply(TypeNode v) {
				return id==null || id.equals(v.getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeNode>() {
			@Override
			public boolean apply(TypeNode v) {
				return name==null || name.equals(v.getName());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeNode>() {
			@Override
			public boolean apply(TypeNode v) {
				return namespace==null || namespace.equals(v.getNamespace());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeNode>() {
			@Override
			public boolean apply(TypeNode v) {
				return isAbstract==v.isAbstract();
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeNode>() {
			@Override
			public boolean apply(TypeNode v) {
				return type==null || type.equals(v.getType());
			}
		});
		// count 
		int count = 0;
		while (iter.hasNext()) {
			count = count+1;
			iter.next();
		}
		assertEquals(expectedNumberOfInstances,count);
	}
	
	protected void doTestTypeReferences(String graph,int expectedNumberOfInstances,final String id,final String sourceId,final String targetId,final String type) throws Exception {
		DirectedGraph<TypeNode, TypeReference> g = loadGraph(graph);
		Iterator<TypeReference> iter = g.getEdges().iterator();
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return id==null || id.equals(e.getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return sourceId==null || sourceId.equals(e.getStart().getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return targetId==null || targetId.equals(e.getEnd().getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return type==null || type.equals(e.getType());
			}
		});
		// count 
		int count = 0;
		while (iter.hasNext()) {
			count = count+1;
			iter.next();
		}
		assertEquals(expectedNumberOfInstances,count);
	}
	
	protected void doTestTypeReferences2(String graph,int expectedNumberOfInstances,final String id,final String sourceId,final String targetId,final String type) throws Exception {
		DirectedGraph<TypeNode, TypeReference> g = loadGraph(graph);
		Iterator<TypeReference> iter = g.getEdges().iterator();
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return id==null || id.equals(e.getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return sourceId==null || sourceId.equals(e.getStart().getFullname());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return targetId==null || targetId.equals(e.getEnd().getFullname());
			}
		});
		iter = Iterators.filter(iter,new Predicate<TypeReference>() {
			@Override
			public boolean apply(TypeReference e) {
				return type==null || type.equals(e.getType());
			}
		});
		// count 
		int count = 0;
		while (iter.hasNext()) {
			count = count+1;
			iter.next();
		}
		assertEquals(expectedNumberOfInstances,count);
	}
	
	
	
	
}
