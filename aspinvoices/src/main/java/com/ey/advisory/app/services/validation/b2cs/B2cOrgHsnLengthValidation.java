package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class B2cOrgHsnLengthValidation
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	private static final List<Integer> LENGTH1 = ImmutableList.of(4, 6, 8);
	private static final List<Integer> LENGTH2 = ImmutableList.of(6, 8);

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String hsnCode = document.getOrgHsnOrSac();
		if (Strings.isNullOrEmpty(hsnCode))
			return errors;
		hsnCode=hsnCode.trim();
		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
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
			errors.add(processingResult());
		}
		return errors;
	}

	private ProcessingResult processingResult() {
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.OrgHsnOrSac);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		return new ProcessingResult(APP_VALIDATION,
				"ER1278", "Length of entered HSN code is not valid as per AATO",
				location);

	}

}
