package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class CmEligibilityIndicator 
                         implements DocRulesValidator<InwardTransDocument> {

	
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document, 
			                                      ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.I15;
		util = StaticContextHolder
				.getBean("OnboardingQuestionValidationsUtil", 
						OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;
		if(GSTConstants.A.equalsIgnoreCase(paramtrvalue) 
				|| GSTConstants.C.equalsIgnoreCase(paramtrvalue)){
			IntStream.range(0, items.size()).forEach(idx -> {
				InwardTransDocLineItem item = items.get(idx);
				
				if(item.getEligibilityIndicator()==null){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
					TransDocProcessingResultLoc location 
					                      = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							 "ER1100",
							"Eligibility Indicator cannot be left blank.",
							location));
				}
			});
		}
		return errors;
	}

}
