package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.newPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
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
public class Gstr1ANewPos
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getNewPos() != null && !document.getNewPos().isEmpty()) {
			String s = document.getNewPos().trim();
			if (s.length() == 1) {
				s = GSTConstants.ZERO + s;
			}
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);
			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				errorLocations.add(newPOS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5019",
						"Invalid NewPOS", location));
			}
		}

		return errors;
	}

}
