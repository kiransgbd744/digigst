sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GDashboardInward1", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GDashboardInward1
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("GDashboardInward1").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			this.onLoadDinomination();
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._loadAllFy(),
					this._loadRecipientGstin()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					if (values[0].hdr.status === "S") {
						this.onLoadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
					}
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			this._setVizProperty("poGrossInward", "vizGrossInward", "Gross Inward Suppliers", "glossy");
			this._setVizProperty("poMonthWise", "vizMonthWise", "Month Wise Trend Analysis", "glossy");
			this._setVizProperty("poB2bSupplier", "vizB2bSupplier", "Top Suppliers B2B", null);
			this._setVizProperty("poMajorGoods", "vizMajorGoods", "Major Goods/Services Procured", null);
			this._setVizProperty("poTaxRateWise", "vizTaxRateWise", "Tax Rate Wise Distribution", "glossy");
		},

		onLoadDinomination: function () {
			var entity = this.getOwnerComponent().getModel("entityAll").getProperty("/1/entityId");
			this.getOwnerComponent().setModel(new JSONModel({
				"entity": [entity],
				"fy": "",
				"gstin": [],
				"period": []
			}), "GloblePayloadInward");

			this.getView().setModel(new JSONModel([{
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
			}]), "oDinomtion");

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

		_loadAllFy: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel();
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data.resp);
						}
						this.getView().setModel(oModel, "oFyModel");
						this.getView().getModel("oFyModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						this.getView().setModel(new JSONModel(), "oFyModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_loadRecipientGstin: function () {
			return new Promise(function (resolve, reject) {
				var aEntity = this.getOwnerComponent().getModel("GloblePayloadInward").getProperty("/entity"),
					PayLoad = {
						"req": {
							"entityId": this.removeAll(aEntity)
						}
					};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getRecepGstins.do",
						contentType: "application/json",
						data: JSON.stringify(PayLoad)
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel();
						if (data.hdr.status === "S") {
							if (data.resp.gstins.length != 0) {
								data.resp.gstins.unshift({
									"gstin": "All"
								});
							}
							oModel.setProperty("/", data.resp);
							var aKey = data.resp.gstins.map(function (e) {
								return e.gstin;
							});
							this.getOwnerComponent().getModel("GloblePayloadInward").setProperty("/gstin", aKey);
						}
						this.getView().setModel(oModel, "oGstinModel");
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						this.getView().setModel(new JSONModel([]), "oGstinModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		selectEntity: function (oEvent) {
			this.selectAll(oEvent);
			this._loadRecipientGstin();
		},

		onLoadReturn: function (fy) {
			if (fy) {
				var sKey = this.getView().byId("idFinance").getSelectedKey(),
					today = new Date(),
					aYear = (sKey ? sKey.split("-") : fy.split("-")),
					cFyMonth = (today.getMonth() + 1).toString().padStart(2, 0) + today.getFullYear(),
					aFiscalYear = {
						"returnPeriods": [{
							"fy": "All",
							"select": true
						}]
					},
					oSelectedKey = ["All"];

				for (var i = 0; i < 12; i++) {
					var fromFy = new Date(aYear[0], (3 + i), 1),
						period = (fromFy.getMonth() + 1).toString().padStart(2, 0) + fromFy.getFullYear();

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
		},

		onChangeDinomination: function (oevnt) {
			this.vSelectDenomKey = oevnt.getParameter("item").getKey();
			var oBundle = this.getResourceBundle();
			this.getView().setModel(new JSONModel({
				"Key": this.vSelectDenomKey
			}), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);
			this.byId("idDimension").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(this.vSelectDenomKey));
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
			var oData1 = this.getView().getModel("oGrossModelMain").getProperty("/"),
				oData = $.extend(true, {}, oData1);

			oData.resp.grossInward.forEach(function (e) {
				e.yAxis = this.DenominationNum(e.yAxis);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "oGrossModel");
		},

		getTopCustB2BValue: function () {
			var oData1 = this.getView().getModel("oTopCustB2BModelMain").getProperty("/"),
				oData = $.extend(true, {}, oData1);

			oData.resp.topCustomers.forEach(function (e) {
				e.yAxis = this.DenominationNum(e.yAxis);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "oTopCustB2BModel");
		},

		getMonthwiseTrendValue: function () {
			var oData1 = this.getView().getModel("oMonthTrendModelMain").getProperty("/"),
				oData = $.extend(true, {}, oData1);

			oData.resp.monthWiseAnalysis.forEach(function (e) {
				e.yAxis = this.DenominationNum(e.yAxis);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "oMonthTrendModel");
		},

		getMajorTaxPayingValue: function () {
			var oData1 = this.getView().getModel("oMajorTaxModelMain").getProperty("/"),
				oData = $.extend(true, {}, oData1);

			oData.resp.majTaxPayingProds.forEach(function (e) {
				e.yAxis = this.DenominationNum(e.yAxis)
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "oMajorTaxModel");
		},

		getTaxRatewiseValue: function () {
			var oData1 = this.getView().getModel("otaxwiseDistModelMain").getProperty("/"),
				oData = $.extend(true, {}, oData1);

			oData.resp.taxRateWise.forEach(function (e) {
				e.invoiceValue = this.DenominationNum(e.invoiceValue);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "otaxwiseDistModel");
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

		onFyChange: function (oEvnt) {
			this.getOwnerComponent().getModel("GloblePayloadInward").setProperty("/period", []);
			this.getOwnerComponent().getModel("GloblePayloadInward").refresh(true);
			this.onLoadReturn(oEvnt.getSource().getSelectedKey());
		},

		onSupplierChange: function () {
			// this.ChartBind();
		},

		onSelectTrendValue: function (oevent) {
			var feedValueAxis = this.getView().byId('valueAxisFeed12'),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys(),
				vIndex = this.getView().byId("idRdValue").getSelectedIndex();

			this.getView().byId("vizMonthWise").removeFeed(feedValueAxis);
			switch (vIndex) {
			case 0:
				var vValue = "INVOICE_VALUE",
					obj = {
						"value": ["Invoice Value"]
					};
				break;
			case 1:
				vValue = "TAXABLE_VALUE";
				obj = {
					"value": ["Taxable Value"]
				};
				break;
			case 2:
				vValue = "TAX_AMOUNT";
				obj = {
					"value": ["Tax Amount"]
				};
				break;
			default:
				vValue = "CREDIT_AVAILABLE";
				obj = {
					"value": ["Credit Available"]
				};
			}
			feedValueAxis.setValues(obj.value);
			this.getView().byId("vizMonthWise").addFeed(feedValueAxis);

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
			var payload = {
				"req": {
					"fy": this.getView().byId("idFinance").getSelectedKey(),
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this._getMonthwiseTrend(payload);
		},

		onRetPeriodChange: function (evt) {
			var oData = this.getView().getModel("oRetPeriodModel").getProperty("/returnPeriods"),
				vlength = oData.length,
				vkeys = evt.getSource().getSelectedKeys();

			if (vkeys.includes("All")) {
				var oSelectedKey = oData.map(function (e) {
					return e.fy;
				});
				this.byId("idReturn").setSelectedKeys(oSelectedKey);
			} else {
				var vLength1 = vlength - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idReturn").setSelectedKeys([]);
				}
			}
			// this.ChartBind();
		},

		onGoSelect: function () {
			//this.onChangeDinomination();
			this.ChartBind();
		},

		MonthCheckSelect: function (evt) {
			var oData = this.getView().getModel("oRetPeriodModel1").getProperty("/returnPeriods"),
				aRetPeriod = [];
			oData.forEach(function (e) {
				if (e.select) {
					aRetPeriod.push(e.fy);
				}
			});

			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();
			switch (vIndex) {
			case 0:
				var vValue = "INVOICE_VALUE";
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
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys();
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
			var payload = {
				"req": {
					"fy": vFy,
					"supplierGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this._getMonthwiseTrend(payload);
		},

		MonthCheckSelectAll: function (evt) {
			var aData = this.getView().getModel("oRetPeriodModel1").getProperty("/returnPeriods"),
				vSelected = evt.getSource().getSelected();
			if (vSelected) {
				for (var i = 0; i < aData.length; i++) {
					aData[i].select = true;
				}
			} else {
				for (var i = 0; i < aData.length; i++) {
					aData[i].select = false;
				}
			}
			this.getView().getModel("oRetPeriodModel1").refresh(true);
			this.MonthCheckSelect();
		},

		onGstinChange: function (evt) {
			var aGstin = this.getView().getModel("oGstinModel").getProperty("/gstins"),
				vkeys = evt.getSource().getSelectedKeys(),
				vlength = aGstin.length;

			if (vkeys.includes("All")) {
				var oSelectedKey = aGstin.map(function (e) {
					return e.gstin;
				});
				this.byId("idSupplier").setSelectedKeys(oSelectedKey);
			} else {
				var vLength1 = vlength - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idSupplier").setSelectedKeys([]);
				}
			}
			// this.ChartBind();
		},

		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys(),
				vIndex = this.getView().byId("idRdValue").getSelectedIndex();

			if (!aGstin.length) {
				MessageBox.error("Please select Recipient GSTIN");
				return;
			}
			if (!aRetPeriod.length) {
				MessageBox.error("Please Select Return Period");
				return;
			}
			switch (vIndex) {
			case 0:
				var vValue = "INVOICE_VALUE";
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

			this.getView().setModel(new JSONModel({
				"Key": this.vSelectDenomKey
			}), "oDmModel");

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
			var flagState = this.byId("switchId").getState(),
				flag = flagState ? "on" : "off",
				aPayload = {
					"req": {
						"fy": vFy,
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod
					}
				},
				aPayload1 = {
					"req": {
						"fy": vFy,
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod,
						"flag": flag
					}
				},
				aPayload2 = {
					"req": {
						"fy": vFy,
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod,
						"valueFlag": vValue
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getLastUpdated(aPayload),
					this._getLiabDetails(aPayload),
					this._grossOutBind(aPayload1),
					this._getTopCustB2B(aPayload),
					this._getMonthwiseTrend(aPayload2),
					this._getMajorTaxPaying(aPayload),
					this._getTaxRatewise(aPayload),
					this._getTaxLiabilityDetails(aPayload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
			// this.MonthCheckSelect();
		},

		onSwitch: function (oEvent) {
			this.ChartBind();
			this._grossOutBind();
		},

		_getLastUpdated: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getLastUpdatedOn.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel();
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data.resp);
						}
						this.getView().setModel(oModel, "getLastUpdatedOn");
						this.getView().getModel("getLastUpdatedOn").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "getLastUpdatedOn");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getLiabDetails: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getTotalITCDetails.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel();
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data.resp);
						}
						this.getView().setModel(oModel, "oTotalLiabDetailsModel");
						this.getView().getModel("oTotalLiabDetailsModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oTotalLiabDetailsModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_grossOutBind: function (aPayload1) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGrossInSup.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload1)
					}).done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "oGrossModelMain");
							this.getView().setModel(new JSONModel(data.resp), "oGrossModel");
							this.GrossOutBindValue();
						} else {
							this.getView().setModel(new JSONModel(), "oGrossModel");
						}
						this.getView().getModel("oGrossModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oGrossModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getTopCustB2B: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getTopCustomerB2B.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "oTopCustB2BModelMain");
							this.getView().setModel(new JSONModel(data.resp), "oTopCustB2BModel");
							this.getTopCustB2BValue();
						} else {
							this.getView().setModel(new JSONModel(), "oTopCustB2BModel");
						}
						this.getView().getModel("oTopCustB2BModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(oModel, "oTopCustB2BModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getMonthwiseTrend: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getMonthWiseTrendAnalysis.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					}).done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "oMonthTrendModelMain");
							this.getView().setModel(new JSONModel(data.resp), "oMonthTrendModel");
							this.getMonthwiseTrendValue();
						} else {
							this.getView().setModel(new JSONModel(), "oMonthTrendModel");
						}
						this.getView().getModel("oMonthTrendModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oMonthTrendModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getMajorTaxPaying: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getMajGoodsProc.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "oMajorTaxModelMain");
							this.getView().setModel(new JSONModel(data.resp), "oMajorTaxModel");
							this.getMajorTaxPayingValue();
						} else {
							this.getView().setModel(new JSONModel(), "oMajorTaxModel");
						}
						this.getView().getModel("oMajorTaxModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oMajorTaxModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getTaxRatewise: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getTaxRateWiseDistribution.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "otaxwiseDistModelMain");
							this.getView().setModel(new JSONModel(data.resp), "otaxwiseDistModel");
							this.getTaxRatewiseValue();
						} else {
							this.getView().setModel(new JSONModel(), "otaxwiseDistModel");
						}
						this.getView().getModel("otaxwiseDistModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oTopCustB2BModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getTaxLiabilityDetails: function (aPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getTaxInwardDetails.do",
						contentType: "application/json",
						data: JSON.stringify(aPayload)
					})
					.done(function (data, status, jqXHR) {
						var oModel = new JSONModel();
						if (data.hdr.status === "S") {
							var retArr = [],
								objL1 = {};
							data.resp.taxInwardDetails.forEach(function (ele) {
								if (ele.level === "L1") {
									objL1 = ele;
									retArr.push(objL1);
									objL1.level2 = [];
								}
								if (ele.level === "L2") {
									objL1.level2.push(ele);
								}
							});
							oModel.setProperty("/", retArr);
						}
						this.getView().setModel(oModel, "oTaxLiabDetailsModel");
						this.getView().getModel("oTaxLiabDetailsModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oTaxLiabDetailsModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressNextPage: function () {
			var oModel = this.getOwnerComponent().getModel("GloblePayloadInward"),
				oSelectedKeyG = oModel.getProperty("/");
			oSelectedKeyG.fy = this.getView().byId("idFinance").getSelectedKey();
			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			oModel.refresh(true);

			this.getRouter().navTo("GDashboardInward2");
		},

		onDownloadGross: function () {
			var oVizFrame = this.getView().byId("vizGrossInward");
			oVizFrame.setVizProperties({
				title: {
					visible: true
				}
			});
			this.onSavePDF(oVizFrame, "Gross Inward Suppliers");
			oVizFrame.setVizProperties({
				title: {
					visible: false
				}
			});
		},

		onDownloadMonth: function () {
			var oVizFrame = this.getView().byId("vizMonthWise");
			oVizFrame.setVizProperties({
				title: {
					visible: true
				}
			});
			this.onSavePDF(oVizFrame, "Monthwise Trend Analysis");
			oVizFrame.setVizProperties({
				title: {
					visible: false
				}
			});
		},

		onDownloadTopCustomer: function () {
			var oVizFrame = this.getView().byId("vizB2bSupplier");
			oVizFrame.setVizProperties({
				title: {
					visible: true
				}
			});
			this.onSavePDF(oVizFrame, "Top Suppliers B2B");
			oVizFrame.setVizProperties({
				title: {
					visible: false
				}
			});
		},

		onDownloadMajorTax: function () {
			var oVizFrame = this.getView().byId("vizMajorGoods");
			oVizFrame.setVizProperties({
				title: {
					visible: true
				}
			});
			this.onSavePDF(oVizFrame, "Major Goods/Services Procured");
			oVizFrame.setVizProperties({
				title: {
					visible: false
				}
			});
		},

		onDownloadTotalRate: function () {
			var oVizFrame = this.getView().byId("vizTaxRateWise");
			oVizFrame.setVizProperties({
				title: {
					visible: true
				}
			});
			this.onSavePDF(oVizFrame, "Tax Rate Wise Distribution");
			oVizFrame.setVizProperties({
				title: {
					visible: false
				}
			});
		},

		onSavePDF: function (val1, pdfName) {
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
			oPDF.save(pdfName);
		},

		_setVizProperty: function (idPopover, idViz, text, drawEffect) {
			var oPopOver = this.getView().byId(idPopover),
				oVizFrame = this.getView().byId(idViz);

			oVizFrame.setVizProperties({
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
						visible: true
					}
				},
				categoryAxis: {
					title: {
						visible: true
					}
				}
			});
			if (drawEffect) {
				oVizFrame.setVizProperties({
					plotArea: {
						drawingEffect: "glossy"
					}
				});
			}
			if (text) {
				oVizFrame.setVizProperties({
					title: {
						text: text
					}
				});
			}
			oPopOver.connect(oVizFrame.getVizUid());
		}
	});

});