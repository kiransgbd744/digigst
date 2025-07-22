package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.AUTOPOPULATED;
import static com.ey.advisory.common.GSTConstants.CLAIMREFUNDFLAG;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;
import static com.ey.advisory.common.GSTConstants.DTA;
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

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Quartet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

@Component("Table3H3IStructuralValidations")
public class Table3H3IStructuralValidations {

	private static final List<Quartet<Integer, String, String, String>> LIST = new ImmutableList.Builder()
			.add(new Quartet<Integer, String, String, String>(21, "ER0023",
					" Field value should be same across all line "
							+ "items of a document.",
					RETURN_PREIOD))
			.add(new Quartet<Integer, String, String, String>(26, "ER0037",
					" Field value should be same across all line "
							+ "items of a document.",
					DOC_DATE))

			.add(new Quartet<Integer, String, String, String>(28, "ER0040",
					" Field value should be same across all line "
							+ "items of a document.",
					GSTConstants.ORIGINAL_DOC_NO))

			.add(new Quartet<Integer, String, String, String>(29, "ER0043",
					" Field should be same across line items of a document.",
					GSTConstants.ORIGINAL_DOC_DATE))

			.add(new Quartet<Integer, String, String, String>(30, "ER0045",
					" Field should be same across line items of a document.",
					PRE_GST))
			.add(new Quartet<Integer, String, String, String>(32, " ER0050",
					" Field should be same across line items of a document.",
					GSTConstants.CGSTIN))
			.add(new Quartet<Integer, String, String, String>(44, "ER0057",
					" Field value should be same across all line "
							+ "items of a document.",
					POS))
			.add(new Quartet<Integer, String, String, String>(46, "ER0060",
					" Field should be same across line items of a document.",
					PORT_CODE))
			.add(new Quartet<Integer, String, String, String>(47, " ER0062",
					" Field should be same across line items of a document.",
					SHIPPING_BILL_NO))
			.add(new Quartet<Integer, String, String, String>(48, " ER0065",
					" Field should be same across line items of a document.",
					SHIPPING_BILL_DATE))

			.add(new Quartet<Integer, String, String, String>(57, "ER0073",
					" Field should be same across line items of a document.",
					SECTION7OFIGSTFLAG))

			.add(new Quartet<Integer, String, String, String>(82, "ER0095",
					" Field should be same across line items of a document.",
					ReverseCharge))
			.add(new Quartet<Integer, String, String, String>(83, "ER0097",
					" Field should be same across line items of a document.",
					TCSFlag))
			.add(new Quartet<Integer, String, String, String>(84, "ER0100",
					" Field should be same across line items of a document.",
					E_ComGstin))
			.add(new Quartet<Integer, String, String, String>(87, "ER0104",
					" Field should be same across line items of a document.",
					CLAIMREFUNDFLAG))
			.add(new Quartet<Integer, String, String, String>(88, "ER0106",
					" Field should be same across line items of a document.",
					AUTOPOPULATED))

			.build();

	private static final List<String> DOC_TYPE = ImmutableList.of(RNV, RCR,
			RDR);
	private static final List<String> SUPPLY_TYPE = ImmutableList.of(EXPT,
			EXPWT);
	private static final List<String> SUPPLY_TYPE87 = ImmutableList.of(SEZT,
			DTA);

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
				if (colNo == 21 || (colNo == 26)
						|| (colNo == 28 && DOC_TYPE.contains(doctype))
						|| (colNo == 29 && DOC_TYPE.contains(doctype))
						|| (colNo == 30) || (colNo == 32) || (colNo == 44)
						|| (colNo == 46 && SUPPLY_TYPE.contains(supplyType))
						|| (colNo == 47 && SUPPLY_TYPE.contains(supplyType))
						|| (colNo == 48 && SUPPLY_TYPE.contains(supplyType))
						|| (colNo == 57) || (colNo == 82) || (colNo == 83)
						|| (colNo == 84)
						|| (colNo == 87 && SUPPLY_TYPE87.contains(supplyType))
						|| (colNo == 88)) {
					if (isFirst) {
						prev = curVal;
						isFirst = false;
					} else {
						if (curVal == null) {
							if (prev != null) {
								errorLocations.add(triplet.getValue3());
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);
							}
						}
						if (curVal != null) {
							if (!curVal.equals(prev)) {
								errorLocations.add(triplet.getValue3());
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
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
		return null;
	}
}
