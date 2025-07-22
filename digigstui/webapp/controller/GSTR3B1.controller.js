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
	return BaseController.extend("com.ey.digigst.controller.GSTR3B1", {
		formatter: formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR3B1
		 */
		onInit: function () {
			var vDate = new Date();
			this.oldValue = 0;
			// 			Processed Records
			this._setDateProperty("gstr3bDate", vDate, vDate, null);
			this._setDateProperty("linkDate", vDate, vDate, null);

			this._setDateProperty("id_SetOFfTaxPeriod", vDate, vDate, null);
			this._setDateProperty("id_SetOFfTaxPeriod1", vDate, vDate, null);

			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);

			this._setDateProperty("idPFromtaxPeriod", vDate, new Date(), null);
			this._setDateProperty("idPTotaxPeriod", vDate, new Date(), vDate);

			this._setDateProperty("idSFromtaxPeriod", vDate, new Date(), null);
			this._setDateProperty("idSTotaxPeriod", vDate, new Date(), vDate);

			this._oDialogCopy = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.CopyData", this);
			this.getView().addDependent(this._oDialogCopy);
			this.selVarArr = [];

			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "AllOtherITC");
			var oData = {
				"saveToGstin": false,
				"icon": false,
				"validate4D1": false
			};
			this.getView().setModel(new JSONModel(oData), "display");
			this.validate4D1 = false
			this.getOwnerComponent().getRouter().getRoute("GSTR3B").attachPatternMatched(this._onRouteMatched, this);
		},

		onAfterRendering: function () {
			var Key = this.getView().getModel("ReturnKey").getProperty("/key");
			this._bindDefaultFilter();
			this._onRouteMatched(Key);
		},

		_bindDefaultFilter: function () {
			var vDate = new Date();
			this.byId("GSTINEntityID").setSelectedKeys();
			this.byId("gstr3bDate").setDateValue(vDate);

			this.byId("id_SetOffGstin").setSelectedKeys();
			this.byId("id_SetOFfTaxPeriod").setDateValue(vDate);

			this.byId("idPGstin").setSelectedKeys();
			this._setDateProperty("idPFromtaxPeriod", vDate, new Date(), null);
			this._setDateProperty("idPTotaxPeriod", vDate, new Date(), vDate);
		},

		_onRouteMatched: function (key) {
			if (key === "RS") {
				this.byId("gstr3bDate").setDateValue($.sap.Date || new Date());
			} else {
				this.byId("gstr3bDate").setDateValue(new Date());
			}
			this.byId("dpGstr3b").setVisible(true);
			this.byId("dpGstr3bSummary").setVisible(false);
			this.byId("iditbgstrThreeB").setSelectedKey("GSRT3BSummary");
			this._bindDefaultData();
			this._bindReqIdFilter();
		},

		_bindReqIdFilter: function () {
			var date = new Date();
			this.byId("idRequestIDwisePage3B").setModel(new JSONModel({
				"frTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"status": ""
			}), "FilterModel");
		},

		_bindDefaultData: function () {
			var oPayload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstins": [],
					"profitCentres": [],
					"plants": [],
					"divisions": [],
					"locations": [],
					"salesOrgs": [],
					"distributionChannels": [],
					"userAccess1": [],
					"userAccess2": [],
					"userAccess3": [],
					"userAccess4": [],
					"userAccess5": [],
					"userAccess6": []
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getAllFy(),
					this._getUserId(),
					this._getGstr3bOnboardingData(),
					this._getGstr3bTabData(oPayload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getAllFy: function () {
			return new Promise(function (resolve, reject) {
				$.get("/aspsapapi/getAllFy.do")
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "AllFy");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"screenName": "gstr1Vs3b"
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
						this.byId("idRequestIDwisePage3B").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onSearch3B1: function () {
			var flag = (this.byId("idDateRange") !== undefined),
				aGstin = this.byId("GSTINEntityID").getSelectedKeys(),
				oPayload = {
					"hdr": {
						"pageNum": 0,
						"pageSize": 50
					},
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": this.byId("gstr3bDate").getValue(),
						"gstins": aGstin.includes("All") ? [] : aGstin,
						"profitCentres": (flag ? this.byId("slProfitCtr1").getSelectedKeys() : []),
						"plants": (flag ? this.byId("slPlant1").getSelectedKeys() : []),
						"divisions": (flag ? this.byId("slDivision1").getSelectedKeys() : []),
						"locations": (flag ? this.byId("slLocation1").getSelectedKeys() : []),
						"salesOrgs": (flag ? this.byId("slSalesOrg1").getSelectedKeys() : []),
						"distributionChannels": (flag ? this.byId("slDistrChannel1").getSelectedKeys() : []),
						"userAccess1": (flag ? this.byId("slUserAccess11").getSelectedKeys() : []),
						"userAccess2": (flag ? this.byId("slUserAccess21").getSelectedKeys() : []),
						"userAccess3": (flag ? this.byId("slUserAccess31").getSelectedKeys() : []),
						"userAccess4": (flag ? this.byId("slUserAccess41").getSelectedKeys() : []),
						"userAccess5": (flag ? this.byId("slUserAccess51").getSelectedKeys() : []),
						"userAccess6": (flag ? this.byId("slUserAccess61").getSelectedKeys() : [])
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			this._getGstr3bTabData(oPayload)
				.finally(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressClear3B: function () {
			this.byId("gstr3bDate").setDateValue(new Date());
			this.byId("GSTINEntityID").setSelectedKeys();
			this.onSearch3B1();
		},

		_getGstr3bOnboardingData: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"entityId": $.sap.entityID
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr3bOnboardingData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data), "Gstr3bOnboard");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel({
							resp: ""
						}), "Gstr3bOnboard");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getGstr3bTabData: function (SelectedData) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr3BEntityDashboard.do",
						contentType: "application/json",
						data: JSON.stringify(SelectedData)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data), "GSTR3B");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressCloseIM: function () {
			this.msg.close();
		},

		onPressGstr3bGstin: function (oEvt) {
			if (!this.msg) {
				this.msg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Importentmsg", this);
				this.getView().addDependent(this.msg);
			}
			this.msg.open();
			this.authState = oEvt.getSource().getParent().getParent().getBindingContext("GSTR3B").getObject().auth;
			this.optionSelected = oEvt.getSource().getParent().getParent().getBindingContext("GSTR3B").getObject().optionSelected;
			var vFlagData = {
				"optionSelected": oEvt.getSource().getParent().getParent().getBindingContext("GSTR3B").getObject().optionSelected
			}
			this.getView().setModel(new JSONModel(vFlagData), "visReport");
			this.gstin = oEvt.getSource().getText();
			this.byId("dpGstr3b").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
			//this.byId("linkEntityID").setSelectedKey($.sap.entityID);
			this.byId("linkDate").setValue(this.byId("gstr3bDate").getValue());
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstin": this.gstin
				}
			};
			this.linkTab1(Request);
			this.byId("linkGSTID").setSelectedKey(this.gstin);
			this.buttonVis();
		},

		buttonVis: function () {
			var req = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": this.byId("linkGSTID").getSelectedKey(),
					"returnType": "GSTR3B"
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstinIsFiled.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "buttonVis");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		buttonVis1: function () {
			var req = {
				"req": {
					"taxPeriod": this.byId("id_SetOFfTaxPeriod1").getValue(),
					"gstin": this.byId("id_SetOffGstin1").getSelectedKey(),
					"returnType": "GSTR3B"
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstinIsFiled.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.dataEditable = true;
					this.getView().setModel(new JSONModel(data.resp), "buttonVis1");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onClearFilter: function () {
			this.byId("linkDate").setValue(this.byId("gstr3bDate").getValue());
			this.byId("linkGSTID").setSelectedKey(this.gstin);
			this.onSearch();
		},

		onSearch: function (oEvt) {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			this.linkTab1(Request);
			this.buttonVis();
		},

		onPressBackGstr3b: function () {
			if (this.gstr3bSummary === "changed" && this.saveChangesclick !== "Saved") {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						this.saveChangesclick = this.gstr3bSummary = "";
						if (sAction === "YES") {
							this.onPressSaveChanges12();
							if (this.fromBack === "succes") {
								this._getGstr3bSummaryData();
							}
						} else {
							this._getGstr3bSummaryData();
						}
					}.bind(this)
				});
			} else {
				this._getGstr3bSummaryData();
			}
		},

		_getGstr3bSummaryData: function () {
			this.byId("dpGstr3b").setVisible(true);
			this.byId("dpGstr3bSummary").setVisible(false);
			this.onSearch3B1();
		},

		linkTab1: function (Request) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr3BgstnDashboard.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "timeStamp");
					var aField = [
							"computedIgst", "computedCgst", "computedSgst", "computdCess",
							"userInputTaxableVal", "userInputIgst", "userInputCgst", "userInputSgst", "userInputCess",
							"gstinTaxableVal", "gstinIgst", "gstinCgst", "gstinSgst", "gstinCess",
							"autoCalTaxableVal", "autoCalIgst", "autoCalCgst", "autoCalSgst", "autoCalCess"
						],
						retArr = [],
						curL1Obj = {}, // the current level1 object.
						curL2Obj = {}; // the current level2 object.

					data.resp.gstr3bGstinDashboard.forEach(function (e) {
						if (e.level === "L1") {
							curL1Obj = e;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (e.level === "L2") {
							curL2Obj = e;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (e.level === "L3") {
							curL2Obj.level3.push(e);
						}
					});
					this.finalArray = data.resp.gstr3bGstinDashboard;
					retArr.forEach(function (l1) {
						var gstnTaxableValNumber = 0,
							gstnSgstNumber = 0,
							gstnIgstNumber = 0,
							gstnCgstNumber = 0,
							gstnCessNumber = 0,
							autoCalTaxableValNumber = 0,
							autoCalIgstNumber = 0,
							autoCalCgstNumber = 0,
							autoCalSgstNumber = 0,
							autoCalCessNumber = 0;
						l1.header = l1.igst = l1.cgst = l1.sgst = l1.cess = true;
						l1.edit = false;

						if (["TORCIS", "E_ITC", "IALF", "DOSN"].includes(l1.supplyType)) {
							l1.link = false;
						} else if (["ISS", "ENANGST", "PPLIA"].includes(l1.supplyType)) {
							l1.link = true;
						}

						l1.computedTaxableVal = 0;
						aField.forEach(function (f) {
							l1[f] = 0;
						});
						l1.level2.forEach(function (l2) {
							if (l2.table === "4(c)") {
								l2.userInputSgst = l2.userInputSgst.toFixed(2);
								l2.userInputIgst = l2.userInputIgst.toFixed(2);
								l2.userInputCgst = l2.userInputCgst.toFixed(2);
								l2.userInputCess = l2.userInputCess.toFixed(2);
							}
							if (l2.level3.length) {
								l2.header = l2.igst = l2.cgst = l2.sgst = l2.cess = true;
								l2.link = false;

								l2.computedTaxableVal = 0;
								aField.forEach(function (f) {
									l2[f] = 0;
								});
								l2.level3.forEach(function (l3) {
									if (l1.supplyType !== "E_ITC") {
										l2.computedTaxableVal += l3.computedTaxableVal;
									} else {
										l3.computedTaxableVal = "";
									}
									l3.header = l3.igst = l3.cgst = l3.sgst = l3.cess = l3.link = l3.edit = false;

									aField.forEach(function (f) {
										l2[f] += l3[f];
									});

									l3.diffenceTaxableVal = +l3.userInputTaxableVal - +l3.gstinTaxableVal;
									l3.diffenceIGSTVal = +l3.userInputIgst - +l3.gstinIgst;
									l3.diffenceCGSTVal = +l3.userInputCgst - +l3.gstinCgst;
									l3.diffenceSGSTVal = +l3.userInputSgst - +l3.gstinSgst;
									l3.diffenceCESSVal = +l3.userInputCess - +l3.gstinCess;

									l2.diffenceTaxableVal = +l2.userInputTaxableVal - +l2.gstinTaxableVal;
									l2.diffenceIGSTVal = +l2.userInputIgst - +l2.gstinIgst;
									l2.diffenceCGSTVal = +l2.userInputCgst - +l2.gstinCgst;
									l2.diffenceSGSTVal = +l2.userInputSgst - +l2.gstinSgst;
									l2.diffenceCESSVal = +l2.userInputCess - +l2.gstinCess;

									if (["4(a)(1)", "4(a)(2)"].includes(l3.table)) {
										l3.header = l3.cgst = l3.sgst = true;
										l3.userInputTaxableVal = l3.userInputCgst = l3.userInputSgst = l3.autoCalTaxableVal = "";
									}
									if (["4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(1)", "4(b)(2)", "4(d)(1)", "4(d)(2)"].includes(l3.table)) {
										l3.header = true;
										l3.userInputTaxableVal = l3.autoCalTaxableVal = "";
									}
									if (["4(a)(1)", "4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(2)", "4(b)(1)"].includes(l3.table)) {
										l3.link = l3.igst = l3.cgst = l3.sgst = l3.cess = true;
									}
									if (l3.table === "4(d)(1)") {
										var oModel = this.getView().getModel("display");
										if (!!l3.userInputIgst || !!l3.userInputCgst || !!l3.userInputSgst || !!l3.userInputCess) {
											oModel.setProperty("/validate4D1", true);
											oModel.setProperty("/saveToGstin", this.validate4D1);
										} else {
											oModel.setProperty("/validate4D1", false);
											oModel.setProperty("/saveToGstin", false);
										}
										oModel.refresh(true);
									}
								}.bind(this));
								if (l1.supplyType !== "E_ITC") {
									l1.computedTaxableVal += l2.computedTaxableVal;
								} else {
									l1.computedTaxableVal = l2.computedTaxableVal = "";
								}

								if (l1.supplyType !== "E_ITC") {
									l2.userInputSgst = l2.userInputSgst || 0;
									l2.userInputIgst = l2.userInputIgst || 0;
									l2.userInputCgst = l2.userInputCgst || 0;
									l2.userInputCess = l2.userInputCess || 0;

									l1.computedIgst += l2.computedIgst;
									l1.computedCgst += l2.computedCgst;
									l1.computedSgst += l2.computedSgst;
									l1.computdCess += l2.computdCess;

									l1.userInputTaxableVal += l2.userInputTaxableVal;
									l1.userInputIgst += l2.userInputIgst;
									l1.userInputCgst += l2.userInputCgst;
									l1.userInputSgst += l2.userInputSgst;
									l1.userInputCess += l2.userInputCess;

									l1.diffenceTaxableVal = +l1.userInputTaxableVal - +l1.gstinTaxableVal;
									l1.diffenceIGSTVal = +l1.userInputIgst - +l1.gstinIgst;
									l1.diffenceCGSTVal = +l1.userInputCgst - +l1.gstinCgst;
									l1.diffenceSGSTVal = +l1.userInputSgst - +l1.gstinSgst;
									l1.diffenceCESSVal = +l1.userInputCess - +l1.gstinCess;
								}
								l1.gstinTaxableVal += l2.gstinTaxableVal;
								l1.gstinIgst += l2.gstinIgst;
								l1.gstinCgst += l2.gstinCgst;
								l1.gstinSgst += l2.gstinSgst;
								l1.gstinCess += l2.gstinCess;

								l1.autoCalTaxableVal += l2.autoCalTaxableVal;
								l1.autoCalIgst += l2.autoCalIgst;
								l1.autoCalCgst += l2.autoCalCgst;
								l1.autoCalSgst += l2.autoCalSgst;
								l1.autoCalCess += l2.autoCalCess;

								if (l1.table === "4" || ["4(a)(1)", "4(c)"].includes(l2.table)) {
									l1.userInputTaxableVal = l1.autoCalTaxableVal = "";
									l2.userInputTaxableVal = l2.autoCalTaxableVal = "";
								}
							} else {
								l2.header = l2.igst = l2.edit = l2.link = false;
								if (l1.supplyType !== "E_ITC") {
									l1.computedTaxableVal += l2.computedTaxableVal;
								} else {
									l1.computedTaxableVal = l2.computedTaxableVal = "";
								}

								l1.computedIgst += l2.computedIgst;
								l1.computedCgst += l2.computedCgst;
								l1.computedSgst += l2.computedSgst;
								l1.computdCess += l2.computdCess;

								l2.userInputTaxableVal = +l2.userInputTaxableVal || 0;
								l1.userInputTaxableVal = (+l1.userInputTaxableVal + l2.userInputTaxableVal).toFixed(2);

								if (l2.table !== "4(c)") {
									l2.userInputIgst = l2.userInputIgst || 0;
									l2.userInputCgst = l2.userInputCgst || 0;
									l2.userInputSgst = l2.userInputSgst || 0;
									l2.userInputCess = l2.userInputCess || 0;

									l1.userInputIgst = (+l1.userInputIgst + l2.userInputIgst).toFixed(2);
									l1.userInputCgst = (+l1.userInputCgst + l2.userInputCgst).toFixed(2);
									l1.userInputSgst = (+l1.userInputSgst + l2.userInputSgst).toFixed(2);
									l1.userInputCess = (+l1.userInputCess + l2.userInputCess).toFixed(2);
								}

								if (l1.supplyType !== "E_ITC") {
									l1.gstinTaxableVal += l2.gstinTaxableVal;
									l1.gstinIgst += l2.gstinIgst;
									l1.gstinCgst += l2.gstinCgst;
									l1.gstinSgst += l2.gstinSgst;
									l1.gstinCess += l2.gstinCess;

									l1.autoCalTaxableVal += l2.autoCalTaxableVal;
									l1.autoCalIgst += l2.autoCalIgst;
									l1.autoCalCgst += l2.autoCalCgst;
									l1.autoCalSgst += l2.autoCalSgst;
									l1.autoCalCess += l2.autoCalCess;
								}

								if (l1.supplyType === "ISS") {
									l2.cgst = l2.sgst = l2.cess = true;
									l1.userInputCgst = l1.userInputSgst = l1.userInputCess = "";
									l2.userInputCgst = l2.userInputSgst = l2.userInputCess = "";
								} else {
									l2.cgst = l2.sgst = l2.cess = false;
								}

								if (l1.supplyType === "ENANGST") {
									//l2.edit = false;
								}

								if (l2.table === "3.1(b)") {
									l2.cgst = l2.sgst = true;
									l2.userInputCgst = l2.userInputSgst = "";
								}

								if (["3.1(c)", "3.1(e)", "3.1.1(b)"].includes(l2.table)) {
									l2.cgst = l2.sgst = l2.cess = l2.igst = true;
									l2.userInputIgst = l2.userInputCgst = l2.userInputSgst = l2.userInputCess = "";
								}

								if (["5(a)", "5(b)"].includes(l2.table)) {
									l2.cgst = l2.sgst = l2.cess = l2.igst = true;
									l2.userInputIgst = l2.userInputCgst = l2.userInputSgst = l2.userInputCess = "";
									l1.userInputIgst = l1.userInputCgst = l1.userInputSgst = l1.userInputCess = "";
								}

								if (["5.1(a)", "5.1(b)"].includes(l2.table)) {
									l2.header = true;
									l2.userInputTaxableVal = l1.userInputTaxableVal = "";
								}

								if (l2.supplyType === "NET_ITC_AVAIL") {
									l2.header = l2.igst = l2.cgst = l2.sgst = l2.cess = true;
								}

								if (l2.table === "4(c)") {
									l2.userInputTaxableVal = l2.autoCalTaxableVal = "";
								}

								if (l1.supplyType !== "E_ITC") {
									l1.diffenceTaxableVal = +l1.userInputTaxableVal - +l1.gstinTaxableVal;
									l1.diffenceIGSTVal = +l1.userInputIgst - +l1.gstinIgst;
									l1.diffenceCGSTVal = +l1.userInputCgst - +l1.gstinCgst;
									l1.diffenceSGSTVal = +l1.userInputSgst - +l1.gstinSgst;
									l1.diffenceCESSVal = +l1.userInputCess - +l1.gstinCess;
								}

								l2.diffenceTaxableVal = +l2.userInputTaxableVal - +l2.gstinTaxableVal;
								l2.diffenceIGSTVal = +l2.userInputIgst - +l2.gstinIgst;
								l2.diffenceCGSTVal = +l2.userInputCgst - +l2.gstinCgst;
								l2.diffenceSGSTVal = +l2.userInputSgst - +l2.gstinSgst;
								l2.diffenceCESSVal = +l2.userInputCess - +l2.gstinCess;
							}
						}.bind(this));
					}.bind(this));
					this._calculateTable4(retArr); // Added for Table 4 and 4(c) calculation on 23.08.2023
					this.getView().setModel(new JSONModel(retArr), "LinkTabGSTN1");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_calculateTable4: function (data) {
			var itc4 = data.find(function (e) {
				return e.table === "4";
			});
			if (itc4) {
				var itc4A = itc4.level2.find(function (e) {
						return e.table === "4(a)";
					}),
					itc4B = itc4.level2.find(function (e) {
						return e.table === "4(b)";
					}),
					itc4C = itc4.level2.find(function (e) {
						return e.table === "4(c)";
					});

				// Table 4 and 4(c) Calculation
				itc4.autoCalCess = itc4C.autoCalCess = (+itc4A.autoCalCess - itc4B.autoCalCess).toFixed(2);
				itc4.autoCalCgst = itc4C.autoCalCgst = (+itc4A.autoCalCgst - itc4B.autoCalCgst).toFixed(2);
				itc4.autoCalIgst = itc4C.autoCalIgst = (+itc4A.autoCalIgst - itc4B.autoCalIgst).toFixed(2);
				itc4.autoCalSgst = itc4C.autoCalSgst = (+itc4A.autoCalSgst - itc4B.autoCalSgst).toFixed(2);

				itc4.computdCess = itc4C.computdCess = (+itc4A.computdCess - itc4B.computdCess).toFixed(2);
				itc4.computedCgst = itc4C.computedCgst = (+itc4A.computedCgst - itc4B.computedCgst).toFixed(2);
				itc4.computedIgst = itc4C.computedIgst = (+itc4A.computedIgst - itc4B.computedIgst).toFixed(2);
				itc4.computedSgst = itc4C.computedSgst = (+itc4A.computedSgst - itc4B.computedSgst).toFixed(2);

				itc4.userInputSgst = itc4C.userInputSgst = (+itc4A.userInputSgst - +itc4B.userInputSgst).toFixed(2);
				itc4.userInputIgst = itc4C.userInputIgst = (+itc4A.userInputIgst - +itc4B.userInputIgst).toFixed(2);
				itc4.userInputCgst = itc4C.userInputCgst = (+itc4A.userInputCgst - +itc4B.userInputCgst).toFixed(2);
				itc4.userInputCess = itc4C.userInputCess = (+itc4A.userInputCess - +itc4B.userInputCess).toFixed(2);

				itc4.gstinCess = itc4C.gstinCess = (+itc4A.gstinCess - +itc4B.gstinCess).toFixed(2);
				itc4.gstinCgst = itc4C.gstinCgst = (+itc4A.gstinCgst - +itc4B.gstinCgst).toFixed(2);
				itc4.gstinIgst = itc4C.gstinIgst = (+itc4A.gstinIgst - +itc4B.gstinIgst).toFixed(2);
				itc4.gstinSgst = itc4C.gstinSgst = (+itc4A.gstinSgst - +itc4B.gstinSgst).toFixed(2);
				itc4.gstinTaxableVal = itc4C.gstinTaxableVal = (+itc4A.gstinTaxableVal - +itc4B.gstinTaxableVal).toFixed(2);

				itc4C.diffenceIGSTVal = +itc4C.userInputIgst - +itc4C.gstinIgst;
				itc4C.diffenceCGSTVal = +itc4C.userInputCgst - +itc4C.gstinCgst;
				itc4C.diffenceSGSTVal = +itc4C.userInputSgst - +itc4C.gstinSgst;
				itc4C.diffenceCESSVal = +itc4C.userInputCess - +itc4C.gstinCess;

				itc4.diffenceIGSTVal = +itc4.userInputIgst - +itc4.gstinIgst;
				itc4.diffenceCGSTVal = +itc4.userInputCgst - +itc4.gstinCgst;
				itc4.diffenceSGSTVal = +itc4.userInputSgst - +itc4.gstinSgst;
				itc4.diffenceCESSVal = +itc4.userInputCess - +itc4.gstinCess;
			}
		},

		onPressSupplyType: function (oEvt) {
			this.text = oEvt.getSource().getText();
			this.onPressSupplyType1();
		},

		onPressSupplyType1: function () {
			var text = this.text;
			if (text === "Inter-State Supplies") {
				this.byId("dpGstr3bAddItem").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressInterStateSupplies();
			} else if (text === "ITC Avalilabe (Whether in full or part)") {
				this.byId("dpGstr3bAddItemITCAV").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
			} else if (text === "ITC Reversed") {
				this.byId("dpGstr3bAddItemITCR").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
			} else if (text === "Ineligible ITC") {
				this.byId("dpGstr3bAddItemINITC").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
			} else if (text === "Exempt, Nil & Non-GST Inward Supplies") {
				this.byId("dpNilNonExempt").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressExemptNill();
			} else if (text === "Interest & Late Fee Payable") {
				this.byId("dpGstr3bAddItemInterst").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
			} else if (text === "All other ITC") {
				this.byId("dpAllOtherITC").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressAllothersITC();
			} else if (text === "Others") {
				this.byId("Other").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				//this.byId("dpAllOtherITC").setVisible(false);
				this.onPressothersReversal();
			} else if (text === "Past period liability") {
				this.byId("dpGstr3bPastPeriod").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressPastLia();
			} else if (text === "As per rules 38, 42 and 43 of CGST Rules and Section 17(5)") {
				this.byId("dp4243").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPress4243();
			} else if (text === "Inward Supplies liable to Reverse Charge (Other than 1 & 2 above)") {
				this.byId("dpInwardSupplies").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressSubSectionName("4.a.3");
			} else if (text === "Import of Goods") {
				this.byId("dpImportofGoods").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressSubSectionName("4.a.1");
			} else if (text === "Inward Supplies from ISD") {
				this.byId("dpISDInwardSupplies").setVisible(true);
				this.byId("dpGstr3bSummary").setVisible(false);
				this.onPressSubSectionName("4.a.4");
			}
		},

		onPressSubSectionName: function (subSec) {
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				var gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"subSection": subSec
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSubsectionDetails4Section.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.edited = false;
					if (subSec === "4.a.1") {
						this.onPressImportofGoods(data);
					} else if (subSec === "4.a.3") {
						this.onPressInwardSupplies(data);
					} else if (subSec === "4.a.4") {
						this.editedISD = false;
						this.onPressISDInwardSupplies(data);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressImportofGoods: function (data) {
			var retArr = [],
				curL1Obj = {},
				curL2Obj = {};

			data.resp.forEach(function (e) {
				if (["G2BCTax_b", "ITCRTP"].includes(e.subSectionName)) {
					e.icon = true;
				}
				if ((this.optionSelected === "B" && ["G2BCTax_a", "G2BCTax_c"].includes(e.subSectionName)) ||
					(["A", "C"].includes(this.optionSelected) && ["G2BCTax_b", "ITCRTP"].includes(e.subSectionName)) || ["G2BCTax"].includes(e.subSectionName)
				) {
					e.edit = false;
				} else {
					e.edit = true;
				}
				e.Check = !["G2BCTax", "ITCRTP"].includes(e.subSectionName);
				switch (e.level) {
				case "L1":
					curL1Obj = e;
					retArr.push(curL1Obj);
					curL1Obj.level2 = [];
					break;
				case "L2":
					curL2Obj = e;
					curL1Obj.level2.push(curL2Obj);
					curL2Obj.level3 = [];
					break;
				case "L3":
					curL2Obj.level3.push(e);
					break;
				}
				if (e.Check && e.radioFlag) {
					curL1Obj.userIgst = e.userIgst;
					curL1Obj.userCgst = e.userCgst;
					curL1Obj.userSgst = e.userSgst;
					curL1Obj.userCess = e.userCess;
				}
			}.bind(this))
			this.getView().setModel(new JSONModel(retArr), "ImportofGoods");
		},

		onPressInwardSupplies: function (data) {
			var aField = [
					"autoCompCess", "autoCompCgst", "autoCompIgst", "autoCompSgst", "autoCompTaxableVal", "digiCess", "digiCgst",
					"digiIgst", "digiSgst", "digiTaxableVal", "userCess", "userCgst", "userIgst", "userSgst", "userTaxableVal"
				],
				retArr = [],
				curL1Obj = {}, // the current level1 object
				curL2Obj = {}; // the current level2 object

			data.resp.forEach(function (e) {
				if (["APG3B_B", "RRTP"].includes(e.subSectionName)) {
					e.icon = true;
				}
				if ((["A", "C"].includes(this.optionSelected) && ["APG3B_B", "RRTP"].includes(e.subSectionName)) ||
					(this.optionSelected === "B" && ["APG3B_A", "APG3B_C"].includes(e.subSectionName)) || ["ISLRP", "APG3B"].includes(e.subSectionName)
				) {
					e.edit = false;
				} else {
					e.edit = true;
				}
				e.Check = !["ISLRP", "APG3B", "RRTP", "ISLURP"].includes(e.subSectionName);
				switch (e.level) {
				case "L1":
					curL1Obj = e;
					retArr.push(curL1Obj);
					curL1Obj.level2 = [];
					break;
				case "L2":
					curL2Obj = e;
					curL1Obj.level2.push(curL2Obj);
					curL2Obj.level3 = [];
					break;
				case "L3":
					curL2Obj.level3.push(e);
					break;
				}
				// if (!e.Check && e.level === "L2") {
				// 	curL1Obj.userIgst = (+curL1Obj.userIgst + e.userIgst);
				// 	curL1Obj.userCgst = (+curL1Obj.userCgst + e.userCgst);
				// 	curL1Obj.userSgst = (+curL1Obj.userSgst + e.userSgst);
				// 	curL1Obj.userCess = (+curL1Obj.userCess + e.userCess);
				// }
			}.bind(this));
			retArr.forEach(function (l1) {
				if (l1.level2.length) {
					aField.forEach(function (f) {
						l1[f] = 0;
					});
				}
				l1.level2.forEach(function (l2) {
					if (l2.level3.length) {
						aField.forEach(function (f) {
							l2[f] = 0;
						});
						l2.level3.forEach(function (l3) {
							if (l3.radioFlag) {
								aField.forEach(function (f) {
									l2[f] += l3[f];
									l1[f] += l3[f];
								});
							}
						});
					} else {
						aField.forEach(function (f) {
							l1[f] += l2[f];
						});
					}
				})
			});
			this.getView().setModel(new JSONModel(retArr), "InwardSupplies");
		},

		onPressISDInwardSupplies: function (data) {
			var retArr = [],
				curL1Obj = {}, // the current level1 object.
				curL2Obj = {}, // the current level2 object.
				flag = false;

			data.resp.forEach(function (e) {
				flag = (flag || e.radioFlag);
				e.Check = ["APPPR", "APG2BARU", "DITCG6", "DITCG6_4"].includes(e.subSectionName);
				e.icon = ["APG2B", "ITCR"].includes(e.subSectionName);

				if (["APG2B", "ITCR"].includes(e.subSectionName)) {
					e.radioFlag = false;
				}
				if ((["A", "C"].includes(this.optionSelected) && ["APG2BARU", "APG2B", "ITCR"].includes(e.subSectionName)) ||
					(this.optionSelected === "B" && ["APPPR", "DITCG6_4"].includes(e.subSectionName))
				) {
					e.edit = false;
				} else {
					e.edit = true;
				}
				e.editInput = (e.subSectionName !== "APG2BARU");

				switch (e.level) {
				case "L1":
					curL1Obj = e;
					retArr.push(curL1Obj);
					curL1Obj.level2 = [];
					break;
				case "L2":
					curL2Obj = e;
					curL1Obj.level2.push(curL2Obj);
					curL2Obj.level3 = [];
					break;
				case "L3":
					curL2Obj.level3.push(e);
					break;
				}
				if (!e.Check && e.level === "L2") {
					curL1Obj.userIgst = (+curL1Obj.userIgst + e.userIgst);
					curL1Obj.userCgst = (+curL1Obj.userCgst + e.userCgst);
					curL1Obj.userSgst = (+curL1Obj.userSgst + e.userSgst);
					curL1Obj.userCess = (+curL1Obj.userCess + e.userCess);
				}
			}.bind(this));
			if (!flag) {
				retArr[0].radioFlag = true;
			}
			this.getView().setModel(new JSONModel(retArr), "ISDInwardSupplies");
		},

		onPress4243: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"entityId": $.sap.entityID,
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/get3bRule38OtherReverals.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					for (var i = 0; i < data.resp.length; i++) {
						var ele = data.resp[i];
						if (data.resp[i].subSectionName === "total_4b1") {
							data.resp[i].edit = false;
						} else if (data.resp[i].subSectionName === "Ineligible_ITC_175") {
							data.resp[i].edit = false;
						} else {
							data.resp[i].edit = this.getView().getModel("buttonVis").getProperty("/dataEditable");
						}
						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							curL2Obj.level3.push(ele);
						}
					}

					var aData = [];
					this.edited3842 = false;
					var vTigstUser = 0;
					var vTcgstUser = 0;
					var vTsgstUser = 0;
					var vTcesstUser = 0;
					var vTigstUser2 = 0;
					var vTcgstUser2 = 0;
					var vTsgstUser2 = 0;
					var vTcesstUser2 = 0;
					var vTigstUserasp = 0;
					var vTcgstUserasp = 0;
					var vTsgstUserasp = 0;
					var vTcesstUserasp = 0;
					var vTigstUserasp2 = 0;
					var vTcgstUserasp2 = 0;
					var vTsgstUserasp2 = 0;
					var vTcesstUserasp2 = 0;

					for (var i = 1; i < retArr.length; i++) {
						vTigstUser = Number(retArr[1].igstUser);
						vTcgstUser = Number(retArr[1].cgstUser);
						vTsgstUser = Number(retArr[1].sgstUser);
						vTcesstUser = Number(retArr[1].cessUser);

						vTigstUserasp = Number(retArr[1].igstAsp);
						vTcgstUserasp = Number(retArr[1].cgstAsp);
						vTsgstUserasp = Number(retArr[1].sgstAsp);
						vTcesstUserasp = Number(retArr[1].cessAsp);

						if (retArr[i].level2.length !== 0) {
							for (var j = 0; j < retArr[i].level2.length; j++) {
								vTigstUser2 += Number(retArr[i].level2[j].igstUser);
								vTcgstUser2 += Number(retArr[i].level2[j].cgstUser);
								vTsgstUser2 += Number(retArr[i].level2[j].sgstUser);
								vTcesstUser2 += Number(retArr[i].level2[j].cessUser);

								vTigstUserasp2 += Number(retArr[i].level2[j].igstAsp);
								vTcgstUserasp2 += Number(retArr[i].level2[j].cgstAsp);
								vTsgstUserasp2 += Number(retArr[i].level2[j].sgstAsp);
								vTcesstUserasp2 += Number(retArr[i].level2[j].cessAsp);
							}
						}
					}
					retArr[0].igstUser = (Number(vTigstUser) + Number(vTigstUser2)).toFixed(2);
					retArr[0].cgstUser = (Number(vTcgstUser) + Number(vTcgstUser2)).toFixed(2);
					retArr[0].sgstUser = (Number(vTsgstUser) + Number(vTsgstUser2)).toFixed(2);
					retArr[0].cessUser = (Number(vTcesstUser) + Number(vTcesstUser2)).toFixed(2);

					retArr[0].igstAsp = (Number(vTigstUserasp) + Number(vTigstUserasp2)).toFixed(2);
					retArr[0].cgstAsp = (Number(vTcgstUserasp) + Number(vTcgstUserasp2)).toFixed(2);
					retArr[0].sgstAsp = (Number(vTsgstUserasp) + Number(vTsgstUserasp2)).toFixed(2);
					retArr[0].cessAsp = (Number(vTcesstUserasp) + Number(vTcesstUserasp2)).toFixed(2);

					retArr[2].igstUser = Number(vTigstUser2).toFixed(2);
					retArr[2].cgstUser = Number(vTcgstUser2).toFixed(2);
					retArr[2].sgstUser = Number(vTsgstUser2).toFixed(2);
					retArr[2].cessUser = Number(vTcesstUser2).toFixed(2);

					retArr[2].igstAsp = Number(vTigstUserasp2).toFixed(2);
					retArr[2].cgstAsp = Number(vTcgstUserasp2).toFixed(2);
					retArr[2].sgstAsp = Number(vTsgstUserasp2).toFixed(2);
					retArr[2].cessAsp = Number(vTcesstUserasp2).toFixed(2);

					this.getView().setModel(new JSONModel(retArr), "Rule3842");
					this.getView().getModel("Rule3842").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onBack3842: function () {
			if (this.edited3842) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onPressSaveRecords3842();
						} else {
							this.byId("dp4243").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dp4243").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onPressSaveRecords3842: function () {
			var oView = this.getView();
			var TabData = oView.getModel("Rule3842").getProperty("/");
			for (var j = 0; j < TabData.length; j++) {
				//////userInputTaxableVal/////
				var val = TabData[j].igstUser.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}

				////////////////////////////////
				var valuserInputIgst = TabData[j].cgstUser.toString();
				var val1userInputIgst = valuserInputIgst.split(".");
				if (val1userInputIgst[1] !== undefined) {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputIgst[1] !== "" && val1userInputIgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg1 = /^[0-9]*\.?[0-9]*$/;
				if (!valuserInputIgst.match(reg1)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				////////////////////////////////
				var userInputCgst = TabData[j].sgstUser.toString();
				var val1userInputCgst = userInputCgst.split(".");
				if (val1userInputCgst[1] !== undefined) {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputCgst[1] !== "" && val1userInputCgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				// if (TabData[i].level2[j].supplyType !== "NET_ITC_AVAIL") {
				var reg2 = /^[0-9]*\.?[0-9]*$/;
				if (!userInputCgst.match(reg2)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				// }
				////////////////////////////////
				var userInputSgst = TabData[j].cessUser.toString();
				var userInputSgst1 = userInputSgst.split(".");
				if (userInputSgst1[1] !== undefined) {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (userInputSgst1[1] !== "" && userInputSgst1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				// if (TabData[i].level2[j].supplyType !== "NET_ITC_AVAIL") {
				var reg3 = /^[0-9]*\.?[0-9]*$/;
				if (!userInputSgst.match(reg3)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				// }

				if (Number(TabData[j].cgstUser) !== Number(TabData[j].sgstUser)) {
					MessageBox.error("CGST and SGST cannot be different for " + TabData[j].Header);
					return;
				}
			}

			if (oView.getModel("Rule3842").getData().length != 0) {
				var gstin;
				// if (oAction === "YES") {
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					gstin = this.gstin;
				} else {
					gstin = this.byId("linkGSTID").getSelectedKey();
				}
				var InputList = [];
				var ComputeList = [];
				oView.getModel("Rule3842").getData().forEach(function (obj) {
					InputList.push({
						"cess": Number(obj.cessUser).toFixed(2),
						"cgst": Number(obj.cgstUser).toFixed(2),
						"igst": Number(obj.igstUser).toFixed(2),
						"sectionName": obj.sectionName,
						"sgst": Number(obj.sgstUser).toFixed(2),
						"subSectionName": obj.subSectionName,
						"taxableVal": "0",
					});
					if (obj.level2.length !== 0) {
						obj.level2.forEach(function (sobj) {
							InputList.push({
								"cess": Number(sobj.cessUser).toFixed(2),
								"cgst": Number(sobj.cgstUser).toFixed(2),
								"igst": Number(sobj.igstUser).toFixed(2),
								"sectionName": sobj.sectionName,
								"sgst": Number(sobj.sgstUser).toFixed(2),
								"subSectionName": sobj.subSectionName,
								"taxableVal": "0",
							});
						});
					}
				}.bind(this));
				var request = {
					"req": {
						"taxPeriod": this.byId("linkDate").getValue(),
						"gstin": gstin,
						//"status": "SAVE",
						"userInputList": InputList
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/save3bRule38OtherReverals.do",
						contentType: "application/json",
						data: JSON.stringify(request)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr && data.hdr.status === "S") {
							MessageBox.success("Changes saved successfully.");
							this.byId("dp4243").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						} else {
							MessageBox.error("Error Occured While Saving the Changes");
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error Occured While Saving the Changes");
					}.bind(this));
			}
		},

		onPressInterStateSupplies: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			this.data0 = [];
			this.data = [];
			this.data1 = [];
			this.getView().setModel(new JSONModel([]), "InterStateSupplies");
			this.getView().setModel(new JSONModel([]), "InterStateSupplies1");
			this.getView().setModel(new JSONModel([]), "InterStateSupplies2");
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getUserInterStateSupplies(payload),
					this._getInterStateSupplies(payload)
				])
				.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this),
					function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
		},

		_getUserInterStateSupplies: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getUserInterStateSupplies.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].sectionName === "3.2(a)") {
								this.data0.push(data.resp[i]);
								this.getView().setModel(new JSONModel(this.data0), "InterStateSupplies");
							} else if (data.resp[i].sectionName === "3.2(b)") {
								this.data.push(data.resp[i]);
								this.getView().setModel(new JSONModel(this.data), "InterStateSupplies1");
							} else if (data.resp[i].sectionName === "3.2(c)") {
								this.data1.push(data.resp[i]);
								this.getView().setModel(new JSONModel(this.data1), "InterStateSupplies2");
							}
						}
						this.vIntStModLength = this.data0.length;
						this.vIntStModLength1 = this.data.length;
						this.vIntStModLength2 = this.data1.length;
						this.placeofSupply();
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getInterStateSupplies: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getInterStateSupplies.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.resp.length) {
							var arr32a = [],
								arr32b = [],
								arr32c = [];
							data.resp.forEach(function (el, i) {
								if (el.sectionName === "3.2(a)") {
									arr32a.push(el);
								} else if (el.sectionName === "3.2(b)") {
									arr32b.push(el);
								} else if (el.sectionName === "3.2(c)") {
									arr32c.push(el);
								}
							});
							this.getView().setModel(new JSONModel(arr32a), "InterStateSuppliesCompute");
							this.getView().setModel(new JSONModel(arr32b), "InterStateSuppliesCompute1");
							this.getView().setModel(new JSONModel(arr32c), "InterStateSuppliesCompute2");
						} else {
							this.getView().setModel(new JSONModel([]), "InterStateSuppliesCompute");
							this.getView().setModel(new JSONModel([]), "InterStateSuppliesCompute1");
							this.getView().setModel(new JSONModel([]), "InterStateSuppliesCompute2");
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressExemptNill: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getExcemptNilNonGstIS.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						data.resp.push({
							cess: 0,
							cgst: 0,
							igst: 0,
							interState: 0,
							intraState: 0,
							sectionName: "5(a)",
							sgst: 0,
							subSectionName: "FSUC",
							taxableVal: 0
						}, {
							cess: 0,
							cgst: 0,
							igst: 0,
							interState: 0,
							intraState: 0,
							sectionName: "5(b)",
							sgst: 0,
							subSectionName: "NGST_SUPPLY",
							taxableVal: 0
						});
					}
					this.getView().setModel(new JSONModel(data), "ExemptNill");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		placeofSupply: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAllState.do",
					contentType: "application/json",
					data: JSON.stringify({})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var data1 = jQuery.extend(true, [], data.resp.states),
						data2 = jQuery.extend(true, [], data.resp.states),
						data3 = jQuery.extend(true, [], data.resp.states);

					this.getView().setModel(new JSONModel(data1), "getAllState");
					this.getView().setModel(new JSONModel(data2), "getAllState1");
					this.getView().setModel(new JSONModel(data3), "getAllState2");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onBackGstr3bSummary: function () {
			if (this.InterStateSuppliesChage === "changed" && this.onInterStateSaveChanges !== "Saved") {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onInterStateSave("back");
						} else {
							this.InterStateSuppliesChage = "";
							this.onInterStateSaveChanges = "";
							this.byId("dpGstr3bAddItem").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearch();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpGstr3bAddItem").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onSearchBack: function (oEvt) {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			this.linkTab1(Request);
		},

		onBackGstr3bITCAv: function () {
			this.byId("dpGstr3bAddItemITCAV").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
		},

		onBackGstr3bITCR: function () {
			this.byId("dpGstr3bAddItemITCR").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
		},

		onBackGstr3bInITC: function () {
			this.byId("dpGstr3bAddItemINITC").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
		},

		onBackGstr3bNil: function () {
			if (this.InterStateSuppliesChage === "changed" && this.onInterStateSaveChanges !== "Saved") {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							var key = "back";
							this.ExemptNillSave(key);
						} else {
							this.InterStateSuppliesChage = "";
							this.onInterStateSaveChanges = "";
							this.byId("dpNilNonExempt").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpNilNonExempt").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onBackGstr3bInterest: function () {
			this.byId("dpGstr3bAddItemInterst").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
		},

		showingCheck: function (oEvt) {
			var data = {
				"eyC": true,
				"copy": true,
				"eyU": true,
				"gstn": true,
				"diff": true
			};
			data.eyC = this.byId("ASPCID").getSelected();
			data.copy = this.byId("GSTNCOMPID").getSelected();
			data.eyU = this.byId("ASPUID").getSelected();
			data.gstn = this.byId("GSTNID").getSelected();
			data.diff = this.byId("DiffID").getSelected();

			this.byId("dpGstr3bSummary").setModel(new JSONModel(data), "visiGSTR3B");
		},

		_negativeAllowedTableList: function () {
			return [
				"3.1(a)", "3.1(b)", "3.1(c)", "3.1(d)", "3.1(e)", "3.2(a)", "3.2(b)", "3.2(c)", "3.1.1(a)", "3.1.1(b)",
				"4(a)(1)", "4(a)(2)", "4(a)(3)", "4(a)(4)", "4(a)(5)", "4(c)", "4(d)(1)", "4(d)(2)"
			];
		},

		onChangeGstr3bSummaryUserInput: function (oEvent, table, taxType) {
			var oContext = oEvent.getSource().getBindingContext("LinkTabGSTN1"),
				TabData = oContext.getModel().getProperty('/'),
				index = oContext.getPath().split('/')[1],
				ChildIndex = oContext.getPath().split('/')[3],
				aTableNeg = this._negativeAllowedTableList(),
				userInputKey = "userInput" + taxType,
				gstinKey = 'gstin' + taxType,
				differenceKey = "diffence" + (taxType === "TaxableVal" ? taxType : (taxType.toUpperCase() + 'Val')),
				obj = TabData[index],
				dataL2 = obj.level2,
				dataL3 = dataL2[ChildIndex].level3,
				total = 0;

			if (aTableNeg.includes(table)) {
				this.anyDecimalValue(oEvent);
			} else {
				this.positiveDecimal(oEvent);
			}

			this.saveChangesclick = "";
			this.gstr3bSummary = "changed";
			if (!dataL3.length) {
				dataL2.forEach(function (e) {
					if (taxType === "TaxableVal" || obj.supplyType !== "E_ITC") {
						total += +e[userInputKey] || 0;
					}
					e[differenceKey] = (+e[userInputKey] || 0) - +e[gstinKey];
				});
				obj[userInputKey] = total.toFixed(2);
				obj[differenceKey] = (+obj[userInputKey] - +obj[gstinKey]);
			} else {
				dataL3.forEach(function (e) {
					total += +e[userInputKey] || 0;
					e[differenceKey] = (+e[userInputKey] || 0) - +e[gstinKey];
				});
				dataL2[ChildIndex][userInputKey] = total.toFixed(2);

				total = 0;
				dataL2.forEach(function (e, i) {
					total += +e[userInputKey] || 0;
					e[differenceKey] = +e[userInputKey] - +e[gstinKey];

					if (i === 0) {
						dataL2[i + 2][userInputKey] = (+e[userInputKey] - +dataL2[i + 1][userInputKey]).toFixed(2);
						dataL2[i + 2][gstinKey] = (+e[gstinKey] - +dataL2[i + 1][gstinKey]).toFixed(2);
						dataL2[i + 2][differenceKey] = (+e[differenceKey] - +dataL2[i + 1][differenceKey]).toFixed(2);

					} else if (i === 1) {
						dataL2[i + 1][userInputKey] = (+dataL2[i - 1][userInputKey] - +e[userInputKey]).toFixed(2);
						dataL2[i + 1][gstinKey] = (+dataL2[i - 1][gstinKey] - +dataL2[i][gstinKey]).toFixed(2);
						dataL2[i + 1][differenceKey] = (+dataL2[i - 1][differenceKey] - +dataL2[i][differenceKey]).toFixed(2);
					}
					if (taxType !== "TaxableVal" && obj.supplyType === "E_ITC") {
						obj[userInputKey] = dataL2[2][userInputKey];
						obj[gstinKey] = dataL2[2][gstinKey];
						obj[differenceKey] = dataL2[2][differenceKey];
					}
				});
				obj[userInputKey] = total.toFixed(2);
				obj[differenceKey] = +obj[userInputKey] - +obj[gstinKey];
			}
			oContext.getModel().refresh(true);
		},

		onPressSaveChanges: function () {
			var oData = this.getView().getModel("LinkTabGSTN1").getProperty("/");
			try {
				oData.forEach(function (item) {
					if (!["ISS", "ENANGST"].includes(item.supplyType)) {
						item.level2.forEach(function (l2) {
							if (["3.1(a)", "3.1(d)", "3.1.1(a)"].includes(l2.table)) { // "5.1(b)"
								if (+l2.userInputCgst !== +l2.userInputSgst) {
									throw new Error("CGST and SGST cannot be different for " + l2.table);
								}
							}
							l2.level3.forEach(function (l3) {
								if (["4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(1)", "4(d)(1)", "4(d)(2)"].includes(l3.table)) {
									if (+l3.userInputCgst !== +l3.userInputSgst) {
										throw new Error("CGST and SGST cannot be different for " + l3.table);
									}
								}
							});
						});
					}
				}.bind(this));
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.saveChangesclick = "Saved";
							this.onPressSaveChanges1();
						}
					}.bind(this)
				});
			} catch (error) {
				MessageBox.error(error.message);
			}
		},

		onPressSaveChanges12: function () {
			var oData = this.getView().getModel("LinkTabGSTN1").getProperty("/");
			try {
				oData.forEach(function (item) {
					if (!["ISS", "ENANGST"].includes(item.supplyType)) {
						item.level2.forEach(function (l2) {
							if (["3.1(a)", "3.1(d)", "3.1.1(a)"].includes(l2.table)) { // "5.1(b)"
								if (+l2.userInputCgst !== +l2.userInputSgst) {
									throw new Error("CGST and SGST cannot be different for " + l2.table);
								}
							}

							l2.level3.forEach(function (l3) {
								if (["4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(1)", "4(d)(1)", "4(d)(2)"].includes(l3.table)) {
									if (+l3.userInputCgst !== +l3.userInputSgst) {
										throw new Error("CGST and SGST cannot be different for " + l3.table);
									}
								}
							});
						});
					}
				}.bind(this));
				this.fromBack = "succes";
				this.onPressSaveChanges1();
			} catch (error) {
				MessageBox.error(error.message);
			}
		},

		onPressSaveChanges1: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getProperty("/");
			var userInputList = [];
			for (var i = 0; i < TabData.length; i++) {
				if (TabData[i].supplyType !== "ISS" && TabData[i].supplyType !== "ENANGST") {
					for (var j = 0; j < TabData[i].level2.length; j++) {
						if (TabData[i].level2[j].table !== "4(a)" && TabData[i].level2[j].table !== "4(b)" && TabData[
								i].level2[j].table !== "4(d)" && TabData[i].level2[j].table !== "3.2(a)" && TabData[i].level2[j].table !== "3.2(b)" &&
							TabData[
								i].level2[j].table !== "3.2(c)") {
							var famPenDet = {
								sectionName: TabData[i].level2[j].table,
								subSectionName: TabData[i].level2[j].supplyType,
								taxableVal: TabData[i].level2[j].userInputTaxableVal === "" ? "0" : TabData[i].level2[j].userInputTaxableVal.toString(),
								igst: TabData[i].level2[j].userInputIgst === "" ? "0" : TabData[i].level2[j].userInputIgst.toString(),
								cgst: TabData[i].level2[j].userInputCgst === "" ? "0" : TabData[i].level2[j].userInputCgst.toString(),
								sgst: TabData[i].level2[j].userInputSgst === "" ? "0" : TabData[i].level2[j].userInputSgst.toString(),
								cess: TabData[i].level2[j].userInputCess === "" ? "0" : TabData[i].level2[j].userInputCess.toString(),
								//pos: "0",
								interState: "0",
								intraState: "0"
							};
							userInputList.push(famPenDet);
						}
						if (TabData[i].level2[j].level3.length > 0) {
							for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
								var famPenDet1 = {
									sectionName: TabData[i].level2[j].level3[k].table,
									subSectionName: TabData[i].level2[j].level3[k].supplyType,
									taxableVal: TabData[i].level2[j].level3[k].userInputTaxableVal === "" ? "0" : TabData[i].level2[j].level3[k].userInputTaxableVal
										.toString(),
									igst: TabData[i].level2[j].level3[k].userInputIgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputIgst.toString(),
									cgst: TabData[i].level2[j].level3[k].userInputCgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputCgst.toString(),
									sgst: TabData[i].level2[j].level3[k].userInputSgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputSgst.toString(),
									cess: TabData[i].level2[j].level3[k].userInputCess === "" ? "0" : TabData[i].level2[j].level3[k].userInputCess.toString(),
									//pos: "0",
									interState: "0",
									intraState: "0"
								};
								userInputList.push(famPenDet1);
							}
						}
					}
				}
			}

			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"status": "SAVE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						MessageBox.success("Changes saved successfully.");
						this.editfun();
						this.onSearch();
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				}.bind(this));
		},

		editfun: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getProperty("/");
			for (var i = 0; i < TabData.length; i++) {
				for (var j = 0; j < TabData[i].level2.length; j++) {
					TabData[i].level2[j].edit = false;
					if (TabData[i].supplyType === "ISS" || TabData[i].supplyType === "ENANGST") {
						TabData[i].level2[j].edit = false;
					}
					if (TabData[i].level2[j].level3.length > 0) {
						for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
							TabData[i].level2[j].level3[k].edit = false;
						}
					}
				}
			}
			this.getView().getModel("LinkTabGSTN1").refresh(true);
		},

		onPressAddRecords: function () {
			this.clearpress = "";
			var tabData = this.getView().getModel("InterStateSupplies").getProperty("/"),
				stateData = this.getView().getModel("getAllState").getProperty("/"),
				stateData1 = $.extend(true, {}, stateData);
			if (tabData.length !== 0) {
				for (var i = 0; i < tabData.length; i++) {
					for (var j = 0; j < stateData1.length; j++) {
						if (tabData[i].pos === stateData1[j].state) {
							stateData1.splice(j, 1);
						}
					}
				}
			}
			this.getView().getModel("getAllState").setData(stateData1);
			var newRec = {
				"pos": "",
				"taxableVal": "",
				"igst": "",
				"sectionName": "3.2(a)",
				"subSectionName": "SMTURP"
			};
			this.data0.push(newRec);
			this.getView().setModel(new JSONModel(this.data0), "InterStateSupplies");
			this.getView().getModel("InterStateSupplies").refresh(true);
		},

		onPressAddRecords1: function () {
			this.clearpress = "";
			var tabData = this.getView().getModel("InterStateSupplies1").getProperty("/");
			var stateData = this.getView().getModel("getAllState1").getProperty("/");
			var stateData1 = $.extend(true, {}, stateData);
			if (tabData.length !== 0) {
				for (var i = 0; i < tabData.length; i++) {
					for (var j = 0; j < stateData1.length; j++) {
						if (tabData[i].pos === stateData1[j].state) {
							stateData1.splice(j, 1);
						}
					}
				}
			}
			this.getView().getModel("getAllState1").setData(stateData1);
			var newRec = {
				"pos": "",
				"taxableVal": "",
				"igst": "",
				"sectionName": "3.2(b)",
				"subSectionName": "SMTCTP"
			};
			this.data.push(newRec);
			this.getView().setModel(new JSONModel(this.data), "InterStateSupplies1");
		},

		onPressAddRecords2: function () {
			this.clearpress = "";
			var tabData = this.getView().getModel("InterStateSupplies2").getProperty("/");
			var stateData = this.getView().getModel("getAllState2").getProperty("/");
			var stateData1 = $.extend(true, {}, stateData);
			if (tabData.length !== 0) {
				for (var i = 0; i < tabData.length; i++) {
					for (var j = 0; j < stateData1.length; j++) {
						if (tabData[i].pos === stateData1[j].state) {
							stateData1.splice(j, 1);
						}
					}
				}
			}
			this.getView().getModel("getAllState2").setData(stateData1);
			var newRec1 = {
				"pos": "",
				"taxableVal": "",
				"igst1": "",
				"sectionName": "3.2(c)",
				"subSectionName": "SMTUINH"
			};
			this.data1.push(newRec1);
			this.getView().setModel(new JSONModel(this.data1), "InterStateSupplies2");
		},

		messageCheck: function (TabData) {
			if (!TabData.pos) {
				MessageBox.error("Please Select Place of Supply");
				return;
			}
			if (!TabData.taxableVal) {
				MessageBox.error("Please Enter Taxable Value");
				return;
			}
			if (!TabData.igst) {
				MessageBox.error("Please Enter IGST Amount");
				return;
			}
		},

		onInterStateSave: function (key) {
			var TabData = this.getView().getModel("LinkTabGSTN1").getProperty("/"),
				TabData1 = this.getView().getModel("InterStateSupplies").getProperty("/"),
				TabData2 = this.getView().getModel("InterStateSupplies1").getProperty("/"),
				TabData3 = this.getView().getModel("InterStateSupplies2").getProperty("/"),
				totTaxval = 0,
				totIgstval = 0,
				flag = false;

			for (var i = 0; i < TabData1.length; i++) {
				totTaxval += +(TabData1[i].taxableVal || "");
				totIgstval += +(TabData1[i].igst || "");

				if (!TabData1[i].pos || !TabData1[i].taxableVal || !TabData1[i].igst) {
					return this.messageCheck(TabData1[i]);
				}
			}

			for (var j = 0; j < TabData2.length; j++) {
				totTaxval += +(TabData2[j].taxableVal || "");
				totIgstval += +(TabData2[j].igst || "");

				if (!TabData2[j].pos || !TabData2[j].taxableVal || !TabData2[j].igst) {
					return this.messageCheck(TabData2[j]);
				}
			}

			for (var k = 0; k < TabData3.length; k++) {
				totTaxval += +(TabData3[k].taxableVal || "");
				totIgstval += +(TabData3[k].igst || "");

				if (!TabData3[k].pos || !TabData3[k].taxableVal || !TabData3[k].igst) {
					return this.messageCheck(TabData3[k]);
				}
			}

			if (key !== "back") {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onInterStateSaveChanges = "Saved";
							this.onInterStateSave1();
						}
					}.bind(this)
				});
			} else {
				this.InterStateSuppliesChage = this.onInterStateSaveChanges = "";
				this.onInterStateSave1();
				this.byId("dpGstr3bAddItem").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onInterStateSave1: function () {
			var userInputList = [];
			var TabData1 = this.getView().getModel("InterStateSupplies").getProperty("/");
			var TabData2 = this.getView().getModel("InterStateSupplies1").getProperty("/");
			var TabData3 = this.getView().getModel("InterStateSupplies2").getProperty("/");
			if (TabData1.length === 0 && TabData2.length === 0 && TabData3.length === 0) {
				this.clearpress = "clicked";
			}
			for (var i = 0; i < TabData1.length; i++) {
				var obj = {
					"sectionName": TabData1[i].sectionName,
					"subSectionName": TabData1[i].subSectionName,
					"taxableVal": TabData1[i].taxableVal.toString(),
					"igst": TabData1[i].igst.toString(),
					"cgst": "0",
					"sgst": "0",
					"cess": "0",
					"pos": TabData1[i].pos, //TabData1[i].pos.split("-")[0].trim(),
					"interState": "0",
					"intraState": "0"
				};
				if (TabData1[i].posCompute !== undefined && TabData1[i].posAutoCal === undefined && (TabData1[i].pos !== undefined &&
						TabData1[i].pos !== "")) {
					userInputList.push(obj);
				} else if (TabData1[i].posCompute === undefined && TabData1[i].posAutoCal !== undefined && (TabData1[i].pos !== undefined &&
						TabData1[i].pos !== "")) {
					userInputList.push(obj);
				} else if (TabData1[i].posCompute === undefined && TabData1[i].posAutoCal === undefined) {
					userInputList.push(obj);
				}
			}

			for (var j = 0; j < TabData2.length; j++) {
				var obj1 = {
					"sectionName": TabData2[j].sectionName,
					"subSectionName": TabData2[j].subSectionName,
					"taxableVal": TabData2[j].taxableVal.toString(),
					"igst": TabData2[j].igst.toString(),
					"cgst": "0",
					"sgst": "0",
					"cess": "0",
					"pos": TabData2[j].pos, //TabData2[j].pos.split("-")[0].trim(),
					"interState": "0",
					"intraState": "0"
				};
				if (TabData2[j].posCompute !== undefined && TabData2[j].posAutoCal === undefined && (TabData2[j].pos !== undefined &&
						TabData2[j].pos !== "")) {
					userInputList.push(obj1);
				} else if (TabData2[j].posCompute === undefined && TabData2[j].posAutoCal !== undefined && (TabData2[j].pos !== undefined &&
						TabData2[j].pos !== "")) {
					userInputList.push(obj1);
				} else if (TabData2[j].posCompute === undefined && TabData2[j].posAutoCal === undefined) {
					userInputList.push(obj1);
				}
			}

			for (var k = 0; k < TabData3.length; k++) {
				var obj2 = {
					"sectionName": TabData3[k].sectionName,
					"subSectionName": TabData3[k].subSectionName,
					"taxableVal": TabData3[k].taxableVal.toString(),
					"igst": TabData3[k].igst.toString(),
					"cgst": "0",
					"sgst": "0",
					"cess": "0",
					"pos": TabData3[k].pos, //TabData3[k].pos.split("-")[0].trim(),
					"interState": "0",
					"intraState": "0"
				};
				if (TabData3[k].posCompute !== undefined && TabData3[k].posAutoCal === undefined && (TabData3[k].pos !== undefined &&
						TabData3[k].pos !== "")) {
					userInputList.push(obj2);
				} else if (TabData3[k].posCompute === undefined && TabData3[k].posAutoCal !== undefined && (TabData3[k].pos !== undefined &&
						TabData3[k].pos !== "")) {
					userInputList.push(obj2);
				} else if (TabData3[k].posCompute === undefined && TabData3[k].posAutoCal === undefined) {
					userInputList.push(obj2);
				}
			}

			var status;
			if (this.clearpress === "clicked") {
				status = "3.2_DELETEALL";
				this.clearpress = "";
			} else {
				status = "3.2_SAVE";
			}

			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": (this.byId("linkGSTID").getSelectedKey() || this.gstin),
					"status": "3.2_SAVE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success("Changes saved successfully.");
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				}.bind(this));
		},

		onSupplyChange: function (oEvt) {
			this.InterStateSuppliesChage = "changed";
			this.onInterStateSaveChanges = "";
			oEvt.getSource().getParent().getParent().getModel("InterStateSupplies").setProperty(oEvt.getSource().getParent().getBindingContext(
				"InterStateSupplies").sPath + "/pos", oEvt.getSource().getSelectedKey());
			this.getView().getModel("InterStateSupplies").refresh(true);
		},

		onSupplyChange1: function (oEvt) {
			this.InterStateSuppliesChage = "changed";
			this.onInterStateSaveChanges = "";
			oEvt.getSource().getParent().getParent().getModel("InterStateSupplies1").setProperty(oEvt.getSource().getParent().getBindingContext(
				"InterStateSupplies1").sPath + "/pos", oEvt.getSource().getSelectedKey());
			this.getView().getModel("InterStateSupplies1").refresh(true);
		},

		onSupplyChange2: function (oEvt) {
			this.InterStateSuppliesChage = "changed";
			this.onInterStateSaveChanges = "";
			oEvt.getSource().getParent().getParent().getModel("InterStateSupplies2").setProperty(oEvt.getSource().getParent().getBindingContext(
				"InterStateSupplies2").sPath + "/pos", oEvt.getSource().getSelectedKey());
			this.getView().getModel("InterStateSupplies2").refresh(true);
		},

		ExemptNillSave: function (key) {
			var TabData1 = this.getView().getModel("ExemptNill").getData().resp;
			for (var i = 0; i < TabData1.length; i++) {
				var val = TabData1[i].interState.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}

				var intraState = TabData1[i].intraState.toString();
				var intraState1 = intraState.split(".");
				if (intraState1[1] !== undefined) {
					if (intraState1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (intraState1[1] !== "" && intraState1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (intraState1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg1 = /^[0-9]*\.?[0-9]*$/;
				if (!intraState.match(reg1)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
			}

			if (key !== "back") {
				MessageBox.information("Do you want to Save Changes!", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this.onInterStateSaveChanges = "Saved";
							this.ExemptNillSave1();
						} else {
							this.byId("dpNilNonExempt").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.ExemptNillSave1();
				this.InterStateSuppliesChage = "";
				this.onInterStateSaveChanges = "";
				key = "";
				this.byId("dpNilNonExempt").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		ExemptNillSave1: function () {
			var userInputList = [];
			var TabData1 = this.getView().getModel("ExemptNill").getData().resp;
			for (var i = 0; i < TabData1.length; i++) {
				var obj = {
					"sectionName": TabData1[i].sectionName,
					"subSectionName": TabData1[i].subSectionName,
					"taxableVal": "0",
					"igst": "0",
					"cgst": "0",
					"sgst": "0",
					"cess": "0",
					"pos": "0",
					"interState": TabData1[i].interState === "" ? "0" : TabData1[i].interState.toString(),
					"intraState": TabData1[i].intraState === "" ? "0" : TabData1[i].intraState.toString()
				};
				userInputList.push(obj);
			}

			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"status": "SAVE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						MessageBox.success("Changes saved successfully.");
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		XLDownload: function () {
			var oCashTabData = this.finalArray;
			for (var i = 0; i < oCashTabData.length; i++) {
				if (oCashTabData[i].supplyType === "TORCIS") {
					oCashTabData[i].supplyType = "Tax on Outward and Reverse Charge Inward Supplies";
				} else if (oCashTabData[i].supplyType === "ISS") {
					oCashTabData[i].supplyType = "Inter-State Supplies";
				} else if (oCashTabData[i].supplyType === "E_ITC") {
					oCashTabData[i].supplyType = "Eligible ITC";
				} else if (oCashTabData[i].supplyType === "ENANGST") {
					oCashTabData[i].supplyType = "Exempt, Nil & Non-GST Inward Supplies";
				} else if (oCashTabData[i].supplyType === "IALF") {
					oCashTabData[i].supplyType = "Interest & Late Fee Payable";
				} else if (oCashTabData[i].supplyType === "OTS1") {
					oCashTabData[i].supplyType = "Outward Taxable Supplies (Other than zero rated, nil rated and exempted)";
				} else if (oCashTabData[i].supplyType === "OTS2") {
					oCashTabData[i].supplyType = "Outward Taxable Supplies (Zero Rated)";
				} else if (oCashTabData[i].supplyType === "OTS3") {
					oCashTabData[i].supplyType = "Other Outward Supplies (Nil Rated, Exempted)";
				} else if (oCashTabData[i].supplyType === "IS") {
					oCashTabData[i].supplyType = "Inward Supplies (Liable to Reverse Charge)";
				} else if (oCashTabData[i].supplyType === "NON_GST") {
					oCashTabData[i].supplyType = "Non-GST Outward Supplies";
				} else if (oCashTabData[i].supplyType === "SMTURP") {
					oCashTabData[i].supplyType = "Supplies made to Unregistered Persons";
				} else if (oCashTabData[i].supplyType === "SMTCTP") {
					oCashTabData[i].supplyType = "Supplies made to Composition Taxable Persons";
				} else if (oCashTabData[i].supplyType === "SMTUINH") {
					oCashTabData[i].supplyType = "Supplies made to UIN holders";
				} else if (oCashTabData[i].supplyType === "ITC_Avail") {
					oCashTabData[i].supplyType = "ITC Avalilabe (Whether in full or part)";
				} else if (oCashTabData[i].supplyType === "IOG") {
					oCashTabData[i].supplyType = "Import of goods";
				} else if (oCashTabData[i].supplyType === "IOS") {
					oCashTabData[i].supplyType = "Import of Service";
				} else if (oCashTabData[i].supplyType === "ISLTR") {
					oCashTabData[i].supplyType = "Inward Supplies liable to Reverse Charge (Other than 1 & 2 above)";
				} else if (oCashTabData[i].supplyType === "ISFISD") {
					oCashTabData[i].supplyType = "Inward Supplies from ISD";
				} else if (oCashTabData[i].supplyType === "AO_ITC") {
					oCashTabData[i].supplyType = "All other ITC";
				} else if (oCashTabData[i].supplyType === "ITC_R") {
					oCashTabData[i].supplyType = "ITC Reversed";
				} else if (oCashTabData[i].supplyType === "AP42&43") {
					oCashTabData[i].supplyType = "As per rules 38, 42 and 43 of CGST Rules and sub-section (5) of section 17";
				} else if (oCashTabData[i].supplyType === "IR_OTHERS") {
					oCashTabData[i].supplyType = "Others";
				} else if (oCashTabData[i].supplyType === "NET_ITC_AVAIL") {
					oCashTabData[i].supplyType = "Net ITC Available (A)-(B)";
				} else if (oCashTabData[i].supplyType === "I_ITC") {
					oCashTabData[i].supplyType = "Other Details";
				} else if (oCashTabData[i].supplyType === "APS17") {
					oCashTabData[i].supplyType = "ITC reclaimed which was reversed under Table 4(B)(2) in earlier tax period";
				} else if (oCashTabData[i].supplyType === "II_OTHERS") {
					oCashTabData[i].supplyType = "Ineligible ITC under section 16(4) and ITC restricted due to PoS provisions";
				} else if (oCashTabData[i].supplyType === "ENANGST") {
					oCashTabData[i].supplyType = "Exempt, Nil & Non-GST Inward Supplies";
				} else if (oCashTabData[i].supplyType === "FSUC") {
					oCashTabData[i].supplyType = "From a Supplier under composition scheme, Exempt & Nil Rated Supply";
				} else if (oCashTabData[i].supplyType === "NGST_SUPPLY") {
					oCashTabData[i].supplyType = "Non GST Supply";
				} else if (oCashTabData[i].supplyType === "IALF") {
					oCashTabData[i].supplyType = "Interest & Late Fee Payable";
				} else if (oCashTabData[i].supplyType === "INTEREST") {
					oCashTabData[i].supplyType = "Interest";
				} else if (oCashTabData[i].supplyType === "LATE_FEES") {
					oCashTabData[i].supplyType = "Late Fees";
				} else if (oCashTabData[i].supplyType === "DOSN") {
					oCashTabData[i].supplyType = "Details of Supplies notified under section 9(5)";
				} else if (oCashTabData[i].supplyType === "TSOEO") {
					oCashTabData[i].supplyType = "Taxable supplies on which E-com operator pays tax u/s 9(5)";
				} else if (oCashTabData[i].supplyType === "TSMRP") {
					oCashTabData[i].supplyType = "Taxable supplies made by registered person through E-com operator";
				} else if (oCashTabData[i].supplyType === "PPLIA") {
					oCashTabData[i].supplyType = "Past period liability";
				}
			}
			var oCashTabModel = new sap.ui.model.json.JSONModel();
			oCashTabModel.setData(oCashTabData);

			var oExport = new Export({
				exportType: new ExportTypeCSV({
					separatorChar: "\t",
					mimeType: "application/vnd.ms-excel",
					charset: "utf-8",
					fileExtension: "xls"
				}),
				models: oCashTabModel,
				rows: {
					path: "/"
				},
				columns: [{
					name: "Table",
					template: {
						content: "{table}"
					}
				}, {
					name: "Type of Supply",
					template: {
						content: "{supplyType}"
					}
				}, {
					name: "Taxable Value(DigiGST Computed)",
					template: {
						content: "{computedTaxableVal}"
					}
				}, {
					name: "IGST(DigiGST Computed)",
					template: {
						content: "{computedIgst}"
					}
				}, {
					name: "CGST(DigiGST Computed)",
					template: {
						content: "{computedCgst}"
					}
				}, {
					name: "SGST(DigiGST Computed)",
					template: {
						content: "{computedSgst}"
					}
				}, {
					name: "Cess(DigiGST Computed)",
					template: {
						content: "{computdCess}"
					}
				}, {
					name: "Taxable Value(GSTN Computed)",
					template: {
						content: "{autoCalTaxableVal}"
					}
				}, {
					name: "IGST(GSTN Computed)",
					template: {
						content: "{autoCalIgst}"
					}
				}, {
					name: "CGST(GSTN Computed)",
					template: {
						content: "{autoCalCgst}"
					}
				}, {
					name: "SGST(GSTN Computed)",
					template: {
						content: "{autoCalSgst}"
					}
				}, {
					name: "Cess(GSTN Computed)",
					template: {
						content: "{autoCalCess}"
					}
				}, {
					name: "Taxable Value(DigiGST User Input)",
					template: {
						content: "{userInputTaxableVal}"
					}
				}, {
					name: "IGST(DigiGST User Input)",
					template: {
						content: "{userInputIgst}"
					}
				}, {
					name: "CGST(DigiGST User Input)",
					template: {
						content: "{userInputCgst}"
					}
				}, {
					name: "SGST(DigiGST User Input)",
					template: {
						content: "{userInputSgst}"
					}
				}, {
					name: "Cess(DigiGST User Input)",
					template: {
						content: "{userInputCess}"
					}
				}, {
					name: "Taxable Value(GSTN)",
					template: {
						content: "{gstinTaxableVal}"
					}
				}, {
					name: "IGST(GSTN)",
					template: {
						content: "{gstinIgst}"
					}
				}, {
					name: "CGST(GSTN)",
					template: {
						content: "{gstinCgst}"
					}
				}, {
					name: "SGST(GSTN)",
					template: {
						content: "{gstinSgst}"
					}
				}, {
					name: "Cess(GSTN)",
					template: {
						content: "{gstinCess}"
					}
				}, {
					name: "Taxable Value(Difference)",
					template: {
						content: "{diffenceTaxableVal}"
					}
				}, {
					name: "IGST(Difference)",
					template: {
						content: "{diffenceIGSTVal}"
					}
				}, {
					name: "CGST(Difference)",
					template: {
						content: "{diffenceCGSTVal}"
					}
				}, {
					name: "SGST(Difference)",
					template: {
						content: "{diffenceSGSTVal}"
					}
				}, {
					name: "Cess(Difference)",
					template: {
						content: "{diffenceCESSVal}"
					}
				}]
			});
			this.onSearch();

			var xlName = "GSTR3B_" + this.byId("linkGSTID").getSelectedKey() + "_" + this.byId("linkDate").getValue() + "_DigiGST"
			oExport.saveFile(xlName).catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});
		},

		onSaveStatus: function (oEvt) {
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Save", this);
				this.getView().addDependent(this._oDialogSaveStats);
			}
			var gstin;
			if (oEvt.getSource().getParent().getParent().getBindingContext("GSTR3B") !== undefined) {
				gstin = oEvt.getSource().getParent().getParent().getBindingContext("GSTR3B").getObject().gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var vDate = new Date();
			this.byId("DP4").setMaxDate(vDate);
			this.byId("DP4").setValue(this.byId("gstr3bDate").getValue());
			this.byId("slGstinSave1").setSelectedKey(gstin);
			this.onSaveOkay();
			this._oDialogSaveStats.open();
			//this.saveGSTIN();
		},

		onCloseDialogSave: function () {
			this._oDialogSaveStats.close();
		},

		saveGSTIN: function () {},

		onSaveOkay: function () {
			var req = {
				"req": {
					"gstin": this.byId("slGstinSave1").getSelectedKey(),
					"taxPeriod": this.byId("DP4").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSaveStatus.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.details.forEach(function (e, i) {
						e.sNo = i + 1;
					});
					this.getView().byId("idTableSave").setModel(new JSONModel(data.resp), "GstinSaveModel");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onPressSaveToGSTN: function () {
			MessageBox.information("Please ensure to save DigiGST(User Input) data before proceeding to Save To GSTN.", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onPressSaveChangesBefore();
					}
				}.bind(this)
			});
		},

		//==================== Added by chaithra on 19/2/2021 to enable save changes before save to GSTN ============//	
		onPressSaveChangesBefore: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getData(),
				msgFlag = true;

			try {
				TabData.forEach(function (item) {
					if (!["ISS", "ENANGST"].includes(item.supplyType)) {
						item.level2.forEach(function (l2) {
							if (["3.1(a)", "3.1(d)"].includes(l2.table)) {
								if (+l2.userInputCgst !== +l2.userInputSgst) {
									throw new Error("CGST and SGST cannot be different for " + l2.table);
								}
							}

							l2.level3.forEach(function (l3) {
								if (["4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(1)", "4(d)(1)", "4(d)(2)"].includes(l3.table)) {
									if (+l3.userInputCgst !== +l3.userInputSgst) {
										throw new Error("CGST and SGST cannot be different for " + l3.table);
									}
								}
							});
						});
					}
					var aFields = [
						"computedTaxableVal", "computedIgst", "computedCgst", "computedSgst", "computdCess",
						"autoCalTaxableVal", "autoCalIgst", "autoCalCgst", "autoCalSgst", "autoCalCess",
						"userInputTaxableVal", "userInputIgst", "userInputCgst", "userInputSgst", "userInputCess",
						"gstinTaxableVal", "gstinIgst", "gstinCgst", "gstinSgst", "gstinCess",
						"diffenceTaxableVal", "diffenceIGSTVal", "diffenceCGSTVal", "diffenceSGSTVal", "diffenceCESSVal"
					];
					aFields.forEach(function (e) {
						if (+item[e]) {
							msgFlag = false;
						}
					});
				}.bind(this));
				if (msgFlag) {
					MessageBox.warning('Do you want to save the data with ZERO Value, are you sure?', {
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (sAction) {
							if (sAction === "YES") {
								this._savetoGstnConfirmation();
							}
						}.bind(this)
					});
				} else {
					this._savetoGstnConfirmation();
				}
			} catch (error) {
				MessageBox.error(error.message);
			}
		},

		_savetoGstnConfirmation: function () {
			MessageBox.information('Do you want to Save Changes?', {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.saveChangesclick = "Saved";
						this.onPressSaveChangesBefore1();
					} else {
						return;
					}
				}.bind(this)
			});
		},

		onPressSaveChangesBefore1: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getData();
			var userInputList = [];
			for (var i = 0; i < TabData.length; i++) {
				if (TabData[i].supplyType !== "ISS" && TabData[i].supplyType !== "ENANGST") {
					for (var j = 0; j < TabData[i].level2.length; j++) {
						if (TabData[i].level2[j].table !== "4(a)" && TabData[i].level2[j].table !== "4(b)" && TabData[
								i].level2[j].table !== "4(d)" && TabData[i].level2[j].table !== "3.2(a)" && TabData[i].level2[j].table !== "3.2(b)" &&
							TabData[
								i].level2[j].table !== "3.2(c)") {
							var famPenDet = {
								sectionName: TabData[i].level2[j].table,
								subSectionName: TabData[i].level2[j].supplyType,
								taxableVal: TabData[i].level2[j].userInputTaxableVal === "" ? "0" : TabData[i].level2[j].userInputTaxableVal.toString(),
								igst: TabData[i].level2[j].userInputIgst === "" ? "0" : TabData[i].level2[j].userInputIgst.toString(),
								cgst: TabData[i].level2[j].userInputCgst === "" ? "0" : TabData[i].level2[j].userInputCgst.toString(),
								sgst: TabData[i].level2[j].userInputSgst === "" ? "0" : TabData[i].level2[j].userInputSgst.toString(),
								cess: TabData[i].level2[j].userInputCess === "" ? "0" : TabData[i].level2[j].userInputCess.toString(),
								//pos: "0",
								interState: "0",
								intraState: "0"
							};
							userInputList.push(famPenDet);
						}
						if (TabData[i].level2[j].level3.length > 0) {
							for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
								var famPenDet1 = {
									sectionName: TabData[i].level2[j].level3[k].table,
									subSectionName: TabData[i].level2[j].level3[k].supplyType,
									taxableVal: TabData[i].level2[j].level3[k].userInputTaxableVal === "" ? "0" : TabData[i].level2[j].level3[k].userInputTaxableVal
										.toString(),
									igst: TabData[i].level2[j].level3[k].userInputIgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputIgst.toString(),
									cgst: TabData[i].level2[j].level3[k].userInputCgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputCgst.toString(),
									sgst: TabData[i].level2[j].level3[k].userInputSgst === "" ? "0" : TabData[i].level2[j].level3[k].userInputSgst.toString(),
									cess: TabData[i].level2[j].level3[k].userInputCess === "" ? "0" : TabData[i].level2[j].level3[k].userInputCess.toString(),
									//pos: "0",
									interState: "0",
									intraState: "0"
								};
								userInputList.push(famPenDet1);
							}
						}
					}
				}
			}

			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"status": "SAVE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						this.onPressSaveToGSTN1();
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		//==================== code ended by chaithra on 19/2/2021 to enable save changes before save to GSTN =======//	
		onPressSaveToGSTN1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var req = {
				"gstin": gstin,
				"retPeriod": this.byId("linkDate").getValue()
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BsaveToGstn.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success(data.resp);
						this.editfun(); // Added by chaithra on 19/2/2021 
						this.onSearch(); // added by chaithra on 19/2/2021
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onGenerate3bPress: function () {
			MessageBox.information("Do you want to initiate Auto Calculate 3B - DigiGST?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onGenerate3bPress1();
					}
				}.bind(this)
			});
		},

		onGenerate3bPress1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"req": {
					"gstin": gstin,
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr3BGenerate.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						this.saveChangesclick = "";
						this.clickCESS = "";
						this.clickSGST = "";
						this.clickCGST = "";
						this.clickIGST = "";
						this.clickTV = "";
						MessageBox.success(data.resp);
						this.onSearch();
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onClearUserData: function () {
			MessageBox.information("Do you want to Clear User Data!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onClearUserData1();
					}
				}.bind(this)
			});
		},

		onClearUserData1: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getData();
			var userInputList = [];
			for (var i = 0; i < TabData.length; i++) {
				if (TabData[i].supplyType !== "ISS" && TabData[i].supplyType !== "ENANGST") {
					for (var j = 0; j < TabData[i].level2.length; j++) {
						if (TabData[i].level2[j].table !== "4(a)" && TabData[i].level2[j].table !== "4(b)" &&
							TabData[i].level2[j].table !== "4(d)" && TabData[i].level2[j].table !== "3.2(a)" &&
							TabData[i].level2[j].table !== "3.2(b)" && TabData[i].level2[j].table !== "3.2(c)"
						) {
							var famPenDet = {
								sectionName: TabData[i].level2[j].table,
								subSectionName: TabData[i].level2[j].supplyType,
								taxableVal: "0",
								igst: "0",
								cgst: "0",
								sgst: "0",
								cess: "0",
								//pos: "0",
								interState: "0",
								intraState: "0"
							};
							userInputList.push(famPenDet);
						}
						if (TabData[i].level2[j].level3.length > 0) {
							for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
								var famPenDet1 = {
									sectionName: TabData[i].level2[j].level3[k].table,
									subSectionName: TabData[i].level2[j].level3[k].supplyType,
									taxableVal: "0",
									igst: "0",
									cgst: "0",
									sgst: "0",
									cess: "0",
									//pos: "0",
									interState: "0",
									intraState: "0"
								};
								userInputList.push(famPenDet1);
							}
						}
					}
				}
			}
			////////////////////End///////////////////
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var req = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"status": "DELETE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/deleteGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						this.onSearch();
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onClearUserData5: function () {
			this.outClearDataPress = "pressed";
			var TabData = this.getView().getModel("LinkTabGSTN1").getData();
			for (var i = 0; i < TabData.length; i++) {
				if (TabData[i].supplyType !== "ENANGST") {
					for (var j = 0; j < TabData[i].level2.length; j++) {
						if (TabData[i].level2[j].table !== "4(a)" && TabData[i].level2[j].table !== "4(b)" &&
							TabData[i].level2[j].table !== "4(d)") {
							TabData[i].userInputTaxableVal = "";
							TabData[i].userInputIgst = "";
							TabData[i].userInputCgst = "";
							TabData[i].userInputSgst = "";
							TabData[i].userInputCess = "";

							TabData[i].diffenceTaxableVal = Number(TabData[i].userInputTaxableVal) - Number(TabData[i].gstinTaxableVal);
							TabData[i].diffenceIGSTVal = Number(TabData[i].userInputIgst) - Number(TabData[i].gstinIgst);
							TabData[i].diffenceCGSTVal = Number(TabData[i].userInputCgst) - Number(TabData[i].gstinCgst);
							TabData[i].diffenceSGSTVal = Number(TabData[i].userInputSgst) - Number(TabData[i].gstinSgst);
							TabData[i].diffenceCESSVal = Number(TabData[i].userInputCess) - Number(TabData[i].gstinCess);

							TabData[i].level2[j].userInputTaxableVal = "";
							TabData[i].level2[j].userInputIgst = "";
							TabData[i].level2[j].userInputCgst = "";
							TabData[i].level2[j].userInputSgst = "";
							TabData[i].level2[j].userInputCess = "";

							TabData[i].level2[j].diffenceTaxableVal = Number(TabData[i].level2[j].userInputTaxableVal) - Number(TabData[i].level2[j].gstinTaxableVal);
							TabData[i].level2[j].diffenceIGSTVal = Number(TabData[i].level2[j].userInputIgst) - Number(TabData[i].level2[j].gstinIgst);
							TabData[i].level2[j].diffenceCGSTVal = Number(TabData[i].level2[j].userInputCgst) - Number(TabData[i].level2[j].gstinCgst);
							TabData[i].level2[j].diffenceSGSTVal = Number(TabData[i].level2[j].userInputSgst) - Number(TabData[i].level2[j].gstinSgst);
							TabData[i].level2[j].diffenceCESSVal = Number(TabData[i].level2[j].userInputCess) - Number(TabData[i].level2[j].gstinCess);
						}
						if (TabData[i].level2[j].level3.length > 0) {
							for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
								// if (TabData[i].level2[j].level3[k].table !== "4(a)(5)") {
								TabData[i].level2[j].userInputTaxableVal = "";
								TabData[i].level2[j].userInputIgst = "";
								TabData[i].level2[j].userInputCgst = "";
								TabData[i].level2[j].userInputSgst = "";
								TabData[i].level2[j].userInputCess = "";

								TabData[i].level2[j].diffenceTaxableVal = Number(TabData[i].level2[j].userInputTaxableVal) - Number(TabData[i].level2[j].gstinTaxableVal);
								TabData[i].level2[j].diffenceIGSTVal = Number(TabData[i].level2[j].userInputIgst) - Number(TabData[i].level2[j].gstinIgst);
								TabData[i].level2[j].diffenceCGSTVal = Number(TabData[i].level2[j].userInputCgst) - Number(TabData[i].level2[j].gstinCgst);
								TabData[i].level2[j].diffenceSGSTVal = Number(TabData[i].level2[j].userInputSgst) - Number(TabData[i].level2[j].gstinSgst);
								TabData[i].level2[j].diffenceCESSVal = Number(TabData[i].level2[j].userInputCess) - Number(TabData[i].level2[j].gstinCess);

								TabData[i].level2[j].level3[k].userInputTaxableVal = "";
								TabData[i].level2[j].level3[k].userInputIgst = "";
								TabData[i].level2[j].level3[k].userInputCgst = "";
								TabData[i].level2[j].level3[k].userInputSgst = "";
								TabData[i].level2[j].level3[k].userInputCess = "";

								TabData[i].level2[j].level3[k].diffenceTaxableVal = Number(TabData[i].level2[j].level3[k].userInputTaxableVal) - Number(
									TabData[i].level2[j].level3[k].gstinTaxableVal);
								TabData[i].level2[j].level3[k].diffenceIGSTVal = Number(TabData[i].level2[j].level3[k].userInputIgst) - Number(TabData[i].level2[
									j].level3[k].gstinIgst);
								TabData[i].level2[j].level3[k].diffenceCGSTVal = Number(TabData[i].level2[j].level3[k].userInputCgst) - Number(TabData[i].level2[
									j].level3[k].gstinCgst);
								TabData[i].level2[j].level3[k].diffenceSGSTVal = Number(TabData[i].level2[j].level3[k].userInputSgst) - Number(TabData[i].level2[
									j].level3[k].gstinSgst);
								TabData[i].level2[j].level3[k].diffenceCESSVal = Number(TabData[i].level2[j].level3[k].userInputCess) - Number(TabData[i].level2[
									j].level3[k].gstinCess);
								// }
							}
						}
					}
				}
			}
			this.getView().getModel("LinkTabGSTN1").refresh(true);
			var aretArr = this.getView().getModel("LinkTabGSTN1").getData();
			var userInputSgst = "0.0";
			var userInputIgst = "0.0";
			var userInputCgst = "0.0";
			var userInputCess = "0.0";
			for (var p = 0; p < aretArr.length; p++) {
				if (aretArr[p].table == "4") {
					for (var t = 0; t < aretArr[p].level2.length; t++) {
						if (aretArr[p].level2[t].table === "4(a)") {
							if (aretArr[p].level2[t].level3.length > 0) {
								var obj = aretArr[p].level2[t].level3.find(function (ob) {
									return ob.table == "4(a)(5)";
								});
								if (obj) {
									userInputSgst = obj.userInputSgst;
									userInputIgst = obj.userInputIgst;
									userInputCgst = obj.userInputCgst;
									userInputCess = obj.userInputCess;
								}
							}
						}
						if (aretArr[p].level2[t].table === "4(c)") {
							aretArr[p].level2[t].userInputSgst = userInputSgst;
							aretArr[p].level2[t].userInputIgst = userInputIgst;
							aretArr[p].level2[t].userInputCgst = userInputCgst;
							aretArr[p].level2[t].userInputCess = userInputCess;

							/*aretArr[p].level2[t].diffenceTaxableVal = Number(aretArr[p].level2[t].userInputTaxableVal) - Number(aretArr[p].level2[t].gstinTaxableVal);
							aretArr[p].level2[t].diffenceIGSTVal = Number(aretArr[p].level2[t].userInputIgst) - Number(aretArr[p].level2[t].gstinIgst);
							aretArr[p].level2[t].diffenceCGSTVal = Number(aretArr[p].level2[t].userInputCgst) - Number(aretArr[p].level2[t].gstinCgst);
							aretArr[p].level2[t].diffenceCESSVal = Number(aretArr[p].level2[t].userInputCess) - Number(aretArr[p].level2[t].gstinCess);*/

							aretArr[p].userInputSgst = userInputSgst;
							aretArr[p].userInputIgst = userInputIgst;
							aretArr[p].userInputCgst = userInputCgst;
							aretArr[p].userInputCess = userInputCess;
						}
					}
					break;
				}
			}
			this.getView().getModel("LinkTabGSTN1").refresh(true);
		},

		onSaveGSTR: function () {
			var gstin = this.byId("gstrTabId").getSelectedIndices();
			if (gstin.length === 0) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Save GSTR-3B?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onSaveGSTR1();
					}
				}.bind(this)
			});
		},

		onSaveGSTR1: function () {
			if (!this._oDialogbulkSaveStats) {
				this._oDialogbulkSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.BulkSave", this);
				this.getView().addDependent(this._oDialogbulkSaveStats);
			}

			var aGSTIN = [];
			var oView = this.getView();
			var oModelData = oView.getModel("GSTR3B").getData();
			var oPath = oView.byId("gstrTabId").getSelectedIndices();
			for (var i = 0; i < oPath.length; i++) {
				aGSTIN.push(oModelData.resp[oPath[i]].gstin);
			}
			var req = {
				"retPeriod": this.byId("gstr3bDate").getValue(),
				"gstinList": aGSTIN
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BBulkSaveToGstn.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._oDialogbulkSaveStats.open();
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		onCloseDialogBulkSave: function () {
			this._oDialogbulkSaveStats.close();
		},

		onPressDifferenceGSTR3B: function (oEvent) {
			this.gstin = oEvent.getSource().getEventingParent().getEventingParent().getBindingContext("GSTR3B").getObject().gstin;
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstin": this.gstin
				}
			};
			this.linkTab1(Request);
			this._oDialoggstr3 = undefined;
			if (!this._oDialoggstr3) {
				this._oDialoggstr3 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr3b.Differencegstr3B", this);
				this.getView().addDependent(this._oDialoggstr3);
			}
			this._oDialoggstr3.open();
		},

		onCDialogDiffGSTR3B: function (oEvent) {
			if (this._oDialoggstr3) {
				this._oDialoggstr3.destroy();
			}
		},

		onEdit: function () {
			MessageBox.information("Do you want to Edit User Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onEditData();
					}
				}.bind(this)
			});
		},

		onEditData: function () {
			var TabData = this.getView().getModel("LinkTabGSTN1").getData();
			for (var i = 0; i < TabData.length; i++) {
				for (var j = 0; j < TabData[i].level2.length; j++) {
					TabData[i].level2[j].edit = true;
					if (TabData[i].supplyType === "ISS" || TabData[i].supplyType === "ENANGST") {
						TabData[i].level2[j].edit = false;
					}
					if (TabData[i].level2[j].level3.length > 0) {
						for (var k = 0; k < TabData[i].level2[j].level3.length; k++) {
							TabData[i].level2[j].level3[k].edit = true;
							if (TabData[i].level2[j].level3[k].supplyType === "AO_ITC" || TabData[i].level2[j].level3[k].supplyType === "AP42&43" ||
								TabData[i].level2[j].level3[k].supplyType === "IR_OTHERS") {
								TabData[i].level2[j].level3[k].edit = false;
							}
						}
					}
				}
			}
			this.getView().getModel("LinkTabGSTN1").refresh(true);

		},

		InterStateSuppliesChage: function (oEvt) {
			this.InterStateSuppliesChage = "changed";
			this.onInterStateSaveChanges = "";
			this.anyDecimalValue(oEvt);
		},

		onInterStateDelete: function (oEvt) {
			var path = oEvt.getSource().getParent().getBindingContext("InterStateSupplies").getPath(),
				index = parseInt(path.substring(path.lastIndexOf('/') + 1));

			MessageBox.information("Do you want to Delete Selected Line Item!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = this.getView().getModel("InterStateSupplies").getData();
						if (this.vIntStModLength == model.length) {
							this.InterStateSuppliesChage = "changed";
							this.onInterStateSaveChanges = "";
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies").refresh(true);
						} else {
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies").refresh(true);
							if (!model.length) {
								this.TabData1Ind = "tab1";
								this.InterStateSuppliesChage = "";
								this.onInterStateSaveChanges = "Saved";
							}
						}
					}
				}.bind(this)
			});
		},

		onInterStateDelete1: function (oEvt) {
			var path = oEvt.getSource().getParent().getBindingContext("InterStateSupplies1").getPath(),
				index = parseInt(path.substring(path.lastIndexOf('/') + 1));
			MessageBox.information("Do you want to Delete Selected Line Item!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = this.getView().getModel("InterStateSupplies1").getData();
						if (this.vIntStModLength1 == model.length) {
							this.InterStateSuppliesChage = "changed";
							this.onInterStateSaveChanges = "";
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies1").refresh(true);
						} else {
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies1").refresh(true);
							if (model.length === 0) {
								this.TabData2Ind = "tab2";
								this.InterStateSuppliesChage = "";
								this.onInterStateSaveChanges = "Saved";
							}
						}
					}
				}.bind(this)
			});
		},

		onInterStateDelete2: function (oEvt) {
			var path = oEvt.getSource().getParent().getBindingContext("InterStateSupplies2").getPath(),
				index = parseInt(path.substring(path.lastIndexOf('/') + 1));
			MessageBox.information("Do you want to Delete Selected Line Item!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = this.getView().getModel("InterStateSupplies2").getData();
						if (this.vIntStModLength2 == model.length) {
							this.InterStateSuppliesChage = "changed";
							this.onInterStateSaveChanges = "";
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies2").refresh(true);
						} else {
							model.splice(index, 1);
							this.getView().getModel("InterStateSupplies2").refresh(true);
							if (model.length === 0) {
								this.TabData2Ind = "tab3";
								this.InterStateSuppliesChage = "";
								this.onInterStateSaveChanges = "Saved";
							}
						}
					}
				}.bind(this)
			});
		},

		onclearData: function () {
			MessageBox.information("Do you want to Clear User Data!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onclearData1();
					}
				}.bind(this)
			});
		},

		onclearData1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var req = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"status": "3.2_DELETE",
					"userInputList": []
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/deleteGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						this.onPressInterStateSupplies();
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onclearData5: function () {
			this.clearpress = "clicked";
			var TabData1 = this.getView().getModel("InterStateSupplies").getData(),
				TabData2 = this.getView().getModel("InterStateSupplies1").getData(),
				TabData3 = this.getView().getModel("InterStateSupplies2").getData();

			if (TabData1.length !== 0) {
				for (var i = TabData1.length; i >= 0; i--) {
					TabData1.splice(i, 1);
				}
			}
			if (TabData2.length !== 0) {
				for (var j = TabData2.length; j >= 0; j--) {
					TabData2.splice(j, 1);
				}
			}
			if (TabData3.length !== 0) {
				for (var k = TabData3.length; k >= 0; k--) {
					TabData3.splice(k, 1);
				}
			}
			this.getView().getModel("InterStateSupplies").refresh(true);
			this.getView().getModel("InterStateSupplies1").refresh(true);
			this.getView().getModel("InterStateSupplies2").refresh(true);
		},

		onExemptclearData: function () {
			MessageBox.information("Do you want to Clear User Data!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onExemptclearData1();
					}
				}.bind(this)
			});
		},

		onExemptclearData1: function () {
			var TabData1 = this.getView().getModel("ExemptNill").getData().resp;
			for (var i = 0; i < TabData1.length; i++) {
				TabData1[i].interStateCompute = "0";
				TabData1[i].intraStateCompute = "0";
				TabData1[i].interState = "0";
				TabData1[i].intraState = "0";
			}
			this.getView().getModel("ExemptNill").refresh(true);
		},

		onPressExpandCollapseGstr3b: function (oEvent) {
			if (oEvent.getSource().getId().includes("bExpGstr3b")) {
				this.byId("tabGstr3bSumm").expandToLevel(2);
				this.byId("tabGstr3bSumm").setVisibleRowCount(8);
			} else {
				this.byId("tabGstr3bSumm").collapseAll();
				this.byId("tabGstr3bSumm").setVisibleRowCount(8);
			}
		},

		onSubmit: function () {
			MessageBox.information("Do you want to Submit!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onSubmit1();
					}
				}.bind(this)
			});
		},

		onSubmit1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var req = {
				"req": {
					"gstin": gstin,
					"retPeriod": this.byId("linkDate").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BSubmitToGstn.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success("Changes saved successfully.");
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		onGSTNdataPress: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": [{
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"fromPeriod": "",
					"toPeriod": ""
				}]
			};
			var Request1 = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstin": this.gstin
				}
			};
			this.onGSTNdataPress1(Request, Request1);
		},

		onGSTNdataPressFrag: function () {
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": [{
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstin": this.gstin,
					"fromPeriod": "",
					"toPeriod": ""
				}]
			};
			var Request1 = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstin": this.gstin
				}
			};
			this.onGSTNdataPress1(Request, Request1);
		},

		onGSTNdataPress1: function (Request, Request1) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGSTR3BSummaryFromGSTN.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success("GSTN data updated successfully");
						this.linkTab1(Request1);
					} else if (data.hdr) {
						MessageBox.error(data.resp[0].msg);
						sap.ui.core.BusyIndicator.hide();
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
						sap.ui.core.BusyIndicator.hide();
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var msg = JSON.parse(jqXHR.responseText).hdr.message;
					if (msg.includes("No data")) {
						MessageBox.error(msg.split(".")[0]);
					} else {
						MessageBox.error(msg);
					}
				});
		},

		onSaveStatusDownload: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("GstinSaveModel").getObject();
			sap.m.URLHelper.redirect("/aspsapapi/download3BSaveResponse.do?id=" + obj.id);
		},

		onSaveStatusDownload1: function (oEvt) {
			var obj = oEvt.getSource().getParent().getBindingContext("TableFS").getObject();
			sap.m.URLHelper.redirect("/aspsapapi/downloadGstr3BErrors.do?id=" + obj.refId);
		},

		onSaveStatusDownload2: function (oEvt) {
			var created = oEvt.getSource().getParent().getBindingContext("GstinSaveModel").getObject().createdTime,
				req = {
					"req": {
						"gstin": this.byId("slGstinSave1").getSelectedKey(),
						"taxPeriod": this.byId("DP4").getValue(),
						"createdOn": created
					}
				};

			this.excelDownload(req, "/aspsapapi/gstr2bJsonDownloadDocument.do");
		},

		onPressGSTR1AdaptFilters: function () {
			if (!this._oDialogANX1Filter) {
				this._oDialogANX1Filter = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr3b.AdaptGSTR3B", this);
				this.getView().addDependent(this._oDialogANX1Filter);
			}

			var oModel = this.byId("dpGstr3b").getModel("DataSecurity");
			sap.ui.getCore().byId("dAdapt1").setModel(oModel, "DataSecurity");
			this._oDialogANX1Filter.open();
		},

		onPressFilterClose: function (oEvent) {
			this._oDialogANX1Filter.close();
		},

		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("GSTR3B").getObject();
			if (oValue1.auth !== "I") {
				return;
			}
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": oValue1.gstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressCancel: function () {
			this._dAuthToken.close();
		},

		onPressYes: function () {
			var aData = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getData(),
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			this.byId("dAuthTokenConfirmation").setBusy(true);
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GenerateOTP", this);
				this.getView().addDependent(this._dGenerateOTP);
			}
			this.byId("dVerifyAuthToken").setModel(new JSONModel(searchInfo.req), "OtpProperty");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getOtp.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
					this._dAuthToken.close();
					if (data.resp.status === "S") {
						var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
							oData = oModel.getData();
						oData.verify = false;
						oData.otp = null;
						oData.resendOtp = false;
						oModel.refresh(true);
						this._dGenerateOTP.open();
					} else {
						MessageBox.error("OTP Generation Failed. Please Try Again", { // Modified by Bharat Gupta on 05.02.2020
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
				}.bind(this));
		},

		onPressResendOTP: function () {
			var oOtpModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				oOtpData = oOtpModel.getData(),
				searchInfo = {
					"req": {
						"gstin": oOtpData.gstin
					}
				};
			oOtpData.resendOtp = false;
			oOtpModel.refresh(true);

			this.byId("dVerifyAuthToken").setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getOtp.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dVerifyAuthToken").setBusy(false);
					var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
						oData = oModel.getData();
					oData.verify = false;
					oData.otp = null;
					if (data.resp.status === "S") {
						oData.resendOtp = true;
						this._dGenerateOTP.open();
					} else {
						oData.resendOtp = false;
						MessageBox.error("OTP Generation Failed. Please Try Again", {
							styleClass: "sapUiSizeCompact"
						});
					}
					oModel.refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
				}.bind(this));
		},

		validateOTP: function (oEvent) {
			var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				value = oEvent.getSource().getValue();

			value = value.replace(/[^\d]/g, "");
			if (value.length === 6) {
				oModel.getData().verify = true;
			} else {
				oModel.getData().verify = false;
			}
			oModel.refresh(true);
			oEvent.getSource().setValue(value);
		},

		onPressVerifyOTP: function () {
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getData(),
				searchInfo = {
					"req": {
						"gstin": oData.gstin,
						"otpCode": oData.otp
					}
				};

			this.byId("dVerifyAuthToken").setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAuthToken.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
					if (data.resp.status === "S") {
						MessageBox.success("OTP is  Matched", {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error("OTP is Not Matched", {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
				}.bind(this));
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		onChangeFYELS: function (oEvent) {
			var vKey = oEvent.getSource().getSelectedKey(),
				cYear = new Date().getFullYear(),
				vFrDt = new Date(+vKey, 3, 1);

			if (+vKey === cYear) {
				var m = new Date().getMonth(),
					vToDt = new Date(vKey, m, 1);
			} else {
				vToDt = new Date(+vKey + 1, 2, 1);
			}
			vToDt.setMonth(vToDt.getMonth() + 1);
			vToDt.setDate(vToDt.getDate() - 1);

			this.byId("idFromPeriodELS").setMinDate(vFrDt);
			this.byId("idFromPeriodELS").setMaxDate(vToDt);
			this.byId("idFromPeriodELS").setDateValue(vFrDt);

			this.byId("idToPeriodELS").setMinDate(vFrDt);
			this.byId("idToPeriodELS").setMaxDate(vToDt);
			this.byId("idToPeriodELS").setDateValue(vToDt);
		},

		onPressDateFilterClose: function (action) {
			if (action === "Apply") {
				var oData = this.getView().getModel("GSTR3B").getProperty("/resp"),
					aIndex = this.byId("gstrTabId").getSelectedIndices(),
					payload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriod": this.byId("gstr3bDate").getValue(),
							"taxPeriodFrom": this._formatPeriod(this.byId("idFromPeriodELS").getDateValue()),
							"taxPeriodTo": this._formatPeriod(this.byId("idToPeriodELS").getDateValue()),
							"dataSecAttrs": {}
						}
					};
				payload.req.dataSecAttrs.GSTIN = aIndex.map(function (idx) {
					return oData[idx].gstin;
				});
				this.reportDownload(payload, "/aspsapapi/getGstr3bEntityLevelReportDownload.do");
			}
			this._dateFilter.close();
		},

		_downloadEntityLevelReport: function () {
			if (!this._dateFilter) {
				this._dateFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.dateFilter", this);
				this.getView().addDependent(this._dateFilter);

				this.byId("idFromPeriodELS").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}
				});
				this.byId("idToPeriodELS").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}
				});
			}
			var vKey = this.getView().getModel("AllFy").getProperty("/finYears/0/key"),
				vDate = new Date(),
				vPeriod = new Date(vKey, 3, 1);

			this.byId("idFYELS").setSelectedKey(vKey);
			this.byId("idFromPeriodELS").setMinDate(vPeriod);
			this.byId("idFromPeriodELS").setMaxDate(vDate);
			this.byId("idFromPeriodELS").setDateValue(vPeriod);

			this.byId("idToPeriodELS").setMinDate(vPeriod);
			this.byId("idToPeriodELS").setMaxDate(vDate);
			this.byId("idToPeriodELS").setDateValue(vDate);
			this._dateFilter.open();
		},

		onChangeDateFilterValue: function (oEvent) {
			var vFrDtId = oEvent.getSource().getId();
			if (vFrDtId.includes("idocFromDate")) {
				var vDatePicker = "idocToDate";
			} else if (vFrDtId.includes("idFromPeriodELS")) {
				vDatePicker = "idToPeriodELS";
			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				fromDate = oEvent.getSource().getDateValue();
			}
			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		onMenuItemPressAnx1down: function (oEvt) {
			var oData = this.getView().getModel("GSTR3B").getProperty("/resp"),
				aIndex = this.byId("gstrTabId").getSelectedIndices(),
				key = oEvt.getParameter("item").getKey(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": this.byId("gstr3bDate").getValue()
					}
				};
			if (!aIndex.length) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			payload.req.gstin = aIndex.map(function (idx) {
				return oData[idx].gstin;
			});
			switch (key) {
			case "submitted":
				payload.req.type = "SaveSubmit";
				payload.req.status = "";
				payload.req.reportCateg = "3B Report";
				payload.req.dataType = "GSTR3B";

				payload.req.gstins = payload.req.gstin;
				delete payload.req.gstin;
				delete payload.req.entityId;
				this.reportDownload(payload, "/aspsapapi/downloadFileStatusCsvReports.do");
				break;
			case "entityLevel":
				this._downloadEntityLevelReport();
				break;
			case "autoCalcGstn":
				this.excelDownload1(payload, "/aspsapapi/gstr3bDownloadAutoCalcReport.do");
				break;
			case "ILD":
				this.excelDownload1(payload, "/aspsapapi/gstr3bInterestReportDownload.do");
				break;
			}
		},

		/**
		 * Method to open Liability Set Off dialog
		 */
		on3bSummLiabillityDetails: function () {
			if (!this._oDialoggstr53) {
				this._oDialoggstr53 = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr3b.Liability", this);
				this.getView().addDependent(this._oDialoggstr53);
			}
			this._oDialoggstr53.open();
			this._refreshLiabilitySetoff();
		},

		/**
		 * Method to Refresh Liability Set off dialog data
		 */
		onRefreshLiablity: function () {
			MessageBox.confirm("Do you want to refresh the offset liability page to fetch the latest ledger / 3B values?", {
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._refreshLiabilitySetoff();
					}
				}.bind(this)
			});
		},

		_refreshLiabilitySetoff: function (flag) {
			var gstin = this.byId("linkGSTID").getSelectedKey() === "" ? this.gstin : this.byId("linkGSTID").getSelectedKey(),
				Request = {
					"req": {
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue()
					}
				};

			this._oDialoggstr53.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/get3bLiabilitySetOff.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					this._oDialoggstr53.setBusy(false);
					this.isRule86B = data.resp.isRule86B;

					if (data.resp.liabilitySetoffStatus !== "SAVED") {
						this._calculateLiabilities(data.resp);
					} else {
						this._displayLiabilities(data.resp);
					}
					sap.ui.getCore().byId("liablityId").setModel(new JSONModel(data.resp), "Liabillity");
					if (flag === "computeAndSave") {
						this._computeSaveLiability();
					}
					if (data.resp.message) {
						MessageBox.error(data.resp.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialoggstr53.setBusy(false);
					MessageBox.error(jqXHR.responseJSON.hdr.message);
				}.bind(this));
		},

		_displayLiabilities: function (data) {
			data.ledgerDetails.forEach(function (item) {
				item.Total = item.total || 0;
				if (item.desc === "tx") {
					item.Total1 = item.crTotal || 0;
				}
			});
			data.gstr3bDetails.forEach(function (item) {
				if (!this.isRule86B) {
					item.otrci2A = "NA";
					item.otrci2B = "NA";
				}
				item.intTax = ["Integrated Tax", "Central Tax", "State/UT Tax"].includes(item.desc);
				item.ceTax = ["Integrated Tax", "Central Tax"].includes(item.desc);
				item.suTax = ["Integrated Tax", "State/UT Tax"].includes(item.desc);
				item.cTax = ["Cess"].includes(item.desc);
				item.Total7 = item.otrc7 || 0;
				item.lateFee12 = item.lateFee12 || 0;
				item.Total14 = item.ucb14 || 0;
				item.Total15 = item.acr15 || 0;
				item.edit = false;
			}.bind(this));
		},

		_getLedgerDetail: function (desc) {
			return {
				"desc": desc,
				"i": 0,
				"c": 0,
				"s": 0,
				"cs": 0,
				"cri": 0,
				"crc": 0,
				"crs": 0,
				"crcs": 0
			};
		},

		_pushLedgerDetail: function (data, field) {
			var aField = ["i", "c", "s", "cs", "cri", "crc", "crs", "crcs"],
				obj = data.find(function (e) {
					return e.desc === field
				});
			if (!obj) {
				data.push(this._getLedgerDetail(field));
				return;
			}
			aField.forEach(function (e) {
				obj[e] = obj[e] || 0;
			});
		},

		_calculateLiabilities: function (data) {
			this._pushLedgerDetail(data.ledgerDetails, "currMonthUtil");
			this._pushLedgerDetail(data.ledgerDetails, "clsBal");
			data.ledgerDetails.forEach(function (e) {
				e.Total = (+e.i + +e.c + +e.s + +e.cs) || 0;
				if (["tx", "currMonthUtil", "clsBal"].includes(e.desc)) {
					e.Total1 = (+e.cri + +e.crc + +e.crs + +e.crcs) || 0;
				}
				if (["tx", "currMonthUtil", "clsBal"].includes(e.desc)) {
					e.Total2 = (+e.nlbIgst + +e.nlbCgst + +e.nlbSgst + +e.nlbCess) || 0;
				}
			});
			data.gstr3bDetails.forEach(function (item) {
				if (!this.isRule86B) {
					item.otrci2A = item.otrci2B = "NA";
				}
				item.intTax = ["Integrated Tax", "Central Tax", "State/UT Tax"].includes(item.desc);
				item.ceTax = ["Integrated Tax", "Central Tax"].includes(item.desc);
				item.suTax = ["Integrated Tax", "State/UT Tax"].includes(item.desc);
				item.cTax = ["Cess"].includes(item.desc);
				item.edit = false;

				if (item.pdi === undefined) {
					item.pdi = 0;
				}
				if (item.pdc === undefined) {
					item.pdc = 0;
				}
				if (item.pds === undefined) {
					item.pds = 0;
				}
				if (item.pdcs === undefined) {
					item.pdcs = 0;
				}
				item.Total7 = (+item.netOthRecTaxPayable2i - +item.pdi - +item.pdc - +item.pds - +item.pdcs) || 0;
				item.Total7 = (item.Total7 < 1 ? 0 : item.Total7).toFixed(2);
				item.rci9 = item.rci9;

				switch (item.desc) {
				case "Integrated Tax":
					var ledger1 = data.ledgerDetails[0].i || 0,
						ledger2 = data.ledgerDetails[1].i || 0,
						ledger3 = data.ledgerDetails[2].i || 0;
					break;
				case "Central Tax":
					var ledger1 = data.ledgerDetails[0].c || 0,
						ledger2 = data.ledgerDetails[1].c || 0,
						ledger3 = data.ledgerDetails[2].c || 0;
					break;
				case "State/UT Tax":
					var ledger1 = data.ledgerDetails[0].s || 0,
						ledger2 = data.ledgerDetails[1].s || 0,
						ledger3 = data.ledgerDetails[2].s || 0;
					break;
				case "Cess":
					var ledger1 = data.ledgerDetails[0].cs || 0,
						ledger2 = data.ledgerDetails[1].cs || 0,
						ledger3 = data.ledgerDetails[2].cs || 0;
					break;
				}

				var total = (+item.Total7 + +item.rci9) || 0,
					init10 = +item.inti10 || 0,
					lateFee = +item.lateFee12 || 0,
					minVal1 = Math.min(total, ledger1),
					minVal2 = Math.min(init10, ledger2),
					minVal3 = Math.min(lateFee, ledger3),
					diff1 = total - minVal1,
					diff2 = init10 - minVal2,
					diff3 = lateFee - minVal3;

				item.Total14 = (minVal1 + minVal2 + minVal3).toFixed(2);
				if (diff1 < 0) {
					diff1 = 0;
				} else if (diff2 < 0) {
					diff2 = 0;
				} else if (diff3 < 0) {
					diff3 = 0;
				}
				item.Total15 = (diff1 + diff2 + diff3).toFixed(2);
			}.bind(this));
		},

		onCancel: function () {
			this._oDialoggstr53.close();
		},

		onLiaEdit: function () {
			MessageBox.information("Do you want to Edit User Data!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onLiaEditData();
					}
				}.bind(this)
			});
		},

		onLiaEditData: function () {
			var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
				oData = oModel.getData();

			oData.gstr3bDetails.forEach(function (item) {
				item.edit = true;
			});
			oModel.refresh(true);
		},

		/**
		 * Method to comupte and save Liability for GSTR-3B Summary
		 */
		onPressCompute: function () {
			MessageBox.information("Do you want to computed based on Rule86B?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					this._confirmComputeSave(oAction === "YES");
				}.bind(this)
			});
		},

		_confirmComputeSave: function (flag) {
			var gstin = this.byId("linkGSTID").getSelectedKey() === "" ? this.gstin : this.byId("linkGSTID").getSelectedKey(),
				Request = {
					"req": {
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue(),
						"isRule86B": flag
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BOffSet86BSave.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._refreshLiabilitySetoff('computeAndSave');
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		_computeSaveLiability: function () {
			var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
				data = oModel.getProperty("/"),
				txLedger = data.ledgerDetails.find(function (e) {
					if (e.desc === "tx") {
						e.cri = +e.cri;
						e.crc = +e.crc;
						e.crs = +e.crs;
						e.crcs = +e.crcs;
						return true;
					}
					return false;
				}),
				intrLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "intr";
				}),
				feeLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "fee";
				}),
				cmLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "currMonthUtil";
				}),
				cbLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "clsBal";
				}),
				otrIGST = this.isRule86B ? data.gstr3bDetails[0].otrci2B : data.gstr3bDetails[0].netOthRecTaxPayable2i,
				otrCGST = this.isRule86B ? data.gstr3bDetails[1].otrci2B : data.gstr3bDetails[1].netOthRecTaxPayable2i,
				otrSGST = this.isRule86B ? data.gstr3bDetails[2].otrci2B : data.gstr3bDetails[2].netOthRecTaxPayable2i,
				otrCess = this.isRule86B ? data.gstr3bDetails[3].otrci2B : data.gstr3bDetails[3].netOthRecTaxPayable2i,
				crIgst = +txLedger.cri,
				crCgst = +txLedger.crc,
				crSgst = +txLedger.crs,
				crCess = +txLedger.crcs;

			this._calIntelligentAnalysis(data.gstr3bDetails, txLedger, true);

			cmLedger.cri = cmLedger.crc = cmLedger.crs = cmLedger.crcs = 0;
			data.gstr3bDetails.forEach(function (item) {
				item.pdi = +item.pdi || 0;
				item.pdc = +item.pdc || 0;
				item.pds = +item.pds || 0;
				item.pdcs = +item.pdcs || 0;

				item.Total7 = +item.netOthRecTaxPayable2i - item.pdi - item.pdc - item.pds - item.pdcs;
				item.Total7 = (item.Total7 < 1 ? 0 : item.Total7).toFixed(2);
				item.rci9 = +item.rci9;

				switch (item.desc) {
				case "Integrated Tax":
					var ledger1 = txLedger.i || 0,
						ledger2 = intrLedger.i || 0,
						ledger3 = feeLedger.i || 0;
					cmLedger.i = (+item.Total7 + +item.rci9);
					break;
				case "Central Tax":
					ledger1 = txLedger.c || 0;
					ledger2 = intrLedger.c || 0;
					ledger3 = feeLedger.c || 0;
					cmLedger.c = (+item.Total7 + +item.rci9);
					break;
				case "State/UT Tax":
					ledger1 = txLedger.s || 0;
					ledger2 = intrLedger.s || 0;
					ledger3 = feeLedger.s || 0;
					cmLedger.s = (+item.Total7 + +item.rci9);
					break;
				case "Cess":
					ledger1 = txLedger.cs || 0;
					ledger2 = intrLedger.cs || 0;
					ledger3 = feeLedger.cs || 0;
					cmLedger.cs = (+item.Total7 + +item.rci9);
					break;
				}

				cmLedger.cri += item.pdi;
				cmLedger.crc += item.pdc;
				cmLedger.crs += item.pds;
				cmLedger.crcs += item.pdcs;

				var total = (+item.Total7 + +item.rci9),
					init10 = +item.inti10,
					lateFee = +item.lateFee12,
					minVal1 = Math.min(total, ledger1),
					minVal2 = Math.min(init10, ledger2),
					minVal3 = Math.min(lateFee, ledger3),
					diff1 = total - minVal1,
					diff2 = init10 - minVal2,
					diff3 = lateFee - minVal3;

				if (diff1 < 0) {
					diff1 = 0;
				} else if (diff2 < 0) {
					diff2 = 0;
				} else if (diff3 < 0) {
					diff3 = 0;
				}
				item.Total14 = (minVal1 + minVal2 + minVal3).toFixed(2);
				item.Total15 = (diff1 + diff2 + diff3).toFixed(2);
			}.bind(this));

			cmLedger.Total = (+cmLedger.i + +cmLedger.c + +cmLedger.s + +cmLedger.cs);
			cmLedger.Total1 = (+cmLedger.cri + +cmLedger.crc + +cmLedger.crs + +cmLedger.crcs);
			var fLedger = ["i", "c", "s", "cs", "Total", "cri", "crc", "crs", "crcs", "Total1"];
			fLedger.forEach(function (e) {
				cbLedger[e] = +txLedger[e] + +(intrLedger[e] || 0) + +(feeLedger[e] || 0) - +cmLedger[e];
			});
			oModel.refresh(true);
			this._computeSave3bLiability();
		},

		// Added for Intelligent analysis calculation - 27.11.2023
		_calIntelligentAnalysis: function (data, taxLed, flag) {
			var txLedger = $.extend(true, {}, taxLed),
				otrIgst = this.isRule86B && flag ? data[0].otrci2B : data[0].netOthRecTaxPayable2i,
				otrCgst = this.isRule86B && flag ? data[1].otrci2B : data[1].netOthRecTaxPayable2i,
				otrSgst = this.isRule86B && flag ? data[2].otrci2B : data[2].netOthRecTaxPayable2i,
				otrCess = this.isRule86B && flag ? data[3].otrci2B : data[3].netOthRecTaxPayable2i;

			data.forEach(function (e) {
				e.pdi = e.pdc = e.pds = 0
			});
			data[3].pdcs = (txLedger.crcs < otrCess) ? txLedger.crcs : otrCess;

			if (txLedger.cri >= otrIgst) {
				var vPdi = Math.floor((txLedger.cri - otrIgst) / 2);
				data[0].pdi = otrIgst;
				data[1].pdi = (vPdi < otrCgst ? vPdi : otrCgst);
				data[2].pdi = (vPdi < otrSgst ? vPdi : otrSgst);
				data[1].pdc = (txLedger.crc + data[1].pdi >= otrCgst ? (otrCgst - data[1].pdi) : txLedger.crc);
				data[2].pds = (txLedger.crs + data[2].pdi >= otrSgst ? (otrSgst - data[2].pdi) : txLedger.crs);

			} else {
				data[0].pdi = txLedger.cri;
				txLedger.cri -= data[0].pdi;
				otrIgst -= data[0].pdi;
				var otrIgst2 = otrIgst / 2;

				data[1].pdc = (txLedger.crc <= otrCgst ? txLedger.crc : otrCgst);
				data[2].pds = (txLedger.crs <= otrSgst ? txLedger.crs : otrSgst);
				txLedger.crc -= data[1].pdc;
				txLedger.crs -= data[2].pds;

				if (otrIgst2 && txLedger.crc > otrIgst2 && txLedger.crs > otrIgst2) {
					data[0].pdc = data[0].pds = otrIgst2;

				} else if (otrIgst) {
					data[0].pdc = (otrIgst > txLedger.crc ? txLedger.crc : otrIgst);
					otrIgst -= data[0].pdc;
					data[0].pds = (otrIgst > txLedger.crs ? txLedger.crs : otrIgst);
					otrIgst -= data[0].pds;
				}
			}
		},

		//======================== added by chaithra on 09/02/2021 To save other columns data into DB=====================//
		_computeSave3bLiability: function () {
			var gstin = !this.byId("linkGSTID").getSelectedKey() ? this.gstin : this.byId("linkGSTID").getSelectedKey(),
				Userdata = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getProperty("/"),
				payload = {
					"req": [{
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue(),
						"otrIgst": +Userdata.gstr3bDetails[0].Total7,
						"otrCgst": +Userdata.gstr3bDetails[1].Total7,
						"otrSgst": +Userdata.gstr3bDetails[2].Total7,
						"otrCess": +Userdata.gstr3bDetails[3].Total7,

						"rcIgst": +Userdata.gstr3bDetails[0].rci8,
						"rcCgst": +Userdata.gstr3bDetails[1].rci8,
						"rcSgst": +Userdata.gstr3bDetails[2].rci8,
						"rcCess": +Userdata.gstr3bDetails[3].rci8,

						"ipIgst": +Userdata.gstr3bDetails[0].inti10,
						"ipCgst": +Userdata.gstr3bDetails[1].inti10,
						"ipSgst": +Userdata.gstr3bDetails[2].inti10,
						"ipCess": +Userdata.gstr3bDetails[3].inti10,

						"acrIgst": +Userdata.gstr3bDetails[0].Total15,
						"acrCgst": +Userdata.gstr3bDetails[1].Total15,
						"acrSgst": +Userdata.gstr3bDetails[2].Total15,
						"acrCess": +Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": +Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": +Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": +Userdata.gstr3bDetails[2].Total14,
						"ucbCess": +Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": +Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": +Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": +Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": +Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};
			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/save3BsetOffComputeToDb.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
						oData = oModel.getProperty("/");

					if (data.hdr !== undefined && data.hdr.status === "S") {
						oData.gstr3bDetails.forEach(function (item) {
							item.edit = false;
						});
						oModel.refresh(true);
						this._confirm3bSaveChanges('C');
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},
		//======================== code ended by chaithra on 09/02/2021 ==================================================//

		/**
		 * Method to save user changes for 3b Liability
		 */
		onLiaSaveChanges: function () {
			var data = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getProperty("/"),
				reg = /^[0-9]*\.?[0-9]*$/;

			var creditIntTax = +data.ledgerDetails[0].cri,
				creditCentralTax = +data.ledgerDetails[0].crc,
				creditStateTax = +data.ledgerDetails[0].crs,
				creditcess = +data.ledgerDetails[0].crcs;

			var payablegstr3BIntTax = +data.gstr3bDetails[0].pdi,
				payablegstr3BIntTax1 = +data.gstr3bDetails[1].pdi,
				payablegstr3BIntTax2 = +data.gstr3bDetails[2].pdi,
				payablegstr3BCentralTax = +data.gstr3bDetails[0].pdc,
				payablegstr3BCentralTax1 = +data.gstr3bDetails[1].pdc,
				payablegstr3BStateTax = +data.gstr3bDetails[0].pds,
				payablegstr3BStateTax1 = +data.gstr3bDetails[2].pds,
				payablegstr3BCess = +data.gstr3bDetails[3].pdcs;

			data.gstr3bDetails.forEach(function (e) {
				e.pdi = +e.pdi || 0;
				e.pdc = +e.pdc || 0;
				e.pds = +e.pds || 0;
				e.pdcs = +e.pdcs || 0;
			});

			if (!payablegstr3BIntTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BIntTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BIntTax2.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BCentralTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BCentralTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BStateTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BStateTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}
			if (!payablegstr3BCess.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			var reg1 = new RegExp('^[0-9]*$');
			if (!reg1.test(payablegstr3BIntTax)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BIntTax1)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BIntTax2)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BCentralTax)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BCentralTax1)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BStateTax)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BStateTax1)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BCess)) {
				MessageBox.error("Decimal Values are not allowed");
				return;
			}
			if (payablegstr3BIntTax + payablegstr3BIntTax1 + payablegstr3BIntTax2 > creditIntTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}
			if (payablegstr3BCentralTax + payablegstr3BCentralTax1 > creditCentralTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}
			if (payablegstr3BStateTax + payablegstr3BStateTax1 > creditStateTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}
			if (payablegstr3BCess > creditcess) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			MessageBox.information("Do you want to save changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._confirm3bSaveChanges('S');
					}
				}.bind(this)
			});
		},

		_confirm3bSaveChanges: function (type) {
			var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
				Userdata = oModel.getProperty("/"),
				gstin = this.byId("linkGSTID").getSelectedKey() || this.gstin,
				txLedger = Userdata.ledgerDetails.find(function (e) {
					if (e.desc === "tx") {
						e.cri = +e.cri;
						e.crc = +e.crc;
						e.crs = +e.crs;
						e.crcs = +e.crcs;
						return true;
					}
					return false;
				}),
				intrLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "intr";
				}),
				feeLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "fee";
				}),
				cmLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "currMonthUtil";
				}),
				cbLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "clsBal";
				}),
				flag7_2A = false;

			Userdata.gstr3bDetails.forEach(function (item) {
				item.pdi = item.pdi || 0;
				item.pdc = item.pdc || 0;
				item.pds = item.pds || 0;
				item.pdcs = item.pdcs || 0;

				item.Total7 = +item.netOthRecTaxPayable2i - item.pdi - item.pdc - item.pds - item.pdcs;
				if (item.Total7 < 1) {
					item.Total7 = 0;
				}
				if (this.isRule86B && item.Total7 < item.otrci2A) {
					flag7_2A = true;
				}

				item.Total7 = item.Total7.toFixed(2);
				item.rci9 = (+item.rci9).toFixed(2);

				switch (item.desc) {
				case "Integrated Tax":
					var ledger1 = txLedger.i || 0,
						ledger2 = intrLedger.i || 0,
						ledger3 = feeLedger.i || 0;
					break;
				case "Central Tax":
					ledger1 = txLedger.c || 0;
					ledger2 = intrLedger.c || 0;
					ledger3 = feeLedger.c || 0;
					break;
				case "State/UT Tax":
					ledger1 = txLedger.s || 0;
					ledger2 = intrLedger.s || 0;
					ledger3 = feeLedger.s || 0;
					break;
				case "Cess":
					ledger1 = txLedger.cs || 0;
					ledger2 = intrLedger.cs || 0;
					ledger3 = feeLedger.cs || 0;
					break;
				}

				var total = (+item.Total7 + +item.rci9),
					init10 = +item.inti10,
					lateFee = +item.lateFee12,
					minVal1 = Math.min(total, ledger1),
					minVal2 = Math.min(init10, ledger2),
					minVal3 = Math.min(lateFee, ledger3),
					diff1 = total - minVal1,
					diff2 = init10 - minVal2,
					diff3 = lateFee - minVal3;

				if (diff1 < 0) {
					diff1 = 0;
				} else if (diff2 < 0) {
					diff2 = 0;
				} else if (diff3 < 0) {
					diff3 = 0;
				}
				item.Total14 = (minVal1 + minVal2 + minVal3).toFixed(2);
				item.Total15 = (diff1 + diff2 + diff3).toFixed(2);
			}.bind(this));
			oModel.refresh(true);

			var Request = {
					"req": {
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue(),
						"liab_ldg_id": Userdata.liab_ldg_id,
						"trans_typ": Userdata.trans_typ,
						"i_pdi": +Userdata.gstr3bDetails[0].pdi,
						"i_pdc": +Userdata.gstr3bDetails[0].pdc,
						"i_pds": +Userdata.gstr3bDetails[0].pds,
						"c_pdi": +Userdata.gstr3bDetails[1].pdi,
						"c_pdc": +Userdata.gstr3bDetails[1].pdc,
						"s_pdi": +Userdata.gstr3bDetails[2].pdi,
						"s_pds": +Userdata.gstr3bDetails[2].pds,
						"cs_pdcs": +Userdata.gstr3bDetails[3].pdcs,
						"i_adjNegative2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"c_adjNegative2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"s_adjNegative2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"cs_adjNegative2i": +Userdata.gstr3bDetails[3].adjNegative2i,
						"i_adjNegative8A": +Userdata.gstr3bDetails[0].adjNegative8A,
						"c_adjNegative8A": +Userdata.gstr3bDetails[1].adjNegative8A,
						"s_adjNegative8A": +Userdata.gstr3bDetails[2].adjNegative8A,
						"cs_adjNegative8A": +Userdata.gstr3bDetails[3].adjNegative8A
					}
				},
				payload = {
					"req": [{
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue(),
						"otrIgst": +Userdata.gstr3bDetails[0].Total7,
						"otrCgst": +Userdata.gstr3bDetails[1].Total7,
						"otrSgst": +Userdata.gstr3bDetails[2].Total7,
						"otrCess": +Userdata.gstr3bDetails[3].Total7,

						"rcIgst": +Userdata.gstr3bDetails[0].rci8,
						"rcCgst": +Userdata.gstr3bDetails[1].rci8,
						"rcSgst": +Userdata.gstr3bDetails[2].rci8,
						"rcCess": +Userdata.gstr3bDetails[3].rci8,

						"ipIgst": +Userdata.gstr3bDetails[0].inti10,
						"ipCgst": +Userdata.gstr3bDetails[1].inti10,
						"ipSgst": +Userdata.gstr3bDetails[2].inti10,
						"ipCess": +Userdata.gstr3bDetails[3].inti10,

						"acrIgst": +Userdata.gstr3bDetails[0].Total15,
						"acrCgst": +Userdata.gstr3bDetails[1].Total15,
						"acrSgst": +Userdata.gstr3bDetails[2].Total15,
						"acrCess": +Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": +Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": +Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": +Userdata.gstr3bDetails[2].Total14,
						"ucbCess": +Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": +Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": +Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": +Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": +Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};
			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);

			if (flag7_2A) {
				MessageBox.information("As per Rule 86B, liability payable is less than 1% of total outward liability. Please rectify?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this._saveSummary3bLiabilityoffset(type, Request, payload);
						}
					}.bind(this)
				});
			} else {
				this._saveSummary3bLiabilityoffset(type, Request, payload);
			}
		},

		_saveSummary3bLiabilityoffset: function (type, request, payload) {
			if (type === "C") {
				this._oDialoggstr53.setBusy(true);
				this._saveSummaryLiabilityOffSet(request)
					.then(function (values) {
							this._oDialoggstr53.setBusy(false);
						}.bind(this),
						function (err) {
							this._oDialoggstr53.setBusy(false);
							console.log("GSTR-3B Summary: ", err);
						}.bind(this));
			} else {
				this._oDialoggstr53.setBusy(true);
				Promise.all([
						this._saveSummaryLiabilityOffSet(request),
						this._saveSummary3BsetOffComputeToDb(payload)
					])
					.then(function (values) {
							this._oDialoggstr53.setBusy(false);
						}.bind(this),
						function (err) {
							this._oDialoggstr53.setBusy(false);
							console.log("GSTR-3B Summary: ", err);
						}.bind(this));
			}
		},

		_saveSummaryLiabilityOffSet: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveChangesLiabilityOffSet.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr !== undefined && data.hdr.status === "S") {
							this._refreshLiabilitySetoff();
							MessageBox.success(data.resp);
						} else {
							MessageBox.error(JSON.parse(data).hdr.message);
						}
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						reject(err);
					});
			}.bind(this));
		},

		_saveSummary3BsetOffComputeToDb: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/save3BsetOffComputeToDb.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						resolve(data.hdr.status);
					})
					.fail(function (err) {
						reject(err);
					});
			});
		},

		onSave3bLiability: function () {
			var data = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getProperty("/gstr3bDetails"),
				aField = ["pdi", "pdc", "pds", "pdcs", "Total7", "rci9"],
				msgFlag = true;

			data.forEach(function (item) {
				aField.forEach(function (f) {
					if (+item[f]) {
						msgFlag = false;
					}
				});
			});

			MessageBox.information("Do you want to save Offset Liability to GSTN?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						if (msgFlag) {
							MessageBox.warning("Do you want to file 3B with ZERO Value, are you sure?", {
								actions: [MessageBox.Action.YES, MessageBox.Action.NO],
								onClose: function (oAction) {
									if (oAction === "YES") {
										this._confirmSave3bLiability();
									}
								}.bind(this)
							});
						} else {
							this._confirmSave3bLiability();
						}
					}
				}.bind(this)
			});
		},

		_confirmSave3bLiability: function () {
			MessageBox.information("By clicking Save Off Set Liability, GSTR-3B will get submitted. Do you want to Save & Submit GSTR-3B ?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onoffLiaSaveChanges();
					}
				}.bind(this)
			});
		},

		onoffLiaSaveChanges: function () {
			var gstin = this.byId("linkGSTID").getSelectedKey() === "" ? this.gstin : this.byId("linkGSTID").getSelectedKey(),
				Request = {
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue()
				};

			var Userdata = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getProperty("/"),
				payload = {
					"req": [{
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue(),

						"otrIgst": +Userdata.gstr3bDetails[0].Total7,
						"otrCgst": +Userdata.gstr3bDetails[1].Total7,
						"otrSgst": +Userdata.gstr3bDetails[2].Total7,
						"otrCess": +Userdata.gstr3bDetails[3].Total7,

						"rcIgst": +Userdata.gstr3bDetails[0].rci8,
						"rcCgst": +Userdata.gstr3bDetails[1].rci8,
						"rcSgst": +Userdata.gstr3bDetails[2].rci8,
						"rcCess": +Userdata.gstr3bDetails[3].rci8,

						"ipIgst": +Userdata.gstr3bDetails[0].inti10,
						"ipCgst": +Userdata.gstr3bDetails[1].inti10,
						"ipSgst": +Userdata.gstr3bDetails[2].inti10,
						"ipCess": +Userdata.gstr3bDetails[3].inti10,

						"acrIgst": +Userdata.gstr3bDetails[0].Total15,
						"acrCgst": +Userdata.gstr3bDetails[1].Total15,
						"acrSgst": +Userdata.gstr3bDetails[2].Total15,
						"acrCess": +Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": +Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": +Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": +Userdata.gstr3bDetails[2].Total14,
						"ucbCess": +Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": +Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": +Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": +Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": +Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};

			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);
			this._oDialoggstr53.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/save3BsetOffComputeToDb.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr !== undefined && data.hdr.status === "S") {
						$.ajax({
								method: "POST",
								url: "/aspsapapi/saveOffSetLiabilityToGstin.do",
								contentType: "application/json",
								data: JSON.stringify(Request)
							})
							.done(function (data, status, jqXHR) {
								this._oDialoggstr53.setBusy(false);
								if (data.hdr && data.hdr.status === "S") {
									if (data.resp.includes("Auth Token is Inactive")) {
										MessageBox.error(data.resp);
									} else {
										MessageBox.success(data.resp, {
											onClose: function () {
												this._get3bLiabilitySetOffStatusUpdate(Request);
											}.bind(this)
										});
									}
									//this.onSearch();
								} else {
									MessageBox.error(JSON.parse(data).hdr.message);
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								this._oDialoggstr53.setBusy(false);
							}.bind(this));
					} else {
						this._oDialoggstr53.setBusy(false);
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialoggstr53.setBusy(false);
				}.bind(this));
		},

		/**
		 * Added on 30.05.2023 for 3B Liability Set off Status update
		 */
		_get3bLiabilitySetOffStatusUpdate: function (payload) {
			var oModel = this._oDialoggstr53.getContent()[0].getModel("Liabillity"),
				oPayload = {
					"req": {
						"gstin": payload.gstin,
						"taxPeriod": payload.taxPeriod,
						"ledgerDetails": this._getPayloadLeaderDetail(oModel.getProperty("/ledgerDetails")),
						"gstr3bDetails": this._getPayloadGstr3bDetail(oModel.getProperty("/gstr3bDetails"))
					}
				};
			var pSetoffStats = this._liabilitySetoffStatus(payload),
				pSaveSetOff = new Promise(function (resolve, reject) {
					$.ajax({
							url: "/aspsapapi/gstr3BOffSetSnapSave.do",
							method: "POST",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						})
						.done(function (data) {
							resolve('S');
						})
						.fail(function (err) {
							reject(err);
						});
				});

			this._oDialoggstr53.setBusy(true);
			Promise.all([pSetoffStats, pSaveSetOff])
				.then(function (values) {
					this._oDialoggstr53.setBusy(false);
				}.bind(this))
				.catch(function (err) {
					this._oDialoggstr53.setBusy(false);
					console.log('GSTR-3B Error:', err);
				}.bind(this));
		},

		_liabilitySetoffStatus: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						url: "/aspsapapi/get3bLiabilitySetOffStatusUpdate.do",
						method: "POST",
						contentType: "application/json",
						data: JSON.stringify({
							"req": payload
						})
					})
					.done(function (data) {
						var oModel = this._oDialoggstr53.getContent()[0].getModel("Liabillity");
						oModel.setProperty('/liabilitySetoffStatus', data.resp.liabilitySetoffStatus);
						oModel.setProperty('/updatedOn', data.resp.updatedOn);
						oModel.refresh(true);
						resolve('S');
					}.bind(this))
					.fail(function (error) {
						reject(error);
					}.bind(this));
			}.bind(this));
		},

		_getPayloadLeaderDetail: function (data) {
			return data.map(function (e) {
				switch (e.desc) {
				case 'tx':
					var obj = {
						"desc": "tx",
						"i": e.i,
						"c": e.c,
						"s": e.s,
						"cs": e.cs,
						"total": e.Total,
						"cri": e.cri,
						"crc": e.crc,
						"crs": e.crs,
						"crcs": e.crcs,
						"crTotal": e.Total1,
						"nlbIgst": e.nlbIgst,
						"nlbCgst": e.nlbCgst,
						"nlbSgst": e.nlbSgst,
						"nlbCess": e.nlbCess,
						"nlbTotal": e.Total1
					};
					break;
				case 'intr':
					obj = {
						"desc": "intr",
						"i": e.i,
						"c": e.c,
						"s": e.s,
						"cs": e.cs,
						"total": e.Total,
					};
					break;
				case 'fee':
					obj = {
						"desc": "fee",
						"i": e.i,
						"c": e.c,
						"s": e.s,
						"cs": e.cs,
						"total": e.Total,
					};
					break;
				case 'currMonthUtil':
					obj = {
						"desc": "currMonthUtil",
						"i": e.i,
						"c": e.c,
						"s": e.s,
						"cs": e.cs,
						"cri": e.cri,
						"crc": e.crc,
						"crs": e.crs,
						"crcs": e.crcs,
						"nlbIgst": e.nlbIgst,
						"nlbCgst": e.nlbCgst,
						"nlbSgst": e.nlbSgst,
						"nlbCess": e.nlbCess,
						"nlbTotal": e.Total1
					};
					break;
				case 'clsBal':
					obj = {
						"desc": "clsBal",
						"i": e.i,
						"c": e.c,
						"s": e.s,
						"cs": e.cs,
						"cri": e.cri,
						"crc": e.crc,
						"crs": e.crs,
						"crcs": e.crcs,
						"nlbIgst": e.nlbIgst,
						"nlbCgst": e.nlbCgst,
						"nlbSgst": e.nlbSgst,
						"nlbCess": e.nlbCess,
						"nlbTotal": e.Total1
					};
					break;
				}
				return obj;
			}.bind(this));
		},

		_getPayloadGstr3bDetail: function (data) {
			return data.map(function (e) {
				switch (e.desc) {
				case "Integrated Tax":
					var obj = {
						"desc": "Integrated Tax",
						"otrci": e.otrci,
						"otrci2A": (e.otrci2A === "NA" ? 0 : e.otrci2A),
						"otrci2B": (e.otrci2B === "NA" ? 0 : e.otrci2B),
						"pdi": e.pdi,
						"pdc": e.pdc,
						"pds": e.pds,
						"otrc7": e.Total7,
						"rci8": e.rci8,
						"inti10": e.inti10,
						"lateFee12": e.lateFee12,
						"ucb14": e.Total14,
						"acr15": e.Total15,
						"adjNegative2i": e.adjNegative2i,
						"netOthRecTaxPayable2i": e.netOthRecTaxPayable2i,
						"adjNegative8A": e.adjNegative8A,
						"rci9": e.rci9
					}
					break;
				case "Central Tax":
					obj = {
						"desc": "Central Tax",
						"otrci": e.otrci,
						"otrci2A": (e.otrci2A === "NA" ? 0 : e.otrci2A),
						"otrci2B": (e.otrci2B === "NA" ? 0 : e.otrci2B),
						"pdi": e.pdi,
						"pdc": e.pdc,
						"otrc7": e.Total7,
						"rci8": e.rci8,
						"inti10": e.inti10,
						"lateFee12": e.lateFee12,
						"ucb14": e.Total14,
						"acr15": e.Total15,
						"adjNegative2i": e.adjNegative2i,
						"netOthRecTaxPayable2i": e.netOthRecTaxPayable2i,
						"adjNegative8A": e.adjNegative8A,
						"rci9": e.rci9
					}
					break;
				case "State/UT Tax":
					obj = {
						"desc": "State/UT Tax",
						"otrci": e.otrci,
						"otrci2A": (e.otrci2A === "NA" ? 0 : e.otrci2A),
						"otrci2B": (e.otrci2B === "NA" ? 0 : e.otrci2B),
						"pdi": e.pdi,
						"pds": e.pds,
						"otrc7": e.Total7,
						"rci8": e.rci8,
						"inti10": e.inti10,
						"lateFee12": e.lateFee12,
						"ucb14": e.Total14,
						"acr15": e.Total15,
						"adjNegative2i": e.adjNegative2i,
						"netOthRecTaxPayable2i": e.netOthRecTaxPayable2i,
						"adjNegative8A": e.adjNegative8A,
						"rci9": e.rci9
					}
					break;
				case "Cess":
					obj = {
						"desc": "Cess",
						"otrci": e.otrci,
						"otrci2A": (e.otrci2A === "NA" ? 0 : e.otrci2A),
						"otrci2B": (e.otrci2B === "NA" ? 0 : e.otrci2B),
						"pdcs": e.pdcs,
						"otrc7": e.Total7,
						"rci8": e.rci8,
						"inti10": e.inti10,
						"lateFee12": e.lateFee12,
						"ucb14": e.Total14,
						"acr15": e.Total15,
						"adjNegative2i": e.adjNegative2i,
						"netOthRecTaxPayable2i": e.netOthRecTaxPayable2i,
						"adjNegative8A": e.adjNegative8A,
						"rci9": e.rci9
					}
					break;
				}
				return obj;
			}.bind(this));
		},

		onSignFile: function (oEvt, button) {
			var key = oEvt.getParameter("item").getKey();
			this.button = button;
			if (key === "DSC") {
				this.onSignFileDSC();
				this.DSCEVCflag = "DSC"
			} else {
				this.onSignFileDSC();
				this.DSCEVCflag = "EVC"
			}
		},
		onSignFileDSC: function () {
			if (this.authState === "I") {
				MessageBox.warning("Auth token is inactive for selected GSTIN, please activate and retry.");
				return;
			}

			if (!this._oDialogSaveStats56) {
				this._oDialogSaveStats56 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.SignandFile", this);
				this.getView().addDependent(this._oDialogSaveStats56);
			}

			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"req": {
					"gstin": gstin
				}
			};

			$.ajax({
					method: "POST",
					url: "/aspsapapi/get3BSignAndFilePanDetails.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					this._oDialogSaveStats56.open();
					if (this.DSCEVCflag === "DSC") {
						data.resp.header = "Please select the authorised signatory for which DSC is attached for initiating filing of GSTR-3B"
					} else {
						data.resp.header = "Please select the authorised signatory for which EVC is attached for initiating filing of GSTR-3B"
					}
					this.getView().setModel(new JSONModel(data.resp), "SignandFile");
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onSaveClose: function () {
			this._oDialogSaveStats56.close();
		},

		onSaveSign: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			if (sel === null) {
				MessageBox.error("Please select an Authorised signatory for initiating filing");
				return;
			}
			var msg = this.button == 'Nil' ? "Do you want to proceed to file Nill Return? - No data will be saved from DigiGST to GST portal." :
				"Do you want to proceed with filing GSTR-3B?"

			MessageBox.information(msg, {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						if (this.DSCEVCflag === "DSC") {
							this.onSaveSign2();
						} else {
							this.onSaveSignEVCConformation();
						}
					}
				}.bind(this)
			});
		},

		onSaveSign2: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var Request = {
				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"pan": selItem
				}
			};
			if (this.button == 'Nil') {
				Request.req.isNil = "Y";
			}

			this.excelDownload(Request, "/aspsapapi/GSTR3BSignAndFileStage1.do");
			this._oDialogSaveStats56.close();
			MessageBox.information("GSTR3B filing is initiated, click on filing status to view the status", {
				onClose: function (oAction) {
					this.onSearch();
				}.bind(this)
			});
		},

		onSaveSignEVCConformation: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var date = this.byId("linkDate").getValue(),
				taxPeriod = date.slice(0, 2) + "/" + date.slice(2, 6),
				Request = {
					"req": {
						"gstin": gstin,
						"taxPeriod": taxPeriod,
						"pan": selItem
					}
				};
			this.ReqPayload = Request;
			if (!this._oDialogSaveStatsConfirm) {
				this._oDialogSaveStatsConfirm = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr3b.SignandFileConformation", this);
				this.getView().addDependent(this._oDialogSaveStatsConfirm);
			}
			this.getView().setModel(new JSONModel(Request.req), "confirmval");
			this._oDialogSaveStats56.close();
			this._oDialogSaveStatsConfirm.open();
		},

		onPopupCancel: function () {
			this._oDialogSaveStatsConfirm.close();
		},

		onPopupConfirm: function () {
			var RequestPayload = {
				"req": {
					"gstin": this.ReqPayload.req.gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"pan": this.ReqPayload.req.pan,
					"returnType": "gstr3b"
				}
			};
			if (this.button == 'Nil') {
				RequestPayload.req.isNil = "Y";
			}

			$.ajax({
					method: "POST",
					url: "/aspsapapi/evcSignAndFileStage1.do",
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr) {
						if (data.hdr.status === "S") {
							this._getSignandFileOTP(data)
							this.signId = data.resp.signId
							this._oDialogSaveStatsConfirm.close();
						} else {
							MessageBox.error(data.resp);
						}
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		_getSignandFileOTP: function (data) {
			if (!this._oDialogSignandFileOTP) {
				this._oDialogSignandFileOTP = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr3b.SignandFileOTP", this);
				this.getView().addDependent(this._oDialogSignandFileOTP);
			}
			this.byId("otpValue").setValue();
			this._oDialogSignandFileOTP.open();
		},

		onPopupOTPCancel: function () {
			this._oDialogSignandFileOTP.close();
		},

		onPopupOTPSign: function () {
			var otp = this.byId("otpValue").getValue();
			if (otp === null) {
				MessageBox.error("Please add OTP");
				return;
			}

			var RequestPayload = {
				"req": {
					"signId": this.signId,
					"otp": otp,
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/evcSignAndFileStage2.do",
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data === true) {
						MessageBox.success("Return Filing has been initiated successfully");
					} else {
						MessageBox.error(data.resp);
					}
					this._oDialogSignandFileOTP.close();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onMenuItemSummary: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			switch (key) {
			case "REVERSAL":
				var oPayload = {
					"req": {
						"gstin": this.byId("linkGSTID").getSelectedKey(),
						"taxPeriod": this.byId("linkDate").getValue(),
						// "reportType": key
					}
				};
				this.reportDownload2(oPayload, "/aspsapapi/download180DaysReversalRespReport.do");
				break;
			case "ILD":
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstin": [this.byId("linkGSTID").getSelectedKey()],
						"taxPeriod": this.byId("linkDate").getValue()
					}
				};
				this.excelDownload1(oPayload, "/aspsapapi/gstr3bInterestReportDownload.do");
				break;
			case "Table4Trans":
				oPayload = {
					"req": {
						"gstin": this.byId("linkGSTID").getSelectedKey(),
						"taxPeriod": this.byId("linkDate").getValue()
					}
				};
				this.reportDownload(oPayload, "/aspsapapi/downloadTable4TransactionalReport.do");
				break;
			default:
				oPayload = {
					"req": {
						"gstins": this.byId("linkGSTID").getSelectedKey(),
						"taxPeriod": this.byId("linkDate").getValue(),
						"reportType": key
					}
				};
				this.reportDownload(oPayload, "/aspsapapi/gstr3bInwardOutwardReport.do");
			}
		},
		/*Code ended byArun on 22/11/2021*/

		//=======================================================================================
		//---------- Code Start For GSTR-1 & 1A vs GSTR-3B Added By Ram ------------------------------
		//=======================================================================================
		handleIconTabBarSelect3B: function (oEvent) {
			if (oEvent.getParameters().selectedKey === "GSRT3BSummary") {

			} else if (oEvent.getParameters().selectedKey === "gstr1vs3b") {
				this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
				this.byId("dpGstr1vs3Summary").setVisible(false);
				// var vDate = new Date();
				// vDate.setDate(vDate.getDate() - 9);
				// this.byId("idPFromtaxPeriod").setDateValue(vDate);
				// this.byId("idPFromtaxPeriod").setMaxDate(new Date());
				// this.byId("idPTotaxPeriod").setDateValue(new Date());
				// this.byId("idPTotaxPeriod").setMinDate(vDate);
				// this.byId("idPTotaxPeriod").setMaxDate(new Date());
				this._getProcessedData();
			} else if (oEvent.getParameters().selectedKey === "gstr2avs3b") {
				// this.byId("dpGstr2Avs3BProcessRecord").setVisible(true);
				// this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
				// this.byId("dpGstr1vs3Summary").setVisible(false);

			} else if (oEvent.getParameters().selectedKey === "gstr3bSetoff") {
				this.onGetSetOffEntityData();
				this.byId("id_SetOFfTaxPeriod").setDateValue(new Date());
				this.byId("id_MainSetOffTab").setFixedColumnCount(1);
			}
		},

		onChangeDateValue: function (oEvent) {
			var vId = oEvent.getSource().getId();
			if (vId.includes("idPFromtaxPeriod")) {
				var vDatePicker = "idPTotaxPeriod";
			} else if (vId.includes("idSFromtaxPeriod")) {
				vDatePicker = "idSTotaxPeriod";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			this.byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
		},

		onSearchGSTR1vs3B: function (flag) {
			if (flag === "P") {
				this._getProcessedData();
			} else {
				this._getSummaryData();
			}
		},

		_getProcessedData: function () {
			var oTaxPeriodFrom = this.getView().byId("idPFromtaxPeriod").getValue() || null,
				oTaxPeriodTo = this.getView().byId("idPTotaxPeriod").getValue() || null,
				aGstin = this.getView().byId("idPGstin").getSelectedKeys(),
				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": oTaxPeriodFrom,
						"taxPeriodTo": oTaxPeriodTo,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			// if (this.byId("dAdapt")) {
			// 	this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
			// }
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1VsGstr3bProcessSummary.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp === undefined) {
						this.getView().setModel(new JSONModel(null), "ProcessedRecord");
					} else {
						this.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error : gstr1VsGstr3bProcessSummary");
				});
		},

		onPressGstr1Summary: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				vFrDate = this.byId("idPFromtaxPeriod").getDateValue(),
				vToDate = this.byId("idPTotaxPeriod").getDateValue(),
				vFrDtSumm = new Date(vFrDate.getFullYear(), vFrDate.getMonth(), 1);

			this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
			this.byId("dpGstr1vs3Summary").setVisible(true);

			this.byId("idSFromtaxPeriod").setDateValue(vFrDtSumm);
			this.byId("idSTotaxPeriod").setMinDate(vFrDtSumm);
			this.byId("idSTotaxPeriod").setDateValue(new Date(vToDate.getFullYear(), vToDate.getMonth(), 1));
			this.byId("idSummaryGstin").setSelectedKey(obj.gstin);

			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idPFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idPTotaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [obj.gstin]
					}
				}
			};
			this._getProcessSummary(searchInfo);
		},

		_getSummaryData: function (oPayload) {
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
					"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
					"dataSecAttrs": {
						"GSTIN": [this.byId("idSummaryGstin").getSelectedKey()]
					}
				}
			};
			this._getProcessSummary(searchInfo);
		},

		_getProcessSummary: function (oPayload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1VsGstr3bReviewSummary.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp === undefined) {
						this.getView().setModel(new JSONModel(null), "ProcessSummary");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].flag = true;
							for (var j = 0; j < data.resp[i].items.length; j++) {
								data.resp[i].items[j].flag = false;
							}
						}
						this.getView().setModel(new JSONModel(data.resp), "ProcessSummary");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "ProcessSummary");
					MessageBox.error("Error : gstr1VsGstr3bReviewSummary ");
				}.bind(this));
		},

		onPressInitiateRecon: function (view) {
			if (view === "P") {
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();

				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var aGstin = oSelectedItem.map(function (idx) {
						return oModelForTab1[idx].gstin;
					}),
					postData = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriodFrom": this.byId("idPFromtaxPeriod").getValue(),
							"taxPeriodTo": this.byId("idPTotaxPeriod").getValue(),
							"dataSecAttrs": {
								"GSTIN": aGstin
							}
						}
					};
			} else {

			}
			this.gstr1vs3bproceCall(postData);
		},

		gstr1vs3bproceCall: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1vs3bproceCall.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);
					this._getProcessedData();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1vs3bproceCall : Error");
				});
		},

		onPressGSTR3BGetDetails: function (view) {
			if (view === "P") {
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();

				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oSelectedItem.length; i++) {
					postData.req.push({
						"taxPeriod": "",
						"gstin": oModelForTab1[oSelectedItem[i]].gstin,
						"fromPeriod": this.byId("idPFromtaxPeriod").getValue(),
						"toPeriod": this.byId("idPTotaxPeriod").getValue()
					});
				}
				this.vPSFlag = "P";
			} else {

			}
			this.getGstr3BGstnGetSection(postData);

		},

		getGstr3BGstnGetSection: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGSTR3BSummaryFromGSTN.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					var aMockMessages = [];
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
					this.getView().setModel(new JSONModel(aMockMessages), "Msg");
					this.onDialogPress();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGSTR3BSummaryFromGSTN.do : Error");
				});
		},

		onPressGstr1aGetDetails: function () {
			var oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices(),
				oData = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getProperty("/"),
				fDate = this.byId("idPFromtaxPeriod").getValue(),
				tDate = this.byId("idPTotaxPeriod").getValue(),
				payload = {
					"req": []
				};

			if (!oSelectedItem.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			oSelectedItem.forEach(function (e) {
				payload.req.push({
					"gstin": oData[e].gstin,
					"fromPeriod": fDate,
					"toPeriod": tDate,
					"ret_period": "",
					"gstr1aSections": [],
					"action_required": "Y",
					"isFailed": false
				});
			}.bind(this));
			this.vPSFlag = "P";
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr1AGstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var aMockMessages = [];
					if (!data.resp.length) {
						MessageBox.information("No Data");
					} else {
						data.resp.forEach(function (e) {
							aMockMessages.push({
								type: 'Success',
								title: e.gstin,
								gstin: e.msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						});
					}
					this.getView().setModel(new JSONModel(aMockMessages), "Msg");
					this.onDialogPress();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr1GstnGetSection: ", err);
				}.bind(this));
		},

		onPressGstr1GetDetails: function (view) {
			if (view === "P") {
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();

				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oSelectedItem.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1[oSelectedItem[i]].gstin,
						"ret_period": "",
						"gstr1aSections": [],
						"action_required": "Y",
						"isFailed": false,
						"fromPeriod": this.byId("idPFromtaxPeriod").getValue(),
						"toPeriod": this.byId("idPTotaxPeriod").getValue()

					});
				}
				this.vPSFlag = "P";
			} else {

			}
			this.getGstr1GstnGetSection(postData);
		},

		getGstr1GstnGetSection: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr1GstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					var aMockMessages = [];
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
					this.getView().setModel(new JSONModel(aMockMessages), "Msg");
					this.onDialogPress();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr1GstnGetSection : Error");
				});
		},

		onDialogPress: function () {
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get Status",
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
								this._getProcessedData();
							} else {
								this._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onPressBack: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("dpGstr1vs3Summary").setVisible(false);
		},

		onPressDownloadReport: function (button) {
			if (button === "P") {
				var oModelForTab1 = this.byId("tabGstr1vs3Process").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGstr1vs3Process").getSelectedIndices();

				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriodFrom": this.byId("idPFromtaxPeriod").getValue(),
						"taxPeriodTo": this.byId("idPTotaxPeriod").getValue(),
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
				oPayload.req.dataSecAttrs.GSTIN = oSelectedItem.map(function (idx) {
					return oModelForTab1[idx].gstin;
				});
				this.excelDownload(oPayload, "/aspsapapi/gstr1vs3bReviewSummaryDownload.do");

			} else {
				var aGstin = [this.byId("idSummaryGstin").getSelectedKey()],
					oPayload = {
						"req": {
							"entityId": [$.sap.entityID],
							"taxPeriodFrom": this.byId("idSFromtaxPeriod").getValue(),
							"taxPeriodTo": this.byId("idSTotaxPeriod").getValue(),
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : aGstin
							}
						}
					};
				this.excelDownload(oPayload, "/aspsapapi/gstr1vs3bReviewSummaryDownload.do");
			}
		},

		/**
		 * Method called to open/close Full Screen for GSTR-1 & 1A vs GSTR-3B
		 * Developed by: Bharat Gupta - 08.12.2020
		 * @memberOf com.ey.digigst.view.GSTR3B1
		 * @param {string} type Full Screen Action
		 */
		on1vs3bFullScreen: function (type) {
			var oContainer = this.byId("cc1vs3bSummary"),
				oTable = this.byId("tabOutward"),
				flag = (type === "O");

			oContainer.setFullScreen(flag);
			oTable.setVisibleRowCount(flag ? 20 : 10);
			this.byId("b1vs3bOpen").setVisible(!flag);
			this.byId("b1vs3bClose").setVisible(flag);
		},

		//=======================================================================================
		//---------- Code End For GSTR-1 & 1A vs GSTR-3B Added By Ram ------------------------------
		//=======================================================================================
		onAutoCalcDigiGst: function () {
			var gstin = this.byId("gstrTabId").getSelectedIndices();
			if (!gstin.length) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			MessageBox.information("Do you want to initiate Auto Calculate 3B - DigiGST?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this._getGstr3BDigiCalculate();
					}
				}.bind(this)
			});
		},

		_getGstr3BDigiCalculate: function () {
			var oModelData = this.getView().getModel("GSTR3B").getProperty("/"),
				oPath = this.getView().byId("gstrTabId").getSelectedIndices(),
				aGSTIN = oPath.map(function (e) {
					return oModelData.resp[e].gstin;
				});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr3BDigiCalculate.do",
					contentType: "application/json",
					data: JSON.stringify({
						"retPeriod": this.byId("gstr3bDate").getValue(),
						"entityId": $.sap.entityID,
						"gstinList": aGSTIN
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this._openAutoCalc3bMsg(data.resp, "DigiGST");
					} else {
						MessageBox.error("Error Occured in Auto Calculated 3B-DigiGST..! Please try again");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured in Auto Calculated 3B-DigiGST..! Please try again");
				}.bind(this));
		},

		_openAutoCalc3bMsg: function (resp, type, flag) {
			if (!this._dAutoCalc3B) {
				this._dAutoCalc3B = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.MultiGstnAuto", this);
				this.getView().addDependent(this._dAutoCalc3B);
			}
			this._dAutoCalc3B.setModel(new JSONModel({
				"type": type,
				"resp": resp,
				"flag": flag
			}), "AutoCalc3bMsg");
			this._dAutoCalc3B.open();
		},

		///BOC by Rakesh on 05.08.2020
		//Bulk Gstn
		onAutoCalcGSTN: function (flag) {
			if (flag === "P") {
				var gstin = this.byId("gstrTabId").getSelectedIndices();
				if (!gstin.length) {
					MessageBox.warning("Please select at least one GSTIN");
					return;
				}
			}

			MessageBox.information("Do you want to initiate Auto Calculate 3B - GSTN?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onAutoCalcDigiGSTBulk(flag);
					}
				}.bind(this)
			});
		},

		onAutoCalcDigiGSTBulk: function (flag) {
			var aGSTIN = [],
				taxPeriod = null;
			if (flag === "P") {
				var oModelData = this.getView().getModel("GSTR3B").getProperty("/"),
					oPath = this.getView().byId("gstrTabId").getSelectedIndices();
				aGSTIN = oPath.map(function (e) {
					return oModelData.resp[e].gstin;
				});
				taxPeriod = this.byId("gstr3bDate").getValue();
			} else {
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					aGSTIN = [this.gstin];
				} else {
					aGSTIN = [this.byId("linkGSTID").getSelectedKey()];
				}
				taxPeriod = this.byId("linkDate").getValue();
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BBulkSaveAutoCalc.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"gstin": aGSTIN,
							"taxPeriod": taxPeriod
						}
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this._openAutoCalc3bMsg(data.resp, "GSTN", flag);
					} else {
						MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseDialogBulkGstn: function () {
			this._dAutoCalc3B.close();
			if (this._dAutoCalc3B.getModel("AutoCalc3bMsg").getProperty("/type") === "DigiGST") {
				this.onSearch3B1();
			} else {
				sap.ui.core.BusyIndicator.show(0);
				var aGSTIN = [],
					taxPeriod = null;
				if (this._dAutoCalc3B.getModel("AutoCalc3bMsg").getProperty("/flag") === "P") {
					var oModelData = this.getView().getModel("GSTR3B").getProperty("/"),
						oPath = this.getView().byId("gstrTabId").getSelectedIndices();
					aGSTIN = oPath.map(function (e) {
						return oModelData.resp[e].gstin;
					});
					taxPeriod = this.byId("gstr3bDate").getValue();
				} else {
					if (this.byId("linkGSTID").getSelectedKey() === "") {
						aGSTIN = [this.gstin];
					} else {
						aGSTIN = [this.byId("linkGSTID").getSelectedKey()];
					}
					taxPeriod = this.byId("linkDate").getValue();
				}
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr3BInterestAutoCalcBulk.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": {
								"gstin": aGSTIN,
								"taxPeriod": taxPeriod
							}
						})
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							if (!this._dIntrestCalcStats) {
								this._dIntrestCalcStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.MultiGstnAuto1", this);
								this.getView().addDependent(this._dIntrestCalcStats);
							}
							this._dIntrestCalcStats.setModel(new JSONModel(data.resp), "BulkGstinModel23");
							this._dIntrestCalcStats.open();
						} else {
							MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
						}
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						MessageBox.error("Error Occured in Auto Calculated 3B-GSTN..! Please try again");
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		onCloseDialogBulkGstn123: function () {
			this._dIntrestCalcStats.close();
		},

		////Copy Data to User Input ----Button
		onCopy1: function (flag) {
			if (flag == "P") {
				var oPath = this.getView().byId("gstrTabId").getSelectedIndices();
				if (!oPath.length) {
					MessageBox.warning("Please select at least one GSTIN");
					return;
				}
			}
			this.screenFlag = flag;
			this.byId("rbgCopyType").setSelectedIndex(1);
			this.byId("rbgCopyType1").setSelectedIndex(0);
			this._oDialogCopy.open();
		},

		onCloseDialogCopy: function (oEvent) {
			this._oDialogCopy.close();
			this.byId("rbgCopyType").setSelectedIndex(1);
			this.byId("rbgCopyType1").setSelectedIndex(0);
		},

		onDialogCopySubmit: function (oEvent) {
			this._oDialogCopy.close();
			var aSelectedIdx = this.byId("rbgCopyType").getSelectedIndex(),
				aSelectedIdx1 = this.byId("rbgCopyType1").getSelectedIndex();

			if (this.screenFlag == "S") {
				var gstin;
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					gstin = this.gstin;
				} else {
					gstin = this.byId("linkGSTID").getSelectedKey();
				}
				var Request = {
					"req": {
						"inwardFlag": aSelectedIdx1 === 0 ? "ASPComp" : (aSelectedIdx1 === 1 ? "AutoCalc" : "Blank"),
						"outwardFlag": aSelectedIdx === 0 ? "ASPComp" : (aSelectedIdx === 1 ? "AutoCalc" : "Blank"),
						"interestAndLateFeeFlag": "AutoCalc",
						"gstin": gstin,
						"taxPeriod": this.byId("linkDate").getValue()
							//"copyFlag": aSelectedIdx === 0 ? "ASPComp" : (aSelectedIdx === 1 ? "AutoCalc" : null) //oCopyFlag
					}
				};
				var GstnsList = "/aspsapapi/getGstr3bCopyToUserInput.do";
			} else {
				var aGSTIN = [];
				var oModelData = this.getView().getModel("GSTR3B").getProperty("/"),
					oPath = this.getView().byId("gstrTabId").getSelectedIndices();
				aGSTIN = oPath.map(function (e) {
					return oModelData.resp[e].gstin;
				});

				var Request = {
					"req": {
						"inwardFlag": aSelectedIdx1 === 0 ? "ASPComp" : (aSelectedIdx1 === 1 ? "AutoCalc" : "Blank"),
						"outwardFlag": aSelectedIdx === 0 ? "ASPComp" : (aSelectedIdx === 1 ? "AutoCalc" : "Blank"),
						"interestAndLateFeeFlag": "AutoCalc",
						"gstinList": aGSTIN,
						"taxPeriod": this.byId("gstr3bDate").getValue()
							//"copyFlag": aSelectedIdx === 0 ? "ASPComp" : (aSelectedIdx === 1 ? "AutoCalc" : null) //oCopyFlag
					}
				};
				var GstnsList = "/aspsapapi/bulkGstr3bCopyToUserInput.do";

			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr) {
						if (data.hdr.status === "S") {
							MessageBox.success(data.resp);
							if (this.screenFlag == "S") {
								this.onSearch();
							} else {
								this.onSearch3B1();
							}
						}
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},
		///////// BOC Rakesh

		onPressAllothersITC2: function (data) {
			var retArr = [],
				curL1Obj = {},
				curL2Obj = {};

			data.resp.forEach(function (e) {
				e.Check = ["APPPR", "APGG2B", "APG2B3BR", "APG2A3BR"].includes(e.subSectionName);
				if (["APGG2B", "APRRU"].includes(e.subSectionName)) {
					e.icon = true;
				}
				if ((["B", "C"].includes(this.optionSelected) && ["APPPR", "APG2B3BR", "APG2A3BR"].includes(e.subSectionName)) ||
					(this.optionSelected === "A" && ["APGG2B", "APRRU"].includes(e.subSectionName)) || ["CTP", "ITCTP"].includes(e.subSectionName)
				) {
					e.edit = false;
				} else {
					e.edit = true;
				}
				switch (e.level) {
				case "L1":
					curL1Obj = e;
					retArr.push(curL1Obj);
					curL1Obj.level2 = [];
					break;
				case "L2":
					curL2Obj = e;
					curL1Obj.level2.push(curL2Obj);
					curL2Obj.level3 = [];
					break;
				case "L3":
					curL2Obj.level3.push(e);
					break;
				}
			}.bind(this));
			this.getView().setModel(new JSONModel(retArr), "AllOtherITC");
		},

		onPressAllothersITC: function () {
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				var gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"entityId": $.sap.entityID,
					"gstin": gstin,
				}
			};
			this.countAll = 0;
			this.edited = false;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getItc10Perc.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.onPressAllothersITC2(data);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressAllothersITC1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"entityId": $.sap.entityID,
					"gstin": gstin
				}
			};
			var AllOtherITC = new JSONModel();
			var AllOtherITC1 = new JSONModel();
			var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getItc10Perc.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.splice(1, 0, {
						"Check": false,
						"Text": true,
						"edit": false
					});
					data.resp.splice(6, 0, {
						"Check": false,
						"Text": true,
						"edit": false
					});
					if (data.resp.length !== 0) {
						var aData = [];
						this.edited = false;

						data.resp.forEach(function (obj) {
							if (obj.subSectionName == "AO_ITC") {
								obj.Header = "All other ITC (Selected data fields will be saved to GSTN)";
								obj.Check = false;
								obj.Text = true;
								obj.edit = false;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								//obj.radioFlag = obj.radioFlag;
								aData.push(obj);
							} else if (obj.subSectionName == "APTPPR") {
								obj.Header = "As per the processed PR";
								obj.Check = true;
								obj.Text = false;
								obj.edit = obj.radioFlag;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								//obj.radioFlag = obj.radioFlag;
								aData.push(obj);
							} else if (obj.subSectionName == "APTG3BR") {
								obj.Header = "";
								obj.Check = false;
								obj.Text = true;
								obj.edit = obj.radioFlag;
								//obj.radioFlag = obj.radioFlag;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								aData.push(obj);
							} else if (obj.subSectionName == "APTG3BR_2APR") {
								obj.Header = "As Per 3B Response(2A Vs PR)";
								obj.Check = true;
								obj.Text = false;
								obj.edit = obj.radioFlag;
								//obj.radioFlag = false;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								aData.push(obj);
							} else if (obj.subSectionName == "APTG3BR_2BPR") {
								obj.Header = "As Per 3B Response(2B Vs PR)";
								obj.Check = true;
								obj.Text = false;
								obj.edit = obj.radioFlag;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								//obj.radioFlag = obj.radioFlag;
							} else if (obj.subSectionName == "APMPITCS36") {
								obj.Header = "";
								obj.Check = false;
								obj.Text = true;
								obj.edit = obj.radioFlag;
								//obj.radioFlag = obj.radioFlag;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
								aData.push(obj);
							} else if (obj.subSectionName == "APMPITCS36_2APR") {
								obj.Header = "As per Maximum Permissible ITC under section 36(4)(2A Vs PR)";
								obj.Check = true;
								obj.Text = false;
								obj.edit = obj.radioFlag;
								obj.radioFlag = obj.radioFlag;
								obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;

								var selDate = this.byId("linkDate").getDateValue();
								var selyear = selDate.getFullYear();
								var curData = new Date();
								var curyear = curData.getFullYear();

								if (selyear <= curyear && (selyear === 2022 || selyear > 2021)) {
									obj.radioEditable = false;
									obj.edit = false;
									obj.enabled = false;
									obj.igstAsp = 0;
									obj.cgstAsp = 0;
									obj.sgstAsp = 0;
									obj.cessAsp = 0;
									obj.igstAutoCal = 0;
									obj.cgstAutoCal = 0;
									obj.sgstAutoCal = 0;
									obj.cessAutoCal = 0;
									obj.igstUser = 0;
									obj.cgstUser = 0;
									obj.sgstUser = 0;
									obj.cessUser = 0;
								} else {
									//obj.enabled = true;
									obj.radioEditable = this.getView().getModel("buttonVis").getData().dataEditable;
									obj.edit = obj.radioFlag;
								}
								aData.push(obj);
							} else if (obj.subSectionName == "ITCRF") {
								obj.Header = "ITC Reversal File (Re-availment of credit)";
								obj.Check = false;
								obj.Text = true;
								obj.edit = this.getView().getModel("buttonVis").getData().dataEditable;
								obj.select = false;
								aData.push(obj);
							} else if (obj.subSectionName == "G3B180DAY_RESP_UPLD") {
								obj.Header = "180 Day Response upload (Re-availment of credit)";
								obj.Check = false;
								obj.Text = true;
								obj.edit = this.getView().getModel("buttonVis").getData().dataEditable;
								obj.select = false;
								aData.push(obj);
							}
						}.bind(this));

						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].radioFlag !== undefined) {
								if (data.resp[i].radioFlag) {
									data.resp[0].igstUser = (+data.resp[i].igstUser + +data.resp[7].igstUser + +data.resp[8].igstUser).toFixed(2);
									data.resp[0].cgstUser = (+data.resp[i].cgstUser + +data.resp[7].cgstUser + +data.resp[8].cgstUser).toFixed(2);
									data.resp[0].sgstUser = (+data.resp[i].sgstUser + +data.resp[7].sgstUser + +data.resp[8].sgstUser).toFixed(2);
									data.resp[0].cessUser = (+data.resp[i].cessUser + +data.resp[7].cessUser + +data.resp[8].cessUser).toFixed(2);

									data.resp[0].igstAutoCal = (+data.resp[i].igstAutoCal + +data.resp[7].igstAutoCal + +data.resp[8].igstAutoCal).toFixed(2);
									data.resp[0].cgstAutoCal = (+data.resp[i].cgstAutoCal + +data.resp[7].cgstAutoCal + +data.resp[8].cgstAutoCal).toFixed(2);
									data.resp[0].sgstAutoCal = (+data.resp[i].sgstAutoCal + +data.resp[7].sgstAutoCal + +data.resp[8].sgstAutoCal).toFixed(2);
									data.resp[0].cessAutoCal = (+data.resp[i].cessAutoCal + +data.resp[7].cessAutoCal + +data.resp[8].cessAutoCal).toFixed(2);

									/*data.resp[0].igstAsp = Number(data.resp[i].igstAsp) + Number(data.resp[7].igstAsp) + Number(data.resp[8].igstAsp);
									data.resp[0].cgstAsp = Number(data.resp[i].cgstAsp) + Number(data.resp[7].cgstAsp) + Number(data.resp[8].cgstAsp);
									data.resp[0].sgstAsp = Number(data.resp[i].sgstAsp) + Number(data.resp[7].sgstAsp) + Number(data.resp[8].sgstAsp);
									data.resp[0].cessAsp = Number(data.resp[i].cessAsp) + Number(data.resp[7].cessAsp) + Number(data.resp[8].cessAsp);*/
								}
							}
						}
						data.resp[0].igstAsp = Number(data.resp[2].igstAsp) + Number(data.resp[7].igstAsp) + Number(data.resp[8].igstAsp);
						data.resp[0].cgstAsp = Number(data.resp[2].cgstAsp) + Number(data.resp[7].cgstAsp) + Number(data.resp[8].cgstAsp);
						data.resp[0].sgstAsp = Number(data.resp[2].sgstAsp) + Number(data.resp[7].sgstAsp) + Number(data.resp[8].sgstAsp);
						data.resp[0].cessAsp = Number(data.resp[2].cessAsp) + Number(data.resp[7].cessAsp) + Number(data.resp[8].cessAsp);

						AllOtherITC.setData(data.resp);
						oTaxReGstnView.setModel(AllOtherITC, "AllOtherITC");
						oTaxReGstnView.getModel("AllOtherITC").refresh(true);
						AllOtherITC1.setData(data.resp);
						oTaxReGstnView.setModel(AllOtherITC1, "AllOtherITC1");
						oTaxReGstnView.getModel("AllOtherITC1").refresh(true);
					} else {
						AllOtherITC.setData([]);
						oTaxReGstnView.setModel(AllOtherITC, "AllOtherITC");
						oTaxReGstnView.getModel("AllOtherITC").refresh(true);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},
		/*code ended by Arun on 19/11/2021*/

		//====== on edit of records //
		onPressEditRecords: function (oevt) {
			MessageBox.information("Do you want to Edit User Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						var oData = this.getView().getModel("AllOtherITC").getProperty("/");
						if (oData.length) {
							oData.forEach(function (obj) {
								if ((obj.subSectionName !== "AO_ITC" && obj.select === true) || obj.subSectionName === "ITCRF") {
									obj.edit = true;
								}
							});
							this.getView().getModel("AllOtherITC").refresh(true);
						}
					}
				}.bind(this)
			});
		},

		//=== on changing amount ======//
		onChangeAmount: function (oEvt) {
			var vPath = oEvt.getSource().getBindingContext("AllOtherITC"),
				oBject = this.getView().getModel("AllOtherITC").getObject(vPath.sPath),
				val = oEvt.getSource().getValue().toString(),
				val1 = val.split(".");

			if (val1[1] !== undefined) {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			var reg = /^[A-Za-z]+$/;
			if (val.match(reg)) {
				MessageBox.error("Alphabets are not allowed");
				return;
			}

			var vTigstUser,
				vTcgstUser,
				vTsgstUser,
				vTcesstUser;
			this.edited = true;

			var retArr = this.getView().getModel("AllOtherITC").getData();
			for (var i = 0; i < retArr.length; i++) {
				vTigstUser = 0;
				vTcgstUser = 0;
				vTsgstUser = 0;
				vTcesstUser = 0;
				if (retArr[i].level2.length !== 0) {
					if (retArr[i].subSectionName === "CTP") {
						for (var j = 0; j < retArr[i].level2.length; j++) {
							if (retArr[i].level2[j].radioFlag) {
								vTigstUser = Number(retArr[i].level2[j].igstUser);
								vTcgstUser = Number(retArr[i].level2[j].cgstUser);
								vTsgstUser = Number(retArr[i].level2[j].sgstUser);
								vTcesstUser = Number(retArr[i].level2[j].cessUser);
							}
						}
						retArr[i].igstUser = Number(vTigstUser).toFixed(2);
						retArr[i].cgstUser = Number(vTcgstUser).toFixed(2);
						retArr[i].sgstUser = Number(vTsgstUser).toFixed(2);
						retArr[i].cessUser = Number(vTcesstUser).toFixed(2);
					} else {
						for (var j = 0; j < retArr[i].level2.length; j++) {
							vTigstUser += Number(retArr[i].level2[j].igstUser);
							vTcgstUser += Number(retArr[i].level2[j].cgstUser);
							vTsgstUser += Number(retArr[i].level2[j].sgstUser);
							vTcesstUser += Number(retArr[i].level2[j].cessUser);
						}
						retArr[i].igstUser = Number(vTigstUser).toFixed(2);
						retArr[i].cgstUser = Number(vTcgstUser).toFixed(2);
						retArr[i].sgstUser = Number(vTsgstUser).toFixed(2);
						retArr[i].cessUser = Number(vTcesstUser).toFixed(2);
					}
				}
			}
			this.getView().getModel("AllOtherITC").refresh(true);
		},

		onChangeAmount12345: function (oEvt) {
			var vPath = oEvt.getSource().getBindingContext("AllOtherITC"),
				oBject = this.getView().getModel("AllOtherITC").getObject(vPath.sPath),
				val = oEvt.getSource().getValue().toString(),
				val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!val.match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			this.edited = true;
			var data1 = this.getView().getModel("AllOtherITC").getData();
			for (var i = 0; i < data1.length; i++) {
				if (data1[i].subSectionName === "APTPPR" || data1[i].subSectionName === "ITCRF" || data1[i].subSectionName ===
					"G3B180DAY_RESP_UPLD" || data1[i].subSectionName === "APTG3BR_2APR" || data1[i].subSectionName === "APTG3BR_2BPR" || data1[i].subSectionName ===
					"APMPITCS36_2APR") {
					if (data1[i].radioFlag) {
						data1[0].igstUser = (Number(data1[i].igstUser) + Number(data1[7].igstUser) + Number(data1[8].igstUser)).toFixed(2);
						data1[0].cgstUser = (Number(data1[i].cgstUser) + Number(data1[7].cgstUser) + Number(data1[8].cgstUser)).toFixed(2);
						data1[0].sgstUser = (Number(data1[i].sgstUser) + Number(data1[7].sgstUser) + Number(data1[8].sgstUser)).toFixed(2);
						data1[0].cessUser = (Number(data1[i].cessUser) + Number(data1[7].cessUser) + Number(data1[8].cessUser)).toFixed(2);
					}
				}
			}
			this.getView().getModel("AllOtherITC").refresh(true);
		},

		//=== on changing amount ======//
		onChangeAmount1: function (oEvt) {
			var vPath = oEvt.getSource().getBindingContext("OtherReversal"),
				oBject = this.getView().getModel("OtherReversal").getObject(vPath.sPath),
				val = oEvt.getSource().getValue().toString(),
				val1 = val.split(".");

			if (val1[1] !== undefined) {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!val.match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			var vTigstUser = 0;
			var vTcgstUser = 0;
			var vTsgstUser = 0;
			var vTcesstUser = 0;
			var vTigstUser2 = 0;
			var vTcgstUser2 = 0;
			var vTsgstUser2 = 0;
			var vTcesstUser2 = 0;
			var vTigstUser3 = 0;
			var vTcgstUser3 = 0;
			var vTsgstUser3 = 0;
			var vTcesstUser3 = 0;
			this.editedOth = true;
			var retArr = this.getView().getModel("OtherReversal").getData();
			for (var i = 1; i < retArr.length; i++) {
				vTigstUser += Number(retArr[i].igstUser);
				vTcgstUser += Number(retArr[i].cgstUser);
				vTsgstUser += Number(retArr[i].sgstUser);
				vTcesstUser += Number(retArr[i].cessUser);
				if (retArr[i].level2.length !== 0) {
					for (var j = 0; j < retArr[i].level2.length; j++) {
						vTigstUser2 += Number(retArr[i].level2[j].igstUser);
						vTcgstUser2 += Number(retArr[i].level2[j].cgstUser);
						vTsgstUser2 += Number(retArr[i].level2[j].sgstUser);
						vTcesstUser2 += Number(retArr[i].level2[j].cessUser);
					}
					retArr[3].igstUser = Number(vTigstUser2).toFixed(2);
					retArr[3].cgstUser = Number(vTcgstUser2).toFixed(2);
					retArr[3].sgstUser = Number(vTsgstUser2).toFixed(2);
					retArr[3].cessUser = Number(vTcesstUser2).toFixed(2);
				}
			}
			retArr[0].igstUser = Number(vTigstUser).toFixed(2);
			retArr[0].cgstUser = Number(vTcgstUser).toFixed(2);
			retArr[0].sgstUser = Number(vTsgstUser).toFixed(2);
			retArr[0].cessUser = Number(vTcesstUser).toFixed(2);
			this.getView().getModel("OtherReversal").refresh(true);

			for (var i = 1; i < retArr.length; i++) {
				vTigstUser3 += Number(retArr[i].igstUser);
				vTcgstUser3 += Number(retArr[i].cgstUser);
				vTsgstUser3 += Number(retArr[i].sgstUser);
				vTcesstUser3 += Number(retArr[i].cessUser);
			}
			retArr[0].igstUser = Number(vTigstUser3).toFixed(2);
			retArr[0].cgstUser = Number(vTcgstUser3).toFixed(2);
			retArr[0].sgstUser = Number(vTsgstUser3).toFixed(2);
			retArr[0].cessUser = Number(vTcesstUser3).toFixed(2);
			this.getView().getModel("OtherReversal").refresh(true);
		},

		onChangeAmount3842: function (oEvt) {
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("Rule3842");
			var oBject = oView.getModel("Rule3842").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!val.match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			var vTigstUser = 0;
			var vTcgstUser = 0;
			var vTsgstUser = 0;
			var vTcesstUser = 0;
			var vTigstUser2 = 0;
			var vTcgstUser2 = 0;
			var vTsgstUser2 = 0;
			var vTcesstUser2 = 0;
			var vTigstUser3 = 0;
			var vTcgstUser3 = 0;
			var vTsgstUser3 = 0;
			var vTcesstUser3 = 0;
			this.edited3842 = true;
			var retArr = oView.getModel("Rule3842").getData();
			for (var i = 1; i < retArr.length; i++) {
				vTigstUser += Number(retArr[i].igstUser);
				vTcgstUser += Number(retArr[i].cgstUser);
				vTsgstUser += Number(retArr[i].sgstUser);
				vTcesstUser += Number(retArr[i].cessUser);
				if (retArr[i].level2.length !== 0) {
					for (var j = 0; j < retArr[i].level2.length; j++) {
						vTigstUser2 += Number(retArr[i].level2[j].igstUser);
						vTcgstUser2 += Number(retArr[i].level2[j].cgstUser);
						vTsgstUser2 += Number(retArr[i].level2[j].sgstUser);
						vTcesstUser2 += Number(retArr[i].level2[j].cessUser);
					}
					retArr[2].igstUser = Number(vTigstUser2).toFixed(2);
					retArr[2].cgstUser = Number(vTcgstUser2).toFixed(2);
					retArr[2].sgstUser = Number(vTsgstUser2).toFixed(2);
					retArr[2].cessUser = Number(vTcesstUser2).toFixed(2);
				}
			}
			retArr[0].igstUser = Number(vTigstUser).toFixed(2);
			retArr[0].cgstUser = Number(vTcgstUser).toFixed(2);
			retArr[0].sgstUser = Number(vTsgstUser).toFixed(2);
			retArr[0].cessUser = Number(vTcesstUser).toFixed(2);
			oView.getModel("Rule3842").refresh(true);

			for (var i = 1; i < retArr.length; i++) {
				vTigstUser3 += Number(retArr[i].igstUser);
				vTcgstUser3 += Number(retArr[i].cgstUser);
				vTsgstUser3 += Number(retArr[i].sgstUser);
				vTcesstUser3 += Number(retArr[i].cessUser);
			}
			retArr[0].igstUser = Number(vTigstUser3).toFixed(2);
			retArr[0].cgstUser = Number(vTcgstUser3).toFixed(2);
			retArr[0].sgstUser = Number(vTsgstUser3).toFixed(2);
			retArr[0].cessUser = Number(vTcesstUser3).toFixed(2);
			oView.getModel("Rule3842").refresh(true);
		},

		//====== on selecting checkbox == //
		onSelectCheckBoxAmount: function (oEvt) {
			var oView = this.getView();
			this.countAll++
				// var vPath = oEvt.getSource().getBindingContext("AllOtherITC");
				// var oBject = oView.getModel("AllOtherITC").getObject(vPath.sPath);
				if (this.countAll > 1) {
					this.edited = true;
				}

			var data1 = oView.getModel("AllOtherITC").getData();
			for (var i = 0; i < data1.length; i++) {
				if (data1[i].level2.length !== 0) {
					data1[i].level2.forEach(function (sobj) {
						if (sobj.radioFlag === true) {
							data1[0].igstUser = (Number(sobj.igstUser)).toFixed(2);
							data1[0].cgstUser = (Number(sobj.cgstUser)).toFixed(2);
							data1[0].sgstUser = (Number(sobj.sgstUser)).toFixed(2);
							data1[0].cessUser = (Number(sobj.cessUser)).toFixed(2);
						}
					});
				}
			}
			oView.getModel("AllOtherITC").refresh(true);
		},

		//====== on selecting checkbox == //
		onSelectRD: function (oEvt) {
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("OtherReversal");
			var oBject = oView.getModel("OtherReversal").getObject(vPath.sPath);
			var vTigstUser = 0;
			var vTcgstUser = 0;
			var vTsgstUser = 0;
			var vTcesstUser = 0;
			var igstUser = 0;
			var cgstUser = 0;
			var sgstUser = 0;
			var cesstUser = 0;
			this.editedOth = true;
			for (var i = 0; i < oView.getModel("OtherReversal").getData().length; i++) {
				oView.getModel("OtherReversal").getData()[i].selectRD = false;
				oView.getModel("OtherReversal").getData()[i].edit = false;
				if (oView.getModel("OtherReversal").getData()[i].subSectionName === "OR_PR" ||
					oView.getModel("OtherReversal").getData()[i].subSectionName === "OR_RFU" ||
					oView.getModel("OtherReversal").getData()[i].subSectionName === "OR_180RRFU") {
					oView.getModel("OtherReversal").getData()[i].rowName = "";
					if (oBject.subSectionName == oView.getModel("OtherReversal").getData()[i].subSectionName) {
						oView.getModel("OtherReversal").getData()[i].selectRD = true;
						oView.getModel("OtherReversal").getData()[i].rowName = oView.getModel("OtherReversal").getData()[i].subSectionName;
						oView.getModel("OtherReversal").getData()[i].edit = true;
						vTigstUser += Number(oView.getModel("OtherReversal").getData()[i].igstUser);
						vTcgstUser += Number(oView.getModel("OtherReversal").getData()[i].cgstUser);
						vTsgstUser += Number(oView.getModel("OtherReversal").getData()[i].sgstUser);
						vTcesstUser += Number(oView.getModel("OtherReversal").getData()[i].cessUser);
					}
				}
			}
			oView.getModel("OtherReversal").getData()[0].igstUser = Number(vTigstUser).toFixed(2);
			oView.getModel("OtherReversal").getData()[0].cgstUser = Number(vTcgstUser).toFixed(2);
			oView.getModel("OtherReversal").getData()[0].sgstUser = Number(vTsgstUser).toFixed(2);
			oView.getModel("OtherReversal").getData()[0].cessUser = Number(vTcesstUser).toFixed(2);
			oView.getModel("OtherReversal").refresh(true);
		},

		//= on save changes ==//
		onPressSaveRecords: function () {
			var oView = this.getView();
			var TabData = oView.getModel("AllOtherITC").getData();
			for (var j = 0; j < TabData.length; j++) {
				var reg = /^[A-Za-z]+$/;
				if (TabData[j].igstUser === undefined) {
					TabData[j].igstUser = 0;
				}
				var val = Number(TabData[j].igstUser).toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (val.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				if (TabData[j].cgstUser === undefined) {
					TabData[j].cgstUser = 0;
				}
				var valuserInputIgst = Number(TabData[j].cgstUser).toString();
				var val1userInputIgst = valuserInputIgst.split(".");
				if (val1userInputIgst[1] !== undefined) {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputIgst[1] !== "" && val1userInputIgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (valuserInputIgst.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				if (TabData[j].sgstUser === undefined) {
					TabData[j].sgstUser = 0;
				}
				var userInputCgst = Number(TabData[j].sgstUser).toString();
				var val1userInputCgst = userInputCgst.split(".");
				if (val1userInputCgst[1] !== undefined) {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputCgst[1] !== "" && val1userInputCgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (userInputCgst.match(reg)) {
					MessageBox.error(" Alphabets are not allowed");
					return;
				}
				if (TabData[j].cessUser === undefined) {
					TabData[j].cessUser = 0;
				}
				var userInputSgst = Number(TabData[j].cessUser).toString();
				var userInputSgst1 = userInputSgst.split(".");
				if (userInputSgst1[1] !== undefined) {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (userInputSgst1[1] !== "" && userInputSgst1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (userInputSgst.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				var sectionName = null;
				if (TabData[j].level2.length) {
					TabData[j].level2.forEach(function (el) {
						if ((+el.cgstUser !== +el.sgstUser) && el.edit && !sectionName) {
							sectionName = el.sectionName;
						}
					});
				}
				if (sectionName) {
					MessageBox.error("CGST and SGST cannot be different for " + sectionName);
					return;
				}
			}

			if (oView.getModel("AllOtherITC").getData().length != 0) {
				var gstin;
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					gstin = this.gstin;
				} else {
					gstin = this.byId("linkGSTID").getSelectedKey();
				}
				var InputList = [];
				var ComputeList = [];
				oView.getModel("AllOtherITC").getData().forEach(function (obj) {
					InputList.push({
						"cess": Number(obj.cessUser).toFixed(2),
						"cgst": Number(obj.cgstUser).toFixed(2),
						"igst": Number(obj.igstUser).toFixed(2),
						"sectionName": obj.sectionName,
						"sgst": Number(obj.sgstUser).toFixed(2),
						"subSectionName": obj.subSectionName,
						"taxableVal": "0",
						"rowName": obj.subSectionName,
						"radioFlag": obj.radioFlag
					});
					if (obj.level2.length !== 0) {
						obj.level2.forEach(function (sobj) {
							InputList.push({
								"cess": Number(sobj.cessUser).toFixed(2),
								"cgst": Number(sobj.cgstUser).toFixed(2),
								"igst": Number(sobj.igstUser).toFixed(2),
								"sectionName": sobj.sectionName,
								"sgst": Number(sobj.sgstUser).toFixed(2),
								"subSectionName": sobj.subSectionName,
								"taxableVal": "0",
								"rowName": sobj.subSectionName,
								"radioFlag": sobj.radioFlag
							});
						});
					}
				}.bind(this));

				var request = {
					"req": {
						"taxPeriod": this.byId("linkDate").getValue(),
						"gstin": gstin,
						"status": "SAVE",
						"userInputList": InputList
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr3BITC10PerSaveUserChanges.do",
						contentType: "application/json",
						data: JSON.stringify(request)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr && data.hdr.status === "S") {
							MessageBox.success(
								"Changes saved successfully. Kindly re-compute the GSTR 3B by clicking on button 'Auto Calculate3B - DigiGST' to refresh values in table 4(B)."
							);
							this.byId("dpAllOtherITC").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						} else {
							MessageBox.error("Error Occured While Saving the Changes");
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error Occured While Saving the Changes");
					});
			}
		},

		//= on save changes ==//
		onPressSaveRecords1: function () {
			var oView = this.getView();
			var oView = this.getView();
			var TabData = oView.getModel("OtherReversal").getData();
			for (var j = 0; j < TabData.length; j++) {
				//////userInputTaxableVal/////
				var val = TabData[j].igstUser.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}

				////////////////////////////////
				var valuserInputIgst = TabData[j].cgstUser.toString();
				var val1userInputIgst = valuserInputIgst.split(".");
				if (val1userInputIgst[1] !== undefined) {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputIgst[1] !== "" && val1userInputIgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg1 = /^[0-9]*\.?[0-9]*$/;
				if (!valuserInputIgst.match(reg1)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				////////////////////////////////
				var userInputCgst = TabData[j].sgstUser.toString();
				var val1userInputCgst = userInputCgst.split(".");
				if (val1userInputCgst[1] !== undefined) {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputCgst[1] !== "" && val1userInputCgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg2 = /^[0-9]*\.?[0-9]*$/;
				if (!userInputCgst.match(reg2)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				var userInputSgst = TabData[j].cessUser.toString();
				var userInputSgst1 = userInputSgst.split(".");
				if (userInputSgst1[1] !== undefined) {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (userInputSgst1[1] !== "" && userInputSgst1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				var reg3 = /^[0-9]*\.?[0-9]*$/;
				if (!userInputSgst.match(reg3)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
			}

			if (oView.getModel("OtherReversal").getData().length != 0) {
				var gstin;
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					gstin = this.gstin;
				} else {
					gstin = this.byId("linkGSTID").getSelectedKey();
				}
				var InputList = [];
				var ComputeList = [];
				oView.getModel("OtherReversal").getData().forEach(function (obj) {
					InputList.push({
						"cess": Number(obj.cessUser).toFixed(2),
						"cgst": Number(obj.cgstUser).toFixed(2),
						"igst": Number(obj.igstUser).toFixed(2),
						"sectionName": obj.sectionName,
						"sgst": Number(obj.sgstUser).toFixed(2),
						"subSectionName": obj.subSectionName,
						"taxableVal": "0",
					});
					if (obj.level2.length !== 0) {
						obj.level2.forEach(function (sobj) {
							InputList.push({
								"cess": Number(sobj.cessUser).toFixed(2),
								"cgst": Number(sobj.cgstUser).toFixed(2),
								"igst": Number(sobj.igstUser).toFixed(2),
								"sectionName": sobj.sectionName,
								"sgst": Number(sobj.sgstUser).toFixed(2),
								"subSectionName": sobj.subSectionName,
								"taxableVal": "0",
							});
						});
					}
				}.bind(this));
				var request = {
					"req": {
						"taxPeriod": this.byId("linkDate").getValue(),
						"gstin": gstin,
						//"status": "SAVE",
						"userInputList": InputList
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/save3bOtherReverals.do",
						contentType: "application/json",
						data: JSON.stringify(request)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr && data.hdr.status === "S") {
							MessageBox.success("Changes saved successfully.");
							this.byId("Other").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						} else {
							MessageBox.error("Error Occured While Saving the Changes");
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error Occured While Saving the Changes");
					});
			}
		},

		Refreshfun: function () {
			var TableData = this.getView().getModel("AllOtherITC").getProperty("/");
			for (var i = 0; i < TableData.length; i++) {
				if (TableData[i].subSectionName === "APTG3BR" ||
					TableData[i].subSectionName === "APMPITCS36") {
					TableData[i].radioFlag = false;
					TableData[i].edit = false;
				} else if (TableData[i].subSectionName === "ITCRF") {
					TableData[i].edit = false;
				} else if (TableData[i].subSectionName === "APTPPR") {
					TableData[i].radioFlag = true;
					TableData[i].edit = false;
				}
			}
			this.getView().getModel("AllOtherITC").refresh(true);
		},

		onReset: function () {
			MessageBox.information("Do you want to Clear User Data ?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						var oModel = this.getView().getModel("AllOtherITC"),
							oData = oModel.getProperty("/");

						oData.forEach(function (e) {
							e.igstUser = Number("0.00");
							e.cgstUser = Number("0.00");
							e.sgstUser = Number("0.00");
							e.cessUser = Number("0.00");
						});
						oModel.refresh(true);
					}
				}.bind(this)
			});
		},
		//=========================================== code ended by chaithra on 6/11/2020 =============================//
		//========== Added by chaithra on 3/12/2020 for GSTR-3B Liability Set off =======//

		onPressothersReversal: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"entityId": $.sap.entityID,
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/get3bOtherReverals.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					for (var i = 0; i < data.resp.length; i++) {
						var ele = data.resp[i];
						if (data.resp[i].subSectionName === "IR_OTHERS") {
							data.resp[i].edit = false;
						} else if (data.resp[i].subSectionName === "OR_175") {
							data.resp[i].edit = false;
						} else {
							data.resp[i].edit = this.getView().getModel("buttonVis").getData().dataEditable;
						}

						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							curL2Obj.level3.push(ele);
						}
					}

					var aData = [];
					this.editedOth = false;
					var vTigstUser = 0;
					var vTcgstUser = 0;
					var vTsgstUser = 0;
					var vTcesstUser = 0;
					var vTigstUser2 = 0;
					var vTcgstUser2 = 0;
					var vTsgstUser2 = 0;
					var vTcesstUser2 = 0;
					var vTigstUserasp = 0;
					var vTcgstUserasp = 0;
					var vTsgstUserasp = 0;
					var vTcesstUserasp = 0;
					var vTigstUserasp1 = 0;
					var vTcgstUserasp1 = 0;
					var vTsgstUserasp1 = 0;
					var vTcesstUserasp1 = 0;

					for (var i = 1; i < retArr.length; i++) {
						if (retArr[i].level2.length !== 0) {
							for (var j = 0; j < retArr[i].level2.length; j++) {
								vTigstUser2 += Number(retArr[i].level2[j].igstUser);
								vTcgstUser2 += Number(retArr[i].level2[j].cgstUser);
								vTsgstUser2 += Number(retArr[i].level2[j].sgstUser);
								vTcesstUser2 += Number(retArr[i].level2[j].cessUser);

								vTigstUserasp += Number(retArr[i].level2[j].igstAsp);
								vTcgstUserasp += Number(retArr[i].level2[j].cgstAsp);
								vTsgstUserasp += Number(retArr[i].level2[j].sgstAsp);
								vTcesstUserasp += Number(retArr[i].level2[j].cessAsp);
							}
							retArr[i].igstUser = Number(vTigstUser2).toFixed(2);
							retArr[i].cgstUser = Number(vTcgstUser2).toFixed(2);
							retArr[i].sgstUser = Number(vTsgstUser2).toFixed(2);
							retArr[i].cessUser = Number(vTcesstUser2).toFixed(2);

							retArr[i].igstAsp = Number(vTigstUserasp).toFixed(2);
							retArr[i].cgstAsp = Number(vTcgstUserasp).toFixed(2);
							retArr[i].sgstAsp = Number(vTsgstUserasp).toFixed(2);
							retArr[i].cessAsp = Number(vTcesstUserasp).toFixed(2);
						}
						vTigstUser += Number(retArr[i].igstUser);
						vTcgstUser += Number(retArr[i].cgstUser);
						vTsgstUser += Number(retArr[i].sgstUser);
						vTcesstUser += Number(retArr[i].cessUser);

						vTigstUserasp1 += Number(retArr[i].igstAsp);
						vTcgstUserasp1 += Number(retArr[i].cgstAsp);
						vTsgstUserasp1 += Number(retArr[i].sgstAsp);
						vTcesstUserasp1 += Number(retArr[i].cessAsp);
					}
					retArr[0].igstUser = (Number(vTigstUser)).toFixed(2);
					retArr[0].cgstUser = (Number(vTcgstUser)).toFixed(2);
					retArr[0].sgstUser = (Number(vTsgstUser)).toFixed(2);
					retArr[0].cessUser = (Number(vTcesstUser)).toFixed(2);

					retArr[0].igstAsp = (Number(vTigstUserasp1)).toFixed(2);
					retArr[0].cgstAsp = (Number(vTcgstUserasp1)).toFixed(2);
					retArr[0].sgstAsp = (Number(vTsgstUserasp1)).toFixed(2);
					retArr[0].cessAsp = (Number(vTcesstUserasp1)).toFixed(2);

					this.getView().setModel(new JSONModel(retArr), "OtherReversal");
					this.getView().getModel("OtherReversal").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onPreeGSTNSetOff: function (oEvt) {
			var oPath = oEvt.getSource().getBindingContext("MainLiability").getPath();
			this.oBject = this.getView().getModel("MainLiability").getObject(oPath);
			this.getView().byId("idDyGstr3bSetoff").setVisible(false);
			this.getView().byId("idDyliablityId").setVisible(true);

			this.gstinLia = this.oBject.gstin;
			this.vgstinSetOff = this.oBject.gstin;
			this.byId("id_SetOffGstin1").setSelectedKey(this.gstinLia);
			this.byId("id_SetOFfTaxPeriod1").setValue(this.byId("id_SetOFfTaxPeriod").getValue());
			var Request = {
				"req": {
					"gstin": this.gstinLia,
					"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue()
				}
			};
			this.onPreeGSTNSetOff1(Request);
			this.buttonVis1();
		},

		onGoSetOff1: function () {
			var Request = {
				"req": {
					"gstin": this.byId("id_SetOffGstin1").getSelectedKey(),
					"taxPeriod": this.byId("id_SetOFfTaxPeriod1").getValue()
				}
			};
			this.onPreeGSTNSetOff1(Request);
			this.buttonVis1();
		},

		onClearSetOff1: function () {
			this.byId("id_SetOFfTaxPeriod1").setDateValue(new Date());
			this.byId("id_SetOffGstin1").setSelectedKey(this.gstinLia);
			this.onGoSetOff1();
		},

		onPreeGSTNSetOff1: function (Request) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/get3bLiabilitySetOff.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data1, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.isRule86B = data1.resp.isRule86B;

					if (data1.resp.liabilitySetoffStatus !== "SAVED") {
						var data = data1.resp;
						data.ledgerDetails.forEach(function (item) {
							item.Total = (+item.i + +item.c + +item.s + +item.cs) || 0;
							if (["tx", "currMonthUtil", "clsBal"].includes(item.desc)) {
								item.Total1 = (+item.cri + +item.crc + +item.crs + +item.crcs) || 0;
							}
							if (["tx", "currMonthUtil", "clsBal"].includes(item.desc)) {
								item.Total2 = (+item.nlbIgst + +item.nlbCgst + +item.nlbSgst + +item.nlbCess) || 0;
							}
						});

						data.gstr3bDetails.forEach(function (item, j) {
							if (!data1.resp.isRule86B) {
								item.otrci2A = "NA";
								item.otrci2B = "NA";
							}
							item.intTax = ["Integrated Tax", "Central Tax", "State/UT Tax"].includes(item.desc);
							item.ceTax = ["Integrated Tax", "Central Tax"].includes(item.desc);
							item.suTax = ["Integrated Tax", "State/UT Tax"].includes(item.desc);
							item.cTax = ["Cess"].includes(item.desc);
							item.edit = false;

							item.int = "None";
							item.ce = "None";
							item.su = "None";
							item.cs = "None";

							if (item.pdi === undefined) {
								item.pdi = 0;
							}

							if (item.pdc === undefined) {
								item.pdc = 0;
							}

							if (item.pds === undefined) {
								item.pds = 0;
							}

							if (item.pdcs === undefined) {
								item.pdcs = 0;
							}

							if (this.isRule86B) {
								var otrci_2B = item.otrci2B;
							} else {
								var otrci_2B = item.netOthRecTaxPayable2i;
							}
							item.Total7 = (+otrci_2B - +item.pdi - +item.pdc - +item.pds - +item.pdcs) || 0;
							if (item.Total7 < 1) {
								item.Total7 = 0;
							}
							item.Total7 = item.Total7.toFixed(2);
							item.rci9 = item.rci9;

							if (j === 0) {
								var val = (+item.Total7 + +item.rci9) || 0,
									val1 = data.ledgerDetails[0].i || 0,
									val2 = data.ledgerDetails[1].i || 0,
									val3 = data.ledgerDetails[2].i || 0,
									col11 = +item.inti10 || 0,
									col13 = +item.lateFee12 || 0,
									minimumVal = Math.min(val, val1),
									minimumVal1 = Math.min(col11, val2),
									minimumVal2 = Math.min(col13, val3);
								item.Total14 = (minimumVal + minimumVal1 + minimumVal2).toFixed(2);

								var minIT = Math.min(val, val1),
									a2 = val - minIT,
									minIT1 = Math.min(col11, val2),
									b2 = col11 - minIT1,
									minIT2 = Math.min(col13, val3),
									c2 = col13 - minIT2;
								if (a2 < 0) {
									a2 = 0;
								} else if (b2 < 0) {
									b2 = 0;
								} else if (c2 < 0) {
									c2 = 0;
								}
								item.Total15 = (a2 + b2 + c2).toFixed(2);
							}

							if (j === 1) {
								var val78 = (+item.Total7 + +item.rci9) || 0,
									val12 = data.ledgerDetails[0].c || 0,
									val23 = data.ledgerDetails[1].c || 0,
									val33 = data.ledgerDetails[2].c || 0,
									col111 = +item.inti10 || 0,
									col133 = +item.lateFee12 || 0,
									minimumValQ = Math.min(val78, val12),
									minimumVal1Q = Math.min(col111, val23),
									minimumVal2Q = Math.min(col133, val33);
								item.Total14 = (minimumValQ + minimumVal1Q + minimumVal2Q).toFixed(2);

								var minITa = Math.min(val78, val12),
									a1 = val78 - minITa,
									minIT1b = Math.min(col111, val23),
									b1 = col111 - minIT1b,
									minIT2c = Math.min(col133, val33),
									c1 = col133 - minIT2c;
								if (a1 < 0) {
									a1 = 0;
								} else if (b1 < 0) {
									b1 = 0;
								} else if (c1 < 0) {
									c1 = 0;
								}
								item.Total15 = (a1 + b1 + c1).toFixed(2);
							}

							if (j === 2) {
								var a = (+item.Total7 + +item.rci9) || 0,
									b = data.ledgerDetails[0].s || 0,
									c = data.ledgerDetails[1].s || 0,
									d = data.ledgerDetails[2].s || 0,
									e = +item.inti10 || 0,
									f = +item.lateFee12 || 0,
									minimumVala = Math.min(a, b),
									minimumVal1b = Math.min(e, c),
									minimumVal2c = Math.min(f, d);
								item.Total14 = (minimumVala + minimumVal1b + minimumVal2c).toFixed(2);

								var minITa1 = Math.min(a, b),
									a12 = a - minITa1,
									minIT1b1 = Math.min(e, c),
									b12 = e - minIT1b1,
									minIT2c1 = Math.min(f, d),
									c12 = f - minIT2c1;
								if (a12 < 0) {
									a12 = 0;
								} else if (b12 < 0) {
									b12 = 0;
								} else if (c12 < 0) {
									c12 = 0;
								}
								item.Total15 = (a12 + b12 + c12).toFixed(2);
							}

							if (j === 3) {
								var a1 = (+item.Total7 + +item.rci9) || 0,
									b1 = data.ledgerDetails[0].cs || 0,
									c1 = data.ledgerDetails[1].cs || 0,
									d1 = data.ledgerDetails[2].cs || 0,
									e1 = +item.inti10 || 0,
									f1 = +item.lateFee12 || 0,
									minimumVala1 = Math.min(a1, b1),
									minimumVal1b1 = Math.min(e1, c1),
									minimumVal2c1 = Math.min(f1, d1);
								item.Total14 = (minimumVala1 + minimumVal1b1 + minimumVal2c1).toFixed(2);

								var minITa14 = Math.min(a1, b1),
									a124 = a1 - minITa14,
									minIT1b14 = Math.min(e1, c1),
									b124 = e1 - minIT1b14,
									minIT2c14 = Math.min(f1, d1),
									c124 = f1 - minIT2c14;
								if (a124 < 0) {
									a124 = 0;
								} else if (b124 < 0) {
									b124 = 0;
								} else if (c124 < 0) {
									c124 = 0;
								}
								item.Total15 = (a124 + b124 + c124).toFixed(2);
							}
						}.bind(this));
					} else {
						this._displayLiabilities(data1.resp);
					}
					this.getView().byId("idDyliablityId").setModel(new JSONModel(data1.resp), "LiabillitySetOff");
					if (data.message !== "" && data.message !== undefined) {
						MessageBox.error(data.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error(jqXHR.responseJSON.hdr.message);
				}.bind(this));
		},

		onBackGstr3bSetOff: function () {
			this.getView().byId("idDyliablityId").setVisible(false);
			this.getView().byId("idDyGstr3bSetoff").setVisible(true);
			this.onGetSetOffEntityData();
		},

		/**
		 * Method to Compute and save Set Off Liability
		 */
		onComputeSaveSetoffLiability: function () {
			MessageBox.information("Do you want to compute Liability Set Off details for selected GSTIN?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._confirmSetoffComputeSave();
					}
				}.bind(this)
			});
		},

		_confirmSetoffComputeSave: function () {
			var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
				data = oModel.getProperty("/"),
				creditIntTax = +data.ledgerDetails[0].cri,
				creditCentralTax = +data.ledgerDetails[0].crc,
				creditStateTax = +data.ledgerDetails[0].crs,
				creditcess = +data.ledgerDetails[0].crcs,

				otrIGST = data.gstr3bDetails[0].otrci,
				otrCGST = data.gstr3bDetails[1].otrci,
				otrSGST = data.gstr3bDetails[2].otrci,
				otrCess = data.gstr3bDetails[3].otrci;

			this._calIntelligentAnalysis(data.gstr3bDetails, data.ledgerDetails[0], false);
			data.gstr3bDetails.forEach(function (item) {
				item.int = item.ce = item.su = item.cs = "None";

				item.pdi = +item.pdi || 0;
				item.pdc = +item.pdc || 0;
				item.pds = +item.pds || 0;
				item.pdcs = +item.pdcs || 0;

				item.Total7 = +item.netOthRecTaxPayable2i - item.pdi - item.pdc - item.pds - item.pdcs;

				switch (item.desc) {
				case "Integrated Tax":
					var ledger1 = data.ledgerDetails[0].i || 0,
						ledger2 = data.ledgerDetails[1].i || 0,
						ledger3 = data.ledgerDetails[2].i || 0;
					break;
				case "Central Tax":
					var ledger1 = data.ledgerDetails[0].c || 0,
						ledger2 = data.ledgerDetails[1].c || 0,
						ledger3 = data.ledgerDetails[2].c || 0;
					break;
				case "State/UT Tax":
					var ledger1 = data.ledgerDetails[0].s || 0,
						ledger2 = data.ledgerDetails[1].s || 0,
						ledger3 = data.ledgerDetails[2].s || 0;
					break;
				case "Cess":
					var ledger1 = data.ledgerDetails[0].cs || 0,
						ledger2 = data.ledgerDetails[1].cs || 0,
						ledger3 = data.ledgerDetails[2].cs || 0;
					break;
				}
				var total = (+item.Total7 + +item.rci9) || 0,
					init10 = +item.inti10 || 0,
					lateFee = +item.lateFee12 || 0,
					minVal1 = Math.min(total, ledger1),
					minVal2 = Math.min(init10, ledger2),
					minVal3 = Math.min(lateFee, ledger3),
					diff1 = total - minVal1,
					diff2 = init10 - minVal2,
					diff3 = lateFee - minVal3;

				item.Total14 = (minVal1 + minVal2 + minVal3).toFixed(2);
				if (diff1 < 0) {
					diff1 = 0;
				} else if (diff2 < 0) {
					diff2 = 0;
				} else if (diff3 < 0) {
					diff3 = 0;
				}
				item.Total15 = (diff1 + diff2 + diff3).toFixed(2);
			}.bind(this));
			oModel.refresh(true);
			this._computeSaveSetoffLiability();
		},

		//========== Added by chaithra on 09/02/2021 Too save data in DB ================//
		_computeSaveSetoffLiability: function () {
			var Userdata = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/"),
				gstin = this.vgstinSetOff,
				payload = {
					"req": [{
						"gstin": gstin,
						"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue(),

						"otrIgst": +Userdata.gstr3bDetails[0].Total7,
						"otrCgst": +Userdata.gstr3bDetails[1].Total7,
						"otrSgst": +Userdata.gstr3bDetails[2].Total7,
						"otrCess": +Userdata.gstr3bDetails[3].Total7,

						"rcIgst": +Userdata.gstr3bDetails[0].rci8,
						"rcCgst": +Userdata.gstr3bDetails[1].rci8,
						"rcSgst": +Userdata.gstr3bDetails[2].rci8,
						"rcCess": +Userdata.gstr3bDetails[3].rci8,

						"ipIgst": +Userdata.gstr3bDetails[0].inti10,
						"ipCgst": +Userdata.gstr3bDetails[1].inti10,
						"ipSgst": +Userdata.gstr3bDetails[2].inti10,
						"ipCess": +Userdata.gstr3bDetails[3].inti10,

						"acrIgst": +Userdata.gstr3bDetails[0].Total15,
						"acrCgst": +Userdata.gstr3bDetails[1].Total15,
						"acrSgst": +Userdata.gstr3bDetails[2].Total15,
						"acrCess": +Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": +Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": +Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": +Userdata.gstr3bDetails[2].Total14,
						"ucbCess": +Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": +Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": +Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": +Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": +Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};
			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/save3BsetOffComputeToDb.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
						oData = oModel.getData();
					if (data.hdr !== undefined && data.hdr.status === "S") {
						oData.gstr3bDetails.forEach(function (item) {
							item.edit = false;
						});
						oModel.refresh(true);
						this._saveSetoffLiability("C");
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_saveSetoffLiability: function (type) {
			var Userdata = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/"),
				txLedger = Userdata.ledgerDetails.find(function (e) {
					if (e.desc === "tx") {
						e.cri = +e.cri;
						e.crc = +e.crc;
						e.crs = +e.crs;
						e.crcs = +e.crcs;
						return true;
					}
					return false;
				}),
				intrLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "intr";
				}),
				feeLedger = Userdata.ledgerDetails.find(function (e) {
					return e.desc === "fee";
				});

			Userdata.gstr3bDetails.forEach(function (item, j) {
				item.pdi = +item.pdi || 0;
				item.pdc = +item.pdc || 0;
				item.pds = +item.pds || 0;
				item.pdcs = +item.pdcs || 0;

				item.Total7 = +item.netOthRecTaxPayable2i - item.pdi - item.pdc - item.pds - item.pdcs;
				if (item.Total7 < 1) {
					item.Total7 = 0;
				}

				item.Total7 = item.Total7.toFixed(2);
				item.rci9 = (+item.rci9).toFixed(2);

				switch (item.desc) {
				case "Integrated Tax":
					var ledger1 = txLedger.i || 0,
						ledger2 = intrLedger.i || 0,
						ledger3 = feeLedger.i || 0;
					break;
				case "Central Tax":
					ledger1 = txLedger.c || 0;
					ledger2 = intrLedger.c || 0;
					ledger3 = feeLedger.c || 0;
					break;
				case "State/UT Tax":
					ledger1 = txLedger.s || 0;
					ledger2 = intrLedger.s || 0;
					ledger3 = feeLedger.s || 0;
					break;
				case "Cess":
					ledger1 = txLedger.cs || 0;
					ledger2 = intrLedger.cs || 0;
					ledger3 = feeLedger.cs || 0;
					break;
				}

				var total = (+item.Total7 + +item.rci9) || 0,
					init10 = +item.inti10 || 0,
					lateFee = +item.lateFee12 || 0,
					minVal1 = Math.min(total, ledger1),
					minVal2 = Math.min(init10, ledger2),
					minVal3 = Math.min(lateFee, ledger3),
					diff1 = total - minVal1,
					diff2 = init10 - minVal2,
					diff3 = lateFee - minVal3;

				item.Total14 = (minVal1 + minVal2 + minVal3).toFixed(2);
				if (diff1 < 0) {
					diff1 = 0;
				} else if (diff2 < 0) {
					diff2 = 0;
				} else if (diff3 < 0) {
					diff3 = 0;
				}
				item.Total15 = (diff1 + diff2 + diff3).toFixed(2);
			});
			this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").refresh(true);
			var Request = {
					"req": {
						"gstin": this.vgstinSetOff,
						"taxPeriod": this.byId("id_SetOFfTaxPeriod1").getValue(),
						"liab_ldg_id": Userdata.liab_ldg_id,
						"trans_typ": Userdata.trans_typ,
						"i_pdi": Userdata.gstr3bDetails[0].pdi.toString(),
						"i_pdc": Userdata.gstr3bDetails[0].pdc.toString(),
						"i_pds": Userdata.gstr3bDetails[0].pds.toString(),
						"c_pdi": Userdata.gstr3bDetails[1].pdi.toString(),
						"c_pdc": Userdata.gstr3bDetails[1].pdc.toString(),
						"s_pdi": Userdata.gstr3bDetails[2].pdi.toString(),
						"s_pds": Userdata.gstr3bDetails[2].pds.toString(),
						"cs_pdcs": Userdata.gstr3bDetails[3].pdcs.toString(),
						"i_adjNegative2i": Userdata.gstr3bDetails[0].adjNegative2i.toString(),
						"c_adjNegative2i": Userdata.gstr3bDetails[1].adjNegative2i.toString(),
						"s_adjNegative2i": Userdata.gstr3bDetails[2].adjNegative2i.toString(),
						"cs_adjNegative2i": Userdata.gstr3bDetails[3].adjNegative2i.toString(),
						"i_adjNegative8A": Userdata.gstr3bDetails[0].adjNegative8A.toString(),
						"c_adjNegative8A": Userdata.gstr3bDetails[1].adjNegative8A.toString(),
						"s_adjNegative8A": Userdata.gstr3bDetails[2].adjNegative8A.toString(),
						"cs_adjNegative8A": Userdata.gstr3bDetails[3].adjNegative8A.toString()
					}
				},
				payload = {
					"req": [{
						"gstin": this.vgstinSetOff,
						"taxPeriod": this.byId("id_SetOFfTaxPeriod1").getValue(),
						"otrIgst": +Userdata.gstr3bDetails[0].Total7,
						"otrCgst": +Userdata.gstr3bDetails[1].Total7,
						"otrSgst": +Userdata.gstr3bDetails[2].Total7,
						"otrCess": +Userdata.gstr3bDetails[3].Total7,

						"rcIgst": +Userdata.gstr3bDetails[0].rci8,
						"rcCgst": +Userdata.gstr3bDetails[1].rci8,
						"rcSgst": +Userdata.gstr3bDetails[2].rci8,
						"rcCess": +Userdata.gstr3bDetails[3].rci8,

						"ipIgst": +Userdata.gstr3bDetails[0].inti10,
						"ipCgst": +Userdata.gstr3bDetails[1].inti10,
						"ipSgst": +Userdata.gstr3bDetails[2].inti10,
						"ipCess": +Userdata.gstr3bDetails[3].inti10,

						"acrIgst": +Userdata.gstr3bDetails[0].Total15,
						"acrCgst": +Userdata.gstr3bDetails[1].Total15,
						"acrSgst": +Userdata.gstr3bDetails[2].Total15,
						"acrCess": +Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": +Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": +Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": +Userdata.gstr3bDetails[2].Total14,
						"ucbCess": +Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": +Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": +Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": +Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": +Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};
			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);

			if (type !== "C") {
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this._saveLiabilityOffSet(Request),
						this._save3BsetOffComputeToDb(payload)
					])
					.then(function (values) {
							sap.ui.core.BusyIndicator.hide();
						}.bind(this),
						function (err) {
							sap.ui.core.BusyIndicator.hide();
							console.log("GSTR-3B Liability SetOff: ", err);
						}.bind(this));
			} else {
				sap.ui.core.BusyIndicator.show(0);
				this._saveLiabilityOffSet(Request)
					.then(function (values) {
							sap.ui.core.BusyIndicator.hide();
						}.bind(this),
						function (err) {
							sap.ui.core.BusyIndicator.hide();
							console.log("GSTR-3B Liability SetOff: ", err);
						}.bind(this));
			}
		},

		_saveLiabilityOffSet: function (Request) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveChangesLiabilityOffSet.do",
						contentType: "application/json",
						data: JSON.stringify(Request)
					})
					.done(function (data, status, jqXHR) {
						var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
							oData = oModel.getData();

						if (data.hdr !== undefined && data.hdr.status === "S") {
							this.onGoSetOff1();
							MessageBox.success(data.resp);
							this.onGetSetOffEntityData();
						} else {
							MessageBox.error(JSON.parse(data).hdr.message);
						}
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						reject(err);
					});
			}.bind(this));
		},

		_save3BsetOffComputeToDb: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/save3BsetOffComputeToDb.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						resolve(data.hdr.status);
					})
					.fail(function (err) {
						reject(err);
					});
			});
		},

		onLiaEdit1: function () {
			MessageBox.information("Do you want to Edit User Data!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this.onLiaEditData1();
					}
				}.bind(this)
			});
		},

		onLiaEditData1: function () {
			var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
				oData = oModel.getProperty("/");
			oData.gstr3bDetails.forEach(function (item) {
				item.edit = true;
			});
			oModel.refresh(true);
		},

		onSaveSetoffLiability: function () {
			if (this.oBject.authStatus === "I") {
				MessageBox.warning("Auth token is inactive for selected GSTIN, please activate and retry.");
				return;
			}
			this.onSaveOffSet();
		},

		onSaveOffSet: function () {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/gstr3bDetails"),
				aField = ["pdi", "pdc", "pds", "pdcs", "Total7", "rci9"],
				msgFlag = true;

			data.forEach(function (item) {
				aField.forEach(function (f) {
					if (+item[f]) {
						msgFlag = false;
					}
				});
			});

			MessageBox.information("Do you want to save Offset Liability to GSTN?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						if (msgFlag) {
							MessageBox.warning("Do you want to file 3B with ZERO Value, are you sure?", {
								actions: [MessageBox.Action.YES, MessageBox.Action.NO],
								onClose: function (oAction) {
									if (oAction === "YES") {
										this._confirmSaveSetoffLiability();
									}
								}.bind(this)
							})
						} else {
							this._confirmSaveSetoffLiability();
						}
					}
				}.bind(this)
			});
		},

		_confirmSaveSetoffLiability: function () {
			MessageBox.information("By clicking Save Off Set Liability, GSTR-3B will get submitted. Do you want to Save & Submit GSTR-3B ?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._save3bLiabilitySetoff();
					}
				}.bind(this)
			});
		},

		_save3bLiabilitySetoff: function () {
			var Request = {
					"gstin": this.vgstinSetOff,
					"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue()
				},
				Userdata = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/"),
				payload = {
					"req": [{
						"gstin": this.vgstinSetOff,
						"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue(),

						"otrIgst": Userdata.gstr3bDetails[0].Total7,
						"otrCgst": Userdata.gstr3bDetails[1].Total7,
						"otrSgst": Userdata.gstr3bDetails[2].Total7,
						"otrCess": Userdata.gstr3bDetails[3].Total7,

						"rcIgst": Userdata.gstr3bDetails[0].rci8,
						"rcCgst": Userdata.gstr3bDetails[1].rci8,
						"rcSgst": Userdata.gstr3bDetails[2].rci8,
						"rcCess": Userdata.gstr3bDetails[3].rci8,

						"ipIgst": Userdata.gstr3bDetails[0].inti10,
						"ipCgst": Userdata.gstr3bDetails[1].inti10,
						"ipSgst": Userdata.gstr3bDetails[2].inti10,
						"ipCess": Userdata.gstr3bDetails[3].inti10,

						"acrIgst": Userdata.gstr3bDetails[0].Total15,
						"acrCgst": Userdata.gstr3bDetails[1].Total15,
						"acrSgst": Userdata.gstr3bDetails[2].Total15,
						"acrCess": Userdata.gstr3bDetails[3].Total15,

						"ucbIgst": Userdata.gstr3bDetails[0].Total14,
						"ucbCgst": Userdata.gstr3bDetails[1].Total14,
						"ucbSgst": Userdata.gstr3bDetails[2].Total14,
						"ucbCess": Userdata.gstr3bDetails[3].Total14,

						"lateFeeIgst": Userdata.gstr3bDetails[0].lateFee12,
						"lateFeeCgst": Userdata.gstr3bDetails[1].lateFee12,
						"lateFeeSgst": Userdata.gstr3bDetails[2].lateFee12,
						"lateFeeCess": Userdata.gstr3bDetails[3].lateFee12,

						"adjnegliabIgst2i": +Userdata.gstr3bDetails[0].adjNegative2i,
						"adjnegliabCgst2i": +Userdata.gstr3bDetails[1].adjNegative2i,
						"adjnegliabSgst2i": +Userdata.gstr3bDetails[2].adjNegative2i,
						"adjnegliabCess2i": +Userdata.gstr3bDetails[3].adjNegative2i,

						"netOtherRc2iiIgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiSgst": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,
						"netOtherRc2iiCess": +Userdata.gstr3bDetails[0].netOthRecTaxPayable2i,

						"adjnegliabIgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabSgst8a": +Userdata.gstr3bDetails[0].adjNegative8A,
						"adjnegliabCess8a": +Userdata.gstr3bDetails[0].adjNegative8A,

						"rci9Igst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Sgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cgst": +Userdata.gstr3bDetails[0].rci9,
						"rci9Cess": +Userdata.gstr3bDetails[0].rci9,
					}]
				};
			this._addPayloadLedger(payload.req, Userdata.ledgerDetails);

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/save3BsetOffComputeToDb.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr !== undefined && data.hdr.status === "S") {
						$.ajax({
								method: "POST",
								url: "/aspsapapi/saveOffSetLiabilityToGstin.do",
								contentType: "application/json",
								data: JSON.stringify(Request)
							})
							.done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr !== undefined && data.hdr.status === "S") {
									MessageBox.success(data.resp, {
										onClose: function () {
											this._get3bLiabilitySetOffStatusUpdate1(Request);
										}.bind(this)
									});
									//this.onSearch();
								} else {
									MessageBox.error(JSON.parse(data).hdr.message);
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					} else {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_addPayloadLedger: function (payload, data) {
			payload.push(data.find(function (e) {
				return e.desc === "currMonthUtil";
			}));
			payload.push(data.find(function (e) {
				return e.desc === "clsBal";
			}));
		},

		onComputeSetOff: function () {
			var aTabData = this.byId("id_MainSetOffTab").getModel("MainLiability").getProperty("/resp"),
				aIndex = this.byId("id_MainSetOffTab").getSelectedIndices();

			if (!aIndex.length) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			var vTaxPeriod = this.byId("id_SetOFfTaxPeriod").getValue(),
				aGstins = aIndex.map(function (idx) {
					return aTabData[idx].gstin;
				});
			var payLoad = {
				"req": {
					"taxPeriod": vTaxPeriod,
					"gstins": aGstins
				}
			};
			MessageBox.information("Do you want to compute Liability Set Off details for selected GSTINs?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr3BSetOffCompute.do",
								contentType: "application/json",
								data: JSON.stringify(payLoad)
							})
							.done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr && data.hdr.status === "S") {
									MessageBox.success("Liability Set Off Computed Successfully");
									this.onGetSetOffEntityData();
								} else {
									MessageBox.error("Error Occured While Compute GSTN data");
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							});
					}
				}.bind(this)
			});
		},

		onGetGSTNData: function () {
			var aTabData = this.byId("id_MainSetOffTab").getModel("MainLiability").getProperty("/resp"),
				aIndex = this.byId("id_MainSetOffTab").getSelectedIndices();

			if (!aIndex) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			var vTaxPeriod = this.byId("id_SetOFfTaxPeriod").getValue(),
				aAuth = [],
				aGstins = aIndex.map(function (idx) {
					if (aTabData[idx].authStatus == "I") {
						aAuth.push(aTabData[idx]);
					};
					return aTabData[idx].gstin;
				}),
				payLoad = {
					"req": {
						"taxPeriod": vTaxPeriod,
						"gstins": aGstins
					}
				};
			if (!aAuth.length) {
				MessageBox.information("Do you want to update the data from GSTN?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							this._gstr3bSetOffEntityGet(payLoad);
						}
					}.bind(this)
				});
			} else if (aAuth.length == aGstins.length) {
				MessageBox.warning("Auth token is inactive for selected GSTINs, hence data displayed is as per DigiGST and not as per GSTN." +
					"\nTo fetch data from GSTN, please activate auth token", {
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								this._gstr3bSetOffEntityGet(payLoad);
							}
						}.bind(this)
					});
			} else {
				MessageBox.warning(
					"Auth token is inactive for few selected GSTINs, hence data displayed is as per DigiGST and not as per GSTN for such GSTINs." +
					"\n" + "To fetch data from GSTN, please activate auth token :" + "\n" +
					"For active GSTINs do you want to update the data from GSTN for GSTINs?", {
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								this._gstr3bSetOffEntityGet(payLoad);
							}
						}.bind(this)
					});
			}
		},

		_gstr3bSetOffEntityGet: function (payLoad) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3bSetOffEntityGet.do",
					contentType: "application/json",
					data: JSON.stringify(payLoad)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success("Get GSTN data Successful");
						this.onGetSetOffEntityData();
					} else {
						MessageBox.error("Error Occured While Get GSTN data");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error(jqXHR.responseJSON.hdr.message);
				});
		},

		onCloseDialogBulkSave12: function () {
			this._oDialogbulkSaveStats12.close();
		},

		onSaveSetOff: function () {
			if (!this._oDialogbulkSaveStats12) {
				this._oDialogbulkSaveStats12 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.BulkSave1", this);
				this.getView().addDependent(this._oDialogbulkSaveStats12);
			}
			var aTabData = this.byId("id_MainSetOffTab").getModel("MainLiability").getProperty("/resp"),
				aIndex = this.byId("id_MainSetOffTab").getSelectedIndices(),
				aGstins = [],
				aAuth = [];

			if (aIndex.length === 0) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			var aField = [
					"paidThroughItcIgst", "paidThroughItcCgst", "paidThroughItcSgst", "addnlCashReqIgst", "addnlCashReqCgst", "addnlCashReqSgst"
				],
				msgFlag = true;
			aIndex.forEach(function (idx) {
				if (aTabData[idx].authStatus === "I") {
					aAuth.push(aTabData[idx]);
				}
				aGstins.push(aTabData[idx].gstin);
				aField.forEach(function (f) {
					if (+aTabData[idx][f]) {
						msgFlag = false;
					}
				});
			});
			var payLoad = {
				"req": {
					"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue(),
					"gstins": aGstins
				}
			};
			MessageBox.information("Do you want to save Offset Liability to GSTN ?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						if (msgFlag) {
							MessageBox.warning("Do you want to file 3B with ZERO Value, are you sure?", {
								actions: [MessageBox.Action.YES, MessageBox.Action.NO],
								onClose: function (sAction) {
									if (sAction === "YES") {
										this._saveGstr3bOffsetLiability(payLoad);
									}
								}.bind(this)
							});
						} else {
							this._saveGstr3bOffsetLiability(payLoad);
						}
					}
				}.bind(this)
			});
		},

		_saveGstr3bOffsetLiability: function (payLoad) {
			MessageBox.information("By clicking Save Off Set Liability, GSTR-3B will get submitted. Do you want to Save & Submit GSTR-3B ?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr3BSetOffEntityLevelSave.do",
								contentType: "application/json",
								data: JSON.stringify(payLoad)
							})
							.done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr && data.hdr.status === "S") {
									this.getView().byId("bulkSaveID12").setModel(new JSONModel(data.resp), "BulkGstinSaveModel1");
									this._oDialogbulkSaveStats12.open();
									this.onGetSetOffEntityData();
								} else {
									MessageBox.error("Error Occured While saving GSTN data");
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					}
				}.bind(this)
			});
		},

		onSaveChangeSetoffLiability: function () {
			this.onLiaSaveChanges3();
		},

		onLiaSaveChanges3: function () {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/"),
				reg = /^[0-9]*\.?[0-9]*$/;

			var creditIntTax = Number(data.ledgerDetails[0].cri),
				creditCentralTax = Number(data.ledgerDetails[0].crc),
				creditStateTax = Number(data.ledgerDetails[0].crs),
				creditcess = Number(data.ledgerDetails[0].crcs);

			var payablegstr3BIntTax = Number(data.gstr3bDetails[0].pdi),
				payablegstr3BIntTax1 = Number(data.gstr3bDetails[1].pdi),
				payablegstr3BIntTax2 = Number(data.gstr3bDetails[2].pdi),
				payablegstr3BCentralTax = Number(data.gstr3bDetails[0].pdc),
				payablegstr3BCentralTax1 = Number(data.gstr3bDetails[1].pdc),
				payablegstr3BStateTax = Number(data.gstr3bDetails[0].pds),
				payablegstr3BStateTax1 = Number(data.gstr3bDetails[2].pds),
				payablegstr3BCess = Number(data.gstr3bDetails[3].pdcs);

			data.gstr3bDetails.forEach(function (e) {
				e.pdi = e.pdi || 0;
				e.pdc = e.pdc || 0;
				e.pds = e.pds || 0;
				e.pdcs = e.pdcs || 0;
			});

			if (!payablegstr3BIntTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BIntTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BIntTax2.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BCentralTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BCentralTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BStateTax.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BStateTax1.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (!payablegstr3BCess.toString().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				return;
			}

			if (payablegstr3BIntTax + payablegstr3BIntTax1 + payablegstr3BIntTax2 > creditIntTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			if (payablegstr3BCentralTax + payablegstr3BCentralTax1 > creditCentralTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			if (payablegstr3BStateTax + payablegstr3BStateTax1 > creditStateTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			if (payablegstr3BCess > creditcess) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			MessageBox.information("Do you want to save changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						this._saveSetoffLiability("S");
					}
				}.bind(this)
			});
		},

		onGoSetOff: function () {
			this.onGetSetOffEntityData();
		},

		onClearSetOff: function () {
			this.byId("id_SetOFfTaxPeriod").setDateValue(new Date());
			this.byId("id_SetOffGstin").setSelectedKeys();
		},

		onGetSetOffEntityData: function () {
			var aGstin = this.byId("id_SetOffGstin").getSelectedKeys(),
				aPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": aGstin.includes("All") ? [] : aGstin,
						"taxPeriod": this.byId("id_SetOFfTaxPeriod").getValue()
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3bSetOffEntityDashboard.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data), "MainLiability");
					this.getView().getModel("MainLiability").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		showingCheck1: function () {
			var data = {
				"CashLd": true,
				"CredLd": true,
				"Other": true
			};
			var CashLed = this.byId("id_CashLed").getSelected(),
				CreditLed = this.byId("id_CreditLed").getSelected(),
				Other = this.byId("id_Other").getSelected();
			data.CashLd = CashLed;
			data.CredLd = CreditLed;
			data.Other = Other;
			this.byId("idDyGstr3bSetoff").setModel(new JSONModel(data), "visiGSTR3BSetOff");
		},

		onPressGenerateOTP1: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("MainLiability").getObject();
			if (oValue1.authStatus !== "I") {
				return;
			}
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": oValue1.gstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onBackGstr3bNil1: function () {
			if (this.edited) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onPressSaveRecords();
						} else {
							this.byId("dpAllOtherITC").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpAllOtherITC").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onBackGstr3bImportofGoods: function () {
			if (this.edited) {
				MessageBox.confirm('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onSaveChangeSubSection('ImportofGoods');
						} else {
							this.byId("dpImportofGoods").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpImportofGoods").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onBackGstr3bInwardSupplies: function () {
			if (this.edited) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onSaveChangeSubSection('InwardSupplies');
						} else {
							this.byId("dpInwardSupplies").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpInwardSupplies").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onBackGstr3bISDInwardSupplies: function () {
			if (this.editedISD) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onSaveChangeSubSection('ISDInwardSupplies');
						} else {
							this.byId("dpISDInwardSupplies").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpISDInwardSupplies").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onSaveChangeSubSection: function (jsonModel) {
			var TabData = this.getView().getModel(jsonModel).getProperty("/"),
				oView = this.getView();

			for (var j = 0; j < TabData.length; j++) {
				var val = TabData[j].userIgst.toString(),
					reg = /^[A-Za-z]+$/,
					val1 = val.split(".");

				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (val.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}

				var valuserInputIgst = TabData[j].userCgst.toString(),
					val1userInputIgst = valuserInputIgst.split(".");
				if (val1userInputIgst[1] !== undefined) {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputIgst[1] !== "" && val1userInputIgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputIgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (valuserInputIgst.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				var userInputCgst = TabData[j].userSgst.toString(),
					val1userInputCgst = userInputCgst.split(".");
				if (val1userInputCgst[1] !== undefined) {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (val1userInputCgst[1] !== "" && val1userInputCgst[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (val1userInputCgst[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (userInputCgst.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				var userInputSgst = TabData[j].userCess.toString(),
					userInputSgst1 = userInputSgst.split(".");
				if (userInputSgst1[1] !== undefined) {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
					if (userInputSgst1[1] !== "" && userInputSgst1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						return;
					}
				} else {
					if (userInputSgst1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						return;
					}
				}
				if (userInputSgst.match(reg)) {
					MessageBox.error("Alphabets are not allowed");
					return;
				}
				var sectionName = null;
				if (TabData[j].level2.length) {
					TabData[j].level2.forEach(function (el) {
						if (el.level3.length) {
							el.level3.forEach(function (l3) {
								if ((+l3.userCgst !== +l3.userSgst) && l3.edit && !sectionName) {
									sectionName = l3.sectionName;
								}
							});
						} else if ((+el.userCgst !== +el.userSgst) && el.edit && !sectionName) {
							sectionName = el.sectionName;
						}
					});
				} else {
					if ((+TabData[j].userCgst !== +TabData[j].userSgst) && TabData[j].edit) {
						sectionName = TabData[j].sectionName;
					}
				}
				if (sectionName) {
					MessageBox.error("CGST and SGST cannot be different for " + sectionName);
					return;
				}
			}
			if (TabData.length != 0) {
				if (this.byId("linkGSTID").getSelectedKey() === "") {
					var gstin = this.gstin;
				} else {
					gstin = this.byId("linkGSTID").getSelectedKey();
				}
				var InputList = [],
					ComputeList = [];
				TabData.forEach(function (l1) {
					InputList.push(this._getUserInputPayload(l1));
					l1.level2.forEach(function (l2) {
						InputList.push(this._getUserInputPayload(l2));
						l2.level3.forEach(function (l3) {
							InputList.push(this._getUserInputPayload(l3));
						}.bind(this));
					}.bind(this));
				}.bind(this));

				if (jsonModel === "ISDInwardSupplies") {
					if (InputList[1].radioFlag === true) {
						InputList[2].radioFlag = InputList[3].radioFlag = true;
					} else {
						InputList[2].radioFlag = InputList[3].radioFlag = false;
					}
				}
				var request = {
					"req": {
						"taxPeriod": this.byId("linkDate").getValue(),
						"gstin": gstin,
						// "status": "SAVE",
						"userInputList": InputList
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr3BUserInputSave.do",
						contentType: "application/json",
						data: JSON.stringify(request)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr && data.hdr.status === "S") {
							MessageBox.success(
								"Changes saved successfully. Kindly re-compute the GSTR 3B by clicking on button 'Auto Calculate3B - DigiGST' to refresh values in table 4(B)."
							);
							if (jsonModel === "ImportofGoods") {
								this.byId("dpImportofGoods").setVisible(false);
								this.byId("dpGstr3bSummary").setVisible(true);
								this.onSearchBack();
							} else if (jsonModel === "InwardSupplies") {
								this.byId("dpInwardSupplies").setVisible(false);
								this.byId("dpGstr3bSummary").setVisible(true);
								this.onSearchBack();
							} else if (jsonModel === "ISDInwardSupplies") {
								this.byId("dpISDInwardSupplies").setVisible(false);
								this.byId("dpGstr3bSummary").setVisible(true);
								this.onSearchBack();
							}
						} else {
							MessageBox.error("Error Occured While Saving the Changes");
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error Occured While Saving the Changes");
					});
			}
		},

		_getUserInputPayload: function (data) {
			return {
				"sectionName": data.sectionName,
				"subSectionName": data.subSectionName,
				"radioFlag": data.radioFlag,
				"igst": data.userIgst,
				"cgst": data.userCgst,
				"sgst": data.userSgst,
				"cess": data.userCess,
				"taxableVal": "0"
			};
		},

		onBackGstr3bOtrs: function () {
			if (this.editedOth) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onPressSaveRecords1();
						} else {
							this.byId("Other").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("Other").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		ITlive: function (oevt) {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/");
			var creditIntTax = Number(data.ledgerDetails[0].cri);
			var payablegstr3BIntTax = Number(data.gstr3bDetails[0].pdi);
			var payablegstr3BIntTax1 = Number(data.gstr3bDetails[1].pdi);
			var payablegstr3BIntTax2 = Number(data.gstr3bDetails[2].pdi);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BIntTax.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}
			if (!payablegstr3BIntTax1.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}
			if (!payablegstr3BIntTax2.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}

				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}

				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}

				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			data.gstr3bDetails[0].Total7 = Number(data.gstr3bDetails[0].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[0].pdi) -
				Number(data.gstr3bDetails[0].pdc) - Number(data.gstr3bDetails[0].pds) - Number(data.gstr3bDetails[0].pdcs);
			if (data.gstr3bDetails[0].Total7 < 1) {
				data.gstr3bDetails[0].Total7 = 0;
			}
			data.gstr3bDetails[0].Total7 = data.gstr3bDetails[0].Total7.toFixed(2);
			data.gstr3bDetails[1].Total7 = Number(data.gstr3bDetails[1].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[1].pdi) -
				Number(data.gstr3bDetails[1].pdc) - Number(data.gstr3bDetails[1].pds) - Number(data.gstr3bDetails[1].pdcs);
			if (data.gstr3bDetails[1].Total7 < 1) {
				data.gstr3bDetails[1].Total7 = 0;
			}
			data.gstr3bDetails[1].Total7 = data.gstr3bDetails[1].Total7.toFixed(2);
			data.gstr3bDetails[2].Total7 = Number(data.gstr3bDetails[2].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[2].pdi) -
				Number(data.gstr3bDetails[2].pdc) - Number(data.gstr3bDetails[2].pds) - Number(data.gstr3bDetails[2].pdcs);
			if (data.gstr3bDetails[2].Total7 < 1) {
				data.gstr3bDetails[2].Total7 = 0;
			}
			data.gstr3bDetails[2].Total7 = data.gstr3bDetails[2].Total7.toFixed(2);
			this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").refresh(true);
		},

		//Changes For Central Tax
		CTlive: function (oevt) {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getData();
			var creditCentralTax = Number(data.ledgerDetails[0].crc);
			var payablegstr3BCentralTax = Number(data.gstr3bDetails[0].pdc);
			var payablegstr3BCentralTax1 = Number(data.gstr3bDetails[1].pdc);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BCentralTax.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}
			if (!payablegstr3BCentralTax1.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}

				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}

				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}

				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			if (payablegstr3BCentralTax + payablegstr3BCentralTax1 > creditCentralTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			data.gstr3bDetails[0].Total7 = Number(data.gstr3bDetails[0].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[0].pdi) -
				Number(data.gstr3bDetails[0].pdc) - Number(data.gstr3bDetails[0].pds) - Number(data.gstr3bDetails[0].pdcs);
			if (data.gstr3bDetails[0].Total7 < 1) {
				data.gstr3bDetails[0].Total7 = 0;
			}
			data.gstr3bDetails[0].Total7 = data.gstr3bDetails[0].Total7.toFixed(2);
			data.gstr3bDetails[1].Total7 = Number(data.gstr3bDetails[1].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[1].pdi) -
				Number(data.gstr3bDetails[1].pdc) - Number(data.gstr3bDetails[1].pds) - Number(data.gstr3bDetails[1].pdcs);
			if (data.gstr3bDetails[1].Total7 < 1) {
				data.gstr3bDetails[1].Total7 = 0;
			}
			data.gstr3bDetails[1].Total7 = data.gstr3bDetails[1].Total7.toFixed(2);
			data.gstr3bDetails[2].Total7 = Number(data.gstr3bDetails[2].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[2].pdi) -
				Number(data.gstr3bDetails[2].pdc) - Number(data.gstr3bDetails[2].pds) - Number(data.gstr3bDetails[2].pdcs);
			if (data.gstr3bDetails[2].Total7 < 1) {
				data.gstr3bDetails[2].Total7 = 0;
			}
			data.gstr3bDetails[2].Total7 = data.gstr3bDetails[2].Total7.toFixed(2);
			this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").refresh(true);
		},

		///Changes for State Tax
		STlive: function (oevt) {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getData();
			var creditStateTax = Number(data.ledgerDetails[0].crs);
			var payablegstr3BStateTax = Number(data.gstr3bDetails[0].pds);
			var payablegstr3BStateTax1 = Number(data.gstr3bDetails[2].pds);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BStateTax.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}
			if (!payablegstr3BStateTax1.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}
				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}
				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}
				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			if (payablegstr3BStateTax + payablegstr3BStateTax1 > creditStateTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			data.gstr3bDetails[0].Total7 = Number(data.gstr3bDetails[0].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[0].pdi) -
				Number(data.gstr3bDetails[0].pdc) - Number(data.gstr3bDetails[0].pds) - Number(data.gstr3bDetails[0].pdcs);
			if (data.gstr3bDetails[0].Total7 < 1) {
				data.gstr3bDetails[0].Total7 = 0;
			}
			data.gstr3bDetails[0].Total7 = data.gstr3bDetails[0].Total7.toFixed(2);
			data.gstr3bDetails[1].Total7 = Number(data.gstr3bDetails[1].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[1].pdi) -
				Number(data.gstr3bDetails[1].pdc) - Number(data.gstr3bDetails[1].pds) - Number(data.gstr3bDetails[1].pdcs);
			if (data.gstr3bDetails[1].Total7 < 1) {
				data.gstr3bDetails[1].Total7 = 0;
			}
			data.gstr3bDetails[1].Total7 = data.gstr3bDetails[1].Total7.toFixed(2);
			data.gstr3bDetails[2].Total7 = Number(data.gstr3bDetails[2].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[2].pdi) -
				Number(data.gstr3bDetails[2].pdc) - Number(data.gstr3bDetails[2].pds) - Number(data.gstr3bDetails[2].pdcs);
			if (data.gstr3bDetails[2].Total7 < 1) {
				data.gstr3bDetails[2].Total7 = 0;
			}
			data.gstr3bDetails[2].Total7 = data.gstr3bDetails[2].Total7.toFixed(2);
			this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").refresh(true);
		},

		CessLive: function (oevt) {
			var data = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").getProperty("/");
			var creditcess = Number(data.ledgerDetails[0].crcs);
			var payablegstr3BCess = Number(data.gstr3bDetails[3].pdcs);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BCess.toString().match(reg)) {
				MessageBox.error("Only positive numeric values are allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}
				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}
				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}
				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			if (payablegstr3BCess > creditcess) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}
			data.gstr3bDetails[3].Total7 = Number(data.gstr3bDetails[3].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[3].pdi) -
				Number(data.gstr3bDetails[3].pdc) - Number(data.gstr3bDetails[3].pds) - Number(data.gstr3bDetails[3].pdcs);
			if (data.gstr3bDetails[3].Total7 < 1) {
				data.gstr3bDetails[3].Total7 = 0;
			}
			data.gstr3bDetails[3].Total7 = data.gstr3bDetails[3].Total7.toFixed(2);
			this.getView().byId("idDyliablityId").getModel("LiabillitySetOff").refresh(true);
		},

		//=============================== Download PDF gstin level Added by chaithra on 30/3/2021==================//
		onDownloadPDF: function (flag) {
			var oPayload = {
				"req": {
					"gstin": (!this.byId("linkGSTID").getSelectedKey() ? this.gstin : this.byId("linkGSTID").getSelectedKey()),
					"taxPeriod": this.byId("linkDate").getValue(),
					"isDigigst": flag
				}
			};

			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.req.isVerified = sAction;
						this.excelDownload(oPayload, "/aspsapapi/generateGstr3BSummaryPDFReport.do");
					}.bind(this)
				});

		},

		//================= Download PDF Entity Level Added by chaithra on 30/3/2021 ===================//
		onPressDownloadEntityPDF: function (flag) {
			var aPath = this.getView().byId("gstrTabId").getSelectedIndices(),
				oModelData = this.getView().getModel("GSTR3B").getData(),
				aGSTIN = [];

			if (aPath.length == 0) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			}
			aPath.forEach(function (e) {
				aGSTIN.push(oModelData.resp[e].gstin);
			});
			var oPayload = {
				"isDigigst": flag,
				"taxPeriod": this.byId("gstr3bDate").getValue(),
				"gstinList": aGSTIN
			};
			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed there from", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.isVerified = sAction;
						this.reportDownload(oPayload, "/aspsapapi/generateBulkGstr3BSummaryPDFReportAsync.do");
					}.bind(this)
				});

		},

		/////===================Calcualtaion 3B Summary liability=========================////
		ITlive1: function (oevt) {
			var data = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getData();
			var creditIntTax = Number(data.ledgerDetails[0].cri);
			var payablegstr3BIntTax = Number(data.gstr3bDetails[0].pdi);
			var payablegstr3BIntTax1 = Number(data.gstr3bDetails[1].pdi);
			var payablegstr3BIntTax2 = Number(data.gstr3bDetails[2].pdi);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BIntTax.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}
			if (!payablegstr3BIntTax1.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}
			if (!payablegstr3BIntTax2.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}
			var reg1 = new RegExp('^[0-9]*$');
			if (!reg1.test(payablegstr3BIntTax)) {
				//oevt.getSource().setValueState("Error");
				MessageBox.error("special character are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BIntTax1)) {
				//oevt.getSource().setValueState("Error");
				MessageBox.error("special character are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BIntTax2)) {
				//oevt.getSource().setValueState("Error");
				MessageBox.error("special character are not allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}
				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}
				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}
				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			if (payablegstr3BIntTax + payablegstr3BIntTax1 + payablegstr3BIntTax2 > creditIntTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			data.gstr3bDetails[0].Total7 = Number(data.gstr3bDetails[0].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[0].pdi) -
				Number(data.gstr3bDetails[0].pdc) - Number(data.gstr3bDetails[0].pds) - Number(data.gstr3bDetails[0].pdcs);
			if (data.gstr3bDetails[0].Total7 < 1) {
				data.gstr3bDetails[0].Total7 = 0;
			}
			data.gstr3bDetails[0].Total7 = data.gstr3bDetails[0].Total7.toFixed(2);
			data.gstr3bDetails[1].Total7 = Number(data.gstr3bDetails[1].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[1].pdi) -
				Number(data.gstr3bDetails[1].pdc) - Number(data.gstr3bDetails[1].pds) - Number(data.gstr3bDetails[1].pdcs);
			if (data.gstr3bDetails[1].Total7 < 1) {
				data.gstr3bDetails[1].Total7 = 0;
			}
			data.gstr3bDetails[1].Total7 = data.gstr3bDetails[1].Total7.toFixed(2);
			data.gstr3bDetails[2].Total7 = Number(data.gstr3bDetails[2].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[2].pdi) -
				Number(data.gstr3bDetails[2].pdc) - Number(data.gstr3bDetails[2].pds) - Number(data.gstr3bDetails[2].pdcs);
			if (data.gstr3bDetails[2].Total7 < 1) {
				data.gstr3bDetails[2].Total7 = 0;
			}
			data.gstr3bDetails[2].Total7 = data.gstr3bDetails[2].Total7.toFixed(2);
			sap.ui.getCore().byId("liablityId").getModel("Liabillity").refresh(true);
		},

		//Changes For Central Tax
		CTlive1: function (oevt) {
			var data = sap.ui.getCore().byId("liablityId").getModel("Liabillity").getData();
			var creditCentralTax = Number(data.ledgerDetails[0].crc);
			var payablegstr3BCentralTax = Number(data.gstr3bDetails[0].pdc);
			var payablegstr3BCentralTax1 = Number(data.gstr3bDetails[1].pdc);

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!payablegstr3BCentralTax.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}
			if (!payablegstr3BCentralTax1.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}

			var reg1 = new RegExp('^[0-9]*$');
			if (!reg1.test(payablegstr3BCentralTax)) {
				MessageBox.error("Special character are not allowed");
				return;
			}
			if (!reg1.test(payablegstr3BCentralTax1)) {
				MessageBox.error("Special character are not allowed");
				return;
			}

			for (var j = 0; j < data.gstr3bDetails.length; j++) {
				if (data.gstr3bDetails[j].pdi === undefined) {
					data.gstr3bDetails[j].pdi = 0;
				}

				if (data.gstr3bDetails[j].pdc === undefined) {
					data.gstr3bDetails[j].pdc = 0;
				}

				if (data.gstr3bDetails[j].pds === undefined) {
					data.gstr3bDetails[j].pds = 0;
				}

				if (data.gstr3bDetails[j].pdcs === undefined) {
					data.gstr3bDetails[j].pdcs = 0;
				}
			}

			if (payablegstr3BCentralTax + payablegstr3BCentralTax1 > creditCentralTax) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			data.gstr3bDetails[0].Total7 = Number(data.gstr3bDetails[0].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[0].pdi) -
				Number(data.gstr3bDetails[0].pdc) - Number(data.gstr3bDetails[0].pds) - Number(data.gstr3bDetails[0].pdcs);
			if (data.gstr3bDetails[0].Total7 < 1) {
				data.gstr3bDetails[0].Total7 = 0;
			}
			data.gstr3bDetails[0].Total7 = data.gstr3bDetails[0].Total7.toFixed(2);
			data.gstr3bDetails[1].Total7 = Number(data.gstr3bDetails[1].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[1].pdi) -
				Number(data.gstr3bDetails[1].pdc) - Number(data.gstr3bDetails[1].pds) - Number(data.gstr3bDetails[1].pdcs);
			if (data.gstr3bDetails[1].Total7 < 1) {
				data.gstr3bDetails[1].Total7 = 0;
			}
			data.gstr3bDetails[1].Total7 = data.gstr3bDetails[1].Total7.toFixed(2);
			data.gstr3bDetails[2].Total7 = Number(data.gstr3bDetails[2].netOthRecTaxPayable2i) - Number(data.gstr3bDetails[2].pdi) -
				Number(data.gstr3bDetails[2].pdc) - Number(data.gstr3bDetails[2].pds) - Number(data.gstr3bDetails[2].pdcs);
			if (data.gstr3bDetails[2].Total7 < 1) {
				data.gstr3bDetails[2].Total7 = 0;
			}
			data.gstr3bDetails[2].Total7 = data.gstr3bDetails[2].Total7.toFixed(2);
			sap.ui.getCore().byId("liablityId").getModel("Liabillity").refresh(true);
		},

		///Changes for State Tax
		STlive1: function (type) {
			var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
				data = oModel.getProperty("/"),
				creditStateTax = +data.ledgerDetails[0].crs,
				payablegstr3BStateTax = +data.gstr3bDetails[0].pds,
				payablegstr3BStateTax1 = +data.gstr3bDetails[2].pds,
				creditcess = +data.ledgerDetails[0].crcs,
				payablegstr3BCess = +data.gstr3bDetails[3].pdcs,
				cmLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "currMonthUtil";
				}),
				reg = /^[0-9]*\.?[0-9]*$/;

			if (type === "T") {
				if (!payablegstr3BStateTax.toString().match(reg) || !payablegstr3BStateTax1.toString().match(reg)) {
					MessageBox.error("Nagative values are not allowed");
					return;
				}

				var reg1 = new RegExp('^[0-9]*$');
				if (!reg1.test(payablegstr3BStateTax) || !reg1.test(payablegstr3BStateTax1)) {
					MessageBox.error("Special character are not allowed");
					return;
				}
				if (payablegstr3BStateTax + payablegstr3BStateTax1 > creditStateTax) {
					MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
					return;
				}
			} else {
				if (!payablegstr3BCess.toString().match(reg)) {
					MessageBox.error("Nagative values are not allowed");
					return;
				}

				var reg1 = new RegExp('^[0-9]*$');
				if (!reg1.test(payablegstr3BCess)) {
					MessageBox.error("Special character are not allowed");
					return;
				}
				if (payablegstr3BCess > creditcess) {
					MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
					return;
				}
			}
			cmLedger.cri = cmLedger.crc = cmLedger.crs = cmLedger.crcs = 0;
			data.gstr3bDetails.forEach(function (e) {
				e.pdi = +e.pdi || 0;
				e.pdc = +e.pdc || 0;
				e.pds = +e.pds || 0;
				e.pdcs = +e.pdcs || 0;
				e.Total7 = (+e.netOthRecTaxPayable2i - e.pdi - e.pdc - e.pds - e.pdcs);
				e.Total7 = (e.Total7 < 1 ? 0 : e.Total7).toFixed(2);

				switch (e.desc) {
				case "Integrated Tax":
					cmLedger.i = (+e.Total7 + +e.rci9);
					break;
				case "Central Tax":
					cmLedger.c = (+e.Total7 + +e.rci9);
					break;
				case "State/UT Tax":
					cmLedger.s = (+e.Total7 + +e.rci9);
					break;
				case "Cess":
					cmLedger.cs = (+e.Total7 + +e.rci9);
					break;
				}
				cmLedger.cri += e.pdi;
				cmLedger.crc += e.pdc;
				cmLedger.crs += e.pds;
				cmLedger.crcs += e.pdcs;
			}.bind(this));

			cmLedger.Total = (+cmLedger.i + +cmLedger.c + +cmLedger.s + +cmLedger.cs);
			cmLedger.Total1 = (+cmLedger.cri + +cmLedger.crc + +cmLedger.crs + +cmLedger.crcs);
			var fLedger = ["i", "c", "s", "cs", "Total", "cri", "crc", "crs", "crcs", "Total1"],
				txLedger = data.ledgerDetails.find(function (e) {
					if (e.desc === "tx") {
						e.cri = +e.cri;
						e.crc = +e.crc;
						e.crs = +e.crs;
						e.crcs = +e.crcs;
						return true;
					}
					return false;
				}),
				intrLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "intr";
				}),
				feeLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "fee";
				}),
				cbLedger = data.ledgerDetails.find(function (e) {
					return e.desc === "clsBal";
				});
			fLedger.forEach(function (e) {
				cbLedger[e] = +txLedger[e] + +(intrLedger[e] || 0) + +(feeLedger[e] || 0) - +cmLedger[e];
			});
			oModel.refresh(true);
		},

		CessLive1: function (oevt) {
			var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
				data = oModel.getData(),
				creditcess = +data.ledgerDetails[0].crcs,
				payablegstr3BCess = +data.gstr3bDetails[3].pdcs,
				reg = /^[0-9]*\.?[0-9]*$/;

			if (!payablegstr3BCess.toString().match(reg)) {
				MessageBox.error("Nagative values are not allowed");
				return;
			}

			var reg1 = new RegExp('^[0-9]*$');
			if (!reg1.test(payablegstr3BCess)) {
				MessageBox.error("Special character are not allowed");
				return;
			}
			if (payablegstr3BCess > creditcess) {
				MessageBox.error("Paid Through ITC can't be more than Credit Ledger Balance");
				return;
			}

			data.gstr3bDetails.forEach(function (e) {
				e.pdi = e.pdi || 0;
				e.pdc = e.pdc || 0;
				e.pds = e.pds || 0;
				e.pdcs = e.pdcs || 0;
				if (e.desc === "Cess") {
					e.Total7 = (+e.netOthRecTaxPayable2i - +e.pdi - +e.pdc - +e.pds - +e.pdcs);
					e.Total7 = (e.Total7 < 1 ? 0 : e.Total7).toFixed(2);
				}
			}.bind(this));
			oModel.refresh(true);
		},

		onChange2i8a: function (screen) {
			if (screen == "Liabillity") {
				var oModel = sap.ui.getCore().byId("liablityId").getModel("Liabillity"),
					data = oModel.getProperty("/");
			} else {
				var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
					data = oModel.getProperty("/");
			}

			data.gstr3bDetails.forEach(function (e) {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!e.adjNegative2i.toString().match(reg)) {
					MessageBox.error("Nagative values are not allowed");
					e.adjNegative2i = 0;
					return;
				}
				if (!e.adjNegative8A.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed.");
					e.adjNegative8A = 0;
					return;
				}
				e.adjNegative2i = +e.adjNegative2i || 0;
				e.adjNegative8A = +e.adjNegative8A || 0;
			}.bind(this));
			oModel.refresh(true);
		},

		onFilingStatus: function () {
			if (!this._oDialogSaveStats9) {
				this._oDialogSaveStats9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.FilingStatus", this);
				this.getView().addDependent(this._oDialogSaveStats9);
			}
			var gstin = this.byId("linkGSTID").getSelectedKey();
			var vDate = new Date();
			this.byId("FStp").setMaxDate(vDate);
			this.byId("FStp").setValue(this.byId("linkDate").getValue());
			this.byId("FsGstin").setSelectedKey(gstin);
			this.onSaveOkay12();
			this._oDialogSaveStats9.open();
		},

		onCloseDialogFS: function () {
			this._oDialogSaveStats9.close();
		},

		onSaveOkay12: function () {
			var req = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": this.byId("FStp").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getFilingStatus.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().byId("idTableFS").setModel(new JSONModel(data.resp), "TableFS");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onGSTNdataPressEntity: function () {
			var gstin = this.byId("gstrTabId").getSelectedIndices();
			if (gstin.length === 0) {
				MessageBox.warning("Please select at least one GSTIN");
				return;
			}
			MessageBox.information("Do you want to Update GSTIN Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onUpdateGstr1Summ1();
					}
				}.bind(this)
			});
		},

		onUpdateGstr1Summ1: function () {
			if (!this._oDialogbulkSaveStatsU) {
				this._oDialogbulkSaveStatsU = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.BulkSave3", this);
				this.getView().addDependent(this._oDialogbulkSaveStatsU);
			}
			var oModelData = this.getView().getModel("GSTR3B").getProperty("/"),
				oPath = this.getView().byId("gstrTabId").getSelectedIndices(),
				taxPeriod = this.byId("gstr3bDate").getValue(),
				aGSTIN = oPath.map(function (e) {
					return {
						"taxPeriod": taxPeriod,
						"gstin": oModelData.resp[e].gstin
					};
				}),
				req = {
					"req": aGSTIN
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGSTR3BSummaryFromGSTN.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().byId("bulkSaveIDU").setModel(new JSONModel(data.resp), "BulkGstinSaveModelU");
					this._oDialogbulkSaveStatsU.open();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					MessageBox.error("Error Occured While Saving the Changes");
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onCloseDialogBulkSaveU: function () {
			this._oDialogbulkSaveStatsU.close();
		},

		onPressPastLia: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			this.PastLia = new JSONModel();
			this.datal = [];
			this.PastLia.setData(this.datal);
			this.getView().setModel(this.PastLia, "PastLiabDtls");
			var AllOtherITC = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getUserPastLiabDtls.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.datal.push(data.resp);
					this.PastLia.setData(this.datal[0]);
					this.getView().setModel(this.PastLia, "PastLiabDtls");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onBackPastPeriod: function () {
			if (this.editedPP) {
				MessageBox.information('Do you want to Save Changes?', {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this.onPPsave1();
						} else {
							this.byId("dpGstr3bPastPeriod").setVisible(false);
							this.byId("dpGstr3bSummary").setVisible(true);
							this.onSearchBack();
						}
					}.bind(this)
				});
			} else {
				this.byId("dpGstr3bPastPeriod").setVisible(false);
				this.byId("dpGstr3bSummary").setVisible(true);
				this.onSearchBack();
			}
		},

		onPressAddRecordsppl: function () {
			this.clearpress = "";
			var newRec = {
				"sectionName": "7.1",
				"subSectionName": "PPLIA",
				"userRetPeriod": "",
				"igst": "",
				"cgst": "",
				"sgst": "",
				"cess": ""
			};
			this.datal[0].push(newRec);
			this.PastLia.setData(this.datal[0]);
			this.getView().setModel(this.PastLia, "PastLiabDtls");
			this.getView().getModel("PastLiabDtls").refresh(true);
		},

		onInterPP: function (oEvt) {
			var path = oEvt.getParameter('listItem').getBindingContext("PastLiabDtls").getPath(),
				index = parseInt(path.substring(path.lastIndexOf('/') + 1));

			MessageBox.information("Do you want to Delete Selected Line Item!", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = this.getView().getModel("PastLiabDtls").getData();
						model.splice(index, 1);
						this.getView().getModel("PastLiabDtls").refresh(true);
					}
				}.bind(this)
			});
		},

		onPPsave: function () {
			var TabData = this.getView().getModel("PastLiabDtls").getData();
			for (var i = 0; i < TabData.length; i++) {
				var val = TabData[i].igst.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						this.fromBack = "";
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					this.fromBack = "";
					return;
				}

				/////////////////////////////////////////////////
				var val = TabData[i].cgst.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						this.fromBack = "";
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					this.fromBack = "";
					return;
				}

				/////////////////////////////////////////////////
				var val = TabData[i].sgst.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						this.fromBack = "";
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					this.fromBack = "";
					return;
				}

				/////////////////////////////////////////////////
				var val = TabData[i].cess.toString();
				var val1 = val.split(".");
				if (val1[1] !== undefined) {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
					if (val1[1] !== "" && val1[1].length > 2) {
						MessageBox.error("More than 2 decimals are not allowed");
						this.fromBack = "";
						return;
					}
				} else {
					if (val1[0].length > 16) {
						MessageBox.error("More than 16 digits are not allowed");
						this.fromBack = "";
						return;
					}
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					this.fromBack = "";
					return;
				}
			}
			MessageBox.information('Do you want to Save Changes?', {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.saveChangesclick = "Saved";
						this.onPPsave1();
					} else {
						return;
					}
				}.bind(this)
			});
		},

		onPPsave1: function () {
			var userInputList = [];
			var TabData1 = this.getView().getModel("PastLiabDtls").getData();
			for (var i = 0; i < TabData1.length; i++) {
				var obj = {
					"sectionName": TabData1[i].sectionName,
					"subSectionName": TabData1[i].subSectionName,
					"taxableVal": "0",
					"igst": TabData1[i].igst,
					"cgst": TabData1[i].cgst,
					"sgst": TabData1[i].sgst,
					"cess": TabData1[i].cess,
					"pos": "0",
					"userRetPeriod": TabData1[i].userRetPeriod
				};
				userInputList.push(obj);
			}

			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"status": "7.1_SAVE",
					"userInputList": userInputList
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveGstinDashboardUserInput.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr && data.hdr.status === "S") {
						MessageBox.success("Changes saved successfully.");
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		onPPliaSave: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var request = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3bSavePastLiab.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = JSON.parse(data).hdr;
					if (sts.status === "S") {
						MessageBox.success(JSON.parse(data).resp);
					} else {
						MessageBox.error(JSON.parse(data).resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		onRecompute: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3bRecomputeInt.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = JSON.parse(data).hdr;
					if (sts.status === "S") {
						MessageBox.success(JSON.parse(data).resp);
					} else {
						MessageBox.error(JSON.parse(data).resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error Occured While Saving the Changes");
				});
		},

		OnPressSS: function (oEvt) {
			var data = oEvt.getSource().getEventingParent().getParent().getBindingContext("Rule3842").getObject();
			var oButton = oEvt.getSource();
			if (data.subSectionName === "Table_4(A)(1)") {
				if (!this._oGstinPopover1) {
					this._oGstinPopover1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Popover1", this);
					this.getView().addDependent(this._oGstinPopover1);
				}
				this._oGstinPopover1.openBy(oButton);

			} else if (data.subSectionName === "Table_4(A)(5)") {
				if (!this._oGstinPopover2) {
					this._oGstinPopover2 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Popover2", this);
					this.getView().addDependent(this._oGstinPopover2);
				}
				this._oGstinPopover2.openBy(oButton);
			}
		},

		OnPressOR: function (oEvt) {
			var data = oEvt.getSource().getEventingParent().getParent().getBindingContext("OtherReversal").getObject();
			var oButton = oEvt.getSource();
			if (data.subSectionName === "OR_175_4A1_4") {
				if (!this._oGstinPopover15) {
					this._oGstinPopover15 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.PopoverOR", this);
					this.getView().addDependent(this._oGstinPopover15);
				}
				this._oGstinPopover15.openBy(oButton);

			} else if (data.subSectionName === "OR_175_4A5") {
				if (!this._oGstinPopover27) {
					this._oGstinPopover27 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.PopoverOR1", this);
					this.getView().addDependent(this._oGstinPopover27);
				}
				this._oGstinPopover27.openBy(oButton);
			}
		},

		onChangeDtReconReqId: function (oEvent) {
			var frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId("toReconTaxPriod").getDateValue();

			if (frDate > toDate) {
				this.byId("toReconTaxPriod").setDateValue(frDate);
			}
			this.byId("toReconTaxPriod").setMinDate(frDate);
		},

		onPressClearRequestIDwise: function () {
			this._bindReqIdFilter();
			this.onPressRequestIDwise3B();
		},

		onPressRequestIDwise3B: function () {
			var oFilterData = this.byId("idRequestIDwisePage3B").getModel("FilterModel").getProperty("/"),
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
			this.byId("dpGstr1vs3ProcessRecord").setVisible(false);
			this.byId("idRequestIDwisePage3B").setVisible(true);
			this.getView().setModel(new JSONModel([]), "ReqWiseData2A");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getgstr1vs3BReportRequestStatusFilter.do",
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

		onPressRequestIDwiseBack3B: function () {
			this.byId("dpGstr1vs3ProcessRecord").setVisible(true);
			this.byId("idRequestIDwisePage3B").setVisible(false);
		},

		onConfigExtractPress2A: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("ReqWiseData2A").getObject(),
				request = {
					"req": {
						"rptDownldPath": obj.rptDownldPath,
						"requestId": obj.requestId
					}
				};
			this.excelDownload(request, "/aspsapapi/downloadGstr1Vs3bReport.do");
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
		},

		OnPressOtherITC: function (header) {
			switch (header) {
			case "G2BCTax_b":
				var vSubSec = "4A-1.1.b";
				break;
			case "ITCRTP":
				vSubSec = "4A-1.2";
				break;
			case "APG3B_B":
				vSubSec = "4A-3.1.a.b";
				break;
			case "RRTP":
				vSubSec = "4A-3.1.b";
				break;
			case "APG2B":
				vSubSec = "4A-4.2.a";
				break;
			case "ITCR":
				vSubSec = "4A-4.2.b";
				break;
			case "APGG2B":
				vSubSec = "4A-5.1.b";
				break;
			case "APRRU":
				vSubSec = "4A-5.2.c";
				break;
			}

			if (this.byId("linkGSTID").getSelectedKey() === "") {
				var gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin,
					"subSection": vSubSec
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getInfoSectionData.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					data.header = header;
					this.OnPressInfoSection(data)
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		OnPressInfoSection: function (data) {
			if (!this._oAllOtherITC3BPopover) {
				this._oAllOtherITC3BPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.IconITC3B", this);
				this.getView().addDependent(this._oAllOtherITC3BPopover);
			}
			this.getView().setModel(new JSONModel(data), "IconITC3B");
			this._oAllOtherITC3BPopover.open();
		},

		OnPressOtherITCClose: function () {
			this._oAllOtherITC3BPopover.close();
		},

		onSelectCheckBoxAmountISD: function (oEvent) {
			if (oEvent.getParameter('selected')) {
				this.editedISD = true;
			}
		},

		onChangeAmtGeneral: function (oEvt, model) {
			var retArr = this.getView().getModel(model).getProperty("/"),
				val = oEvt.getSource().getValue().toString(),
				val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 16) {
					MessageBox.error("More than 16 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^[A-Za-z]+$/;
			if (val.match(reg)) {
				// MessageBox.error("Negative values and alphabets are not allowed");
				MessageBox.error("alphabets are not allowed");
				return;
			}

			this.edited = true;
			if (model === "ISDInwardSupplies") {
				this.editedISD = true;
			}
			retArr.forEach(function (l1) {
				if (l1.level2.length) {
					l1.userIgst = l1.userCgst = l1.userSgst = l1.userCess = 0;
					l1.level2.forEach(function (l2) {
						if (l2.level3.length) {
							l2.userCess = l2.userCgst = l2.userIgst = l2.userSgst = 0;
							l2.level3.forEach(function (l3) {
								l2.userCess = (+l2.userCess + (l3.Check && l3.radioFlag ? +l3.userCess : 0)).toFixed(2);
								l2.userCgst = (+l2.userCgst + (l3.Check && l3.radioFlag ? +l3.userCgst : 0)).toFixed(2);
								l2.userIgst = (+l2.userIgst + (l3.Check && l3.radioFlag ? +l3.userIgst : 0)).toFixed(2);
								l2.userSgst = (+l2.userSgst + (l3.Check && l3.radioFlag ? +l3.userSgst : 0)).toFixed(2);
							});
						}
						l1.userIgst = (+l1.userIgst + ((l2.Check && l2.radioFlag) || !l2.Check ? +l2.userIgst : 0)).toFixed(2);
						l1.userCgst = (+l1.userCgst + ((l2.Check && l2.radioFlag) || !l2.Check ? +l2.userCgst : 0)).toFixed(2);
						l1.userSgst = (+l1.userSgst + ((l2.Check && l2.radioFlag) || !l2.Check ? +l2.userSgst : 0)).toFixed(2);
						l1.userCess = (+l1.userCess + ((l2.Check && l2.radioFlag) || !l2.Check ? +l2.userCess : 0)).toFixed(2);
					});
				}
			}.bind(this));
			this.getView().getModel(model).refresh(true);
		},

		onSelectImportOfGoods: function (oEvent) {
			if (oEvent.getParameter("selected")) {
				var oContext = oEvent.getSource().getBindingContext("ImportofGoods"),
					aPath = oContext.getPath().split("/level2/"),
					oData = oContext.getModel().getProperty(aPath[0]),
					obj = oContext.getObject();

				this.edited = true;
				oData.userIgst = obj.userIgst;
				oData.userCgst = obj.userCgst;
				oData.userSgst = obj.userSgst;
				oData.userCess = obj.userCess;
				oContext.getModel().refresh(true);
			}
		},

		onSelectInwardSupply: function (oEvent) {
			if (oEvent.getParameter("selected")) {
				var oContext = oEvent.getSource().getBindingContext("InwardSupplies"),
					aPath = oContext.getPath().split("/level3/"),
					aData = oContext.getModel().getProperty("/"),
					oData = oContext.getModel().getProperty(aPath[0]),
					obj = oContext.getObject();

				this.edited = true;
				oData.userIgst = obj.userIgst;
				oData.userCgst = obj.userCgst;
				oData.userSgst = obj.userSgst;
				oData.userCess = obj.userCess;

				aData.forEach(function (l1) {
					if (l1.level2.length) {
						l1.userIgst = l1.userCgst = l1.userSgst = l1.userCess = 0;
						l1.level2.forEach(function (l2) {
							l1.userIgst = +l1.userIgst + +l2.userIgst;
							l1.userCgst = +l1.userCgst + +l2.userCgst;
							l1.userSgst = +l1.userSgst + +l2.userSgst;
							l1.userCess = +l1.userCess + +l2.userCess;
						});
					}
				});
				oContext.getModel().refresh(true);
			}
		},

		onPressSaveStatus: function () {
			var payload = {
					"gstin": this.byId("linkGSTID").getSelectedKey(),
					"taxPeriod": this.byId("linkDate").getValue()
				},
				oPromise = this._liabilitySetoffStatus(payload);
			this._oDialoggstr53.setBusy(true);
			oPromise.then(function (data) {
					this._oDialoggstr53.setBusy(false);
				}.bind(this),
				function (err) {
					this._oDialoggstr53.setBusy(false);
				}.bind(this));
		},

		onPressSetoffSaveStatus: function () {
			var payload = {
					"gstin": this.byId("id_SetOffGstin1").getSelectedKey(),
					"taxPeriod": this.byId("id_SetOFfTaxPeriod1").getValue()
				},
				oPromise = this._3bLiabilitySetoffStatus(payload);

			sap.ui.core.BusyIndicator.show(0);
			oPromise.then(function (data) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this),
				function (err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		_get3bLiabilitySetOffStatusUpdate1: function (payload) {
			var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff"),
				oPayload = {
					"req": {
						"gstin": payload.gstin,
						"taxPeriod": payload.taxPeriod,
						"ledgerDetails": this._getPayloadLeaderDetail(oModel.getProperty("/ledgerDetails")),
						"gstr3bDetails": this._getPayloadGstr3bDetail(oModel.getProperty("/gstr3bDetails"))
					}
				};
			var pSetoffStats = this._3bLiabilitySetoffStatus(payload),
				pSaveSetOff = new Promise(function (resolve, reject) {
					$.ajax({
							url: "/aspsapapi/gstr3BOffSetSnapSave.do",
							method: "POST",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						})
						.done(function (data) {
							resolve('S');
						})
						.fail(function (err) {
							reject(err);
						});
				});

			sap.ui.core.BusyIndicator.show(0);
			Promise.all([pSetoffStats, pSaveSetOff])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
					console.log('GSTR-3B Error:', err);
				}.bind(this));
		},

		_3bLiabilitySetoffStatus: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						url: "/aspsapapi/get3bLiabilitySetOffStatusUpdate.do",
						method: "POST",
						contentType: "application/json",
						data: JSON.stringify({
							"req": payload
						})
					})
					.done(function (data) {
						var oModel = this.getView().byId("idDyliablityId").getModel("LiabillitySetOff");
						oModel.setProperty('/liabilitySetoffStatus', data.resp.liabilitySetoffStatus);
						oModel.setProperty('/updatedOn', data.resp.updatedOn);
						oModel.refresh(true);
						resolve('S');
					}.bind(this))
					.fail(function (error) {
						reject(error);
					});
			}.bind(this));
		},

		onPressRefresh4D1: function () {
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}
			var request = {
				"req": {
					"taxPeriod": this.byId("linkDate").getValue(),
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr3BITCReclaim.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						if (data.hdr.message.includes("com.ey.advisory.common.AppException:")) {
							var message = data.hdr.message.split(":")[1];
						} else {
							var message = data.hdr.message;
						}
						MessageBox.error(message);
					} else {
						MessageBox.success("Refresh Successfully");
					}
					this.onValidate();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onValidate: function () {
			var gstin = (this.byId("linkGSTID").getSelectedKey() ? this.byId("linkGSTID").getSelectedKey() : this.gstin),
				request = {
					"req": {
						"taxPeriod": this.byId("linkDate").getValue(),
						"gstin": gstin
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/validateGstr3BItcReclaim.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.onValidatePopup(data);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onValidatePopup: function (data) {
			data.resp.push({
				"sectionName": "A+B-C",
				"igst": (data.resp[0].igst + data.resp[1].igst - data.resp[2].igst).toFixed(2),
				"cgst": (data.resp[0].cgst + data.resp[1].cgst - data.resp[2].cgst).toFixed(2),
				"sgst": (data.resp[0].sgst + data.resp[1].sgst - data.resp[2].sgst).toFixed(2),
				"cess": (data.resp[0].cess + data.resp[1].cess - data.resp[2].cess).toFixed(2)
			});
			if (!this._oValidate) {
				this._oValidate = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.Validation4D1", this);
				this.getView().addDependent(this._oValidate);
			}

			this.getView().setModel(new JSONModel(data), "validation4D1");
			this._oValidate.open();
		},

		onPressValidateClose: function (flag) {
			if (flag === "C") {
				this._oValidate.close();
			} else {
				var oData = this.getView().getModel("validation4D1").getProperty("/resp");
				if (oData[3].igst < 0 || oData[3].cgst < 0 || oData[3].sgst < 0 || oData[3].cess < 0) {
					MessageBox.warning('ITC reclaimed by you in Table 4D(1) is more than ' +
						'the available ITC Reversed including the reported/amended opening balance.' +
						'\n Do you still want to proceed? ', {
							actions: ["Procced", "Cancel"],
							onClose: function (sAction) {
								if (sAction === "Procced") {
									this._gstr3BValidationInputSave(true);
								} else {}
							}.bind(this)
						});
				} else {
					this._gstr3BValidationInputSave(false);
				}
			}
		},

		_gstr3BValidationInputSave: function (flag) {
			var oData = this.getView().getModel("display").getProperty("/"),
				payload = {
					"req": {
						"gstin": this.byId("linkGSTID").getValue(),
						"taxPeriod": this.byId("linkDate").getValue(),
						"validationFlag": true,
						"errorFlag": flag
					}
				};
			this.validate4D1 = true;
			this._oValidate.close();
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr3BValidationInputSave.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = this.getView().getModel("timeStamp");
					oModel.setProperty("/validationFlag", data.resp.validationFlag);
					oModel.setProperty("/errorFlag", data.resp.errorFlag);
					oModel.refresh(true);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		}
	});
});