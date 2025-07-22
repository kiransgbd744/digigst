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

	return BaseController.extend("com.ey.digigst.controller.EInvoiceDashboard", {
		_bExpanded: true,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.EInvoiceDashboard
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("EInvoiceDashboard").attachPatternMatched(this._onRouteMatched, this);
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("EInvoiceDashboard");

			this.onLoadFY();
			var oVizFramePie = this.oVizFrame = this.getView().byId("id_EinvoiceDist"),
				oVizFrameLine = this.oVizFrame = this.getView().byId("idVizFrameLine1"),
				oVizFrameLine2 = this.oVizFrame = this.getView().byId("idVizFrameLine2");

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
			}];
			this.getView().setModel(new JSONModel(aJson), "oDinomtion");
		},

		onAfterRendering: function () {
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
				this.VizFrameBar1.getDataset().getBinding('data').filter([dateFilter]);
			}.bind(this));

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
				this.VizFrameBar2.getDataset().getBinding('data').filter([dateFilter]);
			}.bind(this));
		},

		onLoadFY: function () {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getAllFy.do",
					contentType: "application/json",
				})
				.done(function (data, status, jqXHR) {
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
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel([]), "oFyModel");
					that.getView().getModel("oFyModel").refresh(true);
				});
		},

		onLoadGSTIN: function () {
			var that = this;
			var PayLoad = {
				"req": {
					"entityId": [$.sap.entityID]
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvSupGstins.do",
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (data.resp.gstins.length) {
							data.resp.gstins.unshift({
								"gstin": "All"
							});
						}
						that.getView().setModel(new JSONModel(data.resp), "oGstinModel");
						var oSelectedKey = data.resp.gstins.map(function (el) {
							return (el.gstin)
						});
						that.byId("idSupplier").setSelectedKeys(oSelectedKey);
						that.onGoSelect();
					} else {
						that.getView().setModel(new JSONModel([]), "oGstinModel");
						that.onGoSelect();
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel([]), "oGstinModel");
				});
		},

		onLoadChartdata: function (Fy) {
			var year = +Fy.split("-", 2)[0],
				today = new Date(),
				currYr = today.getFullYear();

			if (currYr == year) {
				var minDate = new Date(currYr + "-" + "4" + "-" + "1");
				this.getView().byId("idDatefrom").setMinDate(minDate);
				this.getView().byId("idDatefrom").setMaxDate(today);
				this.getView().byId("idDatefrom").setDateValue(minDate);

				this.getView().byId("idDateTo").setMinDate(minDate);
				this.getView().byId("idDateTo").setMaxDate(today);
				this.getView().byId("idDateTo").setDateValue(today);
			} else {
				var minDate = new Date(year + "-" + "4" + "-" + "1"),
					maxDate = new Date((year + 1) + "-" + "3" + "-" + "31");
				this.getView().byId("idDatefrom").setMinDate(minDate);
				this.getView().byId("idDatefrom").setMaxDate(maxDate);
				this.getView().byId("idDatefrom").setDateValue(minDate);

				this.getView().byId("idDateTo").setMinDate(minDate);
				this.getView().byId("idDateTo").setMaxDate(maxDate);
				this.getView().byId("idDateTo").setDateValue(maxDate);
			}
		},

		onFyChange: function (oEvnt) {
			var key = oEvnt.getSource().getSelectedKey();
			this.onLoadChartdata(key);
			this.onGoSelect();
		},

		handleChangeFromDate: function (oEvnt) {
			var fromDate = oEvnt.getSource().getDateValue(),
				toDate = this.getView().byId("idDateTo").getDateValue();

			if (fromDate > toDate) {
				this.getView().byId("idDateTo").setDateValue(fromDate);
			}
			this.getView().byId("idDateTo").setMinDate(fromDate);
		},

		onGoSelect: function () {
			this.ChartBind();
		},

		ChartBind: function () {
			var aGstin = this.getView().byId("idSupplier").getSelectedKeys(),
				fromDate = this.getView().byId("idDatefrom").getValue(),
				ToDate = this.getView().byId("idDateTo").getValue();

			if (!aGstin.length) {
				sap.m.MessageBox.warning("Please Select at least one Supplier Gstin");
				return;
			}
			if (aGstin[0] == "All") {
				aGstin.shift();
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
			this.getEinvHeaders(aPayload);
			this.getEinvAvgIrn(aPayload);
		},

		onGetEinvoiceStatus: function (aPayload) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvStatus.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "EinvoiceStatusModel");
					} else {
						that.getView().setModel(new JSONModel({
							"einvoiceStatus": []
						}), "EinvoiceStatusModel");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel({
						"einvoiceStatus": []
					}), "EinvoiceStatusModel");
				});
		},

		onGetgetEinvGenTrendForTotal: function (aPayload) {
			var oVizFrameLine = this.getView().byId("idVizFrameLine1"),
				oRangeSlider = this.getView().byId("idVizSliderLine1");

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvGenTrendForTotal.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var dataModel = new JSONModel(data.resp.einvGenTrends);
					} else {
						dataModel = new JSONModel([]);
					}
					oVizFrameLine.setModel(dataModel, "oEinvTrTotalModel");
					oVizFrameLine.getModel("oEinvTrTotalModel").refresh(true);
					oRangeSlider.setModel(dataModel, "oEinvTrTotalModel");
					oRangeSlider.getModel("oEinvTrTotalModel").refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oVizFrameLine.setModel(new JSONModel([]), "EinvoiceStatusModel");
					oVizFrameLine.getModel("EinvoiceStatusModel").refresh(true);
				});
		},

		getEinvGenTrendForError: function (aPayload) {
			var oVizFrameLine = this.getView().byId("idVizFrameLine2"),
				oRangeSlider = this.getView().byId("idVizSliderLine2");

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvGenTrendForError.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var dataModel = new JSONModel(data.resp.einvGenTrendsError);
					} else {
						dataModel = new JSONModel([]);
					}
					oVizFrameLine.setModel(dataModel, "oEinvTrErrorModel");
					oVizFrameLine.getModel("oEinvTrErrorModel").refresh(true);
					oRangeSlider.setModel(dataModel, "oEinvTrErrorModel");
					oRangeSlider.getModel("oEinvTrErrorModel").refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oVizFrameLine.setModel(new JSONModel([]), "EinvoiceStatusModel");
					oVizFrameLine.getModel("EinvoiceStatusModel").refresh(true);
				});
		},

		getEinvDistribution: function (aPayload) {
			var oVizFrameLine = this.getView().byId("id_EinvoiceDist");
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvDistribution.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var dataModel = new JSONModel(data.resp.einvDistribution);
					} else {
						dataModel = new JSONModel([]);
					}
					oVizFrameLine.setModel(dataModel, "oEinvDistModel");
					oVizFrameLine.getModel("oEinvDistModel").refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oVizFrameLine.setModel(new JSONModel([]), "oEinvDistModel");
					oVizFrameLine.getModel("oEinvDistModel").refresh(true);
				});
		},

		getEinvHeaders: function (aPayload) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvHeaders.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oEnvHdrdetails");
						that.getView().getModel("oEnvHdrdetails").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "oEnvHdrdetails");
						that.getView().getModel("oEnvHdrdetails").refresh(true);
					}

				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel([]), "oEnvHdrdetails");
					that.getView().getModel("oEnvHdrdetails").refresh(true);
				});
		},

		getEinvAvgIrn: function (aPayload) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvAvgIrn.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "oEnvHdrdetails1");
					} else {
						that.getView().setModel(new JSONModel([]), "oEnvHdrdetails1");
					}
					that.getView().getModel("oEnvHdrdetails1").refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel([]), "oEnvHdrdetails1");
					that.getView().getModel("oEnvHdrdetails1").refresh(true);
				});
		},

		onGstinChange: function (evt) {
			var aGstin = this.getView().getModel("oGstinModel").getData().gstins,
				vlength = aGstin.length,
				vkeys = evt.getSource().getSelectedKeys();
			if (vkeys.includes("All")) {
				var oSelectedKey = aGstin.map(function (el) {
					return el.gstin;
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

		getEinvSummary: function (aPayload) {
			var oVizFrameLine = this.getView().byId("idVizFrameColumn");
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEinvSummary.do",
					contentType: "application/json",
					data: JSON.stringify(aPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var aJson = [];
						aJson.push(data.resp.einvSummary);
						var dataModel = new JSONModel(aJson);
					} else {
						var dataModel = new JSONModel([]);
					}
					oVizFrameLine.setModel(dataModel, "oEinvSumModel");
					oVizFrameLine.getModel("oEinvSumModel").refresh(true);
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oVizFrameLine.setModel(new JSONModel([]), "oEinvSumModel");
					oVizFrameLine.getModel("oEinvSumModel").refresh(true);
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
			var oVizFrame = val1,
				sSVG = oVizFrame.exportToSVGString({
					width: 1200,
					height: 1200
				});
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
		},

		onExportTableToPDF: function () {
			var data = this.getView().getModel("EinvoiceStatusModel").getProperty("/einvoiceStatus") || [],
				headers = ["Date", "Generated", "Error", "Duplicate", "Cancelled", "Total"],
				doc = new jsPDF(),
				startX = 10,
				startY = 12,
				cellWidth = 32,
				cellHeight = 6,
				rows = 40;

			if (!data.length) {
				return;
			}
			var title = 'E-Invoice Status',
				titleWidth = doc.getTextWidth(title),
				titleX = (doc.internal.pageSize.getWidth() - titleWidth) / 2,
				aData = data.map(function (el) {
					return {
						"summaryDate": el.summaryDate,
						"generated": '' + el.generated,
						"error": '' + el.error,
						"duplicate": '' + el.duplicate,
						"cancelled": '' + el.cancelled,
						"total": '' + el.total
					}
				});

			aData.forEach(function (el, idx) {
				if (idx % rows === 0) {
					if (idx > 0) {
						doc.addPage();
					}
					startY = 12;
					doc.setFontSize(12);
					doc.setFont("helvetica", "bold");
					doc.text(title, titleX, startY);
					doc.setFontSize(9);
					startY += 6;
					headers.forEach(function (header, index) {
						doc.rect(startX + index * cellWidth, startY, cellWidth, cellHeight);
						doc.text(header, startX + index * cellWidth + 2, startY + 4);
					});
					doc.setFont("helvetica", "normal");
				}
				var y = (startY + (idx % rows + 1) * cellHeight);
				doc.rect(startX, y, cellWidth * headers.length, cellHeight);

				doc.rect(startX, y, cellWidth, cellHeight);
				doc.rect(startX + cellWidth, y, cellWidth, cellHeight);
				doc.rect(startX + cellWidth * 2, y, cellWidth, cellHeight);
				doc.rect(startX + cellWidth * 3, y, cellWidth, cellHeight);
				doc.rect(startX + cellWidth * 4, y, cellWidth, cellHeight);
				doc.rect(startX + cellWidth * 5, y, cellWidth, cellHeight);

				// Add text to each cell
				doc.text(el.summaryDate, startX + 2, y + 4);
				doc.text(el.generated, startX + cellWidth * 2 - doc.getTextWidth(el.generated) - 1, y + 4);
				doc.text(el.error, startX + cellWidth * 3 - doc.getTextWidth(el.error) - 1, y + 4);
				doc.text(el.duplicate, startX + cellWidth * 4 - doc.getTextWidth(el.duplicate) - 1, y + 4);
				doc.text(el.cancelled, startX + cellWidth * 5 - doc.getTextWidth(el.cancelled) - 1, y + 4);
				doc.text(el.total, startX + cellWidth * 6 - doc.getTextWidth(el.total) - 1, y + 4);
			});
			doc.save('E-invoice Status.pdf');
		}
	});
});