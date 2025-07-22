sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.Outward_Consolidated_Report", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Outward_Consolidated_Report
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("Outward_Consolidated_Report").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function () {
			var sRedirectUrl = $.sap.gstr1inoutwardURL;
			var sIframeId = this.getView().byId(this.createId("gstr1out")).getId();
			$("#" + sIframeId).attr("src", sRedirectUrl);
			/*var oReqWisePath = "/aspsapapi/callSacGstr1OutwardReport.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: [{}]
				}).done(function (data, status, jqXHR) {

				}).fail(function (jqXHR, status, err) {});
			});*/
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Outward_Consolidated_Report
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Outward_Consolidated_Report
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Outward_Consolidated_Report
		 */
		//	onExit: function() {
		//
		//	}

	});

});