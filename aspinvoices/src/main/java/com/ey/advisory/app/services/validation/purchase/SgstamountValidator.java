package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CAN;
import static com.ey.advisory.common.GSTConstants.CR;
import static com.ey.advisory.common.GSTConstants.RCR;
import static com.ey.advisory.common.GSTConstants.RRFV;
import static com.ey.advisory.common.GSTConstants.sgstamount;

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

public class SgstamountValidator  
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
				if(item.getSgstAmount().compareTo(BigDecimal.ZERO) < 0){
					
			
			errorLocations.add(sgstamount);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER081",
					"Invalid SGST/UT GST Amount",
					location));
		}
		
	}
		}
	}
		});
		return errors;
	}

}

