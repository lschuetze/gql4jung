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
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Max Dietrich
 * A graph layout that places the given vertices around the edge of a circle
 */
public class FanLayout<V , E> implements Layout<V, E> {

	private Graph<V, E> graph = null;
	private Dimension size = null;
	//A map of all the points and their respective positions
	private Map<V, Point2D> points = null;
	//Checks if the vertex map needs updating 
	private boolean isInitialized = false;
	
	//Vertices that have been manually moved
	private Map<V, Point2D> movedPoints = new HashMap<V, Point2D>();

	public FanLayout(Graph<V, E> graph) {
		super();
		this.graph = graph;
	}

	//Returns a set of points for a given graph
	private Map<V, Point2D> getPoints(Graph<V, E> graph) {

		Map<V, Point2D> map = new HashMap<V, Point2D>();

		//The default size of each vertex box
		Dimension boxSize = new Dimension(120, 36);

		int centerX = this.size.width / 2 - boxSize.width/2;
		int centerY = this.size.height / 2 - boxSize.height/2;
		int radius = centerY - boxSize.height;
		double angle = 2 * Math.PI / (graph.getVertexCount());


		V initialVertex = graph.getVertices().iterator().next();
		//A list of the current points
		List<V> currentPoints = new ArrayList<V>();

		currentPoints.add(initialVertex);

		//Count for determining angle
		int count = 0;
		
		//Adds each current point to the map, then adds all connected points
		//Within the graph to current points. Already mapped points are eliminated.
		while (!currentPoints.isEmpty()) {
			V v = currentPoints.get(0);

			if (!map.containsKey(v)) {
				count++;
				double theta = angle * count;
				int x = centerX + (int) (Math.sin(theta) * radius);
				int y = centerY + (int) (Math.cos(theta) * radius);
				Point p = new Point(x, y);
				map.put(v, p);
				for (E e : this.graph.getOutEdges(v)) {
					if (graph.containsEdge(e)) {
						currentPoints.add(0, this.graph.getDest(e));
					}
				}
				for (E e : this.graph.getInEdges(v)) {
					if (graph.containsEdge(e)) {
						currentPoints.add(0, this.graph.getSource(e));
					}
				}
			} else
				currentPoints.remove(0);

		}

		return map;
	}

	@Override
	public Graph<V, E> getGraph() {
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
	public void setGraph(Graph<V, E> g) {
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
	public Point2D transform(V v) {

		if (!this.isInitialized) {
			this.points = this.getPoints(this.graph);
		}

		if(this.movedPoints.containsKey(v)){
			return this.movedPoints.get(v);
		}
		else return this.points.get(v);
	}

	//Does nothing
	@Override
	public void setInitializer(Transformer<V, Point2D> t) {
	}

	//Does nothing
	@Override
	public boolean isLocked(V arg0) {
		return false;
	}

	//Does nothing
	@Override
	public void lock(V arg0, boolean arg1) {
	}

	//Overrides the position of a point by adding it to movedPoints
	@Override
	public void setLocation(V v, Point2D p) {
		this.movedPoints.put(v, p);
	}

}
