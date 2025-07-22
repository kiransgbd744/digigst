package com.ey.advisory.app.services.validation.sales;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_RATE;
import static com.ey.advisory.common.GSTConstants.SGST_RATE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
/**
 * 
 * @author Mahesh.Golla
 * 
 * BR_OUTWARD_47
 */
/**
 * 
 * This class responsible for if the Cgst and sgst rates are different for any
 * transactions it should be sent the error description like Cgst and sgst rates 
 * are different 
 *
 */
@Component("cgstSgstRatesDiffValidator")
public class CgstSgstRatesDiffValidators
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
/**
 *  Here we are getting rates from Cilent and comparing those rates like cgst 
 *  and sgst,igst rates 
 */
		IntStream.range(0, items.size()).forEach(idx->{
		OutwardTransDocLineItem item = items.get(idx);
		BigDecimal cgstRate = item.getCgstRate();
		BigDecimal sgstRate = item.getSgstRate();
	BigDecimal	taxAmount = (cgstRate.compareTo(sgstRate) > 0)
				? cgstRate.subtract(sgstRate).abs()
				: sgstRate.subtract(cgstRate).abs();

				if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
			errorLocations.add(CGST_RATE);
			errorLocations.add(SGST_RATE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER234",
					"CGST and SGST Rates cannot be different",
					location));
		}
		});
		return errors;
	}
}
