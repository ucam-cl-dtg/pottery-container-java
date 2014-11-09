package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.io.OutputStream;

public class DiscardingOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {
		// Do nothing
	}

}
