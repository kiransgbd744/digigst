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
public class AvailableCessValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);

			BigDecimal cessAmountadv = item.getCessAmountAdvalorem();
			if (cessAmountadv == null) {
				cessAmountadv = BigDecimal.ZERO;
			}
			BigDecimal cessAmountspe = item.getCessAmountSpecific();
			if (cessAmountspe == null) {
				cessAmountspe = BigDecimal.ZERO;
			}
			BigDecimal cessAmount = cessAmountadv.add(cessAmountspe);
			BigDecimal avaliableCess = item.getAvailableCess();
			if (avaliableCess == null) {
				avaliableCess = BigDecimal.ZERO;
			}
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()
					&& !GSTConstants.NO
							.equalsIgnoreCase(item.getEligibilityIndicator())) {
				if (avaliableCess.compareTo(cessAmount) > 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AvailableCess);
					TransDocProcessingResultLoc location 
					               = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1110",
							"Available credit cannot be more than Tax Payable.",
							location));
				}
			}
		});
		return errors;
	}

}
