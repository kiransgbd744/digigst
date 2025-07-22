package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Gstr7IsFiled
		implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String gstin = document.getTdsGstin();
		String taxPeriod = document.getReturnPeriod();

		EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
				.getBean("EhcacheGstinTaxperiod", EhcacheGstinTaxperiod.class);

		GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(
				gstin, taxPeriod, "GSTR7", "FILED",
				TenantContext.getTenantId());

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
