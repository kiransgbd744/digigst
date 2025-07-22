var searchData = {};
var asyncFlag = false;
var recvDate = null;
var oFileId = null;
var oIrnId = null;
var vStatusType = null,
	vRefId = null,
	vShowGstnData = false;

sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/configOutward",
	"com/ey/digigst/util/invManageNew/invManage",
	"com/ey/digigst/util/invManageNew/invManageEWB",
	"sap/m/MessagePopover",
	"sap/m/MessageItem",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage",
	"sap/m/MessageBox",
	"sap/m/Button",
	"sap/m/Dialog",
	"sap/m/Token"
], function (BaseController, configOutward, invManage, invManageEWB, MessagePopover, MessageItem, JSONModel, Storage, MessageBox, Button,
	Dialog, Token) {
	"use strict";

	/**
	 * Message Popover Template
	 * @private
	 */
	var oMessageTemplate = new MessageItem({
		type: "{Msg>type}",
		title: "{Msg>title}",
		activeTitle: "{Msg>active}",
		description: "{Msg>description}",
		subtitle: "{Msg>subtitle}"
	});

	/**
	 * Message Popover object to display multiple message
	 * @private
	 */
	var oMessagePopover = new MessagePopover({
		items: {
			path: "Msg>/",
			template: oMessageTemplate
		},
		activeTitlePress: function (oEvent) {
			// 			var oItem = oEvent.getParameter("item"),
			// 				oMessage = oItem.getBindingContext("Msg").getObject();
		}
	});

	oMessagePopover._oPopover.setContentWidth("900px");

	var oList = new sap.m.List({
		items: {
			path: "Msg>/",
			template: new MessageItem({
				type: "{Msg>type}",
				title: "{Msg>title}",
				activeTitle: "{Msg>active}",
				description: "{Msg>description}",
				subtitle: "{Msg>subtitle}"
			})
		}
	});

	return BaseController.extend("com.ey.digigst.controller.InvManageNew", {
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.InvManageNew
		 */
		onInit: function () {
			this.getOwnerComponent().getModel("invManage").setSizeLimit(500);
			this.getOwnerComponent().getRouter().getRoute("InvManageNew").attachPatternMatched(this._onRouteMatched, this);
			this.flagSubmit = true;
			this.inward240Flag = true;
			var oMultiInput1 = this.byId("iDocNo");
			var fnValidator = function (args) {
				var text = args.text;
				return new Token({
					key: text,
					text: text
				});
			};
			oMultiInput1.addValidator(fnValidator);

			var oMultiInputIW = this.byId("idIWDocNo");
			var fnValidatorIW = function (args) {
				var text = args.text;
				return new Token({
					key: text,
					text: text
				});
			};
			oMultiInputIW.addValidator(fnValidatorIW);

			var oMultiInputIW1 = this.byId("idAVNNo");
			var fnValidatorIW1 = function (args) {
				var text = args.text;
				return new Token({
					key: text,
					text: text
				});
			};
			oMultiInputIW1.addValidator(fnValidatorIW1);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.InvManageNew
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.getUnitOfManagement();
			}
		},

		getUnitOfManagement: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getUnitOfManagement.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data), "UQC");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("changeMultiVehicle.do : Error");
				}.bind(this));
		},

		_setDateValue: function () {
			var vDate = new Date(),
				today = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);

			vDate.setDate(vDate.getDate() - 9);

			this._setDateProperty("iFromDate", vDate, today);
			this._setDateProperty("iToDate", today, today, vDate);

			this._setDateProperty("iFromPeriod", vPeriod, today);
			this._setDateProperty("iToPeriod", vDate, today, vPeriod);

			this._setDateProperty("iIWFromDate", vDate, today);
			this._setDateProperty("iIWToDate", today, today, vDate);

			this._setDateProperty("iIWFromPeriod", vPeriod, today);
			this._setDateProperty("iIWToPeriod", today, today, vPeriod);

			var oModel = this.getOwnerComponent().getModel("filter"),
				oData = oModel.getProperty("/req");
			oData.returnFromDate = this.byId("iFromPeriod").getValue();
			oData.returnToDate = this.byId("iToPeriod").getValue();
			oModel.refresh();
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger(),
				oName = oEvent.getParameter("name");

			oHashChanger.setHash("InvManageNew");
			recvDate = null;
			if (oName === "InvManageNew") {
				this._filterModel();
				this._setDateValue();
				this.getAllGSTIN();
				this.getAllStateCode();

				var oModel = this.getOwnerComponent().getModel("FilterInvoice");
				if (oModel) {
					var oData = oModel.getProperty("/req");
					switch (oData.navType) {
					case "DS":
						if (oData.criteria === "recDate") {
							this.byId("slCriteria").setSelectedKey("recDate");
							this.byId("iFromDate").setValue(oData.dataRecvFrom);
							var oMinDate = this.byId("iFromDate").getDateValue();
							this.byId("iToDate").setMinDate(oMinDate);
							this.byId("iToDate").setValue(oData.dataRecvTo);
						} else {
							this.byId("slCriteria").setSelectedKey("taxPeriod");
							this.byId("iFromPeriod").setValue(oData.taxPeriodFrom);
							var oMinDate = this.byId("iFromPeriod").getDateValue();
							this.byId("iToPeriod").setMinDate(oMinDate);
							this.byId("iToPeriod").setValue(oData.taxPeriodTo);
							if (oData.dateFlag === true) {
								recvDate = oData.dataRecvFrom;
							}
						}
						this.onSelectCriteria("");
						this.byId("idfgiInvMGSINT").setSelectedKeys(oData.dataSecAttrs.GSTIN);
						vStatusType = oData.type;
						this.byId("sbNewManageOI").setSelectedKey("Outward");
						break;
					case "File":
						this.byId("slCriteria").setSelectedKey("recDate");
						this.byId("iFromDate").setValue(oData.dataRecvFrom);
						var oMinDate = this.byId("iFromDate").getDateValue();
						this.byId("iToDate").setMinDate(oMinDate);
						this.byId("iToDate").setValue(oData.dataRecvTo);
						vStatusType = oData.type;
						oFileId = oData.fileId;
						this.byId("sbNewManageOI").setSelectedKey("Outward");
						break;
					case "GSTR1":
						this.byId("sbNewManageOI").setSelectedKey("Outward");

						this.byId("slCriteria").setSelectedKey("taxPeriod");
						this.onSelectCriteria("");
						this.byId("iFromPeriod").setValue(oData.taxPeriodFrom);
						var oMinDate = this.byId("iFromPeriod").getDateValue();
						this.byId("iToPeriod").setMinDate(oMinDate);
						this.byId("iToPeriod").setValue(oData.taxPeriodTo);
						if (oData.gstReturnsStatus != undefined) {
							this.byId("slGSTReturnsStatus").setSelectedKeys(oData.gstReturnsStatus);
						}

						this.byId("idfgiInvMGSINT").setSelectedKeys(oData.dataSecAttrs.GSTIN);

						vStatusType = oData.type;
						vRefId = oData.refId;
						vShowGstnData = oData.showGstnData;
						break;
					case "GSTR6":
						this.byId("sbNewManageOI").setSelectedKey("Inward");

						this.byId("slIWCriteria").setSelectedKey("taxPeriod");
						this.onSelectCriteriaIW("");
						this.byId("iIWFromPeriod").setValue(oData.taxPeriodFrom);
						var oMinDate = this.byId("iIWFromPeriod").getDateValue();
						this.byId("iIWToPeriod").setMinDate(oMinDate);
						this.byId("iIWToPeriod").setValue(oData.taxPeriodTo);

						this.byId("idfgiIWInvMGSINT").setSelectedKeys(oData.dataSecAttrs.GSTIN);
						vStatusType = oData.type; // processing staus

						this.byId("idIWGSTReturn").setSelectedKey("GSTR6");
						this.onChangeGSTReturn("");
						break;
					case "GSTR2":
						this.byId("sbNewManageOI").setSelectedKey("Inward");

						if (oData.criteria === "taxPeriod") {
							this.byId("slIWCriteria").setSelectedKey("taxPeriod");
							this.onSelectCriteriaIW("");
							this.byId("iIWFromPeriod").setValue(oData.taxPeriodFrom);
							var oMinDate = this.byId("iIWFromPeriod").getDateValue();
							this.byId("iIWToPeriod").setMinDate(oMinDate);
							this.byId("iIWToPeriod").setValue(oData.taxPeriodTo);
						} else {
							this.byId("slIWCriteria").setSelectedKey("docDate");
							this.onSelectCriteriaIW("");
							this.byId("iIWFromDate").setValue(oData.docDateFrom);
							var oMinDate = this.byId("iIWFromDate").getDateValue();
							this.byId("iIWToDate").setMinDate(oMinDate);
							this.byId("iIWToDate").setValue(oData.docDateTo);
						}
						this.byId("idfgiIWInvMGSINT").setSelectedKeys(oData.dataSecAttrs.GSTIN);
						vStatusType = "E";

						this.byId("idIWGSTReturn").setSelectedKey("GSTR2");
						this.onChangeGSTReturn("");
						break;
					}
				}
				this.onChangeSegment();
				// this.onPressGoFinal(1);
				this.getDisplay();
			}
		},

		/**
		 * Called to get All Unit of Measurement
		 * Developed by: Bharat Gupta - 19.11.2020
		 * @memberOf com.ey.digigst.view.InvManageNew
		 * @private
		 */
		_getAllUoM: function () {
			$.post("/aspsapapi/getUom.do")
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "UomMaster");
				}.bind(this));
		},

		getAllGSTIN: function () {
			var aRegGSTIN = this.getOwnerComponent().getModel("userPermission").getProperty("/respData/dataSecurity/gstin");
			this.getOwnerComponent().setModel(new JSONModel(aRegGSTIN), "InwardGSTIN");
		},

		getAllStateCode: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAllState.do",
					contentType: "application/json",
					data: JSON.stringify({})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getOwnerComponent().setModel(new JSONModel(data), "stateCode");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onInwardDocNumber: function (oEvent) {
			var docNumber = oEvent.getSource().getValue();
			var regSpecialCharcter = new RegExp(/^[ A-Za-z0-9-/]*$/);
			var regSpace = new RegExp(/^\S*$/);
			if (!regSpecialCharcter.test(docNumber)) {
				oEvent.getSource().setValueState("Error");
				oEvent.getSource().setValueStateText("Document Number" + "\n" + docNumber + "\n" + "Contains special character");

			}
			// else if (docNumber.substring(0, 1) === "0") {
			// 	oEvent.getSource().setValueState("Error");
			// 	oEvent.getSource().setValueStateText("Document Number" + docNumber + "Should not start with zero");
			// } 
			else if (!regSpace.test(docNumber)) {
				oEvent.getSource().setValueState("Error");
				oEvent.getSource().setValueStateText("Document Number" + docNumber + "Should not allow Spaces");
			} else {
				oEvent.getSource().setValueState("None");
			}

		},

		_filterModel: function () {
			var oFilterData = this._getFilterPayload();
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "filter");
		},

		/**
		 * Validate Date Range Selection for Date / Period
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeDateValue: function (oEvent) {
			var vDatePicker;
			if (oEvent.getSource().getId().includes("iFromDate")) {
				vDatePicker = "iToDate";
			} else if (oEvent.getSource().getId().includes("iFromPeriod")) {
				vDatePicker = "iToPeriod";
			} else if (oEvent.getSource().getId().includes("iIWFromDate")) {
				vDatePicker = "iIWToDate";
			} else if (oEvent.getSource().getId().includes("iIWFromPeriod")) {
				vDatePicker = "iIWToPeriod";
			} else if (oEvent.getSource().getId().includes("iUploadFromDate")) {
				vDatePicker = "iUploadToDate";
			} else if (oEvent.getSource().getId().includes("iFtFromDate")) {
				vDatePicker = "iFtToDate";
			} else if (oEvent.getSource().getId().includes("iFtFromPeriod")) {
				vDatePicker = "iFtToPeriod";
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

		onEntityChange: function (oEvent) {
			this._getDataSecurity(oEvent.getSource().getSelectedKey());
			$.sap.entityID = oEvent.getSource().getSelectedKey();
		},

		onPressGo: function () {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.onPressGoFinal(1);
			} else {
				this.onPressInwardGoFinal(1);
			}
		},

		onPressCloudDownload: function () {
			var vSelectedKey = this.getView().byId("slCriteria").getSelectedKey();
			var vReceivFromDate, vReceivToDate, vDocFromDate, vDocToDate, vReturnFromDate, vReturnToDate, vDocNo, vDateEACKFrom, vDateEACKTo,
				vStatus, vEwbDate, vPostingDate, vEWBNo, vTransporterID;

			vStatus = vStatusType;
			if (vSelectedKey === "taxPeriod") {
				if (recvDate === null) {
					vReceivFromDate = null;
					vReceivToDate = null;
				} else {
					vReceivFromDate = recvDate;
					vReceivToDate = recvDate;
				}
				vDocFromDate = null;
				vDocToDate = null;
				vReturnFromDate = this.getView().byId("iFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iToPeriod").getValue();
			} else if (vSelectedKey === "docDate") {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocFromDate = this.getView().byId("iFromDate").getValue();
				vDocToDate = this.getView().byId("iToDate").getValue();
			} else {
				vReceivFromDate = this.getView().byId("iFromDate").getValue();
				vReceivToDate = this.getView().byId("iToDate").getValue();
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocFromDate = null;
				vDocToDate = null;
			}

			if (this.getView().byId("iDocNo").getValue() === "") {
				vDocNo = null;
			} else {
				vDocNo = this.getView().byId("iDocNo").getValue();
			}

			if (this.getView().byId("idfileId").getValue() === "") {
				oFileId = null;
			} else {
				oFileId = this.getView().byId("idfileId").getValue();
			}

			var searchData = {
				"req": {
					"entityId": [
						"36"
					],
					"receivFromDate": "2020-09-29",
					"receivToDate": "2020-09-30",
					"returnFromDate": null,
					"returnToDate": null,
					"docFromDate": null,
					"docToDate": null,
					"docType": [],
					"docNo": null,
					"transType": "O",
					"fileId": null,
					"einvStatus": ["5"],
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docFromDate = vDocFromDate;
			searchData.req.docToDate = vDocToDate;

			searchData.req.docNo = vDocNo;
			if (oFileId != null) {
				searchData.req.fileId = oFileId;
			}

			searchData.req.transType = this.getView().byId("slTransactionType").getSelectedKey();
			searchData.req.docType = this.getView().byId("slDocType").getSelectedKeys();

			var aGstin = this.getView().byId("idfgiInvMGSINT").getSelectedKeys();
			searchData.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;

			if (this.getView().byId("ddOutAdaptAdapt")) {
				this._getOtherOutFiltersASP(searchData.req.dataSecAttrs);
			}

			var url = "/aspsapapi/downloadEinvEwbReports.do";
			this.excelDownload(searchData, url);
		},

		onPressScreenOutwardDownload: function (flag) {
			var vSelectedKey = this.getView().byId("slCriteria").getSelectedKey(),
				aReverseCharge = this.getView().byId("rcFlag").getSelectedKeys(),
				aIndex = this.byId("idtableInvNew").getSelectedIndices(),
				oFileId = this.getView().byId("idfileId").getValue() || null,
				vReturnFromDate = null,
				vReturnToDate = null,
				vReceivFromDate = null,
				vReceivToDate = null,
				vDocFromDate = null,
				vDocToDate = null;

			if (!aReverseCharge.length) {
				MessageBox.error("Please select atleast one Reverse Charge Flag.");
				return;
			}
			if (aIndex.length) {
				var oData = this.getView().getModel("invOutward").getProperty("/resp"),
					vDocNoTok = aIndex.map(function (idx) {
						return oData[idx].docNo;
					});
			} else {
				var aTokens = this.getView().byId("iDocNo").getTokens(),
					vDocNoTok = aTokens.map(function (oToken) {
						return oToken.getText();
					});
			}
			if (vSelectedKey === "taxPeriod") {
				vReceivFromDate = recvDate || null;
				vReceivToDate = recvDate || null;
				vReturnFromDate = this.getView().byId("iFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iToPeriod").getValue();

			} else if (vSelectedKey === "docDate") {
				vDocFromDate = this.getView().byId("iFromDate").getValue();
				vDocToDate = this.getView().byId("iToDate").getValue();
			} else {
				vReceivFromDate = this.getView().byId("iFromDate").getValue();
				vReceivToDate = this.getView().byId("iToDate").getValue();
			}

			searchData = this._getPayloadStruct();
			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docFromDate = vDocFromDate;
			searchData.req.docToDate = vDocToDate;
			searchData.req.docNo = (this.getView().byId("iDocNo").getValue() || null);
			searchData.req.showGstnData = false;
			searchData.req.docNums = vDocNoTok;
			searchData.req.irnNum = (this.getView().byId("idirnId").getValue() || null);

			searchData.req.type = this.getView().byId("idType").getSelectedKey();
			searchData.req.counterPartyGstin = this.getView().byId("slCpAction").getSelectedKey();
			searchData.req.transType = this.getView().byId("slTransactionType").getSelectedKey();
			searchData.req.docType = this.getView().byId("slDocType").getSelectedKeys();
			searchData.req.gstReturnsStatus = this.getView().byId("slGSTReturnsStatus").getSelectedKeys();
			searchData.req.ewbStatus = this.getView().byId("slEWBStatus").getSelectedKeys();
			searchData.req.einvStatus = this.getView().byId("slEINVStatus").getSelectedKeys();
			searchData.req.ewbErrorPoint = this.getView().byId("slEWBErrorPoint").getSelectedKeys();
			searchData.req.ewbNo = (this.getView().byId("idEWBNo").getValue() || null);
			searchData.req.ewbValidUpto = (this.getView().byId("idEWBDate").getValue() || null);
			searchData.req.ewbCancellation = this.getView().byId("slEWBCancellation").getSelectedKeys();
			searchData.req.subSupplyType = this.getView().byId("slSubSupplyType").getSelectedKeys();
			searchData.req.supplyType = this.removeAll(this.getView().byId("slSupplyType").getSelectedKeys());
			searchData.req.transporterID = (this.getView().byId("idTransporterID").getValue() || null);
			searchData.req.postingDate = (this.getView().byId("idPostingDate").getValue() || null);
			searchData.req.processingStatus = vStatusType || null;
			searchData.req.reverseCharge = this.removeAll(aReverseCharge || []);

			var aGstin = this.getView().byId("idfgiInvMGSINT").getSelectedKeys();
			searchData.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;

			if (flag) {
				searchData.req.flag = flag;
			} else {
				delete searchData.req.flag;
			}

			if (oFileId != null) {
				searchData.req.fileId = oFileId;
			}
			if (this.getView().byId("dOutAdapt")) {
				this._getOtherOutFiltersASP(searchData.req.dataSecAttrs);
			}

			delete searchData.hdr;
			searchData.req.dataType = "outward";

			if (searchData.req.type === "GSTR1") {
				var url = "/aspsapapi/downloadInvoiceManagementCsvReports.do";
			} else {
				url = "/aspsapapi/downloadGstr1AInvoiceManagementCsvReports.do";
			}
			this.reportDownload(searchData, url);

		},

		onPressScreenInwardDownload: function (vPageNo) {
			var vSelectedKey = this.getView().byId("slIWCriteria").getSelectedKey(),
				aGstin = this.getView().byId("idfgiIWInvMGSINT").getSelectedKeys(),
				aIndex = this.byId("idIWtableInvNew").getSelectedIndices(),
				vReturnFromDate = null,
				vReturnToDate = null,
				vReceivFromDate = null,
				vReceivToDate = null,
				vDocFromDate = null,
				vDocToDate = null;

			if (vSelectedKey === "taxPeriod") {
				vReceivFromDate = recvDate || null;
				vReceivToDate = recvDate || null;
				vReturnFromDate = this.getView().byId("iIWFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iIWToPeriod").getValue();

			} else if (vSelectedKey === "docDate") {
				vDocFromDate = this.getView().byId("iIWFromDate").getValue();
				vDocToDate = this.getView().byId("iIWToDate").getValue();
			} else {
				vReceivFromDate = this.getView().byId("iIWFromDate").getValue();
				vReceivToDate = this.getView().byId("iIWToDate").getValue();
			}

			if (aIndex.length) {
				var oData = this.getView().getModel("invInward").getProperty("/resp"),
					vDocNoTok = [],
					accVcNo = [];

				aIndex.forEach(function (idx) {
					vDocNoTok.push(oData[idx].docNo);
					if (oData[idx].accVoucherNo) {
						accVcNo.push(oData[idx].accVoucherNo);
					}
				});
			} else {
				var aTokens = this.getView().byId("idIWDocNo").getTokens(),
					aAccVocToken = this.getView().byId("idAVNNo").getTokens();
				vDocNoTok = aTokens.map(function (oToken) {
					return oToken.getText();
				});
				accVcNo = aAccVocToken.map(function (oToken) {
					return oToken.getText();
				});
			}

			searchData = this._getPayloadInwardStruct();
			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docFromDate = vDocFromDate;
			searchData.req.docToDate = vDocToDate;

			searchData.req.docNo = (this.getView().byId("idIWDocNo").getValue() || null);
			searchData.req.docType = this.getView().byId("slIWDocType").getSelectedKeys();
			searchData.req.showGstnData = false;
			searchData.req.gstReturn = this.getView().byId("idIWGSTReturn").getSelectedKey();
			searchData.req.counterPartyGstin = this.getView().byId("slIWCpAction").getSelectedKey();
			searchData.req.gstReturnsStatus = this.getView().byId("slIWGSTReturnsStatus").getSelectedKeys();
			searchData.req.supplyType = this.getView().byId("slIWSupplyType").getSelectedKeys();
			searchData.req.docNums = vDocNoTok;
			searchData.req.suppGstin = this.getView().byId("idSGNo").getValue();
			searchData.req.accVoucherNum = accVcNo;
			searchData.req.postingDate = (this.getView().byId("idPostingDate").getValue() || null);
			searchData.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;
			searchData.req.processingStatus = vStatusType || null;

			if (oFileId != null) {
				searchData.req.fileId = oFileId;
			}

			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(searchData.req.dataSecAttrs);
			}

			delete searchData.hdr;
			searchData.req.dataType = "inward";
			this.reportDownload(searchData, "/aspsapapi/downloadInwardInvoiceManagementCsvReports.do");
		},

		onPressGoFinal: function (vPageNo) {
			var vSelectedKey = this.getView().byId("slCriteria").getSelectedKey(),
				aReverseCharge = this.getView().byId("rcFlag").getSelectedKeys(),
				vReceivFromDate = null,
				vReceivToDate = null,
				vDocFromDate = null,
				vDocToDate = null,
				vReturnFromDate = null,
				vReturnToDate = null,
				vDateEACKFrom, vDateEACKTo, vStatus;

			if (!aReverseCharge.length) {
				MessageBox.error("Please select atleast one Reverse Charge Flag.");
				return;
			}
			// vStatus = this.getView().byId("idStatus").getSelectedKey();
			vStatus = vStatusType;
			switch (vSelectedKey) {
			case "taxPeriod":
				if (recvDate) {
					vReceivFromDate = recvDate;
					vReceivToDate = recvDate;
				}
				vReturnFromDate = this.getView().byId("iFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iToPeriod").getValue();
				break;
			case "docDate":
				vDocFromDate = this.getView().byId("iFromDate").getValue();
				vDocToDate = this.getView().byId("iToDate").getValue();
				break;
			default:
				vReceivFromDate = this.getView().byId("iFromDate").getValue();
				vReceivToDate = this.getView().byId("iToDate").getValue();
			}

			var aGstin = this.getView().byId("idfgiInvMGSINT").getSelectedKeys(),
				oFileId = (this.getView().byId("idfileId").getValue() || null),
				oIrnId = (this.getView().byId("idirnId").getValue() || null),
				aTokens = this.getView().byId("iDocNo").getTokens(),
				vDocNoTok = aTokens.map(function (oToken) {
					return oToken.getText();
				});

			searchData = this._getPayloadStruct();
			searchData.hdr.pageNum = +vPageNo - 1;
			searchData.hdr.pageSize = 100;

			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docFromDate = vDocFromDate;
			searchData.req.docToDate = vDocToDate;

			searchData.req.docNo = (this.getView().byId("iDocNo").getValue() || null);
			searchData.req.showGstnData = false;
			searchData.req.docNums = vDocNoTok;

			searchData.req.type = this.getView().byId("idType").getSelectedKey();
			searchData.req.counterPartyGstin = this.getView().byId("slCpAction").getSelectedKey();
			searchData.req.transType = this.getView().byId("slTransactionType").getSelectedKey();
			searchData.req.docType = this.getView().byId("slDocType").getSelectedKeys();
			searchData.req.gstReturnsStatus = this.getView().byId("slGSTReturnsStatus").getSelectedKeys();
			searchData.req.ewbStatus = this.getView().byId("slEWBStatus").getSelectedKeys();
			searchData.req.einvStatus = this.getView().byId("slEINVStatus").getSelectedKeys();
			searchData.req.ewbErrorPoint = this.getView().byId("slEWBErrorPoint").getSelectedKeys();
			searchData.req.ewbNo = (this.getView().byId("idEWBNo").getValue() || null);
			searchData.req.ewbValidUpto = (this.getView().byId("idEWBDate").getValue() || null);
			searchData.req.ewbCancellation = this.getView().byId("slEWBCancellation").getSelectedKeys();
			searchData.req.subSupplyType = this.getView().byId("slSubSupplyType").getSelectedKeys();
			searchData.req.supplyType = this.removeAll(this.getView().byId("slSupplyType").getSelectedKeys());
			searchData.req.transporterID = (this.getView().byId("idTransporterID").getValue() || null);
			searchData.req.postingDate = (this.getView().byId("idPostingDate").getValue() || null);
			searchData.req.reverseCharge = this.removeAll(aReverseCharge || []);
			searchData.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;
			searchData.req.refId = vRefId;
			searchData.req.showGstnData = vShowGstnData;
			searchData.req.processingStatus = vStatus || null;

			if (oFileId != null) {
				searchData.req.fileId = oFileId;
			}
			if (oIrnId !== null) {
				searchData.req.irnNum = oIrnId;
			}
			if (this.getView().byId("ddOutAdaptAdapt")) {
				this._getOtherOutFiltersASP(searchData.req.dataSecAttrs);
			}
			this.geteInvoiceDocSearch(searchData);
		},

		geteInvoiceDocSearch: function (searchInfo) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/eInvoiceDocSearch.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					vRefId = recvDate = oFileId = null;
					vShowGstnData = false;
					this.bindEInvoiceDocSearch(data);
					this.displayRetunType();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("eInvoiceDocSearch : Error");
				}.bind(this));
		},
		displayRetunType: function () {
			var retType = this.getView().byId("idType").getSelectedKey();
			var odata = {
				"retTypeFlag": true
			};
			if (retType == "GSTR1") {
				odata.retTypeFlag = true;
			} else {
				odata.retTypeFlag = false;
			}
			this.getView().setModel(new JSONModel(odata), "retType");
		},

		bindEInvoiceDocSearch: function (data) {
			if (data.hdr.status === "S" && data.resp.length > 0) {
				var vRecord = (data.hdr.pageNum * data.hdr.pageSize + 1) + "-" + (data.hdr.pageNum * data.hdr.pageSize + data.resp.length),
					pageNumber = Math.ceil(data.hdr.totalCount / 100);
				this.byId("txtRecords").setText("Records: " + vRecord + " / " + data.hdr.totalCount);

				this.byId("txtPageNoErr").setText("/ " + pageNumber);
				this.byId("inPageNoErr").setValue(data.hdr.pageNum + 1);
				if (data.resp.length < 100) {
					this.byId("bNextErr").setEnabled(false);
				} else {
					this.byId("bNextErr").setEnabled(true);
				}
				this.getView().setModel(new JSONModel(data), "invOutward");
			} else {
				this.byId("txtRecords").setText("Records: 0-0 / 0");
				this.byId("inPageNoErr").setValue((data.hdr.pageNum || 0) + 1);
				this.byId("txtPageNoErr").setText("/ 0");

				this.byId("bPrevErr").setEnabled(false);
				this.byId("bNextErr").setEnabled(false);
				this.getView().setModel(new JSONModel({
					"resp": []
				}), "invOutward");
				if (data.hdr.status === "E") {
					MessageBox.error(data.hdr.message);
					this.getView().setModel(new JSONModel([]), "invOutward");
				} else {
					MessageBox.error("No Data");
				}
			}
		},

		onPressInwardGoFinal: function (vPageNo) {
			//eslint-disable-line
			var vSelectedKey = this.getView().byId("slIWCriteria").getSelectedKey();
			var vReceivFromDate, vReceivToDate, vDocFromDate, vDocToDate, vReturnFromDate, vReturnToDate, vDocNo, vDateEACKFrom, vDateEACKTo,
				vStatus, vEwbDate, vPostingDate, vEWBNo, vTransporterID, vDocNoTok, vDocTokens, accVcNo, vDocTokens1;

			// vStatus = this.getView().byId("idStatus").getSelectedKey();
			vStatus = vStatusType;
			if (vSelectedKey === "taxPeriod") {
				if (recvDate === null) {
					vReceivFromDate = null;
					vReceivToDate = null;
				} else {
					vReceivFromDate = recvDate;
					vReceivToDate = recvDate;
				}
				vDocFromDate = null;
				vDocToDate = null;
				vReturnFromDate = this.getView().byId("iIWFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iIWToPeriod").getValue();
			} else if (vSelectedKey === "docDate") {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocFromDate = this.getView().byId("iIWFromDate").getValue();
				vDocToDate = this.getView().byId("iIWToDate").getValue();
			} else {
				vReceivFromDate = this.getView().byId("iIWFromDate").getValue();
				vReceivToDate = this.getView().byId("iIWToDate").getValue();
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocFromDate = null;
				vDocToDate = null;
			}

			if (this.getView().byId("idIWDocNo").getValue() === "") {
				vDocNo = null;
			} else {
				vDocNo = this.getView().byId("idIWDocNo").getValue();
			}

			var aTokens = this.getView().byId("idIWDocNo").getTokens();
			if (aTokens.length === 0) {
				vDocNoTok = [];
			} else {
				vDocTokens = aTokens.map(function (oToken) {
					return oToken.getText();
				});
				vDocNoTok = vDocTokens;
			}

			var aTokens1 = this.getView().byId("idAVNNo").getTokens();
			if (aTokens1.length === 0) {
				accVcNo = [];
			} else {
				vDocTokens1 = aTokens1.map(function (oToken) {
					return oToken.getText();
				});
				accVcNo = vDocTokens1;
			}

			if (this.getView().byId("idIWPostingDate").getValue() === "") {
				vPostingDate = null;
			} else {
				vPostingDate = this.getView().byId("idPostingDate").getValue();
			}

			// vPageNo = this.getView().byId("inIWPageNoErr").getValue();

			searchData = this._getPayloadInwardStruct();
			searchData.hdr.pageNum = Number(vPageNo) - 1;
			searchData.hdr.pageSize = 100;
			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docFromDate = vDocFromDate;
			searchData.req.docToDate = vDocToDate;
			searchData.req.docNo = vDocNo;
			searchData.req.docType = this.getView().byId("slIWDocType").getSelectedKeys();
			searchData.req.showGstnData = false;
			searchData.req.gstReturn = this.getView().byId("idIWGSTReturn").getSelectedKey();
			searchData.req.counterPartyGstin = this.getView().byId("slIWCpAction").getSelectedKey();
			searchData.req.gstReturnsStatus = this.getView().byId("slIWGSTReturnsStatus").getSelectedKeys();
			searchData.req.supplyType = this.getView().byId("slIWSupplyType").getSelectedKeys();
			searchData.req.postingDate = vPostingDate;
			searchData.req.docNums = vDocNoTok;
			searchData.req.suppGstin = this.getView().byId("idSGNo").getValue();
			searchData.req.accVoucherNum = accVcNo; //this.getView().byId("idAVNNo").getValue();

			var aGstin = this.getView().byId("idfgiIWInvMGSINT").getSelectedKeys();
			searchData.req.dataSecAttrs.GSTIN = aGstin.includes("All") ? [] : aGstin;

			if (vStatus === null) {
				searchData.req.processingStatus = null;
			} else {
				searchData.req.processingStatus = vStatus;
			}

			if (oFileId != null) {
				searchData.req.fileId = oFileId;
			}

			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(searchData.req.dataSecAttrs);
			}

			this.getInwardListingApi(searchData);
		},

		getInwardListingApi: function (searchInfo) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/inwardListingApi.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					recvDate = oFileId = null;
					this.bindInwardListingApi(data);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("inwardListingApi : Error");
				}.bind(this));
		},

		bindInwardListingApi: function (data) {
			if (data.hdr.status === "S" && data.resp.length > 0) {
				var vRecord = (data.hdr.pageNum * data.hdr.pageSize + 1) + "-" + (data.hdr.pageNum * data.hdr.pageSize + data.resp.length);
				this.byId("txtIWRecords").setText("Records: " + vRecord + " / " + data.hdr.totalCount);
				var pageNumber = Math.ceil(data.hdr.totalCount / 100);
				this.byId("txtIWPageNoErr").setText("/ " + pageNumber);
				this.byId("inIWPageNoErr").setValue(data.hdr.pageNum + 1);
				if (data.resp.length < 100) {
					this.byId("bIWNextErr").setEnabled(false);
				} else {
					this.byId("bIWNextErr").setEnabled(true);
				}
				this.getView().setModel(new JSONModel(data), "invInward");
			} else {
				this.byId("txtIWRecords").setText("Records: 0-0 / 0");
				this.byId("txtIWPageNoErr").setText("/ 0");
				this.byId("inIWPageNoErr").setValue((data.hdr.pageNum || 0) + 1);
				this.getView().setModel(new JSONModel({
					"resp": []
				}), "invInward");
				this.byId("bIWPrevErr").setEnabled(false);
				this.byId("bIWNextErr").setEnabled(false);
				if (data.hdr.status === "E") {
					MessageBox.error(data.hdr.message);
				} else {
					MessageBox.information("No Data");
				}
			}
		},

		onPressAdaptFilter: function () {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.onPressOutwardAdaptFilter();
			} else {
				this.onPressInwardAdaptFilter();
			}
		},

		/**
		 * Called to open Adapt filter to add additional filter to get data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onPressOutwardAdaptFilter: function () {
			if (!this._oAdpatOutwardFilter) {
				this._oAdpatOutwardFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.AdaptOutwardFilter",
					this);
				this.getView().addDependent(this._oAdpatOutwardFilter);
			}
			this._oAdpatOutwardFilter.open();
		},
		/**
		 * Call when user press on Apply or Cancel button in Adapt Filter dialog
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onPressOutwardFilterClose: function (oEvent) {
			//  //eslint-disable-line
			this._oAdpatOutwardFilter.close();
			if (oEvent.getSource().getId().includes("bOutApply")) {
				this.onPressGoFinal(1);
			}
		},

		/**
		 * Called to open Adapt filter to add additional filter to get data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onPressInwardAdaptFilter: function () {
			if (!this._oAdpatInwardFilter) {
				this._oAdpatInwardFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatInwardFilter);
			}
			this._oAdpatInwardFilter.open();
		},

		/**
		 * Call when user press on Apply or Cancel button in Adapt Filter dialog
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onPressInwardFilterClose: function (oEvent) {
			//  //eslint-disable-line
			this._oAdpatInwardFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				this.onPressInwardGoFinal(1);
			}
		},

		onSelectCriteria: function (oEvent) {
			if (oEvent == "") {
				var vSelectKey = this.getView().byId("slCriteria").getSelectedKey();
			} else {
				var vSelectKey = oEvent.getSource().getSelectedKey();
			}

			if (vSelectKey === "taxPeriod") {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.Period = true;
			} else {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.Period = false;
			}
		},

		onSelectCriteriaIW: function (oEvent) {
			if (oEvent == "") {
				var vSelectKey = this.getView().byId("slIWCriteria").getSelectedKey();
			} else {
				var vSelectKey = oEvent.getSource().getSelectedKey();
			}
			if (vSelectKey === "taxPeriod") {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.inPeriod = true;
			} else {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.inPeriod = false;
			}
		},

		onChangeSegment: function () {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.byId("idtableInvNew").setVisible(true);
				this.byId("hbNewError").setVisible(true);
				this.byId("idfbInvManageNew").setVisible(true);
				//this.byId("outtoolId").setVisible(true);
				// this.byId("sbNewManageLayout").setVisible(true);

				this.byId("idIWtableInvNew").setVisible(false);
				this.byId("hbIWNewError").setVisible(false);
				this.byId("idfbIWInvManageNew").setVisible(false);
				// this.byId("inwtoolId").setVisible(false);
				this.onPressGoFinal(1);
			} else {
				this.byId("idtableInvNew").setVisible(false);
				this.byId("hbNewError").setVisible(false);
				this.byId("idfbInvManageNew").setVisible(false);
				// this.byId("sbNewManageLayout").setVisible(false);

				this.byId("idIWtableInvNew").setVisible(true);
				this.byId("hbIWNewError").setVisible(true);
				this.byId("idfbIWInvManageNew").setVisible(true);
				// this.byId("inwtoolId").setVisible(true);
				//this.byId("outtoolId").setVisible(false);
				this.onChangeGSTReturn("");
				this.onPressInwardGoFinal(1);
			}
		},

		onChangeTrail: function () {
			this.byId("idDPPRInvManageinv2").setVisible(false);
			this.byId("idDPPRInvManageinv3").setVisible(true);
			this.getAuditTrilData();
		},
		onPressAuditTrailBack: function () {
			this.byId("idDPPRInvManageinv2").setVisible(true);
			this.byId("idDPPRInvManageinv3").setVisible(false);
		},

		onPressAuditTrilDownload: function (flag) {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			if (flag === "D") {
				if (this.AuditTypeflag == "O") {
					var searchInfo = {
						"req": {
							"entityId": [$.sap.entityID],
							"type": this.AuditTypeflag,
							"docNum": oData.docNo,
							"docDate": oData.docDate,
							"docType": oData.docType,
							"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
							"returnPeriod": oData.returnPeriod
						}
					};
				} else {
					var searchInfo = {
						"req": {
							"entityId": [$.sap.entityID],
							"type": this.AuditTypeflag,
							"docNum": oData.docNo,
							"docDate": oData.docDate,
							"docType": oData.docType,
							"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
							"returnPeriod": oData.returnPeriod,
							"sgstin": oData.suppGstin,
							"tradeName": oData.supTradeName,
							"legalName": oData.custOrSupName
						}
					};

				}

				var vUrl = "/aspsapapi/downloadAuditTrailReports.do";
				this.excelDownload(searchInfo, vUrl);
			} else {
				if (this.AuditTypeflag == "O") {
					var searchInfo = {
						"req": {
							"type": this.AuditTypeflag,
							"docNum": oData.docNo,
							"docDate": oData.docDate,
							"docType": oData.docType,
							"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
							"returnPeriod": oData.returnPeriod,
						}
					};

				} else {
					var searchInfo = {
						"req": {
							"type": this.AuditTypeflag,
							"docNum": oData.docNo,
							"docDate": oData.docDate,
							"docType": oData.docType,
							"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
							"returnPeriod": oData.returnPeriod,
							"sgstin": oData.suppGstin,
							"tradeName": oData.supTradeName,
							"legalName": oData.custOrSupName
						}
					};
				}

				var vUrl = "/aspsapapi/downloadAuditTrailSummaryReports.do";
				this.excelDownload(searchInfo, vUrl);
			}

		},

		getAuditTrilData: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			if (this.AuditTypeflag == "O") {
				var searchInfo = {
					"req": {
						"docNum": oData.docNo,
						"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
						"returnPeriod": oData.returnPeriod,
						"docType": oData.docType,
						"docDate": oData.docDate,
						"type": this.AuditTypeflag
					}
				};
			} else {
				var searchInfo = {
					"req": {
						"docNum": oData.docNo,
						"gstin": this.AuditTypeflag == "O" ? oData.suppGstin : oData.custGstin,
						"returnPeriod": oData.returnPeriod,
						"docType": oData.docType,
						"docDate": oData.docDate,
						"type": this.AuditTypeflag,
						"sgstin": oData.suppGstin,
						"tradeName": oData.supTradeName,
						"legalName": oData.custOrSupName
					}
				};
			}
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAuditTrailScreenIOData.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					//eslint-disable-line
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "AuditTril");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAuditTrailScreenIOData.do : Error");
				});
		},

		onPressBack: function () {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				var vPageNo = this.byId("inPageNoErr").getValue();
				this.onPressGoFinal(vPageNo);
			} else {
				var vPageNo = this.byId("inIWPageNoErr").getValue();
				this.onPressInwardGoFinal(vPageNo);
			}

			this.byId("sbNewManageLayout").setVisible(false);
			this.byId("tabInwardEdit").setVisible(false);
			this.byId("tabInward240Edit").setVisible(false);
			this.byId("idTabLayoutAction").setVisible(false);
			this.byId("tabLayoutEdit").setVisible(false);
			this.byId("tabOutwardEdit").setVisible(false);
			this._editInvoiceTab(true);
		},

		onProcessRowSelection: function (oEvent) {
			//eslint-disable-line
			var aPath = oEvent.getParameter("rowContext").getPath().split("/");
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			// var oTemp = JSON.parse(JSON.stringify(oData[aPath[1]]));
			var vDocNo = oData.resp[aPath[2]].docNo;

			this.getItem(vDocNo);

			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				// this.byId("tabOutwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
				this.byId("tabOutwardEdit").setVisible(true);
				this.byId("tabInwardEdit").setVisible(false);
			} else {
				// this.byId("tabInwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
				this.byId("tabOutwardEdit").setVisible(false);
				if (this.inward240Flag) {
					this.byId("tabInward240Edit").setVisible(true);
				} else {
					this.byId("tabInwardEdit").setVisible(true);
				}

			}
			this._editInvoiceTab(false);
		},

		onChangeSegmentLayout: function () {
			if (this.byId("sbNewManageLayout").getSelectedKey() === "TableLayout") {
				this.byId("tabOutwardEdit").setVisible(true);

				this.byId("tabLayoutEdit").setVisible(false);
				this.byId("idTabLayoutAction").setVisible(false);
			} else {
				this.byId("tabOutwardEdit").setVisible(false);

				this.byId("tabLayoutEdit").setVisible(true);
				this.byId("idTabLayoutAction").setVisible(true);
			}
		},

		/*buttonVis: function (gstin, taxPeriod) {
			var req = {
				"req": {
					"taxPeriod": taxPeriod,
					"gstin": gstin,
					"returnType": "GSTR1"
				}
			};
			var that = this;
			var GSTR3BModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/gstinIsFiled.do";
			$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					GSTR3BModel.setData(data.resp);
					oTaxReGstnView.setModel(GSTR3BModel, "buttonVis");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},*/

		onPressLink: function (oEvent, id, Flag) {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				// this.byId("tabOutwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");

				if (this.byId("sbNewManageLayout").getSelectedKey() === "TableLayout") {
					this.byId("tabOutwardEdit").setVisible(true);
				} else {
					this.byId("tabLayoutEdit").setVisible(true);
					this.byId("idTabLayoutAction").setVisible(true);
				}
				// this.byId("tabLayoutEdit").setVisible(true);
				// this.byId("idTabLayoutAction").setVisible(true);
				this.byId("sbNewManageLayout").setVisible(true);
				this.byId("tabInward240Edit").setVisible(false);
				this.byId("tabInwardEdit").setVisible(false);
				// this.byId("inwtoolId").setVisible(false);
				var vDocNo = this.getView().getModel("invOutward").getData().resp[oEvent.getSource().getParent().getIndex()].docKey;
				this.docKey = vDocNo;
				this.getItem(vDocNo, id);
				this.AuditTypeflag = "O";
				//	this.buttonVis();
			} else {
				// this.byId("tabInwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
				this.byId("sbNewManageLayout").setVisible(false);
				this.byId("tabOutwardEdit").setVisible(false);
				this.byId("tabLayoutEdit").setVisible(false);
				this.byId("idTabLayoutAction").setVisible(false);
				this.inward240Flag = Flag;
				if (Flag) {
					this.byId("tabInward240Edit").setVisible(true);
					// this.byId("inwtoolId").setVisible(true);
				} else {
					this.byId("tabInwardEdit").setVisible(true);
				}

				var vDocNo = this.getView().getModel("invInward").getData().resp[oEvent.getSource().getParent().getIndex()].docKey;
				this.docKey = vDocNo;
				this.getItemInward(vDocNo);
				this.AuditTypeflag = "I";
			}
			this._editInvoiceTab(false);
		},

		_editInvoiceTab: function (flag) {
			this.byId("dpNewHeader").setVisible(flag);

			this.byId("sbNewManageOI").setVisible(flag);
			this.byId("bNewManageBack").setVisible(!flag);

			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.byId("idtableInvNew").setVisible(flag);
				this.byId("hbNewError").setVisible(flag);
				//this.byId("outtoolId").setVisible(flag);
				// this.byId("sbNewManageLayout").setVisible(flag);
			} else {
				this.byId("idIWtableInvNew").setVisible(flag);
				this.byId("hbIWNewError").setVisible(flag);
				// this.byId("inwtoolId").setVisible(flag);
			}
		},

		getItem: function (vDocNo, vID) {
			var vSelectedKey = this.getView().byId("slCriteria").getSelectedKey(),
				aGstin = this.getView().byId("idfgiInvMGSINT").getSelectedKeys(),
				vDocNo;

			switch (vSelectedKey) {
			case "taxPeriod":
				var vReturnFromDate = this.getView().byId("iFromPeriod").getValue(),
					vReturnToDate = this.getView().byId("iToPeriod").getValue();
				break;
			case "docDate":
				var vDocFromDate = this.getView().byId("iFromDate").getValue(),
					vDocToDate = this.getView().byId("iToDate").getValue();
				break;
			case "recDate":
				var vReceivFromDate = this.getView().byId("iFromDate").getValue(),
					vReceivToDate = this.getView().byId("iToDate").getValue();
				break;
			}

			// if (this.getView().byId("iDocNo").getValue() === "") {
			// 	vDocNo = null;
			// } else {
			// 	vDocNo = this.getView().byId("iDocNo").getValue();
			// }

			var searchInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 100
				},
				"req": {
					"entityId": [$.sap.entityID],
					"receivFromDate": vReceivFromDate || null,
					"receivToDate": vReceivToDate || null,
					"returnFromDate": vReturnFromDate || null,
					"returnToDate": vReturnToDate || null,
					"docFromDate": vDocFromDate || null,
					"docToDate": vDocToDate || null,
					"docType": [],
					"id": vID,
					"docKey": this.docKey,
					"returnType": null,
					"dataCategory": null,
					"tableNumber": [],
					"gstnStatus": [],
					"counterPartyGstin": [],
					"counterPartyFlag": null,
					"processingStatus": null,
					"fileId": null,
					"dataOriginTypeCode": null,
					"refId": null,
					"showGstnData": false,
					"dataSecAttrs": {
						"GSTIN": this.removeAll(aGstin),
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getView().byId("iditbTablayout").setSelectedKey("DDHeader");
			this.getDocSearch(searchInfo);
		},

		getDocSearch: function (searchInfo) {
			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "InvoiceItemModel");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/docSearch.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var obj = data.resp[0];
					obj.totalCess = obj.invCessAdvaloremAmt + obj.invCessSpecificAmt;
					if (obj.complianceApplicable) {
						if (obj.isGstnError == false && obj.isSave == true) {
							obj.gstr1Status = "Save";
						} else if (obj.isGstnError == true && obj.isSave == true) {
							obj.gstr1Status = "Save with Error";
						} else if (obj.isGstnError == false && obj.isSave == false) {
							obj.gstr1Status = "Not Save";
						}
					}
					var oTemp = JSON.parse(JSON.stringify(obj));
					this._aspErrorList(oTemp, obj.errorList);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("docSearch : Error");
				}.bind(this));
		},

		getItemInward: function (vDocNo) {
			var vSelectedKey = this.getView().byId("slIWCriteria").getSelectedKey(),
				vDocNo;
			if (vSelectedKey === "taxPeriod") {
				var vReturnFromDate = this.getView().byId("iIWFromPeriod").getValue(),
					vReturnToDate = this.getView().byId("iIWToPeriod").getValue();
			} else {
				var vReceivFromDate = this.getView().byId("iIWFromDate").getValue(),
					vReceivToDate = this.getView().byId("iIWToDate").getValue();
			}
			var searchInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 100
				},
				"req": {
					"entityId": [$.sap.entityID],
					"receivFromDate": vReceivFromDate || null,
					"receivToDate": vReceivToDate || null,
					"returnFromDate": vReturnFromDate || null,
					"returnToDate": vReturnToDate || null,
					"docType": [],
					"docKey": this.docKey,
					"returnType": null,
					"dataCategory": null,
					"tableNumber": [],
					"gstnStatus": [],
					"counterPartyGstin": [],
					"counterPartyFlag": null,
					"processingStatus": null,
					"fileId": null,
					"dataOriginTypeCode": null,
					"refId": null,
					"showGstnData": false,
					"dataSecAttrs": {
						"GSTIN": this.getView().byId("idfgiIWInvMGSINT").getSelectedKeys(),
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getInwardDocSearch(searchInfo);
		},

		getInwardDocSearch: function (searchInfo) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/inwardDocSearch.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var errCodesFlag = false;
					if (data.resp[0].errorList != undefined && data.resp[0].errorList.length > 0) {
						for (var i = 0; i < data.resp[0].errorList.length; i++) {
							if (data.resp[0].errorList[i].errorType === "ERR") {
								errCodesFlag = true;
								break;
							}
						}
					}
					if (data.resp[0].isDuplicateCheck) {
						var oData = {
							"isLocked": errCodesFlag ? true : !data.resp[0].isDuplicateCheck,
							"lockedReason": "* User can not edit any record as duplication check is enabled."
						};
					} else {
						var oData = {
							"isLocked": errCodesFlag ? true : !data.resp[0].isLocked,
							"lockedReason": "* " + data.resp[0].lockedReason
						};
					}
					that.getView().setModel(new JSONModel(oData), "locked");
					var obj = data.resp[0];
					var oTemp = JSON.parse(JSON.stringify(obj));
					oTemp = that._headerInwardJsonGet(oTemp);
					that._aspErrorList(oTemp, obj.errorList);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("inwardDocSearch : Error");
				});
		},

		onSelectionChange: function () {
			var aGroupData = this.getView().byId("idGroupInvManage").getSelectedKey();
			if (aGroupData === "EWB") {
				this.getView().byId("idMandatoryInvManage").setVisible(true);
			} else {
				this.getView().byId("idMandatoryInvManage").setVisible(false);
			}
		},

		onApplyOutwardInvoice: function () {
			var aGroupData = this.getView().byId("idGroupInvManage").getSelectedKey();
			var aMandatory = this.getView().byId("idMandatoryInvManage").getSelectedKeys();
			// var aFieldData = this.getView().byId("idFieldsInvManage").getSelectedKey();
			var oDisplay = invManageEWB.allTF(false);
			// oDisplay = invManage.selectedFields(oDisplay, aFieldData);
			if (aGroupData === "EWB") {
				if (aMandatory === "" || aMandatory.length === 0) {
					oDisplay = invManageEWB.selectedGroup(oDisplay, aGroupData);
				} else {
					oDisplay = invManageEWB.selectedGroupMandit(oDisplay, aMandatory);
				}

			} else {
				oDisplay = invManageEWB.selectedGroup(oDisplay, aGroupData);
			}
			this.getView().setModel(new JSONModel(oDisplay), "display");
		},

		getDisplay: function () {
			// var oDisplay = this.getOwnerComponent().getModel("invManage").getData().FieldDisplay;
			var oDisplay = invManageEWB.allTF(true);
			this.getView().setModel(new JSONModel(oDisplay), "display");
		},

		/** 
		 * Toggle operation on Message Popover
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent eventing object
		 */
		handleErrorMessages: function (oEvent) {
			oMessagePopover.toggle(oEvent.getSource());
		},
		/**
		 * Called to get Error list from Response object and bind it to Message Popover
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data table data
		 * @param {Object} err error list
		 */
		_aspErrorList: function (data, err) {
			var aErrorMsg = [];
			if (err) {
				for (var i = 0; i < err.length; i++) {
					aErrorMsg.push({
						index: err[i].index,
						field: err[i].errorFields,
						type: err[i].errorType === "ERR" ? "Error" : "Information",
						title: err[i].errorCode,
						active: true,
						description: err[i].errorDesc,
						subtitle: err[i].errorDesc
					});

					var vValueState = err[i].errorFields + "State",
						vValueText = err[i].errorFields + "Text";

					if (typeof err[i].index === "undefined") {
						data[vValueState] = err[i].errorType;
						data[vValueText] = err[i].errorCode + ": " + err[i].errorDesc;
					} else {
						var aErrFields = err[i].errorFields.split(",");
						if (aErrFields.length > 1) {
							this._serErrorValueState(aErrFields, err[i], data);
						} else {
							data.lineItems[err[i].index][vValueState] = err[i].errorType;
							data.lineItems[err[i].index][vValueText] = err[i].errorCode + ": " + err[i].errorDesc;
						}
					}
				}
			}
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.byId("bMessage").setText(aErrorMsg.length);
				this.byId("bMessageLayout").setText(aErrorMsg.length);
				for (var i = 0; i < data.lineItems.length; i++) {
					if (data.lineItems[i].itemUqcUser == undefined) {
						data.lineItems[i].itemUqcUser = data.lineItems[i].itemUqc;
					}
					if (data.lineItems[i].itemQtyUser == undefined) {
						data.lineItems[i].itemQtyUser = data.lineItems[i].itemQty;
					}
				}
			} else {
				this.byId("bMessage1").setText(aErrorMsg.length);
				this.byId("bMessage2").setText(aErrorMsg.length);
				// data = this._headerInwardJsonGet(data);
			}
			oMessagePopover.setModel(new JSONModel(aErrorMsg), "Msg");
			this.getView().setModel(new JSONModel(data), "InvoiceItemModel");
			this._setEditable(false);
		},

		/**
		 * Called to set Value State of Component as Error/Info/None
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} aErrFields Response error fields
		 * @param {Object} err Response errors object
		 * @param {Object} data Data object to edit
		 */
		_serErrorValueState: function (aErrFields, err, data) {
			for (var j = 0; j < aErrFields.length; j++) {
				var vValueState = aErrFields[j] + "State";
				var vValueText = aErrFields[j] + "Text";
				if (!data.lineItems[err.index][vValueState]) {
					data.lineItems[err.index][vValueState] = err.errorType;
					data.lineItems[err.index][vValueText] = err.errorCode + ": " + err.errorDesc;
				} else {
					if (data.lineItems[err.index][vValueState] !== "ERR") {
						data.lineItems[err.index][vValueState] = err.errorType;
					}
					data.lineItems[err.index][vValueText] += "\n" + err.errorCode + ": " + err.errorDesc;
				}
			}
		},

		/**
		 * Called to Navigate to Previous or Next Error in Edit Invoice Management
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		pressPrevNextError: function (oEvent) {
			//eslint-disable-line
			var oModel = oMessagePopover.getModel("Msg"); //eslint-disable-line
			var arrCell = this.byId("tabOutwardEdit").getRows()[0].getCells();
			// var set1 = new Set();
			// set1.add("cgstAmt");
			// set1.add("sgstAmt");
			for (var i = 2; i < arrCell.length; i++) {
				if (arrCell[i].getName() === "cgstAmt") {
					// if (set1.has(arrCell[i].getName())) {
					arrCell[i].focus();
					return;
				}
			}
		},

		/**
		 * Called when user pressed on SaveToGstn button to intiate saving of Outward Invoices data in GSTN server
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onSaveOutwardInvoice: function (oEvent) {
			//eslint-disable-line
			var oData = this.getView().getModel("InvoiceItemModel").getData(),
				flag = false;

			for (var i = 0; i < oData.lineItems.length; i++) {
				if (!oData.lineItems[i].supplyType || !oData.lineItems[i].itemNo) {
					flag = true;
					break;
				}
			}
			if (oData.transType == "O") {
				if (!oData.suppGstin) {
					sap.m.MessageBox.error("Mandatory fields need to be filled", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			}

			if (!oData.returnPeriod || !oData.docType || !oData.docNo || !oData.docDate || flag) {
				sap.m.MessageBox.error("Mandatory fields need to be filled", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			// if ($.sap.myVar === "SV") {
			// 	var url; // = "/aspsapapi/saveInwardSvErrDoc.do";
			// 	sap.m.MessageToast.show("Structured Error Save Implementation is in progress...");
			// 	return;
			// } else {
			// 	url = "/aspsapapi/einvoicesaveDoc.do";
			// }
			var url = "/aspsapapi/einvoicesaveDocUI.do";
			var oPayload = {
				"req": []
			};
			//eslint-disable-line
			// delete oData.undefinedState;
			// delete oData.undefinedText;
			if (oData.dataOriginTypeCode === undefined) {
				sap.m.MessageBox.information("Data Origin Type Code is undefined");
				return;
			}
			oPayload.req.push(this._headerJson(oData));
			// delete oData.errorList;
			// delete oData.extractedBatchId;
			// delete oData.extractedDate;
			// delete oData.javaBeforeSavingOn;
			// delete oData.javaReqReceivedOn;
			// for (var i = 0; i < oData.lineItems.length; i++) {
			// 	delete oData.lineItems[i].id;
			// }
			// oData.dataOriginTypeCode = oData.dataOriginTypeCode.substr(0, 1) + "I";
			// oPayload.req.push(oData);
			this._saveInvoice(oPayload, url);
		},

		/**
		 * Ajax Call to save payload in database
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oPayload payload object
		 * @param {string} url URL to call api
		 */
		_saveInvoice: function (oPayload, url) {
			var that = this;
			var vManageOI = this.byId("sbNewManageOI").getSelectedKey();
			sap.ui.core.BusyIndicator.show(0);
			var oDocNum = oPayload.req[0].docNo;
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "E") {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
						if (vManageOI === "Outward") {
							that.getItem(oDocNum);
						} else {
							that.getItemInward(oDocNum);
						}
					} else {
						if (!data.resp[0].errors || data.resp[0].errors.length === 0) {
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact",
									onClose: function () {
										if (vManageOI === "Outward") {
											that.getItem(oDocNum);
										} else {
											that.getItemInward(oDocNum);
										}
									}
								});
							}
						} else {
							if (!this._oErrorMsg) {
								this._oErrorMsg = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.ErrorMsg", this);
								this.getView().addDependent(this._oErrorMsg);
							}
							var aError = [];
							data.resp.forEach(function (item) {
								item.errors.forEach(function (err) {
									aError.push(err);
								});
							});
							this._oErrorMsg.setModel(new JSONModel(aError), "ErrorMsg");
							this._oErrorMsg.setModel(new JSONModel({
								"docNum": oDocNum
							}), "ErrorProp");
							this._oErrorMsg.open();
						}
						// this._aspSaveDocResponse(data);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this._serverMessage(jqXHR.status);
				}.bind(this));
		},

		onCloseErrorMsg: function () {
			var oDocNum = this._oErrorMsg.getModel("ErrorProp").getProperty("/docNum");
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this.getItem(oDocNum);
			} else {
				this.getItemInward(oDocNum);
			}
			this._oErrorMsg.close();
		},

		/**
		 * Called to validate Response after Save Changes initiated to display appropriate message and bind error message list to Message Popover
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data response data
		 */
		_aspSaveDocResponse: function (data) {
			var that = this;
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				// this._clearOutwardValueState();
				var oModel = this.byId("tabOutwardEdit").getModel("InvoiceItemModel");
			} else {
				// this._clearInwardValueState();

				if (this.inward240Flag) {
					oModel = this.byId("tabInward240Edit").getModel("InvoiceItemModel");
				} else {
					oModel = this.byId("tabInwardEdit").getModel("InvoiceItemModel");
				}
			}
			var oData = oModel.getData();

			oData.id = data.resp[0].id;
			if (!data.resp[0].errors || data.resp[0].errors.length === 0) {
				if (data.hdr.status === "S") {
					sap.m.MessageBox.success(this.getResourceBundle().getText("msgSavedSuccess"), {
						styleClass: "sapUiSizeCompact",
						onClose: function () {
							that._editInvoiceTab(true);
						}
					});
				}
			} else {
				sap.m.MessageBox.success(this.getResourceBundle().getText("msgSavedWithError"), {
					styleClass: "sapUiSizeCompact"
				});
			}
			oModel.refresh(true);
		},

		/**
		 * Called when user pressed on SaveToGstn button to intiate saving of Inward Invoices data in GSTN server
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onSaveInwardInvoice: function () {
			if (this.inward240Flag) {
				var oData = this.byId("tabInward240Edit").getModel("InvoiceItemModel").getData(),
					flag = false;
			} else {
				var oData = this.byId("tabInwardEdit").getModel("InvoiceItemModel").getData(),
					flag = false;
			}

			for (var i = 0; i < oData.lineItems.length; i++) {
				if (!oData.lineItems[i].supplyType) {
					flag = true;
					break;
				}
			}
			if (oData.docNo !== "") {
				var docNumber = oData.docNo;
				var regSpecialCharcter = new RegExp(/^[ A-Za-z0-9-/]*$/);
				var regSpace = new RegExp(/^\S*$/);
				if (!regSpecialCharcter.test(docNumber)) {
					sap.m.MessageBox.error("Document Number contains special character", {
						styleClass: "sapUiSizeCompact"
					});
					return;

				} else if (!regSpace.test(docNumber)) {
					sap.m.MessageBox.error("Document Number should not have space", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			}
			if (!oData.custGstin || !oData.returnPeriod || !oData.docType || !oData.docNo || !oData.docDate || !oData.pos || flag) {
				sap.m.MessageBox.error("Mandatory fields need to be filled", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (this.inward240Flag) {
				if (!(oData.dispatcherGstin == null || oData.dispatcherGstin == "")) {
					if (oData.dispatcherGstin.length < 15) {
						sap.m.MessageBox.error("Dispatcher GSTIN cannot be less than 15 Digits", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
				}

				if (!(oData.shipToGstin == null || oData.shipToGstin == "")) {
					if (oData.shipToGstin.length < 15) {
						sap.m.MessageBox.error("Ship to GSTIN cannot be less than 15 Digits", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
				}

				if (!(oData.originalSupplierGstin == null || oData.originalSupplierGstin == "")) {
					if (oData.originalSupplierGstin.length < 15) {
						sap.m.MessageBox.error("Original Supplier GSTIN cannot be less than 15 Digits", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
				}

				if (!(oData.ecomGSTIN == null || oData.ecomGSTIN == "")) {
					if (oData.ecomGSTIN.length < 15) {
						sap.m.MessageBox.error("ecomGSTIN cannot be less than 15 Digits", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
				}

				if (!(oData.custGstin == null || oData.custGstin == "")) {
					if (oData.custGstin.length < 15) {
						sap.m.MessageBox.error("Customer GSTIN cannot be less than 15 Digits", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
				}
			}
			if (!(oData.portCode == null || oData.portCode == "")) {
				if (oData.portCode.length < 6) {
					sap.m.MessageBox.error("Port Code cannot be less than 6 Digits", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			}
			var oPayload = {
				"req": []
			};
			var url = "/aspsapapi/saveInwardDocUI.do";
			oPayload.req.push(this._headerInwardJson(oData));
			this._saveInvoice(oPayload, url);
		},

		/**
		 * Clearing value state and text for all fields in edit invoice screen for Outward
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearOutwardValueState: function () {
			if (this.inward240Flag) {
				var oModel = this.byId("tabOutward240Edit").getModel("InvoiceItemModel"),
					oData = oModel.getData();
			} else {
				var oModel = this.byId("tabOutwardEdit").getModel("InvoiceItemModel"),
					oData = oModel.getData();
			}

			this._setValueStateNone(oData);
			for (var i = 0; i < oData.lineItems.length; i++) {
				this._setValueStateNone(oData.lineItems[i]);
			}
			oModel.refresh(true);
		},

		/**
		 * Clearing value state and text for all fields in edit invoice screen for Inward
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearInwardValueState: function () {
			if (this.inward240Flag) {
				var oModel = this.byId("tabInward240Edit").getModel("InvoiceItemModel"),
					oData = oModel.getData();
			} else {
				var oModel = this.byId("tabInwardEdit").getModel("InvoiceItemModel"),
					oData = oModel.getData();
			}

			this._setValueStateNone(oData);
			for (var i = 0; i < oData.lineItems.length; i++) {
				this._setValueStateNone(oData.lineItems[i]);
			}
			oModel.refresh(true);
		},

		/**
		 * Setting Invoice management edit screen field state value as None
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Json object
		 */
		_setValueStateNone: function (data) {
			for (var field in data) {
				if (field.endsWith("State")) {
					data[field] = "None";
				}
			}
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Select Object
		 * @return {Object} Payload
		 */
		_headerJson: function (data) { //eslint-disable-line
			var oHeader = {
				"userId": this.validateValue(data.userId),
				"srcFileName": this.validateValue(data.srcFileName),
				"srcIdentifier": this.validateValue(data.srcIdentifier),
				"returnPeriod": this.validateValue(data.returnPeriod),
				"suppGstin": this.validateValue(data.suppGstin),
				"docType": this.validateValue(data.docType),
				"docNo": this.validateValue(data.docNo),
				"docDate": this.validateValue(data.docDate),
				"docAmt": this.validateValue(data.docAmt),
				"orgDocType": this.validateValue(data.orgDocType),
				"crDrPreGst": this.validateValue(data.crDrPreGst),
				"custGstin": this.validateValue(data.custGstin),
				"custOrSupType": this.validateValue(data.custOrSupType),
				"diffPercent": this.validateValue(data.diffPercent),
				"orgCgstin": this.validateValue(data.orgCgstin),
				"custOrSupName": this.validateValue(data.custOrSupName),
				"custOrSupCode": this.validateValue(data.custOrSupCode),
				"custOrSupAddr1": this.validateValue(data.custOrSupAddr1),
				"custOrSupAddr2": this.validateValue(data.custOrSupAddr2),
				"custOrSupAddr4": this.validateValue(data.custOrSupAddr4),
				"billToState": this.validateValue(data.billToState),
				"shipToState": this.validateValue(data.shipToState),
				"pos": this.validateValue(data.pos),
				"stateApplyingCess": this.validateValue(data.stateApplyingCess),
				"portCode": this.validateValue(data.portCode),
				"shippingBillNo": this.validateValue(data.shippingBillNo),
				"shippingBillDate": this.validateValue(data.shippingBillDate),
				"sec7OfIgstFlag": this.validateValue(data.sec7OfIgstFlag),
				"reverseCharge": this.validateValue(data.reverseCharge),
				"tcsFlag": this.validateValue(data.tcsFlag),
				"ecomGSTIN": this.validateValue(data.ecomGSTIN),
				"claimRefundFlag": this.validateValue(data.claimRefundFlag),
				"autoPopToRefundFlag": this.validateValue(data.autoPopToRefundFlag),
				"accVoucherNo": this.validateValue(data.accVoucherNo),
				"accVoucherDate": this.validateValue(data.accVoucherDate),
				"ewbNo": this.validateValue(data.ewbNo),
				"ewbDate": this.validateValue(data.ewbDate),
				"ewbNoresp": this.validateValue(data.ewbNoresp),
				"ewbDateResp": this.validateValue(data.ewbDateResp),
				"irn": this.validateValue(data.irn),
				"irnDate": this.validateValue(data.irnDate),
				"irnResponse": this.validateValue(data.irnResponse),
				"ackDate": this.validateValue(data.ackDate),
				"taxScheme": this.validateValue(data.taxScheme),
				"docCat": this.validateValue(data.docCat),
				"supTradeName": this.validateValue(data.supTradeName),
				"supLegalName": this.validateValue(data.supLegalName),
				"supBuildingNo": this.validateValue(data.supBuildingNo),
				"supBuildingName": this.validateValue(data.supBuildingName),
				"supLocation": this.validateValue(data.supLocation),
				"supPincode": this.validateValue(data.supPincode),
				"supStateCode": this.validateValue(data.supStateCode),
				"supPhone": this.validateValue(data.supPhone),
				"supEmail": this.validateValue(data.supEmail),
				"custTradeName": this.validateValue(data.custTradeName),
				"custPincode": this.validateValue(data.custPincode),
				"custPhone": this.validateValue(data.custPhone),
				"custEmail": this.validateValue(data.custEmail),
				"dispatcherGstin": this.validateValue(data.dispatcherGstin),
				"dispatcherTradeName": this.validateValue(data.dispatcherTradeName),
				"dispatcherBuildingNo": this.validateValue(data.dispatcherBuildingNo),
				"dispatcherBuildingName": this.validateValue(data.dispatcherBuildingName),
				"dispatcherLocation": this.validateValue(data.dispatcherLocation),
				"dispatcherPincode": this.validateValue(data.dispatcherPincode),
				"dispatcherStateCode": this.validateValue(data.dispatcherStateCode),
				"shipToGstin": this.validateValue(data.shipToGstin),
				"shipToTradeName": this.validateValue(data.shipToTradeName),
				"shipToLegalName": this.validateValue(data.shipToLegalName),
				"shipToBuildingNo": this.validateValue(data.shipToBuildingNo),
				"shipToBuildingName": this.validateValue(data.shipToBuildingName),
				"shipToLocation": this.validateValue(data.shipToLocation),
				"shipToPincode": this.validateValue(data.shipToPincode),
				"invOtherCharges": this.validateValue(data.invOtherCharges),
				"invAssessableAmt": this.validateValue(data.invAssessableAmt),
				"invIgstAmt": this.validateValue(data.invIgstAmt),
				"invCgstAmt": this.validateValue(data.invCgstAmt),
				"invSgstAmt": this.validateValue(data.invSgstAmt),
				"invCessAdvaloremAmt": this.validateValue(data.invCessAdvaloremAmt),
				"invCessSpecificAmt": this.validateValue(data.invCessSpecificAmt),
				"invStateCessAmt": this.validateValue(data.invStateCessAmt),
				"roundOff": this.validateValue(data.roundOff),
				"totalInvValueInWords": this.validateValue(data.totalInvValueInWords),
				"foreignCurrency": this.validateValue(data.foreignCurrency),
				"countryCode": this.validateValue(data.countryCode),
				"invValueFc": this.validateValue(data.invValueFc),
				"invPeriodStartDate": this.validateValue(data.invPeriodStartDate),
				"invPeriodEndDate": this.validateValue(data.invPeriodEndDate),
				"payeeName": this.validateValue(data.payeeName),
				"modeOfPayment": this.validateValue(data.modeOfPayment),
				"branchOrIfscCode": this.validateValue(data.branchOrIfscCode),
				"paymentTerms": this.validateValue(data.paymentTerms),
				"paymentInstruction": this.validateValue(data.paymentInstruction),
				"creditTransfer": this.validateValue(data.creditTransfer),
				"directDebit": this.validateValue(data.directDebit),
				"creditDays": this.validateValue(data.creditDays),
				"paymentDueDate": this.validateValue(data.paymentDueDate),
				"accDetail": this.validateValue(data.accDetail),
				"tdsFlag": this.validateValue(data.tdsFlag),
				"tranType": this.validateValue(data.tranType),
				"subsupplyType": this.validateValue(data.subsupplyType),
				"otherSupplyTypeDesc": this.validateValue(data.otherSupplyTypeDesc),
				"transporterID": this.validateValue(data.transporterID),
				"transporterName": this.validateValue(data.transporterName),
				"transportMode": this.validateValue(data.transportMode),
				"transportDocNo": this.validateValue(data.transportDocNo),
				"transportDocDate": this.validateValue(data.transportDocDate),
				"distance": this.validateValue(data.distance),
				"vehicleNo": this.validateValue(data.vehicleNo),
				"vehicleType": this.validateValue(data.vehicleType),
				"exchangeRt": this.validateValue(data.exchangeRt),
				"companyCode": this.validateValue(data.companyCode),
				"glPostingDate": this.validateValue(data.glPostingDate),
				"salesOrderNo": this.validateValue(data.salesOrderNo),
				"custTan": this.validateValue(data.custTan),
				"canReason": this.validateValue(data.canReason),
				"canRemarks": this.validateValue(data.canRemarks),
				"invStateCessSpecificAmt": this.validateValue(data.invStateCessSpecificAmt),
				"tcsFlagIncomeTax": this.validateValue(data.tcsFlagIncomeTax),
				"custPANOrAadhaar": this.validateValue(data.custPANOrAadhaar),
				"glStateCessSpecific": this.validateValue(data.glStateCessSpecific),
				"glCodeIgst": this.validateValue(data.glCodeIgst),
				"glCodeCgst": this.validateValue(data.glCodeCgst),
				"glCodeSgst": this.validateValue(data.glCodeSgst),
				"glCodeAdvCess": this.validateValue(data.glCodeAdvCess),
				"glCodeSpCess": this.validateValue(data.glCodeSpCess),
				"glCodeStateCess": this.validateValue(data.glCodeStateCess),
				"location": this.validateValue(data.location),
				"division": this.validateValue(data.division),
				"salesOrg": this.validateValue(data.salesOrg),
				"distChannel": this.validateValue(data.distChannel),
				"profitCentre1": this.validateValue(data.profitCentre1),
				"profitCentre2": this.validateValue(data.profitCentre2),
				"invRemarks": this.validateValue(data.invRemarks),
				"udf28": this.validateValue(data.udf28),
				"dataOriginTypeCode": data.dataOriginTypeCode.substr(0, 1) + "I",
				"lineItems": []
			};
			for (var i = 0; i < data.lineItems.length; i++) {
				oHeader.lineItems.push(this._itemJson(data.lineItems[i]));
			}
			return oHeader;
		},

		_headerInwardJson: function (data) { //eslint-disable-line
			if (this.inward240Flag) {
				var oHeader = {
					"id": this.validateValue(data.id),
					"irnDate": this.validateValue(data.irnDate),
					"docType": this.validateValue(data.docType),
					"docNo": this.validateValue(data.docNo),
					"docDate": this.validateValue(data.docDate),
					"reverseCharge": this.validateValue(data.reverseCharge),
					"suppGstin": this.validateValue(data.suppGstin),
					// "supTradeName": data.supTradeName || null,
					"custOrSupName": this.validateValue(data.custOrSupName),
					"custOrSupAddr1": this.validateValue(data.custOrSupAddr1),
					"custOrSupAddr2": this.validateValue(data.custOrSupAddr2),
					"custOrSupAddr3": this.validateValue(data.custOrSupAddr3),
					"custOrSupAddr4": this.validateValue(data.custOrSupAddr4),
					"custGstin": this.validateValue(data.custGstin),
					"pos": this.validateValue(data.pos),
					"dispatcherGstin": this.validateValue(data.dispatcherGstin),
					"shipToGstin": this.validateValue(data.shipToGstin),
					"invOtherCharges": this.validateValue(data.invOtherCharges),
					"invAssessableAmt": this.validateValue(data.invAssessableAmt),
					"invIgstAmt": this.validateValue(data.invIgstAmt),
					"invCgstAmt": this.validateValue(data.invCgstAmt),
					"invSgstAmt": this.validateValue(data.invSgstAmt),
					"invCessAdvaloremAmt": this.validateValue(data.invCessAdvaloremAmt),
					"invCessSpecificAmt": this.validateValue(data.invCessSpecificAmt),
					"invStateCessAmt": this.validateValue(data.invStateCessAmt),
					"invStateCessSpecificAmt": this.validateValue(data.invStateCessSpecificAmt),
					"itcEntitlement": this.validateValue(data.itcEntitlement),
					"portCode": this.validateValue(data.portCode),
					"billOfEntryNo": this.validateValue(data.billOfEntryNo),
					"billOfEntryDate": this.validateValue(data.billOfEntryDate),
					"paymentDueDate": this.validateValue(data.paymentDueDate),
					"ecomGSTIN": this.validateValue(data.ecomGSTIN),
					"returnPeriod": this.validateValue(data.returnPeriod),
					"originalSupplierGstin": this.validateValue(data.originalSupplierGstin),
					"diffPercent": this.validateValue(data.diffPercent),
					"sec7OfIgstFlag": this.validateValue(data.sec7OfIgstFlag),
					"claimRefundFlag": this.validateValue(data.claimRefundFlag),
					"autoPopToRefundFlag": this.validateValue(data.autoPopToRefundFlag),
					"crDrPreGst": this.validateValue(data.crDrPreGst),
					"custOrSupType": this.validateValue(data.custOrSupType),
					"custOrSupCode": this.validateValue(data.custOrSupCode),
					"stateApplyingCess": this.validateValue(data.stateApplyingCess),
					"tcsFlag": this.validateValue(data.tcsFlag),
					"tdsFlag": this.validateValue(data.tdsFlag),
					"userId": this.validateValue(data.userId),
					"companyCode": this.validateValue(data.companyCode),
					"srcFileName": this.validateValue(data.srcFileName),
					"division": this.validateValue(data.division),
					"purchaseOrganization": this.validateValue(data.purchaseOrganization),
					"postingDate": this.validateValue(data.postingDate),
					"ewbNo": this.validateValue(data.ewbNo),
					"ewbDate": this.validateValue(data.ewbDate),
					// "roundOff": data.roundOff || null,
					"purchaseVoucherNum": data.purchaseVoucherNum || null,
					"purchaseVoucherDate": this.validateValue(data.purchaseVoucherDate),
					"udf28": this.validateValue(data.udf28),
					"is240Format": this.inward240Flag,
					"dataOriginTypeCode": data.dataOriginTypeCode.substr(0, 1) + "I",
					"lineItems": []

				};
			} else {
				var oHeader = {
					"userId": this.validateValue(data.userId),
					"srcFileName": this.validateValue(data.srcFileName),
					"division": this.validateValue(data.division),
					"purchaseOrganization": this.validateValue(data.purchaseOrganization),
					"profitCentre3": this.validateValue(data.profitCentre3),
					"profitCentre4": this.validateValue(data.profitCentre4),
					"profitCentre5": this.validateValue(data.profitCentre5),
					"profitCentre6": this.validateValue(data.profitCentre6),
					"profitCentre7": this.validateValue(data.profitCentre7),
					"profitCentre8": this.validateValue(data.profitCentre8),
					"returnPeriod": this.validateValue(data.returnPeriod),
					"custGstin": this.validateValue(data.custGstin),
					"docType": this.validateValue(data.docType),
					"docNo": this.validateValue(data.docNo),
					"docDate": this.validateValue(data.docDate),
					"docAmt": this.validateValue(data.docAmt),
					"crDrPreGst": this.validateValue(data.crDrPreGst),
					"suppGstin": this.validateValue(data.suppGstin),
					"custOrSupType": this.validateValue(data.custOrSupType),
					"diffPercent": this.validateValue(data.diffPercent),
					"originalSupplierGstin": this.validateValue(data.originalSupplierGstin),
					"custOrSupName": this.validateValue(data.custOrSupName),
					"custOrSupCode": this.validateValue(data.custOrSupCode),
					"custOrSupAddr1": this.validateValue(data.custOrSupAddr1),
					"custOrSupAddr2": this.validateValue(data.custOrSupAddr2),
					"custOrSupAddr3": this.validateValue(data.custOrSupAddr3),
					"custOrSupAddr4": this.validateValue(data.custOrSupAddr4),
					"pos": this.validateValue(data.pos),
					"stateApplyingCess": this.validateValue(data.stateApplyingCess),
					"portCode": this.validateValue(data.portCode),
					"billOfEntryNo": this.validateValue(data.billOfEntryNo),
					"billOfEntryDate": this.validateValue(data.billOfEntryDate),
					"sec7OfIgstFlag": this.validateValue(data.sec7OfIgstFlag),
					"claimRefundFlag": this.validateValue(data.claimRefundFlag),
					"autoPopToRefundFlag": this.validateValue(data.autoPopToRefundFlag),
					"reverseCharge": this.validateValue(data.reverseCharge),
					"itcEntitlement": this.validateValue(data.itcEntitlement),
					"postingDate": this.validateValue(data.postingDate),
					"ewbNo": this.validateValue(data.ewbNo),
					"ewbDate": this.validateValue(data.ewbDate),
					"roundOff": this.validateValue(data.roundOff),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"is240Format": this.inward240Flag,
					"dataOriginTypeCode": data.dataOriginTypeCode.substr(0, 1) + "I",
					"lineItems": []

				};
			}

			// if (this.byId("slInvDataType").getSelectedKey() === "outward") {
			// 	this._outwardHeader(oHeader, data);
			// } else {
			// 	this._inwardHeader(oHeader, data);
			// }
			for (var i = 0; i < data.lineItems.length; i++) {
				oHeader.lineItems.push(this._itemInwardJson(data.lineItems[i]));
			}
			return oHeader;
		},

		_headerInwardJsonGet: function (data) { //eslint-disable-line
			if (this.inward240Flag) {
				var oHeader = {
					"id": this.validateValue(data.id),
					"irn": this.validateValue(data.irn),
					"irnDate": this.validateValue(data.irnDate),
					"docType": this.validateValue(data.docType),
					"docNo": this.validateValue(data.docNo),
					"docDate": this.validateValue(data.docDate),
					"reverseCharge": this.validateValue(data.reverseCharge),
					"suppGstin": this.validateValue(data.suppGstin),
					// "supTradeName": data.supTradeName || null,
					"custOrSupName": this.validateValue(data.custOrSupName),
					"custOrSupAddr1": this.validateValue(data.custOrSupAddr1),
					"custOrSupAddr2": this.validateValue(data.custOrSupAddr2),
					"custOrSupAddr3": this.validateValue(data.custOrSupAddr3),
					"custOrSupAddr4": this.validateValue(data.custOrSupAddr4),
					"custGstin": this.validateValue(data.custGstin),
					"pos": this.validateValue(data.pos),
					"dispatcherGstin": this.validateValue(data.dispatcherGstin),
					"shipToGstin": this.validateValue(data.shipToGstin),
					"invOtherCharges": this.validateValue(data.invOtherCharges),
					"invAssessableAmt": this.validateValue(data.invAssessableAmt),
					"invIgstAmt": this.validateValue(data.invIgstAmt),
					"invCgstAmt": this.validateValue(data.invCgstAmt),
					"invSgstAmt": this.validateValue(data.invSgstAmt),
					"invCessAdvaloremAmt": this.validateValue(data.invCessAdvaloremAmt),
					"invCessSpecificAmt": this.validateValue(data.invCessSpecificAmt),
					"invStateCessAmt": this.validateValue(data.invStateCessAmt),
					"invStateCessSpecificAmt": this.validateValue(data.invStateCessSpecificAmt),
					"itcEntitlement": this.validateValue(data.itcEntitlement),
					"portCode": this.validateValue(data.portCode),
					"billOfEntryNo": this.validateValue(data.billOfEntryNo),
					"billOfEntryDate": this.validateValue(data.billOfEntryDate),
					"paymentDueDate": this.validateValue(data.paymentDueDate),
					"ecomGSTIN": this.validateValue(data.ecomGSTIN),
					"returnPeriod": this.validateValue(data.returnPeriod),
					"originalSupplierGstin": this.validateValue(data.originalSupplierGstin),
					"diffPercent": this.validateValue(data.diffPercent),
					"sec7OfIgstFlag": this.validateValue(data.sec7OfIgstFlag),
					"claimRefundFlag": this.validateValue(data.claimRefundFlag),
					"autoPopToRefundFlag": this.validateValue(data.autoPopToRefundFlag),
					"crDrPreGst": this.validateValue(data.crDrPreGst),
					"custOrSupType": this.validateValue(data.custOrSupType),
					"custOrSupCode": this.validateValue(data.custOrSupCode),
					"stateApplyingCess": this.validateValue(data.stateApplyingCess),
					"tcsFlag": this.validateValue(data.tcsFlag),
					"tdsFlag": this.validateValue(data.tdsFlag),
					"userId": this.validateValue(data.userId),
					"companyCode": this.validateValue(data.companyCode),
					"srcFileName": this.validateValue(data.srcFileName),
					"division": this.validateValue(data.division),
					"purchaseOrganization": this.validateValue(data.purchaseOrganization),
					"postingDate": this.validateValue(data.postingDate),
					"ewbNo": this.validateValue(data.ewbNo),
					"ewbDate": this.validateValue(data.ewbDate),
					// "roundOff": data.roundOff || null,
					"purchaseVoucherNum": data.purchaseVoucherNum || null,
					"purchaseVoucherDate": this.validateValue(data.purchaseVoucherDate),
					"udf28": this.validateValue(data.udf28),
					"is240Format": this.inward240Flag,
					"dataOriginTypeCode": data.dataOriginTypeCode,
					"lineItems": []

				};
			} else {
				oHeader = {
					"userId": this.validateValue(data.userId),
					"srcFileName": this.validateValue(data.srcFileName),
					"division": this.validateValue(data.division),
					"purchaseOrganization": this.validateValue(data.purchaseOrganization),
					"profitCentre3": this.validateValue(data.profitCentre3),
					"profitCentre4": this.validateValue(data.profitCentre4),
					"profitCentre5": this.validateValue(data.profitCentre5),
					"profitCentre6": this.validateValue(data.profitCentre6),
					"profitCentre7": this.validateValue(data.profitCentre7),
					"profitCentre8": this.validateValue(data.profitCentre8),
					"returnPeriod": this.validateValue(data.returnPeriod),
					"custGstin": this.validateValue(data.custGstin),
					"docType": this.validateValue(data.docType),
					"docNo": this.validateValue(data.docNo),
					"docDate": this.validateValue(data.docDate),
					"docAmt": this.validateValue(data.docAmt),
					"crDrPreGst": this.validateValue(data.crDrPreGst),
					"suppGstin": this.validateValue(data.suppGstin),
					"custOrSupType": this.validateValue(data.custOrSupType),
					"diffPercent": this.validateValue(data.diffPercent),
					"originalSupplierGstin": this.validateValue(data.originalSupplierGstin),
					"custOrSupName": this.validateValue(data.custOrSupName),
					"custOrSupCode": this.validateValue(data.custOrSupCode),
					"custOrSupAddr1": this.validateValue(data.custOrSupAddr1),
					"custOrSupAddr2": this.validateValue(data.custOrSupAddr2),
					"custOrSupAddr3": this.validateValue(data.custOrSupAddr3),
					"custOrSupAddr4": this.validateValue(data.custOrSupAddr4),
					"pos": this.validateValue(data.pos),
					"stateApplyingCess": this.validateValue(data.stateApplyingCess),
					"portCode": this.validateValue(data.portCode),
					"billOfEntryNo": this.validateValue(data.billOfEntryNo),
					"billOfEntryDate": this.validateValue(data.billOfEntryDate),
					"sec7OfIgstFlag": this.validateValue(data.sec7OfIgstFlag),
					"claimRefundFlag": this.validateValue(data.claimRefundFlag),
					"autoPopToRefundFlag": this.validateValue(data.autoPopToRefundFlag),
					"reverseCharge": this.validateValue(data.reverseCharge),
					"itcEntitlement": this.validateValue(data.itcEntitlement),
					"postingDate": this.validateValue(data.postingDate),
					"ewbNo": this.validateValue(data.ewbNo),
					"ewbDate": this.validateValue(data.ewbDate),
					"roundOff": this.validateValue(data.roundOff),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"is240Format": this.inward240Flag,
					"dataOriginTypeCode": data.dataOriginTypeCode,
					"errorList": data.errorList,
					"lineItems": []

				};
			}

			// if (this.byId("slInvDataType").getSelectedKey() === "outward") {
			// 	this._outwardHeader(oHeader, data);
			// } else {
			// 	this._inwardHeader(oHeader, data);
			// }
			for (var i = 0; i < data.lineItems.length; i++) {
				oHeader.lineItems.push(this._itemInwardJsonGet(data.lineItems[i]));
			}
			return oHeader;
		},

		_getOtherOutFiltersASP: function (search) {
			var oDataSecurity = this.getView().getModel("userPermission").getData().respData.dataSecurity.items,
				vDataType = this.byId("sbNewManageOI").getSelectedKey();

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slOutProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slOutPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slOutDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slOutLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg && vDataType === "Inward") {
				search.PO = this.byId("slOutPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg && vDataType === "Outward") {
				search.SO = this.byId("slOutSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel && vDataType === "Outward") {
				search.DC = this.byId("slOutDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("slOutuserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("slOutuserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("slOutuserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("slOutuserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("slOutuserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("slOutuserAccess6").getSelectedKeys();
			}
			return;
		},

		_getOtherFiltersASP: function (search) {
			var oDataSecurity = this.getView().getModel("userPermission").getData().respData.dataSecurity.items,
				vDataType = this.byId("sbNewManageOI").getSelectedKey();

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg && vDataType === "Inward") {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg && vDataType === "Outward") {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel && vDataType === "Outward") {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("sluserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("sluserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("sluserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("sluserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("sluserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("sluserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data line item data object
		 * @return {Object} Line item object
		 */
		_itemJson: function (data) { //eslint-disable-line
			var oItems = {
				"itemNo": this.validateValue(data.itemNo),
				"glCodeTaxableVal": this.validateValue(data.glCodeTaxableVal),
				"supplyType": this.validateValue(data.supplyType),
				"fob": this.validateValue(data.fob),
				"exportDuty": this.validateValue(data.exportDuty),
				"hsnsacCode": this.validateValue(data.hsnsacCode),
				"productCode": this.validateValue(data.productCode),
				"itemDesc": this.validateValue(data.itemDesc),
				"itemType": this.validateValue(data.itemType),
				"itemUqc": this.validateValue(data.itemUqc),
				"itemQty": this.validateValue(data.itemQty),
				"itemUqcUser": this.validateValue(data.itemUqcUser),
				"itemQtyUser": this.validateValue(data.itemQtyUser),
				"taxableVal": this.validateValue(data.taxableVal),
				"igstRt": this.validateValue(data.igstRt),
				"igstAmt": this.validateValue(data.igstAmt),
				"cgstRt": this.validateValue(data.cgstRt),
				"cgstAmt": this.validateValue(data.cgstAmt),
				"sgstRt": this.validateValue(data.sgstRt),
				"sgstAmt": this.validateValue(data.sgstAmt),
				"cessRtAdvalorem": this.validateValue(data.cessRtAdvalorem),
				"cessAmtAdvalorem": this.validateValue(data.cessAmtAdvalorem),
				"cessRtSpecific": this.validateValue(data.cessRtSpecific),
				"cessAmtSpecfic": this.validateValue(data.cessAmtSpecfic),
				"stateCessRt": this.validateValue(data.stateCessRt),
				"stateCessAmt": this.validateValue(data.stateCessAmt),
				"otherValues": this.validateValue(data.otherValues),
				"lineItemAmt": this.validateValue(data.lineItemAmt),
				"tcsAmt": this.validateValue(data.tcsAmt),
				"itcFlag": this.validateValue(data.itcFlag),
				"crDrReason": this.validateValue(data.crDrReason),
				"plantCode": this.validateValue(data.plantCode),
				"serialNoII": this.validateValue(data.serialNoII),
				"productName": this.validateValue(data.productName),
				"isService": this.validateValue(data.isService),
				"barcode": this.validateValue(data.barcode),
				"batchNameOrNo": this.validateValue(data.batchNameOrNo),
				"batchExpiryDate": this.validateValue(data.batchExpiryDate),
				"warrantyDate": this.validateValue(data.warrantyDate),
				"originCountry": this.validateValue(data.originCountry),
				"freeQuantity": this.validateValue(data.freeQuantity),
				"unitPrice": this.validateValue(data.unitPrice),
				"itemAmt": this.validateValue(data.itemAmt),
				"itemDiscount": this.validateValue(data.itemDiscount),
				"preTaxAmt": this.validateValue(data.preTaxAmt),
				"totalItemAmt": this.validateValue(data.totalItemAmt),
				"preceedingInvNo": this.validateValue(data.preceedingInvNo),
				"preceedingInvDate": this.validateValue(data.preceedingInvDate),
				"orderLineRef": this.validateValue(data.orderLineRef),
				"supportingDocURL": this.validateValue(data.supportingDocURL),
				"supportingDocBase64": this.validateValue(data.supportingDocBase64),
				"tcsCgstAmt": this.validateValue(data.tcsCgstAmt),
				"tcsSgstAmt": this.validateValue(data.tcsSgstAmt),
				"tdsIgstAmt": this.validateValue(data.tdsIgstAmt),
				"tdsCgstAmt": this.validateValue(data.tdsCgstAmt),
				"tdsSgstAmt": this.validateValue(data.tdsSgstAmt),
				"subDivision": this.validateValue(data.subDivision),
				"udf1": this.validateValue(data.udf1),
				"udf2": this.validateValue(data.udf2),
				"udf3": this.validateValue(data.udf3),
				"udf4": this.validateValue(data.udf4),
				"udf5": this.validateValue(data.udf5),
				"udf6": this.validateValue(data.udf6),
				"udf7": this.validateValue(data.udf7),
				"udf8": this.validateValue(data.udf8),
				"udf9": this.validateValue(data.udf9),
				"udf10": this.validateValue(data.udf10),
				"udf11": this.validateValue(data.udf11),
				"udf12": this.validateValue(data.udf12),
				"udf13": this.validateValue(data.udf13),
				"udf14": this.validateValue(data.udf14),
				"udf15": this.validateValue(data.udf15),
				"udf16": this.validateValue(data.udf16),
				"udf17": this.validateValue(data.udf17),
				"udf18": this.validateValue(data.udf18),
				"udf19": this.validateValue(data.udf19),
				"udf20": this.validateValue(data.udf20),
				"udf21": this.validateValue(data.udf21),
				"udf22": this.validateValue(data.udf22),
				"udf23": this.validateValue(data.udf23),
				"udf24": this.validateValue(data.udf24),
				"udf25": this.validateValue(data.udf25),
				"udf26": this.validateValue(data.udf26),
				"udf27": this.validateValue(data.udf27),
				"udf28": this.validateValue(data.udf28),
				"udf29": this.validateValue(data.udf29),
				"udf30": this.validateValue(data.udf30),
				"ecomTransactionID": this.validateValue(data.ecomTransactionID),
				"attributeName": this.validateValue(data.attributeName),
				"attributeValue": this.validateValue(data.attributeValue),
				"stateCessSpecificRt": this.validateValue(data.stateCessSpecificRt),
				"stateCessSpecificAmt": this.validateValue(data.stateCessSpecificAmt),
				"tcsRtIncomeTax": this.validateValue(data.tcsRtIncomeTax),
				"tcsAmtIncomeTax": this.validateValue(data.tcsAmtIncomeTax),
				"receiptAdviceDate": this.validateValue(data.receiptAdviceDate),
				"addlInfo": this.validateValue(data.addlInfo),
				"docRefNo": this.validateValue(data.docRefNo),
				"receiptAdviceRef": this.validateValue(data.receiptAdviceRef),
				"contractRef": this.validateValue(data.contractRef),
				"externalRef": this.validateValue(data.externalRef),
				"projectRef": this.validateValue(data.projectRef),
				"custPoRefNo": this.validateValue(data.custPoRefNo),
				"invRef": this.validateValue(data.invRef),
				"tenderRef": this.validateValue(data.tenderRef),
				"paidAmt": this.validateValue(data.paidAmt),
				"balanceAmt": this.validateValue(data.balanceAmt),
				"custPoRefDate": this.validateValue(data.custPoRefDate),
				"profitCentre3": this.validateValue(data.profitCentre3),
				"profitCentre4": this.validateValue(data.profitCentre4),
				"profitCentre5": this.validateValue(data.profitCentre5),
				"profitCentre6": this.validateValue(data.profitCentre6),
				"profitCentre7": this.validateValue(data.profitCentre7),
				"profitCentre8": this.validateValue(data.profitCentre8)

			};
			// if (this.byId("slInvDataType").getSelectedKey() === "outward") {
			// 	this._outwardItems(oItems, data);
			// } else {
			// 	this._inwardItems(oItems, data);
			// }
			return oItems;
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data line item data object
		 * @return {Object} Line item object
		 */
		_itemInwardJson: function (data) { //eslint-disable-line
			if (this.inward240Flag) {
				var oItems = {
					"taxScheme": this.validateValue(data.taxScheme),
					"supplyType": this.validateValue(data.supplyType),
					"docCat": this.validateValue(data.docCat),
					"supStateCode": this.validateValue(data.supStateCode),
					"supPhone": this.validateValue(data.supPhone),
					"supEmail": this.validateValue(data.supEmail),
					"custTradeName": this.validateValue(data.custTradeName),
					"custLegalName": this.validateValue(data.custLegalName),
					"custBuildingNo": this.validateValue(data.custBuildingNo),
					"custBuildingName": this.validateValue(data.custBuildingName),
					"custLocation": this.validateValue(data.custLocation),
					"custPincode": this.validateValue(data.custPincode),
					"billToState": this.validateValue(data.billToState),
					"custPhone": this.validateValue(data.custPhone),
					"custEmail": this.validateValue(data.custEmail),
					"dispatcherTradeName": this.validateValue(data.dispatcherTradeName),
					"dispatcherBuildingNo": this.validateValue(data.dispatcherBuildingNo),
					"dispatcherBuildingName": this.validateValue(data.dispatcherBuildingName),
					"dispatcherLocation": this.validateValue(data.dispatcherLocation),
					"dispatcherPincode": this.validateValue(data.dispatcherPincode),
					"dispatcherStateCode": this.validateValue(data.dispatcherStateCode),
					"shipToTradeName": this.validateValue(data.shipToTradeName),
					"shipToLegalName": this.validateValue(data.shipToLegalName),
					"shipToBuildingNo": this.validateValue(data.shipToBuildingNo),
					"shipToBuildingName": this.validateValue(data.shipToBuildingName),
					"shipToLocation": this.validateValue(data.shipToLocation),
					"shipToPincode": this.validateValue(data.shipToPincode),
					"shipToState": this.validateValue(data.shipToState),
					"itemNo": this.validateValue(data.itemNo),
					"serialNoII": this.validateValue(data.serialNoII),
					"productName": this.validateValue(data.productName),
					"itemDesc": this.validateValue(data.itemDesc),
					"isService": this.validateValue(data.isService),
					"hsnsacCode": this.validateValue(data.hsnsacCode),
					"barcode": this.validateValue(data.barcode),
					"batchNameOrNo": this.validateValue(data.batchNameOrNo),
					"batchExpiryDate": this.validateValue(data.batchExpiryDate),
					"warrantyDate": this.validateValue(data.warrantyDate),
					"orderLineRef": this.validateValue(data.orderLineRef),
					"attributeName": this.validateValue(data.attributeName),
					"attributeValue": this.validateValue(data.attributeValue),
					"originCountry": this.validateValue(data.originCountry),
					"itemUqc": this.validateValue(data.itemUqc),
					"itemQty": this.validateValue(data.itemQty),
					"freeQuantity": this.validateValue(data.freeQuantity),
					"unitPrice": this.validateValue(data.unitPrice),
					"itemAmt": this.validateValue(data.itemAmt),
					"itemDiscount": this.validateValue(data.itemDiscount),
					"preTaxAmt": this.validateValue(data.preTaxAmt),
					"taxableVal": this.validateValue(data.taxableVal),
					"igstRt": this.validateValue(data.igstRt),
					"igstAmt": this.validateValue(data.igstAmt),
					"cgstRt": this.validateValue(data.cgstRt),
					"cgstAmt": this.validateValue(data.cgstAmt),
					"sgstRt": this.validateValue(data.sgstRt),
					"sgstAmt": this.validateValue(data.sgstAmt),
					"cessRtAdvalorem": this.validateValue(data.cessRtAdvalorem),
					"cessAmtAdvalorem": this.validateValue(data.cessAmtAdvalorem),
					"cessRtSpecific": this.validateValue(data.cessRtSpecific),
					"cessAmtSpecfic": this.validateValue(data.cessAmtSpecfic),
					"stateCessRt": this.validateValue(data.stateCessRt),
					"stateCessAmt": this.validateValue(data.stateCessAmt),
					"stateCessSpecificRt": this.validateValue(data.stateCessSpecificRt),
					"stateCessSpecificAmt": this.validateValue(data.stateCessSpecificAmt),
					"supTradeName": this.validateValue(data.supTradeName),
					"otherValues": this.validateValue(data.otherValues),
					"totalItemAmt": this.validateValue(data.totalItemAmt),
					"lineItemAmt": this.validateValue(data.lineItemAmt),
					// "roundOff": data.roundOff || null,
					"purchaseVoucherNum": this.validateValue(data.purchaseVoucherNum),
					"totalInvValueInWords": this.validateValue(data.totalInvValueInWords),
					"eligibilityIndicator": this.validateValue(data.eligibilityIndicator),
					"commonSupplyIndicator": this.validateValue(data.commonSupplyIndicator),
					"availableIgst": this.validateValue(data.availableIgst),
					"availableCgst": this.validateValue(data.availableCgst),
					"availableSgst": this.validateValue(data.availableSgst),
					"availableCess": this.validateValue(data.availableCess),
					"itcReversalIdentifier": this.validateValue(data.itcReversalIdentifier),
					"tcsFlagIncomeTax": this.validateValue(data.tcsFlagIncomeTax),
					"tcsRtIncomeTax": this.validateValue(data.tcsRtIncomeTax),
					"tcsAmtIncomeTax": this.validateValue(data.tcsAmtIncomeTax),
					"foreignCurrency": this.validateValue(data.foreignCurrency),
					"countryCode": this.validateValue(data.countryCode),
					"invValueFc": this.validateValue(data.invValueFc),
					"invRemarks": this.validateValue(data.invRemarks),
					"invPeriodStartDate": this.validateValue(data.invPeriodStartDate),
					"invPeriodEndDate": this.validateValue(data.invPeriodEndDate),
					"originalDocNo": this.validateValue(data.originalDocNo),
					"originalDocDate": this.validateValue(data.originalDocDate),
					"invRef": this.validateValue(data.invRef),
					"receiptAdviceRef": this.validateValue(data.receiptAdviceRef),
					"receiptAdviceDate": this.validateValue(data.receiptAdviceDate),
					"tenderRef": this.validateValue(data.tenderRef),
					"contractRef": this.validateValue(data.contractRef),
					"externalRef": this.validateValue(data.externalRef),
					"projectRef": this.validateValue(data.projectRef),
					"contractNumber": this.validateValue(data.contractNumber),
					"contractDate": this.validateValue(data.contractDate),
					"payeeName": this.validateValue(data.payeeName),
					"modeOfPayment": this.validateValue(data.modeOfPayment),
					"branchOrIfscCode": this.validateValue(data.branchOrIfscCode),
					"paymentTerms": this.validateValue(data.paymentTerms),
					"paymentInstruction": this.validateValue(data.paymentInstruction),
					"creditTransfer": this.validateValue(data.creditTransfer),
					"directDebit": this.validateValue(data.directDebit),
					"creditDays": this.validateValue(data.creditDays),
					"paidAmt": this.validateValue(data.paidAmt),
					"balanceAmt": this.validateValue(data.balanceAmt),
					"accDetail": this.validateValue(data.accDetail),
					"supportingDocURL": this.validateValue(data.supportingDocURL),
					"supportingDoc": this.validateValue(data.supportingDoc),
					"addlInfo": this.validateValue(data.addlInfo),
					"tranType": this.validateValue(data.tranType),
					"subsupplyType": this.validateValue(data.subsupplyType),
					"otherSupplyTypeDesc": this.validateValue(data.otherSupplyTypeDesc),
					"transporterID": this.validateValue(data.transporterID),
					"transporterName": this.validateValue(data.transporterName),
					"transportMode": this.validateValue(data.transportMode),
					"transportDocNo": this.validateValue(data.transportDocNo),
					"transportDocDate": this.validateValue(data.transportDocDate),
					"distance": this.validateValue(data.distance),
					"vehicleNo": this.validateValue(data.vehicleNo),
					"vehicleType": this.validateValue(data.vehicleType),
					"orgDocType": this.validateValue(data.orgDocType),
					"productCode": this.validateValue(data.productCode),
					"itemType": this.validateValue(data.itemType),
					"cifValue": this.validateValue(data.cifValue),
					"customDuty": this.validateValue(data.customDuty),
					"exchangeRt": this.validateValue(data.exchangeRt),
					"crDrReason": this.validateValue(data.crDrReason),
					"tcsIgstAmt": this.validateValue(data.tcsIgstAmt),
					"tcsCgstAmt": this.validateValue(data.tcsCgstAmt),
					"tcsSgstAmt": this.validateValue(data.tcsSgstAmt),
					"tdsIgstAmt": this.validateValue(data.tdsIgstAmt),
					"tdsCgstAmt": this.validateValue(data.tdsCgstAmt),
					"tdsSgstAmt": this.validateValue(data.tdsSgstAmt),
					"srcIdentifier": this.validateValue(data.srcIdentifier),
					"plantCode": this.validateValue(data.plantCode),
					"subDivision": this.validateValue(data.subDivision),
					"location": this.validateValue(data.location),
					"profitCentre": this.validateValue(data.profitCentre),
					"profitCentre2": this.validateValue(data.profitCentre2),
					"profitCentre3": this.validateValue(data.profitCentre3),
					"profitCentre4": this.validateValue(data.profitCentre4),
					"profitCentre5": this.validateValue(data.profitCentre5),
					"profitCentre6": this.validateValue(data.profitCentre6),
					"profitCentre7": this.validateValue(data.profitCentre7),
					"profitCentre8": this.validateValue(data.profitCentre8),
					"glCodeTaxableVal": this.validateValue(data.glCodeTaxableVal),
					"glCodeIgst": this.validateValue(data.glCodeIgst),
					"glCodeCgst": this.validateValue(data.glCodeCgst),
					"glCodeSgst": this.validateValue(data.glCodeSgst),
					"glCodeAdvCess": this.validateValue(data.glCodeAdvCess),
					"glCodeSpCess": this.validateValue(data.glCodeSpCess),
					"glCodeStateCess": this.validateValue(data.glCodeStateCess),
					"glStateCessSpecific": this.validateValue(data.glStateCessSpecific),
					"contractValue": this.validateValue(data.contractValue),
					"docRefNo": this.validateValue(data.docRefNo),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"udf5": this.validateValue(data.udf5),
					"udf6": this.validateValue(data.udf6),
					"udf7": this.validateValue(data.udf7),
					"udf8": this.validateValue(data.udf8),
					"udf9": this.validateValue(data.udf9),
					"udf10": this.validateValue(data.udf10),
					"udf11": this.validateValue(data.udf11),
					"udf12": this.validateValue(data.udf12),
					"udf13": this.validateValue(data.udf13),
					"udf14": this.validateValue(data.udf14),
					"udf15": this.validateValue(data.udf15),
					"udf16": this.validateValue(data.udf16),
					"udf17": this.validateValue(data.udf17),
					"udf18": this.validateValue(data.udf18),
					"udf19": this.validateValue(data.udf19),
					"udf20": this.validateValue(data.udf20),
					"udf21": this.validateValue(data.udf21),
					"udf22": this.validateValue(data.udf22),
					"udf23": this.validateValue(data.udf23),
					"udf24": this.validateValue(data.udf24),
					"udf25": this.validateValue(data.udf25),
					"udf26": this.validateValue(data.udf26),
					"udf27": this.validateValue(data.udf27),
					"udf29": this.validateValue(data.udf29),
					"udf30": this.validateValue(data.udf30),

				};
			} else {
				var oItems = {
					"itemNo": this.validateValue(data.itemNo),
					"glCodeTaxableVal": this.validateValue(data.glCodeTaxableVal),
					"glCodeIgst": this.validateValue(data.glCodeIgst),
					"glCodeCgst": this.validateValue(data.glCodeCgst),
					"glCodeSgst": this.validateValue(data.glCodeSgst),
					"glCodeAdvCess": this.validateValue(data.glCodeAdvCess),
					"glCodeSpCess": this.validateValue(data.glCodeSpCess),
					"glCodeStateCess": this.validateValue(data.glCodeStateCess),
					"supplyType": this.validateValue(data.supplyType),
					"originalDocNo": this.validateValue(data.originalDocNo),
					"originalDocDate": this.validateValue(data.originalDocDate),
					"cifValue": this.validateValue(data.cifValue),
					"customDuty": this.validateValue(data.customDuty),
					"hsnsacCode": this.validateValue(data.hsnsacCode),
					"productCode": this.validateValue(data.productCode),
					"itemDesc": this.validateValue(data.itemDesc),
					"itemType": this.validateValue(data.itemType),
					"itemUqc": this.validateValue(data.itemUqc),
					"itemQty": this.validateValue(data.itemQty),
					"taxableVal": this.validateValue(data.taxableVal),
					"igstRt": this.validateValue(data.igstRt),
					"igstAmt": this.validateValue(data.igstAmt),
					"cgstRt": this.validateValue(data.cgstRt),
					"cgstAmt": this.validateValue(data.cgstAmt),
					"sgstRt": this.validateValue(data.sgstRt),
					"sgstAmt": this.validateValue(data.sgstAmt),
					"cessRtAdvalorem": this.validateValue(data.cessRtAdvalorem),
					"cessAmtAdvalorem": this.validateValue(data.cessAmtAdvalorem),
					"cessRtSpecific": this.validateValue(data.cessRtSpecific),
					"cessAmtSpecfic": this.validateValue(data.cessAmtSpecfic),
					"stateCessRt": this.validateValue(data.stateCessRt),
					"stateCessAmt": this.validateValue(data.stateCessAmt),
					"otherValues": this.validateValue(data.otherValues),
					"lineItemAmt": this.validateValue(data.lineItemAmt),
					"adjustmentRefNo": this.validateValue(data.adjustmentRefNo),
					"adjustmentRefDate": this.validateValue(data.adjustmentRefDate),
					"adjustedTaxableValue": this.validateValue(data.adjustedTaxableValue),
					"adjustedIgstAmt": this.validateValue(data.adjustedIgstAmt),
					"adjustedCgstAmt": this.validateValue(data.adjustedCgstAmt),
					"adjustedSgstAmt": this.validateValue(data.adjustedSgstAmt),
					"adjustedCessAmtAdvalorem": this.validateValue(data.adjustedCessAmtAdvalorem),
					"adjustedCessAmtSpecific": this.validateValue(data.adjustedCessAmtSpecific),
					"adjustedStateCessAmt": this.validateValue(data.adjustedStateCessAmt),
					"eligibilityIndicator": this.validateValue(data.eligibilityIndicator),
					"commonSupplyIndicator": this.validateValue(data.commonSupplyIndicator),
					"availableIgst": this.validateValue(data.availableIgst),
					"availableCgst": this.validateValue(data.availableCgst),
					"availableSgst": this.validateValue(data.availableSgst),
					"availableCess": this.validateValue(data.availableCess),
					"itcReversalIdentifier": this.validateValue(data.itcReversalIdentifier),
					"crDrReason": this.validateValue(data.crDrReason),
					"purchaseVoucherNum": data.purchaseVoucherNum || null,
					"purchaseVoucherDate": this.validateValue(data.purchaseVoucherDate),
					"paymentVoucherNumber": this.validateValue(data.paymentVoucherNumber),
					"paymentDate": this.validateValue(data.paymentDate),
					"contractNumber": this.validateValue(data.contractNumber),
					"contractDate": this.validateValue(data.contractDate),
					"contractValue": this.validateValue(data.contractValue),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"udf5": this.validateValue(data.udf5),
					"udf6": this.validateValue(data.udf6),
					"udf7": this.validateValue(data.udf7),
					"udf8": this.validateValue(data.udf8),
					"udf9": this.validateValue(data.udf9),
					"udf10": this.validateValue(data.udf10),
					"udf11": this.validateValue(data.udf11),
					"udf12": this.validateValue(data.udf12),
					"udf13": this.validateValue(data.udf13),
					"udf14": this.validateValue(data.udf14),
					"udf15": this.validateValue(data.udf15),
					"location": this.validateValue(data.location),
					"profitCentre": this.validateValue(data.profitCentre),
					"plantCode": this.validateValue(data.plantCode)
				};
			}

			// if (this.byId("slInvDataType").getSelectedKey() === "outward") {
			// 	this._outwardItems(oItems, data);
			// } else {
			// 	this._inwardItems(oItems, data);
			// }
			return oItems;
		},

		_itemInwardJsonGet: function (data) { //eslint-disable-line
			if (this.inward240Flag) {
				var oItems = {
					"taxScheme": this.validateValue(data.taxScheme),
					"supplyType": this.validateValue(data.supplyType),
					"docCat": this.validateValue(data.docCat),
					"supStateCode": this.validateValue(data.supStateCode),
					"supPhone": this.validateValue(data.supPhone),
					"supEmail": this.validateValue(data.supEmail),
					"custTradeName": this.validateValue(data.custTradeName),
					"custLegalName": this.validateValue(data.custLegalName),
					"custBuildingNo": this.validateValue(data.custBuildingNo),
					"custBuildingName": this.validateValue(data.custBuildingName),
					"custLocation": this.validateValue(data.custLocation),
					"custPincode": this.validateValue(data.custPincode),
					"billToState": this.validateValue(data.billToState),
					"custPhone": this.validateValue(data.custPhone),
					"custEmail": this.validateValue(data.custEmail),
					"dispatcherTradeName": this.validateValue(data.dispatcherTradeName),
					"dispatcherBuildingNo": this.validateValue(data.dispatcherBuildingNo),
					"dispatcherBuildingName": this.validateValue(data.dispatcherBuildingName),
					"dispatcherLocation": this.validateValue(data.dispatcherLocation),
					"dispatcherPincode": this.validateValue(data.dispatcherPincode),
					"dispatcherStateCode": this.validateValue(data.dispatcherStateCode),
					"shipToTradeName": this.validateValue(data.shipToTradeName),
					"shipToLegalName": this.validateValue(data.shipToLegalName),
					"shipToBuildingNo": this.validateValue(data.shipToBuildingNo),
					"shipToBuildingName": this.validateValue(data.shipToBuildingName),
					"shipToLocation": this.validateValue(data.shipToLocation),
					"shipToPincode": this.validateValue(data.shipToPincode),
					"shipToState": this.validateValue(data.shipToState),
					"supTradeName": this.validateValue(data.supTradeName),
					"itemNo": this.validateValue(data.itemNo),
					"serialNoII": this.validateValue(data.serialNoII),
					"productName": this.validateValue(data.productName),
					"itemDesc": this.validateValue(data.itemDesc),
					"isService": this.validateValue(data.isService),
					"hsnsacCode": this.validateValue(data.hsnsacCode),
					"barcode": this.validateValue(data.barcode),
					"batchNameOrNo": this.validateValue(data.batchNameOrNo),
					"batchExpiryDate": this.validateValue(data.batchExpiryDate),
					"warrantyDate": this.validateValue(data.warrantyDate),
					"orderLineRef": this.validateValue(data.orderLineRef),
					"attributeName": this.validateValue(data.attributeName),
					"attributeValue": this.validateValue(data.attributeValue),
					"originCountry": this.validateValue(data.originCountry),
					"itemUqc": this.validateValue(data.itemUqc),
					"itemQty": this.validateValue(data.itemQty),
					"freeQuantity": this.validateValue(data.freeQuantity),
					"unitPrice": this.validateValue(data.unitPrice),
					"itemAmt": this.validateValue(data.itemAmt),
					"itemDiscount": this.validateValue(data.itemDiscount),
					"preTaxAmt": this.validateValue(data.preTaxAmt),
					"taxableVal": this.validateValue(data.taxableVal),
					"igstRt": this.validateValue(data.igstRt),
					"igstAmt": this.validateValue(data.igstAmt),
					"cgstRt": this.validateValue(data.cgstRt),
					"cgstAmt": this.validateValue(data.cgstAmt),
					"sgstRt": this.validateValue(data.sgstRt),
					"sgstAmt": this.validateValue(data.sgstAmt),
					"cessRtAdvalorem": this.validateValue(data.cessRtAdvalorem),
					"cessAmtAdvalorem": this.validateValue(data.cessAmtAdvalorem),
					"cessRtSpecific": this.validateValue(data.cessRtSpecific),
					"cessAmtSpecfic": this.validateValue(data.cessAmtSpecfic),
					"stateCessRt": this.validateValue(data.stateCessRt),
					"stateCessAmt": this.validateValue(data.stateCessAmt),
					"stateCessSpecificRt": this.validateValue(data.stateCessSpecificRt),
					"stateCessSpecificAmt": this.validateValue(data.stateCessSpecificAmt),
					"otherValues": this.validateValue(data.otherValues),
					"totalItemAmt": this.validateValue(data.totalItemAmt),
					"lineItemAmt": this.validateValue(data.lineItemAmt),
					// "roundOff": data.roundOff || null,
					"purchaseVoucherNum": this.validateValue(data.purchaseVoucherNum),
					"totalInvValueInWords": this.validateValue(data.totalInvValueInWords),
					"eligibilityIndicator": this.validateValue(data.eligibilityIndicator),
					"commonSupplyIndicator": this.validateValue(data.commonSupplyIndicator),
					"availableIgst": this.validateValue(data.availableIgst),
					"availableCgst": this.validateValue(data.availableCgst),
					"availableSgst": this.validateValue(data.availableSgst),
					"availableCess": this.validateValue(data.availableCess),
					"itcReversalIdentifier": this.validateValue(data.itcReversalIdentifier),
					"tcsFlagIncomeTax": this.validateValue(data.tcsFlagIncomeTax),
					"tcsRtIncomeTax": this.validateValue(data.tcsRtIncomeTax),
					"tcsAmtIncomeTax": this.validateValue(data.tcsAmtIncomeTax),
					"foreignCurrency": this.validateValue(data.foreignCurrency),
					"countryCode": this.validateValue(data.countryCode),
					"invValueFc": this.validateValue(data.invValueFc),
					"invRemarks": this.validateValue(data.invRemarks),
					"invPeriodStartDate": this.validateValue(data.invPeriodStartDate),
					"invPeriodEndDate": this.validateValue(data.invPeriodEndDate),
					"originalDocNo": this.validateValue(data.originalDocNo),
					"originalDocDate": this.validateValue(data.originalDocDate),
					"invRef": this.validateValue(data.invRef),
					"receiptAdviceRef": this.validateValue(data.receiptAdviceRef),
					"receiptAdviceDate": this.validateValue(data.receiptAdviceDate),
					"tenderRef": this.validateValue(data.tenderRef),
					"contractRef": this.validateValue(data.contractRef),
					"externalRef": this.validateValue(data.externalRef),
					"projectRef": this.validateValue(data.projectRef),
					"contractNumber": this.validateValue(data.contractNumber),
					"contractDate": this.validateValue(data.contractDate),
					"payeeName": this.validateValue(data.payeeName),
					"modeOfPayment": this.validateValue(data.modeOfPayment),
					"branchOrIfscCode": this.validateValue(data.branchOrIfscCode),
					"paymentTerms": this.validateValue(data.paymentTerms),
					"paymentInstruction": this.validateValue(data.paymentInstruction),
					"creditTransfer": this.validateValue(data.creditTransfer),
					"directDebit": this.validateValue(data.directDebit),
					"creditDays": this.validateValue(data.creditDays),
					"paidAmt": this.validateValue(data.paidAmt),
					"balanceAmt": this.validateValue(data.balanceAmt),
					"accDetail": this.validateValue(data.accDetail),
					"supportingDocURL": this.validateValue(data.supportingDocURL),
					"supportingDoc": this.validateValue(data.supportingDoc),
					"addlInfo": this.validateValue(data.addlInfo),
					"tranType": this.validateValue(data.tranType),
					"subsupplyType": this.validateValue(data.subsupplyType),
					"otherSupplyTypeDesc": this.validateValue(data.otherSupplyTypeDesc),
					"transporterID": this.validateValue(data.transporterID),
					"transporterName": this.validateValue(data.transporterName),
					"transportMode": this.validateValue(data.transportMode),
					"transportDocNo": this.validateValue(data.transportDocNo),
					"transportDocDate": this.validateValue(data.transportDocDate),
					"distance": this.validateValue(data.distance),
					"vehicleNo": this.validateValue(data.vehicleNo),
					"vehicleType": this.validateValue(data.vehicleType),
					"orgDocType": this.validateValue(data.orgDocType),
					"productCode": this.validateValue(data.productCode),
					"itemType": this.validateValue(data.itemType),
					"cifValue": this.validateValue(data.cifValue),
					"customDuty": this.validateValue(data.customDuty),
					"exchangeRt": this.validateValue(data.exchangeRt),
					"crDrReason": this.validateValue(data.crDrReason),
					"tcsIgstAmt": this.validateValue(data.tcsIgstAmt),
					"tcsCgstAmt": this.validateValue(data.tcsCgstAmt),
					"tcsSgstAmt": this.validateValue(data.tcsSgstAmt),
					"tdsIgstAmt": this.validateValue(data.tdsIgstAmt),
					"tdsCgstAmt": this.validateValue(data.tdsCgstAmt),
					"tdsSgstAmt": this.validateValue(data.tdsSgstAmt),
					"srcIdentifier": this.validateValue(data.srcIdentifier),
					"plantCode": this.validateValue(data.plantCode),
					"subDivision": this.validateValue(data.subDivision),
					"location": this.validateValue(data.location),
					"profitCentre": this.validateValue(data.profitCentre),
					"profitCentre2": this.validateValue(data.profitCentre2),
					"profitCentre3": this.validateValue(data.profitCentre3),
					"profitCentre4": this.validateValue(data.profitCentre4),
					"profitCentre5": this.validateValue(data.profitCentre5),
					"profitCentre6": this.validateValue(data.profitCentre6),
					"profitCentre7": this.validateValue(data.profitCentre7),
					"profitCentre8": this.validateValue(data.profitCentre8),
					"glCodeTaxableVal": this.validateValue(data.glCodeTaxableVal),
					"glCodeIgst": this.validateValue(data.glCodeIgst),
					"glCodeCgst": this.validateValue(data.glCodeCgst),
					"glCodeSgst": this.validateValue(data.glCodeSgst),
					"glCodeAdvCess": this.validateValue(data.glCodeAdvCess),
					"glCodeSpCess": this.validateValue(data.glCodeSpCess),
					"glCodeStateCess": this.validateValue(data.glCodeStateCess),
					"glStateCessSpecific": this.validateValue(data.glStateCessSpecific),
					"contractValue": this.validateValue(data.contractValue),
					"docRefNo": this.validateValue(data.docRefNo),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"udf5": this.validateValue(data.udf5),
					"udf6": this.validateValue(data.udf6),
					"udf7": this.validateValue(data.udf7),
					"udf8": this.validateValue(data.udf8),
					"udf9": this.validateValue(data.udf9),
					"udf10": this.validateValue(data.udf10),
					"udf11": this.validateValue(data.udf11),
					"udf12": this.validateValue(data.udf12),
					"udf13": this.validateValue(data.udf13),
					"udf14": this.validateValue(data.udf14),
					"udf15": this.validateValue(data.udf15),
					"udf16": this.validateValue(data.udf16),
					"udf17": this.validateValue(data.udf17),
					"udf18": this.validateValue(data.udf18),
					"udf19": this.validateValue(data.udf19),
					"udf20": this.validateValue(data.udf20),
					"udf21": this.validateValue(data.udf21),
					"udf22": this.validateValue(data.udf22),
					"udf23": this.validateValue(data.udf23),
					"udf24": this.validateValue(data.udf24),
					"udf25": this.validateValue(data.udf25),
					"udf26": this.validateValue(data.udf26),
					"udf27": this.validateValue(data.udf27),
					"udf29": this.validateValue(data.udf29),
					"udf30": this.validateValue(data.udf30)
				};
			} else {
				var oItems = {
					"itemNo": this.validateValue(data.itemNo),
					"glCodeTaxableVal": this.validateValue(data.glCodeTaxableVal),
					"glCodeIgst": this.validateValue(data.glCodeIgst),
					"glCodeCgst": this.validateValue(data.glCodeCgst),
					"glCodeSgst": this.validateValue(data.glCodeSgst),
					"glCodeAdvCess": this.validateValue(data.glCodeAdvCess),
					"glCodeSpCess": this.validateValue(data.glCodeSpCess),
					"glCodeStateCess": this.validateValue(data.glCodeStateCess),
					"supplyType": this.validateValue(data.supplyType),
					"originalDocNo": this.validateValue(data.originalDocNo),
					"originalDocDate": this.validateValue(data.originalDocDate),
					"cifValue": this.validateValue(data.cifValue),
					"customDuty": this.validateValue(data.customDuty),
					"hsnsacCode": this.validateValue(data.hsnsacCode),
					"productCode": this.validateValue(data.productCode),
					"itemDesc": this.validateValue(data.itemDesc),
					"itemType": this.validateValue(data.itemType),
					"itemUqc": this.validateValue(data.itemUqc),
					"itemQty": this.validateValue(data.itemQty),
					"taxableVal": this.validateValue(data.taxableVal),
					"igstRt": this.validateValue(data.igstRt),
					"igstAmt": this.validateValue(data.igstAmt),
					"cgstRt": this.validateValue(data.cgstRt),
					"cgstAmt": this.validateValue(data.cgstAmt),
					"sgstRt": this.validateValue(data.sgstRt),
					"sgstAmt": this.validateValue(data.sgstAmt),
					"cessRtAdvalorem": this.validateValue(data.cessRtAdvalorem),
					"cessAmtAdvalorem": this.validateValue(data.cessAmtAdvalorem),
					"cessRtSpecific": this.validateValue(data.cessRtSpecific),
					"cessAmtSpecfic": this.validateValue(data.cessAmtSpecfic),
					"stateCessRt": this.validateValue(data.stateCessRt),
					"stateCessAmt": this.validateValue(data.stateCessAmt),
					"otherValues": this.validateValue(data.otherValues),
					"lineItemAmt": this.validateValue(data.lineItemAmt),
					"adjustmentRefNo": this.validateValue(data.adjustmentRefNo),
					"adjustmentRefDate": this.validateValue(data.adjustmentRefDate),
					"adjustedTaxableValue": this.validateValue(data.adjustedTaxableValue),
					"adjustedIgstAmt": this.validateValue(data.adjustedIgstAmt),
					"adjustedCgstAmt": this.validateValue(data.adjustedCgstAmt),
					"adjustedSgstAmt": this.validateValue(data.adjustedSgstAmt),
					"adjustedCessAmtAdvalorem": this.validateValue(data.adjustedCessAmtAdvalorem),
					"adjustedCessAmtSpecific": this.validateValue(data.adjustedCessAmtSpecific),
					"adjustedStateCessAmt": this.validateValue(data.adjustedStateCessAmt),
					"eligibilityIndicator": this.validateValue(data.eligibilityIndicator),
					"commonSupplyIndicator": this.validateValue(data.commonSupplyIndicator),
					"availableIgst": this.validateValue(data.availableIgst),
					"availableCgst": this.validateValue(data.availableCgst),
					"availableSgst": this.validateValue(data.availableSgst),
					"availableCess": this.validateValue(data.availableCess),
					"itcReversalIdentifier": this.validateValue(data.itcReversalIdentifier),
					"crDrReason": this.validateValue(data.crDrReason),
					"purchaseVoucherNum": this.validateValue(data.purchaseVoucherNum),
					"purchaseVoucherDate": this.validateValue(data.purchaseVoucherDate),
					"paymentVoucherNumber": this.validateValue(data.paymentVoucherNumber),
					"paymentDate": this.validateValue(data.paymentDate),
					"contractNumber": this.validateValue(data.contractNumber),
					"contractDate": this.validateValue(data.contractDate),
					"contractValue": this.validateValue(data.contractValue),
					"udf1": this.validateValue(data.udf1),
					"udf2": this.validateValue(data.udf2),
					"udf3": this.validateValue(data.udf3),
					"udf4": this.validateValue(data.udf4),
					"udf5": this.validateValue(data.udf5),
					"udf6": this.validateValue(data.udf6),
					"udf7": this.validateValue(data.udf7),
					"udf8": this.validateValue(data.udf8),
					"udf9": this.validateValue(data.udf9),
					"udf10": this.validateValue(data.udf10),
					"udf11": this.validateValue(data.udf11),
					"udf12": this.validateValue(data.udf12),
					"udf13": this.validateValue(data.udf13),
					"udf14": this.validateValue(data.udf14),
					"udf15": this.validateValue(data.udf15),
					"location": this.validateValue(data.location),
					"profitCentre": this.validateValue(data.profitCentre),
					"plantCode": this.validateValue(data.plantCode)
				};
			}

			// if (this.byId("slInvDataType").getSelectedKey() === "outward") {
			// 	this._outwardItems(oItems, data);
			// } else {
			// 	this._inwardItems(oItems, data);
			// }
			return oItems;
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Payload Header object
		 * @param {Object} data response data objectb
		 */
		_outwardHeader: function (header, data) { //eslint-disable-line
			header.salesOrgnization = this.validateValue(data.salesOrgnization);
			header.distChannel = this.validateValue(data.distChannel);
			header.supplierGstin = this.validateValue(data.supplierGstin);
			header.originalDocType = this.validateValue(data.originalDocType);
			header.custGstin = this.validateValue(data.custGstin);
			header.origCgstin = this.validateValue(data.origCgstin);
			header.billToState = this.validateValue(data.billToState);
			header.shipToState = this.validateValue(data.shipToState);
			header.shippingBillNo = this.validateValue(data.shippingBillNo);
			header.shippingBillDate = this.validateValue(data.shippingBillDate);
			header.tcsFlag = this.validateValue(data.tcsFlag);
			header.ecomGSTIN = this.validateValue(data.ecomGSTIN);
			header.accountVoucherNo = this.validateValue(data.accountVoucherNo);
			header.accountVoucherDate = this.validateValue(data.accountVoucherDate);
			header.userDefinedField1 = this.validateValue(data.lineItems[0].userDefinedField1);
			header.userDefinedField2 = this.validateValue(data.lineItems[0].userDefinedField2);
			header.userDefinedField3 = this.validateValue(data.lineItems[0].userDefinedField3);
			header.userDefinedField4 = this.validateValue(data.lineItems[0].userDefinedField4);
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Payload Header object
		 * @param {Object} data response data objectb
		 */
		_inwardHeader: function (header, data) {
			header.purchaseOrganization = this.validateValue(data.purchaseOrganization);
			header.custGstin = this.validateValue(data.custGstin);
			header.supplierGstin = this.validateValue(data.supplierGstin);
			header.originalSupplierGstin = this.validateValue(data.originalSupplierGstin);
			header.billOfEntryNo = this.validateValue(data.billOfEntryNo);
			header.billOfEntryDate = this.validateValue(data.billOfEntryDate);
			header.itcEntitlement = this.validateValue(data.itcEntitlement);
			header.postingDate = this.validateValue(data.postingDate);
		},

		onShowMoreTables: function (oEvent, vId) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDistinctTableNo.do",
					contentType: "application/json",
					data: JSON.stringify({
						"id": vId
					})
				})
				.done(function (data, status, jqXHR) {
					var aData = data.resp.tableNumberList.map(function (e) {
						return {
							"tableNo": e
						};
					});
					if (!this._tableList) {
						this._tableList = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.TableList", this)
						this.getView().addDependent(this._tableList);
					}
					this._tableList.setModel(new JSONModel(aData), "TableList");
					this._tableList.openBy(oEvent.getSource());
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressPagination: function (oEvent) {
			var vInput = "inPageNoErr";
			var vBtnPrev = "bPrevErr";
			var vBtnNext = "bNextErr";
			var txtPageNo = "txtPageNoErr";
			var vValue = parseInt(this.byId(vInput).getValue(), 10);

			if (oEvent.getSource().getId().includes(vBtnPrev)) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId(vBtnPrev).setEnabled(false);
				}
			} else {
				vValue += 1;
				if (vValue > 1) {
					this.byId(vBtnPrev).setEnabled(true);
				}
			}
			this.byId(vInput).setValue(vValue);
			this.onPressGoFinal(vValue);
		},

		onPressInwardPagination: function (oEvent) {
			var vInput = "inIWPageNoErr";
			var vBtnPrev = "bIWPrevErr";
			var vBtnNext = "bIWNextErr";
			var txtPageNo = "txtIWPageNoErr";
			var vValue = parseInt(this.byId(vInput).getValue(), 10);

			if (oEvent.getSource().getId().includes(vBtnPrev)) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId(vBtnPrev).setEnabled(false);
				}
			} else {
				vValue += 1;
				if (vValue > 1) {
					this.byId(vBtnPrev).setEnabled(true);
				}
			}

			this.byId(vInput).setValue(vValue);
			this.onPressInwardGoFinal(vValue);
		},

		/**
		 * Called when user enter page number in input box and press enter or focus out of input box
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSubmitPagination: function () {
			//  //eslint-disable-line
			// if (this.byId("sbManage").getSelectedKey() === "process") {
			// 	var vPageNo = this.byId("inPageNo").getValue();
			// } else {
			// 	vPageNo = this.byId("inPageNoErr").getValue();
			// }
			// var oSearchInfo = this._getSearchInfo(this._getDataType(), vPageNo);

			// if (this.byId("dAdapt") && this.byId("sbInvType").getSelectedKey() === "asp") {
			// 	this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			// } else if (this.byId("sbInvType").getSelectedKey() === "gstn") {
			// 	if (this.byId("dGstnAdapt")) {
			// 		this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
			// 	}
			// }
			// this._getAspInvoices(oSearchInfo);
			var vPageNo = this.byId("inPageNoErr").getValue();
			this.onPressGoFinal(vPageNo);
		},

		onSubmitInwardPagination: function () {
			// if (this.byId("sbManage").getSelectedKey() === "process") {
			// 	var vPageNo = this.byId("inPageNo").getValue();
			// } else {
			// 	vPageNo = this.byId("inPageNoErr").getValue();
			// }
			// var oSearchInfo = this._getSearchInfo(this._getDataType(), vPageNo);

			// if (this.byId("dAdapt") && this.byId("sbInvType").getSelectedKey() === "asp") {
			// 	this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			// } else if (this.byId("sbInvType").getSelectedKey() === "gstn") {
			// 	if (this.byId("dGstnAdapt")) {
			// 		this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
			// 	}
			// }
			// this._getAspInvoices(oSearchInfo);
			var vPageNo = this.byId("inIWPageNoErr").getValue();
			this.onPressInwardGoFinal(vPageNo);
		},

		_getFilterPayload: function () {
			return {
				"hdr": {
					"pageNum": 0,
					"pageSize": 0
				},
				"req": {
					"criteria": "recDate",
					"receivFromDate": null,
					"receivToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docType": null,
					"docNo": null,
					"counterPartyGstin": null,
					"processingStatus": "B",
					"fileId": null,
					"dataOriginTypeCode": null,
					"ewbNo": null,
					"refId": null,
					"showGstnData": null,
					"gstReturnsStatus": null,
					"ewbStatus": null,
					"einvStatus": null,
					"ewbErrorPoint": null,
					"ewbCancellation": null,
					"subSupplyType": null,
					"supplyType": null,
					"transType": "O",
					"postingDate": null,
					"transporterID": null,
					"ewbValidUpto": null,
					"gstFlag": true,
					"reverseCharge": ["all", "Y", "N", "L"],
					"dataSecAttrs": {
						"GSTIN": [],
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"SO": [],
						"DC": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
		},

		/**
		 * Return Payload structure to get data from database
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @return {Object} Payload Structure object
		 */
		_getPayloadStruct: function () {
			return {
				"hdr": {
					"pageNum": 0,
					"pageSize": 0
				},
				"req": {
					"entityId": [],
					"receivFromDate": null,
					"receivToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docFromDate": null,
					"docToDate": null,
					"docType": null,
					"type": null,
					"docNo": null,
					"docNums": null,
					"counterPartyGstin": null,
					"processingStatus": null,
					"fileId": null,
					"dataOriginTypeCode": null,
					"ewbNo": null,
					"refId": null,
					"showGstnData": false,
					"gstReturnsStatus": [],
					"ewbStatus": [],
					"einvStatus": [],
					"ewbErrorPoint": [],
					"ewbCancellation": [],
					"subSupplyType": [],
					"supplyType": [],
					"transType": null,
					"postingDate": null,
					"transporterID": null,
					"ewbValidUpto": null,
					"reverseCharge": [],
					"dataSecAttrs": {
						"GSTIN": []
					},
					"flag": null,
					"irnNum": null
				}
			};
		},

		_getPayloadInwardStruct: function () {
			return {
				"hdr": {
					"pageNum": 0,
					"pageSize": 100
				},
				"req": {
					"entityId": [
						"27"
					],
					"receivFromDate": null,
					"receivToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docFromDate": null,
					"docToDate": null,
					"gstReturn": "",
					"docType": [],
					"docNo": null,
					"counterPartyGstin": null,
					"processingStatus": null,
					"fileId": null,
					"dataOriginTypeCode": null,
					"refId": null,
					"showGstnData": false,
					"gstReturnsStatus": [],
					"supplyType": [],
					"postingDate": null,
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
		},

		_getPayloadStructWP: function () {
			return {
				"req": {
					"entityId": [],
					"receivFromDate": null,
					"receivToDate": null,
					"returnFromDate": null,
					"returnToDate": null,
					"docType": null,
					"docNo": null,
					"returnType": null,
					"dataCategory": null,
					"tableNumber": null,
					"gstnStatus": null,
					"counterPartyGstin": null,
					"counterPartyFlag": null,
					"processingStatus": null,
					"fileId": null,
					"dataOriginTypeCode": null,
					"dateEACKFrom": null,
					"dateEACKTo": null,
					"refId": null, // Added by Bharat Gupta for GSTN api call on 09.12.2019
					"showGstnData": false, // Modified by Bharat Gupta for GSTN api implementation on 26.11.2019
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
		},

		onPressSyncGSTINAPI: function () {
			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var that = this;
			if (oSelectedData.length == 1) {
				var payload = {
					"req": {
						"gstin": oData.resp[oSelectedData[0]].gstin,
						"docHeaderId": JSON.stringify(oData.resp[oSelectedData[0]].id)
							// "syncgstin": oData.resp[oSelectedData[0]].counterPartyGstin,
					}
				};

				var oMassage = "Do you want to initiate Sync GSTIN API call for selected record?";
				MessageBox.show(oMassage, {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.onPressSyncGSTINAPIFinal(payload);
						}
					}
				});
			} else {
				MessageBox.information("Please select single document at a time to Sync GSTIN from GST Common Portal");
			}
		},

		onPressSyncGSTINAPIFinal: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSyncDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.length; i++) {
							if (data.resp[i].SyncGstin == undefined) {
								var SyncGstin = data.resp[i].response;
								var response = "";
							} else {
								var SyncGstin = data.resp[i].SyncGstin;
								var response = data.resp[i].response;
							}
							aMockMessages.push({
								type: 'Success',
								title: SyncGstin,
								gstin: response,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}

						that.getView().setModel(new JSONModel(aMockMessages), "Msg");
						that.onDialogPress123();
						// MessageBox.success("Sync GSTIN details from GST Common Portal is successful (GSTIN No. -" + data.resp.Gstin + ")");
					} else {
						MessageBox.error(data.resp.errorCode + " : " + data.resp.errorMessage);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getSyncDetails : Error");
				});
		},

		onDialogPress123: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Sync GSTIN details",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								// icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();

						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}

			this.pressDialog.open();
		},

		onPressEINVPrint: function () {
			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();

			if (oSelectedData.length > 0) {
				var payload = {
					"req": []
				};

				for (var i = 0; i < oSelectedData.length; i++) {
					if (oData.resp[oSelectedData[i]].irnResponse != undefined) {
						payload.req.push({
							"id": oData.resp[oSelectedData[i]].id,
							"docNo": oData.resp[oSelectedData[i]].docNo,
							"sgstin": oData.resp[oSelectedData[i]].gstin
						});
					}
				}

				if (payload.req.length == 0) {
					MessageBox.error("E-Invoice is not applicable on selected records, hence print cannot be downloaded");
				} else {
					var that = this;
					if (oSelectedData.length == payload.req.length) {
						var vUrl = "/aspsapapi/GoodsmultiplePdfReports.do";
						this.excelDownload(payload, vUrl);
					} else {
						var oMassage =
							"E-Invoice is not applicable on few of the selected records are, hence print for the same will not be downloaded. Do you want to proceed?";
						MessageBox.show(oMassage, {
							icon: MessageBox.Icon.INFORMATION,
							title: "Confirmation",
							actions: [MessageBox.Action.YES, MessageBox.Action.NO],
							onClose: function (oAction) {
								if (oAction === "YES") {
									var vUrl = "/aspsapapi/GoodsmultiplePdfReports.do";
									that.excelDownload(payload, vUrl);
								}
							}
						});
					}

				}
			} else {
				MessageBox.information("Selected at least one record");
			}
		},

		onPressB2CPrint: function () {
			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var docType = ["INV", "CR", "DR", "BOS"];
			var docType1 = ["INV", "CR", "DR"];
			var taxDocType = ["B2CS", "B2CL", "B2B", "CDNUR-B2CL", "CDNR", "NILEXTNON"];
			var supplyType = ["NIL", "NON", "EXT"];

			if (oSelectedData.length > 0) {
				var payload = {
					"req": []
				};

				for (var i = 0; i < oSelectedData.length; i++) {

					if (oData.resp[oSelectedData[i]].irnResponse == undefined && docType.includes(oData.resp[oSelectedData[i]].docType) && taxDocType
						.includes(oData.resp[oSelectedData[i]].taxDocType) && oData.resp[oSelectedData[i]].supplyType != "CAN") {
						payload.req.push({
							"id": oData.resp[oSelectedData[i]].id,
							"docNo": oData.resp[oSelectedData[i]].docNo,
							"sgstin": oData.resp[oSelectedData[i]].gstin
						});
					} else if (oData.resp[oSelectedData[i]].irnResponse == undefined && docType.includes(oData.resp[oSelectedData[i]].docType) &&
						oData.resp[oSelectedData[i]].supplyType == "CAN") {
						payload.req.push({
							"id": oData.resp[oSelectedData[i]].id,
							"docNo": oData.resp[oSelectedData[i]].docNo,
							"sgstin": oData.resp[oSelectedData[i]].gstin
						});
					} else if (oData.resp[oSelectedData[i]].irnResponse == undefined && (oData.resp[oSelectedData[i]].counterPartyGstin == undefined ||
							oData.resp[oSelectedData[i]].counterPartyGstin == "URP") && oData.resp[oSelectedData[i]].docType == "BOS" &&
						oData.resp[oSelectedData[i]].supplyType == "CAN") {
						payload.req.push({
							"id": oData.resp[oSelectedData[i]].id,
							"docNo": oData.resp[oSelectedData[i]].docNo,
							"sgstin": oData.resp[oSelectedData[i]].gstin
						});
					} else if (oData.resp[oSelectedData[i]].irnResponse == undefined && (oData.resp[oSelectedData[i]].counterPartyGstin == undefined ||
							oData.resp[oSelectedData[i]].counterPartyGstin == "URP") && docType.includes(oData.resp[oSelectedData[i]].docType) &&
						supplyType.includes(oData.resp[oSelectedData[i]].supplyType)) {
						payload.req.push({
							"id": oData.resp[oSelectedData[i]].id,
							"docNo": oData.resp[oSelectedData[i]].docNo,
							"sgstin": oData.resp[oSelectedData[i]].gstin
						});
					}

				}

				if (payload.req.length == 0) {
					MessageBox.error("Selected record is other than B2C. Please select appropriate record for B2C print");
				} else {
					var that = this;
					if (oSelectedData.length == payload.req.length) {
						var vUrl = "/aspsapapi/GoodsmultiplePdfReports.do";
						this.excelDownload(payload, vUrl);
					} else {
						var oMassage =
							"Few of selected records are other than B2C,  hence print for only B2C records will get downloaded. Do you want to proceed?";
						MessageBox.show(oMassage, {
							icon: MessageBox.Icon.INFORMATION,
							title: "Confirmation",
							actions: [MessageBox.Action.YES, MessageBox.Action.NO],
							onClose: function (oAction) {
								if (oAction === "YES") {
									var vUrl = "/aspsapapi/GoodsmultiplePdfReports.do";
									that.excelDownload(payload, vUrl);
								}
							}
						});
					}

				}
			} else {
				MessageBox.information("Selected at least one record");
			}
		},

		onPressPrintInvoice: function () {

			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();

			if (oSelectedData.length > 0) {
				// if (oSelectedData.length > 1) {
				// 	MessageBox.information("selected only one record");
				// } else {
				var payload = {
					"req": {
						"ewbNoList": []
					}
				};
				for (var i = 0; i < oSelectedData.length; i++) {

					this.oEWBNum = oData.resp[oSelectedData[i]].ewbNoresp;
					var oFlagStatus = false;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "4") {
						oFlagStatus = true;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "5") {
						oFlagStatus = true;
					} else {
						oFlagStatus = false;
					}

					if (this.oEWBNum == undefined) {
						// MessageBox.information("EWB Number not Available for selected record");
					} else if (oFlagStatus == false) {
						// MessageBox.information("EWB is not generated/ EWB is not Active for selected record");
					} else {
						payload.req.ewbNoList.push(this.oEWBNum);
						// var oReqExcelPath = "/aspsapapi/generateEWBDetailedReport.do?ewbNo=" + this.oEWBNum + "";
						// window.open(oReqExcelPath);
					}
				}
				// }
			} else {
				MessageBox.information("Selected at least one record");
			}

			if (payload.req.ewbNoList.length == 0) {
				MessageBox.information("EWB is not Available/not generated/ not Active for selected record");
			} else {
				if (payload.req.ewbNoList.length != oSelectedData.length) {
					MessageBox.information(
						"EWB not Active/Part A not Generated for some of the selected invoices hence those will not be downloaded.");
				}
				var vUrl = "/aspsapapi/ewbDetailedMultiplePDFReports.do";
				this.excelDownload(payload, vUrl);
			}

		},

		onPressEWBSummaryReport: function () {

			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();

			if (oSelectedData.length > 0) {
				// if (oSelectedData.length > 1) {
				// 	MessageBox.information("selected only one record");
				// } else {
				var payload = {
					"req": {
						"ewbNoList": []
					}
				};
				for (var i = 0; i < oSelectedData.length; i++) {

					this.oEWBNum = oData.resp[oSelectedData[i]].ewbNoresp;
					var oFlagStatus = false;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "4") {
						oFlagStatus = true;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "5") {
						oFlagStatus = true;
					} else {
						oFlagStatus = false;
					}

					if (this.oEWBNum == undefined) {
						// MessageBox.information("EWB Number not Available for selected record");
					} else if (oFlagStatus == false) {
						// MessageBox.information("EWB is not generated/ EWB is not Active for selected record");
					} else {
						payload.req.ewbNoList.push(this.oEWBNum);
						// var oReqExcelPath = "/aspsapapi/generateEWBDetailedReport.do?ewbNo=" + this.oEWBNum + "";
						// window.open(oReqExcelPath);
					}
				}
				// }
			} else {
				MessageBox.information("Selected at least one record");
			}

			if (payload.req.ewbNoList.length == 0) {
				MessageBox.information("EWB is not Available/not generated/ not Active for selected record");
			} else {
				if (payload.req.ewbNoList.length != oSelectedData.length) {
					MessageBox.information(
						"EWB not Active/Part A not Generated for some of the selected invoices hence those will not be downloaded.");
				}
				var vUrl = "/aspsapapi/ewbSummaryMultiplePdfReports.do";
				this.excelDownload(payload, vUrl);
			}

		},

		onPress: function (oEvent) {
			var key = oEvent.getSource().getKey();
			switch (key) {
			case "EINVGenerate":
				this.onPressEINVGeneral(false);
				break;
			case "EINVEWBGenerate":
				this.onPressEINVGeneral(true);
				break;
			case "EINVCancel":
				this.onPressEINVCancel();
				break;
			case "EWBGenerate":
				this.onPressEWBGeneral();
				break;
			case "EWBIRNGenerate":
				this.onPressEWBIRNGenerate();
				break;
			case "EWBCancel":
				this.onPressEWBCancel();
				break;
			case "EWBUpdate":
				this.onPressEWBUpdate("L");
				break;
			case "EWBTransporter":
				this.onPressTansUpdate();
				break;
			case "EWBExtend":
				this.onPressExtend();
				break;
			case "EINVPrint":
				this.onPressEINVPrint();
				break;
			case "B2CPrint":
				this.onPressB2CPrint();
				break;
			case "EWBDetailed":
				this.onPressPrintInvoice();
				break;
			case "EWBSummary":
				this.onPressEWBSummaryReport();
				break;
			case "EINVRP":
				this.onPressCloudDownload();
				break;
			case "IMR":
				this.onPressScreenOutwardDownload();
				break;
			case "SBD":
				var flag = true;
				this.onPressScreenOutwardDownload(flag);
				break;
			}
		},

		onPressUpdatePartB: function () {
			this.onPressEWBUpdate("T");
		},

		onComboBoxChange: function (oEvent) {
			var newval = oEvent.getParameter("newValue");
			var key = oEvent.getSource().getSelectedItem();

			if (newval !== "" && key === null) {
				oEvent.getSource().setValue("");
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
			}

		},

		onPressEINVGeneral: function (oflag) {
			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var searchInfo = {
				"req": {
					"docIdList": [],
					"isEWBReq": oflag
				}
			};
			if (oSelectedData.length > 0) {
				for (var i = 0; i < oSelectedData.length; i++) {

					var oFlagStatus = true;
					if (oflag) {
						if (oData.resp[oSelectedData[i]].irnStatus == "2") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].irnStatus == "5") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].irnStatus == "10") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "10") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "4") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "5") {
							oFlagStatus = false;
						} else {
							oFlagStatus = true;
						}
					} else {
						if (oData.resp[oSelectedData[i]].irnStatus == "2") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].irnStatus == "5") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].irnStatus == "10") {
							oFlagStatus = false;
						} else {
							oFlagStatus = true;
						}
					}

					if (oFlagStatus === true) {
						searchInfo.req.docIdList.push(oData.resp[oSelectedData[i]].id);
					}
				}

				if (searchInfo.req.docIdList.length === 0) {
					MessageBox.information("Not Applicable/Pending/Generated/EWB Active for selected records");
				} else {
					this.geteeInvoiceGenAction1(searchInfo, oflag);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}
		},
		geteeInvoiceGenAction1: function (searchInfo, oflag) {
			if (oflag) {
				var oMassage = "Records Available for E-Invoice and EWB generated will be considered.";
			} else {
				var oMassage = "Records Available for E-Invoice generated will be considered.";
			}
			var that = this;
			MessageBox.show(oMassage, {
				icon: MessageBox.Icon.INFORMATION,
				title: "Confirmation",
				actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
				onClose: function (oAction) {
					if (oAction === "OK") {
						that.geteeInvoiceGenAction2(searchInfo, oflag);
					}
				}
			});
		},

		geteeInvoiceGenAction2: function (searchInfo, oflag) {
			var that = this;
			if (oflag) {
				var oMassage = "Are you sure you want to generate E-Invoice and E-Way Bill for selected records?.";
			} else {
				var oMassage = "Are you sure you want to generate E-Invoice for selected records?.";
			}
			MessageBox.show(oMassage, {
				icon: MessageBox.Icon.INFORMATION,
				title: "Confirmation",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.geteeInvoiceGenAction(searchInfo);
					}
				}
			});
		},
		geteeInvoiceGenAction: function (searchInfo) {
			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/generateEinvoice.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length > 0) {
							if (data.resp[0].docId === undefined) {
								MessageBox.success(data.resp);
							} else {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: "Success",
										title: "Document Number - " + data.resp[i].docNo + " : " + data.resp[i].response,
										active: true,
										icon: "sap-icon://message-success"
											// highlight: "Success",
											// info: "Success"
									});
								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPressgenerateEinvoice();
							}
						}

					} else {
						MessageBox.error(data.resp);
					}
					var vPageNo = that.byId("inPageNoErr").getValue();
					that.onPressGoFinal(vPageNo);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("generateEinvoice : Error");
				});
		},

		onDialogPressgenerateEinvoice: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Generate Einvoice Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});

				//to get access to the global model
				this.getView().addDependent(this.pressDialog);
			}

			this.pressDialog.open();

		},

		onPressEWBGeneral: function (oEvent) {

			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			if (oSelectedData.length > 0) {

				var searchInfo = {
					"req": {
						"docIdList": []
					}
				};
				for (var i = 0; i < oSelectedData.length; i++) {

					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "10") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "4") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "5") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oFlagStatus === true) {
						searchInfo.req.docIdList.push(oData.resp[oSelectedData[i]].id);
					}
				}

				if (searchInfo.req.docIdList.length === 0) {
					MessageBox.information(
						"Not Applicable/Pending (Pushed to NIC)/Part-A Generated/EWB Active for selected records");
				} else {
					this.onPressEWBGeneral1(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		onPressEWBGeneral1: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Records Available for EWB generated will be considered.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					onClose: function (oAction) {
						if (oAction === "OK") {
							that.onPressEWBGeneral2(searchInfo);
						}
					}
				}
			);
		},

		onPressEWBGeneral2: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Are you sure you want to generate E-Way Bill for selected records?.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.getewbGenAction(searchInfo);
						}
					}
				}
			);
		},
		getewbGenAction: function (searchInfo) {
			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/generateEwayBill.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length > 0) {
							if (data.resp[0].docId === undefined) {
								MessageBox.success(data.resp);
							} else {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: "Success",
										title: "Document Number - " + data.resp[i].docNo + " : " + data.resp[i].response,
										active: true,
										icon: "sap-icon://message-success"
											// highlight: "Success",
											// info: "Success"
									});
								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPressgenerateEWB();
							}
						}

					} else {
						MessageBox.error(data.resp);
					}
					var vPageNo = that.byId("inPageNoErr").getValue();
					that.onPressGoFinal(vPageNo);

				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("generateEwayBill.do : Error");
				});
		},

		onDialogPressgenerateEWB: function () {
			var that = this;
			if (!this.pressDialogGenerateEWB) {
				this.pressDialogGenerateEWB = new Dialog({
					title: "Generate EWB Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialogGenerateEWB.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});

				//to get access to the global model
				this.getView().addDependent(this.pressDialogGenerateEWB);
			}

			this.pressDialogGenerateEWB.open();

		},
		onPressEWBIRNGenerate: function (oEvent) {

			//eslint-disable-line
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			if (oSelectedData.length > 0) {

				var searchInfo = {
					"req": {
						"docIdList": []
					}
				};
				for (var i = 0; i < oSelectedData.length; i++) {

					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].irnResponse == "" || oData.resp[oSelectedData[i]].irnResponse == undefined) {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBSubSupplyType == "EXP") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oFlagStatus === true) {
						searchInfo.req.docIdList.push(oData.resp[oSelectedData[i]].id);
					}
				}

				if (searchInfo.req.docIdList.length === 0) {
					MessageBox.information("INR number not available or sub-supply type EXP");
				} else {
					this.onPressEWBIRNGenerate1(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		onPressEWBIRNGenerate1: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Records Available for EWB generated by IRN will be considered.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					onClose: function (oAction) {
						if (oAction === "OK") {
							that.onPressEWBIRNGeneral2(searchInfo);
						}
					}
				}
			);
		},

		onPressEWBIRNGeneral2: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Are you sure you want to generate E-Way Bill by IRN for selected records?.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.getewbIRNGenAction(searchInfo);
						}
					}
				}
			);
		},
		getewbIRNGenAction: function (searchInfo) {
			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/generateEWBByIrn.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length > 0) {
							if (data.resp[0].docId === undefined) {
								MessageBox.success(data.resp);
							} else {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: "Success",
										title: "Document Number - " + data.resp[i].docNo + " : " + data.resp[i].response,
										active: true,
										icon: "sap-icon://message-success"
											// highlight: "Success",
											// info: "Success"
									});
								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPressgenerateEWBIRN();
							}
						}

					} else {
						MessageBox.error(data.resp);
					}
					var vPageNo = that.byId("inPageNoErr").getValue();
					that.onPressGoFinal(vPageNo);

				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("generateEwayBill.do : Error");
				});
		},

		onDialogPressgenerateEWBIRN: function () {
			var that = this;
			if (!this.pressDialogGenerateEWBIRN) {
				this.pressDialogGenerateEWBIRN = new Dialog({
					title: "Generate EWB Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialogGenerateEWBIRN.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});

				//to get access to the global model
				this.getView().addDependent(this.pressDialogGenerateEWBIRN);
			}

			this.pressDialogGenerateEWBIRN.open();

		},

		onPressEINVCancel: function (oEvent) {

			//  //eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var searchInfo = {
				"req": []
			};
			if (oSelectedData.length > 0) {
				this.onPressEINVCancel1();
			} else {
				MessageBox.information("Please select at least one records");
			}

		},
		onPressEINVCancel1: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Records Available for E-Invoice Cancellation will be considered.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					onClose: function (oAction) {
						if (oAction === "OK") {
							that.onPressEINVCancel2(searchInfo);
						}
					}
				}
			);
		},

		onPressEINVCancel2: function (searchInfo) {
			if (!this._oCancelEINV) {
				this._oCancelEINV = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.CancelEINV", this);
				this.getView().addDependent(this._oCancelEINV);
			}
			this._oCancelEINV.open();
		},

		onPressEINVCancelClose: function (oEvent) {
			this._oCancelEINV.close();
			if (oEvent.getSource().getId().includes("bEINVApply")) {
				this.cancelEINVCancel();
			}
		},

		cancelEINVCancel: function () {

			//eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var searchInfo = {
				"req": []
			};

			if (oSelectedData.length === 1) {
				asyncFlag = false;
			} else if (oSelectedData.length > 1) {
				asyncFlag = true;
			}

			if (oSelectedData.length > 0) {
				if (this.byId("idEINVReason").getSelectedKey() == "") {
					MessageBox.information("Please selecte reason code");
					return;
				} else if (this.byId("idEINVReason").getSelectedKey() == "4") {
					if (this.byId("idEINVRemarks").getValue() == "") {
						MessageBox.information("Please enter remarks");
						return;
					}
				}

				for (var i = 0; i < oSelectedData.length; i++) {
					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].einvStatus == "2") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].einvStatus == "6") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oData.resp[oSelectedData[i]].irnResponse != undefined && oFlagStatus === true) {

						searchInfo.req.push({
							"gstin": oData.resp[oSelectedData[i]].gstin,
							"irn": oData.resp[oSelectedData[i]].irnResponse,
							"cnlRsn": this.byId("idEINVReason").getSelectedKey(),
							"cnlRem": this.byId("idEINVRemarks").getValue(),
							"docHeaderId": oData.resp[oSelectedData[i]].id

						});
					}

				}
				if (searchInfo.req.length === 0) {
					MessageBox.information("IRN Number is not available / Not Applicable / Cancelled for selected records");
				} else {
					this.cancelEINVFinal(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		cancelEINVFinal: function (searchInfo) {

			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/cancelEinvoice.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length > 0) {
							if (data.resp[0].docId === undefined) {
								MessageBox.success(data.resp);
							} else {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: "Success",
										title: "Document ID - " + data.resp[i].docId + " : " + data.resp[i].response,
										active: true,
										icon: "sap-icon://message-success"
											// highlight: "Success",
											// info: "Success"
									});
								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPresscancelEinvoice();
							}
						}

					} else {
						MessageBox.error(data.resp);
					}
					var vPageNo = that.byId("inPageNoErr").getValue();
					that.onPressGoFinal(vPageNo);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("cancelEinvoice.do : Error");
				});
		},

		onDialogPresscancelEinvoice: function () {
			var that = this;
			if (!this.pressDialogcancelEinvoice) {
				this.pressDialogcancelEinvoice = new Dialog({
					title: "Cancel Einvoice Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialogcancelEinvoice.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});

				//to get access to the global model
				this.getView().addDependent(this.pressDialogcancelEinvoice);
			}

			this.pressDialogcancelEinvoice.open();

		},

		onPressEWBCancel: function (oEvent) {

			//  //eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var searchInfo = {
				"req": []
			};
			if (oSelectedData.length > 0) {
				var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
				for (var i = 0; i < oSelectedData.length; i++) {
					searchInfo.req.push({
						"gstin": "",
						"docId": oData.resp[oSelectedData[i]].id
					});
				}
				this.onPressEWBCancel1(searchInfo);
			} else {
				MessageBox.information("Please select at least one records");
			}

		},
		onPressEWBCancel1: function (searchInfo) {
			var that = this;
			MessageBox.show(
				"Records Available for EWB Cancellation will be considered.", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Confirmation",
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					onClose: function (oAction) {
						if (oAction === "OK") {
							that.onPressEWBCancel2(searchInfo);
						}
					}
				}
			);
		},

		onPressEWBCancel2: function (searchInfo) {
			if (!this._oCancel) {
				this._oCancel = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.CancelEWB", this);
				this.getView().addDependent(this._oCancel);
			}
			this._oCancel.open();
		},

		onPressCancelClose: function (oEvent) {
			this._oCancel.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				this.cancelEwayBill();
			}
		},

		cancelEwayBill: function () {

			//eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var searchInfo = {
				"req": []
			};

			if (oSelectedData.length === 1) {
				asyncFlag = false;
			} else if (oSelectedData.length > 1) {
				asyncFlag = true;
			}

			if (oSelectedData.length > 0) {
				if (this.byId("idEWBReason").getSelectedKey() == "") {
					MessageBox.information("Please selecte reason code");
					return;
				} else if (this.byId("idEWBReason").getSelectedKey() == "4") {
					if (this.byId("idEWBRemarks").getValue() == "") {
						MessageBox.information("Please enter remarks");
						return;
					}
				}

				for (var i = 0; i < oSelectedData.length; i++) {
					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "6") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oData.resp[oSelectedData[i]].ewbNoresp != undefined && oFlagStatus === true) {

						searchInfo.req.push({
							"docHeaderId": oData.resp[oSelectedData[i]].id,
							"gstin": oData.resp[oSelectedData[i]].gstin,
							"ewbNo": oData.resp[oSelectedData[i]].ewbNoresp,
							"cancelRsnCode": this.byId("idEWBReason").getSelectedKey(),
							"cancelRmrk": this.byId("idEWBRemarks").getValue()

						});
					}

				}
				if (searchInfo.req.length === 0) {
					MessageBox.information("EWB Number is not available / Not Applicable / Cancelled for selected records");
				} else {
					this.cancelEwayBillFinal(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		cancelEwayBillFinal: function (searchInfo) {

			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/cancelEwayBill.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length > 0) {
							if (data.resp[0].docId === undefined) {
								MessageBox.success(data.resp);
							} else {
								for (var i = 0; i < data.resp.length; i++) {
									aMockMessages.push({
										type: "Success",
										title: "Document ID - " + data.resp[i].docId + " : " + data.resp[i].response,
										active: true,
										icon: "sap-icon://message-success"
											// highlight: "Success",
											// info: "Success"
									});
								}
								that.getView().setModel(new JSONModel(aMockMessages), "Msg");
								that.onDialogPresscancelEWB();
							}

						}

					} else {
						MessageBox.error(data.resp);
					}
					that.onClear("C");
					var vPageNo = that.byId("inPageNoErr").getValue();
					that.onPressGoFinal(vPageNo);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("cancelEwayBill.do : Error");
				});
		},

		onDialogPresscancelEWB: function () {
			var that = this;
			if (!this.pressDialogcancelEWB) {
				this.pressDialogcancelEWB = new Dialog({
					title: "Cancel EWB Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialogcancelEWB.close();
							that.onPressGoFinal(1);
						}.bind(this)
					})
				});

				//to get access to the global model
				this.getView().addDependent(this.pressDialogcancelEWB);
			}

			this.pressDialogcancelEWB.open();

		},

		getPartBDetails: function (searchInfo) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getPartBDetails.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data), "PartBDetails");
						that.byId("bUpdateApply").setEnabled(true);
					} else {
						MessageBox.error(data.resp);
						that.getView().setModel(new JSONModel(null), "PartBDetails");
						that.byId("bUpdateApply").setEnabled(false);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getPartBDetails.do : Error");
					that.getView().setModel(new JSONModel(null), "PartBDetails");
				});
		},

		onPressEWBUpdate: function (flag) {
			this.updateFlag = flag;
			if (flag == "L") {
				var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();

				if (oSelectedData.length > 0) {
					var searchInfo = {
						"req": {
							"ewbNo": []
						}
					};
					var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
					for (var i = 0; i < oSelectedData.length; i++) {
						if (oData.resp[oSelectedData[i]].ewbNoresp != undefined) {
							searchInfo.req.ewbNo.push(oData.resp[oSelectedData[i]].ewbNoresp);
						}
					}
					if (searchInfo.req.ewbNo.length === 0) {
						MessageBox.information("EWB Number is not available for selected records");
						return;
					}
					this.getPartBDetails(searchInfo);
					this.onPressEWBUpdate1();
				} else {
					MessageBox.information("Please select at least one records");
				}
			} else {

				var oData = this.getView().getModel("InvoiceItemModel").getData();
				if (oData.ewbNoresp == undefined) {
					MessageBox.information("EWB Number is not available");
					return;
				} else {
					var searchInfo = {
						"req": {
							"ewbNo": [oData.ewbNoresp]
						}
					};
				}

				this.getPartBDetails(searchInfo);
				this.onPressEWBUpdate1();
				// } else {
				// 	MessageBox.information("Please select at least one records");
				// }

			}

		},

		onPressEWBUpdate1: function (searchInfo) {
			if (!this._oUpdate) {
				this._oUpdate = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.UpdateEWB", this);
				this.getView().addDependent(this._oUpdate);
			}
			this._oUpdate.open();
		},

		onPressUpdateClose: function (oEvent) {
			this._oUpdate.close();
			if (oEvent.getSource().getId().includes("bUpdateApply")) {
				this.updateEwayBill();
			}
		},

		onUPSelectionChange: function (oEvent) {
			var ReasonCode = this.byId("idUPReason").getSelectedKey();
			if (ReasonCode == "3") {
				this.byId("idUPHeaderRemark").setVisible(true);
			} else {
				this.byId("idUPHeaderRemark").setVisible(false);
			}
		},

		onAMVSelectionChange: function (oEvent) {
			var ReasonCode = this.byId("idAMVReason").getSelectedKey();
			if (ReasonCode == "3") {
				this.byId("idAMVHeaderRemark").setVisible(true);
			} else {
				this.byId("idAMVHeaderRemark").setVisible(false);
			}
		},

		updateEwayBill: function () {

			//eslint-disable-line
			if (this.updateFlag == "L") {
				var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
				var oData = this.byId("idtableInvNew").getModel("invOutward").getData();

				var searchInfo = {
					"req": []
				};
				if (oSelectedData.length > 0) {
					var vTransMode, vehicleType, reasonRem;
					var vTransportMode = this.byId("idUPTransportMode").getSelectedIndex();
					switch (vTransportMode) {
					case 0:
						vTransMode = "ROAD";
						break;
					case 1:
						vTransMode = "RAIL";
						break;
					case 2:
						vTransMode = "AIR";
						break;
					case 3:
						vTransMode = "SHIP";
						break;
					}

					var vVehicleType = this.byId("idUPVehicleType").getSelectedIndex();
					switch (vVehicleType) {
					case 0:
						vehicleType = "R";
						break;
					case 1:
						vehicleType = "O";
						break;

					}
					var vTransDocDate = this.byId("idUPTranDocDate").getValue();
					if (vTransDocDate == "") {
						vTransDocDate = null;
					}
					var ReasonCode = this.byId("idUPReason").getSelectedKey();
					if (ReasonCode == "3") {
						if (this.byId("idUPRemarks").getValue() == "") {
							MessageBox.information("Please enter remarks");
							return;
						} else {
							reasonRem = this.byId("idUPRemarks").getValue();
						}

					} else {
						reasonRem = "";
					}

					for (var i = 0; i < oSelectedData.length; i++) {
						var oFlagStatus = true;
						if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
							oFlagStatus = false;
						} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "6") {
							oFlagStatus = false;
						} else {
							oFlagStatus = true;
						}

						if (oData.resp[oSelectedData[i]].ewbNoresp != undefined && oFlagStatus === true) {

							searchInfo.req.push({
								"docHeaderId": oData.resp[oSelectedData[i]].id,
								"gstin": oData.resp[oSelectedData[i]].gstin,
								"ewbNo": oData.resp[oSelectedData[i]].ewbNoresp,
								"vehicleNo": this.byId("idUPVehicleNumber").getValue(),
								"fromPlace": this.byId("idUPFromPlace").getValue(),
								"fromState": this.byId("idUPFromState").getSelectedKey(),
								"reasonCode": ReasonCode,
								"reasonRem": reasonRem,
								"transDocNo": this.byId("idUPTranDocNo").getValue(),
								"transDocDate": vTransDocDate,
								"transMode": vTransMode,
								"vehicleType": vehicleType
							});
						}
					}

					if (searchInfo.req.length === 0) {
						MessageBox.information("EWB Number is not available / Not Applicable /  Cancelled for selected records");
					} else {
						this.updatePartBEwayBillFinal(searchInfo);
					}

				} else {
					MessageBox.information("Please select at least one records");
				}

			} else {

				// var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
				var oData = this.getView().getModel("InvoiceItemModel").getData();

				var searchInfo = {
					"req": []
				};
				// if (oSelectedData.length > 0) {
				var vTransMode, vehicleType, reasonRem;
				var vTransportMode = this.byId("idUPTransportMode").getSelectedIndex();
				switch (vTransportMode) {
				case 0:
					vTransMode = "ROAD";
					break;
				case 1:
					vTransMode = "RAIL";
					break;
				case 2:
					vTransMode = "AIR";
					break;
				case 3:
					vTransMode = "SHIP";
					break;
				}

				var vVehicleType = this.byId("idUPVehicleType").getSelectedIndex();
				switch (vVehicleType) {
				case 0:
					vehicleType = "R";
					break;
				case 1:
					vehicleType = "O";
					break;

				}
				var vTransDocDate = this.byId("idUPTranDocDate").getValue();
				if (vTransDocDate == "") {
					vTransDocDate = null;
				}
				var ReasonCode = this.byId("idUPReason").getSelectedKey();
				if (ReasonCode == "3") {
					if (this.byId("idUPRemarks").getValue() == "") {
						MessageBox.information("Please enter remarks");
						return;
					} else {
						reasonRem = this.byId("idUPRemarks").getValue();
					}

				} else {
					reasonRem = "";
				}

				// for (var i = 0; i < oSelectedData.length; i++) {
				var oFlagStatus = true;
				if (oData.ewbStatus == "1") {
					oFlagStatus = false;
				} else if (oData.ewbStatus == "6") {
					oFlagStatus = false;
				} else {
					oFlagStatus = true;
				}

				if (oData.ewbNoresp != undefined && oFlagStatus === true) {
					// if (oData.resp[oSelectedData[i]].ewbNoresp != undefined && oData.resp[oSelectedData[i]].EWBPartAStatus != "6") {
					if (oData.tranType == "O") {
						var vGSTIN = oData.suppGstin;
					} else {
						var vGSTIN = oData.custGstin;
					}

					searchInfo.req.push({
						"docHeaderId": oData.id,
						"gstin": vGSTIN,
						"ewbNo": oData.ewbNoresp,
						"vehicleNo": this.byId("idUPVehicleNumber").getValue(),
						"fromPlace": this.byId("idUPFromPlace").getValue(),
						"fromState": this.byId("idUPFromState").getSelectedKey(),
						"reasonCode": ReasonCode,
						"reasonRem": reasonRem,
						"transDocNo": this.byId("idUPTranDocNo").getValue(),
						"transDocDate": vTransDocDate,
						"transMode": vTransMode,
						"vehicleType": vehicleType
					});
				}
				// }

				if (searchInfo.req.length === 0) {
					MessageBox.information("EWB Number is not available / Not Applicable /  Cancelled for selected records");
				} else {
					this.updatePartBEwayBillFinal(searchInfo);
				}

				// } else {
				// 	MessageBox.information("Please select at least one records");
				// }

			}

		},

		updatePartBEwayBillFinal: function (searchInfo) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/updatePartBEwayBill.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length >= 5) {
							MessageBox.success(data.resp);
							that.onClear("U");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
							that.getPartBHistoryDetails();
						} else {
							for (var i = 0; i < data.resp.length; i++) {
								aMockMessages.push({
									type: 'Success',
									title: data.resp[i].docId,
									response: data.resp[i].response,
									active: true,
									icon: "sap-icon://message-success",
									highlight: "Success",
									info: "Success"
								});
							}
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress("Updated Part-B");

							that.onClear("U");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
							that.getPartBHistoryDetails();
						}
					} else {
						MessageBox.error(data.resp);
						var vPageNo = that.byId("inPageNoErr").getValue();
						that.onPressGoFinal(vPageNo);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("updatePartBEwayBill.do : Error");
				});
		},

		onDialogPress: function (text) {
			var that = this;
			//if (!this.pressDialog) {
			this.pressDialog = new Dialog({
				title: text,
				content: new sap.m.List({
					items: {
						path: "Msg>/",
						template: new sap.m.StandardListItem({
							title: "{Msg>title}",
							description: "{Msg>response}",
							icon: "{Msg>icon}",
							highlight: "{Msg>highlight}",
							// info: "{Msg>info}",
							// infoState: "{Msg>highlight}",
							infoStateInverted: true
						})
					}
				}),
				endButton: new Button({
					text: "Close",
					press: function () {
						this.pressDialog.close();
						// if (this.vPSFlag === "P") {
						// 	that._getProcessedData();
						// } else {
						// 	that._getSummaryData();
						// }
					}.bind(this)
				})
			});
			this.getView().addDependent(this.pressDialog);
			//	}
			this.pressDialog.open();
		},

		onPressTansUpdate: function (oEvent) {
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			if (oSelectedData.length > 0) {
				this.onPressTansUpdate1();
			} else {
				MessageBox.information("Please select at least one records");
			}
		},

		onPressTansUpdate1: function (searchInfo) {
			if (!this.oTransporter) {
				this.oTransporter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.TransporterEWB", this);
				this.getView().addDependent(this.oTransporter);
			}
			this.oTransporter.open();
		},

		onPressTransporterClose: function (oEvent) {
			this.oTransporter.close();
			if (oEvent.getSource().getId().includes("bApplyTran")) {
				this.TransporterEwayBill();
			}
		},

		TransporterEwayBill: function () {

			//eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();
			var vTransporterID = this.byId("idEWBTransporterID").getValue();
			if (vTransporterID == "") {
				MessageBox.information("Enter Transporter ID");
				return;
			}
			var searchInfo = {
				"req": []
			};
			if (oSelectedData.length > 0) {
				for (var i = 0; i < oSelectedData.length; i++) {
					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "6") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oData.resp[oSelectedData[i]].ewbNoresp != undefined && oFlagStatus === true) {

						searchInfo.req.push({
							"docHeaderId": oData.resp[oSelectedData[i]].id,
							"gstin": oData.resp[oSelectedData[i]].gstin,
							"ewbNo": oData.resp[oSelectedData[i]].ewbNoresp,
							"transporterId": vTransporterID
						});
					}
				}

				if (searchInfo.req.length === 0) {
					MessageBox.information("EWB Number is not available / Not Applicable / Cancelled for selected records");
				} else {
					this.updateEwbTransporterFinal(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		updateEwbTransporterFinal: function (searchInfo) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/updateEwbTransporter.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.length < 5) {
							for (var i = 0; i < data.resp.length; i++) {
								aMockMessages.push({
									type: 'Success',
									title: data.resp[i].docId,
									response: data.resp[i].response,
									active: true,
									icon: "sap-icon://message-success",
									highlight: "Success",
									info: "Success"
								});
							}
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress("Update Transporter");
							that.onClear("T");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
						} else {
							MessageBox.success(data.resp);
							that.onClear("T");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
						}
					} else {
						MessageBox.error(data.resp);
						var vPageNo = that.byId("inPageNoErr").getValue();
						that.onPressGoFinal(vPageNo);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("updateEwbTransporter.do : Error");
				});
		},

		onPressExtend: function (oEvent) {
			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			if (oSelectedData.length > 0) {
				this.onPressExtend1();
			} else {
				MessageBox.information("Please select at least one records");
			}
		},

		onPressExtend1: function (searchInfo) {
			if (!this.oExtend) {
				this.oExtend = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.extendEWB", this);
				this.getView().addDependent(this.oExtend);
			}
			this.oExtend.open();
		},

		onPressExtendClose: function (oEvent) {
			this.oExtend.close();
			if (oEvent.getSource().getId().includes("bExtendApply")) {
				this.ExtendEwayBill();
			}
		},
		onExtendSelectionChange: function (oEvent) {
			var ReasonCode = this.byId("idExtendReason").getSelectedKey();
			if (ReasonCode == "99") {
				this.byId("idExtendHeaderRemark").setVisible(true);
			} else {
				this.byId("idExtendHeaderRemark").setVisible(false);
			}
		},

		onSelectReturnGroupExtend: function (oEvent) {
			var vTransportMode = this.byId("idExtendTransportMode").getSelectedIndex();
			if (vTransportMode == 4) {
				this.byId("idHDExtendAddressLine1").setVisible(true);
				this.byId("idHDExtendAddressLine2").setVisible(true);
				this.byId("idHDExtendAddressLine3").setVisible(true);
				this.byId("idHDExtendVehicleType").setVisible(true);
			} else {
				this.byId("idHDExtendAddressLine1").setVisible(false);
				this.byId("idHDExtendAddressLine2").setVisible(false);
				this.byId("idHDExtendAddressLine3").setVisible(false);
				this.byId("idHDExtendVehicleType").setVisible(false);
			}
		},

		ExtendEwayBill: function () {

			//eslint-disable-line

			var oSelectedData = this.byId("idtableInvNew").getSelectedIndices();
			var oData = this.byId("idtableInvNew").getModel("invOutward").getData();

			var searchInfo = {
				"req": []
			};
			if (oSelectedData.length > 0) {
				var vTransMode, transitType, reasonRem, AddLine1, AddLine2, AddLine3;
				var vTransportMode = this.byId("idExtendTransportMode").getSelectedIndex();
				switch (vTransportMode) {
				case 0:
					vTransMode = "ROAD";
					break;
				case 1:
					vTransMode = "RAIL";
					break;
				case 2:
					vTransMode = "AIR";
					break;
				case 3:
					vTransMode = "SHIP";
					break;
				case 4:
					vTransMode = "INTRANSIT";
					break;
				}

				if (vTransportMode == "4") {
					transitType = this.byId("idExtendVehicleType").getSelectedKey();
					AddLine1 = this.byId("idExtendAddressLine1").getValue();
					AddLine2 = this.byId("idExtendAddressLine2").getValue();
					AddLine3 = this.byId("idExtendAddressLine3").getValue();

				} else {
					transitType = "";
					AddLine1 = "";
					AddLine2 = "";
					AddLine3 = "";
				}

				var vTransDocDate = this.byId("idExtendTranDocDate").getValue();
				if (vTransDocDate == "") {
					vTransDocDate = null;
				}
				var ReasonCode = this.byId("idExtendReason").getSelectedKey();
				if (ReasonCode == "99") {
					if (this.byId("idExtendRemarks").getValue() == "") {
						MessageBox.information("Please enter remarks");
						return;
					} else {
						reasonRem = this.byId("idExtendRemarks").getValue();
					}
				} else {
					reasonRem = "";
				}

				for (var i = 0; i < oSelectedData.length; i++) {

					var oFlagStatus = true;
					if (oData.resp[oSelectedData[i]].EWBPartAStatus == "1") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "4") {
						oFlagStatus = false;
					} else if (oData.resp[oSelectedData[i]].EWBPartAStatus == "2") {
						oFlagStatus = false;
					} else {
						oFlagStatus = true;
					}

					if (oData.resp[oSelectedData[i]].ewbNoresp != undefined && oFlagStatus === true) {

						searchInfo.req.push({
							"docHeaderId": oData.resp[oSelectedData[i]].id,
							"gstin": oData.resp[oSelectedData[i]].gstin,
							"ewbNo": oData.resp[oSelectedData[i]].ewbNoresp,
							"vehicleNo": this.byId("idExtendVehicleNumber").getValue(),
							"fromPlace": this.byId("idExtendFromPlace").getValue(),
							"fromState": this.byId("idExtendFromState").getSelectedKey(),
							"remainingDistance": this.byId("idExtendRemDistance").getValue(),
							"transDocNo": this.byId("idExtendTranDocNo").getValue(),
							"transDocDate": vTransDocDate,
							"transMode": vTransMode,
							"extnRsnCode": ReasonCode,
							"extnRemarks": reasonRem,
							"fromPincode": this.byId("idExtendFromPincode").getValue(),
							"consignmentStatus": this.byId("idConsignment").getSelectedKey(),
							"transitType": transitType,
							"addressLine1": AddLine1,
							"addressLine2": AddLine2,
							"addressLine3": AddLine3
						});
					}
				}

				if (searchInfo.req.length === 0) {
					MessageBox.information("EWB Number is not available / Not Applicable / Cancelled / part-A generated for selected records");
				} else {
					this.extendEwbValidityFinal(searchInfo);
				}

			} else {
				MessageBox.information("Please select at least one records");
			}

		},

		extendEwbValidityFinal: function (searchInfo) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/extendEwbValidity.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.hdr.status === "S") {
						if (data.resp.length < 5) {
							for (var i = 0; i < data.resp.length; i++) {
								aMockMessages.push({
									type: 'Success',
									title: data.resp[i].docId,
									response: data.resp[i].response,
									active: true,
									icon: "sap-icon://message-success",
									highlight: "Success",
									info: "Success"
								});
							}
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress("Extend EWB Status");
							that.onClear("E");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
						} else {
							MessageBox.information(data.resp);
							that.onClear("E");
							var vPageNo = that.byId("inPageNoErr").getValue();
							that.onPressGoFinal(vPageNo);
						}
					} else {
						MessageBox.error(data.resp);
						var vPageNo = that.byId("inPageNoErr").getValue();
						that.onPressGoFinal(vPageNo);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("extendEwbValidity.do : Error");
				});
		},

		getcancelEinvoicen: function (searchInfo) {
			//eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: "/aspsapapi/cancelEinvoice.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("cancelEinvoice : Error");
				});
		},

		onPressEINVDownload: function (vPageNo) {
			//eslint-disable-line
			var vSelectedKey = this.getView().byId("slCriteria").getSelectedKey();
			var vReceivFromDate, vReceivToDate, vReturnFromDate, vReturnToDate, vDocNo, vDateEACKFrom, vDateEACKTo;
			vDateEACKFrom = this.getView().byId("idFromEACKDate").getValue();
			vDateEACKTo = this.getView().byId("idToEACKDate").getValue();
			if (vSelectedKey === "docDate") {
				vReceivFromDate = this.getView().byId("iFromDate").getValue();
				vReceivToDate = this.getView().byId("iToDate").getValue();
				vReturnFromDate = null;
				vReturnToDate = null;

			} else {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = this.getView().byId("iFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iToPeriod").getValue();
			}
			if (this.getView().byId("iDocNo").getValue() === "") {
				vDocNo = null;
			} else {
				vDocNo = this.getView().byId("iDocNo").getValue();
			}
			if (vDateEACKFrom === "") {
				vDateEACKFrom = null;
			}

			if (vDateEACKTo === "") {
				vDateEACKTo = null;
			}

			searchData = this._getPayloadStructWP();
			searchData.req.entityId = [$.sap.entityID];
			searchData.req.receivFromDate = vReceivFromDate;
			searchData.req.receivToDate = vReceivToDate;
			searchData.req.returnFromDate = vReturnFromDate;
			searchData.req.returnToDate = vReturnToDate;
			searchData.req.docNo = vDocNo;
			searchData.req.showGstnData = false;
			searchData.req.dateEACKFrom = vDateEACKFrom;
			searchData.req.dateEACKTo = vDateEACKTo;
			searchData.req.dataSecAttrs.GSTIN = this.getView().byId("idfgiInvMGSINT").getSelectedKeys();

			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(searchData.req.dataSecAttrs);
			}
			var url = "/aspsapapi/einvoiceScreenDownload.do";
			this.excelDownload(searchData, url);
		},

		onChangeTransType: function (oEvent) {

			if (oEvent == "") {
				var oKey = this.byId("slTransactionType").getSelectedKey();
			} else {
				var oKey = oEvent.getSource().getSelectedKey();
			}

			var oData = this.getOwnerComponent().getModel("filter").getData();
			if (oKey == "O") {
				oData.req.gstFlag = true;
			} else {
				oData.req.gstFlag = false;
			}
			this.getOwnerComponent().getModel("filter").refresh();
		},

		onChangeGSTReturn: function (oEvent) {
			//eslint-disable-line
			if (oEvent == "") {
				var oKey = this.getView().byId("idIWGSTReturn").getSelectedKey();
			} else {
				var oKey = oEvent.getSource().getSelectedKey();
			}

			if (oKey == "GSTR2") {
				this.getOwnerComponent().getModel("DropDown").getData().GSTReturnsStatus[3].enabled = false;
				this.getOwnerComponent().getModel("DropDown").getData().GSTReturnsStatus[4].enabled = false;

				var aRegGSTIN = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.gstin;
				this.getOwnerComponent().setModel(new JSONModel(aRegGSTIN), "InwardGSTIN");

			} else {
				this.getOwnerComponent().getModel("DropDown").getData().GSTReturnsStatus[3].enabled = true;
				this.getOwnerComponent().getModel("DropDown").getData().GSTReturnsStatus[4].enabled = true;

				var aISDGSTIN = this.getOwnerComponent().getModel("ISDGstin").getData();
				this.getOwnerComponent().setModel(new JSONModel(aISDGSTIN), "InwardGSTIN");
			}
			this.getOwnerComponent().getModel("DropDown").refresh();
		},
		onPressClear: function () {
			if (this.byId("sbNewManageOI").getSelectedKey() === "Outward") {
				this._clearOutwardFilter();
			} else {
				this._clearInwardwardFilter();
			}
		},
		_clearOutwardFilter: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iFromDate").setMaxDate(new Date());
			this.byId("iToDate").setDateValue(new Date());
			this.byId("iToDate").setMinDate(vDate);
			this.byId("iToDate").setMaxDate(new Date());

			this.byId("iFromPeriod").setDateValue(vDate);
			this.byId("iFromPeriod").setMaxDate(new Date());
			this.byId("iToPeriod").setDateValue(new Date());
			this.byId("iToPeriod").setMinDate(vDate);
			this.byId("iToPeriod").setMaxDate(new Date());

			this.getOwnerComponent().getModel("invManage").getData().DatePeriod.Period = false;

			this.byId("slCriteria").setSelectedKey("recDate");
			this.byId("slTransactionType").setSelectedKey("O");
			this.onChangeTransType("");

			this.byId("idfgiInvMGSINT").setSelectedKeys([]);
			this.byId("slCpAction").setSelectedKey(null);
			this.byId("slDocType").setSelectedKeys([]);
			//this.byId("iDocNo").setValue(null);
			this.byId("slGSTReturnsStatus").setSelectedKeys([]);
			this.byId("slEWBStatus").setSelectedKeys([]);
			this.byId("slEINVStatus").setSelectedKeys([]);
			this.byId("slEWBErrorPoint").setSelectedKeys([]);
			this.byId("idEWBNo").setValue(null);
			this.byId("idEWBDate").setValue(null);
			this.byId("iDocNo").setTokens([]);
			this.byId("idirnId").setValue(null);
			this.byId("slEWBCancellation").setSelectedKeys([]);
			this.byId("slSubSupplyType").setSelectedKeys([]);
			this.byId("slSupplyType").setSelectedKeys([]);
			this.byId("idPostingDate").setValue(null);
			this.byId("idTransporterID").setValue(null);
			this.byId("idStatus").setSelectedKey("B");

			if (this.byId("dOutAdapt")) {
				this.getView().byId("slOutProfitCtr").setSelectedKeys([]);
				this.getView().byId("slOutPlant").setSelectedKeys([]);
				this.getView().byId("slOutDivision").setSelectedKeys([]);
				this.getView().byId("slOutLocation").setSelectedKeys([]);
				this.getView().byId("slOutSalesOrg").setSelectedKeys([]);
				this.getView().byId("slOutPurcOrg").setSelectedKeys([]);
				this.getView().byId("slOutDistrChannel").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess1").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess2").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess3").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess4").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess5").setSelectedKeys([]);
				this.getView().byId("slOutuserAccess6").setSelectedKeys([]);
			}
			this.onPressGoFinal(1);

		},
		_clearInwardwardFilter: function () {

			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iIWFromDate").setDateValue(vDate);
			this.byId("iIWFromDate").setMaxDate(new Date());
			this.byId("iIWToDate").setDateValue(new Date());
			this.byId("iIWToDate").setMinDate(vDate);
			this.byId("iIWToDate").setMaxDate(new Date());

			this.byId("iIWFromPeriod").setDateValue(vDate);
			this.byId("iIWFromPeriod").setMaxDate(new Date());
			this.byId("iIWToPeriod").setDateValue(new Date());
			this.byId("iIWToPeriod").setMinDate(vDate);
			this.byId("iIWToPeriod").setMaxDate(new Date());

			this.getOwnerComponent().getModel("invManage").getData().DatePeriod.inPeriod = false;
			this.byId("slIWCriteria").setSelectedKey("recDate");

			this.byId("idIWGSTReturn").setSelectedKey("GSTR2");
			this.onChangeGSTReturn("");

			this.byId("idfgiInvMGSINT").setSelectedKeys([]);
			this.byId("slIWCpAction").setSelectedKey(null);

			this.byId("slIWDocType").setSelectedKeys([]);
			this.byId("idfgiIWInvMGSINT").setSelectedKeys([]);
			this.byId("idIWDocNo").setTokens([]);
			this.byId("slIWGSTReturnsStatus").setSelectedKeys([]);
			this.byId("slIWSupplyType").setSelectedKeys([]);
			this.byId("idIWPostingDate").setValue(null);
			this.byId("idSGNo").setValue(null);
			this.byId("idAVNNo").setTokens([]);

			if (this.byId("dAdapt")) {
				this.getView().byId("slProfitCtr").setSelectedKeys([]);
				this.getView().byId("slPlant").setSelectedKeys([]);
				this.getView().byId("slDivision").setSelectedKeys([]);
				this.getView().byId("slLocation").setSelectedKeys([]);
				// this.getView().byId("slSalesOrg").setSelectedKeys([]);
				this.getView().byId("slPurcOrg").setSelectedKeys([]);
				this.getView().byId("slDistrChannel").setSelectedKeys([]);
				this.getView().byId("sluserAccess1").setSelectedKeys([]);
				this.getView().byId("sluserAccess2").setSelectedKeys([]);
				this.getView().byId("sluserAccess3").setSelectedKeys([]);
				this.getView().byId("sluserAccess4").setSelectedKeys([]);
				this.getView().byId("sluserAccess5").setSelectedKeys([]);
				this.getView().byId("sluserAccess6").setSelectedKeys([]);
			}

			this.onPressInwardGoFinal(1);
		},

		onClear: function (oKey) {
			if (oKey == "C") {
				this.byId("idEWBReason").setSelectedKey(null);;
				this.byId("idEWBRemarks").setValue(null);
			} else if (oKey == "U") {
				this.byId("idUPTransportMode").setSelectedIndex(0);
				this.byId("idUPVehicleType").setSelectedIndex(0);
				this.byId("idUPVehicleNumber").setValue(null);
				this.byId("idUPFromPlace").setValue(null);
				this.byId("idUPFromState").setSelectedKey(null);
				this.byId("idUPTranDocNo").setValue(null);
				this.byId("idUPTranDocDate").setValue(null);
				this.byId("idUPReason").setSelectedKey(null);
				this.byId("idUPRemarks").setValue(null);
			} else if (oKey == "E") {
				this.byId("idExtendTransportMode").setSelectedIndex(0);
				this.byId("idExtendVehicleNumber").setValue(null);
				this.byId("idExtendFromPincode").setValue(null);
				this.byId("idExtendAddressLine1").setValue(null);
				this.byId("idExtendAddressLine2").setValue(null);
				this.byId("idExtendAddressLine3").setValue(null);
				this.byId("idExtendFromPlace").setValue(null);
				this.byId("idExtendFromState").setSelectedKey(null);
				this.byId("idExtendTranDocNo").setValue(null);
				this.byId("idExtendTranDocDate").setValue(null);
				this.byId("idExtendRemDistance").setValue(null);
				this.byId("idExtendReason").setSelectedKey(null);
				this.byId("idExtendRemarks").setValue(null);
				this.byId("idExtendVehicleType").setSelectedKey(null);
				this.byId("idConsignment").setSelectedKey(null);
			} else if (oKey == "T") {
				this.byId("idEWBTransporterID").setValue(null);
			}
		},
		_typeNumber: function (oEvent) {
			var value = oEvent.getSource().getValue();
			var bNotnumber = isNaN(value);
			if (bNotnumber === true) {
				oEvent.getSource().setValue(value.substring(0, value.length - 1));
			}
		},
		onPressEditFormLayout: function (oEvent) {
			this._setEditable(true);
		},
		onPressCancelFormLayout: function (oEvent) {
			this._setEditable(false);
		},

		_setEditable: function (oFlag) {
			//eslint-disable-line
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			oData.edit = oFlag;
			for (var i = 0; i < oData.lineItems.length; i++) {
				oData.lineItems[i].edit = oFlag;
			}
			this.getView().getModel("InvoiceItemModel").refresh();
		},

		onPressChangeMultiVehicle: function (flag, groupID) {
			//eslint-disable-line
			if (!this._oChangeMultiVehicle) {
				this._oChangeMultiVehicle = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.invManageNew.tabLayout.addMultiVehicle", this);
				this.getView().addDependent(this._oChangeMultiVehicle);
			}
			if (flag === "G") {
				this.groupID = groupID;
				this.getVehiclesForGroup(groupID);
			} else {
				this.bindVehiclesForButton();
				this.byId("idAMVHeaderRemark").setVisible(false);
				this._oChangeMultiVehicle.open();
			}
		},

		onPressAMVClose: function (oEvent) {
			if (oEvent.getSource().getId().includes("bAMVApply")) {
				this.updateMultiVehicle();
			} else {
				this._oChangeMultiVehicle.close();
			}
		},

		updateMultiVehicle: function (oEvent) {
			//eslint-disable-line
			var oDataMain = this.getView().getModel("InvoiceItemModel").getData(),
				oModelVehicle = this.getView().getModel("VehiclesForGroup"),
				oDataVehicle = oModelVehicle.getData(),
				vTransportMode = this.byId("idAMVTransportMode").getSelectedIndex(),
				vTransMode, flag = true;

			switch (vTransportMode) {
			case 0:
				vTransMode = "1";
				break;
			case 1:
				vTransMode = "2";
				break;
			case 2:
				vTransMode = "3";
				break;
			case 3:
				vTransMode = "4";
				break;
			}

			if (oDataVehicle.edit) {
				if (oDataVehicle.fromPlace == "") {
					MessageBox.information("From Place is required");
					return;
				} else if (oDataVehicle.fromState == "") {
					MessageBox.information("From State is required");
					return;
				} else if (oDataVehicle.toPlace == "") {
					MessageBox.information("To Place is required");
					return;
				} else if (oDataVehicle.toState == "") {
					MessageBox.information("To State is required");
					return;
				} else if (oDataVehicle.totalQty == "") {
					MessageBox.information("Total Qty is required");
					return;
				} else if (oDataVehicle.unitCode == "") {
					MessageBox.information("Unit Code is required");
					return;
				} else if (oDataVehicle.reasonCode == "") {
					MessageBox.information("Reason Code Code is required");
					return;
				} else if (oDataVehicle.reasonCode == "3" && oDataVehicle.reasonRem == "") {
					MessageBox.information("Reason Remark Code is required");
					return;
				}
				var payload = {
					"req": {
						"ewbNo": oDataMain.ewbNoresp,
						"reasonCode": oDataVehicle.reasonCode,
						"reasonRem": oDataVehicle.reasonRem,
						"fromPlace": oDataVehicle.fromPlace,
						"fromState": oDataVehicle.fromState,
						"toPlace": oDataVehicle.toPlace,
						"toState": oDataVehicle.toState,
						"transMode": vTransMode,
						"totalQuantity": oDataVehicle.totalQty,
						"unitCode": oDataVehicle.unitCode,
						"docNo": oDataMain.docNo,
						"docHeaderId": oDataMain.id,
						"gstin": oDataMain.tranType == "O" ? oDataMain.suppGstin : oDataMain.custGstin,
						"vehicleDetails": []
					}
				};
				for (var i = 0; i < oDataVehicle.vechicleDetails.length; i++) {
					flag = this.validateMultiVehicalDet(oDataVehicle.vechicleDetails[i], vTransportMode); // Added by: Bharat Gupta on 19.11.2020 for validation

					payload.req.vehicleDetails.push({
						"vehicleNo": oDataVehicle.vechicleDetails[i].vehicleNo,
						"transDocNo": oDataVehicle.vechicleDetails[i].transDocNo,
						"transDocDate": oDataVehicle.vechicleDetails[i].transDocDate,
						"quantity": oDataVehicle.vechicleDetails[i].quantity
					});
				}
				if (!flag) {
					MessageBox.information("Fill the required details");
					oModelVehicle.refresh(true);
					return;
				}
				var oURL = "/aspsapapi/initiateMultiVehicle.do";
				this.saveMultiVehicle(payload, oURL);

			} else {
				payload = {
					"req": []
				};
				for (i = 0; i < oDataVehicle.vechicleDetails.length; i++) {
					if (oDataVehicle.vechicleDetails[i].edit) {
						flag = this.validateMultiVehicalDet(oDataVehicle.vechicleDetails[i], vTransportMode); // Added by: Bharat Gupta on 20.11.2020 for validation

						payload.req.push({
							"ewbNo": oDataMain.ewbNoresp,
							"groupNo": this.groupID,
							"vehicleNo": oDataVehicle.vechicleDetails[i].vehicleNo,
							"transDocNo": oDataVehicle.vechicleDetails[i].transDocNo,
							"transDocDate": oDataVehicle.vechicleDetails[i].transDocDate,
							"quantity": oDataVehicle.vechicleDetails[i].quantity,
							"gstin": oDataMain.tranType == "O" ? oDataMain.suppGstin : oDataMain.custGstin
						});
					}
				}
				if (!flag) {
					MessageBox.information("Fill the required details");
					oModelVehicle.refresh(true);
					return;
				}
				oURL = "/aspsapapi/addMultiVehicle.do";
				this.saveMultiVehicle(payload, oURL);
			}
		},

		/**
		 * Method called to validated vehicle items details
		 * Developed by: Bharat Gupta - 19.11.2020
		 * @memberOf com.ey.digigst.view.InvManageNew
		 * @param {Object} vehicleDet Vehicle details
		 * @param {number} trnsMode Transport Mode
		 * @return {boolean} Flag value
		 */
		validateMultiVehicalDet: function (vehicleDet, trnsMode) {
			//  //eslint-disable-line
			var flag = true;
			if (trnsMode === 0 && !vehicleDet.vehicleNo) {
				vehicleDet.vehicleNoState = "Error";
				vehicleDet.transDocNoState = "None";
				vehicleDet.transDocDateState = "None";
				flag = false;
			}
			if (trnsMode !== 0 && !vehicleDet.transDocNo) {
				vehicleDet.transDocNoState = "Error";
				vehicleDet.vehicleNoState = "None";
				flag = false;
			}
			if (trnsMode !== 0 && !vehicleDet.transDocDate) {
				vehicleDet.transDocDateState = "Error";
				vehicleDet.vehicleNoState = "None";
				flag = false;
			}
			if (!vehicleDet.quantity) {
				vehicleDet.quantityState = "Error";
				flag = false;
			}
			return flag;
		},

		onChagneVehicleValue: function (oEvent) {
			oEvent.getSource().setValueState("None");
		},

		saveMultiVehicle: function (searchInfo, oURL) {
			//eslint-disable-line
			this._oChangeMultiVehicle.close();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var aMockMessages = [];
			$.ajax({
					method: "POST",
					url: oURL,
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.hdr.status === "S") {
						if (data.resp[0].vehicleNo == undefined) {
							MessageBox.success(data.resp);
						} else {

							for (var i = 0; i < data.resp.length; i++) {
								aMockMessages.push({
									type: "Success",
									title: "Vehicle No - " + data.resp[i].vehicleNo + " : " + data.resp[i].msg,
									active: true,
									icon: "sap-icon://message-success"
										// highlight: "Success",
										// info: "Success"
								});
							}
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogMultiVehicle();
							that.getView().setModel(new JSONModel(null), "VehiclesForGroup");
						}
					} else {
						MessageBox.error(data.resp.errorMessage);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error(oURL + ": Error");
				});
		},

		onDialogMultiVehicle: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Multi Vehicle Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>description}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								info: "{Msg>info}",
								infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
						}.bind(this)
					})
				});
				//to get access to the global model
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		getVehiclesForGroup: function (groupID) {
			//eslint-disable-line
			var oData = this.getView().getModel("InvoiceItemModel").getData(),
				that = this,
				searchInfo = {
					"req": {
						"ewbNo": oData.ewbNoresp,
						"groupNo": groupID
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getVehiclesForGroup.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.bindVehiclesForGroup(data);
						that._oChangeMultiVehicle.open();
					} else {
						MessageBox.error(data.resp.message);
						that.getView().setModel(new JSONModel(null), "VehiclesForGroup");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(null), "VehiclesForGroup");
					MessageBox.error("getVehiclesForGroup.do : Error");
				});
		},

		bindVehiclesForGroup: function (data) {
			data.resp.transMode = Number(data.resp.transMode) - 1;
			data.resp.edit = false;
			for (var i = 0; i < data.resp.vechicleDetails.length; i++) {
				data.resp.vechicleDetails[i].edit = false;
			}
			this.getView().setModel(new JSONModel(data.resp), "VehiclesForGroup");
		},

		bindVehiclesForButton: function (data) {
			var vData = this.getView().getModel("EwbFromAndToData").getData();
			var oData = {
				"hdr": {
					"status": "S"
				},
				"resp": {
					"ewbNo": "",
					"reasonCode": "",
					"reasonRem": "",
					"fromPlace": vData.fromPlace,
					"fromState": vData.fromState,
					"toPlace": vData.toPlace,
					"toState": vData.toState,
					"transMode": 0,
					"totalQty": "",
					"unitCode": "",
					"docNo": "",
					"edit": true,
					"vechicleDetails": []
				}
			};
			this.getView().setModel(new JSONModel(oData.resp), "VehiclesForGroup");
		},

		onPressAddVehicle: function () {
			var oData = this.getView().getModel("VehiclesForGroup").getData();
			oData.vechicleDetails.push({
				"edit": true,
				"vehicleNo": "",
				"transDocNo": "",
				"transDocDate": "",
				"quantity": null
			});
			this.getView().getModel("VehiclesForGroup").refresh();
		},

		onPressRefresh: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			if (oData.ewbNoresp == undefined) {
				MessageBox.information("EWB Number should be mandatory");
				this.getView().setModel(new JSONModel(null), "MVDetails");
				return;
			}
			var searchInfo = {
				"req": {
					"ewbNo": oData.ewbNoresp,
					"docNum": oData.docNo,
					"suppGSTIN": oData.tranType == "O" ? oData.suppGstin : oData.custGstin
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getMultiVehicleDetails.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].sno = i + 1;
						}
						that.getView().setModel(new JSONModel(data), "MVDetails");
					} else {
						MessageBox.error(data.resp);
						that.getView().setModel(new JSONModel(null), "MVDetails");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(null), "MVDetails");
					MessageBox.error("getPartBDetails.do : Error");
				});
		},

		handleIconTabBarSelectINV: function () {
			var oSelectedKey = this.getView().byId("iditbTablayout").getSelectedKey();
			if (oSelectedKey == "MVInformation") {
				this.onPressRefresh();
				this.listEwbFromAndToData();
			} else if (oSelectedKey == "TransportPartB") {
				this.getPartBHistoryDetails();
			}
		},

		getPartBHistoryDetails: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData(),
				that = this,
				searchInfo = {
					"req": {
						"ewbNo": [oData.ewbNoresp]
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getPartBHistoryDetails.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].sno = i + 1;
						}
						that.getView().setModel(new JSONModel(data), "PartBHistoryDetails");
					} else {
						// MessageBox.error(data.resp);
						that.getView().setModel(new JSONModel(null), "PartBHistoryDetails");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(null), "PartBHistoryDetails");
				});
		},

		onPressUpdateMultiVehicle: function (searchInfo) {
			if (!this._oUpdateVehicleDetails) {
				this._oUpdateVehicleDetails = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.invManageNew.tabLayout.updateVehicleDetails", this);
				this.getView().addDependent(this._oUpdateVehicleDetails);
			}
			this.getView().byId("iduvdVehicleNumber").setSelectedKey("");
			this.getView().byId("iduvdOldTransNo").setValue("");

			this.clearUpdateVehicle();
			this.getGroupsNoForEwb();
			// this._oUpdateVehicleDetails.open();
		},

		onPressUVDClose: function (oEvent) {
			if (oEvent.getSource().getId().includes("bUVDApply")) {
				this.changeMultiVehicle();
			} else {
				this._oUpdateVehicleDetails.close();
			}
		},

		listEwbFromAndToData: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			if (oData.ewbNoresp == undefined) {
				return;
			} else {
				var searchInfo = {
					"req": {
						"ewbNo": oData.ewbNoresp
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/listEwbFromAndToData.do",
						contentType: "application/json",
						data: JSON.stringify(searchInfo)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						this.getView().setModel(new JSONModel(data.resp), "EwbFromAndToData");
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		getGroupsNoForEwb: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData(),
				searchInfo = {
					"req": {
						"ewbNo": oData.ewbNoresp
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGroupsNoForEwb.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].sno = i + 1;
						}
						this.getView().setModel(new JSONModel(data), "GroupsNoForEwb");
						this._oUpdateVehicleDetails.open();
					} else {
						MessageBox.information("Please initiate multi vehicle movement before updating");
						this.getView().setModel(new JSONModel(null), "GroupsNoForEwb");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "GroupsNoForEwb");
				}.bind(this));
		},

		onGroupsNoForEwbChange: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			var searchInfo = {
				"req": {
					"ewbNo": oData.ewbNoresp,
					"groupNo": this.getView().byId("iduvdGroupNo").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getVehicleNumber.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].sno = i + 1;
						}
						this.getView().setModel(new JSONModel(data), "VehicleNumber");
					} else {
						MessageBox.error(data.resp);
						this.getView().setModel(new JSONModel(null), "VehicleNumber");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "VehicleNumber");
					MessageBox.error("getVehicleNumber.do : Error");
				}.bind(this));
		},

		changeMultiVehicle: function () {
			var oData = this.getView().getModel("InvoiceItemModel").getData();
			if (this.getView().byId("iduvdGroupNo").getSelectedKey() == "") {
				MessageBox.information("Group Number is required");
				return;

			} else if (this.getView().byId("iduvdFromState").getSelectedKey() == "") {
				MessageBox.information("From State is required");
				return;
			} else if (this.getView().byId("idFromPlace").getValue() == "") {
				MessageBox.information("From Place is required");
				return;
			} else if (this.getView().byId("iduvdReason").getSelectedKey() == "") {
				MessageBox.information("Reason Code is required");
				return;
			}
			var searchInfo = {
				"req": {
					"ewbNo": oData.ewbNoresp,
					"groupNo": this.getView().byId("iduvdGroupNo").getSelectedKey(),
					"oldvehicleNo": this.getView().byId("iduvdVehicleNumber").getSelectedKey(),
					"newVehicleNo": this.getView().byId("iduvdNewVehicleNo").getValue(),
					"oldTranNo": this.getView().byId("iduvdOldTransNo").getValue(),
					"newTranNo": this.getView().byId("iduvdNewTransNo").getValue(),
					"fromPlace": this.getView().byId("idFromPlace").getValue(),
					"fromState": this.getView().byId("iduvdFromState").getSelectedKey(),
					"reasonCode": this.getView().byId("iduvdReason").getSelectedKey(),
					"reasonRem": this.getView().byId("idUVDRemarks").getValue(),
					"gstin": oData.tranType == "O" ? oData.suppGstin : oData.custGstin
				}
			};
			this._oUpdateVehicleDetails.close();
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/changeMultiVehicle.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp.msg);
						this.clearUpdateVehicle();
					} else {
						MessageBox.error(data.resp.errorMessage);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("changeMultiVehicle.do : Error");
				}.bind(this));
		},

		clearUpdateVehicle: function () {
			this.getView().byId("iduvdGroupNo").setSelectedKey("");
			this.getView().byId("iduvdVehicleNumber").setSelectedKey("");
			this.getView().byId("iduvdNewVehicleNo").setValue("");
			this.getView().byId("iduvdOldTransNo").setValue("");
			this.getView().byId("iduvdNewTransNo").setValue("");
			this.getView().byId("idFromPlace").setValue("");
			this.getView().byId("iduvdFromState").setSelectedKey("");
			this.getView().byId("iduvdReason").setSelectedKey("");
			this.getView().byId("idUVDRemarks").setValue("");
		},

		onSelectionChangeVehicleNumber: function (oEvent) {
			var vData = oEvent.getSource().getSelectedItem().getAdditionalText();
			this.getView().byId("iduvdOldTransNo").setValue(vData);
		},

		onPressDeleteVehicle: function () {
			var tab = this.getView().byId("idtableAMV");
			var sItems = tab.getSelectedIndices();
			var oModel2 = this.getView().byId("idtableAMV").getModel("VehiclesForGroup");
			var itemlist1 = oModel2.getProperty("/").vechicleDetails;
			var reverse = [].concat(tab.getSelectedIndices()).reverse();
			reverse.forEach(function (index) {
				if (itemlist1[index].edit == true) {
					itemlist1.splice(index, 1);
				}
			});
			oModel2.refresh();
			tab.setSelectedIndex(-1);
		},

		onFP: function () {
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.VendorRating", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.open();
		},

		onPressDelete: function () {
			var oModelForTab1 = this.getView().getModel("invInward").getProperty("/resp"),
				index = this.byId("idIWtableInvNew").getSelectedIndices(),
				id = [];

			if (!index.length) {
				MessageBox.information("Plese select atleast One GSTIN");
				return;
			}
			index.forEach(function (e) {
				id.push(oModelForTab1[e].id);
			});
			MessageBox.information('Do you want to delete selected records?', {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onDelete1(id);
					}
				}.bind(this)
			});
		},

		onDelete1: function (id) {
			var req = {
				"req": {
					"docIds": id
				}
			};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/deleteInvoices.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
						this.onPressInwardGoFinal();
					} else {
						MessageBox.success(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressSaveToGstn: function () {
			var aIndex = this.byId("idtableInvNew").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.information("Plese select atleast One GSTIN");
				return;
			}
			MessageBox.warning("Warning: The E-invoice data will be deleted in case you have not performed E-invoice recon!", {
				actions: ["Okay", "Back"],
				onClose: function (sAction) {
					if (sAction === "Okay") {
						MessageBox.confirm("You wanted to Save the data to GSTN, do you really want to continue?", {
							actions: ["Save", "Cancel"],
							emphasizedAction: "Save",
							onClose: function (sAction) {
								if (sAction === "Save") {
									this._validateSaveDelete(this._saveToGstn.bind(this));
								}
							}.bind(this)
						});
					}
				}.bind(this)
			});
		},

		_saveToGstn: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SaveToGstnInvLvl.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S" && data.resp.length) {
						var aMessage = data.resp.map(function (e) {
							return {
								title: (e.gstin || e.msg),
								desc: (e.gstin ? e.msg : ""),
								icon: "sap-icon://message-success",
								highlight: "Success"
							};
						});
						this._displayMessage("Save GSTR-1", aMessage);
					} else {
						MessageBox.error(data.hdr.message);
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("gstr1SaveToGstnResetAndSaveJob : Error");
				}.bind(this));
		},

		onDeleteFromGSTN: function () {
			var aIndex = this.byId("idtableInvNew").getSelectedIndices();
			if (!aIndex.length) {
				MessageBox.information("Plese select atleast one GSTIN");
				return;
			}
			MessageBox.confirm("You wanted to Delete the data from GSTN, do you really want to continue?", {
				actions: ["Delete", "Cancel"],
				emphasizedAction: "Delete",
				onClose: function (sAction) {
					if (sAction === "Delete") {
						this._validateSaveDelete(this._deleteFromGstn.bind(this));
					}
				}.bind(this)
			});
		},

		_deleteFromGstn: function (payload) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1DeleteInvLvl.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var aMessage = data.resp.map(function (e) {
							return {
								title: (e.gstin || e.msg),
								desc: (e.gstin ? e.msg : ""),
								icon: "sap-icon://message-success",
								highlight: "Success"
							};
						});
						this._displayMessage("Delete GSTR-1", aMessage);
					} else {
						MessageBox.error(data.hdr.message);
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_validateSaveDelete: function (fnCallback) {
			var aDocType = [
					"B2B", "B2BA", "B2CL", "B2CLA", "CDNR", "CDNRA", "CDNUR", "CDNURA", "EXPORTS", "EXPORTS-A", "Ecomsup", "CDNUR-EXPORTS", "CAN"
				],
				oData = this.getView().getModel("invOutward").getProperty("/resp"),
				aIndex = this.byId("idtableInvNew").getSelectedIndices(),
				flag = false,
				payload = {
					"req": []
				};

			aIndex.forEach(function (e) {
				if (!aDocType.includes(oData[e].taxDocType)) {
					flag = true;
				}
				payload.req.push({
					"gstin": oData[e].gstin,
					"ret_period": oData[e].retPeriod,
					"docIds": oData[e].id,
					"supplyTypeInvMgmt": oData[e].supplyType,
					"section": oData[e].taxDocType,
					"docKey": oData[e].docKey
				});
			});
			if (flag) {
				var oVBox = new sap.m.VBox({
					items: [
						new sap.m.Label({
							text: "Summary Sections (listed below) data & their respective amendments can only be Saved from GSTR-1 screen \n"
						}),
						new sap.m.Label({
							text: "Note: B2CS, Adv.Received, Adv.Adjusted, NIL/NON/Exempt, SUPECOM(14), ECOM(15(ii) & 15(iv))",
							design: "Bold"
						}).addStyleClass("sapUiSmallMarginTop")
					]
				});
				var dialog = new sap.m.Dialog({
					title: "Information",
					icon: "sap-icon://information",
					type: sap.m.DialogType.Message,
					content: [oVBox],
					beginButton: new sap.m.Button({
						text: "OK",
						press: function () {
							dialog.close();
						}
					}),
					afterClose: function () {
						dialog.destroy(); // Clean up the dialog after closing
					}
				});
				dialog.open();
				return;
			}
			fnCallback(payload);
		},

		_displayMessage: function (title, aMsg) {
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: title,
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>desc}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}"
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							// this._getSaveGstnStatus();
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.setModel(new JSONModel(aMsg), "Msg");
			this.pressDialog.open();
		},

		onSaveStatus: function (oEvent) {
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManageNew.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStats);

			}
			this._oDialogSaveStats.open();
			this._getSaveStatus();
		},

		_getSaveStatus: function () {
			var oData = this.byId("idtableInvNew").getModel("invOutward").getProperty("/"),
				payload = {
					"req": []
				};

			for (var i = 0; i < oData.resp.length; i++) {
				payload.req.push({
					"entityId": $.sap.entityID,
					"gstin": oData.resp[i].gstin,
					"ret_period": oData.resp[i].retPeriod
				});
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1InvLvlSaveStatus.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.resp === undefined) {
						this.byId("dSaveStatus").setModel(new JSONModel([]), "SaveStatus");
					} else {
						data.resp.forEach(function (e, i) {
							e.sno = i + 1;
						});
						this.byId("dSaveStatus").setModel(new JSONModel(data.resp), "SaveStatus");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseSaveStatus: function () {
			this._oDialogSaveStats.close();
		},

		onDownloadConsGstnErrors: function (oEvent, errType, refId, gstin, retPeriod) {
			switch (errType) {
			case "aspError":
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"gstin": gstin,
						"taxPeriod": retPeriod,
						"gstnRefId": refId
					}
				};
				this.excelDownload(oPayload, "/aspsapapi/consoidateGstnErrorReports.do");
				break;
			case "gstnError":
				oPayload = {
					"req": {
						"dataType": "outward",
						"entityId": [$.sap.entityID],
						"type": null,
						"taxPeriod": null,
						"gstnRefId": refId,
						"dataSecAttrs": {}
					}
				};
				oPayload.req.type = "gstnError";
				oPayload.req.taxPeriod = retPeriod;
				oPayload.req.dataSecAttrs.GSTIN = [gstin];
				this.excelDownload(oPayload, "/aspsapapi/downloadGstr1RSReports.do");
				break;
			default:
				window.open("/aspsapapi/downloadGstr1GetSumErrResp.do?refId=" + refId);
			}
		},

		onDownloadConsGstnErrorsDelete: function (oEvent, errType, refId, gstin, retPeriod) {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"type": "gstnError",
					"taxPeriod": retPeriod,
					"refId": refId,
					"dataSecAttrs": {
						"GSTIN": [gstin]
					}
				}
			};
			this.excelDownload(oPayload, "/aspsapapi/downloadGstr1GstnErrorReports.do");
		},

		onSaveStatusDownload2: function (oEvt) {
			var oData = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject(),
				req = {
					"req": {
						"supplierGstin": oData.gstin,
						"returnPeriod": oData.retPeriod,
						"createdOn": oData.createdOn,
						"section": oData.section
					}
				};
			this.excelDownload(req, "/aspsapapi/gstr1JsonDownloadDocument.do");
		},

		onSearch: function (oEvent) {
			var oTable = this.getView().byId("tabSaveStatus"),
				oBinding = oTable.getBinding("items"),
				sQuery = oEvent.getParameter("query");

			if (sQuery && sQuery.length > 0) {
				var oFilter = new sap.ui.model.Filter({
					filters: [
						new sap.ui.model.Filter("refId", sap.ui.model.FilterOperator.Contains, sQuery),
						// new sap.ui.model.Filter("Property2", sap.ui.model.FilterOperator.Contains, sQuery),
						// Add more filters for other properties as needed
					],
					and: false
				});

				oBinding.filter(oFilter);
			} else {
				oBinding.filter([]);
			}
		}
	});
});