sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox) {
	"use strict";
	this.flag3a = true;
	return BaseController.extend("com.ey.digigst.controller.InwardSAC2", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.InwardSAC2
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("InwardSAC2").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			this.byId("PRid").setText("GSTR-2B vs Purchase Register");
			this.onLoadDinomination();
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this.onLoadFY(),
					this.onLoadGSTIN()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					if (values[0].hdr.status === "S") {
						this.onLoadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
					}
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			var oVizFrameDonut = this.oVizFrame = this.getView().byId("id_iddonut");
			var oVizFrameBar = this.oVizFrame = this.getView().byId("idVizFrame");
			var oVizFrameBar3a = this.oVizFrame = this.getView().byId("idVizFrame3a");
			var oVizFrameBar1 = this.oVizFrame = this.getView().byId("idVizFrame1");
			var oVizFrameBar13a = this.oVizFrame = this.getView().byId("idVizFrame13a");
			oVizFrameDonut.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"],
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
			var oPopOver = this.getView().byId("idPopOver2");
			oPopOver.connect(oVizFrameDonut.getVizUid());

			oVizFrameBar.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"]
				},
				title: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: true,
						text: "Total Tax"
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			var oPopOver1 = this.getView().byId("idPopOver");
			oPopOver1.connect(oVizFrameBar.getVizUid());

			oVizFrameBar3a.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"]
				},
				title: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: true,
						text: "Total Tax"
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			var oPopOver3a = this.getView().byId("idPopOver3a");
			oPopOver3a.connect(oVizFrameBar3a.getVizUid());

			oVizFrameBar1.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"]
				},
				title: {
					visible: false
				},
				legend: {
					visible: true
				},
				valueAxis: {
					title: {
						visible: true,
						text: "Total Tax"
					}
				},
				categoryAxis: {
					title: {
						visible: true,
						text: "Report Type"
					}
				}
			});
			var oPopOver2 = this.getView().byId("idPopOver1");
			oPopOver2.connect(oVizFrameBar1.getVizUid());

			oVizFrameBar13a.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"]
				},
				title: {
					visible: false
				},
				legend: {
					visible: true
				},
				valueAxis: {
					title: {
						visible: true,
						text: "Total Tax"
					}
				},
				categoryAxis: {
					title: {
						visible: true,
						text: "Report Type"
					}
				}
			});
			var oPopOver23a = this.getView().byId("idPopOver13a");
			oPopOver23a.connect(oVizFrameBar13a.getVizUid());

		},
		/*onAfterRendering: function () {
			this.onGoSelect();
		},*/
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
			}];
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
							// that.onLoadReturn(that.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
						} else {
							that.getView().setModel(new JSONModel([]), "oFyModel");
						}
						that.getView().getModel("oFyModel").refresh(true);
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
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecepGstins.do",
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
							var oSelectedKeyG = that.getOwnerComponent().getModel("GloblePayloadInward").getData();
							if (oSelectedKeyG.gstin.length === 0) {
								that.byId("idSupplier").setSelectedKeys(oSelectedKey);
							}
							// that.byId("idSupplier").setSelectedKeys(oSelectedKey);
						} else {
							that.getView().setModel(new JSONModel([]), "oGstinModel");
							that.getView().getModel("oGstinModel").refresh(true);
						}
						resolve(data);
					})
					.fail(function (err) {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
						reject(err);
					});
			});
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
				var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayloadInward").getProperty("/period");
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
			// var Gstr2APath = "/aspsapapi/getAllReturnPeriods.do";
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
			// 			var oSelectedKeyG = that.getOwnerComponent().getModel("GloblePayloadInward").getData();
			// 			if (oSelectedKeyG.period.length === 0) {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKey);
			// 			} else {
			// 				that.byId("idReturn").setSelectedKeys(oSelectedKeyG.period);
			// 			}
			// 			// that.byId("idReturn").setSelectedKeys(oSelectedKey);
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

		DenominationFormateValue: function () {
			this.gstr2a2bPrValue();
			var evt = this.getView().byId("idRdValue").getSelectedIndex();
			if (evt === 0) {
				this.byId("PRid").setText("GSTR-2B vs Purchase Register");
				this.Top10SuppliersValue();
				this.gstr12aValue();
			} else {
				this.byId("PRid").setText("GSTR-2A vs Purchase Register");
				this.Top10Suppliers1Value();
				this.gstr12a1Value();
			}
		},

		gstr2a2bPrValue: function () {

			var oData1 = this.getView().getModel("gstr2a2bPrMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.get2aVs2bVsPrSummary.length; i++) {
				oData.resp.get2aVs2bVsPrSummary[i].yAxis = this.DenominationNum(oData.resp.get2aVs2bVsPrSummary[i].yAxis)
			}

			this.getView().setModel(new JSONModel(oData.resp), "gstr2a2bPr");
			this.getView().getModel("gstr2a2bPr").refresh(true);
		},

		Top10SuppliersValue: function () {

			var oData1 = this.getView().getModel("Top10Suppliers2bMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.prVsGstr2b.length; i++) {
				oData.resp.prVsGstr2b[i].gstr2 = this.DenominationNum(oData.resp.prVsGstr2b[i].gstr2)
				oData.resp.prVsGstr2b[i].prTotalTax = this.DenominationNum(oData.resp.prVsGstr2b[i].prTotalTax)
			}

			this.getView().setModel(new JSONModel(oData.resp.prVsGstr2b), "Top10Suppliers");
			this.getView().getModel("Top10Suppliers").refresh(true);
		},
		Top10Suppliers1Value: function () {

			var oData1 = this.getView().getModel("Top10Suppliers2aMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.prVsGstr2a.length; i++) {
				oData.resp.prVsGstr2a[i].gstr2 = this.DenominationNum(oData.resp.prVsGstr2a[i].gstr2)
				oData.resp.prVsGstr2a[i].prTotalTax = this.DenominationNum(oData.resp.prVsGstr2a[i].prTotalTax)
			}

			this.getView().setModel(new JSONModel(oData.resp.prVsGstr2a), "Top10Suppliers");
			this.getView().getModel("Top10Suppliers").refresh(true);
		},
		gstr12aValue: function () {

			var oData1 = this.getView().getModel("GSTR12AVSPR2bMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.purchaseRegisterVsGstr2b.length; i++) {
				oData.resp.purchaseRegisterVsGstr2b[i].gstr2TotalTax = this.DenominationNum(oData.resp.purchaseRegisterVsGstr2b[i].gstr2TotalTax)
				oData.resp.purchaseRegisterVsGstr2b[i].prTotalTax = this.DenominationNum(oData.resp.purchaseRegisterVsGstr2b[i].prTotalTax)
			}

			this.getView().setModel(new JSONModel(oData.resp.purchaseRegisterVsGstr2b), "GSTR12AVSPR");
			this.getView().getModel("GSTR12AVSPR").refresh(true);
		},
		gstr12a1Value: function () {

			var oData1 = this.getView().getModel("GSTR12AVSPR2aMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.purchaseRegisterVsGstr2a.length; i++) {
				oData.resp.purchaseRegisterVsGstr2a[i].gstr2TotalTax = this.DenominationNum(oData.resp.purchaseRegisterVsGstr2a[i].gstr2TotalTax)
				oData.resp.purchaseRegisterVsGstr2a[i].prTotalTax = this.DenominationNum(oData.resp.purchaseRegisterVsGstr2a[i].prTotalTax)
			}

			this.getView().setModel(new JSONModel(oData.resp.purchaseRegisterVsGstr2a), "GSTR12AVSPR");
			this.getView().getModel("GSTR12AVSPR").refresh(true);
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
				return value2;
			}
		},

		onFyChange: function (oEvnt) {
			this.getOwnerComponent().getModel("GloblePayloadInward").setProperty("/period", []);
			this.getOwnerComponent().getModel("GloblePayloadInward").refresh(true);
			this.onLoadReturn(oEvnt.getSource().getSelectedKey());
		},
		onSupplierChange: function () {
			this.ChartBind();
		},
		onSelectTrendValue: function (oevent) {
			var that = this;
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

			if (aGstin.length != 0) {
				if (aGstin[0] == "All") {
					aGstin.shift();
				}
			}
			if (aRetPeriod.length != 0) {
				if (aRetPeriod[0] == "All") {
					aRetPeriod.shift();
				}
			}

			var aPayload = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod
				}
			};
			var evt = oevent.getSource().getSelectedIndex();
			if (evt === 0) {
				this.flag3a = true;
				this.byId("idPopOver").setVisible(true);
				this.byId("idVizFrame").setVisible(true);
				this.byId("idPopOver3a").setVisible(false);
				this.byId("idVizFrame3a").setVisible(false);

				this.byId("idPopOver1").setVisible(true);
				this.byId("idVizFrame1").setVisible(true);
				this.byId("idPopOver13a").setVisible(false);
				this.byId("idVizFrame13a").setVisible(false);
				//this.byId("valueAxid").setValue("2B TOTAL TAX");
				this.byId("PRid").setText("GSTR-2B vs Purchase Register");
				this.Top10Suppliers(aPayload);
				this.gstr12a(aPayload);
			} else {
				this.flag3a = false;
				this.byId("idPopOver").setVisible(false);
				this.byId("idVizFrame").setVisible(false);
				this.byId("idPopOver3a").setVisible(true);
				this.byId("idVizFrame3a").setVisible(true);

				this.byId("idPopOver1").setVisible(false);
				this.byId("idVizFrame1").setVisible(false);
				this.byId("idPopOver13a").setVisible(true);
				this.byId("idVizFrame13a").setVisible(true);
				//this.byId("valueAxid").setValue("2A TOTAL TAX");
				this.byId("PRid").setText("GSTR-2A vs Purchase Register");
				this.Top10Suppliers1(aPayload);
				this.gstr12a1(aPayload);
			}

		},

		onSelectTrendValue1: function (oevent) {
			var that = this;
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

			if (aGstin.length != 0) {
				if (aGstin[0] == "All") {
					aGstin.shift();
				}
			}
			if (aRetPeriod.length != 0) {
				if (aRetPeriod[0] == "All") {
					aRetPeriod.shift();
				}
			}

			var vIndex = oevent.getSource().getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "TOP_SUPPLIERS";
				break;
			case 1:
				vValue = "Taxable_Value";
				break;
			default:
				vValue = "Total_Tax";
			}

			var feedValueAxis = this.getView().byId('valueAxisFeed12');
			this.getView().byId("id_iddonut").removeFeed(feedValueAxis);
			if (vValue == "TOP_SUPPLIERS") {
				var oBject = {
					"value": ["Supplier Count"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "Taxable_Value") {
				var oBject = {
					"value": ["Taxable Value"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "Total_Tax") {
				var oBject = {
					"value": ["Total Tax"]
				};
				feedValueAxis.setValues(oBject.value);
			}
			this.getView().byId("id_iddonut").addFeed(feedValueAxis);

			var aPayload = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this.gstr2a2bPr(aPayload);
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
			//this.onDinomChange();
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
		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			var aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			//var value = this.getView().byId("idDimension").getSelectedKey();

			if (aGstin.length === 0) {
				MessageBox.error("Please select Recipient GSTIN");
				return;
			}

			if (aRetPeriod.length === 0) {
				MessageBox.error("Please Select Return Period");
				return;
			}

			var oBject = {
				"Key": this.vSelectDenomKey
			};
			this.getView().setModel(new JSONModel(oBject), "oDmModel");
			if (aGstin.length !== 0) {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			if (aRetPeriod.length !== 0) {
				if (aRetPeriod[0] === "All") {
					aRetPeriod.shift();
				}
			}
			var aPayload = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod
				}
			};

			var aPayload1 = {
				"req": {
					"fy": vFy,
					"valueFlag": "TOP_SUPPLIERS",
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod
				}
			};

			this.getLastUpdated(aPayload);
			this.getOutward2HeaderData(aPayload);
			//this.Top10Suppliers(aPayload);
			//this.gstr12a(aPayload);
			this.gstr2a2bPr(aPayload1);
			this.TaxInwardDetails(aPayload);
			var evt = this.getView().byId("idRdValue").getSelectedIndex();
			if (evt === 0) {
				this.byId("PRid").setText("GSTR-2B vs Purchase Register");
				this.Top10Suppliers(aPayload);
				this.gstr12a(aPayload);
			} else {
				this.byId("PRid").setText("GSTR-2A vs Purchase Register");
				this.Top10Suppliers1(aPayload);
				this.gstr12a1(aPayload);
			}
		},

		getLastUpdated: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getReconLastUpdatedOn.do";
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
						that.getView().setModel(new JSONModel(data.resp), "getLastUpdatedOn");
						that.getView().getModel("getLastUpdatedOn").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "getLastUpdatedOn");
						that.getView().getModel("getLastUpdatedOn").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "getLastUpdatedOn");
				that.getView().getModel("getLastUpdatedOn").refresh(true);
			});
		},

		onPressNextPage: function () {
			this.getRouter().navTo("InwardSAC2");
		},
		getOutward2HeaderData: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getInward2HeaderData.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oHeaderDetailsModel1");
						that.getView().getModel("oHeaderDetailsModel1").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel1");
						that.getView().getModel("oHeaderDetailsModel1").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oHeaderDetailsModel1");
				that.getView().getModel("oHeaderDetailsModel1").refresh(true);
			});
		},

		Top10Suppliers: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPrVsGstr2bData.do";
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
						that.getView().setModel(new JSONModel(oData), "Top10Suppliers2bMain");
						that.getView().setModel(new JSONModel(data.resp.prVsGstr2b), "Top10Suppliers");
						that.getView().getModel("Top10Suppliers").refresh(true);
						that.Top10SuppliersValue();
					} else {
						that.getView().setModel(new JSONModel([]), "Top10Suppliers");
						that.getView().getModel("Top10Suppliers").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "Top10Suppliers");
				that.getView().getModel("Top10Suppliers").refresh(true);
			});
		},

		Top10Suppliers1: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPrVsGstr2aData.do";
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
						that.getView().setModel(new JSONModel(oData), "Top10Suppliers2aMain");
						that.getView().setModel(new JSONModel(data.resp.prVsGstr2a), "Top10Suppliers");
						that.getView().getModel("Top10Suppliers").refresh(true);
						that.Top10Suppliers1Value();
					} else {
						that.getView().setModel(new JSONModel([]), "Top10Suppliers");
						that.getView().getModel("Top10Suppliers").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "Top10Suppliers");
				that.getView().getModel("Top10Suppliers").refresh(true);
			});
		},

		gstr12a: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPurchaseRegisterVsGstr2b.do";
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
						that.getView().setModel(new JSONModel(oData), "GSTR12AVSPR2bMain");
						that.getView().setModel(new JSONModel(data.resp.purchaseRegisterVsGstr2b), "GSTR12AVSPR");
						that.getView().getModel("GSTR12AVSPR").refresh(true);
						that.gstr12aValue();
					} else {
						that.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						that.getView().getModel("GSTR12AVSPR").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
				that.getView().getModel("GSTR12AVSPR").refresh(true);
			});
		},

		gstr12a1: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPurchaseRegisterVsGstr2a.do";
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
						that.getView().setModel(new JSONModel(oData), "GSTR12AVSPR2aMain");
						that.getView().setModel(new JSONModel(data.resp.purchaseRegisterVsGstr2a), "GSTR12AVSPR");
						that.getView().getModel("GSTR12AVSPR").refresh(true);
						that.gstr12a1Value();
					} else {
						that.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						that.getView().getModel("GSTR12AVSPR").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
				that.getView().getModel("GSTR12AVSPR").refresh(true);
			});
		},

		gstr2a2bPr: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/get2aVs2bVsPrSummary.do";
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
						that.getView().setModel(new JSONModel(oData), "gstr2a2bPrMain");
						that.getView().setModel(new JSONModel(data.resp), "gstr2a2bPr");
						that.getView().getModel("gstr2a2bPr").refresh(true);
						that.gstr2a2bPrValue();
					} else {
						that.getView().setModel(new JSONModel([]), "gstr2a2bPr");
						that.getView().getModel("gstr2a2bPr").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "gstr2a2bPr");
				that.getView().getModel("gstr2a2bPr").refresh(true);
			});
		},

		TaxInwardDetails: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getPr2a2bData.do";
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
						that.getView().setModel(new JSONModel(data.resp), "TaxInwardDetails");
						that.getView().getModel("TaxInwardDetails").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "TaxInwardDetails");
						that.getView().getModel("TaxInwardDetails").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "TaxInwardDetails");
				that.getView().getModel("TaxInwardDetails").refresh(true);
			});
		},

		onPressBackPage: function () {
			var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayloadInward").getData();
			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			this.getOwnerComponent().getModel("GloblePayloadInward").refresh(true);

			this.getRouter().navTo("InwardSAC");
		},

		onDownloadGross: function () {

			if (this.flag3a) {
				var oVizFrame = this.getView().byId("idVizFrame");

			} else {
				var oVizFrame = this.getView().byId("idVizFrame3a");

			}

			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Top 5 Suppliers"
				}
			});
			var filename = "Top 5 Suppliers";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Top 5 Suppliers"
				}
			});

		},

		onDownloadMonth: function () {
			var evt = this.byId("idRdValue").getSelectedIndex();
			var text = "";
			if (evt === 0) {
				text = "GSTR-2B vs Purchase Register";
				var filename = "GSTR-2B vs Purchase Register";
				var oVizFrame = this.getView().byId("idVizFrame1");
			} else {
				text = "GSTR-2A vs Purchase Register";
				var filename = "GSTR-2A vs Purchase Register";
				var oVizFrame = this.getView().byId("idVizFrame13a");
			}

			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: text
				}
			});

			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: text
				}
			});
		},

		onDownloadTopCustomer: function () {

			var vIndex = this.byId("idRdValue1").getSelectedIndex();
			var vValue = "";
			switch (vIndex) {
			case 0:
				vValue = "Supplier Count";
				break;
			case 1:
				vValue = "Taxable Value";
				break;
			default:
				vValue = "Total Tax";
			}
			var oVizFrame = this.getView().byId("id_iddonut");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "2A, 2B and PR Summary" + "(" + vValue + ")"
				}
			});
			var filename = "2A, 2B and PR Summary" + "(" + vValue + ")";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "2A, 2B and PR Summary"
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

			/*var svg = document.querySelector('svg');
			var serializer = new XMLSerializer();
			var svgString = serializer.serializeToString(svg);
			var canvas = document.getElementById("test");
			canvg(canvas, svgString);*/

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
			// oPDF.addImage(sImageData, "PNG", 10, 30, 160, 120);
			oPDF.save(val2);
		}

	});

});