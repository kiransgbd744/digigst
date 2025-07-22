package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

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
public class ShipToPhoneValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) return errors;
		
		/*if (obj.toString().trim().length() != 10) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SHIP_TO_PHONE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10043",
					             "Invalid Ship to Phone", location));
			return errors;
		}*/
		return errors;
	}

}
