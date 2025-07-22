package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Cmgstr1Quantity
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();

		if (!GSTConstants.INV.equalsIgnoreCase(document.getDocType()))
			return errors;

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
				String hsn = item.getHsnSac();
				if (hsn.length() > 1 && !GSTConstants.HSNSAC.equals(hsn.substring(0, 2))) {
					if (item.getQty() == null
							|| item.getQty().compareTo(BigDecimal.ZERO) == 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.QUANTITY);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0152", "Quantity cannot be left blank.",
								location));
					}
				}
			}

		});

		return errors;
	}
}
