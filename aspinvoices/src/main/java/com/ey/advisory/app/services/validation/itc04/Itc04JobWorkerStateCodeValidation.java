/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Itc04JobWorkerStateCodeValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String jwGstin = document.getJobWorkerGstin();
		String jwStateCode = document.getJobWorkerStateCode();

		if (document.getJobWorkerStateCode() != null
				&& !document.getJobWorkerStateCode().isEmpty()) {
			
			String stateCode = document.getJobWorkerStateCode();
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);
			int n = stateCache.findStateCode(stateCode);
			if (n <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.JOB_WORKER_STATE_CODE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5823",
						"Invalid JobWorkerStateCode", location));
			}
		}
		if ((jwGstin == null || jwGstin.isEmpty())
				&& (jwStateCode == null || jwStateCode.isEmpty())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.JOB_WORKER_STATE_CODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5824",
					"JobWorkerGSTIN or JobworkerStateCode either of details should be available.",
					location));
		}
		return errors;
	}
}
