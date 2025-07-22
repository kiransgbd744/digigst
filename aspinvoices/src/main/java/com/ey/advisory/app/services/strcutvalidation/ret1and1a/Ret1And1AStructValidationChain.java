package com.ey.advisory.app.services.strcutvalidation.ret1and1a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToRet1And1AExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Ret1And1AStructValidationChain")
public class Ret1And1AStructValidationChain {

	@Autowired
	@Qualifier("SRFileToRet1And1AExcelConvertion")
	private SRFileToRet1And1AExcelConvertion sRFileToRet1And1AExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = 
			new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new Ret1And1AReturnTypeValidationRule() })
			.put(1, new ValidationRule[] {
					new Ret1And1ASgstinValidationRule() })
			.put(2, new ValidationRule[] {
					new Ret1And1AReturnPeriodValidationRule() })
			.put(3, new ValidationRule[] {
					new Ret1And1AReturnTableValidationRule() })
			.put(4, new ValidationRule[] { new Ret1And1AValueValidationRule() })
			.put(5, new ValidationRule[] { new Ret1And1AIgstValidationRule() })
			.put(6, new ValidationRule[] { new Ret1And1ACgstValidationRule() })
			.put(7, new ValidationRule[] { new Ret1And1ASgstValidationRule() })
			.put(8, new ValidationRule[] { new Ret1And1ACessValidationRule() })
			.put(9, new ValidationRule[] {
					new Ret1And1AProfitCentreValidationRule() })
			.put(10, new ValidationRule[] {
					new Ret1And1APlantValidationRule() })
			.put(11, new ValidationRule[] {
					new Ret1And1ADivisionValidationRule() })
			.put(12, new ValidationRule[] {
					new Ret1And1ALocationValidationRule() })
			.put(13, new ValidationRule[] {
					new Ret1And1ASalesOrgValidationRule() })
			.put(14, new ValidationRule[] {
					new Ret1And1ADistributeChanValidationRule() })
			.put(15, new ValidationRule[] {
					new Ret1And1AUserAccess1ValidationRule() })
			.put(16, new ValidationRule[] {
					new Ret1And1AUserAccess2ValidationRule() })
			.put(17, new ValidationRule[] {
					new Ret1And1AUserAccess3ValidationRule() })
			.put(18, new ValidationRule[] {
					new Ret1And1AUserAccess4ValidationRule() })
			.put(19, new ValidationRule[] {
					new Ret1And1AUserAccess5ValidationRule() })
			.put(20, new ValidationRule[] {
					new Ret1And1AUserAccess6ValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Ret1And1AExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToRet1And1AExcelConvertion
					.getRet1And1AGstnKey(obj);
			for (int i = 0; i < 20; i++) {
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
