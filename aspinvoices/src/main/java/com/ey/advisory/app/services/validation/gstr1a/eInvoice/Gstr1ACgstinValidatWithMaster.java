package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ACgstinValidatWithMaster")
public class Gstr1ACgstinValidatWithMaster
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;
		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;
		if (GSTConstants.URP.equalsIgnoreCase(document.getCgstin()))
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O6.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);

		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty()) {
			return errors;
		}

		if (!document.getIsCgstInMasterCust()) {
			if (paramtrvalue.equalsIgnoreCase(
					CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name())) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RecipientGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0146",
						"Recipient GSTIN not as per Customer "
								+ "Master provided at the time of On Boarding",
						location));
				return errors;
			}

			if (paramtrvalue.equalsIgnoreCase(
					CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name())) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RecipientGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN0008",
						"Recipient GSTIN not as per Customer "
								+ "Master provided at the time of On Boarding",
						location));
				return errors;
			}

		}

		return errors;

	}

}
