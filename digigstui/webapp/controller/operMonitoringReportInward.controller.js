sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.operMonitoringReportInward", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.operMonitoringReportInward
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("operMonitoringReportInward").attachPatternMatched(this._onRouteMatched, this);

			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iFromDate").setMaxDate(new Date());
			this.byId("iToDate").setDateValue(new Date());
			this.byId("iToDate").setMinDate(vDate);
			this.byId("iToDate").setMaxDate(new Date());
			
			this.byId("iExtractionFromDate").setDateValue(vDate);
			this.byId("iExtractionFromDate").setMaxDate(new Date());
			this.byId("iExtractionToDate").setDateValue(new Date());
			this.byId("iExtractionToDate").setMinDate(vDate);
			this.byId("iExtractionToDate").setMaxDate(new Date());
		},

		_onRouteMatched: function (oEvent) {
			var oName = oEvent.getParameter("name");
			if (oName === "operMonitoringReportInward") {
				this._getDailyReconResults();
			}
		},

		onSearch: function (oEvent) {
			this._getDailyReconResults();
		},
		onChangeDateValue: function (oEvent) {
			debugger; //eslint-disable-line
			var vDatePicker;
			if (oEvent.getSource().getId().includes("iFromDate")) {
				vDatePicker = "iToDate";
			}else if (oEvent.getSource().getId().includes("iExtractionFromDate")) {
				vDatePicker = "iExtractionToDate";
			}

			var fromDate = oEvent.getSource().getDateValue(),
				toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},
		
		_getDailyReconResults: function () {

			var that = this;
			var postData = {
				"req": {
					"accVoucherDateFrom": this.byId("iFromDate").getValue(),
					"accVoucherDateTo": this.byId("iToDate").getValue(),
					"extractionDateFrom": this.byId("iExtractionFromDate").getValue(),
					"extractionDateTo": this.byId("iExtractionToDate").getValue(),
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getDailyInwardReconResults.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {

					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "DailyReconResults");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onPressClear: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iFromDate").setMaxDate(new Date());
			this.byId("iToDate").setDateValue(new Date());
			this.byId("iToDate").setMinDate(vDate);
			this.byId("iToDate").setMaxDate(new Date());

			this._getDailyReconResults();
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.operMonitoringReportInward
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.operMonitoringReportInward
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.operMonitoringReportInward
		 */
		//	onExit: function() {
		//
		//	}

	});

});