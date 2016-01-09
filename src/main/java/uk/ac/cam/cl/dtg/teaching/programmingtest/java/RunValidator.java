package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

public class RunValidator {

	public static void main(String[] args) {

		String validatorClass = args[0];
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			try{
				List<HarnessStep> logItems = objectMapper.readValue(System.in, new TypeReference<List<HarnessStep>>() {});
				Result result = validate(validatorClass,logItems, objectMapper);
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
//				System.out.println(objectMapper.writeValueAsString(result));
			} catch (JsonParseException e) {
				objectMapper.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to read input: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			}
			catch (JsonGenerationException|JsonMappingException e) {
				objectMapper.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to serialize output: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (ClassNotFoundException e) {
				objectMapper.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to load harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (InstantiationException|IllegalAccessException e) {
				objectMapper.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("Failed to access harness class: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			} catch (IOException e) {
				objectMapper.writeValue(System.out, ImmutableList.of(HarnessStep.newMessage("IOException: "+e.getMessage()), HarnessStep.newState("Exception",e)));
			}
		} catch (IOException e) {
			System.out.println(String.format("[{message:\"Failed to write unexpected error message to response: %s\",actual:true,expected:false}]",e.getMessage()));
		}
		
		System.out.println();	

	}
	
	public static Result validate(String validatorClass, List<HarnessStep> logItems, ObjectMapper o) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {		

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
		
		Result r = new Result();
		try {
			r.setResults(validator.validate(Collections.unmodifiableMap(measurements)));
			r.setStatus("COMPLETED");
			
		}
		catch (Throwable e) {
			r.setDiagnosticMessage(e.getMessage());
			r.setStatus("FAILED");
		}
		return r;
	}

}
