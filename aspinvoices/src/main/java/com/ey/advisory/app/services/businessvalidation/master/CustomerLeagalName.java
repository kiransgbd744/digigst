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

public class CustomerLeagalName implements BusinessRuleValidator<MasterCustomerEntity>
		 {

	@Override
	public List<ProcessingResult> validate(MasterCustomerEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String outSideIndia = document.getOutSideIndia();
		String legalName = document.getLegalName();
		
		if(outSideIndia != null && 
				GSTConstants.Y.equalsIgnoreCase(outSideIndia)){
			if(legalName == null || legalName.isEmpty()){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.LEGALNAME);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1607",
						"Legal Name cannot be left blank.",
						location));
				return errors;
			}
			
		}
		
		return errors;
	}

}
