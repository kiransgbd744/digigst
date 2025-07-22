package com.ey.advisory.app.services.docs.gstr7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Handling Outward Trans Docs
 * 
 * @author Siva.Reddy
 *
 * @param <K>
 */
@Slf4j
public class Gstr7TransDocRowHandler<K> implements RowHandler {

	private Object[] headerData = null;

	public Gstr7TransDocRowHandler() {
	}

	public Object[] getHeaderData() {
		return headerData;
	}

	@Override
	public void handleHeaderRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		// clone the shared array passed by the framework.
		Object[] arr = row.clone();
		headerData = arr;
	}

	/**
	 * The map to be populated with the the data from the file.
	 */
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();

	private DataBlockKeyBuilder<K> keyBuilder;

	public Gstr7TransDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		Object[] arr = row.clone();
		boolean isRowEmpty = true;
		for (Object object : arr) {
			if (object != null) {
				isRowEmpty = false;
			}
		}
	//	removeSpecialChars(arr);
		String key = (String) keyBuilder.buildDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey in OutwardTransDocRowHandler " + key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}

		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in OutwardTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

	private void removeSpecialChars(Object[] obj) {
		/*
		 * DocumentNumber SupplierStateCode CustomerStateCode BillingPOS
		 * DispatcherStateCode ShipToStateCode ReturnPeriod
		 * 
		 */
		obj[8] = CommonUtility.capital(obj[8]); // DocNo
		obj[8] = CommonUtility.singleQuoteCheck(obj[8]); // DocNo
		obj[8] = CommonUtility.exponentialAndZeroCheck(obj[8]);// DocNo
		obj[18] = CommonUtility.singleQuoteCheck(obj[18]);// supstatecode
		obj[19] = CommonUtility.exponentialAndZeroCheck(obj[19]);// supphonecode
		obj[28] = CommonUtility.singleQuoteCheck(obj[28]); // bill to state code
		obj[29] = CommonUtility.singleQuoteCheck(obj[29]); // pos
		obj[30] = CommonUtility.exponentialAndZeroCheck(obj[30]); // CustomerPhoneValidation
		obj[38] = CommonUtility.singleQuoteCheck(obj[38]); // DispatcherStateCodeValidation
		obj[46] = CommonUtility.singleQuoteCheck(obj[46]); // ShipToStateCodeValidation
		obj[48] = CommonUtility.exponentialAndZeroCheck(obj[48]); // ProductSerialNumber
		obj[52] = CommonUtility.singleQuoteCheck(obj[52]); // HSNSACValidationRule
		obj[52] = CommonUtility.exponentialAndZeroCheck(obj[52]); // HSNSACValidationRule
		obj[57] = CommonUtility.exponentialAndZeroCheck(obj[57]); // orderLineRef
		obj[59] = CommonUtility.exponentialAndZeroCheck(obj[59]); // Attributevalue
		//obj[64] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[64]); // UnitPrice
		obj[65] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[65]); // ItemAmountValidation
		obj[67] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[67]); // PreTaxAmountValidation
		obj[68] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[68]); // ItemAssessableAmountValidation
		obj[70] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[70]); // IgstAmtValidationRule
		obj[72] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[72]); // CgstAmtValidationRule
		obj[74] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[74]); // SgstAmtValidationRule
		obj[76] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[76]); // AdvaloremCessAmtValidationRule
		obj[78] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[78]); // SpecificCessAmtValidationRule
		obj[80] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[80]); // StateCessAdvaloremAmtValidationRule
		obj[82] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[82]); // StateCessAmtValidationRule
		obj[84] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[84]); // TotalItemAmtValidation
		obj[86] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[86]); // InvoiceAssessableAmountValidation
		obj[87] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[87]); // InvoiceIgstAmountValidation
		obj[88] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[88]); // InvoiceCgstAmountValidation
		obj[89] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[89]); // InvoiceSgstAmountValidation
		obj[90] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[90]); // InvoiceCessAdvaloremAmountValidation
		obj[91] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[91]); // InvoiceCessSpecificAmountValidation
		obj[92] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[92]); // InvoiceStateCessAmountValidation
		obj[93] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[93]); // InvoiceStateCessAdvaloremAmountValidation
		obj[94] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[94]); // InvoiceValueValidationRule
		obj[96] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[96]); // TotalInvValueValidation
		obj[99] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[99]); // TcsAmountIncomeTaxValidation
		obj[105] = CommonUtility.exponentialAndZeroCheck(obj[105]); // ShippingBillNumValidationRule
		obj[110] = CommonUtility.singleQuoteCheck(obj[110]); // PreceedingInvoiceNumberValidation
		obj[110] = CommonUtility.exponentialAndZeroCheck(obj[110]); // PreceedingInvoiceNumberValidation
		obj[112] = CommonUtility.exponentialAndZeroCheck(obj[112]); // Otherreference
		obj[113] = CommonUtility.exponentialAndZeroCheck(obj[113]); // receiptAdviceReference
		obj[115] = CommonUtility.exponentialAndZeroCheck(obj[115]); // tenderrefernce
		obj[116] = CommonUtility.exponentialAndZeroCheck(obj[116]); // contractRef
		obj[117] = CommonUtility.exponentialAndZeroCheck(obj[117]); // externalreference
		obj[118] = CommonUtility.exponentialAndZeroCheck(obj[118]); // projectreference
		obj[119] = CommonUtility.exponentialAndZeroCheck(obj[119]); // CustomerPoReferenceNoValidation
		obj[129] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[129]); // PaidAmountValidation
		obj[130] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[130]); // BalanceAmountValidation
		obj[132] = CommonUtility.exponentialAndZeroCheck(obj[132]); // AccountDetails
		obj[144] = CommonUtility.exponentialAndZeroCheck(obj[144]); // TransportDocNoValidation
		obj[149] = CommonUtility.singleQuoteCheck(obj[149]); // RetPeriod
		obj[168] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[168]); // TCSAmountValidationRule
		obj[169] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[169]); // TCSAmountCGSTValidationRule
		obj[170] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[170]); // TCSAmountSGSTValidationRule
		obj[172] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[172]); // TDSAmountValidationRule
		obj[173] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[173]); // TDSAmountCGSTValidationRule
		obj[174] = CommonUtility.exponentialAndZeroCheckForBigDecimal(obj[174]); // TDSAmountSGSTValidationRule
		obj[186] = CommonUtility.exponentialAndZeroCheck(obj[186]); // NewProfitCentre2
		obj[193] = CommonUtility.exponentialAndZeroCheck(obj[193]); // GLAccasbleValue
		obj[194] = CommonUtility.exponentialAndZeroCheck(obj[194]); // GlIgst
		obj[195] = CommonUtility.exponentialAndZeroCheck(obj[195]); // GlCgst
		obj[196] = CommonUtility.exponentialAndZeroCheck(obj[196]); // GlSgst
		obj[197] = CommonUtility.exponentialAndZeroCheck(obj[197]); // glAdvaloremCess
		obj[198] = CommonUtility.exponentialAndZeroCheck(obj[198]); // glAdvaloremSpecificCess
		obj[199] = CommonUtility.exponentialAndZeroCheck(obj[199]); // glStateCessAdvalore
		obj[200] = CommonUtility.exponentialAndZeroCheck(obj[200]); // glStateCess
		obj[202] = CommonUtility.exponentialAndZeroCheck(obj[202]); // SalesOrdernumber
		obj[203] = CommonUtility.singleQuoteCheck(obj[203]); // EWayBillNumberValidationRule
		obj[203] = CommonUtility.exponentialAndZeroCheck(obj[203]); // EWayBillNumberValidationRule
		obj[205] = CommonUtility.exponentialAndZeroCheck(obj[205]); // AccountingVoucNoValidationRule
		obj[207] = CommonUtility.exponentialAndZeroCheck(obj[207]); // DocRefNoValidationRule
		
		obj[209] = CommonUtility.singleQuoteCheck(obj[209]); // UserDefinedField1
		obj[210] = CommonUtility.singleQuoteCheck(obj[210]); // UserDefinedField2
		obj[211] = CommonUtility.singleQuoteCheck(obj[211]); // UserDefinedField3
		obj[212] = CommonUtility.singleQuoteCheck(obj[212]); // UserDefinedField4
		obj[213] = CommonUtility.singleQuoteCheck(obj[213]); // UserDefinedField5
		obj[214] = CommonUtility.singleQuoteCheck(obj[214]); // UserDefinedField6
		obj[215] = CommonUtility.singleQuoteCheck(obj[215]); // UserDefinedField7
		obj[216] = CommonUtility.singleQuoteCheck(obj[216]); // UserDefinedField8
		obj[217] = CommonUtility.singleQuoteCheck(obj[217]); // UserDefinedField9
		obj[218] = CommonUtility.singleQuoteCheck(obj[218]); // UserDefinedField10
		obj[219] = CommonUtility.singleQuoteCheck(obj[219]); // UserDefinedField11
		obj[220] = CommonUtility.singleQuoteCheck(obj[220]); // UserDefinedField12
		obj[221] = CommonUtility.singleQuoteCheck(obj[221]); // UserDefinedField13
		obj[222] = CommonUtility.singleQuoteCheck(obj[222]); // UserDefinedField14
		obj[223] = CommonUtility.singleQuoteCheck(obj[223]); // UserDefinedField15
		obj[224] = CommonUtility.singleQuoteCheck(obj[224]); // UserDefinedField16
		obj[225] = CommonUtility.singleQuoteCheck(obj[225]); // UserDefinedField17
		obj[226] = CommonUtility.singleQuoteCheck(obj[226]); // UserDefinedField18
		obj[227] = CommonUtility.singleQuoteCheck(obj[227]); // UserDefinedField19
		obj[228] = CommonUtility.singleQuoteCheck(obj[228]); // UserDefinedField20
		obj[229] = CommonUtility.singleQuoteCheck(obj[229]); // UserDefinedField21
		obj[230] = CommonUtility.singleQuoteCheck(obj[230]); // UserDefinedField22
		obj[231] = CommonUtility.singleQuoteCheck(obj[231]); // UserDefinedField23
		obj[232] = CommonUtility.singleQuoteCheck(obj[232]); // UserDefinedField24
		obj[233] = CommonUtility.singleQuoteCheck(obj[233]); // UserDefinedField25
		obj[234] = CommonUtility.singleQuoteCheck(obj[234]); // UserDefinedField26
		obj[235] = CommonUtility.singleQuoteCheck(obj[235]); // UserDefinedField27
		obj[236] = CommonUtility.singleQuoteCheck(obj[236]); // UserDefinedField28
		obj[237] = CommonUtility.singleQuoteCheck(obj[237]); // UserDefinedField29
		obj[238] = CommonUtility.singleQuoteCheck(obj[238]); // UserDefinedField30
	}
}