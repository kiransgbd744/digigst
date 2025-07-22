/*package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.services.strcutvalidation.*;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class BillOfEntryValidationRule implements ValidationRule {

	private static final List<String> SUPPLY_TYPES_REQUIRING_IMPORTS =
			ImmutableList.of("IMPG");
	
	private static final int MAX_PORT_CODE = 10000000;
	@Autowired
	private DataConverterFactory converterFactory;

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.TAXABLE_VALUE);
		TransDocProcessingResultLoc location 
		                     = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
				"Invalid Taxable Value",
				location));
		String supplyType = getSupplyType(obj, row, layout);
		if (supplyType == null) {
			return errors;
		}

		if (!SUPPLY_TYPES_REQUIRING_IMPORTS.contains(supplyType)) {
			return true;

		}

		boolean isPresent = ValidatorUtil.isValuePresent(obj);
		if (!isPresent) {
			return errors;
		}
		Converter converter = converterFactory.getConverter(obj, String.class);
		if (converter == null) {
			return errors;
		}
		String billOfEntry = (String) converter.convert(obj);
		if(!billOfEntry.matches("[0-9]+")) { return errors;}
		//return Integer.valueOf(billOfEntry) < MAX_PORT_CODE;
		return errors;

	}

	private String getSupplyType(Object obj, Object[] row,
			TabularDataLayout layout) {
		if (ValidatorUtil.isValuePresent(obj)) {
			Converter converter = converterFactory.getConverter(obj,
					String.class);
			if (converter != null) {
				Object supplyTypeObj = (String) row[11];
				SupplyTypeValidationRule supplyTypeRule =
						new SupplyTypeValidationRule();
				String supplyType = supplyTypeRule
						.getSupplyTypeIfValid(supplyTypeObj, row, layout);
				if (supplyType != null) {
					return supplyType;
				}
			}
		}
		return null;
	}

}
*/