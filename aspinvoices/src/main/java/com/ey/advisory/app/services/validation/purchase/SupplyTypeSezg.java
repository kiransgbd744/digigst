package com.ey.advisory.app.services.validation.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * @author Siva.Nandam
 *
 */
public class SupplyTypeSezg implements DocRulesValidator<InwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { GSTConstants.CGST_AMOUNT,
			GSTConstants.SGST_AMOUNT };

	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZG);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (SUPPLY_TYPE_IMPORTS.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) {
				List<InwardTransDocLineItem> items = document.getLineItems();
				IntStream.range(0, items.size()).forEach(idx -> {
					InwardTransDocLineItem item = items.get(idx);
					
					BigDecimal cgstRate=	item.getCgstRate();
					BigDecimal sgstrate=	item.getSgstRate();
					if(cgstRate==null){
						cgstRate=	BigDecimal.ZERO;
					}
					if(sgstrate==null){
						sgstrate=	BigDecimal.ZERO;
					}
					BigDecimal cgstAmount=	item.getCgstAmount();
					BigDecimal sgstAmount=	item.getSgstAmount();
					if(cgstAmount==null){
						cgstAmount=	BigDecimal.ZERO;
					}
					if(sgstAmount==null){
						sgstAmount=	BigDecimal.ZERO;
					}
					
					BigDecimal allAmount = sgstAmount
							.add(cgstAmount);
					BigDecimal allRate = cgstRate
							.add(sgstrate);
					if (allAmount.compareTo(BigDecimal.ZERO) > 0 
							|| allRate.compareTo(BigDecimal.ZERO) > 0) {
						TransDocProcessingResultLoc location 
						       = new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS);
						errors.add(
								new ProcessingResult(APP_VALIDATION,
										 "ER1309",
										"CGST / SGST cannot be applied"
												+ " in case of SEZG",
										location));
					}
				});

			}
		}

		return errors;
	}

}
