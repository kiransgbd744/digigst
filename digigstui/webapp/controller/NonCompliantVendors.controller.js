sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (Controller, BaseController, JSONModel, Formatter, MessageBox, MessageToast, Filter, FilterOperator) {
	"use strict";
	var vCount = 10;
	return BaseController.extend("com.ey.digigst.controller.NonCompliantVendors", {
		formatter: Formatter,

		onInit: function () {
			// this.getOwnerComponent().getRouter().getRoute("NonCompliantVendors").attachPatternMatched(this._onRouteMatched, this);
			//var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			//this.getView().setModel(oModel, "resp");
			this.getView().byId("id_NonCompalintPg").setVisible(true);
			this.getView().byId("id_EmailPg").setVisible(false);
			this.VMGSTINarr = [];
			this.getView().setModel(new JSONModel([]), "oVendDetailModel");
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._resetDialogSelectAll();

				this.byId("FYid").setText(this.byId("idFiYear").getSelectedKey());
				this.getView().byId("id_NonCompalintPg").setVisible(true);
				this.getView().byId("id_EmailPg").setVisible(false);
				this._bindScreenProperty();
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.onLoadFy(),
						this.vendorPAN()
					])
					.then(function (values) {
						this.onPressGo();
					}.bind(this))
					.catch(function (error) {
						sap.ui.core.BusyIndicator.hide();
						console.log(error.message);
					}.bind(this));
				// this.apiLimt();
			}
		},
		clearVendorData: function () {
			debugger;

			if (this.VendorPan) {
				this.getView().removeDependent(this.VendorPan);
				this.VendorPan.destroy();
				this.VendorPan = null;
			}
			
			if (this.VendorPanE) {
				this.getView().removeDependent(this.VendorPanE);
				this.VendorPanE.destroy();
				this.VendorPanE = null;
			}
		},

		apiLimt: function () {
			//this.financialYear = postData.req.financialYear
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getLimitAndUsageCount.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					//data = JSON.parse(data);
					this.getView().setModel(new JSONModel(data.resp), "ApiLimit");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("complienceHistory : Error");
					this.getView().setModel(new JSONModel(null), "ComplienceHistory");
				}.bind(this));
		},

		_bindScreenProperty: function () {
			var oPrProperty = {
				"summaryFullScreen": false
			};
			this.getView().setModel(new JSONModel(oPrProperty), "PrProperty");
		},

		//=============== Fy Model Binding ===========================//
		onLoadFy: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel((data.hdr.status === "S" ? data.resp : [])), "oFyModel");
						if (data.hdr.status === "S") {
							// this.EntityTabBind();
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "oFyModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		vendorPAN: function () {
			// if (!this.VendorPan) {
			// 	this.VendorPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorPANF4", this);
			// 	this.getView().addDependent(this.VendorPan);
			// }

			if (!$.sap.entityID) {
				return;
			}
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						// url: "/aspsapapi/getVendorPanDetails.do", //=== commented by chaithra on 23/03/2021
						url: "/aspsapapi/getNonCompVendorPan.do",
						contentType: "application/json",
						data: JSON.stringify({
							"req": {
								"entityId": [$.sap.entityID]
							}
						})
					})
					.done(function (data, status, jqXHR) {
						var oData = (data.resp !== "No Data found" ? data.resp.vendorPans : []),
							oModel = new JSONModel(oData);
						oModel.setSizeLimit(oData.length);
						this.getView().setModel(oModel, "venPANDataMain");
						this.getView().setModel(new JSONModel([]), "vendorGSTIN");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onGstr2FullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();
			if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("idCcSummary").setFullScreen(true);
				this.byId("id_NonVendComTab").setVisibleRowCount(26);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("idCcSummary").setFullScreen(false);
				this.byId("id_NonVendComTab").setVisibleRowCount(14);

			}
			oPropModel.refresh(true);
		},

		onPAN: function () {
			if (!this.VenGSTIN) {
				this.VenGSTIN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorGSTINF4", this);
				this.getView().addDependent(this.VenGSTIN);
			}
			if (!this.aVenPan.length) {
				this.getView().setModel(new JSONModel([]), "vendorGSTIN");
				return;
			}
			var searchInfo = {
				"req": {
					"vendorPan": this.aVenPan,
					"entityId": $.sap.entityID
				}
			};
			$.ajax({
					method: "POST",
					// url: "/aspsapapi/getVendorGstin.do", //========= commited by chaithra on 23/03/2021 
					url: "/aspsapapi/getNonCompalintVGstin.do",
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oData = (data.resp !== "No Data found" ? data.resp.vendorGstins : []),
						oModel = new JSONModel(oData);
					oModel.setSizeLimit(oData.length);
					this.getView().setModel(oModel, "vendorGSTIN");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressGo: function () {
			var idx = (this.byId("idFiYear").getSelectedIndex() > -1 ? this.byId("idFiYear").getSelectedIndex() : 0),
				fy = this.getModel("oFyModel").getProperty("/finYears/" + idx + "/key"),
				fiscalYear = this.getModel("oFyModel").getProperty("/finYears/" + idx + "/fy");

			this.byId("FYid").setText(fiscalYear);
			if (fy === "2021") {
				this.byId("col1").setVisible(true);
				this.byId("col2").setVisible(true);
				this.byId("col3").setVisible(false);
				this.byId("col4").setVisible(false);
				this.byId("col5").setVisible(false);
				this.byId("col6").setVisible(true);
			} else if (+(fy) < 2021) {
				this.byId("col1").setVisible(true);
				this.byId("col2").setVisible(true);
				this.byId("col3").setVisible(true);
				this.byId("col4").setVisible(true);
				this.byId("col5").setVisible(false);
				this.byId("col6").setVisible(false);
			} else {
				this.byId("col1").setVisible(false);
				this.byId("col2").setVisible(false);
				this.byId("col3").setVisible(false);
				this.byId("col4").setVisible(false);
				this.byId("col5").setVisible(true);
				this.byId("col6").setVisible(true);
			}

			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 10
				},
				"req": {
					"entityId": $.sap.entityID,
					"vendorPan": this.removeAll(this.aVenPan),
					"vendorGstin": this.removeAll(this.aVenGstin),
					"financialYear": fiscalYear,
					"returnType": this.byId("idRetType").getSelectedKey(),
					"reportType": this.byId("idViewType").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getVendorReturnFilingData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.apiLimt();
					this._nicPagination(data.hdr);
					var odata = data.resp.overallFilingStatusDtos,
						resp = data.resp;
					this.byId("statusId").setText(resp.modifedOn ? (resp.status + ": " + resp.modifedOn) : null);
					this.byId("statsFreq").setText(resp.retFrequencyTime ? (resp.retFrequencyStatus + ": " + resp.retFrequencyTime) : "");
					if (odata !== undefined) {
						var arry = [];
						for (var i = 0; i < odata.length; i++) {
							var L1 = odata[i];
							for (var a = 0; a < odata[i].overAllPanStatus.length; a++) {
								var all = odata[i].overAllPanStatus[a],
									month = all.month;
								if (month.slice(0, 2) === "04") {
									L1.AprilStatus = all.staus;
								}
								if (month.slice(0, 2) === "05") {
									L1.MayStatus = all.staus;
								}
								if (month.slice(0, 2) === "06") {
									L1.JuneStatus = all.staus;
								}
								if (month.slice(0, 2) === "07") {
									L1.JulyStatus = all.staus;
								}
								if (month.slice(0, 2) === "08") {
									L1.AugStatus = all.staus;
								}
								if (month.slice(0, 2) === "09") {
									L1.SepStatus = all.staus;
								}
								if (month.slice(0, 2) === "10") {
									L1.OctStatus = all.staus;
								}
								if (month.slice(0, 2) === "11") {
									L1.NovStatus = all.staus;
								}
								if (month.slice(0, 2) === "12") {
									L1.DecStatus = all.staus;
								}
								if (month.slice(0, 2) === "01") {
									L1.JanStatus = all.staus;
								}
								if (month.slice(0, 2) === "02") {
									L1.FebStatus = all.staus;
								}
								if (month.slice(0, 2) === "03") {
									L1.MarStatus = all.staus;
								}
								if (month.slice(0, 2) === "13") {
									L1.APRJUNStatus = all.staus;
								}
								if (month.slice(0, 2) === "14") {
									L1.JULSEPStatus = all.staus;
								}
								if (month.slice(0, 2) === "15") {
									L1.OCTDECStatus = all.staus;
								}
								if (month.slice(0, 2) === "16") {
									L1.JANMARStatus = all.staus;
								}
								if (month.slice(0, 2) === "17") {
									L1.AprSepStatus = all.staus;
								}
								if (month.slice(0, 2) === "18") {
									L1.OctMarStatus = all.staus;
								}
							}
							if (odata[i].gstinWiseFilingStatusMonthwise !== undefined) {
								for (var j = 0; j < odata[i].gstinWiseFilingStatusMonthwise.length; j++) {
									var L2 = odata[i].gstinWiseFilingStatusMonthwise[j];
									for (var k = 0; k < odata[i].gstinWiseFilingStatusMonthwise[j].eachGstinwiseStatusCombination.length; k++) {
										var L3 = odata[i].gstinWiseFilingStatusMonthwise[j].eachGstinwiseStatusCombination[k],
											timep = L3.month;
										if (timep.slice(0, 2) === "04") {
											L2.AprilStatus = L3.status;
										}
										if (timep.slice(0, 2) === "05") {
											L2.MayStatus = L3.status;
										}
										if (timep.slice(0, 2) === "06") {
											L2.JuneStatus = L3.status;
										}
										if (timep.slice(0, 2) === "07") {
											L2.JulyStatus = L3.status;
										}
										if (timep.slice(0, 2) === "08") {
											L2.AugStatus = L3.status;
										}
										if (timep.slice(0, 2) === "09") {
											L2.SepStatus = L3.status;
										}
										if (timep.slice(0, 2) === "10") {
											L2.OctStatus = L3.status;
										}
										if (timep.slice(0, 2) === "11") {
											L2.NovStatus = L3.status;
										}
										if (timep.slice(0, 2) === "12") {
											L2.DecStatus = L3.status;
										}
										if (timep.slice(0, 2) === "01") {
											L2.JanStatus = L3.status;
										}
										if (timep.slice(0, 2) === "02") {
											L2.FebStatus = L3.status;
										}
										if (timep.slice(0, 2) === "03") {
											L2.MarStatus = L3.status;
										}
										if (timep.slice(0, 2) === "13") {
											L2.APRJUNStatus = L3.status;
										}
										if (timep.slice(0, 2) === "14") {
											L2.JULSEPStatus = L3.status;
										}
										if (timep.slice(0, 2) === "15") {
											L2.OCTDECStatus = L3.status;
										}
										if (timep.slice(0, 2) === "16") {
											L2.JANMARStatus = L3.status;
										}
										if (timep.slice(0, 2) === "17") {
											L2.AprSepStatus = L3.status;
										}
										if (timep.slice(0, 2) === "18") {
											L2.OctMarStatus = L3.status;
										}
									}
								}
							}
							arry.push(L1);
						}

						for (var n = 0; n < arry.length; n++) {
							if (arry[n].gstinWiseFilingStatusMonthwise !== undefined) {
								for (var ab = 0; ab < arry[n].gstinWiseFilingStatusMonthwise.length; ab++) {
									delete arry[n].gstinWiseFilingStatusMonthwise[ab].eachGstinwiseStatusCombination;
								}
							}
						}
						this.getView().setModel(new JSONModel(arry), "NCV");
					} else {
						this.getView().setModel(new JSONModel([]), "NCV");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onMenuItemPress: function (oEvt) {
			var key = oEvt.getParameter("item").getKey(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"financialYear": this.byId("idFiYear").getSelectedKey(),
						"reportType": key
					}
				};
			if (key === "tableData") {
				this.downloadTableData();
				return;
			}
			this.reportDownload(payload, "/aspsapapi/getNonVendorReport.do");
		},

		downloadTableData: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"financialYear": this.byId("idFiYear").getSelectedKey(),
					"returnType": this.byId("idRetType").getSelectedKey(),
					"reportType": this.byId("idViewType").getSelectedKey()
				}
			};
			this.excelDownload(payload, "/aspsapapi/getNonVendorTableReport.do");
		},

		onInitiateReport: function () {
			if (this.aVenPan.length === 0) {
				this.onInitiateReport2();
			} else {
				var that = this;
				var FY = this.byId("idFiYear").getSelectedKey();
				MessageBox.alert('Do you want to initiate Vendor compliance report for FY' + FY + '?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onInitiateReport1();
						}
					}
				});
			}

		},

		onInitiateReport1: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"financialYear": this.byId("idFiYear").getSelectedKey(),
					"vendorGstins": this.removeAll(this.aVenGstin),
					"vendorPans": this.removeAll(this.aVenPan),
					"complianceType": "VendorCompliance"
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initateGetVendorFilingStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.apiLimt();
					var resp = data.resp;
					if (data.hdr.status === "S") {
						this.byId("statusId").setText(resp.status + ": " + resp.initiatedOn);
						MessageBox.success("Successfully Initiated");
					} else {
						MessageBox.error(resp.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.apiLimt();
				}.bind(this));
		},

		onInitiateReport2: function () {
			if (!this._dComplianceCall) {
				this._dComplianceCall = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.ComplianceCall", this);
				this.getView().addDependent(this._dComplianceCall);
			}
			this._dComplianceCall.open();
		},
		onCloseComplianceCall: function (action) {
			if (action === "I") {
				var days = this.byId("slCompCallDays").getSelectedKey(),
					payload = {
						"req": {
							"financialYear": this.byId("idFiYear").getSelectedKey(),
							"noOfDays": days
						}
					};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						url: "/aspsapapi/initiateNonComVendorFilingByDays.do",
						method: "POST",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							sap.m.MessageBox.success(data.resp.status, {
								styleClass: "sapUiSizeCompact"
							});
						} else {
							sap.m.MessageBox.error(data.resp.message, {
								styleClass: "sapUiSizeCompact"
							});
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
			this._dComplianceCall.close();
		},

		onGetFilingFrequency: function () {
			if (!this._dFrequencyCall) {
				this._dFrequencyCall = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.FrequencyCall", this);
				this.getView().addDependent(this._dFrequencyCall);
			}
			this._dFrequencyCall.open();
		},

		onCloseFrequencyCall: function (action) {
			if (action === "I") {
				var days = this.byId("slFreCallDays").getSelectedKey(),
					payload = {
						"req": {
							"financialYear": this.byId("idFiYear").getSelectedKey(),
							"complianceType": "NonComplianceVendor",
							"entityId": "" + $.sap.entityID,
							"noOfDays": days
						}
					};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getFilingFrequency.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							this.byId("statusId").setText(data.resp.status + ": " + data.resp.freqTime);
							MessageBox.success(data.resp.message);
						} else {
							MessageBox.error(data.resp.message);
						}
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));

			}
			this._dFrequencyCall.close();
		},

		// onGetFilingFrequency: function () {
		// 	var payload = {
		// 		"req": {
		// 			"entityId": $.sap.entityID,
		// 			"financialYear": this.byId("idFiYear").getSelectedKey(),
		// 			"complianceType": "NonComplianceVendor"
		// 		}
		// 	};
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getFilingFrequency.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(payload)
		// 		})
		// 		.done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			if (data.hdr.status === "S") {
		// 				this.byId("statusId").setText(data.resp.status + ": " + data.resp.freqTime);
		// 				MessageBox.success(data.resp.message);
		// 			} else {
		// 				MessageBox.error(data.resp.message);
		// 			}
		// 		}.bind(this))
		// 		.fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 		}.bind(this));
		// },

		onnonDetFullScreen: function (oEvt) {
			var tab = this.byId("idRetType").getSelectedKey(),
				id;
			if (tab === "ITC04") {
				id = "ITC04id";
			} else if (tab === "CMP08") {
				id = "CMP08id";
			} else {
				id = "table1";
			}
			if (oEvt === "open") {
				this.byId("closebutnon").setVisible(true);
				this.byId("openbutnon").setVisible(false);
				this.byId("nonId").setFullScreen(true);
				this.byId(id).setVisibleRowCount(20);
			} else {
				this.byId("closebutnon").setVisible(false);
				this.byId("openbutnon").setVisible(true);
				this.byId("nonId").setFullScreen(false);
				this.byId(id).setVisibleRowCount(8);
			}
		},

		onExpand2B: function (oEvent) {
			var tab = this.byId("idRetType").getSelectedKey(),
				id;
			if (tab === "ITC04") {
				id = "ITC04id";
			} else if (tab === "CMP08") {
				id = "CMP08id";
			} else if (tab === "GSTR9" || tab === "GSTR9A" || tab === "GSTR4") {
				id = "Gstr9id";
			} else {
				id = "table1";
			}
			if (oEvent.getSource().getId().includes("expand2B")) {
				this.byId(id).expandToLevel(1);
			} else {
				this.byId(id).collapseAll();
			}
		},

		onReturnTypeChange: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			var fy = this.getModel("oFyModel").getData().finYears[this.byId("idFiYear").getSelectedIndex()].key;
			if (key === "ITC04") {
				this.byId("table1").setVisible(false);
				this.byId("ITC04id").setVisible(true);
				this.byId("Gstr9id").setVisible(false);
				this.byId("CMP08id").setVisible(false);
				if (fy === "2021") {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(true);
				} else if (Number(fy) < 2021) {
					this.byId("col1").setVisible(true);
					this.byId("col2").setVisible(true);
					this.byId("col3").setVisible(true);
					this.byId("col4").setVisible(true);
					this.byId("col5").setVisible(false);
					this.byId("col6").setVisible(false);
				} else {
					this.byId("col1").setVisible(false);
					this.byId("col2").setVisible(false);
					this.byId("col3").setVisible(false);
					this.byId("col4").setVisible(false);
					this.byId("col5").setVisible(true);
					this.byId("col6").setVisible(true);
				}
			} else if (key === "GSTR9" || key === "GSTR9A" || key === "GSTR4") {
				this.byId("table1").setVisible(false);
				this.byId("ITC04id").setVisible(false);
				this.byId("Gstr9id").setVisible(true);
				this.byId("CMP08id").setVisible(false);
			} else if (key === "CMP08") {
				this.byId("table1").setVisible(false);
				this.byId("ITC04id").setVisible(false);
				this.byId("Gstr9id").setVisible(false);
				this.byId("CMP08id").setVisible(true);
			} else {
				this.byId("table1").setVisible(true);
				this.byId("ITC04id").setVisible(false);
				this.byId("Gstr9id").setVisible(false);
				this.byId("CMP08id").setVisible(false);
			}
			this.onPressGo();
		},
		//############################ Email #############################//
		//============= Code added by chaithra on 08/03/2021 =============//
		onEmail: function () {
			var that = this;
			this.getView().byId("id_EmailPg").setVisible(true);
			this.getView().byId("id_NonCompalintPg").setVisible(false);
			var data = [];
			/*,
				data1 = [],
				data2 = [],
				data3 = [],
				data4 = [];*/
			that.getView().setModel(new JSONModel(data), "oVendDetailModel");
			this.onEmailPan();
			/*that.getView().byId("idEmailVenPAN").setModel(new JSONModel(data1), "oVendorPanModel");
			that.getView().byId("idEmailVenGstn").setModel(new JSONModel(data2), "oVendorGSTNModel");
			that.getView().byId("idEmailVenName").setModel(new JSONModel(data3), "oVendNameModel");
			that.getView().byId("idEmailVenCode").setModel(new JSONModel(data4), "oVendCodeModel");*/
			// $.sap.entityID = 32;
			//============ Vendor PAN api ===============//
		},

		onEmailPan: function () {
			// if (!this.VendorPanE) {
			// 	this.VendorPanE = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorEPANF4", this);
			// 	this.getView().addDependent(this.VendorPanE);
			// }
			var payload = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getNonCompComVPan.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.apiLimt();
					var oData = (data.resp !== "No Data found" ? data.resp.vendorPans : []),
						oModel = new JSONModel(oData);
					oModel.setSizeLimit(oData.length);
					this.getView().setModel(oModel, "oVendorPanModel");
					this.getView().setModel(new JSONModel([]), "oVendorGSTNModel");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.apiLimt();
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
			this.onLoadNonCompTable();
		},
		//================= Vendor GSTN api call ====================//
		onLoadVendorGSTN: function () {
			debugger;
			if (!this.VenGSTINE) {
				this.VenGSTINE = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorEGSTINF4", this);
				this.getView().addDependent(this.VenGSTINE);
			}
			if (!this.PanarrE.length) {
				this.getView().setModel(new JSONModel([]), "oVendorGSTNModel");
				return;
			}
			if (this.PanarrE.length !== 0) {
				var oVenPANPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorPan": this.removeAll(this.PanarrE)
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getNonCompComVGstin.do",
						contentType: "application/json",
						data: JSON.stringify(oVenPANPayload)
					})
					.done(function (data, status, jqXHR) {
						this.apiLimt();
						sap.ui.core.BusyIndicator.hide();
						var oData = (data.resp !== "No Data found" ? data.resp.vendorGstins : []),
							oModel = new JSONModel(oData);
						oModel.setSizeLimit(oData.length);
						this.getView().setModel(oModel, "oVendorGSTNModel");
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.apiLimt();
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		//================ Vendor Name api====================//
		onLoadVendorName: function () {
			var that = this;
			var oSelVenPAN = this.byId("idEmailVenGstn").getSelectedItems();
			if (oSelVenPAN.length !== 0) {
				var oSelVenPanArr1 = [];
				if (oSelVenPAN.includes("All")) {
					oSelVenPanArr1 = that.getView().byId("idEmailVenGstn").getModel("oVendorGSTNModel").getData().resp.vendorGstins;
				} else {
					for (var i = 0; i < oSelVenPAN.length; i++) {
						oSelVenPanArr1.push(oSelVenPAN[i].getKey());
					}
				}

				var Url2 = "/aspsapapi/getNonCompVName.do";
				var payload2 = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstIn": this.removeAll(oSelVenPanArr1)
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: Url2,
						contentType: "application/json",
						data: JSON.stringify(payload2)
					})
					.done(function (data, status, jqXHR) {
						that.apiLimt();
						sap.ui.core.BusyIndicator.hide();
						data.resp.unshift({
							vendorName: "All"
						});
						that.getView().byId("idEmailVenName").getModel("oVendNameModel").setData(data);
						that.getView().byId("idEmailVenName").getModel("oVendNameModel").refresh();
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						that.apiLimt();
					});
			}
		},
		//====================== Vendor Code Api=================//
		onLoadVendorCode: function () {
			var that = this;
			var oSelVenPAN = this.byId("idEmailVenGstn").getSelectedItems();
			var oSelVenPAN1 = this.byId("idEmailVenName").getSelectedItems();
			var oSelVenPanArr1 = [];
			var oSelVenPanArr2 = [];
			if (oSelVenPAN.includes("All")) {
				oSelVenPanArr1 = that.getView().byId("idEmailVenGstn").getModel("oVendorGSTNModel").getData().resp.vendorGstins;
			} else {
				for (var i = 0; i < oSelVenPAN.length; i++) {
					oSelVenPanArr1.push(oSelVenPAN[i].getKey());
				}
			}
			if (oSelVenPAN1.includes("All")) {
				oSelVenPanArr2 = that.getView().byId("idEmailVenName").getModel("oVendNameModel").getData().resp;
			} else {
				for (var i = 0; i < oSelVenPAN1.length; i++) {
					oSelVenPanArr2.push(oSelVenPAN1[i].getKey());
				}
			}
			var Url3 = "/aspsapapi/getNonCompVendorCode.do";
			var payload3 = {
				"req": {
					"entityId": $.sap.entityID,
					"vendorGstIn": this.removeAll(oSelVenPanArr1),
					"vendorName": this.removeAll(oSelVenPanArr2)
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: Url3,
					contentType: "application/json",
					data: JSON.stringify(payload3)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
					data.resp.unshift({
						vendorCode: "All"
					});
					that.getView().byId("idEmailVenCode").getModel("oVendCodeModel").setData(data);
					that.getView().byId("idEmailVenCode").getModel("oVendCodeModel").refresh();
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
				});
		},
		onPressEmailGenReport: function () {
			var that = this;
			var FY = this.byId("idFiYear").getSelectedKey();
			var RetT = this.byId("idRetType").getSelectedKey();
			//var oSelVenPAN = this.byId("idEmailVenGstn").getSelectedItems();
			//var oSelNonVenPAN = this.byId("idEmailVenPAN").getSelectedItems();
			//this.aVenGstin = [];
			//this.aVenPan = [];
			if (this.PanarrE.length == 0 && this.GSTINarrE.length == 0) {
				MessageBox.error("Please Select Vendor PAN and Vendor GSTIN");
				return;
			}
			if (this.PanarrE.length == 0 && this.GSTINarrE.length !== 0) {
				MessageBox.error("Please Select Vendor PAN ");
				return;
			}
			if (this.PanarrE.length !== 0 && this.GSTINarrE.length == 0) {
				MessageBox.error("Please Select  Vendor GSTIN");
				return;
			}
			if (this.GSTINarrE.length !== 0) {
				var aGsin = [];
				for (var i = 0; i < this.GSTINarrE.length; i++) {
					aGsin.push({
						"gstin": this.GSTINarrE[i]
					});
				}
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstins": aGsin,
						"financialYear": FY,
						"returnType": RetT
					}
				};
				var Url = "/aspsapapi/generateNonCompVendorReq.do";
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: Url,
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						that.apiLimt();
						if (data.hdr.status == "S") {
							that.onLoadNonCompTable();
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						that.apiLimt();
					});
			};
		},
		onLoadNonCompTable: function () {
			var that = this;
			var payload = {};
			var Url = "/aspsapapi/getNonCompVendorReqDetails.do";
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: Url,
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// var data = {
					//     "hdr": {
					//         "status": "S"
					//     },
					//     "resp": {
					//         "NonCompVendorData": [
					//             {
					//                 "requestId": 33,
					//                 "noOfVendorGstins": 7,
					//                 "createdOn": "19-03-2021 18:41:23",
					//                 "status": "REPORT_GENERATED",
					//                 "totalEmails": 0,
					//                 "sentEmails": 0,
					//                 "financialYear": "2020-21"
					//             }
					//         ]
					//     }
					// };
					that.apiLimt();
					that.getView().getModel("oVendDetailModel").setData(data);
					that.getView().getModel("oVendDetailModel").refresh();
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					that.apiLimt();
				});
		},
		//================= Download zipped report =============//
		onPressNonCompDownload: function (oEvent) {
			var obj = oEvent.getSource().getParent().getBindingContext("oVendDetailModel").getObject();
			var oReqExcelPath = "/aspsapapi/downloadNonCompVendorReport.do?requestId=" + obj.requestId + "";
			window.open(oReqExcelPath);
		},
		RecGstinPress: function (oEvent) {
			var gstins = [];
			var gstins = oEvent.getSource().getParent().getBindingContext("oVendDetailModel").getObject().vendorGstins;
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins);
			var oButton = oEvent.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},
		onPressEmailBack: function () {
			this.getView().byId("id_EmailPg").setVisible(false);
			this.getView().byId("id_NonCompalintPg").setVisible(true);
		},
		onEmailReqIdPress: function (oEvent) {
			var that = this;
			var obj = oEvent.getSource().getParent().getBindingContext("oVendDetailModel").getObject();
			if (!this._oDialogRefund) {
				this._oDialogRefund = sap.ui.xmlfragment("com.ey.digigst.fragments.others.NonComplaintEmail", this);
				this.getView().addDependent(this._oDialogRefund);
			}
			this._oDialogRefund.open();
			sap.ui.getCore().byId("subjectId").setText("Subject: " + $.sap.entityName + " - Non Compliant GST Returns");
			sap.ui.getCore().byId("textId").setValue(
				"Dear Sir/Madam,\n\n We have checked GST Return filing status of your GSTIN and noticed that, few returns are not filed for respective tax period. Please download the Non-Compliant returns list through the below link.\n\n https://eyft.eyasp.in/aspwebemail/sap/otpValidation?reqId=XXXXXX \n\n Regards."
			);
			sap.ui.getCore().byId("textId1").setValue(
				"IMPORTANT: This is an auto-generated e-mail. Please do not reply to this e-mail. For any queries, you may write to email id copied in CC. This e-mail and any files transmitted with it are for the sole use of the intended recipient(s) and may contain confidential and privileged information. If you are not the intended recipient, please destroy all copies and the original message. Any unauthorized review, use, disclosure, dissemination, forwarding, printing or copying of this email or any action taken in reliance on this e-mail is strictly prohibited and may be unlawful."
			);

			sap.ui.getCore().byId("idVenGSTNEmail").setFilterFunction(function (sTerm, oItem) {
				// A case-insensitive 'string contains' filter
				return oItem.getText().match(new RegExp(sTerm, "i")) || oItem.getKey().match(new RegExp(sTerm, "i"));
			});
			sap.ui.getCore().byId("idVenNameEmail").setFilterFunction(function (sTerm, oItem) {
				// A case-insensitive 'string contains' filter
				return oItem.getText().match(new RegExp(sTerm, "i")) || oItem.getKey().match(new RegExp(sTerm, "i"));
			});
			that.GvRequestedId = obj.requestId;
			that.FY = obj.financialYear;
			that.ReturnType = obj.returnType;
			that.onEmailReqIdPress1(obj.requestId);

		},
		onEmailReqIdPress1: function (requestId) {
			var that = this;
			// $.sap.entityID = "P2001353321";
			var oEmailReqIdInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 100,
				},
				"req": {
					"requestID": requestId,
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var oRecpintPANMdl = new JSONModel();
			var oRecpintPANView = this.getView();
			var oRecpintPANPath = "/aspsapapi/getNonCompVendorEmailCommData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRecpintPANPath,
					contentType: "application/json",
					data: JSON.stringify(oEmailReqIdInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._venMstrPaginationFr(data.hdr);
					oRecpintPANMdl.setData(data);
					sap.ui.getCore().byId("idtablerefundEmail").setModel(oRecpintPANMdl, "emailCommPopupData");
					var a = data.resp;
					var hash = {},
						result = [];
					for (var i = 0; i < a.length; i++) {
						if (!hash[a[i].emailStatus]) {
							hash[a[i].emailStatus] = true;
							result.push(a[i]);
						}
					}
					var json = new JSONModel();
					json.setData(result);
					sap.ui.getCore().byId("idtablerefundEmail").setModel(json, "venSecEmailsCombo23");
					that.oEmailPopupVen();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		_venMstrPaginationFr: function (header) {
			var core = sap.ui.getCore();
			var pageNumber = Math.ceil(header.totalCount / 100);
			core.byId("txtPageNoVCF").setText("/ " + pageNumber);
			core.byId("inPageNoVCF").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				core.byId("inPageNoVCF").setValue(pageNumber);
				core.byId("inPageNoVCF").setEnabled(false);
				core.byId("btnPrevVCF").setEnabled(false);
				core.byId("btnNextVCF").setEnabled(false);
				core.byId("btnFirstVCF").setEnabled(false);
				core.byId("btnLastVCF").setEnabled(false);
			} else if (core.byId("inPageNoVCF").getValue() === "" || core.byId("inPageNoVCF").getValue() === "0") {
				core.byId("inPageNoVCF").setValue(1);
				core.byId("inPageNoVCF").setEnabled(true);
				core.byId("btnPrevVCF").setEnabled(false);
				core.byId("btnFirstVCF").setEnabled(false);
			} else {
				core.byId("inPageNoVCF").setEnabled(true);
			}
			var vPageNo = parseInt(core.byId("inPageNoVCF").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				core.byId("btnNextVCF").setEnabled(true);
				core.byId("btnLastVCF").setEnabled(true);
			} else {
				core.byId("btnNextVCF").setEnabled(false);
				core.byId("btnLastVCF").setEnabled(false);
			}
			core.byId("btnPrevVCF").setEnabled(vPageNo > 1 ? true : false);
			core.byId("btnFirstVCF").setEnabled(vPageNo > 1 ? true : false);
		},

		oEmailPopupVen: function (oEvt) {
			var that = this,
				arr = [],
				arr1 = [];
			var oTableData = sap.ui.getCore().byId("idtablerefundEmail").getModel("emailCommPopupData").getData();
			var oSelrow = sap.ui.getCore().byId("idtablerefundEmail").getRows();
			for (var i = 0; i < oTableData.resp.length; i++) {
				var oSecondaryEmailIds = oTableData.resp[i].secondaryEmailIds;
				oSecondaryEmailIds.unshift({
					emailId: "All"
				});
				var oRecipientEmailIds = oTableData.resp[i].recipientEmailIds;
				oRecipientEmailIds.unshift({
					emailId: "All"
				});
				oSelrow[i].getCells()[4].setModel(new JSONModel(oSecondaryEmailIds), "venSecEmailsCombo");
				oSelrow[i].getCells()[5].setModel(new JSONModel(oRecipientEmailIds), "venRecpntEmailsCombo");
			}
		},
		onFilterEmail: function (oEvt) {
			var that = this;
			var oTable = sap.ui.getCore().byId("idtablerefundEmail");
			var searchText = oEvt.getSource().getSelectedKey();
			var filters = [];
			if (searchText !== undefined && searchText !== '') {
				var filter1 = new sap.ui.model.Filter({
					path: "vendorGstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "vendorName",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "emailStatus",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				this.onEmailReqIdPress1(that.GvRequestedId);
			}
		},
		onSelectChange: function (oEvent) {
			var arr = [],
				arr1 = [];
			var selectedIndices = oEvent.getSource().getSelectedIndices();

			if (oEvent.getParameters().selectAll === undefined && selectedIndices.length === 0) {
				this.onRefrshEmailDD();
			} else {
				oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[4].setSelectedKeys("");
				oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[5].setSelectedKeys("");
			}
			for (var index = 0; index < selectedIndices.length; index++) {
				var secData = oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].getModel("emailCommPopupData").getData().resp[
					selectedIndices[index]].secondaryEmailIds;
				//secData.shift();
				for (var i = 0; i < secData.length; i++) {
					arr.push(secData[i].emailId);
				}

				var cerData = oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].getModel("emailCommPopupData").getData().resp[
					selectedIndices[index]].recipientEmailIds;
				//cerData.shift();
				for (var j = 0; j < cerData.length; j++) {
					arr1.push(cerData[j].emailId);
				}
				oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].setSelectedKeys(arr);
				oEvent.getSource().getRows()[selectedIndices[index]].getCells()[5].setSelectedKeys(arr1);
			}
		},
		onPressPaginationVCF: function (oEvent) {
			var core = sap.ui.getCore();
			var vValue = parseInt(core.byId("inPageNoVCF").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevVCF")) {
				vValue -= 1;
				if (vValue === 1) {
					core.byId("btnPrevVCF").setEnabled(false);
				}
				if (!core.byId("btnNextVCF").getEnabled()) {
					core.byId("btnNextVCF").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextVCF")) {
				var vPageNo = parseInt(core.byId("txtPageNoVCF").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					core.byId("btnPrevVCF").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextVCF").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstVCF")) {
				vValue = 1;
				if (vValue === 1) {
					core.byId("btnFirstVCF").setEnabled(false);
				}
				if (!core.byId("btnLastVCF").getEnabled()) {
					core.byId("btnLastVCF").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnLastVCF")) {
				vValue = parseInt(core.byId("txtPageNoVCF").getText().split(" ")[1], 10);

				if (vValue > 1) {
					core.byId("btnFirstVCF").setEnabled(true);
				}
				if (vValue === vPageNo) {
					core.byId("btnLastVCF").setEnabled(false);
				}
			} else {
				vPageNo = parseInt(core.byId("txtPageNoVCF").getText().split(" ")[1], 10);
			}
			core.byId("inPageNoVCF").setValue(vValue);
			core.onEmailReqIdPress1(this.GvRequestedId);
		},

		onSubmitPaginationVCF: function () {
			var core = sap.ui.getCore();
			var vPageNo = core.byId("inPageNoVCF").getValue(),
				pageNumber = parseInt(core.byId("txtPageNoVCF").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onEmailReqIdPress1(this.GvRequestedId);
		},

		onRefrshEmailDD: function () {
			var that = this;
			sap.ui.getCore().byId("idVenGSTNEmail").setSelectedKey("");
			sap.ui.getCore().byId("idVenNameEmail").setSelectedKey("");
			sap.ui.getCore().byId("linkEntityID55").setSelectedKey("");
			this.onEmailReqIdPress1(that.GvRequestedId);
		},
		onPreEmailOtpSend: function (oEvent) {
			var that = this;
			var emailTableID = sap.ui.getCore().byId("idtablerefundEmail"),
				emailTableRows = sap.ui.getCore().byId("idtablerefundEmail").getRows(),
				emailSelectedTableIndices = emailTableID.getSelectedIndices();
			if (emailSelectedTableIndices.length === 0) {
				sap.m.MessageToast.show("Please select atleast one GSTIN");
				return;
			}
			var emailPayload = {
				"req": {
					"EntityName": $.sap.entityName,
					"FY": that.FY,
					"ReturnType": that.ReturnType,
					"VendorDetails": []
				}
			};
			for (var x = 0; x < emailSelectedTableIndices.length; x++) {
				var tempObj = {
					"RcpntEmail": [],
					"VndrSecndEmail": []
				};
				tempObj.VndrContactNo = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].vendorContactNumber;
				tempObj.VcomRequestID = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].requestID;
				tempObj.VGSTIN = emailTableRows[emailSelectedTableIndices[x]].getCells()[0].getText();
				tempObj.vendorName = emailTableRows[emailSelectedTableIndices[x]].getCells()[1].getText();
				tempObj.ReturnType = emailTableRows[emailSelectedTableIndices[x]].getCells()[2].getText();
				tempObj.VndrPrmryEmail = emailTableRows[emailSelectedTableIndices[x]].getCells()[3].getText();
				tempObj.EmailStatus = emailTableRows[emailSelectedTableIndices[x]].getCells()[6].getText();
				var vendorSecDropdown = emailTableRows[emailSelectedTableIndices[x]].getCells()[4].getSelectedItems();
				var receiptEmailDropdown = emailTableRows[emailSelectedTableIndices[x]].getCells()[5].getSelectedItems();

				for (var y = 0; y < vendorSecDropdown.length; y++) {
					tempObj.VndrSecndEmail.push(vendorSecDropdown[y].getText());
				}
				if (tempObj.VndrSecndEmail[0] === "All") {
					tempObj.VndrSecndEmail.shift();
				}
				for (var z = 0; z < receiptEmailDropdown.length; z++) {
					tempObj.RcpntEmail.push(receiptEmailDropdown[z].getText());
				}
				if (tempObj.RcpntEmail[0] === "All") {
					tempObj.RcpntEmail.shift();
				}

				emailPayload.req.VendorDetails.push(tempObj);
			}
			var oEmailpath = "/aspsapapi/postNonCompVEmailCommDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oEmailpath,
					contentType: "application/json",
					data: JSON.stringify(emailPayload)
				}).done(function (data, status, jqXHR) {
					//Email Popup should be closed Here.
					if (data.hdr.status === "S") {
						sap.ui.getCore().byId("idtablerefundEmail").setSelectedIndex(-1);
						MessageBox.success("Email has been successfully Initiated");
						that._oDialogRefund.close();
						that._oDialogRefund.destroy();
						that._oDialogRefund = null;
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
						that._oDialogRefund.close();
						that._oDialogRefund.destroy();
						that._oDialogRefund = null;
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},
		onRefreshNonCompVend: function () {
			var that = this;
			that.onLoadNonCompTable();
		},
		onCloseEmailOtp: function () {
			this._oDialogRefund.close();
			this._oDialogRefund.destroy();
			this._oDialogRefund = null;
		},
		onPressEmailComBack: function () {
			this.getView().byId("id_EmailPg").setVisible(false);
			this.getView().byId("id_NonCompalintPg").setVisible(true);
		},
		onExit: function () {
			this.getView().byId("id_EmailPg").setVisible(false);
			this.getView().byId("id_NonCompalintPg").setVisible(false);
		},
		//############################ Email #############################//
		//============= Code ended by chaithra on 08/03/2021 =============//

		////////Pagination/////////////
		_nicPagination: function (header) {
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoN").setText("/ " + pageNumber);
			this.byId("inPageNoN").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN").setValue(pageNumber);
				this.byId("inPageNoN").setEnabled(false);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			} else if (this.byId("inPageNoN").getValue() === "" || this.byId("inPageNoN").getValue() === "0") {
				this.byId("inPageNoN").setValue(1);
				this.byId("inPageNoN").setEnabled(true);
				this.byId("btnPrevN").setEnabled(false);
				this.byId("btnFirstN").setEnabled(false);
			} else {
				this.byId("inPageNoN").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN").setEnabled(true);
				this.byId("btnLastN").setEnabled(true);
			} else {
				this.byId("btnNextN").setEnabled(false);
				this.byId("btnLastN").setEnabled(false);
			}
			this.byId("btnPrevN").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN").setEnabled(false);
				}
				if (!this.byId("btnNextN").getEnabled()) {
					this.byId("btnNextN").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN")) {
				var vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN").setEnabled(false);
				}
				if (!this.byId("btnLastN").getEnabled()) {
					this.byId("btnLastN").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastN")) {
				vValue = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN").setValue(vValue);
			this.nicCredentialTabl(vValue);
		},

		onSubmitPagination: function () {
			var vPageNo = this.byId("inPageNoN").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl: function (vPageNo) {
			var that = this;
			var FY = this.byId("idFiYear").getSelectedKey();
			var RetT = this.byId("idRetType").getSelectedKey();
			var RepT = this.byId("idViewType").getSelectedKey();
			this.byId("FYid").setText(this.byId("idFiYear").getSelectedKey());

			var payload = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": vCount
				},
				"req": {
					"entityId": $.sap.entityID,
					"vendorPan": this.removeAll(this.aVenPan),
					"vendorGstin": this.removeAll(this.aVenGstin),
					"financialYear": FY,
					"returnType": RetT,
					"reportType": RepT
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getVendorReturnFilingData.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._nicPagination(data.hdr);
				var odata = data.resp.overallFilingStatusDtos,
					resp = data.resp;
				this.byId("statusId").setText(resp.modifedOn ? (resp.status + ": " + resp.modifedOn) : null);
				this.byId("statsFreq").setText(resp.retFrequencyTime ? (resp.retFrequencyStatus + ": " + resp.retFrequencyTime) : "");
				if (odata !== undefined) {
					var arry = [];
					for (var i = 0; i < odata.length; i++) {
						var L1 = odata[i];
						for (var a = 0; a < odata[i].overAllPanStatus.length; a++) {
							var all = odata[i].overAllPanStatus[a];
							var month = all.month;
							if (month.slice(0, 2) === "04") {
								L1.AprilStatus = all.staus;
							}
							if (month.slice(0, 2) === "05") {
								L1.MayStatus = all.staus;
							}
							if (month.slice(0, 2) === "06") {
								L1.JuneStatus = all.staus;
							}
							if (month.slice(0, 2) === "07") {
								L1.JulyStatus = all.staus;
							}
							if (month.slice(0, 2) === "08") {
								L1.AugStatus = all.staus;
							}
							if (month.slice(0, 2) === "09") {
								L1.SepStatus = all.staus;
							}
							if (month.slice(0, 2) === "10") {
								L1.OctStatus = all.staus;
							}
							if (month.slice(0, 2) === "11") {
								L1.NovStatus = all.staus;
							}
							if (month.slice(0, 2) === "12") {
								L1.DecStatus = all.staus;
							}
							if (month.slice(0, 2) === "01") {
								L1.JanStatus = all.staus;
							}
							if (month.slice(0, 2) === "02") {
								L1.FebStatus = all.staus;
							}
							if (month.slice(0, 2) === "03") {
								L1.MarStatus = all.staus;
							}
							if (month.slice(0, 2) === "13") {
								L1.APRJUNStatus = all.staus;
							}
							if (month.slice(0, 2) === "14") {
								L1.JULSEPStatus = all.staus;
							}
							if (month.slice(0, 2) === "15") {
								L1.OCTDECStatus = all.staus;
							}
							if (month.slice(0, 2) === "16") {
								L1.JANMARStatus = all.staus;
							}
							if (month.slice(0, 2) === "17") {
								L1.AprSepStatus = all.staus;
							}
							if (month.slice(0, 2) === "18") {
								L1.OctMarStatus = all.staus;
							}
						}
						if (odata[i].gstinWiseFilingStatusMonthwise !== undefined) {
							for (var j = 0; j < odata[i].gstinWiseFilingStatusMonthwise.length; j++) {
								var L2 = odata[i].gstinWiseFilingStatusMonthwise[j];
								for (var k = 0; k < odata[i].gstinWiseFilingStatusMonthwise[j].eachGstinwiseStatusCombination.length; k++) {
									var L3 = odata[i].gstinWiseFilingStatusMonthwise[j].eachGstinwiseStatusCombination[k];
									var timep = L3.month;
									if (timep.slice(0, 2) === "04") {
										L2.AprilStatus = L3.status;
									}
									if (timep.slice(0, 2) === "05") {
										L2.MayStatus = L3.status;
									}
									if (timep.slice(0, 2) === "06") {
										L2.JuneStatus = L3.status;
									}
									if (timep.slice(0, 2) === "07") {
										L2.JulyStatus = L3.status;
									}
									if (timep.slice(0, 2) === "08") {
										L2.AugStatus = L3.status;
									}
									if (timep.slice(0, 2) === "09") {
										L2.SepStatus = L3.status;
									}
									if (timep.slice(0, 2) === "10") {
										L2.OctStatus = L3.status;
									}
									if (timep.slice(0, 2) === "11") {
										L2.NovStatus = L3.status;
									}
									if (timep.slice(0, 2) === "12") {
										L2.DecStatus = L3.status;
									}
									if (timep.slice(0, 2) === "01") {
										L2.JanStatus = L3.status;
									}
									if (timep.slice(0, 2) === "02") {
										L2.FebStatus = L3.status;
									}
									if (timep.slice(0, 2) === "03") {
										L2.MarStatus = L3.status;
									}
									if (timep.slice(0, 2) === "13") {
										L2.APRJUNStatus = L3.status;
									}
									if (timep.slice(0, 2) === "14") {
										L2.JULSEPStatus = L3.status;
									}
									if (timep.slice(0, 2) === "15") {
										L2.OCTDECStatus = L3.status;
									}
									if (timep.slice(0, 2) === "16") {
										L2.JANMARStatus = L3.status;
									}
									if (timep.slice(0, 2) === "17") {
										L2.AprSepStatus = L3.status;
									}
									if (timep.slice(0, 2) === "18") {
										L2.OctMarStatus = L3.status;
									}
								}
							}
						}
						arry.push(L1);
					}

					for (var n = 0; n < arry.length; n++) {
						if (arry[n].gstinWiseFilingStatusMonthwise !== undefined) {
							for (var ab = 0; ab < arry[n].gstinWiseFilingStatusMonthwise.length; ab++) {
								delete arry[n].gstinWiseFilingStatusMonthwise[ab].eachGstinwiseStatusCombination;
							}
						}
					}
					that.getView().setModel(new JSONModel(arry), "NCV");
				} else {
					that.getView().setModel(new JSONModel([]), "NCV");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onHelpVendorPan: function () {
			if (!this.VendorPan) {
				this.VendorPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorPANF4", this);
				this.getView().addDependent(this.VendorPan);
				this.getView().setModel(new JSONModel({
					pageNumber: 1,
					pageSize: 1000
				}), "DialogProperty");
				this.panUpdatePaginatedModel({
					pageNumber: 1,
					pageSize: 1000
				});
				this.byId("NonsearchId1").setValue();
			}
			this.VendorPan.open();
		},

		onSearchGstinsPAN: function (oEvent) {
			var sQuery = oEvent.getParameter("newValue").toLowerCase();

			// Get the original JSON data
			var oDataMain = this.getView().getModel("venPANDataMain").getProperty("/");
			var oModel = this.getView().getModel("vendorPAN");
			var aData = oModel.getProperty("/"); // Original Data

			// If no query, reset table to original data
			if (!sQuery) {
				oModel.setProperty("/", oDataMain);
				return;
			}

			// Filter the data manually
			var aFilteredData = oDataMain.filter(function (oItem) {
				return oItem.gstin.toLowerCase().includes(sQuery);
			});

			// Set the new filtered data to the model
			oModel.setProperty("/", aFilteredData);
		},

		panUpdatePaginatedModel: function (oProp) {
			this.byId("NonPANCheckId").setSelected(false);
			var aFullData = this.getView().getModel("venPANDataMain").getProperty("/");

			// Calculate Pagination
			var iStartIndex = (oProp.pageNumber - 1) * oProp.pageSize;
			var iEndIndex = iStartIndex + oProp.pageSize;
			var aPaginatedData = aFullData.slice(iStartIndex, iEndIndex);

			// Set a New JSON Model for Paginated Data
			var oPaginatedModel = new JSONModel();
			oPaginatedModel.setSizeLimit(aPaginatedData.length);
			oPaginatedModel.setData(aPaginatedData);
			this.getView().setModel(oPaginatedModel, "vendorPAN");

			var totalPage = aFullData.length / oProp.pageSize;
			if (aFullData.length % oProp.pageSize) {
				totalPage = totalPage + 1;
			}
			var tatal = oProp.pageNumber + " / " + parseInt(totalPage);
			this.byId("idPanTotal").setText(tatal);
		},

		// Load Next Page
		onPanLoadMore: function (oEvent) {

			var pageData = oEvent.getSource().getModel("DialogProperty").getProperty("/");
			var totalItems = this.getView().getModel("venPANDataMain").getProperty("/").length;
			var totalPages = Math.ceil(totalItems / pageData.pageSize);

			if (pageData.pageNumber < totalPages) {
				pageData.pageNumber++;
				this.panUpdatePaginatedModel(pageData);
			}
		},

		// Load Previous Page
		onPanLoadPrevious: function (oEvent) {
			var pageData = oEvent.getSource().getModel("DialogProperty").getProperty("/");
			if (pageData.pageNumber > 1) {
				pageData.pageNumber--;
				this.panUpdatePaginatedModel(pageData);
			}
		},

		onClose: function (oEvent) {
			this.VendorPan.close();
			this.aVenPan = [];
			this.aVenGstin = [];
			var aRegPAN = this.byId("NonselectDialog").getSelectedItems(),
				aPan = [];
			aRegPAN.forEach(function (e) {
				this.aVenPan.push(e.getTitle());
				aPan.push({
					"pan": e.getTitle()
				});
			}.bind(this));
			var oModel = new JSONModel();
			oModel.setSizeLimit(aPan.length);
			oModel.setData(aPan);
			this.getView().setModel(oModel, "Token");
			this.onPAN();
			this.getView().setModel(new JSONModel([]), "TokenGSTN");
			this.byId("NonVGCheckId").setSelected(false);
		},

		onSelect: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("NonselectDialog").removeSelections();
			} else {
				this.byId("NonselectDialog").selectAll();
			}
		},

		// onSearchGstinsPAN: function (oEvent) {
		// 	var aFilter = [];
		// 	var sQuery = oEvent.getSource().getValue();
		// 	if (sQuery) {
		// 		aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
		// 	}
		// 	// filter binding
		// 	var oList = this.getView().byId("NonselectDialog");
		// 	var oBinding = oList.getBinding("items");
		// 	oBinding.filter(aFilter);
		// 	this.getView().byId("NonselectDialog").removeSelections();
		// },

		onHelpVendorGstin: function () {
			this.VenGSTIN.open();
		},

		onCloseGSTIN: function (oEvent) {
			this.VenGSTIN.close();
			this.aVenGstin = [];
			var aRegPAN = this.byId("NonselectDialogGSTIN").getSelectedItems(),
				aGSTIN = [];
			aRegPAN.forEach(function (e) {
				this.aVenGstin.push(e.getTitle());
				aGSTIN.push({
					"gstin": e.getTitle()
				});
			}.bind(this));
			var oModel = new JSONModel();
			oModel.setSizeLimit(aGSTIN.length);
			oModel.setData(aGSTIN);
			this.getView().setModel(oModel, "TokenGSTN");
			//this.onPrsEmailVenGSTIN();
		},

		onSelectGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("NonselectDialogGSTIN").removeSelections();
			} else {
				this.byId("NonselectDialogGSTIN").selectAll();
			}
		},

		onSearchGstins1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("NonselectDialogGSTIN");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("NonselectDialogGSTIN").removeSelections();
		},

		handleValueHelpPan: function () {
			if (!this.VendorPanE) {
				this.VendorPanE = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.NonVendorEPANF4", this);
				this.getView().addDependent(this.VendorPanE);
				this.getView().setModel(new JSONModel({
					pageNumber: 1,
					pageSize: 1000
				}), "eDialogProperty");
				this.panEUpdatePaginatedModel({
					pageNumber: 1,
					pageSize: 1000
				});
				this.byId("NonEsearchId1").setValue();
			}
			this.VendorPanE.open();
		},
		onSearchGstinsPANE: function (oEvent) {
			var sQuery = oEvent.getParameter("newValue").toLowerCase(); // "value" instead of "newValue" for 'change' event

			// Get the original JSON data
			var oDataMain = this.getView().getModel("venPANDataMain").getProperty("/");
			var oModel = this.getView().getModel("oVendorPanModel");
			var aData = oModel.getProperty("/"); // Original Data

			// If no query, reset table to original data
			if (!sQuery) {
				oModel.setProperty("/", oDataMain);
				return;
			}

			// Filter the data manually
			var aFilteredData = oDataMain.filter(function (oItem) {
				return oItem.gstin.toLowerCase().includes(sQuery);
			});

			// Set the new filtered data to the model
			oModel.setProperty("/", aFilteredData);
		},

		panEUpdatePaginatedModel: function (oProp) {
			this.byId("NonEPANCheckId").setSelected(false);
			var aFullData = this.getView().getModel("venPANDataMain").getProperty("/");

			// Calculate Pagination
			var iStartIndex = (oProp.pageNumber - 1) * oProp.pageSize;
			var iEndIndex = iStartIndex + oProp.pageSize;
			var aPaginatedData = aFullData.slice(iStartIndex, iEndIndex);

			// Set a New JSON Model for Paginated Data
			var oPaginatedModel = new JSONModel();
			oPaginatedModel.setSizeLimit(aPaginatedData.length);
			oPaginatedModel.setData(aPaginatedData);
			this.getView().setModel(oPaginatedModel, "oVendorPanModel");

			var totalPage = aFullData.length / oProp.pageSize;
			if (aFullData.length % oProp.pageSize) {
				totalPage = totalPage + 1;
			}
			var tatal = oProp.pageNumber + " / " + parseInt(totalPage);
			this.byId("idPanETotal").setText(tatal);
		},

		// Load Next Page
		onPanELoadMore: function (oEvent) {

			var pageData = oEvent.getSource().getModel("eDialogProperty").getProperty("/");
			var totalItems = this.getView().getModel("venPANDataMain").getProperty("/").length;
			var totalPages = Math.ceil(totalItems / pageData.pageSize);

			if (pageData.pageNumber < totalPages) {
				pageData.pageNumber++;
				this.panEUpdatePaginatedModel(pageData);
			}
		},

		// Load Previous Page
		onPanELoadPrevious: function (oEvent) {
			var pageData = oEvent.getSource().getModel("eDialogProperty").getProperty("/");
			if (pageData.pageNumber > 1) {
				pageData.pageNumber--;
				this.panEUpdatePaginatedModel(pageData);
			}
		},

		onCloseE: function (oEvent) {
			debugger;
			this.VendorPanE.close();
			this.PanarrE = [];
			this.GSTINarrE = [];
			var aRegPAN = this.byId("NonEselectDialog").getSelectedItems(),
				aPan = [];
			aRegPAN.forEach(function (e) {
				this.PanarrE.push(e.getTitle());
				aPan.push({
					"pan": e.getTitle()
				});
			}.bind(this));
			var oModel = new JSONModel();
			oModel.setSizeLimit(aPan.length);
			oModel.setData(aPan);
			this.getView().setModel(oModel, "TokenE");
			this.getView().setModel(new JSONModel([]), "TokenGSTNE");
			this.onLoadVendorGSTN();
			this.byId("NonVGCheckIdE").setSelected(false);
		},

		onSelectE: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("NonEselectDialog").removeSelections();
			} else {
				this.byId("NonEselectDialog").selectAll();
			}
		},

		// onSearchGstinsPANE: function (oEvent) {
		// 	var aFilter = [];
		// 	var sQuery = oEvent.getSource().getValue();
		// 	if (sQuery) {
		// 		aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
		// 	}
		// 	// filter binding
		// 	var oList = this.getView().byId("NonEselectDialog");
		// 	var oBinding = oList.getBinding("items");
		// 	oBinding.filter(aFilter);
		// 	this.getView().byId("NonEselectDialog").removeSelections();
		// },

		handleValueHelpGstin: function () {
			this.VenGSTINE.open();
		},

		onCloseGSTINE: function (oEvent) {
			this.VenGSTINE.close();
			this.GSTINarrE = [];
			var aRegPAN = this.byId("NonselectDialogGSTINE").getSelectedItems(),
				GSTIN1 = [];

			aRegPAN.forEach(function (e) {
				this.GSTINarrE.push(e.getTitle());
				GSTIN1.push({
					"gstin": e.getTitle()
				});
			}.bind(this));
			var oModel = new JSONModel();
			oModel.setSizeLimit(GSTIN1.length);
			oModel.setData(GSTIN1);
			this.getView().setModel(oModel, "TokenGSTNE");
			//this.onPrsEmailVenGSTIN();
		},

		onSelectGSTINE: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("NonselectDialogGSTINE").removeSelections();
			} else {
				this.byId("NonselectDialogGSTINE").selectAll();
			}
		},

		onSearchGstins1E: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("NonselectDialogGSTINE");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("NonselectDialogGSTINE").removeSelections();
		},

		_resetDialogSelectAll: function () {
			this.aVenGstin = [];
			this.aVenPan = [];
			this.PanarrE = [];
			this.GSTINarrE = [];
			this.getView().setModel(new JSONModel([]), "Token");
			this.getView().setModel(new JSONModel([]), "TokenGSTN");
			this.getView().setModel(new JSONModel([]), "TokenE");
			this.getView().setModel(new JSONModel([]), "TokenGSTNE");
			if (this.VendorPan) {
				this.getView().removeDependent(this.VendorPan);
				this.VendorPan.destroy();
				this.VendorPan = null;
			}
			
			if (this.VendorPanE) {
				this.getView().removeDependent(this.VendorPanE);
				this.VendorPanE.destroy();
				this.VendorPanE = null;
			}
			
			if (this.VendorPan) {
				this.getView().byId("NonPANCheckId").setSelected(false);
				this.getView().byId("NonsearchId1").setValue(null);
			}
			if (this.VenGSTIN) {
				this.getView().byId("NonVGCheckId").setSelected(false);
				this.getView().byId("NonsearchIdGSTIN").setValue(null);
			}
			if (this.VendorPanE) {
				this.getView().byId("NonEPANCheckId").setSelected(false);
				this.getView().byId("NonEsearchId1").setValue(null);
			}
			if (this.VenGSTINE) {
				this.getView().byId("NonVGCheckIdE").setSelected(false);
				this.getView().byId("NonsearchIdGSTINE").setValue(null);
			}
		}
	});
});