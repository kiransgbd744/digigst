package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class CMStateUTTaxAmountValidator implements 
DocRulesValidator<OutwardTransDocument>{
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
			ImmutableList.of(GSTConstants.CR, 
					GSTConstants.RFV,GSTConstants.RCR);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<OutwardTransDocLineItem>  items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
		if(!ORGDOCNUM_REQUIRING_IMPORTS.contains(document.getDocType()) && 
				!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType()) ){
			if(item.getSgstAmount()!=null){
			if((BigDecimal.ZERO.compareTo(item.getSgstAmount()) < 0)){
		
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.STATE_UTTAX_AMOUNT);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
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

