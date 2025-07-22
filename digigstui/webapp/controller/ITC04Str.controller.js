sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox"
], function (Controller, JSONModel, Fragment, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.ITC04Str", {

		onInit: function () {
			this.byId("frStockTrade").addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
			this.byId("toStockTrade").addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
			this._bindDefaultData();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onSearchStockTrade();
			}
		},

		_bindDefaultData: function () {
			var today = new Date(),
				date = new Date(),
				m = today.getMonth(),
				d = today.getDate();

			date.setMonth(date.getMonth() - 1);
			this.getView().setModel(new JSONModel({
				"gstin": [],
				"criteria": "ChallanDate",
				"fy": null,
				"frChallanDate": this._getFullDate(date),
				"toChallanDate": this._getFullDate(today),
				"frReturnPeriod": "Q1",
				"toReturnPeriod": "Q1",
				"maxDate": new Date(),
				"minDate": new Date(date.getFullYear(), date.getMonth(), date.getDate())
			}), "FilterModel");
		},

		onFromDateChange: function (evt) {
			var oModel = this.getView().getModel("FilterModel");
			if (oModel.getProperty("/frChallanDate") > oModel.getProperty("/toChallanDate")) {
				oModel.setProperty("/toChallanDate", oModel.getProperty("/frChallanDate"));
			}
			oModel.setProperty("/minDate", new Date(oModel.getProperty("/frChallanDate")));
			oModel.refresh(true);
		},

		onSearchStockTrade: function () {
			var oFilterModel = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"requestType": oFilterModel.criteria,
						"fy": oFilterModel.fy,
						"fromChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.frChallanDate : undefined),
						"toChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.toChallanDate : undefined),
						"fromReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.frReturnPeriod : undefined),
						"toReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.toReturnPeriod : undefined),
						"dataSecAttrs": {
							GSTIN: this.removeAll(oFilterModel.gstin)
						}
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					"method": "POST",
					"url": "/aspsapapi/itc04StockTracking.do",
					"contentType": "application/json",
					"data": JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oData = (typeof (data) === "string" ? JSON.parse(data) : data);
					if (oData.hdr.status === "S") {
						this.getView().setModel(new JSONModel(oData), "StockTradeRpt");
					} else {
						MessageBox.error(oData.errMsg || oData.resp);
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onInitiateReport: function () {
			var oFilterModel = this.getView().getModel("FilterModel").getProperty("/"),
				aIndex = this.byId("tabStkRpt").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"requestType": oFilterModel.criteria,
						"fy": oFilterModel.fy,
						"fromChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.frChallanDate : undefined),
						"toChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.toChallanDate : undefined),
						"fromReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.frReturnPeriod : undefined),
						"toReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.toReturnPeriod : undefined),
						"dataSecAttrs": {
							GSTIN: []
						}
					}
				};
			if (!aIndex.length) {
				MessageBox.error("Select atleast one GSTIN to Initiate report.");
				return;
			}
			var oData = this.getModel("StockTradeRpt").getProperty("/resp");
			payload.req.dataSecAttrs.GSTIN = aIndex.map(function (e) {
				return oData[e].gstin;
			});

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					"method": "POST",
					"url": "/aspsapapi/itc04initiateReport.do",
					"contentType": "application/json",
					"data": JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oData = (typeof (data) === "string" ? JSON.parse(data) : data);
					if (oData.hdr.status === "S") {
						MessageBox.success(data.statusMsg);
					} else {
						MessageBox.error(oData.errMsg);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (["Inactive", "I"].includes(authToken)) {
				this.confirmGenerateOTP(gstin);
			}
		},

		onDownloadReport: function (oEvent) {
			var key = oEvent.getParameter('item').getKey(),
				oFilterModel = this.getView().getModel("FilterModel").getProperty("/"),
				aIndex = this.byId("tabStkRpt").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"requestType": oFilterModel.criteria,
						"fy": oFilterModel.fy,
						"fromChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.frChallanDate : undefined),
						"toChallanDate": (oFilterModel.criteria === "ChallanDate" ? oFilterModel.toChallanDate : undefined),
						"fromReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.frReturnPeriod : undefined),
						"toReturnPeriod": (oFilterModel.criteria === "ReturnPeriod" ? oFilterModel.toReturnPeriod : undefined),
						"dataSecAttrs": {
							GSTIN: []
						}
					}
				};
			if (!aIndex.length) {
				MessageBox.error("Select atleast one GSTIN to Download report.");
				return;
			}
			var oData = this.getModel("StockTradeRpt").getProperty("/resp");
			payload.req.dataSecAttrs.GSTIN = aIndex.map(function (e) {
				return oData[e].gstin;
			});

			if (key === "stockTradeRpt") {
				var url = "/aspsapapi/itc04sttrdownloadrpt.do"
			}
			this.reportDownload(payload, url);
		},

		_getFullDate: function (date) {
			var m = date.getMonth(),
				d = date.getDate();
			return (date.getFullYear() + "-" + (m < 9 ? '0' : '') + (m + 1) + "-" + (d < 10 ? '0' : '') + d);
		}
	});
});