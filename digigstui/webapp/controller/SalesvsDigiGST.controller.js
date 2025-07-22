sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.SalesvsDigiGST", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.SalesvsDigiGST
		 */
		onInit: function () {
			var vDate = new Date(),
				vDateD = new Date();
			this._setDateProperty("idInitiateReconPeriodTax2A", vDate, vDate, new Date("2017", "05", "01"));
			this._setDateProperty("idInitiateReconPeriodTax12A", vDate, vDate, null);

			vDateD.setDate(vDateD.getDate() - 9);
			this._setDateProperty("iFromDate", vDateD, vDate, null);
			this._setDateProperty("iToDate", vDate, vDate, vDateD);

			this._setDateProperty("frReconTaxPriod", vDate, vDate, null);
			this._setDateProperty("toReconTaxPriod", vDate, vDate, vDate);
			this._bindFilterData();
		},

		_bindFilterData: function () {
			var date = new Date();
			this.byId("idRequestIDwisePage2A").setModel(new JSONModel({
				"frTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"toTaxPeriod": ('' + (date.getMonth() + 1)).padStart(2, '0') + date.getFullYear(),
				"initiatedBy": [],
				"status": ""
			}), "FilterModel");
		},

		onBeforeRendering: function () {
			this.byId("idSplitDtl2A").setVisible(true);
			this.byId("idRequestIDwisePage2A").setVisible(false);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.SalesvsDigiGST
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.onPressGoForGSTIN2A(),
						this._getUserId(),
						this.onFileStauts()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		_getUserId: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"entityId": $.sap.entityID,
					"screenName": "gstr1Psd"
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

		onDownloadSR: function () {
			sap.m.URLHelper.redirect("excel/DownloadSR.xlsx", true);
		},

		onSearch: function () {
			this.onFileStauts(true);
		},

		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("iFromDate")) {
				var vDatePicker = "iToDate";
			} else if (oEvent.getSource().getId().includes("InDateFrom")) {
				var vDatePicker = "InDateTo";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		onFromDateChangeInt: function (oevt) {
			this.byId("idInitiateReconPeriodTax12A").setDateValue(oevt.getSource().getDateValue());
		},

		onPressRequestIDwiseBack: function () {
			this.byId("idSplitDtl2A").setVisible(true);
			this.byId("idRequestIDwisePage2A").setVisible(false);
		},

		handleUploadPress: function (oEvent) {
			var oFileUploader = this.byId("uceinvFileUpload");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			oFileUploader.setUploadUrl("/aspsapapi/gstr1SalesUploadDocuments.do");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		handleUploadComplete: function (oEvent) {
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			sap.ui.core.BusyIndicator.hide();
			if (sResponse.hdr.status === "S") {
				this.getView().byId("uceinvFileUpload").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onPressGoForGSTIN2A: function (oEvent) {
			return new Promise(function (resolve, reject) {
				this.byId("searchId").setValue("");
				var payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"toReturnPeriod": this.byId("idInitiateReconPeriodTax12A").getValue(),
						"fromReturnPeriod": this.byId("idInitiateReconPeriodTax2A").getValue(),
						"criteria": "ReturnPeriodWise"
					}
				};
				this.getView().setModel(new JSONModel([]), "GSTIN2A");
				$.ajax({
						method: "POST",
						url: "/aspsapapi/salesRegisterGstinStatus.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = this.getView().getModel("GSTIN2A");
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data.resp.det);
							this.getView().byId("idInitiateReconList2A").selectAll();
						}
						oModel.refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
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

		onReqPress: function () {
			this.byId("idSplitDtl2A").setVisible(false);
			this.byId("idRequestIDwisePage2A").setVisible(true);
			this.onSearchReqIdWise();
		},

		onClearReqIdWise: function () {
			this._bindFilterData();
			this.onSearchReqIdWise();
		},

		onSearchReqIdWise: function (oEvent) {
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
			this.getView().byId("idRequestIDwisePage2A").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/salesRegisterRequestStatusFilter.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					// data.resp.forEach(function (e) {
					// 	if (e.toDocDate !== undefined && e.fromDocDate !== undefined) {
					// 		e.toDocDate = e.toDocDate.split(" ")[0];
					// 		e.fromDocDate = e.fromDocDate.split(" ")[0];
					// 	}
					// });
					this.getView().setModel(new JSONModel(data.resp), "ReqWiseData2A");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressGSTIN: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReqWiseData2A").getObject();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(new JSONModel(obj.gstins), "gstins2A");
			this._oGstinPopover.openBy(oEvent.getSource());
		},

		onConfigExtractPress2A1: function (oEvent) {
			if (!this._dReqIdWiseRpt) {
				this._dReqIdWiseRpt = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._dReqIdWiseRpt);
			}
			var obj = oEvent.getSource().getBindingContext("ReqWiseData2A").getObject(),
				payload = {
					"req": {
						"configId": obj.requestId
							//"reconType": obj.reconType
					}
				};
			this._dReqIdWiseRpt.open();
			this._dReqIdWiseRpt.setBusy(true);
			this._dReqIdWiseRpt.setModel(new JSONModel(obj), "PropReqId");
			this._dReqIdWiseRpt.setModel(new JSONModel([]), "DownloadDocument");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/salesRegisterDownloadIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = this._dReqIdWiseRpt.getModel("DownloadDocument");
					if (data.hdr.status === "S") {
						oModel.setProperty("/", data.resp);
					}
					oModel.refresh(true);
					this._dReqIdWiseRpt.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dReqIdWiseRpt.setBusy(false);
				}.bind(this));
		},

		onCloseDialogDow: function () {
			this._dReqIdWiseRpt.close();
		},

		onFragDownload: function (oEvent) {
			var rptName = oEvent.getSource().getBindingContext("DownloadDocument").getObject().reportName,
				reqId = this._dReqIdWiseRpt.getModel("PropReqId").getProperty("/requestId");
			sap.m.URLHelper.redirect("/aspsapapi/salesRegisterDownloadDocument.do?configId=" + reqId + "&reportType=" + rptName, false);
		},

		onFileStauts: function (flag) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"dataRecvFrom": this.byId("iFromDate").getValue(),
						"dataRecvTo": this.byId("iToDate").getValue(),
						"fileType": "SalesRegisterUpload",
						"dataType": "outward"
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				this.getView().setModel(new JSONModel([]), "FileStatus");
				$.ajax({
						method: "POST",
						url: "/aspsapapi/salesRegisterFileStatus.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = this.getView().getModel("FileStatus");
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data);
						}
						oModel.refresh(true);
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

		onInitiPress: function () {
			var selItems = this.byId("idInitiateReconList2A").getSelectedContextPaths();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						this.onInitiPress12();
					}
				}.bind(this)
			});
		},

		onInitiPress12: function () {
			var aContext = this.getView().byId("idInitiateReconList2A").getSelectedContexts(),
				aGstin = aContext.map(function (e) {
					return e.getObject().gstin;
				}),
				payload = {
					"req": {
						"entityId": +$.sap.entityID,
						"gstins": aGstin,
						"toReturnPeriod": this.byId("idInitiateReconPeriodTax12A").getValue(),
						"fromReturnPeriod": this.byId("idInitiateReconPeriodTax2A").getValue(),
						"criteria": "ReturnPeriodWise",
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/salesRegisterInitiateRecon.do",
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

		onSelectallGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList2A").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList2A").selectAll();
			}
			// this.onInitiateRecon();
		},

		onPressFileStatusDownload: function (oEvent, count, type, status) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("FileStatus").getObject();
			if (count === 0 || !count) {
				sap.m.MessageBox.information("No data available to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var aData = {
				"req": {
					"fileId": obj.id,
					"type": type,
					"reportCateg": "Processed Summary",
					"dataType": "Outward",
					"status": null
				}
			};
			this.reportDownload(aData, "/aspsapapi/gstr1SalesRegisterDownload.do");
		},

		onSearchGstins: function (oEvent) {
			var oBinding = this.byId("idInitiateReconList2A").getBinding("items"),
				sQuery = oEvent.getSource().getValue(),
				aFilter = [];
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
			}
			// filter binding
			oBinding.filter(aFilter);
			this.getView().byId("idInitiateReconList2A").removeSelections();
			this.byId("checkboxID").setSelected(false);
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				this.getView().byId("idInitiateReconList2A").selectAll();
				this.onSelectionChange1();
			}
			this.getView().setModel(new JSONModel([]), "GSTR3B");
		}
	});
});