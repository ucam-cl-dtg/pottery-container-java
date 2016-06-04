package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.HarnessStep;

public abstract class Harness {

	private List<HarnessStep> log = new LinkedList<HarnessStep>();
	
	protected final void log(String message) {
		log.add(HarnessStep.newMessage(message));
	}
	
	protected final void recordState(String message,Object o) {
		log.add(HarnessStep.newState(message, o));
	}
	
	protected final void test(String id, String message, Object actual) {
		log.add(HarnessStep.newMeasurement(id, message,actual));
	}
	
	public List<HarnessStep> getLog() { return log; }
	
	protected abstract void test(Accessor a) throws Throwable;
	
	public void run() throws Throwable {
		test(new Accessor(this));
	}

	private long totalCpu = 0;
	public void startCompute() {
		try {
			Sigar sigar = new Sigar();
			ProcCpu procCpu = sigar.getProcCpu(sigar.getPid());
			totalCpu = procCpu.getTotal();
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void finishCompute(String tag) {
		try {
			Sigar sigar = new Sigar();
			ProcCpu procCpu = sigar.getProcCpu(sigar.getPid());
			log.add(HarnessStep.newMeasurement(tag,"CPU usage", procCpu.getTotal() - totalCpu));
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}
	}
	
	private long nanoTime = 0;
	public void startTime() {
		nanoTime = System.nanoTime();
	}
	
	public void finishTime() {
		log.add(HarnessStep.newState("runningtime",System.nanoTime()-nanoTime));
	}
	
	
}
