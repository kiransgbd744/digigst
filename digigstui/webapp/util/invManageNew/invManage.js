sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"com/ey/digigst/util/Formatter"
], function (MobileLibrary, MessageBox, MessageToast, JSONModel, Fragment, Formatter) {
	"use strict";

	return {

		allTF: function (flag) {
			return {
				"IRN": flag,
				"IRNDate": flag,
				"DSC": flag,
				"TaxScheme": flag,
				"Category": flag,
				"SupplyType": flag,
				"DocCategory": flag,
				"DocumentType": flag,
				"DocumentNumber": flag,
				"DocumentDate": flag,
				"ReverseChargeFlag": flag,
				"SupplierGSTIN": flag,
				"SupplierTradeName": flag,
				"SupplierLegalName": flag,
				"SupplierBuildingNumber": flag,
				"SupplierBuildingName": flag,
				"SupplierFloorNumber": flag,
				"SupplierLocation": flag,
				"SupplierDistrict": flag,
				"SupplierPincode": flag,
				"SupplierStateCode": flag,
				"SupplierPhone": flag,
				"SupplierEmail": flag,
				"CustomerGSTIN": flag,
				"CustomerTradeName": flag,
				"CustomerLegalName": flag,
				"CustomerBuildingNumber": flag,
				"CustomerBuildingName": flag,
				"CustomerFloorNumber": flag,
				"CustomerLocation": flag,
				"CustomerDistrict": flag,
				"CustomerPincode": flag,
				"CustomerStateCode": flag,
				"BillingPOS": flag,
				"CustomerPhone": flag,
				"CustomerEmail": flag,
				"DispatcherGSTIN": flag,
				"DispatcherTradeName": flag,
				"DispatcherBuildingNumber": flag,
				"DispatcherBuildingName": flag,
				"DispatcherFloorNumber": flag,
				"DispatcherLocation": flag,
				"DispatcherDistrict": flag,
				"DispatcherPincode": flag,
				"DispatcherStateCode": flag,
				"DispatcherPhone": flag,
				"DispatcherEmail": flag,
				"ShipToGSTIN": flag,
				"ShipToTradeName": flag,
				"ShipToLegalName": flag,
				"ShipToBuildingNumber": flag,
				"ShipToBuildingName": flag,
				"ShipToFloorNumber": flag,
				"ShipToLocation": flag,
				"ShipToDistrict": flag,
				"ShipToPincode": flag,
				"ShipToStateCode": flag,
				"ShipToPhone": flag,
				"ShipToEmail": flag,
				"ItemSerialNumber": flag,
				"SerialNumberII": flag,
				"OtherDetail1": flag,
				"OtherDetail2": flag,
				"ProductName": flag,
				"ProductDescription": flag,
				"IsService": flag,
				"HSN": flag,
				"Barcode": flag,
				"BatchNameOrNumber": flag,
				"BatchExpiryDate": flag,
				"WarrantyDate": flag,
				"OriginCountry": flag,
				"UnitOfMeasurement": flag,
				"Quantity": flag,
				"FreeQuantity": flag,
				"UnitPrice": flag,
				"ItemAmount": flag,
				"ItemDiscount": flag,
				"ItemOtherCharges": flag,
				"ItemAssessableAmount": flag,
				"PreTaxAmount": flag,
				"InvoiceLineNetAmount": flag,
				"IGSTRate": flag,
				"IGSTAmount": flag,
				"CGSTRate": flag,
				"CGSTAmount": flag,
				"SGSTRate": flag,
				"SGSTAmount": flag,
				"CessAdvaloremRate": flag,
				"CessAdvaloremAmount": flag,
				"CessSpecificRate": flag,
				"CessSpecificAmount": flag,
				"StateCessRate": flag,
				"StateCessAmount": flag,
				"TotalItemAmount": flag,
				"ItemTotal": flag,
				"PreTaxParticulars": flag,
				"TaxOn": flag,
				"Amount": flag,
				"InvoiceDiscount": flag,
				"InvoiceOtherCharges": flag,
				"InvoiceAllowancesOrCharges": flag,
				"SumOfInvoiceLineNetAmount": flag,
				"SumOfAllowancesOnDocumentLevel": flag,
				"SumOfChargesOnDocumentLevel": flag,
				"FreightAmount": flag,
				"InsuranceAmount": flag,
				"PackagingAndForwardingCharges": flag,
				"InvoiceAssessableAmount": flag,
				"Rate": flag,
				"InvoiceIGSTAmount": flag,
				"InvoiceCGSTAmount": flag,
				"InvoiceSGSTAmount": flag,
				"InvoiceCessAdvaloremAmount": flag,
				"InvoiceCessSpecificAmount": flag,
				"InvoiceStateCessAmount": flag,
				"TaxTotal": flag,
				"InvoiceValue": flag,
				"RoundOff": flag,
				"TotalInvoiceValue": flag,
				"ForeignCurrency": flag,
				"CountryCode": flag,
				"InvoiceValueFC": flag,
				"PortCode": flag,
				"ShippingBillNumber": flag,
				"ShippingBillDate": flag,
				"InvoiceRemarks": flag,
				"InvoicePeriodStartDate": flag,
				"InvoicePeriodEndDate": flag,
				"OriginalDocumentNumber": flag,
				"OriginalDocumentDate": flag,
				"OriginalInvoiceNumber": flag,
				"OriginalInvoiceDate": flag,
				"InvoiceReference": flag,
				"PreceedingInvoiceNumber": flag,
				"PreceedingInvoiceDate": flag,
				"ReceiptAdviceReference": flag,
				"TenderReference": flag,
				"ContractReference": flag,
				"ExternalReference": flag,
				"ProjectReference": flag,
				"CustomerPOReferenceNumber": flag,
				"CustomerPOReferenceDate": flag,
				"OrderLineReference": flag,
				"PayeeName": flag,
				"ModeOfPayment": flag,
				"BranchOrIFSCCode": flag,
				"PaymentTerms": flag,
				"PaymentInstruction": flag,
				"CreditTransfer": flag,
				"DirectDebit": flag,
				"CreditDays": flag,
				"PaidAmount": flag,
				"BalanceAmount": flag,
				"PaymentDueDate": flag,
				"AccountDetail": flag,
				"EcomTransaction": flag,
				"EcomGSTIN": flag,
				"EcomPOS": flag,
				"SupportingDocURL": flag,
				"SupportingDocBase64": flag,
				"TDSFlag": flag,
				"TransactionType": flag,
				"SubSupplyType": flag,
				"OtherSupplyTypeDescription": flag,
				"TransporterID": flag,
				"TransporterName": flag,
				"TransportMode": flag,
				"TransportDocNo": flag,
				"TransportDocDate": flag,
				"Distance": flag,
				"VehicleNo": flag,
				"VehicleType": flag,
				"ReturnPeriod": flag,
				"OriginalDocumentType": flag,
				"OriginalCustomerGSTIN": flag,
				"DifferentialPercentageFlag": flag,
				"Section7OfIGSTFlag": flag,
				"ClaimRefundFlag": flag,
				"AutoPopulateToRefund": flag,
				"CRDRPreGST": flag,
				"TCSFlag": flag,
				"CustomerType": flag,
				"CustomerCode": flag,
				"ProductCode": flag,
				"CategoryOfProduct": flag,
				"ITCFlag": flag,
				"StateApplyingCess": flag,
				"FOB": flag,
				"ExportDuty": flag,
				"ExchangeRate": flag,
				"ReasonForCreditDebitNote": flag,
				"TCSIGSTAmount": flag,
				"TCSCGSTAmount": flag,
				"TCSSGSTAmount": flag,
				"TDSIGSTAmount": flag,
				"TDSCGSTAmount": flag,
				"TDSSGSTAmount": flag,
				"UserID": flag,
				"CompanyCode": flag,
				"SourceIdentifier": flag,
				"SourceFileName": flag,
				"ProfitCentre1": flag,
				"ProfitCentre2": flag,
				"PlantCode": flag,
				"Division": flag,
				"SubDivision": flag,
				"Location": flag,
				"SalesOrganisation": flag,
				"DistributionChannel": flag,
				"ProfitCentre3": flag,
				"ProfitCentre4": flag,
				"ProfitCentre5": flag,
				"ProfitCentre6": flag,
				"ProfitCentre7": flag,
				"ProfitCentre8": flag,
				"GLAssessableValue": flag,
				"GLIGST": flag,
				"GLCGST": flag,
				"GLSGST": flag,
				"GLAdvaloremCess": flag,
				"GLSpecificCess": flag,
				"GLStateCess": flag,
				"GLPostingDate": flag,
				"SalesOrderNumber": flag,
				"EWBNumber": flag,
				"EWBDate": flag,
				"AccountingVoucherNumber": flag,
				"AccountingVoucherDate": flag,
				"UserDefinedField1": flag,
				"UserDefinedField2": flag,
				"UserDefinedField3": flag,
				"UserDefinedField4": flag,
				"UserDefinedField5": flag,
				"UserDefinedField6": flag,
				"UserDefinedField7": flag,
				"UserDefinedField8": flag,
				"UserDefinedField9": flag,
				"UserDefinedField10": flag,
				"UserDefinedField11": flag,
				"UserDefinedField12": flag,
				"UserDefinedField13": flag,
				"UserDefinedField14": flag,
				"UserDefinedField15": flag,
				"UserDefinedField16": flag,
				"UserDefinedField17": flag,
				"UserDefinedField18": flag,
				"UserDefinedField19": flag,
				"UserDefinedField20": flag,
				"UserDefinedField21": flag,
				"UserDefinedField22": flag,
				"UserDefinedField23": flag,
				"UserDefinedField24": flag,
				"UserDefinedField25": flag,
				"UserDefinedField26": flag,
				"UserDefinedField27": flag,
				"UserDefinedField28": flag,
				"UserDefinedField29": flag,
				"UserDefinedField30": flag,
				"CustomerTAN": flag,
				"EcomTransactionID": flag,
				"EligibilityIndicator": flag,
				"CommonSupplyIndicator": flag,
				"AvailableIGST": flag,
				"AvailableCGST": flag,
				"AvailableSGST": flag,
				"AvailableCess": flag,
				"ITCEntitlement": flag,
				"ITCReversalIdentifier": flag
			};
		},

		selectedGroup: function (oDisplay, aGroupData) {

			// for (var i = 0; i < aGroupData.length; i++) {
				var vSelGroup = aGroupData;
				switch (vSelGroup) {
				case "All":
					oDisplay = this.allTF(true);
					return oDisplay;
					break;
				case "EInv":
					// code block
					oDisplay.IRN = true;
					oDisplay.DSC = true;
					oDisplay.TaxScheme = true;
					oDisplay.Category = true;
					oDisplay.SupplyType = true;
					oDisplay.DocCategory = true;
					oDisplay.DocumentType = true;
					oDisplay.DocumentNumber = true;
					oDisplay.DocumentDate = true;
					oDisplay.ReverseChargeFlag = true;
					oDisplay.SupplierGSTIN = true;
					oDisplay.SupplierTradeName = true;
					oDisplay.SupplierLegalName = true;
					oDisplay.SupplierBuildingNumber = true;
					oDisplay.SupplierBuildingName = true;
					oDisplay.SupplierFloorNumber = true;
					oDisplay.SupplierLocation = true;
					oDisplay.SupplierDistrict = true;
					oDisplay.SupplierPincode = true;
					oDisplay.SupplierStateCode = true;
					oDisplay.SupplierPhone = true;
					oDisplay.SupplierEmail = true;
					oDisplay.CustomerGSTIN = true;
					oDisplay.CustomerTradeName = true;
					oDisplay.CustomerLegalName = true;
					oDisplay.CustomerBuildingNumber = true;
					oDisplay.CustomerBuildingName = true;
					oDisplay.CustomerFloorNumber = true;
					oDisplay.CustomerLocation = true;
					oDisplay.CustomerDistrict = true;
					oDisplay.CustomerPincode = true;
					oDisplay.CustomerStateCode = true;
					oDisplay.BillingPOS = true;
					oDisplay.CustomerPhone = true;
					oDisplay.CustomerEmail = true;
					oDisplay.DispatcherGSTIN = true;
					oDisplay.DispatcherTradeName = true;
					oDisplay.DispatcherBuildingNumber = true;
					oDisplay.DispatcherBuildingName = true;
					oDisplay.DispatcherFloorNumber = true;
					oDisplay.DispatcherLocation = true;
					oDisplay.DispatcherDistrict = true;
					oDisplay.DispatcherPincode = true;
					oDisplay.DispatcherStateCode = true;
					oDisplay.DispatcherPhone = true;
					oDisplay.DispatcherEmail = true;
					oDisplay.ShipToGSTIN = true;
					oDisplay.ShipToTradeName = true;
					oDisplay.ShipToLegalName = true;
					oDisplay.ShipToBuildingNumber = true;
					oDisplay.ShipToBuildingName = true;
					oDisplay.ShipToFloorNumber = true;
					oDisplay.ShipToLocation = true;
					oDisplay.ShipToDistrict = true;
					oDisplay.ShipToPincode = true;
					oDisplay.ShipToStateCode = true;
					oDisplay.ShipToPhone = true;
					oDisplay.ShipToEmail = true;
					oDisplay.ItemSerialNumber = true;
					oDisplay.SerialNumberII = true;
					oDisplay.OtherDetail1 = true;
					oDisplay.OtherDetail2 = true;
					oDisplay.ProductName = true;
					oDisplay.ProductDescription = true;
					oDisplay.IsService = true;
					oDisplay.HSN = true;
					oDisplay.Barcode = true;
					oDisplay.BatchNameOrNumber = true;
					oDisplay.BatchExpiryDate = true;
					oDisplay.WarrantyDate = true;
					oDisplay.OriginCountry = true;
					oDisplay.UnitOfMeasurement = true;
					oDisplay.Quantity = true;
					oDisplay.FreeQuantity = true;
					oDisplay.UnitPrice = true;
					oDisplay.ItemAmount = true;
					oDisplay.ItemDiscount = true;
					oDisplay.ItemOtherCharges = true;
					oDisplay.ItemAssessableAmount = true;
					oDisplay.PreTaxAmount = true;
					oDisplay.InvoiceLineNetAmount = true;
					oDisplay.IGSTRate = true;
					oDisplay.IGSTAmount = true;
					oDisplay.CGSTRate = true;
					oDisplay.CGSTAmount = true;
					oDisplay.SGSTRate = true;
					oDisplay.SGSTAmount = true;
					oDisplay.CessAdvaloremRate = true;
					oDisplay.CessAdvaloremAmount = true;
					oDisplay.CessSpecificAmount = true;
					oDisplay.StateCessRate = true;
					oDisplay.StateCessAmount = true;
					oDisplay.TotalItemAmount = true;
					oDisplay.ItemTotal = true;
					oDisplay.PreTaxParticulars = true;
					oDisplay.TaxOn = true;
					oDisplay.Amount = true;
					oDisplay.InvoiceDiscount = true;
					oDisplay.InvoiceOtherCharges = true;
					oDisplay.InvoiceAllowancesOrCharges = true;
					oDisplay.SumOfInvoiceLineNetAmount = true;
					oDisplay.SumOfAllowancesOnDocumentLevel = true;
					oDisplay.SumOfChargesOnDocumentLevel = true;
					oDisplay.FreightAmount = true;
					oDisplay.InsuranceAmount = true;
					oDisplay.PackagingAndForwardingCharges = true;
					oDisplay.InvoiceAssessableAmount = true;
					oDisplay.Rate = true;
					oDisplay.InvoiceIGSTAmount = true;
					oDisplay.InvoiceCGSTAmount = true;
					oDisplay.InvoiceSGSTAmount = true;
					oDisplay.InvoiceCessAdvaloremAmount = true;
					oDisplay.InvoiceCessSpecificAmount = true;
					oDisplay.InvoiceStateCessAmount = true;
					oDisplay.TaxTotal = true;
					oDisplay.InvoiceValue = true;
					oDisplay.RoundOff = true;
					oDisplay.ForeignCurrency = true;
					oDisplay.CountryCode = true;
					oDisplay.InvoiceValueFC = true;
					oDisplay.PortCode = true;
					oDisplay.ShippingBillNumber = true;
					oDisplay.ShippingBillDate = true;
					oDisplay.InvoiceRemarks = true;
					oDisplay.InvoicePeriodStartDate = true;
					oDisplay.InvoicePeriodEndDate = true;
					oDisplay.OriginalDocumentNumber = true;
					oDisplay.InvoiceReference = true;
					oDisplay.PreceedingInvoiceNumber = true;
					oDisplay.PreceedingInvoiceDate = true;
					oDisplay.ReceiptAdviceReference = true;
					oDisplay.TenderReference = true;
					oDisplay.ContractReference = true;
					oDisplay.ExternalReference = true;
					oDisplay.ProjectReference = true;
					oDisplay.CustomerPOReferenceNumber = true;
					oDisplay.CustomerPOReferenceDate = true;
					oDisplay.OrderLineReference = true;
					oDisplay.PayeeName = true;
					oDisplay.ModeOfPayment = true;
					oDisplay.BranchOrIFSCCode = true;
					oDisplay.PaymentTerms = true;
					oDisplay.PaymentInstruction = true;
					oDisplay.CreditTransfer = true;
					oDisplay.DirectDebit = true;
					oDisplay.CreditDays = true;
					oDisplay.PaidAmount = true;
					oDisplay.BalanceAmount = true;
					oDisplay.PaymentDueDate = true;
					oDisplay.AccountDetail = true;
					oDisplay.EcomTransaction = true;
					oDisplay.EcomGSTIN = true;
					oDisplay.EcomPOS = true;
					oDisplay.SupportingDocURL = true;
					oDisplay.SupportingDocBase64 = true;
					oDisplay.SubSupplyType = true;
					oDisplay.TransporterID = true;
					oDisplay.TransporterName = true;
					oDisplay.TransportMode = true;
					oDisplay.TransportDocNo = true;
					oDisplay.TransportDocDate = true;
					oDisplay.Distance = true;
					oDisplay.VehicleNo = true;

					break;
				case "EWB":
					oDisplay.DocCategory = true;
					oDisplay.DocumentType = true;
					oDisplay.DocumentNumber = true;
					oDisplay.DocumentDate = true;
					oDisplay.SupplierGSTIN = true;
					oDisplay.SupplierTradeName = true;
					oDisplay.SupplierBuildingNumber = true;
					oDisplay.SupplierBuildingName = true;
					oDisplay.SupplierLocation = true;
					oDisplay.SupplierPincode = true;
					oDisplay.SupplierStateCode = true;
					oDisplay.CustomerGSTIN = true;
					oDisplay.CustomerTradeName = true;
					oDisplay.CustomerStateCode = true;
					oDisplay.DispatcherGSTIN = true;
					oDisplay.DispatcherTradeName = true;
					oDisplay.DispatcherBuildingNumber = true;
					oDisplay.DispatcherBuildingName = true;
					oDisplay.DispatcherLocation = true;
					oDisplay.DispatcherPincode = true;
					oDisplay.DispatcherStateCode = true;
					oDisplay.ShipToGSTIN = true;
					oDisplay.ShipToTradeName = true;
					oDisplay.ShipToBuildingNumber = true;
					oDisplay.ShipToBuildingName = true;
					oDisplay.ShipToLocation = true;
					oDisplay.ShipToPincode = true;
					oDisplay.ShipToStateCode = true;
					oDisplay.ItemSerialNumber = true;
					oDisplay.ProductName = true;
					oDisplay.ProductDescription = true;
					oDisplay.HSN = true;
					oDisplay.UnitOfMeasurement = true;
					oDisplay.Quantity = true;
					oDisplay.ItemOtherCharges = true;
					oDisplay.ItemAssessableAmount = true;
					oDisplay.IGSTRate = true;
					oDisplay.IGSTAmount = true;
					oDisplay.CGSTRate = true;
					oDisplay.CGSTAmount = true;
					oDisplay.SGSTRate = true;
					oDisplay.SGSTAmount = true;
					oDisplay.CessAdvaloremRate = true;
					oDisplay.CessAdvaloremAmount = true;
					oDisplay.CessSpecificRate = true;
					oDisplay.CessSpecificAmount = true;
					oDisplay.InvoiceValue = true;
					oDisplay.TransactionType = true;
					oDisplay.SubSupplyType = true;
					oDisplay.OtherSupplyTypeDescription = true;
					oDisplay.TransporterID = true;
					oDisplay.TransporterName = true;
					oDisplay.TransportMode = true;
					oDisplay.TransportDocNo = true;
					oDisplay.TransportDocDate = true;
					oDisplay.Distance = true;
					oDisplay.VehicleNo = true;
					oDisplay.VehicleType = true;

					break;
				case "GSTR":
					oDisplay.SupplyType = true;
					oDisplay.DocumentType = true;
					oDisplay.DocumentNumber = true;
					oDisplay.DocumentDate = true;
					oDisplay.ReverseChargeFlag = true;
					oDisplay.SupplierGSTIN = true;
					oDisplay.CustomerGSTIN = true;
					oDisplay.BillingPOS = true;
					oDisplay.HSN = true;
					oDisplay.UnitOfMeasurement = true;
					oDisplay.Quantity = true;
					oDisplay.ItemAssessableAmount = true;
					oDisplay.IGSTRate = true;
					oDisplay.IGSTAmount = true;
					oDisplay.CGSTRate = true;
					oDisplay.CGSTAmount = true;
					oDisplay.SGSTRate = true;
					oDisplay.SGSTAmount = true;
					oDisplay.CessAdvaloremAmount = true;
					oDisplay.CessSpecificAmount = true;
					oDisplay.InvoiceValue = true;
					oDisplay.PortCode = true;
					oDisplay.ShippingBillNumber = true;
					oDisplay.ShippingBillDate = true;
					oDisplay.OriginalDocumentNumber = true;
					oDisplay.OriginalDocumentDate = true;
					oDisplay.OriginalInvoiceNumber = true;
					oDisplay.OriginalInvoiceDate = true;
					oDisplay.EcomGSTIN = true;
					oDisplay.ReturnPeriod = true;
					oDisplay.OriginalDocumentType = true;
					oDisplay.OriginalCustomerGSTIN = true;
					oDisplay.DifferentialPercentageFlag = true;
					oDisplay.Section7OfIGSTFlag = true;
					oDisplay.ClaimRefundFlag = true;
					oDisplay.AutoPopulateToRefund = true;
					oDisplay.CRDRPreGST = true;
					oDisplay.TCSFlag = true;

					break;
				case "DigiGST":

					oDisplay.IRNDate = true;
					oDisplay.CessSpecificRate = true;
					oDisplay.TotalInvoiceValue = true;
					oDisplay.TDSFlag = true;
					oDisplay.CustomerType = true;
					oDisplay.CustomerCode = true;
					oDisplay.ProductCode = true;
					oDisplay.CategoryOfProduct = true;
					oDisplay.ITCFlag = true;
					oDisplay.StateApplyingCess = true;
					oDisplay.FOB = true;
					oDisplay.ExportDuty = true;
					oDisplay.ExchangeRate = true;
					oDisplay.ReasonForCreditDebitNote = true;
					oDisplay.TCSIGSTAmount = true;
					oDisplay.TCSCGSTAmount = true;
					oDisplay.TCSSGSTAmount = true;
					oDisplay.TDSIGSTAmount = true;
					oDisplay.TDSCGSTAmount = true;
					oDisplay.TDSSGSTAmount = true;
					oDisplay.UserID = true;
					oDisplay.CompanyCode = true;
					oDisplay.SourceIdentifier = true;
					oDisplay.SourceFileName = true;
					oDisplay.ProfitCentre1 = true;
					oDisplay.ProfitCentre2 = true;
					oDisplay.PlantCode = true;
					oDisplay.Division = true;
					oDisplay.SubDivision = true;
					oDisplay.Location = true;
					oDisplay.SalesOrganisation = true;
					oDisplay.DistributionChannel = true;
					oDisplay.ProfitCentre3 = true;
					oDisplay.ProfitCentre4 = true;
					oDisplay.ProfitCentre5 = true;
					oDisplay.ProfitCentre6 = true;
					oDisplay.ProfitCentre7 = true;
					oDisplay.ProfitCentre8 = true;
					oDisplay.GLAssessableValue = true;
					oDisplay.GLIGST = true;
					oDisplay.GLCGST = true;
					oDisplay.GLSGST = true;
					oDisplay.GLAdvaloremCess = true;
					oDisplay.GLSpecificCess = true;
					oDisplay.GLStateCess = true;
					oDisplay.GLPostingDate = true;
					oDisplay.SalesOrderNumber = true;
					oDisplay.EWBNumber = true;
					oDisplay.EWBDate = true;
					oDisplay.AccountingVoucherNumber = true;
					oDisplay.AccountingVoucherDate = true;
					oDisplay.CustomerTAN = true;
					oDisplay.EcomTransactionID = true;
					oDisplay.EligibilityIndicator = true;
					oDisplay.CommonSupplyIndicator = true;
					oDisplay.AvailableIGST = true;
					oDisplay.AvailableCGST = true;
					oDisplay.AvailableSGST = true;
					oDisplay.AvailableCess = true;
					oDisplay.ITCEntitlement = true;
					oDisplay.ITCReversalIdentifier = true;

					break;
				case "UD":
					oDisplay.UserDefinedField1 = true;
					oDisplay.UserDefinedField2 = true;
					oDisplay.UserDefinedField3 = true;
					oDisplay.UserDefinedField4 = true;
					oDisplay.UserDefinedField5 = true;
					oDisplay.UserDefinedField6 = true;
					oDisplay.UserDefinedField7 = true;
					oDisplay.UserDefinedField8 = true;
					oDisplay.UserDefinedField9 = true;
					oDisplay.UserDefinedField10 = true;
					oDisplay.UserDefinedField11 = true;
					oDisplay.UserDefinedField12 = true;
					oDisplay.UserDefinedField13 = true;
					oDisplay.UserDefinedField14 = true;
					oDisplay.UserDefinedField15 = true;
					oDisplay.UserDefinedField16 = true;
					oDisplay.UserDefinedField17 = true;
					oDisplay.UserDefinedField18 = true;
					oDisplay.UserDefinedField19 = true;
					oDisplay.UserDefinedField20 = true;
					oDisplay.UserDefinedField21 = true;
					oDisplay.UserDefinedField22 = true;
					oDisplay.UserDefinedField23 = true;
					oDisplay.UserDefinedField24 = true;
					oDisplay.UserDefinedField25 = true;
					oDisplay.UserDefinedField26 = true;
					oDisplay.UserDefinedField27 = true;
					oDisplay.UserDefinedField28 = true;
					oDisplay.UserDefinedField29 = true;
					oDisplay.UserDefinedField30 = true;

					break;
				}
			// }
			return oDisplay;
		},

		selectedFields: function (oDisplay, aGroupData) {

			for (var i = 0; i < aGroupData.length; i++) {
				var vSelGroup = aGroupData[i];
				switch (vSelGroup) {
				case "All":
					oDisplay = this.allTF(true);
					return oDisplay;
					break;
				case "IRN":
					oDisplay.IRN = true;
					break;
				case "IRNDate":
					oDisplay.IRNDate = true;
					break;
				case "DSC":
					oDisplay.DSC = true;
					break;
				case "TaxScheme":
					oDisplay.TaxScheme = true;
					break;
				case "Category":
					oDisplay.Category = true;
					break;
				case "SupplyType":
					oDisplay.SupplyType = true;
					break;
				case "DocCategory":
					oDisplay.DocCategory = true;
					break;
				case "DocumentType":
					oDisplay.DocumentType = true;
					break;
				case "DocumentNumber":
					oDisplay.DocumentNumber = true;
					break;
				case "DocumentDate":
					oDisplay.DocumentDate = true;
					break;
				case "ReverseChargeFlag":
					oDisplay.ReverseChargeFlag = true;
					break;
				case "SupplierGSTIN":
					oDisplay.SupplierGSTIN = true;
					break;
				case "SupplierTradeName":
					oDisplay.SupplierTradeName = true;
					break;
				case "SupplierLegalName":
					oDisplay.SupplierLegalName = true;
					break;
				case "SupplierBuildingNumber":
					oDisplay.SupplierBuildingNumber = true;
					break;
				case "SupplierBuildingName":
					oDisplay.SupplierBuildingName = true;
					break;
				case "SupplierFloorNumber":
					oDisplay.SupplierFloorNumber = true;
					break;
				case "SupplierLocation":
					oDisplay.SupplierLocation = true;
					break;
				case "SupplierDistrict":
					oDisplay.SupplierDistrict = true;
					break;
				case "SupplierPincode":
					oDisplay.SupplierPincode = true;
					break;
				case "SupplierStateCode":
					oDisplay.SupplierStateCode = true;
					break;
				case "SupplierPhone":
					oDisplay.SupplierPhone = true;
					break;
				case "SupplierEmail":
					oDisplay.SupplierEmail = true;
					break;
				case "CustomerGSTIN":
					oDisplay.CustomerGSTIN = true;
					break;
				case "CustomerTradeName":
					oDisplay.CustomerTradeName = true;
					break;
				case "CustomerLegalName":
					oDisplay.CustomerLegalName = true;
					break;
				case "CustomerBuildingNumber":
					oDisplay.CustomerBuildingNumber = true;
					break;
				case "CustomerBuildingName":
					oDisplay.CustomerBuildingName = true;
					break;
				case "CustomerFloorNumber":
					oDisplay.CustomerFloorNumber = true;
					break;
				case "CustomerLocation":
					oDisplay.CustomerLocation = true;
					break;
				case "CustomerDistrict":
					oDisplay.CustomerDistrict = true;
					break;
				case "CustomerPincode":
					oDisplay.CustomerPincode = true;
					break;
				case "CustomerStateCode":
					oDisplay.CustomerStateCode = true;
					break;
				case "BillingPOS":
					oDisplay.BillingPOS = true;
					break;
				case "CustomerPhone":
					oDisplay.CustomerPhone = true;
					break;
				case "CustomerEmail":
					oDisplay.CustomerEmail = true;
					break;
				case "DispatcherGSTIN":
					oDisplay.DispatcherGSTIN = true;
					break;
				case "DispatcherTradeName":
					oDisplay.DispatcherTradeName = true;
					break;
				case "DispatcherBuildingNumber":
					oDisplay.DispatcherBuildingNumber = true;
					break;
				case "DispatcherBuildingName":
					oDisplay.DispatcherBuildingName = true;
					break;
				case "DispatcherFloorNumber":
					oDisplay.DispatcherFloorNumber = true;
					break;
				case "DispatcherLocation":
					oDisplay.DispatcherLocation = true;
					break;
				case "DispatcherDistrict":
					oDisplay.DispatcherDistrict = true;
					break;
				case "DispatcherPincode":
					oDisplay.DispatcherPincode = true;
					break;
				case "DispatcherStateCode":
					oDisplay.DispatcherStateCode = true;
					break;
				case "DispatcherPhone":
					oDisplay.DispatcherPhone = true;
					break;
				case "DispatcherEmail":
					oDisplay.DispatcherEmail = true;
					break;
				case "ShipToGSTIN":
					oDisplay.ShipToGSTIN = true;
					break;
				case "ShipToTradeName":
					oDisplay.ShipToTradeName = true;
					break;
				case "ShipToLegalName":
					oDisplay.ShipToLegalName = true;
					break;
				case "ShipToBuildingNumber":
					oDisplay.ShipToBuildingNumber = true;
					break;
				case "ShipToBuildingName":
					oDisplay.ShipToBuildingName = true;
					break;
				case "ShipToFloorNumber":
					oDisplay.ShipToFloorNumber = true;
					break;
				case "ShipToLocation":
					oDisplay.ShipToLocation = true;
					break;
				case "ShipToDistrict":
					oDisplay.ShipToDistrict = true;
					break;
				case "ShipToPincode":
					oDisplay.ShipToPincode = true;
					break;
				case "ShipToStateCode":
					oDisplay.ShipToStateCode = true;
					break;
				case "ShipToPhone":
					oDisplay.ShipToPhone = true;
					break;
				case "ShipToEmail":
					oDisplay.ShipToEmail = true;
					break;
				case "ItemSerialNumber":
					oDisplay.ItemSerialNumber = true;
					break;
				case "SerialNumberII":
					oDisplay.SerialNumberII = true;
					break;
				case "OtherDetail1":
					oDisplay.OtherDetail1 = true;
					break;
				case "OtherDetail2":
					oDisplay.OtherDetail2 = true;
					break;
				case "ProductName":
					oDisplay.ProductName = true;
					break;
				case "ProductDescription":
					oDisplay.ProductDescription = true;
					break;
				case "IsService":
					oDisplay.IsService = true;
					break;
				case "HSN":
					oDisplay.HSN = true;
					break;
				case "Barcode":
					oDisplay.Barcode = true;
					break;
				case "BatchNameOrNumber":
					oDisplay.BatchNameOrNumber = true;
					break;
				case "BatchExpiryDate":
					oDisplay.BatchExpiryDate = true;
					break;
				case "WarrantyDate":
					oDisplay.WarrantyDate = true;
					break;
				case "OriginCountry":
					oDisplay.OriginCountry = true;
					break;
				case "UnitOfMeasurement":
					oDisplay.UnitOfMeasurement = true;
					break;
				case "Quantity":
					oDisplay.Quantity = true;
					break;
				case "FreeQuantity":
					oDisplay.FreeQuantity = true;
					break;
				case "UnitPrice":
					oDisplay.UnitPrice = true;
					break;
				case "ItemAmount":
					oDisplay.ItemAmount = true;
					break;
				case "ItemDiscount":
					oDisplay.ItemDiscount = true;
					break;
				case "ItemOtherCharges":
					oDisplay.ItemOtherCharges = true;
					break;
				case "ItemAssessableAmount":
					oDisplay.ItemAssessableAmount = true;
					break;
				case "PreTaxAmount":
					oDisplay.PreTaxAmount = true;
					break;
				case "InvoiceLineNetAmount":
					oDisplay.InvoiceLineNetAmount = true;
					break;
				case "IGSTRate":
					oDisplay.IGSTRate = true;
					break;
				case "IGSTAmount":
					oDisplay.IGSTAmount = true;
					break;
				case "CGSTRate":
					oDisplay.CGSTRate = true;
					break;
				case "CGSTAmount":
					oDisplay.CGSTAmount = true;
					break;
				case "SGSTRate":
					oDisplay.SGSTRate = true;
					break;
				case "SGSTAmount":
					oDisplay.SGSTAmount = true;
					break;
				case "CessAdvaloremRate":
					oDisplay.CessAdvaloremRate = true;
					break;
				case "CessAdvaloremAmount":
					oDisplay.CessAdvaloremAmount = true;
					break;
				case "CessSpecificRate":
					oDisplay.CessSpecificRate = true;
					break;
				case "CessSpecificAmount":
					oDisplay.CessSpecificAmount = true;
					break;
				case "StateCessRate":
					oDisplay.StateCessRate = true;
					break;
				case "StateCessAmount":
					oDisplay.StateCessAmount = true;
					break;
				case "TotalItemAmount":
					oDisplay.TotalItemAmount = true;
					break;
				case "ItemTotal":
					oDisplay.ItemTotal = true;
					break;
				case "PreTaxParticulars":
					oDisplay.PreTaxParticulars = true;
					break;
				case "TaxOn":
					oDisplay.TaxOn = true;
					break;
				case "Amount":
					oDisplay.Amount = true;
					break;
				case "InvoiceDiscount":
					oDisplay.InvoiceDiscount = true;
					break;
				case "InvoiceOtherCharges":
					oDisplay.InvoiceOtherCharges = true;
					break;
				case "InvoiceAllowancesOrCharges":
					oDisplay.InvoiceAllowancesOrCharges = true;
					break;
				case "SumOfInvoiceLineNetAmount":
					oDisplay.SumOfInvoiceLineNetAmount = true;
					break;
				case "SumOfAllowancesOnDocumentLevel":
					oDisplay.SumOfAllowancesOnDocumentLevel = true;
					break;
				case "SumOfChargesOnDocumentLevel":
					oDisplay.SumOfChargesOnDocumentLevel = true;
					break;
				case "FreightAmount":
					oDisplay.FreightAmount = true;
					break;
				case "InsuranceAmount":
					oDisplay.InsuranceAmount = true;
					break;
				case "PackagingAndForwardingCharges":
					oDisplay.PackagingAndForwardingCharges = true;
					break;
				case "InvoiceAssessableAmount":
					oDisplay.InvoiceAssessableAmount = true;
					break;
				case "Rate":
					oDisplay.Rate = true;
					break;
				case "InvoiceIGSTAmount":
					oDisplay.InvoiceIGSTAmount = true;
					break;
				case "InvoiceCGSTAmount":
					oDisplay.InvoiceCGSTAmount = true;
					break;
				case "InvoiceSGSTAmount":
					oDisplay.InvoiceSGSTAmount = true;
					break;
				case "InvoiceCessAdvaloremAmount":
					oDisplay.InvoiceCessAdvaloremAmount = true;
					break;
				case "InvoiceCessSpecificAmount":
					oDisplay.InvoiceCessSpecificAmount = true;
					break;
				case "InvoiceStateCessAmount":
					oDisplay.InvoiceStateCessAmount = true;
					break;
				case "TaxTotal":
					oDisplay.TaxTotal = true;
					break;
				case "InvoiceValue":
					oDisplay.InvoiceValue = true;
					break;
				case "RoundOff":
					oDisplay.RoundOff = true;
					break;
				case "TotalInvoiceValue":
					oDisplay.TotalInvoiceValue = true;
					break;
				case "ForeignCurrency":
					oDisplay.ForeignCurrency = true;
					break;
				case "CountryCode":
					oDisplay.CountryCode = true;
					break;
				case "InvoiceValueFC":
					oDisplay.InvoiceValueFC = true;
					break;
				case "PortCode":
					oDisplay.PortCode = true;
					break;
				case "ShippingBillNumber":
					oDisplay.ShippingBillNumber = true;
					break;
				case "ShippingBillDate":
					oDisplay.ShippingBillDate = true;
					break;
				case "InvoiceRemarks":
					oDisplay.InvoiceRemarks = true;
					break;
				case "InvoicePeriodStartDate":
					oDisplay.InvoicePeriodStartDate = true;
					break;
				case "InvoicePeriodEndDate":
					oDisplay.InvoicePeriodEndDate = true;
					break;
				case "OriginalDocumentNumber":
					oDisplay.OriginalDocumentNumber = true;
					break;
				case "OriginalDocumentDate":
					oDisplay.OriginalDocumentDate = true;
					break;
				case "OriginalInvoiceNumber":
					oDisplay.OriginalInvoiceNumber = true;
					break;
				case "OriginalInvoiceDate":
					oDisplay.OriginalInvoiceDate = true;
					break;
				case "InvoiceReference":
					oDisplay.InvoiceReference = true;
					break;
				case "PreceedingInvoiceNumber":
					oDisplay.PreceedingInvoiceNumber = true;
					break;
				case "PreceedingInvoiceDate":
					oDisplay.PreceedingInvoiceDate = true;
					break;
				case "ReceiptAdviceReference":
					oDisplay.ReceiptAdviceReference = true;
					break;
				case "TenderReference":
					oDisplay.TenderReference = true;
					break;
				case "ContractReference":
					oDisplay.ContractReference = true;
					break;
				case "ExternalReference":
					oDisplay.ExternalReference = true;
					break;
				case "ProjectReference":
					oDisplay.ProjectReference = true;
					break;
				case "CustomerPOReferenceNumber":
					oDisplay.CustomerPOReferenceNumber = true;
					break;
				case "CustomerPOReferenceDate":
					oDisplay.CustomerPOReferenceDate = true;
					break;
				case "OrderLineReference":
					oDisplay.OrderLineReference = true;
					break;
				case "PayeeName":
					oDisplay.PayeeName = true;
					break;
				case "ModeOfPayment":
					oDisplay.ModeOfPayment = true;
					break;
				case "BranchOrIFSCCode":
					oDisplay.BranchOrIFSCCode = true;
					break;
				case "PaymentTerms":
					oDisplay.PaymentTerms = true;
					break;
				case "PaymentInstruction":
					oDisplay.PaymentInstruction = true;
					break;
				case "CreditTransfer":
					oDisplay.CreditTransfer = true;
					break;
				case "DirectDebit":
					oDisplay.DirectDebit = true;
					break;
				case "CreditDays":
					oDisplay.CreditDays = true;
					break;
				case "PaidAmount":
					oDisplay.PaidAmount = true;
					break;
				case "BalanceAmount":
					oDisplay.BalanceAmount = true;
					break;
				case "PaymentDueDate":
					oDisplay.PaymentDueDate = true;
					break;
				case "AccountDetail":
					oDisplay.AccountDetail = true;
					break;
				case "EcomTransaction":
					oDisplay.EcomTransaction = true;
					break;
				case "EcomGSTIN":
					oDisplay.EcomGSTIN = true;
					break;
				case "EcomPOS":
					oDisplay.EcomPOS = true;
					break;
				case "SupportingDocURL":
					oDisplay.SupportingDocURL = true;
					break;
				case "SupportingDocBase64":
					oDisplay.SupportingDocBase64 = true;
					break;
				case "TDSFlag":
					oDisplay.TDSFlag = true;
					break;
				case "TransactionType":
					oDisplay.TransactionType = true;
					break;
				case "SubSupplyType":
					oDisplay.SubSupplyType = true;
					break;
				case "OtherSupplyTypeDescription":
					oDisplay.OtherSupplyTypeDescription = true;
					break;
				case "TransporterID":
					oDisplay.TransporterID = true;
					break;
				case "TransporterName":
					oDisplay.TransporterName = true;
					break;
				case "TransportMode":
					oDisplay.TransportMode = true;
					break;
				case "TransportDocNo":
					oDisplay.TransportDocNo = true;
					break;
				case "TransportDocDate":
					oDisplay.TransportDocDate = true;
					break;
				case "Distance":
					oDisplay.Distance = true;
					break;
				case "VehicleNo":
					oDisplay.VehicleNo = true;
					break;
				case "VehicleType":
					oDisplay.VehicleType = true;
					break;
				case "ReturnPeriod":
					oDisplay.ReturnPeriod = true;
					break;
				case "OriginalDocumentType":
					oDisplay.OriginalDocumentType = true;
					break;
				case "OriginalCustomerGSTIN":
					oDisplay.OriginalCustomerGSTIN = true;
					break;
				case "DifferentialPercentageFlag":
					oDisplay.DifferentialPercentageFlag = true;
					break;
				case "Section7OfIGSTFlag":
					oDisplay.Section7OfIGSTFlag = true;
					break;
				case "ClaimRefundFlag":
					oDisplay.ClaimRefundFlag = true;
					break;
				case "AutoPopulateToRefund":
					oDisplay.AutoPopulateToRefund = true;
					break;
				case "CRDRPreGST":
					oDisplay.CRDRPreGST = true;
					break;
				case "TCSFlag":
					oDisplay.TCSFlag = true;
					break;
				case "CustomerType":
					oDisplay.CustomerType = true;
					break;
				case "CustomerCode":
					oDisplay.CustomerCode = true;
					break;
				case "ProductCode":
					oDisplay.ProductCode = true;
					break;
				case "CategoryOfProduct":
					oDisplay.CategoryOfProduct = true;
					break;
				case "ITCFlag":
					oDisplay.ITCFlag = true;
					break;
				case "StateApplyingCess":
					oDisplay.StateApplyingCess = true;
					break;
				case "FOB":
					oDisplay.FOB = true;
					break;
				case "ExportDuty":
					oDisplay.ExportDuty = true;
					break;
				case "ExchangeRate":
					oDisplay.ExchangeRate = true;
					break;
				case "ReasonForCreditDebitNote":
					oDisplay.ReasonForCreditDebitNote = true;
					break;
				case "TCSIGSTAmount":
					oDisplay.TCSIGSTAmount = true;
					break;
				case "TCSCGSTAmount":
					oDisplay.TCSCGSTAmount = true;
					break;
				case "TCSSGSTAmount":
					oDisplay.TCSSGSTAmount = true;
					break;
				case "TDSIGSTAmount":
					oDisplay.TDSIGSTAmount = true;
					break;
				case "TDSCGSTAmount":
					oDisplay.TDSCGSTAmount = true;
					break;
				case "TDSSGSTAmount":
					oDisplay.TDSSGSTAmount = true;
					break;
				case "UserID":
					oDisplay.UserID = true;
					break;
				case "CompanyCode":
					oDisplay.CompanyCode = true;
					break;
				case "SourceIdentifier":
					oDisplay.SourceIdentifier = true;
					break;
				case "SourceFileName":
					oDisplay.SourceFileName = true;
					break;
				case "ProfitCentre1":
					oDisplay.ProfitCentre1 = true;
					break;
				case "ProfitCentre2":
					oDisplay.ProfitCentre2 = true;
					break;
				case "PlantCode":
					oDisplay.PlantCode = true;
					break;
				case "Division":
					oDisplay.Division = true;
					break;
				case "SubDivision":
					oDisplay.SubDivision = true;
					break;
				case "Location":
					oDisplay.Location = true;
					break;
				case "SalesOrganisation":
					oDisplay.SalesOrganisation = true;
					break;
				case "DistributionChannel":
					oDisplay.DistributionChannel = true;
					break;
				case "ProfitCentre3":
					oDisplay.ProfitCentre3 = true;
					break;
				case "ProfitCentre4":
					oDisplay.ProfitCentre5 = true;
					break;
				case "ProfitCentre6":
					oDisplay.ProfitCentre6 = true;
					break;
				case "ProfitCentre7":
					oDisplay.ProfitCentre7 = true;
					break;
				case "ProfitCentre8":
					oDisplay.ProfitCentre8 = true;
					break;
				case "GLAssessableValue":
					oDisplay.GLAssessableValue = true;
					break;
				case "GLIGST":
					oDisplay.GLIGST = true;
					break;
				case "GLCGST":
					oDisplay.GLCGST = true;
					break;
				case "GLSGST":
					oDisplay.GLSGST = true;
					break;
				case "GLAdvaloremCess":
					oDisplay.GLAdvaloremCess = true;
					break;
				case "GLSpecificCess":
					oDisplay.GLSpecificCess = true;
					break;
				case "GLStateCess":
					oDisplay.GLStateCess = true;
					break;
				case "GLPostingDate":
					oDisplay.GLPostingDate = true;
					break;
				case "SalesOrderNumber":
					oDisplay.SalesOrderNumber = true;
					break;
				case "EWBNumber":
					oDisplay.EWBNumber = true;
					break;
				case "EWBDate":
					oDisplay.EWBDate = true;
					break;
				case "AccountingVoucherNumber":
					oDisplay.AccountingVoucherNumber = true;
					break;
				case "AccountingVoucherDate":
					oDisplay.AccountingVoucherDate = true;
					break;
				case "UserDefinedField1":
					oDisplay.UserDefinedField1 = true;
					break;
				case "UserDefinedField2":
					oDisplay.UserDefinedField2 = true;
					break;
				case "UserDefinedField3":
					oDisplay.UserDefinedField3 = true;
					break;
				case "UserDefinedField4":
					oDisplay.UserDefinedField4 = true;
					break;
				case "UserDefinedField5":
					oDisplay.UserDefinedField5 = true;
					break;
				case "UserDefinedField6":
					oDisplay.UserDefinedField6 = true;
					break;
				case "UserDefinedField7":
					oDisplay.UserDefinedField7 = true;
					break;
				case "UserDefinedField8":
					oDisplay.UserDefinedField8 = true;
					break;
				case "UserDefinedField9":
					oDisplay.UserDefinedField9 = true;
					break;
				case "UserDefinedField10":
					oDisplay.UserDefinedField10 = true;
					break;
				case "UserDefinedField11":
					oDisplay.UserDefinedField11 = true;
					break;
				case "UserDefinedField12":
					oDisplay.UserDefinedField12 = true;
					break;
				case "UserDefinedField13":
					oDisplay.UserDefinedField13 = true;
					break;
				case "UserDefinedField14":
					oDisplay.UserDefinedField14 = true;
					break;
				case "UserDefinedField15":
					oDisplay.UserDefinedField15 = true;
					break;
				case "UserDefinedField16":
					oDisplay.UserDefinedField16 = true;
					break;
				case "UserDefinedField17":
					oDisplay.UserDefinedField17 = true;
					break;
				case "UserDefinedField18":
					oDisplay.UserDefinedField18 = true;
					break;
				case "UserDefinedField19":
					oDisplay.UserDefinedField19 = true;
					break;
				case "UserDefinedField20":
					oDisplay.UserDefinedField20 = true;
					break;
				case "UserDefinedField21":
					oDisplay.UserDefinedField21 = true;
					break;
				case "UserDefinedField22":
					oDisplay.UserDefinedField22 = true;
					break;
				case "UserDefinedField23":
					oDisplay.UserDefinedField23 = true;
					break;
				case "UserDefinedField24":
					oDisplay.UserDefinedField24 = true;
					break;
				case "UserDefinedField25":
					oDisplay.UserDefinedField25 = true;
					break;
				case "UserDefinedField26":
					oDisplay.UserDefinedField26 = true;
					break;
				case "UserDefinedField27":
					oDisplay.UserDefinedField27 = true;
					break;
				case "UserDefinedField28":
					oDisplay.UserDefinedField28 = true;
					break;
				case "UserDefinedField29":
					oDisplay.UserDefinedField29 = true;
					break;
				case "UserDefinedField30":
					oDisplay.UserDefinedField30 = true;
					break;
				case "CustomerTAN":
					oDisplay.CustomerTAN = true;
					break;
				case "EcomTransactionID":
					oDisplay.EcomTransactionID = true;
					break;
				case "EligibilityIndicator":
					oDisplay.EligibilityIndicator = true;
					break;
				case "CommonSupplyIndicator":
					oDisplay.CommonSupplyIndicator = true;
					break;
				case "AvailableIGST":
					oDisplay.AvailableIGST = true;
					break;
				case "AvailableCGST":
					oDisplay.AvailableCGST = true;
					break;
				case "AvailableSGST":
					oDisplay.AvailableSGST = true;
					break;
				case "AvailableCess":
					oDisplay.AvailableCess = true;
					break;
				case "ITCEntitlement":
					oDisplay.ITCEntitlement = true;
					break;
				case "ITCReversalIdentifier":
					oDisplay.ITCReversalIdentifier = true;
					break;

				}
			}
			return oDisplay;
		}

	};
});