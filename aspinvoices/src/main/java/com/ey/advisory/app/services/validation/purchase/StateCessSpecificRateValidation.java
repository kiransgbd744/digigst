package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class StateCessSpecificRateValidation
		implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			BigDecimal stateCessSpecificRate = item.getStateCessSpecificRate();
			BigDecimal cessRateAdvalorem = item.getCessRateAdvalorem();
			BigDecimal stateCessRate = item.getStateCessRate();

			if (stateCessRate == null) {
				stateCessRate = BigDecimal.ZERO;
			}

			if (stateCessSpecificRate == null) {
				stateCessSpecificRate = BigDecimal.ZERO;
			}
			if (cessRateAdvalorem == null) {
				cessRateAdvalorem = BigDecimal.ZERO;
			}
			if (stateCessSpecificRate.compareTo(BigDecimal.ZERO) < 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SPE_CESS_RATE_STATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0085",
						"Invalid State Cess Specific Rate.", location));
			}
			
			if (cessRateAdvalorem.compareTo(BigDecimal.ZERO) < 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IN_CESS_RATE_ADV);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0081",
						"Invalid CessAdvaloremRate.", location));
			}
			if (stateCessRate.compareTo(BigDecimal.ZERO) < 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.STATE_CESS_RATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0083",
						"Invalid StateCessAdvaloremRate.", location));
			}
		});
		return errors;
	}

}
