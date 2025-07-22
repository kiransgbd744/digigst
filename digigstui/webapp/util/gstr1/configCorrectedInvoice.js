sap.ui.define(function () {
	"use strict";
	return {
		getCorrectedInvoiceData: function () {
			var createInvoiceData = {
				"req": [{
					"supplierGstin": "",
					"fiYear": "",
					"returnPeriod": "",
					"docType": "",
					"docNo": "",
					"docDate": "1111-11-11",
					"docAmount": 0,
					"originalDocNo": "",
					"originalDocDate": "1111-11-11",
					"uinOrComposition": "",
					"custGstin": "",
					"custName": "",
					"shipToState": "",
					"billToState": "",
					"pos": "",
					"shipPortCode": "",
					"reverseCharge": "Y",
					"ecomCustGSTIN": "",
					"dataOriginTypeCode": "",
					"tcsFlag": false,
					"itcFlag": false,
					"customerCode": "",
					"fob": null,
					"shippingBillNo": "",
					"shippingBillDate": null,
					"glAccountCode": "",
					"accountVoucherNo": "",
					"accountVoucherDate": null,
					"crDrReason": "",
					"crDrPreGst": false,
					//"isSubmitted":false,
					"lineItems": []
				}]
			};
			return createInvoiceData;
		}
	};
});