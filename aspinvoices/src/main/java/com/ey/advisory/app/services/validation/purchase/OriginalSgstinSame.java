package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.OrgSgstin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class OriginalSgstinSame implements DocRulesValidator<InwardTransDocument> {
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
	if(document.getOrigSgstin()!=null && !document.getOrigSgstin().isEmpty()){
			if(document.getOrigSgstin()==document.getCgstin() )	{	
			
			errorLocations.add(OrgSgstin);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER128",
					"Original Supplier GSTIN cannot be same as Customer GSTIN",
					location));
		
			}
			}
		}
		return errors;
	}

}


