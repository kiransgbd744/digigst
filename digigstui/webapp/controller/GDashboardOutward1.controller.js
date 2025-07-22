sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/util/Export",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/m/MessageBox"
], function (BaseController, UserPermission, Storage, JSONModel, Export, ExportTypeCSV, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GDashboardOutward1", {
		_bExpanded: true,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GDashboardOutward1
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("GDashboardOutward1").attachPatternMatched(this._onRouteMatched, this);

			var oVizFrameDonut = this.oVizFrame = this.getView().byId("id_iddonut");
			var oVizFrameLine = this.oVizFrame = this.getView().byId("vfBiDashLine");
			var oVizFrameBar = this.oVizFrame = this.getView().byId("idVizFrameBar");
			var oVizFrameBar1 = this.oVizFrame = this.getView().byId("idVizFrameBar1");
			var oVizFrameDonut1 = this.oVizFrame = this.getView().byId("id_iddonut2");
			var oVizFrameStackedBar = this.oVizFrame = this.getView().byId("idVizFrame_stacked_bar");
			oVizFrameDonut.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					drawingEffect: "glossy"
				},
				title: {
					visible: false,
					text: "Gross Outward Suppliers"
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
			oVizFrameBar.setVizProperties({
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
						visible: true
					}
				}
			});
			var oPopOver2 = this.getView().byId("idPopOver2");
			oPopOver2.connect(oVizFrameBar.getVizUid());
			oVizFrameBar1.setVizProperties({
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
						visible: true
					}
				}
			});
			var oPopOver3 = this.getView().byId("idPopOver3");
			oPopOver3.connect(oVizFrameBar.getVizUid());
			oVizFrameDonut1.setVizProperties({
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
			var oPopOver4 = this.getView().byId("idPopOver4");
			oPopOver4.connect(oVizFrameDonut1.getVizUid());

			oVizFrameStackedBar.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					}
				},
				title: {
					visible: false
				},
				legend: {
					visible: true
				},
				valueAxis: {
					title: {
						visible: false
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			var oPopOverStackedBar = this.getView().byId("idPopOver_stacked_bar");
			oPopOverStackedBar.connect(oVizFrameStackedBar.getVizUid());
			this.firstCall = false;

		},
		_onRouteMatched: function (oEvent) {

			this.globalCall()
			this.onLoadDinomination();
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this.onLoadFY(),
					this.onLoadGSTIN()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					this.onLoadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
					this.firstCall = true
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		selectEntity: function (oEvent) {
			this.selectAll(oEvent);
			this.onLoadGSTIN()
		},

		globalCall: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				var entity = this.getOwnerComponent().getModel("entityAll").getData();
				var oData = {
					"entity": [entity[1].entityId],
					"fy": "",
					"gstin": [],
					"period": []
				}
				this.getOwnerComponent().setModel(new JSONModel(oData), "GloblePayload");
			}
		},

		onPressNextPage: function () {
			var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayload").getData();
			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			this.getOwnerComponent().getModel("GloblePayload").refresh(true);

			this.getRouter().navTo("GDashboardOutward2");
		},

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
			this.DenominationFormateValue();
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

			var that = this,
				aEntity = that.getOwnerComponent().getModel("GloblePayload").getData().entity,
				aEntity = this.removeAll(aEntity);
			this.byId("idSupplier").setSelectedKeys(null);

			return new Promise(function (resolve, reject) {
				var PayLoad = {
					"req": {
						"entityId": aEntity
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
						if (this.firstCall) {
							that.ChartBind();
						}
						resolve(data);
					})
					.fail(function (err) {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
						reject(err);
					});
			}.bind(this));
		},
		onLoadReturn: function (fy) {
			if (fy) {
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
			// var Gstr2APath = "/aspsapapi/getAllRtrnPrds.do";
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

			// 			that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel");
			// 			that.getView().getModel("oRetPeriodModel").refresh(true);
			// 			that.getView().setModel(new JSONModel(data.resp), "oRetPeriodModel1");
			// 			that.getView().getModel("oRetPeriodModel1").refresh(true);
			// 			// if (!that.globleFlag) {
			// 			var oSelectedKey = [];
			// 			for (var i = 0; i < data.resp.returnPeriods.length; i++) {
			// 				data.resp.returnPeriods[i].select = true;
			// 				oSelectedKey.push(data.resp.returnPeriods[i].fy);
			// 			};
			// 			// that.byId("idReturn").setSelectedKeys(oSelectedKey);
			// 			
			// 			var oSelectedKeyG = that.getOwnerComponent().getModel("GloblePayload").getData();
			// 			if (oSelectedKeyG.period.length === 0) {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKey);
			// 			} else {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKeyG.period);
			// 			}
			// 			// }
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

		DenominationFormateValue: function () {
			// this.getLiabDetails(aPayload);
			this.GrossOutBindValue();
			this.getTopCustB2BValue();
			this.getMonthwiseTrendValue();
			this.getMajorTaxPayingValue();
			this.getMajorTaxPayingRateValue();
			this.getTaxRatewiseValue();
			// this.getTaxLiabilityDetails(aPayload);
		},
		GrossOutBindValue: function () {

			var oData1 = this.getView().getModel("oGrossModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.grossOutward.length; i++) {
				oData.resp.grossOutward[i].yAxis = this.DenominationNum(oData.resp.grossOutward[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oGrossModel");
			this.getView().getModel("oGrossModel").refresh(true);
		},

		getTopCustB2BValue: function () {

			var oData1 = this.getView().getModel("oTopCustB2BModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.topCustomers.length; i++) {
				oData.resp.topCustomers[i].yAxis = this.DenominationNum(oData.resp.topCustomers[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oTopCustB2BModel");
			this.getView().getModel("oTopCustB2BModel").refresh(true);
		},

		getMonthwiseTrendValue: function () {

			var oData1 = this.getView().getModel("oMonthTrendModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.monthWiseAnalysis.length; i++) {
				oData.resp.monthWiseAnalysis[i].yAxis = this.DenominationNum(oData.resp.monthWiseAnalysis[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oMonthTrendModel");
			this.getView().getModel("oMonthTrendModel").refresh(true);
		},

		getMajorTaxPayingValue: function () {

			var oData1 = this.getView().getModel("oMajorTaxModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.majTaxPayingProds.length; i++) {
				oData.resp.majTaxPayingProds[i].yAxis = this.DenominationNum(oData.resp.majTaxPayingProds[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oMajorTaxModel");
			this.getView().getModel("oMajorTaxModel").refresh(true);
		},

		getMajorTaxPayingRateValue: function () {

			var oData1 = this.getView().getModel("oMajorTaxRateMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.majorTaxPayProducts.length; i++) {
				oData.resp.majorTaxPayProducts[i].per0 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per0)
				oData.resp.majorTaxPayProducts[i].per1 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per1)
				oData.resp.majorTaxPayProducts[i].per1_5 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per1_5)
				oData.resp.majorTaxPayProducts[i].per3 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per3)
				oData.resp.majorTaxPayProducts[i].per5 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per5)
				oData.resp.majorTaxPayProducts[i].per7_5 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per7_5)
				oData.resp.majorTaxPayProducts[i].per12 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per12)
				oData.resp.majorTaxPayProducts[i].per18 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per18)
				oData.resp.majorTaxPayProducts[i].per28 = this.DenominationNum(oData.resp.majorTaxPayProducts[i].per28)
			}

			this.getView().setModel(new JSONModel(oData.resp), "oMajorTaxRate");
			this.getView().getModel("oMajorTaxRate").refresh(true);
		},

		getTaxRatewiseValue: function () {

			var oData1 = this.getView().getModel("otaxwiseDistModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.taxRateWise.length; i++) {
				oData.resp.taxRateWise[i].invoiceValue = this.DenominationNum(oData.resp.taxRateWise[i].invoiceValue)
			}

			this.getView().setModel(new JSONModel(oData.resp), "otaxwiseDistModel");
			this.getView().getModel("otaxwiseDistModel").refresh(true);
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

				// var value2 = Math.round(value2)

				// if (s[0] === "-") {
				// 	flag = true;
				// 	s = s.substr(1);
				// }
				// var y = s.split(".")[0],
				// 	dec = !s.split(".")[1] ? "00" : s.split(".")[1],
				// 	lastThree = y.substring(y.length - 3),
				// 	otherNumbers = y.substring(0, y.length - 3);

				// if (otherNumbers !== "") {
				// 	lastThree = "," + lastThree;
				// }
				// var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;

				// return res;

				return s;
			}
			if (value2 === 0) {
				// var res = Number(value2).toFixed(2);
				return value2;
			}
			// return value2;
		},

		onFyChange: function (oEvnt) {
			this.getOwnerComponent().getModel("GloblePayload").setProperty("/period", []);
			this.getOwnerComponent().getModel("GloblePayload").refresh(true);
			this.onLoadReturn(oEvnt.getSource().getSelectedKey());
		},
		onSupplierChange: function () {
			// this.ChartBind();
		},
		onSelectTrendValue: function (oevent) {
			var that = this;
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
			default:
				vValue = "TAX_AMOUNT";
			}
			var feedValueAxis = this.getView().byId('valueAxisFeed1');
			this.getView().byId("vfBiDashLine").removeFeed(feedValueAxis);
			if (vValue == "INVOICE_VALUE") {
				var oBject = {
					"value": ["Invoice Values"]
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
			}
			this.getView().byId("vfBiDashLine").addFeed(feedValueAxis);

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
			var aPayload2 = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			that.getMonthwiseTrend(aPayload2);
			var value = this.getView().byId("idDimension").getSelectedKey();
			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "Invoice Value";
				break;
			case 1:
				vValue = "Taxable Value";
				break;
			default:
				vValue = "Tax Amount";
			}
			var oBject = {
				"Key": value,
				"yAxis": vValue
			};
			this.getView().setModel(new JSONModel(oBject), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);
			// that.ChartBind();
			// var vIndex = oevent.getSource().getSelectedIndex();
			// var vValue = "";
			// switch(vIndex){
			// 	case 0 : vValue = "INVOICE_VALUE";
			// 	break;
			// 	case 1 : vValue =  "TAXABLE_VALUE";
			// 	break;
			// 	default : vValue = "TAX_AMOUNT";
			// }
			// var vFy 		= this.getView().byId("idFinance").getSelectedKey();
			// var aGstin  	= this.getView().byId("idSupplier").getSelectedKeys();
			// var aRetPeriod = [];
			// for(var i=0; i<that.getView().getModel("oRetPeriodModel1").getData().returnPeriods.length; i++){
			// 	if(that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].select){
			// 		aRetPeriod.push(that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].fy);	
			// 	}
			// }

			// if(aGstin.length != 0){
			// 	if(aGstin[0].value == "All"){
			// 		aGstin.shift();
			// 	}
			// }
			// if(aRetPeriod.length != 0){
			// 	if(aRetPeriod[0] == "All"){
			// 		aRetPeriod.shift();
			// 	}
			// }
			// var aPayload2 = {
			// 	"req" : {
			// 		"fy"				: vFy,
			// 		"supplierGstins"	: aGstin,
			// 		"returnPeriods"		: aRetPeriod,
			// 		"valueFlag"			: vValue
			// 	}
			// };
			// this.getMonthwiseTrend(aPayload2);
		},
		onRetPeriodChange: function (evt) {
			var that = this;
			var vlength = that.getView().getModel("oRetPeriodModel").getData().returnPeriods.length;
			var vkeys = evt.getSource().getSelectedKeys();
			if (vkeys.includes("All")) {
				var oSelectedKey = [];
				for (var i = 0; i < that.getView().getModel("oRetPeriodModel").getData().returnPeriods.length; i++) {
					oSelectedKey.push(that.getView().getModel("oRetPeriodModel").getData().returnPeriods[i].fy);
				};
				that.byId("idReturn").setSelectedKeys(oSelectedKey);
			} else {
				var vLength1 = vlength - 1;
				if (vLength1 == vkeys.length) {
					that.byId("idReturn").setSelectedKeys([]);
				}
			}
			// this.ChartBind();
		},
		onGoSelect: function () {
			// this.onDinomChange();
			this.ChartBind();
		},
		MonthCheckSelect: function (evt) {
			var that = this;
			var aRetPeriod = [];
			for (var i = 0; i < that.getView().getModel("oRetPeriodModel1").getData().returnPeriods.length; i++) {
				if (that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].select) {
					aRetPeriod.push(that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].fy);
				}
			}

			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "INVOICE_VALUE";
				break;
			case 1:
				vValue = "TAXABLE_VALUE";
				break;
			default:
				vValue = "TAX_AMOUNT";
			}
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			if (aGstin.length != 0) {
				if (aGstin[0].value == "All") {
					aGstin.shift();
				}
			}
			if (aRetPeriod.length != 0) {
				if (aRetPeriod[0] == "All") {
					aRetPeriod.shift();
				}
			}
			var aPayload2 = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this.getMonthwiseTrend(aPayload2);
		},
		MonthCheckSelectAll: function (evt) {
			var that = this;
			var vSelected = evt.getSource().getSelected();
			if (vSelected) {
				for (var i = 0; i < that.getView().getModel("oRetPeriodModel1").getData().returnPeriods.length; i++) {
					that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].select = true;
				}
			} else {
				for (var i = 0; i < that.getView().getModel("oRetPeriodModel1").getData().returnPeriods.length; i++) {
					that.getView().getModel("oRetPeriodModel1").getData().returnPeriods[i].select = false;
				}
			}
			that.getView().getModel("oRetPeriodModel1").refresh(true);
			that.MonthCheckSelect();
		},
		onGstinChange: function (evt) {
			var that = this;
			var vlength = that.getView().getModel("oGstinModel").getData().gstins.length;
			var vkeys = evt.getSource().getSelectedKeys();
			if (vkeys.includes("All")) {
				var oSelectedKey = [];
				for (var i = 0; i < that.getView().getModel("oGstinModel").getData().gstins.length; i++) {
					oSelectedKey.push(that.getView().getModel("oGstinModel").getData().gstins[i].gstin);
				};
				that.byId("idSupplier").setSelectedKeys(oSelectedKey);
			} else {
				var vLength1 = vlength - 1;
				if (vLength1 == vkeys.length) {
					that.byId("idSupplier").setSelectedKeys([]);
				}
			}
			// this.ChartBind();
		},
		onSwitchout: function () {
			this.ChartBind();
			this.GrossOutBind();
		},
		ChartBind: function () {

			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			if (aGstin.length === 0) {
				MessageBox.error("Please Select Supplier GSTIN");
				return;
			}

			if (aRetPeriod.length === 0) {
				MessageBox.error("Please Select Return Period");
				return;
			}

			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "INVOICE_VALUE";
				break;
			case 1:
				vValue = "TAXABLE_VALUE";
				break;
			default:
				vValue = "TAX_AMOUNT";
			}
			// var value = this.getView().byId("idDimensionMenu").getSelectedKey();
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
			var flag;
			var flagState = this.byId("switchoutId").getState();
			if (flagState === true) {
				flag = "on";
			} else if (flagState === false) {
				flag = "off";
			}
			var aPayload1 = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"flag": flag
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

			this.getLiabDetails(aPayload);
			this.GrossOutBind(aPayload1);
			this.getTopCustB2B(aPayload);
			this.getTopCustB2BHSN(aPayload);
			// this.MonthCheckSelect();
			this.getMonthwiseTrend(aPayload2);
			this.getMajorTaxPaying(aPayload);
			this.getMajorTaxPayingRate(aPayload);
			this.getTaxRatewise(aPayload);
			this.getTaxLiabilityDetails(aPayload);

		},
		getLiabDetails: function (aPayload) {

			var that = this;
			var Gstr2APath = "/aspsapapi/getTotalLiabDetails.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oTotalLiabDetailsModel");
						that.getView().getModel("oTotalLiabDetailsModel").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oTotalLiabDetailsModel");
						that.getView().getModel("oTotalLiabDetailsModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oTotalLiabDetailsModel");
				that.getView().getModel("oTotalLiabDetailsModel").refresh(true);
			});
		},
		GrossOutBind: function (aPayload1) {

			var that = this;
			var Gstr2APath = "/aspsapapi/getGrossOutSup.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload1)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oData = $.extend(true, {}, data);
						that.getView().setModel(new JSONModel(oData), "oGrossModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oGrossModel");
						that.getView().getModel("oGrossModel").refresh(true);
						that.GrossOutBindValue();
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

		getTopCustB2BHSN: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getTaxLiabIoclDetails.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oTopCustB2BHSN");
						that.getView().getModel("oTopCustB2BHSN").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oTopCustB2BHSN");
						that.getView().getModel("oTopCustB2BHSN").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oTopCustB2BHSN");
				that.getView().getModel("oTopCustB2BHSN").refresh(true);
			});
		},

		getMonthwiseTrend: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getMonthWiseTrend.do";
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
						that.getView().setModel(new JSONModel(oData), "oMonthTrendModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oMonthTrendModel");
						that.getView().getModel("oMonthTrendModel").refresh(true);
						that.getMonthwiseTrendValue();
					} else {
						that.getView().setModel(new JSONModel([]), "oMonthTrendModel");
						that.getView().getModel("oMonthTrendModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oMonthTrendModel");
				that.getView().getModel("oMonthTrendModel").refresh(true);
			});
		},
		getMajorTaxPaying: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getMajTaxPayingProds.do";
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
						that.getView().setModel(new JSONModel(oData), "oMajorTaxModelMain");
						that.getView().setModel(new JSONModel(data.resp), "oMajorTaxModel");
						that.getView().getModel("oMajorTaxModel").refresh(true);
						that.getMajorTaxPayingValue();
					} else {
						that.getView().setModel(new JSONModel([]), "oMajorTaxModel");
						that.getView().getModel("oMajorTaxModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oMajorTaxModel");
				that.getView().getModel("oMajorTaxModel").refresh(true);
			});
		},
		getMajorTaxPayingRate: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getMajorTaxPayingProduct.do";
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
						that.getView().setModel(new JSONModel(oData), "oMajorTaxRateMain");
						that.getView().setModel(new JSONModel(data.resp), "oMajorTaxRate");
						that.getView().getModel("oMajorTaxRate").refresh(true);
						that.getMajorTaxPayingRateValue();
					} else {
						that.getView().setModel(new JSONModel([]), "oMajorTaxRate");
						that.getView().getModel("oMajorTaxRate").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oMajorTaxRate");
				that.getView().getModel("oMajorTaxRate").refresh(true);
			});
		},
		getTaxRatewise: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getTaxRateWiseDist.do";
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
						that.getView().setModel(new JSONModel(oData), "otaxwiseDistModelMain");
						that.getView().setModel(new JSONModel(data.resp), "otaxwiseDistModel");
						that.getView().getModel("otaxwiseDistModel").refresh(true);
						that.getTaxRatewiseValue();
					} else {
						that.getView().setModel(new JSONModel([]), "otaxwiseDistModel");
						that.getView().getModel("otaxwiseDistModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oTopCustB2BModel");
				that.getView().getModel("oTopCustB2BModel").refresh(true);
			});
		},
		getTaxLiabilityDetails: function (aPayload) {
			var oView = this.getView();
			var that = this;
			var Gstr2APath = "/aspsapapi/getTaxLiabDetails.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var data1 = data;
					if (data.hdr.status === "S") {
						// that.getView().setModel(new JSONModel(data.resp), "oTaxLiabDetailsModel");
						// that.getView().getModel("oTaxLiabDetailsModel").refresh(true);

						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data1.resp.taxLiability.length; i++) {
							var ele = data.resp.taxLiability[i];
							var lvl = ele.level; // Get the level of the cur Obj.
							if (lvl === "L1") {
								curL1Obj = ele;
								retArr.push(curL1Obj);
								curL1Obj.level2 = [];
							}
							if (lvl === "L2") {
								curL2Obj = ele;
								curL1Obj.level2.push(curL2Obj);
							}
						}
						var OutwardTable = new JSONModel(retArr);
						oView.setModel(OutwardTable, "oTaxLiabDetailsModel");
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

		onDownloadGross: function () {
			var oVizFrame = this.getView().byId("id_iddonut");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Gross Outward Suppliers"
				}
			});
			var filename = "Gross Outward Suppliers";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Gross Outward Suppliers"
				}
			});
		},
		onDownloadMonth: function () {
			var oVizFrame = this.getView().byId("vfBiDashLine");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Month Wise Trend Analysis"
				}
			});
			var filename = "Monthwise Trend Analysis";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Month Wise Trend Analysis"
				}
			});
		},

		onDownloadTopCustomer: function () {
			var oSwitch = this.getView().byId("idSwitchB2B")
			if (oSwitch.getState()) {
				this.onDownloadTopCustomerB2BTable()
			} else {
				this.onDownloadTopCustomerB2BGraph()
			}
		},

		onDownloadTopCustomerB2BGraph: function () {
			var oVizFrame = this.getView().byId("idVizFrameBar");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Top Customers B2B"
				}
			});
			var filename = "Top Customer B2B";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Top Customers B2B"
				}
			});
		},
		onDownloadMajorTax: function () {

			var oSwitch = this.getView().byId("idSwitchMajorTax")
			if (oSwitch.getState()) {
				var oVizFrame = this.getView().byId("idVizFrame_stacked_bar");
			} else {
				var oVizFrame = this.getView().byId("idVizFrameBar1");
			}

			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Major Tax Paying Products"
				}
			});
			var filename = "Major Tax paying";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Major Tax Paying Products"
				}
			});
		},
		onDownloadTotalRate: function () {
			var oVizFrame = this.getView().byId("id_iddonut2");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Tax Rate Wise Distribution"
				}
			});
			var filename = "Total Rate Wise Distribution";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Tax Rate Wise Distribution"
				}
			});
		},
		onSavePDF: function (val1, val2) {

			//Step 1: Export chart content to svg
			var oVizFrame = val1;
			var sSVG = oVizFrame.exportToSVGString({
				width: 1200,
				height: 1200
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
			oPDF.addImage(sImageData, "PNG", 10, 30, 160, 120);
			// oPDF.addImage(sImageData, "PNG", 10, 30, 160, 120);
			oPDF.save(val2);
		},

		onDownloadTopCustomerB2BTable: function () {

			var oData = this.getView().getModel("oTopCustB2BHSN").getData();
			var oCashTabData = [];
			for (var i = 0; i < oData.taxLiability.length; i++) {
				for (var j = 0; j < oData.taxLiability[i].hsndto.length; j++) {
					oCashTabData.push({
						"gstin": oData.taxLiability[i].transactionType,
						"hsn": oData.taxLiability[i].hsndto[j].transactionType,
						"invoiceValue": this.DenominationNum(oData.taxLiability[i].hsndto[j].invoiceValue),
						"taxableValue": this.DenominationNum(oData.taxLiability[i].hsndto[j].taxableValue)
					})
				}

			}
			var oCashTabModel = new sap.ui.model.json.JSONModel();
			oCashTabModel.setData(oCashTabData);

			var oExport = new Export({
				exportType: new ExportTypeCSV({
					separatorChar: "\t",
					mimeType: "application/vnd.ms-excel",
					charset: "utf-8",
					fileExtension: "xls"
				}),

				models: oCashTabModel,

				rows: {
					path: "/"
				},

				columns: [{
					name: "GSTIN",
					template: {
						content: "{gstin}"
					}
				}, {
					name: "HSN",
					template: {
						content: "{hsn}"
					}
				}, {
					name: "Invoice Value",
					template: {
						content: "{invoiceValue}"
					}
				}, {
					name: "Taxable Value",
					template: {
						content: "{taxableValue}"
					}
				}]
			});
			// this.onSearch();

			var xlName = "Top Customers B2B"
			oExport.saveFile(xlName).catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});

		},
		onSwitchB2B: function (oEvt) {

			if (oEvt.getSource().getState() === false) {
				this.byId("idTopCustB2BDownload").setVisible(true);
				this.byId("idTopCustB2B_A").setVisible(true);
				this.byId("idTopCustB2B_I").setVisible(false);
			} else {
				this.byId("idTopCustB2B_A").setVisible(false);
				this.byId("idTopCustB2B_I").setVisible(true);
				this.byId("idTopCustB2BDownload").setVisible(true);
			}

		},
		onSwitchMajorTax: function (oEvt) {

			if (oEvt.getSource().getState() === false) {
				this.byId("idMajorTax_A").setVisible(true);
				this.byId("idMajorTax_I").setVisible(false);
			} else {

				this.byId("idMajorTax_A").setVisible(false);
				this.byId("idMajorTax_I").setVisible(true);
			}

		}

	});

});