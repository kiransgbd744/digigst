package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CESS_AMT_ADV;
import static com.ey.advisory.common.GSTConstants.CESS_AMT_SPECIFIC;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.EXT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.NIL;
import static com.ey.advisory.common.GSTConstants.NON;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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
import com.google.common.collect.ImmutableList;
/**
 * 
 * This class is responsible to Tax Rate and Tax Amount need to be 0 or BLANK in
 * case Supply Type is NIL or NON or EXT
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_46
 * 
 * @author Murali.Singanamala
 */

@Component("TaxRateAndTaxAmountValidator")
public class TaxRateAndTaxAmountValidator
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> NILORNONOREXT = ImmutableList
			.of(NIL,NON,EXT);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
	
		List<String> errorLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			//BigDecimal taxRate = item.getTaxRate();
			BigDecimal taxAmount = new BigDecimal(0);
			taxAmount = item.getIgstAmount().add(item.getSgstAmount())
					.add(item.getCgstAmount()).add(item.getCessAmountSpecific())
					.add(item.getCessAmountAdvalorem());
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal cessAmount = item.getCessAmountSpecific()
			.add(item.getCessAmountAdvalorem());
			/**
			 * This method first get the taxrate and taxAmount and amount is not
			 * equal to 0 then it throw an error
			 * 
			 */
			if(document.getSupplyType() != null 
					&& !document.getSupplyType().isEmpty()){
			if (NILORNONOREXT.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) {
if(taxAmount.compareTo(BigDecimal.ZERO) != 0 
|| cessAmount.compareTo(BigDecimal.ZERO) != 0 
|| igstAmount.compareTo(BigDecimal.ZERO) != 0 
|| cgstAmount.compareTo(BigDecimal.ZERO) != 0 
|| sgstAmount.compareTo(BigDecimal.ZERO) != 0 ){
				//errorLocations.add(TAX_RATE);
				errorLocations.add(IGST_AMOUNT);
				errorLocations.add(CGST_AMOUNT);
				errorLocations.add(SGST_AMOUNT);
				errorLocations.add(SGST_AMOUNT);
				errorLocations.add(CESS_AMT_SPECIFIC);
				errorLocations.add(CESS_AMT_ADV);
				
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER233",
						"Tax Rate and Tax Amount need to be 0 or BLANK in case "
								+ "Supply Type is NIL or NON or EXT Error",
						location));
			}}
			}
		});

		return errors;
	}
}
