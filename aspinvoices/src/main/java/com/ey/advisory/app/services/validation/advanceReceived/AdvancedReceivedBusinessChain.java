package com.ey.advisory.app.services.validation.advanceReceived;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("AdvancedReceivedBusinessChain")
public class AdvancedReceivedBusinessChain
		implements DocRulesValidatorChain<Gstr1AsEnteredAREntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AdvancedReceivedBusinessChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final ARBusinessRuleValidator[] VALIDATORS = {
			new ARSupplierGstin(), new ARReturnPeriod(), new ARMonth(),
			new AROrgPos(), new AROrgRate(), new ARNewPos(), new ARNewRate(),
			new ArOrgGrossAR(), new ARProfitCentre(), new ARPlant(),
			new ARDivision(), new ARLocation(), new ARSalesOrganization(),
			new ARDistributionChannel(), new ARIntraOrInter(), new ARIntra(),
			new ARInter(), new ARRegTypeSez(), new ARArthimatics(),
			new ARArthimeticsAmendments(), new ArTransType(),
			new ARUserAccess1(), new ARUserAccess2(), new ARUserAccess3(),
			new ARUserAccess4(), new ARUserAccess5(), new ARUserAccess6(),
			new ArIsGstr1ReturnFiled(), };
	/*new ARCgstAmount(), new ARSgstAmount(),*/

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (ARBusinessRuleValidator<Gstr1AsEnteredAREntity> validator : VALIDATORS) {
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
				ProcessingResult result = new ProcessingResult("LOCAL",
						ProcessingResultType.ERROR, "ER9999", msg, null);
				results.add(result);
			}
		}
		return results;
	}

}
