sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/Device",
	"com/ey/digigst/model/models",
	"sap/ui/model/json/JSONModel"
], function (UIComponent, Device, models, JSONModel) {
	"use strict";

	var vIdleTimeout = 600, // seconds
		_idleSecondsTimer = null;
	// $.sap.vIdelTimeCounter = 0;
	$.sap.oSessionTimer = null;
	$.sap.oTime = null;

	return UIComponent.extend("com.ey.digigst.Component", {

		metadata: {
			manifest: "json"
		},

		/**
		 * The component is initialized by UI5 automatically during the startup of the app and calls the init method once.
		 * @public
		 * @override
		 */
		init: function () {
			var that = this;
			// call the base component's init function
			UIComponent.prototype.init.apply(this, arguments);

			// enable routing
			this.getRouter().initialize();

			// set the device model
			this.setModel(models.createDeviceModel(), "device");

			// jQuery.sap.registerModulePath("common", "../zcommon");
			var oDialogController = sap.ui.controller("com.ey.digigst.controller.App");
			// this._approveDialog = sap.ui.xmlfragment("common.frag.Approve", oDialogController);

			$.sap.oSessionTimer = sap.ui.xmlfragment("com.ey.digigst.fragments.SessionTimer", oDialogController);
			$.sap.oSessionTimer.setModel(new JSONModel({
				"time": null
			}), "Timer");

			// jQuery.sap.delayedCall(500, this, function () {
			// 	this.byId("idContinueSession").focus();
			// });

			// UIComponent.addDependent(this.oSessionTimer);

			document.onclick = function (event) {
				// $.sap.vIdelTimeCounter = 0;
				$.sap.oTime = null;
				if ($.sap.oSessionTimer.isOpen()) {
					$.sap.oSessionTimer.close();
					that._resumeSession();
				}
			};

			document.onkeypress = function () {
				// $.sap.vIdelTimeCounter = 0;
				$.sap.oTime = null;
				if ($.sap.oSessionTimer.isOpen()) {
					$.sap.oSessionTimer.close();
					that._resumeSession();
				}
			};

			_idleSecondsTimer = window.setInterval(this.checkIdleTime, 1000);
		},

		/**
		 * Method called to check idle time of user & called api to timeout session in server
		 * Developed by: Bharat Gupta on 12.02.2020
		 * @private
		 */
		checkIdleTime: function () {
			if (!$.sap.oTime) {
				$.sap.oTime = new Date().getTime();
			}
			// $.sap.vIdelTimeCounter++;
			var vTime = Math.round((new Date().getTime() - $.sap.oTime) / 1000);
			// if (vIdleTimeout - $.sap.vIdelTimeCounter <= 120) {
			if (vIdleTimeout - vTime <= 120 && vTime <= vIdleTimeout) {
				if (!$.sap.oSessionTimer.isOpen()) {
					$.sap.oSessionTimer.open();
				}
				var oModel = $.sap.oSessionTimer.getModel("Timer");
				// oModel.getData().time = (vIdleTimeout - $.sap.vIdelTimeCounter);
				oModel.getData().time = (vIdleTimeout - vTime);
				oModel.refresh();
			}
			if (vTime >= vIdleTimeout) { // $.sap.vIdelTimeCounter >= vIdleTimeout || 
				window.clearInterval(_idleSecondsTimer);
				sap.m.URLHelper.redirect("logout.html", false);
				// sap.m.URLHelper.redirect("logout.html?session=true", false);
				// var vlog = window.location.href.replace('index.html?', 'logout.html?');
				// sap.m.URLHelper.redirect(vlog, false);
			}
		},

		/**
		 * Method called to Resume server session
		 * Developed by: Bharat Gupta on 12.02.2020
		 * @private
		 */
		_resumeSession: function () {
			$.ajax({
				method: "GET",
				url: "/aspsapapi/sessionResume.do",
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {}).fail(function (jqXHR, status, err) {});
		}
	});
});