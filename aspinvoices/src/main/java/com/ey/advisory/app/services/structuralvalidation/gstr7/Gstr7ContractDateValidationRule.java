package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr7ContractDateValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;

		LocalDate date = DateFormatForStructuralValidatons
				          .parseObjToDate(obj.toString().trim());
		
		if (date == null) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CONTRACT_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2021",
					"Invalid Contract Date", location));

		}
	
	return errors;
}

}


