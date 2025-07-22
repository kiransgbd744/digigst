package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
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
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ACmPreceedingDocNum")
public class Gstr1ACmPreceedingDocNum
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	private static final List<String> SUPPLYTYPE = ImmutableList.of(
			GSTConstants.TAX, GSTConstants.SEZWP, GSTConstants.SEZWOP,
			GSTConstants.DXP, GSTConstants.DTA);

	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.RNV, GSTConstants.RDR, GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (document.getSupplyType() != null
					&& !document.getSupplyType().isEmpty()) {
				if (document.getCgstin() != null
						&& !document.getCgstin().isEmpty()) {

					if (SUPPLYTYPE.contains(
							trimAndConvToUpperCase(document.getSupplyType()))
							&& DOCTYPE.contains(trimAndConvToUpperCase(
									document.getDocType()))) {

						IntStream.range(0, items.size()).forEach(idx -> {
							Gstr1AOutwardTransDocLineItem item = items.get(idx);
							if (item.getPreceedingInvoiceNumber() == null
									|| item.getPreceedingInvoiceNumber()
											.isEmpty()) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(
										GSTConstants.PRECEEDING_INV_NUMBER);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0039",
										"Preceeding Invoice Number "
												+ "cannot be left balnk.",
										location));
							}
						});
					}

				}
			}

		}

		return errors;
	}
}
