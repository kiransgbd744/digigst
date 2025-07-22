package com.ey.advisory.app.services.businessvalidation.table3h3i;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("Table3h3iValidationChain")
public class Table3h3iValidationChain
		implements DocRulesValidatorChain<InwardTable3I3HExcelEntity> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Table3h3iValidationChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	private static final BusinessRuleValidator[] VALIDATORS = {
			new RecipientGSTINValidator(), new ReturnPeriodValidator(), 
			/*new DocumentTypeValidator(),*/ new SGSTINorPANValidator(),
			new POSValidator(), new HsnSavValidator(), new RateValidator(),
			new AvailableIGSTValidator(), new AvailableCGSTValidator(),
			new AvailableSGSTValidator(), new AvailableCessValidator(),
			new IntraOrInter(),new InwardIntra(),new InwardInter(),
			new ImpsValidator(),new RcValidator(),new RcTranFlag(),
			new SupTypeSez(),new Arithimetic3H3I(),
			new ProfitCentreValidator(), new PlantValidator(),
			new DivisionValidator(), new PurchaseOrganisationValidator(),
			new UserAccess1Validator(), new UserAccess2Validator(),
			new UserAccess3Validator(), new UserAccess4Validator(),
			new UserAccess5Validator(), new UserAccess6Validator(),
			new LocationValidator() };

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		for (BusinessRuleValidator<InwardTable3I3HExcelEntity> 
		validator : VALIDATORS) {
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