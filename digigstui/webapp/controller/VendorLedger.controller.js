sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (Controller, BaseController, Formatter, JSONModel, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.VendorLedger", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.VendorLedger
		 */
		onInit: function () {
			
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "SummTab");
		},

		onAfterRendering: function () {
			
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "SummTab");
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.VendorLedger
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.VendorLedger
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.VendorLedger
		 */
		//	onExit: function() {
		//
		//	}

	});

});