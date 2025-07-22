sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, MessageBox, Formatter, JSONModel, Button, Dialog) {
	"use strict";
	var vCount = 10;
	return BaseController.extend("com.ey.digigst.controller.GSTR6", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR6
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("GSTR6").attachPatternMatched(this._onRouteMatched, this);
			var that = this,
				vDate = new Date(),
				date = new Date(),
				date1 = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1),
				vEmailDateVC = new Date();

			date.setDate(date.getDate() - 9);
			date1.setMonth(date1.getMonth() - 1);
			vPeriod.setDate(1);

			this.byId("idFromTaxPeriodDetailed").setMaxDate(vDate);
			this.byId("idFromTaxPeriodDetailed").setDateValue(vPeriod);
			this.byId("idFromTaxPeriodDetailed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idFromTaxPeriodDetailed").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idToTaxPeriodDetailed").setMinDate(vPeriod);
			this.byId("idToTaxPeriodDetailed").setMaxDate(vDate);
			this.byId("idToTaxPeriodDetailed").setDateValue(vDate);
			this.byId("idToTaxPeriodDetailed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idToTaxPeriodDetailed").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idRSFromTaxPeriodDetailed").setDateValue(vDate);
			this.byId("idRSFromTaxPeriodDetailed").setMaxDate(new Date());
			that.byId("idRSFromTaxPeriodDetailed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idRSFromTaxPeriodDetailed").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idRSToTaxPeriodDetailed").setDateValue(new Date());
			this.byId("idRSToTaxPeriodDetailed").setMinDate(vDate);
			this.byId("idRSToTaxPeriodDetailed").setMaxDate(new Date());
			that.byId("idRSToTaxPeriodDetailed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idRSToTaxPeriodDetailed").$().find("input").attr("readonly", true);
				}
			});

			// 			Processed Records
			this.byId("idTaxperiodGstr6pro").setMaxDate(vDate);
			this.byId("idTaxperiodGstr6pro").setDateValue(vDate);

			this.byId("idTaxSummary").setMaxDate(vDate);
			this.byId("idTaxSummary").setDateValue(vDate);

			this.byId("idTaxPeriodDetailed").setMaxDate(vDate);
			this.byId("idTaxPeriodDetailed").setDateValue(vDate);

			this.byId("idTaxperiodGstr6pro").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxperiodGstr6pro").$().find("input").attr("readonly", true);
				}
			});
			this.byId("idTaxSummary").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxSummary").$().find("input").attr("readonly", true);
				}
			});
			this.byId("idTaxPeriodDetailed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxPeriodDetailed").$().find("input").attr("readonly", true);
				}
			});

			// this.getGstr6ProcessSumData("");
			this._bindPrSummaryProperty(); // PR Summary Property
			var oShow = {
				"count": true,
				"taxValue": true,
				"TotalTax": true,
				"enablecount": false,
				"enabletaxValue": false,
				"enableTotalTax": false,
				"digigst": true,
				"asp": true,
				"gstn": true,
				"diff": true,
				"enabledigigst": false,
				"enableasp": false,
				"enablegstn": false,
				"enablediff": false,
				"check": false
			};
			var oModel = new sap.ui.model.json.JSONModel(oShow);
			this.getView().setModel(oModel, "showing");
			var vDate = new Date();
			this.byId("idTaxperiodGstr6pro").setDateValue(vDate);
			this.byId("idTaxperiodGstr6pro").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxperiodGstr6pro").$().find("input").attr("readonly", true);
				}
			});
			this.byId("id_disCrEligTax").setDateValue(vDate);
			this.byId("id_disCrEligTax").setMaxDate(new Date());
			this.byId("id_disCrEligTax").addEventDelegate({
				onAfterRendering: function () {
					that.byId("id_disCrEligTax").$().find("input").attr("readonly", true);
				}
			});
			this.byId("id_disCrEligTax1").setDateValue(vDate);
			this.byId("id_disCrEligTax1").setMaxDate(new Date());
			this.byId("id_disCrEligTax1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("id_disCrEligTax1").$().find("input").attr("readonly", true);
				}
			});
			this.byId("id_disCrEligTax2").setDateValue(vDate);
			this.byId("id_disCrEligTax2").setMaxDate(new Date());
			this.byId("id_disCrEligTax2").addEventDelegate({
				onAfterRendering: function () {
					that.byId("id_disCrEligTax2").$().find("input").attr("readonly", true);
				}
			});
			this.byId("id_disCrEligTax3").setDateValue(vDate);
			this.byId("id_disCrEligTax3").setMaxDate(vDate);
			this.byId("id_disCrEligTax3").addEventDelegate({
				onAfterRendering: function () {
					that.byId("id_disCrEligTax3").$().find("input").attr("readonly", true);
				}
			});
			this.onLoadFy();
		},

		onPressUpdateGstnData: function () {
			var that = this;
			var oData = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData(),
				aIndex = this.byId("idPREntityTableNew").getSelectedIndices();
			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Please select atleast one GSTIN", {
					styleClass: "sapUiSizeCompact"
				});

				return;
			}
			//	var vTaxPeriod;
			var aGstin = [];
			for (var i = 0; i < aIndex.length; i++) {
				aGstin.push(oData.resp[aIndex[i]].gstin);
				//aGstin.push(oData[aIndex[i]].gstin);
			}
			var oTaxPeriod = this.getView().byId("idTaxperiodGstr6pro").getValue();
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
				"Do you want to Update GSTN Data for GSTR-6?", {
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
					url: "/aspsapapi/getGstr6EntityReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length != 0) {
							for (var i = 0; i < data.resp.length; i++) {
								if (data.resp[i].msg.split(",")[0] != 'Success') {
									if (data.resp[i].msg) {
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
							that.getGstr6ProcessSumData();
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

		onLoadFy: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getAllFy.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json",
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
						// that.EntityTabBind();
					} else {
						that.getView().setModel(new JSONModel([]), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oFyModel");
				that.getView().getModel("oFyModel").refresh(true);
			});
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				this.getGstn();
				// this.getGstr6ProcessSumData(""); commented to remove duplicate api call on 11.05.2023
				// this.onPressSummaryGo();

				this.getGstr6StateCodeInt();
				var object = {
					"aprFlag": false,
					"mayFlag": false,
					"junFlag": false,
					"julFlag": false,
					"augFlag": false,
					"sepFlag": false,
					"octFlag": false,
					"novFlag": false,
					"decFlag": false,
					"janFlag": false,
					"febFlag": false,
					"marFlag": false,
				};
				this.getView().setModel(new JSONModel(object), "month");
			}

			var Key = this.getView().getModel("ReturnKey").getProperty("/key");
			this.byId("dpBulkSavegstrNew1").setVisible(true);
			this.byId("dpBulkSavegstrNew1a").setVisible(false);
			this._onRouteMatched(Key);
		},
		onGstr2BFullScreen: function (oEvt) {
			// var data = this.getView().getModel("GSTR2BTable").getData();
			if (oEvt === "open") {
				this.byId("closebut2B").setVisible(true);
				this.byId("openbut2B").setVisible(false);
				this.byId("G2B").setFullScreen(true);
				this.byId("idgetVtablegstr6progstr2New").setVisibleRowCount(20);
			} else {
				this.byId("closebut2B").setVisible(false);
				this.byId("openbut2B").setVisible(true);
				this.byId("G2B").setFullScreen(false);
				this.byId("idgetVtablegstr6progstr2New").setVisibleRowCount(10);
			}
		},
		getGstn: function () {

			var oView = this.getView();
			var postData = {
				"req": {
					"entityId": $.sap.entityID
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr6DataSecForUser.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oGstnData = new sap.ui.model.json.JSONModel(data.resp[0].dataSecurity.gstin);
					oView.setModel(oGstnData, "Gstin");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr6DataSecForUser : Error");
				});
			});

		},

		_onRouteMatched: function (key) {
			if (key === "DashBoard") {
				this.onPressGstr6SummaryDashBoard();
			} else {
				this.getGstr6ProcessSumData("");
			}
		},

		onPressGstr6SummaryDashBoard: function (oEvent) {
			this.oView.byId("idTaxSummary").setDateValue($.sap.Date);
			this.oView.byId("idgstingstrnew1").setSelectedKey($.sap.DashGSTIN);

			this.byId("dpBulkSavegstrNew1").setVisible(false);
			this.byId("dpBulkSavegstrNew1a").setVisible(true);

		},

		onPressIconTabBar: function (oEvent) {
			if (oEvent.getParameters().selectedKey === "gstr6Summary") {
				this.getGstr6ProcessSumData("");
			} else if (oEvent.getParameters().selectedKey === "get6a") {
				var that = this;
				var vDate = new Date();
				this.byId("idTaxPeriodDetailed").setDateValue(vDate);
				this.byId("idTaxPeriodDetailed").addEventDelegate({
					onAfterRendering: function () {
						that.byId("idTaxPeriodDetailed").$().find("input").attr("readonly", true);
					}
				});
				this.getGstr6AProcessData("");

			} else if (oEvent.getParameters().selectedKey === "distribution") {
				var vDate = new Date();
				this.byId("id_disCrEligTax").setDateValue(vDate);
				this.getGstr6DistriCrEligData("");
				var req = {
					"req": {
						"taxPeriod": this.byId("id_disCrEligTax").getValue(),
						"gstin": this.byId("idgstinDist").getSelectedKeys().toString(),
						"returnType": "GSTR6"
					}
				};
				this.buttonVisDis(req);
				// this.onSelectionGstr6Distribution()
			} else if (oEvent.getParameters().selectedKey === "gstr6") {
				this._getGstr6A();

			}

		},

		buttonVisDis: function (req) {
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

		onPressProcessGoGstr6: function (oEvent) {
			this.getGstr6ProcessSumData("");
		},

		onPressAdaptFilter: function (oEvent) {
			if (oEvent.getSource().getId().includes("idFilterProcess")) {
				this.flag = true;
			} else {
				this.flag = false;
			}
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}

			this._oAdpatFilter.open();

		},

		onPressGoGstr6AProcess: function (oEvent) {
			var oGstData = this.oView.byId("idGetPRSummaryMainTitleGstr6").getText();

			if (oGstData === "GSTN Data") {
				this.getGstr6AProcessData("");
			} else if (oGstData === "Review Summary") {
				// this.getGstr6ADetailedDataFinal(postData);
				this.getGstr6ADetailedData("");
			}
		},

		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				if (this.flag === true) {
					this.getGstr6ProcessSumData("");
				} else {
					this.onPressSummaryGo();
				}
			}
		},

		_getOtherFiltersASP: function (search) {
			var oDataSecurity = this.getView().getModel("userPermission").getData().respData.dataSecurity.items,
				vDataType = "Inward";

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg && vDataType === "Inward") {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg && vDataType === "Outward") {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel && vDataType === "Outward") {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("sluserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("sluserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("sluserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("sluserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("sluserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("sluserAccess6").getSelectedKeys();
			}
			return;
		},

		getGstr6ProcessSumData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idgstingstr6").getSelectedKeys(),
					}
				}
			};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);

			}
			this.getGstr6ProcessSumDataFinal(postData);
		},

		getGstr6ProcessSumDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr6ProcessedData.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oGstr6PRSumData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6PRSumData, "Gstr6PRSumData");
					}
					var oGstr6PRSumData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oGstr6PRSumData, "Gstr6PRSumData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr6ProcessedData : Error");
					var oGstr6PRSumData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oGstr6PRSumData, "Gstr6PRSumData");

				});
			});
		},

		getGstr6ProcessDetlDataFinal: function (postData) {

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr6ReviewSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
						var oGstr6DetailData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6DetailData, "Gstr6DetailedData");
					}
					var oGstr6DetailData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oGstr6DetailData, "Gstr6DetailedData");
					// that.bindSummaryHeader(data);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr6ReviewSummary : Error");
					var oGstr6DetailData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oGstr6DetailData, "Gstr6DetailedData");
				});
			});
		},

		_getSignFileStatus1: function () {
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [this.oView.byId("idgstingstrnew1").getSelectedKey()]
					}
				}

			};
			this._getSignFileStatus(postData);
		},

		_getSignFileStatus: function (payload) {
			var oView = this.getView();
			payload.req.returnType = "GSTR6";
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstnSaveFileStatus.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					data.resp.submit = true;
					oView.setModel(new JSONModel(data.resp), "SignFileStatus");
				});
		},

		_getSubmitStatus: function (payload) {
			var oView = this.getView();
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"gstin": this.oView.byId("idgstingstrnew1").getSelectedKey(),
					"ret_period": oTaxPeriod
				}
			};
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getProceedToFileStatus.do",
					data: JSON.stringify(postData),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					oView.setModel(new JSONModel(data.resp), "Submit");
				});
		},

		getGstr6ProcessDistDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getDistChannelGstr6RevSum.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
							var oGstr6DstribData = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oGstr6DstribData, "Gstr6DistributionData");
						}
						var oGstr6DstribData = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oGstr6DstribData, "Gstr6DistributionData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("getDistChannelGstr6RevSum : Error");
						var oGstr6DstribData = new sap.ui.model.json.JSONModel(null);
						oView.setModel(oGstr6DstribData, "Gstr6DistributionData");
					});
			});
		},

		getGstr6AProcessData: function (oEntity) {
			var oFromTaxPeriod = this.oView.byId("idFromTaxPeriodDetailed").getValue();
			if (oFromTaxPeriod === "") {
				oFromTaxPeriod = null;
			}
			var oToTaxPeriod = this.oView.byId("idToTaxPeriodDetailed").getValue();
			if (oToTaxPeriod === "") {
				oToTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entity": [$.sap.entityID],
					// "taxPeriod": oTaxPeriod,
					"fromPeriod": oFromTaxPeriod,
					"toPeriod": oToTaxPeriod,
					"tableType": this.oView.byId("iddroptatype2Gstr6").getSelectedKeys(),
					"docType": this.oView.byId("iddropDoctype2Gstr6").getSelectedKeys(),
					"data": [],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idGetfgiGSINTMain2GSTR6").getSelectedKeys(),
					}
				}
			};
			this.getGstr6AProcessDataFinal(postData);
		},

		getGstr6AProcessDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6AProcessedData.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
						}
						var oGstr6APRData = new sap.ui.model.json.JSONModel(data);
						oView.setModel(oGstr6APRData, "Gstr6APRData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr6AProcessedData : Error");
					});
			});
		},

		onSelectionGstr6Distribution: function (oEvent) {
			var vGetKey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			this.getView().byId("id_layout1").setVisible(vGetKey === "Invoice");
			this.getView().byId("id_layout2").setVisible(vGetKey === "InfInvoice");
			this.getView().byId("id_layout3").setVisible(vGetKey === "ForcNote");
			this.getView().byId("id_layout4").setVisible(vGetKey === "Ineli");

			this.getView().byId("idPREntitydist").setVisible(vGetKey === "Invoice");
			this.getView().byId("idPREntitydist1").setVisible(vGetKey === "InfInvoice");
			this.getView().byId("idPREntitydist2").setVisible(vGetKey === "ForcNote");
			this.getView().byId("idPREntitydist3").setVisible(vGetKey === "Ineli");

			if (vGetKey === "Invoice") {
				this.getGstr6DistriCrEligData();
				if (this.getView().byId("idgstinDist").getSelectedKeys().length > 1 ||
					this.getView().byId("idgstinDist").getSelectedKeys().length === 0) {
					if (this.getView().getModel("buttonVis") !== undefined) {
						this.getView().getModel("buttonVis").getData().dataEditable = true;
						this.getView().getModel("buttonVis").refresh(true);
					}
				} else {
					var req = {
						"req": {
							"taxPeriod": this.byId("id_disCrEligTax").getValue(),
							"gstin": this.byId("idgstinDist").getSelectedKeys().toString(),
							"returnType": "GSTR6"
						}
					};
					this.buttonVisDis(req);
				}
			} else if (vGetKey === "InfInvoice") {
				this.getGstr6DistriCrInEligData("");
				if (this.getView().byId("idgstinDist1").getSelectedKeys().length > 1 ||
					this.getView().byId("idgstinDist1").getSelectedKeys().length === 0) {
					if (this.getView().getModel("buttonVis") !== undefined) {
						this.getView().getModel("buttonVis").getData().dataEditable = true;
						this.getView().getModel("buttonVis").refresh(true);
					}
				} else {
					var req1 = {
						"req": {
							"taxPeriod": this.byId("id_disCrEligTax1").getValue(),
							"gstin": this.byId("idgstinDist1").getSelectedKeys().toString(),
							"returnType": "GSTR6"
						}
					};
					this.buttonVisDis(req1);
				}
			} else if (vGetKey === "ForcNote") {
				this.getGstr6ReDistriCrEligData("");
				if (this.getView().byId("idgstinDist2").getSelectedKeys().length > 1 ||
					this.getView().byId("idgstinDist2").getSelectedKeys().length === 0) {
					if (this.getView().getModel("buttonVis") !== undefined) {
						this.getView().getModel("buttonVis").getData().dataEditable = true;
						this.getView().getModel("buttonVis").refresh(true);
					}
				} else {
					var req2 = {
						"req": {
							"taxPeriod": this.byId("id_disCrEligTax2").getValue(),
							"gstin": this.byId("idgstinDist2").getSelectedKeys().toString(),
							"returnType": "GSTR6"
						}
					};
					this.buttonVisDis(req2);
				}
			} else {
				this.getGstr6ReDistriCrInEligData("");
				if (this.getView().byId("id_disCrEligTax3").getSelectedKeys().length > 1 ||
					this.getView().byId("id_disCrEligTax3").getSelectedKeys().length === 0) {
					if (this.getView().getModel("buttonVis") !== undefined) {
						this.getView().getModel("buttonVis").getData().dataEditable = true;
						this.getView().getModel("buttonVis").refresh(true);
					}
				} else {
					var req3 = {
						"req": {
							"taxPeriod": this.byId("idgstinDist3").getValue(),
							"gstin": this.byId("id_disCrEligTax3").getSelectedKey(),
							"returnType": "GSTR6"
						}
					};
					this.buttonVisDis(req3);
				}
			}
		},

		onUpload6aSummary: function () {
			var oBundle = this.getResourceBundle(),
				oUploader = this.byId("fuGstr6aSumm"),
				idx = this.byId("rg6aFileType").getSelectedIndex();

			if (!oUploader.getValue()) {
				MessageBox.information(oBundle.getText("gstr2aSelectFile"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			switch (idx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/gstr6aFileUploadDocuments.do");
				break;
			case 1:
				break;
			}
			oUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuGstr6aSumm").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.hdr.message || sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		OnpressGetStatus: function (oEvent, oFlag) {
			this.postpayloadgstr6 = {};
			if (oFlag === "P") {
				this.vFlagGstr6A = "P";
				var oSelectedItem = this.getView().byId("idGetPREntityTableGstr6").getSelectedIndices();
				var oModelForTab1 = this.byId("idGetPREntityTableGstr6").getModel("Gstr6APRData").getData().resp;
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var oTaxPeriod = this.oView.byId("idTaxPeriodDetailed").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				};
			} else if (oFlag === "S") {
				this.vFlagGstr6A = "S";
				var oTaxPeriod = this.oView.byId("idTaxPeriodDetailed1").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": [this.getView().byId("idGetfgiGSINTMain2GSTR61").getSelectedKey()]
					}
				};
			} else {
				this.vFlagGstr6A = "LP";
				var getSucessdata = oEvent.getSource().getBindingContext("Gstr6APRData").sPath.split("/")[2];
				var oModelForTab1 = this.byId("idGetPREntityTableGstr6").getModel("Gstr6APRData").getData().resp;
				var vGstin = oModelForTab1[getSucessdata].gstin;
				var oFromTaxPeriod = this.oView.byId("idFromTaxPeriodDetailed").getValue();
				if (oFromTaxPeriod === "") {
					oFromTaxPeriod = null;
				}
				var oToTaxPeriod = this.oView.byId("idToTaxPeriodDetailed").getValue();
				if (oToTaxPeriod === "") {
					oToTaxPeriod = null;
				}

				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oFromTaxPeriod,
						"gstin": [vGstin]
					}
				};
			}
			if (!this._oDialogGstr6A) {
				this._oDialogGstr6A = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.Gstr6AStatus", this);
				this.getView().addDependent(this._oDialogGstr6A);
			}
			this.postpayloadgstr6 = postData;
			this.getGstr6ASucessStatusDataFinal();
			this.byId("id_gstinPopupget2").setText(vGstin);
			this.byId("id_123TaxProcessGstr6A").setValue(this.oView.byId("idFromTaxPeriodDetailed").getValue());

			var minDate = this.byId("id_123TaxProcessGstr6A").getDateValue();
			var maxDate = this.byId("id_123TaxProcessGstr6A").getDateValue();
			minDate = new Date(minDate.setDate("01"));
			maxDate = new Date(maxDate.setDate("30"));

			this.byId("id_TaxProcessGstr6A").setMinDate(minDate);
			this.byId("id_TaxProcessGstr6A").setMaxDate(maxDate);
			this.byId("id_TaxProcessGstr6A").setValue(postData.req.taxPeriod);
			this.byId("id_123TaxProcessGstr6A").setValue(this.oView.byId("idToTaxPeriodDetailed").getValue());
			this._oDialogGstr6A.open();
		},

		onCloseDialog: function () {
			this.onPressGoGstr6AProcess();
			this._oDialogGstr6A.close();
		},

		getGstr6ASucessStatusDataFinal: function () {
			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.postpayloadgstr6.req.taxPeriod,
					"gstin": this.postpayloadgstr6.req.gstin
				}
			};

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr6aDetailStatus.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
						}
						that.bindGstr6DetailStatus(data);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("getGstr6aDetailStatus : Error");
					});
			});
		},
		bindGstr6DetailStatus: function (data) {
			this._showing1(false);
			var oView = this.getView();
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = false;
				data.resp.lastCall[i].b2baFlag = false;
				data.resp.lastCall[i].cdnFlag = false;
				data.resp.lastCall[i].cdnaFlag = false;
				data.resp.lastCall[i].isdFlag = false;
				data.resp.lastCall[i].isdaFlag = false;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = false;
				data.resp.lastSuccess[i].b2baFlag = false;
				data.resp.lastSuccess[i].cdnFlag = false;
				data.resp.lastSuccess[i].cdnaFlag = false;
				data.resp.lastSuccess[i].isdFlag = false;
				data.resp.lastSuccess[i].isdaFlag = false;
			}
			var oGstr6ASucessData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr6ASucessData, "Gstr6ASucess");
		},

		onPressProcessStatusGO: function () {
			var gstin = this.byId("id_gstinPopupget2").getText();
			var oTaxPeriod = this.byId("id_TaxProcessGstr6A").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"gstin": [gstin]
				}
			};
			this.postpayloadgstr6 = postData;
			this.getGstr6ASucessStatusDataFinal();
			this.byId("id_gstinPopupget2").setText(gstin);
			this.byId("id_TaxProcessGstr6A").setValue(postData.req.taxPeriod);
			this._oDialogGstr6A.open();
		},

		onPressGetGstr2a: function (oEvent, flag, gstin, month) {
			if (!this._oDialogGstr6A) {
				this._oDialogGstr6A = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.Gstr6AStatus", this);
				this.getView().addDependent(this._oDialogGstr6A);
			}

			var vTaxPeriod = this._getTaxPeriod(month),
				postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": vTaxPeriod,
						"gstin": [gstin]
					}
				};
			this.postpayloadgstr6 = postData;
			this.getGstr6ASucessStatusDataFinal();
			this.byId("id_gstinPopupget2").setText(gstin);

			this.byId("id_123TaxProcessGstr6A").setValue(postData.req.taxPeriod);

			var vDate = this.byId("id_123TaxProcessGstr6A").getDateValue();
			var minDate = new Date(vDate.setDate("01"));
			var maxDate = new Date(vDate.setDate("30"));
			this.byId("id_TaxProcessGstr6A").setMinDate(minDate);
			this.byId("id_TaxProcessGstr6A").setMaxDate(maxDate);
			this.byId("id_TaxProcessGstr6A").setValue(postData.req.taxPeriod);
			this._oDialogGstr6A.open();
		},

		_getTaxPeriod: function (month) {
			var aMonth = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"],
				FY = this.getView().byId("dtFinYearGstrNew12").getSelectedKey(),
				idx = aMonth.indexOf(month) + 1,
				FYA = FY.split("-")[0];

			if (idx < 4) {
				return ('' + idx).padStart(2, '0') + (+FYA + 1);
			} else {
				return ('' + idx).padStart(2, '0') + FYA;
			}
		},

		onGstr6aStatsFailed: function (oEvent, type) {
			var payload = {
				"req": {
					"gstin": this.byId("id_gstinPopupget2").getText(),
					"taxPeriod": this.byId("id_TaxProcessGstr6A").getValue(),
					"returnType": "GSTR6A",
					"section": type
				}
			};
			this._oDialogGstr6A.setBusy(true);
			if (!this._msgPopover) {
				this._msgPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.MsgPopover", this);
				this.getView().addDependent(this._msgPopover);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCallFailureErrMsg.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._msgPopover.setModel(new JSONModel(data.resp), "PopoverMsg");
					this._msgPopover.openBy(oEvent.getSource());
					this._oDialogGstr6A.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialogGstr6A.setBusy(false);
				}.bind(this));
		},

		onChangeSegmentStatus: function (oEvent) {
			var key = oEvent.getSource().getSelectedKey();
			this.byId("idtitle1").setVisible(key === "LCS");
			this.byId("idtitle2").setVisible(key === "LSS");
			this.byId("idgetVtablegstr6").setVisible(key === "LCS");
			this.byId("idgetStablegstr6").setVisible(key === "LSS");
		},

		onChangeSegmentProcessStatus: function (oEvent) {
			if (oEvent.getSource().getSelectedKey() === "LCS") {
				sap.ui.getCore().byId("idgettitle").setVisible(true);
				sap.ui.getCore().byId("idGetSucessTitle").setVisible(false);
				sap.ui.getCore().byId("idgetVtablegstr6pro").setVisible(true);
				sap.ui.getCore().byId("idgetStatusgstr6").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "LSS") {
				sap.ui.getCore().byId("idgettitle").setVisible(false);
				sap.ui.getCore().byId("idGetSucessTitle").setVisible(true);
				sap.ui.getCore().byId("idgetVtablegstr6pro").setVisible(false);
				sap.ui.getCore().byId("idgetStatusgstr6").setVisible(true);
			}
		},

		handleLinkPressGetGSTINMain1: function (oEvent) {
			this.oView.byId("id_filterDetail").setVisible(false);
			this.oView.byId("id_filterDetailDetail").setVisible(true);
			this.oView.byId("idGetPRGSTINGstr6").setVisible(true);
			this.oView.byId("idGetPREntityGstr6").setVisible(false);
			this.oView.byId("idGetPRSummaryMainNavBackGstr6").setVisible(true);
			this.oView.byId("idGetPRSummaryMainTitleGstr6").setText("Review Summary");

			var a = oEvent.getSource().getBindingContext("Gstr6APRData").getObject();
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			var fromDate = this.byId("idFromTaxPeriodDetailed").getDateValue();
			fromDate = new Date(fromDate.setDate("01"));
			this.byId("idRSFromTaxPeriodDetailed").setMaxDate(new Date());
			this.byId("idRSToTaxPeriodDetailed").setMinDate(fromDate);
			this.byId("idRSToTaxPeriodDetailed").setMaxDate(new Date());

			this.oView.byId("idRSFromTaxPeriodDetailed").setValue(this.oView.byId("idFromTaxPeriodDetailed").getValue());
			this.oView.byId("idRSToTaxPeriodDetailed").setValue(this.oView.byId("idToTaxPeriodDetailed").getValue());
			this.oView.byId("idGetfgiGSINTMain2GSTR61").setSelectedKey(a.gstin);
			this.oView.byId("iddroptatype2Gstr61").setSelectedKeys(this.oView.byId("iddroptatype2Gstr6").getSelectedKeys());
			this.oView.byId("iddropDoctype2Gstr61").setSelectedKeys(this.oView.byId("iddropDoctype2Gstr6").getSelectedKeys());

			this.getGstr6ADetailedData("");
			// this.oView.byId("idGetfgiGSINTMain2GSTR6").setSelectedKeys(abc);
		},

		handleLinkPressGETGstr6Back: function (oEvent5) {
			this.oView.byId("idGetPREntityGstr6").setVisible(true);
			this.oView.byId("idGetPRGSTINGstr6").setVisible(false);
			// this.oView.byId("idGetPRSWGstr6").setVisible(false);
			// this.oView.byId("idGetfgiGSINTMainGstr6").setVisible(true);
			// this.oView.byId("idGetfgiANX1MainGstr6").setVisible(true);
			// this.oView.byId("idGetfgiDocTypeMainGstr").setVisible(true);
			this.oView.byId("id_filterDetail").setVisible(true);
			this.oView.byId("id_filterDetailDetail").setVisible(false);
			this.oView.byId("idGetPRSummaryMainNavBackGstr6").setVisible(false);
			this.oView.byId("idGetPRSummaryMainTitleGstr6").setText("GSTN Data");
			// this.oView.byId("idGetfgiGSINTMain2GSTR6").setSelectedKeys(abc);
		},

		getGstr6ADetailedData: function (oEntity) {
			var oFromTaxPeriod = this.oView.byId("idRSFromTaxPeriodDetailed").getValue();
			if (oFromTaxPeriod === "") {
				oFromTaxPeriod = null;
			}
			var oToTaxPeriod = this.oView.byId("idRSToTaxPeriodDetailed").getValue();
			if (oToTaxPeriod === "") {
				oToTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					// "taxPeriod": oTaxPeriod,
					"fromPeriod": oFromTaxPeriod,
					"toPeriod": oToTaxPeriod,
					"tableType": this.oView.byId("iddroptatype2Gstr61").getSelectedKeys(),
					"docType": this.oView.byId("iddropDoctype2Gstr61").getSelectedKeys(),
					"data": [],
					"dataSecAttrs": {
						"GSTIN": [this.oView.byId("idGetfgiGSINTMain2GSTR61").getSelectedKey()],
					}
				}
			};
			this.getGstr6ADetailedDataFinal(postData);
		},

		getGstr6ADetailedDataFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6ASummaryData.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
							var oGstr6ADetailData = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oGstr6ADetailData, "Gstr6ADetailed");
						} else {
							that.bindGstr6ASummaryData(data);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr6ASummaryData : Error");
					});
			});
		},

		bindGstr6ASummaryData: function (data) {
			var oView = this.getView();
			var oData = {
				"table": "Total",
				"count": 0,
				"inVoiceVal": 0,
				"taxableValue": 0,
				"totalTax": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0
			};
			for (var i = 0; i < data.resp.length; i++) {
				oData.count += data.resp[i].count;
				oData.inVoiceVal += data.resp[i].inVoiceVal;
				oData.taxableValue += data.resp[i].taxableValue;
				oData.totalTax += data.resp[i].totalTax;
				oData.igst += data.resp[i].igst;
				oData.cgst += data.resp[i].cgst;
				oData.sgst += data.resp[i].sgst;
				oData.cess += data.resp[i].cess;
			}
			data.resp.push(oData);
			var oGstr6ADetailData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr6ADetailData, "Gstr6ADetailed");
		},

		onPressGstrNew11Summary: function (oEvent) {
			var a = oEvent.getSource().getBindingContext("Gstr6PRSumData").getObject();
			var that = this;
			this.oView.byId("idTaxSummary").setValue(this.oView.byId("idTaxperiodGstr6pro").getValue());
			this.byId("idTaxSummary").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxSummary").$().find("input").attr("readonly", true);
				}
			});
			this.oView.byId("idgstingstrnew1").setSelectedKey(a.gstin);
			if (a.authToken == "Active") {
				this.authState = "A";
			} else {
				this.authState = "I";
			}
			// this.getGstr6ProcessDetlData("");
			// this.getGstr6ProcessDetlDataFinal(postData);
			// this.getGstr6ProcessDistriData("");
			this.onPressSummaryGo();
			this.buttonVis();
			// this.getUpdateSaveToGstinButtonIntrigration();

			this.byId("dpBulkSavegstrNew1").setVisible(false);
			this.byId("dpBulkSavegstrNew1a").setVisible(true);
		},

		buttonVis: function () {
			var req = {
				"req": {
					"taxPeriod": this.byId("idTaxSummary").getValue(),
					"gstin": this.byId("idgstingstrnew1").getSelectedKey(),
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
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						GSTR3BModel.setData(data.resp);
						oTaxReGstnView.setModel(GSTR3BModel, "buttonVis");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onPressSummaryGo: function () {
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [this.oView.byId("idgstingstrnew1").getSelectedKey()]
					}
				}
			};
			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(postData.req.dataSecAttrs);
			}
			this.getGstr6ProcessDetlDataFinal(postData);
			this.getGstr6ProcessDistDataFinal(postData);
			this.getUpdateSaveToGstinButtonIntrigration(postData);
			this._getSignFileStatus1();
			this._getSubmitStatus();
		},

		onPressBackGSTRNew1: function (oEvent) {
			this.byId("dpBulkSavegstrNew1").setVisible(true);
			this.byId("dpBulkSavegstrNew1a").setVisible(false);
		},

		onSelectCheckBoxCol: function (oEvent) {
			var oVisiModel = this.getView().getModel("showing"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.asp && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.enableasp = true;

			} else if (!oVisiData.asp && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.enablegstn = true;

			} else if (!oVisiData.asp && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.enablediff = true;

			} else {
				oVisiData.enableasp = false;
				oVisiData.enablegstn = false;
				oVisiData.enablediff = false;
			}
			oVisiModel.refresh(true);
		},

		onSelectCheckBoxCol1: function (oEvent) {
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("idPREntityTableColNew6").setVisible(true);
				this.oView.byId("idPREntityTableColNew7").setVisible(true);
				this.oView.byId("idPREntityTableColNew8").setVisible(true);
				this.oView.byId("idPREntityTableColNew9").setVisible(true);
				this.oView.byId("idPREntityTableColNew10").setVisible(true);
			} else {
				this.oView.byId("idPREntityTableColNew6").setVisible(false);
				this.oView.byId("idPREntityTableColNew7").setVisible(false);
				this.oView.byId("idPREntityTableColNew8").setVisible(false);
				this.oView.byId("idPREntityTableColNew9").setVisible(false);
				this.oView.byId("idPREntityTableColNew10").setVisible(false);
			}
		},

		onSelectCheckBoxCol2: function (oEvent) {
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("idPREntityTableColNew11").setVisible(true);
				this.oView.byId("idPREntityTableColNew12").setVisible(true);
				this.oView.byId("idPREntityTableColNew13").setVisible(true);
				this.oView.byId("idPREntityTableColNew14").setVisible(true);
				this.oView.byId("idPREntityTableColNew15").setVisible(true);
			} else {
				this.oView.byId("idPREntityTableColNew11").setVisible(false);
				this.oView.byId("idPREntityTableColNew12").setVisible(false);
				this.oView.byId("idPREntityTableColNew13").setVisible(false);
				this.oView.byId("idPREntityTableColNew14").setVisible(false);
				this.oView.byId("idPREntityTableColNew15").setVisible(false);
			}
		},

		expandCollapseAnx2SummaryNew2: function (oEvent, controller) {
			if (oEvent.getSource().getId().includes("idNewA2Exp2")) {
				this.byId("tabOutward1gstrnew1").expandToLevel(1);
				this.byId("tabOutward1gstrnew1").setVisibleRowCount(12);
				this.byId("tabDistTablegstrnew").expandToLevel(1);
				this.byId("tabDistTablegstrnew").setVisibleRowCount(12);
			} else {
				this.byId("tabOutward1gstrnew1").collapseAll();
				this.byId("tabOutward1gstrnew1").setVisibleRowCount(5);
				this.byId("tabDistTablegstrnew").collapseAll();
				this.byId("tabDistTablegstrnew").setVisibleRowCount(5);
			}
		},

		expandCollapseGstr6Summary: function (oEvent, controller) {
			if (oEvent.getSource().getId().includes("idGET2ExpGstr6")) {
				this.byId("idGettabRet1mGstr6").expandToLevel(1);
				this.byId("idGettabRet1mGstr6").setVisibleRowCount(10);
			} else {
				this.byId("idGettabRet1mGstr6").collapseAll();
				this.byId("idGettabRet1mGstr6").setVisibleRowCount(5);
			}
		},

		onPressGoCrElig: function () {
			if (this.oView.byId("idgstinDist").getSelectedKeys().length > 1 ||
				this.oView.byId("idgstinDist").getSelectedKeys().length === 0) {
				if (this.getView().getModel("buttonVis") !== undefined) {
					this.getView().getModel("buttonVis").getData().dataEditable = true;
					this.getView().getModel("buttonVis").refresh(true);
				}
			} else {
				var req = {
					"req": {
						"taxPeriod": this.byId("id_disCrEligTax").getValue(),
						"gstin": this.byId("idgstinDist").getSelectedKeys().toString(),
						"returnType": "GSTR6"
					}
				};
				this.buttonVisDis(req);
			}
			this.getGstr6DistriCrEligData();
		},

		getGstr6DistriCrEligData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("id_disCrEligTax").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idgstinDist").getSelectedKeys()
					}
				}
			}
			this.getGstr6DistriCrEligDataFinal(postData);
		},

		getGstr6DistriCrEligDataFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary1.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that._nicPagination(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						oView.setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		_nicPagination: function (header) {
			var vCount = this.getView().byId("idPageN").getSelectedKey();
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoN").setText("/ " + pageNumber);
			this.byId("inPageNoN").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN").setValue(pageNumber);
				this.byId("inPageNoN").setEnabled(false);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			} else if (this.byId("inPageNoN").getValue() === "" || this.byId("inPageNoN").getValue() === "0") {
				this.byId("inPageNoN").setValue(1);
				this.byId("inPageNoN").setEnabled(true);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
			} else {
				this.byId("inPageNoN").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN").setEnabled(true);
				this.byId("btnLastN").setEnabled(true);
			} else {
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			}
			this.byId("btnPrevN").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN").setEnabled(false);
				}
				if (!this.byId("btnNextN").getEnabled()) {
					this.byId("btnNextN").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN")) {
				var vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN").setEnabled(false);
				}
				if (!this.byId("btnLastN").getEnabled()) {
					this.byId("btnLastN").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN")) {
				vValue = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN").setValue(vValue);
			this.nicCredentialTabl1(vValue);
		},

		onSubmitPagination: function () {
			var vPageNo = this.byId("inPageNoN").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl1(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl1: function (vPageNo) {
			var that = this;
			var oTaxPeriod = this.byId("id_disCrEligTax").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.byId("idgstinDist").getSelectedKeys()
					}
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6distributed/summary1.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._nicPagination(data.hdr);
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].edit = false;
					}
					var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onPNChange: function (oEvent, vPageNo) {
			var oTaxPeriod = this.byId("id_disCrEligTax").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/gstr6distributed/summary1.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": oEvent.getSource().getSelectedKey()
					},
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": this.byId("idgstinDist").getSelectedKeys()
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: onicPath,
						contentType: "application/json",
						data: JSON.stringify(searchInfo)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that._nicPagination(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						that.getView().setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});

		},

		getGstr6DistriCrInEligData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("id_disCrEligTax1").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN1").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idgstinDist1").getSelectedKeys()
					}
				}
			}
			this.getGstr6DistriCrInEligDataFinal(postData);
		},

		getGstr6DistriCrInEligDataFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary2.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that._nicPagination1(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6DistrCrInElfData = new sap.ui.model.json.JSONModel(data.resp);
						oView.setModel(oGstr6DistrCrInElfData, "Gstr6DistCrInEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		_nicPagination1: function (header) {
			var vCount = this.getView().byId("idPageN1").getSelectedKey();
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoN1").setText("/ " + pageNumber);
			this.byId("inPageNoN1").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN1").setValue(pageNumber);
				this.byId("inPageNoN1").setEnabled(false);
				this.byId("btnPrevN1").setEnabled(false);
				this.byId("btnNextN1").setEnabled(false);
				this.byId("btnFirstN1").setEnabled(false);
				this.byId("btnLastN1").setEnabled(false);
			} else if (this.byId("inPageNoN1").getValue() === "" || this.byId("inPageNoN1").getValue() === "0") {
				this.byId("inPageNoN1").setValue(1);
				this.byId("inPageNoN1").setEnabled(true);
				this.byId("btnPrevN1").setEnabled(false);
				this.byId("btnFirstN1").setEnabled(false);
			} else {
				this.byId("inPageNoN1").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN1").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN1").setEnabled(true);
				this.byId("btnLastN1").setEnabled(true);
			} else {
				this.byId("btnNextN1").setEnabled(false);
				this.byId("btnLastN1").setEnabled(false);
			}
			this.byId("btnPrevN1").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN1").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination1: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN1").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN1")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN1").setEnabled(false);
				}
				if (!this.byId("btnNextN1").getEnabled()) {
					this.byId("btnNextN1").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN1")) {
				var vPageNo = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN1").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN1")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN1").setEnabled(false);
				}
				if (!this.byId("btnLastN1").getEnabled()) {
					this.byId("btnLastN1").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN1")) {
				vValue = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN1").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN1").setValue(vValue);
			this.nicCredentialTabl2(vValue);
		},

		onSubmitPagination1: function () {
			var vPageNo = this.byId("inPageNoN1").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl2(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl2: function (vPageNo) {
			var that = this;
			var oTaxPeriod = this.byId("id_disCrEligTax1").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN1").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.byId("idgstinDist1").getSelectedKeys()
					}
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6distributed/summary2.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.resp.length === 0) {
					// 	MessageBox.information("No Data");
					// }
					that._nicPagination1(data.hdr);
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].edit = false;
					}
					var oGstr6DistrCrInElfData = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oGstr6DistrCrInElfData, "Gstr6DistCrInEligData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onPNChange1: function (oEvent, vPageNo) {
			var oTaxPeriod = this.byId("id_disCrEligTax1").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/gstr6distributed/summary2.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": oEvent.getSource().getSelectedKey()
					},
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": this.byId("idgstinDist1").getSelectedKeys()
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: onicPath,
						contentType: "application/json",
						data: JSON.stringify(searchInfo)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination1(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						that.getView().setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});

		},

		onPressGoCrInElig: function () {
			if (this.oView.byId("idgstinDist1").getSelectedKeys().length > 1 ||
				this.oView.byId("idgstinDist1").getSelectedKeys().length === 0) {
				if (this.getView().getModel("buttonVis") !== undefined) {
					this.getView().getModel("buttonVis").getData().dataEditable = true;
					this.getView().getModel("buttonVis").refresh(true);
				}
			} else {
				var req1 = {
					"req": {
						"taxPeriod": this.byId("id_disCrEligTax1").getValue(),
						"gstin": this.byId("idgstinDist1").getSelectedKeys().toString(),
						"returnType": "GSTR6"
					}
				};
				this.buttonVisDis(req1);
			}
			this.getGstr6DistriCrInEligData();
		},

		getGstr6ReDistriCrEligData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("id_disCrEligTax2").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN2").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idgstinDist2").getSelectedKeys()
					}
				}
			}
			this.getGstr6ReDistriCrEligDataFinal(postData);
		},

		getGstr6ReDistriCrEligDataFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary3.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination2(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6ReDistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						oView.setModel(oGstr6ReDistrCrElfData, "Gstr6ReDistCrEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		_nicPagination2: function (header) {
			var vCount = this.getView().byId("idPageN2").getSelectedKey();
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoN2").setText("/ " + pageNumber);
			this.byId("inPageNoN2").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN2").setValue(pageNumber);
				this.byId("inPageNoN2").setEnabled(false);
				this.byId("btnPrevN2").setEnabled(false);
				this.byId("btnNextN2").setEnabled(false);
				this.byId("btnFirstN2").setEnabled(false);
				this.byId("btnLastN2").setEnabled(false);
			} else if (this.byId("inPageNoN2").getValue() === "" || this.byId("inPageNoN2").getValue() === "0") {
				this.byId("inPageNoN2").setValue(1);
				this.byId("inPageNoN2").setEnabled(true);
				this.byId("btnPrevN2").setEnabled(false);
				this.byId("btnFirstN2").setEnabled(false);
			} else {
				this.byId("inPageNoN1").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN2").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN2").setEnabled(true);
				this.byId("btnLastN2").setEnabled(true);
			} else {
				this.byId("btnNextN2").setEnabled(false);
				this.byId("btnLastN2").setEnabled(false);
			}
			this.byId("btnPrevN2").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN2").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination2: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN2").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN2")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN2").setEnabled(false);
				}
				if (!this.byId("btnNextN2").getEnabled()) {
					this.byId("btnNextN2").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN2")) {
				var vPageNo = parseInt(this.byId("txtPageNoN2").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN2").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN2").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN2")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN2").setEnabled(false);
				}
				if (!this.byId("btnLastN2").getEnabled()) {
					this.byId("btnLastN2").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN2")) {
				vValue = parseInt(this.byId("txtPageNoN2").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN2").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN2").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN2").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN2").setValue(vValue);
			this.nicCredentialTabl3(vValue);
		},

		onSubmitPagination1: function () {
			var vPageNo = this.byId("inPageNoN2").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN2").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl3(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl3: function (vPageNo) {
			var that = this;
			var oTaxPeriod = this.byId("id_disCrEligTax2").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN2").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.byId("idgstinDist2").getSelectedKeys()
					}
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary3.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination2(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6ReDistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						that.getView().setModel(oGstr6ReDistrCrElfData, "Gstr6ReDistCrEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onPNChange2: function (oEvent, vPageNo) {
			var oTaxPeriod = this.byId("id_disCrEligTax1").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/gstr6distributed/summary3.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": oEvent.getSource().getSelectedKey()
					},
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": this.byId("idgstinDist2").getSelectedKeys()
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: onicPath,
						contentType: "application/json",
						data: JSON.stringify(searchInfo)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination2(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
						that.getView().setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});

		},

		onPressRcrElig: function () {
			if (this.oView.byId("idgstinDist2").getSelectedKeys().length > 1 ||
				this.oView.byId("idgstinDist2").getSelectedKeys().length === 0) {
				if (this.getView().getModel("buttonVis") !== undefined) {
					this.getView().getModel("buttonVis").getData().dataEditable = true;
					this.getView().getModel("buttonVis").refresh(true);
				}
			} else {
				var req1 = {
					"req": {
						"taxPeriod": this.byId("id_disCrEligTax2").getValue(),
						"gstin": this.byId("idgstinDist2").getSelectedKeys().toString(),
						"returnType": "GSTR6"
					}
				};
				this.buttonVisDis(req1);
			}
			this.getGstr6ReDistriCrEligData();
		},

		getGstr6ReDistriCrInEligData: function (oEntity) {
			var oTaxPeriod = this.oView.byId("id_disCrEligTax3").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN3").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idgstinDist3").getSelectedKeys()
					}
				}
			}
			this.getGstr6ReDistriCrInEligDataFinal(postData);
		},

		getGstr6ReDistriCrInEligDataFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary4.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination3(data.hdr);
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6ReDistrCrInElfData = new sap.ui.model.json.JSONModel(data.resp);
						oView.setModel(oGstr6ReDistrCrInElfData, "Gstr6ReDistCrInEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		_nicPagination3: function (header) {
			var vCount = this.getView().byId("idPageN3").getSelectedKey();
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoN3").setText("/ " + pageNumber);
			this.byId("inPageNoN3").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN3").setValue(pageNumber);
				this.byId("inPageNoN3").setEnabled(false);
				this.byId("btnPrevN3").setEnabled(false);
				this.byId("btnNextN3").setEnabled(false);
				this.byId("btnFirstN3").setEnabled(false);
				this.byId("btnLastN3").setEnabled(false);
			} else if (this.byId("inPageNoN3").getValue() === "" || this.byId("inPageNoN3").getValue() === "0") {
				this.byId("inPageNoN3").setValue(1);
				this.byId("inPageNoN3").setEnabled(true);
				this.byId("btnPrevN3").setEnabled(false);
				this.byId("btnFirstN3").setEnabled(false);
			} else {
				this.byId("inPageNoN3").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN3").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN3").setEnabled(true);
				this.byId("btnLastN3").setEnabled(true);
			} else {
				this.byId("btnNextN3").setEnabled(false);
				this.byId("btnLastN3").setEnabled(false);
			}
			this.byId("btnPrevN3").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN3").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination3: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN3").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN3")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN3").setEnabled(false);
				}
				if (!this.byId("btnNextN3").getEnabled()) {
					this.byId("btnNextN3").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN3")) {
				var vPageNo = parseInt(this.byId("txtPageNoN3").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN3").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN3").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN3")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN3").setEnabled(false);
				}
				if (!this.byId("btnLastN3").getEnabled()) {
					this.byId("btnLastN3").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN3")) {
				vValue = parseInt(this.byId("txtPageNoN3").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN3").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN3").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN3").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN3").setValue(vValue);
			this.nicCredentialTabl4(vValue);
		},

		onSubmitPagination3: function () {
			var vPageNo = this.byId("inPageNoN3").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN3").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl4(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl4: function (vPageNo) {
			var that = this;
			var oTaxPeriod = this.byId("id_disCrEligTax3").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN3").getSelectedKey())
				},
				"req": {
					"entity": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.byId("idgstinDist3").getSelectedKeys()
					}
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6distributed/summary4.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// if (data.resp.length === 0) {
						// 	MessageBox.information("No Data");
						// }
						that._nicPagination3();
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
						}
						var oGstr6ReDistrCrInElfData = new sap.ui.model.json.JSONModel(data.resp);
						that.getView().setModel(oGstr6ReDistrCrInElfData, "Gstr6ReDistCrInEligData");

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onPNChange3: function (oEvent, vPageNo) {
			var oTaxPeriod = this.byId("id_disCrEligTax1").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/gstr6distributed/summary4.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": oEvent.getSource().getSelectedKey()
					},
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": this.byId("idgstinDist3").getSelectedKeys()
						}
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: onicPath,
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// if (data.resp.length === 0) {
					// 	MessageBox.information("No Data");
					// }
					that._nicPagination3(data.hdr);
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].edit = false;
					}
					var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oGstr6DistrCrElfData, "Gstr6DistCrEligData");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},

		onPressRcrInElig: function () {
			if (this.oView.byId("idgstinDist3").getSelectedKeys().length > 1 || this.oView.byId("idgstinDist3").getSelectedKeys().length === 0) {
				if (this.getView().getModel("buttonVis") !== undefined) {
					this.getView().getModel("buttonVis").getData().dataEditable = true;
					this.getView().getModel("buttonVis").refresh(true);
				}
			} else {
				var req1 = {
					"req": {
						"taxPeriod": this.byId("id_disCrEligTax3").getValue(),
						"gstin": this.byId("idgstinDist3").getSelectedKeys().toString(),
						"returnType": "GSTR6"
					}
				};
				this.buttonVisDis(req1);
			}
			this.getGstr6ReDistriCrInEligData();
		},

		getGstr6StateCodeInt: function () {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getStates.do",
					contentType: "application/json",
					// data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.states.length === 0) {
						MessageBox.information("No Data");
					}
					var oGstr6StateCodeData = new sap.ui.model.json.JSONModel(data);
					oGstr6StateCodeData.setSizeLimit(5000);
					oView.setModel(oGstr6StateCodeData, "GstStateCode");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		getGstrAButtonIntrigration: function () {
			var oTablIndxPR = this.byId("idGetPREntityTableGstr6").getSelectedIndices();
			var oModelForTab = this.byId("idGetPREntityTableGstr6").getModel("Gstr6APRData").getData().resp;

			var oTaxPeriod = sap.ui.getCore().byId("id_TaxGstrStatusBtn").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": [{
					"gstin": this.oView.byId("idGetfgiGSINTMain2GSTR6").getSelectedKeys(),
					"ret_period": oTaxPeriod
				}]
			};
			for (var i = 0; i < oTablIndxPR.length; i++) {
				postData.req[i].gstin = oModelForTab[oTablIndxPR[i]].gstin;
			}
			this.getGstrAButtonIntrigrationFinal(postData);
		},

		getGstrAButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6aGstnGetSection.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
						} else {
							MessageBox.success("Job Id " + data.resp.jobId + " has been " + data.resp.status);
						}
						// var oGstr6AButtonInt = new sap.ui.model.json.JSONModel(data);
						// oView.setModel(oGstr6AButtonInt, "Gstr6ABtnIntgn");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onGetGstr6ABtnPress: function (oEvent) {
			var that = this;
			var oTablIndxPR = this.byId("idGetPREntityTableGstr6").getSelectedIndices();
			if (oTablIndxPR.length === 0) {
				MessageBox.information("Please Select at least one GSTIN");
				return;
			}

			if (!this._oDialogGstr6ABtn) {
				this._oDialogGstr6ABtn = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.gstr6ABtn", this);
				this.getView().addDependent(this._oDialogGstr6ABtn);
			}

			this._oDialogGstr6ABtn.open();
			sap.ui.getCore().byId("id_TaxGstrStatusBtn").setValue(this.getView().byId("idTaxPeriodDetailed").getValue());
		},

		onSaveGstr6ABtnDialog1: function () {
			this.getGstrAButtonIntrigration();
			this._oDialogGstr6ABtn.close();
		},

		onPressSaveStatus: function (oEvent, flag) {
			if (!this._oDialogSaveStatusBtn) {
				this._oDialogSaveStatusBtn = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.saveStatus", this);
				this.getView().addDependent(this._oDialogSaveStatusBtn);
			}
			this._oDialogSaveStatusBtn.open();
			if (flag == "P") {
				var a = oEvent.getSource().getBindingContext("Gstr6PRSumData").getObject();
				sap.ui.getCore().byId("slGstinSave1").setSelectedKey(a.gstin);
				sap.ui.getCore().byId("TaxDP4").setValue(this.getView().byId("idTaxperiodGstr6pro").getValue());
			} else {
				sap.ui.getCore().byId("slGstinSave1").setSelectedKey(this.getView().byId("idgstingstrnew1").getSelectedKey());
				sap.ui.getCore().byId("TaxDP4").setValue(this.getView().byId("idTaxSummary").getValue());
			}

			sap.ui.getCore().byId("TaxDP4").addEventDelegate({
				onAfterRendering: function () {
					sap.ui.getCore().byId("TaxDP4").$().find("input").attr("readonly", true);
				}
			});
			this.getGstrSaveStatusIntg();

		},
		onCloseDialogSaveSattus: function () {
			this._oDialogSaveStatusBtn.close();
		},

		fnGoForSaveStatus: function () {
			this.getGstrSaveStatusIntg();
		},

		getGstrSaveStatusIntg: function (oEntity) {
			var oTaxPeriod = sap.ui.getCore().byId("TaxDP4").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {

				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [sap.ui.getCore().byId("slGstinSave1").getSelectedKey()],
					}
				}
			};
			this.getGstrSaveStatusIntgFinal(postData);
		},

		getGstrSaveStatusIntgFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6SummarySaveStatus.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
							var oGstr6SaveStatusIntg = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oGstr6SaveStatusIntg, "Gstr6SaveStatusIng");
						} else if (data.resp.message != undefined) {
							MessageBox.information(data.resp.message);
							var oGstr6SaveStatusIntg = new sap.ui.model.json.JSONModel(null);
							oView.setModel(oGstr6SaveStatusIntg, "Gstr6SaveStatusIng");
						} else {
							for (var i = 0; i < data.resp.length; i++) {
								data.resp[i].sno = i + 1;
							}
							var oGstr6SaveStatusIntg = new sap.ui.model.json.JSONModel(data);
							oView.setModel(oGstr6SaveStatusIntg, "Gstr6SaveStatusIng");
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		getUpdateGstnButtonIntrigration: function () {
			var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var vGstins = this.oView.byId("idgstingstrnew1").getSelectedKey();
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"action": "UPDATEGSTIN",
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [vGstins]
					}
				}
			};
			this.getUpdateGstnButtonIntrigrationFinal(postData);
		},

		getUpdateGstnButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr6ReviewSummary.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							that.onPressSummaryGo();
							MessageBox.success("Successfully Updated");
						} else {
							MessageBox.information(data.hdr.message);

						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		fnsUpdateGSTR6: function () {
			var that = this;
			MessageBox.show("Do you want to Update GSTN Data for GSTR-6?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.getUpdateGstnButtonIntrigration();
					}
				}
			});
		},

		fnSaveGstnGSTR6: function (flag) {
			if (!this._oDialogSavetoGstnBtn) {
				this._oDialogSavetoGstnBtn = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.SaveToGstin", this);
				this.getView().addDependent(this._oDialogSavetoGstnBtn);
			}
			if (flag === "P") {
				var oSelectedItem = this.getView().byId("idPREntityTableNew").getSelectedIndices();
				var oModelForTab1 = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
				if (oSelectedItem.length == 0) { //eslint-disable-line
					sap.m.MessageBox.warning("Please select at least one GSTIN.");
					return;
				}
				var oData = {
					"P": true
				};
				this.ResetFlag = "P";
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup"); //eslint-disable-line
				sap.ui.getCore().byId("id_TaxProcessGstr6").setValue(this.oView.byId("idTaxperiodGstr6pro").getValue());

				var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				this.oSavePayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"returnType": "GSTR6",
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
				this._oDialogSavetoGstnBtn.open();
			} else {
				var oData = { //eslint-disable-line
					"P": false
				};
				this.ResetFlag = "S";
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup"); //eslint-disable-line
				sap.ui.getCore().byId("id_TaxProcessGstr6").setValue(this.oView.byId("idTaxSummary").getValue());
				sap.ui.getCore().byId("idGetfgiGSINTMain2GSTR6").setSelectedKey(this.oView.byId("idgstingstrnew1").getSelectedKey());

				this.oSavePayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.oView.byId("idTaxSummary").getValue(),
						"returnType": "GSTR6",
						"dataSecAttrs": {
							"GSTIN": [this.oView.byId("idgstingstrnew1").getSelectedKey()]
						}
					}
				};
				this._oDialogSavetoGstnBtn.open();
			}
		},

		onSaveGstr6ABtnDialogC: function (action) {
			this._oDialogSavetoGstnBtn.close();
		},

		onSaveGstr6ABtnDialog: function (flag) {
			if (flag == "S") {
				this.getSaveGstnButtonIntrigration();
			} else if (flag == "R") {
				this.gstr6SaveToGstnReset();
			}
			this._oDialogSavetoGstnBtn.close();
		},

		gstr6SaveToGstnReset: function () {
			if (this.ResetFlag == 'S') {
				var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var vUserInput = sap.ui.getCore().byId("idRadioBtnSavegstr6").getSelectedIndex() == 0 ? false : true;
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": oTaxPeriod,
						"gstins": [this.oView.byId("idgstingstrnew1").getSelectedKey()],
						"isCrossItcUserInput": vUserInput
					}
				};
				this.gstr6SaveToGstnResetFinal(postData);
			} else {
				var oSelectedItem = this.getView().byId("idPREntityTableNew").getSelectedIndices();
				var oModelForTab1 = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
				var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var vUserInput = sap.ui.getCore().byId("idRadioBtnSavegstr6").getSelectedIndex() == 0 ? false : true;
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": oTaxPeriod,
						"gstins": aGstin,
						"isCrossItcUserInput": vUserInput
					}
				};
				var that = this;
				MessageBox.show("Do you want to Reset Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.gstr6SaveToGstnResetFinal(postData);
						}
					}
				});
			}
		},

		gstr6SaveToGstnResetFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6SaveToGstnReset.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							for (var i = 0; i < data.resp.length; i++) {
								aMockMessages.push({
									type: 'Success',
									title: data.resp[i].msg,
									// gstin: data.resp[i].msg,
									active: true,
									icon: "sap-icon://message-success",
									highlight: "Success",
									info: "Success"
								});
							}
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress9();
							// that.onPressSummaryGo();
						} else {
							if (data.resp.length > 0) {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: 'Success',
										title: data.resp[i].msg,
										// gstin: data.resp[i].msg,
										active: true,
										icon: "sap-icon://message-success",
										highlight: "Success",
										info: "Success"
									});

								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPress9();
							} else {
								sap.m.MessageBox.error("reset failed for selected(active) GSTINs.");
							}
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onDialogPress9: function () {
			var that = this;
			if (!this.pressDialog1) {
				this.pressDialog1 = new Dialog({
					title: "Information",
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
							this._getSaveGstnStatus();
							// that.onPressGoFinal(1);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog1);
			}
			this.pressDialog1.open();
		},

		getSaveGstnButtonIntrigration: function () {
			if (this.ResetFlag == 'S') {
				var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var vUserInput = sap.ui.getCore().byId("idRadioBtnSavegstr6").getSelectedIndex() == 0 ? false : true;
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": oTaxPeriod,
						"gstins": [this.oView.byId("idgstingstrnew1").getSelectedKey()],
						"isCrossItcUserInput": vUserInput
					}
				};
				this.getSaveGstnButtonIntrigrationFinal(postData);
			} else {
				var oSelectedItem = this.getView().byId("idPREntityTableNew").getSelectedIndices();
				var oModelForTab1 = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
				var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var vUserInput = sap.ui.getCore().byId("idRadioBtnSavegstr6").getSelectedIndex() == 0 ? false : true;
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"retPeriod": oTaxPeriod,
						"gstins": aGstin,
						"isCrossItcUserInput": vUserInput
					}
				};
				this.getSaveGstnButtonIntrigrationFinal(postData);
			}
		},

		getSaveGstnButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/gstr6SaveToGstnJob.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
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
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress1();
							// that.onPressSummaryGo();
						} else {
							if (data.resp.length > 0) {
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
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPress1();
							} else {
								sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
							}
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("gstr6SaveToGstnJob : Error");
					});
			});
		},

		onDialogPress1: function () {
			var that = this;
			if (!this.pressDialog1) {
				this.pressDialog1 = new Dialog({
					title: "Information",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog1.close();
							this._getSaveGstnStatus();
							// that.onPressGoFinal(1);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog1);
			}
			this.pressDialog1.open();
		},

		_getSaveGstnStatus: function () {
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSaveGstnStatus.do",
					data: JSON.stringify(this.oSavePayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var saveDate = that.getTimeStamp(data.resp.updatedDate),
						oSaveStats = {
							"updatedDate": saveDate,
							"flag": saveDate ? true : false
						};
					that.getView().setModel(new JSONModel(oSaveStats), "SaveGstnStats");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		getTimeStamp: function (date) {
			var vDate = "";
			if (date) {
				vDate += date.substr(8, 2) + "-" + date.substr(5, 2) + "-" + date.substr(0, 4) + date.substr(10);
			}
			return vDate;
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Information",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							that.getGstr6ASucessStatusDataFinal();
							this.pressDialog.close();
							// that.onPressGoFinal(1);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onPressDelete: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();
			var postData = {
				"req": []
			};

			if (oSource.getId().indexOf("idDelete1") > -1) {
				var oSelectedItem = this.getView().byId("idisdinvEGstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idDelete2") > -1) {
				var oSelectedItem = this.getView().byId("idsecondGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idDelete3") > -1) {
				var oSelectedItem = this.getView().byId("idThirdGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idDelete4") > -1) {
				var oSelectedItem = this.getView().byId("idvoiceisdcr1Gstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			}

			sap.m.MessageBox.show("Do you want to delete the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idDelete1") > -1) {
							var oTablIndxDis = that.byId("idisdinvEGstr6").getSelectedIndices();
							var oTablGetData = that.byId("idisdinvEGstr6").getModel("Gstr6DistCrEligData").getData();
							for (var i = 0; i < oTablIndxDis.length; i++) {
								postData.req.push({
									"id": oTablGetData[oTablIndxDis[i]].id
								});

							}
							that.DistribuDeleteDataFinal(postData, "D1");
							// that.onSave1();
						} else if (oSource.getId().indexOf("idDelete2") > -1) {
							var oTablIndxDis = that.byId("idsecondGstrtab").getSelectedIndices();
							var oTablGetData = that.byId("idsecondGstrtab").getModel("Gstr6DistCrInEligData").getData();
							for (var i = 0; i < oTablIndxDis.length; i++) {
								postData.req.push({
									"id": oTablGetData[oTablIndxDis[i]].id
								});

							}
							that.DistribuDeleteDataFinal(postData, "D2");
							// that.onSave2();
						} else if (oSource.getId().indexOf("idDelete3") > -1) {
							var oTablIndxDis = that.byId("idThirdGstrtab").getSelectedIndices();
							var oTablGetData = that.byId("idThirdGstrtab").getModel("Gstr6ReDistCrEligData").getData();
							for (var i = 0; i < oTablIndxDis.length; i++) {
								postData.req.push({
									"id": oTablGetData[oTablIndxDis[i]].id
								});

							}
							that.DistribuDeleteDataFinal(postData, "D3");
							// that.onSave3();
						} else if (oSource.getId().indexOf("idDelete4") > -1) {
							var oTablIndxDis = that.byId("idvoiceisdcr1Gstr6").getSelectedIndices();
							var oTablGetData = that.byId("idvoiceisdcr1Gstr6").getModel("Gstr6ReDistCrInEligData").getData();
							for (var i = 0; i < oTablIndxDis.length; i++) {
								postData.req.push({
									"id": oTablGetData[oTablIndxDis[i]].id
								});
							}
							that.DistribuDeleteDataFinal(postData, "D4");
							// that.onSave4();
						}
					}
				}
			});
		},

		DistribuDeleteDataFinal: function (postData, flag) {
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/deleteSummary/id.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success("Successfully Deleted");
							if (flag === "D1") {
								that.getGstr6DistriCrEligData();

							} else if (flag === "D2") {
								that.getGstr6DistriCrInEligData();

							} else if (flag === "D3") {
								that.getGstr6ReDistriCrEligData();

							} else if (flag === "D4") {
								that.getGstr6ReDistriCrInEligData();
							}
						} else {
							MessageBox.error("Error");
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("id : Error");
					});
			});
		},

		onPressDistribEdit: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idEdit1") > -1) {
				var oSelectedItem = this.getView().byId("idisdinvEGstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idEdit2") > -1) {
				var oSelectedItem = this.getView().byId("idsecondGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idEdit3") > -1) {
				var oSelectedItem = this.getView().byId("idThirdGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idEdit4") > -1) {
				var oSelectedItem = this.getView().byId("idvoiceisdcr1Gstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			}

			sap.m.MessageBox.show("Do you want to edit the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idEdit1") > -1) {
							that.onEdit1();
						} else if (oSource.getId().indexOf("idEdit2") > -1) {
							that.onEdit2();
						} else if (oSource.getId().indexOf("idEdit3") > -1) {
							that.onEdit3();
						} else if (oSource.getId().indexOf("idEdit4") > -1) {
							that.onEdit4();
						}
					}
				}
			});
		},

		onEdit1: function () {
			var tab = this.getView().byId("idisdinvEGstr6");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6DistCrEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			oJSONModel.refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEdit2: function () {
			var tab = this.getView().byId("idsecondGstrtab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6DistCrInEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			oJSONModel.refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEdit3: function () {
			var tab = this.getView().byId("idThirdGstrtab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6ReDistCrEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			oJSONModel.refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEdit4: function () {
			var tab = this.getView().byId("idvoiceisdcr1Gstr6");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6ReDistCrInEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			oJSONModel.refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onPressDistribSave: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idSave1") > -1) {
				var oSelectedItem = this.getView().byId("idisdinvEGstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idSave2") > -1) {
				var oSelectedItem = this.getView().byId("idsecondGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idSave3") > -1) {
				var oSelectedItem = this.getView().byId("idThirdGstrtab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idSave4") > -1) {
				var oSelectedItem = this.getView().byId("idvoiceisdcr1Gstr6").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			}

			sap.m.MessageBox.show("Do you want to save the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idSave1") > -1) {
							that.onSave1();
						} else if (oSource.getId().indexOf("idSave2") > -1) {
							that.onSave2();
						} else if (oSource.getId().indexOf("idSave3") > -1) {
							that.onSave3();
						} else if (oSource.getId().indexOf("idSave4") > -1) {
							that.onSave4();
						}
					}
				}
			});
		},

		onSave1: function () {
			var payroll = {
				"req": []
			};

			var tab = this.getView().byId("idisdinvEGstr6");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6DistCrEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				delete itemlist1[sItems[i]].edit;
				payroll.req.push(itemlist1[sItems[i]]);
			}

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveGstr6DistributedSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payroll)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success("Successfully Saved");
							that.getGstr6DistriCrEligData();

						} else {
							MessageBox.error("Error");
						}

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("saveGstr6DistributedSummary : Error");
					});
			});
		},

		onSave2: function () {
			var payroll = {
				"req": []
			};

			var tab = this.getView().byId("idsecondGstrtab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6DistCrInEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				delete itemlist1[sItems[i]].edit;
				payroll.req.push(itemlist1[sItems[i]]);
			}

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveGstr6DistributedSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payroll)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success("Successfully Saved");
							that.getGstr6DistriCrInEligData();
						} else {
							MessageBox.error("Error");
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("saveGstr6DistributedSummary : Error");
					});
			});
		},

		onSave3: function () {
			var payroll = {
				"req": []
			};

			var tab = this.getView().byId("idThirdGstrtab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6ReDistCrEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				delete itemlist1[sItems[i]].edit;
				payroll.req.push(itemlist1[sItems[i]]);
			}

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveGstr6DistributedSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payroll)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success("Successfully Saved");
							that.getGstr6ReDistriCrEligData();
						} else {
							MessageBox.error("Error");
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("saveGstr6DistributedSummary : Error");
					});
			});
		},

		onSave4: function () {
			var payroll = {
				"req": []
			};

			var tab = this.getView().byId("idvoiceisdcr1Gstr6");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Gstr6ReDistCrInEligData");
			var itemlist1 = oJSONModel.getProperty("/");
			for (var i = 0; i < sItems.length; i++) {
				delete itemlist1[sItems[i]].edit;
				payroll.req.push(itemlist1[sItems[i]]);
			}

			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveGstr6DistributedSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payroll)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success("Successfully Saved");
							that.getGstr6ReDistriCrInEligData();

						} else {
							MessageBox.error("Error");
						}

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("saveGstr6DistributedSummary : Error");
					});
			});
		},

		onPressToggle: function (oEvent) {
			var aData = oEvent.getSource().getBinding().getModel("Gstr6DetailedData").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "resp") {
				var vRowCount = 7;
			} else {
				vRowCount = 4;
			}
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) { //eslint-disable-line
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[vModel][aPath[1]].items.length;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		OnOpenStateGET2A: function (oEvent) {
			var aData = oEvent.getSource().getBinding().getModel("Gstr6ADetailed").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "resp") {
				var vRowCount = 7;
			} else {
				vRowCount = 4;
			}
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) { //eslint-disable-line
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[vModel][aPath[1]].items.length;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		onPressToggle1: function (oEvent) {
			var aData = oEvent.getSource().getBinding().getModel("Gstr6DistributionData").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "resp") {
				var vRowCount = 5;
			} else {
				vRowCount = 4;
			}
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) { //eslint-disable-line
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[vModel][aPath[1]].items.length;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		onSelectionChangeGstn: function (oEvent) {
			var vGetgstn = oEvent.getSource().getSelectedKeys();
			this.byId("idgstinDist").setSelectedKeys(vGetgstn);
			this.byId("idgstinDist1").setSelectedKeys(vGetgstn);
			this.byId("idgstinDist2").setSelectedKeys(vGetgstn);
			this.byId("idgstinDist3").setSelectedKeys(vGetgstn);
		},

		onDateChange: function (oEvent) {
			var vGetDate = oEvent.getSource().getValue();
			this.byId("id_disCrEligTax").setValue(vGetDate);
			this.byId("id_disCrEligTax1").setValue(vGetDate);
			this.byId("id_disCrEligTax2").setValue(vGetDate);
			this.byId("id_disCrEligTax3").setValue(vGetDate);
		},

		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("idFromTaxPeriodDetailed")) {
				var vDatePicker = "idToTaxPeriodDetailed";
			} else if (oEvent.getSource().getId().includes("idRSFromTaxPeriodDetailed")) {
				var vDatePicker = "idRSToTaxPeriodDetailed";
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

		onPressProcessStatus: function (oEvent) {
			var oSource = oEvent.getSource();
			var that = this;
			var oView = this.getView();
			if (oSource.getId().indexOf("id_processdetailStatus") > -1) {
				var oSelectedItem = oView.byId("idPREntityTableNew").getSelectedIndices();
				var oModelForTab1 = that.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
				if (oSelectedItem.length === 0) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				var oTaxPeriod = oView.byId("idTaxperiodGstr6pro").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				};
			} else {
				var getSucessdata = this.getView().byId("idgstingstrnew1").getSelectedKey();
				var oTaxPeriod = oView.byId("idTaxSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}

				var postData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": [getSucessdata]
					}
				};
			}

			if (!this._oDialog) {
				this._oDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.ProcessStatus", this);
				this.getView().addDependent(this._oDialog);
			}
			this.postpayloadgstr6 = postData;

			this.getGstr6SucessStatusDataFinal();
			this._oDialog.open();
			sap.ui.getCore().byId("id_TaxProcess").setValue(postData.req.taxPeriod);
			// var oTaxPeriod = this.getView().byId("idTaxperiodGstr6pro").getValue();
			// sap.ui.getCore().byId("id_TaxProcess").setValue(oTaxPeriod);
			sap.ui.getCore().byId("id_TaxProcess").addEventDelegate({
				onAfterRendering: function () {
					sap.ui.getCore().byId("id_TaxProcess").$().find("input").attr("readonly", true);
				}
			});
		},

		onCloseProcDialog: function () {
			this.onPressProcessGoGstr6();
			this._oDialog.close();
		},

		getGstr6SucessStatusDataFinal: function () {
			var that = this;
			var postData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": that.postpayloadgstr6.req.taxPeriod,
					"gstin": that.postpayloadgstr6.req.gstin
				}
			};

			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr6DetailStatus.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.resp.length === 0) {
							MessageBox.information("No Data");
						}
						that.bindGstr6sumDetailStatus(data);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("getGstr6DetailStatus : Error");
					});
			});
		},

		bindGstr6sumDetailStatus: function (data) {
			this._showing(false);
			var oView = this.getView();
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = false;
				data.resp.lastCall[i].b2baFlag = false;
				data.resp.lastCall[i].cdnFlag = false;
				data.resp.lastCall[i].cdnaFlag = false;
				data.resp.lastCall[i].isdFlag = false;
				data.resp.lastCall[i].isdaFlag = false;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = false;
				data.resp.lastSuccess[i].b2baFlag = false;
				data.resp.lastSuccess[i].cdnFlag = false;
				data.resp.lastSuccess[i].cdnaFlag = false;
				data.resp.lastSuccess[i].isdFlag = false;
				data.resp.lastSuccess[i].isdaFlag = false;
			}
			var oGstr6AsumSucessData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr6AsumSucessData, "Gstr6AsumSucess");
		},

		onMenuItemPressGstr6down: function (oEvent) {
			var oSelectedItem = this.getView().byId("idPREntityTableNew").getSelectedIndices();
			var oModelForTab1 = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
			}

			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postPayload = {
				"req": {
					"type": null,
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};

			if (oSelectedKey === "gstr6AspError") {
				postPayload.req.type = "gstr6AspError";
				var url = "/aspsapapi/gstr6ReportsDownloads.do";
				this.excelDownload(postPayload, url);

			} else if (oSelectedKey === "gstr6EntityLevelSummary") {
				postPayload.req.type = "gstr6EntityLevelSummary";
				var url = "/aspsapapi/gstr6ReportsDownloads.do";
				this.excelDownload(postPayload, url);

			} else if (oSelectedKey === "processed") {
				postPayload.req.type = "processed";
				postPayload.req.dataType = "inward";
				var url = "/aspsapapi/downloadgstr6reports.do";
				this.excelDownload(postPayload, url);

			} else if (oSelectedKey === "current") {
				postPayload.req.type = "gstr6Process";
				postPayload.req.dataType = "Inward";
				postPayload.req.reportCateg = "ProcessedSummary";
				var url = "/aspsapapi/downloadgstr6ProcessedCsvReports.do";
				this.reportDownload(postPayload, url);

			} else {
				return;
			}
		},

		handleChange: function (oevt) {
			var toDate = this.byId("DP2").getDateValue(),
				fromDate = this.byId("DP1").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("DP2").setDateValue(oevt.getSource().getDateValue());
				this.byId("DP2").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("DP2").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onCloseTaxLiability: function () {
			this.TaxLiability.close();
		},

		onMenuItemPressGstr6Adown: function (oEvent) {
			var oSelectedItem = this.getView().byId("idGetPREntityTableGstr6").getSelectedIndices();
			var oModelForTab1 = this.byId("idGetPREntityTableGstr6").getModel("Gstr6APRData").getData().resp;
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
			}

			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oFromTaxPeriod = this.oView.byId("idFromTaxPeriodDetailed").getValue();
			if (oFromTaxPeriod === "") {
				oFromTaxPeriod = null;
			}
			var oToTaxPeriod = this.oView.byId("idToTaxPeriodDetailed").getValue();
			if (oToTaxPeriod === "") {
				oToTaxPeriod = null;
			}
			var postPayload = {
				"req": {
					"entity": [$.sap.entityID],
					// "taxPeriod": oTaxPeriod,
					"fromPeriod": oFromTaxPeriod,
					"toPeriod": oToTaxPeriod,
					"tableType": this.oView.byId("iddroptatype2Gstr6").getSelectedKeys(),
					"docType": this.oView.byId("iddropDoctype2Gstr6").getSelectedKeys(),
					// "data": this.oView.byId("iddropdata2Gstr6").getSelectedKeys(),
					"dataSecAttrs": {
						"GSTIN": aGstin
					},
					ReportType: "GSTR6A",
					ReportCategory: "GSTR6A_Summary_Report",
					DataType: "GSTR6A"
				}
			};

			if (oSelectedKey === "DownloadReport") {
				var url = "/aspsapapi/gstr6aReportsDownloads.do";
				this.reportDownload(postPayload, url);
			} else {
				return;
			}
		},

		onMenuItemPressGstr6Adown1: function (oEvent) {
			var aGstin = this.oView.byId("idGetfgiGSINTMain2GSTR61").getSelectedKey();
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oFromTaxPeriod = this.oView.byId("idRSFromTaxPeriodDetailed").getValue();
			if (oFromTaxPeriod === "") {
				oFromTaxPeriod = null;
			}
			var oToTaxPeriod = this.oView.byId("idRSToTaxPeriodDetailed").getValue();
			if (oToTaxPeriod === "") {
				oToTaxPeriod = null;
			}
			var postPayload = {
				"req": {
					"entity": [$.sap.entityID],
					// "taxPeriod": oTaxPeriod,
					"fromPeriod": oFromTaxPeriod,
					"toPeriod": oToTaxPeriod,
					"tableType": this.oView.byId("iddroptatype2Gstr61").getSelectedKeys(),
					"docType": this.oView.byId("iddropDoctype2Gstr61").getSelectedKeys(),
					// "data": this.oView.byId("iddropdata2Gstr61").getSelectedKeys(),
					"dataSecAttrs": {
						"GSTIN": [aGstin]
					},
					ReportType: "GSTR6A",
					ReportCategory: "GSTR6A_Summary_Report",
					DataType: "GSTR6A"
				}
			};

			if (oSelectedKey === "processLineItem6A") {
				var url = "/aspsapapi/gstr6aReportsDownloads.do";
				this.reportDownload(postPayload, url);
			} else {
				return;
			}
		},

		onMenuItemPressGstr6down1: function (oEvent) {
			var aGstin = this.oView.byId("idgstingstrnew1").getSelectedKey();
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postPayload = {
				"req": {
					"type": null,
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": [aGstin]
					}
				}
			};

			if (oSelectedKey === "processLineItem") {
				postPayload.req.type = "gstr6Process";
				postPayload.req.dataType = "Inward";
				postPayload.req.reportCateg = "ReviewSummary";
				var url = "/aspsapapi/downloadgstr6ProcessedCsvReports.do";
				this.reportDownload(postPayload, url);

			} else if (oSelectedKey === "gstr6AspError1") {
				postPayload.req.type = "gstr6AspError";
				var url = "/aspsapapi/gstr6ReportsDownloads.do";
				this.excelDownload(postPayload, url);

			} else if (oSelectedKey === "processed1") {
				postPayload.req.type = "processed";
				postPayload.req.dataType = "inward";
				var url = "/aspsapapi/downloadgstr6reports.do";
				this.excelDownload(postPayload, url);

			} else {
				return;
			}
		},

		onPressonlineerrorCor: function (oEvent) {
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idOnlineProcess") > -1) {
				var oSelectedItem = this.getView().byId("idPREntityTableNew").getSelectedIndices();
				var oModelForTab1 = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getData().resp;
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
				}
				var oTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
			} else if (oSource.getId().indexOf("idOnlineSummary") > -1) {
				var aGstin = this.oView.byId("idgstingstrnew1").getSelectedKey();
				var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
			}
			if (aGstin.length === 0) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}

			var aData = {
				"req": {
					"dataType": "Inward",
					"criteria": "taxPeriod",
					"taxPeriodFrom": oTaxPeriod,
					"taxPeriodTo": oTaxPeriod,
					"type": "E",
					"navType": "GSTR6",
					"dataSecAttrs": {
						"GSTIN": [aGstin]
					}
				}
			};
			this.getOwnerComponent().setModel(new JSONModel(aData), "FilterInvoice");
			this.getRouter().navTo("InvManageNew");

		},
		onPressgstrProcess: function (oEvent) {
			if (!this._oDialogprosGstin) {
				this._oDialogprosGstin = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr6.getGstr6ProcessDet", this);
				this.getView().addDependent(this._oDialogprosGstin);
			}
			this._oDialogprosGstin.open();
		},

		onSaveGst6CloseDialogProcess1: function () {
			this._oDialogprosGstin.close();
		},

		onpressB2bGSTR6Dist: function (oEvent) {
			var oKey = oEvent.getSource().getText();
			var ogstin = this.oView.byId("idgstingstrnew1").getSelectedKey();
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			var a = oEvent.getSource().getBindingContext("Gstr6DistributionData").sPath.split("/")[2];
			// var b = oEvent.getSource().getBindingContext("AnxSummary").sPath.split("/")[4];
			var oModelDist = this.getView().getModel("Gstr6DistributionData").getData().resp;
			var oMod = oModelDist[a].distribution;
			// var oTabdata = this.getView().byId("tabDistTablegstrnew").getPath();
			if (oMod === "Distribution - Invoices (Section 5)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Invoice");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Distribution - Invoices (Section 5)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist1").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax1").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("InfInvoice");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Distribution - Credit Notes (Section 8)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Invoice");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Distribution - Credit Notes (Section 8)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist1").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax1").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("InfInvoice");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Redistribution - Invoices (Section 9)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist2").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax2").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("ForcNote");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Redistribution - Invoices (Section 9)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist3").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax3").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Ineli");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Redistribution - Credit Notes (Section 9)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist2").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax2").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("ForcNote");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Redistribution - Credit Notes (Section 9)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist3").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax3").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Ineli");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			}
		},

		onPressGetBtn: function (oEvent) {
			var oBtProcess = sap.ui.getCore().byId("idProcessStatusBtn").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = sap.ui.getCore().byId("idgetVtablegstr6pro").getSelectedIndices();
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegstr6pro").getModel("Gstr6AsumSucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	sap.m.MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcess").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastCall[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr6Sections": [],
						"isFailed": false
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr6Sections.push("b2b");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr6Sections.push("b2ba");
					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr6Sections.push("cdn");
					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr6Sections.push("cdna");
					}
					if (oModelForTab1.lastCall[i].isdFlag) {
						postData.req[i].gstr6Sections.push("isd");
					}
					if (oModelForTab1.lastCall[i].isdaFlag) {
						postData.req[i].gstr6Sections.push("isda");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;

				}
				this.getSectionButtonIntrigrationFinal(postData);

			} else {
				var oSelectedItem = sap.ui.getCore().byId("idgetStatusgstr6").getSelectedIndices();
				var oModelForTab1 = sap.ui.getCore().byId("idgetStatusgstr6").getModel("Gstr6AsumSucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	sap.m.MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcess").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr6Sections": [],
						"isFailed": false
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr6Sections.push("b2b");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr6Sections.push("b2ba");
					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr6Sections.push("cdn");
					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr6Sections.push("cdna");
					}
					if (oModelForTab1.lastSuccess[i].isdFlag) {
						postData.req[i].gstr6Sections.push("isd");
					}
					if (oModelForTab1.lastSuccess[i].isdaFlag) {
						postData.req[i].gstr6Sections.push("isda");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSectionButtonIntrigrationFinal(postData);
			}
		},

		getSectionButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6GstnGetSection.do",
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
						that.onDialogPressPro();
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr6GstnGetSection : Error");
					});
			});
		},

		onDialogPressPro: function () {
			var that = this;
			if (!this.pressDialogpro) {
				this.pressDialogpro = new Dialog({
					title: "Section Wise Get",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							that.getGstr6SucessStatusDataFinal();
							this.pressDialogpro.close();
							// that.onPressGoFinal(1);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialogpro);
			}
			this.pressDialogpro.open();
		},

		onpressB2bGSTR6: function (oEvent) {
			var oKey = oEvent.getSource().getText();
			var ogstin = this.oView.byId("idgstingstrnew1").getSelectedKey();
			var oTaxPeriod = this.oView.byId("idTaxSummary").getValue();
			var a = oEvent.getSource().getBindingContext("Gstr6DetailedData").sPath.split("/")[2];
			// var b = oEvent.getSource().getBindingContext("AnxSummary").sPath.split("/")[4];
			var oModelDist = this.getView().getModel("Gstr6DetailedData").getData().resp;
			var oMod = oModelDist[a].docType;
			var aData = {
				"req": {
					"dataType": "Inward",
					"criteria": "taxPeriod",
					"taxPeriodFrom": oTaxPeriod,
					"taxPeriodTo": oTaxPeriod,
					"type": "P", //processing status
					"navType": "GSTR6",
					"dataSecAttrs": {
						"GSTIN": [ogstin]
					}
				}
			};
			if (oMod === "B2B (Section 3)" && oKey !== "Eligible" && oKey !== "Ineligible") {
				this.getOwnerComponent().setModel(new JSONModel(aData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else if (oMod === "CDN (Section 6B)" && oKey !== "Eligible" && oKey !== "Ineligible") {
				this.getOwnerComponent().setModel(new JSONModel(aData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else if (oMod === "B2BA (Section 6A)" && oKey !== "Eligible" && oKey !== "Ineligible") {
				this.getOwnerComponent().setModel(new JSONModel(aData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else if (oMod === "CDNA (Section 6C)" && oKey !== "Eligible" && oKey !== "Ineligible") {
				this.getOwnerComponent().setModel(new JSONModel(aData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
			} else if (oMod === "Distribution - Invoices (Section 5)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Invoice");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Distribution - Invoices (Section 5)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist1").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax1").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("InfInvoice");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Distribution - Credit Notes (Section 8)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Invoice");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Distribution - Credit Notes (Section 8)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist1").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax1").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("InfInvoice");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Redistribution - Invoices (Section 9)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist2").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax2").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("ForcNote");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();

			} else if (oMod === "Redistribution - Invoices (Section 9)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist3").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax3").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Ineli");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Redistribution - Credit Notes (Section 9)" && oKey === "Eligible") {
				this.oView.byId("idgstinDist2").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax2").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("ForcNote");
				this.getGstr6DistriCrEligData();
				this.onSelectionGstr6Distribution();
			} else if (oMod === "Redistribution - Credit Notes (Section 9)" && oKey === "Ineligible") {
				this.oView.byId("idgstinDist3").setSelectedKeys(ogstin);
				this.oView.byId("id_disCrEligTax3").setValue(oTaxPeriod);
				this.getView().byId("itbGstr6").setSelectedKey("distribution");
				this.getView().byId("idActionInformationDistGstr6").setSelectedKey("Ineli");
				this.getGstr6DistriCrInEligData();
				this.onSelectionGstr6Distribution();
			}
		},

		onDownloadConsGstnErrors: function (oEvent, errType, refId) {
			var payload = {
				"req": {
					"entityId": [$.sap.entityID],
					"type": "gstr6GstnError",
					"taxPeriod": sap.ui.getCore().byId("TaxDP4").getValue(),
					"gstnRefId": refId,
					"dataSecAttrs": {
						"GSTIN": [sap.ui.getCore().byId("slGstinSave1").getSelectedKey()]
					}
				}
			};
			this.excelDownload(payload, "/aspsapapi/downloadGstr6ErrorReports.do");
		},

		onDownloadErrorJson: function (section, refId) {
			var payload = {
				"req": {
					"supplierGstin": sap.ui.getCore().byId("slGstinSave1").getSelectedKey(),
					"returnPeriod": sap.ui.getCore().byId("TaxDP4").getValue(),
					"section": section,
					"refId": refId
				}
			};
			this.excelDownload(payload, "/aspsapapi/gstr6JsonDownloadDocument.do");
		},

		onPressProcessSumBtn: function (oEvent) {
			var oBtProcess = sap.ui.getCore().byId("idProcessStatusBtn").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = sap.ui.getCore().byId("idgetVtablegstr6pro").getSelectedIndices();
				var oModelForTab1 = sap.ui.getCore().byId("idgetVtablegstr6pro").getModel("Gstr6AsumSucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	sap.m.MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcess").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"status": "LastCall",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastCall[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr6Sections": []

					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr6Sections.push("B2B");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr6Sections.push("B2BA");
					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr6Sections.push("CDN");
					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr6Sections.push("CDNA");
					}
					if (oModelForTab1.lastCall[i].isdFlag) {
						postData.req[i].gstr6Sections.push("DISTRIBUTION");
					}
					if (oModelForTab1.lastCall[i].isdaFlag) {
						postData.req[i].gstr6Sections.push("REDISTRIBUTION");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/gstr6SectionwiseDownloads.do";
				this.excelDownload(postData, url);
			} else {
				var oSelectedItem = sap.ui.getCore().byId("idgetStatusgstr6").getSelectedIndices();
				var oModelForTab1 = sap.ui.getCore().byId("idgetStatusgstr6").getModel("Gstr6AsumSucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	sap.m.MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = sap.ui.getCore().byId("id_TaxProcess").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"status": "LastSuccess",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr6Sections": []

					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr6Sections.push("B2B");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr6Sections.push("B2BA");
					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr6Sections.push("CDN");
					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr6Sections.push("CDNA");
					}
					if (oModelForTab1.lastSuccess[i].isdFlag) {
						postData.req[i].gstr6Sections.push("DISTRIBUTION");
					}
					if (oModelForTab1.lastSuccess[i].isdaFlag) {
						postData.req[i].gstr6Sections.push("REDISTRIBUTION");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6Sections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/gstr6SectionwiseDownloads.do";
				this.excelDownload(postData, url);
			}
		},

		onPressProcessSumBtn1: function (oEvent) {
			var oBtProcess = this.byId("sbUpdatStatusGstr6").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = this.byId("idgetVtablegstr6").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetVtablegstr6").getModel("Gstr6ASucess").getData().resp;
				var oTaxPeriod = this.byId("id_TaxProcessGstr6A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"status": "LastCall",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastCall[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr6aSections": []
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr6aSections.push("B2B");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr6aSections.push("B2BA");
					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr6aSections.push("CDN");
					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr6aSections.push("CDNA");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/gstr6ASectionwiseDownloads.do";
				this.excelDownload(postData, url);

			} else {
				var oSelectedItem = this.byId("idgetStablegstr6").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetStablegstr6").getModel("Gstr6ASucess").getData().resp;
				var oTaxPeriod = this.byId("id_TaxProcessGstr6A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"status": "LastSuccess",
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"taxPeriod": oTaxPeriod,
						"gstr6aSections": []
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr6aSections.push("B2B");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr6aSections.push("B2BA");
					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr6aSections.push("CDN");
					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr6aSections.push("CDNA");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				var url = "/aspsapapi/gstr6ASectionwiseDownloads.do";
				this.excelDownload(postData, url);
			}
		},

		onGstr6Asumpress: function (oEvent) {
			var oBtProcess = this.byId("sbUpdatStatusGstr6").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = this.byId("idgetVtablegstr6").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetVtablegstr6").getModel("Gstr6ASucess").getData().resp;
				var oTaxPeriod = this.byId("id_TaxProcessGstr6A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastCall[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr6aSections": [],
						"action_required": "Y",
						"isFailed": false
					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr6aSections.push("b2b");
					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr6aSections.push("b2ba");
					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr6aSections.push("cdn");
					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr6aSections.push("cdna");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSection6AButtonIntrigrationFinal(postData);
			} else {
				var oSelectedItem = this.byId("idgetStablegstr6").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetStablegstr6").getModel("Gstr6ASucess").getData().resp;
				var oTaxPeriod = this.byId("id_TaxProcessGstr6A").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {
					"req": []
				};
				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr6aSections": [],
						"action_required": "Y",
						"isFailed": false
					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr6aSections.push("b2b");
					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr6aSections.push("b2ba");
					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr6aSections.push("cdn");
					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr6aSections.push("cdna");
					}
				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr6aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					sap.m.MessageBox.warning("Select at least one Section");
					return;
				}
				this.getSection6AButtonIntrigrationFinal(postData);
			}
		},

		getSection6AButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6aGstnGetSection.do",
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
						MessageBox.error("Gstr6aGstnGetSection : Error");
					});
			});
		},

		onSelectAllCheck: function (oEvent) {
			var oView = this.getView();
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = oView.getModel("Gstr6ASucess").getData();
			if (oSelectedFlag) {
				this.byId("idgetVtablegstr6").selectAll();
				this.byId("idgetStablegstr6").selectAll();
			} else {
				this.byId("idgetVtablegstr6").setSelectedKeys([]);
				this.byId("idgetStablegstr6").setSelectedKeys([]);
			}
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = oSelectedFlag;
				data.resp.lastCall[i].b2baFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
			}
			oView.getModel("Gstr6ASucess").refresh();
		},

		onDownloadEntityPDF: function (flag) {
			var aPath = this.getView().byId("idPREntityTableNew").getSelectedIndices(),
				oModelData = this.getView().getModel("Gstr6PRSumData").getProperty("/resp");

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
						"taxPeriod": this.byId("idTaxperiodGstr6pro").getValue(),
						"isDigigst": flag,
						"dataSecAttrs": {
							"GSTIN": aGSTIN
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(payload.req.dataSecAttrs, "dpGstr1Summary");
			}
			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed therefrom", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						payload.req.isVerified = sAction;
						this.reportDownload(payload, "/aspsapapi/gstr6PdfReportAsync.do");
					}.bind(this)
				});
		},

		onExportPDF: function (button, flag) {
			var vType = "gstr6ReviewSummary",
				aGstin = [this.oView.byId("idgstingstrnew1").getSelectedKey()],
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": this.oView.byId("idTaxSummary").getValue(),
						"isDigigst": flag,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, "dpGstr1Summary");
			}
			MessageBox.confirm(
				"I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed therefrom", {
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					initialFocus: sap.m.MessageBox.Action.YES,
					onClose: function (sAction) {
						oPayload.req.isVerified = sAction;
						this.pdfDownload(oPayload, "/aspsapapi/gstr6PdfReport.do");
					}.bind(this)
				});
		},

		onExportExcel: function (button) {
			if (button === "P") {
				var vType = "gstr6ProcessedScreen";
				var vTaxPeriod = this.oView.byId("idTaxperiodGstr6pro").getValue();
				var vAdaptFilter = "dpProcessRecord";
				var aGstin = this.byId("idgstingstr6").getSelectedKeys();

			} else {
				var vType = "gstr6ReviewSummary";
				var vTaxPeriod = this.oView.byId("idTaxSummary").getValue();
				var vAdaptFilter = "dpGstr1Summary";
				var aGstin = [this.oView.byId("idgstingstrnew1").getSelectedKey()];
			}
			var vUrl = "/aspsapapi/gstr6ReportsDownloads.do",
				oPayload = {
					"req": {
						"type": vType,
						"entityId": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, vAdaptFilter);
			}
			this.excelDownload(oPayload, vUrl);
		},

		onExportExcel1: function (button) {
			var vTaxPeriod = this.oView.byId("idTaxPeriodDetailed").getValue();
			var vAdaptFilter = "dpProcessRecord";
			var aGstin = this.byId("idGetfgiGSINTMain2GSTR6").getSelectedKeys();

			var vUrl = "/aspsapapi/gstr6aProcessSumamryDownloads.do",
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"tableType": this.oView.byId("iddroptatype2Gstr6").getSelectedKeys(),
						"docType": this.oView.byId("iddropDoctype2Gstr6").getSelectedKeys(),
						// "data": this.oView.byId("iddropdata2Gstr6").getSelectedKeys(),
						"dataSecAttrs": {
							"GSTIN": aGstin
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, vAdaptFilter);
			}
			this.excelDownload(oPayload, vUrl);
		},

		onExportExcel12: function (button) {
			var vTaxPeriod = this.oView.byId("idTaxPeriodDetailed1").getValue();
			var vAdaptFilter = "dpProcessRecord";
			var aGstin = this.byId("idGetfgiGSINTMain2GSTR61").getSelectedKey();
			var vUrl = "/aspsapapi/gstr6aReviewSummaryDownload.do",
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"tableType": [],
						"docType": [],
						// "data": this.oView.byId("iddropdata2Gstr61").getSelectedKeys(),
						"dataSecAttrs": {
							"GSTIN": [aGstin]
						}
					}
				};
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, vAdaptFilter);
			}
			this.excelDownload(oPayload, vUrl);
		},

		onSelectAllCheckHeader: function (oEvent, field) {
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.getView().getModel("Gstr6AsumSucess").getData();
			var oBtProcess = sap.ui.getCore().byId("idProcessStatusBtn").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastCall[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastCall[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastCall[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
						break;
					case "isdFlag":
						data.resp.lastCall[i].isdFlag = oSelectedFlag;
						break;
					case "isdaFlag":
						data.resp.lastCall[i].isdaFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
						break;
					case "isdFlag":
						data.resp.lastSuccess[i].isdFlag = oSelectedFlag;
						break;
					case "isdaFlag":
						data.resp.lastSuccess[i].isdaFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			}
			this.getView().getModel("Gstr6AsumSucess").refresh();
		},

		onRowSelectionChange: function (oEvent) {
			var data = this.getView().getModel("Gstr6AsumSucess").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			var oBtProcess = sap.ui.getCore().byId("idProcessStatusBtn").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlag(true);
				this._showing(true);
			} else if (vRowIndex === -1) {
				this._setFlag(false);
				this._showing(false);
			} else {
				if (oBtProcess === "LCS") {
					if (data.resp.lastCall[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastCall[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].isdFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].isdaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				} else {
					if (data.resp.lastSuccess[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastSuccess[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].isdFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].isdaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].lsFlag = oSelectedFlag;
				}
			}
			this.getView().getModel("Gstr6AsumSucess").refresh();
		},

		_setFlag: function (oSelectedFlag) {
			var data = this.getView().getModel("Gstr6AsumSucess").getData();
			var oBtProcess = sap.ui.getCore().byId("idProcessStatusBtn").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					data.resp.lastCall[i].b2bFlag = oSelectedFlag;
					data.resp.lastCall[i].b2baFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[i].isdFlag = oSelectedFlag;
					data.resp.lastCall[i].isdaFlag = oSelectedFlag;
					data.resp.lastCall[i].lsFlag = oSelectedFlag;
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].isdFlag = oSelectedFlag;
					data.resp.lastSuccess[i].isdaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].lsFlag = oSelectedFlag;
				}
			}
		},

		_showing: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.b2baFlag = oSelectedFlag;
			oData.cdnFlag = oSelectedFlag;
			oData.cdnaFlag = oSelectedFlag;
			oData.isdFlag = oSelectedFlag;
			oData.isdaFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},

		getUpdateSaveToGstinButtonIntrigration: function () {
			var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var vGstins = this.oView.byId("idgstingstrnew1").getSelectedKey();
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"returnType": "GSTR6",
					"dataSecAttrs": {
						"GSTIN": [vGstins]
					}
				}
			};
			this.getUpdateSavetoGstnButtonIntrigrationFinal(postData);
		},

		getUpdateSavetoGstnButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSaveGstnStatus.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var oGstr6savetogstinDate = new sap.ui.model.json.JSONModel(data.resp);
						oView.setModel(oGstr6savetogstinDate, "Gstr6saveToGstinData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("getSaveGstnStatus : Error");
					});
			});
		},

		onRowSelectionChange6A: function (oEvent) {
			var data = this.getView().getModel("Gstr6ASucess").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			var oBtProcess = this.byId("sbUpdatStatusGstr6").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlag1(true);
				this._showing1(true);
			} else if (vRowIndex === -1) {
				this._setFlag1(false);
				this._showing1(false);
			} else {
				if (oBtProcess === "LCS") {
					if (data.resp.lastCall[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastCall[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				} else {
					if (data.resp.lastSuccess[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastSuccess[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].lsFlag = oSelectedFlag;
				}
			}
			this.getView().getModel("Gstr6ASucess").refresh();
		},

		_setFlag1: function (oSelectedFlag) {
			var data = this.getView().getModel("Gstr6ASucess").getData();
			var oBtProcess = this.byId("sbUpdatStatusGstr6").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					data.resp.lastCall[i].b2bFlag = oSelectedFlag;
					data.resp.lastCall[i].b2baFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[i].lsFlag = oSelectedFlag;
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].lsFlag = oSelectedFlag;
				}
			}
		},

		_showing1: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.b2baFlag = oSelectedFlag;
			oData.cdnFlag = oSelectedFlag;
			oData.cdnaFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},

		onSelectAllCheckHeader6A: function (oEvent, field) {
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.getView().getModel("Gstr6ASucess").getData();
			var oBtProcess = this.byId("sbUpdatStatusGstr6").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastCall[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastCall[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastCall[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			}
			this.getView().getModel("Gstr6ASucess").refresh();
		},

		onExportExcelGstr6A1: function (button) {
			var oData = this.getView().getModel("Gstr6DistCrEligData").getProperty("/"),
				aIndex = this.byId("idisdinvEGstr6").getSelectedIndices(),
				vTaxPeriod = this.getView().byId("id_disCrEligTax").getValue(),
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (aIndex.length) {
				aIndex.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[e].isdGstin);
				});
			} else {
				oData.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(e.isdGstin);
				})
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, "dpProcessRecord");
			}
			this.excelDownload(oPayload, "/aspsapapi/distributionEligibleReport.do");
		},

		onExportExcelGstr6A2: function (button) {
			var oData = this.getView().getModel("Gstr6DistCrInEligData").getProperty("/"),
				aIndex = this.byId("idsecondGstrtab").getSelectedIndices(),
				vTaxPeriod = this.oView.byId("id_disCrEligTax1").getValue(),
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (aIndex.length) {
				aIndex.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[e].isdGstin);
				});
			} else {
				oData.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(e.isdGstin);
				})
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, "dpProcessRecord");
			}
			this.excelDownload(oPayload, "/aspsapapi/distributionIneligibleReport.do");
		},

		onExportExcelGstr6A3: function (button) {
			var oData = this.getView().getModel("Gstr6ReDistCrEligData").getProperty("/"),
				aIndex = this.byId("idThirdGstrtab").getSelectedIndices(),
				vTaxPeriod = this.oView.byId("id_disCrEligTax2").getValue(),
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (aIndex.length) {
				aIndex.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[e].isdGstin);
				});
			} else {
				oData.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(e.isdGstin);
				})
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, "dpProcessRecord");
			}
			this.excelDownload(oPayload, "/aspsapapi/redistributionEligibleReport.do");
		},

		onExportExcelGstr6A4: function (button) {
			var oData = this.getView().getModel("Gstr6ReDistCrInEligData").getProperty("/"),
				aIndex = this.byId("idvoiceisdcr1Gstr6").getSelectedIndices(),
				vTaxPeriod = this.oView.byId("id_disCrEligTax3").getValue(),
				oPayload = {
					"req": {
						"entity": [$.sap.entityID],
						"taxPeriod": vTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (aIndex.length) {
				aIndex.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[e].isdGstin);
				});
			} else {
				oData.forEach(function (e) {
					oPayload.req.dataSecAttrs.GSTIN.push(e.isdGstin);
				})
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(oPayload.req.dataSecAttrs, "dpProcessRecord");
			}
			this.excelDownload(oPayload, "/aspsapapi/redistributionIneligibleReport.do");
		},

		onGstr6FullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();

			if (action === "openProcess") {
				oPropData.processFullScreen = true;
				this.byId("idPREntityNew").setFullScreen(true);
				this.byId("idPREntityTableNew").setVisibleRowCount(22);

			} else if (action === "closeProcess") {
				oPropData.processFullScreen = false;
				this.byId("idPREntityNew").setFullScreen(false);
				this.byId("idPREntityTableNew").setVisibleRowCount(8);

			} else if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("id_summarytabgstr6").setFullScreen(true);
				this.byId("tabOutward1gstrnew1").expandToLevel(1);
				this.byId("tabOutward1gstrnew1").setVisibleRowCount(24);
				this.byId("tabDistTablegstrnew").expandToLevel(1);
				this.byId("tabDistTablegstrnew").setVisibleRowCount(13);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("id_summarytabgstr6").setFullScreen(false);
				this.byId("tabOutward1gstrnew1").collapseAll();
				this.byId("tabOutward1gstrnew1").setVisibleRowCount(5);
				this.byId("tabDistTablegstrnew").collapseAll();
				this.byId("tabDistTablegstrnew").setVisibleRowCount(4);
			}
			oPropModel.refresh(true);
		},

		onGstr6AFullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();

			if (action === "openProcess") {
				oPropData.processFullScreen = true;
				this.byId("idGetPREntityGstr6").setFullScreen(true);
				this.byId("idGetPREntityTableGstr6").setVisibleRowCount(22);

			} else if (action === "closeProcess") {
				oPropData.processFullScreen = false;
				this.byId("idGetPREntityGstr6").setFullScreen(false);
				this.byId("idGetPREntityTableGstr6").setVisibleRowCount(8);

			} else if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("idsummaryGstr6A").setFullScreen(true);
				this.byId("idGettabRet1mGstr6").expandToLevel(1);
				this.byId("idGettabRet1mGstr6").setVisibleRowCount(9);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("idsummaryGstr6A").setFullScreen(false);
				this.byId("idGettabRet1mGstr6").collapseAll();
				this.byId("idGettabRet1mGstr6").setVisibleRowCount(5);
			}
			oPropModel.refresh(true);
		},

		_bindPrSummaryProperty: function () {
			var oPrProperty = {
				"process": true,
				"summary": false,
				"processFullScreen": false,
				"summaryFullScreen": false,
				"selectedGstin": null
			};
			this.getView().setModel(new JSONModel(oPrProperty), "PrProperty");
		},

		fnSubmitGSTR6: function () {
			var that = this;
			MessageBox.show("Do you want to Proceed To File ?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.getProceedToFileButtonIntrigration();
					}
				}
			});
		},

		getProceedToFileButtonIntrigration: function () {
			var that = this;
			var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"gstin": this.oView.byId("idgstingstrnew1").getSelectedKey(),
					"ret_period": oTaxPeriod
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/proceedToFile.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							sap.m.MessageBox.success(data.resp.msg, {
								styleClass: "sapUiSizeCompact"
							});
							that.onPressSummaryGo();
						} else {
							sap.m.MessageBox.error(data.resp.msg, {
								styleClass: "sapUiSizeCompact"
							});
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("proceedToFile.do : Error");
					});
			});
		},

		getSubmitButtonIntrigration: function () {
			var oTaxPeriod = this.getView().byId("idTaxSummary").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": [{
					"gstin": this.oView.byId("idgstingstrnew1").getSelectedKey(),
					"ret_period": oTaxPeriod
				}]
			};
			this.getSubmitButtonIntrigrationFinal(postData);
		},

		onDialogPressSubmit: function () {
			var that = this;
			if (!this.pressDialogSubmit) {
				this.pressDialogSubmit = new Dialog({
					title: "Submit Message",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							// that.getGstr6ASucessStatusDataFinal();
							this.pressDialogSubmit.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialogSubmit);
			}
			this.pressDialogSubmit.open();
		},

		getSubmitButtonIntrigrationFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6GstnSubmit.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							for (var i = 0; i < data.resp.length; i++) {
								sap.m.MessageBox.success(data.resp[i].msg, {
									styleClass: "sapUiSizeCompact"
								});
							}
							that.onPressSummaryGo();
						} else {
							if (data.resp.length > 0) {
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
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPressSubmit();
							} else {
								sap.m.MessageBox.error("Submit failed for selected(active) GSTINs.");
							}
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr6GstnSubmit : Error");
					});
			});
		},

		onPressRefreshBtn: function () {
			this.getGstr6SucessStatusDataFinal();
		},

		onPressRefresh2Btn: function () {
			this.getGstr6ASucessStatusDataFinal();
		},

		onSignFile: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			if (key === "DSC") {
				this.onSignFileDSC();
				this.DSCEVCflag = "DSC"
			} else {
				this.onSignFileDSC();
				this.DSCEVCflag = "EVC"
			}
		},

		onSignFileDSC: function () {
			var that = this;
			if (this.authState === "I") {
				MessageBox.show(
					"Auth token is inactive for selected GSTIN, please activate and retry.", {
						icon: MessageBox.Icon.WARNING,
						title: "Warning"
					});
				return;
			}

			if (!this._oDialogSaveStats56) {
				this._oDialogSaveStats56 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.SignandFile", this);
				this.getView().addDependent(this._oDialogSaveStats56);
			}

			var Request = {
				"req": {
					"gstin": this.byId("idgstingstrnew1").getSelectedKey()
				}
			};

			var BulkGstinSaveModel = new JSONModel();
			var GstnsList = "/aspsapapi/get3BSignAndFilePanDetails.do";
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					})
					.done(function (data, status, jqXHR) {
						that._oDialogSaveStats56.open();
						if (that.DSCEVCflag === "DSC") {
							data.resp.header = "Please select the authorised signatory for which DSC is attached for initiating filing of GSTR-6"
						} else {
							data.resp.header = "Please select the authorised signatory for which EVC is attached for initiating filing of GSTR-6"
						}
						BulkGstinSaveModel.setData(data.resp);
						that.getView().setModel(BulkGstinSaveModel, "SignandFile");
					})
					.fail(function (jqXHR, status, err) {});
			});
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
			var that = this;
			MessageBox.show("Do you want to proceed with filing GSTR-6?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						//that.onoffLiaSaveChanges();

						if (that.DSCEVCflag === "DSC") {
							that.onSaveSign2();
						} else {
							that.onSaveSignEVCConformation();
						}
					}
				}
			});
		},

		onSaveSign2: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var Request = {
				"req": {
					"gstin": this.byId("idgstingstrnew1").getSelectedKey(),
					"taxPeriod": this.byId("idTaxSummary").getValue(),
					"pan": selItem
				}
			};

			this.excelDownload(Request, "/aspsapapi/Gstr6SignAndFileStage1.do");
			this._oDialogSaveStats56.close();
			var that = this;
			MessageBox.show("GSTR6 filing is initiated, click on filing status to view the status", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				onClose: function (oAction) {
					//that.onSignFile1();
					that._getSignFileStatus1();
				}
			});
		},

		onSaveSignEVCConformation: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var date = this.byId("idTaxSummary").getValue();
			var taxPeriod = date.slice(0, 2) + "/" + date.slice(2, 6);
			var Request = {
				"req": {
					"gstin": this.byId("idgstingstrnew1").getSelectedKey(),
					"taxPeriod": taxPeriod,
					"pan": selItem
				}
			};

			this.ReqPayload = Request;
			if (!this._oDialogSaveStatsConfirm) {
				this._oDialogSaveStatsConfirm = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr6.SignandFileConformation", this);
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
			var that = this;
			var RequestPayload = {
				"req": {
					"gstin": this.ReqPayload.req.gstin,
					"taxPeriod": this.byId("idTaxSummary").getValue(),
					"pan": this.ReqPayload.req.pan,
					"returnType": "gstr6"
				}
			};

			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/evcSignAndFileStage1.do",
						contentType: "application/json",
						data: JSON.stringify(RequestPayload)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts !== undefined) {
							if (sts.status === "S") {
								// MessageBox.success(data.resp);
								//that.onSearch();
								that._getSignandFileOTP(data)
								that.signId = data.resp.signId
								that._oDialogSaveStatsConfirm.close();
							} else {
								MessageBox.error(data.resp);
								// that._getSignandFileOTP(data)
							}
						} else {
							MessageBox.error(JSON.parse(data).hdr.message);
							// that._getSignandFileOTP(data)
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						//MessageBox.error("Error Occured While Saving the Changes");
					});
			});
		},

		_getSignandFileOTP: function (data) {
			if (!this._oDialogSignandFileOTP) {
				this._oDialogSignandFileOTP = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr6.SignandFileOTP", this);
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
			var that = this;
			var RequestPayload = {
				"req": {
					"signId": this.signId,
					"otp": otp,
				}
			}
			$(document).ready(function ($) {
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
						that._oDialogSignandFileOTP.close();
						// var sts = data.hdr;
						// if (sts !== undefined) {
						// 	if (sts.status === "S") {
						// 		MessageBox.success(data.resp);
						// 		that._oDialogSignandFileOTP.close();
						// 		//that.onSearch();
						// 		// that._getSignandFileOTP(data)
						// 	} else {
						// 		MessageBox.error(data.resp);
						// 	}
						// } else {
						// 	MessageBox.error(JSON.parse(data).hdr.message);
						// }
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						//MessageBox.error("Error Occured While Saving the Changes");
					});
			});
		},

		onFilingStatus: function () {
			if (!this._oDialogSaveStats9) {
				this._oDialogSaveStats9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr6.FilingStatus", this);
				this.getView().addDependent(this._oDialogSaveStats9);
			}
			var gstin = this.byId("idgstingstrnew1").getSelectedKey();
			var vDate = new Date();
			this.byId("FStp").setMaxDate(vDate);
			this.byId("FStp").setValue(this.byId("idTaxSummary").getValue());
			this.byId("FsGstin").setSelectedKey(gstin);
			this.onSaveOkay12();
			this._oDialogSaveStats9.open();
		},

		onCloseDialogFS: function () {
			this._oDialogSaveStats9.close();
			this._getSignFileStatus1();
		},

		onSaveOkay12: function () {
			var that = this;
			var req = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": this.byId("FStp").getValue()
				}
			};
			var GstinSaveModel = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr6FilingStatus.do";
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(req)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();

						GstinSaveModel.setData(data.resp);
						that.getView().byId("idTableFS").setModel(GstinSaveModel, "TableFS");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onSaveStatusDownload1: function (oEvt) {
			this.oReqId1 = oEvt.getSource().getParent().getBindingContext("TableFS").getObject().refId;
			var oReqExcelPath = "/aspsapapi/downloadGstr6Errors.do?id=" + this.oReqId1 + "";
			window.open(oReqExcelPath);
		},

		_getGstr6A: function () {
			var oPayload = {
				"req": {
					"entity": Number($.sap.entityID),
					"financialYear": this.byId("dtFinYearGstrNew12").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slGet6aProcessGstinNew").getSelectedKeys()
					}
				}
			};

			var oGstr2AModel = new JSONModel();
			var oGstr2AView = this.getView();
			var Gstr2APath = "/aspsapapi/gstr2getGSTR6aDashboard.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: Gstr2APath,
						contentType: "application/json",
						data: JSON.stringify(oPayload)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var odata = data.resp;
						this.arry = [];
						for (var i = 0; i < odata.length; i++) {
							var itemArr = odata[i].taxPeriodDetails;
							if (itemArr !== undefined) {
								for (var j = 0; j < itemArr.length; j++) {
									var timep = itemArr[j].taxPeriod;
									var intiatedOnDt = itemArr[j].initiatedOn;
									var Status = itemArr[j].status;
									var downloadbleFlag = itemArr[j].flag;
									var filePath = itemArr[j].filePath;
									var errorMsg = itemArr[j].errorMsg;
									if (timep.slice(0, 2) === "04") {
										odata[i].Apriltimep = timep;
										odata[i].statusAprl = Status;
										odata[i].initiatedOnApril = intiatedOnDt;
										odata[i].filePathAprl = filePath;
										odata[i].downloadbleFlagAprl = downloadbleFlag;
										odata[i].AprilerrorMsg = errorMsg;
										odata[i].AprilCheck = false;
									} else if (timep.slice(0, 2) === "05") {
										odata[i].Maytimep = timep;
										odata[i].statusMay = Status;
										odata[i].initiatedOnMay = intiatedOnDt;
										odata[i].filePathMay = filePath;
										odata[i].downloadbleFlagMay = downloadbleFlag;
										odata[i].MayterrorMsg = errorMsg;
										odata[i].MayCheck = false;
									} else if (timep.slice(0, 2) === "06") {
										odata[i].Junetimep = timep;
										odata[i].statusJune = Status;
										odata[i].initiatedOnJune = intiatedOnDt;
										odata[i].filePathJune = filePath;
										odata[i].downloadbleFlagJune = downloadbleFlag;
										odata[i].JuneerrorMsg = errorMsg;
										odata[i].JuneCheck = false;
									} else if (timep.slice(0, 2) === "07") {
										odata[i].Julytimep = timep;
										odata[i].statusJuly = Status;
										odata[i].initiatedOnJuly = intiatedOnDt;
										odata[i].filePathJuly = filePath;
										odata[i].downloadbleFlagJuly = downloadbleFlag;
										odata[i].JulyerrorMsg = errorMsg;
										odata[i].JulyCheck = false;
									} else if (timep.slice(0, 2) === "08") {
										odata[i].Augtimep = timep;
										odata[i].statusAug = Status;
										odata[i].initiatedOnAug = intiatedOnDt;
										odata[i].filePathAug = filePath;
										odata[i].downloadbleFlagAug = downloadbleFlag;
										odata[i].AugerrorMsg = errorMsg;
										odata[i].AugCheck = false;
									} else if (timep.slice(0, 2) === "09") {
										odata[i].Septtimep = timep;
										odata[i].statusSept = Status;
										odata[i].initiatedOnSept = intiatedOnDt;
										odata[i].filePathSept = filePath;
										odata[i].downloadbleFlagSept = downloadbleFlag;
										odata[i].SepterrorMsg = errorMsg;
										odata[i].SepCheck = false;
									} else if (timep.slice(0, 2) === "10") {
										odata[i].Octtimep = timep;
										odata[i].statusOct = Status;
										odata[i].initiatedOnOct = intiatedOnDt;
										odata[i].filePathOct = filePath;
										odata[i].downloadbleFlagOct = downloadbleFlag;
										odata[i].OcterrorMsg = errorMsg;
										odata[i].OctCheck = false;
									} else if (timep.slice(0, 2) === "11") {
										odata[i].Novtimep = timep;
										odata[i].statusNov = Status;
										odata[i].initiatedOnNov = intiatedOnDt;
										odata[i].filePathNov = filePath;
										odata[i].downloadbleFlagNov = downloadbleFlag;
										odata[i].NoverrorMsg = errorMsg;
										odata[i].NovCheck = false;
									} else if (timep.slice(0, 2) === "12") {
										odata[i].Dectimep = timep;
										odata[i].statusDec = Status;
										odata[i].initiatedOnDec = intiatedOnDt;
										odata[i].filePathDec = filePath;
										odata[i].downloadbleFlagDec = downloadbleFlag;
										odata[i].DecterrorMsg = errorMsg;
										odata[i].DecCheck = false;
									} else if (timep.slice(0, 2) === "01") {
										odata[i].Jantimep = timep;
										odata[i].statusJan = Status;
										odata[i].initiatedOnJan = intiatedOnDt;
										odata[i].filePathJan = filePath;
										odata[i].downloadbleFlagJan = downloadbleFlag;
										odata[i].JanerrorMsg = errorMsg;
										odata[i].JanCheck = false;
									} else if (timep.slice(0, 2) === "02") {
										odata[i].Febtimep = timep;
										odata[i].statusFeb = Status;
										odata[i].initiatedOnFeb = intiatedOnDt;
										odata[i].filePathFeb = filePath;
										odata[i].downloadbleFlagFeb = downloadbleFlag;
										odata[i].FeberrorMsg = errorMsg;
										odata[i].FebCheck = false;
									} else if (timep.slice(0, 2) === "03") {
										odata[i].Martimep = timep;
										odata[i].statusMar = Status;
										odata[i].initiatedOnMar = intiatedOnDt;
										odata[i].filePathMar = filePath;
										odata[i].downloadbleFlagMar = downloadbleFlag;
										odata[i].MarerrorMsg = errorMsg;
										odata[i].MarCheck = false;
									}
								}
							}
							this.arry.push(odata[i]);
						}
						oGstr2AModel.setData(this.arry);
						oGstr2AView.setModel(oGstr2AModel, "Gstr6ANewPRSumData");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			});
		},

		onRowSelectionChange2A: function (oEvent) {
			var vRowIndex = oEvent.getParameters().rowIndex;
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				var object = {
					"aprFlag": true,
					"mayFlag": true,
					"junFlag": true,
					"julFlag": true,
					"augFlag": true,
					"sepFlag": true,
					"octFlag": true,
					"novFlag": true,
					"decFlag": true,
					"janFlag": true,
					"febFlag": true,
					"marFlag": true,
				};
				this.getView().setModel(new JSONModel(object), "month");
			} else if (vRowIndex === -1) {
				var object = {
					"aprFlag": false,
					"mayFlag": false,
					"junFlag": false,
					"julFlag": false,
					"augFlag": false,
					"sepFlag": false,
					"octFlag": false,
					"novFlag": false,
					"decFlag": false,
					"janFlag": false,
					"febFlag": false,
					"marFlag": false,
				};
				this.getView().setModel(new JSONModel(object), "month");
			}
		},

		onDownloadnewGet6AReport: function () {
			var oSelectedItem = this.getView().byId("idgetVtablegstr6progstr2New").getSelectedIndices();
			var oModelForTab1 = this.byId("idgetVtablegstr6progstr2New").getModel("Gstr6ANewPRSumData").getData();
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.information("Select at least one GSTIN");
				return;
			}
			var aMonth = [];
			var oMonthData = this.getView().getModel("month").getData();

			if (oMonthData.aprFlag) {
				aMonth.push("04");
			}
			if (oMonthData.mayFlag) {
				aMonth.push("05");
			}
			if (oMonthData.junFlag) {
				aMonth.push("06");
			}
			if (oMonthData.julFlag) {
				aMonth.push("07");
			}
			if (oMonthData.augFlag) {
				aMonth.push("08");
			}
			if (oMonthData.sepFlag) {
				aMonth.push("09");
			}
			if (oMonthData.octFlag) {
				aMonth.push("10");
			}
			if (oMonthData.novFlag) {
				aMonth.push("11");
			}
			if (oMonthData.decFlag) {
				aMonth.push("12");
			}
			if (oMonthData.janFlag) {
				aMonth.push("01");
			}
			if (oMonthData.febFlag) {
				aMonth.push("02");
			}
			if (oMonthData.marFlag) {
				aMonth.push("03");
			}
			// if (aMonth.length == 0) {
			// 	sap.m.MessageBox.warning("Select at least one Tax Period");
			// 	return;
			// }
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"fy": this.byId("dtFinYearGstrNew12").getSelectedKey(),
					"month": aMonth,
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

			for (var i = 0; i < oSelectedItem.length; i++) {
				postData.req.dataSecAttrs.GSTIN.push(oModelForTab1[oSelectedItem[i]].gstin);
			}
			this.excelDownload(postData, "/aspsapapi/gstr6aReportsDownloadsDashboard.do");
		},

		onPressGetGstr6A: function (view) {
			var oSelectedItem = this.getView().byId("idgetVtablegstr6progstr2New").getSelectedIndices();
			var oModelForTab1 = this.getView().getModel("Gstr6ANewPRSumData").getData();
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.information("Select at least one GSTIN");
				return;
			}

			var aMonth = [];
			var oMonthData = this.getView().getModel("month").getData();

			if (oMonthData.aprFlag) {
				aMonth.push("04");
			}
			if (oMonthData.mayFlag) {
				aMonth.push("05");
			}
			if (oMonthData.junFlag) {
				aMonth.push("06");
			}
			if (oMonthData.julFlag) {
				aMonth.push("07");
			}
			if (oMonthData.augFlag) {
				aMonth.push("08");
			}
			if (oMonthData.sepFlag) {
				aMonth.push("09");
			}
			if (oMonthData.octFlag) {
				aMonth.push("10");
			}
			if (oMonthData.novFlag) {
				aMonth.push("11");
			}
			if (oMonthData.decFlag) {
				aMonth.push("12");
			}
			if (oMonthData.janFlag) {
				aMonth.push("01");
			}
			if (oMonthData.febFlag) {
				aMonth.push("02");
			}
			if (oMonthData.marFlag) {
				aMonth.push("03");
			}

			var postData = {
				"req": []
			};
			if (aMonth.length == 0) {
				sap.m.MessageBox.warning("Select at least one Tax Period");
				return;
			}

			for (var i = 0; i < oSelectedItem.length; i++) {
				postData.req.push({
					"gstin": oModelForTab1[oSelectedItem[i]].gstin,
					"finYear": this.byId("dtFinYearGstrNew12").getSelectedKey(),
					"month": aMonth,
					"gstr6aSections": ["b2b", "b2ba", "cdn", "cdna"],
					"action_required": "Y",
					//"isFailed": false
				});
			}
			this.vPSFlag = "P";
			this.getGstr6AGstnGetSection(postData);
		},

		getGstr6AGstnGetSection: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/Gstr6aGetCallDashboard.do",
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
						that.onDialogPress1c();
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Gstr2aGstnGetSection.do : Error");
					});
			});
		},

		onDialogPress1c: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get GSTR-6A Status",
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
								//infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.clearMonth();
							if (that.vPSFlag === "P") {
								that._getGstr6A();
							} else {
								// that._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		onPressClear: function (view) {
			var that = this,
				vDate = new Date(),
				date = new Date(),
				date1 = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1),
				vEmailDateVC = new Date();

			date.setDate(date.getDate() - 9);
			date1.setMonth(date1.getMonth() - 1);
			vPeriod.setDate(1);

			if (view === "getGstr6A") {
				this.byId("slGet6aProcessGstinNew").setSelectedKeys();
				this.byId("dtFinYearGstrNew12").setSelectedKey("2022-23");
				this._getGstr6A();

			} else if (view === "gstr6ASummary") {
				var oGstData = this.oView.byId("idGetPRSummaryMainTitleGstr6").getText();

				if (oGstData === "GSTN Data") {
					this.byId("idGetfgiGSINTMain2GSTR6").setSelectedKeys();
					this.byId("iddroptatype2Gstr6").setSelectedKeys();
					this.byId("iddropDoctype2Gstr6").setSelectedKeys();

					this.byId("idFromTaxPeriodDetailed").setMaxDate(vDate);
					this.byId("idFromTaxPeriodDetailed").setDateValue(vPeriod);
					this.byId("idToTaxPeriodDetailed").setMinDate(vPeriod);
					this.byId("idToTaxPeriodDetailed").setMaxDate(vDate);
					this.byId("idToTaxPeriodDetailed").setDateValue(vDate);
					this.getGstr6AProcessData("");

				} else if (oGstData === "Review Summary") {
					this.byId("idGetfgiGSINTMain2GSTR61").setSelectedKey();

					this.byId("idRSFromTaxPeriodDetailed").setMaxDate(vDate);
					this.byId("idRSFromTaxPeriodDetailed").setDateValue(vPeriod);
					this.byId("idRSToTaxPeriodDetailed").setMinDate(vPeriod);
					this.byId("idRSToTaxPeriodDetailed").setMaxDate(vDate);
					this.byId("idRSToTaxPeriodDetailed").setDateValue(vDate);
					this.getGstr6ADetailedData("");
				}
			}
		},

		clearMonth: function () {
			var object = {
				"aprFlag": false,
				"mayFlag": false,
				"junFlag": false,
				"julFlag": false,
				"augFlag": false,
				"sepFlag": false,
				"octFlag": false,
				"novFlag": false,
				"decFlag": false,
				"janFlag": false,
				"febFlag": false,
				"marFlag": false,
			};
			this.getView().setModel(new JSONModel(object), "month");
		},

		onPressGenerateOTP: function (oEvent) {
			var key = this.byId("itbGstr6").getSelectedKey();
			var oValue1;
			if (key === "gstr6Summary") {
				oValue1 = oEvent.getSource().getBindingContext("Gstr6PRSumData").getObject();
				if (oValue1.authToken !== "Inactive") {
					return;
				}
			} else if (key === "gstr6") {
				oValue1 = oEvent.getSource().getBindingContext("Gstr6ANewPRSumData").getObject();
				if (oValue1.authStatus !== "I") {
					return;
				}
			} else {
				oValue1 = oEvent.getSource().getBindingContext("Gstr6APRData").getObject();
				if (oValue1.authToken !== "Inactive") {
					return;
				}
			}

			var vGstin = oValue1.gstin;
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": vGstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressCancel: function () {
			this._dAuthToken.close();
		},

		onPressYes: function () {
			var aData = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getData(),
				that = this,
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			that.byId("dAuthTokenConfirmation").setBusy(true);
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
					that.byId("dAuthTokenConfirmation").setBusy(false);
					that._dAuthToken.close();
					if (data.resp.status === "S") {
						var oModel = that.byId("dVerifyAuthToken").getModel("OtpProperty"),
							oData = oModel.getData();
						oData.verify = false;
						oData.otp = null;
						oData.resendOtp = false;
						oModel.refresh(true);
						that._dGenerateOTP.open();
					} else {
						MessageBox.error("OTP Generation Failed. Please Try Again", { // Modified by Bharat Gupta on 05.02.2020
							styleClass: "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dAuthTokenConfirmation").setBusy(false);
				});
		},

		onPressResendOTP: function () {
			var oOtpModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				oOtpData = oOtpModel.getData(),
				that = this,
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
					that.byId("dVerifyAuthToken").setBusy(false);
					var oModel = that.byId("dVerifyAuthToken").getModel("OtpProperty"),
						oData = oModel.getData();
					oData.verify = false;
					oData.otp = null;
					if (data.resp.status === "S") {
						oData.resendOtp = true;
						that._dGenerateOTP.open();
					} else {
						oData.resendOtp = false;
						MessageBox.error("OTP Generation Failed. Please Try Again", {
							styleClass: "sapUiSizeCompact"
						});
					}
					oModel.refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dVerifyAuthToken").setBusy(false);
				});
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
				that = this,
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
					that.byId("dVerifyAuthToken").setBusy(false);
					that._dGenerateOTP.close();
					if (data.resp.status === "S") {
						MessageBox.success("OTP is  Matched", {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error("OTP is Not Matched", {
							styleClass: "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dVerifyAuthToken").setBusy(false);
					that._dGenerateOTP.close();
				});
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		onPressRequestIDwise2A: function (oEvent) {
			this.getView().byId("idRequestIDwisePage3B").setVisible(true);
			this.getView().byId("dpBulkSavegstrNew1").setVisible(false);
		},

		onPressRequestIDwiseBack3B: function () {
			this.getView().byId("idRequestIDwisePage3B").setVisible(false);
			this.getView().byId("dpBulkSavegstrNew1").setVisible(true);
		},

		onPressDownloadPDF: function () {
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"gstrPrintDetails": []
				}
			};

			var oView = this.getView();
			var oModelData = oView.getModel("Gstr6DistCrEligData").getData();
			var oPath = oView.byId("idisdinvEGstr6").getSelectedIndices();
			if (oPath.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < oPath.length; i++) {
				var data = {
					"id": oModelData[oPath[i]].id,
					"documentType": oModelData[oPath[i]].documentType,
					"sgstin": oModelData[oPath[i]].isdGstin,
				}
				postData.req.gstrPrintDetails.push(data);
			}
			this.reportDownload(postData, "/aspsapapi/gstr6DistributionRedistributionPdfReport.do");
		},

		onPressDownloadPDF1: function () {
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"gstrPrintDetails": []
				}
			};

			var oView = this.getView();
			var oModelData = oView.getModel("Gstr6DistCrInEligData").getData();
			var oPath = oView.byId("idsecondGstrtab").getSelectedIndices();
			if (oPath.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < oPath.length; i++) {
				var data = {
					"id": oModelData[oPath[i]].id,
					"documentType": oModelData[oPath[i]].documentType,
					"sgstin": oModelData[oPath[i]].isdGstin,
				}
				postData.req.gstrPrintDetails.push(data);
			}
			this.reportDownload(postData, "/aspsapapi/gstr6DistributionRedistributionPdfReport.do");
		},

		onPressDownloadPDF2: function () {
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"gstrPrintDetails": []
				}
			};

			var oView = this.getView();
			var oModelData = oView.getModel("Gstr6ReDistCrEligData").getData();
			var oPath = oView.byId("idThirdGstrtab").getSelectedIndices();
			if (oPath.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < oPath.length; i++) {
				var data = {
					"id": oModelData[oPath[i]].id,
					"documentType": oModelData[oPath[i]].documentType,
					"sgstin": oModelData[oPath[i]].isdGstin,
				}
				postData.req.gstrPrintDetails.push(data);
			}
			this.reportDownload(postData, "/aspsapapi/gstr6DistributionRedistributionPdfReport.do");
		},

		onPressDownloadPDF3: function () {
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"gstrPrintDetails": []
				}
			};

			var oView = this.getView();
			var oModelData = oView.getModel("Gstr6ReDistCrInEligData").getData();
			var oPath = oView.byId("idvoiceisdcr1Gstr6").getSelectedIndices();
			if (oPath.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < oPath.length; i++) {
				var data = {
					"id": oModelData[oPath[i]].id,
					"documentType": oModelData[oPath[i]].documentType,
					"sgstin": oModelData[oPath[i]].isdGstin,
				}
				postData.req.gstrPrintDetails.push(data);
			}
			this.reportDownload(postData, "/aspsapapi/gstr6DistributionRedistributionPdfReport.do");
		},

		onEntityAutoCalDigiGST: function () {
			var aIndex = this.byId("idPREntityTableNew").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.information("Please select atleast one GSTIN");
				return;
			}
			MessageBox.confirm("Do you want to initiate Auto Calculate - DigiGST?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var oData = this.getView().getModel("Gstr6PRSumData").getProperty("/resp"),
							payload = {
								"req": {
									"gstin": [],
									"taxPeriod": this.byId("idTaxperiodGstr6pro").getValue()
								}
							}
						payload.req.gstin = aIndex.map(function (e) {
							return oData[e].gstin;
						});
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr6ComputeDigiGstEntityLevel.do",
								contentType: "application/json",
								data: JSON.stringify(payload)
							})
							.done(function (data, status, jqXhr) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									MessageBox.success(data.resp);
								} else {
									MessageBox.error(data.resp);
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					}
				}.bind(this)
			});
		},

		onAutoCalculateDigiGST: function () {
			MessageBox.confirm("Do you want to initiate Auto Calculate - DigiGST?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var payload = {
							"req": {
								"gstin": this.byId("idgstingstrnew1").getSelectedKey(),
								"taxPeriod": this.byId("idTaxSummary").getValue()
							}
						}
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/gstr6ComputeDigiGst.do",
								contentType: "application/json",
								data: JSON.stringify(payload)
							})
							.done(function (data, status, jqXhr) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									MessageBox.success(data.resp);
								} else {
									MessageBox.error(data.resp);
								}
							}.bind(this))
							.fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					}
				}.bind(this)
			});
		},

		onFileNilReturn: function (type) {
			if (!this.Importentmsg) {
				this.Importentmsg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.Importentmsg", this);
				this.getView().addDependent(this.Importentmsg);
			}
			if (type === "E") {
				var oData = this.byId("idPREntityTableNew").getModel("Gstr6PRSumData").getProperty("/resp"),
					aIndex = this.byId("idPREntityTableNew").getSelectedIndices();

				if (!aIndex.length) {
					MessageBox.error("Please select atleast one GSTIN");
					return;
				}
				var payload = {
					"req": {
						"retPeriod": this.byId("idTaxperiodGstr6pro").getValue()
					}
				};
				payload.req.gstinList = aIndex.map(function (idx) {
					return oData[idx].gstin;
				});
			} else {
				payload = {
					"req": {
						"gstinList": [this.byId("idgstingstrnew1").getSelectedKey()],
						"retPeriod": this.byId("idTaxSummary").getValue()
					}
				};
			}
			this.byId("gstId").setText(payload.req.gstinList);
			this.byId("RpID").setText(payload.req.retPeriod);
			this.Importentmsg.setModel(new JSONModel(payload.req), "payload");
			this.Importentmsg.open();
		},

		onPressCloseNR: function () {
			this.Importentmsg.close();
		},

		onPressYesNR: function () {
			this.Importentmsg.close();
			var payload = {
				"req": this.Importentmsg.getModel("payload").getProperty("/")
			};
			sap.ui.core.BusyIndicator.show(0);
			if (!this._dMsgNilReturn) {
				this._dMsgNilReturn = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.BulkSave1", this);
				this.getView().addDependent(this._dMsgNilReturn);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr6NilFile.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXhr) {
					sap.ui.core.BusyIndicator.hide();
					this._dMsgNilReturn.setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
					this._dMsgNilReturn.open();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseDialogBulkSave1: function () {
			this._dMsgNilReturn.close();
		}
	});
});