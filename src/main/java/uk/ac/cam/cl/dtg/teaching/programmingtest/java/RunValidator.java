package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.HarnessResponse;
import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.HarnessStep;
import uk.ac.cam.cl.dtg.teaching.programmingtest.java.dto.ValidationResponse;

public class RunValidator {

	public static void main(String[] args) {

		String validatorClass = args[0];
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		long startTime = System.currentTimeMillis();
		try {
			HarnessResponse response;
			try{
				response = objectMapper.readValue(System.in, HarnessResponse.class);
			}
			catch (IOException e) {
				e.printStackTrace();
				objectMapper.writeValue(System.out, new ValidationResponse(false,null,"Failed to read input: "+e.getMessage(),System.currentTimeMillis()-startTime));
				return;
			}
				
			List<HarnessStep> logItems = response.getResponse();
			ValidationResponse result;
			try {
				result = validate(validatorClass,logItems, objectMapper,startTime);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				 objectMapper.writeValue(System.out, new ValidationResponse(false,null,"Failed to access harness class: "+e.getMessage(),System.currentTimeMillis()-startTime));
				return;
			}
			
			try {
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
//				System.out.println(objectMapper.writeValueAsString(result));
			} catch (JsonParseException|JsonGenerationException|JsonMappingException e) {
				objectMapper.writeValue(System.out, new ValidationResponse(false,null,"Failed to serialize output: "+e.getMessage(),System.currentTimeMillis()-startTime));
			} catch (IOException e) {
				objectMapper.writeValue(System.out, new ValidationResponse(false,null,"IOException: "+e.getMessage(),System.currentTimeMillis()-startTime));
			}
		} catch (IOException e) {
			System.out.println(String.format("{failMessage:\"Failed to write error message to response: %s\",success:false,response:null}",e.getMessage()));
		}
		finally {
			System.out.println();
		}

	}
	
	public static ValidationResponse validate(String validatorClass, List<HarnessStep> logItems, ObjectMapper o, long startTime) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {		

		Map<String,Object> measurements = new HashMap<String,Object>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Object get(Object key) {	
				Object r = super.get(key);
				if (r == null) throw new MissingHarnessStepError("Failed to find harness result for key: "+key.toString());
				return r;
			}
			
		};
		
		for(HarnessStep logItem : logItems) {
			if (logItem.getType().equals(HarnessStep.TYPE_MEASUREMENT)) {
				measurements.put(logItem.getId(),logItem.getActual());
			}
		}
		
		@SuppressWarnings("unchecked")
		Class<? extends Validator> c = (Class<? extends Validator>)Class.forName(validatorClass);
		Validator validator = c.newInstance();
		
		 
		try {
			return new ValidationResponse(
					true,
					validator.validate(Collections.unmodifiableMap(measurements)),
					null,
					System.currentTimeMillis()-startTime);			
		}
		catch (Throwable e) {
			return new ValidationResponse(
					false,
					null,
					e.getMessage(),
					System.currentTimeMillis()-startTime);
		}
	}

}
