package com.ey.advisory.app.services.docs.gstr7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.outward.ComprehensiveOutwardDocOtherStructuralValidations;
import com.ey.advisory.app.services.strcutvalidation.outward.DummyValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransCgstAmountValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransContractDateValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransContractValueValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransDeducteeGstinValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransDeductorgstinValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransDocumentDateValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransDocumentNoValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransDocumentTypeValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransIgstAmountValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransInvoiceValueValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransLineNoValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalDeducteeGstinValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalDocDateValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalDocumentNumberValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalInvoiceValueValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalReturnPeriodValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransOriginalTaxableValueValidation;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransPayAdvDateValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransReturnPeriodValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransSgstAmountValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransSupplyTypeValidationRule;
import com.ey.advisory.app.services.structvalidation.gstr7.Gstr7TransTaxableValueValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("Gstr7TransStructuralChain")
public class Gstr7TransStructuralChain {

	@Autowired
	@Qualifier("ComprehensiveOutwardDocOtherStructuralValidations")
	private ComprehensiveOutwardDocOtherStructuralValidations strut;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(1, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(2, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(3, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(4, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(5, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(6, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(7, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(8, new ValidationRule[] {
					new Gstr7TransReturnPeriodValidationRule() }) // ReturnPeriod
			.put(9, new ValidationRule[] {
					new Gstr7TransDocumentTypeValidationRule() }) // DocumentType
			.put(10, new ValidationRule[] {
					new Gstr7TransSupplyTypeValidationRule() }) // SupplyType
			.put(11, new ValidationRule[] {
					new Gstr7TransDeductorgstinValidationRule() }) // Deductor
																	// Gstin
			.put(12, new ValidationRule[] {
					new Gstr7TransDocumentNoValidationRule() }) // DocumentNumber
			.put(13, new ValidationRule[] {
					new Gstr7TransDocumentDateValidationRule() }) // DocumentDate
			.put(14, new ValidationRule[] {
					new Gstr7TransOriginalDocumentNumberValidation() }) // OriginalDocumentNumber
			.put(15, new ValidationRule[] {
					new Gstr7TransOriginalDocDateValidation() }) // OriginalDocumentDate
			.put(16, new ValidationRule[] {
					new Gstr7TransDeducteeGstinValidationRule() }) // DeducteeGSTIN
			.put(17, new ValidationRule[] {
					new Gstr7TransOriginalDeducteeGstinValidationRule() }) // OriginalDeducteeGSTIN
			.put(18, new ValidationRule[] {
					new Gstr7TransOriginalReturnPeriodValidationRule() }) // OriginalReturnPeriod
			.put(19, new ValidationRule[] {
					new Gstr7TransOriginalTaxableValueValidation() }) // OriginalTaxableValue
			.put(20, new ValidationRule[] {
					new Gstr7TransOriginalInvoiceValueValidation() }) // OriginalInvoiceValue
			.put(21, new ValidationRule[] {
					new Gstr7TransLineNoValidationRule() }) // LineNumber
			.put(22, new ValidationRule[] {
					new Gstr7TransTaxableValueValidationRule() }) // TaxableValue
			.put(23, new ValidationRule[] {
					new Gstr7TransIgstAmountValidationRule() }) // IGSTAmount
			.put(24, new ValidationRule[] {
					new Gstr7TransCgstAmountValidationRule() }) // CGSTAmount
			.put(25, new ValidationRule[] {
					new Gstr7TransSgstAmountValidationRule() }) // SGSTAmount
			.put(26, new ValidationRule[] {
					new Gstr7TransInvoiceValueValidation() }) // InvoiceValue
			.put(27, new ValidationRule[] { DummyValidationRule.getInstance() }) // ContractNumber
			.put(28, new ValidationRule[] {
					new Gstr7TransContractDateValidationRule() }) // ContractDate
			.put(29, new ValidationRule[] {
					new Gstr7TransContractValueValidation() }) // ContractValue
			.put(30, new ValidationRule[] { DummyValidationRule.getInstance() }) // PaymentAdviceNumber
			.put(31, new ValidationRule[] {
					new Gstr7TransPayAdvDateValidationRule() }) // PaymentAdviceDate
			.put(32, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField1
			.put(33, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField2
			.put(34, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField3
			.put(35, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField4
			.put(36, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField5
			.put(37, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField6
			.put(38, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField7
			.put(39, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField8
			.put(40, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField9
			.put(41, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField10
			.put(42, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField11
			.put(43, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField12
			.put(44, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField13
			.put(45, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField14
			.put(46, new ValidationRule[] { DummyValidationRule.getInstance() }) // UserDefinedField15
			.build();

	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {
		Map<String, List<ProcessingResult>> map = new HashMap<>();
		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();
			for (Object[] obj : value) {
				for (int i = 0; i < 47; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);
					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(
								value.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);
					});
				}
			}

			if (results != null && !results.isEmpty()) {
				map.put(key, results);
				LOGGER.error("Error Map {} ", map);
			}

		});
		return map;

	}
}