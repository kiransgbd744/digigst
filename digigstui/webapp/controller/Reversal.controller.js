sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/core/util/Export",
	"com/ey/digigst/util/others/Reversal",
	"sap/ui/core/util/ExportTypeCSV"
], function (BaseController, JSONModel, Formatter, MessageBox, MessageToast, Export, Reversal, ExportTypeCSV) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Reversal", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Reversal
		 */
		onInit: function () {
			var oShow = {
				"ratio1": true,
				"ratio2": true,
				"ratio3": true,
				"enabledRatio1": false,
				"enableRatio2": false,
				"enableRatio3": false,
				"check": false
			};
			this.getView().setModel(new JSONModel(oShow), "showing");
			this._setDateProperty("idTaxperiodCrrev", new Date(), null, null);
			this._setDateProperty("idTaxperiodCrrev1", null, null, null);
			this._setDateProperty("idTaxperiodCrrev12", null, null, null);
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.getCreditRevProcessData();
				this.getView().byId("idFinanceWiseReverasl").setVisible(false);
				this.getView().byId("idCreditReverasl").setVisible(true);
				this.getView().byId("idCreditRe").setSelectedKey("RPWise");
				this.getView().byId("idCreditReFinWise1").setSelectedKey("RPWise");
			}
			this.getAllGstin();
		},

		getAllGstin: function () {
			var aReg = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin"),
				aISD = this.getOwnerComponent().getModel("ISDGstin").getProperty("/"),
				aTDS = this.getOwnerComponent().getModel("TDSGstin").getProperty("/"),
				aGstin = [];

			aReg.forEach(function (e) {
				aGstin.push(e);
			});
			aISD.forEach(function (e) {
				aGstin.push(e);
			});
			aTDS.forEach(function (e) {
				aGstin.push(e);
			});
			aGstin.sort(function (a, b) {
				return (a.value > b.value ? 1 : (a.value < b.value ? -1 : 0));
			});
			aGstin.unshift({
				"value": "All"
			})
			this.getView().setModel(new JSONModel(aGstin), "GstinModel");
			this.byId("idgstingstCrRev").setSelectedKeys(null);
			this.byId("idgstingstFinWise1").setSelectedKeys(null);
		},

		onButtonExtractPress: function (oEvent) {
			var vGetDatagstn = this.getView().byId("idtableRPWise").getSelectedIndices();
			if (!vGetDatagstn.length) {
				MessageBox.success("Please select atleast one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Compute Reversal?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.getComputeReversalBtnIntrigration();
					}
				}.bind(this)
			});
		},

		getComputeReversalBtnIntrigration: function () {
			var oTablIndxPR = this.byId("idtableRPWise").getSelectedIndices(),
				oModelForTab = this.byId("idtableRPWise").getModel("CreditRevPRData").getProperty("/resp"),
				oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue(),
				aGstin = oTablIndxPR.map(function (e) {
					return oModelForTab[e].gstin
				}),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			this.getCreditRevBtnIntrigrationFinal(postData);
		},

		getCreditRevBtnIntrigrationFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/proceCallComputeReversal.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.onPressCreditRevPrGo();
						this._showMsgList(data, "Compute Reversal");
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("proceCallComputeReversal : Error");
				});
		},

		onButtonExtractPress1: function () {
			var vGetDatagstn = this.getView().byId("idtableRPWise").getSelectedIndices();
			MessageBox.information("Do you want to Compute Reversal?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.getComputeReversalSumBtnIntrigration();
					}
				}.bind(this)
			});
		},

		getComputeReversalSumBtnIntrigration: function () {
			var oTaxPeriod = this.getView().byId("idTaxperiodCrrev1").getValue(),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("idgstingstCrRev1").getSelectedKey()]
						}
					}

				};
			this.getCreditRevSumBtnIntrigrationFinal(postData);
		},

		getCreditRevSumBtnIntrigrationFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/proceCallComputeReversal.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this._showMsgList(data, "Compute Reversal");
						this.onPressCreditRevGo();
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("proceCallComputeReversal : Error");
				});
		},

		handleLinkPressGSTINMain123: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("CreditRevPRData").getObject();
			this.byId("idCreditReverasl").setVisible(false);
			this.getView().byId("idTaxperiodCrrev1").setValue(this.getView().byId("idTaxperiodCrrev").getValue());
			this.getView().byId("idTaxperiodCrrev12").setValue(this.getView().byId("idTaxperiodCrrev").getValue());
			this.getView().byId("idgstingstCrRev1").setSelectedKey(obj.gstin);
			this.getView().byId("idgstingstCrRev12").setSelectedKey(obj.gstin);
			this.getView().byId("idCreditRe1").setSelectedKey("RPWise");

			this.getView().byId("id_reversal").setVisible(true);
			this.getView().byId("id_turnover").setVisible(false);
			this.getView().byId("id_ReversalFilter").setVisible(true);
			this.getView().byId("Id_trunoverFilter").setVisible(false);
			this.getView().byId("fbSummaryHbox").setVisible(true);
			this.getView().byId("fbSummaryHbox1").setVisible(false);
			this.byId("idCreditReverasl1").setVisible(true);
			this.onPressCreditRevGo();
		},

		handleLinkPressGSTINMainFY123: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("FinYearProcessData").getObject();
			this.byId("idFinanceWiseReverasl").setVisible(false);
			this.getView().byId("idgstingstFinWise").setSelectedKey(obj.gstin);
			this.getView().byId("idgstingstFinWise12").setSelectedKey(obj.gstin);
			this.getView().byId("idFinWise").setSelectedKey("RPWise");
			this.getView().byId("id_reversalFinWise").setVisible(true);
			this.getView().byId("id_turnoverFinWise").setVisible(false);
			this.getView().byId("id_ReversalFilterFinWise").setVisible(true);
			this.getView().byId("Id_trunoverFilFinWise").setVisible(false);
			this.getView().byId("fbSummaryHboxFW").setVisible(true);
			this.getView().byId("fbSummaryHboxFw1").setVisible(false);
			this.byId("idCreditReveraslFW1").setVisible(true);
			this.onPressCreditRevFyGo();
		},

		onPressBackCreditRev: function () {
			this.byId("idCreditReverasl").setVisible(true);
			this.byId("idCreditReverasl1").setVisible(false);
			this.onPressCreditRevPrGo();
		},

		onPressBackCreditRevFY: function () {
			this.byId("idFinanceWiseReverasl").setVisible(true);
			this.byId("idCreditReveraslFW1").setVisible(false);
		},

		onChangeCreditReversal: function (oEvt) {
			var oSelIconKey = oEvt.getSource().getSelectedKey();
			if (oSelIconKey === "RPWise") {
				this.getView().byId("idFinanceWiseReverasl").setVisible(false);
				this.getView().byId("idCreditReveraslFW1").setVisible(false);
				this.getView().byId("idCreditReverasl").setVisible(true);
				this.getView().byId("idCreditRe").setSelectedKey("RPWise");
				this.getView().byId("idCreditReFinWise1").setSelectedKey("RPWise");
				this.getView().byId("idCreditReFinWise1").setVisible(true);
				this.getView().byId("id_ReversalFilterFinWise").setVisible(false);
				this.getCreditRevProcessData();
			} else if (oSelIconKey === "FYWise") {
				this.getView().byId("idCreditRe").setSelectedKey("FYWise");
				this.getView().byId("idCreditReveraslFW1").setVisible(false);
				this.getView().byId("idCreditReFinWise1").setSelectedKey("FYWise");
				this.getView().byId("idFinanceWiseReverasl").setVisible(true);
				this.getView().byId("idCreditReverasl").setVisible(false);
				this.getView().byId("id_ReversalFilterFinWise").setVisible(true);
				this.onPressFinWiseGo();
				// this.getView().byId("idCreditReveraslFW1").setVisible(false);
			}
		},

		onChangeCreditReversalSum: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();

			this.getView().byId("id_reversal").setVisible(key === "RPWise");
			this.getView().byId("id_turnover").setVisible(key === "FYWise");

			this.getView().byId("id_ReversalFilter").setVisible(key === "RPWise");
			this.getView().byId("Id_trunoverFilter").setVisible(key === "FYWise");

			this.getView().byId("fbSummaryHbox").setVisible(key === "RPWise");
			this.getView().byId("fbSummaryHbox1").setVisible(key === "FYWise");

			this.getView().byId("idCreditReveraslFW1").setVisible(false);

			if (key === "RPWise") {
				this.getView().byId("idTaxperiodCrrev1").setValue(this.getView().byId("idTaxperiodCrrev12").getValue());
				this.getView().byId("idgstingstCrRev1").setSelectedKey(this.getView().byId("idgstingstCrRev12").getSelectedKey());
				this.onPressCreditRevGo();
			} else if (key === "FYWise") {
				this.getView().byId("idTaxperiodCrrev12").setValue(this.getView().byId("idTaxperiodCrrev1").getValue());
				this.getView().byId("idgstingstCrRev12").setSelectedKey(this.getView().byId("idgstingstCrRev1").getSelectedKey());
				this.onPressTurnOverGo();
			}
		},

		onChangeCreditReversalSum1: function (oEvt) {
			var oSelIconKey = oEvt.getSource().getSelectedKey();
			if (oSelIconKey === "RPWise") {
				this.getView().byId("id_reversalFinWise").setVisible(true);
				this.getView().byId("id_turnoverFinWise").setVisible(false);
				this.getView().byId("id_ReversalFilterFinWise").setVisible(true);
				this.getView().byId("Id_trunoverFilFinWise").setVisible(false);
				this.getView().byId("fbSummaryHboxFW").setVisible(true);
				this.getView().byId("fbSummaryHboxFw1").setVisible(false);
				// this.getView().byId("idCreditReveraslFW1").setVisible(false);
				this.onPressCreditRevFyGo();
			} else if (oSelIconKey === "FYWise") {
				this.getView().byId("id_ReversalFilterFinWise").setVisible(false);
				this.getView().byId("fbSummaryHboxFW").setVisible(false);
				this.getView().byId("id_reversalFinWise").setVisible(false);
				this.getView().byId("id_turnoverFinWise").setVisible(true);
				this.getView().byId("Id_trunoverFilFinWise").setVisible(true);
				this.getView().byId("fbSummaryHboxFw1").setVisible(true);
				// this.getView().byId("idCreditReveraslFW1").setVisible(false);
				this.onPressTurnOverGoFY();
			}
		},

		onPressAdaptFilter: function (oEvent) {
			if (oEvent.getSource().getId().includes("idFilterProCRrev")) {
				this.flag = true;
			} else {
				this.flag = false;
			}
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}

			this._oAdpatFilter.open();

		},

		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
		},

		onMenuItemPresscreRev: function (oEvent, flag) {
			var key = oEvent.getParameters().item.getKey();
			switch (key) {
			case "ratio1":
				MessageBox.information("Do you want to push Ratio I Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio1");
						}
					}.bind(this)
				});
				break;
			case "ratio2":
				MessageBox.information("Do you want to push Ratio II Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio2");
						}
					}.bind(this)
				});
				break;
			case "ratio3":
				MessageBox.information("Do you want to push Ratio III Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio3");
						}
					}.bind(this)
				});
				break;
			}
		},

		fnBeinBtnPress: function (flag, Ratio) {
			if (flag === "P") {
				var vGetDatagstn = this.getView().byId("idtableRPWise").getSelectedIndices();
				if (!vGetDatagstn.length) {
					MessageBox.success("Please select atleast one GSTIN");
					return;
				}
			}

			switch (Ratio) {
			case "Ratio1":
				MessageBox.information("Do you want to push Ratio I Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio1");
						}
					}.bind(this)
				});
				break;
			case "Ratio2":
				MessageBox.information("Do you want to push Ratio II Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio2");
						}
					}.bind(this)
				});
				break;
			case "Ratio3":
				MessageBox.information("Do you want to push Ratio III Reversal to GSTR-3B Table 4B1?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.getCrediRevRatio1BtnData(flag, "Ratio3");
						}
					}.bind(this)
				});
				break;
			}
		},

		getCrediRevRatio1BtnData: function (flag, vAction) {
			if (flag === "S") {
				var oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue(),
					aGstin = [this.getView().byId("idgstingstCrRev1").getSelectedKey()];
			} else {
				var oModel = this.byId("idtableRPWise").getModel("CreditRevPRData").getProperty("/resp"),
					aIndex = this.getView().byId("idtableRPWise").getSelectedIndices(),
					oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue(),
					aGstin = aIndex.map(function (e) {
						return oModel[e].gstin;
					});
			}

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"ratio": vAction,
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			this.getCrediRevRatio1BtnDataFinal(postData);
		},

		getCrediRevRatio1BtnDataFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/pushToGstr3BCredRevRatio.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp, {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error(data.hdr.message);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onPartPressLink: function (oEvent) {
			var oKey = oEvent.getSource().getText();
			if (oKey === "Total Reversal As Per Ratio 1 (D1 + D2)") {
				this.fnBeinBtnPress('S', 'Ratio1');

			} else if (oKey === "Total Reversal As Per Ratio 2 (D1 + D2)") {
				this.fnBeinBtnPress('S', 'Ratio2');

			} else if (oKey === "Total Reversal As Per Ratio 3 (D1 + D2)") {
				this.fnBeinBtnPress('S', 'Ratio3');
			}
		},

		onPartPressLink1: function (oEvent) {
			this.getRouter().navTo("InvManageNew");
		},

		onSelectCheckBoxRatio11: function (oEvent) {
			var oVisiModel = this.getView().getModel("showing"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.ratio1 && !oVisiData.ratio2 && !oVisiData.ratio3) {
				oVisiData.enableRatio1 = true;

			} else if (!oVisiData.ratio1 && oVisiData.ratio2 && !oVisiData.ratio3) {
				oVisiData.enableRatio2 = true;

			} else if (!oVisiData.ratio1 && !oVisiData.ratio2 && oVisiData.ratio3) {
				oVisiData.enableRatio3 = true;

			} else {
				oVisiData.enableRatio1 = false;
				oVisiData.enableRatio2 = false;
				oVisiData.enableRatio3 = false;
			}
			oVisiModel.refresh(true);
		},

		expandCollapseCreditRevNew2: function (oEvent) {
			if (oEvent.getSource().getId().includes("idCreditRevExp2")) {
				this.byId("id_turnoverTab").expandToLevel(1);
				this.byId("id_turnoverTab").setVisibleRowCount(25);

				this.byId("id_turnoverTab2").expandToLevel(1);
				this.byId("id_turnoverTab2").setVisibleRowCount(18);
			} else {
				this.byId("id_turnoverTab").collapseAll();
				this.byId("id_turnoverTab").setVisibleRowCount(7);

				this.byId("id_turnoverTab2").collapseAll();
				this.byId("id_turnoverTab2").setVisibleRowCount(8);
			}
		},

		getCreditRevProcessData: function (oEntity) {
			var oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue(),
				aGstin = this.getView().byId("idgstingstCrRev").getSelectedKeys(),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin,
						}
					}
				};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);
			}
			this.getCreditRevProcessDataFinal(postData);
		},

		getCreditRevProcessDataFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCredRevrProcess.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (!data.resp || !data.resp.length) {
						MessageBox.information("No Data");
						this.getView().setModel(new JSONModel(null), "CreditRevPRData");
					} else {
						this.getView().setModel(new JSONModel(data), "CreditRevPRData");
					}
					this._bindSummaryData(data);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "CreditRevPRData");
				}.bind(this));
		},

		getCreditRevData: function (oEntity) {
			var oTaxPeriod = this.getView().byId("idTaxperiodCrrev1").getValue(),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("idgstingstCrRev1").getSelectedKey()]
						}
					}
				};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);
			}
			this.getCreditRevDataFinal(postData);
		},

		getCreditRevDataFinal: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCredReversal.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.tab.length === 0) {
						MessageBox.information("No Data");
						this.getView().setModel(new JSONModel(null), "CreditRevData");
					} else {
						this.getView().setModel(new JSONModel(data), "CreditRevData");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "CreditRevData");
				}.bind(this));
		},

		onPressCreditRevGo: function (oEvent) {
			this.getCreditRevData();
		},

		onPressCreditRevPrGo: function (oEvent) {
			this.getCreditRevProcessData();
		},

		onDownloadButtonPress: function (reportType) {
			var oModel = this.byId("idtableRPWise").getModel("CreditRevPRData").getProperty("/resp"),
				aIndex = this.getView().byId("idtableRPWise").getSelectedIndices(),
				oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue();

			if (!aIndex.length) {
				MessageBox.warning("Select at least one record");
				return;
			}
			var aGstin = aIndex.map(function (e) {
					return oModel[e].gstin;
				}),
				postPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "ITCREVERSAL",
						"taxPeriod": oTaxPeriod,
						"reportType": reportType,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			this.reportDownloadReversal(postPayload, "/aspsapapi/downloadreversalreports.do");
			return;
		},

		getCreditTurnOverData: function (oEntity) {
			var oTaxPeriod = this.getView().byId("idTaxperiodCrrev12").getValue(),
				postData = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("idgstingstCrRev12").getSelectedKey()]
						}
					}
				};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);
			}
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this.getCreditTurnOverDataFinal(postData),
					this.getCreditTurnOverBDataFinal(postData)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				})
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		getCreditTurnOverDataFinal: function (postData) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getCredTurnOverPartA.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						if (!data.resp.length) {
							MessageBox.information("No Data");
							this.getView().setModel(new JSONModel(null), "TurnOverData");
						} else {
							this.getView().setModel(new JSONModel(data), "TurnOverData");
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(null), "TurnOverData");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		getCreditTurnOverBDataFinal: function (postData) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getCredTurnOverPartB.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						data.resp.forEach(function (e) {
							e.edit = false;
							if (e.turnoverComp.includes("Ratio 1")) {
								e.key = "Ratio1";
							} else if (e.turnoverComp.includes("Ratio 2")) {
								e.key = "Ratio2";
							}
						});
						if (!data.resp.length) {
							MessageBox.information("No Data");
							this.getView().setModel(new JSONModel(null), "TurnOverDataB");
						} else {
							this.getView().setModel(new JSONModel(data), "TurnOverDataB");
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(null), "TurnOverDataB");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressTurnOverGo: function (oEvent) {
			this.getCreditTurnOverData();
		},

		onReversalDownloadpress: function (reportType) {
			var vGetDatagstn = this.getView().byId("idgstingstCrRev1").getSelectedKey(),
				oTaxPeriod = this.getView().byId("idTaxperiodCrrev1").getValue(),
				postPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "ITCREVERSAL",
						"taxPeriod": oTaxPeriod,
						"reportType": reportType,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("idgstingstCrRev1").getSelectedKey()]
						}
					}
				};
			this.reportDownloadReversal(postPayload, "/aspsapapi/downloadreversalreports.do");
			return;
		},

		OnturnoverDownloadPress: function (reportType) {
			var vGetDatagstn = this.getView().byId("idgstingstCrRev12").getSelectedKey(),
				oTaxPeriod = this.getView().byId("idTaxperiodCrrev12").getValue(),
				postPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"type": "ITCREVERSAL",
						"taxPeriod": oTaxPeriod,
						"reportType": reportType,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("idgstingstCrRev12").getSelectedKey()]
						}
					}
				};
			this.reportDownloadReversal(postPayload, "/aspsapapi/downloadreversalreports.do");
			return;
		},

		_bindSummaryData: function (data) {
			var oGstrSummary = {
				"ratio1": 0,
				"ratio2": 0,
				"ratio3": 0
			};
			data.resp.forEach(function (e) {
				oGstrSummary.ratio1 += e.ratio1TotalTax;
				oGstrSummary.ratio2 += e.ratio2TotalTax;
				oGstrSummary.ratio3 += e.ratio3TotalTax;
			});
			this.getView().setModel(new JSONModel(oGstrSummary), "creditRevSummary");
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onTurnoverRatioClear: function () {
			var oModel = this.getView().getModel("TurnOverDataB"),
				oData = oModel.getProperty("/resp");

			oData.forEach(function (e) {
				if (["Ratio1", "Ratio2"].includes(e.key)) {
					e.userInputRatio = "";
				}
			});
			oModel.refresh(true);
		},

		onTurnoverRatioEdit: function () {
			var oModel = this.getView().getModel("TurnOverDataB"),
				oData = oModel.getProperty("/resp");
			oData.forEach(function (e) {
				e.edit = true;
			});
			oModel.refresh(true);
		},

		onTurnoverRatioSave: function () {
			var oData = this.getView().getModel("TurnOverDataB").getProperty("/resp"),
				taxPeriod = this.byId("idTaxperiodCrrev12").getValue(),
				gstin = this.byId("idgstingstCrRev12").getSelectedKey(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": taxPeriod,
						"gstin": gstin
					}
				};

			["Ratio1", "Ratio2"].forEach(function (f) {
				var obj = oData.find(function (e) {
					return e.key === f;
				});
				if (obj) {
					payload.req["userInput" + f] = obj.userInputRatio;
				}
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveUserInputCredReversal.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var reqPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": taxPeriod,
							"dataSecAttrs": {
								"GSTIN": [gstin]
							}
						}
					};
					this.getCreditTurnOverBDataFinal(reqPayload)
						.then(function (values) {
							sap.ui.core.BusyIndicator.hide();
						}.bind(this))
						.catch(function (err) {
							sap.ui.core.BusyIndicator.hide();
						}.bind(this));
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onTurnoverRatioMoveData: function () {
			var oData = this.getView().getModel("TurnOverDataB").getProperty("/resp"),
				taxPeriod = this.byId("idTaxperiodCrrev12").getValue(),
				gstin = this.byId("idgstingstCrRev12").getSelectedKey(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": taxPeriod,
						"gstin": gstin
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/moveToUserInputCredReversal.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var payload = {
							"req": {
								"entityId": [$.sap.entityID],
								"taxPeriod": taxPeriod,
								"dataSecAttrs": {
									"GSTIN": [gstin]
								}
							}
						}
						MessageBox.success("Success");
						this.getCreditTurnOverBDataFinal(payload);
					} else {
						MessageBox.error("Failed");
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_visiTurnoverUsrInput: function (value) {
			if (!value) {
				return false;
			}
			return (value.includes("Ratio 1") || value.includes("Ratio 2"));
		},

		// financial year wise

		getFinanceyearProcessData: function (oEntity) {
			// var oTaxPeriod = this.getView().byId("idTaxperiodCrrev").getValue();
			var aGstin = this.getView().byId("idgstingstFinWise1").getSelectedKeys();
			var aFinYr = this.getView().byId("dtFinYearGstr123").getSelectedKey();
			var vFromDate = "04" + aFinYr.split('-')[0];
			var vStringformat = Number(aFinYr.split('-')[0]) + 1;
			var vToDate = "03" + JSON.stringify(vStringformat);
			// if (oTaxPeriod === "") {
			// 	oTaxPeriod = null;
			// }
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": vFromDate,
					"taxPeriodTo": vToDate,
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin,
					}

				}

			};
			this.getFinanceYearProcessDataFinal(postData);
		},

		getFinanceYearProcessDataFinal: function (postData) {
			debugger;
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getFinancialYearWiseEntity.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oFinPRSumData = new JSONModel(data);
					oView.setModel(oFinPRSumData, "FinYrPRData");
					that._bindfinTypSummaryData(data);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getFinancialYearWiseEntity : Error");
					var oFinPRSumData = new JSONModel(null);
					oView.setModel(oFinPRSumData, "FinYrPRData");

				});
			});
		},

		onPressFinWiseGo: function (oEvent) {
			this.getFinanceyearProcessData();
		},

		onSelectFinTypeGroup: function () {
			this._bindfinTypSummaryData();

		},
		_bindfinTypSummaryData: function () {
			debugger;
			var oData = this.getView().getModel("FinYrPRData").getData();
			var vRatio = this.getView().byId("idRadioFinType").getSelectedIndex();
			var oView = this.getView();
			if (vRatio === 0) {
				var oFinPRSumData = new JSONModel(oData.resp.Ratio1);
				oView.setModel(oFinPRSumData, "FinYearProcessData");

			} else if (vRatio === 1) {
				var oFinPRSumData = new JSONModel(oData.resp.Ratio2);
				oView.setModel(oFinPRSumData, "FinYearProcessData");

			} else if (vRatio === 2) {
				var oFinPRSumData = new JSONModel(oData.resp.Ratio3);
				oView.setModel(oFinPRSumData, "FinYearProcessData");

			}

		},
		onButtonExtractPressFY: function () {
			debugger;
			var that = this;
			var vGetDatagstn = that.getView().byId("idtableFYWise").getSelectedIndices();
			if (vGetDatagstn.length === 0) {
				MessageBox.success("Please select atleast one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Compute Reversal?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.getComputeReversalBtnIntrigrationFY();
					}
				}
			});
		},

		getComputeReversalBtnIntrigrationFY: function () {
			debugger;
			var aFinYr = this.getView().byId("dtFinYearGstr123").getSelectedKey();
			var vFromDate = "04" + aFinYr.split('-')[0];
			var vStringformat = Number(aFinYr.split('-')[0]) + 1;
			var vToDate = "03" + JSON.stringify(vStringformat);
			var oTablIndxPR = this.byId("idtableFYWise").getSelectedIndices();
			var oModelForTab = this.byId("idtableFYWise").getModel("FinYearProcessData").getData();

			var aGstin = [];
			for (var i = 0; i < oTablIndxPR.length; i++) {
				aGstin.push(oModelForTab[oTablIndxPR[i]].gstin);
			}

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"fromRetPeriod": vFromDate,
					"toRetPeriod": vToDate,
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			this.getCreditRevBtnIntrigrationFinalFY(postData);
		},

		getCreditRevBtnIntrigrationFinalFY: function (postData) {
			debugger;
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getProdCallCompFinaYearCredRev.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp, {
							styleClass: "sapUiSizeCompact"
						});
						that.onPressFinWiseGo();
					} else {
						MessageBox.error(data.hdr.message);

					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getProdCallCompFinaYearCredRev : Error");
				});
			});
		},

		onPressCreditRevFyGo: function (oEvent) {
			debugger;
			this.getCreditRevFYData();
		},

		getCreditRevFYData: function (oEntity) {
			var aFinYr = this.getView().byId("dtFinYearturnover").getSelectedKey();
			var vFromDate = "04" + aFinYr.split('-')[0];
			var vStringformat = Number(aFinYr.split('-')[0]) + 1;
			var vToDate = "03" + JSON.stringify(vStringformat);

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"fromRetPeriod": vFromDate,
					"toRetPeriod": vToDate,
					"dataSecAttrs": {
						"GSTIN": [this.getView().byId("idgstingstFinWise").getSelectedKey()]
					}
				}
			};
			this.getCreditRevDataFYFinal(postData);
		},

		getCreditRevDataFYFinal: function (postData) {
			var oView = this.getView();

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getCredReversalFinancialYear.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oCreditivSumfyData = new JSONModel(null);
						oView.setModel(oCreditivSumfyData, "CreditRevFYData");
					}
					var oCreditivSumfyData = new JSONModel(data);
					oView.setModel(oCreditivSumfyData, "CreditRevFYData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getCredReversalFinancialYear : Error");
					var oCreditivSumfyData = new JSONModel(null);
					oView.setModel(oCreditivSumfyData, "CreditRevFYData");

				});
			});
		},

		onPressTurnOverGoFY: function (oEvent) {
			debugger;
			this.getCreditTurnOverDataFY();

		},
		getCreditTurnOverDataFY: function (oEntity) {
			debugger;
			var aGstin = this.getView().byId("idgstingstFinWise12").getSelectedKey();
			var aFinYr = this.getView().byId("dtFinYearturnover2").getSelectedKey();
			var vFromDate = "04" + aFinYr.split('-')[0];
			var vStringformat = Number(aFinYr.split('-')[0]) + 1;
			var vToDate = "03" + JSON.stringify(vStringformat);

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"fromRetPeriod": vFromDate,
					"toRetPeriod": vToDate,
					"dataSecAttrs": {
						"GSTIN": [aGstin]
					}
				}
			};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);

			}
			this.getCreditTurnOverDataFinalFY(postData);
			this.getCreditTurnOverBDataFinalFY(postData);
		},

		getCreditTurnOverDataFinalFY: function (postData) {
			debugger;
			var oView = this.getView();

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getFinancialYearCredTurnOverPart1.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oTurnoverSumDataFY = new JSONModel(null);
						oView.setModel(oTurnoverSumDataFY, "TurnOverDataFY");
					}
					var oTurnoverSumDataFY = new JSONModel(data);
					oView.setModel(oTurnoverSumDataFY, "TurnOverDataFY");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getFinancialYearCredTurnOverPart1 : Error");
					var oTurnoverSumDataFY = new JSONModel(null);
					oView.setModel(oTurnoverSumDataFY, "TurnOverDataFY");

				});
			});
		},

		getCreditTurnOverBDataFinalFY: function (postData) {
			debugger;
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getFinancialYearCredTurnOverPart2.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oTurnoverSumDataFY = new JSONModel(null);
						oView.setModel(oTurnoverSumDataFY, "TurnOverDataBFY");
					}
					var oTurnoverSumDataFY = new JSONModel(data);
					oView.setModel(oTurnoverSumDataFY, "TurnOverDataBFY");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getFinancialYearCredTurnOverPart2 : Error");
					var oTurnoverSumDataFY = new JSONModel(null);
					oView.setModel(oTurnoverSumDataFY, "TurnOverDataBFY");
				});
			});
		},

		_showMsgList: function (data, title) {
			if (!this._msgList) {
				this._msgList = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.MsgList", this);
				this.getView().addDependent(this._msgList);
			}
			if (title) {
				this._msgList.setTitle(title);
			}
			this._msgList.setModel(new JSONModel(data.resp), "MessageList");
			this._msgList.open();
		},

		onCloseMsgList: function () {
			this._msgList.close();
		}
	});
});