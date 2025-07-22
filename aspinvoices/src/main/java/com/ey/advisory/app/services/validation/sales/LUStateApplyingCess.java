package com.ey.advisory.app.services.validation.sales;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
@Component("LUStateApplyingCess")
public class LUStateApplyingCess implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;
	
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		
		if(document.getStateApplyingCess()!=null 
				&& !document.getStateApplyingCess().isEmpty()){
		String stateApplyingCess=document.getStateApplyingCess();
		stateCache = StaticContextHolder.
				getBean("DefaultStateCache",StateCache.class);
		int n=stateCache.findStateCode(stateApplyingCess);
		if(n<=0){
		
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.STATEAPPLYINGCESS);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0058",
					"Invalid State Applying Cess (State Code)",
					location));
			}
		}
		return errors;
	}
}
