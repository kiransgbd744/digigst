sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GDashboardInward2", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GDashboardInward2
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("GDashboardInward2").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			this.byId("PRid").setText("GSTR-2B vs Purchase Register");
			this._loadDinomination();
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._loadAllFy(),
					this._loadRecipientGstin()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					this._loadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			this._setVizProperty("idPopOver2", "id_iddonut", "Gross Outward Suppliers", null, null, "glossy");
			this._setVizProperty("idPopOver", "idVizFrame", "Top 5 Suppliers", "Total Tax", null);
			this._setVizProperty("idPopOver3a", "idVizFrame3a", "Top 5 Suppliers", null, null);
			this._setVizProperty("idPopOver1", "idVizFrame1", "GSTR-2B vs Purchase Register", "Total Tax", "Report Type");
			this._setVizProperty("idPopOver13a", "idVizFrame13a", "GSTR-2A vs Purchase Register", "Total Tax", "Report Type");
		},

		_loadDinomination: function () {
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
			switch (vIndex) {
			case 'Thousands':
				var vValue = "K";
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
						this.getView().getModel("oGstinModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						this.getView().setModel(new JSONModel(), "oGstinModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		selectEntity: function (oEvent) {
			this.selectAll(oEvent);
			this._loadRecipientGstin();
		},

		_loadReturn: function (fy) {
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
			var oBundle = this.getResourceBundle();
			this.vSelectDenomKey = oevnt.getParameter("item").getKey();
			this.getView().setModel(new JSONModel({
				"Key": this.vSelectDenomKey
			}), "oDmModel");
			this.getView().getModel("oDmModel").refresh(true);
			this.byId("idDimension").setText(oBundle.getText("rupeesIn") + " " + oBundle.getText(this.vSelectDenomKey));
			this._denominationFormatValue();
		},

		_denominationFormatValue: function () {
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
			var aData = this.getView().getModel("gstr2a2bPrMain").getProperty("/"),
				oData = $.extend(true, {}, aData);

			oData.resp.get2aVs2bVsPrSummary.forEach(function (e) {
				e.yAxis = this.DenominationNum(e.yAxis)
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp), "gstr2a2bPr");
			this.getView().getModel("gstr2a2bPr").refresh(true);
		},

		Top10SuppliersValue: function () {
			var aData = this.getView().getModel("Top10Suppliers2bMain").getProperty("/"),
				oData = $.extend(true, {}, aData);

			oData.resp.prVsGstr2b.forEach(function (e) {
				e.gstr2 = this.DenominationNum(e.gstr2);
				e.prTotalTax = this.DenominationNum(e.prTotalTax);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp.prVsGstr2b), "Top10Suppliers");
			this.getView().getModel("Top10Suppliers").refresh(true);
		},

		Top10Suppliers1Value: function () {
			var aData = this.getView().getModel("Top10Suppliers2aMain").getProperty("/"),
				oData = $.extend(true, {}, aData);

			oData.resp.prVsGstr2a.forEach(function (e) {
				e.gstr2 = this.DenominationNum(e.gstr2);
				e.prTotalTax = this.DenominationNum(e.prTotalTax);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp.prVsGstr2a), "Top10Suppliers");
			this.getView().getModel("Top10Suppliers").refresh(true);
		},

		gstr12aValue: function () {
			var aData = this.getView().getModel("GSTR12AVSPR2bMain").getProperty("/"),
				oData = $.extend(true, {}, aData);

			oData.resp.purchaseRegisterVsGstr2b.forEach(function (e) {
				e.gstr2TotalTax = this.DenominationNum(e.gstr2TotalTax);
				e.prTotalTax = this.DenominationNum(e.prTotalTax);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp.purchaseRegisterVsGstr2b), "GSTR12AVSPR");
			this.getView().getModel("GSTR12AVSPR").refresh(true);
		},

		gstr12a1Value: function () {
			var aData = this.getView().getModel("GSTR12AVSPR2aMain").getProperty("/"),
				oData = $.extend(true, {}, aData);

			oData.resp.purchaseRegisterVsGstr2a.forEach(function (e) {
				e.gstr2TotalTax = this.DenominationNum(e.gstr2TotalTax);
				e.prTotalTax = this.DenominationNum(e.prTotalTax);
			}.bind(this));

			this.getView().setModel(new JSONModel(oData.resp.purchaseRegisterVsGstr2a), "GSTR12AVSPR");
			this.getView().getModel("GSTR12AVSPR").refresh(true);
		},

		DenominationNum: function (value2) {
			var value1 = this.vSelectDenomKey;
			if (!isNaN(value2) && value2 !== null && value2 !== 0 && value2 !== "") {
				switch (this.vSelectDenomKey) {
				case "K":
					value2 = (+value2 / 1000);
					var vFlagd = value1;
					break;
				case "L":
					value2 = (+value2 / 100000);
					vFlagd = 'Lac';
					break;
				case "C":
					value2 = (+value2 / 10000000);
					vFlagd = "Cr";
					break;
				case "M":
					value2 = (+value2 / 1000000);
					break;
				case "B":
					value2 = (+value2 / 1000000000);
					break;
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
			this._loadReturn(oEvnt.getSource().getSelectedKey());
		},

		onSupplierChange: function () {
			this.ChartBind();
		},

		onSelectTrendValue: function (oevent) {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

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

			var evt = oevent.getSource().getSelectedIndex(),
				aPayload = {
					"req": {
						"fy": vFy,
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod
					}
				};
			this.flag3a = !evt;
			this.byId("idPopOver").setVisible(!evt);
			this.byId("idVizFrame").setVisible(!evt);
			this.byId("idPopOver3a").setVisible(!!evt);
			this.byId("idVizFrame3a").setVisible(!!evt);
			this.byId("idPopOver1").setVisible(!evt);
			this.byId("idVizFrame1").setVisible(!evt);
			this.byId("idPopOver13a").setVisible(!!evt);
			this.byId("idVizFrame13a").setVisible(!!evt);
			this.byId("PRid").setText(!evt ? "GSTR-2B vs Purchase Register" : "GSTR-2A vs Purchase Register");

			if (evt === 0) {
				this.Top10Suppliers(aPayload);
				this.gstr12a(aPayload);
			} else {
				this.Top10Suppliers1(aPayload);
				this.gstr12a1(aPayload);
			}
		},

		onSelectTrendValue1: function (oevent) {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

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
			switch (vIndex) {
			case 0:
				var vValue = "TOP_SUPPLIERS";
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
			} else if (vValue == "Taxable_Value") {
				var oBject = {
					"value": ["Taxable Value"]
				};
			} else if (vValue == "Total_Tax") {
				var oBject = {
					"value": ["Total Tax"]
				};
			}
			feedValueAxis.setValues(oBject.value);
			this.getView().byId("id_iddonut").addFeed(feedValueAxis);

			var aPayload = {
				"req": {
					"fy": vFy,
					"recepientGstins": aGstin,
					"returnPeriods": aRetPeriod,
					"valueFlag": vValue
				}
			};
			this._gstr2a2bPr(aPayload);
		},

		onRetPeriodChange: function (evt) {
			var oData = this.getView().getModel("oRetPeriodModel").getProperty("/returnPeriods"),
				vkeys = evt.getSource().getSelectedKeys();

			if (vkeys.includes("All")) {
				var aKey = oData.map(function (e) {
					return oData[i].fy;
				});
				this.byId("idReturn").setSelectedKeys(aKey);
			} else {
				var vLength1 = oData.length - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idReturn").setSelectedKeys([]);
				}
			}
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
			default:
				vValue = "TAX_AMOUNT";
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
			var oData = this.getView().getModel("oRetPeriodModel1").getProperty("/returnPeriods"),
				vSelected = evt.getSource().getSelected();
			if (vSelected) {
				oData.forEach(function (e) {
					e.select = true;
				})
			} else {
				oData.forEach(function (e) {
					e.select = false;
				});
			}
			this.getView().getModel("oRetPeriodModel1").refresh(true);
			this.MonthCheckSelect();
		},

		onGstinChange: function (evt) {
			var oData = this.getView().getModel("oGstinModel").getProperty("/gstins"),
				vkeys = evt.getSource().getSelectedKeys();
			if (vkeys.includes("All")) {
				var oSelectedKey = oData.map(function (e) {
					return e.gstin;
				});
				this.byId("idSupplier").setSelectedKeys(oSelectedKey);
			} else {
				var vLength1 = oData.length - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idSupplier").setSelectedKeys([]);
				}
			}
		},

		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

			if (!aGstin.length) {
				MessageBox.error("Please select Recipient GSTIN");
				return;
			}

			if (!aRetPeriod.length) {
				MessageBox.error("Please Select Return Period");
				return;
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
			var payload = {
					"req": {
						"fy": vFy,
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod
					}
				},
				payload1 = {
					"req": {
						"fy": vFy,
						"valueFlag": "TOP_SUPPLIERS",
						"recepientGstins": aGstin,
						"returnPeriods": aRetPeriod
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getLastUpdated(payload),
					this._getOutward2HeaderData(payload),
					this._gstr2a2bPr(payload1),
					this._taxInwardDetails(payload)
				])
				.then(function (values) {
					this.byId("idRdValue").setSelectedIndex(0);
					this.byId("idRdValue1").setSelectedIndex(0);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			var evt = this.getView().byId("idRdValue").getSelectedIndex();
			if (evt === 0) {
				this.byId("PRid").setText("GSTR-2B vs Purchase Register");
				this.Top10Suppliers(payload);
				this.gstr12a(payload);
			} else {
				this.byId("PRid").setText("GSTR-2A vs Purchase Register");
				this.Top10Suppliers1(payload);
				this.gstr12a1(payload);
			}
		},

		_getLastUpdated: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getReconLastUpdatedOn.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data.resp), "getLastUpdatedOn");
						} else {
							this.getView().setModel(new JSONModel([]), "getLastUpdatedOn");
						}
						this.getView().getModel("getLastUpdatedOn").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "getLastUpdatedOn");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getOutward2HeaderData: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getInward2HeaderData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data.resp), "oHeaderDetailsModel1");
						} else {
							this.getView().setModel(new JSONModel([]), "oHeaderDetailsModel1");
						}
						this.getView().getModel("oHeaderDetailsModel1").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oHeaderDetailsModel1");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_gstr2a2bPr: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/get2aVs2bVsPrSummary.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "gstr2a2bPrMain");
							this.getView().setModel(new JSONModel(data.resp), "gstr2a2bPr");
							this.gstr2a2bPrValue();
						} else {
							this.getView().setModel(new JSONModel([]), "gstr2a2bPr");
						}
						this.getView().getModel("gstr2a2bPr").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "gstr2a2bPr");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_taxInwardDetails: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getPr2a2bData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data.resp), "TaxInwardDetails");
						} else {
							this.getView().setModel(new JSONModel([]), "TaxInwardDetails");
						}
						this.getView().getModel("TaxInwardDetails").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "TaxInwardDetails");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressNextPage: function () {
			this.getRouter().navTo("InwardSAC2");
		},

		Top10Suppliers: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getPrVsGstr2bData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "Top10Suppliers2bMain");
							this.getView().setModel(new JSONModel(data.resp.prVsGstr2b), "Top10Suppliers");
							this.Top10SuppliersValue();
						} else {
							this.getView().setModel(new JSONModel([]), "Top10Suppliers");
						}
						this.getView().getModel("Top10Suppliers").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "Top10Suppliers");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		Top10Suppliers1: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getPrVsGstr2aData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "Top10Suppliers2aMain");
							this.getView().setModel(new JSONModel(data.resp.prVsGstr2a), "Top10Suppliers");
							this.Top10Suppliers1Value();
						} else {
							this.getView().setModel(new JSONModel([]), "Top10Suppliers");
						}
						this.getView().getModel("Top10Suppliers").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "Top10Suppliers");
					}.bind(this));
			}.bind(this));
		},

		gstr12a: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getPurchaseRegisterVsGstr2b.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "GSTR12AVSPR2bMain");
							this.getView().setModel(new JSONModel(data.resp.purchaseRegisterVsGstr2b), "GSTR12AVSPR");
							this.gstr12aValue();
						} else {
							this.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						}
						this.getView().getModel("GSTR12AVSPR").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		gstr12a1: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getPurchaseRegisterVsGstr2a.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "GSTR12AVSPR2aMain");
							this.getView().setModel(new JSONModel(data.resp.purchaseRegisterVsGstr2a), "GSTR12AVSPR");
							this.gstr12a1Value();
						} else {
							this.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						}
						this.getView().getModel("GSTR12AVSPR").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "GSTR12AVSPR");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressBackPage: function () {
			var oModel = this.getOwnerComponent().getModel("GloblePayloadInward"),
				oSelectedKeyG = oModel.getProperty("/");

			oSelectedKeyG.period = this.getView().byId("idReturn").getSelectedKeys();
			oModel.refresh(true);
			this.getRouter().navTo("GDashboardInward1");
		},

		onDownloadGross: function () {
			var oVizFrame = this.getView().byId(this.flag3a ? "idVizFrame" : "idVizFrame3a");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Top 5 Suppliers"
				}
			});
			this.onSavePDF(oVizFrame, "Top 5 Suppliers");
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Top 5 Suppliers"
				}
			});
		},

		onDownloadMonth: function () {
			var evt = this.byId("idRdValue").getSelectedIndex(),
				text = (evt === 0 ? "GSTR-2B vs Purchase Register" : "GSTR-2A vs Purchase Register"),
				filename = (evt === 0 ? "GSTR-2B vs Purchase Register" : "GSTR-2A vs Purchase Register"),
				oVizFrame = this.getView().byId(evt === 0 ? "idVizFrame1" : "idVizFrame13a");

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
			switch (vIndex) {
			case 0:
				var vValue = "Supplier Count";
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
					text: "2A, 2B and PR Summary(" + vValue + ")"
				}
			});
			this.onSavePDF(oVizFrame, "2A, 2B and PR Summary(" + vValue + ")");
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "2A, 2B and PR Summary"
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

		_setVizProperty: function (idPopover, idViz, text, valueTxt, categoryTxt) {
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

			if (idViz === "id_iddonut") {
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
			if (valueTxt) {
				oVizFrame.setVizProperties({
					valueAxis: {
						title: {
							text: "Total Tax"
						}
					}
				});
			}
			if (categoryTxt) {
				oVizFrame.setVizProperties({
					categoryAxis: {
						title: {
							text: "Report Type"
						}
					}
				});
			}
			oPopOver.connect(oVizFrame.getVizUid());
		}
	});
});