package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CESS_AMT_ADV;
import static com.ey.advisory.common.GSTConstants.CESS_AMT_SPECIFIC;
import static com.ey.advisory.common.GSTConstants.CESS_AMT_STATE;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
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
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class EInvoiceArithmeticCheckValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private boolean isValidTax(BigDecimal amount, BigDecimal assembleValue,
			BigDecimal rate) {
		
		BigDecimal mulamount = assembleValue.multiply(rate);
		if(mulamount.compareTo(amount)!=0){
			return false;
		}
		return true;
	}

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {

			OutwardTransDocLineItem item = items.get(idx);
			
			BigDecimal taxableValue = item.getTaxableValue();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal SgstRate = item.getSgstRate();
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal CsgstRate = item.getCgstRate();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal IgstRate = item.getIgstRate();
			BigDecimal quantity = item.getQty();
			BigDecimal unitPrice = item.getUnitPrice();
			BigDecimal specificCessAmount = item.getCessAmountSpecific();
			BigDecimal specificCessRate = item.getCessRateSpecific();
			BigDecimal cessAmountAdvalorem = item.getCessAmountAdvalorem();
			BigDecimal cessRateAdvalorem = item.getCessRateAdvalorem();
			BigDecimal stateCessAmount = item.getStateCessAmount();
			BigDecimal stateCessRate = item.getStateCessRate();
			
			if(stateCessRate==null){
				stateCessRate=BigDecimal.ZERO;
			}
			if(stateCessAmount==null){
				stateCessAmount=BigDecimal.ZERO;
			}
			if(cessRateAdvalorem==null){
				cessRateAdvalorem=BigDecimal.ZERO;
			}
			if(cessAmountAdvalorem==null){
				cessAmountAdvalorem=BigDecimal.ZERO;
			}
			
			if(specificCessRate==null){
				specificCessRate=BigDecimal.ZERO;
			}
			if(specificCessAmount==null){
				specificCessAmount=BigDecimal.ZERO;
			}
			if(taxableValue==null){
				taxableValue=BigDecimal.ZERO;
			}
			if(sgstAmount==null){
				sgstAmount=BigDecimal.ZERO;
			}
			if(SgstRate==null){
				SgstRate=BigDecimal.ZERO;
			}
			if(cgstAmount==null){
				cgstAmount=BigDecimal.ZERO;
			}
			if(CsgstRate==null){
				CsgstRate=BigDecimal.ZERO;
			}
			if(igstAmount==null){
				igstAmount=BigDecimal.ZERO;
			}
			if(IgstRate==null){
				IgstRate=BigDecimal.ZERO;
			}
			if(quantity==null){
				quantity=BigDecimal.ZERO;
			}
			
			if(unitPrice==null){
				unitPrice=BigDecimal.ZERO;
			}
			
			
			if (!isValidTax(taxableValue,quantity,
					unitPrice)) {
				String[] errorLocations = new String[] { CGST_AMOUNT };
				TransDocProcessingResultLoc location 
				            = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11004",
						"Item Asessable Amount is incorrectly computed",
						location));
			}
			
			if (!isValidTax(cgstAmount,taxableValue,
					CsgstRate)) {
				String[] errorLocations = new String[] { CGST_AMOUNT };
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11005",
						"CGST Amount is incorrectly computed ", location));
			}
			if (!isValidTax(sgstAmount,taxableValue,
					SgstRate)) {
				String[] errorLocations = new String[] { SGST_AMOUNT };
				TransDocProcessingResultLoc location 
				                    = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11006",
						"SGST Amount is incorrectly computed ", location));
			}
			if (!isValidTax(igstAmount,taxableValue,
					IgstRate)) {
				String[] errorLocations = new String[] { IGST_AMOUNT };
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11007",
						"IGST Amount is incorrectly computed ", location));
			}
			if (!isValidTax(specificCessAmount,
					taxableValue, specificCessRate)) {
				String[] errorLocations = new String[] { CESS_AMT_SPECIFIC };
				TransDocProcessingResultLoc location 
				                    = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11008",
						"Cess Specific is incorrectly computed", location));
			}
			if (!isValidTax(cessAmountAdvalorem,
					taxableValue,cessRateAdvalorem)) {
				String[] errorLocations = new String[] { CESS_AMT_ADV };
				TransDocProcessingResultLoc location 
				                   = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11009",
						"Cess Advalorem is incorrectly computed", location));
			}
			if (!isValidTax(stateCessAmount,taxableValue,
					stateCessRate)) {
				String[] errorLocations = new String[] { CESS_AMT_STATE };
				TransDocProcessingResultLoc location 
				                    = new TransDocProcessingResultLoc(
						idx, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN11010",
						"State Cess is incorrectly computed", location));
			}
		});

		return errors;
	}

}
