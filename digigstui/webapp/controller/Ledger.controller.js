sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter"
], function (Controller, JSONModel, Fragment, MessageBox, Formatter) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.Ledger", {
		onInit: function () {
			var vDate = new Date(),
				date1 = new Date();

			date1.setMonth(date1.getMonth() - 1);

			this._setReadOnly("dtLibDtl", vDate, vDate, null);
			this._setReadOnly("dtLedgrCreditFromDate", date1, vDate, null);
			this._setReadOnly("dtLedgrCreditToDate", vDate, vDate, date1);
			this._setReadOnly("dtCashDtlFromDate", date1, vDate, null);
			this._setReadOnly("dtCashDtlToDate", vDate, vDate, date1);
			this._setReadOnly("dtReclaimFr", date1, vDate, null);
			this._setReadOnly("dtReclaimTo", vDate, vDate, date1);
			this._setReadOnly("dtFrRcm");
			this._setReadOnly("dtToRcm");
			this._setReadOnly("negLiaDtFr");
			this._setReadOnly("negLiaDtTo");
			this._bindDefaultValue();
			this.getOwnerComponent().getRouter().getRoute("Ledger").attachPatternMatched(this._onRouteMatched, this);
		},

		_bindDefaultValue: function () {
			var vDate = new Date(),
				date1 = new Date();

			date1.setMonth(date1.getMonth() - 1);
			this.setModel(new JSONModel({
				"title": null,
				"gstin": null,
				"btnBack": false,
				"table": "tabLedger",
				"frRcm": date1,
				"toRcm": vDate,
				"frNegLia": date1,
				"toNegLia": vDate,
				"maxDate": vDate
			}), "ViewProperty");
		},

		_onRouteMatched: function (oEvent) {
			this.getRouter().getHashChanger().setHash("Ledger");
			this.onPressGo();
		},

		onFromDateChangeLedgrCredit: function () {
			var toDate = this.byId("dtLedgrCreditToDate").getDateValue(),
				frDate = this.byId("dtLedgrCreditFromDate").getDateValue();

			if (frDate > toDate) {
				this.byId("dtLedgrCreditToDate").setDateValue(frDate);
			}
			this.byId("dtLedgrCreditToDate").setMinDate(frDate);
		},

		onFromDateChangeCashDtl: function () {
			var toDate = this.byId("dtCashDtlToDate").getDateValue(),
				frDate = this.byId("dtCashDtlFromDate").getDateValue();

			if (frDate > toDate) {
				this.byId("dtCashDtlToDate").setDateValue(frDate);
			}
			this.byId("dtCashDtlToDate").setMinDate(frDate);
		},

		onPressGo: function () {
			var payload = {
				"req": {
					"entityId": +($.sap.entityID)
				}
			};
			this.getView().setModel(new JSONModel([]), "LedgerTable");
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/GetSummaryLedgerBalance.do", //getCashITCLibLegderBalance.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.sort(function (a, b) {
						return a.gstin.localeCompare(b.gstin);
					});
					this.getView().setModel(new JSONModel(data), "LedgerTable");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onRefresh: function () {
			var oTabData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				aIndex = this.byId("tabLedger").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": +($.sap.entityID)
					}
				};
			payload.req.gstins = aIndex.map(function (e) {
				return oTabData[e].gstin;
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/LedgerRefresh.do", //getCashITCLibLegderBalance.do"
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (!this._dLedgerMsg) {
						this._dLedgerMsg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.MultiGstnAuto", this);
						this.getView().addDependent(this._dLedgerMsg);
					}
					sap.ui.core.BusyIndicator.hide();
					this._dLedgerMsg.setModel(new JSONModel(data), "AutoCalc3bMsg");
					this._dLedgerMsg.setTitle("Get Ledger Call Status");
					this._dLedgerMsg.open();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseDialogBulkGstn: function () {
			this.onPressGo();
			this._dLedgerMsg.close();
		},

		/**
		 * Download Ledger Summary Table Data
		 */
		onDownloadLedger: function (oEvt) {
			var oTabData = this._getLedgerDataSource(),
				data = [{
					sheet: "Ledger Data",
					cols: this._getLedgerColumns(),
					rows: this._formatAmountValue(oTabData)
				}];
			this.xlsxTabDataDownload("Entity Level Ledger_" + this.byId("dtLedger").getValue() + ".xlsx", data);
		},

		_getLedgerColumns: function () {
			return [{
				key: "gstin",
				header: "GSTIN",
				width: 17
			}, {
				key: "itcigst_totbal",
				header: "IGST(Electronic Credit Register)",
				width: 12
			}, {
				key: "itccgst_totbal",
				header: "CGST(Electronic Credit Register)",
				width: 12
			}, {
				key: "itcsgst_totbal",
				header: "SGST(Electronic Credit Register)",
				width: 12
			}, {
				key: "itccess_totbal",
				header: "Cess(Electronic Credit Register)",
				width: 12
			}, {
				key: "cashigst_tot_bal",
				header: "IGST(Electronic Cash Ledger)",
				width: 12
			}, {
				key: "cashcgst_tot_bal",
				header: "CGST(Electronic Cash Ledger)",
				width: 12
			}, {
				key: "cashsgst_tot_bal",
				header: "SGST(Electronic Cash Ledger)",
				width: 12
			}, {
				key: "cashcess_tot_bal",
				header: "Cess(Electronic Cash Ledger)",
				width: 12
			}, {
				key: "libigst_totbal",
				header: "IGST(Electronic Liability Ledger)",
				width: 12
			}, {
				key: "libcgst_totbal",
				header: "CGST(Electronic Liability Ledger)",
				width: 12
			}, {
				key: "libsgst_totbal",
				header: "SGST(Electronic Liability Ledger)",
				width: 12
			}, {
				key: "libcess_totbal",
				header: "Cess(Electronic Liability Ledger)",
				width: 12
			}, {
				key: "crRevigst_totbal",
				header: "IGST(Electronic Credit Reversal & Re - claim Ledger)",
				width: 12
			}, {
				key: "crRevcgst_totbal",
				header: "CGST(Electronic Credit Reversal & Re - claim Ledger)",
				width: 12
			}, {
				key: "crRevsgst_totbal",
				header: "SGST(Electronic Credit Reversal & Re - claim Ledger)",
				width: 12
			}, {
				key: "crRevcess_totbal",
				header: "Cess(Electronic Credit Reversal & Re - claim Ledger)",
				width: 12
			}, {
				key: "rcmIgst_totbal",
				header: "IGST(RCM Ledger)",
				width: 12
			}, {
				key: "rcmCgst_totbal",
				header: "CGST(RCM Ledger)",
				width: 12
			}, {
				key: "rcmSgst_totbal",
				header: "SGST(RCM Ledger)",
				width: 12
			}, {
				key: "rcmCess_totbal",
				header: "Cess(RCM Ledger)",
				width: 12
			}, {
				key: "negativeIgst_totbal",
				header: "IGST(Negative Liability)",
				width: 12
			}, {
				key: "negativeCgst_totbal",
				header: "CGST(Negative Liability)",
				width: 12
			}, {
				key: "negativeSgst_totbal",
				header: "SGST(Negative Liability)",
				width: 12
			}, {
				key: "negativeCess_totbal",
				header: "Cess(Negative Liability)",
				width: 12
			}, {
				key: "lastupdated_date",
				header: "Last Updated",
				width: 12
			}];
		},

		_getLedgerDataSource: function () {
			var oTabData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				aIndex = this.byId("tabLedger").getSelectedIndices();
			if (!aIndex.length) {
				return $.extend(true, [], oTabData);
			}
			return aIndex.map(function (e) {
				return oTabData[e];
			});
		},

		_formatAmountValue: function (data) {
			var aField = [
				"itcigst_totbal", "itccgst_totbal", "itcsgst_totbal", "itccess_totbal",
				"cashigst_tot_bal", "cashcgst_tot_bal", "cashsgst_tot_bal", "cashcess_tot_bal",
				"libigst_totbal", "libcgst_totbal", "libsgst_totbal", "libcess_totbal",
				"crRevigst_totbal", "crRevcgst_totbal", "crRevsgst_totbal", "crRevcess_totbal",
				"rcmIgst_totbal", "rcmCgst_totbal", "rcmSgst_totbal", "rcmCess_totbal",
				"negativeIgst_totbal", "negativeCgst_totbal", "negativeSgst_totbal", "negativeCess_totbal"
			];
			data.forEach(function (e) {
				aField.forEach(function (f) {
					e[f] = this.formatLedgerAmount(e[f]) || "0";
				}.bind(this));
			}.bind(this));
			return data;
		},

		formatLedgerAmount: function (value) {
			if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
				var flag = false,
					s = typeof (value) === "number" ? value.toString() : value;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var y = s.split(".")[0],
					dec = s.split(".")[1],
					lastThree = y.substring(y.length - 3),
					otherNumbers = y.substring(0, y.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + (dec ? "." + dec : "");
				return res;
			}
			return value;
		},

		/***********************************************************************
		 * Electronic Credit Register
		 ***********************************************************************/
		onPrsDtlCreditLdgr: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "idTabCashLedger", "idtableLiability", "tabReclaim"],
				oModel = this.getModel("ViewProperty");

			if (this._validateAuthToken(obj.status)) {
				return;
			}
			this.oCreditGstin = obj.gstin;

			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));

			this.byId("tabCreditLedgerDtl").setVisible(true);
			this.byId("idElectronicledger").setVisible(true);

			oModel.setProperty("/title", "Electronic Credit Register - " + obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "creditLedger");
			oModel.refresh(true);

			var oCreditInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": +($.sap.entityID),
					"gstin": obj.gstin,
					"fr_dt": this.byId("dtLedgrCreditFromDate").getValue(),
					"to_dt": this.byId("dtLedgrCreditToDate").getValue()
				}
			};
			this._getLedrCredit(oCreditInfo);
		},

		_getLedrCredit: function (oCreditInfo) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getITCLedgerDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oCreditInfo)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var oSerNo = data.resp;
						for (var i = 0; i < oSerNo.length; i++) {
							oSerNo[i].tabSerNo = i + 1;
							oSerNo[i].gstin = this.oCreditGstin;
						}
						this.getView().setModel(new JSONModel(data), "CreditDtls");
					} else {
						var oGSTIN1 = new JSONModel([]);
						this.getView().setModel(oGSTIN1, "CreditDtls");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onPrsCreditGo: function () {
			var oCreditInfoGo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					//"entityId": Number(this.byId("ledgrEntity").getSelectedKey()),
					"gstin": this.oCreditGstin,
					"fr_dt": this.byId("dtLedgrCreditFromDate").getValue(),
					"to_dt": this.byId("dtLedgrCreditToDate").getValue()
				}
			};
			this._getLedrCredit(oCreditInfoGo);
		},

		//############################################  Electronic Credit Register Dtl Download   ##################################//
		onDtlCreditDwnld: function (oEvent) {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.oCreditGstin,
					"fr_dt": this.byId("dtLedgrCreditFromDate").getValue(),
					"to_dt": this.byId("dtLedgrCreditToDate").getValue()
				}
			};
			this.excelDownload(payload, "/aspsapapi/getCreditLedgerReportDownload.do");
		},

		onDownloadCreditLedgerPdf: function (oEvent) {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.oCreditGstin,
					"fr_dt": this.byId("dtLedgrCreditFromDate").getValue(),
					"to_dt": this.byId("dtLedgrCreditToDate").getValue()
				}
			};
			this.pdfDownload(payload, "/aspsapapi/getCreditLedgerDetailsPdf.do");
		},

		//###############################################   Electronic Cash Ledger    ###########################################//
		onPrsDtlCashLdgr: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "tabCreditLedgerDtl", "idtableLiability", "tabReclaim"],
				oModel = this.getModel("ViewProperty");
			if (this._validateAuthToken(obj.status)) {
				return;
			}
			this.oTabCashGstin = obj.gstin;

			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));
			this.byId("idTabCashLedger").setVisible(true);

			oModel.setProperty("/title", "Electronic Cash Ledger - " + obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "cashLedger");
			oModel.refresh(true);

			var oCashDtlInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": +($.sap.entityID),
					"gstin": obj.gstin,
					"fr_dt": this.byId("dtCashDtlFromDate").getValue(),
					"to_dt": this.byId("dtCashDtlToDate").getValue()
				}
			};
			this._getCashLedgr(oCashDtlInfo);
		},

		_getCashLedgr: function (oCashDtlInfo) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCashLedgerDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oCashDtlInfo)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var oCashSrNo = data.resp;
						for (var j = 0; j < oCashSrNo.length; j++) {
							oCashSrNo[j].cashTabSrNo = j + 1;
							oCashSrNo[j].gstin = this.oTabCashGstin;
						}
						this.getView().setModel(new JSONModel(data), "EleCashDtls");
					} else {
						var oGSTIN1 = new JSONModel([]);
						this.getView().setModel(oGSTIN1, "EleCashDtls");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onPrsCashGo: function () {
			var oCashDtlInfoGo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": +($.sap.entityID),
					"gstin": this.oTabCashGstin,
					"fr_dt": this.byId("dtCashDtlFromDate").getValue(),
					"to_dt": this.byId("dtCashDtlToDate").getValue()
				}
			};
			this._getCashLedgr(oCashDtlInfoGo);
		},

		/**
		 * Electronic Cash Ledger Dtl Download
		 */
		onCashExclDwnld: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.oTabCashGstin,
					"fr_dt": this.byId("dtCashDtlFromDate").getValue(),
					"to_dt": this.byId("dtCashDtlToDate").getValue()
				}
			};
			this.excelDownload(payload, "/aspsapapi/getCashLedgerReportDownload.do");
		},

		///////////////////////////////////////////////////Electronic Liability Ledger///////////////////////////////////////////
		onDetLiblyLdgr: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "tabCreditLedgerDtl", "idTabCashLedger", "tabReclaim"],
				oModel = this.getModel("ViewProperty");
			if (this._validateAuthToken(obj.status)) {
				return;
			}
			this.oTabLibGstn = obj.gstin;

			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));
			this.byId("idtableLiability").setVisible(true);

			oModel.setProperty("/title", "Electronic Liability Ledger - " + obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "liabilityLedger");
			oModel.refresh(true);

			var oTabLibGstnInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"gstin": obj.gstin,
					"ret_period": this.byId("dtLibDtl").getValue()
				}
			};
			this._getLiabilityInfo(oTabLibGstnInfo);
		},

		_getLiabilityInfo: function (oTabLibGstn) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getLiabilityLedgerDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oTabLibGstn)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						data.resp.forEach(function (e, i) {
							e.oDtlLibSrNo = i + 1;
							e.gstin = this.oTabLibGstn;
						}.bind(this));
						this.getView().setModel(new JSONModel(data), "LiabilityDtl");
					} else {
						this.getView().setModel(new JSONModel([]), "LiabilityDtl");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		/////////Electronic Liability Ledger GO  //////////////
		onGoLibDtls: function () {
			var oTabLibGstn = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": this.oTabLibGstn,
					"ret_period": this.byId("dtLibDtl").getValue()
				}
			};
			this._getLiabilityInfo(oTabLibGstn);
		},
		///////////////////////////////////////////////////Electronic Liability PDF Download///////////////////////////////////////
		onPrsLibPDFDwnld: function (oEvent) {
			var payload = {
				"req": {
					"gstin": this.oTabLibGstn,
					"ret_period": this.byId("dtLibDtl").getValue()
				}
			};
			this.pdfDownload(payload, "/aspsapapi/getLiabilityLedgerDetailsPdf.do");
		},

		///////////////////////////////////////////////////Electronic Liability Ledger Dtl Excel Download///////////////////////////////////////
		onPrsLibExclDwnld: function () {
			var data = [{
				sheet: "Electronic Liability Ledger",
				cols: this._createLiabilityLedgerColumn(),
				rows: this._liabilityFlattenData()
			}];
			this.xlsxTabDataDownload("Electronic Liability Ledger_" + this.getDateTimeStamp() + ".xlsx", data);
		},

		_liabilityFlattenData: function () {
			var oData = this.getView().getModel("LiabilityDtl").getProperty("/resp");
			return oData.map(function (item) {
				return {
					oDtlLibSrNo: item.oDtlLibSrNo,
					gstin: item.gstin,
					dt: item.dt,
					ref_no: item.ref_no,
					ret_period: item.ret_period,
					desc: item.desc,
					tr_typ: item.tr_typ,
					disTyp: item.disTyp,
					igstTx: (item.igst && item.igst.tx ? item.igst.tx : undefined),
					igstIntr: (item.igst && item.igst.intr ? item.igst.intr : undefined),
					igstPen: (item.igst && item.igst.pen ? item.igst.pen : undefined),
					igstFee: (item.igst && item.igst.fee ? item.igst.fee : undefined),
					igstOth: (item.igst && item.igst.oth ? item.igst.oth : undefined),
					igstTot: (item.igst && item.igst.tot ? item.igst.tot : undefined),
					cgstTx: (item.cgst && item.cgst.tx ? item.cgst.tx : undefined),
					cgstIntr: (item.cgst && item.cgst.intr ? item.cgst.intr : undefined),
					cgstPen: (item.cgst && item.cgst.pen ? item.cgst.pen : undefined),
					cgstFee: (item.cgst && item.cgst.fee ? item.cgst.fee : undefined),
					cgstOth: (item.cgst && item.cgst.oth ? item.cgst.oth : undefined),
					cgstTot: (item.cgst && item.cgst.tot ? item.cgst.tot : undefined),
					sgstTx: (item.sgst && item.sgst.tx ? item.sgst.tx : undefined),
					sgstIntr: (item.sgst && item.sgst.intr ? item.sgst.intr : undefined),
					sgstPen: (item.sgst && item.sgst.pen ? item.sgst.pen : undefined),
					sgstFee: (item.sgst && item.sgst.fee ? item.sgst.fee : undefined),
					sgstOth: (item.sgst && item.sgst.oth ? item.sgst.oth : undefined),
					sgstTot: (item.sgst && item.sgst.tot ? item.sgst.tot : undefined),
					cessTx: (item.cess && item.cess.tx ? item.cess.tx : undefined),
					cessIntr: (item.cess && item.cess.intr ? item.cess.intr : undefined),
					cessPen: (item.cess && item.cess.pen ? item.cess.pen : undefined),
					cessFee: (item.cess && item.cess.fee ? item.cess.fee : undefined),
					cessOth: (item.cess && item.cess.oth ? item.cess.oth : undefined),
					cessTot: (item.cess && item.cess.tot ? item.cess.tot : undefined),
					igstbalTx: (item.igstbal && item.igstbal.tx ? item.igstbal.tx : undefined),
					igstbalIntr: (item.igstbal && item.igstbal.intr ? item.igstbal.intr : undefined),
					igstbalPen: (item.igstbal && item.igstbal.pen ? item.igstbal.pen : undefined),
					igstbalFee: (item.igstbal && item.igstbal.fee ? item.igstbal.fee : undefined),
					igstbalOth: (item.igstbal && item.igstbal.oth ? item.igstbal.oth : undefined),
					igstbalTot: (item.igstbal && item.igstbal.tot ? item.igstbal.tot : undefined),
					cgstbalTx: (item.cgstbal && item.cgstbal.tx ? item.cgstbal.tx : undefined),
					cgstbalIntr: (item.cgstbal && item.cgstbal.intr ? item.cgstbal.intr : undefined),
					cgstbalPen: (item.cgstbal && item.cgstbal.pen ? item.cgstbal.pen : undefined),
					cgstbalFee: (item.cgstbal && item.cgstbal.fee ? item.cgstbal.fee : undefined),
					cgstbalOth: (item.cgstbal && item.cgstbal.oth ? item.cgstbal.oth : undefined),
					cgstbalTot: (item.cgstbal && item.cgstbal.tot ? item.cgstbal.tot : undefined),
					sgstbalTx: (item.sgstbal && item.sgstbal.tx ? item.sgstbal.tx : undefined),
					sgstbalIntr: (item.sgstbal && item.sgstbal.intr ? item.sgstbal.intr : undefined),
					sgstbalPen: (item.sgstbal && item.sgstbal.pen ? item.sgstbal.pen : undefined),
					sgstbalFee: (item.sgstbal && item.sgstbal.fee ? item.sgstbal.fee : undefined),
					sgstbalOth: (item.sgstbal && item.sgstbal.oth ? item.sgstbal.oth : undefined),
					sgstbalTot: (item.sgstbal && item.sgstbal.tot ? item.sgstbal.tot : undefined),
					cessbalTx: (item.cessbal && item.cessbal.tx ? item.cessbal.tx : undefined),
					cessbalIntr: (item.cessbal && item.cessbal.intr ? item.cessbal.intr : undefined),
					cessbalPen: (item.cessbal && item.cessbal.pen ? item.cessbal.pen : undefined),
					cessbalFee: (item.cessbal && item.cessbal.fee ? item.cessbal.fee : undefined),
					cessbalOth: (item.cessbal && item.cessbal.oth ? item.cessbal.oth : undefined),
					cessbalTot: (item.cessbal && item.cessbal.tot ? item.cessbal.tot : undefined)
				};
			});
		},

		_createLiabilityLedgerColumn: function () {
			return [{
				header: "Sr.No",
				key: "oDtlLibSrNo"
			}, {
				header: "Gstin",
				key: "gstin",
				width: 17
			}, {
				header: "Date",
				key: "dt",
				width: 10
			}, {
				header: "Reference No",
				key: "ref_no",
				width: 18
			}, {
				header: "Ledger Used for Discharging Liability",
				key: "disTyp"
			}, {
				header: "Description",
				key: "desc",
				width: 30
			}, {
				header: "Transaction Type(D/C)",
				key: "tr_typ"
			}, {
				header: "Tax (IGST D/C)",
				key: "igstTx"
			}, {
				header: "Interest (IGST D/C)",
				key: "igstIntr"
			}, {
				header: "Penalty (IGST D/C)",
				key: "igstPen"
			}, {
				header: "Fee (IGST D/C)",
				key: "igstFee"
			}, {
				header: "Others (IGST D/C)",
				key: "igstOth"
			}, {
				header: "Total (IGST D/C)",
				key: "igstTot"
			}, {
				header: "Tax (IGST Bal)",
				key: "igstbalTx"
			}, {
				header: "Interest (IGST Bal)",
				key: "igstbalIntr"
			}, {
				header: "Penalty (IGST Bal)",
				key: "igstbalPen"
			}, {
				header: "Fee (IGST Bal)",
				key: "igstbalFee"
			}, {
				header: "Others (IGST Bal)",
				key: "igstbalOth"
			}, {
				header: "Total (IGST Bal)",
				key: "igstbalTot"
			}, {
				header: "Tax (CGST D/C)",
				key: "cgstTx"
			}, {
				header: "Interest (CGST D/C)",
				key: "cgstIntr"
			}, {
				header: "Penalty (CGST D/C)",
				key: "cgstPen"
			}, {
				header: "Fee (CGST D/C)",
				key: "cgstFee"
			}, {
				header: "Others (CGST D/C)",
				key: "cgstOth"
			}, {
				header: "Total (CGST D/C)",
				key: "cgstTot"
			}, {
				header: "Tax (CGST Bal)",
				key: "cgstbalTx"
			}, {
				header: "Interest (CGST Bal)",
				key: "cgstbalIntr"
			}, {
				header: "Penalty (CGST Bal)",
				key: "cgstbalPen"
			}, {
				header: "Fee (CGST Bal)",
				key: "cgstbalFee"
			}, {
				header: "Others (CGST Bal)",
				key: "cgstbalOth"
			}, {
				header: "Total (CGST Bal)",
				key: "cgstbalTot"
			}, {
				header: "Tax (SGST D/C)",
				key: "sgstTx"
			}, {
				header: "Interest (SGST D/C)",
				key: "sgstIntr"
			}, {
				header: "Penalty (SGST D/C)",
				key: "sgstPen"
			}, {
				header: "Fee (SGST D/C)",
				key: "sgstFee"
			}, {
				header: "Others (SGST D/C)",
				key: "sgstOth"
			}, {
				header: "Total (SGST D/C)",
				key: "sgstTot"
			}, {
				header: "Tax (SGST Bal)",
				key: "sgstbalTx"
			}, {
				header: "Interest (SGST Bal)",
				key: "sgstbalIntr"
			}, {
				header: "Penalty (SGST Bal)",
				key: "sgstbalPen"
			}, {
				header: "Fee (SGST Bal)",
				key: "sgstbalFee"
			}, {
				header: "Others (SGST Bal)",
				key: "sgstbalOth"
			}, {
				header: "Total (SGST Bal)",
				key: "sgstbalTot"
			}, {
				header: "Tax (Cess D/C)",
				key: "cessTx"
			}, {
				header: "Interest (Cess D/C)",
				key: "cessIntr"
			}, {
				header: "Penalty (Cess D/C)",
				key: "cessPen"
			}, {
				header: "Fee (Cess D/C)",
				key: "cessFee"
			}, {
				header: "Others (Cess D/C)",
				key: "cessOth"
			}, {
				header: "Total (Cess D/C)",
				key: "cessTot"
			}, {
				header: "Tax (Cess Bal)",
				key: "cessbalTx"
			}, {
				header: "Interest (Cess Bal)",
				key: "cessbalIntr"
			}, {
				header: "Penalty (Cess Bal)",
				key: "cessbalPen"
			}, {
				header: "Fee (Cess Bal)",
				key: "cessbalFee"
			}, {
				header: "Others (Cess Bal)",
				key: "cessbalOth"
			}, {
				header: "Total (Cess Bal)",
				key: "cessbalTot"
			}];
		},

		onChangeEleLedger: function (oEvt) {
			if (oEvt.getSource().getSelectedKey() === "PCB") {
				this.byId("tabLedger2").setVisible(true);
				this.byId("tabCreditLedgerDtl").setVisible(false);
			} else if (oEvt.getSource().getSelectedKey() === "EleLedger") {
				this.byId("tabLedger2").setVisible(false);
				this.byId("tabCreditLedgerDtl").setVisible(true);
			}
		},

		onPressBack: function () {
			var aField = ["tabCreditLedgerDtl", "idTabCashLedger", "idtableLiability", "idElectronicledger", "tabLedger2", "tabReclaim"];

			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));

			this.byId("tabLedger").setVisible(true);
			this._bindDefaultValue();

			// oModel = this.getModel("ViewProperty"),
			// oModel.setProperty("/title", null);
			// oModel.setProperty("/btnBack", false);
			// oModel.refresh(true);
		},

		onGetLedgerCall: function (type) {
			if (!this._dLedger) {
				Fragment.load({
						id: this.getView().getId(),
						name: "com.ey.digigst.fragments.general.Ledger",
						controller: this
					})
					.then(function (oDialog) {
						this._dLedger = oDialog;
						var oFromDp = Fragment.byId(this.getView().getId(), "frDate"),
							oToDp = Fragment.byId(this.getView().getId(), "toDate"),
							oFrTaxDp = Fragment.byId(this.getView().getId(), "frTaxPeriod"),
							oToTaxDp = Fragment.byId(this.getView().getId(), "toTaxPeriod");

						oFromDp.addEventDelegate({
							onAfterRendering: this.setReadonly.bind(this)
						});
						oToDp.addEventDelegate({
							onAfterRendering: this.setReadonly.bind(this)
						});
						oFrTaxDp.addEventDelegate({
							onAfterRendering: this.setReadonly.bind(this)
						});
						oToTaxDp.addEventDelegate({
							onAfterRendering: this.setReadonly.bind(this)
						});
						this.getView().addDependent(oDialog);
						this._loadLedgerDialog(type);
					}.bind(this));
			} else {
				this._loadLedgerDialog(type);
			}
		},

		_loadLedgerDialog: function (type) {
			var today = new Date(),
				frDate = new Date();

			frDate = new Date(frDate.setMonth(frDate.getMonth() - 1));

			this._dLedger.setModel(new JSONModel({
				"title": (type === "C" ? "Get Detailed Ledger Call" : "Get Liability Ledger call"),
				"typeVisi": (type === "C"),
				"type": type,
				"ledgerType": "Cash Ledger",
				"fromDate": this._getFormatDate(frDate),
				"toDate": this._getFormatDate(today),
				"frTaxPeriod": (today.getMonth() + 1).toString().padStart(2, '0') + today.getFullYear(),
				"toTaxPeriod": (today.getMonth() + 1).toString().padStart(2, '0') + today.getFullYear(),
				"minDate": new Date(today.getFullYear(), today.getMonth() - 1, today.getDate()),
				"minPeriod": new Date(today.getFullYear(), today.getMonth()),
				"minFrDate": null,
				"maxToDate": today,
				"maxDate": today,
			}), "Property");
			this._dLedger.open();
		},

		onChangeFrDate: function (oEvent, type) {
			var date = oEvent.getSource().getValue(),
				oModel = this._dLedger.getModel("Property");

			if (type === "D") {
				var toMaxDate = new Date(+date.substr(6) + 1, +date.substr(3, 2) - 1, date.substr(0, 2)),
					frDate = new Date(date.substr(6), +date.substr(3, 2) - 1, date.substr(0, 2)),
					toDate = oModel.getProperty("/toDate"),
					today = new Date();

				if (frDate > new Date(toDate.substr(6), +toDate.substr(3, 2) - 1, toDate.substr(0, 2))) {
					oModel.setProperty("/toDate", date);
				}
				if (new Date(toDate.substr(6), +toDate.substr(3, 2) - 1, toDate.substr(0, 2)) > toMaxDate) {
					oModel.setProperty("/toDate", ('' + toMaxDate.getDate()).padStart(2, 0) + '-' +
						(toMaxDate.getMonth() + 1).toString().padStart(2, 0) + '-' + toMaxDate.getFullYear());
				}
				oModel.setProperty("/maxToDate", (toMaxDate < today ? toMaxDate : today));
				oModel.setProperty("/minDate", frDate);
			} else {
				var toPeriod = oModel.getProperty("/toTaxPeriod");
				if ((date.substr(2) + date.substr(0, 2)) > (toPeriod.substr(2) + toPeriod.substr(0, 2))) {
					oModel.setProperty("/toTaxPeriod", date);
				}
				oModel.setProperty("/minPeriod", new Date(date.substr(2), +date.substr(0, 2) - 1, 1));
			}
			oModel.refresh(true);
		},

		onSelectLedgerType: function (key) {
			var oModel = this._dLedger.getModel("Property"),
				frDate = oModel.getProperty("/fromDate"),
				fromDate = new Date(frDate.substr(6), +frDate.substr(3, 2) - 1, frDate.substr(0, 2)),
				minDate = null;

			switch (key) {
			case "Reversal & Reclaim Ledger":
				minDate = new Date('2023-08-31');
				break;
			case "ITC RCM Ledger":
			case "Negative Liability Ledger":
				minDate = new Date('2024-08-31');
				break;
			}
			if (minDate && minDate > fromDate) {
				var maxToDate = new Date((minDate.getFullYear() + 1), minDate.getMonth(), minDate.getDate()),
					maxDate = (maxToDate > oModel.getProperty("/maxDate") ? oModel.getProperty("/maxDate") : maxToDate);

				oModel.setProperty("/fromDate", minDate.getDate().toString().padStart(2, 0) + '-' +
					(minDate.getMonth() + 1).toString().padStart(2, 0) + '-' + minDate.getFullYear());

				oModel.setProperty("/toDate", (maxDate.getDate().toString().padStart(2, 0) + '-' +
					(maxDate.getMonth() + 1).toString().padStart(2, 0) + '-' + maxDate.getFullYear()));

				oModel.setProperty("/maxToDate", maxDate);
				fromDate = minDate;
			}
			oModel.setProperty("/minDate", fromDate);
			oModel.setProperty("/minFrDate", minDate);
			oModel.refresh(true);
		},

		onCloseLedgerCall: function () {
			this._dLedger.close();
		},

		onInitiateLedgerCall: function () {
			var aIndex = this.byId("tabLedger").getSelectedIndices(),
				oData = this._dLedger.getModel("Property").getProperty("/");

			if (!aIndex.length) {
				MessageBox.warning("Please select atleast one GSTIN");
				return;
			}
			if (oData.type === "C") {
				this._getCashCreditLedgerCall(aIndex, oData);
			} else {
				this._getLiabilityLedgerCall(aIndex, oData);
			}
			this._dLedger.close();
		},

		_getCashCreditLedgerCall: function (aIndex, oData) {
			var oLedgerData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				oLedger = aIndex.map(function (idx) {
					return {
						"entityId": $.sap.entityID,
						"gstin": oLedgerData[idx].gstin
					};
				}),
				payload = {
					"req": {
						"reportType": oData.ledgerType,
						"fromdate": oData.fromDate,
						"toDate": oData.toDate,
						"ledgerDetails": oLedger
					}
				};
			if (["ITC RCM Ledger", "Negative Liability Ledger"].includes(oData.ledgerType)) {
				this.reportDownload(payload, "/aspsapapi/getBulkRcmAndNegetiveDetails.do");
			} else {
				this.reportDownload(payload, "/aspsapapi/getBulkCreditAndCashAndCrRevAndReclaimDetails.do");
			}
		},

		_getLiabilityLedgerCall: function (aIndex, oData) {
			var oLedgerData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				oLedger = aIndex.map(function (idx) {
					return {
						"entityId": $.sap.entityID,
						"gstin": oLedgerData[idx].gstin
					};
				}),
				payload = {
					"req": {
						"from_ret_period": oData.frTaxPeriod,
						"to_ret_period": oData.toTaxPeriod,
						"ledgerDetails": oLedger
					}
				};
			this._reportDownload(payload, "/aspsapapi/getBulkLiabilityLedgerDetails.do");
		},

		_reportDownload: function (payload, url) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					url: url,
					method: "POST",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						this._displayMsgList(oData.respBody);
						this._dMsgList.setModel(new JSONModel(oData), "LedgerResponse");
						this._dMsgList.setTitle("Get Liability Ledger Call");
					} else {
						sap.m.MessageBox.error(oData.hdr.message || oData.resp, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseMsgList: function () {
			var view = this._dMsgList.getModel("MsgView").getProperty("/view");
			if (!view) {
				var oData = this._dMsgList.getModel("LedgerResponse").getProperty("/jobParams");
				if (oData.id) {
					sap.m.MessageBox.success("Initiated Request ID : " + oData.id +
						"\n For Report Type : " + oData.reportType +
						"\n Navigate to \"Reports >> Request Reports\"", {
							styleClass: "sapUiSizeCompact"
						});
				}
			} else {
				this._dialogSaveOpeningBalance(true);
			}
			this._dMsgList.close();
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "I") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onDownloadCashLedgerPdf: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.oTabCashGstin,
					"fr_dt": this.byId("dtCashDtlFromDate").getValue(),
					"to_dt": this.byId("dtCashDtlToDate").getValue()
				}
			};
			this.pdfDownload(payload, "/aspsapapi/getCashLedgerDetailsPDF.do");
		},

		onChangeReclaimDate: function () {
			var frDate = this.byId("dtReclaimFr").getDateValue(),
				toDate = this.byId("dtReclaimTo").getDateValue();

			if (frDate > toDate) {
				this.byId("dtReclaimTo").setDateValue(frDate);
			}
			this.byId("dtReclaimTo").setMinDate(frDate);
		},

		onDetCrRevNReclaimLedger: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "tabCreditLedgerDtl", "idTabCashLedger", "idtableLiability"],
				oModel = this.getModel("ViewProperty");

			if (this._validateAuthToken(obj.status)) {
				return;
			}
			oModel.setProperty("/title", "Electronic Credit Reversal & Re-claimLedger - " + obj.gstin);
			oModel.setProperty("/gstin", obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "reclaimLedger");
			oModel.refresh(true);
			this.byId("tabReclaim").setVisible(true);

			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));
			this.onPressReclaimDetail();
		},

		onPressReclaimDetail: function () {
			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.getModel("ViewProperty").getProperty("/gstin"),
					"fr_dt": this.byId("dtReclaimFr").getValue(),
					"to_dt": this.byId("dtReclaimTo").getValue()
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCreditClaimAndReverseBalDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						data.resp.forEach(function (e, i) {
							e.srNo = i + 1;
						});
						oModel.setData(data.resp);
					}
					this.getView().setModel(oModel, "ReclaimDet");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onExcelDownloadReclaim: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.getModel("ViewProperty").getProperty("/gstin"),
					"fr_dt": this.byId("dtReclaimFr").getValue(),
					"to_dt": this.byId("dtReclaimTo").getValue()
				}
			};
			this.excelDownload(payload, "/aspsapapi/getReclaimDetailedExcelReport.do");
		},

		onPdfDownloadReclaim: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.getModel("ViewProperty").getProperty("/gstin"),
					"fr_dt": this.byId("dtReclaimFr").getValue(),
					"to_dt": this.byId("dtReclaimTo").getValue()
				}
			};
			this.pdfDownload(payload, "/aspsapapi/getReclaimDetailedPdfReport.do");
		},

		onCloseLedgerSave: function () {
			this._dSaveLedger.then(function (oDialog) {
				oDialog.close();
			});
		},

		onGetSaveRcmAction: function (key) {
			switch (key) {
			case "getRcmLedger":
				this._getOpeningBalance("ITC RCM Ledger (Opening Balance)");
				break;
			case "saveRcmLedger":
				this._saveOpeningBalance("RCM");
				break;
			}
		},

		onGetSaveItcReversalAction: function (key) {
			switch (key) {
			case "getItcReversal":
				this._getOpeningBalance("ITC Reversal & Re-Claim Ledger (Opening Balance)");
				break;
			case "saveItcReversal":
				this._saveOpeningBalance("RECLAIM");
				break;
			}
		},

		_getOpeningBalance: function (repType) {
			var oData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				aIndex = this.byId("tabLedger").getSelectedIndices(),
				payload = {
					"req": {
						"reportType": repType
					}
				};
			if (!aIndex.length) {
				MessageBox.warning("Please select atleast one GSTIN");
				return;
			}
			payload.req.ledgerDetails = aIndex.map(function (e) {
				return {
					"entityId": $.sap.entityID,
					"gstin": oData[e].gstin
				};
			});
			this.reportDownload(payload, "/aspsapapi/getOpeningBalanceRcmDetails.do");
		},

		_saveOpeningBalance: function (ledgerType) {
			var aIndex = this.byId("tabLedger").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.warning("Please select atleast one GSTIN");
				return;
			}
			if (!this._dSaveLedger) {
				this._dSaveLedger = this.loadFragment({
					name: "com.ey.digigst.fragments.general.LedgerSave"
				});
			}
			this._dSaveLedger.then(function (oDialog) {
				oDialog.setTitle(this._getSaveOpeningTitle(ledgerType));
				oDialog.setModel(new JSONModel({
					"type": ledgerType
				}), "LedgerType");
				this._saveOpeningBalanceData(ledgerType, oDialog, true);
			}.bind(this));
		},

		onLedgerSaveRefresh: function () {
			this._dSaveLedger.then(function (oDialog) {
				var ledgerType = oDialog.getModel("LedgerType").getProperty("/type");
				this._saveOpeningBalanceData(ledgerType, oDialog, false);
			}.bind(this));
		},

		_saveOpeningBalanceData: function (ledgerType, oDialog, flag) {
			var oData = this.getView().getModel("LedgerTable").getProperty("/resp"),
				aIndex = this.byId("tabLedger").getSelectedIndices(),
				oObject = (flag ? this.getView() : oDialog),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"ledgerType": ledgerType
					}
				};

			payload.req.gstins = aIndex.map(function (e) {
				return oData[e].gstin;
			});
			oObject.setBusy(true);
			oDialog.setModel(new JSONModel([]), "SaveOpeningBal");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/SaveOpeningBalenceForRcmAndReclaimLedger.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (Array.isArray(data.saveOpnBalForRcm) && data.saveOpnBalForRcm.length) {
						data.saveOpnBalForRcm.forEach(function (e) {
							e.isAmended = "S";
						}.bind(this));
					}
					if (flag && data.getRespBody.length) {
						this._displayMsgList(data.getRespBody, ledgerType);
						this._dMsgList.setTitle(this._getSaveOpeningTitle(ledgerType));
					} else {
						this._dialogSaveOpeningBalance(flag);
					}
					oDialog.setModel(new JSONModel(data.saveOpnBalForRcm), "SaveOpeningBal");
					oObject.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					oObject.setBusy(false);
				}.bind(this));
		},

		_getSaveOpeningTitle: function (ledgerType) {
			switch (ledgerType) {
			case "RCM":
				return "Save opening balance for RCM Ledger";
			case "RECLAIM":
				return "Save Opening Balance for Credit Reversal and Re-Claimed Ledger";
			}
		},

		_dialogSaveOpeningBalance: function (flag) {
			this._dSaveLedger.then(function (oDialog) {
				var oData = oDialog.getModel("SaveOpeningBal").getProperty("/");
				if (flag && Array.isArray(oData) && oData.length && !oDialog.isOpen()) {
					oDialog.open();
				}
			});
		},

		onLedgerSaveToGstn: function () {
			MessageBox.confirm("Do you want to initiate Save to GSTN", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				initialFocus: MessageBox.Action.YES,
				onClose: function (sAction) {
					if (sAction === "YES") {
						this._dSaveLedger.then(function (oDialog) {
							var oData = oDialog.getModel("SaveOpeningBal").getProperty("/"),
								ledgerType = oDialog.getModel("LedgerType").getProperty("/type"),
								payload = {
									"req": {
										"ledgerType": ledgerType,
									}
								};
							payload.req.reqData = oData.map(function (e) {
								return {
									"entityId": $.sap.entityID,
									"gstin": e.gstin,
									"igst": e.igst,
									"cgst": e.cgst,
									"sgst": e.sgst,
									"cess": e.cess,
									"isAmended": e.isAmended
								};
							});
							oDialog.setBusy(true);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/SaveToGstinforRcmAndReclaimLedger.do",
									contentType: "application/json",
									data: JSON.stringify(payload)
								})
								.done(function (data, status, jqXHR) {
									MessageBox.success("Saved initiated successfully");
									setTimeout(function () {
										this._saveOpeningBalanceData(ledgerType, oDialog, false);
									}.bind(this), 5000);
								}.bind(this))
								.fail(function (jqXHR, status, err) {
									oDialog.setBusy(false);
								}.bind(this));
						}.bind(this));
					}
				}.bind(this)
			});
		},

		onDateChange: function (oEvent, type) {
			var oFilterModel = this.getModel("ViewProperty"),
				oFilter = oFilterModel.getProperty("/"),
				frDate, toDate;

			switch (type) {
			case "rcm":
				frDate = "frRcm", toDate = "toRcm";
				break;
			case "negLia":
				frDate = "frNegLia", toDate = "toNegLia";
				break;
			}
			if (oFilter[frDate] > oFilter[toDate]) {
				oFilter[toDate] = oFilter[frDate];
			}
			oFilterModel.refres(true);
		},

		/***********************************************************************
		 * ITC RCM Ledger
		 ***********************************************************************/
		onDetRcmLedger: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "tabCreditLedgerDtl", "idTabCashLedger", "idtableLiability"],
				oModel = this.getModel("ViewProperty");

			if (this._validateAuthToken(obj.status)) {
				return;
			}
			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));
			oModel.setProperty("/title", "ITC RCM Ledger - " + obj.gstin);
			oModel.setProperty("/gstin", obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "rcmLedger");
			oModel.refresh(true);
			this._getRcmLedgerDetails();
		},

		onRcmLedgerDetail: function () {
			this._getRcmLedgerDetails();
		},

		_getRcmLedgerDetails: function () {
			var oFilter = this.getModel("ViewProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": 0,
						"pageSize": 50
					},
					"req": {
						"gstin": oFilter.gstin,
						"frdt": this._formatDateSeparator(oFilter.frRcm),
						"todt": this._formatDateSeparator(oFilter.toRcm)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "RcmLedger");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getRcmBalDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.forEach(function (e, i) {
						e.srNo = (i + 1);
					});
					this.getView().setModel(new JSONModel(data.resp), "RcmLedger");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onExcelDownloadRCM: function () {
			var oFilter = this.getModel("ViewProperty").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstin": oFilter.gstin,
						"fr_dt": this._getFormatDate(oFilter.frRcm),
						"to_dt": this._getFormatDate(oFilter.toRcm)
					}
				};
			this.excelDownload(payload, "/aspsapapi/getRCMDetailedScnExcelReport.do");
		},

		/***********************************************************************
		 * Negative Liability Ledger
		 ***********************************************************************/
		onDetNegativeLiability: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("LedgerTable").getObject(),
				aField = ["tabLedger", "tabCreditLedgerDtl", "idTabCashLedger", "idtableLiability"],
				oModel = this.getModel("ViewProperty");

			if (this._validateAuthToken(obj.status)) {
				return;
			}
			aField.forEach(function (tab) {
				this.byId(tab).setVisible(false);
			}.bind(this));
			oModel.setProperty("/title", "Negative Liability Ledger - " + obj.gstin);
			oModel.setProperty("/gstin", obj.gstin);
			oModel.setProperty("/btnBack", true);
			oModel.setProperty("/table", "negLiability");
			oModel.refresh(true);
			this._getNegativeLiability();
		},

		onNegativeLiabilityDetail: function () {
			this._getNegativeLiability();
		},

		_getNegativeLiability: function () {
			var oFilter = this.getModel("ViewProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": 0,
						"pageSize": 50
					},
					"req": {
						"gstin": oFilter.gstin,
						"from_date": this._getFormatDate(oFilter.frNegLia),
						"to_date": this._getFormatDate(oFilter.toNegLia)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "NegativeLiability");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getNegativeBalDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.forEach(function (e, i) {
						e.srNo = (i + 1);
					});
					this.getView().setModel(new JSONModel(data.resp), "NegativeLiability");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onExcelDownloadNegLiability: function () {
			var oFilter = this.getModel("ViewProperty").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstin": oFilter.gstin,
						"fr_dt": this._getFormatDate(oFilter.frNegLia),
						"to_dt": this._getFormatDate(oFilter.toNegLia)
					}
				};
			this.excelDownload(payload, "/aspsapapi/getNegativeLedgerDetailscnExcelReport.do");
		},

		_validateAuthToken: function (status) {
			if (["I", "Inactive"].includes(status)) {
				MessageBox.error("Auth Token is Inactive, Please Activate");
				return true;
			}
			return false;
		},

		_visiDetIcon: function (igst, cgst, sgst) {
			return (igst !== null && igst !== undefined && cgst !== null && cgst !== undefined && sgst !== null && sgst !== undefined);
		},

		_setReadOnly: function (id, value, maxDate, minDate) {
			this.byId(id).setMinDate(minDate || null);
			this.byId(id).setMaxDate(maxDate || null);
			this.byId(id).setDateValue(value || null);
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		setReadonly: function (oEvent) {
			oEvent.srcControl.$().find("input").attr("readonly", true);
		},

		_getFormatDate: function (date) {
			return date.getDate().toString().padStart(2, '0') + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + "-" + date.getFullYear();
		},

		_formatDateSeparator: function (date) {
			return date.getDate().toString().padStart(2, '0') + "/" + (date.getMonth() + 1).toString().padStart(2, '0') + "/" + date.getFullYear();
		}
	});
});