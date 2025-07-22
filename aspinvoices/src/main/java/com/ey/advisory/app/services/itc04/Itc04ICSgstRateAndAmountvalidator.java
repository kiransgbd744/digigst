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
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Itc04ICSgstRateAndAmountvalidator
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		BigDecimal common = BigDecimal.ZERO;
		BigDecimal igstAmount = document.getLineItems().get(0)
				.getIgstAmount() != null
						? document.getLineItems().get(0).getIgstAmount()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal cgstAmount = document.getLineItems().get(0)
				.getCgstAmount() != null
						? document.getCgstAmount().setScale(2,
								BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal sgstAmount = document.getLineItems().get(0)
				.getSgstAmount() != null
						? document.getLineItems().get(0).getSgstAmount()
								.setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
		BigDecimal igstRate = document.getLineItems().get(0)
				.getIgstRate() != null
						? document.getLineItems().get(0).getIgstRate()
						: BigDecimal.ZERO;
		BigDecimal cgstRate = document.getLineItems().get(0)
				.getCgstRate() != null
						? document.getLineItems().get(0).getSgstRate()
						: BigDecimal.ZERO;
		BigDecimal sgstRate = document.getLineItems().get(0)
				.getSgstRate() != null
						? document.getLineItems().get(0).getCgstRate()
						: BigDecimal.ZERO;

		if ((igstAmount.compareTo(common) != 0
				&& sgstAmount.compareTo(common) != 0
				&& cgstAmount.compareTo(common) != 0)
				|| (igstAmount.compareTo(common) != 0
						&& sgstAmount.compareTo(common) != 0)
				|| (igstAmount.compareTo(common) != 0
						&& cgstAmount.compareTo(common) != 0)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IGST_AMOUNT);
			errorLocations.add(GSTConstants.CGST_AMOUNT);
			errorLocations.add(GSTConstants.SGST_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5865",
					"A transaction can either be inter state or intra state.",
					location));

		}
		if ((igstRate.compareTo(common) != 0 && sgstRate.compareTo(common) != 0
				&& cgstRate.compareTo(common) != 0)
				|| (igstRate.compareTo(common) != 0
						&& sgstRate.compareTo(common) != 0)
				|| (igstRate.compareTo(common) != 0
						&& cgstRate.compareTo(common) != 0)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IGST_RATE);
			errorLocations.add(GSTConstants.CGST_RATE);
			errorLocations.add(GSTConstants.SGST_RATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5865",
					"A transaction can either be inter state or intra state.",
					location));

		}

		return errors;
	}

}
