package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6StateCodeValidator
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getStateCode() != null
				&& !document.getStateCode().isEmpty()) {
			String stateCode = document.getStateCode();
			if (stateCode.length() == 1) {
				stateCode = GSTConstants.ZERO + stateCode;
			}
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);
			int n = stateCache.findStateCode(stateCode);
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.POS);
				TransDocProcessingResultLoc location 
				         = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3010",
						"Invalid State Code", location));
			}
		}
		return errors;
	}
}
