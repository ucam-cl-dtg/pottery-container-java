package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.LinkedList;
import java.util.List;

public class FittedPolynomial extends FittedCurve {

	private int degree;
	
	public FittedPolynomial(double[] x, double[] y, int degree) {
		super(x, y);
		this.degree = degree;
	}

	@Override
	protected List<Point> mapValues(double[] x, double[] y) {

		List<Point> result = new LinkedList<Point>();
		for(int i=0;i<x.length;++i) {	
			Point p = new Point();
        	p.xCoeff = new double[degree+1];
        	p.y = y[i];
            double s = 1.0;
            for(int j=0;j<=degree;++j) {
            	p.xCoeff[j] = s;
            	s *= x[i];
            }
            result.add(p);
        }
        return result;
	}

	@Override
	protected String getName() {
		return "o(n^"+degree+")";
		
	}

}
