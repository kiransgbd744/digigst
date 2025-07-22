package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CustomerRgstin
implements BusinessRuleValidator<MasterCustomerEntity> {

	@Override
	public List<ProcessingResult> validate(MasterCustomerEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		String outSideIndia = document.getOutSideIndia();
		String recipientGstnOrPan = document.getRecipientGstnOrPan();
		
		if(outSideIndia != null && GSTConstants.Y
				.equalsIgnoreCase(outSideIndia)){
			if(recipientGstnOrPan != null && !recipientGstnOrPan.isEmpty()){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1605",
						"Recipient GSTIN/PAN should be blank.",
						location));
				return errors;
			}
		}
		if(outSideIndia != null && 
				GSTConstants.N.equalsIgnoreCase(outSideIndia)){
			if(recipientGstnOrPan == null || recipientGstnOrPan.isEmpty()){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1606",
						"Recipient GSTIN/PAN cannot be left blank.",
						location));
				return errors;
			}
			
		}
		
		return errors;
	}

}
