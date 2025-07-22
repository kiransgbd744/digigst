package com.ey.advisory.app.services.docs.gstr7;

/**
 * Siva Reddy
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransCanValidation;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransCutOffRuleValidation;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransLineNoValidation;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalDeducteeGstin;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalDocDate;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalDocNum;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalInvoiceValue;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalRetPeriod;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransOriginalTaxableValue;
import com.ey.advisory.app.services.validation.gstr7Trans.Gstr7TransSgstCgstIgstAmountValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr7TransDocRulesValidatorChain")
@Slf4j
public class Gstr7TransDocRulesValidatorChain
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@SuppressWarnings("rawtypes")
	private static final Gstr7TransDocRulesValidator[] GSTR7 = {
			new GSTR7TransOriginalDeducteeGstin(),
			new GSTR7TransOriginalDocNum(), new GSTR7TransOriginalDocDate(),
			new GSTR7TransOriginalDocDate(),
			new GSTR7TransOriginalTaxableValue(),
			new GSTR7TransOriginalInvoiceValue(),
			new GSTR7TransOriginalRetPeriod(),
			new Gstr7TransSgstCgstIgstAmountValidator(),
			new GSTR7TransLineNoValidation(),
//			new GSTR7TransCanValidation(),
			new GSTR7TransCutOffRuleValidation()};

	static class DocRulesValidatorPrefWrapper
			implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

		private Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> validator;
		private long execTime;

		DocRulesValidatorPrefWrapper(
				Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> validator) {
			this.validator = validator;
		}

		@Override
		public List<ProcessingResult> validate(
				Gstr7TransDocHeaderEntity document, ProcessingContext context) {
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

	private Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> getWrappedValidator(
			Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> validator) {
		return new DocRulesValidatorPrefWrapper(validator);
	}

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		// List<ProcessingResult> cgstresults = new ArrayList<>();

		List<ProcessingResult> results = new ArrayList<>();

		for (Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> validator : GSTR7) {
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
				LOGGER.error(msg, ex);
				results.add(result);
			}
		}

		if (!results.isEmpty()) {
			Boolean complianceError = results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);

			if (complianceError) {
				document.setProcessed(false);
				document.setError(true);
			} else {
				// If there are no errors, then set the isProcessed to
				// true
				// and isError to false
				document.setProcessed(true);
				document.setError(false);
			}
		} else {
			document.setProcessed(true);
			document.setError(false);
		}

		return results;

	}
}