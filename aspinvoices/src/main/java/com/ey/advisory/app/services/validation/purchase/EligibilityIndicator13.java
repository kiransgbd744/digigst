package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.NO;
import static com.ey.advisory.common.GSTConstants.eligibiltIndicator;

import java.math.BigDecimal;
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

public class EligibilityIndicator13 implements DocRulesValidator<InwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
	if(item.getEligibilityIndicator()!=null 
			&& !item.getEligibilityIndicator().isEmpty()){
		if(!NO.equalsIgnoreCase(item.getEligibilityIndicator())){
			if(item.getAvailableIgst().compareTo(BigDecimal.ZERO) == 0 
					|| item.getAvailableCgst().compareTo(BigDecimal.ZERO) == 0 
					|| item.getAvailableSgst().compareTo(BigDecimal.ZERO) == 0 
					|| item.getCessAmount().compareTo(BigDecimal.ZERO) == 0 ){
			errorLocations.add(eligibiltIndicator);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER403",
					"Available ITC amount is not as per Eligibility Indicator",
					location));	
		}
		
	}
		if(NO.equalsIgnoreCase(item.getEligibilityIndicator())){
			if(item.getAvailableIgst().compareTo(BigDecimal.ZERO) != 0 
					|| item.getAvailableCgst().compareTo(BigDecimal.ZERO) != 0 
					|| item.getAvailableSgst().compareTo(BigDecimal.ZERO) != 0 
					|| item.getCessAmount().compareTo(BigDecimal.ZERO) != 0 ){
			errorLocations.add(eligibiltIndicator);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER403",
					"Available ITC amount is not as per Eligibility Indicator",
					location));	
		}	
		}
	}
		});
		return errors;
	}

}