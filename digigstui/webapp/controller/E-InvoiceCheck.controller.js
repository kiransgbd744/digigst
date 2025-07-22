sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
], function (Controller, JSONModel) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.E-InvoiceCheck", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.E-InvoiceCheck
		 */
		onInit: function () {
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "InvoiceCheck");
		},

		fnMenuItemPress: function (oEvent) {
			var that = this;
			var oButton = oEvent.getSource();
			if (oButton.getId().indexOf("idOutward") > -1) {
				this.getView().byId("idOutward").addStyleClass("HomeCSS");
				this.getView().byId("idOutward").addStyleClass("btnClr3");
				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax1").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef1").removeStyleClass("HomeCSS");
				this.getView().byId("idComp1").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN1").removeStyleClass("HomeCSS");
			} else if (oButton.getId().indexOf("idInward") > -1) {
				oData.Footer = true;
				this.getView().byId("idInward").addStyleClass("HomeCSS");
				this.getView().byId("idInward").addStyleClass("btnClr3");

				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");

				oData.In = true;
				this.loadInwardData();
			} else if (oButton.getId().indexOf("idTaxPaid") > -1) {
				oData.Tax = true;
				oData.Footer = true;
				this.getView().byId("idTaxPaid").addStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");

				this.TaxPaidTabBind();
			} else if (oButton.getId().indexOf("idPYTransaction") > -1) {
				oData.PY = true;
				oData.Footer = true;
				this.getView().byId("idPYTransaction").addStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");
				this.PYTabBind();
			} else if (oButton.getId().indexOf("idDiffTax") > -1) {
				oData.Diff = true;
				oData.Footer = true;
				this.getView().byId("idDiffTax").addStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");

				this.onloadDiffTax();
			} else if (oButton.getId().indexOf("idDemandRef") > -1) {
				oData.DemR = true;
				oData.Footer = true;
				this.getView().byId("idDemandRef").addStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");

				this.onloadDemandRefTax();
			} else if (oButton.getId().indexOf("idComp") > -1) {
				oData.Comp = true;
				oData.Footer = true;
				this.getView().byId("idComp").addStyleClass("HomeCSS");
				this.getView().byId("idComp").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");
				this.getView().byId("idHSN").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.getView().byId("idHSN").removeStyleClass("btnClr3");

				this.onloadCompDmdTax();
			} else if (oButton.getId().indexOf("idHSN") > -1) {
				oData.HSN = true;
				oData.Footer = true;
				this.getView().byId("idHSN").addStyleClass("HomeCSS");
				this.getView().byId("idHSN").addStyleClass("btnClr3");

				this.getView().byId("idInward").removeStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
				this.getView().byId("idComp").removeStyleClass("HomeCSS");
				this.getView().byId("idOutward").removeStyleClass("HomeCSS");

				this.getView().byId("idInward").removeStyleClass("btnClr3");
				this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
				this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
				this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
				this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
				this.getView().byId("idComp").removeStyleClass("btnClr3");
				this.getView().byId("idOutward").removeStyleClass("btnClr3");
				this.onLoadHSNSummary();
			}

		},

	});

});