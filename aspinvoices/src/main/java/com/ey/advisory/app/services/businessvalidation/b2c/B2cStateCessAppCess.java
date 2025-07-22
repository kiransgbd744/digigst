package com.ey.advisory.app.services.businessvalidation.b2c;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.STATEAPPLYINGCESS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cStateCessAppCess
		implements BusinessRuleValidator<OutwardB2cExcelEntity> {
/*	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;*/
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {

		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (document.getStateApplyCess() != null
				&& !document.getStateApplyCess().trim().isEmpty()) {
			String s = document.getStateApplyCess().trim();

			if (s != null && !s.isEmpty()) {
				/*StatecodeRepository = StaticContextHolder.getBean(
						"StatecodeRepository", StatecodeRepository.class);

				List<String> pos = StatecodeRepository.getStateCode(s);*/
				String stateCode=document.getPos();
				if(stateCode.length()==1){
					stateCode=GSTConstants.ZERO+stateCode;
				}
				stateCache = StaticContextHolder.
						getBean("DefaultStateCache",StateCache.class);
				int n=stateCache.findStateCode(stateCode);
				
				if (n <= 0 ) {
					errorLocations.add(STATEAPPLYINGCESS);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0217",
							"Invalid State Applying Cess (State Code)",
							location));
				}
			}
		}
		return errors;
	}
}
