package com.ey.advisory.app.services.strcutvalidation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class CustomeCodeValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		/*errorLocations.add(GSTConstants.TAXABLE_VALUE);
		TransDocProcessingResultLoc location 
		                     = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
				"Invalid Taxable Value",
				location));*/
		if (ValidatorUtil.isValuePresent(obj)) {
			/*Converter converter = converterFactory.getConverter(obj,
					String.class);
			if (converter != null) {
				// List<String> cgstinList = new ArrayList<>();
				String cgstin = (String) converter.convert(obj);
				if (!(cgstin.length() == 15 && gstinLookUpServiceImpl
						.isValidGstinForGroup(cgstin, "ern00002"))) {
					return errors;
				}
			}*/
		}
		return errors;
	}

}
