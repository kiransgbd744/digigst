sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/m/MessagePopover",
	"com/ey/digigst/util/Formatter",
	"com/ey/digigst/util/gstr1/configCorrectedInvoice",
	"sap/m/MessageItem"
], function (JSONModel, MessageBox, MessageToast, MessagePopover, Formatter, configCorrectedInvoice, MessageItem) {
	"use strict";
	var oMessageTemplate = new MessageItem({
		type: "{type}",
		title: "{title}",
		activeTitle: "{active}",
		description: "{description}",
		subtitle: "{subtitle}"
	});

	var oMessagePopover = new MessagePopover({
		items: {
			path: "/",
			template: oMessageTemplate
		},
		activeTitlePress: function () {
			// 			MessageToast.show("Active title is pressed");
		}
	});
	return {
		/*================================================================================*/
		/*========= Success MessageBox ===================================================*/
		/*================================================================================*/
		_successMessage: function (vMsg) {
			MessageBox.success(vMsg, {
				title: "Success",
				styleClass: "sapUiSizeCompact"
			});
		},

		/*================================================================================*/
		/*========= Error MessageBox =====================================================*/
		/*================================================================================*/
		_errorMessage: function (vMsg) {
			MessageBox.error(vMsg, {
				title: "Error",
				styleClass: "sapUiSizeCompact"
			});
		},

		/*================================================================================*/
		/*========= Information MessageBox ===============================================*/
		/*================================================================================*/
		_infoMessage: function (vMsg) {
			MessageBox.information(vMsg, {
				styleClass: "sapUiSizeCompact"
			});
		},

		/*================================================================================*/
		/*========= Message Popover Event ================================================*/
		/*================================================================================*/
		_popOverForErrors: function (oEvent) {
			oMessagePopover.toggle(oEvent.getSource());
		},

		/*================================================================================*/
		/*========= Get GSTIN Management Details =========================================*/
		/*================================================================================*/
		_getGstnManageDetails: function (controller) {
			if (controller.byId("sbGstnManage").getSelectedKey() === "ProcessGSTN") {
				controller.table = controller.section = "";
				this._getProcessedData(controller, false);
			} else if (controller.byId("sbGstnManage").getSelectedKey() === "ErrorGSTN") {
				this._getErrorData(controller);
			}
		},

		/*================================================================================*/
		/*========= Segmented Button =====================================================*/
		/*================================================================================*/
		_segmentManage: function (oEvent, controller) {
			// 			debugger; //eslint-disable-line no-debugger
			var oSegmentBtn = oEvent.getSource().getSelectedKey();
			if (oSegmentBtn === "ErrorGSTN") {
				var vTab = "tabErrorGstn",
					vModel = "InvoiceModel",
					vFlag = false;
			} else {
				// vTab = "tabProcessGstn";
				// vModel = "AspProcessModel";
				vFlag = true;
				controller.table = controller.section = "";
			}
			controller.byId("bBackGSTN").setVisible(false);
			controller.byId("idGstnInvErr").setVisible(false);

			controller.byId("tabProcessGstn").setVisible(vFlag);
			controller.byId("tabErrorGstn").setVisible(!vFlag);
			controller.byId("tabGstnErrorItem").setVisible(false);

			controller.byId("idErrToolbar").setVisible(!vFlag);
			var oModel = controller.byId(vTab).getModel(vModel);
			if (oSegmentBtn === "ErrorGSTN" && !oModel) {
				this._getErrorData(controller);
			} else if (!oModel) {
				this._getProcessedData(controller, false);
			}
		},

		/*================================================================================*/
		/*========= Navigate Back to Main Screen =========================================*/
		/*================================================================================*/
		_navBack: function (oEvent, controller) {
			var vButton = controller.getView().byId("sbGstnManage").getSelectedKey();
			controller.table = controller.section = "";
			if (vButton === "ProcessGSTN") {
				var vFlag = true;
				// this._getProcessedData(controller, false);
			} else {
				vFlag = false;
				this._getErrorData(controller);
			}
			// 			debugger; //eslint-disable-line no-debugger
			controller.byId("bBackGSTN").setVisible(false);
			controller.byId("idGstnInvErr").setVisible(false);
			controller.byId("idErrToolbar").setVisible(!vFlag);
			controller.byId("tabErrorGstn").setVisible(!vFlag);
			controller.byId("tabGstnErrorItem").setVisible(false);
		},

		/*================================================================================*/
		/*======== Search Criteria to get data from AJAX Call ============================*/
		/*================================================================================*/
		_searchInfo: function (controller, status) {
			var vCriteria = controller.byId("slGstinCriteria").getSelectedKey();
			var aEntity = controller.byId("slGstnEntity").getSelectedKeys();
			var aGstins = controller.byId("slSuppGstn").getSelectedKeys();
			var vDocNo = controller.byId("inGstnDocNo").getValue();
			if (controller.byId("sbGstnManage").getSelectedKey() === "ProcessGSTN") {
				var vPageNo = controller.byId("iPageNo").getValue();
			} else {
				vPageNo = controller.byId("iPageNoErr").getValue();
			}
			if (!vPageNo || vPageNo === "0") {
				vPageNo = 1;
			} else {
				vPageNo = parseInt(vPageNo, 10);
			}
			var searchInfo = {
				"hdr": {
					"pageNum": vPageNo - 1,
					"pageSize": 50
				},
				"req": {
					"entityId": aEntity,
					"gstins": aGstins,
					"criteria": vCriteria,
					"receivFromDate": null,
					"receivToDate": null,
					"docFromDate": null,
					"docToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docNo": null,
					"processingStatus": status
				}
			};
			if (vDocNo !== "") {
				searchInfo.req.docNo = vDocNo;
			}
			switch (vCriteria) {
			case "RECEIVED_DATE_SEARCH":
				searchInfo.req.receivFromDate = controller._formatDate(controller.byId("drsGstnManage").getDateValue());
				searchInfo.req.receivToDate = controller._formatDate(controller.byId("drsGstnManage").getSecondDateValue());
				break;
			case "DOCUMENT_DATE_SEARCH":
				searchInfo.req.docFromDate = controller._formatDate(controller.byId("drsGstnManage").getDateValue());
				searchInfo.req.docToDate = controller._formatDate(controller.byId("drsGstnManage").getSecondDateValue());
				break;
			case "RETURN_DATE_SEARCH":
				searchInfo.req.returnFromDate = (controller._formatPeriod(controller.byId("drsGstnManage").getDateValue())).substr(2, 6);
				searchInfo.req.returnToDate = (controller._formatPeriod(controller.byId("drsGstnManage").getSecondDateValue())).substr(2, 6);
				break;
			}
			return searchInfo;
		},

		/*================================================================================*/
		/*======== GSTN Process ==========================================================*/
		/*================================================================================*/
		_getProcessedData: function (controller, vInfo) {
			// 			debugger; //eslint-disable-line no-debugger
			var that = this;
			that.info = vInfo;
			if (controller.byId("sbGstnManage").getSelectedKey() === "ProcessGSTN") {
				controller.byId("tabProcessGstn").setVisible(true);
				controller.byId("tabErrorGstn").setVisible(false);
				controller.byId("tabGstnErrorItem").setVisible(false);
			}
			// 			var bCompact = !!controller.getView().$().closest(".sapUiSizeCompact").length;
			// 			var searchInfo = this._searchInfo(controller);
			// 			controller.getView().setBusy(true);
			// 			var oGstinProcessedModel = new JSONModel();
			// 			var countForProGstin = 0;
			// 			$(document).ready(function ($) {
			// 				$.ajax({
			// 					method: "POST",
			// 					url: "/aspsapapi/docSearch.do",
			// 					contentType: "application/json",
			// 					data: JSON.stringify(searchInfo)
			// 				}).done(function (data, status, jqXHR) {
			// 					controller.getView().setBusy(false);
			// 					var gstinProcessData = [];

			// 					for (var z = 0; z < data.resp.length; z++) {
			// 						if (data.resp[z].isProcessed === false) {
			// 							countForProGstin++;
			// 						} else {
			// 							gstinProcessData.push(data.resp[z]);
			// 						}
			// 					}
			// 					if (countForProGstin === data.resp.length) {
			// 						MessageBox.information(
			// 							"Processed records does not exist.", {
			// 								styleClass: bCompact ? "sapUiSizeCompact" : ""
			// 							});
			// 						oGstinProcessedModel.setData();
			// 						//oView.setModel(oGstinProcessedModel, "GstinProcessModel");
			// 					} else {
			// 						oGstinProcessedModel.setData(that._processGstinJSON(gstinProcessData));

			// 					}
			// 					controller.byId("tabForGSTINProcessed").setModel(oGstinProcessedModel, "GstinProcessModel");

			// 				}).fail(function (jqXHR, status, err) {
			// 					controller.getView().setBusy(false);
			// 					if (jqXHR.status === 500) {
			// 						MessageBox.information("Incorrect Filter Data/Some Mandatory field is missing.", {
			// 							styleClass: bCompact ? "sapUiSizeCompact" : ""
			// 						});
			// 					} else if (jqXHR.status === 504) {
			// 						MessageBox.information("Gateway Timeout.", {
			// 							styleClass: bCompact ? "sapUiSizeCompact" : ""
			// 						});
			// 					}
			// 				});
			// 			});
		},

		// 		_processGstinJSON: function (gstinProcessData) {
		// 			var gstinHedData = [];
		// 			var indexForGstin = 0;
		// 			for (var v = 0; v < gstinProcessData.length; v++) {
		// 				if (gstinProcessData[v].crDrPreGst === false) {
		// 					gstinProcessData[v].crDrPreGst = "N";
		// 				} else {
		// 					gstinProcessData[v].crDrPreGst = "Y";
		// 				}
		// 				if (gstinProcessData[v].tcsFlag === false) {
		// 					gstinProcessData[v].tcsFlag = "N";
		// 				} else {
		// 					gstinProcessData[v].tcsFlag = "Y";
		// 				}
		// 				if (gstinProcessData[v].itcFlag === false) {
		// 					gstinProcessData[v].itcFlag = "N";
		// 				} else {
		// 					gstinProcessData[v].itcFlag = "Y";
		// 				}
		// 				gstinHedData.push({
		// 					"sno": indexForGstin + 1,
		// 					"supplierGstin": gstinProcessData[v].supplierGstin,
		// 					"id": gstinProcessData[v].id,
		// 					"fiYear": gstinProcessData[v].fiYear,
		// 					"returnPeriod": gstinProcessData[v].returnPeriod,
		// 					"supplyType": gstinProcessData[v].supplyType,
		// 					"docType": gstinProcessData[v].docType,
		// 					"docNo": gstinProcessData[v].docNo,
		// 					"docDate": gstinProcessData[v].docDate,
		// 					"docAmount": gstinProcessData[v].docAmount,
		// 					"originalDocNo": gstinProcessData[v].originalDocNo,
		// 					"originalDocDate": gstinProcessData[v].originalDocDate,
		// 					//"taxableValue": gstinProcessData[v].taxableValue,
		// 					"gstr1SubCategory": gstinProcessData[v].gstr1SubCategory,
		// 					"gstr1TableNo": gstinProcessData[v].gstr1TableNo,
		// 					"uinOrComposition": gstinProcessData[v].uinOrComposition,
		// 					"custGstin": gstinProcessData[v].custGstin,
		// 					"custName": gstinProcessData[v].custName,
		// 					"shipToState": gstinProcessData[v].shipToState,
		// 					"billToState": gstinProcessData[v].billToState,
		// 					"pos": gstinProcessData[v].pos,
		// 					"shipPortCode": gstinProcessData[v].shipPortCode,
		// 					"reverseCharge": gstinProcessData[v].reverseCharge,
		// 					"ecomCustGSTIN": gstinProcessData[v].ecomCustGSTIN,
		// 					"dataOriginTypeCode": gstinProcessData[v].dataOriginTypeCode,
		// 					"tcsFlag": gstinProcessData[v].tcsFlag,
		// 					"itcFlag": gstinProcessData[v].itcFlag,
		// 					"customerCode": gstinProcessData[v].customerCode,
		// 					"fob": gstinProcessData[v].fob,
		// 					"shippingBillNo": gstinProcessData[v].shippingBillNo,
		// 					"shippingBillDate": gstinProcessData[v].shippingBillDate,
		// 					"glAccountCode": gstinProcessData[v].glAccountCode,
		// 					"accountVoucherNo": gstinProcessData[v].accountVoucherNo,
		// 					"accountVoucherDate": gstinProcessData[v].accountVoucherDate,
		// 					"crDrReason": gstinProcessData[v].crDrReason,
		// 					"crDrPreGst": gstinProcessData[v].crDrPreGst,
		// 					"lineItems": []
		// 				});

		// 				var lengthofCorrectedDataItemsGstin = gstinProcessData[v].lineItems.length;
		// 				for (var i = 0; i < lengthofCorrectedDataItemsGstin; i++) {
		// 					gstinHedData[indexForGstin].lineItems.push({
		// 						"supplierGstin": gstinProcessData[v].supplierGstin,
		// 						//"id": gstinProcessData[v].id,
		// 						"fiYear": gstinProcessData[v].fiYear,
		// 						"returnPeriod": gstinProcessData[v].returnPeriod,
		// 						"supplyType": gstinProcessData[v].supplyType,
		// 						"docType": gstinProcessData[v].docType,
		// 						"docNo": gstinProcessData[v].docNo,
		// 						"docDate": gstinProcessData[v].docDate,
		// 						"docAmount": gstinProcessData[v].docAmount,
		// 						"originalDocNo": gstinProcessData[v].originalDocNo,
		// 						"originalDocDate": gstinProcessData[v].originalDocDate,
		// 						//"taxableValue": gstinProcessData[v].taxableValue,
		// 						"gstr1SubCategory": gstinProcessData[v].gstr1SubCategory,
		// 						"gstr1TableNo": gstinProcessData[v].gstr1TableNo,
		// 						"uinOrComposition": gstinProcessData[v].uinOrComposition,
		// 						"custGstin": gstinProcessData[v].custGstin,
		// 						"custName": gstinProcessData[v].custName,
		// 						"shipToState": gstinProcessData[v].shipToState,
		// 						"billToState": gstinProcessData[v].billToState,
		// 						"pos": gstinProcessData[v].pos,
		// 						"shipPortCode": gstinProcessData[v].shipPortCode,
		// 						"reverseCharge": gstinProcessData[v].reverseCharge,
		// 						"ecomCustGSTIN": gstinProcessData[v].ecomCustGSTIN,
		// 						"dataOriginTypeCode": gstinProcessData[v].dataOriginTypeCode,
		// 						"tcsFlag": gstinProcessData[v].tcsFlag,
		// 						"itcFlag": gstinProcessData[v].itcFlag,
		// 						"customerCode": gstinProcessData[v].customerCode,
		// 						"fob": gstinProcessData[v].fob,
		// 						"shippingBillNo": gstinProcessData[v].shippingBillNo,
		// 						"shippingBillDate": gstinProcessData[v].shippingBillDate,
		// 						"glAccountCode": gstinProcessData[v].glAccountCode,
		// 						"accountVoucherNo": gstinProcessData[v].accountVoucherNo,
		// 						"accountVoucherDate": gstinProcessData[v].accountVoucherDate,
		// 						"crDrReason": gstinProcessData[v].crDrReason,
		// 						"crDrPreGst": gstinProcessData[v].crDrPreGst,
		// 						//"infoList": infoList,
		// 						"id": gstinProcessData[v].lineItems[i].id,
		// 						"itemNo": gstinProcessData[v].lineItems[i].itemNo,
		// 						"hsnsacCode": gstinProcessData[v].lineItems[i].hsnsacCode,
		// 						"itemDesc": gstinProcessData[v].lineItems[i].itemDesc,
		// 						"itemType": gstinProcessData[v].lineItems[i].itemType,
		// 						"itemUqc": gstinProcessData[v].lineItems[i].itemUqc,
		// 						"itemQty": gstinProcessData[v].lineItems[i].itemQty,
		// 						//"supplyType": gstinProcessData[v].lineItems[i].supplyType,
		// 						"taxableVal": gstinProcessData[v].lineItems[i].taxableVal,
		// 						//"taxRate": gstinProcessData[v].lineItems[i].taxRate,
		// 						"igstRate": gstinProcessData[v].lineItems[i].igstRate,
		// 						"sgstRate": gstinProcessData[v].lineItems[i].cgstRate,
		// 						"cgstRate": gstinProcessData[v].lineItems[i].sgstRate,
		// 						"igstAmt": gstinProcessData[v].lineItems[i].igstAmt,
		// 						"cgstAmt": gstinProcessData[v].lineItems[i].cgstAmt,
		// 						"sgstAmt": gstinProcessData[v].lineItems[i].sgstAmt,
		// 						"cessRateSpecific": gstinProcessData[v].lineItems[i].cessRateSpecific,
		// 						"cessAmtSpecfic": gstinProcessData[v].lineItems[i].cessAmtSpecfic,
		// 						"cessRateAdvalorem": gstinProcessData[v].lineItems[i].cessRateAdvalorem,
		// 						"cessAmtAdvalorem": gstinProcessData[v].lineItems[i].cessAmtAdvalorem,
		// 						"productCode": gstinProcessData[v].lineItems[i].productCode
		// 					});
		// 				}
		// 				indexForGstin++;
		// 			}
		// 			return gstinHedData;
		// 		},

		/*================================================================================*/
		/*======== GSTN Error ============================================================*/
		/*================================================================================*/
		_getErrorData: function (controller) {
			controller.byId("idGstnInvErr").setVisible(false);
			if (controller.byId("sbGstnManage").getSelectedKey() === "ErrorGSTN") {
				controller.byId("tabProcessGstn").setVisible(false);
				controller.byId("tabErrorGstn").setVisible(true);
				controller.byId("tabGstnErrorItem").setVisible(false);
			}
			var bCompact = !!controller.getView().$().closest(".sapUiSizeCompact").length;
			var searchInfo = this._searchInfo(controller, "E");
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstnDocSearch.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				var vPageNo = Math.ceil(data.hdr.totalCount / 50);
				controller.byId("tPageNoErr").setText("/ " + vPageNo);
				if (vPageNo === 0) {
					controller.byId("iPageNoErr").setValue(0);
					controller.byId("iPageNoErr").setEnabled(false);
					controller.byId("bPrevErr").setEnabled(false);
					controller.byId("bNextErr").setEnabled(false);

				} else if (controller.byId("iPageNoErr").getValue() === "" || controller.byId("iPageNoErr").getValue() === "0") {
					controller.byId("iPageNoErr").setValue(1);
					controller.byId("iPageNoErr").setEnabled(true);
					controller.byId("bPrevErr").setEnabled(false);
				}
				var vInPage = parseInt(controller.byId("iPageNoErr").getValue(), 10);
				if (vPageNo > 1 && vPageNo !== vInPage) {
					controller.byId("bNextErr").setEnabled(true);
				} else {
					controller.byId("bNextErr").setEnabled(false);
				}

				var oInvoiceModel = new JSONModel();
				if (data.resp.length === 0) {
					MessageBox.information("No Errors in this record.", {
						styleClass: bCompact ? "sapUiSizeCompact" : ""
					});
					oInvoiceModel.setData([]);
				} else {
					oInvoiceModel.setData(data.resp);
				}
				controller.byId("tabErrorGstn").setModel(oInvoiceModel, "InvoiceModel");
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
				var oInvoiceModel = new JSONModel();
				oInvoiceModel.setData([]);
				controller.byId("tabErrorGstn").setModel(oInvoiceModel, "InvoiceModel");
			});
		},

		/*================================================================================*/
		/*======== GSTN Error Correction =================================================*/
		/*================================================================================*/
		_detailErrToCorrect: function (oEvent, controller) {
			var oErrorItem = oEvent.getSource().getBindingContext("InvoiceModel").getObject();
			controller.byId("bBackGSTN").setVisible(true);
			controller.byId("idErrToolbar").setVisible(false);
			controller.byId("tabErrorGstn").setVisible(false);
			controller.byId("tabGstnErrorItem").setVisible(true);
			controller.byId("idGstnInvErr").setVisible(true);

			var oModelErrorItems = new JSONModel();
			oModelErrorItems.setData(this._errorJSON(oErrorItem, controller));
			controller.byId("tabGstnErrorItem").setModel(oModelErrorItems, "itemsModel");

			debugger; //eslint-disable-line no-debugger
			for (var i = 0; i < oErrorItem.errorList.length; i++) {
				if (oErrorItem.errorList[i].index >= 0) {
					var vIdx = oErrorItem.errorList[i].index;
					var colForHeader = controller.byId("tabGstnErrorItem").getRows()[vIdx].getCells();
					// } else {
					// 	colForHeader = controller.byId("tabGstnErrorItem").getRows()[0].getCells();
				}
				this._setValueState(colForHeader, oErrorItem.errorList[i]);
			}

		},

		/*================================================================================*/
		/*======== Set ValueState of Input Fields ========================================*/
		/*================================================================================*/
		_setValueState: function (colForHeader, errFields) {
			if (errFields.errorType === "ERR") {
				var aField = errFields.errorFields.split(",");
				for (var i = 3; i < colForHeader.length; i++) {
					if (aField.includes(colForHeader[i].getName())) {
						colForHeader[i].setValueState(sap.ui.core.ValueState.Error);
						colForHeader[i].setValueStateText(errFields.errorDesc);
					}
				}
			}
		},

		/*================================================================================*/
		/*======== Parsing Error GSTN data in JSON =======================================*/
		/*================================================================================*/
		_errorJSON: function (aDataError, controller) {
			var aData = JSON.parse(JSON.stringify(aDataError));
			aData.crDrPreGst = aData.crDrPreGst ? "Y" : "N";
			aData.tcsFlag = aData.tcsFlag ? "Y" : "N";
			aData.itcFlag = aData.itcFlag ? "Y" : "N";

			var aErrorMsg = [];
			for (var i = 0; i < aData.errorList.length; i++) {
				aErrorMsg.push({
					type: aData.errorList[i].errorType === "ERR" ? "Error" : "Information",
					title: aData.errorList[i].errorCode,
					active: true,
					description: aData.errorList[i].errorDesc,
					subtitle: aData.errorList[i].errorDesc
				});
			}
			oMessagePopover.setModel(new JSONModel(aErrorMsg));
			if (i > 0) {
				controller.byId("idGstnInvErr").setText(i);
			} else {
				controller.byId("idGstnInvErr").setText("");
			}

			// 			debugger; //eslint-disable-line no-debugger
			// 			for (i = 0; i < aData.lineItems.length; i++) {
			// 				aData.lineItems[i].supplierGstin = aData.supplierGstin;
			// 				aData.lineItems[i].fiYear = aData.fiYear;
			// 				aData.lineItems[i].docNo = aData.docNo;
			// 				aData.lineItems[i].docType = aData.docType;
			// 				aData.lineItems[i].docDate = aData.docDate;
			// 				aData.lineItems[i].docAmount = aData.docAmount;
			// 				aData.lineItems[i].returnPeriod = aData.returnPeriod;
			// 				aData.lineItems[i].gstr1SubCategory = aData.gstr1SubCategory;
			// 				aData.lineItems[i].gstr1TableNo = aData.gstr1TableNo;
			// 				aData.lineItems[i].supplyType = aData.supplyType;
			// 				aData.lineItems[i].originalDocNo = aData.originalDocNo;
			// 				aData.lineItems[i].originalDocDate = aData.originalDocDate;
			// 				aData.lineItems[i].crDrPreGst = aData.crDrPreGst;
			// 				aData.lineItems[i].pos = aData.pos;
			// 				aData.lineItems[i].custGstin = aData.custGstin;
			// 				aData.lineItems[i].billToState = aData.billToState;
			// 				aData.lineItems[i].shipToState = aData.shipToState;
			// 				aData.lineItems[i].shippingBillNo = aData.shippingBillNo;
			// 				aData.lineItems[i].shippingBillDate = aData.shippingBillDate;
			// 				aData.lineItems[i].accountVoucherNo = aData.accountVoucherNo;
			// 				aData.lineItems[i].accountVoucherDate = aData.accountVoucherDate;
			// 				aData.lineItems[i].tcsFlag = aData.tcsFlag;
			// 				aData.lineItems[i].itcFlag = aData.itcFlag;
			// 				// "uinOrComposition"
			// 				// "custOrSuppCode"
			// 				// "custOrSuppName"
			// 				// "shipPortCode"
			// 				// "reverseCharge"
			// 				// "ecomCustGSTIN"
			// 				// "dataOriginTypeCode"
			// 				// "fob"
			// 				// "exportDuty"
			// 				// "plantCode"
			// 				// "sourceIdentifier"
			// 				// "sourceFileName"
			// 				// "division"
			// 				// "subDivision"
			// 				// "profitCentre1"
			// 				// "profitCentre2"
			// 				// "crDrReason"
			// 				// "crDrPreGst"
			// 				// "portCode"
			// 				// "userDefinedField1"
			// 				// "userDefinedField2"
			// 				// "userDefinedField3"
			// 				// "derivedTaxperiod"
			// 				// "id"
			// 				// "itemNo"
			// 				// "hsnsacCode"
			// 				// "productCode"
			// 				// "itemDesc"
			// 				// "itemType"
			// 				// "itemUqc"
			// 				// "itemQty"
			// 				// "glAccountCode"
			// 				// "taxableVal"
			// 				// "igstRate"
			// 				// "sgstRate"
			// 				// "cgstRate"
			// 				// "igstAmt"
			// 				// "cgstAmt"
			// 				// "sgstAmt"
			// 				// "cessRateSpecific"
			// 				// "cessAmtSpecfic"
			// 				// "cessRateAdvalorem"
			// 				// "cessAmtAdvalorem"
			// 				// "lineItemAmt"

			// 				// 			an1Gstr1SubCategory: "B2B"
			// 				// 			an1Gstr1TableNo: "3B"
			// 				// 			derivedTaxperiod: 201903
			// 			}
			return aData;
		}

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		// 		_saveAspCorrectedError: function (oEvent, that) {
		// 			var self = this;
		// 			var bCompact;
		// 			var oBundle = that.getOwnerComponent().getModel("i18n").getResourceBundle();
		// 			this.oErrCorrectedData = that.byId("tableForInvCorGstinErr").getModel("itemsModel").oData;
		// 			this.bCompact = !!oEvent.getSource().$().closest(".sapUiSizeCompact").length;
		// 			MessageBox.confirm(
		// 				oBundle.getText("config.errorCorrection.confirmationMsg"), {
		// 					styleClass: bCompact ? "sapUiSizeCompact" : "",
		// 					onClose: function (oAction) {
		// 						if (oAction === "OK") {
		// 							self._saveErrors(that);

		// 						} else {
		// 							that._reset();
		// 						}

		// 					}
		// 				});
		// 		},
		// 		_saveErrors: function (that) {
		// 			var oCorrectedData = this.oErrCorrectedData;
		// 			this._createInvoiceCorrectionModel(that);
		// 			var oCorectedDataMod = that.oView.getModel("CreateInvoiceOrgModel").getData();
		// 			if (oCorrectedData.crDrPreGst === "Y") {
		// 				oCorrectedData.crDrPreGst = true;
		// 			} else {
		// 				oCorrectedData.crDrPreGst = false;
		// 			}
		// 			if (oCorrectedData.tcsFlag === "Y") {
		// 				oCorrectedData.tcsFlag = true;
		// 			} else {
		// 				oCorrectedData.tcsFlag = false;
		// 			}
		// 			if (oCorrectedData.itcFlag === "Y") {
		// 				oCorrectedData.itcFlag = true;
		// 			} else {
		// 				oCorrectedData.itcFlag = false;
		// 			}
		// 			//oCorectedDataMod.req[0].docDate = oCorrectedData.docDate;
		// 			oCorectedDataMod.req[0].id = oCorrectedData.id;
		// 			oCorectedDataMod.req[0].supplierGstin = oCorrectedData.supplierGstin;
		// 			oCorectedDataMod.req[0].fiYear = oCorrectedData.fiYear;
		// 			oCorectedDataMod.req[0].returnPeriod = oCorrectedData.returnPeriod;
		// 			oCorectedDataMod.req[0].supplyType = oCorrectedData.supplyType;
		// 			oCorectedDataMod.req[0].docType = oCorrectedData.docType;
		// 			oCorectedDataMod.req[0].docNo = oCorrectedData.docNo;
		// 			oCorectedDataMod.req[0].docDate = oCorrectedData.docDate;
		// 			oCorectedDataMod.req[0].docAmount = oCorrectedData.docAmount;
		// 			oCorectedDataMod.req[0].originalDocNo = oCorrectedData.originalDocNo;
		// 			oCorectedDataMod.req[0].originalDocDate = oCorrectedData.originalDocDate;
		// 			//oCorectedDataMod.req[0].taxableValue = oCorrectedData.taxableValue;
		// 			oCorectedDataMod.req[0].uinOrComposition = oCorrectedData.uinOrComposition;
		// 			oCorectedDataMod.req[0].custGstin = oCorrectedData.custGstin;
		// 			oCorectedDataMod.req[0].custName = oCorrectedData.custName;
		// 			oCorectedDataMod.req[0].shipToState = oCorrectedData.shipToState;
		// 			oCorectedDataMod.req[0].billToState = oCorrectedData.billToState;
		// 			oCorectedDataMod.req[0].pos = oCorrectedData.pos;
		// 			oCorectedDataMod.req[0].shipPortCode = oCorrectedData.shipPortCode;
		// 			oCorectedDataMod.req[0].reverseCharge = oCorrectedData.reverseCharge;
		// 			oCorectedDataMod.req[0].ecomCustGSTIN = oCorrectedData.ecomCustGSTIN;
		// 			oCorectedDataMod.req[0].dataOriginTypeCode = oCorrectedData.dataOriginTypeCode;
		// 			oCorectedDataMod.req[0].tcsFlag = oCorrectedData.tcsFlag;
		// 			oCorectedDataMod.req[0].itcFlag = oCorrectedData.itcFlag;
		// 			oCorectedDataMod.req[0].customerCode = oCorrectedData.customerCode;
		// 			oCorectedDataMod.req[0].fob = oCorrectedData.fob;
		// 			oCorectedDataMod.req[0].shippingBillNo = oCorrectedData.shippingBillNo;
		// 			oCorectedDataMod.req[0].shippingBillDate = oCorrectedData.shippingBillDate;
		// 			oCorectedDataMod.req[0].glAccountCode = oCorrectedData.glAccountCode;
		// 			oCorectedDataMod.req[0].accountVoucherNo = oCorrectedData.accountVoucherNo;
		// 			oCorectedDataMod.req[0].accountVoucherDate = oCorrectedData.accountVoucherDate;
		// 			var lengthofCorrectedDataItems = oCorrectedData.lineItems.length;
		// 			for (var i = 0; i < lengthofCorrectedDataItems; i++) {
		// 				oCorectedDataMod.req[0].lineItems.push({
		// 					//"id": oCorrectedData.lineItems[i].id,
		// 					"itemNo": oCorrectedData.lineItems[i].itemNo,
		// 					"hsnsacCode": oCorrectedData.lineItems[i].hsnsacCode,
		// 					"itemDesc": oCorrectedData.lineItems[i].itemDesc,
		// 					"itemType": oCorrectedData.lineItems[i].itemType,
		// 					"itemUqc": oCorrectedData.lineItems[i].itemUqc,
		// 					"itemQty": oCorrectedData.lineItems[i].itemQty,
		// 					"supplyType": oCorrectedData.lineItems[i].supplyType,
		// 					"taxableVal": oCorrectedData.lineItems[i].taxableVal,
		// 					//"taxRate": oCorrectedData.lineItems[i].taxRate,
		// 					"igstRate": oCorrectedData.lineItems[i].igstRate,
		// 					"sgstRate": oCorrectedData.lineItems[i].cgstRate,
		// 					"cgstRate": oCorrectedData.lineItems[i].sgstRate,
		// 					"igstAmt": oCorrectedData.lineItems[i].igstAmt,
		// 					"cgstAmt": oCorrectedData.lineItems[i].cgstAmt,
		// 					"sgstAmt": oCorrectedData.lineItems[i].sgstAmt,
		// 					"cessRateSpecific": oCorrectedData.lineItems[i].cessRateSpecific,
		// 					"cessAmtSpecfic": oCorrectedData.lineItems[i].cessAmtSpecfic,
		// 					"cessRateAdvalorem": oCorrectedData.lineItems[i].cessRateAdvalorem,
		// 					"cessAmtAdvalorem": oCorrectedData.lineItems[i].cessAmtAdvalorem,
		// 					"productCode": oCorrectedData.lineItems[i].productCode
		// 				});
		// 			}
		// 			/*           oCorectedDataMod.req[0]. = oCorrectedData.;
		// 			                oCorectedDataMod.req[0]. = oCorrectedData.;*/
		// 			var dataForSave = JSON.stringify(oCorectedDataMod);
		// 			//JSON.parse(dataForSave.replace(/([{,])(\s*)([A-Za-z0-9_\-]+?)\s*:/g, '$1"$3":'))
		// 			var sUrl = "/aspsapapi/saveDoc.do";
		// 			$(document).ready(function ($) {
		// 				$.ajax({
		// 					method: "POST",
		// 					url: sUrl,
		// 					contentType: "application/json",
		// 					data: dataForSave
		// 				}).done(function (data, status, jqXHR) {
		// 					var colForHeaderSuc, colForHeader, newErr;
		// 					var lineItemsBeforeCorrected = that.byId("tabErrorGstn").getModel("InvoiceModel").oData;
		// 					if (data.resp[0].errors.length === 0) {
		// 						//that.getView().byId("editButtonInvoice").setEnabled(false);
		// 						that.getView().byId("bSaveASP").setEnabled(false);
		// 						sap.m.MessageBox.show("Saved Successfully", {
		// 							icon: sap.m.MessageBox.Icon.SUCCESS,
		// 							title: "Success",
		// 							actions: [sap.m.MessageBox.Action.CLOSE],
		// 							id: "messageBoxId1",
		// 							styleClass: this.bCompact ? "sapUiSizeCompact" : "",
		// 							contentWidth: "100px"
		// 						});
		// 						var aMockMessages = [];
		// 						aMockMessages.push({
		// 							type: "Success",
		// 							counter: 0
		// 						});
		// 						var newoModel = new JSONModel();
		// 						newoModel.setData(aMockMessages);

		// 						var viewModelwithoutErr = new JSONModel();
		// 						viewModelwithoutErr.setData({
		// 							errMessagesLength: 0 + ""
		// 						});
		// 						that.getView().setModel(viewModelwithoutErr);
		// 						oMessagePopover.setModel(newoModel);
		// 						for (var t = 0; t < lineItemsBeforeCorrected.length; t++) {
		// 							if (lineItemsBeforeCorrected[t].id === data.resp[0].oldId) {
		// 								oCorrectedData.id = data.resp[0].id;
		// 								newErr = lineItemsBeforeCorrected[t].errorList;
		// 								for (var p = 0; p < newErr.length; p++) {
		// 									var errorFild = newErr[p].errorFields;
		// 									var arrayerrorFilds = errorFild.split(",");
		// 									if (newErr[p]["index"] === undefined) {
		// 										colForHeader = that.byId("tableForInvCorGstinErr").getRows()[0].getCells();
		// 										for (var h = 3; h < colForHeader.length; h++) {
		// 											colForHeader[h].setEnabled(false);
		// 											for (var q = 0; q < arrayerrorFilds.length; q++) {

		// 												if (colForHeader[h].getName() === arrayerrorFilds[q]) {
		// 													colForHeader[h].setValueState(sap.ui.core.ValueState.Success);
		// 												}
		// 											}
		// 										}
		// 									} else {
		// 										var lineItemNum = newErr[p].index;
		// 										colForHeader = that.byId("tableForInvCorGstinErr").getRows()[lineItemNum].getCells();
		// 										for (var j = 3; j < colForHeader.length; j++) {
		// 											colForHeader[j].setEnabled(false);
		// 											for (var m = 0; m < arrayerrorFilds.length; m++) {

		// 												if (colForHeader[j].getName() === arrayerrorFilds[m]) {
		// 													colForHeader[j].setValueState(sap.ui.core.ValueState.Success);
		// 												}
		// 											}
		// 										}
		// 									}
		// 								}

		// 							}
		// 						}
		// 					} else {
		// 						var JSONErrors = data.resp[0].errors;
		// 						var countOfErrors = JSONErrors.length;
		// 						sap.m.MessageBox.show("Saved With " + countOfErrors + " Errors", {
		// 							icon: sap.m.MessageBox.Icon.ERROR,
		// 							title: "Error",
		// 							actions: [sap.m.MessageBox.Action.CLOSE],
		// 							id: "messageBoxId1",
		// 							details: JSONErrors,
		// 							styleClass: this.bCompact ? "sapUiSizeCompact" : "",
		// 							contentWidth: "100px"
		// 						});
		// 						//Popover
		// 						var aMessagesWithErrors = [];
		// 						for (var x = 0; x < JSONErrors.length; x++) {
		// 							aMessagesWithErrors.push({
		// 								type: "Error",
		// 								title: JSONErrors[x].errorCode,
		// 								active: true,
		// 								description: JSONErrors[x].errorDesc,
		// 								subtitle: JSONErrors[x].errorDesc
		// 							});
		// 						}
		// 						var newModelWithErr = new JSONModel();
		// 						newModelWithErr.setData(aMessagesWithErrors);
		// 						oMessagePopover.setModel(newModelWithErr);

		// 						var viewModelwithErr = new JSONModel();
		// 						viewModelwithErr.setData({
		// 							errMessagesLength: JSONErrors.length + ""
		// 						});
		// 						that.getView().setModel(viewModelwithErr);

		// 						//Popover
		// 					}
		// 					//	this._createInvoiceCorrectionModel(that);

		// 					for (var tl = 0; tl < lineItemsBeforeCorrected.length; tl++) {
		// 						if (lineItemsBeforeCorrected[tl].id === data.resp[0].oldId) {
		// 							oCorrectedData.id = data.resp[0].id;
		// 							newErr = data.resp[0].errors;
		// 							var arrayofErr = [];
		// 							var newArraySuccess = [];
		// 							var oldErr = lineItemsBeforeCorrected[tl].errorList;
		// 							for (var f = 0; f < oldErr.length; f++) {
		// 								for (var v = 0; v < newErr.length; v++) {
		// 									if (oldErr[f].errorCode !== newErr[v].errorCode) {
		// 										if (!arrayofErr.includes(oldErr[f].errorCode)) {
		// 											arrayofErr.push(oldErr[f].errorCode);
		// 											newArraySuccess.push(oldErr[f]);
		// 										}
		// 									}
		// 								}
		// 								//return arrayofErr;
		// 							}
		// 							for (var qs = 0; qs < newArraySuccess.length; qs++) {
		// 								var errorFildSuc = newArraySuccess[qs].errorFields;
		// 								var arrayerrorFildsSuc = errorFildSuc.split(",");
		// 								if (newArraySuccess[qs]["index"] === undefined) {
		// 									colForHeaderSuc = that.byId("tableForInvCorGstinErr").getRows()[0].getCells();
		// 									for (var hs = 3; hs < colForHeaderSuc.length; hs++) {
		// 										for (var pt = 0; pt < arrayerrorFildsSuc.length; pt++) {
		// 											if (colForHeaderSuc[hs].getName() === arrayerrorFildsSuc[pt]) {
		// 												colForHeaderSuc[hs].setValueState(sap.ui.core.ValueState.Success);
		// 											}
		// 										}
		// 									}
		// 								} else {
		// 									var lineItemNumSuc = newArraySuccess[qs].index;
		// 									colForHeaderSuc = that.byId("tableForInvCorGstinErr").getRows()[lineItemNumSuc].getCells();
		// 									for (var js = 3; js < colForHeaderSuc.length; js++) {
		// 										for (var ms = 0; ms < arrayerrorFildsSuc.length; ms++) {
		// 											if (colForHeaderSuc[js].getName() === arrayerrorFildsSuc[ms]) {
		// 												colForHeaderSuc[js].setValueState(sap.ui.core.ValueState.Success);
		// 											}
		// 										}
		// 									}
		// 								}
		// 							}
		// 							for (var pa = 0; pa < newErr.length; pa++) {
		// 								errorFild = newErr[pa].errorFields;
		// 								var errorDescInResp = newErr[pa].errorDesc;
		// 								arrayerrorFilds = errorFild.split(",");
		// 								if (newErr[pa]["index"] === undefined) {
		// 									colForHeader = that.byId("tableForInvCorGstinErr").getRows()[0].getCells();
		// 									for (var ha = 3; ha < colForHeader.length; ha++) {
		// 										for (var qa = 0; qa < arrayerrorFilds.length; qa++) {
		// 											if (colForHeader[ha].getName() === arrayerrorFilds[qa]) {
		// 												colForHeader[ha].setValueState(sap.ui.core.ValueState.Error);
		// 												colForHeader[ha].setValueStateText(errorDescInResp);
		// 											}
		// 										}
		// 									}
		// 								} else {
		// 									lineItemNum = newErr[pa].index;
		// 									colForHeader = that.byId("tableForInvCorGstinErr").getRows()[lineItemNum].getCells();
		// 									for (var ja = 3; ja < colForHeader.length; ja++) {
		// 										for (var ma = 0; ma < arrayerrorFilds.length; ma++) {
		// 											if (colForHeader[ja].getName() === arrayerrorFilds[ma]) {
		// 												colForHeader[ja].setValueState(sap.ui.core.ValueState.Error);
		// 												colForHeader[ja].setValueStateText(errorDescInResp);
		// 											}
		// 										}
		// 									}
		// 								}
		// 							}
		// 						}
		// 					}
		// 				}).fail(function (jqXHR, status, err) {
		// 					//alert("Failed");
		// 				});
		// 			}); //end
		// 		}
	};
});