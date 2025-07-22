sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.PDFReader", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.PDFReader
		 */
		onInit: function () {

		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onTabBind();
			}
		},

		onUpload: function () {
			var oFileUploader = this.byId("FileUploadid");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			oFileUploader.setUploadUrl("/aspsapapi/uploadPdfReaderFile.do");
			oFileUploader.removeAllParameters();
			oFileUploader.addParameter(
				new sap.ui.unified.FileUploaderParameter({
					name: "entityId",
					value: $.sap.entityID
				})
			);
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var oRespnse = oEvent.getParameters().responseRaw,
				status = JSON.parse(oRespnse);
			if (status.hdr.status === "S") {
				this.getView().byId("FileUploadid").setValue("");
				sap.m.MessageBox.success("File Uploaded Successfully", {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				sap.m.MessageBox.error(status.resp, {
					styleClass: "sapUiSizeCompact"
				});
			}
			this.onTabBind();
		},

		onTabBind: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.get("/aspsapapi/getPdfUploadStatus.do", {
					"entityId": $.sap.entityID
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "PDFCode");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onRefresh: function () {
			this.onTabBind();
		},
		DownloadFile: function (oEvent) {
			var fileId = oEvent.getSource().getEventingParent().getBindingContext("PDFCode").getObject().fileId,
				Request = {
					"entityId": $.sap.entityID,
					"fileId": fileId
				};
			this.excelDownload(Request, "/aspsapapi/generatePdfReaderReport.do");
		},

	});

});