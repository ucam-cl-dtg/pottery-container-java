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

import java.lang.reflect.Method;

/**
 * Class used to record requests for tracking calls to a particular method.
 */
public class MethodCallTrackingRequest {

  private String classToInstrument;
  private String methodToInstrument;
  private Method trackerMethod;

  /**
   * Record requests to this particular method in the chosen class by calling the tracker method.
   */
  public MethodCallTrackingRequest(
      String classToInstrument, String methodToInstrument, Method trackerMethod) {
    super();
    this.classToInstrument = classToInstrument;
    this.methodToInstrument = methodToInstrument;
    this.trackerMethod = trackerMethod;
  }

  public String getClassToInstrument() {
    return classToInstrument;
  }

  public String getMethodToInstrument() {
    return methodToInstrument;
  }

  public Method getTrackerMethod() {
    return trackerMethod;
  }
}
