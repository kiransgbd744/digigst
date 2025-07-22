package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6LuGstinValidation
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	/**
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @param ProcessingContext
	 */

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();

		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.

		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);

		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getIsdGstin());
		if (gstin == null) {

			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.GSTIN);
			TransDocProcessingResultLoc location 
			               = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER3056",
					"Invalid ISD GSTIN is not as per On-Boarding data",
					location));
			return errors;

		}
		if (gstin != null && gstin.getRegistrationType() != null) {
			if (!GSTConstants.ISD
					.equalsIgnoreCase(gstin.getRegistrationType())) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.GSTIN);
				TransDocProcessingResultLoc location 
				          = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3056",
						"Invalid ISD GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
		}
		return errors;
	}

}
