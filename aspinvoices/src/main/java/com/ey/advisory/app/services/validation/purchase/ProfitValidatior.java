package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.DataSecurityApplicabilityChecker;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class ProfitValidatior
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("DataSecurityApplicabilityChecker")
	private DataSecurityApplicabilityChecker onboardAttributesChecking;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();

		String paramkryId = CONFIG_PARAM_INWARD_QUE_KEY_ID.I4.name();
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		Map<Long, List<Pair<String, String>>> entityAtValMap = document
				.getEntityAtValMap();

		List<InwardTransDocLineItem> items = document.getLineItems();

		Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = document
				.getEntityAtConfMap();

		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);

			String profitCentre = item.getProfitCentre();
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramtrvalue)) {
				onboardAttributesChecking = StaticContextHolder.getBean(
						"DataSecurityApplicabilityChecker",
						DataSecurityApplicabilityChecker.class);

				EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
						document.getEntityId(), OnboardingConstant.PC);
				Map<Long, String> entitAtConfigInnerMap = entAtConfMap
						.get(entityAtConfigKey);
				entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {

					String atOutward = entitAtConfMap.getValue();
					if (OnboardingConstant.AT_M.equalsIgnoreCase(atOutward)) {
						boolean isAttrValid = onboardAttributesChecking
								.isAttrValid(entityAtValMap,
										OnboardingConstant.PC, profitCentre,
										document.getEntityId());
						if (profitCentre == null || profitCentre.isEmpty()) {
							errorLocations.add(GSTConstants.PROFITCENTRE);
							TransDocProcessingResultLoc location 
							       = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1121",
									"Profit Centre not as per configurable "
											+ "parameters selected by user.",
									location));
						}
						if (!isAttrValid) {

							errorLocations.add(GSTConstants.PROFITCENTRE);
							TransDocProcessingResultLoc location 
							      = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1121",
									" Profit Centre not as per configurable"
											+ " parameters selected by user.",
									location));

						}
					}
					if (OnboardingConstant.AT_O.equalsIgnoreCase(atOutward)) {
						boolean isAttrValid = onboardAttributesChecking
								.isAttrValid(entityAtValMap,
										OnboardingConstant.PC, profitCentre,
										document.getEntityId());

						if (!isAttrValid) {

							errorLocations.add(GSTConstants.PROFITCENTRE);
							TransDocProcessingResultLoc location 
							      = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN1003",
									"Profit Centre not as per configurable"
											+ " parameters selected by user.",
									location));

						}
					}
				});
			}
			if (profitCentre != null && !profitCentre.isEmpty()) {
				String paramkeyId12 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I12.name();
				String paramterValue12 = util.valid(entityConfigParamMap,
						paramkeyId12, document.getEntityId());
				if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
						.equalsIgnoreCase(paramterValue12)) {

					onboardAttributesChecking = StaticContextHolder.getBean(
							"DataSecurityApplicabilityChecker",
							DataSecurityApplicabilityChecker.class);
					boolean isAttrUserValid = onboardAttributesChecking
							.isAttrValid(OnboardingConstant.PC, profitCentre,
									document.getEntityId(),
									document.getCreatedBy());
					if (!isAttrUserValid) {
						errorLocations.add(GSTConstants.PROFITCENTRE);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1137",
										"Profit Centre is not as per user "
										+ "access level"
												+ "  provided in during "
												+ "On-Boarding.",
										location));
					}
				}
			}
		});

		return errors;
	}

}
