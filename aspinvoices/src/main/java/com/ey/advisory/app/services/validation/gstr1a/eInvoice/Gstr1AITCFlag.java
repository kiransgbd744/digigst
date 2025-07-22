package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

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
@Component("Gstr1AITCFlag")
public class Gstr1AITCFlag
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			if (item.getItcFlag() != null && !item.getItcFlag().isEmpty()) {
				boolean valid = Gstr1AYorNFlagValidation.itcvalid(item.getItcFlag());
				if (!valid) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.ItcFlag);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0102",
							"Invalid ITC Flag.", location));

				}
			}
		});
		return errors;
	}

}
