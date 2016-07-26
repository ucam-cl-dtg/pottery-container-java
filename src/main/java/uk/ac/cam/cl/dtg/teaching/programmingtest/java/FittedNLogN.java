/**
 * pottery-container-java - Within-container library for testing Java code
 * Copyright © 2015 Andrew Rice (acr31@cam.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.LinkedList;
import java.util.List;

public class FittedNLogN extends FittedCurve {
	
	public FittedNLogN(double[] x, double[] y) {
		super(x, y);
	}
	
	@Override
	protected List<Point> mapValues(double[] x, double[] y) {

		List<Point> result = new LinkedList<Point>();
		for(int i=0;i<x.length;++i) {	
			Point p = new Point();
        	p.xCoeff = new double[] { x[i] * Math.log(x[i]) };
        	p.y = y[i];
            result.add(p);
        }
        return result;
	}

	@Override
	protected String getName() {
		return "o(2^n)";
		
	}

}
