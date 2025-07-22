package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
@Component("Gstr1ACmPreceedingDocDate")
public class Gstr1ACmPreceedingDocDate
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	private static final List<String> CRDR = ImmutableList.of(

			GSTConstants.CR, GSTConstants.DR);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (document.getCrDrPreGst() != null
					&& !document.getCrDrPreGst().isEmpty()) {
				if (CRDR.contains(
						trimAndConvToUpperCase(document.getDocType()))) {

					if (GSTConstants.Y
							.equalsIgnoreCase(document.getCrDrPreGst())) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern(GSTConstants.ddMMyyyy);

						LocalDate gstStartDate = LocalDate
								.parse(GSTConstants.DATE01072017, formatter);
						IntStream.range(0, items.size()).forEach(idx -> {
							Gstr1AOutwardTransDocLineItem item = items.get(idx);
							if (item.getPreceedingInvoiceDate() != null) {
								if (item.getPreceedingInvoiceDate()
										.compareTo(gstStartDate) > 0) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(
											GSTConstants.PRECEEDING_INV_DATE);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER0041",
											"Invalid Preceeding Invoice Document Date.",
											location));
								}
							}
						});
					}
				}
			}
		}
		return errors;
	}
}
