package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
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
public class RemarksMandatoryValidation
		implements DocRulesValidator<OutwardTransDocument> {

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

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O15.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		String cancellationReason = document.getCancellationReason();
		if (cancellationReason == null || cancellationReason.isEmpty()) {
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.E.name()
					.equalsIgnoreCase(paramtrvalue)) {
				if (document.getInvoiceRemarks() == null
						|| document.getInvoiceRemarks().isEmpty()) {
					errorLocations.add(GSTConstants.REMARK);
					TransDocProcessingResultLoc location 
					       = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15170",
							"Invalid Cancellation reason", location));
					return errors;
				}
			}
			return errors;
		} else {
			try {
				int canReason = Integer.parseInt(cancellationReason);

				if (canReason == 4) {
					if (document.getCancellationRemarks() == null
							|| document.getCancellationRemarks().isEmpty()) {
						errorLocations.add(GSTConstants.REMARK);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER15170", "CancellationRemarks Cannot be left blank",
								location));
						return errors;
					}

				}
			} catch (Exception e) {

			}
		}
		return errors;
	}

}
