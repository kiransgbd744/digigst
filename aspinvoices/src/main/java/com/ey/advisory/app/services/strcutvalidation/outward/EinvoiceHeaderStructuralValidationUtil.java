package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * @author Mahesh.Golla
 *
 */
public class EinvoiceHeaderStructuralValidationUtil {

	private EinvoiceHeaderStructuralValidationUtil() {
	}

	// Header Fields
	private static final Map<String, Integer> HEADER_FIELDS = 
			new ImmutableMap.Builder<String, Integer>()

			.put(GSTConstants.IRN, 1)
			.put(GSTConstants.IRN_DATE, 1)
			.put(GSTConstants.TAX_SCHEME, 1)
			.put(GSTConstants.CAN_REASON, 1)
			.put(GSTConstants.DOC_CATEGORY, 1)
			.put(GSTConstants.DOC_TYPE, 1)
			.put(GSTConstants.DOC_NO, 1)
			.put(GSTConstants.DOC_DATE, 1)
			.put(GSTConstants.ReverseCharge, 1)
			.put(GSTConstants.SGSTIN, 1)
			.put(GSTConstants.SUPPLIER_PINCODE, 1)
			.put(GSTConstants.SUPPLIER_STATE_CODE, 1)
			.put(GSTConstants.RecipientGSTIN, 1)
			.put(GSTConstants.CUSTOMER_PINCODE, 1)
			.put(GSTConstants.BillToState, 1)
			.put(GSTConstants.POS, 1)
			.put(GSTConstants.DISPATCHER_GSTIN, 1)
			.put(GSTConstants.DISPATCHER_PINCODE, 1)
			.put(GSTConstants.DISPATCHER_STATE_CODE, 1)
			.put(GSTConstants.SHIP_TO_GSTIN, 1)
			.put(GSTConstants.SHIP_TO_PINCODE, 1)
			.put(GSTConstants.SHIP_TO_STATE_CODE, 1)
			.put(GSTConstants.ORIGIN_COUNTRY, 1)
			.put(GSTConstants.TOTAL_INV_VALUE, 1)
			.put(GSTConstants.TCS_FLAG_INCOME_TAX, 1)
			.put(GSTConstants.TCS_AMOUNT_INCOME_TAX, 1)
			.put(GSTConstants.CUSTOMER_PAN_ADHAR, 1)
			.put(GSTConstants.FOREIGN_CURRENCY, 1)
			.put(GSTConstants.COUNTRY_CODE, 1)
			.put(GSTConstants.INVOICE_VALUEFC, 1)
			.put(GSTConstants.PORT_CODE, 1)
			.put(GSTConstants.SHIPPING_BILL_NO, 1)
			.put(GSTConstants.SHIPPING_BILL_DATE, 1)
			.put(GSTConstants.INVOICE_PERIOD_STARTDATE, 1)
			.put(GSTConstants.INVOICE_PERIOD_ENDDATE, 1)
			.put(GSTConstants.CUSTOMER_PO_REFERENCE_DATE, 1)
			.put(GSTConstants.MODE_OF_PAYMENT, 1)
			.put(GSTConstants.BRANCH_OR_IFSC_CODE, 1)
			.put(GSTConstants.CREDIT_DAYS, 1)
			.put(GSTConstants.PAYMENT_DUE_DATE, 1)
			.put(GSTConstants.ACCOUNT_DETAIL, 1)
			.put(GSTConstants.E_ComGstin, 1)
			.put(GSTConstants.ECOM_TRANSACTION, 1)
			.put(GSTConstants.TRANSCTION_TYPE, 1)
			.put(GSTConstants.SUB_SUPPLY_TYPE, 1)
			.put(GSTConstants.OTH_SUPTYPE_DESC, 1)
			.put(GSTConstants.TRANS_PORTER_ID, 1)
			.put(GSTConstants.TRANS_PORT_MODE, 1)
			.put(GSTConstants.TRANS_PORT_DOCNO, 1)
			.put(GSTConstants.TRANS_PORT_DOCDATE, 1)
			.put(GSTConstants.DISTANCE, 1)
			.put(GSTConstants.VEHICLE_NO, 1)
			.put(GSTConstants.VEHICLE_TYPE, 1)
			.put(GSTConstants.RETURN_PREIOD, 1)
			.put(GSTConstants.ORGDOC_TYPE, 1)
			.put(GSTConstants.ORIG_CUST_GSTIN, 1)
			.put(GSTConstants.DifferentialPercentageFlag, 1)
			.put(GSTConstants.SECTION7OFIGSTFLAG, 1)
			.put(GSTConstants.CLAIMREFUNDFLAG, 1)
			.put(GSTConstants.AUTOPOPULATED, 1)
			.put(GSTConstants.PRE_GST, 1)
			.put(GSTConstants.RECIPIENTTYPE, 1)
			.put(GSTConstants.CUST_CODE, 1)
			.put(GSTConstants.PRODUCT_CODE, 1)
			.put(GSTConstants.STATEAPPLYINGCESS, 1)
			.put(GSTConstants.EXCHANGE_RATE, 1)
			.put(GSTConstants.DIVISION, 1)
			.put(GSTConstants.LOCATION, 1)
			.put(GSTConstants.SALESORG, 1)
			.put(GSTConstants.DISTRIBUTIONCHAN, 1)
			.put(GSTConstants.GL_POSTING_DATE, 1)
			.put(GSTConstants.SalesOrderNumber, 1)
			.put(GSTConstants.EWay_BillNo, 1)
			.put(GSTConstants.EWay_BillDate, 1)
			.put(GSTConstants.ACCVOCHDATE, 1)
			.put(GSTConstants.CUSTOMER_TAN, 1)
			.put(GSTConstants.SUPPLIER_TRADE_NAME, 1)
			.put(GSTConstants.SUPPLIER_LEGAL_NAME, 1)
			.put(GSTConstants.SupplierAddress1, 1)
			.put(GSTConstants.SupplierAddress2, 1)
			.put(GSTConstants.SUPPLIER_LOCATION, 1)
			.put(GSTConstants.SUPPLIER_PHONE, 1)
			.put(GSTConstants.SUPPLIER_MAIL, 1)
			.put(GSTConstants.CUSTO_TRADE_NAME, 1)
			.put(GSTConstants.CUSTOMER_LEGAL_NAME, 1)
			.put(GSTConstants.CUST_ADDER1, 1)
			.put(GSTConstants.CUST_ADDER2, 1)
			.put(GSTConstants.CUSTOMER_LOCATION, 1)
			.put(GSTConstants.CUSTOMER_PHONE, 1)
			.put(GSTConstants.CUSTOMER_EMAIL, 1)
			.put(GSTConstants.DISPATCHER_TRADE_NAME, 1)
			.put(GSTConstants.DIS_PATCHER_ADDR1, 1)
			.put(GSTConstants.DIS_PATCHER_ADDR2, 1)
			.put(GSTConstants.DISPATCHER_LOCATION, 1)
			.put(GSTConstants.SHIP_TO_TRADE_NAME, 1)
			.put(GSTConstants.SHIP_TO_LEGAL_NAME, 1)
			.put(GSTConstants.SHIP_TO_ADDRESS1, 1)
			.put(GSTConstants.SHIP_TO_ADDRESS2, 1)
			.put(GSTConstants.SHIP_TO_LOCATION, 1)
			.put(GSTConstants.TRANSPOTER_NAME, 1)
			.put(GSTConstants.USER_DEFINED28,1)
			.build();

