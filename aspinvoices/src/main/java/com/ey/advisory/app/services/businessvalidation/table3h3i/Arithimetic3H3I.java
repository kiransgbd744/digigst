/**
 * 
 */
package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
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
public class Arithimetic3H3I
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		BigDecimal taxableValue = BigDecimal.ZERO;
		String taxbleVal = document.getTaxableValue();
		if (taxbleVal != null && !taxbleVal.isEmpty()) {
			taxableValue = NumberFomatUtil.getBigDecimal(taxbleVal);
		}
		BigDecimal rate = BigDecimal.ZERO;
		String rates = document.getRate();
		if (rates != null && !rates.isEmpty()) {
			rate = NumberFomatUtil.getBigDecimal(rates);
		}

		BigDecimal interTaxAmount = taxableValue.multiply(rate)
				.divide(BigDecimal.valueOf(100));
		BigDecimal intraTaxAmount = taxableValue.multiply(rate)
				.divide(BigDecimal.valueOf(200));

		BigDecimal cgstAmount = BigDecimal.ZERO;
		String cgsts = document.getCentralTaxAmount();
		if (cgsts != null && !cgsts.isEmpty()) {
			cgstAmount = NumberFomatUtil.getBigDecimal(cgsts);
		}

		BigDecimal sgstAmount = BigDecimal.ZERO;
		String sgstss = document.getStateUTTaxAmount();
		if (sgstss != null && !sgstss.isEmpty()) {
			sgstAmount = NumberFomatUtil.getBigDecimal(sgstss);
		}
		BigDecimal igstAmount = BigDecimal.ZERO;
		String igst = document.getIntegratedTaxAmount();
		if (igst != null && !igst.isEmpty()) {
			igstAmount = NumberFomatUtil.getBigDecimal(igst);
		}

		BigDecimal str = new BigDecimal("0.65");
		BigDecimal interL65taxAmount = taxableValue.multiply(rate).multiply(str)
				.divide(BigDecimal.valueOf(100));
		BigDecimal intraL65taxAmount = taxableValue.multiply(rate).multiply(str)
				.divide(BigDecimal.valueOf(200));

		if (document.getDiffPercent() != null
				&& !document.getDiffPercent().isEmpty()
				&& document.getDiffPercent().equalsIgnoreCase("L65")) {

			if (document.getSupplierGSTINorpan() != null
					&& document.getSupplierGSTINorpan().length() == 10) {
				if (sgstAmount.compareTo(intraL65taxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0426",
							"SGST Amount is incorrectly computed.", location));

				}
				if (cgstAmount.compareTo(intraL65taxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0427",
							"CGST Amount is incorrectly computed.", location));

				}
			}
			if (document.getSupplierGSTINorpan() != null
					&& document.getSupplierGSTINorpan().length() == 15) {
				String statecode = document.getSupplierGSTINorpan().substring(0,
						2);
				if (document.getPos() != null
						&& document.getPos().equalsIgnoreCase(statecode)) {
					if (sgstAmount.compareTo(intraL65taxAmount) != 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0426",
								"SGST Amount is incorrectly computed.",
								location));

					}
					if (cgstAmount.compareTo(intraL65taxAmount) != 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.CGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0427",
								"CGST Amount is incorrectly computed.",
								location));

					}
				} else {
					if (document.getPos() != null
							&& !document.getPos().equalsIgnoreCase(statecode)) {
						if (igstAmount.compareTo(interL65taxAmount) != 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_AMOUNT);
							TransDocProcessingResultLoc location = 
									new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0428",
									"IGST Amount is incorrectly computed.",
									location));
						}

					}
				}
			}

		} else {
			if (document.getSupplierGSTINorpan() != null
					&& document.getSupplierGSTINorpan().length() == 10) {
				if (sgstAmount.compareTo(intraTaxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0426",
							"SGST Amount is incorrectly computed.", location));

				}
				if (cgstAmount.compareTo(intraTaxAmount) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN0427",
							"CGST Amount is incorrectly computed.", location));

				}
			}
			if (document.getSupplierGSTINorpan() != null
					&& document.getSupplierGSTINorpan().length() == 15) {
				String statecode = document.getSupplierGSTINorpan().substring(0,
						2);
				if (document.getPos() != null
						&& document.getPos().equalsIgnoreCase(statecode)) {
					if (sgstAmount.compareTo(intraTaxAmount) != 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0426",
								"SGST Amount is incorrectly computed.",
								location));

					}
					if (cgstAmount.compareTo(intraTaxAmount) != 0) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.CGST_AMOUNT);
						TransDocProcessingResultLoc location =
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0427",
								"CGST Amount is incorrectly computed.",
								location));

					}
				} else {
					if (document.getPos() != null
							&& !document.getPos().equalsIgnoreCase(statecode)) {
						if (igstAmount.compareTo(interTaxAmount) != 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_AMOUNT);
							TransDocProcessingResultLoc location = 
									new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0428",
									"IGST Amount is incorrectly computed.",
									location));
						}

					}
				}
			}
		}

		return errors;
	}

}
