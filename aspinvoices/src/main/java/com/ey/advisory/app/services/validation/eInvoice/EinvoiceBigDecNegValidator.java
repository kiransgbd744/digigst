/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.BigDecimalNagativeValueUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class EinvoiceBigDecNegValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();

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
		BigDecimal taxTotal = document.getTaxTotal();
		BigDecimal invoiceDiscount = document.getInvoiceDiscount();
		BigDecimal invoiceOtherCharges = document.getInvoiceOtherCharges();
		BigDecimal invoiceAllowancesOrCharges = document.getInvoiceAllowancesOrCharges();
		BigDecimal sumOfInvoiceLineNetAmount = document.getSumOfInvoiceLineNetAmount();
		BigDecimal sumOfAllowancesOnDocumentLevel = document.getSumOfAllowancesOnDocumentLevel();
		BigDecimal sumOfChargesOnDocumentLevel = document.getSumOfChargesOnDocumentLevel();
		BigDecimal freightAmount = document.getFreightAmount();
		BigDecimal insuranceAmount = document.getInsuranceAmount();
		BigDecimal packagingAndForwardingCharges = document.getPackagingAndForwardingCharges();
		BigDecimal invoiceValueFc = document.getInvoiceValueFc();
		if (invoiceDiscount == null) {
			invoiceDiscount = BigDecimal.ZERO;
		}
		if (invoiceOtherCharges == null) {
			invoiceOtherCharges = BigDecimal.ZERO;
		}
		if (invoiceAllowancesOrCharges == null) {
			invoiceAllowancesOrCharges = BigDecimal.ZERO;
		}
		if (sumOfInvoiceLineNetAmount == null) {
			sumOfInvoiceLineNetAmount = BigDecimal.ZERO;
		}
		if (sumOfAllowancesOnDocumentLevel == null) {
			sumOfAllowancesOnDocumentLevel = BigDecimal.ZERO;
		}
		if (sumOfChargesOnDocumentLevel == null) {
			sumOfChargesOnDocumentLevel = BigDecimal.ZERO;
		}
		if (freightAmount == null) {
			freightAmount = BigDecimal.ZERO;
		}
		if (insuranceAmount == null) {
			insuranceAmount = BigDecimal.ZERO;
		}
		if (packagingAndForwardingCharges == null) {
			packagingAndForwardingCharges = BigDecimal.ZERO;
		}
		if (invoiceValueFc == null) {
			invoiceValueFc = BigDecimal.ZERO;
		}
		if (taxTotal == null) {
			taxTotal = BigDecimal.ZERO;
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
		
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceDiscount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.invoiceDiscount);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10068",
					"Invalid Invoice Discount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceOtherCharges)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.invoiceOtherCharges);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10069",
					"Invalid Invoice Other Charges", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceAllowancesOrCharges)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.invoiceAllowancesOrCharges);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10070",
					"Invalid InvoiceAllowancesOrCharges", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				sumOfInvoiceLineNetAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.sumOfInvoiceLineNetAmount);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10071",
					"Invalid Sum Invoice line net amount", location));
		}
		
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				sumOfAllowancesOnDocumentLevel)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_VALUE_FC);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10072",
					"Invalid Sum of allowances on document level", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				sumOfChargesOnDocumentLevel)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.sumOfChargesOnDocumentLevel);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10073",
					"Invalid Sum of charges on document level", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				freightAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.freightAmount);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10074",
					"Invalid Freight Amount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				insuranceAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.insuranceAmount);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10075",
					"Invalid Insurance Amount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				packagingAndForwardingCharges)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.packagingAndForwardingCharges);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10076",
					"Invalid Packaging And Forwarding Charges", location));
		}
		
		
		
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceValueFc)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_VALUE_FC);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10090",
					"Invalid InvoiceValueFC", location));
		}
		
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				taxTotal)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.TAXTOTAL);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10085",
					"Invalid Tax Total", location));
		}
		
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceAssessableAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INVASSVAL);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10077",
					"Invalid Invoice Assesable Amount", location));
		}

		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceCessAdvaloremAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_ADV_CESSAMOUNT);
			TransDocProcessingResultLoc location 
			             = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10082",
					"Invalid Invoice Cess Advalorem Amount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceCessSpecificAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INVSPECESSAMOUNT);
			TransDocProcessingResultLoc location 
			               = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10083",
					"Invalid Invoice Cess Specific Amount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document,
				invoiceStateCessAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INVSTATECESSAMOUNT);
			TransDocProcessingResultLoc location 
			        = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10084",
					"Invalid Invoice State Cess Amount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document, igstAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_IGST_AMOUNT);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10079",
					"Invalid Invoice IGSTAmount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document, cgstAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_CGST_AMOUNT);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10080",
					"Invalid Invoice CGSTAmount", location));
		}
		if (!BigDecimalNagativeValueUtil.Negvalidate(document, sgstAmount)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INV_SGST_AMOUNT);
			TransDocProcessingResultLoc location 
			                = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10081",
					"Invalid Invoice SGSTAmount", location));
		}
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
			BigDecimal tcsCgstAmount = item.getTcsCgstAmount();
			BigDecimal tcsSgstAmount = item.getTcsSgstAmount();
			BigDecimal tdsIgstAmount = item.getTdsIgstAmount();
			BigDecimal tdsCgstAmount = item.getTdsCgstAmount();
			BigDecimal tdsSgstAmount = item.getTdsSgstAmount();
			BigDecimal itemDiscount = item.getItemDiscount();
			BigDecimal preTaxAmount = item.getPreTaxAmount();
			BigDecimal invoiceLineNetAmount = item.getInvoiceLineNetAmount();
			BigDecimal itemTotal = item.getItemTotal();
			BigDecimal taxOn = item.getTaxOn();
			BigDecimal amount = item.getAmount();
			BigDecimal totalitemAmunt = item.getTotalItemAmount();

			BigDecimal itemAmount = item.getItemAmount();

			if (totalitemAmunt == null) {
				totalitemAmunt = BigDecimal.ZERO;
			}

			if (itemAmount == null) {
				itemAmount = BigDecimal.ZERO;
			}

			if (tcsCgstAmount == null) {
				tcsCgstAmount = BigDecimal.ZERO;
			}
			
			if (tcsSgstAmount == null) {
				tcsSgstAmount = BigDecimal.ZERO;
			}
			if (tdsIgstAmount == null) {
				tdsIgstAmount = BigDecimal.ZERO;
			}
			if (tdsCgstAmount == null) {
				tdsCgstAmount = BigDecimal.ZERO;
			}
			if (tdsSgstAmount == null) {
				tdsSgstAmount = BigDecimal.ZERO;
			}
			if (itemDiscount == null) {
				itemDiscount = BigDecimal.ZERO;
			}
			if (preTaxAmount == null) {
				preTaxAmount = BigDecimal.ZERO;
			}
			if (invoiceLineNetAmount == null) {
				invoiceLineNetAmount = BigDecimal.ZERO;
			}
			if (itemTotal == null) {
				itemTotal = BigDecimal.ZERO;
			}
			if (taxOn == null) {
				taxOn = BigDecimal.ZERO;
			}
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, tcsCgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.tcsCgstAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10128",
						"Invalid TCSCGSTAmount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, tcsSgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.tcsSgstAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10129",
						"Invalid TCSSGSTAmount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, tdsIgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.tdsIgstAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10130",
						"Invalid TDSIGSTAmount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, tdsCgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.tdsCgstAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10131",
						"Invalid TDSCGSTAmount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, tdsSgstAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.tdsSgstAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10132",
						"Invalid TDSSGSTAmount", location));
			}
			
			if (!BigDecimalNagativeValueUtil.Negvalidate(document,
					itemDiscount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.itemDiscount);
				TransDocProcessingResultLoc location 
				             = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10060",
						"Invalid Item Discount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document,
					preTaxAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.preTaxAmount);
				TransDocProcessingResultLoc location 
				             = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10061",
						"Invalid Pretax Amount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document,
					invoiceLineNetAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.invoiceLineNetAmount);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10062",
						"Invalid Invoice Invoice_line_net_amount", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, itemTotal)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ITEM_TOTAL);
				TransDocProcessingResultLoc location 
				               = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10064",
						"Invalid Item Total", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, taxOn)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.TAXON);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10066",
						"Invalid Tax On", location));
			}
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, amount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.AMOUNT);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10067",
						"Invalid Amount", location));
			}
			
			
			if (!BigDecimalNagativeValueUtil.Negvalidate(document, itemAmount)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ITEMAMUNT);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10058",
						"Invalid Item Amount", location));
			}

			if (!BigDecimalNagativeValueUtil.Negvalidate(document,
					totalitemAmunt)) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.TOTALITEMAMT);
				TransDocProcessingResultLoc location 
				             = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10063",
						"Invalid Total Item Amount", location));
			}

		});
		return errors;
	}

}
*/