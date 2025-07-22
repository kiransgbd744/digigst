package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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

/**
 * @author Siva.Nandam
 *
 */
@Component("IsServiceValidation")
public class IsServiceValidation
		implements DocRulesValidator<OutwardTransDocument> {
	

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			String isService = item.getIsService();
			if(isService==null || isService.isEmpty()){
				isService=GSTConstants.N;
			}
			boolean valid = YorNFlagValidation
					.valid(isService);
			if(!valid){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IS_SERVICE);
				TransDocProcessingResultLoc location 
				      = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER10046",
						"Invalid IS Service Flag", location));
			
			}
			if (valid && item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1
					&& GSTConstants.SERVICES_CODE.equalsIgnoreCase(
							item.getHsnSac().substring(0, 2)) 
					&& GSTConstants.N.equalsIgnoreCase(isService)) {
				
					
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.IS_SERVICE);
					TransDocProcessingResultLoc location 
					      = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							"ER10046",
							"Invalid IS Service Flag", location));
				}
			if (valid && item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1
					&& !GSTConstants.SERVICES_CODE.equalsIgnoreCase(
							item.getHsnSac().substring(0, 2)) 
					&& GSTConstants.Y.equalsIgnoreCase(isService)) {
				
					
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.IS_SERVICE);
					TransDocProcessingResultLoc location 
					      = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							 "ER10046",
							"Invalid IS Service Flag", location));
				}	
				
			
		});
		return errors;
	}

}
