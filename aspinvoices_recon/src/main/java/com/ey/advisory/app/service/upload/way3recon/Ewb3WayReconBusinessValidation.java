/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Ewb3WayReconBusinessValidation")
public class Ewb3WayReconBusinessValidation implements DocRulesValidatorChain<EwbUploadProcessedHeaderEntity> {
	
		// Maintain a list of validators that can be invoked in sequence.
		// This can be the preliminary implementation. Later on we can have
		// more complex rule ordering and rule evaluation decisions stored in the
		// DB and invoked for the list of validations.
		private static final BusinessRuleValidator[] VALIDATORS = { 
				new EwbUploadItemNumberValidator(),
				new EwbGstinValidator(),
			//	new Gstr7ActionTypeValidator()
				};

		@Override
		public List<ProcessingResult> validate(EwbUploadProcessedHeaderEntity prod, ProcessingContext context) {
			List<ProcessingResult> results = new ArrayList<>();
			for (BusinessRuleValidator<EwbUploadProcessedHeaderEntity> validator : VALIDATORS) {
				if (validator == null) {
					LOGGER.warn("Validator cannot be null. " + "Ignoring the null validator!!");
					continue;
				}
				// Get the class name of the validator, for logging in case of
				// any errors.
				String validatorCls = validator.getClass().getSimpleName();
				try {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Executing validator '%s'", validatorCls);
						LOGGER.debug(msg);
					}
					if (validator != null) {
						List<ProcessingResult> tmpResults = validator.validate(prod, context);
						results.addAll(tmpResults);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format("Executed validator '%s'. No: of Results " + "available: '%d'",
									validatorCls, (tmpResults != null) ? tmpResults.size() : 0);
							LOGGER.debug(msg);
						}
					}
				} catch (Exception ex) {
					// create a processing result to inform that the validator
					// failed.
					String exName = ex.getClass().getSimpleName();
					String msg = String.format("Error while executing the validator '%s'. " + "Exception: '%s'",
							validatorCls, exName);
					ProcessingResult result = new ProcessingResult("LOCAL", ProcessingResultType.ERROR, "ER9999", msg,
							null);
					results.add(result);
				}
			}
			return results;
		}
	}