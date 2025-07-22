/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.Itc04JwTypeCache;
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
@Component("Itc04JwTypeMasterValidation")
public class Itc04JwTypeMasterValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("DefaultItc04JwTypeCache")
	private Itc04JwTypeCache itc04JwTypeCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		if (document.getJobWorkerType() != null
				&& !document.getJobWorkerType().isEmpty()) {

			itc04JwTypeCache = StaticContextHolder
					.getBean("DefaultItc04JwTypeCache", Itc04JwTypeCache.class);
			int n = itc04JwTypeCache.findJwType(
					trimAndConvToUpperCase(document.getJobWorkerType()));
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.JOB_WORKER_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5857",
						"Invalid JobWorkerType", location));
			}
		}
		if (document.getJobWorkerGstin() == null
				|| document.getJobWorkerGstin().isEmpty()) {
			if ("S".equalsIgnoreCase(document.getJobWorkerType())) {
				
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.JOB_WORKER_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5858",
						"Job work type cannot be S (SEZ) when JobWorkGSTIN is Blank",
						location));
			}
		}

		return errors;
	}

}