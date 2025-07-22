package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * This class is responsible to CGST and SGST Amounts are Different
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_72
 * 
 * @author Murali.Singanamala
 *
 */

@Component("TaxAmountValidator")
public class TaxAmountValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> result = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> infoLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal taxAmount = new BigDecimal(0);
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();

			taxAmount = (cgstAmount.compareTo(sgstAmount) > 0)
					? cgstAmount.subtract(sgstAmount).abs()
					: sgstAmount.subtract(cgstAmount).abs();

			/**
			 * This method first add the cgstAmount and sgstAmount and compare
			 * the both the amount is greater than 0 and then it get an
			 * information message
			 * 
			 */

			if (taxAmount.compareTo(BigDecimal.ONE) >= 0) {
				infoLocations.add(CGST_AMOUNT);
				infoLocations.add(SGST_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, infoLocations.toArray());
				result.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN212",
						"CGST and SGST amounts cannot be different", location));
			}

		});

		return result;
	}
}
