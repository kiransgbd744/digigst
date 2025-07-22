package com.ey.advisory.app.services.validation.tdstcs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.caches.TcsTdsCategoryCache;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class TcsTdsIsFiledValidation
		implements B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {


	@Override
	public List<ProcessingResult> validate(Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String gstin = document.getGstin();
		String taxPeriod = document.getRetPeriod();

		EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
				.getBean("EhcacheGstinTaxperiod", EhcacheGstinTaxperiod.class);

		String groupCode = TenantContext.getTenantId();

		GstrReturnStatusEntity entity = ehcachegstinTaxPeriod
				.isGstinFiled(gstin, taxPeriod, "GSTR7", "FILED", groupCode);
		if (entity != null) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
					"GSTR7 for selected tax period  is already filed", location));
		}

		return errors;
	}

}
