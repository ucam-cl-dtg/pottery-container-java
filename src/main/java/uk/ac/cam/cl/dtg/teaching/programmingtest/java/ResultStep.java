package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

public class ResultStep {

	private ValidationStep validationStep;
	private HarnessStep harnessStep;

	public ResultStep() {}
	
	public ResultStep(HarnessStep l,ValidationStep r) {
		this.harnessStep = l;
		this.validationStep = r;
	}

	public ValidationStep getValidationStep() {
		return validationStep;
	}

	public void setValidationStep(ValidationStep validationStep) {
		this.validationStep = validationStep;
	}

	public HarnessStep getHarnessStep() {
		return harnessStep;
	}

	public void setHarnessStep(HarnessStep harnessStep) {
		this.harnessStep = harnessStep;
	}

	
}
