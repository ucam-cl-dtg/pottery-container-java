package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.List;

public class Result {

	public static final String STATUS_FAIL = "FAIL";
	public static final String STATUS_WARNING = "WARNING";
	public static final String STATUS_PASS = "PASS";
	
	private String message;
	private String detail;
	private String status;
	private List<ResultStep> steps;
	
	public Result() {}

	public Result(String status, String message, String detail, List<ResultStep> steps) {
		super();
		this.message = message;
		this.detail = detail;
		this.status = status;
		this.steps = steps;
	}

	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ResultStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ResultStep> steps) {
		this.steps = steps;
	}

	
	
}
