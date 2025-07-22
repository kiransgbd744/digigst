sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	
	"sap/m/MessagePopover",
	"com/ey/digigst/util/gstr2/ConfigPRProcess",
		"com/ey/digigst/util/gstr2/configCorrectedInvoice",
		"sap/m/MessageItem"

], function (JSONModel, MessageBox, MessageToast, MessagePopover, ConfigPRProcess,configCorrectedInvoice, MessageItem) {
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
			MessageToast.show("Active title is pressed");
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

		_segmentManage: function (oEvent, controller) {
			var oSegmentBtn = oEvent.getSource().getSelectedKey();
			if (oSegmentBtn === "Error") {
				controller.byId("tabErrorPR").setVisible(true);
				controller.byId("tabProcessPR").setVisible(false);
					controller.byId("btnBackPR").setVisible(false);
				controller.byId("tabErrorEdit").setVisible(false);
				controller.byId("tabProcessEdit").setVisible(false);
				controller.byId("btnEditPR").setVisible(false);
				var oModel = controller.byId("tabErrorPR").getModel("InvManagePR");
				if (!oModel) {
					this._getErrorData(controller);
				}
			} else {
				controller.byId("tabProcessPR").setVisible(true);
				controller.byId("tabErrorPR").setVisible(false);
				controller.byId("btnEditPR").setVisible(true);
					controller.byId("btnBackPR").setVisible(false);
	             controller.byId("tabErrorEdit").setVisible(false);
				controller.byId("tabProcessEdit").setVisible(false);
				controller.byId("btnDeletePR").setVisible(false);
				controller.table = controller.section = "";
				oModel = controller.byId("tabProcessPR").getModel("PRProcessModel");
				if (!oModel) {
					this._getProcessedData(controller, false);
				}
			}

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
		/*======== Search Criteria to get data from AJAX Call ============================*/
		/*================================================================================*/
		_searchInfo: function (controller, status) {
			var vCriteria = controller.byId("idslInvManageCriteria").getSelectedKey();
			var aEntity = controller.byId("idInvEntity").getSelectedKeys();
			var aGstins = controller.byId("idslInvManageGSTIN").getSelectedKeys();
			var vDocNo = controller.byId("idInvManageDocNo").getValue();

			if (controller.byId("sbManagePR").getSelectedKey() === "Process") {
				var vPageNo = controller.byId("inPageNoPR").getValue();
			}
			if (!vPageNo) {
				vPageNo = 1;
			} else {
				vPageNo = parseInt(vPageNo, 10);
			}

			var searchInfo = {
				"hdr": {
					"pageNum": vPageNo - 1,
					"pageSize": 5
				},
				"req": {
					"gstins": aGstins,
					"criteria": vCriteria,
					"receivFromDate": null,
					"receivToDate": null,
					"docFromDate": null,
					"docToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docNo": null,
					"entityId": aEntity
				}
			};
			/*	var searchInfo = {
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
						"processingStatus": status
					}
				};*/
			if (vDocNo !== "") {
				searchInfo.req.docNo = vDocNo;
			}
			switch (vCriteria) {
			case "RECEIVED_DATE_SEARCH":
				searchInfo.req.receivFromDate = controller._formatDate(controller.byId("idDrsInvManage").getDateValue());
				searchInfo.req.receivToDate = controller._formatDate(controller.byId("idDrsInvManage").getSecondDateValue());
				break;
			case "DOCUMENT_DATE_SEARCH":
				searchInfo.req.docFromDate = controller._formatDate(controller.byId("idDrsInvManage").getDateValue());
				searchInfo.req.docToDate = controller._formatDate(controller.byId("idDrsInvManage").getSecondDateValue());
				break;
			case "RETURN_DATE_SEARCH":
				searchInfo.req.returnFromDate = (controller._formatPeriod(controller.byId("idDrsInvManage").getDateValue())).substr(2, 6);
				searchInfo.req.returnToDate = (controller._formatPeriod(controller.byId("idDrsInvManage").getSecondDateValue())).substr(2, 6);
				break;
			}
			return searchInfo;
		},
		
		
				/*================================================================================*/
		/*===============================PR Process Edit Correction===============================*/
		/*================================================================================*/
		_savePRCorrectedError: function (oEvent, controller) {
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
		_createInvoiceCorrectionModel: function (controller) {
			var oCreateInvoiceOrgData = configCorrectedInvoice.getCorrectedInvoiceData();
			var oCreateInvoiceOrgModel = new JSONModel();
			oCreateInvoiceOrgModel.setData(oCreateInvoiceOrgData);
			controller.oView.setModel(oCreateInvoiceOrgModel, "CreateInvoiceOrgModel");
		},

		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		/*================================================================================*/
		/*======== PR Process AJAX Call to get Call =====================================*/
		/*================================================================================*/
		_getProcessedData: function (controller, vInfo) {
			var that = this;
			that.info = vInfo;
			if (vInfo) {
				var stats = "I";
			} else {
				stats = "P";
			}

			if (controller.byId("sbManagePR").getSelectedKey() === "Process") {
				controller.getView().byId("tabProcessPR").setVisible(true);
				controller.getView().byId("tabProcessEdit").setVisible(false);
				controller.getView().byId("tabErrorEdit").setVisible(false);
				controller.getView().byId("tabErrorPR").setVisible(false);
			}
			var bCompact = !!controller.getView().$().closest(".sapUiSizeCompact").length;
			var searchInfo = that._searchInfo(controller, stats);
			controller.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2docSearch.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var vPageNo = Math.ceil(data.hdr.totalCount / 50);
					controller.byId("txtPageNoPR").setText("/ " + vPageNo);
					if (vPageNo === 0) {
						controller.byId("inPageNoPR").setValue(0);
						controller.byId("inPageNoPR").setEnabled(false);
						controller.byId("btnPrevPR").setEnabled(false);
						controller.byId("btnNextPR").setEnabled(false);

					} else if (controller.byId("inPageNoPR").getValue() === "" || controller.byId("inPageNoPR").getValue() === "0") {
						controller.byId("inPageNoPR").setValue(1);
						controller.byId("inPageNoPR").setEnabled(true);
						controller.byId("btnPrevPR").setEnabled(false);
					}
					if (vPageNo > 1) {
						controller.byId("btnNextPR").setEnabled(true);
					} else {
						controller.byId("btnNextPR").setEnabled(false);
					}
					var aProcessData = [];
					for (var z = 0; z < data.resp.length; z++) {
						var oData = [];

						aProcessData.push(data.resp[z]);

					}
					aProcessData.sort(function (a, b) {
						return a.docNo - b.docNo;
					});
					if (aProcessData.length === 0) {
						that._infoMessage("Processed records does not exist.");
					} else {
						oData = that._parsePRJson(aProcessData);
					}
					var oAspProcessedModel = new JSONModel();
					oAspProcessedModel.setData(oData);
					if (!oData) {
						controller.byId("txtRecordsPR").setText("Records: 0");
					} else {
						controller.byId("txtRecordsPR").setText("Records: " + oData.length);
					}
					controller.byId("tabProcessPR").setModel(oAspProcessedModel, "PRProcessModel");

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
		/*========= Save Edit Processed Records ==========================================*/
		/*================================================================================*/
		_saveEditPRProcessedRec: function (oEvent, controller) {
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
		/*========= Ajax Call to Save Edit Processed Records =============================*/
		/*================================================================================*/
		_saveProcessedRec: function (controller) {
			var that = this,
				countForMandatory = 0,
				oCorrectedData = controller.byId("tabProcessEdit").getModel("ProcessItemEdit").getData();

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
					// 	var colForHeader;
					if (data.resp[0].errors.length === 0) {
						controller.byId("btnProcessedInvErrors").setVisible(false);
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
				oModelProcessEdit = controller.byId("tabProcessEdit").getModel("ProcessItemEdit"),
				oCorrectedData = oModelProcessEdit.getData();

			controller.byId("btnProcessedInvErrors").setVisible(true);
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
			var editedDataWithErr = controller.getView().byId("tabProcessPR").getModel("PRProcessModel").getData();
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
							colForHeader = controller.byId("tabProcessEdit").getRows()[0].getCells();
						} else {
							var lineItemNum = newErr[p].index;
							colForHeader = controller.byId("tabProcessEdit").getRows()[lineItemNum].getCells();
						}
						this._setValueState(colForHeader, arrayerrorFilds);
					}
				}
			}
			oModelProcessEdit.refresh(true);
		},

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
		/*========= Edit PR Process Records =============================================*/
		/*================================================================================*/
		_editAspProcessedRec: function (controller, oEvent) {
			var rowIndexSelected = controller.byId("tabProcessPR").getSelectedIndex();
			if (rowIndexSelected === -1) {
				this._errorMessage("Please select one record to edit");
				return;
			}
			var processedDataForEdit = controller.byId("tabProcessPR").getContextByIndex(rowIndexSelected).getObject();
			var oData = this._headerPRJson(processedDataForEdit, "", "Invoice");
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
			controller.byId("btnBackPR").setVisible(true);
		
			controller.byId("btnEditPR").setVisible(false);
		
			controller.byId("btnDeletePR").setVisible(false);
			controller.byId("btnSavePR").setVisible(true);
			controller.byId("tabProcessPR").setVisible(false);
			controller.byId("tabErrorPR").setVisible(false);
			controller.byId("tabProcessEdit").setVisible(true);
			controller.byId("tabErrorEdit").setVisible(false);

			var oProcessedModelForItems = new JSONModel(oData);
			controller.byId("tabProcessEdit").setModel(oProcessedModelForItems, "ProcessItemEdit");
		},


		/*================================================================================*/
		/*========= Get ASP Management Details ===========================================*/
		/*================================================================================*/
		_fileType: function () {
			return ["B2B", "B2BA", "B2CL", "B2CLA", "EXP", "EXPA"];
		},

		/*================================================================================*/
		/*========= Navigate Back to PR  Process Screen ==================================*/
		/*================================================================================*/
			_navBackProcessed: function (oEvent, controller) {
			var vButton = controller.getView().byId("sbManagePR").getSelectedKey();
			controller.table = controller.section = "";
			controller.byId("btnBackPR").setVisible(false);
			controller.byId("btnSavePR").setVisible(false);
		controller.byId("tabProcessEdit").setVisible(false);

			if (vButton === "Process") {
				var vFlag = true;
				this._getProcessedData(controller, false);
			} else {
				vFlag = false;
				this._getErrorData(controller, false);
			}
		//	controller.byId("bAddASP").setVisible(vFlag);
			controller.byId("btnEditPR").setVisible(vFlag);
			controller.byId("btnDeletePR").setVisible(vFlag);
		//	controller.byId("tabProcessPR").setVisible(vFlag);
		},




			_deletePRProcess: function (oEvent, controller) {
			var that = this;
			var aIndices = controller.byId("tabProcessASP").getSelectedIndices();
			if (aIndices.length === 0) {
				that._infoMessage("No data selected to delete.");
				return;
			}
			var aDocId = [];
			var oData = controller.byId("tabProcessPR").getModel("PRProcessModel").getData();
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
		
		
		_parsePRJson: function (data) {

			var invProcessedHedData = [];
			debugger; //eslint-disable-line no-debugger
			for (var i = 0; i < data.length; i++) {
				data[i].crDrPreGst = data[i].crDrPreGst ? "Y" : "N";
				data[i].tcsFlag = data[i].tcsFlag ? "Y" : "N";
				data[i].itcFlag = data[i].itcFlag ? "Y" : "N";

				invProcessedHedData.push(this._processData(data[i]));

			}
			return invProcessedHedData;
		},

		_processData: function (data) {
			var infoList = this._infoList(data);
			var aRateData = this._headerPRJson(data, infoList, "Rate");
			var aInvData = this._headerPRJson(data, infoList, "Invoice");
			for (var i = 0; i < data.lineItems.length; i++) {
				var aItemData = this._itemPRJson(data, i);
				aRateData.lineItems.push(aItemData);
			}
			aInvData.lineItems.push(aRateData);
			return aInvData;
		},

		/*================================================================================*/
		/*======== Parse return data to JSON Format ======================================*/
		/*================================================================================*/
		_infoList: function (data) { //eslint-disable-line
			var infoList = "";
			if (data.errorList) {
				for (var b = 0; b < data.errorList.length; b++) {
					var infoCodes = [];
					var infoAvai = data.errorList;
					for (var r = 0; r < infoAvai.length; r++) {
						if (!infoCodes.includes(infoAvai[r].errorCode)) {
							infoCodes.push(infoAvai[r].errorCode);
						}
					}
					infoList = infoCodes.toString();
				}
			}
			return infoList;
		},



	/*================================================================================*/
		/*========= PR Error ============================================================*/
		/*================================================================================*/
		_getErrorData: function (controller) {
			var that = this;
			//	controller.byId("GstinStatusFil").setVisible(false);
			controller.byId("btnEditPR").setVisible(false);
			//controller.byId("idAddinvAsp").setVisible(false);
			controller.byId("btnDeletePR").setVisible(false);
			//controller.byId("bInvErrors").setVisible(false);
			//	controller.byId("idPRsavechange").setVisible(false);
			//	controller.byId("bSaveASPProcessed").setVisible(false);
			if (controller.byId("sbManagePR").getSelectedKey() === "Error") {
				controller.byId("tabProcessPR").setVisible(false);
				controller.byId("tabErrorPR").setVisible(true);
				controller.byId("tabProcessEdit").setVisible(false);
				controller.byId("tabErrorEdit").setVisible(false);
			}
			var bCompact = !!controller.getView().$().closest(".sapUiSizeCompact").length;
			var searchInfo = that._searchInfo(controller, "E");
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/docSearch.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				var oInvoiceModel = new JSONModel();
				var aDataError = [];
				for (var i = 0; i < data.resp.length; i++) {
					if (data.resp[i].isError) {
						aDataError.push(data.resp[i]);
					}
				}
				if (aDataError.length === 0) {
					MessageBox.information("No Errors in this record.", {
						styleClass: bCompact ? "sapUiSizeCompact" : ""
					});
					oInvoiceModel.setData(aDataError);
				} else {
					oInvoiceModel.setData(that._errorJSON(aDataError));
				}
				controller.byId("tabErrorPR").setModel(oInvoiceModel, "InvManagePR");
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
		},
		
		
		
		
		
		
		
		//Error Json
		
		
		
		
		
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
						errorCodes.push(errorsAvai[r].isError);
						errorDesc.push(errorsAvai[r].isInfo);
					}
				}
			//	var errCodeStr = errorCodes.toString(),
				//	errorDescStr = errorDesc.toString();

				// if (aDataError[v].crDrPreGst === false) {
				// 	aDataError[v].crDrPreGst = "N";
				// } else {
				// 	aDataError[v].crDrPreGst = "Y";
				// }
				// if (aDataError[v].tcsFlag === false) {
				// 	aDataError[v].tcsFlag = "N";
				// } else {
				// 	aDataError[v].tcsFlag = "Y";
				// }
				// if (aDataError[v].itcFlag === false) {
				// 	aDataError[v].itcFlag = "N";
				// } else {
				// 	aDataError[v].itcFlag = "Y";
				// }

				aDataError[v].crDrPreGst = aDataError[v].crDrPreGst ? "Y" : "N";
				aDataError[v].tcsFlag = aDataError[v].tcsFlag ? "Y" : "N";
				aDataError[v].itcFlag = aDataError[v].itcFlag ? "Y" : "N";

				invHedData.push({
					"sno": indexForInv + 1,
				
					"gstr1SubCategory":  aDataError[v].gstr1SubCategory,
				"gstr1TableNo":  aDataError[v].gstr1TableNo,

				"docNo":  aDataError[v].docNo,
				"docDate": aDataError[v].docDate,
				"docType": aDataError[v].docType,
				"fiYear": aDataError[v].fiYear,
				"plantCode": aDataError[v].plantCode,
				"supplierGstin": aDataError[v].supplierGstin,
				"custGstin": aDataError[v].custGstin,
				"ecomCustGSTIN":  aDataError[v].ecomCustGSTIN,

				"reverseCharge": aDataError[v].reverseCharge,
				"pos":  aDataError[v].pos,
				"custOrSuppCode":  aDataError[v].custOrSuppCode,
				"custOrSuppName":  aDataError[v].custOrSuppName,
				"sourceIdentifier":  aDataError[v].sourceIdentifier,
				"sourceFileName": aDataError[v].sourceFileName,
				"originalDocNo":  aDataError[v].originalDocNo,
				"originalDocDate":  aDataError[v].originalDocDate,
				"isError":  aDataError[v].isError,
				"isInfo":  aDataError[v].isInfo,
				"docAmount":  aDataError[v].docAmount,
				"isProcessed":  aDataError[v].isProcessed,
				"accountVoucherNo":  aDataError[v].accountVoucherNo,
				"accountVoucherDate":  aDataError[v].accountVoucherDate,
				"division":  aDataError[v].division,
				"subDivision":  aDataError[v].subDivision,
				"profitCentre1":  aDataError[v].profitCentre1,
				"profitCentre2":  aDataError[v].profitCentre2,
				"crDrReason":  aDataError[v].crDrReason,
				"crDrPreGst":  aDataError[v].crDrPreGst,
				"userDefinedField1": aDataError[v].userDefinedField1,
				"userDefinedField2":  aDataError[v].userDefinedField2,
				"userDefinedField3":  aDataError[v].userDefinedField3,

				"returnPeriod":  aDataError[v].returnPeriod,
				"isDeleted":  aDataError[v].isDeleted,
				"origSupplierGstin":  aDataError[v].origSupplierGstin,
            "billOfEntryNo":  aDataError[v].billOfEntryNo,
            "billOfEntryDate": aDataError[v].billOfEntryDate,
            "cifValue": aDataError[v].cifValue,
            "customDuty":  aDataError[v].customDuty,

					
					"lineItems": [],
					"errorList": []
				});

				for (var i = 0; i < aDataError[v].lineItems.length; i++) {
					invHedData[indexForInv].lineItems.push({
					
					  "eligibilityInd" : aDataError[v].lineItems[i].eligibilityInd,
	"commonSupplyInd" : aDataError[v].lineItems[i].commonSupplyInd,
			"availableIgst" : aDataError[v].lineItems[i].availableIgst,
			"availableCgst" :  aDataError[v].lineItems[i].availableCgst,
		"availableSgst" :  aDataError[v].lineItems[i].availableSgst,
		"availableCess" :  aDataError[v].lineItems[i].availableCess,
		"itcReversalId" :  aDataError[v].lineItems[i].itcReversalId,
		"paymentVoucherNum" :  aDataError[v].lineItems[i].paymentVoucherNum,
		"paymentDate" :  aDataError[v].lineItems[i].paymentDate,
			"contractNo" :  aDataError[v].lineItems[i].contractNo,
			"contractDate" :  aDataError[v].lineItems[i].contractDate,
			"contractValue" :  aDataError[v].lineItems[i].contractValue,
			"id" : aDataError[v].lineItems[i].id,
		"itemNo" :  aDataError[v].lineItems[i].itemNo,
			"hsnsacCode" :  aDataError[v].lineItems[i].hsnsacCode,
			"supplyType" : aDataError[v].lineItems[i].supplyType,
		"productCode" : aDataError[v].lineItems[i].productCode,
		"itemDesc" :  aDataError[v].lineItems[i].itemDesc,
	"itemType" :  aDataError[v].lineItems[i].itemType,
		"itemUqc" :  aDataError[v].lineItems[i].itemUqc,
			"itemQty" :  aDataError[v].lineItems[i].itemQty,
		"glAccountCode" :  aDataError[v].lineItems[i].glAccountCode,
			"igstRate" :  aDataError[v].lineItems[i].igstRate,
		"cgstRate" :  aDataError[v].lineItems[i].cgstRate,
			
			"sgstRate" :  aDataError[v].lineItems[i].sgstRate,
		"igstAmt" :  aDataError[v].lineItems[i].igstAmt,
		"cgstAmt" :  aDataError[v].lineItems[i].cgstAmt,
		"sgstAmt" :  aDataError[v].lineItems[i].sgstAmt,
	
			"cessAmtAdvalorem" :  aDataError[v].lineItems[i].cessAmtAdvalorem,
		"cessAmtSpecfic" :  aDataError[v].lineItems[i].cessAmtSpecfic,
		"cessRateAdvalorem" :  aDataError[v].lineItems[i].cessRateAdvalorem,
						"cessRateSpecific" :  aDataError[v].lineItems[i].cessRateSpecific,
			"taxableVal" :  aDataError[v].lineItems[i].taxableVal
							// 		"errorCodeForLineItem": ""
					});
					if (aDataError[v].errorList) {
						this._errorList(aDataError[v].errorList, invHedData, indexForInv, arrayOfErrCode, arrayOfErrCodeLine);
						// 		for (var b = 0; b < aDataError[v].errorList.length; b++) {
						// 			invHedData[indexForInv].errorList.push({
						// 				"index": aDataError[v].errorList[b].index,
						// 				"errorCode": aDataError[v].errorList[b].errorCode,
						// 				"errorDesc": aDataError[v].errorList[b].errorDesc,
						// 				"errorFields": aDataError[v].errorList[b].errorFields,
						// 				"errorType": aDataError[v].errorList[b].errorType
						// 			});
						// 			if (aDataError[v].errorList[b].index === undefined) {
						// 				arrayOfErrCode.push(aDataError[v].errorList[b].errorCode);
						// 			} else {
						// 				arrayOfErrCodeLine.push(aDataError[v].errorList[b].errorCode);
						// 			}
						// 		}
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
		/*========= For Correcting Errors in Detail ======================================*/
		/*================================================================================*/
		_detailErrToCorrect: function (controller, oEvent) {
			var dataForCorrection = oEvent.getSource().getBindingContext("InvManagePR").getObject();
			controller.byId("btnBackPR").setVisible(true);
			controller.byId("btnEditPR").setVisible(false);
			controller.byId("btnSavePR").setVisible(true);
			//controller.byId("idPRerrorsInInv").setVisible(true);
			//	controller.byId("bSaveASP").setEnabled(true);
			controller.byId("btnDeletePR").setVisible(true);
			controller.byId("tabProcessPR").setVisible(false);
			controller.byId("tabErrorPR").setVisible(false);
			controller.byId("tabProcessEdit").setVisible(false);
			controller.byId("tabErrorEdit").setVisible(true);
			var oErrorModelForItems = new JSONModel();
			oErrorModelForItems.setData(dataForCorrection);
			controller.byId("tabErrorEdit").setModel(oErrorModelForItems, "ErrorItemEdit");
			var errorListOfInvoice = dataForCorrection.errorList;
			// 			Popover
			var aMockMessages = [],
				aMockMessagesErr = [],
				errCodesAvai = [];

			for (var k = 0; k < errorListOfInvoice.length; k++) {
				errCodesAvai.push(errorListOfInvoice[k].errorCode);
				aMockMessages.push({
					type: "Error",
					title: errorListOfInvoice[k].errorCode,
					active: true,
					description: errorListOfInvoice[k].errorDesc,
					subtitle: errorListOfInvoice[k].errorDesc
						// 		counter: errorListOfInvoice.length
				});
				aMockMessagesErr.push({
					type: "Error",
					title: errorListOfInvoice[k].errorCode,
					active: true
				});
			}
			// 			Err codes
			// 			var oModel = new JSONModel();
			// 			oModel.setData(aMockMessages);
			//	oMessagePopover.setModel(new JSONModel(aMockMessages));

			var viewModel = new JSONModel();
			viewModel.setData({
				errMessagesLength: errorListOfInvoice.length + ""
			});
			controller.getView().setModel(viewModel);

			// 			At Header Level
			var colForHeader;
			for (var o = 0; o < errorListOfInvoice.length; o++) {
				var errorFieldInJson = errorListOfInvoice[o].errorFields;
				var errorDescInJson = errorListOfInvoice[o].errorDesc;
				var arrayErrors = errorFieldInJson.split(",");
				// arrayErrors.push(errorDesc: errorListOfInvoice[o].errorDesc);
				if (errorListOfInvoice[o].index === undefined) {
					colForHeader = controller.byId("tabErrorEdit").getRows()[0].getCells();
					// 	for (var h = 3; h < colForHeader.length; h++) {
					// 		for (var q = 0; q < arrayErrors.length; q++) {
					// 			if (colForHeader[h].getName() === arrayErrors[q]) {
					// 				colForHeader[h].setValueState(sap.ui.core.ValueState.Error);
					// 				colForHeader[h].setValueStateText(errorDescInJson);
					// 			}
					// 		}
					// 	}
				} else {
					var lineItemNum = errorListOfInvoice[o].index;
					colForHeader = controller.byId("tabErrorEdit").getRows()[lineItemNum].getCells();
					// 	for (var j = 3; j < colForHeader.length; j++) {
					// 		for (var m = 0; m < arrayErrors.length; m++) {
					// 			if (colForHeader[j].getName() === arrayErrors[m]) {
					// 				colForHeader[j].setValueState(sap.ui.core.ValueState.Error);
					// 				colForHeader[j].setValueStateText(errorDescInJson);
					// 			}
					// 		}
					// 	}
				}
				for (var j = 3; j < colForHeader.length; j++) {
					for (var m = 0; m < arrayErrors.length; m++) {
						if (colForHeader[j].getName() === arrayErrors[m]) {
							colForHeader[j].setValueState(sap.ui.core.ValueState.Error);
							colForHeader[j].setValueStateText(errorDescInJson);
						}
					}
				}
			}
		},
		/*================================================================================*/
		/*======== Parse return data to JSON Format ======================================*/
		/*================================================================================*/
		_processJSON: function (aProcessData) {
			var vIndex = 0,
				invProcessedHedData = [];
			for (var v = 0; v < aProcessData.length; v++) {
				var infoList = this._infoList(aProcessData[v]);

				aProcessData[v].crDrPreGst = aProcessData[v].crDrPreGst ? "Y" : "N";
				aProcessData[v].tcsFlag = aProcessData[v].tcsFlag ? "Y" : "N";
				aProcessData[v].itcFlag = aProcessData[v].itcFlag ? "Y" : "N";

				invProcessedHedData.push(this._headerJSON(aProcessData[v], infoList));
				invProcessedHedData[vIndex].sno = vIndex + 1;

				for (var i = 0; i < aProcessData[v].lineItems.length; i++) {
					invProcessedHedData[vIndex].lineItems.push(this._itemJSON(aProcessData[v], i));

				}
				vIndex++;
			}
			return invProcessedHedData;
		},
		_headerPRJson: function (data, infoList, level) {
			return {
				"sno": level,
				"gstr1SubCategory": data.gstr1SubCategory,
				"gstr1TableNo": data.gstr1TableNo,
				"docNo": data.docNo,
				"docDate": data.docDate,
				"docType": data.docType,
				"fiYear": data.fiYear,
				"plantCode": data.plantCode,
				"supplierGstin": data.supplierGstin,
				"custGstin": data.custGstin,
				"ecomCustGSTIN": data.ecomCustGSTIN,
				"reverseCharge": data.reverseCharge,
				"pos": data.pos,
				"custOrSuppCode": data.custOrSuppCode,
				"custOrSuppName": data.custOrSuppName,
				"sourceIdentifier": data.sourceIdentifier,
				"sourceFileName": data.sourceFileName,
				"originalDocNo": data.originalDocNo,
				"originalDocDate": data.originalDocDate,
				"isError": data.isError,
				"isInfo": data.isInfo,
				"docAmount": data.docAmount,
				"isProcessed": data.isProcessed,
				"accountVoucherNo": data.accountVoucherNo,
				"accountVoucherDate": data.accountVoucherDate,
				"division": data.division,
				"subDivision": data.subDivision,
				"profitCentre1": data.profitCentre1,
				"profitCentre2": data.profitCentre2,
				"crDrReason": data.crDrReason,
				"crDrPreGst": data.crDrPreGst,
				"userDefinedField1": data.userDefinedField1,
				"userDefinedField2": data.userDefinedField2,
				"userDefinedField3": data.userDefinedField3,

				"returnPeriod": data.returnPeriod,
				"isDeleted": data.isDeleted,
				"origSupplierGstin": data.origSupplierGstin,
				"billOfEntryNo": data.billOfEntryNo,
				"billOfEntryDate": data.billOfEntryDate,
				"cifValue": data.cifValue,
				"customDuty": data.customDuty,

				"lineItems": []
			};
		},
		_itemPRJson: function (data, i) {
			var oObject = ConfigPRProcess.getObject();
			oObject.sno = "";
			oObject.gstr1SubCategory = data.gstr1SubCategory;
			oObject.gstr1TableNo = data.gstr1TableNo;
			oObject.docNo = data.docNo;
			oObject.docDate = data.docDate;
			oObject.sno = data.docType;
			oObject.fiYear = data.fiYear;
			oObject.plantCode = data.plantCode;
			oObject.supplierGstin = data.supplierGstin;
			oObject.custGstin = data.custGstin;
			oObject.ecomCustGSTIN = data.ecomCustGSTIN;

			oObject.reverseCharge = data.reverseCharge;
			oObject.pos = data.pos;
			oObject.custOrSuppCode = data.custOrSuppCode;
			oObject.custOrSuppName = data.custOrSuppName;
			oObject.sourceIdentifier = data.sourceIdentifier;
			oObject.sourceFileName = data.sourceFileName;
			oObject.originalDocNo = data.originalDocNo;
			oObject.originalDocDate = data.originalDocDate;
			oObject.isError = data.isError;
			oObject.isInfo = data.isInfo;
			oObject.docAmount = data.docAmount;
			oObject.isProcessed = data.isProcessed;
			oObject.accountVoucherNo = data.accountVoucherNo;
			oObject.accountVoucherDate = data.accountVoucherDate;
			oObject.division = data.division;
			oObject.subDivision = data.subDivision;
			oObject.profitCentre1 = data.profitCentre1;
			oObject.profitCentre2 = data.profitCentre2;
			oObject.crDrReason = data.crDrReason;
			oObject.crDrPreGst = data.crDrPreGst;
			oObject.userDefinedField1 = data.userDefinedField1;
			oObject.userDefinedField2 = data.userDefinedField2;
			oObject.userDefinedField3 = data.userDefinedField3;
			oObject.returnPeriod = data.returnPeriod;
			oObject.isDeleted = data.isDeleted;
			oObject.origSupplierGstin = data.origSupplierGstin;
			oObject.billOfEntryNo = data.billOfEntryNo;
			oObject.billOfEntryDate = data.billOfEntryDate;
			oObject.cifValue = data.cifValue;
			oObject.customDuty = data.customDuty;

			oObject.eligibilityInd = data.lineItems[i].eligibilityInd;
			oObject.commonSupplyInd = data.lineItems[i].commonSupplyInd;
			oObject.availableIgst = data.lineItems[i].availableIgst;
			oObject.availableCgst = data.lineItems[i].availableCgst;
			oObject.availableSgst = data.lineItems[i].availableSgst;
			oObject.availableCess = data.lineItems[i].availableCess;
			oObject.itcReversalId = data.lineItems[i].itcReversalId;
			oObject.paymentVoucherNum = data.lineItems[i].paymentVoucherNum;
			oObject.paymentDate = data.lineItems[i].paymentDate;
			oObject.contractNo = data.lineItems[i].contractNo;
			oObject.contractDate = data.lineItems[i].contractDate;
			oObject.contractValue = data.lineItems[i].contractValue;
			oObject.id = data.lineItems[i].id;
			oObject.itemNo = data.lineItems[i].itemNo;
			oObject.hsnsacCode = data.lineItems[i].hsnsacCode;
			oObject.supplyType = data.lineItems[i].supplyType;
			oObject.productCode = data.lineItems[i].productCode;
			oObject.itemDesc = data.lineItems[i].itemDesc;
			oObject.itemType = data.lineItems[i].itemType;
			oObject.itemUqc = data.lineItems[i].itemUqc;
			oObject.itemQty = data.lineItems[i].itemQty;
			oObject.glAccountCode = data.lineItems[i].glAccountCode;
			oObject.igstRate = data.lineItems[i].igstRate;
			oObject.cgstRate = data.lineItems[i].cgstRate;
			oObject.sgstRate = data.lineItems[i].sgstRate;
			oObject.igstAmt = data.lineItems[i].igstAmt;
			oObject.cgstAmt = data.lineItems[i].cgstAmt;
			oObject.sgstAmt = data.lineItems[i].sgstAmt;
			oObject.itemQty = data.lineItems[i].itemQty;
			oObject.cessAmtAdvalorem = data.lineItems[i].cessAmtAdvalorem;
			oObject.cessAmtSpecfic = data.lineItems[i].cessAmtSpecfic;
			oObject.cessRateAdvalorem = data.lineItems[i].cessRateAdvalorem;
			oObject.cessRateSpecific = data.lineItems[i].cessRateSpecific;
			oObject.taxableVal = data.lineItems[i].taxableVal;
			return oObject;
		},
		_getPrManageDetails: function (controller) {
			if (controller.byId("sbManagePR").getSelectedKey() === "Process") {
				controller.byId("inPageNoPR").setValue("");
				controller.table = controller.section = "";
				this._getProcessedData(controller, false);

			} else if (controller.byId("sbManagePR").getSelectedKey() === "Error") {
				this._getErrorData(controller);
			}

		}
	};
});