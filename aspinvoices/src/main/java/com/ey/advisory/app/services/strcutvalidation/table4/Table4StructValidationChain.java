package com.ey.advisory.app.services.strcutvalidation.table4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToOutwardTableDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Table4StructValidationChain")
public class Table4StructValidationChain {

	@Autowired
	@Qualifier("SRFileToOutwardTableDetailsConvertion")
	private SRFileToOutwardTableDetailsConvertion sRFileToOutwardTableDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] {
					new Table4ReturnTypeValidationRule() })
			.put(1, new ValidationRule[] { new Table4SgstinValidationRule() })
			.put(2, new ValidationRule[] {
					new Table4ReturnPeriodValidationRule() })
			.put(3, new ValidationRule[] { new Table4EcomValidationRule() })
			.put(4, new ValidationRule[] {
					new Table4ValOfSuppMadeValidationRule() })
			.put(5, new ValidationRule[] {
					new Table4ValOfSuppRetValidationRule() })
			.put(6, new ValidationRule[] {
					new Table4NetOfSuppValidationRule() })
			.put(7, new ValidationRule[] { new Table4IgstValidationRule() })
			.put(8, new ValidationRule[] { new Table4CgstValidationRule() })
			.put(9, new ValidationRule[] { new Table4SgstValidationRule() })
			.put(10, new ValidationRule[] { new Table4CessValidationRule() })
			.put(11, new ValidationRule[] {
					new Table4ProfitCentreValidationRule() })
			.put(12, new ValidationRule[] { new Table4PlantValidationRule() })
			.put(13, new ValidationRule[] {
					new Table4DivisionValidationRule() })
			.put(14, new ValidationRule[] {
					new Table4LocationValidationRule() })
			.put(15, new ValidationRule[] {
					new Table4SalesOrgValidationRule() })
			.put(16, new ValidationRule[] {
					new Table4DistributeChanValidationRule() })
			.put(17, new ValidationRule[] {
					new Table4UserAccess1ValidationRule() })
			.put(18, new ValidationRule[] {
					new Table4UserAccess2ValidationRule() })
			.put(19, new ValidationRule[] {
					new Table4UserAccess3ValidationRule() })
			.put(20, new ValidationRule[] {
					new Table4UserAccess4ValidationRule() })
			.put(21, new ValidationRule[] {
					new Table4UserAccess5ValidationRule() })
			.put(22, new ValidationRule[] {
					new Table4UserAccess6ValidationRule() })
			/*
			 * .put(23,new ValidationRule[] { new
			 * StateCessAmountValidationRule() }) .put(24,new ValidationRule[] {
			 * new TotalValueValidationRule() }) .put(25,new ValidationRule[] {
			 * new StateCessRateValidationRule() }) .put(26,new ValidationRule[]
			 * { new StateApplyingCessValidationRule() })
			 * 
			 * .put(26,new ValidationRule[] { new UserDefinedValidationRule() })
			 * .put(27,new ValidationRule[] { new UserDefinedValidationRule() })
			 * .put(28,new ValidationRule[] { new UserDefinedValidationRule() })
			 */
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<OutwardTable4ExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToOutwardTableDetailsConvertion
					.getTable4InvKey(obj);
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

	/*
	 * private static String getKeyValues(Object[] obj) { String sgstin=null;
	 * String returnPeriod=null; String newPos=null; String newhsnOrsac=null;
	 * String newRate=null; String newEcomgstin=null; sgstin
	 * =(obj[0]!=null)?(String.valueOf(obj[0])).trim():""; returnPeriod
	 * =(obj[1]!=null)?(String.valueOf(obj[1])).trim():"";
	 * newPos=(obj[12]!=null)?(String.valueOf(obj[12])).trim():"";
	 * newhsnOrsac=(obj[13]!=null)?(String.valueOf(obj[13])).trim():"";
	 * newRate=(obj[16]!=null)?(String.valueOf(obj[16])).trim():"";
	 * newEcomgstin=(obj[18]!=null)?(String.valueOf(obj[18])).trim():""; return
	 * new StringJoiner("|").add(sgstin).add(returnPeriod)
	 * .add(newPos).add(newhsnOrsac).add(newRate) .add(newEcomgstin).toString();
	 * }
	 */
}
