sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (Controller, BaseController, JSONModel, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Gstr1PRvsEINV", {
		onInit: function () {
			var vDate = new Date();
			this.byId("GLtaxDate").setMaxDate(vDate);
			this.byId("GLtaxDate").setDateValue(vDate);

			this.byId("GLtaxDate").addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);
			this._bindFilterData();
			this.onLoadGstnList();
		},

		_bindFilterData: function () {
			var date = new Date();
			this.byId("id_RequestIDpage").setModel(new JSONModel({
				"frTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"status": ""
			}), "FilterModel");
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"screenName": "gstr1Einv"
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
						this.byId("id_RequestIDpage").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onBeforeRendering: function () {
			this.byId("id_PRvsEINVpage").setVisible(true);
			this.byId("id_RequestIDpage").setVisible(false);
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onLoadGstnList();
				this._getUserId();
			}
		},

		onPressRequestIDwise: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(true);
			this.getView().byId("id_PRvsEINVpage").setVisible(false);
			this.onSearchReqIdWise();
		},

		onPressRequestIDwiseBack: function (oEvent) {
			this.getView().byId("id_RequestIDpage").setVisible(false);
			this.getView().byId("id_PRvsEINVpage").setVisible(true);
		},
		//======================= To Load GSTN List ========================================//
		onLoadGstnList: function () {
			var that = this;
			if ($.sap.entityID === "" && (this.byId("GLtaxDate").getValue() === null || this.byId("GLtaxDate").getValue() === "")) {
				return;
			}
			var aPayload = {
				"entityId": parseInt($.sap.entityID),
				"taxPeriod": this.byId("GLtaxDate").getValue()
			};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1EinvGstinsList.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.byId("idGstnList").setModel(new JSONModel(data.resp), "GSTINEINV");
					that.byId("idGstnList").selectAll();
					//that.onLoadStatusTable();
					var oSelectedKey = that.byId("segmentID").getSelectedKey();
					if (oSelectedKey === "PreReconSummary") {
						that.onLoadPreTable();
					} else if (oSelectedKey === "PostReconSummary") {
						that.onLoadPostTable();
					} else if (oSelectedKey === "ResponseSummary") {
						that.onLoadRSTable();
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//=================== Request id wise api =================================================//
		onChangeDtReconReqId: function (oEvent) {
			var frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId("toReconTaxPriod").getDateValue();

			if (frDate > toDate) {
				this.byId("toReconTaxPriod").setDateValue(frDate);
			}
			this.byId("toReconTaxPriod").setMinDate(frDate);
		},

		onClearReqIdWise: function () {
			this._bindFilterData();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function () {
			var oFilterData = this.byId("id_RequestIDpage").getModel("FilterModel").getProperty("/"),
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
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr1EinvReportRequestStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.byId("id_RequestIDpage").setModel(new JSONModel(data.resp), "oRequestIDWise");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressGSTIN: function (oEvt) {
			var gstins = [];
			var that = this;
			var TabData = that.byId("id_RequestIDpage").getModel("oRequestIDWise").getData();
			var gstins = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().gstins;
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins);
			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},
		//================ initiate recon =======================//
		onPressInitiateRecon: function () {
			var oView = this.getView();
			var that = this;
			var aGSTIN = [];
			var oModelData = this.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = oView.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length == 0) {
				MessageBox.error("select GSTN data");
				return;
			}
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						var aPayload = {
							"entityId": parseInt($.sap.entityID),
							"gstins": aGSTIN,
							"taxPeriod": that.byId("GLtaxDate").getValue()
						};
						sap.ui.core.BusyIndicator.show(0);
						var GstnsList = "/aspsapapi/gstr1EinvInitiateMatching.do";
						$(document).ready(function ($) {
							$.ajax({
								method: "POST",
								url: GstnsList,
								contentType: "application/json",
								data: JSON.stringify(aPayload)
							}).done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.resp.status == "Success") {
									MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
										title: "Initiate Matching Successfully done",
										actions: [sap.m.MessageBox.Action.OK],
										onClose: function (oActionSuccess) {
											if (oActionSuccess === "OK") {
												that.onLoadStatusTable();
											}
										}
									});
								} else {
									MessageBox.information("Error while doing Initiate Matching");
								}
							}).fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
							});
						});
					}
				}
			});
		},

		onChangeSegment: function (oEvt) {
			var oSelectedKey = oEvt.getSource().getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.byId("idChart1").setVisible(true);
				this.byId("idChart2").setVisible(false);
				this.byId("idChart3").setVisible(false);
				this.onLoadPreTable();
			} else if (oSelectedKey === "PostReconSummary") {
				this.byId("idChart1").setVisible(false);
				this.byId("idChart2").setVisible(true);
				this.byId("idChart3").setVisible(false);
				this.onLoadPostTable();
			} else if (oSelectedKey === "ResponseSummary") {
				this.byId("idChart1").setVisible(false);
				this.byId("idChart2").setVisible(false);
				this.byId("idChart3").setVisible(true);
				this.onLoadRSTable();
			}
		},
		//==============================Loading  List of Status table ===================//
		onGoPress: function () {
			this.getView().byId("idGstnList").selectAll();
			this.getView().byId("checkboxID").setSelected(true);
			this.onLoadGstnList();
			//this.onLoadPreTable();
			var oSelectedKey = this.byId("segmentID").getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.onLoadPreTable();
			} else if (oSelectedKey === "PostReconSummary") {
				this.onLoadPostTable();
			} else if (oSelectedKey === "ResponseSummary") {
				this.onLoadStatusTable();
			}
		},

		onLoadPreTable: function () {
			var oView = this.getView();
			var that = this;
			var aGSTIN = [];
			//var oModelData = this.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = oView.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length === 0) {
				//that.byId("id_GstnStatusTab").setModel(new JSONModel([]), "oGstr1EinvStatusList");
				that.byId("idTab1").setModel(new JSONModel([]), "PreReconSummary");
				that.byId("idTab1").getModel("PreReconSummary").refresh(true);
				return;
			}
			var aPayload = {
				"req": {
					"entityId": parseInt($.sap.entityID),
					"gstins": aGSTIN,
					"taxPeriod": this.byId("GLtaxDate").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getPreReconSumm.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//that.byId("id_GstnStatusTab").setModel(new JSONModel(data.resp), "oGstr1EinvStatusList");
					if (data.hdr.status === "S") {
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.preSummary.length; i++) {
							var ele = data.resp.preSummary[i];
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
						var oGSTIN = new JSONModel(retArr);
						that.byId("idTab1").setModel(oGSTIN, "PreReconSummary");
						//oView.setModel(oGSTIN, "PreReconSummary");
					} else {
						that.byId("idTab1").setModel(new JSONModel([]), "PreReconSummary");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onLoadPostTable: function () {
			var oView = this.getView();
			var that = this;
			var aGSTIN = [];
			//var oModelData = this.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = oView.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length === 0) {
				//that.byId("id_GstnStatusTab").setModel(new JSONModel([]), "oGstr1EinvStatusList");
				that.byId("idTab2").setModel(new JSONModel([]), "PostReconSummary");
				that.byId("idTab2").getModel("PostReconSummary").refresh(true);
				return;
			}
			var aPayload = {
				"req": {
					"entityId": parseInt($.sap.entityID),
					"recipientGstins": aGSTIN,
					"taxPeriod": this.byId("GLtaxDate").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getPostReconSummary.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						//var Ndata = data.resp.postrecondata;
						/*var obj = {
							"section": "Total",
							"level": "L1",
							"einvcount": 0,
							"einvpercentage": 0,
							"einvtaxablevalue": 0,
							"einvtotaltax": 0,
							"salesRegcount": 0,
							"salesRegpercentage": 0,
							"salesRegtaxablevalue": 0,
							"salesRegtotaltax": 0
						};
						Ndata.push(obj);*/
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.postrecondata.length; i++) {
							var ele = data.resp.postrecondata[i];
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

						debugger;
						var number = 0,
							number1 = 0,
							number2 = 0,
							number3 = 0,
							number4 = 0,
							number5 = 0,
							number6 = 0,
							number7 = 0;
						for (var j = 0; j < retArr.length; j++) {
							number += Number(retArr[j].einvcount);
							number1 += Number(retArr[j].einvpercentage);
							number2 += Number(retArr[j].einvtaxablevalue);
							number3 += Number(retArr[j].einvtotaltax);
							number4 += Number(retArr[j].salesRegcount);
							number5 += Number(retArr[j].salesRegpercentage);
							number6 += Number(retArr[j].salesRegtaxablevalue);
							number7 += Number(retArr[j].salesRegtotaltax);
							if (j === retArr.length - 1) {
								var obj = {
									"section": "Total",
									"level": "L1",
									"einvcount": number,
									"einvpercentage": number1.toFixed(2),
									"einvtaxablevalue": number2,
									"einvtotaltax": number3,
									"salesRegcount": number4,
									"salesRegpercentage": number5.toFixed(2),
									"salesRegtaxablevalue": number6,
									"salesRegtotaltax": number7
								};
							}
						}

						retArr.push(obj);
						var oGSTIN = new JSONModel(retArr);
						that.byId("idTab2").setModel(oGSTIN, "PostReconSummary");
						//oView.setModel(oGSTIN, "PreReconSummary");
					} else {
						that.byId("idTab2").setModel(new JSONModel([]), "PostReconSummary");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onLoadRSTable: function () {
			var oView = this.getView();
			var that = this;
			var aGSTIN = [];
			//var oModelData = this.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = oView.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length === 0) {
				//that.byId("id_GstnStatusTab").setModel(new JSONModel([]), "oGstr1EinvStatusList");
				that.byId("idTab3").setModel(new JSONModel([]), "ResponseSummary");
				that.byId("idTab3").getModel("ResponseSummary").refresh(true);
				return;
			}
			var aPayload = {
				"req": {
					"gstins": aGSTIN,
					"taxPeriod": this.byId("GLtaxDate").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getResponseSumm.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						//that.byId("id_GstnStatusTab").setModel(new JSONModel(data.resp), "oGstr1EinvStatusList");
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.respSummary.length; i++) {
							var ele = data.resp.respSummary[i];
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
						var oGSTIN = new JSONModel(retArr);
						that.byId("idTab3").setModel(oGSTIN, "ResponseSummary");
						//oView.setModel(oGSTIN, "PreReconSummary");
					} else {
						that.byId("idTab3").setModel(new JSONModel([]), "ResponseSummary");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onExpandFirstLevel1: function () {
			var oSelectedKey = this.byId("segmentID").getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.byId("idTab1").expandToLevel(1);
			} else if (oSelectedKey === "PostReconSummary") {
				this.byId("idTab2").expandToLevel(1);
			} else if (oSelectedKey === "ResponseSummary") {
				this.byId("idTab3").expandToLevel(1);
			}
		},

		onCollapseAll1: function () {
			var oSelectedKey = this.byId("segmentID").getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.byId("idTab1").collapseAll();
			} else if (oSelectedKey === "PostReconSummary") {
				this.byId("idTab2").collapseAll();
			} else if (oSelectedKey === "ResponseSummary") {
				this.byId("idTab3").collapseAll();
			}
		},

		onIntiateReconFullScreen: function (oEvt) {
			//var data = this.getView().getModel("InitiateRecon2A").getData()[0].items;
			var oSelectedKey = this.byId("segmentID").getSelectedKey(),
				id, id1, id2, id3, count;
			if (oSelectedKey === "PreReconSummary") {
				id = "idChart1";
				id1 = "idTab1";
				id2 = "closebut";
				id3 = "openbut";
				count = 18;
			} else if (oSelectedKey === "PostReconSummary") {
				id = "idChart2";
				id1 = "idTab2";
				id2 = "closebut1";
				id3 = "openbut1";
				count = 18;
			} else if (oSelectedKey === "ResponseSummary") {
				id = "idChart3";
				id1 = "idTab3";
				id2 = "closebut2";
				id3 = "openbut2";
				count = 8;
			}
			if (oEvt === "open") {
				this.byId(id2).setVisible(true);
				this.byId(id3).setVisible(false);
				this.byId(id).setFullScreen(true);
				this.byId(id1).setVisibleRowCount(22);
			} else {
				this.byId(id2).setVisible(false);
				this.byId(id3).setVisible(true);
				this.byId(id).setFullScreen(false);
				this.byId(id1).setVisibleRowCount(count);
			}
		},

		onLoadStatusTable: function () {
			var oSelectedKey = this.byId("segmentID").getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.onLoadPreTable();
			} else if (oSelectedKey === "PostReconSummary") {
				this.onLoadPostTable();
			} else if (oSelectedKey === "ResponseSummary") {
				this.onLoadRSTable();
			}
			/*var oView = this.getView();
			var that = this;
			var aGSTIN = [];
			var oModelData = this.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = oView.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length == 0) {
				that.byId("id_GstnStatusTab").setModel(new JSONModel([]), "oGstr1EinvStatusList");
				that.byId("id_GstnStatusTab").getModel("oGstr1EinvStatusList").refresh(true);
				return;
			}
			var aPayload = {
				"gstins": aGSTIN,
				"taxPeriod": this.byId("GLtaxDate").getValue()
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr1EinvStatusList.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.byId("id_GstnStatusTab").setModel(new JSONModel(data.resp), "oGstr1EinvStatusList");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});*/
		},
		onSelectallGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idGstnList").removeSelections();
			} else {
				this.getView().byId("idGstnList").selectAll();
			}
			//this.onLoadStatusTable();
			var oSelectedKey = this.byId("segmentID").getSelectedKey();
			if (oSelectedKey === "PreReconSummary") {
				this.onLoadPreTable();
			} else if (oSelectedKey === "PostReconSummary") {
				this.onLoadPostTable();
			} else if (oSelectedKey === "ResponseSummary") {
				this.onLoadRSTable();
			}
		},
		//============== Search for GSTN list =====================//
		onSearchGstins1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("idGstnList");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("idGstnList").removeSelections();
			this.byId("checkboxID").setSelected(false);
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				this.getView().byId("idGstnList").selectAll();
				//this.onLoadStatusTable();
				var oSelectedKey = this.byId("segmentID").getSelectedKey();
				if (oSelectedKey === "PreReconSummary") {
					this.onLoadPreTable();
				} else if (oSelectedKey === "PostReconSummary") {
					this.onLoadPostTable();
				} else if (oSelectedKey === "ResponseSummary") {
					this.onLoadRSTable();
				}
			}
			//this.onInitiateRecon();
			// var oInitiateRecon1 = new sap.ui.model.json.JSONModel([]);
			// this.getView().setModel(oInitiateRecon1, "InitiateRecon2A");
		},
		//================= Download Report in request id wise =======================//
		onConfigExtractPress2A1: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().requestId;
			this.reconType = oEvt.getSource().getEventingParent().getParent().getBindingContext("oRequestIDWise").getObject().reconType;
			/*var oReqExcelPath = "/aspsapapi/gstr2DownloadDocument.do?configId=" + this.oReqId + "";
			window.open(oReqExcelPath);*/
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			var oIntiData = {
				"req": {
					"configId": this.oReqId,
					"reconType": this.reconType
				}
			};
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/gstr1EinvDownloadIdWise.do";
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

		onFragDownload: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("DownloadDocument").getObject(),
				request = {
					"req": {
						"filePath": obj.path,
						"docId": obj.docId
					}
				};

			this.excelDownload(request, "/aspsapapi/gstr1EinvfiledownloadDoc.do");
		},
		onRefreshRequestIDwise2A: function () {
			this.onSearchReqIdWise();
		},
		//===================== Response Pop up ============================//
		onPressResponse: function () {
			// if (!this.oResponseFrag) {
			this.oResponseFrag = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr1.ReconResponse", this);
			this.getView().addDependent(this.oResponseFrag);
			//}
			this.oResponseFrag.open();
			var Array = {
				"addlGSTINForR1": "",
				"notAvbinDigiGSTR10": "",
				"avbinDigiGST": "",
				"addlDigiGST": "",
				"notAvbinDigiGSTR1": "",
				"delfailedStatus": ""
			};
			// var Array = {};
			this.getView().setModel(new JSONModel(Array), "oRespModel");
		},
		onChangeResponse: function (oEvent) {
			if (oEvent.getParameter("selectedItem")) {
				var key = oEvent.getParameter("selectedItem").getKey();
				oEvent.getSource().setSelectedKey(key);
			}
		},
		onPressResetResp: function () {
			var Array = {
				"addlGSTINForR1": "",
				"notAvbinDigiGSTR10": "",
				"avbinDigiGST": "",
				"addlDigiGST": "",
				"notAvbinDigiGSTR1": "",
				"delfailedStatus": ""
			};
			this.getView().setModel(new JSONModel(Array), "oRespModel");
			this.getView().getModel("oRespModel").refresh(true);
		},
		onPressRespCancel: function () {
			this.oResponseFrag.destroy();
		},
		onPressSubmitResp: function () {
			var that = this;
			var View = this.getView();
			var aGSTIN = [];
			var oModelData = that.byId("idGstnList").getModel("GSTINEINV").getData();
			var oPath = View.byId("idGstnList").getSelectedContexts();
			for (var i = 0; i < oPath.length; i++) {
				var object = this.byId("idGstnList").getModel("GSTINEINV").getObject(oPath[i].sPath);
				aGSTIN.push(object.gstin);
			}
			if (aGSTIN.length == 0) {
				MessageBox.error("Please select atleast one GSTN to response");
				return;
			}
			if (that.getView().getModel("oRespModel").getData().addlGSTINForR1 == "" &&
				that.getView().getModel("oRespModel").getData().avbinDigiGST == "" &&
				that.getView().getModel("oRespModel").getData().addlDigiGST == "" &&
				that.getView().getModel("oRespModel").getData().notAvbinDigiGSTR1 == "" &&
				that.getView().getModel("oRespModel").getData().delfailedStatus == "") {
				MessageBox.error("Please select atleast one response");
				return;
			}
			MessageBox.confirm("Do you want to continue with the selected responses?", {
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oActionSuccess) {
					if (oActionSuccess == "YES") {
						that.getView().getModel("oRespModel").getData();
						var aPayload = {
							"req": {
								"gstins": aGSTIN,
								"addlGSTINForR1": that.getView().getModel("oRespModel").getData().addlGSTINForR1,
								"notAvbinDigiGSTR10": that.getView().getModel("oRespModel").getData().notAvbinDigiGSTR10,
								"avbinDigiGST": that.getView().getModel("oRespModel").getData().avbinDigiGST,
								"addlDigiGST": that.getView().getModel("oRespModel").getData().addlDigiGST,
								"notAvbinDigiGSTR1": that.getView().getModel("oRespModel").getData().notAvbinDigiGSTR1,
								"delfailedStatus": that.getView().getModel("oRespModel").getData().delfailedStatus,
								"returnPeriod": that.byId("GLtaxDate").getValue()
							}
						};
						sap.ui.core.BusyIndicator.show(0);
						var oIniPath = "/aspsapapi/submitEinvSummaryResp.do";
						$(document).ready(function ($) {
							$.ajax({
								method: "POST",
								url: oIniPath,
								contentType: "application/json",
								data: JSON.stringify(aPayload)
							}).done(function (data, status, jqXHR) {
								// that.oResponseFrag.destroy();
								// that.onLoadStatusTable();
								sap.ui.core.BusyIndicator.hide();
								var data1 = JSON.parse(data);
								if (data1.hdr.status == "S") {
									MessageBox.success(data1.resp);
									that.oResponseFrag.destroy();
									that.onLoadStatusTable();
								} else {
									MessageBox.error(data1.resp);
									that.oResponseFrag.destroy();
									that.onLoadStatusTable();
								}
							}).fail(function (jqXHR, status, err) {
								sap.ui.core.BusyIndicator.hide();
								var oGSTIN12 = new JSONModel([]);
								View.setModel(oGSTIN12, "DownloadDocument");
							});
						});
					}
				}
			});
		},

		//======== Download summary reports ========//
		onDownloadReport: function (oEvent) {
			var oModel = this.byId("idGstnList").getModel("GSTINEINV"),
				oPath = this.getView().byId("idGstnList").getSelectedContextPaths(),
				vKey = oEvent.getParameter("item").getKey(),
				aGSTIN = oPath.map(function (path) {
					return oModel.getObject(path + "/gstin");
				});
			if (aGSTIN.length == 0) {
				MessageBox.error("Select GSTIN to download reports");
				return;
			}
			if (vKey === "C") {
				var payload = {
					"req": {
						"taxPeriod": this.byId("GLtaxDate").getValue(),
						"gstins": aGSTIN
					}
				};
				this.reportDownload(payload, "/aspsapapi/downloadEinvConsolidatedReport.do");
			}
		},

		onDownloadSummary: function (oEvent) {
			var oModel = this.byId("idGstnList").getModel("GSTINEINV"),
				aPath = this.getView().byId("idGstnList").getSelectedContextPaths(),
				selectedKey = this.byId("segmentID").getSelectedKey(),
				aGSTIN = aPath.map(function (path) {
					return oModel.getObject(path + "/gstin");
				});

			if (aGSTIN.length == 0) {
				MessageBox.error("Select GSTIN to download reports");
				return;
			}
			var payload = {
				"req": {
					"entityId": +$.sap.entityID,
					"taxPeriod": this.byId("GLtaxDate").getValue(),
					"gstins": (selectedKey !== 'PostReconSummary' ? aGSTIN : undefined),
					"recipientGstins": (selectedKey === 'PostReconSummary' ? aGSTIN : undefined)
				}
			};

			switch (selectedKey) {
			case "PostReconSummary":
				this.excelDownload(payload, "/aspsapapi/downloadPostReconReport.do");
				break;
			case "PreReconSummary":
				this.excelDownload(payload, "/aspsapapi/downloadPreReconReport.do");
				break;
			case "ResponseSummary":
				this.excelDownload(payload, "/aspsapapi/downloadRespReconReport.do");
				break;
			}
		},

		excelDownload: function (reqData, url) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
				method: "POST",
				url: url,
				dataType: "binary",
				processData: false,
				contentType: "application/json",
				data: JSON.stringify(reqData)
			}).done(function (e, t, a) {
				sap.ui.core.BusyIndicator.hide();
				if (e.size < 50) {
					sap.m.MessageBox.information("No Records Found", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				} else if (e.size < 100) {
					sap.m.MessageBox.information("No Records Found", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				var o = new Blob([e], {
					type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
				});
				var r = "";
				var i = a.getResponseHeader("Content-Disposition");
				if (i) {
					var s = /filename[^;=\n]*=(.*)/;
					var l = s.exec(i);
					if (l !== null && l[1]) {
						r = l[1].replace(/['"]/g, "");
					}
				}
				if (!r) {
					r = "MyFileName.xlsx";
				}
				var d = document.createElement("a");
				d.href = window.URL.createObjectURL(o);
				d.download = r;
				d.setAttribute("visibility", "hidden");
				document.body.appendChild(d); //eslint-disable-line
				d.click();
			}).fail(function (e, t, a) {
				sap.ui.core.BusyIndicator.hide();
			});
		}
	});
});