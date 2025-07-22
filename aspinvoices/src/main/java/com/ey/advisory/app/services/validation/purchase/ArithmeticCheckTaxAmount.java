package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class ArithmeticCheckTaxAmount implements 
                                      DocRulesValidator<InwardTransDocument>{

	private static final List<String> UNI_IMPORTS = ImmutableList
			.of("YL","NL");

	private static final List<String> UNIOr_IMPORTS = ImmutableList
			.of("Y","N","");
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errorsInfo = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();

		
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);

			BigDecimal taxableValue=	item.getTaxableValue();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal SgstRate = item.getSgstRate();
			BigDecimal taxSgstAmount = taxableValue.multiply(SgstRate)
					.divide(BigDecimal.valueOf(100));
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal CsgstRate = item.getCgstRate();
			BigDecimal taxcgstAmount = taxableValue.multiply(CsgstRate)
					.divide(BigDecimal.valueOf(100));
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal IgstRate = item.getIgstRate();
			BigDecimal taxigstAmount = taxableValue.multiply(IgstRate)
					.divide(BigDecimal.valueOf(100));
			
			  BigDecimal str= new BigDecimal("0.65");
			  
			BigDecimal taxSgstAmount1 = taxableValue.multiply(SgstRate)
					.multiply(str)
					.divide(BigDecimal.valueOf(100));
			BigDecimal taxcgstAmount1 = taxableValue.multiply(CsgstRate)
					.multiply(str)
					.divide(BigDecimal.valueOf(100));
			BigDecimal taxigstAmount1 = taxableValue.multiply(IgstRate)
					.multiply(str)
					.divide(BigDecimal.valueOf(100));
			
			if(UNI_IMPORTS.contains(item.getCommonSupplyIndicator())){
				if((sgstAmount.compareTo(taxSgstAmount1) != 0)){
					errorLocations.add(SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN202",
							"SGST Amount is incorrectly computed ", location)); 
				}
				if((cgstAmount.compareTo(taxcgstAmount1) != 0)){
					errorLocations.add(CGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN207",
							"CGST Amount is incorrectly computed ", location)); 
				}
				if((igstAmount.compareTo(taxigstAmount1) != 0)){
					errorLocations.add(IGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN208",
							"IGST Amount is incorrectly computed ", location)); 
				}
			}
			else if(item.getCommonSupplyIndicator()==null 
				|| (UNIOr_IMPORTS.contains(item.getCommonSupplyIndicator()))){
			if((sgstAmount.compareTo(taxSgstAmount) != 0)){
				errorLocations.add(SGST_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errorsInfo.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN202",
						"SGST Amount is incorrectly computed ", location)); 
			}
			if((cgstAmount.compareTo(taxcgstAmount) != 0)){
				errorLocations.add(CGST_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errorsInfo.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN207",
						"CGST Amount is incorrectly computed ", location)); 
			}
			if((igstAmount.compareTo(taxigstAmount) != 0)){
				errorLocations.add(IGST_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errorsInfo.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN208",
						"IGST Amount is incorrectly computed ", location)); 
			}
			}
		
		});
		
		return errorsInfo;
	}

}

