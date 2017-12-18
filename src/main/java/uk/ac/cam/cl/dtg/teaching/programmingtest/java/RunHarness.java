/*
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

package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.PrintStream;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.HarnessResponse;

public class RunHarness {

  /**
   * Execute the candidate's code using the test harness and print the results to standard out.
   */
  public static void main(String[] args) {
    PrintStream stdout = System.out;
    PrintStream stderr = System.err;
    int exitCode;
    try {
      System.setOut(new PrintStream(new DiscardingOutputStream()));
      System.setErr(new PrintStream(new DiscardingOutputStream()));
      // needs to be an uncloseable stream because ObjectWriter.writeValue seems to call close ;-(
      exitCode = run(stdout);
    } finally {
      System.setOut(stdout);
      System.setErr(stderr);
    }
    System.exit(exitCode);
  }

  private static int run(PrintStream out) {
    ObjectMapper o = new ObjectMapper();
    o.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    ObjectWriter writer = o.writerWithDefaultPrettyPrinter();
    int exitCode = -1;
    try {
      try {
        HarnessResponse h = new HarnessResponse();
        Accessor a = new Accessor();
        for (TestCase t : TestCase.getTestCases()) {
          h.addHarnessPart(t.execute(a));
        }
        h.setCompleted(true);
        writer.writeValue(out, h);
        exitCode = 0;
      } catch (JsonGenerationException | JsonMappingException e) {
        writer.writeValue(
            out, new HarnessResponse("Failed to serialize output: " + e.getMessage()));
      } catch (IOException e) {
        writer.writeValue(out, new HarnessResponse("IOException: " + e.getMessage()));
      }
    } catch (IOException e) {
      out.println(
          String.format(
              "{message:\"Unexpected error when writing response: %s\"]," //
                  + "completed:false,"
                  + "testParts:[]}",
              e.getMessage()));
    }
    out.println();
    out.println();
    return exitCode;
  }
}
