sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox",
	"sap/m/Button",
	"sap/m/Dialog",
	"sap/m/Token"
], function (Controller, JSONModel, Fragment, MessageBox, Button, Dialog, Token) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.IMS", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.IMS
		 */
		onInit: function () {
			var obj = {
				"btnSegment": "IMSGetCall",
				"getIMSFullScreen": false,
				"imsSummary": "entity",
				"imsRecords": "entity",
				"title": "IMS Get Call",
				"navBack": false
			};
			this.getView().setModel(new JSONModel(obj), "ViewProperty");
			this._setDateProperty("iFromDate");
			this._setDateProperty("iToDate");
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onSegmentBtnChange();
			}
			var oModel = this.getView().getModel("ViewProperty");
			oModel.setProperty("/imsSummary", "entity");
			oModel.refresh(true);
		},

		onSegmentBtnChange: function () {
			var oModel = this.getView().getModel("ViewProperty"),
				key = oModel.getProperty("/btnSegment");

			switch (key) {
			case "IMSGetCall":
				oModel.setProperty("/title", "IMS Get Call");
				this.getIMSData();
				break;
			case "IMSSummary":
				oModel.setProperty("/title", "IMS Summary");
				oModel.setProperty("/imsSummary", "entity");
				this.onGoSummary();
				break;
			case "IMSRecords":
				oModel.setProperty("/title", "IMS Records");
				oModel.setProperty("/imsRecords", "entity");
				var vPageNo = this.byId("inPageNoN").getValue();
				this._bindReqIdFilter();
				this.getIMSStatusData(vPageNo);
				break;
			}
			oModel.setProperty("/navBack", false);
			oModel.refresh(true);
		},

		/**
		 * Get IMS Count
		 */
		onGet2aFullScreen: function (action) {
			var oPropModel = this.getView().getModel("ViewProperty"),
				oPropData = oPropModel.getData();

			if (action === "openProcess") {
				oPropData.getIMSFullScreen = true;
				this.byId("idCcIMS").setFullScreen(true);
				this.byId("idTableIMS").setVisibleRowCount(18);

			} else if (action === "closeProcess") {
				oPropData.getIMSFullScreen = false;
				this.byId("idCcIMS").setFullScreen(false);
				this.byId("idTableIMS").setVisibleRowCount(8);
			}
			oPropModel.refresh(true);
		},

		getIMSData: function () {
			var aGstin = this.byId("IdimsGetCallGstin").getSelectedKeys(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": aGstin.includes("All") ? [] : aGstin
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getImsCalls.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data), "getIMS");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

		},

		clearImsGetCall: function () {
			this.byId("IdimsGetCallGstin").setSelectedKeys();
			this.getIMSData();
		},

		onPressInvoivceGetCallStatus: function (gstin) {
			if (!this._InvGetCallStatus) {
				this._InvGetCallStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.InvGetCallStatus", this);
				this.getView().addDependent(this._InvGetCallStatus);
			}
			this._InvGetCallStatus.open();
			this._InvGetCallStatus.setModel(new JSONModel({
				"gstin": gstin
			}), "StatusProperty")
			this._bindInvoivceGetCallStatus(gstin);
		},

		_bindInvoivceGetCallStatus: function (gstin) {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": gstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getImsDetailCallPopup.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.bindGetImsDetailCallPopup(data);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		bindGetImsDetailCallPopup: function (oData) {
			var data = {};
			oData.resp.forEach(function (item) {
				switch (item.type) {
				case "B2B":
					data.B2BStatus = item.status || "";
					data.B2BTimeStamp = item.timeStamp || "";
					break;
				case "B2BA":
					data.B2BAStatus = item.status || "";
					data.B2BATimeStamp = item.timeStamp || "";
					break;
				case "CN":
					data.CNStatus = item.status || "";
					data.CNTimeStamp = item.timeStamp || "";
					break;
				case "CNA":
					data.CNAStatus = item.status || "";
					data.CNATimeStamp = item.timeStamp || "";
					break;
				case "DN":
					data.DNStatus = item.status || "";
					data.DNTimeStamp = item.timeStamp || "";
					break;
				case "DNA":
					data.DNAStatus = item.status || "";
					data.DNATimeStamp = item.timeStamp || "";
					break;
				case "ECOM":
					data.ECOMStatus = item.status || "";
					data.ECOMTimeStamp = item.timeStamp || "";
					break;
				case "ECOMA":
					data.ECOMAStatus = item.status || "";
					data.ECOMATimeStamp = item.timeStamp || "";
					break;
				}
			});
			this._InvGetCallStatus.setModel(new JSONModel([data]), "InvGetCallStatus");
		},

		onCloseInvGetCallStatus: function () {
			this._InvGetCallStatus.close();
		},

		onPressGetIMSInv: function (view) {
			var oData = this.byId("idTableIMS").getModel("getIMS").getProperty('/resp'),
				aIndex = this.getView().byId("idTableIMS").getSelectedIndices(),
				postData = {
					"req": []
				};

			if (!aIndex.length) {
				MessageBox.warning("Select at least one GSTIN");
				return;
			}
			aIndex.forEach(function (idx) {
				postData.req.push({
					"gstin": oData[idx].gstin,
					"gstr2aSections": ["B2B", "B2BA", "CN", "CNA", "DN", "DNA", "ECOM", "ECOMA"]
				});
			});
			this.onPressGetIMSInvFinal(postData);
		},

		onPressGetIMSInvFinal: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/initaiteImsGetCall.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {
							aMockMessages.push({
								type: 'Success',
								title: data.resp[i].gstin,
								gstin: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});
						}
					}
					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr2aGstnGetSection.do : Error");
				});
			});
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get IMS Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.getIMSData();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},
		// IMS Count End

		onDownloadImsGetCall: function (type) {
			var oGetCallModel = this.byId("idTableIMS").getModel("getIMS").getProperty("/resp"),
				oSelectedItem = this.getView().byId("idTableIMS").getSelectedIndices(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [],
						"tableType": [],
						"type": "getCallTableData"
					}
				};
			if (!oSelectedItem.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			oSelectedItem.forEach(function (e) {
				payload.req.gstins.push(oGetCallModel[e].gstin);
			});
			this.excelDownload(payload, "/aspsapapi/getImsTableReportsDownload.do");
		},

		/**
		 * Get IMS Summmary
		 */
		onPressSaveStatus: function (gstin) {
			if (!this._dCheckStatus) {
				this._dCheckStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.CheckStatus", this);
				this.getView().addDependent(this._dCheckStatus);
			}
			this.gstin = gstin;
			this._dCheckStatus.open();
			this._dCheckStatus.setModel(new JSONModel(this._getCheckStatusObj(gstin)), "CheckStatusProperty")
			this.onSegmentBtnCSChange();
		},

		onSelectGstin: function (gstin) {
			var oModel = this.getView().getModel("ViewProperty");
			this.byId("idGstinGstinLev").setSelectedKey(gstin);
			oModel.setProperty("/imsSummary", "gstn");
			oModel.setProperty("/navBack", true);
			oModel.refresh(true);
			this._bindGstinSummary();
		},

		navBackToEntitySummary: function () {
			var oModel = this.getView().getModel("ViewProperty"),
				key = oModel.getProperty("/btnSegment");
			if (key === "IMSSummary") {
				oModel.setProperty("/imsSummary", "entity");
				oModel.setProperty("/navBack", false);
				oModel.refresh(true);
			} else {
				oModel.setProperty("/title", "IMS Records");
				oModel.setProperty("/imsRecords", "entity");
				oModel.setProperty("/navBack", false);
				var vPageNo = this.byId("inPageNoN").getValue();
				this._bindReqIdFilter();
				this.getIMSStatusData(vPageNo);
			}
		},

		onImsSummaryCheckStatus: function () {
			if (!this._dCheckStatus) {
				this._dCheckStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.CheckStatus", this);
				this.getView().addDependent(this._dCheckStatus);
			}
			this._dCheckStatus.open();
			var vGstin = this.byId("idGstinGstinLev").getSelectedKey();
			this._dCheckStatus.setModel(new JSONModel(this._getCheckStatusObj(vGstin)), "CheckStatusProperty")
			this.gstin = vGstin;
			this.onSegmentBtnCSChange();
		},

		_getCheckStatusObj: function (gstin) {
			return {
				"csbtnSegment": "OngoingSave",
				"gstin": gstin,
				"pgSizeI": 25,
				"pgSizeQ": 25
			};
		},

		onCloseCheckStatus: function () {
			this._dCheckStatus.close();
		},

		onSegmentBtnCSChange: function () {
			var key = this._dCheckStatus.getModel("CheckStatusProperty").getProperty("/csbtnSegment");
			switch (key) {
			case "OngoingSave":
				this._bindCheckStatus();
				break;
			case "QueueStatus":
				this._bindQueueStatus();
				break;
			}
		},

		_bindCheckStatus: function () {
			var oProp = this._dCheckStatus.getModel("CheckStatusProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (oProp.pageNoI || 1) - 1,
						"pageSize": oProp.pgSizeI
					},
					"req": {
						"entityId": $.sap.entityID,
						"gstin": this.gstin
					}
				};
			this._dCheckStatus.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsSummarySaveStatusPopup.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._dCheckStatus.setBusy(false);
					this._dCheckStatus.setModel(new JSONModel(data), "CheckStatus");
					this._countPagination(data.hdr, "I");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dCheckStatus.setBusy(false);
				}.bind(this));
		},

		_bindQueueStatus: function () {
			var oProp = this._dCheckStatus.getModel("CheckStatusProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (oProp.pageNoQ || 1) - 1,
						"pageSize": oProp.pgSizeQ
					},
					"req": {
						"entityId": $.sap.entityID,
						"gstin": this.gstin
					}
				};
			this._dCheckStatus.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsSummarySaveQueueStatus.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._dCheckStatus.setBusy(false);
					this._dCheckStatus.setModel(new JSONModel(data), "QueueStatus");
					this._countPagination(data.hdr, "Q");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dCheckStatus.setBusy(false);
				}.bind(this));
		},

		_countPagination: function (hdr, type) {
			var oModel = this._dCheckStatus.getModel("CheckStatusProperty"),
				vTotal = Math.ceil(hdr.totalCount / oModel.getProperty("/pgSize" + type)),
				vPageNo = (vTotal ? hdr.pageNum + 1 : 0);

			oModel.setProperty("/pageNo" + type, vPageNo);
			oModel.setProperty("/pgTotal" + type, vTotal);
			oModel.setProperty("/ePageNo" + type, vTotal > 1);
			oModel.setProperty("/bFirst" + type, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev" + type, vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext" + type, vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast" + type, vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onPaginationChkStats: function (type, btn) {
			var oModel = this._dCheckStatus.getModel("CheckStatusProperty");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo" + type, 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo" + type, oModel.getProperty("/pageNo" + type) - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo" + type, oModel.getProperty("/pageNo" + type) + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo" + type, oModel.getProperty("/pgTotal" + type));
				break;
			}
			oModel.refresh(true);
			if (type === "I") {
				this._bindCheckStatus();
			} else {
				this._bindQueueStatus();
			}
		},

		onSubmitPaginationChkStats: function (table, type) {
			var oModel = this._dCheckStatus.getModel("CheckStatusProperty");
			if (type === 'S') {
				oModel.setProperty("/pageNo" + table, 1);
				oModel.refresh(true);
			}
			if (table === "I") {
				this._bindCheckStatus();
			} else {
				this._bindQueueStatus();
			}
		},

		onDownloadCheckStatus: function (oEvent) {
			var gstin = this._dCheckStatus.getModel("CheckStatusProperty").getProperty("/gstin"),
				obj = oEvent.getSource().getBindingContext("CheckStatus").getObject(),
				payload = {
					"req": {
						"gstin": gstin,
						"section": obj.section,
						"refId": obj.refId,
						"type": "JSON"
					}
				};
			if (obj.status === "PE") {
				if (!this.oConfirmDialog) {
					this.oConfirmDialog = new Dialog({
						title: "Confirmation",
						icon: "sap-icon://sys-help-2",
						content: [
							new sap.m.VBox({
								alignItems: "Center",
								items: [
									new sap.m.Text({
										text: "{DialogProp>/title}"
									}).addStyleClass("sapUiTinyMargin"),
									new sap.m.RadioButtonGroup({
										selectedIndex: "{DialogProp>/select}",
										columns: 2,
										buttons: [
											new sap.m.RadioButton({
												text: "JSON"
											}),
											new sap.m.RadioButton({
												text: "Excel"
											})
										]
									})
								]
							})
						],
						buttons: [
							new sap.m.Button({
								text: "OK",
								press: function () {
									var payload = this.oConfirmDialog.getModel("DialogProp").getProperty("/payload"),
										key = this.oConfirmDialog.getModel("DialogProp").getProperty("/select");
									payload.req.type = (!key ? "JSON" : "XLSX");
									this.excelDownload(payload, "/aspsapapi/imsErrorCountJsonDownloadDocument.do");
									this.oConfirmDialog.close();
								}.bind(this)
							})
						]
					}).addStyleClass("sapUiSizeCompact");
				}
				this.oConfirmDialog.setModel(new JSONModel({
					"title": "Please select the format to download file.",
					"select": 0,
					"payload": payload
				}), "DialogProp");
				this.oConfirmDialog.open();
			} else {
				this.excelDownload(payload, "/aspsapapi/imsErrorCountJsonDownloadDocument.do");
			}
		},

		onExpandCollapseImsSumamry: function (value) {
			if (value === "E") {
				this.byId("tabImsGstn").expandToLevel(2);
			} else {
				this.byId("tabImsGstn").collapseAll();
			}
		},

		onClearMainFilter: function () {
			var oModel = this.getView().getModel("ViewProperty"),
				key = oModel.getProperty("/imsSummary");
			if (key == "entity") {
				this.byId("idGstinEntityLev").setSelectedKeys();
				this.byId("idTableType").setSelectedKeys();
				this._bindEntitySummary();
			} else {
				this._bindGstinSummary();
			}
		},

		onGoSummary: function () {
			var oModel = this.getView().getModel("ViewProperty"),
				key = oModel.getProperty("/imsSummary");
			if (key == "entity") {
				this._bindEntitySummary();
			} else {
				this._bindGstinSummary();
			}
		},

		_bindEntitySummary: function () {
			var aGstin = this.byId("idGstinEntityLev").getSelectedKeys(),
				aTableType = this.byId("idTableType").getSelectedKeys(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": aGstin.includes("All") ? [] : aGstin,
						"tableType": aTableType.includes("All") ? [] : aTableType
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getImsSummaryEntityLvl.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.forEach(function (el) {
						el.totalDiff = Math.abs(el.gstnTotal - el.recordCountsAll);
					});
					this.getView().setModel(new JSONModel(data), "EntitySummary");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onEntityViewDifference: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("EntitySummary").getObject();
			if (!this._dImsDiff) {
				this._dImsDiff = this.loadFragment({
					"id": this.getView().getId(),
					"name": "com.ey.digigst.fragments.gstr2.IMS.ViewDifference",
					"controller": this
				});
			}
			this._dImsDiff.then(function (oDialog) {
				oDialog.setModel(new JSONModel({
					"gstnTotal": obj.gstnTotal,
					"aspTotal": obj.recordCountsAll,
					"diffTotal": obj.totalDiff
				}), "Difference");
				oDialog.open();
			});
		},

		onCloseViewDifference: function () {
			this._dImsDiff.then(function (oDialog) {
				oDialog.close();
			});
		},

		_bindGstinSummary: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": this.byId("idGstinGstinLev").getSelectedKey(),
					"tableType": []
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getImsSummaryGstinLvl.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._bindTotalCountSummary(data);
					this.getView().setModel(new JSONModel(data.resp), "GstnSummary");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

		},

		_bindTotalCountSummary: function (data) {
			var oCount = {
				"gstnTotal": 0,
				"gstnNoAction": 0,
				"gstnAccepted": 0,
				"gstnRejected": 0,
				"gstnPendingTotal": 0,
				"countAPIDateTime": "(IMS Get Call " + data.data.timeStamp + ")"
			};

			data.resp.forEach(function (entry) {
				oCount.gstnTotal += entry.gstnTotal || 0;
				oCount.gstnNoAction += entry.gstnNoAction || 0;
				oCount.gstnAccepted += entry.gstnAccepted || 0;
				oCount.gstnRejected += entry.gstnRejected || 0;
				oCount.gstnPendingTotal += entry.gstnPendingTotal || 0;
				this._getGstnDifferenceTotal(entry);
				entry.viewDiff = (typeof (entry.aspTotal) === "number");
				entry.diffTotal = Math.abs(entry.gstnTotal - entry.recordCountsAll);
			}.bind(this));
			this.getView().setModel(new JSONModel(oCount), "GstnSummaryCount");
		},

		_getGstnDifferenceTotal: function (data) {
			data.items.forEach(function (entry) {
				if (entry.items) {
					entry.recordCountsAll = 0;
					entry.items.forEach(function (el) {
						el.viewDiff = (typeof (el.aspTotal) === "number");
						el.diffTotal = Math.abs(el.gstnTotal - el.recordCountsAll);
						entry.recordCountsAll += el.recordCountsAll;
					});
				}
				data.recordCountsAll += entry.recordCountsAll;
				entry.viewDiff = (typeof (entry.aspTotal) === "number");
				entry.diffTotal = Math.abs(entry.gstnTotal - entry.recordCountsAll);
			});
		},

		onGstnViewDifference: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("GstnSummary").getObject();
			if (!this._dImsDiff) {
				this._dImsDiff = this.loadFragment({
					"id": this.getView().getId(),
					"name": "com.ey.digigst.fragments.gstr2.IMS.ViewDifference",
					"controller": this
				});
			}
			this._dImsDiff.then(function (oDialog) {
				oDialog.setModel(new JSONModel({
					"gstnTotal": obj.gstnTotal,
					"aspTotal": obj.recordCountsAll,
					"diffTotal": obj.diffTotal
				}), "Difference");
				oDialog.open();
			});
		},

		onDownloadImsSummaryTabData: function () {
			var oImsSummModel = this.getModel("EntitySummary").getProperty("/resp"),
				oSelectedItem = this.getView().byId("imsSummaryEntity").getSelectedIndices(),
				aTableType = this.removeAll(this.byId("idTableType").getSelectedKeys()),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [],
						"tableType": [],
						"type": "ImsSummaryTableData"
					}
				};
			if (!oSelectedItem.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			oSelectedItem.forEach(function (e) {
				payload.req.gstins.push(oImsSummModel[e].gstin);
			});
			aTableType.forEach(function (e) {
				payload.req.tableType.push(e.toUpperCase());
			});
			this.excelDownload(payload, "/aspsapapi/getImsTableReportsDownload.do");
		},

		onDownloadImsRecordReport: function (type, rptType) {
			var payload = {
				"req": {
					"type": "IMSSummary",
					"entityId": $.sap.entityID,
					"gstins": [],
					"tableType": [],
					"fromDate": "",
					"toDate": "",
					"docNums": [],
					"id": [],
					"docType": [],
					"vendorPan": [],
					"vendorGstins": [],
					"digiGstAction": [],
					"gstnAction": [],
					"reportType": rptType
				}
			};
			if (type === "G") {
				payload.req.gstins.push(this.byId("idGstinGstinLev").getSelectedKey());
			} else {
				var oImsSummModel = this.getModel("EntitySummary").getProperty("/resp"),
					oSelectedItem = this.getView().byId("imsSummaryEntity").getSelectedIndices(),
					aTableType = this.removeAll(this.byId("idTableType").getSelectedKeys()),
					aTables = this.getOwnerComponent().getModel("DropDown").getProperty("/get2aTableType1");

				if (!oSelectedItem.length) {
					sap.m.MessageBox.warning("Select at least one GSTIN");
					return;
				}
				payload.req.gstins = oSelectedItem.map(function (e) {
					return oImsSummModel[e].gstin;
				});
				payload.req.tableType = aTableType.map(function (e) {
					var obj = aTables.find(function (type) {
						return type.text === e;
					});
					return obj.key.toUpperCase();
				});
			}
			if (rptType !== "Amendment") {
				this.reportDownload(payload, "/aspsapapi/getImsRecordsSummaryReportData.do");
			} else {
				if (!this._dAmendmentTrack) {
					this._dAmendmentTrack = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.AmendmentTrack", this);
					this.getView().addDependent(this._dAmendmentTrack);
					this._setDateProperty("frAmendRep");
					this._setDateProperty("toAmendRep");
				}
				var today = new Date();
				this._dAmendmentTrack.setModel(new JSONModel(payload), "ReportPayload");
				this._dAmendmentTrack.setModel(new JSONModel({
					"fromPeriod": new Date(today.getFullYear(), today.getMonth() - 1, 1),
					"toPeriod": today,
					"maxDate": today
				}), "TrackAmendment");
				this._dAmendmentTrack.open();
			}
		},

		onChangeTrackAmendmentDate: function (oEvent) {
			var oModel = this._dAmendmentTrack.getModel("TrackAmendment");

			if (oModel.getProperty("/fromPeriod") > oModel.getProperty("/toPeriod")) {
				oModel.setProperty("/toPeriod", new Date(oModel.getProperty("/fromPeriod")));
			}
			oModel.refresh(true);
		},

		onAmendmentTrackRep: function (type) {
			if (type === "C") {
				this._dAmendmentTrack.close();
				return;
			}
			var payload = this._dAmendmentTrack.getModel("ReportPayload").getProperty("/"),
				oFilter = this._dAmendmentTrack.getModel("TrackAmendment").getProperty("/");

			payload.req.reportType = "IMS Amendment Original Track Report";
			payload.req.fromPeriod = this._formatPeriod(oFilter.fromPeriod);
			payload.req.toPeriod = this._formatPeriod(oFilter.toPeriod);

			this.reportDownload(payload, "/aspsapapi/getImsRecordsSummaryReportData.do");
			this._dAmendmentTrack.close();
		},

		onSelectSaveStatausPopup: function (oEvent) { //eslint-disable-line
			var vSelected = oEvent.getSource().getSelected();
			this._resetSaveToGSTIN(vSelected)
		},

		onSelectTableType: function () {
			var oType = ["b2bFlag", "b2baFlag", "ecomFlag", "ecomaFlag", "cnFlag", "cnaFlag", "dnFlag", "dnaFlag"],
				oModel = this._dSaveToGstin.getModel("GetSection"),
				oData = oModel.getProperty("/"),
				flag = true;

			oType.forEach(function (f) {
				flag = flag && !!oData[f];
			});
			oData.allFlag = flag;
			oModel.refresh(true);
		},

		_resetSaveToGSTIN: function (vSelected) {
			var oType = ["allFlag", "b2bFlag", "b2baFlag", "ecomFlag", "ecomaFlag", "cnFlag", "cnaFlag", "dnFlag", "dnaFlag"],
				oModel = this._dSaveToGstin.getModel("GetSection"),
				oData = oModel.getProperty("/");

			oType.forEach(function (f) {
				oData[f] = vSelected;
			});
			oModel.refresh();
		},

		onEntityLevelSaveToGstn: function () {
			var oData = this.getView().getModel("EntitySummary").getProperty("/resp"),
				aIndex = this.byId('imsSummaryEntity').getSelectedIndices(),
				obj = {
					"type": "entityLevel"
				};

			if (!aIndex.length) {
				MessageBox.warning("Select at least one GSTIN");
				return;
			} else if (aIndex.length > 10) {
				MessageBox.warning("Bulk Save can be initiated for max 10 GSTINs at one time.");
				return;
			}
			if (!this._dSaveToGstin) {
				this._dSaveToGstin = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.SaveToGstin", this);
				this.getView().addDependent(this._dSaveToGstin);
			}
			obj.gstins = aIndex.map(function (idx) {
				return oData[idx].gstin;
			});
			this._dSaveToGstin.setModel(new JSONModel(obj), "GetSection");
			this._dSaveToGstin.open();
		},

		onGstnLevelSaveToGstn: function (oEvent, flag) {
			if (!this._dSaveToGstin) {
				this._dSaveToGstin = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.SaveToGstin", this);
				this.getView().addDependent(this._dSaveToGstin);
			}
			this._dSaveToGstin.setModel(new JSONModel({
				"type": "gstinLevel",
				"gstin": this.byId("idGstinGstinLev").getSelectedKey()
			}), "GetSection");
			this._dSaveToGstin.open();
		},

		onSaveIMSBtnDialog: function (flag) {
			switch (flag) {
			case 'C':
				this._dSaveToGstin.close();
				break;
			case 'R':
				this._resetSaveToGSTIN(false);
				break;
			case 'S':
				var oData = this._dSaveToGstin.getModel("GetSection").getProperty('/'),
					oType = ["b2b", "b2ba", "ecom", "ecoma", "cn", "cna", "dn", "dna"],
					payload = {
						"req": {
							"gstins": (oData.type === "entityLevel" ? oData.gstins : [oData.gstin]),
							"tableType": []
						}
					};

				oType.forEach(function (e) {
					if (oData[e + "Flag"]) {
						payload.req.tableType.push(e.toUpperCase());
					}
				});
				if (!payload.req.tableType.length) {
					MessageBox.information("Select at least one table");
					return;
				}
				MessageBox.confirm("Do you want to save data for selected GSTIN?", {
					initialFocus: MessageBox.Action.CANCEL,
					styleClass: "sapUiSizeCompact",
					onClose: function (oAction) {
						if (oAction === MessageBox.Action.OK) {
							if (oData.type === "entityLevel") {
								this._entitySaveToGstn(payload);
							} else {
								this._saveToGstnFinal(payload);
							}
						}
					}.bind(this)
				});
				break;
			}
		},

		_entitySaveToGstn: function (payload) {
			this._dSaveToGstin.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsBulkSaveToGstn.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._msgSaveToGstn(data);
					this._dSaveToGstin.setBusy(false);
					this._dSaveToGstin.close();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dSaveToGstin.setBusy(false);
				}.bind(this));
		},

		_gstinLevelSaveToGstn: function (payload) {
			MessageBox.confirm("Do you want to save data for selected GSTIN?", {
				initialFocus: MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === MessageBox.Action.OK) {
						this._saveToGstnFinal(payload);
					}
				}.bind(this)
			});
		},

		_saveToGstnFinal: function (oPayload) {
			this._dSaveToGstin.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsSaveToGstn.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this._msgSaveToGstn(data);
					this._dSaveToGstin.setBusy(false);
					this._dSaveToGstin.close();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._dSaveToGstin.setBusy(false);
				}.bind(this));
		},

		_getDialogHighlight: function (msg) {
			if (msg === "Auth Token is Inactive, Please Activate" || msg === "Please select only the Active GSTINs") {
				return "Error";
			} else {
				return "Success"
			}
		},

		_msgSaveToGstn: function (data) {
			var aMessage = [];
			data.resp.forEach(function (e) {
				aMessage.push({
					type: 'Success',
					title: (typeof e.tableType === "undefined" ? '' : e.tableType + ' - '),
					gstin: (e.gstin ? e.gstin : e.msg),
					msg: (!e.gstin ? '' : e.msg),
					active: true,
					icon: "sap-icon://message-success",
					highlight: this._getDialogHighlight(e.msg),
					info: "Success"
				});
			}.bind(this));
			this.onDisplaySaveToGstnMsg(aMessage);
		},

		onDisplaySaveToGstnMsg: function (aMessage) {
			if (!this.dMsgSaveToGstn) {
				this.dMsgSaveToGstn = new Dialog({
					title: "Save to GSTN",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title} {Msg>gstin}",
								description: "{Msg>msg}",
								// icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.dMsgSaveToGstn.close();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.dMsgSaveToGstn);
			}
			this.dMsgSaveToGstn.removeStyleClass("sapUiSizeCompact");
			if (aMessage.length > 1) {
				this.dMsgSaveToGstn.addStyleClass("sapUiSizeCompact");
			}
			this.dMsgSaveToGstn.setModel(new JSONModel(aMessage), "Msg");
			this.dMsgSaveToGstn.open();
		},

		onUploadImsSummary: function () {
			var oUploader = this.byId("fuImsSummary"),
				vIdx = this.byId("rgbImsSummType").getSelectedIndex();

			if (!oUploader.getValue()) {
				MessageBox.information("Please select file to Upload");
				return;
			}
			switch (vIdx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/imsFileUploadDocuments.do");
				break;
			}
			oUploader.upload();
		},

		onImsSummUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuImsSummary").setValue();
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

		onImsSummDownloadTemplate: function () {
			sap.m.URLHelper.redirect("excel/IMS_FormatFile.xlsx", false);
		},

		onDeleteDigiGST: function () {
			var aIndex = this.byId("imsSummaryEntity").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.warning("Select at least one GSTIN");
				return;
			} else if (aIndex.length > 10) {
				MessageBox.warning("Bulk Delete can be initiated for max 10 GSTINs at one time.");
				return;
			}
			if (!this._dDeleteIMS) {
				this._dDeleteIMS = Fragment.load({
						"id": this.getView().getId(),
						"name": "com.ey.digigst.fragments.gstr2.IMS.DeleteIMS",
						"controller": this
					})
					.then(function (oDialog) {
						this._dDeleteIMS = oDialog;
						this.getView().addDependent(oDialog);
						this._openDeleteIMS(aIndex);
					}.bind(this));
			} else {
				this._openDeleteIMS(aIndex);
			}
		},

		_openDeleteIMS: function (aIndex) {
			var oData = this.getView().getModel("EntitySummary").getProperty("/resp"),
				obj = {
					"tabType": {},
					"action": {}
				};
			obj.gstins = aIndex.map(function (idx) {
				return oData[idx].gstin;
			});
			this._dDeleteIMS.setModel(new JSONModel(obj), "DeleteIMS");
			this._dDeleteIMS.open();
		},

		onSelectDeleteTabType: function () {
			var oType = ["b2b", "b2ba", "ecom", "ecoma", "cn", "cna", "dn", "dna"],
				oModel = this._dDeleteIMS.getModel("DeleteIMS"),
				oData = oModel.getProperty("/tabType"),
				flag1 = true;

			oType.forEach(function (f) {
				flag1 = flag1 && !!oData[f];
			});
			oData.all = flag1;
			oModel.refresh(true);
		},

		onSelectDeleteAllTabTypeIMS: function (oEvent) {
			var vSelected = oEvent.getSource().getSelected();
			this._resetDeleteIms(vSelected)
		},

		_resetDeleteIms: function (vSelected) {
			var oType = ["b2b", "b2ba", "ecom", "ecoma", "cn", "cna", "dn", "dna"],
				oModel = this._dDeleteIMS.getModel("DeleteIMS"),
				oData = oModel.getProperty("/tabType");

			oType.forEach(function (f) {
				oData[f] = vSelected;
			});
			oModel.refresh();
		},

		onSelectDeleteAllActionIMS: function (oEvent) {
			var vSelected = oEvent.getSource().getSelected(),
				oModel = this._dDeleteIMS.getModel("DeleteIMS"),
				oData = oModel.getProperty("/action"),
				oType = ["NE", "A", "R", "P"];

			oType.forEach(function (f) {
				oData[f] = vSelected;
			});
			oModel.refresh();
		},

		onSelectDeleteAction: function () {
			var oType = ["NE", "A", "R", "P"],
				oModel = this._dDeleteIMS.getModel("DeleteIMS"),
				oData = oModel.getProperty("/action"),
				flag1 = true;

			oType.forEach(function (f) {
				flag1 = flag1 && !!oData[f];
			});
			oData.all = flag1;
			oModel.refresh(true);
		},

		onCloseImsDelete: function (type) {
			if (type === "C") {
				this._dDeleteIMS.close();
			} else {
				var oData = this._dDeleteIMS.getModel("DeleteIMS").getProperty("/"),
					aTabType = ["b2b", "b2ba", "ecom", "ecoma", "cn", "cna", "dn", "dna"],
					aAction = ["NE", "A", "R", "P"],
					flag1 = false,
					flag2 = false;

				aTabType.forEach(function (tab) {
					flag1 = flag1 || !!oData.tabType[tab];
				});
				aAction.forEach(function (action) {
					flag2 = flag2 || !!oData.action[action];
				});
				if (!flag1) {
					MessageBox.error("Select atleast one Table type to delete.");
					return;
				}
				if (!flag2) {
					MessageBox.error("Select atleast one IMS Action to delete.");
					return;
				}
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": oData.gstins,
						"tableType": [],
						"imsAction": []
					}
				};
				for (var fl in oData.tabType) {
					if (fl !== "all" && oData.tabType[fl]) {
						payload.req.tableType.push(fl.toUpperCase());
					}
				}
				for (var fl in oData.action) {
					if (fl !== "all" && oData.action[fl]) {
						payload.req.imsAction.push(fl);
					}
				}
				MessageBox.confirm("Do you want to delete data for selected GSTINs?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							this._dDeleteIMS.setBusy(true);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/deleteImsSummaryUnsavedData.do",
									data: JSON.stringify(payload),
									contentType: "application/json"
								})
								.done(function (data, status, jqXHR) {
									MessageBox.success(data.hdr.message);
									this._dDeleteIMS.setBusy(false);
									this._dDeleteIMS.close();
								}.bind(this))
								.fail(function (jqXHR, status, err) {
									this._dDeleteIMS.setBusy(false);
								}.bind(this));
						}
					}.bind(this)
				});
			}
		},
		// IMS Summary End

		/**
		 * Get IMS Status
		 */
		_bindReqIdFilter: function () {
			var vDate = new Date();
			var firstDayOfPreviousMonth = new Date(vDate.getFullYear(), vDate.getMonth() - 1, 1);
			this.getView().setModel(new JSONModel({
				"gstin": [],
				"fromDate": firstDayOfPreviousMonth,
				"toDate": new Date(),
				"actionGstn": [],
				"actionDigiGST": [],
				"tableType": [],
				"docType": [],
				"maxDate": new Date()
			}), "StatusFilter");
		},

		onChangeDateValue: function (oEvent) {
			var vDatePicker;
			if (oEvent.getSource().getId().includes("iFromDate")) {
				vDatePicker = "iToDate";
			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			this.byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
		},

		_setTokenValidator: function (id) {
			this.byId(id).addValidator(function (args) {
				var text = args.text.trim();
				return new Token({
					key: text,
					text: text
				});
			});
		},

		onPressAdaptFilter: function () {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
				this._setTokenValidator("iDocNo");
				this._setTokenValidator("iUniqueID");
				this._setTokenValidator("iVenPan");
				this._setTokenValidator("iVenGstin");
			}
			this._oAdpatFilter.open();
		},

		onPressClearIMSRecords: function () {
			this._bindReqIdFilter();
			if (this.byId("dAdapt")) {
				this.byId("iDocNo").setTokens([]);
				this.byId("iUniqueID").setTokens([]);
				this.byId("iVenPan").setTokens([]);
				this.byId("iVenGstin").setTokens([]);
			}
			var vPageNo = this.byId("inPageNoN").getValue();
			this.getIMSStatusData(vPageNo);
		},

		onPressGoIMSRecords: function (key) {
			if (this._oAdpatFilter) {
				this._oAdpatFilter.close();
			}
			if (key === "A") {
				var vPageNo = this.byId("inPageNoN").getValue();
				this.getIMSStatusData(vPageNo);
			}
		},
		// Function to read input values from MultiInput
		readMultiInputValues: function (id) {
			if (!this.byId(id)) {
				return [];
			}
			var aTokens = this.byId(id).getTokens(),
				aValues = aTokens.map(function (oToken) {
					return oToken.getText();
				});
			return aValues;

		},
		onPNChange: function (oEvt) {
			// var vPageNo = this.byId("inPageNoN").getValue();
			this.getIMSStatusData(1);
		},

		getIMSStatusData: function (vPageNo) {
			vPageNo = Number(vPageNo);
			var oFilter = this.getView().getModel("StatusFilter").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": this.byId("idPageN").getSelectedKey()
					},
					"req": {
						"entityId": $.sap.entityID,
						"gstins": this.removeAll(oFilter.gstin),
						"vendorPans": this.readMultiInputValues("iVenPan"),
						"vendorGstins": this.readMultiInputValues("iVenGstin"),
						"tableTypes": this.removeAll(oFilter.tableType),
						"docTypes": this.removeAll(oFilter.docType),
						"docNumbers": this.readMultiInputValues("iDocNo"),
						"imsUniqueIds": this.readMultiInputValues("iUniqueID"),
						"toDocDate": this._formatDate(oFilter.toDate),
						"fromDocDate": this._formatDate(oFilter.fromDate),
						"actionDigiGsts": this.removeAll(oFilter.actionDigiGST),
						"actionGstns": this.removeAll(oFilter.actionGstn)
					}
				};
			if (payload.req.actionDigiGsts.length === 0) {
				payload.req.actionDigiGsts = ["N", "A", "R", "P"]
			}
			if (payload.req.actionGstns.length === 0) {
				payload.req.actionGstns = ["N", "A", "R", "P"]
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsRecordScreenData.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._nicPagination(data.hdr);
					this.getView().setModel(new JSONModel(data), "IMSStatus");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressTrailDigiGST: function (oEvent, docNumber, docDate, imsUniqueId, docKey) {
			if (!this._oTrailDigiGST) {
				this._oTrailDigiGST = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.Trail", this);
				this.getView().addDependent(this._oTrailDigiGST);
			}
			var oButton = oEvent.getSource();
			sap.ui.core.BusyIndicator.show(0);
			this._bindTailDigiGSTData(docNumber, docDate, imsUniqueId, docKey)
				.finally(function () {
					sap.ui.core.BusyIndicator.hide();
					this._oTrailDigiGST.openBy(oButton);
				}.bind(this));
		},

		onCloseDigiGSTPopover: function () {
			this._oTrailDigiGST.close();
		},

		_bindTailDigiGSTData: function (docNumber, docDate, imsUniqueId, docKey) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"imsUniqueId": imsUniqueId,
						"docKey": docKey
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/imsRecordTrailPopup.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						if (data.resp) {
							data.resp.forEach(function (el, i) {
								el.sno = (i + 1);
							});
						}
						var oData = {
							"resp": data.resp || [],
							"action": [{
								"docNumber": docNumber,
								"docDate": docDate,
								"imsUniqueId": imsUniqueId
							}]
						};
						this._oTrailDigiGST.setModel(new JSONModel(oData), "IMSTrail");
						this._oTrailDigiGST.setContentHeight((data.resp && data.resp.length > 15) ? "40rem" : null);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressTrailGSTN: function (oEvent, docNumber, docDate, imsUniqueId, docKey) {
			if (!this._oTrailGSTN) {
				this._oTrailGSTN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.TrailGSTN", this);
				this.getView().addDependent(this._oTrailGSTN);
			}
			var oButton = oEvent.getSource();
			sap.ui.core.BusyIndicator.show(0);
			this._bindTailGSTNData(docNumber, docDate, imsUniqueId, docKey)
				.finally(function (values) {
					sap.ui.core.BusyIndicator.hide();
					this._oTrailGSTN.openBy(oButton);
				}.bind(this));
		},

		onCloseGSTNPopover: function () {
			this._oTrailGSTN.close();
		},

		_bindTailGSTNData: function (docNumber, docDate, imsUniqueId, docKey) {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"docKey": docKey
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/imsRecordGstnTrailPopup.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						if (data.resp) {
							data.resp.forEach(function (el, i) {
								el.sno = (i + 1);
							});
						}
						var oData = {
							"resp": data.resp || [],
							"action": [{
								"docNumber": docNumber,
								"docDate": docDate,
								"imsUniqueId": imsUniqueId
							}]
						};
						this._oTrailGSTN.setModel(new JSONModel(oData), "IMSTrailGSTN");
						this._oTrailGSTN.setContentHeight((data.resp && data.resp.length > 15) ? "40rem" : null);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onDownloadImsRecords: function () {
			var aGstin = this.getOwnerComponent().getModel("userPermission").getProperty("/respData/dataSecurity/gstin"),
				oSelectedItem = this.byId("tabImsRecords").getSelectedIndices(),
				oFilter = this.getView().getModel("StatusFilter").getProperty("/"),
				oModel = this.getView().getModel("IMSStatus").getProperty("/resp"),
				payload = {
					"req": {
						"type": "IMSRecords",
						"entityId": $.sap.entityID,
						"gstins": [],
						"fromDate": this._formatDate(oFilter.fromDate),
						"toDate": this._formatDate(oFilter.toDate),
						"gstnAction": this.removeAll(oFilter.actionGstn),
						"digiGstAction": this.removeAll(oFilter.actionDigiGST),
						"tableType": this.removeAll(oFilter.tableType),
						"docType": this.removeAll(oFilter.docType),
						"id": [],
						"docNums": [],
						"vendorPan": this.readMultiInputValues("iVenPan"),
						"vendorGstins": this.readMultiInputValues("iVenGstin"),
						"reportType": "ACTIVE"
					}
				};
			if (oSelectedItem.length) {
				oSelectedItem.forEach(function (e) {
					if (!payload.req.gstins.includes(oModel[e].gstinRecipient)) {
						payload.req.gstins.push(oModel[e].gstinRecipient);
					}
					payload.req.id.push(oModel[e].imsUniqueId);
					payload.req.docNums.push(oModel[e].docNumber);
				});
			} else {
				if (oFilter.gstin.length) {
					payload.req.gstins = oFilter.gstin;
				} else {
					aGstin.forEach(function (e) {
						if (e.value.toLowerCase() !== 'all') {
							payload.req.gstins.push(e.value);
						}
					});
				}
				payload.req.id = this.readMultiInputValues("iUniqueID");
				payload.req.docNums = this.readMultiInputValues("iDocNo");
			}
			this.reportDownload(payload, "/aspsapapi/getImsRecordsSummaryReportData.do");
		},

		onTokenUpdate: function (oEvent, type) {
			var oMultiInput = oEvent.getSource();
			var aTokens = oMultiInput.getTokens();

			// If the number of tokens exceeds 30, remove the excess tokens
			if (aTokens.length > 30) {
				// Slice the array to keep only the first 30 tokens
				var aTokensToKeep = aTokens.slice(0, 30);

				// Remove all tokens and then add back only the first 30 tokens
				oMultiInput.removeAllTokens();
				aTokensToKeep.forEach(function (oToken) {
					oMultiInput.addToken(oToken);
				});

				// Optionally, show a message to the user
				MessageBox.error("Maximum 30 " + type + " are allowed.");
				// sap.m.MessageToast.show("Only the first 30 tokens are kept.");
			}
		},

		_nicPagination: function (header) {
			var oTable = this.byId("tabImsRecords");
			oTable.clearSelection();
			var vCountD = this.getView().byId("idPageN")._getSelectedItemText();
			var pageNumber = Math.ceil(header.totalCount / vCountD);
			this.byId("txtPageNoN").setText("/ " + pageNumber);
			this.byId("inPageNoN").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN").setValue(pageNumber);
				this.byId("inPageNoN").setEnabled(false);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			} else if (this.byId("inPageNoN").getValue() === "" || this.byId("inPageNoN").getValue() === "0") {
				this.byId("inPageNoN").setValue(1);
				this.byId("inPageNoN").setEnabled(true);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
			} else {
				this.byId("inPageNoN").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN").setEnabled(true);
				this.byId("btnLastN").setEnabled(true);
			} else {
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			}
			this.byId("btnPrevN").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN").setEnabled(false);
				}
				if (!this.byId("btnNextN").getEnabled()) {
					this.byId("btnNextN").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN")) {
				var vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN").setEnabled(false);
				}
				if (!this.byId("btnLastN").getEnabled()) {
					this.byId("btnLastN").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN")) {
				vValue = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN").setValue(vValue);
			this.getIMSStatusData(vValue);
		},

		onSubmitPagination: function () {
			var vPageNo = this.byId("inPageNoN").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.getIMSStatusData(vPageNo);
		},
		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive" || authToken === "I") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onAction: function (oEvt) {
			debugger;
			var oData = this.byId('tabImsRecords').getModel('IMSStatus').getProperty('/resp'),
				aIndices = this.byId("tabImsRecords").getSelectedIndices(),
				key = oEvt.getParameter("item").getKey();

			if (!aIndices.length) {
				MessageBox.information("Please select record to proceed");
				return;
			}
			if (!this._Action) {
				this._Action = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.IMS.Action", this);
				this.getView().addDependent(this._Action);
			}
			if (oData.length === aIndices.length) {
				this._dialogImsAction(key);
			} else {

				if (aIndices.length == 1 && key == "A") {
					var tableType = ["B2B", "ECOM", "CDN"]
					var visFlag = oData[aIndices[0]].docType == "CR" ? true : !tableType.includes(oData[aIndices[0]].tableType);
					var action = {
						"imsResponseRemarks": oData[aIndices[0]].imsResponseRemarks,
						"itcReductionRequired": oData[aIndices[0]].itcReductionRequired,
						"igstDeclToReduceItc": oData[aIndices[0]].itcReductionRequired == "Y" ? oData[aIndices[0]].igstDeclToReduceItc : 0,
						"cgstDeclToReduceItc": oData[aIndices[0]].itcReductionRequired == "Y" ? oData[aIndices[0]].cgstDeclToReduceItc : 0,
						"sgstDeclToReduceItc": oData[aIndices[0]].itcReductionRequired == "Y" ? oData[aIndices[0]].sgstDeclToReduceItc : 0,
						"cessDeclToReduceItc": oData[aIndices[0]].itcReductionRequired == "Y" ? oData[aIndices[0]].cessDeclToReduceItc : 0,
						"flag": oData[aIndices[0]].itcReductionRequired == "Y" ? true : false
					}

				} else {
					visFlag = false;
					var action = {
						"imsResponseRemarks": ""
					}
				}

				this.getView().setModel(new JSONModel(action), "actionProperty");
				// this.byId("RMid").setValue();
				this._Action.setModel(new JSONModel({
					"action": key,
					"records": "Selected",
					"visFlag": visFlag
				}), "ImsProperty");
				this._Action.open();
			}
		},

		changeITCReduction: function () {
			debugger;
			var oModel = this.getView().getModel("actionProperty"),
				oData = oModel.getData(),
				oTabData = this.byId('tabImsRecords').getModel('IMSStatus').getProperty('/resp'),
				aIndices = this.byId("tabImsRecords").getSelectedIndices();

			oData.flag = oData.itcReductionRequired == "Y" ? true : false;
			oData.igstDeclToReduceItc = oData.itcReductionRequired == "Y" ? oTabData[aIndices[0]].igstDeclToReduceItc : 0;
			oData.cgstDeclToReduceItc = oData.itcReductionRequired == "Y" ? oTabData[aIndices[0]].cgstDeclToReduceItc : 0;
			oData.sgstDeclToReduceItc = oData.itcReductionRequired == "Y" ? oTabData[aIndices[0]].sgstDeclToReduceItc : 0;
			oData.cessDeclToReduceItc = oData.itcReductionRequired == "Y" ? oTabData[aIndices[0]].cessDeclToReduceItc : 0;

			oModel.refresh();
		},

		_dialogImsAction: function (key) {
			var oRadioButtonGroup = new sap.m.RadioButtonGroup({
					id: "rgbImsAction",
					columns: 1,
					selectedIndex: 0,
					buttons: [
						new sap.m.RadioButton({
							text: "Select records appearing on all pages"
						}),
						new sap.m.RadioButton({
							text: "Select records of first page only"
						})
					]
				}).addStyleClass("sapUiTinyMargin"),
				oDialog = new Dialog({
					title: "IMS Action Response",
					content: oRadioButtonGroup,
					buttons: [
						new Button({
							text: "Confirm",
							press: function () {
								var vIdx = oRadioButtonGroup.getSelectedIndex();
								this.byId("RMid").setValue();
								this._Action.setModel(new JSONModel({
									"action": key,
									"records": (!vIdx ? 'All' : 'Selected')
								}), "ImsProperty");
								this._Action.open();
								oDialog.close();
							}.bind(this)
						}),
						new Button({
							text: "Close",
							press: function () {
								oDialog.close();
							}.bind(this)
						})
					],
					afterClose: function () {
						oDialog.destroy();
					}
				});
			oDialog.addStyleClass("sapUiSizeCompact");
			oDialog.open();
		},

		onActionClose: function () {
			this._Action.close();
		},

		onActionSave: function () {
			debugger;
			var vAction = this._Action.getModel("ImsProperty").getProperty('/action');
			var oActionPro = this.getView().getModel("actionProperty").getProperty('/');
			if (oActionPro.flag && vAction == "A") {
				if (oActionPro.igstDeclToReduceItc === "" || oActionPro.igstDeclToReduceItc === null) {
					MessageBox.error("Enter IGST Reduced amount");
					return;
				}
				if (oActionPro.cgstDeclToReduceItc === "" || oActionPro.cgstDeclToReduceItc === null) {
					MessageBox.error("Enter CGST Reduced amount");
					return;
				}
				if (oActionPro.sgstDeclToReduceItc === "" || oActionPro.sgstDeclToReduceItc === null) {
					MessageBox.error("Enter SGST Reduced amount");
					return;
				}
				if (oActionPro.cessDeclToReduceItc === "" || oActionPro.cessDeclToReduceItc === null) {
					MessageBox.error("Enter CESS Reduced amount");
					return;
				}
				if (oActionPro.cgstDeclToReduceItc != oActionPro.sgstDeclToReduceItc) {
					MessageBox.error("CGST & SGST amount declared for ITC reduction are not matched");
					return;
				}
			}
			sap.m.MessageBox.confirm("Are you sure you want to proceed with Action '" + this._getActionText(vAction) + "'?", {
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				initialFocus: sap.m.MessageBox.Action.NO, // Set focus on NO by default
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.YES) {
						this.onActionSaveFinal(); // Call the function if Yes is selected
					}
				}.bind(this)
			});
		},

		_getActionText: function (action) {
			switch (action) {
			case 'N':
				return 'No Action';
			case 'A':
				return 'Accept';
			case 'R':
				return 'Reject';
			case 'P':
				return 'Pending';
			}
		},

		onActionSaveFinal: function () {
			debugger;
			var oAction = this._Action.getModel("ImsProperty").getProperty('/'),
				aTabData = this.getView().getModel("IMSStatus").getProperty('/resp'),
				aIndices = this.byId("tabImsRecords").getSelectedIndices(),
				oActionPro = this.getView().getModel("actionProperty").getProperty('/'),
				imsUniqueId = [];

			if (oAction.records === "All") {
				var oFilter = this.getView().getModel("StatusFilter").getProperty('/'),
					payload = {
						"req": {
							"entityId": $.sap.entityID,
							"imsUniqueId": [],
							"actionTaken": oAction.action,
							"records": oAction.records,
							"gstins": this.removeAll(oFilter.gstin),
							"tableTypes": this.removeAll(oFilter.tableType),
							"docTypes": this.removeAll(oFilter.docType),
							"fromDocDate": this._formatDate(oFilter.fromDate),
							"toDocDate": this._formatDate(oFilter.toDate),
							"actionGstns": this.removeAll(oFilter.actionGstn),
							"actionDigiGsts": this.removeAll(oFilter.actionDigiGST),
							"vendorPans": this.readMultiInputValues("iVenPan"),
							"vendorGstins": this.readMultiInputValues("iVenGstin"),
							"docNumbers": this.readMultiInputValues("iDocNo"),
							"imsUniqueIds": this.readMultiInputValues("iUniqueID"),
							// "responseRemark": this.byId("RMid").getValue()
							"responseRemark": oActionPro.imsResponseRemarks,
							"itcRedRequired": oActionPro.itcReductionRequired,
							"declIgst": oActionPro.igstDeclToReduceItc,
							"declCgst": oActionPro.cgstDeclToReduceItc,
							"declSgst": oActionPro.sgstDeclToReduceItc,
							"declCess": oActionPro.cessDeclToReduceItc
						}
					};
				if (!oFilter.gstin.length) {
					var aGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin");
					payload.req.gstins = aGstin.map(function (el) {
						return el.value;
					});
				}
			} else {
				imsUniqueId = aIndices.map(function (el) {
					return aTabData[el].imsUniqueId;
				});
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"imsUniqueId": imsUniqueId,
						"actionTaken": oAction.action,
						// "responseRemark": this.byId("RMid").getValue()
						"responseRemark": oActionPro.imsResponseRemarks,
						"itcRedRequired": oActionPro.itcReductionRequired,
						"declIgst": oActionPro.igstDeclToReduceItc,
						"declCgst": oActionPro.cgstDeclToReduceItc,
						"declSgst": oActionPro.sgstDeclToReduceItc,
						"declCess": oActionPro.cessDeclToReduceItc
					}
				};
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/imsActionResponse.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.status === "Information") {
							MessageBox.information(data.resp.message);
						} else {
							MessageBox.success(data.resp);
						}
						this._Action.close();
						var vValue = this.byId("inPageNoN").getValue();
						setTimeout(this.getIMSStatusData(vValue), 1000);
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onRequestStatus: function () {
			var oModel = this.getView().getModel("ViewProperty");
			oModel.setProperty("/title", "Request Status");
			oModel.setProperty("/imsRecords", "Request Status");
			oModel.setProperty("/navBack", true);
			oModel.refresh(true);
			this._bindRequestStatus();
		},

		_bindRequestStatus: function () {
			var request = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/imsRequestStatusData.do",
				contentType: "application/json",
				data: JSON.stringify(request)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					this.getView().setModel(new JSONModel(data), "RequestStatus");
				} else {
					this.getView().setModel(new JSONModel([]), "RequestStatus");
				}

			}.bind(this)).fail(function (jqXHR, status, err) {
				this.getView().setModel(new JSONModel([]), "RequestStatus");
				sap.ui.core.BusyIndicator.hide();
			}.bind(this));
		},
		onDownloadRequestStatus: function (docId, reqId) {
			var oIntiData = {
				"req": {
					"docId": docId,
					"reqId": reqId
				}
			};
			var path = "/aspsapapi/imsRequestStatusErrorDownload.do";
			// this.reportDownload(oIntiData, path)
			this.excelDownload(oIntiData, path);
		},

		// IMS Status End
	});
});