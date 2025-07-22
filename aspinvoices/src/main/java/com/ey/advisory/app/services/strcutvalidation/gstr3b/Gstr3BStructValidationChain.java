package com.ey.advisory.app.services.strcutvalidation.gstr3b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.services.docs.SRFileToGstr3BConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr3BStructValidationChain")
public class Gstr3BStructValidationChain {

	@Autowired
	@Qualifier("SRFileToGstr3BConvertion")
	private SRFileToGstr3BConvertion sRFileToGstr3BConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new Gstr3bTaxPayerGstin() })
			.put(1, new ValidationRule[] {
					new Gstr3bReturnPeriodValidationRule() })
			.put(2, new ValidationRule[] {
					new Gstr3bSerialNumberPeriodValidationRule() })
			.put(3, new ValidationRule[] {
					new Gstr3bDescriptionPeriodValidationRule() })
			.put(4, new ValidationRule[] { new Gstr3bIgstValidationRule() })
			.put(5, new ValidationRule[] { new Gstr3bCgstValidationRule() })
			.put(6, new ValidationRule[] { new Gstr3bSgstValidationRule() })
			.put(7, new ValidationRule[] { new Gstr3bCessValidationRule() })
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<Gstr3bExcelEntity> dumpExcelDatas) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToGstr3BConvertion.getGstr3bInvKey(obj);
			for (int i = 0; i < 8; i++) {
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
