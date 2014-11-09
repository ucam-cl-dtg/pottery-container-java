package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.LinkedList;
import java.util.List;

public abstract class Harness {

	private List<HarnessStep> log = new LinkedList<HarnessStep>();
	
	protected final void log(String message) {
		log.add(HarnessStep.newMessage(message));
	}
	
	protected final void recordState(String message,Object o) {
		log.add(HarnessStep.newState(message, o));
	}
	
	protected final void test(String id, String message, Object actual) {
		log.add(HarnessStep.newTest(id, message,actual));
	}
	
	public List<HarnessStep> getLog() { return log; }
	
	protected abstract void test(Accessor a) throws Throwable;
	
	public void run() throws Throwable {
		test(new Accessor(this));
	}
}
