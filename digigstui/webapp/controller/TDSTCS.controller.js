sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, Formatter, JSONModel, MessageBox, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.TDSTCS", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.TDSTCS
		 */
		onInit: function () {
			this._setDateProperty("idTdsDate");
			this._setDateProperty("idTdsSummary");
			this._setDateProperty("idTdsaDate");
			this._setDateProperty("idTdsaSummary");
			this._setDateProperty("idTcsDate");
			this._setDateProperty("idTcsSummary");
			this._setDateProperty("idTcsaDate");
			this._setDateProperty("idTcsaSummary");
			this._bindDefaultData();
			this.glbTaxperiod = this.byId("idTdsDate").getValue();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID &&
				this.getRouter().getHashChanger().getHash() === "Others/TDSTCS") {
				this.glbEntityId = $.sap.entityID;
				this.getView().byId("itbtdstcs").setSelectedKey("tds")
				this.handleIconTabBarSelect();
			}
		},

		_bindDefaultData: function () {
			var today = new Date(),
				period = (today.getMonth() + 1).toString().padStart(2, 0) + today.getFullYear(),
				obj = {
					"entityTdsGstin": [],
					"entityTdsPeriod": period,
					"entityTdsaGstin": [],
					"entityTdsaPeriod": period,
					"entityTcsGstin": [],
					"entityTcsPeriod": period,
					"entityTcsaGstin": [],
					"entityTcsaPeriod": period,
					"maxDate": today
				};
			this.getView().setModel(new JSONModel(obj), "FilterData");
		},

		handleIconTabBarSelect: function () {
			var key = this.getView().byId("itbtdstcs").getSelectedKey(),
				views = {
					"tds": "dpBulkSaveTDS",
					"tdsa": "dpBulkSaveTDSA",
					"tcs": "dpBulkSaveTCS",
					"tcsa": "dpBulkSaveTCSA",
					"tdsS": "dpBulkSaveTDSSummary",
					"tdsaS": "dpBulkSaveTDSASummary",
					"tcsS": "dpBulkSaveTCSSummary",
					"tcsaS": "dpBulkSaveTCSASummary"
				};

			for (var view in views) {
				this.byId(views[view]).setVisible(false);
			}
			if (views[key]) {
				this.byId(views[key]).setVisible(true);
			}
			this._getTdsTcsEntityData(key);
		},

		/**
		 * Get Entity Level Data
		 */
		onPressTDSProessGo: function () {
			this._getTdsTcsEntityData("tds");
		},

		onPressTDSAProcessGo: function () {
			this._getTdsTcsEntityData("tdsa");
		},

		onPressTCSProcessGo: function () {
			this._getTdsTcsEntityData("tcs");
		},

		onPressTCSAProcessGo: function () {
			this._getTdsTcsEntityData("tcsa");
		},

		_getTdsTcsEntityData: function (action) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this._getEntityGstinTaxPeriod(oFilter, action + '-date'),
						"action": action.toUpperCase(),
						"dataSecAttrs": {
							"GSTIN": this._getEntityGstinTaxPeriod(oFilter, action + '-gstin'),
							"Plant": [],
							"PC": [],
							"D": [],
							"L": [],
							"PO": [],
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
			this._setTdsTcsData(action, []);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/tdsProcessSummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length) {
						this._setTdsTcsData(action, data);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "TDSProessData");
				}.bind(this));
		},

		_setTdsTcsData: function (type, data) {
			var oModel = new JSONModel(data);
			switch (type) {
			case "tds":
				this.getView().setModel(oModel, "TDSProessData");
				this.byId("idTableTds").clearSelection();
				break;
			case "tdsa":
				this.getView().setModel(oModel, "TDSAProessData");
				this.byId("idTableTdsa").clearSelection();
				break;
			case "tcs":
				this.getView().setModel(oModel, "TCSProessData");
				this.byId("idTableTcs").clearSelection();
				break;
			case "tcsa":
				this.getView().setModel(oModel, "TCSAProessData");
				this.byId("idTableTcsa").clearSelection();
				break;
			}
		},

		/**
		 * Get Detail Level Data
		 */
		onPressTdsGstin: function (oEvent) {
			var oFilterModel = this.getView().getModel("FilterData"),
				obj = oEvent.getSource().getBindingContext("TDSProessData").getObject();

			oFilterModel.setProperty("/gstnTdsGstin", obj.gstin);
			oFilterModel.setProperty("/gstnTdsPeriod", oFilterModel.getProperty("/entityTdsPeriod"));
			oFilterModel.refresh(true);

			this.byId("dpBulkSaveTDS").setVisible(false);
			this.byId("dpBulkSaveTDSSummary").setVisible(true);
			this._getTdsTcsDetailData("tds");
		},

		onPressTdsSumGo: function () {
			this._getTdsTcsDetailData("tds");
		},

		onPressBackTDSSum: function () {
			this.byId("dpBulkSaveTDS").setVisible(true);
			this.byId("dpBulkSaveTDSSummary").setVisible(false);
		},

		onPressTdsaGstin: function (oEvent) {
			var oFilterModel = this.getView().getModel("FilterData"),
				obj = oEvent.getSource().getBindingContext("TDSAProessData").getObject();

			oFilterModel.setProperty("/gstnTdsaGstin", obj.gstin);
			oFilterModel.setProperty("/gstnTdsaPeriod", oFilterModel.getProperty("/entityTdsaPeriod"));
			oFilterModel.refresh(true);

			this.byId("dpBulkSaveTDSA").setVisible(false);
			this.byId("dpBulkSaveTDSASummary").setVisible(true);

			this._getTdsTcsDetailData('tdsa');
		},

		onPressTDSASummaryGo: function () {
			this._getTdsTcsDetailData('tdsa');
		},

		onPressBackTDSASum: function () {
			this.byId("dpBulkSaveTDSA").setVisible(true);
			this.byId("dpBulkSaveTDSASummary").setVisible(false);
		},

		onPressTCSGstin: function (oEvent) {
			var oFilterModel = this.getView().getModel("FilterData"),
				obj = oEvent.getSource().getBindingContext("TCSProessData").getObject();

			oFilterModel.setProperty("/gstnTcsGstin", obj.gstin);
			oFilterModel.setProperty("/gstnTcsPeriod", oFilterModel.getProperty("/entityTcsPeriod"));
			oFilterModel.refresh(true);

			this.byId("dpBulkSaveTCS").setVisible(false);
			this.byId("dpBulkSaveTCSSummary").setVisible(true);

			this._getTdsTcsDetailData('tcs');
		},

		onPressTcsSummaryGo: function () {
			this._getTdsTcsDetailData('tcs');
		},

		onPressBackTCSSum: function () {
			this.byId("dpBulkSaveTCS").setVisible(true);
			this.byId("dpBulkSaveTCSSummary").setVisible(false);
		},

		onPressTCSAGstin: function (oEvent) {
			var oFilterModel = this.getView().getModel("FilterData"),
				obj = oEvent.getSource().getBindingContext("TCSAProessData").getObject();

			oFilterModel.setProperty("/gstnTcsaGstin", obj.gstin);
			oFilterModel.setProperty("/gstnTcsaPeriod", oFilterModel.getProperty("/entityTcsaPeriod"));
			oFilterModel.refresh(true);

			this.byId("dpBulkSaveTCSA").setVisible(false);
			this.byId("dpBulkSaveTCSASummary").setVisible(true);

			this._getTdsTcsDetailData('tcsa');
		},

		onPressTcsaSummaryGo: function () {
			this._getTdsTcsDetailData('tcsa');
		},

		onPressBackTCSASum: function () {
			this.byId("dpBulkSaveTCSA").setVisible(true);
			this.byId("dpBulkSaveTCSASummary").setVisible(false);
		},

		_getTdsTcsDetailData: function (type) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/"),
				oTaxPeriod = this._getGstinTaxPeriod(oFilter, type + '-date'),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"action": type.toUpperCase(),
						"dataSecAttrs": {
							"GSTIN": [this._getGstinTaxPeriod(oFilter, type + '-gstin')]
						}
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			this._bindTdsTcsSummaryData(type, null);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/tdsSummaryRecords.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (!data.resp || !data.resp.length) {
						// MessageBox.information("No Data");
						return;
					}
					var oTotal = {
						"totalCount": (data.resp ? data.resp.length : 0),
						"totalAmount": 0,
						"totalTax": 0,
						"igst": 0,
						"sgst": 0,
						"cgst": 0
					};
					data.resp.forEach(function (el) {
						oTotal.totalAmount += el.totamount;
						oTotal.igst += el.amountIgst;
						oTotal.sgst += el.amountSgst;
						oTotal.cgst += el.amountCgst;
						oTotal.totalTax += (el.amountIgst + el.amountSgst + el.amountCgst);
					});
					this._bindTdsTcsSummaryData(type, data, oTotal);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_bindTdsTcsSummaryData: function (type, data, totalData) {
			if (!totalData) {
				totalData = {
					"totalCount": 0,
					"totalAmount": 0,
					"totalTax": 0,
					"igst": 0,
					"sgst": 0,
					"cgst": 0
				};
			}
			switch (type) {
			case "tds":
				this.getView().setModel(new JSONModel(data), "TDSSummaryData");
				this.getView().setModel(new JSONModel(totalData), "GstrTdsTcsTotalHead");
				break;
			case "tdsa":
				this.getView().setModel(new JSONModel(data), "TDSASummaryData");
				this.getView().setModel(new JSONModel(totalData), "GstrTdsaTotalHead");
				break;
			case "tcs":
				this.getView().setModel(new JSONModel(data), "TCSSummaryData");
				this.getView().setModel(new JSONModel(totalData), "GstrTcsTotalHead");
				break;
			case "tcsa":
				this.getView().setModel(new JSONModel(data), "TCSASummaryData");
				this.getView().setModel(new JSONModel(totalData), "GstrTcsaTotalHead");
				break;
			}
		},

		/**
		 * Get TDS/TCS/TDSA/TCSA
		 */
		onPressProcessGetTds: function (view, type) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/");
			if (view === "P") {
				var oTaxPeriod = this._getEntityGstinTaxPeriod(oFilter, type.toLowerCase() + '-date'),
					oData = this._getEntityTdsTcsTableData(type.toLowerCase() + "-data"),
					aIndex = this._getEntityTdsTcsTableData(type.toLowerCase() + "-idx"),
					payload = {
						"req": []
					};

				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
				aIndex.forEach(function (idx) {
					payload.req.push({
						"gstin": oData[idx].gstin,
						"ret_period": oTaxPeriod,
						"isFailed": false
					});
				})
			} else {
				var payload = {
					"req": [{
						"gstin": this._getGstinTaxPeriod(oFilter, type.toLowerCase() + '-gstin'),
						"ret_period": this._getGstinTaxPeriod(oFilter, type.toLowerCase() + '-date'),
						"isFailed": false
					}]
				};
			}
			MessageBox.information("Do you want to initiate Get call for TDS?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._getTdsIntrigrationFinal(payload, view, type);
					}
				}.bind(this)
			});
		},

		_getTdsIntrigrationFinal: function (payload, view, type) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr2xGstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length) {
						var aMockMessages = data.resp.map(function (e) {
							return {
								type: 'Success',
								title: e.gstin,
								gstin: e.msg,
								active: true,
								icon: (data.hdr.status === "S" ? "sap-icon://message-success" : "sap-icon://message-error"),
								highlight: (data.hdr.status === "S" ? "Success" : "Error"),
								info: "Success"
							};
						});
						this.getView().setModel(new JSONModel(aMockMessages), "Msg");
						this.onDialogPress(view, type);
					} else {
						sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDialogPress: function (view, type) {
			if (!this._dMessage) {
				this._dMessage = new Dialog({
					title: "Get TDS Data",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this._dMessage.close();
						}.bind(this)
					})
				});
				this.getView().addDependent(this._dMessage);
			}
			this._dMessage.setModel(new JSONModel({
				"view": view,
				"type": type
			}), "DialogProperty");
			this._dMessage.open();
		},

		/**
		 * Download Reports TDS/TCS/TDSA/TCSA
		 */
		onPressProcessDownload: function (view, screen, type) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/");
			if (view === "P") {
				var oTaxPeriod = this._getEntityGstinTaxPeriod(oFilter, screen.toLowerCase() + '-date'),
					oData = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-data"),
					aIndex = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-idx"),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": oTaxPeriod,
							"type": type,
							"gstin": []
						}
					};
				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
				aIndex.forEach(function (idx) {
					payload.req.gstin.push(oData[idx].gstin);
				});
			} else {
				var payload = {
					"req": {
						"type": type,
						"entityId": [$.sap.entityID],
						"taxPeriod": this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + '-date'),
						"gstin": [this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + '-gstin')]
					}
				};
			}
			this.excelDownload(payload, "/aspsapapi/downloadGetgstr2xReports.do");
		},

		/**
		 * TDS/TCS/TDSA/TCSA Action
		 */
		onAcceptActionPress: function (view, action, screen) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/");
			if (view === "P") {
				var oTaxPeriod = this._getEntityGstinTaxPeriod(oFilter, screen.toLowerCase() + '-date'),
					oData = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-data"),
					aIndex = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-idx"),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": oTaxPeriod,
							"gstinDeductor": [],
							"docKey": [],
							"screenType": view,
							"action": action,
							"type": screen,
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};

				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
				aIndex.forEach(function (idx) {
					payload.req.dataSecAttrs.GSTIN.push(oData[idx].gstin);
					if (oData[idx].docKey) {
						payload.req.docKey.push(oData[idx].docKey);
					}
				});
			} else {
				var oTaxPeriod = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + '-date'),
					aGstin = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + '-gstin'),
					oData = this._getGstinTdsTcsTableData(screen.toLowerCase() + "-data"),
					aIndex = this._getGstinTdsTcsTableData(screen.toLowerCase() + "-idx");

				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN Deductor");
					return;
				}
				var payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"gstinDeductor": [],
						"docKey": [],
						"screenType": view,
						"action": action,
						"type": screen,
						"dataSecAttrs": {
							"GSTIN": [aGstin]
						}
					}
				};
				aIndex.forEach(function (idx) {
					if (oData[idx].docKey) {
						payload.req.docKey.push(oData[idx].docKey);
					}
					if (oData[idx].gstinDeductee) {
						payload.req.gstinDeductor.push(oData[idx].gstinDeductee);
					}
				});
			}
			if (["TCS", "TCSA"].includes(screen) && action === "reject") {
				if (!this._dRejectRemark) {
					this._dRejectRemark = sap.ui.xmlfragment("com.ey.digigst.fragments.others.tdstcs.RejectRemark", this);
					this.getView().addDependent(this._dRejectRemark);
				}
				var aRemarks = this._getRemarks(),
					obj = {
						"remark": aRemarks[0].key,
						"comment": aRemarks[0].text,
						"edit": false
					};
				this._dRejectRemark.setModel(new JSONModel(payload), "ActionPayload");
				this._dRejectRemark.setModel(new JSONModel(aRemarks), "RemarkList");
				this._dRejectRemark.setModel(new JSONModel(obj), "RejectRemark");
				this._dRejectRemark.open();
				return;
			}
			MessageBox.information(this._getActionMessage(action), {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._tdsTcsAction(payload);
					}
				}.bind(this)
			});
		},

		onChangeRejectRemark: function () {
			var oModel = this._dRejectRemark.getModel("RejectRemark"),
				remark = oModel.getProperty("/remark"),
				aRemarks = this._getRemarks(),
				obj = aRemarks.find(function (e) {
					return e.key === remark;
				});

			oModel.setProperty("/comment", (remark !== "OTH" ? obj.text : null));
			oModel.setProperty("/edit", (remark === "OTH"));
			oModel.refresh(true);
		},

		onProceedRejectAction: function () {
			var payload = this._dRejectRemark.getModel("ActionPayload").getProperty("/"),
				obj = this._dRejectRemark.getModel("RejectRemark").getProperty("/");

			if (!obj.comment) {
				MessageBox.error("Please provide Rejected Reason.", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			payload.req.remark = obj.remark;
			payload.req.comment = obj.comment;

			this._dRejectRemark.close();
			this._tdsTcsAction(payload);
		},

		onCancelRejectAction: function () {
			this._dRejectRemark.close();
		},

		_getActionMessage: function (action) {
			switch (action) {
			case "accept":
				return "Do you want to Accept TDS Data ?";
			case "reject":
				return "Do you want to Reject TDS Data ?";
			case "noaction":
				return "Do you want to take No Action on TDS Data?";
			}
		},

		_tdsTcsAction: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/tdsUpdateAction.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						sap.m.MessageBox.success("Changes Made Successfully.", {
							styleClass: "sapUiSizeCompact"
						});
						if (payload.req.screenType === "P") {
							this._getTdsTcsEntityData(payload.req.type.toLowerCase());
						} else {
							this._getTdsTcsDetailData(payload.req.type.toLowerCase());
						}
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * TDS/TCS Save Data to GSTN
		 */
		onSaveToGstinPress1: function (view, screen) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/");
			if (view === "P") {
				var oTaxPeriod = this._getEntityGstinTaxPeriod(oFilter, screen.toLowerCase() + "-date"),
					aIndex = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-idx"),
					oData = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-data"),
					payload = {
						"req": {
							"entityId": $.sap.entityID,
							"retPeriod": oTaxPeriod,
							"gstins": [],
							"tableSections": []
						}
					};

				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
				payload.req.gstins = aIndex.map(function (idx) {
					return oData[idx].gstin;
				});
			} else {
				var oTaxPeriod = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + "-date"),
					aGstin = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + "-gstin"),
					payload = {
						"req": {
							"entityId": $.sap.entityID,
							"retPeriod": oTaxPeriod,
							"gstins": [aGstin],
							"tableSections": []
						}
					};
			}
			this.onSaveToGstinPress(payload);
		},

		onSaveToGstinPress: function (payload) {
			if (!this._dSaveToGstn) {
				this._dSaveToGstn = sap.ui.xmlfragment("com.ey.digigst.fragments.others.tdstcs.SaveToGstin", this);
				this.getView().addDependent(this._dSaveToGstn);
			}
			var obj = {
				"all": false,
				"TCS": false,
				"TCSA": false,
				"TDS": false,
				"TDSA": false
			};
			this.getView().setModel(new JSONModel(obj), "SaveToGstinData");
			this._dSaveToGstn.setModel(new JSONModel(payload), "SavePayload");
			this._dSaveToGstn.open();
		},

		onSelectAllSaveToGstn: function (oEvent) {
			var oModel = this.getView().getModel("SaveToGstinData"),
				sSelect = oEvent.getParameter('selected');

			["TDS", "TDSA", "TCS", "TCSA"].forEach(function (f) {
				oModel.setProperty("/" + f, sSelect);
			});
			oModel.refresh(true);
		},

		onSelectSaveToGstn: function () {
			var oType = ["TDS", "TDSA", "TCS", "TCSA"],
				oModel = this.getView().getModel("SaveToGstinData"),
				oData = oModel.getProperty("/"),
				flag1 = true;

			oType.forEach(function (f) {
				flag1 = flag1 && !!oData[f];
			});
			oData.all = flag1;
			oModel.refresh(true);
		},

		onCloseSaveToGstn: function () {
			this._dSaveToGstn.close();
		},

		onConfirmSaveToGstn: function () {
			this._dSaveToGstn.close();
			var oData = this.getView().getModel("SaveToGstinData").getProperty("/"),
				payload = this._dSaveToGstn.getModel("SavePayload").getProperty("/");

			["TDS", "TDSA", "TCS", "TCSA"].forEach(function (f) {
				if (oData[f]) {
					payload.req.tableSections.push(f);
				}
			}.bind(this));
			if (!payload.req.tableSections.length) {
				MessageBox.success("Please select atleast one Section");
				return;
			}
			this.getSaveGstnButtonIntrigrationFinal(payload);
		},

		getSaveGstnButtonIntrigrationFinal: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2XSaveToGstnJob.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length) {
						var flag = (data.hdr.status === "S"),
							aMockMessages = data.resp.map(function (el) {
								return {
									title: el.gstin,
									gstin: el.msg,
									icon: (flag ? "sap-icon://message-success" : "sap-icon://message-error"),
									highlight: (flag ? "Success" : "Error")
								};
							})
						this.getView().setModel(new JSONModel(aMockMessages), "Msg");
						this.onDialogPressStoG();
					} else {
						// sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDialogPressStoG: function () {
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Save to GSTN Message",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}"
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

		/**
		 * TDS/TCS Save Status
		 */
		onSaveStatusPress: function (view, screen) {
			var oFilter = this.getView().getModel("FilterData").getProperty("/");
			if (view === "P") {
				var oTaxPeriod = this._getEntityGstinTaxPeriod(oFilter, screen.toLowerCase() + "-date"),
					aIndex = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-idx"),
					oData = this._getEntityTdsTcsTableData(screen.toLowerCase() + "-data"),
					payload = {
						"req": {
							"entityId": ('' + $.sap.entityID),
							"taxPeriod": oTaxPeriod,
							"type": screen,
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};

				if (!aIndex.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
				payload.req.dataSecAttrs.GSTIN = aIndex.map(function (idx) {
					return oData[idx].gstin;
				});
			} else {
				var oTaxPeriod = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + "-date"),
					aGstin = this._getGstinTaxPeriod(oFilter, screen.toLowerCase() + "-gstin"),
					payload = {
						"req": {
							"entityId": $.sap.entityID,
							"taxPeriod": oTaxPeriod,
							"type": screen,
							"dataSecAttrs": {
								"GSTIN": [aGstin]
							}
						}
					};
			}
			this._openDialogSaveStatus(payload, oTaxPeriod);
		},

		_openDialogSaveStatus: function (payload, taxPeriod) {
			if (!this._oDialogSaveStatusBtn) {
				this._oDialogSaveStatusBtn = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.others.tdstcs.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStatusBtn);
			}
			this._setDateProperty("dtSaveStats1", new Date(taxPeriod.substr(2), (+taxPeriod.substr(0, 2) - 1), 1));
			this._oDialogSaveStatusBtn.open();
			this.getSaveStatusDataFinal(payload);
		},

		onCloseSaveStatus: function () {
			this._oDialogSaveStatusBtn.close();
		},

		getSaveStatusDataFinal: function (payload) {
			this._oDialogSaveStatusBtn.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2xSummarySaveStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._oDialogSaveStatusBtn.setBusy(false);
					var oModel = new JSONModel(null);
					if (data.resp.length) {
						oModel.setProperty("/", data);
					}
					this._oDialogSaveStatusBtn.setModel(oModel, "SaveStatusData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialogSaveStatusBtn.setBusy(false);
					this._oDialogSaveStatusBtn.setModel(new JSONModel(null), "SaveStatusData");
				}.bind(this));
		},

		/**
		 * Sign & File
		 */
		onSignFilePress: function () {
			MessageBox.confirm("Do you want to Sign & File?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {

					}
				}
			});
		},

		alphaNumberSpecial: function (oEvent) {
			var value = oEvent.getSource().getValue();
			if (/[^a-zA-Z0-9\-\/ ]/.test(value)) {
				oEvent.getSource().setValue(value.substr(0, value.length - 1));
			}
		},

		_getEntityGstinTaxPeriod: function (filterData, type) {
			switch (type) {
			case "tds-date":
				return filterData.entityTdsPeriod;
			case "tdsa-date":
				return filterData.entityTdsaPeriod;
			case "tcs-date":
				return filterData.entityTcsPeriod;
			case "tcsa-date":
				return filterData.entityTcsaPeriod;
			case "tds-gstin":
				var aGstin = filterData.entityTdsGstin;
				break;
			case "tdsa-gstin":
				aGstin = filterData.entityTdsaGstin;
				break;
			case "tcs-gstin":
				aGstin = filterData.entityTcsGstin;
				break;
			case "tcsa-gstin":
				aGstin = filterData.entityTcsaGstin;
				break;
			}
			return aGstin.includes('All') ? [] : aGstin;
		},

		_getEntityTdsTcsTableData: function (action) {
			switch (action) {
			case "tds-data":
				return this.getView().getModel("TDSProessData").getProperty("/resp");
			case "tdsa-data":
				return this.getView().getModel("TDSAProessData").getProperty("/resp");
			case "tcs-data":
				return this.getView().getModel("TCSProessData").getProperty("/resp");
			case "tcsa-data":
				return this.getView().getModel("TCSAProessData").getProperty("/resp");
			case "tds-idx":
				return this.byId("idTableTds").getSelectedIndices();
			case "tdsa-idx":
				return this.byId("idTableTdsa").getSelectedIndices();
			case "tcs-idx":
				return this.byId("idTableTcs").getSelectedIndices();
			case "tcsa-idx":
				return this.byId("idTableTcsa").getSelectedIndices();
			}
		},

		_getGstinTaxPeriod: function (filterData, type) {
			switch (type) {
			case "tds-date":
				return filterData.gstnTdsPeriod;
			case "tdsa-date":
				return filterData.gstnTdsaPeriod;
			case "tcs-date":
				return filterData.gstnTcsPeriod;
			case "tcsa-date":
				return filterData.gstnTcsaPeriod;
			case "tds-gstin":
				return filterData.gstnTdsGstin;
			case "tdsa-gstin":
				return filterData.gstnTdsaGstin;
			case "tcs-gstin":
				return filterData.gstnTcsGstin;
			case "tcsa-gstin":
				return filterData.gstnTcsaGstin;
			}
		},

		_getGstinTdsTcsTableData: function (action) {
			switch (action) {
			case "tds-data":
				return this.getView().getModel("TDSSummaryData").getProperty("/resp");
			case "tdsa-data":
				return this.getView().getModel("TDSASummaryData").getProperty("/resp");
			case "tcs-data":
				return this.getView().getModel("TCSSummaryData").getProperty("/resp");
			case "tcsa-data":
				return this.getView().getModel("TCSASummaryData").getProperty("/resp");
			case "tds-idx":
				return this.byId("idtabtdsSummary").getSelectedIndices();
			case "tdsa-idx":
				return this.byId("idtabtdsaSummary").getSelectedIndices();
			case "tcs-idx":
				return this.byId("idTabTcssum").getSelectedIndices();
			case "tcsa-idx":
				return this.byId("idTabTcsasum").getSelectedIndices();
			}
		},

		_getRemarks: function () {
			return [{
				key: "GSTIN",
				text: "GSTIN of Collector"
			}, {
				key: "NAME",
				text: "Trade name/Legal name of Collector"
			}, {
				key: "TPRD",
				text: "Tax Period of GSTR-8"
			}, {
				key: "GVAL",
				text: "Gross Value"
			}, {
				key: "SUPR",
				text: "Supplies returned"
			}, {
				key: "NVAL",
				text: "Net Value"
			}, {
				key: "IGST",
				text: "Amount of tax collected by e-commerce operators - IGST"
			}, {
				key: "CGST",
				text: "Amount of tax collected by e-commerce operators - CGST"
			}, {
				key: "SGST",
				text: "Amount of tax collected by e-commerce operators - SGST/UTGST"
			}, {
				key: "POS",
				text: "POS"
			}, {
				key: "OTH",
				text: "Others (Specify)"
			}];
		}
	});
});