package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

@Component("Gstr1ALURecipientGSTINValidation")
public class Gstr1ALURecipientGSTINValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (Strings.isNullOrEmpty(document.getSgstin())
				|| Strings.isNullOrEmpty(document.getCgstin())
				|| GSTConstants.I
						.equalsIgnoreCase(document.getTransactionType())
				|| GSTConstants.URP.equalsIgnoreCase(document.getCgstin()))
			return errors;
		String cgstin = document.getCgstin();
		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		String regex2 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][UOuo]"
				+ "[Nn][A-Za-z0-9]$";

		String regex3 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][Nn]"
				+ "[Rr][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Pattern pattern1 = Pattern.compile(regex1);

		Pattern pattern2 = Pattern.compile(regex2);

		Pattern pattern3 = Pattern.compile(regex3);

		Matcher matcher = pattern.matcher(cgstin.trim());
		Matcher matcher1 = pattern1.matcher(cgstin.trim());
		Matcher matcher2 = pattern2.matcher(cgstin.trim());
		Matcher matcher3 = pattern3.matcher(cgstin.trim());
		if (matcher.matches() || matcher1.matches() || matcher2.matches()
				|| matcher3.matches()) {

		} else {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RecipientGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER0048",
					"Invalid Recipient GSTIN.", location));
			return errors;
		}

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O6.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		if (paramtrvalue == null || paramtrvalue.isEmpty() || paramtrvalue
				.equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name())) {
			stateCache = StaticContextHolder.getBean("DefaultStateCache",
					StateCache.class);

			String statecode = document.getCgstin().substring(0, 2);
			int n = stateCache.findStateCode(statecode);
			if (n <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RecipientGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0048",
						"Invalid Recipient GSTIN.", location));
				return errors;
			}

		}
		return errors;
	}
}
