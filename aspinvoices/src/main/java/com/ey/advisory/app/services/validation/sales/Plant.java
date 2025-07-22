package com.ey.advisory.app.services.validation.sales;

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
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
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
import com.ey.advisory.common.multitenancy.TenantContext;


/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Plant")
public class Plant implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DataSecurityApplicabilityChecker")
	private DataSecurityApplicabilityChecker onboardAttributesChecking;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O5.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		String paramkeyId12 = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O12.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		
		String paramterValue12 = util.valid(entityConfigParamMap, paramkeyId12,
				document.getEntityId());
		
		Map<Long, List<Pair<String, String>>> entityAtValMap = document
				.getEntityAtValMap();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = document
				.getEntityAtConfMap();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
				String plant = item.getPlantCode();
				if(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
		                .equalsIgnoreCase(paramtrvalue)){
				onboardAttributesChecking = StaticContextHolder.getBean(
						"DataSecurityApplicabilityChecker",
						DataSecurityApplicabilityChecker.class);
				
				EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
						document.getEntityId(), OnboardingConstant.PLANT);
				Map<Long, String> entitAtConfigInnerMap = entAtConfMap
						.get(entityAtConfigKey);
				entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {

					String atOutward = entitAtConfMap.getValue();
					if (OnboardingConstant.AT_M.equalsIgnoreCase(atOutward)) {
						boolean isAttrValid = onboardAttributesChecking
								.isAttrValid(entityAtValMap,
										OnboardingConstant.PLANT, plant,
										document.getEntityId());
						if(plant==null || plant.isEmpty()){
							errorLocations.add(GSTConstants.PLANT);
							TransDocProcessingResultLoc location 
							      = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0127",
									" Plant is not as per master provided "
									+ "during On-Boarding.",
									location));
						}
						if (!isAttrValid) {
							
								errorLocations.add(GSTConstants.PLANT);
								TransDocProcessingResultLoc location 
								      = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0127",
										" Plant is not as per master provided "
										+ "during On-Boarding.",
										location));
							
						
						}
					}
					if (OnboardingConstant.AT_O.equalsIgnoreCase(atOutward)) {
						boolean isAttrValid = onboardAttributesChecking
								.isAttrValid(entityAtValMap,
										OnboardingConstant.PLANT, plant,
										document.getEntityId());
						
						if (!isAttrValid) {
							
								errorLocations.add(GSTConstants.PLANT);
								TransDocProcessingResultLoc location 
								      = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0011",
										"Plant is not as per master provided "
										+ "during On-Boarding.",
										location));
							
						
						}
					}
				});
				}
          if(plant!=null && !plant.isEmpty()){
					
					if(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
							          .equalsIgnoreCase(paramterValue12)){
						onboardAttributesChecking = StaticContextHolder.getBean(
								"DataSecurityApplicabilityChecker",
								DataSecurityApplicabilityChecker.class);
					boolean isAttrUserValid = onboardAttributesChecking
							.isAttrValid(
							OnboardingConstant.PLANT,
							plant,document.getEntityId(),document.getCreatedBy()); 
					if(!isAttrUserValid){
						errorLocations.add(GSTConstants.PLANT);
						TransDocProcessingResultLoc location 
						      = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								 "ER0154",
								"Plant is not as per user access level"
								+ "  provided in during On-Boarding.",
									location));
						}
					}
				}
		});

		return errors;
	}

}
