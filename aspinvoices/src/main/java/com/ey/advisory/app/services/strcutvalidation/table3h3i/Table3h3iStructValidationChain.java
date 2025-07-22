package com.ey.advisory.app.services.strcutvalidation.table3h3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.docs.InwardTable3H3IDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Sujith.Nanga
 *
 */
@Component("Table3h3iStructValidationChain")
public class Table3h3iStructValidationChain {

	@Autowired
	@Qualifier("InwardTable3H3IDetailsConvertion")
	private InwardTable3H3IDetailsConvertion inwardTable3H3IDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = 
			new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] { new ReturnTypeValidationRule() })
			.put(1, new ValidationRule[] { new RecipientGSTINValidationRule() })
			.put(2, new ValidationRule[] { new ReturnPeriodValidationRule() })
			// .put(3, new ValidationRule[] { new DocumentTypeValidationRule()
			// })
			.put(3, new ValidationRule[] {
					new TransactionFlagValidationRule() })
			.put(4, new ValidationRule[] { new SgstinOrPanValidationRule() })
			.put(5, new ValidationRule[] { new SupplierNameValidationRule() })
			.put(6, new ValidationRule[] {
					new DifferentialPercentageFlagValidationRule() })
			.put(7, new ValidationRule[] {
					new Section7ofIGSTFlagValidationRule() })
			.put(8, new ValidationRule[] {
					new AutoPopulateToRefundValidationRule() })
			.put(9, new ValidationRule[] { new PosValidationRule1() })
			.put(10, new ValidationRule[] { new HSNValidationRule() })
			.put(11, new ValidationRule[] { new TaxablevalueValidationRule() })
			.put(12, new ValidationRule[] { new RateValidationRule() })
			.put(13, new ValidationRule[] { new H3IGSTAmountValidationRule() })

			.put(14, new ValidationRule[] { new H3CGSTAmountValidationRule() })
			//

			.put(15, new ValidationRule[] { new H3SGSTAmountValidationRule() })

			.put(16, new ValidationRule[] { new H3CessAmountValidationRule() })
			.put(17, new ValidationRule[] { new H3TotalValueValidationRule() })
			.put(18, new ValidationRule[] {
					new EligibilityIndicatorValidationRule() })
			.put(19, new ValidationRule[] { new AvailableIGSTValidationRule() })
			.put(20, new ValidationRule[] { new AvailableCGSTValidationRule() })
			.put(21, new ValidationRule[] { new AvailableSGSTValidationRule() })
			.put(22, new ValidationRule[] { new AvailableCessValidationRule() })
			/*.put(23, new ValidationRule[] { new ProfitCentreValidationRule() })
			.put(24, new ValidationRule[] { new PlantValidationRule() })
			.put(25, new ValidationRule[] { new DivisionValidationRule() })
			.put(26, new ValidationRule[] { new LocationValidationRule() })
			.put(27, new ValidationRule[] {
					new PurchaseOrganisationValidationRule() })
			.put(28, new ValidationRule[] { new UserAccess1ValidationRule() })
			.put(29, new ValidationRule[] { new UserAccess2ValidationRule() })
			.put(30, new ValidationRule[] { new UserAccess3ValidationRule() })
			.put(31, new ValidationRule[] { new UserAccess4ValidationRule() })
			.put(32, new ValidationRule[] { new UserAccess5ValidationRule() })
			.put(33, new ValidationRule[] { new UserAccess6ValidationRule() })*/
			/*
			 * .put(34, new ValidationRule[] { new
			 * UserDefinedField1ValidationRule() }) .put(35, new
			 * ValidationRule[] { new UserDefinedField2ValidationRule() })
			 * .put(36, new ValidationRule[] { new
			 * UserDefinedField3ValidationRule() })
			 */
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<InwardTable3I3HExcelEntity> excelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();

		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = inwardTable3H3IDetailsConvertion
					.getTable3h3iInvKey(obj);
			for (int i = 0; i < 23; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {
					List<ProcessingResult> errors = rule.isValid(cellVal, obj,
							null);
					results.addAll(errors);

				});
			}
			if (results != null && results.size() > 0) {
				String keys = 
						key.concat(GSTConstants.SLASH).concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>())
				.addAll(results);
			}
			is++;
			}
		return map;

	}

}
