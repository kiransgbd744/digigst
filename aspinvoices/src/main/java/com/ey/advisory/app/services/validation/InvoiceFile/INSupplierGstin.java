package com.ey.advisory.app.services.validation.InvoiceFile;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import com.google.common.base.Strings;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class INSupplierGstin
		implements B2csBusinessRuleValidator<Gstr1AsEnteredInvEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (Strings.isNullOrEmpty(document.getSgstin()))
			return errors;
		Map<String, String> gstinInfoMap = (Map<String, String>) context
				.getAttribute("gstinInfoMap");
		if (!gstinInfoMap.containsKey(document.getSgstin())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5202",
					"Supplier GSTIN is not as per On-Boarding data", location));
		}

		return errors;
	}
}
