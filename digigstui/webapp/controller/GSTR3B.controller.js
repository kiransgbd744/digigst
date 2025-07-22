sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/gstr3b/OutwardSupply",
	"sap/ui/model/json/JSONModel"
], function (BaseController, OutwardSupply, JSONModel) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GSTR3B", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR3B
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("GSTR3B").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.GSTR3B
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTR3B
		 */
		onAfterRendering: function () {
			var vDate = new Date();
			vDate.setMonth(vDate.getMonth() - 1);

			// 			GSTR 3B Summary
			this.byId("drsTaxPeriod").setDateValue(vDate);
			this.byId("drsTaxPeriod").setSecondDateValue(vDate);
			this.byId("drsTaxPeriod").setMaxDate(new Date());
			// 			Outward Supply
			this.byId("outDrsPeriod").setDateValue(vDate);
			this.byId("outDrsPeriod").setSecondDateValue(vDate);
			this.byId("outDrsPeriod").setMaxDate(new Date());
			//          Inward Supply
			this.byId("inDrsPeriod").setDateValue(vDate);
			this.byId("inDrsPeriod").setSecondDateValue(vDate);
			this.byId("inDrsPeriod").setMaxDate(new Date());
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GSTR3B
		 */
		onExit: function () {
			sap.ui.core.BusyIndicator.hide();
		},

		_onRouteMatched: function (oEvent) {
			this._bindDefaultData();
		},

		_bindDefaultData: function () {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/entities.do",
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oEntityModel = new JSONModel(data);
					that.getView().setModel(oEntityModel, "EntityModel");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		/*===============================================================*/
		/*========== On Select Icon Tab Bar =============================*/
		/*===============================================================*/
		handleIconTabBarSelect: function (oEvent) {
			if (oEvent.getSource().getSelectedKey() === "OutwardSupply") {
				var oModel = this.getView().getModel("OutSuppSummary");
				if (!oModel) {
					OutwardSupply._outwardSummary(this);
				}
			}
		},

		/*===============================================================*/
		/*========== On Select/Change Entity ============================*/
		/*===============================================================*/
		onChangeEntity: function (oEvent) {
			var that = this;
			var vId = oEvent.getSource().getId();
			if (vId.indexOf("slSummEntity") > -1) {
				that.vId = "slSummGstin";
			} else if (vId.indexOf("slOutEntity") > -1) {
				that.vId = "slOutGstin";
			} else if (vId.indexOf("slInEntity") > -1) {
				that.vId = "slInGstin";
			}
			var aEntity = oEvent.getSource().getSelectedKeys();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
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
					that.byId(that.vId).setModel(oSupplierGSTIN, "SuppGstinModel");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		/*===============================================================*/
		/*========== On Press Clear Button ==============================*/
		/*===============================================================*/
		onClearFilter: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("fbSummary") > -1) {
				var vEntity = "slSummEntity",
					vGstin = "slSummGstin",
					vDateRange = "drsTaxPeriod";
			} else if (oSource.getId().indexOf("fbOutSupply") > -1) {
				vEntity = "slOutEntity";
				vGstin = "slOutGstin";
				vDateRange = "outDrsPeriod";
			} else if (oSource.getId().indexOf("fbInSupply") > -1) {
				vEntity = "slInEntity";
				vGstin = "slInGstin";
				vDateRange = "inDrsPeriod";
			}
			var vDate = new Date();
			vDate.setMonth(vDate.getMonth() - 1);

			this.byId(vEntity).setSelectedKeys([]);
			this.byId(vGstin).setSelectedKeys([]);
			this.byId(vDateRange).setDateValue(vDate);
			this.byId(vDateRange).setSecondDateValue(vDate);

			var oModel = this.byId(vGstin).getModel("SuppGstinModel");
			oModel.setData([]);
			oModel.refresh(true);
		},

		/*===============================================================*/
		/*========== On Press Go Button =================================*/
		/*===============================================================*/
		onSearch: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("fbOutSupply") > -1) {
				OutwardSupply._outwardSummary(this);
			}
		},

		/*===============================================================*/
		/*========== On Select Filter change ============================*/
		/*===============================================================*/
		onFilterChange: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("") > -1) {
				OutwardSupply._filterChange(oEvent, this);
			}
		}
	});
});