package com.ey.advisory.app.services.structuralvalidation.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;


/***
 * 
 * @author Siva Krishna
 *
 */
@Slf4j
@Component("Gstr6BusinessValidationChain")
public class Gstr6BusinessValidationChain 
                   implements DocRulesValidatorChain<Gstr6DistributionExcelEntity> {
	

// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
private static final BusinessRuleValidator[] VALIDATORS = {
		new Gstr6ReturnPeriodValidator(),
		new Gstr6CgstinPosValidation(),
		new Gstr6StateCodeValidator(),
		new Gstr6OrgCgstinPosValidation(),
		new Gstr6OrgStateCodeValidator(),
		new Gstr6DocTypeValidation(),
		new Gstr6SupplyTypeValidation(),
		new CMOriginalCreditNoteNumAndDate(),
		new Gstr6EligibilityIndicator(),
		new Gstr6TaxMandatory(),
		new Gstr6OriginalDocNumAndDate(),
	    new Gstr6CMDocumentDateValidator(),
		new Gstr6CmOrgDocDateValidator(),
		new Gstr6OriginalCreditNoteDate(),
		new Gstr6CmDocdateValidator(),
		new Gstr6CmOriginalDocDateValidator(),
		new Gstr6CmCrDocDateValidator(),
		new Gstr6LuGstinValidation(),
		//new Gstr6CanLookUpValidation(),
		new IsGstr6DitrubutionReturnFiled(),
		new Gstr6OriginalDocNumValidator(),
		new Gstr6EligibilityIndicatorMatchValidator(),
		new Gstr6RgstinValidation(),
		
};

@Override
public List<ProcessingResult> validate(Gstr6DistributionExcelEntity gstr6,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	if(GSTConstants.CAN.equalsIgnoreCase(gstr6.getSupplyType())) return results;
	for(BusinessRuleValidator<Gstr6DistributionExcelEntity> 
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
					validator.validate(gstr6, context);
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
