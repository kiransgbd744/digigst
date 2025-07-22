package com.ey.advisory.app.services.validation.gstr1a.NilNonExpt;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class Gstr1ANilNonExptHsnLengthValidation implements
		B2csBusinessRuleValidator<Gstr1ANilNonExemptedAsEnteredEntity> {

	private static final List<Integer> LENGTH1 = ImmutableList.of(4, 6, 8);
	private static final List<Integer> LENGTH2 = ImmutableList.of(6, 8);

	@Override
	public List<ProcessingResult> validate(
			Gstr1ANilNonExemptedAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String hsn = document.getHsn();
		if (Strings.isNullOrEmpty(hsn))
			return errors;
		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name();

		OnboardingQuestionValidationsUtil util = StaticContextHolder.getBean(
				"OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String answer = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		if (((Strings.isNullOrEmpty(answer)
				|| GSTConstants.A.equalsIgnoreCase(answer))
				&& !LENGTH1.contains(hsn.length()))
				|| (GSTConstants.B.equalsIgnoreCase(answer)
						&& !LENGTH2.contains(hsn.length()))) {
			errorLocations.add(HSNORSAC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1278",
					"Length of entered HSN code is not valid as per AATO.",
					location));
		}

		return errors;
	}

}
