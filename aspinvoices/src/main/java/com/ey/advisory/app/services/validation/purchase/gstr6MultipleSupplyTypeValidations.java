package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SupplyType;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.GSTConstants.INWARD_ERR_CODES;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class gstr6MultipleSupplyTypeValidations 
implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> SUPPLY_TYPE = ImmutableList.of(
			GSTConstants.TAX, GSTConstants.NIL, GSTConstants.EXT,
			GSTConstants.NON);

	/*private static final List<String> SUPPLY_TYPE1 = ImmutableList.of(
			 GSTConstants.NIL, GSTConstants.EXT,
			GSTConstants.NON);
	
	
	private static final List<String> SUPPLY_TYPE2 = ImmutableList.of(
			GSTConstants.EXT,
			GSTConstants.NON);
*/
	private boolean isValidSupplyType(String firstType, String curType) {
		return (SUPPLY_TYPE.contains(trimAndConvToUpperCase(curType))
			&& SUPPLY_TYPE.contains(trimAndConvToUpperCase(firstType)));
		
	}
	/*private boolean isValidSupplyType1(String firstType, String curType) {
		return (SUPPLY_TYPE1.contains(trimAndConvToUpperCase(curType))
			&& SUPPLY_TYPE1.contains(trimAndConvToUpperCase(firstType)));
		
	}
	private boolean isValidSupplyType2(String firstType, String curType) {
		return  (SUPPLY_TYPE2.contains(trimAndConvToUpperCase(curType))
			&& SUPPLY_TYPE2.contains(trimAndConvToUpperCase(firstType)));
		
	}*/
	private boolean isValidSupplyType3(String firstType, String curType) {
		return curType.equals(trimAndConvToUpperCase(firstType));
	}
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		List<InwardTransDocLineItem> items = document.getLineItems();
		
			
		String firstType = items.get(0).getSupplyType();

			IntStream.range(0, items.size()).forEach(idx -> {
				if (isValidSupplyType(firstType, items.get(idx).getSupplyType())
						/*|| isValidSupplyType1(firstType,
								items.get(idx).getSupplyType())
						|| isValidSupplyType2(firstType,
								items.get(idx).getSupplyType())*/
						|| isValidSupplyType3(firstType,
								items.get(idx).getSupplyType())) {
					// nothing

				} else {
				errorLocations.add(SupplyType);
				TransDocProcessingResultLoc location 
				        = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, 
						INWARD_ERR_CODES.ER1304.name(),
						INWARD_ERR_CODES.ER1304.getDesc(), location));
			}

			
		});
		
		return errors;
	}

}
