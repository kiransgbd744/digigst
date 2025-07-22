/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;


@Component("OriginalDocnumValidation")
public class OriginalDocnumValidation implements 
	DocRulesValidator<OutwardTransDocument>{
	
	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS = 
			ImmutableList.of("RNV", "CR","DR","RCR","RDR","RFV");

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		List<ProcessingResult> errors = new ArrayList<>();
		String docType=document.getDocType();
		String docNo=document.getDocNo();
		List<OutwardTransDocLineItem>  items = document.getLineItems();
		
		IntStream.range(0, items.size()).forEach(idx -> {
		
		//	String OriginalDocNo=items.get(idx).getDocument().getOrigDocNo();
		
		if(DOC_TYPES_REQUIRING_IMPORTS.contains(docType))
		{
			if(docNo != items.get(idx).getDocument().getOrigDocNo()){
				
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = 
						   new TransDocProcessingResultLoc(null, 
								   errorLocations.toArray());
			
				errors.add(new ProcessingResult(APP_VALIDATION, "ER057",
						"Invalid Original Document Number or "
						+ "Original Document Number is missing",
						location));			
			}	
		}
				
	});
		return errors;
	}

}
*/