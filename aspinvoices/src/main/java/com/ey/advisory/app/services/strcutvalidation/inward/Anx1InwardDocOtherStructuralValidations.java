package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DIVISION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;
import static com.ey.advisory.common.GSTConstants.DTA;
import static com.ey.advisory.common.GSTConstants.DXP;
import static com.ey.advisory.common.GSTConstants.IMPG;
import static com.ey.advisory.common.GSTConstants.LOCATION;
import static com.ey.advisory.common.GSTConstants.PLANT;
import static com.ey.advisory.common.GSTConstants.PORT_CODE;
import static com.ey.advisory.common.GSTConstants.POS;
import static com.ey.advisory.common.GSTConstants.PRE_GST;
import static com.ey.advisory.common.GSTConstants.PROFITCENTRE;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;
import static com.ey.advisory.common.GSTConstants.SECTION7OFIGSTFLAG;
import static com.ey.advisory.common.GSTConstants.USERACCESS2;
import static com.ey.advisory.common.GSTConstants.USERACCESS3;
import static com.ey.advisory.common.GSTConstants.USERACCESS4;
import static com.ey.advisory.common.GSTConstants.USERACCESS5;
import static com.ey.advisory.common.GSTConstants.USERACCESS6;
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
 * @author Siva.Nandam
 *
 */
@Component("Anx1InwardDocOtherStructuralValidations")
public class Anx1InwardDocOtherStructuralValidations {

	private static final List<Quartet<Integer, String, String, String>> LIST 
	= new ImmutableList.Builder<Quartet<Integer, String, String, String>>()

			.add(new Quartet<Integer, String, String, String>(2, "ER1002",
					PROFITCENTRE + "should be same across all line "
							+ "items of a document.",
					GSTConstants.PROFITCENTRE))
			.add(new Quartet<Integer, String, String, String>(3, "ER1004",
					PLANT + " should be same across all line "
							+ "items of a document.",
					GSTConstants.PLANT))
			.add(new Quartet<Integer, String, String, String>(4, "ER1006",
					DIVISION + " should be same across all line "
							+ "items of a document.",
					GSTConstants.DIVISION))
			.add(new Quartet<Integer, String, String, String>(5, "ER1008",
					LOCATION + " should be same across all line "
							+ "items of a document.",
					GSTConstants.LOCATION))
			.add(new Quartet<Integer, String, String, String>(6, "ER1010",
					" Purchase organization should be same "
							+ "across all line items of a document",
					GSTConstants.PURCHASEORG))

			.add(new Quartet<Integer, String, String, String>(7, "ER1012",
					"User Access 1 should be same across "
							+ "all line items of a document",
					GSTConstants.USERACCESS1))
			.add(new Quartet<Integer, String, String, String>(8, "ER1014",
					"User Access 2 should be same across "
							+ "all line items of a document",
					USERACCESS2))
			.add(new Quartet<Integer, String, String, String>(9, "ER1016",
					"User Access 3 should be same across "
							+ "all line items of a document",
					USERACCESS3))
			.add(new Quartet<Integer, String, String, String>(10, "ER1018",
					"User Access 4 should be same across "
							+ "all line items of a document",
					USERACCESS4))

			.add(new Quartet<Integer, String, String, String>(11, "ER1020",
					"User Access 5 should be same across "
							+ "all line items of a document",
					USERACCESS5))
			.add(new Quartet<Integer, String, String, String>(12, "ER1022",
					"User Access 6 should be same across "
							+ "all line items of a document",
					USERACCESS6))

			.add(new Quartet<Integer, String, String, String>(20, "ER1033",
					"Return Period should be same across "
							+ "all line items of a document",
					RETURN_PREIOD))

			.add(new Quartet<Integer, String, String, String>(25, "ER1047",
					"Document Date should be same across "
							+ "all line items of a document. ",
					DOC_DATE))

			.add(new Quartet<Integer, String, String, String>(28, "ER1049",
					"CRDRPreGST should be same across "
							+ "all line items of a document.",
					PRE_GST))
			.add(new Quartet<Integer, String, String, String>(30, "ER1055",
					"Supplier GSTIN should be same across all line "
							+ "items of a document.",
					GSTConstants.SGSTIN))

