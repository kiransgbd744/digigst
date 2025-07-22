sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, MessageBox, Formatter, JSONModel, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GSTR1", {
		formatter: Formatter,

		onInit: function () {
			var vDate = new Date();
			// this.getOwnerComponent().getRouter().getRoute("GSTR1").attachPatternMatched(this._onRouteMatched, this);
			// this.getOwnerComponent().getRouter().getRoute("Returns").attachPatternMatched(this._onRouteMatched, this);

			// 			Processed Records
			this.byId("dtProcessed").setMaxDate(vDate);
			this.byId("dtProcessed").setDateValue(vDate);

			this.byId("dtSummary").setMaxDate(vDate);
			this.byId("dtSummary").setDateValue(vDate);

			this.byId("dtProcessed").addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
			this.byId("dtSummary").addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
			this.getView().setModel(new JSONModel({
				"check": false
			}), "showing");
			this.getView().setModel(new JSONModel({
				"check": false
			}), "showing2");
			this.getView().setModel(new JSONModel({
				"edit": false
			}), "showing1");

			this._getAllFy();
			this._getAllUoM();
			this._getAllRate();
			this._getStateCode();
			var oModel1 = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel1, "GSTR3B");
		},

		//Changes doen by Vinay Kodam 16-04-2020//
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.byId("dtProcessed").setDateValue(new Date());
			}
			var Key = this.getView().getModel("ReturnKey").getProperty("/key");
			this._onRouteMatched(Key);
		},

		//Changes doen by Vinay Kodam 16-04-2020//
		_onRouteMatched: function (key) {
			if (key === "DashBoard") {
				this.onPressGstr1SummaryDashBoard();
			} else if (key === "gstr1PvsI") {
				this.byId("idIconTabBarGstr1").setSelectedKey("gstr1PvsI");
			} else {
				this.byId("idIconTabBarGstr1").setSelectedKey("PrSummary");
				this.byId("dpProcessRecord").setVisible(true);
				this.byId("dpGstr1Summary").setVisible(false);
				// this._getProcessedData();
				this.onPressClear("P", "C");
			}
		},

		//Changes doen by Vinay Kodam 16-04-2020//
		onPressGstr1SummaryDashBoard: function () {
			this.byId("dpProcessRecord").setVisible(false);
			this.byId("dpGstr1Summary").setVisible(true);

			// this.byId("slSummaryEntity").setSelectedKey(this.byId("slEntity").getSelectedKey());
			this.byId("dtSummary").setDateValue($.sap.Date);
			// this._getDataSecurity(this.byId("slEntity").getSelectedKey(), "dpGstr1Summary");
			this.byId("slSummaryGstin").setSelectedKey($.sap.DashGSTIN);

			var searchInfo = this._summarySearchInfo($.sap.DashGSTIN);
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
			}
			var oVisiSummary = {
				"asp": true,
				"gstn": true,
				"diff": true,
				"enableAsp": false,
				"enableGstn": false,
				"enableDiff": false
			};
			this.getView().setModel(new JSONModel(oVisiSummary), "visiSummAnx1");
			this._getProcessSummary(searchInfo, true);
		},

		/**
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onEntityChange: function (oEvent) {
			if (oEvent.getSource().getId().includes("slEntity")) {
				var vPage = "dpProcessRecord";

			} else if (oEvent.getSource().getId().includes("slSummaryEntity")) {
				vPage = "dpGstr1Summary";
			}
			var vEntityId = oEvent.getSource().getSelectedKey();
			this._getDataSecurity(vEntityId, vPage);
		},

		/**
		 * Called to get Data from Database
		 * Developed by: Bharat Gupta - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSearch: function (oEvent) {
			if (oEvent.getSource().getId().includes("bProcessGo")) {
				this._getProcessedData();
			} else {
				this._getSummaryData();
				this.lastUpdated("CB");
			}
			//var searchInfo = this._summarySearchInfo(obj.gstin);
			//this._getSignFileStatus();
		},

		/**
		 * Called to get Payload for additional filter value
		 * Developed by: Bharat Gupta - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} search Object of SearchInfo
		 * @param {string} vPage  Dynamic page ID
		 * @retrun
		 */
		_getOtherFilters: function (search, vPage) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items;

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg) {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg) {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel) {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("slUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("slUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("slUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("slUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("slUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("slUserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * Called to get Adapt Filter value in structure
		 * Developed by: Bharat Gupta - 11.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param  {string} vPage Dynamic Page
		 * @return {Object} oAdaptFilter
		 */
		_adaptFilterValue: function (vPage) {
			var oAdaptFilter = {};
			this._getOtherFilters(oAdaptFilter, vPage);
			return oAdaptFilter;
		},

		/**
		 * Called for payload to get Summary Data
		 * Developed by: Bharat Gupta - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param  {string} vGstin selected Gstin
		 * @return {Object} oSearchInfo
		 */
		_summarySearchInfo: function (vGstin) {
			var oSearchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"docFromDate": null,
					"docToDate": null,
					"EINVGenerated": [],
					"EWBGenerated": [],
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
			if (vGstin) {
				oSearchInfo.req.dataSecAttrs.GSTIN.push(vGstin);
			} else {
				oSearchInfo.req.dataSecAttrs.GSTIN.push(this.byId("slSummaryGstin").getSelectedKey());
				// var aGstin = this.byId("slSummaryGstin").getSelectedKeys();
				// oSearchInfo.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;
			}
			return oSearchInfo;
		},

		/** 
		 * Called for Processed Records Search Criteria
		 * Developed by: Jakeer Syed - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} searchInfo
		 */
		_getProcessSearchInfo: function () {
			var aGstin = this.byId("slGstin").getSelectedKeys(),
				aTableType = this.byId("idPTableType").getSelectedKeys(),
				aDocTypee = this.byId("idPDocType").getSelectedKeys(),

				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dtProcessed").getValue(),
						"tableType": aTableType.includes("All") ? [] : aTableType,
						"docType": aDocTypee.includes("All") ? [] : aDocTypee,
						"docFromDate": null,
						"docToDate": null,
						"EINVGenerated": [],
						"EWBGenerated": [],
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			this.oSavePayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"returnType": "GSTR1",
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};
			return searchInfo;
		},

		/** 
		 * Called to get Processed Records data
		 * Developed by: Jakeer Syed - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getProcessedData: function () {
			if (!$.sap.entityID || $.sap.entityID === "") {
				this._infoMessage(this.getResourceBundle().getText("msgNoData"));
				return;
			}
			var oModel = this.byId("tabGstr1Process").getModel("ProcessedRecord"),
				searchInfo = this._getProcessSearchInfo();

			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
				var docFromDate = this.getView().byId("idocFromDate").getValue();
				if (docFromDate === "") {
					docFromDate = null;
				}
				var docToDate = this.getView().byId("idocToDate").getValue();
				if (docToDate === "") {
					docToDate = null;
				}

				var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys();
				var EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

				searchInfo.req.docFromDate = docFromDate;
				searchInfo.req.docToDate = docToDate;
				searchInfo.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				searchInfo.req.EWBGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;

			}
			// if (oModel) {
			// 	oModel.setData(null);
			// 	oModel.refresh(true);
			// }
			this._getSaveGstnStatus();
			//this._getSignFileStatus(searchInfo.req);
			sap.ui.core.BusyIndicator.show(0);
			this.byId("dpProcessRecord").setModel(new JSONModel(searchInfo.req), "ProcessPayload");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1ProcessedRecords.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.byId("bRupeesView").setText(this.getResourceBundle().getText("rupeesIn") + " " + this.getResourceBundle().getText("A"));
					this.byId("tabGstr1Process").setModel(new JSONModel(data.resp), "ProcessedRecord");
					this.byId("dpProcessRecord").setModel(new JSONModel(data.resp), "ProcessedModel");
					// this.onPressClear('P');
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Api called to get Review summary data from Database
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getSummaryData: function () {
			var searchInfo = this._summarySearchInfo();
			this._getSignFileStatus(searchInfo.req);
			if (this.byId("dAdapt")) {
				var docFromDate = this.getView().byId("idocFromDate").getValue();
				if (docFromDate === "") {
					docFromDate = null;
				}
				var docToDate = this.getView().byId("idocToDate").getValue();
				if (docToDate === "") {
					docToDate = null;
				}

				var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys();
				var EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

				searchInfo.req.docFromDate = docFromDate;
				searchInfo.req.docToDate = docToDate;
				searchInfo.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				searchInfo.req.EWBGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;

				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
			}
			this._getProcessSummary(searchInfo, true);
		},

		/**
		 * Called to open dialog to get additional filter
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressAdaptFilter: function (oEvent) {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			// var vDate = new Date();
			// vDate.setDate(vDate.getDate() - 9);

			// this.byId("idocFromDate").setDateValue(vDate);
			// this.byId("idocFromDate").setMaxDate(new Date());
			// this.byId("idocToDate").setDateValue(new Date());
			// this.byId("idocToDate").setMinDate(vDate);
			// this.byId("idocToDate").setMaxDate(new Date());

			if (oEvent.getSource().getId().includes("bProcessFilter")) {
				var oModel = this.byId("dpProcessRecord").getModel("DataSecurity"),
					oAdaptModel = this.byId("dpProcessRecord").getModel("AdaptFilter");

				if (oAdaptModel) {
					var aAdaptData = oAdaptModel.getData();
					this.byId("slProfitCtr").setSelectedKeys(aAdaptData.PC);
					this.byId("slPlant").setSelectedKeys(aAdaptData.plant);
					this.byId("slDivision").setSelectedKeys(aAdaptData.division);
					this.byId("slLocation").setSelectedKeys(aAdaptData.location);
					this.byId("slSalesOrg").setSelectedKeys(aAdaptData.salesOrg);
					this.byId("slPurcOrg").setSelectedKeys(aAdaptData.purcOrg);
					this.byId("slDistrChannel").setSelectedKeys(aAdaptData.distrChannel);
					this.byId("slUserAccess1").setSelectedKeys(aAdaptData.userAccess1);
					this.byId("slUserAccess2").setSelectedKeys(aAdaptData.userAccess2);
					this.byId("slUserAccess3").setSelectedKeys(aAdaptData.userAccess3);
					this.byId("slUserAccess4").setSelectedKeys(aAdaptData.userAccess4);
					this.byId("slUserAccess5").setSelectedKeys(aAdaptData.userAccess5);
					this.byId("slUserAccess6").setSelectedKeys(aAdaptData.userAccess6);
				}
				var oFilterEnabled = {
					"enabled": true
				};
				this.getView().setModel(new JSONModel(oFilterEnabled), "FilterEnabled");

			} else if (oEvent.getSource().getId().includes("bSummaryFilter")) {
				oModel = this.byId("dpGstr1Summary").getModel("DataSecurity");

				var oFilterEnabled = {
					"enabled": true
				};
				this.getView().setModel(new JSONModel(oFilterEnabled), "FilterEnabled");
			}
			this.byId("dAdapt").setModel(oModel, "DataSecurity");
			this._oAdpatFilter.open();
		},

		/**
		 * Called when user close Additional filter dialog either clicking on Apply or Cancel
		 * Developed by: Bharat Gupta - 18.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing Parameter
		 */
		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				// var oAdaptFilter = {};
				if (this.byId("dpProcessRecord").getVisible()) {
					// this._getOtherFilters(oAdaptFilter, "dpProcessRecord");
					// this.getView().byId("dpProcessRecord").setModel(new JSONModel(oAdaptFilter), "AdaptFilter");
					this._getProcessedData();

				} else if (this.byId("dpGstr1Summary").getVisible()) {
					this._getSummaryData();
				}
			}
		},

		/**
		 * Developed by: Jakeer Syed - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} view View type
		 */
		onPressSaveStatus: function (oEvent, view) {
			var that = this,
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": "",
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.SaveStatus", this);
				this.byId("dpProcessRecord").addDependent(this._oDialogSaveStats);

				this.byId("dtSaveStats").setMaxDate(new Date());
				this.byId("dtSaveStats").setDateValue(new Date());

				this.byId("dtSaveStats").addEventDelegate({
					onAfterRendering: function () {
						that.byId("dtSaveStats").$().find("input").attr("readonly", true);
					}
				});
			}
			if (view === "P") {
				var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
					vPeriod = this.byId("dtProcessed").getValue(),
					vGstin = obj.gstin;
			} else {
				vGstin = this.byId("slSummaryGstin").getSelectedKey(); //s()[0];
				vPeriod = this.byId("dtSummary").getValue();
			}
			oPayload.req.taxPeriod = vPeriod;
			oPayload.req.dataSecAttrs.GSTIN.push(vGstin);

			this.byId("slSaveStatsGstin").setSelectedKey(vGstin);
			this.byId("dtSaveStats").setValue(vPeriod);
			this._oDialogSaveStats.open();
			this._getSaveStatus(oPayload);
		},

		/**
		 * Method called to get Save Status data on click Go button
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSearchSaveStatus: function () {

			var aGstin = this.byId("slSaveStatsGstin").getSelectedKey(),
				oModel = this.byId("dSaveStatus").getModel("SaveStatus");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			if (aGstin.length === 0) {
				sap.m.MessageToast.show("Please select atleast one Gstin");
				return;
			}
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("dtSaveStats").getValue(),
					"dataSecAttrs": {
						"GSTIN": [aGstin] //.includes("All") ? [] : aGstin
					}
				}
			};
			this._getSaveStatus(oPayload);
		},

		/**
		 * Method called to trigger Ajax call to get Save Status data
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_getSaveStatus: function (oPayload) {

			var that = this,
				oModel = that.byId("dSaveStatus").getModel("SaveStatus");

			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1SummarySaveStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].sno = i + 1;
				}
				var oData = new JSONModel(data.resp);
				oData.setSizeLimit(2000);
				that.byId("dSaveStatus").setModel(oData, "SaveStatus");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called to close Save Status Dialog
		 * Developed by: Jakeer Syed - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onCloseSaveStatus: function () {
			this._oDialogSaveStats.close();
		},

		/**
		 * Called to display difference data for gstin
		 * Developed by: Jakeer Syed - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDifference: function (oEvent) {

			if (!this._oDialogDifference) {
				this._oDialogDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.Difference", this);
				this.getView().addDependent(this._oDialogDifference);
			}
			this._oDialogDifference.open();
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dtProcessed").getValue(),
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			oPayload.req.dataSecAttrs.GSTIN.push(obj.gstin);
			this.byId("dDifferenceGstr1").setModel(new JSONModel(oPayload.req), "Payload");
			this._getDifferenceData();
		},

		/**
		 * Called to get difference data for gstin from DB
		 * Developed by: Bharat Gupta - 09.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getDifferenceData: function () {

			var oPayload = {
				"req": this.byId("dDifferenceGstr1").getModel("Payload").getData()
			};
			this.byId("dDifferenceGstr1").setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1DifferenceSummary.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this.byId("dDifferenceGstr1").setBusy(false);
					if (data.hdr.lastUpdatedDate) {
						this.byId("tDiffUpdate").setText("Last Updated: " + data.hdr.lastUpdatedDate);
					} else {
						this.byId("tDiffUpdate").setText(null);
					}

					if (data.hdr.rateIncludedInHsn) {
						data.resp.forEach(function (e) {
							if (e.section == "HSN_ASP" || e.section == "HSN_UI") {
								e.invoiceValue = "NA";
							}
						});
					}
					if (data.hdr.hsnUserInput) {
						data.resp.splice(12, 1)
					} else {
						data.resp.splice(13, 1)
					}

					if (data.hdr.nilUserInput) {
						data.resp.splice(17, 1)
					} else {
						data.resp.splice(18, 1)
					}
					var oData = data.resp.filter(function (e) {
						return (!["14", "14A", "15(i)", "15(ii)", "15(iii)", "15(iv)", "15A.1.a", "15A.1.b", "15A.2.a", "15A.2.b"].includes(e.section));
					});

					this.byId("dDifferenceGstr1").setModel(new JSONModel(oData), "Difference");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onCloseDifferenceDialog: function () {
			this._oDialogDifference.close();
		},

		/**
		 * Called to get Summary data for selected GSTIN
		 * Developed by: Bharat Gupta - 18.10.2019
		 * Modified by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressGstr1Summary: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpProcessRecord").setVisible(false);
			this.byId("dpGstr1Summary").setVisible(true);

			this.byId("dtSummary").setDateValue(this.byId("dtProcessed").getDateValue());
			this.byId("slSummaryGstin").setSelectedKey(obj.gstin);
			if (obj.authToken == "Inactive") {
				this.authState = "I"
			} else {
				this.authState = "A"
			}
			this.lastUpdated("CB");
			var searchInfo = this._summarySearchInfo(obj.gstin);
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
			}
			var oVisiSummary = {
				"asp": true,
				"gstn": true,
				"diff": true,
				"enableAsp": false,
				"enableGstn": false,
				"enableDiff": false
			};
			this.getView().setModel(new JSONModel(oVisiSummary), "visiSummAnx1");
			this._getProcessSummary(searchInfo, true);
			this._getSignFileStatus(searchInfo.req);
			// var gstin = []
			var oInitdata = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": searchInfo.req.taxPeriod,
					"retType": "GSTR1",
					"gstins": [obj.gstin]
				}
			};
			this._getDisableSaveandSign(oInitdata);
			this.buttonVis();
		},

		buttonVis: function () {
			var req = {
				"req": {
					"taxPeriod": this.byId("dtSummary").getValue(),
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"returnType": "GSTR1"
				}
			};
			var that = this;
			var GSTR3BModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/gstinIsFiled.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					GSTR3BModel.setData(data.resp);
					oTaxReGstnView.setModel(GSTR3BModel, "buttonVis");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		_getDisableSaveandSign: function (oInitdata) {
			//          	var postData = {
			// 	"req": {
			// 		"entityId": $.sap.entityID
			// 	}
			// };
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/saveAndSignData.do",
					contentType: "application/json",
					data: JSON.stringify(oInitdata)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//for (var i = 0; i < data.resp.checkerGstins.length; i++) {
					//data.resp.rettype1 = [];
					//data.resp.gstins1 = [];
					// dta.rettype = [];
					// data.gstins = [];
					// oData.respData.dataSecurity.gstin.unshift({
					// 	value: "All"
					// });
					//data.resp.checkerGstins
					// data.resp.checkerGstins.sort(function (a, b) {
					// 	return a.value.localeCompare(b.value);
					// });

					// data.resp.checkerGstins.unshift({
					// 	gstin: "All"
					// });

					//	}
					that.getOwnerComponent().setModel(new JSONModel(data.resp), "CheckVisble");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		_getAllFy: function () {
			$.get("/aspsapapi/getAllFy.do")
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "AllFy");
				}.bind(this));
		},

		/**
		 * Called to get All Unit of Measurement
		 * Developed by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getAllUoM: function () {

			var that = this;
			// $.post("/aspsapapi/getUom.do")
			$.post("/aspsapapi/getUnitOfManagement.do")
				.done(function (data, status, jqXHR) {
					that.getView().setModel(new JSONModel(data.resp), "UomMaster");
				});
		},

		/**
		 * Called to get All Rate value
		 * Developed by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getAllRate: function () {

			var that = this;
			$.post("/aspsapapi/getRate.do")
				.done(function (data, status, jqXHR) {
					that.getView().setModel(new JSONModel(data.resp), "RateMaster");
				});
		},

		/**
		 * Called to get All State Code
		 * Developed by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getStateCode: function () {

			var that = this;
			$.post("/aspsapapi/getStates.do")
				.done(function (data, status, jqXHR) {
					that.getView().setModel(new JSONModel(data.states), "PosMaster");
				});
		},

		/**
		 * Method called to get Review Summary data
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} searchInfo Search Payload get data from tables
		 * @param {boolean} flag Flag value to get Approval & Submit status
		 */
		_getProcessSummary: function (searchInfo, flag) {
			var oModel = this.byId("dpGstr1Summary").getModel("ProcessSummary"),
				oSummModel = this.byId("id_BlockLayoutgstr1").getModel("gstr1Summary"),
				oSavePayload = {
					"req": {
						"entityId": searchInfo.req.entityId,
						"taxPeriod": searchInfo.req.taxPeriod,
						"returnType": "GSTR1",
						"dataSecAttrs": searchInfo.req.dataSecAttrs
					}
				};
			searchInfo.req.returnType = "GSTR1";
			this.byId("dpGstr1Summary").setModel(new JSONModel(searchInfo.req), "SummaryPayload");
			if (oModel) {
				oSummModel.setData(null);
				oSummModel.refresh(true);
				oModel.setData(null);
				oModel.refresh(true);
			}
			this.byId("dpGstr1Summary").setBusy(true);
			$.when(
					$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr1SummaryScreen.do",
						data: JSON.stringify(searchInfo),
						contentType: "application/json"
					}),
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getSaveGstnStatus.do",
						data: JSON.stringify(oSavePayload),
						contentType: "application/json"
					})
				)
				.done(function (data, saveStats) {

					this.byId("dpGstr1Summary").setBusy(false);
					var saveDate = this.getTimeStamp(saveStats[0].resp.updatedDate),
						oSaveStats = {
							"updatedDate": saveDate,
							"flag": saveDate ? true : false
						};
					if (data[0].resp.rateIncludedInHsn) {
						this.rateIncludedInHsn = true;
					} else {
						this.rateIncludedInHsn = false;
					}
					this._bindSummaryData(data[0]);
					this._bindNilExemptData(data[0].resp, data[0].resp.lastUpdatedNilTime);
					this._bindHsnData(data[0].resp, data[0].resp.lastUpdatedHsnTime);
					data[0].resp.updateFlag = data[0].resp.lastUpdatedDate ? true : false;
					this.byId("dpGstr1Summary").setModel(new JSONModel(data[0].resp), "ProcessSummary");
					this.getView().setModel(new JSONModel(oSaveStats), "SaveGstnStats");
				}.bind(this))
				.fail(function () {
					this.byId("dpGstr1Summary").setBusy(false);
				}.bind(this));
			if (flag) {
				this._approvalStatus();
				this._getSubmitStatus(searchInfo.req);
				//this._getSignFileStatus(searchInfo.req);
			}
		},

		_getSaveGstnStatus: function () {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSaveGstnStatus.do",
					data: JSON.stringify(this.oSavePayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var saveDate = this.getTimeStamp(data.resp.updatedDate),
						oSaveStats = {
							"updatedDate": saveDate,
							"flag": saveDate ? true : false
						};

					this.getView().setModel(new JSONModel(oSaveStats), "SaveGstnStats");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Called to get Status for Appraval Request
		 * Developed by: Bharat Gupta - 13.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_approvalStatus: function () {

			var that = this,
				aGstin = this.byId("slSummaryGstin").getSelectedKey(),
				oPayload = {
					"req": {
						"gstin": aGstin,
						"returnPeriod": this.byId("dtSummary").getValue()
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getApprovalStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				// if (data.resp === undefined) {
				// 	that.byId("txtReqSent").setText("");
				// 	that.byId("txtStatus").setText("");
				// } else {
				// 	switch (data.resp.status) {
				// 	case 0:
				// 		var vStatus = "Approval Pending",
				// 			styleCls = "statsPending";
				// 		break;
				// 	case 1:
				// 		vStatus = "Approved";
				// 		styleCls = "statsApprove";
				// 		break;
				// 	case 2:
				// 		vStatus = "Rejected";
				// 		styleCls = "statsReject";
				// 		break;
				// 	}
				// 	var arrInitiateDt = data.resp.initiatedOn.split("T");
				// 	that.byId("txtStatus").removeStyleClass("statsPending");
				// 	that.byId("txtStatus").removeStyleClass("statsApprove");
				// 	that.byId("txtStatus").removeStyleClass("statsReject");
				// 	that.byId("txtStatus").setText(vStatus);
				// 	if (data.resp.approvedOn) {
				// 		arrInitiateDt = data.resp.approvedOn.split("T");
				// 		that.byId("txtReqSent").setText(": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
				// 	} else {
				// 		arrInitiateDt = data.resp.initiatedOn.split("T");
				// 		that.byId("txtReqSent").setText(": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
				// 	}
				// 	that.byId("txtStatus").addStyleClass(styleCls);
				// }
				if (data.resp) {
					data.resp.timeStamp = !data.resp.approvedOn ? data.resp.initiatedOn : data.resp.approvedOn;
				}
				that.byId("dpGstr1Summary").setModel(new JSONModel(data.resp), "ApprovalStatus");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method called to get Submit Status
		 * Developed by: Bharat Gupta - 12.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_getSubmitStatus: function (payload) {

			var that = this,
				oPayload = {
					"req": $.extend(true, {}, payload)
				};
			oPayload.req.returnType = "GSTR1";
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstnSubmitStatus.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					switch (data.resp.status) {
					case "In Progress":
						data.resp.submit = true;
						data.resp.signFile = false;
						break;
					case "Success":
						data.resp.submit = true;
						data.resp.signFile = true;
						break;
					case "Failed":
						data.resp.submit = false;
						data.resp.signFile = false;
						break;
					default:
						data.resp.submit = false;
						data.resp.signFile = false;
					}
					data.resp.visi = data.resp.status ? true : false;
					that.byId("dpGstr1Summary").setModel(new JSONModel(data.resp), "SubmitStatus");
				});
		},

		/**
		 * Method called to get Sign & File Status
		 * Developed by: Bharat Gupta - 09.08.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_getSignFileStatus: function (payload) {
			payload.returnType = "GSTR1";
			var that = this,
				oPayload = {
					"req": payload
				};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstnSaveFileStatus.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					// switch (data.resp.status) {
					// case "In Progress":
					// case "Success":
					// 	data.resp.submit = true;
					// 	break;
					// default:
					// 	data.resp.submit = false;
					// }
					data.resp.submit = true;
					that.byId("dpGstr1Summary").setModel(new JSONModel(data.resp), "SignFileStatus");
				});
		},

		/**
		 * Method called to bind Review Summary total value
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Response data
		 */
		_bindSummaryData: function (data) {
			var oGstrSummary = {
				"ASP": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				},
				"GSTN": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				},
				"DIFF": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				}
			};
			data.resp.outward.forEach(function (e) {
				oGstrSummary.ASP.totalTax += e.aspTaxPayble;
				oGstrSummary.ASP.igst += e.aspIgst;
				oGstrSummary.ASP.cgst += e.aspCgst;
				oGstrSummary.ASP.sgst += e.aspSgst;
				oGstrSummary.ASP.cess += e.aspCess;
				oGstrSummary.GSTN.totalTax += e.gstnTaxPayble;
				oGstrSummary.GSTN.igst += e.gstnIgst;
				oGstrSummary.GSTN.cgst += e.gstnCgst;
				oGstrSummary.GSTN.sgst += e.gstnSgst;
				oGstrSummary.GSTN.cess += e.gstnCess;
				oGstrSummary.DIFF.totalTax += e.diffTaxPayble;
				oGstrSummary.DIFF.igst += e.diffIgst;
				oGstrSummary.DIFF.cgst += e.diffCgst;
				oGstrSummary.DIFF.sgst += e.diffSgst;
				oGstrSummary.DIFF.cess += e.diffCess;
			});
			data.resp.Advances.forEach(function (e) {
				if (e.taxDocType === "ADV REC" || e.taxDocType === "ADV REC-A") {
					oGstrSummary.ASP.totalTax += e.aspTaxPayble;
					oGstrSummary.ASP.igst += e.aspIgst;
					oGstrSummary.ASP.cgst += e.aspCgst;
					oGstrSummary.ASP.sgst += e.aspSgst;
					oGstrSummary.ASP.cess += e.aspCess;
					oGstrSummary.GSTN.totalTax += e.gstnTaxPayble;
					oGstrSummary.GSTN.igst += e.gstnIgst;
					oGstrSummary.GSTN.cgst += e.gstnCgst;
					oGstrSummary.GSTN.sgst += e.gstnSgst;
					oGstrSummary.GSTN.cess += e.gstnCess;
					oGstrSummary.DIFF.totalTax += e.diffTaxPayble;
					oGstrSummary.DIFF.igst += e.diffIgst;
					oGstrSummary.DIFF.cgst += e.diffCgst;
					oGstrSummary.DIFF.sgst += e.diffSgst;
					oGstrSummary.DIFF.cess += e.diffCess;
				}
				if (e.taxDocType === "ADV ADJ" || e.taxDocType === "ADV ADJ-A") {
					oGstrSummary.ASP.totalTax = +((oGstrSummary.ASP.totalTax - e.aspTaxPayble).toFixed(2));
					oGstrSummary.ASP.igst = +((oGstrSummary.ASP.igst - e.aspIgst).toFixed(2));
					oGstrSummary.ASP.cgst = +((oGstrSummary.ASP.cgst - e.aspCgst).toFixed(2));
					oGstrSummary.ASP.sgst = +((oGstrSummary.ASP.sgst - e.aspSgst).toFixed(2));
					oGstrSummary.ASP.cess = +((oGstrSummary.ASP.cess - e.aspCess).toFixed(2));
					oGstrSummary.GSTN.totalTax = +((oGstrSummary.GSTN.totalTax - e.gstnTaxPayble).toFixed(2));
					oGstrSummary.GSTN.igst = +((oGstrSummary.GSTN.igst - e.gstnIgst).toFixed(2));
					oGstrSummary.GSTN.cgst = +((oGstrSummary.GSTN.cgst - e.gstnCgst).toFixed(2));
					oGstrSummary.GSTN.sgst = +((oGstrSummary.GSTN.sgst - e.gstnSgst).toFixed(2));
					oGstrSummary.GSTN.cess = +((oGstrSummary.GSTN.cess - e.gstnCess).toFixed(2));
					oGstrSummary.DIFF.totalTax = +((oGstrSummary.DIFF.totalTax - e.diffTaxPayble).toFixed(2));
					oGstrSummary.DIFF.igst = +((oGstrSummary.DIFF.igst - e.diffIgst).toFixed(2));
					oGstrSummary.DIFF.cgst = +((oGstrSummary.DIFF.cgst - e.diffCgst).toFixed(2));
					oGstrSummary.DIFF.sgst = +((oGstrSummary.DIFF.sgst - e.diffSgst).toFixed(2));
					oGstrSummary.DIFF.cess = +((oGstrSummary.DIFF.cess - e.diffCess).toFixed(2));
				}
			});
			data.resp.ecomSup.forEach(function (e) {
				if (["14(ii)", "14A(ii)", "15", "15A(I)", "15A(II)"].includes(e.taxDocType)) {
					oGstrSummary.ASP.totalTax += e.aspIgst + e.aspCgst + e.aspSgst + e.aspCess;
					oGstrSummary.ASP.igst += e.aspIgst;
					oGstrSummary.ASP.cgst += e.aspCgst;
					oGstrSummary.ASP.sgst += e.aspSgst;
					oGstrSummary.ASP.cess += e.aspCess;
					oGstrSummary.GSTN.totalTax += (!e.gstnIgst ? 0 : e.gstnIgst) + (!e.gstnCgst ? 0 : e.gstnCgst) +
						(!e.gstnSgst ? 0 : e.gstnSgst) + (!e.gstnCess ? 0 : e.gstnCess);
					oGstrSummary.GSTN.igst += (!e.gstnIgst ? 0 : e.gstnIgst);
					oGstrSummary.GSTN.cgst += (!e.gstnCgst ? 0 : e.gstnCgst);
					oGstrSummary.GSTN.sgst += (!e.gstnSgst ? 0 : e.gstnSgst);
					oGstrSummary.GSTN.cess += (!e.gstnCess ? 0 : e.gstnCess);
					oGstrSummary.DIFF.totalTax += (!e.diffIgst ? 0 : e.diffIgst) + (!e.diffCgst ? 0 : e.diffCgst) +
						(!e.diffSgst ? 0 : e.diffSgst) + (!e.diffCess ? 0 : e.diffCess);
					oGstrSummary.DIFF.igst += (!e.diffIgst ? 0 : e.diffIgst);
					oGstrSummary.DIFF.cgst += (!e.diffCgst ? 0 : e.diffCgst);
					oGstrSummary.DIFF.sgst += (!e.diffSgst ? 0 : e.diffSgst);
					oGstrSummary.DIFF.cess += (!e.diffCess ? 0 : e.diffCess);
				}
			});
			this.byId("id_BlockLayoutgstr1").setModel(new JSONModel(oGstrSummary), "gstr1Summary");
		},

		/**
		 * Developed by: Bharat Gupta - 02.12.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} data NIL, Exempt, Non Response data
		 * @param {Object} date Save to Gstn date
		 */
		_bindNilExemptData: function (data, date) {
			var oBundle = this.getResourceBundle();
			for (var i = 0; i < data.nil.length; i++) {
				data.nil[i].select = false;
				data.nil[i].flag = true;

				if (!data.nilUserInput && data.nil[i].taxDocType === "ASP_NILEXTNON") {
					var obj = $.extend(true, {}, data.nil[i]),
						msg = "considerComputed";

					data.nil[i].select = true;

				} else if (data.nilUserInput && data.nil[i].taxDocType === "UI_NILEXTNON") {
					obj = $.extend(true, {}, data.nil[i]);
					msg = "considerUserEdit";
					data.nil[i].select = true;
				}
			}
			obj.taxDocType = "NILEXTNON";
			obj.flag = false;
			data.nil.unshift(obj);
			data.nilFooterMsg = date ? oBundle.getText(msg) + " (" + this.getTimeStamp(date) + ")" : null;
		},

		/**
		 * Developed by: Bharat Gupta - 02.12.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} data HSN Response data
		 * @param {Object} date Save to Gstn Status object
		 */
		_bindHsnData: function (data, date) {
			var oBundle = this.getResourceBundle();
			for (var i = 0; i < data.hsnSummary.length; i++) {
				data.hsnSummary[i].select = false;
				data.hsnSummary[i].flag = "R";

				if (!data.hsnUserInput && data.hsnSummary[i].taxDocType === "HSN_ASP") {
					var obj = $.extend(true, {}, data.hsnSummary[i]),
						msg = "considerComputed";
					data.hsnSummary[i].select = true;
				} else if (data.hsnUserInput && data.hsnSummary[i].taxDocType === "HSN_UI") {
					obj = $.extend(true, {}, data.hsnSummary[i]);
					msg = "considerUserEdit";
					data.hsnSummary[i].select = true;
				}
				for (var j = 0; j < data.hsnSummary[i].items.length; j++) {
					data.hsnSummary[i].items[j].select = false;
					data.hsnSummary[i].items[j].flag = "T";
					data.hsnSummary[i].items[j].taxDocType = data.hsnSummary[i].items[j].taxDocType + "  "
				}
			}
			obj.taxDocType = "HSN";
			obj.flag = "H";
			delete obj.items

			data.hsnSummary.unshift(obj);
			data.hsnFooterMsg = date ? oBundle.getText(msg) + " (" + this.getTimeStamp(date) + ")" : null;
		},

		/**
		 * Method called when user change Radio button
		 * Developed by: Bharat Gupta - 02.12.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oEvent Eventing object
		 * @param {string} btn Radio Button group
		 */
		onSelectRadioBtn: function (oEvent, btn) {
			if (oEvent.getParameter("selected")) {
				var oBundle = this.getResourceBundle(),
					obj = oEvent.getSource().getBindingContext("ProcessSummary").getObject(),
					that = this;

				if (btn === "nil") {
					var vText = oBundle.getText("nilTab8"),
						vRadio = oBundle.getText(obj.taxDocType === "UI_NILEXTNON" ? "userEdit" : "digiComputed");
				} else {
					vText = oBundle.getText("hsnTab12");
					vRadio = oBundle.getText(obj.taxDocType === "HSN_UI" ? "userEdit" : "digiComputed");
				}
				sap.m.MessageBox.confirm(oBundle.getText("msgChangeRadio", [vText, vRadio]), {
					actions: [
						sap.m.MessageBox.Action.YES,
						sap.m.MessageBox.Action.NO
					],
					initialFocus: null,
					styleClass: "sapUiSizeCompact",
					onClose: function (action) {
						var oSummModel = that.byId("dpGstr1Summary").getModel("ProcessSummary"),
							oSummData = oSummModel.getData();

						if (action === sap.m.MessageBox.Action.YES) {
							var oRep = $.extend(true, {}, obj);
							oRep.select = oRep.flag = false;

							if (btn === "nil") {
								oRep.taxDocType = "NIL";
								oSummData.nil.splice(0, 1, oRep);
								oSummData.nilUserInput = (obj.taxDocType === "UI_NILEXTNON");
							} else {
								debugger
								oRep.taxDocType = "HSN";
								oRep.select = false;
								oRep.flag = "H";
								delete oRep.items;
								oSummData.hsnSummary.splice(0, 1, oRep);
								oSummData.hsnUserInput = (obj.taxDocType === "HSN_UI");
							}
						} else {
							that._revertRadioSelection(btn === "nil" ? oSummData.nil : oSummData.hsnSummary);
						}
						oSummModel.refresh(true);
					}
				});
			}
		},

		/**
		 * Method called to revert Radio button selection
		 * Developed by: Bharat Gupta - 07.12.2020
		 * @param {Object} data Review Summary object for Nil/HSN
		 */
		_revertRadioSelection: function (data) {
			for (var i = 0; i < data.length; i++) {
				if (data[i].taxDocType === "NIL" || data[i].taxDocType === "HSN") {
					continue;
				}
				data[i].select = !data[i].select;
			}
		},

		/**
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectCheckBox: function (oEvent) {
			var oVisiModel = this.getView().getModel("visiSummAnx1"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.asp && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.enableAsp = true;

			} else if (!oVisiData.asp && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.enableGstn = true;

			} else if (!oVisiData.asp && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.enableDiff = true;

			} else {
				oVisiData.enableAsp = false;
				oVisiData.enableDiff = false;
				oVisiData.enableGstn = false;
			}
			oVisiModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta - 08.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 * @param {boolean} flag   Flag value to set fields true / false
		 * @return {Object} Object Struture
		 */
		_getGstr1DetailCheckStats: function (flag) {
			return {
				"b2b": flag,
				"b2ba": flag,
				"b2cl": flag,
				"b2cla": flag,
				"b2cs": flag,
				"b2csa": flag,
				"cdnr": flag,
				"cdnra": flag,
				"cdnur": flag,
				"cdnura": flag,
				"exp": flag,
				"expa": flag,
				"nilNon": flag,
				"hsn": flag,
				"docSeries": flag,
				"atTxpd": flag,
				"ataTxpda": flag,
				"select": flag,
				"partial": false
			};
		},

		/**
		 * Method called to open dialog for Getting GSTR-1 details
		 * Developed by: Bharat Gupta - 08.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 * @param {string} view View type ('P','S')
		 */
		onPressGstr1GetDetails: function (view) {
			var that = this,
				oBundle = this.getResourceBundle();
			if (view === "P") {
				if (!this._getGstr1Detail) {
					this._getGstr1Detail = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.GetGstr1Details", this);
					this.getView().addDependent(this._getGstr1Detail);
				}
				this.byId("dGetDetails").setModel(new JSONModel(this._getGstr1DetailCheckStats(true)), "CheckStats");
				this._getGstr1Detail.open();
			} else {
				sap.m.MessageBox.confirm(oBundle.getText("confirmGstr1Get"), {
					styleClass: "sapUiSizeCompact",
					onClose: function (oAction) {
						if (oAction === sap.m.MessageBox.Action.OK) {
							sap.ui.core.BusyIndicator.show(0);
							var oPayload = {
								"req": [{
									"gstin": that.byId("slSummaryGstin").getSelectedKey(),
									"ret_period": that.byId("dtSummary").getValue()
								}]
							};
							$.ajax({
								method: "POST",
								url: "/aspsapapi/Gstr1GstnGetSection.do",
								contentType: "application/json",
								data: JSON.stringify(oPayload)
							}).done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									sap.m.MessageBox.success(oBundle.getText("msgGetGstnData"), {
										styleClass: "sapUiSizeCompact"
									});
								} else {
									sap.m.MessageBox.error(data.hdr.message, {
										styleClass: "sapUiSizeCompact"
									});
								}
							}).fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							});
						}
					}
				});
			}
		},

		/**
		 * Method Called when user click on OK / Close button
		 * Developed by: Bharat Gupta - 08.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 * @param {string} action Button action
		 */
		onCloseGetDetails: function (action) {
			this._getGstr1Detail.close();
		},

		/**
		 * Developed by: Bharat Gupta - 08.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 */
		onSelectTriState: function () {
			var oModel = this.byId("dGetDetails").getModel("CheckStats"),
				oData = oModel.getData();
			oModel.setData(this._getGstr1DetailCheckStats(oData.select));
			oModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta - 08.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 */
		onSelectDetailCheckBox: function () {
			var oModel = this.byId("dGetDetails").getModel("CheckStats"),
				oData = oModel.getData(),
				selectFlag = false,
				partialFlag = true;

			for (var field in oData) {
				if (field === "select" || field === "partial") {
					continue;
				}
				selectFlag = selectFlag || oData[field];
				partialFlag = partialFlag && oData[field];
			}
			oData.select = selectFlag;
			oData.partial = !partialFlag;
			oModel.refresh(true);
		},

		/**
		 * Called to navigate back to Process screen
		 * Developed by: Bharat Gupta - 18.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @priavte
		 */
		onPressBack: function () {
			this.byId("dpProcessRecord").setVisible(true);
			this.byId("dpGstr1Summary").setVisible(false);
		},

		/**
		 * Called to clear filter value and table data to default
		 * Developed by: Bharat Gupta - 17.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressClear: function (flag, clearFlag) {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("dAdapt")) {
				this.getView().byId("idocFromDate").setValue("");
				this.getView().byId("idocToDate").setValue("");
				this.getView().byId("iEINVGenerated").setSelectedKeys([]);
				this.getView().byId("iEWBGenerated").setSelectedKeys([]);

				this.getView().byId("idDateRange").setValue("");
				this.getView().byId("slProfitCtr").setSelectedKeys([]);
				this.getView().byId("slPlant").setSelectedKeys([]);
				this.getView().byId("slDivision").setSelectedKeys([]);
				this.getView().byId("slLocation").setSelectedKeys([]);
				this.getView().byId("slSalesOrg").setSelectedKeys([]);
				this.getView().byId("slPurcOrg").setSelectedKeys([]);
				this.getView().byId("slDistrChannel").setSelectedKeys([]);
				this.getView().byId("slUserAccess1").setSelectedKeys([]);
				this.getView().byId("slUserAccess2").setSelectedKeys([]);
				this.getView().byId("slUserAccess3").setSelectedKeys([]);
				this.getView().byId("slUserAccess4").setSelectedKeys([]);
				this.getView().byId("slUserAccess5").setSelectedKeys([]);
				this.getView().byId("slUserAccess6").setSelectedKeys([]);
			}
			if (clearFlag == "C") {
				if (flag == "P") {
					var vPage = "dpProcessRecord",
						vGstin = "slGstin",
						vDatePicker = "dtProcessed";
					this.byId(vPage).setModel(null, "AdaptFilter");
					this.byId(vGstin).setSelectedKeys([]);
					this.byId("idPTableType").setSelectedKeys([]);
					this.byId("idPDocType").setSelectedKeys([]);

				} else if (flag == "S") {
					vPage = "dpGstr1Summary";
					// vGstin = "slSummaryGstin";
					vDatePicker = "dtSummary";
				}
				this.byId(vDatePicker).setDateValue(vDate);

				if (flag == "P") {
					this._getProcessedData();

				} else if (flag == "S") {
					this._getSummaryData();
				}
			}
		},

		/**
		 * Method called to get Payload for Document Summary Popup
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} Document Summary
		 */
		_popupProperty: function () {
			return {
				"title": "",
				"segment": "summary",
				"btnEnable": true,
				"maxDate": null,
				"orgGrossAdv": null,
				"newGrossAdv": null
			};
		},

		/**
		 * Method called to open Dialog box to display Document Type wise data
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {String} taxDocType Tax Document Type
		 */
		onPressOutwardLink: function (taxDocType) {
			var oSummPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData();
			oSummPayload.docType = taxDocType;

			if (taxDocType === "B2CS") {
				this._getB2csSummary(oSummPayload);

			} else if (taxDocType === "B2CSA") {
				this._getB2csaSummary(oSummPayload);

			} else {
				var oPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData(),
					oFilterData = {
						"req": {
							"dataType": "outward",
							"criteria": "taxPeriod",
							"taxPeriodFrom": null,
							"taxPeriodTo": null,
							"navType": "GSTR1",
							"dataOriginType": null,
							"refId": null,
							"showGstnData": false,
							"type": "P",
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};
				oFilterData.req.taxPeriodFrom = oPayload.taxPeriod;
				oFilterData.req.taxPeriodTo = oPayload.taxPeriod;
				oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			}
		},

		onPressStatusForNavToInv: function (refId) {
			var oPayload = this.byId("dtSaveStats").getValue(),
				oFilterData = {
					"req": {
						"dataType": "outward",
						"criteria": "taxPeriod",
						"taxPeriodFrom": this.byId("dtSaveStats").getValue(),
						"taxPeriodTo": this.byId("dtSaveStats").getValue(),
						"navType": "GSTR1",
						"dataOriginType": null,
						"type": null,
						"refId": refId,
						"showGstnData": true,
						"gstReturnsStatus": ["GstnError"],
						"dataSecAttrs": {
							"GSTIN": [this.byId("slSaveStatsGstin").getSelectedKey()]
						}
					}
				};
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvManageNew");
		},

		/**
		 * Method called to Download Excel file
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} section Section
		 */
		onDownloadOutward: function (section) {
			var oSummPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData(),
				aOutward = ["B2B", "B2BA", "B2CL", "B2CLA", "EXPORTS", "EXPORTS-A", "CDNR", "CDNRA", "CDNUR", "CDNURA", "B2CS", "B2CSA"],
				oPayload = {
					"req": {
						"entityId": oSummPayload.entityId,
						"dataType": "outward",
						"taxDocType": section,
						"taxPeriod": oSummPayload.taxPeriod,
						"dataSecAttrs": oSummPayload.dataSecAttrs
					}
				};
			if (aOutward.includes(section)) {
				this.reportDownload(oPayload, "/aspsapapi/downloadGstr1SectionWiseReports.do");
			} else {
				this.excelDownload(oPayload, "/aspsapapi/downloadGstr1SecReports.do");
			}
		},

		/**
		 * Method called to get and display B2CS data in Dialog
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_getB2csSummary: function (payload) {
			var oProperty = this._popupProperty(),
				oPayload = {
					"req": payload
				};

			if (!this._oB2csSummary) {
				this._oB2csSummary = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.B2csSummary", this);
				this.getView().addDependent(this._oB2csSummary);
			}
			this._oB2csSummary.setModel(new JSONModel(oProperty), "Property");
			this._oB2csSummary.setModel(new JSONModel(oPayload.req), "Payload");
			this.byId("tabB2csDetails").setFirstVisibleRow(0);
			this._oB2csSummary.open();
			this._getAspVertical(this._oB2csSummary, true);
		},

		/**
		 * Method called to get and display B2CSA data in Dialog
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_getB2csaSummary: function (payload) {
			var oProperty = this._popupProperty(),
				oPayload = {
					"req": payload
				};
			if (!this._oB2csaSummary) {
				this._oB2csaSummary = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.B2csaSummary", this);
				this.getView().addDependent(this._oB2csaSummary);
			}
			oProperty.maxDate = new Date(payload.taxPeriod.substr(2), payload.taxPeriod.substr(0, 2), 0);
			this._oB2csaSummary.setModel(new JSONModel(oProperty), "Property");
			this._oB2csaSummary.setModel(new JSONModel(oPayload.req), "Payload");
			this.byId("tabB2csaDetails").setFirstVisibleRow(0);
			this._oB2csaSummary.open();
			this._getAspVertical(this._oB2csaSummary, true);
		},

		/**
		 * Method called to get ASP Vertical data from DB
		 * Developed by: Bharat Gupta - 23.05.2020
		 * Modified by: Bharat Gupta - 04.08.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oAspVertical Reference object
		 * @param {boolean} flag Flag value to get (Summary & Gstn View) or All data
		 */
		_getAspVertical: function (oAspVertical, flag) {
			var oPayload = {
				"req": oAspVertical.getModel("Payload").getData()
			};
			oAspVertical.setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1AspVertical.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				oAspVertical.setBusy(false);
				if (data.hdr.status === "S" && flag) {
					for (var i = 0; i < data.resp.verticalData.length; i++) {
						data.resp.verticalData[i].sNo = i + 1;
						data.resp.verticalData[i].visi = false;
					}
					var oData = $.extend(true, [], data.resp);
					oAspVertical.setModel(new JSONModel(data.resp), "AspVertical");
					oAspVertical.setModel(new JSONModel(oData), "AspData");
				} else if (data.hdr.status === "S" && !flag) {
					var oAspModel = oAspVertical.getModel("AspVertical"),
						oAspData = oAspModel.getData();
					oAspData.summary = data.resp.summary;
					oAspData.gstnView = data.resp.gstnView;
					oAspModel.refresh(true);
				}
			}).fail(function (jqXHR, status, err) {
				oAspVertical.setBusy(false);
			});
		},

		/**
		 * Method called to navigate to screen based on selected view type
		 * Developed by: Bharat Gupta - 03.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Selected Link value
		 */
		onAspVerticalLink: function (view) {
			if (view === "TRANSACTIONAL") {
				var oPayload = this._oB2csSummary.getModel("Payload").getData(),
					oFilterData = {
						"req": {
							"dataType": "outward",
							"criteria": "taxPeriod",
							"taxPeriodFrom": null,
							"taxPeriodTo": null,
							"navType": "GSTR1",
							"dataOriginType": null,
							"refId": null,
							"showGstnData": false,
							"type": "P",
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};
				oFilterData.req.taxPeriodFrom = oPayload.taxPeriod;
				oFilterData.req.taxPeriodTo = oPayload.taxPeriod;
				oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");

			} else if (view === "VERTICAL") {
				var oAspPropModel = this._oB2csSummary.getModel("Property");
				oAspPropModel.getData().segment = "detail";
				oAspPropModel.refresh(true);
			}
		},

		/**
		 * Method called to return ASP Outward Vertical object
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param  {Object} oAspPayload ASP Payload Object
		 * @param  {string} docType Document type
		 * @return {Object} ASP Vertical object
		 */
		_getOutwardVertical: function (oAspPayload, docType) {
			return {
				"sgstn": oAspPayload.dataSecAttrs.GSTIN[0],
				"taxPeriod": oAspPayload.taxPeriod,
				"transType": "N",
				"month": (docType === "b2csa" ? this.aspPeriod(oAspPayload.taxPeriod) : null),
				"orgPos": null,
				"orgHsnOrSac": null,
				"orgUom": null,
				"orgQunty": (docType === "b2csa" ? 0 : null),
				"orgRate": null,
				"orgTaxableValue": (docType === "b2csa" ? 0 : null),
				"orgEcomGstin": null,
				"orgEcomSupplValue": (docType === "b2csa" ? 0 : null),
				"newPos": null,
				"newHsnOrSac": null,
				"newUom": null,
				"newQunty": 0,
				"newRate": null,
				"newTaxableValue": 0,
				"newEcomGstin": null,
				"newEcomSupplValue": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0,
				"totalValue": 0,
				"profitCntr": null,
				"plant": null,
				"division": null,
				"location": null,
				"salesOrg": null,
				"distrChannel": null,
				"usrAccess1": null,
				"usrAccess2": null,
				"usrAccess3": null,
				"usrAccess4": null,
				"usrAccess5": null,
				"usrAccess6": null,
				"usrDefined1": null,
				"usrDefined2": null,
				"usrDefined3": null,
				"visi": true
			};
		},

		/**
		 * Method called to add new record in Vertical table
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} docType Document type
		 */
		onPressAddAsp: function (docType) {
			if (docType === "b2cs") {
				var oTable = this.byId("tabB2csDetails"),
					oAspPayload = this._oB2csSummary.getModel("Payload").getData();
			} else {
				oTable = this.byId("tabB2csaDetails");
				oAspPayload = this._oB2csaSummary.getModel("Payload").getData();
			}
			var obj = this._getOutwardVertical(oAspPayload, docType),
				oModel = oTable.getModel("AspVertical"),
				oData = oModel.getData();

			obj.sNo = oData.verticalData.length + 1;
			oData.verticalData.push(obj);
			oModel.refresh(true);
			oTable.setSelectedIndex(oData.verticalData.length - 1);
			oTable.setFirstVisibleRow(oData.verticalData.length - 9);
		},

		/**
		 * Method called to edit selected records
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Selected Link value
		 */
		onPressEditAsp: function (view) {
			if (view === "b2cs") {
				var vTable = "tabB2csDetails";
			} else {
				vTable = "tabB2csaDetails";
			}
			var oAspModel = this.byId(vTable).getBinding().getModel(),
				aIndex = this.byId(vTable).getSelectedIndices();

			if (aIndex.length === 0) {
				var oBundle = this.getResourceBundle();
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oAspData = oAspModel.getData().verticalData;

			for (var i = 0; i < aIndex.length; i++) {
				oAspData[aIndex[i]].visi = true;
			}
			oAspModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Selected Link value
		 * @param {Object} data Data object
		 * @return {Object} Payload object
		 */
		_getAspVerticalSaveObj: function (view, data) {
			return {
				"sNo": data.sNo,
				"id": data.id || null,
				"sgstn": data.sgstn,
				"section": view,
				"taxPeriod": data.taxPeriod,
				"transType": data.transType,
				"month": data.month || null,
				"orgPos": data.orgPos || null,
				"orgHsnOrSac": data.orgHsnOrSac || null,
				"orgUom": data.orgUom || null,
				"orgQunty": data.orgQunty || 0,
				"orgRate": data.orgRate || 0,
				"orgTaxableValue": data.orgTaxableValue || 0,
				"orgEcomGstin": data.orgEcomGstin || null,
				"orgEcomSupplValue": data.orgEcomSupplValue || 0,
				"newPos": data.newPos || null,
				"newHsnOrSac": data.newHsnOrSac || null,
				"newUom": data.newUom || null,
				"newQunty": data.newQunty || 0,
				"newRate": data.newRate || 0,
				"newTaxableValue": data.newTaxableValue || 0,
				"newEcomGstin": data.newEcomGstin || null,
				"newEcomSupplValue": data.newEcomSupplValue || 0,
				"igst": data.igst || 0,
				"cgst": data.cgst || 0,
				"sgst": data.sgst || 0,
				"cess": data.cess || 0,
				"totalValue": data.totalValue || 0,
				"profitCntr": data.profitCntr || null,
				"plant": data.plant || null,
				"division": data.division || null,
				"location": data.location || null,
				"salesOrg": data.salesOrg || null,
				"distrChannel": data.distrChannel || null,
				"usrAccess1": data.usrAccess1 || null,
				"usrAccess2": data.usrAccess2 || null,
				"usrAccess3": data.usrAccess3 || null,
				"usrAccess4": data.usrAccess4 || null,
				"usrAccess5": data.usrAccess5 || null,
				"usrAccess6": data.usrAccess6 || null,
				"usrDefined1": data.usrDefined1 || null,
				"usrDefined2": data.usrDefined2 || null,
				"usrDefined3": data.usrDefined3 || null
			};
		},

		/**
		 * Method called to validate B2cs/B2csa 
		 * Developed by: Bharat Gupta - 06.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Selected Link value
		 * @param {Object} data Selected data object
		 * @return {boolean} Validation Success/Failure
		 */
		validateOutwardField: function (view, data) {
			var oBundle = this.getResourceBundle(),
				flag = false;
			if (view === "b2cs") {
				var aFields = ["newPos", "newHsnOrSac", "newRate", "newTaxableValue", "totalValue"];
			} else {
				aFields = ["month", "orgPos", "orgRate", "newPos", "newHsnOrSac", "newRate", "newTaxableValue", "totalValue"];
			}
			// for (var i = 0; i < aFields.length; i++) {
			// 	if ((!data[aFields[i]] && !aFields[i].includes("Rate")) || (!data[aFields[i]] && data[aFields[i]] !== 0)) {
			// 		data[aFields[i] + "State"] = "Error";
			// 		data[aFields[i] + "StateText"] = oBundle.getText("msgMandatory");
			// 		flag = true;
			// 	}
			// }
			for (var i = 0; i < aFields.length; i++) {
				var field = aFields[i];
				var fieldValue = data[field];

				if (!fieldValue && (!field.includes("Rate") || fieldValue !== 0)) {
					data[field + "State"] = "Error";
					data[field + "StateText"] = oBundle.getText("msgMandatory");
					flag = true;
				}
			}
			return flag;
		},

		/**
		 * Method called to edit selected records
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Selected Link value
		 */
		onPressSaveAsp: function (view) {
			if (view === "b2cs") {
				var oDialog = this._oB2csSummary,
					vTable = "tabB2csDetails";
			} else {
				oDialog = this._oB2csaSummary;
				vTable = "tabB2csaDetails";
			}
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId(vTable).getSelectedIndices(),
				oModel = oDialog.getModel("AspVertical"),
				oData = oModel.getData().verticalData,
				flag, oPayload = {
					req: []
				};

			if (!aIndex.length) {
				sap.m.MessageBox.information(oBundle.getText("msgSaveSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			aIndex.forEach(function (idx) {
				this._setValueStateNone(oData[idx]);
				flag = this.validateOutwardField(view, oData[idx]);
				if (!flag && oData[idx].visi) {
					oPayload.req.push(this._getAspVerticalSaveObj(view, oData[idx]));
				}
			}.bind(this));
			if (flag) {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oModel.refresh(true);

			} else if (oPayload.req.length > 0) {
				oDialog.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr1VerticalUpdate.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						this._bindAspResponse(oDialog, data);
						this._getAspVertical(oDialog, false);
						oDialog.setBusy(false);
						var oSummPayload = {
							"req": this.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						this._getProcessSummary(oSummPayload, false);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						oDialog.setBusy(false);
					});
			} else {
				sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to get ASP Field mapping object
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} Field mapping object
		 */
		_aspFieldMapping: function () {
			return {
				"TransactionType": "transType",
				"MONTH": "month",
				"OrgPOS": "orgPos",
				"OrgHsnOrSac": "orgHsnOrSac",
				"unitOfMeasure": "orgUom",
				"itemQty": "orgQunty",
				"OrgRate": "orgRate",
				"ORGTAXABLE_VALUE": "orgTaxableValue",
				"OrgE_ComGstin": "orgEcomGstin",
				"ORGECOM_SUP_VALUE": "orgEcomSupplValue",
				"newPOS": "newPos",
				"NewHsnOrSac": "newHsnOrSac",
				"newunitOfMeasure": "newUom",
				"newquantity": "newQunty",
				"newRate": "newRate",
				"NEW_TAXABLE_VALUE": "newTaxableValue",
				"NEWE_COMGSTIN": "newEcomGstin",
				"NEWECOM_SUP_VALUE": "newEcomSupplValue",
				"igstAmt": "igst",
				"cgstAmt": "cgst",
				"sgstAmt": "sgst",
				"cessAmt": "cess",
				"TOTAL_VALUE": "totalValue",
				"profitCentre": "profitCntr",
				"plantCode": "plant",
				"division": "division",
				"location": "location",
				"salesOrg": "salesOrg",
				"distChannel": "distrChannel",
				"profitCentre3": "usrAccess1",
				"profitCentre4": "usrAccess2",
				"profitCentre5": "usrAccess3",
				"profitCentre6": "usrAccess4",
				"profitCentre7": "usrAccess5",
				"profitCentre8": "usrAccess6",
				"hsn": "hsn"
			};
		},

		/**
		 * Method called to get ASP Field mapping object
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oDialog Dialog reference
		 * @param {Object} data Response data object
		 */
		_bindAspResponse: function (oDialog, data) {
			var oBundle = this.getResourceBundle(),
				oStateData = this.getView().getModel("PosMaster").getData(),
				oAspModel = oDialog.getModel("AspVertical"),
				oAspData = oAspModel.getData().verticalData,
				oModel = oDialog.getModel("AspData"),
				flag = true,
				flag1 = false;

			if (data.hdr.status === "S") {
				for (var i = 0; i < oAspData.length; i++) {
					var count = 0;
					this._setValueStateNone(oAspData[i]);
					for (var j = 0; j < data.resp.length; j++) {
						if (oAspData[i].sNo === data.resp[j].sNo && !data.resp[j].errorList) {
							for (var f in data.resp[j]) {
								oAspData[i][f] = data.resp[j][f];
							}
							// Added by Bharat Gupta - 21.02.2021 for State Name
							oAspData[i].orgStateName = this._getStateName(oStateData, data.resp[j].orgPos);
							oAspData[i].newStateName = this._getStateName(oStateData, data.resp[j].newPos); // End of Code by Bharat - 21.02.2021
							oAspData[i].visi = false;
							break;
						} else if (oAspData[i].sNo === data.resp[j].sNo) {
							oAspData[i].id = data.resp[j].id;
							count = this._bindValueState(oAspData[i], data.resp[j].errorList, this._aspFieldMapping());
							flag1 = true;
							if (count === 0) {
								for (f in data.resp[j]) {
									oAspData[i][f] = data.resp[j][f];
								}
							}
							break;
						}
					}
					if (count > 0) {
						flag = false;
					}
				}
				if (flag && data.hdr.status === "S") {
					var oData = oModel.getData();
					oData.verticalData = [];

					for (var k = 0, l = oAspData.length; k < l; k++) {
						if (oAspData[k].id) {
							oData.verticalData.push($.extend(true, {}, oAspData[k]));
						}
					}
					oModel.refresh(true);
					if (flag1) {
						sap.m.MessageBox.information(oBundle.getText("infoMsgOnSave"), {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				} else {
					sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
						styleClass: "sapUiSizeCompact"
					});
				}
				oAspModel.refresh(true);
			} else {
				sap.m.MessageBox.error(data.hdr.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to get State Name based on State Id
		 * Developed by: Bharat Gupta - 21.02.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oState State Data
		 * @param {string} pos State Id
		 * @return {string} State Name
		 */
		_getStateName: function (oState, pos) {
			var vStateName = null;
			if (pos) {
				for (var i = 0; i < oState.length; i++) {
					if (oState[i].stateCode === pos) {
						vStateName = oState[i].stateName;
						break;
					}
				}
			}
			return vStateName;
		},

		/**
		 * Method called to delete selected ASP Outward data (B2CS/B2CSA)
		 * Developed by: Sarvmangla on 08.07.2020
		 * Modified by: Bharat Gupta - 09.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view View type
		 */
		onPressDeleteAsp: function (view) {
			if (view === "b2cs") {
				var oAspDialog = this._oB2csSummary,
					vTable = "tabB2csDetails";
			} else {
				oAspDialog = this._oB2csaSummary;
				vTable = "tabB2csaDetails";
			}
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId(vTable).getSelectedIndices(),
				oModel = oAspDialog.getModel("AspVertical"),
				oData = oModel.getData(),
				that = this,
				flag = false,
				aIdx = [],
				oPayload = {
					"req": {
						"docType": oAspDialog.getModel("Payload").getData().docType,
						"id": []
					}
				};
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgDeleteSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oData.verticalData[aIndex[i]].id) {
					aIdx.push(aIndex[i]);
					continue;
				}
				oPayload.req.id.push(
					oData.verticalData[aIndex[i]].id
				);
				flag = true;
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmDelete"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						if (aIdx.length > 0) {
							that.byId(vTable).setSelectedIndex(-1);
							for (var j = aIdx.length; j > 0; j--) {
								oData.verticalData.splice(aIdx[j - 1], 1);
							}
							oModel.refresh(true);
						}
						if (flag) {
							oAspDialog.setBusy(true);
							$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr1RecordDelete.do",
								data: JSON.stringify(oPayload),
								contentType: "application/json"
							}).done(function (data, status, jqXHR) {
								oAspDialog.setBusy(false);
								if (data.hdr.status === "S") {
									var oSummPayload = {
										"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
									};
									that._getProcessSummary(oSummPayload, false);
									that._getAspVertical(oAspDialog, true);
									MessageBox.success(oBundle.getText("deleteSuccess"), {
										styleClass: "sapUiSizeCompact"
									});
								} else {
									MessageBox.error(oBundle.getText("deleteFailed1"), {
										styleClass: "sapUiSizeCompact"
									});
								}
							}).fail(function (jqXHR, status, err) {
								oAspDialog.setBusy(false);
							});
						}
					}
				}
			});
		},

		/**
		 * Called to open dialog for Advances when user press advances link in review summary Advace table
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} taxDocType Tax Document Type
		 */
		onPressAdvanceLink: function (taxDocType) {
			var oSummPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData();
			oSummPayload.docType = taxDocType;

			if (taxDocType === "ADV REC" || taxDocType === "ADV ADJ") {
				this._advancesSummary(oSummPayload);

			} else if (taxDocType === "ADV REC-A" || taxDocType === "ADV ADJ-A") {
				this._advancesAmendSummary(oSummPayload);
			}
		},

		/**
		 * Method called to open Advances Popup
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_advancesSummary: function (payload) {
			var oBundle = this.getResourceBundle(),
				oProperty = this._popupProperty();
			if (!this._oAdvances) {
				this._oAdvances = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.Advances", this);
				this.getView().addDependent(this._oAdvances);
			}

			switch (payload.docType) {
			case "ADV REC":
				oProperty.title = oBundle.getText("gstr1_at");
				oProperty.newGrossAdv = oBundle.getText("newGrossRec");
				break;
			case "ADV ADJ":
				oProperty.title = oBundle.getText("gstr1_txpd");
				oProperty.newGrossAdv = oBundle.getText("newGrossAdj");
				break;
			}
			this._oAdvances.setModel(new JSONModel(oProperty), "Property");
			this._oAdvances.setModel(new JSONModel(payload), "Payload");
			this.byId("tabAdvVertical").setFirstVisibleRow(0);
			this._oAdvances.open();
			this._getAdvancesData(this._oAdvances, true);
		},

		/**
		 * Method called to open Advance Amendments Popup
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Payload object
		 */
		_advancesAmendSummary: function (payload) {
			var oBundle = this.getResourceBundle(),
				oProperty = this._popupProperty();
			if (!this._oAdvAmendment) {
				this._oAdvAmendment = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.AdvancesAmend", this);
				this.getView().addDependent(this._oAdvAmendment);
			}

			switch (payload.docType) {
			case "ADV REC-A":
				oProperty.title = oBundle.getText("gstr1_ata");
				oProperty.orgGrossAdv = oBundle.getText("orgGrossRec");
				oProperty.newGrossAdv = oBundle.getText("newGrossRec");
				break;
			case "ADV ADJ-A":
				oProperty.title = oBundle.getText("gstr1_txpda");
				oProperty.orgGrossAdv = oBundle.getText("orgGrossAdj");
				oProperty.newGrossAdv = oBundle.getText("newGrossAdj");
				break;
			}
			oProperty.maxDate = new Date(payload.taxPeriod.substr(2), payload.taxPeriod.substr(0, 2), 0);
			this._oAdvAmendment.setModel(new JSONModel(oProperty), "Property");
			this._oAdvAmendment.setModel(new JSONModel(payload), "Payload");
			this.byId("tabAmendVertical").setFirstVisibleRow(0);
			this._oAdvAmendment.open();
			this._getAdvancesData(this._oAdvAmendment, true);
		},

		/**
		 * Method called to get Advances data from DB
		 * Developed by: Bharat Gupta - 22.05.2020
		 * Modified by: Bharat Gupta - 04.08.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oAdvance Advance/Amendment Popup object
		 * @param {boolean} flag Flag value to get (Summary & Gstn View) or All data
		 */
		_getAdvancesData: function (oAdvance, flag) {
			var oPayload = oAdvance.getModel("Payload").getData(),
				payload = {
					"req": $.extend(true, {}, oPayload)
				};
			payload.req.docType = this._tableNumber(oPayload.docType);
			oAdvance.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1AdvancedData.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					oAdvance.setBusy(false);
					if (data.hdr.status === "S" && flag) {
						for (var i = 0; i < data.resp.verticalData.length; i++) {
							data.resp.verticalData[i].sNo = i + 1;
							data.resp.verticalData[i].visi = false;
						}
						var oData = $.extend(true, [], data.resp);
						oAdvance.setModel(new JSONModel(data.resp), "Advances");
						oAdvance.setModel(new JSONModel(oData), "AdvData");
					} else if (data.hdr.status === "S" && !flag) {
						var oAdvModel = oAdvance.getModel("Advances"),
							oAdvData = oAdvModel.getData();
						oAdvData.summary = data.resp.summary;
						oAdvData.gstnView = data.resp.gstnView;
						oAdvModel.refresh(true);
					}
				})
				.fail(function (jqXHR, status, err) {
					oAdvance.setBusy(false);
				});
		},

		/**
		 * Method called to get Advances object
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oAdvance Advance object
		 * @param  {string} type Advances type
		 * @return {Object} Advances object
		 */
		_getAdvVerticalData: function (oAdvance, type) {
			var oData = oAdvance.getModel("Payload").getData();
			return {
				"sgstn": oData.dataSecAttrs.GSTIN[0],
				"taxPeriod": oData.taxPeriod,
				"transType": "N",
				"month": (type === "amend" ? this.aspPeriod(oData.taxPeriod) : null),
				"orgPos": null,
				"orgRate": null,
				"orgTaxableValue": (type === "amend" ? 0 : null),
				"newPos": null,
				"newRate": null,
				"newTaxableValue": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0,
				"profitCntr": null,
				"plant": null,
				"division": null,
				"location": null,
				"salesOrg": null,
				"distrChannel": null,
				"usrAccess1": null,
				"usrAccess2": null,
				"usrAccess3": null,
				"usrAccess4": null,
				"usrAccess5": null,
				"usrAccess6": null,
				"usrDefined1": null,
				"usrDefined2": null,
				"usrDefined3": null,
				"visi": true
			};
		},

		/**
		 * Method called to add new record in Advances table
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onAddAdvance: function () {
			var obj = this._getAdvVerticalData(this._oAdvances, "adv"),
				oModel = this.byId("tabAdvVertical").getModel("Advances"),
				oData = oModel.getData().verticalData;

			obj.sNo = (oData.length === 0 ? 0 : oData[oData.length - 1].sNo) + 1;
			oData.push(obj);
			oModel.refresh(true);
			this.byId("tabAdvVertical").setSelectedIndex(oData.length - 1);
			this.byId("tabAdvVertical").setFirstVisibleRow(oData.length - 9);
		},

		/**
		 * Method called to edit Selected Advance Records
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onEditAdvance: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabAdvVertical").getSelectedIndices(),
				oModel = this.byId("tabAdvVertical").getModel("Advances");

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				var oData = oModel.getData();
				for (var i = 0; i < aIndex.length; i++) {
					oData.verticalData[aIndex[i]].visi = true;
				}
				oModel.refresh(true);
			}
		},

		/**
		 * Method called to get Advance Payload object 
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string}  type Advances Type
		 * @param {Object}  oDialog Advance Popup reference
		 * @param {Object}  data Data object
		 * @return {Object} Advance payload object
		 */
		_getAdvanceObj: function (type, oDialog, data) {
			var oPayload = oDialog.getModel("Payload").getData();
			return {
				"id": data.id || null,
				"sNo": data.sNo,
				"gstin": data.sgstn,
				"taxPeriod": data.taxPeriod,
				"section": this._tableNumber(oPayload.docType),
				"transType": data.transType,
				"month": data.month || null,
				"orgPos": data.orgPos || null,
				"orgRate": (type === "amend" ? data.orgRate || 0 : null),
				"orgAdvance": (type === "amend" ? data.orgTaxableValue || 0 : null),
				"newPos": data.newPos || null,
				"newRate": data.newRate || 0,
				"newAdvance": data.newTaxableValue || 0,
				"igst": data.igst || 0,
				"cgst": data.cgst || 0,
				"sgst": data.sgst || 0,
				"cess": data.cess || 0,
				"profitCntr": data.profitCntr || null,
				"plant": data.plant || null,
				"division": data.division || null,
				"location": data.location || null,
				"salesOrg": data.salesOrg || null,
				"distrChannel": data.distrChannel || null,
				"usrAccess1": data.usrAccess1 || null,
				"usrAccess2": data.usrAccess2 || null,
				"usrAccess3": data.usrAccess3 || null,
				"usrAccess4": data.usrAccess4 || null,
				"usrAccess5": data.usrAccess5 || null,
				"usrAccess6": data.usrAccess6 || null,
				"usrDefined1": data.usrDefined1 || null,
				"usrDefined2": data.usrDefined2 || null,
				"usrDefined3": data.usrDefined3 || null
			};
		},

		/**
		 * Method called to validate B2cs/B2csa 
		 * Developed by: Bharat Gupta - 06.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oDialog Advance Popup reference
		 * @param {Object} data Selected data object
		 * @return {boolean} Validation Success/Failure
		 */
		validateAdvance: function (oDialog, data) {
			var oPayload = oDialog.getModel("Payload").getData(),
				oBundle = this.getResourceBundle(),
				flag = false;
			if (oPayload.docType === "ADV REC" || oPayload.docType === "ADV ADJ") {
				var aFields = ["newPos", "newRate", "newTaxableValue"];
			} else {
				aFields = ["month", "orgPos", "orgRate", "orgTaxableValue", "newPos", "newRate", "newTaxableValue"];
			}
			for (var i = 0; i < aFields.length; i++) {
				if ((!data[aFields[i]] && !aFields[i].includes("Rate")) || (!data[aFields[i]] && data[aFields[i]] !== 0)) {
					data[aFields[i] + "State"] = "Error";
					data[aFields[i] + "StateText"] = oBundle.getText("msgMandatory");
					flag = true;
				}
			}
			return flag;
		},

		/**
		 * Method called to trigger Ajax call to Save Advance data
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSaveAdvance: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabAdvVertical").getSelectedIndices(),
				that = this;

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgSaveSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oModel = this.byId("tabAdvVertical").getModel("Advances"),
				oData = oModel.getData().verticalData,
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndex.length; i++) {
				this._setValueStateNone(oData[aIndex[i]]);
				var flag = this.validateAdvance(this._oAdvances, oData[aIndex[i]]);
				if (!flag && oData[aIndex[i]].visi) {
					oPayload.req.push(this._getAdvanceObj("adv", this._oAdvances, oData[aIndex[i]]));
				}
			}
			if (flag) {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oModel.refresh(true);
			} else if (oPayload.req.length > 0) {
				this._oAdvances.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr1AtTxpdVerticalUpdate.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						that._oAdvances.setBusy(false);
						that._bindAdvanceResponse(that._oAdvances, data);
						that._getAdvancesData(that._oAdvances, false);
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						that._getProcessSummary(oSummPayload, false);
					})
					.fail(function (jqXHR, status, err) {
						that._oAdvances.setBusy(false);
					});
			} else {
				sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to add new record in Advance Amendment
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onAddAmendment: function () {
			var obj = this._getAdvVerticalData(this._oAdvAmendment, "amend"),
				oModel = this.byId("tabAmendVertical").getModel("Advances"),
				oData = oModel.getData().verticalData;

			obj.sNo = (oData.length === 0 ? 0 : oData[oData.length - 1].sNo) + 1;
			oData.push(obj);
			oModel.refresh(true);
			this.byId("tabAmendVertical").setSelectedIndex(oData.length - 1);
			this.byId("tabAmendVertical").setFirstVisibleRow(oData.length - 9);
		},

		/**
		 * Method called to edit selected Advance amendment records
		 * Developed by: Bharat Gupta - 23.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onEditAmendment: function () {
			var aIndex = this.byId("tabAmendVertical").getSelectedIndices(),
				oModel = this.byId("tabAmendVertical").getModel("Advances");

			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Select atleast one record to edit", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				var oData = oModel.getData();
				for (var i = 0; i < aIndex.length; i++) {
					oData.verticalData[aIndex[i]].visi = true;
				}
				oModel.refresh(true);
			}
		},

		/**
		 * Method called to trigger Ajax call to Save Advance data
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSaveAmendment: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabAmendVertical").getSelectedIndices(),
				that = this;

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgSaveSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oModel = this.byId("tabAmendVertical").getModel("Advances"),
				oData = oModel.getData().verticalData,
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndex.length; i++) {
				this._setValueStateNone(oData[aIndex[i]]);
				var flag = this.validateAdvance(this._oAdvAmendment, oData[aIndex[i]]);
				if (!flag && oData[aIndex[i]].visi) {
					oPayload.req.push(this._getAdvanceObj("amend", this._oAdvAmendment, oData[aIndex[i]]));
				}
			}
			if (flag) {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oModel.refresh(true);
			} else if (oPayload.req.length > 0) {
				this._oAdvAmendment.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr1AtTxpdVerticalUpdate.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						that._oAdvAmendment.setBusy(false);
						that._bindAdvanceResponse(that._oAdvAmendment, data);
						that._getAdvancesData(that._oAdvAmendment, false);
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						that._getProcessSummary(oSummPayload, false);
					})
					.fail(function (jqXHR, status, err) {
						that._oAdvAmendment.setBusy(false);
					});
			} else {
				sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to bind Advances Response to table
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oDialog Popup Reference
		 * @param {Object} data Response data object:1
		 */
		_bindAdvanceResponse: function (oDialog, data) {
			var oBundle = this.getResourceBundle(),
				oStateData = this.getView().getModel("PosMaster").getData(),
				oAdvModel = oDialog.getModel("AdvData"),
				oModel = oDialog.getModel("Advances"),
				oData = oModel.getData().verticalData,
				flag = true,
				flag1 = false;

			if (data.hdr.status === "S") {
				for (var i = 0; i < oData.length; i++) {
					var count = 0;
					this._setValueStateNone(oData[i]);
					for (var j = 0; j < data.resp.length; j++) {
						if (oData[i].sNo === data.resp[j].sNo && !data.resp[j].errorList) {
							var aFields = ["sNo", "gstin", "taxPeriod", "section"];
							for (var f in data.resp[j]) {
								if (aFields.includes(f)) {
									continue;
								}
								if (f === "newAdvance") {
									oData[i].newTaxableValue = data.resp[j].newAdvance;
								}
								oData[i][f] = data.resp[j][f];
							}
							// Added by Bharat Gupta - 21.02.2021 for State Name
							oData[i].orgStateName = this._getStateName(oStateData, data.resp[j].orgPos);
							oData[i].newStateName = this._getStateName(oStateData, data.resp[j].newPos); // End of Code by Bharat - 21.02.2021
							oData[i].visi = false;
							break;
						} else if (oData[i].sNo === data.resp[j].sNo) {
							oData[i].id = data.resp[j].id;
							count = this._bindValueState(oData[i], data.resp[j].errorList, this._getAdvanceFieldMapping());
							flag1 = true;
							break;
						}
					}
					if (count > 0) {
						flag = false;
					}
				}
				if (flag && data.hdr.status === "S") {
					var oAdvData = oAdvModel.getData();
					oAdvData.verticalData = [];

					for (var k = 0, l = oData.length; k < l; k++) {
						if (oData[k].id) {
							oAdvData.verticalData.push($.extend(true, {}, oData[k]));
						}
					}
					oAdvModel.refresh(true);
					if (flag1) {
						sap.m.MessageBox.information(oBundle.getText("infoMsgOnSave"), {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				} else {
					sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
						styleClass: "sapUiSizeCompact"
					});
				}
				oModel.refresh(true);
			} else {
				sap.m.MessageBox.error(data.hdr.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to get Advance field value mapping
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} Fields mapping object
		 */
		_getAdvanceFieldMapping: function () {
			return {
				"suppGstin": "",
				"returnPeriod": "",
				"TransactionType": "transType",
				"MONTH": "month",
				"OrgPOS": "orgPos",
				"OrgRate": "orgRate",
				"OrgGrossAdvanceReceived": "orgTaxableValue",
				"OrgGrossAdvanceAdjustment": "orgTaxableValue",
				"newPOS": "newPos",
				"newRate": "newRate",
				"NewGrossAdvanceReceived": "newTaxableValue",
				"NewGrossAdvanceAdjustment": "newTaxableValue",
				"igstAmt": "igst",
				"cgstAmt": "cgst",
				"sgstAmt": "sgst",
				"cessAmt": "cess",
				"profitCentre": "profitCntr",
				"plantCode": "plant",
				"divison": "division",
				"location": "location",
				"salesOrg": "salesOrg",
				"distChannel": "distrChannel",
				"profitCentre3": "usrAccess1",
				"profitCentre4": "usrAccess2",
				"profitCentre5": "usrAccess3",
				"profitCentre6": "usrAccess4",
				"profitCentre7": "usrAccess5",
				"profitCentre8": "usrAccess6"
			};
		},

		/**
		 * Method called to delete selected Advance data
		 * Developed by: Bharat Gupta - 09.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view View Type for Action
		 */
		onDeleteAdvance: function (view) {
			if (view === "adv") {
				var oAdvDialog = this._oAdvances,
					vTable = "tabAdvVertical";
			} else {
				oAdvDialog = this._oAdvAmendment;
				vTable = "tabAmendVertical";
			}
			var that = this,
				oBundle = this.getResourceBundle(),
				aIndex = this.byId(vTable).getSelectedIndices(),
				oModel = oAdvDialog.getModel("Advances"),
				oData = oModel.getData(),
				aIdx = [],
				flag = false,
				oPayload = {
					"req": {
						"docType": null,
						"id": []
					}
				};
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgDeleteSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oPayload.req.docType) {
					oPayload.req.docType = oData.verticalData[aIndex[i]].section;
				}
				if (!oData.verticalData[aIndex[i]].id) {
					aIdx.push(aIndex[i]);
					continue;
				}
				oPayload.req.id.push(
					oData.verticalData[aIndex[i]].id
				);
				flag = true;
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmDelete"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						if (aIdx.length > 0) {
							that.byId(vTable).setSelectedIndex(-1);
							for (var j = aIdx.length; j > 0; j--) {
								oData.verticalData.splice(aIdx[j - 1], 1);
							}
							oModel.refresh(true);
						}
						if (flag) {
							oAdvDialog.setBusy(true);
							$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr1RecordDelete.do",
								data: JSON.stringify(oPayload),
								contentType: "application/json"
							}).done(function (data, status, jqXHR) {
								oAdvDialog.setBusy(false);
								if (data.hdr.status === "S") {
									var oSummPayload = {
										"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
									};
									that._getProcessSummary(oSummPayload, false);
									that._getAdvancesData(oAdvDialog, true);
									MessageBox.success(oBundle.getText("deleteSuccess"), {
										styleClass: "sapUiSizeCompact"
									});
								} else {
									MessageBox.error(oBundle.getText("deleteFailed1"), {
										styleClass: "sapUiSizeCompact"
									});
								}
							}).fail(function (jqXHR, status, err) {
								oAdvDialog.setBusy(false);
							});
						}
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oData Advance Data object
		 * @param {Object} errors Error object
		 * @param {Object} oFields Mapping field object
		 * @return {number} Error count
		 */
		_bindValueState: function (oData, errors, oFields) {
			var errCount = 0;
			errors.forEach(function (err) {
				var aErrField = err.errorField.split(",");
				aErrField.forEach(function (f) {
					var field = oFields[f];
					if (field && oData[field + "State"] !== "Error") {
						oData[field + "State"] = (err.errorType === "ERROR" ? "Error" : "Information");
						oData[field + "StateText"] = err.errorDesc;
					}
					if (err.errorType === "ERROR") {
						errCount++;
					}
				});
			});
			return errCount;
		},

		/**
		 * Method called to Navigate to Vertical data details on click of Vertical link in Popup Summary table
		 * Developed by: Bharat Gupta - 13.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Popup view type
		 */
		onVerticalLink: function (view) {
			if (view === "b2csa") {
				var oDialog = this._oB2csaSummary;
			} else if (view === "adv") {
				oDialog = this._oAdvances;
			} else if (view === "amend") {
				oDialog = this._oAdvAmendment;
			}
			var oModel = oDialog.getModel("Property");
			oModel.getData().segment = "detail";
			oModel.refresh(true);
		},

		onVerticalLinkADV: function (view) {
			if (view === "TRANSACTIONAL") {
				var oDialog = this._oAdvances;
				var oPayload = this._oAdvances.getModel("Payload").getData(),
					oFilterData = {
						"req": {
							"dataType": "outward",
							"criteria": "taxPeriod",
							"taxPeriodFrom": null,
							"taxPeriodTo": null,
							"navType": "GSTR1",
							"dataOriginType": null,
							"refId": null,
							"showGstnData": false,
							"type": "P",
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};
				oFilterData.req.taxPeriodFrom = oPayload.taxPeriod;
				oFilterData.req.taxPeriodTo = oPayload.taxPeriod;
				oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else if (view === "VERTICAL") {
				oDialog = this._oAdvances;
				var oModel = oDialog.getModel("Property");
				oModel.getData().segment = "detail";
				oModel.refresh(true);
			}
		},
		/**
		 * Called to open Nil, Exempt and Non GST Supplies dialog when user hyper link in Nil, Exempt and Non GST Supplies table
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing Object
		 */
		onPressNilExemptLink: function (oEvent) {
			var oPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData(),
				oProperty = this._popupProperty();
			if (!this._oNilExempt) {
				this._oNilExempt = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.NilExempt", this);
				this.getView().addDependent(this._oNilExempt);
			}
			this.byId("tabNilDetails").setSelectedIndex(-1);
			this._oNilExempt.setModel(new JSONModel(oProperty), "Property");
			this._oNilExempt.setModel(new JSONModel(oPayload), "Payload");
			this._oNilExempt.open();
			this._getNilExemptData();
		},

		/**
		 * Method called to get Nil Exempted and Non-GST data from DB
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getNilExemptData: function () {
			var oPayload = this._oNilExempt.getModel("Payload").getData(),
				oNilModel = this._oNilExempt.getModel("NilExempt"),
				payload = {
					"req": {
						"entityId": oPayload.entityId,
						"taxPeriod": oPayload.taxPeriod,
						"dataSecAttrs": oPayload.dataSecAttrs
					}
				};

			this._oNilExempt.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1NilExmpNonGstStauts.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._oNilExempt.setBusy(false);
					if (data.hdr.status === "S") {
						data.resp.gstnView.forEach(function (e) {
							e.visi = false;
						});
						data.resp.verticalData.forEach(function (e, i) {
							e.sNo = i + 1;
							e.visi = false;
							e.edit = false;
						});
						var oData = $.extend(true, [], data.resp);
						this._oNilExempt.setModel(new JSONModel(data.resp), "NilExempt");
						this._oNilExempt.setModel(new JSONModel(oData), "ExemptData");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oNilExempt.setBusy(false);
				}.bind(this));
		},

		/**
		 * Method called to navigate to Screen bases on selected Summary Type
		 * Developed by: Bharat Gupta - 17.09.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} type Summary Type (Transaction/Summary)
		 */
		onNilExemptLink: function (type) {
			if (type === "TRANSACTIONAL") {
				var oPayload = this._oNilExempt.getModel("Payload").getData(),
					oFilterData = {
						"req": {
							"dataType": "outward",
							"criteria": "taxPeriod",
							"taxPeriodFrom": null,
							"taxPeriodTo": null,
							"navType": "GSTR1",
							"dataOriginType": null,
							"refId": null,
							"showGstnData": false,
							"type": "P",
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};
				oFilterData.req.taxPeriodFrom = oPayload.taxPeriod;
				oFilterData.req.taxPeriodTo = oPayload.taxPeriod;
				oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else {
				var oPropModel = this._oNilExempt.getModel("Property");
				oPropModel.getData().segment = "detail";
				oPropModel.refresh(true);
			}
		},

		/**
		 * Method called to edit Nil Exempted and Non-GST data
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onEditNilExempt: function () {
			var oNilModel = this._oNilExempt.getModel("NilExempt");
			if (oNilModel) {
				var oData = oNilModel.getData();
				for (var i = 0; i < oData.gstnView.length; i++) {
					oData.gstnView[i].visi = true;
				}
				oNilModel.refresh(true);
			}
		},

		/**
		 * Method called to Cancel user change to original value
		 * Developed by: Bharat Gupta - 03.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onCancelNilExempt: function () {
			var oExemptData = this._oNilExempt.getModel("ExemptData").getData(),
				oNilModel = this._oNilExempt.getModel("NilExempt"),
				oNilExemptData = $.extend(true, [], oExemptData);

			oNilModel.setData(oNilExemptData);
			oNilModel.refresh(true);
		},

		/**
		 * Method called to move ASP Computed value to As Edited by User fields
		 * Developed by: Bharat Gupta - 03.06.2020
		 * Modified by: Bharat Gupta - 02.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onMoveNilComputedToUser: function () {
			var oBundle = this.getResourceBundle(),
				oData = this._oNilExempt.getModel("NilExempt").getData(),
				oNilExemptData = $.extend(true, [], oData),
				that = this,
				oPayload = {
					"req": []
				};

			for (var i = 0; i < oNilExemptData.gstnView.length; i++) {
				oNilExemptData.gstnView[i].usrExempted = oNilExemptData.gstnView[i].aspExempted;
				oNilExemptData.gstnView[i].usrNilRated = oNilExemptData.gstnView[i].aspNilRated;
				oNilExemptData.gstnView[i].usrNonGst = oNilExemptData.gstnView[i].aspNonGst;
				oPayload.req.push(this._getNilRatedObj(oNilExemptData.gstnView[i]));
			}
			sap.m.MessageBox.confirm(oBundle.getText("msgHsnMoveConfirm"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						that._oNilExempt.setBusy(true);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr1NilExmpNonGstSave.do",
								data: JSON.stringify(oPayload),
								contentType: "application/json"
							})
							.done(function (data, status, jqXHR) {
								var oSummPayload = {
									"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
								};
								that._getNilExemptData();
								that._getProcessSummary(oSummPayload, false);
							})
							.fail(function (jqXHR, status, err) {
								that._oNilExempt.setBusy(false);
							});
					}
				}
			});
		},

		/**
		 * Method called to get Nil Non Exempt save object
		 * Developed by: Bharat Gupta - 02.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Nil-Non object
		 * @return {Object} Payload object
		 */
		_getNilRatedObj: function (data) {
			return {
				"id": data.id,
				"docKey": data.docKey,
				"gstin": data.gstin,
				"taxPeriod": data.taxPeriod,
				"desc": data.desc,
				"aspNilRated": data.aspNilRated,
				"aspExempted": data.aspExempted,
				"aspNonGst": data.aspNonGst,
				"usrNilRated": data.usrNilRated,
				"usrExempted": data.usrExempted,
				"usrNonGst": data.usrNonGst
			};
		},

		/**
		 * Method called to triggered Ajax call to save Nil Exempt Non-GST data
		 * Developed by: Bharat Gupta - 19.06.3030
		 * Modified by: Bharat Gupta - 02.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSaveNillExempt: function () {
			var oBundle = this.getResourceBundle(),
				oExemptModel = this._oNilExempt.getModel("ExemptData"),
				oExemptData = oExemptModel.getData(),
				oNilModel = this._oNilExempt.getModel("NilExempt"),
				oNilData = oNilModel.getData(),
				that = this,
				oPayload = {
					"req": this._getNilSavePayload(oNilData.gstnView, oExemptData.gstnView)
				};

			if (oPayload.req.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("noChangeFound"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			// for (var i = 0; i < oNilData.gstnView.length; i++) {
			// 	oPayload.req.push(this._getNilRatedObj(oNilData.gstnView[i]));
			// }
			this._oNilExempt.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1NilExmpNonGstSave.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oNilExempt.setBusy(false);
					if (data.resp.status === "S") {
						for (var i = 0; i < oNilData.gstnView.length; i++) {
							oNilData.gstnView[i].visi = false;
						}
						// var oData = $.extend(true, [], oNilData),
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						// oExemptModel.setData(oData);
						// oExemptModel.refresh(true);

						// oNilModel.refresh(true);
						that._getNilExemptData();
						that._getProcessSummary(oSummPayload, false);
						sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						sap.m.MessageBox.success(oBundle.getText("saveFailed"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					that._oNilExempt.setBusy(false);
				});
		},

		/**
		 * Method called to check if Nil/Non/Exempt value has changed
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} nilData Popup Nil/Non/Exempt data
		 * @param {Object} extData Fixed Nil/Non/Exempt data
		 * @return {Object} Nil/Non/Exempt Save payload
		 */
		_getNilSavePayload: function (nilData, extData) {

			var payload = [];
			for (var i = 0; i < nilData.length; i++) {
				if (parseFloat(nilData[i].usrNilRated) !== parseFloat(extData[i].usrNilRated) ||
					parseFloat(nilData[i].usrExempted) !== parseFloat(extData[i].usrExempted) ||
					parseFloat(nilData[i].usrNonGst) !== parseFloat(extData[i].usrNonGst)) {

					payload.push(nilData[i]);
				}
			}
			return payload;
		},

		/**
		 * Developed by: Bharat Gupta - 30.09.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} Nil Exempt Vertical object
		 */
		_getNilExemptObject: function () {
			return {
				"sNo": 0,
				"hsn": null,
				"desc": null,
				"uqc": null,
				"qunty": 0,
				"nilInterReg": 0,
				"nilIntraReg": 0,
				"nilInterUnreg": 0,
				"nilIntraUnreg": 0,
				"extInterReg": 0,
				"extIntraReg": 0,
				"extInterUnreg": 0,
				"extIntraUnreg": 0,
				"nonInterReg": 0,
				"nonIntraReg": 0,
				"nonInterUnreg": 0,
				"nonIntraUnreg": 0,
				"visi": true,
				"edit": true
			};
		},

		/**
		 * Method called to add new records
		 * Developed by: Bharat Gupta - 30.09.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressAddNilExempt: function () {

			var oNilModel = this._oNilExempt.getModel("NilExempt");
			if (oNilModel) {
				var oNilData = oNilModel.getData().verticalData,
					obj = this._getNilExemptObject();

				obj.sNo = (oNilData.length === 0 ? 0 : oNilData[oNilData.length - 1].sNo) + 1;
				oNilData.push(obj);
				oNilModel.refresh(true);
				this.byId("tabNilDetails").setSelectedIndex(oNilData.length - 1);
				this.byId("tabNilDetails").setFirstVisibleRow(oNilData.length - 9);
			}
		},

		/**
		 * Method called to edit selected records
		 * Developed by: Bharat Gupta - 30.09.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressEditNilExempt: function () {

			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabNilDetails").getSelectedIndices(),
				oNilModel = this._oNilExempt.getModel("NilExempt");

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oNilData = oNilModel.getData();
			for (var i = 0; i < aIndex.length; i++) {
				oNilData.verticalData[aIndex[i]].visi = true;
			}
			oNilModel.refresh(true);
		},

		/**
		 * Method called get Nil/Non/Exempt Save object
		 * Developed by: Bharat Gupta - 30.09.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Nil/Non/Exempt data to save
		 * @param {Object} payload Nil/Non/Exempt get payload
		 * @return {Object} Nil Save object
		 */
		_getExemptSavePayload: function (data, payload) {
			return {
				"id": data.id || null,
				"sNo": data.sNo,
				"docKey": data.docKey || null,
				"gstin": payload.dataSecAttrs.GSTIN[0],
				"taxPeriod": payload.taxPeriod,
				"hsn": data.hsn || null,
				"desc": data.desc || null,
				"uqc": data.uqc || null,
				"qunty": data.qunty || 0,
				"nilInterReg": data.nilInterReg || 0,
				"nilIntraReg": data.nilIntraReg || 0,
				"nilInterUnreg": data.nilInterUnreg || 0,
				"nilIntraUnreg": data.nilIntraUnreg || 0,
				"extInterReg": data.extInterReg || 0,
				"extIntraReg": data.extIntraReg || 0,
				"extInterUnreg": data.extInterUnreg || 0,
				"extIntraUnreg": data.extIntraUnreg || 0,
				"nonInterReg": data.nonInterReg || 0,
				"nonIntraReg": data.nonIntraReg || 0,
				"nonInterUnreg": data.nonInterUnreg || 0,
				"nonIntraUnreg": data.nonIntraUnreg || 0
			};
		},

		validateOutwardFieldNil: function (data) {
			var oBundle = this.getResourceBundle(),
				flag = false;

			var aFields = ["hsn", "uqc"];
			aFields.forEach(function (f) {
				if ((!data[f]) || (!data[f] && data[f] !== 0)) {
					data[f + "State"] = "Error";
					data[f + "StateText"] = oBundle.getText("msgMandatory");
					flag = true;
				}
			});
			return flag;
		},

		/**
		 * Method called to add new records in Nil/Non/Exempt popup
		 * Developed by: Bharat Gupta - 10.10.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressSaveNilExempt: function () {
			var aIndex = this.byId("tabNilDetails").getSelectedIndices(),
				oPayNil = this._oNilExempt.getModel("Payload").getProperty("/"),
				oNilModel = this._oNilExempt.getModel("NilExempt"),
				oNilData = oNilModel.getProperty("/verticalData"),
				flag = false,
				oPayload = {
					"req": []
				};

			aIndex.forEach(function (e) {
				this._setValueStateNone(oNilData[e]);
				flag = this.validateOutwardFieldNil(oNilData[e]) || flag;
				if (!flag && oNilData[e].visi) {
					oPayload.req.push(this._getExemptSavePayload(oNilData[e], oPayNil));
				}
			}.bind(this));

			if (!oPayload.req.length) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("noChangeFound"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (flag) {
				sap.m.MessageBox.error(this.getResourceBundle().getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oNilModel.refresh(true);
				return;
			}
			this._oNilExempt.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1NilExmpNonVerticalSave.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					var oSummPayload = {
							"req": this.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						},
						flag = this._bindNil(this._oNilExempt, data);
					if (flag) {
						this._getNilExemptData1(this._oNilExempt, false);
						this._getProcessSummary(oSummPayload, false);
					}
					this._oNilExempt.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oNilExempt.setBusy(false);
				}.bind(this));
		},

		_getNilExemptData1: function (oAspVertical, flag) {
			var oPayload = {
				"req": oAspVertical.getModel("Payload").getData()
			};
			oAspVertical.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1NilExmpNonGstStauts.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					oAspVertical.setBusy(false);
					if (data.hdr.status === "S" && flag) {
						data.resp.verticalData.forEach(function (el, i) {
							el.sNo = i + 1;
							el.edit = false;
							el.visi = false;
						});
						var oData = $.extend(true, [], data.resp);
						oAspVertical.setModel(new JSONModel(data.resp), "NilExempt");
						oAspVertical.setModel(new JSONModel(oData), "ExemptData");
					} else if (data.hdr.status === "S" && !flag) {
						var oAspModel = oAspVertical.getModel("NilExempt"),
							oAspData = oAspModel.getData();
						oAspData.summary = data.resp.summary;
						oAspData.gstnView = data.resp.gstnView;
						oAspData.verticalData = data.resp.verticalData;
						oAspData.gstnView.forEach(function (e) {
							e.visi = false;
						});
						oAspData.verticalData.forEach(function (e) {
							e.visi = false;
						});
						oAspModel.refresh(true);
					}
				})
				.fail(function (jqXHR, status, err) {
					oAspVertical.setBusy(false);
				});
		},

		_bindValueStateNil: function (oData, errors, oFields) {
			var errCount = 0;
			for (var i = 0; i < errors.length; i++) {
				var aErrField = errors[i].errorField.split(",");
				for (var j = 0; j < aErrField.length; j++) {
					var field = "hsn";
					if (field && oData[field + "State"] !== "Error") {
						oData[field + "State"] = (errors[i].errorType === "ERR" ? "Error" : "Information");
						oData[field + "StateText"] = errors[i].errorDesc;
					}
					if (errors[i].errorType === "ERR") {
						errCount++;
					}
				}
			}
			return errCount;
		},

		_getNilFieldMapping: function () {
			return {
				"hsn": "hsn",
				"itemUqc": "uqc"
			};
		},

		_bindNil: function (oDialog, data) {
			var oBundle = this.getResourceBundle(),
				oAspModel = oDialog.getModel("NilExempt"),
				oAspData = oAspModel.getProperty("/verticalData"),
				oModel = oDialog.getModel("ExemptData"),
				flag = true,
				flag1 = false;

			if (data.hdr.status === "S") {
				oAspData.forEach(function (el) {
					var obj = data.resp.find(function (resp) {
							return el.sNo === +resp.sNo
						}),
						count = 0;
					this._setValueStateNone(el);
					if (obj && !obj.errorList) {
						this._moveObject(el, obj);
						el.visi = false;
					} else if (obj) {
						el.id = obj.id;
						count = this._bindValueState(el, obj.errorList, this._getNilFieldMapping());
						flag1 = true;
						if (count === 0) {
							this._moveObject(el, obj);
						}
					}
					if (count > 0) {
						flag = false;
					}
				}.bind(this));
				if (flag && data.hdr.status === "S") {
					var oData = oModel.getData();
					oData.verticalData = [];
					oAspData.forEach(function (el) {
						if (el.id) {
							oData.verticalData.push($.extend(true, {}, el));
						}
					});
					oModel.refresh(true);
					if (flag1) {
						sap.m.MessageBox.information(oBundle.getText("infoMsgOnSave"), {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				} else {
					sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
						styleClass: "sapUiSizeCompact"
					});
				}
				oAspModel.refresh(true);
				return flag;
			} else {
				sap.m.MessageBox.error(data.hdr.message, {
					styleClass: "sapUiSizeCompact"
				});
				return false;
			}
		},

		_moveObject: function (el, obj) {
			for (var f in obj) {
				el[f] = obj[f];
			}
		},

		/**
		 * Method called to delete selected records
		 * Developed by: Bharat Gupta - 10.10.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressDeleteNilExempt: function () {
			var that = this,
				flag = false,
				oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabNilDetails").getSelectedIndices(),
				oPayNil = this._oNilExempt.getModel("Payload").getData(),
				oModel = this._oNilExempt.getModel("NilExempt"),
				oData = oModel.getData(),
				aIdx = [],
				oPayload = {
					"req": {
						"docType": "NIL",
						"id": [],
						"docKey": [],
						"gstin": oPayNil.dataSecAttrs.GSTIN[0],
						"taxPeriod": oPayNil.taxPeriod
					}
				};
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgDeleteSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oData.verticalData[aIndex[i]].id) {
					aIdx.push(aIndex[i]);
				} else {
					flag = true;
					oPayload.req.id.push(oData.verticalData[aIndex[i]].id);
					oPayload.req.docKey.push(oData.verticalData[aIndex[i]].docKey);
				}
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmDelete"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						if (aIdx.length > 0) {
							that.byId("tabNilDetails").setSelectedIndex(-1);
							for (var j = aIdx.length; j > 0; j--) {
								oData.verticalData.splice(aIdx[j - 1], 1);
							}
							oModel.refresh(true);
						}
						if (flag) {
							that._oNilExempt.setBusy(true);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/nilExmpNonVerticalDelete.do",
									data: JSON.stringify(oPayload),
									contentType: "application/json"
								})
								.done(function (data, status, jqXHR) {
									that._oNilExempt.setBusy(false);
									if (data.hdr.status === "S") {
										var oSummPayload = {
											"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
										};
										that._getNilExemptData();
										that._getProcessSummary(oSummPayload, false);
										MessageBox.success(oBundle.getText("deleteSuccess"), {
											styleClass: "sapUiSizeCompact"
										});
									} else {
										MessageBox.error(oBundle.getText("deleteFailed"), {
											styleClass: "sapUiSizeCompact"
										});
									}
								})
								.fail(function (jqXHR, status, err) {
									that._oNilExempt.setBusy(false);
								});
						}
					}
				}
			});
		},

		/**
		 * Called to open HSN Summary dialog when user hyper link in HSN Summary table
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressHsnSummaryLink: function () {
			var oPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData(),
				oProperty = this._popupProperty();

			if (!this._oHsnSummary) {
				this._oHsnSummary = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.HsnSummary", this);
				this.getView().addDependent(this._oHsnSummary);
			}
			this._oHsnSummary.setModel(new JSONModel(oProperty), "Property");
			this._oHsnSummary.setModel(new JSONModel(oPayload), "Payload");
			this._oHsnSummary.setBusy(true);
			this._oHsnSummary.open();
			this._getHsnSummaryData();
		},

		/**
		 * Called to get HSN Summary details data from DB
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getHsnSummaryData: function () {
			var oHsnPayload = this._oHsnSummary.getModel("Payload").getProperty("/"),
				oHsnModel = this._oHsnSummary.getModel("HsnSummary"),
				payload = {
					"req": oHsnPayload
				};

			if (oHsnModel) {
				oHsnModel.setData(null);
				oHsnModel.refresh(true);
			}
			this._oHsnSummary.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1HsnAspVertical.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._oHsnSummary.setBusy(false);
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.verticalData.length; i++) {
							data.resp.verticalData[i].sNo = i + 1;
							data.resp.verticalData[i].visi = false;
							data.resp.verticalData[i].edit = false;
						}
						if (this.rateIncludedInHsn) {
							data.resp.rateIncludedInHsn = true;
						} else {
							data.resp.rateIncludedInHsn = false;
						}
						var oData = $.extend(true, {}, data.resp);
						this._oHsnSummary.setModel(new JSONModel(data.resp), "HsnSummary");
						this._oHsnSummary.setModel(new JSONModel(oData), "HsnData");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oHsnSummary.setBusy(false);
				}.bind(this));
		},

		/**
		 * Method called to get HSN Summary Object
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} HSN Object
		 */
		_getHsnObject: function () {
			var oData = this._oHsnSummary.getModel("Payload").getData();
			return {
				"sgstn": oData.dataSecAttrs.GSTIN[0],
				"taxPeriod": oData.taxPeriod,
				"hsn": null,
				"desc": null,
				"taxRate": null,
				"aspDesc": null,
				"uiDesc": null,
				"uqc": null,
				"aspTotalValue": 0,
				"aspTaxableValue": 0,
				"aspIgst": 0,
				"aspCgst": 0,
				"aspSgst": 0,
				"aspCess": 0,
				"usrTotalValue": 0,
				"usrTaxableValue": 0,
				"usrIgst": 0,
				"usrCgst": 0,
				"usrSgst": 0,
				"usrCess": 0,
				"visi": true,
				"edit": true
			};
		},

		/**
		 * Method called to new record for HSN summary data
		 * Developed by: Bharat Gupta - 03.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onAddHsnSummary: function () {
			var oHsnModel = this._oHsnSummary.getModel("HsnSummary");
			if (oHsnModel) {
				var oHsnData = oHsnModel.getData().verticalData,
					obj = this._getHsnObject();

				obj.sNo = (oHsnData.length === 0 ? 0 : oHsnData[oHsnData.length - 1].sNo) + 1;
				oHsnData.push(obj);
				oHsnModel.refresh(true);
				this.byId("tabVerticalHsn").setSelectedIndex(oHsnData.length - 1);
				this.byId("tabVerticalHsn").setFirstVisibleRow(oHsnData.length - 9);
			}
		},

		/**
		 * Method called to edit HSN summary data
		 * Developed by: Bharat Gupta - 25.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onEditHsnSummary: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabVerticalHsn").getSelectedIndices(),
				oHsnModel = this._oHsnSummary.getModel("HsnSummary");

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				var oHsnData = oHsnModel.getData();
				for (var i = 0; i < aIndex.length; i++) {
					oHsnData.verticalData[aIndex[i]].visi = true;
				}
				oHsnModel.refresh(true);
			}
		},

		/**
		 * Method called when user cancel change to revert to original value
		 * Developed by: Bharat Gupta - 03.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onCancelHsnSummary: function () {
			var oHsnModel = this._oHsnSummary.getModel("HsnSummary"),
				oData = this._oHsnSummary.getModel("HsnData").getData(),
				oHsnData = $.extend(true, {}, oData);

			this.byId("tabVerticalHsn").setSelectedIndex(-1);
			oHsnModel.setData(oHsnData);
			oHsnModel.refresh(true);
		},

		/**
		 * Method called to move HSN ASP Computed value to As Edited by User fields
		 * Developed by: Bharat Gupta - 03.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onMoveHsnComputedToUser: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabVerticalHsn").getSelectedIndices(),
				oData = this._oHsnSummary.getModel("HsnSummary").getData().verticalData,
				that = this,
				oPayload = {
					"req": []
				};

			if (!aIndex.length) {
				sap.m.MessageBox.information("Select records to move", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			aIndex.forEach(function (idx) {
				oPayload.req.push({
					"sNo": oData[idx].sNo,
					"hsn": oData[idx].hsn,
					"recordType": oData[idx].recordType,
					// "desc": oData[idx].desc,
					"aspDesc": oData[idx].aspDesc,
					// "uiDesc": oData[idx].aspQunty == 0 ? "" : oData[idx].aspDesc,
					"uiDesc": oData[idx].aspDesc,
					"docKey": oData[idx].docKey,
					"sgstn": oData[idx].sgstn,
					"taxPeriod": oData[idx].taxPeriod,
					"uqc": oData[idx].uqc,
					"taxRate": oData[idx].taxRate,
					"aspQunty": oData[idx].aspQunty,
					"aspTotalValue": oData[idx].aspTotalValue,
					"aspTaxableValue": oData[idx].aspTaxableValue,
					"aspIgst": oData[idx].aspIgst,
					"aspCgst": oData[idx].aspCgst,
					"aspSgst": oData[idx].aspSgst,
					"aspCess": oData[idx].aspCess,
					"usrQunty": oData[idx].aspQunty,
					"usrTotalValue": oData[idx].aspTotalValue,
					"usrTaxableValue": oData[idx].aspTaxableValue,
					"usrIgst": oData[idx].aspIgst,
					"usrCgst": oData[idx].aspCgst,
					"usrSgst": oData[idx].aspSgst,
					"usrCess": oData[idx].aspCess
				});
			});
			sap.m.MessageBox.confirm(oBundle.getText("msgHsnMoveConfirm"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						this._oHsnSummary.setBusy(true);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/moveDigiGstToUserEdit.do",
								data: JSON.stringify(oPayload),
								contentType: "application/json"
							})
							.done(function (data, status, jqXHR) {

								if (data.hdr.status === "E") {
									MessageBox.error(data.hdr.message, {
										styleClass: "sapUiSizeCompact"
									});
									this._oHsnSummary.setBusy(false);
								} else {
									MessageBox.success(data.hdr.message, {
										styleClass: "sapUiSizeCompact"
									});
									var oSummPayload = {
										"req": this.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
									};
									this._getProcessSummary(oSummPayload, false);
									this._getHsnSummaryData();
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								this._oHsnSummary.setBusy(false);
							}.bind(this));
					}
				}.bind(this)
			});
		},

		/**
		 * Method called to delete selected HSN Summary data
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onDeleteHsnSummary: function () {
			var that = this,
				oBundle = this.getResourceBundle(),
				oHsnPayload = this._oHsnSummary.getModel("Payload").getData(),
				oModel = this._oHsnSummary.getModel("HsnSummary"),
				oData = oModel.getData(),
				aIndex = this.byId("tabVerticalHsn").getSelectedIndices(),
				aIdx = [],
				flag = false,
				oPayload = {
					"req": {
						"docType": "HSN",
						"id": [],
						"docKey": [],
						"gstin": oHsnPayload.dataSecAttrs.GSTIN[0],
						"taxPeriod": oHsnPayload.taxPeriod
					}
				};
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgDeleteSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oData.verticalData[aIndex[i]].docKey) {
					aIdx.push(aIndex[i]);
					continue;
				}
				oPayload.req.docKey.push(
					oData.verticalData[aIndex[i]].docKey
				);
				flag = true;
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmDelete"), {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						if (aIdx.length > 0) {
							that.byId("tabVerticalHsn").setSelectedIndex(-1);
							for (var j = aIdx.length; j > 0; j--) {
								oData.verticalData.splice(aIdx[j - 1], 1);
							}
							oModel.refresh(true);
						}
						if (flag) {
							that._oHsnSummary.setBusy(true);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/gstr1RecordDelete.do",
									data: JSON.stringify(oPayload),
									contentType: "application/json"
								})
								.done(function (data, status, jqXHR) {
									that._oHsnSummary.setBusy(false);
									if (data.hdr.status === "S") {
										var oSummPayload = {
											"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
										};
										that._getProcessSummary(oSummPayload, false);
										that._getHsnSummaryData();
										MessageBox.success(oBundle.getText("deleteSuccess"), {
											styleClass: "sapUiSizeCompact"
										});
									} else {
										MessageBox.error(oBundle.getText("deleteFailed1"), {
											styleClass: "sapUiSizeCompact"
										});
									}
								})
								.fail(function (jqXHR, status, err) {
									that._oHsnSummary.setBusy(false);
								});
						}
					}
				}
			});
		},

		/**
		 * Method called to get Payload Save HSN Summary
		 * Developed by: Bharat Gupta - 23.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data HSN Summary data
		 * @return {Object} HSN Summary Payload
		 */
		_getHsnSavePayload: function (data) {
			return {
				"sNo": data.sNo,
				"docKey": data.docKey || null,
				"sgstn": data.sgstn,
				"taxPeriod": data.taxPeriod,
				"hsn": data.hsn,
				"recordType": data.recordType,
				// "desc": data.desc,
				"aspDesc": data.aspDesc,
				"uiDesc": data.uiDesc,
				"uqc": data.uqc,
				"taxRate": data.taxRate,
				"aspQunty": data.aspQunty || 0,
				"aspTotalValue": data.aspTotalValue || 0,
				"aspTaxableValue": data.aspTaxableValue || 0,
				"aspIgst": data.aspIgst || 0,
				"aspCgst": data.aspCgst || 0,
				"aspSgst": data.aspSgst || 0,
				"aspCess": data.aspCess || 0,
				"usrQunty": data.usrQunty || 0,
				"usrTotalValue": data.usrTotalValue || 0,
				"usrTaxableValue": data.usrTaxableValue || 0,
				"usrIgst": data.usrIgst || 0,
				"usrCgst": data.usrCgst || 0,
				"usrSgst": data.usrSgst || 0,
				"usrCess": data.usrCess || 0
			};
		},

		/**
		 * Method called to Save HSN Summary Vertical Data
		 * Developed by: Bharat Gupta - 23.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSaveHsnSummary: function () {
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("tabVerticalHsn").getSelectedIndices(),
				oHsnModel = this._oHsnSummary.getModel("HsnSummary"),
				oHsnData = oHsnModel.getData().verticalData,
				that = this,
				flag = true,
				oPayload = {
					"req": []
				};
			if (aIndex.length == 0) {
				sap.m.MessageBox.error("Please select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oHsnData[aIndex[i]].recordType) {
					flag = false;
					oHsnData[aIndex[i]].recordTypeState = "Error";
					oHsnData[aIndex[i]].recordTypeStateText = oBundle.getText("msgMandatory");
				}
				if (!oHsnData[aIndex[i]].hsn) {
					flag = false;
					oHsnData[aIndex[i]].hsnState = "Error";
					oHsnData[aIndex[i]].hsnStateText = oBundle.getText("msgMandatory");
				}
				/*if (!oHsnData[aIndex[i]].desc) {
					flag = false;
					oHsnData[aIndex[i]].descState = "Error";
					oHsnData[aIndex[i]].descStateText = oBundle.getText("msgMandatory");
				}*/
				if (!oHsnData[aIndex[i]].uqc) {
					flag = false;
					oHsnData[aIndex[i]].uqcState = "Error";
					oHsnData[aIndex[i]].uqcStateText = oBundle.getText("msgMandatory");
				}
				if (this.rateIncludedInHsn) {
					if (!JSON.stringify(oHsnData[aIndex[i]].taxRate)) {
						flag = false;
						oHsnData[aIndex[i]].taxRateState = "Error";
						oHsnData[aIndex[i]].taxRateStateText = oBundle.getText("msgMandatory");
					}
				}
				//  && oHsnData[aIndex[i]].visi
				if (flag) {
					oPayload.req.push(this._getHsnSavePayload(oHsnData[aIndex[i]]));
				}
			}
			if (flag && oPayload.req.length > 0) {
				this._oHsnSummary.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr1HsnUserUpdateData.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						that._bindHsnSummaryData(data);
						that._oHsnSummary.setBusy(false);
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						that._getProcessSummary(oSummPayload, false);
					})
					.fail(function (jqXHR, status, err) {
						that._oHsnSummary.setBusy(false);
					});
			} else {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oHsnModel.refresh(true);
			}
		},

		/**
		 * Method called when value changed
		 * Developed by: Bharat Gupta - 06.11.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeHsnValue: function (oEvent) {
			oEvent.getSource().setValueState("None");
		},

		/**
		 * Method called to get HSN Summary Field mapping
		 * Developed by: Bharat Gupta - 23.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} HSN Summary field mapping
		 */
		_getHsnFieldMapping: function () {
			return {
				"NewHsnOrSac": "hsn",
				"itemUqc": "uqc",
				// "": "usrTaxableValue",
				"igstAmt": "usrIgst",
				"cgstAmt": "usrCgst",
				"sgstAmt": "usrSgst",
				"cessAmt": "usrCess"
			};
		},

		/**
		 * Called to bind response data to HSN Summary popup
		 * Developed by: Bharat Gupta - 23.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Response data
		 */
		_bindHsnSummaryData: function (data) {
			var oBundle = this.getResourceBundle(),
				oHsnModel = this._oHsnSummary.getModel("HsnData"),
				oModel = this._oHsnSummary.getModel("HsnSummary"),
				oData = oModel.getData().verticalData,
				flag = true;

			for (var i = 0; i < oData.length; i++) {
				var count = 0;

				this._setValueStateNone(oData[i]);
				for (var j = 0; j < data.resp.length; j++) {
					if (oData[i].sNo === data.resp[j].sNo && data.resp[j].errorList.length === 0) {
						oData[i].docKey = data.resp[j].docKey;
						oData[i].usrTaxableValue = data.resp[j].usrTaxableValue;
						oData[i].usrTotalValue = data.resp[j].usrTotalValue;
						oData[i].usrCess = data.resp[j].usrCess;
						oData[i].usrCgst = data.resp[j].usrCgst;
						oData[i].usrIgst = data.resp[j].usrIgst;
						oData[i].usrQunty = data.resp[j].usrQunty;
						oData[i].usrSgst = data.resp[j].usrSgst;
						oData[i].visi = false;
						oData[i].edit = false;
						break;
					} else if (oData[i].sNo === data.resp[j].sNo) {
						oData[i].docKey = data.resp[j].docKey;
						count = this._bindValueState(oData[i], data.resp[j].errorList, this._getHsnFieldMapping());
						break;
					}
				}
				if (count > 0) {
					flag = false;
				}
			}
			if (flag && data.hdr.status === "S") {
				// var aData = {
				// 	"verticalData": []
				// };
				// for (var k = 0, l = oData.length; k < l; k++) {
				// 	if (oData[k].docKey) {
				// 		aData.verticalData.push(oData[k]);
				// 	}
				// }
				// oHsnModel.setData(aData);
				// oHsnModel.refresh(true);
				this._getHsnSummaryData();
				sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
			}
			oModel.refresh(true);
		},

		/**
		 * Called to open Document Issue dialog when user hyper link in Document Issue table
		 * Developed by: Bharat Gupta - 15.05.2020
		 * Modified by:  Bharat Gupta - 04.08.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressDocIssedLink: function () {

			this.lastUpdated("DS");
			var oPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData(),
				oProperty = this._popupProperty();
			if (!this._oDocIssued) {
				this._oDocIssued = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.DocIssued", this);
				this.getView().addDependent(this._oDocIssued);
			}
			oProperty.docNatureId = 1;
			this._oDocIssued.setModel(new JSONModel(oProperty), "Property");
			this._oDocIssued.setModel(new JSONModel(oPayload), "Payload");
			this._oDocIssued.open();
			this._getDocIssueSummary();
		},

		lastUpdated: function (identifier) {
			var req = {
				"req": {
					"entityId": $.sap.entityID,
					"returnPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"identifier": identifier
				}
			};
			var GstinSaveModel = new JSONModel();
			var that = this;
			//this._oDocIssued.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1DocSeriesAutoCompStatus.do",
					data: JSON.stringify(req),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					//	that._oDocIssued.setBusy(false);
					GstinSaveModel.setData(data.resp);
					that.getView().setModel(GstinSaveModel, "DocSeriesAutoCompStatus");
				})
				.fail(function (jqXHR, status, err) {
					// that._oDocIssued.setBusy(false);
				});
		},

		/**
		 * Method called to get Doc Issue Summary data
		 * Developed by:  Bharat Gupta - 04.08.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_getDocIssueSummary: function () {
			var that = this,
				payload = {
					"req": this._oDocIssued.getModel("Payload").getData()
				};
			this._oDocIssued.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getdocseries.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oDocIssued.setBusy(false);
					if (data.hdr.status === "S") {
						that._oDocIssued.setModel(new JSONModel(data.resp.gstnView), "DocIssuedSummary");
					}
				})
				.fail(function (jqXHR, status, err) {
					that._oDocIssued.setBusy(false);
				});
		},

		/**
		 * Method called to get Data based when user change Segment button
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeDocSegment: function (oEvent) {

			if (this.getView().byId("id_ChangeDocSegment").getSelectedKey() === "vertical") {
				var oPropData = this._oDocIssued.getModel("Property").getData();
				this.onChangeDocNature(oPropData.docNatureId);
			}
		},

		/**
		 * Method called to get Data based when user change Segment button
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {number} natureId Selected Doc Nature Id
		 */
		onPressDocNature: function (natureId) {

			var oPropModel = this._oDocIssued.getModel("Property"),
				oPropData = oPropModel.getData();
			oPropData.segment = "vertical";
			oPropData.docNatureId = natureId;
			oPropModel.refresh(true);
			this.onChangeDocNature(natureId);
		},

		/**
		 * Method called to get Data based on selected Document Nature
		 * Developed by: Bharat Gupta - 18.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {number} natureId Selected Doc Nature Id
		 */
		onChangeDocNature: function (natureId) {

			var oPayloadData = this._oDocIssued.getModel("Payload").getData(),
				oDocModel = this._oDocIssued.getModel("DocIssuedData"),
				that = this,
				payload = {
					"req": $.extend(true, {}, oPayloadData)
				};
			payload.req.docNatureId = natureId;
			if (oDocModel) {
				oDocModel.setData(null);
				oDocModel.refresh(true);
			}
			that._oDocIssued.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getdocseries.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oDocIssued.setBusy(false);
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.gstnView.length; i++) {
							data.resp.gstnView[i].sNo = i + 1;
							data.resp.gstnView[i].visi = false;
						}
						var oData = $.extend(true, [], data.resp.gstnView);
						that._oDocIssued.setModel(new JSONModel(data.resp.gstnView), "DocIssuedData");
						that._oDocIssued.setModel(new JSONModel(oData), "DocData");
					}
				})
				.fail(function (jqXHR, status, err) {
					that._oDocIssued.setBusy(false);
				});
		},

		/**
		 * Method called to Add new record to Document Issued
		 * Developed by: Bharat Gupta - 18.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onAddDocIssued: function () {

			var oPropModel = this._oDocIssued.getModel("Property").getData(),
				oModel = this._oDocIssued.getModel("DocIssuedData"),
				oData = oModel.getData(),
				obj = {
					"sNo": (oData.length === 0 ? 0 : oData[oData.length - 1].sNo) + 1,
					"docNatureId": oPropModel.docNatureId,
					"docNature": this.docIssuedHeader(oPropModel.docNatureId),
					"seriesFrom": null,
					"seriesTo": null,
					"total": null,
					"cancelled": null,
					"netIssued": 0,
					"visi": true
				};
			oData.push(obj);
			oModel.refresh(true);
			this.byId("tabVerticalDoc").setSelectedIndex(oData.length - 1);
		},

		/**
		 * Method called to edit Select Document Issued data
		 * Developed by: Bharat Gupta - 18.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onEditDocIssued: function () {

			var aIndex = this.byId("tabVerticalDoc").getSelectedIndices();
			if (aIndex.length === 0) {
				var oBundle = this.getResourceBundle();
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oDocModel = this._oDocIssued.getModel("DocIssuedData"),
				oDocData = oDocModel.getData();

			for (var i = 0; i < aIndex.length; i++) {
				oDocData[aIndex[i]].visi = true;
			}
			oDocModel.refresh(true);
		},

		/**
		 * Method called to update Document Issued data when Total/cancelled data changed
		 * Developed by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeDocIssue: function (oEvent) {

			var obj = oEvent.getSource().getBindingContext("DocIssuedData").getObject();
			obj.netIssued = (!obj.total ? 0 : parseInt(obj.total, 10)) - (!obj.cancelled ? 0 : parseInt(obj.cancelled, 10));
		},

		/**
		 * Method called to validate selected Document Issued data
		 * Developed by: Bharat Gupta - 07.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Doc Issued data object
		 * @return {boolean} validation status flag
		 */
		validateDocIssued: function (data) {
			var aFields = ["seriesFrom", "seriesTo", "total", "cancelled"],
				oBundle = this.getResourceBundle(),
				flag = false;
			for (var i = 0; i < aFields.length; i++) {
				if (!data[aFields[i]] && data[aFields[i]] !== 0) {
					data[aFields[i] + "State"] = "Error";
					data[aFields[i] + "StateText"] = oBundle.getText("msgMandatory");
					flag = true;
				}
			}
			return flag;
		},

		/**
		 * Method called to get Document issued payload object
		 * Developed by: Bharat Gupta - 18.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Document Issued payload object
		 * @param {Object} data Document Issued data object
		 * @return {Object} Save paylaod object
		 */
		_getDocIssueObj: function (payload, data) {

			return {
				"sNo": data.sNo,
				"id": data.id || null,
				"userSNo": data.sNo,
				"sgstin": payload.dataSecAttrs.GSTIN[0],
				"retPeriod": payload.taxPeriod,
				"docNatureId": data.docNatureId,
				"docNature": data.docNature,
				"seriesFrom": data.seriesFrom,
				"seriesTo": data.seriesTo,
				"total": data.total || 0,
				"cancelled": data.cancelled || 0,
				"netIssued": data.netIssued || 0
			};
		},

		/**
		 * Method called to save Select Document Issued data
		 * Developed by: Bharat Gupta - 18.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onSaveDocIssued: function () {

			var aIndex = this.byId("tabVerticalDoc").getSelectedIndices(),
				that = this;
			var oBundle = this.getResourceBundle();
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgSaveSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var docPayload = this._oDocIssued.getModel("Payload").getData(),
				oModel = this._oDocIssued.getModel("DocIssuedData"),
				oData = oModel.getData(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndex.length; i++) {
				this._setValueStateNone(oData[aIndex[i]]);
				var flag = this.validateDocIssued(oData[aIndex[i]]);
				if (oData[aIndex[i]].visi) {
					oPayload.req.push(this._getDocIssueObj(docPayload, oData[aIndex[i]]));
				}
			}
			if (flag) {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oModel.refresh(true);

			} else if (oPayload.req.length > 0) {
				this._oDocIssued.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/docseriesSaveAndEdit.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						that._oDocIssued.setBusy(false);
						that._bindDocIssueResp(data);
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						that._getProcessSummary(oSummPayload, false);
						that._getDocIssueSummary();
					})
					.fail(function (jqXHR, status, err) {
						that._oDocIssued.setBusy(false);
					});
			} else {
				sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to get object of Document Issued Field mapping
		 * Developed by: Bharat Gupta - 01.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @return {Object} Doc Nature Field mapping
		 */
		_docNatureMapping: function () {
			return {
				"suppGstin": "",
				"returnPeriod": "",
				"serialNumber": "",
				"NatureOfDocument": "",
				"From": "seriesFrom",
				"to": "seriesTo",
				"totalNumber": "total",
				"can": "cancelled",
				"netNumber": "netIssued"
			};
		},

		/**
		 * Method called to bind Document Issued Response
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Response data
		 */
		_bindDocIssueResp: function (data) {
			if (data.hdr.status === "S") {
				var oBundle = this.getResourceBundle(),
					oPropData = this._oDocIssued.getModel("Property").getData(),
					oDocModel = this._oDocIssued.getModel("DocIssuedData"),
					oModel = this._oDocIssued.getModel("DocData"),
					oDocData = oDocModel.getData(),
					flag = true;

				for (var i = 0; i < oDocData.length; i++) {
					var count = 0;
					this._setValueStateNone(oDocData[i]);
					for (var j = 0; j < data.resp.length; j++) {
						if (oDocData[i].sNo === data.resp[j].sNo && !data.resp[j].errorList) {
							oDocData[i].id = data.resp[j].id;
							oDocData[i].total = data.resp[j].total;
							oDocData[i].cancelled = data.resp[j].cancelled;
							oDocData[i].netIssued = data.resp[j].netIssued;
							oDocData[i].visi = false;
							break;
						} else if (oDocData[i].sNo === data.resp[j].sNo) {
							count = this._bindValueState(oDocData[i], data.resp[j].errorList, this._docNatureMapping());
							break;
						}
					}
					if (count > 0) {
						flag = false;
					}
				}
				if (flag) {
					var aData = [];
					for (var k = 0, l = oDocData.length; k < l; k++) {
						if (oDocData[k].id) {
							aData.push($.extend(true, {}, oDocData[k]));
						}
					}
					oModel.setData(aData);
					oModel.refresh(true);
					sap.m.MessageBox.success(oBundle.getText("saveSuccess"), {
						styleClass: "sapUiSizeCompact"
					});
					this.onChangeDocNature(oPropData.docNatureId);
				} else {
					sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
						styleClass: "sapUiSizeCompact"
					});
				}
				oDocModel.refresh(true);
			}
		},

		/**
		 * Method called to Delete selected Document Issued
		 * Developed by: Sarvmangla - 08.07.2019
		 * Modified by: Bharat Gupta - 09.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onDeleteDocIssued: function () {

			var oBundle = this.getResourceBundle(),
				oModel = this.byId("tabVerticalDoc").getModel("DocIssuedData"),
				oDocData = oModel.getData(),
				aIndex = this.byId("tabVerticalDoc").getSelectedIndices(),
				vDocNatureId = this.byId("slSeriesFor").getSelectedKey(),
				that = this,
				aIdx = [],
				flag = false,
				postData = {
					"req": {
						"id": []
					}
				};
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgDeleteSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oDocData[aIndex[i]].id) {
					aIdx.push(aIndex[i]);
					continue;
				}
				postData.req.id.push(
					oDocData[aIndex[i]].id
				);
				flag = true;
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmDelete"), {
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						if (aIdx.length > 0) {
							that.byId("tabVerticalDoc").setSelectedIndex(-1);
							for (var j = aIdx.length; j > 0; j--) {
								oDocData.splice(aIdx[j - 1], 1);
							}
							oModel.refresh(true);
						}
						if (flag) {
							that._oDocIssued.setBusy(true);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/gstr1DocseriesDelete.do",
									data: JSON.stringify(postData),
									contentType: "application/json"
								})
								.done(function (data, status, jqXHR) {
									that._oDocIssued.setBusy(false);
									if (data.hdr.status === "S") {
										var oSummPayload = {
											"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
										};
										that._getProcessSummary(oSummPayload, false);
										that.onChangeDocNature(vDocNatureId);
										that._getDocIssueSummary();
										MessageBox.success(oBundle.getText("deleteSuccess"), {
											styleClass: "sapUiSizeCompact"
										});
									} else {
										MessageBox.error(oBundle.getText("deleteFailed"), {
											styleClass: "sapUiSizeCompact"
										});
									}
								})
								.fail(function (jqXHR, status, err) {
									that._oDocIssued.setBusy(false);
								});
						}
					}
				}
			});
		},

		/**
		 * Called to Navigate Invoice Management Screen on Click on SEZ link
		 * Developed by: Bharat Gupta - 15.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} taxDocType Tax Document Type
		 */
		onPressSezLink: function (taxDocType) {

			// var obj = oEvent.getSource().getBindingContext("ProcessSummary").getObject();
		},

		/**
		 * Called to change Document Issue dialog UI content when user select segment button option (i.e. Summary/Vetical)
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing Object
		 */
		onChangeSegmentDoc: function (oEvent) {

			if (oEvent.getSource().getSelectedKey() === "summary") {
				var flag = true;
			} else {
				flag = false;
			}
			this.getView().byId("tabSummaryDoc").setVisible(flag);
			this.getView().byId("tabVerticalDoc").setVisible(!flag);
		},

		/**
		 * Called when user press close button on dialog to close the opened dialog
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta - 04.01.2021 / 06.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view Popup view which triggered event
		 */
		onCloseDialog: function (view) {
			var oBundle = this.getResourceBundle();
			if (view === "b2cs") {
				var oAspData = this._oB2csSummary.getModel("AspVertical").getProperty("/verticalData"),
					oData = this._oB2csSummary.getModel("AspData").getProperty("/verticalData"),
					aFields = [
						"transType", "newPos", "newStateName", "newHsnOrSac", "newUom", "newQunty", "newRate",
						"newTaxableValue", "newEcomSupplValue", "igst", "cgst", "sgst", "cess", "totalValue"
					],
					flagB2CS = false;

				oAspData.forEach(function (item) {
					var obj = oData.find(function (el) {
						return el.sNo === item.sNo;
					});
					if (!obj) {
						flagB2CS = true;
					} else {
						aFields.forEach(function (f) {
							if (item[f] != obj[f]) {
								flagB2CS = true;
							}
						});
					}
				});
				if (flagB2CS) {
					sap.m.MessageBox.confirm(oBundle.getText("msgPopupPendingChange"), {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
						onClose: function (action) {
							if (action === sap.m.MessageBox.Action.YES) {
								this.onPressSaveAsp(view);
							} else {
								this._oB2csSummary.close();
							}
						}.bind(this)
					});
				} else {
					this._oB2csSummary.close();
				}
			} else if (view === "b2csa") {
				this._confirmPopupClose(this._oB2csaSummary, this._compareAspAndAdvanceObj(this._oB2csaSummary, view), view);

			} else if (view === "advances") {
				this._confirmPopupClose(this._oAdvances, this._compareAspAndAdvanceObj(this._oAdvances, view), view);

			} else if (view === "amendment") {
				this._confirmPopupClose(this._oAdvAmendment, this._compareAspAndAdvanceObj(this._oAdvAmendment, view), view);

			} else if (view === "nilExempt") {
				var obj1 = this._oNilExempt.getModel("NilExempt").getData().verticalData,
					flagNil = false;

				for (var i = 0; i < obj1.length; i++) {
					if (obj1[i].visi) {
						// this._setValueStateNone(obj1[i]);
						flagNil = true;
					}
				}
				if (flagNil) {
					var that = this;
					sap.m.MessageBox.confirm(oBundle.getText("msgPopupPendingChange"), {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
						onClose: function (action) {
							if (action === sap.m.MessageBox.Action.YES) {
								that.onPressSaveNilExempt();
							} else {
								that._oNilExempt.close();
							}
						}
					});
				} else {
					this._oNilExempt.close();
				}

			} else if (view === "hsnSummary") {
				this._confirmPopupClose(this._oHsnSummary, this._compareHsnObj(), "hsn");

			} else if (view === "docIssued") {
				if (this.byId("id_ChangeDocSegment").getSelectedKey() === "summary") {
					this._oDocIssued.close();
				} else {
					this._confirmPopupClose(this._oDocIssued, this._compareDocIssued(), view);
				}
				// this._oDocIssued.close();
			}
		},

		/**
		 * Method called to validate mandatory fields
		 * Developed by: Bharat Gupta - 05.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} obj Data object
		 * @param {string} view Popup view
		 * @return {boolean} Flag value
		 */
		_checkMandatory: function (obj, view) {
			var flag = true;
			switch (view) {
			case "b2cs":
				if (obj.newPos && obj.newHsnOrSac && obj.newRate && obj.newTaxableValue && obj.totalValue) {
					flag = false;
				}
				break;
			case "b2csa":
				if (obj.month && obj.orgPos && obj.orgRate && obj.newPos && obj.newHsnOrSac && obj.newRate && obj.newTaxableValue && obj.totalValue) {
					flag = false;
				}
				break;
			case "advances":
				if (obj.newPos && obj.newRate && obj.newTaxableValue) {
					flag = false;
				}
				break;
			case "amendment":
				if (obj.month && obj.orgPos && obj.orgRate && obj.orgTaxableValue && obj.newPos && obj.newRate && obj.newTaxableValue) {
					flag = false;
				}
				break;
			case "nil":
				if (obj.hsn && obj.uqc) {
					flag = false;
				}
				/*var aFields = ["hsn"];
				for (var f in obj) {
					if (aFields.includes(f)) {
						continue;
					}
					if (obj[f]) {
						flag = false;
					}
				}*/
				break;
			case "hsn":
				if (obj.hsn && obj.uqc && obj.taxRate) {
					flag = false;
				}
				break;
			case "docIssued":
				if (obj.seriesFrom && obj.seriesTo && obj.total && obj.cancelled) {
					flag = false;
				}
				break;
			default:
				flag = true;
			}
			return flag;
		},

		/**
		 * Method called to compare ASP vertical
		 * Developed by: Bharat Gupta - 07.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oDialog ASP Vertical object
		 * @param {string} view View type
		 * @return {boolean} Flag value
		 */
		_compareAspAndAdvanceObj: function (oDialog, view) {
			var flag = true;
			if (view === "b2cs") {
				var obj1 = oDialog.getModel("AspVertical").getProperty("/verticalData"),
					obj2 = oDialog.getModel("AspData").getProperty("/verticalData");
				for (var i = 0; i < obj1.length; i++) {
					if (obj1[i].visi) {
						// this._setValueStateNone(obj1[i]);
						// var flag 
						// flag = flag && !this.validateOutwardField(view, obj1[i]);
						flag = false;
					}
				}
			} else if (view === "b2csa") {
				var obj1 = oDialog.getModel("AspVertical").getProperty("/verticalData"),
					obj2 = oDialog.getModel("AspData").getProperty("/verticalData");

			} else {
				obj1 = oDialog.getModel("Advances").getProperty("/verticalData");
				obj2 = oDialog.getModel("AdvData").getProperty("/verticalData");

			}
			for (var i = 0, len = obj1.length; i < len; i++) {
				for (var j = 0, l = obj2.length; j < l; j++) {
					if (obj1[i].id === obj2[j].id) {
						break;
					}
				}
				if (j === l) {
					flag = flag && this._checkMandatory(obj1[i], view);
					continue;
				}
				flag = flag && this._validateDialogData(obj1[i], obj2[i], view);
			}
			return flag;
		},

		/**
		 * Method called to compare two array objects
		 * Developed by: Bharat Gupta - 04.01.2021
		 * Modified by: Bharat Gupta - 06.01.2021 / 07.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {string} view Dialog type
		 * @return {boolean} Flag
		 */
		_compareNilData: function (oDialog, view) {

			var obj1 = oDialog.getModel("NilExempt").getData().verticalData,
				obj2 = oDialog.getModel("ExemptData").getData().verticalData,
				flag = true;

			for (var i = 0; i < obj1.length; i++) {
				if (obj1[i].visi) {
					// this._setValueStateNone(obj1[i]);
					flag = false;
				}
			}

			// for (var i = 0, len = obj1.length; i < len; i++) {
			// 	for (var j = 0, l = obj2.length; j < l; j++) {
			// 		if (obj1[i].id === obj2[j].id) {
			// 			break;
			// 		}
			// 	}
			// 	if (j === l) {
			// 		flag = flag && this._checkMandatory(obj1[i], view);
			// 		continue;
			// 	}
			// 	flag = flag && this._validateDialogData(obj1[i], obj2[j], view);
			// }
			return flag;

		},

		/**
		 * Method called to compare two array objects
		 * Developed by: Bharat Gupta - 04.01.2021
		 * Modified by: Bharat Gupta - 06.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @return {boolean} Flag
		 */
		_compareHsnObj: function () {
			var obj1 = this._oHsnSummary.getModel("HsnSummary").getProperty("/verticalData"),
				obj2 = this._oHsnSummary.getModel("HsnData").getProperty("/verticalData"),
				lookup = {},
				flag = true;

			obj1.forEach(function (el, i) {
				var obj = obj2.find(function (e) {
					return (el.sNo === e.sNo); // el.docKey === e.docKey;
				});
				if (obj) {
					flag = flag && this._validateDialogData(el, obj, "hsn");
				} else {
					flag = flag && this._checkMandatory(el, "hsn");
				}
			}.bind(this));
			return flag;
		},

		/**
		 * Method called to compare Doc Issued
		 * Developed by: Bharat Gupta - 09.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @return {boolean} Flag
		 */
		_compareDocIssued: function () {

			var obj1 = this._oDocIssued.getModel("DocIssuedData").getData(),
				obj2 = this._oDocIssued.getModel("DocData").getData(),
				flag = true;

			for (var i = 0, l = obj1.length; i < l; i++) {
				for (var j = 0, len = obj2.length; j < len; j++) {
					if (obj1[i].id === obj2[j].id) {
						break;
					}
				}
				if (j === len) {
					flag = flag && this._checkMandatory(obj1[i], "docIssued");
					continue;
				}
				flag = flag && this._validateDialogData(obj1[i], obj2[j], "docIssued");
			}
			return flag;
		},

		/**
		 * Method called to validate Advance Popup value
		 * Developed by: Bharat Gupta - 07.01.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} obj1 Object 1
		 * @param {Object} obj2 Object 2
		 * @param {string} view Dialog type
		 * @return {boolean} Flag value
		 */
		_validateDialogData: function (obj1, obj2, view) {
			var flag = true;
			if (view === "b2cs") {
				var aFields = [
					"transType", "newPos", "newHsnOrSac", "newUom", "newQunty", "newRate", "newTaxableValue", "newEcomGstin", "newEcomSupplValue",
					"igst", "cgst", "sgst", "cess", "totalValue", "profitCntr", "plant", "division", "location", "salesOrg", "distrChannel",
					"usrAccess1", "usrAccess2", "usrAccess3", "usrAccess4", "usrAccess5", "usrAccess6", "usrDefined1", "usrDefined2",
					"usrDefined3"
				];
			} else if (view === "b2csa") {
				aFields = [
					"transType", "month", "orgPos", "orgHsnOrSac", "orgUom", "orgQunty", "orgRate", "orgTaxableValue", "orgEcomGstin",
					"orgEcomSupplValue", "newPos", "newHsnOrSac", "newUom", "newQunty", "newRate", "newTaxableValue", "newEcomGstin",
					"newEcomSupplValue", "igst", "cgst", "sgst", "cess", "totalValue", "profitCntr", "plant", "division", "location", "salesOrg",
					"distrChannel", "usrAccess1", "usrAccess2", "usrAccess3", "usrAccess4", "usrAccess5", "usrAccess6", "usrDefined1",
					"usrDefined2",
					"usrDefined3"
				];
			} else if (view === "advances") {
				aFields = [
					"transType", "newPos", "newRate", "newTaxableValue", "igst", "cgst", "sgst", "cess", "profitCntr", "plant", "division",
					"location", "salesOrg", "distrChannel", "usrAccess1", "usrAccess2", "usrAccess3", "usrAccess4", "usrAccess5", "usrAccess6",
					"usrDefined1", "usrDefined2", "usrDefined3"
				];
			} else if (view === "amendment") {
				aFields = [
					"transType", "month", "orgPos", "orgRate", "orgTaxableValue", "newPos", "newRate", "newTaxableValue",
					"igst", "cgst", "sgst", "cess", "profitCntr", "plant", "division", "location", "salesOrg", "distrChannel", "usrAccess1",
					"usrAccess2", "usrAccess3", "usrAccess4", "usrAccess5", "usrAccess6", "usrDefined1", "usrDefined2", "usrDefined3"
				];
			} else if (view === "nilExempt") {
				aFields = [
					"hsn", "desc", "uqc", "qunty", "nilInterReg", "nilIntraReg", "nilInterUnreg", "nilIntraUnreg", "extInterReg", "extIntraReg",
					"extInterUnreg", "extIntraUnreg", "nonInterReg", "nonIntraReg", "nonInterUnreg", "nonIntraUnreg"
				];
			} else if (view === "hsn") {
				aFields = ["uiDesc", "usrTaxableValue", "usrTotalValue", "usrQunty", "usrIgst", "usrCgst", "usrSgst", "usrCess"];

			} else if (view === "docIssued") {
				aFields = ["seriesFrom", "seriesTo", "total", "cancelled", "netIssued"];
			}

			aFields.forEach(function (f) {
				if (obj1[f] !== obj2[f]) {
					flag = false;
				}
			});
			// for (var i = 0; i < aFields.length; i++) {
			// 	if (obj1[aFields[i]] !== obj2[aFields[i]]) {
			// 		flag = false;
			// 	}
			// }
			return flag;
		},

		/**
		 * Method called to close HSN Summary
		 * Developed by: Bharat Gupta - 04.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oDialog Dialog object
		 * @param {boolean} flag Flag value
		 * @param {string} view Dialog type
		 */
		_confirmPopupClose: function (oDialog, flag, view) {
			if (flag) {
				oDialog.close();
			} else if (this.getView().getModel("buttonVis").getProperty("/dataEditable") === false) {
				oDialog.close();
			} else {
				sap.m.MessageBox.confirm(this.getResourceBundle().getText("msgPopupPendingChange"), {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					onClose: function (action) {
						if (action === sap.m.MessageBox.Action.YES) {
							switch (view) {
							case "b2cs":
								this.onPressSaveAsp(view);
							case "b2csa":
								this._saveB2csSummary(oDialog, view);
								break;
							case "advances":
							case "amendment":
								this._saveAdvance(oDialog, view);
								break;
							case "nilExempt":
								// this._saveNilData();
								this.onPressSaveNilExempt();
								break;
							case "hsn":
								this._saveHsnSummary();
								break;
							case "docIssued":
								this._saveDocIssued();
								break;
							}
						} else {
							oDialog.close();
						}
					}.bind(this)
				});
			}
		},

		/**
		 * Method called to save B2CS Summary
		 * Developed by: Bharat Gupta - 07.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oDialog Dialog object
		 * @param {string} view Dialog type
		 */
		_saveB2csSummary: function (oDialog, view) {

			var that = this,
				oAspData = oDialog.getModel("AspVertical").getData().verticalData,
				oPayload = {
					"req": []
				};

			for (var i = 0; i < oAspData.length; i++) {
				var flag = this.validateOutwardField(view, oAspData[i]);
				if (!flag && oAspData[i].visi) {
					oPayload.req.push(this._getAspVerticalSaveObj(view, oAspData[i]));
				}
			}
			oDialog.setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1VerticalUpdate.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				oDialog.setBusy(false);
				oDialog.close();
				var oSummPayload = {
					"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
				};
				that._getProcessSummary(oSummPayload, false);
			}).fail(function (jqXHR, status, err) {
				oDialog.setBusy(false);
			});
		},

		/**
		 * Method called to save pending Advance / Amendment changes
		 * Developed by: Bharat Gupta - 07.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oDialog Dialog object
		 * @param {string} view Dialog type
		 */
		_saveAdvance: function (oDialog, view) {

			var that = this,
				type = (view === "advances" ? "adv" : "amend"),
				oData = oDialog.getModel("Advances").getData().verticalData,
				oPayload = {
					"req": []
				};

			for (var i = 0; i < oData.length; i++) {
				var flag = this.validateAdvance(oDialog, oData[i]);
				if (!flag && oData[i].visi) {
					oPayload.req.push(this._getAdvanceObj(type, oDialog, oData[i]));
				}
			}
			oDialog.setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1AtTxpdVerticalUpdate.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				oDialog.setBusy(false);
				oDialog.close();
				var oSummPayload = {
					"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
				};
				that._getProcessSummary(oSummPayload, false);
			}).fail(function (jqXHR, status, err) {
				oDialog.setBusy(false);
			});
		},

		/**
		 * Method called to save Pending Nil Dialog changes
		 * Developed by: Bharat Gupta - 05.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_saveNilData: function () {

			var that = this,
				oPayNil = this._oNilExempt.getModel("Payload").getData(),
				oNilData = this._oNilExempt.getModel("NilExempt").getData(),
				oExtData = this._oNilExempt.getModel("ExemptData").getData(),
				oPayload = {
					"req": this._getNilSavePayload(oNilData.gstnView, oExtData.gstnView)
				};

			if (oPayload.req.length > 0) {
				var vUrl = "/aspsapapi/gstr1NilExmpNonGstSave.do";
			} else {
				vUrl = "/aspsapapi/gstr1NilExmpNonVerticalSave.do";
				oPayload.req = [];
				for (var i = 0, l = oNilData.verticalData.length; i < l; i++) {
					if (oNilData.verticalData[i].visi) {
						oPayload.req.push(this._getExemptSavePayload(oNilData.verticalData[i], oPayNil));
					}
				}
			}
			this._oNilExempt.setBusy(true);
			$.ajax({
					method: "POST",
					url: vUrl,
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oNilExempt.setBusy(false);
					if ((!!data.hdr && data.hdr.status === "S") || (!!data.resp && data.resp.status === "S")) {
						that._oNilExempt.close();
						var oSummPayload = {
							"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
						};
						that._getProcessSummary(oSummPayload, false);
					} else {
						sap.m.MessageBox.error(data.hdr.message, {
							"styleClass": "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					that._oNilExempt.setBusy(false);
				});
		},

		/**
		 * Method called to save HSN Summary popup data
		 * Developed by: Bharat Gupta - 05.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_saveHsnSummary: function () {

			var that = this,
				oHsnData = this._oHsnSummary.getModel("HsnSummary").getData().verticalData,
				oPayload = {
					"req": []
				};

			for (var i = 0; i < oHsnData.length; i++) {
				if (oHsnData[i].visi && !!oHsnData[i].hsn && !!oHsnData[i].uqc) {
					oPayload.req.push(this._getHsnSavePayload(oHsnData[i]));
				}
			}
			this._oHsnSummary.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1HsnUserUpdateData.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oHsnSummary.setBusy(false);
					that._oHsnSummary.close();
					var oSummPayload = {
						"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
					};
					that._getProcessSummary(oSummPayload, false);
				})
				.fail(function (jqXHR, status, err) {
					that._oHsnSummary.setBusy(false);
				});
		},

		/**
		 * Method called to save Doc Issued unsaved data
		 * Developed by: Bharat Gupta - 09.01.2021
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_saveDocIssued: function () {

			var that = this,
				docPayload = this._oDocIssued.getModel("Payload").getData(),
				oDocData = this._oDocIssued.getModel("DocIssuedData").getData(),
				oPayload = {
					"req": []
				};
			for (var i = 0, l = oDocData.length; i < l; i++) {
				if (oDocData[i].visi) {
					oPayload.req.push({
						"sNo": oDocData[i].sNo,
						"id": oDocData[i].id || null,
						"userSNo": oDocData[i].sNo,
						"sgstin": docPayload.dataSecAttrs.GSTIN[0],
						"retPeriod": docPayload.taxPeriod,
						"docNatureId": oDocData[i].docNatureId,
						"seriesFrom": oDocData[i].seriesFrom,
						"seriesTo": oDocData[i].seriesTo,
						"total": oDocData[i].total,
						"cancelled": oDocData[i].cancelled,
						"netIssued": oDocData[i].netIssued
					});
				}
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/docseriesSaveAndEdit.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					that._oDocIssued.setBusy(false);
					that._oDocIssued.close();
					var oSummPayload = {
						"req": that.byId("dpGstr1Summary").getModel("SummaryPayload").getData()
					};
					that._getProcessSummary(oSummPayload, false);
				})
				.fail(function (jqXHR, status, err) {
					that._oDocIssued.setBusy(false);
				});
		},

		/**
		 * Called when user click on Save to GSTN button 
		 * Developed by: Bharat Gupta - 11.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSaveGstn: function (oEvent) {

			var oPayload = {
				"hdr": {
					"pageNo": "",
					"pageSize": ""
				},
				"req": {
					"gstins": [],
					"retPeriod": null,
					"isNilUserInput": false,
					"isHsnUserInput": false
				}
			};
			if (oEvent.getSource().getId().includes("bSaveToGstn")) {
				var vIndices = this.byId("tabGstr1Process").getSelectedIndices(),
					aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData(),
					vPeriod = this.byId("dtProcessed").getValue();
				if (vIndices.length === 0) {
					sap.m.MessageBox.alert("Please select atleast one record", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.retPeriod = vPeriod;
				for (var i = 0; i < vIndices.length; i++) {
					oPayload.req.gstins.push(aData[vIndices[i]].gstin);
				}
			} else if (oEvent.getSource().getId().includes("bSaveGstn")) {
				var aGstin = this.byId("slSummaryGstin").getSelectedKey();
				oPayload.req.gstins.push(aGstin);
				oPayload.req.retPeriod = this.byId("dtSummary").getValue();
			}
			// this._saveConfirmMsg(oPayload);
		},

		/**
		 * Method to Show Message box to get confirmation from user
		 * Developed by: Bharat Gupta - 11.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_saveConfirmMsg: function (oPayload) {
			var oBundle = this.getResourceBundle(),
				that = this;
			sap.m.MessageBox.confirm(oBundle.getText("msgSaveConfirm"), {
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						that._saveToGstn(oPayload);
					}
				}
			});
		},

		/**
		 * Method to trigger Background Job to save data in GSTN
		 * Developed by: Bharat Gupta - 11.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_saveToGstn: function (oPayload) {

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SaveToGstnJob.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._msgSaveToGstn(data);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		/**
		 * Method called to close Save To Gstn Popup
		 * Developed by: Bharat Gupta - 21.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onCloseSaveToGstnStats: function () {
			this._saveToGstnStats.close();
		},

		/**
		 * Method called to Show Response Gstin and msg as popup
		 * Developed by: Bharat Gupta - 21.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Response Object
		 */
		_msgSaveToGstn: function (data) {

			if (!this._saveToGstnStats) {
				this._saveToGstnStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.SaveToGstnStats", this);
				this.getView().addDependent(this._saveToGstnStats);
			}
			this.byId("dSaveToGstnStats").setModel(new JSONModel(data), "MsgSaveToGstn");
			this._saveToGstnStats.open();
		},

		/**
		 * Called to trigger Approval Request to get Approval
		 * Developed by: Bharat Gupta - 10.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressApprovalRequest: function (oEvent) {

			var that = this;
			if (oEvent.getSource().getId().includes("bApprRequest")) {
				var aGstin = this.byId("slSummaryGstin").getSelectedKey(),
					oPayload = {
						"req": {
							"entityId": $.sap.entityID,
							"gstins": [aGstin],
							"returnPeriod": this.byId("dtSummary").getValue()
						}
					};
			} else if (oEvent.getSource().getId().includes("bApprovalRequest")) {
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [],
						"returnPeriod": this.byId("dtProcessed").getValue()
					}
				};
				var aIndices = this.byId("tabGstr1Process").getSelectedIndices(),
					aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
				if (aIndices.length === 0) {
					sap.m.MessageBox.alert("Please select atleast one record", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				for (var i = 0; i < aIndices.length; i++) {
					oPayload.req.gstins.push(aData[aIndices[i]].gstin);
				}
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1RequestApproval.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						sap.m.MessageBox.success("Request sent for Approval", {
							styleClass: "sapUiSizeCompact"
						});
						var oModel = that.byId("dpGstr1Summary").getModel("ApprovalStatus"),
							oData = {
								"status": -3,
								"timeStamp": data.resp.createdDate
							};
						if (oModel) {
							oModel.setData(oData);
							oModel.refresh(true);
						} else {
							that.byId("dpGstr1Summary").setModel(new JSONModel(oData), "ApprovalStatus");
						}
						// that.byId("txtReqSent").setText("Sent: " + data.resp.createdDate);
					} else {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		/**
		 * Called to navigate to Invoice Management screen for Error Correction
		 * Developed by: Bharat Gupta - 11.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} view View type ('P','S')
		 */
		onPressErrorCorrection: function (view) {

			var oFilterData = {
				"req": {
					"dataType": "outward",
					"criteria": "taxPeriod",
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"navType": "GSTR1",
					"dataOriginType": null,
					"refId": null,
					"showGstnData": false,
					"type": "E",
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

			if (view === "P") {
				var oPayload = this.byId("dpProcessRecord").getModel("ProcessPayload").getData();
			} else {
				oPayload = this.byId("dpGstr1Summary").getModel("SummaryPayload").getData();
			}
			oFilterData.req.taxPeriodFrom = oPayload.taxPeriod;
			oFilterData.req.taxPeriodTo = oPayload.taxPeriod;
			oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvManageNew");
		},

		/**
		 * Called to download report in Excel file for selected type
		 * Developed by: Bharat Gupta - 11.12.2019
		 * Modified by: Bharat Gupta - 09.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent	 Eventing object
		 * @param {Object} parameter Paremeter object
		 * @param {string} view 	 View type
		 */
		onCloseTaxLiability: function () {
			this.TaxLiability.close();
		},
		onSaveTaxLiability: function (oEvent) {

			var oBundle = this.getResourceBundle();
			var selGSTIN;
			if (this.type === "P") {
				selGSTIN = [];
				var oData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr1Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}

				for (var i = 0; i < aIndex.length; i++) {
					selGSTIN.push(oData[aIndex[i]].gstin);
				}
			} else {
				selGSTIN = [this.byId("slSummaryGstin").getSelectedKey()];
			}
			var Request = {
				"req": {
					"entityId": [$.sap.entityID],
					"gstin": selGSTIN,
					"fromTaxPeriod": this.byId("DP1").getValue(),
					"toTaxPeriod": this.byId("DP2").getValue()
				}
			};

			var vUrl = "/aspsapapi/gmrOutwardSummaryDownloadReport.do";
			this.excelDownload(Request, vUrl);
			this.TaxLiability.close();

		},

		onPressDownloadReport: function (oEvent, parameter, view) {
			this.type = view;
			var selectedKey = parameter.getKey(),
				aReportType = ["asUploaded", "aspError", "gstnError", "shipBillDetail"],
				aReportTypeFilter = [
					"nilRateSavable", "processedSaveable", "b2csSavable", "hsnSummary", "asUploaded", "aspError", "gstnError",
					"shipBillDetail", "RateLevelData", "ConsolidatedHSNSummary"
				],
				oPayload = {
					"req": {
						"dataType": "Outward",
						"entityId": [$.sap.entityID],
						"type": oEvent.getParameter("item").getKey(),
						"reportCateg": (view === "P" ? "ProcessedSummary" : "ReviewSummary"),
						"dataSecAttrs": {
							"GSTIN": [],
						}
					}
				};

			if (view === "P") {
				var oData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr1Process").getSelectedIndices();

				if (!aIndex.length) {
					sap.m.MessageBox.information(this.getResourceBundle().getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();

				if (aReportTypeFilter.includes(selectedKey)) {
					var aTableType = this.byId("idPTableType").getSelectedKeys(),
						aDocTypee = this.byId("idPDocType").getSelectedKeys();

					oPayload.req.tableType = aTableType.includes("All") ? [] : aTableType;
					oPayload.req.docType = aDocTypee.includes("All") ? [] : aDocTypee;
				}

				aIndex.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[e].gstin);
				});
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
					if (aReportTypeFilter.includes(selectedKey)) {
						var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys(),
							EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

						oPayload.req.docDateFrom = (this.getView().byId("idocFromDate").getValue() || null);
						oPayload.req.docDateTo = (this.getView().byId("idocToDate").getValue() || null);
						oPayload.req.eInvGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
						oPayload.req.eWbGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;
					}
				}
			} else {
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
				oPayload.req.dataSecAttrs.GSTIN.push(this.byId("slSummaryGstin").getSelectedKey());
				if (aReportType.includes(selectedKey)) {
					oPayload.req.tableType = oPayload.req.docType = [];
				}

				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
					if (aReportTypeFilter.includes(selectedKey)) {
						var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys(),
							EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

						oPayload.req.docDateFrom = (this.getView().byId("idocFromDate").getValue() || null);
						oPayload.req.docDateTo = (this.getView().byId("idocToDate").getValue() || null);
						oPayload.req.eInvGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
						oPayload.req.eWbGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;
					}
				}
			}

			if ((view === "P" && aReportType.includes(selectedKey) && selectedKey !== "asUploaded") ||
				(view !== "P" && aReportType.includes(selectedKey))) {
				if (selectedKey === "shipBillDetail") {
					oPayload.req.type = "Shipping Bill";
					//	oPayload.req.reportCateg = "InvoiceManagement";
				}
				this.reportDownload(oPayload, "/aspsapapi/downloadProcessedCsvReports.do");

			} else if (selectedKey === "entityLevel") {
				if (!this._dateFilter) {
					this._dateFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.dateFilter", this);
					this.getView().addDependent(this._dateFilter);
				}
				var vKey = this.getView().getModel("AllFy").getProperty("/finYears/0/key"),
					vDate = new Date(),
					vPeriod = new Date(vKey, 3, 1);

				this.byId("idFYELS").setSelectedKey(vKey);
				this._setDateProperty("idFromPeriodELS", vPeriod, vDate, vPeriod);
				this._setDateProperty("idToPeriodELS", vDate, vDate, vPeriod);

				this._dateFilter.setModel(new JSONModel(oPayload), "RptPayload");
				this._dateFilter.open();

			} else if (selectedKey === "gstnError") {
				this.excelDownload(oPayload, "/aspsapapi/consolidatedGstnErrorReports.do");

			} else if (selectedKey === "stockTransferRepo") {
				oPayload.req.type = "Stock Transfer";
				oPayload.req.reportCateg = "Stock Transfer";
				oPayload.req.tableType = oPayload.req.docType = [];
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();
				this.reportDownload(oPayload, "/aspsapapi/downloadProcessedCsvReports.do");

			} else if (selectedKey === "taxLiabilitysumrepo") {
				if (!this.TaxLiability) {
					this.TaxLiability = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.TaxLiabilitySummaryreportF4",
						this);
					this.getView().addDependent(this.TaxLiability);
				}
				var vDate = new Date();
				this._setDateProperty("DP1", vDate, vDate);
				this._setDateProperty("DP2", vDate, vDate);
				this.TaxLiability.open();

			} else if (selectedKey === "shipBillDetail") {
				oPayload.req.type = "Shipping Bill";
				oPayload.req.reportCateg = "ProcessedSummary";
				this.reportDownload(oPayload, "/aspsapapi/downloadProcessedCsvReports.do");

			} else if (selectedKey === "RateLevelData") {
				oPayload.req.type = "RateLevelReport";
				this.reportDownload(oPayload, "/aspsapapi/downloadProcessedCsvReports.do");

			} else if (selectedKey === "ConsolidatedHSNSummary") {
				oPayload.req.type = "VerticleUploadHSN";
				oPayload.req.reportCateg = "GSTR1";
				this.reportDownload(oPayload, "/aspsapapi/hsnSummaryReport.do");

			} else if (selectedKey === "asUploaded") {
				if (!this._dateFilter) {
					this._dateFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.dateFilter", this);
					this.getView().addDependent(this._dateFilter);
				}
				var vKey = this.getView().getModel("AllFy").getProperty("/finYears/0/key"),
					vDate = new Date(),
					vPeriod = new Date(vKey, 3, 1);

				this.byId("idFYELS").setSelectedKey(vKey);
				this._setDateProperty("idFromPeriodELS", vDate, vDate, vPeriod);
				this._setDateProperty("idToPeriodELS", vDate, vDate, vDate);

				this._dateFilter.setModel(new JSONModel(oPayload), "RptPayload");
				this._dateFilter.open();

			} else {
				this.excelDownload(oPayload, "/aspsapapi/downloadGstr1RSReports.do");
			}
		},

		onPressDateFilterClose: function (flag) {
			if (flag === "Apply") {
				var payload = this._dateFilter.getModel("RptPayload").getProperty("/");
				payload.req.taxPeriodFrom = this.byId("idFromPeriodELS").getValue();
				payload.req.taxPeriodTo = this.byId("idToPeriodELS").getValue();
				this.reportDownload(payload, "/aspsapapi/downloadProcessedCsvReports.do");
				this._dateFilter.close();
			} else {
				this._dateFilter.close();
			}
		},

		handleChangeDatePeriod: function (oevt) {
			var toDate = this.byId("idToPeriodELS").getDateValue(),
				fromDate = this.byId("idFromPeriodELS").getDateValue();

			if (fromDate > toDate) {
				this.byId("idToPeriodELS").setDateValue(oevt.getSource().getDateValue());
			}
			this.byId("idToPeriodELS").setMinDate(oevt.getSource().getDateValue());
		},

		handleChange: function (oevt) {
			var toDate = this.byId("DP2").getDateValue(),
				fromDate = this.byId("DP1").getDateValue();
			if (fromDate > toDate) {
				this.byId("DP2").setDateValue(oevt.getSource().getDateValue());
			}
			this.byId("DP2").setMinDate(oevt.getSource().getDateValue());
		},

		/**
		 * Developed by: Bharat Gupta - 19.03.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {stritn} button Screen Level Download button identifier
		 */
		onExportExcel: function (button) {

			if (button === "P") {
				var vType = "processRecScreen",
					vTaxPeriod = "dtProcessed",
					vAdaptFilter = "dpProcessRecord";
				var aGstin = this.byId("slGstin").getSelectedKeys();
				var aTableType = this.byId("idPTableType").getSelectedKeys(),
					aDocTypee = this.byId("idPDocType").getSelectedKeys();

				var oReport = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();

				var vUrl = "/aspsapapi/downloadGstr1RSReports.do",
					oPayload = {
						"req": {
							"dataType": "outward",
							"type": vType,
							"entityId": [$.sap.entityID],
							"taxPeriod": this.byId(vTaxPeriod).getValue(),
							"report": oReport,
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : aGstin
							}
						}
					};

			} else {
				vType = "gstr1ReviewSummary";
				vTaxPeriod = "dtSummary";
				vAdaptFilter = "dpGstr1Summary";
				aGstin = [this.byId("slSummaryGstin").getSelectedKey()];
				var aTableType = [],
					aDocTypee = [];

				var vUrl = "/aspsapapi/downloadGstr1RSReports.do",
					oPayload = {
						"req": {
							"dataType": "outward",
							"type": vType,
							"entityId": [$.sap.entityID],
							"taxPeriod": this.byId(vTaxPeriod).getValue(),
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : aGstin
							}
						}
					};
			}

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, vAdaptFilter);
				var docFromDate = this.getView().byId("idocFromDate").getValue();
				if (docFromDate === "") {
					docFromDate = null;
				}
				var docToDate = this.getView().byId("idocToDate").getValue();
				if (docToDate === "") {
					docToDate = null;
				}

				var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys();
				var EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

				oPayload.req.docFromDate = docFromDate;
				oPayload.req.docToDate = docToDate;
				oPayload.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				oPayload.req.EWBGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;
			}
			this.excelDownload(oPayload, vUrl);
		},

		onDownloadEnityPdf: function (flag) {
			var aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
				aIndex = this.byId("tabGstr1Process").getSelectedIndices();

			if (!aIndex.length) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			var aGstin = aIndex.map(function (e) {
				return aData[e].gstin;
			});
			var oPayload = {
				"req": {
					// "dataType": "outward",
					// "type": "processRecScreen",
					"isDigigst": flag,
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
				var docFromDate = this.getView().byId("idocFromDate").getValue() || null,
					docToDate = this.getView().byId("idocToDate").getValue() || null,
					EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys(),
					EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

				oPayload.req.docFromDate = docFromDate;
				oPayload.req.docToDate = docToDate;
				oPayload.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				oPayload.req.EWBGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;
			}
			MessageBox.confirm("Do you want to print the below verification message on the PDF generated? \n" +
				"I hereby solemnly affirm and declare that the information given herein above is true" +
				" and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.req.isVerified = sAction;
						this.reportDownload(oPayload, "/aspsapapi/Gstr1EntityLevelSummaryPdfReports.do");
					}.bind(this)
				});
		},

		onDownloadGstnPdf: function (flag) {
			var oPayload = {
				"req": {
					// "dataType": "outward",
					// "type": "gstr1ReviewSummary",
					"isDigigst": flag,
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("dtSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("slSummaryGstin").getSelectedKey()]
					}
				}
			};

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				var docFromDate = this.getView().byId("idocFromDate").getValue() || null,
					docToDate = this.getView().byId("idocToDate").getValue() || null,
					EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys(),
					EWBGenerated = this.getView().byId("iEWBGenerated").getSelectedKeys();

				oPayload.req.docFromDate = docFromDate;
				oPayload.req.docToDate = docToDate;
				oPayload.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				oPayload.req.EWBGenerated = EWBGenerated.includes("All") ? [] : EWBGenerated;
			}
			MessageBox.confirm("Do you want to print the below verification message on the PDF generated? \n" +
				"I hereby solemnly affirm and declare that the information given herein above is true" +
				" and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.req.isVerified = sAction;
						this.excelDownload(oPayload, "/aspsapapi/generateGstr1SummaryPdfReport.do");
					}.bind(this)
				});
		},

		/**
		 * Developed by: Bharat Gupta - 12.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param  {stritn} value Value to be get Key value
		 * @return {string} vKey
		 */
		_tableNumber: function (value) {
			switch (value) {
			case "EXPORTS":
				var vKey = "EXP";
				break;
			case "EXPORTS-A":
				vKey = "EXPA";
				break;
			case "NILEXTNON":
				vKey = "NIL";
				break;
			case "ADV REC":
				vKey = "AT";
				break;
			case "ADV ADJ":
				vKey = "TXPD";
				break;
			case "ADV REC-A":
				vKey = "ATA";
				break;
			case "ADV ADJ-A":
				vKey = "TXPDA";
				break;
			case "DOC ISSUED":
				vKey = "DOC";
				break;
			default:
				vKey = value;
				break;
			}
			return vKey;
		},

		/**
		 * Called when user change amount format to Absolute, Lakhs, Crores, Millions, Billions
		 * Developed by: Bharat Gupta - 09.01.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onDisplayRupeesValue: function (oEvent) {

			var vKey = oEvent.getParameter("item").getKey(),
				oBundle = this.getResourceBundle(),
				oModel = this.byId("dpProcessRecord").getModel("ProcessedModel");

			switch (vKey) {
			case "A":
				var div = 1;
				break;
			case "L":
				div = 100000;
				break;
			case "C":
				div = 10000000;
				break;
			case "M":
				div = 1000000;
				break;
			case "B":
				div = 1000000000;
				break;
			}
			this.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(vKey));
			if (oModel) {
				var oData = oModel.getData(),
					aData = [];
				for (var i = 0; i < oData.length; i++) {
					var data = {};
					data.state = oData[i].state;
					data.gstin = oData[i].gstin;
					data.regType = oData[i].regType;
					data.authToken = oData[i].authToken;
					data.status = oData[i].status;
					data.timeStamp = oData[i].timeStamp;
					data.count = oData[i].count;
					data.supplies = this.amt2decimal(oData[i].supplies / div);
					data.igst = this.amt2decimal(oData[i].igst / div);
					data.cgst = this.amt2decimal(oData[i].cgst / div);
					data.sgst = this.amt2decimal(oData[i].sgst / div);
					data.cess = this.amt2decimal(oData[i].cess / div);
					aData.push(data);
				}
				this.byId("tabGstr1Process").setModel(new JSONModel(aData), "ProcessedRecord");
			}
		},

		onFetchGstnData: function () {
			var payload = {
				"req": {
					"action": "UPDATEGSTIN",
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("dtSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("slSummaryGstin").getSelectedKey()]
					}
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/fetchGstr1GetSummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXhr) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Called to update Gstn Data from sandbox
		 * Developed by: Bharat Gupta - 25.01.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} view   View type ('P','S')
		 */
		onUpdateGstnData: function (oEvent, view) {

			var that = this,
				oBundle = that.getResourceBundle(),
				oPayload = {
					"req": {
						"action": "UPDATEGSTIN",
						"entityId": [$.sap.entityID],
						"taxPeriod": null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (view === "D") {
				var oData = this.byId("dDifferenceGstr1").getModel("Payload").getData();
				oPayload.req.taxPeriod = oData.taxPeriod;
				oPayload.req.dataSecAttrs = oData.dataSecAttrs;
			} else if (view === "S") {
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
				oPayload.req.dataSecAttrs.GSTIN = [this.byId("slSummaryGstin").getSelectedKey()];
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				}
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmUpdate"), {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr1GetSummary.do",
								contentType: "application/json",
								data: JSON.stringify(oPayload)
							})
							.done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									// var oSaveGstn = that.byId("dpGstr1Summary").getModel("SaveGstnStats").getData();

									if (!!that._oDialogDifference && that._oDialogDifference.isOpen()) {
										sap.m.MessageBox.success(data.hdr.message, {
											styleClass: "sapUiSizeCompact"
										});
										//that.byId("tDiffUpdate").setText(data.resp.lastUpdatedDate ? ("Last Updated: " + data.resp.lastUpdatedDate) : null);
										that._getDifferenceData();
									} else {
										sap.m.MessageBox.success(data.hdr.message, {
											styleClass: "sapUiSizeCompact"
										});
										/*that._bindSummaryData(data);
										that._bindNilExemptData(data.resp, data.resp.lastUpdatedNilTime);
										that._bindHsnData(data.resp, data.resp.lastUpdatedHsnTime);
										data.resp.updateFlag = data.resp.lastUpdatedDate ? true : false;*/
										//that.byId("dpGstr1Summary").setModel(new JSONModel(data.resp), "ProcessSummary");
										// that.byId("tSummUpdate").setText(data.resp.lastUpdatedDate ? ("Last Updated: " + that.getTimeStamp(data.resp.lastUpdatedDate)) :
										// 	null);
									}
									that._getSummaryData();
									/*sap.m.MessageBox.success(oBundle.getText("msgUpdateData"), {
										styleClass: "sapUiSizeCompact"
									});*/
								} else {
									sap.m.MessageBox.error(data.hdr.message, {
										styleClass: "sapUiSizeCompact"
									});
								}
							})
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							});
					}
				}
			});
		},

		/**
		 * Method called to Generate OTP confirmation popup
		 * Developed by: Bharat Gupta - 21.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} gstin	 Gstin
		 * @Param {string} authToken Auth Token
		 */
		onActivateAuthToken: function (gstin, authToken) {

			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		/**
		 * Method called to Refresh Data after activation of Auth Token
		 * Developed by: Bharat Gupta - 21.04.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		refreshData: function () {
			this._getProcessedData();
		},

		/**
		 * Method called to Download Excel Report
		 * Developed by: Bharat Gupta - 11.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		onPressDowloadExcel: function () {

			var vUrl = "/aspsapapi/downloadGstr1RSReports.do",
				// aGstin = this.byId("slSummaryGstin").getSelectedKeys(),
				oPayload = {
					"req": {
						"dataType": "outward",
						"type": "gstr1ReviewSummary",
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": [this.byId("slSummaryGstin").getSelectedKey()] //aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
			}
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Method called to Download Consolidated GSTN Error Report in Excel format
		 * Developed by: Bharat Gupta - 11.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} oEvent	Eventing object
		 * @param {string} errType	Error Type
		 * @param {string} refId	Reference ID
		 */
		onDownloadConsGstnErrors: function (oEvent, errType, refId) {

			if (errType === "aspError") {
				var vUrl = "/aspsapapi/consoidateGstnErrorReports.do",
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
							"taxPeriod": this.byId("dtSaveStats").getValue(),
							"gstnRefId": refId
						}
					};
				this.excelDownload(oPayload, vUrl);
			} else if (errType === "gstnError") {
				var vUrl = "/aspsapapi/downloadGstr1RSReports.do";
				oPayload = {
					"req": {
						"dataType": "outward",
						"entityId": [$.sap.entityID],
						"type": null,
						"taxPeriod": null,
						"gstnRefId": refId,
						"dataSecAttrs": {}
					}
				};
				// var aGstin = this.byId("slSaveStatsGstin").getSelectedKey();
				oPayload.req.type = "gstnError";
				oPayload.req.taxPeriod = this.byId("dtSaveStats").getValue();
				oPayload.req.dataSecAttrs.GSTIN = [this.byId("slSaveStatsGstin").getSelectedKey()]; //aGstin.includes("All") ? [] : aGstin;
				this.excelDownload(oPayload, vUrl);
			} else {
				var url = "/aspsapapi/downloadGstr1GetSumErrResp.do?refId=" + refId;
				window.open(url);
			}

		},
		// Code started  by sarvmangla.
		onDownloadConsGstnErrorsDelete: function (oEvent, errType, refId) {

			// if (errType === "aspError") {
			var vUrl = "/aspsapapi/downloadGstr1GstnErrorReports.do",
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "gstnError",
						"taxPeriod": this.byId("dtSaveStats").getValue(),
						"refId": refId,
						"dataSecAttrs": {
							"GSTIN": [this.byId("slSaveStatsGstin").getSelectedKey()]
						}

						// "entityId": [$.sap.entityID],
						// "gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
						// "taxPeriod": this.byId("dtSaveStats").getValue(),
						// "gstnRefId": refId
					}
				};
			// } else {
			// 	vUrl = "/aspsapapi/downloadGstr1RSReports.do";
			// 	oPayload = {
			// 		"req": {
			// 			"dataType": "outward",
			// 			"entityId": [$.sap.entityID],
			// 			"type": null,
			// 			"taxPeriod": null,
			// 			"gstnRefId": refId,
			// 			"dataSecAttrs": {}
			// 		}
			// 	};
			// 	// var aGstin = this.byId("slSaveStatsGstin").getSelectedKey();
			// 	oPayload.req.type = "gstnError";
			// 	oPayload.req.taxPeriod = this.byId("dtSaveStats").getValue();
			// 	oPayload.req.dataSecAttrs.GSTIN = [this.byId("slSaveStatsGstin").getSelectedKey()]; //aGstin.includes("All") ? [] : aGstin;
			// }
			this.excelDownload(oPayload, vUrl);
		},
		// Code ended  by sarvmangla.

		onDownloadGS: function (oEvent, errType, refId) {
			var url = "/aspsapapi/downloadGstr1GetSumErrResp.do?refId=" + refId;
			window.open(url);
		},

		/**
		 * Method called for final Submition of GSTN data to GSTN portal
		 * Developed by: Bharat Gupta - 12.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {stritn} view View 
		 */
		onPressSubmit: function (view) {

			var that = this,
				oBundle = this.getResourceBundle(),
				oPayload = {
					"req": []
				};
			if (view === "P") {
				var vIndices = this.byId("tabGstr1Process").getSelectedIndices(),
					aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData(),
					vPeriod = this.byId("dtProcessed").getValue();
				if (vIndices.length === 0) {
					sap.m.MessageBox.information("Please select atleast one record", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				for (var i = 0; i < vIndices.length; i++) {
					oPayload.req.push({
						"gstin": aData[vIndices[i]].gstin,
						"ret_period": vPeriod
					});
				}
			} else {
				var aGstin = this.byId("slSummaryGstin").getSelectedKey();
				oPayload.req.push({
					"gstin": aGstin,
					"ret_period": this.byId("dtSummary").getValue()
				});
			}
			sap.m.MessageBox.information(oBundle.getText("msgSubmitInfo"), {
				styleClass: "sapUiSizeCompact",
				actions: ["Submit", sap.m.MessageBox.Action.CANCEL],
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (oAction) {
					if (oAction === "Submit") {
						sap.m.MessageBox.confirm(oBundle.getText("msgSubmitConfirm"), {
							styleClass: "sapUiSizeCompact",
							actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
							initialFocus: sap.m.MessageBox.Action.NO,
							onClose: function (action) {
								if (action === sap.m.MessageBox.Action.YES) {
									that._submitGstn(oPayload);
								}
							}
						});
					}
				}
			});
		},

		/**
		 * Method called to submit data to GSTN
		 * Developed by: Bharat Gupta - 12.05.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} payload Request Payload
		 */
		_submitGstn: function (payload) {

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr1GstnSubmit.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._msgSaveToGstn(data);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		/**
		 * Method called to download jnlp file for Sign-N-File
		 * Developed by: Bharat Gupta - 08.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} view View which triggered event
		 */
		onPressSignNFile: function (view) {

			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": {
						"gstin": null,
						"taxPeriod": null
					}
				};
			if (view === "P") {
				var oProcessPayload = this.byId("dpProcessRecord").getModel("ProcessPayload").getData(),
					oData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr1Process").getSelectedIndices();
				if (aIndex.length !== 1) {
					sap.m.MessageBox.information(oBundle.getText("msgOneGstinOnly"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.gstin = oData[aIndex[0]].gstin;
				oPayload.req.taxPeriod = oProcessPayload.taxPeriod;
			} else {
				oPayload.req.gstin = this.byId("slSummaryGstin").getSelectedKey();
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
			}
			this.excelDownload(oPayload, "/aspsapapi/execSignAndFileStage1.do");
		},

		/**
		 * Method called to format Document Issued Header
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {number} natureId Document Nature Id
		 * @return {string} Document Text
		 */
		docIssuedHeader: function (natureId) {

			var oDocModel = this._oDocIssued.getModel("DocIssuedSummary"),
				vDocNature = (typeof (natureId) !== "number" ? parseInt(natureId, 10) : natureId),
				vText = null;
			if (oDocModel) {
				var oDocIssued = oDocModel.getData();
				for (var i = 0; i < oDocIssued.length; i++) {
					if (oDocIssued[i].docNatureId === vDocNature) {
						vText = oDocIssued[i].docNature;
						break;
					}
				}
			}
			return vText;
		},

		/**
		 * Method called to set Value state to Default
		 * Developed by: Bharat Gupta - 19.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} data Data object
		 */
		_setValueStateNone: function (data) {
			for (var field in data) {
				if (field.endsWith("State")) {
					data[field] = "None";
				}
			}
		},

		/**
		 * Developed by: Bharat Gupta - 17.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} obj Period
		 * @return {date}  Date object
		 */
		getMaxDate: function (obj) {

			return (new Date(obj.substr(2), obj.substr(0, 2), 0));
		},

		/**
		 * Method called to format DateTimeStamp to DD-MM-YYYY HH:MM:SS
		 * Developed by: Bharat Gupta - 07.12.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {string} date Date value to format
		 * @return {string} Formatted date value
		 */
		getTimeStamp: function (date) {

			var vDate = "";
			if (date) {
				vDate += date.substr(8, 2) + "-" + date.substr(5, 2) + "-" + date.substr(0, 4) + date.substr(10);
			}
			return vDate;
		},

		/**
		 * Developed by: Bharat Gupta - 18.10.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {stritn}  value Value to be formatted
		 * @return {string} vDocTypeText
		 */
		formatDocType: function (value) {
			var oBundle = this.getResourceBundle();
			if (value) {
				var vKey = this._tableNumber(value),
					vDocTypeText = oBundle.getText(vKey);
				return vDocTypeText;
			}
			return value;
		},

		/**
		 * Method called to format Short period in MMyyyy format
		 * Developed by: Bharat Gupta - 2_.06.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {stritn}  period Period in MMM yyyy
		 * @return {string} Formatted period in MMyyyy
		 */
		aspPeriod: function (period) {

			var vDate = new Date(Formatter.periodFormat(period));
			vDate.setMonth(vDate.getMonth() - 1);
			var m = vDate.getMonth() + 1;
			return (m < 10 ? "0" + m : m).toString() + vDate.getFullYear();
		},

		/**
		 * Called to format amount value
		 * Developed by: Bharat Gupta - 12.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string}  vAmount Amount to convert
		 * @return {number} amount
		 */
		amt2decimal: function (vAmount) {
			var amount = vAmount.toString();
			if (amount.indexOf(".") > -1 && amount.indexOf("e-") === -1) {
				return amount.substring(0, amount.indexOf(".") + 3);
			} else if (amount.indexOf("e-") > -1) {
				return 0;
			}
			return vAmount;
		},

		/**
		 * Method called to get Approval Status Text
		 * Developed by: Bharat Gupta - 12.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {number}  status Approval Status
		 * @return {string} Approval Text
		 */
		apprStatusText: function (status) {

			var oBundle = this.getResourceBundle();
			switch (status) {
			case -3:
				var vStatus = oBundle.getText("sent");
				break;
			case 0:
				vStatus = oBundle.getText("approvePending");
				break;
			case 1:
				vStatus = oBundle.getText("approved");
				break;
			case 2:
				vStatus = oBundle.getText("rejected");
				break;
			default:
				vStatus = null;
			}
			return (!vStatus ? null : vStatus + ":");
		},

		/**
		 * Method called to get Approval Status Color
		 * Developed by: Bharat Gupta - 12.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {number}  status Approval Status
		 * @return {string} Status Type
		 */
		apprStatusColor: function (status) {

			switch (status) {
			case -3:
				var vStatus = "sent";
				break;
			case 0:
				vStatus = "pending";
				break;
			case 1:
				vStatus = "accept";
				break;
			case 2:
				vStatus = "reject";
				break;
			default:
				vStatus = "none";
			}
			return vStatus;
		},

		/**
		 * Method called to format TimeStamp date
		 * Developed by: Bharat Gupta - 12.07.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {string} timeStmp Time Stamp
		 * @return {string} Formatted Time Stamp
		 */
		apprStatusTime: function (timeStmp) {

			if (timeStmp && timeStmp.indexOf("T") > -1) {
				var d = timeStmp.split("T");
				return Formatter.dateFormat(d[0]) + " " + d[1].substr(0, 8);
			} else if (timeStmp) {
				return timeStmp;
			}
			return null;
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oEvent Eventing object
		 * @param {boolean} flag Flag value
		 */
		onPressGetGstr1Detail: function (oEvent, flag) {
			if (!this._oDialog) {
				this._oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.ProcessStatus", this);
				this.getView().addDependent(this._oDialog);
			}
			if (flag === "P") {
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();
				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Please Select at least one Gstin");
					return;
				}
				var oTaxPeriod = this.byId("dtProcessed").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = oSelectedItem.map(function (e) {
					return oModelForTab1[e].gstin;
				});
				this.getView().setModel(new JSONModel({
					"P": true
				}), "visiSummPopup");
				this.byId("id_TaxProcess1").setValue(oTaxPeriod);
				this.byId("id_gstinPopup1").setText("");
				this.vPSFlag = "P";
			} else {
				var oTaxPeriod = this.getView().byId("dtSummary").getValue(),
					gstin = this.getView().byId("slSummaryGstin").getSelectedKey(),
					aGstin = [gstin];

				this.byId("id_TaxProcess1").setValue(oTaxPeriod);
				this.byId("id_gstinPopup1").setText(gstin);
				this.getView().setModel(new JSONModel({
					"P": false
				}), "visiSummPopup");
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				this.vPSFlag = "S";
			}
			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin
				}
			};
			this.postpayloadgstr1 = postData;
			this.getGstr1ASucessStatusDataFinal();
			this._oDialog.open();
		},

		onGstr1StatsFailed: function (oEvent, type) {
			var flag = this.getView().getModel("visiSummPopup").getProperty("/P"),
				payload = {
					"req": {
						"gstin": (flag ? oEvent.getSource().getBindingContext("Gstr1ASucess").getObject().gstin : this.byId("id_gstinPopup1").getText()),
						"taxPeriod": this.byId("id_TaxProcess1").getValue(),
						"returnType": "GSTR1",
						"section": type
					}
				};
			this._oDialog.setBusy(true);
			if (!this._msgPopover) {
				this._msgPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.MsgPopover", this);
				this.getView().addDependent(this._msgPopover);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCallFailureErrMsg.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._msgPopover.setModel(new JSONModel(data.resp), "PopoverMsg");
					this._msgPopover.openBy(oEvent.getSource());
					this._oDialog.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialog.setBusy(false);
				}.bind(this));
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onCloseProcDialog: function () {
			this._oDialog.close();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oEvent Eventing object
		 */
		onChangeSegmentProcessStatus: function (oEvent) {
			var key = oEvent.getSource().getSelectedKey();
			this.byId("idgettitle12").setVisible(key === "LCS");
			this.byId("idGetSucessTitle12").setVisible(key === "LSS");
			this.byId("idgetVtablegstr1pro").setVisible(key === "LCS");
			this.byId("idgetVtablegst1proLast").setVisible(key === "LSS");
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} postData Data object
		 */
		getGstr1ASucessStatusDataFinal: function (postData) {

			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1DetailStatus.do",
					contentType: "application/json",
					data: JSON.stringify(that.postpayloadgstr1)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					that.bindGstr1DetailStatus(data);
					// var oGstr6ASucessData = new sap.ui.model.json.JSONModel(data);
					// oView.setModel(oGstr6ASucessData, "Gstr6ASucess");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr1DetailStatus : Error");
				});
			});
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} data Data object
		 */
		bindGstr1DetailStatus: function (data) {

			var oView = this.getView();
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = false;
				data.resp.lastCall[i].b2baFlag = false;
				data.resp.lastCall[i].b2clFlag = false;
				data.resp.lastCall[i].b2claFlag = false;
				data.resp.lastCall[i].exportFlag = false;
				data.resp.lastCall[i].expoartAFlag = false;
				data.resp.lastCall[i].cdnrFlag = false;
				data.resp.lastCall[i].cdnraFlag = false;
				data.resp.lastCall[i].cdnurFlag = false;
				data.resp.lastCall[i].cdnuraFlag = false;
				data.resp.lastCall[i].b2csFlag = false;
				data.resp.lastCall[i].b2csaFlag = false;
				data.resp.lastCall[i].nilNonFlag = false;
				data.resp.lastCall[i].advrFlag = false;
				data.resp.lastCall[i].advraFlag = false;
				data.resp.lastCall[i].advaFlag = false;
				data.resp.lastCall[i].advaaFlag = false;
				data.resp.lastCall[i].hsnFlag = false;
				data.resp.lastCall[i].docseriesFlag = false;
				data.resp.lastCall[i].lsFlag = false;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = false;
				data.resp.lastSuccess[i].b2baFlag = false;
				data.resp.lastSuccess[i].b2clFlag = false;
				data.resp.lastSuccess[i].b2claFlag = false;
				data.resp.lastSuccess[i].exportFlag = false;
				data.resp.lastSuccess[i].expoartAFlag = false;
				data.resp.lastSuccess[i].cdnrFlag = false;
				data.resp.lastSuccess[i].cdnraFlag = false;
				data.resp.lastSuccess[i].cdnurFlag = false;
				data.resp.lastSuccess[i].cdnuraFlag = false;
				data.resp.lastSuccess[i].b2csFlag = false;
				data.resp.lastSuccess[i].b2csaFlag = false;
				data.resp.lastSuccess[i].nilNonFlag = false;
				data.resp.lastSuccess[i].advrFlag = false;
				data.resp.lastSuccess[i].advraFlag = false;
				data.resp.lastSuccess[i].advaFlag = false;
				data.resp.lastSuccess[i].advaaFlag = false;
				data.resp.lastSuccess[i].hsnFlag = false;
				data.resp.lastSuccess[i].docseriesFlag = false;
				data.resp.lastSuccess[i].lsFlag = false;
			}
			this._showing(false);
			var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr1ASucessData, "Gstr1ASucess");
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onPressGetBtn: function (oEvent) {
			var oBtProcess = this.byId("idProcessStatusBtn1").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oModelForTab1 = this.byId("idgetVtablegstr1pro").getModel("Gstr1ASucess").getProperty("/resp"),
					oTaxPeriod = this.byId("id_TaxProcess1").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastCall[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr1Sections": [],
						// "action_required": "Y",
						"isFailed": false
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr1Sections.push("b2b");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr1Sections.push("b2ba");
					}
					if (oModelForTab1.lastCall[i].b2clFlag) {
						postData.req[i].gstr1Sections.push("b2cl");
					}
					if (oModelForTab1.lastCall[i].b2claFlag) {
						postData.req[i].gstr1Sections.push("b2cla");
					}
					if (oModelForTab1.lastCall[i].exportFlag) {
						postData.req[i].gstr1Sections.push("exp");
					}
					if (oModelForTab1.lastCall[i].expoartAFlag) {
						postData.req[i].gstr1Sections.push("expa");
					}
					if (oModelForTab1.lastCall[i].cdnrFlag) {
						postData.req[i].gstr1Sections.push("cdnr");
					}
					if (oModelForTab1.lastCall[i].cdnraFlag) {
						postData.req[i].gstr1Sections.push("cdnra");
					}
					if (oModelForTab1.lastCall[i].cdnurFlag) {
						postData.req[i].gstr1Sections.push("cdnur");
					}
					if (oModelForTab1.lastCall[i].cdnuraFlag) {
						postData.req[i].gstr1Sections.push("cdnura");
					}
					if (oModelForTab1.lastCall[i].b2csFlag) {
						postData.req[i].gstr1Sections.push("b2cs");
					}
					if (oModelForTab1.lastCall[i].b2csaFlag) {
						postData.req[i].gstr1Sections.push("b2csa");
					}
					if (oModelForTab1.lastCall[i].nilNonFlag) {
						postData.req[i].gstr1Sections.push("nil");
					}
					if (oModelForTab1.lastCall[i].advrFlag) {
						postData.req[i].gstr1Sections.push("at");
					}
					if (oModelForTab1.lastCall[i].advraFlag) {
						postData.req[i].gstr1Sections.push("ata");
					}
					if (oModelForTab1.lastCall[i].advaFlag) {
						postData.req[i].gstr1Sections.push("txp");
					}
					if (oModelForTab1.lastCall[i].advaaFlag) {
						postData.req[i].gstr1Sections.push("txpa");
					}
					if (oModelForTab1.lastCall[i].hsnFlag) {
						postData.req[i].gstr1Sections.push("hsn");
					}
					if (oModelForTab1.lastCall[i].docseriesFlag) {
						postData.req[i].gstr1Sections.push("doc_issue");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSectionButtonIntrigrationFinal(postData);
			} else {
				var oModelForTab1 = this.byId("idgetVtablegst1proLast").getModel("Gstr1ASucess").getProperty("/resp"),
					oTaxPeriod = this.byId("id_TaxProcess1").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr1Sections": [],
						"action_required": "Y",
						"isFailed": false
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr1Sections.push("b2b");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr1Sections.push("b2ba");
					}
					if (oModelForTab1.lastSuccess[i].b2clFlag) {
						postData.req[i].gstr1Sections.push("b2cl");
					}
					if (oModelForTab1.lastSuccess[i].b2claFlag) {
						postData.req[i].gstr1Sections.push("b2cla");
					}
					if (oModelForTab1.lastSuccess[i].exportFlag) {
						postData.req[i].gstr1Sections.push("exp");
					}
					if (oModelForTab1.lastSuccess[i].expoartAFlag) {
						postData.req[i].gstr1Sections.push("expa");
					}
					if (oModelForTab1.lastSuccess[i].cdnrFlag) {
						postData.req[i].gstr1Sections.push("cdnr");
					}
					if (oModelForTab1.lastSuccess[i].cdnraFlag) {
						postData.req[i].gstr1Sections.push("cdnra");
					}
					if (oModelForTab1.lastSuccess[i].cdnurFlag) {
						postData.req[i].gstr1Sections.push("cdnur");
					}
					if (oModelForTab1.lastSuccess[i].cdnuraFlag) {
						postData.req[i].gstr1Sections.push("cdnura");
					}
					if (oModelForTab1.lastSuccess[i].b2csFlag) {
						postData.req[i].gstr1Sections.push("b2cs");
					}
					if (oModelForTab1.lastSuccess[i].b2csaFlag) {
						postData.req[i].gstr1Sections.push("b2csa");
					}
					if (oModelForTab1.lastSuccess[i].nilNonFlag) {
						postData.req[i].gstr1Sections.push("nil");
					}
					if (oModelForTab1.lastSuccess[i].advrFlag) {
						postData.req[i].gstr1Sections.push("at");
					}
					if (oModelForTab1.lastSuccess[i].advraFlag) {
						postData.req[i].gstr1Sections.push("ATA");
					}
					if (oModelForTab1.lastSuccess[i].advaFlag) {
						postData.req[i].gstr1Sections.push("TXP");
					}
					if (oModelForTab1.lastSuccess[i].advaaFlag) {
						postData.req[i].gstr1Sections.push("TXPA");
					}
					if (oModelForTab1.lastSuccess[i].hsnFlag) {
						postData.req[i].gstr1Sections.push("hsn");
					}
					if (oModelForTab1.lastSuccess[i].docseriesFlag) {
						postData.req[i].gstr1Sections.push("DOC_ISSUE");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSectionButtonIntrigrationFinal(postData);
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		getSectionButtonIntrigrationFinal: function (postData) {

			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr1GstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							aMockMessages.push({
								type: "Success",
								title: data.resp[i].gstin,
								gstin: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
					}
					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogPress();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr1GstnGetSection : Error");
				});
			});
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get GSTR1 Call",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.onPressGetGstr1Detail("", that.vPSFlag);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onRowSelectionChange: function (oEvent) {

			var data = this.getView().getModel("Gstr1ASucess").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			var oBtProcess = this.byId("idProcessStatusBtn1").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlag(true);
				this._showing(true);
			} else if (vRowIndex === -1) {
				this._setFlag(false);
				this._showing(false);
			} else {

				if (oBtProcess === "LCS") {
					if (data.resp.lastCall[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}

					data.resp.lastCall[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2clFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2claFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].exportFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].expoartAFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnrFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnraFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnurFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnuraFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2csFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2csaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].nilNonFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].advrFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].advraFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].advaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].advaaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].hsnFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].docseriesFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				} else {
					if (data.resp.lastSuccess[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastSuccess[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2clFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2claFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].exportFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].expoartAFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnrFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnraFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnurFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnuraFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2csFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2csaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].nilNonFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].advrFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].advraFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].advaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].advaaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].hsnFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].docseriesFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].lsFlag = oSelectedFlag;
				}
			}
			this.getView().getModel("Gstr1ASucess").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_setFlag: function (oSelectedFlag) {
			var data = this.getView().getModel("Gstr1ASucess").getData();
			var oBtProcess = this.byId("idProcessStatusBtn1").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					data.resp.lastCall[i].b2bFlag = oSelectedFlag;
					data.resp.lastCall[i].b2baFlag = oSelectedFlag;
					data.resp.lastCall[i].b2clFlag = oSelectedFlag;
					data.resp.lastCall[i].b2claFlag = oSelectedFlag;
					data.resp.lastCall[i].exportFlag = oSelectedFlag;
					data.resp.lastCall[i].expoartAFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnrFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnraFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnurFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnuraFlag = oSelectedFlag;
					data.resp.lastCall[i].b2csFlag = oSelectedFlag;
					data.resp.lastCall[i].b2csaFlag = oSelectedFlag;
					data.resp.lastCall[i].nilNonFlag = oSelectedFlag;
					data.resp.lastCall[i].advrFlag = oSelectedFlag;
					data.resp.lastCall[i].advraFlag = oSelectedFlag;
					data.resp.lastCall[i].advaFlag = oSelectedFlag;
					data.resp.lastCall[i].advaaFlag = oSelectedFlag;
					data.resp.lastCall[i].hsnFlag = oSelectedFlag;
					data.resp.lastCall[i].docseriesFlag = oSelectedFlag;
					data.resp.lastCall[i].lsFlag = oSelectedFlag;
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2clFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2claFlag = oSelectedFlag;
					data.resp.lastSuccess[i].exportFlag = oSelectedFlag;
					data.resp.lastSuccess[i].expoartAFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnrFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnraFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnurFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnuraFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2csFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2csaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].nilNonFlag = oSelectedFlag;
					data.resp.lastSuccess[i].advrFlag = oSelectedFlag;
					data.resp.lastSuccess[i].advraFlag = oSelectedFlag;
					data.resp.lastSuccess[i].advaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].advaaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].hsnFlag = oSelectedFlag;
					data.resp.lastSuccess[i].docseriesFlag = oSelectedFlag;
					data.resp.lastSuccess[i].lsFlag = oSelectedFlag;
				}
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_showing: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.b2baFlag = oSelectedFlag;
			oData.b2clFlag = oSelectedFlag;
			oData.b2claFlag = oSelectedFlag;
			oData.exportFlag = oSelectedFlag;
			oData.expoartAFlag = oSelectedFlag;
			oData.cdnrFlag = oSelectedFlag;
			oData.cdnraFlag = oSelectedFlag;
			oData.cdnurFlag = oSelectedFlag;
			oData.cdnuraFlag = oSelectedFlag;
			oData.b2csFlag = oSelectedFlag;
			oData.b2csaFlag = oSelectedFlag;
			oData.nilNonFlag = oSelectedFlag;
			oData.advrFlag = oSelectedFlag;
			oData.advraFlag = oSelectedFlag;
			oData.advaFlag = oSelectedFlag;
			oData.advaaFlag = oSelectedFlag;
			oData.hsnFlag = oSelectedFlag;
			oData.docseriesFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onSelectAllCheck: function (oEvent) {

			var oView = this.getView();
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = oView.getModel("Gstr1ASucess").getData();
			if (oSelectedFlag) {
				this.byId("idgetVtablegstr1pro").selectAll();
				this.byId("idgetVtablegst1proLast").selectAll();
			} else {
				this.byId("idgetVtablegstr1pro").setSelectedKeys([]);
				this.byId("idgetVtablegst1proLast").setSelectedKeys([]);
			}
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = oSelectedFlag;
				data.resp.lastCall[i].b2baFlag = oSelectedFlag;
				data.resp.lastCall[i].b2clFlag = oSelectedFlag;
				data.resp.lastCall[i].b2claFlag = oSelectedFlag;
				data.resp.lastCall[i].exportFlag = oSelectedFlag;
				data.resp.lastCall[i].expoartAFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnrFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnraFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnurFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnuraFlag = oSelectedFlag;
				data.resp.lastCall[i].b2csFlag = oSelectedFlag;
				data.resp.lastCall[i].b2csaFlag = oSelectedFlag;
				data.resp.lastCall[i].nilNonFlag = oSelectedFlag;
				data.resp.lastCall[i].advrFlag = oSelectedFlag;
				data.resp.lastCall[i].advraFlag = oSelectedFlag;
				data.resp.lastCall[i].advaFlag = oSelectedFlag;
				data.resp.lastCall[i].advaaFlag = oSelectedFlag;
				data.resp.lastCall[i].hsnFlag = oSelectedFlag;
				data.resp.lastCall[i].docseriesFlag = oSelectedFlag;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2clFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2claFlag = oSelectedFlag;
				data.resp.lastSuccess[i].exportFlag = oSelectedFlag;
				data.resp.lastSuccess[i].expoartAFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnrFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnraFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnurFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnuraFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2csFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2csaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].nilNonFlag = oSelectedFlag;
				data.resp.lastSuccess[i].advrFlag = oSelectedFlag;
				data.resp.lastSuccess[i].advraFlag = oSelectedFlag;
				data.resp.lastSuccess[i].advaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].advaaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].hsnFlag = oSelectedFlag;
				data.resp.lastSuccess[i].docseriesFlag = oSelectedFlag;
			}
			// var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
			oView.getModel("Gstr1ASucess").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onSelectAllCheckHeader: function (oEvent, field) {

			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.getView().getModel("Gstr1ASucess").getData();
			var oBtProcess = this.byId("idProcessStatusBtn1").getSelectedKey();
			// field = JSON.parse(field);
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastCall[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastCall[i].b2baFlag = oSelectedFlag;
						break;
					case "b2clFlag":
						data.resp.lastCall[i].b2clFlag = oSelectedFlag;
						break;
					case "b2claFlag":
						data.resp.lastCall[i].b2claFlag = oSelectedFlag;
						break;
					case "exportFlag":
						data.resp.lastCall[i].exportFlag = oSelectedFlag;
						break;
					case "expoartAFlag":
						data.resp.lastCall[i].expoartAFlag = oSelectedFlag;
						break;
					case "cdnrFlag":
						data.resp.lastCall[i].cdnrFlag = oSelectedFlag;
						break;
					case "cdnraFlag":
						data.resp.lastCall[i].cdnraFlag = oSelectedFlag;
						break;
					case "cdnurFlag":
						data.resp.lastCall[i].cdnurFlag = oSelectedFlag;
						break;
					case "cdnuraFlag":
						data.resp.lastCall[i].cdnuraFlag = oSelectedFlag;
						break;
					case "b2csFlag":
						data.resp.lastCall[i].b2csFlag = oSelectedFlag;
						break;
					case "b2csaFlag":
						data.resp.lastCall[i].b2csaFlag = oSelectedFlag;
						break;
					case "nilNonFlag":
						data.resp.lastCall[i].nilNonFlag = oSelectedFlag;
						break;
					case "advrFlag":
						data.resp.lastCall[i].advrFlag = oSelectedFlag;
						break;
					case "advraFlag":
						data.resp.lastCall[i].advraFlag = oSelectedFlag;
						break;
					case "advaFlag":
						data.resp.lastCall[i].advaFlag = oSelectedFlag;
						break;
					case "advaaFlag":
						data.resp.lastCall[i].advaaFlag = oSelectedFlag;
						break;
					case "hsnFlag":
						data.resp.lastCall[i].hsnFlag = oSelectedFlag;
						break;
					case "docseriesFlag":
						data.resp.lastCall[i].docseriesFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
						break;
					case "b2clFlag":
						data.resp.lastSuccess[i].b2clFlag = oSelectedFlag;
						break;
					case "b2claFlag":
						data.resp.lastSuccess[i].b2claFlag = oSelectedFlag;
						break;
					case "exportFlag":
						data.resp.lastSuccess[i].exportFlag = oSelectedFlag;
						break;
					case "expoartAFlag":
						data.resp.lastSuccess[i].expoartAFlag = oSelectedFlag;
						break;
					case "cdnrFlag":
						data.resp.lastSuccess[i].cdnrFlag = oSelectedFlag;
						break;
					case "cdnraFlag":
						data.resp.lastSuccess[i].cdnraFlag = oSelectedFlag;
						break;
					case "cdnurFlag":
						data.resp.lastSuccess[i].cdnurFlag = oSelectedFlag;
						break;
					case "cdnuraFlag":
						data.resp.lastSuccess[i].cdnuraFlag = oSelectedFlag;
						break;
					case "b2csFlag":
						data.resp.lastSuccess[i].b2csFlag = oSelectedFlag;
						break;
					case "b2csaFlag":
						data.resp.lastSuccess[i].b2csaFlag = oSelectedFlag;
						break;
					case "nilNonFlag":
						data.resp.lastSuccess[i].nilNonFlag = oSelectedFlag;
						break;
					case "advrFlag":
						data.resp.lastSuccess[i].advrFlag = oSelectedFlag;
						break;
					case "advraFlag":
						data.resp.lastSuccess[i].advraFlag = oSelectedFlag;
						break;
					case "advaFlag":
						data.resp.lastSuccess[i].advaFlag = oSelectedFlag;
						break;
					case "advaaFlag":
						data.resp.lastSuccess[i].advaaFlag = oSelectedFlag;
						break;
					case "hsnFlag":
						data.resp.lastSuccess[i].hsnFlag = oSelectedFlag;
						break;
					case "docseriesFlag":
						data.resp.lastSuccess[i].docseriesFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			}
			this.getView().getModel("Gstr1ASucess").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onPressSaveGstn1: function (oEvent, flag) {

			if (!this._oDialogSaveStatusBtn) {
				this._oDialogSaveStatusBtn = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.SaveToGstin", this);
				this.getView().addDependent(this._oDialogSaveStatusBtn);
			}

			if (flag === "P") {
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();

				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("PLease Select at least one Gstin");
					return;
				}
				this.ResetFlag = "P";
				var oModel = this.getView().setModel(new JSONModel({
					"P": true
				}), "visiSummPopup");
				sap.ui.getCore().byId("id_TaxProcess12").setValue(this.oView.byId("dtProcessed").getValue());
				sap.ui.getCore().byId("id_select1Nd").setSelected(false);
				sap.ui.getCore().byId("id_select2Nd").setSelected(false);

				var oTaxPeriod = this.oView.byId("dtProcessed").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = oSelectedItem.map(function (e) {
					return oModelForTab1[e].gstin;
				});
				this.oSavePayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"returnType": "GSTR1",
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};

				this.onRadioBtnselect();
				// var oTaxPeriod = this.oView.byId("dtProcessed").getValue();
				// if (oTaxPeriod === "") {
				// 	oTaxPeriod = null;
				// }
				// var aGstin = [];
				// for (var i = 0; i < oSelectedItem.length; i++) {
				// 	aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				// }
				// var postData = {
				// 	"req": {
				// 		"entityId": $.sap.entityID,
				// 		"taxPeriod": oTaxPeriod,
				// 		"gstin": aGstin
				// 	}
				// };
				// this.postpayloadgstr1 = postData;
				// this.getGstr1ASucessStatusDataFinal();
				// this.vPSFlag = "P";
				this._oDialogSaveStatusBtn.open();
			} else {
				this.ResetFlag = "S";
				this.getView().setModel(new JSONModel({
					"P": false
				}), "visiSummPopup");
				sap.ui.getCore().byId("id_TaxProcess12").setValue(this.oView.byId("dtSummary").getValue());
				sap.ui.getCore().byId("slSummaryGstin11").setSelectedKey(this.oView.byId("slSummaryGstin").getSelectedKey());
				sap.ui.getCore().byId("id_select1Nd").setSelected(false);
				sap.ui.getCore().byId("id_select2Nd").setSelected(false);
				this.onRadioBtnselect();

				this.oSavePayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.oView.byId("dtSummary").getValue(),
						"returnType": "GSTR1",
						"dataSecAttrs": {
							"GSTIN": [this.oView.byId("slSummaryGstin").getSelectedKey()]
						}
					}
				};
				this._oDialogSaveStatusBtn.open();
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onRadioBtnselect: function (oEvent) {

			this.getSavetoGstinIntrigration("abc");
			// sap.ui.getCore().byId("id_select1Nd").setSelected(false);
			// sap.ui.getCore().byId("id_select2Nd").setSelected(false);
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onSelectSaveStatausPopup: function (oEvent, flag) {

			var oModel = this.getView().getModel("GetSection"),
				oData = oModel.getProperty("/"),
				vSelected = oEvent.getSource().getSelected();

			if (flag === "T") {
				oData.b2bFlag = vSelected;
				oData.b2baFlag = vSelected;
				oData.b2clFlag = vSelected;
				oData.b2claFlag = vSelected;
				oData.expFlag = vSelected;
				oData.expaFlag = vSelected;
				oData.cdnrFlag = vSelected;
				oData.cdnraFlag = vSelected;
				oData.cdnurFlag = vSelected;
				oData.cdnuraFlag = vSelected;
				oData.ecom15Flag = vSelected;
				oData.ecom15AIFlag = vSelected;
				oData.ecom15AIIFlag = vSelected;
				oData.ecom15iFlag = vSelected;
				oData.ecom15iiFlag = vSelected;
			} else {
				oData.atFlag = vSelected;
				oData.ataFlag = vSelected;
				oData.txpFlag = vSelected;
				oData.txpaFlag = vSelected;
				oData.nilFlag = vSelected;
				oData.b2csFlag = vSelected;
				oData.b2csaFlag = vSelected;
				oData.hsnsumFlag = vSelected;
				oData.docissFlag = vSelected;
				oData.supp14Flag = vSelected;
				oData.supp14AFlag = vSelected;
			}
			oModel.refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		getSavetoGstinIntrigration: function (flag) {

			var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcess12").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var vGstins = sap.ui.getCore().byId("slSummaryGstin11").getSelectedKey(),
				postData = {
					"req": [{
						"gstin": vGstins,
						"ret_period": oTaxPeriod
					}]
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SaveToGstnSectionSelection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (flag === "S") {
						this.bindGetSavetoGstinIntrigration(data, "S");
					} else {
						this.bindGetSavetoGstinIntrigration(data, "abc");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1SaveToGstnSectionSelection : Error");
				});
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} data Data object
		 * @param {string} flag Action
		 */
		bindGetSavetoGstinIntrigration: function (data, flag) {
			var aField = [
				"b2bFlag", "b2baFlag", "b2clFlag", "b2claFlag", "expFlag", "expaFlag", "cdnrFlag", "cdnraFlag", "cdnurFlag", "cdnuraFlag",
				"ecom15Flag", "ecom15AIFlag", "ecom15AIIFlag", "ecom15iFlag", "ecom15iiFlag",
				"atFlag", "ataFlag", "txpFlag", "txpaFlag", "nilFlag", "b2csFlag", "b2csaFlag", "hsnsumFlag", "docissFlag", "supp14Flag",
				"supp14AFlag", "edit",
				"b2bEdit", "b2baEdit", "b2clEdit", "b2claEdit", "expEdit", "expaEdit", "cdnrEdit", "cdnraEdit", "cdnurEdit", "cdnuraEdit",
				"atEdit", "ataEdit", "txpEdit", "txpaEdit", "nilEdit", "b2csEdit", "b2csaEdit", "hsnsumEdit", "docissEdit"
			]
			data.resp.forEach(function (item) {
				aField.forEach(function (f) {
					item[f] = false;
				});
			});

			if (flag === "S") {
				data.resp[0].edit = true;
				if (data.resp[0].sections.includes("B2B")) {
					data.resp[0].b2bEdit = true;
				}
				if (data.resp[0].sections.includes("B2BA")) {
					data.resp[0].b2baEdit = true;
				}
				if (data.resp[0].sections.includes("B2CL")) {
					data.resp[0].b2clEdit = true;
				}
				if (data.resp[0].sections.includes("B2CLA")) {
					data.resp[0].b2claEdit = true;
				}
				if (data.resp[0].sections.includes("EXP")) {
					data.resp[0].expEdit = true;
				}
				if (data.resp[0].sections.includes("EXPA")) {
					data.resp[0].expaEdit = true;
				}
				if (data.resp[0].sections.includes("CDNR")) {
					data.resp[0].cdnrEdit = true;
				}
				if (data.resp[0].sections.includes("CDNRA")) {
					data.resp[0].cdnraEdit = true;
				}
				if (data.resp[0].sections.includes("CDNUR")) {
					data.resp[0].cdnurEdit = true;
				}
				if (data.resp[0].sections.includes("CDNURA")) {
					data.resp[0].cdnuraEdit = true;
				}
				if (data.resp[0].sections.includes("AT")) {
					data.resp[0].atEdit = true;
				}
				if (data.resp[0].sections.includes("ATA")) {
					data.resp[0].ataEdit = true;
				}
				if (data.resp[0].sections.includes("TXP")) {
					data.resp[0].txpEdit = true;
				}
				if (data.resp[0].sections.includes("TXPA")) {
					data.resp[0].txpaEdit = true;
				}
				if (data.resp[0].sections.includes("NIL")) {
					data.resp[0].nilEdit = true;
				}
				if (data.resp[0].sections.includes("B2CS")) {
					data.resp[0].b2csEdit = true;
				}
				if (data.resp[0].sections.includes("B2CSA")) {
					data.resp[0].b2csaEdit = true;
				}
				if (data.resp[0].sections.includes("HSNSUM")) {
					data.resp[0].hsnsumEdit = true;
				}
				if (data.resp[0].sections.includes("DOCSISS")) {
					data.resp[0].docissEdit = true;
				}
			}
			var oModel1 = new sap.ui.model.json.JSONModel(data.resp[0]);
			this.getView().setModel(oModel1, "GetSection");
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oEvent Eventing object
		 */
		onPressProcessSumBtngstr1: function (oEvent) {
			var oBtProcess = this.byId("idProcessStatusBtn1").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oModelForTab1 = this.byId("idgetVtablegstr1pro").getModel("Gstr1ASucess").getProperty("/resp"),
					oTaxPeriod = this.byId("id_TaxProcess1").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastCall[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr1aSections": []
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr1aSections.push("B2B");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr1aSections.push("B2BA");
					}
					if (oModelForTab1.lastCall[i].cdnrFlag) {
						postData.req[i].gstr1aSections.push("CDNR");
					}
					if (oModelForTab1.lastCall[i].cdnraFlag) {
						postData.req[i].gstr1aSections.push("CDNRA");
					}
					if (oModelForTab1.lastCall[i].cdnurFlag) {
						postData.req[i].gstr1aSections.push("CDNUR");
					}
					if (oModelForTab1.lastCall[i].cdnuraFlag) {
						postData.req[i].gstr1aSections.push("CDNURA");
					}
					if (oModelForTab1.lastCall[i].exportFlag) {
						postData.req[i].gstr1aSections.push("EXP");
					}
					if (oModelForTab1.lastCall[i].expoartAFlag) {
						postData.req[i].gstr1aSections.push("EXPA");
					}
					if (oModelForTab1.lastCall[i].b2clFlag) {
						postData.req[i].gstr1aSections.push("B2CL");
					}
					if (oModelForTab1.lastCall[i].b2claFlag) {
						postData.req[i].gstr1aSections.push("B2CLA");
					}
					if (oModelForTab1.lastCall[i].b2csFlag) {
						postData.req[i].gstr1aSections.push("B2CS");
					}
					if (oModelForTab1.lastCall[i].b2csaFlag) {
						postData.req[i].gstr1aSections.push("B2CSA");
					}
					if (oModelForTab1.lastCall[i].advrFlag) {
						postData.req[i].gstr1aSections.push("ADV REC");
					}
					if (oModelForTab1.lastCall[i].advraFlag) {
						postData.req[i].gstr1aSections.push("ADV REC-A");
					}
					if (oModelForTab1.lastCall[i].advaFlag) {
						postData.req[i].gstr1aSections.push("ADV ADJ");
					}
					if (oModelForTab1.lastCall[i].advaaFlag) {
						postData.req[i].gstr1aSections.push("ADV ADJ-A");
					}
					if (oModelForTab1.lastCall[i].nilNonFlag) {
						postData.req[i].gstr1aSections.push("NILEXTNON");
					}
					if (oModelForTab1.lastCall[i].hsnFlag) {
						postData.req[i].gstr1aSections.push("HSN");
					}
					if (oModelForTab1.lastCall[i].docseriesFlag) {
						postData.req[i].gstr1aSections.push("INV SERIES");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/gstr1aGetDownloads.do";
				this.excelDownload(postData, url);
			} else {
				var oModelForTab1 = this.byId("idgetVtablegst1proLast").getModel("Gstr1ASucess").getProperty("/resp"),
					oTaxPeriod = this.byId("id_TaxProcess1").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"status": "LastSuccess",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr1aSections": []
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr1aSections.push("B2B");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr1aSections.push("B2BA");
					}
					if (oModelForTab1.lastSuccess[i].cdnrFlag) {
						postData.req[i].gstr1aSections.push("CDNR");
					}
					if (oModelForTab1.lastSuccess[i].cdnraFlag) {
						postData.req[i].gstr1aSections.push("CDNRA");
					}
					if (oModelForTab1.lastSuccess[i].cdnurFlag) {
						postData.req[i].gstr1aSections.push("CDNUR");
					}
					if (oModelForTab1.lastSuccess[i].cdnuraFlag) {
						postData.req[i].gstr1aSections.push("CDNURA");
					}
					if (oModelForTab1.lastSuccess[i].exportFlag) {
						postData.req[i].gstr1aSections.push("EXP");
					}
					if (oModelForTab1.lastSuccess[i].expoartAFlag) {
						postData.req[i].gstr1aSections.push("EXPA");
					}
					if (oModelForTab1.lastSuccess[i].b2clFlag) {
						postData.req[i].gstr1aSections.push("B2CL");
					}
					if (oModelForTab1.lastSuccess[i].b2claFlag) {
						postData.req[i].gstr1aSections.push("B2CLA");
					}
					if (oModelForTab1.lastSuccess[i].b2csFlag) {
						postData.req[i].gstr1aSections.push("B2CS");
					}
					if (oModelForTab1.lastSuccess[i].b2csaFlag) {
						postData.req[i].gstr1aSections.push("B2CSA");
					}
					if (oModelForTab1.lastSuccess[i].advrFlag) {
						postData.req[i].gstr1aSections.push("AT");
					}
					if (oModelForTab1.lastSuccess[i].advraFlag) {
						postData.req[i].gstr1aSections.push("ATA");
					}
					if (oModelForTab1.lastSuccess[i].advaFlag) {
						postData.req[i].gstr1aSections.push("TXP");
					}
					if (oModelForTab1.lastSuccess[i].advaaFlag) {
						postData.req[i].gstr1aSections.push("TXPA");
					}
					if (oModelForTab1.lastSuccess[i].nilNonFlag) {
						postData.req[i].gstr1aSections.push("NIL");
					}
					if (oModelForTab1.lastSuccess[i].hsnFlag) {
						postData.req[i].gstr1aSections.push("HSN");
					}
					if (oModelForTab1.lastSuccess[i].docseriesFlag) {
						postData.req[i].gstr1aSections.push("DOC_ISSUE");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				url = "/aspsapapi/gstr1aGetDownloads.do";
				this.excelDownload(postData, url);
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @param {string} action Action type
		 */
		onSaveGstr6ABtnDialog: function (action) {
			switch (action) {
			case "S":
				this.gstr1SaveToGstnResetAndSaveJob();
				break;
			case "R":
				this.gstr1SaveToGstnReset();
				break;
			case "C":
				this._oDialogSaveStatusBtn.close();
				break;
			}
		},

		/**
		 * Method called to Save data to GSTN
		 * Developed by: Sarvamangala
		 * Modified by: Bharat Gupta - 07.12.2020
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onPressdeleteAuotodreafted: function (oEvent, flag) {

			var that = this;
			if (flag === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Please Select at least one Gstin");
					return;
				}
				var flag1 = false;
				var flag2 = false;
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
					if (oModelForTab1[oSelectedItem[i]].authToken == "Active") {
						flag1 = true;

					}
					if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
						flag2 = true;

					}
				}
				var payload = {
					"req": {
						"gstins": aGstin,
						"retPeriod": that.byId("dtProcessed").getValue(),
					}
				};
				if (flag1 == true && flag2 == true) {
					MessageBox.show(
						"Auth token for few GSTINs is inactive,hence GSTR-1 data available on GST common portal will be deleted for GSTINs for which auth token is active. Do you want to proceed?", {
							icon: MessageBox.Icon.INFORMATION,
							title: "Information",
							actions: ["Yes", "No"],
							emphasizedAction: "Yes",
							onClose: function (sAction) {
								if (sAction === "Yes") {
									// that.onPressdeleteAuotodreaftedfullFinal(payload);
									that.onDelete2ProcessScreenGstr1(payload);

								}

							}
						}
					);

				} else {
					this.onDelete2ProcessScreenGstr1(payload);
				}

			} else {
				var that = this;
				var oSummaryData = this.byId("dpGstr1Summary").getModel("ProcessSummary").getData(),
					payload = {
						"req": {
							"gstins": [that.byId("slSummaryGstin").getSelectedKey()],
							"retPeriod": that.byId("dtSummary").getValue(),
							// "tableSections": [],
							// "isResetSave": (oRadioBtn === 0), // isResetSave
							"isNilUserInput": oSummaryData.nilUserInput,
							"isHsnUserInput": oSummaryData.hsnUserInput
						}
					};
				this.onDelete2ProcessScreenGstr1(payload);

				// oRadioBtn = sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex(),
				// oModelSave = this.getView().getModel("GetSection").getData(),

			}

		},
		onDelete2ProcessScreenGstr1: function (payload) {
			var that = this;
			MessageBox.show(
				"Do you want to delete GSTN data based on records uploaded as part of “Delete GSTN Records” file (Navigation : Data Status > Web Upload > Category GST Returns > File Type Delete GSTN Records)?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: ["Proceed", "Cancel"],
					emphasizedAction: "Proceed",
					onClose: function (sAction) {
						if (sAction === "Proceed") {
							MessageBox.show(
								"Do you want to proceed with deletion of data from GSTN? On click of proceed button, deletion will be initiated", {
									icon: MessageBox.Icon.INFORMATION,
									title: "Information",
									actions: ["Proceed", "Cancel"],
									emphasizedAction: "Proceed",
									onClose: function (sAction) {
										if (sAction === "Proceed") {
											that.onPressdeleteAuotodreaftedFinal(payload);

										} else {}

									}
								}
							);
							// that.onPressdeleteAuotodreaftedFinal(payload);

						} else {}

					}
				}
			);

		},
		onPressdeleteAuotodreaftedFinal: function (payload) {

			// this._oDialogSaveStatusBtn.close();
			var oView = this.getView(),
				aMockMessages = [],
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1DeleteGstnDataJob.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length >= 0) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.hdr.status === "S") {
								MessageBox.success(
									data.resp[i].msg);

							} else {
								MessageBox.error(data.resp[i].msg);

							}

						}
					}

					// if (data.resp.length > 0) {
					// 	for (var i = 0; i < data.resp.length; i++) {
					// 		aMockMessages.push({
					// 			type: "Success",
					// 			title: data.resp[i].gstin,
					// 			gstin: data.resp[i].msg,
					// 			active: true,
					// 			icon: "sap-icon://message-success",
					// 			highlight: "Success",
					// 			info: "Success"
					// 		});
					// 	}
					// 	that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					// 	that.onDialogDeletePress();
					// }
					// that.bindGetSavetoGstinIntrigration(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1DeleteGstnDataJob : Error");
				});
			});
		},
		onDialogDeletePress: function () {
			var that = this;
			if (!this.pressDelDialog) {
				this.pressDelDialog = new Dialog({
					title: "Delete Data Screen Message",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDelDialog.close();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDelDialog);
			}
			this.pressDelDialog.open();
		},

		gstr1SaveToGstnReset: function () {

			var that = this;
			if (this.ResetFlag === "P") {
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices(),
					flag1 = false,
					flag2 = false,
					aGstin = oSelectedItem.map(function (e) {
						if (oModelForTab1[e].authToken == "Active") {
							flag1 = true;
						}
						if (oModelForTab1[e].authToken == "Inactive") {
							flag2 = true;
						}
						return oModelForTab1[e].gstin;
					});
				var payload = {
					"req": {
						"gstins": aGstin,
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
					}
				};

			} else {
				var oSummaryData = this.byId("dpGstr1Summary").getModel("ProcessSummary").getData();
				// oRadioBtn = sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex(),

				payload = {
					"req": {
						"gstins": [sap.ui.getCore().byId("slSummaryGstin11").getSelectedKey()],
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
						"isNilUserInput": oSummaryData.nilUserInput,
						"isHsnUserInput": oSummaryData.hsnUserInput
					}
				};

			}
			var oModelSave = this.getView().getModel("GetSection").getData();

			if (oModelSave.b2bFlag) {
				payload.req.tableSections.push("B2B");
			}
			if (oModelSave.b2baFlag) {
				payload.req.tableSections.push("B2BA");
			}
			if (oModelSave.b2clFlag) {
				payload.req.tableSections.push("B2CL");
			}
			if (oModelSave.b2claFlag) {
				payload.req.tableSections.push("B2CLA");
			}
			if (oModelSave.b2csFlag) {
				payload.req.tableSections.push("B2CS");
			}
			if (oModelSave.b2csaFlag) {
				payload.req.tableSections.push("B2CSA");
			}
			if (oModelSave.cdnrFlag) {
				payload.req.tableSections.push("CDNR");
			}
			if (oModelSave.cdnraFlag) {
				payload.req.tableSections.push("CDNRA");
			}
			if (oModelSave.cdnurFlag) {
				payload.req.tableSections.push("CDNUR");
			}
			if (oModelSave.cdnuraFlag) {
				payload.req.tableSections.push("CDNURA");
			}
			if (oModelSave.ecom15Flag) {
				payload.req.tableSections.push("Ecomsup");
			}
			if (oModelSave.ecom15iFlag) {
				payload.req.tableSections.push("ECOM[15(I) & 15(III)]");
			}
			if (oModelSave.ecom15iiFlag) {
				payload.req.tableSections.push("ECOM[15(II) & 15(IV)]");
			}
			if (oModelSave.docissFlag) {
				payload.req.tableSections.push("DOCISS");
			}
			if (oModelSave.supp14Flag) {
				payload.req.tableSections.push("Supecom");
			}
			if (oModelSave.supp14AFlag) {
				payload.req.tableSections.push("SupecomA");
			}
			if (oModelSave.expFlag) {
				payload.req.tableSections.push("EXP");
			}
			if (oModelSave.expaFlag) {
				payload.req.tableSections.push("EXPA");
			}
			if (oModelSave.hsnsumFlag) {
				payload.req.tableSections.push("HSNSUM");
			}
			if (oModelSave.nilFlag) {
				payload.req.tableSections.push("NIL");
			}
			if (oModelSave.txpFlag) {
				payload.req.tableSections.push("TXP");
			}
			if (oModelSave.txpaFlag) {
				payload.req.tableSections.push("TXPA");
			}
			if (oModelSave.atFlag) {
				payload.req.tableSections.push("AT");
			}
			if (oModelSave.ataFlag) {
				payload.req.tableSections.push("ATA");
			}
			if (payload.req.tableSections.length === 0) {
				MessageBox.information("Please select at-least one section");
				return;

			}
			if ((oModelSave.b2bFlag === true) || (oModelSave.expFlag === true) || (oModelSave.cdnurFlag === true) || (oModelSave.cdnrFlag ===
					true)) {

				MessageBox.show(
					"E-Invoice response will be removed and incremental+all data will be pushed?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								if (!this._oDialogResetBtn) {
									this._oDialogResetBtn = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.Reset", this);
									this.getView().addDependent(this._oDialogResetBtn);

								}
								this.vDialogResetBtnFlag = true;
								this._oDialogResetBtn.open();
							}
						}.bind(this)
					}
				);

			} else {
				MessageBox.show(
					"Do you want to Reset Data?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								that.gstr1SaveToGstnResetFinal(payload);

							}

						}
					}
				);

			}

		},
		onSaveResetBtnDialogC: function () {
			this._oDialogResetBtn.close();

		},

		onResetProcedDialog: function () {
			var that = this;
			if (this.ResetFlag === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var flag1 = false;
				var flag2 = false;
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
					if (oModelForTab1[oSelectedItem[i]].authToken == "Active") {
						flag1 = true;

					}
					if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
						flag2 = true;

					}
				}
				var payload = {
					"req": {
						"gstins": aGstin,
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
					}
				};

			} else {
				var oSummaryData = this.byId("dpGstr1Summary").getModel("ProcessSummary").getData();
				// oRadioBtn = sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex(),

				payload = {
					"req": {
						"gstins": [sap.ui.getCore().byId("slSummaryGstin11").getSelectedKey()],
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
						"isNilUserInput": oSummaryData.nilUserInput,
						"isHsnUserInput": oSummaryData.hsnUserInput
					}
				};

			}
			var oModelSave = this.getView().getModel("GetSection").getData();

			if (oModelSave.b2bFlag) {
				payload.req.tableSections.push("B2B");
			}
			if (oModelSave.b2baFlag) {
				payload.req.tableSections.push("B2BA");
			}
			if (oModelSave.b2clFlag) {
				payload.req.tableSections.push("B2CL");
			}
			if (oModelSave.b2claFlag) {
				payload.req.tableSections.push("B2CLA");
			}
			if (oModelSave.b2csFlag) {
				payload.req.tableSections.push("B2CS");
			}
			if (oModelSave.b2csaFlag) {
				payload.req.tableSections.push("B2CSA");
			}
			if (oModelSave.cdnrFlag) {
				payload.req.tableSections.push("CDNR");
			}
			if (oModelSave.cdnraFlag) {
				payload.req.tableSections.push("CDNRA");
			}
			if (oModelSave.cdnurFlag) {
				payload.req.tableSections.push("CDNUR");
			}
			if (oModelSave.cdnuraFlag) {
				payload.req.tableSections.push("CDNURA");
			}
			if (oModelSave.docissFlag) {
				payload.req.tableSections.push("DOCISS");
			}
			if (oModelSave.supp14Flag) {
				payload.req.tableSections.push("Supecom");
			}
			if (oModelSave.supp14AFlag) {
				payload.req.tableSections.push("SupecomA");
			}
			if (oModelSave.expFlag) {
				payload.req.tableSections.push("EXP");
			}
			if (oModelSave.expaFlag) {
				payload.req.tableSections.push("EXPA");
			}
			if (oModelSave.hsnsumFlag) {
				payload.req.tableSections.push("HSNSUM");
			}
			if (oModelSave.nilFlag) {
				payload.req.tableSections.push("NIL");
			}
			if (oModelSave.txpFlag) {
				payload.req.tableSections.push("TXP");
			}
			if (oModelSave.txpaFlag) {
				payload.req.tableSections.push("TXPA");
			}
			if (oModelSave.atFlag) {
				payload.req.tableSections.push("AT");
			}
			if (oModelSave.ataFlag) {
				payload.req.tableSections.push("ATA");
			}
			var getRadioSelection = sap.ui.getCore().byId("idRadioBtnReset").getSelectedIndex();
			switch (getRadioSelection) {
			case 0:
				that.gstr1SaveToGstnResetnonRespondedFinal(payload);
				break;
			case 1:
				that.gstr1SaveToGstnResetFinal(payload);
				break;
			}
		},

		gstr1SaveToGstnResetFinal: function (payload) {
			this._oDialogSaveStatusBtn.close();
			if (this.vDialogResetBtnFlag) {
				this._oDialogResetBtn.close();
			}

			var oView = this.getView(),
				aMockMessages = [],
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SaveToGstnReset.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length > 0) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].gstin === undefined) {
								var Title = data.resp[i].msg;
								var ogstin = "";
							} else {
								var Title = data.resp[i].gstin;
								var ogstin = data.resp[i].msg;
							}
							aMockMessages.push({
								type: "Success",
								title: Title,
								gstin: ogstin,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
						that.getView().setModel(new JSONModel(aMockMessages), "Msg");
						that.onDialogSavePress();
					}
					// that.bindGetSavetoGstinIntrigration(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1SaveToGstnReset : Error");
				});
			});
		},
		gstr1SaveToGstnResetnonRespondedFinal: function (payload) {

			this._oDialogResetBtn.close();
			this._oDialogSaveStatusBtn.close();
			var oView = this.getView(),
				aMockMessages = [],
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1AspResetNonRespondedDataOnly.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length > 0) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].gstin === undefined) {
								var Title = data.resp[i].msg;
								var ogstin = "";
							} else {
								var Title = data.resp[i].gstin;
								var ogstin = data.resp[i].msg;
							}
							aMockMessages.push({
								type: "Success",
								title: Title,
								gstin: ogstin,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
						that.getView().setModel(new JSONModel(aMockMessages), "Msg");
						that.onDialogSavePress();
					}
					// that.bindGetSavetoGstinIntrigration(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1AspResetNonRespondedDataOnly : Error");
				});
			});
		},

		gstr1SaveToGstnResetAndSaveJob: function () {
			if (this.ResetFlag === "P") {
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices(),
					flag1 = false,
					flag2 = false,
					aGstin = oSelectedItem.map(function (e) {
						if (oModelForTab1[e].authToken == "Active") {
							flag1 = true;
						}
						if (oModelForTab1[e].authToken == "Inactive") {
							flag2 = true;
						}
						return oModelForTab1[e].gstin;
					});
				var payload = {
					"req": {
						"gstins": aGstin,
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
						// "isNilUserInput": oSummaryData.nilUserInput,
						// "isHsnUserInput": oSummaryData.hsnUserInput
					}
				};
			} else {
				var oSummaryData = this.byId("dpGstr1Summary").getModel("ProcessSummary").getProperty("/");
				// oRadioBtn = sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex(),
				payload = {
					"req": {
						"gstins": [sap.ui.getCore().byId("slSummaryGstin11").getSelectedKey()],
						"retPeriod": sap.ui.getCore().byId("id_TaxProcess12").getValue(),
						"tableSections": [],
						// "isResetSave": (oRadioBtn === 0), // isResetSave
						"isNilUserInput": oSummaryData.nilUserInput,
						"isHsnUserInput": oSummaryData.hsnUserInput
					}
				};
			}
			var oModelSave = this.getView().getModel("GetSection").getProperty("/");

			if (oModelSave.b2bFlag) {
				payload.req.tableSections.push("B2B");
			}
			if (oModelSave.b2baFlag) {
				payload.req.tableSections.push("B2BA");
			}
			if (oModelSave.b2clFlag) {
				payload.req.tableSections.push("B2CL");
			}
			if (oModelSave.b2claFlag) {
				payload.req.tableSections.push("B2CLA");
			}
			if (oModelSave.b2csFlag) {
				payload.req.tableSections.push("B2CS");
			}
			if (oModelSave.b2csaFlag) {
				payload.req.tableSections.push("B2CSA");
			}
			if (oModelSave.cdnrFlag) {
				payload.req.tableSections.push("CDNR");
			}
			if (oModelSave.cdnraFlag) {
				payload.req.tableSections.push("CDNRA");
			}
			if (oModelSave.cdnurFlag) {
				payload.req.tableSections.push("CDNUR");
			}
			if (oModelSave.cdnuraFlag) {
				payload.req.tableSections.push("CDNURA");
			}
			if (oModelSave.ecom15Flag) {
				payload.req.tableSections.push("Ecomsup");
			}
			if (oModelSave.ecom15iFlag) {
				payload.req.tableSections.push("ECOM[15(I) & 15(III)]");
			}
			if (oModelSave.ecom15iiFlag) {
				payload.req.tableSections.push("ECOM[15(II) & 15(IV)]");
			}
			if (oModelSave.docissFlag) {
				payload.req.tableSections.push("DOCISS");
			}
			if (oModelSave.supp14Flag) {
				payload.req.tableSections.push("Supecom");
			}
			if (oModelSave.supp14AFlag) {
				payload.req.tableSections.push("SupecomA");
			}
			if (oModelSave.expFlag) {
				payload.req.tableSections.push("EXP");
			}
			if (oModelSave.expaFlag) {
				payload.req.tableSections.push("EXPA");
			}
			if (oModelSave.hsnsumFlag) {
				payload.req.tableSections.push("HSNSUM");
			}
			if (oModelSave.nilFlag) {
				payload.req.tableSections.push("NIL");
			}
			if (oModelSave.txpFlag) {
				payload.req.tableSections.push("TXP");
			}
			if (oModelSave.txpaFlag) {
				payload.req.tableSections.push("TXPA");
			}
			if (oModelSave.atFlag) {
				payload.req.tableSections.push("AT");
			}
			if (oModelSave.ataFlag) {
				payload.req.tableSections.push("ATA");
			}
			if (payload.req.tableSections.length === 0) {
				MessageBox.information("Please select at-least one section");
				return;
			}
			MessageBox.warning("Warning: The E-invoice data will be deleted in case you have not performed E-invoice recon!", {
				actions: ["Okay", "Back"],
				onClose: function (sAction) {
					if (sAction === "Okay") {
						if (this.ResetFlag === "P") {
							if (flag1 == true && flag2 == true) {
								MessageBox.information("Auth token for few GSTINs is inactive, please activate to initiate Save GSTR-1", {
									actions: ["Yes", "No"],
									emphasizedAction: "Yes",
									onClose: function (sAction) {
										if (sAction === "Yes") {
											this.gstr1SaveToGstnResetAndSaveJobFinal(payload);
										}
									}.bind(this)
								});
							} else {
								this.gstr1SaveToGstnResetAndSaveJobFinal(payload);
							}
						} else {
							this.gstr1SaveToGstnResetAndSaveJobFinal(payload);
						}
					}
				}.bind(this)
			});
		},

		gstr1SaveToGstnResetAndSaveJobFinal: function (payload) {

			this._oDialogSaveStatusBtn.close();
			var oView = this.getView(),
				aMockMessages = [],
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SaveToGstnResetAndSaveJob.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.resp.length > 0) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].gstin === undefined) {
								var Title = data.resp[i].msg;
								var ogstin = "";
							} else {
								var Title = data.resp[i].gstin;
								var ogstin = data.resp[i].msg;
							}

							aMockMessages.push({
								type: "Success",
								title: Title,
								gstin: ogstin,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}

						that.getView().setModel(new JSONModel(aMockMessages), "Msg");
						that.onDialogSavePress();
					}
					// that.bindGetSavetoGstinIntrigration(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1SaveToGstnResetAndSaveJob : Error");
				});
			});
		},

		onDialogSavePress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Save GSTR-1",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							this._getSaveGstnStatus();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onPressRefreshBtn: function () {
			this.getGstr1ASucessStatusDataFinal();
		},

		//=======================================================================================
		//----------------------- Code Start Added by Ram on 11-11-2020 ------------------------
		//=======================================================================================

		onChangeFYELS: function (oEvent) {
			var vKey = oEvent.getSource().getSelectedKey(),
				cYear = new Date().getFullYear(),
				vFrDt = new Date(+vKey, 3, 1);

			if (+vKey === cYear) {
				var m = new Date().getMonth(),
					vToDt = new Date(vKey, m, 1);
			} else {
				vToDt = new Date(+vKey + 1, 2, 1);
			}
			vToDt.setMonth(vToDt.getMonth() + 1);
			vToDt.setDate(vToDt.getDate() - 1);

			this.byId("idFromPeriodELS").setMinDate(vFrDt);
			this.byId("idFromPeriodELS").setMaxDate(vToDt);
			this.byId("idFromPeriodELS").setDateValue(vFrDt);

			this.byId("idToPeriodELS").setMinDate(vFrDt);
			this.byId("idToPeriodELS").setMaxDate(vToDt);
			this.byId("idToPeriodELS").setDateValue(vToDt);
		},

		/**
		 * Developed by: Ram Sundar
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onChangeDateFilterValue: function (oEvent) {

			var vFrDtId = oEvent.getSource().getId();
			if (vFrDtId.includes("idocFromDate")) {
				var vDatePicker = "idocToDate";
			} else if (vFrDtId.includes("idFromPeriodELS")) {
				vDatePicker = "idToPeriodELS";
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
		//=======================================================================================
		//----------------------- Code Start Added by Ram on 11-11-2020 ------------------------
		//=======================================================================================
		//=======================================================================================
		//----------------------- Code Start Added by Sarva reg:- E-inv Changes on 11-11-2020 ------------------------
		//=======================================================================================
		onPressGetGst1Einv: function (oEvent, flag) {

			if (!this._oDialog1) {
				this._oDialog1 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.InvoiceStatus", this);
				this.getView().addDependent(this._oDialog1);
			}

			if (flag === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
				var oData = {
					"P": true
				};
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				sap.ui.getCore().byId("id_TaxProcesseInv").setValue(this.oView.byId("dtProcessed").getValue());
				var oTaxPeriod = this.oView.byId("dtProcessed").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var postData = {
					// "req": [{
					// 	"gstin": aGstin,
					// 	"ret_period": oTaxPeriod
					// }]

					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				};
				this.postpayloadgstr1 = postData;
				this.getGstr1ASucessStatusDataFinalEinv();
				this.vPSFlag = "P";
				this._oDialog1.open();

			} else {
				var oData = {
					"P": false
				};
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				sap.ui.getCore().byId("id_TaxProcesseInv").setValue(this.oView.byId("dtSummary").getValue());
				sap.ui.getCore().byId("id_gstineInv").setText(this.oView.byId("slSummaryGstin").getSelectedKey());
				var oTaxPeriod = this.oView.byId("dtSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					// "req": [{
					// 	"gstin": [this.getView().byId("slSummaryGstin").getSelectedKey()],
					// 	"ret_period": oTaxPeriod
					// }]

					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": [this.getView().byId("slSummaryGstin").getSelectedKey()]
					}
				};
				this.postpayloadgstr1 = postData;
				this.getGstr1ASucessStatusDataFinalEinv();
				this.vPSFlag = "S";
				this._oDialog1.open();
			}
			// var oSource = oEvent.getSource();
		},

		onCloseProcEinvDialog: function () {
			this._oDialog1.close();
		},

		onChangeSegmentProcessStatusEinv: function (oEvent) {

			// var oView = this.getView();
			if (oEvent.getSource().getSelectedKey() === "LCS") {
				sap.ui.getCore().byId("idgetEinvoice").setVisible(true);
				sap.ui.getCore().byId("idGetSucessEinvoice").setVisible(false);
				sap.ui.getCore().byId("idgetVtablegstr1Einv").setVisible(true);
				sap.ui.getCore().byId("idgetVtablegst1proEinvLast").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "LSS") {
				sap.ui.getCore().byId("idgetEinvoice").setVisible(false);
				sap.ui.getCore().byId("idGetSucessEinvoice").setVisible(true);
				sap.ui.getCore().byId("idgetVtablegstr1Einv").setVisible(false);
				sap.ui.getCore().byId("idgetVtablegst1proEinvLast").setVisible(true);
			}
		},

		onPressRefreshBtnEINV: function () {
			this.getGstr1ASucessStatusDataFinalEinv();
		},

		getGstr1ASucessStatusDataFinalEinv: function () {

			var oView = this.getView();
			var that = this;
			// url: "/aspsapapi/Gstr1EInvGstnGetSection.do",
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1EPopUpRecords.do",
					contentType: "application/json",
					data: JSON.stringify(that.postpayloadgstr1)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					that.bindGstr1DetailStatusEinv(data);
					// var oGstr6ASucessData = new sap.ui.model.json.JSONModel(data);
					// oView.setModel(oGstr6ASucessData, "Gstr6ASucess");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr1EPopUpRecords : Error");
				});
			});
		},

		bindGstr1DetailStatusEinv: function (data) {

			var oView = this.getView();
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = false;
				data.resp.lastCall[i].cdnrFlag = false;
				data.resp.lastCall[i].cdnurFlag = false;
				data.resp.lastCall[i].exportsFlag = false;
				data.resp.lastCall[i].lsFlag = false;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = false;
				data.resp.lastSuccess[i].cdnrFlag = false;
				data.resp.lastSuccess[i].cdnurFlag = false;
				data.resp.lastSuccess[i].exportsFlag = false;
				data.resp.lastSuccess[i].lsFlag = false;
			}
			this._showing(false);
			var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr1ASucessData, "Gstr1ASucess");
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onRowSelectionChangeEINV: function (oEvent) {

			var data = this.getView().getModel("Gstr1ASucess").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			var oBtProcess = sap.ui.getCore().byId("idProcessEinv").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlagEINV(true);
				this._showingEINV(true);
			} else if (vRowIndex === -1) {
				this._setFlagEINV(false);
				this._showingEINV(false);
			} else {

				if (oBtProcess === "LCS") {
					if (data.resp.lastCall[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}

					data.resp.lastCall[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnrFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnurFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].exportsFlag = oSelectedFlag;

					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				} else {
					if (data.resp.lastSuccess[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastSuccess[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnrFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnurFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].exportsFlag = oSelectedFlag;

					data.resp.lastSuccess[vRowIndex].lsFlag = oSelectedFlag;
				}
			}
			this.getView().getModel("Gstr1ASucess").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_setFlagEINV: function (oSelectedFlag) {
			var data = this.getView().getModel("Gstr1ASucess").getData();
			var oBtProcess = sap.ui.getCore().byId("idProcessEinv").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					data.resp.lastCall[i].b2bFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnrFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnurFlag = oSelectedFlag;
					data.resp.lastCall[i].exportsFlag = oSelectedFlag;

					data.resp.lastCall[i].lsFlag = oSelectedFlag;
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnrFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnurFlag = oSelectedFlag;
					data.resp.lastSuccess[i].exportsFlag = oSelectedFlag;
					data.resp.lastSuccess[i].lsFlag = oSelectedFlag;
				}
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		_showingEINV: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.cdnrFlag = oSelectedFlag;
			oData.cdnurFlag = oSelectedFlag;
			oData.exportsFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @param {Object} oEvent Eventing object
		 */
		onPressDownloadReportsEINV: function (oEvent) {

			var oBtProcess = sap.ui.getCore().byId("idProcessEinv").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegstr1Einv").getModel("Gstr1ASucess").getData().resp;
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {

					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"status": "lastCall",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastCall[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr1EinvSections": []
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr1EinvSections.push("einv_b2b");
					}
					if (oModelForTab1.lastCall[i].cdnrFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnr");
					}
					if (oModelForTab1.lastCall[i].cdnurFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnur");
					}
					if (oModelForTab1.lastCall[i].exportsFlag) {
						postData.req[i].gstr1EinvSections.push("einv_exp");
					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1EinvSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/downloadGstr1InvoicesReport.do";
				this.excelDownload(postData, url);
			} else {
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegst1proEinvLast").getModel("Gstr1ASucess").getData().resp;

				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"status": "LastSuccess",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr1EinvSections": []
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr1EinvSections.push("einv_b2b");
					}
					if (oModelForTab1.lastSuccess[i].cdnrFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnr");
					}
					if (oModelForTab1.lastSuccess[i].cdnurFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnur");
					}
					if (oModelForTab1.lastSuccess[i].exportsFlag) {
						postData.req[i].gstr1EinvSections.push("einv_exp");
					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1EinvSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				url = "/aspsapapi/downloadGstr1InvoicesReport.do";
				this.excelDownload(postData, url);
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onPressGetBtnEINV: function (oEvent) {

			var oBtProcess = sap.ui.getCore().byId("idProcessEinv").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegstr1Einv").getModel("Gstr1ASucess").getData().resp;
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastCall[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr1EinvSections": []
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr1EinvSections.push("einv_b2b");
					}
					if (oModelForTab1.lastCall[i].cdnrFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnr");
					}
					if (oModelForTab1.lastCall[i].cdnurFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnur");
					}
					if (oModelForTab1.lastCall[i].exportsFlag) {
						postData.req[i].gstr1EinvSections.push("einv_exp");
					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1EinvSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSectionButtonIntrigrationFinalEINV(postData);
			} else {
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegst1proEinvLast").getModel("Gstr1ASucess").getProperty("/resp"),
					oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr1EinvSections": []
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr1EinvSections.push("einv_b2b");
					}

					if (oModelForTab1.lastSuccess[i].exportsFlag) {
						postData.req[i].gstr1EinvSections.push("einv_exp");
					}
					if (oModelForTab1.lastSuccess[i].cdnrFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnr");
					}
					if (oModelForTab1.lastSuccess[i].cdnurFlag) {
						postData.req[i].gstr1EinvSections.push("einv_cdnur");
					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr1EinvSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSectionButtonIntrigrationFinalEINV(postData);
			}
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		getSectionButtonIntrigrationFinalEINV: function (postData) {

			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr1EInvGstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							aMockMessages.push({
								type: "Success",
								title: data.resp[i].gstin,
								gstin: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
					}
					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogPressEIN();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr1GstnGetSection : Error");
				});
			});
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onDialogPressEIN: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get EINV Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.getGstr1ASucessStatusDataFinalEinv();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onSignFile: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			if (key === "DSC") {
				this.DSCEVCflag = "DSC"
			} else {
				this.DSCEVCflag = "EVC"
			}
			this.onSignFileDSC();
		},

		onSignFileDSC: function () {
			var that = this;
			if (this.authState === "I") {
				MessageBox.show(
					"Auth token is inactive for selected GSTIN, please activate and retry.", {
						icon: MessageBox.Icon.WARNING,
						title: "Warning"
					});
				return;
			}

			if (!this._oDialogSaveStats56) {
				this._oDialogSaveStats56 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.SignandFile", this);
				this.getView().addDependent(this._oDialogSaveStats56);
			}

			// //var that = this;
			// var gstin;
			// if (this.byId("linkGSTID").getSelectedKey() === "") {
			// 	gstin = this.gstin;
			// } else {
			// 	gstin = this.byId("linkGSTID").getSelectedKey();
			// }
			var Request = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKey()
				}
			};

			var BulkGstinSaveModel = new JSONModel();
			var GstnsList = "/aspsapapi/get3BSignAndFilePanDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					//var sts = data.hdr;
					//	if (sts !== undefined) {
					that._oDialogSaveStats56.open();
					if (that.DSCEVCflag === "DSC") {
						data.resp.header = "Please select the authorised signatory for which DSC is attached for initiating filing of GSTR-1"
					} else {
						data.resp.header = "Please select the authorised signatory for which EVC is attached for initiating filing of GSTR-1"
					}
					BulkGstinSaveModel.setData(data.resp);
					that.getView().setModel(BulkGstinSaveModel, "SignandFile");
					//} /*else {
					//	MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
					//}*/
				}).fail(function (jqXHR, status, err) {
					//MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
				});
			});
		},

		onSaveClose: function () {
			this._oDialogSaveStats56.close();
		},

		onSaveSign: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			if (sel === null) {
				MessageBox.error("Please select an Authorised signatory for initiating filing");
				return;
			}

			var that = this;
			MessageBox.show(
				"Do you want to proceed with filing GSTR-1?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//that.onoffLiaSaveChanges();

							if (that.DSCEVCflag === "DSC") {
								that.onSaveSign2();
							} else {
								that.onSaveSignEVCConformation();
							}
						}
					}
				});

			/**/
		},

		onSignFile123: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			if (sel === null) {
				MessageBox.error("Please select an Authorised signatory for initiating filing");
				return;
			}

			var that = this;
			MessageBox.show(
				"Do you want to proceed with filing GSTR-1?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//that.onoffLiaSaveChanges();
							if (that.DSCEVCflag === "DSC") {
								that.onSaveSign2();
							} else {
								that.onSaveSignEVCConformation();
							}
							var searchInfo = that._summarySearchInfo(that.byId("slSummaryGstin").getSelectedKey());
							that._getProcessSummary(searchInfo, true);
						}
					}
				});
		},

		onSaveSign2: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			// var gstin;
			// if (this.byId("linkGSTID").getSelectedKey() === "") {
			// 	gstin = this.gstin;
			// } else {
			// 	gstin = this.byId("linkGSTID").getSelectedKey();
			// }

			var Request = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"taxPeriod": this.byId("dtSummary").getValue(),
					"pan": selItem
				}
			};

			var GstnsList = "/aspsapapi/execSignAndFileStage1.do";
			// var GstnsList = "/aspsapapi/GSTR3BSignAndFileStage1.do";

			this.excelDownload(Request, GstnsList);
			this._oDialogSaveStats56.close();
			var that = this;
			MessageBox.show(
				"GSTR1 filing is initiated, click on filing status to view the status", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					onClose: function (oAction) {
						//that.onSignFile1();
						that.onSearch();
					}
				});
		},

		onSaveSignEVCConformation: function () {

			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			// var gstin;
			// if (this.byId("linkGSTID").getSelectedKey() === "") {
			// 	gstin = this.gstin;
			// } else {
			// 	gstin = this.byId("linkGSTID").getSelectedKey();
			// }

			var date = this.byId("dtSummary").getValue();
			var taxPeriod = date.slice(0, 2) + "/" + date.slice(2, 6);
			var Request = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"taxPeriod": taxPeriod,
					"pan": selItem
				}
			};
			this.ReqPayload = Request;
			if (!this._oDialogSaveStatsConfirm) {
				this._oDialogSaveStatsConfirm = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr1.SignandFileConformation", this);
				this.getView().addDependent(this._oDialogSaveStatsConfirm);
			}
			this.getView().setModel(new JSONModel(Request.req), "confirmval");
			this._oDialogSaveStats56.close();
			this._oDialogSaveStatsConfirm.open();
		},
		onPopupCancel: function () {
			this._oDialogSaveStatsConfirm.close();
		},

		onPopupConfirm: function () {

			var that = this;
			var RequestPayload = {
				"req": {
					"gstin": this.ReqPayload.req.gstin,
					"taxPeriod": this.byId("dtSummary").getValue(),
					"pan": this.ReqPayload.req.pan,
					"returnType": "gstr1"
				}
			};

			var GstnsList = "/aspsapapi/evcSignAndFileStage1.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts !== undefined) {
						if (sts.status === "S") {
							// MessageBox.success(data.resp);
							//that.onSearch();
							that._getSignandFileOTP(data)
							that.signId = data.resp.signId
							that._oDialogSaveStatsConfirm.close();
						} else {
							MessageBox.error(data.resp);
							// that._getSignandFileOTP(data)
						}
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
						// that._getSignandFileOTP(data)
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					//MessageBox.error("Error Occured While Saving the Changes");
				});
			});

		},

		_getSignandFileOTP: function (data) {
			if (!this._oDialogSignandFileOTP) {
				this._oDialogSignandFileOTP = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr1.SignandFileOTP", this);
				this.getView().addDependent(this._oDialogSignandFileOTP);
			}
			this.byId("otpValue").setValue();
			this._oDialogSignandFileOTP.open();
		},
		onPopupOTPCancel: function () {
			this._oDialogSignandFileOTP.close();
		},

		onPopupOTPSign: function () {

			var otp = this.byId("otpValue").getValue();
			if (otp === "") {
				MessageBox.error("Please add OTP");
				return;
			}

			var that = this;
			var RequestPayload = {
				"req": {
					"signId": this.signId,
					"otp": otp,
				}
			}

			var GstnsList = "/aspsapapi/evcSignAndFileStage2.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data === true) {
						MessageBox.success("Return Filing has been initiated successfully");
					} else {
						MessageBox.error(data.resp);
					}
					that._oDialogSignandFileOTP.close();

					// var sts = data.hdr;
					// if (sts !== undefined) {
					// 	if (sts.status === "S") {
					// 		MessageBox.success(data.resp);
					// 		that._oDialogSignandFileOTP.close();
					// 		//that.onSearch();
					// 		// that._getSignandFileOTP(data)
					// 	} else {
					// 		MessageBox.error(data.resp);
					// 	}
					// } else {
					// 	MessageBox.error(JSON.parse(data).hdr.message);
					// }
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					//MessageBox.error("Error Occured While Saving the Changes");
				});
			});

		},

		onFilingStatus: function () {
			if (!this._oDialogSaveStats9) {
				this._oDialogSaveStats9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.FilingStatus", this);
				this.getView().addDependent(this._oDialogSaveStats9);
			}

			var gstin = this.byId("slSummaryGstin").getSelectedKey();
			var vDate = new Date();
			this.byId("FStp").setMaxDate(vDate);
			this.byId("FStp").setValue(this.byId("dtSummary").getValue());
			this.byId("FsGstin").setSelectedKey(gstin);
			this.onSaveOkay12();
			this._oDialogSaveStats9.open();
		},

		onCloseDialogFS: function () {
			this._oDialogSaveStats9.close();
		},

		onSaveOkay12: function () {
			var that = this;
			var req = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": this.byId("FStp").getValue()
				}
			};
			var GstinSaveModel = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr1FilingStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					/*for (var i = 0; i < data.resp.details.length; i++) {
						data.resp.details[i].sNo = i + 1;
					}*/
					GstinSaveModel.setData(data.resp);
					that.getView().byId("idTableFS").setModel(GstinSaveModel, "TableFS");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onSaveStatusDownload1: function (oEvt) {
			this.oReqId1 = oEvt.getSource().getParent().getBindingContext("TableFS").getObject().refId;
			var oReqExcelPath = "/aspsapapi/downloadGstr1Errors.do?id=" + this.oReqId1 + "";
			window.open(oReqExcelPath);
		},

		/**
		 * Developed by: Sarvamangala
		 * @memberOf com.ey.digigst.view.GSTR1
		 */
		onSelectAllCheckHeaderEINV: function (oEvent, field) {

			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.getView().getModel("Gstr1ASucess").getData();
			var oBtProcess = sap.ui.getCore().byId("idProcessEinv").getSelectedKey();
			// field = JSON.parse(field);
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastCall[i].b2bFlag = oSelectedFlag;
						break;
					case "exportsFlag":
						data.resp.lastCall[i].exportsFlag = oSelectedFlag;
						break;

					case "cdnrFlag":
						data.resp.lastCall[i].cdnrFlag = oSelectedFlag;
						break;

					case "cdnurFlag":
						data.resp.lastCall[i].cdnurFlag = oSelectedFlag;
						break;

					default:
						// code block
					}
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
						break;

					case "exportsFlag":
						data.resp.lastSuccess[i].exportsFlag = oSelectedFlag;
						break;

					case "cdnrFlag":
						data.resp.lastSuccess[i].cdnrFlag = oSelectedFlag;
						break;

					case "cdnurFlag":
						data.resp.lastSuccess[i].cdnurFlag = oSelectedFlag;
						break;

					default:
						// code block
					}
				}
			}
			this.getView().getModel("Gstr1ASucess").refresh();
		},
		onPressdeleteAuotodreaftedfull: function (oEvent, flag) {

			var that = this;
			if (flag === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Please Select at least one Gstin");
					return;
				}
				var flag1 = false;
				var flag2 = false;
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
					if (oModelForTab1[oSelectedItem[i]].authToken == "Active") {
						flag1 = true;

					}
					if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
						flag2 = true;

					}
				}
				var payload = {
					"req": {
						"gstins": aGstin,
						"retPeriod": that.byId("dtProcessed").getValue(),
					}
				};
				if (flag1 == true && flag2 == true) {
					MessageBox.show(
						"Auth token for few GSTINs is inactive,hence GSTR-1 data available on GST common portal will be deleted for GSTINs for which auth token is active. Do you want to proceed?", {
							icon: MessageBox.Icon.INFORMATION,
							title: "Information",
							actions: ["Yes", "No"],
							emphasizedAction: "Yes",
							onClose: function (sAction) {
								if (sAction === "Yes") {
									// that.onPressdeleteAuotodreaftedfullFinal(payload);
									that.onDeleteProcessScreenGstr1(payload);

								}

							}
						}
					);

				} else {
					this.onDeleteProcessScreenGstr1(payload);
				}

			} else {
				var payload = {
					"req": {
						"gstins": [that.byId("slSummaryGstin").getSelectedKey()],
						"retPeriod": that.byId("dtSummary").getValue(),
					}
				};
				this.onDeleteProcessScreenGstr1(payload);

			}

		},
		onDeleteProcessScreenGstr1: function (payload) {
			var that = this;
			MessageBox.show(
				"GSTR-1 data available on GST common portal will be deleted. Do you want to proceed?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: ["Yes", "No"],
					emphasizedAction: "Yes",
					onClose: function (sAction) {
						if (sAction === "Yes") {
							MessageBox.show(
								"Please note - API call to delete data from GST Portal will be initiated. Do you want to proceed with deletion?", {
									icon: MessageBox.Icon.INFORMATION,
									title: "Information",
									actions: ["Proceed", "Cancel"],
									emphasizedAction: "Proceed",
									onClose: function (sAction) {
										if (sAction === "Proceed") {
											that.onPressdeleteAuotodreaftedfullFinal(payload);

										} else {}

									}
								}
							);
							// that.onPressdeleteAuotodreaftedfullFinal(payload);

						} else {}

					}
				}
			);

		},
		onPressdeleteAuotodreaftedfullFinal: function (payload) {

			var oView = this.getView(),
				aMockMessages = [],
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1GstnResetJob.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length >= 0) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.hdr.status === "S") {
								MessageBox.success(
									data.resp[i].msg);

							} else {
								MessageBox.error(data.resp[i].msg);

							}

						}
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1GstnResetJob : Error");
				});
			});
		},
		//=======================================================================================
		//----------------------- Code Start Ended by Sarva reg:- E-inv Changes on 11-11-2020 ------------------------
		//=======================================================================================

		onUpdateGstr1Summ: function () {
			var gstin = this.byId("tabGstr1Process").getSelectedIndices();
			if (!gstin.length) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Update GSTN Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onUpdateGstr1Summ1();
					}
				}.bind(this)
			});
		},

		onUpdateGstr1Summ1: function () {
			var oModelData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
				oPath = this.getView().byId("tabGstr1Process").getSelectedIndices(),
				aGSTIN = oPath.map(function (e) {
					return oModelData[e].gstin;
				}),
				payload = {
					"req": {
						"retPeriod": this.byId("dtProcessed").getValue(),
						"gstinList": aGSTIN
					}
				};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1BulkGetSumm.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						if (!this._dGstr1Msg) {
							this._dGstr1Msg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.BulkSave", this);
							this.getView().addDependent(this._dGstr1Msg);
						}
						this._dGstr1Msg.setTitle("Generate GSTR1 Summary Status");
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._dGstr1Msg.open();
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		onCloseDialogBulkSave: function () {
			this._dGstr1Msg.close();
		},

		onPressFetchGstn: function () {
			var gstin = this.byId("tabGstr1Process").getSelectedIndices();
			if (!gstin.length) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Fetch GSTN Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this._fetchBulkGstnData();
					}
				}.bind(this)
			});
		},

		_fetchBulkGstnData: function () {
			var oModelData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getProperty("/"),
				oPath = this.getView().byId("tabGstr1Process").getSelectedIndices(),
				aGSTIN = oPath.map(function (e) {
					return oModelData[e].gstin;
				}),
				payload = {
					"req": {
						"retPeriod": this.byId("dtProcessed").getValue(),
						"gstinList": aGSTIN
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/fetchGstr1BulkGetSumm.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (!this._dGstr1Msg) {
							this._dGstr1Msg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.BulkSave", this);
							this.getView().addDependent(this._dGstr1Msg);
						}
						this._dGstr1Msg.setTitle("Fetch GSTR1 Summary Status");
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._dGstr1Msg.open();
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				}.bind(this));
		},

		onSaveStatusDownload2: function (oEvt) {
			var section = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().section;
			var createdOn = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().createdOn;
			var req = {
				"req": {
					"supplierGstin": this.byId("slSaveStatsGstin").getSelectedKey(),
					"returnPeriod": this.byId("dtSaveStats").getValue(),
					"createdOn": createdOn,
					"section": section
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr1JsonDownloadDocument.do";
			this.excelDownload(req, oReqExcelPath);
		},

		RFA: function () {
			var aGstin = [];
			var vIndices = this.byId("tabGstr1Process").getSelectedIndices(),
				aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < vIndices.length; i++) {
				aGstin.push(aData[vIndices[i]].gstin);
			}
			if (!this._Filter) {
				this._Filter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.RFA", this);
				this.getView().addDependent(this._Filter);
			}

			var oIntiData = {
				"req": {
					"entityId": $.sap.entityID,
					"retType": "GSTR1",
					"gstins": aGstin
				}
			};
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/getApprovalRequestData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						/*Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");*/
						//var retArr = [];
						if (typeof data.resp.gstinInfo === 'object') {
							for (var i = 0; i < data.resp.gstinInfo.length; i++) {
								data.resp.gstinInfo[i].Comments = "";
								data.resp.gstinInfo[i].requestChecker = [];
								data.resp.gstinInfo[i].requestedFor = [];

							}
							var oRequestApp = new JSONModel(data.resp.gstinInfo);
							View.setModel(oRequestApp, "RequestAprvoldata");
						} else {
							var oRequestApp1 = new JSONModel([]);
							View.setModel(oRequestApp1, "RequestAprvoldata");
							MessageBox.information(data.resp.gstinInfo);
						}

					} else {
						var oRequestApp1 = new JSONModel([]);
						View.setModel(oRequestApp1, "RequestAprvoldata");
					}
				}).fail(function (jqXHR, status, err) {
					var oRequestApp = new JSONModel([]);
					View.setModel(oRequestApp, "RequestAprvoldata");
				});
			});
			this._Filter.open();

		},
		onPressRequestMadeTo: function (oEvt) {
			//sap.m.MessageToast.show("Test");
			var requestMade = [];
			var TabData = this.getView().getModel("RequestAprvolStatus").getData();
			var reqestMade = oEvt.getSource().getEventingParent().getParent().getBindingContext("RequestAprvolStatus").getObject().requestId;
			for (var i = 0; i < TabData.length; i++) {
				//for(var j=0;j<TabData[i].requestMadeTo)
				if (reqestMade === TabData[i].requestId) {
					requestMade.push(TabData[i].requestMadeTo);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(requestMade[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "requestMadeTo");
			this._oGstinPopover.openBy(oButton);
		},
		onPressRequestCheckers: function (oEvt) {
			var requestMade = [];
			var TabData = this.getView().getModel("RequestAprvoldata").getData();
			var reqestMade = oEvt.getSource().getEventingParent().getParent().getBindingContext("RequestAprvoldata").getObject().gstin;
			for (var i = 0; i < TabData.length; i++) {
				//for(var j=0;j<TabData[i].requestMadeTo)
				for (var j = 0; j < TabData[i].checkerMailIds.length; j++)
					if (reqestMade === TabData[i].gstin) {
						requestMade.push(TabData[i].checkerMailIds[j].userEmailStr);
					}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(requestMade);

			var oButton = oEvt.getSource();
			if (!this._oRequestCheckers) {
				this._oRequestCheckers = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.PopoverRequestCheckers",
					this);
				this.getView().addDependent(this._oRequestCheckers);
			}
			this._oRequestCheckers.setModel(oReqWiseModel1, "requestCheckers");
			this._oRequestCheckers.openBy(oButton);
		},

		RFAS: function () {
			var aGstin = [];
			var vIndices = this.byId("tabGstr1Process").getSelectedIndices(),
				aData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < vIndices.length; i++) {
				aGstin.push(aData[vIndices[i]].gstin);
			}

			if (!this._Filter1) {
				this._Filter1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.RFAS", this);
				this.getView().addDependent(this._Filter1);
			}
			var oIntiData = {
				"req": {

					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"retType": "GSTR1",
					"gstins": aGstin
				}
			};
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/approvalRequestSummaryData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						/*Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");*/
						//var retArr = [];
						if (typeof data.resp.requestData === 'object') {
							var oRequestApp = new JSONModel(data.resp.requestData);
							View.setModel(oRequestApp, "RequestAprvolStatus");
						} else {
							var oRequestApp1 = new JSONModel([]);
							View.setModel(oRequestApp1, "RequestAprvolStatus");
							MessageBox.information(data.resp.requestData);
						}
					} else {
						var oRequestApp1 = new JSONModel([]);
						View.setModel(oRequestApp1, "RequestAprvolStatus");
					}
				}).fail(function (jqXHR, status, err) {
					var oRequestApp = new JSONModel([]);
					View.setModel(oRequestApp, "RequestAprvolStatus");
				});
			});

			this._Filter1.open();
		},
		RPS: function () {
			if (!this._Filter) {
				this._Filter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.RPS", this);
				this.getView().addDependent(this._Filter);
			}
			this._Filter.open();
		},

		onsubmitRFA: function () {
			var aGstin = [];
			var vIndices = this.byId("gstrTabIdApp").getSelectedIndices(),
				aData = this.byId("gstrTabIdApp").getModel("RequestAprvoldata").getData();
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < vIndices.length; i++) {
				aGstin.push({
					gstin: aData[vIndices[i]].gstin,

					selectedCheckers: aData[vIndices[i]].requestChecker,
					requestedFor: aData[vIndices[i]].requestedFor,
					comments: aData[vIndices[i]].Comments
				});
			}

			var oIntiData = {
				"req": {
					"entityId": $.sap.entityID,
					"retType": "GSTR1",
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"requestInfo": aGstin

				}
			};
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/submitApprovalRequest.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						/*Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");*/
						//var retArr = [];
						MessageBox.success(data.resp);

					}
				}).fail(function (jqXHR, status, err) {
					MessageBox.error("submitApprovalRequest : Error");
					// var oRequestApp = new JSONModel([]);
					// View.setModel(oRequestApp, "RequestAprvoldata");
				});
			});
			//aData.forEach(item)
			//	var onSubmitData = [];

		},

		onCloseRFA: function () {
			this._Filter.close();
		},
		onCloseRPA: function () {
			this._Filter.close();
		},

		onCloseRFAS: function () {
			this._Filter1.close();
		},

		onPressInvoiceSerice: function () {
			var req = {
				"req": {
					"entityId": $.sap.entityID,
					"returnPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"gstin": this.byId("slSummaryGstin").getSelectedKey()
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/gstr1DocSeriesAutoComp.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
						that._getSummaryData();
					} else {
						MessageBox.error(data.resp);
					}
					that.lastUpdated("CB");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onPressInvoiceSericeE: function () {
			if (!this._oDialogbulkSaveStats2) {
				this._oDialogbulkSaveStats2 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.BulkSave2", this);
				this.getView().addDependent(this._oDialogbulkSaveStats2);
			}

			var aGSTIN = [];
			var oModelData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
			var oPath = this.byId("tabGstr1Process").getSelectedIndices();
			if (oPath.length === 0) {
				MessageBox.information("Please select atleast one GSTIN");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				aGSTIN.push(oModelData[oPath[i]].gstin);
			}
			var req = {
				"req": {
					"entityId": $.sap.entityID,
					"returnPeriod": this.byId("dtProcessed").getValue(),
					"gstins": aGSTIN
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var BulkGstinSaveModel = new JSONModel();
			var GstnsList = "/aspsapapi/gstr1BulkDocSerAutoComp.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.hdr.status === "S") {
						BulkGstinSaveModel.setData(data.resp);
						that.getView().byId("bulkSaveID2").setModel(BulkGstinSaveModel, "BulkGstinSaveModel2");
						that._oDialogbulkSaveStats2.open();
						that._getProcessedData();
					} else {
						MessageBox.error(data.errMsg);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onCloseDialogBulkSave2: function () {
			this._oDialogbulkSaveStats2.close();
		},

		onFileNILReturn: function () {
			var that = this;
			MessageBox.show("Do you want to proceed to file Nill Return? - No data will be saved from DigiGST to GST portal.", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: ["Yes", "Cancel"],
				emphasizedAction: "Yes",
				onClose: function (sAction) {
					if (sAction === "Yes") {
						that.onFileNILReturn1();
					}
				}
			});
		},

		onFileNILReturn1: function () {
			var req = {
				"req": {
					"action": "NILFILLING",
					"entityId": [$.sap.entityID],
					"taxPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"dataSecAttrs": {
						"GSTIN": [this.byId("slSummaryGstin").getSelectedKey()]
					}
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/gstr1FileNilReturn.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onFileNILReturnP: function () {
			if (!this._oDialogbulkSaveStats1) {
				this._oDialogbulkSaveStats1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.BulkSave1", this);
				this.getView().addDependent(this._oDialogbulkSaveStats1);
			}

			if (!this.Importentmsg) {
				this.Importentmsg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.Importentmsg", this);
				this.getView().addDependent(this.Importentmsg);
			}

			var aGSTIN = [];
			var oModelData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
			var oPath = this.byId("tabGstr1Process").getSelectedIndices();
			if (oPath.length === 0) {
				MessageBox.information("Please select atleast one GSTIN");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				aGSTIN.push(oModelData[oPath[i]].gstin);
			}

			this.Importentmsg.open();
			this.byId("gstId").setText(aGSTIN);
			this.byId("RpID").setText(this.byId("dtProcessed").getValue());
		},

		onPressCloseNR: function () {
			this.Importentmsg.close();
		},

		onPressYesNR: function () {
			this.Importentmsg.close();
			this.onFileNILReturnfinal();
		},

		onFileNILReturnfinal: function () {
			var aGSTIN = [];
			var oModelData = this.byId("tabGstr1Process").getModel("ProcessedRecord").getData();
			var oPath = this.byId("tabGstr1Process").getSelectedIndices();
			if (oPath.length === 0) {
				MessageBox.information("Please select atleast one GSTIN");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				aGSTIN.push(oModelData[oPath[i]].gstin);
			}

			var req = {
				"req": {
					"retPeriod": this.byId("dtProcessed").getValue(),
					"gstinList": aGSTIN
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var BulkGstinSaveModel = new JSONModel();
			var GstnsList = "/aspsapapi/gstr1BulkFileNilReturn.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					BulkGstinSaveModel.setData(data.resp);
					that.getView().byId("bulkSaveID1").setModel(BulkGstinSaveModel, "BulkGstinSaveModel");
					that._oDialogbulkSaveStats1.open();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onCloseDialogBulkSave1: function () {
			this._oDialogbulkSaveStats1.close();
		}

	});
});