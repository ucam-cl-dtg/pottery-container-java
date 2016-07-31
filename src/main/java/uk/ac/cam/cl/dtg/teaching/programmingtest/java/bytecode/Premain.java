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
package uk.ac.cam.cl.dtg.teaching.programmingtest.java.bytecode;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Premain {

	static Instrumentation instrumentation;
	
	public static void premain(String agentArgs, Instrumentation inst) {
		Premain.instrumentation = inst;
		
		boolean transforming = false;
		
		if (Counter.ENABLE_INSTRUCTION_COUNTING) {
			inst.addTransformer(new InstructionCounterTransformer());
			transforming = true;
		}

		if (MethodParameterTransformer.TRACKING_REQUESTS.size() != 0) {
			inst.addTransformer(new MethodParameterTransformer());
			transforming = true;
		}
		
		if (transforming) {
			for(Class<?> c : inst.getAllLoadedClasses()) {
				try{
					inst.retransformClasses(c);
				} catch (UnmodifiableClassException e) {}	
			}
		}
	}
}
	
