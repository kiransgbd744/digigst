package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
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
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ACancelationReasonValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	private static final List<Integer> CAN_REASON = ImmutableList.of(

			1, 2, 3, 4);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
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
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramtrvalue)) {
				errorLocations.add(GSTConstants.CAN_REASON);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15169",
						"CancellationReason Cannot be left blank", location));
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("1");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("2");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("3");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.E.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("4");
			}
			return errors;
		} else {
			try {
				int canReason = Integer.parseInt(cancellationReason);

				if (!CAN_REASON.contains(canReason)) {

					errorLocations.add(GSTConstants.CAN_REASON);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15008",
							"Invalid Cancellation reason", location));
					return errors;
				}
			} catch (Exception e) {
				errorLocations.add(GSTConstants.CAN_REASON);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15008",
						"Invalid Cancellation reason", location));
				return errors;

			}
		}
		return errors;
	}

}
