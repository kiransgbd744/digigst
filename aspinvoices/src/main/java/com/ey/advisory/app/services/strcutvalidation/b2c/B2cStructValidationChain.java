package com.ey.advisory.app.services.strcutvalidation.b2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToOutwardB2CExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.UOMValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("B2cStructValidationChain")
public class B2cStructValidationChain {

	@Autowired
	@Qualifier("SRFileToOutwardB2CExcelConvertion")
	private SRFileToOutwardB2CExcelConvertion sRFileToOutwardB2CExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new B2cReturnTypeValidationRule() })
			.put(1, new ValidationRule[] { new B2cSgstinValidationRule() })
			.put(2, new ValidationRule[] {
					new B2cReturnPeriodValidationRule() })
			// .put(3, new ValidationRule[] { new B2cDocTypeValidation() })
			.put(3, new ValidationRule[] {
					new B2cDifferentialValidationRule() })
			.put(4, new ValidationRule[] { new B2cSection7ofIGSTFlag() })
			.put(5, new ValidationRule[] {
					new B2cAutoPopRefundValidationRule() })
			.put(6, new ValidationRule[] { new PosValidationRule() })
			.put(7, new ValidationRule[] { new B2cHsnOrSacValidationRule() })
			.put(8, new ValidationRule[] { new UOMValidationRule() })
			.put(9, new ValidationRule[] { new B2cQuantityValidationRule() })
			.put(10, new ValidationRule[] { new B2cRateValidator() })
			.put(11, new ValidationRule[] {
					new B2cTaxableValueValidationRule() })
			.put(12, new ValidationRule[] { new B2cIgstValidationRule() })
			.put(13, new ValidationRule[] { new B2cCgstValidationRule() })
			.put(14, new ValidationRule[] { new B2cSgstValidationRule() })
			.put(15, new ValidationRule[] { new B2cCessValidationRule() })
			.put(16, new ValidationRule[] { new B2cTotalValValidationRule() })
			.put(17, new ValidationRule[] {
					new B2cStateApplyingCessValidationRule() })
			.put(18, new ValidationRule[] {
					new B2cStateCessRateValidationRule() })
			.put(19, new ValidationRule[] { new B2cStateCessValidationRule() })
			.put(20, new ValidationRule[] { new B2cTCSFlagValidationRule() })
			.put(21, new ValidationRule[] { new B2cEComGSTINValidationRule() })
			.put(22, new ValidationRule[] {
					new B2cEComValueOfSMadeValidationRule() })
			.put(23, new ValidationRule[] {
					new B2cEComValueOfSRetValidationRule() })
			.put(24, new ValidationRule[] {
					new B2cEComNetValueOfSValidationRule() })
			.put(25, new ValidationRule[] { new B2cTcsAmtValidationRule() })
			// .put(26,new ValidationRule[] { new B2cOtherValValidationRule() })
			.put(26, new ValidationRule[] {
					new B2cProfitCentreValidationRule() })
			.put(27, new ValidationRule[] { new B2cPlantValidationRule() })
			.put(28, new ValidationRule[] { new B2cDivisionValidationRule() })
			.put(29, new ValidationRule[] { new B2cLocationValidationRule() })
			.put(30, new ValidationRule[] { new B2cSalesOrgValidationRule() })
			.put(31, new ValidationRule[] {
					new B2cDistributeChanValidationRule() })
			.put(32, new ValidationRule[] {
					new B2cUserAccess1ValidationRule() })
			.put(33, new ValidationRule[] {
					new B2cUserAccess2ValidationRule() })
			.put(34, new ValidationRule[] {
					new B2cUserAccess3ValidationRule() })
			.put(35, new ValidationRule[] {
					new B2cUserAccess4ValidationRule() })
			.put(36, new ValidationRule[] {
					new B2cUserAccess5ValidationRule() })
			.put(37, new ValidationRule[] {
					new B2cUserAccess6ValidationRule() })
			/*
			 * .put(40,new ValidationRule[] { new UserDefinedValidationRule() })
			 * .put(41,new ValidationRule[] { new UserDefinedValidationRule() })
			 * .put(42,new ValidationRule[] { new UserDefinedValidationRule() })
			 */
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<OutwardB2cExcelEntity> saveExcelAll) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();

		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = saveExcelAll.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToOutwardB2CExcelConvertion
					.getB2cInvKeyValues(obj);
			for (int i = 0; i < 38; i++) {
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
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>()).addAll(results);
			}
			is++;
		}
		return map;

	}
}
