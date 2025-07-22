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
 * This class responsible for validate the Intra-State GSTIN is in case of
 * supply type other than NSY/NIL/DTA/EXT/EXPT/SEZ OR NON then the supplier and
 * receiver GSTINs of first two digits are different but POS is same supplier
 * GSTIN In case of Intra-State Supply, IGST Tax Rate and and/or Tax Amount
 * cannot be applied
 * 
 * BUSINESS RULE_ID--BR_OUTWARD_55
 * 
 * @author Mahesh.Golla
 *
 *//*

@Component("intraStateIGSTValidator")
public class IntraStateIGSTValidator
		implements DocRulesValidator<OutwardTransDocument> {

	*//**
	 * This Method validate if the supply type is other then NSY/NIL/DTA/SEZ
	 * EXT/EXPT the first digits of supplier and receiver GSTIN are same but POS
	 * is same as Supplier GSTIN If the validation fails error is returned with
	 * description
	 * 
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @return List<ProcessingResult>
	 * 
	 *//*
	private String supplyType ;
	private String supplyGstin ;
	private String receiverGstin;
	private String placeOfSupply;
private String firstTwoDigitsSgstin;
private String firstTwoDigitsRgstin;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
		if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
		 supplyType = document.getSupplyType();}
		if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
		 supplyGstin = document.getSgstin();}
		if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
		 receiverGstin = document.getCgstin();}
		if(document.getPos()!=null && !document.getPos().isEmpty()){
	      placeOfSupply = document.getPos();}
		if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
		firstTwoDigitsSgstin = supplyGstin.substring(0, 2);}
		if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
		firstTwoDigitsRgstin = receiverGstin.substring(0, 2);}

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
	if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
		if(document.getPos()!=null && !document.getPos().isEmpty()){
			if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
			if(item.getIgstAmount()!=null){
				if(item.getSgstAmount()!=null){
					if(item.getCgstAmount()!=null){
						
				
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal cgstAmount = item.getCgstAmount();
			
			if (!supplyType.equalsIgnoreCase("NSY")
					&& !supplyType.equalsIgnoreCase("NIL")
					&& !supplyType.equalsIgnoreCase("NON")
					&& !supplyType.equalsIgnoreCase("EXT")
					&& !supplyType.equalsIgnoreCase("EXPT")
					&& !supplyType.equalsIgnoreCase("SEZ")
					&& !supplyType.equalsIgnoreCase("DTA")) {

				if (!firstTwoDigitsSgstin.equalsIgnoreCase(firstTwoDigitsRgstin)
						&& (placeOfSupply
								.equalsIgnoreCase(firstTwoDigitsSgstin))) {
					if (((sgstAmount.compareTo(BigDecimal.ZERO) < 0)
							&& (cgstAmount.compareTo(BigDecimal.ZERO) < 0))
							|| (igstAmount.compareTo(BigDecimal.ZERO) > 0)) {

						errorLocations.add(IGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER238",
								"In case of Intra-State Supply, IGST Tax "
										+ "Rate and and/or Tax Amount "
										+ "cannot be applied",
								location));
					}
				}

			}
	}
					
				}		}
				
			}
		}
	}
}
		});

		return errors;
	}

}
*/