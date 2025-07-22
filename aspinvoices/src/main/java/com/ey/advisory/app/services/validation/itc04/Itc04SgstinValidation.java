/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04SgstinValidation")
public class Itc04SgstinValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("DefaultStateCache") private StateCache stateCache;
	 */

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();

		if (document.getSupplierGstin() != null
				&& !document.getSupplierGstin().isEmpty()) {
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSupplierGstin());
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5809",
						"Invalid SupplierGSTIN.", location));
				return errors;

			}

			String registrationType = gstin.getRegistrationType() != null
					? gstin.getRegistrationType().trim() : null;
			if (!(GSTConstants.SEZD.equalsIgnoreCase(registrationType)
					|| GSTConstants.SEZU.equalsIgnoreCase(registrationType)
					|| GSTConstants.REGULAR
							.equalsIgnoreCase(registrationType))) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5809",
						"Invalid SupplierGSTIN.", location));
				return errors;

			}
			/*
			 * stateCache = StaticContextHolder.getBean("DefaultStateCache",
			 * StateCache.class);
			 * 
			 * String statecode = document.getSupplierGstin().substring(0, 2);
			 * int n = stateCache.findStateCode(statecode); if (n <= 0) {
			 * Set<String> errorLocations = new HashSet<>();
			 * errorLocations.add(GSTConstants.SGSTIN);
			 * TransDocProcessingResultLoc location = new
			 * TransDocProcessingResultLoc( null, errorLocations.toArray());
			 * errors.add(new ProcessingResult(APP_VALIDATION, "ER5809",
			 * "Invalid SupplierGSTIN.", location)); }
			 */
		}
		return errors;
	}
}
