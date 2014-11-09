package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.Map;

public interface Validator {

	public Map<String, ValidationStep> validate(Map<String, Object> actualObjects);

}
