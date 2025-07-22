package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.commomSupplyIndicator;

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
import com.google.common.collect.ImmutableList;

public class CommonSupplyIndicator 
                             implements DocRulesValidator<InwardTransDocument>{
	private static final List<String> CommomSupplyIndicator = ImmutableList
			.of("Y","N","YL","NL");
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
	if(item.getCommonSupplyIndicator()!=null 
			&& !item.getCommonSupplyIndicator().isEmpty()){
		if(!CommomSupplyIndicator.contains(item.getCommonSupplyIndicator())){
			errorLocations.add(commomSupplyIndicator);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER105",
					"Invalid Common Supply Indicator",
					location));
		}
	}
		
		});
		return errors;
	}

}

