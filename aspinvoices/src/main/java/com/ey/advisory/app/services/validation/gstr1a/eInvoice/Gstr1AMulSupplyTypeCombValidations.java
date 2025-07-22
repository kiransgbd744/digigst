package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SupplyType;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.GSTConstants.OUTWARD_ERR_CODES;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AMulSupplyTypeCombValidations")
public class Gstr1AMulSupplyTypeCombValidations
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE = ImmutableList.of(
			GSTConstants.NIL_SUPPLY_TYPE, GSTConstants.TAX, GSTConstants.EXT,
			GSTConstants.NON, GSTConstants.SCH3);
	private static final List<String> SUPPLY_TYPE3 = ImmutableList.of(
			GSTConstants.NIL_SUPPLY_TYPE, GSTConstants.DTA, GSTConstants.EXT,
			GSTConstants.NON, GSTConstants.SCH3);// NON=SCH3
	private static final List<String> SUPPLY_TYPE1 = ImmutableList
			.of(GSTConstants.EXPT, GSTConstants.EXPWT);
	private static final List<String> SUPPLY_TYPE2 = ImmutableList
			.of(GSTConstants.SEZWP, GSTConstants.SEZWOP);

	private boolean isValidSupplyType(String firstType, String curType) {
		return (SUPPLY_TYPE.contains(trimAndConvToUpperCase(curType))
				&& SUPPLY_TYPE.contains(trimAndConvToUpperCase(firstType)));
	}

	private boolean isValidSupplyType1(String firstType, String curType) {
		return (SUPPLY_TYPE1.contains(trimAndConvToUpperCase(curType))
				&& SUPPLY_TYPE1.contains(trimAndConvToUpperCase(firstType)));
	}

	private boolean isValidSupplyType2(String firstType, String curType) {
		return (SUPPLY_TYPE2.contains(trimAndConvToUpperCase(curType))
				&& SUPPLY_TYPE2.contains(trimAndConvToUpperCase(firstType)));
	}

	private boolean isValidSupplyType3(String firstType, String curType) {
		return (SUPPLY_TYPE3.contains(trimAndConvToUpperCase(curType))
				&& SUPPLY_TYPE3.contains(trimAndConvToUpperCase(firstType)));
	}

	private boolean isValidSupplyType4(String firstType, String curType) {
		return curType.equals(trimAndConvToUpperCase(firstType));
	}

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		String firstType = items.get(0).getSupplyType();

		IntStream.range(0, items.size()).forEach(idx -> {

			if (isValidSupplyType(firstType, items.get(idx).getSupplyType())
					|| isValidSupplyType1(firstType,
							items.get(idx).getSupplyType())
					|| isValidSupplyType3(firstType,
							items.get(idx).getSupplyType())
					|| isValidSupplyType2(firstType,
							items.get(idx).getSupplyType())
					|| isValidSupplyType4(firstType,
							items.get(idx).getSupplyType())) {
				// nothing to do
			} else {
				errorLocations.add(SupplyType);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						OUTWARD_ERR_CODES.ER0503.name(),
						OUTWARD_ERR_CODES.ER0503.getDesc(), location));
			}
		});

		return errors;
	}

}
