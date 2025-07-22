package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AHsnMandatory")
public class Gstr1AHsnMandatory
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> DOC_TYPE = ImmutableList.of("ADV", "ADJ");

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String docType = document.getDocType();
		if (Strings.isEmpty(docType)
				|| DOC_TYPE.contains(docType.trim().toUpperCase()))
			return errors;
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			if (item.getHsnSac() == null || item.getHsnSac().isEmpty()) {

				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.HSN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10047",
						"HSNorSAC is mandatory.", location));

			}
		});
		return errors;
	}

}
