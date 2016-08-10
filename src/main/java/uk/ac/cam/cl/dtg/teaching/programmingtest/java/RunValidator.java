/**
 * pottery-container-java - Within-container library for testing Java code
 * Copyright © 2015 Andrew Rice (acr31@cam.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Interpretation;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.Measurement;
import uk.ac.cam.cl.dtg.teaching.programmingtest.containerinterface.ValidatorResponse;

public class RunValidator {

	public static void main(String[] args) {

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
		
		Set<String> missingIds = new TreeSet<String>();
		ValidatorResponse v = new ValidatorResponse();
		try {
			for(TestCase t : TestCase.getTestCases()) {
				t.interpret(map, v,missingIds);
			}
			if (!missingIds.isEmpty()) {
				v.setMessage("Incomplete response from harness. Missing measurements: "+missingIds.stream().reduce((x,y)->x+","+y).get());
			}
			else {
				v.setCompleted(true);
			}
		} 
		catch (IOException e1) {
			System.out.println(String.format("{completed:false,interpretation:\"%s\",measurements:[],missingIds:[],message:\"%s\"}",
					Interpretation.INTERPRETED_FAILED,
					"Failed to scan for classes: "+e1.getMessage()));
			System.exit(-1);
			return;
		}
		
		ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
//		ObjectWriter writer = objectMapper.writer();

		try {
			System.out.println(writer.writeValueAsString(v));
		} catch (IOException e) {
			System.out.println(String.format("{completed:false,interpretation:\"%s\",measurements:[],missingIds:[],message:\"%s\"}",
					Interpretation.INTERPRETED_FAILED,
					"Failed to serialize output: "+e.getMessage()));
			System.exit(-1);
			return;
		}
		System.out.println();
		System.out.println();
		System.exit(0);
	}

}
