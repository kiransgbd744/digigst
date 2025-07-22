sap.ui.define([
	"jquery.sap.global",
	'com/ey/onboarding/controller/BaseController',
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	'sap/m/MessageBox',
	'sap/m/Token',
	'sap/ui/export/Spreadsheet'
], function (jQuery, BaseController, Controller, JSONModel, MessageBox, Token, Spreadsheet) {
	"use strict";

	return BaseController.extend("com.ey.onboarding.controller.EINVCofig", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.onboarding.view.EINVCofig
		 */
		onInit: function () {
			var aData = [{
				"sno": 1,
				"gstin": "29AAAPH9357H1A2",
				"terms": "Condition1"

			}]
			this.getView().setModel(new JSONModel(aData), "terms");
		},

		onSelectionChangeEntity: function (oEvent) {
			//eslint-disable-line
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idEntityTS") > -1) {
				$.sap.entityId = this.byId("idEntityTS").getSelectedKey();
				this.bindTemplateSelection($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityBD") > -1) {
				$.sap.entityId = this.byId("idEntityBD").getSelectedKey();
				this.bindBankDetails($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityTC") > -1) {
				$.sap.entityId = this.byId("idEntityTC").getSelectedKey();
				this.bindTermAndCond($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityLOGO") > -1) {
				$.sap.entityId = this.byId("idEntityLOGO").getSelectedKey();
				this.bindLogoFileUpload($.sap.entityId);
			}
		},
		handleIconTabBarSelect: function (oEvent) {
			debugger;
			if (oEvent.getParameters().selectedKey === "TemplateSelection") {
				this.getTemplateSelection();
			} else if (oEvent.getParameters().selectedKey === "BankDetails") {
				this.getBankDetails();
			} else if (oEvent.getParameters().selectedKey === "TermsCondition") {
				this.getTermAndCond();
			} else if (oEvent.getParameters().selectedKey === "Logo") {
				this.getLogo();
			}
		},

		getLogo: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var LogoFileUpload = "/SapOnboarding/getLogoFileUpload.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: LogoFileUpload,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntity = new JSONModel(data);
					oEntity.setSizeLimit(2000);
					oView.setModel(oEntity, "logoEntity");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityLOGO").setSelectedKey($.sap.entityId);
					that.bindLogoFileUpload($.sap.entityId);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
		},

		bindLogoFileUpload: function (entityID) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var data = oView.getModel("logoEntity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idLOGOSubmit").setEnabled(false);
			} else {
				this.getView().byId("idLOGOSubmit").setEnabled(true);

			}
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					debugger;
					if (data.resp[i].logoType.split(".")[1] == "png") {
						var img = "data:image/png;base64,"
					} else {
						var img = "data:image/jpeg;base64,"
					}
					img = img + data.resp[i].logoFile;
					this.getView().byId("image").setSrc(img);
					break;
				}
			}
		},

		getTemplateSelection: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var CompanyCodeMapping = "/SapOnboarding/getTemplateSelection.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: CompanyCodeMapping,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntity = new JSONModel(data);
					oEntity.setSizeLimit(2000);
					oView.setModel(oEntity, "tsEntity");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityTS").setSelectedKey($.sap.entityId);
					that.bindTemplateSelection($.sap.entityId);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
		},

		bindTemplateSelection: function (entityID) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var data = oView.getModel("tsEntity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idTSSubmit").setEnabled(false);
				var oTemplateSelection = new JSONModel();
				oTemplateSelection.setData();
				oView.setModel(oTemplateSelection, "TemplateSelection");
				return;

			} else {
				this.getView().byId("idTSSubmit").setEnabled(true);

			}
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].item == undefined) {
						var oTemplateSelection = new JSONModel();
						oTemplateSelection.setData();
						oView.setModel(oTemplateSelection, "TemplateSelection");
						return;
					}
					var oLength = data.resp[i].item.length;
					aData = data.resp[i]
					break;
				}
			}

			var oTemplateSelection = new JSONModel();
			oTemplateSelection.setData(aData);
			oView.setModel(oTemplateSelection, "TemplateSelection");

		},

		getBankDetails: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var CompanyCodeMapping = "/SapOnboarding/getbankDetails.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: CompanyCodeMapping,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntity = new JSONModel(data);
					oEntity.setSizeLimit(2000);
					oView.setModel(oEntity, "bdEntity");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityBD").setSelectedKey($.sap.entityId);
					that.bindBankDetails($.sap.entityId);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
		},

		bindBankDetails: function (entityID) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var data = oView.getModel("bdEntity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idBDSubmit").setEnabled(false);
				var oBankDetails = new JSONModel();
				oBankDetails.setData();
				oView.setModel(oBankDetails, "BankDetails");
				return;

			} else {
				this.getView().byId("idBDSubmit").setEnabled(true);

			}
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].item == undefined) {
						var oBankDetails = new JSONModel();
						oBankDetails.setData();
						oView.setModel(oBankDetails, "BankDetails");
						return;
					}
					var oLength = data.resp[i].item.length;
					aData = data.resp[i]
					for (var j = 0; j < oLength; j++) {
						aData.item[j].sno = j + 1;
						aData.item[j].edit = false;

					}
				}
			}
			var oBankDetails = new JSONModel();
			oBankDetails.setData(aData);
			oView.setModel(oBankDetails, "BankDetails");

		},

		getTermAndCond: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var CompanyCodeMapping = "/SapOnboarding/getTermAndCond.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: CompanyCodeMapping,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntity = new JSONModel(data);
					oEntity.setSizeLimit(2000);
					oView.setModel(oEntity, "tcEntity");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityTC").setSelectedKey($.sap.entityId);
					debugger;
					that.bindTermAndCond($.sap.entityId);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
		},

		bindTermAndCond: function (entityID) {
			debugger; //eslint-disable-line
			var oView = this.getView();
			var data = oView.getModel("tcEntity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idTCSubmit").setEnabled(false);
				var oTermsCondition = new JSONModel();
				oTermsCondition.setData();
				oView.setModel(oTermsCondition, "TermsCondition");
				return;

			} else {
				this.getView().byId("idTCSubmit").setEnabled(true);

			}
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oTermsCondition = new JSONModel();
						oTermsCondition.setData();
						oView.setModel(oTermsCondition, "TermsCondition");
						return;
					}
					var oLength = data.resp[i].items.length;
					aData = data.resp[i]
					for (var j = 0; j < oLength; j++) {
						aData.items[j].sno = j + 1;

					}
				}
			}
			var oTermsCondition = new JSONModel();
			oTermsCondition.setData(aData);
			oView.setModel(oTermsCondition, "TermsCondition");

		},

		onSubmit: function (oEvent) {
			//eslint-disable-line
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idTSSubmit") > -1) {
				this.onSubmitTS();
			} else if (oSource.getId().indexOf("idTCSubmit") > -1) {
				this.onSubmitTC();
			} else if (oSource.getId().indexOf("idBDSubmit") > -1) {
				this.onSubmitBD();
			}
		},
		onSubmitTS: function () {
			var that = this;
			var oData = this.getView().getModel("TemplateSelection").getData();
			// var postData = {
			// 	"req": {
			// 		"entityId": $.sap.entityId,
			// 		"goodsTaxInv": oData.item.goodsTaxInv,
			// 		"goodsCDNotes": oData.item.goodsCDNotes,
			// 		"serviceTaxInv": oData.item.serviceTaxInv,
			// 		"serviceCDNotes": oData.item.serviceCDNotes
			// 	}
			// }

			var postData = {
				"req": {
					"entityId": $.sap.entityId,
					"goodsTaxInv": this.getView().byId("idgoodsTaxInv").getSelectedKey(),
					"goodsCDNotes": this.getView().byId("idgoodsCDNotes").getSelectedKey(),
					"serviceTaxInv": this.getView().byId("idserviceTaxInv").getSelectedKey(),
					"serviceCDNotes": this.getView().byId("idserviceCDNotes").getSelectedKey(),
					"gstr6IsdDistribution": this.getView().byId("idgstr6IsdDistribution").getSelectedKey(),
					"gstr6CRDistribution": this.getView().byId("idgstr6CRDistribution").getSelectedKey(),
					"gstr6IsdReDistribution": this.getView().byId("idgstr6IsdReDistribution").getSelectedKey(),
					"gstr6CRReDistribution": this.getView().byId("idgstr6CRReDistribution").getSelectedKey()
				}
			}

			// if (oTabData.length > 0) {

			var jsonForSearch = JSON.stringify(postData);
			var saveUserInfo = "/SapOnboarding/saveTemplateSelection.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: saveUserInfo,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
					if (status == 'success') {
						MessageBox.success(data.resp.message);
					} else {
						MessageBox.error(data.resp.message);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error : saveTemplateSelection");
				});
			});
			// }
		},
		onSubmitTC: function () {
			var that = this;
			var postData = {
				"req": [{
					"groupCode": $.sap.groupCode,
					// "groupId": 1,
					// "id": null,
					"entityId": $.sap.entityId,
					"gstinId": this.getView().byId("idtcGSTIN").getSelectedKeys(),
					"termsCond": this.getView().byId("idtermsAndCond").getValue()
				}]
			}

			var jsonForSearch = JSON.stringify(postData);
			var saveUserInfo = "/SapOnboarding/saveTermAndCond.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: saveUserInfo,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {

					if (data.resp.status == 'S') {
						that.getTermAndCond();
						sap.ui.core.BusyIndicator.hide();
						MessageBox.success(data.resp.message);
					} else {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error(data.resp.message);
					}
				}).fail(function (jqXHR, status, err) {

					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});

		},

		onSubmitBD: function () {

			var tab = this.getView().byId("idTableBD");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("BankDetails").getData().item;
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"groupCode": $.sap.groupCode,
						"entityId": $.sap.entityId,
						"gstinId": oTabData[j].gstinId,
						"bankAcc": oTabData[j].bankAcc,
						"ifscCode": oTabData[j].ifscCode,
						"beneficiary": oTabData[j].beneficiary,
						"paymentDueDate": oTabData[j].paymentDueDate,
						"paymentTerms": oTabData[j].paymentTerms,
						"paymentInstruction": oTabData[j].paymentInstruction,
						"bankName": oTabData[j].bankName,
						"bankAddrs": oTabData[j].bankAddrs
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveBankDetails.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: saveUserInfo,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (data.resp.status == 'S') {
							that.getBankDetails();
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success(data.resp.message);
						} else {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error(data.resp.message);
						}
					}).fail(function (jqXHR, status, err) {

						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error");
					});
				});
			}
		},

		onAddrow: function (oEvent) {

			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idBDAddrow") > -1) {
				this.onAddrowBD();
			}
		},

		onAddrowBD: function () {
			var oModel2 = this.getView().byId("idTableBD").getModel("BankDetails");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.item.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"id": null,
				"gstinId": [],
				"bankAcc": "",
				"ifscCode": "",
				"beneficiary": "",
				"paymentInstruction": "",
				"paymentTerms": "",
				"paymentDueDate": null
			};

			itemlist.item.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onEditRows: function (oEvent) {
			//eslint-disable-line

			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idBDEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableBD").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			}

			sap.m.MessageBox.show("Do you want to edit the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idBDEditRows") > -1) {
							that.onEditBD();
						}
					}
				}
			})

		},

		onEditBD: function () {

			var tab = this.getView().byId("idTableBD");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("BankDetails");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist1.item[sItems[i]].edit = true;
			}

			var oGstinDetail = new JSONModel();
			oGstinDetail.setData(itemlist1);
			this.getView().setModel(oGstinDetail, "BankDetails");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onDeleteRows: function (oEvent) {

			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idTMDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableTC").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			}
			sap.m.MessageBox.show("Do you want to delete the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idTMDeleteRows") > -1) {
							that.onDeleteTC();
						}
					}
				}
			})

		},

		onDeleteTC: function () {
			var tab = this.getView().byId("idTableTC");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("TermsCondition").getData();
			var postData = {
				"req": {
					"entityId": $.sap.entityId,
					"id": []
				}
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.id.push(oTabData.items[j].id);
				}

				var jsonForSearch = JSON.stringify(postData);
				var delelteElRegistration = "/SapOnboarding/deleteTermAndCond.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: delelteElRegistration,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (data.resp.status == 'S') {
							that.getTermAndCond();
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success(data.resp.message);
						} else {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error("Error");
						}
					}).fail(function (jqXHR, status, err) {

						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error");
					});
				});
			}
		},
		handleUploadPress: function (oEvent) {
			var oFileUploader = this.byId("uceinvFileUpload");
			if (this.byId("idEntityLOGO").getSelectedItem() != null) {
				var oText = this.byId("idEntityLOGO").getSelectedItem().getText();
				var oKey = this.byId("idEntityLOGO").getSelectedItem().getKey();
			}
			oFileUploader.setAdditionalData(oKey + "-" + oText);
			oFileUploader.setUploadUrl("/SapOnboarding/logoFileUpload.do");

			sap.ui.core.BusyIndicator.show();
			oFileUploader.upload();
		},

		/*===========================================================*/
		/*========= Upload Complete =================================*/
		/*===========================================================*/
		handleUploadComplete: function (oEvent) {
			//eslint-disable-line
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.resp.status === "S") {
				this.getView().byId("uceinvFileUpload").setValue(null);
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},
		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.onboarding.view.EINVCofig
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.onboarding.view.EINVCofig
		 */
		onAfterRendering: function () {
			this.getTemplateSelection();
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.onboarding.view.EINVCofig
		 */
		//	onExit: function() {
		//
		//	}

	});

});