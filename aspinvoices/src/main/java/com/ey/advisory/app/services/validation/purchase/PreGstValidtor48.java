/*package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_NO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class PreGstValidtor48 implements DocRulesValidator<InwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(document.getCrDrPreGST()!=null 
				&& !document.getCrDrPreGST().isEmpty()){
		if(document.getCrDrPreGST().equalsIgnoreCase(GSTConstants.Y)){
			if(document.getOrigDocDate() == null 
					|| document.getOrigDocNo() == null 
					|| document.getOrigDocNo().isEmpty()){
				errorLocations.add(ORIGINAL_DOC_NO);
				TransDocProcessingResultLoc location = 
		                  new TransDocProcessingResultLoc(
		null, errorLocations.toArray());
errors.add(new ProcessingResult(APP_VALIDATION, "ER057",
		"Invalid Original Document Number "
		+ "or Original Document Number is missing",
		location));	
}
			}
		}
		
		return errors;
	}

}*/