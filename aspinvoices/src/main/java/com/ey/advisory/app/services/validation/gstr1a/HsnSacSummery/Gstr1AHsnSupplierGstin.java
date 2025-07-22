package com.ey.advisory.app.services.validation.gstr1a.HsnSacSummery;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredHsnEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredHsnEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class Gstr1AHsnSupplierGstin
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredHsnEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredHsnEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			Ehcachegstin ehcachegstin = StaticContextHolder
					.getBean("Ehcachegstin", Ehcachegstin.class);
			String groupCode = TenantContext.getTenantId();
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSgstin());
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5702",
						"Supplier GSTIN is not as per On-Boarding data.",
						location));
			}
		}
		return errors;
	}
}
