package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.*;

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
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class SezValidator71 implements DocRulesValidator<InwardTransDocument>{
	
	private static final List<String> Supply_Type_IMPORTS = ImmutableList
			.of(SEZG,SEZS,DTA);
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			if(document.getSupplyType()!=null 
					&& !document.getSupplyType().isEmpty()){
	if(Supply_Type_IMPORTS.contains(document.getSupplyType())){
			if(item.getCgstAmount().compareTo(BigDecimal.ZERO) != 0 
				|| item.getSgstAmount().compareTo(BigDecimal.ZERO) != 0 
				
					 ){
				
			errorLocations.add(SUPPLY_TYPE);
			errorLocations.add(cgst);
			errorLocations.add(sgst);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN310",
					"In case of SEZ/DTA IGST should be charged.",
					location));	
		}
		
	}
			}
		});
		
		return errors;
	}

}

