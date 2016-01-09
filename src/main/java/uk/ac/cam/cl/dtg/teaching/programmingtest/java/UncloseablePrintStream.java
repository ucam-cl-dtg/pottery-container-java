package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.OutputStream;
import java.io.PrintStream;

public class UncloseablePrintStream extends PrintStream {

	public UncloseablePrintStream(OutputStream out) {
		super(out);
	}

	@Override
	public void close() {
		// Do not close
		super.flush();
	}

}
