package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

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

/**
 * @author Siva.Nandam
 *
 */
public class HsnValidation implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			if (GSTConstants.Y.equalsIgnoreCase(item.getIsService())) {
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()
						&& item.getHsnSac().length() > 2) {
					if (!GSTConstants.SERVICES_CODE.equalsIgnoreCase(
							item.getHsnSac().substring(0, 2))) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(HSNORSAC);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER10048",
										"HSN should start with 99 in "
												+ "case of supply of services.",
										location));
					}
				}
			}

		});
		return errors;
	}

}
