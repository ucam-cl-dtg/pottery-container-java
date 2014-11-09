package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

public class RunHarness {

	public static void main(String[] args) {

		PrintStream stdout = System.out;
		PrintStream stderr = System.err;
		System.setOut(new PrintStream(new DiscardingOutputStream()));
		System.setErr(new PrintStream(new DiscardingOutputStream()));
		ObjectMapper o = new ObjectMapper();
		int exitCode = 0;
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Harness> testClass = (Class<? extends Harness>)Class.forName(args[0]);
				Harness instance = testClass.newInstance();
				try {
					instance.run();
				} catch (Throwable t) {
					exitCode = -1;
					instance.getLog().add(HarnessStep.newMessage("Unexpected exception: "+t.getMessage()));
					instance.getLog().add(HarnessStep.newState("Exception",t));
				} finally {
					System.setOut(stdout);
					System.setErr(stderr);
				}
				System.out.println(o.writerWithDefaultPrettyPrinter().writeValueAsString(instance.getLog()));
				System.out.println();
				System.exit(exitCode);
			} catch (JsonGenerationException|JsonMappingException e) {
				o.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to serialize output: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (ClassNotFoundException e) {
				o.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to load harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (InstantiationException|IllegalAccessException e) {
				o.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to access harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (IOException e) {
				o.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("IOException: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			}
		} catch (IOException e) {
			System.out.println(String.format("[{message:\"Failed to write unexpected error message to response: %s\",actual:true,expected:false}]",e.getMessage()));
		}
		System.out.println();
		System.exit(-1);
	}

}
