sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	'sap/m/library',
	"sap/m/MessageBox"
], function (BaseController, JSONModel, library, MessageBox) {
	"use strict";
	var vCount = 10;
	return BaseController.extend("com.ey.digigst.controller.asnycReports", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.asnycReports
		 */
		onInit: function () {
			var that = this,
				vDate = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);
			this.getOwnerComponent().getRouter().getRoute("asnycReports").attachPatternMatched(this._onRouteMatched, this);

			this.byId("idFrom").setMaxDate(vDate);
			this.byId("idFrom").setDateValue(vPeriod);
			this.byId("idFrom").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idFrom").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idTo").setMinDate(vPeriod);
			this.byId("idTo").setMaxDate(vDate);
			this.byId("idTo").setDateValue(vDate);
			this.byId("idTo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTo").$().find("input").attr("readonly", true);
				}
			});
		},

		onFromDateChange: function (oevt) {
			var toDate = this.byId("idTo").getDateValue(),
				fromDate = this.byId("idFrom").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("From Date can not be greter then To Date");
				this.byId("idTo").setDateValue(oevt.getSource().getDateValue());
				this.byId("idTo").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idTo").setMinDate(oevt.getSource().getDateValue());
			}
		},

		_onRouteMatched: function () {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("asnycReports");
			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 10
				},
				"req": {
					"entityId": Number($.sap.entityID),
					"dataType": this.removeAll(this.byId("idDT").getSelectedKeys()),
					"reportCateg": this.removeAll(this.byId("idRC").getSelectedKeys()),
					"requestFromDate": this.byId("idFrom").getValue(),
					"requestToDate": this.byId("idTo").getValue()
				}
			};
			this.tabBind(payload);
		},

		onClear: function () {
			var vDate = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);

			this.byId("idDT").setSelectedKeys([]);
			this.byId("idRC").setSelectedKeys([]);
			this.getView().setModel(new JSONModel([]), "getReportCategory");

			this.byId("idFrom").setMaxDate(vDate);
			this.byId("idFrom").setDateValue(vPeriod);

			this.byId("idTo").setMinDate(vPeriod);
			this.byId("idTo").setMaxDate(vDate);
			this.byId("idTo").setDateValue(vDate);

			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 10
				},
				"req": {
					"entityId": $.sap.entityID,
					"dataType": this.removeAll(this.byId("idDT").getSelectedKeys()),
					"reportCateg": this.removeAll(this.byId("idRC").getSelectedKeys()),
					"requestFromDate": this.byId("idFrom").getValue(),
					"requestToDate": this.byId("idTo").getValue()
				}
			};
			this.tabBind(payload);
		},

		tabBind: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getReportFileStatusDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = new JSONModel([]);
					if (data.resp.AsyncReportsData !== "No Data") {
						oModel.setData(data);
					}
					this._nicPagination(data.hdr);
					this.getView().setModel(oModel, "AsnycReport");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDTChange: function () {
			var payload = {
				"req": {
					"dataType": this.removeAll(this.byId("idDT").getSelectedKeys())
				}
			};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getReportCategory.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel([]);
					if (data.resp.reportCategory.length) {
						data.resp.reportCategory.sort(function (a, b) {
							return a.repCateg.localeCompare(b.repCateg);
						});
						data.resp.reportCategory.unshift({
							"repCateg": "All"
						});
						oModel.setData(data.resp);
					}
					this.getView().setModel(oModel, "getReportCategory");
					this.getView().byId("idRC").setSelectedKeys([]);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel([]), "getReportCategory");
				}.bind(this));
		},

		onDownloadPress: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().requestId;
			var vCatgory = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().reportCateg;
			//var status = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().status;
			//======= Added by chaithra for 180 Days reversal response ========//
			if (vCatgory == "REV_180DAYS_RESPONSE") {
				var oReqExcelPath = "/aspsapapi/rev180RespReportDownload.do?configId=" + this.oReqId + "";
				window.open(oReqExcelPath);
			} else if (vCatgory == "Recon Response 2BPR") {
				var oReqExcelPath = "/aspsapapi/gstr2BPRRespDownload.do?configId=" + this.oReqId + "";
				window.open(oReqExcelPath);
			} else if (vCatgory == "Recon Response 2APR(202DFs)") {
				var oReqExcelPath = "/aspsapapi/gstr2AAutoPRRespDownload.do?configId=" + this.oReqId + "";
				window.open(oReqExcelPath);
			} else if (vCatgory == "Recon Response Auto(108DFs)" || vCatgory == "Recon Response SFTP(108DFs)") {
				var oReqExcelPath = "/aspsapapi/gstr2APRSftpRespDownload.do?configId=" + this.oReqId + "";
				window.open(oReqExcelPath);
			} else {
				var oReqExcelPath = "/aspsapapi/fileStatusDownloadDocument.do?configId=" + this.oReqId + "";
				window.open(oReqExcelPath);
			}
		},

		onFragDownloadAll: function (oReqId) {
			var oData = this.getView().getModel("DownloadDocument").getData();
			for (var i = 0; i < oData.length; i++) {
				this.onFragDownload(oData[i].requestId, oData[i].reportCateg);
			}
		},
		onFragDownload: function (oReqId, vCatgory) {
			if (vCatgory == "REV_180DAYS_RESPONSE") {
				var oReqExcelPath = "/aspsapapi/rev180RespReportDownload.do?configId=" + oReqId + "";
			} else if (vCatgory == "Recon Response 2BPR") {
				oReqExcelPath = "/aspsapapi/gstr2BPRRespDownload.do?configId=" + oReqId + "";
			} else if (vCatgory == "Recon Response 2APR(202DFs)") {
				oReqExcelPath = "/aspsapapi/gstr2AAutoPRRespDownload.do?configId=" + oReqId + "";
			} else if (vCatgory == "Recon Response Auto(108DFs)" || vCatgory == "Recon Response SFTP(108DFs)") {
				oReqExcelPath = "/aspsapapi/gstr2APRSftpRespDownload.do?configId=" + oReqId + "";
			} else {
				oReqExcelPath = "/aspsapapi/fileStatusDownloadDocument.do?configId=" + oReqId + "";
			}
			library.URLHelper.redirect(oReqExcelPath, true);
		},

		onRefresh: function () {
			this._onRouteMatched();
		},

		////////Pagination/////////////
		_nicPagination: function (header) {
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
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/getReportFileStatusDetails.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": vCount
					},
					"req": {
						"entityId": Number($.sap.entityID),
						"dataType": this.removeAll(this.byId("idDT").getSelectedKeys()),
						"reportCateg": this.removeAll(this.byId("idRC").getSelectedKeys()),
						"requestFromDate": this.byId("idFrom").getValue(),
						"requestToDate": this.byId("idTo").getValue()
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
					that._nicPagination(data.hdr);
					onicCrdMdl.setData(data);
					that.getView().setModel(onicCrdMdl, "AsnycReport");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onRDownloadPress: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getBindingContext("AsnycReport").getObject().requestId;
			var vReportType = oEvt.getSource().getEventingParent().getBindingContext("AsnycReport").getObject().reportType;
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.Async.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			if (vReportType === "Credit Distribution Annexure Report") {
				var oData = {
					"visFlag": true
				}
			} else {
				var oData = {
					"visFlag": false
				}
			}

			this.getView().setModel(new JSONModel(oData), "visFlagModel");

			var oIntiData = {
				"req": {
					"requestId": this.oReqId
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/reportFileDownloadIdWise.do";
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
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.asnycReports
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.asnycReports
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.asnycReports
		 */
		//	onExit: function() {
		//
		//	}

	});

});