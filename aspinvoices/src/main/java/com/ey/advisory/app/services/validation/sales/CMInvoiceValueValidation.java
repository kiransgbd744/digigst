/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class CMInvoiceValueValidation implements 
DocRulesValidator<OutwardTransDocument>{



	private static final List<String> DOCTYPE_REQUIRING_IMPORTS = 
			ImmutableList.of("CR", "RFV","RCR");
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
List<OutwardTransDocLineItem>  items = document.getLineItems();
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			String hsnSacNo = item.getHsnSac();
			String uom=item.get;
			
		
		
		
		
	
		  Matcher matcher = pattern.matcher(document.getOrigDocNo());
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getOrigDocNo() != null && !document.getOrigDocNo().isEmpty()) {
		if (ORGDOCNUM_REQUIRING_IMPORTS.contains(document.getDocType()))  {
			if(!matcher.matches()){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.INVOICE_VALUE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER084",
						"Invalid Invoice Value",
						location));
		}
			
				
			}
		
		
	}
		return errors;
}
}*/