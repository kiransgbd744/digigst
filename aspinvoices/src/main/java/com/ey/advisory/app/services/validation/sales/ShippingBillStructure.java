package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class ShippingBillStructure
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.RCR, GSTConstants.RDR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (DOC_TYPES_REQUIRING_IMPORTS.contains(document.getDocType())) {
			if (document.getShippingBillNo() != null
					&& !document.getShippingBillNo().isEmpty()) {

				String shippingBillNo = document.getShippingBillNo().trim();
				String regex = "^[a-zA-Z0-9/-]*$";
				Pattern pattern = Pattern.compile(regex);

				Matcher matcher = pattern.matcher(shippingBillNo);
				if (shippingBillNo.length() > 16 || !matcher.matches()) {
					errorLocations.add(GSTConstants.SHIPPING_BILL_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0061",
							"Invalid Shipping Bill Number.", location));
					return errors;
				}

			}
		}
		return errors;
	}

}
