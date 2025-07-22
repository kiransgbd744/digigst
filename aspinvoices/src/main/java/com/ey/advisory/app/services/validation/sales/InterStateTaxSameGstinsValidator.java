/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * This class is responsible for validation of in case of supply type is other
 * than NSY/NON/NIL/EXT/EXPT/SEZ OR DTA the first two digits of supplier and
 * receiver GSTIN are same but POS are different from supplier GSTIN In case of
 * Inter-State Supply, CGST & SGST/UTGST Tax Rate and/or Tax Amount cannot be
 * applied
 * 
 * BUSINESS RULE_ID--BR_OUTWARD_54
 * 
 * @author Mahesh.Golla
 *
 *//*


@Component("interStateTaxSameGstins")
public class InterStateTaxSameGstinsValidator
		implements DocRulesValidator<OutwardTransDocument> {

	
	
	*//**
	 * In this method if validate the GSTINs of supplier and receiver first two
	 * digits and POS different from supplier GSTIN then it will check the CGST
	 * and SGST if it is amount rate is there instead of IGST this validation is
	 * fails error is returned with description
	 * 
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @return List<ProcessingResult>
	 *//*
private String supplyType;
	
		private String supplyGstin;
		private	String receiverGstin;
		private	String placeOfSupply;
		private String firstTwoDigitsSgstin;
	private String	firstTwoDigitsRgstin;
		
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
		 supplyType = document.getSupplyType();
}
if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){		 
supplyGstin = document.getSgstin();
}
if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
		 receiverGstin = document.getCgstin();
}
if(document.getPos()!=null && !document.getPos().isEmpty()){
		 placeOfSupply = document.getPos();
}
if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){	
		 firstTwoDigitsSgstin = supplyGstin.substring(0, 2);
}
if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
		 firstTwoDigitsRgstin = receiverGstin.substring(0, 2);
}
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal cgstAmount = item.getCgstAmount();
			if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
				if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){	
					if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
						if(document.getPos()!=null && !document.getPos().isEmpty()){
							if(igstAmount!=null){
								if(sgstAmount!=null){
									if(cgstAmount!=null){
							
			if (!supplyType.equalsIgnoreCase("NSY")
					&& !supplyType.equalsIgnoreCase("NIL")
					&& !supplyType.equalsIgnoreCase("NON")
					&& !supplyType.equalsIgnoreCase("EXT")
					&& !supplyType.equalsIgnoreCase("EXPT")
					&& !supplyType.equalsIgnoreCase("SEZ")
					&& !supplyType.equalsIgnoreCase("DTA")) {

				if (firstTwoDigitsSgstin.equalsIgnoreCase(firstTwoDigitsRgstin)
						&& !(placeOfSupply
								.equalsIgnoreCase(firstTwoDigitsSgstin))) {
					if (((sgstAmount.compareTo(BigDecimal.ZERO) > 0)
							|| (cgstAmount.compareTo(BigDecimal.ZERO) > 0))
							|| (igstAmount.compareTo(BigDecimal.ZERO) < 0)) {

						errorLocations.add(CGST_AMOUNT);
						errorLocations.add(SGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER237",
								"In case of Inter-State Supply, "
										+ "CGST & SGST/UTGST Tax Rate and/or "
										+ "Tax Amount cannot be applied",
								location));
					}
				}
			}
			
									}
								}
								
							}
						}
					}
				}
			}

		});

		return errors;
	}
}
*/