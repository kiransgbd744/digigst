sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, MessageBox, Formatter, JSONModel, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GSTR1EINV", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Gstr1PRvsSD
		 */
		onInit: function () {
			var today = new Date(),
				vPeriod = new Date(today.getFullYear(), today.getMonth(), 1);

			this._setDateProperty("idPFromtaxPeriod", vPeriod, today);
			this._setDateProperty("idPTotaxPeriod", today, today, vPeriod);

			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Gstr1PRvsSD
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.byId("dpGstr1vs3Summary").setVisible(false);

				var oVisiSummary = {
					"asp": true,
					"gstn": true,
					"diff": true,
					"enableAsp": false,
					"enableGstn": false,
					"enableDiff": false
				};
				// this._getProcessedData();
				this.onPressClear("P", "C");
				this.getView().setModel(new JSONModel(oVisiSummary), "visiSummAnx1");
			}
		},

		onPressBack: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("dpGstr1vs3Summary").setVisible(false);
		},

		onDownloadOutward: function (section) {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"dataType": "outward",
					"taxDocType": section,
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			this.excelDownload(oPayload, "/aspsapapi/downloadAspvsSubmittedSecReports.do");
		},

		onPressAdaptFilter: function (oEvent) {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.EINV.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			if (oEvent.getSource().getId().includes("bProcessFilter")) {
				this.PRflag = true;
			} else {
				this.PRflag = false;
			}
			var vDate = new Date();
			// vDate.setDate(vDate.getDate() - 9);

			// this.byId("idocFromDate").setDateValue(vDate);
			this.byId("idocFromDate").setMaxDate(new Date());
			// this.byId("idocToDate").setDateValue(new Date());
			// this.byId("idocToDate").setMinDate(vDate);
			this.byId("idocToDate").setMaxDate(new Date());

			// if (oEvent.getSource().getId().includes("bProcessFilter")) {
			// 	var oModel = this.byId("dpProcessRecord").getModel("DataSecurity"),
			// 		oAdaptModel = this.byId("dpProcessRecord").getModel("AdaptFilter");

			// 	if (oAdaptModel) {
			// 		var aAdaptData = oAdaptModel.getData();
			// 		this.byId("slProfitCtr").setSelectedKeys(aAdaptData.PC);
			// 		this.byId("slPlant").setSelectedKeys(aAdaptData.plant);
			// 		this.byId("slDivision").setSelectedKeys(aAdaptData.division);
			// 		this.byId("slLocation").setSelectedKeys(aAdaptData.location);
			// 		this.byId("slSalesOrg").setSelectedKeys(aAdaptData.salesOrg);
			// 		this.byId("slPurcOrg").setSelectedKeys(aAdaptData.purcOrg);
			// 		this.byId("slDistrChannel").setSelectedKeys(aAdaptData.distrChannel);
			// 		this.byId("slUserAccess1").setSelectedKeys(aAdaptData.userAccess1);
			// 		this.byId("slUserAccess2").setSelectedKeys(aAdaptData.userAccess2);
			// 		this.byId("slUserAccess3").setSelectedKeys(aAdaptData.userAccess3);
			// 		this.byId("slUserAccess4").setSelectedKeys(aAdaptData.userAccess4);
			// 		this.byId("slUserAccess5").setSelectedKeys(aAdaptData.userAccess5);
			// 		this.byId("slUserAccess6").setSelectedKeys(aAdaptData.userAccess6);
			// 	}

			// } else if (oEvent.getSource().getId().includes("bSummaryFilter")) {
			// 	oModel = this.byId("dpGstr1Summary").getModel("DataSecurity");
			// }
			// this.byId("dAdapt").setModel(oModel, "DataSecurity");
			this._oAdpatFilter.open();
		},

		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				var oAdaptFilter = {};
				if (this.PRflag) {
					this._getProcessedData();

				} else {
					this._getSummaryData();
				}
			}
		},

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

		onChangeDateValue: function (oEvent) {
			var vDatePicker;
			if (oEvent.getSource().getId().includes("idPFromtaxPeriod")) {
				vDatePicker = "idPTotaxPeriod";
			} else if (oEvent.getSource().getId().includes("idSFromtaxPeriod")) {
				vDatePicker = "idSTotaxPeriod";
			} else if (oEvent.getSource().getId().includes("idocFromDate")) {
				vDatePicker = "idocToDate";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			this.byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}

		},

		onSearchGSTR1vs3B: function (flag) {
			if (flag === "P") {
				this._getProcessedData();
			} else {
				this._getSummaryData();
			}
		},

		_getProcessedData: function () {
			var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod").getValue() || null,
				oTaxPeriodTo = this.getView().byId("idPTotaxPeriod").getValue() || null,
				aGstin = this.getView().byId("idPGstin").getSelectedKeys(),
				aTableType = this.getView().byId("idPTableType").getSelectedKeys(),
				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oTaxPeriodFrom,
						"taxPeriodTo": oTaxPeriodTo,
						"tableType": aTableType.includes("All") ? [] : aTableType,
						"docFromDate": null,
						"docToDate": null,
						"EINVGenerated": [],
						"EWBGenerated": [],
						"AutoDraftedGSTN": [],
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			var that = this;

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
				var AutoDraftedGSTN = this.getView().byId("iAutoDraftedGSTN").getSelectedKeys();

				searchInfo.req.docFromDate = docFromDate;
				searchInfo.req.docToDate = docToDate;
				searchInfo.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				searchInfo.req.AutoDraftedGSTN = AutoDraftedGSTN.includes("All") ? [] : AutoDraftedGSTN;
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/eInvoiceProcessedRecords.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.resp === undefined) {
					that.getView().setModel(new JSONModel(null), "ProcessedRecord");
				} else {
					that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
				}

			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				MessageBox.error("Error : eInvoiceProcessedRecords");
			});

		},

		onPressClear: function (screen, flag) {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("dAdapt")) {
				this.getView().byId("idocFromDate").setValue("");
				this.getView().byId("idocToDate").setValue("");
				this.getView().byId("iEINVGenerated").setSelectedKeys([]);
				this.getView().byId("iAutoDraftedGSTN").setSelectedKeys([]);

				// this.getView().byId("idDateRange").setValue("");
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
			if (flag == "C") {
				var today = new Date(),
					vPeriod = new Date(today.getFullYear(), today.getMonth(), 1);

				if (screen === "P") {
					this.byId("idPGstin").setSelectedKeys([]);
					this.byId("idPTableType").setSelectedKeys([]);

					this._setDateProperty("idPFromtaxPeriod", vPeriod, today);
					this._setDateProperty("idPTotaxPeriod", today, today, vPeriod);
					this._getProcessedData();

				} else if (screen === "S") {
					this.byId("idSummaryGstin").setSelectedKey(null);
					this.byId("idSTableType").setSelectedKeys([]);

					this._setDateProperty("idSFromtaxPeriod", vPeriod, today);
					this._setDateProperty("idSTotaxPeriod", today, today, vPeriod);
					this._getSummaryData();
				}
			}
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onPressGstr1Summary: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
			this.byId("dpGstr1vs3Summary").setVisible(true);

			this.byId("idSFromtaxPeriod").setValue(this.byId("idPFromtaxPeriod").getValue());
			this.byId("idSTotaxPeriod").setValue(this.byId("idPTotaxPeriod").getValue());
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			// this.byId("idPFromtaxPeriod").setDateValue(vDate);
			// this.byId("idPFromtaxPeriod").setMaxDate(new Date());
			// this.byId("idPTotaxPeriod").setDateValue(new Date());
			var fromDate = this.byId("idPFromtaxPeriod").getDateValue();
			fromDate = new Date(fromDate.setDate("01"));
			this.byId("idSFromtaxPeriod").setMaxDate(new Date());
			this.byId("idSTotaxPeriod").setMinDate(fromDate);
			this.byId("idSTotaxPeriod").setMaxDate(new Date());

			this.byId("idSummaryGstin").setSelectedKey(obj.gstin);
			var tableType = this.byId("idSTableType").getSelectedKeys();

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"tableType": tableType.includes("All") ? [] : tableType,
					"docFromDate": null,
					"docToDate": null,
					"EINVGenerated": [],
					"AutoDraftedGSTN": [],
					"dataSecAttrs": {
						"GSTIN": [obj.gstin]
					}
				}
			};

			this._getProcessSummary(searchInfo);
		},

		_getSummaryData: function (oPayload) {
			var tableType = this.byId("idSTableType").getSelectedKeys();
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"tableType": tableType.includes("All") ? [] : tableType,
					"docFromDate": null,
					"docToDate": null,
					"EINVGenerated": [],
					"AutoDraftedGSTN": [],
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			// var searchInfo = {
			// 	"req": {
			// 		"entityId": [$.sap.entityID],
			// 		"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
			// 		"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
			// 		"dataSecAttrs": {
			// 			"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
			// 		}
			// 	}
			// };

			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
				var docFromDate = this.getView().byId("idocFromDate").getValue();
				if (docFromDate === "") {
					docFromDate = null;
				}
				var docToDate = this.getView().byId("idocToDate").getValue();
				if (docToDate === "") {
					docToDate = null;
				}

				var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys();
				var AutoDraftedGSTN = this.getView().byId("iAutoDraftedGSTN").getSelectedKeys();

				searchInfo.req.docFromDate = docFromDate;
				searchInfo.req.docToDate = docToDate;
				searchInfo.req.EINVGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				searchInfo.req.AutoDraftedGSTN = AutoDraftedGSTN.includes("All") ? [] : AutoDraftedGSTN;
			}

			this._getProcessSummary(searchInfo);
		},

		_getProcessSummary: function (oPayload) {
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/einvSummaryScreen.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._bindSummaryData(data);
				if (data.resp === undefined) {
					that.getView().setModel(new JSONModel(null), "ProcessSummary");
				} else {
					// for (var i = 0; i < data.resp.length; i++) {
					// 	data.resp[i].flag = true;
					// 	for (var j = 0; j < data.resp[i].items.length; j++) {
					// 		data.resp[i].items[j].flag = false;
					// 	}
					// }
					that.getView().setModel(new JSONModel(data.resp), "ProcessSummary");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(null), "ProcessSummary");
				MessageBox.error("Error : einvSummaryScreen.do");
			});
		},
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
		formatDocType: function (value) {
			var oBundle = this.getResourceBundle();
			if (value) {
				var vKey = this._tableNumber(value),
					vDocTypeText = oBundle.getText(vKey);
				return vDocTypeText;
			}
			return value;
		},

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
			if (data.resp.length > 0) {
				for (var i = 0; i < data.resp.length; i++) {
					oGstrSummary.ASP.totalTax += data.resp[i].aspTaxPayble;
					oGstrSummary.ASP.igst += data.resp[i].aspIgst;
					oGstrSummary.ASP.cgst += data.resp[i].aspCgst;
					oGstrSummary.ASP.sgst += data.resp[i].aspSgst;
					oGstrSummary.ASP.cess += data.resp[i].aspCess;
					oGstrSummary.GSTN.totalTax += data.resp[i].gstnTaxPayble;
					oGstrSummary.GSTN.igst += data.resp[i].gstnIgst;
					oGstrSummary.GSTN.cgst += data.resp[i].gstnCgst;
					oGstrSummary.GSTN.sgst += data.resp[i].gstnSgst;
					oGstrSummary.GSTN.cess += data.resp[i].gstnCess;
					oGstrSummary.DIFF.totalTax += data.resp[i].diffTaxPayble;
					oGstrSummary.DIFF.igst += data.resp[i].diffIgst;
					oGstrSummary.DIFF.cgst += data.resp[i].diffCgst;
					oGstrSummary.DIFF.sgst += data.resp[i].diffSgst;
					oGstrSummary.DIFF.cess += data.resp[i].diffCess;
				}
			}
			this.getView().setModel(new JSONModel(oGstrSummary), "gstr1Summary");
		},

		onPressGetGst1EinvStatus: function (oEvent, gstin) {
			if (!this._oDialog1) {
				this._oDialog1 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.EINV.InvoiceStatus", this);
				this.getView().addDependent(this._oDialog1);

			}
			sap.ui.getCore().byId("idpopupgstin").setValue(gstin);
			var oTaxPeriodFrom = this.oView.byId("idPFromtaxPeriod").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			var oTaxPeriodTo = this.oView.byId("idPTotaxPeriod").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}

			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriodFrom": oTaxPeriodFrom,
					"taxPeriodTo": oTaxPeriodTo,
					"gstin": [gstin]
				}
			};
			this.postpayloadgstr1 = postData;
			this.getGstr1ASucessStatusDataFinalEinv();
			this.vPSFlag = "P";
			this._oDialog1.open();

		},

		onCloseProcEinvDialog: function () {
			this._oDialog1.close();
		},
		onChangeSegmentProcessStatusEinv: function (oEvent) {
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
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvoiceStatusPopupData.do",
					contentType: "application/json",
					data: JSON.stringify(that.postpayloadgstr1)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oGstr1ASucessData, "Gstr1ASucess");
					}
					// that.bindGstr1DetailStatusEinv(data);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getEinvoiceStatusPopupData : Error");
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
		onPressDownloadReportsEINV: function (oEvent) { //eslint-disable-line
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
				for (var i = postData.req.length - 1; i >= 0; i--) { //eslint-disable-line
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
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegst1proEinvLast").getModel("Gstr1ASucess").getData().resp; //eslint-disable-line

				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue(); //eslint-disable-line
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = { //eslint-disable-line
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) { //eslint-disable-line
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
				for (var i = postData.req.length - 1; i >= 0; i--) { //eslint-disable-line
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
				// var oSelectedItem = sap.ui.getCore().byId("idgetVtablegst1proLast").getSelectedIndices();
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegst1proEinvLast").getModel("Gstr1ASucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	sap.m.MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcesseInv").getValue();
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
		onPressGetGst1Einv: function (oEvent, flag) {
			var oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();
			var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getData();
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var oTaxPeriodFrom = this.oView.byId("idPFromtaxPeriod").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			var oTaxPeriodTo = this.oView.byId("idPTotaxPeriod").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}
			var postData = {
				"req": []
			};
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				// aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				postData.req.push({
					"gstin": oModelForTab1[oSelectedItem[i]].gstin,
					"fromPeriod": oTaxPeriodFrom,
					"toPeriod": oTaxPeriodTo
				})

			}

			this.vPSFlag = "P";
			this.getSectionButtonIntrigrationFinalEINV(postData);

			// var oSource = oEvent.getSource();

		},
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
					MessageBox.error("Gstr1EInvGstnGetSection : Error");
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
							// that.getGstr1ASucessStatusDataFinalEinv();
							that._getProcessedData();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onPressDownloadReport: function (view) {
			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": null,
						"taxPeriodTo": null,
						"tableType": [],
						"type": "gstr1EInvProcess",
						"docDateFrom": null,
						"docDateTo": null,
						"eInvGenerated": [],
						"autoDraftedGSTN": [],
						"dataType": "outward",
						"reportCateg": "GET GSTR1-EInvoice",
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			// 	{
			// 	"req": {
			// 		"gstin": [],
			// 		"fromPeriod": null,
			// 		"toPeriod": null,
			// 		"gstr1EinvSections": []
			// 	}
			// };
			if (view === "P") {
				var oData = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr1vs3Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				var tableType = this.byId("idPTableType").getSelectedKeys();
				oPayload.req.tableType = tableType.includes("All") ? [] : tableType;
				oPayload.req.taxPeriodFrom = this.byId("idPFromtaxPeriod").getValue();
				oPayload.req.taxPeriodTo = this.byId("idPTotaxPeriod").getValue();
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndex[i]].gstin);
				}
			} else {
				oPayload.req.taxPeriodFrom = this.byId("idSFromtaxPeriod").getValue();
				oPayload.req.taxPeriodTo = this.byId("idSTotaxPeriod").getValue();
				oPayload.req.dataSecAttrs.GSTIN.push(this.byId("idSummaryGstin").getSelectedKey());
				var tableType = this.byId("idSTableType").getSelectedKeys();
				oPayload.req.tableType = tableType.includes("All") ? [] : tableType;
			}

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				var docFromDate = this.getView().byId("idocFromDate").getValue();
				if (docFromDate === "") {
					docFromDate = null;
				}
				var docToDate = this.getView().byId("idocToDate").getValue();
				if (docToDate === "") {
					docToDate = null;
				}

				var EINVGenerated = this.getView().byId("iEINVGenerated").getSelectedKeys();
				var AutoDraftedGSTN = this.getView().byId("iAutoDraftedGSTN").getSelectedKeys();

				oPayload.req.docDateFrom = docFromDate;
				oPayload.req.docDateTo = docToDate;
				oPayload.req.eInvGenerated = EINVGenerated.includes("All") ? [] : EINVGenerated;
				oPayload.req.autoDraftedGSTN = AutoDraftedGSTN.includes("All") ? [] : AutoDraftedGSTN;
			}

			// var vUrl = "/aspsapapi/downloadGstr1InvoicesReport.do";
			var vUrl = "/aspsapapi/downloadEInvCsvReports.do";
			this.reportDownload(oPayload, vUrl);

		},

		onPressUpload: function (oEvent) {
			//eslint-disable-line
			var oFileUploader = this.byId("fileUploader");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}

			oFileUploader.setUploadUrl("/aspsapapi/gstr1FileUploadDocuments.do");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fileUploader").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
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

	});

});