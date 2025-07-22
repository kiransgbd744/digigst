package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class IntraSateValidator
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
				BigDecimal igstAmount = item.getIgstAmount();

				if (document.getPos() != null && !document.getPos().isEmpty()) {
					if (document.getSupplyType() != null
							&& !document.getSupplyType().isEmpty()) {
						if ("TAX".equalsIgnoreCase(document.getSupplyType())) {

							if (firstTwoDigitsSgstin
									.equalsIgnoreCase(document.getPos())) {

								if (BigDecimal.ZERO
										.compareTo(igstAmount) != 0) {
									errorLocations.add(IGST_AMOUNT);
									TransDocProcessingResultLoc location = 
											new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER238",
											"In case of Intra-State Supply, "
											+ "IGST Tax Rate and "
													+ "and/or Tax Amount "
													+ "cannot be applied",
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
