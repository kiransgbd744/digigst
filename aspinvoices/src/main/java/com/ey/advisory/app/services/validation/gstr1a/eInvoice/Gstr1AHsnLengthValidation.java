package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class Gstr1AHsnLengthValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<Integer> LENGTH1 = ImmutableList.of(4, 6, 8);
	private static final List<Integer> LENGTH2 = ImmutableList.of(6, 8);
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);
			String hsnCode = item.getHsnSac();
			if (!Strings.isNullOrEmpty(hsnCode)) {
				String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name();
				util = StaticContextHolder.getBean(
						"OnboardingQuestionValidationsUtil",
						OnboardingQuestionValidationsUtil.class);
				Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
						.getEntityConfigParamMap();
				String answer = util.valid(entityConfigParamMap, paramkryId,
						document.getEntityId());
				if (((Strings.isNullOrEmpty(answer)
						|| GSTConstants.A.equalsIgnoreCase(answer))
						&& !LENGTH1.contains(hsnCode.length()))
						|| (GSTConstants.B.equalsIgnoreCase(answer)
								&& !LENGTH2.contains(hsnCode.length()))) {
					errors.add(processingResult(idx));
				}
			}
		});
		return errors;

	}

	private ProcessingResult processingResult(Integer idx) {
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.HSNORSAC);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				idx, errorLocations.toArray());
		return new ProcessingResult(APP_VALIDATION, "ER1278",
				"Length of entered HSN code is not valid as per AATO",
				location);

	}

}
