/**
 * Copyright 2009 Max Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Max Dietrich
 * A graph layout that places the given vertices around the edge of an ellipse
 */
	public class EllipticFanLayout<V extends Vertex, E extends Edge> implements
			Layout<Vertex, Edge> {
		
		public EllipticFanLayout(Graph<Vertex, Edge> graph) {
			super();
			this.graph = graph;
		}
	
		protected Graph<Vertex, Edge> graph = null;
		protected Dimension size = null;
		//A map of all the points and their respective positions
		protected Map<Vertex, EllipticPolarPoint> points = null;
		//Checks if the vertex map needs updating 
		private boolean isInitialized = false;
		
		//The default dimension for each box
		public static Dimension boxSize = new Dimension(120, 36);
		
		//Vertices that have been manually moved
		private Map<Vertex, Point2D> movedPoints = new HashMap<Vertex, Point2D>();
	

	
		//Returns a set of points for a given graph
		protected Map<Vertex, EllipticPolarPoint> getPoints(Graph<Vertex, Edge> graph) {
	
			Map<Vertex, EllipticPolarPoint> map = new HashMap<Vertex, EllipticPolarPoint>();
			int centerX = this.size.width / 2 - boxSize.width/2;
			int centerY = this.size.height / 2 - boxSize.height/2;
			int Xradius = centerX - boxSize.width;
			int Yradius = centerY - boxSize.height;
			double angle = 2 * Math.PI / (graph.getVertexCount());
	
	
			Vertex initialVertex = graph.getVertices().iterator().next();
			//A list of the current points
			List<Vertex> currentPoints = new ArrayList<Vertex>();
	
			currentPoints.add(initialVertex);
	
			//Count for determining angle
			int count = 0;
			
			//Adds each current point to the map, then adds all connected points
			//Within the graph to current points. Already mapped points are eliminated.
			while (!currentPoints.isEmpty()) {
				Vertex v = currentPoints.get(0);
	
				if (!map.containsKey(v)) {
					count++;
					double theta = angle * count;
					EllipticPolarPoint p = new EllipticPolarPoint(theta, Xradius, Yradius);
					map.put(v, p);
					for (Edge e : v.getOutEdges()) {
						if (graph.containsEdge(e)) {
							currentPoints.add(0, e.getEnd());
						}
					}
					for (Edge e : v.getInEdges()) {
						if (graph.containsEdge(e)) {
							currentPoints.add(0, e.getStart());
						}
					}
				} else
					currentPoints.remove(0);
	
			}
	
			return map;
		}
	
		@Override
		public Graph<Vertex, Edge> getGraph() {
			return graph;
		}
	
		@Override
		public Dimension getSize() {
			return size;
		}
	
		//Does nothing
		@Override
		public void initialize() {
			reset();
		}
	
		//Resets the current graph
		@Override
		public void reset() {
			if (this.movedPoints.isEmpty()) {
				this.points = null;
				this.isInitialized = false;
			}
		}
	
		@Override
		public void setGraph(Graph<Vertex, Edge> g) {
			this.graph = g;
			this.movedPoints.clear();
		}
	
		@Override
		public void setSize(Dimension d) {
			//System.out.println("Setting size...");
			this.size = d;
			this.movedPoints.clear();
			reset();
		}
	
		//Returns the value of the vertex in the vertex map
		//If moved, returns moved point instead
		//Recalculates points if necessary
		@Override
		public Point2D transform(Vertex v) {
	
			if (!this.isInitialized) {
				this.points = this.getPoints(this.graph);
			}
	
			if(this.movedPoints.containsKey(v)){
				return this.movedPoints.get(v);
			}
			int centerX = this.size.width / 2 - boxSize.width/2;
			int centerY = this.size.height / 2 - boxSize.height/2;
			Point p = this.points.get(v).toCartesian();
			p.x += centerX;
			p.y += centerY;
			return p;
		}
	
		//Does nothing
		@Override
		public void setInitializer(Transformer<Vertex, Point2D> t) {
		}
	
		//Does nothing
		@Override
		public boolean isLocked(Vertex arg0) {
			return false;
		}
	
		//Does nothing
		@Override
		public void lock(Vertex arg0, boolean arg1) {
		}
	
		//Overrides the position of a point by adding it to movedPoints
		@Override
		public void setLocation(Vertex v, Point2D p) {
			this.movedPoints.put(v, p);
		}
	
	}