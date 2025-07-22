package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class VendorSuppliergstin
		implements BusinessRuleValidator<MasterVendorEntity> {

	@Override
	public List<ProcessingResult> validate(MasterVendorEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		String outSideIndia = document.getOutSideIndia();
		String supplierGstinOrPan = document.getSupplierGstinPan();

		if (outSideIndia != null
				&& GSTConstants.Y.equalsIgnoreCase(outSideIndia)) {
			if (supplierGstinOrPan != null && !supplierGstinOrPan.isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1628",
						"Suppliergstin GSTIN/PAN should be blank.", location));
				return errors;
			}
		}
		if (outSideIndia != null
				&& GSTConstants.N.equalsIgnoreCase(outSideIndia)) {
			if (supplierGstinOrPan == null || supplierGstinOrPan.isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1629",
						"Suppliergstin GSTIN/PAN cannot be left blank.",
						location));
				return errors;
			}

		}

		return errors;
	}

}
