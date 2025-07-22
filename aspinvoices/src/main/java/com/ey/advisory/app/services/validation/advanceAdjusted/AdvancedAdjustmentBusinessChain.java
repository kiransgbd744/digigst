package com.ey.advisory.app.services.validation.advanceAdjusted;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("AdvancedAdjustmentBusinessChain")
public class AdvancedAdjustmentBusinessChain
		implements DocRulesValidatorChain<Gstr1AsEnteredTxpdFileUploadEntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AsEnteredTxpdFileUploadEntity.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final B2csBusinessRuleValidator[] VALIDATORS = {

			new AASupplierGstin(), new AAReturnPeriod(), new AAMonth(),
			new AAOrgPos(), new AAOrgRate(), new AAOrgGrossAdvAdjustment(),
			new AANewPos(), new AANewRate(), 
			new TxpdArthimatics(),
			new TxpdAmendmentsCheck(),
			new TxpdInter(),
			new TxpdIntra(),
			new TxpdIntraOrInter(),
			new TxpdRegTypeSez(),
			new TxpdTransType(),
			/* new AANewGrossAdvAdjustment(),
			 * new AACgstAmount(), new AASgstAmount()
			 */
			new TxpdProfitCenter(), new TxpdPlant(), new TxpdDivision(),
			new TxpdLocation(), new TxpdSalesOrg(), new TxpdDistributeChannel(),
			new TxpdUser1Access(), new TxpdUser2Access(), new TxpdUser3Access(),
			new TxpdUser4Access(), new TxpdUser5Access(),
			new TxpdUser6Access(),
			new AaIsGstr1ReturnFiled(), };

	@Override
	public List<ProcessingResult> validate(
			Gstr1AsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (B2csBusinessRuleValidator<Gstr1AsEnteredTxpdFileUploadEntity> validator : VALIDATORS) {
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
