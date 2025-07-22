package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.NO;
import static com.ey.advisory.common.GSTConstants.eligibiltIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class ITCClaimValidator implements DocRulesValidator<InwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
				String	firstTwoDigitsCgstin = document.getCgstin().substring(0, 2);
				if(document.getPos()!=null && !document.getPos().isEmpty()){
					 if (!firstTwoDigitsCgstin.equalsIgnoreCase(document.getPos())){
	if(item.getEligibilityIndicator()!=null 
			                   && !item.getEligibilityIndicator().isEmpty()){
		if(!NO.equalsIgnoreCase(item.getEligibilityIndicator())){
			errorLocations.add(eligibiltIndicator);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER474",
					"ITC cannot be claime as POS and "
					+ "Recipient State code are diffferent.",
					location));
		}
	}
					 }
				}
			}	
		});
		return errors;
	}

}
