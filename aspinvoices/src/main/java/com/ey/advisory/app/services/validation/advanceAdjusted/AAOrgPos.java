package com.ey.advisory.app.services.validation.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.OrgPOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
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
public class AAOrgPos implements
		B2csBusinessRuleValidator<Gstr1AsEnteredTxpdFileUploadEntity> {
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;
 
	@Override
	public List<ProcessingResult> validate(
			Gstr1AsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getOrgPOS() != null && !document.getOrgPOS().isEmpty()) {
			String s = document.getOrgPOS().trim();
			if(s.length()==1){
				s=GSTConstants.ZERO+s;
			}
			stateCache = StaticContextHolder
					.getBean("DefaultStateCache", StateCache.class);
			int n = stateCache.findStateCode(s);
			if (n <= 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(OrgPOS);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5157",
						"Invalid Org POS", location));
				return errors;
			}
		}

		if (document.getOrgPOS() != null && !document.getOrgPOS().isEmpty()) {
			if (document.getNewPOS() != null
					&& !document.getNewPOS().isEmpty()) {
				if (!document.getOrgPOS().trim()
						.equalsIgnoreCase(document.getNewPOS().trim())) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(OrgPOS);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5157",
							"OrgPOS should be same as New Pos.", location));
					return errors;

				}
			}
		}
		return errors;
	}

}
