package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * This class is responsible to validate if the Arithmetic check of Tax
 * Amount.Except in case of cess rate sepecifi and cess amount specific.
 * 
 * @author Mahesh.Golla BR_OUTWARD_16
 *
 */
@Component("Gstr1AArithmeticCheckTaxAmount")
public class Gstr1AArithmeticCheckTaxAmount
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	/**
	 * To check that Tax Amount = Taxable Value*Tax Rate. Exceptions: a) In case
	 * of Cess Rate specific & cess amount specific. b) Except when value in
	 * field 'UIN or Composition' is "L/UL/CL"; then Tax Amount=Taxable
	 * Value*Rate*0.65.
	 */

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errorsInfo = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			BigDecimal taxableValue = item.getTaxableValue();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal SgstRate = item.getSgstRate();
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal CsgstRate = item.getCgstRate();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal IgstRate = item.getIgstRate();
			if (taxableValue == null) {
				taxableValue = BigDecimal.ZERO;
			}
			if (sgstAmount == null) {
				sgstAmount = BigDecimal.ZERO;
			}
			if (SgstRate == null) {
				SgstRate = BigDecimal.ZERO;
			}
			if (cgstAmount == null) {
				cgstAmount = BigDecimal.ZERO;
			}
			if (CsgstRate == null) {
				CsgstRate = BigDecimal.ZERO;
			}
			if (igstAmount == null) {
				igstAmount = BigDecimal.ZERO;
			}
			if (IgstRate == null) {
				IgstRate = BigDecimal.ZERO;
			}
			BigDecimal taxSgstAmount = taxableValue.multiply(SgstRate)
					.divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);

			BigDecimal taxcgstAmount = taxableValue.multiply(CsgstRate)
					.divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);

			BigDecimal taxigstAmount = taxableValue.multiply(IgstRate)
					.divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);

			BigDecimal str = new BigDecimal("0.65");

			BigDecimal taxSgstAmount1 = taxableValue.multiply(SgstRate)
					.multiply(str).divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);
			BigDecimal taxcgstAmount1 = taxableValue.multiply(CsgstRate)
					.multiply(str).divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);
			BigDecimal taxigstAmount1 = taxableValue.multiply(IgstRate)
					.multiply(str).divide(BigDecimal.valueOf(100))
					.setScale(2, RoundingMode.HALF_UP);

			if (GSTConstants.L65.equalsIgnoreCase(document.getDiffPercent())) {
				if ((sgstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxSgstAmount1) != 0)) {
					String[] errorLocations = new String[] { SGST_AMOUNT };
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0502",
							"SGST Amount is incorrectly computed ", location));
				}
				if ((cgstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxcgstAmount1) != 0)) {
					String[] errorLocations = new String[] { CGST_AMOUNT };
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0512",
							"CGST Amount is incorrectly computed ", location));
				}
				if ((igstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxigstAmount1) != 0)) {
					String[] errorLocations = new String[] { IGST_AMOUNT };
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0513",
							"IGST Amount is incorrectly computed ", location));
				}
			} else if (!GSTConstants.L65
					.equalsIgnoreCase(document.getDiffPercent())) {
				if ((sgstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxSgstAmount) != 0)) {
					String[] errorLocations = new String[] { SGST_AMOUNT };

					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0502",
							"SGST Amount is incorrectly computed ", location));
				}
				if ((cgstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxcgstAmount) != 0)) {
					String[] errorLocations = new String[] { CGST_AMOUNT };
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0512",
							"CGST Amount is incorrectly computed ", location));
				}
				if ((igstAmount.setScale(2, RoundingMode.HALF_UP)
						.compareTo(taxigstAmount) != 0)) {
					String[] errorLocations = new String[] { IGST_AMOUNT };
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations);
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0513",
							"IGST Amount is incorrectly computed ", location));
				}

			}
		});

		return errorsInfo;
	}

}
