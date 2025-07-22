package com.ey.advisory.app.services.validation.InvoiceFile;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("InvoiceFileValidationChain")
public class InvoiceFileValidationChain
		implements DocRulesValidatorChain<Gstr1AsEnteredInvEntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InvoiceFileValidationChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final B2csBusinessRuleValidator[] VALIDATORS = {
			new INSupplierGstin(), new INReturnPeriod(), new NatureOfDocument(),
			new CanAndTotalVal(), new NetIssueVal(), new From(),
			new ToValidation(), new SerialNoAndNatureOfDescription(),
			new INIsGstr1ReturnFiled(),

	};

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (B2csBusinessRuleValidator<Gstr1AsEnteredInvEntity> validator : VALIDATORS) {
			if (validator == null) {
				LOGGER.warn("Validator cannot be null. "
						+ "Ignoring the null validator!!");
				continue;
			}
			// Get the class name of the validator, for logging in case of
			// any errors.
			String validatorCls = validator.getClass().getSimpleName();
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Executing validator '%s'",
							validatorCls);
					LOGGER.debug(msg);
				}
				List<ProcessingResult> tmpResults = validator.validate(document,
						context);
				results.addAll(tmpResults);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed validator '%s'. No: of Results "
									+ "available: '%d'",
							validatorCls,
							(tmpResults != null) ? tmpResults.size() : 0);
					LOGGER.debug(msg);
				}

			} catch (Exception ex) {
				// create a processing result to inform that the validator
				// failed.
				String exName = ex.getClass().getSimpleName();
				String msg = String
						.format("Error while executing the validator '%s'. "
								+ "Exception: '%s'", validatorCls, exName);
				LOGGER.error(msg, ex);
				ProcessingResult result = new ProcessingResult("LOCAL",
						ProcessingResultType.ERROR, "ER9999", msg, null);
				results.add(result);
			}
		}
		return results;
	}

}
