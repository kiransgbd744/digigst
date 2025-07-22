package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("Gstr1AB2csValidationChain")
public class Gstr1AB2csValidationChain
		implements DocRulesValidatorChain<Gstr1AAsEnteredB2csEntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AB2csValidationChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final B2csBusinessRuleValidator[] VALIDATORS = {
			new Gstr1ASupplierGstin(), new Gstr1AReturnPeriod(),
			// new TransactionType(),
			new Gstr1AMonth(), new Gstr1AOrgPos(), new Gstr1AOrgHsnOrSac(),
			new Gstr1AB2csOrgHsnCodeLengthValidation(), new Gstr1AOrgUom(), new Gstr1ANewUom(),
			new Gstr1AOrgRate(), new Gstr1AOrgEcomSupValue(), new Gstr1ANewPos(),
			new Gstr1ANewHSNorSAC(), new Gstr1AB2csNewHsnCodeLengthValidation(),
			new Gstr1AOrgNewRate(), new Gstr1ANewEcomSupValue(), new Gstr1AB2csIntraOrInter(),
			new Gstr1AB2csIntra(), new Gstr1AB2csInter(), new Gstr1AB2csRegTypeSez(),
			/* new B2csEcomGstinTcs(), */
			new Gstr1AB2csArthimatics(), new Gstr1AAmendmentsCheck(),
			/*
			 * new CgstAmount(), new SgstAmount(),
			 */
			new Gstr1AB2csProfitCenter(), new Gstr1AB2csPlant(), new Gstr1AB2csDivision(),
			new Gstr1AB2csLocation(), new Gstr1AB2csSalesOrg(), new Gstr1AB2csDistributeChannel(),
			new Gstr1AB2csUser1Access(), new Gstr1AB2csUser2Access(), new Gstr1AB2csUser3Access(),
			new Gstr1AB2csUser4Access(), new Gstr1AB2csUser5Access(), new Gstr1AB2csUser6Access(),
			new Gstr1AB2csIsGstr1ReturnFiled(), new Gstr1AB2cOrgHsnLengthValidation(),
			new Gstr1AB2cNewHsnLengthValidation(), };

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> validator : VALIDATORS) {
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
