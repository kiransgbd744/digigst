package com.ey.advisory.app.services.businessvalidation.table3h3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class SGSTINorPANValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		if (document.getTransactionFlag() != null
				&& !document.getTransactionFlag().isEmpty()) {
			if (document.getTransactionFlag()
					.equalsIgnoreCase(GSTConstants.RC)) {
				String supplierGSTINorpan = document.getSupplierGSTINorpan();
				if (supplierGSTINorpan == null ||supplierGSTINorpan.isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							" ER1212",
							"Supplier GSTIN or PAN cannot be left blank in case "
							+ "Transaction flag is RC.",
							location));
					return errors;
				}
			}

			if (document.getTransactionFlag()
					.equalsIgnoreCase(GSTConstants.IMPS)) {
				if (document.getSupplierGSTINorpan() == null
						|| document.getSupplierGSTINorpan().isEmpty()) {
					// nothing
				} else {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							" ER1213",
							"Supplier GSTIN or PAN should be blank in case"
							+ " of IMPS.",
							location));
					return errors;
				}
			}
		}
		return errors;
	}
}
