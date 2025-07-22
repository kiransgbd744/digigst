sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/app/UserPermission",
	"sap/ui/util/Storage",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	'sap/ui/model/FilterOperator',
	'sap/ui/model/Filter'
], function (BaseController, UserPermission, Storage, JSONModel, MessageBox, FilterOperator, Filter) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GEInvoiceDashboard", {
		_bExpanded: true,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GEInvoiceDashboard
		 */
		onInit: function () {
			debugger;
			this.getOwnerComponent().getRouter().getRoute("GEInvoiceDashboard").attachPatternMatched(this._onRouteMatched, this);
			var entity = this.getOwnerComponent().getModel("entityAll").getData();
			this.getView().byId("idEntityEInvoice").setSelectedKeys(entity[1].entityId.toString());
		},
		_onRouteMatched: function (oEvent) {
			//this.onLoadDinomination();
			this.onLoadFY();
			//this.onLoadGSTIN();
			var oVizFramePie = this.oVizFrame = this.getView().byId("id_EinvoiceDist");
			var oVizFrameLine = this.oVizFrame = this.getView().byId("idVizFrameLine1");
			var oVizFrameLine2 = this.oVizFrame = this.getView().byId("idVizFrameLine2");
			oVizFramePie.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					drawingEffect: "glossy",
					colorPalette: ["#4CA887", "#F88C75", "#4793C8", "#EE536B"]
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
			var oPopOver1 = this.getView().byId("idPopOver1");
			oPopOver1.connect(oVizFramePie.getVizUid());
			oVizFrameLine.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#228B22", "#F88C75"]
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
			var oPopOver2 = this.getView().byId("idPopOver2");
			oPopOver2.connect(oVizFrameLine.getVizUid());
			oVizFrameLine2.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#4793C8", "#F20F0F", "#F88C75"]
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
			var oPopOver3 = this.getView().byId("idPopOver3");
			oPopOver3.connect(oVizFrameLine2.getVizUid());

			this.VizFrameBar1 = oVizFrameLine;
			this.VizFrameBar2 = oVizFrameLine2;
			var oRangeSlider = this.getView().byId("idVizSliderLine1");
			oRangeSlider.setValueAxisVisible(false);
			oRangeSlider.setShowPercentageLabel(false);
			oRangeSlider.setShowStartEndLabel(false);
			var oRangeSlider2 = this.getView().byId("idVizSliderLine2");
			oRangeSlider2.setValueAxisVisible(false);
			oRangeSlider2.setShowPercentageLabel(false);
			oRangeSlider2.setShowStartEndLabel(false);
		},

		selectEntity: function (oEvent) {
			this.selectAll(oEvent);
			this.onLoadGSTIN();
		},

		onLoadDinomination: function () {
			var aJson = [{
					"Key": "N",
					"Value": "None"
				}, {
					"Key": "K",
					"Value": "Thousands"
				}, {
					"Key": "L",
					"Value": "Lakhs"
				}, {
					"Key": "C",
					"Value": "Crore"
				}

			];
			this.getView().setModel(new JSONModel(aJson), "oDinomtion");

		},
		onAfterRendering: function () {
			var that = this;
			var oRangeSlider = this.getView().byId("idVizSliderLine1");
			oRangeSlider.setValueAxisVisible(false);
			oRangeSlider.setShowPercentageLabel(false);
			oRangeSlider.setShowStartEndLabel(false);
			oRangeSlider.attachRangeChanged(function (e) {
				var data = e.getParameters().data;
				var start = data.start.Date;
				var end = data.end.Date;
				var dateFilter = new Filter({
					path: "summaryDate",
					test: function (oValue) {
						var time = Date.parse(new Date(oValue));
						return (time >= start && time <= end);
					}
				});
				that.VizFrameBar1.getDataset().getBinding('data').filter([dateFilter]);
			});
			var oRangeSlider2 = this.getView().byId("idVizSliderLine2");
			oRangeSlider2.setValueAxisVisible(false);
			oRangeSlider2.setShowPercentageLabel(false);
			oRangeSlider2.setShowStartEndLabel(false);
			oRangeSlider2.attachRangeChanged(function (e) {
				var data = e.getParameters().data;
				var start = data.start.Date;
				var end = data.end.Date;
				var dateFilter = new Filter({
					path: "summaryDate",
					test: function (oValue) {
						var time = Date.parse(new Date(oValue));
						return (time >= start && time <= end);
					}
				});
				that.VizFrameBar2.getDataset().getBinding('data').filter([dateFilter]);
			});
			//this.onGoSelect();
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
							that.onLoadChartdata(that.vFyear);
						} else {
							that.vFyear = that.getView().getModel("oFyModel").getData().finYears[0].fy;
							that.onLoadChartdata(that.vFyear);
						}
						that.onLoadGSTIN();
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
			var that = this,
				aEntity = this.byId("idEntityEInvoice").getSelectedKeys(),
				aEntity = this.removeAll(aEntity);
			this.byId("idSupplier").setSelectedKeys(null);
			var PayLoad = {
				"req": {
					"entityId": aEntity
				}
			};
			var GstnPath = "/aspsapapi/getEinvSupGstins.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnPath,
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					debugger;
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
						that.byId("idSupplier").setSelectedKeys(oSelectedKey);
						that.onGoSelect();
					} else {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.getView().getModel("oGstinModel").refresh(true);
						that.onGoSelect();
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oGstinModel");
				that.getView().getModel("oGstinModel").refresh(true);
			});
		},
		// onDinomChange : function(){
		// 	var value		= this.getView().byId("idDimension").getSelectedKey();
		// 	var oBject = {
		// 		"Key"	: value
		// 	};
		// 	this.getView().setModel(new JSONModel(oBject),"oDmModel");
		// 	this.getView().getModel("oDmModel").refresh(true);
		// 	// this.Denomination1(value);
		// },
		onLoadChartdata: function (Fy) {
			var that = this;
			var oSelFy = Fy.split("-", 2);
			var year = Number(oSelFy[0]);
			var today = new Date();
			var currYr = today.getFullYear();
			if (currYr == year) {
				var minDate = new Date(currYr + "-" + "4" + "-" + "1");
				that.getView().byId("idDatefrom").setMinDate(minDate);
				that.getView().byId("idDatefrom").setMaxDate(today);
				that.getView().byId("idDatefrom").setDateValue(minDate);

				that.getView().byId("idDateTo").setMinDate(minDate);
				that.getView().byId("idDateTo").setMaxDate(today);
				that.getView().byId("idDateTo").setDateValue(today);
			} else {
				var minDate = new Date(year + "-" + "4" + "-" + "1");
				var maxDate = new Date(year + 1 + "-" + "3" + "-" + "31");
				that.getView().byId("idDatefrom").setMinDate(minDate);
				that.getView().byId("idDatefrom").setMaxDate(maxDate);
				that.getView().byId("idDatefrom").setDateValue(minDate);

				that.getView().byId("idDateTo").setMinDate(minDate);
				that.getView().byId("idDateTo").setMaxDate(maxDate);
				that.getView().byId("idDateTo").setDateValue(maxDate);
			}
			//this.onGoSelect();
		},
		onFyChange: function (oEvnt) {
			var that = this;
			var key = oEvnt.getSource().getSelectedKey();
			that.vFyear = key;
			var oSelFy = key.split("-", 2);
			var year = Number(oSelFy[0]);
			var today = new Date();
			var currYr = today.getFullYear();
			if (currYr == year) {
				var minDate = new Date(currYr + "-" + "4" + "-" + "1");
				that.getView().byId("idDatefrom").setMinDate(minDate);
				that.getView().byId("idDatefrom").setMaxDate(today);
				that.getView().byId("idDatefrom").setDateValue(minDate);

				that.getView().byId("idDateTo").setMinDate(minDate);
				that.getView().byId("idDateTo").setMaxDate(today);
				that.getView().byId("idDateTo").setDateValue(today);
			} else {
				var minDate = new Date(year + "-" + "4" + "-" + "1");
				var maxDate = new Date(year + 1 + "-" + "3" + "-" + "31");
				that.getView().byId("idDatefrom").setMinDate(minDate);
				that.getView().byId("idDatefrom").setMaxDate(maxDate);
				that.getView().byId("idDatefrom").setDateValue(minDate);

				that.getView().byId("idDateTo").setMinDate(minDate);
				that.getView().byId("idDateTo").setMaxDate(maxDate);
				that.getView().byId("idDateTo").setDateValue(maxDate);
			}
			this.onGoSelect();
		},
		handleChangeFromDate: function (oEvnt) {
			var fromDate = oEvnt.getSource().getDateValue();
			var toDate = this.getView().byId("idDateTo").getDateValue();
			if (fromDate > toDate) {
				this.getView().byId("idDateTo").setDateValue(fromDate);
			}
			this.getView().byId("idDateTo").setMinDate(fromDate);
		},
		onGoSelect: function () {
			// this.onDinomChange();
			this.ChartBind();
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
			debugger;
			//var vFy = this.getView().byId("idFinance").getSelectedKey();
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys();
			// var value		= this.getView().byId("idDimension").getSelectedKey();
			var fromDate = this.getView().byId("idDatefrom").getValue();
			var ToDate = this.getView().byId("idDateTo").getValue();
			// var oBject = {
			// 	"Key" : value 
			// };
			// this.getView().setModel(new JSONModel(oBject),"oDmModel");
			// this.getView().getModel("oDmModel").refresh(true);
			if (aGstin.length != 0) {
				if (aGstin[0] == "All") {
					aGstin.shift();
				}
			}
			if (aGstin.length === 0) {
				sap.m.MessageBox.warning("Please Select at least one Supplier Gstin");
				return;
			}
			var aPayload = {
				"req": {
					"supplierGstins": aGstin,
					"fromDate": fromDate,
					"toDate": ToDate
				}
			};
			var aPayload1 = {
				"req": {
					"supplierGstins": aGstin,
					"fromDate": fromDate,
					"toDate": ToDate,
					"entityId": parseInt($.sap.entityID)
				}
			};
			this.onGetEinvoiceStatus(aPayload);
			this.onGetgetEinvGenTrendForTotal(aPayload);
			this.getEinvGenTrendForError(aPayload);
			this.getEinvDistribution(aPayload);
			// this.getEinvSummary(aPayload1);
			this.getEinvHeaders(aPayload);
			this.getEinvAvgIrn(aPayload);
		},
		onGetEinvoiceStatus: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getEinvStatus.do";
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
						that.getView().setModel(new JSONModel(data.resp), "EinvoiceStatusModel");
						that.getView().getModel("EinvoiceStatusModel").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "EinvoiceStatusModel");
						that.getView().getModel("EinvoiceStatusModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "EinvoiceStatusModel");
				that.getView().getModel("EinvoiceStatusModel").refresh(true);
			});
		},
		onGetgetEinvGenTrendForTotal: function (aPayload) {
			var that = this;
			var oVizFrameLine = that.getView().byId("idVizFrameLine1");
			var oRangeSlider = this.getView().byId("idVizSliderLine1");
			var Gstr2APath = "/aspsapapi/getEinvGenTrendForTotal.do";
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
						var dataModel = new JSONModel(data.resp.einvGenTrends);
						oVizFrameLine.setModel(dataModel, "oEinvTrTotalModel");
						oVizFrameLine.getModel("oEinvTrTotalModel").refresh(true);
						oRangeSlider.setModel(dataModel, "oEinvTrTotalModel");
						oRangeSlider.getModel("oEinvTrTotalModel").refresh(true);
					} else {
						var dataModel = new JSONModel([]);
						oVizFrameLine.setModel(dataModel, "oEinvTrTotalModel");
						oVizFrameLine.getModel("oEinvTrTotalModel").refresh(true);
						oRangeSlider.setModel(dataModel, "oEinvTrTotalModel");
						oRangeSlider.getModel("oEinvTrTotalModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				oVizFrameLine.setModel(new JSONModel([]), "EinvoiceStatusModel");
				oVizFrameLine.getModel("EinvoiceStatusModel").refresh(true);
			});
		},
		getEinvGenTrendForError: function (aPayload) {
			var that = this;
			var oVizFrameLine = that.getView().byId("idVizFrameLine2");
			var oRangeSlider = this.getView().byId("idVizSliderLine2");
			var Gstr2APath = "/aspsapapi/getEinvGenTrendForError.do";
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
						var dataModel = new JSONModel(data.resp.einvGenTrendsError);
						oVizFrameLine.setModel(dataModel, "oEinvTrErrorModel");
						oVizFrameLine.getModel("oEinvTrErrorModel").refresh(true);
						oRangeSlider.setModel(dataModel, "oEinvTrErrorModel");
						oRangeSlider.getModel("oEinvTrErrorModel").refresh(true);
					} else {
						var dataModel = new JSONModel([]);
						oVizFrameLine.setModel(dataModel, "oEinvTrErrorModel");
						oVizFrameLine.getModel("oEinvTrErrorModel").refresh(true);
						oRangeSlider.setModel(dataModel, "oEinvTrErrorModel");
						oRangeSlider.getModel("oEinvTrErrorModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				oVizFrameLine.setModel(new JSONModel([]), "EinvoiceStatusModel");
				oVizFrameLine.getModel("EinvoiceStatusModel").refresh(true);
			});
		},
		getEinvDistribution: function (aPayload) {
			var that = this;
			var oVizFrameLine = that.getView().byId("id_EinvoiceDist");
			var Gstr2APath = "/aspsapapi/getEinvDistribution.do";
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
						var dataModel = new JSONModel(data.resp.einvDistribution);
						oVizFrameLine.setModel(dataModel, "oEinvDistModel");
						oVizFrameLine.getModel("oEinvDistModel").refresh(true);
					} else {
						var dataModel = new JSONModel([]);
						oVizFrameLine.setModel(dataModel, "oEinvDistModel");
						oVizFrameLine.getModel("oEinvDistModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				oVizFrameLine.setModel(new JSONModel([]), "oEinvDistModel");
				oVizFrameLine.getModel("oEinvDistModel").refresh(true);
			});
		},
		getEinvSummary: function (aPayload) {
			var that = this;
			var oVizFrameLine = that.getView().byId("idVizFrameColumn");
			var Gstr2APath = "/aspsapapi/getEinvSummary.do";
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
						var aJson = [];
						aJson.push(data.resp.einvSummary);
						var dataModel = new JSONModel(aJson);
						oVizFrameLine.setModel(dataModel, "oEinvSumModel");
						oVizFrameLine.getModel("oEinvSumModel").refresh(true);
					} else {
						var dataModel = new JSONModel([]);
						oVizFrameLine.setModel(dataModel, "oEinvSumModel");
						oVizFrameLine.getModel("oEinvSumModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				oVizFrameLine.setModel(new JSONModel([]), "oEinvSumModel");
				oVizFrameLine.getModel("oEinvSumModel").refresh(true);
			});
		},
		getEinvHeaders: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getEinvHeaders.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oEnvHdrdetails");
						that.getView().getModel("oEnvHdrdetails").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oEnvHdrdetails");
						that.getView().getModel("oEnvHdrdetails").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oEnvHdrdetails");
				that.getView().getModel("oEnvHdrdetails").refresh(true);
			});
		},
		getEinvAvgIrn: function (aPayload) {
			var that = this;
			var Gstr2APath = "/aspsapapi/getEinvAvgIrn.do";
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
						that.getView().setModel(new JSONModel(data.resp), "oEnvHdrdetails1");
						that.getView().getModel("oEnvHdrdetails1").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oEnvHdrdetails1");
						that.getView().getModel("oEnvHdrdetails1").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oEnvHdrdetails1");
				that.getView().getModel("oEnvHdrdetails1").refresh(true);
			});
		},
		onPressNextPage: function () {
			this.getRouter().navTo("SACDashboardOutward2");
		},
		onDownloadColumn: function () {
			var oVizFrame = this.getView().byId("idVizFrameColumn");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "E-invoice Summary"
				}
			});
			var filename = "E-invoice Summary";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "E-invoice Summary"
				}
			});
		},
		onDownloadPie: function () {
			var oVizFrame = this.getView().byId("id_EinvoiceDist");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "E-Invoice Distribution"
				}
			});
			var filename = "E-Invoice Distribution";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "E-Invoice Distribution"
				}
			});
		},
		onDownloadLine1: function () {
			var oVizFrame = this.getView().byId("idVizFrameLine1");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Total Calls and IRN Generated"
				}
			});
			var filename = "Total Calls and IRN Generated";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Total Calls and IRN Generated"
				}
			});
		},
		onDownloadLine2: function () {
			var oVizFrame = this.getView().byId("idVizFrameLine2");
			oVizFrame.setVizProperties({
				title: {
					visible: true,
					text: "Duplicate, Error and Cancelled"
				}
			});
			var filename = "Duplicate, Error and Cancelled";
			this.onSavePDF(oVizFrame, filename);
			oVizFrame.setVizProperties({
				title: {
					visible: false,
					text: "Duplicate, Error and Cancelled"
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
			oPDF.addImage(sImageData, "PNG", 15, 40, 180, 160);
			// oPDF.addImage(sImageData, "PNG", 10, 30, 160, 120);
			oPDF.save(val2);
		}

	});

});