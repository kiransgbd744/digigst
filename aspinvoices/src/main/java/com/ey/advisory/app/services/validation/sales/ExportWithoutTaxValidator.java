package com.ey.advisory.app.services.validation.sales;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import com.google.common.base.Strings;

import static com.ey.advisory.common.GSTConstants.*;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * @author Mahesh.Golla
 * 
 *         BR_OUTWARD_35
 */

@Component("ExportWithoutTaxValidator")
public class ExportWithoutTaxValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE = ImmutableList.of(
			GSTConstants.NIL,
			GSTConstants.NON, GSTConstants.EXT);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		if(Strings.isNullOrEmpty(document.getSupplyType())) return errors;
		if(!SUPPLYTYPE.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) return errors;
				IntStream.range(0, items.size()).forEach(idx -> {
					OutwardTransDocLineItem item = items.get(idx);
					BigDecimal cgstAmount = item.getCgstAmount();
					BigDecimal sgstAmount = item.getSgstAmount();
					BigDecimal igstAmount = item.getIgstAmount();
					BigDecimal cessAmountSpecific=	item.getCessAmountSpecific();
					BigDecimal cessAmountAdvalorem=	item.getCessAmountAdvalorem();
					BigDecimal igstRate=item.getIgstRate();
					BigDecimal cgstRate=item.getIgstRate();
					BigDecimal sgstRate=item.getIgstRate();
					
					BigDecimal cessRateAdvalorem=item.getCessRateAdvalorem();
					BigDecimal cessRateSpecific=item.getCessRateSpecific();
					
					if (cgstAmount == null) {
						cgstAmount = BigDecimal.ZERO;
					}
					if (sgstAmount == null) {
						sgstAmount = BigDecimal.ZERO;
					}
					if (igstAmount == null) {
						igstAmount = BigDecimal.ZERO;
					}
					if (cessAmountSpecific == null) {
						cessAmountSpecific = BigDecimal.ZERO;
					}
if(cessAmountAdvalorem==null){
	cessAmountAdvalorem=BigDecimal.ZERO;	
}
if(cessRateSpecific==null){
	cessRateSpecific=BigDecimal.ZERO;	
}
if(cessRateAdvalorem==null){
	cessRateAdvalorem=BigDecimal.ZERO;	
}

if(igstRate==null){
	igstRate=BigDecimal.ZERO;
}
if(cgstRate==null){
	cgstRate=BigDecimal.ZERO;
}
if(sgstRate==null){
	sgstRate=BigDecimal.ZERO;
}
BigDecimal cessRate=cessRateAdvalorem.add(cessRateSpecific);
					
					BigDecimal cessAmount = cessAmountSpecific
							.add(cessAmountAdvalorem);
					BigDecimal totalTaxAmount = igstAmount
							.add(sgstAmount).add(cgstAmount)
							.add(cessAmount);
							

					if (sgstAmount.compareTo(BigDecimal.ZERO) != 0
							|| cgstAmount.compareTo(BigDecimal.ZERO) != 0
							|| igstAmount.compareTo(BigDecimal.ZERO) != 0
							|| cessAmount.compareTo(BigDecimal.ZERO) != 0
							|| totalTaxAmount.compareTo(BigDecimal.ZERO) != 0
							|| igstRate
									.compareTo(BigDecimal.ZERO) != 0
							|| cgstRate
									.compareTo(BigDecimal.ZERO) != 0
							|| sgstRate
									.compareTo(BigDecimal.ZERO) != 0
							|| cessRate
									.compareTo(BigDecimal.ZERO) != 0) {
						Set<String> errorLocations = new HashSet<>();
						if (sgstAmount.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.SGST_AMOUNT);
						}
						if (cgstAmount.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.CGST_AMOUNT);
						}
						if (igstAmount.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.IGST_AMOUNT);
						}
						if (cessAmount.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.CESS_AMT_ADV);
							errorLocations.add(GSTConstants.CESS_AMT_SPECIFIC);
						}
						if (sgstRate
								.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.SGST_RATE);
						}
						if (cgstRate
								.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.CGST_RATE);
						}
						if (igstRate
								.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.IGST_RATE);
						}
						if (cessRate
								.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.CESS_RATE_ADV);
							errorLocations.add(GSTConstants.CESS_RATE_SPECIFIC);
						}
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0505", "Tax cannot be applied", location));
					}

				});
		
		return errors;
	}

}
