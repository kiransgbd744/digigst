package com.ey.advisory.app.services.strcutvalidation.outward;

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
@Component("ComprehensiveOutwardDocOtherStructuralValidations")
public class ComprehensiveOutwardDocOtherStructuralValidations {

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

			.add(new Quartet<Integer, String, String, String>(2, "ER15124",
					"TaxScheme should be same across "
							+ "all line items of a document",
					GSTConstants.TAX_SCHEMA))

			.add(new Quartet<Integer, String, String, String>(3, "ER15125",
					"CancellationReason should be same across "
							+ "all line items of a document",
					GSTConstants.CAN_REASON))

			.add(new Quartet<Integer, String, String, String>(4, "ER15126",
					"CancellationRemarks should be same across "
							+ "all line items of a document",
					GSTConstants.CAN_REMARKS))//

			.add(new Quartet<Integer, String, String, String>(6, "ER15127",
					"DocCategory should be same across "
							+ "all line items of a document",
					GSTConstants.DOC_CATEGORY))
			
			.add(new Quartet<Integer, String, String, String>(9, "ER0037",
					"DocumentDate should be same across "
							+ "all line items of a document",
					GSTConstants.DOC_DATE))
			.add(new Quartet<Integer, String, String, String>(10, "ER0095",
					"ReverseChargeFlag should be same across "
							+ "all line items of a document",
					GSTConstants.ReverseCharge))
			
			.add(new Quartet<Integer, String, String, String>(12, "ER15128",
					"SupplierTradeName should be same across "
							+ "all line items of a document",
					GSTConstants.SUPPLIER_TRADE_NAME))
			
			.add(new Quartet<Integer, String, String, String>(14, "ER15222",
					"SupplierAddress1 should be same across "
							+ "all line items of a document",
					GSTConstants.SupplierAddress1))
			
			.add(new Quartet<Integer, String, String, String>(15, "ER15143",
					"SupplierAddress2 should be same across "
							+ "all line items of a document",
					GSTConstants.SupplierAddress2))//

