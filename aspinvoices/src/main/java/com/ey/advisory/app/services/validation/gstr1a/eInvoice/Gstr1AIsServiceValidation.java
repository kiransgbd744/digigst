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
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AIsServiceValidation")
public class Gstr1AIsServiceValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);
			String isService = item.getIsService();
			if (isService == null || isService.isEmpty()) {
				isService = GSTConstants.N;
			}
			boolean valid = Gstr1AYorNFlagValidation.valid(isService);
			if (!valid) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IS_SERVICE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10046",
						"Invalid IS Service Flag", location));

			}
			if (valid && item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1
					&& GSTConstants.SERVICES_CODE
							.equalsIgnoreCase(item.getHsnSac().substring(0, 2))
					&& GSTConstants.N.equalsIgnoreCase(isService)) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IS_SERVICE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10046",
						"Invalid IS Service Flag", location));
			}
			if (valid && item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1
					&& !GSTConstants.SERVICES_CODE
							.equalsIgnoreCase(item.getHsnSac().substring(0, 2))
					&& GSTConstants.Y.equalsIgnoreCase(isService)) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IS_SERVICE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10046",
						"Invalid IS Service Flag", location));
			}

		});
		return errors;
	}

}
