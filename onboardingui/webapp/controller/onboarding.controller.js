var inputdata = [];
var oColCount = 2;
var oEntityMain;
var oInd;
sap.ui.define([
	"jquery.sap.global",
	'com/ey/onboarding/controller/BaseController',
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	'sap/ui/model/Filter',
	'sap/ui/model/FilterOperator',
	'sap/m/MessageBox',
	'sap/m/Token',
	'sap/ui/export/Spreadsheet'
], function (jQuery, BaseController, Controller, JSONModel, Filter, FilterOperator, MessageBox, Token, Spreadsheet) {
	"use strict";

	return BaseController.extend("com.ey.onboarding.controller.onboarding", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.onboarding.view.onboarding
		 */
		onInit: function () {
			this.defaultDate();
			this.gefaultDateData();
			this.BEFlag = "B";
			this.setlastEditedDate();
			this.getOwnerComponent().getRouter().attachRoutePatternMatched(this._onRouteMatched, this);
		},

		onAfterRendering: function () {

		},

		setlastEditedDate: function () {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show();
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/getEinvoiceDate.do",
					contentType: "application/json",
					data: JSON.stringify([])
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var odateModel = new JSONModel();
					odateModel.setData(data.resp);
					oView.setModel(odateModel, "getDateModel");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
		},

		onSubmitInv: function () {
			var postData = {
				"req": {
					"date": this.byId("DateId").getValue()
				}
			}

			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/SapOnboarding/saveEinvoiceDate.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
			// this.setlastEditedDate();
		},

		//----------- Added by Arun on 10/2/2022--------------------------------

		defaultDate: function (oEvent) {
			var vDate = new Date();
			this.byId("DateId").setMaxDate(vDate);
		},

		gefaultDateData: function () {
			var that = this;
			this.byId("DateId").addEventDelegate({
				onAfterRendering: function () {
					that.byId("DateId").$().find("input").attr("readonly", true);
				}
			});
		},
		// -------ended by Arun on 10/2/2022----------------------------------------

		_onRouteMatched: function (oEvent) {
			var oContextPath = oEvent.getParameter("arguments").contextPath;

			if (oEvent.getParameter("name") == "onboarding") {
				var aTab = [
					"FileUpload", "Organization", "UserCreation", "DataSecurity", "AppPermission1", "GroupPermission", "ERPRegistration",
					"companycodeMapping", "StandardIntegration", "ServiceOptions", "MakerChecker", "Feedback"
				];
				this.getView().byId("idIconTabBar").setSelectedKey(oContextPath);
				this.getAllAPI();
				if (aTab.includes(oContextPath)) {
					this._getOnBoardingDate(oContextPath);
				}
			} else {
				this.getView().byId("idIconTabBar").setSelectedKey("FileUpload");
			}
		},

		handleIconTabBarSelect: function (oEvent) {
			this._getOnBoardingDate(oEvent.getSource().getSelectedKey());
		},

		_getOnBoardingDate: function (key) {
			switch (key) {
			case "FileUpload":
				if ($.sap.groupCode === "sp0002") {
					this.byId("EinvoiceId").setVisible(true);
				} else {
					this.byId("EinvoiceId").setVisible(true);
				}
				var oRadio = this.byId("rbgStatus").getSelectedIndex();
				if ($.sap.groupCode === "sp0002" && oRadio === 9) {
					this.byId("EinvId").setVisible(true);
				} else {
					this.byId("EinvId").setVisible(true);
				}
				break;
			case "ELExtract":
				this.getELDetails();
				break;
			case "GSTNDetails":
				this.getELRegistration();
				break;
			case "Organization":
				this.getOrganization();
				break;
			case "UserCreation":
				this.getUserCreation();
				break;
			case "DataSecurity":
				this.byId("idEntityDS").setSelectedKey($.sap.entityId);
				this.bindDataSecurity($.sap.entityId, false);
				break;
			case "AppPermission1":
				this.getUserCreation();
				this.getProfile();
				break;
			case "ERPRegistration":
				this.geterpregistration();
				break;
			case "ERPMapping":
				break;
			case "StandardIntegration":
				this.getERPScenario();
				this.getEventBasedScenarioPermission();
				this.getSFTPScenarioPermission();
				break;
			case "AdvancedIntegration":
				break;
			case "MakerChecker":
				this.MakerEntity();
				break;
			case "OrganizationData":
				this.byId("idEntityValueOrg").setSelectedKey($.sap.entityId);
				break;
			case "ServiceOptions":
				this.getServiceOptions();
				break;
			case "companycodeMapping":
				this.getCompanycodeMapping();
				break;
			case "Feedback":
				this._getFeedbacks();
				break;
			case "GroupPermission":
				this._getGroupAppPermission();
				break;
			}
		},

		getServiceOptions: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			// var jsonForSearch = JSON.stringify(postData);
			var UserInfo = "/SapOnboarding/getServiceOption.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntityUser = new JSONModel(data);
						oEntityUser.setSizeLimit(2000);
						oView.setModel(oEntityUser, "entityServiceOption");

						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityUserCreation").setSelectedKey($.sap.entityId);
						that.bindSeviceOption($.sap.entityId);

					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindSeviceOption: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityServiceOption").getData();
			var oLen = data.resp.length;
			if (oLen === 0) {
				this.getView().byId("idSOSubmit").setEnabled(false);
				oView.setModel(new JSONModel(), "ServiceOptions");
				return;
			} else {
				this.getView().byId("idSOSubmit").setEnabled(true);
			}
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						oView.setModel(new JSONModel(), "ServiceOptions");
						return;
					}
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						data.resp[i].items[j].sno = j + 1;
						data.resp[i].items[j].edit = false;
						if (!data.resp[i].ewbFlag) {
							data.resp[i].items[j].ewb = "";
						}
						if (!data.resp[i].einvFlag) {
							data.resp[i].items[j].einv = "";
						}
					}
					oView.setModel(new JSONModel(data.resp[i]), "ServiceOptions");
				}
			}
		},

		getAllAPI: function (oEvent) {
			this.getDataSecurity();
			this.getFunctionality();
			this.getELRegistration();
			// this.getUserCreation();
			this.getELDetails();
			// this.getOrganization();
			// this.getAppPermission();
			// this.geterpregistration();
			// this.getERPScenario();
			// this.getProfile();
		},

		getFunctionality: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var FunctionalityDesc = "/SapOnboarding/getFunctionalityDesc.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: FunctionalityDesc,
						contentType: "application/json",
						// data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var oFunctionality = new JSONModel();
						oFunctionality.setData(data.resp);
						oView.setModel(oFunctionality, "functionality");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		onPressGo: function (oEvent) {
			var oView = this.getView();
			var that = this;
			var oEntityValueOrg = oView.byId("idEntityValueOrg").getSelectedKey();
			var oAttributOrg = oView.byId("idAttributOrg").getSelectedKey();
			if (oEntityValueOrg === "") {
				sap.m.MessageBox.warning("Select Entity");
				return;
			} else if (oAttributOrg === "") {
				sap.m.MessageBox.warning("Select Attribut");
				return;
			}
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode,
					"entityId": oEntityValueOrg,
					"attCode": oAttributOrg
				}
			};

			var jsonForSearch = JSON.stringify(postData);
			var OrganizationData = "/SapOnboarding/getOrganizationData.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: OrganizationData,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that.bindOrganizationData(data);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});

		},

		bindOrganizationData: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					"sno": i + 1,
					"edit": false,
					"id": data.resp[i].id,
					"attributeName": data.resp[i].attributeName

				});
			}
			var oOrganizationData = new JSONModel(aData.resp);
			oView.setModel(oOrganizationData, "OrgData");
		},

		getELDetails: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/SapOnboarding/getLatestElEntitlement.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: ELDetails,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that.bindELDetails(data);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindELDetails: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			if (oLen > 0) {
				this.getView().byId("idELESubmit").setEnabled(true);
			} else {
				this.getView().byId("idELESubmit").setEnabled(false);
				var oELExtractInfo = new JSONModel();
				oELExtractInfo.setData();
				oView.setModel(oELExtractInfo, "ELExtractInfo");
				return;
			}
			data.resp = _.sortBy(data.resp, "entityId");
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					"sno": i + 1,
					"edit": false,
					"elId": data.resp[i].elId,
					"entityId": data.resp[i].entityId,
					"entityName": data.resp[i].entityName,
					"groupCode": data.resp[i].groupCode,
					"pan": data.resp[i].pan,
					"companyCode": data.resp[i].companyCode,
					"gstinList": data.resp[i].gstinList,
					"functionality": data.resp[i].functionality,
					"fromTaxPeriod": data.resp[i].fromTaxPeriod,
					"toTaxPeriod": data.resp[i].toTaxPeriod,
					"elValue": data.resp[i].elValue,
					"contractStartPeriod": data.resp[i].contractStartPeriod,
					"contractEndPeriod": data.resp[i].contractEndPeriod,
					"renewal": data.resp[i].renewal,
					"gfisId": data.resp[i].gfisId,
					"paceId": data.resp[i].paceId
				});
			}
			var oELExtractInfo = new JSONModel();
			oELExtractInfo.setData(aData.resp);
			oView.setModel(oELExtractInfo, "ELExtractInfo");
		},

		handleLinkPress: function (oEvent) {
			var vRow = oEvent.getSource().getBindingContext("ELExtractInfo").getPath().split("/")[1]
			var oELData = this.getModel("ELExtractInfo").getData();
			var aJsonData = [];
			if (oELData[vRow].gstinList == undefined) {
				MessageBox.information("No record found");
			}
			for (var i = 0; i < oELData[vRow].gstinList.length; i++) {
				aJsonData.push({
					"GSTIN": oELData[vRow].gstinList[i]
				})
			}
			if (!this._oPopover) {
				this._oPopover = sap.ui.xmlfragment("com.ey.onboarding.Fragments.Popover", this);
				this.getView().addDependent(this._oPopover);
			}
			var oGSTIN = new JSONModel();
			oGSTIN.setData(aJsonData);
			this.getView().setModel(oGSTIN, "GSTIN");
			this._oPopover.openBy(oEvent.getSource());
		},

		handleLinkPress1: function (oEvent) {
			var vRow = oEvent.getSource().getParent().getId().split('idELHistory-rows-row')[1];
			var oELData = this.getModel("history").getData();
			var aJsonData = [];
			if (oELData[vRow].gstinList == undefined) {
				MessageBox.information("No record found");
			}
			for (var i = 0; i < oELData[vRow].gstinList.length; i++) {
				aJsonData.push({
					"GSTIN": oELData[vRow].gstinList[i]
				})
			}
			if (!this._oPopover) {
				this._oPopover = sap.ui.xmlfragment("com.ey.onboarding.Fragments.Popover", this);
				this.getView().addDependent(this._oPopover);
			}
			var oGSTIN = new JSONModel();
			oGSTIN.setData(aJsonData);
			this.getView().setModel(oGSTIN, "GSTIN");
			this._oPopover.openBy(oEvent.getSource());
		},

		getELRegistration: function (oEvent) {
			if ($.sap.groupCode === "sp0002") {
				this.byId("EinvoiceId").setVisible(true);
			} else {
				this.byId("EinvoiceId").setVisible(true);
			}
			this.getELDetails();
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELRegistration = "/SapOnboarding/getElRegistration.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: ELRegistration,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntity = new JSONModel(data);
						oEntity.setSizeLimit(2000);
						oView.setModel(oEntity, "entity");
						that.getOwnerComponent().setModel(oEntity, "EntityModel"); // Added for Entity data by Bharat Gupta on 04.11.2019
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityFileUpload").setSelectedKey($.sap.entityId);
						that.byId("idEntityELReg").setSelectedKey($.sap.entityId);
						that.byId("idEntityELReg1").setSelectedKey($.sap.entityId);
						that.bindELRegistration($.sap.entityId);
						that.tabBind($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindELRegistration: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idELRegSubmit").setEnabled(false);
				var oGstinDetail = new JSONModel();
				oGstinDetail.setData();
				oView.setModel(oGstinDetail, "GstinDetail");
				return;
			} else {
				this.getView().byId("idELRegSubmit").setEnabled(true);
			}
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oGstinDetail = new JSONModel();
						oGstinDetail.setData();
						oView.setModel(oGstinDetail, "GstinDetail");
						return;
					}
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						if (j === 0 || j === 1) {
							var systemID = "PRDCLNT800";
						} else {
							var systemID = "PRDCLNT801";
						}
						aData.resp.push({
							sno: j + 1,
							edit: false,
							systemID: systemID,
							bankAccNo: data.resp[i].items[j].bankAccNo,
							effectiveDate: data.resp[i].items[j].effectiveDate,
							entityId: data.resp[i].items[j].entityId,
							groupCode: data.resp[i].items[j].groupCode,
							gstnUsername: data.resp[i].items[j].gstnUsername,
							id: data.resp[i].items[j].id,
							primaryAuthEmail: data.resp[i].items[j].primaryAuthEmail,
							primaryContactEmail: data.resp[i].items[j].primaryContactEmail,
							secondaryContactEmail: data.resp[i].items[j].secondaryContactEmail,
							regEmail: data.resp[i].items[j].regEmail,
							regMobile: data.resp[i].items[j].regMobile,
							regType: data.resp[i].items[j].regType,
							secondaryAuthEmail: data.resp[i].items[j].secondaryAuthEmail,
							supplierGstin: data.resp[i].items[j].supplierGstin,
							turnover: data.resp[i].items[j].turnover,
							registeredName: data.resp[i].items[j].registeredName,
							address1: data.resp[i].items[j].address1,
							address2: data.resp[i].items[j].address2,
							address3: data.resp[i].items[j].address3
						})
					}
					break;
				}
			}
			var oGstinDetail = new JSONModel();
			oGstinDetail.setData(aData.resp);
			oView.setModel(oGstinDetail, "GstinDetail");
		},

		getCompanycodeMapping: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var CompanyCodeMapping = "/SapOnboarding/getCompanyCodeMapping.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: CompanyCodeMapping,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntity = new JSONModel(data);
						oEntity.setSizeLimit(2000);
						oView.setModel(oEntity, "ccmEntity");
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityCC").setSelectedKey($.sap.entityId);
						that.bindCompanyCodeMapping($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindCompanyCodeMapping: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("ccmEntity").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idCCMSubmit").setEnabled(false);
				var oCompanyCodeMapping = new JSONModel();
				oCompanyCodeMapping.setData();
				oView.setModel(oCompanyCodeMapping, "CompanyCodeMapping");
				return;
			} else {
				this.getView().byId("idCCMSubmit").setEnabled(true);
			}
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].item == undefined) {
						var oCompanyCodeMapping = new JSONModel();
						oCompanyCodeMapping.setData();
						oView.setModel(oCompanyCodeMapping, "CompanyCodeMapping");
						return;
					}
					var oLength = data.resp[i].item.length;
					aData = data.resp[i]
					for (var j = 0; j < oLength; j++) {
						aData.item[j].sno = j + 1;
						aData.item[j].edit = false;
					}
					break;
				}
			}

			var oCompanyCodeMapping = new JSONModel();
			oCompanyCodeMapping.setData(aData);
			oView.setModel(oCompanyCodeMapping, "CompanyCodeMapping");
		},

		getUserCreation: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var UserInfo = "/SapOnboarding/getUserInfo.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntityUser = new JSONModel(data);
						oEntityUser.setSizeLimit(2000);
						oView.setModel(oEntityUser, "entityUser");
						oView.setModel(oEntityUser, "entityAppPer");

						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityUserCreation").setSelectedKey($.sap.entityId);
						that.bindUserCreation($.sap.entityId);
						that.bindAppPermission($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindUserCreation: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityUser").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idUCSubmit").setEnabled(false);
				var oUserInfo = new JSONModel();
				oUserInfo.setData();
				oView.setModel(oUserInfo, "UserInfo");
				return;
			} else {
				this.getView().byId("idUCSubmit").setEnabled(true);
			}
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oUserInfo = new JSONModel();
						oUserInfo.setData();
						oView.setModel(oUserInfo, "UserInfo");
						return;
					}
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						aData.resp.push({
							"sno": j + 1,
							"edit": false,
							"visible": false,
							"id": data.resp[i].items[j].id,
							"groupCode": data.resp[i].items[j].groupCode,
							"userName": data.resp[i].items[j].userName,
							"firstName": data.resp[i].items[j].firstName,
							"lastName": data.resp[i].items[j].lastName,
							"email": data.resp[i].items[j].email,
							"mobile": data.resp[i].items[j].mobile,
							"userRole": data.resp[i].items[j].userRole,
							"isFlag": data.resp[i].items[j].isFlag,
							"createdBy": data.resp[i].items[j].createdBy,
							"createdOn": data.resp[i].items[j].createdOn
						})
					}
				}
			}
			var oUserInfo = new JSONModel();
			oUserInfo.setData(aData.resp);
			oView.setModel(oUserInfo, "UserInfo");
		},

		geterpregistration: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var UserInfo = "/SapOnboarding/geterpregistration.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that.binderpregistration(data);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		binderpregistration: function (data) {
			var oView = this.getView();
			data.resp = _.sortBy(data.resp, "id");
			var aData = {
				"resp": []
			};
			for (var i = 0; i < data.resp.length; i++) {
				aData.resp.push({
					"sno": i + 1,
					"edit": false,
					"id": data.resp[i].id,
					"systemId": data.resp[i].systemId,
					"sourceType": data.resp[i].sourceType,
					"portocal": data.resp[i].portocal,
					"hostName": data.resp[i].hostName,
					"port": data.resp[i].port,
					"userName": data.resp[i].userName,
					"password": data.resp[i].password,
					"status": data.resp[i].status
				})
			}
			var oREPDetails = new JSONModel();
			oREPDetails.setData(aData.resp);
			oView.setModel(oREPDetails, "REPDetails");
		},

		getOrganization: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var Organization = "/SapOnboarding/getOrganization.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: Organization,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntityOrg = new JSONModel(data);
						oEntityOrg.setSizeLimit(2000);
						oView.setModel(oEntityOrg, "entityOrg");
						// if (oEntityMain) {
						// 	var entityID = oEntityMain;
						// } else {
						// 	var entityID = data.resp[0].entityId;
						// }
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityOrg").setSelectedKey($.sap.entityId);
						that.bindOrganization($.sap.entityId);
						that.bindOrganizationValue($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		bindOrganization: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityOrg").getData();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			var oFlagH = false;
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oOrgConfig = new JSONModel();
						oOrgConfig.setData();
						oView.setModel(oOrgConfig, "hierar");
						return;
					}
					data.resp[0].items = _.sortBy(data.resp[0].items, "id");
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						if (data.resp[i].items[j].attCode == "SD") {
							// oFlagH = true;
							oFlagH = false;
						} else if (data.resp[i].items[j].attCode == "PC2") {
							// oFlagH = true;
							oFlagH = false;
						} else {
							oFlagH = false;
						}
						if (oFlagH === false) {
							if (data.resp[i].items[j].outword == 'M') {
								var vOutward = true;
							} else {
								var vOutward = false;
							}
							if (data.resp[i].items[j].inword == 'M') {
								var vInword = true;
							} else {
								var vInword = false;
							}
							aData.resp.push({
								"id": data.resp[i].items[j].id,
								"isActive": data.resp[i].items[j].isActive,
								"isApplicable": data.resp[i].items[j].isApplicable,
								"attCode": data.resp[i].items[j].attCode,
								"attName": data.resp[i].items[j].attName,
								"outword": vOutward,
								"inword": vInword
							})
						}
					}
				}
			}
			var oOrgConfig = new JSONModel();
			oOrgConfig.setData(aData.resp);
			oView.setModel(oOrgConfig, "hierar");
		},

		bindOrganizationValue: function (entityID) {
			var oView = this.getView();
			var oOrganizationData = new JSONModel(null);
			oView.setModel(oOrganizationData, "OrgData");
			var data = oView.getModel("entityOrg").getData();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oOrgConfig = new JSONModel();
						oOrgConfig.setData();
						oView.setModel(oOrgConfig, "hierarValue");
						return;
					}
					data.resp[0].items = _.sortBy(data.resp[0].items, "id");
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						if (data.resp[i].items[j].outword == 'M') {
							var vOutward = true;
						} else {
							var vOutward = false;
						}
						if (data.resp[i].items[j].inword == 'M') {
							var vInword = true;
						} else {
							var vInword = false;
						}
						aData.resp.push({
							"id": data.resp[i].items[j].id,
							"isActive": data.resp[i].items[j].isActive,
							"isApplicable": data.resp[i].items[j].isApplicable,
							"attCode": data.resp[i].items[j].attCode,
							"attName": data.resp[i].items[j].attName,
							"outword": vOutward,
							"inword": vInword
						})
					}
				}
			}
			var oOrgConfig = new JSONModel();
			oOrgConfig.setData(aData.resp);
			oView.setModel(oOrgConfig, "hierarValue");
		},

		getDataSecurity: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var Organization = "/SapOnboarding/getDataSecurity.do";
			sap.ui.core.BusyIndicator.show();
			$.ajax({
					method: "POST",
					url: Organization,
					contentType: "application/json",
					data: jsonForSearch
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntityDS = new JSONModel(data);
					oEntityDS.setSizeLimit(2000);
					oView.setModel(oEntityDS, "entityDS");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityDS").setSelectedKey($.sap.entityId);
					that.bindDataSecurity($.sap.entityId, true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this._serverMessage(jqXHR);
				}.bind(this));
		},

		bindDataSecurity: function (entityID, Flag) {
			var oView = this.getView();
			var data = oView.getModel("entityDS").getData();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					aData.resp.push(data.resp[i]);
					break;
				}
			}

			if (aData.resp[0].gstin != undefined) {
				if (aData.resp[0].gstin.length > 1) {
					for (var i = 0; i < aData.resp[0].gstin.length; i++) {
						if (aData.resp[0].gstin[i].id === 'All') {
							aData.resp[0].gstin.splice(i, 1);
						}
					}
					aData.resp[0].gstin.unshift({
						"id": "All",
						"supplierGstin": "All"
					});
				}
			}

			if (aData.resp[0].profitCenter != undefined) {
				if (aData.resp[0].profitCenter.length > 1) {
					for (var i = 0; i < aData.resp[0].profitCenter.length; i++) {
						if (aData.resp[0].profitCenter[i].id === 'All') {
							aData.resp[0].profitCenter.splice(i, 1);
						}
					}
					aData.resp[0].profitCenter.unshift({
						"id": "All",
						"profitCenter": "All"
					});
				}
			}

			if (aData.resp[0].profitCenter2 != undefined) {
				if (aData.resp[0].profitCenter2.length > 1) {
					for (var i = 0; i < aData.resp[0].profitCenter2.length; i++) {
						if (aData.resp[0].profitCenter2[i].id === 'All') {
							aData.resp[0].profitCenter2.splice(i, 1);
						}
					}
					aData.resp[0].profitCenter2.unshift({
						"id": "All",
						"profitCenter2": "All"
					});
				}
			}

			if (aData.resp[0].plant != undefined) {
				if (aData.resp[0].plant.length > 1) {
					for (var i = 0; i < aData.resp[0].plant.length; i++) {
						if (aData.resp[0].plant[i].id === 'All') {
							aData.resp[0].plant.splice(i, 1);
						}
					}
					aData.resp[0].plant.unshift({
						"id": "All",
						"plant": "All"
					});
				}
			}
			if (aData.resp[0].division != undefined) {
				if (aData.resp[0].division.length > 1) {
					for (var i = 0; i < aData.resp[0].division.length; i++) {
						if (aData.resp[0].division[i].id === 'All') {
							aData.resp[0].division.splice(i, 1);
						}
					}
					aData.resp[0].division.unshift({
						"id": "All",
						"division": "All"
					});
				}
			}
			if (aData.resp[0].subDivision != undefined) {
				if (aData.resp[0].subDivision.length > 1) {
					for (var i = 0; i < aData.resp[0].subDivision.length; i++) {
						if (aData.resp[0].subDivision[i].id === 'All') {
							aData.resp[0].subDivision.splice(i, 1);
						}
					}
					aData.resp[0].subDivision.unshift({
						"id": "All",
						"subDivision": "All"
					});
				}
			}
			if (aData.resp[0].location != undefined) {
				if (aData.resp[0].location.length > 1) {
					for (var i = 0; i < aData.resp[0].location.length; i++) {
						if (aData.resp[0].location[i].id === 'All') {
							aData.resp[0].location.splice(i, 1);
						}
					}
					aData.resp[0].location.unshift({
						"id": "All",
						"location": "All"
					});
				}
			}
			if (aData.resp[0].salesOrg != undefined) {
				if (aData.resp[0].salesOrg.length > 1) {
					for (var i = 0; i < aData.resp[0].salesOrg.length; i++) {
						if (aData.resp[0].salesOrg[i].id === 'All') {
							aData.resp[0].salesOrg.splice(i, 1);
						}
					}
					aData.resp[0].salesOrg.unshift({
						"id": "All",
						"salesOrg": "All"
					});
				}
			}
			if (aData.resp[0].purchOrg != undefined) {
				if (aData.resp[0].purchOrg.length > 1) {
					for (var i = 0; i < aData.resp[0].purchOrg.length; i++) {
						if (aData.resp[0].purchOrg[i].id === 'All') {
							aData.resp[0].purchOrg.splice(i, 1);
						}
					}
					aData.resp[0].purchOrg.unshift({
						"id": "All",
						"purchOrg": "All"
					});
				}
			}
			if (aData.resp[0].distChannel != undefined) {
				if (aData.resp[0].distChannel.length > 1) {
					for (var i = 0; i < aData.resp[0].distChannel.length; i++) {
						if (aData.resp[0].distChannel[i].id === 'All') {
							aData.resp[0].distChannel.splice(i, 1);
						}
					}
					aData.resp[0].distChannel.unshift({
						"id": "All",
						"distChannel": "All"
					});
				}
			}
			if (aData.resp[0].userAccess1 != undefined) {
				if (aData.resp[0].userAccess1.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess1.length; i++) {
						if (aData.resp[0].userAccess1[i].id === 'All') {
							aData.resp[0].gstin.splice(i, 1);
						}
					}
					aData.resp[0].userAccess1.unshift({
						"id": "All",
						"userAccess1": "All"
					});
				}
			}
			if (aData.resp[0].userAccess2 != undefined) {
				if (aData.resp[0].userAccess2.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess2.length; i++) {
						if (aData.resp[0].userAccess2[i].id === 'All') {
							aData.resp[0].userAccess2.splice(i, 1);
						}
					}
					aData.resp[0].userAccess2.unshift({
						"id": "All",
						"userAccess2": "All"
					});
				}
			}
			if (aData.resp[0].userAccess3 != undefined) {
				if (aData.resp[0].userAccess3.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess3.length; i++) {
						if (aData.resp[0].userAccess3[i].id === 'All') {
							aData.resp[0].userAccess3.splice(i, 1);
						}
					}
					aData.resp[0].userAccess3.unshift({
						"id": "All",
						"userAccess3": "All"
					});
				}
			}
			if (aData.resp[0].userAccess4 != undefined) {
				if (aData.resp[0].userAccess4.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess4.length; i++) {
						if (aData.resp[0].userAccess4[i].id === 'All') {
							aData.resp[0].userAccess4.splice(i, 1);
						}
					}
					aData.resp[0].userAccess4.unshift({
						"id": "All",
						"userAccess4": "All"
					});
				}
			}
			if (aData.resp[0].userAccess5 != undefined) {
				if (aData.resp[0].userAccess5.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess5.length; i++) {
						if (aData.resp[0].userAccess5[i].id === 'All') {
							aData.resp[0].userAccess5.splice(i, 1);
						}
					}
					aData.resp[0].userAccess5.unshift({
						"id": "All",
						"userAccess5": "All"
					});
				}
			}
			if (aData.resp[0].userAccess6 != undefined) {
				if (aData.resp[0].userAccess6.length > 1) {
					for (var i = 0; i < aData.resp[0].userAccess6.length; i++) {
						if (aData.resp[0].userAccess6[i].id === 'All') {
							aData.resp[0].userAccess6.splice(i, 1);
						}
					}
					aData.resp[0].userAccess6.unshift({
						"id": "All",
						"userAccess6": "All"
					});
				}
			}
			if (aData.resp[0].sourceId != undefined) {
				if (aData.resp[0].sourceId.length > 1) {
					for (var i = 0; i < aData.resp[0].sourceId.length; i++) {
						if (aData.resp[0].sourceId[i].id === 'All') {
							aData.resp[0].sourceId.splice(i, 1);
						}
					}
					aData.resp[0].sourceId.unshift({
						"id": "All",
						"sourceId": "All"
					});
				}
			}
			var oDataF4 = new JSONModel();
			oDataF4.setSizeLimit(500);
			oDataF4.setData(aData.resp[0]);
			oView.setModel(oDataF4, "dataF4");
			this.hideDataSecurity(aData.resp[0]);
			if (Flag == true) {
				this.getDataPermission();
			} else {
				this.bindDataPermission(entityID);
			}
		},

		hideDataSecurity: function (data) {
			var oView = this.getView();
			var oDataF4 = new JSONModel();
			oDataF4.setData(data.items);
			oView.setModel(oDataF4, "visCol");

			// this.byId("idColPC").setVisible(data.items.profitCenter);
			// this.byId("idColPC2").setVisible(data.items.profitCenter2);
			// this.byId("idColP").setVisible(data.items.plant);
			// this.byId("idColD").setVisible(data.items.division);
			// this.byId("idColSD").setVisible(data.items.subDivision);
			// this.byId("idColL").setVisible(data.items.location);
			// this.byId("idColSO").setVisible(data.items.salesOrg);
			// this.byId("idColPO").setVisible(data.items.purchOrg);
			// this.byId("idColDC").setVisible(data.items.distChannel);
			// this.byId("idColUD1").setVisible(data.items.userAccess1);
			// this.byId("idColUD2").setVisible(data.items.userAccess2);
			// this.byId("idColUD3").setVisible(data.items.userAccess3);
			// this.byId("idColUD4").setVisible(data.items.userAccess4);
			// this.byId("idColUD5").setVisible(data.items.userAccess5);
			// this.byId("idColUD6").setVisible(data.items.userAccess6);
			// this.byId("idSourceID").setVisible(data.items.sourceId);
		},

		getDataPermission: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var Organization = "/SapOnboarding/getDataPermission.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: Organization,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntityDP = new JSONModel(data);
						oEntityDP.setSizeLimit(2000);
						oView.setModel(oEntityDP, "entityDP");
						// if (oEntityMain) {
						// 	var entityID = oEntityMain;
						// } else {
						// 	var entityID = data.resp[0].entityId;
						// }
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.bindDataPermission($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		bindDataPermission: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityDP").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idUserMappingSubmit").setEnabled(false);
				var oDataPermission = new JSONModel();
				oDataPermission.setData();
				oView.setModel(oDataPermission, "dataPermission");
				return;
			} else {
				this.getView().byId("idUserMappingSubmit").setEnabled(true);
			}
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oDataPermission = new JSONModel();
						oDataPermission.setData();
						oView.setModel(oDataPermission, "dataPermission");
						return;
					}
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						aData.resp.push({
							"sno": j + 1,
							"edit": false,
							"id": data.resp[i].items[j].id,
							"gstinIds": data.resp[i].items[j].gstinIds,
							"userName": data.resp[i].items[j].userName,
							"email": data.resp[i].items[j].email,
							"profitCenter": data.resp[i].items[j].profitCenter,
							"profitCenter2": data.resp[i].items[j].profitCenter2,
							"location": data.resp[i].items[j].location,
							"plant": data.resp[i].items[j].plant,
							"division": data.resp[i].items[j].division,
							"subDivision": data.resp[i].items[j].subDivision,
							"salesOrg": data.resp[i].items[j].salesOrg,
							"purchOrg": data.resp[i].items[j].purchOrg,
							"distChannel": data.resp[i].items[j].distChannel,
							"userAccess1": data.resp[i].items[j].userAccess1,
							"userAccess2": data.resp[i].items[j].userAccess2,
							"userAccess3": data.resp[i].items[j].userAccess3,
							"userAccess4": data.resp[i].items[j].userAccess4,
							"userAccess5": data.resp[i].items[j].userAccess5,
							"userAccess6": data.resp[i].items[j].userAccess6,
							"sourceId": data.resp[i].items[j].sourceId
						})
					}
					break;
				}
			}
			var oDataPermission = new JSONModel();
			oDataPermission.setSizeLimit(500);
			oDataPermission.setData(aData.resp);
			oView.setModel(oDataPermission, "dataPermission");
		},

		getAppPermission: function (oEvent) {
			debugger;
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			sap.ui.core.BusyIndicator.show();
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/getAppPermission.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntityAppPer = new JSONModel(data);
					oEntityAppPer.setSizeLimit(2000);
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.byId("idEntityAppPermission").setSelectedKey($.sap.entityId);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this._serverMessage(jqXHR);
				}.bind(this));
		},

		bindAppPermission: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityAppPer").getData();
			var oLen = data.resp.length;
			if (data.resp[0] === 'S') {
				this.getView().byId("idAppPermSubmit").setEnabled(false);
				var oAppPermission = new JSONModel();
				oAppPermission.setData();
				oView.setModel(oAppPermission, "AppPermission");
				return;
			} else {
				this.getView().byId("idAppPermSubmit").setEnabled(true);
			}
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oAppPermission = new JSONModel();
						oAppPermission.setData();
						oView.setModel(oAppPermission, "AppPermission");
						return;
					}
					var oLength = data.resp[i].items.length;
					for (var j = 0; j < oLength; j++) {
						aData.resp.push({
							"sno": j + 1,
							"edit": false,
							"id": data.resp[i].items[j].id,
							"userName": data.resp[i].items[j].userName,
							"email": data.resp[i].items[j].email,
						})
					}
				}
			}

			var oAppPermission = new JSONModel();
			oAppPermission.setSizeLimit(2000);
			oAppPermission.setData(aData.resp);
			oView.setModel(oAppPermission, "AppPermission");
		},

		getERPScenario: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var UserInfo = "/SapOnboarding/getScenarioPermission.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						// data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntityScenarioPermission = new JSONModel(data);
						oEntityScenarioPermission.setSizeLimit(2000);
						oView.setModel(oEntityScenarioPermission, "entityScenarioPermission");
						// if (oEntityMain) {
						// 	var entityID = oEntityMain;
						// } else {
						// 	var entityID = data.resp[0].entityId;
						// }
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.byId("idEntityScenario").setSelectedKey($.sap.entityId);
						that.bindERPScenario($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		bindERPScenario: function (entityID) {
			var oView = this.getView();
			var data = oView.getModel("entityScenarioPermission").getData();
			var oLen = data.resp.length;
			var aData = {};
			for (var i = 0; i < oLen; i++) {
				if (data.resp[i].entityId == entityID) {
					if (data.resp[i].items == undefined) {
						var oScenario = new JSONModel();
						var oScenario1 = new JSONModel();
						oScenario1.setSizeLimit(data.resp[i].gstinDetail.length);
						oScenario.setData();
						oView.setModel(oScenario, "Scenario");
						oScenario1.setData();
						oView.setModel(oScenario1, "Scenariog");
						return;
					}
					var oLength = data.resp[i].items.length;
					aData = JSON.parse(JSON.stringify(data.resp[i]));
					aData.gstinDetail.unshift({
						"gstinId": "All",
						"gstinName": "All"
					})
					for (var j = 0; j < oLength; j++) {
						aData.items[j].sno = j + 1;
						aData.items[j].edit = false;

					}
				}
			}
			var oScenario = new JSONModel();
			oScenario.setSizeLimit(2000);
			oScenario.setData(aData);
			oView.setModel(oScenario, "ScenarioBackgound");
			var oScenario1 = new JSONModel();
			oScenario1.setSizeLimit(2000);
			oScenario1.setData(aData);
			oView.setModel(oScenario1, "Scenariog");
			if (this.BEFlag === "B") {
				this.byId("idEvenBackground").setSelectedKey("B");
			} else if (this.BEFlag === "E") {
				this.byId("idEvenBackground").setSelectedKey("E");
			} else {
				this.byId("idEvenBackground").setSelectedKey("S");
			}
			this.onChangeSegment();
		},

		getEventBasedScenarioPermission: function () {
			var that = this;
			var oView = this.getView();
			var UserInfo = "/SapOnboarding/getEventBasedScenarioPermission.do";
			var oData = {};
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						// data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						var oLength = data.resp[0].items.length;
						oData = data.resp[0];
						for (var j = 0; j < oLength; j++) {
							oData.items[j].sno = j + 1;
							oData.items[j].edit = false;
						}
						var oScenario = new JSONModel();
						oScenario.setSizeLimit(2000);
						oScenario.setData(oData);
						oView.setModel(oScenario, "ScenarioEvent");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		getSFTPScenarioPermission: function () {
			var that = this;
			var oView = this.getView();
			var UserInfo = "/SapOnboarding/getSftpScenarioPermission.do";
			var oData = {};
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						// data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						var oLength = data.resp[0].items.length;
						oData = data.resp[0];
						for (var j = 0; j < oLength; j++) {
							oData.items[j].sno = j + 1;
							oData.items[j].edit = false;

						}
						var oScenario = new JSONModel();
						oScenario.setSizeLimit(2000);
						oScenario.setData(oData);
						oView.setModel(oScenario, "ScenarioSFTP");
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error : getSftpScenarioPermission.do");
					});
			});
		},

		getProfile: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var UserInfo = "/SapOnboarding/getProfilePermission.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: UserInfo,
						contentType: "application/json",
						// data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp.unshift({
							"id": "N",
							"profileName": "None"
						})
						data.resp.unshift({
							"id": "A",
							"profileName": "All"
						})

						var oProfile = new JSONModel(data);
						oProfile.setSizeLimit(2000);
						oView.setModel(oProfile, "Profile");
						// if (oEntityMain) {
						// 	var entityID = oEntityMain;
						// } else {
						// 	var entityID = data.resp[0].entityId;
						// }
						// that.byId("idEntityScenario").setSelectedKey(entityID);
						// that.bindERPScenario(entityID);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		onApply: function (oEvent) {
			var oSelectedKey = this.getView().byId("idProfileCB").getSelectedKey();
			if (oSelectedKey === "") {
				sap.m.MessageBox.warning("Select at least one profile");
				return;
			}
			var oProfileData = this.getView().getModel("Profile").getData();
			var oPermData = this.getView().getModel("APPPerm").getData();

			if (oSelectedKey === "N") {
				for (var b = 0; b < oPermData.resp.length; b++) {
					oPermData.resp[b].applicaple = false
				}
				this.getView().getModel("APPPerm").refresh();
				return;
			}

			if (oSelectedKey === "A") {
				for (var b = 0; b < oPermData.resp.length; b++) {
					oPermData.resp[b].applicaple = true
				}
				this.getView().getModel("APPPerm").refresh();
				return;
			}

			for (var i = 0; i < oProfileData.resp.length; i++) {
				if (oProfileData.resp[i].id === Number(oSelectedKey)) {
					var j = i;
					break;
				}
			}
			for (var b = 0; b < oPermData.resp.length; b++) {
				oPermData.resp[b].applicaple = false
			}
			for (var a = 0; a < oProfileData.resp[j].appProfilePermissionResps.length; a++) {
				for (var b = 0; b < oPermData.resp.length; b++) {
					if (oPermData.resp[b].permCode === oProfileData.resp[j].appProfilePermissionResps[a].promCode) {
						oPermData.resp[b].applicaple = true
					}
				}
			}
			this.getView().getModel("APPPerm").refresh();
		},

		onSelectCheckBox: function (oEvent) {
			if (oEvent.getSource().getSelected() === true) {
				var oPermData = this.getView().getModel("APPPerm").getData();
				var oData = {
					"resp": []
				};
				for (var i = 0; i < oPermData.resp.length; i++) {
					if (oPermData.resp[i].applicaple === true) {
						oData.resp.push(oPermData.resp[i]);
					}
				}
				var oTabJson = new sap.ui.model.json.JSONModel();
				oTabJson.setData(oData);
				this.getView().setModel(oTabJson, "APPPerm");
			} else {
				this.onSelectionChangeList();
			}
		},

		onEditRows: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idELRegEditRows") > -1) {
				var oSelectedItem = this.getView().byId("tabDataGSTNDet").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idUCEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableUC").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idELEEditrow") > -1) {
				var oSelectedItem = this.getView().byId("idTableDEE").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idDataSecurityEditrow") > -1) {
				var oSelectedItem = this.getView().byId("idUserMappingTable").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idAPAEditrows") > -1) {
				var oSelectedItem = this.getView().byId("idTableAP").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idOrgDataEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idOrgDataGridTable").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idERPEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableERP").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idSOEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableSO").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idCCEditRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableCC").getSelectedIndices();
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
						if (oSource.getId().indexOf("idELRegEditRows") > -1) {
							that.onEditELReg();
						} else if (oSource.getId().indexOf("idUCEditRows") > -1) {
							that.onEditUC();
						} else if (oSource.getId().indexOf("idELEEditrow") > -1) {
							that.onEditELDetails();
						} else if (oSource.getId().indexOf("idDataSecurityEditrow") > -1) {
							that.onEditDataSecurity();
						} else if (oSource.getId().indexOf("idAPAEditrows") > -1) {
							that.onEditAppPermission();
						} else if (oSource.getId().indexOf("idOrgDataEditRows") > -1) {
							that.onEditOrgData();
						} else if (oSource.getId().indexOf("idERPEditRows") > -1) {
							that.onERPEditRows();
						} else if (oSource.getId().indexOf("idSOEditRows") > -1) {
							that.onSOEditRows();
						} else if (oSource.getId().indexOf("idCCEditRows") > -1) {
							that.onCCEditRows();
						}
					}
				}
			});
		},

		onEditELReg: function () {
			var tab = this.getView().byId("tabDataGSTNDet");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("GstinDetail");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}

			var oGstinDetail = new JSONModel();
			oGstinDetail.setData(itemlist1);
			this.getView().setModel(oGstinDetail, "GstinDetail");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditUC: function () {
			var tab = this.getView().byId("idTableUC");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("UserInfo");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			var oUserInfo = new JSONModel();
			oUserInfo.setData(itemlist1);
			this.getView().setModel(oUserInfo, "UserInfo");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},
		onEditELDetails: function () {
			var tab = this.getView().byId("idTableDEE");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ELExtractInfo");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			var oELExtractInfo = new JSONModel();
			oELExtractInfo.setData(itemlist1);
			this.getView().setModel(oELExtractInfo, "ELExtractInfo");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditDataSecurity: function () {
			var tab = this.getView().byId("idUserMappingTable");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("dataPermission");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			var oDataPermission = new JSONModel();
			oDataPermission.setData(itemlist1);
			this.getView().setModel(oDataPermission, "dataPermission");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditAppPermission: function () {
			var tab = this.getView().byId("idTableAP");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("AppPermission");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			var oAppPermission = new JSONModel();
			oAppPermission.setData(itemlist1);
			this.getView().setModel(oAppPermission, "AppPermission");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onEditOrgData: function () {
			var tab = this.getView().byId("idOrgDataGridTable");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("OrgData");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}

			var oOrgData = new JSONModel();
			oOrgData.setData(itemlist1);
			this.getView().setModel(oOrgData, "OrgData");

			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onERPEditRows: function () {
			var tab = this.getView().byId("idTableERP");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("REPDetails");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}

			var oREPDetails = new JSONModel();
			oREPDetails.setData(itemlist1);
			this.getView().setModel(oREPDetails, "REPDetails");
			// this.getView().refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onSOEditRows: function () {
			var tab = this.getView().byId("idTableSO");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ServiceOptions");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1.items[sItems[i]].edit = true;
			}

			var oServiceOptions = new JSONModel();
			oServiceOptions.setData(itemlist1);
			this.getView().setModel(oServiceOptions, "ServiceOptions");
			// this.getView().refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onCCEditRows: function () {
			var tab = this.getView().byId("idTableCC");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("CompanyCodeMapping");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1.item[sItems[i]].edit = true;
			}

			var oCompanyCodeMapping = new JSONModel();
			oCompanyCodeMapping.setData(itemlist1);
			this.getView().setModel(oCompanyCodeMapping, "CompanyCodeMapping");
			// this.getView().refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onAddrow: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idELEAddrow") > -1) {
				this.onAddrowELExtract();
			} else if (oSource.getId().indexOf("idHierarchyAddrow") > -1) {
				this.insertRowValue();
			} else if (oSource.getId().indexOf("idGSTINAddrow") > -1) {
				this.onAddrowGSTINDetails();
			} else if (oSource.getId().indexOf("idUCAddrow") > -1) {
				this.onAddrowUC();
			} else if (oSource.getId().indexOf("idUserMappingAddrow") > -1) {
				this.insertRowValue();
			} else if (oSource.getId().indexOf("idOrgDataAddrow") > -1) {
				this.onAddrowOrgData();
			} else if (oSource.getId().indexOf("idERPAddrow") > -1) {
				this.onERPAddrow();
			} else if (oSource.getId().indexOf("idSPAddrow") > -1) {
				this.onSPAddrow();
			} else if (oSource.getId().indexOf("idSOAddrow") > -1) {
				this.onSOAddrow();
			} else if (oSource.getId().indexOf("idCCAddrow") > -1) {
				this.onCCAddrow();
			}
		},

		onAddrowUC: function () {
			var oModel2 = this.getView().byId("idTableUC").getModel("UserInfo");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"visible": true,
				"id": null,
				"groupCode": "",
				"userName": "",
				"firstName": "",
				"lastName": "",
				"email": "",
				"mobile": "",
				"userRole": "",
				"isFlag": "",
				"createdBy": "",
				"createdOn": ""
			};

			itemlist.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onERPAddrow: function () {
			var oModel2 = this.getView().byId("idTableERP").getModel("REPDetails");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"id": null,
				"systemId": "",
				"portocal": "",
				"sourceType": "",
				"hostName": "",
				"port": "",
				"userName": "",
				"password": "",
				"status": false
			};

			itemlist.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onSPAddrow: function () {
			var oModel2 = this.getView().byId("idTableSP").getModel("Scenario");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.items.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"id": null,
				"gstnItemList": [],
				"erpId": "",
				"scenarioId": "",
				"destName": "",
				"jobFrequency": ""

			};

			itemlist.items.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onSOAddrow: function () {
			var oModel2 = this.getView().byId("idTableSO").getModel("ServiceOptions");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.items.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"id": null,
				"gstin": "",
				"plant": "",
				"ewb": "",
				"einv": ""

			};

			itemlist.items.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onCCAddrow: function () {
			var oModel2 = this.getView().byId("idTableCC").getModel("CompanyCodeMapping");
			var itemlist = oModel2.getProperty("/");
			var vSNO = itemlist.item.length + 1;
			var emptyObject = {
				"sno": vSNO,
				"edit": true,
				"id": null,
				"companyCode": "",
				"erpId": ""

			};

			itemlist.item.push(emptyObject);
			oModel2.setProperty("/", itemlist);
		},

		onAddrowOrgData: function () {
			var oView = this.getView();
			var oModel2 = this.getView().byId("idOrgDataGridTable").getModel("OrgData");
			var oEntityValueOrg = oView.byId("idEntityValueOrg").getSelectedKey();
			var oAttributOrg = oView.byId("idAttributOrg").getSelectedKey();
			if (oEntityValueOrg === "") {
				sap.m.MessageBox.warning("Select Entity");
				return;
			} else if (oAttributOrg === "") {
				sap.m.MessageBox.warning("Select Attribut");
				return;
			} else {
				var itemlist = oModel2.getProperty("/");
				if (itemlist.length === undefined) {
					var aData = {
						"resp": []
					};
					aData.resp.push({
						"sno": 1,
						"edit": true,
						"id": null,
						"attributeName": ""
					});
					var oOrganizationData = new JSONModel(aData.resp);
					this.getView().setModel(oOrganizationData, "OrgData");
				} else {
					var vSNO = itemlist.length + 1;
					var emptyObject = {
						"sno": vSNO,
						"edit": true,
						"id": null,
						"attributeName": ""
					};

					itemlist.push(emptyObject);
					oModel2.setProperty("/", itemlist);
				}
			}
		},

		onSelectOrg: function (oEvent) {
			if (oEvent.getParameters().selected === false) {
				var selectedRow = oEvent.getSource().getBindingContext('hierar').getPath().split('/')[1];
				var oModel2 = this.getView().byId("idOrgConfig").getModel("hierar");
				var itemlist = oModel2.getProperty("/");
				itemlist[selectedRow].inword = false;
				itemlist[selectedRow].outword = false;
				oModel2.setProperty("/", itemlist);
			} else {
				var selectedRow = oEvent.getSource().getBindingContext('hierar').getPath().split('/')[1];
				var oModel2 = this.getView().byId("idOrgConfig").getModel("hierar");
				var itemlist = oModel2.getProperty("/");
				itemlist[selectedRow].inword = true;
				itemlist[selectedRow].outword = true;
				oModel2.setProperty("/", itemlist);
			}
		},

		/*===========================================================*/
		/*-------------------- Upload File --------------------------*/
		/*===========================================================*/
		onSelectChangeRadioButton: function (oEvent) {
			var oSelected = oEvent.getParameters().selectedIndex;
			if (oSelected == 0 || oSelected == 8) {
				this.byId("idEntityFileUpload").setVisible(false);
				this.byId("idEntityLabelFileUpload").setVisible(false);
				this.byId("EinvId").setVisible(false);
			} else if (oSelected == 9) {
				this.byId("idEntityFileUpload").setVisible(false);
				this.byId("idEntityLabelFileUpload").setVisible(false);
				this.byId("EinvId").setVisible(true);
			} else {
				this.byId("idEntityFileUpload").setVisible(true);
				this.byId("idEntityLabelFileUpload").setVisible(true);
				this.byId("EinvId").setVisible(false);
			}
		},

		handleUploadPress: function (oEvent) {
			var oFileUploader = this.byId("ucFileUpload");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageBox.error("Please select file to upload");
				return;
			}
			if (this.byId("idEntityFileUpload").getSelectedItem() != null) {
				var oText = this.byId("idEntityFileUpload").getSelectedItem().getText();
				var oKey = this.byId("idEntityFileUpload").getSelectedItem().getKey();
			}
			var oRadio = this.byId("rbgStatus").getSelectedIndex();
			switch (oRadio) {
			case 0:
				oFileUploader.setAdditionalData("");
				oFileUploader.setUploadUrl("/SapOnboarding/elEntitlementEntityUpload.do");
				break;
			case 1:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/elEntitlementFunctionalityUpload.do");
				break;
			case 2:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/registrationFileUpload.do");
				break;
			case 3:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/organizationFileUpload.do");
				break;
			case 4:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/userCreationFileUpload.do");
				break;
			case 5:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/dataSecuFileUpload.do");
				break;
			case 6:
				oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/appPermissionFileUpload.do");
				break;
			case 8:
				//oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/uploadVendorDefaultDueDateMaster.do");
				break;
			case 9:
				//oFileUploader.setAdditionalData(oKey + "-" + oText);
				oFileUploader.setUploadUrl("/SapOnboarding/einvoiceApplicableFileUpload.do");
				break;
			}
			sap.ui.core.BusyIndicator.show();
			oFileUploader.upload();
		},

		/*===========================================================*/
		/*========= Upload Complete =================================*/
		/*===========================================================*/
		handleUploadComplete: function (oEvent) {
			debugger;
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.resp.status !== undefined) {
				if (sResponse.resp.status === "Success") {
					this.getView().byId("ucFileUpload").setValue(null);
					MessageBox.success(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
					this.getAllAPI();
				} else {
					this.getView().byId("ucFileUpload").setValue(null);
					MessageBox.error(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
					this.getAllAPI();
				}
			} else {
				if (sResponse.hdr.status === "S") {
					this.getView().byId("ucFileUpload").setValue(null);
					MessageBox.success(sResponse.resp, {
						styleClass: "sapUiSizeCompact"
					});
					this.getAllAPI();
				} else {
					this.getView().byId("ucFileUpload").setValue(null);
					MessageBox.error(sResponse.resp, {
						styleClass: "sapUiSizeCompact"
					});
					this.getAllAPI();
				}
			}
		},

		handleDownloadPress: function () {
			var oRadio = this.byId("rbgStatus").getSelectedIndex();
			switch (oRadio) {
			case 0:
				sap.m.URLHelper.redirect("excel/EL_Entitlement_Entity.xlsx", false);
				break;
			case 1:
				sap.m.URLHelper.redirect("excel/EL_Entitlement_Funtional.xlsx", false);
				break;
			case 2:
				sap.m.URLHelper.redirect("excel/GSTIN_Template.xlsx", false);
				break;
			case 3:
				sap.m.URLHelper.redirect("excel/Organization_Updated.xlsx", false);
				break;
			case 4:
				sap.m.URLHelper.redirect("excel/User_Creation.xlsx", false);
				break;
			case 5:
				sap.m.URLHelper.redirect("excel/Data_Security.xlsx", false);
				break;
			case 6:
				sap.m.URLHelper.redirect("excel/App_Permission.xlsx", false);
				break;
			case 8:
				sap.m.URLHelper.redirect("excel/Vendors_Due_Date.xlsx", false);
				break;
			case 9:
				sap.m.URLHelper.redirect("excel/Einvoice_Applicability.xlsx", false);
				break;
			}
		},

		onSelectionChangeAttribut: function (oEvent) {
			var oView = this.getView();
			var oOrganizationData = new JSONModel(null);
			oView.setModel(oOrganizationData, "OrgData");
			var oSelectedItem = oEvent.getSource().getSelectedItem().getId().split("idAttributOrg-")[1];
			var inword = this.getView().getModel("hierarValue").getData()[Number([oSelectedItem])].inword;
			var outword = this.getView().getModel("hierarValue").getData()[Number([oSelectedItem])].outword;
			this.getView().byId("idCheckBoxOutward").setSelected(outword);
			this.getView().byId("idCheckBoxInward").setSelected(inword);
		},

		onSelectionChangeEntity: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idEntityELReg") > -1) {
				// var oEntityKey = this.byId("idEntityELReg").getSelectedKey();
				$.sap.entityId = this.byId("idEntityELReg").getSelectedKey();
				this.bindELRegistration($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityUserCreation") > -1) {
				// var oEntityKey = this.byId("idEntityUserCreation").getSelectedKey();
				$.sap.entityId = this.byId("idEntityUserCreation").getSelectedKey();
				this.bindUserCreation($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityAppPermission1") > -1) {
				// var oEntityKey = this.byId("idEntityAppPermission1").getSelectedKey();
				$.sap.entityId = this.byId("idEntityAppPermission1").getSelectedKey();
				this.bindAppPermission($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityOrg") > -1) {
				// var oEntityKey = this.byId("idEntityOrg").getSelectedKey();
				$.sap.entityId = this.byId("idEntityOrg").getSelectedKey();
				this.bindOrganization($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityValueOrg") > -1) {
				// var oEntityKey = this.byId("idEntityValueOrg").getSelectedKey();
				$.sap.entityId = this.byId("idEntityValueOrg").getSelectedKey();
				this.bindOrganizationValue($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityDS") > -1) {
				// var oEntityKey = this.byId("idEntityDS").getSelectedKey();
				$.sap.entityId = this.byId("idEntityDS").getSelectedKey();
				this.bindDataSecurity($.sap.entityId, false);
			} else if (oSource.getId().indexOf("idEntityScenario") > -1) {
				// var oEntityKey = this.byId("idEntityScenario").getSelectedKey();
				$.sap.entityId = this.byId("idEntityScenario").getSelectedKey();
				this.bindERPScenario($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntitySO") > -1) {
				// var oEntityKey = this.byId("idEntityScenario").getSelectedKey();
				$.sap.entityId = this.byId("idEntitySO").getSelectedKey();
				this.bindSeviceOption($.sap.entityId);
			} else if (oSource.getId().indexOf("idEntityFileUpload") > -1) {
				$.sap.entityId = this.byId("idEntityFileUpload").getSelectedKey();
			} else if (oSource.getId().indexOf("idEntityCC") > -1) {
				$.sap.entityId = this.byId("idEntityCC").getSelectedKey();
				this.bindCompanyCodeMapping($.sap.entityId);
			}
		},

		onSearch: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var oEntityid = this.byId("idEntityHistory").getSelectedKey();
			var oUpdatedateHistory = this.byId("idUpdatedateHistory").getValue();
			var oElValue = this.byId("idElValueHistory").getValue();
			if (oEntityid == "") {
				sap.m.MessageBox.warning("Select Entity");
				return;
			} else {

			}
			if (oUpdatedateHistory == "") {
				var FromUpdateDate = null;
				var ToUpdateDate = null;
			} else {
				var FromUpdateDate = oUpdatedateHistory.split(' - ')[0];
				var ToUpdateDate = oUpdatedateHistory.split(' - ')[1];
			}
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode,
					"entityId": oEntityid,
					"elValue": oElValue,
					"fromUpdateDate": FromUpdateDate,
					"toUpdateDate": ToUpdateDate
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/SapOnboarding/getEleEntitlementHistory.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: ELDetails,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that.bindELHistory(data)
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		bindELHistory: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			data.resp = _.sortBy(data.resp, "elId").reverse();
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					"sno": i + 1,
					"edit": false,
					"elId": data.resp[i].elId,
					"entityId": data.resp[i].entityId,
					"entityName": data.resp[i].entityName,
					"groupCode": data.resp[i].groupCode,
					"pan": data.resp[i].pan,
					"gstinList": data.resp[i].gstinList,
					"functionality": data.resp[i].functionality,
					"updateDate": data.resp[i].updateDate,
					"fromTaxPeriod": data.resp[i].fromTaxPeriod,
					"toTaxPeriod": data.resp[i].toTaxPeriod,
					"elValue": data.resp[i].elValue,
					"contractStartPeriod": data.resp[i].contractStartPeriod,
					"contractEndPeriod": data.resp[i].contractEndPeriod,
					"renewal": data.resp[i].renewal,
					"gfisId": data.resp[i].gfisId
				})
			}

			var oELHistory = new JSONModel();
			oELHistory.setData(aData.resp);
			oView.setModel(oELHistory, "history");
		},

		/*	getELExtractInfo: function (oEvent) {
			var that = this;
			var UserInfo = "/SapOnboarding/getELExtractInfo.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: UserInfo,
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					
					that.bindELExtractInfo(data);
					sap.ui.core.BusyIndicator.hide();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		bindELExtractInfo: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					"sno": i + 1,
					"elId": data.resp[i].elId,
					"groupCode": data.resp[i].groupCode,
					"entityId": data.resp[i].entityId,
					"gstin": data.resp[i].gstin,
					"functionality": data.resp[i].functionality,
					"contractStartPeriod": data.resp[i].contractStartPeriod,
					"contractEndPeriod": data.resp[i].contractEndPeriod,
					"renewal": data.resp[i].renewal
				})
			}
			var oELExtractInfo = new JSONModel();
			oELExtractInfo.setData(aData);
			oView.setModel(oELExtractInfo, "ELExtractInfo");
		},
		
		getGstinDetail: function (oEvent) {
			var that = this;
			var GstinDetail = "/SapOnboarding/getGstinDetail.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstinDetail,
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.bindGstinDetail(data);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		
		bindGstinDetail: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					sno: i + 1,
					bankAccNo: data.resp[i].bankAccNo,
					effectiveDate: data.resp[i].effectiveDate,
					entityId: data.resp[i].entityId,
					groupCode: data.resp[i].groupCode,
					gstnUsername: data.resp[i].gstnUsername,
					id: data.resp[i].id,
					primaryAuthEmail: data.resp[i].primaryAuthEmail,
					primaryContactEmail: data.resp[i].primaryContactEmail,
					secondaryContactEmail: data.resp[i].secondaryContactEmail,
					registeredEmail: data.resp[i].registeredEmail,
					registeredMobileNo: data.resp[i].registeredMobileNo,
					registrationType: data.resp[i].registrationType,
					secondaryAuthEmail: data.resp[i].secondaryAuthEmail,
					supplierGstin: data.resp[i].supplierGstin,
					turnoverFY: data.resp[i].turnoverFY,
					turnoverFYQuarter: data.resp[i].turnoverFYQuarter
				})
			}
			var oGstinDetail = new JSONModel();
			oGstinDetail.setData(aData);
			oView.setModel(oGstinDetail, "GstinDetail");
		},

		getUserInfo: function (oEvent) {
			var that = this;
			var UserInfo = "/SapOnboarding/getUserInfo.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: UserInfo,
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					
					that.bindUserInfo(data);
					sap.ui.core.BusyIndicator.hide();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		
		bindUserInfo: function (data) {
			var oView = this.getView();
			var oLen = data.resp.length;
			var aData = {
				"resp": []
			};
			for (var i = 0; i < oLen; i++) {
				aData.resp.push({
					"sno": i + 1,
					"id": data.resp[i].id,
					"userName": "Saket.Patawari@vodafone.eyasp.in",
					"userRole": "CFO",
					"groupCode": data.resp[i].groupCode,
					"firstName": data.resp[i].firstName,
					"lastName": data.resp[i].lastName,
					"email": data.resp[i].email,
					"phoneNo": data.resp[i].phoneNo,
					"hierarchy": data.resp[i].hierarchy,
					"accessValue": data.resp[i].accessValue,
					"allYesOrNo": false,
					"gstr1FileUpload": false,
					"gstr1Save": false,
					"gstr1SubmitFile": false,
					"gstr1Reports": false,
					"gstr2FileUpload": false,
					"gstr22AVsPr": false,
					"gstr2GetGstr2a": false,
					"gstr2Reports": false,
					"gstr3bFileUpload": false,
					"gstr3Save": false,
					"gstr3SubmitFile": false,
					"gstr3Reports": false,
					"gstr6FileUpload": false,
					"gstr66AVsPr": false,
					"gstr6GetGstr6a": false,
					"gstr6Reports": false,
					"gstr9And9cYesOrNo": false,
					"dashBoardYesOrNo": false,
					"intutDashBoardYesOrNo": false
				})
			}
			var oUserInfo = new JSONModel();
			oUserInfo.setData(aData);
			oView.setModel(oUserInfo, "UserInfo");
		},

		onExportTemplate: function (oEvent) {
			var aCols, aProducts, oSettings, oSheet, oFilename, oSource;
			oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idETHierarchy") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.Hierarchy;
				aProducts = this.getView().getModel("ExcelHeader").oData.HierarchyValue;
				oFilename = "HierarchyTemplate";
			} else if (oSource.getId().indexOf("idETGSTIN") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.GSTIN;
				aProducts = this.getView().getModel("ExcelHeader").oData.GSTINValue;
				oFilename = "GSTINTemplate";
			} else if (oSource.getId().indexOf("idETUser") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.User;
				aProducts = this.getView().getModel("ExcelHeader").oData.UserValue;
				oFilename = "UserTemplate";
			}
			oSettings = {
				workbook: {
					columns: aCols
				},
				dataSource: aProducts,
				fileName: oFilename
			};
			oSheet = new Spreadsheet(oSettings);
			oSheet.build().then(function () {
				sap.m.MessageToast.show('Spreadsheet export has finished');
			}).finally(function () {
				oSheet.destroy();
			});
		},
*/
		onExportExcel: function (oEvent) {
			var aCols, aProducts, oSettings, oSheet, oFilename, oSource;
			oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idEDHierarchy") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.Hierarchy;
				aProducts = this.getView().getModel("Hierarchy").getProperty('/');
				oFilename = "HierarchyData";
			} else if (oSource.getId().indexOf("idEDGSTIN") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.GSTIN;
				aProducts = this.getView().getModel("GstinDetail").getProperty('/').resp;
				oFilename = "GSTINData";
			} else if (oSource.getId().indexOf("idEDUser") > -1) {
				aCols = this.getView().getModel("ExcelHeader").oData.User;
				aProducts = this.getView().getModel("UserInfo").getProperty('/').resp;
				oFilename = "UserData";
			}

			oSettings = {
				workbook: {
					columns: aCols
				},
				dataSource: aProducts,
				fileName: oFilename
			};
			oSheet = new Spreadsheet(oSettings);
			oSheet.build().then(function () {
				sap.m.MessageToast.show('Spreadsheet export has finished');
			}).finally(function () {
				oSheet.destroy();
			});
		},

		onAddrowELExtract: function () {
			var table = this.getView().byId("idTableDEE");
			var itemlist1 = table.getModel("ELExtractInfo").getData().resp;
			var length = itemlist1.length;
			var emptyObject = {
				sno: length + 1,
				groupCode: "",
				entityId: "",
				gstin: "",
				functionality: "",
				contractStartPeriod: "",
				contractEndPeriod: "",
				renewal: "",
				elId: null
			};
			table.getModel("ELExtractInfo").getData().resp.push(emptyObject);
			table.getModel("ELExtractInfo").refresh(true);
		},

		onAddrowGSTINDetails: function () {
			var oModel2 = this.getView().byId("tabDataGSTNDet").getModel("GstinDetail");
			var itemlist1 = oModel2.getProperty("/").resp;
			var vSNO = itemlist1.length + 1;
			var emptyObject = {
				sno: vSNO,
				bankAccNo: "",
				effectiveDate: "",
				entityId: "1",
				groupCode: $.sap.groupCode,
				gstnUsername: "",
				id: null,
				primaryAuthEmail: "",
				primaryContactEmail: "",
				secondaryContactEmail: "",
				registeredEmail: "",
				registeredMobileNo: "",
				registrationType: "",
				secondaryAuthEmail: "",
				supplierGstin: "",
				turnoverFY: "",
				turnoverFYQuarter: ""
			};
			itemlist1.push(emptyObject);
			oModel2.setProperty("/resp", itemlist1);
		},

		onAddrowUserCreation: function () {
			var oModel2 = this.getView().byId("idTableUC").getModel("UserInfo");
			var zerolength = oModel2.oData.resp.length;
			if (oModel2 == undefined || zerolength == 0) {
				var resp = [];
				var emptyObject = {
					"sno": "",
					"id": "",
					"firstName": "",
					"lastName": "",
					"email": "",
					"phoneNo": "",
					"hierarchy": "",
					"accessValue": "",
					"groupCode": $.sap.groupCode,
					"entityId": 1,
					"allYesOrNo": false,
					"gstr1FileUpload": false,
					"gstr1Save": false,
					"gstr1SubmitFile": false,
					"gstr1Reports": false,
					"gstr2FileUpload": false,
					"gstr22AVsPr": false,
					"gstr2GetGstr2a": false,
					"gstr2Reports": false,
					"gstr3bFileUpload": false,
					"gstr3Save": false,
					"gstr3SubmitFile": false,
					"gstr3Reports": false,
					"gstr6FileUpload": false,
					"gstr66AVsPr": false,
					"gstr6GetGstr6a": false,
					"gstr6Reports": false,
					"gstr9And9cYesOrNo": false,
					"dashBoardYesOrNo": false,
					"intutDashBoardYesOrNo": false
				};
				resp.push(emptyObject);

				var item = [];
				item.resp = resp;
				var oTabJson = new sap.ui.model.json.JSONModel();
				oTabJson.setData(item);
				this.getView().byId("idTableUC").setModel(oTabJson, "UserInfo");
			} else {
				var oModel2 = this.getView().byId("idTableUC").getModel("UserInfo");
				var itemlist1 = oModel2.getProperty("/").resp;
				var length = itemlist1.length;

				/*	for (var i = 0; i < length; i++) {
						var a1 = itemlist1[i].id;
						var b1 = itemlist1[i].firstName;
						var c1 = itemlist1[i].lastName;
						var d1 = itemlist1[i].email;
						var e1 = itemlist1[i].phoneNo;
						var f1 = itemlist1[i].hierarchy;
						var h1 = itemlist1[i].accessValue;

						if (a1 == "" || b1 == "" || c1 == "" || d1 == "" || e1 == "" || f1 == "" || h1 == "") {
							sap.m.MessageBox.alert("Please Fiil All the Details");
							return;
						}
					} */

				var da = length - 1;
				var ser = Number(itemlist1[da].id) + 1;
				var vSNO = itemlist1.length + 1;
				var emptyObject = {
					"sno": vSNO,
					"id": null,
					"firstName": "",
					"lastName": "",
					"email": "",
					"phoneNo": "",
					"hierarchy": "",
					"accessValue": "",
					"groupCode": $.sap.groupCode,
					"entityId": 1,
					"allYesOrNo": false,
					"gstr1FileUpload": false,
					"gstr1Save": false,
					"gstr1SubmitFile": false,
					"gstr1Reports": false,
					"gstr2FileUpload": false,
					"gstr22AVsPr": false,
					"gstr2GetGstr2a": false,
					"gstr2Reports": false,
					"gstr3bFileUpload": false,
					"gstr3Save": false,
					"gstr3SubmitFile": false,
					"gstr3Reports": false,
					"gstr6FileUpload": false,
					"gstr66AVsPr": false,
					"gstr6GetGstr6a": false,
					"gstr6Reports": false,
					"gstr9And9cYesOrNo": false,
					"dashBoardYesOrNo": false,
					"intutDashBoardYesOrNo": false
				};

				itemlist1.push(emptyObject);
				oModel2.setProperty("/resp", itemlist1);
			}
		},

		onDeleteRows: function (oEvent) {
			var that = this;
			var oFlag = false;
			var oSource = oEvent.getSource();

			if (oSource.getId().indexOf("idELRegDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("tabDataGSTNDet").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idUCDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableUC").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idOrgDataDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idOrgDataGridTable").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idERPDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableERP").getSelectedIndices();
				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Select at least one record");
					return;
				}
			} else if (oSource.getId().indexOf("idSPDeleteRows") > -1) {
				var oSelectedItem = this.getView().byId("idTableSP").getSelectedIndices();
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
						if (oSource.getId().indexOf("idELRegDeleteRows") > -1) {
							that.onDeleteELReg();
						} else if (oSource.getId().indexOf("idUCDeleteRows") > -1) {
							that.onDeleteUC();
						} else if (oSource.getId().indexOf("idOrgDataDeleteRows") > -1) {
							that.onDeleteOrgData();
						} else if (oSource.getId().indexOf("idERPDeleteRows") > -1) {
							that.onDeleteERP();
						} else if (oSource.getId().indexOf("idSPDeleteRows") > -1) {
							that.onDeleteSP();
						}
					}
				}
			})

		},

		onDeleteELReg: function () {
			var tab = this.getView().byId("tabDataGSTNDet");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("GstinDetail").getData();
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
				var delelteElRegistration = "/SapOnboarding/deleteElRegistration.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: delelteElRegistration,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getELRegistration();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully Deleted");
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onDeleteUC: function () {
			var tab = this.getView().byId("idTableUC");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("UserInfo").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": this.byId("idEntityUserCreation").getSelectedItem().getKey(),
						"id": oTabData[j].id
					});
				}
				var jsonForSearch = JSON.stringify(postData);
				var deledeUserInfo = "/SapOnboarding/deleteUserCreation.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: deledeUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getUserCreation();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully Deleted");
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onDeleteOrgData: function () {
			var tab = this.getView().byId("idOrgDataGridTable");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("OrgData").getData();
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
				var deledeUserInfo = "/SapOnboarding/deleteAttributes.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: deledeUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {

							if (status == 'success') {
								that.onPressGo();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully Deleted");
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onDeleteERP: function () {
			var tab = this.getView().byId("idTableERP");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("REPDetails").getData();
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
				var deledeUserInfo = "/SapOnboarding/deleteErpRegistration.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: deledeUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully Deleted");
								that.geterpregistration();
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error("Error : deleteErpRegistration.do");
						});
				});
			}
		},

		onDeleteSP: function () {
			var tab = this.getView().byId("idTableSP");
			var sItems = tab.getSelectedIndices();
			var oEntityID = this.getView().byId("idEntityScenario").getSelectedKey();
			var that = this;
			var oTabData = this.getView().getModel("Scenario").getData();
			var postData = {
				"req": []
			}
			this.BEFlag = this.getView().byId("idEvenBackground").getSelectedKey();
			if (oTabData.items.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": oEntityID,
						"gstnsId": oTabData.items[j].gstnItemList,
						"erpId": oTabData.items[j].erpId,
						"scenarioId": oTabData.items[j].scenarioId,
						"jobType": this.getView().byId("idEvenBackground").getSelectedKey(),
						"destName": oTabData.items[j].destName
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var deledeUserInfo = "/SapOnboarding/deleteScenario.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: deledeUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully Deleted");
								that.getERPScenario();
								that.getEventBasedScenarioPermission();
								that.getSFTPScenarioPermission();
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error("Error : deleteErpRegistration.do");
						});
				});
			}
		},

		onDeleteRowsELExtract: function () {
			var deleteData = {
				"resp": []
			}
			var tab = this.getView().byId("idTableDEE");
			var sItems = tab.getSelectedIndices();
			var oModel2 = this.getView().byId("idTableDEE").getModel("ELExtractInfo");
			var itemlist1 = oModel2.getProperty("/").resp;
			var reverse = [].concat(tab.getSelectedIndices()).reverse();
			reverse.forEach(function (index) {
				if (itemlist1[index].elId != null) {
					deleteData.resp.push(itemlist1[index]);
				}
				itemlist1.splice(index, 1);
			});
			oModel2.refresh();
			tab.setSelectedIndex(-1);
			if (deleteData.resp.length > 0) {
				this.onDeleteDBELExtract(deleteData);
			} else {
				MessageBox.success("Successfully deleted");
			}
		},

		onDeleteRowsGSTINDetails: function () {
			var deleteData = {
				"resp": []
			}
			var tab = this.getView().byId("tabDataGSTNDet");
			var sItems = tab.getSelectedIndices();
			var oModel2 = this.getView().byId("tabDataGSTNDet").getModel("GstinDetail");
			var itemlist1 = oModel2.getProperty("/").resp;
			var reverse = [].concat(tab.getSelectedIndices()).reverse();
			reverse.forEach(function (index) {
				if (itemlist1[index].id != null) {
					deleteData.resp.push(itemlist1[index]);
				}
				itemlist1.splice(index, 1);
			});
			oModel2.refresh();
			tab.setSelectedIndex(-1);

			if (deleteData.resp.length > 0) {
				this.onDeleteDBGSTINDetails(deleteData);
			} else {
				MessageBox.success("Successfully deleted");
			}
		},

		onDeleteRowsUserCreation: function () {
			var deleteData = {
				"resp": []
			}
			var tab = this.getView().byId("idTableUC");
			var sItems = tab.getSelectedIndices();
			var oModel2 = this.getView().byId("idTableUC").getModel("UserInfo");
			var itemlist1 = oModel2.getProperty("/").resp;
			var reverse = [].concat(tab.getSelectedIndices()).reverse();
			reverse.forEach(function (index) {
				if (itemlist1[index].id != null) {
					deleteData.resp.push(itemlist1[index]);
				}
				itemlist1.splice(index, 1);
			});
			oModel2.refresh();
			tab.setSelectedIndex(-1);
			if (deleteData.resp.length > 0) {
				this.onDeleteDBUserCreation(deleteData);
			} else {
				MessageBox.success("Successfully deleted");
			}
		},

		onDeleteDBELExtract: function (deleteData) {
			var that = this;
			var oTabData = deleteData;
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"elId": oTabData.resp[i].elId,
						"groupCode": oTabData.resp[i].groupCode,
						"entityId": oTabData.resp[i].entityId,
						"gstin": oTabData.resp[i].gstin,
						"functionality": oTabData.resp[i].functionality,
						"contractStartPeriod": oTabData.resp[i].contractStartPeriod,
						"contractEndPeriod": oTabData.resp[i].contractEndPeriod,
						"renewal": oTabData.resp[i].renewal
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/deleteELExtract.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getELExtractInfo();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully deleted");
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onDeleteDBGSTINDetails: function (deleteData) {
			var that = this;
			var oTabData = deleteData;
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"id": oTabData.resp[i].id,
						"entityId": oTabData.resp[i].entityId,
						"supplierGstin": oTabData.resp[i].supplierGstin,
						"registrationType": oTabData.resp[i].registrationType,
						"gstnUsername": oTabData.resp[i].gstnUsername,
						"effectiveDate": oTabData.resp[i].effectiveDate,
						"registeredEmail": oTabData.resp[i].registeredEmail,
						"registeredMobileNo": oTabData.resp[i].registeredMobileNo,
						"primaryAuthEmail": oTabData.resp[i].primaryAuthEmail,
						"secondaryAuthEmail": oTabData.resp[i].secondaryAuthEmail,
						"primaryContactEmail": oTabData.resp[i].primaryContactEmail,
						"secondaryContactEmail": oTabData.resp[i].secondaryContactEmail,
						"bankAccNo": oTabData.resp[i].bankAccNo,
						"turnoverFY": oTabData.resp[i].turnoverFY,
						"groupCode": oTabData.resp[i].groupCode,
						"turnoverFYQuarter": oTabData.resp[i].turnoverFYQuarter
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveGstinDetail = "/SapOnboarding/deleteGstin.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveGstinDetail,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully deleted");
								that.getGstinDetail();
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onDeleteDBUserCreation: function (deleteData) {
			var that = this;
			var oTabData = deleteData;
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"id": oTabData.resp[i].id,
						"groupCode": oTabData.resp[i].groupCode,
						"firstName": oTabData.resp[i].firstName,
						"lastName": oTabData.resp[i].lastName,
						"email": oTabData.resp[i].email,
						"phoneNo": oTabData.resp[i].phoneNo,
						"hierarchy": oTabData.resp[i].hierarchy,
						"accessValue": oTabData.resp[i].accessValue,
						"allYesOrNo": false,
						"gstr1FileUpload": false,
						"gstr1Save": false,
						"gstr1SubmitFile": false,
						"gstr1Reports": false,
						"gstr2FileUpload": false,
						"gstr22AVsPr": false,
						"gstr2GetGstr2a": false,
						"gstr2Reports": false,
						"gstr3bFileUpload": false,
						"gstr3Save": false,
						"gstr3SubmitFile": false,
						"gstr3Reports": false,
						"gstr6FileUpload": false,
						"gstr66AVsPr": false,
						"gstr6GetGstr6a": false,
						"gstr6Reports": false,
						"gstr9And9cYesOrNo": false,
						"dashBoardYesOrNo": false,
						"intutDashBoardYesOrNo": false
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/deleteUserInfo.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getUserInfo();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success("Successfully deleted");
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error("Error");
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmit: function (oEvent) {
			var oSource = oEvent.getSource();
			if (oSource.getId().indexOf("idELESubmit") > -1) {
				this.onSubmitELDetails();
			} else if (oSource.getId().indexOf("idELRegSubmit") > -1) {
				this.onSubmitELReg();
			} else if (oSource.getId().indexOf("idUCSubmit") > -1) {
				this.onSubmitUC();
			} else if (oSource.getId().indexOf("idOrgConfigSubmit") > -1) {
				this.onSubmitOrgConfig();
			} else if (oSource.getId().indexOf("idUserMappingSubmit") > -1) {
				this.onSubmitDS();
			} else if (oSource.getId().indexOf("idAPSubmit") > -1) {
				this.onSubmitAP();
			} else if (oSource.getId().indexOf("idOrgDataSubmit") > -1) {
				this.onSubmitOrgData();
			} else if (oSource.getId().indexOf("idERPSubmit") > -1) {
				this.onERPSubmit();
			} else if (oSource.getId().indexOf("idAppPermSubmit") > -1) {
				this.onAppPermSubmit();
			} else if (oSource.getId().indexOf("idScenarioSubmit") > -1) {
				this.onScenarioSubmit();
			} else if (oSource.getId().indexOf("idSOSubmit") > -1) {
				this.onSOSubmit();
			} else if (oSource.getId().indexOf("idCCMSubmit") > -1) {
				this.onCCSubmit();
			}
		},

		onSubmitELDetails: function () {
			var tab = this.getView().byId("idTableDEE");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("ELExtractInfo").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": oTabData[j].entityId,
						"elId": oTabData[j].elId,
						"entityName": oTabData[j].entityName,
						"groupCode": oTabData[j].groupCode,
						"pan": oTabData[j].pan,
						"gstinList": oTabData[j].gstinList,
						"functionality": oTabData[j].functionality,
						"fromTaxPeriod": oTabData[j].fromTaxPeriod,
						"toTaxPeriod": oTabData[j].toTaxPeriod,
						"contractStartPeriod": oTabData[j].contractStartPeriod,
						"contractEndPeriod": oTabData[j].contractEndPeriod,
						"elValue": oTabData[j].elValue,
						"renewal": oTabData[j].renewal,
						"gfisId": oTabData[j].gfisId,
						"paceId": oTabData[j].paceId
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateLatestEleEntitlement.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getELDetails();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitELReg: function () {
			var tab = this.getView().byId("tabDataGSTNDet");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("GstinDetail").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": this.byId("idEntityELReg").getSelectedItem().getKey(),
						"entityName": this.byId("idEntityELReg").getSelectedItem().getText(),
						"id": oTabData[j].id,
						"regType": oTabData[j].regType,
						"groupCode": $.sap.groupCode,
						"supplierGstin": oTabData[j].supplierGstin,
						"gstnUsername": oTabData[j].gstnUsername,
						"bankAccNo": oTabData[j].bankAccNo,
						"regEmail": oTabData[j].regEmail,
						"regMobile": oTabData[j].regMobile,
						"effectiveDate": oTabData[j].effectiveDate,
						"primaryAuthEmail": oTabData[j].primaryAuthEmail,
						"secondaryAuthEmail": oTabData[j].secondaryAuthEmail,
						"primaryContactEmail": oTabData[j].primaryContactEmail,
						"secondaryContactEmail": oTabData[j].secondaryContactEmail,
						"turnover": oTabData[j].turnover,
						"registeredName": oTabData[j].registeredName,
						"address1": oTabData[j].address1,
						"address2": oTabData[j].address2,
						"address3": oTabData[j].address3
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateElRegistration.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								oEntityMain = that.byId("idEntityELReg").getSelectedItem().getKey();
								that.getELRegistration();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitUC: function () {
			var tab = this.getView().byId("idTableUC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("UserInfo").getData();
			var postData = {
				"req": []
			};
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (oTabData[j].userName === "") {
						sap.m.MessageBox.warning("Please enter user name");
						return;
					}
					if (oTabData[j].isFlag == "false" || oTabData[j].isFlag == false) {
						var vIsFlag = false;
					} else {
						var vIsFlag = true;
					}

					postData.req.push({
						"entityId": this.byId("idEntityUserCreation").getSelectedItem().getKey(),
						"entityName": this.byId("idEntityUserCreation").getSelectedItem().getText(),
						"groupCode": $.sap.groupCode,
						"id": oTabData[j].id,
						"userName": oTabData[j].userName,
						"firstName": oTabData[j].firstName,
						"lastName": oTabData[j].lastName,
						"email": oTabData[j].email,
						"mobile": oTabData[j].mobile,
						"userRole": oTabData[j].userRole,
						"isFlag": vIsFlag
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateUserCreation.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								oEntityMain = that.byId("idEntityUserCreation").getSelectedItem().getKey();
								that.getUserCreation();
								that.getDataSecurity();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitOrgConfig: function () {
			var tab = this.getView().byId("idOrgConfig");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("hierar").getData();

			var postData = {
				"req": []
			};
			if (oTabData.length > 0) {
				for (var i = 0; i < oTabData.length; i++) {
					if (oTabData[i].isApplicable == false) {
						var Output = 'N';
						var Iutput = 'N';
					} else {
						if (oTabData[i].outword == false) {
							var Output = 'O';
						} else {
							var Output = 'M';
						}

						if (oTabData[i].inword == false) {
							var Iutput = 'O';
						} else {
							var Iutput = 'M';
						}
					}
					postData.req.push({
						"entityId": this.byId("idEntityOrg").getSelectedItem().getKey(),
						"entityName": this.byId("idEntityOrg").getSelectedItem().getText(),
						"groupCode": $.sap.groupCode,
						"id": oTabData[i].id,
						"attCode": oTabData[i].attCode,
						"attName": oTabData[i].attName,
						"outword": Output,
						"inword": Iutput
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var updateOrganization = "/SapOnboarding/updateOrganization.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: updateOrganization,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								oEntityMain = that.byId("idEntityOrg").getSelectedItem().getKey();
								that.getOrganization();
								that.getDataSecurity();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitAP: function () {
			var tab = this.getView().byId("idTableAP");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("AppPermission").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": this.byId("idEntityAppPermission").getSelectedItem().getKey(),
						"entityName": this.byId("idEntityAppPermission").getSelectedItem().getText(),
						"groupCode": $.sap.groupCode,
						"id": oTabData[j].id,
						"userName": oTabData[j].userName,
						"gstr1FileUpload": oTabData[j].gstr1FileUpload,
						"gstr1Save": oTabData[j].gstr1Save,
						"gstr1SubmitFile": oTabData[j].gstr1SubmitFile,
						"gstr1Reports": oTabData[j].gstr1Reports,
						"gstr2FileUpload": oTabData[j].gstr2FileUpload,
						"gstr22AVsPr": oTabData[j].gstr22AVsPr,
						"gstr2GetGstr2a": oTabData[j].gstr2GetGstr2a,
						"gstr2Reports": oTabData[j].gstr2Reports,
						"gstr3bFileUpload": oTabData[j].gstr3bFileUpload,
						"gstr3Save": oTabData[j].gstr3Save,
						"gstr3SubmitFile": oTabData[j].gstr3SubmitFile,
						"gstr3Reports": oTabData[j].gstr3Reports,
						"gstr6FileUpload": oTabData[j].gstr6FileUpload,
						"gstr66AVsPr": oTabData[j].gstr66AVsPr,
						"gstr6GetGstr6a": oTabData[j].gstr6GetGstr6a,
						"gstr6Reports": oTabData[j].gstr6Reports,
						"gstr9And9cYesOrNo": oTabData[j].gstr9And9cYesOrNo,
						"dashBoardYesOrNo": oTabData[j].dashBoardYesOrNo,
						"intutDashBoardYesOrNo": oTabData[j].intutDashBoardYesOrNo
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateAppPermission.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								oEntityMain = that.byId("idEntityAppPermission").getSelectedItem().getKey();
								that.getAppPermission();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitDS: function () {
			var tab = this.getView().byId("idUserMappingTable");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("dataPermission").getData();
			var postData = {
				"req": {
					"entityId": this.byId("idEntityDS").getSelectedItem().getKey(),
					"entityName": this.byId("idEntityDS").getSelectedItem().getText(),
					"groupCode": $.sap.groupCode,
					"userDetails": []
				}
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (oTabData[j].profitCenter != undefined) {
						for (var i = 0; i < oTabData[j].profitCenter.length; i++) {
							if (oTabData[j].profitCenter[i] === 'All') {
								oTabData[j].profitCenter.splice(i, 1);
							}
						}
					}

					if (oTabData[j].profitCenter2 != undefined) {
						for (var i = 0; i < oTabData[j].profitCenter2.length; i++) {
							if (oTabData[j].profitCenter2[i] === 'All') {
								oTabData[j].profitCenter2.splice(i, 1);
							}
						}
					}

					if (oTabData[j].plant != undefined) {
						for (var i = 0; i < oTabData[j].plant.length; i++) {
							if (oTabData[j].plant[i] === 'All') {
								oTabData[j].plant.splice(i, 1);
							}
						}
					}

					if (oTabData[j].division != undefined) {
						for (var i = 0; i < oTabData[j].division.length; i++) {
							if (oTabData[j].division[i] === 'All') {
								oTabData[j].division.splice(i, 1);
							}
						}
					}

					if (oTabData[j].subDivision != undefined) {
						for (var i = 0; i < oTabData[j].subDivision.length; i++) {
							if (oTabData[j].subDivision[i] === 'All') {
								oTabData[j].subDivision.splice(i, 1);
							}
						}
					}

					if (oTabData[j].gstinIds != undefined) {
						for (var i = 0; i < oTabData[j].gstinIds.length; i++) {
							if (oTabData[j].gstinIds[i] === 'All') {
								oTabData[j].gstinIds.splice(i, 1);
							}
						}
					}
					if (oTabData[j].location != undefined) {
						for (var i = 0; i < oTabData[j].location.length; i++) {
							if (oTabData[j].location[i] === 'All') {
								oTabData[j].location.splice(i, 1);
							}
						}
					}
					if (oTabData[j].salesOrg != undefined) {
						for (var i = 0; i < oTabData[j].salesOrg.length; i++) {
							if (oTabData[j].salesOrg[i] === 'All') {
								oTabData[j].salesOrg.splice(i, 1);
							}
						}
					}
					if (oTabData[j].purchOrg != undefined) {
						for (var i = 0; i < oTabData[j].purchOrg.length; i++) {
							if (oTabData[j].purchOrg[i] === 'All') {
								oTabData[j].purchOrg.splice(i, 1);
							}
						}
					}
					if (oTabData[j].distChannel != undefined) {
						for (var i = 0; i < oTabData[j].distChannel.length; i++) {
							if (oTabData[j].distChannel[i] === 'All') {
								oTabData[j].distChannel.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess1 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess1.length; i++) {
							if (oTabData[j].userAccess1[i] === 'All') {
								oTabData[j].userAccess1.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess2 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess2.length; i++) {
							if (oTabData[j].userAccess2[i] === 'All') {
								oTabData[j].userAccess2.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess3 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess3.length; i++) {
							if (oTabData[j].userAccess3[i] === 'All') {
								oTabData[j].userAccess3.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess4 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess4.length; i++) {
							if (oTabData[j].userAccess4[i] === 'All') {
								oTabData[j].userAccess4.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess6 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess5.length; i++) {
							if (oTabData[j].userAccess5[i] === 'All') {
								oTabData[j].userAccess5.splice(i, 1);
							}
						}
					}
					if (oTabData[j].userAccess6 != undefined) {
						for (var i = 0; i < oTabData[j].userAccess6.length; i++) {
							if (oTabData[j].userAccess6[i] === 'All') {
								oTabData[j].userAccess6.splice(i, 1);
							}
						}
					}

					if (oTabData[j].sourceId != undefined) {
						for (var i = 0; i < oTabData[j].sourceId.length; i++) {
							if (oTabData[j].sourceId[i] === 'All') {
								oTabData[j].sourceId.splice(i, 1);
							}
						}
					}

					postData.req.userDetails.push({
						"userId": oTabData[j].id,
						"userName": oTabData[j].userName,
						"profitCenter": oTabData[j].profitCenter,
						"profitCenter2": oTabData[j].profitCenter2,
						"plant": oTabData[j].plant,
						"division": oTabData[j].division,
						"subDivision": oTabData[j].subDivision,
						"gstinIds": oTabData[j].gstinIds,
						"location": oTabData[j].location,
						"salesOrg": oTabData[j].salesOrg,
						"purchOrg": oTabData[j].purchOrg,
						"distChannel": oTabData[j].distChannel,
						"userAccess1": oTabData[j].userAccess1,
						"userAccess2": oTabData[j].userAccess2,
						"userAccess3": oTabData[j].userAccess3,
						"userAccess4": oTabData[j].userAccess4,
						"userAccess5": oTabData[j].userAccess5,
						"userAccess6": oTabData[j].userAccess6,
						"sourceId": oTabData[j].sourceId
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateDataPermission.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								oEntityMain = that.byId("idEntityDS").getSelectedItem().getKey();
								that.getDataSecurity();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitOrgData: function () {
			var oView = this.getView();
			var tab = this.getView().byId("idOrgDataGridTable");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var oEntityValueOrg = oView.byId("idEntityValueOrg").getSelectedKey();
			var oAttributOrg = oView.byId("idAttributOrg").getSelectedKey();
			if (oEntityValueOrg === "") {
				sap.m.MessageBox.warning("Select Entity");
				return;
			} else if (oAttributOrg === "") {
				sap.m.MessageBox.warning("Select Attribut");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("OrgData").getData();
			var postData = {
				"req": []
			};
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (oTabData[j].userName === "") {
						sap.m.MessageBox.warning("Please enter user name");
						return;
					}
					postData.req.push({
						"id": oTabData[j].id,
						"groupCode": $.sap.groupCode,
						"entityId": oEntityValueOrg,
						"attCode": oAttributOrg,
						"attributeName": oTabData[j].attributeName,
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/addAttributes.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
								that.onPressGo();
								that.getDataSecurity();
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onERPSubmit: function () {
			var tab = this.getView().byId("idTableERP");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("REPDetails").getData();
			var postData = {
				"req": []
			}
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (oTabData[j].sourceType == "SFTP") {
						if (oTabData[j].portocal != "HTTPS") {
							sap.m.MessageBox.warning("Select HTTPS protocal for SFTP source type");
							return;
						}
					}
					postData.req.push({
						"id": oTabData[j].id,
						"systemId": oTabData[j].systemId,
						"portocal": oTabData[j].portocal,
						"sourceType": oTabData[j].sourceType,
						"hostName": oTabData[j].hostName,
						"port": oTabData[j].port,
						"userName": oTabData[j].userName,
						"password": oTabData[j].password,
						"status": oTabData[j].status
					});

				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveErpInformation.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.geterpregistration();
								that.getERPScenario()
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onAppPermSubmit: function () {
			var oEntityID = this.getView().byId("idEntityAppPermission1").getSelectedKey();
			var oSelectedUserItem = this.getView().byId("idInitiateReconList").getSelectedContextPaths()[0].split("/")[1]
			var oListData = this.getView().getModel("AppPermission").getData();
			var oUserID = oListData[oSelectedUserItem].userName;
			var that = this;
			var oTabData = this.getView().getModel("APPPerm").getData();
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				var appFlag = false;
				for (var i = 0; i < oTabData.resp.length; i++) {
					if (oTabData.resp[i].applicaple) {
						appFlag = true;
					}

					postData.req.push({
						// "id": oTabData[j].id,
						"entityId": oEntityID,
						"id": oTabData.resp[i].id,
						"userName": oUserID,
						"applicaple": oTabData.resp[i].applicaple,
						"permCode": oTabData.resp[i].permCode
					});
				}

				if (!appFlag) {
					sap.m.MessageBox.warning("Select at least one permission");
					return;
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateAppPermission.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.onSelectionChangeList();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onScenarioSubmit: function () {
			var tab = this.getView().byId("idTableSP");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var sItems = tab.getSelectedIndices();
			var oEntityID = this.getView().byId("idEntityScenario").getSelectedKey();
			var that = this;
			var oTabData = this.getView().getModel("Scenario").getData();
			this.BEFlag = this.getView().byId("idEvenBackground").getSelectedKey();
			var postData = {
				"req": []
			}
			if (oTabData.items.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (this.getView().byId("idEvenBackground").getSelectedKey() === "B") {
						if (oTabData.items[j].gstnItemList.length === 0) {
							MessageBox.information("Please select at least one GSTIN");
							return;
						}
						this.removeAll(oTabData.items[j].gstnItemList);
					}

					if (oTabData.items[j].erpId === "") {
						MessageBox.information("Please select Source Id");
						return;
					}

					if (oTabData.items[j].scenarioId === "") {
						MessageBox.information("Please select Scenario Name");
						return;
					}
					if (this.getView().byId("idEvenBackground").getSelectedKey() == "S") {
						if (oTabData.items[j].endPointURI.split("://")[0] != "https") {
							MessageBox.information("URL start from https, please enter correct URL");
							return;
						}
					}
					if (this.getView().byId("idEvenBackground").getSelectedKey() !== "S") {
						if (oTabData.items[j].destName === "") {
							MessageBox.information("Please enter Destination Name");
							return;
						}
					}

					postData.req.push({
						// "id": oTabData[j].id,
						"entityId": oEntityID,
						"gstnsId": oTabData.items[j].gstnItemList,
						"erpId": oTabData.items[j].erpId,
						"scenarioId": oTabData.items[j].scenarioId,
						"destName": oTabData.items[j].destName,
						"jobFrequency": oTabData.items[j].jobFrequency,
						"endPointURI": oTabData.items[j].endPointURI,
						"jobType": this.getView().byId("idEvenBackground").getSelectedKey(),
						"startRootTag": oTabData.items[j].startRootTag,
						"endRootTag": oTabData.items[j].endRootTag,
						"companyCode": oTabData.items[j].companyCode,
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveErpScenario.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getERPScenario();
								that.getEventBasedScenarioPermission();
								that.getSFTPScenarioPermission();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSOSubmit: function () {
			var tab = this.getView().byId("idTableSO");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var sItems = tab.getSelectedIndices();
			// var oEntityID = this.getView().byId("idEntityScenario").getSelectedKey();
			var that = this;
			var oTabData = this.getView().getModel("ServiceOptions").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];

					// if (oTabData.items[j].gstin === "") {
					// 	if (oTabData.items[j].plant === "") {
					// 		MessageBox.information("Please select GSTIN or Plant");
					// 		return;
					// 	}
					// }

					if (oTabData.items[j].einv === "") {
						if (oTabData.items[j].ewb === "") {
							MessageBox.information("Please select E-Invoice or EWB");
							return;
						}
					}
					var compKeyMain = $.sap.entityId + oTabData.items[j].gstin + oTabData.items[j].plant

					postData.req.push({
						"id": oTabData.items[j].id,
						"gstin": oTabData.items[j].gstin,
						"entityId": $.sap.entityId,
						"plant": oTabData.items[j].plant,
						"ewb": oTabData.items[j].ewb === "" ? null : oTabData.items[j].ewb,
						"einv": oTabData.items[j].einv === "" ? null : oTabData.items[j].einv,
					});

					for (var m = 0; m < oTabData.items.length; m++) {
						var compKeyItem = $.sap.entityId + oTabData.items[m].gstin + oTabData.items[m].plant
						if (compKeyMain == compKeyItem) {
							if (j != m) {
								postData.req.pop();
								break;
							}
						}
					}
				}
				if (postData.req.length === 0) {
					MessageBox.information("Selected combination already present in table");
					return;
				}
				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveServiceOption.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (status == 'success') {
								that.getServiceOptions();
								MessageBox.success(data.resp.message);
							} else {
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onCCSubmit: function () {
			var tab = this.getView().byId("idTableCC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var that = this;
			var oTabData = this.getView().getModel("CompanyCodeMapping").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					// if (oTabData.items[j].gstin === "") {
					// 	if (oTabData.item[j].plant === "") {
					// 		MessageBox.information("Please select GSTIN or Plant");
					// 		return;
					// 	}
					// }

					if (oTabData.item[j].erpId === "") {
						MessageBox.information("Please select Source ID");
						return;
					}
					if (oTabData.item[j].companyCode === "") {
						MessageBox.information("Please select Company Code");
						return;
					}
					for (var m = 0; m < oTabData.sourceId.length; m++) {
						if (oTabData.sourceId[m].id == oTabData.item[j].erpId) {
							var sourceId = oTabData.sourceId[m].sourceId
						}
					}
					postData.req.push({
						"groupCode": $.sap.groupCode,
						"id": oTabData.item[j].id,
						"entityId": $.sap.entityId,
						"erpId": oTabData.item[j].erpId,
						"sourceId": sourceId,
						"companyCode": oTabData.item[j].companyCode,
						"status": oTabData.item[j].status
					});
				}
				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveCompanyCodeMapp.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							sap.ui.core.BusyIndicator.hide();
							if (data.resp.status == 'S') {
								that.getCompanycodeMapping();
								MessageBox.success(data.resp.message);
							} else {
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error("Error : saveCompanyCodeMapp");
						});
				});
			}
		},

		onDeleteELReg1: function () {
			var tab = this.getView().byId("tabDataGSTNDet");
			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("GstinDetail").getData();
			var postData = {
				"req": []
			}
			if (sItems.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					postData.req.push({
						"entityId": 1,
						"entityName": "SAP_POC_Sandbox",
						"id": oTabData[j].id,
						"regType": oTabData[j].regType,
						"groupCode": $.sap.groupCode,
						"supplierGstin": oTabData[j].supplierGstin,
						"gstnUsername": oTabData[j].gstnUsername,
						"bankAccNo": oTabData[j].bankAccNo,
						"regEmail": oTabData[j].regEmail,
						"regMobile": oTabData[j].regMobile,
						"effectiveDate": oTabData[j].effectiveDate,
						"primaryAuthEmail": oTabData[j].primaryAuthEmail,
						"secondaryAuthEmail": oTabData[j].secondaryAuthEmail,
						"primaryContactEmail": oTabData[j].primaryContactEmail,
						"secondaryContactEmail": oTabData[j].secondaryContactEmail
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/updateElRegistration.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getELRegistration();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitELExtract: function () {
			var that = this;
			var oTabData = this.getView().getModel("ELExtractInfo").getData();
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"elId": oTabData.resp[i].elId,
						"groupCode": oTabData.resp[i].groupCode,
						"entityId": oTabData.resp[i].entityId,
						"gstin": oTabData.resp[i].gstin,
						"functionality": oTabData.resp[i].functionality,
						"contractStartPeriod": oTabData.resp[i].contractStartPeriod,
						"contractEndPeriod": oTabData.resp[i].contractEndPeriod,
						"renewal": oTabData.resp[i].renewal
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveELExtract.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getELExtractInfo();
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitGSTINDetails: function () {
			var that = this;
			var oTabData = this.getView().getModel("GstinDetail").getData();
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"id": oTabData.resp[i].id,
						"entityId": oTabData.resp[i].entityId,
						"supplierGstin": oTabData.resp[i].supplierGstin,
						"registrationType": oTabData.resp[i].registrationType,
						"gstnUsername": oTabData.resp[i].gstnUsername,
						"effectiveDate": oTabData.resp[i].effectiveDate,
						"registeredEmail": oTabData.resp[i].registeredEmail,
						"registeredMobileNo": oTabData.resp[i].registeredMobileNo,
						"primaryAuthEmail": oTabData.resp[i].primaryAuthEmail,
						"secondaryAuthEmail": oTabData.resp[i].secondaryAuthEmail,
						"primaryContactEmail": oTabData.resp[i].primaryContactEmail,
						"secondaryContactEmail": oTabData.resp[i].secondaryContactEmail,
						"bankAccNo": oTabData.resp[i].bankAccNo,
						"turnoverFY": oTabData.resp[i].turnoverFY,
						"groupCode": oTabData.resp[i].groupCode,
						"turnoverFYQuarter": oTabData.resp[i].turnoverFYQuarter
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveGstinDetail = "/SapOnboarding/saveGstinDetail.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveGstinDetail,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
								that.getGstinDetail();
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onSubmitUserCreation: function () {
			var that = this;
			var oTabData = this.getView().getModel("UserInfo").getData();
			var postData = {
				"req": []
			}
			if (oTabData.resp.length > 0) {
				for (var i = 0; i < oTabData.resp.length; i++) {
					postData.req.push({
						"id": oTabData.resp[i].id,
						"groupCode": oTabData.resp[i].groupCode,
						"firstName": oTabData.resp[i].firstName,
						"lastName": oTabData.resp[i].lastName,
						"email": oTabData.resp[i].email,
						"phoneNo": oTabData.resp[i].phoneNo,
						"hierarchy": oTabData.resp[i].hierarchy,
						"accessValue": oTabData.resp[i].accessValue,
						"allYesOrNo": false,
						"gstr1FileUpload": false,
						"gstr1Save": false,
						"gstr1SubmitFile": false,
						"gstr1Reports": false,
						"gstr2FileUpload": false,
						"gstr22AVsPr": false,
						"gstr2GetGstr2a": false,
						"gstr2Reports": false,
						"gstr3bFileUpload": false,
						"gstr3Save": false,
						"gstr3SubmitFile": false,
						"gstr3Reports": false,
						"gstr6FileUpload": false,
						"gstr66AVsPr": false,
						"gstr6GetGstr6a": false,
						"gstr6Reports": false,
						"gstr9And9cYesOrNo": false,
						"dashBoardYesOrNo": false,
						"intutDashBoardYesOrNo": false
					});
				}

				var jsonForSearch = JSON.stringify(postData);
				var saveUserInfo = "/SapOnboarding/saveUserInfo.do";
				sap.ui.core.BusyIndicator.show();
				$(document).ready(function ($) {
					$.ajax({
							method: "POST",
							url: saveUserInfo,
							contentType: "application/json",
							data: jsonForSearch
						})
						.done(function (data, status, jqXHR) {
							if (status == 'success') {
								that.getUserInfo()
								sap.ui.core.BusyIndicator.hide();
								MessageBox.success(data.resp.message);
							} else {
								sap.ui.core.BusyIndicator.hide();
								MessageBox.error(data.resp.message);
							}
						})
						.fail(function (jqXHR, status, err) {
							sap.ui.core.BusyIndicator.hide();
							this._serverMessage(jqXHR);
						}.bind(this));
				}.bind(this));
			}
		},

		onButtonPress: function (oEvent) {
			this.byId('idGridInput').setVisible(true);
			// this.byId('idGridTable').setVisible(false);
		},

		onButtonPressSubmit: function (oEvent) {
			this.byId('idGridInput').setVisible(false);
			// this.byId('idGridTable').setVisible(true);
		},

		onConfigPress: function (oEvent) {
			var oButton = oEvent.getSource();
			if (!this._config) {
				this._config = sap.ui.xmlfragment("com.ey.onboarding.Fragments.HierarchyInput", this);
				this.getView().addDependent(this._config);
			}
			this._config.open();
		},

		handleCloseButton: function (oEvent) {
			this._config.close();
		},

		bindComboBox: function () {
			var aComboData = [{
				"key": "Division",
				"name": "Division",
				"flag": true
			}, {
				"key": "GSTIN",
				"name": "GSTIN",
				"flag": true
			}, {
				"key": "Sub Division",
				"name": "Sub Division",
				"flag": true
			}, {
				"key": "Profit Centre",
				"name": "Profit Centre",
				"flag": true
			}, {
				"key": "Business Unit",
				"name": "Business Unit",
				"flag": true
			}, {
				"key": "Segment",
				"name": "Segment",
				"flag": true
			}, {
				"key": "Plant",
				"name": "Plant",
				"flag": true
			}, {
				"key": "Storage Location",
				"name": "Storage Location",
				"flag": true
			}, {
				"key": "Sales Office",
				"name": "Sales Office",
				"flag": true
			}, {
				"key": "Distribution Channel",
				"name": "Distribution Channel",
				"flag": true
			}, {
				"key": "User Define 1",
				"name": "User Define 1",
				"flag": true
			}, {
				"key": "User Define 2",
				"name": "User Define 2",
				"flag": true
			}, {
				"key": "User Define 3",
				"name": "User Define 3",
				"flag": true
			}];

			var oComboData = new sap.ui.model.json.JSONModel(aComboData);
			this.getView().setModel(oComboData, "Combo");
		},

		insertRowValue: function () {
			var jsonData = this.getView().getModel("Hierarchy");
			if (jsonData == undefined) {
				var oFlag = true;
				var oGroup = "Group";
				var oEntity = "Entity";
			} else {
				var oFlag = false;
				var oGroup = "";
				var oEntity = "";
			}
			var tabdata = {
				"flag": false,
				"Group": "",
				"Entity": "",
				"GSTIN": "",
				"Level4": "",
				"Level5": "",
				"Level6": "",
				"Level7": "",
				"Level8": "",
				"Level9": "",
				"Level10": "",
				"Level11": "",
				"Level12": "",
				"Level13": "",
			};
			inputdata.push(tabdata);
			var oTabJson = new sap.ui.model.json.JSONModel();
			oTabJson.setData(inputdata);
			this.getView().setModel(oTabJson, "Hierarchy");
		},

		onSelectionChange: function (oEvent) {
			var oSelectedKey = oEvent.getSource().getSelectedKey();
			var oCombo = this.getView().getModel("Combo").getData();
			for (var i = 0; i < oCombo.length; i++) {
				if (oCombo[i].key == oSelectedKey) {
					//	oCombo.splice(i, 1);
					oCombo[i].flag = false;
					break;
				}
			}
			var oComboData = new sap.ui.model.json.JSONModel(oCombo);
			this.getView().setModel(oComboData, "Combo");
		},

		onSelectionChange1: function (oEvent) {
			var oSelectedKey = oEvent.getSource().getSelectedKey();
			var oCombo = this.getView().getModel("hierar").getData();
			var oTable = this.byId("idGridTable1");
			for (var i = 0; i < oCombo.length; i++) {
				if (oCombo[i].Attribut == oSelectedKey) {
					oTable.getColumns()[i + 3].setVisible(true);
				} else {
					oTable.getColumns()[i + 3].setVisible(false);
				}
			}
			var oComboData = new sap.ui.model.json.JSONModel(oCombo);
			this.getView().setModel(oComboData, "Combo");
		},

		onUpdateFinished: function (oEvent) {
			var oItems = this.getView().byId("idInitiateReconList").getItems()
			if (oItems.length > 0) {
				oItems[0].setSelected(true);
				this.onSelectionChangeList();
			} else {
				var oTabJson = new sap.ui.model.json.JSONModel();
				oTabJson.setData();
				this.getView().setModel(oTabJson, "APPPerm");
			}
		},

		onSelectionChangeList: function (oEvent) {
			var oPath = this.getView().byId("idInitiateReconList").getSelectedContextPaths()[0].split("/")[1];
			var oAppPermission = this.getView().getModel("AppPermission").getData();
			var SelectedKey = oAppPermission[oPath].userName;
			var oEntityValuePerm = this.getView().byId("idEntityAppPermission1").getSelectedKey();
			var postData = {
				"req": {
					"entityId": oEntityValuePerm,
					"userName": SelectedKey,
					"applicaple": true,
					"permCode": "P2"
				}
			};
			this.onUserChangeAppPermission(postData)
		},

		onUserChangeAppPermission: function (postData) {
			sap.ui.core.BusyIndicator.show();
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/getAppPermission.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._sortDataByPermCode(data.resp);
					data.resp.forEach(function (e, i) {
						e.sno = i + 1;
					})
					this.getView().setModel(new JSONModel(data), "APPPerm");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this._serverMessage(jqXHR);
				}.bind(this));
		},

		handleUploadComplete1: function (oEvent) {
			var file = oEvent.getParameter("files") && oEvent.getParameter("files")[0];
			var oControl = this;
			var aRowdata = [];
			aRowdata.splice(0, aRowdata.length);
			// if (file && window.FileReader) {
			var reader = new FileReader();
			var result = {};
			var data;
			reader.onload = function (e) {
				var data = e.target.result;
				var wb = XLSX.read(data, {
					type: 'binary'
				});
				wb.SheetNames.forEach(function (sheetName) {
					aRowdata = XLSX.utils.sheet_to_row_object_array(wb.Sheets[sheetName]);
					if (aRowdata.length > 0) {
						result[sheetName] = aRowdata;
						oControl.fnReadExcel(aRowdata); // for insert og read data to array
					}
				});
			};
			reader.readAsBinaryString(file);
			// }
		},

		fnReadExcel: function (aRowdata) {
			var inputdata = [];
			for (var i = 0; i < aRowdata.length; i++) {
				if (i > 0) {
					oFlag = false
				} else {
					oFlag = true
				}
				inputdata.push({
					"flag": oFlag,
					"Group": aRowdata[i].Group,
					"Entity": aRowdata[i].Entity,
					"GSTIN": aRowdata[i].GSTIN,
					"Level4": aRowdata[i].Level4,
					"Level5": aRowdata[i].Level5,
					"Level6": aRowdata[i].Level6,
					"Level7": aRowdata[i].Level7,
					"Level8": aRowdata[i].Level8,
					"Level9": aRowdata[i].Level9,
					"Level10": aRowdata[i].Level10,
					"Level11": aRowdata[i].Level11,
					"Level12": aRowdata[i].Level12,
					"Level13": aRowdata[i].Level13,
				});
			}

			var oTabJson = new sap.ui.model.json.JSONModel();
			oTabJson.setData(inputdata);
			this.getView().setModel(oTabJson, "Hierarchy");
		},
		onAddNewColumn: function () {
			if (Number(oColCount) > 13) {
				return;
			}
			oColCount = Number(oColCount) + 1;
			var oTable = this.byId("idGridTable");
			if (oColCount == 2) {
				oTable.getColumns()[oColCount].setVisible(true);
			} else {
				var oColCountPrev = Number(oColCount) - 1;
				var oSelectedKey = oTable.getColumns()[oColCountPrev].getMultiLabels()[0].getItems()[1].getSelectedKey()
				if (oSelectedKey == "") {
					var oLevel = oTable.getColumns()[oColCountPrev].getMultiLabels()[0].getItems()[0].getText();
					sap.m.MessageBox.information("Please Select " + oLevel + " Level");
					oColCount = Number(oColCount) - 1;
				} else {
					oTable.getColumns()[oColCount].setVisible(true);
				}
			}
		},

		onDeleteColumn: function () {
			if (oColCount < 2) {
				return;
			} else {
				var that = this;
				var oTable = this.byId("idGridTable");
				var oLevel = oTable.getColumns()[oColCount].getMultiLabels()[0].getItems()[0].getText();
				var aComoData = this.getView().getModel("Combo").getData();
				sap.m.MessageBox.show("Do you want to delete " + oLevel + " column?", {
					icon: sap.m.MessageBox.Icon.INFORMATION,
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction == "NO") {

						} else if (oAction == "YES") {
							var oSelectedKey = oTable.getColumns()[oColCount].getMultiLabels()[0].getItems()[1].getSelectedKey()
							if (oSelectedKey == "") {
								oTable.getColumns()[oColCount].setVisible(false);
								oColCount = Number(oColCount) - 1;
							} else {
								aComoData.push({
									"key": oSelectedKey,
									"name": oSelectedKey,
									"flag": true
								});
								oTable.getColumns()[oColCount].getMultiLabels()[0].getItems()[1].setSelectedKey('');
								var oComboData = new sap.ui.model.json.JSONModel(aComoData);
								that.getView().setModel(oComboData, "Combo");
								oTable.getColumns()[oColCount].setVisible(false);
								oColCount = Number(oColCount) - 1;
							}
						}
					}
				})
			}
		},

		onSelectRadioButton: function (oEvent) {
			var oTable = this.byId("idUserMappingTable");
			if (oEvent.getParameters().selectedIndex == 1) {
				oTable.getColumns()[9].setVisible(false);
				oTable.getColumns()[10].setVisible(true);
				oTable.getColumns()[11].setVisible(false);
			} else if (oEvent.getParameters().selectedIndex == 0) {
				oTable.getColumns()[9].setVisible(true);
				oTable.getColumns()[10].setVisible(false);
				oTable.getColumns()[11].setVisible(true);
			} else {
				oTable.getColumns()[9].setVisible(true);
				oTable.getColumns()[10].setVisible(true);
				oTable.getColumns()[11].setVisible(true);
			}
		},

		onSelectionChangeEL: function (oEvent) {
			var aSelectedKeys = oEvent.getSource().getSelectedKeys();
			var oLen = aSelectedKeys.length;
			var oPop = aSelectedKeys.pop();
			var oChangeKey = oEvent.getParameters().changedItem.getKey();
			var allKeys = ['All', 'GSTR-1', '2A PR Matching', 'GSTR-3B', 'GSTR-6', 'GSTR-7', 'GSTR-8', 'GSTR-9'];
			if (oPop == 'All') {
				oEvent.getSource().setSelectedKeys(allKeys);
			} else if (oChangeKey == 'All') {
				oEvent.getSource().setSelectedKeys(null);
			} else if (oChangeKey != 'All') {
				for (var i = 0; i < oLen; i++) {
					if (aSelectedKeys[i] == 'All') {
						aSelectedKeys.splice(i, 1);
						aSelectedKeys.push(oPop);
						oEvent.getSource().setSelectedKeys(aSelectedKeys);
						break;
					}
				}
			}
		},

		OnSelectAllCheckBox: function (oEvent) {
			var oFlag = false;
			var oView = this.getView();
			var oAppPermission = oView.getModel("AppPermission").getData();
			var k = oEvent.getSource().getParent().getId().split("idTableAP-rows-row")[1];
			if (oEvent.getParameters().selected == true) {
				oFlag = true;
			} else {
				oFlag = false;
			}
			oAppPermission[k].gstr1FileUpload = oFlag;
			oAppPermission[k].gstr1Reports = oFlag;
			oAppPermission[k].gstr1Save = oFlag;
			oAppPermission[k].gstr1SubmitFile = oFlag;
			oAppPermission[k].gstr2FileUpload = oFlag;
			oAppPermission[k].gstr2GetGstr2a = oFlag;
			oAppPermission[k].gstr2Reports = oFlag;
			oAppPermission[k].gstr3Reports = oFlag;
			oAppPermission[k].gstr3Save = oFlag;
			oAppPermission[k].gstr3SubmitFile = oFlag;
			oAppPermission[k].gstr3bFileUpload = oFlag;
			oAppPermission[k].gstr6FileUpload = oFlag;
			oAppPermission[k].gstr6GetGstr6a = oFlag;
			oAppPermission[k].gstr6Reports = oFlag;
			oAppPermission[k].gstr9And9cYesOrNo = oFlag;
			oAppPermission[k].gstr22AVsPr = oFlag;
			oAppPermission[k].gstr66AVsPr = oFlag;
			oAppPermission[k].dashBoardYesOrNo = oFlag;
			oAppPermission[k].intutDashBoardYesOrNo = oFlag;
			oView.getModel("AppPermission").refresh();
		},

		onButtonChange: function (oEvent) {
			var oSelectedKey = oEvent.getSource().getSelectedKey();
			if (oSelectedKey == "Status") {
				this.byId("hbRbgFileStatus").setVisible(false);
				this.byId("idHorizontalLayout").setVisible(false);
			} else {
				this.byId("hbRbgFileStatus").setVisible(true);
				this.byId("idHorizontalLayout").setVisible(true);
			}
		},

		onChangeSegment: function () {
			var SelectedKey = this.byId("idEvenBackground").getSelectedKey();
			var oData = {
				"visFlag": false,
				"visFlagJF": false,
				"visFlagS": false,
				"visFlagCC": false
			};
			var data = {};
			var data1 = this.getView().getModel("ScenarioBackgound").getData();
			var data2 = this.getView().getModel("ScenarioEvent").getData();
			var data3 = this.getView().getModel("ScenarioSFTP").getData();
			if (SelectedKey === "B") {
				oData.visFlag = true;
				oData.visFlagJF = true;
				oData.visFlagS = true;
				oData.visFlagCC = true;
				data = $.extend(true, {}, data1);;
			} else if (SelectedKey === "E") {
				oData.visFlag = false;
				oData.visFlagJF = false;
				oData.visFlagS = true;
				oData.visFlagCC = false;
				data = $.extend(true, {}, data2);;
			} else {
				oData.visFlag = false;
				oData.visFlagJF = true;
				oData.visFlagS = false;
				oData.visFlagCC = false;
				data = $.extend(true, {}, data3);;
			}

			this.getView().setModel(new JSONModel(oData), "EventBackg");
			this.getView().setModel(new JSONModel(data), "Scenario");
		},

		MakerEntity: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELRegistration = "/SapOnboarding/getElRegistration.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: ELRegistration,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						data.resp = _.sortBy(data.resp, "entityId");
						var oEntity = new JSONModel(data);
						oEntity.setSizeLimit(2000);
						oView.setModel(oEntity, "entityMaker");
						//that.getOwnerComponent().setModel(oEntity, "EntityModel"); // Added for Entity data by Bharat Gupta on 04.11.2019
						if (!$.sap.entityId) {
							$.sap.entityId = data.resp[0].entityId;
						}
						that.tabBind($.sap.entityId);
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error", {
							styleClass: "sapUiSizeCompact"
						});
					});
			});
		},

		onPressGo1: function () {
			var entity = this.byId("idEntityELReg1").getSelectedKey()
			this.tabBind(entity);
		},

		tabBind: function (entity) {
			var Request = {
				"req": {
					"entityId": entity,
					"retType": this.byId("RTid").getSelectedKey()
				}
			};

			var that = this;
			var jsonForSearch = JSON.stringify(Request);
			var saveGstinDetail = "/SapOnboarding/getApprovalWorkflowStatus.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: saveGstinDetail,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var info = data.resp.gstinInfo;
						if (info) {
							for (var i = 0; i < info.length; i++) {
								info[i].edit = true;
							}
							var gJson = that.getView().getModel("MakerSel"),
								oTabJson = new sap.ui.model.json.JSONModel();
							oTabJson.setData(info);
							that.getView().setModel(oTabJson, "Maker");
							gJson.setData(info);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		onSubmitMaker: function () {
			var tab = this.getView().byId("idTableMC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}

			var sItems = tab.getSelectedIndices();
			var that = this;
			var oTabData = this.getView().getModel("Maker").getData();
			var postData = {
				"req": {
					"entityId": this.byId("idEntityELReg1").getSelectedKey(),
					"retType": this.byId("RTid").getSelectedKey(),
					"gstinInfo": []
				}
			};
			if (oTabData.length > 0) {
				for (var i = 0; i < sItems.length; i++) {
					var j = sItems[i];
					if (oTabData[j].selectedMakers.length === 0) {
						MessageBox.error("Please select atleast one Maker");
						return;
					}
					if (oTabData[j].selectedCheckers.length === 0) {
						MessageBox.error("Please select atleast one Checker");
						return;
					}
					var selectedMakers = [],
						selectedCheckers = [];
					postData.req.gstinInfo.push({
						"gstin": oTabData[j].gstin,
						"isPresent": oTabData[j].isPresent,
						"selectedMakers": oTabData[j].selectedMakers,
						"selectedCheckers": oTabData[j].selectedCheckers
					});
				}
			}
			var that = this;
			var jsonForSearch = JSON.stringify(postData);
			var saveGstinDetail = "/SapOnboarding/submitApprovalChekerMakerDetails.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: saveGstinDetail,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {

						sap.ui.core.BusyIndicator.hide();
						MessageBox.success(data.resp.msg);
						that.onPressGo();
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						this._serverMessage(jqXHR);
					}.bind(this));
			}.bind(this));
		},

		onChecker: function (oEvt) {
			var arr1 = oEvt.oSource.getBindingContext("Maker").getObject().selectedMakers;
			var arr2 = oEvt.oSource.getBindingContext("Maker").getObject().selectedCheckers;
			var arr = []; // Array to contain match elements
			for (var i = 0; i < arr1.length; ++i) {
				for (var j = 0; j < arr2.length; ++j) {
					if (arr1[i] == arr2[j]) { // If element is in both the arrays
						arr.push(arr1[i]); // Push to arr array
					}
				}
			}

			if (arr.length !== 0) {
				MessageBox.error("A user can not be same in both maker and checker");
				oEvt.oSource.getBindingContext("Maker").getObject().selectedCheckers = [];
			}
		},

		onEditRowsMaker: function () {
			var tab = this.getView().byId("idTableMC");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("Maker");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			tab.getModel("Maker").refresh(true);
		},

		_getFeedbacks: function () {
			this.getView().setBusy(true);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/ui/getAllUserFeedbackSvyDtls.do",
					contentType: "application/json",
					data: JSON.stringify({
						"groupCode": $.sap.groupCode
					})
				})
				.done(function (data) {
					this.getView().setBusy(false);
					this._bindFeedbackData(data);
				}.bind(this))
				.fail(function (error) {
					this.getView().setBusy(false);
					console.log("Feedback Error: ", error);
				}.bind(this));
		},

		_bindFeedbackData: function (data) {
			var oFeedback = data.resp.surveyDtls.map(function (element) {
				var obj = {
					"groupCode": element.groupCode,
					"userName": element.userName,
					"submittedOn": element.submittedOn
				}
				element.results.forEach(function (e) {
					obj['answer' + e.quesCode] = e.answerDesc;
					if (e.quesCode === "Q2") {
						obj["isFileReq" + e.quesCode] = e.isFileReq;
						obj["quesId" + e.quesCode] = e.quesId;
					}
				});
				return obj;
			});
			this.getView().setModel(new JSONModel(oFeedback), "FeedbackModel");
		},

		onDownloadReport: function () {
			var aIndex = this.byId("tabUserFeedback").getSelectedIndices(),
				oFeedback = this.getView().getModel("FeedbackModel");

			if (!aIndex.length) {
				MessageBox.information("Please select record(s) to download.");
				return;
			}
			var payload = {
				"req": {}
			};
			payload.req.report = aIndex.map(function (e) {
				return oFeedback.getProperty("/" + e);
			});
			this.excelDownload(payload, "/SapOnboarding/ui/downloadFeedbackSvyDtls.do");
		},

		onDownloadFeedback: function (fileId) {
			sap.m.URLHelper.redirect("/SapOnboarding/ui/SurveyUserSuggDocument.do?confgPrmtId=" + fileId, true);
		},

		_getGroupAppPermission: function () {
			this.getView().setBusy(true);
			this._getGroupPermissionUserList()
				.then(function (result) {
					var oItem = this.byId("lGroupPerm").getItems()[0];
					this.byId("lGroupPerm").setSelectedItem(oItem);
					this._getGroupPermissionData(result[0].userName);
				}.bind(this))
				.catch(function (error) {
					this.getView().setBusy(false);
					console.log("Group Level App Permission: ", error);
				}.bind(this));
		},

		_getGroupPermissionUserList: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getGroupUserInfo.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": {
								"groupCode": $.sap.groupCode
							}
						})
					})
					.done(function (data, status, jqXHRa) {
						this.getView().setModel(new JSONModel(data.resp), "GroupUserList");
						resolve(data.resp);
					}.bind(this))
					.fail(function (error) {
						reject(JSON.parse(error.responseText));
					}.bind(this));
			}.bind(this));
		},

		_getGroupPermissionData: function (userName) {
			return new Promise(function (resolve, reject) {
				this.getView().setBusy(true);
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getGroupLevelAppPermission.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": {
								"userName": userName
							}
						})
					})
					.done(function (data, status, jqXHRa) {
						this._sortDataByPermCode(data.resp);
						data.resp.forEach(function (e, i) {
							e.sno = i + 1;
						});
						this.getView().setBusy(false);
						this.getView().setModel(new JSONModel(data.resp), "GroupPermission");
						resolve(data);
					}.bind(this))
					.fail(function (error) {
						this.getView().setBusy(false);
						reject(JSON.parse(error.responseText));
					}.bind(this));
			}.bind(this));
		},

		_sortDataByPermCode: function (data) {
			if (data.length) {
				return data.sort(function (a, b) {
					// Extract permCode and remove any non-numeric characters for comparison
					var codeA = a.permCode.replace(/\D/g, '');
					var codeB = b.permCode.replace(/\D/g, '');

					// Convert to numbers for numerical comparison
					codeA = codeA === '' ? 0 : parseInt(codeA, 10);
					codeB = codeB === '' ? 0 : parseInt(codeB, 10);

					// Compare the permCodes numerically
					return codeA - codeB;
				});
			}
			return data;
		},

		onSelectUserGP: function (oEvent) {
			var oUser = oEvent.getSource().getSelectedItem().getBindingContext("GroupUserList").getObject();
			this._getGroupPermissionData(oUser.userName);
		},

		onGrpPermissionChange: function (oEvent) {
			var oBinding = this.byId("lGroupPerm").getBinding("items"),
				sQuery = oEvent.getSource().getValue(),
				aFilters = new Filter([
					new Filter("userName", FilterOperator.Contains, sQuery),
					new Filter("email", FilterOperator.Contains, sQuery)
				], false);
			oBinding.filter(aFilters, "Application");
		},

		onSubmitGroupPermission: function () {
			var oUser = this.byId("lGroupPerm").getSelectedItem().getBindingContext("GroupUserList").getObject(),
				aPermission = this.getView().getModel("GroupPermission").getProperty("/"),
				payload = {
					"req": []
				};

			aPermission.forEach(function (e) {
				payload.req.push({
					"id": e.id,
					"userName": oUser.userName,
					"applicaple": e.applicaple,
					"permCode": e.permCode
				});
			});

			this.getView().setBusy(true);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/updateGroupLevelAppPermission.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.resp.status === "S") {
						MessageBox.success(data.resp.message);
						this._getGroupPermissionData(oUser.userName);
					} else {
						MessageBox.error(data.resp.message);
					}
					this.getView().setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setBusy(false);
				}.bind(this));
		}
	});
});