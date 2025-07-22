package com.ey.advisory.app.services.strcutvalidation.einvoice;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class DispatcherLoactionValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		/*if (!isPresent(obj)) return errors;

		if (obj.toString().trim().length() > 60 || 
				obj.toString().trim().length() < 3) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DISPATCHER_LOCATION);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10014",
					             "Invalid Dispatcher Location", location));
			return errors;
		}*/
		return errors;
	}

}
