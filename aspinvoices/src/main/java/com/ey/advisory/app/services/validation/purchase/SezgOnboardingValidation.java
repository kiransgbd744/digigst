package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
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
import com.google.common.base.Strings;

public class SezgOnboardingValidation
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String portCode = document.getPortCode();
		String billOfEntryNo = document.getBillOfEntryNo();
		LocalDate billOfEntryDate = document.getBillOfEntryDate();
		if (!Strings.isNullOrEmpty(portCode)
				&& !Strings.isNullOrEmpty(billOfEntryNo)
				&& billOfEntryDate != null)
			return errors;
		List<InwardTransDocLineItem> items = document.getLineItems();
		String paramkryId = GSTConstants.I37; 
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty() 
				|| GSTConstants.B.equalsIgnoreCase(paramtrvalue))
			return errors;
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			String supplyType = item.getSupplyType();
			if (GSTConstants.SEZG.equalsIgnoreCase(supplyType)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SupplyType);
				TransDocProcessingResultLoc location 
				                      = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						 "ER1275",
						"For SEZ Goods supply, Bill of entry details "
						+ "(Port Code, BOE No. & BOE Date) cannot be left blank",
						location));
			}
		});
		return errors;
	}

}
