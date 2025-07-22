package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.app.services.strcutvalidation.*;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class PaymentDateValidationRule implements ValidationRule {
	@Autowired
	private DataConverterFactory converterFactory;

	@Override
	public List<ProcessingResult>isValid(Object obj, Object[] row, 
			                                TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.TAXABLE_VALUE);
		TransDocProcessingResultLoc location 
		                     = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
				"Invalid Taxable Value",
				location));
		boolean isPresent = ValidatorUtil.isValuePresent(obj);
		/*if (!isPresent) {
			return true;
		}
*/
		// Check if a converter is available. Otherwise return false.
		Converter converter = converterFactory.getConverter(obj,
				LocalDate.class);
		if (converter == null) {
			return errors;
		}

		// Get the converted value
		LocalDate paymentDate = (LocalDate) converter.convert(obj);

		// Check if Purchase Voucher Date satisfies the basic doc Date
		// rules.
		if(paymentDate == null){
			return errors;
		}
		//return !(paymentDate == null);
		return errors;

	}

}
