package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * This class is responsible to Cess Amount cannot be applied for any CGST and
 * SGST and IGST need not to be applies
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_65
 * 
 * @author Murali.Singanamala
 *
 */

@Component("CessAmountValidator")
public class CessAmountValidator

		implements DocRulesValidator<OutwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CessAmountValidator.class);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> result = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		Set<String> errorLocations = new HashSet<>();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal cessAmount = item.getCessAmount();

			/**
			 * This method first add the cgstAmount and sgstAmount and compare
			 * the both the amount is greater than 0 and cess amount is greater
			 * than 0 then it get an information
			 */
			/**
			 * This method compare the igst amount is greater than 0 and cess
			 * amount is greater than 0 then it get an information
			 */
			if (((sgstAmount.add(cgstAmount).add(igstAmount))
					.compareTo(BigDecimal.ZERO) == 0)
					&& cessAmount.compareTo(BigDecimal.ZERO) > 0) {

				errorLocations.add(GSTConstants.CESS_AMT_ADVALOREM);
				errorLocations.add(GSTConstants.CESS_AMT_SPECIFIC);

				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				result.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN308",
						" Cess Amount cannot be applied ", location));
			}

			

			
		});

		return result;
	}

}
