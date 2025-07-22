package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

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

public class DocumentNumberValidator implements DocRulesValidator<InwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		Set<String> errorLocations = new HashSet<>();
		//String docType = document.getDocType();
		
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();

			if(document.getSupplyType()!=null 
					&& !document.getSupplyType().isEmpty()){

				/**
				 * This method first add the cgstAmount and sgstAmount and igst
				 * amount and check first intra state or inter state, the amount
				 * is not equal is 0 then throw an error
				 */
				if (((sgstAmount.compareTo(BigDecimal.ZERO) != 0)
						&& (cgstAmount.compareTo(BigDecimal.ZERO) != 0))
						&& (igstAmount.compareTo(BigDecimal.ZERO) != 0)) {

					errorLocations.add(CGST_AMOUNT);
					errorLocations.add(SGST_AMOUNT);
					errorLocations.add(IGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER224",
									"An invoice can either be intra-State or "
											+ "inter-State " + "Error",
									location));
				}

				/**
				 * This method checkes the cgstAmount and sgstAmount and the
				 * amount is not equal is 0 then throw an error
				 */

				else	if ((cgstAmount.compareTo(BigDecimal.ZERO) != 0
						&& igstAmount.compareTo(BigDecimal.ZERO) != 0)
						|| (sgstAmount.compareTo(BigDecimal.ZERO) != 0
								&& igstAmount
										.compareTo(BigDecimal.ZERO) != 0)) {
					errorLocations.add(CGST_AMOUNT);
					errorLocations.add(SGST_AMOUNT);

					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER224",
									"An invoice can either be intra-State or "
											+ "inter-State " + "Error",
									location));

				}
			}
					
		});
		
		

		return errors;

	}
}
