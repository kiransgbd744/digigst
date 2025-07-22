sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, Formatter, JSONModel, MessageBox) {
	"use strict";
	var _aValidTabKeys = ["AutoRecon", "QRCode", "VendorTrend", "PDFReader"];
	return BaseController.extend("com.ey.digigst.controller.APModule", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.APModule
		 */
		onInit: function () {
			this.getView().setModel(new JSONModel(), "view");
			// this.apiLimt();
			this.GstnValidtr();
			this._bindDefaultData();
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "InvoiceCheck");
			this.getOwnerComponent().getRouter().getRoute("APModule").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger(),
				oArg = oEvent.getParameter("arguments").contextPath,
				key = oEvent.getParameter("arguments").key;

			if (_aValidTabKeys.includes(oArg)) {
				this.getView().getModel("view").setProperty("/selectedTabKey", oArg);
				this.getRouter().getTargets().display(oArg);
			} else {
				this.getView().getModel("view").setProperty("/selectedTabKey", "EInvoiceAV");
			}
			oHashChanger.setHash("APModule/" + oArg + "/" + key);
			this.getView().setModel(new JSONModel({
				"applicability": [],
				"gstinStatus": [],
				"noOfDays": 0
			}), "VenMasterFilter");
			this.onSearchEInv();
		},

		_bindDefaultData: function () {
			var oModel = this.getView().getModel("AIMProperty"),
				obj = {
					"segment": !oModel ? "SearchByGSTIN" : oModel.getProperty("/segment"),
					"pageNo": 0,
					"pgTotal": 0,
					"pgSize": 10,
					"ePageNo": false,
					"bFirst": false,
					"bPrev": false,
					"bNext": false,
					"bLast": false,
				};
			this.getView().setModel(new JSONModel(obj), "AIMProperty");
			this.getView().setModel(new JSONModel({
				"applicability": [],
				"gstinStatus": [],
				"noOfDays": 0
			}), "VenMasterFilter");
			var oGoButton = this.byId("fbVenMaster")._oSearchButton;
			oGoButton.setText("Search");
			oGoButton.setType("Transparent");
			oGoButton.addStyleClass("buttoncolor");
			this.byId("fbVenMaster")._oClearButtonOnFB.addStyleClass("buttoncolorSec");
		},

		handleIconTabBarSelect: function (oEvent) {
			// this.getRouter().navTo("APModule", {
			// 	contextPath: oEvent.getParameter("selectedKey"),
			// 	key: "a"
			// }, true);

			this.handleIconTabBarSelectFinal(oEvent.getParameter("selectedKey"));
		},

		handleIconTabBarSelectFinal: function (key) {
			this.getRouter().navTo("APModule", {
				contextPath: key,
				key: "a"
			}, true);
		},

		apiLimt: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getLimitAndUsageCount.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "ApiLimit");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "ComplienceHistory");
					MessageBox.error("complienceHistory : Error");
				}.bind(this));
		},

		onChangeSegment: function (oEvt) {
			if (oEvt.getSource().getSelectedKey() !== "vendorMaster") {
				this.apiLimt();
			}
		},

		GstnValidtr: function () {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getTaxPayerConfigDetail.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"user": "SYSTEM",
							"einvApplicability": true
						}
					})
				})
				.done(function (data, status, jqXHR) {
					var oGstnSrNo = data.resp.det;
					for (var j = 0; j < oGstnSrNo.length; j++) {
						oGstnSrNo[j].SrNum = j + 1;
					}
					this.apiLimt();
					this.getView().setModel(new JSONModel(data), "BulkValidList");
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onRefresh: function () {
			this.GstnValidtr();
		},

		//#############  File Download -> PAN/GSTIN Validator(Bulk Validation)
		onDownloadCatalog: function (oEvent) {
			var oReqId = oEvent.getSource().getBindingContext("BulkValidList").getObject().requestId;
			window.open("/aspsapapi/downloadGstinValidatorExcelDocument.do?requestId=" + oReqId);
		},

		//############# Table leve Excel File download -> PAN/GSTIN Validator
		onGSTINExclDwnld: function (oEvt) {
			sap.m.URLHelper.redirect("model/Data.xlsx", true);
		},

		//#############  File Upload -> PAN/GSTIN Validator(Bulk Validation)
		onUpload: function () {
			var oFileUploader = this.byId("ucWebUpload22");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageBox.error("Please select file to upload");
				return;
			}
			oFileUploader.setUploadUrl("/aspsapapi/gstinValidFileUpload.do");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.setAdditionalData("true");
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
		},

		//############# Tab -> GSTIN Validator(Search)
		onSearchGSTN: function () {
			var oGstin1 = this.byId("idpangstn").getValue();
			if (oGstin1 === "") {
				MessageBox.alert("Please enter GSTIN Number");
				this.getView().setModel(new JSONModel([]), "SearchGstnData");
				return;
			}
			var oSearhInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"gstin": this.byId("idpangstn").getValue(),
					"einvApplicability": true
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getTaxPayerDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oSearhInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.apiLimt();
					if (data.hdr.status === "E") {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
					this.getView().setModel(new JSONModel(data.resp), "SearchGstnData");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPress: function (oEvt) {
			var selKey = oEvt.getSource().getTitle();
			this.byId("labId").setText(selKey);
			if (selKey === "Basic Detail") {}
		},

		onSearchEInv: function (type) {
			var oFilterModel = this.getView().getModel("VenMasterFilter").getProperty("/"),
				oDataProp = this.getView().getModel("AIMProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (!oDataProp.pageNo || type !== "P" ? 0 : +oDataProp.pageNo - 1),
						"pageSize": oDataProp.pgSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"einvoiceApplicability": this.removeAll(oFilterModel.applicability),
						"gstinStatus": this.removeAll(oFilterModel.gstinStatus),
						"statusNotUpdatedInLastDays": oFilterModel.noOfDays
					}
				};
			this._getVendorMasterData(payload);
		},

		_getVendorMasterData: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/vendorGstinDetailSearch.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp["results"] = data.resp.vendorMappingRespDtoList;
					this.getView().setModel(new JSONModel(data), "EInvVendorMaster");
					this.byId("tabEInvVenMaster").setSelectedIndex(-1);
					this._eInvPagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_eInvPagination: function (hdr) {
			var oModel = this.getView().getModel("AIMProperty"),
				vTotal = Math.ceil(hdr.totalRecords / oModel.getProperty("/pgSize")),
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

		onPaginationVenMaster: function (btn) {
			var oModel = this.getView().getModel("AIMProperty");
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
			this.onSearchEInv('P');
		},

		onSubPaginationVenMaster: function (type) {
			var oModel = this.getView().getModel("AIMProperty");
			if (type === "S") {
				oModel.setProperty("/pageNo", 1);
				oModel.refresh(true);
			}
			if (+oModel.getProperty("/pageNo") > +oModel.getProperty("/pgTotal")) {
				oModel.setProperty("/pageNo", +oModel.getProperty("/pgTotal"));
			}
			this.onSearchEInv('P');
		},

		onClearEInv: function () {
			this._bindDefaultData();
			this.onSearchEInv('P');
		},

		onPressLink: function (type) {
			var oFilterModel = this.getView().getModel("VenMasterFilter").getProperty("/"),
				oDataProp = this.getView().getModel("AIMProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": 0,
						"pageSize": oDataProp.pgSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"einvoiceApplicability": ["Yes", "No"].includes(type) ? [type] : [],
						"gstinStatus": !["Yes", "No"].includes(type) ? [type] : [],
						"statusNotUpdatedInLastDays": oFilterModel.noOfDays
					}
				};
			this._getVendorMasterData(payload);
		},

		onInitiateGetCall: function () {
			var aIndex = this.byId("tabEInvVenMaster").getSelectedIndices(),
				oData = this.getView().getModel("EInvVendorMaster").getProperty("/resp/results"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstins": []
					}
				};
			payload.req.vendorGstins = aIndex.map(function (e) {
				return oData[e].vendorGstin;
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initateGetCallForVendorGstnDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.hdr.message, {
							styleClass: "msgBoxSize"
						});
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDownloadReport: function () {
			var aIndex = this.byId("tabEInvVenMaster").getSelectedIndices(),
				oData = this.getView().getModel("EInvVendorMaster").getProperty("/resp/results"),
				url = "/aspsapapi/downloadGetVendorGstinDetailsReports.do",
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstinDetails": []
					}
				};
			payload.req.gstinDetails = aIndex.map(function (e) {
				return {
					"gstin": oData[e].vendorGstin
				};
			});
			this.reportDownload(payload, url);
		}
	});
});