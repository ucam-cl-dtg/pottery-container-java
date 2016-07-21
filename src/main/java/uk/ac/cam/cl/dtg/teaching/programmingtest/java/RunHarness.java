package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.HarnessResponse;

public class RunHarness {

	public static void main(String[] args) {
		PrintStream stdout = System.out;
		PrintStream stderr = System.err;
		int exitCode;
		try {
			System.setOut(new PrintStream(new DiscardingOutputStream()));
			System.setErr(new PrintStream(new DiscardingOutputStream()));
			// needs to be an uncloseable stream because ObjectWriter.writeValue seems to call close ;-( 
			exitCode = run(args[0],stdout);
		}
		finally {
			System.setOut(stdout);
			System.setErr(stderr);
		}
		System.exit(exitCode);
	}
	
	public static int run(String className, PrintStream out) {
		ObjectMapper o = new ObjectMapper();
		o.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		ObjectWriter writer = o.writerWithDefaultPrettyPrinter();
//		ObjectWriter writer = o.writer();
		int exitCode = -1;
		try {
			try {
				HarnessResponse h = new HarnessResponse();
				Accessor a = new Accessor();
				for(TestCase t : TestCase.getTestCases(className)) {
					h.addHarnessPart(t.execute(a));
				}
				h.setCompleted(true);	
				writer.writeValue(out,h);
				exitCode = 0;
			} catch (JsonGenerationException|JsonMappingException e) {
				writer.writeValue(out, new HarnessResponse("Failed to serialize output: "+e.getMessage()));
			} catch (ClassNotFoundException e) {
				writer.writeValue(out, new HarnessResponse("Failed to load harness class: "+e.getMessage()));
			} catch (IOException e) {
				writer.writeValue(out, new HarnessResponse("IOException: "+e.getMessage()));
			}
		} catch (IOException e) {
			out.println(String.format("{message:\"Unexpected error when writing response: %s\"],completed:false,testParts:[]}",e.getMessage()));
		}
		out.println();
		out.println();
		return exitCode;
	}

}
