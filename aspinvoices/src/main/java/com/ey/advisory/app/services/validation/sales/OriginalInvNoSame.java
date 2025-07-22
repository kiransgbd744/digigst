package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
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
@Component("OriginalInvNoSame")
public class OriginalInvNoSame implements DocRulesValidator<OutwardTransDocument> {

	private boolean isValidTax(OutwardTransDocLineItem firstType,
			OutwardTransDocLineItem curType) {
		
		
		if(isPresent(firstType.getOriginalInvoiceNumber()) 
				&& !isPresent(curType.getOriginalInvoiceNumber())){
			return false;
		}
		
		if(!isPresent(firstType.getOriginalInvoiceNumber()) 
				&& isPresent(curType.getOriginalInvoiceNumber())){
			return false;
		}
		
		if (firstType.getOriginalInvoiceNumber() != null
				&& !firstType.getOriginalInvoiceNumber().isEmpty()){
			if (!firstType.getOriginalInvoiceNumber()
					.equalsIgnoreCase(curType.getOriginalInvoiceNumber())) {
				return false;
			}
		}
		return true;

	}

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item  = items.get(0);

			if (!isValidTax(item, items.get(idx))) {
				errorLocations.add(GSTConstants.ORG_INV_NO);
				TransDocProcessingResultLoc location 
				                  = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER0040",
								" original Invoice Number should be same across "
										+ "all line items of a document.",
								location));
			}

		});

		return errors;
	}

}
