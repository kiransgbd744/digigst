sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/json/JSONModel"
], function (Controller, MessageBox, JSONModel) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.B2C", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.B2C
		 */
		onInit: function () {
			//this.defultBind();
		},

		onAfterRendering: function () {
			this.defultBind();
		},

		defultBind: function () {
			var that = this;
			//var GSTR3BModel = new JSONModel();
			//var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			//that.byId("labId").setText("Created on : 08/03/2021 05:39:11 PM");
			var GstnsList = "/aspsapapi/getOnBoardingParams.do?entityId=" + $.sap.entityID;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList,
					contentType: "application/json",
					//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
					var data1 = data.resp;
					if (data1.transMode === "15") {
						that.byId("RB31").setSelectedIndex(0);
					} else {
						that.byId("RB31").setSelectedIndex(1);
					}
					that.byId("payId").setValue(data1.payeeAddress);
					that.byId("payName").setValue(data1.payeeName);
					that.byId("payMer").setValue(data1.payeeMerCode);
					if (data1.transQRMed === "02") {
						that.byId("RB4").setSelectedIndex(0);
					} else if (data1.transQRMed === "03") {
						that.byId("RB4").setSelectedIndex(1);
					} else if (data1.transQRMed === "04") {
						that.byId("RB4").setSelectedIndex(2);
					} else if (data1.transQRMed === "05") {
						that.byId("RB4").setSelectedIndex(3);
					} else if (data1.transQRMed === "06") {
						that.byId("RB4").setSelectedIndex(4);
					}
					that.byId("QrId").setValue(data1.qrExpiry);
					that.byId("labId").setText(data1.createdOn);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onSubmit: function () {
			debugger;
			var that = this;
			var transMode, RB2 = this.byId("RB31").getSelectedButton().getText(),
				transQRMed, RB3 = this.byId("RB4").getSelectedButton().getText();

			var val = this.byId("QrId").getValue();
			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!val.match(reg)) {
				MessageBox.error("Negative values are not allowed");
				return;
			}

			if (this.byId("payId").getValue() === "") {
				MessageBox.information("Please Enter Payee Address");
				return;
			}
			if (this.byId("payName").getValue() === "") {
				MessageBox.information("Please Enter Payee Name");
				return;
			}
			if (this.byId("payMer").getValue() === "") {
				MessageBox.information("Please Enter Payee Merchant Code");
				return;
			}
			if (this.byId("QrId").getValue() === "") {
				MessageBox.information("Please Enter QR code Expire Time");
				return;
			}

			if (RB2 === "15= Dynamic QR Code (Offline) - For Other and E-com Taxpayer") {
				transMode = "15";
			} else if (RB2 === "22 = Online Dynamic QR Code") {
				transMode = "22";
			}

			if (RB3 === "02 - APP") {
				transQRMed = "02";
			} else if (RB3 === "03 - POS") {
				transQRMed = "03";
			} else if (RB3 === "04 - Physical / Share Intent mode") {
				transQRMed = "04";
			} else if (RB3 === "05 - ATM") {
				transQRMed = "05";
			} else if (RB3 === "06 - WEB") {
				transQRMed = "06";
			}

			var SelectedData = {
				"req": {
					"entityId": $.sap.entityID,
					"qrMode": "Dynamic",
					"transMode": transMode,
					"payeeAddress": this.byId("payId").getValue(),
					"payeeName": this.byId("payName").getValue(),
					"payeeMerCode": this.byId("payMer").getValue(),
					"transQRMed": transQRMed,
					"qrExpiry": this.byId("QrId").getValue(),

				}
			};
			//var GSTR3BModel = new JSONModel();
			//var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveB2COnboardingParams.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
					/*that.byId("payId").setValue();
					that.byId("payName").setValue();
					that.byId("payMer").setValue();
					that.byId("QrId").setValue();*/
					if (JSON.parse(data).hdr.status === "S") {
						MessageBox.success(JSON.parse(data).resp);
						that.defultBind();
					} else {
						MessageBox.error(JSON.parse(data).resp);
					}
					//GSTR3BModel.setData(data);
					//oTaxReGstnView.setModel(GSTR3BModel, "GSTR3B");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.B2C
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.B2C
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.B2C
		 */
		//	onExit: function() {
		//
		//	}

	});

});