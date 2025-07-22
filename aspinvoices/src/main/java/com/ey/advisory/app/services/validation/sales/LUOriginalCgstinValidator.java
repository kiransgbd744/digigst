package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class LUOriginalCgstinValidator  implements 
DocRulesValidator<OutwardTransDocument>{

	
	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		StatecodeRepository = StaticContextHolder.
				getBean("StatecodeRepository",StatecodeRepository.class);
		if(document.getOrigCgstin()!=null 
				&& !document.getOrigCgstin().isEmpty()){
		String s=document.getOrigCgstin().substring(0, 2);
		int n=StatecodeRepository.findStateCode(s);
		if(n<=0){
		
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ORG_GSTIN);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER063",
					"Invalid Original Customer GSTIN",
					location));
		}
		}
		return errors;
	}
}