sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.transMonitoringReportInward", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.transMonitoringReportOutward
		 */
		onInit: function () {
			var that = this,
				vDate = new Date();
			this.getOwnerComponent().getRouter().getRoute("transMonitoringReportInward").attachPatternMatched(this._onRouteMatched, this);

			// 			Processed Records
			this.byId("dtTaxPeriod").setMaxDate(vDate);
			this.byId("dtTaxPeriod").setDateValue(vDate);

			this.byId("dtTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtTaxPeriod").$().find("input").attr("readonly", true);
				}
			});
		},

		_onRouteMatched: function (oEvent) {
			var oName = oEvent.getParameter("name");
			if (oName === "transMonitoringReportInward") {
				this._getTransOutward();
			}
		},

		onSearch: function() {
			this._getTransOutward();
		},

		onPressClear: function() {
			var vDate = new Date();
			this.byId("slGstin").setSelectedKeys(null);
			this.byId("dtTaxPeriod").setMaxDate(vDate);
			this.byId("dtTaxPeriod").setDateValue(vDate);
			this.byId("idSourceID").setValue("");
			this.byId("idStatus").setSelectedKeys(null);

			this._getTransOutward();
		},

		_getTransOutward: function () {
			var that = this;
			var aGstin = this.byId("slGstin").getSelectedKeys();
			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": aGstin.includes("All") ? [] : aGstin,
					"taxPeriod": this.byId("dtTaxPeriod").getValue(),
					"sourceId": this.byId("idSourceID").getValue(),
					"status": this.byId("idStatus").getSelectedKeys()
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getTransactionalInwardDocs.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {

					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "transInward");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

			// var data = {

			// 	"resp": [{
			// 		"gstin": "29AAAPH9357H1A2",
			// 		"taxPeriod": "092020",
			// 		"sourceID": 1,
			// 		"status": "1",
			// 		"status": "ASP Error",
			// 		"docType": "INV",
			// 		"supplyType": "TAX",
			// 		"erpInvNo": 1,
			// 		"erpTotalInvValue": "0.00",
			// 		"erpAccValue": "0.00",
			// 		"erpIgst": "0.00",
			// 		"erpCgst": "0.00",
			// 		"erpSgst": "0.00",
			// 		"erpCess": "0.00",
			// 		"cloudInvNo": 1,
			// 		"cloudTotalInvValue": "0.00",
			// 		"cloudAccValue": "0.00",
			// 		"cloudIgst": "0.00",
			// 		"cloudCgst": "0.00",
			// 		"cloudSgst": "0.00",
			// 		"cloudCess": "0.00",
			// 	}]
			// };
			// this.getView().setModel(new JSONModel(data), "transInward");
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.transMonitoringReportOutward
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.transMonitoringReportOutward
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.transMonitoringReportOutward
		 */
		//	onExit: function() {
		//
		//	}

	});

});