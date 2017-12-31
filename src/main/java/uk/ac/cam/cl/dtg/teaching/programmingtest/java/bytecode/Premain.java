/*
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

  /** Some classes cause the JVM to segv if we try to transform them. */
  private static final String[] TRANSFORMATION_BLACKLIST =
      new String[] {"java.lang.Class", "java.lang.invoke.LambdaForm"};

  static Instrumentation instrumentation;

  /**
   * Check which kinds of instrumentation we are supposed to perform and begin transforming classes
   * if needed.
   */
  public static void premain(String agentArgs, Instrumentation inst) {
    Premain.instrumentation = inst;

    boolean transforming = false;

    if (InstructionCounterTransformer.ENABLE_INSTRUCTION_COUNTING) {
      inst.addTransformer(new InstructionCounterTransformer());
      transforming = true;
    }

    if (MethodParameterTransformer.TRACKING_REQUESTS.size() != 0) {
      inst.addTransformer(new MethodParameterTransformer());
      transforming = true;
    }

    if (transforming) {
      for (Class<?> c : inst.getAllLoadedClasses()) {
        if (!isBlacklisted(c)) {
          try {
            inst.retransformClasses(c);
          } catch (UnmodifiableClassException e) {
            // ignore
          }
        }
      }
    }
  }

  private static boolean isBlacklisted(Class<?> c) {
    for (String blacklisted : TRANSFORMATION_BLACKLIST) {
      if (c.getName().contains(blacklisted)) {
        return true;
      }
    }
    return false;
  }
}
