package com.ey.advisory.app.services.strcutvalidation.b2cs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.docs.SRFileToB2CSExelDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("B2csStructValidationChain")
public class B2csStructValidationChain {

	@Autowired
	@Qualifier("SRFileToB2CSExelDetailsConvertion")
	private SRFileToB2CSExelDetailsConvertion sRFileToB2CSExelDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new B2csSgstinValidationRule() })
			.put(1, new ValidationRule[] { new B2csRetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new B2csTranactionType() })
			.put(3, new ValidationRule[] { new B2csMonthValidationRule() })// month
			.put(4, new ValidationRule[] { new B2csPOSValidationRule() })// orgpos
			.put(5, new ValidationRule[] { new B2csOrghsnSacValidationRule() })
			.put(6, new ValidationRule[] { new B2csUOMValidationRule() })
			.put(7, new ValidationRule[] { new B2csQuantityValidationRule() })// orgquantity
			.put(8, new ValidationRule[] { new OrgRateValidationRule() })
			.put(9, new ValidationRule[] {
					new OrgTaxableValueValidationRule() })
			.put(10, new ValidationRule[] { new OrgEcomGstinValidationRule() })
			.put(11, new ValidationRule[] {
					new B2csEcomSupValueValidationRule() })// OrgeComSupplyValue
			.put(12, new ValidationRule[] { new NewPOSValidationRule() })// new
																			// pos
			.put(13, new ValidationRule[] { new B2csNewhsnSacValidationRule() })// newhsnsac
			.put(14, new ValidationRule[] { new B2csNewUOMValidationRule() })// new
																				// UOM
			.put(15, new ValidationRule[] { new B2cNewQuantity() })// new
																	// quantity
			.put(16, new ValidationRule[] { new NewRateValidationRule() })// new
																			// rate
			.put(17, new ValidationRule[] {
					new NewTaxableValueValidationRule() })// new taxableValue
			.put(18, new ValidationRule[] { new newEcomGstinValidationRule() })// new
																				// ecomgstin
			.put(19, new ValidationRule[] {
					new NewEcomSupValueValidationRule() })// new ecomsupplyValue
			.put(20, new ValidationRule[] { new B2csIgstValueValidationRule() })// igstamount
			.put(21, new ValidationRule[] { new B2csCgstValueValidationRule() })// cgstamount
			.put(22, new ValidationRule[] { new B2csSgstValueValidationRule() })// sgstamount
			.put(23, new ValidationRule[] { new B2csCessValueValidationRule() })// cess
			.put(24, new ValidationRule[] {
					new B2csTotalValueValidationRule() })// invalidTotal
			.put(25, new ValidationRule[] {
					new B2csProfitCentreValidationRule() })
			.put(26, new ValidationRule[] { new B2csPlantValidationRule() })
			.put(27, new ValidationRule[] { new B2csDivisionValidationRule() })
			.put(28, new ValidationRule[] { new B2csLocationValidationRule() })
			.put(29, new ValidationRule[] { new B2csSalesOrgValidationRule() })
			.put(30, new ValidationRule[] {
					new B2csDistributeChanValidationRule() })
			.put(31, new ValidationRule[] {
					new B2csUserAccess1ValidationRule() })
			.put(32, new ValidationRule[] {
					new B2csUserAccess2ValidationRule() })
			.put(33, new ValidationRule[] {
					new B2csUserAccess3ValidationRule() })
			.put(34, new ValidationRule[] {
					new B2csUserAccess4ValidationRule() })
			.put(35, new ValidationRule[] {
					new B2csUserAccess5ValidationRule() })
			.put(36, new ValidationRule[] {
					new B2csUserAccess6ValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<Gstr1AsEnteredB2csEntity> excelDataSave) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();

		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDataSave.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToB2CSExelDetailsConvertion.getInvKey(obj);
			for (int i = 0; i < 37; i++) {
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
