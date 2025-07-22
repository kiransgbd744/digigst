sap.ui.define([
	"com/ey/digigst/util/gstr1/ManageASP",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
], function (ManageASP, JSONModel, MessageBox, MessageToast) {
	"use strict";

	return {
		/*=====================================================================*/
		/*======= API Summary Structure =======================================*/
		/*=====================================================================*/
		_apiSummaryStruc: function () {
			return {
				"receivedDate": "",
				"docDate": "",
				"gstin": "",
				"period": "",
				"section": "",
				"count": 0,
				"taxableValue": 0,
				"toatlTaxes": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0,
				"authToken": "",
				"status": "",
				"flag": "",
				"isBold": false,
				"items": []
			};
		},

		/*=====================================================================*/
		/*======= Segment Button ==============================================*/
		/*=====================================================================*/
		_segmentStatus: function (oEvent, controller) {
			var oSegmentBtn = oEvent.getSource().getSelectedKey();
			if (oSegmentBtn === "API") {
				this._uploadVisible(false, controller, "A");

			} else if (oSegmentBtn === "Upload") {
				if (controller.byId("sbUpload").getSelectedKey() === "Upload") {
					var vFlag = true,
						vSb = "U";
				} else {
					vFlag = false;
					vSb = "F";
				}
				this._uploadVisible(vFlag, controller, vSb);
			} else {
				this._uploadVisible(false, controller, "S");
			}
		},

		/*=====================================================================*/
		/*======= Segment Button for Web-upload ===============================*/
		/*=====================================================================*/
		_segmentUpload: function (oEvent, controller) {
			if (oEvent.getSource().getSelectedKey() === "Upload") {
				var vFlag = true,
					vSb = "F";
			} else {
				vFlag = false;
				vSb = "F";
			}
			this._uploadVisible(vFlag, controller, vSb);
		},

		/*=====================================================================*/
		/*======= Web Upload visibility =======================================*/
		/*=====================================================================*/
		_uploadVisible: function (vFlag, controller, vSb) {
			controller.byId("fbStatus").setVisible(!vFlag);
			controller.byId("hbRbgStatus").setVisible(vFlag);
			controller.byId("ucWebUpload").setVisible(vFlag);
			controller.byId("btnUpload").setVisible(vFlag);

			if (vSb === "A") {
				controller.byId("tabData").setVisible(true);
			} else {
				controller.byId("tabData").setVisible(false);
			}
			if (vSb === "U" || vSb === "F") {
				controller.byId("sbUpload").setVisible(true);
			} else {
				controller.byId("sbUpload").setVisible(false);
			}
			if (vSb === "F" || vSb === "S") {
				if (vSb === "F") {
					controller.byId("tabFileStatus").setVisible(!vFlag);
					controller.byId("tabSftpStatus").setVisible(false);
				} else {
					controller.byId("tabFileStatus").setVisible(false);
					controller.byId("tabSftpStatus").setVisible(true);
				}
				controller.byId("fbCriteria").setVisible(false);
				controller.byId("idEntity").setVisible(false);
				controller.byId("idGSTIN").setVisible(false);
				controller.byId("idFileType").setVisible(true);
			} else {
				controller.byId("tabFileStatus").setVisible(false);
				controller.byId("tabSftpStatus").setVisible(false);
				controller.byId("fbCriteria").setVisible(true);
				controller.byId("idEntity").setVisible(true);
				controller.byId("idGSTIN").setVisible(true);
				controller.byId("idFileType").setVisible(false);
			}
			if (vSb === "F" && !vFlag) {
				var oModel = controller.byId("tabFileStatus").getModel("FileStatus");
				if (!oModel) {
					this._getFileStatus(controller);
				}
			}
		},

		/*=====================================================================*/
		/*======= On Search of Status =========================================*/
		/*=====================================================================*/
		_getDataStatus: function (controller) {
			var vSB = controller.byId("sbStatus").getSelectedKey();
			if (vSB === "API") {
				this._getStatusAPI(controller);
			} else if (vSB === "Upload") {
				this._getFileStatus(controller);
			}
		},

		_getStatusAPI: function (controller) {
			var that = this;
			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			var aEntity = controller.byId("slStatusEntity").getSelectedKeys();
			var aGstin = controller.byId("mcbStatusGSTIN").getSelectedKeys();

			if (vCriteria === "RETURN_DATE_SEARCH") {
				var vFromDate = (controller._formatPeriod(controller.byId("drsStatus").getDateValue())).substr(2, 6);
				var vToDate = (controller._formatPeriod(controller.byId("drsStatus").getSecondDateValue())).substr(2, 6);
			} else {
				vFromDate = controller._formatDate(controller.byId("drsStatus").getDateValue());
				vToDate = controller._formatDate(controller.byId("drsStatus").getSecondDateValue());
			}

			var oModel = controller.byId("tabData").getModel("Data");
			if (oModel) {
				oModel.setData(null);
			}
			if (vFromDate === "") {
				MessageToast.show("Please select date Period");
				return;
			} else {
				var searchInfo = {
					"req": {
						"entityId": aEntity,
						"gstin": aGstin,
						"dataRecvFrom": null,
						"dataRecvTo": null,
						"docDateFrom": null,
						"docDateTo": null,
						"retPeriodFrom": null,
						"retPeriodTo": null
					}
				};
				switch (vCriteria) {
				case "RECEIVED_DATE_SEARCH":
					searchInfo.req.dataRecvFrom = vFromDate;
					searchInfo.req.dataRecvTo = vToDate;
					break;
				case "DOCUMENT_DATE_SEARCH":
					searchInfo.req.docDateFrom = vFromDate;
					searchInfo.req.docDateTo = vToDate;
					break;
				case "RETURN_DATE_SEARCH":
					searchInfo.req.retPeriodFrom = vFromDate;
					searchInfo.req.retPeriodTo = vToDate;
					break;
				}
				controller.getView().setBusy(true);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1DataStatus.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var oJsonData = that._bindStatusTableFinal(data, controller);
					controller.byId("tabData").setModel(new JSONModel(oJsonData), "Data");
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			}
		},

		_bindStatusTableFinal: function (data, controller) {
			var oJsonData = [];
			var oConsData = {
				"receivedDate": "",
				"documentDate": "",
				"sapTotal": 0,
				"diff": 0,
				"aspTotal": 0,
				"aspProcessed": 0,
				"aspError": 0,
				"aspInfo": 0,
				"gstnProcessed": 0,
				"gstnError": 0,
				"rectifier": 0
			};
			this._columnVisible(controller);
			for (var i = 0; i < data.resp.length; i++) {
				oJsonData.push({
					"receivedDate": data.resp[i].receivedDate,
					"documentDate": data.resp[i].documentDate,
					"sapTotal": data.resp[i].sapTotal,
					"diff": data.resp[i].diff,
					"aspTotal": data.resp[i].aspTotal,
					"aspProcessed": data.resp[i].aspProcessed,
					"aspError": data.resp[i].aspError,
					"aspInfo": data.resp[i].aspInfo,
					"gstnProcessed": data.resp[i].gstnProcessed,
					"gstnError": data.resp[i].gstnError,
					"rectifier": data.resp[i].rectifier
				});
				oConsData.aspTotal = (oConsData.aspTotal === "" ? 0 : parseInt(oConsData.aspTotal, 10)) +
					(data.resp[i].aspTotal === "" ? 0 : parseInt(data.resp[i].aspTotal, 10));
				oConsData.aspProcessed = (oConsData.aspProcessed === "" ? 0 : parseInt(oConsData.aspProcessed, 10)) +
					(data.resp[i].aspProcessed === "" ? 0 : parseInt(data.resp[i].aspProcessed, 10));
				oConsData.aspError = (oConsData.aspError === "" ? 0 : parseInt(oConsData.aspError, 10)) +
					(data.resp[i].aspError === "" ? 0 : parseInt(data.resp[i].aspError, 10));
				oConsData.aspInfo = (oConsData.aspInfo === "" ? 0 : parseInt(oConsData.aspInfo, 10)) +
					(data.resp[i].aspInfo === "" ? 0 : parseInt(data.resp[i].aspInfo, 10));
				oConsData.gstnProcessed = (oConsData.gstnProcessed === "" ? 0 : parseInt(oConsData.gstnProcessed, 10)) +
					(data.resp[i].gstnProcessed === "" ? 0 : parseInt(data.resp[i].gstnProcessed, 10));
				oConsData.gstnError = (oConsData.gstnError === "" ? 0 : parseInt(oConsData.gstnError, 10)) +
					(data.resp[i].gstnError === "" ? 0 : parseInt(data.resp[i].gstnError, 10));
			}
			oJsonData.unshift(oConsData);
			return oJsonData;
		},

		/*=====================================================================*/
		/*========= Column Visibility of Recieved/Document Date ===============*/
		/*=====================================================================*/
		_columnVisible: function (controller) {
			var oStatusModel = controller.getView().getModel("DataStatus");
			if (controller.byId("slStatusCriteria").getSelectedKey() === "DOCUMENT_DATE_SEARCH") {
				oStatusModel.getData().docDate = true;
			} else {
				oStatusModel.getData().docDate = false;
			}
			oStatusModel.refresh(true);
		},

		/*=====================================================================*/
		/*======== Navigate back to Data status ===============================*/
		/*=====================================================================*/
		_navBack: function (oEvent, controller) {
			controller.byId("dynamicPageId").setVisible(true);
			controller.byId("dyStatusSummary").setVisible(false);
			controller.byId("tabData").setSelectedIndex(null);
		},

		/*=====================================================================*/
		/*======== On Press Status Count ======================================*/
		/*=====================================================================*/
		_pressStatusCount: function (oEvent, controller) {
			var oBundle = controller.getResourceBundle();
			var oObject = oEvent.getSource().getBindingContext("Data").getObject();
			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			controller.byId("slManageCriteria").setSelectedKey(vCriteria);
			var vColIdx = oEvent.getSource().getDependents()[0].getText();
			//oEvent.getSource().getEventingParent().getAggregation("customData")[0].getValue();
			if (vColIdx === "1" || vColIdx === "3" || vColIdx === "4") {
				var vDrs = "drsManage";
				var lManage = "lManageDate";
				var slEntity = "slManageEntity";
				var slGstin = "slManageGSTIN";
				var vTabBar = "InvoiceMangement";
			} else {
				vDrs = "drsManage";
				lManage = "lManageDate";
				slEntity = "slManageEntity";
				slGstin = "slManageGSTIN";
				vTabBar = "InvoiceMangement";
			}
			if (oObject.receivedDate === "" && oObject.documentDate === "") {
				controller.byId(vDrs).setDateValue(controller.byId("drsStatus").getDateValue());
				controller.byId(vDrs).setSecondDateValue(controller.byId("drsStatus").getSecondDateValue());

			} else if (vCriteria !== "DOCUMENT_DATE_SEARCH") {
				controller.byId(vDrs).setDateValue(new Date(oObject.receivedDate));
				controller.byId(vDrs).setSecondDateValue(new Date(oObject.receivedDate));

			} else {
				controller.byId(vDrs).setDateValue(new Date(oObject.documentDate));
				controller.byId(vDrs).setSecondDateValue(new Date(oObject.documentDate));
			}
			if (vCriteria === "RETURN_DATE_SEARCH") {
				controller.byId(vDrs).setDisplayFormat("MMM yyyy");
				controller.byId(lManage).setLabel(oBundle.getText("DateRange1"));
			} else {
				controller.byId(vDrs).setDisplayFormat("MMM dd, yyyy");
				controller.byId(lManage).setLabel(oBundle.getText("DateRange"));
			}
			controller.byId(slEntity).setSelectedKeys(controller.byId("slStatusEntity").getSelectedKeys());
			controller.byId(slGstin).setSelectedKeys(controller.byId("mcbStatusGSTIN").getSelectedKeys());
			controller.byId("idIconTabBar").setSelectedKey(vTabBar);

			controller.file = null;
			switch (vColIdx) {
			case "1":
				controller.byId("sbManage").setSelectedKey("ProcessASP");
				controller.table = controller.section = "";
				ManageASP._getProcessedData(controller, false);
				break;
			case "2":
				break;
			case "3":
				controller.byId("sbManage").setSelectedKey("ErrorASP");
				ManageASP._getErrorData(controller);
				break;
			case "4":
				controller.byId("sbManage").setSelectedKey("ProcessASP");
				controller.table = controller.section = "";
				ManageASP._getProcessedData(controller, true);
				break;
			}
		},

		/*========================================================================================================*/
		/*======== API Summary Call for Single Date ==============================================================*/
		/*========================================================================================================*/
		_apiDetails: function (oEvent, controller) {
			var aDate = [];
			var oObject = oEvent.getSource().getBindingContext("Data").getObject();
			if (oObject.aspProcessed > 0) {
				if (controller.byId("slStatusCriteria").getSelectedKey() === "DOCUMENT_DATE_SEARCH") {
					aDate.push(oObject.documentDate);
				} else {
					aDate.push(oObject.receivedDate);
				}
				this._getDataSummary(aDate, controller);
			} else {
				MessageBox.information("No Process Data to display Summary", {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/*===========================================================*/
		/*======== API Summary Call for Multiple Date ===============*/
		/*===========================================================*/
		_apiSummary: function (controller) {
			var aDate = [];
			var aIndices = controller.byId("tabData").getSelectedIndices();
			if (aIndices.length === 0) {
				MessageToast.show("Atleast one rows should be selected");
				return;
			}
			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			var oData = controller.byId("tabData").getModel("Data").getData();
			for (var i = 0; i < aIndices.length; i++) {
				if (oData[aIndices[i]].aspProcessed > 0) {
					if (vCriteria === "DOCUMENT_DATE_SEARCH") {
						aDate.push(oData[aIndices[i]].documentDate);
					} else {
						aDate.push(oData[aIndices[i]].receivedDate);
					}
				}
			}
			if (aDate.length > 0) {
				this._getDataSummary(aDate, controller);
			} else {
				MessageBox.information("No Process Data to display Summary", {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/*===========================================================*/
		/*======== Get Call to get API Summary Data =================*/
		/*===========================================================*/
		_getDataSummary: function (aDate, controller) {
			var that = this;
			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			var aEntity = controller.byId("slStatusEntity").getSelectedKeys();
			var aGstins = controller.byId("mcbStatusGSTIN").getSelectedKeys();
			var searchInfo = {
				"req": {
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"retPeriodFrom": null,
					"retPeriodTo": null,
					"entityId": aEntity,
					"gstin": aGstins,
					"dates": aDate
				}
			};
			switch (vCriteria) {
			case "RECEIVED_DATE_SEARCH":
				searchInfo.req.dataRecvFrom = controller._formatDate(controller.byId("drsStatus").getDateValue());
				searchInfo.req.dataRecvTo = controller._formatDate(controller.byId("drsStatus").getSecondDateValue());
				break;
			case "DOCUMENT_DATE_SEARCH":
				searchInfo.req.docDateFrom = controller._formatDate(controller.byId("drsStatus").getDateValue());
				searchInfo.req.docDateTo = controller._formatDate(controller.byId("drsStatus").getSecondDateValue());
				break;
			case "RETURN_DATE_SEARCH":
				searchInfo.req.retPeriodFrom = (controller._formatPeriod(controller.byId("drsStatus").getDateValue())).substr(2, 6);
				searchInfo.req.retPeriodTo = (controller._formatPeriod(controller.byId("drsStatus").getSecondDateValue())).substr(2, 6);
				break;
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/apiSummary.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				that._bindSummaryAPI(data.resp, controller);
				controller.byId("dynamicPageId").setVisible(false);
				controller.byId("dyStatusSummary").setVisible(true);
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*===========================================================*/
		/*======== Binding API Summary Data JSON ====================*/
		/*===========================================================*/
		_bindSummaryAPI: function (data, controller) {
			var aSummaryAPI = [],
				oSummary = this._apiSummaryStruc(),
				oJsonData = this._apiSummaryStruc(),
				oConsData = this._apiSummaryStruc();
			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			this._columnVisible(controller);

			for (var i = 0; i < data.length; i++) {
				this._sectionData(oJsonData, data[i], vCriteria);

				if ((i === data.length - 1) ||
					(oJsonData.gstin !== data[i + 1].gstin || oJsonData.period !== data[i + 1].period) ||
					(vCriteria === "DOCUMENT_DATE_SEARCH" && oJsonData.docDate !== data[i + 1].docDate) ||
					(vCriteria !== "DOCUMENT_DATE_SEARCH" && oJsonData.receivedDate !== data[i + 1].receivedDate)
				) {
					this._gstinWiseData(oSummary, oJsonData, vCriteria);
					oJsonData = this._apiSummaryStruc(); //JSON.parse(JSON.stringify(oCustData));
				}
				if ((i === data.length - 1) ||
					(vCriteria === "DOCUMENT_DATE_SEARCH" && oSummary.docDate !== data[i + 1].docDate && oSummary.docDate !== "") ||
					(vCriteria !== "DOCUMENT_DATE_SEARCH" && oSummary.receivedDate !== data[i + 1].receivedDate && oSummary.receivedDate !== "")
				) {
					aSummaryAPI.push(oSummary);
					oConsData.count += oSummary.count;
					oConsData.taxableValue += oSummary.taxableValue;
					oConsData.toatlTaxes += oSummary.toatlTaxes;
					oConsData.igst += oSummary.igst;
					oConsData.cgst += oSummary.cgst;
					oConsData.sgst += oSummary.sgst;
					oConsData.cess += oSummary.cess;
					oSummary = this._apiSummaryStruc(); //JSON.parse(JSON.stringify(oCustData));
				}
			}
			if (aSummaryAPI.length > 1) {
				aSummaryAPI.unshift(oConsData);
			}
			var oApiSummary = new JSONModel(aSummaryAPI);
			controller.byId("tabStatusData").setModel(oApiSummary, "Summary");
		},

		_sectionData: function (oJsonData, data, vCriteria) {
			oJsonData.items.push(data);
			if (oJsonData.receivedDate === "" && vCriteria !== "DOCUMENT_DATE_SEARCH") {
				oJsonData.receivedDate = data.receivedDate;
			}
			if (oJsonData.docDate === "" && vCriteria === "DOCUMENT_DATE_SEARCH") {
				oJsonData.docDate = data.docDate;
			}
			if (oJsonData.gstin === "") {
				oJsonData.gstin = data.gstin;
			}
			if (oJsonData.period === "") {
				oJsonData.period = data.period;
			}
			if (oJsonData.authToken === "") {
				oJsonData.authToken = data.authToken;
			}
			if (oJsonData.status === "") {
				oJsonData.status = data.status;
			}
			oJsonData.flag = 1;
			oJsonData.count = oJsonData.count + data.count;
			oJsonData.taxableValue = oJsonData.taxableValue + data.taxableValue;
			oJsonData.toatlTaxes = oJsonData.toatlTaxes + data.toatlTaxes;
			oJsonData.igst = oJsonData.igst + data.igst;
			oJsonData.cgst = oJsonData.cgst + data.cgst;
			oJsonData.sgst = oJsonData.sgst + data.sgst;
			oJsonData.cess = oJsonData.cess + data.cess;
		},

		_gstinWiseData: function (oSummary, oJsonData, vCriteria) {
			oSummary.items.push(oJsonData);
			if (vCriteria === "DOCUMENT_DATE_SEARCH") {
				oSummary.docDate = oJsonData.docDate;
			} else {
				oSummary.receivedDate = oJsonData.receivedDate;
			}
			oSummary.flag = 0;
			oSummary.count += oJsonData.count;
			oSummary.taxableValue += oJsonData.taxableValue;
			oSummary.toatlTaxes += oJsonData.toatlTaxes;
			oSummary.igst += oJsonData.igst;
			oSummary.cgst += oJsonData.cgst;
			oSummary.sgst += oJsonData.sgst;
			oSummary.cess += oJsonData.cess;
		},

		/*===========================================================*/
		/*======== Toggle API Summary Data ==========================*/
		/*===========================================================*/
		_toggleApiSummary: function (oEvent, controller) {
			var oData = controller.byId("tabStatusData").getModel("Summary").getData();
			var vPath = oEvent.getParameters().rowContext.sPath;
			var aPath = vPath.split("/");
			if (aPath.length === 4) {
				oData[aPath[1]].items[aPath[3]].isBold = !oData[aPath[1]].items[aPath[3]].isBold;
			}
			controller.byId("tabStatusData").getModel("Summary").refresh(true);
		},

		/*========================================================================================================*/
		/*======== Auth Token API for Activation =================================================================*/
		/*========================================================================================================*/
		_pressAuthToken: function (oEvent, controller) {
			var oObject = oEvent.getSource().getEventingParent().getParent().getBindingContext("Summary").getObject();
			if (oObject.authToken !== "Active") {
				var oDialog = new sap.m.Dialog({
					title: "Auth Token",
					type: "Message",
					content: [
						new sap.m.Label({
							text: "Enter OTP",
							labelFor: "inOTP"
						}),
						new sap.m.Input("inOTP", {
							width: "100%"
						})
					],
					beginButton: new sap.m.Button({
						text: "OK",
						press: function () {
							// 			var sText = sap.ui.getCore().byId('rejectDialogTextarea').getValue();
							// 			MessageToast.show('Note is: ' + sText);
							oDialog.close();
						}
					}),
					endButton: new sap.m.Button({
						text: "Cancel",
						press: function () {
							oDialog.close();
						}
					}),
					afterClose: function () {
						oDialog.destroy();
					}
				}).addStyleClass("sapUiSizeCompact");
				oDialog.open();
			}
		},

		/*===========================================================*/
		/*========= Download Templete ===============================*/
		/*===========================================================*/
		_downloadTemplate: function (oEvent, controller) {

		},

		/*===========================================================*/
		/*========= Upload Changes ==================================*/
		/*===========================================================*/
		_uploadChange: function (oEvent, controller) {
			// 			var that = this;
			// 			var file = oEvent.getParameter("files");
			// 			// 			var numfiles = oEvent.getParameter("files").length;
			// 			var files = [];
			// 			that.oModel = controller.byId("ucWebUpload").getModel("UploadCollection");
			// 			$.each(file, function (i, val) {
			// 				// var aData = that.oModel.getData();
			// 				var fixname = val.name;
			// 				var filename = fixname.substring(0, fixname.indexOf("."));
			// 				var extension = fixname.substring(fixname.indexOf(".") + 1);

			// 				var obj = {
			// 					"filename": filename,
			// 					"extension": extension
			// 				};
			// 				files.push(obj);
			// 			});
		},

		/*===========================================================*/
		/*========= Missmatch Upload File ===========================*/
		/*===========================================================*/
		_typeMissmatch: function (oEvent, controller) {
			var aFileTypes = oEvent.getSource().getFileType();
			jQuery.each(aFileTypes, function (key, value) {
				aFileTypes[key] = "*." + value;
			});
			var sSupportedFileTypes = aFileTypes.join(", ");
			MessageToast.show("The file type *." + oEvent.getParameters("files").getParameters("files").fileType +
				" is not supported. Choose one of the following types: " + sSupportedFileTypes);
		},

		/*===========================================================*/
		/*========= Upload File =====================================*/
		/*===========================================================*/
		_uploadFile: function (oEvent, controller) {
			var oFileUploader = controller.getView().byId("ucWebUpload");
			var oRadio = controller.byId("rbgStatus").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/webRawUploadDocuments.do");
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/webB2csUploadDocuments.do");
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/webNilUploadDocuments.do");
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/webAtUploadDocuments.do");
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/webTxpdUploadDocuments.do");
				break;
			case 5:
				oFileUploader.setUploadUrl("/aspsapapi/webInvoiceUploadDocuments.do");
				break;
			case 6:
				oFileUploader.setUploadUrl("/aspsapapi/webHsnUploadDocuments.do");
				break;
			case 7:
				oFileUploader.setUploadUrl("/aspsapapi/webRawGstr2UploadDocuments.do");
				break;
			}
			controller.getView().setBusy(true);
			oFileUploader.upload();
		},

		/*===========================================================*/
		/*========= Upload Complete =================================*/
		/*===========================================================*/
		_uploadComplete: function (oEvent, controller) {
			controller.getView().setBusy(false);
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				controller.getView().byId("ucWebUpload").setValue();
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/*===========================================================*/
		/*========= Getting File Status =============================*/
		/*===========================================================*/
		_getFileStatus: function (controller) {
			// 			var vCriteria = controller.byId("slStatusCriteria").getSelectedKey();
			var vFileTYpe = controller.byId("slFileType").getSelectedKey();
			vFileTYpe = vFileTYpe === "" ? "all" : vFileTYpe;
			// 			if (vCriteria === "RETURN_DATE_SEARCH") {
			// 			var vFromDate = (controller._formatPeriod(controller.byId("drsStatus").getDateValue())).substr(2, 6);
			// 			var vToDate = (controller._formatPeriod(controller.byId("drsStatus").getSecondDateValue())).substr(2, 6);
			// 			} else {
			var vFromDate = controller._formatDate(controller.byId("drsStatus").getDateValue());
			var vToDate = controller._formatDate(controller.byId("drsStatus").getSecondDateValue());
			// 			}
			var searchInfo = {
				"req": {
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"retPeriodFrom": null,
					"retPeriodTo": null,
					"fileType": vFileTYpe
				}
			};
			// 			switch (vCriteria) {
			// 			case "RECEIVED_DATE_SEARCH":
			searchInfo.req.dataRecvFrom = vFromDate;
			searchInfo.req.dataRecvTo = vToDate;
			// 				break;
			// 			case "DOCUMENT_DATE_SEARCH":
			// 				searchInfo.req.docDateFrom = vFromDate;
			// 				searchInfo.req.docDateTo = vToDate;
			// 				break;
			// 			case "RETURN_DATE_SEARCH":
			// 				searchInfo.req.retPeriodFrom = vFromDate;
			// 				searchInfo.req.retPeriodTo = vToDate;
			// 				break;
			// 			}
			var oModel = controller.byId("tabFileStatus").getModel("FileStatus");
			if (oModel) {
				oModel.setData(null);
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/fileStatus.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				controller.byId("tabFileStatus").setModel(new JSONModel(data.resp), "FileStatus");
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*===========================================================*/
		/*========= Navigate to Invoice Management ==================*/
		/*===========================================================*/
		_fileStatusCount: function (oEvent, controller) {
			var oBundle = controller.getResourceBundle(),
				vColIdx = oEvent.getSource().getDependents()[0].getText(),
				oObject = oEvent.getSource().getBindingContext("FileStatus").getObject();

			if (oObject.fileType === "RAW") {
				var vDrs = "drsManage",
					lManage = "lManageDate";

				controller.byId(vDrs).setDateValue(new Date(oObject.updatedOn));
				controller.byId(vDrs).setSecondDateValue(new Date(oObject.updatedOn));
				controller.byId(vDrs).setDisplayFormat("MMM dd, yyyy");
				controller.byId(lManage).setLabel(oBundle.getText("DateRange"));
				controller.byId("idIconTabBar").setSelectedKey("InvoiceMangement");

				controller.table = controller.section = "";
				controller.file = oObject.id;
				switch (vColIdx) {
				case "1":
					controller.byId("sbManage").setSelectedKey("ProcessASP");
					ManageASP._getProcessedData(controller, false);
					break;
				case "2":
					controller.byId("sbManage").setSelectedKey("ErrorASP");
					ManageASP._getErrorData(controller);
					break;
				case "3":
					controller.byId("sbManage").setSelectedKey("ProcessASP");
					ManageASP._getProcessedData(controller, true);
					break;
				}
			}
		}
	};
});