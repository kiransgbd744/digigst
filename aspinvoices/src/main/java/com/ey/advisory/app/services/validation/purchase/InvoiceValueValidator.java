package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CAN;
import static com.ey.advisory.common.GSTConstants.CR;
import static com.ey.advisory.common.GSTConstants.INVOICE_VALUE;
import static com.ey.advisory.common.GSTConstants.RCR;
import static com.ey.advisory.common.GSTConstants.RRFV;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class InvoiceValueValidator  
                          implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
            ImmutableList.of(CR,RCR,GSTConstants.RFV,RRFV);
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
	if(document.getDocType()!=null && !document.getDocType().isEmpty()){
		if(document.getSupplyType()!=null 
				&& !document.getSupplyType().isEmpty()){
			if((!ORGDOCNUM_REQUIRING_IMPORTS.contains(document.getDocType())) 
					|| !CAN.equalsIgnoreCase(document.getSupplyType())){
			if(document.getDocAmount().compareTo(BigDecimal.ZERO) < 0){
					
			
			errorLocations.add(INVOICE_VALUE);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER084",
					"Invalid Invoice Value",
					location));
		}
		
	}
		}
	}
		});
		return errors;
	}

}






