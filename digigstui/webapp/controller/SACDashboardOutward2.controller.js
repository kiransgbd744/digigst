sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.SACDashboardOutward2", {
		_bExpanded: true,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.SACDashboardOutward2
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("SACDashboardOutward2").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function () {
			debugger;
			this.onLoadDinomination();
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this.onLoadFY(),
					this.onLoadGSTIN()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					this.onLoadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			var oVizFrameDonut = this.oVizFrame = this.getView().byId("id_iddonut");
			var oVizFrameLine = this.oVizFrame = this.getView().byId("vfBiDashLine");
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
			var oPopOver = this.getView().byId("idPopOver");
			oPopOver.connect(oVizFrameDonut.getVizUid());
			oVizFrameLine.setVizProperties({
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
					visible: false
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
			var oPopOver1 = this.getView().byId("idPopOver1");
			oPopOver1.connect(oVizFrameLine.getVizUid());

		},
		// onAfterRendering : function(){
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
				"Value": "Crores"
			}, {
				"Key": "M",
				"Value": "Millions"
			}, {
				"Key": "B",
				"Value": "Billions"
			}, {
				"Key": "A",
				"Value": "Absolute"
			}, ];
			this.getView().setModel(new JSONModel(aJson), "oDinomtion");
			var vIndex = this.getView().byId("idDimension").getText();
			vIndex = vIndex.split("In ")[1];
			var vValue = "K";
			switch (vIndex) {
			case 'Thousands':
				vValue = "K";
				break;
			case 'Lakhs':
				vValue = "L";
				break;
			case 'Crores':
				vValue = "C";
				break;
			case 'Millions':
				vValue = "M";
				break;
			case 'Billions':
				vValue = "B";
				break;
			case 'Absolute':
				vValue = "A";
				break;
			default:
				vValue = "K";
			}
			this.vSelectDenomKey = vValue;
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
			// this.Denomination1(value);
			this.DenominationFormateValue();
		},

		DenominationFormateValue: function () {
			this.getPsdVsErrDataValue();
			this.getCompAnalysisDataValue();
		},

		getPsdVsErrDataValue: function () {

			var oData1 = this.getView().getModel("oGrossModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.psdVsErr.length; i++) {
				oData.resp.psdVsErr[i].yAxis = this.DenominationNum(oData.resp.psdVsErr[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oGrossModel");
			this.getView().getModel("oGrossModel").refresh(true);
		},

		getCompAnalysisDataValue: function () {

			var oData1 = this.getView().getModel("oTopCustB2BModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.revenueCompAnalysis.length; i++) {
				oData.resp.revenueCompAnalysis[i].yAxis = this.DenominationNum(oData.resp.revenueCompAnalysis[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oTopCustB2BModel");
			this.getView().getModel("oTopCustB2BModel").refresh(true);
		},

		DenominationNum: function (value2) {

			var value1 = this.vSelectDenomKey;
			if (!isNaN(value2) && value2 !== null && value2 !== 0 && value2 !== "") {
				var vFlagd = "";
				if (value1 == "K") {
					value2 = (Number(value2) / 1000);
					vFlagd = value1;

				} else if (value1 == "L") {
					value2 = (Number(value2) / 100000);
					vFlagd = 'Lac';

				} else if (value1 == "C") {
					value2 = (Number(value2) / 10000000);
					vFlagd = "Cr"

				} else if (value1 == "M") {
					value2 = (Number(value2) / 1000000);
				} else if (value1 == "B") {
					value2 = (Number(value2) / 1000000000);
				}
				var s = typeof (value2) === "number" ? value2.toFixed(2) : value2;

				return s;
			}
			if (value2 === 0) {
				return value2;
			}
		},

		onLoadFY: function () {
			var that = this;
			return new Promise(function (resolve, reject) {
				var Gstr2APath = "/aspsapapi/getAllFy.do";
				$.ajax({
						method: "GET",
						url: Gstr2APath,
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							that.getView().setModel(new JSONModel(data.resp), "oFyModel");
							that.getView().getModel("oFyModel").refresh(true);
							// that.onLoadReturn(that.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
						} else {
							that.getView().setModel(new JSONModel([]), "oFyModel");
							that.getView().getModel("oFyModel").refresh(true);
						}
						resolve(data);
					})
					.fail(function (err) {
						that.getView().setModel(new JSONModel([]), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
						reject(err);
					});
			});
		},
		onLoadGSTIN: function () {
			var that = this;
			return new Promise(function (resolve, reject) {
				var PayLoad = {
					"req": {
						"entityId": [$.sap.entityID]
					}
				};
				var GstnPath = "/aspsapapi/getOut2SGstins.do";
				$.ajax({
						method: "POST",
						url: GstnPath,
						contentType: "application/json",
						data: JSON.stringify(PayLoad)
					})
					.done(function (data, status, jqXHR) {
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
							var oSelectedKeyG = that.getOwnerComponent().getModel("GloblePayload").getData();
							if (oSelectedKeyG.gstin.length === 0) {
								that.byId("idSupplier").setSelectedKeys(oSelectedKey);
							}
						} else {
							that.getView().setModel(new JSONModel([]), "oGstinModel");
							that.getView().getModel("oGstinModel").refresh(true);
						}
						resolve(data);
					})
					.fail(function (jqXHR, status, err) {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
						reject(err);
					});
			});
		},
		onLoadReturn: function (fy) {
			if (fy) {
				debugger;
				var sKey = this.getView().byId("idFinance").getSelectedKey(),
					today = new Date(),
					aYear = (sKey ? sKey.split("-") : fy.split("-")),
					cFyMonth = ('' + (today.getMonth() < 9 ? '0' : '') + (today.getMonth() + 1) + today.getFullYear()),
					aFiscalYear = {
						"returnPeriods": [{
							"fy": "All",
							"select": true
						}]
					},
					oSelectedKey = ["All"];

				for (var i = 0; i < 12; i++) {
					var fromFy = new Date(aYear[0], (3 + i), 1),
						period = '' + (fromFy.getMonth() < 9 ? '0' : '') + (fromFy.getMonth() + 1) + fromFy.getFullYear();

					oSelectedKey.push(period);
					aFiscalYear.returnPeriods.push({
						"fy": period,
						"select": true
					});
					if (period === cFyMonth) {
						break;
					}
				}
				this.getView().setModel(new JSONModel(aFiscalYear), "oRetPeriodModel");
				this.getView().setModel(new JSONModel(aFiscalYear), "oRetPeriodModel1");
				var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayload").getProperty("/period");
				this.byId("idReturn").setSelectedKeys(!oSelectedKeyG.length ? oSelectedKey : oSelectedKeyG);
				this.ChartBind();
			}
			// var vFy = this.getView().byId("idFinance").getSelectedKey();
			// if (vFy != "") {
			// 	fy = vFy
			// }
			// var that = this;
			// var PayLoad = {
			// 	"req": {
			// 		"fy": fy
			// 	}
			// };
			// var Gstr2APath = "/aspsapapi/getOut2RPrds.do";
			// sap.ui.core.BusyIndicator.show(0);
			// $(document).ready(function ($) {
			// 	$.ajax({
			// 		method: "POST",
			// 		url: Gstr2APath,
			// 		contentType: "application/json",
			// 		data: JSON.stringify(PayLoad)
			// 	}).done(function (data, status, jqXHR) {
			// 		sap.ui.core.BusyIndicator.hide();
			// 		if (data.hdr.status === "S") {
			// 			if (data.resp.returnPeriods.length != 0) {
			// 				data.resp.returnPeriods.unshift({
			// 					"fy": "All",
			// 					"select": true
			// 				});
			// 			}
			// 			var oSelectedKey = [];
			// 			for (var i = 0; i < data.resp.returnPeriods.length; i++) {
			// 				data.resp.returnPeriods[i].select = true;
			// 				oSelectedKey.push(data.resp.returnPeriods[i].fy);
			// 			};
			// 			that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel");
			// 			that.getView().getModel("oRetPeriodModel").refresh(true);
			// 			that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel1");
			// 			that.getView().getModel("oRetPeriodModel1").refresh(true);

			// 			// that.byId("idReturn").setSelectedKeys(oSelectedKey);
			// 			var oSelectedKeyG = that.getOwnerComponent().getModel("GloblePayload").getData();
			// 			if (oSelectedKeyG.period.length === 0) {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKey);
			// 			} else {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKeyG.period);
			// 			}
			// 			that.ChartBind();
			// 		} else {
			// 			that.getView().setModel(new JSONModel([]), "oRetPeriodModel");
			// 			that.getView().getModel("oRetPeriodModel").refresh(true);
			// 		}

			// 	}).fail(function (jqXHR, status, err) {});
			// 	sap.ui.core.BusyIndicator.hide();
			// 	that.getView().setModel(new JSONModel([]), "oRetPeriodModel");
			// 	that.getView().getModel("oRetPeriodModel").refresh(true);
			// });
		},
		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";

			if (aGstin.length === 0) {
				MessageBox.error("Please Select Supplier GSTIN");
				return;
			}

			if (aRetPeriod.length === 0) {
				MessageBox.error("Please Select Return Period");
				return;
			}

			switch (vIndex) {
			case 0:
				vValue = "INVOICE_VALUE";
				break;
			case 1:
				vValue = "TAXABLE_VALUE";
				break;
			case 2:
				vValue = "TAX_AMOUNT";
				break;
			default:
				vValue = "B2B_CNT";
			}
			// var value = this.getView().byId("idDimension").getSelectedKey();
			var oBject = {
				"Key": this.vSelectDenomKey
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
			var aPayload2 = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this.getOutward2HeaderData(aPayload);
			// this.getOut2TotalTurnOver(aPayload);
			this.getPsdVsErrData(aPayload);
			this.getCompAnalysisData(aPayload2);
			this.getOutSuppDetails(aPayload);
		},
		onSelectTrendValue: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "INVOICE_VALUE";
				break;
			case 1:
				vValue = "TAXABLE_VALUE";
				break;
			case 2:
				vValue = "TAX_AMOUNT";
				break;
			default:
				vValue = "B2B_CNT";
			}
			var aPayload2 = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this.getCompAnalysisData(aPayload2);
			var feedValueAxis = this.getView().byId('valueAxisFeed1');
			this.getView().byId("vfBiDashLine").removeFeed(feedValueAxis);
			if (vValue == "INVOICE_VALUE") {
				var oBject = {
					"value": ["Invoice Value"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAXABLE_VALUE") {
				var oBject = {
					"value": ["Taxable Value"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAX_AMOUNT") {
				var oBject = {
					"value": ["Tax Amount"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "B2B_CNT") {
				var oBject = {
					"value": ["No of B2B Customer"]
				};
				feedValueAxis.setValues(oBject.value);
			}
			this.getView().byId("vfBiDashLine").addFeed(feedValueAxis);
		},
		onFyChange: function (oEvnt) {
			this.getOwnerComponent().getModel("GloblePayload").setProperty("/period", []);
			this.getOwnerComponent().getModel("GloblePayload").refresh(true);
			this.onLoadReturn(oEvnt.getSource().getSelectedKey());
		},
		onGoSelect: function () {
			// this.onDinomChange();
			this.ChartBind();
		},
		getOutward2HeaderData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getOutward2HeaderData.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oHeaderDetailsModel");
						that.getView().getModel("oHeaderDetailsModel").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel");
						that.getView().getModel("oHeaderDetailsModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel");
				that.getView().getModel("oHeaderDetailsModel").refresh(true);
			});
		},
		// getOut2TotalTurnOver: function (aPayload) {
		// 	var that = this;
		// 	var Gstr2APath = "/aspsapapi/getOut2TotalTurnOver.do";
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: Gstr2APath,
		// 			contentType: "application/json",
		// 			data: JSON.stringify(aPayload)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			if (data.hdr.status === "S") {
		// 				that.getView().setModel(new JSONModel(data.resp), "oHeaderDetailsModel2");
		// 				that.getView().getModel("oHeaderDetailsModel2").refresh(true);
		// 			} else {
		// 				that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel2");
		// 				that.getView().getModel("oHeaderDetailsModel2").refresh(true);
		// 			}

		// 		}).fail(function (jqXHR, status, err) {});
		// 		sap.ui.core.BusyIndicator.hide();
		// 		that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel2");
		// 		that.getView().getModel("oHeaderDetailsModel2").refresh(true);
		// 	});
		// },
		getPsdVsErrData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPsdVsErrData.do";
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
						that.getPsdVsErrDataValue();
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
		getOutSuppDetails: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getOutSuppDetails.do";
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
		getCompAnalysisData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getCompAnalysisData.do";
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
						that.getCompAnalysisDataValue()
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
		onPressBackPage: function () {
			var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayload").getData();
			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			this.getOwnerComponent().getModel("GloblePayload").refresh(true);
			this.getRouter().navTo("SACDashboardOutward");
		},
		onPressNextPage: function () {
			this.getRouter().navTo("SACDashboardOutward3");
		},
		onDownloadGross: function () {
			var oVizFrame = this.getView().byId("id_iddonut");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Processed Vs Error Records"
				}
			});
			var filename = "Processed Vs Error Records";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Processed Vs Error Records"
				}
			});

		},
		onDownloadMonth: function () {
			var oVizFrame = this.getView().byId("vfBiDashLine");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Revenue Comparative Analysis"
				}
			});
			var filename = "Revenue Comparative Analysis";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Revenue Comparative Analysis"
				}
			});
		},
		onSavePDF: function (val1, val2) {
			//Step 1: Export chart content to svg
			var oVizFrame = val1;
			var sSVG = oVizFrame.exportToSVGString({
				width: 800,
				height: 600
			});

			// UI5 library bug fix:
			//    Legend SVG created by UI5 library has transform attribute with extra space
			//    eg:   transform="translate (-5,0)" but it should be without spaces in string quotes
			//    tobe: transform="translate(-5,0)
			sSVG = sSVG.replace(/translate /gm, "translate");

			//Step 2: Create Canvas html Element to add SVG content
			var oCanvasHTML = document.createElement("canvas");
			canvg(oCanvasHTML, sSVG); // add SVG content to Canvas

			// STEP 3: Get dataURL for content in Canvas as PNG/JPEG
			var sImageData = oCanvasHTML.toDataURL("image/png");

			// STEP 4: Create PDF using library jsPDF
			var oPDF = new jsPDF();
			oPDF.addImage(sImageData, "PNG", 15, 40, 180, 160);
			oPDF.save(val2);
		}
	});

});