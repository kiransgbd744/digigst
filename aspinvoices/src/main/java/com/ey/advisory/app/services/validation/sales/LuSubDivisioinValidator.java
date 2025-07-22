package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DivisionInfoRepository;
import com.ey.advisory.app.data.repositories.client.PortCodeRepository;
import com.ey.advisory.app.data.repositories.client.SubDisivionInfoRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("LuSubDivisioinValidator")
public class LuSubDivisioinValidator implements 
DocRulesValidator<OutwardTransDocument>{

	@Autowired
	@Qualifier("SubDisivionInfoRepository")
	private SubDisivionInfoRepository SubDisivionInfoRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		SubDisivionInfoRepository = StaticContextHolder.
				getBean("SubDisivionInfoRepository",SubDisivionInfoRepository.class);
		if(document.getSubDivision()!=null 
				&& !document.getSubDivision().isEmpty()){
		String s=document.getSubDivision();
		int n=SubDisivionInfoRepository.findSubDivision(s);
		if(n<=0){
		
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUB_DIVISION);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ERSBD",
					"Invalid SubDivision",
					location));
		}
		}
		return errors;
	}
}