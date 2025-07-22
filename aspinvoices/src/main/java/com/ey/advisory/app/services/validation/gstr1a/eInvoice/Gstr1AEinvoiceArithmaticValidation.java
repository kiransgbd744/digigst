package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AEinvoiceArithmaticValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		BigDecimal invoiceCessAdvaloremAmount = document
				.getInvoiceCessAdvaloremAmount();
		BigDecimal invoiceCessSpecificAmount = document
				.getInvoiceCessSpecificAmount();
		BigDecimal invoiceStateCessAmount = document
				.getInvoiceStateCessAmount();
		BigDecimal igstAmount = document.getInvoiceIgstAmount();
		BigDecimal cgstAmount = document.getInvoiceCgstAmount();
		BigDecimal sgstAmount = document.getInvoiceSgstAmount();
		BigDecimal invoiceAssessableAmount = document
				.getInvoiceAssessableAmount();
		if (invoiceCessAdvaloremAmount == null) {
			invoiceCessAdvaloremAmount = BigDecimal.ZERO;
		}
		if (invoiceCessSpecificAmount == null) {
			invoiceCessSpecificAmount = BigDecimal.ZERO;
		}
		if (invoiceStateCessAmount == null) {
			invoiceStateCessAmount = BigDecimal.ZERO;
		}
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		if (invoiceAssessableAmount == null) {
			invoiceAssessableAmount = BigDecimal.ZERO;
		}
		BigDecimal itemtaxableValue = BigDecimal.ZERO;
		BigDecimal itemIgst = BigDecimal.ZERO;
		BigDecimal itemCgst = BigDecimal.ZERO;
		BigDecimal itemSgst = BigDecimal.ZERO;
		BigDecimal cessAdv = BigDecimal.ZERO;
		BigDecimal cessSpe = BigDecimal.ZERO;
		BigDecimal cessState = BigDecimal.ZERO;
		// BigDecimal invoiceValues = BigDecimal.ZERO;
		for (Gstr1AOutwardTransDocLineItem item : items) {
			itemtaxableValue = itemtaxableValue
					.add(item.getTaxableValue() == null ? BigDecimal.ZERO
							: item.getTaxableValue());
			itemIgst = itemIgst.add(item.getIgstAmount() == null
					? BigDecimal.ZERO : item.getIgstAmount());
			itemCgst = itemCgst.add(item.getCgstAmount() == null
					? BigDecimal.ZERO : item.getCgstAmount());
			itemSgst = itemSgst.add(item.getSgstAmount() == null
					? BigDecimal.ZERO : item.getSgstAmount());
			cessAdv = cessAdv.add(item.getCessAmountAdvalorem() == null
					? BigDecimal.ZERO : item.getCessAmountAdvalorem());
			cessSpe = cessSpe.add(item.getCessAmountSpecific() == null
					? BigDecimal.ZERO : item.getCessAmountSpecific());
			cessState = cessState.add(item.getStateCessAmount() == null
					? BigDecimal.ZERO : item.getStateCessAmount());
		}

		/*
		 * items.forEach(item -> { itemtaxableValue.add(item.getTaxableValue()
		 * == null ? BigDecimal.ZERO : item.getTaxableValue()); });
		 * items.forEach(item -> { itemIgst.add(item.getIgstAmount() == null ?
		 * BigDecimal.ZERO : item.getIgstAmount()); }); items.forEach(item -> {
		 * itemCgst.add(item.getCgstAmount() == null ? BigDecimal.ZERO :
		 * item.getCgstAmount()); }); items.forEach(item -> {
		 * itemSgst.add(item.getSgstAmount() == null ? BigDecimal.ZERO :
		 * item.getSgstAmount()); }); items.forEach(item -> {
		 * cessAdv.add(item.getCessAmountAdvalorem() == null ? BigDecimal.ZERO :
		 * item.getCessAmountAdvalorem()); }); items.forEach(item -> {
		 * cessSpe.add(item.getCessAmountSpecific() == null ? BigDecimal.ZERO :
		 * item.getCessAmountSpecific()); }); items.forEach(item -> {
		 * cessState.add(item.getStateCessAmount() == null ? BigDecimal.ZERO :
		 * item.getStateCessAmount()); });
		 */
		if (invoiceCessAdvaloremAmount.compareTo(cessAdv) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INV_ADV_CESSAMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11015",
					"Invoice Cess Advalorem is incorrectly computed",
					location));
		}
		if (invoiceAssessableAmount.compareTo(itemtaxableValue) != 0) {
			String[] errorLocations = new String[] { GSTConstants.INVASSVAL };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11011",
					"Invoice Asessable Amount is incorrectly computed",
					location));
		}
		if (igstAmount.compareTo(itemIgst) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INV_IGST_AMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11012",
					"InvoiceIGST is incorrectly computed", location));
		}
		if (cgstAmount.compareTo(itemCgst) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INV_CGST_AMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11013",
					"Invoice CGST is incorrectly computed", location));
		}
		if (sgstAmount.compareTo(itemSgst) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INV_SGST_AMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11014",
					"Invoice SGST is incorrectly computed", location));
		}
		if (invoiceCessSpecificAmount.compareTo(cessSpe) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INVSPECESSAMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11016",
					"Invoice Cess Specific is incorrectly computed", location));
		}
		if (invoiceStateCessAmount.compareTo(cessState) != 0) {
			String[] errorLocations = new String[] {
					GSTConstants.INVSTATECESSAMOUNT };
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations);
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN11017",
					"Invoice State Cess is incorrectly computed", location));
		}
		/*
		 * for(OutwardTransDocLineItem item:items){ BigDecimal lineItemAmt =
		 * item.getLineItemAmt(); if(lineItemAmt==null){
		 * lineItemAmt=BigDecimal.ZERO; }
		 * invoiceValues=invoiceValues.add(lineItemAmt);
		 * 
		 * }
		 */
		/*
		 * BigDecimal sumInvamount=invoiceCessSpecificAmount
		 * .add(invoiceCessSpecificAmount) //.add(invoiceStateCessAmount)
		 * //.add(igstAmount) //.add(cgstAmount) //.add(sgstAmount)
		 * //.add(invoiceAssessableAmount) .add(invoiceCessAdvaloremAmount);
		 * BigDecimal taxAmount = (sumInvamount.compareTo(invoiceValues) > 0) ?
		 * sumInvamount.subtract(invoiceValues).abs() :
		 * invoiceValues.subtract(sumInvamount).abs();
		 * 
		 * if(taxAmount.compareTo(BigDecimal.ZERO) > 2){ String[] errorLocations
		 * = new String[] { GSTConstants.INVOICE_VALUE };
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations); errors.add(new
		 * ProcessingResult(APP_VALIDATION, ProcessingResultType.INFO,
		 * "IN11018", "Invoice Value is incorrectly computed", location)); }
		 */
		return errors;
	}

}
