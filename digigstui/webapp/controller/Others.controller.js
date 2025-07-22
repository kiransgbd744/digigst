sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/routing/HashChanger",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/core/util/Export",
	"com/ey/digigst/util/others/Reversal",
	"sap/ui/core/util/ExportTypeCSV"
], function (BaseController, Formatter, JSONModel, HashChanger, MessageBox, MessageToast, Export, Reversal, ExportTypeCSV) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Others", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Others
		 */
		onInit: function () {
			var that = this;
			var vDate = new Date();
			this.byId("dtRetFSts").setMaxDate(vDate);
			this.byId("dtRetFSts").setDateValue(vDate);

			this.byId("dtSRetPrd").setMaxDate(vDate);
			this.byId("dtSRetPrd").setDateValue(vDate);

			// var vDate = new Date();
			// this.byId("idTaxperiodCrrev").setDateValue(vDate);
			// this.gstinData();
			this.getOwnerComponent().getRouter().getRoute("Others").attachPatternMatched(this._onRouteMatched, this);
			// var oShow = {
			// 	"ratio1": true,
			// 	"ratio2": true,
			// 	"ratio3": true,
			// 	"enabledRatio1": false,
			// 	"enableRatio2": false,
			// 	"enableRatio3": false,
			// 	"check": false
			// };
			// var oModel = new sap.ui.model.json.JSONModel(oShow);
			// this.getView().setModel(oModel, "showing");
			// this._getProcessedData();
			//var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			//this.getView().setModel(oModel, "NCV");
		},

		TableBinding: function () {
			// 			var that = this;
			// 			var EWBTableModel = new JSONModel();
			// 			var EWBTableView = this.getView(),

			// 			 getEWBRequestStatusSummary = "/aspsapapi/getEWBRequestStatusSummaryFilter.do",
			// 			          {
			//                   "req" : {
			//                   "entityId": 32,
			//                     "gstins": [
			//                   "29AAAPH9357H000"
			//                       ],
			//                       "criteria":"BillDate",
			//                       "fromdate":"2021-12-01",
			//                         "toDate":"2022-12-08"
			//     }
			// }

			// 			      sap.ui.core.BusyIndicator.show(0);
			// 			$(document).ready(function ($) {
			// 				$.ajax({
			// 					method: "POST",
			// 					url: getEWBRequestStatusSummary,
			// 					contentType: "application/json",
			// 					data: JSON.stringify()
			// 				}).done(function (data, status, jqXHR) {
			// 					sap.ui.core.BusyIndicator.hide();
			// 					EWBTableModel.setData(data);
			// 					EWBTableView.setModel(EWBTableModel, "GSTR3B");
			// 				}).fail(function (jqXHR, status, err) {
			// 					sap.ui.core.BusyIndicator.hide();
			// 				});
			// 			});

			var EWBInfo = {
				"req": {
					"entityId": 32,
					"gstins": [
						"29AAAPH9357H000"
					],
					"criteria": "BillDate",
					"fromdate": "2021-12-01",
					"toDate": "2022-12-08"
				}
			};
			var EWBTableModel = new JSONModel();
			var EWBTableView = this.getView();
			var getEWBRequestStatusSummary = "/aspsapapi/getEWBRequestStatusSummaryFilter.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: getEWBRequestStatusSummary,
					contentType: "application/json",
					data: JSON.stringify(EWBInfo)
				}).done(function (data, status, jqXHR) {
					that.apiLimt();
					var oGstnSrNo = data.resp.det;
					for (var j = 0; j < oGstnSrNo.length; j++) {
						oGstnSrNo[j].SrNum = j + 1;
					}
					EWBTableModel.setData(data);
					EWBTableView.setModel(EWBTableModel, "GSTR3B");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		_onRouteMatched: function (oEvt) {
			var oHashChanger = this.getRouter().getHashChanger(),
				contextPath = oEvt.getParameter("arguments").contextPath;
			oHashChanger.setHash("Others/" + contextPath);

			this.gstinData();
			this.onLoadFy();
			this.apiLimt();
			var key = oEvt.getParameter("arguments").contextPath;
			this.byId("iditbReports").setSelectedKey(key);
			this.handleIconTabBarSelect1(key);

			// this.byId("iditbReports").setSelectedKey(key);
			// if (this.getOwnerComponent().getModel("userPermission").getData().appPermission.P11) {
			// 	this.byId("iditbReports").setSelectedKey("B2CDynamicQRNew");
			// } else {

			// 	if (key === "ManageAuthToken") {
			// 		this._getProcessedData();
			// 	} else if (key === "GSTINValidator") {
			// 		this.GstnValidtr();
			// 	}
			// 	// else if (key === "Creditreversal") {
			// 	// 	this.getCreditRevProcessData();
			// 	// 	// this.getCreditRevData();
			// 	// 	// this.getCreditTurnOverData();

			// 	// } 
			// 	else {
			// 		this.byId("sbManagevendor").setSelectedKey("vendor");
			// 		this.CPFilingSt();
			// 	}
			// 	this.byId("iditbReports").setSelectedKey("ManageAuthToken"); //==== Added by chaithra on 23/3/2021 ====//
			// }
		},

		apiLimt: function () {
			//this.financialYear = postData.req.financialYear
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: "/aspsapapi/getLimitAndUsageCount.do",
					contentType: "application/json"
						//data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//data = JSON.parse(data);
					var oComplienceHistory = new sap.ui.model.json.JSONModel(data.resp);
					oView.setModel(oComplienceHistory, "ApiLimit");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("complienceHistory : Error");
					var oComplienceHistory = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oComplienceHistory, "ComplienceHistory");

				});
			});
		},

		//=============== Fy Model Binding ===========================//
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

		handleIconTabBarSelect: function (oEvent) {
			var sTabId = oEvent.getSource().getSelectedKey(),
				oHashChanger = HashChanger.getInstance();

			oHashChanger.setHash("Others/" + sTabId);
			this.handleIconTabBarSelect1(sTabId);
		},

		handleIconTabBarSelect1: function (sTabId) {
			// this.apiLimt(); // commented for removing duplicate api call on 12.05.2023
			switch (sTabId) {
			case "GSTINValidator":
				this.GstnValidtr();
				break;
			case "VenRetFilngStatus":
				this.CPFilingSt();
				break;
			case "ManageAuthToken":
				this._getProcessedData();
				break;
			case "Creditreversal":
				// this.getCreditRevProcessData();
				break;
			case "NonCompliantVendors":
				break;
			}
		},

		gstinData: function () {
			var oReqWiseModel = new JSONModel();
			var that = this,
				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID]
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstins.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				data.resp.unshift({
					gstin: "All"
				});
				oReqWiseModel.setSizeLimit(data.resp.length);
				sap.ui.core.BusyIndicator.hide();
				oReqWiseModel.setData(data.resp);
				that.getView().setModel(oReqWiseModel, "gstins");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onAfterRendering: function () {
			// this.getCreditRevProcessData();
			// this.getCreditRevData();
			// this.getCreditTurnOverData();
			// 
		},

		/*=====================================================================================*/
		/*======== Manage Auth Token ==========================================================*/
		/*=====================================================================================*/
		/**
		 * Method Called to Get Auth Token list
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		onSearchAuthToken: function () {
			this._getProcessedData();
		},

		/**
		 * Method called to filter data based on Active/Inactive
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onFilterChange: function (oEvent) {
			var Status = oEvent.getSource().getSelectedKey();
			var oFilter = [];
			if (Status !== "") {
				oFilter.push(new sap.ui.model.Filter({
					path: "status",
					operator: "EQ",
					value1: Status
				}));
			}
			this.getView().byId("idtablemanageauth").getBinding("rows").filter(oFilter);
		},

		// _getProcessSearchInfo: function () {
		// 	/*var aEntity = [];
		// 	aEntity.push(this.byId("slEntity").getSelectedKey());*/
		// 	var searchInfo = {
		// 		"req": {
		// 			"entityId": [$.sap.entityID]
		// 		}
		// 	};
		// 	return searchInfo;
		// },

		/** 
		 * Called to get AuthToken data
		 * Developed by: Jakeer Syed - 06.11.2019
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		_getProcessedData: function () {
			// debugger; //eslint-disable-line
			var that = this,
				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID]
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAuthTokenDetails.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("idtablemanageauth").setModel(new JSONModel(data.resp), "AuthtokenRecord");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method called to open Auth Token Generate Confirmation Dialog
		 * Modified by: Bharat Gupta on 17.04.2020
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		openAuthDialogGenerate: function (oEvent) {
			// debugger; //eslint-disable-line
			var vGstin = oEvent.getSource().getBindingContext("AuthtokenRecord").getObject().gstin;

			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": vGstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
			// if (!this.oDialog) {
			// 	this.oDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.others.AuthToken", this);
			// 	this.getView().addDependent(this.oDialog);
			// }
			// this.oDialog.open();
			// this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
			// 	"gstin": vGstin
			// }), "AuthTokenGstin");

			// var value = [vGstin];
			// this.oValue1 = value;
		},

		/**
		 * Method called to close Auth Token Generate Confirmation Dialog
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		onPressCancel: function () {
			this._dAuthToken.close();
		},

		/**
		 * Method called to Generate OTP to activate Auto Token
		 * Modified by: Bharat Gupta on 17.04.2020
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		onPressYes: function () {
			// debugger; //eslint-disable-line
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
			}).done(function (data, status, jqXHR) {
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
			}).fail(function (jqXHR, status, err) {
				that.byId("dAuthTokenConfirmation").setBusy(false);
			});
		},

		// _getEntitySearchInfo: function (gstin) {
		// 	var searchInfo = {
		// 		"req": {
		// 			"gstin": gstin
		// 		}
		// 	};
		// 	return searchInfo;
		// },
		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		/**
		 * Method called to resend OTP
		 * Developed by: Bharat Gupta on 17.04.2020
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		onPressResendOTP: function () {
			// debugger; //eslint-disable-line
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
			}).done(function (data, status, jqXHR) {
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
			}).fail(function (jqXHR, status, err) {
				that.byId("dVerifyAuthToken").setBusy(false);
			});
		},

		/**
		 * Method called to validate OTP
		 * Modified by: Bharat Gupta on 17.04.2020
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		validateOTP: function (oEvent) {
			// debugger; //eslint-disable-line
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

		/**
		 * Method Called to verify OTP
		 * Modified by: Bharat Gupta on 17.04.2020
		 * @memberOf com.ey.digigst.view.Others
		 * @private
		 */
		onPressVerifyOTP: function () {
			// debugger; //eslint-disable-line
			// var otpvalue = sap.ui.getCore().byId("idotp").getValue();
			// var oValue1 = this.oValue1[0];
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
			}).done(function (data, status, jqXHR) {
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
			}).fail(function (jqXHR, status, err) {
				that.byId("dVerifyAuthToken").setBusy(false);
				that._dGenerateOTP.close();
			});
			// sap.ui.getCore().byId("idotp").destroy();
			//	sap.ui.getCore().GenerateOTP.close();
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Others
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Others
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Others
		 */
		//	onExit: function() {
		//
		//	}

		/////////###############################################  BOC by Rakesh #####################################################
		//////########################  Tab- GSTIN Validator   #######################
		onChangeSegment: function (oEvt) {
			var oSelIconKey = oEvt.getSource().getSelectedKey();
			if (oSelIconKey === "SearchByGSTIN") {
				this.getView().byId("gstinId").setVisible(true);
				this.getView().byId("idtoolbargstn").setVisible(false);
				this.getView().byId("idblocklayoutgstn").setVisible(false);
			} else if (oSelIconKey === "SearchByPAN") {
				this.getView().byId("gstinId").setVisible(false);
				this.getView().byId("idtoolbargstn").setVisible(true);
				this.getView().byId("idblocklayoutgstn").setVisible(true);
			}
		},

		GstnValidtr: function () {
			var bulkValidInfo = {
				"req": {
					"user": "SYSTEM"
				}
			};
			var oBulkModel = new JSONModel();
			var oBulkView = this.getView();
			var BulkValidPath = "/aspsapapi/getTaxPayerConfigDetail.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: BulkValidPath,
					contentType: "application/json",
					data: JSON.stringify(bulkValidInfo)
				}).done(function (data, status, jqXHR) {
					that.apiLimt();
					var oGstnSrNo = data.resp.det;
					for (var j = 0; j < oGstnSrNo.length; j++) {
						oGstnSrNo[j].SrNum = j + 1;
					}
					oBulkModel.setData(data);
					oBulkView.setModel(oBulkModel, "BulkValidList");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onRefresh: function () {
			this.GstnValidtr();
		},

		//#############  File Download -> PAN/GSTIN Validator(Bulk Validation)
		onDownloadCatalog: function (oEvent) {
			var oReqId = this.getView().getModel("BulkValidList").getData().resp.det[oEvent.getSource().getParent().getIndex()].requestId;
			var oExclDwnldPath = "/aspsapapi/downloadGstinValidatorExcelDocument.do?requestId=" + oReqId + "";
			window.open(oExclDwnldPath);
		},

		//############# Table leve Excel File download -> PAN/GSTIN Validator
		onGSTINExclDwnld: function (oEvt) {
			sap.m.URLHelper.redirect("model/Data.xlsx", true);
			//window.open( "com.ey.digigst/webapp/model/Data.xls");
			/*var oModel = new sap.ui.model.json.JSONModel();
			var oTabData = this.getView().getModel("BulkValidList").getData().resp.det;
			oModel.setData(oTabData);

			var oExport = new Export({
				exportType: new ExportTypeCSV({
					separatorChar: "\t",
					mimeType: "application/vnd.ms-excel",
					charset: "utf-8",
					fileExtension: "xls"
				}),

				models: oModel,

				rows: {
					path: "/"
				},
				columns: [{
					name: "GSTIN",
					template: {
						content: " "
					}
				}]

			});
			// download exported file
			oExport.saveFile().catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});*/
		},

		//#############  File Upload -> PAN/GSTIN Validator(Bulk Validation)
		onUpload: function () {
			var oFileUploader = this.byId("ucWebUpload22");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}

			oFileUploader.setUploadUrl("/aspsapapi/gstinValidFileUpload.do");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var oRespnse = oEvent.getParameters().responseRaw;
			var status = JSON.parse(oRespnse);
			if (status.hdr.status === "S") {
				this.getView().byId("ucWebUpload22").setValue("");
				sap.m.MessageBox.success(status.resp, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(status.resp, {
					styleClass: "sapUiSizeCompact"
				});
			}
			/*var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("ucWebUpload22").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}*/
		},

		//############# Tab -> GSTIN Validator(Search)
		onSearchGSTN: function () {
			var that = this;
			var oGstin1 = this.byId("idpangstn").getValue();
			if (oGstin1 === "") {
				MessageToast.show("Please enter GSTIN Number");
				return;
			}

			var oSearhInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": this.byId("idpangstn").getValue()
				}
			};
			var oSearhModel = new JSONModel();
			var oSearhView = this.getView();
			var oSearhPath = "/aspsapapi/getTaxPayerDetails.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oSearhPath,
					contentType: "application/json",
					data: JSON.stringify(oSearhInfo)
				}).done(function (data, status, jqXHR) {
					that.apiLimt();
					if (data.hdr.status === "E") {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
					oSearhModel.setData(data.resp);
					oSearhView.setModel(oSearhModel, "SearchGstnData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//#################################### Tab ->  Vendor Return Filing Status   ##############################################
		onChangeSegmentReturn: function (oEvent) {
			this.apiLimt();
			var vId = oEvent.getSource().getSelectedKey();
			if (vId === "vendor") {
				this.getView().byId("vendorReturnId").setVisible(true);
				this.getView().byId("idheaderVendor").setVisible(false);
				this.getView().byId("idFileStatus").setVisible(false);
				//this.getView().byId("idToolbarSearch").setVisible(false);
				this.getView().byId("idlabel").setVisible(true);
				//this.getView().byId("idblocklayoutreturn").setVisible(false);
				this.getView().byId("idblocklayoutreturn").setVisible(false);

			} else if (vId === "customer") {
				this.getView().byId("idfilterbarvend1").setVisible(true);
				this.getView().byId("idfilterbarvend2").setVisible(false);
				this.getView().byId("vendorReturnId").setVisible(false);
				this.getView().byId("idheaderVendor").setVisible(true);
				this.getView().byId("idFileStatus").setVisible(true);
				//this.getView().byId("idToolbarSearch").setVisible(false);
				this.getView().byId("idlabel").setVisible(false);
				//this.getView().byId("idblocklayoutreturn").setVisible(false);
				this.getView().byId("idblocklayoutreturn").setVisible(false);

				//this.onMyFileStGo();

			} else if (vId === "search") {
				this.getView().byId("idfilterbarvend1").setVisible(false);
				this.getView().byId("idfilterbarvend2").setVisible(true);
				this.getView().byId("vendorReturnId").setVisible(false);
				this.getView().byId("idheaderVendor").setVisible(true);
				this.getView().byId("idFileStatus").setVisible(false);
				//this.getView().byId("idToolbarSearch").setVisible(true);
				this.getView().byId("idlabel").setVisible(false);
				//this.getView().byId("idblocklayoutreturn").setVisible(true);
				this.getView().byId("idblocklayoutreturn").setVisible(true);

				var retSearchInfo = {
					//"req": {
					"gstins": this.byId("idSerchGstin").getValue().split(","),
					"returnPeriod": this.getView().byId("dtSRetPrd").getValue(),
					"returnType": this.getView().byId("idcombotreturn").getSelectedKeys()
						//}
				};
				this._onSearchGoMdl(retSearchInfo);
			}
		},

		_onSearchGoMdl: function (retSearchInfo) {
			var oRetSearchMdl = new JSONModel();
			var oRetSearchView = this.getView();
			var RetSearchPath = "/aspsapapi/getReturnFilingSearchResults.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RetSearchPath,
					contentType: "application/json",
					data: JSON.stringify(retSearchInfo)
				}).done(function (data, status, jqXHR) {
					that.apiLimt();
					var oMyFileSrNo = data.resp.filingStatus;
					for (var i = 0; i < oMyFileSrNo.length; i++) {
						oMyFileSrNo[i].myFileSrNum = i + 1;
					}
					oRetSearchMdl.setData(data);
					oRetSearchView.setModel(oRetSearchMdl, "RetSearchTable");
				}).fail(function (jqXHR, status, err) {
					that.apiLimt();
				});
			});
		},

		onSearchGo: function () {
			var oGstn = this.byId("idSerchGstin").getValue().split(",");
			if (oGstn.length > 10) {
				MessageToast.show("Maximum 10 GSTINs allowed");
				return;
			}
			var retSearchInfo = {
				//"req": {
				"gstins": oGstn,
				"returnPeriod": this.getView().byId("dtSRetPrd").getValue(),
				"returnType": this.getView().byId("idcombotreturn").getSelectedKeys()
					//}
			};
			this._onSearchGoMdl(retSearchInfo);
		},

		onClearFilter: function () {
			this.getView().byId("idSerchGstin").setValue(null);
			this.getView().byId("dtSRetPrd").setValue(null);
			this.getView().byId("idcombotreturn").setSelectedKeys(null);
		},

		//############# Table leve Excel File download ->  Search(Vendor Filing Status)
		onVRFSExcel: function () {
			var oVRFSExcelModel = new sap.ui.model.json.JSONModel();
			var oSerTabData = this.getView().getModel("RetSearchTable").getData().resp.filingStatus;
			oVRFSExcelModel.setData(oSerTabData);

			var oExport = new Export({
				exportType: new ExportTypeCSV({
					separatorChar: "\t",
					mimeType: "application/vnd.ms-excel",
					charset: "utf-8",
					fileExtension: "xls"
				}),

				models: oVRFSExcelModel,

				rows: {
					path: "/"
				},
				columns: [{
					name: "Sr.No",
					template: {
						content: "{myFileSrNum}"
					}
				}, {
					name: "GSTIN",
					template: {
						content: "{gstin}"
					}
				}, {
					name: "Return Type",
					template: {
						content: "{retType}"
					}
				}, {
					name: "Return Period",
					template: {
						content: "{retPeriod}"
					}
				}, {
					name: "Due Date",
					template: {
						content: "{}"
					}
				}, {
					name: "Filing Date",
					template: {
						content: "{filingDate}"
					}
				}, {
					name: "ARN No.",
					template: {
						content: "{arnNo}"
					}
				}, {
					name: "Status",
					template: {
						content: "{status}"
					}
				}]

			});
			// download exported file
			oExport.saveFile().catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});
		},
		//########################################### EOC logic Search  ############################################################

		//############################ BOC logic -> My File Status  ##############################
		_bindDefaultData: function () {
			var entityInfo = {
				"req": {}
			};
			var oEntities = new JSONModel();
			var oView = this.getView();
			var EntityListPath = "/aspsapapi/getAllEntities.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: EntityListPath,
					contentType: "application/json",
					data: JSON.stringify(entityInfo)
				}).done(function (data, status, jqXHR) {
					oEntities.setData(data);
					oView.setModel(oEntities, "EntityList");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onSelectChange: function () {
			//var oRetGstin = Number(this.byId("idRetFilnEnty").getSelectedKey());
			var retFilngStInfo = {
				//"req": {
				"entityId": $.sap.entityID
					//}
			};

			var oRetFilnModel = new JSONModel();
			var oRetFilnView = this.getView();
			var oRetFilnPath = "/aspsapapi/getGSTINsForEntity.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRetFilnPath,
					contentType: "application/json",
					data: JSON.stringify(retFilngStInfo)
				}).done(function (data, status, jqXHR) {
					oRetFilnModel.setData(data);
					oRetFilnView.setModel(oRetFilnModel, "RetFilnStGSTNList");
				}).fail(function (jqXHR, status, err) {});
			});

		},

		onMyFileStGo: function () {
			/*var oMyFileGstn = this.byId("idRetFStGSTN").getSelectedItems().length;
			var oMyFileGstnArr = [];
			for (var i = 0; i < oMyFileGstn; i++) {
				oMyFileGstnArr.push(this.byId("idRetFStGSTN").getSelectedItems()[i].getKey());
			}*/
			//debugger;
			//var gstin = [];
			var aGstin = this.byId("idRetFStGSTN").getSelectedKeys();
			//var allGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
			/*if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				for (var i = 0; i < allGstin.length; i++) {
					gstin.push(allGstin[i].value);
				}
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}*/
			if (aGstin[0] === "All") {
				aGstin.shift();
			}

			if (aGstin.length === 0) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}

			var retFilngStInfoGo = {
				//"req": {
				"gstins": aGstin, //aGstin.length === 0 ? gstin : aGstin,
				"returnPeriod": this.byId("dtRetFSts").getValue(),
				"returnTypes": this.byId("idMyFileRetTyp").getSelectedKeys(),
				"finYear": this.byId("finYearID").getSelectedKey() === "" ? "2017-18" : this.byId("finYearID").getSelectedKey()
					//}
			};
			sap.ui.core.BusyIndicator.show(0);
			var oRetFilnModelGo = new JSONModel();
			var oRetFilnViewGo = this.getView();
			var oRetFilnPathGo = "/aspsapapi/getMyFileStatus.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRetFilnPathGo,
					contentType: "application/json",
					data: JSON.stringify(retFilngStInfoGo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oVRFSerNo = data.resp.filingStatus;
					for (var k = 0; k < oVRFSerNo.length; k++) {
						oVRFSerNo[k].vrfSrNo = k + 1;
					}
					oRetFilnModelGo.setData(data);
					oRetFilnViewGo.setModel(oRetFilnModelGo, "RetFStGSTNTable");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onClearFilterMyFile: function () {
			//this.byId("idRetFilnEnty").setSelectedKey(null);
			this.byId("idRetFStGSTN").setSelectedItems(null);
			this.byId("dtRetFSts").setValue(null);
			this.byId("idMyFileRetTyp").setSelectedItems(null);
		},

		onMyFileRefresh: function () {
			this.onMyFileStGo();
		},

		//############# Table leve Excel File download ->  My File Status
		onMyFileStExcelDwnld: function () {
			/*sap.m.URLHelper.redirect("model/Return_Filing.xlsx", true);*/
			var oMyFileModel = new sap.ui.model.json.JSONModel();
			var oMyFileTabData = this.getView().getModel("RetFStGSTNTable").getData().resp.filingStatus;
			oMyFileModel.setData(oMyFileTabData);

			var oExport = new Export({
				exportType: new ExportTypeCSV({
					fileExtension: "xls",
					separatorChar: "\t",
					charset: "utf-8",
					mimeType: "application/vnd.ms-excel"
				}),

				models: oMyFileModel,

				rows: {
					path: "/"
				},
				columns: [{
					name: "Sr.No",
					template: {
						content: "{vrfSrNo}"
					}
				}, {
					name: "GSTIN",
					template: {
						content: "{gstin}"
					}
				}, {
					name: "Return Type",
					template: {
						content: "{retType}"
					}
				}, {
					name: "Return Period",
					template: {
						content: "{retPeriod}"
					}
				}, {
					name: "Due Date",
					template: {
						content: "{}"
					}
				}, {
					name: "Filing Date",
					template: {
						content: "{filingDate}"
					}
				}, {
					name: "ARN No.",
					template: {
						content: "{arnNo}"
					}
				}, {
					name: "Status",
					template: {
						content: "{status}"
					}
				}]

			});
			// download exported file
			oExport.saveFile().catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});
		},
		//########################################### EOC logic -> My File Status  #########################################################################

		//############################ BOC logic -> Counter Party Filing Status   ###############
		CPFilingSt: function () {
			var cpFilingStInfo = {
				"req": {
					"user": "SYSTEM"
				}
			};

			var oCpFilingStModel = new JSONModel();
			var oCpFilingStView = this.getView();
			var oCpFilingStPath = "/aspsapapi/ReturnFilingCounterParty.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oCpFilingStPath,
					contentType: "application/json",
					data: JSON.stringify(cpFilingStInfo)
				}).done(function (data, status, jqXHR) {
					that.apiLimt();
					var oSrNo = data.resp.det;
					for (var i = 0; i < oSrNo.length; i++) {
						oSrNo[i].SrNumber = i + 1;
					}
					oCpFilingStModel.setData(data);
					oCpFilingStView.setModel(oCpFilingStModel, "CPFilingStable");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//#############   File Upload -> Counter Party
		onCPUpload: function () {
			var oFileUploader = this.byId("cpFileUpld");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			var iSelectedIndex = this.byId("filingFrequencyGroup").getSelectedIndex();
			var selFalg = (iSelectedIndex === 0 ? 'Y' : 'N');
			oFileUploader.setUploadUrl("/aspsapapi/returnFilingFileUpload.do");
			oFileUploader.setAdditionalData(selFalg);
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();

		},

		onCPUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();

			var oRespnse = oEvent.getParameters().responseRaw;
			if (oRespnse === "SUCCESS") {
				this.getView().byId("cpFileUpld").setValue("");
				sap.m.MessageBox.success(oRespnse, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(oRespnse, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		//#############   File download -> Counter Party 
		onCPFileDownload: function (oEvt) {
			var oCpFileDwlnd = oEvt.getSource().getEventingParent().getBindingContext("CPFilingStable").getObject().requestId;
			var oCpFileDwnPath = "/aspsapapi/downloadReturnFilingDocument.do?requestId=" + oCpFileDwlnd + "";
			window.open(oCpFileDwnPath);
		},

		onCPRefresh: function () {
			this.CPFilingSt();
		},

		//############# Table leve Excel File download -> Counter Party Filing Status
		onCpExcelDwnld: function () {
			sap.m.URLHelper.redirect("model/Return_Filing.xlsx", true);
			/*	var oCpFSModel = new sap.ui.model.json.JSONModel();
				var oCpFSTabData = this.getView().getModel("CPFilingStable").getData().resp.det;
				oCpFSModel.setData(oCpFSTabData);

				var oExport = new Export({
					exportType: new ExportTypeCSV({
						separatorChar: "\t",
						mimeType: "application/vnd.ms-excel",
						charset: "utf-8",
						fileExtension: "xls"
					}),

					models: oCpFSModel,

					rows: {
						path: "/"
					},
					columns: [{
						name: "Sr.No",
						template: {
							content: "{SrNumber}"
						}
					}, {
						name: "Request Id",
						template: {
							content: "{requestId}"
						}
					}, {
						name: "Date",
						template: {
							content: "{dateOfUpload}"
						}
					}, {
						name: "Number Of GSTIN",
						template: {
							content: "{noOfGstins}"
						}
					}, {
						name: "Status",
						template: {
							content: "{status}"
						}
					}]

				});
				// download exported file
				oExport.saveFile().catch(function (oError) {
					MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
				}).then(function () {
					oExport.destroy();
				});*/
		},

		/////////////NON Compliant Vendor//////////////////
		////////////////////Start//////////////////////////
		onReturnTypeChange: function (oEvt) {
			debugger;
			var key = oEvt.getSource().getSelectedKey();
			if (key === "ITC04") {
				this.byId("table1").setVisible(false);
				this.byId("ITC04id").setVisible(true);
				this.byId("Gstr9id").setVisible(false);
			} else if (key === "GSTR-9" || key === "GSTR-9C") {
				this.byId("table1").setVisible(false);
				this.byId("ITC04id").setVisible(false);
				this.byId("Gstr9id").setVisible(true);
			} else {
				this.byId("table1").setVisible(true);
				this.byId("ITC04id").setVisible(false);
				this.byId("Gstr9id").setVisible(false);
			}

		}

		/////////////NON Compliant Vendor//////////////////
		////////////////////End//////////////////////////

	});
});