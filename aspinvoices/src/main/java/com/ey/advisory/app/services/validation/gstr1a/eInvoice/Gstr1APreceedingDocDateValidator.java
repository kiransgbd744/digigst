package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1APreceedingDocDateValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.RNV, GSTConstants.RCR, GSTConstants.RDR);

	public static void main(String[] args) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
		// Calculate the last day of the month.

		Integer derivedRetPeriod = GenUtil.getDerivedTaxPeriod("072024"); // 082024

		System.out.println(derivedRetPeriod);
		LocalDate preInvDate = LocalDate.of(2024, 7, 15);

		Integer derivedPreceedInvDate = preInvDate != null
				? GenUtil.getDerivedTaxPeriod(preInvDate.format(formatter)) : 0;

				System.out.println(derivedPreceedInvDate);
		System.out.println(preInvDate); // 01-07-2024
		if (derivedPreceedInvDate != 0
				&& derivedPreceedInvDate > derivedRetPeriod) {
			System.out.println("if");
		}

	}

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (document.getTaxperiod() != null
					&& !document.getTaxperiod().isEmpty()) {

				String doctype = document.getDocType();
				// String taxPeriod = document.getTaxperiod();
				Integer derivedRetPeriod = GenUtil
						.getDerivedTaxPeriod(document.getTaxperiod()); // 082024
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("MMyyyy");
				// Calculate the last day of the month.
				if (DOC_TYPES_REQUIRING_IMPORTS
						.contains(trimAndConvToUpperCase(doctype))) {

					/**
					 * taxPeriod has only month and year so any common date we
					 * are choosing for both returnPeriod and originalDocDate.
					 */
					IntStream.range(0, items.size()).forEach(idx -> {
						Gstr1AOutwardTransDocLineItem item = items.get(idx);

						Integer derivedPreceedInvDate = item
								.getPreceedingInvoiceDate() != null
										? GenUtil.getDerivedTaxPeriod(
												item.getPreceedingInvoiceDate()
														.format(formatter)) // 072024
										: 0;
						if (derivedPreceedInvDate != 0
								&& derivedPreceedInvDate > derivedRetPeriod) {
							errorLocations
									.add(GSTConstants.PRECEEDING_INV_DATE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0520",
									"Preceeding Invoice date should be "
											+ "prior to the Return period",
									location));

						}
					});
				}

			}
		}
		return errors;
	}

}
