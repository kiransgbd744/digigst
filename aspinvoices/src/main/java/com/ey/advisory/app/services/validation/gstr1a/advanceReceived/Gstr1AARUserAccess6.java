package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.app.services.validation.sales.DataSecurityApplicabilityChecker;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AARUserAccess6
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {

	@Autowired
	@Qualifier("DataSecurityApplicabilityChecker")
	private DataSecurityApplicabilityChecker onboardAttributesChecking;

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O5.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		Map<Long, List<Pair<String, String>>> entityAtValMap = document
				.getEntityAtValMap();
		String paramkeyId12 = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O12.name();
		String paramterValue12 = util.valid(entityConfigParamMap, paramkeyId12,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty() || paramtrvalue
				.equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()))
			return errors;

		Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = document
				.getEntityAtConfMap();

		String ua6 = document.getUserAccess6();
		onboardAttributesChecking = StaticContextHolder.getBean(
				"DataSecurityApplicabilityChecker",
				DataSecurityApplicabilityChecker.class);

		EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
				document.getEntityId(), OnboardingConstant.UD6);
		Map<Long, String> entitAtConfigInnerMap = entAtConfMap
				.get(entityAtConfigKey);
		entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {

			String atOutward = entitAtConfMap.getValue();
			if (OnboardingConstant.AT_M.equalsIgnoreCase(atOutward)) {
				boolean isAttrValid = onboardAttributesChecking.isAttrValid(
						entityAtValMap, OnboardingConstant.UD6, ua6,
						document.getEntityId());
				if (ua6 == null || ua6.isEmpty()) {
					errorLocations.add(GSTConstants.USER_ACCESS6);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5150",
							" User Access 6 is not as per master "
									+ "provided during On-Boarding.",
							location));
				}
				if (!isAttrValid) {

					errorLocations.add(GSTConstants.USER_ACCESS6);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5150",
							" User Access 6 is not as per master "
									+ "provided during On-Boarding.",
							location));
				}

			}
			if (OnboardingConstant.AT_O.equalsIgnoreCase(atOutward)) {
				boolean isAttrValid = onboardAttributesChecking.isAttrValid(
						entityAtValMap, OnboardingConstant.UD6, ua6,
						document.getEntityId());

				if (!isAttrValid) {

					errorLocations.add(GSTConstants.USER_ACCESS6);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5112",
							" User Access 6 is not as per master "
									+ "provided during On-Boarding.",
							location));

				}
			}
		});

		if (ua6 != null && !ua6.isEmpty()) {

			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramterValue12)) {
				boolean isAttrUserValid = onboardAttributesChecking.isAttrValid(
						OnboardingConstant.PC, ua6, document.getEntityId(),
						document.getCreatedBy());
				if (!isAttrUserValid) {
					errorLocations.add(GSTConstants.USER_ACCESS6);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5162",
							"User Access 6 is not as per user access level"
									+ "  provided in during On-Boarding.",
							location));
				}
			}
		}

		return errors;
	}

}
