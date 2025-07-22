package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class DeliveryChallanNumberValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			return errors;
		}
		if (obj.toString().trim().length() > 16) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DELIVERY_CHALLAN_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5810",
					"Invalid DeliveryChallanNumber.", location));
			return errors;
		}

		String deliveryChallanNo = obj.toString().trim();
		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(deliveryChallanNo);
		if (!matcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DELIVERY_CHALLAN_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5810",
					"Invalid DeliveryChallanNumber.", location));
			return errors;
		}

		return errors;
	}
}
