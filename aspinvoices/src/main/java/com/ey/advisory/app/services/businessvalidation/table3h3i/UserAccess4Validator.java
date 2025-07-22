package com.ey.advisory.app.services.businessvalidation.table3h3i;

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
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
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

/**
 * 
 * @author Mahesh.Golla
 *
 */
	public class UserAccess4Validator implements 
	BusinessRuleValidator<InwardTable3I3HExcelEntity>{
		@Autowired
		@Qualifier("DataSecurityApplicabilityChecker")
		private DataSecurityApplicabilityChecker onboardAttributesChecking;
		
		@Autowired
		@Qualifier("OnboardingQuestionValidationsUtil")
		private OnboardingQuestionValidationsUtil util;
		@Override
		public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
				ProcessingContext context) {
			List<ProcessingResult> errors = new ArrayList<>();
			Set<String> errorLocations = new HashSet<>();
			if (document.getSupplierGSTINorpan() == null || 
					document.getSupplierGSTINorpan().isEmpty())
				return errors;

			String paramkryId = CONFIG_PARAM_INWARD_QUE_KEY_ID.I4.name();
			util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
					OnboardingQuestionValidationsUtil.class);
			Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
					.getEntityConfigParamMap();
			String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
					document.getEntityId());
			Map<Long, List<Pair<String, String>>> entityAtValMap = document
					.getEntityAtValMap();
			String paramkeyId12 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I12.name();
			String paramterValue12 = util.valid(entityConfigParamMap, paramkeyId12,
					document.getEntityId());
			

			Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = document
					.getEntityAtConfMap();

					String ua4 = document.getUserAccess4();
					if(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
			                .equalsIgnoreCase(paramtrvalue)){
					onboardAttributesChecking = StaticContextHolder.getBean(
							"DataSecurityApplicabilityChecker",
							DataSecurityApplicabilityChecker.class);
					
					EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
							document.getEntityId(), OnboardingConstant.UD4);
					Map<Long, String> entitAtConfigInnerMap = entAtConfMap
							.get(entityAtConfigKey);
					entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {

						String atOutward = entitAtConfMap.getValue();
						if (OnboardingConstant.AT_M.equalsIgnoreCase(atOutward)) {
							boolean isAttrValid = onboardAttributesChecking
									.isAttrValid(entityAtValMap,
											OnboardingConstant.UD4, ua4,
											document.getEntityId());
							if(ua4==null || ua4.isEmpty()){
								errorLocations.add(GSTConstants.USER_ACCESS4);
								TransDocProcessingResultLoc location 
								      = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1258",
										"User Access 4 not as per configurable"
										+ " parameters selected by user.",
										location));
							}
							if (!isAttrValid) {
								
									errorLocations.add(GSTConstants.USER_ACCESS4);
									TransDocProcessingResultLoc location 
									      = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(APP_VALIDATION,
											"ER1258",
											"User Access 4 not as per configurable"
											+ " parameters selected by user.",
											location));
								
							
							}
						}
						if (OnboardingConstant.AT_O.equalsIgnoreCase(atOutward)) {
							boolean isAttrValid = onboardAttributesChecking
									.isAttrValid(entityAtValMap,
											OnboardingConstant.UD4, ua4,
											document.getEntityId());
							
							if (!isAttrValid) {
								
									errorLocations.add(GSTConstants.USER_ACCESS4);
									TransDocProcessingResultLoc location 
									      = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(APP_VALIDATION,
											ProcessingResultType.INFO, "IN1209",
											"User Access 4 not as per configurable"
											+ " parameters selected by user.",
											location));
								
							
							}
						}
					});
				
					}
		if (ua4 != null && !ua4.isEmpty()) {

			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramterValue12)) {
				boolean isAttrUserValid = onboardAttributesChecking.isAttrValid(
						OnboardingConstant.PC, ua4, document.getEntityId(),
						document.getCreatedBy());
				if (!isAttrUserValid) {
					errorLocations.add(GSTConstants.USER_ACCESS4);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1269",
							"User Access 4 is not as per user access level"
									+ "  provided in during On-Boarding.",
							location));
				}
			}
		}
		return errors;
	}

}