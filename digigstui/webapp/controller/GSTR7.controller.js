sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Dialog",
	"sap/m/Button",
	"sap/m/MessageBox"
], function (BaseController, Formatter, JSONModel, Dialog, Button, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GSTR7", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR7
		 */
		onInit: function () {
			var that = this,
				vDate = new Date();
			this.getOwnerComponent().getRouter().getRoute("GSTR7").attachPatternMatched(this._onRouteMatched, this);

			// 			Processed Records
			this.byId("idPtaxPeriod").setMaxDate(vDate);
			this.byId("idPtaxPeriod").setDateValue(vDate);

			this.byId("idPtaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idPtaxPeriod").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idSummarytaxPeriod").setMaxDate(vDate);
			this.byId("idSummarytaxPeriod").setDateValue(vDate);

			this.byId("idSummarytaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idSummarytaxPeriod").$().find("input").attr("readonly", true);
				}
			});

			// this.byId("idSummarytaxPeriod").setMaxDate(vDate);
			// this.byId("idSummarytaxPeriod").setDateValue(vDate);
		},

		_onRouteMatched: function (key) {
			// var key = oEvent.getParameter("arguments").key;
			if (key === "DashBoard") {
				// this.onPressGstr1SummaryDashBoard();
			} else {
				this.byId("dpGstr7ProcessRecord").setVisible(true);
				this.byId("dpGstr7Summary").setVisible(false);
				this._getProcessedData();
			}
		},

		onSearch: function (oEvent) {
			if (oEvent.getSource().getId().includes("bProcessGo")) {
				this._getProcessedData();
			} else {
				this._getSummaryData();
			}

		},

		onUpdateGstnDataProcess: function () {
			var that = this;
			var oData = this.byId("tabGstr7Process").getModel("ProcessedRecord").getData(),
				aIndex = this.byId("tabGstr7Process").getSelectedIndices();
			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});

				return;
			}
			//	var vTaxPeriod;
			var aGstin = [];
			for (var i = 0; i < aIndex.length; i++) {
				aGstin.push(oData[aIndex[i]].gstin);
			}
			var oTaxPeriod = this.getView().byId("idPtaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			//	var vGstins = this.oView.byId("idgstingstrnew1").getSelectedKey();
			// if (vGstins.length === 1) {
			// 	var ogstin = vGstins[0];
			// } else {
			// 	MessageBox.information("Please select  only one GSTIN");
			// 	return;
			// }
			var postData = {

				"req": {
					"entityId": [$.sap.entityID],
					"action": "UPDATEGSTIN",
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}

				// "gstin": ogstin,
				// "rtnprd": oTaxPeriod

			};

			MessageBox.show(
				"Do you want to Update GSTN Data for GSTR-7?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.getUpdateGstnButtonIntrigration1(postData);

							// MessageBox.success("Update GSTN Data Inprogess.");

						}

					}
				}
			);

		},

		getUpdateGstnButtonIntrigration1: function (postData) {
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7EntityReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
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
							MessageBox.success("Successfully Updated");
						}

					} else {
						MessageBox.information(data.hdr.message);

					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr6ReviewSummary : Error");
				});
			});

		},

		_getProcessedData: function () {
			debugger; //eslint-disable-line

			// var data = {
			// 	"resp": [{
			// 		"gstin": "29AAAPH9357H000",
			// 		"authToken": "Inactive",
			// 		"state": "Karnataka",
			// 		"saveStatus": "NOT INITIATED",
			// 		"saveDateTime": "28/05/2020 02:53:00 PM",
			// 		"fileStatus": "NOT INITIATED",
			// 		"fileDateTime": "28/05/2020 02:53:00 PM",
			// 		"count": 0,
			// 		"totalAmount": 0,
			// 		"igst": 0,
			// 		"cgst": 0,
			// 		"sgst": 0

			// 	}]
			// };

			// this.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");

			var oTaxPeriod = this.getView().byId("idPtaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var aGstin = this.getView().byId("idPGstin").getSelectedKeys();

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin,
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": []
					}
				}
			};

			// var oPaylad = {
			// 	"req": {
			// 		"entityId": [$.sap.entityID],
			// 		"taxPeriod": oTaxPeriod,
			// 		"returnType": "GSTR7",
			// 		"dataSecAttrs": {
			// 			"GSTIN": aGstin.includes("All") ? [] : aGstin
			// 		}
			// 	}
			// };
			// this.saveToGSTINPayload = oPaylad
			// this._saveToGstnStatus();

			var that = this;

			// if (this.byId("dAdapt")) {
			// 	this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
			// }
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr7ProcessPrSummary.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();

				that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(null), "ProcessedRecord");
				MessageBox.error("Error : getGstr7ProcessPrSummary ");
			});
		},

		onPressDifference: function (oEvent) {
			// debugger; //eslint-disable-line
			if (!this._oDialogDifference) {
				this._oDialogDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.Difference", this);
				this.getView().addDependent(this._oDialogDifference);
			}
			this._oDialogDifference.open();

			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			// oPayload.req.dataSecAttrs.GSTIN.push(obj.gstin);
			this.getView().byId("ifDiffGSTIN").setText(obj.gstin);
			this._getDifferenceData();
		},

		_getDifferenceData: function () {

			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.getView().byId("idPtaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.getView().byId("ifDiffGSTIN").getText()]
					}
				}
			};

			// debugger; //eslint-disable-line
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr7ReviewDiff.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				// if (data.hdr.lastUpdatedDate) {
				// 	that.byId("tDiffUpdate").setText("Last Updated: " + data.hdr.lastUpdatedDate);
				// } else {
				// 	that.byId("tDiffUpdate").setText(null);
				// }
				that.getView().setModel(new JSONModel(data.resp), "Difference");
				// that.byId("dDifferenceGstr1").setModel(new JSONModel(data.resp), "Difference");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(null), "Difference");
				MessageBox.error("Error : getGstr7ReviewDiff ");
			});
		},

		onCloseDifferenceDialog: function () {
			this._oDialogDifference.close();
		},

		onPressGstr1Summary: function (oEvent) {
			debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpGstr7ProcessRecord").setVisible(false);
			this.byId("dpGstr7Summary").setVisible(true);

			this.byId("idSummarytaxPeriod").setValue(this.byId("idPtaxPeriod").getValue());
			this.byId("idSummaryGstin").setSelectedKey(obj.gstin);

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("idSummarytaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [obj.gstin]
					}
				}
			};
			// var searchInfo = this._summarySearchInfo(obj.gstin);
			// if (this.byId("dAdapt")) {
			// 	this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
			// }
			var oPaylad = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("idSummarytaxPeriod").getValue(),
					"returnType": "GSTR7",
					"dataSecAttrs": {
						"GSTIN": [obj.gstin]
					}
				}
			};
			this.saveToGSTINPayload = oPaylad;
			this._saveToGstnStatus();

			var oVisiSummary = {
				"asp": true,
				"gstn": true,
				"diff": true,
				"enableAsp": false,
				"enableGstn": false,
				"enableDiff": false
			};
			this.getView().setModel(new JSONModel(oVisiSummary), "visiSummAnx1");
			this._getProcessSummary(searchInfo);
		},

		_getSummaryData: function (oPayload) {
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("idSummarytaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			var oPaylad = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("idSummarytaxPeriod").getValue(),
					"returnType": "GSTR7",
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			this.saveToGSTINPayload = oPaylad;
			this._saveToGstnStatus();
			this._getProcessSummary(searchInfo);
		},

		_getProcessSummary: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr7ReviewSummary.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.bindSummaryHeader(data);
				that.getView().setModel(new JSONModel(data.resp), "ProcessSummary");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(null), "ProcessSummary");
				MessageBox.error("Error : getGstr7ReviewSummary ");
			});
		},

		bindSummaryHeader: function (odata) {
			var oHeaderData = {
				"ASP": {
					"count": 0,
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0
				},
				"GSTN": {
					"count": 0,
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0
				},
				"DIFF": {
					"count": 0,
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0
				}
			};
			for (var i = 0; i < odata.resp.response.length; i++) {
				oHeaderData.ASP.count += odata.resp.response[i].aspCount;
				oHeaderData.ASP.totalTax += odata.resp.response[i].aspTotalAmount;
				oHeaderData.ASP.igst += odata.resp.response[i].aspIgst;
				oHeaderData.ASP.cgst += odata.resp.response[i].aspCgst;
				oHeaderData.ASP.sgst += odata.resp.response[i].aspSgst;

				oHeaderData.GSTN.count += odata.resp.response[i].gstnCount;
				oHeaderData.GSTN.totalTax += odata.resp.response[i].gstnTotalAmount;
				oHeaderData.GSTN.igst += odata.resp.response[i].gstnIgst;
				oHeaderData.GSTN.cgst += odata.resp.response[i].gstnCgst;
				oHeaderData.GSTN.sgst += odata.resp.response[i].gstnSgst;

				oHeaderData.DIFF.count += odata.resp.response[i].diffCount;
				oHeaderData.DIFF.totalTax += odata.resp.response[i].diffTotalAmount;
				oHeaderData.DIFF.igst += odata.resp.response[i].diffIgst;
				oHeaderData.DIFF.cgst += odata.resp.response[i].diffCgst;
				oHeaderData.DIFF.sgst += odata.resp.response[i].diffSgst;
			}
			this.getView().setModel(new JSONModel(oHeaderData), "HeaderSummary");

		},

		onPressBack: function () {
			this.byId("dpGstr7ProcessRecord").setVisible(true);
			this.byId("dpGstr7Summary").setVisible(false);
		},

		onPressEdit: function (oEvent, oFlag) {

			if (oFlag == "Table-3") {
				if (!this._Original) {
					this._Original = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.Original", this);
					this.getView().addDependent(this._Original);
				}

				this._Original.open();
			} else {
				if (!this._Amendment) {
					this._Amendment = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.Amendment", this);
					this.getView().addDependent(this._Amendment);
				}

				this._Amendment.open();
			}
		},

		onPressOriginalClose: function () {
			this._Original.close();
		},

		onPressAmendmentClose: function () {
			this._Amendment.close();
		},

		onPressSaveGstn: function (oEvent) {
			// debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"retPeriod": null,
					"gstins": []
				}
			};
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": null,
					"returnType": "GSTR7",
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

			if (oEvent.getSource().getId().includes("bSaveToGstn")) {
				var vIndices = this.byId("tabGstr7Process").getSelectedIndices(),
					aData = this.byId("tabGstr7Process").getModel("ProcessedRecord").getData(),
					vPeriod = this.byId("idPtaxPeriod").getValue();
				if (vIndices.length === 0) {
					sap.m.MessageBox.information("Please select atleast one GSTIN", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.retPeriod = vPeriod;
				searchInfo.req.taxPeriod = vPeriod;
				for (var i = 0; i < vIndices.length; i++) {
					oPayload.req.gstins.push(aData[vIndices[i]].gstin);
					searchInfo.req.dataSecAttrs.GSTIN.push(aData[vIndices[i]].gstin);
				}
			} else {
				var aGstin = this.byId("idSummaryGstin").getSelectedKey();
				oPayload.req.gstins.push(aGstin);
				oPayload.req.retPeriod = this.byId("idSummarytaxPeriod").getValue();
				searchInfo.req.taxPeriod = this.byId("idSummarytaxPeriod").getValue();
				searchInfo.req.dataSecAttrs.GSTIN.push(aGstin);
			}
			this._saveConfirmMsg(oPayload);
			this.saveToGSTINPayload = searchInfo;
			// this._saveToGstnStatus();
		},

		_saveConfirmMsg: function (oPayload) {
			debugger;
			var oBundle = this.getResourceBundle(),
				that = this;
			if (oPayload.req.gstins.length == 1) {
				var msg = 'Do you want to save data for selected GSTIN?';
			} else {
				var msg = 'Do you want to save data for selected GSTINs?';
			}
			sap.m.MessageBox.confirm(msg, {
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						that._saveToGstn(oPayload);
					}
				}
			});
		},

		_saveToGstn: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr7SaveToGstnJob.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					// sap.m.MessageBox.success("Save initiated for selected(active) GSTINs. Please review after 15 minutes.", {
					// 	styleClass: "sapUiSizeCompact"
					// });
					that._msgSaveToGstn(data);
					that._saveToGstnStatus();
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

		onPressDownloadReport: function (oEvent, parameter, view, view1) {
			// debugger; //eslint-disable-line
			var key = oEvent.getParameter("item").getKey();
			var oBundle = this.getResourceBundle(),

				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": null,
						"type": null,
						"dataSecAttrs": {
							"GSTIN": [],
							"SO": [],
							"D": [],
							"PLANT": [],
							"UD1": [],
							"UD2": [],
							"UD3": [],
							"UD4": [],
							"UD5": [],
							"UD6": []
						}
					}

				};

			if (key === "gstr7Entity") {
				var oData = this.byId("tabGstr7Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr7Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				var GSTIN = [];
				//oPayload.req.type = oEvent.getParameter("item").getKey();
				//oPayload.req.taxPeriod = this.byId("idPtaxPeriod").getValue();
				for (var i = 0; i < aIndex.length; i++) {
					GSTIN.push(oData[aIndex[i]].gstin);
				}
				var req = {
					"req": {
						"gstins": GSTIN,
						"entityId": $.sap.entityID,
						"taxPeriod": this.byId("idPtaxPeriod").getValue()
					}
				};
				var vUrl = "/aspsapapi/gstr7EntityLevelReportDownload.do";
				this.excelDownload(req, vUrl);
				return;
			}

			if (view === "P") {
				var oData = this.byId("tabGstr7Process").getModel("ProcessedRecord").getData(),
					aIndex = this.byId("tabGstr7Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.type = oEvent.getParameter("item").getKey();
				oPayload.req.taxPeriod = this.byId("idPtaxPeriod").getValue();
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndex[i]].gstin);
				}
			}
			/*else if (view1 === "E") {
					var oData1 = this.byId("tabGstr7Process").getModel("ProcessedRecord").getData(),
					aIndex1 = this.byId("tabGstr7Process").getSelectedIndices();
				if (aIndex.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.type = oEvent.getParameter("item").getKey();
				oPayload.req.taxPeriod = this.byId("idPtaxPeriod").getValue();
				for (var j = 0; j < aIndex.length; j++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData1[aIndex1[j]].gstin);
				}
				var vAutoCalUrl = "/gstr7EntityLevelReportDownload.do";
				this.excelDownload1(oPayload, vAutoCalUrl);
			}*/
			else {
				oPayload.req.type = oEvent.getParameter("item").getKey();
				oPayload.req.taxPeriod = this.byId("idSummarytaxPeriod").getValue();
				oPayload.req.dataSecAttrs.GSTIN.push(this.byId("idSummaryGstin").getSelectedKey());
			}

			var vUrl = "/aspsapapi/downloadGstr7RSReports.do";
			this.excelDownload(oPayload, vUrl);

			// if (parameter.getKey() === "asUploaded") {
			// 	var vUrl = "/aspsapapi/downloadProcessedCsvReports.do";
			// 	oPayload.req.reportCateg = (view === "P" ? "ProcessedSummary" : "ReviewSummary"); // Report category based on Screen
			// 	this.reportDownload(oPayload, vUrl);
			// } else if (parameter.getKey() === "gstnError") {
			// 	vUrl = "/aspsapapi/consolidatedGstnErrorReports.do";
			// 	this.excelDownload(oPayload, vUrl);
			// } else {
			// 	vUrl = "/aspsapapi/downloadGstr1RSReports.do";
			// 	this.excelDownload(oPayload, vUrl);
			// }
		},

		onDownloadConsGstnErrors: function (oEvent, errType, refId) {
			debugger; //eslint-disable-line
			if (errType === "aspError") {
				var vUrl = "/aspsapapi/consoidateGstnErrorReports.do",
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
							"taxPeriod": this.byId("dtSaveStats").getValue(),
							"gstnRefId": refId
						}
					};
			} else if (errType === "gstr7Refid") {
				vUrl = "/aspsapapi/downloadGstr7RSReports.do";
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dtSaveStats").getValue(),
						"type": errType,
						"gstnRefId": refId,
						"dataSecAttrs": {
							"GSTIN": [this.byId("slGstinSaveStats").getSelectedKey()]
						}
					}

				};
			} else {
				vUrl = "/aspsapapi/downloadGstr7RSReports.do";
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dtSaveStats").getValue(),
						"type": errType,
						"gstnRefId": refId,
						"dataSecAttrs": {
							"GSTIN": [this.byId("slGstinSaveStats").getSelectedKey()]
						}
					}

				};
			}
			this.excelDownload(oPayload, vUrl);
		},

		onDownloadSummary: function () {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("dtSaveStats").getValue(),
					"type": "gstr7GstnError",
					"dataSecAttrs": {
						"GSTIN": [this.byId("slGstinSaveStats").getSelectedKey()],
						"SO": [],
						"D": [],
						"PLANT": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}

			};

			var vUrl = "/aspsapapi/downloadGstr7RSReports.do";
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
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStats);

				this.byId("dtSaveStats").setMaxDate(new Date());
				this.byId("dtSaveStats").setDateValue(new Date());

				this.byId("dtSaveStats").addEventDelegate({
					onAfterRendering: function () {
						that.byId("dtSaveStats").$().find("input").attr("readonly", true);
					}
				});
			}
			if (view === "P") {
				var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
					vPeriod = this.byId("idPtaxPeriod").getValue(),
					vGstin = obj.gstin;
			} else {
				vGstin = this.byId("idSummaryGstin").getSelectedKey(); //s()[0];
				vPeriod = this.byId("idSummarytaxPeriod").getValue();
			}
			oPayload.req.taxPeriod = vPeriod;
			oPayload.req.dataSecAttrs.GSTIN.push(vGstin);

			this.byId("slGstinSaveStats").setSelectedKey(vGstin);
			this.byId("dtSaveStats").setValue(vPeriod);
			this._oDialogSaveStats.open();
			this._getSaveStatus(oPayload);
		},

		onCloseSaveStatus: function () {
			this._oDialogSaveStats.close();
		},

		onPressStatusGo: function () {
			// debugger; //eslint-disable-line
			var aGstin = this.byId("slGstinSaveStats").getSelectedKey(),
				oModel = this.byId("dSaveStatus").getModel("SaveStatus");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			if (aGstin.length === 0) {
				sap.m.MessageToast.show("Please select atleast one GSTIN");
				return;
			}
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("dtSaveStats").getValue(),
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
				url: "/aspsapapi/gstr7SummarySaveStatus.do",
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

		onUpdateGstnData: function (oEvent, view) {
			// debugger; //eslint-disable-line
			var that = this,
				oBundle = that.getResourceBundle(),
				oPayload = {
					"req": {
						"action": "UPDATEGSTIN",
						"entityId": [$.sap.entityID],
						"taxPeriod": null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (view === "D") {
				oPayload.req.taxPeriod = this.byId("idPtaxPeriod").getValue();
				oPayload.req.dataSecAttrs.GSTIN = [this.byId("ifDiffGSTIN").getText()];
			} else if (view === "S") {
				oPayload.req.taxPeriod = this.byId("idSummarytaxPeriod").getValue();
				oPayload.req.dataSecAttrs.GSTIN = [this.byId("idSummaryGstin").getSelectedKey()];
				// if (this.byId("dAdapt")) {
				// 	this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr1Summary");
				// }
			}
			sap.m.MessageBox.confirm("Do you want to Update GSTN Data for GSTR-7?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/getGstr7ReviewSummary.do", // Gstr1GstnGetSection.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (data.hdr.status === "S") {
								if (!!that._oDialogDifference && that._oDialogDifference.isOpen()) {
									that.byId("tDiffUpdate").setText(data.resp.lastUpdatedDate ? ("Last Updated: " + data.resp.lastUpdatedDate) : null);
									that._getDifferenceData();
								} else {
									that._getSummaryData();
									// that.bindSummaryHeader(data);
									// that.getView().setModel(new JSONModel(data.resp), "ProcessSummary");
									// that.byId("tSummUpdate").setText(data.resp.lastUpdatedDate ? ("Last Updated: " + data.resp.lastUpdatedDate) : null);
								}
								sap.m.MessageBox.success(oBundle.getText("msgUpdateData"), {
									styleClass: "sapUiSizeCompact"
								});
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

		onPressGstr7GetDetails: function (view) {
			// debugger; //eslint-disable-line
			var that = this,
				oBundle = this.getResourceBundle();
			if (view === "P") {
				if (!this._getGstr7Detail) {
					this._getGstr7Detail = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.GetGstr1Details", this);
					this.getView().addDependent(this._getGstr7Detail);
				}
				this.byId("dGetDetails").setModel(new JSONModel(this._getGstr7DetailCheckStats(true)), "CheckStats");
				this._getGstr7Detail.open();
			} else {
				sap.m.MessageBox.information(oBundle.getText("confirmGstr1Get"), {
					styleClass: "sapUiSizeCompact",
					onClose: function (oAction) {
						if (oAction === sap.m.MessageBox.Action.OK) {
							var oPayload = {
								"req": [{
									"gstin": that.byId("idSummaryGstin").getSelectedKey(),
									"ret_period": that.byId("idSummarytaxPeriod").getValue(),
									"gstr7Sections": ["tds"]

								}]
							};
							sap.ui.core.BusyIndicator.show(0);
							$.ajax({
								method: "POST",
								url: "/aspsapapi/Gstr7GstnGetSection.do",
								contentType: "application/json",
								data: JSON.stringify(oPayload)
							}).done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									sap.m.MessageBox.success(oBundle.getText("msgGetGstnData"), {
										styleClass: "sapUiSizeCompact"
									});
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
			}
		},

		onPressClear: function (oEvent) {
			var vDate = new Date();
			if (oEvent.getSource().getId().includes("bProcessClear")) {
				this.byId("idPGstin").setSelectedKeys([]);
				this.byId("idPtaxPeriod").setDateValue(vDate);
				this._getProcessedData();
			} else {
				this.byId("idSummaryGstin").setSelectedKeys([]);
				this.byId("idSummarytaxPeriod").setDateValue(vDate);
				this._getSummaryData();
			}

		},

		onPressGstr7TableTypeLink: function (tableType) {
			debugger;
			if (tableType == "Table-3") {
				this._getTable3();
			} else if (tableType == "Table-4") {
				this._getTable4();
			}
			// this.getView().setModel(new JSONModel(data), "TablePopup");
			// this._oDialogTable.open();
		},
		_getTable3: function () {
			if (!this._oDialogTable3) {
				this._oDialogTable3 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.Table3PopUp", this);
				this.getView().addDependent(this._oDialogTable3);
			}
			this.byId("idTable3taxPeriod").setValue(this.byId("idSummarytaxPeriod").getValue())
			this.byId("idTable3Gstin").setSelectedKey(this.byId("idSummaryGstin").getSelectedKey())
			this.getGstr7PopupSummary("Table-3")
			this._oDialogTable3.open();

		},

		onPressTable3Close: function () {
			this._oDialogTable3.close();
		},

		_getTable4: function () {
			if (!this._oDialogTable4) {
				this._oDialogTable4 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7.Table4PopUp", this);
				this.getView().addDependent(this._oDialogTable4);
			}
			this.byId("idTable4taxPeriod").setValue(this.byId("idSummarytaxPeriod").getValue())
			this.byId("idTable4Gstin").setSelectedKey(this.byId("idSummaryGstin").getSelectedKey())
			this.getGstr7PopupSummary("Table-4")
			this._oDialogTable4.open();

		},
		onPressTable4Close: function () {
			this._oDialogTable4.close();
		},

		getGstr7PopupSummary: function (table) {
			// debugger; //eslint-disable-line
			var that = this;
			if (table == "Table-3") {
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": table,
						"taxPeriod": this.byId("idTable3taxPeriod").getValue(),
						"dataSecAttrs": {
							"GSTIN": [this.byId("idTable3Gstin").getSelectedKey()]
						}
					}
				};
			} else {

				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": table,
						"taxPeriod": this.byId("idTable4taxPeriod").getValue(),
						"dataSecAttrs": {
							"GSTIN": [this.byId("idTable4Gstin").getSelectedKey()]
						}
					}
				};

			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr7PopupSummary.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel(data), "PopupSummary");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_saveToGstnStatus: function () {
			var searchInfo = this.saveToGSTINPayload;
			var that = this;
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
						// that.byId("tProSave").setText(data.resp.updatedDate);
						that.byId("tSummSave").setText(data.resp.updatedDate);
					} else {
						// that.byId("tProSave").setText(null);
						that.byId("tSummSave").setText(null);
					}
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onExportExcel: function (view) {
			if (view == "P") {

				var vGstin = this.byId("idPGstin").getSelectedKeys(); //s()[0];
				var vPeriod = this.byId("idPtaxPeriod").getValue();
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": vPeriod,
						"type": "gstr7ProcessScreen",
						"dataSecAttrs": {
							"GSTIN": vGstin.includes("All") ? [] : vGstin
						}
					}
				};
				var vUrl = "/aspsapapi/downloadGstr7RSReports.do";
				this.excelDownload(oPayload, vUrl);

			} else {
				var vGstin = this.byId("idSummaryGstin").getSelectedKey(); //s()[0];
				var vPeriod = this.byId("idSummarytaxPeriod").getValue();
				var oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": vPeriod,
							"dataSecAttrs": {
								"GSTIN": [vGstin]
							}
						}
					}
					// var vUrl = "/aspsapapi/gstr7PdfReport.do";
					// this.excelDownload(oPayload, vUrl);

			}
		},

		onDownloadEntityPDF: function (flag) {
			var aPath = this.getView().byId("tabGstr7Process").getSelectedIndices(),
				oModelData = this.getView().getModel("ProcessedRecord").getProperty("/");

			if (aPath.length == 0) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			}
			var aGSTIN = aPath.map(function (e) {
					return oModelData[e].gstin;
				}),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("idPtaxPeriod").getValue(),
						"isDigigst": flag,
						"dataSecAttrs": {
							"GSTIN": aGSTIN
						}
					}
				};
			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed therefrom", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						payload.req.isVerified = sAction;
						this.reportDownload(payload, "/aspsapapi/gstr7PdfReportAsync.do");
					}.bind(this)
				});
		},

		onPressDownloadPdf: function (flag) {
			var vGstin = this.byId("idSummaryGstin").getSelectedKey(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("idSummarytaxPeriod").getValue(),
						"isDigigst": flag,
						"dataSecAttrs": {
							"GSTIN": [vGstin]
						}
					}
				}
			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed therefrom", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.req.isVerified = sAction;
						this.pdfDownload(oPayload, "/aspsapapi/gstr7PdfReport.do");
					}.bind(this)
				});
		},

		onActivateAuthToken: function (gstin, authToken) {
			// debugger; //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.GSTR7
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTR7
		 */
		onAfterRendering: function () {
			this.byId("dpGstr7ProcessRecord").setVisible(true);
			this.byId("dpGstr7Summary").setVisible(false);
			// this._getProcessedData();
			var Key = this.getView().getModel("ReturnKey").getProperty("/key");
			this._onRouteMatched(Key);
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GSTR7
		 */
		//	onExit: function() {
		//
		//	}

	});

});