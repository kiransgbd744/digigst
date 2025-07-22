sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/Dialog"
], function (Controller, JSONModel, MessageBox, Dialog) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.Gstr2bVs3b", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Gstr2bVs3b
		 */
		onInit: function () {
			this.byId("dp2b3bFr").addEventDelegate({
				onAfterRendering: this.setReadonly.bind(this)
			});
			this.byId("dp2b3bTo").addEventDelegate({
				onAfterRendering: this.setReadonly.bind(this)
			});
			this.byId("dp2b3bSummFr").addEventDelegate({
				onAfterRendering: this.setReadonly.bind(this)
			});
			this.byId("dp2b3bSummTo").addEventDelegate({
				onAfterRendering: this.setReadonly.bind(this)
			});
			this.setModel(new JSONModel({
				visiSumm: false
			}), "Property");
			this._initialiseData();
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Gstr2bVs3b
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Gstr2bVs3b
		 */
		onAfterRendering: function () {
			if (this.entityId !== $.sap.entityID) {
				this.entityId = $.sap.entityID;
				this._initialiseData();
				this.onSearch2bVs3b();
			}
		},

		setReadonly: function (oEvent) {
			oEvent.srcControl.$().find("input").attr("readonly", true);
		},

		_initialiseData: function () {
			var today = new Date();
			this.setModel(new JSONModel({
				gstins: [],
				frDate: (today.getMonth() < 9 ? "0" : "") + (today.getMonth() + 1) + today.getFullYear(),
				toDate: (today.getMonth() < 9 ? "0" : "") + (today.getMonth() + 1) + today.getFullYear(),
				minDate: new Date(today.getFullYear(), today.getMonth()),
				maxDate: new Date()
			}), "FilterModel");
		},

		onChangeDateValue: function (type) {
			if (type === "E") {
				var oModel = this.getModel("FilterModel"),
					frDate = oModel.getProperty("/frDate"),
					toDate = oModel.getProperty("/toDate");

				if (new Date(frDate.substr(2), +frDate.substr(0, 2) - 1) > new Date(toDate.substr(2), +toDate.substr(0, 2) - 1)) {
					oModel.setProperty("/toDate", frDate);
				}
			} else {
				oModel = this.getModel("SummFilterModel");
				frDate = oModel.getProperty("/frSummDate");
				toDate = oModel.getProperty("/toSummDate");

				if (new Date(frDate.substr(2), +frDate.substr(0, 2) - 1) > new Date(toDate.substr(2), +toDate.substr(0, 2) - 1)) {
					oModel.setProperty("/toSummDate", frDate);
				}
			}
			oModel.setProperty("/minDate", new Date(frDate.substr(2), +frDate.substr(0, 2) - 1));
			oModel.refresh(true);
		},

		onPressClear: function () {
			this._initialiseData();
			this.onSearch2bVs3b();
		},

		onSearch2bVs3b: function () {
			var oFilter = this.getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oFilter.frDate,
						"taxPeriodTo": oFilter.toDate,
						"dataSecAttrs": {
							"GSTIN": this.removeAll(oFilter.gstins)
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2bVsGstr3bProcessSummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.setModel(new JSONModel(data.resp), "Gstr2b3bProcessData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onInitiateRecon: function () {
			var aIndex = this.byId("tabEntity").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.success("Please select atleast one GSTIN");
				return;
			}
			MessageBox.show("Do you want to Generate?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._initiateRecon2bVs3b();
					}
				}.bind(this)
			});
		},

		_initiateRecon2bVs3b: function () {
			var oModel = this.getModel("Gstr2b3bProcessData").getProperty("/"),
				oFilter = this.getModel("FilterModel").getProperty("/"),
				aIndex = this.byId("tabEntity").getSelectedIndices(),
				aGstin = aIndex.map(function (e) {
					return oModel[e].gstin;
				}),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oFilter.frDate,
						"taxPeriodTo": oFilter.toDate,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2bvs3bproceCall.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp, {
							styleClass: "sapUiSizeCompact"
						});
						this.onSearch2bVs3b();
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr2avs3bproceCall. : Error");
				}.bind(this));
		},

		onDialogPress: function () {
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "{MsgList>/title}",
					content: new sap.m.List({
						items: {
							path: "MsgList>/msg",
							template: new sap.m.StandardListItem({
								title: "{MsgList>title}",
								description: "{MsgList>gstin}",
								icon: "{MsgList>icon}",
								highlight: "{MsgList>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new sap.m.Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							this.onSearch2bVs3b();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onInitiateGetGstr3b: function () {
			var oData = this.byId("tabEntity").getModel("Gstr2b3bProcessData").getProperty("/"),
				oFilter = this.getModel("FilterModel").getProperty("/"),
				aIndex = this.byId("tabEntity").getSelectedIndices(),
				payload = {};
			if (!aIndex.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			payload.req = aIndex.map(function (e) {
				return {
					"taxPeriod": "",
					"gstin": oData[e].gstin,
					"fromPeriod": oFilter.frDate,
					"toPeriod": oFilter.toDate
				};
			}.bind(this));
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGSTR3BSummaryFromGSTN.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (!data.resp.length) {
						MessageBox.information("No Data");
						return;
					}
					var aMessage = data.resp.map(function (e) {
						return {
							icon: "sap-icon://message-success",
							title: e.gstin,
							gstin: e.msg,
							active: true,
							highlight: "Success"
						};
					});
					this.getView().setModel(new JSONModel({
						"title": "Get GSTR-3B Status",
						"msg": aMessage
					}), "MsgList");
					this.onDialogPress();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					// MessageBox.error("getGSTR3BSummaryFromGSTN.do : Error");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onInitiateGetGstr2b: function () {
			var oModel = this.getModel("Gstr2b3bProcessData").getProperty("/"),
				oFilter = this.getModel("FilterModel").getProperty("/"),
				aIndex = this.byId("tabEntity").getSelectedIndices(),
				payload = {
					"req": {
						"gstins": [],
						"fromTaxPeriod": oFilter.frDate,
						"toTaxPeriod": oFilter.toDate
					}
				};

			if (!aIndex.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			payload.req.gstins = aIndex.map(function (e) {
				return oModel[e].gstin;
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2BResponse.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (!data.resp.length) {
						MessageBox.information("No Data");
						return;
					}
					var aMessage = data.resp.map(function (e) {
						return {
							icon: "sap-icon://message-success",
							title: e.gstin,
							gstin: e.msg,
							active: true,
							highlight: "Success"
						};
					});
					this.getView().setModel(new JSONModel({
						"title": "Get GSTR-2B Status",
						"msg": aMessage
					}), "MsgList");
					this.onDialogPress();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr2aGstnGetSection.do : Error");
				}.bind(this));
		},

		onDownloadEntityRpt: function () {
			var oFilter = this.getModel("FilterModel").getProperty("/"),
				oData = this.getModel("Gstr2b3bProcessData").getProperty("/"),
				aIndex = this.byId("tabEntity").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "P",
						"fromtaxPeriod": oFilter.frDate,
						"totaxPeriod": oFilter.toDate,
						"gstin": []
					}
				};

			if (!aIndex.length) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			}
			payload.req.gstin = aIndex.map(function (e) {
				return oData[e].gstin;
			});
			this.excelDownload(payload, "/aspsapapi/gstr2bvs3bReviewSummaryDownload.do");
		},

		onBackToEntity: function () {
			var oModel = this.getModel("Property");
			oModel.setProperty("/visiSumm", false);
			oModel.refresh(true);
		},

		onGstr2bSummary: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("Gstr2b3bProcessData").getObject(),
				oFilterData = this.getModel("FilterModel").getProperty("/"),
				oModel = this.getModel("Property");

			oModel.setProperty("/visiSumm", true);
			oModel.refresh(true);
			this.setModel(new JSONModel({
				gstin: obj.gstin,
				frSummDate: oFilterData.frDate,
				toSummDate: oFilterData.toDate,
				minDate: new Date(oFilterData.frDate.substr(2), +oFilterData.frDate.substr(0, 2) - 1),
				maxDate: new Date(),
				fullScreen: false
			}), "SummFilterModel");
			this.onSearch2bVs3bGstn();
		},

		onPressClearGstn: function () {
			var oFilterData = this.getModel("FilterModel").getProperty("/"),
				oGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin"),
				oModel = this.getModel("SummFilterModel");

			oModel.setProperty("/gstin", oGstin[0].value);
			oModel.setProperty("/frSummDate", oFilterData.frDate);
			oModel.setProperty("/toSummDate", oFilterData.toDate);
			oModel.refresh(true);
			this.onSearch2bVs3bGstn();
		},

		onSearch2bVs3bGstn: function () {
			var oFilter = this.getModel("SummFilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oFilter.frSummDate,
						"taxPeriodTo": oFilter.toSummDate,
						"gstin": oFilter.gstin
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2bVsGstr3bReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oSummary = [],
						obj = {
							"description": "Difference",
							"calFeild": "C=A-B",
							"igst": 0,
							"cgst": 0,
							"sgst": 0,
							"cess": 0
						};
					data.resp.forEach(function (e) {
						if (e.calFeild === "A") {
							obj.igst += e.igst;
							obj.cgst += e.cgst;
							obj.sgst += e.sgst;
							obj.cess += e.cess;
							oSummary.push(e);
						}
						if (e.calFeild === "B") {
							obj.igst -= e.igst;
							obj.cgst -= e.cgst;
							obj.sgst -= e.sgst;
							obj.cess -= e.cess;
							oSummary.push(e);
						}
					});
					data.resp.push(obj);
					oSummary.push(obj);
					this.setModel(new JSONModel(oSummary), "SummaryData");
					this.setModel(new JSONModel(data.resp), "GstnDetailsData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		on2bVs3bFullScreen: function (type) {
			var oModel = this.getModel("SummFilterModel");
			oModel.setProperty("/fullScreen", type === "O");
			oModel.refresh(true);
		},

		onDownloadGstnRpt: function () {
			var oFilter = this.getModel("SummFilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "R",
						"fromtaxPeriod": oFilter.frSummDate,
						"totaxPeriod": oFilter.toSummDate,
						"gstin": [oFilter.gstin]
					}
				};
			this.excelDownload(payload, "/aspsapapi/gstr2bvs3bReviewSummaryDownload.do");
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (["I", "Inactive"].includes(authToken)) {
				this.confirmGenerateOTP(gstin);
			}
		},

		onExportExcel: function (button) {
			if (button === "P") {
				var vType = "gstr2bvs3bPrSummary";
				var vFTaxPeriod = this.oView.byId("dp2b3bFr").getValue();
				var vTTaxPeriod = this.oView.byId("dp2b3bTo").getValue();
				// var vAdaptFilter = "dpProcessRecord";
				var aGstin = this.byId("idPGstin2B").getSelectedKeys(),
					oPayload = {
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
			} else {
				var vType = "gstr2bvs3bReviewSummary";
				var vFTaxPeriod = this.oView.byId("dp2b3bSummFr").getValue();
				var vTTaxPeriod = this.oView.byId("dp2b3bSummTo").getValue();
				// var vAdaptFilter = "dpGstr1Summary";
				var aGstin = this.oView.byId("idSGstin2B").getSelectedKey(),
					oPayload = {
						"req": {
							"type": vType,
							"entityId": [$.sap.entityID],
							"taxPeriodFrom": vFTaxPeriod,
							"taxPeriodTo": vTTaxPeriod,
							"gstin": aGstin
						}
					};
			}
			this.excelDownload(oPayload, "/aspsapapi/gstr2bvs3bScreenDownloads.do");
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Gstr2bVs3b
		 */
		//	onExit: function() {
		//
		//	}
	});
});