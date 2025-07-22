/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.*;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants
.*;
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
 * In this class responsible for validation of in case supply type is other than
 * NSY/NON/NIL/EXT/EXPT/SEZ OR DTA the first two digits of supplier and receiver
 * GSTIN are same but POS are blank In case of Intra-State Supply, IGST Tax Rate
 * and and/or Tax Amount cannot be applied
 * 
 * BUSINESS RULE_ID--BR_OUTWARD_56
 * 
 * @author Mahesh.Golla
 *
 *//*
@Component("intraStateIgstPosDiffValidator")
public class IntraStateIgstPosDiffValidator
		implements DocRulesValidator<OutwardTransDocument> {
	*//**
	 * This Method validate if the supply type is other then NSY/NIL/DTA/SEZ
	 * EXT/EXPT the first digits of supplier and receiver GSTIN are same but POS
	 * is blank If the validation fails error is returned with description
	 * 
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @return List<ProcessingResult>
	 * 
	 *//*
private String supplyType;
private String supplyGstin;
private String receiverGstin;
private String placeOfSupply;
private String firstTwoDigitsRgstin;
private String firstTwoDigitsSgstin;
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
		if(document.getPos()!=null){
		 placeOfSupply = document.getPos();
		}
		if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
			 firstTwoDigitsSgstin = supplyGstin.substring(0, 2);
			}
		if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
		firstTwoDigitsRgstin = receiverGstin.substring(0, 2);
		}
		if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
			if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
				if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
					if(document.getPos()!=null){
					IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal cgstAmount = item.getCgstAmount();
			if(igstAmount!=null){
				if(sgstAmount!=null){
					if(cgstAmount!=null){
				
			if (!supplyType.equalsIgnoreCase(NSY)
					&& !supplyType.equalsIgnoreCase(NIL)
					&& !supplyType.equalsIgnoreCase(NON)
					&& !supplyType.equalsIgnoreCase(EXT)
					&& !supplyType.equalsIgnoreCase(EXPT)
					&& !supplyType.equalsIgnoreCase(SEZ)
					&& !supplyType.equalsIgnoreCase(DTA)) {
				if (firstTwoDigitsSgstin.equalsIgnoreCase(firstTwoDigitsRgstin)
						&& (placeOfSupply.equalsIgnoreCase(""))) {
					if (((sgstAmount.compareTo(BigDecimal.ZERO) < 0)
							&& (cgstAmount.compareTo(BigDecimal.ZERO) < 0))
							|| (igstAmount.compareTo(BigDecimal.ZERO) > 0)) {

						errorLocations.add(IGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER238",
								"In case of Intra-State Supply, IGST Tax "
										+ "Rate and and/or Tax Amount cannot be "
										+ "applied",
								location));
					}
					
				}
			}
		}
				}

			}
		});
					}}}}
		return errors;
	}

}
*/