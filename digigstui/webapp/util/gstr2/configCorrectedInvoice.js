sap.ui.define(function () {
	"use strict";
	return {
		getCorrectedInvoiceData: function () {
			var createInvoiceData = {
				"req": [{
					"gstr1SubCategory": "",
				"gstr1TableNo": "",
				"docNo": "",
				"docDate": "1111-11-11",
				"docType": "",
				"fiYear": "",
				"plantCode": "",
				"supplierGstin": "",
				"custGstin": "",
				"ecomCustGSTIN": "",
				"reverseCharge": "Y",
				"pos": "",
				"custOrSuppCode": "",
				"custOrSuppName": "",
				"sourceIdentifier": "",
				"sourceFileName": "",
				"originalDocNo": "",
				"originalDocDate": "1111-11-11",
				"isError": "",
				"isInfo": "",
				"docAmount": "",
				"isProcessed": "",
				"accountVoucherNo": "",
				"accountVoucherDate": "",
				"division": "",
				"subDivision": "",
				"profitCentre1": "",
				"profitCentre2": "",
				"crDrReason": "",
				"crDrPreGst": "",
				"userDefinedField1": "",
				"userDefinedField2": "",
				"userDefinedField3": "",
				"returnPeriod": "",
				"isDeleted": "",
				"origSupplierGstin": "",
				"billOfEntryNo": "",
				"billOfEntryDate": "",
				"cifValue": "",
				"customDuty": "",
					"lineItems": []
				}]
			};
			return createInvoiceData;
		}
	};
});