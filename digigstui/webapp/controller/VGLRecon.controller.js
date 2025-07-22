sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	'sap/m/MessageBox',
	"sap/m/MessageToast"
], function (Controller, JSONModel, Fragment, MessageBox, MessageToast) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.VGLRecon", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.VGLRecon
		 */
		onInit: function () {
			this.getView().setModel(new JSONModel({
				"segBtn": "pGLRecon",
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 10,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false,
				"viewFileType": 0,
				"viewType": "glCode"
			}), "Property");
			// this._bindMasterDataView();
			this._getMasterData();
		},

		_bindMasterDataView: function () {
			var oModel = new JSONModel();
			oModel.loadData("model/GLRecon.json");
			oModel.attachRequestCompleted(function () {
				var oData = oModel.getProperty("/");
				this.getView().setModel(new JSONModel(oData.glCode), "GLCodeMaster");
				this.getView().setModel(new JSONModel(oData.busiUnit), "BusinessUnit");
				this.getView().setModel(new JSONModel(oData.docType), "DocumentType");
				this.getView().setModel(new JSONModel(oData.suppType), "SupplyType");
				this.getView().setModel(new JSONModel(oData.taxCode), "TaxCodeMaster");
				this.getView().setModel(new JSONModel(oData.glMapping), "GLMapping");
			}.bind(this));
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.VGLRecon
		 */
		onAfterRendering: function () {
			if ($.sap.entityID && this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._getFileStatus();
			}
		},

		_getFileStatus: function (flag) {
			var oModel = this.getView().getModel("FileStatus"),
				oPropModel = this.getView().getModel("Property"),
				oDataProp = oPropModel.getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (oDataProp.pageNo || 1) - 1,
						"pageSize": +oDataProp.pgSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"flag": true
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

			sap.ui.core.BusyIndicator.show(0);
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
					this.getView().setModel(new JSONModel(data.resp), "FileStatus");
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
				"type": "masterData",
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

		onDownloadTemplate: function () {
			var idx = this.byId("rgbFileType").getSelectedIndex();
			switch (idx) {
			case 0:
				var fileName = "MasterFile_GL_code.xlsx";
				break;
			case 1:
				fileName = "MasterFile_BusinessUnit.xlsx";
				break;
			case 2:
				fileName = "MasterFile_DocumentType.xlsx";
				break;
			case 3:
				fileName = "MasterFile_SupplyType.xlsx";
				break;
			case 4:
				fileName = "MasterFile_TaxCode.xlsx";
				break;
			}
			sap.m.URLHelper.redirect("excel/glRecon/" + fileName, false);
		},

		onPressUpload: function () {

			var oFileUploader = this.getView().byId("fileUploaderGL"),
				sKey = this.getView().getModel("Property").getProperty("/segBtn");

			if (!oFileUploader.getValue()) {
				sap.m.MessageBox.warning("Please select file to upload.");
				return;
			}
			switch (this.byId("rgbFileType").getSelectedIndex()) {
			case 0:
				var keyValue = "GL Code Master";
				break;
			case 1:
				keyValue = "Business Unit";
				break;
			case 2:
				keyValue = "Document Type";
				break;
			case 3:
				keyValue = "Supply Type";
				break;
			case 4:
				keyValue = "Tax Code Master";
				break;
			}
			var url = "/aspsapapi/glMasterFileUploadDocuments.do";
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.setAdditionalData($.sap.entityID + "*" + keyValue);
			oFileUploader.setUploadUrl(url);
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

		onDownloadFailedFile: function (fileId, fileType) {
			var payload = {
				"req": {
					"entityId": [
						$.sap.entityID
					],
					"FileId": fileId,
					"FileType": fileType
				}
			}
			var vUrl = "/aspsapapi/downloadFailedMasterUploads.do";
			this.excelDownload(payload, vUrl);
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

		onDialogAfterRendering: function (oEvent) {
			oEvent.srcControl.$().find("input").attr("readonly", true);
		},

		onViewMasterData: function () {
			var oPropModel = this.getView().getModel("Property");
			oPropModel.setProperty("/viewType", this._getViewType(oPropModel.getProperty("/viewFileType")));
			oPropModel.refresh(true);

			this._getMasterData();
		},

		_getViewType: function (idx) {
			switch (idx) {
			case 0:
				return "glCode";
			case 1:
				return "busiUnit";
			case 2:
				return "docType";
			case 3:
				return "suppType";
			case 4:
				return "taxCode";
			case 5:
				return "glMapping";
			}
		},

		_getModelName: function () {
			var idx = this.getView().getModel("Property").getProperty("/viewFileType");
			switch (idx) {
			case 0:
				return "GLCodeMaster";
				break;
			case 1:
				return "BusinessUnit";
				break;
			case 2:
				return "DocumentType";
				break;
			case 3:
				return "SupplyType";
				break;
			case 4:
				return "TaxCodeMaster";
				break;
			case 5:
				return "GLMapping";
				break;
			}
		},

		_getFileType: function () {
			var idx = this.getView().getModel("Property").getProperty("/viewFileType");
			switch (idx) {
			case 0:
				return "GL Code Master";
				break;
			case 1:
				return "Business Unit";
				break;
			case 2:
				return "Document Type";
				break;
			case 3:
				return "Supply Type";
				break;
			case 4:
				return "Tax Code Master";
				break;
			case 5:
				return "GL Mapping";
				break;
			}
		},

		_getMasterData: function () {
			var model = this._getModelName(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"fileType": this._getFileType()
					}
				};
			var oModel = this.getView().getModel(model);
			sap.ui.core.BusyIndicator.show(0);
			if (oModel) {
				oModel.setProperty("/", []);
				oModel.refresh(true);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getMasterFileData.do",
					data: JSON.stringify(payload),
					contentType: "application/json"
				})
				.done(function (data) {

					sap.ui.core.BusyIndicator.hide();
					if (data.resp.message == undefined) {
						data.resp.forEach(function (item) {
							item.edit = false; // Set edit property to false
							// item.editMandit = false;
						});
						this.getView().setModel(new JSONModel(data.resp), model);
					} else {
						this.getView().setModel(new JSONModel([]), model);
					}

				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), model);
				}.bind(this));
		},

		_getMasterObject: function (idx) {
			var idx = this.getView().getModel("Property").getProperty("/viewFileType");
			switch (idx) {
			case 0:
				return {
					"edit": true,
					// "editMandit": true,
					// "id": null,
					"cgstTaxGlCode": null,
					"sgstTaxGlCode": null,
					"igstTaxGlCode": null,
					"ugstTaxGlCode": null,
					"compensationCessGlCode": null,
					"keralaCessGlCode": null,
					"revenueGls": null,
					"expenceGls": null,
					"exchangeRate": null,
					// "diffGl": null,
					"exportGl": null,
					"forexGlsPor": null,
					"taxableAdvanceLiabilityGls": null,
					"nonTaxableAdvanceLiabilityGls": null,
					"ccAndStGls": null,
					"unbilledRevenueGls": null,
					"bankAccGls": null,
					"inputTaxGls": null,
					"fixedAssetGls": null
				};
			case 1:
				return {
					"edit": true,
					// "editMandit": true,
					// "id": null,
					"businessPlace": "",
					"businessArea": "",
					"plantCode": "",
					"profitCentre": "",
					"costCentre": "",
					"gstin": ""
				};
			case 2:
				return {
					"edit": true,
					// "editMandit": true,
					// "id": null,
					"docType": "",
					"docTypeMs": ""
				};
			case 3:
				return {
					"edit": true,
					// "editMandit": true,
					// "id": null,
					"supplyTypeReg": "",
					"supplyTypeMs": ""
				};

			case 4:
				return {
					"edit": true,
					// "editMandit": true,
					// "id": null,
					"transactionTypeGl": "",
					"taxCodeDescriptionMs": "",
					"taxTypeMs": "",
					"eligibilityMs": "",
					"taxRateMs": null
				};
			case 5:
				return "glMapping";
			}
		},

		onAdd: function () {
			// Get the model
			var model = this._getModelName();
			var oModel = this.getView().getModel(model);
			// Create a new entry
			var oNewEntry = this._getMasterObject();
			// Add the new entry to the model
			var aData = oModel.getData();
			aData.unshift(oNewEntry);
			oModel.setData(aData);
			//	MessageToast.show("Entry added successfully!");
		},
		onEdit: function () {
			var model = this._getModelName();
			var tab = this.getView().byId(model); // Get the table by ID
			var selectedIndices = tab.getSelectedIndices(); // Get selected indices
			var oJSONModel = tab.getModel(model); // Get the model
			var itemList = oJSONModel.getProperty("/"); // Get the data array

			// if (["DocumentType", "SupplyType"].includes(model)) {
			// 	MessageToast.show("Editing is not allowed for the selected Table.");
			// 	return;
			// }
			// Check if any rows are selected
			if (selectedIndices.length === 0) {
				MessageToast.show("Please select at least one row to edit.");
				return;
			}

			// Set the edit property to true for each selected item
			for (var i = 0; i < selectedIndices.length; i++) {
				var index = selectedIndices[i];
				itemList[index].edit = true; // Set edit mode
			}

			// Update the model with the modified data
			oJSONModel.setProperty("/", itemList); // Update the model

			// MessageToast.show("Edit mode enabled for selected rows.");
		},

		onDelete: function () {
			var model = this._getModelName();
			var tab = this.getView().byId(model); // Get the table by ID
			var selectedIndices = tab.getSelectedIndices(); // Get selected indices
			var oJSONModel = tab.getModel(model); // Get the model
			var itemList = oJSONModel.getProperty("/"); // Get the data array

			// Check if any rows are selected
			if (selectedIndices.length === 0) {
				MessageToast.show("Please select at least one row to delete.");
				return;
			}

			// Show confirmation message
			sap.m.MessageBox.show("Do you want to delete the selected records?", {
				icon: sap.m.MessageBox.Icon.QUESTION,
				title: "Confirm Deletion",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
						if (oAction === sap.m.MessageBox.Action.NO) {
							// User chose not to delete, simply return
							return;
						} else if (oAction === sap.m.MessageBox.Action.YES) {
							// Proceed with deletion
							var idsToDelete = [];

							// Remove selected records from the model
							for (var i = selectedIndices.length - 1; i >= 0; i--) {
								var index = selectedIndices[i]; // Get the selected index
								var selectedItem = itemList[index]; // Get the selected item

								// Check if the ID is defined
								if (selectedItem.id !== null) {
									idsToDelete.push({
										'id': selectedItem.id
									}); // Collect IDs for backend deletion
								}

								itemList.splice(index, 1); // Remove the item from the array
							}

							// Update the model with the remaining records
							oJSONModel.setProperty("/", itemList);

							// If there are IDs to delete, send them to the backend
							if (idsToDelete.length > 0) {
								var postData = {
									// "req": {
									"entityId": $.sap.entityID,
									"fileType": this._getFileType(),
									"delete": idsToDelete
										// }
								};

								var jsonForSearch = JSON.stringify(postData);
								var deleteUserInfo = "/aspsapapi/deleteMasterFileData.do";
								sap.ui.core.BusyIndicator.show(0);
								$.ajax({
										method: "POST",
										url: deleteUserInfo,
										contentType: "application/json",
										data: jsonForSearch
									})
									.done(function (data, status, jqXHR) {
										sap.ui.core.BusyIndicator.hide();
										if (data.resp.status === 'Success') {
											MessageBox.success(data.resp.message);
											// Refresh data if needed
										} else {
											MessageBox.error(data.resp.message);
										}
										this._getMasterData();
									}.bind(this))
									.fail(function (jqXHR, status, err) {
										sap.ui.core.BusyIndicator.hide();
										this._serverMessage(jqXHR);
									}.bind(this));
							} else {
								MessageToast.show("No records with defined IDs to submit.");
							}
						}
					}.bind(this) // Bind the context to access `this` inside the callback
			});
		},

		onSave: function () {
			var model = this._getModelName();
			var tab = this.getView().byId(model); // Get the table by ID
			var selectedIndices = tab.getSelectedIndices(); // Get selected indices
			var oJSONModel = tab.getModel(model); // Get the model
			var itemList = oJSONModel.getProperty("/"); // Get the data array

			// Create a new object to break the link
			var newItemList = JSON.parse(JSON.stringify(itemList));

			// Check if any rows are selected
			if (selectedIndices.length === 0) {
				MessageToast.show("Please select at least one row to save.");
				return;
			}

			// Prepare data for submission
			var dataToSave = [];
			for (var i = 0; i < selectedIndices.length; i++) {
				var index = selectedIndices[i]; // Get the selected index
				var selectedItem = newItemList[index]; // Get the selected item
				// if (selectedItem.editMandit) {
				// debugger;
				if (model == "GLCodeMaster") {
					var isValid = this._validateFields(selectedItem);
					if (!isValid) {
						MessageToast.show("At least one field must be filled.");
						return;
					}
				}

				if (this._checkMandatory(selectedItem)) {
					MessageToast.show("Please fill in all mandatory fields.");
					return;
				}
				// }

				// Check if the item has the necessary properties to be saved
				delete selectedItem.edit;
				// delete selectedItem.editMandit;
				// selectedItem.entityId = $.sap.entityID;
				dataToSave.push(selectedItem);
			}

			// Check if there is data to save
			if (dataToSave.length === 0) {
				MessageToast.show("No records with defined IDs to save.");
				return;
			}

			// Show confirmation message before saving
			sap.m.MessageBox.show("Do you want to save the selected records?", {
				icon: sap.m.MessageBox.Icon.QUESTION,
				title: "Confirm Save",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
						if (oAction === sap.m.MessageBox.Action.NO) {
							// User chose not to save, simply return
							return;
						} else if (oAction === sap.m.MessageBox.Action.YES) {
							// Proceed with saving
							var postData = [{
								"entityId": $.sap.entityID,
								"fileType": this._getFileType(),
								"data": dataToSave // Prepare the data for saving
							}];

							var jsonForSearch = JSON.stringify(postData);
							var saveUserInfo = "/aspsapapi/saveMasterFileData.do"; // Replace with your API endpoint
							sap.ui.core.BusyIndicator.show(0);
							$.ajax({
									method: "POST",
									url: saveUserInfo,
									contentType: "application/json",
									data: jsonForSearch
								})
								.done(function (data, status, jqXHR) {
									sap.ui.core.BusyIndicator.hide();
									if (data.resp.status === 'Success') {
										MessageBox.success(data.resp.message);
										this._getMasterData(); // Refresh data if needed
									} else {
										MessageBox.error(data.resp.message);
										// this._getMasterData();
									}
								}.bind(this))
								.fail(function (jqXHR, status, err) {
									sap.ui.core.BusyIndicator.hide();
									this._serverMessage(jqXHR);
								}.bind(this));
						}
					}.bind(this) // Bind the context to access `this` inside the callback
			});
		},
		_validateFields: function (data) {
			// Remove unwanted properties
			delete data.edit;
			// delete data.editMandit;

			// Flag to check if at least one field is not null or empty
			var hasNonNullField = false;

			// Iterate through the properties of the object
			for (var key in data) {
				if (data.hasOwnProperty(key)) {
					// Check if the value is not null and not an empty string
					if (![null, ""].includes(data[key])) {
						hasNonNullField = true; // Found a non-null and non-empty field
						break; // No need to check further
					}
				}
			}

			return hasNonNullField; // Return true if at least one field is not null or empty, otherwise false
		},

		_checkMandatory: function (data) {
			var idx = this.getView().getModel("Property").getProperty("/viewFileType");

			switch (idx) {
			case 0:
				return false; // No mandatory fields for case 0

			case 1:
				return (data.gstin === "" || data.gstin === null); // Return true if gstin is empty or null

			case 2:
				return (data.docType === "" || data.docType === null ||
					data.docTypeMs === "" || data.docTypeMs === null); // Return true if either field is empty or null

			case 3:
				return (data.supplyTypeReg === "" || data.supplyTypeReg === null ||
					data.supplyTypeMs === "" || data.supplyTypeMs === null); // Return true if either field is empty or null

			case 4:
				return (data.transactionTypeGl === "" || data.transactionTypeGl === null ||
					data.taxRateMs === "" || data.taxRateMs === null); // Return true if either field is empty or null

			default:
				return false; // Default case if idx does not match any case
			}
		},

		// _setReadonly: function (vId) {
		// 	var obj = this.byId(vId)
		// 	obj.addEventDelegate({
		// 		onAfterRendering: this.onDialogAfterRendering.bind(this)
		// 	});
		// },

		// _formatTaxPeriod: function (value) {
		// 	if (!value) {
		// 		return null;
		// 	}
		// 	var value = "" + value;
		// 	if (+value.substr(0, 4) > 2100) {
		// 		return value;
		// 	}
		// 	var date = new Date(value.slice(0, 4), (+value.slice(4) - 1), '01');
		// 	return date.toLocaleString('default', {
		// 		month: 'short',
		// 		year: 'numeric'
		// 	});
		// }
	});
});