package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class OSMeasurement {

	public static long getCpuTime() {
		try {
			Sigar sigar = new Sigar();
			ProcCpu procCpu = sigar.getProcCpu(sigar.getPid());
			return procCpu.getTotal();
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}
	}

}
