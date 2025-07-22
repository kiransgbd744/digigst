sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"com/ey/digigst/util/Formatter"
], function (MobileLibrary, MessageBox, MessageToast, JSONModel, Fragment, Formatter) {
	"use strict";

	return {

		/*===================================================================*/
		/*========= Press Toggle Open State =================================*/
		/*===================================================================*/
		_toggleOpenState: function (oEvent) {
			if (oEvent.getParameters().expanded) {
				var aData = oEvent.getSource().getBinding().getModel("Summary").getData();
				var vPath = oEvent.getSource().getBinding().getPath();
				var vLength = oEvent.getSource().getVisibleRowCount() + aData[(vPath.split("/")[1])][0].items.length;
				if (vLength < 10) {
					oEvent.getSource().setVisibleRowCount(vLength);
				} else {
					oEvent.getSource().setVisibleRowCount(10);
				}
				oEvent.getSource().setProperty("expandFirstLevel", true);
			} else {
				// var vKey = oEvent.getSource().getEventingParent().getHeaderToolbar().getContent()[2].getSelectedKey();
				// if (vKey === "") {
				oEvent.getSource().setVisibleRowCount(3);
				// } else {
				// 	oEvent.getSource().setVisibleRowCount(1);
				// }
				oEvent.getSource().setProperty("expandFirstLevel", false);
			}
		},
		onActivateAuthToken1: function (that,gstin, authToken) {
			debugger; //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},
	};
});