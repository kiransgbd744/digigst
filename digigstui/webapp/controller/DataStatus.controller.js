sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage"
], function (BaseController, JSONModel, Storage) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.DataStatus", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.DataStatus
		 */
		onInit: function () {
			this.bindDefaultData();
			this.getOwnerComponent().getRouter().getRoute("DataStatus").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.DataStatus
		 */
		onAfterRendering: function () {
			this._bindDefaultDate();
			this._bindDefaultUploadDate();

			var oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataType;
			this.byId("slFileDataType").setModel(new JSONModel(oDataType.gstr13b), "DataType");

			var oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileType,
				fileType = this._removeComprehensiveRaw(oFileType.gstr1);
			this.byId("slStatsFileType").setModel(new JSONModel(fileType), "FileType");
			// this.byId("slStatsFileType").setModel(new JSONModel(oFileType.gstr1), "FileType");
		},

		/**
		 * Called to bind Default date value in Data Status - API screen
		 * @memberOf com.ey.digigst.view.DataStatus
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

		/**
		 * Called to bind Default date value for Upload Date
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		_bindDefaultUploadDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("drsUpload").setDateValue(vDate);
			this.byId("drsUpload").setSecondDateValue(new Date());
			this.byId("drsUpload").setMaxDate(new Date());
		},

		/**
		 * Called to bind defauld data to Data Status screens
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		bindDefaultData: function () {
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
			this.byId("drsUpload").addEventDelegate({
				onAfterRendering: function () {
					that.byId("drsUpload").$().find("input").attr("readonly", true);
				}
			});
			var oJsonData = {
				"withSAPTotal": "N",
				"apiFilter": true,
				"returnPeriod": false
			};
			this.getView().setModel(new JSONModel(oJsonData), "DataStatus");
			var oProp = {
				"outward": true,
				"rawSummary": false,
				"verSummary": false
			};
			this.getView().setModel(new JSONModel(oProp), "Properties");

			// var oStorage = new Storage(Storage.Type.session, "digiGst");
			// var oEntityData = oStorage.get("entity");
			// if (!oEntityData) {
			// 	this.getRouter().navTo("Home");
			// } else {
			// 	var oModel = this.getOwnerComponent().getModel("EntityModel");
			// 	oModel.setData(oEntityData);
			// 	oModel.refresh(true);
			// 	this._getDataSecurity(oEntityData[0].entityId);
			// }
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		_onRouteMatched: function (oEvent) {
			// debugger; //eslint-disable-line
			this.byId("dpDataStatus").setVisible(true);
			this.byId("dpSummayAPI").setVisible(false);

			if (this.byId("sbStatus").getSelectedKey() === "api") {
				this._getDataStatusAPI();

			} else if (this.byId("sbStatus").getSelectedKey() === "upload") {
				this._getFileStauts();

			} else {
				this.getSftpStatus();
			}
		},

		_removeComprehensiveRaw: function (aFileType) {
			// debugger; //eslint-disable-line
			var types = aFileType;
			for (var i = 0; i < aFileType.length; i++) {
				if (aFileType[i].key === "COMPREHENSIVE_RAW") {
					types = aFileType.slice(i + 1);
					types[0].text = "Raw File";
					break;
				}
			}
			return types;
		},

		/**
		 * Called wher user change segment button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeSegment: function (oEvent) {
			debugger;
			var vSegmentBtnId = oEvent.getSource().getId();
			var vFlag = false;
			this.byId("bAdaptFilter").setVisible(vFlag);
			if (vSegmentBtnId.includes("sbStatus")) {
				var oModel = this.getView().getModel("DataStatus");
				var oSegmentBtn = oEvent.getSource().getSelectedKey();
				if (oSegmentBtn === "api") {
					var vSb = "A";
					oModel.getData().apiFilter = true;
					this.byId("bAdaptFilter").setVisible(!vFlag);

				} else if (oSegmentBtn === "upload") {
					if (this.byId("sbUpload").getSelectedKey().includes("upload")) {
						vFlag = true;
						vSb = "U";
					} else {
						vSb = "F";
					}
					oModel.getData().apiFilter = false;
				} else {
					vSb = "S";
					oModel.getData().apiFilter = false;
				}
				oModel.refresh(true);
			} else if (vSegmentBtnId.includes("sbUpload")) {
				vSb = "F";
				if (oEvent.getSource().getSelectedKey() === "upload") {
					vFlag = true;
					vSb = "U";
				}
			}
			this._uploadVisible(vFlag, vSb);
		},

		/**
		 * Called to set visibility of UI component based on selected segment button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {boolean} vFlag flag value
		 * @param {string} vSb Selected segment button identifier
		 */
		_uploadVisible: function (vFlag, vSb) {
			// debugger; //eslint-disable-line
			this.byId("fbStatus").setVisible(!vFlag);
			this.byId("fbStatusHbox").setVisible(!vFlag);
			this.byId("rbReturnType").setVisible(vFlag);
			this.byId("iRbgStatus").setVisible(vFlag);
			this.byId("fileUploader").setVisible(vFlag);
			this.byId("btnUpload").setVisible(vFlag);

			if (vSb === "A") {
				this.byId("tabData").setVisible(true);
			} else {
				this.byId("tabData").setVisible(false);
			}
			if (vSb === "U" || vSb === "F") {
				this.byId("sbUpload").setVisible(true);
			} else {
				this.byId("sbUpload").setVisible(false);
			}
			if (vSb === "F" || vSb === "S") {
				if (vSb === "F") {
					var key = this.byId("slStatsFileType").getSelectedKey();
					if (key === "RAW" || key === "COMPREHENSIVE_RAW") {
						this.byId("tabFileStatus").setVisible(!vFlag);
					} else {
						this.byId("tabVerFileStatus").setVisible(!vFlag);
						this.byId("tabRecFileStatus").setVisible(!vFlag);
					}
					this.byId("tabSftpStatus").setVisible(false);
				} else {
					this.byId("tabFileStatus").setVisible(false);
					this.byId("tabVerFileStatus").setVisible(false);
					this.byId("tabRecFileStatus").setVisible(false);
					this.byId("tabSftpStatus").setVisible(true);
				}
			} else {
				this.byId("tabFileStatus").setVisible(false);
				this.byId("tabVerFileStatus").setVisible(false);
				this.byId("tabRecFileStatus").setVisible(false);
				this.byId("tabSftpStatus").setVisible(false);
			}
			if (vSb === "F") {
				var oFileModel = this.getView().getModel("FileStatus");
				if (!oFileModel) {
					this._getFileStauts();
				}
			}
		},

		/**
		 * Called when user change data type
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectChange: function (oEvent) {
			// debugger; //eslint-disable-line
			var vKey = oEvent.getSource().getSelectedKey();
			if (oEvent.getSource().getId().includes("slStatsDataType")) {
				var oModel = this.getView().getModel("Properties");
				var oData = oModel.getData();
				oData.outward = (vKey === "outward" ? true : false);
				oModel.refresh();

			} else if (oEvent.getSource().getId().includes("slFileDataType")) {
				var oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileType,
					oFileModel = this.byId("slStatsFileType").getModel("FileType");

				if (vKey === "outward" || vKey === "gstr1") {
					var fileType = this._removeComprehensiveRaw(oFileType[vKey]);
					oFileModel.setData(fileType);
				} else {
					oFileModel.setData(oFileType[vKey]);
				}
				oFileModel.refresh(true);
			}
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
			}
			var fromDate = oEvent.getSource().getDateValue(),
				toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectReturnGroup: function (oEvent) {
			// debugger; //eslint-disable-line
			var vId = oEvent.getSource().getSelectedButton().getId(),
				flag = true;

			if (vId.includes("bGstr13b")) {
				flag = false;
			}
			this.byId("rgDataType").setVisible(!flag);
			this.byId("rgNewReturnDataType").setVisible(flag);
			this.onSelectRadioGroup();
		},

		/**
		 * Called when user select any radio button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onSelectRadioGroup: function () {
			// debugger; //eslint-disable-line
			this.byId("rbgOutward").setVisible(false);
			this.byId("rbgInward").setVisible(false);
			this.byId("rbgOthers").setVisible(false);
			this.byId("rbgGstr1").setVisible(false);
			this.byId("rbgOld3B").setVisible(false);

			var vIdMain = this.byId("rgReturnType").getSelectedButton().getId();

			if (vIdMain.includes("bGstr13b")) {
				var vId = this.byId("rgDataType").getSelectedButton().getId();

			} else if (vIdMain.includes("bNewReturn")) {
				vId = this.byId("rgNewReturnDataType").getSelectedButton().getId();
			}

			if (vId.includes("bOutward")) {
				this.byId("rbgOutward").setVisible(true);

			} else if (vId.includes("bInward")) {
				this.byId("rbgInward").setVisible(true);

			} else if (vId.includes("bOthers")) {
				this.byId("rbgOthers").setVisible(true);

			} else if (vId.includes("bGstr3b")) {
				this.byId("rbgOld3B").setVisible(true);

			} else if (vId.includes("bGstr1")) {
				this.byId("rbgGstr1").setVisible(true);
			}
		},

		/**
		 * Called when user change criteria to bind respective period format
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeCriteria: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getSelectedKey() === "RETURN_DATE_SEARCH") {
				var flag = true;
			} else {
				flag = false;
			}
			var oModel = this.getView().getModel("DataStatus");
			oModel.getData().returnPeriod = flag;
			oModel.refresh(true);
		},

		/**
		 * Called when user change Entity in Entity dropdown to bind Data Security attribute in screen w.r.t. selected Entity
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onEntityChange: function (oEvent) {
			this._getDataSecurity(oEvent.getSource().getSelectedKey());
		},

		/**
		 * Called for payload structure to get data from database
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @return {Object} searchInfo Search Criteria object
		 */
		_getApiSearchInfo: function () {
			// var aEntity = [];
			// aEntity.push(this.byId("slStatsEntity").getSelectedKey());
			var searchInfo = {
				"req": {
					"dataType": this.byId("slStatsDataType").getSelectedKey(),
					"entityId": [$.sap.entityID], //aEntity,
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"dataSecAttrs": {
						"GSTIN": this.byId("slStatsGSTIN").getSelectedKeys()
					}
				}
			};

			var vCriteria = this.byId("slStatsCriteria").getSelectedKey();
			if (vCriteria === "RECEIVED_DATE_SEARCH") {
				searchInfo.req.dataRecvFrom = this.byId("iFromDate").getValue();
				searchInfo.req.dataRecvTo = this.byId("iToDate").getValue();

			} else {
				searchInfo.req.taxPeriodFrom = this.byId("iFromPeriod").getValue();
				searchInfo.req.taxPeriodTo = this.byId("iToPeriod").getValue();
			}
			return searchInfo;
		},

		/**
		 * Called to add additional filter (Data Security) value in search payload
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} search SearchInfo object
		 */
		_getOtherFilters: function (search) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items,
				// this.getView().getModel("DataSecurity").getData().items,
				vDataType = this.byId("slStatsDataType").getSelectedKey();

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
		 * Called when user click on search button to get data from database
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		onSearch: function () {
			// debugger; //eslint-disable-line
			if (this.byId("sbStatus").getSelectedKey().includes("api")) {
				this._getDataStatusAPI();

			} else if (this.byId("sbStatus").getSelectedKey().includes("upload")) {
				this._getFileStauts();
			} else {
				this.getSftpStatus();
			}
		},

		/**
		 * When user press on adapt filter button then this function called to open Data Security filter dialog for addtional filter
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		onPressAdaptFilter: function () {
			// debugger; //eslint-disable-line
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.invManage.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			this._oAdpatFilter.open();
		},

		/**
		 * Called to close open adapt filter dialog
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressFilterClose: function (oEvent) {
			// debugger; //eslint-disable-line
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				if (this.byId("sbStatus").getSelectedKey().includes("api")) {
					this._getDataStatusAPI();

				} else if (this.byId("sbStatus").getSelectedKey().includes("upload")) {
					this._getFileStauts();
				} else {
					this.getSftpStatus();
				}
			}
		},

		/**
		 * Called to get API data from Database
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		_getDataStatusAPI: function () {
			// debugger; //eslint-disable-line
			var that = this;
			if (!$.sap.entityID) { //(!this.byId("slStatsEntity").getSelectedKey() || this.byId("slStatsEntity").getSelectedKey() === "") {
				this._infoMessage(this.getResourceBundle().getText("msgNoData"));
				return;
			}
			var searchInfo = this._getApiSearchInfo();
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs);
			}
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.byId("tabData").getModel("Data");
			if (oModel) {
				oModel.setData(null);
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAnx1DataStatus.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.resp.length > 0) {
					var oJsonData = that._bindStatusTableFinal(data.resp);
					that.byId("tabData").setModel(new JSONModel(oJsonData), "Data");

					var oVisiModel = that.getView().getModel("DataStatus");
					oVisiModel.getData().withSAPTotal = data.hdr.withSAPTotal;
					oVisiModel.refresh(true);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Binding Final Data for Table with Consolidated Data
		 * @private
		 * @param {Object} data Response object to convert in Summary JSON Format
		 * @return {Object} oJsonData
		 */
		_bindStatusTableFinal: function (data) {
			var oJsonData = [];
			var oConsData = {
				"receivedDate": "",
				"sapTotal": 0,
				"diff": 0,
				"totalRecords": 0,
				"processedActive": 0,
				"processedInactive": 0,
				"errorActive": 0,
				"errorInactive": 0,
				"infoActive": 0,
				"infoInactive": 0
			};
			for (var i = 0; i < data.length; i++) {
				oJsonData.push(data[i]);
				oConsData.sapTotal = oConsData.sapTotal + (data[i].sapTotal === "" ? 0 : parseInt(data[i].sapTotal, 10));
				oConsData.diff = oConsData.diff + (data[i].diff === "" ? 0 : parseInt(data[i].diff, 10));
				oConsData.totalRecords = oConsData.totalRecords + (data[i].totalRecords === "" ? 0 : parseInt(data[i].totalRecords, 10));
				oConsData.processedActive = oConsData.processedActive + (data[i].processedActive === "" ? 0 : parseInt(data[i].processedActive, 10));
				oConsData.processedInactive = oConsData.processedInactive +
					(data[i].processedInactive === "" ? 0 : parseInt(data[i].processedInactive, 10));
				oConsData.errorActive = oConsData.errorActive + (data[i].errorActive === "" ? 0 : parseInt(data[i].errorActive, 10));
				oConsData.errorInactive = oConsData.errorInactive + (data[i].errorInactive === "" ? 0 : parseInt(data[i].errorInactive, 10));
				oConsData.infoActive = oConsData.infoActive + (data[i].infoActive === "" ? 0 : parseInt(data[i].infoActive, 10));
				oConsData.infoInactive = oConsData.infoInactive + (data[i].infoInactive === "" ? 0 : parseInt(data[i].infoInactive, 10));
			}
			oJsonData.unshift(oConsData);
			return oJsonData;
		},

		// /**
		//  * Called when check/uncheck row selector in api table
		//  * @memberOf com.ey.digigst.view.DataStatus
		//  * @private
		//  * @param {Object} oEvent Eventing object
		//  */
		// onRowSelectionChange: function (oEvent) {
		// 	// debugger; //eslint-disable-lines
		// 	if (oEvent.getSource().getSelectedIndex() === 0) {
		// 		oEvent.getSource().removeSelectionInterval(0, 0);
		// 		return;
		// 	}
		// 	var aIndices = this.byId("tabData").getSelectedIndices();
		// 	if (aIndices.length > 0) {
		// 		this.byId("bStatsSumm").setEnabled(true);
		// 		this.byId("bStatsDel").setEnabled(true);
		// 	} else {
		// 		this.byId("bStatsSumm").setEnabled(false);
		// 		this.byId("bStatsDel").setEnabled(false);
		// 	}
		// },

		/**
		 * Called when user click on Hyper link in binded table value
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent	Eventing object
		 * @param {string} navType	Navigation From (api/file status)
		 * @param {string} type		Record Type (Processed, Error, Info)
		 * @param {number} count	Record Count
		 * @param {string} errType	Record Error Type
		 */
		onPressStatsLink: function (oEvent, navType, type, count, errType) {
			// debugger; //eslint-disable-line
			if (count === 0) {
				sap.m.MessageBox.error("No Data to display", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (navType === "api") {
				var oFilterData = this._apiFilterData(oEvent, type, errType);
			} else {
				oFilterData = this._fileStatusFilterData(oEvent, type, errType);
			}
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			// if (!msg) {
			this.getRouter().navTo("InvoiceManage");
			// } else {
			// 	sap.m.MessageBox.error("No Data to display", {
			// 		styleClass: "sapUiSizeCompact"
			// 	});
			// }
		},

		/**
		 * Called to bind filter value to get ERP Data for Invoice Management
		 * @private
		 * @param {Object} oEvent	Eventing object
		 * @param {string} type		Selected Link Type
		 * @param {string} errType	Record Error Type
		 * @return {Object} oFilterData
		 */
		_apiFilterData: function (oEvent, type) {
			// debugger; //eslint-disable-line
			// link = oEvent.getSource().getDependents()[0].getText(),
			var obj = oEvent.getSource().getBindingContext("Data").getObject(),
				oFilterData = {
					"dataOriginType": "A",
					"segment": "asp"
				};

			oFilterData.status = type;
			if (type === "E") {
				oFilterData.validation = "BV";
			}
			// if (link === "processed" && obj.processedActive) {
			// 	oFilterData.status = "P";

			// } else if (link === "error" && obj.errorActive > 0) {
			// 	oFilterData.status = "E";
			// 	oFilterData.validation = "BV"; // Modified by Bharat Gupta on 30.11.2019

			// } else if (link === "info" && obj.infoActive) {
			// 	oFilterData.status = "I";

			// } else {
			// 	return "No Data to display";
			// }
			if (this.byId("slStatsDataType").getSelectedKey() === "outward") { // Added by Ram Mahato on 19.11.2019
				oFilterData.dataType = "gstr1";
			} else {
				oFilterData.dataType = this.byId("slStatsDataType").getSelectedKey();
			} // End of Code by Ram Mahato

			oFilterData.criteria = this.byId("slStatsCriteria").getSelectedKey();
			// oFilterData.entity = this.byId("slStatsEntity").getSelectedKey();
			oFilterData.gstin = this.byId("slStatsGSTIN").getSelectedKeys();

			if (obj.receivedDate === "" && oFilterData.criteria !== "RETURN_DATE_SEARCH") { // Modified by Bharat Gupta on 30.11.2019
				oFilterData.fromDate = this.byId("iFromDate").getDateValue();
				oFilterData.toDate = this.byId("iToDate").getDateValue();

			} else if (oFilterData.criteria === "RETURN_DATE_SEARCH") {
				oFilterData.fromDate = this.byId("iFromPeriod").getDateValue();
				oFilterData.toDate = this.byId("iToPeriod").getDateValue();
				if (obj.receivedDate !== "") {
					oFilterData.receivedDate = obj.receivedDate;
				}
			} else {
				oFilterData.fromDate = oFilterData.toDate = new Date(obj.receivedDate);
			}
			if (this.byId("dAdapt")) {
				oFilterData.dataSecAttrs = [];
				this._getOtherFilters(oFilterData.dataSecAttrs);
			}
			// this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			return oFilterData;
		},

		/**
		 * Called to bind filter value to get File Data for Invoice Management
		 * @private
		 * @param {Object}	oEvent	Eventing object
		 * @param {string}	type	Selected Link Type
		 * @param {string}	errType	Record Error Type
		 * @return {Object} oFilterData
		 */
		_fileStatusFilterData: function (oEvent, type, errType) {
			// debugger; //eslint-disable-line
			// vLink = oEvent.getSource().getDependents()[0].getText(),
			var obj = oEvent.getSource().getBindingContext("FileStatus").getObject(),
				oFilterData = {
					"dataOriginType": "E",
					"segment": "asp"
				};

			oFilterData.status = type;
			if (errType) {
				oFilterData.validation = errType;
			}

			// if (vLink === "Processed" && (obj.processedActive > 0 || obj.processed > 0)) {
			// 	oFilterData.status = "P";

			// } else if (vLink === "Errors" && (obj.errorsActive > 0 || obj.error > 0)) {
			// 	oFilterData.status = "E";
			// 	oFilterData.validation = "BV"; // Modified by Bharat Gupta on 30.11.2019

			// } else if (vLink === "StructuredError" && obj.strucError > 0) { // Modified by Bharat Gupta on 30.11.2019
			// 	oFilterData.status = "E";
			// 	oFilterData.validation = "SV"; // Modified by Bharat Gupta on 30.11.2019

			// } else if (vLink === "Info" && (obj.infoActive > 0 || obj.information > 0)) {
			// 	oFilterData.status = "I";

			// } else {
			// 	return "No Data to display";
			// }
			// if (vLink === "Errors") { // Commented by Bharat Gupta on 30.11.2019
			// 	oFilterData.validation = "BV";
			// } else if (vLink === "StructuredError") {
			// 	oFilterData.validation = "SV";
			// }
			oFilterData.fileId = obj.id;
			oFilterData.dataType = obj.dataType.toLocaleLowerCase();
			oFilterData.fromDate = oFilterData.toDate = new Date(obj.uploadedOn);
			// this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			return oFilterData;
		},

		/**
		 * API summary structure format to bind data in api summary screen
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @return {Object} Api summary object
		 */
		_objApiSummary: function () {
			return {
				"date": "",
				"gstin": "",
				"authToken": "",
				"returnPeriod": "",
				"returnType": "",
				"returnSection": "",
				"count": 0,
				"taxableValue": 0,
				"totalTaxes": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0,
				"reviewStatus": "",
				"saveStatus": "",
				"flag": "",
				"items": []
			};
		},

		/**
		 * Called when user click on Recieved Date Link to see summary on that particular date
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressDetail: function (oEvent) {
			// debugger; //eslint-disable-line
			this.visiCons = false;
			var oObject = oEvent.getSource().getBindingContext("Data").getObject(),
				aDate = [];
			if (oObject.processedActive === 0) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgProcess"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (this.byId("slStatsCriteria").getSelectedKey() === "DOCUMENT_DATE_SEARCH") {
				aDate.push(oObject.documentDate);
			} else {
				aDate.push(oObject.receivedDate);
			}
			this._getDataSummary(aDate);
		},

		/**
		 * Called when user click on Summary button after selecting Recieved date(s) in Data Status api table
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		onPressSummary: function () {
			// debugger; //eslint-disable-line
			this.visiCons = true;
			var oBundle = this.getResourceBundle(),
				vCriteria = this.byId("slStatsCriteria").getSelectedKey(),
				aIndices = this.byId("tabData").getSelectedIndices(),
				oData = this.byId("tabData").getModel("Data").getData(),
				aDate = [];
			if (aIndices.length === 0) {
				sap.m.MessageToast.show(oBundle.getText("msgSelectOneRecord"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndices.length; i++) {
				if (oData[aIndices[i]].processedActive > 0) {
					if (vCriteria === "DOCUMENT_DATE_SEARCH") {
						aDate.push(oData[aIndices[i]].documentDate);
					} else {
						aDate.push(oData[aIndices[i]].receivedDate);
					}
				}
			}
			if (aDate.length === 0) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgProcess"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			this._getDataSummary(aDate);
		},

		/**
		 * Ajax call to display summary of particular recieved date(s)
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} aDate List of selected date(s)
		 */
		_getDataSummary: function (aDate) {
			// debugger; //eslint-disable-line
			var that = this;
			var searchInfo = this._getApiSearchInfo();
			searchInfo.req.dates = aDate;
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getDataStatusApiSummary.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpDataStatus").setVisible(false);
				that.byId("dpSummayAPI").setVisible(true);
				if (that.byId("slStatsDataType").getSelectedKey() === "outward") {
					that.byId("txtApiSumm").setText("API Outward Summary");
				} else {
					that.byId("txtApiSumm").setText("API Inward Summary");
				}
				if (data.hdr.status === "E") {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
					that.byId("tabStatusAPI").setModel(new JSONModel(), "Summary");
				} else {
					that._bindSummaryData(data.resp);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Converting and Binding Data in JSON format
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} data Response data
		 */
		_bindSummaryData: function (data) {
			// debugger; //eslint-disable-line
			var aSummary = [],
				aSummaryTot = [],
				oConsData = this._objApiSummary();
			this._jsonSummaryData(data, oConsData, aSummary);
			if (this.visiCons) {
				oConsData.date = "Total";
				oConsData.flag = 0;
				aSummaryTot.push(oConsData);
				this.byId("tabStatusTotal").setVisible(true);
			} else {
				this.byId("tabStatusTotal").setVisible(false);
			}
			this.byId("tabStatusTotal").setModel(new JSONModel(aSummaryTot), "Summary");
			this.byId("tabStatusAPI").setModel(new JSONModel(aSummary), "Summary");
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} data Response data
		 * @param {Object} oConsData Consolidate object
		 * @param {Object} aSummary final Summary object to bind in table
		 */
		_jsonSummaryData: function (data, oConsData, aSummary) {
			var oJsonData = this._objApiSummary(),
				oTempData = this._objApiSummary();

			for (var i = 0; i < data.length; i++) {
				this._sectionWise(oTempData, data[i], true);
				if ((i + 1 === data.length) || oTempData.gstin !== (data[i + 1].GSTIN || data[i + 1].gstin) ||
					oTempData.returnPeriod !== data[i + 1].returnPeriod || oTempData.date !== data[i + 1].date) {

					this._gstinWise(oJsonData, oTempData);
					oTempData = this._objApiSummary();
				}
				if ((i === data.length - 1) || (oJsonData.date !== data[i + 1].date && oJsonData.date !== "")) {
					aSummary.push(oJsonData);
					oJsonData = this._objApiSummary();
				}
				this._summaryDataTotal(oConsData, data[i]);
			}
		},

		/**
		 * Method called to get Total of Summary data for Response objects
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oConsData Consolidate object
		 * @param {Object} data Response object
		 */
		_summaryDataTotal: function (oConsData, data) {
			if (this.visiCons) {
				for (var j = 0; j < oConsData.items.length; j++) {
					if ((oConsData.items[j].gstin === data.GSTIN) && (oConsData.items[j].returnPeriod === data.returnPeriod)) {
						this._sectionWise(oConsData.items[j], data, false);
						oConsData.count += data.count || 0;
						oConsData.taxableValue += data.taxableValue || 0;
						oConsData.totalTaxes += data.totalTaxes || 0;
						oConsData.igst += data.igst || 0;
						oConsData.cgst += data.cgst || 0;
						oConsData.sgst += data.sgst || 0;
						oConsData.cess += data.cess || 0;
						break;
					}
				}
				if (j === oConsData.items.length) {
					var oTotData = this._objApiSummary();
					this._sectionWise(oTotData, data, true);
					this._gstinWise(oConsData, oTotData);
				}
			}
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oConsData Consolidate object
		 * @param {Object} data Response object
		 * @param {boolean} vFlag Flag value for sectionwise
		 */
		_sectionWise: function (oConsData, data, vFlag) {
			oConsData.items.push(data);
			oConsData.date = data.date;
			if (vFlag) {
				oConsData.gstin = data.GSTIN || data.gstin;
				oConsData.returnPeriod = data.returnPeriod;
				oConsData.authToken = data.authToken;
			}
			oConsData.count += data.count || 0;
			oConsData.taxableValue += data.taxableValue || 0;
			oConsData.totalTaxes += data.totalTaxes || 0;
			oConsData.igst += data.igst || 0;
			oConsData.cgst += data.cgst || 0;
			oConsData.sgst += data.sgst || 0;
			oConsData.cess += data.cess || 0;
			// 			oConsData.saveStatus = data.saveStatus;
			// 			oConsData.reviewStatus = data.reviewStatus;
			oConsData.flag = 1;
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oJsonData Json object
		 * @param {Object} oConsData Consolidate object
		 */
		_gstinWise: function (oJsonData, oConsData) {
			oJsonData.items.push(oConsData);

			oJsonData.date = oConsData.date;
			oJsonData.gstin = oConsData.gstin;
			oJsonData.returnPeriod = oConsData.returnPeriod;
			oJsonData.count += oConsData.count;
			oJsonData.taxableValue += oConsData.taxableValue;
			oJsonData.totalTaxes += oConsData.totalTaxes;
			oJsonData.igst += oConsData.igst;
			oJsonData.cgst += oConsData.cgst;
			oJsonData.sgst += oConsData.sgst;
			oJsonData.cess += oConsData.cess;
			oJsonData.authToken = oConsData.authToken;
			// 			oJsonData.saveStatus = oConsData.saveStatus;
			// 			oConsData.reviewStatus = oConsData.reviewStatus;
			oJsonData.flag = 0;
		},

		/**
		 * On press Expand / Collapse All button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressExpandCollapse: function (oEvent) {
			if (oEvent.getSource().getId().includes("bTotExpApi")) {
				this.byId("tabStatusTotal").expandToLevel(2);

			} else if (oEvent.getSource().getId().includes("bTotColApi")) {
				this.byId("tabStatusTotal").collapseAll();

			} else if (oEvent.getSource().getId().includes("bExpApi")) {
				this.byId("tabStatusAPI").expandToLevel(2);

			} else if (oEvent.getSource().getId().includes("bColApi")) {
				this.byId("tabStatusAPI").collapseAll();
			}
		},

		/**
		 * Called when user press Back button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressBack: function (oEvent) {
			this.byId("dpDataStatus").setVisible(true);
			this.byId("dpSummayAPI").setVisible(false);
			this.byId("tabData").setSelectedIndex(-1);
		},

		/**
		 * Called after upload is completed and response back from server
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fileUploader").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/*====================================================================================*/
		/*======= On Press Upload Button =====================================================*/
		/*====================================================================================*/
		onPressUpload: function (oEvent) {
			// debugger; //eslint-disable-line
			var oFileUploader = this.byId("fileUploader");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}

			var vIdMain = this.byId("rgReturnType").getSelectedButton().getId();

			if (vIdMain.includes("bGstr13b")) {
				var vId = this.byId("rgDataType").getSelectedButton().getId();
			} else if (vIdMain.includes("bNewReturn")) {
				vId = this.byId("rgNewReturnDataType").getSelectedButton().getId();
			}

			if (vId.includes("bOutward")) {
				this._uploadOutward(oFileUploader);

			} else if (vId.includes("bInward")) {
				this._uploadInward(oFileUploader);

			} else if (vId.includes("bOthers")) {
				this._uploadOthers(oFileUploader);

			} else if (vId.includes("bGstr3b")) {
				this._uploadGstr3B(oFileUploader);

			} else if (vId.includes("bGstr1")) {
				this._uploadGstr1(oFileUploader);
			}
			sap.ui.core.BusyIndicator.show(0);
			// oFileUploader.setAdditionalData(oKey + "-" + oText);
			oFileUploader.upload();
		},

		// 		Outward File Upload
		_uploadOutward: function (oFileUploader) {
			// var oRadio = this.byId("rbgOutward").getSelectedIndex();
			var vText = this.byId("rbgOutward").getSelectedButton().getDependents()[0].getProperty("text");
			switch (vText) {
			case "raw239": // Comprehensive Raw File
				oFileUploader.setUploadUrl("/aspsapapi/webEinvoiceRawUploadDocuments.do");
				break;
			case "b2c": // B2C
				oFileUploader.setUploadUrl("/aspsapapi/outwardB2c.do");
				break;
			case "shipBill": // Shipping Bill Details
				// oFileUploader.setUploadUrl("/aspsapapi"); 
				break;
			case "eComm": // Supplies through E-commerce
				oFileUploader.setUploadUrl("/aspsapapi/outwardTable4.do");
				break;
			case "raw109": //Raw File (109 Format)
				oFileUploader.setUploadUrl("/aspsapapi/webRawUploadDocuments.do");
				break;
			}
		},

		// 		Inward File Upload
		_uploadInward: function (oFileUploader) {
			// var oRadio = this.byId("rbgInward").getSelectedIndex();
			var vText = this.byId("rbgInward").getSelectedButton().getDependents()[0].getProperty("text");
			switch (vText) {
			case "rawFile": // Raw file
				oFileUploader.setUploadUrl("/aspsapapi/anx2InwardRawFileUploadDocuments.do");
				break;
			case "revImps": // Reverse Charge & Import Services file
				oFileUploader.setUploadUrl("/aspsapapi/table3h3i.do");
				break;
			case "missingDoc": // Missing Documents
				// oFileUploader.setUploadUrl("/aspsapapi"); 
				break;
			case "reconResponse": //Recon Response
				oFileUploader.setUploadUrl("/aspsapapi/anx2UserRespFileUpload.do");
				break;
			}
		},

		// 		Other File Upload
		_uploadOthers: function (oFileUploader) {
			// var oRadio = this.byId("rbgOthers").getSelectedIndex();
			var vText = this.byId("rbgOthers").getSelectedButton().getDependents()[0].getProperty("text");
			switch (vText) {
			case "rawFile":
				oFileUploader.setUploadUrl("/aspsapapi/ret1And1AUploads.do"); // RET 1 / 1A User Inputs
				break;
			case "lateFee":
				oFileUploader.setUploadUrl("/aspsapapi/interestAndLateFee.do"); // Interest and Late fee
				break;
			case "utilization": // Set off & Utilization
				oFileUploader.setUploadUrl("/aspsapapi/setOffAndUtilizations.do");
				break;
			case "payStatus": // Payment Status (ITC Reversal)
				// oFileUploader.setUploadUrl("/aspsapapi"); 
				break;
			case "refund": // Refund from Cash Ledger
				oFileUploader.setUploadUrl("/aspsapapi/refunds.do");
				break;
			}
		},

		// 		Old Outward File Upload (GSTR-1)
		_uploadGstr1: function (oFileUploader) {
			// var oRadio = this.byId("rbgGstr1").getSelectedIndex();
			var vText = this.byId("rbgGstr1").getSelectedButton().getDependents()[0].getProperty("text");
			switch (vText) {
			case "raw239": // Comprehensive Raw File Upload (239 Format)
				oFileUploader.setUploadUrl("/aspsapapi/webEinvoiceRawUploadDocuments.do");
				break;
			case "raw109": // Raw File Upload
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1RawUploadDocuments.do");
				break;
			case "b2cs": // B2CS 
				oFileUploader.setUploadUrl("/aspsapapi/webB2csUploadDocuments.do");
				break;
			case "advReciev": // Advance Received
				oFileUploader.setUploadUrl("/aspsapapi/webAtUploadDocuments.do");
				break;
			case "advAdjust": // Advance Adjusted
				oFileUploader.setUploadUrl("/aspsapapi/webTxpdUploadDocuments.do");
				break;
			case "invSeries": // Invoice Series
				oFileUploader.setUploadUrl("/aspsapapi/webInvoiceUploadDocuments.do");
				break;
				// case 5:
				// 	oFileUploader.setUploadUrl("/aspsapapi/webNilUploadDocuments.do"); // Nil/Non/Exemp
				// 	break;
				// case 6:
				// 	oFileUploader.setUploadUrl("/aspsapapi/webHsnUploadDocuments.do"); // HSN Summary
				// 	break;
			}
		},

		// 		Old Outward File Upload (GSTR-1)
		_uploadGstr3B: function (oFileUploader) {
			var oRadio = this.byId("rbgOld3B").getSelectedIndex();
			switch (oRadio) {
			case 0:
				//	oFileUploader.setUploadUrl("/aspsapapi/.do"); // Raw File Upload
				break;

			}
		},

		/**
		 * Called to get payload for getting file status data
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @return {Object} searchInfo
		 */
		_getFileStatusSearchInfo: function () {
			var searchInfo = {
				"req": {
					"dataType": this.byId("slFileDataType").getSelectedKey(),
					"fileType": this.byId("slStatsFileType").getSelectedKey(),
					"dataRecvFrom": this._formatDate(this.byId("drsUpload").getDateValue()),
					"dataRecvTo": this._formatDate(this.byId("drsUpload").getSecondDateValue())
				}
			};
			if (this.byId("slFileDataType").getSelectedKey() === "others") {
				searchInfo.req.dataType = "RET";
			}
			if (searchInfo.req.dataType === "gstr1" && searchInfo.req.fileType === "COMPREHENSIVE_RAW") {
				searchInfo.req.dataType = "outward";
			}
			return searchInfo;
		},

		/**
		 * Called to get file status of uploaded file using web-upload from server
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		_getFileStauts: function () {
			// debugger; //eslint-disable-line
			var that = this,
				aVertical = ["b2c"],
				vKey = that.byId("slStatsFileType").getSelectedKey(),
				vDataType = this.byId("slFileDataType").getSelectedKey(),
				oPropModel = this.getView().getModel("Properties"),
				oPropData = oPropModel.getData();

			if (vDataType === "gstr1" || vDataType === "gstr3b") {
				oPropData.rawSummary = false;
			} else {
				oPropData.rawSummary = true;
			}
			if (aVertical.includes(vKey)) {
				oPropData.verSummary = true;
			} else {
				oPropData.verSummary = false;
			}
			oPropModel.refresh(true);

			if (vKey === "raw" || vKey === "COMPREHENSIVE_RAW") {
				//var flag = true;
				that.byId("tabFileStatus").setVisible(true);
				that.byId("tabVerFileStatus").setVisible(false);
				that.byId("tabRecFileStatus").setVisible(false);

			} else if (vKey === "reconResponse") { // added by Vinay on 09-12-2019
				//flag = false;
				that.byId("tabFileStatus").setVisible(false);
				that.byId("tabVerFileStatus").setVisible(false);
				that.byId("tabRecFileStatus").setVisible(true);
				this.reconTable();
				return;
			} else {
				that.byId("tabFileStatus").setVisible(false);
				that.byId("tabVerFileStatus").setVisible(true);
				that.byId("tabRecFileStatus").setVisible(false);
			}
			var searchInfo = this._getFileStatusSearchInfo();
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.byId("tabFileStatus").getModel("FileStatus");
			if (oModel) {
				oModel.setData(null);
			}
			this._getfileStatusData(searchInfo);
		},

		/**
		 * API call to get File Status Data
		 * Developed by: Bharat Gupta on 04.03.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} payload Payload
		 */
		_getfileStatusData: function (payload) {
			var that = this;
			this.getView().setModel(new JSONModel(payload), "FileStatusPayload");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/fileStatus.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();

				var fileType = that.byId("slStatsFileType").getSelectedKey();
				if (fileType.toLowerCase() === "raw" || fileType.toLowerCase() === "comprehensive_raw") {
					that.byId("tabFileStatus").setModel(new JSONModel(data.resp), "FileStatus");
				} else {
					that.byId("tabVerFileStatus").setModel(new JSONModel(data.resp), "FileStatus");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called when user click download button in API Screen to download data in excel file
		 * Developed by - Bharat Gupta
		 * Modified by  - Bharat Gupta on 28.03.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent	Eventing parameter
		 * @param {number} count	Records Count
		 * @param {string} type 	Record Type
		 * @param {string} status	Record Status (active/inactive)
		 */
		onPressReportDownload: function (oEvent, count, type, status) {
			// debugger; //eslint-disable-line
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("Data").getObject(),
				vUrl = "/aspsapapi/downloadApiReports.do"; // "/aspsapapi/downloadcsvReports.do"; 

			// var vType, vStatus, count = 0;
			// var vText = oEvent.getSource().getDependents()[0].getText();
			// switch (vText) {
			// case "Total":
			// 	vType = "totalrecords";
			// 	count = obj.totalRecords;
			// 	break;
			// case "ProcessActive":
			// 	vType = "processed";
			// 	vStatus = "processedActive";
			// 	count = obj.processedActive;
			// 	break;
			// case "ProcessInactive":
			// 	vType = "processed";
			// 	vStatus = "processedInactive";
			// 	count = obj.processedInactive;
			// 	break;
			// case "ErrorActive":
			// 	vType = "error";
			// 	vStatus = "errorActive";
			// 	count = obj.errorActive;
			// 	break;
			// case "ErrorInactive":
			// 	vType = "error";
			// 	vStatus = "errorInactive";
			// 	count = obj.errorInactive;
			// 	break;
			// case "InfoActive":
			// 	vType = "processedinfo";
			// 	vStatus = "processedInfoActive";
			// 	count = obj.infoActive;
			// 	break;
			// case "InfoInactive":
			// 	vType = "processedinfo";
			// 	vStatus = "processedInfoInactive";
			// 	count = obj.infoInactive;
			// 	break;
			// }
			if (count === 0) {
				sap.m.MessageBox.information("No data to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oPayload = {
				"req": {
					"dataType": this.getView().byId("slStatsDataType").getSelectedKey(),
					"entityId": [$.sap.entityID],
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"type": type,
					"status": status || null,
					"dataSecAttrs": {
						"GSTIN": this.getView().byId("slStatsGSTIN").getSelectedKeys()
					}
				}
			};
			if (obj.receivedDate !== "") {
				oPayload.req.dataRecvFrom = obj.receivedDate;
				oPayload.req.dataRecvTo = obj.receivedDate;

			} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RECEIVED_DATE_SEARCH") {
				oPayload.req.dataRecvFrom = this.byId("iFromDate").getValue();
				oPayload.req.dataRecvTo = this.byId("iToDate").getValue();

			} else {
				oPayload.req.taxPeriodFrom = this.byId("iFromPeriod").getValue();
				oPayload.req.taxPeriodTo = this.byId("iToPeriod").getValue();
			}
			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs);
			}
			this.excelDownload(oPayload, vUrl);
		},

		/**
		 * Called when user click download button in File Status to download data in excel file
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 * @param {number} count Record count
		 * @param {string} type Download Type
		 * @param {string} status Status of Records (Active/Inactive)
		 */
		onPressFileStatusDownload: function (oEvent, count, type, status) {
			// debugger; //eslint-disable-line
			// var count = 0,
			// vText = oEvent.getSource().getDependents()[0].getText(),
			var vUrl = "/aspsapapi/downloadAnx1Reports.do",
				obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("FileStatus").getObject();

			// switch (vText) {
			// case "Total":
			// 	var vType = "totalrecords",
			// 		vStatus = null;
			// 	count = obj.total;
			// 	break;
			// case "ProcessActive":
			// 	vType = "processed";
			// 	vStatus = "processedActive";
			// 	count = obj.processedActive;
			// 	break;
			// case "ProcessInactive":
			// 	vType = "processed";
			// 	vStatus = "processedInactive";
			// 	count = obj.processedInactive;
			// 	break;
			// case "StructuredError":
			// 	vType = "error";
			// 	vStatus = "errorSv";
			// 	count = obj.strucError;
			// 	break;
			// case "ErrorActive":
			// 	vType = "error";
			// 	vStatus = "errorActive";
			// 	count = obj.errorsActive;
			// 	break;
			// case "ErrorInactive":
			// 	vType = "error";
			// 	vStatus = "errorInactive";
			// 	count = obj.errorsInactive;
			// 	break;
			// case "ErrorTotal":
			// 	vType = "error";
			// 	vStatus = "errorTotal";
			// 	count = obj.totalStrucBusinessError;
			// 	break;
			// case "InfoActive":
			// 	vType = "processedinfo";
			// 	vStatus = "processedInfoActive";
			// 	count = obj.infoActive;
			// 	break;
			// case "InfoInactive":
			// 	vType = "processedinfo";
			// 	vStatus = "processedInfoInactive";
			// 	count = obj.infoInactive;
			// 	break;
			// }
			if (count === 0) {
				sap.m.MessageBox.information("No data available to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var aData = {
				"req": {
					"fileId": obj.id,
					"type": type
				}
			};
			if (type === "error") {
				aData.req.errorType = status;
			} else {
				aData.req.status = status;
			}
			this.excelDownload(aData, vUrl);
		},

		/**
		 * Called when user click download button in File Status to download Vertical Upload data in excel file
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing parameter
		 * @param {string} type Downlaod Type
		 * @param {number} count Record count
		 */
		onVerticalDownload: function (oEvent, type, count) {
			// debugger; //eslint-disable-line
			// var count = 0,
			// 	vText = oEvent.getSource().getDependents()[0].getText(),
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("FileStatus").getObject(),
				sel = this.getView().byId("slStatsReturnType").getSelectedKey(); // Added by Jakeer Syed on 08.11.2019

			if (sel === "newReturn") { // Modified by Jakeer Syed on 08.11.2019
				// Modified by:Bharat Gupta on 20.01.2020 for Ret1/1a Downlaod
				if (obj.fileType === "RET1AND1A" || obj.fileType === "INTEREST" || obj.fileType === "SETOFFANDUTIL" || obj.fileType === "REFUNDS") {
					var vUrl = "/aspsapapi/downloadRet1And1AVerticalReports.do";
				} else {
					vUrl = "/aspsapapi/downloadVerticalReports.do";
				}
			} else if (sel === "gstr13b") {
				vUrl = "/aspsapapi/downloadGstr1VerticalReports.do";
			} // End of Code by Jakeer Syed

			// switch (vText) {
			// case "Total":
			// 	var vType = "totalrecords";
			// 	count = obj.total;
			// 	break;
			// case "Processed":
			// 	vType = "processed";
			// 	count = obj.processed;
			// 	break;
			// case "Errors":
			// 	vType = "error";
			// 	count = obj.error;
			// 	break;
			// case "Information":
			// 	vType = "processedinfo";
			// 	count = obj.information;
			// 	break;
			// }
			if (count === 0) {
				sap.m.MessageBox.information("No data to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var aData = {
				"req": {
					"fileId": obj.id,
					"type": type
				}
			};
			this.excelDownload(aData, vUrl);
		},

		/**
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeReturnType: function (oEvent) {
			// debugger; //eslint-disable-line
			if (oEvent.getSource().getSelectedKey() === "gstr13b") {
				var oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataType.gstr13b,
					vKey = "gstr1";
			} else {
				oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataType.newReturn;
				vKey = "outward";
			}
			this.byId("slFileDataType").setModel(new JSONModel(oDataType), "DataType");

			var oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileType,
				oFileModel = this.byId("slStatsFileType").getModel("FileType");

			if (vKey === "outward" || vKey === "gstr1") {
				var fileType = this._removeComprehensiveRaw(oFileType[vKey]);
				oFileModel.setData(fileType);
			} else {
				oFileModel.setData(oFileType[vKey]);
			}
			oFileModel.refresh(true);
			this.byId("slStatsFileType").setSelectedKey(oFileType[vKey][0].key);
		},

		/**
		 * Called when user click on Upload Date Link to see summary of File
		 * Develped by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressFileDetail: function (oEvent) {
			// debugger; //eslint-disable-line
			this.visiCons = false;
			var obj = oEvent.getSource().getBindingContext("FileStatus").getObject(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"dataType": obj.dataType,
						"fileType": obj.fileType,
						"fileId": [obj.id]
					}
				};
			this._fileSummary(oPayload);
		},

		/**
		 * Called when user click on Summary button on selected files
		 * Develped by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {string} type for which File Type event trigger (Raw, Vertical, Recon)
		 */
		onPressFileSummary: function (type) {
			// debugger; //eslint-disable-line
			if (type === "raw") {
				var oData = this.byId("tabFileStatus").getModel("FileStatus").getData(),
					aIndices = this.byId("tabFileStatus").getSelectedIndices();
			} else if (type === "vertical") {
				oData = this.byId("tabVerFileStatus").getModel("FileStatus").getData();
				aIndices = this.byId("tabVerFileStatus").getSelectedIndices();
			}
			if (aIndices.length === 0) {
				var oBundle = this.getResourceBundle();
				sap.m.MessageToast.show(oBundle.getText("msgSelectOneRecord"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			var oPayload = {
				"req": {
					"entityId": [$.sap.entityID],
					"dataType": oData[aIndices[0]].dataType,
					"fileType": oData[aIndices[0]].fileType,
					"fileId": []
				}
			};
			for (var i = 0; i < aIndices.length; i++) {
				oPayload.req.fileId.push(oData[aIndices[i]].id);
			}
			this.visiCons = false;
			this._fileSummary(oPayload);
		},

		/**
		 * Api called to get Summary of File(s)
		 * Develped by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} payload Payload object
		 */
		_fileSummary: function (payload) {
			// debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			that.byId("txtApiSumm").setText("File Summary");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getDataStatusFileSummary.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpDataStatus").setVisible(false);
				that.byId("dpSummayAPI").setVisible(true);
				if (data.hdr.status === "E") {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
					that.byId("tabStatusAPI").setModel(new JSONModel(), "Summary");
				} else {
					that._bindSummaryData(data.resp);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Called to get SFTP file status from server
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		getSftpStatus: function () {
			// debugger; //eslint-disable-line
			// var that = this;
			// var searchInfo = this._getFileStatusSearchInfo();
		},

		/**
		 * Called to clear all filter value & table data to default
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onPressClear: function (oEvent) {
			// debugger; //eslint-disable-line
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("dAdapt")) {
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
			if (this.byId("sbStatus").getSelectedKey() === "api") {
				// var oDataEntity = this.byId("slStatsEntity").getModel("EntityModel").getData();
				this.getView().byId("slStatsDataType").setSelectedKey("outward");
				this.getView().byId("slStatsCriteria").setSelectedKey("RECEIVED_DATE_SEARCH");
				// this.getView().byId("slStatsEntity").setSelectedKey(oDataEntity[0].entityId);
				this.getView().byId("slStatsGSTIN").clearSelection();
				this._bindDefaultDate();

				var oModel = this.getView().getModel("DataStatus");
				oModel.getData().returnPeriod = false;
				oModel.refresh(true);
				this._getDataStatusAPI();

			} else {
				var oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileType,
					oFileModel = this.byId("slStatsFileType").getModel("FileType");
				oFileModel.setData(oFileType.outward);
				oFileModel.refresh(true);

				this.getView().byId("slFileDataType").setSelectedKey("outward");
				this._bindDefaultUploadDate();
				this._getFileStauts();
			}
		},

		/**
		 * Method call to download Error/Info Catalog in excel
		 * Developed by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} vValue Catalog type (i.e: err/info)
		 * @param {string} vType Application type (i.e: api/file)
		 */
		onPressCatalog: function (oEvent, vValue, vType) {
			// debugger; //eslint-disable-line
			if (vType === "api") {
				var vDataType = this.byId("slStatsDataType").getSelectedKey();
			} else {
				vDataType = this.byId("slFileDataType").getSelectedKey();
			}
			var vUrl = "/aspsapapi/errorInfoCatalog.do",
				oPayload = {
					"req": {
						"dataType": vDataType,
						"type": vValue
					}
				};
			this.excelDownload(oPayload, vUrl);
		},

		////BOC RAkesh Bommidi on 27.01.2020            (((added by Vinay on 09-12-2019)
		reconTable: function () {
			var that = this;
			var searchInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"dataType": this.byId("slFileDataType").getSelectedKey() === "inward" ? "Inward" : "Inward",
					"fileType": this.byId("slStatsFileType").getSelectedKey() === "reconResponse" ? "Recon Response" : "Recon Response",
					"returnType": this.byId("slStatsReturnType").getSelectedKey(),
					"uploadFromDate": this._formatDate(this.byId("drsUpload").getDateValue()),
					"uploadToDate": this._formatDate(this.byId("drsUpload").getSecondDateValue())
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getReconResponseDataStatus.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("tabRecFileStatus").setModel(new JSONModel(data.resp), "ReconFileStatus");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onTotRecErrDownload: function (oEvent) {
			//var vText = oEvent.getSource().getDependents()[0].getText();
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("ReconFileStatus").getObject();
			var path = "/aspsapapi/generateReport.do?fileId=" + obj.fileId + "&fileType=Total";
			window.open(path);
		},

		onErrDownload: function (oEvent) {
			//var vText = oEvent.getSource().getDependents()[0].getText();
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("ReconFileStatus").getObject();
			var path = "/aspsapapi/generateReport.do?fileId=" + obj.fileId + "&fileType=Error";
			window.open(path);
		}
	});
});