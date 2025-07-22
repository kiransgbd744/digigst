sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel"
], function (Controller, JSONModel) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.gstr1outward", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.gstr1outward
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("gstr1outward").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function () {
			var sRedirectUrl = $.sap.gstr1outwardURL;
			var sIframeId = this.getView().byId(this.createId("outId")).getId();
			$("#" + sIframeId).attr("src", sRedirectUrl);
			/*var BulkGstinSaveModel = new JSONModel();
			BulkGstinSaveModel.setData({
				"url": $.sap.gstr1outwardURL
			});
			this.getView().setModel(BulkGstinSaveModel, "SACURL");*/
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.gstr1outward
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.gstr1outward
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.gstr1outward
		 */
		//	onExit: function() {
		//
		//	}

	});

});