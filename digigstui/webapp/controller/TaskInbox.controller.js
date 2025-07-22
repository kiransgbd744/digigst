sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter"
], function (BaseController, Controller, JSONModel, MessageBox, Formatter) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.TaskInbox", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.TaskInbox
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("TaskInbox").attachPatternMatched(this._onRouteMatched, this);
			var oModel1 = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel1, "GSTR3B");
		},

		_onRouteMatched: function (oEvent) {
			this.getAllGSTIN();
		},
		getAllGSTIN: function () {

			var that = this;
			var postData = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getCheckerGstins.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//for (var i = 0; i < data.resp.checkerGstins.length; i++) {
					//data.resp.rettype1 = [];
					//data.resp.gstins1 = [];
					// dta.rettype = [];
					// data.gstins = [];
					// oData.respData.dataSecurity.gstin.unshift({
					// 	value: "All"
					// });
					//data.resp.checkerGstins
					// data.resp.checkerGstins.sort(function (a, b) {
					// 	return a.value.localeCompare(b.value);
					// });

					data.resp.checkerGstins.unshift({
						gstin: "All"
					});

					//	}
					that.getOwnerComponent().setModel(new JSONModel(data.resp), "Checkergstn");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
			//eslint-disable-line
			//	var aGSTIN = [];
			//var aRegGSTIN = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.gstin;
			//	this.getOwnerComponent().setModel(new JSONModel(aRegGSTIN), "InwardGSTIN");
		},
		onDateTypeChange: function () {

			var oDate = this.getView().byId("idDateType").getSelectedKey();
			if (oDate === "Tax Perioid") {
				this.getView().byId("RequestDateFr").setVisible(false);
				this.getView().byId("RequestDateToFilt").setVisible(false);
				this.getView().byId("TaxperiodFr").setVisible(true);
				this.getView().byId("TaxperiodTo").setVisible(true);

			} else if (oDate === "Document Date") {
				this.getView().byId("RequestDateFr").setVisible(true);
				this.getView().byId("RequestDateToFilt").setVisible(true);
				this.getView().byId("TaxperiodFr").setVisible(false);
				this.getView().byId("TaxperiodTo").setVisible(false);
			}

		},
		onSearchTaskInbox: function () {
			//"entityId":31,
			//   "retType":["GSTR1"],
			//   "reqDateFrom":"2022-07-08",
			//   "reqDateTo":"2022-07-08",
			//   "taxPeriodFrom":"072022",
			//   "taxPeriodTo":"072022",
			//   "gstins":["29AAAPH9357H000"]
			var that = this;
			var aGstin = this.getView().byId("GSTINEntityID").getSelectedKeys();
			var aRet = this.getView().byId("returnty").getSelectedKeys();

			// this.getView().byId("RequestTaxPeriodFrom").getValue(),
			// this.getView().byId("RequestTaxPeriodTo").getValue(),

			var oIntiData = {
				"req": {
					"entityId": $.sap.entityID,
					"reqDateFrom": this.getView().byId("RequestDateFrom").getValue(),
					"reqDateTo": this.getView().byId("RequestDateTo").getValue(),
					"taxPeriodFrom": this.getView().byId("RequestTaxPeriodFrom").getValue(),
					"taxPeriodTo": this.getView().byId("RequestTaxPeriodTo").getValue(),
					"retType": aRet,
					"gstins": aGstin
				}
			};
			var oDate = this.getView().byId("idDateType").getSelectedKey();
			if (oDate === "Tax Perioid") {
				oIntiData.req.reqDateFrom = "";
				oIntiData.req.reqDateTo = "";
			}
			if (oDate === "Document Date") {
				oIntiData.req.taxPeriodFrom = "";
				oIntiData.req.taxPeriodTo = "";
			}
			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/approvalCheckerRequestData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {

						if (typeof data.resp.requestInfo === 'object') {
							for (var i = 0; i < data.resp.requestInfo.length; i++) {
								data.resp.requestInfo[i].action = "";
							}
							var oRequestTabSum = new JSONModel(data);
							View.setModel(oRequestTabSum, "TaskTabSumarry");
							var oRequestApp = new JSONModel(data.resp.requestInfo);
							View.setModel(oRequestApp, "RequestCheckerStatus");
						} else {
							var oRequestTabSum = new JSONModel(data);
							View.setModel(oRequestTabSum, "TaskTabSumarry");
							var oRequestApp1 = new JSONModel([]);
							View.setModel(oRequestApp1, "RequestCheckerStatus");
							MessageBox.information(data.resp.requestInfo);
						}
					} else {
						var oRequestApp1 = new JSONModel([]);
						View.setModel(oRequestApp1, "RequestCheckerStatus");
					}
				}).fail(function (jqXHR, status, err) {
					var oRequestApp = new JSONModel([]);
					View.setModel(oRequestApp, "RequestCheckerStatus");
				});
			});

			//sap.m.MessageToast.show("test");

		},
		onSubmit: function () {
			var that = this;
			var aRequest = [];
			var vIndices = this.byId("RequestTabId").getSelectedIndices(),
				aData = this.byId("RequestTabId").getModel("RequestCheckerStatus").getData();
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Request", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < vIndices.length; i++) {
				aRequest.push({
					requestId: aData[vIndices[i]].requestId,
					action: aData[vIndices[i]].action,
					checkComnts: aData[vIndices[i]].commChecker,
					status: aData[vIndices[i]].status,
					actionTakenBy: aData[vIndices[i]].actionTakenBy
				});
			}

			var oIntiData = {
				"req": {
					"entityId": $.sap.entityID,
					"isSubmit": true,
					"submitInfo": aRequest
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/submitAndRevertCheckerResponse.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {

						MessageBox.information(data.resp.actionInfo);
						that.onSearchTaskInbox();
					}
				}).fail(function (jqXHR, status, err) {
					MessageBox.error("submitAndRevertCheckerResponse" + err);
				});
			});
			//	MessageBox.information("test");
			//submitAndRevertCheckerResponse
			// var oIntiData = {
			// 	"req": {
			// 		"entityId": $.sap.entityID,
			// 		"reqDateFrom": this.getView().byId("RequestDateFrom").getValue(),
			// 		"reqDateTo": this.getView().byId("RequestDateTo").getValue(),
			// 		"taxPeriodFrom": this.getView().byId("RequestTaxPeriodFrom").getValue(),
			// 		"taxPeriodTo": this.getView().byId("RequestTaxPeriodTo").getValue(),
			// 		"retType": aRet,
			// 		"gstins": aGstin
			// 	}
			// };
			// 	var Model = new JSONModel();
			// var View = this.getView();
			// var oIniPath = "/aspsapapi/approvalCheckerRequestData.do";
			// $(document).ready(function ($) {
			// 	$.ajax({
			// 		method: "POST",
			// 		url: oIniPath,
			// 		contentType: "application/json",
			// 		data: JSON.stringify(oIntiData)
			// 	}).done(function (data, status, jqXHR) {
			// 		if (data.hdr.status === "S") {

			// 			if (typeof data.resp.requestInfo === 'object') {
			// 				var oRequestTabSum = new JSONModel(data);
			// 				View.setModel(oRequestTabSum, "TaskTabSumarry");
			// 				var oRequestApp = new JSONModel(data.resp.requestInfo);
			// 				View.setModel(oRequestApp, "RequestCheckerStatus");
			// 			} else {
			// 				var oRequestApp1 = new JSONModel([]);
			// 				View.setModel(oRequestApp1, "RequestCheckerStatus");
			// 				MessageBox.information(data.resp.requestInfo);
			// 			}
			// 		} else {
			// 			var oRequestApp1 = new JSONModel([]);
			// 			View.setModel(oRequestApp1, "RequestCheckerStatus");
			// 		}
			// 	}).fail(function (jqXHR, status, err) {
			// 		var oRequestApp = new JSONModel([]);
			// 		View.setModel(oRequestApp, "RequestCheckerStatus");
			// 	});
			// });

		},
		onRevertApproval: function () {
			var that = this;
			var aRequest = [];
			var aRejRequest = [];
			var vIndices = this.byId("RequestTabId").getSelectedIndices(),
				aData = this.byId("RequestTabId").getModel("RequestCheckerStatus").getData();
			if (vIndices.length === 0) {
				sap.m.MessageBox.information("Please select atleast one Request", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < vIndices.length; i++) {
				if (aData[vIndices[i]].status === "Approved") {
					aRequest.push({
						requestId: aData[vIndices[i]].requestId,
						action: aData[vIndices[i]].action,
						checkComnts: aData[vIndices[i]].commChecker,
						status: aData[vIndices[i]].status,
						actionTakenBy: aData[vIndices[i]].actionTakenBy
					});
				} else {
					aRejRequest.push(aData[vIndices[i]].requestId);
				}

			}
			var flag = 0;
			for (var j = 0; j < aRejRequest.length; j++) {
				if (flag === j) {
					var str = aRejRequest[j];
				} else {
					var str = str + "," + aRejRequest[i];
				}

			}

			if (str) {
				MessageBox.information("Selected Request id's Status should have Approved"+ str);
				return;
			}

			// if(aRequest.length ===0){
			// 	MessageBox.information("Selected Request Status should be Approved");
			// 	return;
			// }

			var oIntiData = {
				"req": {
					"entityId": $.sap.entityID,
					"isSubmit": false,
					"submitInfo": aRequest
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/submitAndRevertCheckerResponse.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						MessageBox.information(data.resp.actionInfo);
						that.onSearchTaskInbox();
					}
				}).fail(function (jqXHR, status, err) {
					MessageBox.error("submitAndRevertCheckerResponse" + err);
				});
			});

		},

		onAfterRendering: function () {

				this.getView().byId("RequestDateTo").setDateValue(new Date());
				var date = new Date();
				var toDate = date.setDate(date.getDate() - 10);
				var ntoDate = new Date(toDate);
				this.getView().byId("RequestDateFrom").setDateValue(ntoDate);
				this.getView().byId("RequestTaxPeriodFrom").setDateValue(new Date());
				this.getView().byId("RequestTaxPeriodTo").setDateValue(new Date());

				//this.getView().byId("RequestDateToFilt").setDateValue(new Date() - 10);

			}
			/**
			 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
			 * (NOT before the first rendering! onInit() is used for that one!).
			 * @memberOf com.ey.digigst.view.TaskInbox
			 */
			//	onBeforeRendering: function() {
			//
			//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.TaskInbox
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.TaskInbox
		 */
		//	onExit: function() {
		//
		//	}

	});

});