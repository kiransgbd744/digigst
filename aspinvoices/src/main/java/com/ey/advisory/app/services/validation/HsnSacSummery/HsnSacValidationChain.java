package com.ey.advisory.app.services.validation.HsnSacSummery;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;
import com.ey.advisory.app.data.entities.client.Gstr1HsnFileUploadEntity;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.services.validation.sales.SalesDocRulesValidatorChain;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
@Component("HsnSacValidationChain")
public class HsnSacValidationChain 
                   implements DocRulesValidatorChain<Gstr1HsnFileUploadEntity> {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(HsnSacValidationChain.class);


// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
private static final B2csBusinessRuleValidator[] VALIDATORS = {
		new HsnSupplierGstin(),
		new HsnReturnPeriod(),
		new HsnOrSac(),
		new Uom(),
		new Quantity(),
		new TaxableValue(),
		new Igst(),
		new Cgst(),
		new Sgst(),
		new Cess(),
		
		
		
};

@Override
public List<ProcessingResult> validate(Gstr1HsnFileUploadEntity document,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	for(B2csBusinessRuleValidator<Gstr1HsnFileUploadEntity> 
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
