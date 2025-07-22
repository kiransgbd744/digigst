sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.InwardSAC", {

		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("InwardSAC").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("InwardSAC");

			this.globalCall();
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
			var oVizFrameLine = this.oVizFrame = this.getView().byId("vfBiDashLine");
			var oVizFrameBar = this.oVizFrame = this.getView().byId("idVizFrameBar");
			var oVizFrameBar1 = this.oVizFrame = this.getView().byId("idVizFrameBar1");
			var oVizFrameDonut1 = this.oVizFrame = this.getView().byId("id_iddonut2");

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
					text: "Gross Inward Suppliers"
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
				/*plotArea: {
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

				}*/
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"],
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
			var oPopOver2 = this.getView().byId("idPopOver3");
			oPopOver2.connect(oVizFrameBar1.getVizUid());

			oVizFrameBar.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"],
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
			var oPopOver3 = this.getView().byId("idPopOver2");
			oPopOver3.connect(oVizFrameBar.getVizUid());

			oVizFrameDonut1.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#1777CF", "#FF6D00", "#189D3E", "#B9251C", "#750E5C", "#42C9C2"],
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
		globalCall: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				var oData = {
					"fy": "",
					"gstin": [],
					"period": []
				}

				this.getOwnerComponent().setModel(new JSONModel(oData), "GloblePayloadInward");
			}
		},

		onLoadFY: function () {
			var that = this;
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
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
			// 			debugger;
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
			/*var value = this.getView().byId("idDimension").getSelectedKey();
			var oBject = {
				"Key": value
			};
			this.getView().setModel(new JSONModel(oBject), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);*/
			this.DenominationFormateValue();
		},

		DenominationFormateValue: function () {
			this.GrossOutBindValue();
			this.getTopCustB2BValue();
			this.getMonthwiseTrendValue();
			this.getMajorTaxPayingValue();
			this.getTaxRatewiseValue();
		},

		GrossOutBindValue: function () {

			var oData1 = this.getView().getModel("oGrossModelMain").getData();
			var oData = $.extend(true, {}, oData1);
			for (var i = 0; i < oData.resp.grossInward.length; i++) {
				oData.resp.grossInward[i].yAxis = this.DenominationNum(oData.resp.grossInward[i].yAxis)
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
			case 2:
				vValue = "TAX_AMOUNT";
				break;
			default:
				vValue = "CREDIT_AVAILABLE";
			}
			var feedValueAxis = this.getView().byId('valueAxisFeed12');
			this.getView().byId("vfBiDashLine").removeFeed(feedValueAxis);
			if (vValue == "INVOICE_VALUE") {
				var oBject = {
					"value": ["Invoice Value"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "CREDIT_AVAILABLE") {
				var oBject = {
					"value": ["Credit Available"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAX_AMOUNT") {
				var oBject = {
					"value": ["Tax Amount"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAXABLE_VALUE") {
				var oBject = {
					"value": ["Taxable Value"]
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
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			that.getMonthwiseTrend(aPayload2);

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
			case 2:
				vValue = "TAX_AMOUNT";
				break;
			default:
				vValue = "CREDIT_AVAILABLE";
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
			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			var vValue = "";

			if (aGstin.length === 0) {
				MessageBox.error("Please select Recipient GSTIN");
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
				vValue = "CREDIT_AVAILABLE";
			}

			//var value = this.getView().byId("idDimension").getSelectedKey();
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
			var flag;
			var flagState = this.byId("switchId").getState();
			if (flagState === true) {
				flag = "on";
			} else if (flagState === false) {
				flag = "off";
			}
			var aPayload1 = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					// "returnPeriods": aRetPeriod.length,
					"returnPeriods": aRetPeriod,
					"flag": flag
				}
			};
			var aPayload = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					// "returnPeriods": aRetPeriod.length
					"returnPeriods": aRetPeriod

				}
			};

			var aPayload2 = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};

			this.getLastUpdated(aPayload);
			this.getLiabDetails(aPayload);
			this.GrossOutBind(aPayload1);
			this.getTopCustB2B(aPayload);
			// this.MonthCheckSelect();
			this.getMonthwiseTrend(aPayload2);
			this.getMajorTaxPaying(aPayload);
			this.getTaxRatewise(aPayload);
			this.getTaxLiabilityDetails(aPayload);
		},

		/*Code added by Arun on 15/12/2021*/
		onSwitch: function (oEvent) {

			// if (oEvent.getSource().getState()) {
			// 	var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			// 	this.getView().setModel(oModel, "oGrossModel");
			// } else if (!oEvent.getSource().getState()) {
			this.ChartBind();
			this.GrossOutBind();
			// }

		},
		/*Code ended by Arun on 15/12/2021*/

		getLastUpdated: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getLastUpdatedOn.do";
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

		getLiabDetails: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getTotalITCDetails.do";
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
			var Gstr2APath = "/aspsapapi/getGrossInSup.do";
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
			var Gstr2APath = "/aspsapapi/getTopCustomerB2B.do";
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
		getMonthwiseTrend: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getMonthWiseTrendAnalysis.do";
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
			var Gstr2APath = "/aspsapapi/getMajGoodsProc.do";
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
		getTaxRatewise: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getTaxRateWiseDistribution.do";
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
			var Gstr2APath = "/aspsapapi/getTaxInwardDetails.do";
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
					if (data1.hdr.status === "S") {

						// for (var i = 0; i < data1.resp.taxInwardDetails.length; i++) {
						// 	data1.resp.taxInwardDetails[i].HDR = "L1";
						// }
						// var InwardTable = new sap.ui.model.json.JSONModel(data1);
						// oView.setModel(InwardTable, "oTaxLiabDetailsModel");
						// // that.getView().setModel(new JSONModel(data.resp), "oTaxLiabDetailsModel");
						// that.getView().getModel("oTaxLiabDetailsModel").refresh(true);

						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data1.resp.taxInwardDetails.length; i++) {
							var ele = data.resp.taxInwardDetails[i];
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
						var InwardTable = new JSONModel(retArr);
						oView.setModel(InwardTable, "oTaxLiabDetailsModel");
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
		onPressNextPage: function () {
			var oSelectedKeyG = this.getOwnerComponent().getModel("GloblePayloadInward").getData();
			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			this.getOwnerComponent().getModel("GloblePayloadInward").refresh(true);

			this.getRouter().navTo("InwardSAC2");
		},
		onDownloadGross: function () {
			var oVizFrame = this.getView().byId("id_iddonut");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Gross Inward Suppliers"
				}
			});
			var filename = "Gross Inward Suppliers";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Gross Inward Suppliers"
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
			var oVizFrame = this.getView().byId("idVizFrameBar");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Top Suppliers B2B"
				}
			});
			var filename = "Top Suppliers B2B";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Top Suppliers B2B"
				}
			});
		},
		onDownloadMajorTax: function () {
			var oVizFrame = this.getView().byId("idVizFrameBar1");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Major Goods/Services Procured"
				}
			});
			var filename = "Major Goods/Services Procured";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Major Goods/Services Procured"
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
			var filename = "Tax Rate Wise Distribution";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
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