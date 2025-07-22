package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
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

public class PreedingDocDateValidation
		implements DocRulesValidator<OutwardTransDocument> {
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		LocalDate docdate = document.getDocDate();
		if (docdate == null) return errors;
		List<String> errorLocations = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(0);
			if (item.getPreceedingInvoiceDate() != null
					&& item.getPreceedingInvoiceDate().isAfter(docdate)) {
					errorLocations.add(GSTConstants.PRECEEDING_INV_DATE);
					TransDocProcessingResultLoc location 
					 = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15221",
							"Preceding invoice date should be less  than document date",
							location));
				}
			
		});
		return errors;
	}

}
