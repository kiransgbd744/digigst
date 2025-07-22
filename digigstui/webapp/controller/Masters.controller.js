sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Masters", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Masters
		 */
		onInit: function () {
			this._bindDefaultUploadDate();
			this.getOwnerComponent().getRouter().getRoute("Masters").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function () {
			this.getView().byId("sbUpload").setSelectedKey("upload");
			var aJson = {
				"WebUpload": true,
				"FileStatus": false,
				"master": false
			};
			this.getView().setModel(new JSONModel(aJson), "oVisiModel");
			this.getView().getModel("oVisiModel").refresh(true);
		},

		_bindDefaultUploadDate: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			this.byId("iFromPeriod").setDateValue(vDate);
			this.byId("iFromPeriod").setMaxDate(new Date());

			this.byId("iToPeriod").setDateValue(new Date());
			this.byId("iToPeriod").setMinDate(vDate);
			this.byId("iToPeriod").setMaxDate(new Date());

			this.getView().setModel(new JSONModel({
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 100,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false
			}), "Property");
		},

		onChangeSegment: function (oEvent) {
			var oModel = this.getView().getModel("oVisiModel"),
				key = oEvent.getSource().getSelectedKey();

			oModel.setProperty("/WebUpload", key === "upload");
			oModel.setProperty("/FileStatus", key === "status");
			oModel.setProperty("/master", key === "master");

			switch (key) {
			case "upload":
				break;
			case "status":
				this.onSearch();
				break;
			case "master":
				this._getVendorMasterData();
				break;
			}
			this.getView().getModel("oVisiModel").refresh(true);
		},

		onPressDownload: function (oEvent) {
			var vIdMain = this.byId("rgCategory").getSelectedIndex();
			if (vIdMain == 0) {
				var vId = this.byId("rgDataTyGS").getSelectedIndex();
				if (vId == 0) {
					var vfId = this.byId("rgFileTypeIn").getSelectedIndex();
					if (vfId == 0) {
						sap.m.URLHelper.redirect("excel/Recipient_Master.xlsx", true);
					}
				}
			}
		},

		onPressUpload: function (oEvent) {
			var oFileUploader = this.byId("fileUploader");
			// vIdx = this.byId("rgDataType").getSelectedIndex(),
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			var vIdMain = this.byId("rgCategory").getSelectedIndex();

			if (vIdMain == 0) {
				var vId = this.byId("rgDataTyGS").getSelectedIndex();
				if (vId == 0) {
					var vfId = this.byId("rgFileTypeIn").getSelectedIndex();
					if (vfId == 0) {
						oFileUploader.setUploadUrl("/aspsapapi/recipientMasterUpload.do");
					}
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

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

		onSearch: function () {
			var oView = this.getView();
			var vDataType = this.getView().byId("idDataType").getSelectedKey(),
				vFileType = this.getView().byId("idFileType").getSelectedKey(),
				vFromdate = this.getView().byId("iFromPeriod").getValue(),
				vTodate = this.getView().byId("iToPeriod").getValue();

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
					"fileType": vFileType
				}
			};

			this.getView().setModel(new JSONModel([]), "oFileStatus");
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getRecipientUploadedInfo.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					if (data.resp === undefined || data.resp.length === 0) {
						MessageBox.information("No Data");
						oView.getModel("oFileStatus").setData([]);
						oView.getModel("oFileStatus").refresh(true);
					} else {
						oView.getModel("oFileStatus").setData(data);
						oView.getModel("oFileStatus").refresh(true);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("fileStatus : Error");
					var oFileStatusData = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oFileStatusData, "FileStatus");
				}.bind(this));
		},

		onVerticalDownload: function (oEvent, type, count) {
			var obj = oEvent.getSource().getEventingParent().getParent().getBindingContext("oFileStatus").getObject();
			if (obj.fileType == "RECIPIENT_MASTER") {
				var vUrl = "/aspsapapi/getRecipientMasterErrReport.do";
				if (type == "error") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"flagofRecord": "errorrecords",
						}
					};
					this.excelDownload(aData, vUrl);
				} else if (type == "processedinfo") {
					var aData = {
						"req": {
							"fileId": obj.id,
							"flagofRecord": "informationrecords",
						}
					};
					this.excelDownload(aData, vUrl);
				}
			}
		},

		_getVendorMasterData: function () {
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getRecipientPAN(),
					this._getRecipientGSTN(),
					this._getRecipientMaster(0, true)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function () {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getRecipientPAN: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecipientPanInfo.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.unshift({
							gstin: "All"
						});
						this.getView().setModel(new JSONModel(data.resp), "recipientPanData");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getRecipientGSTN: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecipientGstnInfo.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.unshift({
							recipientGstin: "All"
						});
						this.getView().setModel(new JSONModel(data.resp), "venGstinData");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPrsSearchVenMstr: function (vPageNo) {
			this._getRecipientMaster(0);
		},

		_getRecipientMaster: function (vPageNo, flag) {
			var payload = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": 100,
				},
				"req": {
					"entityId": $.sap.entityID,
					"recipientPan": this.removeAll(this.getView().byId("idVURecptPAN").getSelectedKeys()),
					"recipientGstin": this.removeAll(this.getView().byId("idVUVendrGstn").getSelectedKeys())
				}
			};

			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecipientMasterData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (!flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						this._paginationRecipientMaster(data.hdr);
						this.getView().byId("idMasterRecipt").setModel(new JSONModel(data.resp.recipientMasterData), "oReciMstrTabData");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (!flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_paginationRecipientMaster: function (header) {
			var oModel = this.getView().getModel("Property"),
				vTotal = Math.ceil(header.totalCount / oModel.getProperty("/pgSize")),
				vPageNo = (vTotal ? header.pageNum + 1 : 0);

			oModel.setProperty("/pageNo", vPageNo);
			oModel.setProperty("/pgTotal", vTotal);
			oModel.setProperty("/ePageNo", vTotal > 1);
			oModel.setProperty("/bFirst", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onPressPagination: function (btn) {
			var oModel = this.getView().getModel("Property");
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
			this._getRecipientMaster(oModel.getProperty("/pageNo"));
		},

		onSubmitPagination: function () {
			var oModel = this.getView().getModel("Property");

			if (+oModel.getProperty("/pageNo") > +oModel.getProperty("/pgTotal")) {
				oModel.setProperty("/pageNo", +oModel.getProperty("/pgTotal"));
			}
			oModel.refresh(true);
			this._getRecipientMaster(oModel.getProperty("/pageNo"));
		},

		onDwnldTablDataVM: function () {
			var oDwnldTabDataVMReq = {
				"req": {
					"entityId": $.sap.entityID,
					"recipientPan": this.removeAll(this.getView().byId("idVURecptPAN").getSelectedKeys()),
					"vendorGstin": this.removeAll(this.getView().byId("idVUVendrGstn").getSelectedKeys())
				}
			};
			this.excelDownload(oDwnldTabDataVMReq, "/aspsapapi/getVendorMasterReport.do");
		}
	});
});