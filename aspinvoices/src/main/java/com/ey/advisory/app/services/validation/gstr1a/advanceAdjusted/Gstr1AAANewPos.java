package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.newPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AAANewPos implements
		B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getNewPOS() != null && !document.getNewPOS().isEmpty()) {
			String s = document.getNewPOS().trim();
			if (s.length() == 1) {
				s = GSTConstants.ZERO + s;
			}
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);

			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(newPOS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5164",
						"Invalid New POS", location));
				return errors;
			}
		}

		if (document.getNewPOS() != null && !document.getNewPOS().isEmpty()) {
			if (document.getOrgPOS() != null
					&& !document.getOrgPOS().isEmpty()) {
				if (!(document.getOrgPOS().trim()
						.equalsIgnoreCase(document.getNewPOS().trim()))) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(newPOS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5165",
							"New POS Cannot be different from OrgPOS",
							location));
					return errors;
				}
			}
		}
		return errors;
	}

}
