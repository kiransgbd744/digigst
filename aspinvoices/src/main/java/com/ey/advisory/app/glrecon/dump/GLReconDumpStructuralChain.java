package com.ey.advisory.app.glrecon.dump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.gl.recon.AccountingVoucherDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.AccountingVoucherNumberValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.AmountInLocalCurrencyValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.ClearingDocumentDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.ERPDocumentTypeValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.EntryDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.FiscalYearValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.GLAccountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.MigoDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.MiroDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.PaymentDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.PeriodValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.PostingDateValidation;
import com.ey.advisory.app.services.strcutvalidation.gl.recon.TransactionTypeValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.EinvoiceHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("GLReconDumpStructuralChain")
public class GLReconDumpStructuralChain {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GLReconDumpStructuralChain.class);
	private static final Map<Integer, ValidationRule[]> GL_RECON_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { new TransactionTypeValidation() })
			.put(2, new ValidationRule[] { new FiscalYearValidation() })
			.put(3, new ValidationRule[] { new PeriodValidation() })
			.put(6, new ValidationRule[] { new GLAccountValidationRule() })
			.put(10, new ValidationRule[] { new ERPDocumentTypeValidation() })
			.put(11, new ValidationRule[] {
					new AccountingVoucherNumberValidation() })
			.put(12, new ValidationRule[] {
					new AccountingVoucherDateValidation() })
			.put(15, new ValidationRule[] { new PostingDateValidation() })
			.put(16, new ValidationRule[] {
					new AmountInLocalCurrencyValidation() })
			.put(19, new ValidationRule[] {
					new ClearingDocumentDateValidation() })
			.put(41, new ValidationRule[] { new PaymentDateValidation() })
			.put(53, new ValidationRule[] { new MigoDateValidation() })
			.put(55, new ValidationRule[] { new MiroDateValidation() })
			.put(62, new ValidationRule[] { new EntryDateValidation() })

			.build();

	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				GL_RECON_STRUCT_VAL_RULES.forEach((i, j) -> {

					// First get the validators to be applied
					ValidationRule[] rules = GL_RECON_STRUCT_VAL_RULES.get(i);
					LOGGER.debug("Validating column index: " + i + " with rules: " + Arrays.toString(rules));
					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(
								value.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);

					});
				});
			}

			List<ProcessingResult> errorsResult = EinvoiceHeaderStructuralValidationUtil
					.eliminateDuplicates(results);

			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}
