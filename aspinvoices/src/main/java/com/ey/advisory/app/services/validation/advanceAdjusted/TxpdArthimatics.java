/**
 * 
 */
package com.ey.advisory.app.services.validation.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Mahesh.Golla
 *
 */
public class TxpdArthimatics
		implements B2csBusinessRuleValidator<Gstr1AsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		BigDecimal taxableValue = BigDecimal.ZERO;
		String taxableValues = (document.getNewGrossAdvanceAdjusted() != null)
				? document.getNewGrossAdvanceAdjusted().trim() : null;
		if (taxableValues != null && !taxableValues.isEmpty()) {
			taxableValue = NumberFomatUtil.getBigDecimal(taxableValues);
			taxableValue.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		}
		BigDecimal rate = BigDecimal.ZERO;
		String rates = (document.getNewRate() != null)
				? document.getNewRate().trim() : null;
		if (rates != null && !rates.isEmpty()) {
			rate = NumberFomatUtil.getBigDecimal(rates);
			rate.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		}
		String sgstin = (document.getSgstin() != null)
				? document.getSgstin().trim().substring(0, 2) : null;
		String pos = (document.getNewPOS() != null)
				? document.getNewPOS().trim() : null;

		BigDecimal interTaxAmount = taxableValue.multiply(rate)
				.divide(BigDecimal.valueOf(100));
		BigDecimal intraTaxAmount = taxableValue.multiply(rate)
				.divide(BigDecimal.valueOf(200));

		BigDecimal cgstAmount = BigDecimal.ZERO;
		String cgst = (document.getCentralTaxAmount() != null)
				? document.getCentralTaxAmount().trim() : null;
		if (cgst != null && !cgst.isEmpty()) {
			cgstAmount = NumberFomatUtil.getBigDecimal(cgst);
			cgstAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		}
		BigDecimal sgstAmount = BigDecimal.ZERO;
		String sgsts = (document.getStateUTTaxAmount() != null)
				? document.getStateUTTaxAmount().trim() : null;
		if (sgsts != null && !sgsts.isEmpty()) {
			sgstAmount = NumberFomatUtil.getBigDecimal(sgsts);
			sgstAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		}
		BigDecimal igstAmount = BigDecimal.ZERO;
		String igst = (document.getIntegratedTaxAmount() != null)
				? document.getIntegratedTaxAmount().trim() : null;
		if (igst != null && !igst.isEmpty()) {
			igstAmount = NumberFomatUtil.getBigDecimal(igst);
			igstAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		}

		BigDecimal str = new BigDecimal("0.65");
		BigDecimal interL65taxAmount = taxableValue.multiply(rate).multiply(str)
				.divide(BigDecimal.valueOf(100));
		BigDecimal intraL65taxAmount = taxableValue.multiply(rate).multiply(str)
				.divide(BigDecimal.valueOf(200));
		if (document.getTransactionType() != null
				&& !document.getTransactionType().isEmpty()
				&& (document.getTransactionType().trim().equalsIgnoreCase("L65")
				|| document.getTransactionType().trim().
				   equalsIgnoreCase("ZL65"))) {
			if ((sgstin != null && pos != null)
					&& sgstin.equalsIgnoreCase(pos)) {
				if (sgstAmount.compareTo(intraL65taxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location =
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5510",
							"SGST Amount is incorrectly computed.", location));
					// return errors;
				}
				if (cgstAmount.compareTo(intraL65taxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5511",
							"CGST Amount is incorrectly computed.", location));
					// return errors;
				}
			}
			else{
				if (igstAmount.compareTo(interL65taxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.IGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5512",
							"IGST Amount is incorrectly computed.", location));
					// return errors;
				}

			}
		} else {
			if ((sgstin != null && pos != null)
					&& sgstin.equalsIgnoreCase(pos)) {
				if (sgstAmount.compareTo(intraTaxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5510",
							"SGST Amount is incorrectly computed.", location));
					// return errors;
				}
				if (cgstAmount.compareTo(intraTaxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5511",
							"CGST Amount is incorrectly computed.", location));
					// return errors;
				}
			} else {
				if (igstAmount.compareTo(interTaxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.IGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN5512",
							"IGST Amount is incorrectly computed.", location));
					// return errors;
				}
			}
		}
		return errors;
	}

}
