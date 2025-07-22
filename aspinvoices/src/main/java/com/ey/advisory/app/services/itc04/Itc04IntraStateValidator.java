package com.ey.advisory.app.services.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Itc04IntraStateValidator
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		Set<String> errorLocations = new HashSet<>();
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);

		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSupplierGstin());

		if (gstin != null) {

			String registrationType = gstin.getRegistrationType() != null
					? gstin.getRegistrationType().trim() : null;
			if ((GSTConstants.SEZD.equalsIgnoreCase(registrationType)
					|| GSTConstants.SEZU.equalsIgnoreCase(registrationType))) {
				return errors;
			}
		}
		String supplierGstin = (document.getSupplierGstin() != null)
				? document.getSupplierGstin().trim().substring(0, 2) : null;
		String pos = getStateCode(document.getJobWorkerGstin(),
				document.getJobWorkerStateCode());

		BigDecimal igstRate = document.getLineItems().get(0)
				.getIgstRate() != null
						? document.getLineItems().get(0).getIgstRate()
						: BigDecimal.ZERO;

		/*// new validation added
		if (supplierGstin != null && pos != null
				&& document.getJobWorkerType() != null
				&& supplierGstin.equalsIgnoreCase(pos)) {

			if (document.getJobWorkerType().equalsIgnoreCase("S")
					&& igstRate.compareTo(BigDecimal.ZERO) > 0) {
				return errors;

			} else {
				errorLocations.add(GSTConstants.IGST_RATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5863",
						"IGST Rate cannot be applied in case of Intra state supply.",
						location));
				return errors;
			}

		}*/
		// new validation ends

		if (supplierGstin != null && pos != null
				&& supplierGstin.equalsIgnoreCase(pos)
				&& !"S".equalsIgnoreCase(document.getJobWorkerType())
				&& igstRate.compareTo(BigDecimal.ZERO) > 0) {
			errorLocations.add(GSTConstants.IGST_RATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5863",
					"IGST Rate cannot be applied in case of Intra state supply.",
					location));
			return errors;

		}
		return errors;
	}

	private String getStateCode(String jobWorkerGstin,
			String jobWorkerStateCode) {
		if (jobWorkerGstin != null && !jobWorkerGstin.isEmpty()
				&& jobWorkerStateCode != null
				&& !jobWorkerStateCode.isEmpty()) {
			return jobWorkerGstin.substring(0, 2);
		}
		if (jobWorkerStateCode != null && !jobWorkerStateCode.isEmpty()) {
			return jobWorkerStateCode;
		}
		if (jobWorkerGstin != null && !jobWorkerGstin.isEmpty()) {
			return jobWorkerGstin.substring(0, 2);
		}
		return jobWorkerGstin;
	}

}
