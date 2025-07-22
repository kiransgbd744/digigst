package com.ey.advisory.app.report.convertor;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesTableDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.ReportConvertor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1EInvReportConvertor")
public class Gstr1EInvReportConvertor implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr, String reportType) {

		GSTR1GetEInvoicesTableDto obj = new GSTR1GetEInvoicesTableDto();

		obj.setIrnNum(arr[2] != null ? arr[2].toString() : null);
		obj.setIrnGenDate(arr[3] != null ? arr[3].toString() : null);
		obj.seteInvStatus(arr[4] != null ? arr[4].toString() : null);
		obj.setAutoDraftStatus(arr[5] != null ? arr[5].toString() : null);
		obj.setAutoDraftDate(arr[6] != null ? arr[6].toString() : null);
		obj.setErrorCode(arr[7] != null ? arr[7].toString() : null);
		obj.setErrorMsg(arr[8] != null ? arr[8].toString() : null);
		obj.setReturnPeriod(arr[9] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		// obj.setReturnPeriod(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setCustomerGSTIN(arr[11] != null ? arr[11].toString() : null);
		obj.setCustTradeName(arr[12] != null ? arr[12].toString() : null);
		obj.setDocumentType(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplyType(arr[14] != null ? arr[14].toString() : null);
		obj.setDocumentNo(arr[15] != null ? arr[15].toString() : null);
		obj.setDocumentDate(arr[16] != null ? arr[16].toString() : null);
		obj.setPos(
				arr[17] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[17].toString()) : null);
		// obj.setPos(arr[17] != null ? arr[17].toString() : null);
		obj.setPortCode(arr[18] != null ? arr[18].toString() : null);
		obj.setShippingbillNumber(arr[19] != null ? arr[19].toString() : null);
		obj.setShippingbillDate(arr[20] != null ? arr[20].toString() : null);
		obj.setReverseCharge(arr[21] != null ? arr[21].toString() : null);
		obj.setEcomGSTIN(arr[22] != null ? arr[22].toString() : null);
		obj.setItemSerialNumber(arr[23] != null ? arr[23].toString() : null);

		/*
		 * BigDecimal bigDecimaItem = (BigDecimal) arr[22]; if (bigDecimaItem !=
		 * null) { obj.setItemAssessAmount( bigDecimaItem.setScale(2,
		 * BigDecimal.ROUND_HALF_UP));
		 * 
		 * }
		 */
		obj.setTaxRate(arr[24] != null ? arr[24].toString() : null);
		obj.setItemAssessAmount(arr[25] != null ? arr[25].toString() : null);
		obj.setIgstAmount(arr[26] != null ? arr[26].toString() : null);
		obj.setCgstAmount(arr[27] != null ? arr[27].toString() : null);
		obj.setSgstAmount(arr[28] != null ? arr[28].toString() : null);
		obj.setCessAmount(arr[29] != null ? arr[29].toString() : null);
		obj.setInvoiceValue(arr[30] != null ? arr[30].toString() : null);

		obj.setIrnSourceType(arr[31] != null ? arr[31].toString() : null);
		obj.setTableType(arr[32] != null ? arr[32].toString() : null);
		return obj;
	}
}
