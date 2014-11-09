package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

public class ValidationStep {

	public static final String STATUS_PASS = Result.STATUS_PASS;
	public static final String STATUS_WARNING = Result.STATUS_WARNING;
	public static final String STATUS_FAIL = Result.STATUS_FAIL;
	
	private String status;
	private String message;

	public ValidationStep(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public ValidationStep() {
		
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
