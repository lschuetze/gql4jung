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

import java.awt.Point;

/**
 * 
 * @author Max Dietrich A polar coordinate in an elliptic domain
 */
public class EllipticPolarPoint {
	
	//Angle, minor and mayor radii of ellipse
	public double angle, xRadius, yRadius;

	public EllipticPolarPoint(double angle, double radius) {
		this.angle = (angle)%(2*Math.PI);
		this.xRadius = radius;
		this.yRadius = radius;
	}

	public EllipticPolarPoint(double angle, double Xr, double Yr) {
		this.angle = (angle)%(2*Math.PI);
		this.xRadius = Xr;
		this.yRadius = Yr;
	}

	public Point toCartesian() {
		int x = (int) (Math.sin(angle) * xRadius);
		int y = (int) (Math.cos(angle) * yRadius);
		return new Point(x, y);
	}

	public Point toCartesian(int xOffs, int yOffs) {
		int x = (int) (Math.sin(angle) * xRadius) + xOffs;
		int y = (int) (Math.cos(angle) * yRadius) + yOffs;
		return new Point(x, y);
	}
}
