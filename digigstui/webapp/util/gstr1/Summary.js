sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"com/ey/digigst/util/Formatter"
], function (MobileLibrary, MessageBox, MessageToast, JSONModel, Fragment, Formatter) {
	"use strict";

	return {
		/*===================================================================*/
		/*========= Review Summary Table List ===============================*/
		/*===================================================================*/
		_tableList: function () {
			return [
				"b2b", "b2ba", "b2cl", "b2cla", "exp", "expa", "cdnr", "cdnra", "cdnur", "cdnura",
				"b2cs", "b2csa", "nil", "at", "ata", "txpd", "txpda", "hsn", "doc_issued"
			];
		},

		/*===================================================================*/
		/*========= Display Error Message ===================================*/
		/*===================================================================*/
		_errorMessage: function (vMsg) {
			MessageBox.error(vMsg, {
				styleClass: "sapUiSizeCompact"
			});
		},

		/*===================================================================*/
		/*========= JSON Structure ==========================================*/
		/*===================================================================*/
		_jsonSummStuct: function () {
			return {
				type: "",
				tableSection: "",
				records: 0,
				invValue: 0,
				taxableValue: 0,
				taxPayble: 0,
				igst: 0,
				cgst: 0,
				sgst: 0,
				cess: 0,
				items: []
			};
		},

		_jsonDocIssued: function () {
			return {
				type: "",
				tableSection: "",
				records: 0,
				totalIssued: 0,
				netIssued: 0,
				cancelled: 0,
				items: []
			};
		},

		_jsonNilRated: function () {
			return {
				type: "",
				taxableValue: 0,
				totalExempted: 0,
				totalNilRated: 0,
				totalNonGST: 0,
				items: []
			};
		},

		/*===================================================================*/
		/*========= AJAX Call to get Review Summary Data ====================*/
		/*===================================================================*/
		_reviewSummary: function (controller) {
			var that = this;
			var aEntity = controller.byId("slSummEntity").getSelectedKeys();
			var aGstin = controller.byId("mcbSummaryGSTIN").getSelectedKeys();
			var vFromDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getDateValue())).substr(2, 6);
			var vToDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getSecondDateValue())).substr(2, 6);
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1Summary.do",
				data: JSON.stringify({
					"req": {
						"entityId": aEntity,
						"gstin": aGstin,
						"fromTaxPeriod": vFromDate,
						"toTaxPeriod": vToDate
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				var aData = that._summaryJSON(data.resp, controller);
				var aTabId = that._tableList();
				for (var i = 0; i < aTabId.length; i++) {
					controller.byId(aTabId[i]).setVisibleRowCount(3);
				}
				controller.getView().setModel(new JSONModel(aData), "ReviewSummary");
				controller.getView().setModel(new JSONModel(aData), "Summary");
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
				if (jqXHR.status === 500) {
					that._errorMessage("Internal Server Error");
				} else if (jqXHR.status === 503) {
					that._errorMessage("Service Unavailable");
				} else if (jqXHR.status === 504) {
					that._errorMessage("Gateway Timeout");
				}
			});
		},

		_summaryJSON: function (data, controller) {
			var aData = [];
			var oBundle = controller.getResourceBundle();
			for (var vField in data) {
				aData[vField] = [];
				for (var vCol in data[vField]) {
					if (vField === "docIssued") {
						var oSummJson = this._jsonDocIssued();
					} else if (vField === "nil") {
						oSummJson = this._jsonNilRated();
					} else {
						oSummJson = this._jsonSummStuct();
					}
					oSummJson.type = oBundle.getText(vCol);
					if (vCol === "ey") {
						this._eyJSON(data, oSummJson, vField, vCol);

					} else if (vField === "docIssued") {
						oSummJson.tableSection = "";
						oSummJson.records = data[vField][vCol].records;
						oSummJson.totalIssued = data[vField][vCol].totalIssued;
						oSummJson.netIssued = data[vField][vCol].netIssued;
						oSummJson.cancelled = data[vField][vCol].cancelled;

					} else if (vField === "nil") {
						oSummJson.tableSection = "";
						oSummJson.taxableValue = data[vField][vCol].taxableValue;
						oSummJson.totalExempted = data[vField][vCol].totalExempted;
						oSummJson.totalNilRated = data[vField][vCol].totalNilRated;
						oSummJson.totalNonGST = data[vField][vCol].totalNonGST;
					} else {
						oSummJson.tableSection = "";
						oSummJson.records = data[vField][vCol].records;
						oSummJson.invValue = data[vField][vCol].invValue;
						oSummJson.taxableValue = data[vField][vCol].taxableValue;
						oSummJson.taxPayble = data[vField][vCol].taxPayble;
						oSummJson.igst = data[vField][vCol].igst;
						oSummJson.cgst = data[vField][vCol].cgst;
						oSummJson.sgst = data[vField][vCol].sgst;
						oSummJson.cess = data[vField][vCol].cess;
					}
					aData[vField].push(oSummJson);
				}
			}
			return aData;
		},

		_eyJSON: function (data, oSummJson, vField, vCol) {
			if (vField === "docIssued") {
				if (data[vField][vCol].length > 1) {
					for (var i = 0; i < data[vField][vCol].length; i++) {
						data[vField][vCol][i].type = data[vField][vCol][i].tableSection;
						oSummJson.items.push(data[vField][vCol][i]);
						this._docIssueJSON(oSummJson, data, vField, vCol, i);
					}
				} else {
					this._docIssueJSON(oSummJson, data, vField, vCol, 0);
				}
			} else if (vField === "nil") {
				if (data[vField][vCol].length > 1) {
					for (i = 0; i < data[vField][vCol].length; i++) {
						data[vField][vCol][i].type = data[vField][vCol][i].tableSection;
						oSummJson.items.push(data[vField][vCol][i]);
						this._nilSummJSON(oSummJson, data, vField, vCol, i);
					}
				} else {
					this._nilSummJSON(oSummJson, data, vField, vCol, 0);
				}
			} else {
				if (data[vField][vCol].length > 1) {
					for (i = 0; i < data[vField][vCol].length; i++) {
						data[vField][vCol][i].type = data[vField][vCol][i].tableSection;
						oSummJson.items.push(data[vField][vCol][i]);
						this._summJSON(oSummJson, data, vField, vCol, i);
					}
				} else if (data[vField][vCol].length === 1) {
					this._summJSON(oSummJson, data, vField, vCol, 0);
				}
			}
		},

		_summJSON: function (oSummJson, data, vField, vCol, i) {
			oSummJson.tableSection = data[vField][vCol][i].tableSection;
			oSummJson.records += data[vField][vCol][i].records;
			oSummJson.invValue += data[vField][vCol][i].invValue;
			oSummJson.taxableValue += data[vField][vCol][i].taxableValue;
			oSummJson.taxPayble += data[vField][vCol][i].taxPayble;
			oSummJson.igst += data[vField][vCol][i].igst;
			oSummJson.cgst += data[vField][vCol][i].cgst;
			oSummJson.sgst += data[vField][vCol][i].sgst;
			oSummJson.cess += data[vField][vCol][i].cess;
		},

		_docIssueJSON: function (oSummJson, data, vField, vCol, i) {
			oSummJson.tableSection = data[vField][vCol][i].tableSection;
			oSummJson.records += data[vField][vCol][i].records;
			oSummJson.totalIssued += data[vField][vCol][i].totalIssued;
			oSummJson.netIssued += data[vField][vCol][i].netIssued;
			oSummJson.cancelled += data[vField][vCol][i].cancelled;
		},

		_nilSummJSON: function (oSummJson, data, vField, vCol, i) {
			oSummJson.tableSection = data[vField][vCol][i].tableSection;
			oSummJson.taxableValue += data[vField][vCol][i].taxableValue;
			oSummJson.totalNilRated += data[vField][vCol][i].totalNilRated;
			oSummJson.totalExempted += data[vField][vCol][i].totalExempted;
			oSummJson.totalNonGST += data[vField][vCol][i].totalNonGST;
		},

		/*===================================================================*/
		/*========= Press Toggle Open State =================================*/
		/*===================================================================*/
		_toggleOpenState: function (oEvent) {
			if (oEvent.getParameters().expanded) {
				var aData = oEvent.getSource().getBinding().getModel("Summary").getData();
				var vPath = oEvent.getSource().getBinding().getPath();
				var vLength = oEvent.getSource().getVisibleRowCount() + aData[(vPath.split("/")[1])][0].items.length;
				if (vLength < 10) {
					oEvent.getSource().setVisibleRowCount(vLength);
				} else {
					oEvent.getSource().setVisibleRowCount(10);
				}
				oEvent.getSource().setProperty("expandFirstLevel", true);
			} else {
				var vKey = oEvent.getSource().getEventingParent().getHeaderToolbar().getContent()[2].getSelectedKey();
				if (vKey === "") {
					oEvent.getSource().setVisibleRowCount(3);
				} else {
					oEvent.getSource().setVisibleRowCount(1);
				}
				oEvent.getSource().setProperty("expandFirstLevel", false);
			}
		},

		/*===================================================================*/
		/*========= On filter of Table type =================================*/
		/*===================================================================*/
		_filterSummary: function (oEvent, controller) {
			var aTabId = this._tableList();
			var vKey = oEvent.getSource().getSelectedKey();
			var data = this._filterData(controller.getView().getModel("ReviewSummary").getData(), vKey);
			controller.getView().setModel(new JSONModel(data), "Summary");

			for (var i = 0; i < aTabId.length; i++) {
				if (vKey !== "") {
					controller.byId(aTabId[i]).setVisibleRowCount(1);
				} else {
					controller.byId(aTabId[i]).setVisibleRowCount(3);
				}
			}
		},

		_filterData: function (data, vValue) {
			var aData = [];
			for (var vField in data) {
				aData[vField] = [];
				for (var i = 0; i < data[vField].length; i++) {
					if (vValue === "" || data[vField][i].type === vValue) {
						aData[vField].push(data[vField][i]);
					}
				}
			}
			return aData;
		},

		/*===================================================================*/
		/*========= On press Expand All & Collapse All ======================*/
		/*===================================================================*/
		_pressExpandCollapse: function (oEvent, controller) {
			var vId = oEvent.getSource().getId();
			var aTabId = this._tableList();
			var vSummKey = controller.byId("slSummary").getSelectedKey();
			for (var i = 0; i < aTabId.length; i++) {
				var oTable = controller.byId(aTabId[i]);
				if (oTable) {
					if (vId.indexOf("bExpand") > -1 && (vSummKey === "" || vSummKey === "EY") && !oTable.getProperty("expandFirstLevel")) {
						this._expandAll(oTable, aTabId[i]);
					} else if (vId.indexOf("bCollapse") > -1 && oTable.getProperty("expandFirstLevel")) {
						this._collapseAll(oTable, vSummKey);
					}
				}
			}
		},

		_expandAll: function (oTable, vTab) {
			var oData = oTable.getModel("Summary").getData()[vTab];
			if (oData) {
				var vLength = oTable.getVisibleRowCount() + oData[0].items.length;
				if (vLength < 10) {
					oTable.setVisibleRowCount(vLength);
				} else {
					oTable.setVisibleRowCount(10);
				}
				oTable.expandToLevel(1);
				oTable.setProperty("expandFirstLevel", true);
			}
		},

		_collapseAll: function (oTable, vKey) {
			if (vKey !== "") {
				oTable.setVisibleRowCount(1);
			} else {
				oTable.setVisibleRowCount(3);
			}
			oTable.collapseAll();
			oTable.setProperty("expandFirstLevel", false);
		},

		/*===================================================================*/
		/*========= On press Link of Table or Section =======================*/
		/*===================================================================*/
		_pressLink: function (oEvent, controller) {
			var that = this;
			var aEntity = controller.byId("slSummEntity").getSelectedKeys();
			var oObject = oEvent.getSource().getBindingContext("Summary").getObject();
			var vCateg = oEvent.getSource().getEventingParent().getParent().getParent().getExtension()[0].getContent()[0].getText();
			that.type = oObject.type;

			var vIconTab = (that.type === "GSTN") ? "GSTNInvoiceManagement" : "InvoiceMangement";
			var vTabBar = (that.type === "GSTN") ? "ProcessGSTIN" : "ProcessASP";
			var vSegButton = (that.type === "GSTN") ? "gstinSecManage" : "sbManage";
			var vCriteria = (that.type === "GSTN") ? "slGstinCriteria" : "slManageCriteria";
			var vLabel = (that.type === "GSTN") ? "lGstnDate" : "lManageDate";
			var vDateRange = (that.type === "GSTN") ? "drsGstnManage" : "drsManage";
			var vEntity = (that.type === "GSTN") ? "slGstnEntity" : "slManageEntity";
			that.vGstin = (that.type === "GSTN") ? "slSuppGstn" : "slManageGSTIN";

			controller.byId("idIconTabBar").setSelectedKey(vIconTab);
			controller.byId(vSegButton).setSelectedKey(vTabBar);
			controller.byId(vCriteria).setSelectedKey("RETURN_DATE_SEARCH");
			controller.byId(vLabel).setLabel("From Period - To Period");
			controller.byId(vDateRange).setDisplayFormat("MMM yyyy");
			controller.byId(vDateRange).setDateValue(controller.byId("drsTaxPeriod").getDateValue());
			controller.byId(vDateRange).setSecondDateValue(controller.byId("drsTaxPeriod").getSecondDateValue());
			controller.byId(vEntity).setSelectedKeys(aEntity);

			if (that.type !== "GSTN") {
				// controller.aspInfo = false;
				controller.table = vCateg;
				controller.section = oObject.tableSection;
			}
			if (aEntity.length > 0) {
				controller.getView().setBusy(true);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstins.do",
					data: JSON.stringify({
						"req": {
							"entityId": aEntity
						}
					}),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					var oSupplierGSTIN = new JSONModel(data);
					controller.byId(that.vGstin).setModel(oSupplierGSTIN, "SuppGstinModel");
					controller.byId(that.vGstin).setSelectedKeys(controller.byId("mcbSummaryGSTIN").getSelectedKeys());
					if (that.type === "GSTN") {
						sap.ui.require(["com/ey/digigst/util/gstr1/ManageGSTIN"], function (ManageGSTIN) {
							// 			ManageGSTIN._displayGSTINProcessedData(controller, false);
						});
					} else {
						sap.ui.require(["com/ey/digigst/util/gstr1/ManageASP"], function (ManageASP) {
							ManageASP._getProcessedData(controller, false);
						});
					}
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			} else {
				if (that.type === "GSTN") {
					sap.ui.require(["com/ey/digigst/util/gstr1/ManageGSTIN"], function (ManageGSTIN) {
						// 			ManageGSTIN._displayGSTINProcessedData(controller, false);
					});
				} else {
					var ManageASP = sap.ui.require("com/ey/digigst/util/gstr1/ManageASP");
					ManageASP._getProcessedData(controller, false);
				}
			}
		},

		/*===================================================================*/
		/*========= Check Status ============================================*/
		/*===================================================================*/
		_checkStatus: function (controller) {
			var that = this;
			if (!controller.getView().byId("idCheckStatus")) {
				Fragment.load({
					id: controller.getView().getId(),
					name: "com.ey.digigst.fragments.gstr1.CheckStatus",
					controller: controller
				}).then(function (oDialog) {
					controller.getView().addDependent(oDialog);
				});
			}
			controller.getView().setBusy(true);
			var aEntity = controller.byId("slSummEntity").getSelectedKeys();
			var aGstin = controller.byId("mcbSummaryGSTIN").getSelectedKeys();
			var vFromDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getDateValue())).substr(2, 6);
			var vToDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getSecondDateValue())).substr(2, 6);

			$.ajax({
				method: "POST",
				url: "/aspsapapi/getCheckStatus.do",
				data: JSON.stringify({
					"req": {
						"entityId": aEntity,
						"gstin": aGstin,
						"retPeriodFrom": vFromDate,
						"retPeriodTo": vToDate
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				var aData = that._checkStatusJSON(data.resp);
				controller.byId("idItemTab").setModel(new JSONModel(aData), "CheckStats");
				controller.getView().byId("idCheckStatus").open();
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		_checkStatusJSON: function (data) {
			for (var i = 0; i < data.length; i++) {
				data[i].sNo = i + 1;
			}
			return data;
		},

		/*===================================================================*/
		/*========= on Press Approval Button ================================*/
		/*===================================================================*/
		_pressApproval: function (oEvent, controller) {
			var vButton = oEvent.getSource().getId();
			// 			var aGstin = controller.byId("mcbSummaryGSTIN").getSelectedKeys();
			if (vButton.indexOf("bReqAprvl") > -1) {
				this._requestForApproval(oEvent, controller);
			} else if (vButton.indexOf("bGetAprvl") > -1) {
				// if (aGstin.length !== 1) {
				// 	MessageToast.show("Select only one GSTIN");
				// 	return;
				// }
				this._getApprovalStatus(oEvent, controller);
			}
		},

		_getApprovalStatus: function (oEvent, controller) {
			var aGstin = controller.byId("mcbSummaryGSTIN").getSelectedKeys();
			var vFromDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getDateValue())).substr(2, 6);
			// 			var vToDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getSecondDateValue())).substr(2, 6);
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			if (aGstin.length !== 1) {
				// clearInterval();
				MessageToast.show("Select only one GSTIN");
				return;
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getApprovalStatus.do",
				data: JSON.stringify({
					"req": {
						"retPeriod": vFromDate,
						"gstin": aGstin[0],
						"docType": "INV"
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				if (!data.resp) {
					MessageToast.show("No Status for selected GSTIN and period");
				} else {
					MessageBox.information(Formatter._requestStatus(data.resp.status), {
						styleClass: "sapUiSizeCompact"
					});
				}
				// controller.byId("txtAprStatus").setText(Formatter._requestStatus(data.resp.status));
				// setInterval(function () {
				// 	that._getApprovalStatus(oEvent, controller);
				// }, 30000);
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		_requestForApproval: function (oEvent, controller) {
			var aEntity = controller.byId("slSummEntity").getSelectedKeys();
			var aGstin = controller.byId("mcbSummaryGSTIN").getSelectedKeys();
			var vFromDate = (controller._formatPeriod(controller.byId("drsTaxPeriod").getDateValue())).substr(2, 6);
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			if (aGstin.length !== 1) {
				MessageToast.show("Select only one GSTIN");
				return;
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/requestForApproval.do",
				data: JSON.stringify({
					"req": {
						"groupId": "1",
						"entityId": aEntity[0],
						"retPeriod": vFromDate,
						"gstin": aGstin[0],
						"docType": "INV",
						"status": "1"
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				if (!data.resp) {
					MessageToast.show("No Status for selected GSTIN and period");
				} else {
					MessageBox.information(Formatter._requestStatus(data.resp.status), {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*===================================================================*/
		/*========= On Download Summary =====================================*/
		/*===================================================================*/
		_DownloadSummary: function (oEvent) {
			MessageToast.show("Download");
		}
	};
});