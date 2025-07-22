sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.SACLiabilityPayment", {
		_bExpanded: true,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.SACLiabilityPayment
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("SACLiabilityPayment").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("SACLiabilityPayment");

			sap.ui.core.BusyIndicator.show(0);
			this._loadDinomination();
			Promise.all([
					this._loadFiscalYear(),
					this._loadSupGstin()
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
					this.onLoadReturn(this.getView().getModel("oFyModel").getProperty("/finYears/0/fy"));
				}.bind(this), function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));

			var oVizFrameLine = this.oVizFrame = this.getView().byId("vfBiDashLine");
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

		_loadFiscalYear: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data.resp), "oFyModel");
						} else {
							this.getView().setModel(new JSONModel([]), "oFyModel");
						}
						this.getView().getModel("oFyModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						this.getView().setModel(new JSONModel([]), "oFyModel");
						this.getView().getModel("oFyModel").refresh(true);
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_loadSupGstin: function () {
			return new Promise(function (resolve, reject) {
				var PayLoad = {
					"req": {
						"entityId": [$.sap.entityID]
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSupGstins.do",
						contentType: "application/json",
						data: JSON.stringify(PayLoad)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							if (data.resp.gstins.length != 0) {
								data.resp.gstins.unshift({
									"gstin": "All"
								});
							}
							this.getView().setModel(new JSONModel(data.resp), "oGstinModel");
							this.getView().getModel("oGstinModel").refresh(true);
							var oSelectedKey = [];
							for (var i = 0; i < data.resp.gstins.length; i++) {
								oSelectedKey.push(data.resp.gstins[i].gstin);
							};
							this.byId("idSupplier").setSelectedKeys(oSelectedKey);
						} else {
							this.getView().setModel(new JSONModel([]), "oGstinModel");
							this.getView().getModel("oGstinModel").refresh(true);
						}
						resolve(data);
					}.bind(this))
					.fail(function (err) {
						this.getView().setModel(new JSONModel([]), "oGstinModel");
						this.getView().getModel("oGstinModel").refresh(true);
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onLoadReturn: function (fy) {
			if (fy) {
				var today = new Date(),
					aYear = fy.split("-"),
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
				this.byId("idReturn").setSelectedKeys(oSelectedKey);
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
			this.DenominationFormateValue();
		},

		DenominationFormateValue: function () {
			this.getMonthwiseTrendValue();
		},

		getMonthwiseTrendValue: function () {
			var oData1 = this.getView().getModel("oMonthTrendModelMain").getProperty("/resp"),
				oData = $.extend(true, {}, oData1);

			if (oData1) {
				oData.totalLiabList.forEach(function (e) {
					e.yAxis = this.DenominationNum(e.yAxis);
				}.bind(this));
				oData.liabItcList.forEach(function (e) {
					e.yAxis = this.DenominationNum(e.yAxis);
				}.bind(this));
				oData.liabCashList.forEach(function (e) {
					e.yAxis = this.DenominationNum(e.yAxis);
				}.bind(this));
			}

			var vIndex = this.getView().byId("idRdValue").getSelectedIndex();

			switch (vIndex) {
			case 0:
				oData.displayList = oData.totalLiabList;
				break;
			case 1:
				oData.displayList = oData.liabItcList;
				break;
			default:
				oData.displayList = oData.liabCashList;
			}

			this.getView().setModel(new JSONModel(oData), "oMonthTrendModel");
			this.getView().getModel("oMonthTrendModel").refresh(true);
		},

		onSelectTrendValue: function (oevent) {
			debugger;
			var vIndex = this.getView().byId("idRdValue").getSelectedIndex(),
				oData = this.getView().getModel("oMonthTrendModel").getData();

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
			var feedValueAxis = this.getView().byId('valueAxisFeed1');
			this.getView().byId("vfBiDashLine").removeFeed(feedValueAxis);
			if (vValue == "INVOICE_VALUE") {
				var oBject = {
					"value": ["Total liability"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAXABLE_VALUE") {
				var oBject = {
					"value": ["Liability paid through ITC"]
				};
				feedValueAxis.setValues(oBject.value);
			} else if (vValue == "TAX_AMOUNT") {
				var oBject = {
					"value": ["Liability paid through Cash"]
				};
				feedValueAxis.setValues(oBject.value);
			}
			this.getView().byId("vfBiDashLine").addFeed(feedValueAxis);

			if (vValue === "INVOICE_VALUE") {
				oData.displayList = oData.totalLiabList;
			} else if (vValue === "TAXABLE_VALUE") {
				oData.displayList = oData.liabItcList;
			} else if (vValue === "TAX_AMOUNT") {
				oData.displayList = oData.liabCashList;
			}

			this.getView().setModel(new JSONModel(oData), "oMonthTrendModel");
			this.getView().getModel("oMonthTrendModel").refresh(true);
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
			this.vFyear = oEvnt.getSource().getSelectedKey();
			this.onLoadReturn(this.vFyear);
		},

		onRetPeriodChange: function (evt) {
			var oData = this.getView().getModel("oRetPeriodModel").getProperty("/returnPeriods"),
				vkeys = evt.getSource().getSelectedKeys();
			if (vkeys.includes("All")) {
				var aFY = oData.map(function (e) {
					e.fy;
				})
				this.byId("idReturn").setSelectedKeys(aFY);
			} else {
				var vLength1 = oData.length - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idReturn").setSelectedKeys([]);
				}
			}
		},

		onGoSelect: function () {
			this.ChartBind();
		},

		ChartBind: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();
			if (!aGstin.length) {
				MessageBox.error("Please Select Supplier GSTIN");
				return;
			}
			if (!aRetPeriod.length) {
				MessageBox.error("Please Select Return Period");
				return;
			}
			if (!vFy) {
				vFy = this.getView().getModel("oFyModel").getProperty("/finYears/0/fy");
			}

			this.getView().setModel(new JSONModel({
				"Key": this.vSelectDenomKey
			}), "oDmModel");

			var payload = {
				"req": {
					"fy": vFy,
					"supplierGstins": this.removeAll(aGstin),
					"returnPeriods": this.removeAll(aRetPeriod)
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getMonthwiseTrend(payload),
					this._getTaxLiabilityDetails(payload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getMonthwiseTrend: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr3bHeaderGraphData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							var oData = $.extend(true, {}, data);
							this.getView().setModel(new JSONModel(oData), "oMonthTrendModelMain");
							this.getView().setModel(new JSONModel(data.resp), "oMonthTrendModel");
							this.getMonthwiseTrendValue();
						} else {
							this.getView().setModel(new JSONModel([]), "oMonthTrendModel");
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

		_getTaxLiabilityDetails: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr3bTableData.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "S") {
							this.getView().setModel(new JSONModel(data), "oTaxLiabDetailsModel");
						} else {
							this.getView().setModel(new JSONModel([]), "oTaxLiabDetailsModel");
						}
						this.getView().getModel("oTaxLiabDetailsModel").refresh(true);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(), "oTaxLiabDetailsModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onGstinChange: function (evt) {
			var oData = this.getView().getModel("oGstinModel").getProperty("/gstins"),
				vkeys = evt.getSource().getSelectedKeys();

			if (vkeys.includes("All")) {
				var aGstin = oData.map(function (e) {
					return e.gstin;
				});
				this.byId("idSupplier").setSelectedKeys(aGstin);
			} else {
				var vLength1 = oData.length - 1;
				if (vLength1 == vkeys.length) {
					this.byId("idSupplier").setSelectedKeys([]);
				}
			}
			// this.ChartBind();
		},

		onDownloadMonthe: function () {
			var vFy = this.getView().byId("idFinance").getSelectedKey(),
				aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				aRetPeriod = this.getView().byId("idReturn").getSelectedKeys();

			if (!aGstin.length) {
				MessageBox.error("Please Select Suppler Gstin");
				return;
			}
			if (!aRetPeriod.length) {
				MessageBox.error("Please Select Return Period");
				return;
			}
			var payload = {
				"req": {
					"fy": vFy,
					"supplierGstins": this.removeAll(aGstin),
					"returnPeriods": this.removeAll(aRetPeriod)
				}
			};
			this.reportDownload(payload, "/aspsapapi/asyncgstr3BOutwrdDashbrdReport.do");
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

		onDownloadMonth1: function () {
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
		}
	});
});