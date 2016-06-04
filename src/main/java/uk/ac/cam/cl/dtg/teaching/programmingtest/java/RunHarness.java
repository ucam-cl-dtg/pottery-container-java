package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.HarnessResponse;
import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.HarnessStep;

public class RunHarness {

	public static void main(String[] args) {
		PrintStream stdout = System.out;
		PrintStream stderr = System.err;
		int exitCode;
		try {
			System.setOut(new PrintStream(new DiscardingOutputStream()));
			System.setErr(new PrintStream(new DiscardingOutputStream()));
			// needs to be an uncloseable stream because ObjectWriter.writeValue seems to call close ;-( 
			exitCode = run(args[0],new UncloseablePrintStream(stdout));
		}
		finally {
			System.setOut(stdout);
			System.setErr(stderr);
		}
		System.exit(exitCode);
	}
	
	public static int run(String className, PrintStream out) {
		ObjectMapper o = new ObjectMapper();
		ObjectWriter writer = o.writerWithDefaultPrettyPrinter();
//		ObjectWriter writer = o.writer();
		int exitCode = -1;
		long startTime = System.currentTimeMillis();
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Harness> testClass = (Class<? extends Harness>)Class.forName(className);
				Harness instance = testClass.newInstance();
				boolean success = true;
				try {
					instance.run();
					exitCode = 0;
				} catch (Throwable t) {
					success = false;
					instance.getLog().add(HarnessStep.newMessage("Unexpected exception: "+t.getMessage()+" "+t.getClass()));
					instance.getLog().add(HarnessStep.newState("Exception",t));
				} 
				writer.writeValue(out,new HarnessResponse(success,instance.getLog(),null,System.currentTimeMillis()-startTime));
			} catch (JsonGenerationException|JsonMappingException e) {
				writer.writeValue(out, new HarnessResponse(false,null,"Failed to serialize output: "+e.getMessage(),System.currentTimeMillis()-startTime));
			} catch (ClassNotFoundException e) {
				writer.writeValue(out, new HarnessResponse(false,null,"Failed to load harness class: "+e.getMessage(),System.currentTimeMillis()-startTime));
			} catch (InstantiationException|IllegalAccessException e) {
				writer.writeValue(out, new HarnessResponse(false,null,"Failed to access harness class: "+e.getMessage(),System.currentTimeMillis()-startTime));
			} catch (IOException e) {
				writer.writeValue(out, new HarnessResponse(false,null,"IOException: "+e.getMessage(),System.currentTimeMillis()-startTime));
			}
		} catch (IOException e) {
			out.println(String.format("{failMessage:\"Failed to write unexpected error message to response: %s\",success:false,response:null}]",e.getMessage()));
		}
		out.println();
		out.println();
		return exitCode;
	}

}
