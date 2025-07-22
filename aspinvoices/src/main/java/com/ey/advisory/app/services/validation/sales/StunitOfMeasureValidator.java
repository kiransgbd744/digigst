package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class StunitOfMeasureValidator 
implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem>  items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
if(item.getUom()!=null){
			String uom=item.getUom();
		
		String regex= "^[a-zA-Z-]*$";
		
		
		Pattern pattern = Pattern.compile(regex);
	
		  Matcher matcher = pattern.matcher(uom);
		  if(!matcher.matches()){
			  Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = 
						   new TransDocProcessingResultLoc(null,
								   errorLocations.toArray());
			
				errors.add(new ProcessingResult(APP_VALIDATION, "ER073", 
						"Invalid Unit of Measurement",
						location));	  
		  }
		  }
		});
		
		
		return errors;
	} 


}
