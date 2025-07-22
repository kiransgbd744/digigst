sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox"
], function (Controller, JSONModel, Fragment, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GSTR7_TXN", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR7_TXN
		 */
		onInit: function () {
			this.setReadonly("eTaxPeriod");
			this.setReadonly("gTaxPeriod");
			this._bindDefault();
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTR7_TXN
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onEntitySearch();
			}
			this.onBackToEntityTxn();
		},

		_bindDefault: function () {
			var dt = new Date(),
				obj = {
					"entityGstins": [],
					"entityTaxPeriod": (dt.getMonth() + 1).toString().padStart(2, 0) + dt.getFullYear(),
					"gstnGstins": [],
					"gstnTaxPeriod": (dt.getMonth() + 1).toString().padStart(2, 0) + dt.getFullYear(),
					"maxDate": dt,
					"gstnSaveToGstnDate": null
				};
			this.getView().setModel(new JSONModel(obj), "FilterModel");
		},

		_getGstr7TxnEntityData: function () {
			return new Promise(function (resolve, reject) {
				var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"returnType": "GSTR7_TRANSACTIONAL",
							"taxPeriod": oFilterData.entityTaxPeriod,
							"dataSecAttrs": {
								"GSTIN": this.removeAll(oFilterData.entityGstins)
							}
						}
					};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr7ProcessPrSummary.do",
						// url: "/aspsapapi/getGstr7TransProcessPrSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "EntityTxnModel");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onEntitySearch: function () {
			sap.ui.core.BusyIndicator.show(0);
			this._getGstr7TxnEntityData()
				.finally(function () {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
		},

		onEntityClear: function () {
			this._bindDefault();
			this.onEntitySearch();
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (["Inactive", "I"].includes(authToken)) {
				this.confirmGenerateOTP(gstin);
			}
		},

		onPressTxnGstn: function (gstin) {
			var oFilterModel = this.getView().getModel("FilterModel"),
				oFilterData = oFilterModel.getProperty("/");

			oFilterData.gstnGstins = gstin;
			oFilterData.gstnTaxPeriod = oFilterData.entityTaxPeriod;

			oFilterModel.refresh(true);
			this.byId("NavCon").to(this.byId("dpGstnGstr7Txn"));
			this.onGstnSearch();
		},

		onBackToEntityTxn: function () {
			this.byId("NavCon").to(this.byId("dpEntityGstr7Txn"));
		},

		onGstnClear: function () {
			var oFilterModel = this.getView().getModel("FilterModel");

			oFilterModel.setProperty("/gstnTaxPeriod", oFilterModel.getProperty("/entityTaxPeriod"));
			oFilterModel.setProperty("/gstnSaveToGstnDate", null);
			oFilterModel.refresh(true);
			this.onGstnSearch();
		},

		onGstnSearch: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"retPeriod": oFilterData.gstnTaxPeriod,
					"gstins": [oFilterData.gstnGstins]
				};
			sap.ui.core.BusyIndicator.show(0);
			this._visiTxnGstn();
			Promise.all([
					this._getGstr7TxnGstinData(oFilterData),
					this._saveToGstnStatus(payload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (error) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_saveToGstnStatus: function (req) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": req.retPeriod,
						"returnType": "GSTR7",
						"dataSecAttrs": {
							"GSTIN": req.gstins
						}
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSaveGstnStatus.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oModel = this.getView().getModel("FilterModel");
							oModel.setProperty("/gstnSaveToGstnDate", data.resp.updatedDate || null);
							oModel.refresh(true);
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		_getGstr7TxnGstinData: function (filterData) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"returnType": "GSTR7_TRANSACTIONAL",
						"taxPeriod": filterData.gstnTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [filterData.gstnGstins]
						}
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr7ReviewSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "GstnTxnModel");
						this._bindGstnHeader(data.resp);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		_bindGstnHeader: function (data) {
			var obj = {
				"ASP": this._initializeCategory(),
				"GSTN": this._initializeCategory(),
				"DIFF": this._initializeCategory()
			};
			data.response.forEach(function (el) {
				obj.ASP = this._aggregateData(obj.ASP, el, 'asp');
				obj.GSTN = this._aggregateData(obj.GSTN, el, 'gstn');
				obj.DIFF = this._aggregateData(obj.DIFF, el, 'diff');
			}.bind(this));
			this.getView().setModel(new JSONModel(obj), "GstnSummary");
		},

		_initializeCategory: function () {
			return {
				"count": 0,
				"totalTax": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0
			};
		},

		_aggregateData: function (category, el, prefix) {
			category.count += el[prefix + 'Count'];
			category.totalTax += el[prefix + 'TotalAmount'];
			category.igst += el[prefix + 'Igst'];
			category.cgst += el[prefix + 'Cgst'];
			category.sgst += el[prefix + 'Sgst'];
			return category;
		},

		_visiTxnGstn: function () {
			var obj = {
				"asp": true,
				"gstn": true,
				"diff": true,
				"enableAsp": false,
				"enableGstn": false,
				"enableDiff": false
			};
			this.getView().setModel(new JSONModel(obj), "VisiTxn");
		},

		onShowingCheckBox: function () {
			var oModel = this.getView().getModel("VisiTxn"),
				obj = oModel.getProperty("/");

			obj.enableAsp = !(obj.gstn || obj.diff);
			obj.enableGstn = !(obj.asp || obj.diff);
			obj.enableDiff = !(obj.asp || obj.gstn);
			oModel.refresh(true);
		},

		onCloseSaveStatus: function () {
			this._dSaveStats.close();
		},

		onPressSaveStatus: function (oEvent, view) {
			if (!this._dSaveStats) {
				this._dSaveStats = Fragment.load({
						id: this.getView().getId(),
						name: "com.ey.digigst.fragments.gstr7_txn.SaveStatus",
						controller: this
					})
					.then(function (oDialog) {
						this._dSaveStats = oDialog;
						this.getView().addDependent(oDialog);
						this.byId("dtSaveStats").addEventDelegate({
							onAfterRendering: function (oEvent) {
								oEvent.srcControl.$().find("input").attr("readonly", true);
							}
						});
						this._setSaveStatus(oEvent, view);
					}.bind(this));
			} else {
				this._setSaveStatus(oEvent, view);
			}
		},

		_setSaveStatus: function (oEvent, view) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/");
			if (view === "E") {
				var obj = oEvent.getSource().getBindingContext("EntityTxnModel").getObject(),
					vPeriod = oFilterData.entityTaxPeriod,
					vGstin = obj.gstin;
			} else {
				vGstin = oFilterData.gstnGstins;
				vPeriod = oFilterData.gstnTaxPeriod;
			}
			this._dSaveStats.setModel(new JSONModel({
				"gstin": vGstin,
				"taxPeriod": vPeriod,
				"maxDate": new Date()
			}), "DialogProperty");
			this._dSaveStats.open();
		},

		onPressStatusGo: function () {
			var oProp = this._dSaveStats.getModel("DialogProperty").getProperty("/"),
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oProp.taxPeriod,
						"isTransactional": true,
						"dataSecAttrs": {
							"GSTIN": [oProp.gstin]
						}
					}
				};

			this._dSaveStats.setBusy(true);
			this._dSaveStats.setModel(new JSONModel(), "SaveStatus");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr7SummarySaveStatus.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._dSaveStats.setBusy(false);
					if (data.resp) {
						data.resp.forEach(function (e, i) {
							e.sno = i + 1;
						});
					}
					this._dSaveStats.setModel(new JSONModel(data.resp || []), "SaveStatus");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dSaveStats.setBusy(false);
				}.bind(this));
		},

		// onDeleteDigiData: function (view) {
		// 	if (!this._dDeleteData) {
		// 		this._dDeleteData = Fragment.load({
		// 				id: this.getView().getId(),
		// 				name: "com.ey.digigst.fragments.gstr7_txn.DeleteDigiGST",
		// 				controller: this
		// 			})
		// 			.then(function (oDialog) {
		// 				this._dDeleteData = oDialog;
		// 				this.getView().addDependent(oDialog);
		// 				this._setDefaultDeleteData(view);
		// 			}.bind(this));
		// 	} else {
		// 		this._setDefaultDeleteData(view);
		// 	}
		// },

		// _setDefaultDeleteData: function (view) {
		// 	var oFilterData = this.getView().getModel("FilterModel").getProperty("/");
		// 	if (view === "E") {
		// 		var oData = this.getView().getModel("EntityTxnModel").getProperty("/"),
		// 			aIndex = this.byId("tabEntityGstr7").getSelectedIndices();
		// 		if (!aIndex.length) {
		// 			MessageBox.error("Select atleast one GSTIN to delete DigiGST data");
		// 			return;
		// 		}
		// 		var period = oFilterData.entityTaxPeriod,
		// 			gstin = aIndex.map(function (idx) {
		// 				return oData[idx].gstin;
		// 			});
		// 	} else {
		// 		gstin = [oFilterData.gstnGstins];
		// 		period = oFilterData.gstnTaxPeriod;
		// 	}
		// 	this._dDeleteData.setModel(new JSONModel({
		// 		"gstin": gstin,
		// 		"taxPeriod": period,
		// 		"partial": false,
		// 		"all": false,
		// 		"tds": false,
		// 		"tdsa": false
		// 	}), "DialogProperty");
		// 	this._dDeleteData.open();
		// },

		// onSelectAllDeleteData: function () {
		// 	var oModel = this._dDeleteData.getModel("DialogProperty"),
		// 		oData = oModel.getProperty("/");

		// 	oData.tds = oData.tdsa = oData.all;
		// 	oModel.refresh(true);
		// },

		// onSelectTable: function () {
		// 	var oModel = this._dDeleteData.getModel("DialogProperty"),
		// 		oData = oModel.getProperty("/");

		// 	oData.all = (oData.tds || oData.tdsa);
		// 	oData.partial = !(oData.tds && oData.tdsa);
		// 	oModel.refresh(true);
		// },

		// onCloseDeleteData: function (type) {
		// 	if (type === "C") {
		// 		this._dDeleteData.close();
		// 		return;
		// 	}
		// 	MessageBox.confirm("Are you sure, you want to proceed for deletion?", {
		// 		actions: [MessageBox.Action.YES, MessageBox.Action.NO],
		// 		onClose: function (sAction) {
		// 			if (sAction === 'YES') {
		// 				debugger;
		// 			}
		// 		}.bind(this)
		// 	});
		// },

		onPressDownloadReport: function (oEvent, key, type) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/");

			if (type === "E") {
				var oData = this.getView().getModel("EntityTxnModel").getProperty("/"),
					aIndex = this.byId("tabEntityGstr7").getSelectedIndices();
				if (!aIndex.length) {
					MessageBox.error("Select atleast one GSTIN to download report");
					return;
				}
				var taxPeriod = oFilterData.entityTaxPeriod,
					gstin = aIndex.map(function (idx) {
						return oData[idx].gstin;
					});
			} else {
				taxPeriod = oFilterData.gstnTaxPeriod;
				gstin = [oFilterData.gstnGstins];
			}
			if (key === "entityLevel") {
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": taxPeriod,
						"gstins": gstin
					}
				}
				this.reportDownload(payload, "/aspsapapi/gstr7TransEntityLevelReportDownload.do");
			} else {
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": taxPeriod,
						"type": key,
						"dataType": "GSTR7",
						"dataSecAttrs": {
							"GSTIN": gstin
						}
					}
				};
				this.reportDownload(payload, "/aspsapapi/downloadGstr7TransRSReports.do");
			}
		},

		onPressGstr7TableTypeLink: function (tableType) {
			if (tableType == "Table-3") {
				this._getTable3();
			} else if (tableType == "Table-4") {
				this._getTable4();
			}
		},

		_getTable3: function () {
			if (!this._oDialogTable3) {
				this._oDialogTable3 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7_txn.Table3PopUp", this);
				this.getView().addDependent(this._oDialogTable3);
				this.byId("idTable3taxPeriod").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}
				});
			}
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oProp = {
					"table": "Table-3",
					"taxPeriod": oFilterData.gstnTaxPeriod,
					"gstin": oFilterData.gstnGstins,
					"maxDate": new Date()
				};
			this._oDialogTable3.setModel(new JSONModel(oProp), "DialogProperty");
			this.getGstr7PopupSummary("Table-3", this._oDialogTable3)
			this._oDialogTable3.open();
		},

		onPressTable3Close: function () {
			this._oDialogTable3.close();
		},

		_getTable4: function () {
			if (!this._oDialogTable4) {
				this._oDialogTable4 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7_txn.Table4PopUp", this);
				this.getView().addDependent(this._oDialogTable4);
				this.byId("idTable4taxPeriod").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}
				});
			}
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oProp = {
					"table": "Table-4",
					"taxPeriod": oFilterData.gstnTaxPeriod,
					"gstin": oFilterData.gstnGstins,
					"maxDate": new Date()
				};
			this._oDialogTable4.setModel(new JSONModel(oProp), "DialogProperty");
			this.getGstr7PopupSummary("Table-4", this._oDialogTable4)
			this._oDialogTable4.open();
		},

		onPressTable4Close: function () {
			this._oDialogTable4.close();
		},

		getGstr7PopupSummary: function (table) {
			var oDialog = (table === "Table-3" ? this._oDialogTable3 : this._oDialogTable4),
				oFilter = oDialog.getModel("DialogProperty").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"returnType": "GSTR7_TRANSACTIONAL",
						"type": table,
						"taxPeriod": oFilter.taxPeriod,
						"dataSecAttrs": {
							"GSTIN": [oFilter.gstin]
						}
					}
				};
			oDialog.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7PopupSummary.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					oDialog.setBusy(false);
					oDialog.setModel(new JSONModel(data), "PopupSummary");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					oDialog.setBusy(false);
				}.bind(this));
		},

		onPressDifference: function (oEvent) {
			if (!this._dDifference) {
				this._dDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr7_txn.Difference", this);
				this.getView().addDependent(this._dDifference);
			}
			this._dDifference.open();

			var obj = oEvent.getSource().getBindingContext("EntityTxnModel").getObject();
			this.getView().byId("ifDiffGSTIN").setText(obj.gstin);
			this._getDifferenceData();
		},

		_getDifferenceData: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"returnType": "GSTR7_TRANSACTIONAL",
						"taxPeriod": oFilter.entityTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": [this.getView().byId("ifDiffGSTIN").getText()]
						}
					}
				};
			this._dDifference.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7ReviewDiff.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._dDifference.setBusy(false);
					this._dDifference.setModel(new JSONModel(data.resp), "Difference");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dDifference.setBusy(false);
					this._dDifference.setModel(new JSONModel(null), "Difference");
					MessageBox.error("Error : getGstr7ReviewDiff ");
				}.bind(this));
		},

		onCloseDifferenceDialog: function () {
			this._dDifference.close();
		},

		onUpdateGstnDataProcess: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.byId("tabEntityGstr7").getModel("EntityTxnModel").getData(),
				aIndex = this.byId("tabEntityGstr7").getSelectedIndices();

			if (!aIndex.length) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var aGstin = aIndex.map(function (idx) {
					return oData[idx].gstin;
				}),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"action": "UPDATEGSTIN",
						"taxPeriod": oFilterData.entityTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};

			MessageBox.information("Do you want to Update GSTN Data for GSTR-7?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.getUpdateGstnButtonIntrigration1(payload);
					}
				}.bind(this)
			});
		},

		getUpdateGstnButtonIntrigration1: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7EntityReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var msg = "";
						if (data.resp.length) {
							data.resp.forEach(function (e, i) {
								if (e.msg) {
									if (e.msg.split(",")[0] != 'Success') {
										if (i == 0) {
											var msg = e.msg;
										} else {
											msg += "," + e.gstin;
										}
									}
								}
							});
						}
						if (msg) {
							sap.ui.core.BusyIndicator.hide();
							sap.m.MessageBox.error(msg, {
								styleClass: "sapUiSizeCompact"
							});
						} else {
							MessageBox.success("Successfully Updated");
							this._getGstr7TxnEntityData()
								.finally(function () {
									sap.ui.core.BusyIndicator.hide();
								}.bind(this))
						}
					} else {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.information(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onUpdateGstnData: function (oEvent, view) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"action": "UPDATEGSTIN",
						"taxPeriod": null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			switch (view) {
			case "D":
				oPayload.req.taxPeriod = oFilterData.entityTaxPeriod;
				oPayload.req.dataSecAttrs.GSTIN = [this.byId("ifDiffGSTIN").getText()];
				break;
			case "G":
				oPayload.req.taxPeriod = oFilterData.gstnTaxPeriod;
				oPayload.req.dataSecAttrs.GSTIN = [oFilterData.gstnGstins];
			}
			sap.m.MessageBox.confirm("Do you want to Update GSTN Data for GSTR-7?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						this._getUpdateGstnData(oPayload);
					}
				}.bind(this)
			});
		},

		_getUpdateGstnData: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7ReviewSummary.do", // Gstr1GstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (this._dDifference && this._dDifference.isOpen()) {
							this.byId("tDiffUpdate").setText(data.resp.lastUpdatedDate ? ("Last Updated: " + data.resp.lastUpdatedDate) : null);
							this._getDifferenceData();
						} else {
							this._getSummaryData();
						}
						sap.m.MessageBox.success(this.getResourceBundle().getText("msgUpdateData"), {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSaveToGstn: function (type) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"isTransactional": true,
						"retPeriod": null,
						"gstins": []
					}
				};
			if (type === "E") {
				var aData = this.byId("tabEntityGstr7").getModel("EntityTxnModel").getProperty("/"),
					vIndices = this.byId("tabEntityGstr7").getSelectedIndices();

				if (!vIndices.length) {
					sap.m.MessageBox.information("Please select atleast one GSTIN", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.retPeriod = oFilterData.entityTaxPeriod;
				oPayload.req.gstins = vIndices.map(function (e) {
					return aData[e].gstin;
				});
			} else {
				oPayload.req.retPeriod = oFilterData.gstnTaxPeriod;
				oPayload.req.gstins.push(oFilterData.gstnGstins);
			}

			sap.m.MessageBox.confirm('Do you want to save data for selected GSTIN(s)?', {
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						this._saveToGstn(oPayload);
					}
				}.bind(this)
			});
		},

		_saveToGstn: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr7SaveToGstnJob.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this._msgSaveToGstn(data);
						this._saveToGstnStatus(payload.req)
							.finally(function () {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					} else {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length) {
							this._msgSaveToGstn(data);
						} else {
							sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
						}
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_msgSaveToGstn: function (data) {
			var aMockMessages = data.resp.map(function (e) {
				return {
					type: 'Success',
					title: e.gstin,
					gstin: e.msg,
					active: true,
					icon: "sap-icon://message-success",
					highlight: "Success",
					info: "Success"
				};
			});
			this.getView().setModel(new JSONModel(aMockMessages), "Msg");
			this.onDialogPress();
		},

		onDialogPress: function () {
			if (!this.pressDialog) {
				this.pressDialog = new sap.m.Dialog({
					title: "Save to GSTN",
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
					endButton: new sap.m.Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		}
	});
});