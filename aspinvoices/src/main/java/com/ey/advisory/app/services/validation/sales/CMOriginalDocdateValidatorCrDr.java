package com.ey.advisory.app.services.validation.sales;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
@Component("CMOriginalDocdateValidatorCrDr")
public class CMOriginalDocdateValidatorCrDr
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> CRDR = ImmutableList.of(

			GSTConstants.CR, GSTConstants.DR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (document.getCrDrPreGst() != null
					&& !document.getCrDrPreGst().isEmpty()) {
				if (CRDR.contains(trimAndConvToUpperCase(document.getDocType()))) {
					
					if(GSTConstants.Y.equalsIgnoreCase(document.getCrDrPreGst())){
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("ddMMyyyy");

						LocalDate gstStartDate = LocalDate.parse("01072017",
								formatter);
						IntStream.range(0, items.size()).forEach(idx -> {
							OutwardTransDocLineItem item = items.get(idx);
							if (item.getOrigDocDate() != null) {
								if (item.getOrigDocDate()
										.compareTo(gstStartDate) > 0) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(
											GSTConstants.ORIGINAL_DOC_DATE);
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER0041",
											"Invalid Original Document Date.",
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
