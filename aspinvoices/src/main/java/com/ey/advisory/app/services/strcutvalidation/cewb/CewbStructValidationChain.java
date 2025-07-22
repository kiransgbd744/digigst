package com.ey.advisory.app.services.strcutvalidation.cewb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CewbStructValidationChain")
public class CewbStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new CewbSerialNumber() })
			.put(1, new ValidationRule[] { new CewbEwayBillNumber() })
			.put(2, new ValidationRule[] { new CewbFromPlace() })
			.put(3, new ValidationRule[] { new CewbFromState() })
			.put(4, new ValidationRule[] { new CewbVehicleNumber() })
			.put(5, new ValidationRule[] { new CewbTransMode() })
			.put(6, new ValidationRule[] { new CewbTransDocNumber() })
			.put(7, new ValidationRule[] { new CewbTransDocDate() })
			.put(8, new ValidationRule[] { new CewbSgstin() }).build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap, List<CewbExcelEntity> dumpExcelDatas) {

		Map<String, List<Object[]>> allDocsMap = rawDocMap.stream()
				.collect(Collectors.groupingBy(doc -> getInvKey(doc)));
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = dumpExcelDatas.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			List<Object[]> value = allDocsMap.get(key);
			List<String> listStr = new ArrayList<>();

			if (value != null && value.size() > 1) {
				for (Object[] val : value) {
					String sNo = getSno(val);
					listStr.add(sNo);
				}

				List<ProcessingResult> errors = new ArrayList<>();
				String sno = getSno(obj);
				if (!listStr.get(0).equalsIgnoreCase(sno)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SERIAL_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER6129",
							"Data other than the EWB.no across single s.no should be same.",
							location));
					results.addAll(errors);

				}

			}

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

	private String getSno(Object[] obj) {
		String fromPlace = (obj[2] != null) ? (String.valueOf(obj[2])).trim()
				: "";
		String fraomState = (obj[3] != null) ? (String.valueOf(obj[3])).trim()
				: "";
		String transMode = (obj[5] != null) ? (String.valueOf(obj[5])).trim()
				: "";
		String gstin = (obj[8] != null) ? (String.valueOf(obj[8])).trim() : "";
		
		String vechicleNo = (obj[4] != null) ? (String.valueOf(obj[4])).trim()
				: "";

		String transNo = (obj[6] != null) ? (String.valueOf(obj[6])).trim()
				: "";

		String transDate = (obj[7] != null) ? (String.valueOf(obj[7])).trim()
				: "";
		String transDates = "";

		if (transDate != null && !transDate.isEmpty()) {
			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(transDate.trim());
			transDates = date == null ? transDate : date.toString();
		}

		return new StringJoiner("|").add(vechicleNo).add(transNo)
				.add(transDates).add(fromPlace).add(fraomState)
				.add(transMode).add(gstin).toString();
	}

	private String getInvKey(Object[] obj) {
		String sNo = (obj[0] != null) ? (String.valueOf(obj[0])).trim() : "";
		return new StringJoiner("|").add(sNo).toString();
	}
}
