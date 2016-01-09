package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

public class CurveFitter {

	public CurveFitter() {
		
	}

	public static void main(String[] args) {
		double[] y = new double[] {
				30,
				50,
				50,
				60,
				100,
				100,
				120,
				170,
				140,
				180,
				200,
				220,
				260,
				290,
				320,
				280,
				320 };
		
		double[] y2 = new double[] {
				1570,
				 2860,
				 4340,
				 5840,
				 7450,
				 9020,
				 10660,
				 12250,
				 13900,
				 15560,
				 17120,
				 18670,
				 20360,
				 22220,
				 23900,
				 25810,
				 27350

		};
		
		double[] x = new double[y.length];
		for(int i=0;i<x.length;++i) x[i] = scale(i);
		
		System.out.println("x=["+Joiner.on(",").join(Arrays.stream(x).boxed().collect(Collectors.toList()))+"]");
		System.out.println("y=["+Joiner.on(",").join(Arrays.stream(y).boxed().collect(Collectors.toList()))+"]");
		System.out.println(linearFit(x,y));
		
		System.out.println("x=["+Joiner.on(",").join(Arrays.stream(x).boxed().collect(Collectors.toList()))+"]");
		System.out.println("y=["+Joiner.on(",").join(Arrays.stream(y2).boxed().collect(Collectors.toList()))+"]");
		System.out.println(linearFit(x,y2));
	}
	
	
	public static int scale(int i) {
		return 10000000 + i*10000000;	
	}
	
	static void printA(String name, List<Double> x) {
		System.out.println(name+"= ["+Joiner.on(",").join(x)+"]");		
	}
	
	public static String linearFit(double[] x, double[] y) {
        List<FittedCurve> options = new LinkedList<FittedCurve>();
        options.add(new FittedPolynomial(x,y,1));
        options.add(new FittedPolynomial(x,y,2));
        options.add(new FittedPolynomial(x,y,3));
        options.add(new FittedExponential(x,y));
        options.add(new FittedNLogN(x,y));
//        options.add(new Option(y,logx(x),"o(lg n)"));
                
        for(FittedCurve o: options) {
        	o.fit();
        }
        
        FittedCurve min = Collections.min(options);        
        return min.getName(); 
	}
	

	
	static double[][] logx(double[] x) {
		double[][] result = new double[x.length][];
        for(int i=0;i<x.length;++i) {
        	result[i] = new double[] { 1,Math.log(x[i]) };
        }
        return result;		
	}

	static double[][] nlogx(double[] x) {
		double[][] result = new double[x.length][];
        for(int i=0;i<x.length;++i) {
        	result[i] = new double[] { 1,x[i] * Math.log(x[i]) };
        }
        return result;		
	}

	
	static double[] logy(double[] y) {
		double[] result = new double[y.length];
		for(int i=0;i<y.length;++i) {
			result[i] = Math.log(y[i]);
		}
		return result;
	}
}
