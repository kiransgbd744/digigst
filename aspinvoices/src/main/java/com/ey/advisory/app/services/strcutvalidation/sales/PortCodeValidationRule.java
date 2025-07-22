package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.app.data.repositories.client.PortCodeRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.strcutvalidation.*;
import com.google.common.collect.ImmutableList;

public class PortCodeValidationRule implements ValidationRule {

	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj!=null){
		if(obj.toString().length() > 6){
			errorLocations.add(GSTConstants.PORT_CODE);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER067",
					"Invalid Port Code",
					location));
		}
		}
	
		
		return errors;

	}

	
}
