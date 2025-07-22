package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("Gstr1AAdvancedAdjustmentBusinessChain")
public class Gstr1AAdvancedAdjustmentBusinessChain
		implements DocRulesValidatorChain<Gstr1AAsEnteredTxpdFileUploadEntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AsEnteredTxpdFileUploadEntity.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final B2csBusinessRuleValidator[] VALIDATORS = {

			new Gstr1AAASupplierGstin(), new Gstr1AAAReturnPeriod(),
			new Gstr1AAAMonth(), new Gstr1AAAOrgPos(), new Gstr1AAAOrgRate(),
			new Gstr1AAAOrgGrossAdvAdjustment(), new Gstr1AAANewPos(),
			new Gstr1AAANewRate(), new Gstr1ATxpdArthimatics(),
			new Gstr1ATxpdAmendmentsCheck(), new Gstr1ATxpdInter(), new Gstr1ATxpdIntra(),
			new Gstr1ATxpdIntraOrInter(), new Gstr1ATxpdRegTypeSez(), new Gstr1ATxpdTransType(),
			/*
			 * new AANewGrossAdvAdjustment(), new AACgstAmount(), new
			 * AASgstAmount()
			 */
			new Gstr1ATxpdProfitCenter(), new Gstr1ATxpdPlant(), new Gstr1ATxpdDivision(),
			new Gstr1ATxpdLocation(), new Gstr1ATxpdSalesOrg(), new Gstr1ATxpdDistributeChannel(),
			new Gstr1ATxpdUser1Access(), new Gstr1ATxpdUser2Access(), new Gstr1ATxpdUser3Access(),
			new Gstr1ATxpdUser4Access(), new Gstr1ATxpdUser5Access(), new Gstr1ATxpdUser6Access(),
			new Gstr1AAaIsGstr1ReturnFiled(), };

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> validator : VALIDATORS) {
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
