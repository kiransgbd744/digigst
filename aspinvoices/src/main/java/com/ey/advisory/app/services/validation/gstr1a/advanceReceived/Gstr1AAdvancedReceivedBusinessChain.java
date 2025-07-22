package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("Gstr1AAdvancedReceivedBusinessChain")
public class Gstr1AAdvancedReceivedBusinessChain
		implements DocRulesValidatorChain<Gstr1AAsEnteredAREntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AAdvancedReceivedBusinessChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final ARBusinessRuleValidator[] VALIDATORS = {
			new Gstr1AARSupplierGstin(), new Gstr1AARReturnPeriod(),
			new Gstr1AARMonth(), new Gstr1AAROrgPos(), new Gstr1AAROrgRate(),
			new Gstr1AARNewPos(), new Gstr1AARNewRate(),
			new Gstr1AArOrgGrossAR(), new Gstr1AARProfitCentre(),
			new Gstr1AARPlant(), new Gstr1AARDivision(), new Gstr1AARLocation(),
			new Gstr1AARSalesOrganization(), new Gstr1AARDistributionChannel(),
			new Gstr1AARIntraOrInter(), new Gstr1AARIntra(),
			new Gstr1AARInter(), new Gstr1AARRegTypeSez(),
			new Gstr1AARArthimatics(), new Gstr1AARArthimeticsAmendments(),
			new Gstr1AArTransType(), new Gstr1AARUserAccess1(),
			new Gstr1AARUserAccess2(), new Gstr1AARUserAccess3(),
			new Gstr1AARUserAccess4(), new Gstr1AARUserAccess5(),
			new Gstr1AARUserAccess6(), new Gstr1AArIsGstr1ReturnFiled(), };
	/* new ARCgstAmount(), new ARSgstAmount(), */

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> validator : VALIDATORS) {
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
