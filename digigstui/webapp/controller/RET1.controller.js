sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage"
], function (BaseController, Formatter, JSONModel, Storage) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.RET1", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.RET1
		 */
		onInit: function () {
			var that = this,
				vDate = new Date();

			// 			Processed Records
			this.byId("dtProcessed").setMaxDate(vDate);
			this.byId("dtProcessed").setDateValue(vDate);

			this.byId("dtSummary").setMaxDate(vDate);
			this.byId("dtSummary").setDateValue(vDate);

			this.byId("dtProcessed").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtProcessed").$().find("input").attr("readonly", true);
				}
			});
			this.byId("dtSummary").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtSummary").$().find("input").attr("readonly", true);
				}
			});

			var oProp = {
				"appr": false,
				"updateDate": null,
				"apprStatus": null,
				"apprTime": null,
				"apprClass": null
			};
			this.getView().setModel(new JSONModel(oProp), "Ret1Property");

			// var oStorage = new Storage(Storage.Type.session, "digiGst");
			// var oEntityData = oStorage.get("entity");
			// if (!oEntityData) {
			// 	this.getRouter().navTo("Home");
			// } else {
			// 	var oModel = this.getOwnerComponent().getModel("EntityModel");
			// 	oModel.setData(oEntityData);
			// 	oModel.refresh(true);
			// 	this._getDataSecurity(oEntityData[0].entityId, "dpProcessRet1");
			// }
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.RET1
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.RET1
		 */
		onAfterRendering: function () {
			// debugger; //eslint-disable-line
			this._getProcessRecords(this._processPayload());
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.RET1
		 */
		//	onExit: function() {
		//
		//	}

		/**
		 * Called to get Data Security data based on selected Entity
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onEntityChange: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("slEntity")) {
				var vPage = "dpProcessRet1";

			} else if (oEvent.getSource().getId().includes("slRet1Entity")) {
				vPage = "dpSummaryRet1";
			}
			var vEntityId = oEvent.getSource().getSelectedKey();
			this._getDataSecurity(vEntityId, vPage);
		},

		/**
		 * Developed by Bharat Gupta on 18.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object 
		 */
		onSearch: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("bProcessGo")) {
				var oPayload = this._processPayload();
				this._getProcessRecords(oPayload);
			} else if (oEvent.getSource().getId().includes("bSummaryGo")) {
				this._getSummaryData(this._summaryPayload());
				this._getApprovalStatus();
			}
		},

		/** 
		 * Called to get Ret1 Process Records search Payload
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @return {Object} oPayload
		 */
		_processPayload: function () {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], //[this.byId("slEntity").getSelectedKey()],
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slGstin").getSelectedKeys()
					}
				}
			};
			return oPayload;
		},

		/**
		 * Call to get Ret-1 Processed Records data
		 * Developed by: Bharat Gupta on 17.11.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oPayload Payload structure
		 */
		_getProcessRecords: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this,
				oModel = that.byId("tabProcessRet1").getModel("ProcessedRecord");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
				var oProcessModel = that.byId("dpProcessRet1").getModel("ProcessedModel");
				oProcessModel.setData(null);
				oProcessModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getRet1ProcessedRecords.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oBundle = that.getResourceBundle();
				that.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText("A"));
				that.byId("tabProcessRet1").setModel(new JSONModel(data.resp), "ProcessedRecord");
				that.byId("dpProcessRet1").setModel(new JSONModel(data.resp), "ProcessedModel");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called when click on back button to go back on Process screen
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressBack: function (oEvent) {
			// debugger; //eslint-disable-line
			this.byId("dpProcessRet1").setVisible(true);
			this.byId("dpSummaryRet1").setVisible(false);
		},

		/**
		 * Called when click on Gstin link in process screen to get summary of selected Gstin
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressRet1Summary: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpProcessRet1").setVisible(false);
			this.byId("dpSummaryRet1").setVisible(true);

			// this.byId("slRet1Entity").setSelectedKey(this.byId("slEntity").getSelectedKey());
			this.byId("dtSummary").setDateValue(this.byId("dtProcessed").getDateValue());
			// this._getDataSecurity(this.byId("slEntity").getSelectedKey(), "dpSummaryRet1");
			this.byId("slRet1Gstin").setSelectedKeys(obj.gstin);

			var oVisiCol = {
				"computed": true,
				"usrInput": true,
				"gstn": true,
				"diff": true,
				"aspComp": false,
				"aspUser": false,
				"gstnChk": false,
				"diffChk": false
			};
			this.getView().setModel(new JSONModel(oVisiCol), "VisiProp");

			this._getSummaryData(this._summaryPayload(obj.gstin));
			this._getApprovalStatus();
		},

		/** 
		 * Called to get Ret1 Summary search Payload
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {string} vGstin Gstin number
		 * @return {Object} oPayload
		 */
		_summaryPayload: function (vGstin) {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
					"taxPeriod": this.byId("dtSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
					}
				}
			};
			if (vGstin) {
				oPayload.req.dataSecAttrs.GSTIN = [];
				oPayload.req.dataSecAttrs.GSTIN.push(vGstin);
			}
			return oPayload;
		},

		/**
		 * Called to get Summary data for selected Gstin
		 * Developed by: Bharat Gupta on 19.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_getSummaryData: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			that._collapseAllTables();
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1Summary.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._bindSummary(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called to bind formatted data to Review summary tables
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Response object
		 */
		_bindSummary: function (data) {
			// debugger; //eslint-disable-line
			var oSummaryRet1 = {
				"aspValues": [],
				"lateFee": [],
				"taxPayment": [],
				"refund": []
			};
			this._jsonAspValues(oSummaryRet1.aspValues, data.aspValues);
			oSummaryRet1.lateFee.push(this._jsonLateFee(data.lateFee.tab_6));
			oSummaryRet1.taxPayment.push(this._jsonTaxPayment(data.taxPayment.tab_7));
			oSummaryRet1.refund.push(this._jsonRefund(data.refund.tab_8));
			this.byId("dpSummaryRet1").setModel(new JSONModel(oSummaryRet1), "SummaryRet1");
		},

		/**
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oSummary Summary object
		 * @param {Object} data Response object
		 */
		_jsonAspValues: function (oSummary, data) {
			// debugger; //eslint-disable-line
			var aTable = ["A3", "B3", "C3", "D3", "E3", "A4", "B4", "C4", "D4", "E4", "5"];

			for (var i = 0; i < aTable.length; i++) {
				var aAspValue = this._jsonAsp(),
					oTemp = data[aTable[i]];

				for (var j = 0; j < oTemp.length; j++) {
					if (!oTemp[j].section) {
						aAspValue.tableSection = oTemp[j].table;
						aAspValue.table = oTemp[j].table;
						aAspValue.section = oTemp[j].section;
						aAspValue.supplyType = oTemp[j].supplyType;
						aAspValue.aspValue = oTemp[j].aspValue;
						aAspValue.aspIgst = oTemp[j].aspIgst;
						aAspValue.aspCgst = oTemp[j].aspCgst;
						aAspValue.aspSgst = oTemp[j].aspSgst;
						aAspValue.aspCess = oTemp[j].aspCess;
						aAspValue.usrValue = oTemp[j].usrValue;
						aAspValue.usrIgst = oTemp[j].usrIgst;
						aAspValue.usrCgst = oTemp[j].usrCgst;
						aAspValue.usrSgst = oTemp[j].usrSgst;
						aAspValue.usrCess = oTemp[j].usrCess;
						aAspValue.gstnValue = oTemp[j].gstnValue;
						aAspValue.gstnIgst = oTemp[j].gstnIgst;
						aAspValue.gstnCgst = oTemp[j].gstnCgst;
						aAspValue.gstnSgst = oTemp[j].gstnSgst;
						aAspValue.gstnCess = oTemp[j].gstnCess;
						aAspValue.diffValue = oTemp[j].diffValue;
						aAspValue.diffIgst = oTemp[j].diffIgst;
						aAspValue.diffCgst = oTemp[j].diffCgst;
						aAspValue.diffSgst = oTemp[j].diffSgst;
						aAspValue.diffCess = oTemp[j].diffCess;
						aAspValue.link = false;
						aAspValue.edit = false;
					} else {
						aAspValue.items.push(this._linkAspValues(oTemp[j]));
					}
				}
				oSummary.push(aAspValue);
			}
		},

		/**
		 * Called to Enable/disable Link & Editable for table field to get input from user
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Response object
		 * @return {Object} data
		 */
		_linkAspValues: function (data) {
			// debugger; //eslint-disable-line
			var aTable = [
				"3A8", "3C3", "3C4", "3C5", "3D1", "3D2", "3D3", "3D4",
				"4A4", "4A10", "4A11", "4B2", "4B3", "4B4", "4B5", "4E1", "4E2"
			];
			data.tableSection = data.section;
			data.link = false;
			data.edit = false;

			if (data.section && aTable.includes(data.table)) {
				data.link = true;
			}
			return data;
		},

		/**
		 * Called to get JSON structure for ASP Values to format & bind to Summary table
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @return {Object} JSON Structure
		 */
		_jsonAsp: function () {
			return {
				"table": null,
				"section": null,
				"supplyType": null,
				"aspValue": 0,
				"aspIgst": 0,
				"aspCgst": 0,
				"aspSgst": 0,
				"aspCess": 0,
				"usrValue": 0,
				"usrIgst": 0,
				"usrCgst": 0,
				"usrSgst": 0,
				"usrCess": 0,
				"gstnValue": 0,
				"gstnIgst": 0,
				"gstnCgst": 0,
				"gstnSgst": 0,
				"gstnCess": 0,
				"diffValue": 0,
				"diffIgst": 0,
				"diffCgst": 0,
				"diffSgst": 0,
				"diffCess": 0,
				"items": []
			};
		},

		/**
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Response object for Late Fee
		 * @return {Object} oLateFee
		 */
		_jsonLateFee: function (data) {
			// debugger; //eslint-disable-line
			var oLateFee = {
				"items": []
			};
			for (var i = 0; i < data.length; i++) {
				if (!data[i].section) {
					oLateFee.table = data[i].table;
					oLateFee.desc = data[i].desc;
					oLateFee.usrIgst = data[i].usrIgst;
					oLateFee.usrCgst = data[i].usrCgst;
					oLateFee.usrSgst = data[i].usrSgst;
					oLateFee.usrCess = data[i].usrCess;
					oLateFee.usrLateFeeCgst = data[i].usrLateFeeCgst;
					oLateFee.usrLateFeeSgst = data[i].usrLateFeeSgst;
					oLateFee.gstnIgst = data[i].gstnIgst;
					oLateFee.gstnCgst = data[i].gstnCgst;
					oLateFee.gstnSgst = data[i].gstnSgst;
					oLateFee.gstnCess = data[i].gstnCess;
					oLateFee.gstnLateFeeCgst = data[i].gstnLateFeeCgst;
					oLateFee.gstnLateFeeSgst = data[i].gstnLateFeeSgst;
					oLateFee.diffIgst = data[i].diffIgst;
					oLateFee.diffCgst = data[i].diffCgst;
					oLateFee.diffSgst = data[i].diffSgst;
					oLateFee.diffCess = data[i].diffCess;
					oLateFee.diffLateFeeCgst = data[i].diffLateFeeCgst;
					oLateFee.diffLateFeeSgst = data[i].diffLateFeeSgst;
					oLateFee.link = true;
					oLateFee.edit = false;
				} else {
					var oTemp = data[i];
					oTemp.table = data[i].section;
					oTemp.link = false;
					oTemp.edit = false;
					oLateFee.items.push(oTemp);
				}
			}
			return oLateFee;
		},

		/**
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Response object for Tax payment
		 * @return {Object} oTaxPayment
		 */
		_jsonTaxPayment: function (data) {
			// debugger; //eslint-disable-line
			var oTaxPayment = {
				"items": []
			};
			for (var i = 0; i < data.length; i++) {
				if (!data[i].section) {
					oTaxPayment.table = data[i].table;
					oTaxPayment.desc = data[i].desc;
					oTaxPayment.usrPayable = data[i].usrPayable;
					oTaxPayment.usrOtherPayable = data[i].usrOtherPayable;
					oTaxPayment.usrPaid = data[i].usrPaid;
					oTaxPayment.usrOtherPaid = data[i].usrOtherPaid;
					oTaxPayment.usrLiability = data[i].usrLiability;
					oTaxPayment.usrOtherLiability = data[i].usrOtherLiability;
					oTaxPayment.usrItcPaidIgst = data[i].usrItcPaidIgst;
					oTaxPayment.usrItcPaidCgst = data[i].usrItcPaidCgst;
					oTaxPayment.usrItcPaidSgst = data[i].usrItcPaidSgst;
					oTaxPayment.usrItcPaidCess = data[i].usrItcPaidCess;
					oTaxPayment.usrCashPaidTax = data[i].usrCashPaidTax;
					oTaxPayment.usrCashPaidInterest = data[i].usrCashPaidInterest;
					oTaxPayment.usrCashPaidLateFee = data[i].usrCashPaidLateFee;
					this._jsonGstnTaxPayment(oTaxPayment, data[i]);
					this._jsonDiffTaxPayment(oTaxPayment, data[i]);
					oTaxPayment.link = true;
					oTaxPayment.edit = false;
				} else {
					var oTemp = data[i];
					oTemp.table = data[i].section;
					oTemp.link = false;
					oTemp.edit = false;
					oTaxPayment.items.push(oTemp);
				}
			}
			return oTaxPayment;
		},

		/**
		 * Called for JSON ojbect for Tax Payment
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oTaxPayment JSON Object
		 * @param {Object} data Response object for Tax Payment
		 */
		_jsonGstnTaxPayment: function (oTaxPayment, data) {
			oTaxPayment.gstnPayable = data.gstnPayable;
			oTaxPayment.gstnOtherPayable = data.gstnOtherPayable;
			oTaxPayment.gstnPaid = data.gstnPaid;
			oTaxPayment.gstnOtherPaid = data.gstnOtherPaid;
			oTaxPayment.gstnLiability = data.gstnLiability;
			oTaxPayment.gstnOtherLiability = data.gstnOtherLiability;
			oTaxPayment.gstnItcPaidIgst = data.gstnItcPaidIgst;
			oTaxPayment.gstnItcPaidCgst = data.gstnItcPaidCgst;
			oTaxPayment.gstnItcPaidSgst = data.gstnItcPaidSgst;
			oTaxPayment.gstnItcPaidCess = data.gstnItcPaidCess;
			oTaxPayment.gstnCashPaidTax = data.gstnCashPaidTax;
			oTaxPayment.gstnCashPaidInterest = data.gstnCashPaidInterest;
			oTaxPayment.gstnCashPaidLateFee = data.gstnCashPaidLateFee;
		},

		/**
		 * Called for JSON ojbect for Tax Payment
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oTaxPayment JSON Object
		 * @param {Object} data Response object for Tax Payment
		 */
		_jsonDiffTaxPayment: function (oTaxPayment, data) {
			oTaxPayment.diffPayable = data.diffPayable;
			oTaxPayment.diffOtherPayable = data.diffOtherPayable;
			oTaxPayment.diffPaid = data.diffPaid;
			oTaxPayment.diffOtherPaid = data.diffOtherPaid;
			oTaxPayment.diffLiability = data.diffLiability;
			oTaxPayment.diffOtherLiability = data.diffOtherLiability;
			oTaxPayment.diffItcPaidIgst = data.diffItcPaidIgst;
			oTaxPayment.diffItcPaidCgst = data.diffItcPaidCgst;
			oTaxPayment.diffItcPaidSgst = data.diffItcPaidSgst;
			oTaxPayment.diffItcPaidCess = data.diffItcPaidCess;
			oTaxPayment.diffCashPaidTax = data.diffCashPaidTax;
			oTaxPayment.diffCashPaidInterest = data.diffCashPaidInterest;
			oTaxPayment.diffCashPaidLateFee = data.diffCashPaidLateFee;
		},

		/**
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Response object for Refund claim
		 * @return {Object} oRefund
		 */
		_jsonRefund: function (data) {
			// debugger; //eslint-disable-line
			var oRefund = {
				"items": []
			};
			for (var i = 0; i < data.length; i++) {
				if (!data[i].section) {
					oRefund.table = data[i].table;
					oRefund.desc = data[i].desc;
					oRefund.usrTax = data[i].usrTax;
					oRefund.usrInterest = data[i].usrInterest;
					oRefund.usrPenality = data[i].usrPenality;
					oRefund.usrFee = data[i].usrFee;
					oRefund.usrOther = data[i].usrOther;
					oRefund.usrTotal = data[i].usrTotal;
					oRefund.gstnTax = data[i].gstnTax;
					oRefund.gstnInterest = data[i].gstnInterest;
					oRefund.gstnPenality = data[i].gstnPenality;
					oRefund.gstnFee = data[i].gstnFee;
					oRefund.gstnOther = data[i].gstnOther;
					oRefund.gstnTotal = data[i].gstnTotal;
					oRefund.diffTax = data[i].diffTax;
					oRefund.diffInterest = data[i].diffInterest;
					oRefund.diffPenality = data[i].diffPenality;
					oRefund.diffFee = data[i].diffFee;
					oRefund.diffOther = data[i].diffOther;
					oRefund.diffTotal = data[i].diffTotal;
					oRefund.link = true;
					oRefund.edit = false;
				} else {
					var oTemp = data[i];
					oTemp.table = data[i].section;
					oTemp.link = false;
					oTemp.edit = false;
					oRefund.items.push(oTemp);
				}
			}
			return oRefund;
		},

		onRowSelectionChange: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabProcessRet1").getSelectedIndices(),
				oPropModel = this.getView().getModel("Ret1Property");
			if (aIndices.length > 0) {
				oPropModel.getData().appr = true;
			} else {
				oPropModel.getData().appr = false;
			}
			oPropModel.refresh(true);
		},

		onPressApprRequest: function (oEvent, view) {
			// debugger; //eslint-disable-line
			var that = this,
				oPayload = {
					"req": {
						"gstins": [],
						"returnPeriod": null
					}
				};
			if (view === "P") {
				var aIndices = this.byId("tabProcessRet1").getSelectedIndices(),
					oData = this.byId("tabProcessRet1").getModel("ProcessedRecord").getData();

				for (var i = 0; i < aIndices.length; i++) {
					oPayload.req.gstins.push(oData[aIndices[i]].gstin);
				}
				oPayload.req.returnPeriod = this.byId("dtProcessed").getValue();
			} else {
				oPayload.req.gstins = this.byId("slRet1Gstin").getSelectedKeys();
				oPayload.req.returnPeriod = this.byId("dtSummary").getValue();
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/requestRet1ForApproval.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oBundle = that.getResourceBundle();
				if (data.hdr.status === "S") {
					var oPropModel = that.getView().getModel("Ret1Property");
					oPropModel.getData().apprStatus = oBundle.getText("sent") + ": ";
					oPropModel.getData().apprTime = data.resp.createdDate;
					oPropModel.getData().apprClass = "sent";
					oPropModel.refresh(true);
					sap.m.MessageBox.success(oBundle.getText("msgApproval"), {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_getApprovalStatus: function () {
			// debugger; //eslint-disable-line
			var that = this,
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstin": this.byId("slRet1Gstin").getSelectedKeys()[0],
						"returnPeriod": this.byId("dtSummary").getValue()
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getRet1ApprovalStatus.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				var oBundle = that.getResourceBundle(),
					oPropModel = that.getView().getModel("Ret1Property"),
					oViewData = oPropModel.getData();

				if (data.resp === undefined) {
					oViewData.apprStatus = null;
					oViewData.apprTime = null;
				} else {
					switch (data.resp.status) {
					case 0:
						var vStatus = oBundle.getText("approvePending"),
							styleCls = "pending";
						break;
					case 1:
						vStatus = oBundle.getText("approved");
						styleCls = "accept";
						break;
					case 2:
						vStatus = oBundle.getText("Rejected");
						styleCls = "reject";
						break;
					}
					var arrInitiateDt = data.resp.initiatedOn.split("T");
					oViewData.apprStatus = vStatus;
					oViewData.apprClass = styleCls;
					if (data.resp.approvedOn) {
						arrInitiateDt = data.resp.approvedOn.split("T");
						oViewData.apprTime = ": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8);
					} else {
						oViewData.apprTime = null;
					}
				}
				oPropModel.refresh(true);
			}).fail(function (jqXHR, status, err) {

			});
		},

		/**
		 * Called when user click on Expand All/Collapse All button on Review summary
		 * Developed by: Bharat Gupta on 20.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressExpandCollapse: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("bExpand")) {
				this.byId("tabAspValues").expandToLevel(1);
				this.byId("tabAspValues").setVisibleRowCount(15); // 53
				this.byId("tabInterest").expandToLevel(1);
				this.byId("tabInterest").setVisibleRowCount(5);
				this.byId("tabTaxPayment").expandToLevel(1);
				this.byId("tabTaxPayment").setVisibleRowCount(5);
				this.byId("tabRefund").expandToLevel(1);
				this.byId("tabRefund").setVisibleRowCount(5);
			} else {
				this._collapseAllTables();
			}
		},

		/**
		 * Called to collapse all table data records
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		_collapseAllTables: function () {
			this.byId("tabAspValues").collapseAll();
			this.byId("tabAspValues").setVisibleRowCount(11);
			this.byId("tabInterest").collapseAll();
			this.byId("tabInterest").setVisibleRowCount(1);
			this.byId("tabTaxPayment").collapseAll();
			this.byId("tabTaxPayment").setVisibleRowCount(1);
			this.byId("tabRefund").collapseAll();
			this.byId("tabRefund").setVisibleRowCount(1);
		},

		/**
		 * Called when user press on toggle button to expand/collapse table data
		 * @Developed by: Bharat Gupta on 29.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressToggle: function (oEvent) {
			// debugger; //eslint-disable-line
			var aData = oEvent.getSource().getBinding().getModel("SummaryRet1").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "aspValues") {
				var vRowCount = 11;
			} else {
				vRowCount = 1;
			}
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) { //eslint-disable-line
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[vModel][aPath[1]].items.length;
			}
			if (vRowCount > 15) {
				vRowCount = 15;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		/**
		 * Method called when user click to select and unselect Checkbox to make atleast one checkbox should be selected
		 * Develped by: Bharat Gupta on 12.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSelectCheckBox: function () {
			// debugger; //eslint-disable-line
			var oVisiModel = this.getView().getModel("VisiProp"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.computed && !oVisiData.usrInput && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.aspComp = true;

			} else if (!oVisiData.computed && oVisiData.usrInput && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.aspUser = true;

			} else if (!oVisiData.computed && !oVisiData.usrInput && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.gstnChk = true;

			} else if (!oVisiData.computed && !oVisiData.usrInput && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.diffChk = true;

			} else {
				oVisiData.aspComp = false;
				oVisiData.aspUser = false;
				oVisiData.gstnChk = false;
				oVisiData.diffChk = false;
			}
			oVisiModel.refresh(true);
		},

		/**
		 * Called to make Summary table Editable to take user input
		 * Developed by: Bharat Gupta on 02.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onPressEditSummary: function () {
			// debugger; //eslint-disable-line
			var aEditable = [
				"3A8", "3C3", "3C4", "3C5", "3D1", "3D2", "3D3", "3D4",
				"4A4", "4A10", "4A11", "4B2", "4B3", "4B4", "4B5", "4E1", "4E2"
			];

			var oSummaryModel = this.byId("dpSummaryRet1").getModel("SummaryRet1"),
				oSummaryData = oSummaryModel.getData();

			for (var i = 0; i < oSummaryData.aspValues.length; i++) {
				var aData = oSummaryData.aspValues[i].items;
				for (var j = 0; j < aData.length; j++) {
					if (aEditable.includes(aData[j].table)) {
						aData[j].edit = true;
					}
				}
			}
			oSummaryModel.refresh(true);
		},

		/**
		 * Called to open Save Status dialog
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSaveStatus: function (oEvent) {
			// debugger; //eslint-disable-line
			var that = this,
				obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();
			var oPayload = {
				"req": {
					"returnType": "ret1",
					"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
					"gstin": obj.gstin,
					"taxPeriod": this.byId("dtProcessed").getValue()
				}
			};
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.SaveStatus", this);
				this.byId("dpProcessRet1").addDependent(this._oDialogSaveStats);
				this.byId("dtStats").setMaxDate(new Date());
				this.byId("dtStats").addEventDelegate({
					onAfterRendering: function () {
						that.byId("dtStats").$().find("input").attr("readonly", true);
					}
				});
			}
			this.byId("slGstinStats").setSelectedKey(obj.gstin);
			this.byId("dtStats").setValue(this.byId("dtProcessed").getValue());
			this.getSaveStauts(oPayload);
			this._oDialogSaveStats.open();
		},

		onPressStatusGo: function () {
			var oPayload = {
				"req": {
					"returnType": "ret1",
					"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
					"gstin": this.byId("slGstinStats").getSelectedKey(),
					"taxPeriod": this.byId("dtStats").getValue()
				}
			};
			this.getSaveStauts(oPayload);
		},

		/**
		 * Called to close Save Status dialog
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onCloseSaveStatus: function () {
			this._oDialogSaveStats.close();
		},

		/**
		 * Called to open Difference dialog
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDifference: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				oPayload = {
					"req": {
						"returnType": "ret1",
						"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
						"gstin": obj.gstin,
						"taxPeriod": this.byId("dtProcessed").getValue()
					}
				};
			if (!this._oDialogDifference) {
				this._oDialogDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.Difference", this);
				this.getView().addDependent(this._oDialogDifference);
			}
			this.byId("dDifference").setModel(new JSONModel(oPayload.req), "Payload");
			this.getDifferenceData(oPayload);
			this._oDialogDifference.open();
		},

		/**
		 * Called to close difference dialog
		 * Developed by: Bharat Gupta on 17.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onCloseDifference: function () {
			this._oDialogDifference.close();
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressAspValues: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("SummaryRet1").getObject(),
				aTable = ["3A8", "3C5", "4A4", "4B4", "4B5"];

			if (aTable.includes(obj.table)) {
				this._dialogAspValues(obj);
			} else {
				this._dialogAspVertical(obj);
			}
		},

		/**
		 * Developed by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} obj Object
		 */
		_dialogAspValues: function (obj) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				oProp = {
					"enabled": false,
					"edit": false
				},
				oPayload = {
					"req": {
						"table": obj.table,
						"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
						}
					}
				};
			if (!this._oDialogAspValues) {
				this._oDialogAspValues = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.AspValues", this);
				this.byId("dpSummaryRet1").addDependent(this._oDialogAspValues);
			}
			this.byId("lAspValues").setText(oBundle.getText(obj.supplyType)); // Dialog Header Text
			this.byId("dAspValues").setModel(new JSONModel(oProp), "Property"); // Dialog Property
			this.byId("dAspValues").setModel(new JSONModel(oPayload.req), "Payload"); // Payload data to get data
			this.byId("txtValuesEntity").setText(this.getOwnerComponent().getModel("userPermission").getData().respData.entityName); // Entity
			this.byId("txtValuesGstin").setText(this.byId("slRet1Gstin").getSelectedKeys()[0]); // Gstin
			this.byId("txtValuesPeriod").setText(Formatter.periodFormat(this.byId("dtSummary").getValue())); // Tax Period

			this._getAspValuesData(oPayload);
			this._oDialogAspValues.open();
		},

		/**
		 * Called to get ASP Values data
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} payload Object
		 */
		_getAspValuesData: function (payload) {
			var oModel = this.getView().getModel("AspValues"),
				that = this;
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1AspDetail.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
				}
				that.byId("tabValuesDet").setModel(new JSONModel(data.resp), "AspValues");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} obj Object
		 */
		_dialogAspVertical: function (obj) {
			if (!this._oDialogAspVertical) {
				this._oDialogAspVertical = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.AspVertical", this);
				this.byId("dpSummaryRet1").addDependent(this._oDialogAspVertical);
			}
			var oBundle = this.getResourceBundle(),
				oProp = {
					"summary": true,
					"segment": "summary",
					"enabled": false,
					"edit": false
				},
				oPayload = {
					"req": {
						"table": obj.table,
						"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
						}
					}
				};
			this.byId("tAspVertical").setText(oBundle.getText(obj.supplyType)); // Dialog Header Text
			this.byId("dAspVertical").setModel(new JSONModel(oProp), "Property"); // Dialog Property
			this.byId("dAspVertical").setModel(new JSONModel(oPayload.req), "Payload"); // Payload data to get Data
			this.byId("tVerticalEntity").setText(this.getOwnerComponent().getModel("userPermission").getData().respData.entityName); // Entity
			this.byId("tVerticalGstin").setText(this.byId("slRet1Gstin").getSelectedKeys()[0]); // Gstin
			this.byId("tVerticalPeriod").setText(Formatter.periodFormat(this.byId("dtSummary").getValue())); // Tax Period

			this._getAspVerticalData(oPayload);
			this._oDialogAspVertical.open();
		},

		/**
		 * Called to get ASP Vertical data
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} payload Object
		 */
		_getAspVerticalData: function (payload) {
			var oModel = this.byId("dAspVertical").getModel("AspVertical"),
				that = this;
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1AspVertical.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.verticalData.length; i++) {
					data.resp.verticalData[i].edit = false;
				}
				that.byId("dAspVertical").setModel(new JSONModel(data.resp), "AspVertical");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called when user change segment button in ASP Vertical Dialog
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onAspSegmentChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var oModelProp = this.byId("dAspVertical").getModel("Property"),
				oDataProp = oModelProp.getData();
			if (oEvent.getSource().getSelectedKey() === "summary") {
				oDataProp.summary = true;
				oDataProp.segment = "summary";
			} else {
				oDataProp.summary = false;
				oDataProp.segment = "data";
			}
			oModelProp.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSummaryLink: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getText().toLowerCase() === "vertical") {
				var oPropModel = this.byId("dAspVertical").getModel("Property");
				oPropModel.getData().summary = false;
				oPropModel.getData().segment = "data";
				oPropModel.refresh(true);
			}
		},

		/**
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onPressEdit: function () {
			// debugger; //eslint-disable-line
			var oPropModel = this.byId("dAspVertical").getModel("Property"),
				oModel = this.byId("dAspVertical").getModel("AspVertical");
			oPropModel.getData().summary = false;
			oPropModel.getData().segment = "data";
			oPropModel.refresh(true);

			if (oModel) {
				var oData = oModel.getData();
				for (var i = 0; i < oData.verticalData.length; i++) {
					oData.verticalData[i].edit = true;
				}
			}
			oModel.refresh(true);
		},

		/**
		 * Called to Enabled/Disabled button in dialog when user select row in table
		 * Developed by: Bharat Gupta on 24.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onAspRowSelection: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("tabValuesDet")) {
				var aIndices = this.byId("tabValuesDet").getSelectedIndices(),
					oPropModel = this.byId("dAspValues").getModel("Property"),
					oData = this.byId("tabValuesDet").getModel("AspValues").getData();
			} else {
				aIndices = this.byId("tabDataVertical").getSelectedIndices();
				oPropModel = this.byId("dAspVertical").getModel("Property");
				oData = this.byId("tabDataVertical").getModel("AspVertical").getData().verticalData;
			}
			if (aIndices.length > 0) {
				oPropModel.getData().enabled = true;
				for (var i = 0; i < aIndices.length; i++) {
					if (oData[aIndices[i]].id) {
						oPropModel.getData().edit = true;
						break;
					}
				}
			} else {
				oPropModel.getData().enabled = false;
				oPropModel.getData().edit = false;
			}
			oPropModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditAspValues: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to edit selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabValuesDet").getSelectedIndices(),
							oDataModel = that.byId("tabValuesDet").getModel("AspValues"),
							oData = oDataModel.getData();

						for (var i = 0; i < aIndices.length; i++) {
							var obj = oData[aIndices[i]];
							obj.edit = true;
						}
						oDataModel.refresh(true);
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditAspVertical: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to edit selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabDataVertical").getSelectedIndices(),
							oDataModel = that.byId("dAspVertical").getModel("AspVertical"),
							oData = oDataModel.getData().verticalData;

						for (var i = 0; i < aIndices.length; i++) {
							var obj = oData[aIndices[i]];
							obj.edit = true;
						}
						oDataModel.refresh(true);
					}
				}
			});
		},

		/**
		 * Method call to get JSON Object for ASP Data
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {string} vDialog Dialog name
		 * @return {Object} JSON Structure
		 */
		_jsonAspVertical: function (vDialog) {
			// debugger; //eslint-disable-line
			var oPayload = this.byId(vDialog).getModel("Payload").getData();
			return {
				"returnType": "RET-1",
				"gstin": oPayload.dataSecAttrs.GSTIN[0],
				"taxPeriod": oPayload.taxPeriod,
				"returnTable": oPayload.table,
				"value": 0,
				"igstAmt": 0,
				"cgstAmt": 0,
				"sgstAmt": 0,
				"cessAmt": 0,
				"profitCenter": null,
				"plant": null,
				"division": null,
				"location": null,
				"salesOrg": null,
				"distrChannel": null,
				"userAccess1": null,
				"userAccess2": null,
				"userAccess3": null,
				"userAccess4": null,
				"userAccess5": null,
				"userAccess6": null,
				"userDefined1": null,
				"userDefined2": null,
				"userDefined3": null,
				"edit": true
			};
		},

		/**
		 * Method called to add New ASP Value line item in table
		 * Developed by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddAspValues: function () {
			// debugger; //eslint-disable-line
			var obj = this._jsonAspVertical("dAspValues"),
				oDataModel = this.byId("tabValuesDet").getModel("AspValues");

			if (oDataModel) {
				oDataModel.getData().push(obj);
				oDataModel.refresh(true);
			} else {
				var aData = [obj];
				this.byId("tabValuesDet").setModel(new JSONModel(aData), "AspValues");
			}
		},

		/**
		 * Method called to add New ASP Vertical line item in table
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddAspVertical: function () {
			// debugger; //eslint-disable-line
			var obj = this._jsonAspVertical("dAspVertical"),
				oDataModel = this.byId("dAspVertical").getModel("AspVertical");
			if (oDataModel) {
				oDataModel.getData().verticalData.push(obj);
				oDataModel.refresh(true);
			}
		},

		/**
		 * Method call to delete data for ASP Values in dialog
		 * Developed by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onDeleteAspValues: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to delete selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabValuesDet").getSelectedIndices(),
							oDataModel = that.byId("tabValuesDet").getModel("AspValues"),
							oData = oDataModel.getData();

						for (var i = aIndices.length - 1; i > -1; i--) {
							oData.splice(aIndices[i], 1);
						}
						oDataModel.refresh(true);
						that.byId("tabValuesDet").setSelectedIndex(-1);
					}
				}
			});
		},

		/**
		 * Method call to delete ASP Vertical Data in dialog
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onDeleteAspVertical: function () {
			debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to delete selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabDataVertical").getSelectedIndices(),
							oDataModel = that.byId("dAspVertical").getModel("AspVertical"),
							oData = oDataModel.getData().verticalData;

						for (var i = aIndices.length - 1; i > -1; i--) {
							oData.splice(aIndices[i], 1);
						}
						oDataModel.refresh(true);
						that.byId("tabDataVertical").setSelectedIndex(-1);
					}
				}
			});
		},

		/**
		 * Method call to delete ASP Vertical Data in dialog
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} data Data object
		 * @return {Object} Json object
		 */
		_jsonAspData: function (data) { //eslint-disable-line
			return {
				"id": data.id || null,
				"returnType": data.returnType,
				"gstin": data.gstin,
				"taxPeriod": data.taxPeriod,
				"returnTable": data.returnTable,
				"value": data.value || 0,
				"igstAmt": data.igstAmt || 0,
				"cgstAmt": data.cgstAmt || 0,
				"sgstAmt": data.sgstAmt || 0,
				"cessAmt": data.cessAmt || 0,
				"profitCenter": data.profitCenter || null,
				"plant": data.plant || null,
				"division": data.division || null,
				"location": data.location || null,
				"salesOrg": data.salesOrg || null,
				"distrChannel": data.distrChannel || null,
				"userAccess1": data.userAccess1 || null,
				"userAccess2": data.userAccess2 || null,
				"userAccess3": data.userAccess3 || null,
				"userAccess4": data.userAccess4 || null,
				"userAccess5": data.userAccess5 || null,
				"userAccess6": data.userAccess6 || null,
				"userDefined1": data.userDefined1 || null,
				"userDefined2": data.userDefined2 || null,
				"userDefined3": data.userDefined3 || null
			};
		},

		/**
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveAspValues: function () {
			// debugger; //eslint-disable-line
			var that = this,
				oData = this.byId("tabValuesDet").getModel("AspValues").getData(),
				aIndices = this.byId("tabValuesDet").getSelectedIndices(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndices.length; i++) {
				oPayload.req.push(this._jsonAspData(oData[aIndices[i]]));
			}
			sap.m.MessageBox.confirm("Do you want to save selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/ret1VerticalUpdate.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							var oProperty = that.byId("dAspValues").getModel("Property"),
								payload = {
									"req": that.byId("dAspValues").getModel("Payload").getData()
								};
							that.byId("tabValuesDet").setSelectedIndex(-1);
							oProperty.getData().enabled = false;
							oProperty.getData().edit = false;
							oProperty.refresh(true);
							that._getAspValuesData(payload);
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact"
								});
							}
						}).fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
						});
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveAspVertical: function () {
			// debugger; //eslint-disable-line
			var that = this,
				oData = this.byId("tabDataVertical").getModel("AspVertical").getData().verticalData,
				aIndices = this.byId("tabDataVertical").getSelectedIndices(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndices.length; i++) {
				oPayload.req.push(this._jsonAspData(oData[aIndices[i]]));
			}
			sap.m.MessageBox.confirm("Do you want to save selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/ret1VerticalUpdate.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							var oProperty = that.byId("dAspVertical").getModel("Property"),
								payload = {
									"req": that.byId("dAspVertical").getModel("Payload").getData()
								};
							that.byId("tabDataVertical").setSelectedIndex(-1);
							oProperty.getData().enabled = false;
							oProperty.getData().edit = false;
							oProperty.refresh(true);
							that._getAspVerticalData(payload);
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact"
								});
							}
						}).fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
						});
					}
				}
			});
		},

		/**
		 * Method Called to close ASP Values Dialog
		 * Developed by: Bharat Gupta on 26.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onCloseAspValues: function () {
			this._oDialogAspValues.close();
		},

		/**
		 * Method Called to close ASP Vertical Dialog
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onCloseAspVertical: function () {
			this._oDialogAspVertical.close();
		},

		onDialogRowSelection: function (oEvent) {
			if (oEvent.getSource().getId().includes("tabLateFee")) {
				var aIndices = this.byId("tabLateFee").getSelectedIndices(),
					oPropModel = this.byId("dLateFee").getModel("Property"),
					oData = this.byId("tabLateFee").getModel("LateFee").getData();

			} else if (oEvent.getSource().getId().includes("tabPaymentOfTax")) {
				aIndices = this.byId("tabPaymentOfTax").getSelectedIndices();
				oPropModel = this.byId("dTaxPayment").getModel("Property");
				oData = this.byId("tabPaymentOfTax").getModel("TaxPayment").getData();

			} else if (oEvent.getSource().getId().includes("tabRefundClaim")) {
				aIndices = this.byId("tabRefundClaim").getSelectedIndices();
				oPropModel = this.byId("dRefundClaim").getModel("Property");
				oData = this.byId("tabRefundClaim").getModel("RefundClaim").getData();
			}
			if (aIndices.length > 0) {
				oPropModel.getData().enabled = true;
				for (var i = 0; i < aIndices.length; i++) {
					if (oData[aIndices[i]].id) {
						oPropModel.getData().edit = true;
						break;
					}
				}
			} else {
				oPropModel.getData().enabled = false;
				oPropModel.getData().edit = false;
			}
			oPropModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressLateFee: function (oEvent) {
			if (!this._oDialogLateFee) {
				this._oDialogLateFee = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.LateFee", this);
				this.getView().addDependent(this._oDialogLateFee);
			}
			var vData = Formatter.periodFormat(this.byId("dtSummary").getValue()),
				oProp = {
					"enabled": false,
					"edit": false
				},
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
						}
					}
				};
			this.byId("dLateFee").setModel(new JSONModel(oProp), "Property");
			this.byId("dLateFee").setModel(new JSONModel(oPayload.req), "Payload"); // Payload data to get Data
			this.byId("txtLateFeeEntity").setText(this.getOwnerComponent().getModel("userPermission").getData().respData.entityName);
			this.byId("txtLateFeeGstin").setText(this.byId("slRet1Gstin").getSelectedKeys()[0]);
			this.byId("txtLateFeePeriod").setText(vData);
			this._getLateFeeData(oPayload);
			this._oDialogLateFee.open();
		},

		/**
		 * Method called to get Interest & Late Fee data from table
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_getLateFeeData: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			this._clearTableModel("LateFee", "tabLateFee");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1LateFeeDetail.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
					data.resp[i].visi = false;
				}
				that.byId("tabLateFee").setModel(new JSONModel(data.resp), "LateFee");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method call to Edit selected Late Fee records
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditLateFee: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to edit selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabLateFee").getSelectedIndices(),
							oDataModel = that.byId("tabLateFee").getModel("LateFee"),
							oData = oDataModel.getData();

						for (var i = 0; i < aIndices.length; i++) {
							var obj = oData[aIndices[i]];
							obj.edit = true;
						}
						oDataModel.refresh(true);
					}
				}
			});
		},

		/**
		 * Method call to Add new records in table
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddLateFee: function () {
			// debugger; //eslint-disable-line
			var oDataModel = this.byId("tabLateFee").getModel("LateFee"),
				oPayload = this.byId("dLateFee").getModel("Payload").getData(),
				obj = {
					"sNo": 0,
					"returnType": "RET-1",
					"gstin": oPayload.dataSecAttrs.GSTIN[0],
					"taxPeriod": oPayload.taxPeriod,
					"returnTable": "",
					"igstInterest": 0,
					"cgstInterest": 0,
					"sgstInterest": 0,
					"cessInterest": 0,
					"cgstLateFee": 0,
					"sgstLateFee": 0,
					"userDefined1": "",
					"userDefined2": "",
					"userDefined3": "",
					"edit": true,
					"visi": true
				};
			if (oDataModel) {
				var oData = oDataModel.getData(),
					aReturnTable = ["6(1)", "6(2)", "6(3)", "6(4)"],
					arr = [];
				for (var i = 0; i < oData.length; i++) {
					if (aReturnTable.includes(oData[i].returnTable)) {
						arr.push(oData[i].returnTable);
					}
				}
				var result = aReturnTable.filter(function (n) {
					return !this.has(n);
				}, new Set(arr)); //eslint-disable-line

				if (result.length > 0) {
					obj.sNo = (!oData[oData.length - 1] ? 1 : oData[oData.length - 1].sNo + 1); // oData[oData.length - 1].sNo + 1;
					obj.returnTable = result[0] || aReturnTable[(oData.length % 4)];
					oData.push(obj);
				}
				oDataModel.refresh(true);
			}
		},

		/**
		 * Method call to delete selected data from Late Fee
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onDeleteLateFee: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabLateFee").getSelectedIndices(),
				oDataModel = this.byId("tabLateFee").getModel("LateFee"),
				oData = oDataModel.getData();

			for (var i = aIndices.length - 1; i > -1; i--) {
				oData.splice(aIndices[i], 1);
			}
			oDataModel.refresh(true);
			this.byId("tabLateFee").setSelectedIndex(-1);
		},

		/**
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @return {Object} oPayload Save Payload object
		 */
		_payloadSaveLateFee: function () {
			var oLateFeeData = this.byId("tabLateFee").getModel("LateFee").getData(),
				aIndices = this.byId("tabLateFee").getSelectedIndices(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndices.length; i++) {
				var data = oLateFeeData[aIndices[i]];
				oPayload.req.push({
					"id": data.id || null,
					"sNo": data.sNo,
					"returnType": data.returnType,
					"gstin": data.gstin,
					"taxPeriod": data.taxPeriod,
					"returnTable": data.returnTable,
					"igstInterest": data.igstInterest || 0,
					"cgstInterest": data.cgstInterest || 0,
					"sgstInterest": data.sgstInterest || 0,
					"cessInterest": data.cessInterest || 0,
					"cgstLateFee": data.cgstLateFee || 0,
					"sgstLateFee": data.sgstLateFee || 0,
					"userDefined1": data.userDefined1 || null,
					"userDefined2": data.userDefined2 || null,
					"userDefined3": data.userDefined3 || null
				});
			}
			return oPayload;
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveLateFee: function () {
			debugger; //eslint-disable-line
			var that = this,
				oPayload = this._payloadSaveLateFee();

			sap.m.MessageBox.confirm("Do you want to save selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/ret1IntLateFeeUpdate.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							var oProperty = that.byId("dLateFee").getModel("Property"),
								payload = {
									"req": that.byId("dLateFee").getModel("Payload").getData()
								};
							that.byId("tabLateFee").setSelectedIndex(-1);
							oProperty.getData().enabled = false;
							oProperty.getData().edit = false;
							oProperty.refresh(true);
							that._getLateFeeData(payload);
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact"
								});
							}
						}).fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
						});
					}
				}
			});
		},
		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 */
		onCloseLateFee: function () {
			this._oDialogLateFee.close();
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressTaxPayment: function (oEvent) {
			if (!this._oDialogTaxPayment) {
				this._oDialogTaxPayment = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.TaxPayment", this);
				this.getView().addDependent(this._oDialogTaxPayment);
			}
			var vData = Formatter.periodFormat(this.byId("dtSummary").getValue()),
				oProp = {
					"enabled": false,
					"edit": false
				},
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
						}
					}
				};
			this.byId("dTaxPayment").setModel(new JSONModel(oProp), "Property");
			this.byId("dTaxPayment").setModel(new JSONModel(oPayload.req), "Payload"); // Payload data to get Data
			this.byId("txtTaxEntity").setText(this.getOwnerComponent().getModel("userPermission").getData().respData.entityName);
			this.byId("txtTaxGstin").setText(this.byId("slRet1Gstin").getSelectedKeys()[0]);
			this.byId("txtTaxPeriod").setText(vData);
			this._getTaxPaymentData(oPayload);
			this._oDialogTaxPayment.open();
		},

		/**
		 * Method called to get Interest & Late Fee data from table
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_getTaxPaymentData: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			that._clearTableModel("TaxPayment", "tabPaymentOfTax");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1PaymentTax.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
					data.resp[i].visi = false;
				}
				that.byId("tabPaymentOfTax").setModel(new JSONModel(data.resp), "TaxPayment");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditTaxPayment: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to edit selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabPaymentOfTax").getSelectedIndices(),
							oDataModel = that.byId("tabPaymentOfTax").getModel("TaxPayment"),
							oData = oDataModel.getData();

						for (var i = 0; i < aIndices.length; i++) {
							var obj = oData[aIndices[i]];
							obj.edit = true;
						}
						oDataModel.refresh(true);
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddTaxPayment: function () {
			// debugger; //eslint-disable-line
			var oDataModel = this.byId("tabPaymentOfTax").getModel("TaxPayment"),
				oPayload = this.byId("dTaxPayment").getModel("Payload").getData(),
				obj = {
					"sNo": 0,
					"returnType": "RET-1",
					"gstin": oPayload.dataSecAttrs.GSTIN[0],
					"taxPeriod": oPayload.taxPeriod,
					"desc": "",
					"taxPayableRc": 0,
					"taxPayableOtherRc": 0,
					"taxPaidRc": 0,
					"taxPaidOtherRc": 0,
					"adjLiabilityRc": 0,
					"adjLiabilityOtherRc": 0,
					"itcPaidIgst": 0,
					"itcPaidCgst": 0,
					"itcPaidSgst": 0,
					"itcPaidCess": 0,
					"cashPaidTax": 0,
					"cashPaidInterest": 0,
					"cashPaidLateFee": 0,
					"userDefined1": "",
					"userDefined2": "",
					"userDefined3": "",
					"edit": true,
					"visi": true
				};
			if (oDataModel) {
				var oData = oDataModel.getData(),
					aDesc = ["igst", "cgst", "sgst", "cess"],
					arr = [];

				for (var i = 0; i < oData.length; i++) {
					if (!arr.includes(oData[i].desc)) {
						arr.push(oData[i].desc);
					}
				}
				var result = aDesc.filter(function (n) {
					return !this.has(n);
				}, new Set(arr)); //eslint-disable-line

				if (result.length > 0) {
					obj.sNo = !oData[oData.length - 1] ? 1 : oData[oData.length - 1].sNo + 1;
					obj.desc = result[0] || aDesc[(oData.length % 4)];
					oData.push(obj);
				}
				oDataModel.refresh(true);
			}
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onDeleteTaxPayment: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabPaymentOfTax").getSelectedIndices(),
				oDataModel = this.byId("tabPaymentOfTax").getModel("TaxPayment"),
				oData = oDataModel.getData();

			for (var i = aIndices.length - 1; i > -1; i--) {
				oData.splice(aIndices[i], 1);
			}
			oDataModel.refresh(true);
			this.byId("tabPaymentOfTax").setSelectedIndex(-1);
		},

		/**
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} indices Array of selected index
		 * @return {Object} oPayload Save Payload object
		 */
		_payloadSaveTaxPayment: function (indices) { //eslint-disable-line
			var oTaxData = this.byId("tabPaymentOfTax").getModel("TaxPayment").getData(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < indices.length; i++) {
				var data = oTaxData[indices[i]];
				oPayload.req.push({
					"id": data.id || null,
					"sNo": data.sNo,
					"returnType": data.returnType,
					"gstin": data.gstin,
					"taxPeriod": data.taxPeriod,
					"desc": data.desc || null,
					"taxPayableRc": data.taxPayableRc || 0,
					"taxPayableOtherRc": data.taxPayableOtherRc || 0,
					"taxPaidRc": data.taxPaidRc || 0,
					"taxPaidOtherRc": data.taxPaidOtherRc || 0,
					"adjLiabilityRc": data.adjLiabilityRc || 0,
					"adjLiabilityOtherRc": data.adjLiabilityOtherRc || 0,
					"itcPaidIgst": data.itcPaidIgst || 0,
					"itcPaidCgst": data.itcPaidCgst || 0,
					"itcPaidSgst": data.itcPaidSgst || 0,
					"itcPaidCess": data.itcPaidCess || 0,
					"cashPaidTax": data.cashPaidTax || 0,
					"cashPaidInterest": data.cashPaidInterest || 0,
					"cashPaidLateFee": data.cashPaidLateFee || 0,
					"userDefined1": data.userDefined1 || null,
					"userDefined2": data.userDefined2 || null,
					"userDefined3": data.userDefined3 || null
				});
			}
			return oPayload;
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveTaxPayment: function () {
			debugger; //eslint-disable-line
			var that = this,
				aIndices = this.byId("tabPaymentOfTax").getSelectedIndices(),
				oPayload = this._payloadSaveTaxPayment(aIndices);

			sap.m.MessageBox.confirm("Do you want to save selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/ret1PaymentUpdate.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							var oProperty = that.byId("dTaxPayment").getModel("Property"),
								payload = {
									"req": that.byId("dTaxPayment").getModel("Payload").getData()
								};
							that.byId("tabPaymentOfTax").setSelectedIndex(-1);
							oProperty.getData().enabled = false;
							oProperty.getData().edit = false;
							oProperty.refresh(true);
							that._getTaxPaymentData(payload);
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact"
								});
							}
						}).fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
						});
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onCloseTaxPayment: function () {
			this._oDialogTaxPayment.close();
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressRefundSummary: function (oEvent) {
			// debugger; //eslint-disable-line
			if (!this._oDialogRefund) {
				this._oDialogRefund = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1.RefundClaim", this);
				this.getView().addDependent(this._oDialogRefund);
			}
			var vData = Formatter.periodFormat(this.byId("dtSummary").getValue()),
				oProp = {
					"enabled": false,
					"edit": false
				},
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID], // [this.byId("slRet1Entity").getSelectedKey()],
						"taxPeriod": this.byId("dtSummary").getValue(),
						"dataSecAttrs": {
							"GSTIN": this.byId("slRet1Gstin").getSelectedKeys()
						}
					}
				};
			this.byId("dRefundClaim").setModel(new JSONModel(oProp), "Property");
			this.byId("dRefundClaim").setModel(new JSONModel(oPayload.req), "Payload"); // Payload data to get Data
			this.byId("txtRefundEntity").setText(this.getOwnerComponent().getModel("userPermission").getData().respData.entityName);
			this.byId("txtRefundGstin").setText(this.byId("slRet1Gstin").getSelectedKeys()[0]);
			this.byId("txtRefundPeriod").setText(vData);
			this._getRefundData(oPayload);
			this._oDialogRefund.open();
		},

		/**
		 * Method called to get Interest & Late Fee data from table
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oPayload Payload object
		 */
		_getRefundData: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			that._clearTableModel("RefundClaim", "tabRefundClaim");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/ret1Refund.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
					data.resp[i].visi = false;
				}
				that.byId("tabRefundClaim").setModel(new JSONModel(data.resp), "RefundClaim");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditRefundClaim: function () {
			// debugger; //eslint-disable-line
			var that = this;
			sap.m.MessageBox.confirm("Do you want to edit selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						var aIndices = that.byId("tabRefundClaim").getSelectedIndices(),
							oDataModel = that.byId("tabRefundClaim").getModel("RefundClaim"),
							oData = oDataModel.getData();

						for (var i = 0; i < aIndices.length; i++) {
							var obj = oData[aIndices[i]];
							obj.edit = true;
						}
						oDataModel.refresh(true);
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddRefundClaim: function () {
			// debugger; //eslint-disable-line
			var oDataModel = this.byId("tabRefundClaim").getModel("RefundClaim"),
				oPayload = this.byId("dRefundClaim").getModel("Payload").getData(),
				obj = {
					"sNo": 0,
					"gstin": oPayload.dataSecAttrs.GSTIN[0],
					"taxPeriod": oPayload.taxPeriod,
					"desc": "",
					"tax": 0,
					"interest": 0,
					"penalty": 0,
					"fee": 0,
					"other": 0,
					"total": 0,
					"userDefined1": "",
					"userDefined2": "",
					"userDefined3": "",
					"edit": true,
					"visi": true
				};
			if (oDataModel) {
				var oData = oDataModel.getData(),
					aDesc = ["igst", "cgst", "sgst", "cess"],
					arr = [];

				for (var i = 0; i < oData.length; i++) {
					if (aDesc.includes(oData[i].desc)) {
						arr.push(oData[i].desc);
					}
				}
				var result = aDesc.filter(function (n) {
					return !this.has(n);
				}, new Set(arr)); //eslint-disable-line

				if (result.length > 0) {
					obj.sNo = !oData[oData.length - 1] ? 1 : oData[oData.length - 1].sNo + 1;
					obj.desc = result[0] || aDesc[(oData.length % 4)];
					oData.push(obj);
				}
				oDataModel.refresh(true);
			}
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onDeleteRefundClaim: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabRefundClaim").getSelectedIndices(),
				oDataModel = this.byId("tabRefundClaim").getModel("RefundClaim"),
				oData = oDataModel.getData();

			for (var i = aIndices.length - 1; i > -1; i--) {
				oData.splice(aIndices[i], 1);
			}
			oDataModel.refresh(true);
			this.byId("tabRefundClaim").setSelectedIndex(-1);
		},

		/**
		 * Developed by: Bharat Gupta on 29.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @return {Object} oPayload Save Payload object
		 */
		_payloadSaveRefund: function () {
			var oRefundData = this.byId("tabRefundClaim").getModel("RefundClaim").getData(),
				aIndices = this.byId("tabRefundClaim").getSelectedIndices(),
				oPayload = {
					"req": []
				};
			for (var i = 0; i < aIndices.length; i++) {
				var data = oRefundData[aIndices[i]];
				oPayload.req.push({
					"id": data.id || null,
					"sNo": data.sNo,
					"gstin": data.gstin,
					"taxPeriod": data.taxPeriod,
					"desc": data.desc || null,
					"tax": data.tax || 0,
					"interest": data.interest || 0,
					"penalty": data.penalty || 0,
					"fee": data.fee || 0,
					"other": data.other || 0,
					"total": data.total || 0,
					"userDefined1": data.userDefined1 || null,
					"userDefined2": data.userDefined2 || null,
					"userDefined3": data.userDefined3 || null
				});
			}
			return oPayload;
		},

		/**
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveRefundClaim: function () {
			debugger; //eslint-disable-line
			var that = this,
				oPayload = this._payloadSaveRefund();

			sap.m.MessageBox.confirm("Do you want to save selected records?", {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.OK) {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/ret1RefundUpdate.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							var oProperty = that.byId("dRefundClaim").getModel("Property"),
								payload = {
									"req": that.byId("dRefundClaim").getModel("Payload").getData()
								};
							that.byId("tabRefundClaim").setSelectedIndex(-1);
							oProperty.getData().enabled = false;
							oProperty.getData().edit = false;
							oProperty.refresh(true);
							that._getRefundData(payload);
							if (data.hdr.status === "S") {
								sap.m.MessageBox.success(that.getResourceBundle().getText("msgSavedSuccess"), {
									styleClass: "sapUiSizeCompact"
								});
							}
						}).fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
						});
					}
				}
			});
		},

		/**
		 * Developed by: Bharat Gupta on 23.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 */
		onCloseRefundClaim: function () {
			this._oDialogRefund.close();
		},

		/**
		 * Called when user change amount format to Absolute, Lakhs, Crores, Millions, Billions
		 * Developed by: Bharat Gupta on 12.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onDisplayRupeesValue: function (oEvent) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			var vKey = oEvent.getParameter("item").getKey();
			this.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(vKey));
			switch (vKey) {
			case "A":
				var div = 1;
				break;
			case "L":
				div = 100000;
				break;
			case "C":
				div = 10000000;
				break;
			case "M":
				div = 1000000;
				break;
			case "B":
				div = 1000000000;
				break;
			}
			var oModel = this.byId("dpProcessRet1").getModel("ProcessedModel");
			if (oModel) {
				var oData = oModel.getData(),
					aData = [];
				for (var i = 0; i < oData.length; i++) {
					var data = {};
					data.state = oData[i].state;
					data.gstin = oData[i].gstin;
					data.regType = oData[i].regType;
					data.authToken = oData[i].authToken;
					data.status = oData[i].status;
					data.timeStamp = oData[i].timeStamp;
					data.liability = this.amt2decimal(oData[i].liability / div);
					data.revCharge = this.amt2decimal(oData[i].revCharge / div);
					data.otherCharge = this.amt2decimal(oData[i].otherCharge / div);
					data.itc = this.amt2decimal(oData[i].itc / div);
					data.tds = this.amt2decimal(oData[i].tds / div);
					data.tcs = this.amt2decimal(oData[i].tcs / div);
					aData.push(data);
				}
				this.byId("tabProcessRet1").setModel(new JSONModel(aData), "ProcessedRecord");
			}
		},

		/**
		 * Developed by: Bharat Gupta - 31.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onPressClear: function (oEvent) {
			debugger; //eslint-disable-line
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (oEvent.getSource().getId().includes("bProcessClear")) {
				var vPage = "dpProcessRet1",
					// vEntity = "slEntity",
					vGstin = "slGstin",
					vPeriod = "dtProcessed";
			} else {
				vPage = "dpSummaryRet1";
				// vEntity = "slRet1Entity";
				vGstin = "slRet1Gstin";
				vPeriod = "dtSummary";
			}
			var oData = this.getOwnerComponent().getModel("EntityModel").getData(),
				oSecurityModel = this.byId(vPage).getModel("DataSecurity");
			oSecurityModel.setData(oData[0]);
			oSecurityModel.refresh(true);

			// this.getView().byId(vEntity).setSelectedKey(oData[0].entityId);
			this.getView().byId(vGstin).setSelectedKeys([]);
			this.byId(vPeriod).setMaxDate(vDate);
			this.byId(vPeriod).setDateValue(vDate);
		},

		/**
		 * Developed by: Bharat Gupta - 12.12.2019
		 * @memberOf com.ey.digigst.view.RET
		 * @private
		 * @param {stritn} value Value to be get Key value
		 * @return {string} vKey
		 */
		_tableNumber: function (value) { //eslint-disable-line
			switch (value) {
			case "OUTWARD SUPPLIES":
				var vKey = "outwardSupply";
				break;
			case "Inward Supplies":
				vKey = "rcInwardSupplie";
				break;
			case "DR/CR Notes":
				vKey = "drCrNotes";
				break;
			case "SUPPLIES":
				vKey = "supplies3d";
				break;
			case "Total Value":
				vKey = "TotalValue3e";
				break;
			case "B2B":
				vKey = "b2bRC";
				break;
			case "Prior Period":
				vKey = "priorPeriod";
				break;
			case "ADV REC":
				vKey = "advReciev";
				break;
			case "ADV ADJ":
				vKey = "advAdjust";
				break;
			case "OutWard Supplies":
				vKey = "RevOutward";
				break;
			case "SEZ DTA":
				vKey = "sezDta";
				break;
			case "Eligibe Credit":
				vKey = "eligibleCredit";
				break;
			case "Rev":
				vKey = "";
				break;
			case "Details Of Reversals":
				vKey = "";
				break;
			default:
				vKey = value;
				break;
			}
			return vKey;
		},

		onPressDownloadExcel: function (oEvent) {
			// debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"type": null,
					"entityId": [$.sap.entityID],
					"taxPeriod": null,
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
			if (oEvent.getSource().getId().includes("bExportExcel")) {
				var vUrl = "/aspsapapi/downloadRet1ProcessedReport.do",
					aIndices = this.byId("tabProcessRet1").getSelectedIndices(),
					oData = this.byId("tabProcessRet1").getModel("ProcessedRecord").getData();

				oPayload.req.type = "ret1ProcessedSummary";
				// oPayload.req.entityId.push($.sap.entityID); // this.byId("slEntity").getSelectedKey());
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();
				if (aIndices.length > 0) {
					for (var i = 0; i < aIndices.length; i++) {
						oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndices[i]].gstin);
					}
				} else {
					oPayload.req.dataSecAttrs.GSTIN = this.byId("slGstin").getSelectedKeys();
				}
			} else {
				vUrl = "/aspsapapi/downloadRet1RSReports.do";
				oPayload.req.type = "ret1ReviewSummary";
				// oPayload.req.entityId.push(this.byId("slRet1Entity").getSelectedKey());
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
				oPayload.req.dataSecAttrs.GSTIN = this.byId("slRet1Gstin").getSelectedKeys();
			}
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onPressDownloadPdf: function () {
			debugger; //eslint-disable-line
		},

		/**
		 * Method call to synchronize asp data to GSTN poratl data
		 * Developed by: Bharat Gupta - 24.02.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} view View type (i.e. Summary Screen, Dialog)
		 */
		onUpdateGstnData: function (oEvent, view) {
			// debugger; //eslint-disable-line
			var that = this,
				oBundle = this.getResourceBundle(),
				oPayload = {
					"req": []
				};
			if (view === "S") {
				var aGstin = this.byId("slRet1Gstin").getSelectedKeys(),
					vDate = this.byId("dtSummary").getValue();
				if (aGstin.length === 0) {
					sap.m.MessageBox.information(oBundle.getText("msgSelectOneGstin"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				for (var i = 0; i < aGstin.length; i++) {
					oPayload.req.push({
						"gstin": aGstin[i],
						"rtnprd": vDate
					});
				}
			} else {
				var payload = this.byId("dDifference").getModel("Payload").getData();
				oPayload.req.push({
					"gstin": payload.gstin,
					"rtnprd": payload.taxPeriod
				});
			}
			sap.ui.core.BusyIndicator.hide();
			$.ajax({
				method: "POST",
				url: "/aspsapapi/RetGstnGetSection.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oPropModel = that.getView().getModel("Ret1Property");
				if (data.hdr.status === "S") {
					oPropModel.getData().updateDate = data.resp.updatedDate;
					sap.m.MessageBox.success(oBundle.getText("msgUpdateData"), {
						styleClass: "sapUiSizeCompact"
					});
				}
				oPropModel.refresh(true);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta - 26.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} vDocTypeText
		 */
		formatDocType: function (value) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			if (value) {
				var vKey = this._tableNumber(value),
					vDocTypeText = oBundle.getText(vKey);
				return vDocTypeText;
			}
			return value;
		},

		/**
		 * Called to format amount value
		 * Developed by: Bharat Gupta on 12.12.2019
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {string} vAmount Amount to convert
		 * @return {number} amount
		 */
		amt2decimal: function (vAmount) {
			var amount = vAmount.toString();
			if (amount.indexOf(".") > -1 && amount.indexOf("e-") === -1) {
				return amount.substring(0, amount.indexOf(".") + 3);
			} else if (amount.indexOf("e-") > -1) {
				return 0;
			}
			return vAmount;
		},

		/**
		 * Method called to format ASP Table values
		 * Developed by: Bharat Gupta - 08.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} vTabValue
		 */
		aspValueFormat: function (value) {
			// debugger; //eslint-disable-line
			var vTabValue;
			switch (value) {
			case "A3":
				vTabValue = "3A";
				break;
			case "B3":
				vTabValue = "3B";
				break;
			case "C3":
				vTabValue = "3C";
				break;
			case "D3":
				vTabValue = "3D";
				break;
			case "E3":
				vTabValue = "3E";
				break;
			case "A4":
				vTabValue = "4A";
				break;
			case "B4":
				vTabValue = "4B";
				break;
			case "C4":
				vTabValue = "4C";
				break;
			case "D4":
				vTabValue = "4D";
				break;
			case "E4":
				vTabValue = "4E";
				break;
			default:
				vTabValue = value;
			}
			return vTabValue;
		},

		/**
		 * Method called to format Tax Decription
		 * Developed by: Bharat Gupta - 01.02.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {string} vValue Value to be format
		 * @return {string} Tax decription
		 */
		taxDescription: function (vValue) {
			if (vValue) {
				var oData = this.getOwnerComponent().getModel("Returns").getData().gst;
				for (var k in oData) {
					if (vValue === oData[k].key) {
						break;
					}
				}
				return oData[k].text;
			}
			return null;
		},

		// _amountValue: function (vValue) {
		// 	var oCurrObj = {
		// 		style: "currency",
		// 		currency: "INR"
		// 	};
		// 	var amount = Number(vValue).toLocaleString("en-IN", oCurrObj);
		// 	return amount;
		// },

		/**
		 * Method called to clear Table data
		 * Developed by: Bharat Gupta - 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 * @param {string} vModel Model name
		 * @param {string} vTab Table name
		 */
		_clearTableModel: function (vModel, vTab) {
			if (vTab) {
				var oModel = this.byId(vTab).getModel(vModel);
			} else {
				oModel = this.getView().getModel(vModel);
			}
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
		}
	});
});