package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Quartet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Itc04HeaderStructuralValidations")
public class Itc04HeaderStructuralValidations {

	private static final List<Quartet<Integer, String, String, String>> LIST = new ImmutableList.Builder<Quartet<Integer, String, String, String>>()

			.add(new Quartet<Integer, String, String, String>(12, "ER5868",
					"Job Worker GSTIN should be same across all line items of a document.",
					GSTConstants.JOB_WORKER_GSTIN))

			.add(new Quartet<Integer, String, String, String>(13, "ER5869",
					"Job Worker State Code should be same across all line items of a document.",
					GSTConstants.JOB_WORKER_STATE_CODE))

			.add(new Quartet<Integer, String, String, String>(14, "ER5870",
					"Job Worker State Type should be same across all line items of a document.",
					GSTConstants.JOB_WORKER_TYPE))

			.add(new Quartet<Integer, String, String, String>(15, "ER5871",
					"Job Worker ID should be same across all line items of a document.",
					GSTConstants.JOB_WORKER_ID))

			.add(new Quartet<Integer, String, String, String>(16, "ER5872",
					"Job Worker name should be same across all line items of a document.",
					GSTConstants.JOB_WORKER_NAME))//

			.add(new Quartet<Integer, String, String, String>(43, "ER5873",
					"Posting Date should be same across all line items of a document.",
					GSTConstants.POSTING_DATE))
			.add(new Quartet<Integer, String, String, String>(44, "ER5877",
					"User ID should be same across all line items of a document.",
					GSTConstants.UserID))

			.add(new Quartet<Integer, String, String, String>(45, "ER5874",
					"Company Code should be same across all line items of a document.",
					GSTConstants.CompanyCode))
			.add(new Quartet<Integer, String, String, String>(52, "ER5875",
					"Accounting Voucher Number should be same across all line items of a document.",
					GSTConstants.ACCOUNTING_VOCHAR_NUM))

			.add(new Quartet<Integer, String, String, String>(53, "ER5876",
					"Accounting Voucher date should be same across all line items of a document.",
					GSTConstants.ACCVOCHDATE))
			.build();

	public List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();

		for (Object[] row : rows) {
			if (isPresent(row[1]) && GSTConstants.CAN.equalsIgnoreCase(
					trimAndConvToUpperCase(row[1].toString().trim()))) {
				return results;
			}
		}
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

		for (Object[] objarr : listobj) {
			String curVal = null;

			if (isPresent(objarr[colNo])) {
				curVal = objarr[colNo].toString().trim();
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
					if (!curVal.equalsIgnoreCase(prev)) {
						if (colNo == 43 || colNo == 53) {
							LocalDate curdate = DateFormatForStructuralValidatons
									.parseObjToDate(curVal);
							LocalDate prevdate = DateFormatForStructuralValidatons
									.parseObjToDate(prev);
							if (curdate == null && prevdate == null) {
								errorLocations.add(triplet.getValue3());
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);
							}

							if ((curdate == null && prevdate != null)
									|| (prevdate == null && curdate != null)) {
								errorLocations.add(triplet.getValue3());
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);

							}
							if (curdate != null && prevdate != null) {
								if (curdate.compareTo(prevdate) != 0) {
									errorLocations.add(triplet.getValue3());
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								}
							}

						} else {

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

		}
		return null;
	}
}
