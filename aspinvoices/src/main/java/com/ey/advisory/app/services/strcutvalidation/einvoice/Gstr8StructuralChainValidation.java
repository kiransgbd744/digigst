/**
 * 
 */
package com.ey.advisory.app.services.strcutvalidation.einvoice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.gstr8.CentralTaxAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.DocumentTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.EcomGstinValidation;
import com.ey.advisory.app.services.strcutvalidation.gstr8.IdentifierValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.IntegratedTaxAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.NetSuppliesValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.OriginalNetSuppliesValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.OriginalPosValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.OriginalReturnPeriodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.OriginalSupplierGstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.PosValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.ReturnFromRegisteredValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.ReturnPeriodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.ReturnsFromUnRegisteredValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.StateUTTaxAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.SupplierGstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.SuppliesToRegisteredValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.SuppliesToUnRegisteredValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.SupplyTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.UserDefinedFieldValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr8.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr8StructuralChainValidation")
public class Gstr8StructuralChainValidation {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new EcomGstinValidation() })
			.put(1, new ValidationRule[] { new ReturnPeriodValidationRule() })
			.put(2, new ValidationRule[] { new IdentifierValidationRule() })
			.put(3, new ValidationRule[] {
					new OriginalReturnPeriodValidationRule() })
			.put(4, new ValidationRule[] {
					new OriginalNetSuppliesValidationRule() })
			.put(5, new ValidationRule[] { new DocumentTypeValidationRule() })
			.put(6, new ValidationRule[] { new SupplyTypeValidationRule() })
			.put(7, new ValidationRule[] { new SupplierGstinValidationRule() })
			.put(8, new ValidationRule[] {
					new OriginalSupplierGstinValidationRule() })
			.put(9, new ValidationRule[] {
					new PosValidationRule() })
			.put(10, new ValidationRule[] {
					new OriginalPosValidationRule() })			
			.put(11, new ValidationRule[] {
					new SuppliesToRegisteredValidationRule() })
			.put(12, new ValidationRule[] {
					new ReturnFromRegisteredValidationRule() })
			.put(13, new ValidationRule[] {
					new SuppliesToUnRegisteredValidationRule() })
			.put(14, new ValidationRule[] {
					new ReturnsFromUnRegisteredValidationRule() })
			.put(15, new ValidationRule[] { new NetSuppliesValidationRule() })
			.put(16, new ValidationRule[] {
					new IntegratedTaxAmountValidationRule() })
			.put(17, new ValidationRule[] {
					new CentralTaxAmountValidationRule() })
			.put(18, new ValidationRule[] {
					new StateUTTaxAmountValidationRule() })
			.put(19, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(20, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(21, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(22, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(23, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(24, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(25, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(26, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(27, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(28, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(29, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(30, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(31, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(32, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.put(33, new ValidationRule[] {
					new UserDefinedFieldValidationRule() })
			.build();

	public void validation(List<ProcessingResult> validationResult,
			Object[] rowData) {

		for (int i = 0; i < 34; i++) {
			final int j = i;
			// First get the validators to be applied
			ValidationRule[] rules = STRUCT_VAL_RULES.get(i);
			Object cellVal = rowData[i];
			Arrays.stream(rules).forEach(rule -> {
				List<ProcessingResult> errors = rule.isValid(j, cellVal,
						rowData, null);
				validationResult.addAll(errors);
			});
		}

	}

}
