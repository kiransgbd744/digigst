sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/export/Spreadsheet",
	"sap/ui/export/library"
], function (BaseController, MessageBox, Formatter, JSONModel, Spreadsheet, exportLibrary) {
	"use strict";

	var EdmType = exportLibrary.EdmType;
	return BaseController.extend("com.ey.digigst.controller.imGetInwardEINV", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.imGetInwardEINV
		 */
		onInit: function () {
			this.byId("dtProcessed").addEventDelegate({
				onAfterRendering: function (evt) {
					evt.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
			this.byId("dtSummary").addEventDelegate({
				onAfterRendering: function (evt) {
					evt.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.imGetInwardEINV
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.imGetInwardEINV
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._setDefaultFilter();
			}
		},

		_setDefaultFilter: function () {
			var today = new Date(),
				obj = {
					"period": "" + (today.getMonth() + 1).toString().padStart(2, '0') + today.getFullYear(),
					"maxDate": new Date(),
					"supplyType": [],
					"gstins": []
				};
			this.getView().setModel(new JSONModel(obj), "FilterModel");
			this.getView().setModel(new JSONModel({
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 100,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false,
				"visiSumm": false,
				"flag": false
			}), "Property");
			this.onSearch();
		},

		onSwitchEInv: function () {
			this.onSearch();
		},

		onPressClear: function () {
			this._setDefaultFilter();
		},

		onSearch: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oPropData = this.getView().getModel("Property").getProperty("/"),
				vId = (!oPropData.flag ? "tabEntityInward" : "tabInwardDetails"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": this.removeAll(oFilterData.gstins),
						"taxPeriod": oFilterData.period,
						"supplyType": this.removeAll(oFilterData.supplyType),
						"type": (!oPropData.flag ? "EntityLevel" : "DetailedLevel")
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/inwardEinvoiceEntitySummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.byId(vId).setModel(new JSONModel(data.resp), "ProcessedRecord");
					this._eInvPagination(data.hdr, vId);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_eInvPagination: function (hdr, vId) {
			var oModel = this.getView().getModel("Property"),
				vTotal = Math.ceil(hdr.totalCount / oModel.getProperty("/pgSize")),
				vPageNo = (vTotal ? hdr.pageNum + 1 : 0);

			oModel.setProperty("/pageNo", vPageNo);
			oModel.setProperty("/pgTotal", vTotal);
			oModel.setProperty("/ePageNo", vTotal > 1);
			oModel.setProperty("/bFirst", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onPaginationEInv: function (btn) {
			var oModel = this.getView().getModel("Property");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo", 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo", oModel.getProperty("/pgTotal"));
				break;
			}
			oModel.refresh(true);
			this.onSearch();
		},

		onSubmitPaginationEInv: function () {
			this.onSearch();
		},

		onPressBack: function () {
			this.getView().getModel("Property").setProperty("/visiSumm", false);
		},

		onUploadGetInwardEInv: function () {
			var oBundle = this.getResourceBundle(),
				oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oUploader = this.byId("fuGetInwardEInv"),
				vIdx = this.byId("rgbFileType").getSelectedIndex();

			if (oUploader.getValue() === "") {
				sap.m.MessageBox.information(oBundle.getText("gstr2aSelectFile"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			switch (vIdx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/inwardEinvoiceFileUploadDocument.do");
				break;
			}
			sap.ui.core.BusyIndicator.show(0);
			oUploader.upload();
		},

		onEInvUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuGetInwardEInv").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				if (sResponse.hdr.message) {
					sap.m.MessageBox.error(sResponse.hdr.message, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.error(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}
		},

		onInitiateGetInwardEInv: function () {
			var aIndex = this.byId("tabEntityInward").getSelectedIndices(),
				oData = this.byId("tabEntityInward").getModel("ProcessedRecord").getProperty("/"),
				taxPeriod = this.getView().getModel("FilterModel").getProperty("/period"),
				aAuthToken = [],
				payload = {
					"req": []
				};

			if (!aIndex.length) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			}

			aIndex.forEach(function (e) {
				payload.req.push({
					"gstin": oData[e].gstin,
					"ret_period": taxPeriod,
					"gstr2aSections": ["B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"]
				});
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/GetCallInwardIrnList.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._displayMsgList(data.resp);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onGstinEInvDetails: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				taxPeriod = this.getView().getModel("FilterModel").getProperty("/period"),
				oHeader = {
					"gstin": obj.gstin,
					"authToken": obj.authToken,
					"taxPeriod": taxPeriod,
					"irnStatus": [],
					"maxDate": new Date()
				};

			this.getView().setModel(new JSONModel(oHeader), "HeaderValue");
			this.getView().getModel("Property").setProperty("/visiSumm", true);
			this._getGstinLevelData();
		},

		_getGstinLevelData: function () {
			var oHeader = this.getView().getModel("HeaderValue").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": oHeader.gstin,
						"taxPeriod": oHeader.taxPeriod,
						"irnStatus": this.removeAll(oHeader.irnStatus)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/inwardEinvoiceGstinLevelData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var aField = ["Invoice", "Credit", "Debit"],
						oData = [];
					aField.forEach(function (f) {
						var obj = {
							"supplyType": (f !== "Invoice" ? (f === "Credit" ? "Credit Note" : "Debit Note") : "Invoice"),
							"count": 0,
							"taxableVal": 0,
							"igst": 0,
							"cgst": 0,
							"sgst": 0,
							"cess": 0,
							"totalTax": 0,
							"totInvVal": 0,
							"level": "L1",
							"level2": []
						};

						data.resp[f].forEach(function (item) {
							obj.count += +item.count;
							obj.taxableVal += +item.taxableVal;
							obj.igst += +item.igst;
							obj.cgst += +item.cgst;
							obj.sgst += +item.sgst;
							obj.cess += +item.cess;
							obj.totalTax += +item.totalTax;
							obj.totInvVal += +item.totInvVal;
							obj.level2.push(item);
						});
						oData.push(obj);
					});
					this.getView().setModel(new JSONModel(oData), "SummaryRecord");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onGstnSearch: function () {
			this._getGstinLevelData();
		},

		onGstnClear: function () {
			var oGstin = this.getView().getModel("IsdGstin").getProperty("/"),
				oModel = this.getView().getModel("HeaderValue"),
				dt = new Date();

			oModel.setProperty("/gstin", oGstin[0].gstin);
			oModel.setProperty("/taxPeriod", "" + (dt.getMonth() + 1) + dt.getFullYear());
			oModel.setProperty("/irnStatus", []);
			oModel.refresh(true);
			this._getGstinLevelData();
		},

		onPressDetailStatus: function (oEvent) {
			if (!this._dStatus) {
				this._dStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.inwardEINV.InwardEInvSumm", this);
				this.getView().addDependent(this._dStatus);
			}
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();
			this._dStatus.open();
			this._getInwardEInvStatus(obj.gstin);
		},

		_getInwardEInvStatus: function (gstin) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [gstin],
						"taxPeriod": oFilterData.period,
						"supplyType": this.removeAll(oFilterData.supplyType),
						"type": "SectionLevel"
					}
				};

			this._dStatus.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/inwardEinvoiceEntitySummary.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._dStatus.setBusy(false);
					var aField = ["All", "B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"];
					aField.forEach(function (e) {
						data.resp["flag" + e] = false;
					});
					this._dStatus.setModel(new JSONModel([data.resp]), "EInvInwardSummary");
					this._dStatus.setModel(new JSONModel(payload.req), "FilterData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dStatus.setBusy(false);
				}.bind(this));
		},

		onCheckedStatus: function (oEvent, type) {
			var aField = ["B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"],
				oModel = this._dStatus.getModel("EInvInwardSummary"),
				oData = oModel.getProperty("/0"),
				value = oEvent.getParameter("selected");

			if (type === "all") {
				aField.forEach(function (e) {
					oData["flag" + e] = value;
				});
			} else {
				var flag = true;
				aField.forEach(function (e) {
					flag = flag && oData["flag" + e];
				});
				oData.flagAll = flag;
			}
			oModel.refresh(true);
		},

		onCloseInwardStatus: function (btn) {
			if (btn === "C") {
				this._dStatus.close();
			} else {
				var aField = ["B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"],
					data = this._dStatus.getModel("EInvInwardSummary").getProperty("/0"),
					obj = {
						"gstin": data.gstin,
						"ret_period": this._dStatus.getModel("FilterData").getProperty("/taxPeriod"),
						"gstr2aSections": []
					};

				aField.forEach(function (e) {
					if (data["flag" + e]) {
						obj.gstr2aSections.push(e);
					}
				});
				if (!obj.gstr2aSections.length) {
					MessageBox.error("Please select atleast one Supply Type");
					return;
				}
				this._dStatus.setBusy(true);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/GetCallInwardIrnList.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": [obj]
						})
					})
					.done(function (data, status, jqXHR) {
						this._dStatus.setBusy(false);
						this._displayMsgList(data.resp);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this._dStatus.setBusy(false);
					}.bind(this));
			}
		},

		onDownloadReport: function (oEvent) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.byId("tabEntityInward").getModel("ProcessedRecord").getProperty("/"),
				aIndex = this.byId("tabEntityInward").getSelectedIndices(),
				key = oEvent.getParameter("item").getProperty("key");

			if (key === "table") {
				this._downloadTableData();
				return;
			}
			if (!aIndex.length) {
				MessageBox.error("Please select atleast one GSTIN to download report");
				return;
			}
			var aGstin = aIndex.map(function (e) {
					return oData[e].gstin;
				}),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"returnPeriod": oFilterData.period,
						"supplyType": this.removeAll(oFilterData.supplyType),
						"gstins": aGstin,
						"type": "SUMMARY"
					}
				};

			switch (key) {
			case "summary":
				var url = "/aspsapapi/getInwardIrnSummaryReportData.do";
				break;
			case "json":
				url = "/aspsapapi/downloadJsonReport.do";
				break;
			case "detail":
				url = "/aspsapapi/getInwardIrnDetailedReportData.do";
				break;
			case "nested":
				url = "/aspsapapi/getEnvoicesNestedReportDownload.do";
				break;
			}
			this.reportDownload(payload, url);
		},

		_downloadTableData: function () {
			var oRowBinding = this.byId("tabEntityInward").getBinding("rows"),
				aCols = this._createColumnConfig(),
				oSettings = {
					workbook: {
						columns: aCols,
						hierarchyLevel: 'Level'
					},
					dataSource: oRowBinding,
					fileName: "Inward E-invoice_Table Data_" + this.getDateTimeStamp() + ".xlsx",
					worker: false
				};

			var oSheet = new Spreadsheet(oSettings);
			oSheet.build().finally(function () {
				oSheet.destroy();
			});
		},

		_createColumnConfig: function () {
			var aCols = [];

			aCols.push({
				label: "GSTIN",
				property: ["gstin"],
				type: EdmType.String
			});

			aCols.push({
				label: "Status",
				property: 'status',
				type: EdmType.String
			});

			aCols.push({
				label: "Count of Supplier GSTIN",
				property: "countSuppGstn",
				type: EdmType.Number
			});

			aCols.push({
				label: "Count of E-Invoice",
				property: "countEinv",
				type: EdmType.Number
			});

			aCols.push({
				label: "Active E-Invoice count",
				property: "activeEinv",
				type: EdmType.Number
			});

			aCols.push({
				label: "Cancelled E-Invoice count",
				property: "canclEinv",
				type: EdmType.Number
			});

			aCols.push({
				label: "Total invoice Amount",
				property: "totlInvAmt",
				type: EdmType.Number,
			});
			return aCols;
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "I") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onDownloadGstnReport: function (oEvent) {
			var oHeader = this.getView().getModel("HeaderValue").getProperty("/"),
				key = oEvent.getParameter("item").getProperty("key");

			if (key === "table") {
				this._downloadTableData();
				return;
			}
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": [oHeader.gstin],
					"returnPeriod": oHeader.taxPeriod,
					"supplyType": ["B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"],
					"irnStatus": this.removeAll(oHeader.irnStatus),
					"type": "SUMMARY"
				}
			};

			switch (key) {
			case "summary":
				var url = "/aspsapapi/getInwardIrnSummaryReportData.do";
				break;
			case "json":
				url = "/aspsapapi/downloadJsonReport.do";
				break;
			case "detail":
				url = "/aspsapapi/getInwardIrnDetailedReportData.do";
				break;
			case "nested":
				url = "/aspsapapi/getEnvoicesNestedReportDownload.do";
				break;
			}
			this.reportDownload(payload, url);
		},

		onDownloadTabData: function () {
			var oHeader = this.getView().getModel("HeaderValue").getProperty("/"),
				data = [{
					sheet: "Inward E-invoice",
					cols: this._createGetInwardColumnConfig(),
					rows: this._getInwardDataSource()
				}];
			this.xlsxTabDataDownload(oHeader.gstin + "_" + oHeader.taxPeriod + "_InwE-INV_TableData_" + this.getDateTimeStamp() + ".xlsx", data);
		},

		_getInwardDataSource: function () {
			var data = this.getModel("SummaryRecord").getProperty("/"),
				aData = [];
			data.forEach(function (e) {
				var obj = $.extend(true, {}, e);
				delete obj.level2;
				obj.styles = {
					type: "row"
				}
				aData.push(obj);
				e.level2.forEach(function (d) {
					aData.push(d);
				}.bind(this));
			}.bind(this));
			return aData;
		},

		_createGetInwardColumnConfig: function () {
			var aCols = [];

			aCols.push({
				header: "Table",
				key: "supplyType",
				width: 12
			});

			aCols.push({
				header: "Count",
				key: "count",
				width: 12
			});

			aCols.push({
				header: "Taxable Value",
				key: "taxableVal",
				width: 14
			});

			aCols.push({
				header: "IGST",
				key: "igst",
				width: 14
			});

			aCols.push({
				header: "CGST",
				key: "cgst",
				width: 14
			});

			aCols.push({
				header: "SGST",
				key: "sgst",
				width: 14
			});

			aCols.push({
				header: "Cess",
				key: "cess",
				width: 14
			});

			aCols.push({
				header: "Total Tax",
				key: "totalTax",
				width: 14
			});

			aCols.push({
				header: "Total Invoice Value",
				key: "totInvVal",
				width: 17
			});
			return aCols;
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.imGetInwardEINV
		 */
		//	onExit: function() {
		//
		//	}
	});
});