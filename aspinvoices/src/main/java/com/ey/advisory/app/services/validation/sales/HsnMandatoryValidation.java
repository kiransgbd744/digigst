package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Siva.Nandam
 *
 */
public class HsnMandatoryValidation
		implements DocRulesValidator<OutwardTransDocument> {

	
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O11.name();
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		//String paramtrvalue = util.valid(paramkryId, document.getSgstin());
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		if(!GSTConstants.
				 NON.equalsIgnoreCase(document.getSupplyType())) return errors;

		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			if (paramtrvalue == null || paramtrvalue.isEmpty() 
					|| CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramtrvalue)) {
				if (item.getHsnSac() == null || item.getHsnSac().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					TransDocProcessingResultLoc location 
					            = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0070",
							"HSN or SAC cannot be left blank.", location));

				}

				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
					int hsn = Integer.parseInt(item.getHsnSac());
					if (hsn == 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(HSNORSAC);
						TransDocProcessingResultLoc location 
						         = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0070", "HSN or SAC cannot be left blank.",
								location));
					}
				}
			}
			

		});

		return errors;
	}

}
