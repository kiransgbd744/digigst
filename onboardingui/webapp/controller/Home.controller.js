sap.ui.define([
	"com/ey/onboarding/controller/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/viz/ui5/format/ChartFormatter",
	"sap/viz/ui5/api/env/Format",
	"sap/ui/Device"
], function (BaseController, JSONModel, ChartFormatter, Format, Device) {
	"use strict";

	return BaseController.extend("com.ey.onboarding.controller.Home", {

		onInit: function () {
			var oViewModel = new JSONModel({
				isPhone: Device.system.phone
			});
			this.setModel(oViewModel, "view");
			Device.media.attachHandler(function (oDevice) {
				this.getModel("view").setProperty("/isPhone", oDevice.name === "Phone");
			}.bind(this));
			this.getOwnerComponent().getRouter().attachRoutePatternMatched(this._onRouteMatched, this);
		},

		onAfterRendering: function () {

		},

		_onRouteMatched: function (oEvent) {
			// 			debugger; //eslint-disable-line
			// var idIframe = this.byId("attachmentframe");
			// this.postMessage();
		}
	});
});