package com.ey.advisory.app.services.validation.sales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
@Component("CMCustomerGstnValidator")
public class CMCustomerGstnValidator implements 
DocRulesValidator<OutwardTransDocument>{
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
			ImmutableList.of(GSTConstants.SEZ,GSTConstants.DXP);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		if(document.getSupplyType()!=null 
				                    && !document.getSupplyType().isEmpty()){
		if (ORGDOCNUM_REQUIRING_IMPORTS.contains(
				trimAndConvToUpperCase(document.getSupplyType())))  {
	if ((!(document.getCgstin()!= null)) || document.getCgstin().isEmpty()) {
			
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CGSTN);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER061",
						"Invalid Customer GSTIN",
						location));
				
		}
			
				
			}
		
		
	}
		return errors;
}
}
