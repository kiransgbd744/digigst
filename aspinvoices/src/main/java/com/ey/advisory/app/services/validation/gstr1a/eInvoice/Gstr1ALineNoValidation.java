package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ALineNoValidation")
public class Gstr1ALineNoValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		Set<String> set = new HashSet<>();
		IntStream.range(0, items.size()).forEach(idx -> {

			Gstr1AOutwardTransDocLineItem item = items.get(idx);
			String lineNo = item.getLineNo();
			if (lineNo == null) {
				errorLocations.add(GSTConstants.LINE_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER0046",
						"Line number Cannot be Empty", location));
				return;
			}

			if (!set.add(item.getLineNo())) {
				errorLocations.add(GSTConstants.LINE_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER0514",
						"Line number in a document cannot be repeated",
						location));

			}

		});

		return errors;
	}

}
