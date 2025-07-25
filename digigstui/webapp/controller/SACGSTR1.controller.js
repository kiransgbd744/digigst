sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.SACGSTR1", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.SACGSTR1
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("SACGSTR1").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oName = oEvent.getParameter("name");
			//	var oContextPath = oEvent.getParameter("arguments").contextPath;
			if (oName === "SACGSTR1") {}
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.SACGSTR1
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.SACGSTR1
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.SACGSTR1
		 */
		//	onExit: function() {
		//
		//	}

	});

});