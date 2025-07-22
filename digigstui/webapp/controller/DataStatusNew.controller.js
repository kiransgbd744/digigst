var searchData = {};
this.withTrans = false;
sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/util/Storage",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, Storage, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.DataStatusNew", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 */
		onInit: function () {
			this.bindDefaultData();
			this.getOwnerComponent().getRouter().getRoute("DataStatusNew").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger(),
				oName = oEvent.getParameter("name");

			oHashChanger.setHash("DataStatusNew");
			if (oName === "DataStatusNew") {
				this.byId("slStatsDataType").setSelectedKey("outward");
				this.getAllGSTIN("O");
				//this.getDataStatusAPI();
				this.byId("sbUpload").setVisible(true);
				this.byId("sbUpload").setSelectedKey("upload");
				this.byId("sbStatus").setSelectedKey("upload");
				this.byId("tabRecFileStatus").setVisible(false);
				this.byId("tabData").setVisible(false);
				this._uploadVisible(true, "U");
				this._getGstr8Gstin();
			}
		},

		onAfterRendering: function () {
			this._bindDefaultDate();
			this._bindDefaultUploadDate();
			this._bindDefaultSftpDate();
			var oModel = this.getOwnerComponent().getModel("DropDown"),
				oDataType = oModel.getProperty("/dataTypeNew"),
				oFileType = oModel.getProperty("/fileTypeNew"),
				oSftpDataType = oModel.getProperty("/sftpDataType"),
				oSftpFileType = oModel.getProperty("/sftpFileType"),
				oPropIMS = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR1"),
				oPropIMSGR2 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR2"),
				oPropIMSGR4 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR4");

			oFileType.inward.forEach(function (e) {
				if (e.key === "IMS") {
					e.enable = oPropIMS || false;
				} else if (e.key === "2BPR_IMS_RECON_RESPONSE") {
					e.enable = (oPropIMS && oPropIMSGR2) || false;
				} else if (e.key === "2APR_IMS_RECON_RESPONSE") {
					e.enable = (oPropIMS && oPropIMSGR4) || false;
				}
			});
			this.byId("slFileDataType").setModel(new JSONModel(oDataType.integrated), "DataType");
			this.byId("slStatsFileType").setModel(new JSONModel(oFileType.outward), "FileType");
			this.byId("slSftpDataType").setModel(new JSONModel(oSftpDataType.integrated), "DataType");
			this.byId("slSftpFileType").setModel(new JSONModel(oSftpFileType.outward), "FileType");
			oModel.refresh(true);

			var oVisiSum = {
				"apieinv": false,
				"apiewb": false,
				"apigst": false,
				"einv": true, // true,
				"ewb": true, // true,
				"gst": true, // true
				"apiFullScreen": false,
				"fileFullScreen": false,
				"sftpFullScreen": false
			}; //End of Code
			this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");
		},

		_bindDefaultUploadDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iUploadFromDate").setDateValue(vDate);
			this.byId("iUploadFromDate").setMaxDate(new Date());

			this.byId("iUploadToDate").setDateValue(new Date());
			this.byId("iUploadToDate").setMinDate(vDate);
			this.byId("iUploadToDate").setMaxDate(new Date());
		},

		/**
		 * Developed by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 */
		_bindDefaultSftpDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("dtSftpUploadFrom").setDateValue(vDate);
			this.byId("dtSftpUploadFrom").setMaxDate(new Date());

			this.byId("dtSftpUploadTo").setDateValue(new Date());
			this.byId("dtSftpUploadTo").setMinDate(vDate);
			this.byId("dtSftpUploadTo").setMaxDate(new Date());
		},

		_getGstr8Gstin: function () {
			var payload = {
				"req": {
					"entityId": [$.sap.entityID]
				}
			};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr8Gstins.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var entityData = data.resp.gstins.map(function (el) {
							return {
								value: el.gstin
							};
						});
						if (entityData.length) {
							entityData.unshift({
								value: "All"
							})
						}
						this.getView().setModel(new JSONModel(entityData), "Gstr8Gstin");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onSelectCriteria: function () {
			var vSelectKey = this.getView().byId("idAPICriteria").getSelectedKey();
			if (vSelectKey === "docDate") {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.Period = false;
			} else {
				this.getOwnerComponent().getModel("invManage").getData().DatePeriod.Period = true;
			}
		},

		/**
		 * Validate Date Range Selection for Date / Period
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("iFromDate")) {
				var vDatePicker = "iToDate";
			} else if (oEvent.getSource().getId().includes("iFromPeriod")) {
				vDatePicker = "iToPeriod";
			} else if (oEvent.getSource().getId().includes("iUploadFromDate")) {
				vDatePicker = "iUploadToDate";
			} else if (oEvent.getSource().getId().includes("dtSftpUploadFrom")) {
				vDatePicker = "dtSftpUploadTo";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
			if (oEvent.getSource().getId().includes("bApply")) {
				this.getDataStatusAPI();
			}
		},

		onSearch: function (oEvent) {
			if (this.byId("sbStatus").getSelectedKey().includes("api")) {
				this.getDataStatusAPI();

			} else if (this.byId("sbStatus").getSelectedKey().includes("upload")) {
				this.getFileStatusData("");
			} else {
				this._getSftpFileData();
			}
		},

		getDataStatusAPI: function () {
			var vSelectedKey = this.getView().byId("slStatsCriteria").getSelectedKey();
			var vReceivFromDate, vReceivToDate, vReturnFromDate, vReturnToDate, vDocumentDateFrom, vDocumentDateTo, vAccVoucherDateFrom,
				vAccVoucherDateTo;
			var aGstin = this.getView().byId("slStatsGSTIN").getSelectedKeys();

			if (vSelectedKey === "RECEIVED_DATE_SEARCH" || vSelectedKey === "") {
				vReceivFromDate = this.getView().byId("iFromDate").getValue();
				vReceivToDate = this.getView().byId("iToDate").getValue();
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocumentDateFrom = null;
				vDocumentDateTo = null;
				vAccVoucherDateFrom = null;
				vAccVoucherDateTo = null;

			} else if (vSelectedKey === "DocDate") {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocumentDateFrom = this.getView().byId("iFromDate").getValue();
				vDocumentDateTo = this.getView().byId("iToDate").getValue();;
				vAccVoucherDateFrom = null;
				vAccVoucherDateTo = null;
			} else if (vSelectedKey === "AccVoucherDate") {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = null;
				vReturnToDate = null;
				vDocumentDateFrom = null;
				vDocumentDateTo = null;
				vAccVoucherDateFrom = this.getView().byId("iFromDate").getValue();;
				vAccVoucherDateTo = this.getView().byId("iToDate").getValue();;
			} else {
				vReceivFromDate = null;
				vReceivToDate = null;
				vReturnFromDate = this.getView().byId("iFromPeriod").getValue();
				vReturnToDate = this.getView().byId("iToPeriod").getValue();
				vDocumentDateFrom = null;
				vDocumentDateTo = null;
				vAccVoucherDateFrom = null;
				vAccVoucherDateTo = null;
			}

			searchData = this._getPayloadStruct();
			// searchData.hdr.pageNum = vPageNo - 1;;
			// searchData.hdr.pageSize = 50;
			searchData.req.dataType = this.getView().byId("slStatsDataType").getSelectedKey();
			searchData.req.entityId = [$.sap.entityID];
			searchData.req.dataRecvFrom = vReceivFromDate;
			searchData.req.dataRecvTo = vReceivToDate;
			searchData.req.taxPeriodFrom = vReturnFromDate;
			searchData.req.taxPeriodTo = vReturnToDate;
			searchData.req.documentDateFrom = vDocumentDateFrom;
			searchData.req.documentDateTo = vDocumentDateTo;
			searchData.req.accVoucherDateFrom = vAccVoucherDateFrom;
			searchData.req.accVoucherDateTo = vAccVoucherDateTo;
			searchData.req.docDateFrom = null;
			searchData.req.docDateTo = null;
			searchData.req.dataSecAttrs.GSTIN = this.removeAll(aGstin);

			if (this.getView().byId("dAdapt")) {
				this._getOtherFiltersASP(searchData.req.dataSecAttrs);
			}
			this.getDataStatusAPIFinal(searchData);
		},

		getDataStatusAPIFinal: function (searchInfo) {
			var that = this,
				oModel = this.getView().getModel("DataStatusAPI");

			sap.ui.core.BusyIndicator.show(0);
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getEInvoceDataStatusScreen.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.bindEInvoceDataStatusScreen(data);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getEInvoceDataStatusScreen : Error");
				});
			});
		},

		bindEInvoceDataStatusScreen: function (data) {
			var key = this.getView().byId("slStatsDataType").getSelectedKey();
			var oVisiSum = {
				"apieinv": false,
				"apiewb": false,
				"apigst": false,
				"einv": false, //oData.einv,
				"ewb": false, //oData.ewb,
				"gst": false, //oData.gst, 
				"dms": ["outward", "inward"].includes(key),
				"apiFullScreen": false,
				"fileFullScreen": false,
				"sftpFullScreen": false
			};
			this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");

			this.getView().setModel(new JSONModel(data), "DataStatusAPI");
		},

		onChangeSegment: function (oEvent) {
			var vSegmentBtnId = oEvent.getSource().getId(),
				vFlag = false;

			this.byId("bAdaptFilter").setVisible(vFlag);
			if (vSegmentBtnId.includes("sbStatus")) {
				var oSegmentBtn = oEvent.getSource().getSelectedKey(),
					oModel = this.getView().getModel("DataStatus");
				if (oSegmentBtn === "api") {
					var vSb = "A";
					this.byId("tabRecFileStatus").setVisible(false);
					this.byId("bAdaptFilter").setVisible(!vFlag);
					oModel.getData().apiFilter = true;
					oModel.getData().sftpFilter = false;
					this.getDataStatusAPI();
				} else if (oSegmentBtn === "upload") {
					if (this.byId("sbUpload").getSelectedKey().includes("upload")) {
						vFlag = true;
						vSb = "U";
						this.byId("tabRecFileStatus").setVisible(false);
						this.byId("tabData").setVisible(false);
					} else {
						vSb = "F";
					}
					oModel.getData().apiFilter = false;
					oModel.getData().sftpFilter = false;

				} else if (oSegmentBtn === "sftp") {
					vSb = "S";
					oModel.getData().apiFilter = false;
					oModel.getData().sftpFilter = true;

				} else {
					vSb = "UI";
					oModel.getData().apiFilter = false;
				}
				oModel.refresh(true);
			} else if (vSegmentBtnId.includes("sbUpload")) {
				vSb = "F";
				if (oEvent.getSource().getSelectedKey() === "upload") {
					this.byId("tabRecFileStatus").setVisible(false);
					this.byId("tabData").setVisible(false);
					vFlag = true;
					vSb = "U";
				}
			}
			// this._getFileStauts();
			this._uploadVisible(vFlag, vSb);
		},

		_uploadVisible: function (vFlag, vSb) {
			this.byId("fbStatus").setVisible(!vFlag);
			this.byId("fbStatusHbox").setVisible(!vFlag);
			this.byId("rbReturnType").setVisible(vFlag);
			this.byId("iRbgStatus").setVisible(vFlag);
			this.byId("fileUploader").setVisible(vFlag);
			this.byId("btnUpload").setVisible(vFlag);
			var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
			if (vGR10) {
				this.byId("iRbgTransformation").setVisible(vFlag);
			}

			if (vSb === "A") {
				this.byId("id_chartds3").setVisible(true);
			} else {
				this.byId("id_chartds3").setVisible(false);
			}
			if (vSb === "U" || vSb === "F") {
				this.byId("sbUpload").setVisible(true);
			} else {
				this.byId("sbUpload").setVisible(false);
			}
			if (vSb === "F" || vSb === "S") {
				if (vSb === "F") {
					var key = this.byId("slStatsFileType").getSelectedKey();
					if (key === "RAW") {
						// this.byId("tabFileStatus").setVisible(!vFlag);
					} else {
						// this.byId("tabVerFileStatus").setVisible(!vFlag);
						this.byId("tabRecFileStatus").setVisible(!vFlag);
						this.byId("tabData").setVisible(!vFlag);
						this.byId("tabUientriesStatus").setVisible(false);
					}
					this.byId("iCcSftpData").setVisible(false);
					this.byId("tabUientriesStatus").setVisible(false);
					// this.getFileStatusData("");

				} else {
					// this.byId("tabFileStatus").setVisible(false);
					// this.byId("tabVerFileStatus").setVisible(false);
					this.byId("tabRecFileStatus").setVisible(false);
					this.byId("tabData").setVisible(false);
					this.byId("tabUientriesStatus").setVisible(false);
					this.byId("iCcSftpData").setVisible(true);
					var vDate = new Date();
					vDate.setDate(vDate.getDate() - 9);

					this.byId("iUploadFromDate").setDateValue(vDate);
					this.byId("iUploadFromDate").setMaxDate(new Date());
					this.byId("iUploadToDate").setDateValue(new Date());
					this.byId("iUploadToDate").setMinDate(vDate);
					this.byId("iUploadToDate").setMaxDate(new Date());
					this._getSftpFileData();
					// this.getFileStatusData("");
				}
			} else {
				// this.byId("tabFileStatus").setVisible(false);
				// this.byId("tabVerFileStatus").setVisible(false);
				this.byId("tabRecFileStatus").setVisible(false);
				this.byId("tabData").setVisible(false);
				this.byId("iCcSftpData").setVisible(false);
				this.byId("tabUientriesStatus").setVisible(false);

			}
			if (vSb === "UI") {
				this.byId("tabRecFileStatus").setVisible(false);
				this.byId("tabData").setVisible(false);
				this.byId("iCcSftpData").setVisible(false);
				this.byId("tabUientriesStatus").setVisible(true);
				// this._getFileStauts();

			}
			if (vSb === "F") {
				var oFileModel = this.getView().getModel("FileStatus");
				if (!oFileModel) {
					vDate = new Date();
					vDate.setDate(vDate.getDate() - 9);

					this.byId("iUploadFromDate").setDateValue(vDate);
					this.byId("iUploadFromDate").setMaxDate(new Date());
					this.byId("iUploadToDate").setDateValue(new Date());
					this.byId("iUploadToDate").setMinDate(vDate);
					this.byId("iUploadToDate").setMaxDate(new Date());
				}
				this.getFileStatusData("");
			}
		},

		getFileStatusData: function (oEntity) {
			var that = this,
				vKey = that.byId("slStatsFileType").getSelectedKey();

			if (["raw", "COMPREHENSIVE_RAW", "ITC04_FILE", "COMPREHENSIVE_INWARD_RAW", "COMPREHENSIVE_RAW_1A"].includes(vKey)) {
				if (vKey === "COMPREHENSIVE_RAW_1A") {
					that.byId("bStatsSummUp3").setVisible(false);
				} else {
					that.byId("bStatsSummUp3").setVisible(true);
				}
				that.byId("idShowing").setVisible(true);
				that.byId("tabData").setVisible(true);
				that.byId("tabVerFileStatus").setVisible(false);
				that.byId("tabVerFileStatus1").setVisible(false); // Added by chaithra on 1/2/2021 for delete/exclusive save
				var oData = this.getView().getModel("visiSummDataStatus2").getData();
				if (["COMPREHENSIVE_RAW", "COMPREHENSIVE_RAW_1A"].includes(vKey)) {
					that.byId("deleteId").setVisible(false);
					that.byId("ideinv").setEnabled(true);
					that.byId("idewb").setEnabled(true);
					that.byId("idgst").setEnabled(true);

					var oVisiSum = {
						"apieinv": false,
						"apiewb": false,
						"apigst": false,
						"einv": true, //oData.einv,
						"ewb": true, //oData.ewb,
						"gst": true, //oData.gst, 
						"dms": true,
						"apiFullScreen": false,
						"fileFullScreen": false,
						"sftpFullScreen": false
					};
					this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");
				} else {
					// Commentted by Bharat Gupta on 14.04.2020 for Clean - up	
					that.byId("deleteId").setVisible(true);
					that.byId("ideinv").setEnabled(false);
					that.byId("idewb").setEnabled(false);
					that.byId("idgst").setEnabled(false);
					var oVisiSum = {
						"apieinv": false,
						"apiewb": false,
						"apigst": false,
						"einv": false, // true,
						"ewb": false, // true,
						"gst": false, // true
						"dms": vKey === 'COMPREHENSIVE_INWARD_RAW' ? true : false,
						"apiFullScreen": false,
						"fileFullScreen": false,
						"sftpFullScreen": false
					}; // End of Code
					this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");
				}
			} else {
				that.byId("bStatsSummUp3").setVisible(false);
				that.byId("idShowing").setVisible(false);
				that.byId("deleteId").setVisible(false);
				that.byId("tabData").setVisible(false);
				that.byId("tabVerFileStatus").setVisible(true);
				that.byId("tabVerFileStatus1").setVisible(false); // Added by chaithra on 1/2/2021 for delete/exclusive save

				//================= Added by chaithra for E-invoice Recon on 20/1/2021 ==========//	
				if (vKey === "EINVOICE_RECON" || vKey === "180_days_Reversal" || vKey === "REV_180DAYS_RESPONSE" || vKey === "VerticalUploadHSN") { //180 days reversal added by chaithra on 30/3/2021
					that.byId("clmnInformation").setVisible(false);
				}
				if (vKey === "DELETE_GSTN" || vKey === "EXCLUSIVE_SAVE_FILE") {
					that.byId("tabVerFileStatus").setVisible(false);
					that.byId("tabVerFileStatus1").setVisible(true);
				}
				if (vKey !== "EINVOICE_RECON" && vKey !== "DELETE_GSTN" && vKey !== "EXCLUSIVE_SAVE_FILE" && vKey !== "180_days_Reversal" && vKey !==
					"REV_180DAYS_RESPONSE" && vKey !== "VerticalUploadHSN") { //180days reversal added by chaithra on 30/3/2021
					that.byId("clmnInformation").setVisible(true);
				}
				//================== code ended by chaithra on 21/1/2021 ========================//
			}
			var searchInfo = this._getFileStatusSearchInfo();
			searchInfo.req.entityId = searchInfo.req.entityId[0];
			this.getFileStatusDataFinal(searchInfo);
		},

		getFileStatusDataFinal: function (postData) {
			var oView = this.getView(),
				oModel = oView.getModel("FileStatus");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/newFileStatus.do",
				contentType: "application/json",
				data: JSON.stringify(postData)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();

				if (data.resp === undefined || data.resp.length === 0) {
					MessageBox.information("No Data");
					var oFileStatusData = new JSONModel(null);
					oView.setModel(oFileStatusData, "FileStatus");
				} else {
					oFileStatusData = new JSONModel(data);
					oView.setModel(oFileStatusData, "FileStatus");
				}

			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				MessageBox.error("fileStatus : Error");
				var oFileStatusData = new JSONModel(null);
				oView.setModel(oFileStatusData, "FileStatus");
			});
		},

		getViewRulesInUpload: function () {
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getViewRulesInUpload.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.status = "S") {
						var oModel = new JSONModel(data.rules);
						oModel.setSizeLimit(data.rules.length);
						this.getView().setModel(oModel, "Rules");
					} else {
						this.getView().setModel(new JSONModel(null), "Rules");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(null), "Rules");
				}.bind(this));
		},

		onSelectFileTransformation: function (oEvent) {
			var vId = oEvent.getSource().getSelectedButton().getId();
			if (vId.includes("withTrans")) {
				this.byId("idDMSRule").setVisible(true);
				this.getViewRulesInUpload();
				this.withTrans = true;
			} else {
				this.byId("idDMSRule").setVisible(false);
				this.withTrans = false;
			}

		},

		onSelectReturnGroup: function (oEvent) {
			var vId = oEvent.getSource().getSelectedButton().getId(),
				data = this.getView().getModel("userPermission").getProperty("/appPermission/P99"),
				data1 = this.getView().getModel("userPermission").getProperty("/appPermission/P12");
			if (data) {
				this.byId("fileUploader").setEnabled(data);
				this.byId("btnUpload").setEnabled(data);
			}
			if (data1) {
				this.byId("fileUploader").setEnabled(data1);
				this.byId("btnUpload").setEnabled(data1);
			}

			if (vId.includes("bIntegrated")) {
				this.byId("rgIntegrated").setVisible(true);
				var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
				if (vGR10) {
					this.byId("iRbgTransformation").setVisible(true);
				}
				this.byId("rgGSTReturns").setVisible(false);
				this.byId("rgEWB").setVisible(false);
			} else if (vId.includes("bGSTReturns")) {
				this.byId("rgIntegrated").setVisible(false);
				this.byId("iRbgTransformation").setVisible(false);
				this.byId("rgGSTReturns").setVisible(true);
				this.byId("rgEWB").setVisible(false);
			} else {
				this.byId("rgIntegrated").setVisible(false);
				this.byId("iRbgTransformation").setVisible(false);
				this.byId("rgGSTReturns").setVisible(false);
				this.byId("rgEWB").setVisible(true);
			}
			this.onSelectRadioGroup();
		},

		onSelectRadioGroup: function () {
			var data = this.getView().getModel("userPermission").getProperty("/appPermission/P99"),
				data1 = this.getView().getModel("userPermission").getProperty("/appPermission/P12");
			if (data) {
				this.byId("fileUploader").setEnabled(data);
				this.byId("btnUpload").setEnabled(data);
			}
			if (data1) {
				this.byId("fileUploader").setEnabled(data1);
				this.byId("btnUpload").setEnabled(data1);
			}

			this.byId("rbgIOutward").setVisible(false);
			this.byId("rbgIGSTR1AOutward").setVisible(false);
			this.byId("rbgIInward").setVisible(false);
			this.byId("rbgGROutward").setVisible(false);
			this.byId("rbgGRGSTR1AOutward").setVisible(false);
			this.byId("rbgGRInward").setVisible(false);
			this.byId("rbgGRGSTR3B").setVisible(false);
			this.byId("rbgGRGSTR9").setVisible(false);
			this.byId("rbgGROthers").setVisible(false);
			this.byId("rbgewbEWB").setVisible(false);

			var vIdMain = this.byId("rgReturnType").getSelectedButton().getId();

			if (vIdMain.includes("bIntegrated")) {
				var vId = this.byId("rgIntegrated").getSelectedButton().getId();
			} else if (vIdMain.includes("bGSTReturns")) {
				vId = this.byId("rgGSTReturns").getSelectedButton().getId();
			} else if (vIdMain.includes("bEWB")) {
				vId = this.byId("rgEWB").getSelectedButton().getId();
			}

			if (vId.includes("bIOutward")) {
				this.byId("rbgIOutward").setVisible(true);

			} else if (vId.includes("bIGSTR1AOutward")) {
				this.byId("rbgIGSTR1AOutward").setVisible(true);

			} else if (vId.includes("bIInward")) {
				this.byId("rbgIInward").setVisible(true);

			} else if (vId.includes("bGROutward")) {
				this.byId("rbgGROutward").setVisible(true);

			} else if (vId.includes("bGRGSTR1AOutward")) {
				this.byId("rbgGRGSTR1AOutward").setVisible(true);

			} else if (vId.includes("bGRInward")) {
				this.byId("rbgGRInward").setVisible(true);

			} else if (vId.includes("bGRGSTR3B")) {
				this.byId("rbgGRGSTR3B").setVisible(true);

			} else if (vId.includes("bGRGSTR9")) {
				this.byId("rbgGRGSTR9").setVisible(true);

			} else if (vId.includes("bGROthers")) {
				this.byId("rbgGROthers").setVisible(true);

			} else if (vId.includes("bewbEWB")) {
				this.byId("rbgewbEWB").setVisible(true);
			}
		},

		bindDefaultData: function () {
			var aFields = [
				"iFromDate", "iToDate", "iFromPeriod", "iToPeriod", "iUploadFromDate", "iUploadToDate", "dtSftpUploadFrom", "dtSftpUploadTo"
			];

			aFields.forEach(function (field) {
				this._setDateProperty(field);
			}.bind(this));

			var oJsonData = {
				"withSAPTotal": "N",
				"apiFilter": false,
				"sftpFilter": false,
				"returnPeriod": false
			};
			this.getView().setModel(new JSONModel(oJsonData), "DataStatus");
			var oProp = {
				"outward": true
			};
			this.getView().setModel(new JSONModel(oProp), "Properties");
		},

		onChangeCriteria: function (oEvent) {
			if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RETURN_DATE_SEARCH") {
				var flag = true;
			} else {
				flag = false;
			}
			var oModel = this.getView().getModel("DataStatus");
			oModel.getData().returnPeriod = flag;
			oModel.refresh(true);
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
				if (sResponse.resp.status === "Success") {
					sap.m.MessageBox.success(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.information(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onPressDownload: function (oEvent) {
			var vIdMain = this.byId("rgReturnType").getSelectedButton().getId();

			if (vIdMain.includes("bIntegrated")) {
				var vId = this.byId("rgIntegrated").getSelectedButton().getId();

			} else if (vIdMain.includes("bGSTReturns")) {
				vId = this.byId("rgGSTReturns").getSelectedButton().getId();

			} else if (vIdMain.includes("bEWB")) {
				vId = this.byId("rgEWB").getSelectedButton().getId();
			}

			if (vId.includes("bIOutward")) {
				var oRadio = this.byId("rbgIOutward").getSelectedIndex();
				switch (oRadio) {
				case 0:
					var aObj = [{
						filePath: "excel/outward/Raw_File_239DFs.xlsx",
						fileName: "Raw_File_239DFs.xlsx"
					}, {
						filePath: "excel/outward/Understanding of Outward 239 DFs Template (Staging file) .xlsx",
						fileName: "Understanding of Outward 239 DFs Template (Staging file) .xlsx"
					}];
					this.downloadZipAll("Outward 239 DFs upload template & Staging file.zip", aObj);
					break;
				}
			} else if (vId.includes("bIGSTR1AOutward")) {
				var oRadio = this.byId("rbgIGSTR1AOutward").getSelectedIndex();
				switch (oRadio) {
				case 0:
					var aObj = [{
						filePath: "excel/outward/Raw_File_239DFs.xlsx",
						fileName: "Raw_File_239DFs.xlsx"
					}, {
						filePath: "excel/outward/Understanding_1A_outward_239_DFs.xlsx",
						fileName: "Understanding_1A of Outward 239 DFs Template (Staging file) .xlsx"
					}];
					this.downloadZipAll("Outward_1A 239 DFs upload template & Staging file.zip", aObj);
					break;

				}
			} else if (vId.includes("bIInward")) {
				var oRadio = this.byId("rbgIInward").getSelectedIndex();
				switch (oRadio) {
				case 0:
					var aObj = [{
						filePath: "excel/inward/Rawfile_240DFs.xlsx",
						fileName: "Rawfile_240DFs.xlsx"
					}, {
						filePath: "excel/inward/Understanding of Inward 240DFs Template (Staging File).xlsx",
						fileName: "Understanding of Inward 240DFs Template (Staging File).xlsx"
					}];
					this.downloadZipAll("Inward 240 DFs upload template & Staging file.zip", aObj);
					break;
				}
			} else if (vId.includes("bGROutward")) {
				oRadio = this.byId("rbgGROutward").getSelectedIndex();
				switch (oRadio) {
					// case 0:
					// 	sap.m.URLHelper.redirect("excel/Raw_File_109DFs.xlsx", false); // Raw File
					// 	break;
				case 0:
					var aObj = [{
						filePath: "excel/B2CS.xlsx",
						fileName: "B2CS.xlsx"
					}, {
						filePath: "excel/stagingFiles/Understanding_of_B2CS_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of B2CS Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("B2CS Form Vertical Upload & Staging File.zip", aObj);
					break;
				case 1:
					aObj = [{
						filePath: "excel/Advance_Received.xlsx",
						fileName: "Advance_Received.xlsx"
					}, {
						filePath: "excel/stagingFiles/Understanding_of_Advance_Received_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of Advance Received Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("Advance Received Vertical Upload & Staging File.zip", aObj);
					break;
				case 2:
					aObj = [{
						filePath: "excel/Advance_Adjusted.xlsx",
						fileName: "Advance_Adjusted.xlsx"
					}, {
						filePath: "excel/stagingFiles/Understanding_of_Advance_Adjusted_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of Advance Adjusted Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("Advance Adjusted Vertical Upload & Staging File.zip", aObj);
					break;
				case 3:
					sap.m.URLHelper.redirect("excel/Invoice_series.xlsx", false); // Invoice Series
					break;
				case 4:
					sap.m.URLHelper.redirect("excel/Nil_Non_Exempt.xlsx", false); // Invoice Series
					break;
				case 5:
					sap.m.URLHelper.redirect("excel/Gstr1EinvoiceReconTemplate.csv", false); // Invoice Recon
					break;
				case 6:
					sap.m.URLHelper.redirect("excel/SAP_Delete_GSTN_Data_Upload_format.xlsx", false); // Invoice Recon
					break;
				case 7:
					sap.m.URLHelper.redirect("excel/SAP_Resposne_Vertical_Upload_File_for_DigiGST_Processed_Data.xlsx", false); // Invoice Recon
					break;
				case 8:
					aObj = [{
						filePath: "excel/SAP_HSN_Upload.xlsx",
						fileName: "SAP_HSN_Upload.xlsx"
					}, {
						filePath: "excel/stagingFiles/Undersatnding_of_HSN_Vertical_Upload_Staging_file.xlsx",
						fileName: "Undersatnding of HSN Vertical Upload (Staging file).xlsx"
					}];
					this.downloadZipAll("HSN Vertical Upload & Staging File.zip", aObj);
					break;
				}
			} else if (vId.includes("bGRGSTR1AOutward")) {
				oRadio = this.byId("rbgGRGSTR1AOutward").getSelectedIndex();
				switch (oRadio) {
					// case 0:
					// 	sap.m.URLHelper.redirect("excel/Raw_File_109DFs.xlsx", false); // Raw File
					// 	break;
				case 0:
					var aObj = [{
						filePath: "excel/B2CS.xlsx",
						fileName: "B2CS.xlsx"
					}, {
						filePath: "excel/stagingFiles_1A/1A_Understanding_of_B2CS_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of B2CS Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("1A_B2CS Form Vertical Upload & Staging File.zip", aObj);
					break;
				case 1:
					aObj = [{
						filePath: "excel/Advance_Received.xlsx",
						fileName: "Advance_Received.xlsx"
					}, {
						filePath: "excel/stagingFiles_1A/1A_Understanding_of_Advance_Received_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of Advance Received Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("1A_Advance Received Vertical Upload & Staging File.zip", aObj);
					break;
				case 2:
					aObj = [{
						filePath: "excel/Advance_Adjusted.xlsx",
						fileName: "Advance_Adjusted.xlsx"
					}, {
						filePath: "excel/stagingFiles_1A/1A_Understanding_of_Advance_Adjusted_Vertical_Upload_Staging_File.xlsx",
						fileName: "Understanding of Advance Adjusted Vertical Upload (Staging File).xlsx"
					}];
					this.downloadZipAll("1A_Advance Adjusted Vertical Upload & Staging File.zip", aObj);
					break;
				case 3:
					sap.m.URLHelper.redirect("excel/Invoice_series.xlsx", false); // Invoice Series
					break;
				case 4:
					sap.m.URLHelper.redirect("excel/Nil_Non_Exempt.xlsx", false); // Nil_Non_Exempt
					break;
					// case 5:
					// 	sap.m.URLHelper.redirect("excel/Gstr1EinvoiceReconTemplate.csv", false); // Invoice Recon
					// 	break;
					// case 6:
					// 	sap.m.URLHelper.redirect("excel/SAP_Delete_GSTN_Data_Upload_format.xlsx", false);
					// 	break;
					// case 7:
					// 	sap.m.URLHelper.redirect("excel/SAP_Resposne_Vertical_Upload_File_for_DigiGST_Processed_Data.xlsx", false); 
					// 	break;
				case 5:
					aObj = [{
						filePath: "excel/SAP_HSN_Upload.xlsx",
						fileName: "SAP_HSN_Upload.xlsx"
					}, {
						filePath: "excel/stagingFiles_1A/1A_Undersatnding_of_HSN_Vertical_Upload_Staging_file.xlsx",
						fileName: "Undersatnding of HSN Vertical Upload (Staging file).xlsx"
					}];
					this.downloadZipAll("1A_HSN Vertical Upload & Staging File.zip", aObj);
					break;
				}
			} else if (vId.includes("bGRInward")) {
				var oRadio = this.byId("rbgGRInward").getSelectedIndex();
				switch (oRadio) {
				case 0:
					sap.m.URLHelper.redirect("excel/Rawfile_115dfs.xlsx", false);
					break;
				case 1:
					sap.m.URLHelper.redirect("excel/GSTR6_DIstribution.xlsx", false);
					break;
				case 2:
					sap.m.URLHelper.redirect("excel/ISD_Auto_Distribution.xlsx", false);
					break;
				case 3:
					sap.m.URLHelper.redirect("excel/Recon_Response_2A6A_vs_PR_202DFs.csv", false);
					break;
				case 4:
					sap.m.URLHelper.redirect("excel/Recon_Response_2A6A_vs_PR_108DFs.csv", false);
					break;
				case 5:
					sap.m.URLHelper.redirect("excel/Recon_Response_2B_PR.csv", false);
					break;
				case 6:
					sap.m.URLHelper.redirect("excel/Cross_ITC.xlsx", false);
					break;
				case 7:
					sap.m.URLHelper.redirect("excel/IMS_Response.xlsx", false);
					break;
				case 8:
					sap.m.URLHelper.redirect("excel/Recon_Response_2BvsPR_IMS_Report.csv", false);
					break;
				case 9:
					sap.m.URLHelper.redirect("excel/Recon_Response_2AvsPR_IMS_Report.csv", false);
					break;
				}
			} else if (vId.includes("bGRGSTR3B")) {
				var oRadio = this.byId("rbgGRGSTR3B").getSelectedIndex();
				switch (oRadio) {
				case 0:
					aObj = [{
						filePath: "excel/GSTR3B_Form.xlsx",
						fileName: "GSTR3B_Form.xlsx"
					}, {
						filePath: "excel/stagingFiles/Understanding_of_GSTR3B_Form_Staging_File.xlsx",
						fileName: "Understanding of GSTR3B Form (Staging File).xlsx"
					}];
					this.downloadZipAll("GSTR3B Form Vertical Upload & Staging File", aObj);
					break;
				case 1:
					// sap.m.URLHelper.redirect("excel/Raw_File_239DFs.xlsx", false);
					break;
				case 2:
					sap.m.URLHelper.redirect("excel/GSTR3B_ITC_Reversal.xlsx", false);
					break;
				case 3:
					sap.m.URLHelper.redirect("excel/180_Days_Payment_Reference.csv", false);
					break;
				case 4:
					sap.m.URLHelper.redirect("excel/180_Days_Reversal_Response.csv", false);
					break;
				}
			} else if (vId.includes("bGRGSTR9")) {
				var oRadio = this.byId("rbgGRGSTR9").getSelectedIndex();
				switch (oRadio) {
				case 0:
					sap.m.URLHelper.redirect("excel/GSTR9_Form.xlsx", false);
					break;
				case 1:
					sap.m.URLHelper.redirect("excel/Gstr9_Hsn.xlsx", false);
					break;
				}
			} else if (vId.includes("bGROthers")) {
				var oRadio = this.byId("rbgGROthers").getSelectedIndex();
				switch (oRadio) {
				case 0:
					sap.m.URLHelper.redirect("excel/GSTR7_TDS.xlsx", false);
					break;
				case 1:
					var aObj = [{
						filePath: "excel/GSTR7_Transactional.xlsx",
						fileName: "GSTR-7_Transactional.xlsx"
					}, {
						filePath: "excel/GSTR7_Transactional_Staging_file.xlsx",
						fileName: "Understanding of GSTR7 Transactional template (staging file).xlsx"
					}];
					this.downloadZipAll("GSTR7 Upload template & Staging file.zip", aObj);
					break;
				case 2:
					var aObj = [{
						filePath: "excel/gstr8/GSTR-8_Download_Template.xlsx",
						fileName: "GSTR 8_Download Template.xlsx"
					}, {
						filePath: "excel/gstr8/Understanding_of_GSTR-8_download_template.xlsx",
						fileName: "Understanding of GSTR 8 download template.xlsx"
					}];
					this.downloadZipAll("GSTR 8 Download Template & Staging file.zip", aObj);
					break;
				case 3:
					sap.m.URLHelper.redirect("excel/ITC04.xlsx", false);
					break;
				case 4:
					sap.m.URLHelper.redirect("excel/GSTR2X_ConsolidatedReport.xlsx", false);
					break;
				case 5:
					sap.m.URLHelper.redirect("excel/GL_Outward_Template.xlsx", false);
					break;
				case 6:
					var aObj = [{
						filePath: "excel/returns/Common_Credit_Reversal_Exceptional_Tagging_Upload_Template.xlsx",
						fileName: "Common Credit Reversal_Exceptional Tagging_Upload Template.xlsx"
					}, {
						filePath: "excel/returns/Understanding_of_Common_Credit_Reversal_Exceptional_Tagging_Upload_Template.xlsx",
						fileName: "Understanding of Common Credit Reversal_Exceptional Tagging_Upload Template.xlsx"
					}];
					this.downloadZipAll("Common Credit Reversal_Exceptional Tagging Upload_2012202315107563.zip", aObj);
					break;
				}
			} else if (vId.includes("bewbEWB")) {
				var oRadio = this.byId("rbgewbEWB").getSelectedIndex();
				switch (oRadio) {
				case 0:
					sap.m.URLHelper.redirect("excel/CEWB.xlsx", false);
					break;
				case 1:
					sap.m.URLHelper.redirect("excel/EWB.xlsx", false);
					break;
				}
			}
		},

		onPressUpload: function (oEvent) {
			var oFileUploader = this.byId("fileUploader");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}

			var vIdMain = this.byId("rgReturnType").getSelectedButton().getId();

			if (vIdMain.includes("bIntegrated")) {
				var vId = this.byId("rgIntegrated").getSelectedButton().getId();

			} else if (vIdMain.includes("bGSTReturns")) {
				vId = this.byId("rgGSTReturns").getSelectedButton().getId();
			} else if (vIdMain.includes("bEWB")) {
				vId = this.byId("rgEWB").getSelectedButton().getId();
			}

			if (vId.includes("bIOutward")) {
				this._uploadIOutward(oFileUploader);

			} else if (vId.includes("bIGSTR1AOutward")) {
				this._uploadIGRGSTR1AOutward(oFileUploader);

			} else if (vId.includes("bIInward")) {
				this._uploadIInward(oFileUploader);

			} else if (vId.includes("bGROutward")) {
				this._uploadGROutward(oFileUploader);

			} else if (vId.includes("bGRGSTR1AOutward")) {
				this._uploadGRGSTR1AOutward(oFileUploader);

			} else if (vId.includes("bGRInward")) {
				this._uploadGRInward(oFileUploader);

			} else if (vId.includes("bGRGSTR3B")) {
				this._uploadGRGSTR3B(oFileUploader);

			} else if (vId.includes("bGRGSTR9")) {
				this._uploadGRGSTR9(oFileUploader);

			} else if (vId.includes("bGROthers")) {
				this._uploadGROthers(oFileUploader);

			} else if (vId.includes("bewbEWB")) {
				this._uploadewbEWB(oFileUploader);
			}
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		// 	Integrated Outward File Upload
		_uploadIOutward: function (oFileUploader) {
			var oRadio = this.byId("rbgIOutward").getSelectedIndex();
			var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
			switch (oRadio) {
			case 0:
				if (this.withTrans && vGR10) {
					var key = this.getView().byId("idRules").getSelectedKey();
					if (key === "") {
						sap.m.MessageBox.show("Please select DMS Rule");
						return;
					}
					oFileUploader.setAdditionalData(key);
				} else {
					oFileUploader.setAdditionalData(null);
				}
				oFileUploader.setUploadUrl("/aspsapapi/webEinvoiceRawUploadDocuments.do");
				break;
			}
		},

		// 	Integrated Outward File Upload
		_uploadIGRGSTR1AOutward: function (oFileUploader) {
			var oRadio = this.byId("rbgIGSTR1AOutward").getSelectedIndex();
			var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
			switch (oRadio) {
			case 0:
				if (this.withTrans && vGR10) {
					var key = this.getView().byId("idRules").getSelectedKey();
					if (key === "") {
						sap.m.MessageBox.show("Please select DMS Rule");
						return;
					}
					oFileUploader.setAdditionalData(key);
				} else {
					oFileUploader.setAdditionalData(null);
				}
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1AEinvoiceRawUploadDocuments.do");

				break;
			}
		},

		// 	Integrated Inward File Upload
		_uploadIInward: function (oFileUploader) {
			var oRadio = this.byId("rbgIInward").getSelectedIndex();
			var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
			switch (oRadio) {
			case 0:
				// oFileUploader.setUploadUrl("/aspsapapi/gstr2UserRespFileUpload.do");
				if (this.withTrans && vGR10) {
					var key = this.getView().byId("idRules").getSelectedKey();
					if (key === "") {
						sap.m.MessageBox.show("Please select DMS Rule");
						return;
					}
					oFileUploader.setAdditionalData(key);
				} else {
					oFileUploader.setAdditionalData(null);
				}
				oFileUploader.setUploadUrl("/aspsapapi/inwardRawfileUploads.do"); // Raw File (240 DFs)

				break;
			}
		},

		// 	GST Returns	Outward File Upload
		_uploadGROutward: function (oFileUploader) {
			var oRadio = this.byId("rbgGROutward").getSelectedIndex();
			switch (oRadio) {
				// case 0:
				// 	oFileUploader.setUploadUrl("/aspsapapi/webGstr1RawUploadDocuments.do"); // Raw File
				// 	break;
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/webB2csUploadDocuments.do"); // B2CS
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/webAtUploadDocuments.do"); // Advance Received
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/webTxpdUploadDocuments.do"); // Advance Adjusted
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/webInvoiceUploadDocuments.do"); // Invoice Series
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/webNilUploadDocuments.do"); // Nil/Non/Exemp
				break;
			case 5:
				oFileUploader.setUploadUrl("/aspsapapi/webEinvoiceReconUploadDocuments.do"); // Einvoice Recon
				// oFileUploader.setUploadUrl("/aspsapapi/webHsnUploadDocuments.do"); // HSN Summary
				break;
			case 6:
				oFileUploader.setUploadUrl("/aspsapapi/gstr1GSTINDeleteData.do"); // GSTN delete data
				break;
			case 7:
				oFileUploader.setUploadUrl("/aspsapapi/gstr1RespFileUpload.do"); // Exclusive save
				break;
			case 8:
				oFileUploader.setUploadUrl("/aspsapapi/webHsnUploadDocuments.do"); // Exclusive save
				break;
			}
		},

		// 	GST Returns	Outward File Upload GSTR-1A
		_uploadGRGSTR1AOutward: function (oFileUploader) {
			var oRadio = this.byId("rbgGRGSTR1AOutward").getSelectedIndex();
			switch (oRadio) {
				// case 0:
				// 	oFileUploader.setUploadUrl("/aspsapapi/webGstr1RawUploadDocuments.do"); // Raw File
				// 	break;
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aB2csUploadDocuments.do"); // B2CS
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aAtUploadDocuments.do"); // Advance Received
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aTxpdUploadDocuments.do"); // Advance Adjusted
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aInvoiceUploadDocuments.do"); // Invoice Series
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aNilUploadDocuments.do"); // Nil/Non/Exemp
				break;
				// case 5:
				// 	oFileUploader.setUploadUrl("/aspsapapi/webGstr1aEinvoiceReconUploadDocuments.do"); // Einvoice Recon
				// 	break;
				// case 6:
				// 	oFileUploader.setUploadUrl("/aspsapapi/gstr1Gstr1aGSTINDeleteData.do"); // GSTN delete data
				// 	break;
				// case 7:
				// 	oFileUploader.setUploadUrl("/aspsapapi/gstr1Gstr1aRespFileUpload.do"); // Exclusive save
				// 	break;
			case 5:
				oFileUploader.setUploadUrl("/aspsapapi/webGstr1aHsnUploadDocuments.do"); // Exclusive save
				break;
			}
		},

		// 	GST Returns	Inward File Upload
		_uploadGRInward: function (oFileUploader) {
			var oRadio = this.byId("rbgGRInward").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/anx2InwardRawFileUploadDocuments.do"); // Raw File (115 DFs)
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/distributionWebUpload.do"); // Distribution (GSTR-6)
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/isddistributionWebUpload.do"); // Recon Response
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/gstr2AResponseUpload.do"); // Recon Response
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/gstr2AERPResponseUpload.do"); // Recon Response
				break;
			case 5:
				oFileUploader.setUploadUrl("/aspsapapi/gstr2bprResponseUpload.do"); // Recon Response
				oFileUploader.setAdditionalData($.sap.entityID);
				break;
			case 6:
				oFileUploader.setUploadUrl("/aspsapapi/crossItcWebUploads.do"); // Cross ITC
				break;
			case 7:
				oFileUploader.setUploadUrl("/aspsapapi/imsResponseUpload.do");
				break;
			case 8:
				oFileUploader.setUploadUrl("/aspsapapi/gstr2bprImsResponseUpload.do");
				oFileUploader.setAdditionalData($.sap.entityID);
				break;
			case 9:
				oFileUploader.setUploadUrl("/aspsapapi/gstr2aprImsResponseUpload.do");
				oFileUploader.setAdditionalData($.sap.entityID);
				break;
			}
		},

		// 	GST Returns	GSTR3B File Upload
		_uploadGRGSTR3B: function (oFileUploader) {
			var oRadio = this.byId("rbgGRGSTR3B").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/gstr3bSummaryReportWebUpload.do");
				break;
			case 1:
				// oFileUploader.setUploadUrl("/aspsapapi/webRawUploadDocuments.do"); //Raw File
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/gstr3bWebUploads.do"); // ITC Reversal Upload
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/revarsal180DaysUpload.do"); // 180 days Reversal Upload Adde by chaithra on 30/3/2021
				oFileUploader.setAdditionalData($.sap.entityID);
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/reversal180DaysResponseUpload.do"); // 180 days Reversal response upload added by chaithra on 10/6/2021
				break;
			}
		},

		// 	GST Returns	GSTR3B File Upload
		_uploadGRGSTR9: function (oFileUploader) {
			var oRadio = this.byId("rbgGRGSTR9").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/gstr9InwardOutwardWebUploads.do");
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/gstr9HsnFileUploadDocuments.do");
				break;
			}
		},

		// 	GST Returns	Others File Upload
		_uploadGROthers: function (oFileUploader) {
			var oRadio = this.byId("rbgGROthers").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/gstr7FileUploadDocuments.do");
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/gstr7TransFileUploadDocuments.do");
				break;
			case 2:
				oFileUploader.setUploadUrl("/aspsapapi/gstr8FileUpload.do");
				break;
			case 3:
				oFileUploader.setUploadUrl("/aspsapapi/itc04WebUploads.do");
				break;
			case 4:
				oFileUploader.setUploadUrl("/aspsapapi/tcsAndTdsWebUploads.do");
				break;
			case 5:
				oFileUploader.setUploadUrl("/aspsapapi/glReconFileUpload.do");
				break;
			case 6:
				oFileUploader.setUploadUrl("/aspsapapi/commonCreditReversalWebUpload.do"); // Common Credit Reversal - Exceptional Tagging
				break;
			}
		},

		// 	GST Returns	Outward File Upload
		_uploadewbEWB: function (oFileUploader) {
			var oRadio = this.byId("rbgewbEWB").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/aspsapapi/cewbWebUploads.do");
				break;
			case 1:
				oFileUploader.setUploadUrl("/aspsapapi/ewb3WayReconUpload.do"); //Raw File
				break;
			}
		},

		onPressAdaptFilter: function () {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.dataStatusNew.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			// if (this.byId("slStatsDataType").getSelectedKey() === "outward") {
			// 	this.byId("idsalesOrg").setVisible(true);
			// 	this.byId("idpurchOrg").setVisible(false);
			// 	this.byId("iddistChannel").setVisible(true);
			// } else {
			// 	this.byId("idsalesOrg").setVisible(false);
			// 	this.byId("idpurchOrg").setVisible(true);
			// 	this.byId("iddistChannel").setVisible(false);
			// }
			this._oAdpatFilter.open();
		},

		onChangeReturnType: function (oEvent, btn) {
			var vStatsReturnType = oEvent.getSource().getSelectedKey();
			if (btn === "file") {
				if (vStatsReturnType === "integrated") {
					var oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataTypeNew.integrated,
						vKey = "outward";
				} else if (vStatsReturnType === "EWB") {
					oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataTypeNew.EWB;
					vKey = "EWB";
				} else {
					oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataTypeNew.gstReturns;
					vKey = "gstr1";
				}
				var oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileTypeNew,
					oFileModel = this.byId("slStatsFileType").getModel("FileType");

				oFileModel.setData(oFileType[vKey]);
				oFileModel.refresh(true);

				this.byId("slFileDataType").setModel(new JSONModel(oDataType), "DataType");
				this.byId("slStatsFileType").setSelectedKey(oFileType[vKey][0].key);
			} else {
				if (vStatsReturnType === "integrated") {
					oDataType = this.getOwnerComponent().getModel("DropDown").getData().sftpDataType.integrated;
					vKey = "outward";
				} else {
					oDataType = this.getOwnerComponent().getModel("DropDown").getData().sftpDataType.gstReturns;
					vKey = "inward";
				}
				var oSftpFileType = this.getOwnerComponent().getModel("DropDown").getData().sftpFileType,
					oSftpModel = this.byId("slSftpFileType").getModel("FileType");

				oSftpModel.setData(oSftpFileType[vKey]);
				oSftpModel.refresh(true);

				this.byId("slSftpDataType").setModel(new JSONModel(oDataType), "DataType");
				this.byId("slSftpFileType").setSelectedKey(oFileType[vKey][0].key);
			}
		},

		onSelectChange: function (oEvent) {
			var oDropModel = this.getOwnerComponent().getModel("DropDown"),
				oCriteria = oDropModel.getProperty("/criteria"),
				vKey = oEvent.getSource().getSelectedKey();

			if (oEvent.getSource().getId().includes("slStatsDataType")) {
				var oModel = this.getView().getModel("Properties"),
					oData = oModel.getData();
				oData.outward = (vKey === "outward" ? true : false);
				oModel.refresh();

				if (["outward"].includes(vKey)) {
					this.getAllGSTIN('O');
					oCriteria[1].enabled = oCriteria[2].enabled = oCriteria[3].enabled = true;

				} else if (["ITC-04", "vendor_payment", "ims", "gstr7txn"].includes(vKey)) {
					if (vKey === "gstr7txn") {
						this.getAllGSTIN('TDS');
					} else {
						this.getAllGSTIN('O');
					}
					oCriteria[1].enabled = oCriteria[2].enabled = oCriteria[3].enabled = false;
					this.getView().byId("slStatsCriteria").setSelectedKey("RECEIVED_DATE_SEARCH");
					this.onChangeCriteria();

				} else if (vKey === "gstr8") {
					this.getAllGSTIN('G8');
					oCriteria[1].enabled = oCriteria[2].enabled = oCriteria[3].enabled = false;
				} else {
					this.getAllGSTIN('I');
					oCriteria[1].enabled = oCriteria[2].enabled = oCriteria[3].enabled = true;
				}
			} else if (oEvent.getSource().getId().includes("slFileDataType")) {
				var oFileType = this.getOwnerComponent().getModel("DropDown").getProperty("/fileTypeNew"),
					oFileModel = this.byId("slStatsFileType").getModel("FileType");

				oFileModel.setData(oFileType[vKey]);
				oFileModel.refresh(true);

			} else if (oEvent.getSource().getId().includes("slSftpDataType")) {
				var oFileType = this.getOwnerComponent().getModel("DropDown").getProperty("/sftpFileType"),
					oFileModel = this.byId("slSftpFileType").getModel("FileType");

				oFileModel.setData(oFileType[vKey]);
				oFileModel.refresh(true);
			}
			this.getView().byId("slStatsGSTIN").setSelectedKeys([]);
			this.getView().setModel(new JSONModel({
				"show": (vKey !== "gstr8")
			}), "ViewProperty");
		},

		_getFileStatusSearchInfo: function () {
			var vDataType = this.getView().byId("slFileDataType").getSelectedKey(),
				vFileType = this.getView().byId("slStatsFileType").getSelectedKey(),
				vFromdate = this.getView().byId("iUploadFromDate").getValue(),
				vTodate = this.getView().byId("iUploadToDate").getValue();

			if (vFromdate === "") {
				vFromdate = null;
			}
			if (vTodate === "") {
				vTodate = null;
			}
			var searchInfo = {
				"req": {
					"dataRecvFrom": vFromdate,
					"dataRecvTo": vTodate,
					"entityId": [$.sap.entityID],
					"fileType": vFileType,
					"dataType": vDataType
				}
			};

			if (this.byId("slFileDataType").getSelectedKey() === "others") {
				searchInfo.req.dataType = "RET";
			}
			if (searchInfo.req.dataType === "gstr1" && searchInfo.req.fileType === "COMPREHENSIVE_RAW") {
				searchInfo.req.dataType = "outward";
			}
			if (searchInfo.req.dataType === "inward1") {
				searchInfo.req.dataType = "inward";
			}
			return searchInfo;
		},

		_getFileStauts: function () {
			var that = this,
				vKey = that.byId("slStatsFileType").getSelectedKey();
			/// added by Vinay on 09-12-2019 ///
			if (vKey === "raw" || vKey === "COMPREHENSIVE_RAW") {
				//var flag = true;
				that.byId("tabData").setVisible(true);
				// that.byId("tabVerFileStatus").setVisible(false);
				// that.byId("tabRecFileStatus").setVisible(false);
			} else if (vKey === "reconResponse") {
				//flag = false;
				that.byId("tabData").setVisible(false);
				// that.byId("tabVerFileStatus").setVisible(false);
				// that.byId("tabRecFileStatus").setVisible(true);
				this.reconTable();
				return;
			} else {
				that.byId("tabData").setVisible(false);
				// that.byId("tabVerFileStatus").setVisible(true);
				// that.byId("tabRecFileStatus").setVisible(false);
			}

			var searchInfo = this._getFileStatusSearchInfo();
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.byId("tabData").getModel("FileStatus");
			// var oModel1 = this.byId("tabDatasftp3").getModel("FileStatus");
			if (oModel) {
				oModel.setData(null);
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/newFileStatus.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();

				// vKey = that.byId("slStatsFileType").getSelectedKey();
				// if (vKey === "raw" || vKey === "COMPREHENSIVE_RAW") {
				// 	that.byId("tabData").setModel(new JSONModel(data.resp), "FileStatus");
				// 	// that.byId("tabDatasftp3").setModel(new JSONModel(data.resp), "FileStatus");
				// } else {
				// 	// that.byId("tabVerFileStatus").setModel(new JSONModel(data.resp), "FileStatus");
				// }
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_getOtherFiltersASP: function (search) {
			var oDataSecurity = this.getView().getModel("userPermission").getData().respData.dataSecurity.items,
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

		_getPayloadStruct: function () {
			return {
				"req": {
					"dataType": "",
					"entityId": [$.sap.entityID],
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"documentDateTo": null,
					"documentDateFrom": null,
					"accVoucherDateFrom": null,
					"accVoucherDateTo": null,
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
		},

		// please add for tabSummaryAPI table visibility
		onPressDetail2: function () {
			this.getView().byId("tabStatusTotal").setVisible(false);
			this.getView().byId("tabStatusAPI").setVisible(true);

			this.getView().byId("dpDataStatus").setVisible(false);
			this.getView().byId("dpSummayAPI").setVisible(true);
			this.getView().byId("txtApiSumm").setText("API Summary");
		},

		ViewSummaryDatePress: function () {
			this.getView().byId("tabStatusTotal").setVisible(true);
			this.getView().byId("tabStatusAPI").setVisible(false);

			this.getView().byId("dpDataStatus").setVisible(false);
			this.getView().byId("dpSummayAPI").setVisible(true);

			this.getView().byId("tabStatusTotal").setVisible(true);
			this.getView().byId("tabStatusAPI").setVisible(false);
			this.getView().byId("txtApiSumm").setText("File Status Summary");
			// this.getView().byId("tabRecFileStatus").setVisible(false);
			// this.getView().byId("idViewSumm").setVisible(true);
		},

		/**
		 * Called when user click on Recieved Date Link to see summary on that particular date
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent	Eventing object
		 * @param {string} date		Selected Date
		 * @param {number} count	Process Active record count
		 */
		onPressDetail: function (oEvent, date, count) {
			this.visiCons = false;
			var aDate = [date];
			if (count === 0) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgProcess"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			this._getDataSummary(aDate);
			this.byId("tabApiSummary").setVisibleRowCount(15);
		},

		/**
		 * Called when user click on Summary button after selecting Recieved date(s) in Data Status api table
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		onPressSummary: function () {
			this.visiCons = true;
			var oBundle = this.getResourceBundle(),
				// vCriteria = this.byId("slStatsCriteria").getSelectedKey(),
				aIndices = this.byId("tabDataAPI").getSelectedIndices(),
				oData = this.byId("tabDataAPI").getModel("DataStatusAPI").getData().resp,
				aDate = [];
			if (aIndices.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgSelectOneRecord"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndices.length; i++) {
				if (oData[aIndices[i]].processeActive > 0) {
					if (oData[aIndices[i]].date !== undefined) {
						aDate.push(oData[aIndices[i]].date);
					}
				}
			}
			if (aDate.length === 0) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgProcess"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			// aDate.splice(0, 1);
			this._getDataSummary(aDate);
			this.byId("tabApiSummary").setVisibleRowCount(10);
		},

		/**
		 * Ajax call to display summary of particular recieved date(s)
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} aDate List of selected date(s)
		 */
		_getDataSummary: function (aDate) {
			var oModel = this.byId("tabStatusTotal").getModel("Summary"),
				searchInfo = this._getApiSearchInfo(),
				that = this;

			searchInfo.req.dates = aDate;
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs);
			}
			sap.ui.core.BusyIndicator.show(0);
			this.byId("tabStatusTotal").setModel(new JSONModel(searchInfo), "DsPayload");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getDataStatusApiSummary.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpDataStatus").setVisible(false); // Data Status Page
				that.byId("dpSummayAPI").setVisible(true); // Summary Page

				that.getView().byId("tabStatusTotal").setVisible(true); // Data Status API Summary Aggregate Table
				that.getView().byId("tabApiSummary").setVisible(true); // Data Status API Summary Details Table
				that.getView().byId("tabStatusAPI").setVisible(false); // File Summary Aggregate Table
				that.getView().byId("tabFileSummary").setVisible(false); // File Summary Details Table

				// Setting Page Header for Summary Page
				if (that.byId("slStatsDataType").getSelectedKey() === "outward") {
					that.byId("txtApiSumm").setText("API Outward Summary");
				} else {
					that.byId("txtApiSumm").setText("API Inward Summary");
				}
				if (data.hdr.status === "E") {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
					// setting null data for Aggregate and Details tables
					that.byId("tabStatusTotal").setModel(new JSONModel(), "Summary");
					that.byId("tabApiSummary").setModel(new JSONModel(), "Summary");
				} else {
					that._bindSummaryData(data.resp, searchInfo.req);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				sap.m.MessageBox.error("error : getDataStatusApiSummary.do", {
					styleClass: "sapUiSizeCompact"
				});
			});
		},

		/**
		 * Converting and Binding Data in JSON format
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} data Response data
		 * @param {Object} payload Payload object
		 */
		_bindSummaryData: function (data, payload) {
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
				oConsData.date = "Total";
				oConsData.flag = 0;
				aSummaryTot.push(oConsData);
				this.byId("tabStatusTotal").setVisible(false);
			}
			this.byId("tabApiSummary").setVisible(true);
			this.byId("tabStatusTotal").setModel(new JSONModel(aSummaryTot), "Summary");
			this.byId("tabApiSummary").setModel(new JSONModel(aSummary), "SummaryAPI");
		},

		_getApiSearchInfo: function () {
			var aGstin = this.byId("slStatsGSTIN").getSelectedKeys(),
				searchInfo = {
					"req": {
						"dataType": this.byId("slStatsDataType").getSelectedKey(),
						"entityId": [$.sap.entityID], //aEntity,
						"dataRecvFrom": null,
						"dataRecvTo": null,
						"docDateFrom": null,
						"docDateTo": null,
						"taxPeriodFrom": null,
						"taxPeriodTo": null,
						"documentDateTo": null,
						"documentDateFrom": null,
						"accVoucherDateFrom": null,
						"accVoucherDateTo": null,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			var vCriteria = this.byId("slStatsCriteria").getSelectedKey();
			if (vCriteria === "RECEIVED_DATE_SEARCH") {
				searchInfo.req.dataRecvFrom = this.byId("iFromDate").getValue();
				searchInfo.req.dataRecvTo = this.byId("iToDate").getValue();

			} else if (vCriteria === "DocDate") {
				searchInfo.req.documentDateFrom = this.byId("iFromDate").getValue();
				searchInfo.req.documentDateTo = this.byId("iToDate").getValue();
			} else if (vCriteria === "AccVoucherDate") {
				searchInfo.req.accVoucherDateFrom = this.byId("iFromDate").getValue();
				searchInfo.req.accVoucherDateTo = this.byId("iToDate").getValue();
			} else {
				searchInfo.req.taxPeriodFrom = this.byId("iFromPeriod").getValue();
				searchInfo.req.taxPeriodTo = this.byId("iToPeriod").getValue();
			}
			return searchInfo;
		},

		/**
		 * Called when user click on Upload Date Link to see summary of File
		 * Develped by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {number} count File Process Count
		 */
		onPressFileDetail: function (oEvent, count) {
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

			if (count === 0) {
				sap.m.MessageBox.information(this.getResourceBundle().getText("msgNoFileData"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			this._fileSummary(oPayload);
			this.byId("tabFileSummary").setVisibleRowCount(15);
		},

		onPressViewCompl: function (oEvent) {
			var vTabData = this.getView().byId("tabData").getSelectedIndices(),
				oData = this.getView().getModel("FileStatus").getData().resp;

			this.visiCons = true;

			if (vTabData.length >= 0) {
				this.getView().byId("dpDataStatus").setVisible(false);
				this.getView().byId("dpSummayAPI").setVisible(true);
				this.getView().byId("txtApiSumm").setText("File Status Summary");
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"dataType": oData[0].dataType,
						"fileType": oData[0].fileType,
						"fileId": []
					}
				};

				for (var i = 0; i < vTabData.length; i++) {
					oPayload.req.fileId.push(oData[vTabData[i]].id);
				}
				this._fileSummary(oPayload);
				this.byId("tabFileSummary").setVisibleRowCount(10);
			} else {
				MessageBox.information("Please Select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onPressFileDetailsftp: function (oEvent) {
			this.visiCons = false;
			var obj = oEvent.getSource().getBindingContext("SftpStatus").getObject(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"dataType": obj.dataType,
						"fileType": obj.fileType,
						"fileId": [obj.id]
					}
				};
			this._fileSummary(oPayload);
			this.byId("tabFileSummary").setVisibleRowCount(15);
		},

		onPressViewComplsftp: function (oEvent) {
			var vTabData = this.getView().byId("tabSftpData").getSelectedIndices(),
				oData = this.getView().getModel("SftpStatus").getData();

			this.visiCons = true;
			if (vTabData.length >= 0) {
				// this.getView().byId("tabStatusTotal").setVisible(true);
				// this.getView().byId("tabStatusAPI").setVisible(false);

				this.getView().byId("dpDataStatus").setVisible(false);
				this.getView().byId("dpSummayAPI").setVisible(true);
				this.getView().byId("txtApiSumm").setText("SFTP Status Summary");
				var oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"dataType": oData[0].dataType,
						"fileType": oData[0].fileType,
						"fileId": []
					}
				};

				for (var i = 0; i < vTabData.length; i++) {
					oPayload.req.fileId.push(oData[vTabData[i]].id);
				}
				this._fileSummary(oPayload);
				this.byId("tabFileSummary").setVisibleRowCount(10);
			} else {
				MessageBox.information("Please Select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		/**
		 * Method called to Generate OTP confirmation popup
		 * Developed by: Bharat Gupta - 26.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {string} gstin	 Gstin
		 * @Param {string} authToken Auth Token
		 * @param {string} origin	 Callfunction Origin
		 */
		onActivateAuthToken: function (gstin, authToken, origin) {
			if (authToken === "Inactive") {
				this.callback = origin;
				this.confirmGenerateOTP(gstin);
			}
		},

		/**
		 * Method called to Refresh Data after activation of Auth Token
		 * Developed by: Bharat Gupta - 26.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 */
		refreshData: function () {
			if (this.callback === "D") {
				var oData = this.byId("tabStatusTotal").getModel("DsPayload").getData();
				this._getDataSummary(oData.req.dates);
			} else {
				var oPayload = this.byId("tabStatusAPI").getModel("FsPayload").getData();
				this._fileSummary(oPayload);
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
		 * Api called to get Summary of File(s)
		 * Develped by: Bharat Gupta on 07.02.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} payload Payload object
		 */
		_fileSummary: function (payload) {
			var oModel = this.byId("tabStatusAPI").getModel("Summary"),
				that = this;

			sap.ui.core.BusyIndicator.show(0);
			that.byId("txtApiSumm").setText("File Summary");
			that.byId("tabStatusAPI").setModel(new JSONModel(payload), "FsPayload");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getNewDataStatusFileSummary.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpDataStatus").setVisible(false);
				that.byId("dpSummayAPI").setVisible(true);

				that.getView().byId("tabStatusTotal").setVisible(false); // Data Status API Aggregate table
				that.getView().byId("tabApiSummary").setVisible(false); // Data Status API Details table
				that.getView().byId("tabStatusAPI").setVisible(false); // File Summary Aggregate Table
				that.getView().byId("tabFileSummary").setVisible(true); // File Summary Details Table

				that.getView().byId("txtApiSumm").setText("File Status Summary");

				if (data.hdr.status === "E") {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
					that.byId("tabStatusAPI").setModel(new JSONModel(), "Summary");
				} else {
					that._bindFileSummary(payload.req, data.resp);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				sap.m.MessageBox.error("error : getDataStatusFileSummary.do", {
					styleClass: "sapUiSizeCompact"
				});
			});
		},

		/**
		 * Method called to bind file summary
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} payload Payload object
		 * @param {Object} data Response data
		 */
		_bindFileSummary: function (payload, data) {
			var oFileTotal = [],
				oAggrFile = {
					"fileName": "Total",
					"taxableValue": 0,
					"totalTaxes": 0,
					"count": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0,
					"items": []
				};
			for (var i = 0, l = data.length; i < l; i++) {
				if (data[i].fileName) {
					this._calculateFileTotal(data[i]);
					if (this.visiCons) {
						this._calculateFileSumaryTotal(data[i], oAggrFile);
					}
				}
			}
			if (this.visiCons) {
				this._calculateAggregate(oAggrFile);
				oFileTotal.push(oAggrFile);
				this.getView().byId("tabStatusAPI").setVisible(true);
				this.byId("tabStatusAPI").setModel(new JSONModel(oFileTotal), "Summary");
			}
			this.byId("tabFileSummary").setModel(new JSONModel(data), "SummaryFile");
		},

		/**
		 * Method called to calculate Aggregate File Summary
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} data Response data
		 * @param {Object} fileTotal Aggregate object
		 */
		_calculateFileSumaryTotal: function (data, fileTotal) {
			for (var i = 0, p = data.items.length; i < p; i++) {
				for (var j = 0, q = fileTotal.items.length; j < q; j++) {
					if (fileTotal.items[j].gstin === data.items[i].gstin && fileTotal.items[j].returnPeriod === data.items[i].returnPeriod) {
						this._calFileTotal(data.items[i], fileTotal.items[j]);
						break;
					}
				}
				if (j === q) {
					fileTotal.items.push(data.items[i]);
				}
			}
		},

		/**
		 * Method called to calculate Items value
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} data Response data
		 * @param {Object} fileTotal Aggregate object
		 */
		_calFileTotal: function (data, fileTotal) {
			for (var i = 0, p = data.items.length; i < p; i++) {
				for (var j = 0, q = fileTotal.items.length; j < q; j++) {
					if (data.items[i].returnType === fileTotal.items[j].returnType && data.items[i].returnSection === fileTotal.items[j].returnSection) {
						this._totalFileSummary(fileTotal, data.items[i]);
						this._totalFileSummary(fileTotal.items[j], data.items[i]);
						break;
					}
				}
				if (j === q) {
					this._totalFileSummary(fileTotal, data.items[i]);
					fileTotal.items.push(data.items[i]);
				}
			}
		},

		/**
		 * Method called to calculate total value for Aggregate file summary
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} obj Aggregate object
		 * @param {Object} data Response data
		 */
		_totalFileSummary: function (obj, data) {
			obj.taxableValue += data.taxableValue;
			obj.totalTaxes += data.totalTaxes;
			obj.count += data.count;
			obj.igst += data.igst;
			obj.cgst += data.cgst;
			obj.sgst += data.sgst;
			obj.cess += data.cess;
		},

		/**
		 * Method called to calculate File Total
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} data Response data
		 */
		_calculateFileTotal: function (data) {
			for (var i = 0, l = data.items.length; i < l; i++) {
				data.count = (data.count || 0) + data.items[i].count;
				data.taxableValue = (data.taxableValue || 0) + data.items[i].taxableValue;
				data.totalTaxes = (data.totalTaxes || 0) + data.items[i].totalTaxes;
				data.igst = (data.igst || 0) + data.items[i].igst;
				data.cess = (data.cess || 0) + data.items[i].cess;
				data.sgst = (data.sgst || 0) + data.items[i].sgst;
				data.cgst = (data.cgst || 0) + data.items[i].cgst;
			}
		},

		/**
		 * Method called to calculate Aggregate file summary
		 * Developed by: Bharat Gupta - 30.11.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @param {Object} fileAggr Aggregate file object
		 */
		_calculateAggregate: function (fileAggr) {
			for (var i = 0, l = fileAggr.items.length; i < l; i++) {
				fileAggr.taxableValue += fileAggr.items[i].taxableValue;
				fileAggr.totalTaxes += fileAggr.items[i].totalTaxes;
				fileAggr.count += fileAggr.items[i].count;
				fileAggr.igst += fileAggr.items[i].igst;
				fileAggr.cgst += fileAggr.items[i].cgst;
				fileAggr.sgst += fileAggr.items[i].sgst;
				fileAggr.cess += fileAggr.items[i].cess;
			}
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
					if ((oConsData.items[j].gstin === (data.GSTIN || data.gstin)) && (oConsData.items[j].returnPeriod === data.returnPeriod)) {
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
		 * Called when user click download button in API Screen to download data in excel file
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent	Eventing parameter
		 * @param {numbet} count	Record count
		 * @param {string} type 	Record Type
		 * @param {string} status	Record Status
		 */
		onPressReportDownload: function (oEvent, count, type, status) {
			var vType, vStatus,
				vText = oEvent.getSource().getDependents()[0].getText(),
				obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("DataStatusAPI").getObject();

			if (count === 0 || !count) {
				sap.m.MessageBox.information("No data to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			var aGstin = this.getView().byId("slStatsGSTIN").getSelectedKeys();
			var aData = {
				"req": {
					"dataType": this.getView().byId("slStatsDataType").getSelectedKey(),
					"entityId": [$.sap.entityID], // aEntity,
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"documentDateFrom": null,
					"documentDateTo": null,
					"accVoucherDateFrom": null,
					"accVoucherDateTo": null,
					"status": status,
					"type": type,
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};
			if (obj.date !== "" && obj.date !== undefined) {
				aData.req.dataRecvFrom = obj.date;
				aData.req.dataRecvTo = obj.date;
				if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RETURN_DATE_SEARCH") {
					aData.req.taxPeriodFrom = this.byId("iFromPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getDateValue());
					aData.req.taxPeriodTo = this.byId("iToPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getSecondDateValue());
				} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "DocDate") {
					aData.req.documentDateFrom = this.byId("iFromDate").getValue();
					aData.req.documentDateTo = this.byId("iToDate").getValue();
				} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "AccVoucherDate") {
					aData.req.accVoucherDateFrom = this.byId("iFromDate").getValue();
					aData.req.accVoucherDateTo = this.byId("iToDate").getValue();
				}

			} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RECEIVED_DATE_SEARCH") {
				aData.req.dataRecvFrom = this.byId("iFromDate").getValue();
				aData.req.dataRecvTo = this.byId("iToDate").getValue();
			} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "DocDate") {
				aData.req.documentDateFrom = this.byId("iFromDate").getValue();
				aData.req.documentDateTo = this.byId("iToDate").getValue();
			} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "AccVoucherDate") {
				aData.req.accVoucherDateFrom = this.byId("iFromDate").getValue();
				aData.req.accVoucherDateTo = this.byId("iToDate").getValue();
			} else {
				aData.req.taxPeriodFrom = this.byId("iFromPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getDateValue());
				aData.req.taxPeriodTo = this.byId("iToPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getSecondDateValue());
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(aData.req.dataSecAttrs);
			}

			if (this.getView().byId("slStatsDataType").getSelectedKey() == "ITC-04") {
				// sap.m.MessageBox.information("Development in progress", {
				// 	styleClass: "sapUiSizeCompact"
				// });
				// return;
				var vUrl = "/aspsapapi/itc04DataStatusReports.do";
				aData.req.reportCateg = "DataStatus";
				aData.req.dataType = "ITC04";
				this.excelDownload(aData, vUrl);
			} else {
				var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do";
				aData.req.reportCateg = "DataStatus";
				this.reportDownload(aData, vUrl);
			}

		},

		/**
		 * Called when user click download button in API Screen to download SFTP Upload data in excel file
		 * Developed by: Bharat Gupta on 23.04.2020
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent	Eventing parameter
		 * @param {numbet} count	Record count
		 * @param {string} type 	Record Type
		 * @param {string} status	Record Status
		 */
		onDownloadSftpReport: function (oEvent, count, type, status) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("SftpStatus").getObject();
			if (count === 0 || !count) {
				sap.m.MessageBox.information("No data available to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (obj.dataType === "INWARD") {
				var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do";
				var aData = {
					"req": {
						"fileId": obj.id,
						"type": type,
						"dataType": "Inward",
						"reportCateg": "FileStatus",
						"status": status
					}
				};
				this.reportDownload(aData, vUrl);

			} else if (obj.fileType === "ITC04_FILE") {
				var vUrl = "/aspsapapi/downloadITC04Reports.do";
				if (type === "errorBv") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": status
						}
					};
				} else if (type === "errorSv") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": "errorSv"
						}
					};
				} else if (type === "errorTotal") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": "errorTotal"
						}
					};
				} else {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"status": status
						}
					};
				}
				this.excelDownload(aData, vUrl);
			} else {
				var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do";
				var aData = {
					"req": {
						"fileId": obj.id,
						"type": type,
						"dataType": obj.dataType,
						"reportCateg": "SFTP",
						"status": status
					}
				};
				this.reportDownload(aData, vUrl);
			}
		},

		onPressFileStatusDownload: function (oEvent, count, type, status) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("FileStatus").getObject();
			if (count === 0 || !count) {
				sap.m.MessageBox.information("No data available to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (obj.dataType === "INWARD") {
				var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do",
					aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"dataType": "Inward",
							"reportCateg": "FileStatus",
							"status": status
						}
					};
				this.reportDownload(aData, vUrl);

			} else if (obj.fileType === "ITC04_FILE") {
				var vUrl = "/aspsapapi/downloadITC04Reports.do";
				if (type === "errorBv") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": status
						}
					};
				} else if (type === "errorSv") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": "errorSv"
						}
					};
				} else if (type === "errorTotal") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": "error",
							"errorType": "errorTotal"
						}
					};
				} else {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"status": status
						}
					};
				}
				this.excelDownload(aData, vUrl);
			} else {
				if (status == "EWB") {
					var vUrl = "/aspsapapi/downloadOutwardFileStatusEwbCsvReports.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": (obj.dataType === "OUTWARD_1A" ? "Outward_1A" : "Outward"),
							"status": null,
						}
					};
				} else if (status == "EINV") {
					var vUrl = "/aspsapapi/downloadOutwardFileStatusEinvCsvReports.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": (obj.dataType === "OUTWARD_1A" ? "Outward_1A" : "Outward"),
							"status": null,
						}
					};
				} else if (status == "RET") {
					var vUrl = "/aspsapapi/downloadOutwardFileStatusRetCsvReports.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": (obj.dataType === "OUTWARD_1A" ? "Outward_1A" : "Outward"),
							"status": null,
						}
					};
				} else {
					var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": (obj.dataType === "OUTWARD_1A" ? "Outward_1A" : "Outward"),
							"status": status
						}
					};
				}
				this.reportDownload(aData, vUrl);
			}
		},

		onPressStatsLink: function (oEvent, navType, type, count, errType) {
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
			this.getRouter().navTo("InvManageNew");
		},

		_apiFilterData: function (oEvent, type, errType) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("DataStatusAPI").getObject(),
				aGstin = this.getView().byId("slStatsGSTIN").getSelectedKeys();

			var aData = {
				"req": {
					"dataType": this.getView().byId("slStatsDataType").getSelectedKey(),
					"criteria": "recDate",
					"entityId": [$.sap.entityID], // aEntity,
					"dataRecvFrom": null,
					"dataRecvTo": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"type": type,
					"errType": errType,
					"dateFlag": false,
					"navType": "DS",
					"dataSecAttrs": {
						"GSTIN": aGstin.includes("All") ? [] : aGstin
					}
				}
			};
			if (obj.date !== "" && obj.date !== undefined) {
				aData.req.dataRecvFrom = obj.date;
				aData.req.dataRecvTo = obj.date;
				if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RETURN_DATE_SEARCH") {
					aData.req.criteria = "recPeriod";
					aData.req.taxPeriodFrom = this.byId("iFromPeriod").getValue();
					aData.req.taxPeriodTo = this.byId("iToPeriod").getValue();
					aData.req.dateFlag = true;
				}

			} else if (this.getView().byId("slStatsCriteria").getSelectedKey() === "RECEIVED_DATE_SEARCH") {
				aData.req.criteria = "recDate";
				aData.req.dataRecvFrom = this.byId("iFromDate").getValue(); //this._formatDate(this.byId("drsStatus").getDateValue());
				aData.req.dataRecvTo = this.byId("iToDate").getValue(); //this._formatDate(this.byId("drsStatus").getSecondDateValue());

			} else {
				aData.req.criteria = "recPeriod";
				aData.req.taxPeriodFrom = this.byId("iFromPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getDateValue());
				aData.req.taxPeriodTo = this.byId("iToPeriod").getValue(); // this._formatPeriod(this.byId("drsPeriod").getSecondDateValue());
			}
			if (this.byId("dAdapt")) {
				this._getOtherFiltersASP(aData.req.dataSecAttrs);
			}
			return aData;
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
			var obj = oEvent.getSource().getBindingContext("FileStatus").getObject(),
				oFilterData = {
					"req": {
						"dataOriginType": "E",
						"segment": "asp",
						"navType": "File"
					}
				};

			oFilterData.req.status = type;
			oFilterData.req.type = type;
			if (errType) {
				oFilterData.req.validation = errType;
			}
			oFilterData.req.fileId = obj.id;
			oFilterData.req.dataType = obj.dataType.toLocaleLowerCase();
			oFilterData.req.dataRecvFrom = oFilterData.toDate = new Date(obj.uploadedOn);
			oFilterData.req.dataRecvTo = oFilterData.toDate = new Date(obj.uploadedOn);
			return oFilterData;
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
			var obj = oEvent.getSource().getBindingContext("FileStatus").getObject(),
				sel = this.getView().byId("slStatsReturnType").getSelectedKey();

			if (count === 0) {
				sap.m.MessageBox.information("No data to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (sel === "gstReturns") { // Code by Bharat Gupta on 17.04.2020
				if (["RET1AND1A", "INTEREST", "SETOFFANDUTIL", "REFUNDS"].includes(obj.fileType)) {
					var vUrl = "/aspsapapi/downloadRet1And1AVerticalReports.do";

				} else if (obj.fileType.toUpperCase() === "DISTRIBUTION") { // Added for GSTR-6 Distribution: Bharat Gupta - 05.05.2020
					vUrl = "/aspsapapi/downloaddistributionReports.do";

				} else if (obj.fileType.toUpperCase() === "ISD") { // Added for GSTR-6 Distribution: Bharat Gupta - 05.05.2020
					vUrl = "/aspsapapi/downloadIsdFileReports.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": "ISD",
							"status": status
						}
					};
					this.reportDownload(aData, vUrl);
					return;
				} else if (obj.fileType === "RECON_RESPONSE") { // Added for Recon REsponse: vinay kodam - 11.05.2020
					if (type === "totalrecords") {
						vUrl = "/aspsapapi/gstrUserResponse2ReportsDownload.do";
					} else {
						vUrl = "/aspsapapi/gstr2APRAutoRespReportGenerate.do";
						var aData1 = {
							"req": {
								"type": type,
								"fileId": obj.id
							}
						};
						this.reportDownload(aData1, vUrl);
						return;
					}
				} else if (obj.fileType === "RECON_RESPONSE_ERP") { // Added for Recon REsponse: vinay kodam - 11.05.2020
					if (type === "totalrecords") {
						vUrl = "/aspsapapi/gstrUserResponse2ReportsDownload.do";
					} else {
						vUrl = "/aspsapapi/gstr2ASftpRespReportGenerate.do";

						var aData1 = {
							"req": {
								"type": type,
								"fileId": obj.id
							}
						};
						this.reportDownload(aData1, vUrl);
						return;
					}
				} else if (obj.fileType === "2BPR_RECON_RESPONSE") { // Added for Recon REsponse: vinay kodam - 23.11.2021
					if (type === "totalrecords") {
						vUrl = "/aspsapapi/gstr2bprResponseReportDwnld.do";
					} else {
						vUrl = "/aspsapapi/gstr2BPRRespReportGenerate.do";
						var aData1 = {
							"req": {
								"type": type,
								"fileId": obj.id
							}
						};
						this.reportDownload(aData1, vUrl);
						return;
					}
				} else if (obj.fileType === "GSTR3B_ITC_4243") { // Added for GSTR-3B - ITC Reversal by Bharat Gupta on 19.05.2020
					vUrl = "/aspsapapi/downloadGstr3BReports.do";

				} else if (obj.fileType === "GSTR7_TDS") { // Added for GSTR-3B - ITC Reversal by Bharat Gupta on 19.05.2020
					vUrl = "/aspsapapi/downloadGstr7Reports.do";

				} else if (obj.fileType === "GSTR7_TRANSACTIONAL") {
					var payload = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": "GSTR7",
							"status": null
						}
					};
					this.reportDownload(payload, "/aspsapapi/downloadGstr7TransFileReports.do");
					return;

				} else if (obj.fileType === "GSTR8") { // Added for GSTR-8 - ITC Reversal by Ram on 28.02.2024
					vUrl = "/aspsapapi/downloadGstr8FileReports.do";
					if (type === "totalrecords") {
						type = "Total Records"
					}
					if (type === "processed") {
						type = "Processed Records"
					}
					if (type === "error") {
						type = "Error Records"
					}
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "FileStatus",
							"dataType": "GSTR8",
							"status": null
						}
					};
					this.reportDownload(aData, vUrl);
					return;

				} else if (obj.fileType === "NILNONEXMPT") {
					vUrl = "/aspsapapi/downloadNilNonReports.do";

				} else if (obj.fileType === "ITC04_FILE") {
					vUrl = "/aspsapapi/downloadITC04Reports.do";

				} else if (obj.fileType === "TCSANDTDS") {
					vUrl = "/aspsapapi/downloadGstr2xReports.do";

				} else if (obj.fileType === "GLUPLOAD") { // GL Upload added by chaithra on 23/11/2020 ==/
					vUrl = "/aspsapapi/getGLTotalRecords.do";
				} else if (obj.fileType === "EINVOICE_RECON") { // E-invoice Recon added by chaithra on 27/1/2021 =========//
					var vUrl = "/aspsapapi/downloadFileStatusCsvReports.do";
					if (type === "totalrecords") {
						var vUrl = "/aspsapapi/downloadEinvReconTotalReport.do?fileId=" + obj.id;
						var adata = {};
						this.excelDownload2(adata, vUrl);
					} else if (type == "processed") {
						var aData = {
							"req": {
								"fileId": obj.id,
								"type": "EinvProcess",
								"reportCateg": "EINVOICE_RECON",
								"dataType": "Outward",
								"status": status
							}
						};
						this.reportDownload(aData, vUrl);
					} else {
						var aData = {
							"req": {
								"fileId": obj.id,
								"type": type,
								"reportCateg": "EINVOICE_RECON",
								"dataType": "Outward",
								"status": status
							}
						};
						this.reportDownload(aData, vUrl);
					}
				} else if (obj.fileType === "DELETE_GSTN") { // Delete GSTN data added by chaithra on 28/1/2021 =====//
					vUrl = "/aspsapapi/gstr1EinvGstinDeleteFileDownload.do";
				} else if (obj.fileType === "EXCLUSIVE_SAVE_FILE") { // Exclusive save file data added by chaithra on 28/1/2021 =====//
					vUrl = "/aspsapapi/gstr1VsEinvReportsDownload.do";
				} else if (obj.fileType === "VerticalUploadHSN") { // Exclusive save file data added by chaithra on 28/1/2021 =====//
					vUrl = "/aspsapapi/downloadHsnFileReports.do";
					if (type === "totalrecords") {
						type = "HSN_VERTICAL_TOTAL"
					}
					if (type === "processed") {
						type = "HSN_VERTICAL_PROCESSED"
					}
					if (type === "error") {
						type = "HSN_VERTICAL_ERROR"
					}
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": obj.dataType === "GSTR1A" ? "GSTR1A" : "GSTR1",
							"dataType": obj.dataType === "GSTR1A" ? "Outward_1A" : "Outward",
							"status": null
						}
					};
					this.reportDownload(aData, vUrl);
					return;
				} else if (obj.fileType === "180_days_Reversal") { // 180Days reversal added by chaithra on 30/3/2021 ======//
					vUrl = "/aspsapapi/reversal180ReportsDownloadAsync.do";

					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type + "180days",
							"reportCateg": "GSTR3B",
							"dataType": "Inward",
							"status": null
						}
					};
					this.reportDownload(aData, vUrl);
					return;

				} else if (obj.fileType === "3BFORM") { // 180Days reversal added by chaithra on 30/3/2021 ======//
					vUrl = "/aspsapapi/gstr3bSummaryReportWebDownload.do";
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"dataType": "GSTR3B"
						}
					};
					this.excelDownload(aData, vUrl);
					return;
				} else if (obj.fileType === "GSTR9_HSN") {
					vUrl = "/aspsapapi/downloadGstr9HsnReports.do";
				} else if (obj.fileType === "GSTR9INWARDOUTWARD") {
					vUrl = "/aspsapapi/downloadGstr9InOutwardReports.do";
				} else if (obj.fileType === "CROSS_ITC") {
					vUrl = "/aspsapapi/downloadCrossItcReports.do";
				} else if (obj.fileType === "REV_180DAYS_RESPONSE") {
					vUrl = "/aspsapapi/rev180RespReportRequest.do";
					if (type == "processed") {
						type = "180DaysRespProcessed";
					};
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type,
							"reportCateg": "REV_180DAYS_RESPONSE",
							"dataType": "GSTR3B",
							"status": status
						}
					};
					this.reportDownload(aData, vUrl);
				} else if (obj.fileType === "COMMON_CREDIT_REVERSAL") {
					var payload = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					this.reportDownload(payload, "/aspsapapi/exceptionalTaggingDownload.do");
					return;
				} else if (obj.fileType === "IMS") {
					var payload = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					this.reportDownload(payload, "/aspsapapi/getImsReportGenerate.do");
					return;
				} else if (obj.fileType === "2BPR_IMS_RECON_RESPONSE") {
					var payload = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					if (type === "totalrecords") {
						this.excelDownload(payload, "/aspsapapi/gstr2BPRImsTotalRptDownload.do");
					} else {
						this.reportDownload(payload, "/aspsapapi/gstr2BPRIMSReportGenerate.do");
					}
					return;
				} else if (obj.fileType === "2APR_IMS_RECON_RESPONSE") {
					var payload = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					if (type === "totalrecords") {
						this.excelDownload(payload, "/aspsapapi/gstr2APRImsTotalRptDownload.do");
					} else {
						this.reportDownload(payload, "/aspsapapi/gstr2APRIMSReportGenerate.do");
					}
					return;
				} else {
					vUrl = "/aspsapapi/downloadGstr1VerticalReports.do";
				}
			} else if (sel === "gstr13b") {
				vUrl = "/aspsapapi/downloadGstr1VerticalReports.do";
			} else if (sel === "EWB") {
				if (obj.fileType === "EWB_UPLOAD") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					vUrl = "/aspsapapi/ewbRespReportRequest.do";
					this.reportDownload(aData, vUrl);
					return;
				} else {
					vUrl = "/aspsapapi/downloadCewbReports.do";
				}
			}
			if (obj.fileType !== "EINVOICE_RECON" && obj.fileType !== "REV_180DAYS_RESPONSE") {
				if (obj.fileType !== "GLUPLOAD") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"type": type
						}
					};
					if (obj.fileType === "DELETE_GSTN" ||
						obj.fileType === "EXCLUSIVE_SAVE_FILE") // Added by chaithra on 28/1/2021 for Delete GSTN and Exclusive file save
					{
						this.csvDownload(aData, vUrl);
					} else {
						this.excelDownload(aData, vUrl);
					}
				} else {
					if (type == "processed") {
						vUrl = "/aspsapapi/getGLProcessedandErrorRecords.do";
						var aData = {
							"fileId": obj.id,
							"flagofRecord": "glReconProcessedRecords"
						};
					} else if (type == "error") {
						vUrl = "/aspsapapi/getGLProcessedandErrorRecords.do";
						var aData = {
							"fileId": obj.id,
							"flagofRecord": "glReconErrorRecords"
						};
					} else {
						var aData = {
							"fileId": obj.id
						};
					}
					this.excelDownload(aData, vUrl);
				}
			}
		},

		onVerticalSFTPDownload: function (oEvent, type, count) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("SftpStatus").getObject(),
				sel = this.getView().byId("slSftpCateg").getSelectedKey(); // Added by Jakeer Syed on 08.11.2019

			if (count === 0) {
				sap.m.MessageBox.information("No data to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			if (obj.fileType === "GSTR7_TDS") { // Added for GSTR7_TDS by Ram on 07.10.2020
				var vUrl = "/aspsapapi/downloadGstr7Reports.do";

			} else if (obj.fileType === "ITC04_FILE") {
				vUrl = "/aspsapapi/downloadITC04Reports.do";

			} else if (obj.fileType.toUpperCase() === "ISD") {
				vUrl = "/aspsapapi/downloadIsdFileReports.do";
				var aData = {
					"req": {
						"fileId": obj.id,
						"type": type,
						"reportCateg": "SFTP",
						"dataType": "ISD",
						"status": null
					}
				};
				this.reportDownload(aData, vUrl);
				return;
			} else if (obj.fileType === "RECON_RESPONSE") { // Added for Recon REsponse: vinay kodam - 11.05.2020
				if (type === "totalrecords") {
					vUrl = "/aspsapapi/gstrUserResponse2ReportsDownload.do";
				} else {
					vUrl = "/aspsapapi/gstr2ASftpRespReportGenerate.do";
					var aData1 = {
						"req": {
							"type": type,
							"fileId": obj.id,
							"flag": true
						}
					};
					this.reportDownload(aData1, vUrl);
					return;
				}
			} else {
				vUrl = "/aspsapapi/gstrUserResponse2ReportsDownload.do";
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
		 * Called to Clear Selection Filter value to default value
		 * Developed by: Bharat Gupta on 16.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 */
		onPressClear: function () {
			var oModel = this.getView().getModel("DataStatus"),
				oData = oModel.getData(),
				vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("sbStatus").getSelectedKey() === "api") {
				if (this.byId("dAdapt")) {
					var aField = [
						"slProfitCtr", "slPlant", "slDivision", "slLocation", "slSalesOrg", "slPurcOrg", "slDistrChannel", "sluserAccess1",
						"sluserAccess2", "sluserAccess3", "sluserAccess4", "sluserAccess5", "sluserAccess6"
					];
					aField.forEach(function (f) {
						this.byId(f).setSelectedKeys([]);
					}.bind(this));
					// this.getView().byId("slProfitCtr").setSelectedKeys([]);
					// this.getView().byId("slPlant").setSelectedKeys([]);
					// this.getView().byId("slDivision").setSelectedKeys([]);
					// this.getView().byId("slLocation").setSelectedKeys([]);
					// this.getView().byId("slSalesOrg").setSelectedKeys([]);
					// this.getView().byId("slPurcOrg").setSelectedKeys([]);
					// this.getView().byId("slDistrChannel").setSelectedKeys([]);
					// this.getView().byId("sluserAccess1").setSelectedKeys([]);
					// this.getView().byId("sluserAccess2").setSelectedKeys([]);
					// this.getView().byId("sluserAccess3").setSelectedKeys([]);
					// this.getView().byId("sluserAccess4").setSelectedKeys([]);
					// this.getView().byId("sluserAccess5").setSelectedKeys([]);
					// this.getView().byId("sluserAccess6").setSelectedKeys([]);
				}
				oData.returnPeriod = false;
				this.byId("slStatsDataType").setSelectedKey("outward");
				this.byId("slStatsCriteria").setSelectedKey("RECEIVED_DATE_SEARCH");
				this.byId("slStatsGSTIN").setSelectedKeys([]);
				this._bindDefaultDate();
				this.getDataStatusAPI();

			} else if (this.byId("sbStatus").getSelectedKey() === "upload") {
				var oDataType = this.getOwnerComponent().getModel("DropDown").getData().dataTypeNew.integrated,
					oFileType = this.getOwnerComponent().getModel("DropDown").getData().fileTypeNew,
					oFileModel = this.byId("slStatsFileType").getModel("FileType");

				oFileModel.setData(oFileType.outward);
				oFileModel.refresh(true);

				this.byId("slStatsReturnType").setSelectedKey("integrated");
				this.byId("slFileDataType").setModel(new JSONModel(oDataType), "DataType");
				this.byId("slFileDataType").setSelectedKey(oDataType[0].key);
				this.byId("slStatsFileType").setSelectedKey(oFileType.outward[0].key);
				this._bindFileDefaultDate();
				this.getFileStatusData("");

			} else if (this.byId("sbStatus").getSelectedKey() === "sftp") {
				oDataType = this.getOwnerComponent().getModel("DropDown").getData().sftpDataType.integrated;
				oFileType = this.getOwnerComponent().getModel("DropDown").getData().sftpFileType;
				oFileModel = this.byId("slSftpFileType").getModel("FileType");

				oFileModel.setData(oFileType.outward);
				oFileModel.refresh(true);
				this.byId("slSftpCateg").setSelectedKey("integrated");
				this.byId("slSftpDataType").setModel(new JSONModel(oDataType), "DataType");
				this.byId("slSftpDataType").setSelectedKey(oDataType[0].key);
				this.byId("slSftpFileType").setSelectedKey(oFileType.outward[0].key);
				this._bindDefaultSftpDate();
				this._getSftpFileData();
			}
			oModel.refresh(true);
		},

		/**
		 * Called to bind Default date value in Data Status - API screen
		 * Developed by: Bharat Gupta on 16.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 */
		_bindDefaultDate: function () {
			var vDate = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);

			vDate.setDate(vDate.getDate() - 9);
			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iFromDate").setMaxDate(new Date());

			this.byId("iToDate").setDateValue(new Date());
			this.byId("iToDate").setMaxDate(new Date());
			this.byId("iToDate").setMinDate(vDate);

			this.byId("iFromPeriod").setDateValue(vPeriod);
			this.byId("iFromPeriod").setMaxDate(new Date());

			this.byId("iToPeriod").setDateValue(new Date());
			this.byId("iToPeriod").setMaxDate(new Date());
			this.byId("iToPeriod").setMinDate(vPeriod);
		},

		/**
		 * Called to bind Default date value in Upload date fields
		 * Developed by: Bharat Gupta on 16.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 */
		_bindFileDefaultDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iUploadFromDate").setDateValue(vDate);
			this.byId("iUploadFromDate").setMaxDate(new Date());

			this.byId("iUploadToDate").setDateValue(new Date());
			this.byId("iUploadToDate").setMaxDate(new Date());
			this.byId("iUploadToDate").setMinDate(vDate);
		},

		/**
		 * Called to open/close table in Full Screen mode
		 * Developed by: Bharat Gupta on 16.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 * @param {string} action Action for full screen
		 */
		onPressFullScreen: function (action) {
			var oModel = this.getView().getModel("visiSummDataStatus2");
			oModel.setProperty("/apiFullScreen", (action === "open"));
			oModel.refresh(true);

			this.byId("id_chartds3").setFullScreen(action === "open");
			this.byId("tabDataAPI").setVisibleRowCount(action === "open" ? 20 : 12);
		},

		/**
		 * Method called to open/close file table in Full Screen Mode
		 * Developed by: Bharat Gupta - 22.12.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 * @param {string} action Action for full screen
		 */
		onFileFullScreen: function (action) {
			var oModel = this.getView().getModel("visiSummDataStatus2");
			oModel.getData().fileFullScreen = (action === "open");
			oModel.refresh(true);

			this.byId("tabRecFileStatus").setFullScreen(action === "open");
			this.byId("tabData").setVisibleRowCount(action === "open" ? 20 : 12);
		},

		/**
		 * Method called to get SFTP file data
		 * Developed by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.view.DataStatusNew
		 * @private
		 */
		_getSftpFileData: function () {
			var vKey = this.byId("slSftpFileType").getSelectedKey();

			if (["raw", "COMPREHENSIVE_RAW", "ITC04_FILE", "COMPREHENSIVE_INWARD_RAW"].includes(vKey)) {
				this.byId("tabSftpData").setVisible(true);
				this.byId("tabSftpVerFileStatus").setVisible(false);
				if (vKey === "COMPREHENSIVE_RAW") {
					this.byId("einvSFTP").setEnabled(false);
					this.byId("ewbSFTP").setEnabled(false);
					this.byId("gstSFTP").setEnabled(false);
					var oVisiSum = {
						"apieinv": false,
						"apiewb": false,
						"apigst": false,
						"einv": false, // true,
						"ewb": false, // true,
						"gst": false, // true
						"dms": true,
						"apiFullScreen": false,
						"fileFullScreen": false,
						"sftpFullScreen": false
					};
					this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");
				} else {
					this.byId("einvSFTP").setEnabled(false);
					this.byId("ewbSFTP").setEnabled(false);
					this.byId("gstSFTP").setEnabled(false);
					var oVisiSum = {
						"apieinv": false,
						"apiewb": false,
						"apigst": false,
						"einv": false, // true,
						"ewb": false, // true,
						"gst": false, // true
						"dms": vKey === 'COMPREHENSIVE_INWARD_RAW' ? true : false,
						"apiFullScreen": false,
						"fileFullScreen": false,
						"sftpFullScreen": false
					}; // End of Code
					this.getView().setModel(new JSONModel(oVisiSum), "visiSummDataStatus2");
				}
			} else {
				this.byId("tabSftpData").setVisible(false);
				this.byId("tabSftpVerFileStatus").setVisible(true);
			}
			this._getSftpFileDataFinal();
		},

		_getSftpFileDataFinal: function () {
			var dataType = this.byId("slSftpDataType").getSelectedKey();
			if (dataType == "inward1") {
				dataType = "inward";
			}

			var oSftpModel = this.getView().getModel("SftpStatus"),
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"dataType": dataType,
						"fileType": this.byId("slSftpFileType").getSelectedKey(),
						"dataRecvFrom": this.byId("dtSftpUploadFrom").getValue(),
						"dataRecvTo": this.byId("dtSftpUploadTo").getValue()
					}
				};
			if (oSftpModel) {
				oSftpModel.setData(null);
				oSftpModel.refresh(true);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/newSftpFileStatus.do",
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "SftpStatus");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("newSftpFileStatus : Error");
				}.bind(this));
		},

		/**
		 * On press Expand / Collapse All button
		 * @memberOf com.ey.digigst.view.DataStatus
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} action Expand/Collapse action
		 */
		onPressExpandCollapse: function (oEvent, action) {
			var oTable = oEvent.getSource().getParent().getParent();
			if (action === "E") {
				oTable.expandToLevel(3);
			} else {
				oTable.collapseAll();
			}
		},

		getAllGSTIN: function (flag) {
			var aRegGSTIN = this.getOwnerComponent().getModel("userPermission").getProperty("/respData/dataSecurity/gstin"),
				aGstin = [];

			switch (flag) {
			case "O":
				aGstin = aRegGSTIN.slice(0);
				break;
			case "TDS":
				aGstin = this.getOwnerComponent().getModel("allTDSGstin").getProperty("/");
				break;
			case "G8":
				aGstin = this.getView().getModel("Gstr8Gstin").getProperty("/");
				break;
			default:
				var aISDGSTIN = this.getOwnerComponent().getModel("ISDGstin").getProperty("/"),
					aGstin = aRegGSTIN.map(function (e) {
						return e;
					});
				aISDGSTIN.forEach(function (e) {
					aGstin.push(e);
				});
			}
			aGstin.sort(function (a, b) {
				if (a.value === "All")
					return -1; // "All" should come first
				if (b.value === "All")
					return 1; // Move other values down
				return a.value.localeCompare(b.value); // Sort alphabetically
			});
			this.getView().setModel(new JSONModel(aGstin), "allGSTIN");
		},

		onSftpFullScreen: function (action) {
			var oModel = this.getView().getModel("visiSummDataStatus2");

			this.byId("iCcSftpData").setFullScreen(action === "open");
			this.byId("tabSftpData").setVisibleRowCount(action === "open" ? 18 : 12);

			oModel.setProperty("/sftpFullScreen", (action === "open"));
			oModel.refresh(true);
		},

		AppPer180: function (oEvt) {
			var data = this.getView().getModel("userPermission").getProperty("/appPermission/P13"),
				text = oEvt.getSource().getSelectedButton().getText();

			if (text === "180 Days Payment Reference" || text === "180 Days Reversal Response") {
				this.byId("fileUploader").setEnabled(data);
				this.byId("btnUpload").setEnabled(data);
			} else {
				this.byId("fileUploader").setEnabled(true);
				this.byId("btnUpload").setEnabled(true);
			}
		},

		onDelete: function () {
			var index = this.byId("tabData").getSelectedIndices(),
				data = this.getView().getModel("FileStatus").getData().resp[index[0]];

			if (data.processedActive === 0 && data.errorsActive === 0) {
				MessageBox.information("No active records to delete.");
				return;
			}
			if (!index.length) {
				MessageBox.information("Plese select atleast One Record.");
				return;
			}
			if (index.length > 1) {
				MessageBox.information("Please select one file at a time for deleting records.");
				return;
			}
			MessageBox.information('Do you want to delete records for selected file?', {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onDelete1();
					}
				}.bind(this)
			});
		},

		onDelete1: function () {
			var index = this.byId("tabData").getSelectedIndices(),
				data = this.getView().getModel("FileStatus").getData().resp[index[0]].id,
				req = {
					"req": {
						"fileId": data
					}
				};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/deleteFile.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);
					this.onSearch();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		}
	});
});