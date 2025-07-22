package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class OriginalDocNum implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.CR, GSTConstants.DR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (DOCTYPE
					.contains(trimAndConvToUpperCase(document.getDocType()))) {
				IntStream.range(0, items.size()).forEach(idx -> {
					OutwardTransDocLineItem item = items.get(idx);

					if (item.getOrigDocNo() == null
							|| item.getOrigDocNo().isEmpty()) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, " ER10096",
										"Original Document Number cannot be left..",
										location));
					}
				});
			}
		}
		return errors;
	}

}
