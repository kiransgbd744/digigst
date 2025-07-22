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
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;


	public class UserAccess1Validator implements 
	BusinessRuleValidator<InwardTable3I3HExcelEntity>{

		@Autowired
		@Qualifier("DataSecurityApplicabilityChecker")
		private DataSecurityApplicabilityChecker onboardAttributesChecking;
		
		@Autowired
		@Qualifier("OnboardingQuestionValidationsUtil")
		private OnboardingQuestionValidationsUtil util;

		@Override
		public List<ProcessingResult> 
		validate(InwardTable3I3HExcelEntity document,
				ProcessingContext context) {
			List<ProcessingResult> errors = new ArrayList<>();
			Set<String> errorLocations = new HashSet<>();
			@SuppressWarnings("unused")
			String groupCode = TenantContext.getTenantId();
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
			String paramkeyId12 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I12.name();
			String paramterValue12 = util.valid(entityConfigParamMap, paramkeyId12,
					document.getEntityId());
			Map<Long, List<Pair<String, String>>> entityAtValMap = document
					.getEntityAtValMap();

			

			Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = document
					.getEntityAtConfMap();

					String ua1 = document.getUserAccess1();
					if(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
			                .equalsIgnoreCase(paramtrvalue)){
					onboardAttributesChecking = StaticContextHolder.getBean(
							"DataSecurityApplicabilityChecker",
							DataSecurityApplicabilityChecker.class);
					
					EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
							document.getEntityId(), OnboardingConstant.UD1);
					Map<Long, String> entitAtConfigInnerMap = entAtConfMap
							.get(entityAtConfigKey);
					entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {

						String atOutward = entitAtConfMap.getValue();
						if (OnboardingConstant.AT_M.equalsIgnoreCase(atOutward)) {
							boolean isAttrValid = onboardAttributesChecking
									.isAttrValid(entityAtValMap,
											OnboardingConstant.UD1, ua1,
											document.getEntityId());
							if(ua1==null || ua1.isEmpty()){
								errorLocations.add(GSTConstants.USER_ACCESS1);
								TransDocProcessingResultLoc location 
								      = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1255",
										"User Access 1 not as per configurable "
										+ "parameters selected by user.",
										location));
							}
							if (!isAttrValid) {
								
									errorLocations.add(GSTConstants.USER_ACCESS1);
									TransDocProcessingResultLoc location 
									      = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(APP_VALIDATION,
											"ER1255",
											"User Access 1 not as per configurable "
											+ "parameters selected by user.",
											location));
								
							
							}
						}
						if (OnboardingConstant.AT_O.equalsIgnoreCase(atOutward)) {
							boolean isAttrValid = onboardAttributesChecking
									.isAttrValid(entityAtValMap,
											OnboardingConstant.UD1, ua1,
											document.getEntityId());
							
							if (!isAttrValid) {
								
									errorLocations.add(GSTConstants.USER_ACCESS1);
									TransDocProcessingResultLoc location 
									      = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(APP_VALIDATION,
											ProcessingResultType.INFO, "IN1206",
											"User Access 1 not as per configurable "
											+ "parameters selected by user.",
											location));
								
							
							}
						}
					});
					}
					 if(ua1!=null && !ua1.isEmpty()){
							
							if(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
									          .equalsIgnoreCase(paramterValue12)){
							boolean isAttrUserValid = onboardAttributesChecking
									.isAttrValid(
									OnboardingConstant.PC,
									ua1,document.getEntityId(),
									document.getCreatedBy()); 
							if(!isAttrUserValid){
								errorLocations.add(GSTConstants.USER_ACCESS1);
								TransDocProcessingResultLoc location 
								      = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										 "ER1266",
										"User Access 1 is not as per user access level"
										+ "  provided in during On-Boarding.",
											location));
								}
							}
						}

			return errors;
		}

	}
