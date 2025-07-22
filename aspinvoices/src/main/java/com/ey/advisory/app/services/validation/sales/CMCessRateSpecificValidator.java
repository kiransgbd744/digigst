package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
@Component("CMCessRateSpecificValidator")
public class CMCessRateSpecificValidator implements 
DocRulesValidator<OutwardTransDocument>{
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<OutwardTransDocLineItem>  items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			BigDecimal hsnSacNo = item.getCessRateSpecific();
			
			if((BigDecimal.ZERO.compareTo(hsnSacNo) < 0)){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CESS_RATE_SPECIFIC);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER082",
						"Invalid Cess Rate",
						location));
		
			}
	});
		return errors;
}
}


