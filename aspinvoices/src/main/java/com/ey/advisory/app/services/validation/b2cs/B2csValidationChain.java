package com.ey.advisory.app.services.validation.b2cs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.app.services.validation.eInvoice.HsnLengthValidation;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
@Component("B2csValidationChain")
public class B2csValidationChain 
                   implements DocRulesValidatorChain<Gstr1AsEnteredB2csEntity> {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(B2csValidationChain.class);


// Maintain a list of validators that can be invoked in sequence.
// This can be the preliminary implementation. Later on we can have
// more complex rule ordering and rule evaluation decisions stored in the
// DB and invoked for the list of validations.
private static final B2csBusinessRuleValidator[] VALIDATORS = {
		new SupplierGstin(),
		new ReturnPeriod(),
		//new TransactionType(),
		new Month(),
		new OrgPos(),
		new OrgHsnOrSac(),
		new B2csOrgHsnCodeLengthValidation(),
		new OrgUom(),
		new NewUom(),
		new OrgRate(),
		new OrgEcomSupValue(),
		new NewPos(),
		new NewHSNorSAC(),
		new B2csNewHsnCodeLengthValidation(),
		new OrgNewRate(),
		new NewEcomSupValue(),
		new B2csIntraOrInter(),
		new B2csIntra(),
		new B2csInter(),
		new B2csRegTypeSez(),
		/*new B2csEcomGstinTcs(),*/
		new B2csArthimatics(),
		new AmendmentsCheck(),		
		/*
		 new CgstAmount(),
		new SgstAmount(),*/
		new B2csProfitCenter(),
		new B2csPlant(),
		new B2csDivision(),
		new B2csLocation(),
		new B2csSalesOrg(),
		new B2csDistributeChannel(),
		new B2csUser1Access(),
		new B2csUser2Access(),
		new B2csUser3Access(),
		new B2csUser4Access(),
		new B2csUser5Access(),
		new B2csUser6Access(),
		new B2csIsGstr1ReturnFiled(),
		new B2cOrgHsnLengthValidation(),
		new B2cNewHsnLengthValidation(),
};

@Override
public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
		ProcessingContext context) {
	List<ProcessingResult> results = new ArrayList<>();
	for(B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> 
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
