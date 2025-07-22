package com.ey.advisory.app.services.strcutvalidation;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.BillToState;
import static com.ey.advisory.common.GSTConstants.CGSTN;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;
import static com.ey.advisory.common.GSTConstants.E_ComGstin;
import static com.ey.advisory.common.GSTConstants.ORG_CGSTN;
import static com.ey.advisory.common.GSTConstants.ORIGINAL_DOC_DATE;
import static com.ey.advisory.common.GSTConstants.ORIGINAL_DOC_NO;
import static com.ey.advisory.common.GSTConstants.PORT_CODE;
import static com.ey.advisory.common.GSTConstants.POS;
import static com.ey.advisory.common.GSTConstants.PRE_GST;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;
import static com.ey.advisory.common.GSTConstants.ReasonForCDebitNote;
import static com.ey.advisory.common.GSTConstants.SHIPPING_BILL_DATE;
import static com.ey.advisory.common.GSTConstants.SHIPPING_BILL_NO;
import static com.ey.advisory.common.GSTConstants.ShipToState;
import static com.ey.advisory.common.GSTConstants.UINorComposition;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Quartet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

@Component("OutwardDocOtherStructuralValidations")
public class OutwardDocOtherStructuralValidations {

	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of("CR", "DR", "RCR", "RDR");
	private static final List<Quartet<Integer, String, String, String>> LIST = new ImmutableList.Builder()
			.add(new Quartet<Integer, String, String, String>(8, "ER130",
					"Single document cannot have multiple return period.",
					RETURN_PREIOD))
			.add(new Quartet<Integer, String, String, String>(13, "E131",
					"Single document cannot have multiple docment dates.",
					DOC_DATE))
			.add(new Quartet<Integer, String, String, String>(14, "ER139",
					"Single document cannot have multiple Original document number.",
					ORIGINAL_DOC_NO))
			.add(new Quartet<Integer, String, String, String>(15, "ER140",
					"Single document cannot have multiple Original document Date.",
					ORIGINAL_DOC_DATE))
			.add(new Quartet<Integer, String, String, String>(16, "ER132",
					"Single document cannot have multiple flags.", PRE_GST))
			.add(new Quartet<Integer, String, String, String>(18, "ER133",
					"Single document cannot have Multiple Customer GSTIN.",
					CGSTN))
			.add(new Quartet<Integer, String, String, String>(19, "ER062",
					"Single document cannot have multiple flags.",
					UINorComposition))
			.add(new Quartet<Integer, String, String, String>(20, "ER134",
					"Single document cannot have multiple original customer GSTIN.",
					ORG_CGSTN))
			.add(new Quartet<Integer, String, String, String>(23, "ERXXX",
					"Single document cannot have multiple Bill to State.",
					BillToState))
			.add(new Quartet<Integer, String, String, String>(24, "ERXXX",
					"Single document cannot have multiple Ship To State",
					ShipToState))
			.add(new Quartet<Integer, String, String, String>(25, "ERXXX",
					"Single document cannot have multiple POS.", POS))
			.add(new Quartet<Integer, String, String, String>(26, "ER135",
					"Single document cannot have multiple port code.",
					PORT_CODE))
			.add(new Quartet<Integer, String, String, String>(27, "ER136",
					"Single document cannot have multiple Shipping Bill Number.",
					SHIPPING_BILL_NO))
			.add(new Quartet<Integer, String, String, String>(28, "ER137",
					"Single document cannot have multiple Shipping Bill date.",
					SHIPPING_BILL_DATE))
			.add(new Quartet<Integer, String, String, String>(51, "ER138",
					"Single document cannot have multiple ecomGSTIN",
					E_ComGstin))
			.add(new Quartet<Integer, String, String, String>(53, "ER141",
					"Single document cannot have multiple reasons for issuing note",
					ReasonForCDebitNote))
			.build();

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
		List<ProcessingResult> errors = new ArrayList<>();

		List<String> errorLocations = new ArrayList<>();
		String prev = null;
		boolean isFirst = true;

		int colNo = triplet.getValue0();
		if (colNo != 53) {
			for (Object[] objarr : listobj) {
				String curVal = null;
				
				if (objarr[colNo] != null) {
					curVal = objarr[colNo].toString();
				}

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
									triplet.getValue1(), triplet.getValue2(),
									location);
						}
					}
					if (curVal != null) {
						if (!curVal.equals(prev)) {
							errorLocations.add(triplet.getValue3());
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							return new ProcessingResult(APP_VALIDATION,
									triplet.getValue1(), triplet.getValue2(),
									location);
						}
					}
				}

			}
			;
		}
		if (colNo == 53) {
			for (Object[] objarr : listobj) {
				Object curVal = objarr[colNo];

				if (isFirst) {

					if (curVal != null) {
						prev = curVal.toString();
					}
					isFirst = false;
				} else {
					if (curVal == null) {
						if (prev != null) {
							errorLocations.add(triplet.getValue3());
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							return new ProcessingResult(APP_VALIDATION,
									triplet.getValue1(), triplet.getValue2(),
									location);
						}
					}
					if (curVal != null) {
						if (ORGDOCNUM_REQUIRING_IMPORTS
								.contains(curVal.toString())) {
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
			;
		}
		return null;
	}
}
