sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ITC04vsEWB", {

		onInit: function () {
			var vDate = new Date();
			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);
			this._bindReqIdFilter();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				// this.byId("idInitiateReconList2A").selectAll();
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this._getAllFiscalYear(),
						this._getUserId()
					])
					.then(function (values) {
						this.taxPeriodFirst(values[0].finYears[0].fullFy);
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
				this.byId("idSplitDtl2A").setVisible(true);
				this.byId("idRequestIDwisePage2A").setVisible(false);
			}
		},

		_bindReqIdFilter: function () {
			var date = new Date();
			this.byId("idRequestIDwisePage2A").setModel(new JSONModel({
				"frTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"status": ""
			}), "FilterModel");
		},

		_getAllFiscalYear: function () {
			return new Promise(function (resolve, reject) {
				this.getView().setModel(new JSONModel([]), "oFyModelIVE");
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data.resp), "oFyModelIVE");
						}
						resolve(data.resp);
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
					"screenName": "itc04Ewb"
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
						this.byId("idRequestIDwisePage2A").setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onFnYear: function (flag) {
			this.taxPeriod(flag);
		},

		taxPeriod: function (flag) {
			var id = (flag === "P" ? "slPFinancialyear" : "slSFinancialyear"),
				FY = this.byId(id).getSelectedItem().getText(),
				payload = {
					"req": {
						"fy": FY,
					}
				};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getITC04taxPeriods.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp.taxPeriodList), "ITC04VEtaxPeriod");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		taxPeriodFirst: function (Fy) {
			var payload = {
				"req": {
					"fy": Fy
				}
			};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getITC04taxPeriods.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel(data.resp.taxPeriodList);
					this.getView().setModel(oModel, "ITC04VEtaxPeriod");
					this.ListeBinding();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		taxperiod: function (key) {
			switch (key) {
			case "Q1":
				return 13;
			case "Q2":
				return 14;
			case "Q3":
				return 15;
			case "Q4":
				return 16;
			case "H1":
				return 17;
			case "H2":
				return 18;
			}
		},

		onPressGoForGSTIN2A: function () {
			var From = this.taxperiod(this.byId("slPQTaxPeriod").getSelectedKey()),
				To = this.taxperiod(this.byId("slPQTaxPeriod1").getSelectedKey());

			if (From > To) {
				MessageBox.error("From Taxperiod can not be Greater than To Taxperiod");
				return;
			}
			this.ListeBinding();
		},

		onPressClear: function () {
			this.byId("idReconType").setSelectedKey("GSTIN Submitted Data");
			this.byId("slPFinancialyear").setSelectedKey("2022");
			this.byId("slPQTaxPeriod").setSelectedKey("H1");
			this.byId("slPQTaxPeriod1").setSelectedKey("H1");
			sap.ui.core.BusyIndicator.show(0);
			this.taxPeriodFirst(this.byId("slPFinancialyear").getSelectedItem().getText());
		},

		ListeBinding: function () {
			var vTaxPeriod = this.taxperiod(this.byId("slPQTaxPeriod").getSelectedKey()),
				vTaxPeriod1 = this.taxperiod(this.byId("slPQTaxPeriod1").getSelectedKey()),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"criteria": this.byId("idReconType").getSelectedKey(),
						"fy": this.byId("slPFinancialyear").getSelectedItem().getText(),
						//"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey()
						"fromTaxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"toTaxPeriod": vTaxPeriod1 + this.byId("slPFinancialyear").getSelectedKey(),
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "GSTIN2A");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDataForReconEwbVsItc04Summary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp.det), "GSTIN2A");
						this.getView().byId("idInitiateReconList2A").selectAll();
						this.getView().byId("checkboxID").setSelected(true);
						this.TableBinding();
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSelectionChange1: function () {
			this.TableBinding();
		},

		onSelectallGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList2A").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList2A").selectAll();
			}
			this.TableBinding();
		},

		TableBinding: function () {
			var oContext = this.byId("idInitiateReconList2A").getSelectedContexts(),
				vTaxPeriod = this.taxperiod(this.byId("slPQTaxPeriod").getSelectedKey()),
				vTaxPeriod1 = this.taxperiod(this.byId("slPQTaxPeriod1").getSelectedKey()),
				aGSTIN = oContext.map(function (e) {
					return e.getObject().gstin;
				}),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"criteria": this.byId("idReconType").getSelectedKey(),
						"dataSecAttrs": {
							"GSTIN": aGSTIN
						},
						"gstin": aGSTIN,
						"fy": this.byId("slPFinancialyear")._getSelectedItemText(),
						//"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey()
						"fromTaxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"toTaxPeriod": vTaxPeriod1 + this.byId("slPFinancialyear").getSelectedKey()
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/ewbVsItc04SummaryInitiateRecon.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "ewbVsItc04Summary");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onChangeDtReconReqId: function (oEvent) {
			var frDate = oEvent.getSource().getDateValue(),
				toDate = this.byId("toReconTaxPriod").getDateValue();

			if (frDate > toDate) {
				this.byId("toReconTaxPriod").setDateValue(frDate);
			}
			this.byId("toReconTaxPriod").setMinDate(frDate);
		},

		onRequestIdWise: function () {
			this.getView().byId("idRequestIDwisePage2A").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			this.onSearchReqIdWise();
		},

		onClearReqIdWise: function () {
			this._bindReqIdFilter();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function () {
			var oFilterData = this.byId("idRequestIDwisePage2A").getModel("FilterModel").getProperty("/"),
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
					url: "/aspsapapi/getEwbVsItc04ReportRequestStatusFilter.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data), "ReqWiseData2A");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressRequestIDwiseBack: function () {
			this.byId("idSplitDtl2A").setVisible(true);
			this.byId("idRequestIDwisePage2A").setVisible(false);
		},

		fnIntiniateBtnPress2A: function () {
			var selItems = this.byId("idInitiateReconList2A").getSelectedContextPaths();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (sAction) {
					if (sAction === "OK") {
						this.onPressInitiate();
					}
				}.bind(this)
			});

		},

		onPressInitiate: function () {
			var oContext = this.byId("idInitiateReconList2A").getSelectedContexts(),
				vTaxPeriod = this.taxperiod(this.byId("slPQTaxPeriod").getSelectedKey()),
				vTaxPeriod1 = this.taxperiod(this.byId("slPQTaxPeriod1").getSelectedKey()),
				aGSTIN = oContext.map(function (e) {
					return e.getObject().gstin;
				}),
				payload = {
					"req": {
						"entityId": [$.sap.entityID],
						"fy": this.byId("slPFinancialyear").getSelectedItem().getText(),
						//"taxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"criteria": this.byId("idReconType").getSelectedKey(),
						"gstins": aGSTIN,
						"addReport": [
							"DropOutRecords",
							"ConsolidatedReport"
						],
						"fromTaxPeriod": vTaxPeriod + this.byId("slPFinancialyear").getSelectedKey(),
						"toTaxPeriod": vTaxPeriod1 + this.byId("slPFinancialyear").getSelectedKey()
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/EwbVsItc04InitiateRecon.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
						title: "Initiate Matching Successfully done"
					});
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onConfigExtractPress2A1: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReqWiseData2A").getObject(),
				payload = {
					"req": {
						"configId": obj.requestId
					}
				};
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}
			this._getGstr2a1.open();
			this._getGstr2a1.setBusy(true);
			this._getGstr2a1.setModel(new JSONModel([]), "DownloadDocument");
			this._getGstr2a1.setModel(new JSONModel(obj), "RequestIdProd");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/ewbvsItc04DownloadIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this._getGstr2a1.setModel(new JSONModel(data.resp), "DownloadDocument");
					}
					this._getGstr2a1.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._getGstr2a1.setBusy(false);
				}.bind(this));

		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onPressGSTIN: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReqWiseData2A").getObject()
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(new JSONModel(obj.gstins), "gstins2A");
			this._oGstinPopover.openBy(oEvent.getSource());
		},

		onFragDownload: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("DownloadDocument").getObject(),
				reqId = this._getGstr2a1.getModel("RequestIdProd").getProperty("/requestId");
			sap.m.URLHelper.redirect("/aspsapapi/eWBVsItc04DownloadDocument.do?configId=" + reqId + "&reportType=" + obj.reportName);
		}
	});
});