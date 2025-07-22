sap.ui.define([
	"sap/ui/Device",
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/core/util/Export",
	"sap/m/Button",
	"sap/m/Dialog",
	"sap/ui/core/util/ExportTypeCSV"
], function (Device, BaseController, Formatter, JSONModel, MessageBox, Filter, FilterOperator, Export, Button, Dialog, ExportTypeCSV) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GrpLComplianceHistory", {
		formatter: Formatter,
		onInit: function () {
			this.getView().setModel(new JSONModel({
				"resp": {
					"visFlag": false
				}
			}), "CHStatus");
			if (this.getOwnerComponent().getModel("GroupPermission").getProperty("/G1")) {
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.onFy(),
						this.getListEntity()
					])
					.then(function (results) {
						var payload = {
							"req": {
								"returnType": "GSTR1",
								"financialYear": results[0].finYears[0].fy,
								"dataSecAttrs": {
									"entityIds": []
								}
							}
						};
						this.getGSTR1DataFinal(payload);
					}.bind(this))
					.catch(function (error) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
			this.getOwnerComponent().getRouter().getRoute("GrpLComplianceHistory").attachPatternMatched(this._onRouteMatched, this);
			//this.getAllReturnsData();
		},

		_onRouteMatched: function () {
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(true);
				this.getView().byId("idgstr9Tab").setVisible(false);
				var key = this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()].key;
				if (key === "2021") {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(true);
				} else if (Number(key) < 2021) {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(true);
					this.byId("col4").setVisible(true);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(false);
				} else {
					this.byId("col1").setVisible(false);
					this.byId("col2").setVisible(false);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(true);
					this.byId("col6").setVisible(true);
				}
			} else if (vSelectedKey == "GSTR9") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(true);

			} else {
				this.getView().byId("idgstr1Tab").setVisible(true);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(false);
			}
			//this.getAllReturnsData();
		},

		handleLinkPressGetGSTINMain1: function (oEvt) {
			$.sap.grpentityID = oEvt.getSource().getBindingContext("ComplienceHistory").getObject().entityId;
			$.sap.grpReturn = this.getView().byId("sbComplianceHistory").getSelectedKey();
			this.getRouter().navTo("glEntityComplianceHistory");
		},

		onFy: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "oFyModel");
						resolve(data.resp);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "oFyModel");
						reject(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		getListEntity: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getDataSecurityForUser.do",
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						data.resp.unshift({
							entityId: "All",
							entityName: "All"
						});
						this.getView().setModel(new JSONModel(data.resp), "GrpEntity");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		onPressEmail: function () {
			this.getView().byId("idDPComplianceHistory").setVisible(false);
			this.getView().byId("id_EmailPg").setVisible(true);
		},
		onPressEmailBack: function () {
			this.getView().byId("idDPComplianceHistory").setVisible(true);
			this.getView().byId("id_EmailPg").setVisible(false);
		},

		onPressGo: function () {
			this.onChangeSegment();
		},

		onChangeSegment: function () {
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey(),
				key = this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()].key;
			if (vSelectedKey == "ITC04") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(true);
				this.getView().byId("idgstr9Tab").setVisible(false);
				if (key === "2021") {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(true);
				} else if (Number(key) < 2021) {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(true);
					this.byId("col4").setVisible(true);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(false);
				} else {
					this.byId("col1").setVisible(false);
					this.byId("col2").setVisible(false);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(true);
					this.byId("col6").setVisible(true);
				}
			} else if (vSelectedKey == "GSTR9") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(true);

			} else {
				this.getView().byId("idgstr1Tab").setVisible(true);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(false);
			}
			this.getAllReturnsData();
		},

		getAllReturnsData: function (oEntity) {
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
					"dataSecAttrs": {
						"entityIds": aGstin.includes("All") ? [] : aGstin
					}
				}
			};

			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				postData.req.returnType = "ITC04";
				this.getITC04DataFinal(postData);
			} else if (vSelectedKey == "GSTR9") {
				postData.req.returnType = "GSTR9";
				this.getGSTR9DataFinal(postData);
			} else {
				if (vSelectedKey == "GSTR1") {
					postData.req.returnType = "GSTR1";
				} else if (vSelectedKey == "GSTR1A") {
					postData.req.returnType = "GSTR1A";
				} else if (vSelectedKey == "GSTR6") {
					postData.req.returnType = "GSTR6";
				} else if (vSelectedKey == "GSTR3B") {
					postData.req.returnType = "GSTR3B";
				} else if (vSelectedKey == "GSTR7") {
					postData.req.returnType = "GSTR7";
				}
				this.getGSTR1DataFinal(postData);
			}
		},

		getGSTR1DataFinal: function (postData) {
			this.financialYear = postData.req.financialYear;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/groupComplienceHistory.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel(null);
					sap.ui.core.BusyIndicator.hide();
					data = JSON.parse(data);
					if (!data.resp.length) {
						MessageBox.information("No Data");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = this.financialYear;
						oModel.setProperty("/", data);
					}
					this.getView().setModel(oModel, "ComplienceHistory");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "ComplienceHistory");
				}.bind(this));
		},

		getITC04DataFinal: function (postData) {
			this.financialYear = postData.req.financialYear;
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGroupItc04Compliance.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oComplienceHistory = new JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = that.financialYear;
						var oComplienceHistory = new JSONModel(data);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oComplienceHistory = new JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},

		getGSTR9DataFinal: function (postData) {
			this.financialYear = postData.req.financialYear;
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGroupGstr9Compliance.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oComplienceHistory = new JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = that.financialYear;
						var oComplienceHistory = new JSONModel(data);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oComplienceHistory = new JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");
				});
			});
		},

		onPressDownloadReport: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			if (key === "TL") {
				var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
				var postData = {
					"req": {
						"returnType": null,
						"financialYear": this.byId("dtFinYearGstrNew").getSelectedKey(),
						"dataSecAttrs": {
							"entityIds": []
						}
					}
				};
				var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
				if (vSelectedKey == "ITC04") {
					postData.req.returnType = "ITC04";
					var oSelectedItem = this.getView().byId("iditc04Tab").getSelectedIndices();
					var oModelForTab1 = this.byId("iditc04Tab").getModel("ComplienceHistory").getData().resp;
					if (oSelectedItem.length == 0) { //eslint-disable-line
						sap.m.MessageBox.warning("Please Select at least one Entity Name");
						return;
					}
					var aGstin = [];
					for (var i = 0; i < oSelectedItem.length; i++) {
						aGstin.push(oModelForTab1[oSelectedItem[i]].entityId);
					}
				} else if (vSelectedKey == "GSTR9") {
					postData.req.returnType = "GSTR9";
					var oSelectedItem = this.getView().byId("idgstr9Tab").getSelectedIndices();
					var oModelForTab1 = this.byId("idgstr9Tab").getModel("ComplienceHistory").getData().resp;
					if (oSelectedItem.length == 0) { //eslint-disable-line
						sap.m.MessageBox.warning("Please Select at least one Entity Name");
						return;
					}
					var aGstin = [];
					for (var i = 0; i < oSelectedItem.length; i++) {
						aGstin.push(oModelForTab1[oSelectedItem[i]].entityId);
					}
				} else {
					if (vSelectedKey == "GSTR1") {
						postData.req.returnType = "GSTR1";
					} else if (vSelectedKey == "GSTR1A") {
						postData.req.returnType = "GSTR1A";
					}  else if (vSelectedKey == "GSTR6") {
						postData.req.returnType = "GSTR6";
					} else if (vSelectedKey == "GSTR3B") {
						postData.req.returnType = "GSTR3B";
					} else if (vSelectedKey == "GSTR7") {
						postData.req.returnType = "GSTR7";
					}
					var oSelectedItem = this.getView().byId("idgstr1Tab").getSelectedIndices();
					var oModelForTab1 = this.byId("idgstr1Tab").getModel("ComplienceHistory").getData().resp;
					if (oSelectedItem.length == 0) { //eslint-disable-line
						sap.m.MessageBox.warning("Please Select at least one Entity Name");
						return;
					}
					var aGstin = [];
					for (var i = 0; i < oSelectedItem.length; i++) {
						aGstin.push(oModelForTab1[oSelectedItem[i]].entityId);
					}
				}
				postData.req.dataSecAttrs.entityIds = aGstin;
				this.excelDownload(postData, "/aspsapapi/groupComplainceReportDownload.do");
			} else {
				var req = {
					"req": {
						"returnType": this.getView().byId("sbComplianceHistory").getSelectedKey(),
						"financialYear": this.byId("dtFinYearGstrNew").getSelectedKey()
					}
				};
				this.excelDownload(req, "/aspsapapi/groupSummaryComplainceReportDownload.do");
			}
		},

		onPressInitiateCall: function () {
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
			this.authState = false;
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				postData.req.returnType = "ITC04";
				var oSelectedItem = this.getView().byId("iditc04Tab").getSelectedIndices();
				var oModelForTab1 = this.byId("iditc04Tab").getModel("ComplienceHistory").getData().resp.ComplienceSummeryRespDto;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please Select at least one GSTIN");
					return;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
						this.authState = true;
					}
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			} else if (vSelectedKey == "GSTR9") {
				postData.req.returnType = "GSTR9";
				var oSelectedItem = this.getView().byId("idgstr9Tab").getSelectedIndices();
				var oModelForTab1 = this.byId("idgstr9Tab").getModel("ComplienceHistory").getData().resp.ComplienceSummeryRespDto;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please Select at least one GSTIN");
					return;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
						this.authState = true;
					}
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			} else {
				if (vSelectedKey == "GSTR1") {
					postData.req.returnType = "GSTR1";
				} else if (vSelectedKey == "GSTR1A") {
					postData.req.returnType = "GSTR1A";
				} else if (vSelectedKey == "GSTR6") {
					postData.req.returnType = "GSTR6";
				} else if (vSelectedKey == "GSTR3B") {
					postData.req.returnType = "GSTR3B";
				} else if (vSelectedKey == "GSTR7") {
					postData.req.returnType = "GSTR7";
				}
				var oSelectedItem = this.getView().byId("idgstr1Tab").getSelectedIndices();
				var oModelForTab1 = this.byId("idgstr1Tab").getModel("ComplienceHistory").getData().resp.ComplienceSummeryRespDto;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please Select at least one GSTIN");
					return;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			}
			postData.req.dataSecAttrs.GSTIN = aGstin;
			this.initiateCallFinal(postData);
		},

		initiateCallFinal: function (postData) {
			var that = this;
			sap.m.MessageBox.confirm("Do you want to initiate call ?", {
				styleClass: "sapUiSizeCompact",
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				onClose: function (action) {
					if (action === sap.m.MessageBox.Action.OK) {
						that.initiateCallFinal1(postData)
					}
				}
			});
		},

		initiateCallFinal1: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/initateGeClientFilingStatus.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp.status);
					var oCHStatus = new JSONModel(data);
					oView.setModel(oCHStatus, "CHStatus");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr9Compliance : Error");

				});
			});
		},

		getAllGSTIN: function () {
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "GSTR7") {
				var aGSTIN = this.getOwnerComponent().getModel("allTDSGstin").getData();

			} else if (vSelectedKey == "GSTR6") {
				var aGSTIN = this.getOwnerComponent().getModel("allISDGstin").getData();
			} else {
				var aGSTIN = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.gstin;
			}
			this.getOwnerComponent().setModel(new JSONModel(aGSTIN), "GSTIN");
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},
	});
});