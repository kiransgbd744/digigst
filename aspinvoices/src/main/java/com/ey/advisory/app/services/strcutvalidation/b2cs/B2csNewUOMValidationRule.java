package com.ey.advisory.app.services.strcutvalidation.b2cs;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2csNewUOMValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj))
			return errors;
		
			String uom = obj.toString().trim();
			if (uom.length() > 30 || !uom.matches("^[a-zA-Z-]*$")) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(NEW_UOM);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER5021",
						"Invalid NewUOM.", location));
				return errors;
		}
		return errors;
	}
}
