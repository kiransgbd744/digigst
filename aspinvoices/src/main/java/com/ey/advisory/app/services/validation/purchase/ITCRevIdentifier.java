package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class ITCRevIdentifier
		implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> ITC = ImmutableList.of(GSTConstants.T1,
			GSTConstants.T2, GSTConstants.T3, GSTConstants.T4);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> revIdSet = new HashSet<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			String revId = item.getItcReversalIdentifier();
			if (!Strings.isNullOrEmpty(revId)) {
				revIdSet.add(revId);
				if (!ITC.contains(
						item.getItcReversalIdentifier().toUpperCase())) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.ITC_REVERSAL_IDENTIFIER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1112",
							"Invalid ITC Reversal Indicator.", location));

				}
			}
		});
		if (revIdSet.size() > 2) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.ITC_REVERSAL_IDENTIFIER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN1112",
					"ITC Reversal Identifier provided in the first line "
					+ "item will be considered for reversal computation in GSTR-3B.",
					location));
		}
		return errors;
	}

}
