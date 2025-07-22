package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.multitenancy.TenantContext;

public class EcomGstinValidation implements ValidationRule {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER8001",
					"Invalid eCOMGSTIN or eCOMGSTIN is not Onboarded",
					location));
			return errors;
		}
		if (obj.toString().trim().length() != 15) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER8001",
					"Invalid eCOMGSTIN or eCOMGSTIN is not Onboarded",
					location));
			return errors;
		}
		String groupCode = TenantContext.getTenantId();
		Ehcachegstin ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				obj.toString().trim());
		if (gstin == null) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER8001",
					"Invalid eCOMGSTIN or eCOMGSTIN is not Onboarded",
					location));
			return errors;
		}

		return errors;

	}

}
