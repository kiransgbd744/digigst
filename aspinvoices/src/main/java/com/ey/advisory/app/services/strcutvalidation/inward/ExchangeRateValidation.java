package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class ExchangeRateValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		//if (!StringUtils.isNumeric(obj.toString().trim()) || obj.toString().trim().length() > 100) {
		if (!isDecimal(obj.toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EXCHANGE_RATE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10127",
					          "Invalid Exchange Rate.", location));
			return errors;
		}

		
		
		boolean isValid = NumberFomatUtil.is4And4digValidDec(obj.toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EXCHANGE_RATE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10127",
					          "Invalid Exchange Rate.", location));
			return errors;
		}
		obj=new BigDecimal(obj.toString()).setScale(4, BigDecimal.ROUND_HALF_EVEN);
		return errors;
	}

}
