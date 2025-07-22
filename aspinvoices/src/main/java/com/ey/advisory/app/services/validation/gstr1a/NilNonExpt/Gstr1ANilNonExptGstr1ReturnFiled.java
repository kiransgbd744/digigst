package com.ey.advisory.app.services.validation.gstr1a.NilNonExpt;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

public class Gstr1ANilNonExptGstr1ReturnFiled implements
		B2csBusinessRuleValidator<Gstr1ANilNonExemptedAsEnteredEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1ANilNonExemptedAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getSgstin() != null && !document.getSgstin().isEmpty()
				&& document.getReturnPeriod() != null
				&& !document.getReturnPeriod().isEmpty()) {
			String gstin = document.getSgstin();
			String taxPeriod = document.getReturnPeriod();

			String groupCode = TenantContext.getTenantId();
			String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O36.name();
			OnboardingQuestionValidationsUtil util = StaticContextHolder
					.getBean("OnboardingQuestionValidationsUtil",
							OnboardingQuestionValidationsUtil.class);
			Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
					.getEntityConfigParamMap();
			String answer = util.valid(entityConfigParamMap, paramkryId,
					document.getEntityId());
			if (answer == null || GSTConstants.B.equalsIgnoreCase(answer))
				return errors;
			EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
					.getBean("EhcacheGstinTaxperiod",
							EhcacheGstinTaxperiod.class);

			GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(
					gstin, taxPeriod, APIConstants.GSTR1A.toUpperCase(), "FILED", groupCode);
			if (entity != null) {
				errorLocations.add(GSTConstants.HSNORSAC);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
						"GSTR1A for this tax period is already filed",
						location));
			}

		}

		return errors;
	}

}