	private static boolean isHeaderField(String fieldName) {
		return HEADER_FIELDS.containsKey(fieldName);
	}

	static Map<String, List<ProcessingResult>> keyMap = new HashMap<>();

	public static List<ProcessingResult> eliminateDuplicates(
			List<ProcessingResult> errors) {
		// creating list for non_header_errors
		List<ProcessingResult> nonHeaderErrors = new ArrayList<ProcessingResult>();
		// creating Map for non_header_errors
		Map<String, ProcessingResult> headerErrorsMap = new HashMap<>();

		for (ProcessingResult error : errors) {
			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			if(loc != null){
			Object[] fields = loc.getFieldIdentifiers();
			if (fields.length == 0) {
				nonHeaderErrors.add(error);
				continue;
			}

			String fieldName = (String) fields[0];
			boolean isHeaderField = isHeaderField(fieldName);

			if (isHeaderField) {
				// creating key by using fileldName and errorCode
				String key = fieldName + error.getCode();
				headerErrorsMap.putIfAbsent(key, error);
			} else {
				nonHeaderErrors.add(error);
			}
		}
		}

		List<ProcessingResult> retResult = new ArrayList();
		// added non_header errors.
		retResult.addAll(nonHeaderErrors);
		// added header_errors
		retResult.addAll(headerErrorsMap.values());

		return retResult;
	}

}
