package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Balakrishna.S
 *
 */

public class LuSupplierGstin implements DocRulesValidator<InwardTransDocument>{

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;
	
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		StatecodeRepository = StaticContextHolder.
				getBean("StatecodeRepository",StatecodeRepository.class);
		if(document.getSgstin()!=null 
				&& !document.getSgstin().isEmpty()){
		String s=document.getSgstin().substring(0,1);
		
		int n=StatecodeRepository.findStateCode(s);
		if(n<=0){
		
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER052",
					"Invalid Supplier GSTIN ",
					location));
		}
		}
		return errors;
	}
		
	
	

}
