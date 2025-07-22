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
public class UnitOfMeasurementValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		/*if (obj.toString().trim().length() < 3 || obj.toString().trim().length() > 50) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.UOM);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10052",
					             "Invalid Unit of measurement.", location));
			return errors;
		}*/
		if(obj.toString().trim().length() > 100){
			obj=obj.toString().trim().substring(0, 100).toUpperCase();
		}
		return errors;
	}

}
