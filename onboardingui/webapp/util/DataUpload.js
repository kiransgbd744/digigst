sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageToast",
	"sap/m/UploadCollectionParameter"
], function (JSONModel, MessageToast, UploadCollectionParameter) {
	"use strict";

	return {
		_fnDataReceived: function (oView) {
			var vDateRange = oView.byId("drsReceive").getValue();
			var vGSTIN = oView.byId("mcbGSTIN").getSelectedKeys();
			if (!vDateRange) {
				return;
			}
			var oJsonData = [],
				vFromDate = vDateRange.split("-")[0].trim(),
				vToDate = vDateRange.split("-")[1].trim(),
				oInvoiceData = oView.getOwnerComponent().getModel("Invoices").getData();
			var oConsData = {
				"SlNo": "C",
				"Date": "",
				"EY": {
					"Records": "",
					"Taxable": "",
					"Amount": ""
				},
				"ERP": {
					"Records": "",
					"Taxable": "",
					"Amount": ""
				},
				"Diff": {
					"Records": "",
					"Taxable": "",
					"Amount": ""
				}
			};
			for (var i = 0; i < oInvoiceData.receive.length; i++) {
				oJsonData.push(oInvoiceData.receive[i]);
				// if (oConsData.Date === "") {
				// 	oConsData.Date = oInvoiceData.receive[i].Date;
				// } else if (i === oInvoiceData.receive.length - 1) {
				// 	oConsData.Date = oInvoiceData.receive[i].Date + " To " + oConsData.Date;
				// }
				oConsData.EY.Records = (oConsData.EY.Records === "" ? 0 : parseInt(oConsData.EY.Records, 10)) + (oInvoiceData.receive[i].EY.Records ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].EY.Records, 10));
				oConsData.EY.Taxable = (oConsData.EY.Taxable === "" ? 0 : parseInt(oConsData.EY.Taxable, 10)) + (oInvoiceData.receive[i].EY.Taxable ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].EY.Taxable, 10));
				oConsData.EY.Amount = (oConsData.EY.Amount === "" ? 0 : parseInt(oConsData.EY.Amount, 10)) + (oInvoiceData.receive[i].EY.Amount ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].EY.Amount, 10));
				oConsData.ERP.Records = (oConsData.ERP.Records === "" ? 0 : parseInt(oConsData.ERP.Records, 10)) + (oInvoiceData.receive[i].ERP.Records ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].ERP.Records, 10));
				oConsData.ERP.Taxable = (oConsData.ERP.Taxable === "" ? 0 : parseInt(oConsData.ERP.Taxable, 10)) + (oInvoiceData.receive[i].ERP.Taxable ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].ERP.Taxable, 10));
				oConsData.ERP.Amount = (oConsData.ERP.Amount === "" ? 0 : parseInt(oConsData.ERP.Amount, 10)) + (oInvoiceData.receive[i].ERP.Amount ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].ERP.Amount, 10));
				oConsData.Diff.Records = (oConsData.Diff.Records === "" ? 0 : parseInt(oConsData.Diff.Records, 10)) + (oInvoiceData.receive[i].Diff
					.Records === "" ? 0 : parseInt(oInvoiceData.receive[i].Diff.Records, 10));
				oConsData.Diff.Taxable = (oConsData.Diff.Taxable === "" ? 0 : parseInt(oConsData.Diff.Taxable, 10)) + (oInvoiceData.receive[i].Diff
					.Taxable === "" ? 0 : parseInt(oInvoiceData.receive[i].Diff.Taxable, 10));
				oConsData.Diff.Amount = (oConsData.Diff.Amount === "" ? 0 : parseInt(oConsData.Diff.Amount, 10)) + (oInvoiceData.receive[i].Diff.Amount ===
					"" ? 0 : parseInt(oInvoiceData.receive[i].Diff.Amount, 10));
			}
			oJsonData.unshift(oConsData);
			var oModel = new JSONModel(oJsonData);
			oView.byId("tabReceive").setModel(oModel, "Receive");
			oView.byId("tabReceive").getModel().refresh(true);
			oView.byId("tabReceive").setVisible(true);
		},

		/*==============================================================*/
		/*============= Upload =========================================*/
		/*==============================================================*/
		onFileSizeExceed: function (oEvent) {
			MessageToast.show("FileSizeExceed event triggered.");
		},

		onTypeMissmatch: function (oEvent) {
			MessageToast.show("TypeMissmatch event triggered.");
		},

		onUploadChange: function (oEvent) {
			var oUploadCollection = oEvent.getSource();
			// Header Token
			var oCustomerHeaderToken = new UploadCollectionParameter({
				name: "x-csrf-token",
				value: "securityTokenFromModel"
			});
			oUploadCollection.addHeaderParameter(oCustomerHeaderToken);
		},

		onUploadComplete: function (oEvent, oView) {
			// If the upload is triggered by a new version, this function updates the metadata of the old file and deletes the progress indicator once the upload was finished.
			var oData = oView.byId("ucWebUpload").getModel().getData();
			var aItems = jQuery.extend(true, {}, oData).items;
			var oItem = {};
			var sUploadedFile = oEvent.getParameter("files")[0].fileName;
			// at the moment parameter fileName is not set in IE9
			if (!sUploadedFile) {
				var aUploadedFile = (oEvent.getParameters().getSource().getProperty("value")).split(/\" "/);
				sUploadedFile = aUploadedFile[0];
			}
			oItem = {
				"documentId": jQuery.now().toString(), // generate Id,
				"fileName": sUploadedFile,
				"mimeType": "",
				"thumbnailUrl": "",
				"url": "",
				"attributes": [{
					"title": "Uploaded By",
					"text": "You"
				}, {
					"title": "Uploaded On",
					"text": new Date(jQuery.now()).toLocaleDateString()
				}, {
					"title": "File Size",
					"text": "505000"
				}, {
					"title": "Version",
					"text": "1"
				}]
			};
			aItems.unshift(oItem);
			this.byId("UploadCollection").getModel().setData({
				"items": aItems
			});
			// Sets the text to the label
			this.byId("attachmentTitle").setText(this.getAttachmentTitleText());
		},

		onBeforeUploadStarts: function (oEvent) {
			// Header Slug
			var oCustomerHeaderSlug = new UploadCollectionParameter({
				name: "slug",
				value: oEvent.getParameter("fileName")
			});
			oEvent.getParameters().addHeaderParameter(oCustomerHeaderSlug);
		},

		getAttachmentTitleText: function () {
			var aItems = this.byId("UploadCollection").getItems();
			return "Uploaded (" + aItems.length + ")";
		},

		onDownloadItem: function () {
			var oUploadCollection = this.byId("UploadCollection");
			var aSelectedItems = oUploadCollection.getSelectedItems();
			if (aSelectedItems) {
				for (var i = 0; i < aSelectedItems.length; i++) {
					oUploadCollection.downloadItem(aSelectedItems[i], true);
				}
			} else {
				MessageToast.show("Select an item to download");
			}
		},

		onSelectionChange: function () {
			var oUploadCollection = this.byId("UploadCollection");
			// If there's any item selected, sets download button enabled
			if (oUploadCollection.getSelectedItems().length > 0) {
				this.byId("downloadButton").setEnabled(true);
				if (oUploadCollection.getSelectedItems().length === 1) {
					this.byId("versionButton").setEnabled(true);
				} else {
					this.byId("versionButton").setEnabled(false);
				}
			} else {
				this.byId("downloadButton").setEnabled(false);
				this.byId("versionButton").setEnabled(false);
			}
		},

		updateFile: function () {
			var oData = this.byId("UploadCollection").getModel().getData();
			var aItems = jQuery.extend(true, {}, oData).items;
			// Adds the new metadata to the file which was updated.
			for (var i = 0; i < aItems.length; i++) {
				if (aItems[i].documentId === this.oItemToUpdate.getDocumentId()) {
					// Uploaded by
					aItems[i].attributes[0].text = "You";
					// Uploaded on
					aItems[i].attributes[1].text = new Date(jQuery.now()).toLocaleDateString();
					// Version
					var iVersion = parseInt(aItems[i].attributes[3].text, 10);
					iVersion++;
					aItems[i].attributes[3].text = iVersion;
				}
			}
			// Updates the model.
			this.byId("UploadCollection").getModel().setData({
				"items": aItems
			});
			// Sets the flag back to false.
			this.bIsUploadVersion = false;
			this.oItemToUpdate = null;
		}
	};
});