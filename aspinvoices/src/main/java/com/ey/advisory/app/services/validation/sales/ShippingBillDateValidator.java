package com.ey.advisory.app.services.validation.sales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
@Component("ShippingBillDateValidator")
public class ShippingBillDateValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLY_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of("EXPT", "EXPWT");

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		LocalDate shippingBillDate = document.getShippingBillDate();
		if (!SUPPLY_TYPES_REQUIRING_IMPORTS.contains(
				trimAndConvToUpperCase(supplyType)) 
				&& !"RNV".equalsIgnoreCase(docType)) {
			return errors;
		}

		if (shippingBillDate != null) {
			return errors;
		}
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.SHIPPING_BILL_DATE);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER101",
				"Shipping Bill Date is mandatory in case of exports/SEZ",
				location));
		return errors;

	}
}