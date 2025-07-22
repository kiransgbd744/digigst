package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.AUTOPOPULATED;
import static com.ey.advisory.common.GSTConstants.CLAIMREFUNDFLAG;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;
import static com.ey.advisory.common.GSTConstants.DXP;
import static com.ey.advisory.common.GSTConstants.EXPT;
import static com.ey.advisory.common.GSTConstants.EXPWT;
import static com.ey.advisory.common.GSTConstants.E_ComGstin;
import static com.ey.advisory.common.GSTConstants.PORT_CODE;
import static com.ey.advisory.common.GSTConstants.POS;
import static com.ey.advisory.common.GSTConstants.PRE_GST;
import static com.ey.advisory.common.GSTConstants.RCR;
import static com.ey.advisory.common.GSTConstants.RDR;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;
import static com.ey.advisory.common.GSTConstants.RNV;
import static com.ey.advisory.common.GSTConstants.ReverseCharge;
import static com.ey.advisory.common.GSTConstants.SECTION7OFIGSTFLAG;
import static com.ey.advisory.common.GSTConstants.SEZT;
import static com.ey.advisory.common.GSTConstants.SHIPPING_BILL_DATE;
import static com.ey.advisory.common.GSTConstants.SHIPPING_BILL_NO;
import static com.ey.advisory.common.GSTConstants.TCSFlag;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Quartet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("VendorStructuralValidations")
public class VendorStructuralValidations {

	private static final List<Quartet<Integer, String, String, String>> LIST 
	= new ImmutableList.Builder<Quartet<Integer, String, String, String>>()
						.add(new Quartet<Integer, String, String, String>(21, "ER0023",
					"Return Period should be same across all "
							+ "line items of a document",
					RETURN_PREIOD))
			.add(new Quartet<Integer, String, String, String>(26, "ER0037",
					"Document Date should be same across "
							+ "all line items of a document. ",
					DOC_DATE))

			.add(new Quartet<Integer, String, String, String>(28, "ER0040",
					"Original Document Number should be same across "
							+ "all line items of a document.",
					GSTConstants.ORIGINAL_DOC_NO))

			.add(new Quartet<Integer, String, String, String>(29, "ER0043",
					"Original Document Date should be same across all"
							+ " line items of a document",
					GSTConstants.ORIGINAL_DOC_DATE))

			.add(new Quartet<Integer, String, String, String>(30, "ER0045",
					"CRDRPreGST should be same across all line "
							+ "items of a document.",
					PRE_GST))
			.add(new Quartet<Integer, String, String, String>(32, " ER0050",
					"Recipient GSTIN should be same across all line "
							+ "items of a document.",
					GSTConstants.CGSTIN))
			.add(new Quartet<Integer, String, String, String>(33, " ER0124",
					" Recipient Type should be same across all "
							+ "line items of a document",
					GSTConstants.RECIPIENTTYPE))
			.add(new Quartet<Integer, String, String, String>(34, " ER0125",
					"Differential Percentage Flag should be same "
							+ "across all line items of a document",
					GSTConstants.DIFFERENTIAL_PERCENTAGE_FLAG))

			.add(new Quartet<Integer, String, String, String>(44, "ER0057",
					"POS should be same across all line items of a document.",
					POS))
			.add(new Quartet<Integer, String, String, String>(46, "ER0060",
					"Port Code should be same across all line "
							+ "items of a document.",
					PORT_CODE))
			.add(new Quartet<Integer, String, String, String>(47, " ER0062",
					" Shipping Bill Number should be same across all"
							+ " line items of a document.",
					SHIPPING_BILL_NO))
			.add(new Quartet<Integer, String, String, String>(48, " ER0065",
					" Shipping Bill Date should be same across all "
							+ "line items of a document.",
					SHIPPING_BILL_DATE))

			.add(new Quartet<Integer, String, String, String>(57, "ER0073",
					" Section 7 Flag should be same across "
							+ "all line items of a document.",
					SECTION7OFIGSTFLAG))

