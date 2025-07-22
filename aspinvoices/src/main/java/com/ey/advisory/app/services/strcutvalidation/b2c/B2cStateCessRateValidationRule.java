package com.ey.advisory.app.services.strcutvalidation.b2c;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cStateCessRateValidationRule implements ValidationRule {
	private static final String STATE_CESS_RATE = "STATE_CESS_RATE";

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		
		if (!isPresent(obj))
			return errors;
			Set<String> errorLocations = new HashSet<>();
			if(!isDecimal(obj.toString().trim())){
				errorLocations.add(STATE_CESS_RATE);
				TransDocProcessingResultLoc location    = 
						new TransDocProcessingResultLoc(
	            null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0218",
						"Invalid Cess Rate (State Cess Rate).",
						location));
				return errors;
			}
			boolean isValid =
					NumberFomatUtil.is3digValidDec(obj.toString().trim());
			if( !isValid){
				errorLocations.add(STATE_CESS_RATE);
				TransDocProcessingResultLoc location    = 
						new TransDocProcessingResultLoc(
	            null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0218",
						"Invalid Cess Rate (State Cess Rate).",
						location));
				return errors;
			}
			BigDecimal result = new BigDecimal(obj.toString().trim());
			if (result.compareTo(BigDecimal.ZERO) < 0) {
				errorLocations.add(STATE_CESS_RATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0218",
						"Invalid Cess Rate (State Cess Rate).", location));
				return errors;
		}	
	
		return errors;
	}

}
