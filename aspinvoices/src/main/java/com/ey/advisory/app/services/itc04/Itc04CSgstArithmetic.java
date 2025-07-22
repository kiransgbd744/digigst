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

public class Itc04CSgstArithmetic
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		BigDecimal cgstRate = document.getLineItems().get(0)
				.getCgstRate() != null
						? document.getLineItems().get(0).getCgstRate()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;

		BigDecimal sgstRate = document.getLineItems().get(0)
				.getSgstRate() != null
						? document.getLineItems().get(0).getSgstRate()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;

		BigDecimal taxableValue = document.getLineItems().get(0)
				.getTaxableValue() != null
						? document.getLineItems().get(0).getTaxableValue()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal cgstAmount = document.getLineItems().get(0)
				.getCgstAmount() != null
						? document.getLineItems().get(0).getCgstAmount()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;

		BigDecimal sgstAmount = document.getLineItems().get(0)
				.getSgstAmount() != null
						? document.getLineItems().get(0).getSgstAmount()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;

		BigDecimal rateCgsstPer = cgstRate.divide(BigDecimal.valueOf(100));
		BigDecimal rateSgsstPer = sgstRate.divide(BigDecimal.valueOf(100));
		BigDecimal arithmeticCgstValue = rateCgsstPer.multiply(taxableValue);
		BigDecimal arithmeticSgstValue = rateSgsstPer.multiply(taxableValue);

		if (arithmeticCgstValue.compareTo(cgstAmount) != 0) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CGST_RATE);
			errorLocations.add(GSTConstants.TaxableValue);
			errorLocations.add(GSTConstants.cgstamount);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN5802",
					"CGST / SGST amount is incorrectly computed.", location));
		}
		if (arithmeticSgstValue.compareTo(sgstAmount) != 0) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGST_RATE);
			errorLocations.add(GSTConstants.TaxableValue);
			errorLocations.add(GSTConstants.sgstamount);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN5802",
					"CGST / SGST amount is incorrectly computed.", location));
		}

		return errors;
	}

}
