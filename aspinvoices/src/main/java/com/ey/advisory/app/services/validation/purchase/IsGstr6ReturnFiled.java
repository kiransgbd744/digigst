package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Ravindra V S
 *
 */
public class IsGstr6ReturnFiled
		implements DocRulesValidator<InwardTransDocument> {
	private static final String PIPE = "|";

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String gstin = document.getCgstin();
		String taxPeriod = document.getTaxperiod();

		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + PIPE + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
					"GSTR6 for selected tax period  is already filed",
					location));
			document.setDeleted(true);
			document.setIsError(true);
			document.setErrCodes("ER1276");
		}

		return errors;
	}

}
