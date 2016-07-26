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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;

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
				if (e.getCause() != null) {
					e = e.getCause();
				}
			}
			if (e instanceof InvocationTargetException) {
				Throwable f = ((InvocationTargetException)e).getTargetException();
				if (f != null) e = f;
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
		return p;
	}
	
	public void interpret(Map<String,Measurement> m, ValidatorResponse v, Set<String> missingIds) {
		for (String id : getIds()) {
			if (m.containsKey(id)) {
				Interpretation i = interpret(m.get(id));
				v.getInterpretations().add(i);
			}
			else {
				missingIds.add(id);
			}
		}
	}
	

	public static List<TestCase> getTestCases() throws IOException {
		return ClassPath.from(TestCase.class.getClassLoader()).getTopLevelClasses()
		.stream()
		.map(i-> {
			try {
				return i.load();
			} catch (NoClassDefFoundError e1) {
				return null;
			}
		})
		.filter(c -> c != null && c.getAnnotation(JavaTest.class) != null)
		.map(c -> Arrays.asList(c.getFields()).stream()
				.map(f -> {
					if (TestCase.class.isAssignableFrom(f.getType())) {
						try {				
							return (TestCase)f.get(null);
						} catch (IllegalArgumentException|IllegalAccessException e) {}
					}
					return null;
				})
				.filter(t -> t != null)
				.collect(Collectors.toList()))
		.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
	}
}