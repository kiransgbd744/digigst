package com.ey.advisory.app.services.strcutvalidation.advanceAdjusted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.docs.dto.Gstr1VerticalAtRespDto;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

@Component("AAStructValidationChain")
public class AAStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder()
			.put(0, new ValidationRule[] { new TxpdSgstinValidationRule() })
			.put(1, new ValidationRule[] { new TxpdRetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new TxpdTranactionType() })
			.put(3, new ValidationRule[] { new TxpdMonthValidationRule() })// month
			.put(4, new ValidationRule[] { new TxpdPOSValidationRule() })// orgpos
			.put(5, new ValidationRule[] { new TxpdOrgRateValidationRule() })
			.put(6, new ValidationRule[] { new TxpdOrgGrossAdvanceAdjusted() })// orgGross
			.put(7, new ValidationRule[] { new TxpdNewPOSValidationRule() })// new
																			// pos
			.put(8, new ValidationRule[] { new TxpdNewRateValidationRule() })// new
																				// rate
			.put(9, new ValidationRule[] { new TxpdNewGrossAdvanceAdjusted() })// new 
																				// gross
			.put(10, new ValidationRule[] { new TxpdIgstAmt() })// igst
			.put(11, new ValidationRule[] { new TxpdCgst() })// cgst
			.put(12, new ValidationRule[] { new TxpdSgst() })// sgst
			.put(13, new ValidationRule[] { new TxpdCess() })// cess 
			.put(14, new ValidationRule[] { new TxpdProfitCenter() })
			.put(15, new ValidationRule[] { new TxpdPlant() })
			.put(16, new ValidationRule[] { new TxpdDivision() })
			.put(17, new ValidationRule[] { new TxpdLocation() })
			.put(18, new ValidationRule[] { new TxpdSalesOrganization() })
			.put(19, new ValidationRule[] { new TxpdDistributedChannel() })
			.put(20, new ValidationRule[] { new TxpdUserAccess1() })
			.put(21, new ValidationRule[] { new TxpdUserAccess2() })
			.put(22, new ValidationRule[] { new TxpdUserAccess3() })
			.put(23, new ValidationRule[] { new TxpdUserAccess4() })
			.put(24, new ValidationRule[] { new TxpdUserAccess5() })
			.put(25, new ValidationRule[] { new TxpdUserAccess6() }).build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr1AsEnteredTxpdFileUploadEntity> excelDataSave) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = excelDataSave.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			for (int i = 0; i < 26; i++) {
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
			List<Object[]> rawDocMap, List<Gstr1VerticalAtRespDto> list) {

		Map<String, List<ProcessingResult>> map = 
				new HashMap<String, List<ProcessingResult>>();
		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = list.get(is).getSNo();
			List<ProcessingResult> results = new ArrayList<>();
			String key = getInvKey(obj);
			for (int i = 0; i < 26; i++) {
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

	public String getInvKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transactionType = (obj[2] != null
				&& !obj[2].toString().isEmpty()) ? String.valueOf(obj[2]).trim()
						: GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim() : "";
		String orgPOS = (obj[4] != null) ? String.valueOf(obj[4]).trim() : "";
		String orgRate = (obj[5] != null) ? String.valueOf(obj[5]).trim() : "";
		String newPOS = (obj[7] != null) ? String.valueOf(obj[7]).trim() : "";
		String newRate = (obj[8] != null) ? String.valueOf(obj[8]).trim() : "";

		String profitCentre = (obj[14] != null && !obj[14].toString().isEmpty())
				? String.valueOf(obj[14]).trim() : GSTConstants.NA;
		String plant = (obj[15] != null && !obj[15].toString().isEmpty())
				? String.valueOf(obj[15]).trim() : GSTConstants.NA;
		String division = (obj[16] != null && !obj[16].toString().isEmpty())
				? String.valueOf(obj[16]).trim() : GSTConstants.NA;
		String location = (obj[17] != null && !obj[17].toString().isEmpty())
				? String.valueOf(obj[17]).trim() : GSTConstants.NA;
		String salesOrganization = (obj[18] != null
				&& !obj[18].toString().isEmpty())
						? String.valueOf(obj[18]).trim() : GSTConstants.NA;
		String distributionChannel = (obj[19] != null
				&& !obj[19].toString().isEmpty())
						? String.valueOf(obj[19]).trim() : GSTConstants.NA;
		String userAccess1 = (obj[20] != null && !obj[20].toString().isEmpty())
				? String.valueOf(obj[20]).trim() : GSTConstants.NA;
		String userAccess2 = (obj[21] != null && !obj[21].toString().isEmpty())
				? String.valueOf(obj[21]).trim() : GSTConstants.NA;
		String userAccess3 = (obj[22] != null && !obj[22].toString().isEmpty())
				? String.valueOf(obj[22]).trim() : GSTConstants.NA;
		String userAccess4 = (obj[23] != null && !obj[23].toString().isEmpty())
				? String.valueOf(obj[23]).trim() : GSTConstants.NA;
		String userAccess5 = (obj[24] != null && !obj[24].toString().isEmpty())
				? String.valueOf(obj[24]).trim() : GSTConstants.NA;
		String userAccess6 = (obj[25] != null && !obj[25].toString().isEmpty())
				? String.valueOf(obj[25]).trim() : GSTConstants.NA;
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sgstin)
				.add(retPeriod).add(transactionType).add(month).add(orgPOS)
				.add(orgRate).add(newPOS).add(newRate).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrganization)
				.add(distributionChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();
	}
}
