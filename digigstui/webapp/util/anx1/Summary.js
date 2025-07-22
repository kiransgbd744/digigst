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
		/*===================================================================*/
		/*========= Review Summary Table List ===============================*/
		/*===================================================================*/
		_tableList: function () {
			return ["b2c", "b2b", "expt", "expwt", "sezt", "sezwt", "deemExpt"];
		},

		/*===================================================================*/
		/*========= Display Error Message ===================================*/
		/*===================================================================*/
		_errorMessage: function (vMsg) {
			MessageBox.error(vMsg, {
				styleClass: "sapUiSizeCompact"
			});
		},

		_jsonSummStuct: function () {
			return {
				type: "",
				tableSection: "",
				docType: "",
				records: 0,
				invValue: 0,
				taxableValue: 0,
				taxPayble: 0,
				igst: 0,
				cgst: 0,
				sgst: 0,
				cess: 0,
				items: []
			};
		},

		/*===================================================================*/
		/*===================================================================*/
		/*===================================================================*/
		_reviewSummary: function (controller) {
			var that = this;
			var aEntity = controller.byId("slSummEntity").getSelectedKeys();
			var aGstin = controller.byId("slSummGSTIN").getSelectedKeys();
			var vFromDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getDateValue())).substr(2, 6);
			var vToDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getSecondDateValue())).substr(2, 6);
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			controller.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/annexure1Summary.do",
					data: JSON.stringify({
						"req": {
							"entityId": aEntity,
							"gstin": aGstin,
							"fromTaxPeriod": vFromDate,
							"toTaxPeriod": vToDate
						}
					}),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var aData = that._summaryJSON(data.resp, controller);
					var aTabId = that._tableList();
					for (var i = 0; i < aTabId.length; i++) {
						controller.byId(aTabId[i]).setVisibleRowCount(3);
					}
					controller.getView().setModel(new JSONModel(aData), "ReviewSummary");
					controller.getView().setModel(new JSONModel(aData), "Summary");
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			});
		},

		/*===================================================================*/
		/*===================================================================*/
		/*===================================================================*/
		_summaryJSON: function (data, controller) {
			var aData = [];
			for (var vField in data) {
				aData[vField] = [];

				for (var vCol in data[vField]) {
					var oSummJson = this._jsonSummStuct();
					oSummJson.type = vCol;
					if (vCol === "ey") {
						this._eyJSON(data, oSummJson, vField, vCol);
						this._sortJson(oSummJson);
					} else {
						oSummJson.tableSection = "";
						oSummJson.docType = data[vField][vCol].docType;
						oSummJson.records = data[vField][vCol].records;
						oSummJson.invValue = data[vField][vCol].invValue;
						oSummJson.taxableValue = data[vField][vCol].taxableValue;
						oSummJson.taxPayble = data[vField][vCol].taxPayble;
						oSummJson.igst = data[vField][vCol].igst;
						oSummJson.cgst = data[vField][vCol].cgst;
						oSummJson.sgst = data[vField][vCol].sgst;
						oSummJson.cess = data[vField][vCol].cess;
					}
					aData[vField].push(oSummJson);
				}
			}
			return aData;
		},

		_eyJSON: function (data, oSummJson, vField, vCol) {
			if (data[vField][vCol].length > 1) {
				for (var i = 0; i < data[vField][vCol].length; i++) {
					data[vField][vCol][i].type = data[vField][vCol][i].docType;
					data[vField][vCol][i].sNo = Formatter._docTypeSNo(data[vField][vCol][i].docType);
					oSummJson.items.push(data[vField][vCol][i]);
					this._summJSON(oSummJson, data, vField, vCol, i);
				}
			} else if (data[vField][vCol].length === 1) {
				this._summJSON(oSummJson, data, vField, vCol, 0);
			}
		},

		_summJSON: function (oSummJson, data, vField, vCol, i) {
			oSummJson.tableSection = data[vField][vCol][i].tableSection;
			oSummJson.docType = data[vField][vCol][i].docType;
			oSummJson.records += data[vField][vCol][i].records;
			oSummJson.invValue += data[vField][vCol][i].invValue;
			oSummJson.taxableValue += data[vField][vCol][i].taxableValue;
			oSummJson.taxPayble += data[vField][vCol][i].taxPayble;
			oSummJson.igst += data[vField][vCol][i].igst;
			oSummJson.cgst += data[vField][vCol][i].cgst;
			oSummJson.sgst += data[vField][vCol][i].sgst;
			oSummJson.cess += data[vField][vCol][i].cess;
		},

		_sortJson: function (oSummJson) {
			oSummJson.items.sort(function (a, b) {
				return a.sNo - b.sNo;
			});
		},

		/*===================================================================*/
		/*========= Press Toggle Open State =================================*/
		/*===================================================================*/
		_toggleOpenState: function (oEvent) {
			if (oEvent.getParameters().expanded) {
				var aData = oEvent.getSource().getBinding().getModel("Summary").getData();
				var vPath = oEvent.getSource().getBinding().getPath();
				var vLength = oEvent.getSource().getVisibleRowCount() + aData[(vPath.split("/")[1])][0].items.length;
				if (vLength < 10) {
					oEvent.getSource().setVisibleRowCount(vLength);
				} else {
					oEvent.getSource().setVisibleRowCount(10);
				}
				oEvent.getSource().setProperty("expandFirstLevel", true);
			} else {
				// var vKey = oEvent.getSource().getEventingParent().getHeaderToolbar().getContent()[2].getSelectedKey();
				// if (vKey === "") {
				oEvent.getSource().setVisibleRowCount(3);
				// } else {
				// 	oEvent.getSource().setVisibleRowCount(1);
				// }
				oEvent.getSource().setProperty("expandFirstLevel", false);
			}
		}
	};
});