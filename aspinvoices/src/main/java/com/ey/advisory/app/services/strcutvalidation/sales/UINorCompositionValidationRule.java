package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.app.services.strcutvalidation.Converter;
import com.ey.advisory.app.services.strcutvalidation.DataConverterFactory;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class UINorCompositionValidationRule implements ValidationRule {
	/*@Autowired
	private DataConverterFactory converterFactory;*/
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
			ImmutableList.of("U","C","L","UL","CL"," ");
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		if (obj!=null) {
		String UINorComposition=obj.toString();
		if (!(ORGDOCNUM_REQUIRING_IMPORTS.contains(
				UINorComposition))) {
	
		
		errorLocations.add(GSTConstants.UINorComposition);
TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER062",
				"Invalid Indicator",
				location));

	
		
	}
		
		
	
		}
		
		return errors;
	}

	

}
