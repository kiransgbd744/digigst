/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.eligibiltIndicator;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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
import com.google.common.collect.ImmutableList;

*//**
 * @author Siva.Nandam
 *
 *//*
public class HsnEligibilityIndicatorValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> EligibilityIndicator = ImmutableList
			.of(GSTConstants.IG, GSTConstants.CG);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (!GSTConstants.SLF.equalsIgnoreCase(document.getDocType()))
			return errors;
		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()) {
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
					if (item.getHsnSac().length() > 1) {
						String hsn = item.getHsnSac().substring(0, 2);
						String ei = item.getEligibilityIndicator();
						if (GSTConstants.SERVICES_CODE.equalsIgnoreCase(hsn)
								&& EligibilityIndicator
										.contains(trimAndConvToUpperCase(ei))) {
							errorLocations.add(eligibiltIndicator);
							TransDocProcessingResultLoc location 
							          = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1101", "Invalid Eligibility Indicator.",
									location));
						}
						if (!GSTConstants.SERVICES_CODE.equalsIgnoreCase(hsn)
								&& GSTConstants.IS.equalsIgnoreCase(ei)) {
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
*/