package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.services.docs.Gstr6DistrbtnExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
@Component("Gstr6StructuralValidatorChain")
public class Gstr6StructuralValidatorChain {

	@Autowired
	@Qualifier("Gstr6DistrbtnExcelConvertion")
	private Gstr6DistrbtnExcelConvertion gstr6DistrbtnExcelConvertion;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	            = new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] {
					new Gstr6ReturnPeriodValidationRule() })
			.put(1, new ValidationRule[] {
					new Gstr6SuppliergstinValidationRule() })
			.put(2, new ValidationRule[] {
					new Gstr6RecipientGgstinValidationRule() })
			.put(3, new ValidationRule[] { new Gstr6StateCodeValidation() })
			.put(4, new ValidationRule[] {
					new Gstr6OrigRecipientGgstinValidationRule() })
			.put(5, new ValidationRule[] { new Gstr6OrigStateCodeValidation() })
			.put(6, new ValidationRule[] {
					new Gstr6DocumentTypeValidationRule() })
			.put(7, new ValidationRule[] { new Gstr6SupTypeValidationRule() })
			.put(8, new ValidationRule[] {
					new Gstr6DocumentNoValidationRule() })
			.put(9, new ValidationRule[] {
					new Gstr6DocumentDateValidationRule() })
			.put(10, new ValidationRule[] {
					new Gstr6OrgDocumentNoValidationRule() })
			.put(11, new ValidationRule[] {
					new Gstr6OrgDocumentDateValidationRule() })
			.put(12, new ValidationRule[] {
					new Gstr6OriginalCreditNoteNumber() })
			.put(13, new ValidationRule[] {
					new Gstr6OriginalCreditNoteDateValidtion() })
			.put(14, new ValidationRule[] { new Gstr6OEligibilityValidtion() })
			.put(15, new ValidationRule[] {
					new Gstr6IGSTasIGSTValidationValidtion() })
			.put(16, new ValidationRule[] {
					new Gstr6IGSTasSGSTValidationValidtion() })
			.put(17, new ValidationRule[] {
					new Gstr6IGSTasCGSTValidationValidtion() })
			.put(18, new ValidationRule[] { new Gstr6SGSTasSGSTValidtion() })
			.put(19, new ValidationRule[] { new Gstr6SGSTasIGSTValidtion() })
			.put(20, new ValidationRule[] { new Gstr6CGSTasCGSTValidtion() })
			.put(21, new ValidationRule[] { new Gstr6CGSTasIGSTValidtion() })
			.put(22, new ValidationRule[] { new Gstr6CESSAmountValidtion() })

			.build();

	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap,
			List<Gstr6DistributionExcelEntity> saveExcelAll) {

		Set<String> keySet=new HashSet<String>();
		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		Integer is = 0;
		for (Object[] obj : rawDocMap) {
			Long id = saveExcelAll.get(is).getId();
			List<ProcessingResult> results = new ArrayList<>();
			String key = gstr6DistrbtnExcelConvertion.getFileProcessedKey(obj);
			
			if(!keySet.add(key)){
				List<ProcessingResult> errors = new ArrayList<>();
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DUP_RECORD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3030",
						"Duplicate document.", location));
				results.addAll(errors);
			//	continue;
			}
			
			for (int i = 0; i < 23; i++) {
				// First get the validators to be applied
				ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

				Object cellVal = obj[i];
				Arrays.stream(rules).forEach(rule -> {

					List<ProcessingResult> errors = rule.isValid(
							rawDocMap.indexOf(obj), cellVal, obj, null);
					results.addAll(errors);

				});
			}

			if (results != null && results.size() > 0) {
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());
				map.computeIfAbsent(keys,
						k -> new ArrayList<ProcessingResult>()).addAll(results);
				
			//	map.put(key, results);
			}
			is++;
		}
		return map;

	}

	
}
