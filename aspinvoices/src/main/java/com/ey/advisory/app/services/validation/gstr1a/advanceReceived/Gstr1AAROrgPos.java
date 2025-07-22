package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.OrgPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AAROrgPos
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getOrgPos() != null && !document.getOrgPos().isEmpty()) {
			String s = document.getOrgPos().trim();
			if (s.length() == 1) {
				s = GSTConstants.ZERO + s;
			}
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);
			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(OrgPOS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5107",
						"Invalid Org POS", location));
				return errors;
			}
		}

		if (document.getOrgPos() != null && !document.getOrgPos().isEmpty()) {
			if (document.getNewPos() != null
					&& !document.getNewPos().isEmpty()) {

				if (!document.getOrgPos().trim()
						.equalsIgnoreCase(document.getNewPos().trim())) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(OrgPOS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5108",
							"Invalid OrgPOS.", location));
					return errors;

				}
			}
		}
		return errors;
	}

}
