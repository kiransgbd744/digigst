package com.ey.advisory.app.services.validation.sales;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import static com.ey.advisory.common.GSTConstants.*;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * This class is responsible for validation of taxable value or tax amount for
 * document types other than document types - CR or RCR or RFV/CAN
 * 
 * BUSINESS RULE_ID--BR_OUTWARD_31
 * 
 * @author Mahesh.Golla
 *
 */
@Component("taxValueInvoiceValidator")
public class TaxValueInvoiceValidator
		implements DocRulesValidator<OutwardTransDocument> {

	/**
	 * This method validates if taxable value or tax amount is negative for
	 * documents other than the document types - CR or RCR or RFV/CAN If the
	 * validation fails error is returned with description
	 * 
	 * @param OutwardTransDocument
	 * @return OutwardTransDocument
	 * 
	 */
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.CR, GSTConstants.RFV, GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {

			OutwardTransDocLineItem item = items.get(idx);

			if (document.getDocType() != null
					&& !document.getDocType().isEmpty()) {
				if (document.getSupplyType() != null
						&& !document.getSupplyType().isEmpty()) {
					if ((!ORGDOCNUM_REQUIRING_IMPORTS.contains(
							trimAndConvToUpperCase(document.getDocType())))
							|| !GSTConstants.CAN.equalsIgnoreCase(
									document.getSupplyType())) {
						if (item.getTaxableValue()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(TAX_AMOUNT);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER225",
									"Taxable Value or Tax Amount cannot be "
											+ "negative for the document "
											+ "types other "
											+ "than CR or RCR or RFV/CAN",
									location));
						}
						/*if (item.getIgstAmount()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(IGST_AMOUNT);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER077",
									"Invalid IGST Amount",
									location));
						}
						if (item.getCgstAmount()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(CGST_AMOUNT);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER079",
									"Invalid CGST Amount",
									location));
						}
						if (item.getSgstAmount()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(SGST_AMOUNT);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER081",
									"Invalid SGST/UT GST Amount",
									location));
						}
						if (item.getCessAmountAdvalorem()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(CESS_AMT_ADV);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER083",
									"Invalid Cess Amount",
									location));
						}
						if (item.getCessAmountSpecific()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(CESS_AMT_SPECIFIC);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER083",
									"Invalid Cess Amount",
									location));
						}
						if (item.getLineItemAmt()
								.compareTo(BigDecimal.ZERO) < 0) {
							errorLocations.add(INVOICE_VALUE);
							TransDocProcessingResultLoc location 
							              = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER084",
									"Invalid Invoice Value",
									location));
						}*/
					}
				}

			}

		});
		return errors;
	}
}
