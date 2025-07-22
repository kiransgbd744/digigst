sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/core/util/Export",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, Controller, formatter, JSONModel, MessageBox, MessageToast, Export, ExportTypeCSV, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Gstr2A3B", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Gstr2A3B
		 */
		onInit: function () {
			this._bindDefaultFilter();
		},

		onAfterRendering: function () {
			this._bindDefaultFilter();
			this.byId("dpGstr2Avs3BProcessRecord").setVisible(true);
			this.byId("dpGstr2vs3BSummary").setVisible(false);
			this.byId("idRequestIDwisePage2A3B").setVisible(false);
			this.getProcessData();
			this._bindReqIdFilter();
		},

		_bindDefaultFilter: function () {
			var today = new Date(),
				vDate = new Date(today.getFullYear(), today.getMonth(), 1);

			this._setDateProperty("idPFromtaxPeriod2A", vDate, today, null);
			this._setDateProperty("idPTotaxPeriod2A", today, today, vDate);

			this._setDateProperty("frReconTaxPriod", null, today, null);
			this._setDateProperty("toReconTaxPriod", null, today, vDate);
			this.byId("idPGstin2A").setSelectedKeys();
		},

		_bindReqIdFilter: function () {
			var date = new Date(),
				vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("idRequestIDwisePage2A3B").setModel(new JSONModel({
				"frTaxPeriod": ('' + (vDate.getMonth() + 1)).padStart(2, '0') + vDate.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"requestId": [],
				"initiationByUserEmailId": [],
				"status": ""
			}), "FilterModel");
		},

		getProcessData: function (oEntity) {
			var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod2A").getValue(),
				oTaxPeriodTo = this.getView().byId("idPTotaxPeriod2A").getValue(),
				aGstin = this.getView().byId("idPGstin2A").getSelectedKeys(),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oTaxPeriodFrom,
						"taxPeriodTo": oTaxPeriodTo,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			this.getProcessDataFinal(postData);
		},

		getProcessDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2aVsGstr3bProcessSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oProcessData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oProcessData, "ProessData");
					}
					var oProcessData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oProcessData, "ProessData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr2aVsGstr3bProcessSummary : Error");
					var oProcessData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oProcessData, "ProessData");

				});
			});
		},

		onSearchGSTR2vs3B: function () {
			this.getProcessData();
		},

		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("idPFromtaxPeriod2A")) {
				var vDatePicker = "idPTotaxPeriod2A";
			} else {
				vDatePicker = "idSTotaxPeriod3B";
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

		onPressGstr2ASummary: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProessData").getObject(),
				frDate = this.byId("idPFromtaxPeriod2A").getDateValue(),
				toDate = this.byId("idPTotaxPeriod2A").getDateValue(),
				today = new Date();

			this.byId("dpGstr2Avs3BProcessRecord").setVisible(false);
			this.byId("dpGstr2vs3BSummary").setVisible(true);

			this._setDateProperty("idSFromtaxPeriod3B", frDate, today);
			this._setDateProperty("idSTotaxPeriod3B", toDate, today, frDate);

			this.byId("idSummaryGstin3B").setSelectedKey(obj.gstin);
			this.getSummaryData();
		},

		onPressBack1: function () {
			this.byId("dpGstr2Avs3BProcessRecord").setVisible(true);
			this.byId("dpGstr2vs3BSummary").setVisible(false);
		},

		onButtonGeneratePress: function () {
			var that = this;
			var vGetDatagstn = that.getView().byId("tabGstr1vs3Process2A").getSelectedIndices();
			if (vGetDatagstn.length === 0) {
				MessageBox.success("Please select atleast one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Generate?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.getGenerateBtnIntrigration();
					}
				}
			});
		},

		getGenerateBtnIntrigration: function () {
			var oTablIndxPR = this.byId("tabGstr1vs3Process2A").getSelectedIndices();
			var oModelForTab = this.byId("tabGstr1vs3Process2A").getModel("ProessData").getData().resp;

			var aGstin = [];
			for (var i = 0; i < oTablIndxPR.length; i++) {
				aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);
			}
			var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod2A").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			var oTaxPeriodTo = this.getView().byId("idPTotaxPeriod2A").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": oTaxPeriodFrom,
					"taxPeriodTo": oTaxPeriodTo,
					"reconType": "GSTR2AVS3BVS3B",
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			this.getGenerateBtnIntrigrationFinal(postData);
		},

		getGenerateBtnIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2avs3bproceCall.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						sap.m.MessageBox.success(data.resp, {
							styleClass: "sapUiSizeCompact"

						});
						// that.onPressCreditRevPrGo();
						that.onSearchGSTR2vs3B();
						// that.onPressCreditRevGo();
					} else {
						MessageBox.error(data.hdr.message);

					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr2avs3bproceCall : Error");
				});
			});
		},

		getSummaryData: function (oEntity) {
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod3B").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod3B").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin3B").getSelectedKey()]
					}
				}
			};
			this.getSummaryDataFinal(postData);
		},

		getSummaryDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2aVsGstr3bReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oSummaryData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oSummaryData, "SummaryData");
					}
					var oSummaryData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oSummaryData, "SummaryData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr2aVsGstr3bReviewSummary : Error");
					var oSummaryData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oSummaryData, "SummaryData");

				});
			});
		},

		onSearchGSTR2vs3BGo: function () {
			this.getSummaryData();
		},

		onDownloadProcessBtnPress: function (oEvent) {
			var oTablIndxPR = this.byId("tabGstr1vs3Process2A").getSelectedIndices();
			var oModelForTab = this.byId("tabGstr1vs3Process2A").getModel("ProessData").getData().resp;
			var that = this;
			if (oTablIndxPR.length === 0) {
				MessageBox.success("Please select atleast one GSTIN");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < oTablIndxPR.length; i++) {
				aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);
			}
			var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod2A").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			var oTaxPeriodTo = this.getView().byId("idPTotaxPeriod2A").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"type": "P",
					"fromtaxPeriod": oTaxPeriodFrom,
					"totaxPeriod": oTaxPeriodTo,
					"gstin": aGstin
				}
			};
			this.excelDownload(postData, "/aspsapapi/gstr2Avs3bReviewSummaryDownload.do");
			return;
		},

		onDownloadSmmarypress: function (oEvent) {
			var oTaxPeriodFrom = this.getView().byId("idSFromtaxPeriod3B").getValue();
			if (oTaxPeriodFrom === "") {
				oTaxPeriodFrom = null;
			}
			var oTaxPeriodTo = this.getView().byId("idSTotaxPeriod3B").getValue();
			if (oTaxPeriodTo === "") {
				oTaxPeriodTo = null;
			}
			var postPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"type": "R",
					"fromtaxPeriod": oTaxPeriodFrom,
					"totaxPeriod": oTaxPeriodTo,
					"gstin": [this.oView.byId("idSummaryGstin3B").getSelectedKey()]

				}
			};
			this.excelDownload(postPayload, "/aspsapapi/gstr2Avs3bReviewSummaryDownload.do");
			return;
		},

		onPressGSTR3BGetDetails: function (view) {
			if (view === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1vs3Process2A").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1vs3Process2A").getModel("ProessData").getData().resp;
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oSelectedItem.length; i++) {
					postData.req.push({
						"taxPeriod": "",
						"gstin": oModelForTab1[oSelectedItem[i]].gstin,
						"fromPeriod": this.byId("idPFromtaxPeriod2A").getValue(),
						"toPeriod": this.byId("idPTotaxPeriod2A").getValue()
					});
				}
				this.vPSFlag = "P";
			}
			this.getGstr3BGstnGetSection(postData);
		},

		getGstr3BGstnGetSection: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGSTR3BSummaryFromGSTN.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
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
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("getGSTR3BSummaryFromGSTN.do : Error");
					});
			});
		},
		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get GSTR-3B Status",
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
							if (this.vPSFlag === "P") {
								that.getProcessData();
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

		onExportExcel: function (button) {
			if (button === "P") {
				var vType = "gstr2avs3bPrSummary";
				var vFTaxPeriod = this.oView.byId("idPFromtaxPeriod2A").getValue();
				var vTTaxPeriod = this.oView.byId("idPTotaxPeriod2A").getValue();
				// var vAdaptFilter = "dpProcessRecord";
				var aGstin = this.byId("idPGstin2A").getSelectedKeys();

			} else {
				var vType = "gstr2avs3bReviewSummary";
				var vFTaxPeriod = this.oView.byId("idSFromtaxPeriod3B").getValue();
				var vTTaxPeriod = this.oView.byId("idSTotaxPeriod3B").getValue();
				// var vAdaptFilter = "dpGstr1Summary";
				var aGstin = [this.oView.byId("idSummaryGstin3B").getSelectedKey()];
			}
			var oPayload = {
				"req": {
					"type": vType,
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": vFTaxPeriod,
					"taxPeriodTo": vTTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			this.excelDownload(oPayload, "/aspsapapi/gstr2avs3bScreenDownloads.do");
		},

		onPressGstr2AGetDetails: function (view) {
			if (view === "P") {
				var oSelectedItem = this.getView().byId("tabGstr1vs3Process2A").getSelectedIndices();
				var oModelForTab1 = this.byId("tabGstr1vs3Process2A").getModel("ProessData").getData().resp;
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oSelectedItem.length; i++) {
					postData.req.push({
						"taxPeriod": "",
						"gstin": oModelForTab1[oSelectedItem[i]].gstin,
						"fromPeriod": this.byId("idPFromtaxPeriod2A").getValue(),
						"toPeriod": this.byId("idPTotaxPeriod2A").getValue()
					});
				}
				this.vPSFlag = "P";
			}
			this.getGstr2BGstnGetSection(postData);
		},

		getGstr2BGstnGetSection: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr2aGstnGetSection.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
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
						that.onDialogPress1();
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr2aGstnGetSection.do : Error");
					});
			});
		},

		onDialogPress1: function () {
			var that = this;
			if (!this.pressDialog1) {
				this.pressDialog1 = new Dialog({
					title: "Get GSTR-2A Status",
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
							this.pressDialog1.close();
							if (this.vPSFlag === "P") {
								that.getProcessData();
							} else {
								that._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog1);
			}
			this.pressDialog1.open();
		},

		/**
		 * Method called to open/close Full Screen for GSTR-2A vs GSTR-3B
		 * Developed by: Bharat Gupta - 08.12.2020
		 * @memberOf com.ey.digigst.view.Gstr2A3B
		 * @param {string} type Full Screen Action
		 */
		on2aVs3bFullScreen: function (type) {
			var oContainer = this.byId("cc2aVs3bSummary"),
				oTable = this.byId("tabOutward3B"),
				flag = (type === "O");

			oContainer.setFullScreen(flag);
			oTable.setVisibleRowCount(flag ? 14 : 7);
			this.byId("b2aVs3bOpen").setVisible(!flag);
			this.byId("b2aVs3bClose").setVisible(flag);
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"reconType": "GSTR2AVS3B"
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getinitiatedByIds.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.requestDetails.unshift({
							UserId: "All"
						});
						this.byId("idRequestIDwisePage2A3B").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onUserEmails: function () {
			var payload = {
				"entityId": $.sap.entityID,
				"reconType": "GSTR2AVS3B"
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getinitiatedByUserEmailIds.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				data.resp.requestDetails.unshift({
					emailId: "All"
				});
				that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2EmailIds");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onRequestId: function () {
			var oFilterData = this.byId("idRequestIDwisePage2A3B").getModel("FilterModel").getProperty("/");
			var payload = {
				"entityId": $.sap.entityID,
				"initiationDateFrom": oFilterData.frTaxPeriod,
				"initiationDateTo": oFilterData.toTaxPeriod,
				"reconType": "GSTR2AVS3B"
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGSTR2AVS3B2bVS3bRequestIds.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				data.resp.requestDetails.unshift({
					requestId: "All"
				});
				var oModel = new JSONModel(data.resp.requestDetails);
				oModel.setSizeLimit(data.resp.requestDetails.length);
				that.getView().setModel(oModel, "getgstr2RequestIds");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onChangeDtReconReqIdF: function (oEvent) {
			var frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId("toReconTaxPriod").getDateValue();

			if (frDate > toDate) {
				this.byId("toReconTaxPriod").setDateValue(frDate);
			}
			this.byId("toReconTaxPriod").setMinDate(frDate);
			this.onRequestId();
		},

		onChangeDtReconReqIdT: function (oEvent) {
			this.onRequestId();
		},

		onPressClearRequestIDwise: function () {
			this._bindReqIdFilter();
			this.onPressRequestIDwise2A3B();
		},

		onPressRequestIDwise2A3B: function () {
			this.byId("dpGstr2Avs3BProcessRecord").setVisible(false);
			this.byId("idRequestIDwisePage2A3B").setVisible(true);
			this._getUserId();
			this.onRequestId();
			this.onUserEmails();
			this.onPressRequestIDwise2A3Bfinal()
		},

		onPressRequestIDwise2A3Bfinal: function () {
			var oFilterData = this.byId("idRequestIDwisePage2A3B").getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"taxPeriodFrom": oFilterData.frTaxPeriod,
						"taxPeriodTo": oFilterData.toTaxPeriod,
						"initiationByUserId": this.removeAll(oFilterData.initiatedBy),
						"requestId": this.removeAll(oFilterData.requestId),
						"initiationByUserEmailId": this.removeAll(oFilterData.initiationByUserEmailId),
						"reconStatus": oFilterData.status
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "ReqWiseData2A");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/GSTR2AVS3B2Bvs3bReqIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.resp !== "No Data") {
						this.getView().setModel(new JSONModel(data), "ReqWiseData2A");
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressRequestIDwiseBack2A3B: function () {
			this.byId("dpGstr2Avs3BProcessRecord").setVisible(true);
			this.byId("idRequestIDwisePage2A3B").setVisible(false);
		},

		onConfigExtractPress2A: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("ReqWiseData2A").getObject(),
				request = {
					"req": {
						"requestId": obj.requestId,
						"reconType": "GSTR2AVS3B"
					}
				};
			this.excelDownload(request, "/aspsapapi/gstr2A2Bvs3BRptDwnldButton.do");
		},

		onPressGSTIN: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReqWiseData2A").getObject(),
				reqId = obj.requestId;

			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(new JSONModel(obj.gstins), "gstins2A");
			this._oGstinPopover.openBy(oEvent.getSource());
		}
	});
});