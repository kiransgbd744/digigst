sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox, MessageToast) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.App", {
		_bExpanded: true,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.App
		 */
		onInit: function () {
			this.getView().addStyleClass(this.getContentDensityClass());

			this.byId("bHome").attachBrowserEvent("tab keyup", function (oEvent) {
				this._bKeyboard = oEvent.type === "keyup";
			}, this);

			var object = {
				"entity": true,
				"group": false
			};
			this.getView().setModel(new JSONModel(object), "Display");
			this.getListEntity();
			this.getScreens();
			this.getRouter().navTo("Home");
			this.onCallAPI();
		},

		onCallAPI: function () {
			$.ajax({
				method: "GET",
				url: "/aspsapapi/downTimeMaintenanceMsg.do",
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				if (data.resp) {
					// Display the message in a control that supports HTML
					var oText = new sap.ui.core.HTML({
						content: "<div style='font-size: 1.2rem;'>" + data.resp + "</div>" // Render raw HTML
					});

					sap.m.MessageBox.show(oText, {
						title: "IMPORTANT", // Header text
						styleClass: "customMessageBox",
						actions: [sap.m.MessageBox.Action.OK], // Action buttons
						onClose: function () {}
					});
				}
			}).fail(function (jqXHR, status, err) {

			});
		},

		OnResumeSession: function () {
			$.sap.vIdelTimeCounter = 0;
			$.sap.oSessionTimer.close();
			this._resumeSession();
		},

		onLogout: function () {
			var oStorage = new Storage(Storage.Type.session, "digiGst");
			oStorage.removeAll();
			sap.m.URLHelper.redirect("/do/logout", false);
		},

		_resumeSession: function () {
			$.ajax({
				method: "GET",
				url: "/aspsapapi/sessionResume.do",
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {}).fail(function (jqXHR, status, err) {});
		},

		/**
		 * Called when usesr pressed logout button
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		handleLogoutPress: function (oEvent) {
			var oStorage = new Storage(Storage.Type.session, "digiGst");
			this.userProfile.close(oEvent.getSource());
			oStorage.removeAll();
			if (!this.getView().getModel("UserFeedback").getProperty("/isSubmitted")) {
				MessageBox.show("Can you take a brief survey regarding your User Experience with DigiGST?" +
					"\nApproximate time to complete: 2 mins", {
						title: "USER FEEDBACK",
						actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
						onClose: function (sAction) {
							if (sAction === "YES") {
								this.onPressFeedback();
							} else {
								sap.m.URLHelper.redirect("/do/logout", false);
							}
						}.bind(this)
					});
			} else {
				sap.m.URLHelper.redirect("/do/logout", false);
			}
		},

		appEntityPopup: function (self) {
			var oCombo = self.getView().byId("slAppEntity"),
				oPopOver = oCombo.getList();
		},

		onAfterRendering: function () {
			this.appEntityPopup(this);
			this._uploadFiles = [];
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/userDetails.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "UserInfo");
					this.getOwnerComponent().setModel(new JSONModel(data.resp), "UserInfo");
					$.sap.GroupCode = data.resp.groupCode;
					this._groupLevelAccess(data.resp.groupPermissions);
					this._getUserFeedback(data.resp.userId);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this._serverMessage(jqXHR.status);
				}.bind(this));
		},

		_groupLevelAccess: function (data) {
			var oPermission = {};
			data.imsRoles.forEach(function (e) {
				oPermission[e] = true;
			});
			data.permissions.forEach(function (e) {
				oPermission[e] = true;
			});
			this.getOwnerComponent().setModel(new JSONModel(oPermission), "GroupPermission");
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 */
		getListEntity: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDataSecurityForUser.do",
					contentType: "application/json",
					async: false
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "entity");
					var oData = $.extend(true, {}, data);
					oData.resp.unshift({
						entityId: "All",
						entityName: "All"
					});
					this.getOwnerComponent().setModel(new JSONModel(oData.resp), "entityAll");
					$.sap.entityID = data.resp[0].entityId;
					$.sap.entityName = data.resp[0].entityName;
					this.getSelectedEntity(data.resp[0].entityId);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					if ([500, 501, 502, 503].includes(jqXHR.status)) {
						this._serverMessage(jqXHR.status);
					} else {
						var oResp = JSON.parse(jqXHR.responseText);
						MessageBox.error(oResp.errMsg);
					}
				}.bind(this));
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {string} oEvent Eventing Object
		 */
		onEntityChange: function (oEvent) {
			var entityID = oEvent.getSource().getSelectedKey();
			var entityName = oEvent.getSource().getSelectedItem().getText();
			$.sap.entityID = entityID;
			$.sap.entityName = entityName;
			this.getSelectedEntity(entityID);
			this.getRouter().navTo("DataStatus");
			this.getRouter().navTo("Home");
			this.onChangeButton('Home');
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {string} entityId Entity Id
		 */
		getSelectedEntity: function (entityId) {
			var oEntityData = this.getView().getModel("entity").getData();
			var oDataPermission,
				oData = {
					"respData": {},
					"appPermission": {
						"R1": false,
						"R2": false,
						"R3": false,
						"R5": false,
						"R6": false,
						"R7": false,
						"R8": false,
						"R9": false,
						"R10": false,
					}
				};
			UserPermission._setPermionFalse(oData);

			for (var i = 0; i < oEntityData.length; i++) {
				if (oEntityData[i].entityId == entityId) {
					oData.respData = oEntityData[i];
					if (oData.respData.appPermission.permission &&
						(!oData.respData.appPermission.permission.includes('P2') ||
							oData.respData.appPermission.permission.includes('P1'))
					) {
						if (oData.respData.appPermission.role.includes('R1')) {
							oData.appPermission.R1 = true;
						}
						if (oData.respData.appPermission.role.includes('R2')) {
							oData.appPermission.R2 = true;
						}
						if (oData.respData.appPermission.role.includes('R3')) {
							oData.appPermission.R3 = true;
						}
						if (oData.respData.appPermission.role.includes('R5')) {
							oData.appPermission.R5 = true;
						}
						if (oData.respData.appPermission.role.includes('R6')) {
							oData.appPermission.R6 = true;
						}
						if (oData.respData.appPermission.role.includes('R7')) {
							oData.appPermission.R7 = true;
						}
					}
					if (oData.respData.appPermission.role.includes('R8')) {
						oData.appPermission.R8 = true;
					}
					if (oData.respData.appPermission.role.includes('R9')) {
						oData.appPermission.R9 = true;
					}
					if (oData.respData.appPermission.role.includes('R10')) {
						oData.appPermission.R10 = true;
					}

					oData.respData = $.extend(true, {}, oEntityData[i]);
					oData.respData.dataSecurity.gstin.sort(function (a, b) {
						return a.value.localeCompare(b.value);
					});
					oDataPermission = $.extend(true, {}, oData);
					oData.respData.dataSecurity.gstin.unshift({
						value: "All"
					});
					break;
				}
			}
			UserPermission._setPermionTrue(oData);
			var oUserPermission = new JSONModel(oData),
				oDataPermission = new JSONModel(oDataPermission);
			oUserPermission.setSizeLimit(1000);
			oDataPermission.setSizeLimit(1000);
			this.getOwnerComponent().setModel(oUserPermission, "userPermission");
			this.getOwnerComponent().setModel(oDataPermission, "DataPermission");
			this.getISDGstn(entityId);
			this.getTDSGstn(entityId);
		},

		getISDGstn: function (entityId) {
			var that = this;
			var postData = {
				"req": {
					"entityId": entityId
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr6DataSecForUser.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oData = $.extend(true, {}, data);
					data.resp[0].dataSecurity.gstin.unshift({
						"value": "All"
					});

					that.getOwnerComponent().setModel(new JSONModel(oData.resp[0].dataSecurity.gstin), "ISDGstin");
					that.getOwnerComponent().setModel(new JSONModel(data.resp[0].dataSecurity.gstin), "allISDGstin");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		getTDSGstn: function (entityId) {
			var that = this;
			var postData = {
				"req": {
					"entityId": entityId
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr7DataSecForUser.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();

					var oData = $.extend(true, {}, data);
					data.resp[0].dataSecurity.gstin.unshift({
						"value": "All"
					});

					that.getOwnerComponent().setModel(new JSONModel(oData.resp[0].dataSecurity.gstin), "TDSGstin");
					that.getOwnerComponent().setModel(new JSONModel(data.resp[0].dataSecurity.gstin), "allTDSGstin");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.App
		 */
		onExit: function () {
			var oStorage = new Storage(Storage.Type.session, "digiGst");
			oStorage.removeAll("digiGst");
		},

		/**
		 * Called when user select any menu button
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnMenuItemPress: function (oEvent) {
			var oButton = oEvent.getSource();
			// 			var eDock = sap.ui.core.Popup.Dock;
			if (oButton.getId().includes("bHome")) { // Navigation to Home Screen
				this.getRouter().navTo("Home");
				this.onChangeButton('Home');

			} else if (oButton.getId().includes("bStatus")) { // Navigation to Data Status Screen
				this.getRouter().navTo("DataStatus");

			} else if (oButton.getId().includes("bManage")) { // Navigation to Invoice Management Screen
				this.getRouter().navTo("InvoiceManage");

			} else if (oButton.getId().includes("bSACReports")) { // Navigation to SAC Reports Screen
				this.getRouter().navTo("SACReports");

			} else if (oButton.getId().includes("bGSTR1")) { // Navigation to SAC Reports Screen
				this.getRouter().navTo("GSTR1");

			} else if (oButton.getId().includes("bReports")) { //Navigate to Reports Screen
				this.getRouter().navTo("Reports");

			} else if (oButton.getId().includes("bOthers")) { //Navigate to Others Screen
				this.getRouter().navTo("Others");

			} else if (oButton.getId().includes("bLedger")) {
				this.getRouter().navTo("Ledger");
				this.onChangeButton('Ledger');

			} else if (oButton.getId().includes("bNewManage")) {
				var oFilterData = {
					"req": {
						"navType": "APP"
					}
				};
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
				this.onChangeButton('InvManageNew');

			} else if (oButton.getId().includes("bNewStatus")) {
				this.getRouter().navTo("DataStatusNew");
				this.onChangeButton('DataStatusNew');
			} else if (oButton.getId().includes("bEWB")) {
				this.getRouter().navTo("EWB");
				this.onChangeButton('EWB');
			} else if (oButton.getId().includes("bGSTNotices")) {
				this.getRouter().navTo("GSTNotices");
				this.onChangeButton('GSTNotices');
			} else if (oButton.getId().includes("bAPICallDB")) {
				this.getRouter().navTo("APICallDashboard");
				this.onChangeButton('APICallDashboard');
			} else if (oButton.getId().includes("APModule")) {
				this.getRouter().navTo("APModule", {
					contextPath: "AutoRecon",
					key: "app"
				});
				this.onChangeButton('A.I.M');
			}
		},

		onChangeButton: function (oButton) {
			var aField = ["bHome", "bNewStatus", "bNewManage", "bLedger", "bEWB", "bGSTNotices", "bAPICallDB",
				"APModule", "mbReturns", "mbReports", "bOthers", "mbSACDashboard"
			];
			aField.forEach(function (e) {
				this.byId(e).removeStyleClass("HomeCSS");
			}.bind(this));
			switch (oButton) {
			case "Home":
				this.byId("bHome").addStyleClass("HomeCSS");
				break;
			case "DataStatusNew":
				this.byId("bNewStatus").addStyleClass("HomeCSS");
				break;
			case "InvManageNew":
				this.byId("bNewManage").addStyleClass("HomeCSS");
				break;
			case "Ledger":
				this.byId("bLedger").addStyleClass("HomeCSS");
				break;
			case "EWB":
				this.byId("bEWB").addStyleClass("HomeCSS");
				break;
			case "GSTNotices":
				this.byId("bGSTNotices").addStyleClass("HomeCSS");
				break;
			case "APICallDashboard":
				this.byId("bAPICallDB").addStyleClass("HomeCSS");
				break;
			case "A.I.M":
				this.byId("APModule").addStyleClass("HomeCSS");
				break;
			case "Returns":
				this.byId("mbReturns").addStyleClass("HomeCSS");
				break;
			case "Reports":
				this.byId("mbReports").addStyleClass("HomeCSS");
				break;
			case "Others":
				this.byId("bOthers").addStyleClass("HomeCSS");
				break;
			case "SRCDashboard":
				this.byId("mbSACDashboard").addStyleClass("HomeCSS");
				break;
			}
		},

		onPrsAPICallDB: function () {
			this.getRouter().navTo("APICallDashboard");
		},

		fnMenuItemPressInvManage: function (oEvent) {
			switch (oEvent.getParameters().item.getKey()) {
			case "OutwardInward":
				var oFilterData = {
					"req": {
						"navType": "APP"
					}
				};
				this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
				this.getRouter().navTo("InvManageNew");
				break;
			case "InwardEInvoices":
				this.getRouter().navTo("inwardEINV");
				break;
			case "gstnRecords":
				this.getRouter().navTo("GstnRecords");
				break;
			}
			this.onChangeButton('InvManageNew');
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnNewReturnMenuItemPress: function (oEvent) {
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTitle = oEvent.getSource().getTitle();
			if (oTitle === "Returns") {
				this.getRouter().navTo("Returns", {
					contextPath: oSelectedKey,
					key: "app"
				});
				this.onChangeButton('Returns');
			}
		},

		/**
		 * Developed by: vinay kodam
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnNewReportMenuItemPress: function (oEvent) {
			var oSelectedKey = oEvent.getParameter("item").getKey();
			this.getRouter().navTo(oSelectedKey);
			this.onChangeButton('Reports');
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnSACMenuItemPress: function (oEvent) {
			var oSelectedKey = oEvent.getParameters().item.getKey();
			var oTitle = oEvent.getSource().getTitle();

			if (oSelectedKey === "Outward") {
				this.getRouter().navTo("SACReports");

			} else if (oSelectedKey === "GSTR-1 Inward Report" || oSelectedKey === "GSTR-1 Outward Report") {
				var that = this;
				var Payload = {
					"req": {
						"reportName": oSelectedKey,
						"entityId": $.sap.entityID
					}
				};
				var GstnsList = "/aspsapapi/callSacGstr1OutwardReport.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Payload)
					}).done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts !== undefined) {
							if (sts.status === "S") {
								$.sap.gstr1inoutwardURL = data.resp;
								if (oSelectedKey === "GSTR-1 Inward Report") {
									that.getRouter().navTo("SACInward");
								} else {
									that.getRouter().navTo("Outward_Consolidated_Report");
								}
							} else {
								MessageBox.error(data.resp);
							}
						} else {}
					}).fail(function (jqXHR, status, err) {
						MessageBox.error("Service Not Found");
					});
				});

			} else if (oSelectedKey === "Einvoice") {
				this.getRouter().navTo("SACEinvoice");

			} else if (oSelectedKey === "GSTR1") {
				this.getRouter().navTo("SACGSTR1");

			} else if (oSelectedKey === "GSTR1Outward") {
				var that = this;
				var GstnsList = "/aspsapapi/getSACDashboardUrl.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "GET",
						url: GstnsList,
						contentType: "application/json"
					}).done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts !== undefined) {
							if (sts.status === "S") {
								$.sap.gstr1outwardURL = data.resp;
								that.getRouter().navTo("gstr1outward");
							} else {
								MessageBox.error(data.resp);
							}
						} else {}
					}).fail(function (jqXHR, status, err) {
						MessageBox.error("Service Not Found");
					});
				});
			} else if (oSelectedKey === "OutwardFYReport") {
				this.getRouter().navTo("Outward_FY_Report");

			}
		},

		fnSACMenuItemPressEx: function (oEvent) {
			this.getRouter().navTo("Extraction");
		},

		fnSACMenuItemPressEx1: function (oEvent) {
			this.getRouter().navTo("oExtraction1");
		},

		fnSACDashboard: function (oEvent) {
			var key = oEvent.getParameters().item.getKey();
			this.onChangeButton('SRCDashboard');
			switch (key) {
			case "SACOutward1":
				this.getRouter().navTo("SACDashboardOutward");
				break;
			case "SACOutward2":
				this.getRouter().navTo("SACDashboardOutward2");
				break;
			case "SACOutward3":
				this.getRouter().navTo("SACDashboardOutward3");
				break;
			case "SACOutward":
				this.getRouter().navTo("SACDashboardOutward");
				break;
			case "Inward":
				this.getRouter().navTo("InwardSAC");
				break;
			case "Einvoice":
				this.getRouter().navTo("EInvoiceDashboard");
				break;
			case "SACInwardSupply2":
				this.getRouter().navTo("InwardSAC2");
				break;
			case "SACLiabilityPayment":
				this.getRouter().navTo("SACLiabilityPayment");
				break;
			}
		},

		/**
		 * Method called to navigate for Others views
		 * Develped by: Bharat Gupta on 08.04.2020
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {string} route Router path name
		 */
		onOthersMenuItemPress: function (oEvent) {
			var oSelectedKey = oEvent.getParameters().item.getKey();
			this.getRouter().navTo("Others", {
				contextPath: oSelectedKey
			});
			this.onChangeButton('Others');
		},

		onPrsAPModule: function (oEvent) {
			this.getRouter().navTo("APModule", {
				contextPath: "AutoRecon",
				key: "app"
			});
		},

		/**
		 * Called to open User Profile dialog
		 * Developed by: Bharat Gupta
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnAvatarPress: function (oEvent) {
			if (!this.userProfile) {
				this.userProfile = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.UserProfile", this);
				this.userProfile.setPlacement("Bottom");
				this.getView().addDependent(this.userProfile);
			}
			this.userProfile.openBy(oEvent.getSource());
		},

		onChangeApp: function (oEvt) {
			var oModel = this.getView().getModel("Display"),
				state = oEvt.getSource().getState();

			oModel.setProperty("/group", state);
			oModel.setProperty("/entity", !state);
			oModel.refresh(true);
			if (!state) {
				this.getRouter().navTo("Home");
			} else {
				var aPermission = this.getModel("UserInfo").getProperty("/groupPermissions/permissions");
				if (!aPermission.length || aPermission.includes("G1")) {
					var view = "compHistory";
				} else if (aPermission.includes("G2")) {
					view = "dashboard";
				} else if (aPermission.includes("G3")) {
					view = "authToken";
				} else if (aPermission.includes("G4")) {
					view = "reconRequest";
				}
				this.onGrpLvlMenuItem(view);
			}
		},

		onGrpLvlMenuItem: function (view) {
			switch (view) {
			case "compHistory":
				this.getRouter().navTo("GrpLComplianceHistory");
				break;
			case "dashboard":
				this.getRouter().navTo("GDashboardOutward1");
				break;
			case "authToken":
				this.getRouter().navTo("GAuthToken");
				break;
			case "reconRequest":
				this.getRouter().navTo("GReconReqStats");
				break;
			}
			this.onChangeGroupButton(view)
		},

		fnGourpDashboard: function (key) {
			this.getRouter().navTo(key);
			this.onChangeGroupButton('dashboard');
		},

		onChangeGroupButton: function (oButton) {
			var aField = ["bGrpCompHistory", "bGrpDashboard", "bGrpAuthToken", "bGrpReconReq"];
			aField.forEach(function (e) {
				this.byId(e).removeStyleClass("HomeCSS");
			}.bind(this));
			switch (oButton) {
			case "compHistory":
				this.byId("bGrpCompHistory").addStyleClass("HomeCSS");
				break;
			case "dashboard":
				this.byId("bGrpDashboard").addStyleClass("HomeCSS");
				break;
			case "authToken":
				this.byId("bGrpAuthToken").addStyleClass("HomeCSS");
				break;
			case "reconRequest":
				this.byId("bGrpReconReq").addStyleClass("HomeCSS");
				break;
			}
		},

		getScreens: function (data) {
			var vScreen1 = window.location.href.split('#/')[1];
			if (vScreen1 === undefined) {
				this.getRouter().navTo("Home");
				this.onChangeButton('Home');
			} else {
				if (vScreen1 === "InvPOC") {
					this.getRouter().navTo("InvPOC", {
						contextPath: "SalesReceivables"
					});
				} else {
					this.getRouter().navTo(vScreen1);
				}
				this.onChangeButton(vScreen1);
			}
		},

		fnHelpPress: function (oEvent) {
			var key = oEvent.getParameter("item").getKey();
			if (key === "UserFeedback") {
				this.onPressFeedback();
			}
		},

		createContentFeedback: function (sId, oContext) {
			var oUIControl = new sap.m.VBox().addStyleClass("sapUiSmallMarginBottom");
			oUIControl.addItem(new sap.m.Label({
				text: "{UserFeedback>quesCode}. {UserFeedback>ques}",
				required: true,
				// width: "63rem",
				wrapping: true
			}).addStyleClass("feedbackFont"));

			if (oContext.getProperty("keyType") === "RA") {
				var oItem = oContext.getProperty("items"),
					oGrid = new sap.ui.layout.Grid({
						defaultSpan: "XL6 L6 M6 S12",
						width: "50rem"
					});
				oItem.forEach(function (item, i) {
					var oHbox = new sap.m.HBox();
					oHbox.addItem(new sap.m.RatingIndicator({
						maxValue: 5,
						iconSize: "32px",
						value: "{UserFeedback>items/" + i + "/answerDesc}"
					}));
					oGrid.addContent(new sap.m.Text({
						text: "{UserFeedback>items/" + i + "/ques}",
						width: "100%",
						textAlign: "End"
					}).addStyleClass("sapUiSmallMarginTop feedbackFont"));
					oGrid.addContent(oHbox);
				});
				oUIControl.addItem(oGrid);

			} else if (oContext.getProperty("keyType") === "TA" || oContext.getProperty("keyType") === "TAF") {
				var oControl = new sap.m.TextArea({
					cols: 126,
					rows: 3,
					maxLength: 250,
					value: "{UserFeedback>answerDesc}"
				}).addStyleClass("sapUiTinyMarginBegin");
				oUIControl.addItem(oControl);

				if (oContext.getProperty("keyType") === "TAF") {
					var oFileUpload = new sap.ui.unified.FileUploader(this.createId("FileUploader" + oContext.getProperty("quesCode")), {
						name: "myFileUpload",
						fileType: "pdf",
						maximumFileSize: 2,
						tooltip: "Upload your file to the local server",
						fileSizeExceed: [this.handleFileSizeExceed, this],
						typeMissmatch: [this.handleTypeMissmatch, this],
						change: [this.handleUploadChange, this],
						width: "100%"
					}).addStyleClass("ufUpload buttoncolorSec sapUiTinyMarginBegin");
					oUIControl.addItem(oFileUpload);
				}
			} else if (oContext.getProperty("keyType") === "M") {
				var oHbox = new sap.m.HBox({
						direction: "Column",
					}).addStyleClass("sapUiTinyMarginBegin"),
					oItem = oContext.getProperty("items");

				oItem.forEach(function (item, i) {
					oHbox.addItem(new sap.m.CheckBox({
						name: "{UserFeedback>items/" + i + "/answerCode}",
						text: "{UserFeedback>items/" + i + "/answerDesc}",
						selected: "{UserFeedback>items/" + i + "/select}"
					}).addStyleClass("cbFontSize"));
				});
				oUIControl.addItem(oHbox);

			} else if (oContext.getProperty("keyType") === "R") {
				var oItem = oContext.getProperty("items"),
					oRbg = new sap.m.RadioButtonGroup({
						columns: 2,
						selectedIndex: "{UserFeedback>index}"
					}),
					oInput = new sap.m.Input({
						value: "{UserFeedback>answerDesc}",
						visible: "{=!${UserFeedback>index}}",
						maxLength: 50,
						width: "30rem"
					});
				oItem.forEach(function (e) {
					oRbg.addButton(new sap.m.RadioButton({
						text: e.answerDesc
					}));
				});
				oUIControl.addItem(oRbg.addStyleClass("sapUiTinyMarginBegin"));
				oUIControl.addItem(oInput.addStyleClass("sapUiTinyMarginBegin"));
			}
			return oUIControl;
		},

		_getUserFeedback: function (userId) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getFeedbackSvyDtls.do",
					contentType: "application/json",
					data: JSON.stringify({
						"userName": userId
					})
				})
				.done(function (data) {
					this.getView().setModel(new JSONModel(data.resp), "UserFeedback");
				}.bind(this))
				.fail(function (err) {}.bind(this));
		},

		onPressFeedback: function () {
			if (!this._dUserFeedback) {
				this._dUserFeedback = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.UserFeedback", this);
				this.getView().addDependent(this._dUserFeedback);
			}
			var oData = this.getView().getModel("UserFeedback").getProperty("/");
			oData.results.forEach(function (element) {
				if (element.keyType === "M") {
					var aCheck = element.answerDesc ? element.answerDesc.split("*") : [];
					element.items.forEach(function (t) {
						t.select = aCheck.includes(t.answerCode);
					});
				} else if (element.keyType === "R") {
					element.index = +!element.answerDesc;
				}
			});
			this._dUserFeedback.setModel(new JSONModel(oData), "UserFeedback");
			this._dUserFeedback.open();
		},

		handleFileSizeExceed: function (oEvent) {
			MessageBox.error("Please upload a pdf file with size less than 2 mb");
		},

		handleTypeMissmatch: function (oEvent) {
			MessageBox.error("Please upload a file with extension .pdf");
		},

		handleUploadChange: function (oEvent) {
			var vId = oEvent.getSource().getId(),
				obj = this._uploadFiles.find(function (item) {
					return (item.id === vId);
				});
			if (!obj) {
				this._uploadFiles.push({
					"id": vId,
					"file": oEvent.getParameter("files")[0]
				});
			} else {
				obj.file = oEvent.getParameter("files")[0];
			}
		},

		_validateFeedback: function (data) {
			var aQues = [],
				msg = "";

			data.results.forEach(function (element) {
				if (element.keyType === "RA") {
					element.items.forEach(function (e) {
						if (!e.answerDesc) {
							msg += this._errorFeedbackMessage(element.quesCode, aQues, element.keyType);
						}
					}.bind(this));
				} else if (element.keyType === "M") {
					var arr = element.items.filter(function (e) {
						return e.select;
					});
					if (!arr.length) {
						msg += this._errorFeedbackMessage(element.quesCode, aQues, element.keyType);
					}
				} else if (element.keyType === "R") {
					if (!element.index && !element.answerDesc) {
						msg += this._errorFeedbackMessage(element.quesCode, aQues, element.keyType);
					}
				} else {
					if (!element.answerDesc) {
						msg += this._errorFeedbackMessage(element.quesCode, aQues, element.keyType);
					}
				}
			}.bind(this));
			return msg;
		},

		_errorFeedbackMessage: function (quesCode, arr, type) {
			if (!arr.includes(quesCode)) {
				arr.push(quesCode);
				return ("\t" + arr.length + '. Answer' +
					(['RA'].includes(type) ? '(s)' : '') + ' to Question ' +
					quesCode.substr(1) + " is missing\n");
			}
			return "";
		},

		_createFeedbackPayload: function (data) {
			var payload = {
				"req": {
					"userName": data.userName,
					"results": []
				}
			};
			data.results.forEach(function (element) {
				var obj = {
					"quesId": element.quesId,
					"quesCode": element.quesCode,
					"keyType": element.keyType,
					"ques": element.ques
				};
				if (element.keyType === "RA") {
					obj.items = element.items.map(function (el) {
						return {
							quesId: el.quesId,
							ques: el.ques,
							answerDesc: el.answerDesc || 0
						};
					});
				} else if (element.keyType === "M") {
					obj.answerDesc = "";
					element.items.forEach(function (e) {
						if (e.select) {
							obj.answerDesc += (!obj.answerDesc ? '' : '*') + e.answerCode;
						}
					});
				} else if (element.keyType === "R") {
					obj.answerDesc = (!element.index ? element.answerDesc : undefined);
				} else {
					obj.answerDesc = element.answerDesc;
				}
				payload.req.results.push(obj);
			});
			return JSON.stringify(payload);
		},

		onCloseFeedback: function (type) {
			if (type === "submit") {
				var oFeedback = this._dUserFeedback.getModel("UserFeedback").getProperty("/"),
					msg = this._validateFeedback(oFeedback),
					form = new FormData();
				if (msg) {
					msg = "Please answer all the questions.\n\n" + msg;
					MessageBox.error(msg);
					return;
				}
				if (!this._validateEmail(oFeedback)) {
					MessageBox.error("Please enter valid email.");
					return;
				}

				var obj = oFeedback.results.find(function (item) {
					return (item.keyType === "TAF");
				});
				this._uploadFiles.forEach(function (e) {
					if (e.id.includes("FileUploader" + obj.quesCode)) {
						form.append("file", e.file, e.file.name);
					}
				});
				form.append("data", this._createFeedbackPayload(oFeedback));
				this._dUserFeedback.setBusy(true);
				$.ajax({
						"method": "POST",
						"url": "/aspsapapi/saveFeedbackSvyDtls.do",
						"mimeType": "multipart/form-data",
						"processData": false,
						"contentType": false,
						"data": form
					})
					.done(function (data) {
						this._dUserFeedback.setBusy(false);
						var obj = JSON.parse(data);
						if (obj.hdr.status === "S") {
							this.getView().getModel("UserFeedback").setProperty("/isSubmitted", true);
							MessageBox.success(
								"Thank you for taking out time to  provide your valuable feedback. Press the close button to go to Home screen", {
									actions: [sap.m.MessageBox.Action.CLOSE],
									onClose: function () {
										this._dUserFeedback.close();
									}.bind(this)
								});
						} else {
							MessageBox.error(obj.msg);
						}
					}.bind(this))
					.fail(function (err) {
						this._dUserFeedback.setBusy(false);
					}.bind(this));
			} else {
				this._dUserFeedback.close();
			}
		},

		_validateEmail: function (data) {
			var flag = true;
			data.results.forEach(function (item) {
				if (item.quesCode === 'Q5' && !item.index && !item.answerDesc.match(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)) {
					flag = false;
				}
			});
			return flag;
		},

		onPrivacyNotice: function () {
			if (!this._pdfViewer) {
				this._pdfViewer = new sap.m.PDFViewer({
					'isTrustedSource': true
				});
				this.getView().addDependent(this._pdfViewer);
			}
			this._pdfViewer.setSource("excel/general/Privacy_Notice.pdf");
			this._pdfViewer.setTitle("Privacy Notice");
			this._pdfViewer.open();
		}
	});
});