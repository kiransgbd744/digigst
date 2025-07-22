package com.ey.advisory.app.services.businessvalidation.b2c;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;


/***
 * 
 * @author Mahesh.Golla
 *
 */
@Component("B2cBusinessValidationChain")
public class B2cBusinessValidationChain 
                   implements DocRulesValidatorChain<OutwardB2cExcelEntity> {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(B2cBusinessValidationChain.class);


// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
private static final BusinessRuleValidator[] VALIDATORS = {
		new B2cSupplierGstin(),
		new B2cReturnPeriod(),
		//new B2cDocType(),
		new B2cPos(),
		new B2cOrgHsnOrSac(),
		new B2cStateCessAppCess(),
		new B2cEcomGstin(),
		new B2cEcomValOfSuppMade(),
		new B2cEcomNetValOfSupp(),
		new B2cIntraOrInter(),
		new B2cIntra(),
		new B2cInter(),
		new B2cRegTypeSez(),
		//new B2cEcomGstinTcs(),
		new B2cArthimatics(),
		new B2cProfitCenter(),
		new B2cPlant(),
		new B2c4Division(),
		new B2cLocation(),
		new B2cSalesOrg(),
		new B2cDistributeChannel(),
		new B2cUser1Access(),
		new B2cUser2Access(),
		new B2cUser3Access(),
		new B2cUser4Access(),
		new B2cUser5Access(),
		new B2cUser6Access(),
		new LuB2cRateValidator()
};

@Override
public List<ProcessingResult> validate(OutwardB2cExcelEntity b2cs,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	for(BusinessRuleValidator<OutwardB2cExcelEntity> 
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
					validator.validate(b2cs, context);
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
