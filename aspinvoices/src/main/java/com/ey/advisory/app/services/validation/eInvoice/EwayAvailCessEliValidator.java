/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class EwayAvailCessEliValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		if (!GSTConstants.SLF.equalsIgnoreCase(document.getDocType()))
			return errors;
		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()) {
				if (item.getEligibilityIndicator().equalsIgnoreCase("NO")) {
					BigDecimal availableCess = item.getAvailableCess();
					if (availableCess == null) {
						availableCess = BigDecimal.ZERO;
					}
					if (availableCess.compareTo(BigDecimal.ZERO) != 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.AvailableCess);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1109",
										"Available credit should be zero "
												+ "where Eligibility "
												+ "Indicator is 'NO'.",
										location));
					}
				}
			}
		});
		return errors;
	}

}
*/