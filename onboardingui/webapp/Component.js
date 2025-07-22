sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/Device",
	"com/ey/onboarding/model/models",
	"sap/ui/core/routing/History",
	"sap/ui/model/resource/ResourceModel",
	"com/ey/onboarding/controller/ListSelector",
	"com/ey/onboarding/controller/ErrorHandler",
	"com/ey/onboarding/util/xlsx",
	"com/ey/onboarding/util/Kendo",
	"sap/ui/model/json/JSONModel",
	"sap/ui/thirdparty/jquery"
], function (UIComponent, Device, models, History, ResourceModel, JSONModel, jquery, ListSelector, ErrorHandler, XLSX, Kendo) {
	"use strict";

	return UIComponent.extend("com.ey.onboarding.Component", {

		metadata: {
			manifest: "json",
			dependencies: {
				libs: [
					"sap.f",
					"sap.m",
					"sap.ui.layout",
					"sap.ui.comp"
				]
			},
			config: {
				sample: {
					stretch: true,
					files: [
						"uploadCollection.json",
						"LinkedDocuments/Business Plan Agenda.doc",
						"LinkedDocuments/Business Plan Topics.xls",
						"LinkedDocuments/Document.txt",
						"LinkedDocuments/Instructions.pdf",
						"LinkedDocuments/Notes.txt",
						"LinkedDocuments/Screenshot.jpg",
						"LinkedDocuments/Third Quarter Results.ppt"
					]
				}
			}
		},

		/**
		 * The component is initialized by UI5 automatically during the startup of the app and calls the init method once.
		 * @public
		 * @override
		 */
		init: function () {
			// call the base component's init function
			UIComponent.prototype.init.apply(this, arguments);

			// enable routing
			this.getRouter().initialize();

			// set the device model
			this.setModel(models.createDeviceModel(), "device");
		},

		myNavBack: function () {
			var oHistory = History.getInstance();
			var oPrevHash = oHistory.getPreviousHash();
			if (oPrevHash !== undefined) {
				window.history.go(-1);
			} else {
				this.getRouter().navTo("masterSettings", {}, true);
			}
		},

		getContentDensityClass: function () {
			if (!this._sContentDensityClass) {
				if (!sap.ui.Device.support.touch) {
					this._sContentDensityClass = "sapUiSizeCompact";
				} else {
					this._sContentDensityClass = "sapUiSizeCozy";
				}
			}
			return this._sContentDensityClass;
		}
	});
});