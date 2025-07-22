package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.OrgPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class OrgPos
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache; 

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getOrgPos() != null && !document.getOrgPos().isEmpty()) {
			String s = document.getOrgPos().trim();
			if(s.length()==1){
				s=GSTConstants.ZERO+s;
			}
			stateCache = StaticContextHolder
					.getBean("DefaultStateCache", StateCache.class);
			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				errorLocations.add(OrgPOS);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5011",
						"Invalid OrgPOS.", location));
				return errors;
			}
		}

		return errors;
	}

}
