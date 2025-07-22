package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.CessAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.CgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.IgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.PosValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.ReturnPeriodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.SGstinValidation;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.SgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.gstr3bSummary.TotalTaxablevalueValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DummyValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EinvoiceHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

@Component("Gstr3bSummaryStructuralChain")
public class Gstr3bSummaryStructuralChain {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3bSummaryStructuralChain.class);
	private static Map<Integer, ValidationRule[]> GSTR3B_STRUCT_VAL_RULES;
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap, List<String> tableNumbers) {
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		for (String tableNumber : tableNumbers) {
			rawDocMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> value = entry.getValue();
				List<ProcessingResult> results = new ArrayList<>();
				if (tableNumber.equalsIgnoreCase("3.1(a)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							.put(2, new ValidationRule[] {
									DummyValidationRule.getInstance() })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.1(b)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.1(c)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.1(d)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.1(e)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(A)(1)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(A)(2)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(A)(3)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();

				} else if (tableNumber.equalsIgnoreCase("4(A)(4)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(A)(5)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(B)(1)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(B)(2)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("4(D)(1)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();

				} else if (tableNumber.equalsIgnoreCase("4(D)(2)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5(A)(1)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5(A)(2)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5(A)(3)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5(A)(4)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5.1(A)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("5.1(B)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(5, new ValidationRule[] {
									new CgstAmountValidationRule() })
							.put(6, new ValidationRule[] {
									new SgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.2(1)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.put(8, new ValidationRule[] {
									new PosValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.2(2)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.put(8, new ValidationRule[] {
									new PosValidationRule() })
							.build();
				} else if (tableNumber.equalsIgnoreCase("3.2(3)")) {
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()

							.put(0, new ValidationRule[] {
									new ReturnPeriodValidationRule() })
							.put(1, new ValidationRule[] {
									new SGstinValidation() })
							// .put(2, new ValidationRule[] { new
							// DocumentTypeValidationRule()
							// })
							.put(3, new ValidationRule[] {
									new TotalTaxablevalueValidationRule() })
							.put(4, new ValidationRule[] {
									new IgstAmountValidationRule() })
							.put(7, new ValidationRule[] {
									new CessAmountValidationRule() })
							.put(8, new ValidationRule[] {
									new PosValidationRule() })
							.build();
				} else {

					List<ProcessingResult> errors = new ArrayList<>();
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.TABLE_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER053",
							"Invalid Table Number as per Master", location));
					GSTR3B_STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
							.put(2, new ValidationRule[] {
									(ValidationRule) errors })
							.build();
				}
				int length = GSTR3B_STRUCT_VAL_RULES.size();
				for (Object[] obj : value) {

					for (int i = 0; i < length; i++) {
						// First get the validators to be applied
						ValidationRule[] rules = GSTR3B_STRUCT_VAL_RULES.get(i);
						LOGGER.debug(rules.toString());
						Object cellVal = obj[i];
						Arrays.stream(rules).forEach(rule -> {
							List<ProcessingResult> errors = rule.isValid(
									value.indexOf(obj), cellVal, obj, null);
							results.addAll(errors);

						});
					}
				}

				// check for incompatible values across rows in the same colummn
				// List<ProcessingResult> sameColummnErrors =
				// strut.validate(value);

				// results.addAll(sameColummnErrors);
				List<ProcessingResult> errorsResult = EinvoiceHeaderStructuralValidationUtil
						.eliminateDuplicates(results);

				if (errorsResult != null && errorsResult.size() > 0) {
					map.put(key, errorsResult);
				}
			});
		}
		return map;

	}
}
