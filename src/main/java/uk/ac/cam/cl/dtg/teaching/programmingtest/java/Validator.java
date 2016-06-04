package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.util.List;
import java.util.Map;

import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.ValidationStep;

public interface Validator {

	public List<ValidationStep> validate(Map<String, Object> actualObjects);

}
