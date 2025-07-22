package com.ey.advisory.app.services.validation.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Ravindra V S
 *
 */
public class ArIsGstr1ReturnFiled
		implements ARBusinessRuleValidator<Gstr1AsEnteredAREntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredAREntity document,
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
					gstin, taxPeriod, "GSTR1", "FILED", groupCode);

			if (entity != null) {
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
						"GSTR1 for this tax period is already filed",
						location));
			}

		}

		return errors;
	}

}
