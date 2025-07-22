sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, MessageBox, Formatter, JSONModel, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Gstr1PRvsSD", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Gstr1PRvsSD
		 */
		onInit: function () {
			var vDate = new Date();
			this._setDateProperty("idPFromtaxPeriod", vDate, vDate, null);
			this._setDateProperty("idPTotaxPeriod", vDate, vDate, vDate);
			this._setDateProperty("idSFromtaxPeriod", vDate, vDate, null);
			this._setDateProperty("idSTotaxPeriod", vDate, vDate, vDate);
			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);
			this._bindFilterData();
		},

		_bindFilterData: function () {
			var date = new Date();
			this.byId("id_RequestIDpage").setModel(new JSONModel({
				"frTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"status": ""
			}), "FilterModel");
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"screenName": "gstr1Submit"
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2UserNames.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.requestDetails.unshift({
							userName: "All"
						});
						this.byId("id_RequestIDpage").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onBeforeRendering: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("id_RequestIDpage").setVisible(false);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Gstr1PRvsSD
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				var vDate = new Date();
				this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
				this.byId("dpGstr1vs3Summary").setVisible(false);

				this._setDateProperty("idPFromtaxPeriod", vDate, vDate, null);
				this._setDateProperty("idPTotaxPeriod", vDate, vDate, vDate);

				this._getUserId();
				// this._getProcessedData();
				this.onPressClear("P");

				var oVisiSummary = {
					"asp": true,
					"gstn": true,
					"diff": true,
					"enableAsp": false,
					"enableGstn": false,
					"enableDiff": false
				};
				this.getView().setModel(new JSONModel(oVisiSummary), "visiSummAnx1");
			}
		},

		onPressBack: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("dpGstr1vs3Summary").setVisible(false);
		},

		onDownloadOutward: function (section) {
			if (this.byId("idSTotaxPeriod").getValue()) {
				var toPeriod1 = this.byId("idSTotaxPeriod").getValue().split(" ").length;
				if (toPeriod1 === 1) {
					var sumTotaxperiod = this.byId("idSTotaxPeriod").getValue();
				} else {
					sumTotaxperiod = this.byId("idSTotaxPeriod").getValue().split(" ")[0] + this.byId("idSTotaxPeriod").getValue().split(" ")[1];
				}
			}
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"dataType": "outward",
					"taxDocType": section,
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": sumTotaxperiod,
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			this.excelDownload(oPayload, "/aspsapapi/downloadAspvsSubmittedSecReports.do");
		},

		onPressAdaptFilter: function (oEvent) {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.PvsS.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			// var vDate = new Date();
			// vDate.setDate(vDate.getDate() - 9);

			// this.byId("idocFromDate").setDateValue(vDate);
			// this.byId("idocFromDate").setMaxDate(new Date());
			// this.byId("idocToDate").setDateValue(new Date());
			// this.byId("idocToDate").setMinDate(vDate);
			// this.byId("idocToDate").setMaxDate(new Date());

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
				if (this.byId("bProcessFilter").getVisible()) {
					this._getProcessedData();
				} else if (this.byId("bSummaryFilter").getVisible()) {
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
			// if (oDataSecurity.purchOrg) {
			// 	search.PO = this.byId("slPurcOrg").getSelectedKeys();
			// }
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

		onChangeDateValue: function (oEvent, type) {
			var vDatePicker = (type === "P" ? "idPTotaxPeriod" : "idSTotaxPeriod"),
				frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId(vDatePicker).getDateValue();

			if (frDate > toDate) {
				this.byId(vDatePicker).setDateValue(frDate);
			}
			this.byId(vDatePicker).setMinDate(frDate);
		},

		onChangeToDateValue: function (oEvent1) {
			var fromDate1 = oEvent1.getSource().getDateValue();
			if (fromDate1.getFullYear() <= 2021 && fromDate1.getMonth() + 1 <= 4) {
				if (fromDate1.getFullYear() === 2021 && fromDate1.getMonth() === 3) {
					this.getView().byId("idPTotaxPeriod").setDateValue(new Date("04/01/2021"));
					this.getView().byId("idSTotaxPeriod").setDateValue(new Date("04/01/2021"));
				} else {
					this.getView().byId("idPTotaxPeriod").setDateValue(oEvent1.getSource().getDateValue());
					this.getView().byId("idSTotaxPeriod").setDateValue(oEvent1.getSource().getDateValue());
				}
				this.getView().byId("idPTotaxPeriod").setMaxDate(null);
				this.getView().byId("idPTotaxPeriod").setMinDate(null);
				this.getView().byId("idPTotaxPeriod").setMaxDate(new Date("04/01/2021"));
				this.getView().byId("idPTotaxPeriod").setValueFormat('MM yyyy');
				this.getView().byId("idPTotaxPeriod").setDisplayFormat('MMM yyyy');
				this.getView().byId("idSTotaxPeriod").setMaxDate(null);
				this.getView().byId("idSTotaxPeriod").setMinDate(null);
				this.getView().byId("idSTotaxPeriod").setMaxDate(new Date("04/01/2021"));
				this.getView().byId("idSTotaxPeriod").setValueFormat('MM yyyy');
				this.getView().byId("idSTotaxPeriod").setDisplayFormat('MMM yyyy');
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
			var aGstin = this.getView().byId("idPGstin").getSelectedKeys(),
				oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod").getValue(),
				oTaxPeriodTo = this.getView().byId("idPTotaxPeriod").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}
			if (oTaxPeriodTo.split(" ").length === 1) {
				oTaxPeriodTo = oTaxPeriodTo;
			} else {
				oTaxPeriodTo = oTaxPeriodTo.split(" ")[0] + oTaxPeriodTo.split(" ")[1];
			}
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": oTaxPeriodFrom,
					"taxPeriodTo": oTaxPeriodTo,
					"tableType": [],
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};
			var that = this;

			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getProcessedVsSubmittedData.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp === undefined) {
						that.getView().setModel(new JSONModel(null), "ProcessedRecord");
					} else {
						that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error : gstr1VsGstr3bProcessSummary");
				});
		},

		onPressClear: function (screen) {
			var today = new Date(),
				vPeriod = new Date(today.getFullYear(), today.getMonth(), 1);

			if (this.byId("dAdapt")) {
				this.getView().byId("idDateRange").setValue("");
				this.getView().byId("slProfitCtr").setSelectedKeys([]);
				this.getView().byId("slPlant").setSelectedKeys([]);
				this.getView().byId("slDivision").setSelectedKeys([]);
				this.getView().byId("slLocation").setSelectedKeys([]);
				this.getView().byId("slSalesOrg").setSelectedKeys([]);
				// this.getView().byId("slPurcOrg").setSelectedKeys([]);
				this.getView().byId("slDistrChannel").setSelectedKeys([]);
				this.getView().byId("slUserAccess1").setSelectedKeys([]);
				this.getView().byId("slUserAccess2").setSelectedKeys([]);
				this.getView().byId("slUserAccess3").setSelectedKeys([]);
				this.getView().byId("slUserAccess4").setSelectedKeys([]);
				this.getView().byId("slUserAccess5").setSelectedKeys([]);
				this.getView().byId("slUserAccess6").setSelectedKeys([]);
			}
			if (screen === "P") {
				this.byId("idPGstin").setSelectedKeys([]);

				this._setDateProperty("idPFromtaxPeriod", vPeriod, today);
				this._setDateProperty("idPTotaxPeriod", today, today, vPeriod);
				this._getProcessedData();

			} else if (screen === "S") {
				this.byId("idSummaryGstin").setSelectedKey(null);

				this._setDateProperty("idSFromtaxPeriod", vPeriod, today);
				this._setDateProperty("idSTotaxPeriod", today, today, vPeriod);
				this._getSummaryData();
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

			this.byId("idSFromtaxPeriod").setDateValue(this.byId("idPFromtaxPeriod").getDateValue());
			this.byId("idSTotaxPeriod").setMinDate(this.byId("idPFromtaxPeriod").getDateValue());
			this.byId("idSTotaxPeriod").setDateValue(this.byId("idPTotaxPeriod").getDateValue());
			this.byId("idSummaryGstin").setSelectedKey(obj.gstin);

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [obj.gstin]
					}
				}
			};
			this._getProcessSummary(searchInfo);
		},

		_getSummaryData: function (oPayload) {
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			this._getProcessSummary(searchInfo);
		},

		_getProcessSummary: function (oPayload) {
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/processVsSubmitScreen.do",
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
				MessageBox.error("Error : processVsSubmitScreen.do ");
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
				if (e.taxDocType === "ADV REC") {
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
				if (e.taxDocType === "ADV ADJ") {
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
			this.getView().setModel(new JSONModel(oGstrSummary), "gstr1Summary");
		},
		onPressInitiateRecon: function (view) {
			if (view === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select atleast one GSTIN");
					return;
				}
				MessageBox.confirm("Are you sure you want to Initiate Matching?", {
					actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
					onClose: function (oActionSuccess) {
						if (oActionSuccess === "OK") {
							var postData = {
								"req": {
									"entityId": [$.sap.entityID],
									"taxPeriodFrom": this.byId("idPFromtaxPeriod").getValue(),
									"taxPeriodTo": this.byId("idPTotaxPeriod").getValue(),
									"dataSecAttrs": {
										"GSTIN": []
									}
								}
							};
							for (var i = 0; i < oSelectedItem.length; i++) {
								postData.req.dataSecAttrs.GSTIN.push(oModelForTab1[oSelectedItem[i]].gstin);
							}
							if (postData.req.taxPeriodTo) {
								var toPeriod1 = postData.req.taxPeriodTo.split(" ").length;
								if (toPeriod1 === 1) {
									postData.req.taxPeriodTo = postData.req.taxPeriodTo;
								} else {
									postData.req.taxPeriodTo = postData.req.taxPeriodTo.split(" ")[0] + postData.req.taxPeriodTo.split(" ")[1];
								}
							}
							this.vPSFlag = "P";
							this.gstr1InitiateReconCall(postData);
						}
					}.bind(this)
				});
			} else {

			}
		},

		gstr1InitiateReconCall: function (postData) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1PRvsSubmittedInitiateMatching.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.hdr.status == 'S') {
					// 	MessageBox.success(data.resp);
					// 	that._getProcessedData();
					// } else {
					// 	MessageBox.error(data.resp);
					// }
					if (data.resp.status == "Success") {
						MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
							title: "Initiate Matching Successfully done",
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (oActionSuccess) {
								if (oActionSuccess === "OK") {
									that._getProcessedData();
								}
							}
						});
					} else {
						MessageBox.information("Error while doing Initiate Matching");
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGSTR3BSummaryFromGSTN.do : Error");
				});
			});
		},
		onPressDownloadReport: function (button) {
			// debugger; //eslint-disable-line
			if (button === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select atleast one GSTIN");
					return;
				}

				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "aspProcessedSubmitted",
						"taxPeriodFrom": this.byId("idPFromtaxPeriod").getValue(),
						"taxPeriodTo": this.byId("idPTotaxPeriod").getValue(),
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
				for (var i = 0; i < oSelectedItem.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oModelForTab1[oSelectedItem[i]].gstin);
				}

				if (oPayload.req.taxPeriodTo) {
					var toPeriod = oPayload.req.taxPeriodTo.split(" ").length;
					if (toPeriod === 1) {
						oPayload.req.taxPeriodTo = oPayload.req.taxPeriodTo;
					} else {
						oPayload.req.taxPeriodTo = oPayload.req.taxPeriodTo.split(" ")[0] + oPayload.req.taxPeriodTo.split(" ")[1];
					}
				}

				var vUrl = "/aspsapapi/aspProcessedVsSubmitDownloadsreport.do";

				// if (this.byId("dAdapt")) {
				// 	this._getOtherFilters(oPayload.req.dataSecAttrs, vAdaptFilter);
				// }
				this.excelDownload(oPayload, vUrl);
			} else {
				var aGstin = [this.byId("idSummaryGstin").getSelectedKey()];

				var vUrl = "/aspsapapi/aspProcessedVsSubmitDownloadsreport.do";
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "aspProcessedSubmitted",
						"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
						"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
				if (oPayload.req.taxPeriodTo) {
					var toPeriod1 = oPayload.req.taxPeriodTo.split(" ").length;
					if (toPeriod1 === 1) {
						oPayload.req.taxPeriodTo = oPayload.req.taxPeriodTo;
					} else {
						oPayload.req.taxPeriodTo = oPayload.req.taxPeriodTo.split(" ")[0] + oPayload.req.taxPeriodTo.split(" ")[1];
					}
				}

				// if (this.byId("dAdapt")) {
				// 	this._getOtherFilters(oPayload.req.dataSecAttrs, vAdaptFilter);
				// }
				this.excelDownload(oPayload, vUrl);
			}
		},

		onPressGstr1GetDetails: function (view) {
			debugger; //eslint-disable-line
			if (view === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getData();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select atleast one GSTIN");
					return;
				}

				var postData = {
					"req": []
				};
				if (this.byId("idPTotaxPeriod").getValue()) {
					var toPeriod1 = this.byId("idPTotaxPeriod").getValue().split(" ").length;
					if (toPeriod1 === 1) {
						var pTotaxperiod = this.byId("idPTotaxPeriod").getValue();
					} else {
						pTotaxperiod = this.byId("idPTotaxPeriod").getValue().split(" ")[0] + this.byId("idPTotaxPeriod").getValue().split(" ")[1];

					}
				}
				for (var i = 0; i < oSelectedItem.length; i++) {
					postData.req.push({
						"taxPeriod": "",
						"gstin": oModelForTab1[oSelectedItem[i]].gstin,
						"fromPeriod": this.byId("idPFromtaxPeriod").getValue(),
						"toPeriod": pTotaxperiod
					});
				}
				this.vPSFlag = "P";
			} else {

			}

			this.getGstr1GstnGetSection(postData);

		},

		getGstr1GstnGetSection: function (postData) {
			debugger; //eslint-disable-line
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
								type: 'Success',
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
					MessageBox.error("getGSTR3BSummaryFromGSTN.do : Error");
				});
			});
		},
		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get Status",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							if (this.vPSFlag === "P") {
								that._getProcessedData();
							} else {
								that._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}

			this.pressDialog.open();
		},

		//=================== Request id wise api =================================================//
		onPressRequestIDwise: function (oEvent) {
			this.byId("id_RequestIDpage").setVisible(true);
			this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
			this.onSearchReqIdWise();
		},

		onPressRequestIDwiseBack: function (oEvent) {
			this.byId("id_RequestIDpage").setVisible(false);
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
		},

		onChangeDtReconReqId: function (oEvent) {
			var frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId("toReconTaxPriod").getDateValue();

			if (frDate > toDate) {
				this.byId("toReconTaxPriod").setDateValue(frDate);
			}
			this.byId("toReconTaxPriod").setMinDate(frDate);
		},

		onClearReqIdWise: function () {
			this._bindFilterData();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function () {
			var oFilterData = this.byId("id_RequestIDpage").getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": '' + $.sap.entityID,
						"taxPeriodFrom": oFilterData.frTaxPeriod,
						"taxPeriodTo": oFilterData.toTaxPeriod,
						"initiationByUserId": this.removeAll(oFilterData.initiatedBy),
						"reconStatus": oFilterData.status
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1PrVsSubmReportStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.byId("id_RequestIDpage").setModel(new JSONModel(data.resp), "oRequestIDWise");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressGSTIN: function (oEvt) {
			var gstins = [];
			var that = this;
			var TabData = that.byId("id_RequestIDpage").getModel("oRequestIDWise").getData();
			var gstins = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().gstins;
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins);
			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},

		//================= Download Report in request id wise =======================//
		onConfigExtractPress2A1: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().requestId;
			this.reconType = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().reconType;

			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			var oIntiData = {
				"req": {
					"configId": this.oReqId,
					"reconType": this.reconType
				}
			};
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/gstr1PrVsSubmDownloadIdWise.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "DownloadDocument");
					}
				}).fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "DownloadDocument");
				});
			});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onFragDownload: function (oEvt) {
			var that = this;
			var vPath = oEvt.getSource().getBindingContext("DownloadDocument").getObject().path;
			var request = {
				"req": {
					"filePath": vPath
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr1PrVsSubmfiledownloadDoc.do";
			that.excelDownload(request, oReqExcelPath);
		},

		onRefreshRequestIDwise2A: function () {
			this.onSearchReqIdWise();
		}
	});
});