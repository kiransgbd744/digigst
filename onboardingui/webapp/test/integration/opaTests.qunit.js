/* global QUnit */
QUnit.config.autostart = false;

sap.ui.getCore().attachInit(function () {
	"use strict";

	sap.ui.require([
		"com/ey/onboarding/test/integration/AllJourneys"
	], function () {
		QUnit.start();
	});
});