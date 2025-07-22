package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.app.services.strcutvalidation.*;
public class ItemDescriptionValidationRule implements ValidationRule {
	@Autowired
	private DataConverterFactory converterFactory;

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                 TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.TAXABLE_VALUE);
		TransDocProcessingResultLoc location 
		                     = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
				"Invalid Taxable Value",
				location));
		if (ValidatorUtil.isValuePresent(obj)) {
			Converter converter = converterFactory.getConverter(obj,
					String.class);
			if (converter != null) {
				String itemDesc = (String) converter.convert(obj);
				if(itemDesc.length() >= 100){
				return	errors;
				}
					//return itemDesc.length() <= 100;
			}
		}
		return errors;
	}

}
