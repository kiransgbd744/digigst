package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class POSValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	/*@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;*/
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		/*StatecodeRepository = StaticContextHolder.getBean("StatecodeRepository",
				StatecodeRepository.class);*/
		if (document.getPos() != null && !document.getPos().isEmpty()) {
			/*String s = document.getPos();
			int n = StatecodeRepository.findStateCode(s);*/
			String stateCode=document.getPos();
			if(stateCode.length()==1){
				stateCode=GSTConstants.ZERO+stateCode;
			}
			stateCache = StaticContextHolder.
					getBean("DefaultStateCache",StateCache.class);
			int n=stateCache.findStateCode(stateCode);

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.POS);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1218",
						"Invalid POS", location));
			}
		}
		return errors;
	}
}
