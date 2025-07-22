sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox"
], function (Controller, JSONModel, Fragment, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GSTR8", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR8
		 */
		onInit: function () {
			this._setReadOnly("dTaxPeriod");
			this._setReadOnly("dGstnTaxPeriod");
			this._bindInitialData();
		},

		_bindInitialData: function () {
			var today = new Date();
			this.setModel(new JSONModel({
				"gstin": [],
				"taxPeriod": ('' + (today.getMonth() + 1)).padStart(2, 0) + today.getFullYear(),
				"maxDate": new Date()
			}), "FilterModel");
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTR8
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this._getGstr8Gstin(),
						this._getEntityLevelData()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
			var Key = this.getView().getModel("ReturnKey").getProperty("/key");
			this._onRouteMatched(Key);
		},

		_onRouteMatched: function (key) {

		},

		_getGstr8Gstin: function () {
			var payload = {
				"req": {
					"entityId": [$.sap.entityID]
				}
			};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr8Gstins.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var entityData = $.extend(true, [], data.resp.gstins);
						if (entityData.length) {
							entityData.unshift({
								gstin: "All"
							})
						}
						this.getView().setModel(new JSONModel(entityData), "EntityGstinModel");
						this.getView().setModel(new JSONModel(data.resp.gstins), "GstinModel");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getEntityLevelData: function () {
			var oFilterData = this.getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oFilterData.taxPeriod,
						"dataSecAttrs": {
							"GSTIN": oFilterData.gstin
						}
					}
				};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr8ProcessSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "EntiryLevelRecord");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onEntitySearch: function () {
			this._getEntityLevelData();
		},

		onEntityClear: function () {
			var oModel = this.getModel("FilterModel"),
				today = new Date();
			oModel.setProperty("/taxPeriod", ('' + (today.getMonth() + 1)).padStart(2, 0) + today.getFullYear());
			oModel.setProperty("/gstin", []);
			oModel.refresh(true);
			this._getEntityLevelData();
		},

		onDownloadReport: function (type, key) {
			if (type === "E") {
				var oTabData = this.getView().getModel("EntiryLevelRecord").getProperty("/"),
					aIndex = this.byId("tabEntiryGstr8").getSelectedIndices(),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": this.getModel("FilterModel").getProperty("/taxPeriod"),
							"dataSecAttrs": {
								"GSTIN": []
							}
						}
					};
				if (!aIndex.length) {
					MessageBox.information("Plese select atleast One GSTIN");
					return;
				}
				payload.req.dataSecAttrs.GSTIN = aIndex.map(function (e) {
					return oTabData[e].gstin;
				});
			} else {
				var obj = this.getModel("HeaderModel").getProperty("/"),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": obj.taxPeriod,
							"dataSecAttrs": {
								"GSTIN": [obj.gstin]
							}
						}
					};
			}
			switch (key) {
			case "processRecords":
				var url = "/aspsapapi/gstr8ProcessedReport.do";
				break;
			case "summaryRecords":
				url = "/aspsapapi/gstr8EntityLvlReport.do";
				break;
			}
			this.reportDownload(payload, url);
		},

		onDownloadEntityPDF: function (key) {
			var obj = this.byId("tabEntiryGstr8").getModel("EntiryLevelRecord").getProperty("/"),
				aIdx = this.byId("tabEntiryGstr8").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.byId("dTaxPeriod").getValue(),
						"dataSecAttrs": {},
						"isDigigst": (key === "digiGst")
					}
				};
			if (aIdx.length == 0) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			}
			payload.req.dataSecAttrs.GSTIN = aIdx.map(function (e) {
				return obj[e].gstin;
			});
			MessageBox.confirm("I hereby solemnly affirm and declare that the information given herein above is true" +
				" and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						payload.req.isVerified = sAction;
						this.reportDownload(payload, "/aspsapapi/Gstr8EntityLevelPdfReports.do");
					}.bind(this)
				});
		},

		onDifference: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("EntiryLevelRecord").getObject();
			if (!this.pDialog) {
				this.pDialog = this.loadFragment({
					name: "com.ey.digigst.fragments.gstr8.Difference"
				});
			}
			this.pDialog.then(function (oDialog) {
				oDialog.open();
				oDialog.setModel(new JSONModel(this._getDifferenceData()));
			}.bind(this));
		},

		onCloseDiffGstr8: function () {
			this.pDialog.then(function (oDialog) {
				oDialog.close();
			});
		},

		onBackToEntity: function () {
			this.byId("navCon").to(this.byId("dpGstr8Entity"));
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (["Inactive", "I"].includes(authToken)) {
				this.confirmGenerateOTP(gstin);
			}
		},

		onEntitySaveGstr8: function () {
			var aIndex = this.byId("tabEntiryGstr8").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.information("Plese select atleast One GSTIN");
				return;
			}

			MessageBox.confirm("You wanted to Save the data to GSTN, do you really want to continue?", {
				actions: ["Save", "Cancel"],
				emphasizedAction: "Save",
				onClose: function (sAction) {
					if (sAction === "Save") {
						var taxPeriod = this.getView().getModel("FilterModel").getProperty("/taxPeriod"),
							oTabData = this.getView().getModel("EntiryLevelRecord").getProperty("/"),
							payload = {
								"req": []
							};

						aIndex.forEach(function (e) {
							payload.req.push({
								"gstin": oTabData[e].gstin,
								"ret_period": taxPeriod
							});
						});
						this._gstr8SaveToGstn(payload);
					}
				}.bind(this)
			});
		},

		_gstr8SaveToGstn: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr8SavetoGstn.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var aMessage = data.resp.map(function (e) {
						return {
							title: (e.gstin || e.msg),
							desc: (e.gstin ? e.msg : ""),
							icon: (data.hdr.status === "S" ? "sap-icon://message-success" : "sap-icon://error"),
							highlight: (data.hdr.status === "S" ? "Success" : "Error")
						};
					});
					this._displayMessage("Save GSTR-8", aMessage);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onEntityGetGstr8: function () {
			var aIndex = this.byId("tabEntiryGstr8").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.information("Plese select atleast One GSTIN");
				return;
			}
			var taxPeriod = this.getView().getModel("FilterModel").getProperty("/taxPeriod"),
				oTabData = this.getView().getModel("EntiryLevelRecord").getProperty("/"),
				payload = {
					"req": []
				};
			payload.req = aIndex.map(function (e) {
				return {
					"gstin": oTabData[e].gstin,
					"ret_period": taxPeriod
				}
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr8Summary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var aMessage = data.resp.map(function (e) {
							return {
								title: (e.gstin || e.msg),
								desc: (e.gstin ? e.msg : ""),
								icon: (data.hdr.status === "S" ? "sap-icon://message-success" : "sap-icon://error"),
								highlight: (data.hdr.status === "S" ? "Success" : "Error")
							};
						});
						this._displayMessage("Get GSTR-8", aMessage);
						this._getEntityLevelData();
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onEntityGstin: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("EntiryLevelRecord").getObject(),
				taxPeriod = this.getModel("FilterModel").getProperty("/taxPeriod");

			this.byId("navCon").to(this.byId("dpGstr8Gstn"));
			this.setModel(new JSONModel({
				"gstin": obj.gstin,
				"taxPeriod": taxPeriod,
				"maxDate": new Date()
			}), "HeaderModel");
			this._getGstnLevelData();
		},

		onGstnLevelClear: function () {
			var aGstin = this.getView().getModel("GstinModel").getProperty("/"),
				oModel = this.getModel("HeaderModel");
			oModel.setProperty("/gstin", aGstin[0].gstin);
			oModel.setProperty("/taxPeriod", this.getModel("FilterModel").getProperty("/taxPeriod"));
			oModel.refresh(true);
			this._getGstnLevelData();
		},

		onGstnLevelSearch: function () {
			this._getGstnLevelData();
		},

		_getGstnLevelData: function () {
			var oFilterData = this.getModel("HeaderModel").getProperty("/"),
				payload = {
					"req": {
						"gstin": oFilterData.gstin,
						"taxPeriod": oFilterData.taxPeriod
					}
				};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr8ReviewSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var aField = ["TCS", "TCSA", "URD", "URDA"],
							aData = aField.map(function (f) {
								var obj = data.resp.response.find(function (e) {
									return e.section === f;
								});
								obj.diffGrossSuppliesMade = obj.aspGrossSuppliesMade - obj.gstnGrossSuppliesMade || 0;
								obj.diffGrossSuppliesReturned = obj.aspGrossSuppliesReturned - obj.gstnGrossSuppliesReturned || 0;
								obj.diffNetSupplies = obj.aspNetSupplies - obj.gstnNetSupplies || 0;
								obj.diffIgst = obj.aspIgst - obj.gstnIgst || 0;
								obj.diffCgst = obj.aspCgst - obj.gstnCgst || 0;
								obj.diffSgst = obj.aspSgst - obj.gstnSgst || 0;
								obj.items.forEach(function (e) {
									e.diffGrossSuppliesMade = e.gstnGrossSuppliesMade = "NA";
									e.diffGrossSuppliesReturned = e.gstnGrossSuppliesReturned = "NA";
								});
								return obj;
							});
						this.getView().setModel(new JSONModel(aData), "GstnLevelRecord");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onGstnSaveGstn: function () {
			MessageBox.confirm("You wanted to Save the data to GSTN, do you really want to continue?", {
				actions: ["Save", "Cancel"],
				emphasizedAction: "Save",
				onClose: function (sAction) {
					if (sAction === "Save") {
						var oFilterData = this.getView().getModel("HeaderModel").getProperty("/"),
							payload = {
								"req": [{
									"gstin": oFilterData.gstin,
									"ret_period": oFilterData.taxPeriod
								}]
							};
						this._gstr8SaveToGstn(payload);
					}
				}.bind(this)
			});
		},

		onGstnGetGstr8: function () {
			var oFilterData = this.getView().getModel("HeaderModel").getProperty("/"),
				payload = {
					"req": [{
						"gstin": oFilterData.gstin,
						"ret_period": oFilterData.taxPeriod
					}]
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr8Summary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var aMessage = data.resp.map(function (e) {
							return {
								title: (e.gstin || e.msg),
								desc: (e.gstin ? e.msg : ""),
								icon: (data.hdr.status === "S" ? "sap-icon://message-success" : "sap-icon://error"),
								highlight: (data.hdr.status === "S" ? "Success" : "Error")
							};
						});
						this._displayMessage("Get GSTR-8", aMessage);
						this._getGstnLevelData();
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSaveStatus: function () {
			if (!this._dSaveStatus) {
				this._dSaveStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr8.SaveStatus", this);
				this.getView().addDependent(this._dSaveStatus);

				this.byId("dtSaveStats").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}
				});
			}
			var taxPeriod = this.getView().getModel("HeaderModel").getProperty("/taxPeriod"),
				gstin = this.getView().getModel("HeaderModel").getProperty("/gstin"),
				payload = {
					"req": [{
						"entityId": $.sap.entityID,
						"gstin": gstin,
						"ret_period": taxPeriod
					}]
				};

			this._dSaveStatus.setModel(new JSONModel({
				"gstin": gstin,
				"taxPeriod": taxPeriod,
				"maxDate": new Date()
			}), "SaveStatsFilter");
			this._dSaveStatus.open();

			this._getSaveStatusData(payload);
		},

		onSearchSaveStatus: function () {
			var oFilter = this._dSaveStatus.getModel("SaveStatsFilter").getProperty("/"),
				payload = {
					"req": [{
						"entityId": $.sap.entityID,
						"gstin": oFilter.gstin,
						"ret_period": oFilter.taxPeriod
					}]
				};
			this._getSaveStatusData(payload);
		},

		_getSaveStatusData: function (payload) {
			this._dSaveStatus.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr8InvLvlSaveStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						data.resp.forEach(function (e, i) {
							e.sNo = i + 1;
						});
						oModel.setProperty("/", data.resp);
					} else {
						MessageBox.error(data.hdr.message);
					}
					this.getView().setModel(oModel, "SaveStatus");
					this._dSaveStatus.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dSaveStatus.setBusy(false);
				}.bind(this));
		},

		onCloseSaveStatus: function () {
			this._dSaveStatus.close();
		},

		onDownloadGstnPDF: function (key) {
			var obj = this.getModel("HeaderModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"gstin": obj.gstin,
						"taxPeriod": obj.taxPeriod,
						"isDigigst": (key === "digiGst")
					}
				};
			MessageBox.confirm("I hereby solemnly affirm and declare that the information given herein above is true" +
				" and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						payload.req.isVerified = sAction;
						this.pdfDownload(payload, "/aspsapapi/Gstr8GstinLevelPdfReports.do");
					}.bind(this)
				});
		},

		_displayMessage: function (title, aMsg) {
			if (!this._msgDisplay) {
				this._msgDisplay = new sap.m.Dialog({
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>desc}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}"
							})
						}
					}),
					endButton: new sap.m.Button({
						text: "Close",
						press: function () {
							this._msgDisplay.close();
						}.bind(this)
					})
				});
				this.getView().addDependent(this._msgDisplay);
			}
			this._msgDisplay.setTitle(title);
			this._msgDisplay.setModel(new JSONModel(aMsg), "Msg");
			this._msgDisplay.open();
		},

		_getDifferenceData: function () {
			return [{
				"section": "TCS",
				"grossSuppliesMade": 0,
				"grossSuppliesReturn": 0,
				"netSupplies": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0
			}, {
				"section": "TCSA",
				"grossSuppliesMade": 0,
				"grossSuppliesReturn": 0,
				"netSupplies": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0
			}, {
				"section": "URD",
				"grossSuppliesMade": 0,
				"grossSuppliesReturn": 0,
				"netSupplies": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0
			}, {
				"section": "URDA",
				"grossSuppliesMade": 0,
				"grossSuppliesReturn": 0,
				"netSupplies": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0
			}];
		},

		_setReadOnly: function (controlId) {
			this.byId(controlId).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GSTR8
		 */
		//	onExit: function() {
		//
		//	}
	});
});