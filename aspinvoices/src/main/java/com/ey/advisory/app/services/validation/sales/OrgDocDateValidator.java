package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ORIGINAL_DOC_DATE;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
public class OrgDocDateValidator
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.RNV, GSTConstants.RCR, GSTConstants.RDR);

	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (document.getTaxperiod() != null
					&& !document.getTaxperiod().isEmpty()) {

				String doctype = document.getDocType();
				// String taxPeriod = document.getTaxperiod();
				String tax = "01" + document.getTaxperiod();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");

				// Calculate the last day of the month.
				LocalDate returnPeriod = LocalDate.parse(tax, formatter);
				if (DOC_TYPES_REQUIRING_IMPORTS
						.contains(trimAndConvToUpperCase(doctype))) {

					/**
					 * taxPeriod has only month and year so any common date we
					 * are choosing for both returnPeriod and originalDocDate.
					 */
					IntStream.range(0, items.size()).forEach(idx -> {
						OutwardTransDocLineItem item = items.get(idx);

						if ((item.getPreceedingInvoiceDate() != null
								&& item.getPreceedingInvoiceDate()
										.compareTo(returnPeriod) >= 0)) {
							errorLocations.add(ORIGINAL_DOC_DATE);
							TransDocProcessingResultLoc location 
					        = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0520",
									"Original document date should be "
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
