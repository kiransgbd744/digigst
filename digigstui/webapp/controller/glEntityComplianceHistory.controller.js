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
], function (Device, BaseController, Formatter, JSONModel, MessageBox, Filter, FilterOperator, Export, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.glEntityComplianceHistory", {

		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ComplianceHistory
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("glEntityComplianceHistory").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function () {
			debugger;
			this.getView().byId("sbComplianceHistory").setSelectedKey($.sap.grpReturn);
			if ($.sap.grpReturn === "ITC04") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(true);
				this.getView().byId("idgstr9Tab").setVisible(false);
			} else if ($.sap.grpReturn === "GSTR9") {
				this.getView().byId("idgstr1Tab").setVisible(false);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(true);
			} else {
				this.getView().byId("idgstr1Tab").setVisible(true);
				this.getView().byId("iditc04Tab").setVisible(false);
				this.getView().byId("idgstr9Tab").setVisible(false);
			}
			this.Gstinlist();
			this.onFy();
		},

		Gstinlist: function () {
			var that = this;
			var req = {
				"req": {
					"returnType": this.getView().byId("sbComplianceHistory").getSelectedKey(),
					"entityId": $.sap.grpentityID
				}
			};

			var Gstr2APath = "/aspsapapi/getGstinsWithDataSec.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					debugger;
					var data1 = JSON.parse(data);
					if (data1.hdr.status === "S") {
						data1.resp.unshift({
							gstin: "All"
						});
						that.getView().setModel(new JSONModel(data1.resp), "GSTIN");
						that.getView().getModel("GSTIN").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "GSTIN");
						that.getView().getModel("GSTIN").refresh(true);
					}
				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "GSTIN");
				that.getView().getModel("GSTIN").refresh(true);
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
					debugger;
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
						//that.onPrsSearchGstr1Go(data.resp.finYears[0].fy);
						if ($.sap.grpReturn === "ITC04") {
							if (data.resp.finYears[0].key === "2021") {
								that.byId("col1").setVisible(true);
								that.byId("col2").setVisible(true);
								that.byId("col3").setVisible(false);
								that.byId("col4").setVisible(false);
								that.byId("col5").setVisible(false);
								that.byId("col6").setVisible(true);
							} else if (Number(data.resp.finYears[0].key) < 2021) {
								that.byId("col1").setVisible(true);
								that.byId("col2").setVisible(true);
								that.byId("col3").setVisible(true);
								that.byId("col4").setVisible(true);
								that.byId("col5").setVisible(false);
								that.byId("col6").setVisible(false);
							} else {
								that.byId("col1").setVisible(false);
								that.byId("col2").setVisible(false);
								that.byId("col3").setVisible(false);
								that.byId("col4").setVisible(false);
								that.byId("col5").setVisible(true);
								that.byId("col6").setVisible(true);
							}
						}
						that.getAllReturnsData(data.resp.finYears[0].fy, data.resp.finYears[0].key);
						/*var aGstin = that.byId("slGet2aProcessGstinNew").getSelectedKeys();
						var postData = {
							"req": {
								"returnType": $.sap.grpReturn,
								"entity": $.sap.grpentityID,
								"financialYear": data.resp.finYears[0].fy,
								"dataSecAttrs": {
									"GSTIN": aGstin.includes("All") ? [] : aGstin
								}
							}
						};
						that.getGSTR1DataFinal(postData);*/
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

		apiLimt: function () {
			debugger;
			//this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: "/aspsapapi/getLimitAndUsageCount.do",
					contentType: "application/json"
						//data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//data = JSON.parse(data);
					var oComplienceHistory = new sap.ui.model.json.JSONModel(data.resp);
					oView.setModel(oComplienceHistory, "ApiLimit");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("complienceHistory : Error");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
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
			this.getAllReturnsData();
		},

		onPressBack: function () {
			this.getRouter().navTo("GrpLComplianceHistory");
		},

		onChangeSegment: function () {
			debugger;
			this.Gstinlist();
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			//var key = this.getView().byId("dtFinYearGstrNew").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				var key = this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()].key;
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

		getAllReturnsData: function (financialYear, fKey) {
			debugger;
			var fy;
			if (this.oView.byId("dtFinYearGstrNew").getSelectedKey() === "") {
				fy = financialYear;
			} else {
				fy = this.oView.byId("dtFinYearGstrNew").getSelectedKey();
			}
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"entity": $.sap.grpentityID,
					"financialYear": fy,
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};

			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey === "ITC04") {
				//var key = this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()].key;
				var key;
				if (this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()] === undefined) {
					key = fKey;
				} else {
					key = this.getModel("oFyModel").getData().finYears[this.byId("dtFinYearGstrNew").getSelectedIndex()].key;
				}
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
				postData.req.returnType = "ITC04";
				this.getITC04DataFinal(postData);
			} else if (vSelectedKey == "GSTR9") {
				postData.req.returnType = "GSTR9";
				this.getGSTR9DataFinal(postData);
			} else {
				if (vSelectedKey == "GSTR1") {
					postData.req.returnType = "GSTR1";
				} else if (vSelectedKey == "GSTR6") {
					postData.req.returnType = "GSTR6";
				} else if (vSelectedKey == "GSTR3B") {
					postData.req.returnType = "GSTR3B";
				} else if (vSelectedKey == "GSTR7") {
					postData.req.returnType = "GSTR7"
				}
				this.getGSTR1DataFinal(postData);
			}
		},

		getGSTR1DataFinal: function (postData) {
			debugger;
			this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/complienceHistory.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					data = JSON.parse(data);
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = that.financialYear;
						var oComplienceHistory = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					MessageBox.error("No GSTIN's are available");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},

		getITC04DataFinal: function (postData) {
			debugger;

			this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getItc04Compliance.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = that.financialYear;
						var oComplienceHistory = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					}

				}).fail(function (jqXHR, status, err) {
					that.apiLimt();
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("No GSTIN's are available");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},

		getGSTR9DataFinal: function (postData) {
			debugger;
			this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr9Compliance.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					} else {
						data.resp.visFlag = true;
						data.resp.financialYear = that.financialYear;
						var oComplienceHistory = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					MessageBox.error("No GSTIN's are available");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},
		onPressDownloadReport: function () {
			debugger;
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"entity": $.sap.grpentityID,
					"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			}

			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				postData.req.returnType = "ITC04"
				var oSelectedItem = this.getView().byId("iditc04Tab").getSelectedIndices();
				var oModelForTab1 = this.byId("iditc04Tab").getModel("ComplienceHistory").getData().resp.ComplienceSummeryRespDto;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please Select at least one GSTIN");
					return;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			} else if (vSelectedKey == "GSTR9") {
				postData.req.returnType = "GSTR9"
				var oSelectedItem = this.getView().byId("idgstr9Tab").getSelectedIndices();
				var oModelForTab1 = this.byId("idgstr9Tab").getModel("ComplienceHistory").getData().resp.ComplienceSummeryRespDto;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please Select at least one GSTIN");
					return;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			} else {
				if (vSelectedKey == "GSTR1") {
					postData.req.returnType = "GSTR1";
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
			this.excelDownload(postData, "/aspsapapi/ComplainceReportDownload.do");
			this.apiLimt();

		},
		onPressInitiateCall: function () {
			debugger;
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"entity": $.sap.grpentityID,
					"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			}
			this.authState = false;
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey();
			if (vSelectedKey == "ITC04") {
				postData.req.returnType = "ITC04"
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
				postData.req.returnType = "GSTR9"
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
					// if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
					// 	this.authState = true;
					// }
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
			}
			postData.req.dataSecAttrs.GSTIN = aGstin;
			// if (this.authState) {
			// 	MessageBox.show(
			// 		"Auth token is inactive for selected GSTIN, please select only activate GSTIN and retry.", {
			// 			icon: MessageBox.Icon.WARNING,
			// 			title: "Warning"
			// 		});
			// 	return;
			// }
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
			debugger;

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
					that.apiLimt();
					MessageBox.success(data.resp.status);
					var oCHStatus = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oCHStatus, "CHStatus");
					that.getAllReturnsData();
				}).fail(function (jqXHR, status, err) {
					that.apiLimt();
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr9Compliance : Error");

				});
			});
		},

		getAllGSTIN: function () {
			debugger; //eslint-disable-line

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
			// debugger; //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

	});

});