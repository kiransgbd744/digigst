sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	'sap/m/library',
	"sap/ui/model/FilterOperator"
], function (Controller, BaseController, Formatter, JSONModel, MessageBox, Filter, library, FilterOperator) {
	"use strict";
	var vCount = 10;
	return BaseController.extend("com.ey.digigst.controller.AutoRecon", {
		formatter: Formatter,
		//return Controller.extend("com.ey.digigst.controller.AutoRecon", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.AutoRecon
		 */
		onInit: function () {
			var vDate = new Date(),
				date1 = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);
			date1.setMonth(date1.getMonth() - 1);

			// Auto Recon Summary
			this._setDateProperty("idInitiateReconPeriod2A", date1, vDate, new Date("2017", "06", "01"));
			this._setDateProperty("idInitiateReconPeriod12A", vDate, vDate, date1);

			this._setDateProperty("idInitiateReconPeriodTax2A", vPeriod, vDate, new Date("2017", "06", "01"));
			this._setDateProperty("idInitiateReconPeriodTax12A", vDate, vDate, vPeriod);

			this._setDateProperty("idInitiateReconPeriodTax2A1", vPeriod, vDate, new Date("2017", "06", "01"));
			this._setDateProperty("idInitiateReconPeriodTax12A1", vDate, vDate, vPeriod);

			this._setDateProperty("dtPI", vPeriod, vDate, new Date("2017", "06", "01"));
			this._setDateProperty("dtPITo", vDate, vDate, vPeriod);

			this._setDateProperty("idReconPeriod2A8", vPeriod, vDate, new Date("2017", "06", "01"));
			this._setDateProperty("idReconPeriod12A9", vDate, vDate, vPeriod);

			var object = {
				"TAX": true,
				"DOC": false,
				"TAX1": true,
				"DOC1": false,
				"PR": true,
				"A2": false,
			};
			this.getView().setModel(new JSONModel(object), "Display");
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				var oModelData = this.getOwnerComponent().getModel("userPermission").getProperty("/appPermission");
				if (oModelData.P22) {
					this.handleIconTabBarSelectAP();
					this._getAllGSTIN();
					this._getRecipientGstin();
				}
			}
		},

		_getAllGSTIN: function () {
			var regGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin"),
				isdGstin = this.getOwnerComponent().getModel("ISDGstin").getProperty("/"),
				aGstin = regGstin.map(function (e) {
					return e;
				});
			isdGstin.forEach(function (e) {
				aGstin.push(e);
			});
			var oData = aGstin.slice();
			oData.unshift({
				"value": "All"
			});
			this.getOwnerComponent().setModel(new JSONModel(oData), "allisdregGstin");
		},

		_getRecipientGstin: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecipientGstins.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": {
								"entityId": +$.sap.entityID
							}
						})
					})
					.done(function (data, status, jqXHR) {
						data.resp.gstinInfo.unshift({
							"gstin": "All"
						});
						this.getView().setModel(new JSONModel(data.resp.gstinInfo), "getRecipientGstins");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						console.log("Recipient Gstin Error:\n", err);
						reject(data);
					}.bind(this));
			}.bind(this));
		},

		handleIconTabBarSelectAP: function () {
			var key = this.byId("APiconid").getSelectedKey();
			switch (key) {
			case "OnBoarding":
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.onBordingTabAPI(false),
						this.onBordingTab2API(false)
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
				break;
			case "AutoReconSummary":
				this.onPressGoForGSTIN2A();
				break;
			case "GenerateReport":
				this.onGRTabBind();
				break;
			}
		},

		onDateRangeChange: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			this.getView().getModel("Display").getData().DOC = (key === "Recon Date");
			this.getView().getModel("Display").getData().TAX = (key !== "Recon Date");
			this.getView().getModel("Display").refresh(true);
		},

		onDateRangeChange1: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			this.getView().getModel("Display").getData().DOC1 = (key === "Recon Date");
			this.getView().getModel("Display").getData().TAX1 = (key !== "Recon Date");
			this.getView().getModel("Display").refresh(true);
		},

		onPressGoForGSTIN2A: function (oEvent) {
			this.byId("searchId").setValue("");
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID)
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getReconSummGstinData.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp.gstinsData), "GSTIN2A");
						this.getView().byId("checkboxID").setSelected(true);
						this.getView().byId("idInitiateReconList2A").selectAll();
						this.onReconSummary();
						this.onIncrementalSummary();
					} else {
						this.getView().setModel(new JSONModel([]), "GSTIN2A");
						this.getView().setModel(new JSONModel([]), "InitiateRecon2A");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel([]), "GSTIN2A");
					this.getView().setModel(new JSONModel([]), "InitiateRecon2A");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSelectallGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList2A").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList2A").selectAll();
			}
			this.onReconSummary();
			this.onIncrementalSummary();
		},

		onSelectionChange1: function (oEvent) {
			this.onReconSummary();
			this.onIncrementalSummary();
		},

		onReconSummary: function (oEvt) {
			var oPath = this.getView().byId("idInitiateReconList2A").getSelectedItems(),
				oModelData = this.getView().getModel("GSTIN2A").getData(),
				selKey = this.byId("idDateRange2A").getSelectedKey(),
				aGSTIN = [];

			oPath.forEach(function (item) {
				aGSTIN.push(item.getBindingContext("GSTIN2A").getProperty("gstin"));
			});
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"recipientGstins": aGSTIN,
					"toTaxPeriodPR": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
					"fromTaxPeriodPR": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
					"toTaxPeriod2A": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax12A1").getValue() : "",
					"fromTaxPeriod2A": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax2A1").getValue() : "",
					"fromReconDate": selKey === "Recon Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
					"toReconDate": selKey === "Recon Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
					"criteria": this.byId("idCriteria").getSelectedKey()
				}
			};
			this.onInitiateRecon1(postData);
		},

		onInitiateRecon1: function (postData) {
			// eslint-disable-line
			var oView = this.getView();
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/getAutoReconSummary.do";
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.hdr.status === "S") {
						that.byId("ARStimeId").setText("(Records as on :" + data.resp.summaryDetails.lastUpdatedOn + ")'");
						var data12 = data.resp.summaryDetails.autoReconSummary;
						data12.unshift({
							"reportCategory": "Auto Locked Records",
							"level": "L1"
						});

						var obj = {
							"reportCategory": "Unlocked Records",
							"level": "L1"
						};

						for (var j = 0; j < data12.length; j++) {
							if (data12[j].lockStatus === "Unlock") {
								data12.splice(j, 0, obj);
								break;
							}
						}
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.summaryDetails.autoReconSummary.length; i++) {
							var ele = data.resp.summaryDetails.autoReconSummary[i];
							var lvl = ele.level; // Get the level of the cur Obj.
							if (lvl === "L1") {
								curL1Obj = ele;
								retArr.push(curL1Obj);
								curL1Obj.level2 = [];
							}
							if (lvl === "L2") {
								curL2Obj = ele;
								curL1Obj.level2.push(curL2Obj);
							}
						}
						var oGSTIN = new JSONModel(retArr);
						oView.setModel(oGSTIN, "InitiateRecon2A");
					} else {
						var oGSTIN2 = new JSONModel([]);
						oView.setModel(oGSTIN2, "InitiateRecon2A");
					}
					//var oInitiateRecon1 = new JSONModel(data.resp);
					//oView.setModel(oInitiateRecon1, "InitiateRecon2A");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
			});
		},

		onIncrementalSummary: function () {
			var oView = this.getView();
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTIN2A").getData();
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			var postData = {
				"req": {
					"recipientGstins": aGSTIN,
					"entityId": $.sap.entityID
				}
			};

			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/getIncrementalDataSumm.do";
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.byId("IncTimeId").setText("(From :" + data.resp.incrementalData.lastUpdatedOn + " " + "To:" + data.resp.incrementalData
							.toLastUpdatedOn +
							")");
						var oGSTIN = new JSONModel(data.resp.incrementalData.incrementalSummary);
						oView.setModel(oGSTIN, "incrementalData");
					} else {
						var oGSTIN2 = new JSONModel([]);
						oView.setModel(oGSTIN2, "incrementalData");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "incrementalData");
				});
			});
		},

		onBordingTabAPI: function (flag) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAutoReconOnBoarding.do",
						contentType: "application/json",
						data: {
							"entityId": $.sap.entityID
						}
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							data.resp.forEach(function (e) {
								e.HDR = "L1";
							});
							this.getView().setModel(new JSONModel(data), "onboarding");
							this.getView().byId("statusId").setText(data.resp[0].lastUpdatedOn);
						} else if (data.resp === "No Data Available") { //
							var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/onBording.json"));
							this.getView().setModel(oModel, "onboarding");
						} else {
							this.getView().setModel(new JSONModel([]), "onboarding");
						}
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onBordingTab2API: function (flag) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAutoReconAddParamOnBrd.do",
						contentType: "application/json",
						data: {
							"entityId": $.sap.entityID
						}
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							data.resp.forEach(function (e) {
								e.HDR = "L1";
							});
							this.getView().setModel(new JSONModel(data), "onboarding2");
							this.getView().byId("statusId1").setText(data.resp[0].lastUpdatedOn);

						} else if (data.resp === "No Data Available") { //
							var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/onBording2.json"));
							this.getView().setModel(oModel, "onboarding2");
						} else {
							this.getView().setModel(new JSONModel([]), "onboarding2");
						}
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onOBsubmit: function () {
			var model = this.getView().getModel("onboarding").getData();
			if (model.resp[1].categories.length === 7) {
				model.resp[1].categories.push({
					"ReportType": "Potential-I",
					"AutoLock": "No",
					"ReverseFeed": "No",
					"ERPReportType": "MisMatch",
					"ApporvalStatus": "Approved for full payment",
					"OptionalReportSelected": false,
					"ImsActionAllowed": "No Response",
					"ImsActionBlocked": "No Response"
				}, {
					"ReportType": "Potential-II",
					"AutoLock": "No",
					"ReverseFeed": "No",
					"ERPReportType": "MisMatch",
					"ApporvalStatus": "Approved for full payment",
					"OptionalReportSelected": false,
					"ImsActionAllowed": "No Response",
					"ImsActionBlocked": "No Response"
				}, {
					"ReportType": "Logical Match",
					"AutoLock": "No",
					"ReverseFeed": "No",
					"ERPReportType": "MisMatch",
					"ApporvalStatus": "Approved for full payment",
					"OptionalReportSelected": false,
					"ImsActionAllowed": "No Response",
					"ImsActionBlocked": "No Response"
				});
			}
			var postData = {
				"entityId": $.sap.entityID,
				"req": model.resp
			};
			var that = this;
			var url = "/aspsapapi/saveAutoReconOnBoarding.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);
					that.onBordingTabAPI(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onOBsubmit1: function () {

			var model = this.getView().getModel("onboarding2").getData();
			var postData = {
				"entityId": $.sap.entityID,
				"req": model.resp
			};
			var that = this;
			var url = "/aspsapapi/saveAutoReconOnBrdAddParam.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);
					that.onBordingTab2API(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onGRTabBind: function (oEvent) {
			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 10
				},
				"req": {
					"entityId": $.sap.entityID
				}
			};
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var that = this;
			var oReqWisePath = "/aspsapapi/getAutoReconReqDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					that._nicPagination(data.hdr);
					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "ReqWiseData2A");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		////////Pagination/////////////
		_nicPagination: function (header) {
			//  //eslint-disable-line
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNo").setText("/ " + pageNumber);
			this.byId("inPageNo").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNo").setValue(pageNumber);
				this.byId("inPageNo").setEnabled(false);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnNext").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			} else if (this.byId("inPageNo").getValue() === "" || this.byId("inPageNo").getValue() === "0") {
				this.byId("inPageNo").setValue(1);
				this.byId("inPageNo").setEnabled(true);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
			} else {
				this.byId("inPageNo").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNo").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNext").setEnabled(true);
				this.byId("btnLast").setEnabled(true);
			} else {
				this.byId("btnNext").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			}
			this.byId("btnPrev").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirst").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination: function (oEvent) {
			//  //eslint-disable-line
			var vValue = parseInt(this.byId("inPageNo").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrev")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrev").setEnabled(false);
				}
				if (!this.byId("btnNext").getEnabled()) {
					this.byId("btnNext").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNext")) {
				var vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrev").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNext").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirst")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirst").setEnabled(false);
				}
				if (!this.byId("btnLast").getEnabled()) {
					this.byId("btnLast").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLast")) {
				vValue = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirst").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLast").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			}
			this.byId("inPageNo").setValue(vValue);
			this.nicCredentialTabl(vValue);
		},

		onSubmitPagination: function () {
			var vPageNo = this.byId("inPageNo").getValue(),
				pageNumber = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl: function (vPageNo) {
			var searchInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": vCount
				},
				"req": {
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAutoReconReqDetails.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._nicPagination(data.hdr);
					this.getView().setModel(new JSONModel(data), "ReqWiseData2A");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},
		onPressGoGR: function () {
			var selKey = this.byId("idDateRange2A1").getSelectedKey(),
				oRecptGstn = this.byId("idPIGstins").getSelectedItems(),
				oEmailRepTypData = this.byId("DocTypeIDPI").getSelectedItems();

			if (oRecptGstn.length === 0) {
				MessageBox.information("Please select atleast one Recipient Gstin");
				return;
			}
			if (oEmailRepTypData.length === 0) {
				MessageBox.information("Please select atleast one Report Type");
				return;
			}
			if (!this._validateGenerateReportDate()) {
				return;
			}
			if (oRecptGstn.length > 0 && oRecptGstn[0].getText().trim() === "All") {
				oRecptGstn.shift();
			}
			if (oEmailRepTypData.length > 0 && oEmailRepTypData[0].getText() === "All") {
				oEmailRepTypData.shift();
			}
			var oRecptGstnArr = oRecptGstn.map(function (e) {
					return {
						"gstin": e.getKey()
					};
				}),
				oEmailRepTypArr = oEmailRepTypData.map(function (e) {
					return {
						"reportType": e.getKey()
					};
				});

			var requestPost = {
				"req": {
					"entityId": $.sap.entityID,
					"recipientGstins": oRecptGstnArr,
					"reportTypes": oEmailRepTypArr,
					"toTaxPeriod": selKey === "Tax Period" ? this.byId("dtPITo").getValue() : "",
					"fromTaxPeriod": selKey === "Tax Period" ? this.byId("dtPI").getValue() : "",
					"reconFromDate": selKey === "Recon Date" ? this._formatDate(this.byId("idReconPeriod2A8").getDateValue()) : "",
					"reconToDate": selKey === "Recon Date" ? this._formatDate(this.byId("idReconPeriod12A9").getDateValue()) : ""
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/generateAutoReconReportReq.do",
					contentType: "application/json",
					data: JSON.stringify(requestPost)
				})
				.done(function (data, status, jqXHR) {
					MessageBox.success(data.resp);
					this.onGRTabBind();
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_validateGenerateReportDate: function () {
			var rangeType = this.byId("idDateRange2A1").getSelectedKey();
			switch (rangeType) {
			case "Tax Period":
				var taxPeriodFr = this.byId("dtPI").getValue(),
					taxPeriodTo = this.byId("dtPITo").getValue(),
					dateFrom = new Date(taxPeriodFr.substr(2), +taxPeriodFr.substr(0, 2) - 1, 1),
					dateTo = new Date(taxPeriodTo.substr(2), +taxPeriodTo.substr(0, 2) - 1, 1);
				break;
			case "Recon Date":
				dateFrom = this.byId("idReconPeriod2A8").getDateValue();
				dateTo = this.byId("idReconPeriod12A9").getDateValue();
				break;
			}
			var months = (dateTo.getFullYear() - dateFrom.getFullYear()) * 12;
			months -= dateFrom.getMonth();
			months += dateTo.getMonth();
			if (months > 5) {
				MessageBox.information("Kindly select max 6 months at once");
				return false;
			}
			return true;
		},

		onPressGSTIN: function (oEvt) {
			var reqId = oEvt.getSource().getBindingContext("ReqWiseData2A").getObject().requestId,
				TabData = this.getView().getModel("ReqWiseData2A").getProperty("/resp/requestData"),
				oGstin = TabData.find(function (e) {
					return e.requestId === reqId;
				});
			if (!this._oGstinPopoverAP) {
				this._oGstinPopoverAP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.PopoverGSTIN", this);
				this.getView().addDependent(this._oGstinPopoverAP);
			}
			this._oGstinPopoverAP.setModel(new JSONModel(oGstin.recipientGstins), "gstins2A");
			this._oGstinPopoverAP.openBy(oEvt.getSource());
		},

		onPressReport: function (oEvt) {
			var reqId = oEvt.getSource().getBindingContext("ReqWiseData2A").getObject().requestId,
				TabData = this.getView().getModel("ReqWiseData2A").getProperty("/resp/requestData"),
				oRptType = TabData.find(function (e) {
					return e.requestId === reqId;
				});
			if (!this._oGstinPopoverAP1) {
				this._oGstinPopoverAP1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.PopoverReport", this);
				this.getView().addDependent(this._oGstinPopoverAP1);
			}
			this._oGstinPopoverAP1.setModel(new JSONModel(oRptType.reportTypes), "PopoverReport");
			this._oGstinPopoverAP1.openBy(oEvt.getSource());
		},

		onRefresh: function () {
			this.onGRTabBind();
		},

		onFromDateChange: function (oEvent) {
			var toDate = this.byId("idInitiateReconPeriod12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriod2A").getDateValue();
			if (fromDate > toDate) {
				this.byId("idInitiateReconPeriod12A").setDateValue(oEvent.getSource().getDateValue());
			}
			this.byId("idInitiateReconPeriod12A").setMinDate(oEvent.getSource().getDateValue());
		},

		onFromTaxPeriodChange: function (oEvent) {
			var toDate = this.byId("idInitiateReconPeriodTax12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A").getDateValue();
			if (fromDate > toDate) {
				this.byId("idInitiateReconPeriodTax12A").setDateValue(oEvent.getSource().getDateValue());
			}
			this.byId("idInitiateReconPeriodTax12A").setMinDate(oEvent.getSource().getDateValue());
		},

		onFromTaxPeriodChange2A: function (oEvent) {
			var toDate = this.byId("idInitiateReconPeriodTax12A1").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A1").getDateValue();
			if (fromDate > toDate) {
				this.byId("idInitiateReconPeriodTax12A1").setDateValue(oEvent.getSource().getDateValue());
			}
			this.byId("idInitiateReconPeriodTax12A1").setMinDate(oEvent.getSource().getDateValue());
		},

		onFromDateChangeGR: function (oEvent) {
			var toDate = this.byId("idReconPeriod12A9").getDateValue(),
				fromDate = this.byId("idReconPeriod2A8").getDateValue();
			if (fromDate > toDate) {
				this.byId("idReconPeriod12A9").setDateValue(oEvent.getSource().getDateValue());
			}
			this.byId("idReconPeriod12A9").setMinDate(oEvent.getSource().getDateValue());
		},

		handleChangePISumm: function (oEvent) {
			var toDate = this.byId("dtPITo").getDateValue(),
				fromDate = this.byId("dtPI").getDateValue();
			if (fromDate > toDate) {
				this.byId("dtPITo").setDateValue(oEvent.getSource().getDateValue());
			}
			this.byId("dtPITo").setMinDate(oEvent.getSource().getDateValue());
		},

		onPITCFullScreen: function (type) {
			this.byId("closebutPITC").setVisible(type === "open");
			this.byId("openbutPITC").setVisible(type !== "open");
			this.byId("FullId").setFullScreen(type === "open");
			this.byId("tabId").setVisibleRowCount(type === "open" ? 22 : 9);
		},

		press: function (oEvt) {
			var oButton = oEvt.getSource();
			if (!this._oGstinPopover12) {
				this._oGstinPopover12 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.PopoverAPM", this);
				this.getView().addDependent(this._oGstinPopover12);
			}
			this._oGstinPopover12.openBy(oButton);
		},

		pressIncrement: function (oEvt) {
			var data = oEvt.getSource().getEventingParent().getParent().getBindingContext("incrementalData").getObject();
			if (data.reportCategory === "New Records") {
				if (!this._oGstinPopover1) {
					this._oGstinPopover1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.Popover1", this);
					this.getView().addDependent(this._oGstinPopover1);
				}
				this._oGstinPopover1.openBy(oEvt.getSource());

			} else if (data.reportCategory === "Modified") {
				if (!this._oGstinPopover2) {
					this._oGstinPopover2 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.Popover2", this);
					this.getView().addDependent(this._oGstinPopover2);
				}
				this._oGstinPopover2.openBy(oEvt.getSource());

			} else if (data.reportCategory === "Deleted") {
				if (!this._oGstinPopover3) {
					this._oGstinPopover3 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.Popover3", this);
					this.getView().addDependent(this._oGstinPopover3);
				}
				this._oGstinPopover3.openBy(oEvt.getSource());
			}
		},

		onExpandFirstLevel1: function () {
			this.byId("idintable2A1").expandToLevel(1);
		},

		onCollapseAll1: function () {
			this.byId("idintable2A1").collapseAll();
		},

		onIntiateReconFullScreen: function (type) {
			this.byId("closebut").setVisible(type === "open");
			this.byId("openbut").setVisible(type !== "open");
			this.byId("oninreconTab1").setFullScreen(type === "open");
			this.byId("idintable2A1").setVisibleRowCount(type === "open" ? 23 : 11);
		},

		onIncrementDataFullScreen: function (type) {
			this.byId("closebutIn").setVisible(type === "open");
			this.byId("openbutIn").setVisible(type !== "open");
			this.byId("onincrementTab1").setFullScreen(type === "open");
			this.byId("idintable2A").setVisibleRowCount(type === "open" ? 23 : 11);
		},

		AutoLockChange: function (oEvent) {
			var oContext = oEvent.getSource().getBindingContext("onboarding"),
				oData = oContext.getObject();
			if (oData.AutoLock === "Yes" || oData.AutoLock === "3B Lock") {
				oData.ReverseFeed = "Yes";
			}
			oContext.getModel().refresh(true);
		},

		onSearchGstins1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("idInitiateReconList2A");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("idInitiateReconList2A").removeSelections();
			this.byId("checkboxID").setSelected(false);
			/*this.onInitiateRecon();*/
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				this.getView().byId("idInitiateReconList2A").selectAll();
				this.onSelectionChange1();
			}
			//this.onInitiateRecon();
			var oInitiateRecon1 = new JSONModel([]);
			this.getView().setModel(oInitiateRecon1, "InitiateRecon2A");
		},

		onRepDownload: function () {
			var oPath = this.getView().byId("idInitiateReconList2A").getSelectedItems(),
				oModelData = this.getView().getModel("GSTIN2A").getProperty("/"),
				aGSTIN = [];

			oPath.forEach(function (item) {
				aGSTIN.push(item.getBindingContext("GSTIN2A").getProperty("gstin"));
			});

			var selKey = this.byId("idDateRange2A").getSelectedKey(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"recipientGstins": aGSTIN,
						//"gstin": aGSTIN,
						"toTaxPeriodPR": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
						"fromTaxPeriodPR": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
						"toTaxPeriod2A": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax12A1").getValue() : "",
						"fromTaxPeriod2A": selKey === "Tax Period" ? this.byId("idInitiateReconPeriodTax2A1").getValue() : "",
						"fromReconDate": selKey === "Recon Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
						"toReconDate": selKey === "Recon Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
						"criteria": this.byId("idCriteria").getSelectedKey()
					}
				};
			this.excelDownload(oPayload, "/aspsapapi/getAutoReconSummaryReport.do");
		},

		onChangeCriteria: function (oEvent) {
			var oModel = this.getView().getModel("Display");

			if (this.getView().byId("idCriteria").getSelectedKey() === "PRtaxperiod") {
				oModel.getData().PR = true;
				oModel.getData().A2 = false;
			} else if (this.getView().byId("idCriteria").getSelectedKey() === "2Ataxperiod") {
				oModel.getData().PR = false;
				oModel.getData().A2 = true;
			} else {
				oModel.getData().PR = true;
				oModel.getData().A2 = true;
			}
			oModel.refresh(true);
		},

		onRDownloadPress: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}
			var oIntiData = {
				"req": {
					"requestId": this.oReqId
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/generateReportFileDownloadIdWise.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "DownloadDocument");
					}
				}).fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "DownloadDocument");
				});
			});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},
		onDownloadPress: function (oEvt) {
			debugger;
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().requestId;
			// var vCatgory = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().reportCateg;
			this.onFragDownload(this.oReqId);
		},

		onFragDownloadAll: function (oReqId) {
			var oData = this.getView().getModel("DownloadDocument").getData();
			for (var i = 0; i < oData.length; i++) {
				this.onFragDownload(oData[i].requestId);
			}
		},

		onFragDownload: function (oReqId) {
			var oReqExcelPath = "/aspsapapi/downloadAutoReconReport.do?requestId=" + oReqId;
			library.URLHelper.redirect(oReqExcelPath, true);
		},

		onInfoIconPress: function (oEvt) {
			var oButton = oEvt.getSource();
			var oPopover = this.byId("statusPopover");
			if (!this._IMSInfoMessage) {
				this._oTrail = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.IMSInfoMessage", this);
				this.getView().addDependent(this._IMSInfoMessage);
			}
			oPopover.openBy(oButton);
		},

		onCloseIMSInfoMessage: function () {
			this.byId("statusPopover").close();
		},
	});
});