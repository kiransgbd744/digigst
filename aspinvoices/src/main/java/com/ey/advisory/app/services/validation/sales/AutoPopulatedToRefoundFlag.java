package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
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
@Component("AutoPopulatedToRefoundFlag")
public class AutoPopulatedToRefoundFlag 
              implements DocRulesValidator<OutwardTransDocument> {
	private boolean isValidTax(OutwardTransDocLineItem firstType,
			OutwardTransDocLineItem curType) {
		
		

		
		String firstTypeAutoPopulate=firstType.getAutoPopToRefundFlag();
		String curTypeAutoPopulate=curType.getAutoPopToRefundFlag();
		
		if(firstTypeAutoPopulate==null || firstTypeAutoPopulate.isEmpty()){
			firstTypeAutoPopulate=GSTConstants.N;
		}
		if(curTypeAutoPopulate==null || curTypeAutoPopulate.isEmpty()){
			curTypeAutoPopulate=GSTConstants.N;
		}
		
		if(!curTypeAutoPopulate.equalsIgnoreCase(firstTypeAutoPopulate)){
			return false;
		}
		return true;

	}
	
	@Override
	public List<ProcessingResult> 
	    validate(OutwardTransDocument document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getDocType()==null 
				|| document.getDocType().isEmpty()) return errors;
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item  = items.get(0);

			if (!isValidTax(item, items.get(idx))) {
				errorLocations.add(GSTConstants.AUTO_POPULATETO_REFUND);
				TransDocProcessingResultLoc location 
				                  = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER0106",
								"Auto Populate to Refund Flag should be "
								+ "same across all line items of a document.",
								location));
			}

		});

		return errors;
	}

}
