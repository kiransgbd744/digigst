package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.jcraft.jsch.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("GSTR7TransCutOffRuleValidation")
public class GSTR7TransCutOffRuleValidation
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		String cutOffRetPeriod = (String) context
				.getAttribute("gstr7.transactional.cutOff");
		Integer cutOffRetPeriodInt = GenUtil
				.convertTaxPeriodToInt(cutOffRetPeriod);
		LOGGER.error("ReturnPeriod {} ", document.getReturnPeriod());
		Integer currentRetPeriodInt = GenUtil
				.convertTaxPeriodToInt(document.getReturnPeriod());

		if (currentRetPeriodInt < cutOffRetPeriodInt) {

			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);

			errors.add(new ProcessingResult(APP_VALIDATION, "ER63042",
					"Invalid template for the return period mentioned", location));

		}

		return errors;

	}
}
