package com.ey.advisory.app.services.strcutvalidation.InvoiceFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1AInvoiceDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("Gstr1AInvoiceSeriesStructValidationChain")
public class Gstr1AInvoiceSeriesStructValidationChain {
	@Autowired
	@Qualifier("SRFileToGstr1AInvoiceDetailsConvertion")
	private SRFileToGstr1AInvoiceDetailsConvertion sRFileToInvoiceDetailsConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new InvSgstinValidationRule() })
			.put(1, new ValidationRule[] { new InvRetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new SerialNOValidator() })
			.put(3, new ValidationRule[] { new NatureOfDocumentValidator() })
			.put(4, new ValidationRule[] { new FromValidator() })
			.put(5, new ValidationRule[] { new ToValidator() })// to
			.put(6, new ValidationRule[] { new TotalNumber() })// totalNo
			.put(7, new ValidationRule[] { new cancalled() })// can
			.put(8, new ValidationRule[] { new NetNumber() })// netNumber
			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr1AAsEnteredInvEntity> excelDataSave) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDataSave.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToInvoiceDetailsConvertion.generateInvKey(obj);
			for (int i = 0; i < 9; i++) {
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

	public Map<String, List<ProcessingResult>> validationApi(
			List<Object[]> listObAt,
			List<Gstr1VerticalDocSeriesRespDto> reqUserInputs) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : listObAt) {
			Long id = reqUserInputs.get(is).getSNo();
			List<ProcessingResult> results = new ArrayList<>();
			String key = sRFileToInvoiceDetailsConvertion.generateInvKey(obj);
			for (int i = 0; i < 9; i++) {
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
