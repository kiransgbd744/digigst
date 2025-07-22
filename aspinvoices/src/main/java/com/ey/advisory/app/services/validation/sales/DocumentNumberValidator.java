package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.*;

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
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * This class is responsible to An invoice can either be intra-State or
 * inter-State
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_30
 * 
 * @author Murali.Singanamala
 *
 */
@Component("DocumentNumberValidator")
public class DocumentNumberValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		
		//String docType = document.getDocType();
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

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
					Set<String> errorLocations = new HashSet<>();
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
					Set<String> errorLocations = new HashSet<>();
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