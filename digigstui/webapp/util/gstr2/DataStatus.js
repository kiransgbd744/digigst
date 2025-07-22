sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
], function (JSONModel, MessageBox, MessageToast) {
	"use strict";

	return {
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
			debugger; //eslint-disable-line no-debugger
			controller.byId("fbStatusPR").setVisible(!vFlag);
			controller.byId("hbStatusPR").setVisible(vFlag);
			controller.byId("fuPR").setVisible(vFlag);
			controller.byId("btnUploadPR").setVisible(vFlag);

			if (vSb === "A") {
				controller.byId("tabDataPR").setVisible(true);
			} else {
				controller.byId("tabDataPR").setVisible(false);
			}
			if (vSb === "U" || vSb === "F") {
				controller.byId("sbUpload").setVisible(true);
			} else {
				controller.byId("sbUpload").setVisible(false);
			}
			if (vSb === "F" || vSb === "S") {
				controller.byId("tabFileStatPR").setVisible(true);
				controller.byId("iEntityPR").setVisible(false);
				controller.byId("iGstinPR").setVisible(false);
				controller.byId("idFileType").setVisible(true);
			} else {
				controller.byId("tabFileStatPR").setVisible(false);
				controller.byId("iEntityPR").setVisible(true);
				controller.byId("iGstinPR").setVisible(true);
				controller.byId("idFileType").setVisible(false);
			}
		},

		/*=====================================================================*/
		/*======= Ajax call to get data from cloud ============================*/
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
			var vCriteria = controller.byId("slStatsCriteriaG2").getSelectedKey();
			var aEntity = controller.byId("slStatEntityG2").getSelectedKeys();
			var aGstin = controller.byId("slStatGstnG2").getSelectedKeys();

			if (vCriteria === "RETURN_DATE_SEARCH") {
				var vFromDate = (controller._formatPeriod(controller.byId("drsStatsGstr2").getDateValue())).substr(2, 6);
				var vToDate = (controller._formatPeriod(controller.byId("drsStatsGstr2").getSecondDateValue())).substr(2, 6);
			} else {
				vFromDate = controller._formatDate(controller.byId("drsStatsGstr2").getDateValue());
				vToDate = controller._formatDate(controller.byId("drsStatsGstr2").getSecondDateValue());
			}

			var oModel = controller.byId("tabDataPR").getModel("Data");
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
					url: "/aspsapapi/getGstr2DataStatus.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var oJsonData = that._bindStatusTableFinal(data, controller);
					controller.byId("tabDataPR").setModel(new JSONModel(oJsonData), "Data");
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

		_getFileStatus: function (controller) {

		}
	};
});