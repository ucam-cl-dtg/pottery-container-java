package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Interpretation;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Measurement;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.ValidatorResponse;

public class RunValidator {

	public static void main(String[] args) {

		String validatorClass = args[0];
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		List<Measurement> measurements;
		try{
			measurements = objectMapper.readValue(System.in, new TypeReference<List<Measurement>>() {});
		}
		catch (IOException e) {
			System.out.println("Failed to read input: "+e.getMessage());
			System.exit(-1);
			return;
		}

		Map<String,Measurement> map = new HashMap<>();
		for(Measurement m : measurements) {
			map.put(m.getId(), m);
		}
		
		ValidatorResponse v = new ValidatorResponse();
		try {
			for(TestCase t : TestCase.getTestCases(validatorClass)) {
				t.interpret(map, v);
			}
			v.setCompleted(true);
		} catch (ClassNotFoundException e1) {
			v.setMessage("Failed to load validator class: "+e1.getMessage());
		}
		
		ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
//		ObjectWriter writer = objectMapper.writer();

		try {
			System.out.println(writer.writeValueAsString(v));
		} catch (IOException e) {
			System.out.println(String.format("{completed:false,interpretation:\"%s\",measurements:[],missingIds:[],message:\"%s\"}",
					Interpretation.INTERPRETED_BAD,
					"Failed to serialize output: "+e.getMessage()));
			System.exit(-1);
			return;
		}
		System.exit(0);
	}

}
