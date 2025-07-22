sap.ui.define([], function () {
	"use strict";

	return {
		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oData Data App Permission Object
		 */
		_setPermionFalse: function (oData) {
			var aField = [
				"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P11", "P12", "P13", "P101", "P102", "P103", "P99", "P22", "P23",
				"P24", "P25", "P27", "P28", "P29", "P30", "P31", "P32", "P33", "P34", "P35", "P36", "P37", "P38", "P39", "P40", "P41", "P42"
			];
			aField.forEach(function (e) {
				oData.appPermission[e] = ["P101", "P102"].includes(e);
			});
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oData Data App Permission Object
		 */
		_setPermionTrueAll: function (oData) {
			var aField = ["P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P101", "P102", "P103", "P99", "P41"];
			aField.forEach(function (e) {
				oData.appPermission[e] = true;
			});
		},

		/**
		 * Developed by: Ram Sundar Mahato
		 * @memberOf com.ey.digigst.view.App
		 * @private
		 * @param {Object} oData Data App Permission Object
		 */
		_setPermionTrue: function (oData) { //eslint-disable-line
			oData.respData.appPermission.permission.forEach(function (e) {
				if (e === "P1") {
					this._setPermionTrueAll(oData);
				}
				oData.appPermission[e] = true;
			}.bind(this));
		},
	};
});