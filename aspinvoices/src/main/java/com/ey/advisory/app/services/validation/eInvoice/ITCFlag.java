package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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

/**
 * @author Siva.Nandam
 *
 */
@Component("ITCFlag")
public class ITCFlag implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
		
		
		if (item.getItcFlag() != null
				&& !item.getItcFlag().isEmpty()) {
			boolean valid = YorNFlagValidation
					.itcvalid(item.getItcFlag());
			if (!valid) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ItcFlag);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0102",
						"Invalid ITC Flag.", location));

			}
		}
		});
		return errors;
	}

}
