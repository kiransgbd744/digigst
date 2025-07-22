/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Itc04JobWorkerGstinValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String jwGstin = document.getJobWorkerGstin();
		String jwStateCode = document.getJobWorkerStateCode();

		if ((jwGstin == null || jwGstin.isEmpty())
				&& (jwStateCode == null || jwStateCode.isEmpty())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.JOB_WORKER_GSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5822",
					"JobWorkerGSTIN or JobworkerStateCode either of details should be available.",
					location));
		}
		return errors;
	}
}
