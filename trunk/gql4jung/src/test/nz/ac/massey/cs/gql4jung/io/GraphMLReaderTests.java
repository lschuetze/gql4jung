/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.gql4jung.io;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import org.junit.Test;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the graphml reader.
 * @author jens dietrich
 */

public class GraphMLReaderTests {

	private DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/gql4jung/io/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        GraphMLReader greader = new GraphMLReader(reader);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}
	
	private void doTestVertices(String graph,int expectedNumberOfInstances,final String id,final String name,final String namespace,final boolean isAbstract,final String type) throws Exception {
		DirectedGraph<Vertex, Edge> g = loadGraph("graph1.graphml");
		Iterator<Vertex> iter = g.getVertices().iterator();
		iter = Iterators.filter(iter,new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				return id==null || id.equals(v.getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				return name==null || name.equals(v.getName());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				return namespace==null || namespace.equals(v.getNamespace());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				return isAbstract==v.isAbstract();
			}
		});
		iter = Iterators.filter(iter,new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
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
	
	private void doTestEdges(String graph,int expectedNumberOfInstances,final String id,final String sourceId,final String targetId,final String type) throws Exception {
		DirectedGraph<Vertex, Edge> g = loadGraph("graph1.graphml");
		Iterator<Edge> iter = g.getEdges().iterator();
		iter = Iterators.filter(iter,new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				return id==null || id.equals(e.getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				return sourceId==null || sourceId.equals(e.getStart().getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				return targetId==null || targetId.equals(e.getEnd().getId());
			}
		});
		iter = Iterators.filter(iter,new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
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
	
	@Test
	public void testVertices1() throws Exception {
		DirectedGraph<Vertex, Edge> g = loadGraph("graph1.graphml");
		doTestVertices(
				"graph1.graphml",
				1,
				"1",
				"v1",
				"test1",
				true,
				"interface"
				);
	}
	@Test
	public void testVertices2() throws Exception {
		doTestVertices(
				"graph1.graphml",
				0,
				"1",
				"v1",
				"test1",
				false,
				"interface"
				);
	}
	@Test
	public void testVertices3() throws Exception {
		doTestVertices(
				"graph1.graphml",
				4,
				null,
				null,
				"test1",
				false,
				null
				);
	}
	@Test
	public void testVertices4() throws Exception {
		doTestVertices(
				"graph1.graphml",
				4,
				null,
				null,
				"test1",
				true,
				null
				);
	}
	
	@Test
	public void testEdges1() throws Exception {
		doTestEdges(
				"graph1.graphml",
				1,
				"edge-1-2",
				"1",
				"2",
				"uses"
				);
	}
	@Test
	public void testEdges2() throws Exception {
		doTestEdges(
				"graph1.graphml",
				0,
				"edge-1-2",
				"1",
				"2",
				"extends"
				);
	}
	@Test
	public void testEdges3() throws Exception {
		doTestEdges(
				"graph1.graphml",
				2,
				null,
				null,
				null,
				"extends"
				);
	}
	@Test
	public void testEdges4() throws Exception {
		doTestEdges(
				"graph1.graphml",
				7,
				null,
				null,
				null,
				null
				);
	}
	@Test
	public void testEdges5() throws Exception {
		doTestEdges(
				"graph1.graphml",
				2,
				null,
				"1",
				null,
				null
				);
	}
	
}
