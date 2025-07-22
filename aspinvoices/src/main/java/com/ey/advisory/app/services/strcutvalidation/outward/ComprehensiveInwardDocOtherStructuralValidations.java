package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Quartet;

import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class ComprehensiveInwardDocOtherStructuralValidations {
	private ComprehensiveInwardDocOtherStructuralValidations() {
	}
	private static final List<Quartet<Integer, String, String, String>> LIST 
	    = new ImmutableList.Builder<Quartet<Integer, String, String, String>>()

			.add(new Quartet<Integer, String, String, String>(0, "ER15004",
					"IRN should be same across "
							+ "all line items of a document",
					GSTConstants.IRN))

			.add(new Quartet<Integer, String, String, String>(1, "ER15123",
					"IRNDate should be same across "
							+ "all line items of a document",
					GSTConstants.IRN_DATE))
			.add(new Quartet<Integer, String, String, String>(7, "ER1047",
					"Document Date should be same across "
							+ "all line items of a document. ",
					DOC_DATE))
			.add(new Quartet<Integer, String, String, String>(8, "ER1098",
					" Reverse Charge Flag should be same across "
							+ "all line items of a document.",
					GSTConstants.ReverseCharge))
			.add(new Quartet<Integer, String, String, String>(9, "ER1055",
					"Supplier GSTIN should be same across all line "
							+ "items of a document.",
					GSTConstants.SGSTIN))
			
			.add(new Quartet<Integer, String, String, String>(27, "ER1060",
					"POS should be same across all line items of a document.",
					POS))
			
			.add(new Quartet<Integer, String, String, String>(30, "ER15034",
					"DispatcherGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_GSTIN))
			.add(new Quartet<Integer, String, String, String>(37, "ER16001",
					"ShipToGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.SHIP_TO_GSTIN))
			
			.add(new Quartet<Integer, String, String, String>(101, "ER16002",
					"ITCEntitlement should be same across "
							+ "all line items of a document",
					GSTConstants.ITC_ENTITLEMENT))
			
			.add(new Quartet<Integer, String, String, String>(109, "ER1063",
					" Port Code should be same across all line "
							+ "items of a document.",
					PORT_CODE))
			
			
			.add(new Quartet<Integer, String, String, String>(110, "ER1066",
					"Bill of Entry should be same across all line "
							+ "items of a document.",
					GSTConstants.BillOfEntry))
			.add(new Quartet<Integer, String, String, String>(111, "ER1069",
					" Bill of Entry Date should be same across all line "
							+ "items of a document.",
					GSTConstants.BillOfEntryDate))
			
			.add(new Quartet<Integer, String, String, String>(136, "ER15060",
					"PaymentDueDate should be same across "
							+ "all line items of a document",
					GSTConstants.PAYMENT_DUE_DATE))
			
			.add(new Quartet<Integer, String, String, String>(138, "ER0100",
					"EcomGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.E_ComGstin))
			
			.add(new Quartet<Integer, String, String, String>(153, "ER1033",
					"Return Period should be same across "
							+ "all line items of a document",
					RETURN_PREIOD))
			
			.add(new Quartet<Integer, String, String, String>(155, "ER16003",
					"OriginalSupplierGSTIN  should be same across "
							+ "all line items of a document",
					GSTConstants.ORIG_CUST_GSTIN))
			.add(new Quartet<Integer, String, String, String>(156, "ER16004",
					"DifferentialPercentageFlag  should be same across "
							+ "all line items of a document",
					GSTConstants.DIFF_PER_FLAG))
           
			.add(new Quartet<Integer, String, String, String>(157, "ER1073",
					"Section 7 Flag should be same across all line"
							+ " items of a document",
					SECTION7OFIGSTFLAG))
			.add(new Quartet<Integer, String, String, String>(161, "ER16005",
					"SupplierType should be same across all line"
							+ " items of a document",
					SECTION7OFIGSTFLAG))
			
			.add(new Quartet<Integer, String, String, String>(170, "ER15046",
					"TCSFlagIncomeTax should be same across "
							+ "all line items of a document",
					GSTConstants.TCSFlag))
			.add(new Quartet<Integer, String, String, String>(174, "ER15085",
					"TDSFlagGST should be same across "
							+ "all line items of a document",
					GSTConstants.TDSFlagGST))
			.add(new Quartet<Integer, String, String, String>(183, "ER1006",
					DIVISION + " should be same across all line "
							+ "items of a document.",
					GSTConstants.DIVISION))
			.add(new Quartet<Integer, String, String, String>(185, "ER1008",
					LOCATION + " should be same across all line "
							+ "items of a document.",
					GSTConstants.LOCATION))
			
			.add(new Quartet<Integer, String, String, String>(186, "ER1010",
					" Purchase organization should be same "
							+ "across all line items of a document",
					GSTConstants.PURCHASEORG))

			.add(new Quartet<Integer, String, String, String>(187, "ER1002",
					PROFITCENTRE + "should be same across all line "
							+ "items of a document.",
					GSTConstants.PROFITCENTRE))
			
			.add(new Quartet<Integer, String, String, String>(188, "ER1012",
					"User Access 1 should be same across "
							+ "all line items of a document",
					GSTConstants.USERACCESS1))
			
			.add(new Quartet<Integer, String, String, String>(203, "ER16006",
					"GLPostingDate should be same across "
							+ "all line items of a document",
					GSTConstants.GL_POSTING_DATE))
			
			.add(new Quartet<Integer, String, String, String>(205, "ER16007",
					"EWBNumber should be same across "
							+ "all line items of a document",
					GSTConstants.EWay_BillNo))
			.add(new Quartet<Integer, String, String, String>(206, "ER16008",
					"EWBDate should be same across "
							+ "all line items of a document",
					GSTConstants.EWay_BillDate))
			.add(new Quartet<Integer, String, String, String>(208, "ER16009",
					"AccountingVoucherDate should be same across all line items of a document",
					GSTConstants.ACCVOCHDATE))
			.build();

	public static List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();

		for (Object[] row : rows) {
			if (isPresent(row[3]) && GSTConstants.CAN.equalsIgnoreCase(
					trimAndConvToUpperCase(row[3].toString().trim()))) {
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

	private static ProcessingResult validateColForEqValues(List<Object[]> listobj,
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

			if ((colNo == 8) || (colNo == 156) 
					|| (colNo == 157) || (colNo == 170)
					|| (colNo == 174)) {
				if (curVal == null) {
					curVal = GSTConstants.N;
				}
			}
			if (isFirst) {
				prev = curVal;
				isFirst = false;
			} else {

				if (curVal == null) {
					if (prev != null) {

						errorLocations.add(triplet.getValue3());
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						return new ProcessingResult(APP_VALIDATION,
								triplet.getValue1(), triplet.getValue2(),
								location);

					}
				}
				if (curVal != null) {
					if (!curVal.equalsIgnoreCase(prev)) {
						if (colNo == 1 || colNo == 111 || colNo == 136 
								|| colNo == 203 || colNo == 206 || colNo == 208
								) {
							LocalDate curdate = DateFormatForStructuralValidatons
									.parseObjToDate(curVal);
							LocalDate prevdate = DateFormatForStructuralValidatons
									.parseObjToDate(prev);
							if (curdate == null && prevdate == null) {
								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);
							}

							if ((curdate == null && prevdate != null)
									|| (prevdate == null && curdate != null)) {

								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);

							}
							if (curdate != null && prevdate != null) {
								if (curdate.compareTo(prevdate) != 0) {
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									return new ProcessingResult(APP_VALIDATION,
											triplet.getValue1(),
											triplet.getValue2(), location);
								}
							}

						} else {

							errorLocations.add(triplet.getValue3());
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
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
