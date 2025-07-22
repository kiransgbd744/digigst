sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage",
	"sap/m/MessageBox"
], function (BaseController, Formatter, JSONModel, Storage, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ANX1", {
		formatter: Formatter,

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

			// var oStorage = new Storage(Storage.Type.session, "digiGst");
			// var oEntityData = oStorage.get("entity");
			// if (!oEntityData) {
			// 	this.getRouter().navTo("Home");
			// } else {
			// 	var oModel = this.getOwnerComponent().getModel("EntityModel");
			// 	oModel.setData(oEntityData);
			// 	oModel.refresh(true);
			// 	this._getDataSecurity(oEntityData[0].entityId, "dpProcessRecord");
			// }

			var oBundle = this.getResourceBundle();
			var oVisiCol = {
				"colText": oBundle.getText("supplies"),
				"colVisi": true,
				"outward": true,
				"inward": true
			};
			this.byId("dpProcessRecord").setModel(new JSONModel(oVisiCol), "Visi");

			var oVisiSumm = {
				"summary": true,
				"details": false
			};
			this.byId("dpAnx1Summary").setModel(new JSONModel(oVisiSumm), "VisiSummary");

			/* Added by Vinay - 13.09.2019 */
			this.byId("dtCounterParty").setMaxDate(vDate);
			this.byId("dtCounterParty").setDateValue(vDate);

			this.byId("dtTaxRecon").setMaxDate(vDate);
			this.byId("dtTaxRecon").setDateValue(vDate);

			this.byId("dtRecSumm").setMaxDate(vDate);
			this.byId("dtRecSumm").setDateValue(vDate);

			this.byId("dtResponseSum").setMaxDate(vDate);
			this.byId("dtResponseSum").setDateValue(vDate);

			this.byId("dtResponseDtl").setMaxDate(vDate);
			this.byId("dtResponseDtl").setDateValue(vDate);

			this.byId("dtSummary1").setMaxDate(vDate);
			this.byId("dtSummary1").setDateValue(vDate);

			this.byId("recpSummGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("recpSummGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("recpSummPAN").addEventDelegate({
				onAfterRendering: function () {
					that.byId("recpSummPAN").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtRecSumm").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtRecSumm").$().find("input").attr("readonly", true);
				}
			});
			this.byId("recpSummRGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("recpSummRGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseSumGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseSumGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseSumPAN").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseSumPAN").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseSumRGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseSumRGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("dtResponseSum").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtResponseSum").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseDtlGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseDtlGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseDtlPAN").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseDtlPAN").$().find("input").attr("readonly", true);
				}
			});
			this.byId("responseDtlRGstin").addEventDelegate({
				onAfterRendering: function () {
					that.byId("responseDtlRGstin").$().find("input").attr("readonly", true);
				}
			});
			this.byId("RecptDtlUpldDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("RecptDtlUpldDate").$().find("input").attr("readonly", true);
				}
			});
			this.byId("dtResponseDtl").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtResponseDtl").$().find("input").attr("readonly", true);
				}
			});
		},

		onAfterRendering: function () {
			this._getProcessedData();
			this._getCounterParty();
			this._getTaxRecon();
			this.onSrSummSEntyChange();
			this.onRecpSumGo();
		},

		/*===================================================================*/
		/*====== IconTab Bar Selection handle ===============================*/
		/*===================================================================*/
		handleIconTabBarSelect: function (oEvent) {
			var Key = this.byId("itbAnx1").getSelectedKey();
			if (Key === "CounterParty") {
				//this.onCPEntityChange();
				this._getCounterParty();

			} else if (Key === "recon") {
				//this.onTaxRecnEntyChng();
				this._getTaxRecon();

			} else if (Key === "summary") {
				this.onSrSummSEntyChange();
				this.onRecpSumGo();
			}
		},

		/**
		 * Method called to get Data Security attribure when user change entity
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent eventing object
		 */
		onEntityChange: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("slEntitySave")) {
				var vPage = "dpProcessRecord";

			} else if (oEvent.getSource().getId().includes("slSummEntity")) {
				vPage = "dpAnx1Summary";
				this.onEntitySummChange();
			}
			var vEntityId = oEvent.getSource().getSelectedKey();
			this._getDataSecurity(vEntityId, vPage);
		},

		/**
		 * Called when user pressed search button to get data from database
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent eventing object
		 */
		onSearch: function (oEvent) {
			if (oEvent.getSource().getId().includes("bProcessGo")) {
				this._getProcessedData();

			} else if (oEvent.getSource().getId().includes("bSummaryGo")) {
				this._getSummary();

			} else if (oEvent.getSource().getId().includes("bCpGo")) { //fbCounterParty
				this._getCounterParty();

			} else if (oEvent.getSource().getId().includes("bReconGo")) { //fbTaxRecon
				this._getTaxRecon();

			} else if (oEvent.getSource().getId().includes("fbRecptSummary")) {
				this._getReceiptSummary();

			}
		},

		/**
		 * Processed Records Operations
		 * ----------------------------
		 * Called when user change view type
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing Object
		 */
		onSelectChange: function (oEvent) {
			var oBundle = this.getResourceBundle();
			var oModel = this.byId("dpProcessRecord").getModel("Visi");
			var oData = oModel.getData();
			var vKey = oEvent.getSource().getSelectedKey();
			switch (vKey) {
			case "outward":
				oData.colText = oBundle.getText("outColTxt");
				oData.colVisi = false;
				oData.outward = true;
				oData.inward = false;
				break;
			case "inward":
				oData.colText = oBundle.getText("inColTxt");
				oData.colVisi = false;
				oData.outward = false;
				oData.inward = true;
				break;
			default:
				oData.colText = oBundle.getText("supplies");
				oData.colVisi = true;
				oData.outward = true;
				oData.inward = true;
			}
			oModel.refresh(true);
		},

		/** 
		 * Called for Processed Records Search Criteria
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @return {Object} searchInfo
		 */
		_getProcessSearchInfo: function () {
			// var aEntity = [];
			// aEntity.push(this.byId("slEntitySave").getSelectedKey());
			var searchInfo = {
				"req": {
					"entityId": [$.sap.entityID], // aEntity,
					"taxPeriod": this.byId("dtProcessed").getValue(),
					"gstnUploadDate": this.byId("slUploadGstnDt").getSelectedKey(),
					"dataSecAttrs": {
						"GSTIN": this.byId("slGstinSave").getSelectedKeys()
					}
				}
			};
			return searchInfo;
		},

		/** 
		 * Called to get Processed Records datab
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {boolean} flag Flag to get Adapt filter value in searchInfo
		 */
		_getProcessedData: function () {
			// debugger; //eslint-disable-line
			var that = this;
			// var vEntity = this.byId("slEntitySave").getSelectedKey();
			if (!$.sap.entityID) {
				this._infoMessage(this.getResourceBundle().getText("msgNoData"));
				return;
			}
			var searchInfo = this._getProcessSearchInfo();
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpProcessRecord");
			}
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.byId("tabProcessRecord").getModel("ProcessRecord");
			if (oModel) {
				oModel.setData(null);
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getProcessedRecords.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oBundle = that.getResourceBundle();
				that.byId("bRupeesView").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText("A"));
				that.byId("tabProcessRecord").setModel(new JSONModel(data.resp), "ProcessRecord");
				that.byId("dpProcessRecord").setModel(new JSONModel(data.resp), "ProcessedModel");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method called to open dialog & get data for Save Status
		 * Developed by: Bharat Gupta on 13.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressSaveStatus: function (oEvent) {
			// debugger; //eslint-disable-line
			var that = this,
				oPayload = {
					"req": {
						"returnType": "anx1",
						"entityId": $.sap.entityID,
						"gstin": null,
						"taxPeriod": null
					}
				};
			if (!this._oDialogSaveStats) {
				this._oDialogSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx1.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStats);
				this.byId("dtSaveStats").setMaxDate(new Date());
				this.byId("dtSaveStats").addEventDelegate({
					onAfterRendering: function () {
						that.byId("dtSaveStats").$().find("input").attr("readonly", true);
					}
				});
			}
			if (oEvent.getSource().getId().includes("bSaveStatus")) {
				// var oDataSecurity = this.byId("dpProcessRecord").getModel("DataSecurity").getData(),
				var obj = oEvent.getSource().getBindingContext("ProcessRecord").getObject();

				// oPayload.req.entityId = this.byId("slEntitySave").getSelectedKey();
				oPayload.req.gstin = obj.gstin;
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();
			} else {
				// oDataSecurity = this.byId("dpAnx1Summary").getModel("DataSecurity").getData();
				// oPayload.req.entityId = this.byId("slSummEntity").getSelectedKey();
				oPayload.req.gstin = this.byId("slSummGstin").getSelectedKeys()[0];
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
			}
			// this.byId("dSaveStatus").setModel(new JSONModel(oDataSecurity), "DataSecurity");
			this.byId("dSaveStatus").setModel(new JSONModel(oPayload.req), "Fields");
			this.getSaveStauts(oPayload);
			this._oDialogSaveStats.open();
		},

		/**
		 * Develped by: Bharat Gupta on 16.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onPressStatusGo: function () {
			var oPayload = {
				"req": this.byId("dSaveStatus").getModel("Fields").getData()
			};
			this.getSaveStauts(oPayload);
		},

		/**
		 * Called to close Save Status Dialog
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onCloseSaveStatus: function () {
			this._oDialogSaveStats.close();
		},

		/**
		 * Called to display difference data for gstin
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDifference: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessRecord").getObject(),
				oPayload = {
					"req": {
						"returnType": "anx1",
						"entityId": $.sap.entityID, // this.byId("slEntitySave").getSelectedKey(),
						"gstin": obj.gstin,
						"taxPeriod": this.byId("dtProcessed").getValue()
					}
				};
			if (!this._oDialogDifference) {
				this._oDialogDifference = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx1.Difference", this);
				this.getView().addDependent(this._oDialogDifference);
			}
			this.byId("dDifference").setModel(new JSONModel(oPayload.req), "Payload");
			this.getDifferenceData(oPayload);
			this._oDialogDifference.open();
		},

		/**
		 * Method called to Navigate to Invoice Managament screen to display details
		 * Developed by: Bharat Gupta on 14.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {string} refId Reference Id	
		 */
		onPressStatusLink: function (refId) {
			// debugger; //eslint-disable-line
			var oData = this.byId("dSaveStatus").getModel("Fields").getData(),
				oFilterData = {
					"dataType": "outward",
					"criteria": "RETURN_DATE_SEARCH",
					"status": "E",
					"validation": "BV"
				};
			oFilterData.segment = "gstn";
			oFilterData.returnType = "anx1";
			oFilterData.entity = $.sap.entityID; // oData.entityId;
			oFilterData.gstin = oData.gstin;
			oFilterData.fromDate = oFilterData.toDate = this.byId("dtSaveStats").getDateValue();
			oFilterData.refId = refId;
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvoiceManage");
		},

		/**
		 * Called to close Difference Dialog
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onCloseDifferenceDialog: function () {
			this._oDialogDifference.close();
		},

		/**
		 * get Processed Records Summary
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing Parameter
		 */
		onProcessSummary: function (oEvent) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getBindingContext("ProcessRecord").getObject(),
				oVisiModel = this.byId("dpAnx1Summary").getModel("VisiSummary");

			oVisiModel.getData().summary = true;
			oVisiModel.getData().details = false;
			oVisiModel.refresh(true);

			this.byId("sbSummary").setSelectedKey("summary");
			this.byId("sbDetails").setSelectedKey("summary");

			this.byId("itbAnx1").setVisible(false);
			this.byId("dpAnx1Summary").setVisible(true);
			this.byId("iPanelProcess").setVisible(true);
			this.byId("iPanelCP").setVisible(false);

			// this.byId("slSummEntity").setSelectedKey(this.byId("slEntitySave").getSelectedKey());
			this.byId("dtSummary").setDateValue(this.byId("dtProcessed").getDateValue());
			// this._getDataSecurity(this.byId("slEntitySave").getSelectedKey(), "dpAnx1Summary");
			this.byId("slSummGstin").setSelectedKeys(obj.gstin);

			var oVisible = {
				"asp": true,
				"memo": true,
				"gstn": true,
				"diff": true,
				"eAsp": false,
				"eMemo": false,
				"eGstn": false,
				"eDiff": false
			};
			this.getView().setModel(new JSONModel(oVisible), "visiSummAnx1");

			var searchInfo = this._getSummarySearchInfo(obj.gstin);
			this._getProcessSummary(searchInfo);
		},

		/**
		 * Called to get Payload for Summary data search
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {string} vGstin Selected Gstin
		 * @return {Object} oSearchInfo
		 */
		_getSummarySearchInfo: function (vGstin) {
			var oSearchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
			// oSearchInfo.req.entityId.push(this.byId("slSummEntity").getSelectedKey());
			if (vGstin) {
				oSearchInfo.req.dataSecAttrs.GSTIN.push(vGstin);
			} else {
				oSearchInfo.req.dataSecAttrs.GSTIN = this.byId("slSummGstin").getSelectedKeys();
			}
			return oSearchInfo;
		},

		/**
		 * Ajax Call to get Process Records Summary
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} searchInfo Payload to get data
		 */
		_getProcessSummary: function (searchInfo) {
			// debugger; //eslint-disable-line
			var oSummaryHeader = this.byId("dpAnx1Summary").getModel("SummaryHeader"),
				oSummaryModel = this.byId("dpAnx1Summary").getModel("ProcessSummary"),
				oDetailHeader = this.byId("dpAnx1Summary").getModel("DetailHeader"),
				oDetailModel = this.byId("dpAnx1Summary").getModel("ProcessSummaryDet");

			if (oSummaryModel) {
				oSummaryModel.setData(null);
				oSummaryModel.refresh(true);
			}
			if (oSummaryHeader) {
				oSummaryHeader.setData(null);
				oSummaryHeader.refresh(true);
			}
			if (oDetailModel) {
				oDetailModel.setData(null);
				oDetailModel.refresh(true);
			}
			if (oDetailHeader) {
				oDetailHeader.setData(null);
				oDetailHeader.refresh(true);
			}
			this._processSummary(searchInfo);
			this._processSummaryDetails(searchInfo);
			this._approvalStatus();
		},

		/**
		 * Call Process Summary Api to get Summary data 
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} searchInfo Payload object
		 */
		_processSummary: function (searchInfo) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/annexure1TotalSummary.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpAnx1Summary").setModel(new JSONModel(data.resp), "ProcessSummary");
				that._bindSummaryTotal(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Calculate & Bind Summary Header data
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Response object data
		 */
		_bindSummaryTotal: function (data) {
			// debugger; //eslint-disable-line
			var oProcSummary = this._summaryJson(),
				oSummryHeader = {};

			for (var i = 0; i < data.outward.length; i++) {
				this._processSum(data.outward[i], oProcSummary);
			}
			oSummryHeader.outward = oProcSummary;
			oProcSummary = this._summaryJson();
			for (i = 0; i < data.inward.length; i++) {
				this._processSum(data.inward[i], oProcSummary);
			}
			oSummryHeader.inward = oProcSummary;
			this.byId("dpAnx1Summary").setModel(new JSONModel(oSummryHeader), "SummaryHeader");
		},

		/**
		 * Call Process Summary Detail Api to get Details data 
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} searchInfo Payload object
		 */
		_processSummaryDetails: function (searchInfo) {
			var that = this;
			this.byId("slDocTypeAnx1").setSelectedKeys(null);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/annexure1Summary.do",
				data: JSON.stringify(searchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				// sap.ui.core.BusyIndicator.hide();
				that.byId("dpAnx1Summary").setModel(new JSONModel(data.resp), "SummaryDetails");
				that._bindSummaryDetails(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called after getting Processed records from api and bind data to Summary tables
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Response object
		 */
		_bindSummaryDetails: function (data) {
			// debugger; //eslint-disable-line
			var aOutward = ["b2c", "b2b", "expt", "expwt", "sezt", "sezwt", "deemExpt"],
				aInward = ["rev", "imps", "impg", "impgSez"],
				oProcSummary = this._summaryJson(),
				oDetailHeader = {},
				oSummary = {
					"outward": [],
					"inward": [],
					"ecom": []
				};
			for (var i = 0; i < aOutward.length; i++) {
				var oSummJson = this._summaryJson();
				oSummJson.docType = aOutward[i];

				for (var j = 0; j < data.outward[aOutward[i]].length; j++) {
					if (data.outward[aOutward[i]][j].docType === "total") {
						this._summaryTotal(data.outward[aOutward[i]][j], oSummJson);
						this._processSum(data.outward[aOutward[i]][j], oProcSummary);
					} else {
						oSummJson.items.push(data.outward[aOutward[i]][j]);
					}
				}
				oSummary.outward.push(oSummJson);
			}
			oDetailHeader.outward = oProcSummary;
			oProcSummary = this._summaryJson();
			for (i = 0; i < aInward.length; i++) {
				oSummJson = this._summaryJson();
				oSummJson.docType = aInward[i];

				for (j = 0; j < data.inward[aInward[i]].length; j++) {
					if (data.inward[aInward[i]][j].docType === "total") {
						this._summaryTotal(data.inward[aInward[i]][j], oSummJson);
						this._processSum(data.inward[aInward[i]][j], oProcSummary);
					} else {
						oSummJson.items.push(data.inward[aInward[i]][j]);
					}
				}
				oSummary.inward.push(oSummJson);
			}
			oDetailHeader.inward = oProcSummary;

			oProcSummary = data.ecom.table4[0];
			oProcSummary.docType = "ecom";
			oSummary.ecom.push(oProcSummary);
			this.byId("dpAnx1Summary").setModel(new JSONModel(oDetailHeader), "DetailHeader");

			this.byId("dpAnx1Summary").setModel(new JSONModel(oSummary), "ProcessSummaryDet");
			this.byId("tabOutward").setVisibleRowCount(15);
			this.byId("tabInward").setVisibleRowCount(15);
			// this.byId("slDocTypeAnx1").setSelectedKey("all");
		},

		/**
		 * Called to move data in header object for Process Sumamry
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Response object
		 * @param {Object} oSummJson Object to bind data in table
		 * @param {string} table Table Name
		 */
		_summaryTotal: function (data, oSummJson, table) {
			// oSummJson.docType = table;
			oSummJson.eyCount = data.eyCount;
			oSummJson.eyInvoiceValue = data.eyInvoiceValue;
			oSummJson.eyTaxableValue = data.eyTaxableValue;
			oSummJson.eyTaxPayble = data.eyTaxPayble;
			oSummJson.eyIgst = data.eyIgst;
			oSummJson.eyCgst = data.eyCgst;
			oSummJson.eySgst = data.eySgst;
			oSummJson.eyCess = data.eyCess;

			oSummJson.memoCount = data.memoCount;
			oSummJson.memoInvoiceValue = data.memoInvoiceValue;
			oSummJson.memoTaxableValue = data.memoTaxableValue;
			oSummJson.memoTaxPayble = data.memoTaxPayble;
			oSummJson.memoIgst = data.memoIgst;
			oSummJson.memoCgst = data.memoCgst;
			oSummJson.memoSgst = data.memoSgst;
			oSummJson.memoCess = data.memoCess;

			oSummJson.gstnCount = data.gstnCount;
			oSummJson.gstnInvoiceValue = data.gstnInvoiceValue;
			oSummJson.gstnTaxableValue = data.gstnTaxableValue;
			oSummJson.gstnTaxPayble = data.gstnTaxPayble;
			oSummJson.gstnIgst = data.gstnIgst;
			oSummJson.gstnCgst = data.gstnCgst;
			oSummJson.gstnSgst = data.gstnSgst;
			oSummJson.gstnCess = data.gstnCess;

			oSummJson.diffCount = data.diffCount;
			oSummJson.diffInvoiceValue = data.diffInvoiceValue;
			oSummJson.diffTaxableValue = data.diffTaxableValue;
			oSummJson.diffTaxPayble = data.diffTaxPayble;
			oSummJson.diffIgst = data.diffIgst;
			oSummJson.diffCgst = data.diffCgst;
			oSummJson.diffSgst = data.diffSgst;
			oSummJson.diffCess = data.diffCess;
		},

		/**
		 * Called to calculate sum of all tables
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Response object
		 * @param {Object} oSumProcess Object to bind data in table
		 */
		_processSum: function (data, oSumProcess) {
			// debugger; //eslint-disable-line
			oSumProcess.eyCount += data.eyCount;
			oSumProcess.eyInvoiceValue += data.eyInvoiceValue;
			oSumProcess.eyTaxableValue += data.eyTaxableValue;
			oSumProcess.eyTaxPayble += data.eyTaxPayble;
			oSumProcess.eyIgst += data.eyIgst;
			oSumProcess.eyCgst += data.eyCgst;
			oSumProcess.eySgst += data.eySgst;
			oSumProcess.eyCess += data.eyCess;

			oSumProcess.memoCount += data.memoCount;
			oSumProcess.memoInvoiceValue += data.memoInvoiceValue;
			oSumProcess.memoTaxableValue += data.memoTaxableValue;
			oSumProcess.memoTaxPayble += data.memoTaxPayble;
			oSumProcess.memoIgst += data.memoIgst;
			oSumProcess.memoCgst += data.memoCgst;
			oSumProcess.memoSgst += data.memoSgst;
			oSumProcess.memoCess += data.memoCess;

			oSumProcess.gstnCount += data.gstnCount;
			oSumProcess.gstnInvoiceValue += data.gstnInvoiceValue;
			oSumProcess.gstnTaxableValue += data.gstnTaxableValue;
			oSumProcess.gstnTaxPayble += data.gstnTaxPayble;
			oSumProcess.gstnIgst += data.gstnIgst;
			oSumProcess.gstnCgst += data.gstnCgst;
			oSumProcess.gstnSgst += data.gstnSgst;
			oSumProcess.gstnCess += data.gstnCess;

			oSumProcess.diffCount += data.diffCount;
			oSumProcess.diffInvoiceValue += data.diffInvoiceValue;
			oSumProcess.diffTaxableValue += data.diffTaxableValue;
			oSumProcess.diffTaxPayble += data.diffTaxPayble;
			oSumProcess.diffIgst += data.diffIgst;
			oSumProcess.diffCgst += data.diffCgst;
			oSumProcess.diffSgst += data.diffSgst;
			oSumProcess.diffCess += data.diffCess;
		},

		/**
		 * Payload structrue to bind data in Review Summary screen
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @return {Object} JSON Object
		 */
		_summaryJson: function () {
			return {
				"tableNo": "",
				"eyCount": 0,
				"eyInvoiceValue": 0,
				"eyTaxableValue": 0,
				"eyTaxPayble": 0,
				"eyIgst": 0,
				"eyCgst": 0,
				"eySgst": 0,
				"eyCess": 0,
				"memoCount": 0,
				"memoInvoiceValue": 0,
				"memoTaxableValue": 0,
				"memoTaxPayble": 0,
				"memoIgst": 0,
				"memoCgst": 0,
				"memoSgst": 0,
				"memoCess": 0,
				"gstnCount": 0,
				"gstnInvoiceValue": 0,
				"gstnTaxableValue": 0,
				"gstnTaxPayble": 0,
				"gstnIgst": 0,
				"gstnCgst": 0,
				"gstnSgst": 0,
				"gstnCess": 0,
				"diffCount": 0,
				"diffInvoiceValue": 0,
				"diffTaxableValue": 0,
				"diffTaxPayble": 0,
				"diffIgst": 0,
				"diffCgst": 0,
				"diffSgst": 0,
				"diffCess": 0,
				"items": []
			};
		},

		/**
		 * Method called to get Approval status for Gstin
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		_approvalStatus: function () {
			// debugger; //eslint-disable-line
			var that = this,
				oPayload = {
					"req": {
						"gstin": this.byId("slSummGstin").getSelectedKeys()[0],
						"returnPeriod": this.byId("dtSummary").getValue()
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getApprovalStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.resp === undefined) {
					that.byId("txtReqSent").setText("");
					that.byId("txtStatus").setText("");
					that.byId("txtAppTime").setText("");
				} else {
					switch (data.resp.status) {
					case 0:
						var vStatus = "Approval Pending",
							styleCls = "statsPending";
						// vState = "Warning",
						break;
					case 1:
						vStatus = "Approved";
						styleCls = "statsApprove";
						// vState = "Success";
						break;
					case 2:
						vStatus = "Rejected";
						styleCls = "statsReject";
						// vState = "Error";
						break;
					}
					var arrInitiateDt = data.resp.initiatedOn.split("T");
					that.byId("txtReqSent").setText("Sent: " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
					that.byId("txtStatus").removeStyleClass("statsPending");
					that.byId("txtStatus").removeStyleClass("statsApprove");
					that.byId("txtStatus").removeStyleClass("statsReject");
					that.byId("txtStatus").setText(vStatus);
					that.byId("txtStatus").addStyleClass(styleCls);
					if (data.resp.approvedOn) {
						arrInitiateDt = data.resp.approvedOn.split("T");
						that.byId("txtAppTime").setText(": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
					} else {
						that.byId("txtAppTime").setText("");
					}
					// that.byId("txtStatus").setState(vState);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method called to get Process Summary Data from api
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		_getSummary: function () {
			var searchInfo = this._getSummarySearchInfo();
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpAnx1Summary");
			}
			this._getProcessSummary(searchInfo);
		},

		/**
		 * Called when user press on back button to go to Main screen from Review Summary
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressBack: function (oEvent) {
			this.byId("itbAnx1").setVisible(true);
			this.byId("dpAnx1Summary").setVisible(false);
		},

		/**
		 * Called when user click on Expand All/Collapse All button on Review summary
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressExpandCollapse: function (oEvent) {
			if (oEvent.getSource().getId().includes("bExpAnx1")) {
				if (this.byId("dpAnx1Summary").getModel("ProcessSummaryDet")) {
					this.byId("tabOutward").expandToLevel(1);
					this.byId("tabOutward").setVisibleRowCount(15);
					this.byId("tabInward").expandToLevel(1);
					this.byId("tabInward").setVisibleRowCount(15);
				}
			} else {
				this.byId("tabOutward").collapseAll();
				this.byId("tabOutward").setVisibleRowCount(7);
				this.byId("tabInward").collapseAll();
				this.byId("tabInward").setVisibleRowCount(4);
			}
		},

		/**
		 * Called when user press on toggle button to expand/collapse table data
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressToggle: function (oEvent) {
			// debugger; //eslint-disable-line
			var aData = oEvent.getSource().getBinding().getModel("ProcessSummaryDet").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];
			if (vModel === "outward") {
				var vRowCount = 7;
			} else {
				vRowCount = 4;
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
		 * Called to enabled/disable Save to Gstn button based on the row selection in process table
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onRowSelectionChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var aIndices = this.byId("tabProcessRecord").getSelectedIndices(),
				flag = false;

			if (aIndices.length > 0) {
				flag = true;
			}
			this.byId("bSaveToGstn").setEnabled(flag);
		},

		/**
		 * Modified by Bharat Gupta on 05.12.2019
		 * Called when user click on SaveToGstn button on Screen
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing Object
		 */
		onPressSaveToGstn: function (oEvent) {
			// debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"tables": [],
					"gstins": [],
					"retPeriod": ""
				}
			};
			if (oEvent.getSource().getId().includes("bSaveToGstn")) {
				var aIndices = this.byId("tabProcessRecord").getSelectedIndices(),
					aData = this.byId("tabProcessRecord").getModel("ProcessRecord").getData();

				if (aIndices.length !== 1) {
					sap.m.MessageBox.alert("Please select atleast one record", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oPayload.req.retPeriod = this.byId("dtProcessed").getValue();
				for (var i = 0; i < aIndices.length; i++) {
					oPayload.req.gstins.push(aData[aIndices[i]].gstin);
				}
			} else if (oEvent.getSource().getId().includes("bSaveGstn")) {
				var aGstin = this.byId("slSummGstin").getSelectedKeys();
				if (aGstin.length === 0) {
					sap.m.MessageBox.alert("Please select atleast one Gstin");
					return;
				}
				oPayload.req.gstins = aGstin;
				oPayload.req.retPeriod = this.byId("dtSummary").getValue();
			}
			if (!this._confirmSaveGstn) {
				this._confirmSaveGstn = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx1.ConfirmSave", this);
				this.getView().addDependent(this._confirmSaveGstn);
			}
			this.byId("dSaveGstn").setModel(new JSONModel(this._saveCheckStats(true)), "CheckStats");
			this.byId("dSaveGstn").setModel(new JSONModel(oPayload), "Payload");
			this._confirmSaveGstn.open();
		},

		/**
		 * Method call to get Save To Gstn Dialog Checkbox status (True/False)
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Boolean} flag Flag Value
		 * @return {Object} Checkbox Status
		 */
		_saveCheckStats: function (flag) {
			return {
				"b2c": flag,
				"b2b": flag,
				"expwp": flag,
				"sezwp": flag,
				"de": flag,
				"rev": flag,
				"imps": flag,
				"impg": flag,
				"impgsez": flag,
				"mis": flag,
				"ecom": flag,
				"select": flag,
				"partial": false,
				"saveEnable": flag
			};
		},

		/**
		 * Method Called when user Select/Unselect Tri-state Checkbox
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectTriState: function (oEvent) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				bStats = oEvent.getSource().getSelected(),
				oModel = this.byId("dSaveGstn").getModel("CheckStats");

			oModel.setData(this._saveCheckStats(bStats));
			if (!bStats) {
				oModel.getData().tooltip = oBundle.getText("msgSaveTableOne");
			}
			oModel.refresh(true);
		},

		/**
		 * Method called when user select/unselect Checkbox in Save Confirmation Dialog
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onSelectSaveCheck: function () {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle(),
				oModel = this.byId("dSaveGstn").getModel("CheckStats"),
				oData = oModel.getData();

			oData.select = oData.b2c || oData.b2b || oData.expwp || oData.sezwp || oData.de || oData.rev || oData.imps || oData.impg || oData.impgsez ||
				oData.mis || oData.ecom;
			oData.partial = !(oData.b2c && oData.b2b && oData.expwp && oData.sezwp && oData.de && oData.rev && oData.imps && oData.impg &&
				oData.impgsez &&
				oData.mis && oData.ecom);

			if (!oData.select) {
				oData.tooltip = oBundle.getText("msgSaveTableOne");
			} else {
				oData.tooltip = null;
			}
			oModel.refresh(true);
		},

		/**
		 * Method called to close Save Confirm Dialog
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onCloseSaveToGstn: function () {
			this._confirmSaveGstn.close();
		},

		/**
		 * Method called to Initiate Save To Gstn Process to save selected table data
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onSaveToGstnData: function () {
			// debugger; //eslint-disable-line
			var oData = this.byId("dSaveGstn").getModel("CheckStats").getData(),
				oPayload = this.byId("dSaveGstn").getModel("Payload").getData(),
				aTables = ["b2c", "b2b", "expwp", "sezwp", "de", "rev", "imps", "impg", "impgsez", "mis", "ecom"];

			for (var obj in oData) {
				if (oData[obj] && aTables.includes(obj)) {
					oPayload.req.tables.push(obj);
				}
				if (oData[obj] && obj === "expwp") {
					oPayload.req.tables.push("expwop");
				}
				if (oData[obj] && obj === "sezwp") {
					oPayload.req.tables.push("sezwop");
				}
			}
			this._saveToGstn(oPayload);
			this._confirmSaveGstn.close();
		},

		/**
		 * Modified by Bharat Gupta on 05.12.2019
		 * Trigger Ajax call for Save operation to Save selected GSTIN(s) to GSTN server
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oPayload Payload object to get data
		 */
		_saveToGstn: function (oPayload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/anx1SaveToGstnJob.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					sap.m.MessageBox.success("Save initiated for selected(active) GSTINs. Please review after 15 minutes.", {
						styleClass: "sapUiSizeCompact",
						onClose: function () {
							that._getProcessedData();
						}
					});
				} else {
					sap.m.MessageBox.error("Save failed for selected(active) GSTINs.");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called to open Additional filter dialog after pressing adapt filter button in screen
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressAdaptFilter: function (oEvent) {
			// debugger; //eslint-disable-line
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx1.AdaptAnx1", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			// if (oEvent.getSource().getId().includes("bProcessFilter")) {
			// 	var oModel = this.byId("dpProcessRecord").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bSummaryFilter")) {
			// 	oModel = this.byId("dpAnx1Summary").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bReconFilter")) {
			// 	oModel = this.byId("dpTaxRecon").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bCpFilter")) {
			// 	oModel = this.byId("dpCParty").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bRecptFilter")) {
			// 	oModel = this.byId("dpRecipientSRSumm").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bResDtlFilter")) {
			// 	oModel = this.byId("dpRecipntRespDtl").getModel("DataSecurity");

			// } else if (oEvent.getSource().getId().includes("bRespnsFilter")) {
			// 	oModel = this.byId("dpRecipntRespSumm").getModel("DataSecurity");
			// }

			// this.byId("dAdapt").setModel(oModel, "DataSecurity");
			this._oAdpatFilter.open();
		},

		/**
		 * Called when user close Additional filter dialog selecting Apply / Close
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing Parameter
		 */
		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				if (this.byId("itbAnx1").getVisible()) {
					if (this.byId("itbAnx1").getSelectedKey() === "process") {
						this._getProcessedData();
					} else if (this.byId("itbAnx1").getSelectedKey() === "CounterParty") {
						this._getCounterParty();
					} else if (this.byId("itbAnx1").getSelectedKey() === "recon") {
						this._getTaxRecon();
					}
				} else if (this.byId("dpAnx1Summary").getVisible()) {
					var searchInfo = this._getSummarySearchInfo();
					this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpAnx1Summary");

					if (this.byId("sbSummary").getSelectedKey() === "summary") {
						this._getProcessSummary(searchInfo);
					}
				}
			}
		},

		/**
		 * Called to add additional filter value in Request payload
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} search Object of SearchInfo
		 * @param {string} vPage Dynamic page ID
		 * @retrun
		 */
		_getOtherFilters: function (search, vPage) {
			// debugger; //eslint-disable-line
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items;
			//this.byId(vPage).getModel("DataSecurity").getData().items;

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
			if (oDataSecurity.purchOrg) {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg) {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel) {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("slUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("slUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("slUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("slUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("slUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("slUserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * Called when press on More/Less button to see additional consolidated details of GSTIN(s)
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressMoreLess: function (oEvent) {
			var vId = oEvent.getSource().getId();
			if (vId.includes("bSummMore") || vId.includes("bMore")) {
				var flag = true;

			} else if (vId.includes("bSummLess") || vId.includes("bLess")) {
				flag = false;
			}
			if (vId.includes("bSummMore") || vId.includes("bSummLess")) {
				this.getView().byId("outSummGstn").setVisible(flag);
				this.getView().byId("outSummDiff").setVisible(flag);
				this.getView().byId("inSummGstn").setVisible(flag);
				this.getView().byId("inSummDiff").setVisible(flag);
				this.getView().byId("bSummLess").setVisible(flag);
				this.getView().byId("bSummMore").setVisible(!flag);
			} else {
				this.getView().byId("outGstn").setVisible(flag);
				this.getView().byId("outDiff").setVisible(flag);
				this.getView().byId("inGstn").setVisible(flag);
				this.getView().byId("inDiff").setVisible(flag);
				this.getView().byId("bLess").setVisible(flag);
				this.getView().byId("bMore").setVisible(!flag);
			}
		},

		/**
		 * Called when user change amount format to Absolute, Lakhs, Crores, Millions, Billions
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onMenuItemSelect: function (oEvent) {
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
			var oModel = this.byId("dpProcessRecord").getModel("ProcessedModel");
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
					data.inCount = oData[i].inCount;
					data.inSupplies = this.amt2decimal(oData[i].inSupplies / div);
					data.inIgst = this.amt2decimal(oData[i].inIgst / div);
					data.inCgst = this.amt2decimal(oData[i].inCgst / div);
					data.inSgst = this.amt2decimal(oData[i].inSgst / div);
					data.inCess = this.amt2decimal(oData[i].inCess / div);
					data.outCount = oData[i].outCount;
					data.outSupplies = this.amt2decimal(oData[i].outSupplies / div);
					data.outIgst = this.amt2decimal(oData[i].outIgst / div);
					data.outCgst = this.amt2decimal(oData[i].outCgst / div);
					data.outSgst = this.amt2decimal(oData[i].outSgst / div);
					data.outCess = this.amt2decimal(oData[i].outCess / div);
					aData.push(data);
				}
				this.byId("tabProcessRecord").setModel(new JSONModel(aData), "ProcessRecord");
			}
		},

		/**
		 * Ajax call to get Request Approval
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressRequestForApproval: function (oEvent) {
			// debugger; //eslint-disable-line
			var that = this;
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID, // this.byId("slSummEntity").getSelectedKey(),
					"gstin": this.byId("slSummGstin").getSelectedKeys()[0],
					"returnPeriod": this.byId("dtSummary").getValue()
				}
			};
			sap.ui.core.BusyIndicator.hide();
			$.ajax({
				method: "POST",
				url: "/aspsapapi/requestForApproval.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					that.byId("txtReqSent").setText("Sent: " + data.resp.createdDate);
					sap.m.MessageBox.success("Request sent for Approval", {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called to clear filter value and table data to default
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressClear: function (oEvent) {
			// debugger; //eslint-disable-line
			var oBundle = this.getResourceBundle();
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("dAdapt")) {
				this.getView().byId("idDateRange").setValue("");
				this.getView().byId("slProfitCtr").clearSelection();
				this.getView().byId("slPlant").clearSelection();
				this.getView().byId("slDivision").clearSelection();
				this.getView().byId("slLocation").clearSelection();
				this.getView().byId("slSalesOrg").clearSelection();
				this.getView().byId("slPurcOrg").clearSelection();
				this.getView().byId("slDistrChannel").clearSelection();
				this.getView().byId("slUserAccess1").clearSelection();
				this.getView().byId("slUserAccess2").clearSelection();
				this.getView().byId("slUserAccess3").clearSelection();
				this.getView().byId("slUserAccess4").clearSelection();
				this.getView().byId("slUserAccess5").clearSelection();
				this.getView().byId("slUserAccess6").clearSelection();
			}
			if (oEvent.getSource().getId().includes("bProcessClear")) {
				var vPage = "dpProcessRecord",
					// vEntity = "slEntitySave",
					vGstin = "slGstinSave",
					vPeriod = "dtProcessed",
					oVisiCol = {
						"colText": oBundle.getText("supplies"),
						"colVisi": true,
						"outward": true,
						"inward": true
					};
				this.getView().byId("slViewRecord").setSelectedKey("all");
				var oModel = this.byId("dpProcessRecord").getModel("Visi");
				oModel.setData(oVisiCol);
				oModel.refresh(true);
			}
			var oData = this.getOwnerComponent().getModel("UserSecurity").getData();
			var oSecurityModel = this.byId(vPage).getModel("DataSecurity");
			oSecurityModel.setData(oData[0]);
			oSecurityModel.refresh(true);

			// this.getView().byId(vEntity).setSelectedKey(oData[0].entityId);
			this.getView().byId(vGstin).clearSelection();
			this.byId(vPeriod).setMaxDate(vDate);
			this.byId(vPeriod).setDateValue(vDate);
			if (oEvent.getSource().getId().includes("bProcessClear")) {
				this._getProcessedData();
			}
		},

		/**
		 * Called when user press on segment to see summary / details of selected GSTIN(s) in Review Summary Screen
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onButtonSelectionChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var oVisiModel = this.byId("dpAnx1Summary").getModel("VisiSummary");
			if (oEvent.getSource().getSelectedKey() === "summary") {
				oVisiModel.getData().details = false;
			} else {
				oVisiModel.getData().details = true;
			}
			oVisiModel.refresh(true);
		},

		/**
		 * Method called to select/unselect All/selected value in Filter Dropdown
		 * Developed by: Bharat Gupta on 03.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		_setFilterKey: function (oEvent) {
			var oSelectedKeys = oEvent.getSource().getSelectedKeys();
			if (oSelectedKeys.includes("all")) {
				if (oSelectedKeys[oSelectedKeys.length - 1] === "all") {
					oEvent.getSource().setSelectedItems(oEvent.getSource().getItems());

				} else if (oEvent.getSource().getItems().length - 1 === oSelectedKeys.length) {
					var vIdxAll = oSelectedKeys.indexOf("all");
					oSelectedKeys.splice(vIdxAll, 1);
					oEvent.getSource().setSelectedKeys(oSelectedKeys);
				}
			} else {
				if (oEvent.getSource().getItems().length - 1 === oSelectedKeys.length) {
					oEvent.getSource().setSelectedItems(null);
				} else {
					oEvent.getSource().setSelectedKeys(oSelectedKeys);
				}
			}
		},

		/**
		 * Called to move data in header object for Process Sumamry
		 * Developed by: Bharat Gupta on 03.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Response object
		 * @param {Object} oSummJson Object to bind data in table
		 */
		_subSummaryTotal: function (data, oSummJson) {
			oSummJson.eyCount += data.eyCount;
			oSummJson.eyInvoiceValue -= data.eyInvoiceValue;
			oSummJson.eyTaxableValue -= data.eyTaxableValue;
			oSummJson.eyTaxPayble -= data.eyTaxPayble;
			oSummJson.eyIgst -= data.eyIgst;
			oSummJson.eyCgst -= data.eyCgst;
			oSummJson.eySgst -= data.eySgst;
			oSummJson.eyCess -= data.eyCess;

			oSummJson.memoCount += data.memoCount;
			oSummJson.memoInvoiceValue -= data.memoInvoiceValue;
			oSummJson.memoTaxableValue -= data.memoTaxableValue;
			oSummJson.memoTaxPayble -= data.memoTaxPayble;
			oSummJson.memoIgst -= data.memoIgst;
			oSummJson.memoCgst -= data.memoCgst;
			oSummJson.memoSgst -= data.memoSgst;
			oSummJson.memoCess -= data.memoCess;

			oSummJson.gstnCount += data.gstnCount;
			oSummJson.gstnInvoiceValue -= data.gstnInvoiceValue;
			oSummJson.gstnTaxableValue -= data.gstnTaxableValue;
			oSummJson.gstnTaxPayble -= data.gstnTaxPayble;
			oSummJson.gstnIgst -= data.gstnIgst;
			oSummJson.gstnCgst -= data.gstnCgst;
			oSummJson.gstnSgst -= data.gstnSgst;
			oSummJson.gstnCess -= data.gstnCess;

			oSummJson.diffCount += data.diffCount;
			oSummJson.diffInvoiceValue -= data.diffInvoiceValue;
			oSummJson.diffTaxableValue -= data.diffTaxableValue;
			oSummJson.diffTaxPayble -= data.diffTaxPayble;
			oSummJson.diffIgst -= data.diffIgst;
			oSummJson.diffCgst -= data.diffCgst;
			oSummJson.diffSgst -= data.diffSgst;
			oSummJson.diffCess -= data.diffCess;
		},

		/**
		 * Called to filter Summary data based on Document Type dropdown filter value
		 * Developed by: Bharat Gupta on 03.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onFilterChange: function (oEvent) {
			// debugger; //eslint-disable-line
			// this._setFilterKey(oEvent);
			this.selectAll(oEvent);
			var aKey = oEvent.getSource().getSelectedKeys(),
				data = this.byId("dpAnx1Summary").getModel("SummaryDetails").getData(),
				vOutLength = 7,
				vInLength = 4;

			if (aKey.includes("all") || aKey.length === 0) {
				this._bindSummaryDetails(data);

			} else {
				var aOutward = ["b2c", "b2b", "expt", "expwt", "sezt", "sezwt", "deemExpt"],
					aInward = ["rev", "imps", "impg", "impgSez"],
					oSummary = {
						"outward": [],
						"inward": [],
						"ecom": []
					};
				for (var i = 0; i < aOutward.length; i++) {
					var oSummJson = this._summaryJson();
					oSummJson.docType = aOutward[i];
					this._filterSummaryData(aKey, data.outward[aOutward[i]], oSummJson);
					vOutLength += oSummJson.items.length;
					oSummary.outward.push(oSummJson);
				}
				for (i = 0; i < aInward.length; i++) {
					oSummJson = this._summaryJson();
					oSummJson.docType = aInward[i];
					this._filterSummaryData(aKey, data.inward[aInward[i]], oSummJson);
					vInLength += oSummJson.items.length;
					oSummary.inward.push(oSummJson);
				}
				oSummJson = data.ecom.table4[0];
				oSummJson.docType = "ecom";
				oSummary.ecom.push(oSummJson);

				this.byId("dpAnx1Summary").setModel(new JSONModel(oSummary), "ProcessSummaryDet");
				this.byId("tabOutward").setVisibleRowCount(vOutLength);
				this.byId("tabInward").setVisibleRowCount(vInLength);
				this._bindDetailHeader(oSummary);
			}
		},

		/**
		 * Developed by: Bharat Gupta on 03.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} aKey Filter Keys
		 * @param {Object} data Data to be filter
		 * @param {Object} oSummJson Result data object
		 */
		_filterSummaryData: function (aKey, data, oSummJson) {
			for (var j = 0; j < data.length; j++) {
				if (aKey.includes(data[j].docType)) {
					if (data[j].docType === "CR" || data[j].docType === "RCR") {
						this._subSummaryTotal(data[j], oSummJson);
					} else {
						this._processSum(data[j], oSummJson);
					}
					oSummJson.items.push(data[j]);
				}
			}
		},

		/**
		 * Developed by: Bharat Gupta on 03.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} data Data to calculate Header amount
		 */
		_bindDetailHeader: function (data) {
			// debugger; //eslint-disable-line
			var oModel = this.byId("dpAnx1Summary").getModel("DetailHeader"),
				oProcSummary = this._summaryJson(),
				oDetailHeader = {};

			for (var i = 0; i < data.outward.length; i++) {
				this._processSum(data.outward[i], oProcSummary);
			}
			oDetailHeader.outward = oProcSummary;
			oProcSummary = this._summaryJson();
			for (i = 0; i < data.inward.length; i++) {
				this._processSum(data.inward[i], oProcSummary);
			}
			oDetailHeader.inward = oProcSummary;
			oModel.setData(oDetailHeader);
			oModel.refresh(true);
		},

		/**
		 * Called to enable/disable to Checkbox, so that atleast one checkbox should be checked
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectCheckBox: function (oEvent) {
			// debugger; //eslint-disable-line
			var oVisiModel = this.getView().getModel("visiSummAnx1"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.asp && !oVisiData.memo && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.eAsp = true;

			} else if (!oVisiData.asp && oVisiData.memo && !oVisiData.gstn && !oVisiData.diff) {
				oVisiData.eMemo = true;

			} else if (!oVisiData.asp && !oVisiData.memo && oVisiData.gstn && !oVisiData.diff) {
				oVisiData.eGstn = true;

			} else if (!oVisiData.asp && !oVisiData.memo && !oVisiData.gstn && oVisiData.diff) {
				oVisiData.eDiff = true;

			} else {
				oVisiData.eAsp = false;
				oVisiData.eMemo = false;
				oVisiData.eDiff = false;
				oVisiData.eGstn = false;
			}
			oVisiModel.refresh(true);
		},

		/**
		 * Called to trigger background job to Updata GSTN data from sandbox
		 * Developed by: Bharat Gupta on 24.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onUpdateGstnData: function (oEvent) {
			// debugger; //eslint-disable-line
			var that = this,
				oBundle = that.getResourceBundle(),
				oPayload = {
					"req": []
				};
			if (oEvent.getSource().getId().includes("bDiffUpdate")) {
				var oData = this.byId("dDifference").getModel("Payload").getData();
				oPayload.req.push({
					"gstin": oData.gstin,
					"rtnprd": oData.taxPeriod
				});
			} else if (oEvent.getSource().getId().includes("bSummUpdate")) {
				var aGstin = this.byId("slSummGstin").getSelectedKeys(),
					vPeriod = this.byId("dtSummary").getValue();
				for (var i = 0; i < aGstin.length; i++) {
					oPayload.req.push({
						"gstin": aGstin[i],
						"rtnprd": vPeriod
					});
				}
			}
			sap.m.MessageBox.confirm(oBundle.getText("confirmUpdate"), {
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === "OK") {
						sap.ui.core.BusyIndicator.show(0);
						$.ajax({
							method: "POST",
							url: "/aspsapapi/Anx1GstnGetSection.do",
							contentType: "application/json",
							data: JSON.stringify(oPayload)
						}).done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (data.hdr.status === "S") {
								that.byId("tSummUpdate").setText(data.resp.updatedDate);
								that.byId("tDiffUpdateStats").setText(data.resp.updatedDate);
								sap.m.MessageBox.success(oBundle.getText("msgUpdateData"), {
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
		 * Method Called to Navigate to Invoice Management for Error Correction
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onMenuErrorCorrection: function (oEvent) {
			// debugger; //eslint-disable-line
			var oFilterData = {
				"dataType": "outward",
				"criteria": "RETURN_DATE_SEARCH",
				"status": "E",
				"validation": "BV",
				"segment": oEvent.getParameter("item").getKey(),
				"entity": $.sap.entityID
			};

			if (oEvent.getSource().getId().includes("bErrorCorr")) {
				// oFilterData.segment = oEvent.getParameter("item").getKey();
				// oFilterData.entity = this.byId("slEntitySave").getSelectedKey();
				oFilterData.gstin = this.byId("slGstinSave").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("dtProcessed").getDateValue();
				if (this.byId("dAdapt")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpProcessRecord");
				}
			} else {
				// oFilterData.segment = oEvent.getParameter("item").getKey();
				// oFilterData.entity = this.byId("slSummEntity").getSelectedKey();
				oFilterData.gstin = this.byId("slSummGstin").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("dtSummary").getDateValue();
				if (this.byId("dAdapt")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpAnx1Summary");
				}
			}
			if (oFilterData.segment === "gstn") {
				oFilterData.returnType = "anx1";
			}
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvoiceManage");
		},

		/**
		 * Developed by: Bharat Gupta on 17.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDownloadReport: function (oEvent) {
			// debugger; //eslint-disable-line
			var vUrl = "/aspsapapi/downloadAnx1RSReports.do",
				oPayload = {
					"req": {
						"type": oEvent.getParameter("item").getKey(),
						"entityId": [$.sap.entityID],
						"taxPeriod": null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (oEvent.getSource().getId().includes("bDownloadRpt")) {
				var aIndices = this.byId("tabProcessRecord").getSelectedIndices();

				// oPayload.req.entityId.push(this.byId("slEntitySave").getSelectedKey());
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();
				if (aIndices.length > 0) {
					var oData = this.byId("tabProcessRecord").getModel("ProcessRecord").getData();
					for (var i = 0; i < aIndices.length; i++) {
						oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndices[i]].gstin);
					}
				} else {
					oPayload.req.dataSecAttrs.GSTIN = this.byId("slGstinSave").getSelectedKeys();
				}
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
				}
			} else {
				// oPayload.req.entityId.push(this.byId("slSummEntity").getSelectedKey());
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
				oPayload.req.dataSecAttrs.GSTIN = this.byId("slSummGstin").getSelectedKeys();
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpAnx1Summary");
				}
			}
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Method called to download Excel file for Screen Level Annexure-1 Summary
		 * Developed by: Bharat Gupta on 31.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onDownloadSummary: function (oEvent) {
			// debugger; //eslint-disable-line
			var vUrl = "/aspsapapi/downloadAnx1RSReports.do",
				oPayload = {
					"req": {
						"type": null,
						"entityId": [$.sap.entityID],
						"taxPeriod": null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (oEvent.getSource().getId().includes("bDownloadProcess")) {
				oPayload.req.type = "anx1ProcessedSummary";
				oPayload.req.gstnUploadDate = "AGGREGATE";
				// oPayload.req.entityId.push(this.byId("slEntitySave").getSelectedKey());
				oPayload.req.dataSecAttrs.GSTIN = this.byId("slGstinSave").getSelectedKeys();
				oPayload.req.taxPeriod = this.byId("dtProcessed").getValue();
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpProcessRecord");
				}
			} else {
				// oPayload.req.entityId.push(this.byId("slSummEntity").getSelectedKey());
				oPayload.req.dataSecAttrs.GSTIN = this.byId("slSummGstin").getSelectedKeys();
				oPayload.req.taxPeriod = this.byId("dtSummary").getValue();
				if (this.byId("dAdapt")) {
					this._getOtherFilters(oPayload.req.dataSecAttrs, "dpAnx1Summary");
				}
				if (oEvent.getSource().getId().includes("bSummaryDownload")) {
					oPayload.req.type = "anx1ReviewSummary";
				} else if (oEvent.getSource().getId().includes("bDetailDownload")) {
					oPayload.req.type = "anx1DetailedSummary";
				}
			}
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Method called to download Excel file for Consolidated ASP Error
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onDownloadAspError: function () {
			var vUrl = "/aspsapapi/downloadAnx1RSReports.do",
				oSearchField = this.byId("dSaveStatus").getModel("Fields").getData(),
				oPayload = {
					"req": {
						"type": "anx1ConsolErrorReport",
						"entityId": [oSearchField.entityId],
						"taxPeriod": oSearchField.taxPeriod,
						"dataSecAttrs": {
							"GSTIN": [oSearchField.gstin]
						}
					}
				};
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Method called to download Excel file for Consolidated GSTN Error
		 * Developed by: Bharat Gupta on 04.02.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 */
		onDownloadGstnError: function () {
			var vUrl = "/aspsapapi/downloadAnx1RSReports.do",
				oSearchField = this.byId("dSaveStatus").getModel("Fields").getData(),
				oPayload = {
					"req": {
						"type": "anxGstnError",
						"entityId": [oSearchField.entityId],
						"taxPeriod": oSearchField.taxPeriod,
						"dataSecAttrs": {
							"GSTIN": [oSearchField.gstin]
						}
					}
				};
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Called to format amount value 
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.ANX1
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
		 * Developed by Bharat Gupta on 25.01.2020
		 * @memberOf com.ey.digigst.view.ANX1
		 * @private
		 * @param {string} vValue Value to be format
		 * @return {string} desc Table/Section Description
		 */
		sectionFormat: function (vValue) {
			if (vValue) {
				var oBundle = this.getResourceBundle(),
					desc = oBundle.getText("anx1_" + vValue);
			}
			return desc;
		},

		/*==========================================================================================*/
		/*======= Counter Party Response Table Binding Based on EntityId, GSTIN & Tax Period========*/
		/*==========================================================================================*/
		//**********/////BOC Table Binding based on 3 selection -23-08-2019
		_getCounterParty: function () {
			var gstin = this.byId("cpGstin").getSelectedItems().length;
			var gstnarr = [];
			for (var i = 0; i < gstin; i++) {
				gstnarr.push(this.byId("cpGstin").getSelectedItems()[i].getKey());
			}
			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;
			if (this.byId("idDateRange") !== undefined) {
				Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var cPsearchInfo = {
				"entityId": $.sap.entityID,
				"gstins": gstnarr,
				"taxPeriod": this.byId("dtCounterParty").getValue(),
				"startDocDate": null,
				"endDocDate": null,
				"profitCentres": profitCentres,
				"plants": plants,
				"divisions": divisions,
				"locations": locations,
				"salesOrgs": salesOrgs,
				"distributionChannels": distributionChannels,
				"userAccess1": userAccess1,
				"userAccess2": userAccess2,
				"userAccess3": userAccess3,
				"userAccess4": userAccess4,
				"userAccess5": userAccess5,
				"userAccess6": userAccess6
			};
			var cPpath = "/aspsapapi/getCounterPartyInfo.do";
			this.getCpModel = new JSONModel();
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: cPpath,
					contentType: "application/json",
					data: JSON.stringify(cPsearchInfo)
				}).done(function (data, status, jqXHR) {
					that.getCpModel.setData(data);
					that.getView().setModel(that.getCpModel, "CounterPartyInfo");
				}).fail(function (jqXHR, status, err) {});
			});
		},
		//**********/////EOC Table Binding based on 3 selection -23-08-2019

		onCpClear: function () {
			//this.byId("cpEntity").setSelectedKey("");
			this.byId("cpGstin").setSelectedKeys("");
			var vDate = new Date();
			this.byId("dtCounterParty").setMaxDate(vDate);
			this.byId("dtCounterParty").setDateValue(vDate);
			//this.getCpModel.setData([]);
			//this.getView().getModel("CounterPartyInfo").refresh(true);
			this._getCounterParty();
		},

		/*============================================================================*/
		/*======================= Link(GSTN - Counter Party)==========================*/
		/*============================================================================*/
		onPressAnx1Summary: function (oEvt) {
			var oVisiModel = this.byId("dpAnx1Summary").getModel("VisiSummary");
			oVisiModel.getData().summary = false;
			oVisiModel.refresh(true);
			this.gstn = oEvt.getSource().getText();
			var controller = this.getView();
			controller.byId("itbAnx1").setVisible(false);
			controller.byId("dpAnx1Summary").setVisible(true);
			controller.byId("sbSummary").setSelectedKey("respCP");
			controller.byId("iPanelProcess").setVisible(false);
			controller.byId("iPanelCP").setVisible(true);
			//var oCPgstin = this.byId("cpGstin").getSelectedItems().length;
			var oCPDate = this.byId("dtCounterParty").getValue();
			/*	var gstnarr = [];
				for (var i = 0; i < oCPgstin; i++) {
					gstnarr.push(this.byId("cpGstin").getSelectedItems()[i].getKey());
				}*/
			//controller.byId("slSummEntity1").setSelectedKey(oCPEntity);
			controller.byId("slSummGstin1").setSelectedKey(this.gstn);
			controller.byId("dtSummary1").setValue(oCPDate);
			var LinksearchInfo = {
				"entityId": $.sap.entityID,
				"gstins": this.gstn,
				"taxPeriod": this.byId("dtSummary1").getValue(),
				"tableSection": "",
				"docType": ""
			};
			this.counterPartyLink(LinksearchInfo);
		},

		onPressTaxRecLink: function (oEvent) {
			var controller = this.getView();
			controller.byId("itbAnx1").setVisible(false);
			controller.byId("dpAnx1Summary").setVisible(true);
			controller.byId("sbSummary").setSelectedKey("summary");
			controller.byId("iPanelProcess").setVisible(true);
			controller.byId("iPanelCP").setVisible(false);
			var obj = oEvent.getSource().getBindingContext("TaxReconInfo").getObject();
			///////////////////////Added By Vinay 14-01-2020///////////////////////////////////
			var entity = $.sap.entityID;
			controller.byId("slSummEntity").setSelectedKey(entity);
			var oCDate = this.byId("dtTaxRecon").getValue();
			this.byId("dtSummary").setValue(oCDate);
			this.byId("slSummGstin").setSelectedKeys(obj.gstin);
			///////////////////////////////////////////
			var searchInfo = this._getSummarySearchInfo(obj.gstin);
			this._getProcessSummary(searchInfo);

		},

		onSelectionChange: function (oEvent) {
			if (oEvent.getSource().getId().includes("sbRecipSummary")) {
				if (oEvent.getSource().getSelectedKey().includes("srSumm")) {
					var vFlag1 = false,
						vFlag2 = false;
					this.byId("dpRecipientSRSumm").setVisible(true);
					this.byId("dpRecipntRespSumm").setVisible(false);
					this.byId("dpRecipntRespDtl").setVisible(false);
					this.onSrSummSEntyChange();
				} else if (oEvent.getSource().getSelectedKey().includes("respSumm")) {
					vFlag1 = true;
					vFlag2 = false;
					this.byId("dpRecipientSRSumm").setVisible(false);
					this.byId("dpRecipntRespSumm").setVisible(true);
					this.byId("dpRecipntRespDtl").setVisible(false);
					this.onResponseEntyChng();
					this.onResponseSumGo();
				} else if (oEvent.getSource().getSelectedKey().includes("respDet")) {
					vFlag1 = false;
					vFlag2 = true;
					this.byId("dpRecipientSRSumm").setVisible(false);
					this.byId("dpRecipntRespSumm").setVisible(false);
					this.byId("dpRecipntRespDtl").setVisible(true);
					this.onRespDtlEntyChng();
					this.onRespeDtlGo();
				}
				this.byId("tabRecipSummary").setVisible(!vFlag1 && !vFlag2);
				this.byId("tabRespSummary").setVisible(vFlag1 && !vFlag2);
				this.byId("tabRespDetails").setVisible(!vFlag1 && vFlag2);

			} else if (oEvent.getSource().getId().includes("sbSummaryAnx1")) {
				if (oEvent.getSource().getSelectedKey().includes("summary")) {
					vFlag1 = true;
				} else {
					vFlag1 = false;
				}

			} else if (oEvent.getSource().getId().includes("sbSummaryCP")) {
				if (oEvent.getSource().getSelectedKey().includes("summary")) {
					vFlag1 = true;
				} else {
					vFlag1 = false;
				}
				this.byId("slTableCP").setVisible(vFlag1);
				this.byId("slDocsCP").setVisible(vFlag1);
				this.byId("tabSummaryCP").setVisible(vFlag1);
				this.byId("tabSummaryDet").setVisible(!vFlag1);
			} else if (oEvent.getSource().getId().includes("sbSummary")) {
				if (oEvent.getSource().getSelectedKey().includes("summary")) {
					vFlag1 = true;
					this.byId("iPanelProcess").setVisible(true);
					this.byId("iPanelCP").setVisible(false);
				} else {
					vFlag1 = false;
					this.byId("iPanelProcess").setVisible(false);
					this.byId("iPanelCP").setVisible(true);
				}
				var oVisiModel = this.byId("dpAnx1Summary").getModel("VisiSummary");
				oVisiModel.getData().summary = vFlag1;
				oVisiModel.refresh(true);
				if (oEvent.getSource().getSelectedKey().includes("summary")) {
					var gstin = this.byId("slSummGstin1").getSelectedKey();
					this.byId("slSummGstin").setSelectedKeys(gstin);
					this.byId("dtSummary").setValue(this.byId("dtSummary1").getValue());
					var searchInfo = this._getSummarySearchInfo(gstin);
					this._getProcessSummary(searchInfo);
				}
			}
		},

		cpLinkGSTN: function (Request) {
			var oSummGstinDetail = new JSONModel();
			var oSummGstnView = this.getView();
			var SummGstnsListPath = "/aspsapapi/getGSTINsForEntity.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: SummGstnsListPath,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					oSummGstinDetail.setData(data.resp.gstins);
					oSummGstnView.setModel(oSummGstinDetail, "CpSummGstnsList");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onCpSummaryGo: function (gstnarr11) {
			/*var sumEntity = this.byId("slSummEntity1").getSelectedKey();
			if (sumEntity === "") {
				sap.m.MessageBox.error("Please Select Entity", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}*/

			/*var summGstin = this.byId("slSummGstin1").getSelectedItems().length;

			if (summGstin === 0) {
				sap.m.MessageBox.error("Please Select GSTIN", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var gstnarr1 = [];
			for (var i = 0; i < summGstin; i++) {
				gstnarr1.push(this.byId("slSummGstin1").getSelectedItems()[i].getKey());
			}*/
			var LinksearchInfo = {
				"entityId": $.sap.entityID,
				"gstins": this.byId("slSummGstin1").getSelectedKey(),
				"taxPeriod": this.byId("dtSummary1").getValue(),
				"tableSection": this.byId("slTableCP").getSelectedKeys().length === 0 ? "" : this.byId("slTableCP").getSelectedKeys(),
				"docType": this.byId("slDocsCP").getSelectedKeys().length === 0 ? "" : this.byId("slDocsCP").getSelectedKeys()
			};
			this.counterPartyLink(LinksearchInfo);
		},

		counterPartyLink: function (LinksearchInfo) {
			var Linkpath = "/aspsapapi/getCounterPartySummary.do";
			this.cPLinkModel = new JSONModel();
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Linkpath,
					contentType: "application/json",
					data: JSON.stringify(LinksearchInfo)
				}).done(function (data, status, jqXHR) {
					that.cPLinkModel.setData(data);
					that.getView().setModel(that.cPLinkModel, "CpLinkSummInfo");
				}).fail(function (jqXHR, status, err) {});
			});

			////////********* Detail Table
			var LinDetlkpath = "/aspsapapi/getCounterPartyDetail.do";
			this.cPLnkDtlModel = new JSONModel();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: LinDetlkpath,
					contentType: "application/json",
					data: JSON.stringify(LinksearchInfo)
				}).done(function (data, status, jqXHR) {
					that.cPLnkDtlModel.setData(data);
					that.getView().setModel(that.cPLnkDtlModel, "CntrPrtyLnkDtl");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onChangeTables: function () {
			this.onCpSummaryGo();
		},

		onChangeDocType: function () {
			this.onCpSummaryGo();
		},

		onSummClear: function () {
			//this.byId("slSummEntity1").setSelectedKey("");
			this.byId("slSummGstin1").setSelectedKey(this.gstn);
			this.byId("slTableCP").setSelectedKeys([]);
			this.byId("slDocsCP").setSelectedKeys([]);
			var vDate = new Date();
			this.byId("dtSummary1").setMaxDate(vDate);
			this.byId("dtSummary1").setDateValue(vDate);
			this.onCpSummaryGo();
		},

		/*======================================================================================*/
		/*========Based on EntityId, GSTNs will display(Tax Recon Tab)======================*/
		/*======================================================================================*/
		//////********BOC GSTNs list(Tax Recon Tab) on 22-08-2019
		onTaxRecnEntyChng: function () {
			//this._getDataSecurity($.sap.entityID, "dpTaxRecon");
			/*	var taxRecGstnInfo = {
					"entityId": Number(this.byId("taxReconEntity").getSelectedKey())
				};
				var oTaxReGstinModel = new JSONModel();
				//var oUserInfo = new JSONModel();
				//var that = this;
				var oTaxReGstnView = this.getView();
				var GstnsList = "/aspsapapi/getGSTINsForEntity.do";
				//var UserInfo = "/SapOnboarding/getUserInfo.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(taxRecGstnInfo)
					}).done(function (data, status, jqXHR) {
						oTaxReGstinModel.setData(data);
						oTaxReGstnView.setModel(oTaxReGstinModel, "TaxRecGstnLst");
					}).fail(function (jqXHR, status, err) {});
				});*/
		},

		/*_getDataSecurity11: function () {
			// debugger; //eslint-disable-line
			var that = this;
			var oTaxReGstinModel123 = new JSONModel();
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);w(0);w(0);w(0);w(0);w(0);w(0);w(0);w(0);w(0);w(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAnxDataSecApplAttr.do",
				contentType: "application/json",
				async: false,
				data: JSON.stringify({
					"req": {
						"entityId": Number(this.byId("taxReconEntity").getSelectedKey())
					}
				})
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				oTaxReGstinModel123.setData(data.resp);
				oView.setModel(oTaxReGstinModel123, "DataSecurity");

			}).fail(function (jqXHR, status, err) {
				oView.setBusy(false);
			});
		},*/

		//////********EOC GSTNs list(Tax Recon Tab) on 22-08-2019

		/*============================================================================*/
		/*================== Tax Recon Table Binding==================================*/
		/*============================================================================*/
		//**********/////BOC Table Binding based on 3 selection -23-08-2019
		_getTaxRecon: function () {
			var taxgstin = this.byId("taxReconGstin").getSelectedItems().length;
			var taxgstnArr = [];
			for (var i = 0; i < taxgstin; i++) {
				taxgstnArr.push(this.byId("taxReconGstin").getSelectedItems()[i].getKey());
			}

			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;

			if (this.byId("idDateRange") !== undefined) {
				Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var TaxReconInfo = {
				"hdr": {
					"pageNo": "",
					"pageSize": ""
				},
				"req": {
					"entityId": $.sap.entityID,
					"gstins": taxgstnArr,
					"taxPeriod": this.byId("dtTaxRecon").getValue(),
					"startDocDate": null,
					"endDocDate": null,
					"profitCentres": profitCentres,
					"plants": plants,
					"divisions": divisions,
					"locations": locations,
					"salesOrgs": salesOrgs,
					"distributionChannels": distributionChannels,
					"userAccess1": userAccess1,
					"userAccess2": userAccess2,
					"userAccess3": userAccess3,
					"userAccess4": userAccess4,
					"userAccess5": userAccess5,
					"userAccess6": userAccess6
				}
			};
			var TaxReconpath = "/aspsapapi/getTaxAmountReconInfo.do";
			this.taxRecModel = new JSONModel();
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: TaxReconpath,
					contentType: "application/json",
					data: JSON.stringify(TaxReconInfo)
				}).done(function (data, status, jqXHR) {
					that.taxRecModel.setData(data);
					that.getView().setModel(that.taxRecModel, "TaxReconInfo");
				}).fail(function (jqXHR, status, err) {});
			});
		},
		//**********/////EOC Table Binding based on 3 selection -23-08-2019

		onTaxRecClear: function () {
			this.byId("taxReconGstin").setSelectedKeys("");
			var vDate = new Date();
			this.byId("dtTaxRecon").setMaxDate(vDate);
			this.byId("dtTaxRecon").setDateValue(vDate);
			this._getTaxRecon();
		},

		/*========================================Recipient Summary Tab============================================*/
		/*========GSTINs Binding based on selection of Entity Lst ((SrSummary.frag))on 03-08-2019=================*/
		/*=========================================================================================================*/

		onSrSummSEntyChange: function () {
			var that = this;
			//this._getDataSecurity(Number(this.byId("recpSummEntity").getSelectedKey()), "dpRecipientSRSumm");
			var RecSGSTNInfo = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("dtRecSumm").getValue()
			};
			var oRespSumGstnModel1 = new JSONModel();
			var oRespSumGstnModel2 = new JSONModel();
			var oRecSGstnView = this.getView();
			var oRecSGstnPath = "/aspsapapi/getRecipientFilterForEntity.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRecSGstnPath,
					contentType: "application/json",
					data: JSON.stringify(RecSGSTNInfo)
				}).done(function (data, status, jqXHR) {
					var arry = [];
					that.Data = data.resp.det;
					for (var i = 0; i < that.Data.length; i++) {
						for (var n = 0; n < that.Data[i].gstinSgstinPan.length; n++) {
							arry.push(that.Data[i].gstinSgstinPan[n]);
						}
					}
					var newArray = [];
					var lookupObject = {};
					var newArray1 = [];
					var lookupObject1 = {};
					for (var j in arry) {
						lookupObject[arry[j]["cgstin"]] = arry[j];
					}
					for (j in lookupObject) {
						newArray.push(lookupObject[j]);
					}
					for (var k in arry) {
						lookupObject1[arry[k]["cpan"]] = arry[k];
					}
					for (k in lookupObject1) {
						newArray1.push(lookupObject1[k]);
					}
					that.newArray = newArray;
					that.newArray12 = newArray1;
					oRespSumGstnModel1.setData(newArray);
					oRecSGstnView.setModel(oRespSumGstnModel1, "ReciSummGSTN1");
					oRespSumGstnModel2.setData(newArray1);
					oRecSGstnView.setModel(oRespSumGstnModel2, "ReciSummGSTN12");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		///////////////////On Sr summary Gstin Change///////////////
		///////////Added by Vinay Kodam////////////////
		onSrsummGstinChange: function (oEvt) {
			var arry = [];
			var gstins = oEvt.getSource().getSelectedKeys();
			var Data = this.Data;
			var oRespSumGstnModel1 = new JSONModel();
			var oRespSumGstnModel2 = new JSONModel();
			if (gstins.length !== 0) {
				for (var j = 0; j < gstins.length; j++) {
					for (var i = 0; i < Data.length; i++) {
						for (var n = 0; n < Data[i].gstinSgstinPan.length; n++) {
							if (gstins[j] === Data[i].gstin) {
								arry.push(Data[i].gstinSgstinPan[n]);
							}
						}
					}
				}
				var newArray1 = [];
				var lookupObject1 = {};

				for (var k in arry) {
					lookupObject1[arry[k]["cpan"]] = arry[k];
				}
				for (k in lookupObject1) {
					newArray1.push(lookupObject1[k]);
				}
				oRespSumGstnModel1.setData(arry);
				this.getView().setModel(oRespSumGstnModel1, "ReciSummGSTN1");
				oRespSumGstnModel2.setData(newArray1);
				this.getView().setModel(oRespSumGstnModel2, "ReciSummGSTN12");
			} else {
				oRespSumGstnModel1.setData(this.newArray);
				this.getView().setModel(oRespSumGstnModel1, "ReciSummGSTN1");
				oRespSumGstnModel2.setData(this.newArray12);
				this.getView().setModel(oRespSumGstnModel2, "ReciSummGSTN12");
			}
		},

		onSRsumPAN: function (evt) {
			var RespnseSumGstinsModel = new JSONModel();
			var RespnseSumGstinsView = this.getView();
			var arr = [],
				obj = {};
			var selItems = evt.getSource().getSelectedItems(); //[0].getText();
			var cgstins = this.newArray;
			if (selItems.length !== 0) {
				for (var k = 0; k < selItems.length; k++) {
					for (var i = 0; i < cgstins.length; i++) {
						var a = cgstins[i].cgstin;
						var cgstins1 = a.slice(2, 12);
						if (selItems[k].getText() === cgstins1) {
							obj = cgstins[i];
							arr.push(obj);
						}
					}
				}
				RespnseSumGstinsModel.setData(arr);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "ReciSummGSTN1");
			} else {
				RespnseSumGstinsModel.setData(this.newArray);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "ReciSummGSTN1");
			}
		},

		/*========GSTNs Binding based on selection of Entity Lst ((RespSummary.frag))on 03-08-2019===============*/

		onResponseEntyChng: function () {
			var that = this;
			//this._getDataSecurity(Number(this.byId("responseSumEntity").getSelectedKey()), "dpRecipntRespSumm");
			var rspnseSumGSTINInfo = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("dtResponseSum").getValue()
			};

			var oRespSumGstnModel = new JSONModel();
			var oRespSumGstnModel1 = new JSONModel();
			var oRespSumGstnView = this.getView();
			var oRespnseGstnPath = "/aspsapapi/getRecipientFilterForEntity.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRespnseGstnPath,
					contentType: "application/json",
					data: JSON.stringify(rspnseSumGSTINInfo)
				}).done(function (data, status, jqXHR) {
					var arry = [];
					that.Data1 = data.resp.det;
					for (var i = 0; i < that.Data1.length; i++) {
						for (var n = 0; n < that.Data1[i].gstinSgstinPan.length; n++) {
							arry.push(that.Data1[i].gstinSgstinPan[n]);
						}
					}
					var newArray = [];
					var lookupObject = {};
					var newArray1 = [];
					var lookupObject1 = {};

					for (var j in arry) {
						lookupObject[arry[j]["cgstin"]] = arry[j];
					}
					for (j in lookupObject) {
						newArray.push(lookupObject[j]);
					}

					for (var k in arry) {
						lookupObject1[arry[k]["cpan"]] = arry[k];
					}
					for (k in lookupObject1) {
						newArray1.push(lookupObject1[k]);
					}

					that.newArray1 = newArray;
					that.newArray2 = newArray1;
					oRespSumGstnModel.setData(newArray);
					oRespSumGstnView.setModel(oRespSumGstnModel, "RespnseSumGstins");
					oRespSumGstnModel1.setData(newArray1);
					oRespSumGstnView.setModel(oRespSumGstnModel1, "RespnseSumGstins1");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onRespSummGstinChange: function (oEvt) {
			var arry = [];
			var gstins = oEvt.getSource().getSelectedKeys();
			var Data = this.Data1;
			var oRespSumGstnModel1 = new JSONModel();
			var oRespSumGstnModel2 = new JSONModel();
			if (gstins.length !== 0) {
				for (var j = 0; j < gstins.length; j++) {
					for (var i = 0; i < Data.length; i++) {
						for (var n = 0; n < Data[i].gstinSgstinPan.length; n++) {
							if (gstins[j] === Data[i].gstin) {
								arry.push(Data[i].gstinSgstinPan[n]);
							}
						}
					}
				}

				var newArray1 = [];
				var lookupObject1 = {};
				for (var k in arry) {
					lookupObject1[arry[k]["cpan"]] = arry[k];
				}
				for (k in lookupObject1) {
					newArray1.push(lookupObject1[k]);
				}

				oRespSumGstnModel1.setData(arry);
				this.getView().setModel(oRespSumGstnModel1, "RespnseSumGstins");
				oRespSumGstnModel2.setData(newArray1);
				this.getView().setModel(oRespSumGstnModel2, "RespnseSumGstins1");
			} else {
				oRespSumGstnModel1.setData(this.newArray1);
				this.getView().setModel(oRespSumGstnModel1, "RespnseSumGstins");
				oRespSumGstnModel2.setData(this.newArray2);
				this.getView().setModel(oRespSumGstnModel2, "RespnseSumGstins1");
			}
		},

		onRespSummPan: function (evt) {
			var RespnseSumGstinsModel = new JSONModel();
			var RespnseSumGstinsView = this.getView();
			var arr = [],
				obj = {};
			var selItems = evt.getSource().getSelectedItems(); //[0].getText();
			var cgstins = this.newArray1;
			if (selItems.length !== 0) {
				for (var k = 0; k < selItems.length; k++) {
					for (var i = 0; i < cgstins.length; i++) {
						var a = cgstins[i].cgstin;
						var cgstins1 = a.slice(2, 12);
						if (selItems[k].getText() === cgstins1) {
							obj = cgstins[i];
							arr.push(obj);
						}
					}
				}
				RespnseSumGstinsModel.setData(arr);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "RespnseSumGstins");
			} else {
				RespnseSumGstinsModel.setData(this.newArray1);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "RespnseSumGstins");
			}
		},

		/*========GSTNs Binding based on selection of Entity Lst ((Resp Detail.frag))on 03-08-2019===============*/

		onRespDtlEntyChng: function () {
			var that = this;
			//this._getDataSecurity(Number(this.byId("responseDtlEntity").getSelectedKey()), "dpRecipntRespDtl");
			var respDtlGSTNInfo = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("dtResponseDtl").getValue()
			};
			var oRespDtlGstModel = new JSONModel();
			var oRespDtlGstModel1 = new JSONModel();
			var oRespDtlGstView = this.getView();
			var oRespDtlGstPath = "/aspsapapi/getRecipientFilterForEntity.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRespDtlGstPath,
					contentType: "application/json",
					data: JSON.stringify(respDtlGSTNInfo)
				}).done(function (data, status, jqXHR) {
					var arry = [];
					that.Data2 = data.resp.det;
					for (var i = 0; i < that.Data2.length; i++) {
						for (var n = 0; n < that.Data2[i].gstinSgstinPan.length; n++) {
							arry.push(that.Data2[i].gstinSgstinPan[n]);
						}
					}
					var newArray = [];
					var lookupObject = {};
					var newArray1 = [];
					var lookupObject1 = {};

					for (var j in arry) {
						lookupObject[arry[j]["cgstin"]] = arry[j];
					}
					for (j in lookupObject) {
						newArray.push(lookupObject[j]);
					}

					for (var k in arry) {
						lookupObject1[arry[k]["cpan"]] = arry[k];
					}

					for (k in lookupObject1) {
						newArray1.push(lookupObject1[k]);
					}

					that.newArray22 = newArray;
					that.newArray23 = newArray1;
					oRespDtlGstModel.setData(newArray);
					oRespDtlGstView.setModel(oRespDtlGstModel, "RespDtlGstinsList");
					oRespDtlGstModel1.setData(newArray1);
					oRespDtlGstView.setModel(oRespDtlGstModel1, "RespDtlGstinsList1");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onRespDtlGstin: function (oEvt) {
			var arry = [];
			var gstins = oEvt.getSource().getSelectedKeys();
			var Data = this.Data2;
			var oRespSumGstnModel1 = new JSONModel();
			var oRespSumGstnModel12 = new JSONModel();
			if (gstins.length !== 0) {
				for (var j = 0; j < gstins.length; j++) {
					for (var i = 0; i < Data.length; i++) {
						for (var n = 0; n < Data[i].gstinSgstinPan.length; n++) {
							if (gstins[j] === Data[i].gstin) {
								arry.push(Data[i].gstinSgstinPan[n]);
							}
						}
					}
				}

				var newArray1 = [];
				var lookupObject1 = {};
				for (var k in arry) {
					lookupObject1[arry[k]["cpan"]] = arry[k];
				}
				for (k in lookupObject1) {
					newArray1.push(lookupObject1[k]);
				}

				oRespSumGstnModel1.setData(arry);
				this.getView().setModel(oRespSumGstnModel1, "RespDtlGstinsList");
				oRespSumGstnModel12.setData(newArray1);
				this.getView().setModel(oRespSumGstnModel12, "RespDtlGstinsList1");
			} else {
				oRespSumGstnModel1.setData(this.newArray22);
				this.getView().setModel(oRespSumGstnModel1, "RespDtlGstinsList");
				oRespSumGstnModel12.setData(this.newArray23);
				this.getView().setModel(oRespSumGstnModel12, "RespDtlGstinsList1");
			}
		},

		onRespDetPAN: function (evt) {
			var RespnseSumGstinsModel = new JSONModel();
			var RespnseSumGstinsView = this.getView();
			var arr = [],
				obj = {};
			var selItems = evt.getSource().getSelectedItems(); //[0].getText();
			var cgstins = this.newArray22;
			if (selItems.length !== 0) {
				for (var k = 0; k < selItems.length; k++) {
					for (var i = 0; i < cgstins.length; i++) {
						var a = cgstins[i].cgstin;
						var cgstins1 = a.slice(2, 12);
						if (selItems[k].getText() === cgstins1) {
							obj = cgstins[i];
							arr.push(obj);
						}
					}
				}
				RespnseSumGstinsModel.setData(arr);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "RespDtlGstinsList");
			} else {
				RespnseSumGstinsModel.setData(this.newArray22);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "RespDtlGstinsList");
			}
		},

		/*=========================================================================================================*/
		/*======== SR Summary Table Binding based on selection of Entity,SGSTIN,TaxPeriod(SrSummary.frag)on 04-08-2019===============*/
		/*=========================================================================================================*/
		onRecpSumGo: function () {
			var recpSummPAN = this.byId("recpSummPAN").getSelectedKeys();
			if (recpSummPAN.length === 0) {
				this.onSrSummSEntyChange();
			}
			var oSearchInfo = this._getSearchInfo(1);
			this.submitData(oSearchInfo);
		},

		submitData: function (oSearchInfo) {
			this.oRecptSumGoModel = new JSONModel();
			this.oRecptSumView = this.getView();
			var recptSummGOPath = "/aspsapapi/getSRSummaryDetails.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: recptSummGOPath,
					contentType: "application/json",
					data: JSON.stringify(oSearchInfo)
				}).done(function (data, status, jqXHR) {
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					for (var i = 0; i < data.resp.det.length; i++) {
						var ele = data.resp.det[i];
						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							delete ele.cPan;
							delete ele.cName;
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							delete ele.cPan;
							delete ele.cName;
							delete ele.cgstin;
							curL2Obj.level3.push(ele);
						}
					}
					for (var j = 0; j < retArr.length; j++) {
						retArr[j].cgstin = retArr[j].level2.length;
					}
					that.SRsummaryPagination(retArr.length);
					that.oRecptSumGoModel.setData(retArr);
					that.oRecptSumView.setModel(that.oRecptSumGoModel, "recptSummGO");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		SRsummaryPagination: function (header) {
			var pageNumber = Math.ceil(header / 50);
			this.byId("SRtxtPageNo").setText("/ " + pageNumber);
			this.byId("SRinPageNo").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("SRinPageNo").setValue(pageNumber);
				this.byId("SRinPageNo").setEnabled(false);
				this.byId("SRbtnPrev").setEnabled(false);
				this.byId("SRbtnNext").setEnabled(false);
			} else if (this.byId("SRinPageNo").getValue() === "" || this.byId("SRinPageNo").getValue() === "0") {
				this.byId("SRinPageNo").setValue(1);
				this.byId("SRinPageNo").setEnabled(true);
				this.byId("SRbtnPrev").setEnabled(false);
			} else {
				this.byId("SRinPageNo").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("SRinPageNo").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("SRbtnNext").setEnabled(true);
			} else {
				this.byId("SRbtnNext").setEnabled(false);
			}
			this.byId("SRbtnPrev").setEnabled(vPageNo > 1 ? true : false);
		},

		onRecSumClear: function () {
			this.byId("recpSummGstin").setSelectedItems("");
			this.byId("recpSummPAN").setSelectedItems("");
			this.byId("recpSummRGstin").setSelectedItems("");
			this.byId("slRecptUploadDate").setSelectedKey("");
			var vDate = new Date();
			this.byId("dtRecSumm").setMaxDate(vDate);
			this.byId("dtRecSumm").setDateValue(vDate);
			this.onRecpSumGo();
		},

		/*=========================================================================================================*/
		/*======== Response Summary Table Binding based on selection of Entity,SGSTIN,TaxPeriod(RespSummary.frag)on 05-08-2019===============*/
		/*=========================================================================================================*/
		onResponseSumGo: function () {
			/*	var oRespSEnty = this.byId("responseSumEntity").getSelectedKey();
				if (oRespSEnty === "") {
					sap.m.MessageBox.error("Please Select Entity Id", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}*/
			var responseSumPAN = this.byId("responseSumPAN").getSelectedKeys();
			if (responseSumPAN.length === 0) {
				this.onResponseEntyChng();
			}
			var oRespcSGstin = this.byId("responseSumGstin").getSelectedItems();
			var oRespcSGstnArr = [];
			for (var j = 0; j < oRespcSGstin.length; j++) {
				oRespcSGstnArr.push(this.byId("responseSumGstin").getSelectedItems()[j].getKey());
			}

			var cPans = this.byId("responseSumPAN").getSelectedKeys();
			var cgstin = this.byId("responseSumRGstin").getSelectedKeys();
			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;

			if (this.byId("idDateRange") !== undefined) {
				Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var RespnseSummInfo = {
				"hdr": {
					"pageNo": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"gstins": oRespcSGstnArr,
					"taxPeriod": this.byId("dtResponseSum").getValue(),
					"cPans": cPans,
					"cgstin": cgstin,
					"startDocDate": null,
					"endDocDate": null,
					"profitCentres": profitCentres,
					"plants": plants,
					"divisions": divisions,
					"locations": locations,
					"salesOrgs": salesOrgs,
					"distributionChannels": distributionChannels,
					"userAccess1": userAccess1,
					"userAccess2": userAccess2,
					"userAccess3": userAccess3,
					"userAccess4": userAccess4,
					"userAccess5": userAccess5,
					"userAccess6": userAccess6
				}
				/*"entityId": Number(oRespSEnty),
				"sgstins": oRespcSGstnArr,
				"taxPeriod": this.byId("dtResponseSum").getValue()*/
			};
			this.oResSumModel = new JSONModel();
			var oRSGoRespSum = this.getView();
			var RecpscSGoPath = "/aspsapapi/getRecipientResponseSummary.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RecpscSGoPath,
					contentType: "appliaction/json",
					data: JSON.stringify(RespnseSummInfo)
				}).done(function (data, status, jqXHR) {
					// Initialize an empty return array.
					var retArr = [];
					// Declare 2 variables to refer to the current L1 level object
					// and current L2 level object, while iterating over the elements
					// of the input array.
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					// Iterate over the input array. The elements will be ordered 
					// according to the hierarchy. So, if we encounter an L1 object, the
					// L2 objects will follow that object. Similarly, the L3 objects 
					// will follow the L1 objects.
					/*arr.forEach(ele => */
					for (var j = 0; j < data.resp.det.length; j++) {
						var ele = data.resp.det[j];
						var lvl = ele.level; // Get the level of the cur Obj.
						// If the level of the object is L1, then add the curObj to the
						// ret arr and initialize a level2 array for pushing all the
						// level2 objects.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							// Initialize the L2 array for storing the L2 objects that
							// follow this L1 object.
							curL1Obj.level2 = [];
						}
						// If the 
						if (lvl === "L2") {
							delete ele.cPan;
							delete ele.cName;
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							// Initialize an empty array to push the L3 objects that
							// follow the L1 objects.
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							delete ele.cPan;
							delete ele.cName;
							delete ele.cgstin;
							// Push the L3 level object to the L3 array for the current
							// L2 object.
							curL2Obj.level3.push(ele);
						}
					}
					for (var a = 0; a < retArr.length; a++) {
						retArr[a].cgstin = retArr[a].level2.length;
					}
					//return retArr;
					that.oResSumModel.setData(retArr);
					oRSGoRespSum.setModel(that.oResSumModel, "RespnseSummTabl");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onResponseSumClear: function () {
			this.byId("responseSumGstin").setSelectedItems("");
			this.byId("responseSumPAN").setSelectedItems("");
			this.byId("responseSumRGstin").setSelectedItems("");
			this.byId("responseSumRGstin").setSelectedKey("");
			var vDate = new Date();
			this.byId("dtResponseSum").setMaxDate(vDate);
			this.byId("dtResponseSum").setDateValue(vDate);
			this.onResponseSumGo();
		},

		/*=========================================================================================================*/
		/*======== Response Detail Table Binding based on selection of Entity,SGSTIN,TaxPeriod(RespDetails.frag)on 05-08-2019===============*/
		/*=========================================================================================================*/
		onRespeDtlGo: function () {
			var responseDtlPAN = this.byId("responseDtlPAN").getSelectedKeys();
			if (responseDtlPAN.length === 0) {
				this.onRespDtlEntyChng();
			}
			var oRespDtlGstin = this.byId("responseDtlGstin").getSelectedItems();
			var oRespDtlGstnArr = [];
			for (var j = 0; j < oRespDtlGstin.length; j++) {
				oRespDtlGstnArr.push(this.byId("responseDtlGstin").getSelectedItems()[j].getKey());
			}

			var cPans = this.byId("responseDtlPAN").getSelectedKeys();
			var cgstin = this.byId("responseDtlRGstin").getSelectedKeys();
			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;

			if (this.byId("idDateRange") !== undefined) {
				Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var respDtlInfo = {
				"hdr": {
					"pageNo": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"gstins": oRespDtlGstnArr,
					"taxPeriod": this.byId("dtResponseDtl").getValue(),
					"cPans": cPans,
					"cgstin": cgstin,
					"startDocDate": null,
					"endDocDate": null,
					"profitCentres": profitCentres,
					"plants": plants,
					"divisions": divisions,
					"locations": locations,
					"salesOrgs": salesOrgs,
					"distributionChannels": distributionChannels,
					"userAccess1": userAccess1,
					"userAccess2": userAccess2,
					"userAccess3": userAccess3,
					"userAccess4": userAccess4,
					"userAccess5": userAccess5,
					"userAccess6": userAccess6
				}
			};
			this.oRSGoResDtlModel = new JSONModel();
			var oRSGoResDtlView = this.getView();
			var RecpscSGoPath = "/aspsapapi/getRecipientResponseDetails.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RecpscSGoPath,
					contentType: "appliaction/json",
					data: JSON.stringify(respDtlInfo)
				}).done(function (data, status, jqXHR) {
					// Initialize an empty return array.
					var retArr = [];
					// Declare 2 variables to refer to the current L1 level object
					// and current L2 level object, while iterating over the elements
					// of the input array.
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					// Iterate over the input array. The elements will be ordered 
					// according to the hierarchy. So, if we encounter an L1 object, the
					// L2 objects will follow that object. Similarly, the L3 objects 
					// will follow the L1 objects.
					/*arr.forEach(ele => */
					for (var k = 0; k < data.resp.det.length; k++) {
						var ele = data.resp.det[k];
						if (curL1Obj.level2 !== undefined) {
							data.resp.det[0].cgstin = curL1Obj.level2.length;
						}
						var lvl = ele.level; // Get the level of the cur Obj.
						// If the level of the object is L1, then add the curObj to the
						// ret arr and initialize a level2 array for pushing all the
						// level2 objects.
						if (lvl === "L1") {
							if (curL1Obj.level2 !== undefined) {
								ele.cgstin = curL1Obj.level2.length;
							}
							curL1Obj = ele;
							retArr.push(curL1Obj);
							// Initialize the L2 array for storing the L2 objects that
							// follow this L1 object.
							curL1Obj.level2 = [];
						}
						// If the 
						if (lvl === "L2") {
							delete ele.cPan;
							delete ele.cName;
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							// Initialize an empty array to push the L3 objects that
							// follow the L1 objects.
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							delete ele.cPan;
							delete ele.cName;
							delete ele.cgstin;
							// Push the L3 level object to the L3 array for the current
							// L2 object.
							curL2Obj.level3.push(ele);
						}
					}
					for (var b = 0; b < retArr.length; b++) {
						retArr[b].cgstin = retArr[b].level2.length;
					}
					//return retArr;
					that.oRSGoResDtlModel.setData(retArr);
					oRSGoResDtlView.setModel(that.oRSGoResDtlModel, "RespnseDtlTable");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onRespDtlClear: function () {
			this.byId("responseDtlGstin").setSelectedItems("");
			this.byId("responseDtlPAN").setSelectedItems("");
			this.byId("responseDtlRGstin").setSelectedItems("");
			this.byId("RecptDtlUpldDate").setSelectedKey("");
			var vDate = new Date();
			this.byId("dtResponseDtl").setMaxDate(vDate);
			this.byId("dtResponseDtl").setDateValue(vDate);
			this.onRespeDtlGo();
		},

		SRonPressPagination: function (oEvent) {
			var vValue = parseInt(this.byId("SRbtnPrev").getValue(), 10);
			if (oEvent.getSource().getId().includes("SRbtnPrev")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("SRbtnPrev").setEnabled(false);
				}
				if (!this.byId("SRbtnNext").getEnabled()) {
					this.byId("SRbtnNext").setEnabled(true);
				}
			} else {
				var vPageNo = parseInt(this.byId("SRtxtPageNo").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("SRbtnPrev").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("SRbtnNext").setEnabled(false);
				}
			}
			this.byId("SRinPageNo").setValue(vValue);
			//var status = this._getDataType(),
			var oSearchInfo = this._getSearchInfo(vValue);
			this.submitData(oSearchInfo);
		},

		_getSearchInfo: function (vPageNo) {
			//var aEntity = [];
			//aEntity.push(this.byId("slInvEntity").getSelectedKey());
			var oSgstin = this.byId("recpSummGstin").getSelectedItems().length;
			var oSgstinArr = [];
			for (var i = 0; i < oSgstin; i++) {
				oSgstinArr.push(this.byId("recpSummGstin").getSelectedItems()[i].getKey());
			}

			var cPans = this.byId("recpSummPAN").getSelectedKeys();
			var cgstin = this.byId("recpSummRGstin").getSelectedKeys();
			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;

			if (this.byId("idDateRange") !== undefined) {
				Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var searchInfo = {
				"hdr": {
					"pageNo": vPageNo - 1,
					"pageSize": 50
				},
				"req": {
					"entityId": $.sap.entityID,
					"gstins": oSgstinArr,
					"taxPeriod": this.byId("dtRecSumm").getValue(),
					"cPans": cPans,
					"cgstin": cgstin,
					"startDocDate": null,
					"endDocDate": null,
					"profitCentres": profitCentres,
					"plants": plants,
					"divisions": divisions,
					"locations": locations,
					"salesOrgs": salesOrgs,
					"distributionChannels": distributionChannels,
					"userAccess1": userAccess1,
					"userAccess2": userAccess2,
					"userAccess3": userAccess3,
					"userAccess4": userAccess4,
					"userAccess5": userAccess5,
					"userAccess6": userAccess6
				}
			};
			return searchInfo;
		},

		/**
		 * Called when user enter page number in input box and press enter or focus out of input box
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSubmitPagination: function () {
			var vPageNo = this.byId("SRinPageNo").getValue();
			var oSearchInfo = this._getSearchInfo(vPageNo);
			this.submitData(oSearchInfo);
		},

		fnBeinBtnPress: function () {
			var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			MessageBox.information(
				"Do you want to post these value to RET1?", {
					styleClass: bCompact ? "sapUiSizeCompact" : ""
				}
			);
		}
	});
});