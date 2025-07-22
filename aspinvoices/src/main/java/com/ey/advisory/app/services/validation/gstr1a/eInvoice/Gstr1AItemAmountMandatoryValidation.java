package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AItemAmountMandatoryValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);
			BigDecimal itemAmt = item.getItemAmount();
			if (itemAmt == null) {
				itemAmt = BigDecimal.ZERO;
			}
			if (itemAmt.compareTo(BigDecimal.ZERO) == 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ITEM_AMOUNT);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10059",
						" Item Amount cannot be left blank.", location));
			}
		});

		return errors;
	}

}
