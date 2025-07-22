// var oDataType = "";
sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/configOutward",
	"sap/m/MessagePopover",
	"sap/m/MessageItem",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage"
], function (BaseController, configOutward, MessagePopover, MessageItem, JSONModel, Storage) {
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
			// 			debugger; //eslint-disable-line
			// 			var oItem = oEvent.getParameter("item"),
			// 				oMessage = oItem.getBindingContext("Msg").getObject();
		}
	});

	return BaseController.extend("com.ey.digigst.controller.InvoiceManage", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 */
		onInit: function () {
			$.sap.returnType = "GSTR1";
			// var oStorage = new Storage(Storage.Type.session, "digiGst"),
			// 	oEntityData = oStorage.get("entity");

			// if (!oEntityData) {
			// 	this.getRouter().navTo("Home");
			// } else {
			// 	var oModel = this.getOwnerComponent().getModel("EntityModel");
			// 	oModel.setData(oEntityData);
			// 	oModel.refresh(true);
			// 	this._getDataSecurity(oEntityData[0].entityId);
			// }
			this._bindReadOnlyProp();

			var oProp = {
				"outward": true,
				"colVisi": true,
				"aspView": true, // Modified on 29.11.2019
				"returnPeriod": false,
				"returnType": true,
				"tableNo": true,
				"gstnStatus": true,
				"cpAction": true,
				"validation": false,
				"saveAction": false,
				"btnPrevNext": false,
				"msgPopover": false
			};
			this.getView().setModel(new JSONModel(oProp), "Properties");

			var oData = this.getOwnerComponent().getModel("DropDown").getData();

			this.byId("slReturnType").setModel(new JSONModel(oData.returnType.outward), "ReturnType");
			this.byId("slGstnReturnType").setModel(new JSONModel(oData.returnType.outward), "ReturnType");

			this.byId("slDocType").setModel(new JSONModel(oData.docType.outward), "DocType");
			this.byId("slGstnDocType").setModel(new JSONModel(oData.docType.outward), "DocType");

			this.getOwnerComponent().getRouter().getRoute("InvoiceManage").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 */
		onAfterRendering: function () {
			this._bindDefaultDate();
			this._bindGstnDefaultData();

			var aDataType = this.getOwnerComponent().getModel("DropDown").getData().dataType1;
			this.byId("slGstnDataType").setSelectedKey(aDataType[0].key);

			// var aEntityData = this.getOwnerComponent().getModel("EntityModel").getData();
			// this.byId("slGstnEntity").setSelectedKey(aEntityData[0].entityId);
			// this._getGstnDataSecurity(aEntityData[0].entityId);
		},

		/**
		 * Added by Bharat Gupta on 30.11.2019
		 * Bind read only property to Filter Components
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_bindReadOnlyProp: function () {
			var that = this;
			this.byId("iFromDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("iFromDate").$().find("input").attr("readonly", true);
				}
			});
			this.byId("iToDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("iToDate").$().find("input").attr("readonly", true);
				}
			});
			this.byId("iFromPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("iFromPeriod").$().find("input").attr("readonly", true);
				}
			});
			this.byId("iToPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("iToPeriod").$().find("input").attr("readonly", true);
				}
			});
			// Added for GSTN by Bharat Gupta on 29.11.2019
			this.byId("gFromPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("gFromPeriod").$().find("input").attr("readonly", true);
				}
			});
			this.byId("gToPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("gToPeriod").$().find("input").attr("readonly", true);
				}
			}); // End of Code by Bharat Gupta on 29.11.2019
		},

		/**
		 * Bind default date value to Date Picker
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_bindDefaultDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iFromDate").setMaxDate(new Date());

			this.byId("iToDate").setDateValue(new Date());
			this.byId("iToDate").setMaxDate(new Date());
			this.byId("iToDate").setMinDate(vDate);

			this.byId("iFromPeriod").setDateValue(vDate);
			this.byId("iFromPeriod").setMaxDate(new Date());

			this.byId("iToPeriod").setDateValue(new Date());
			this.byId("iToPeriod").setMaxDate(new Date());
			this.byId("iToPeriod").setMinDate(vDate);

		},

		_bindGstnDefaultData: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			// Added for GSTN by Bharat Gupta on 29.11.2019
			this.byId("gFromPeriod").setDateValue(vDate);
			this.byId("gFromPeriod").setMaxDate(new Date());

			this.byId("gToPeriod").setDateValue(new Date());
			this.byId("gToPeriod").setMaxDate(new Date());
			this.byId("gToPeriod").setMinDate(vDate);
			// End of Code by Bharat Gupta on 29.11.2019
		},

		/**
		 * router matched method for routing & navigation
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		_onRouteMatched: function (oEvent) {
			// debugger; //eslint-disable-line
			var oFilterModel = this.getOwnerComponent().getModel("FilterInvoice");
			this._editInvoiceTab(true); // Modified by Bharat Gupta on 29.11.2019

			if (oFilterModel) {
				this._bindforDataStatusFilter(oFilterModel);

			} else {
				// Modified by Bharat Gupta for GSTN api implementation on 26.11.2019
				var oSearchInfo = this._getSearchInfo(this._getDataType(), 1);
				this._getAspInvoices(oSearchInfo);
			}
		},

		_bindTableNumber: function (vTableNumber) { //eslint-disable-line
			// debugger; //eslint-disable-line
			var aTableNumber = [];

			switch (vTableNumber) {
			case "B2B":
				aTableNumber = ["4A", "4B", "6B", "6C"];
				break;
			case "B2CL":
				aTableNumber = ["5A"];
				break;
			case "B2BA":
			case "B2CLA":
				aTableNumber = ["9A"];
				break;
			case "EXP":
				aTableNumber = ["6A"];
				break;
				// case "EXPA":
				// 	aTableNumber = ["9A"];
				// 	break;
			case "CDNR":
			case "CDNUR":
				aTableNumber = ["9B"];
				break;
			case "CDNRA":
			case "CDNURA":
				aTableNumber = ["9C"];
				break;
			case "B2CS":
				aTableNumber = ["7A(1)", "7B(1)"];
				break;
			case "B2CSA":
				aTableNumber = ["10A"];
				break;
			case "NIL":
				aTableNumber = ["8A", "8B", "8C", "8D"]; // Nil, Exempt and Non GST Supplies
				break;
			case "AT":
				aTableNumber = ["11_P1_A1", "11_P1_A2"]; // Adv. Received (Section 11 Part I-11A)
				break;
			case "ATA":
				aTableNumber = ["11_P2_A1", "11_P2_A2"]; // Adv. Received Amended (Section 11 Part II-11A)
				break;
			case "TXPD":
				aTableNumber = ["11_P1_B1", "11_P1_B2"]; // Adv. Adjusted (Section 11 Part I-11B)
				break;
			case "TXPDA":
				aTableNumber = ["11_P2_B1", "11_P2_B2"]; // Adv. Adjusted Amended (Section 11 Part II-11B)
				break;
			case "HSN":
				aTableNumber = ["12"];
				break;
			case "DOC":
				aTableNumber = ["13"];
				break;
			}
			this.byId("slTabNumber").setSelectedKeys(aTableNumber);
		},

		/**
		 * Modified by Bharat Gupta on 30.11.2019
		 * Add adapt filter value to search data based on filter value from data status navigation
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oFilterData Filter data value to bind in adapt filter
		 */
		_bindAdaptFilter: function (oFilterData) {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManage.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			this.byId("slProfitCtr").setSelectedKeys(oFilterData.PC);
			this.byId("slPlant").setSelectedKeys(oFilterData.Plant);
			this.byId("slDivision").setSelectedKeys(oFilterData.D);
			this.byId("slLocation").setSelectedKeys(oFilterData.L);
			this.byId("slSalesOrg").setSelectedKeys(oFilterData.SO);
			this.byId("slPurcOrg").setSelectedKeys(oFilterData.PO);
			this.byId("slDistrChannel").setSelectedKeys(oFilterData.DC);
			this.byId("slUserAccess1").setSelectedKeys(oFilterData.UD1);
			this.byId("slUserAccess2").setSelectedKeys(oFilterData.UD2);
			this.byId("slUserAccess3").setSelectedKeys(oFilterData.UD3);
			this.byId("slUserAccess4").setSelectedKeys(oFilterData.UD4);
			this.byId("slUserAccess5").setSelectedKeys(oFilterData.UD5);
			this.byId("slUserAccess6").setSelectedKeys(oFilterData.UD6);
		},

		/**
		 * validate filter based on Filter data from Data Status Screen
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oFilterData Filter data object
		 * @return {Object} oSearchInfo
		 */
		_apiFilter: function (oFilterData) {
			// debugger; //eslint-disable-line
			if (oFilterData.dataType === "gstr1") {
				this.byId("slInvDataType").setSelectedKey("outward");
				if (oFilterData.status !== "E") {
					var vReturnType = "GSTR1";
				}
			} else {
				this.byId("slInvDataType").setSelectedKey(oFilterData.dataType);
				if (oFilterData.status !== "E") {
					vReturnType = oFilterData.dataType === "outward" ? "ANX1" : "ANX2";
				}
			}
			$.sap.returnType = vReturnType;
			this.byId("slInvCriteria").setSelectedKey(oFilterData.criteria);
			if (oFilterData.criteria === "RETURN_DATE_SEARCH") {
				var flag = true;
				this.byId("iFromPeriod").setDateValue(oFilterData.fromDate);
				this.byId("iToPeriod").setDateValue(oFilterData.toDate);
				this.byId("iToPeriod").setMinDate(oFilterData.fromDate);
			} else {
				flag = false;
				this.byId("iFromDate").setDateValue(oFilterData.fromDate);
				this.byId("iToDate").setDateValue(oFilterData.toDate);
				this.byId("iToDate").setMinDate(oFilterData.fromDate);
			}
			// Modified by Bharat Gupta on 30.11.2019
			var vDataType = oFilterData.dataType === "gstr1" ? "outward" : oFilterData.dataType;
			this._setFilterValue(vDataType); // End of Code by Bharat Gupta
			this.byId("slReturnType").setSelectedKey(vReturnType); // Modified by Ram Mahato on 19.11.2019
			this._changeReturnType(vReturnType); // End of Code by Ram Mahato

			if (oFilterData.gstin) {
				// this.byId("slInvEntity").setSelectedKey(oFilterData.entity);
				// this._getDataSecurity(oFilterData.entity);
				this.byId("slInvGstin").setSelectedKeys(oFilterData.gstin);
			}
			if (oFilterData.tableNo) { // Table Number - Added by Bharat Gupta on 16.12.2019
				this._bindTableNumber(oFilterData.tableNo);
			}
			if (oFilterData.validation) { // Validation Type (i.e. Business Validation or Structure Validation)
				this.byId("slValidation").setSelectedKey(oFilterData.validation);
			}
			if (oFilterData.dataSecAttrs) { // Data Security Filters
				this._bindAdaptFilter(oFilterData.dataSecAttrs);
			}
			var oModel = this.getView().getModel("Properties");
			oModel.getData().returnPeriod = flag;
			oModel.refresh(true);
			return this._getSearchInfo(oFilterData.status, 1);
		},

		/**
		 * validate filter based on Filter data from Data Status Screen
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oFilterData Filter data object
		 * @return {Object} oSearchInfo
		 */
		_apiGstnFilter: function (oFilterData) {
			// debugger; //eslint-disable-line
			if (oFilterData.dataType === "gstr1") {
				this.byId("slGstnDataType").setSelectedKey("outward");
				if (oFilterData.status !== "E") {
					var vReturnType = "GSTR1";
				}
			} else {
				this.byId("slGstnDataType").setSelectedKey(oFilterData.dataType);
				if (oFilterData.status !== "E") {
					vReturnType = oFilterData.dataType === "outward" ? "ANX1" : "ANX2";
				} else if (oFilterData.returnType) {
					vReturnType = oFilterData.returnType.toUpperCase();
				}
			}
			$.sap.returnType = vReturnType;
			this.byId("gFromPeriod").setDateValue(oFilterData.fromDate);
			this.byId("gToPeriod").setDateValue(oFilterData.toDate);
			this.byId("gToPeriod").setMinDate(oFilterData.fromDate);

			var vDataType = oFilterData.dataType === "gstr1" ? "outward" : oFilterData.dataType;
			this._setFilterValue(vDataType);

			this.byId("slGstnReturnType").setSelectedKey(vReturnType);
			this._changeReturnType(vReturnType);

			if (oFilterData.gstin) {
				// this.byId("slGstnEntity").setSelectedKey(oFilterData.entity);
				// this._getDataSecurity(oFilterData.entity);
				this.byId("slGstnGstin").setSelectedKeys(oFilterData.gstin);
			}
			if (oFilterData.tableNo) { // Table Number
				this._bindTableNumber(oFilterData.tableNo);
			}
			if (oFilterData.refId) { // Reference Id
				this.byId("idGstnRefId").setValue(oFilterData.refId);
			}
			if (oFilterData.dataSecAttrs) { // Data Security Filters
				this._bindAdaptFilter(oFilterData.dataSecAttrs);
			}
			return this._getSearchInfo(oFilterData.status, 1);
		},

		/**
		 * validate filter based on Filter data from Data Status Screen for Excel File upload
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oFilterData Filter data object
		 * @return {Object} oSearchInfo
		 */
		_fileStatusFilter: function (oFilterData) {
			// debugger; //eslint-disable-line
			var oPropModel = this.getView().getModel("Properties");
			oPropModel.getData().returnPeriod = false;
			oPropModel.refresh(true);

			// Added by Bharat Gupta on 30.11.2019
			if (oFilterData.dataType === "gstr1") {
				this.byId("slInvDataType").setSelectedKey("outward");
				$.sap.returnType = "GSTR1";
			} else {
				this.byId("slInvDataType").setSelectedKey(oFilterData.dataType);
				$.sap.returnType = oFilterData.dataType;
			} // End of Code by Bharat Gupta
			this.byId("slInvCriteria").setSelectedKey("RECEIVED_DATE_SEARCH");
			this.byId("iFromDate").setDateValue(oFilterData.fromDate);
			this.byId("iToDate").setDateValue(oFilterData.toDate);
			// Added by Bharat Gupta on 30.11.2019
			if (oFilterData.validation) {
				this.byId("slValidation").setSelectedKey(oFilterData.validation);
			} // End of Code by Bharat Gupta
			return this._getSearchInfo(oFilterData.status, 1);
		},

		/**
		 * Modified by Bharat Gupta on 30.11.2019
		 * Called to get data ba Data Status Filter value
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oFilterModel Filter model reference
		 */
		_bindforDataStatusFilter: function (oFilterModel) {
			// debugger; //eslint-disable-line
			var oFilterData = oFilterModel.getData(),
				bSegment = oFilterData.status === "E" ? "error" : "process";

			this.byId("sbInvType").setSelectedKey(oFilterData.segment);
			this.byId("sbManage").setSelectedKey(bSegment);
			this._changeSegmentBtn();
			if (oFilterData.segment === "asp") {
				this._clearFilterValue();
			} else {
				this._clearGstnFilterValue();
			}
			if (oFilterData.dataOriginType === "E") {
				var oSearchInfo = this._fileStatusFilter(oFilterData);

			} else if (oFilterData.segment === "asp") {
				oSearchInfo = this._apiFilter(oFilterData);

			} else if (oFilterData.segment === "gstn") {
				oSearchInfo = this._apiGstnFilter(oFilterData);
			}
			this._getAspInvoices(oSearchInfo);
		},

		/**
		 * Modified by Bharat Gupta on 30.11.2019
		 * Method to Show/hide filter component as per selected Segement button
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_changeSegmentBtn: function () {
			// debugger; //eslint-disable-line
			var vInvType = this.byId("sbInvType").getSelectedKey(),
				vType = this.byId("sbManage").getSelectedKey(),
				oModel = this.getView().getModel("Properties"),
				oData = oModel.getData(),
				flag = false;

			if (vType === "process") {
				flag = true;
			}
			oData.aspView = vInvType === "gstn" ? false : true; // Modified on 29.11.2019
			oData.returnType = flag;
			oData.tableNo = flag;
			oData.gstnStatus = flag;
			oData.cpAction = flag;
			oData.validation = !flag;
			oModel.refresh(true);

			this.byId("dpHeader").setVisible(true);
			this.byId("bManageBack").setVisible(false);
			this.byId("hbError").setVisible(!flag);
			// this.byId("bCounterParty").setVisible(flag);
			// this.byId("bEditProcess").setVisible(flag);
			this.byId("tabInvProcess").setVisible(flag);
			this.byId("tabInvProcess").setSelectedIndex(-1);
			this.byId("tabInvoiceError").setVisible(!flag);
		},

		/**
		 * Developed by Jakeer Syed
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing Object
		 */
		onChangeReturnType: function (oEvent) {
			// debugger; //eslint-disable-line
			var vReturnType = oEvent.getSource().getSelectedKey();
			this._changeReturnType(vReturnType);
			this.getOwnerComponent().setModel(null, "FilterInvoice");
		},

		_changeReturnType: function (vReturnType) {
			// debugger; //eslint-disable-line
			var vInvType = this.byId("sbInvType").getSelectedKey(),
				oTableNumber = this.getOwnerComponent().getModel("DropDown").getData().tableNumber,
				oTableNoData = [];
			if (vInvType === "asp") {
				var vDataType = this.byId("slInvDataType").getSelectedKey(),
					vTabNumber = "slTabNumber";
			} else {
				vDataType = this.byId("slGstnDataType").getSelectedKey();
				vTabNumber = "slGstnTabNumber";
			}
			if (vDataType === "outward" && vReturnType === "ANX1") {
				oTableNoData = oTableNumber.anx1Outward;

			} else if (vDataType === "outward" && vReturnType === "RET1") {
				oTableNoData = oTableNumber.ret1Outward;

			} else if (vDataType === "outward" && vReturnType === "ANX1A") {
				oTableNoData = oTableNumber.anx1aOutward;

			} else if (vDataType === "outward" && vReturnType === "RET1A") {
				oTableNoData = oTableNumber.ret1aOutward;

			} else if (vDataType === "outward" && vReturnType === "GSTR1") {
				oTableNoData = oTableNumber.gstr1Outward;

			} else if (vDataType === "inward" && vReturnType === "ANX1") {
				oTableNoData = oTableNumber.anx1Inward;

			} else if (vDataType === "inward" && vReturnType === "ANX2") {
				// oTableNoData = oTableNumber.ret1Outward;

			} else if (vDataType === "inward" && vReturnType === "RET1") {
				// oTableNoData = oTableNumber.ret1Outward;

			} else if (vDataType === "inward" && vReturnType === "ANX1A") {
				// oTableNoData = oTableNumber.anx1aOutward;

			} else if (vDataType === "inward" && vReturnType === "RET1A") {
				// oTableNoData = oTableNumber.ret1aOutward;

			} else if (vDataType === "inward" && vReturnType === "GSTR6") {
				// oTableNoData = oTableNumber.ret1aOutward;
			}
			this.byId(vTabNumber).setModel(new JSONModel(oTableNoData), "TableNumber");
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
		 * Validate Date Range Selection for Date / Period
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeDateValue: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getId().includes("iFromDate")) {
				var vDatePicker = "iToDate";
			} else if (oEvent.getSource().getId().includes("iFromPeriod")) {
				vDatePicker = "iToPeriod";
			} else if (oEvent.getSource().getId().includes("gFromPeriod")) {
				vDatePicker = "gToPeriod";
			}
			var fromDate = oEvent.getSource().getDateValue(),
				toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
			this.getOwnerComponent().setModel(null, "FilterInvoice");
		},

		/**
		 * Called to set Invoice Filter Model as Null
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onChangeValue: function () {
			this.getOwnerComponent().setModel(null, "FilterInvoice");
		},

		/**
		 * Developed by Bharat Gupta
		 * Called when user press segment button to display related table and data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onChangeSegment: function () {
			// debugger; //eslint-disable-line
			this._changeSegmentBtn();
			var oSearchInfo = this._getSearchInfo(this._getDataType(), 1);
			this._getAspInvoices(oSearchInfo);
		},

		/**
		 * Called when user change DataType, casecading DocType & Return Type based on selected DataType
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onSelectChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var vKey = oEvent.getSource().getSelectedKey();

			this._setFilterValue(vKey);
			if (oEvent.getSource().getId().includes("slInvDataType")) {
				this._changeReturnType(this.byId("slReturnType").getSelectedKey());
			} else {
				this._changeReturnType(this.byId("slGstnReturnType").getSelectedKey());
			}
			this.getOwnerComponent().setModel(null, "FilterInvoice");
		},

		/**
		 * Modified by Bharat Gupta on 30.11.2019
		 * Method to Bind Return Type and Doc Type Drop Down value based on Data Type
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {string} vKey Key value to search data
		 */
		_setFilterValue: function (vKey) {
			// debugger; //eslint-disable-line
			var oData = this.getOwnerComponent().getModel("DropDown").getData(),
				oModel = this.getView().getModel("Properties"),
				vInvType = this.byId("sbInvType").getSelectedKey(),
				aData = oModel.getData();

			if (vInvType === "asp") {
				var vReturnType = "slReturnType",
					vDocType = "slDocType";
			} else {
				vReturnType = "slGstnReturnType";
				vDocType = "slGstnDocType";
			}
			aData.outward = (vKey === "outward" ? true : false);
			oModel.refresh(true);

			var oReturnModel = this.byId(vReturnType).getModel("ReturnType");
			oReturnModel.setData(oData.returnType[vKey]);
			oReturnModel.refresh(true);

			var oDocTypeModel = this.byId(vDocType).getModel("DocType");
			oDocTypeModel.setData(oData.docType[vKey]);
			oDocTypeModel.refresh(true);
		},

		/**
		 * Called when user changed entity in selection box
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent eventing parameter
		 */
		onEntityChange: function (oEvent) {
			this.getOwnerComponent().setModel(null, "FilterInvoice");
			this._getDataSecurity(oEvent.getSource().getSelectedKey());
		},

		/**
		 * Called when user changed entity in selection box for GSTN
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent eventing parameter
		 */
		onChangeGstnEntity: function (oEvent) {
			this._getGstnDataSecurity(oEvent.getSource().getSelectedKey());
		},

		/**
		 * Method called to get Data Security attributes for selected entity from table
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {string} vEntity Entity Id
		 */
		_getGstnDataSecurity: function (vEntity) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAnxDataSecApplAttr.do",
				contentType: "application/json",
				async: false,
				data: JSON.stringify({
					"req": {
						"entityId": vEntity
					}
				})
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var oModel = that.getView().getModel("GstnSecurity");
				if (!oModel) {
					that.getView().setModel(new JSONModel(data.resp), "GstnSecurity");
				} else {
					oModel.setData(data.resp);
					oModel.refresh(true);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called when user change Criteria to change Date range selection type
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onChangeCriteria: function (oEvent) {
			if (oEvent.getSource().getSelectedKey() === "RETURN_DATE_SEARCH") {
				var flag = true;
			} else {
				flag = false;
			}
			var oModel = this.getView().getModel("Properties");
			oModel.getData().returnPeriod = flag;
			oModel.refresh(true);
			this.getOwnerComponent().setModel(null, "FilterInvoice");
		},

		/**
		 * Return Invoice Management Type (like: Process/Error)
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @return {string} vType
		 */
		_getDataType: function () {
			var vType = this.byId("sbManage").getSelectedKey() === "process" ? "P" : "E";
			if (vType === "P") {
				var oModel = this.byId("tabInvProcess").getModel("Processed");
			} else {
				oModel = this.byId("tabInvoiceError").getModel("InvoiceError");
			}
			if (oModel) {
				oModel.setData(null);
			}
			return vType;
		},

		/**
		 * Called when user press search button to get data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onSearch: function () {
			var status = this._getDataType(),
				oSearchInfo = this._getSearchInfo(status, 1),
				vInvType = this.byId("sbInvType").getSelectedKey();

			if (this.byId("dAdapt") && vInvType === "asp") {
				this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			} else if (vInvType === "gstn") { // Modified by Bharat Gupta for GSTN api implementation on 26.11.2019
				if (this.byId("dGstnAdapt")) {
					this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
				}
			}
			this._getAspInvoices(oSearchInfo);
		},

		/**
		 * Called to open Adapt filter to add additional filter to get data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onPressAdaptFilter: function () {
			if (this.byId("sbInvType").getSelectedKey() === "asp") {
				if (!this._oAdpatFilter) {
					this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManage.AdaptFilter", this);
					this.getView().addDependent(this._oAdpatFilter);
				}
				this._oAdpatFilter.open();
			} else {
				if (!this._oGstnAdaptFilter) {
					this._oGstnAdaptFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManage.AdaptFilterGstn", this);
					this.getView().addDependent(this._oGstnAdaptFilter);
				}
				this._oGstnAdaptFilter.open();
			}
		},

		/**
		 * Call when user press on Apply or Cancel button in Adapt Filter dialog
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onPressFilterClose: function (oEvent) {
			// debugger; //eslint-disable-line
			var status = this._getDataType(),
				oSearchInfo = this._getSearchInfo(status, 1);

			if (this.byId("sbInvType").getSelectedKey() === "asp") {
				this._oAdpatFilter.close();
				if (oEvent.getSource().getId().includes("bApply")) {
					this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);
					this._getAspInvoices(oSearchInfo);
				}
			} else if (this.byId("sbInvType").getSelectedKey() === "gstn") {
				this._oGstnAdaptFilter.close();
				if (oEvent.getSource().getId().includes("bGstnApply")) {
					this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
					this._getAspInvoices(oSearchInfo);
				}
			}
		},

		/**
		 * Pagination to handle large amount of data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressPagination: function (oEvent) {
			// debugger; //eslint-disable-line
			var vInvType = this.byId("sbInvType").getSelectedKey(),
				vSB = this.byId("sbManage").getSelectedKey();

			if (vSB === "process") {
				var vInput = "inPageNo",
					vBtnPrev = "btnPrev",
					vBtnNext = "btnNext",
					txtPageNo = "txtPageNo";
			} else {
				vInput = "inPageNoErr";
				vBtnPrev = "bPrevErr";
				vBtnNext = "bNextErr";
				txtPageNo = "txtPageNoErr";
			}
			var vValue = parseInt(this.byId(vInput).getValue(), 10);
			if (oEvent.getSource().getId().includes(vBtnPrev)) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId(vBtnPrev).setEnabled(false);
				}
				if (!this.byId(vBtnNext).getEnabled()) {
					this.byId(vBtnNext).setEnabled(true);
				}
			} else {
				var vPageNo = parseInt(this.byId(txtPageNo).getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId(vBtnPrev).setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId(vBtnNext).setEnabled(false);
				}
			}
			this.byId(vInput).setValue(vValue);
			var status = this._getDataType(),
				oSearchInfo = this._getSearchInfo(status, vValue);

			if (this.byId("dAdapt") && vInvType === "asp") {
				this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			} else if (vInvType === "gstn") {
				if (this.byId("dGstnAdapt")) {
					this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
				}
			}
			this._getAspInvoices(oSearchInfo);
		},

		/**
		 * Called when user enter page number in input box and press enter or focus out of input box
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSubmitPagination: function () {
			// debugger; //eslint-disable-line
			if (this.byId("sbManage").getSelectedKey() === "process") {
				// var vPageNo = this.byId("inPageNo").getValue();
				var vInputId = "inPageNo";
			} else {
				// vPageNo = this.byId("inPageNoErr").getValue();
				vInputId = "inPageNoErr";
			}
			var vPageNo = this.byId(vInputId).getValue();
			if (vPageNo < 1) {
				vPageNo = 1;
				this.byId(vInputId).setValue(vPageNo);
			}
			var oSearchInfo = this._getSearchInfo(this._getDataType(), vPageNo);

			if (this.byId("dAdapt") && this.byId("sbInvType").getSelectedKey() === "asp") {
				this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			} else if (this.byId("sbInvType").getSelectedKey() === "gstn") {
				if (this.byId("dGstnAdapt")) {
					this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
				}
			}
			this._getAspInvoices(oSearchInfo);
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
					"entityId": [$.sap.entityID],
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
					"refId": null, // Added by Bharat Gupta for GSTN api call on 09.12.2019
					"showGstnData": false, // Modified by Bharat Gupta for GSTN api implementation on 26.11.2019
					"dataSecAttrs": {
						"GSTIN": null
					}
				}
			};
		},

		/**
		 * Called to create payload to get data from server
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {string} status Records Type
		 * @param {int} vPageNo Page Number
		 * @return {Object} searchInfo
		 */
		_getSearchInfo: function (status, vPageNo) {
			// debugger; //eslint-disable-line
			var searchInfo = this._getPayloadStruct();

			// Payload Header body
			searchInfo.hdr.pageNum = vPageNo - 1;
			searchInfo.hdr.pageSize = 50;

			// Payload Request body
			searchInfo.req.processingStatus = status;

			if (this.byId("sbInvType").getSelectedKey() === "asp") {
				this._aspSearchInfo(searchInfo);
			} else {
				this._gstnSearchInfo(searchInfo);
			}
			return searchInfo;
		},

		/**
		 * Create Payload structure to search data for ASP Invoice Management
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} searchInfo Search Criteria
		 */
		_aspSearchInfo: function (searchInfo) {
			var oFilterModel = this.getOwnerComponent().getModel("FilterInvoice");

			// searchInfo.req.entityId.push($.sap.entityID); // this.byId("slInvEntity").getSelectedKey()); // Entity Id
			searchInfo.req.docType = this.byId("slDocType").getSelectedKeys(); // Document Type
			searchInfo.req.tableNumber = this.byId("slTabNumber").getSelectedKeys(); // Table Number
			searchInfo.req.gstnStatus = this.byId("slGstnStatus").getSelectedKeys(); // GSTN Status
			searchInfo.req.counterPartyGstin = this.byId("slCpGstin").getSelectedKeys(); // Counter Party Gstin
			searchInfo.req.counterPartyFlag = null; // Counter Party Action
			searchInfo.req.dataSecAttrs.GSTIN = this.byId("slInvGstin").getSelectedKeys();

			if (this.byId("slReturnType").getSelectedKey() !== "") { // Return Type
				searchInfo.req.returnType = [this.byId("slReturnType").getSelectedKey()];
			}
			switch (this.byId("slInvCriteria").getSelectedKey()) {
			case "RECEIVED_DATE_SEARCH":
				searchInfo.req.receivFromDate = this.byId("iFromDate").getValue(); // Received Date
				searchInfo.req.receivToDate = this.byId("iToDate").getValue();
				break;
			case "RETURN_DATE_SEARCH":
				searchInfo.req.returnFromDate = this.byId("iFromPeriod").getValue(); // Return Period
				searchInfo.req.returnToDate = this.byId("iToPeriod").getValue();
				break;
			}
			if (this.byId("idInvDocNo").getValue() !== "") {
				searchInfo.req.docNo = this.byId("idInvDocNo").getValue();
			}
			if (oFilterModel) {
				var oFilterData = oFilterModel.getData();
				searchInfo.req.fileId = oFilterData.fileId || null;
				searchInfo.req.dataOriginTypeCode = oFilterData.dataOriginType || null;
				if (oFilterData.receivedDate) {
					searchInfo.req.receivFromDate = oFilterData.receivedDate;
					searchInfo.req.receivToDate = oFilterData.receivedDate;
				}
				if (searchInfo.req.fileId) {
					searchInfo.req.entityId = [];
				}
			}
		},

		/**
		 * Create Payload structure to search data for GSTN Invoice Management
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} searchInfo Search Criteria
		 */
		_gstnSearchInfo: function (searchInfo) {
			// debugger; //eslint-disable-line
			// searchInfo.req.entityId.push(this.byId("slGstnEntity").getSelectedKey()); // Entity Id
			searchInfo.req.dataSecAttrs.GSTIN = this.byId("slGstnGstin").getSelectedKeys(); // Gstin(s)
			searchInfo.req.returnFromDate = this.byId("gFromPeriod").getValue(); // From Period
			searchInfo.req.returnToDate = this.byId("gToPeriod").getValue(); // To Period

			if (this.byId("slGstnReturnType").getSelectedKey() !== "") { // Return Type
				searchInfo.req.returnType = [this.byId("slGstnReturnType").getSelectedKey()];
			}
			searchInfo.req.tableNumber = this.byId("slGstnTabNumber").getSelectedKeys(); // Table Number
			searchInfo.req.docType = this.byId("slGstnDocType").getSelectedKeys(); // Doc Type

			if (this.byId("idGstnDocNo").getValue() !== "") { // Document Number
				searchInfo.req.docNo = this.byId("idGstnDocNo").getValue();
			}
			if (this.byId("idGstnRefId").getValue() !== "") { // Reference Id
				searchInfo.req.refId = this.byId("idGstnRefId").getValue();
			}
			searchInfo.req.showGstnData = true;
		},

		/**
		 * Called to bind ASP Adapt filter values
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} search Search Info Process
		 */
		_getOtherFiltersASP: function (search) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items,
				// this.getView().getModel("DataSecurity").getData().items,
				vDataType = this.byId("slInvDataType").getSelectedKey();

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
			if (oDataSecurity.purchOrg && vDataType === "inward") {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg && vDataType === "outward") {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel && vDataType === "outward") {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userDefined1) {
				search.UD1 = this.byId("slUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userDefined2) {
				search.UD2 = this.byId("slUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userDefined3) {
				search.UD3 = this.byId("slUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userDefined4) {
				search.UD4 = this.byId("slUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userDefined5) {
				search.UD5 = this.byId("slUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userDefined6) {
				search.UD6 = this.byId("slUserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * Called to bind ASP Adapt filter values
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} search Search Info Process
		 */
		_getOtherFiltersGSTN: function (search) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items,
				// this.getView().getModel("GstnSecurity").getData().items,
				vDataType = this.byId("slGstnDataType").getSelectedKey();

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slGstnProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slGstnPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slGstnDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slGstnLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg && vDataType === "inward") {
				search.PO = this.byId("slGstnPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg && vDataType === "outward") {
				search.SO = this.byId("slGstnSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel && vDataType === "outward") {
				search.DC = this.byId("slGstnDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userDefined1) {
				search.UD1 = this.byId("slGstnUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userDefined2) {
				search.UD2 = this.byId("slGstnUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userDefined3) {
				search.UD3 = this.byId("slGstnUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userDefined4) {
				search.UD4 = this.byId("slGstnUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userDefined5) {
				search.UD5 = this.byId("slGstnUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userDefined6) {
				search.UD6 = this.byId("slGstnUserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * Return ASP url to get Data from Database
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @return {string} vUrl
		 */
		_getAspUrl: function () {
			// debugger; //eslint-disable-line
			var oPropModel = this.getView().getModel("Properties"),
				vInvType = this.byId("sbInvType").getSelectedKey(),
				vButton = this.byId("sbManage").getSelectedKey(),
				vUrl;
			$.sap.myVar = null;

			if (vInvType === "asp") {
				var vDataType = this.byId("slInvDataType").getSelectedKey();
			} else {
				vDataType = this.byId("slGstnDataType").getSelectedKey();
			}

			if (vDataType === "outward" || vDataType === "gstr1") { // Modified by Ram on 20.11.2019
				oPropModel.getData().colVisi = true;
				vUrl = "/aspsapapi/docSearch.do";
				if (vInvType === "asp" && vButton === "error" && this.byId("slValidation").getSelectedKey() === "SV") {
					$.sap.myVar = "SV";
					vUrl = "/aspsapapi/outwardSvErrDocSearch.do";
				}
			} else {
				oPropModel.getData().colVisi = false;
				vUrl = "/aspsapapi/inwardDocSearch.do";
				if (vInvType === "asp" && vButton === "error" && this.byId("slValidation").getSelectedKey() === "SV") {
					$.sap.myVar = "SV";
					vUrl = "/aspsapapi/inwardSvErrDocSearch.do";
				}
			}
			oPropModel.refresh(true);
			return vUrl; //eslint-disable-line
		},

		/**
		 * Called to get ASP Invoices data from Server
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} payload Search criteria object
		 */
		_getAspInvoices: function (payload) {
			// debugger; //eslint-disable-line
			if (!$.sap.entityID) { // (!this.byId("slInvEntity").getSelectedKey() || this.byId("slInvEntity").getSelectedKey() === "") {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgNoData"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var that = this,
				vUrl = this._getAspUrl(),
				oDataType = {
					"dataType": this.byId("slInvDataType").getSelectedKey()
				};

			this.byId("txtRecords").setText("Records: 0");
			this.getView().setModel(new JSONModel(payload.req), "Payload");
			this.getView().setModel(new JSONModel(oDataType), "DataType");
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: vUrl,
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._bindAspInvoice(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that._serverMessage(jqXHR.status);
				});
			});
		},

		/**
		 * Called to bind ASP Invoice data object
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Data object
		 */
		_bindAspInvoice: function (data) {
			// debugger; //eslint-disable-line
			var vInvType = this.byId("sbInvType").getSelectedKey();
			if ((vInvType === "asp" && this.getView().byId("slReturnType").getSelectedKey() === "") ||
				(vInvType === "gstn" && this.getView().byId("slGstnReturnType").getSelectedKey() === "")) {
				var selected = $.sap.returnType;

			} else if (vInvType === "asp") {
				selected = this.getView().byId("slReturnType").getSelectedKey();

			} else if (vInvType === "gstn") {
				selected = this.getView().byId("slGstnReturnType").getSelectedKey();
			}

			if (data.resp.length > 0) {
				var vRecord = (data.hdr.pageNum * data.hdr.pageSize + 1) + "-" + (data.hdr.pageNum * data.hdr.pageSize + data.resp.length);
				this.byId("txtRecords").setText("Records: " + vRecord + " / " + data.hdr.totalCount);
			}
			if (data.resp.length === 0) {
				this._infoMessage("Records does not exist.");
			}
			if (this.byId("sbManage").getSelectedKey() === "process") {
				this._aspProcessPagination(data.hdr);
				var oData = [];
				for (var i = 0; i < data.resp.length; i++) {
					// changes By jakeer 08.11.2019
					if (selected === "GSTR1" || selected === "") {
						data.resp[i].returnType = data.resp[i].gstrReturnType;
						data.resp[i].an1Gstr1SubCategory = data.resp[i].gstr1SubCategory;
						data.resp[i].an1Gstr1TableNo = data.resp[i].gstr1TableNo;
					}
					// End by Jakeer
					oData.push(this._processData(data.resp[i]));
				}
				this.byId("tabInvProcess").setModel(new JSONModel(oData), "Processed");
			} else {
				this._aspErrorPagination(data.hdr);
				this._aspErrorData(data.hdr, data.resp);
				this.byId("tabInvoiceError").setModel(new JSONModel(data.resp), "InvoiceError");
			}
		},

		/**
		 * Called to add error and information code & description in object
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Header Object
		 * @param {Object} data Response object
		 */
		_aspErrorData: function (header, data) {
			// debugger; //eslint-disable-line
			for (var i = 0; i < data.length; i++) {
				data[i].sNo = (header.pageNum * header.pageSize) + (i + 1);
				var oErrorList = this._errorInfoList(data[i]);
				data[i].errorCode = oErrorList.errCode;
				data[i].errorDesc = oErrorList.errDesc;
				data[i].infoCode = oErrorList.infoCode;
				data[i].infoDesc = oErrorList.infoDesc;
			}
		},

		/**
		 * Called to get Error & Information List
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Data objectṇ
		 * @return {Object} oInfoList
		 */
		_errorInfoList: function (data) {
			var oInfoList = {};
			if (data.errorList) {
				var infoCode = [],
					infoDesc = [],
					errCode = [],
					errDesc = [];
				for (var i = 0; i < data.errorList.length; i++) {
					if (data.errorList[i].errorType === "INFO") {
						this._pushInfoErrDesc(data.errorList[i], infoCode, infoDesc);
						// if (!infoCode.includes(data.errorList[i].errorCode)) {
						// 	infoCode.push(data.errorList[i].errorCode);
						// 	if (data.errorList[i].errorDesc) {
						// 		infoDesc.push(data.errorList[i].errorCode + ": " + data.errorList[i].errorDesc.trim());
						// 	}
						// }
					} else {
						this._pushInfoErrDesc(data.errorList[i], errCode, errDesc);
						// if (!errCode.includes(data.errorList[i].errorCode)) {
						// errCode.push(data.errorList[i].errorCode);
						// if (data.errorList[i].errorDesc) {
						// 	errDesc.push(data.errorList[i].errorCode + ": " + data.errorList[i].errorDesc.trim());
						// }
						// }
					}
				}
				oInfoList.infoCode = infoCode.toString();
				oInfoList.infoDesc = infoDesc.toString();
				oInfoList.infoDesc = oInfoList.infoDesc.replace(/,/g, "\n");
				oInfoList.errCode = errCode.toString();
				oInfoList.errDesc = errDesc.toString();
				oInfoList.errDesc = oInfoList.errDesc.replace(/,/g, "\n");
			}
			return oInfoList;
		},

		/**
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} errData Error Object
		 * @param {Object} code Arrays of Error/Info code object
		 * @param {Object} desc Arrays of Error/Info description object
		 */
		_pushInfoErrDesc: function (errData, code, desc) {
			if (!code.includes(errData.errorCode)) {
				code.push(errData.errorCode);
				if (errData.errorDesc) {
					desc.push(errData.errorCode + ": " + errData.errorDesc.trim());
				}
			}
		},

		/**
		 * JSON Data fromat for Process Data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Inward process object
		 * @param {Object} aInvData Invoice data
		 * @param {int} i HSN line item number
		 */
		_infoList: function (data, aInvData, i) {
			// debugger; //eslint-disable-line
			var aInfoCode = aInvData.infoCode ? aInvData.infoCode.split(",") : [],
				errorDesc = null;
			for (var k = 0; k < data.errorList.length; k++) {
				if (data.errorList[k].index === i && !aInfoCode.includes(data.errorList[k].errorCode)) {
					errorDesc = data.errorList[k].errorCode + ": " + data.errorList[k].errorDesc;
					aInfoCode.push(data.errorList[k].errorCode);
					aInvData.infoCode = aInfoCode.toString(); //(!aInvData.infoCode ? data.errorList[k].errorCode : aInvData.infoCode + "," + data.errorList[k].errorCode);
					aInvData.infoDesc = (!aInvData.infoDesc ? errorDesc : aInvData.infoDesc + ",\n" + errorDesc);
				}
			}
		},

		/**
		 * JSON Data fromat for Process Data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Inward process object
		 * @return {Object} aInvData
		 */
		_processData: function (data) {
			// debugger; //eslint-disable-line
			var oInfo = this._errorInfoList(data),
				aInvData = JSON.parse(JSON.stringify(data));

			if (this.byId("slInvDataType").getSelectedKey() === "inward") {
				aInvData.an1Gstr1SubCategory = data.anxSubCategory;
				aInvData.an1Gstr1TableNo = data.anxTableNo;
			}
			aInvData.view = "Invoice";
			aInvData.hsnItems = [];
			aInvData.hsnItems.push(this._headerAspJson(data, oInfo, 0));

			for (var i = 0; i < data.lineItems.length; i++) {
				for (var j = 0; j < aInvData.hsnItems.length; j++) {
					if (data.lineItems[i].hsnsacCode === aInvData.hsnItems[j].hsnsacCode &&
						data.lineItems[i].igstRate === aInvData.hsnItems[j].igstRate) {
						aInvData.hsnItems[j].lineItems.push(data.lineItems[i]);
						this._calculateRateTax(aInvData.hsnItems[j], data.lineItems[i]);
						if (data.errorList) {
							this._infoList(data, aInvData.hsnItems[j], i);
						}
						break;
					}
				}
				if (j === aInvData.hsnItems.length) {
					var aRateData = this._headerAspJson(data, oInfo, i);
					this._calculateRateTax(aRateData, data.lineItems[i]);
					aRateData.lineItems.push(data.lineItems[i]);
					if (data.errorList) {
						this._infoList(data, aRateData, i);
					}
					aInvData.hsnItems.push(aRateData);
				}
			}
			for (i = 0; i < aInvData.hsnItems.length; i++) {
				aInvData.infoCode = oInfo.infoCode; //(!aInvData.infoCode ? aInvData.hsnItems[i].infoCode : aInvData.infoCode + "," + aInvData.hsnItems[i].infoCode);
				aInvData.infoDesc = oInfo.infoDesc; //(!aInvData.infoDesc ? aInvData.hsnItems[i].infoDesc : aInvData.infoDesc + "," + aInvData.hsnItems[i].infoDesc);
			}
			if (aInvData.infoDesc) {
				aInvData.infoDesc = aInvData.infoDesc.replace(/,/g, "\n");
			}
			return aInvData;
		},

		/**
		 * Calculate Taxable Value and Total Taxes for each Invoice Rate level
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} obj Responase Object
		 * @param {Object} data Response lineitem
		 */
		_calculateRateTax: function (obj, data) {
			obj.taxableValue = (!obj.taxableValue ? 0 : obj.taxableValue) + data.taxableVal;
			obj.taxPayable = (!obj.taxPayable ? 0 : obj.taxPayable) + data.taxPayable;
		},

		/**
		 * Calculate data for HSN+Rate level for each Invoice
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data Response object
		 * @param {Object} info Error & Info object
		 * @param {int} idx Lineitem index number
		 * @return {Object} obj
		 */
		_headerAspJson: function (data, info, idx) {
			// debugger; //eslint-disable-line
			var obj = {};
			obj.view = "HSN + Rate";
			obj.returnType = data.returnType;
			obj.returnPeriod = data.returnPeriod;
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				obj.an1Gstr1SubCategory = data.an1Gstr1SubCategory;
				obj.an1Gstr1TableNo = data.an1Gstr1TableNo;
			} else {
				obj.an1Gstr1SubCategory = data.anxSubCategory;
				obj.an1Gstr1TableNo = data.anxTableNo;
			}
			obj.suppGstin = data.suppGstin;
			obj.custGstin = data.custGstin;
			obj.docType = data.docType;
			obj.docNo = data.docNo;
			obj.docDate = data.docDate;
			// obj.infoCode = (!obj.infoCode ? info.infoCode : obj.infoCode + "," + info.infoCode);
			// obj.infoDesc = (!obj.infoDesc ? info.infoDesc : obj.infoDesc + "," + info.infoDesc);
			if (data.lineItems.length > 0) {
				obj.hsnsacCode = data.lineItems[idx].hsnsacCode;
				obj.igstRt = data.lineItems[idx].igstRt;
				obj.sgstRt = data.lineItems[idx].cgstRt;
				obj.cgstRt = data.lineItems[idx].sgstRt;
			}
			obj.lineItems = [];
			return obj;
		},

		/**
		 * Pagination process for ASP Process records
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Response header
		 */
		_aspProcessPagination: function (header) { //(pageNumber) {
			var pageNumber = Math.ceil(header.totalCount / 50);
			this.byId("txtPageNo").setText("/ " + pageNumber);
			this.byId("inPageNo").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNo").setValue(pageNumber);
				this.byId("inPageNo").setEnabled(false);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnNext").setEnabled(false);

			} else if (this.byId("inPageNo").getValue() === "" || this.byId("inPageNo").getValue() === "0") {
				this.byId("inPageNo").setValue(1);
				this.byId("inPageNo").setEnabled(true);
				this.byId("btnPrev").setEnabled(false);
			} else {
				this.byId("inPageNo").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNo").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNext").setEnabled(true);
			} else {
				this.byId("btnNext").setEnabled(false);
			}
			this.byId("btnPrev").setEnabled(vPageNo > 1 ? true : false);
		},

		/**
		 * Pagination process for ASP Error records
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Response header
		 */
		_aspErrorPagination: function (header) {
			var pageNumber = Math.ceil(header.totalCount / 50);
			this.byId("txtPageNoErr").setText("/ " + pageNumber);
			this.byId("inPageNoErr").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoErr").setValue(pageNumber);
				this.byId("inPageNoErr").setEnabled(false);
				this.byId("bPrevErr").setEnabled(false);
				this.byId("bNextErr").setEnabled(false);

			} else if (this.byId("inPageNoErr").getValue() === "" || this.byId("inPageNoErr").getValue() === "0") {
				this.byId("inPageNoErr").setValue(1);
				this.byId("inPageNoErr").setEnabled(true);
				this.byId("bPrevErr").setEnabled(false);
			} else {
				this.byId("inPageNoErr").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoErr").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("bNextErr").setEnabled(true);
			} else {
				this.byId("bNextErr").setEnabled(false);
			}
			this.byId("bPrevErr").setEnabled(vPageNo > 1 ? true : false);
		},

		/**
		 * Called when user select any row in process screen and navigate to edit screen with select process records
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent eventing object
		 */
		onProcessRowSelection: function (oEvent) {
			// debugger; //eslint-disable-line
			var aPath = oEvent.getParameter("rowContext").getPath().split("/"),
				oData = this.byId("tabInvProcess").getModel("Processed").getData(),
				oTemp = JSON.parse(JSON.stringify(oData[aPath[1]]));

			if (oTemp.errorList) {
				this._aspErrorList(oTemp, oTemp.errorList);
			} else {
				oMessagePopover.setModel(null, "Msg");
			}
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this.byId("tabOutwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
			} else {
				this.byId("tabInwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
			}
			this._editInvoiceTab(false, oTemp.errorList);
		},

		/**
		 * Called when user select any row in error screen and navigate to edit screen with select error records
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent eventing object
		 */
		onErrorItemSelect: function (oEvent) {
			// debugger; //eslint-disable-line
			this._editInvoiceTab(false);
			var obj = oEvent.getSource().getBindingContext("InvoiceError").getObject();
			var oTemp = JSON.parse(JSON.stringify(obj));
			this._aspErrorList(oTemp, obj.errorList);
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this.byId("tabOutwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
			} else {
				this.byId("tabInwardEdit").setModel(new JSONModel(oTemp), "InvoiceItemModel");
			}
		},

		/**
		 * Event for Back button operation to Navigate back to Listing page
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onPressBack: function () {
			// debugger; //eslint-disable-line
			if (this.byId("sbInvType").getSelectedKey() === "asp" && this.byId("sbManage").getSelectedKey() === "process") {
				var oMsgModel = oMessagePopover.getModel("Msg");
				if (oMsgModel) {
					var oMsgData = oMsgModel.getData(),
						vIdxErr = oMsgData.findIndex(function (item, i) {
							return item.errorType === "ERR";
						});
					if (vIdxErr !== -1) {
						var oBundle = this.getResourceBundle(),
							that = this;
						sap.m.MessageBox.confirm(oBundle.getText("confirmInvBack"), {
							styleClass: "sapUiSizeCompact",
							onClose: function (oAction) {
								if (oAction === sap.m.MessageBox.Action.OK) {
									that._rollbackToLatestProcess();
								}
							}
						});
					} else {
						this._back();
					}
				} else {
					this._back();
				}
			} else {
				this._back();
			}
		},

		/**
		 * Method called to Navigate back to List Page
		 * Developed by: Bharat Gupta on 26.02.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private 
		 */
		_back: function () {
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this._clearOutwardValueState();
			} else {
				this._clearInwardValueState();
			}
			this._editInvoiceTab(true);
		},

		/**
		 * Method called to Rollback data to Latest Process Record
		 * Developed by: Bharat Gupta on 26.02.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private 
		 */
		_rollbackToLatestProcess: function () {
			// debugger; //eslint-disable-line
			if (this.byId("tabOutwardEdit").getVisible()) {
				var vEditTab = "tabOutwardEdit",
					vDataType = "outward";
			} else if (this.byId("tabInwardEdit").getVisible()) {
				vEditTab = "tabInwardEdit";
				vDataType = "inward";
			}
			var that = this,
				oData = this.byId(vEditTab).getModel("InvoiceItemModel").getData(),
				oPayload = {
					"req": {
						"id": oData.id,
						"dataType": vDataType,
						"gstnData": false
					}
				};
			if (this.byId("sbInvType").getSelectedKey() === "gstn") {
				oPayload.req.gstnData = true;
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/revertingInvoice.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._back();
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * called to display / hide tables and buttons
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private 
		 * @param {boolean} flag Flag value
		 * @param {Object} errList Error list
		 */
		_editInvoiceTab: function (flag, errList) {
			var oPropModel = this.getView().getModel("Properties"),
				vFlag = false;

			this.byId("dpHeader").setVisible(flag);
			this.byId("sbInvType").setEnabled(flag);
			this.byId("sbManage").setEnabled(flag);
			this.byId("bManageBack").setVisible(!flag);
			this.byId("iInvoiceToolbar").setVisible(flag);

			if (this.byId("sbManage").getSelectedKey() === "process") {
				this.byId("tabInvProcess").setVisible(flag);
				this.byId("tabInvProcess").setSelectedIndex(-1);
				if (errList) {
					vFlag = true;
				}
			} else {
				vFlag = true;
				this.byId("tabInvoiceError").setVisible(flag);
				this.byId("hbError").setVisible(flag);
			}
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this.byId("tabOutwardEdit").setVisible(!flag);
			} else {
				this.byId("tabInwardEdit").setVisible(!flag);
			}
			// this.byId("bPrevError").setVisible(vFlag);
			// this.byId("bNextError").setVisible(vFlag);
			this.byId("bMessage").setVisible(vFlag);
			this.byId("bMessage1").setVisible(vFlag);

			if (oPropModel.getData().saveAction) {
				oPropModel.getData().saveAction = false;
				oPropModel.refresh(true);
				if (this.byId("sbInvType").getSelectedKey() === "asp") {
					var oSearchInfo = this._getSearchInfo(this._getDataType(), 1);
					if (this.byId("dAdapt")) {
						this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);
					}
					this._getAspInvoices(oSearchInfo);
				}
			}
		},

		/**
		 * Clearing value state and text for all fields in edit invoice screen for Outward
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearOutwardValueState: function () {
			var oModel = this.byId("tabOutwardEdit").getModel("InvoiceItemModel"),
				oData = oModel.getData();
			this._setValueStateNone(oData);
			for (var i = 0; i < oData.lineItems.length; i++) {
				this._setValueStateNone(oData.lineItems[i]);
			}
			oModel.refresh(true);
		},

		/**
		 * Clearing value state and text for all fields in edit invoice screen for Inward
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearInwardValueState: function () {
			var oModel = this.byId("tabInwardEdit").getModel("InvoiceItemModel"),
				oData = oModel.getData();
			this._setValueStateNone(oData);
			for (var i = 0; i < oData.lineItems.length; i++) {
				this._setValueStateNone(oData.lineItems[i]);
			}
			oModel.refresh(true);
		},

		/**
		 * Setting Invoice management edit screen field state value as None
		 * Developed by: Bharat Gupta
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
		 * Outward other header details
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Payload Header object
		 * @param {Object} data response data objectb
		 */
		_outwardHeaders: function (header, data) {
			var arrFields = [
				"profitCentre1", "division", "location", "salesOrg", "distChannel", "glCodeIgst", "glCodeCgst", "glCodeSgst", "glCodeAdvCess",
				"glCodeSpCess", "glCodeStateCess", "orgDocType", "origCgstin", "billToState", "shipToState", "shippingBillNo", "shippingBillDate",
				"tcsFlag", "ecomGSTIN", "accVoucherNo", "accVoucherDate"
			];
			for (var i = 0; i < arrFields.length; i++) {
				header[arrFields[i]] = data[arrFields[i]] || null;
			}
		},

		/**
		 * Inward Data Security & GL Code values
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * Developed by: Bharat Gupta on 19.03.2020
		 * @private
		 * @param {Object} header Payload Header Object
		 * @param {Object} data Select Object
		 */
		_inwardDataSecurity: function (header, data) {
			header.division = data.division || null; // Divsion
			header.purchaseOrganization = data.purchaseOrganization || null; // Purchase Organisation
			header.userAccess1 = data.profitCentre3 || null; // User Access 1
			header.userAccess2 = data.profitCentre4 || null; // User Access 2
			header.userAccess3 = data.profitCentre5 || null; // User Access 3
			header.userAccess4 = data.profitCentre6 || null; // User Access 4
			header.userAccess5 = data.profitCentre7 || null; // User Access 5
			header.userAccess6 = data.profitCentre8 || null; // User Access 6
		},

		/**
		 * Inward other header details
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} header Payload Header object
		 * @param {Object} data response data objectb
		 */
		_inwardOtherHeaders: function (header, data) {
			header.purchaseOrganization = data.purchaseOrganization || null;
			header.custGstin = data.custGstin || null;
			header.supplierGstin = data.supplierGstin || null;
			header.originalSupplierGstin = data.originalSupplierGstin || null;
			header.billOfEntryNo = data.billOfEntryNo || null;
			header.billOfEntryDate = data.billOfEntryDate || null;
			header.itcEntitlement = data.itcEntitlement || null;
			header.postingDate = data.postingDate || null;
		},

		/**
		 * Method to create Payload to save data
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @private
		 * @param {Object} data Select Object
		 * @return {Object} Payload
		 */
		_headerJson: function (data) {
			// debugger; //eslint-disable-line
			var dataType = this.getView().getModel("DataType").getData().dataType,
				oHeader = {},
				arrFields = ["id", "userId", "srcFileName",
					"returnPeriod", "docType", "docNo", "docDate", "suppGstin", "custGstin", "crDrPreGst", "custOrSupType", "diffPercent",
					"custOrSupName", "custOrSupCode", "custOrSupAddr1", "custOrSupAddr2", "custOrSupAddr3", "custOrSupAddr4", "pos",
					"stateApplyingCess", "portCode", "sec7OfIgstFlag", "reverseCharge", "claimRefundFlag", "autoPopToRefundFlag", "eWayBillNo",
					"eWayBillDate", "extractedBatchId", "extractedDate", "fileId"
				];
			for (var i = 0; i < arrFields.length; i++) {
				oHeader[arrFields[i]] = data[arrFields[i]] || null;
			}
			oHeader.dataOriginTypeCode = data.dataOriginTypeCode.substr(0, 1) + "I";

			if (dataType === "outward") { //(this.byId("slInvDataType").getSelectedKey() === "outward") {
				this._outwardHeaders(oHeader, data);
			} else {
				this._inwardDataSecurity(oHeader, data); // Inward Data Security Filter value
				this._inwardOtherHeaders(oHeader, data);
			}
			oHeader.lineItems = [];
			for (var j = 0; j < data.lineItems.length; j++) {
				oHeader.lineItems.push(this._itemJson(data.lineItems[j], dataType));
			}
			return oHeader;
		},

		/**
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} items Payload Item object
		 * @param {Object} data response data objectb
		 */
		_outwardItems: function (items, data) {
			items.plantCode = data.plantCode || null;
			items.profitCentre3 = data.profitCentre3 || null;
			items.profitCentre4 = data.profitCentre4 || null;
			items.profitCentre5 = data.profitCentre5 || null;
			items.profitCentre6 = data.profitCentre6 || null;
			items.profitCentre7 = data.profitCentre7 || null;
			items.profitCentre8 = data.profitCentre8 || null;
			items.glCodeTaxableVal = data.glCodeTaxableVal || null;
			items.preceedingInvNo = data.preceedingInvNo || null;
			items.preceedingInvDate = data.preceedingInvDate || null;
			items.fob = data.fob || 0;
			items.exportDuty = data.exportDuty || 0;
			items.tcsAmt = data.tcsAmt || 0;
			items.itcFlag = data.itcFlag || null;
		},

		/**
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} items Payload Item object
		 * @param {Object} data response data objectb
		 */
		_inwardItems: function (items, data) {
			var aNumField = ["cifValue", "customDuty", "availableIgst", "availableCgst", "availableSgst", "availableCess", "contractValue"],
				arrField = [
					"profitCentre", "plantCode", "location", "glCodeTaxableVal", "glCodeIgst", "glCodeCgst", "glCodeSgst", "glCodeAdvCess",
					"glCodeSpCess", "glCodeStateCess", "originalDocNo", "originalDocDate", "cifValue", "customDuty", "eligibilityIndicator",
					"commonSupplyIndicator", "availableIgst", "availableCgst", "availableSgst", "availableCess", "itcReversalIdentifier",
					"purchaseVoucherNum", "purchaseVoucherDate", "paymentVoucherNumber", "paymentDate", "contractNumber", "contractDate",
					"contractValue"
				];
			for (var i = 0; i < arrField.length; i++) {
				items[arrField[i]] = data[arrField[i]] || (aNumField.includes(arrField[i]) ? 0 : null);
			}
		},

		/**
		 * Common Field for Outward/Inward Save Payload
		 * Developed by: Bharat Gupta
		 * Modified by: Bharat Gupta on 19.03.2020
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data line item data object
		 * @param {string} dataType Datatype (Outward/Inward)
		 * @return {Object} Line item object
		 */
		_itemJson: function (data, dataType) {
			var oItems = {},
				arrField = [
					"itemNo", "supplyType", "hsnsacCode", "productCode", "itemDesc", "itemType", "itemUqc", "itemQty", "taxableVal",
					"igstRt", "igstAmt", "cgstRt", "cgstAmt", "sgstRt", "sgstAmt", "cessRtAdvalorem", "cessAmtAdvalorem", "cessRtSpecific",
					"cessAmtSpecfic", "stateCessRt", "stateCessAmt", "otherValues", "lineItemAmt", "adjustmentRefNo", "adjustmentRefDate",
					"adjustedTaxableValue", "adjustedIgstAmt", "adjustedCgstAmt", "adjustedSgstAmt", "adjustedCessAmtAdvalorem",
					"adjustedCessAmtSpecific", "adjustedStateCessAmt", "crDrReason",
					"udf1", "udf2", "udf3", "udf4", "udf5", "udf6", "udf7", "udf8", "udf9", "udf10", "udf11", "udf12", "udf13", "udf14", "udf15"
				];
			for (var i = 0; i < arrField.length; i++) {
				oItems[arrField[i]] = data[arrField[i]] || (arrField[i].includes("Amt") || arrField[i].includes("Rt") ? 0 : null);
			}
			if (dataType === "outward") { //(this.byId("slInvDataType").getSelectedKey() === "outward") {
				this._outwardItems(oItems, data);
			} else {
				this._inwardItems(oItems, data);
			}
			return oItems;
		},

		/**
		 * Called when user pressed on SaveToGstn button to intiate saving of Outward Invoices data in GSTN server
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onSaveOutwardInvoice: function (oEvent) {
			// debugger; //eslint-disable-line
			var oData = this.byId("tabOutwardEdit").getModel("InvoiceItemModel").getData(),
				flag = false;

			for (var i = 0; i < oData.lineItems.length; i++) {
				if (!oData.lineItems[i].supplyType || !oData.lineItems[i].itemNo) {
					flag = true;
					break;
				}
			}
			if (!oData.suppGstin || !oData.returnPeriod || !oData.docType || !oData.docNo || !oData.docDate || flag) {
				sap.m.MessageBox.error("Mandatory Field is missing", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if ($.sap.myVar === "SV") {
				var url; // = "/aspsapapi/saveInwardSvErrDoc.do";
				sap.m.MessageToast.show("Structured Error Save Implementation is in progress...");
				return;
			} else {
				url = "/aspsapapi/saveDocUI.do";
			}
			var oPayload = {
				"req": []
			};
			oPayload.req.push(this._headerJson(oData));
			this._saveInvoice(oPayload, url);
		},

		/**
		 * Called when user pressed on SaveToGstn button to intiate saving of Inward Invoices data in GSTN server
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 */
		onSaveInwardInvoice: function () {
			// debugger; //eslint-disable-line
			var oData = this.byId("tabInwardEdit").getModel("InvoiceItemModel").getData(),
				flag = false;

			for (var i = 0; i < oData.lineItems.length; i++) {
				if (!oData.lineItems[i].supplyType) {
					flag = true;
					break;
				}
			}
			if (!oData.custGstin || !oData.returnPeriod || !oData.docType || !oData.docNo || !oData.docDate || !oData.pos || flag) {
				sap.m.MessageBox.error("Mandatory Field is missing", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if ($.sap.myVar === "SV") {
				var url = "/aspsapapi/saveInwardSvErrDoc.do";
			} else {
				url = "/aspsapapi/saveInwardDocUI.do";
			}
			var oPayload = {
				"req": []
			};
			oPayload.req.push(this._headerJson(oData));
			this._saveInvoice(oPayload, url);
		},

		/**
		 * Ajax Call to save payload in database
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} oPayload payload object
		 * @param {string} url URL to call api
		 */
		_saveInvoice: function (oPayload, url) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						that._aspSaveDocResponse(data);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that._serverMessage(jqXHR.status);
				});
			});
		},

		/**
		 * Called to validate Response after Save Changes initiated to display appropriate message and bind error message list to Message Popover
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data response data
		 */
		_aspSaveDocResponse: function (data) {
			// debugger; //eslint-disable-line
			var that = this,
				vIdxErrInfo = -1,
				oPropModel = this.getView().getModel("Properties");
			oPropModel.getData().saveAction = true;
			oPropModel.refresh(true);

			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this._clearOutwardValueState();
				var oModel = this.byId("tabOutwardEdit").getModel("InvoiceItemModel");
			} else {
				this._clearInwardValueState();
				oModel = this.byId("tabInwardEdit").getModel("InvoiceItemModel");
			}
			var oData = oModel.getData();

			oData.id = data.resp[0].id;
			if (data.resp[0].errors) {
				vIdxErrInfo = data.resp[0].errors.findIndex(function (item, i) {
					return item.errorType === "ERR";
				});
			}
			if (vIdxErrInfo !== -1) { //(!data.resp[0].errors || data.resp[0].errors.length === 0) {
				oData.errorList = data.resp[0].errors;
				this._aspErrorList(oData, data.resp[0].errors);
				sap.m.MessageBox.error(this.getResourceBundle().getText("msgSavedWithError"), {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				if (data.resp[0].errors) {
					vIdxErrInfo = data.resp[0].errors.findIndex(function (item, i) {
						return item.errorType === "INFO";
					});
				}
				if (vIdxErrInfo !== -1) {
					oData.errorList = data.resp[0].errors;
					this._aspErrorList(oData, data.resp[0].errors);
					sap.m.MessageBox.information(this.getResourceBundle().getText("msgSavedWithInfo"), {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					if (data.hdr.status === "S") {
						sap.m.MessageBox.success(this.getResourceBundle().getText("msgSavedSuccess"), {
							styleClass: "sapUiSizeCompact",
							onClose: function () {
								that._editInvoiceTab(true);
							}
						});
					}
				}
			}
			oModel.refresh(true);
		},

		/**
		 * Called to get Error list from Response object and bind it to Message Popover
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 * @param {Object} data table data
		 * @param {Object} err error list
		 */
		_aspErrorList: function (data, err) {
			// debugger; //eslint-disable-line
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
						subtitle: err[i].errorDesc,
						errorType: err[i].errorType
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
			if (this.byId("slInvDataType").getSelectedKey() === "outward") {
				this.byId("bMessage").setText(aErrorMsg.length);
				this.byId("bMessage").setVisible(true);
			} else {
				this.byId("bMessage1").setText(aErrorMsg.length);
				this.byId("bMessage").setVisible(true);
			}
			oMessagePopover.setModel(new JSONModel(aErrorMsg), "Msg");
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
			debugger; //eslint-disable-line
			var oModel = oMessagePopover.getModel("Msg"); //eslint-disable-line
			var arrCell = this.byId("tabOutwardEdit").getRows()[0].getCells();
			for (var i = 2; i < arrCell.length; i++) {
				if (arrCell[i].getName() === "cgstAmt") {
					arrCell[i].focus();
				}
			}
		},

		/**
		 * Called to clear filter and table value & restored it to default values
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		onPressClear: function () {
			// debugger; //eslint-disable-line
			this._clearAdapFilter();
			// var oDataEntity = this.getOwnerComponent().getModel("EntityModel").getData(),
			var oPropModel = this.getView().getModel("Properties");

			this.getOwnerComponent().setModel(null, "FilterInvoice");
			oPropModel.getData().returnPeriod = false;
			oPropModel.refresh(true);

			if (this.byId("sbInvType").getSelectedKey() === "asp") {
				this.byId("slInvDataType").setSelectedKey("outward");
				this.byId("slInvCriteria").setSelectedKey("RECEIVED_DATE_SEARCH");
				// this.byId("slInvEntity").setSelectedKey(oDataEntity[0].entityId);
				this.byId("slInvGstin").setSelectedKeys([]);
				this._bindDefaultDate();
				this._clearFilterValue();
			} else {
				this.byId("slGstnDataType").setSelectedKey("outward");
				// this.byId("slGstnEntity").setSelectedKey(oDataEntity[0].entityId);
				this.byId("slGstnGstin").setSelectedKeys([]);
				this._bindGstnDefaultData();
				this._clearGstnFilterValue();
			}
			var oSearchInfo = this._getSearchInfo(this._getDataType(), 1);
			if (this.byId("dAdapt") && this.byId("sbInvType").getSelectedKey() === "asp") {
				this._getOtherFiltersASP(oSearchInfo.req.dataSecAttrs);

			} else if (this.byId("sbInvType").getSelectedKey() === "gstn") {
				if (this.byId("dGstnAdapt")) {
					this._getOtherFiltersGSTN(oSearchInfo.req.dataSecAttrs);
				}
			}
			this._getAspInvoices(oSearchInfo);
		},

		/**
		 * Called to clear Adapt filter value
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearAdapFilter: function () {
			if (this.byId("dAdapt") && this.byId("sbInvType").getSelectedKey() === "asp") {
				this.byId("slProfitCtr").setSelectedKeys([]);
				this.byId("slPlant").setSelectedKeys([]);
				this.byId("slDivision").setSelectedKeys([]);
				this.byId("slLocation").setSelectedKeys([]);
				this.byId("slSalesOrg").setSelectedKeys([]);
				this.byId("slPurcOrg").setSelectedKeys([]);
				this.byId("slDistrChannel").setSelectedKeys([]);
				this.byId("slUserAccess1").setSelectedKeys([]);
				this.byId("slUserAccess2").setSelectedKeys([]);
				this.byId("slUserAccess3").setSelectedKeys([]);
				this.byId("slUserAccess4").setSelectedKeys([]);
				this.byId("slUserAccess5").setSelectedKeys([]);
				this.byId("slUserAccess6").setSelectedKeys([]);
			}
			if (this.byId("dGstnAdapt") && this.byId("sbInvType").getSelectedKey() === "gstn") {
				this.byId("slGstnProfitCtr").setSelectedKeys([]);
				this.byId("slGstnPlant").setSelectedKeys([]);
				this.byId("slGstnDivision").setSelectedKeys([]);
				this.byId("slGstnLocation").setSelectedKeys([]);
				this.byId("slGstnSalesOrg").setSelectedKeys([]);
				this.byId("slGstnPurcOrg").setSelectedKeys([]);
				this.byId("slGstnDistrChannel").setSelectedKeys([]);
				this.byId("slGstnUserAccess1").setSelectedKeys([]);
				this.byId("slGstnUserAccess2").setSelectedKeys([]);
				this.byId("slGstnUserAccess3").setSelectedKeys([]);
				this.byId("slGstnUserAccess4").setSelectedKeys([]);
				this.byId("slGstnUserAccess5").setSelectedKeys([]);
				this.byId("slGstnUserAccess6").setSelectedKeys([]);
			}
		},

		/**
		 * Clear Filter Value of other non-mandatory ASP fields value
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearFilterValue: function () {
			$.sap.returnType = "GSTR1";
			this.byId("slDocType").setSelectedKeys([]);
			this.byId("idInvDocNo").setValue(null);
			this.byId("slReturnType").setSelectedKeys([]);
			this.byId("slTabNumber").setSelectedKeys([]);
			this.byId("slGstnStatus").setSelectedKeys([]);
			this.byId("slCpGstin").setSelectedKeys([]);
			this.byId("slCpAction").setSelectedKeys([]);
		},

		/**
		 * Clear Filter Value of other non-mandatory GSTN fields value
		 * @memberOf com.ey.digigst.view.InvoiceManage
		 * @private
		 */
		_clearGstnFilterValue: function () {
			this.byId("slGstnReturnType").setSelectedKeys([]);
			this.byId("slGstnTabNumber").setSelectedKeys([]);
			this.byId("slGstnDocType").setSelectedKeys([]);
			this.byId("idGstnDocNo").setValue(null);
			this.byId("idGstnRefId").setValue(null);
		}
	});
});