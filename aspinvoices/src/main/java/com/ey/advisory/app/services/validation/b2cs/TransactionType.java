package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.TransactionType;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class TransactionType  implements 
B2csBusinessRuleValidator<Gstr1B2csDetailsEntity> {
	private static final List<String> Trans_TYPE_IMPORTS = 
			ImmutableList.of("L","Z","ZL");
	@Override
	public List<ProcessingResult> validate(Gstr1B2csDetailsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getTransType()!=null && !document.getTransType().isEmpty()){
			if(!Trans_TYPE_IMPORTS.contains(document.getTransType())){
				errorLocations.add(TransactionType);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ERXXX",
						"Invalid Transaction Type",
						location));
			}
		}
		
		return errors;
	}

}
