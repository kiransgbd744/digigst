sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, MessageBox, Formatter, JSONModel, Button, Dialog) {
	"use strict";
	return BaseController.extend("com.ey.digigst.controller.TDSTCSvsGSTR1Summary", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.TDSTCSvsGSTR1Summary
		 */
		onInit: function () {

		},

		onAfterRendering: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("dpGstr1vs3Summary").setVisible(false);
			this._getProcessedData();

		},
		onPressBack: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("dpGstr1vs3Summary").setVisible(false);
		},
		_getProcessedData: function () {

			debugger; //eslint-disable-line
			// var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod").getValue();
			// if (oTaxPeriodFrom === "") {
			// 	oTaxPeriodFrom = null;
			// }
			// var oTaxPeriodTo = this.getView().byId("idPTotaxPeriod").getValue();
			// if (oTaxPeriodTo === "") {
			// 	oTaxPeriodTo = null;
			// }

			// var aGstin = this.getView().byId("idPGstin").getSelectedKeys();

			// var searchInfo = {
			// 	"req": {
			// 		"entityId": [$.sap.entityID],
			// 		"taxPeriodFrom": oTaxPeriodFrom,
			// 		"taxPeriodTo": oTaxPeriodTo,
			// 		"tableType": [],
			// 		"dataSecAttrs": {
			// 			"GSTIN": aGstin.includes("All") ? [] : aGstin
			// 		}
			// 	}
			// };
			// var that = this;

			// if (this.byId("dAdapt")) {
			// 	this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
			// }
			// sap.ui.core.BusyIndicator.show(0);
			// $.ajax({
			// 	method: "POST",
			// 	url: "/aspsapapi/getProcessedVsSubmittedData.do",
			// 	contentType: "application/json",
			// 	data: JSON.stringify(searchInfo)
			// }).done(function (data, status, jqXHR) {
			// 	sap.ui.core.BusyIndicator.hide();
			// 	if (data.resp === undefined) {
			// 		that.getView().setModel(new JSONModel(null), "ProcessedRecord");
			// 	} else {
			// 		that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
			// 	}

			// }).fail(function (jqXHR, status, err) {
			// 	sap.ui.core.BusyIndicator.hide();
			// 	MessageBox.error("Error : gstr1VsGstr3bProcessSummary");
			// });

			var data = {
				"resp": [{
					"gstin": "29AAAPH9357H000"
				}]
			}
			this.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
		},
		
		onPressGstr1Summary: function (oEvent) {
			debugger; //eslint-disable-line
			// var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
			this.byId("dpGstr1vs3Summary").setVisible(true);


			// this.byId("idSummaryGstin").setSelectedKey(obj.gstin);

			// var searchInfo = {
			// 	"req": {
			// 		"entityId": [$.sap.entityID],
			// 		"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
			// 		"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
			// 		"dataSecAttrs": {
			// 			"GSTIN": [obj.gstin]
			// 		}
			// 	}
			// };

			// this._getProcessSummary(searchInfo);
		},


		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.TDSTCSvsGSTR1Summary
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.TDSTCSvsGSTR1Summary
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.TDSTCSvsGSTR1Summary
		 */
		//	onExit: function() {
		//
		//	}

	});

});