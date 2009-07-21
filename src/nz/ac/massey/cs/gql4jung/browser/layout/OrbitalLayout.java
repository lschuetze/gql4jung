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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.RankedVertex;
import nz.ac.massey.cs.gql4jung.browser.resultviews.VisualEdge;
import nz.ac.massey.cs.gql4jung.browser.resultviews.VisualVertex;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Max Dietrich An extension of the elliptic fan layout that uses
 *         varying 'layers' of nodes These nodes are arranged for minimal
 *         intershell connection lengths
 */
public class OrbitalLayout<V extends RankedVertex, E>
		extends EllipticFanLayout<RankedVertex, E> {

	public OrbitalLayout(Graph graph) {
		super(graph);
	}

	protected Map<RankedVertex, EllipticPolarPoint> getPoints(
			Graph<RankedVertex, E> graph) {

		List<ArrayList<RankedVertex>> layers = this.extractLayers(graph);

		List<HashMap<RankedVertex, EllipticPolarPoint>> assignedLayers = new ArrayList<HashMap<RankedVertex, EllipticPolarPoint>>();

		int centerX = this.size.width / 2 - boxSize.width / 2;
		int centerY = this.size.height / 2 - boxSize.height / 2;
		int XR = (centerX - boxSize.width) / (layers.size());
		int YR = (centerY - boxSize.height) / (layers.size());

		// Initial shell
		{
			List<RankedVertex> initialShell = layers.get(0);
			RankedVertex initialPoint = initialShell.get(0);
			List<RankedVertex> currentPoints = new ArrayList<RankedVertex>();
			currentPoints.add(initialPoint);

			double innerangle = 2 * Math.PI / (initialShell.size());
			int count = 0;

			HashMap<RankedVertex, EllipticPolarPoint> firstMap = new HashMap<RankedVertex, EllipticPolarPoint>();

			while (!currentPoints.isEmpty()) {
				RankedVertex v = currentPoints.get(0);
				if (!firstMap.containsKey(v)) {
					double theta = innerangle * count;
					count++;
					EllipticPolarPoint p = new EllipticPolarPoint(theta, XR, YR);
					firstMap.put(v, p);
					for (E e : this.graph.getOutEdges(v)) {
						RankedVertex newV = this.graph.getDest(e);
						if (initialShell.contains(newV)) {
							currentPoints.add(0, newV);
						}
					}
					for (E e : this.graph.getInEdges(v)) {
						RankedVertex newV = this.graph.getSource(e);
						if (initialShell.contains(newV)) {
							currentPoints.add(0, newV);
						}
					}
				}

				else
					currentPoints.remove(0);
			}
			for(RankedVertex v:initialShell){
				if(!firstMap.containsKey(v)){
					double theta = innerangle * count;
					count++;
					EllipticPolarPoint p = new EllipticPolarPoint(theta, XR, YR);
					firstMap.put(v, p);
				}
			}
			assignedLayers.add(firstMap);
		}

		// Outer Shells
		for (int depth = 1; depth < layers.size(); depth++) {

			int radiusX = (depth + 1) * XR;
			int radiusY = (depth + 1) * YR;
			List<RankedVertex> shell = layers.get(depth);

			double angle = 2 * Math.PI / (shell.size());
			List<Integer> availableAngles = new ArrayList<Integer>();
			for (int i = 0; i < shell.size() + 1; i++) {
				availableAngles.add(i);
			}

			HashMap<RankedVertex, EllipticPolarPoint> previous = assignedLayers
					.get(depth - 1);

			HashMap<RankedVertex, EllipticPolarPoint> map = new HashMap<RankedVertex, EllipticPolarPoint>();

			for (RankedVertex v : shell) {

				List<EllipticPolarPoint> connected = new ArrayList<EllipticPolarPoint>();

				for (E e : this.graph.getOutEdges(v)) {
					RankedVertex newV = this.graph.getDest(e);
					if (previous.containsKey(newV)) {
						connected.add(previous.get(newV));
					}
				}
				for (E e : this.graph.getInEdges(v)) {
					RankedVertex newV = this.graph.getSource(e);
					if (previous.containsKey(newV)) {
						connected.add(previous.get(newV));
					}
				}

				double theta = 0;
				Integer I = 0;
				double dist = Double.MAX_VALUE;

				for (int i = 0; i < shell.size(); i++) {
					if (availableAngles.contains(i)) {
						double ang = angle * i;
						double d = 0;
						for (EllipticPolarPoint dpoint : connected) {
							d += Math.abs(ang - dpoint.angle) % (2 * Math.PI);
						}
						if (d < dist) {
							dist = d;
							theta = ang;
							I = i;
						}
					}
				}

				EllipticPolarPoint p = new EllipticPolarPoint(theta, radiusX,
						radiusY);
				availableAngles.remove((Integer)I);
				map.put(v, p);
			}
			for(RankedVertex v:shell){
				if(!map.containsKey(v)){
					double theta = availableAngles.get(0);
					EllipticPolarPoint p = new EllipticPolarPoint(theta, XR, YR);
					map.put(v, p);
				}
			}
			assignedLayers.add(assignedLayers.size(), map);
		}

		Map<RankedVertex, EllipticPolarPoint> returnValues = new HashMap<RankedVertex, EllipticPolarPoint>();
		for (HashMap<RankedVertex, EllipticPolarPoint> map : assignedLayers) {
			returnValues.putAll(map);
		}
		return returnValues;
	}

	// Returns a list of layers, where each layer is a list of vertices of the
	// same depth
	private List<ArrayList<RankedVertex>> extractLayers(
			Graph<RankedVertex, E> graph) {
		Iterator<RankedVertex> iterator = graph.getVertices().iterator();

		List<ArrayList<RankedVertex>> layers = new ArrayList<ArrayList<RankedVertex>>();

		while (iterator.hasNext()) {
			RankedVertex v = (RankedVertex) iterator.next();
			int depth = v.getDegree();
			while (layers.size() <= depth) {
				layers.add(new ArrayList<RankedVertex>());
			}
			layers.get(depth).add(v);
		}
		return layers;
	}

}
