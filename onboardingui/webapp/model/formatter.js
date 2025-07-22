sap.ui.define([], function() {
	"use strict";

	return {
		/**
		 * @public
		 * @param {boolean} bIsPhone the value to be checked
		 * @returns {string} path to image
		 */
		srcImageValue: function(bIsPhone) {
			var sImageSrc = "";
			if (bIsPhone === false) {
				sImageSrc = "./images/homeImage.jpg";
			} else {
				sImageSrc = "./images/homeImage_small.jpg";
			}
			return sImageSrc;
		},

		formatCurrency: function(sCurrCode) {
			var sBrowserLocale = sap.ui.getCore().getConfiguration().getLanguage();
			var oLocale = new sap.ui.core.Locale(sBrowserLocale);
			var oLocaleData = new sap.ui.core.LocaleData(oLocale);
			return oLocaleData.getCurrencySymbol(sCurrCode);
		},

		amount: function(vValue) {
			if (!isNaN(vValue)) {
				return Number(parseFloat(vValue)).toFixed(2);
			}
			return vValue;
		}
	};
});