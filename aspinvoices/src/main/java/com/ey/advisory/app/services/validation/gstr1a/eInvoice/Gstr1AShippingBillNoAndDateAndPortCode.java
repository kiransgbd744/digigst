package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AShippingBillNoAndDateAndPortCode")
public class Gstr1AShippingBillNoAndDateAndPortCode
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE = ImmutableList
			.of(GSTConstants.EXPT, GSTConstants.EXPWT);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() == null
				|| document.getSupplyType().isEmpty())
			return errors;

		if (!SUPPLY_TYPE
				.contains(trimAndConvToUpperCase(document.getSupplyType())))
			return errors;
		if (!isPresent(document.getShippingBillNo())
				&& document.getShippingBillDate() == null)
			return errors;

		if (isPresent(document.getShippingBillNo())
				&& document.getShippingBillDate() != null)
			return errors;

		if (document.getShippingBillNo() == null
				|| document.getShippingBillNo().isEmpty()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SHIPPING_BILL_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0063",
					"Shipping Bill Number cannot be left blank.", location));
		}
		if (document.getShippingBillDate() == null) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SHIPPING_BILL_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0066",
					"Shipping Bill Date cannot be left blank.", location));
		}

		return errors;
	}

}