			.add(new Quartet<Integer, String, String, String>(40, "ER1060",
					"POS should be same across all line items of a document.",
					POS))
			.add(new Quartet<Integer, String, String, String>(42, "ER1063",
					" Port Code should be same across all line "
							+ "items of a document.",
					PORT_CODE))
			.add(new Quartet<Integer, String, String, String>(43, "ER1066",
					"Bill of Entry should be same across all line "
							+ "items of a document.",
					GSTConstants.BillOfEntry))
			.add(new Quartet<Integer, String, String, String>(44, "ER1069",
					" Bill of Entry Date should be same across all line "
							+ "items of a document.",
					GSTConstants.BillOfEntryDate))

			.add(new Quartet<Integer, String, String, String>(53, "ER1073",
					"Section 7 Flag should be same across all line"
							+ " items of a document",
					SECTION7OFIGSTFLAG))

			.add(new Quartet<Integer, String, String, String>(69, "ER0186",
					"Claim Refund Flag should be same across "
							+ "all line items of a document.",
					GSTConstants.CLAIMREFUNDFLAG))
			.add(new Quartet<Integer, String, String, String>(70, "ER1088",
					"Auto Populate to Refund Flag should be same "
							+ "across all line items of a document.",
					GSTConstants.AUTO_POP_REFUND))
			.add(new Quartet<Integer, String, String, String>(80, "ER1098",
					" Reverse Charge Flag should be same across "
							+ "all line items of a document.",
					GSTConstants.ReverseCharge))

			.build();

	private static final List<String> SUPPLY_TYPE = ImmutableList.of(IMPG);
	private static final List<String> SUPPLY_TYPE70 = ImmutableList.of(DTA,
			DXP);

	public List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();

		for (Object[] row : rows) {
			if (isPresent(row[23]) && GSTConstants.CAN.equalsIgnoreCase(
					trimAndConvToUpperCase(row[23].toString().trim()))) {
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
				if (

				(colNo == 4) || (colNo == 6) || (colNo == 7) || (colNo == 8)
						|| (colNo == 9) || (colNo == 10) || (colNo == 11)
						|| (colNo == 12)

						|| (colNo == 20) || (colNo == 25) || (colNo == 28)
						|| (colNo == 30) || (colNo == 40)
						|| (colNo == 42 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString().trim())))
						|| (colNo == 43 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString().trim())))
						|| (colNo == 44 && SUPPLY_TYPE.contains(
								trimAndConvToUpperCase(supplyType.toString().trim())))
						|| (colNo == 53)
						|| (colNo == 69 && SUPPLY_TYPE70.contains(
								trimAndConvToUpperCase(supplyType.toString().trim())))
						|| (colNo == 70)
						|| (colNo == 80 &&GSTConstants.IMPS
								.equalsIgnoreCase(supplyType.toString().trim()))
						|| (colNo == 80
								&& GSTConstants.SLF
								.equalsIgnoreCase(doctype.toString().trim()))

				) {
					if ((colNo == 28) || (colNo == 53) || (colNo == 69)
							|| (colNo == 70) || (colNo == 80)) {
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
								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								return new ProcessingResult(APP_VALIDATION,
										triplet.getValue1(),
										triplet.getValue2(), location);
							}
						}
						if (curVal != null) {
							if (!curVal.equalsIgnoreCase(prev)) {
								if(colNo == 25 || colNo == 44 ){
									LocalDate curdate = DateFormatForStructuralValidatons
							                  .parseObjToDate(curVal);
									LocalDate prevdate = DateFormatForStructuralValidatons
							                  .parseObjToDate(prev);
									if(curdate==null && prevdate==null){
										TransDocProcessingResultLoc location 
										= new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										return new ProcessingResult(APP_VALIDATION,
												triplet.getValue1(),
												triplet.getValue2(), location);
									}
									
									if ((curdate == null && prevdate != null) 
											||( prevdate == null && curdate != null)) {
										
											TransDocProcessingResultLoc location 
											= new TransDocProcessingResultLoc(
													null, errorLocations.toArray());
											return new ProcessingResult(APP_VALIDATION,
													triplet.getValue1(),
													triplet.getValue2(), location);
										
										}
									if(curdate!=null && prevdate!=null){
										if(curdate.compareTo(prevdate)!=0){
											TransDocProcessingResultLoc location 
											= new TransDocProcessingResultLoc(
													null, errorLocations.toArray());
											return new ProcessingResult(APP_VALIDATION,
													triplet.getValue1(),
													triplet.getValue2(), location);
										}
									}
									
								}else{
								
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
