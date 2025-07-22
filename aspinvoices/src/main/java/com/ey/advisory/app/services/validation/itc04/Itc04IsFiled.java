/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */
public class Itc04IsFiled implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String gstin = document.getSupplierGstin();
		String taxPeriod = document.getQRetPeriod();

		EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
				.getBean("EhcacheGstinTaxperiod", EhcacheGstinTaxperiod.class);

		GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(
				gstin, taxPeriod, "ITC04", "FILED",
				TenantContext.getTenantId());

		if (entity != null) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1280",
					"ITC04 for selected tax period is already filed",
					location));

		}
		return errors;
	}
}
