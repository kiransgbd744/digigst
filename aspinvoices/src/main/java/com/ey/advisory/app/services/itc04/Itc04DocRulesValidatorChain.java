/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.itc04.Itc045A5BValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04CgstRateValidator;
import com.ey.advisory.app.services.validation.itc04.Itc04DeliveryChallanDateValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04DeliveryChallanNumValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04GoodsTypeMasterValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04HsnValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04IgstRateValidator;
import com.ey.advisory.app.services.validation.itc04.Itc04InvoiceDateValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04InvoiceNumValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04IsFiled;
import com.ey.advisory.app.services.validation.itc04.Itc04JWDeliveryChallanNumValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04JobWorkerGstinValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04JobWorkerStateCodeValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04JwTypeMasterValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04NatureOfJwValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04ProductDescriptionValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04ReturnPeriodMasterValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04SgstRateValidator;
import com.ey.advisory.app.services.validation.itc04.Itc04SgstinValidation;
import com.ey.advisory.app.services.validation.itc04.Itc04TableNumberMasterValidation;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04DocRulesValidatorChain")
@Slf4j
public class Itc04DocRulesValidatorChain
		implements DocRulesValidatorChain<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.

	private static final Itc04DocRulesValidator[] ITC04_VALIDATORS = {

			new Itc04TableNumberMasterValidation(),
			new Itc04ReturnPeriodMasterValidation(),
			new Itc04SgstinValidation(),
			new Itc04DeliveryChallanNumValidation(),
			new Itc04DeliveryChallanDateValidation(),
			new Itc04JWDeliveryChallanNumValidation(),
			new Itc04JWDeliveryChallanDateValidation(),
			new Itc04InvoiceNumValidation(), new Itc04InvoiceDateValidation(),
			new Itc04GoodsTypeMasterValidation(),
			new Itc04JobWorkerGstinValidation(),
			new Itc04JobWorkerStateCodeValidation(),
			new Itc04JwTypeMasterValidation(), new Itc04NatureOfJwValidation(),
			new Itc04HsnValidation(), new Itc04IgstRateValidator(),
			new Itc04CgstRateValidator(), new Itc04SgstRateValidator(),
			new Itc045A5BValidation(),
			new Itc04IntraStateValidator(),
			new Itc04InterStateValidator(),
			new Itc04IgstArithmetic(),
			new Itc04CSgstArithmetic(),
			new Itc04ICSgstRateAndAmountvalidator(),
			new Itc04SezValidator(),
			new Itc04IsFiled(),
			new Itc04ProductDescriptionValidation()};

	static class DocRulesValidatorPrefWrapper
			implements Itc04DocRulesValidator<Itc04HeaderEntity> {

		private Itc04DocRulesValidator<Itc04HeaderEntity> validator;
		private long execTime;

		DocRulesValidatorPrefWrapper(
				Itc04DocRulesValidator<Itc04HeaderEntity> validator) {
			this.validator = validator;
		}

		@Override
		public List<ProcessingResult> validate(Itc04HeaderEntity document,
				ProcessingContext context) {
			long startTime = System.currentTimeMillis();
			List<ProcessingResult> result = this.validator.validate(document,
					context);
			long endTime = System.currentTimeMillis();
			this.execTime = endTime - startTime;
			return result;
		}

		public long getExecTime() {
			return this.execTime;
		}
	}

	private Itc04DocRulesValidator<Itc04HeaderEntity> getWrappedValidator(
			Itc04DocRulesValidator<Itc04HeaderEntity> validator) {
		return new DocRulesValidatorPrefWrapper(validator);
	}

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		Itc04DocRulesValidator[] VALIDATORS = ITC04_VALIDATORS;

		List<ProcessingResult> results = new ArrayList<>();
		for (Itc04DocRulesValidator<Itc04HeaderEntity> validator : VALIDATORS) {
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
				DocRulesValidatorPrefWrapper wrappedValidator = (DocRulesValidatorPrefWrapper) getWrappedValidator(
						validator);
				List<ProcessingResult> tmpResults = wrappedValidator
						.validate(document, context);

				PerfStatistics.updateValidatorStat(validatorCls,
						wrappedValidator.execTime);

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
				LOGGER.error(msg, ex);
			}
		}
		return results;
	}

}
