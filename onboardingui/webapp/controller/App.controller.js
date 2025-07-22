sap.ui.define([
	"com/ey/onboarding/controller/BaseController",
	"sap/m/ResponsivePopover",
	"sap/m/MessagePopover",
	"sap/m/ActionSheet",
	"sap/m/Button",
	"sap/m/Link",
	"sap/m/MessagePopoverItem",
	"sap/ui/core/CustomData",
	"sap/m/MessageToast",
	"sap/ui/Device",
	"sap/ui/core/syncStyleClass",
	"sap/m/library"
], function (BaseController, ResponsivePopover, MessagePopover, ActionSheet, Button, Link, MessagePopoverItem,
	CustomData, MessageToast, Device, syncStyleClass, mobileLibrary) {

	"use strict";

	// shortcut for sap.m.PlacementType
	// 	var PlacementType = mobileLibrary.PlacementType;

	// shortcut for sap.m.VerticalPlacementType
	// 	var VerticalPlacementType = mobileLibrary.VerticalPlacementType;

	// shortcut for sap.m.ButtonType
	var ButtonType = mobileLibrary.ButtonType;

	return BaseController.extend("com.ey.onboarding.controller.App", {
		_bExpanded: true,

		onInit: function () {
			this.getView().addStyleClass(this.getOwnerComponent().getContentDensityClass());

			// if the app starts on desktop devices with small or meduim screen size, collaps the sid navigation
			if (Device.resize.width <= 1024) {
				this.onSideNavButtonPress();
			}
			Device.media.attachHandler(function (oDevice) {
				if ((oDevice.name === "Tablet" && this._bExpanded) || oDevice.name === "Desktop") {
					this.onSideNavButtonPress();
					// set the _bExpanded to false on tablet devices extending and collapsing of side navigation should be done
					// when resizing from desktop to tablet screen sizes)
					this._bExpanded = (oDevice.name === "Desktop");
				}
			}.bind(this));
			this.getOwnerComponent().setModel(new sap.ui.model.json.JSONModel({
				"code": null
			}), "GroupCdModel");
			this._bindGroupCode();
		},

		_bindGroupCode: function () {
			this.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getAllGroupCodes.do",
						contentType: "application/json",
						async: false
					})
					.done(function (data, status, jqXHR) {
						this.getView().setBusy(false);
						var oGroupModel = new sap.ui.model.json.JSONModel();
						oGroupModel.setData(data.resp);
						oGroupModel.setSizeLimit(500);
						this.getOwnerComponent().setModel(oGroupModel, "GroupCode");
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setBusy(false);
						sap.m.MessageBox.error("Error");
					}.bind(this));
			}.bind(this));
		},

		fnHandleUserMenuItemPress: function (oEvent) {
			MessageToast.show(oEvent.getSource().getText() + " was pressed");
		},

		onUserNamePress: function (oEvent) {
			var oBundle = this.getModel("i18n").getResourceBundle();
			// close message popover
			var oMessagePopover = this.byId("errorMessagePopover");
			if (oMessagePopover && oMessagePopover.isOpen()) {
				oMessagePopover.destroy();
			}
			var oActionSheet = new ActionSheet(this.getView().createId("userMessageActionSheet"), {
				title: oBundle.getText("userHeaderTitle"),
				showCancelButton: false,
				buttons: [
					new Button({
						id: "bLogout",
						text: "Logout",
						type: ButtonType.Transparent,
						press: this.fnHandleUserMenuItemPress
					})
				],
				afterClose: function () {
					oActionSheet.destroy();
				}
			});
			// forward compact/cozy style into dialog
			syncStyleClass(this.getView().getController().getOwnerComponent().getContentDensityClass(), this.getView(), oActionSheet);
			oActionSheet.openBy(oEvent.getSource());
		},

		onSideNavButtonPress: function () {
			var oToolPage = this.byId("app");
			var bSideExpanded = oToolPage.getSideExpanded();
			this._setToggleButtonTooltip(bSideExpanded);
			oToolPage.setSideExpanded(!oToolPage.getSideExpanded());
		},

		_setToggleButtonTooltip: function (bSideExpanded) {},

		fnBoardingMenuItemPress: function (oEvent) {
			var oSelectedKey = oEvent.getParameters().item.getKey(),
				oTitle = oEvent.getSource().getTitle();
			if (oTitle === "Configuration") {
				if (oSelectedKey === "ConfigurableParameter") {
					this.getRouter().navTo("ConfigParameter");
				} else if (oSelectedKey === "Master") {
					this.getRouter().navTo("Master", {
						contextPath: oSelectedKey
					});
				} else {
					this.getRouter().navTo("Master", {
						contextPath: oSelectedKey
					});
				}
			} else {
				this.getRouter().navTo("onboarding", {
					contextPath: oSelectedKey
				});
			}
		},

		fnMenuItemPress: function (oEvent) {
			this.getRouter().navTo("home");
		},

		onPressDowntime: function (oEvent) {
			this.getRouter().navTo("DowntimeMaintenance");
		},

		onAfterRendering: function () {
			// Create the model
			var oModel = new sap.ui.model.json.JSONModel();
			// Assign the model to the view 
			this.getView().setModel(oModel, "UserInfo");
			// Load the data
			oModel.loadData("/services/userapi/currentUser");
			// Add a completion handler to log the json and any errors
			oModel.attachRequestCompleted(function onCompleted(oEvent) {
				if (oEvent.getParameter("success")) {
					this.setData({
						"json": this.getJSON(),
						"status": "Success"
					}, true);
				} else {
					var msg = oEvent.getParameter("errorObject").textStatus;
					if (msg) {
						this.setData("status", msg);
					} else {
						this.setData("status", "Unknown error retrieving user info");
					}
				}
			});
			// End model creation and loading
			this.getUserGroupCode();
		},

		getUserGroupCode: function () {
			// 			debugger; //eslint-disable-line
			if (!this.byId("dGroupCode")) {
				var oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.ey.onboarding.Fragments.GroupCode", this);
				this.getView().addDependent(oDialog);
				oDialog.open();
			} else {
				this.byId("dGroupCode").open();
			}
		},

		onCloseDialog: function (oEvent) {
			var vGroupCode = this.getOwnerComponent().getModel("GroupCdModel").getProperty("/code");
			if (oEvent.getSource().getId().includes("bOK") && vGroupCode !== $.sap.groupCode) {
				var that = this;
				var searchInfo = {
					"req": {
						"currGroupCode": ($.sap.groupCode ? $.sap.groupCode : null),
						"newGroupCode": vGroupCode
					}
				};
				$.sap.groupCode = vGroupCode;
				$.sap.groupName = this.byId("slGroupCode").getSelectedItem().getText();
				this.byId("bGroupCode").setText(this.byId("slGroupCode").getSelectedItem().getText());
				this.getRouter().navTo("Home");
				// that.getRouter().navTo("onboarding", {
				// 	contextPath: "FileUpload"
				// });

				this.getView().setBusy(false);
				$.ajax({
					method: "POST",
					url: "/SapOnboarding/switchGroupCode.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					that.getView().setBusy(false);
					if (data.hdr.status === "S") {
						sap.m.MessageBox.success(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
						that.getELRegistration();

						// sap.ui.controller("com.ey.onboarding.controller.onboarding").getAllAPI();
					} else {
						sap.m.MessageBox.error(data.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}).fail(function (jqXHR, status, err) {
					that.getView().setBusy(false);
					sap.m.MessageBox.error("Error on switching group code", {
						styleClass: "sapUiSizeCompact"
					});
				});
			}
			oEvent.getSource().getEventingParent().close();
		},

		getELRegistration: function (oEvent) {
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
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.message === "No Record Found") {
						$.sap.entityId = null;
					} else {
						$.sap.entityId = data.resp[0].entityId;
					}
					that.getRouter().navTo("onboarding", {
						contextPath: "FileUpload"
					});

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
			});
		},
		/**
		 * Called to open User Profile dialog
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		fnAvatarPress: function (oEvent) {
			// debugger; //eslint-disable-line
			if (!this.userProfile) {
				this.userProfile = sap.ui.xmlfragment(this.getView().getId(), "com.ey.onboarding.Fragments.UserProfile", this);
				this.userProfile.setPlacement("Bottom");
				this.getView().addDependent(this.userProfile);
			}
			this.userProfile.openBy(oEvent.getSource());
		},

		/**
		 * Called when usesr pressed logout button
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		handleLogoutPress: function (oEvent) {
			// debugger; // eslint-disable-line
			this.userProfile.close(oEvent.getSource());
			sap.m.URLHelper.redirect("/do/logout", false);
		}
	});
});