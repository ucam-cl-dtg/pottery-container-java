/**
 * pottery-container-java - Within-container library for testing Java code Copyright © 2015 Andrew
 * Rice (acr31@cam.ac.uk)
 *
 * <p>This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.cam.cl.dtg.teaching.programmingtest.java.bytecode;

import java.util.concurrent.atomic.AtomicLong;

public class InstructionCounter {

  private static AtomicLong instructions = new AtomicLong(0);
  private static AtomicLong allocations = new AtomicLong(0);

  public static void incrementInstructions() {
    instructions.incrementAndGet();
  }

  public static long getTotalInstructions() {
    return instructions.get();
  }

  public static void incrementAllocations(Object allocated) {
    allocations.addAndGet(Premain.instrumentation.getObjectSize(allocated));
  }

  public static long getTotalAllocations() {
    return allocations.get();
  }
}
