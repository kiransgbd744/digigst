package com.ey.advisory.app.services.businessvalidation.ret1and1a;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;


/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Ret1And1ABusinessValidationChain")
public class Ret1And1ABusinessValidationChain 
                   implements DocRulesValidatorChain<Ret1And1AExcelEntity> {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(Ret1And1ABusinessValidationChain.class);


// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
@SuppressWarnings("rawtypes")
private static final BusinessRuleValidator[] VALIDATORS = {
		new Ret1And1ASupplierGstin(),
		new Ret1And1AReturnPeriod(),
		new Ret1And1AReturnTable(),
		new Ret1And1ASection3NoAtx(),
		new Ret1And1AProfitCenter(),
		new Ret1And1APlant(),
		new Ret1And1ADivision(),
		new Ret1And1ALocation(),
		new Ret1And1ASalesOrg(),
		new Ret1And1ADistributeChannel(),
		new Ret1And1AUser1Access(),
		new Ret1And1AUser2Access(),
		new Ret1And1AUser3Access(),
		new Ret1And1AUser4Access(),
		new Ret1And1AUser5Access(),
		new Ret1And1AUser6Access()
};

@SuppressWarnings("unchecked")
@Override
public List<ProcessingResult> validate(Ret1And1AExcelEntity document,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	for(BusinessRuleValidator<Ret1And1AExcelEntity> 
				validator: VALIDATORS) {
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
				String msg = String.format(
						"Executing validator '%s'", validatorCls);
				LOGGER.debug(msg);
			}
			List<ProcessingResult> tmpResults = 
					validator.validate(document, context);
			results.addAll(tmpResults);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executed validator '%s'. No: of Results "
						+ "available: '%d'", validatorCls, 
						(tmpResults != null) ? tmpResults.size() : 0);
				LOGGER.debug(msg);
			}
		} catch(Exception ex) {
			// create a processing result to inform that the validator
			// failed.
			String exName = ex.getClass().getSimpleName();
			String msg = String.format(
					"Error while executing the validator '%s'. "
					+ "Exception: '%s'", 
						validatorCls, exName);
			ProcessingResult result = new ProcessingResult(
					"LOCAL", ProcessingResultType.ERROR, "ER9999", 
					msg, null);
			results.add(result);
		}
	}
	return results;
}
}
