package com.ey.advisory.app.services.validation.eInvoice;
/**
 * Siva krishna
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.LUHsnOrSacValidator;
import com.ey.advisory.app.services.validation.sales.LuCgstRateValidator;
import com.ey.advisory.app.services.validation.sales.LuIgstRateValidator;
import com.ey.advisory.app.services.validation.sales.LuSgstRateValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

@Component("EinvoiceExtraSalesDocRulesValidatorChain")
@Slf4j
public class EinvoiceExtraSalesDocRulesValidatorChain
		implements DocRulesValidatorChain<OutwardTransDocument> {

	
	
	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.

			
			private static final DocRulesValidator[] EWB = {
					new LuDocCategoryValidation(), new DocTypeMasterValidation(),
					new EInvoiceSgstinValidation(), new LuEwbCgstinValidation(),
					new CmDocCatogeryValdaton(), new LUHsnOrSacValidator(),
					new EinvoiceUqcValidator(), new EInvoiceQuantityValidation(),
					new LuIgstRateValidator(), new LuCgstRateValidator(),
					new LuSgstRateValidator(), new LuTransactionTypeValidation(),
					new SuSupplyTypeValidation(), new CmOtherSupplyDesc(),
				//	new LuTransportValidation(),relaxing
					new TransDocNoAndTransDocDateValidator(),
					new VehicleNovalidation(),
					//manDatory
					new DocCatoryManDatory(),
					new SupplierStateCodeMandatory(),
					new CustomerStateCodeMandatory(),
					new HsnMandatory(),
					new ItemAssessableAmountMandatory(),
					new InvoiceValueMandatory(),
					new TransactionTypeMandatory(),
					new SubSupplyTypeMandatory(),
					
			
			
	};
	
		
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> results = new ArrayList<>();
		
		DocRulesValidator[] VALIDATORS = EWB;
				
		for(DocRulesValidator<OutwardTransDocument> 
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
				LOGGER.error(msg);
				results.add(result);
			}
		}
		return results;
	}

}
