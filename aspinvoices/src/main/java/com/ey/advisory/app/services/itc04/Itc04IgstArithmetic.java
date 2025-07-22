package com.ey.advisory.app.services.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Itc04IgstArithmetic
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		BigDecimal igstRate = document.getLineItems().get(0)
				.getIgstRate() != null
						? document.getLineItems().get(0).getIgstRate()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal taxableValue = document.getLineItems().get(0)
				.getTaxableValue() != null
						? document.getLineItems().get(0).getTaxableValue()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal igstAmount = document.getLineItems().get(0)
				.getIgstAmount() != null
						? document.getLineItems().get(0).getIgstAmount()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal ratePer = igstRate.divide(BigDecimal.valueOf(100));
		BigDecimal arithmeticValue = ratePer.multiply(taxableValue);

		if (arithmeticValue.compareTo(igstAmount) != 0) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IGST_RATE);
			errorLocations.add(GSTConstants.TaxableValue);
			errorLocations.add(GSTConstants.igstamount);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN5801",
					"IGST Amount is incorrectly computed.", location));
			return errors;

		}

		return errors;
	}

}
