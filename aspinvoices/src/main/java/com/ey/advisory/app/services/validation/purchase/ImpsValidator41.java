package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SUPPLY_TYPE;
import static com.ey.advisory.common.GSTConstants.cgst;
import static com.ey.advisory.common.GSTConstants.sgst;

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

public class ImpsValidator41 implements DocRulesValidator<InwardTransDocument>{

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
	if(GSTConstants.IMPS.equalsIgnoreCase(document.getSupplyType())){
			if(item.getCgstAmount().compareTo(BigDecimal.ZERO) != 0 
				|| item.getSgstAmount().compareTo(BigDecimal.ZERO) != 0 
				
					 ){
				
			errorLocations.add(SUPPLY_TYPE);
			errorLocations.add(cgst);
			errorLocations.add(sgst);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER408",
					"In case of Imports, CGST and SGST cannot be applied",
					location));	
		}
	}
	}
		});
		
		return errors;
	}

}
