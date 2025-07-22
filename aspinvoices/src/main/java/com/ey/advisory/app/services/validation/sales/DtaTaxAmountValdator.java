package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("DtaTaxAmountValdator")
public class DtaTaxAmountValdator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT, SGST_AMOUNT,
			GSTConstants.CGST_RATE, GSTConstants.SGST_RATE,

	};

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();

		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (GSTConstants.DTA.equalsIgnoreCase(document.getSupplyType())) {

				IntStream.range(0, items.size()).forEach(idx -> {

					OutwardTransDocLineItem item = items.get(idx);
					BigDecimal cgstAmount = item.getCgstAmount();
					BigDecimal sgstAmount = item.getSgstAmount();
					BigDecimal cgstRate = item.getCgstRate();
					BigDecimal sgstRate = item.getSgstRate();
					if (cgstAmount == null) {
						cgstAmount = BigDecimal.ZERO;
					}
					if (sgstAmount == null) {
						sgstAmount = BigDecimal.ZERO;
					}
					if (cgstRate == null) {
						cgstRate = BigDecimal.ZERO;
					}
					if (sgstRate == null) {
						sgstRate = BigDecimal.ZERO;
					}
					if (sgstAmount.compareTo(BigDecimal.ZERO) != 0
							|| cgstAmount.compareTo(BigDecimal.ZERO) != 0
							|| sgstRate.compareTo(BigDecimal.ZERO) != 0
							|| cgstRate.compareTo(BigDecimal.ZERO) != 0) {
						TransDocProcessingResultLoc location 
						          = new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS);
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0506",
										"In case Supply type is DTA,"
												+ " then CGST / SGST "
												+ "cannot be applied.",
										location));
					}

				});
			}
		}
		return errors;
	}

}
