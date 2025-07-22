package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.BigDecimalNagativeValueUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class InwardDecimalNegativeAmountValidator implements DocRulesValidator<InwardTransDocument> {
	
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.CR, GSTConstants.RFV, GSTConstants.RCR,
					GSTConstants.RRFV, GSTConstants.AV,GSTConstants.RAV,GSTConstants.ADJ);
	// few rules commented because of that fields not their in inward 240 file structure
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if(ORGDOCNUM_REQUIRING_IMPORTS.contains(document.getDocType())) return errors;
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			BigDecimal	 taxableValue = item.getTaxableValue();
			BigDecimal invoiceValue =item.getLineItemAmt();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal advcessAmount = item.getCessAmountAdvalorem();
			BigDecimal specessAmount = item.getCessAmountSpecific();
			BigDecimal statecessAmount = item.getStateCessAmount();
			BigDecimal otherValues = item.getOtherValues();
			BigDecimal stateCessSpecificAmt = item.getStateCessSpecificAmt();
			if(stateCessSpecificAmt==null){
				stateCessSpecificAmt=BigDecimal.ZERO;
			}
			if(invoiceValue==null){
				invoiceValue=BigDecimal.ZERO;
			}
			if(taxableValue==null){
				taxableValue=BigDecimal.ZERO;
			}
			if (!BigDecimalNagativeValueUtil.validate(invoiceValue)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.INVOICE_VALUE);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1084",
						"Invalid Invoice Value",
						location));
			}
			
			if (!BigDecimalNagativeValueUtil.validate(taxableValue)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.TAXABLE_VALUE);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1074",
						"Invalid Item Assessable Amount",
						location));
			}
		//	BigDecimal taxableValueadj = item.getAdjustedTaxableValue();
			//BigDecimal igstadj = item.getAdjustedIgstAmt();
			//BigDecimal cgstadj = item.getAdjustedCgstAmt();
			//BigDecimal sgstadj = item.getAdjustedSgstAmt();
			
			//BigDecimal advcessamountadj = item.getAdjustedCessAmtAdvalorem();
			///BigDecimal specificcessamountadj = item.getAdjustedCessAmtSpecific();
			//BigDecimal statecessamountadj = item.getAdjustedStateCessAmt();
			
			/*if(advcessamountadj==null){
				advcessamountadj=BigDecimal.ZERO;
			}
			if(specificcessamountadj==null){
				specificcessamountadj=BigDecimal.ZERO;
			}
			if(statecessamountadj==null){
				statecessamountadj=BigDecimal.ZERO;
			}*/
			
			if(igstAmount==null){
				igstAmount=BigDecimal.ZERO;
			}
			if(cgstAmount==null){
				cgstAmount=BigDecimal.ZERO;
			}
			if(sgstAmount==null){
				sgstAmount=BigDecimal.ZERO;
			}
			if(advcessAmount==null){
				advcessAmount=BigDecimal.ZERO;
			}
			if(specessAmount==null){
				specessAmount=BigDecimal.ZERO;
			}
			if(statecessAmount==null){
				statecessAmount=BigDecimal.ZERO;
			}
			if(otherValues==null){
				otherValues=BigDecimal.ZERO;
			}
			
			/*if(taxableValueadj==null){
				taxableValueadj=BigDecimal.ZERO;
			}
			if(igstadj==null){
				igstadj=BigDecimal.ZERO;
			}
			if(cgstadj==null){
				cgstadj=BigDecimal.ZERO;
			}
			if(sgstadj==null){
				sgstadj=BigDecimal.ZERO;
			}
			*/
			
			
			
			if (!BigDecimalNagativeValueUtil.validate(stateCessSpecificAmt)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.STATE_CESS_SPECIFIC_AMOUNT);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER0086",
						"Invalid State Cess Specific Amount",
						location));
			}
			
			if (!BigDecimalNagativeValueUtil.validate(igstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.igstamount);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1076",
						"Invalid IGST Amount",
						location));
			}
			if (!BigDecimalNagativeValueUtil.validate(cgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.cgstamount);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1078",
						"Invalid CGST Amount",
						location));
			}
			if (!BigDecimalNagativeValueUtil.validate(sgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.sgstamount);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1080",
						"Invalid SGST / UTGST Amount",
						location));
			}
			/*if (!BigDecimalNagativeValueUtil.validate(advcessAmount) 
					|| !BigDecimalNagativeValueUtil.validate(specessAmount) 
					|| !BigDecimalNagativeValueUtil.validate(statecessAmount)) {*/
				
				if(!BigDecimalNagativeValueUtil.validate(advcessAmount)){
					List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.CESS_AMT_ADVALOREM);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER0082",
						"Invalid CessAdvaloremAmount",
						location));
				}
				if (!BigDecimalNagativeValueUtil.validate(specessAmount)) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.CESS_AMT_SPECIFIC);
					TransDocProcessingResultLoc location 
					= new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							"ER1082",
							"Invalid Cess Specific Amount",
							location));
				}
				
				if (!BigDecimalNagativeValueUtil.validate(statecessAmount)) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.CESS_AMT_STATE);
					TransDocProcessingResultLoc location 
					= new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							"ER0084",
							"Invalid StateCessAdvaloremAmount",
							location));
				}
			
			if (!BigDecimalNagativeValueUtil.validate(otherValues)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.OTHERVALUE);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1083",
						"Invalid Item Other Charges",
						location));
			}
			/*if (!BigDecimalNagativeValueUtil.validate(taxableValueadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.TaxableValueAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1091",
						"Invalid Taxable Value Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil.validate(igstadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.IntegratedTaxAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1092",
						"Invalid IGST Amount Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil.validate(cgstadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.CentralTaxAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1093",
						"Invalid CGST Amount Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil.validate(sgstadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.StateUTTaxAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1094",
						"Invalid SGST / UTGST Amount Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil
					               .validate(advcessamountadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.AdvaloremCessAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1095",
						"Invalid Advalorem Cess Amount Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil
					        .validate(specificcessamountadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.SpecificCessAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1096",
						"Invalid Specific Cess Amount Adjusted",
						location));
			}
			if (!BigDecimalNagativeValueUtil
					        .validate(statecessamountadj)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.StateCessAmountAdjusted);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						 "ER1158",
						"Invalid State Cess Amount Adjusted",
						location));
			}*/

		});
		return errors;
	}
	
}
