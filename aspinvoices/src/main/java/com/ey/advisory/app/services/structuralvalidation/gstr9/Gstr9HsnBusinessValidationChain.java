package com.ey.advisory.app.services.structuralvalidation.gstr9;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Component("Gstr9HsnBusinessValidationChain")
public class Gstr9HsnBusinessValidationChain
		implements DocRulesValidatorChain<Gstr9HsnAsEnteredEntity> {

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final BusinessRuleValidator[] VALIDATORS = {

			new Gstr9GstnValidator(), new Gstr9HsnValidator(),
			new Gstr9RateValidator(),
			 new Gstr9FyValidator(),
            new Gstr9UqcValidator()

	};

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity prod,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (BusinessRuleValidator<Gstr9HsnAsEnteredEntity> validator : VALIDATORS) {
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
				if (validator != null) {
					List<ProcessingResult> tmpResults = validator.validate(prod,
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
				}
			} catch (Exception ex) {
				// create a processing result to inform that the validator
				// failed.
				String exName = ex.getClass().getSimpleName();
				String msg = String
						.format("Error while executing the validator '%s'. "
								+ "Exception: '%s'", validatorCls, exName);
				ProcessingResult result = new ProcessingResult("LOCAL",
						ProcessingResultType.ERROR, "ER9999", msg, null);
				results.add(result);
			}
		}
		return results;
	}
}
