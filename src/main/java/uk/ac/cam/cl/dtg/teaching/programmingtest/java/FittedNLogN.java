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
