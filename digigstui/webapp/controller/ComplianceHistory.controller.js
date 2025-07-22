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
	"sap/m/Dialog"
], function (Device, BaseController, Formatter, JSONModel, MessageBox, Filter, FilterOperator, Export, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ComplianceHistory", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ComplianceHistory
		 */
		onInit: function () {
			var oView = this.getView();
			var data = {
				"resp": {}
			};
			data.resp.visFlag = false;
			var oCHStatus = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oCHStatus, "CHStatus");
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.ComplianceHistory
		 */
		onAfterRendering: function () {
			
			if (this.glbEntityId !== $.sap.entityID && this.getRouter().getHashChanger().getHash()) {
				this.glbEntityId = $.sap.entityID;
				this.onFy();
			}
		},

		onFy: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getAllFy.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "GET",
						url: Gstr2APath,
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							that.getView().setModel(new JSONModel(data.resp), "oFyModel");
							that.getView().getModel("oFyModel").refresh(true);
						} else {
							that.getView().setModel(new JSONModel([]), "oFyModel");
							that.getView().getModel("oFyModel").refresh(true);
						}
						that.onChangeSegment();
					})
					.fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oFyModel");
				that.getView().getModel("oFyModel").refresh(true);
			});
		},

		apiLimt: function () {
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

		onChangeSegment: function () {
			this.getAllGSTIN();
			this.getView().byId("slGet2aProcessGstinNew").setSelectedKeys([]);
			var vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey(),
				vIndex = this.byId("dtFinYearGstrNew").getSelectedIndex() < 0 ? 0 : this.byId("dtFinYearGstrNew").getSelectedIndex(),
				key = this.getModel("oFyModel").getProperty('/finYears/' + vIndex + '/key');

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
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys(),
				vSelectedKey = this.getView().byId("sbComplianceHistory").getSelectedKey(),
				postData = {
					"req": {
						"returnType": null,
						"entity": $.sap.entityID,
						"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				}

			if (vSelectedKey == "ITC04") {
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
					postData.req.returnType = "GSTR7"
				}
				this.getGSTR1DataFinal(postData);
			}
		},

		getGSTR1DataFinal: function (postData) {
			
			postData.req.financialYear = postData.req.financialYear || this.getModel("oFyModel").getProperty('/finYears/0/key');
			this.financialYear = postData.req.financialYear;
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/complienceHistory.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						
						sap.ui.core.BusyIndicator.hide();
						data = JSON.parse(data);
						if (data.hdr.status === "E") {
							MessageBox.error(data.hdr.message);
							var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oComplienceHistory, "ComplienceHistory");
						} else {
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
						}
					})
					.fail(function (jqXHR, status, err) {
						debugger;
						sap.ui.core.BusyIndicator.hide();
						that.apiLimt();
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
						if (jqXHR.responseJSON.hdr.status === "E") {
							MessageBox.error(jqXHR.responseJSON.hdr.message);
						} else {
							MessageBox.error("complienceHistory : Error");
						}
						
					});
			});
		},

		getITC04DataFinal: function (postData) {
			this.financialYear = postData.req.financialYear;
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getItc04Compliance.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "E") {
							MessageBox.error(data.hdr.message);
							var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oComplienceHistory, "ComplienceHistory");
						} else {
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
						}
					})
					.fail(function (jqXHR, status, err) {
						that.apiLimt();
						sap.ui.core.BusyIndicator.hide();
						if (jqXHR.responseJSON.hdr.status === "E") {
							MessageBox.error(jqXHR.responseJSON.hdr.message);
						} else {
							MessageBox.error("complienceHistory : Error");
						}
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
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
						url: "/aspsapapi/getGstr9Compliance.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "E") {
							MessageBox.error(data.hdr.message);
							var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oComplienceHistory, "ComplienceHistory");
						} else {
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
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						that.apiLimt();
						if (jqXHR.responseJSON.hdr.status === "E") {
							MessageBox.error(jqXHR.responseJSON.hdr.message);
						} else {
							MessageBox.error("complienceHistory : Error");
						}
						var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oComplienceHistory, "ComplienceHistory");
					});
			});
		},

		onPressDownloadReport: function () {
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"entity": $.sap.entityID,
					"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

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
			this.excelDownload(postData, "/aspsapapi/ComplainceReportDownload.do");
			this.apiLimt();
		},

		onPressInitiateCall: function () {
			var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys();
			var postData = {
				"req": {
					"returnType": null,
					"entity": $.sap.entityID,
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
					// if (oModelForTab1[oSelectedItem[i]].authToken == "Inactive") {
					// 	this.authState = true;
					// }
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
						that.initiateCallFinal1(postData);
					}
				}
			});
		},

		_gstinsStatus: function () {
			if (!this._oDialogbulkSaveStats) {
				this._oDialogbulkSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.BulkSave", this);
				this.getView().addDependent(this._oDialogbulkSaveStats);
			}
			this._oDialogbulkSaveStats.open();
		},
		onCloseDialogBulkSave: function () {
			this._oDialogbulkSaveStats.close();
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

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.ComplianceHistory
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.ComplianceHistory
		 */
		//	onExit: function() {
		//
		//	}

	});

});