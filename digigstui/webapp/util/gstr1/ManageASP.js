sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/m/MessagePopover",
	"com/ey/digigst/util/Formatter",
	"com/ey/digigst/util/gstr1/ConfigAsp",
	"com/ey/digigst/util/gstr1/configCorrectedInvoice",
	"sap/m/MessageItem"
], function (JSONModel, MessageBox, MessageToast, MessagePopover, Formatter, ConfigAsp, configCorrectedInvoice, MessageItem) {
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

		_rowSelectionEvent: function (oEvent, controller) {
			var vIndex = oEvent.getSource().getSelectedIndex();
			var vPath = oEvent.getParameters().rowContext.sPath;
			var arr = vPath.split("/");
			if (arr.length > 2) {
				oEvent.getSource().removeSelectionInterval(vIndex, vIndex);
			}
		},

		/*================================================================================*/
		/*========= Get ASP Management Details ===========================================*/
		/*================================================================================*/
		_fileType: function () {
			return ["B2B", "B2BA", "B2CL", "B2CLA", "EXP", "EXPA"];
		},

		_infoStruct: function () {
			return {
				infoList: "",
				infoDesc: ""
			};
		},

		/*================================================================================*/
		/*========= Get ASP Management Details ===========================================*/
		/*================================================================================*/
		_stagingColumn: function (oEvent, controller) {
			debugger; //eslint-disable-line
			if (!this._oDialog) {
				this._oDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.StagingTable", controller);
				this._oDialog.setModel(controller.getView().getModel("ColVisiAsp"));
			}
			// 			var obj = ConfigAsp.getObject();
			// 			for (var vField in obj) {
			// 				if (vField === "lineItems") {
			// 					break;
			// 				}
			// 				obj[vField] = "true";
			// 			}
			// 			var oModel = new JSONModel();
			// 			controller.getView().setModel(oModel, "AspColVisi");

			// toggle compact style
			jQuery.sap.syncStyleClass("sapUiSizeCompact", controller.getView(), this._oDialog);
			this._oDialog.open();
		},

		/*================================================================================*/
		/*========= Get ASP Management Details ===========================================*/
		/*================================================================================*/
		_getAspManageDetails: function (controller) {
			if (controller.byId("sbManage").getSelectedKey() === "ProcessASP") {
				controller.byId("inPageNo").setValue("");
				controller.table = controller.section = "";
				this._getProcessedData(controller, false);

			} else if (controller.byId("sbManage").getSelectedKey() === "ErrorASP") {
				this._getErrorData(controller);
			}
		},

		/*================================================================================*/
		/*========= Segmented Button =====================================================*/
		/*================================================================================*/
		_segmentManage: function (oEvent, controller) {
			var oSegmentBtn = oEvent.getSource().getSelectedKey();
			this._handleSegmentBtn(oSegmentBtn, controller);
		},

		_handleSegmentBtn: function (oSegmentBtn, controller) {
			if (oSegmentBtn === "ErrorASP") {
				var vTab = "tabErrorASP",
					vModel = "InvoiceModel",
					vFlag = false;

			} else {
				vTab = "tabProcessASP";
				vModel = "AspProcessModel";
				vFlag = true;
				controller.table = controller.section = "";
			}
			controller.byId("bBackASP").setVisible(false);
			controller.byId("bSaveASP").setVisible(false);
			controller.byId("bSaveASPProcessed").setVisible(false);
			controller.byId("bProcessedInvErrors").setVisible(false);
			controller.byId("bInvErrors").setVisible(false);

			controller.byId("bEditASP").setVisible(vFlag);
			// 			controller.byId("bAddASP").setVisible(vFlag);
			controller.byId("bDelASP").setVisible(vFlag);

			controller.byId("tabProcessASP").setVisible(vFlag);
			controller.byId("tabErrorASP").setVisible(!vFlag);
			controller.byId("iErrToolbar").setVisible(!vFlag);
			controller.byId("GstinStatusFil").setVisible(vFlag);

			controller.byId("tableForProcessedEdit").setVisible(false);
			controller.byId("tabAspErrorItem").setVisible(false);

			var oModel = controller.byId(vTab).getModel(vModel);
			if (!oModel && oSegmentBtn === "ErrorASP") {
				this._getErrorData(controller);
			} else if (!oModel) {
				this._getProcessedData(controller, false);
			}
		},

		/*================================================================================*/
		/*========= Navigate Back to Main Screen =========================================*/
		/*================================================================================*/
		_navBack: function (oEvent, controller) {
			var vButton = controller.getView().byId("sbManage").getSelectedKey();
			controller.table = controller.section = "";
			controller.byId("bBackASP").setVisible(false);
			controller.byId("bSaveASP").setVisible(false);
			controller.byId("bSaveASPProcessed").setVisible(false);

			if (vButton === "ProcessASP") {
				var vFlag = true;
				this._getProcessedData(controller, false);
			} else {
				vFlag = false;
				this._getErrorData(controller);
			}
			// 			controller.byId("bAddASP").setVisible(vFlag);
			controller.byId("iErrToolbar").setVisible(!vFlag);
			controller.byId("bEditASP").setVisible(vFlag);
			controller.byId("bDelASP").setVisible(vFlag);
			controller.byId("GstinStatusFil").setVisible(vFlag);
		},

		/*================================================================================*/
		/*======== On Change Data Category ===============================================*/
		/*================================================================================*/
		_changeDataCategory: function (oEvent, controller) {
			// 			var oSource = oEvent.getSource();
		},

		/*================================================================================*/
		/*======== Pagination ============================================================*/
		/*================================================================================*/
		_aspPagination: function (oEvent, controller) {
			var vSB = controller.byId("sbManage").getSelectedKey();
			if (vSB === "ProcessASP") {
				var vInput = "inPageNo",
					vBtnPrev = "btnPrev",
					vBtnNext = "btnNext",
					txtPageNo = "txtPageNo";
			} else {
				vInput = "inPageNoErr";
				vBtnPrev = "btnPrevErr";
				vBtnNext = "btnNextErr";
				txtPageNo = "txtPageNoErr";
			}
			var oInput = controller.byId(vInput);
			var vValue = parseInt(oInput.getValue(), 10);
			if (oEvent.getSource().getId().indexOf(vBtnPrev) > -1) {
				vValue -= 1;
				if (vValue === 1) {
					controller.byId(vBtnPrev).setEnabled(false);
				}
				if (!controller.byId(vBtnNext).getEnabled()) {
					controller.byId(vBtnNext).setEnabled(true);
				}
			} else {
				var vPageNo = parseInt(controller.byId(txtPageNo).getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					controller.byId(vBtnPrev).setEnabled(true);
				}
				if (vValue === vPageNo) {
					controller.byId(vBtnNext).setEnabled(false);
				}
			}
			oInput.setValue(vValue);
			if (vSB === "ProcessASP") {
				this._getProcessedData(controller, false);
			} else {
				this._getErrorData(controller);
			}
		},

		_aspSubmitPage: function (oEvent, controller) {
			this._getProcessedData(controller, false);
		},

		/*================================================================================*/
		/*======== Search Criteria to get data from AJAX Call ============================*/
		/*================================================================================*/
		_searchInfo: function (controller, status) {
			var vCriteria = controller.byId("slManageCriteria").getSelectedKey();
			var aEntity = controller.byId("slManageEntity").getSelectedKeys();
			var aGstins = controller.byId("slManageGSTIN").getSelectedKeys();
			var vDocNo = controller.byId("inManageDocNo").getValue();
			var vGstnStats = controller.byId("slManageGSTNStatus").getSelectedKeys();
			if (controller.byId("sbManage").getSelectedKey() === "ProcessASP") {
				var vPageNo = controller.byId("inPageNo").getValue();
			} else {
				vPageNo = controller.byId("inPageNoErr").getValue();
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
					"gstnStatus": vGstnStats,
					"processingStatus": status,
					"fileId": controller.file
				}
			};
			if (vDocNo !== "") {
				searchInfo.req.docNo = vDocNo;
			}
			switch (vCriteria) {
			case "RECEIVED_DATE_SEARCH":
				searchInfo.req.receivFromDate = controller._formatDate(controller.byId("drsManage").getDateValue());
				searchInfo.req.receivToDate = controller._formatDate(controller.byId("drsManage").getSecondDateValue());
				break;
			case "DOCUMENT_DATE_SEARCH":
				searchInfo.req.docFromDate = controller._formatDate(controller.byId("drsManage").getDateValue());
				searchInfo.req.docToDate = controller._formatDate(controller.byId("drsManage").getSecondDateValue());
				break;
			case "RETURN_DATE_SEARCH":
				searchInfo.req.returnFromDate = (controller._formatPeriod(controller.byId("drsManage").getDateValue())).substr(2, 6);
				searchInfo.req.returnToDate = (controller._formatPeriod(controller.byId("drsManage").getSecondDateValue())).substr(2, 6);
				break;
			}
			return searchInfo;
		},

		/*================================================================================*/
		/*======== ASP Process AJAX Call to get Data =====================================*/
		/*================================================================================*/
		_getProcessedData: function (controller, vInfo) {
			var that = this;
			that.info = vInfo;
			if (vInfo) {
				var stats = "I";
			} else {
				stats = "P";
			}
			controller.byId("bSaveASP").setVisible(false);
			controller.byId("bSaveASPProcessed").setVisible(false);
			controller.byId("GstinStatusFil").setVisible(true);
			controller.byId("bEditASP").setVisible(true);
			// 			controller.byId("bAddASP").setVisible(true);
			controller.byId("bDelASP").setVisible(true);

			if (controller.byId("sbManage").getSelectedKey() === "ProcessASP") {
				controller.getView().byId("tabProcessASP").setVisible(true);
				controller.getView().byId("tableForProcessedEdit").setVisible(false);
				controller.getView().byId("tabAspErrorItem").setVisible(false);
				controller.getView().byId("tabErrorASP").setVisible(false);
				controller.getView().byId("iErrToolbar").setVisible(false);
			}
			var bCompact = !!controller.getView().$().closest(".sapUiSizeCompact").length;
			var searchInfo = that._searchInfo(controller, stats);
			controller.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/docSearch.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var vPageNo = Math.ceil(data.hdr.totalCount / 50);
					controller.byId("txtPageNo").setText("/ " + vPageNo);
					if (vPageNo === 0) {
						controller.byId("inPageNo").setValue(0);
						controller.byId("inPageNo").setEnabled(false);
						controller.byId("btnPrev").setEnabled(false);
						controller.byId("btnNext").setEnabled(false);

					} else if (controller.byId("inPageNo").getValue() === "" || controller.byId("inPageNo").getValue() === "0") {
						controller.byId("inPageNo").setValue(1);
						controller.byId("inPageNo").setEnabled(true);
						controller.byId("btnPrev").setEnabled(false);
					}
					var vInPage = parseInt(controller.byId("inPageNo").getValue(), 10);
					if (vPageNo > 1 && vPageNo !== vInPage) {
						controller.byId("btnNext").setEnabled(true);
					} else {
						controller.byId("btnNext").setEnabled(false);
					}
					if (vInPage > 1) {
						controller.byId("btnPrev").setEnabled(true);
					}

					var aProcessData = [];
					for (var z = 0; z < data.resp.length; z++) {
						var oData = [];
						aProcessData.push(data.resp[z]);
					}
					if (aProcessData.length === 0) {
						that._infoMessage("Processed records does not exist.");
					} else {
						oData = that._parseJsonASP(aProcessData); //that._processJSON(aProcessData);
					}
					aProcessData.sort(function (a, b) {
						return a.docNo - b.docNo;
					});
					var oAspProcessedModel = new JSONModel();
					oAspProcessedModel.setData(oData);
					if (!oData) {
						controller.byId("txtRecords").setText("Records: 0");
					} else {
						controller.byId("txtRecords").setText("Records: " + oData.length);
					}
					controller.byId("tabProcessASP").setModel(oAspProcessedModel, "AspProcessModel");

				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
					if (jqXHR.status === 500) {
						MessageBox.information("Incorrect Filter Data/Some Mandatory field is missing.", {
							styleClass: bCompact ? "sapUiSizeCompact" : ""
						});
					} else if (jqXHR.status === 503) {
						MessageBox.information("Service Unavailable", {
							styleClass: bCompact ? "sapUiSizeCompact" : ""
						});
					} else if (jqXHR.status === 504) {
						MessageBox.information("Gateway Timeout.", {
							styleClass: bCompact ? "sapUiSizeCompact" : ""
						});
					}
				});
			});
		},

		/*================================================================================*/
		/*======== Parse return data to JSON Format ======================================*/
		/*================================================================================*/
		_infoList: function (data, oInfoCode) {
			if (data.errorList) {
				var infoCodes = [],
					infoDesc = [];
				for (var i = 0; i < data.errorList.length; i++) {
					infoCodes.push(data.errorList[i].errorCode);
					infoDesc.push(data.errorList[i].errorDesc.trim());
				}
				oInfoCode.infoList = infoCodes.toString();
				oInfoCode.infoDesc = infoDesc.toString();
			}
		},

		/*================================================================================*/
		/*======== Parse return data to JSON Format ======================================*/
		/*================================================================================*/
		// 		_processJSON: function (aProcessData) {
		// 			var vIndex = 0,
		// 				infoList = "",
		// 				infoDesc = "",
		// 				invProcessedHedData = [];
		// 			for (var v = 0; v < aProcessData.length; v++) {
		// 				// var infoList = this._infoList(aProcessData[v]);
		// 				this._infoList(aProcessData[v], infoList, infoDesc);

		// 				aProcessData[v].crDrPreGst = aProcessData[v].crDrPreGst ? "Y" : "N";
		// 				aProcessData[v].tcsFlag = aProcessData[v].tcsFlag ? "Y" : "N";
		// 				aProcessData[v].itcFlag = aProcessData[v].itcFlag ? "Y" : "N";

		// 				invProcessedHedData.push(this._headerJSON(aProcessData[v], infoList, infoDesc));
		// 				invProcessedHedData[vIndex].sno = vIndex + 1;

		// 				for (var i = 0; i < aProcessData[v].lineItems.length; i++) {
		// 					invProcessedHedData[vIndex].lineItems.push(this._itemJSON(aProcessData[v], i));
		// 				}
		// 				vIndex++;
		// 			}
		// 			return invProcessedHedData;
		// 		},

		/*================================================================================*/
		/*========= Format header & item data in JSON structure ==========================*/
		/*================================================================================*/
		// 		_headerJSON: function (data, infoList, infoDesc) {
		// 			return {
		// 				"sno": "",
		// 				"supplierGstin": data.supplierGstin,
		// 				"id": data.id,
		// 				"fiYear": data.fiYear,
		// 				"returnPeriod": data.returnPeriod,
		// 				"supplyType": data.supplyType,
		// 				"docType": data.docType,
		// 				"docNo": data.docNo,
		// 				"docDate": data.docDate,
		// 				"docAmount": data.docAmount,
		// 				"originalDocNo": data.originalDocNo,
		// 				"originalDocDate": data.originalDocDate,
		// 				"gstr1SubCategory": data.gstr1SubCategory,
		// 				"gstr1TableNo": data.gstr1TableNo,
		// 				"uinOrComposition": data.uinOrComposition,
		// 				"custGstin": data.custGstin,
		// 				"custOrSuppName": data.custOrSuppName,
		// 				"shipToState": data.shipToState,
		// 				"billToState": data.billToState,
		// 				"pos": data.pos,
		// 				"shipPortCode": data.shipPortCode,
		// 				"reverseCharge": data.reverseCharge,
		// 				"ecomCustGSTIN": data.ecomCustGSTIN,
		// 				"dataOriginTypeCode": data.dataOriginTypeCode,
		// 				"tcsFlag": data.tcsFlag,
		// 				"itcFlag": data.itcFlag,
		// 				"custOrSuppCode": data.custOrSuppCode,
		// 				"crDrReason": data.crDrReason,
		// 				"fob": data.fob,
		// 				"shippingBillNo": data.shippingBillNo,
		// 				"shippingBillDate": data.shippingBillDate,
		// 				"glAccountCode": data.glAccountCode,
		// 				"accountVoucherNo": data.accountVoucherNo,
		// 				"accountVoucherDate": data.accountVoucherDate,
		// 				"crDrPreGst": data.crDrPreGst,
		// 				"infoList": infoList,
		// 				"infoDesc": infoDesc,
		// 				"lineItems": []
		// 			};
		// 		},

		// 		_itemJSON: function (data, i) {
		// 			return {
		// 				"supplierGstin": data.supplierGstin,
		// 				"fiYear": data.fiYear,
		// 				"returnPeriod": data.returnPeriod,
		// 				"supplyType": data.supplyType,
		// 				"docType": data.docType,
		// 				"docNo": data.docNo,
		// 				"docDate": data.docDate,
		// 				"docAmount": data.docAmount,
		// 				"originalDocNo": data.originalDocNo,
		// 				"originalDocDate": data.originalDocDate,
		// 				"gstr1SubCategory": data.gstr1SubCategory,
		// 				"gstr1TableNo": data.gstr1TableNo,
		// 				"uinOrComposition": data.uinOrComposition,
		// 				"custGstin": data.custGstin,
		// 				"custOrSuppName": data.custOrSuppName,
		// 				"shipToState": data.shipToState,
		// 				"billToState": data.billToState,
		// 				"pos": data.pos,
		// 				"shipPortCode": data.shipPortCode,
		// 				"reverseCharge": data.reverseCharge,
		// 				"ecomCustGSTIN": data.ecomCustGSTIN,
		// 				"dataOriginTypeCode": data.dataOriginTypeCode,
		// 				"tcsFlag": data.tcsFlag,
		// 				"itcFlag": data.itcFlag,
		// 				"custOrSuppCode": data.custOrSuppCode,
		// 				"fob": data.fob,
		// 				"shippingBillNo": data.shippingBillNo,
		// 				"shippingBillDate": data.shippingBillDate,
		// 				"glAccountCode": data.glAccountCode,
		// 				"accountVoucherNo": data.accountVoucherNo,
		// 				"accountVoucherDate": data.accountVoucherDate,

		// 				"id": data.lineItems[i].id,
		// 				"itemNo": data.lineItems[i].itemNo,
		// 				"hsnsacCode": data.lineItems[i].hsnsacCode,
		// 				"itemDesc": data.lineItems[i].itemDesc,
		// 				"itemType": data.lineItems[i].itemType,
		// 				"itemUqc": data.lineItems[i].itemUqc,
		// 				"itemQty": data.lineItems[i].itemQty,
		// 				"igstRate": data.lineItems[i].igstRate,
		// 				"sgstRate": data.lineItems[i].cgstRate,
		// 				"cgstRate": data.lineItems[i].sgstRate,
		// 				"igstAmt": data.lineItems[i].igstAmt,
		// 				"cgstAmt": data.lineItems[i].cgstAmt,
		// 				"sgstAmt": data.lineItems[i].sgstAmt,
		// 				"taxableVal": data.lineItems[i].taxableVal,
		// 				"cessRateSpecific": data.lineItems[i].cessRateSpecific,
		// 				"cessAmtSpecfic": data.lineItems[i].cessAmtSpecfic,
		// 				"cessRateAdvalorem": data.lineItems[i].cessRateAdvalorem,
		// 				"cessAmtAdvalorem": data.lineItems[i].cessAmtAdvalorem,
		// 				"productCode": data.lineItems[i].productCode,
		// 				"lineItemAmt": data.lineItems[i].lineItemAmt
		// 			};
		// 		},

		/*================================================================================*/
		/*========= Edit ASP Process Records =============================================*/
		/*================================================================================*/
		_editAspProcessedRec: function (controller, oEvent) {
			var rowIndexSelected = controller.byId("tabProcessASP").getSelectedIndex();
			if (rowIndexSelected === -1) {
				this._errorMessage("Please select one record to edit");
				return;
			}
			var processedDataForEdit = controller.byId("tabProcessASP").getContextByIndex(rowIndexSelected).getObject();
			var oInfoCode = this._infoStruct();
			var oData = this._headerAspJson(processedDataForEdit, oInfoCode, "Invoice");
			for (var i = 0; i < processedDataForEdit.lineItems.length; i++) {
				var vFlag = false,
					aFileType = this._fileType();

				for (var j = 0; j < aFileType.length; j++) {
					if (processedDataForEdit.gstr1SubCategory === aFileType[j]) {
						vFlag = true;
						break;
					}
				}
				if (vFlag) {
					for (j = 0; j < processedDataForEdit.lineItems[i].lineItems.length; j++) {
						oData.lineItems.push(processedDataForEdit.lineItems[i].lineItems[j]);
					}
				} else {
					oData.lineItems.push(processedDataForEdit.lineItems[i]);
				}
			}
			if (processedDataForEdit.sno !== "Invoice") {
				this._errorMessage("Please select Invoice records");
				return;
			}
			controller.byId("bBackASP").setVisible(true);
			controller.byId("bSaveASPProcessed").setVisible(true);
			controller.byId("bEditASP").setVisible(false);
			controller.byId("bAddASP").setVisible(false);
			controller.byId("bDelASP").setVisible(false);
			controller.byId("bSaveASP").setVisible(false);
			controller.byId("tabProcessASP").setVisible(false);
			controller.byId("tabErrorASP").setVisible(false);
			controller.byId("tableForProcessedEdit").setVisible(true);
			controller.byId("tabAspErrorItem").setVisible(false);

			var oProcessedModelForItems = new JSONModel(oData);
			controller.byId("tableForProcessedEdit").setModel(oProcessedModelForItems, "processdItemsModel");
		},

		/*================================================================================*/
		/*========= Change of ASP Processed Edit Records =================================*/
		/*================================================================================*/
		_changeEditData: function (oEvent, controller) {
			var obj = oEvent.getSource().getBindingContext("processdItemsModel").getObject();
			var oModel = controller.byId("tableForProcessedEdit").getModel("processdItemsModel");
			var oData = oModel.getData();

			this._changeAspData(oData, obj);
			for (var i = 0; i < oData.lineItems.length; i++) {
				this._changeAspData(oData.lineItems[i], obj);
			}
			oModel.refresh(true);
		},

		_changeAspData: function (data, obj) {
			data.returnPeriod = obj.returnPeriod;
			data.docDate = obj.docDate;
			data.docAmount = obj.docAmount;
			data.supplyType = obj.supplyType;
			data.derivedTaxperiod = obj.derivedTaxperiod;
			data.originalDocNo = obj.originalDocNo;
			data.originalDocDate = obj.originalDocDate;
			data.shippingBillNo = obj.shippingBillNo;
			data.shippingBillDate = obj.shippingBillDate;
			data.billToState = obj.billToState;
			data.shipToState = obj.shipToState;
			data.accountVoucherNo = obj.accountVoucherNo;
			data.accountVoucherDate = obj.accountVoucherDate;
		},

		/*================================================================================*/
		/*========= Save Edit Processed Records ==========================================*/
		/*================================================================================*/
		_saveEditAspProcessedRec: function (oEvent, controller) {
			var that = this;
			var oBundle = controller.getOwnerComponent().getModel("i18n").getResourceBundle();
			MessageBox.confirm(oBundle.getText("config.errorCorrection.confirmationMsg"), {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === "OK") {
						that._saveProcessedRec(controller);
					} else {
						controller._reset();
					}
				}
			});
		},

		/*================================================================================*/
		/*========= JSON for header & item for Save to GSTN operation ====================*/
		/*================================================================================*/
		_jsonSaveHeader: function (data, controller) {
			var oData = controller.getView().getModel("CreateInvoiceOrgModel").getData();
			var aData = oData.req[0];

			aData.id = data.id;
			aData.supplierGstin = data.supplierGstin;
			aData.fiYear = data.fiYear;
			aData.returnPeriod = data.returnPeriod;
			aData.gstr1SubCategory = data.gstr1SubCategory;
			aData.gstr1TableNo = data.gstr1TableNo;
			aData.supplyType = data.supplyType;
			aData.docType = data.docType;
			aData.docNo = data.docNo;
			aData.docDate = data.docDate;
			aData.docAmount = data.docAmount;
			aData.originalDocNo = data.originalDocNo;
			aData.originalDocDate = data.originalDocDate;
			aData.uinOrComposition = data.uinOrComposition;
			aData.custGstin = data.custGstin;
			aData.custOrSuppCode = data.custOrSuppCode;
			aData.custOrSuppName = data.custOrSuppName;
			aData.shipToState = data.shipToState;
			aData.billToState = data.billToState;
			aData.pos = data.pos;
			aData.shipPortCode = data.shipPortCode;
			aData.reverseCharge = data.reverseCharge;
			aData.ecomCustGSTIN = data.ecomCustGSTIN;
			aData.dataOriginTypeCode = "U";
			aData.tcsFlag = data.tcsFlag;
			aData.itcFlag = data.itcFlag;
			aData.fob = data.fob;
			aData.shippingBillNo = data.shippingBillNo;
			aData.shippingBillDate = data.shippingBillDate;
			aData.accountVoucherNo = data.accountVoucherNo;
			aData.accountVoucherDate = data.accountVoucherDate;
			aData.crDrReason = data.crDrReason;
			aData.crDrPreGst = data.crDrPreGst;
			aData.exportDuty = data.exportDuty;
			aData.sourceIdentifier = data.sourceIdentifier;
			aData.sourceFileName = data.sourceFileName;
			aData.division = data.division;
			aData.subDivision = data.subDivision;
			aData.profitCentre1 = data.profitCentre1;
			aData.profitCentre2 = data.profitCentre2;
			aData.plantCode = data.plantCode;
			aData.derivedTaxperiod = data.derivedTaxperiod;
			aData.userDefinedField1 = data.userDefinedField1;
			aData.userDefinedField2 = data.userDefinedField2;
			aData.userDefinedField3 = data.userDefinedField3;
			// 			obj.portCode = data.portCode;
			return oData;
		},

		_jsonSaveItem: function (data) {
			return {
				"itemNo": data.itemNo,
				"hsnsacCode": data.hsnsacCode,
				"itemDesc": data.itemDesc,
				"itemType": data.itemType,
				"itemUqc": data.itemUqc,
				"itemQty": data.itemQty,
				"taxableVal": data.taxableVal,
				"supplyType": data.supplyType,
				"glAccountCode": data.glAccountCode,
				"igstRate": data.igstRate,
				"sgstRate": data.cgstRate,
				"cgstRate": data.sgstRate,
				"igstAmt": data.igstAmt,
				"cgstAmt": data.cgstAmt,
				"sgstAmt": data.sgstAmt,
				"cessRateSpecific": data.cessRateSpecific,
				"cessAmtSpecfic": data.cessAmtSpecfic,
				"cessRateAdvalorem": data.cessRateAdvalorem,
				"cessAmtAdvalorem": data.cessAmtAdvalorem,
				"productCode": data.productCode,
				"lineItemAmt": 0
			};
		},

		/*================================================================================*/
		/*========= Ajax Call to Save Edit Processed Records =============================*/
		/*================================================================================*/
		_saveProcessedRec: function (controller) {
			var that = this,
				countForMandatory = 0,
				oCorrectedData = controller.byId("tableForProcessedEdit").getModel("processdItemsModel").getData();

			oCorrectedData.crDrPreGst = oCorrectedData.crDrPreGst === "Y" ? true : false;
			oCorrectedData.tcsFlag = oCorrectedData.tcsFlag === "Y" ? true : false;
			oCorrectedData.itcFlag = oCorrectedData.itcFlag === "Y" ? true : false;

			for (var ma = 0; ma < oCorrectedData.lineItems.length; ma++) {
				if (oCorrectedData.returnPeriod === "" || oCorrectedData.supplyType === "" || oCorrectedData.docDate === "" ||
					oCorrectedData.lineItems[ma].itemNo === "" || oCorrectedData.lineItems[ma].hsnsacCode === "") {
					countForMandatory++;
				} else {
					countForMandatory = 0;
				}
			}
			if (countForMandatory > 0) {
				that._errorMessage("Mandatory Fields are Missing");
				return;
			}
			that._createInvoiceCorrectionModel(controller);
			var oCorectedDataMod = that._jsonSaveHeader(oCorrectedData, controller);

			for (var i = 0; i < oCorrectedData.lineItems.length; i++) {
				oCorectedDataMod.req[0].lineItems.push(that._jsonSaveItem(oCorrectedData.lineItems[i]));
			}
			controller.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/saveDoc.do",
					contentType: "application/json",
					data: JSON.stringify(oCorectedDataMod)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					controller.aspEdit = true;
					if (data.resp[0].errors.length === 0) {
						controller.byId("bProcessedInvErrors").setVisible(false);
						oCorrectedData.id = data.resp[0].id;
						that._successMessage("Saved Successfully");
					} else {
						that._successProcessSave(data, controller);
					}
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			});
		},

		_successProcessSave: function (data, controller) {
			var colForHeader,
				gstinJSONErrors = data.resp[0].errors,
				countOfErrors = gstinJSONErrors.length,
				oModelProcessEdit = controller.byId("tableForProcessedEdit").getModel("processdItemsModel"),
				oCorrectedData = oModelProcessEdit.getData();

			controller.byId("bProcessedInvErrors").setVisible(true);
			this._errorMessage("Saved With " + countOfErrors + " Errors");
			// 			Popover Messagae
			var aMessagesWithGstinErrors = [];
			for (var x = 0; x < gstinJSONErrors.length; x++) {
				aMessagesWithGstinErrors.push({
					type: "Error",
					title: gstinJSONErrors[x].errorCode,
					active: true,
					description: gstinJSONErrors[x].errorDesc,
					subtitle: gstinJSONErrors[x].errorDesc
				});
			}
			oMessagePopover.setModel(new JSONModel(aMessagesWithGstinErrors));

			var viewModelwithGstinErr = new JSONModel();
			viewModelwithGstinErr.setData({
				errMessagesLength: gstinJSONErrors.length + ""
			});
			controller.getView().setModel(viewModelwithGstinErr);

			// 			Popover Message
			this._createInvoiceCorrectionModel(controller);
			var editedDataWithErr = controller.getView().byId("tabProcessASP").getModel("AspProcessModel").getData();
			for (var t = 0; t < editedDataWithErr.length; t++) {
				if (editedDataWithErr[t].id === data.resp[0].oldId) {
					oCorrectedData.id = data.resp[0].id;
					var newErr = data.resp[0].errors,
						newArraySuccess = [];

					for (var v = 0; v < newErr.length; v++) {
						newArraySuccess.push(newErr[v]);
					}
					for (var p = 0; p < newErr.length; p++) {
						var errorFild = newErr[p].errorFields;
						var arrayerrorFilds = errorFild.split(",");
						if (newErr[p].index === undefined) {
							colForHeader = controller.byId("tableForProcessedEdit").getRows()[0].getCells();
						} else {
							var lineItemNum = newErr[p].index;
							colForHeader = controller.byId("tableForProcessedEdit").getRows()[lineItemNum].getCells();
						}
						this._setValueState(colForHeader, arrayerrorFilds);
					}
				}
			}
			oModelProcessEdit.refresh(true);
		},

		/*================================================================================*/
		/*======== Set ValueState of Input Fields ========================================*/
		/*================================================================================*/
		// 		_setValueState: function (colForHeader, errFields) {
		// 			if (errFields.errorType === "ERR") {
		// 				var aField = errFields.errorFields.split(",");
		// 				for (var i = 3; i < colForHeader.length; i++) {
		// 					if (aField.includes(colForHeader[i].getName())) {
		// 						colForHeader[i].setValueState(sap.ui.core.ValueState.Error);
		// 						colForHeader[i].setValueStateText(errFields.errorDesc);
		// 					}
		// 				}
		// 			}
		// 		},
		_setValueState: function (colForHeader, arrayerrorFilds) {
			for (var i = 0; i < arrayerrorFilds.length; i++) {
				for (var j = 3; j < colForHeader.length; j++) {
					if (colForHeader[j].getName() === arrayerrorFilds[i]) {
						colForHeader[j].setValueState(sap.ui.core.ValueState.Error);
					}
				}
			}
		},

		/*================================================================================*/
		/*========= ASP Error AJAX Call to get Data ======================================*/
		/*================================================================================*/
		_getErrorData: function (controller) {
			var that = this;
			controller.byId("GstinStatusFil").setVisible(false);
			controller.byId("bEditASP").setVisible(false);
			controller.byId("bAddASP").setVisible(false);
			controller.byId("bDelASP").setVisible(false);
			controller.byId("bInvErrors").setVisible(false);
			controller.byId("bSaveASP").setVisible(false);
			controller.byId("bSaveASPProcessed").setVisible(false);
			controller.byId("tabProcessASP").setVisible(false);
			controller.byId("tabErrorASP").setVisible(true);
			controller.byId("tableForProcessedEdit").setVisible(false);
			controller.byId("tabAspErrorItem").setVisible(false);

			var searchInfo = that._searchInfo(controller, "E");
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/docSearch.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				var vPageNo = Math.ceil(data.hdr.totalCount / 50);
				controller.byId("txtPageNoErr").setText("/ " + vPageNo);
				if (vPageNo === 0) {
					controller.byId("inPageNoErr").setValue(0);
					controller.byId("inPageNoErr").setEnabled(false);
					controller.byId("btnPrevErr").setEnabled(false);
					controller.byId("btnNextErr").setEnabled(false);

				} else if (controller.byId("inPageNoErr").getValue() === "" || controller.byId("inPageNoErr").getValue() === "0") {
					controller.byId("inPageNoErr").setValue(1);
					controller.byId("inPageNoErr").setEnabled(true);
					controller.byId("btnPrevErr").setEnabled(false);
				}
				var vInPage = parseInt(controller.byId("inPageNoErr").getValue(), 10);
				if (vPageNo > 1 && vPageNo !== vInPage) {
					controller.byId("btnNextErr").setEnabled(true);
				} else {
					controller.byId("btnNextErr").setEnabled(false);
				}

				var oInvoiceModel = new JSONModel();
				if (data.resp.length === 0) {
					oInvoiceModel.setData([]);
					that._infoMessage("No Errors in this record.");
				} else {
					oInvoiceModel.setData(that._errorJSON(data.resp));
				}
				controller.byId("tabErrorASP").setModel(oInvoiceModel, "InvoiceModel");
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
				if (jqXHR.status === 500) {
					that._infoMessage("Incorrect Filter Data/Some Mandatory field is missing.");
				} else if (jqXHR.status === 503) {
					that._infoMessage("Service Unavailable");
				} else if (jqXHR.status === 504) {
					that._infoMessage("Gateway Timeout.");
				}
			});
		},

		/*================================================================================*/
		/*========= For Correcting Errors in Detail ======================================*/
		/*================================================================================*/
		_detailErrToCorrect: function (controller, oEvent) {
			var oErrorItem = oEvent.getSource().getBindingContext("InvoiceModel").getObject();
			controller.byId("bBackASP").setVisible(true);
			controller.byId("bEditASP").setVisible(false);
			controller.byId("bSaveASP").setVisible(true);
			controller.byId("bSaveASP").setEnabled(true);
			controller.byId("bDelASP").setVisible(true);
			controller.byId("bInvErrors").setVisible(true);
			controller.byId("iErrToolbar").setVisible(false);
			controller.byId("tabProcessASP").setVisible(false);
			controller.byId("tabErrorASP").setVisible(false);
			controller.byId("tableForProcessedEdit").setVisible(false);
			controller.byId("tabAspErrorItem").setVisible(true);

			var oErrorModelForItems = new JSONModel();
			oErrorModelForItems.setData(oErrorItem);
			controller.byId("tabAspErrorItem").setModel(oErrorModelForItems, "itemsModel");

			// 			Message Popover
			var aErrorMsg = [];
			for (var i = 0; i < oErrorItem.errorList.length; i++) {
				aErrorMsg.push({
					type: oErrorItem.errorList[i].errorType === "ERR" ? "Error" : "Information",
					title: oErrorItem.errorList[i].errorCode,
					active: true,
					description: oErrorItem.errorList[i].errorDesc,
					subtitle: oErrorItem.errorList[i].errorDesc
				});
			}
			oMessagePopover.setModel(new JSONModel(aErrorMsg));

			var viewModel = new JSONModel();
			viewModel.setData({
				errMessagesLength: oErrorItem.errorList.length + ""
			});
			controller.getView().setModel(viewModel);

			// 			At Header Level
			for (i = 0; i < oErrorItem.errorList.length; i++) {
				if (oErrorItem.errorList[i].index >= 0) {
					var vIdx = oErrorItem.errorList[i].index;
					var colForHeader = controller.byId("tabAspErrorItem").getRows()[vIdx].getCells();
				} else {
					colForHeader = controller.byId("tabAspErrorItem").getRows()[0].getCells();
				}
				this._valueState(colForHeader, oErrorItem.errorList[i]);
			}
		},

		_valueState: function (colForHeader, errFields) {
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
		/*================================================================================*/
		/*================================================================================*/
		_popOverForErrors: function (oEvent, controller) {
			oMessagePopover.toggle(oEvent.getSource());
		},

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		_errorJSON: function (aDataError) {
			var indexForInv = 0,
				invHedData = [],
				arrayOfErrCode = [],
				arrayOfErrCodeLine = [];

			for (var v = 0; v < aDataError.length; v++) {
				var errorCodes = [],
					errorDesc = [];

				if (aDataError[v].errorList) {
					var errorsAvai = aDataError[v].errorList;
					for (var r = 0; r < errorsAvai.length; r++) {
						errorCodes.push(errorsAvai[r].errorCode);
						errorDesc.push(errorsAvai[r].errorDesc);
					}
				}
				var errCodeStr = errorCodes.toString(),
					errorDescStr = errorDesc.toString();

				aDataError[v].crDrPreGst = aDataError[v].crDrPreGst ? "Y" : "N";
				aDataError[v].tcsFlag = aDataError[v].tcsFlag ? "Y" : "N";
				aDataError[v].itcFlag = aDataError[v].itcFlag ? "Y" : "N";

				invHedData.push({
					"sno": indexForInv + 1,
					"supplierGstin": aDataError[v].supplierGstin,
					"id": aDataError[v].id,
					"fiYear": aDataError[v].fiYear,
					"returnPeriod": aDataError[v].returnPeriod,
					"supplyType": aDataError[v].supplyType,
					"docType": aDataError[v].docType,
					"docNo": aDataError[v].docNo,
					"docDate": aDataError[v].docDate,
					"docAmount": aDataError[v].docAmount,
					"originalDocNo": aDataError[v].originalDocNo,
					"originalDocDate": aDataError[v].originalDocDate,
					"uinOrComposition": aDataError[v].uinOrComposition,
					"custGstin": aDataError[v].custGstin,
					"custOrSuppName": aDataError[v].custOrSuppName,
					"shipToState": aDataError[v].shipToState,
					"billToState": aDataError[v].billToState,
					"pos": aDataError[v].pos,
					"shipPortCode": aDataError[v].shipPortCode,
					"reverseCharge": aDataError[v].reverseCharge,
					"ecomCustGSTIN": aDataError[v].ecomCustGSTIN,
					"dataOriginTypeCode": aDataError[v].dataOriginTypeCode,
					"tcsFlag": aDataError[v].tcsFlag,
					"itcFlag": aDataError[v].itcFlag,
					"custOrSuppCode": aDataError[v].custOrSuppCode,
					"fob": aDataError[v].fob,
					"shippingBillNo": aDataError[v].shippingBillNo,
					"shippingBillDate": aDataError[v].shippingBillDate,
					"glAccountCode": aDataError[v].glAccountCode,
					"accountVoucherNo": aDataError[v].accountVoucherNo,
					"accountVoucherDate": aDataError[v].accountVoucherDate,
					"crDrReason": "",
					"crDrPreGst": aDataError[v].crDrPreGst,

					"errCodeStr": errCodeStr,
					"errorDescStr": errorDescStr,
					"lineItems": [],
					"errorList": []
				});

				for (var i = 0; i < aDataError[v].lineItems.length; i++) {
					invHedData[indexForInv].lineItems.push({
						"id": aDataError[v].lineItems[i].id,
						"itemNo": aDataError[v].lineItems[i].itemNo,
						"hsnsacCode": aDataError[v].lineItems[i].hsnsacCode,
						"itemDesc": aDataError[v].lineItems[i].itemDesc,
						"itemType": aDataError[v].lineItems[i].itemType,
						"itemUqc": aDataError[v].lineItems[i].itemUqc,
						"itemQty": aDataError[v].lineItems[i].itemQty,
						"supplyType": aDataError[v].lineItems[i].supplyType,
						"taxableVal": aDataError[v].lineItems[i].taxableVal,
						"igstRate": aDataError[v].lineItems[i].igstRate,
						"sgstRate": aDataError[v].lineItems[i].cgstRate,
						"cgstRate": aDataError[v].lineItems[i].sgstRate,
						"igstAmt": aDataError[v].lineItems[i].igstAmt,
						"cgstAmt": aDataError[v].lineItems[i].cgstAmt,
						"sgstAmt": aDataError[v].lineItems[i].sgstAmt,
						"cessRateSpecific": aDataError[v].lineItems[i].cessRateSpecific,
						"cessAmtSpecfic": aDataError[v].lineItems[i].cessAmtSpecfic,
						"cessRateAdvalorem": aDataError[v].lineItems[i].cessRateAdvalorem,
						"cessAmtAdvalorem": aDataError[v].lineItems[i].cessAmtAdvalorem,
						"productCode": aDataError[v].lineItems[i].productCode
							// 		"errorCodeForLineItem": ""
					});
					if (aDataError[v].errorList) {
						this._errorList(aDataError[v].errorList, invHedData, indexForInv, arrayOfErrCode, arrayOfErrCodeLine);
					}
				}
				indexForInv++;
			}
			return invHedData;
		},

		_errorList: function (errorList, invHedData, indexForInv, arrayOfErrCode, arrayOfErrCodeLine) {
			for (var b = 0; b < errorList.length; b++) {
				invHedData[indexForInv].errorList.push({
					"index": errorList[b].index,
					"errorCode": errorList[b].errorCode,
					"errorDesc": errorList[b].errorDesc,
					"errorFields": errorList[b].errorFields,
					"errorType": errorList[b].errorType
				});
				if (errorList[b].index === undefined) {
					arrayOfErrCode.push(errorList[b].errorCode);
				} else {
					arrayOfErrCodeLine.push(errorList[b].errorCode);
				}
			}
		},

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		_createInvoiceCorrectionModel: function (controller) {
			var oCreateInvoiceOrgData = configCorrectedInvoice.getCorrectedInvoiceData();
			var oCreateInvoiceOrgModel = new JSONModel();
			oCreateInvoiceOrgModel.setData(oCreateInvoiceOrgData);
			controller.oView.setModel(oCreateInvoiceOrgModel, "CreateInvoiceOrgModel");
		},

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		_saveAspCorrectedError: function (oEvent, controller) {
			var that = this;
			var oBundle = controller.getOwnerComponent().getModel("i18n").getResourceBundle();
			MessageBox.confirm(oBundle.getText("config.errorCorrection.confirmationMsg"), {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === "OK") {
						that._saveErrors(controller);
					} else {
						controller._reset();
					}
				}
			});
		},

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		_saveErrors: function (controller) {
			var that = this;
			var oCorrectedData = controller.byId("tabAspErrorItem").getModel("itemsModel").getData();
			that._createInvoiceCorrectionModel(controller);
			oCorrectedData.crDrPreGst = oCorrectedData.crDrPreGst === "Y" ? true : false;
			oCorrectedData.tcsFlag = oCorrectedData.tcsFlag === "Y" ? true : false;
			oCorrectedData.itcFlag = oCorrectedData.itcFlag === "Y" ? true : false;

			var oCorectedDataMod = that._jsonSaveHeader(oCorrectedData, controller);
			var lengthofCorrectedDataItems = oCorrectedData.lineItems.length;
			for (var i = 0; i < lengthofCorrectedDataItems; i++) {
				oCorectedDataMod.req[0].lineItems.push(that._jsonSaveItem(oCorrectedData.lineItems[i]));
			}
			$(document).ready(function ($) {
				controller.getView().setBusy(true);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/saveDoc.do",
					contentType: "application/json",
					data: JSON.stringify(oCorectedDataMod)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					controller.aspEdit = true;
					that._saveErrorSuccess(data, oCorrectedData, controller);
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			});
		},

		_saveErrorSuccess: function (data, oCorrectedData, controller) {
			var lineItemsBeforeCorrected = controller.byId("tabErrorASP").getModel("InvoiceModel").getData();

			if (data.resp[0].errors.length === 0) {
				controller.getView().byId("bSaveASP").setEnabled(false);
				this._successMessage("Saved Successfully");
				var aErrorMsg = [];
				aErrorMsg.push({
					type: "Success",
					counter: 0
				});
				oMessagePopover.setModel(new JSONModel(aErrorMsg));

				var viewModelwithoutErr = new JSONModel();
				viewModelwithoutErr.setData({
					errMessagesLength: 0 + ""
				});
				controller.getView().setModel(viewModelwithoutErr);

				for (var t = 0; t < lineItemsBeforeCorrected.length; t++) {
					if (lineItemsBeforeCorrected[t].id === data.resp[0].oldId) {
						oCorrectedData.id = data.resp[0].id;
						this._errorCorrect(lineItemsBeforeCorrected[t], controller);
					}
				}
			} else {
				var JSONErrors = data.resp[0].errors;
				this._errorMessage("Saved With " + JSONErrors.length + " Errors");
				// Popover Message
				for (var i = 0; i < JSONErrors.length; i++) {
					aErrorMsg.push({
						type: JSONErrors[i].errorType === "ERR" ? "Error" : "Information",
						title: JSONErrors[i].errorCode,
						active: true,
						description: JSONErrors[i].errorDesc,
						subtitle: JSONErrors[i].errorDesc
					});
				}
				oMessagePopover.setModel(new JSONModel(aErrorMsg));

				var viewModelwithErr = new JSONModel();
				viewModelwithErr.setData({
					errMessagesLength: JSONErrors.length + ""
				});
				controller.getView().setModel(viewModelwithErr);
			}

			for (var tl = 0; tl < lineItemsBeforeCorrected.length; tl++) {
				if (lineItemsBeforeCorrected[tl].id === data.resp[0].oldId) {
					oCorrectedData.id = data.resp[0].id;
					this._errorCorrect1(data.resp[0].errors, lineItemsBeforeCorrected[tl].errorList, controller);
				}
			}
		},

		_errorCorrect: function (lineItemsBeforeCorrected, controller) {
			var newErr = lineItemsBeforeCorrected.errorList;
			for (var p = 0; p < newErr.length; p++) {
				var arrayerrorFilds = newErr[p].errorFields.split(",");
				if (!newErr[p].index) {
					var colForHeader = controller.byId("tabAspErrorItem").getRows()[0].getCells();
				} else {
					var lineItemNum = newErr[p].index;
					colForHeader = controller.byId("tabAspErrorItem").getRows()[lineItemNum].getCells();
				}
				for (var h = 3; h < colForHeader.length; h++) {
					colForHeader[h].setEnabled(false);
					for (var q = 0; q < arrayerrorFilds.length; q++) {

						if (colForHeader[h].getName() === arrayerrorFilds[q]) {
							colForHeader[h].setValueState(sap.ui.core.ValueState.Success);
						}
					}
				}
			}
		},

		_errorCorrect1: function (newErr, oldErr, controller) {
			var arrayofErr = [];
			var newArraySuccess = [];
			for (var f = 0; f < oldErr.length; f++) {
				for (var v = 0; v < newErr.length; v++) {
					if (oldErr[f].errorCode !== newErr[v].errorCode) {
						if (!arrayofErr.includes(oldErr[f].errorCode)) {
							arrayofErr.push(oldErr[f].errorCode);
							newArraySuccess.push(oldErr[f]);
						}
					}
				}
			}
			for (var qs = 0; qs < newArraySuccess.length; qs++) {
				var errorFildSuc = newArraySuccess[qs].errorFields;
				var arrayerrorFildsSuc = errorFildSuc.split(",");
				if (!newArraySuccess[qs].index) {
					var colForHeaderSuc = controller.byId("tabAspErrorItem").getRows()[0].getCells();
				} else {
					var lineItemNumSuc = newArraySuccess[qs].index;
					colForHeaderSuc = controller.byId("tabAspErrorItem").getRows()[lineItemNumSuc].getCells();
				}
				for (var hs = 3; hs < colForHeaderSuc.length; hs++) {
					for (var pt = 0; pt < arrayerrorFildsSuc.length; pt++) {
						if (colForHeaderSuc[hs].getName() === arrayerrorFildsSuc[pt]) {
							colForHeaderSuc[hs].setValueState(sap.ui.core.ValueState.Success);
							colForHeaderSuc[hs].setValueStateText("");
						}
					}
				}
			}
			for (var pa = 0; pa < newErr.length; pa++) {
				var errorDescInResp = newErr[pa].errorDesc;
				var arrayerrorFilds = newErr[pa].errorFields.split(",");
				if (!newErr[pa].index) {
					var colForHeader = controller.byId("tabAspErrorItem").getRows()[0].getCells();
				} else {
					colForHeader = controller.byId("tabAspErrorItem").getRows()[newErr[pa].index].getCells();
				}
				for (var ha = 3; ha < colForHeader.length; ha++) {
					for (var qa = 0; qa < arrayerrorFilds.length; qa++) {
						if (colForHeader[ha].getName() === arrayerrorFilds[qa]) {
							colForHeader[ha].setValueState(sap.ui.core.ValueState.Error);
							colForHeader[ha].setValueStateText(errorDescInResp);
						}
					}
				}
			}
		},

		/*================================================================================*/
		/*========= Delete from ASP / GSTN or Both =======================================*/
		/*================================================================================*/
		_deleteASP: function (oEvent, controller) {
			var that = this;
			var aIndices = controller.byId("tabProcessASP").getSelectedIndices();
			if (aIndices.length === 0) {
				that._infoMessage("No data selected to delete.");
				return;
			}
			var aDocId = [];
			var oData = controller.byId("tabProcessASP").getModel("AspProcessModel").getData();
			for (var i = 0; i < aIndices.length; i++) {
				aDocId.push(oData[aIndices[i]].id);
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/deleteGstr1Docs.do",
				contentType: "application/json",
				data: JSON.stringify({
					"req": {
						"docIds": aDocId
					}
				})
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				if (data.hdr.status === "S") {
					that._successMessage("Data deleted successfully");
					that._getProcessedData(controller, false);
				}
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*================================================================================*/
		/*========= Parsing ASP Process JSON =============================================*/
		/*================================================================================*/
		_parseJsonASP: function (data) {
			var invProcessedHedData = [];
			for (var i = 0; i < data.length; i++) {
				data[i].crDrPreGst = data[i].crDrPreGst ? "Y" : "N";
				data[i].tcsFlag = data[i].tcsFlag ? "Y" : "N";
				data[i].itcFlag = data[i].itcFlag ? "Y" : "N";

				invProcessedHedData.push(this._processData(data[i]));
			}
			return invProcessedHedData;
		},

		_processData: function (data) {
			var oInfoCode = this._infoStruct();
			this._infoList(data, oInfoCode);

			var aInvData = this._headerAspJson(data, oInfoCode, "Invoice"),
				aRateData = this._headerAspJson(data, oInfoCode, "Rate", 0);

			var vFlag = false,
				aFileType = this._fileType();

			for (var i = 0; i < aFileType.length; i++) {
				if (data.gstr1SubCategory === aFileType[i]) {
					vFlag = true;
					break;
				}
			}
			if (vFlag) {
				for (var j = 0; j < data.lineItems.length; j++) {
					var aItemData = this._itemAspJson(data, j);
					if (aItemData.igstRate !== aRateData.igstRate || aItemData.cgstRate !== aRateData.cgstRate) {
						this._taxRate(aRateData);
						aInvData.lineItems.push(aRateData);
						aRateData = this._headerAspJson(data, oInfoCode, "Rate", j);
					}
					aRateData.lineItems.push(aItemData);
				}
				this._taxRate(aRateData);
				aInvData.lineItems.push(aRateData);
				this._taxRate(aInvData);
			} else {
				for (j = 0; j < data.lineItems.length; j++) {
					aItemData = this._itemAspJson(data, j);
					aInvData.lineItems.push(aItemData);
				}
			}
			return aInvData;
		},

		_taxRate: function (obj) {
			for (var i = 0; i < obj.lineItems.length; i++) {
				obj.itemQty += obj.lineItems[i].itemQty;
				obj.taxableVal += obj.lineItems[i].taxableVal;
				obj.igstAmt += obj.lineItems[i].igstAmt;
				obj.cgstAmt += obj.lineItems[i].cgstAmt;
				obj.sgstAmt += obj.lineItems[i].sgstAmt;
				obj.cessAmtSpecfic += obj.lineItems[i].cessAmtSpecfic;
				obj.cessAmtAdvalorem += obj.lineItems[i].cessAmtAdvalorem;
			}
		},

		/*================================================================================*/
		/*========= ASP Header JSON ======================================================*/
		/*================================================================================*/
		_headerAspJson: function (data, oInfoCode, level, idx) {
			var obj = ConfigAsp.getObject();

			obj.sno = level;
			obj.id = data.id;
			obj.supplierGstin = data.supplierGstin;
			obj.fiYear = data.fiYear;
			obj.returnPeriod = data.returnPeriod;
			obj.supplyType = data.supplyType;
			obj.docType = data.docType;
			obj.docNo = data.docNo;
			obj.docDate = data.docDate;
			obj.docAmount = data.docAmount;
			obj.originalDocNo = data.originalDocNo;
			obj.originalDocDate = data.originalDocDate;
			obj.gstr1SubCategory = data.gstr1SubCategory;
			obj.gstr1TableNo = data.gstr1TableNo;
			obj.uinOrComposition = data.uinOrComposition;
			obj.custGstin = data.custGstin;
			obj.custOrSuppName = data.custOrSuppName;
			obj.shipToState = data.shipToState;
			obj.billToState = data.billToState;
			obj.pos = data.pos;
			obj.shipPortCode = data.shipPortCode;
			obj.reverseCharge = data.reverseCharge;
			obj.ecomCustGSTIN = data.ecomCustGSTIN;
			obj.dataOriginTypeCode = data.dataOriginTypeCode;
			obj.tcsFlag = data.tcsFlag;
			obj.itcFlag = data.itcFlag;
			obj.custOrSuppCode = data.custOrSuppCode;
			obj.crDrReason = data.crDrReason;
			obj.fob = data.fob;
			obj.shippingBillNo = data.shippingBillNo;
			obj.shippingBillDate = data.shippingBillDate;
			obj.glAccountCode = data.glAccountCode;
			obj.accountVoucherNo = data.accountVoucherNo;
			obj.accountVoucherDate = data.accountVoucherDate;
			obj.crDrPreGst = data.crDrPreGst;
			obj.exportDuty = data.exportDuty;
			obj.plantCode = data.plantCode;
			obj.sourceIdentifier = data.sourceIdentifier;
			obj.sourceFileName = data.sourceFileName;
			obj.division = data.division;
			obj.subDivision = data.subDivision;
			obj.profitCentre1 = data.profitCentre1;
			obj.profitCentre2 = data.profitCentre2;
			obj.crDrReason = data.crDrReason;
			obj.crDrPreGst = data.crDrPreGst;
			obj.portCode = data.portCode;
			obj.userDefinedField1 = data.userDefinedField1;
			obj.userDefinedField2 = data.userDefinedField2;
			obj.userDefinedField3 = data.userDefinedField3;
			obj.derivedTaxperiod = data.derivedTaxperiod;
			obj.infoList = oInfoCode.infoList;
			obj.infoDesc = oInfoCode.infoDesc;
			obj.lineItems = [];

			if (level === "Rate") {
				obj.igstRate = data.lineItems[idx].igstRate;
				obj.sgstRate = data.lineItems[idx].cgstRate;
				obj.cgstRate = data.lineItems[idx].sgstRate;
				obj.cessRateSpecific = data.lineItems[idx].cessRateSpecific;
				obj.cessRateAdvalorem = data.lineItems[idx].cessRateAdvalorem;
			}
			return obj;
		},

		_itemAspJson: function (data, i) {
			var obj = ConfigAsp.getObject();
			obj.sno = "";
			obj.supplierGstin = data.supplierGstin;
			obj.fiYear = data.fiYear;
			obj.returnPeriod = data.returnPeriod;
			obj.supplyType = data.supplyType;
			obj.docType = data.docType;
			obj.docNo = data.docNo;
			obj.docDate = data.docDate;
			obj.docAmount = data.docAmount;
			obj.originalDocNo = data.originalDocNo;
			obj.originalDocDate = data.originalDocDate;
			obj.gstr1SubCategory = data.gstr1SubCategory;
			obj.gstr1TableNo = data.gstr1TableNo;
			obj.uinOrComposition = data.uinOrComposition;
			obj.custGstin = data.custGstin;
			obj.custOrSuppName = data.custOrSuppName;
			obj.shipToState = data.shipToState;
			obj.billToState = data.billToState;
			obj.pos = data.pos;
			obj.shipPortCode = data.shipPortCode;
			obj.reverseCharge = data.reverseCharge;
			obj.ecomCustGSTIN = data.ecomCustGSTIN;
			obj.dataOriginTypeCode = data.dataOriginTypeCode;
			obj.tcsFlag = data.tcsFlag;
			obj.itcFlag = data.itcFlag;
			obj.custOrSuppCode = data.custOrSuppCode;
			obj.fob = data.fob;
			obj.shippingBillNo = data.shippingBillNo;
			obj.shippingBillDate = data.shippingBillDate;
			obj.glAccountCode = data.glAccountCode;
			obj.accountVoucherNo = data.accountVoucherNo;
			obj.accountVoucherDate = data.accountVoucherDate;
			obj.exportDuty = data.exportDuty;
			obj.plantCode = data.plantCode;
			obj.sourceIdentifier = data.sourceIdentifier;
			obj.sourceFileName = data.sourceFileName;
			obj.division = data.division;
			obj.subDivision = data.subDivision;
			obj.profitCentre1 = data.profitCentre1;
			obj.profitCentre2 = data.profitCentre2;
			obj.crDrReason = data.crDrReason;
			obj.crDrPreGst = data.crDrPreGst;
			obj.portCode = data.portCode;
			obj.userDefinedField1 = data.userDefinedField1;
			obj.userDefinedField2 = data.userDefinedField2;
			obj.userDefinedField3 = data.userDefinedField3;
			obj.derivedTaxperiod = data.derivedTaxperiod;

			obj.itemNo = data.lineItems[i].itemNo;
			obj.hsnsacCode = data.lineItems[i].hsnsacCode;
			obj.itemDesc = data.lineItems[i].itemDesc;
			obj.itemType = data.lineItems[i].itemType;
			obj.itemUqc = data.lineItems[i].itemUqc;
			obj.itemQty = data.lineItems[i].itemQty;
			obj.taxableVal = data.lineItems[i].taxableVal;
			obj.igstRate = data.lineItems[i].igstRate;
			obj.sgstRate = data.lineItems[i].cgstRate;
			obj.cgstRate = data.lineItems[i].sgstRate;
			obj.igstAmt = data.lineItems[i].igstAmt;
			obj.cgstAmt = data.lineItems[i].cgstAmt;
			obj.sgstAmt = data.lineItems[i].sgstAmt;
			obj.cessRateSpecific = data.lineItems[i].cessRateSpecific;
			obj.cessAmtSpecfic = data.lineItems[i].cessAmtSpecfic;
			obj.cessRateAdvalorem = data.lineItems[i].cessRateAdvalorem;
			obj.cessAmtAdvalorem = data.lineItems[i].cessAmtAdvalorem;
			obj.productCode = data.lineItems[i].productCode;
			obj.lineItemAmt = data.lineItems[i].lineItemAmt;
			return obj;
		}
	};
});