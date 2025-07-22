package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
public class ServiceEligibilityValidator
		implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> ELIGIBILITY_IND = ImmutableList
			.of(GSTConstants.IS, GSTConstants.NO);

	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			String isService = item.getIsService();
			String hsnSac = item.getHsnSac();
			String eligibilityInd = item.getEligibilityIndicator();
			if ((!Strings.isNullOrEmpty(hsnSac))
					&& (!Strings.isNullOrEmpty(eligibilityInd))
					&&GSTConstants.Y.equalsIgnoreCase(isService)
					 && hsnSac.length() > 1
					&& GSTConstants.SERVICES_CODE
							.equalsIgnoreCase(hsnSac.substring(0, 2))
					&& (!ELIGIBILITY_IND.contains(trimAndConvToUpperCase(eligibilityInd)))) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1274",
						"Eligibility Indicator should be IS/NO for input services", location));

			}

		});

		return errors;
	}

}
