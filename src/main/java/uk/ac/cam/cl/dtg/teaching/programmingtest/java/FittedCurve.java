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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.google.common.base.Joiner;

public abstract class FittedCurve implements Comparable<FittedCurve> {
	
	protected static class Point {
		double y;
		double[] xCoeff;
	}
	
	private double rsq;
	private double[] x;
	private double[] y;
	
	public FittedCurve(double[] x,double[] y) {
		super();
		this.x = x;
		this.y = y;
	}

	protected abstract List<Point> mapValues(double[] x,double[] y);
	protected double unmapY(double y) { return y; }
	protected abstract String getName();
	
	@Override
	public int compareTo(FittedCurve o) {
		if (rsq == o.rsq) {
			return Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
		}
		else {
			return Double.compare(rsq, o.rsq);
		}
	}
	
	public void fit() {
		
		List<Point> mapped = mapValues(x,y);
		DescriptiveStatistics s = new DescriptiveStatistics();
		for(int i=0;i<20;++i) {
			double r = fit(mapped,0.5);
			s.addValue(r);
		}
		rsq = s.getMean();
//		System.out.println(rsq);
	}
	
	public double fit(List<Point> mapped,double proportion) {
		Collections.shuffle(mapped);
		int total = (int)(mapped.size() * proportion);
		double[][] x = new double[total][];
		double[] y = new double[total];
		Iterator<Point> step = mapped.iterator();
		for(int i=0;i<total;++i) {
			Point next = step.next();
			x[i] = next.xCoeff;
			y[i] = next.y;			
		}
		
		OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
    	ols.setNoIntercept(true); 
    	ols.newSampleData(y,x);
    	RealMatrix coef = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters());
    	double rsqTotal = 0.0;
    	for(Point p : mapped) {
    		double yhat = coef.preMultiply(p.xCoeff)[0];
    		rsqTotal += unmapY(Math.pow(p.y-yhat,2.0));
    	}
    	
    	if (DEBUG) {
    		List<Point> newMaps = mapValues(this.x,this.y);
    		LinkedList<Double> xs = new LinkedList<>();
    		LinkedList<Double> ys = new LinkedList<>();
    		int i=0;
    		for(Point p : newMaps) {
        		double yhat = unmapY(coef.preMultiply(p.xCoeff)[0]);
        		ys.addLast(yhat);
        		xs.addLast(this.x[i++]);
    		}
    		System.out.println("x=["+Joiner.on(",").join(xs)+"]");
    		System.out.println("y=["+Joiner.on(",").join(ys)+"]");
    	}
    	return rsqTotal;
	}
	
	public static final boolean DEBUG = false;
}