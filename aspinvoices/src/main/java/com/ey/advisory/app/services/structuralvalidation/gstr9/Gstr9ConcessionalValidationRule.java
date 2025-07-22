package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9ConcessionalValidationRule implements ValidationRule {
	private static final List<String> IMPORTS = ImmutableList.of("Y", "N",
			"n", "y");


	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;
		

		if (!IMPORTS.contains(obj.toString().trim())) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CONCESSIONAL);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6180",
					"Invalid Concessional rate value", location));

			return errors;
		}
	
	return errors;
}

}



