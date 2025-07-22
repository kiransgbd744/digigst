package com.ey.advisory.app.services.strcutvalidation.b2c;

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

public class B2cStateApplyingCessValidationRule implements ValidationRule {
	private static final String STATE_APPLYING_CESS = "STATE_APPLYING_CESS";

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj))
			return errors;
		int stateAppCess = obj.toString().trim().length();
			if(stateAppCess > 2){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(STATE_APPLYING_CESS);
				TransDocProcessingResultLoc location    = 
						new TransDocProcessingResultLoc(
	            null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0217",
						"Invalid State Applying Cess (State Code).",
						location));
				return errors;
			}
		
		return errors;
	}

}
