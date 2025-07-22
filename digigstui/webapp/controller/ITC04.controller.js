sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Dialog",
	"sap/m/Button",
], function (BaseController, MessageBox, Formatter, JSONModel, Dialog, Button) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ITC04", {
		formatter: Formatter,

		onInit: function () {
			var that = this,
				vDate = new Date();
			this.getOwnerComponent().getRouter().getRoute("ITC04").attachPatternMatched(this._onRouteMatched, this);

			this.byId("slPFinancialyear").addEventDelegate({
				onAfterRendering: function () {
					that.byId("slPFinancialyear").$().find("input").attr("readonly", true);
				}
			});
			this.byId("slPQTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("slPQTaxPeriod").$().find("input").attr("readonly", true);
				}
			});

			this.byId("slSFinancialyear").addEventDelegate({
				onAfterRendering: function () {
					that.byId("slSFinancialyear").$().find("input").attr("readonly", true);
				}
			});

			this.byId("slSQTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("slSQTaxPeriod").$().find("input").attr("readonly", true);
				}
			});
			this.vPSFlag = "P";

		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onFy();
			}
		},

		// onBeforeRendering: function () {
		// 	debugger
		// 	// this.byId("dtProcessed").setDateValue(new Date());
		// 	this._getProcessedData();
		// },

		taxperiod: function (key) {
			var vTaxPeriod;
			if (key === "Q1") {
				vTaxPeriod = 13;
			} else if (key === "Q2") {
				vTaxPeriod = 14;
			} else if (key === "Q3") {
				vTaxPeriod = 15;
			} else if (key === "Q4") {
				vTaxPeriod = 16;
			} else if (key === "H1") {
				vTaxPeriod = 17;
			} else if (key === "H2") {
				vTaxPeriod = 18;
			}
			return vTaxPeriod;
		},

		onPressITC04UpdateGstn: function () {
			debugger;
			var oData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData(),
				aIndex = this.byId("tabITC04Process").getSelectedIndices();
			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var vTaxPeriod;
			var aGstin = [];
			for (var i = 0; i < aIndex.length; i++) {
				aGstin.push(oData[aIndex[i]].gstin);
			}
			var key = this.byId("slPQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
					"action": "UPDATEGSTIN",
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};

			this.onUpdateGstnDataFinal1(searchInfo);
		},

		onUpdateGstnDataFinal1: function (searchInfo) {
			var that = this;
			//var oBundle = that.getResourceBundle();

			sap.m.MessageBox.confirm("Do you want to Update GSTN data for ITC-04?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/itc04EntityUpdateGstn.do", // Gstr1GstnGetSection.do",
							//surl: "/aspsapapi/getGstr6EntityReviewSummary.do",
							contentType: "application/json",
							data: JSON.stringify(searchInfo)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (data.hdr.status === "S") {
								if (data.resp.length != 0) {
									for (var i = 0; i < data.resp.length; i++) {
										if (data.resp[i].msg) {
											if (data.resp[i].msg.split(",")[0] != 'Success') {
												if (i == 0) {
													var msg = data.resp[i].msg;
												} else {
													msg = msg + "," + data.resp[i].gstin;
												}
											}
										}
									}

								}

								if (msg) {
									sap.m.MessageBox.error(msg, {
										styleClass: "sapUiSizeCompact"
									});

								} else {
									that._getProcessedData();
									MessageBox.success("GSTIN Data Updated Successfully");
								}
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
		},

		onFy: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getAllFy.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json",
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
						that.taxPeriodFirst(data.resp.finYears[0].fullFy);
					} else {
						that.getView().setModel(new JSONModel([]), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
					}
				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oFyModel");
				that.getView().getModel("oFyModel").refresh(true);
			});
		},

		onFnYear: function (flag) {
			this.taxPeriodFY(flag);
		},

		taxPeriodFY: function (flag) {
			debugger;
			var that = this,
				FY;
			if (flag === "P") {
				FY = this.byId("slPFinancialyear").getSelectedItem().getText();
			} else if (flag === "G") {
				FY = this.byId("slSSFinancialyear").getSelectedItem().getText();
			} else {
				FY = this.byId("slSFinancialyear").getSelectedItem().getText();
			}
			var searchInfo = {
				"req": {
					"fy": FY,
				}
			};

			$.ajax({
				method: "POST",
				url: "/aspsapapi/getITC04taxPeriods.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				debugger;
				that.getView().setModel(new JSONModel(data.resp.taxPeriodList), "ITC04taxPeriod");
				/*if (flag !== "G") {
					that._getProcessedData();
				}*/
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		taxPeriodFirst: function (Fy) {
			var that = this;
			var searchInfo = {
				"req": {
					"fy": Fy,
				}
			};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getITC04taxPeriods.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(data.resp.taxPeriodList), "ITC04taxPeriod");
				that._getProcessedData();
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		taxPeriod1: function (Fy) {
			var that = this;
			//var aGstin = this.byId("slGstin").getSelectedKeys();
			//var vTaxPeriod = this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var searchInfo = {
				"req": {
					"fy": Fy,
				}
			};

			$.ajax({
				method: "POST",
				url: "/aspsapapi/getITC04taxPeriods.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(data.resp.taxPeriodList), "ITC04taxPeriod");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_onRouteMatched: function (oEvent) {
			var key = oEvent.getParameter("arguments").key;
			// this.taxPeriod();
			if (key === "DashBoard") {
				// this.onPressGstr1SummaryDashBoard();
			} else {
				this.byId("dpProcessRecord").setVisible(true);
				this.byId("dpITC04Summary").setVisible(false);
				// this._getProcessedData();
			}
		},

		onSearch: function (key) {
			debugger;
			if (key === "P") {
				var key1 = this.byId("slPQTaxPeriod").getSelectedKey();
				if (key1 === "") {
					MessageBox.error("Please Select Tax Period");
					return;
				}
				this._getProcessedData();
			} else {
				this.onFnYear("S");
				this._getSummaryData();
				this._getSignFileStatus1();
				this._saveToGstnStatus();
			}

		},

		onPressAdaptFilter: function (flag) {
			debugger //eslint-disable-line
			this.prosumFlag = flag;
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.itc04.AdaptFilter",
					this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			this._oAdpatFilter.open();
		},

		onPressFilterClose: function (oEvent) {
			//  //eslint-disable-line
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				if (this.prosumFlag == "P") {
					this._getProcessedData();
				} else {
					this._getSummaryData();
				}
			}
		},

		_getProcessedData: function () {
			var that = this;
			var aGstin = this.byId("slGstin").getSelectedKeys();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key1 = this.byId("slPQTaxPeriod").getSelectedKey();
			var key;
			if (key1 === "") {
				key = this.getView().getModel("ITC04taxPeriod").getData()[0].key;
			} else {
				key = this.byId("slPQTaxPeriod").getSelectedKey();
			}
			vTaxPeriod = this.taxperiod(key);
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};

			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpITC04Summary");
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/itcProcessSummary.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});

		},

		onPressGstr1Summary: function (oEvent) {
			debugger; //eslint-disable-line
			this.vPSFlag = "S";
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();
			if (obj.authToken == "Active") {
				this.authState = "A"
			} else {
				this.authState = "I"
			}
			this.byId("dpProcessRecord").setVisible(false);
			this.byId("dpITC04Summary").setVisible(true);
			this.byId("slSummaryGstin").setSelectedKey(obj.gstin);
			var vTaxPeriod;
			//this.taxPeriod1();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var vGstin = this.byId("slSummaryGstin").getSelectedKey();
			this.byId("slSFinancialyear").setSelectedKey(this.byId("slPFinancialyear").getSelectedKey());
			this.byId("slSQTaxPeriod").setSelectedKey(this.byId("slPQTaxPeriod").getSelectedKey());
			this._getProcessSummary(null);
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"action": null,
					"dataSecAttrs": {
						"GSTIN": [vGstin]
					}
				}
			};
			this._getSignFileStatus1();
			this._saveToGstnStatus();
		},

		_getSummaryData: function () {
			this._getProcessSummary(null);
		},

		// onUpdateGstnData: function () {
		// 	this._getProcessSummary("UPDATEGSTIN");
		// },

		_getProcessSummary: function (action) {
			debugger; //eslint-disable-line
			var that = this;
			//this.onFnYear('S');
			//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var vGstin = this.byId("slSummaryGstin").getSelectedKey();
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"action": null,
					"dataSecAttrs": {
						"GSTIN": [vGstin]
					}
				}
			};

			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpITC04Summary");
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/itc04SummaryScreen.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				// that._bindSummaryData(data);
				if (data.resp.lastUpdatedDate) {
					that.byId("tSummUpdate").setText(data.resp.lastUpdatedDate);
				} else {
					that.byId("tSummUpdate").setText(null);
				}
				for (var i = 1; i < data.resp.itc04Records.length; i++) {
					data.resp.itc04Records[i].diffTaxableValue = "NA";
					data.resp.itc04Records[i].gstnTaxableValue = "NA";
				}

				that.byId("dpITC04Summary").setModel(new JSONModel(data.resp), "ProcessSummary");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onPressBack: function () {
			this.vPSFlag = "P";
			this.byId("dpProcessRecord").setVisible(true);
			this.byId("dpITC04Summary").setVisible(false);
			var fy = this.byId("slPFinancialyear").getSelectedKey();
			this.taxPeriod1(fy);
		},

		onPressSaveGstn: function (flag) {
			this.onPressSaveGstn1(flag);
		},

		onSelectALl: function () {
			debugger;
			var oModelData = this.getView().getModel("GetSection");
			if (sap.ui.getCore().byId("id_select1Nd").getSelected() == true) {
				oModelData.getData().Table4 = true;
				oModelData.getData().Table5A = true;
				oModelData.getData().Table5B = true;
				oModelData.getData().Table5C = true;
				oModelData.refresh();
			} else {
				oModelData.getData().Table4 = false;
				oModelData.getData().Table5A = false;
				oModelData.getData().Table5B = false;
				oModelData.getData().Table5C = false;
				oModelData.refresh();
			}
		},

		onPressSignNFile: function (view) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": {
						"gstin": null,
						"taxPeriod": null
					}
				};
			if (view === "P") {

				var oData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabITC04Process").getSelectedIndices();
				if (aIndex.length !== 1) {
					sap.m.MessageBox.information(oBundle.getText("msgOneGstinOnly"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				//var vTaxPeriod = this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				oPayload.req.gstin = oData[aIndex[0]].gstin;
				oPayload.req.taxPeriod = vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey();
			} else {
				//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
				var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				oPayload.req.gstin = this.byId("slSummaryGstin").getSelectedKey();
				oPayload.req.taxPeriod = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();
			}
			this.excelDownload(oPayload, "/aspsapapi/itc04ExecSignAndFileStage1.do");
		},

		onPressSaveGstn1: function (flag) {
			debugger; //eslint-disable-line
			if (flag == "P") {
				var vIndices = this.byId("tabITC04Process").getSelectedIndices(),
					aData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData();
				if (vIndices.length === 0) {
					sap.m.MessageBox.information("Please select atleast one GSTIN", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			}

			if (!this._oDialogSaveStatusBtn) {
				this._oDialogSaveStatusBtn = sap.ui.xmlfragment("com.ey.digigst.fragments.itc04.SaveToGstin", this);
				this.getView().addDependent(this._oDialogSaveStatusBtn);
			}

			if (flag == "P") {
				var aGstin = [];
				for (var i = 0; i < vIndices.length; i++) {
					aGstin.push(aData[vIndices[i]].gstin);
				}
				sap.ui.getCore().byId("idSaveToGstinGstin").setSelectedKeys(aGstin);
				sap.ui.getCore().byId("idSaveToGstinFinancialyear").setSelectedKey(this.byId("slPFinancialyear").getSelectedKey());
				sap.ui.getCore().byId("idSaveToGstinTaxPeriod").setSelectedKey(this.byId("slPQTaxPeriod").getSelectedKey());
			} else {
				sap.ui.getCore().byId("idSaveToGstinGstin").setSelectedKeys([this.byId("slSummaryGstin").getSelectedKey()]);
				sap.ui.getCore().byId("idSaveToGstinFinancialyear").setSelectedKey(this.byId("slSFinancialyear").getSelectedKey());
				sap.ui.getCore().byId("idSaveToGstinTaxPeriod").setSelectedKey(this.byId("slSQTaxPeriod").getSelectedKey());
			}

			sap.ui.getCore().byId("id_select1Nd").setSelected(true);
			var data = {
				"all": true,
				"allEnabled": true,
				"Table4": true,
				"Table5A": true,
				"Table5B": true,
				"Table5C": true,
				"Table4Enabled": true,
				"Table5AEnabled": true,
				"Table5BEnabled": true,
				"Table5CEnabled": true
			};
			var oGetSection = new sap.ui.model.json.JSONModel(data);
			this.getView().setModel(oGetSection, "GetSection");

			sap.ui.getCore().byId("idRadioBtnSave").setSelectedIndex(1);

			// sap.ui.getCore().byId("id_select1Nd").setSelected(false);
			// sap.ui.getCore().byId("id_select2Nd").setSelected(false);
			// this.onRadioBtnselect();

			this._oDialogSaveStatusBtn.open();

		},

		onRadioBtnselect: function (flag) {
			debugger;
			if (sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex() == 0) {
				sap.ui.getCore().byId("id_select1Nd").setSelected(true);
				var data = {

					"all": true,
					"allEnabled": true,
					"Table4": true,
					"Table5A": true,
					"Table5B": true,
					"Table5C": true,
					"Table4Enabled": true,
					"Table5AEnabled": true,
					"Table5BEnabled": true,
					"Table5CEnabled": true
				};
				var oGetSection = new sap.ui.model.json.JSONModel(data);
				this.getView().setModel(oGetSection, "GetSection");

				var vTaxPeriod = sap.ui.getCore().byId("idSaveToGstinTaxPeriod").getSelectedKey() + sap.ui.getCore().byId(
					"idSaveToGstinFinancialyear").getSelectedKey();
				var searchInfo = {
					"req": [{
						"ret_period": vTaxPeriod,
						"gstin": sap.ui.getCore().byId("idSaveToGstinGstin").getSelectedKey()
					}]
				};
				var that = this;
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/itc04SaveToGstnSectionSelection.do",
					data: JSON.stringify(searchInfo),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.bindGstnSectionSelection(data);
					debugger;
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			} else {
				sap.ui.getCore().byId("id_select1Nd").setSelected(true);
				var data = {
					"all": true,
					"allEnabled": true,
					"Table4": true,
					"Table5A": true,
					"Table5B": true,
					"Table5C": true,
					"Table4Enabled": true,
					"Table5AEnabled": true,
					"Table5BEnabled": true,
					"Table5CEnabled": true
				};
				var oGetSection = new sap.ui.model.json.JSONModel(data);
				this.getView().setModel(oGetSection, "GetSection");
			}

		},
		bindGstnSectionSelection: function (oData) {
			var data = {
				"all": false,
				"allEnabled": false,
				"Table4": false,
				"Table5A": false,
				"Table5B": false,
				"Table5C": false,
				"Table4Enabled": false,
				"Table5AEnabled": false,
				"Table5BEnabled": false,
				"Table5CEnabled": false
			};

			if (oData.resp[0].sections.includes('M2JW') == true) {
				data.Table4 = true;
				data.Table4Enabled = true;
			}
			if (oData.resp[0].sections.includes('TABLE5A') == true) {
				data.Table5A = true;
				data.Table5AEnabled = true;
			}
			if (oData.resp[0].sections.includes('TABLE5B') == true) {
				data.Table5B = true;
				data.Table5BEnabled = true;
			}
			if (oData.resp[0].sections.includes('TABLE5C') == true) {
				data.Table5C = true;
				data.Table5CEnabled = true;
			}
			var oGetSection = new sap.ui.model.json.JSONModel(data);
			this.getView().setModel(oGetSection, "GetSection");
		},

		onSaveGstr6ABtnDialog: function (flag) {
			this._oDialogSaveStatusBtn.close();
			this._saveToGstnStatus();
			if (flag === "C") {
				this._oDialogSaveStatusBtn.close();

			} else {
				this.itc04SaveToGstnJob();

			}

		},
		itc04SaveToGstnJob: function () {
			debugger;
			if (sap.ui.getCore().byId("idRadioBtnSave").getSelectedIndex() == 0) {
				var vTaxPeriod;
				var key = sap.ui.getCore().byId("idSaveToGstinTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var vTaxPeriod1 = vTaxPeriod + sap.ui.getCore().byId("idSaveToGstinFinancialyear").getSelectedKey();
				var searchInfo = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": vTaxPeriod1,
						"gstins": sap.ui.getCore().byId("idSaveToGstinGstin").getSelectedKeys(),
						"isResetSave": true,
						"tableSections": []
					}
				}
				var oData = this.getView().getModel("GetSection").getData();
				if (oData.Table4) {
					searchInfo.req.tableSections.push('4');
				}
				if (oData.Table5A) {
					searchInfo.req.tableSections.push('5A')
				}
				if (oData.Table5B) {
					searchInfo.req.tableSections.push('5B')
				}
				if (oData.Table5C) {
					searchInfo.req.tableSections.push('5C')
				}

				if (searchInfo.req.tableSections.length == 0) {
					sap.m.MessageBox.information("No table Section is available for save");
					return;
				}

				var that = this;
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/itc04SaveToGstnResetAndSaveJob.do",
					data: JSON.stringify(searchInfo),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._msgSaveToGstn(data);
					that._saveToGstnStatus();
					debugger;
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			} else {
				var vTaxPeriod;
				var key = sap.ui.getCore().byId("idSaveToGstinTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var vTaxPeriod1 = vTaxPeriod + sap.ui.getCore().byId("idSaveToGstinFinancialyear").getSelectedKey();
				var searchInfo = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": vTaxPeriod1,
						"gstins": sap.ui.getCore().byId("idSaveToGstinGstin").getSelectedKeys(),
						"tableSections": []
					}
				}
				var oData = this.getView().getModel("GetSection").getData();
				if (oData.Table4) {
					searchInfo.req.tableSections.push('4');
				}
				if (oData.Table5A) {
					searchInfo.req.tableSections.push('5A')
				}
				if (oData.Table5B) {
					searchInfo.req.tableSections.push('5B')
				}
				if (oData.Table5C) {
					searchInfo.req.tableSections.push('5C')
				}

				if (searchInfo.req.tableSections.length == 0) {
					sap.m.MessageBox.information("No table Section is available for save");
					return;
				}

				var that = this;
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/itc04SaveToGstnJob.do",
					data: JSON.stringify(searchInfo),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._msgSaveToGstn(data);
					that._saveToGstnStatus();
					debugger;
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			}
		},

		_saveToGstnStatus: function () {
			var that = this;
			//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"returnType": "ITC04",
					"dataSecAttrs": {
						"GSTIN": [this.byId("slSummaryGstin").getSelectedKey()],
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

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getSaveGstnStatus.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				debugger;
				if (data.hdr.status === "S") {
					if (data.resp.updatedDate) {
						that.byId("tSummSave").setText(data.resp.updatedDate);
					} else {
						that.byId("tSummSave").setText(null);
					}
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_saveToGstn: function () {
			var that = this;
			//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var searchInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"retPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"gstins": [this.byId("slSummaryGstin").getSelectedKey()]
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/itc04SaveToGstnJob.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					that._msgSaveToGstn(data);
				} else {
					if (data.resp.length > 0) {
						that._msgSaveToGstn(data);
					} else {
						sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
					}
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},
		_msgSaveToGstn: function (data) {
			var aMockMessages = [];
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
			this.getView().setModel(new JSONModel(aMockMessages), "Msg");
			this.onDialogPress();
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Save to GSTN",
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
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},
		onUpdateGstnData: function (oEvent, view) {
			if (view === "S") {
				//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
				var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var vGstin = this.byId("slSummaryGstin").getSelectedKey();
				var searchInfo = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
						"action": "UPDATEGSTIN",
						"dataSecAttrs": {
							"GSTIN": [vGstin]
						}
					}
				};
			}
			this.onUpdateGstnDataFinal(searchInfo);
		},

		onUpdateGstnDataFinal: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			//var oBundle = that.getResourceBundle();

			sap.m.MessageBox.confirm("Do you want to Update GSTN data for ITC-04?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/itc04SummaryScreen.do", // Gstr1GstnGetSection.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (data.hdr.status === "S") {
								// if (data.resp.lastUpdatedDate) {
								// 	that.byId("tSummUpdate").setText(data.resp.lastUpdatedDate);
								// } else {
								// 	that.byId("tSummUpdate").setText(null);
								// }
								// for (var i = 1; i < data.resp.itc04Records.length; i++) {
								// 	data.resp.itc04Records[i].diffTaxableValue = "NA";
								// 	data.resp.itc04Records[i].gstnTaxableValue = "NA";
								// }
								// that.byId("dpITC04Summary").setModel(new JSONModel(data.resp), "ProcessSummary");
								that._getProcessSummary();
								sap.m.MessageBox.success("GSTIN Data Updated Successfully");
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
		},
		onPressDownloadReportPopup: function () {
			debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "itc04SavedSubmitted",
						"taxPeriod": this.postpayloadgstr6.req.taxPeriod,
						"tableno": [],
						"dataSecAttrs": {
							"GSTIN": this.postpayloadgstr6.req.gstin
						}
					}
				};

			var vUrl = "/aspsapapi/ITC04ReportsDownloads.do";
			this.excelDownload(oPayload, vUrl);

		},
		onPressDownloadReport: function (oEvent, parameter, view) {
			debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "",
						"taxPeriod": "132019",
						"tableno": [],
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (view === "P") {
				var oData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabITC04Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.type = oEvent.getParameter("item").getKey();
				var vTaxPeriod;
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				oPayload.req.taxPeriod = vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey();
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndex[i]].gstin);
				}
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
				}
			} else {
				oPayload.req.type = oEvent.getParameter("item").getKey();
				var vTaxPeriod;
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				oPayload.req.taxPeriod = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();
				oPayload.req.dataSecAttrs.GSTIN.push(this.byId("slSummaryGstin").getSelectedKey());
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				}
			}
			var vUrl = "/aspsapapi/ITC04ReportsDownloads.do";
			this.excelDownload(oPayload, vUrl);

		},

		onPressSaveStatus: function (oEvent, view) {
			debugger; //eslint-disable-line
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
			if (!this._oDialogSaveStatsitc04) {
				this._oDialogSaveStatsitc04 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.itc04.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStatsitc04);

				this.byId("slSSFinancialyear").addEventDelegate({
					onAfterRendering: function () {
						that.byId("slSSFinancialyear").$().find("input").attr("readonly", true);
					}
				});

				this.byId("slSSQTaxPeriod").addEventDelegate({
					onAfterRendering: function () {
						that.byId("slSSQTaxPeriod").$().find("input").attr("readonly", true);
					}
				});
			}

			if (view === "P") {
				var vTaxPeriod;
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
					vPeriod = vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
					vGstin = obj.gstin;

				this.byId("slSaveStatsGstin").setSelectedKey(vGstin);
				this.byId("slSSQTaxPeriod").setSelectedKey(this.byId("slPQTaxPeriod").getSelectedKey());
				this.byId("slSSFinancialyear").setSelectedKey(this.byId("slPFinancialyear").getSelectedKey());
			} else {
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				var vTaxPeriod;
				vTaxPeriod = this.taxperiod(key);
				var vGstin = this.byId("slSummaryGstin").getSelectedKey(); //s()[0];
				var vPeriod = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();

				this.byId("slSaveStatsGstin").setSelectedKey(vGstin);
				this.byId("slSSQTaxPeriod").setSelectedKey(this.byId("slSQTaxPeriod").getSelectedKey());
				this.byId("slSSFinancialyear").setSelectedKey(this.byId("slSFinancialyear").getSelectedKey());
			}
			oPayload.req.taxPeriod = vPeriod;
			oPayload.req.dataSecAttrs.GSTIN.push(vGstin);

			this._oDialogSaveStatsitc04.open();
			this._getSaveStatus(oPayload);
		},
		onCloseSaveStatus: function () {
			this._oDialogSaveStatsitc04.close();
		},
		onSearchSaveStatus: function () {
			// debugger; //eslint-disable-line
			var aGstin = this.byId("slSaveStatsGstin").getSelectedKey(),
				oModel = this.byId("dSaveStatus").getModel("SaveStatus");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			if (aGstin.length === 0) {
				sap.m.MessageToast.show("Please select atleast one GSTIN");
				return;
			}
			var key = this.byId("slSSQTaxPeriod").getSelectedKey();
			var vTaxPeriod = this.taxperiod(key);

			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": vTaxPeriod + this.byId("slSSFinancialyear").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": [aGstin] //.includes("All") ? [] : aGstin
					}
				}
			};
			this._getSaveStatus(oPayload);
		},
		_getSaveStatus: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this,
				oModel = that.byId("dSaveStatus").getModel("SaveStatus");

			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/itc04SummarySaveStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].sno = i + 1;
				}
				that.byId("dSaveStatus").setModel(new JSONModel(data.resp), "SaveStatus");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onExportExcel: function (button) {
			debugger; //eslint-disable-line
			if (button === "P") {
				var vType = "itc04prsummary",
					vAdaptFilter = "dpProcessRecord";
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				var vTaxPeriod = this.taxperiod(key);
				var aGstin = this.byId("slGstin").getSelectedKeys();
				var vTaxPeriod1 = vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey();

			} else {
				var vType = "itc04Reviewsummary",
					vAdaptFilter = "dpGstr1Summary";
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				var vTaxPeriod = this.taxperiod(key);
				var aGstin = [this.byId("slSummaryGstin").getSelectedKey()];
				var vTaxPeriod1 = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();
			}
			var vUrl = "/aspsapapi/itc04ScreensReportsDownloads.do",
				oPayload = {
					"req": {
						"type": vType,
						"entityId": [$.sap.entityID],
						"taxPeriod": vTaxPeriod1,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin

						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpITC04Summary");
			}
			this.excelDownload(oPayload, vUrl);
		},

		onPressDownloadPdf: function (button, flag) {
			// debugger; //eslint-disable-line
			if (button === "P") {
				var vType = "itc04prsummary",
					//vTaxPeriod = this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey(),
					vAdaptFilter = "dpProcessRecord";
				// var aGstin = this.byId("slGstin").getSelectedKeys();
				var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var aGstin = [];
				var vTaxPeriod1 = vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey();
				var vIndices = this.byId("tabITC04Process").getSelectedIndices(),
					aData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData();
				if (vIndices.length === 0) {
					sap.m.MessageBox.information("Please select atleast one GSTIN", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				for (var i = 0; i < vIndices.length; i++) {
					aGstin.push(aData[vIndices[i]].gstin);
				}
				var vUrl = "/aspsapapi/itc04PdfReport.do",
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": vTaxPeriod1,
							"action": null,
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : aGstin
							}
						}
					};
			} else {
				var vType = "itc04Reviewsummary",
					//vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey(),
					vAdaptFilter = "dpGstr1Summary";
				var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var aGstin = [this.byId("slSummaryGstin").getSelectedKey()];
				var vTaxPeriod1 = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();
				var vUrl = "/aspsapapi/itc04PdfReport.do",
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": vTaxPeriod1,
							"action": null,
							"isDigigst": flag,
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : aGstin
							}
						}
					};
			}

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpITC04Summary");
			}

			this.excelDownload(oPayload, vUrl);
		},

		onActivateAuthToken: function (gstin, authToken) {
			// debugger; //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onDownloadConsGstnErrors: function (oEvent, errType, refId) {
			debugger; //eslint-disable-line
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var vTaxPeriod1 = vTaxPeriod + this.byId("slSSFinancialyear").getSelectedKey();
			if (errType === "aspError") {
				var vUrl = "/aspsapapi/ITC04ReportsDownloads.do",
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
							"taxPeriod": vTaxPeriod1,
							"gstnRefId": refId
						}
					};
			} else {
				vUrl = "/aspsapapi/ITC04ReportsDownloads.do";
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "itc04GstnRefid",
						"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
						"taxPeriod": vTaxPeriod1,
						"gstnRefId": refId
					}
				};
				// var aGstin = this.byId("slSaveStatsGstin").getSelectedKey();
				// oPayload.req.type = "itc04GstnError";
				// oPayload.req.taxPeriod = this.byId("slSSQTaxPeriod").getSelectedKey() + this.byId("slSSFinancialyear").getSelectedKey();
				// oPayload.req.dataSecAttrs.GSTIN = [this.byId("slSaveStatsGstin").getSelectedKey()]; //aGstin.includes("All") ? [] : aGstin;
			}
			this.excelDownload(oPayload, vUrl);
		},

		onPressITC04TableTypeLink: function (tableType) {
			debugger;
			if (!this._oDialogTable) {
				this._oDialogTable = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.itc04.TablePopUp", this);
				this.getView().addDependent(this._oDialogTable);
			}

			var data = {
				"tableName": null,
				"tableFlag": false,
				"resp": []
			}
			if (tableType == "M2JW (Section 4)") {
				data.tableFlag = false;
				data.tableName = "Table 4 - M2JW";
				data.headerName = "Goods sent - Manufacturer to Job Worker";
				this.tableType = "4";
				this._getTablePOPUP("table4");

			} else if (tableType == "JW2M (Section 5A)") {
				data.tableFlag = true;
				data.tableName = "Table 5A - JW2M";
				data.headerName = "Goods received back - Job Worker to Manufacturer (5A)";
				this.tableType = "5A";
				this._getTablePOPUP("table5A");
			} else if (tableType == "OtherJW2M (Section 5B)") {
				data.tableFlag = true;
				data.tableName = "Table 5B - Other JW2M";
				data.headerName = "Goods received back - Other Job Worker to Manufacturer (5B)";
				this.tableType = "5B";
				this._getTablePOPUP("table5B");
			} else if (tableType == "M2JWSoldfromJW (Section 5C)") {
				data.tableFlag = true;
				data.tableName = "Table 5C - Sold from JW";
				data.headerName = "Goods sold from Job Worker Premises (5C)";
				this.tableType = "5C";
				this._getTablePOPUP("table5C");
			}
			this.getView().setModel(new JSONModel(data), "TablePopup");
			this._oDialogTable.open();
		},

		onPressTableClose: function () {
			this._oDialogTable.close();
		},

		_getOtherFilters: function (search, vPage) {
			// debugger; //eslint-disable-line
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
			if (oDataSecurity.profitCenter2) {
				search.PC2 = this.byId("slprofitCenter2").getSelectedKeys();
			}

			return;
		},

		_getTablePOPUP: function (type) {
			debugger; //eslint-disable-line
			//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var aGstin = [this.byId("slSummaryGstin").getSelectedKey()];
			var vTaxPeriod1 = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();

			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod1,
					"type": type, //"table4/table5A/table5B/table5C",
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpITC04Summary");
			}
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getITC04Popup.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				// for (var i = 0; i < data.resp.length; i++) {
				// 	data.resp[i].sno = i + 1;
				// }
				that.getView().setModel(new JSONModel(data), "ITC04Popup");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onPressPopupDownloadReport: function () {
			//var vTaxPeriod = this.byId("slSQTaxPeriod").getSelectedKey() + this.byId("slSFinancialyear").getSelectedKey();
			var vTaxPeriod; //= this.byId("slPQTaxPeriod").getSelectedKey() + this.byId("slPFinancialyear").getSelectedKey();
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var aGstin = [this.byId("slSummaryGstin").getSelectedKey()];
			var vTaxPeriod1 = vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey();
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"type": "itc04Aspuploaded",
					"taxPeriod": vTaxPeriod1,
					"tableno": [this.tableType],
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			}

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpITC04Summary");
			}
			var vUrl = "/aspsapapi/ITC04ReportsDownloads.do";
			this.excelDownload(oPayload, vUrl);
		},

		onPressITC04GetDetails: function (oEvent, oFlag) {
			debugger;
			// var oSource = oEvent.getSource();
			this.postpayloadgstr6 = {};
			if (oFlag === "P") {
				this.vFlaITC04 = "P";
				var oData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabITC04Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information('Please Select at least one GSTIN', {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}

				var aGstin = [];
				for (var i = 0; i < aIndex.length; i++) {
					aGstin.push(oData[aIndex[i]].gstin);

				}
				var vTaxPeriod;
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"gstin": aGstin
					}
				};
			} else if (oFlag === "S") {
				this.vFlaITC04 = "S";
				var vTaxPeriod;
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
						"gstin": [this.byId("slSummaryGstin").getSelectedKey()]
					}
				};

			} else {}

			if (!this._oDialogGstr6A) {
				this._oDialogGstr6A = sap.ui.xmlfragment("com.ey.digigst.fragments.itc04.GetITC04Status", this);
				this.getView().addDependent(this._oDialogGstr6A);

			}
			this.postpayloadgstr6 = postData;

			this.getGstr6ASucessStatusDataFinal();
			debugger;
			sap.ui.getCore().byId("id_TaxProcessGstr6A").setValue(postData.req.taxPeriod);
			if (oFlag === "P") {
				sap.ui.getCore().byId("id_TaxProcessGstr6A1").setValue(this.byId("slPFinancialyear")._getSelectedItemText());
				sap.ui.getCore().byId("id_TaxProcessGstr6A2").setValue(this.byId("slPQTaxPeriod")._getSelectedItemText());
			} else {
				sap.ui.getCore().byId("id_TaxProcessGstr6A1").setValue(this.byId("slSFinancialyear")._getSelectedItemText());
				sap.ui.getCore().byId("id_TaxProcessGstr6A2").setValue(this.byId("slSQTaxPeriod")._getSelectedItemText());
			}
			this._oDialogGstr6A.open();

		},
		onCloseDialog: function () {
			this._oDialogGstr6A.close();
			this._getProcessSummary();
		},

		getGstr6ASucessStatusDataFinal: function () {
			// var postData = {

			// 	"req": {
			// 		"entityId": $.sap.entityID,
			// 		"taxPeriod": this.postpayloadgstr6.req.taxPeriod,
			// 		"gstin": this.postpayloadgstr6.req.gstin
			// 	}
			// };
			var postData = this.postpayloadgstr6;

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getItc04PopUpRecords.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					// that.bindGstr6DetailStatus(data);

					var oGstrITC04SucessData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oGstrITC04SucessData, "GstrITC04Sucess");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					//MessageBox.error("getItc04PopUpRecords : Error");
				});
			});
		},

		onChangeSegmentStatus: function (oEvent) {
			debugger;
			// var oView = this.getView();
			if (oEvent.getSource().getSelectedKey() === "LCS") {
				sap.ui.getCore().byId("idtitle1").setVisible(true);
				sap.ui.getCore().byId("idtittle2").setVisible(false);
				sap.ui.getCore().byId("idgetVtablegstr6").setVisible(true);
				sap.ui.getCore().byId("idgetStablegstr6").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "LSS") {
				sap.ui.getCore().byId("idtitle1").setVisible(false);
				sap.ui.getCore().byId("idtittle2").setVisible(true);
				sap.ui.getCore().byId("idgetStablegstr6").setVisible(true);
				sap.ui.getCore().byId("idgetVtablegstr6").setVisible(false);

			}
		},

		onPressITC04GetDetails123: function (view) {
			debugger; //eslint-disable-line
			var view = this.vFlaITC04
			var oBundle = this.getResourceBundle(),
				oPayload = {
					"req": []
				};
			if (view === "P") {
				var oData = this.byId("tabITC04Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabITC04Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information('Please Select at least one GSTIN', {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				var vTaxPeriod;
				var key = this.byId("slPQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.push({
						"gstin": oData[aIndex[i]].gstin,
						"ret_period": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"itc04Sections": ["GET"]
					});
				}
				// if (this.byId("dAdapt")) {
				// 	this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
				// }
			} else {
				var vTaxPeriod;
				var key = this.byId("slSQTaxPeriod").getSelectedKey();
				vTaxPeriod = this.taxperiod(key);
				oPayload.req.push({
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"ret_period": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"itc04Sections": ["GET"]
				});
				// if (this.byId("dAdapt")) {
				// 	this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				// }
			}
			// var vUrl = "/aspsapapi/ITC04ReportsDownloads.do";
			// this.excelDownload(oPayload, vUrl);
			this.Itc04GstnGetSection(oPayload);

		},

		Itc04GstnGetSection: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			var aMockMessages = [];
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/Itc04GstnGetSection.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
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
				//that.vPSFlag = "P";
				that.getView().setModel(new JSONModel(aMockMessages), "Msg");
				that.onDialogPress();

			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Save Status",
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
							that.getGstr6ASucessStatusDataFinal();
							debugger;
							if (that.vPSFlag === "P") {
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

		onSignFile: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			if (key === "DSC") {
				this.onSignFileDSC();
				this.DSCEVCflag = "DSC"
			} else {
				this.onSignFileDSC();
				this.DSCEVCflag = "EVC"
			}
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
				this._oDialogSaveStats56 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.itc04.SignandFile", this);
				this.getView().addDependent(this._oDialogSaveStats56);
			}

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

					that._oDialogSaveStats56.open();
					if (that.DSCEVCflag === "DSC") {
						data.resp.header = "Please select the authorised signatory for which DSC is attached for initiating filing of ITC-04"
					} else {
						data.resp.header = "Please select the authorised signatory for which EVC is attached for initiating filing of ITC-04"
					}
					BulkGstinSaveModel.setData(data.resp);
					that.getView().setModel(BulkGstinSaveModel, "SignandFile");

				}).fail(function (jqXHR, status, err) {});
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
				"Do you want to proceed with filing ITC-04?", {
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

		onSaveSign2: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var vTaxPeriod;
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var Request = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"pan": selItem
				}
			};
			var GstnsList = "/aspsapapi/itc04ExecSignAndFileStage1.do";
			this.excelDownload(Request, GstnsList);
			this._oDialogSaveStats56.close();
			var that = this;
			MessageBox.show(
				"ITC04 filing is initiated, click on filing status to view the status", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					onClose: function (oAction) {
						this._getSignFileStatus1();
					}
				});
		},

		onSaveSignEVCConformation: function () {
			debugger;
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var vTaxPeriod;
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			vTaxPeriod = this.taxperiod(key);
			var Request = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKey(),
					"taxPeriod": vTaxPeriod + "/" + this.byId("slSFinancialyear").getSelectedKey(),
					"pan": selItem
				}
			};

			this.ReqPayload = Request;
			if (!this._oDialogSaveStatsConfirm) {
				this._oDialogSaveStatsConfirm = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.itc04.SignandFileConformation", this);
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
			var key = this.byId("slSQTaxPeriod").getSelectedKey();
			var vTaxPeriod = this.taxperiod(key);
			var RequestPayload = {
				"req": {
					"gstin": this.ReqPayload.req.gstin,
					"taxPeriod": vTaxPeriod + this.byId("slSFinancialyear").getSelectedKey(),
					"pan": this.ReqPayload.req.pan,
					"returnType": "itc04"
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
					"com.ey.digigst.fragments.itc04.SignandFileOTP", this);
				this.getView().addDependent(this._oDialogSignandFileOTP);
			}
			this.byId("otpValue").setValue();
			this._oDialogSignandFileOTP.open();
		},
		onPopupOTPCancel: function () {
			this._oDialogSignandFileOTP.close();
		},

		onPopupOTPSign: function () {
			debugger;
			var otp = this.byId("otpValue").getValue();
			if (otp === null) {
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
				this._oDialogSaveStats9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.itc04.FilingStatus", this);
				this.getView().addDependent(this._oDialogSaveStats9);
			}

			var gstin = this.byId("slSummaryGstin").getSelectedKey();
			var vDate = new Date();
			//this.byId("FStp").setMaxDate(vDate);
			this.byId("FStp").setSelectedKey(this.byId("slSQTaxPeriod").getSelectedKey());
			this.byId("FsGstin").setSelectedKey(gstin);
			this.onSaveOkay12();
			this._oDialogSaveStats9.open();
		},

		onCloseDialogFS: function () {
			this._oDialogSaveStats9.close();
			this._getSignFileStatus1();
		},

		onSaveOkay12: function () {
			var that = this;
			var key = this.byId("FStp").getSelectedKey();
			var vTaxPeriod;
			vTaxPeriod = this.taxperiod(key);
			var req = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey()
				}
			};
			var GstinSaveModel = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getItc04FilingStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					GstinSaveModel.setData(data.resp);
					that.getView().byId("idTableFS").setModel(GstinSaveModel, "TableFS");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onSaveStatusDownload1: function (oEvt) {
			this.oReqId1 = oEvt.getSource().getParent().getBindingContext("TableFS").getObject().refId;
			var oReqExcelPath = "/aspsapapi/downloadItc04Errors.do?id=" + this.oReqId1 + "";
			window.open(oReqExcelPath);
		},

		_getSignFileStatus1: function () {
			var vGstin = this.byId("slSummaryGstin").getSelectedKey();
			var key = this.byId("slPQTaxPeriod").getSelectedKey();
			var vTaxPeriod = this.taxperiod(key);
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
					"action": null,
					"dataSecAttrs": {
						"GSTIN": [vGstin]
					}
				}
			};
			this._getSignFileStatus(searchInfo);
		},

		_getSignFileStatus: function (payload) {
			debugger; //eslint-disable-line
			payload.req.returnType = "ITC04";
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstnSaveFileStatus.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					data.resp.submit = true;
					that.byId("dpITC04Summary").setModel(new JSONModel(data.resp), "SignFileStatus");
				});
		},

		onPressClear: function () {
			this.byId("slGstin").setSelectedKeys([]);
			this.byId("slPFinancialyear").setSelectedKey("2022");
			this.byId("slPQTaxPeriod").setSelectedKey("H1");
			var flag = "P"
			this.taxPeriodFY(flag);
			this._getProcessedData();
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.ITC04
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.ITC04
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.ITC04
		 */
		//	onExit: function() {
		//
		//	}

	});

});