			.add(new Quartet<Integer, String, String, String>(17, "ER15144",
					"SupplierPincode should be same across "
							+ "all line items of a document",
					GSTConstants.SUPPLIER_PINCODE))
			.add(new Quartet<Integer, String, String, String>(18, "ER15145",
					"SupplierStateCode should be same across "
							+ "all line items of a document",
					GSTConstants.SUPPLIER_STATE_CODE))
			.add(new Quartet<Integer, String, String, String>(19, "ER15146",
					"SupplierPhone should be same across "
							+ "all line items of a document",
					GSTConstants.SUPPLIER_PHONE))
			.add(new Quartet<Integer, String, String, String>(20, "ER15133",
					"SupplierEmail should be same across "
							+ "all line items of a document",
					GSTConstants.SUPP_EMAIL))
			.add(new Quartet<Integer, String, String, String>(21, "ER0050",
					"CustomerGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.CGSTIN))
			.add(new Quartet<Integer, String, String, String>(22, "ER15027",
					"CustomerTradeName should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTO_TRADE_NAME))

			.add(new Quartet<Integer, String, String, String>(24, "ER15223",
					"CustomerAddress1 should be same across "
							+ "all line items of a document",
					GSTConstants.CUST_ADDER1))
			
			
			.add(new Quartet<Integer, String, String, String>(25, "ER15028",
					"CustomerAddress2 should be same across "
							+ "all line items of a document",
					GSTConstants.CUST_ADDER2))

			.add(new Quartet<Integer, String, String, String>(27, "ER15029",
					"CustomerPincode should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_PINCODE))
			.add(new Quartet<Integer, String, String, String>(28, "ER15030",
					"CustomerStateCode should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_STATE_CODE))
			.add(new Quartet<Integer, String, String, String>(29, "ER0057",
					"BillingPOS should be same across "
							+ "all line items of a document",
					GSTConstants.POS))
			.add(new Quartet<Integer, String, String, String>(30, "ER15032",
					"CustomerPhone should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_PHONE))
			.add(new Quartet<Integer, String, String, String>(31, "ER15033",
					"CustomerEmail should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_EMAIL))
			.add(new Quartet<Integer, String, String, String>(32, "ER15034",
					"DispatcherGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_GSTIN))
			.add(new Quartet<Integer, String, String, String>(33, "ER15035",
					"DispatcherTradeName should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_TRADE_NAME))
			.add(new Quartet<Integer, String, String, String>(34, "ER15036",
					"DispatcherAddress1 should be same across "
							+ "all line items of a document",
					GSTConstants.DIS_PATCHER_ADDR1))
			.add(new Quartet<Integer, String, String, String>(35, "ER15037",
					"DispatcherAddress2 should be same across "
							+ "all line items of a document",
					GSTConstants.DIS_PATCHER_ADDR2))
			.add(new Quartet<Integer, String, String, String>(36, "ER15038",
					"DispatcherLocation should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_LOCATION))
			.add(new Quartet<Integer, String, String, String>(37, "ER15039",
					"DispatcherPincode should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_PINCODE))
			.add(new Quartet<Integer, String, String, String>(38, "ER15040",
					"DispatcherStateCode should be same across "
							+ "all line items of a document",
					GSTConstants.DISPATCHER_STATE_CODE))
			.add(new Quartet<Integer, String, String, String>(40, "ER15041",
					"ShipToTradeName should be same across "
							+ "all line items of a document",
					GSTConstants.SHIP_TO_TRADE_NAME))
			.add(new Quartet<Integer, String, String, String>(42, "ER15042",
					"ShipToAddress1 should be same across "
							+ "all line items of a document",
					GSTConstants.SHIP_ADDR1))
			.add(new Quartet<Integer, String, String, String>(43, "ER15043",
					"ShipToAddress2 should be same across "
							+ "all line items of a document",
					GSTConstants.SHIP_ADDR2))//
			.add(new Quartet<Integer, String, String, String>(95, "ER15044",
					"RoundOff should be same across "
							+ "all line items of a document",
					GSTConstants.ROUND_OFF))
			.add(new Quartet<Integer, String, String, String>(96, "ER15045",
					"TotalInvoiceValue should be same across "
							+ "all line items of a document",
					GSTConstants.TOTAL_INV_VALUE))
			.add(new Quartet<Integer, String, String, String>(97, "ER15046",
					"TCSFlagIncomeTax should be same across "
							+ "all line items of a document",
					GSTConstants.TCSFlag))
			.add(new Quartet<Integer, String, String, String>(100, "ER15047",
					"CustomerPANOrAadhaar should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_PAN_ADHAR))
			.add(new Quartet<Integer, String, String, String>(104, "ER0060",
					"PortCode should be same across "
							+ "all line items of a document",
					GSTConstants.PORT_CODE))
			.add(new Quartet<Integer, String, String, String>(105, "ER0062",
					"ShippingBillNumber should be same across "
							+ "all line items of a document",
					GSTConstants.SHIPPING_BILL_NO))
			.add(new Quartet<Integer, String, String, String>(106, "ER0065",
					"ShippingBillDate should be same across "
							+ "all line items of a document",
					GSTConstants.SHIPPING_BILL_DATE))
			.add(new Quartet<Integer, String, String, String>(107, "ER15051",
					"InvoiceRemarks should be same across "
							+ "all line items of a document",
					GSTConstants.INV_REMARKS))

			.add(new Quartet<Integer, String, String, String>(121, "ER15052",
					"PayeeName should be same across "
							+ "all line items of a document",
					GSTConstants.PAYEE_NAME))
			.add(new Quartet<Integer, String, String, String>(122, "ER15053",
					"ModeOfPayment should be same across "
							+ "all line items of a document",
					GSTConstants.MODE_OF_PAYMENT))
			.add(new Quartet<Integer, String, String, String>(123, "ER15054",
					"BranchOrIFSCCode should be same across "
							+ "all line items of a document",
					GSTConstants.BRANCH_OR_IFSC_CODE))
			.add(new Quartet<Integer, String, String, String>(124, "ER15055",
					"PaymentTerms should be same across "
							+ "all line items of a document",
					GSTConstants.PAYMENT_TERMS))
			.add(new Quartet<Integer, String, String, String>(125, "ER15056",
					"PaymentInstruction should be same across "
							+ "all line items of a document",
					GSTConstants.PAYMENT_INNSTRUCTIONS))
			.add(new Quartet<Integer, String, String, String>(126, "ER15057",
					"CreditTransfer should be same across "
							+ "all line items of a document",
					GSTConstants.CREDIT_TRANSFER))

			.add(new Quartet<Integer, String, String, String>(127, "ER15058",
					"DirectDebit should be same across "
							+ "all line items of a document",
					GSTConstants.DIRDBT))
			.add(new Quartet<Integer, String, String, String>(128, "ER15059",
					"CreditDays should be same across "
							+ "all line items of a document",
					GSTConstants.CREDIT_DAYS))
			.add(new Quartet<Integer, String, String, String>(131, "ER15060",
					"PaymentDueDate should be same across "
							+ "all line items of a document",
					GSTConstants.PAYMENT_DUE_DATE))
			.add(new Quartet<Integer, String, String, String>(132, "ER15061",
					"AccountDetail should be same across "
							+ "all line items of a document",
					GSTConstants.ACCOUNT_DETAIL))
			.add(new Quartet<Integer, String, String, String>(133, "ER0100",
					"EcomGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.E_ComGstin))
			.add(new Quartet<Integer, String, String, String>(134, "ER15063",
					"EcomTransactionID should be same across "
							+ "all line items of a document",
					GSTConstants.ECOM_TRANSACTION))
			.add(new Quartet<Integer, String, String, String>(138, "ER15064",
					"TransactionType should be same across "
							+ "all line items of a document",
					GSTConstants.TRANSACTION_TYPE))
			.add(new Quartet<Integer, String, String, String>(139, "ER15065",
					"SubSupplyType should be same across "
							+ "all line items of a document",
					GSTConstants.SUB_SUPPLY_TYPE))

			.add(new Quartet<Integer, String, String, String>(140, "ER15066",
					"OtherSupplyTypeDescription should be same across "
							+ "all line items of a document",
					GSTConstants.OTH_SUPTYPE_DESC))

			.add(new Quartet<Integer, String, String, String>(141, "ER15067",
					"TransporterID should be same across "
							+ "all line items of a document",
					GSTConstants.TRANS_PORTER_ID))
			.add(new Quartet<Integer, String, String, String>(142, "ER15068",
					"TransporterName should be same across "
							+ "all line items of a document",
					GSTConstants.TRANSPOTER_NAME))
			.add(new Quartet<Integer, String, String, String>(144, "ER15069",
					"TransportDocNo should be same across "
							+ "all line items of a document",
					GSTConstants.TRANS_DOC_NO))
			.add(new Quartet<Integer, String, String, String>(145, "ER15070",
					"TransportDocDate should be same across "
							+ "all line items of a document",
					GSTConstants.TRANS_DOC_DATE))
			.add(new Quartet<Integer, String, String, String>(147, "ER15071",
					"VehicleNo should be same across "
							+ "all line items of a document",
					GSTConstants.VEHICLE_NO))
			.add(new Quartet<Integer, String, String, String>(148, "ER15072",
					"VehicleType should be same across "
							+ "all line items of a document",
					GSTConstants.VEHICLE_TYPE))

			.add(new Quartet<Integer, String, String, String>(149, "ER0023",
					"ReturnPeriod should be same across "
							+ "all line items of a document",
					GSTConstants.RETURN_PERIOD))
			.add(new Quartet<Integer, String, String, String>(150, "ER15073",
					"OriginalDocumentType should be same across "
							+ "all line items of a document",
					GSTConstants.ORGDOC_TYPE))
			.add(new Quartet<Integer, String, String, String>(151, "ER15074",
					"OriginalCustomerGSTIN should be same across "
							+ "all line items of a document",
					GSTConstants.ORG_CGSTN))
			.add(new Quartet<Integer, String, String, String>(152, "ER0125",
					"DifferentialPercentageFlag should be same across "
							+ "all line items of a document",
					GSTConstants.DIFF_PER_FLAG))

			.add(new Quartet<Integer, String, String, String>(153, "ER0073",
					"Section7OfIGSTFlag should be same across "
							+ "all line items of a document",
					GSTConstants.SEC7_IGST_FLAG))
			.add(new Quartet<Integer, String, String, String>(154, "ER0104",
					"ClaimRefundFlag should be same across "
							+ "all line items of a document",
					GSTConstants.CLAIMREFUNDFLAG))

			.add(new Quartet<Integer, String, String, String>(155, "ER0106",
					"AutoPopulateToRefund should be same across "
							+ "all line items of a document",
					GSTConstants.AUTO_POP_REFUND))
			.add(new Quartet<Integer, String, String, String>(156, "ER0045",
					"CRDRPreGST should be same across "
							+ "all line items of a document",
					GSTConstants.PRE_GST))
			.add(new Quartet<Integer, String, String, String>(157, "ER0124",
					"CustomerType should be same across "
							+ "all line items of a document",
					GSTConstants.RECIPIENTTYPE))
			.add(new Quartet<Integer, String, String, String>(158, "ER15082",
					"CustomerCode should be same across "
							+ "all line items of a document",
					GSTConstants.CUST_CODE))

			.add(new Quartet<Integer, String, String, String>(162, "ER15083",
					"StateApplyingCess should be same across "
							+ "all line items of a document",
					GSTConstants.STATEAPPLYINGCESS))
			.add(new Quartet<Integer, String, String, String>(165, "ER15084",
					"ExchangeRate should be same across "
							+ "all line items of a document",
					GSTConstants.EXCHANGE_RATE))

			.add(new Quartet<Integer, String, String, String>(171, "ER15085",
					"TDSFlagGST should be same across "
							+ "all line items of a document",
					GSTConstants.TDSFlagGST))
			.add(new Quartet<Integer, String, String, String>(175, "ER15086",
					"UserID should be same across "
							+ "all line items of a document",
					GSTConstants.UserID))
			.add(new Quartet<Integer, String, String, String>(176, "ER15087",
					"CompanyCode should be same across "
							+ "all line items of a document",
					GSTConstants.CompanyCode))
			.add(new Quartet<Integer, String, String, String>(177, "ER15088",
					"SourceIdentifier should be same across "
							+ "all line items of a document",
					GSTConstants.SourceIdentifier))
			.add(new Quartet<Integer, String, String, String>(178, "ER15089",
					"SourceFileName should be same across "
							+ "all line items of a document",
					GSTConstants.SourceFileName))
			.add(new Quartet<Integer, String, String, String>(180, "ER0114",
					"Division should be same across "
							+ "all line items of a document",
					GSTConstants.DIVISION))

			.add(new Quartet<Integer, String, String, String>(183, "ER0116",
					"SalesOrganisation should be same across "
							+ "all line items of a document",
					GSTConstants.SALESORG))
			.add(new Quartet<Integer, String, String, String>(182, "ER0115",
					"Location should be same across "
							+ "all line items of a document",
					GSTConstants.LOCATION))

			.add(new Quartet<Integer, String, String, String>(184, "ER15093",
					"DistributionChannel should be same across "
							+ "all line items of a document",
					GSTConstants.DISTRIBUTIONCHAN))
			.add(new Quartet<Integer, String, String, String>(185, "ER0112",
					"ProfitCentre1 should be same across "
							+ "all line items of a document",
					GSTConstants.PROFITCENTRE1))
			.add(new Quartet<Integer, String, String, String>(186, "ER15096",
					"ProfitCentre2 should be same across "
							+ "all line items of a document",
					GSTConstants.PROFITCENTRE2))
			/*.add(new Quartet<Integer, String, String, String>(194, "ER15097",
					"GLIGST should be same across "
							+ "all line items of a document",
					GSTConstants.GLIGST))

			.add(new Quartet<Integer, String, String, String>(195, "ER15098",
					"GLCGST should be same across "
							+ "all line items of a document",
					GSTConstants.GLCGST))
			.add(new Quartet<Integer, String, String, String>(196, "ER15099",
					"GLSGST should be same across "
							+ "all line items of a document",
					GSTConstants.GLSGST))

			.add(new Quartet<Integer, String, String, String>(197, "ER15100",
					"GLAdvaloremCess should be same across "
							+ "all line items of a document",
					GSTConstants.GLAdvaloremCess))
					
			.add(new Quartet<Integer, String, String, String>(198, "ER15101",
					"GLSpecificCess should be same across "
							+ "all line items of a document",
					GSTConstants.GLSpecificCess))
					
			.add(new Quartet<Integer, String, String, String>(199, "ER15102",
					"GLStateCessAdvalorem should be same across "
							+ "all line items of a document",
					GSTConstants.GLStateCessAdvalorem))
					
			.add(new Quartet<Integer, String, String, String>(200, "ER15103",
					"GLStateCessSpecific should be same across "
							+ "all line items of a document",
					GSTConstants.GLStateCessSpecific))
*/
			.add(new Quartet<Integer, String, String, String>(201, "ER15104",
					"GLPostingDate should be same across "
							+ "all line items of a document",
					GSTConstants.GL_POSTING_DATE))
			.add(new Quartet<Integer, String, String, String>(202, "ER15105",
					"SalesOrderNumber should be same across "
							+ "all line items of a document",
					GSTConstants.SalesOrderNumber))

			.add(new Quartet<Integer, String, String, String>(203, "ER15107",
					"EWBNumber should be same across "
							+ "all line items of a document",
					GSTConstants.EWay_BillNo))
			.add(new Quartet<Integer, String, String, String>(204, "ER15108",
					"EWBDate should be same across "
							+ "all line items of a document",
					GSTConstants.EWay_BillDate))
			.add(new Quartet<Integer, String, String, String>(205, "ER15109",
					"AccountingVoucherNumber should be same across "
							+ "all line items of a document",
					GSTConstants.ACCVOCHNUM))
			.add(new Quartet<Integer, String, String, String>(206, "ER15110",
					"AccountingVoucherDate should be same across "
							+ "all line items of a document",
					GSTConstants.ACCVOCHDATE))

			.add(new Quartet<Integer, String, String, String>(208, "ER15141",
					"CustomerTAN should be same across "
							+ "all line items of a document",
					GSTConstants.CUSTOMER_TAN))
			.build();

	public  List<ProcessingResult> validate(List<Object[]> rows) {

		List<ProcessingResult> results = new ArrayList<>();
		for (Object[] row : rows) {
			if (isPresent(row[5]) && GSTConstants.CAN.equalsIgnoreCase(
					trimAndConvToUpperCase(row[5].toString().trim()))) {
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

			if ((colNo == 10) || (colNo == 152) || (colNo == 153)
					|| (colNo == 154) || (colNo == 155) || (colNo == 156)
					|| (colNo == 171)) {
				if (curVal == null) {
					curVal =GSTConstants.N;
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
						if (colNo == 1 || colNo == 9 || colNo == 106 
								|| colNo == 131 || colNo == 145 
								||  colNo == 201 || colNo == 204 
								|| colNo == 206) {
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
