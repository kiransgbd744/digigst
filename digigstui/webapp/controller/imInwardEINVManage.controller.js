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
	return BaseController.extend("com.ey.digigst.controller.imInwardEINVManage", {
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.imInwardEINVManage
		 */
		onInit: function () {
			this._setReadOnlyDate("iFromDate");
			this._setReadOnlyDate("iToDate");
			this._setReadOnlyDate("iFromPeriod");
			this._setReadOnlyDate("iToPeriod");
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.imInwardEINVManage
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.imInwardEINVManage
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._setDefaultFilter();
			}
		},

		_setDefaultFilter: function () {
			var today = new Date(),
				vDate = new Date(),
				vPeriod = new Date();

			vDate.setDate(vDate.getDate() - 90);
			vPeriod.setMonth(vPeriod.getMonth() - 2);

			var obj = {
				"criteria": "taxPeriod",
				"frDate": this._getDate(vDate),
				"toDate": this._getDate(today),
				"frPeriod": "" + (vPeriod.getMonth() + 1).toString().padStart(2, '0') + vPeriod.getFullYear(),
				"toPeriod": "" + (today.getMonth() + 1).toString().padStart(2, '0') + today.getFullYear(),
				"minDate": vDate,
				"maxDate": today,
				"minPeriod": vPeriod,
				"maxPeriod": today,
				"maxDt": new Date(),
				"supplyType": [],
				"irnStatus": [],
				"gstins": [],
				"docNum": null,
				"irn": null,
				"vendrGstin": null
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
				"displayTab": "TableLayout"
			}), "Property");
			this.onSearch();
		},

		onChangeDateValue: function (oEvent, type) {
			var oModel = this.getView().getModel("FilterModel");
			if (type === "D") {
				var date = oModel.getProperty("/frDate"),
					minDate = new Date(date.substr(0, 4), +date.substr(5, 2) - 1, date.substr(8, 2)),
					maxDate = new Date(date.substr(0, 4), +date.substr(5, 2) - 1, +date.substr(8, 2) + 90);

				if (maxDate > new Date()) {
					maxDate = new Date();
				}
				oModel.setProperty("/minDate", minDate);
				oModel.setProperty("/maxDate", maxDate);

				if (maxDate < new Date(oModel.getProperty("/toDate")) ||
					minDate > new Date(oModel.getProperty("/toDate"))) {
					oModel.setProperty("/toDate", this._getDate(maxDate));
				}
			} else {
				var frPeriod = oModel.getProperty("/frPeriod"),
					toPeriod = oModel.getProperty("/toPeriod");
				minDate = new Date(frPeriod.substr(2, 4), +frPeriod.substr(0, 2) - 1);
				maxDate = new Date(frPeriod.substr(2, 4), +frPeriod.substr(0, 2) + 1);

				if (maxDate > new Date()) {
					maxDate = new Date();
				}
				oModel.setProperty("/minPeriod", minDate);
				oModel.setProperty("/maxPeriod", maxDate);
				if (maxDate < new Date(toPeriod.substr(2, 4), +toPeriod.substr(0, 2) - 1) ||
					minDate > new Date(toPeriod.substr(2, 4), +toPeriod.substr(0, 2) - 1)) {
					oModel.setProperty("/toPeriod", "" + (maxDate.getMonth() + 1).toString().padStart(2, '0') + maxDate.getFullYear());
				}
			}
			oModel.refresh(true);
		},

		onSearch: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oDataProp = this.getView().getModel("Property").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (!oDataProp.pageNo ? 0 : +oDataProp.pageNo - 1),
						"pageSize": +oDataProp.pgSize
					},
					"req": {
						"entityId": [$.sap.entityID],
						"criteria": (oFilterData.criteria === "taxPeriod" ? "Month" : "Date"),
						"fromTaxPeriod": (oFilterData.criteria === "taxPeriod" ? oFilterData.frPeriod : null),
						"toTaxPeriod": (oFilterData.criteria === "taxPeriod" ? oFilterData.toPeriod : null),
						"fromDate": (oFilterData.criteria === "docDate" ? oFilterData.frDate : null),
						"toDate": (oFilterData.criteria === "docDate" ? oFilterData.toDate : null),
						"supplyType": this.removeAll(oFilterData.supplyType),
						"irnStatus": this.removeAll(oFilterData.irnStatus),
						"docNum": oFilterData.docNum,
						"irn": oFilterData.irn,
						"vendrGstin": oFilterData.vendrGstin,
						"gstins": oFilterData.gstins
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getInwardIrnInvMngtTableData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
					this._eInvPagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_eInvPagination: function (hdr) {
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

		onPressClear: function () {
			this._setDefaultFilter();
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
			// var oModel = this.getView().getModel("Property"),
			// 	flag = oModel.getProperty("/glRecFilter");

			// if (type === "S") {
			// 	oModel.setProperty("/pageNo", 1);
			// 	oModel.refresh(true);
			// }
			this.onSearch();
		},

		onPressBack: function () {
			this.getView().getModel("Property").setProperty("/visiSumm", false);
		},

		onProcessDocNo: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();
			this.getModel("Property").setProperty("/visiSumm", true);
			this.setModel(new JSONModel(obj), "InvHeaderKey");
			sap.ui.core.BusyIndicator.show(0);
			this.irnNum = obj.irnNum;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getInwardIrnDetailedData.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"irnNum": obj.irnNum
						}
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "EInvMgmtDetail");
					// this._eInvPagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_setReadOnlyDate: function (id, date, maxDate, minDate) {
			this.byId(id).addEventDelegate({
				onAfterRendering: function (evt) {
					evt.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
		},

		_getDate: function (date) {
			return date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + "-" +
				date.getDate().toString().padStart(2, '0');
		},

		onDownloadReport: function (oEvent) {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.getView().getModel("ProcessedRecord").getProperty("/"),
				aIndex = this.byId("tabEInvManage").getSelectedIndices(),
				key = oEvent.getParameter("item").getProperty("key");

			if (key === "table") {
				this._downloadTableData();
				return;
			}
			if (!aIndex.length) {
				var oGstin = this.getOwnerComponent().getModel("userPermission").getProperty("/respData/dataSecurity/gstin"),
					aGstin = oGstin.map(function (e) {
						return e.value;
					}),
					aId = [];
			} else {
				aGstin = aIndex.map(function (e) {
					return oData[e].gstin;
				});
				aId = aIndex.map(function (e) {
					return oData[e].id;
				});
			}
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"criteria": (oFilterData.criteria === "taxPeriod" ? "Month" : "Date"),
					"fromTaxPeriod": (oFilterData.criteria === "taxPeriod" ? oFilterData.frPeriod : null),
					"toTaxPeriod": (oFilterData.criteria === "taxPeriod" ? oFilterData.toPeriod : null),
					"fromDate": (oFilterData.criteria === "docDate" ? oFilterData.frDate : null),
					"toDate": (oFilterData.criteria === "docDate" ? oFilterData.toDate : null),
					"supplyType": this.removeAll(oFilterData.supplyType),
					"irnStatus": this.removeAll(oFilterData.irnStatus),
					"docNum": oFilterData.docNum,
					"irn": oFilterData.irn,
					"vendorGstin": oFilterData.vendrGstin,
					"gstins": this.removeAll(aGstin),
					"ids": aId,
					"type": "INVMNGT"
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
				payload.req.irnSts = this.removeAll(oFilterData.irnStatus);
				payload.req.irnNo = oFilterData.irn;
				break;
			}
			this.reportDownload(payload, url);
		},

		_downloadTableData: function () {
			var oRowBinding = this.byId("tabEInvManage").getBinding("rows"),
				aCols = this._createColumnConfig(),
				oSettings = {
					workbook: {
						columns: aCols,
						context: {
							sheetName: "Inward E-invoice Management"
						},
						hierarchyLevel: 'Level'
					},
					dataSource: oRowBinding,
					fileName: "Inward E-invoice Management_Table Data_" + this.getDateTimeStamp() + ".xlsx",
					worker: false // We need to disable worker because we are using a MockServer as OData Service
				};

			var oSheet = new Spreadsheet(oSettings);
			oSheet.build().finally(function () {
				oSheet.destroy();
			});
		},

		_createColumnConfig: function () {
			var aCols = [];

			aCols.push({
				label: "Recipient GSTIN",
				property: ["gstin"],
				type: EdmType.String
			});
			aCols.push({
				label: "Vendor GSTIN",
				property: "vendorGstin",
				type: EdmType.String
			});
			aCols.push({
				label: "Doc No",
				property: "docNo",
				type: EdmType.String
			});
			aCols.push({
				label: "Doc Date",
				property: "docDate",
				type: EdmType.String
			});
			aCols.push({
				label: "Doc Type",
				property: "docType",
				type: EdmType.String
			});
			aCols.push({
				label: "Supply Type",
				property: "supplyType",
				type: EdmType.String
			});
			aCols.push({
				label: "Taxable Value",
				property: "taxableVal",
				type: EdmType.Number
			});
			aCols.push({
				label: "Total Tax",
				property: "totalTax",
				type: EdmType.Number
			});
			aCols.push({
				label: "IGST",
				property: "igst",
				type: EdmType.Number
			});
			aCols.push({
				label: "CGST",
				property: "cgst",
				type: EdmType.Number
			});
			aCols.push({
				label: "SGST",
				property: "sgst",
				type: EdmType.Number
			});
			aCols.push({
				label: "Cess",
				property: "cess",
				type: EdmType.Number
			});
			aCols.push({
				label: "Total Invoice Value",
				property: "totInvVal",
				type: EdmType.Number
			});
			aCols.push({
				label: "IRN Number",
				property: "irnNum",
				type: EdmType.String
			});
			aCols.push({
				label: "IRN status",
				property: "irnSts",
				type: EdmType.String
			});
			aCols.push({
				label: "Acknowledgment Number",
				property: "ackNum",
				type: EdmType.String
			});
			aCols.push({
				label: "Acknowledgment Date",
				property: "ackDt",
				type: EdmType.String
			});
			aCols.push({
				label: "E-Way Bill Number",
				property: "ewbNo",
				type: EdmType.String
			});
			aCols.push({
				label: "E-Way Bill Date",
				property: "ewbDt",
				type: EdmType.String
			});
			aCols.push({
				label: "Cancellation Date",
				property: "cnclDt",
				type: EdmType.String
			});
			return aCols;
		},

		onPressPrint: function (oEvent) {
			var oData = this.getView().getModel("ProcessedRecord").getProperty("/"),
				aIndex = this.byId("tabEInvManage").getSelectedIndices(),
				key = oEvent.getParameter("item").getProperty("key"),
				payload = {
					"req": []
				};

			if (!aIndex.length) {
				MessageBox.error("Please select atleast one record to print PDF.");
				return;
			}
			aIndex.forEach(function (e) {
				payload.req.push({
					"irn": oData[e].irnNum,
					"supplyType": oData[e].supplyType,
					"irnStatus": (oData[e].irnSts === "Active" ? "ACT" : "CNL"),
					"docNum": oData[e].docNo,
					"docType": oData[e].docType
				});
			});
			switch (key) {
			case "summary":
				var url = "/aspsapapi/InwardEinvoiceSummaryPdfReports.do";
				break;
			case "detail":
				url = "/aspsapapi/InwardEinvoicePdfReports.do";
				break;
			}
			this.excelDownload(payload, url);
		},

		onChangeSegmentLayoutEINV: function () {

			var displayTab = this.getView().getModel("Property").getProperty("/displayTab");
			if (displayTab === "TabLayout") {
				this.getPrecedingDoc();
			}

		},

		getPrecedingDoc: function () {
			// "3860e3ad39c9adbd9cc3fefd6b989b4d60a3ee8f57b9026eeedfad071e7c2adf"
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getInwardIrnInvMngtTabLayoutData.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"irnNum": this.irnNum
						}
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.PrecedingDoc(data)
				}.bind(this))
				.fail(function (jqXHR, status, err) {

					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		PrecedingDoc: function (data) {
			var maxValue = Math.max(data.resp.AttribDtls.length, data.resp.PrecDocDtls.length, data.resp.ContrDtls.length, data.resp.AddlDocDtls
				.length);
			var oData = [];

			for (var i = 0; i < maxValue; i++) {
				oData.push({
					"attributeName": data.resp.AttribDtls[i] === undefined ? null : data.resp.AttribDtls[i].attributeName,
					"attributeValue": data.resp.AttribDtls[i] === undefined ? null : data.resp.AttribDtls[i].attributeValue,
					"preceedingInvoiceNumber": data.resp.PrecDocDtls[i] === undefined ? null : data.resp.PrecDocDtls[i].preceedingInvoiceNumber,
					"preceedingInvoiceDate": data.resp.PrecDocDtls[i] === undefined ? null : data.resp.PrecDocDtls[i].preceedingInvoiceDate,
					"otherReference": data.resp.PrecDocDtls[i] === undefined ? null : data.resp.PrecDocDtls[i].otherReference,
					"receiptAdviceReference": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].receiptAdviceReference,
					"receiptAdviceDate": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].receiptAdviceDate,
					"tenderReference": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].tenderReference,
					"contractReference": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].contractReference,
					"externalReference": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].externalReference,
					"projectReference": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].projectReference,
					"customerPOReferenceNumber": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].customerPOReferenceNumber,
					"customerPOReferenceDate": data.resp.ContrDtls[i] === undefined ? null : data.resp.ContrDtls[i].customerPOReferenceDate,
					"supportingDocURL": data.resp.AddlDocDtls[i] === undefined ? null : data.resp.AddlDocDtls[i].supportingDocURL,
					"supportingDocument": data.resp.AddlDocDtls[i] === undefined ? null : data.resp.AddlDocDtls[i].supportingDocument,
					"additionalInformation": data.resp.AddlDocDtls[i] === undefined ? null : data.resp.AddlDocDtls[i].additionalInformation
				})
			}

			this.getView().setModel(new JSONModel(oData), "PrecedingDoc");
		},

		onPressUpdatePRLinking: function (oEvent) {
			debugger;
			var oData = this.getView().getModel("ProcessedRecord").getProperty("/"),
				aIndex = this.byId("tabEInvManage").getSelectedIndices(),
				aId =[];
				

			if (!aIndex.length) {
				MessageBox.error("Please select atleast one record to Update PR Linking.");
				return;
			} else {
				aId = aIndex.map(function (e) {
					return oData[e].id;
				});
			}
			
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/updatePRTagging.do",
					contentType: "application/json",
					data: JSON.stringify({
					"req": {
						"ids" : aId
					}
				})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// data = JSON.parse(data);
					if(data.hdr.status =="S"){
						MessageBox.success(data.hdr.message);
					}else{
						MessageBox.error(data.resp);
					}
					
					// this.PrecedingDoc(data)
				}.bind(this))
				.fail(function (jqXHR, status, err) {

					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.imInwardEINVManage
		 */
		//	onExit: function() {
		//
		//	}
	});
});