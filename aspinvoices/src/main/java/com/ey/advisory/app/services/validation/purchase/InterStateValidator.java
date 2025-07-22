package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class InterStateValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			if (document.getSgstin() != null
					&& !document.getSgstin().isEmpty()) {

				String firstTwoDigitsSgstin = document.getSgstin().substring(0,
						2);

				BigDecimal sgstAmount = item.getSgstAmount();
				BigDecimal cgstAmount = item.getCgstAmount();
				if (document.getPos() != null && !document.getPos().isEmpty()) {
					if (document.getSupplyType() != null
							&& !document.getSupplyType().isEmpty()) {
						if ("TAX".equalsIgnoreCase(document.getSupplyType())) {
							if (!firstTwoDigitsSgstin
									.equalsIgnoreCase(document.getPos())) {
								if (cgstAmount.add(sgstAmount)
										.compareTo(BigDecimal.ZERO) != 0
										|| cgstAmount
												.compareTo(BigDecimal.ZERO) != 0
										|| sgstAmount.compareTo(
												BigDecimal.ZERO) != 0) {
									errorLocations.add(CGST_AMOUNT);
									errorLocations.add(SGST_AMOUNT);
									TransDocProcessingResultLoc location = 
											new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER237",
											"In case of Inter-State Supply, "
											+ "CGST & SGST/UTGST "
													+ "Tax Rate and/or Tax "
													+ "Amount cannot be applied",
											location));

								}

							}
						}
					}
				}
			}
		});

		return errors;
	}
}
