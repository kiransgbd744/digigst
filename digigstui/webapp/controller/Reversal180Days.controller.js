sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Dialog",
	"sap/m/Button"
], function (BaseController, MessageBox, Formatter, JSONModel, Dialog, Button) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Reversal180Days", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Reversal180Days
		 */
		onInit: function () {
			var vDate = new Date(),
				aCriteria = [{
					key: "docDate",
					text: "Document Date"
				}, {
					key: "accVoucherDate",
					text: "Accounting Voucher Date"
				}, {
					key: "taxPeriod",
					text: "Tax Period"
				}];
			this.getView().setModel(new JSONModel(aCriteria), "CriteriaModel");
			this._setDateProperty("idDateFrom", null, null, null);
			this._setDateProperty("idDateTo", null, null, null);
			this._setDateProperty("idFromPeriod", null, null, null);
			this._setDateProperty("idToPeriod", null, null, null);

			this._setDateProperty("frDocDate", null, null, null);
			this._setDateProperty("toDocDate", null, null, null);
			this._setDateProperty("frTaxPeriod", null, null, null);
			this._setDateProperty("toTaxPeriod", null, null, null);
		},

		onBeforeRendering: function () {
			this._bindScreenProperty();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._bindDefaultFilter();
				this._bindReqIdWiseProp();
				this.onLoadEntity();
				this._getUserId();
			}
		},

		_bindDefaultFilter: function () {
			var vDate = new Date(),
				frDate = new Date();
			frDate.setMonth(frDate.getMonth() - 1);
			this.getView().setModel(new JSONModel({
				"gstins": [],
				"criteria": "docDate",
				"fromDate": frDate,
				"toDate": vDate,
				"fromTaxPeriod": vDate,
				"toTaxPeriod": vDate,
				"maxDate": vDate
			}), "FilterModel");
		},

		_bindReqIdWiseProp: function () {
			var vDate = new Date(),
				frDate = new Date();
			frDate.setMonth(frDate.getMonth() - 1);
			this.getView().setModel(new JSONModel({
				"gstins": [],
				"criteria": "docDate",
				"fromDate": frDate,
				"toDate": vDate,
				"fromTaxPeriod": vDate,
				"toTaxPeriod": vDate,
				"maxDate": vDate,
				"initiatedBy": [],
				"reconStatus": ""
			}), "ReqIdFilter");
		},

		_bindScreenProperty: function () {
			this.getView().setModel(new JSONModel({
				"OutFullScreen": false,
				"reqIdWise": false
			}), "PrProperty");
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"screenName": "itcReverse180d"
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
						this.byId("idRequestIDwisePage").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onChangeDateValue: function (type) {
			var oModel = this.getView().getModel("FilterModel");
			if (type === "D" && oModel.getProperty("/fromDate") > oModel.getProperty("/toDate")) {
				oModel.setProperty("/toDate", oModel.getProperty("/fromDate"));

			} else if (type === "P" && oModel.getProperty("/fromTaxPeriod") > oModel.getProperty("/toTaxPeriod")) {
				oModel.setProperty("/toTaxPeriod", oModel.getProperty("/fromTaxPeriod"));
			}
			oModel.refresh(true);
		},

		onGstr2FullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty");
			oPropModel.setProperty("/OutFullScreen", action === "openSummary");
			oPropModel.refresh(true);
		},

		onLoadEntity: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"criteria": oFilter.criteria,
						"gstins": (oFilter.gstins.includes("All") ? [] : oFilter.gstins),
						"fromDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.fromDate) : undefined),
						"toDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.toDate) : undefined),
						"fromTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.fromTaxPeriod) : undefined),
						"toTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.toTaxPeriod) : undefined),
						"entityId": parseInt($.sap.entityID)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getITCReversal180Summry.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "SummTab");
					this.getView().getModel("SummTab").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCompute: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				aTabData = this.byId("idSumTab").getModel("SummTab").getProperty("/"),
				aIndex = this.byId("idSumTab").getSelectedIndices(),
				Gstin = [];

			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Select atleast one record to compute", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				aIndex.forEach(function (idx) {
					Gstin.push(aTabData[idx].gstin);
				});
			}
			if ((oFilter.criteria !== "taxPeriod" && !oFilter.fromDate) || (oFilter.criteria === "taxPeriod" && !oFilter.fromTaxPeriod)) {
				sap.m.MessageBox.error("Please select From Date");
				return;
			}
			if ((oFilter.criteria !== "taxPeriod" && !oFilter.toDate) || (oFilter.criteria === "taxPeriod" && !oFilter.toTaxPeriod)) { //this.getView().byId("idDateTo").getValue()
				sap.m.MessageBox.error("Please select To Date");
				return;
			}
			MessageBox.confirm("Do you want to initiate 180 reversal compute for select GSTINs", {
				title: "Confirm",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "YES") {
						var PayLoad = {
							"req": {
								"gstins": Gstin,
								"criteria": oFilter.criteria,
								"fromDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.fromDate) : undefined),
								"toDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.toDate) : undefined),
								"fromTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.fromTaxPeriod) : undefined),
								"toTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.toTaxPeriod) : undefined),
								"entityId": parseInt($.sap.entityID)
							}
						};
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
								method: "POST",
								url: "/aspsapapi/getITCReversal180Compute.do",
								contentType: "application/json",
								data: JSON.stringify(PayLoad)
							})
							.done(function (data, status, jqXHR) {
								sap.ui.core.BusyIndicator.hide();
								if (data.hdr.status === "S") {
									MessageBox.success("Data compute successfully");
									this.onLoadEntity();
								} else if (data.hdr.status === "E") {
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

		onClearMainFilter: function () {
			this._bindDefaultFilter();
		},

		onPressRequestIDwise: function () {
			var oModel = this.getView().getModel("PrProperty");
			oModel.setProperty("/reqIdWise", true);
			oModel.refresh(true);
			this.onSearchReqIdWise();
		},

		onPressRequestIDwiseBack: function () {
			var oModel = this.getView().getModel("PrProperty");
			oModel.setProperty("/reqIdWise", false);
			oModel.refresh(true);
		},

		onChangeReqWiseDate: function (type) {
			var oModel = this.getView().getModel("ReqIdFilter");
			if (type === "D" && oModel.getProperty("/fromDate") > oModel.getProperty("/toDate")) {
				oModel.setProperty("/toDate", oModel.getProperty("/fromDate"));

			} else if (type === "P" && oModel.getProperty("/fromTaxPeriod") > oModel.getProperty("/toTaxPeriod")) {
				oModel.setProperty("/toTaxPeriod", oModel.getProperty("/fromTaxPeriod"));
			}
			oModel.refresh(true);
		},

		onClearReqIdWise: function () {
			this._bindReqIdWiseProp();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function (oEvent) {
			var oFilter = this.getView().getModel("ReqIdFilter").getProperty("/"),
				payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"criteria": oFilter.criteria,
						"fromDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.fromDate) : undefined),
						"toDate": (oFilter.criteria !== "taxPeriod" ? this._getDate(oFilter.toDate) : undefined),
						"fromTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.fromTaxPeriod) : undefined),
						"toTaxPeriod": (oFilter.criteria === "taxPeriod" ? this._getTaxPeriod(oFilter.toTaxPeriod) : undefined),
						"initiationByUserId": oFilter.initiatedBy,
						"reconStatus": oFilter.reconStatus
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getITCReversal180ReqIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "oRequestWise");
					this.getView().getModel("oRequestWise").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onConfigExtractPress2A1: function (oEvent) {
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}
			this._getGstr2a1.open();

			var obj = oEvent.getSource().getBindingContext("oRequestWise").getObject(),
				payload = {
					"req": {
						"computeId": obj.computeId
					}
				};
			this._getGstr2a1.setBusy(true);
			this._getGstr2a1.setModel(new JSONModel(obj), "PropReport");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/reversal180DownloadIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel(data.hdr.status === "S" ? data.resp : []);
					this.getView().setModel(oModel, "DownloadDocument");
					this._getGstr2a1.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel([]), "DownloadDocument");
					this._getGstr2a1.setBusy(false);
				}.bind(this));
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onFragDownload: function (oEvent) {
			var computeId = this._getGstr2a1.getModel("PropReport").getProperty("/computeId"),
				obj = oEvent.getSource().getBindingContext("DownloadDocument").getObject(),
				payload = {
					"req": {
						"configId": computeId,
						"reportType": obj.reportName,
					}
				};
			this.excelDownload(payload, "/aspsapapi/itcReversalReportDownload.do");
		},

		onPressGSTIN: function (oEvent) {
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			var obj = oEvent.getSource().getBindingContext("oRequestWise").getObject();
			this._oGstinPopover.setModel(new JSONModel(obj.gstin), "gstins2A");
			this._oGstinPopover.openBy(oEvent.getSource());
		},

		//========================== OTP generation ============================//
		onPressGenerateOTP: function (oEvent) {
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			var obj = oEvent.getSource().getBindingContext("SummTab").getObject();
			if (obj.authtoken !== "I") {
				return;
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": obj.gstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressYes: function () {
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GenerateOTP", this);
				this.getView().addDependent(this._dGenerateOTP);
			}
			var gstin = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getProperty("/gstin"),
				payload = {
					"req": {
						"gstin": gstin
					}
				};
			this.byId("dAuthTokenConfirmation").setBusy(true);
			this.byId("dVerifyAuthToken").setModel(new JSONModel(payload.req), "OtpProperty");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getOtp.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
					this._dAuthToken.close();
					if (data.resp.status === "S") {
						var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty");
						oModel.getProperty("/verify", false);
						oModel.getProperty('otp', null);
						oModel.getProperty("resendOtp", false);
						oModel.refresh(true);
						this._dGenerateOTP.open();
					} else {
						MessageBox.error("OTP Generation Failed. Please Try Again", {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
				}.bind(this));
		},

		onPressVerifyOTP: function () {
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getProperty("/"),
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

		onPressCancel: function () {
			this._dAuthToken.close();
		},
		//============================ end of OTP Generation =============================//

		_getDate: function (date) {
			var d = date.getDate(),
				m = date.getMonth();
			return (d < 10 ? '0' : '') + d + "-" + (m < 9 ? '0' : '') + (m + 1) + "-" + date.getFullYear();
		},

		_getTaxPeriod: function (date) {
			var m = date.getMonth();
			return (m < 9 ? '0' : '') + (m + 1) + date.getFullYear();
		}
	});
});