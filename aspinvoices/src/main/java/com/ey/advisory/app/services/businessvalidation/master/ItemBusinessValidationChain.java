package com.ey.advisory.app.services.businessvalidation.master;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.entities.client.MasterProductEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;


/**
 * 
 * @author Anand3.M
 *
 */
@Component("ItemBusinessValidationChain")
public class ItemBusinessValidationChain 
                   implements BusinessRuleValidator<MasterItemEntity> {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(ItemBusinessValidationChain.class);


// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
private static final BusinessRuleValidator[] VALIDATORS = {
		new ItemGstin(),
		new ItemHsn(),
		new ItemRate()
		
	
};

@Override
public List<ProcessingResult> validate(MasterItemEntity item,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	for(BusinessRuleValidator<MasterItemEntity> 
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
					validator.validate(item, context);
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