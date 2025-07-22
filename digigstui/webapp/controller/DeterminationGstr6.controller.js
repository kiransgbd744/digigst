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

	return BaseController.extend("com.ey.digigst.controller.DeterminationGstr6", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.DeterminationGstr6
		 */
		onInit: function () {
			var that = this,
				vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);

			this.byId("idTaxPeriodDetermination").setMaxDate(vDate);
			this.byId("idTaxPeriodDetermination").setDateValue(vPeriod);
			this.byId("idTaxPeriodDetermination").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxPeriodDetermination").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idPFromtaxPeriod2A").setMaxDate(vDate);
			this.byId("idPFromtaxPeriod2A").setDateValue(vPeriod);
			this.byId("idPFromtaxPeriod2A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idPFromtaxPeriod2A").$().find("input").attr("readonly", true);
				}
			});
			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);
			this._bindReqIdFilter();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				var vDate = new Date();
				this.byId("idTaxPeriodDetermination").setDateValue(vDate);
				this.glbEntityId = $.sap.entityID;
				this.getDetrminationProcessData();
				this._getUserId();
			}
			this.byId("idGetDetrmination").setVisible(true);
			this.byId("dpDetrminationSummary").setVisible(false);
			this.byId("id_RequestIDpage").setVisible(false);
		},

		_bindReqIdFilter: function () {
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
					"screenName": "gstr6Dtr"
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

		handleLinkDetrminationGstin: function (oEvent) {
			this.byId("idGetDetrmination").setVisible(false);
			this.byId("dpDetrminationSummary").setVisible(true);
			var a = oEvent.getSource().getBindingContext("Gstr6DetrPRSumData").getObject();
			var that = this;
			this.oView.byId("idPFromtaxPeriod2A").setValue(this.oView.byId("idTaxPeriodDetermination").getValue());
			this.byId("idPFromtaxPeriod2A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idPFromtaxPeriod2A").$().find("input").attr("readonly", true);
				}
			});
			this.oView.byId("idPGstinTrsUm").setSelectedKey(a.gstin);
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			this.getView().byId("idPFromtaxPeriod2AB").setDateValue(vDate);
			this.getView().byId("idPFromtaxPeriod2AB").setMaxDate(new Date());
			this.getView().byId("idPFromtaxPeriod2AB1").setDateValue(new Date());
			this.getView().byId("idPFromtaxPeriod2AB1").setMinDate(vDate);
			this.getView().byId("idPFromtaxPeriod2AB1").setMaxDate(new Date());
			// var obj = oEvent.getSource().getBindingContext("TDSProessData").getObject();
			// this.byId("idTdsSummary").setDateValue(this.byId("idTdsDate").getDateValue());
			// this.byId("slTdsSummaryGstin").setSelectedKey(obj.gstin);
			this.getDetrminationSummaryData();
			this.buttonVis();
		},

		buttonVis: function () {
			var req = {
				"req": {
					"taxPeriod": this.byId("idPFromtaxPeriod2A").getValue(),
					"gstin": this.byId("idPGstinTrsUm").getSelectedKey(),
					"returnType": "GSTR6"
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
		onPressBackDetrSum: function (oEvent) {
			this.byId("idGetDetrmination").setVisible(true);
			this.byId("dpDetrminationSummary").setVisible(false);
			// var obj = oEvent.getSource().getBindingContext("TDSProessData").getObject();
			// this.byId("idTdsSummary").setDateValue(this.byId("idTdsDate").getDateValue());
			// this.byId("slTdsSummaryGstin").setSelectedKey(obj.gstin);
			// this.getTdsSummaryData();

		},

		_getGstr6ComputeTimeStamp: function () {
			var postData = this.TimeStampPayload
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr6ComputeTimeStamp.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "ComputeTimeStamp");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr6ComputeTimeStamp.do : Error");
				});
			});
		},

		OnpressCalculateTurnOverdigi: function (oEvent, flag) {
			debugger;
			var that = this;
			this.turnoverDigiFlag = flag;
			if (flag === "P") {
				var vGetDatagstn = that.getView().byId("tabOutwarddet").getSelectedIndices();
				if (vGetDatagstn.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;

				}
				if (!this._oDialog) {
					this._oDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.TurnoverDigigst", this);
					this.getView().addDependent(this._oDialog);

				}

				var vDate = new Date();
				vDate.setDate(vDate.getDate() - 9);
				sap.ui.getCore().byId("idDterSummaryTaxFr").setDateValue(vDate);
				sap.ui.getCore().byId("idDterSummaryTaxFr").setMaxDate(new Date());
				sap.ui.getCore().byId("idDterSummaryTaxTo").setDateValue(new Date());
				sap.ui.getCore().byId("idDterSummaryTaxTo").setMinDate(vDate);
				sap.ui.getCore().byId("idDterSummaryTaxTo").setMaxDate(new Date());
				this._oDialog.open();
			} else {
				var oTablIndxPR = this.byId("idTabDistrisum").getSelectedIndices();
				if (oTablIndxPR.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;

				}
				var oModelForTab = this.byId("idTabDistrisum").getModel("Gstr6DetrPRTrSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

				}

				// var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

				var oTaxPeriodFrm = this.byId("idPFromtaxPeriod2AB").getValue();
				if (oTaxPeriodFrm === "") {
					oTaxPeriodFrm = null;
				}
				var oTaxPeriodTo = this.byId("idPFromtaxPeriod2AB1").getValue();
				if (oTaxPeriodTo === "") {
					oTaxPeriodTo = null;
				}
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"taxPeriodFrom": oTaxPeriodFrm,
						"taxPeriodTo": oTaxPeriodTo,
						"gstin": aGstin,
						"isdGstin": [this.getView().byId("idPGstinTrsUm").getSelectedKey()],
						"tableType": []
					}
				};
				this.TimeStampPayload = {
					"req": {
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				}

				MessageBox.show(
					"Do you want to Calculate Turnover(DigiGST)?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								var URL = "/aspsapapi/Gstr6CalculateTurnOverDigiGst.do";
								that.getTurnoverDigiIntrigrationFinal(postData, URL);
							}

						}
					}
				);

			}
		},
		onCloseDialogCopy: function (oEvent) {
			this._oDialog.close();
		},
		OnpressCalTurnoverGstn: function (oEvent, flag) {
			debugger;
			this.turnoverGstnFlag = flag;
			var that = this;

			if (flag === "P") {
				var vGetDatagstn = that.getView().byId("tabOutwarddet").getSelectedIndices();
				if (vGetDatagstn.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;

				}
				if (!this._oDialog1) {
					this._oDialog1 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.Turnovergstn", this);
					this.getView().addDependent(this._oDialog1);

				}
				var vDate = new Date();
				vDate.setDate(vDate.getDate() - 9);
				sap.ui.getCore().byId("idDterSummaryTaxFr1").setDateValue(vDate);
				sap.ui.getCore().byId("idDterSummaryTaxFr1").setMaxDate(new Date());
				sap.ui.getCore().byId("idDterSummaryTaxTo1").setDateValue(new Date());
				sap.ui.getCore().byId("idDterSummaryTaxTo1").setMinDate(vDate);
				sap.ui.getCore().byId("idDterSummaryTaxTo1").setMaxDate(new Date());
				// this._oDialog.open();
				this._oDialog1.open();
			} else {
				var oTablIndxPR = this.byId("idTabDistrisum").getSelectedIndices();
				if (oTablIndxPR.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;

				}

				var oModelForTab = this.byId("idTabDistrisum").getModel("Gstr6DetrPRTrSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

				}

				// var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

				var oTaxPeriodFrm = this.byId("idPFromtaxPeriod2AB").getValue();
				if (oTaxPeriodFrm === "") {
					oTaxPeriodFrm = null;
				}
				var oTaxPeriodTo = this.byId("idPFromtaxPeriod2AB1").getValue();
				if (oTaxPeriodTo === "") {
					oTaxPeriodTo = null;
				};
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"taxPeriodFrom": oTaxPeriodFrm,
						"taxPeriodTo": oTaxPeriodTo,
						"gstin": aGstin,
						"isdGstin": [this.getView().byId("idPGstinTrsUm").getSelectedKey()],
						"tableType": []
					}
				};
				this.TimeStampPayload = {
					"req": {
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				}
				MessageBox.show(
					"Do you want to Calculate Turnover(GSTN)?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								var URL = "/aspsapapi/Gstr6CalculateTurnOverGstn.do";
								that.getTurnoverGstnIntrigrationFinal(postData, URL);

							}

						}
					}
				);

			}

		},
		onDialogTurnSubmit: function (oEvent) {
			this._oDialog1.close();
		},
		onCopy1: function (oEvent, flag) {
			debugger;
			this.turnoverGstnFlag = flag;

			// if (!this._oDialog1) {
			// 	this._oDialog1 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.Turnovergstn", this);
			// 	this.getView().addDependent(this._oDialog1);

			// }
			var that = this;
			if (flag === "P") {
				var vGetDatagstn = that.getView().byId("tabOutwarddet").getSelectedIndices();
				if (vGetDatagstn.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;
				}
			} else {
				var vGetDatagstn = that.getView().byId("idTabDistrisum").getSelectedIndices();
				if (vGetDatagstn.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;
				}
			}
			//var vDate = new Date();
			if (!this._oDialogCopy1) {
				this._oDialogCopy1 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.CopyData", this);
				this.getView().addDependent(this._oDialogCopy1);

			}
			var vDate = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);

			sap.ui.getCore().byId("idMonth").setMaxDate(vDate);
			sap.ui.getCore().byId("idMonth").setDateValue(vPeriod);
			this._oDialogCopy1.open();
			//sap.ui.getCore().byId("idMonth").setDateValue(vDate);
		},

		onCopyradio: function (oevt) {
			debugger;
			var index = oevt.getSource().getSelectedIndex();
			if (index === 2) {
				sap.ui.getCore().byId("idMonth").setVisible(true);
			} else {
				sap.ui.getCore().byId("idMonth").setVisible(false);
			}
		},

		onDialogCopySubmit: function (oEvent) {
			debugger;

			if (this.turnoverGstnFlag === "P") {
				var aID = [];
				var oTablIndxPR = this.byId("tabOutwarddet").getSelectedIndices();
				var oModelForTab = this.byId("tabOutwarddet").getModel("Gstr6DetrPRSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);
				}

				var oTaxPeriod = this.getView().byId("idTaxPeriodDetermination").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
			} else {
				var oTablIndxPR = this.byId("idTabDistrisum").getSelectedIndices();
				var oModelForTab = this.byId("idTabDistrisum").getModel("Gstr6DetrPRTrSumData").getData().resp;

				var aID = [],
					unSavedGstins = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					if (oModelForTab[oTablIndxPR[i]].id !== undefined) {
						aID.push(oModelForTab[oTablIndxPR[i]].id);
					} else {
						unSavedGstins.push(oModelForTab[oTablIndxPR[i]].gstin);
					}

				}

				var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

			}
			var oType = sap.ui.getCore().byId("rbgCopyType").getSelectedIndex();
			var vType;
			if (oType == 0) {
				vType = "A";
			} else if (oType == 1) {
				vType = "B";
			} else {
				vType = "C";
			}

			var postData = {
				"req": {
					"id": aID,
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin,
					"type": vType,
					"unSavedGstins": unSavedGstins
				}
			};

			if (vType === "C") {
				debugger;
				postData.req.fromPeriod = sap.ui.getCore().byId("idMonth").getValue();
			}

			this.getUserInputIntrigrationFinal(postData);
			this._oDialogCopy1.close();
		},

		onDialogCopySubmitCancel: function () {
			this._oDialogCopy1.close();
		},

		getUserInputIntrigrationFinal: function (postData) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr6ComputeTurnOverUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp[0].msg);
					that.getDetrminationSummaryData();
					/*if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							aMockMessages.push({
								type: 'Success',
								// title: data.resp[i].gstin,
								MSG: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
					}
					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogUserInputPress();*/
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onDialogUserInputPress: function () {
			var that = this;
			if (!this.pressUIDialog) {
				this.pressUIDialog = new Dialog({
					title: "Copy Data to User Input",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>MSG}",
								// description: "{msg>MSG}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								//infoStateInverted: "true"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressUIDialog.close();
							if (this.turnoverGstnFlag === "P") {
								that.getDetrminationProcessData();
							} else {
								that.getDetrminationSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressUIDialog);
			}

			this.pressUIDialog.open();
		},

		OnpressComputeCredit: function (oEvent, flag) {
			debugger;

			var that = this;
			this.turnoverGstnFlag = flag;
			if (flag === "P") {
				var vGetDatagstn = that.getView().byId("tabOutwarddet").getSelectedIndices();
				if (vGetDatagstn.length === 0) {
					MessageBox.information("Please select atleast one GSTIN");
					return;
				}
			}
			// var vGetDatagstn = that.getView().byId("idtableRPWise").getSelectedIndices();
			// if (vGetDatagstn.length === 0) {
			// 	MessageBox.success("Please select atleast one GSTIN");
			// 	return;

			// }
			MessageBox.alert(
				"Do you want to compute the credit distribution?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							// that.getComputeReversalBtnIntrigration();
							that.onDialogCompCreditSubmit();

							// MessageBox.success("Reversal computation is Inprogess.Please check after 15 minutes");

						}

					}
				}
			);
		},
		onDialogCompCreditSubmit: function () {
			debugger;
			// this._oDialogCopy1.close();
			if (this.turnoverGstnFlag === "P") {
				var oTablIndxPR = this.byId("tabOutwarddet").getSelectedIndices();
				var oModelForTab = this.byId("tabOutwarddet").getModel("Gstr6DetrPRSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

				}

				var oTaxPeriod = this.getView().byId("idTaxPeriodDetermination").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var oTableType = this.getView().byId("iddropdeterminationGstr6").getSelectedKeys();

			} else {
				var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var oTableType = [];
			}

			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin,
					"tableType": oTableType

				}
			};
			this.TimeStampPayload = {
				"req": {
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin
				}
			}
			this.getCompCdertIntrigrationFinal(postData);
			// this._getGstr6ComputeTimeStamp();
		},
		getCompCdertIntrigrationFinal: function (postData) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr6ComputeCreditDistribution.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.hdr.status === "S") {
					// 	// that.onPressSummaryGo();
					// 	for (var i = 0; i < data.resp.length; i++) {
					// 		MessageBox.success(data.resp[i].msg);
					// 	}
					// } else {
					// 	MessageBox.error(data.hdr.message);

					// }

					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {

							aMockMessages.push({
								type: 'Success',
								title: data.resp[i].msg,
								// MSG: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}
					}

					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogCompCredittPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr6ComputeCreditDistribution.do : Error");
				});
			});
		},

		onDialogCompCredittPress: function () {
			var that = this;
			if (!this.pressUICredDialog) {
				this.pressUICredDialog = new Dialog({
					title: "Compute Credit Distribution",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								// description: "{msg>MSG}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								//infoStateInverted: "true"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressUICredDialog.close();

							if (this.turnoverGstnFlag === "P") {
								that.getDetrminationProcessData();
							} else {
								that.getDetrminationSummaryData();
								that._getGstr6ComputeTimeStamp();

							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressUICredDialog);
			}

			this.pressUICredDialog.open();
		},
		onSelectCheckBoxDetr: function (oEvent) {
			debugger;
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("IdIgsteligDis1").setVisible(true);
				this.oView.byId("IdIgsteligDis2").setVisible(true);
				this.oView.byId("IdIgsteligDis3").setVisible(true);
				this.oView.byId("IdIgsteligDis4").setVisible(true);
				// this.oView.byId("IdIgsteligDis5").setVisible(true);
			} else {
				this.oView.byId("IdIgsteligDis1").setVisible(false);
				this.oView.byId("IdIgsteligDis2").setVisible(false);
				this.oView.byId("IdIgsteligDis3").setVisible(false);
				this.oView.byId("IdIgsteligDis4").setVisible(false);
				// this.oView.byId("IdIgsteligDis5").setVisible(false);
			}
		},
		onSelectCheckBoxDetr2: function (oEvent) {
			debugger;
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("IdIgstIneligDis1").setVisible(true);
				this.oView.byId("IdIgstIneligDis2").setVisible(true);
				this.oView.byId("IdIgstIneligDis3").setVisible(true);
				this.oView.byId("IdIgstIneligDis4").setVisible(true);
				// this.oView.byId("IdIgsteligDis5").setVisible(true);
			} else {
				this.oView.byId("IdIgstIneligDis1").setVisible(false);
				this.oView.byId("IdIgstIneligDis2").setVisible(false);
				this.oView.byId("IdIgstIneligDis3").setVisible(false);
				this.oView.byId("IdIgstIneligDis4").setVisible(false);
				// this.oView.byId("IdIgsteligDis5").setVisible(false);
			}
		},

		getDetrminationProcessData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("idTaxPeriodDetermination").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var vtableType = this.oView.byId("iddropdeterminationGstr6").getSelectedKeys();
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"returnPeriod": oTaxPeriod,
					"tableType": vtableType.includes("All") ? [] : vtableType,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idGetDeterminationGstin").getSelectedKeys()
					}
				}
			};

			// if (this.getView().byId("dAdapt")) {
			// 	this._getOtherFiltersASP(postData.req.dataSecAttrs);

			// }
			// this.TimeStampPayload = {
			// 	"req": {
			// 		"taxPeriod": oTaxPeriod,
			// 		"gstin": this.oView.byId("idGetDeterminationGstin").getSelectedKeys()
			// 	}
			// }
			this.getDetrminationPrDataFinal(postData);
			// this._getGstr6ComputeTimeStamp();
		},

		getDetrminationPrDataFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6Determination.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (!data.resp.length) {
						MessageBox.information("No Data");
						this.getView().setModel(new JSONModel(null), "Gstr6DetrPRSumData");
						return;
					}
					this.getView().setModel(new JSONModel(data), "Gstr6DetrPRSumData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr6Determination : Error");
					this.getView().setModel(new JSONModel(null), "Gstr6DetrPRSumData");
				}.bind(this));
		},

		onPressGoGstr6DetrProcess: function () {
			this.getDetrminationProcessData();
		},

		onChangeDateValue: function (oEvent) {
			debugger; //eslint-disable-line
			var vDatePicker;
			if (oEvent.getSource().getId().includes("idDterSummaryTaxFr")) {
				vDatePicker = "idDterSummaryTaxTo";
			} else if (oEvent.getSource().getId().includes("idPFromtaxPeriod2AB")) {
				vDatePicker = "idPFromtaxPeriod2AB1";

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

			// var fromDate = oEvent.getSource().getDateValue(),
			// 	toDate = this.byId(vDatePicker).getDateValue();
			// if (fromDate > toDate) {
			// 	this.byId(vDatePicker).setDateValue(fromDate);
			// }
			// this.byId(vDatePicker).setMinDate(fromDate);
		},

		onDialogCopySubmitDigi: function () {
			debugger;
			this._oDialog.close();
			if (this.turnoverDigiFlag === "P") {

				var oTablIndxPR = this.byId("tabOutwarddet").getSelectedIndices();
				var oModelForTab = this.byId("tabOutwarddet").getModel("Gstr6DetrPRSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

				}

				var oTaxPeriod = this.getView().byId("idTaxPeriodDetermination").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
			} else {
				var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

			}
			var oTaxPeriodFrm = sap.ui.getCore().byId("idDterSummaryTaxFr").getValue();
			if (oTaxPeriodFrm === "") {
				oTaxPeriodFrm = null;
			}
			var oTaxPeriodTo = sap.ui.getCore().byId("idDterSummaryTaxTo").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}

			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"taxPeriodFrom": oTaxPeriodFrm,
					"taxPeriodTo": oTaxPeriodTo,
					"gstin": aGstin,
					"tableType": []
				}

			};
			this.TimeStampPayload = {
				"req": {
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin
				}
			}
			var URL = "/aspsapapi/Gstr6CalculateTurnOverDigiGstProcessed.do";
			this.getTurnoverDigiIntrigrationFinal(postData, URL);
		},
		getTurnoverDigiIntrigrationFinal: function (postData, url) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
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
								// title: data.resp[i].gstin,
								MSG: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}
					}

					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogDigiPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr6CalculateTurnOverDigiGstProcessed.do : Error");
				});
			});
		},
		onDialogDigiPress: function () {
			var that = this;
			if (!this.pressDigiDialog) {
				this.pressDigiDialog = new Dialog({
					title: "Calculate turnover(Digigst)",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>MSG}",
								// description: "{msg>MSG}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								//infoStateInverted: "true"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDigiDialog.close();

							if (this.turnoverDigiFlag === "P") {
								that.getDetrminationProcessData();
							} else {
								that.getDetrminationSummaryData();
								that._getGstr6ComputeTimeStamp();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDigiDialog);
			}

			this.pressDigiDialog.open();
		},
		onDialogTurnSubmit1: function () {
			debugger;
			// this.turnoverGstnFlag = flag;
			this._oDialog1.close();
			if (this.turnoverGstnFlag === "P") {
				var oTablIndxPR = this.byId("tabOutwarddet").getSelectedIndices();
				var oModelForTab = this.byId("tabOutwarddet").getModel("Gstr6DetrPRSumData").getData().resp;

				var aGstin = [];
				for (var i = 0; i < oTablIndxPR.length; i++) {
					aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

				}

				var oTaxPeriod = this.getView().byId("idTaxPeriodDetermination").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
			} else {
				var aGstin = [this.getView().byId("idPGstinTrsUm").getSelectedKey()];
				var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

			}
			var oTaxPeriodFrm = sap.ui.getCore().byId("idDterSummaryTaxFr1").getValue();
			if (oTaxPeriodFrm === "") {
				oTaxPeriodFrm = null;
			}
			var oTaxPeriodTo = sap.ui.getCore().byId("idDterSummaryTaxTo1").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}

			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"taxPeriodFrom": oTaxPeriodFrm,
					"taxPeriodTo": oTaxPeriodTo,
					"gstin": aGstin,
					"tableType": []

					// "entityId": [$.sap.entityID],
					// "taxPeriodFrom": oTaxPeriodFrom,
					// "taxPeriodTo": oTaxPeriodTo,
					// "dataSecAttrs": {
					// 	"GSTIN": aGstin
					// }

				}

			};
			this.TimeStampPayload = {
				"req": {
					"taxPeriod": oTaxPeriod,
					"gstin": aGstin
				}
			}
			var URL = "/aspsapapi/Gstr6CalculateTurnOverGstnProcessed.do";
			this.getTurnoverGstnIntrigrationFinal(postData, URL);

		},
		getTurnoverGstnIntrigrationFinal: function (postData, url) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
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
								// title: data.resp[i].gstin,
								MSG: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}
					}

					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogGstnPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr6CalculateTurnOverGstnProcessed.do : Error");
				});
			});
		},
		onDialogGstnPress: function () {
			var that = this;
			if (!this.pressgstnDialog) {
				this.pressgstnDialog = new Dialog({
					title: "Calculate turnover(GSTN)",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>MSG}",
								// description: "{msg>MSG}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								//infoStateInverted: "true"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressgstnDialog.close();

							if (this.turnoverGstnFlag === "P") {
								that.getDetrminationProcessData();
							} else {
								that.getDetrminationSummaryData();
								that._getGstr6ComputeTimeStamp();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressgstnDialog);
			}

			this.pressgstnDialog.open();
		},
		getDetrminationSummaryData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("idPFromtaxPeriod2A").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var oFromTaxPeriod = this.oView.byId("idPFromtaxPeriod2AB").getValue();
			if (oFromTaxPeriod === "") {
				oFromTaxPeriod = null;
			}
			var oToTaxPeriod = this.oView.byId("idPFromtaxPeriod2AB1").getValue();
			if (oToTaxPeriod === "") {
				oToTaxPeriod = null;
			}
			var postData = {

				"req": {
					"entityId": [$.sap.entityID],
					"returnPeriod": oTaxPeriod,
					"taxPeriodFrom": oFromTaxPeriod,
					"taxPeriodTo": oToTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [this.oView.byId("idPGstinTrsUm").getSelectedKey()]
					}

					// "entityId": [$.sap.entityID],
					// "returnPeriod": oTaxPeriod,
					// "dataSecAttrs": {
					// 	"GSTIN": this.oView.byId("idGetDeterminationGstin").getSelectedKeys()
					// }

					// "entityId": [$.sap.entityID],
					// "taxPeriod": oTaxPeriod,
					// "dataSecAttrs": {
					// 	"GSTIN": this.oView.byId("idgstingstr6").getSelectedKeys(),
					// }
				}

			};
			// if (this.getView().byId("dAdapt")) {
			// 	this._getOtherFiltersASP(postData.req.dataSecAttrs);

			// }
			this.TimeStampPayload = {
				"req": {
					"taxPeriod": oTaxPeriod,
					"gstin": [this.oView.byId("idPGstinTrsUm").getSelectedKey()]
				}
			}
			this.getDetrminationPrSmDataFinal(postData);
			this._getGstr6ComputeTimeStamp();
		},

		getDetrminationPrSmDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6Turnover.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oGstr6DtrPRTrSumData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6DtrPRTrSumData, "Gstr6DetrPRTrSumData");
					}
					var oTotal = {
						"count": 0,
						"digigst": 0,
						"gstn": 0,
						"userInput": 0
					};
					for (var i = 0; i < data.resp.length; i++) {
						oTotal.count = data.resp.length;
						oTotal.digigst += data.resp[i].turnoverDigiGST;
						oTotal.gstn += data.resp[i].turnoverGstn;
						oTotal.userInput += data.resp[i].turnoverUserEdited;
						data.resp[i].Sno = i + 1;
						data.resp[i].edit = false;
						data.resp[i].edit1 = false;
						data.resp[i].turnoverUserEdited = data.resp[i].turnoverUserEdited;
					}
					var oGstr6DtrPRTrSumData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oGstr6DtrPRTrSumData, "Gstr6DetrPRTrSumData");
					var oGstr6DtrPRTrTotal = new sap.ui.model.json.JSONModel(oTotal);
					oView.setModel(oGstr6DtrPRTrTotal, "Gstr6TotalHead");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr6Turnover : Error");
					var oGstr6DtrPRTrSumData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oGstr6DtrPRTrSumData, "Gstr6DetrPRTrSumData");

				});
			});
		},
		onPressGodetrminSum: function () {
			this.buttonVis();
			this.getDetrminationSummaryData();

		},
		onMenuItemPressGstr6downPr: function (oEvent) {
			debugger;
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTablIndxPR = this.byId("tabOutwarddet").getSelectedIndices();
			var oModelForTab = this.byId("tabOutwarddet").getModel("Gstr6DetrPRSumData").getData().resp;
			var that = this;
			if (oTablIndxPR.length === 0) {
				MessageBox.information("Please select atleast one GSTIN");
				return;

			}

			var aGstin = [];
			for (var i = 0; i < oTablIndxPR.length; i++) {
				aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);

			}
			var oTaxPeriod = this.getView().byId("idTaxPeriodDetermination").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			if (oSelectedKey === "current") {
				var postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"returnPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}

				};
				// postData.req.type = "ITCREVERSAL";
				var url = "/aspsapapi/gstr6DeterminationDownloadReports.do";
				this.excelDownload(postData, url);
				return;
			} else {
				var Request = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": aGstin,
						"taxPeriod": oTaxPeriod
					}
				};

				var url1 = "/aspsapapi/getGstr6IsdAnnxFile.do";
				this.reportDownload(Request, url1);
			}

		},
		onMenuItemPressGstr6downSr: function (oEvent) {
			debugger;
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTaxPeriod = this.getView().byId("idPFromtaxPeriod2A").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			if (oSelectedKey === "current") {
				var postPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"returnPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [this.oView.byId("idPGstinTrsUm").getSelectedKey()]
						}
					}

				};

				var url = "/aspsapapi/gstr6DeterminationDownloadReports.do";
				this.excelDownload(postPayload, url);
				return;
			} else {
				var Request = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [this.oView.byId("idPGstinTrsUm").getSelectedKey()],
						"taxPeriod": oTaxPeriod
					}
				};

				var url1 = "/aspsapapi/getGstr6IsdAnnxFile.do";
				this.reportDownload(Request, url1);
			}

		},

		onPressDistEdit: function () {
			debugger;
			var tab = this.getView().byId("idTabDistrisum");
			var sItems = tab.getSelectedIndices();
			if (sItems.length === 0) {
				MessageBox.information("Please select atleast one Record");
				return;

			}

			var oJSONModel = tab.getModel("Gstr6DetrPRTrSumData");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist1.resp[sItems[i]].edit1 = true;
			}

			var oGstinDetail = new JSONModel();
			oGstinDetail.setData(itemlist1);
			this.getView().setModel(oGstinDetail, "Gstr6DetrPRTrSumData");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onPressDistAdd: function () {
			var oModel2 = this.getView().getModel("Gstr6DetrPRTrSumData");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.resp.length + 1;
			var emptyObject = {
				"Sno": vSNO,
				"edit": true,
				"edit1": true,
				"id": null,
				"gstin": "",
				"state": "",
				"getGstr1Status": "",
				"turnoverDigiGST": null,
				"turnoverGstn": null,
				"turnoverUserEdited": null
			};

			itemlist.resp.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},
		onPressDistSave: function () {
			debugger;
			var tab = this.getView().byId("idTabDistrisum");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				MessageBox.information("Please select atleast one record");
				return;
			}
			var oJSONModel = tab.getModel("Gstr6DetrPRTrSumData");
			var itemlist1 = oJSONModel.getProperty("/");
			var payload = {
				"req": []
			};
			for (var i = 0; i < sItems.length; i++) {
				payload.req.push({
					"isdGstin": this.getView().byId("idPGstinTrsUm").getSelectedKey(),
					"currentRetPer": this.getView().byId("idPFromtaxPeriod2A").getValue(),
					"id": itemlist1.resp[sItems[i]].id,
					"gstin": itemlist1.resp[sItems[i]].gstin,
					"state": itemlist1.resp[sItems[i]].state,
					"getGstr1Status": itemlist1.resp[sItems[i]].getGstr1Status,
					"turnoverGstn": itemlist1.resp[sItems[i]].turnoverGstn,
					"turnoverDigiGST": itemlist1.resp[sItems[i]].turnoverDigiGST,
					"turnoverUserEdited": itemlist1.resp[sItems[i]].turnoverUserEdited
				});

				// itemlist1.resp[sItems[i]].edit = true;
			}
			var that = this;
			MessageBox.show(
				"Do you want to save the records?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.gstr6EditAndSave(payload);
						}

					}
				}
			);

		},

		gstr6EditAndSave: function (postData) {
			debugger;
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6EditAndSave.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.resp.length === 0) {
					MessageBox.success(data.resp);
					// }
					that.getDetrminationSummaryData();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onPressDistDelete: function () {
			debugger;
			var tab = this.getView().byId("idTabDistrisum");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				MessageBox.information("Please select atleast one record");
				return;
			}
			var oJSONModel = tab.getModel("Gstr6DetrPRTrSumData");
			var itemlist1 = oJSONModel.getProperty("/");
			var payload = {
				"req": {
					"entityid": $.sap.entityID,
					"id": []
				}
			};

			var reverse = [].concat(tab.getSelectedIndices()).reverse();
			reverse.forEach(function (index) {
				if (itemlist1.resp[index].id != null) {
					// deleteData.resp.push(itemlist1[index]);
					payload.req.id.push(itemlist1.resp[index].id);
				} else {
					itemlist1.resp.splice(index, 1);
				}

			});
			oJSONModel.refresh();
			tab.setSelectedIndex(-1);
			if (payload.req.id.length > 0) {
				var that = this;
				MessageBox.show(
					"Do you want to delete the Records?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								that.gstr6TurnOverDelete(payload);
								// that.getTurnoverDigiIntrigrationFinal(postData);

							} else {
								// that.getDetrminationSummaryData();
							}

						}
					}
				);
				// this.onDeleteDBELExtract(deleteData);
				// this.gstr6TurnOverDelete(payload);
			} else {
				MessageBox.success("Successfully deleted");
			}
		},

		gstr6TurnOverDelete: function (postData) {
			debugger;
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6TurnOverDelete.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.resp.length === 0) {
					MessageBox.success(data.resp);
					that.getDetrminationSummaryData();
					// }

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onChangeDateValuedigi: function (oEvent) {
			debugger; //eslint-disable-line
			var vDatePicker;
			if (oEvent.getSource().getId().includes("idDterSummaryTaxFr")) {
				vDatePicker = "idDterSummaryTaxTo";
			} else if (oEvent.getSource().getId().includes("idDterSummaryTaxFr1")) {
				vDatePicker = "idDterSummaryTaxTo1";

			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = sap.ui.getCore().byId(vDatePicker).getDateValue();
			sap.ui.getCore().byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				sap.ui.getCore().byId(vDatePicker).setDateValue(fromDate);
			}

		},
		onChangeDateValuegstn: function (oEvent) {
			debugger; //eslint-disable-line
			var vDatePicker;
			if (oEvent.getSource().getId().includes("idDterSummaryTaxFr1")) {
				vDatePicker = "idDterSummaryTaxTo1";
			} else if (oEvent.getSource().getId().includes("idDterSummaryTaxFr1")) {
				vDatePicker = "idDterSummaryTaxTo1";

			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = sap.ui.getCore().byId(vDatePicker).getDateValue();
			sap.ui.getCore().byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				sap.ui.getCore().byId(vDatePicker).setDateValue(fromDate);
			}

		},

		onPressGetStatus: function (oEvent, flag) {
			debugger;
			var that = this;
			if (!this._oDialogStatus) {
				this._oDialogStatus = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.GetStatus", this);
				this.getView().addDependent(this._oDialogStatus);

			}
			var a = oEvent.getSource().getBindingContext("Gstr6DetrPRTrSumData").getObject();
			sap.ui.getCore().byId("idinputGstin").setValue(a.gstin);

			this._oDialogStatus.open();
			this.getGstrGetStatusIntg();
		},

		onCloseProcStatusDialog: function (oEvent) {
			this._oDialogStatus.close();
		},

		getGstrGetStatusIntg: function (oEntity) {
			debugger;

			var oTaxFromPeriod = this.getView().byId("idPFromtaxPeriod2AB").getValue();
			if (oTaxFromPeriod === "") {
				oTaxFromPeriod = null;
			}
			var oTaxToPeriod = this.getView().byId("idPFromtaxPeriod2AB1").getValue();
			if (oTaxToPeriod === "") {
				oTaxToPeriod = null;
			}

			// var a = oEvent.getSource().getBindingContext("Gstr6DetrPRTrSumData").getObject();
			var vgstin = sap.ui.getCore().byId("idinputGstin").getValue();
			var postData = {

				"req": {
					"gstin": vgstin,
					"periodFrom": oTaxFromPeriod,
					"periodTo": oTaxToPeriod
				}

			};
			this.getGstrGetStatusIntgFinal(postData);
		},

		getGstrGetStatusIntgFinal: function (postData) {
			debugger;

			var oView = this.getView();

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr6ComputeGstr1SummaryStatus.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oGstr6getStatusIntg = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6getStatusIntg, "Gstr6GetStatusIng");
					} else if (data.resp.message != undefined) {
						MessageBox.information(data.resp.message);
						var oGstr6getStatusIntg = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6getStatusIntg, "Gstr6GetStatusIng");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].sno = i + 1;
						}
						var oGstr6getStatusIntg = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oGstr6getStatusIntg, "Gstr6GetStatusIng");
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr6ComputeGstr1SummaryStatus : Error");
				});
			});
		},
		onPressRefreshBtnPopup: function () {
			this.getGstrGetStatusIntg();

		},
		onActivateAuthToken: function (gstin, authToken) {
			// debugger; //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onPressRequestIDwise: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(true);
			this.getView().byId("idGetDetrmination").setVisible(false);
			this.onSearchReqIdWise();
		},
		onPressRequestIDwiseBack: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(false);
			this.getView().byId("idGetDetrmination").setVisible(true);
		},

		onPressRequestIDwiseSummary: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(true);
			this.getView().byId("dpDetrminationSummary").setVisible(false);
			this.onSearchReqIdWise();
		},
		onPressRequestIDwiseSummaryBack: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(false);
			this.getView().byId("dpDetrminationSummary").setVisible(true);
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
			this._bindReqIdFilter();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function (oEvent) {
			var oFilterData = this.byId("id_RequestIDpage").getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"taxPeriodFrom": oFilterData.frTaxPeriod,
						"taxPeriodTo": oFilterData.toTaxPeriod,
						"initiationByUserId": this.removeAll(oFilterData.initiatedBy),
						"reconStatus": oFilterData.status
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr6CredDistReqIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "oRequestIDWise");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onConfigExtractPress2A1: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("oRequestIDWise").getObject(),
				payload = {
					"req": {
						"requestId": obj.requestId
					}
				};
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}
			this._getGstr2a1.open();
			this._getGstr2a1.setBusy(true);
			this._getGstr2a1.setModel(new JSONModel([]), "DownloadDocument");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6CredDistRptDwnldButton.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this._getGstr2a1.setModel(new JSONModel(data.resp), "DownloadDocument");
					}
					this._getGstr2a1.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._getGstr2a1.setBusy(false);
				}.bind(this));
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onFragDownload: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("DownloadDocument").getObject(),
				payload = {
					"req": {
						"DocId": obj.docId
					}
				};
			this.excelDownload(payload, "/aspsapapi/gstr6CredDistZDownload.do");
		},

		onPressGSTIN: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("oRequestIDWise").getObject();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(new JSONModel(obj.gstinsList), "gstins2A");
			this._oGstinPopover.openBy(oEvent.getSource());
		}
	});
});