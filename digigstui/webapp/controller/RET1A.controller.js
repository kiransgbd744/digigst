sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage"
], function (BaseController, Formatter, JSONModel, Storage) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.RET1A", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.RET1A
		 */
		onInit: function () {
			var that = this,
				vDate = new Date();

			// 			Processed Records
			this.byId("dtProcessed").setMaxDate(vDate);
			this.byId("dtProcessed").setDateValue(vDate);

			this.byId("dRet1aSummary").setMaxDate(vDate);
			this.byId("dRet1aSummary").setDateValue(vDate);

			this.byId("dpProcessRet1a").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtProcessed").$().find("input").attr("readonly", true);
				}
			});
			this.byId("dRet1aSummary").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dRet1aSummary").$().find("input").attr("readonly", true);
				}
			});

			// var oStorage = new Storage(Storage.Type.session, "digiGst");
			// var oEntityData = oStorage.get("entity");
			// if (!oEntityData) {
			// 	this.getRouter().navTo("Home");
			// } else {
			// 	var oModel = this.getOwnerComponent().getModel("EntityModel");
			// 	oModel.setData(oEntityData);
			// 	oModel.refresh(true);
			// 	this._getDataSecurity(oEntityData[0].entityId, "dpProcessRet1a");
			// }
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.RET1A
		 */
		onAfterRendering: function () {
			// debugger; //eslint-disable-line
			this._getProcessRecords(this._processPayload());
		},

		/**
		 * Developed by: Bharat Gupta on 03.01.2020
		 * Called to get Data Security data based on selected Entity
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onEntityChange: function (oEvent) {
			if (oEvent.getSource().getId().includes("slEntity")) {
				var vPage = "dpProcessRet1a";

			} else if (oEvent.getSource().getId().includes("slRet1aEntity")) {
				vPage = "dpRet1aSummary";
			}
			var vEntityId = oEvent.getSource().getSelectedKey();
			this._getDataSecurity(vEntityId, vPage);
		},

		/**
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object 
		 */
		onSearch: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("bProcessGo")) {
				this._getProcessRecords(this._processPayload());

			} else if (oEvent.getSource().getId().includes("bSummaryGo")) {
				this._getSummaryData(this._summaryPayload());
			}
		},

		/** 
		 * Called to get Ret1 Process Records search Payload
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {string} vGstin Selected GSTIN
		 * @return {Object} oPayload
		 */
		_processPayload: function (vGstin) {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], // [this.byId("slEntity").getSelectedKey()],
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slGstin").getSelectedKeys()
					}
				}
			};
			if (vGstin) {
				oPayload.req.dataSecAttrs.GSTIN = [vGstin];
			}
			return oPayload;
		},

		/**
		 * Call to get Ret-1A Processed Records data
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oPayload Payload structure
		 */
		_getProcessRecords: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this,
				oModel = that.byId("tabProcessRet1a").getModel("ProcessedRecord");

			if (oModel) {
				oModel.setData(null);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getRet1AProcessedRecords.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oBundle = that.getResourceBundle();
				that.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText("A"));
				that.byId("tabProcessRet1a").setModel(new JSONModel(data.resp), "ProcessedRecord");
				that.byId("dpProcessRet1a").setModel(new JSONModel(data.resp), "ProcessedModel");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method triggered when user click on Back button to navigate back to process screen
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressBack: function () {
			this.byId("dpProcessRet1a").setVisible(true);
			this.byId("dpRet1aSummary").setVisible(false);
		},

		/**
		 * Called when click on Gstin link in process screen to get summary of selected Gstin
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressRet1aSummary: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();

			this.byId("dpProcessRet1a").setVisible(false);
			this.byId("dpRet1aSummary").setVisible(true);

			// this.byId("slRet1aEntity").setSelectedKey(this.byId("slEntity").getSelectedKey());
			this.byId("dRet1aSummary").setDateValue(this.byId("dtProcessed").getDateValue());
			// this._getDataSecurity(this.byId("slEntity").getSelectedKey(), "dpRet1aSummary");
			this.byId("slRet1aGstin").setSelectedKeys(obj.gstin);

			var oVisiCol = {
				"computed": true,
				"usrInput": true,
				"gstn": true,
				"diff": true,
				"compChk": false,
				"userChk": false,
				"gstnChk": false,
				"diffChk": false
			};
			this.getView().setModel(new JSONModel(oVisiCol), "VisiProp");

			this._getSummaryData(this._summaryPayload(obj.gstin));
		},

		/** 
		 * Called to get Ret1 Summary search Payload
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {string} vGstin Gstin number
		 * @return {Object} oPayload
		 */
		_summaryPayload: function (vGstin) {
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], // [this.byId("slRet1aEntity").getSelectedKey()],
					"taxPeriod": this.byId("dRet1aSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slRet1aGstin").getSelectedKeys()
					}
				}
			};
			if (vGstin) {
				oPayload.req.dataSecAttrs.GSTIN = [vGstin];
			}
			return oPayload;
		},

		/**
		 * Called to get Summary data for selected Gstin
		 * Developed by: Bharat Gupta on 03.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
				url: "/aspsapapi/ret1aSummary.do",
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
		 * Called to bind Ret1a Summary data in table
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} data Response object
		 */
		_bindSummary: function (data) {
			// debugger; //eslint-disable-line
			var oSummaryRet1 = {
				"aspValues": [],
				"lateFee": [],
				"taxPayment": []
			};
			this._jsonAspValues(oSummaryRet1.aspValues, data.aspValues);
			oSummaryRet1.lateFee.push(this._jsonLateFee(data.lateFee.tab_5));
			oSummaryRet1.taxPayment.push(this._jsonTaxPayment(data.taxPayment.tab_6));
			this.byId("dpRet1aSummary").setModel(new JSONModel(oSummaryRet1), "SummaryRet1a");
		},

		/**
		 * Called to get JSON structure for ASP Values to format & bind to Summary table
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oSummary Summary object
		 * @param {Object} data Response object
		 */
		_jsonAspValues: function (oSummary, data) {
			// debugger; //eslint-disable-line
			var aTable = ["A3", "B3", "C3", "D3", "E3", "A4", "B4", "C4"];

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
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} data Response object
		 * @return {Object} data
		 */
		_linkAspValues: function (data) {
			// debugger; //eslint-disable-line
			var aTable = ["3A4", "3C1", "3C4", "3D1", "3D2", "3D3", "3D4", "4A5", "4B1", "4B2"];
			data.tableSection = data.section;
			data.link = false;
			data.edit = false;

			if (data.section && aTable.includes(data.table)) {
				data.link = true;
			}
			return data;
		},

		/**
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressNilReturn: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onRequestApproval: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressDeleteData: function () {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSaveGstn: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressSubmit: function () {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSignNFile: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onGenerateRet1a: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * Developed by: Bharat Gupta on 02.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSaveStatus: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				that = this,
				oPayload = {
					"req": {
						"returnType": "ret1a",
						"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
						"gstin": obj.gstin,
						"taxPeriod": this.byId("dtProcessed").getValue()
					}
				};
			if (!this._oDialogSaveStatus) {
				this._oDialogSaveStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.SaveStatus", this);
				this.byId("dpProcessRet1a").addDependent(this._oDialogSaveStatus);
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
			this._oDialogSaveStatus.open();
		},

		/**
		 * Method called get Save Status after clicking on Go button in Save Status dialog to get data from api and bind it to table
		 * Developed by: Bharat Gupta on 21.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressStatusGo: function () {
			debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"returnType": "ret1a",
					"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
					"gstin": this.byId("slGstinStats").getSelectedKey(),
					"taxPeriod": this.byId("dtProcessed").getValue()
				}
			};
			this.getSaveStauts(oPayload);
		},

		/**
		 * Called to close Save Status dialog
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseSaveStatus: function () {
			this._oDialogSaveStatus.close();
		},

		/**
		 * Called to open Difference dialog
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDifference: function (oEvent) {
			// debugger; //eslint-disable-line
			if (!this._oDialogDifference) {
				this._oDialogDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.Difference", this);
				this.getView().addDependent(this._oDialogDifference);
			}
			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject(),
				oPayload = {
					"req": {
						"returnType": "ret1a",
						"entityId": $.sap.entityID, // this.byId("slEntity").getSelectedKey(),
						"gstin": obj.gstin,
						"taxPeriod": this.byId("dtProcessed").getValue()
					}
				};
			this._oDialogDifference.open();
			this.getDifferenceData(oPayload);
		},

		/**
		 * Called to close difference dialog
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseDifference: function () {
			this._oDialogDifference.close();
		},

		/**
		 * Called display Process Records in Absolute, Lakhs, Crores, Millions, Billions format
		 * Develped by: Bharat Gupta on 02.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
			var oModel = this.byId("dpProcessRet1a").getModel("ProcessedModel");
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
					aData.push(data);
				}
				this.byId("tabProcessRet1a").setModel(new JSONModel(aData), "ProcessRecord");
			}
		},

		/**
		 * Called to Downlaad Reports
		 * Developed by: Bharat Gupta on 
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDownloadReport: function (oEvent) {
			debugger; //eslint-disable-line	
		},

		/**
		 * Called to Download Process data in Excel format
		 * Developed by: Bharat Gupta on 
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onExportExcel: function (oEvent) {
			debugger; //eslint-disable-line	
		},

		/**
		 * Called to Downlaad Process data in PDF format
		 * Developed by: Bharat Gupta on 
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onExportPdf: function (oEvent) {
			debugger; //eslint-disable-line	
		},

		/**
		 * Method triggered when user select/Unselect Check Box to see table data
		 * Developed by: Bharat Gupta on 02.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onSelectCheckBox: function () {
			var oVisiModel = this.getView().getModel("VisiProp"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.computed && !oVisiData.usrInput && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.compChk = true;

			} else if (!oVisiData.computed && oVisiData.usrInput && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.userChk = true;

			} else if (!oVisiData.computed && !oVisiData.usrInput && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.gstnChk = true;

			} else if (!oVisiData.computed && !oVisiData.usrInput && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.diffChk = true;

			} else {
				oVisiData.compChk = false;
				oVisiData.userChk = false;
				oVisiData.gstnChk = false;
				oVisiData.diffChk = false;
			}
			oVisiModel.refresh(true);
		},

		/**
		 * Method called to Expand/collapse all table records
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressExpandCollapse: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("bExpand")) {
				var oModel = this.byId("tabAspValues").getModel("SummaryRet1a");
				if (oModel) {
					this.byId("tabAspValues").expandToLevel(1);
					this.byId("tabAspValues").setVisibleRowCount(26);
					this.byId("tabInterest").expandToLevel(1);
					this.byId("tabInterest").setVisibleRowCount(5);
					this.byId("tabTaxPayment").expandToLevel(1);
					this.byId("tabTaxPayment").setVisibleRowCount(5);
				}
			} else {
				this._collapseAllTables();
			}
		},

		/**
		 * Method called to collapse all table records
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		_collapseAllTables: function () {
			this.byId("tabAspValues").collapseAll();
			this.byId("tabAspValues").setVisibleRowCount(8);
			this.byId("tabInterest").collapseAll();
			this.byId("tabInterest").setVisibleRowCount(1);
			this.byId("tabTaxPayment").collapseAll();
			this.byId("tabTaxPayment").setVisibleRowCount(1);
		},

		/**
		 * Method triggered when user click on icon to expand/collapse select record in Table
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressToggle: function (oEvent) {
			// debugger; //eslint-disable-line
			var aData = oEvent.getSource().getBinding().getModel("SummaryRet1a").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "aspValues") {
				var vRowCount = 8;
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
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		/**
		 * Method triggered to Clear User Data when user click on Clear User Data button
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressClearData: function () {
			debugger; //eslint-disable-line
		},

		/**
		 * Method triggered to Save Changes made by user
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressSaveChange: function () {
			debugger; //eslint-disable-line
		},

		onAspSegmentChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var oPropModel = this.getView().getModel("DialogProp"),
				oDataProp = oPropModel.getData();

			if (oEvent.getSource().getSelectedKey() === "summary") {
				oDataProp.summary = true;
				oDataProp.segment = "summary";
			} else {
				oDataProp.summary = false;
				oDataProp.segment = "data";
			}
			oPropModel.refresh(true);
		},

		/**
		 * 
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressAspValues: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("SummaryRet1a").getObject(),
				aTable = ["3A4", "3C1", "4A5", "4B1", "4B2"];

			if (aTable.includes(obj.table)) {
				this._dialogAspValues(obj);
			} else {
				this._dialogVerticalData(obj);
			}
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} obj Object
		 */
		_dialogAspValues: function (obj) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			if (!this._oDialogLiability) {
				this._oDialogLiability = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.AspValues", this);
				this.getView().addDependent(this._oDialogLiability);
			}
			this.byId("iAspValueTitle").setText(oBundle.getText(obj.supplyType));
			this.byId("txtValuesEntity").setText($.sap.entityID); // (this.byId("slRet1aEntity")._getSelectedItemText());
			this.byId("txtValuesGstin").setText(this.byId("slRet1aGstin").getSelectedKeys()[0]);
			this.byId("txtValuesPeriod").setText(Formatter.periodFormat(this.byId("dRet1aSummary").getValue()));
			this._oDialogLiability.open();
		},

		/**
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} obj Object
		 */
		_dialogVerticalData: function (obj) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			if (!this._oDialogAspVertical) {
				this._oDialogAspVertical = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.AspVertical", this);
				this.getView().addDependent(this._oDialogAspVertical);
			}
			var oProp = {
				"summary": true,
				"segment": "summary"
			};
			this.getView().setModel(new JSONModel(oProp), "DialogProp");
			this.byId("iVerticalTitle").setText(oBundle.getText(obj.supplyType));
			this.byId("txtVerticalEntity").setText($.sap.entityID); // (this.byId("slRet1aEntity")._getSelectedItemText());
			this.byId("txtVerticalGstin").setText(this.byId("slRet1aGstin").getSelectedKeys()[0]);
			this.byId("txtVerticalPeriod").setText(Formatter.periodFormat(this.byId("dRet1aSummary").getValue()));
			this._oDialogAspVertical.open();
		},

		/**
		 * Method trigger to close Tax Payment dialog
		 * Developed by: Bhart Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseAspValues: function () {
			this._oDialogLiability.close();
		},

		/**
		 * Method trigger to close Tax Payment dialog
		 * Developed by: Bhart Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseAspVertical: function () {
			this._oDialogAspVertical.close();
		},

		/**
		 * Method trigger to open Interest & Late Fee dialog when user click on link in Interest & Late Fee Table data
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressLateFee: function () {
			debugger; //eslint-disable-line
			if (!this._oDialogLateFee) {
				this._oDialogLateFee = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.LateFee", this);
				this.getView().addDependent(this._oDialogLateFee);
			}
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], // [this.byId("slRet1aEntity").getSelectedKey()],
					"taxPeriod": this.byId("dRet1aSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slRet1aGstin").getSelectedKeys()
					}
				}
			};
			this.byId("txtLateFeeEntity").setText($.sap.entityID); // (this.byId("slRet1aEntity")._getSelectedItemText());
			this.byId("txtLateFeeGstin").setText(this.byId("slRet1aGstin").getSelectedKeys()[0]);
			this.byId("txtLateFeePeriod").setText(Formatter.periodFormat(this.byId("dRet1aSummary").getValue()));
			this._getLateFeeData(oPayload);
			this._oDialogLateFee.open();
		},

		/**
		 * Method called to get Interest & Late Fee data from table
		 * Developed by: Bharat Gupta on 11.01.2020
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
				url: "/aspsapapi/ret1ALateFeeDetail.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
				}
				that.byId("tabLateFee").setModel(new JSONModel(data.resp), "LateFee");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method call to Edit selected Late Fee records
		 * Developed by: Bharat Gupta on 11.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditLateFee: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabLateFee").getSelectedIndices(),
				oDataModel = this.byId("tabLateFee").getModel("LateFee"),
				oData = oDataModel.getData();

			for (var i = 0; i < aIndices.length; i++) {
				var obj = oData[aIndices[i]];
				obj.edit = true;
			}
			oDataModel.refresh(true);
		},

		/**
		 * Method call to Add new records in table
		 * Developed by: Bharat Gupta on 11.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddLateFee: function () {
			// debugger; //eslint-disable-line
			var oDataModel = this.byId("tabLateFee").getModel("LateFee"),
				oData = oDataModel.getData(),
				obj = {
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
					"edit": true
				};
			oData.push(obj);
			oDataModel.refresh(true);
		},

		/**
		 * Method call to delete selected data from Late Fee
		 * Developed by: Bharat Gupta on 11.01.2020
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
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveLateFee: function () {
			// debugger; //eslint-disable-line
		},

		/**
		 * Method trigger to close Interest & Late Fee dialog
		 * Developed by: Bhart Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseLateFee: function () {
			this._oDialogLateFee.close();
		},

		/**
		 * Method triggered to open Tax Payment dialog
		 * Developed by: Bharat Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onPressTaxPayment: function () {
			debugger; //eslint-disable-line
			if (!this._oDialogTaxPayment) {
				this._oDialogTaxPayment = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.ret1a.TaxPayment", this);
				this.getView().addDependent(this._oDialogTaxPayment);
			}
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID], // [this.byId("slRet1aEntity").getSelectedKey()],
					"taxPeriod": this.byId("dRet1aSummary").getValue(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slRet1aGstin").getSelectedKeys()
					}
				}
			};
			this.byId("txtTaxEntity").setText($.sap.entityID); // (this.byId("slRet1aEntity")._getSelectedItemText());
			this.byId("txtTaxGstin").setText(this.byId("slRet1aGstin").getSelectedKeys()[0]);
			this.byId("txtTaxPeriod").setText(Formatter.periodFormat(this.byId("dRet1aSummary").getValue()));
			this._getTaxPaymentData(oPayload);
			this._oDialogTaxPayment.open();
		},

		/**
		 * Method called to get Interest & Late Fee data from table
		 * Developed by: Bharat Gupta on 11.01.2020
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
				url: "/aspsapapi/ret1APaymentTax.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					data.resp[i].edit = false;
				}
				that.byId("tabPaymentOfTax").setModel(new JSONModel(data.resp), "TaxPayment");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta on 11.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onEditTaxPayment: function () {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabPaymentOfTax").getSelectedIndices(),
				oDataModel = this.byId("tabPaymentOfTax").getModel("TaxPayment"),
				oData = oDataModel.getData();

			for (var i = 0; i < aIndices.length; i++) {
				var obj = oData[aIndices[i]];
				obj.edit = true;
			}
			oDataModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta on 11.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onAddTaxPayment: function () {
			// debugger; //eslint-disable-line
			var oDataModel = this.byId("tabPaymentOfTax").getModel("TaxPayment"),
				oData = oDataModel.getData(),
				obj = {
					"desc": "",
					"taxPayableRc": 0,
					"taxPayableOtherRc": 0,
					"taxPaidRc": 0,
					"taxPaidOtherRc": 0,
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
					"edit": true
				};
			oData.push(obj);
			oDataModel.refresh(true);
		},

		/**
		 * Developed by: Bharat Gupta on 11.01.2020
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
		 * Developed by: Bharat Gupta on 10.01.2020
		 * @memberOf com.ey.digigst.view.RET1
		 * @private
		 */
		onSaveTaxPayment: function () {
			debugger; //eslint-disable-line
		},

		/**
		 * Method trigger to close Tax Payment dialog
		 * Developed by: Bhart Gupta on 06.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 */
		onCloseTaxPayment: function () {
			this._oDialogTaxPayment.close();
		},

		/**
		 * Method Called to update data from GSTN server
		 * Developed by: Bharat Gupta on 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressUpdateGstn: function (oEvent) {
			debugger; //eslint-disable-line
		},

		/**
		 * Method called to restore default value to all
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressClear: function (oEvent) {
			// debugger; //eslint-disable-line	
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (oEvent.getSource().getId().includes("bProcessClear")) {
				// var vPage = "dpProcessRet1a",
				// vEntity = "slEntity",
				var vGstin = "slGstin",
					vPeriod = "dtProcessed";
			} else {
				// vPage = "dpRet1aSummary";
				// vEntity = "slRet1aEntity";
				vGstin = "slRet1aGstin";
				vPeriod = "dRet1aSummary";
			}
			// var oData = this.getOwnerComponent().getModel("EntityModel").getData(),
			// 	oSecurityModel = this.byId(vPage).getModel("DataSecurity");
			// oSecurityModel.setData(oData[0]);
			// oSecurityModel.refresh(true);

			// this.getView().byId(vEntity).setSelectedKey(oData[0].entityId);
			this.getView().byId(vGstin).setSelectedKeys([]);
			this.byId(vPeriod).setMaxDate(vDate);
			this.byId(vPeriod).setDateValue(vDate);
		},

		/**
		 * Method called to format field description
		 * Developed by: Bharat Gupta - 04.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} vDocTypeText
		 */
		formatDocType: function (value) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			if (value) {
				return oBundle.getText(value);
			}
			return value;
		},

		/**
		 * Called to format amount value
		 * Developed by: Bharat Gupta on 12.12.2019
		 * @memberOf com.ey.digigst.view.RET1A
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
		 * Developed by: Bharat Gupta - 08.01.2020
		 * @memberOf com.ey.digigst.view.RET1A
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
			default:
				vTabValue = value;
			}
			return vTabValue;
		},

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