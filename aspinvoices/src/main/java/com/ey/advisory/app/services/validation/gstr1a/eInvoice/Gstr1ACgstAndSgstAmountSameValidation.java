package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ACgstAndSgstAmountSameValidation")
public class Gstr1ACgstAndSgstAmountSameValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();

		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (DOC_TYPE_IMPORTS
					.contains(trimAndConvToUpperCase(document.getDocType()))
					&& GSTConstants.Y
							.equalsIgnoreCase(document.getCrDrPreGst()))

				return errors;

			String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O43.name();

			OnboardingQuestionValidationsUtil util = StaticContextHolder
					.getBean("OnboardingQuestionValidationsUtil",
							OnboardingQuestionValidationsUtil.class);
			Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
					.getEntityConfigParamMap();
			String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
					document.getEntityId());

			/**
			 * Here we are getting rates from Cilent and comparing those amounts
			 * like cgst and sgst Amounts
			 */
			IntStream.range(0, items.size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = items.get(idx);
				BigDecimal cgstAmount = item.getCgstAmount();
				BigDecimal sgstAmount = item.getSgstAmount();

				if (cgstAmount == null) {
					cgstAmount = BigDecimal.ZERO;
				}
				if (sgstAmount == null) {
					sgstAmount = BigDecimal.ZERO;
				}
				BigDecimal taxAmount = (cgstAmount.compareTo(sgstAmount) > 0)
						? cgstAmount.subtract(sgstAmount).abs()
						: sgstAmount.subtract(cgstAmount).abs();

				if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
						.equalsIgnoreCase(paramtrvalue)
						&& taxAmount.compareTo(BigDecimal.ZERO) > 0) {

					errorLocations.add(GSTConstants.cgstamount);
					errorLocations.add(GSTConstants.sgstamount);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5936",
							"CGST & SGST amounts cannot be different",
							location));

				} else {
					if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
						errorLocations.add(GSTConstants.cgstamount);
						errorLocations.add(GSTConstants.sgstamount);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0501",
								"CGST and SGST Amounts are Different",
								location));
					}
				}
			});
		}
		return errors;
	}
}
