sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment"
], function (Controller, JSONModel, Fragment) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.VGLReconSrVsGL", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.VGLReconSrVsGL
		 */
		onInit: function () {
			this.getView().setModel(new JSONModel(this._setViewProperty()), "Property");
			this._bindMasterDataView();

			this._setReadonly("reconInitFrom");
			this._setReadonly("reconInitTo");

			// Get the current date
			var vDate = new Date();

			// Set the fromDate to the current date
			var fromDate = vDate.toISOString().split('T')[0]; // Format: YYYY-MM-DD

			// Set the toDate to 9 days ago
			vDate.setDate(vDate.getDate() - 9);
			var toDate = vDate.toISOString().split('T')[0];

			var oData = {
				"file": {
					"fileType": "GL_DUMP",
					"fromDate": fromDate,
					"toDate": toDate
				},
				"api": {
					"fileType": "GL_DUMP",
					"fromDate": fromDate,
					"toDate": toDate
				},
				"sftp": {
					"fileType": "GL_DUMP",
					"fromDate": fromDate,
					"toDate": toDate
				}
			};
			this.getView().setModel(new JSONModel(oData), "filter");

			var psData = {
				"taxPeriodFrom": null,
				"taxPeriodTo": null,
				"transType": "O",
				"gstins": []
			};
			this.getView().setModel(new JSONModel(psData), "psfilter");

			this._bindDefaultData();
		},

		_bindDefaultData: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this._setFromDateProperty("idFileFromDate", vDate, new Date());
			this._setToDateProperty("idFileToDate", new Date(), new Date(), vDate);

			this._setFromDateProperty("idSFTPFromDate", vDate, new Date())
			this._setToDateProperty("idSFTPToDate", new Date(), new Date(), vDate)

			this._setFromDateProperty("idAPIFromDate", vDate, new Date())
			this._setToDateProperty("idAPIToDate", new Date(), new Date(), vDate)

			this._setFromDateProperty("idPFromtaxPeriod", vDate, new Date())
			this._setToDateProperty("idPTotaxPeriod", new Date(), new Date(), vDate)

			this._setFromDateProperty("idSFromtaxPeriod", vDate, new Date())
			this._setToDateProperty("idSTotaxPeriod", new Date(), new Date(), vDate)
		},

		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("idFileFromDate")) {
				var vDatePicker = "idFileToDate";
			} else if (oEvent.getSource().getId().includes("idSFTPFromDate")) {
				vDatePicker = "idSFTPToDate";
			} else if (oEvent.getSource().getId().includes("idAPIFromDate")) {
				vDatePicker = "idAPIToDate";
			} else if (oEvent.getSource().getId().includes("idPFromtaxPeriod")) {
				vDatePicker = "idPTotaxPeriod";
			} else if (oEvent.getSource().getId().includes("idSFromtaxPeriod")) {
				vDatePicker = "idSTotaxPeriod";
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

		onPressClear: function () {
			this._bindDefaultData();
		},
		onChangeSegment: function (oEvent) {

			var oSegmentBtn = oEvent.getSource().getSelectedKey();
			switch (oSegmentBtn) {
			case 'status':
				this.onSearch('WEB_UPLOAD');
				break;
			case 'api':
				this.onSearch('API');
				break;
			case 'sftp':
				this.onSearch('SFTP');
				break;
			}

		},

		onSearch: function (source) {

			var oData = this.getView().getModel("filter").getData();

			switch (source) {
			case 'WEB_UPLOAD':
				var Payload = {
					"req": {
						"dataRecvFrom": oData.file.fromDate,
						"dataRecvTo": oData.file.toDate,
						"entityId": $.sap.entityID,
						"fileType": oData.file.fileType,
						"dataType": "GL Recon",
						"source": source ///API/SFTP
					}
				};
				var model = "FileStatus"
				break;
			case 'API':
				Payload = {
					"req": {
						"dataRecvFrom": oData.api.fromDate,
						"dataRecvTo": oData.api.toDate,
						"entityId": $.sap.entityID,
						"fileType": oData.api.fileType,
						"dataType": "GL Recon",
						"source": source ///API/SFTP
					}
				};
				model = "ApiStatus"
				break;
			case 'SFTP':
				Payload = {
					"req": {
						"dataRecvFrom": oData.sftp.fromDate,
						"dataRecvTo": oData.sftp.toDate,
						"entityId": $.sap.entityID,
						"fileType": oData.sftp.fileType,
						"dataType": "GL Recon",
						"source": source ///API/SFTP
					}
				};
				model = "SftpStatus"
				break;
			}

			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.getView().getModel(model);
			if (oModel) {
				oModel.setProperty("/", []);
				oModel.refresh(true);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/glReconFileStatus.do",
					data: JSON.stringify(Payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					// var data = {
					// 	"hdr": {
					// 		"status": "S"
					// 	},
					// 	"resp": [{
					// 		"uploadedOn": "2025-04-14",
					// 		"uploadedBy": "P2001353321",
					// 		"dataType": "OUTWARD",
					// 		"fileType": "COMPREHENSIVE_RAW",
					// 		"fileName": "COMPREHENSIVE_RAW_20250414110655_HSN Transactional.xlsx",
					// 		"fileStatus": "Processed",
					// 		"total": 3,
					// 		"processedActive": 3,
					// 		"processedInactive": 1,
					// 		"errorsActive": 1,
					// 		"errorsInactive": 1,
					// 		"id": 834572,
					// 		"transformationStatus": ""
					// 	}]
					// }
					this.getView().setModel(new JSONModel(data.resp), model);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressFileStatusDownload: function (oEvent, count, type, status, model) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext(model).getObject();
			if (count === 0 || !count) {
				sap.m.MessageBox.information("No data available to download", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			var vUrl = "/aspsapapi/downloadGlFileReports.do";
			var aData = {
				"req": {
					"fileId": obj.id,
					"type": type,
					"reportCateg": "GL_DUMP",
					"dataType": "Gl Recon",
					"status": status
				}
			};
			this.reportDownload(aData, vUrl);

		},

		onPressPayloadID: function (oEvent) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("ApiStatus").getObject();
			var vUrl = "/aspsapapi/glReconErpFileDownload.do";
			var aData = {
				"req": {
					"payloadId": obj.payloadId
				}
			};
			this.excelDownload(aData, vUrl);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.VGLReconSrVsGL
		 */
		onAfterRendering: function () {
			if ($.sap.entityID && this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._getFileStatus();

				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this._getAllGstin(),
						this._getgstr2UserNames(),
						this._getgstr2EmailIds()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		_bindMasterDataView: function () {
			var oModel = new JSONModel();
			oModel.loadData("model/GLRecon.json");
			oModel.attachRequestCompleted(function () {
				var oData = oModel.getProperty("/");
				this.getView().setModel(new JSONModel(oData.apiStatus), "ApiStatus");
				this.getView().setModel(new JSONModel(oData.fileStatus), "FileStatus");
				this.getView().setModel(new JSONModel(oData.sftpStatus), "SftpStatus");
				// this.getView().setModel(new JSONModel(oData.glProcessEntity), "GLProcessEntity");
				this.getView().setModel(new JSONModel(oData.glProcessGstin), "GLProcessGstin");
				this.getView().setModel(new JSONModel(oData.glReqIdWise), "ReconReqIdWise");
				this.getView().setModel(new JSONModel(oData.glReconResult), "GLReconResult");
			}.bind(this));
		},

		onSelectSrVsGlTab: function (type) {

			if (type === "processSumm") {
				this.byId("navCon").to(this.byId("dpEntity"), "Slide");
				this.onSearchGLPS();
			}
		},

		_getAllGstin: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstinList.do",
						data: JSON.stringify({
							"req": {
								"entityId": [$.sap.entityID]
							}
						}),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						if (data.resp.length) {
							data.resp.unshift({
								"gstin": "All"
							});
						}
						this.getView().setModel(new JSONModel(data.resp), "GstinModel");
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getgstr2UserNames: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2UserNames.do",
						data: JSON.stringify({
							"entityId": $.sap.entityID,
							"screenName": "glRecon"
						}),
						contentType: "application/json"
					})
					.done(function (data, status, jqXhr) {
						if (data.resp.requestDetails.length) {
							data.resp.requestDetails.unshift({
								"userName": "All"
							});
						}
						this.getView().setModel(new JSONModel(data.resp.requestDetails), "UserModel");
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getgstr2EmailIds: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2EmailIds.do",
						data: JSON.stringify({
							"entityId": $.sap.entityID,
							"screenName": "glRecon"
						}),
						contentType: "application/json"
					})
					.done(function (data, status, jqXhr) {
						if (data.resp.requestDetails.length) {
							data.resp.requestDetails.unshift({
								"email": "All"
							});
						}
						this.getView().setModel(new JSONModel(data.resp.requestDetails), "EmailModel");
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onChangeFromPeriod: function (oEvent) {
			var period = oEvent.getSource().getValue(),
				oModel = this.getView().getModel("Property");

			if (oModel.getProperty("/fromTaxPeriod") > oModel.getProperty("/toTaxPeriod")) {
				oModel.setProperty("/toTaxPeriod", period);
			}
			oModel.setProperty("/minDate", new Date(period.substr(0, 4), +period.substr(4) - 1));
			oModel.refresh(true);
		},

		_getFileStatus: function (flag) {
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.getView().getModel("GlFileStatus"),
				oPropModel = this.getView().getModel("Property"),
				oDataProp = oPropModel.getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (oDataProp.pageNo || 1) - 1,
						"pageSize": +oDataProp.pgSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"flag": false
					}
				};

			if (flag) {
				var oFltrData = this._glReconFilter.getModel("ReconFilter").getProperty("/");
				oPropModel.setProperty("/glRecFilter", true);
				payload.req.dataRecvFrom = oFltrData.uploadedFrom;
				payload.req.dataRecvTo = oFltrData.uploadedTo;
				payload.req.fileType = (oDataProp.segBtn === "pGLRecon" ? oFltrData.fileType : undefined);
				payload.req.status = oFltrData.status;
			} else {
				oPropModel.setProperty("/glRecFilter", false);
			}
			oPropModel.refresh(true);

			if (oModel) {
				oModel.setProperty("/", []);
				oModel.refresh(true);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/glFileStatus.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data) {
					this.getView().setModel(new JSONModel(data.resp), "GlFileStatus");
					this._countPagination(data.hdr);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_countPagination: function (hdr) {
			var oModel = this.getView().getModel("Property"),
				vTotal = Math.ceil(hdr.totalCount / oModel.getProperty("/pgSize")),
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

		onFilterGLMasterData: function () {
			if (!this._glReconFilter) {
				this._glReconFilter = Fragment.load({
					id: this.getView().getId(),
					name: "com.ey.digigst.fragments.others.GLRecon.ReconFilter",
					controller: this
				}).then(function (oDialog) {
					this._glReconFilter = oDialog;

					var oDatePickerFrom = Fragment.byId(this.getView().getId(), "dtUploadFrom"),
						oDatePickerTo = Fragment.byId(this.getView().getId(), "dtUploadTo");

					oDatePickerFrom.addEventDelegate({
						onAfterRendering: this.onDialogAfterRendering.bind(this)
					});
					oDatePickerTo.addEventDelegate({
						onAfterRendering: this.onDialogAfterRendering.bind(this)
					});
					if (!oDialog.getModel("ReconFilter")) {
						this._setReconFilter();
					}
					this.getView().addDependent(this._glReconFilter);
					this._glReconFilter.open();
				}.bind(this));
			} else {
				if (!this.getView().getModel("Property").getProperty("/glRecFilter")) {
					this._setReconFilter();
				}
				this._glReconFilter.open();
			}
		},

		_setReconFilter: function () {
			var today = new Date(),
				fromDt = new Date();

			fromDt = new Date(fromDt.setDate(fromDt.getDate() - 14));
			this._glReconFilter.setModel(new JSONModel({
				"type": "srVsGl",
				"uploadedFrom": fromDt.getFullYear() + "-" + (fromDt.getMonth() + 1).toString().padStart(2, '0') + "-" +
					(fromDt.getDate()).toString().padStart(2, '0'),
				"uploadedTo": today.getFullYear() + "-" + (today.getMonth() + 1).toString().padStart(2, '0') + "-" +
					(today.getDate()).toString().padStart(2, '0'),
				"minDate": fromDt,
				"maxDate": new Date()
			}), "ReconFilter");
		},

		onChangeReconFilterDate: function (evt, type) {
			var oModel = evt.getSource().getModel("ReconFilter"),
				value = evt.getParameter('value');

			if (type === "F") {
				oModel.setProperty("/minDate", new Date(value));
				if (oModel.getProperty("/uploadedTo") < value) {
					oModel.setProperty("/uploadedTo", value);
				}
			}
			oModel.refresh(true);
		},

		onCloseReconFilter: function (type) {
			if (type === 'F') {
				this._getFileStatus(true);
			}
			this._glReconFilter.close();
		},

		onDownloadSrGlTemp: function () {
			sap.m.URLHelper.redirect("excel/glRecon/GL_Dump_Template.xlsx", false);
		},

		onPressUpload: function () {
			var oFileUploader = this.getView().byId("fileUploaderGL"),
				sKey = this.getView().getModel("Property").getProperty("/segBtn");

			if (!oFileUploader.getValue()) {
				sap.m.MessageBox.warning("Please select file to upload.")
				return;
			}
			oFileUploader.setAdditionalData($.sap.entityID);
			oFileUploader.setUploadUrl("/aspsapapi/webGlDumpFileUpload.do");
			var vGR10 = this.getOwnerComponent().getModel("GroupPermission").getProperty("/GR10");
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
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			sap.ui.core.BusyIndicator.hide();
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fileUploaderGL").setValue(null);
				this._getFileStatus();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onDownloadFile: function (fileName) {
			sap.m.URLHelper.redirect("/aspsapapi/glFileDownload.do?fileName=" + fileName, false);
		},

		onPressPagination: function (btn) {
			var oModel = this.getView().getModel("Property"),
				flag = oModel.getProperty("/glRecFilter");

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
			this._getFileStatus(flag);
		},

		onSubmitPagination: function (type) {
			var oModel = this.getView().getModel("Property"),
				flag = oModel.getProperty("/glRecFilter");

			if (type === "S") {
				oModel.setProperty("/pageNo", 1);
				oModel.refresh(true);
			}
			this._getFileStatus(flag);
		},

		onPressSummaryGstinLink: function (oEvent) {
			this.byId("navCon").to(this.byId("dpGstin"), "Slide");
		},

		onPressBackToEntity: function () {
			this.byId("navCon").to(this.byId("dpEntity"), "Slide");
		},

		convertTaxPeriod: function (taxPeriod) {
			debugger;
			// Ensure the input is a string
			var taxPeriodStr = taxPeriod.toString();

			// Extract the year and month
			var year = taxPeriodStr.substring(2, 6); // Get the first four characters (YYYY)
			var month = taxPeriodStr.substring(0, 2); // Get the last two characters (MM)

			// Return the formatted string as "MMYYYY"
			return year + month;
		},

		onInitiateMatching: function () {
			debugger;
			var oModelForTab1 = this.byId("idTabGLPS").getModel("GLProcessEntity").getProperty("/"),
				oSelectedItem = this.getView().byId("idTabGLPS").getSelectedIndices();

			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("PLease Select at least one Gstin");
				return;
			}

			var aGstin = oSelectedItem.map(function (e) {
				return oModelForTab1[e].gstin;
			});

			var oData = this.getView().getModel("psfilter").getProperty("/"),
				payload = {
					"entityId": $.sap.entityID,
					"gstins": aGstin,
					"fromTaxPeriod": this.convertTaxPeriod(oData.taxPeriodFrom),
					"toTaxPeriod": this.convertTaxPeriod(oData.taxPeriodTo),
					"transactionType": oData.transType
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGlRecon.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					sap.m.MessageBox.success(data.resp.status);
				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onNavBackToRecon: function () {
			if (this.getView().getModel("Property").getProperty("/reqIdWise") === "E") {
				this.byId("navCon").to(this.byId("dpEntity"), "Slide");
			} else {
				this.byId("navCon").to(this.byId("dpGstin"), "Slide");
			}
		},

		onRequestIdWise: function (type) {
			this.byId("navCon").to(this.byId("pReqIdWise"), "Slide");
			this.getView().getModel("Property").setProperty("/reqIdWise", type);
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getGLReconIds(false),
					this._getGlReconReportData(false)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getGLReconIds: function (flag) {
			var oPropModel = this.getView().getModel("Property"),
				oPayload = {
					"entityId": $.sap.entityID,
					"initiationDateFrom": oPropModel.getProperty("/initiationDateFrom"),
					"initiationDateTo": oPropModel.getProperty("/initiationDateTo")
				};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGlReconRequestIds.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXhr) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						if (data.hdr.status !== "S") {
							MessageBox.error(data.hdr.message);
							return;
						}
						if (data.resp.requestDetails.length) {
							data.resp.requestDetails.unshift({
								"requestId": "All"
							});
						}
						this.getView().setModel(new JSONModel(data.resp.requestDetails), "GlReconReqIds")
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getGlReconReportData: function (flag) {
			var oPropModel = this.getView().getModel("Property"),
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"initiationDateFrom": oPropModel.getProperty("/initiationDateFrom"),
						"initiationDateTo": oPropModel.getProperty("/initiationDateTo"),
						"requestId": this.removeAll(oPropModel.getProperty("/requestIds")),
						"initiationByUserId": this.removeAll(oPropModel.getProperty("/initUserIds")),
						"initiationByUserEmailId": this.removeAll(oPropModel.getProperty("/initEmailIds")),
						"reconStatus": oPropModel.getProperty("/status")
					}
				};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGlReconReportRequestStatusFilter.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXhr) {
						this.byId("tabReconReqIdWise").setModel(new JSONModel(data.resp), "ReconReqIdWise");
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						resolve(data);
					}.bind(this))
					.fail(function (error) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(error);
					}.bind(this));
			}.bind(this));
		},

		onChangeReconInitiateDate: function (evt, type) {
			var oPropModel = this.getView().getModel("Property"),
				value = evt.getParameter('value');

			if (type === "F") {
				oPropModel.setProperty("/minReconDate", new Date(value));
				if (oPropModel.getProperty("/initiationDateTo") < value) {
					oPropModel.setProperty("/initiationDateTo", value);
				}
			}
			oPropModel.refresh(true);
			this._getGLReconIds(true);
		},

		onReconResults: function () {
			this._getGlReconReportData(true);
		},

		onClearReconResult: function () {
			var oPropModel = this.getView().getModel("Property"),
				today = new Date(),
				vDate20 = new Date();

			vDate20 = new Date(vDate20.setDate(vDate20.getDate() - 19));
			oPropModel.setProperty("/initiationDateFrom", vDate20.getFullYear() + "-" + (vDate20.getMonth() + 1).toString().padStart(2, '0') +
				"-" + vDate20.getDate().toString().padStart(2, '0'));
			oPropModel.setProperty("/initiationDateTo", today.getFullYear() + "-" + (today.getMonth() + 1).toString().padStart(2, '0') + "-" +
				today.getDate().toString().padStart(2, '0'));
			oPropModel.setProperty("/requestIds", []);
			oPropModel.setProperty("/initUserIds", []);
			oPropModel.setProperty("/initEmailIds", []);
			oPropModel.setProperty("/status", null);
			oPropModel.refresh(true);
			this.onPressReconReqIdWise();
		},

		onReconGstinCount: function (evt) {
			var oContext = evt.getSource().getBindingContext("ReconReqIdWise"),
				aGstin = oContext.getProperty(oContext.getPath() + "/gstins"),
				oSource = evt.getSource();
			if (!this._pPopover) {
				this._pPopover = Fragment.load({
					name: "com.ey.digigst.fragments.others.GLRecon.PopoverGstin",
				}).then(function (oPopover) {
					this.getView().addDependent(oPopover);
					return oPopover;
				}.bind(this));
			}
			this._pPopover.then(function (oPopover) {
				oPopover.setModel(new JSONModel(aGstin), "Gstins");
				oPopover.openBy(oSource);
			});
		},

		onViewReconSummary: function (oEvent) {
			if (!this._dPreRecon) {
				this._dPreRecon = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GLRecon.PreReconSumm", this);
				this.getView().addDependent(this._dPreRecon);
			}
			var reqId = oEvent.getSource().getBindingContext("ReconReqIdWise").getObject().requestId;
			this._dPreRecon.open();
			this._getPreReconSummary(reqId);
		},

		_getPreReconSummary: function (reqId) {
			this._dPreRecon.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGlReconViewSummary.do",
					data: JSON.stringify({
						"requestId": reqId
					}),
					contentType: "application/json"
				})
				.done(function (data, status, jqXhr) {
					this._dPreRecon.setBusy(false);
					this._dPreRecon.setModel(new JSONModel([data.resp.summaryDetails]), "PreReconSummary");
				}.bind(this))
				.fail(function (jqXhr, error, status) {
					this._dPreRecon.setBusy(false);
				}.bind(this));
		},

		onCloseReconSummary: function () {
			this._dPreRecon.close();
		},

		onDownloadReconFile: function (fileName) {
			sap.m.URLHelper.redirect("/aspsapapi/glReconFileDownload.do?fileName=" + fileName, false);
		},

		_setViewProperty: function () {
			var today = new Date(),
				vDate20 = new Date(),
				vPeriod = "" + today.getFullYear() + (today.getMonth() + 1).toString().padStart(2, '0');

			vDate20 = new Date(vDate20.setDate(vDate20.getDate() - 19));
			vDate20 = vDate20.getFullYear() + "-" + (vDate20.getMonth() + 1).toString().padStart(2, '0') + "-" +
				vDate20.getDate().toString().padStart(2, '0');
			return {
				// "segBtn": "pGLRecon",
				// "glRecFilter": false,
				"sbUpload": "upload",
				"sbStatus": "upload",
				"fileTransformation": 0,
				"infoTooltip": "Kindly note - if With Transformation is selected while uploading the file, the data will be transformed basis rules set up in Data Mapping Solution",
				"gstins": [],
				"fromTaxPeriod": vPeriod,
				"toTaxPeriod": vPeriod,
				"minDate": new Date(today.getFullYear(), today.getMonth()),
				"maxDate": today,
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 10,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false,

				"reqIdWise": null,
				"minReconDate": new Date(vDate20),
				"initiationDateFrom": vDate20,
				"initiationDateTo": today.getFullYear() + "-" + (today.getMonth() + 1).toString().padStart(2, '0') + "-" +
					today.getDate().toString().padStart(2, '0'),
				"requestIds": [],
				"initUserIds": [],
				"initEmailIds": [],
				"status": null,
				"pageNoReq": 0,
				"pgTotalReq": 0,
				"pgSizeReq": 10,
				"ePageNoReq": false,
				"bFirstReq": false,
				"bPrevReq": false,
				"bNextReq": false,
				"bLastReq": false
			};
		},

		onDialogAfterRendering: function (oEvent) {
			oEvent.srcControl.$().find("input").attr("readonly", true);
		},

		_setReadonly: function (vId) {
			var obj = this.byId(vId)
			obj.addEventDelegate({
				onAfterRendering: this.onDialogAfterRendering.bind(this)
			});
		},

		_formatTaxPeriod: function (value) {
			if (!value) {
				return null;
			}
			var value = "" + value;
			if (+value.substr(0, 4) > 2100) {
				return value;
			}
			var date = new Date(value.slice(0, 4), (+value.slice(4) - 1), '01');
			return date.toLocaleString('default', {
				month: 'short',
				year: 'numeric'
			});
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
		getViewRulesInUpload: function () {
			var oView = this.getView();
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
						oView.setModel(oModel, "Rules");
					} else {
						oView.setModel(new JSONModel(null), "Rules");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oView.setModel(new JSONModel(null), "Rules");
				});
		},

		onPressClearGLPS: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			this._setFromDateProperty("idPFromtaxPeriod", vDate, new Date());
			this._setToDateProperty("idPTotaxPeriod", new Date(), new Date(), vDate);
			var oPropModel = this.getView().getModel("psfilter");

			oPropModel.setProperty("/transType", "O");
			oPropModel.setProperty("/gstins", []);
			oPropModel.refresh(true);
			this.onSearchGLPS();
		},

		onSearchGLPS: function () {

			var oData = this.getView().getModel("psfilter").getData();
			var Payload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriodFrom": oData.taxPeriodFrom,
					"taxPeriodTo": oData.taxPeriodTo,
					"transType": [oData.transType],
					"gstins": oData.gstins
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			var oModel = this.getView().getModel("GLProcessEntity");
			if (oModel) {
				oModel.setProperty("/", []);
				oModel.refresh(true);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/glProcessedSummary.do",
					data: JSON.stringify(Payload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "GLProcessEntity");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Download Process Summary Table Data
		 */
		onDownloadGLPRTrans: function (oEvt) {
			var oModelForTab1 = this.byId("idTabGLPS").getModel("GLProcessEntity").getProperty("/"),
				oSelectedItem = this.getView().byId("idTabGLPS").getSelectedIndices();

			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("PLease Select at least one Gstin");
				return;
			}

			var aGstin = oSelectedItem.map(function (e) {
				return oModelForTab1[e].gstin;
			});

			var oData = this.getView().getModel("psfilter").getData();
			var oPayload = {
				"req": {
					"gstins": aGstin,
					"taxPeriodFrom": oData.taxPeriodFrom,
					"taxPeriodTo": oData.taxPeriodTo,
					"transactionType": oData.transType
				}
			};

			this.reportDownload(oPayload, "/aspsapapi/downloadGlConsolidatedReports.do");
		},

		onDownloadGLPS: function (oEvt) {
			var data = [{
				sheet: "GL Processed Summary",
				cols: this._getColumns(),
				rows: this._getDataSource(),
			}];
			this.xlsxTabDataDownload("GL Processed Data_Entity Level Summary_" + this._getExcelTimeStamp() + ".xlsx", data);
		},

		_getColumns: function () {
			return [{
				key: "gstin",
				header: "GSTIN",
				width: 18
			}, {
				key: "count",
				header: "Count of Records",
				width: 18
			}, {
				key: "assessableAmt",
				header: "Assessable Amount",
				width: 18
			}, {
				key: "igst",
				header: "IGST",
				width: 12
			}, {
				key: "cgst",
				header: "CGST",
				width: 12
			}, {
				key: "sgst",
				header: "SGST",
				width: 12
			}, {
				key: "cess",
				header: "Cess",
				width: 12
			}, ];
		},

		_getDataSource: function () {
			var oTabData = this.getView().getModel("GLProcessEntity").getProperty("/"),
				aIndex = this.byId("idTabGLPS").getSelectedIndices();
			if (!aIndex.length) {
				return $.extend(true, [], oTabData);
			}
			return aIndex.map(function (e) {
				return oTabData[e];
			});
		},
	});
});