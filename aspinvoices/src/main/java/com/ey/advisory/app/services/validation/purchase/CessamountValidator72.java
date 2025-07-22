package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.cess;
import static com.ey.advisory.common.GSTConstants.cgst;
import static com.ey.advisory.common.GSTConstants.igst;
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
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CessamountValidator72 implements DocRulesValidator<InwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
	
			if(item.getCessAmount().compareTo(BigDecimal.ZERO) != 0 ){
				if(item.getIgstAmount().compareTo(BigDecimal.ZERO) == 0
					&& item.getCgstAmount().compareTo(BigDecimal.ZERO) == 0 
					&& item.getSgstAmount().compareTo(BigDecimal.ZERO) == 0){
				errorLocations.add(cgst);
				errorLocations.add(sgst);
				errorLocations.add(igst);
				errorLocations.add(cess);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					"ER470",
					"Tax amount cannot be blank in case value "
					+ "in Cess amount in available",
					location));	
		
				}
	}
		});
		return errors;
	}



}
