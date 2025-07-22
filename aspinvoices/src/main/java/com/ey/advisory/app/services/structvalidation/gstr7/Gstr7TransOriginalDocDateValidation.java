package com.ey.advisory.app.services.structvalidation.gstr7;

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

import joptsimple.internal.Strings;

/**
 * 
 * @author Siva.Reddy
 *
 */
public class Gstr7TransOriginalDocDateValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			return errors;
		}

		String originalDocDate = obj != null ? obj.toString().trim() : "";

		if (!Strings.isNullOrEmpty(originalDocDate)) {
			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(obj.toString().trim());

			if (date == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63005",
						"Invalid Document Date.", location));
				return errors;
			}
		}
		return errors;
	}

}
