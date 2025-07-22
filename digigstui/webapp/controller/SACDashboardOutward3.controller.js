sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.SACDashboardOutward3", {
		_bExpanded: true,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.SACDashboardOutward3
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("SACDashboardOutward3").attachPatternMatched(this._onRouteMatched, this);
		},
		_onRouteMatched: function () {
			this.onLoadDinomination();
			this.onLoadFY();
			this.onLoadGSTIN();
			var oVizFrameDonut = this.oVizFrame = this.getView().byId("id_iddonut");
			var oVizFramePie = this.oVizFrame = this.getView().byId("id_Pie");
			var oVizFramecolumn = this.oVizFrame = this.getView().byId("idVizFrameColumn");
			oVizFrameDonut.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					drawingEffect: "glossy"
				},
				title: {
					visible: false
				},
				legend: {
					visible: true
				},
				valueAxis: {
					title: {
						visible: true
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			oVizFramePie.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					drawingEffect: "glossy"
				},
				title: {
					visible: false
				},
				legend: {
					visible: true
				},
				valueAxis: {
					title: {
						visible: true
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			var oPopOver = this.getView().byId("idPopOver");
			oPopOver.connect(oVizFrameDonut.getVizUid());
			var oPopOver1 = this.getView().byId("idPopOver1");
			oPopOver1.connect(oVizFrameDonut.getVizUid());
			oVizFramecolumn.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					}
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: true
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}
				}
			});
			var oPopOver2 = this.getView().byId("idPopOver2");
			oPopOver2.connect(oVizFramecolumn.getVizUid());
		},
		// onAfterRendering: function () {
		// 	this.onGoSelect();
		// },
		onLoadDinomination: function () {
			var aJson = [{
				"Key": "K",
				"Value": "Thousands"
			}, {
				"Key": "L",
				"Value": "Lakhs"
			}, {
				"Key": "C",
				"Value": "Crore"
			}, {
				"Key": "N",
				"Value": "Rupees in Absolute"
			}, ];
			this.getView().setModel(new JSONModel(aJson), "oDinomtion");
			this.vSelectDenomKey = "K"

		},
		onDinomChange: function (oevnt) {
			this.vSelectDenomKey = oevnt.getParameter("item").getKey();
			var oBject = {
				"Key": this.vSelectDenomKey
			};
			this.getView().setModel(new JSONModel(oBject), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);
			var oBundle = this.getResourceBundle();
			this.byId("idDimension").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(this.vSelectDenomKey));

			// var value = this.getView().byId("idDimension").getSelectedKey();
			// var oBject = {
			// 	"Key": value
			// };
			// this.getView().setModel(new JSONModel(oBject), "oDmModel");
			// this.getView().getModel("oDmModel").refresh(true);

			this.DenominationFormateValue();
		},

		DenominationFormateValue: function () {

			this.getGstNetLiabDataValue();
			this.getTopCustB2BValue();
			this.getUtilSummDataValue();
		},

		getGstNetLiabDataValue: function () {
			debugger;
			var oData1 = this.getView().getModel("oGstNetLiabModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.gstNetLiab.length; i++) {
				oData.resp.gstNetLiab[i].yAxis = this.DenominationNum(oData.resp.gstNetLiab[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oGstNetLiabModel");
			this.getView().getModel("oGstNetLiabModel").refresh(true);
		},
		getTopCustB2BValue: function () {
			debugger;
			var oData1 = this.getView().getModel("oTopCustB2BModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.topCustomers.length; i++) {
				oData.resp.topCustomers[i].yAxis = this.DenominationNum(oData.resp.topCustomers[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oTopCustB2BModel");
			this.getView().getModel("oTopCustB2BModel").refresh(true);
		},
		getUtilSummDataValue: function () {
			debugger;
			var oData1 = this.getView().getModel("oGrossModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.utilSummData.length; i++) {
				oData.resp.utilSummData[i].yAxis = this.DenominationNum(oData.resp.utilSummData[i].yAxis)
			}
			this.getView().setModel(new JSONModel(oData.resp), "oGrossModel");
			this.getView().getModel("oGrossModel").refresh(true);
		},

		DenominationNum: function (value2) {

			var value1 = this.vSelectDenomKey;
			if (!isNaN(value2) && value2 !== null && value2 !== 0 && value2 !== "") {
				var vFlagd = "";
				if (value1 == "K") {
					// if(value2 >= 1000) {
					value2 = (Number(value2) / 1000);
					vFlagd = value1;
					// }

				} else if (value1 == "L") {
					// if (value2 >= 100000) {
					value2 = (Number(value2) / 100000);
					vFlagd = 'Lac';
					//}
				} else if (value1 == "C") {
					// if (value2 >= 10000000) {
					value2 = (Number(value2) / 10000000);
					vFlagd = "Cr"
						//}
				} else if (value1 == "M") {
					value2 = (Number(value2) / 1000000);
				} else if (value1 == "B") {
					value2 = (Number(value2) / 1000000000);
				}
				// var flag = false,
				var s = typeof (value2) === "number" ? value2.toFixed(2) : value2;

				return s;
			}
			if (value2 === 0) {
				// var res = Number(value2).toFixed(2);
				return value2;
			}
			// return value2;
		},

		onLoadFY: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getAllFy.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json",
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
						if (that.vFyear) {
							that.onLoadReturn(that.vFyear);
						} else {
							that.vFyear = that.getView().getModel("oFyModel").getData().finYears[0].fy;
							that.onLoadReturn(that.getView().getModel("oFyModel").getData().finYears[0].fy);
						}

					} else {
						that.getView().setModel(new JSONModel([]), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oFyModel");
				that.getView().getModel("oFyModel").refresh(true);
			});
		},
		onLoadGSTIN: function () {
			var that = this;
			var PayLoad = {
				"req": {
					"entityId": parseInt($.sap.entityID)
				}
			};
			var GstnPath = "/aspsapapi/getOut3SGstins.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnPath,
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.gstins.length != 0) {
							data.resp.gstins.unshift({
								"gstin": "All"
							});
						}
						that.getView().setModel(new JSONModel(data.resp), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
						var oSelectedKey = [];
						for (var i = 0; i < data.resp.gstins.length; i++) {
							oSelectedKey.push(data.resp.gstins[i].gstin);
						};
						// that.byId("idSupplier").setSelectedKeys(oSelectedKey);
					} else {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oGstinModel");
				that.getView().getModel("oGstinModel").refresh(true);
			});
		},
		onLoadReturn: function (fy) {
			var that = this;
			var PayLoad = {
				"req": {
					"fy": fy
				}
			};
			var Gstr2APath = "/aspsapapi/getOut3RPrds.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.returnPeriods.length != 0) {
							data.resp.returnPeriods.unshift({
								"fy": "All",
								"select": true
							});
						}
						var oSelectedKey = [];
						for (var i = 0; i < data.resp.returnPeriods.length; i++) {
							data.resp.returnPeriods[i].select = true;
							oSelectedKey.push(data.resp.returnPeriods[i].fy);
						};
						that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel");
						that.getView().getModel("oRetPeriodModel").refresh(true);
						that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel1");
						that.getView().getModel("oRetPeriodModel1").refresh(true);
						// that.byId("idReturn").setSelectedKeys(oSelectedKey);
						that.ChartBind();
					} else {
						that.getView().setModel(new JSONModel([]), "oRetPeriodModel");
						that.getView().getModel("oRetPeriodModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oRetPeriodModel");
				that.getView().getModel("oRetPeriodModel").refresh(true);
			});
		},
		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			var value = this.vSelectDenomKey;

			if (aGstin.length === 0) {
				MessageBox.error("Please Select Suppler Gstin");
				return;
			}

			if (aRetPeriod.length === 0) {
				MessageBox.error("Please Select Return Period");
				return;
			}
			var oBject = {
				"Key": value
			};
			this.getView().setModel(new JSONModel(oBject), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);
			if (aGstin.length != 0) {
				if (aGstin[0].value == "All") {
					aGstin.shift();
				}
			}
			if (aRetPeriod.length != 0) {
				if (aRetPeriod[0].value == "All") {
					aRetPeriod.shift();
				}
			}
			var aPayload = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod
				}
			};
			this.getOutward3HeaderData(aPayload);
			this.getUtilSummData(aPayload);
			this.getGstNetLiabData(aPayload);
			this.getTopCustB2B(aPayload);
			this.getLiabTableData(aPayload);
		},
		onFyChange: function (oEvnt) {
			var that = this;
			var key = oEvnt.getSource().getSelectedKey();
			that.vFyear = key;
			this.onLoadReturn(key);
		},
		onGoSelect: function () {
			// this.onDinomChange();
			this.ChartBind();
		},

		onPressBackPage: function () {
			this.getRouter().navTo("SACDashboardOutward2");
		},
		onPressNextPage: function () {
			this.getRouter().navTo("SACDashboardOutward");
		},
		getOutward3HeaderData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getOutward3HeaderData.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oHeaderModel");
						that.getView().getModel("oHeaderModel").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oHeaderModel");
						that.getView().getModel("oHeaderModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oHeaderModel");
				that.getView().getModel("oHeaderModel").refresh(true);
			});
		},
		getUtilSummData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getUtilSummData.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oData = $.extend(true, {}, data);
						that.getView().setModel(new JSONModel(oData), "oGrossModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oGrossModel");
						that.getView().getModel("oGrossModel").refresh(true);
						that.getUtilSummDataValue()
					} else {
						that.getView().setModel(new JSONModel([]), "oGrossModel");
						that.getView().getModel("oGrossModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oGrossModel");
				that.getView().getModel("oGrossModel").refresh(true);
			});
		},
		getGstNetLiabData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getGstNetLiabData.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oData = $.extend(true, {}, data);
						that.getView().setModel(new JSONModel(oData), "oGstNetLiabModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oGstNetLiabModel");
						that.getView().getModel("oGstNetLiabModel").refresh(true);
						that.getGstNetLiabDataValue();
					} else {
						that.getView().setModel(new JSONModel([]), "oGstNetLiabModel");
						that.getView().getModel("oGstNetLiabModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oGrossModel");
				that.getView().getModel("oGrossModel").refresh(true);
			});
		},
		getLiabTableData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getLiabTableData.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oTaxLiabDetailsModel");
						that.getView().getModel("oTaxLiabDetailsModel").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oTaxLiabDetailsModel");
						that.getView().getModel("oTaxLiabDetailsModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oTaxLiabDetailsModel");
				that.getView().getModel("oTaxLiabDetailsModel").refresh(true);
			});
		},
		getTopCustB2B: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getTopCustB2B.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oData = $.extend(true, {}, data);
						that.getView().setModel(new JSONModel(oData), "oTopCustB2BModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oTopCustB2BModel");
						that.getView().getModel("oTopCustB2BModel").refresh(true);
						that.getTopCustB2BValue();
					} else {
						that.getView().setModel(new JSONModel([]), "oTopCustB2BModel");
						that.getView().getModel("oTopCustB2BModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oTopCustB2BModel");
				that.getView().getModel("oTopCustB2BModel").refresh(true);
			});
		},
	});

});