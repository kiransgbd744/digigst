package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.eligibiltIndicator;

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

/**
 * 
 * @author Siva.Nandam
 *
 */
public class HsnEligibilityIndicator
		implements DocRulesValidator<InwardTransDocument> {
/*
	private static final List<String> EligibilityIndicator = ImmutableList
			.of("IG", "CG");
*/
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()) {
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
					if (item.getHsnSac().length() > 1) {
						String hsn = item.getHsnSac().substring(0, 2);
						String ei = item.getEligibilityIndicator();
						/*if ("99".equalsIgnoreCase(hsn) && EligibilityIndicator
								.contains(trimAndConvToUpperCase(ei))) {
							errorLocations.add(eligibiltIndicator);
							TransDocProcessingResultLoc location 
							    = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1101", "Invalid Eligibility Indicator.",
									location));
						}*/
						if (!"99".equalsIgnoreCase(hsn)
								&& "IS".equalsIgnoreCase(ei)) {
							errorLocations.add(eligibiltIndicator);
							TransDocProcessingResultLoc location 
							 = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1101", "Invalid Eligibility Indicator.",
									location));
						}
					}
				}
			}

		});
		return errors;
	}

}