sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	var vCount1 = 100;
	return BaseController.extend("com.ey.digigst.controller.QRCode", {
		formatter: Formatter,
		onInit: function () {
			var that = this,
				vDate = new Date(),
				date = new Date(),
				date1 = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);

			date.setDate(date.getDate() - 9);
			date1.setMonth(date1.getMonth() - 1);

			this.byId("FromDate").setMinDate(new Date("2017", "05", "01"));
			this.byId("FromDate").setMaxDate(vDate);
			this.byId("FromDate").setDateValue(vPeriod);
			this.byId("FromDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("FromDate").$().find("input").attr("readonly", true);
				}
			});

			this.byId("ToDate").setMinDate(vPeriod);
			this.byId("ToDate").setMaxDate(vDate);
			this.byId("ToDate").setDateValue(vDate);
			this.byId("ToDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("ToDate").$().find("input").attr("readonly", true);
				}
			});

			var oVizFrameBar1 = this.oVizFrame = this.getView().byId("idVizFrame12"),
				oPopOver2 = this.getView().byId("idPopOver12");
			oPopOver2.connect(oVizFrameBar1.getVizUid());
			oVizFrameBar1.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					}
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: false,
						text: ""
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}
				}
			});

			var oVizFrameBar12 = this.oVizFrame = this.getView().byId("idVizFrame1"),
				oPopOver12 = this.getView().byId("idPopOver1");
			oPopOver12.connect(oVizFrameBar12.getVizUid());
			oVizFrameBar12.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					}
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: false,
						text: ""
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}
				}
			});
			this._bindDefaultProperty();
		},

		_bindDefaultProperty: function () {
			var obj = {
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 100,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false,
			};
			this.getView().setModel(new JSONModel(obj), "QRProperty");
		},

		onFromTaxPeriodChange: function (oevt) {
			var toDate = this.byId("ToDate").getDateValue(),
				fromDate = this.byId("FromDate").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("From Date can not be greter then To Date");
				this.byId("ToDate").setDateValue(oevt.getSource().getDateValue());
				this.byId("ToDate").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("ToDate").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.onLoadRecipientGstin(),
						this.onLoadPAN()
					])
					.finally(function () {
						this.handleIconTabBar();
					}.bind(this));
			}
			this.onPressBack();
		},

		onLoadRecipientGstin: function () {
			return new Promise(function (resolve, reject) {
				$.get("/aspsapapi/getQRRecipientGstin.do", {
						'entityId': $.sap.entityID
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel([]);
						if (data.hdr.status === "S") {
							data.resp.data.unshift({
								gstin: "All"
							});
							oModel.setProperty("/", data.resp.data);
						}
						this.getView().setModel(oModel, "QRRecipientGstin");
						this.getView().getModel("QRRecipientGstin").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "QRRecipientGstin");
						this.getView().getModel("QRRecipientGstin").refresh(true);
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onLoadPAN: function () {
			return new Promise(function (resolve, reject) {
				$.get("/aspsapapi/getQRVendorPan.do", {
						'entityId': $.sap.entityID
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel([]);
						if (data.hdr.status === "S") {
							data.resp.vendorGstins.unshift({
								gstin: "All"
							});
							oModel.setProperty("/", data.resp);
						}
						this.getView().setModel(oModel, "QRVendorPan");
						this.getView().getModel("QRVendorPan").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "QRVendorPan");
						this.getView().getModel("QRVendorPan").refresh(true);
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPAN: function () {
			var that = this,
				searchInfo = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorPan": this.removeAll(this.byId("PanId").getSelectedKeys())
					}
				};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getQRVendorGstin.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.vendorGstins.unshift({
						gstin: "All"
					});
					that.getView().setModel(new JSONModel(data.resp.vendorGstins), "QRVendorGstin");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onUpload: function () {
			var oFileUploader = this.byId("FileUploadid");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			oFileUploader.setUploadUrl("/aspsapapi/uploadQRCodeFile.do");
			oFileUploader.removeAllParameters();
			oFileUploader.addParameter(
				new sap.ui.unified.FileUploaderParameter({
					name: "entityId",
					value: $.sap.entityID
				})
			);
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var oRespnse = oEvent.getParameters().responseRaw,
				status = JSON.parse(oRespnse);
			if (status.hdr.status === "S") {
				this.getView().byId("FileUploadid").setValue("");
				sap.m.MessageBox.success("File Uploaded Successfully", {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(status.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
			this.onTabBind();
		},

		onTabBind: function () {
			return new Promise(function (resolve, reject) {
				var oProp = this.getView().getModel("QRProperty").getProperty("/");
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						url: "/aspsapapi/getUploadStatus.do",
						type: "POST",
						contentType: "application/json",
						data: JSON.stringify({
							"hdr": {
								"pageNum": (oProp.pageNo ? oProp.pageNo - 1 : 0),
								"pageSize": oProp.pgSize
							},
							"req": {
								"entityId": $.sap.entityID
							}
						})
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "QRCode");
						this.byId("table1").setSelectedIndex(-1);
						this._uploadPagination(data.hdr);
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}.bind(this));
		},

		_uploadPagination: function (hdr) {
			var oModel = this.getView().getModel("QRProperty"),
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

		onPaginationQrCode: function (btn) {
			var oModel = this.getView().getModel("QRProperty");
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
			this.onTabBind();
		},

		onSubPaginationQrCode: function (type) {
			var oModel = this.getView().getModel("QRProperty");
			if (type === "S") {
				oModel.setProperty("/pageNo", 1);
				oModel.refresh(true);
			}
			if (+oModel.getProperty("/pageNo") > +oModel.getProperty("/pgTotal")) {
				oModel.setProperty("/pageNo", +oModel.getProperty("/pgTotal"));
			}
			this.onTabBind();
		},

		onRefresh: function () {
			this.onTabBind();
		},

		DownloadFile: function (oEvent) {
			var fileId = oEvent.getSource().getEventingParent().getBindingContext("QRCode").getObject().fileId,
				Request = {
					"entityId": $.sap.entityID,
					"fileId": fileId
				};
			this.excelDownload(Request, "/aspsapapi/generateReport.do");
		},

		ErrDownloadFile: function (oEvent) {
			var fileId = oEvent.getSource().getEventingParent().getBindingContext("QRCode").getObject().fileId,
				Request = {
					"entityId": $.sap.entityID,
					"fileId": fileId
				};
			this.excelDownload(Request, "/aspsapapi/generateErroredReport.do");
		},

		onPressBack: function () {
			this.byId("viewId").setVisible(true);
			this.byId("viewId1").setVisible(false);
		},

		onResultSummaryView: function (oEvent) {
			this.byId("viewId").setVisible(false);
			this.byId("viewId1").setVisible(true);
			var fileId = oEvent.getSource().getEventingParent().getBindingContext("QRCode").getObject().fileId;
			this.getView().setModel(new JSONModel({
				"fileId": fileId,
				"pageNoA": 0,
				"pgTotalA": 0,
				"pgSizeA": 10,
				"ePageNoA": false,
				"bFirstA": false,
				"bPrevA": false,
				"bNextA": false,
				"bLastA": false,
				"pageNoB": 0,
				"pgTotalB": 0,
				"pgSizeB": 10,
				"ePageNoB": false,
				"bFirstB": false,
				"bPrevB": false,
				"bNextB": false,
				"bLastB": false,
				"pageNoC": 0,
				"pgTotalC": 0,
				"pgSizeC": 10,
				"ePageNoC": false,
				"bFirstC": false,
				"bPrevC": false,
				"bNextC": false,
				"bLastC": false,
				"fullScreenA": false,
				"fullScreenB": false,
				"fullScreenC": false
			}), "PropDetail");
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getViewSummDetails(fileId),
					this._getViewTableSummDetails(fileId, 1, 10)
				])
				.then(function (results) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (error) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getViewSummDetails: function (fileId) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getViewSummDetails.do",
						contentType: "application/json",
						data: JSON.stringify({
							"entityId": $.sap.entityID,
							"fileId": fileId
						})
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp.data), "ViewSummDetails");
						resolve(data);
					}.bind(this))
					.fail(function (error) {
						reject(error);
					});
			}.bind(this));
		},

		_getViewTableSummDetails: function (fileId, pageNo, pageSize) {
			return new Promise(function (resolve, reject) {
				var oProperty = this.getView().getModel("PropDetail").getProperty("/"),
					payload = {
						"hdr": {
							"pageNum": pageNo - 1,
							"pageSize": pageSize
						},
						"entityId": $.sap.entityID,
						"fileId": fileId
					}
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getViewTableSummDetails.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = this.getView().getModel("PropDetail");
						if (data.hdr.optionOpted === "A") {
							data.resp.data.qrSummaryData.forEach(function (e, i) {
								e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
							});
						} else if (data.hdr.optionOpted === "B") {
							data.resp.data.qrpdfSummaryData.forEach(function (e, i) {
								e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
							});
						} else if (data.hdr.optionOpted === "C") {
							data.resp.data.qrpdfJsonSummaryData.forEach(function (e, i) {
								e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
							});
						}
						this.getView().setModel(new JSONModel(data.resp.data), "ViewSummTableDetails");
						oModel.setProperty("/optionOpted", data.hdr.optionOpted);
						oModel.refresh(true);
						this._uploadDataPagination(data.hdr);
						resolve(data);
					}.bind(this))
					.fail(function (error) {
						reject(error);
					});
			}.bind(this));
		},

		onUploadDataPagination: function (opt, btn) {
			var oModel = this.getView().getModel("PropDetail"),
				fileId = oModel.getProperty("/fileId");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo" + opt, 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pageNo" + opt) - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pageNo" + opt) + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pgTotal" + opt));
				break;
			}
			oModel.refresh(true);
			this._getViewTableSummDetails(fileId, +oModel.getProperty("/pageNo" + opt), +oModel.getProperty("/pgSize" + opt));
		},

		onUpDataPaginationSubmit: function (opt, type) {
			var oModel = this.getView().getModel("PropDetail"),
				fileId = oModel.getProperty("/fileId");

			if (type === "S") {
				oModel.setProperty("/pageNo" + opt, 1);
			}
			if (+oModel.getProperty("/pageNo" + opt) > +oModel.getProperty("/pgTotal" + opt)) {
				oModel.setProperty("/pageNo" + opt, +oModel.getProperty("/pgTotal" + opt));
			}
			oModel.refresh(true);
			this._getViewTableSummDetails(fileId, +oModel.getProperty("/pageNo" + opt), +oModel.getProperty("/pgSize" + opt));
		},

		_uploadDataPagination: function (header) {
			var opt = header.optionOpted,
				oModel = this.getView().getModel("PropDetail"),
				vTotal = Math.ceil(header.totalCount / oModel.getProperty("/pgSize" + opt)),
				vPageNo = (vTotal ? header.pageNum + 1 : 0);

			oModel.setProperty("/pageNo" + opt, vPageNo);
			oModel.setProperty("/pgTotal" + opt, vTotal);
			oModel.setProperty("/ePageNo" + opt, vTotal > 1);
			oModel.setProperty("/bFirst" + opt, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev" + opt, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext" + opt, vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast" + opt, vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onSummaryTabSearch: function (oEvent, opt) {
			var sQuery = oEvent.getSource().getValue().trim(),
				vId = (opt === "A" ? "qrSummaryTab" : "qrPdfSummaryTab"),
				oBinding = this.byId(vId).getBinding("rows");

			if (sQuery) {
				var aFields = this._getSummaryTableField(opt),
					aEqField = this._getSummaryTableField("Eq" + opt),
					aFilter = aFields.map(function (e) {
						if (e === "BuyerGstin_Match") {
							return new Filter({
								path: "BuyerGstin_Match",
								operator: FilterOperator.StartsWith,
								value1: sQuery
							});
						}
						return new Filter({
							path: e,
							operator: (aEqField.includes(e) ? FilterOperator.EQ : FilterOperator.Contains),
							value1: sQuery
						});
					});
				oBinding.filter(new Filter({
					filters: aFilter,
					and: false
				}), "Application");
			} else {
				oBinding.filter([], "Application");
			}
		},

		_getSummaryTableField: function (opt) {
			switch (opt) {
			case "A":
				return [
					"slNo", "File Name", "SellerGstin", "BuyerGstin", "DocNo", "DocTyp", "DocDt", "TotInvVal", "ItemCnt", "MainHsnCode", "Irn",
					"Validated_Date", "IrnDt", "Signature", "BuyerGstin_Match", "SellerGstin_Match", "DocNo_Match", "DocDt_Match", "DocTyp_Match",
					"TotInvVal_Match", "MainHsnCode_Match", "Irn_Match", "Match_Count", "Mismatch_Count", "Declaration_Rule48_(4)"
				];
			case "EqA":
				return ["slNo", "ItemCnt", "Match_Count", "Mismatch_Count", "Declaration_Rule48_(4)"];
			case "B":
				return [
					"slNo", "reportCategory", "matchReasons", "misMatchReasons", "matchCount", "mismatchCount", "buyerGstinQR", "buyerGstinPDF",
					"sellerGstinQR", "sellerGstinPDF", "docNoQR", "docNoPDF", "docDtQR", "docDtPDF", "docTypeQR", "docTypePDF", "totInvValQR",
					"totInvValPDF", "itemCntQR", "mainHsnCodeQR", "mainHsnCodePDF", "irnQR", "irnPDF", "irnDateQR", "irnDatePDF", "signatureQR",
					"Filename", "validatedDate", "declaration", "einvAppli"
				];
			case "EqB":
				return ["slNo", "matchCount", "mismatchCount", "itemCntQR"];
			case "C":
				return [
					"slNo", "reportCategory", "matchReasons", "misMatchReasons", "matchCount", "mismatchCount", "buyerGstinJson", "buyerGstinPDF",
					"sellerGstinJson", "sellerGstinPDF", "docNoJson", "docNoJson", "docDtJson", "docDtPDF", "docTypeJson", "docTypePDF",
					"totInvValJson", "totInvValPDF", "itemCntJson", "itemCntPDF", "mainHsnCodeJson", "mainHsnCodePDF", "signatureQR", "irnJson",
					"irnPDF", "irnDateQR", "irnDatePDF", "irnStatusJson", "irnCancellationDateJson", "posJson", "posPdf", "taxableValueJson",
					"taxableValuePdf", "igstJson", "igstPdf", "cgstJson", "cgstPdf", "sgstUtgstJson", "sgstUtgstPdf", "cessJson", "cessPdf",
					"totalTaxJson", "totalTaxPdf", "reverseChargeJson", "reverseChargePdf", "purchaseOrderNoJson", "purchaseOrderNoPdf",
					"quantityJson", "quantityPdf", "unitPriceJson", "unitPricePdf", "lineItemAmountJson", "lineItemAmountPdf", "fileName",
					"validatedDate", "declaration", "einvAppli"
				];
			case "EqC":
				return [
					"slNo", "matchCount", "mismatchCount", "itemCntJson", "itemCntPDF", "posJson", "posPdf", "taxableValueJson", "taxableValuePdf",
					"igstJson", "igstPdf", "cgstJson", "cgstPdf", "sgstUtgstJson", "sgstUtgstPdf", "cessJson", "cessPdf", "totalTaxJson",
					"totalTaxPdf"
				];
			}
		},

		onDetailFullScreen: function (opt, action) {
			var oModel = this.getView().getModel("PropDetail");
			oModel.setProperty("/fullScreen" + opt, action === 'open');
			oModel.refresh(true);
		},

		handleIconTabBar: function () {
			var vKey = this.byId("iconid").getSelectedKey();
			switch (vKey) {
			case "UploadData":
				this.onTabBind();
				break;
			case "ValidatedData":
				this._initializeDefault();
				this.onSearchValidateData();
				break;
			}
		},

		_initializeDefault: function () {
			this.getView().setModel(new JSONModel({
				"pageNoA": 0,
				"pgTotalA": 0,
				"pgSizeA": 10,
				"ePageNoA": false,
				"bFirstA": false,
				"bPrevA": false,
				"bNextA": false,
				"bLastA": false,
				"pageNoB": 0,
				"pgTotalB": 0,
				"pgSizeB": 10,
				"ePageNoB": false,
				"bFirstB": false,
				"bPrevB": false,
				"bNextB": false,
				"bLastB": false,
				"pageNoC": 0,
				"pgTotalC": 0,
				"pgSizeC": 10,
				"ePageNoC": false,
				"bFirstC": false,
				"bPrevC": false,
				"bNextC": false,
				"bLastC": false,
				"fullScreenA": false,
				"fullScreenB": false,
				"fullScreenC": false
			}), "PropValidate");
		},

		onSearchValidateData: function () {
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getValidateGraphData(),
					this._getValidateTabData(1, 10)
				])
				.then(function (result) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function () {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getValidateGraphData: function (payload) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"recipientGstins": this.removeAll(this.byId("GstinId").getSelectedKeys()),
						"vendorGstin": this.removeAll(this.byId("VGid").getSelectedKeys()),
						"validatedFrom": this.byId("FromDate").getValue(),
						"validatedTo": this.byId("ToDate").getValue(),
						"recordStatus": this.removeAll(this.byId("RSid").getSelectedKeys())
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getConsolidatedData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						this.getView().setModel(new JSONModel(data.resp.data), "ConsolidatedData");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		_getValidateTabData: function (pageNo, pageSize) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"hdr": {
						"pageNum": pageNo - 1,
						"pageSize": pageSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"recipientGstins": this.removeAll(this.byId("GstinId").getSelectedKeys()),
						"vendorGstin": this.removeAll(this.byId("VGid").getSelectedKeys()),
						"validatedFrom": this.byId("FromDate").getValue(),
						"validatedTo": this.byId("ToDate").getValue(),
						"recordStatus": this.removeAll(this.byId("RSid").getSelectedKeys())
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getConsolidatedTableData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var oPropModel = this.getView().getModel("PropValidate"),
							oModel = new JSONModel(null);
						if (data.hdr.status === "S") {
							if (data.hdr.optionOpted === "A") {
								data.resp.data.qrSummaryData.forEach(function (e, i) {
									e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
								});
							} else if (data.hdr.optionOpted === "B") {
								data.resp.data.qrpdfSummaryData.forEach(function (e, i) {
									e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
								});
							} else if (data.hdr.optionOpted === "C") {
								data.resp.data.qrpdfJsonSummaryData.forEach(function (e, i) {
									e.slNo = (data.hdr.pageNum * data.hdr.pageSize) + i + 1;
								});
							}
							oModel.setData(data.resp.data);
							this._validateDataPagination(data.hdr);
						}
						this.getView().setModel(oModel, "ConsolidatedTableData");
						oPropModel.setProperty("/optionOpted", data.hdr.optionOpted);
						oPropModel.refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onValidateDataPagination: function (opt, btn) {
			var oModel = this.getView().getModel("PropValidate");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo" + opt, 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pageNo" + opt) - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pageNo" + opt) + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo" + opt, oModel.getProperty("/pgTotal" + opt));
				break;
			}
			oModel.refresh(true);
			this._getValidateTabData(+oModel.getProperty("/pageNo" + opt), +oModel.getProperty("/pgSize" + opt));
		},

		onVDPaginationSubmit: function (opt, type) {
			var oModel = this.getView().getModel("PropValidate");

			if (type === "S") {
				oModel.setProperty("/pageNo" + opt, 1);
			}
			if (+oModel.getProperty("/pageNo" + opt) > +oModel.getProperty("/pgTotal" + opt)) {
				oModel.setProperty("/pageNo" + opt, +oModel.getProperty("/pgTotal" + opt));
			}
			oModel.refresh(true);
			this._getValidateTabData(+oModel.getProperty("/pageNo" + opt), +oModel.getProperty("/pgSize" + opt));
		},

		_validateDataPagination: function (header) {
			var opt = header.optionOpted,
				oModel = this.getView().getModel("PropValidate"),
				vTotal = Math.ceil(header.totalCount / oModel.getProperty("/pgSize" + opt)),
				vPageNo = (vTotal ? header.pageNum + 1 : 0);

			oModel.setProperty("/pageNo" + opt, vPageNo);
			oModel.setProperty("/pgTotal" + opt, vTotal);
			oModel.setProperty("/ePageNo" + opt, vTotal > 1);
			oModel.setProperty("/bFirst" + opt, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev" + opt, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext" + opt, vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast" + opt, vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onFullScreenVD: function (opt, action) {
			var oModel = this.getView().getModel("PropValidate");
			oModel.setProperty("/fullScreen" + opt, action === 'open');
			oModel.refresh(true);
		},

		nicViewSummaryTab: function (vPageNo) {
			var onicCrdMdl = new JSONModel(),
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
					},
					"entityId": $.sap.entityID,
					"fileId": this.getView().getModel("PropDetail").getProperty("/fileId")
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getViewTableSummDetails.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._uploadDataPagination(data.hdr);
					var oSrNo = data.resp.data.qrSummaryData;
					for (var i = 0; i < oSrNo.length; i++) {
						oSrNo[i].SlNo = i + 1;
					}
					onicCrdMdl.setData(data.resp.data);
					that.getView().setModel(onicCrdMdl, "ViewSummTableDetails");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onSearchGstinTab1: function (oEvt) {
			var oTable = this.byId("GSTINTabId"),
				searchText = oEvt.getSource().getValue(),
				filters = [];
			if (searchText.trim() != '') {
				var filter1 = new sap.ui.model.Filter({
					path: "gstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "invoices",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "match",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		onSearchGstinTab: function (oEvt) {
			var oTable = this.byId("GSTNwiseId"),
				searchText = oEvt.getSource().getValue(),
				filters = [];
			if (searchText.trim() != '') {
				var filter1 = new sap.ui.model.Filter({
					path: "gstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "invoices",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "match",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		onSearchPANTab: function (oEvt) {
			var oTable = this.byId("panTabId"),
				searchText = oEvt.getSource().getValue(),
				filters = [];
			if (searchText.trim() != '') {
				var filter1 = new sap.ui.model.Filter({
					path: "gstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "invoices",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "match",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		onpressValidatedatapageTab: function (vPageNo) {
			var that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": Number(this.getView().byId("idPageVd").getSelectedKey())
					},
					"req": {
						// "recipientGstins": [],
						// "vendorGstin": [],
						// "validatedFrom": this.byId("FromDate").getValue(),
						// "validatedTo": this.byId("ToDate").getValue(),
						// // "validatedFrom": "09-11-2021",
						// // "validatedTo": "10-11-2021",
						// "recordStatus": []
						"entityId": $.sap.entityID,
						"recipientGstins": this.removeAll(this.byId("GstinId").getSelectedKeys()),
						"vendorGstin": this.removeAll(this.byId("VGid").getSelectedKeys()),
						"validatedFrom": this.byId("FromDate").getValue(),
						"validatedTo": this.byId("ToDate").getValue(),
						"recordStatus": this.removeAll(this.byId("RSid").getSelectedKeys())
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getConsolidatedTableData.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = new JSONModel(null);
					if (data.hdr.status === "S") {
						that._validateDataPagination(data.hdr);
						var oSrNo = data.resp.data.qrSummaryData;
						for (var i = 0; i < oSrNo.length; i++) {
							oSrNo[i].SlNo = i + 1;
						}
						oModel.setData(data.resp.data);
					}
					that.getView().setModel(oModel, "ConsolidatedTableData");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(null), "ConsolidatedTableData");
				});
		},

		onPNVDChange: function (oEvent) {
			var Request = {
				"hdr": {
					"pageNum": 0,
					"pageSize": oEvent.getSource().getSelectedKey()
				},
				"req": {
					"entityId": $.sap.entityID,
					"recipientGstins": this.removeAll(this.byId("GstinId").getSelectedKeys()),
					"vendorGstin": this.removeAll(this.byId("VGid").getSelectedKeys()),
					"validatedFrom": this.byId("FromDate").getValue(),
					"validatedTo": this.byId("ToDate").getValue(),
					"recordStatus": this.removeAll(this.byId("RSid").getSelectedKeys())
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getConsolidatedTableData.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					var onicTableMdl = new JSONModel(null);
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that._validateDataPagination(data.hdr);
						var vSlNo = 0;
						if (data.resp.data.qrSummaryData !== undefined) {
							for (var i = 0; i < data.resp.data.qrSummaryData.length; i++) {
								vSlNo = vSlNo + 1;
								data.resp.data.qrSummaryData[i].SlNo = vSlNo;
							}
						}
						onicTableMdl.setData(data.resp.data);
						// that.onpressValidatedatapageTab();
					}
					that.getView().setModel(onicTableMdl, "ConsolidatedTableData");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(null), "ConsolidatedTableData");
				});
		},

		onSearchPANTab1: function (oEvt) {
			var oTable = this.byId("PANTabId1"),
				searchText = oEvt.getSource().getValue(),
				filters = [];
			if (searchText.trim() != '') {
				var filter1 = new sap.ui.model.Filter({
					path: "gstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "invoices",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "match",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		ValidateDataTabSearch: function (oEvent, opt) {
			var sQuery = oEvent.getSource().getValue().trim(),
				vId = (opt === "A" ? "qrVDSummaryTab" : (opt === "B" ? "qrpdfVDSummaryData" : "qrPdfJsonVDSummaryTab")),
				oBinding = this.byId(vId).getBinding("rows");

			if (sQuery) {
				var aFields = this._getSummaryTableField(opt),
					aEqField = this._getSummaryTableField("Eq" + opt),
					aFilter = aFields.map(function (e) {
						if (e === "BuyerGstin_Match") {
							return new Filter({
								path: "BuyerGstin_Match",
								operator: FilterOperator.StartsWith,
								value1: sQuery
							});
						}
						return new Filter({
							path: e,
							operator: (aEqField.includes(e) ? FilterOperator.EQ : FilterOperator.Contains),
							value1: sQuery
						});
					});
				oBinding.filter(new Filter({
					filters: aFilter,
					and: false
				}), "Application");
			} else {
				oBinding.filter([], "Application");
			}
		},

		onReportDownload: function () {
			var Request = {
				"req": {
					"entityId": $.sap.entityID,
					"recipientGstins": this.removeAll(this.byId("GstinId").getSelectedKeys()),
					"vendorGstin": this.removeAll(this.byId("VGid").getSelectedKeys()),
					"validatedFrom": this.byId("FromDate").getValue(),
					"validatedTo": this.byId("ToDate").getValue(),
					"recordStatus": this.removeAll(this.byId("RSid").getSelectedKeys())
				}
			};
			this.excelDownload(Request, "/aspsapapi/generateConsReport.do");
		},

		onDownloadJSON: function (jsonData) {
			this._jsonDownload(jsonData, 'Inward E-invoice_JSON_', true);
		},

		onLinkPress: function (oEvt) {
			var reqId = oEvt.getSource().getEventingParent().getBindingContext("QRCode").getObject().fileId,
				oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.PopoverVC", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getFileDetailsbyId.do?fileId=" + reqId,
					contentType: "application/json",
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "FileDetails");
						that._oGstinPopover.openBy(oButton);
						//that.EntityTabBind();
					} else {
						that.getView().setModel(new JSONModel([]), "FileDetails");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel([]), "FileDetails");
				});
		}
	});
});