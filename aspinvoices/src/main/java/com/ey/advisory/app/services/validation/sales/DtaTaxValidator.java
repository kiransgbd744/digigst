package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class DtaTaxValidator implements DocRulesValidator<OutwardTransDocument> {
	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT,
			SGST_AMOUNT };
	private static final List<String> SUPPLY_TYPES_REQUIRING_IMPORTS = 
			ImmutableList.of("DTA");
	@Override
	public List<ProcessingResult> validate(
			     OutwardTransDocument document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal csgstAmount = cgstAmount.add(sgstAmount);
			if (document.getSupplyType()!=null 
					&& !document.getSupplyType().isEmpty()) {
				if(SUPPLY_TYPES_REQUIRING_IMPORTS
						.contains(document.getSupplyType())){
					if(cgstAmount.compareTo(BigDecimal.ZERO) != 0 
							|| sgstAmount.compareTo(BigDecimal.ZERO) != 0 
							|| csgstAmount.compareTo(BigDecimal.ZERO) != 0 
							){
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER305",
						"Supply Type : DTA, then CGST & SGST shall be zero.",
						location));
			}
				}	
			}
				
		});

		return errors;
	}

}

