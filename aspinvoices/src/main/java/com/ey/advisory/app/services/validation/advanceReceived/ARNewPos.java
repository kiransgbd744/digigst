package com.ey.advisory.app.services.validation.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.newPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class ARNewPos
		implements ARBusinessRuleValidator<Gstr1AsEnteredAREntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache; 

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getNewPos() != null && !document.getNewPos().isEmpty()) {
			String s = document.getNewPos().trim();
			if(s.length()==1){
				s=GSTConstants.ZERO+s;
			}
			stateCache = StaticContextHolder
					.getBean("DefaultStateCache", StateCache.class);
			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(newPOS);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5114",
						"Invalid New POS", location));
				return errors;
			}
		}

		if (document.getNewPos() != null && !document.getNewPos().isEmpty()) {
			if (document.getOrgPos() != null
					&& !document.getOrgPos().isEmpty()) {
				if (!(document.getOrgPos().trim()
						.equalsIgnoreCase(document.getNewPos().trim()))) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(newPOS);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5115",
							"New POS Cannot be different from OrgPOS",
							location));
					return errors;
				}
			}
		}
		return errors;
	}

}
