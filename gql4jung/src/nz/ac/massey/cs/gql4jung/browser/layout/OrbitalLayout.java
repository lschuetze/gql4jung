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
import nz.ac.massey.cs.gql4jung.browser.resultviews.VisualEdge;
import nz.ac.massey.cs.gql4jung.browser.resultviews.VisualVertex;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Max Dietrich An extension of the elliptic fan layout that uses
 *         varying 'layers' of nodes These nodes are arranged for minimal
 *         intershell connection lengths
 */
public class OrbitalLayout<V extends VisualVertex, E extends VisualEdge>
		extends EllipticFanLayout<Vertex, Edge> {

	public OrbitalLayout(Graph graph) {
		super(graph);
	}

	protected Map<Vertex, EllipticPolarPoint> getPoints(
			Graph<Vertex, Edge> graph) {

		List<ArrayList<VisualVertex>> layers = this.extractLayers(graph);

		List<HashMap<Vertex, EllipticPolarPoint>> assignedLayers = new ArrayList<HashMap<Vertex, EllipticPolarPoint>>();

		int centerX = this.size.width / 2 - boxSize.width / 2;
		int centerY = this.size.height / 2 - boxSize.height / 2;
		int XR = (centerX - boxSize.width) / (layers.size());
		int YR = (centerY - boxSize.height) / (layers.size());

		// Initial shell
		{
			List<VisualVertex> initialShell = layers.get(0);
			Vertex initialPoint = initialShell.get(0);
			List<Vertex> currentPoints = new ArrayList<Vertex>();
			currentPoints.add(initialPoint);

			double innerangle = 2 * Math.PI / (initialShell.size());
			int count = 0;

			HashMap<Vertex, EllipticPolarPoint> firstMap = new HashMap<Vertex, EllipticPolarPoint>();

			while (!currentPoints.isEmpty()) {
				Vertex v = currentPoints.get(0);
				if (!firstMap.containsKey(v)) {
					double theta = innerangle * count;
					count++;
					EllipticPolarPoint p = new EllipticPolarPoint(theta, XR, YR);
					firstMap.put(v, p);
					for (Edge e : v.getOutEdges()) {
						Vertex newV = e.getEnd();
						if (initialShell.contains(newV)) {
							currentPoints.add(0, newV);
						}
					}
					for (Edge e : v.getInEdges()) {
						Vertex newV = e.getStart();
						if (initialShell.contains(newV)) {
							currentPoints.add(0, newV);
						}
					}
				}

				else
					currentPoints.remove(0);
			}
			assignedLayers.add(firstMap);
		}

		// Outer Shells
		for (int depth = 1; depth < layers.size(); depth++) {

			int radiusX = (depth + 1) * XR;
			int radiusY = (depth + 1) * YR;
			List<VisualVertex> shell = layers.get(depth);

			double angle = 2 * Math.PI / (shell.size());
			List<Integer> availableAngles = new ArrayList<Integer>();
			for (int i = 0; i < shell.size() + 1; i++) {
				availableAngles.add(i);
			}

			HashMap<Vertex, EllipticPolarPoint> previous = assignedLayers
					.get(depth - 1);

			HashMap<Vertex, EllipticPolarPoint> map = new HashMap<Vertex, EllipticPolarPoint>();

			for (Vertex v : shell) {

				List<EllipticPolarPoint> connected = new ArrayList<EllipticPolarPoint>();

				for (Edge e : v.getOutEdges()) {
					Vertex newV = e.getEnd();
					if (previous.containsKey(newV)) {
						connected.add(previous.get(newV));
					}
				}
				for (Edge e : v.getInEdges()) {
					Vertex newV = e.getStart();
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

			assignedLayers.add(assignedLayers.size(), map);
		}

		Map<Vertex, EllipticPolarPoint> returnValues = new HashMap<Vertex, EllipticPolarPoint>();
		for (HashMap<Vertex, EllipticPolarPoint> map : assignedLayers) {
			returnValues.putAll(map);
		}
		return returnValues;
	}

	// Returns a list of layers, where each layer is a list of vertices of the
	// same depth
	private List<ArrayList<VisualVertex>> extractLayers(
			Graph<Vertex, Edge> graph) {
		Iterator<Vertex> iterator = graph.getVertices().iterator();

		List<ArrayList<VisualVertex>> layers = new ArrayList<ArrayList<VisualVertex>>();

		while (iterator.hasNext()) {
			VisualVertex v = (VisualVertex) iterator.next();
			int depth = v.getDistanceFromMotif();
			while (layers.size() <= depth) {
				layers.add(new ArrayList<VisualVertex>());
			}
			layers.get(depth).add(v);
		}
		return layers;
	}

}
