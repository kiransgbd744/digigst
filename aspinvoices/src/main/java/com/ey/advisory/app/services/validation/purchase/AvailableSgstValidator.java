package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class AvailableSgstValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);

			BigDecimal asgst = item.getAvailableSgst();
			BigDecimal sgst = item.getSgstAmount();
			if (asgst == null) {
				asgst = BigDecimal.ZERO;
			}
			if (sgst == null) {
				sgst = BigDecimal.ZERO;
			}
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()
					&& !GSTConstants.NO
							.equalsIgnoreCase(item.getEligibilityIndicator())) {
				if (asgst.compareTo(sgst) > 0) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AvailableSgstamount);
					TransDocProcessingResultLoc location 
					            = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1108",
							"Available credit cannot be more than Tax Payable.",
							location));
				}
			}
		});
		return errors;
	}

}
