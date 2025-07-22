sap.ui.define([
	"jquery.sap.global",
	"com/ey/onboarding/controller/BaseController",
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (jQuery, BaseController, Controller, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.onboarding.controller.Master", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.onboarding.view.Master
		 */
		onInit: function () {
			var vDate = new Date();
			this.byId("iFromDate").setDateValue(vDate);
			this.byId("iToDate").setDateValue(vDate);
			this.getOwnerComponent().getRouter().attachRoutePatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			debugger; //eslint-disable-line
			var oName = oEvent.getParameter("name");
			var oContextPath = oEvent.getParameter("arguments").contextPath;
			if (oName == "Master") {
				this.getView().byId("idIconTabBarMaster").setSelectedKey(oContextPath);
				this.getAllAPI();
			} else {
				this.getView().byId("idIconTabBarMaster").setSelectedKey("MasterFileUpload");
				// this.getAllAPI();
			}
		},

		onChangeFileStatus: function (oEvent) {
			debugger; //eslint-disable-line
			if (oEvent.getSource().getSelectedKey() === "Upload") {
				this.getView().byId("hbRbgFileStatus").setVisible(true);
				this.getView().byId("idHorizontalLayout").setVisible(true);
				this.getView().byId("fbStatus").setVisible(false);
				this.getView().byId("tabFileStatus").setVisible(false);
			} else {
				this.getView().byId("hbRbgFileStatus").setVisible(false);
				this.getView().byId("idHorizontalLayout").setVisible(false);
				this.getView().byId("fbStatus").setVisible(true);
				this.getView().byId("tabFileStatus").setVisible(true);
			}
		},

		getAllAPI: function () {
			debugger; //eslint-disable-line
			var oEntity = this.getOwnerComponent().getModel("EntityModel").getData();
			if (!$.sap.entityId) {
				$.sap.entityId = oEntity.resp[0].entityId;
			}
			this.byId("slEntity").setSelectedKey($.sap.entityId);
			this.byId("slProductEntity").setSelectedKey($.sap.entityId);
			this.byId("slItemEntity").setSelectedKey($.sap.entityId);
			this.byId("slCustEntity").setSelectedKey($.sap.entityId);
			this.byId("slVendorEntity").setSelectedKey($.sap.entityId);

			this.getMasterProduct($.sap.entityId);
			this.getMasterItem($.sap.entityId);
			this.getMasterCustomer($.sap.entityId);
			this.getMasterVendor($.sap.entityId);
		},

		onSelectChangeProduct: function (oEvent) {
			debugger; //eslint-disable-line
			$.sap.entityId = oEvent.getSource().getSelectedKey();
			var vId = oEvent.getSource().getId();
			if (vId.includes("slProductEntity")) {
				this.getMasterProduct($.sap.entityId);
			}
			if (vId.includes("slItemEntity")) {
				this.getMasterItem($.sap.entityId);
			}
			if (vId.includes("slCustEntity")) {
				this.getMasterCustomer($.sap.entityId);
			}
			if (vId.includes("slVendorEntity")) {
				this.getMasterVendor($.sap.entityId);
			}

		},

		handleIconTabBarSelect: function (oEvent) {
			if (oEvent.getParameters().selectedKey === "All") {

			} else if (oEvent.getParameters().selectedKey === "MasterFileUpload") {
				this.byId("slEntity").setSelectedKey($.sap.entityId);

			} else if (oEvent.getParameters().selectedKey === "ProductMaster") {
				this.byId("slProductEntity").setSelectedKey($.sap.entityId);
				this.getMasterProduct($.sap.entityId);

			} else if (oEvent.getParameters().selectedKey === "ItemMaster") {
				this.byId("slItemEntity").setSelectedKey($.sap.entityId);
				this.getMasterItem($.sap.entityId);
				
			} else if (oEvent.getParameters().selectedKey === "CustomerMaster") {
				this.byId("slCustEntity").setSelectedKey($.sap.entityId);
				this.getMasterCustomer($.sap.entityId);
				
			} else if (oEvent.getParameters().selectedKey === "VendorMaster") {
				this.byId("slVendorEntity").setSelectedKey($.sap.entityId);
				this.getMasterVendor($.sap.entityId);
				
			}
		},

		onSearchMaster: function (oEvent) {
			debugger; //eslint-disable-line
			var vId = oEvent.getSource().getId();
			if (vId.includes("fbProduct")) {
				this.getMasterProduct($.sap.entityId);

			} else if (vId.includes("fbItem")) {
				this.getMasterItem($.sap.entityId);

			} else if (vId.includes("fbCustomer")) {
				this.getMasterCustomer($.sap.entityId);

			} else if (vId.includes("fbVendor")) {
				this.getMasterVendor($.sap.entityId);
			}
		},

		getMasterProduct: function (vEntity) { // Modified by Bharat Gupta on 05.11.2019
			debugger; //eslint-disable-line
			var that = this,
				oView = this.getView(),
				ELDetails = "/SapOnboarding/getMasterProduct.do",
				jsonForSearch = JSON.stringify({
					"req": {
						"entityId": vEntity
					}
				});

			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// that.bindMasterProduct(data)
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].sno = i + 1;
					}
					var oMasterProduct = new JSONModel();
					oMasterProduct.setData(data.resp);
					oView.setModel(oMasterProduct, "MasterProduct");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});
		},

		getMasterItem: function (vEntity) {
			debugger; //eslint-disable-line
			var that = this,
				oView = this.getView(),
				ELDetails = "/SapOnboarding/getMasterItem.do",
				jsonForSearch = JSON.stringify({
					"req": {
						"entityId": vEntity
					}
				});

			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].sno = i + 1;
					}
					var oMasterItem = new JSONModel();
					oMasterItem.setData(data.resp);
					oView.setModel(oMasterItem, "MasterItem");
					// that.bindMasterItem(data)
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});
		},

		getMasterCustomer: function (vEntity) {
			debugger; //eslint-disable-line
			var that = this,
				oView = this.getView(),
				ELDetails = "/SapOnboarding/getMasterCustomer.do",
				jsonForSearch = JSON.stringify({
					"req": {
						"entityId": vEntity
					}
				});

			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].sno = i + 1;
					}
					var oMasterCustomer = new JSONModel();
					oMasterCustomer.setData(data.resp);
					oView.setModel(oMasterCustomer, "MasterCustomer");
					// that.bindMasterCustomer(data)
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});
		},

		getMasterVendor: function (vEntity) {
			debugger; //eslint-disable-line
			var that = this,
				oView = this.getView(),
				ELDetails = "/SapOnboarding/getMasterVendor.do",
				jsonForSearch = JSON.stringify({
					"req": {
						"entityId": vEntity
					}
				});
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].sno = i + 1;
					}
					var oMasterVendor = new JSONModel();
					oMasterVendor.setData(data.resp);
					oView.setModel(oMasterVendor, "MasterVendor");
					// that.bindMasterVendor(data)
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});
		},

		onEditRows: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idMasterProductEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterProductTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at list one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterItemEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterItemTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at list one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterCustomerEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterCustomerTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at list one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterVendorEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterVendorTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at list one record");
					return;
				}
			}

			sap.m.MessageBox.show("Do you want to edit the records??", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "NO") {

					} else if (oAction == "YES") {
						if (oSource.getId().indexOf("idMasterProductEditRows") > -1) {
							that.onEditMasterProduct();
						} else if (oSource.getId().indexOf("idMasterItemEditRows") > -1) {
							that.onEditMasterItem();
						} else if (oSource.getId().indexOf("idMasterCustomerEditRows") > -1) {
							that.onEditMasterCustomer();
						} else if (oSource.getId().indexOf("idMasterVendorEditRows") > -1) {
							that.onEditMasterVendor();
						}
					}
				}
			})
		},

		onEditMasterProduct: function () {

			var tab = this.getView().byId("idMasterProductTab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("MasterProduct");
			var itemlist = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist[sItems[i]].edit = true;
			}
			var oMasterProduct = new JSONModel();
			oMasterProduct.setData(itemlist);
			this.getView().setModel(oMasterProduct, "MasterProduct");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditMasterItem: function () {

			var tab = this.getView().byId("idMasterItemTab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("MasterItem");
			var itemlist = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist[sItems[i]].edit = true;
			}

			var oMasterItem = new JSONModel();
			oMasterItem.setData(itemlist);
			this.getView().setModel(oMasterItem, "MasterItem");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditMasterCustomer: function () {

			var tab = this.getView().byId("idMasterCustomerTab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("MasterCustomer");
			var itemlist = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist[sItems[i]].edit = true;
			}
			var oMasterCustomer = new JSONModel();
			oMasterCustomer.setData(itemlist);
			this.getView().setModel(oMasterCustomer, "MasterCustomer");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditMasterVendor: function () {

			var tab = this.getView().byId("idMasterVendorTab");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("MasterVendor");
			var itemlist = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {

				itemlist[sItems[i]].edit = true;
			}
			var oMasterVendor = new JSONModel();
			oMasterVendor.setData(itemlist);
			this.getView().setModel(oMasterVendor, "MasterVendor");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onSubmit: function (oEvent) {

			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idMasterProductSubmit") > -1) {
				this.onSubmitMasterProduct();
			} else if (oSource.getId().indexOf("idMasterItemSubmit") > -1) {
				this.onSubmitMasterItem();
			} else if (oSource.getId().indexOf("idMasterCustomerSubmit") > -1) {
				this.onSubmitMasterCustomer();
			} else if (oSource.getId().indexOf("idMasterVendorSubmit") > -1) {
				this.onSubmitMasterVendor();
			}
		},

		onSubmitMasterProduct: function () {

			var tab = this.getView().byId("idMasterProductTab");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at list one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterProduct").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id,
						"entity": oTabData[j].entity,
						"sGstin": oTabData[j].sGstin,
						"attribut1": oTabData[j].attribut1,
						"attribut2": oTabData[j].attribut2,
						"attribut3": oTabData[j].attribut3,
						"itmCode": oTabData[j].itmCode,
						"desc": oTabData[j].desc,
						"category": oTabData[j].category,
						"hsnSac": oTabData[j].hsnSac,
						"uom": oTabData[j].uom,
						"reverseCharge": oTabData[j].reverseCharge,
						"tds": oTabData[j].tds,
						"diffPercent": oTabData[j].diffPercent,
						"nilOrNonOrExmt": oTabData[j].nilOrNonOrExmt,
						"notificationNum": oTabData[j].notificationNum,
						"rate": oTabData[j].rate,
						"itcFlag": oTabData[j].itcFlag,
						"glCode": oTabData[j].glCode

					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateMasterProduct.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: saveUserInfo,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterProduct(that.byId("slProductEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Modified");
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

		onSubmitMasterItem: function () {

			var tab = this.getView().byId("idMasterItemTab");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at list one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterItem").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id,
						"entity": oTabData[j].entity,
						"sGstin": oTabData[j].sGstin,
						"attribut1": oTabData[j].attribut1,
						"attribut2": oTabData[j].attribut2,
						"attribut3": oTabData[j].attribut3,
						"itmCode": oTabData[j].itmCode,
						"desc": oTabData[j].desc,
						"category": oTabData[j].category,
						"hsnSac": oTabData[j].hsnSac,
						"uom": oTabData[j].uom,
						"reverseCharge": oTabData[j].reverseCharge,
						"tds": oTabData[j].tds,
						"diffPercent": oTabData[j].diffPercent,
						"nilOrNonOrExmt": oTabData[j].nilOrNonOrExmt,
						"notificationNum": oTabData[j].notificationNum,
						"rate": oTabData[j].rate,
						"elgblIndicator": oTabData[j].elgblIndicator,
						"perOfElgbl": oTabData[j].perOfElgbl,
						"comsuppIndicator": oTabData[j].comsuppIndicator,
						"itcFlag": oTabData[j].itcFlag,
						"glCode": oTabData[j].glCode
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateMasterItem.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: saveUserInfo,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterItem(that.byId("slItemEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Modified");
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

		onSubmitMasterCustomer: function () {

			var tab = this.getView().byId("idMasterCustomerTab");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at list one record");
				return;
			}
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterCustomer").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id,
						"gstinOrPan": oTabData[j].gstinOrPan,
						"legalName": oTabData[j].legalName,
						"customerName": oTabData[j].customerName,
						"regType": oTabData[j].regType,
						"custCode": oTabData[j].custCode,
						"outSideIndia": oTabData[j].outSideIndia,
						"emailId": oTabData[j].emailId,
						"mobileNum": oTabData[j].mobileNum,
						"address1": oTabData[j].address1,
						"address2": oTabData[j].address2,
						"place": oTabData[j].place,
						"suppPinCode": oTabData[j].suppPinCode,
						"custPinCode": oTabData[j].custPinCode,
						"stateCode": oTabData[j].stateCode,
						"aproDistance": oTabData[j].aproDistance
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateMasterCustomer.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: saveUserInfo,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterCustomer(that.byId("slCustEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Modified");
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

		onSubmitMasterVendor: function () {

			var tab = this.getView().byId("idMasterVendorTab");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at list one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterVendor").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id,
						"rgstin": oTabData[j].rgstin,
						"rpan": oTabData[j].rpan,
						"vendorCode": oTabData[j].vendorCode,
						"vendonGstin": oTabData[j].vendonGstin,
						"vendorName": oTabData[j].vendorName,
						"emailId1": oTabData[j].emailId1,
						"emailId2": oTabData[j].emailId2,
						"mobileNum1": oTabData[j].mobileNum1,
						"mobileNum2": oTabData[j].mobileNum2,
						"triggerPoint": oTabData[j].triggerPoint
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var MasterVendor = "/SapOnboarding/updateMasterVendor.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: MasterVendor,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterVendor(that.byId("slVendorEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Modified");
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

		/*===========================================================*/
		/*========= File Upload  ====================================*/
		/*===========================================================*/
		handleUploadPress: function (oEvent) {
			var oFileUploader = this.byId("ucMasterFileUpload"),
				oRadio = this.byId("rbgMasterStatus").getSelectedIndex(),
				oKey = this.byId("slEntity").getSelectedKey();

			if (oKey === "") {
				sap.m.MessageToast.show("Please select Entity for uploading file");
				return;
			} else if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}
			oFileUploader.setAdditionalData(oKey);

			switch (oRadio) {
			case 0:
				oFileUploader.setUploadUrl("/SapOnboarding/productMasterWebUpload.do");
				break;
			case 1:
				oFileUploader.setUploadUrl("/SapOnboarding/itemMasterWebUpload.do");
				break;
			case 2:
				oFileUploader.setUploadUrl("/SapOnboarding/customerMasterWebUpload.do");
				break;
			case 3:
				oFileUploader.setUploadUrl("/SapOnboarding/vendorMasterWebUpload.do");
				break;
			}
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
			if (sResponse.hdr.status === "S") {
				this.getView().byId("ucMasterFileUpload").setValue(null);
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
				this.getAllAPI();
			} else {
				MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onDeleteRows: function (oEvent) {

			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idMasterProductDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterProductTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterItemDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterItemTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterCustomerDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterCustomerTab").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idMasterVendorDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idMasterVendorTab").getSelectedIndices();
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
						if (oSource.getId().indexOf("idMasterProductDeleteRows") > -1) {
							that.onDeleteProduct();
						} else if (oSource.getId().indexOf("idMasterItemDeleteRows") > -1) {
							that.onDeleteItem();
						} else if (oSource.getId().indexOf("idMasterCustomerDeleteRows") > -1) {
							that.onDeleteCustome();
						} else if (oSource.getId().indexOf("idMasterVendorDeleteRows") > -1) {
							that.onDeleteVendor();
						}
					}
				}
			})

		},

		onDeleteProduct: function () {
			var tab = this.getView().byId("idMasterProductTab");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterProduct").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var delelteElRegistration = "/SapOnboarding/deleteMasterProduct.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: delelteElRegistration,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterProduct(that.byId("slProductEntity").getSelectedKey());

							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Deleted");
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
		onDeleteItem: function () {
			var tab = this.getView().byId("idMasterItemTab");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterItem").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var delelteElRegistration = "/SapOnboarding/deleteMasterItem.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: delelteElRegistration,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {

							that.getMasterItem(that.byId("slItemEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Deleted");
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
		onDeleteCustome: function () {
			var tab = this.getView().byId("idMasterCustomerTab");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterCustomer").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var delelteElRegistration = "/SapOnboarding/deleteMasterCustomer.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: delelteElRegistration,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterCustomer(that.byId("slCustEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Deleted");
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
		onDeleteVendor: function () {
			var tab = this.getView().byId("idMasterVendorTab");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("MasterVendor").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"id": oTabData[j].id
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var delelteElRegistration = "/SapOnboarding/deleteMasterVendor.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: delelteElRegistration,
						contentType: "application/json",
						data: jsonForSearch
					}).done(function (data, status, jqXHR) {

						if (status == 'success') {
							that.getMasterVendor(that.byId("slVendorEntity").getSelectedKey());
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success("Successfully Deleted");
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

		onSearchFileStatus: function () {
			debugger; //eslint-disable-line
			var that = this;
			var oView = this.getView();
			var oEntityName = this.getView().byId("slMFUEntity").getSelectedKey();
			var oFileType = this.getView().byId("slMFUFileType").getSelectedKey();
			var oFromDate = this.oView.byId("iFromDate").getValue();
			if (oFromDate === "") {
				oFromDate = null;
			}
			var oToDate = this.oView.byId("iToDate").getValue();
			if (oToDate === "") {
				oToDate = null;
			}
			var postData = {
				"req": {
					"dataRecvFrom": oFromDate,
					"dataRecvTo": oToDate,
					"entityId": Number(oEntityName),
					"fileType": oFileType

				}
			}

			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/SapOnboarding/getMasterFileStatus.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch,
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.status === "S") {
						var oFileStatus = new JSONModel();
						oFileStatus.setData(null);
						oView.setModel(oFileStatus, "FileStatus");
					} else {
						var oFileStatus = new JSONModel();
						oFileStatus.setData(data.resp);
						oView.setModel(oFileStatus, "FileStatus");
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error");
				});
			});
		},

		onPressFileStatusDownload: function (oEvent) {
			debugger;
			var oText = oEvent.getSource().getDependents()[0].getText();
			var oRow = oEvent.getSource().getEventingParent().getParent().getBindingContext("FileStatus").getPath().split('/')[1]
			var oModelData = this.getView().getModel("FileStatus").getData();
			if (oText === "errorCount") {
				var reqData = {
					"req": {
						"fileName": oModelData[Number(oRow)].fileName
					}
				}
				var url = "/SapOnboarding/downloadMasterErrorReport.do";
				this.excelDownload(reqData, url);
			} else if (oText === "fileName") {
				var reqData = {
					"req": {
						"fileName": oModelData[Number(oRow)].fileName
					}
				}
				var url = "/SapOnboarding/downloadMasterReport.do";
				this.excelDownload(reqData, url);
			}

		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.onboarding.view.Master
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.onboarding.view.Master
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.onboarding.view.Master
		 */
		//	onExit: function() {
		//
		//	}

	});
});