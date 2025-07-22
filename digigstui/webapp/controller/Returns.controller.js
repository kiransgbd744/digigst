sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/anx2/anx2Data",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, anx2Data, Formatter, JSONModel, MessageBox) {
	"use strict";

	var _aValidTabKeys = ["ANX1", "GSTR1", "GSTR1A", "GSTR3B", "GSTR6", "GSTR2", "GSTR7", "GSTR7_TXN", "GSTR8", "ITC04", "GSTR9"];

	return BaseController.extend("com.ey.digigst.controller.Returns", {
		Formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Returns
		 */
		onInit: function () {
			this.getView().setModel(new JSONModel(), "view");
			this.getOwnerComponent().getRouter().getRoute("Returns").attachPatternMatched(this._onRouteMatched, this);
		},

		//Changes doen by Vinay Kodam 16-04-2020//
		_onRouteMatched: function (oEvent) {
			var oHashChanger = sap.ui.core.routing.HashChanger.getInstance(),
				oArg = oEvent.getParameter("arguments").contextPath,
				key = oEvent.getParameter("arguments").key;

			this.getView().setModel(new JSONModel({
				"key": key
			}), "ReturnKey");

			if (_aValidTabKeys.includes(oArg)) {
				this.getView().getModel("view").setProperty("/selectedTabKey", oArg);
				this.getRouter().getTargets().display(oArg);
			}
			oHashChanger.setHash("Returns/" + oArg + "/" + key);
		},

		handleIconTabBarSelect: function (oEvent) {
			this.getRouter().navTo("Returns", {
				contextPath: oEvent.getParameter("selectedKey"),
				key: "a"
			}, true);
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Returns
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Returns
		 */
		// onAfterRendering: function () {
		// },

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Returns
		 */
		//	onExit: function() {
		//
		//	}

	});
});