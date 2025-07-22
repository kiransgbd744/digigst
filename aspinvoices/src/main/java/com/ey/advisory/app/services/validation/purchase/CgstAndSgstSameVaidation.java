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
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_RATE;
import static com.ey.advisory.common.GSTConstants.SGST_RATE;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * @author Siva.Nandam
 *
 */
public class CgstAndSgstSameVaidation
		implements DocRulesValidator<InwardTransDocument> {

	

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();

		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			
			if (DOC_TYPE_IMPORTS.contains(
					trimAndConvToUpperCase(document.getDocType())) 
					&& GSTConstants.Y.equalsIgnoreCase(document.getCrDrPreGst()) ){
				//nothing to do
			}else{
			
						/**
						 * Here we are getting rates from Cilent and comparing
						 * those rates like cgst and sgst rates
						 */
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
							
							
							
							
							BigDecimal taxAmount = (cgstRate
									.compareTo(sgstrate) > 0)
											? cgstRate.subtract(sgstrate).abs()
											: sgstrate.subtract(cgstRate).abs();

							if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
								errorLocations.add(CGST_RATE);
								errorLocations.add(SGST_RATE);
								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1302",
										"CGST and SGST Rates cannot be different",
										location));
							}
						});
					}
				}
			
		
		return errors;
	}
}
