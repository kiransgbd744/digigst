package com.ey.advisory.app.services.strcutvalidation.common.credit.reversal;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentNoValidationRule implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		try {
			if (!isPresent(obj)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1041",
						"Document Number cannot be left blank", location));
				return errors;
			}

			if (obj.toString().length() > 16) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1042",
						"Invalid Document no", location));
				return errors;

			}

			String docNo = obj.toString().trim();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(docNo);
			if (!matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1042",
						"Invalid Document no", location));
				return errors;
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured: Document No:{}", obj.toString(),
					e);
		}
		return errors;
	}

}
