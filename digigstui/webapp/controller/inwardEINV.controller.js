sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel"
], function (Controller, JSONModel) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.inwardEINV", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.inwardEINV
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("inwardEINV").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.inwardEINV
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.inwardEINV
		 */
		// onAfterRendering: function () {
		// 
		// },

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger(),
				itb = this.byId("itbInward").getSelectedKey(),
				oView = this.getView().byId(itb);

			oHashChanger.setHash("inwardEINV");
			if (oView) {
				var oController = oView.getController();
				if (oController && oController._setDefaultFilter) {
					oController._setDefaultFilter();
				}
			}
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._getIsdGstin();
			}
		},

		_getIsdGstin: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstinListIrn.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"entityId": $.sap.entityID
						}
					})
				})
				.done(function (data, status, jqXHR) {
					var oData = $.extend(true, {}, data.resp);
					data.resp.unshift({
						gstin: 'All'
					});
					this.getView().setModel(new JSONModel(data.resp), "IRNGstin");
					this.getView().setModel(new JSONModel(oData), "IsdGstin");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSelectEInvTabBar: function () {
			var oView = this.getView().byId("getInward");
			if (oView) {
				var oController = oView.getController();
				if (oController && oController._setDefaultFilter) {
					oController._setDefaultFilter();
				}
			}
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.inwardEINV
		 */
		//	onExit: function() {
		//
		//	}
	});
});