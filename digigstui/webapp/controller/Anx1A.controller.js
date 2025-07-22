var oFilterData = [];
var flFlag = false;
sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage"
], function (BaseController, Formatter, JSONModel, Storage) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Anx1A", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Anx-1A
		 */
		onInit: function () {
			var oStorage = new Storage(Storage.Type.session, "digiGst");
			var oEntityData = oStorage.get("entity");
			if (!oEntityData) {
				this.getRouter().navTo("Home");
			} else {
				var oModel = this.getOwnerComponent().getModel("EntityModel");
				oModel.setData(oEntityData);
				oModel.refresh(true);
				this._getDataSecurity(oEntityData[0].entityId, "dpProcessAnx1APage");
				// this._getOtherFilters(oFilterData, "dpProcessAnx1APage");
			}
			var oBundle = this.getResourceBundle();
			var oVisiCol = {
				"colText": oBundle.getText("supplies"),
				"colVisi": true,
				"outward": true,
				"inward": true
			};
			this.byId("dpProcessAnx1APage").setModel(new JSONModel(oVisiCol), "Visi");

			var oVisiSumm = {
				"summary": true,
				"details": false
			};
			this.byId("dpAnx1a").setModel(new JSONModel(oVisiSumm), "VisiSummary");

		},
		_onRouteMatched: function (oEvent) {

		},

		onAfterRendering: function () {

			var vDate = new Date();
			this.byId("drsAnx1aPeriodOn").setMaxDate(vDate);
			this.byId("drsAnx1aPeriodOn").setDateValue(vDate);
			this.byId("drsAnx1aPeriod").setMaxDate(vDate);
			this.byId("drsAnx1aPeriod").setDateValue(vDate);

			// this.onEntityChange();
			this.onSearchAnx1AprocessGo();

		},

		onSearchAnx1AprocessGo: function () {
			 //eslint-disable-line
			// var vEntity = this.getView().byId("slAnx1aEntityDataSave").getSelectedKey();
			var vGstin = this.getView().byId("idfgiGSINDataAnax1").getSelectedKeys();
			var vTaxPeriod = this.getView().byId("drsAnx1aPeriodOn").getValue();
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": vGstin,
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getAnx1Aprocess(searchInfo);

		},

		onSearchAnx1AprocessApply: function () {
			 //eslint-disable-line
			if (flFlag === false) {
				// var vEntity = this.getView().byId("slAnx1aEntityDataSave").getSelectedKey();
				var vGstin = this.getView().byId("idfgiGSINDataAnax1").getSelectedKeys();
				var vTaxPeriod = this.getView().byId("drsAnx1aPeriodOn").getValue();
				var searchInfo = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": vGstin,
							"PC": this.byId("slProfitCtrAnx1A").getSelectedKeys(),
							"Plant": this.byId("slPlantAnx1A").getSelectedKeys(),
							"D": this.byId("slDivisionAnx1A").getSelectedKeys(),
							"L": this.byId("slLocationAnx1A").getSelectedKeys(),
							"SO": this.byId("slSalesOrgAnx1A").getSelectedKeys(),
							"DC": this.byId("slDistrChannelAnx1A").getSelectedKeys(),
							"UD1": this.byId("slUserAccess1Anx1A").getSelectedKeys(),
							"UD2": this.byId("slUserAccess2Anx1A").getSelectedKeys(),
							"UD3": this.byId("slUserAccess3Anx1A").getSelectedKeys(),
							"UD4": this.byId("slUserAccess4Anx1A").getSelectedKeys(),
							"UD5": this.byId("slUserAccess5Anx1A").getSelectedKeys(),
							"UD6": this.byId("slUserAccess6Anx1A").getSelectedKeys()
						}
					}
				};
				this.getAnx1Aprocess(searchInfo);
				this._getOtherFilters(oFilterData, "dpProcessAnx1APage");
			} else {
				// var vEntity = this.getView().byId("slAnx1aSummEntity").getSelectedKey();
				var vGstin = this.getView().byId("id_gstinMultiBox").getSelectedKeys();
				var vTaxPeriod = this.getView().byId("drsAnx1aPeriod").getValue();

				var searchInfoRev = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": vGstin,
							"PC": this.byId("slProfitCtrAnx1A").getSelectedKeys(),
							"Plant": this.byId("slPlantAnx1A").getSelectedKeys(),
							"D": this.byId("slDivisionAnx1A").getSelectedKeys(),
							"L": this.byId("slLocationAnx1A").getSelectedKeys(),
							"SO": this.byId("slSalesOrgAnx1A").getSelectedKeys(),
							"DC": this.byId("slDistrChannelAnx1A").getSelectedKeys(),
							"UD1": this.byId("slUserAccess1Anx1A").getSelectedKeys(),
							"UD2": this.byId("slUserAccess2Anx1A").getSelectedKeys(),
							"UD3": this.byId("slUserAccess3Anx1A").getSelectedKeys(),
							"UD4": this.byId("slUserAccess4Anx1A").getSelectedKeys(),
							"UD5": this.byId("slUserAccess5Anx1A").getSelectedKeys(),
							"UD6": this.byId("slUserAccess6Anx1A").getSelectedKeys()
						}
					}
				};
				this.onPressAnx1aSummaryFInal(searchInfoRev);
			}
		},

		getAnx1Aprocess: function (searchInfo) {
			var that = this;
			that.byId("tabAnx1aProcess").setModel(new JSONModel(""), "ProcessRecordAnx1A");
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAnx1aProcessedRecords.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpProcessAnx1APage").setModel(new JSONModel(data.resp), "ProcessSummaryAnx1a");
				that.byId("tabAnx1aProcess").setModel(new JSONModel(data.resp), "ProcessRecordAnx1A");
				// that.getHeaderSummaryData(data);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});

		},
		// onEntityChange: function (oEvent) {
		// 	 //eslint-disable-line
		// 	if (oEvent.getSource().getId().includes("slAnx1aEntityDataSave")) {
		// 		var vPage = "dpProcessAnx1APage";

		// 	} else if (oEvent.getSource().getId().includes("slAnx1aSummEntity")) {
		// 		vPage = "dpAnx1a";
		// 	}
		// 	var vEntityId = oEvent.getSource().getSelectedKey();
		// 	this._getDataSecurity(vEntityId, vPage);

		// },

		// onEntityChange: function () {
		// 	var oEntitySum = this.getView().byId("slAnx1aEntityDataSave").getSelectedKey();
		// 	this._getDataSecurity(oEntitySum);
		// 	var oEntitySumSum = this.getView().byId("slAnx1aSummEntity").getSelectedKey();
		// 		this._getDataSecurity(oEntitySumSum);

		// },

		onPressAnx1aSummaryGO: function () {
			 //eslint-disable-line
			// var vEntity = this.getView().byId("slAnx1aSummEntity").getSelectedKey();
			var vGstin = this.getView().byId("id_gstinMultiBox").getSelectedKeys();
			var vTaxPeriod = this.getView().byId("drsAnx1aPeriod").getValue();

			var searchInfoRev = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": vGstin,
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.onPressAnx1aSummaryFInal(searchInfoRev);
		},

		onPressAnx1aSummary: function (oEvent) {
			 //eslint-disable-line
			// var oEntity = this.getView().byId("slAnx1aEntityDataSave").getSelectedKey();
			var oEntity = $.sap.entityID;
			var vTaxPeriod = this.getView().byId("drsAnx1aPeriodOn").getValue();
			var obj = oEvent.getSource().getBindingContext("ProcessRecordAnx1A").getObject();
			this._getDataSecurity(oEntity, "dpAnx1a");
			// this.getView().byId("slAnx1aSummEntity").setSelectedKey(oEntity);
			this.getView().byId("id_gstinMultiBox").setSelectedKeys(obj.gstin);
			this.getView().byId("drsAnx1aPeriod").setValue(vTaxPeriod);
			this.getView().byId("itemsegment1a").setSelectedKey("summary");
			this.onSelectionChangeAnx1asegment();
			var oVisible = {
				"asp": true,
				"gstn": true,
				"diff": true,
				"eAsp": false,
				"eGstn": false,
				"eDiff": false
			};
			this.getView().setModel(new JSONModel(oVisible), "visiSummAnx1A");
			var searchInfoRev = {
				"req": {
					"entityId": [oEntity],
					"taxPeriod": vTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [obj.gstin],
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.onPressAnx1aSummaryFInal(searchInfoRev);
		},

		onPressAnx1aSummaryFInal: function (searchInfoRev) {
			 //eslint-disable-line
			this.getView().byId("dpProcessAnx1APage").setVisible(false);
			this.getView().byId("dpAnx1a").setVisible(true);
			var that = this;
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAnx1aReviewSummary.do",
				data: JSON.stringify(searchInfoRev),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				oView.setModel(new JSONModel(data), "ProcessSummaryAnx1aSummary");
				that.getHeaderSummaryData(data);
				// that._bindSummaryTotal(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});

		},
		onPressBackAnx1a: function () {
			 //eslint-disable-line
			this.getView().byId("dpProcessAnx1APage").setVisible(true);
			this.getView().byId("dpAnx1a").setVisible(false);

		},
		fnMoreLessPress1: function (oEvent) {
			 //eslint-disable-line
			if (oEvent.getSource().getId().includes("iMorea1a")) {
				var flag = true;
			} else {
				flag = false;
			}

			this.byId("iMorea1a").setVisible(!flag);
			this.byId("iLessa1a").setVisible(flag);
			this.byId("oHboxGstna1a").setVisible(flag);
			this.byId("oHboxDiffa1a").setVisible(flag);
			this.byId("iHboxGstna1a").setVisible(flag);
			this.byId("iHboxDiffa1a").setVisible(flag);
		},
		_DataReturn: function () {
			return {
				"Outward": {
					"Asp": {
						"type": "ASP",
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"Gstn": {
						"type": "GSTN",
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"Diff": {
						"type": "DIFF",
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					}

				},
				"Inward": {
					"Asp": {
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"Gstn": {
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"Diff": {
						"outwardSup": 0,
						"totalTax": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0

					}
				}
			};
		},

		getHeaderSummaryData: function (data) {
			 //eslint-disable-line
			var aPRHeaderData = this._DataReturn();
			for (var i = 0; i < data.resp.outward.length; i++) {
				aPRHeaderData.Outward.Asp.outwardSup += data.resp.outward[i].aspTaxableValue;
				aPRHeaderData.Outward.Asp.totalTax += data.resp.outward[i].aspTaxPayble;
				aPRHeaderData.Outward.Asp.igst += data.resp.outward[i].aspIgst;
				aPRHeaderData.Outward.Asp.cgst += data.resp.outward[i].aspCgst;
				aPRHeaderData.Outward.Asp.sgst += data.resp.outward[i].aspSgst;
				aPRHeaderData.Outward.Asp.cess += data.resp.outward[i].aspCess;

				// aPRHeaderData.Outward.Gstn.cess += data.resp.outward[i].gstnCess;
				aPRHeaderData.Outward.Gstn.outwardSup += data.resp.outward[i].gstnTaxableValue;
				aPRHeaderData.Outward.Gstn.totalTax += data.resp.outward[i].gstnTaxPayble;
				aPRHeaderData.Outward.Gstn.igst += data.resp.outward[i].gstnIgst;
				aPRHeaderData.Outward.Gstn.cgst += data.resp.outward[i].gstnCgst;
				aPRHeaderData.Outward.Gstn.sgst += data.resp.outward[i].gstnSgst;
				aPRHeaderData.Outward.Gstn.cess += data.resp.outward[i].gstnCess;

				// aPRHeaderData.Outward.Diff.cess += data.resp.outward[i].diffCess;
				aPRHeaderData.Outward.Diff.outwardSup += data.resp.outward[i].diffTaxableValue;
				aPRHeaderData.Outward.Diff.totalTax += data.resp.outward[i].diffTaxPayble;
				aPRHeaderData.Outward.Diff.igst += data.resp.outward[i].diffIgst;
				aPRHeaderData.Outward.Diff.cgst += data.resp.outward[i].diffCgst;
				aPRHeaderData.Outward.Diff.sgst += data.resp.outward[i].diffSgst;
				aPRHeaderData.Outward.Diff.cess += data.resp.outward[i].diffCess;
			}

			for (var i = 0; i < data.resp.inward.length; i++) {
				// aPRHeaderData.Inward.Asp.cess += data.resp.inward[i].aspCess;
				aPRHeaderData.Inward.Asp.outwardSup += data.resp.inward[i].aspTaxableValue;
				aPRHeaderData.Inward.Asp.totalTax += data.resp.inward[i].aspTaxPayble;
				aPRHeaderData.Inward.Asp.igst += data.resp.inward[i].aspIgst;
				aPRHeaderData.Inward.Asp.cgst += data.resp.inward[i].aspCgst;
				aPRHeaderData.Inward.Asp.sgst += data.resp.inward[i].aspSgst;
				aPRHeaderData.Inward.Asp.cess += data.resp.inward[i].aspCess;

				// aPRHeaderData.Inward.Gstn.cess += data.resp.inward[i].gstnCess;
				aPRHeaderData.Inward.Gstn.outwardSup += data.resp.inward[i].gstnTaxableValue;
				aPRHeaderData.Inward.Gstn.totalTax += data.resp.inward[i].gstnTaxPayble;
				aPRHeaderData.Inward.Gstn.igst += data.resp.inward[i].gstnIgst;
				aPRHeaderData.Inward.Gstn.cgst += data.resp.inward[i].gstnCgst;
				aPRHeaderData.Inward.Gstn.sgst += data.resp.inward[i].gstnSgst;
				aPRHeaderData.Inward.Gstn.cess += data.resp.inward[i].gstnCess;

				// aPRHeaderData.Inward.Diff.cess += data.resp.inward[i].diffCess;
				aPRHeaderData.Inward.Diff.outwardSup += data.resp.inward[i].diffTaxableValue;
				aPRHeaderData.Inward.Diff.totalTax += data.resp.inward[i].diffTaxPayble;
				aPRHeaderData.Inward.Diff.igst += data.resp.inward[i].diffIgst;
				aPRHeaderData.Inward.Diff.cgst += data.resp.inward[i].diffCgst;
				aPRHeaderData.Inward.Diff.sgst += data.resp.inward[i].diffSgst;
				aPRHeaderData.Inward.Diff.cess += data.resp.inward[i].diffCess;
			}

			var oAnx1asum = new sap.ui.model.json.JSONModel(aPRHeaderData);
			this.getView().byId("id_Anx1aBlockLayout").setModel(oAnx1asum, "ANX1ASUM");
		},
		onPressErrorCorrection: function (oEvent) {
			 //eslint-disable-line
			var oFilterData = {
				"dataType": "outward",
				"criteria": "RETURN_DATE_SEARCH",
				"status": "E",
				"validation": "BV"
			};

			if (oEvent.getSource().getId().includes("bErrorCorranx1A")) {
				oFilterData.segment = oEvent.getParameter("item").getKey();
				oFilterData.entity = $.sap.entityID;
				oFilterData.gstin = this.byId("idfgiGSINDataAnax1").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("drsAnx1aPeriodOn").getDateValue();
				if (this.byId("dAdaptAnx1A")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpProcessAnx1APage");
				}
			} else {
				oFilterData.segment = oEvent.getParameter("item").getKey();
				oFilterData.entity = $.sap.entityID;
				oFilterData.gstin = this.byId("id_gstinMultiBox").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("drsAnx1aPeriod").getDateValue();
				if (this.byId("dAdaptAnx1A")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpAnx1a");
				}
			}
			if (oFilterData.segment === "gstn") {
				oFilterData.returnType = "anx1a";
			}
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvoiceManage");
		},

		_getOtherFilters: function (search, vPage) {
			//  //eslint-disable-line
			var oDataSecurity = this.byId(vPage).getModel("DataSecurity").getData().items;

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtrAnx1A").getSelectedKeys();
			} else {
				search.PC = [];
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlantAnx1A").getSelectedKeys();
			} else {
				search.Plant = [];
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivisionAnx1A").getSelectedKeys();
			} else {
				search.D = [];
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocationAnx1A").getSelectedKeys();
			} else {
				search.L = [];
			}
			if (oDataSecurity.purchOrg) {
				search.PO = this.byId("slPurcOrgAnx1A").getSelectedKeys();
			} else {
				search.PO = [];
			}
			if (oDataSecurity.salesOrg) {
				search.SO = this.byId("slSalesOrgAnx1A").getSelectedKeys();
			} else {
				search.SO = [];
			}
			if (oDataSecurity.distChannel) {
				search.DC = this.byId("slDistrChannelAnx1A").getSelectedKeys();
			} else {
				search.DC = [];
			}
			if (oDataSecurity.userDefined1) {
				search.UD1 = this.byId("slUserAccess1Anx1A").getSelectedKeys();
			} else {
				search.UD1 = [];
			}
			if (oDataSecurity.userDefined2) {
				search.UD2 = this.byId("slUserAccess2Anx1A").getSelectedKeys();
			} else {
				search.UD2 = [];
			}
			if (oDataSecurity.userDefined3) {
				search.UD3 = this.byId("slUserAccess3Anx1A").getSelectedKeys();
			} else {
				search.UD3 = [];
			}
			if (oDataSecurity.userDefined4) {
				search.UD4 = this.byId("slUserAccess4Anx1A").getSelectedKeys();
			} else {
				search.UD4 = [];
			}
			if (oDataSecurity.userDefined5) {
				search.UD5 = this.byId("slUserAccess5Anx1A").getSelectedKeys();
			} else {
				search.UD5 = [];
			}
			if (oDataSecurity.userDefined6) {
				search.UD6 = this.byId("slUserAccess6Anx1A").getSelectedKeys();
			} else {
				search.UD6 = [];
			}
			return;
		},

		onPressAdaptFiltersAnx1A: function (oEvent) {
			 //eslint-disable-line
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ANX1A.AdaptAnx1A", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			if (oEvent.getSource().getId().includes("bProcessFilterAnx1A")) {
				flFlag = false;
				var oModel = this.byId("dpProcessAnx1APage").getModel("DataSecurity");
				this.byId("slProfitCtrAnx1A").setSelectedKeys(oFilterData.PC);
				this.byId("slPlantAnx1A").setSelectedKeys(oFilterData.Plant);
				this.byId("slDivisionAnx1A").setSelectedKeys(oFilterData.D);
				this.byId("slLocationAnx1A").setSelectedKeys(oFilterData.L);
				this.byId("slPurcOrgAnx1A").setSelectedKeys(oFilterData.PO);
				this.byId("slSalesOrgAnx1A").setSelectedKeys(oFilterData.SO);
				this.byId("slDistrChannelAnx1A").setSelectedKeys(oFilterData.DC);
				this.byId("slUserAccess1Anx1A").setSelectedKeys(oFilterData.UD1);
				this.byId("slUserAccess2Anx1A").setSelectedKeys(oFilterData.UD2);
				this.byId("slUserAccess3Anx1A").setSelectedKeys(oFilterData.UD3);
				this.byId("slUserAccess4Anx1A").setSelectedKeys(oFilterData.UD4);
				this.byId("slUserAccess5Anx1A").setSelectedKeys(oFilterData.UD5);
				this.byId("slUserAccess6Anx1A").setSelectedKeys(oFilterData.UD6);
			} else if (oEvent.getSource().getId().includes("bProcessFilterSummaryAnx1A")) {
				flFlag = true;
				oModel = this.byId("dpAnx1a").getModel("DataSecurity");
				this.byId("slProfitCtrAnx1A").setSelectedKeys(oFilterData.PC);
				this.byId("slPlantAnx1A").setSelectedKeys(oFilterData.Plant);
				this.byId("slDivisionAnx1A").setSelectedKeys(oFilterData.D);
				this.byId("slLocationAnx1A").setSelectedKeys(oFilterData.L);
				this.byId("slPurcOrgAnx1A").setSelectedKeys(oFilterData.PO);
				this.byId("slSalesOrgAnx1A").setSelectedKeys(oFilterData.SO);
				this.byId("slDistrChannelAnx1A").setSelectedKeys(oFilterData.DC);
				this.byId("slUserAccess1Anx1A").setSelectedKeys(oFilterData.UD1);
				this.byId("slUserAccess2Anx1A").setSelectedKeys(oFilterData.UD2);
				this.byId("slUserAccess3Anx1A").setSelectedKeys(oFilterData.UD3);
				this.byId("slUserAccess4Anx1A").setSelectedKeys(oFilterData.UD4);
				this.byId("slUserAccess5Anx1A").setSelectedKeys(oFilterData.UD5);
				this.byId("slUserAccess6Anx1A").setSelectedKeys(oFilterData.UD6);
			}

			this.byId("dAdaptAnx1A").setModel(oModel, "DataSecurity");
			this._oAdpatFilter.open();
		},

		// onPressAdaptFiltersAnx1A: function (oEvent) {
		// 	 //eslint-disable-line
		// 	if (!this._oAdpatFilter) {
		// 		this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ANX1A.AdaptAnx1A", this);
		// 		this.getView().addDependent(this._oAdpatFilter);
		// 	}
		// 	// if (oEvent.getSource().getId().includes("bProcessFilterAnx1A")) {
		// 	// 	var oModel = this.byId("dpProcessRecord").getModel("DataSecurity");

		// 	// } else if (oEvent.getSource().getId().includes("bSummaryFilter")) {
		// 	// 	oModel = this.byId("dpAnx1Summary").getModel("DataSecurity");
		// 	// }

		// 	// this.byId("dAdapt").setModel(oModel, "DataSecurity");
		// 	this._oAdpatFilter.open();
		// },
		onPressFilterClose: function (oEvent) {
			this.onSearchAnx1AprocessApply();
			this._oAdpatFilter.close();

			// if (oEvent.getSource().getId().includes("bApply")) {
			// 	if (this.byId("itbAnx1").getVisible()) {
			// 		if (this.byId("itbAnx1").getSelectedKey() === "process") {
			// 			this._getProcessedData();
			// 		} else if (this.byId("itbAnx1").getSelectedKey() === "CounterParty") {
			// 			this._getCounterParty();
			// 		} else if (this.byId("itbAnx1").getSelectedKey() === "recon") {
			// 			this._getTaxRecon();
			// 		}
			// 	} else if (this.byId("dpAnx1Summary").getVisible()) {
			// 		var searchInfo = this._getSummarySearchInfo();
			// 		this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpAnx1Summary");

			// 		if (this.byId("sbSummary").getSelectedKey() === "summary") {
			// 			this._getProcessSummary(searchInfo);
			// 		}
			// 	}
			// }
		},
		fnTableSavPress: function (oEvent) {
			
			var that = this,
				obj = oEvent.getSource().getBindingContext("ProcessRecordAnx1A").getObject(),
				oPayload = {
					"req": {
						"returnType": "anx1a",
						"entityId": $.sap.entityID,
						"gstin": obj.gstin,
						"taxPeriod": this.byId("drsAnx1aPeriodOn").getValue()
					}
				};

			if (!this._oDialogSave) {
				this._oDialogSave = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ANX1A.SaveStatusAnx1A", this);
				this.byId("dpProcessAnx1APage").addDependent(this._oDialogSave);
				this.byId("DP4").setMaxDate(new Date());
				this.byId("DP4").addEventDelegate({
					onAfterRendering: function () {
						that.byId("DP4").$().find("input").attr("readonly", true);
					}
				});
			}
			this.byId("slGstinSave1").setSelectedKey(obj.gstin);
			this.byId("DP4").setValue(this.byId("drsAnx1aPeriodOn").getValue());
			this.getSaveStauts(oPayload);
			this._oDialogSave.open();
			// return this._oDialogSave;
		},

		// 	fnTableSavPress: function (oEvent) {
		// 	//  //eslint-disable-line
		// 	var that = this,
		// 		obj = oEvent.getSource().getBindingContext("ProcessRecord").getObject(),
		// 		oPayload = {
		// 			"req": {
		// 				"returnType": "anx1",
		// 				"entityId": this.byId("slEntitySave").getSelectedKey(),
		// 				"gstin": obj.gstin,
		// 				"taxPeriod": this.byId("dtProcessed").getValue()
		// 			}
		// 		};
		// 	if (!this._oDialogSaveStats) {
		// 		this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx1.SaveStatus", this);
		// 		this.byId("dpProcessRecord").addDependent(this._oDialogSaveStats);
		// 		this.byId("dtSaveStats").setMaxDate(new Date());
		// 		this.byId("dtSaveStats").addEventDelegate({
		// 			onAfterRendering: function () {
		// 				that.byId("dtSaveStats").$().find("input").attr("readonly", true);
		// 			}
		// 		});
		// 	}
		// 	this.byId("slGstinSaveStats").setSelectedKey(obj.gstin);
		// 	this.byId("dtSaveStats").setValue(this.byId("dtProcessed").getValue());
		// 	this.getSaveStauts(oPayload);
		// 	this._oDialogSaveStats.open();
		// },

		onCloseDialogSave: function () {
			this._oDialogSave.close();
		},
		onPressDifferenceanx1a: function (oEvent) {
			
			var obj = oEvent.getSource().getBindingContext("ProcessRecordAnx1A").getObject(),
				oPayload = {
					"req": {
						"returnType": "anx1a",
						"entityId": $.sap.entityID,
						"gstin": obj.gstin,
						"taxPeriod": this.byId("drsAnx1aPeriodOn").getValue()
					}
				};
			if (!this._oDialog) {
				this._oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ANX1A.DifferenceAnx1A", this);
				this.getView().addDependent(this._oDialog);
			}
			this.getDifferenceData(oPayload);
			this._oDialog.open();
		},

		onCloseDialog: function () {
			this._oDialog.close();
		},

		/**
		 * Called when user change amount format to Absolute, Lakhs, Crores, Millions, Billions
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onMenuItemPressAnx1down: function (oEvent) {
			 //eslint-disable-line
			var oBundle = this.getResourceBundle();
			var vKey = oEvent.getParameter("item").getKey();
			this.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(vKey));
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
			var oModel = this.byId("dpProcessAnx1APage").getModel("ProcessSummaryAnx1a");
			if (oModel) {
				var oData = oModel.getData(),
					aData = [];
				for (var i = 0; i < oData.length; i++) {
					var data = {};
					data.state = oData[i].state;
					data.gstin = oData[i].gstin;
					data.regType = oData[i].regType;
					data.outType = oData[i].outType;
					data.outCount = oData[i].outCount;
					data.outSupplies = oData[i].outSupplies;
					data.outIgst = this.amt2decimal(oData[i].outIgst / div);
					data.outCgst = this.amt2decimal(oData[i].outCgst / div);
					data.outSgst = this.amt2decimal(oData[i].outSgst / div);
					data.outCess = this.amt2decimal(oData[i].outCess / div);
					data.inType = oData[i].inType;
					data.inSupplies = this.amt2decimal(oData[i].inSupplies / div);
					data.inIgst = this.amt2decimal(oData[i].inIgst / div);
					data.inCgst = this.amt2decimal(oData[i].inCgst / div);
					data.inSgst = this.amt2decimal(oData[i].inSgst / div);
					data.inCess = this.amt2decimal(oData[i].inCess / div);
					data.inCount = oData[i].inCount;
					data.status = oData[i].status;
					aData.push(data);
				}
				this.byId("tabAnx1aProcess").setModel(new JSONModel(aData), "ProcessRecordAnx1A");
			}
		},
		amt2decimal: function (vAmount) {
			var amount = vAmount.toString();
			if (amount.indexOf(".") > -1 && amount.indexOf("e-") === -1) {
				return amount.substring(0, amount.indexOf(".") + 3);
			} else if (amount.indexOf("e-") > -1) {
				return 0;
			}
			return vAmount;
		},
		onSelectChangeanx1a: function (oEvent) {
			var oBundle = this.getResourceBundle();
			var oModel = this.byId("dpProcessAnx1APage").getModel("Visi");
			var oData = oModel.getData();
			var vKey = oEvent.getSource().getSelectedKey();
			switch (vKey) {
			case "outward":
				oData.colText = oBundle.getText("outColTxt");
				oData.colVisi = false;
				oData.outward = true;
				oData.inward = false;
				break;
			case "inward":
				oData.colText = oBundle.getText("inColTxt");
				oData.colVisi = false;
				oData.outward = false;
				oData.inward = true;
				break;
			default:
				oData.colText = oBundle.getText("supplies");
				oData.colVisi = true;
				oData.outward = true;
				oData.inward = true;
			}
			oModel.refresh(true);
		},
		clearAdpatFilter: function (oEvent) {
			this.getView().byId("idDateRangeAnx1A").setValue("");
			this.getView().byId("slProfitCtrAnx1A").clearSelection();
			this.getView().byId("slPlantAnx1A").clearSelection();
			this.getView().byId("slDivisionAnx1A").clearSelection();
			this.getView().byId("slLocationAnx1A").clearSelection();
			this.getView().byId("slSalesOrgAnx1A").clearSelection();
			this.getView().byId("slPurcOrgAnx1A").clearSelection();
			this.getView().byId("slDistrChannelAnx1A").clearSelection();
			this.getView().byId("slUserAccess1Anx1A").clearSelection();
			this.getView().byId("slUserAccess2Anx1A").clearSelection();
			this.getView().byId("slUserAccess3Anx1A").clearSelection();
			this.getView().byId("slUserAccess4Anx1A").clearSelection();
			this.getView().byId("slUserAccess5Anx1A").clearSelection();
			this.getView().byId("slUserAccess6Anx1A").clearSelection();

		},
		onSelectionChangeAnx1asegment: function (oEvent) {
			

			var key = this.getView().byId("itemsegment1a").getSelectedKey();

			var oVisiModel = this.byId("dpAnx1a").getModel("VisiSummary");

			if (key === "details") {

				this.byId("tabOutward1a").setVisible(false);
				this.byId("tabInward1a").setVisible(false);
				this.byId("tabEcom1a").setVisible(true);
				this.byId("tabOutwarda1a").setVisible(true);
				this.byId("tabInwarda1a").setVisible(true);
				oVisiModel.getData().details = true;

			} else {

				this.byId("tabOutward1a").setVisible(true);
				this.byId("tabInward1a").setVisible(true);
				this.byId("tabEcom1a").setVisible(true);
				this.byId("tabOutwarda1a").setVisible(false);
				this.byId("tabInwarda1a").setVisible(false);
				oVisiModel.getData().details = false;

				//	this.byId("tabInward").setVisible(false);

			}
			oVisiModel.refresh(true);

		},
		onSelectCheckBoxAnx1A: function (oEvent) {
			var oVisiModel = this.getView().getModel("visiSummAnx1A"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.asp && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.eAsp = true;

			} else if (!oVisiData.asp && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.eGstn = true;

			} else if (!oVisiData.asp && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.eDiff = true;

			} else {
				oVisiData.eAsp = false;
				oVisiData.eDiff = false;
				oVisiData.eGstn = false;
			}
			oVisiModel.refresh(true);
		},

		aspValueFormat: function (value) {
			//  //eslint-disable-line
			var vTabValue;
			switch (value) {
			case "3A":
				vTabValue = "Amendment of B2C (3A)";
				break;
			case "3C":
				vTabValue = "Amendment of Export with Tax (3C)";
				break;
			case "3D":
				vTabValue = "Amendment of Export without Tax (3D)";
				break;
			case "3H":
				vTabValue = "Amendment of Reverse Charge (3H)";
				break;
			case "3I":
				vTabValue = "Amendment of Import of Services (3I)";
				break;
			case "3J":
				vTabValue = "Amendment of Import of Goods (3J)";
				break;
			case "3K":
				vTabValue = "Amendment of Import of goods SEZ (3K)";
				break;
			case "TABLE-4":
				vTabValue = "TABLE-4";
				break;
			case "RNV":
				vTabValue = "Invoice";
				break;
			case "RDR":
				vTabValue = "Debit Note";
				break;
			case "RCR":
				vTabValue = "Credit Note";
				break;
			default:
				vTabValue = value;
			}
			return vTabValue;
		},

		onPressExpandCollapseAnx1a: function (oEvent) {
			
			if (oEvent.getSource().getId().includes("bExpAnx1a")) {
				this.byId("tabOutwarda1a").expandToLevel(1);
				this.byId("tabOutwarda1a").setVisibleRowCount(12);
				this.byId("tabInwarda1a").expandToLevel(1);
				this.byId("tabInwarda1a").setVisibleRowCount(16);
			} else {
				this.byId("tabOutwarda1a").collapseAll();
				this.byId("tabOutwarda1a").setVisibleRowCount(3);
				this.byId("tabInwarda1a").collapseAll();
				this.byId("tabInwarda1a").setVisibleRowCount(4);
			}
		},

		onPressToggle: function (oEvent) {
			 //eslint-disable-line
			var aData = oEvent.getSource().getBinding().getModel("ProcessSummaryAnx1aSummary").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[2];
			if (vModel === "outward") {
				var vRowCount = 3;
			} else {
				vRowCount = 4;
			}
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) {
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData.resp[vModel][aPath[1]].items.length;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Anx-1A
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Anx-1A
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Anx-1A
		 */
		//	onExit: function() {
		//
		//	}

	});

});