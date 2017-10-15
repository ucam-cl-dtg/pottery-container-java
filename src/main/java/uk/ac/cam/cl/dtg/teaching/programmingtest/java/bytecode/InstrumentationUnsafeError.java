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

public class InstrumentationUnsafeError extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InstrumentationUnsafeError() {
    super();
  }

  public InstrumentationUnsafeError(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InstrumentationUnsafeError(String message, Throwable cause) {
    super(message, cause);
  }

  public InstrumentationUnsafeError(String message) {
    super(message);
  }

  public InstrumentationUnsafeError(Throwable cause) {
    super(cause);
  }
}
