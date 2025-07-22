sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel"
], function (BaseController, JSONModel) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.AuthToken", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.AuthToken
		 */
		onInit: function () {

		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.AuthToken
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.AuthToken
		 */
		onAfterRendering: function () {
			this.getOwnerComponent().getRouter().getRoute("AuthToken").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.AuthToken
		 */
		//	onExit: function() {
		//
		//	}

		_onRouteMatched: function (oEvent) {
			var that = this;
			$(document).ready(function ($) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/entities.do",
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oEntityModel = new JSONModel(data);
					that.byId("slEntity").setModel(oEntityModel, "EntityModel");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onChangeEntity: function (oEvent) {
			var that = this;
			var aEntity = oEvent.getSource().getSelectedKeys();
			$(document).ready(function ($) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstins.do",
					data: JSON.stringify({
						"req": {
							"entityId": aEntity
						}
					}),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oSupplierGSTIN = new JSONModel(data);
					that.byId("slGstin").setModel(oSupplierGSTIN, "SuppGstinModel");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onClearFilter: function (oEvent) {
			this.byId("slEntity").setSelectedKeys([]);
			this.byId("slGstin").setSelectedKeys([]);
			var oModel = this.byId("slGstin").getModel("SuppGstinModel");
			oModel.setData([]);
			oModel.refresh(true);
		},

		onSearch: function (oEvent) {

		},

		getIndex: function (vEntity, vGstin) {
			var oBinding = this.byId("tabManageToken").getBinding("items");
			var vIndex = -1;
			$.each(oBinding.oList, function (index, object) {
				if (object.entityId === vEntity && object.gstin === vGstin) {
					vIndex = index + 1;
				}
			});
			return vIndex;
		},

		onPressGenerate: function (oEvent) {
			var oObject = oEvent.getSource().getBindingContext("AuthToken").getObject();
			if (oObject.authToken !== "Active") {
				var oDialog = new sap.m.Dialog({
					title: "Auth Token",
					type: "Message",
					content: [
						new sap.m.Label({
							text: "Enter OTP",
							labelFor: "inOTP"
						}),
						new sap.m.Input("inOTP", {
							width: "100%"
						})
					],
					beginButton: new sap.m.Button({
						text: "OK",
						press: function () {
							// 			var sText = sap.ui.getCore().byId('rejectDialogTextarea').getValue();
							// 			MessageToast.show('Note is: ' + sText);
							oDialog.close();
						}
					}),
					endButton: new sap.m.Button({
						text: "Cancel",
						press: function () {
							oDialog.close();
						}
					}),
					afterClose: function () {
						oDialog.destroy();
					}
				}).addStyleClass("sapUiSizeCompact");
				oDialog.open();
			}
		}
	});
});