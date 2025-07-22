sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel"
], function (BaseController, Storage, JSONModel) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.EWB", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.EWB
		 */
		onInit: function () {
			var that = this;
			that.TableBinding();
			// var vDate = new Date();
			// vDate.setDate(vDate.getDate() - 9);

			// this.byId("iIWFromDate").setDateValue(vDate);
			// this.byId("iIWFromDate").setMaxDate(new Date());

			// this.byId("iIWToDate").setDateValue(new Date());
			// this.byId("iIWToDate").setMinDate(vDate);
			// this.byId("iIWToDate").setMaxDate(new Date());

			// this.byId("iIWFromDate").addEventDelegate({
			// 	onAfterRendering: function () {
			// 		that.byId("iIWFromDate").$().find("input").attr("readonly", true);
			// 	}
			// });
			// this.byId("iIWToDate").addEventDelegate({
			// 	onAfterRendering: function () {
			// 		that.byId("iIWToDate").$().find("input").attr("readonly", true);
			// 	}
			// });

			this.getOwnerComponent().getRouter().getRoute("EWB").attachPatternMatched(this._onRouteMatched, this);
		},
		_onRouteMatched: function (oEvent) {
			debugger
			var oName = oEvent.getParameter("name");
			// var oArg = oEvent.getParameter("arguments").contextPath;

			if (oName == "EWB") {
				this._getConsolidatedEwbDetails();
			}

		},

		onPressGo: function () {
			this._getConsolidatedEwbDetails();
		},

		_getConsolidatedEwbDetails: function () {

			debugger;
			var aGstin = this.getView().byId("slGstin").getSelectedKeys();
			var vIWFromDate = this.getView().byId("iIWFromDate").getValue();
			var vIWToDate = this.getView().byId("iIWToDate").getValue();

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"CEWBFrom": vIWFromDate == "" ? null : vIWFromDate,
					"CEWBTo": vIWToDate == "" ? null : vIWToDate,
					"CEWBNo": this.getView().byId("idCEWBNo").getValue(),
					"PreviousCEWBNo": this.getView().byId("idPCEWBNo").getValue(),
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};

			var that = this;
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getConsolidatedEwbDetails.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].sno = i + 1;
				}

				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(data), "CEWB");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onPressEWB: function (oEvt, sno) {
			debugger;
			var ewbs = [];
			var TabData = this.getView().getModel("CEWB").getData();
			// var vGSTIN = oEvt.getSource().getEventingParent().getParent().getBindingContext("CEWB").getObject().GSTIN;
			for (var i = 0; i < TabData.resp.length; i++) {
				if (sno === TabData.resp[i].sno) {
					for (var j = 0; j < TabData.resp[i].EWB.length; j++) {
						ewbs.push({
							"ewb": TabData.resp[i].EWB[j]
						});
					}
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(ewbs);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.EWB.Popover", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "ewb");
			this._oGstinPopover.openBy(oButton);
		},

		onChangeDateValue: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("iIWFromDate")) {
				var vDatePicker = "iIWToDate";
			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				fromDate = oEvent.getSource().getDateValue();
			}
			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		onSegEwb: function (oEvent) {

			var vKey1 = oEvent.getSource().getSelectedKey();
			if (vKey1 === "selfoutward" || vKey1 === "selfInward" || vKey1 === "cewb") {
				var setkey = "docH";
				this.byId("idsegsetEwb").setSelectedKey(setkey);
				if (vKey1 === "cewb") {
					this.byId("idCEWB").setVisible(true);
				} else {
					this.byId("idCEWB").setVisible(false);
					var oFilterData = {
						"req": {
							"navType": "EWB"
						}
					};
					if (vKey1 === "selfoutward") {
						oFilterData.req.dataType = "Outward";
					} else {
						oFilterData.req.dataType = "inward";
					}
					this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
					this.getRouter().navTo("InvManageNew");
				}

			} else if (vKey1 === "cOutward" || vKey1 === "cInward") {
				setkey = "docL";
				this.byId("idsegsetEwb").setSelectedKey(setkey);
				this.byId("idCEWB").setVisible(false);

			} else {
				setkey = "export";
				this.byId("idsegsetEwb").setSelectedKey(setkey);
				this.byId("idCEWB").setVisible(false);
			}

		},

		onPressDownloadPdf: function () {
			var vUrl = "/aspsapapi/cewbPdfReports.do";
			var oPayload = {
				"req": []
			};
			var vIndices = this.byId("idCEWBtab").getSelectedIndices(),
				aData = this.byId("idCEWBtab").getModel("CEWB").getData().resp;
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select at least one generated record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < vIndices.length; i++) {
				if (aData[vIndices[i]].CEWBNo != undefined) {
					oPayload.req.push({
						"cEWBNo": aData[vIndices[i]].CEWBNo
					});
				}

			}

			if (oPayload.req.length === 0) {
				sap.m.MessageBox.information("Please select the record where Consolidated E-way bills are generated", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				this.excelDownload(oPayload, vUrl);
			}

		},

		onPressDownloadReport: function () {

			debugger;
			var aGstin = this.getView().byId("slGstin").getSelectedKeys();
			var vIWFromDate = this.getView().byId("iIWFromDate").getValue();
			var vIWToDate = this.getView().byId("iIWToDate").getValue();

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"CEWBFrom": vIWFromDate == "" ? null : vIWFromDate,
					"CEWBTo": vIWToDate == "" ? null : vIWToDate,
					"CEWBNo": this.getView().byId("idCEWBNo").getValue() == "" ? null : this.getView().byId("idCEWBNo").getValue(),
					"PreviousCEWBNo": this.getView().byId("idPCEWBNo").getValue() == "" ? null : this.getView().byId("idPCEWBNo").getValue(),
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					},
					"items": []
				}
			};

			var vIndices = this.byId("idCEWBtab").getSelectedIndices(),
				aData = this.byId("idCEWBtab").getModel("CEWB").getData().resp;
			if (vIndices.length !== 0) {
				for (var i = 0; i < vIndices.length; i++) {
					searchInfo.req.items.push({
						"fileId": aData[vIndices[i]].fileId,
						"serialNo": aData[vIndices[i]].serialNo
					});
				}
			}

			var vUrl = "/aspsapapi/cewbDownloadReports.do";
			this.excelDownload(searchInfo, vUrl);

		},
			TableBinding: function () {
			var that = this;
			var EWBTableModel = new JSONModel();
			var EWBTableView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var getEWBRequestStatusSummary = "/aspsapapi/getEWBRequestStatusSummaryFilter.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: getEWBRequestStatusSummary,
					contentType: "application/json",
					data: JSON.stringify()
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					EWBTableModel.setData(data);
					EWBTableView.setModel(EWBTableModel, "GSTR3B");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.EWB
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.EWB
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.EWB
		 */
		//	onExit: function() {
		//
		//	}

	});

});