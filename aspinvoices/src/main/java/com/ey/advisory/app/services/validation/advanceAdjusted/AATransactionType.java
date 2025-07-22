package com.ey.advisory.app.services.validation.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.TransactionType;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AdvanceAdjustmentFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class AATransactionType implements
		B2csBusinessRuleValidator<Gstr1AdvanceAdjustmentFileUploadEntity> {
	private static final List<String> Trans_TYPE_IMPORTS = 
			ImmutableList.of("L","Z","ZL");
	@Override
	public List<ProcessingResult> validate(
			Gstr1AdvanceAdjustmentFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getTransactionType()!=null && !document.getTransactionType().isEmpty()){
			if(!Trans_TYPE_IMPORTS.contains(document.getTransactionType())){
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



