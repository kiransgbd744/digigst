sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, Formatter, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.CustomizeReport", {
		onInit: function () {
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.json", "/CustomizeReport.json"));
			this.getView().setModel(oModel, "InvoiceCheck");
			this.byId("labId").setText("Basic Details");
			this.finalArr = [];
			this.basicDetails = [];
			this.DigiSpec = [];
			this.EnvResp = [];
			this.GLDet = [];
			this.OrgDet = [];
			this.UsrDet = [];
			this.OrdDet = [];
			this.OthDet = [];
			this.ITDet = [];
			this.ItmDet = [];
			this.ExpDet = [];
			this.PartyInv = [];
			this.TransDet = [];
			var oComplienceHistory = new sap.ui.model.json.JSONModel();
			//oComplienceHistory.setSizeLimit(this.finalArr.length);
			oComplienceHistory.setData(this.finalArr);
			this.getView().setModel(oComplienceHistory, "ReportTemplate");
			this.getView().getModel("ReportTemplate").refresh(true);
			var GSTReturnsStatus = [{
				"key": "GSTR1_TRANS_LEVEL",
				"text": "GSTR1 processed records",
				"enabled": false,
				"select": true
			}, {
				"key": "GSTR3Bprocessedrecords",
				"text": "GSTR3B processed records",
				"enabled": false,
				"select": false
			}, {
				"key": "INV_MANAGMENT_REPORT",
				"text": "Invoice Management records",
				"enabled": false,
				"select": false
			}];
			oComplienceHistory.setData(GSTReturnsStatus);
			this.getView().setModel(oComplienceHistory, "Template");
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.report = "GSTR1_TRANS_LEVEL";
				this.getReports(this.report);
				this.timeStamp();
			}
		},

		timeStamp: function () {
			var that = this;
			var req = {
				"entityId": $.sap.entityID
			};

			var url = "/aspsapapi/customizedUserLog.do";
			sap.ui.core.BusyIndicator.show(0);
			debugger;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oComplienceHistory1 = new sap.ui.model.json.JSONModel(data.resp.requestDetails);
					that.getView().setModel(oComplienceHistory1, "timeStamp");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		getReports: function (reportType) {
			debugger;
			this.finalArr = [];
			this.basicDetails = [];
			this.DigiSpec = [];
			this.EnvResp = [];
			this.GLDet = [];
			this.OrgDet = [];
			this.UsrDet = [];
			this.OrdDet = [];
			this.OthDet = [];
			this.ITDet = [];
			this.ItmDet = [];
			this.ExpDet = [];
			this.PartyInv = [];
			this.TransDet = [];
			/*var oComplienceHistory = new sap.ui.model.json.JSONModel([]);
			this.getView().setModel(oComplienceHistory, "ReportTemplate");
			this.getView().getModel("ReportTemplate").refresh(true);*/
			/*var oComplienceHistory = new sap.ui.model.json.JSONModel();
			oComplienceHistory.setSizeLimit(this.finalArr.length);
			oComplienceHistory.setData(this.finalArr);
			this.getView().setModel(oComplienceHistory, "ReportTemplate");
			this.getView().getModel("ReportTemplate").refresh(true);*/

			//this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			//var oComplienceHistory1 = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: "/aspsapapi/getCustReptData.do?entityId=" + $.sap.entityID + "&reportType=" + reportType,
					//url: "/aspsapapi/getCustReptData.do?entityId=43&reportType=GSTR1_TRANS_LEVEL",
					contentType: "application/json"
						//data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					debugger;
					//data = JSON.parse(data);
					//this.basicDetails
					for (var i = 0; i < data.resp.Basic_Details.length; i++) {
						if (data.resp.Basic_Details[i].visible === true) {
							that.basicDetails.push(data.resp.Basic_Details[i]);
						}
					}
					for (var j = 0; j < data.resp.Parties_Inv.length; j++) {
						if (data.resp.Parties_Inv[j].visible === true) {
							that.PartyInv.push(data.resp.Parties_Inv[j]);
						}
					}
					for (var k = 0; k < data.resp.Export_Details.length; k++) {
						if (data.resp.Export_Details[k].visible === true) {
							that.ExpDet.push(data.resp.Export_Details[k]);
						}
					}
					for (var l = 0; l < data.resp.ItemVal_Details.length; l++) {
						if (data.resp.ItemVal_Details[l].visible === true) {
							that.ItmDet.push(data.resp.ItemVal_Details[l]);
						}
					}
					for (var m = 0; m < data.resp.IncTax_Details.length; m++) {
						if (data.resp.IncTax_Details[m].visible === true) {
							that.ITDet.push(data.resp.IncTax_Details[m]);
						}
					}
					for (var n = 0; n < data.resp.Other_Details.length; n++) {
						if (data.resp.Other_Details[n].visible === true) {
							that.OthDet.push(data.resp.Other_Details[n]);
						}
					}
					for (var o = 0; o < data.resp.OrderRef_Details.length; o++) {
						if (data.resp.OrderRef_Details[o].visible === true) {
							that.OrdDet.push(data.resp.OrderRef_Details[o]);
						}
					}
					for (var p = 0; p < data.resp.UserDef_Details.length; p++) {
						if (data.resp.UserDef_Details[p].visible === true) {
							that.UsrDet.push(data.resp.UserDef_Details[p]);
						}
					}
					for (var q = 0; q < data.resp.OrgHei_Details.length; q++) {
						if (data.resp.OrgHei_Details[q].visible === true) {
							that.OrgDet.push(data.resp.OrgHei_Details[q]);
						}
					}
					for (var r = 0; r < data.resp.GL_Details.length; r++) {
						if (data.resp.GL_Details[r].visible === true) {
							that.GLDet.push(data.resp.GL_Details[r]);
						}
					}
					for (var s = 0; s < data.resp.EinvEwbGstResp.length; s++) {
						if (data.resp.EinvEwbGstResp[s].visible === true) {
							that.EnvResp.push(data.resp.EinvEwbGstResp[s]);
						}
					}
					for (var t = 0; t < data.resp.DigiGst_Spec.length; t++) {
						if (data.resp.DigiGst_Spec[t].visible === true) {
							that.DigiSpec.push(data.resp.DigiGst_Spec[t]);
						}
					}
					for (var u = 0; u < data.resp.Trans_Details.length; u++) {
						if (data.resp.Trans_Details[u].visible === true) {
							that.TransDet.push(data.resp.Trans_Details[u]);
						}
					}
					var oComplienceHistory = new sap.ui.model.json.JSONModel(data.resp);
					oView.setModel(oComplienceHistory, "Reports");
					// var final = "";
					// final = that.finalArr.concat(that.basicDetails, that.DigiSpec, that.EnvResp, that.GLDet, that.OrgDet, that.UsrDet, that.OrdDet,
					// 	that.OthDet, that.ITDet, that.ItmDet, that.ExpDet, that.PartyInv, that.TransDet);
					// debugger;
					// var oComplienceHistory1 = new sap.ui.model.json.JSONModel(final);
					// oView.setModel(oComplienceHistory1, "ReportTemplate");
					that.onSelectAll();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("complienceHistory : Error");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},

		onPress: function (oEvt) {
			debugger;
			var selKey = oEvt.getSource().getTitle();
			this.byId("labId").setText(selKey);
			if (selKey === "Basic Details") {
				this.byId("Basic_Details").setVisible(true);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Parties Involved") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(true);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Exports Details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(true);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Line Item & value details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(true);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "TCS / TDS (Income-Tax)") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(true);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Other details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(true);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Order Reference details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(true);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "User Defined fields") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(true);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Org. heirarchy") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(true);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "GL Details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(true);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "EINV, EWB, GST Return responses") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(true);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "DigiGST Specific fields") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(true);
				this.byId("tab13").setVisible(false);
			} else if (selKey === "Transport Details") {
				this.byId("Basic_Details").setVisible(false);
				this.byId("tab2").setVisible(false);
				this.byId("tab3").setVisible(false);
				this.byId("tab4").setVisible(false);
				this.byId("tab5").setVisible(false);
				this.byId("tab6").setVisible(false);
				this.byId("tab7").setVisible(false);
				this.byId("tab8").setVisible(false);
				this.byId("tab9").setVisible(false);
				this.byId("tab10").setVisible(false);
				this.byId("tab11").setVisible(false);
				this.byId("tab12").setVisible(false);
				this.byId("tab13").setVisible(true);
			}
		},

		onSelectBD: function (oEvt) {
			debugger;
			this.basicDetails = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().Basic_Details;

			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.basicDetails.push(data[i]);

				} else {
					data[i].visible = flag;
					this.basicDetails = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			/*var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			this.getView().setModel(oComplienceHistory, "ReportTemplate");*/
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectSingle: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.basicDetails.push(selKey);
			} else {
				for (var i = 0; i < this.basicDetails.length; i++) {
					if (this.basicDetails[i].field === selKey.field) {
						this.basicDetails.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectPI: function (oEvt) {
			this.PartyInv = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().Parties_Inv;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.PartyInv.push(data[i]);

				} else {
					data[i].visible = flag;
					this.PartyInv = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectPI: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.PartyInv.push(selKey);
			} else {
				for (var i = 0; i < this.PartyInv.length; i++) {
					if (this.PartyInv[i].field === selKey.field) {
						this.PartyInv.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectExpDet: function (oEvt) {
			this.ExpDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().Export_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.ExpDet.push(data[i]);

				} else {
					data[i].visible = flag;
					this.ExpDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectED: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.ExpDet.push(selKey);
			} else {
				for (var i = 0; i < this.ExpDet.length; i++) {
					if (this.ExpDet[i].field === selKey.field) {
						this.ExpDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectItmDet: function (oEvt) {
			this.ItmDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().ItemVal_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.ItmDet.push(data[i]);

				} else {
					data[i].visible = flag;
					this.ItmDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectID: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.ItmDet.push(selKey);
			} else {
				for (var i = 0; i < this.ItmDet.length; i++) {
					if (this.ItmDet[i].field === selKey.field) {
						this.ItmDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectITDet: function (oEvt) {
			this.ITDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().IncTax_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.ITDet.push(data[i]);

				} else {
					data[i].visible = flag;
					this.ITDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectITDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.ITDet.push(selKey);
			} else {
				for (var i = 0; i < this.ITDet.length; i++) {
					if (this.ITDet[i].field === selKey.field) {
						this.ITDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectOthDet: function (oEvt) {
			this.OthDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().Other_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.OthDet.push(data[i]);

				} else {
					data[i].visible = flag;
					this.OthDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectOthDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.OthDet.push(selKey);
			} else {
				for (var i = 0; i < this.OthDet.length; i++) {
					if (this.OthDet[i].field === selKey.field) {
						this.OthDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectOrdDet: function (oEvt) {
			this.OrdDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().OrderRef_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.OrdDet.push(data[i]);
				} else {
					data[i].visible = flag;
					this.OrdDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectOrdDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.OrdDet.push(selKey);
			} else {
				for (var i = 0; i < this.OrdDet.length; i++) {
					if (this.OrdDet[i].field === selKey.field) {
						this.OrdDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectUsrDet: function (oEvt) {
			this.UsrDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().UserDef_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.UsrDet.push(data[i]);
				} else {
					data[i].visible = flag;
					this.UsrDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectUsrDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.UsrDet.push(selKey);
			} else {
				for (var i = 0; i < this.UsrDet.length; i++) {
					if (this.UsrDet[i].field === selKey.field) {
						this.UsrDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectOrgDet: function (oEvt) {
			this.OrgDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().OrgHei_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.OrgDet.push(data[i]);
				} else {
					data[i].visible = flag;
					this.OrgDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectOrgDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.OrgDet.push(selKey);
			} else {
				for (var i = 0; i < this.OrgDet.length; i++) {
					if (this.OrgDet[i].field === selKey.field) {
						this.OrgDet.splice(i, 1);
					}
				}
			}
			this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet, this.OthDet,
				this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectGLDet: function (oEvt) {
			this.GLDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().GL_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.GLDet.push(data[i]);
				} else {
					data[i].visible = flag;
					this.GLDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectGLDet: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.GLDet.push(selKey);
			} else {
				for (var i = 0; i < this.GLDet.length; i++) {
					if (this.GLDet[i].field === selKey.field) {
						this.GLDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectEnvResp: function (oEvt) {
			this.EnvResp = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().EinvEwbGstResp;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.EnvResp.push(data[i]);
				} else {
					data[i].visible = flag;
					this.EnvResp = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectEnv: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.EnvResp.push(selKey);
			} else {
				for (var i = 0; i < this.EnvResp.length; i++) {
					if (this.EnvResp[i].field === selKey.field) {
						this.EnvResp.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectDigiSpec: function (oEvt) {
			this.DigiSpec = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().DigiGst_Spec;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.DigiSpec.push(data[i]);
				} else {
					data[i].visible = flag;
					this.DigiSpec = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectDigi: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.DigiSpec.push(selKey);
			} else {
				for (var i = 0; i < this.DigiSpec.length; i++) {
					if (this.DigiSpec[i].field === selKey.field) {
						this.DigiSpec.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSelectTrans: function (oEvt) {
			this.TransDet = [];
			var flag = oEvt.getSource().getSelected();
			var data = this.getView().getModel("Reports").getData().Trans_Details;
			for (var i = 0; i < data.length; i++) {
				if (flag === true) {
					data[i].visible = flag;
					this.TransDet.push(data[i]);
				} else {
					data[i].visible = flag;
					this.TransDet = [];
				}
			}
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onSingleSelectTans: function (oEvt) {
			debugger;
			var flag = oEvt.getSource().getSelected();
			var selKey = oEvt.getSource().getParent().getBindingContext("Reports").getObject();
			if (flag) {
				this.TransDet.push(selKey);
			} else {
				for (var i = 0; i < this.TransDet.length; i++) {
					if (this.TransDet[i].field === selKey.field) {
						this.TransDet.splice(i, 1);
					}
				}
			}
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onPressSaveChanges: function () {
			debugger;
			var that = this;
			var result = [];
			this.basicDetails.forEach(function (item) {
				if (result.indexOf(item) < 0) {
					result.push(item);
				}
			});
			this.basicDetails = result;

			var result1 = [];
			this.DigiSpec.forEach(function (item) {
				if (result1.indexOf(item) < 0) {
					result1.push(item);
				}
			});
			this.DigiSpec = result1;

			var result2 = [];
			this.EnvResp.forEach(function (item) {
				if (result2.indexOf(item) < 0) {
					result2.push(item);
				}
			});
			this.EnvResp = result2;

			var result3 = [];
			this.ExpDet.forEach(function (item) {
				if (result3.indexOf(item) < 0) {
					result3.push(item);
				}
			});
			this.ExpDet = result3;

			var result4 = [];
			this.GLDet.forEach(function (item) {
				if (result4.indexOf(item) < 0) {
					result4.push(item);
				}
			});
			this.GLDet = result4;

			var result5 = [];
			this.ITDet.forEach(function (item) {
				if (result5.indexOf(item) < 0) {
					result5.push(item);
				}
			});
			this.ITDet = result5;

			var result6 = [];
			this.ItmDet.forEach(function (item) {
				if (result6.indexOf(item) < 0) {
					result6.push(item);
				}
			});
			this.ItmDet = result6;

			var result7 = [];
			this.OrdDet.forEach(function (item) {
				if (result7.indexOf(item) < 0) {
					result7.push(item);
				}
			});
			this.OrdDet = result7;

			var result8 = [];
			this.OrgDet.forEach(function (item) {
				if (result8.indexOf(item) < 0) {
					result8.push(item);
				}
			});
			this.OrgDet = result8;

			var result9 = [];
			this.OthDet.forEach(function (item) {
				if (result9.indexOf(item) < 0) {
					result9.push(item);
				}
			});
			this.OthDet = result9;

			var result10 = [];
			this.PartyInv.forEach(function (item) {
				if (result10.indexOf(item) < 0) {
					result10.push(item);
				}
			});
			this.PartyInv = result10;

			var result11 = [];
			this.UsrDet.forEach(function (item) {
				if (result11.indexOf(item) < 0) {
					result11.push(item);
				}
			});
			this.UsrDet = result11;

			var result12 = [];
			this.TransDet.forEach(function (item) {
				if (result12.indexOf(item) < 0) {
					result12.push(item);
				}
			});
			this.TransDet = result12;

			var Request = {
				"req": {
					"entityId": $.sap.entityID,
					"reportType": this.report,
					"reportId": "1",
					"Basic_Details": this.basicDetails,
					"DigiGst_Spec": this.DigiSpec,
					"EinvEwbGstResp": this.EnvResp,
					"Export_Details": this.ExpDet,
					"GL_Details": this.GLDet,
					"IncTax_Details": this.ITDet,
					"ItemVal_Details": this.ItmDet,
					"OrderRef_Details": this.OrdDet,
					"OrgHei_Details": this.OrgDet,
					"Other_Details": this.OthDet,
					"Parties_Inv": this.PartyInv,
					"UserDef_Details": this.UsrDet,
					"Trans_Details": this.TransDet
				}
			};
			var url = "/aspsapapi/saveCustReptData.do";
			sap.ui.core.BusyIndicator.show(0);
			debugger;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success("Reports Successfully Submitted");
					that.report = "GSTR1_TRANS_LEVEL";
					that.getReports(that.report);
					that.timeStamp();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onSelectAll: function () {
			this.finalArr = [];
			this.basicDetails = [];
			this.DigiSpec = [];
			this.EnvResp = [];
			this.GLDet = [];
			this.OrgDet = [];
			this.UsrDet = [];
			this.OrdDet = [];
			this.OthDet = [];
			this.ITDet = [];
			this.ItmDet = [];
			this.ExpDet = [];
			this.PartyInv = [];
			this.TransDet = [];
			var data = this.getView().getModel("Reports").getData();
			for (var i = 0; i < data.Basic_Details.length; i++) {
				data.Basic_Details[i].visible = true;
				this.basicDetails.push(data.Basic_Details[i]);
			}
			for (var i = 0; i < data.Parties_Inv.length; i++) {
				data.Parties_Inv[i].visible = true;
				this.PartyInv.push(data.Parties_Inv[i]);
			}

			for (var i = 0; i < data.Export_Details.length; i++) {
				data.Export_Details[i].visible = true;
				this.ExpDet.push(data.Export_Details[i]);
			}

			for (var i = 0; i < data.ItemVal_Details.length; i++) {
				data.ItemVal_Details[i].visible = true;
				this.ItmDet.push(data.ItemVal_Details[i]);
			}

			for (var i = 0; i < data.IncTax_Details.length; i++) {
				data.IncTax_Details[i].visible = true;
				this.ITDet.push(data.IncTax_Details[i]);
			}

			for (var i = 0; i < data.Other_Details.length; i++) {
				data.Other_Details[i].visible = true;
				this.OthDet.push(data.Other_Details[i]);
			}

			for (var i = 0; i < data.OrderRef_Details.length; i++) {
				data.OrderRef_Details[i].visible = true;
				this.OrdDet.push(data.OrderRef_Details[i]);
			}

			for (var i = 0; i < data.UserDef_Details.length; i++) {
				data.UserDef_Details[i].visible = true;
				this.UsrDet.push(data.UserDef_Details[i]);
			}

			for (var i = 0; i < data.OrgHei_Details.length; i++) {
				data.OrgHei_Details[i].visible = true;
				this.OrgDet.push(data.OrgHei_Details[i]);
			}

			for (var i = 0; i < data.GL_Details.length; i++) {
				data.GL_Details[i].visible = true;
				this.GLDet.push(data.GL_Details[i]);
			}

			for (var i = 0; i < data.EinvEwbGstResp.length; i++) {
				data.EinvEwbGstResp[i].visible = true;
				this.EnvResp.push(data.EinvEwbGstResp[i]);
			}

			for (var i = 0; i < data.DigiGst_Spec.length; i++) {
				data.DigiGst_Spec[i].visible = true;
				this.DigiSpec.push(data.DigiGst_Spec[i]);
			}

			for (var i = 0; i < data.Trans_Details.length; i++) {
				data.Trans_Details[i].visible = true;
				this.TransDet.push(data.Trans_Details[i]);
			}

			this.byId("NonPANCheckId").setSelected(true);
			this.byId("PiId").setSelected(true);
			this.byId("EDid").setSelected(true);
			this.byId("LIid").setSelected(true);
			this.byId("TCSid").setSelected(true);
			this.byId("Odid").setSelected(true);
			this.byId("OrdId").setSelected(true);
			this.byId("UdfID").setSelected(true);
			this.byId("Ohid").setSelected(true);
			this.byId("GLid").setSelected(true);
			this.byId("EinvId").setSelected(true);
			this.byId("DSFid").setSelected(true);
			this.byId("TDid").setSelected(true);

			//"Trans_Details": this.TransDet
			this.getView().getModel("Reports").refresh(true);
			var final = this.finalArr.concat(this.basicDetails, this.DigiSpec, this.EnvResp, this.GLDet, this.OrgDet, this.UsrDet, this.OrdDet,
				this.OthDet, this.ITDet, this.ItmDet, this.ExpDet, this.PartyInv, this.TransDet);
			//var oComplienceHistory = new sap.ui.model.json.JSONModel(final);
			//this.getView().setModel(oComplienceHistory, "ReportTemplate");
			var oComplienceHistory1 = new sap.ui.model.json.JSONModel();
			//oComplienceHistory1.setSizeLimit(final.length);
			oComplienceHistory1.setData(final);
			this.getView().setModel(oComplienceHistory1, "ReportTemplate");
		},

		onDeSelectAll: function () {
			/*var data = this.getView().getModel("Reports").getData();
			for (var i = 0; i < data.Basic_Details.length; i++) {
				data.Basic_Details[i].visible = false;
				this.basicDetails.push(data.Basic_Details[i]);
			}
			for (var i = 0; i < data.Parties_Inv.length; i++) {
				data.Parties_Inv[i].visible = false;
				this.PartyInv.push(data.Parties_Inv[i]);
			}

			for (var i = 0; i < data.Export_Details.length; i++) {
				data.Export_Details[i].visible = false;
				this.ExpDet.push(data.Export_Details[i]);
			}

			for (var i = 0; i < data.ItemVal_Details.length; i++) {
				data.ItemVal_Details[i].visible = false;
				this.ItmDet.push(data.ItemVal_Details[i]);
			}

			for (var i = 0; i < data.IncTax_Details.length; i++) {
				data.IncTax_Details[i].visible = false;
				this.ITDet.push(data.IncTax_Details[i]);
			}

			for (var i = 0; i < data.Other_Details.length; i++) {
				data.Other_Details[i].visible = false;
				this.OthDet.push(data.Other_Details[i]);
			}

			for (var i = 0; i < data.OrderRef_Details.length; i++) {
				data.OrderRef_Details[i].visible = false;
				this.OrdDet.push(data.OrderRef_Details[i]);
			}

			for (var i = 0; i < data.UserDef_Details.length; i++) {
				data.UserDef_Details[i].visible = false;
				this.UsrDet.push(data.UserDef_Details[i]);
			}

			for (var i = 0; i < data.OrgHei_Details.length; i++) {
				data.OrgHei_Details[i].visible = false;
				this.OrgDet.push(data.OrgHei_Details[i]);
			}

			for (var i = 0; i < data.GL_Details.length; i++) {
				data.GL_Details[i].visible = false;
				this.GLDet.push(data.GL_Details[i]);
			}

			for (var i = 0; i < data.EinvEwbGstResp.length; i++) {
				data.EinvEwbGstResp[i].visible = false;
				this.EnvResp.push(data.EinvEwbGstResp[i]);
			}

			for (var i = 0; i < data.DigiGst_Spec.length; i++) {
				data.DigiGst_Spec[i].visible = false;
				this.DigiSpec.push(data.DigiGst_Spec[i]);
			}

			for (var i = 0; i < data.Trans_Details.length; i++) {
				data.Trans_Details[i].visible = false;
				this.TransDet.push(data.Trans_Details[i]);
			}*/

			//this.getView().getModel("Reports").refresh(true);
			this.finalArr = [];
			this.basicDetails = [];
			this.DigiSpec = [];
			this.EnvResp = [];
			this.GLDet = [];
			this.OrgDet = [];
			this.UsrDet = [];
			this.OrdDet = [];
			this.OthDet = [];
			this.ITDet = [];
			this.ItmDet = [];
			this.ExpDet = [];
			this.PartyInv = [];
			this.TransDet = [];
			this.byId("NonPANCheckId").setSelected(false);
			this.byId("PiId").setSelected(false);
			this.byId("EDid").setSelected(false);
			this.byId("LIid").setSelected(false);
			this.byId("TCSid").setSelected(false);
			this.byId("Odid").setSelected(false);
			this.byId("OrdId").setSelected(false);
			this.byId("UdfID").setSelected(false);
			this.byId("Ohid").setSelected(false);
			this.byId("GLid").setSelected(false);
			this.byId("EinvId").setSelected(false);
			this.byId("DSFid").setSelected(false);
			this.byId("TDid").setSelected(false);
			var oComplienceHistory = new sap.ui.model.json.JSONModel();
			//oComplienceHistory.setSizeLimit(this.finalArr.length);
			oComplienceHistory.setData(this.finalArr);
			this.getView().setModel(oComplienceHistory, "ReportTemplate");
			this.getView().getModel("ReportTemplate").refresh(true);
		},

		onMenuItemPressAnx1down: function (oEvt) {
			this.report = oEvt.getParameter("item").getKey();
			this.getReports(this.report);
		},

		onButtonPress: function (oEvt) {
			var oButton = oEvt.getSource();
			this.byId("actionSheet").openBy(oButton);
		}

	});

});