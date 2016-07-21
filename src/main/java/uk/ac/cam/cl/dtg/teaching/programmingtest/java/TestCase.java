package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.HarnessPart;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Interpretation;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Measurement;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.ValidatorResponse;

public abstract class TestCase {
	
	/**
	 * @return the unique ids given to the measurements in this test
	 */
	protected abstract String[] getIds();

	/**
	 * Run the test, returning a HarnessPart with summary, steps and measurements
	 * @param accessor dynamic loader for the candidates code
	 * @param HarnessPart object with details of the test (add to this)
	 */
	protected abstract void test(Accessor accessor,HarnessPart p);

	/**
	 * Inspect the measurement and return an interpretation
	 * @param m the measurement
	 * @return the interpretation of the measurement
	 */
	protected abstract Interpretation interpret(Measurement m);
	
	public HarnessPart execute(Accessor accessor) {
		HarnessPart p = new HarnessPart();
		try {
			test(accessor,p);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				e = e.getCause();
				if (e instanceof InvocationTargetException) {
					e = ((InvocationTargetException)e).getTargetException();
				}
				p.setErrorSummary("An unexpected exception occurred: "+e.getClass().getName());
				StringBuffer b = new StringBuffer();
				b.append(String.format("<code>%s</code>: %s",e.getClass().getName(),e.getMessage()));
				b.append("<ul>");
				for(StackTraceElement el : e.getStackTrace()) {
					b.append(String.format("<li>at <code>%s.%s(%s:%d)</code></li>",el.getClassName(),el.getMethodName(),el.getFileName(),el.getLineNumber()));
				}
				b.append("</ul>");
				p.setErrorDetail(b.toString());
			}
		}
		return p;
	}
	
	public void interpret(Map<String,Measurement> m, ValidatorResponse v) {
		for (String id : getIds()) {
			if (m.containsKey(id)) {
				Interpretation i = interpret(m.get(id));
				v.getInterpretations().add(i);
			}
			else {
				v.getMissingIds().add(id);
			}
		}
	}
	

	public static List<TestCase> getTestCases(String className) throws ClassNotFoundException {
		Class<?> testClass = Class.forName(className);		
		return Arrays.asList(testClass.getFields()).stream()
		.map(f -> {
			if (TestCase.class.isAssignableFrom(f.getType())) {
				try {				
					return Optional.of((TestCase)f.get(null));
				} catch (IllegalArgumentException|IllegalAccessException e) {}
			}
			return Optional.<TestCase>empty();
		})
		.filter(t -> t.isPresent())
		.map(t -> t.get())
		.collect(Collectors.toList());
	}
}