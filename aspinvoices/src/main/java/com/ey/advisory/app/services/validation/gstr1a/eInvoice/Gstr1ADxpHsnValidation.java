package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ADxpHsnValidation")
public class Gstr1ADxpHsnValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> SUPPLYTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.DXP);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			if (document.getSupplyType() != null
					&& !document.getSupplyType().isEmpty()) {
				if (SUPPLYTYPE_IMPORTS.contains(
						trimAndConvToUpperCase(document.getSupplyType()))) {
					if (item.getHsnSac() != null
							&& !item.getHsnSac().isEmpty()) {

						if (item.getHsnSac().length() > 1) {
							String hsn2digit = item.getHsnSac().substring(0, 2);
							int hsn1 = Integer.parseInt(hsn2digit);
							if (99 == hsn1) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(HSNORSAC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0515",
										"In case of Deemed Exports, "
												+ "HSN should not start with 99",
										location));
							}
						}

					}
				}
			}
		});
		return errors;
	}

}
