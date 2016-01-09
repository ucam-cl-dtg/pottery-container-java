package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;

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
		int exitCode = 0;
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Harness> testClass = (Class<? extends Harness>)Class.forName(className);
				Harness instance = testClass.newInstance();
				try {
					instance.run();
				} catch (Throwable t) {
					exitCode = -1;
					instance.getLog().add(HarnessStep.newMessage("Unexpected exception: "+t.getMessage()+" "+t.getClass()));
					instance.getLog().add(HarnessStep.newState("Exception",t));
				} 
				writer.writeValue(out,instance.getLog());
			} catch (JsonGenerationException|JsonMappingException e) {
				writer.writeValue(out, ImmutableList.of(HarnessStep.newMessage("Failed to serialize output: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (ClassNotFoundException e) {
				writer.writeValue(out, ImmutableList.of(HarnessStep.newMessage("Failed to load harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (InstantiationException|IllegalAccessException e) {
				writer.writeValue(out, ImmutableList.of(HarnessStep.newMessage("Failed to access harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (IOException e) {
				writer.writeValue(out, ImmutableList.of(HarnessStep.newMessage("IOException: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			}
		} catch (IOException e) {
			out.println(String.format("[{message:\"Failed to write unexpected error message to response: %s\",actual:true,expected:false}]",e.getMessage()));
		}
		out.println();
		out.println();
		return exitCode;
	}

}
