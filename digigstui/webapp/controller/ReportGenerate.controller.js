sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel"
], function (BaseController, Storage, JSONModel) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ReportGenerate", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ReportGenerate
		 */
		onInit: function () {
			debugger; //eslint-disable-line
			var oStorage = new Storage(Storage.Type.session, "digiGst");
			var oEntityData = oStorage.get("entity");

			// var vgetTitle=this.getView().byId("tabReport").getSelectedItem().getTitle();
			if (!oEntityData) {
				this.getRouter().navTo("Home");
			} else {
				var oModel = this.getOwnerComponent().getModel("EntityModel");
				oModel.setData(oEntityData);
				oModel.refresh(true);
				this._getDataSecurity(oEntityData[0].entityId, "outwardAnx1");
			}
			this.bindOutwardDefault();
			var odataListl = this.getView().byId("tabReport").getSelectedContextPaths();

		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.ReportGenerate
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.ReportGenerate
		 */
		onAfterRendering: function () {
			debugger; //eslint-disable-line
			this.getMasterReports();

		},

		getMasterReports: function () {
			var that = this;
			var oView = this.getView();
			var oReqWisePath = "/aspsapapi/getMasterReports.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					// data: JSON.stringify(oReqWiseInfo)
				}).done(function (data, status, jqXHR) {
					debugger; //eslint-disable-line
					var oModelReports = new JSONModel(data);
					oView.setModel(oModelReports, "ReportMaster");

				}).fail(function (jqXHR, status, err) {});
			});

		},

		onUpdateFinished: function (oEvent) {
			debugger; //eslint-disable-line
			var oList = this.getView().byId("tabReport");
			var aItems = this.getView().byId("tabReport").getItems();
			oList.setSelectedItem(aItems[2]);
			this.onSelectTreeItem(oEvent);
		},

		oncheckSelecte: function (oEvent) {
			debugger; //eslint-disable-line
			var oSelect = oEvent.getSource().getSelected();
			if (oEvent.getSource().getId().includes("idDocType")) {
				if (oSelect === false) {
					this.getView().byId("iAnx1OutwardDocType").removeSelections();
				} else {
					this.getView().byId("iAnx1OutwardDocType").selectAll();
				}

			} else if (oEvent.getSource().getId().includes("idSupplyType")) {
				if (oSelect === false) {
					this.getView().byId("iAnx1OutwardSupplType").removeSelections();
				} else {
					this.getView().byId("iAnx1OutwardSupplType").selectAll();
				}
			} else if (oEvent.getSource().getId().includes("idAttributes")) {
				if (oSelect === false) {
					this.getView().byId("iAnx1OutwardAttr").removeSelections();
				} else {
					this.getView().byId("iAnx1OutwardAttr").selectAll();
				}
			}

		},

		bindOutwardDefault: function () {
			debugger; //eslint-disable-line
			var that = this,
				vDate = new Date();

			this.byId("dtFromAnx1outward").setMaxDate(vDate);
			this.byId("dtFromAnx1outward").setDateValue(vDate);

			this.byId("dtToAnx1outward").setMaxDate(vDate);
			this.byId("dtToAnx1outward").setDateValue(vDate);

			this.byId("dtFromAnx1outward").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtFromAnx1outward").$().find("input").attr("readonly", true);
				}
			});
			this.byId("dtToAnx1outward").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtToAnx1outward").$().find("input").attr("readonly", true);
				}
			});

		},

		onSelectTreeItem: function (oEvent) {
			debugger; //eslint-disable-line
			var vDetailPage = oEvent.getSource().getSelectedItem().getDependents()[0].getText();

			var oTitle = oEvent.getSource().getSelectedItem().getTitle();

			var ListData =

				{
					"req": {
						"reportsKey": vDetailPage
					}
				};
			this.byId("outwardAnx1").setTitle(oTitle);
			this._bindDefaulListtData(ListData);

		},

		_bindDefaulListtData: function (ListData) {
			debugger; //eslint-disable-line
			// var entityInfo = {
			// 	"req": {}
			// };
			var oEntitiesList = new JSONModel();
			var oView = this.getView();
			var EntityListPath = "/aspsapapi/getDetailsReports.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: EntityListPath,
					contentType: "application/json",
					data: JSON.stringify(ListData)
				}).done(function (data, status, jqXHR) {
					oEntitiesList.setData(data);
					oView.setModel(oEntitiesList, "EntityListItem");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onEntityChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var vPage = "outwardAnx1";
			var vEntityId = oEvent.getSource().getSelectedKey();
			this._getDataSecurity(vEntityId, vPage);

		}
	});
});