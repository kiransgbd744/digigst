/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class TotalInvValAtithmeticValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		BigDecimal taxablevalue = document.getInvoiceAssessableAmount();
		BigDecimal invoiceSgstAmount = document.getInvoiceSgstAmount();
		BigDecimal invoiceCgstAmount = document.getInvoiceCgstAmount();
		BigDecimal invoiceIgstAmount = document.getInvoiceIgstAmount();
		BigDecimal invoiceDiscount = document.getInvoiceDiscount();
		BigDecimal invoiceCessAdvaloremAmount = document
				.getInvoiceCessAdvaloremAmount();
		BigDecimal invoiceCessSpecificAmount = document
				.getInvoiceCessSpecificAmount();
		BigDecimal invoiceStateCessAmount = document
				.getInvoiceStateCessAmount();
		BigDecimal otherValues = document.getOtherValues();
		
		if (taxablevalue == null) {
			taxablevalue = BigDecimal.ZERO;
		}
		if (invoiceSgstAmount == null) {
			invoiceSgstAmount = BigDecimal.ZERO;
		}
		if (invoiceCgstAmount == null) {
			invoiceCgstAmount = BigDecimal.ZERO;
		}
		if (invoiceIgstAmount == null) {
			invoiceIgstAmount = BigDecimal.ZERO;
		}
		if (invoiceDiscount == null) {
			invoiceDiscount = BigDecimal.ZERO;
		}
		if (invoiceCessAdvaloremAmount == null) {
			invoiceCessAdvaloremAmount = BigDecimal.ZERO;
		}
		if (invoiceCessSpecificAmount == null) {
			invoiceCessSpecificAmount = BigDecimal.ZERO;
		}
		if (invoiceStateCessAmount == null) {
			invoiceStateCessAmount = BigDecimal.ZERO;
		}
		if (otherValues == null) {
			otherValues = BigDecimal.ZERO;
		}
		
		List<OutwardTransDocLineItem> items = document.getLineItems();
		BigDecimal totalInvoiceValue = BigDecimal.ZERO;
		BigDecimal sumOfAll = taxablevalue.add(invoiceSgstAmount)
				.add(invoiceCgstAmount).add(invoiceIgstAmount)
				.add(invoiceCessAdvaloremAmount).add(invoiceCessSpecificAmount)
				.add(invoiceStateCessAmount).add(otherValues);
		BigDecimal alldata = sumOfAll.subtract(invoiceDiscount);

		for (OutwardTransDocLineItem item : items) {

			BigDecimal lineItemAmt = item.getLineItemAmt();

			if (lineItemAmt == null) {
				lineItemAmt = BigDecimal.ZERO;
			}
			totalInvoiceValue = totalInvoiceValue.add(lineItemAmt);

		}

		if(alldata.compareTo(totalInvoiceValue) !=0){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.INVOICE_VALUE);
			TransDocProcessingResultLoc location 
			      = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO,
					 "IN11018",
					"Invoice Value is incorrectly computed.",
						location));
		}
		return errors;
	}

}
*/