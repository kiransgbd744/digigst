package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1ShippingBillNoSame")
public class Anx1ShippingBillNoSame implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.EXPT, GSTConstants.EXPWT);
	private boolean isValidTax(OutwardTransDocLineItem firstType,
			OutwardTransDocLineItem curType) {
		
		
		if(isPresent(firstType.getShippingBillNo()) 
				&& !isPresent(curType.getShippingBillNo())){
			return false;
		}
		
		if(!isPresent(firstType.getShippingBillNo()) 
				&& isPresent(curType.getShippingBillNo())){
			return false;
		}
		
		if (firstType.getShippingBillNo() != null
				&& !firstType.getShippingBillNo().isEmpty()){
			if (!firstType.getShippingBillNo()
					.equalsIgnoreCase(curType.getShippingBillNo())) {
				return false;
			}
		}
		return true;

	}

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
if(document.getSupplyType()==null 
                    || document.getSupplyType().isEmpty()) return errors;
if(!SUPPLY_TYPE_IMPORTS.contains(document.getSupplyType().trim())) return errors;
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item  = items.get(0);

			if (!isValidTax(item, items.get(idx))) {
				errorLocations.add(GSTConstants.SHIPPING_BILL_NO);
				TransDocProcessingResultLoc location 
				                  = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER0062",
								"Shipping Bill Number should be same "
								+ "across all line items of a document.",
								location));
			}

		});

		return errors;
	}

}