			.add(new Quartet<Integer, String, String, String>(82, "ER0095",
					"Reverse Charge Flag should be same across all "
							+ "line items of a document.",
					ReverseCharge))
			.add(new Quartet<Integer, String, String, String>(83, "ER0097",
					"TCS Flag should be same across all line items of a document.",
					TCSFlag))
			.add(new Quartet<Integer, String, String, String>(84, "ER0100",
					" E-Com GSTIN should be same across all line "
							+ "items of a document.",
					E_ComGstin))
			.add(new Quartet<Integer, String, String, String>(87, "ER0104",
					" Claim Refund Flag should be same across all "
							+ "line items of a document.",
					CLAIMREFUNDFLAG))
			.add(new Quartet<Integer, String, String, String>(88, "ER0106",
					"Auto Populate to Refund Flag should be same across all "
							+ "line items of a document.",
					AUTOPOPULATED))

			.build();// 31

	private static final List<String> DOC_TYPE = ImmutableList.of(RNV, RCR,
			RDR);
	private static final List<String> SUPPLY_TYPE = ImmutableList.of(EXPT,
			EXPWT);
	private static final List<String> SUPPLY_TYPE87 = ImmutableList.of(SEZT,
			DXP);

	public List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();

		for (Quartet<Integer, String, String, String> triplet : LIST) {
			ProcessingResult result = validateColForEqValues(rows, triplet);
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}

	private ProcessingResult validateColForEqValues(List<Object[]> listobj,
			Quartet<Integer, String, String, String> triplet) {

		List<String> errorLocations = new ArrayList<>();
		String prev = null;
		boolean isFirst = true;

		int colNo = triplet.getValue0();
		// if (colNo != 53) {
		for (Object[] objarr : listobj) {
			String curVal = null;

			if (objarr[colNo] != null) {
				curVal = objarr[colNo].toString();
			}
			Object doctype = objarr[23];
			Object supplyType = objarr[24];
			if (doctype != null && supplyType != null) {
				if (/*
					 * (colNo == 2) || (colNo == 3) || (colNo == 4) || (colNo ==
					 * 5) || (colNo == 6) || (colNo == 7) || (colNo == 8) ||
					 * (colNo == 9) || (colNo == 10) || (colNo == 11) || (colNo
					 * == 12) || (colNo == 13) ||
					 */ (colNo == 21) || (colNo == 26)
						|| (colNo == 28 && DOC_TYPE.contains(
								trimAndConvToUpperCase(doctype.toString())))
						|| (colNo == 29 && DOC_TYPE.contains(
								trimAndConvToUpperCase(doctype.toString())))
						|| (colNo == 30) || (colNo == 32) || (colNo == 33)
						|| (colNo == 34) || (colNo == 44)
						|| (colNo == 46 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString())))
						|| (colNo == 47 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString())))
						|| (colNo == 48 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString())))
						|| (colNo == 57) || (colNo == 82) || (colNo == 83)
						|| (colNo == 84)
						|| (colNo == 87 && SUPPLY_TYPE87.contains(
								trimAndConvToUpperCase(supplyType.toString())))
						|| (colNo == 88)) {
					if ((colNo == 30) || (colNo == 34) || (colNo == 57)
							|| (colNo == 82) || (colNo == 83) || (colNo == 87)
							|| (colNo == 88)) {
						if (curVal == null) {
							curVal = "N";
						}
					}
					if (isFirst) {
						prev = curVal;
						isFirst = false;
					} else {
						if (curVal == null) {
							if (prev != null) {
								if (colNo == 28 || colNo == 29) {
									errorLocations.add(triplet.getValue3());
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											listobj.indexOf(objarr),
											errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								} else {
									errorLocations.add(triplet.getValue3());
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								}
							}
						}
						if (curVal != null) {
							if (!curVal.equalsIgnoreCase(prev)) {
								if (colNo == 28 || colNo == 29) {
									errorLocations.add(triplet.getValue3());
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											listobj.indexOf(objarr),
											errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								} else {
									errorLocations.add(triplet.getValue3());
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								}
							}
						}
					}
				}
			}

		}

		return null;
	}
}
