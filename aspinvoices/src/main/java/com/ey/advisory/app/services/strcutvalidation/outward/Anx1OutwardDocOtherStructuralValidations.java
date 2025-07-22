package com.ey.advisory.app.services.strcutvalidation.outward;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Quartet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Anx1OutwardDocOtherStructuralValidations")
public class Anx1OutwardDocOtherStructuralValidations {

	private static final List<Quartet<Integer, String, String, String>> LIST 
	= new ImmutableList.Builder<Quartet<Integer, String, String, String>>()

			.add(new Quartet<Integer, String, String, String>(2, "ER0112",
					" Profit Centre should be same across "
							+ "all line items of a document",
					GSTConstants.PROFITCENTRE))
			.add(new Quartet<Integer, String, String, String>(3, "ER0113",
					"Plant should be same across all line items of a document",
					GSTConstants.PLANT))
			.add(new Quartet<Integer, String, String, String>(4, "ER0114",
					"Division should be same across all "
							+ "line items of a document",
					GSTConstants.DIVISION))
			.add(new Quartet<Integer, String, String, String>(5, "ER0115",
					"Location should be same across all line "
							+ "items of a document",
					GSTConstants.LOCATION))
			.add(new Quartet<Integer, String, String, String>(6, "ER0116",
					"Sales organization should be same "
							+ "across all line items of a document",
					GSTConstants.SALESORG))
			.add(new Quartet<Integer, String, String, String>(7, "ER0117",
					"Distribution Channel should be same across "
							+ "all line items of a document",
					GSTConstants.DISTRIBUTIONCHAN))
			.add(new Quartet<Integer, String, String, String>(8, "ER0118",
					"User Access 1 should be same across all "
							+ "line items of a document",
					GSTConstants.USERACCESS1))
			.add(new Quartet<Integer, String, String, String>(9, "ER0119",
					"User Access 2 should be same across all "
							+ "line items of a document",
					USERACCESS2))
			.add(new Quartet<Integer, String, String, String>(10, "ER0120",
					"User Access 3 should be same across all"
							+ " line items of a document",
					USERACCESS3))
			.add(new Quartet<Integer, String, String, String>(11, "ER0121",
					"User Access 4 should be same across all"
							+ " line items of a document",
					USERACCESS4))

			.add(new Quartet<Integer, String, String, String>(12, "ER0122",
					"User Access 5 should be same across "
							+ "all line items of a document",
					USERACCESS5))
			.add(new Quartet<Integer, String, String, String>(13, "ER0123",
					"User Access 6 should be same across all "
							+ "line items of a document",
					USERACCESS6))

			.add(new Quartet<Integer, String, String, String>(21, "ER0023",
					"Return Period should be same across all "
							+ "line items of a document",
					RETURN_PREIOD))
			.add(new Quartet<Integer, String, String, String>(26, "ER0037",
					"Document Date should be same across "
							+ "all line items of a document. ",
					DOC_DATE))

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

			.build();

	private static final List<String> SUPPLY_TYPE = ImmutableList.of(EXPT,
			EXPWT);
	private static final List<String> SUPPLY_TYPE87 = ImmutableList.of(SEZWP,
			DXP);

	public List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();
		for (Object[] row : rows) {
			if (isPresent(row[24]) && GSTConstants.CAN.equalsIgnoreCase(
					trimAndConvToUpperCase(row[24].toString().trim()))) {
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
			Object doctype = objarr[23];
			Object supplyType = objarr[24];
			if (isPresent(doctype) && isPresent(supplyType)) {
				if ((colNo == 4) || (colNo == 6) || (colNo == 7) || (colNo == 8)
						|| (colNo == 9) || (colNo == 10) || (colNo == 11)
						|| (colNo == 12) || (colNo == 13) || (colNo == 21)
						|| (colNo == 26)

						|| (colNo == 30) || (colNo == 32) || (colNo == 33)
						|| (colNo == 34) || (colNo == 44)
						|| (colNo == 46
								&& SUPPLY_TYPE.contains(trimAndConvToUpperCase(
										supplyType.toString().trim())))
						|| (colNo == 47) || (colNo == 48)

						|| (colNo == 57) || (colNo == 82) || (colNo == 83)
						|| (colNo == 84)
						|| (colNo == 87 && SUPPLY_TYPE87
								.contains(trimAndConvToUpperCase(
										supplyType.toString().trim())))
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

								errorLocations.add(triplet.getValue3());
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);

							}
						}
						if (curVal != null) {
							if (!curVal.equalsIgnoreCase(prev)) {
								if (colNo == 26 || colNo == 48) {
									LocalDate curdate = DateFormatForStructuralValidatons
											.parseObjToDate(curVal);
									LocalDate prevdate = DateFormatForStructuralValidatons
											.parseObjToDate(prev);
									if (curdate == null && prevdate == null) {
										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										return new ProcessingResult(
												APP_VALIDATION,
												triplet.getValue1(),
												triplet.getValue2(), location);
									}

									if ((curdate == null && prevdate != null)
											|| (prevdate == null
													&& curdate != null)) {

										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										return new ProcessingResult(
												APP_VALIDATION,
												triplet.getValue1(),
												triplet.getValue2(), location);

									}
									if (curdate != null && prevdate != null) {
										if (curdate.compareTo(prevdate) != 0) {
											TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
													null,
													errorLocations.toArray());
											return new ProcessingResult(
													APP_VALIDATION,
													triplet.getValue1(),
													triplet.getValue2(),
													location);
										}
									}

								} else {

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

		}

		return null;
	}
}
