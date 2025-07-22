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
	return BaseController.extend("com.ey.digigst.controller.MyCH", {
		formatter: Formatter,
		onInit: function () {
			this.GSTINarr = [];
			this.Panarr = [];
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				//this.byId("FYid").setText(this.byId("idFiYear").getSelectedKey());
				this.byId("idPageN").setSelectedKey("10");
				this.onLoadFy();
				this.vendorPAN();
				this.MyGstin();
			}
		},

		MyGstin: function () {
			var oSelVenPANMdl = new JSONModel();
			var that = this,
				searchInfo = {
					"req": {
						"entityId": $.sap.entityID
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAllRegularGstins.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				data.resp.gstins.unshift({
					gstin: "All"
				});
				sap.ui.core.BusyIndicator.hide();
				oSelVenPANMdl.setData(data.resp.gstins);
				that.getView().setModel(oSelVenPANMdl, "MyGstin");
				//that.getView().setModel(new JSONModel(data.resp.vendorPans), "vendorPAN");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		vendorPAN: function () {
			if (!this.VendorPan) {
				this.VendorPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.VendorPANF4", this);
				this.getView().addDependent(this.VendorPan);
			}
			var oSelVenPANMdl = new JSONModel();
			var that = this,
				searchInfo = {
					"req": {
						"entityId": [$.sap.entityID]
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getNonCompVendorPan.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					oSelVenPANMdl.setSizeLimit(data.resp.vendorPans.length);
					oSelVenPANMdl.setData(data.resp.vendorPans);
					that.getView().setModel(oSelVenPANMdl, "vendorPAN");
				} else {
					oSelVenPANMdl.setData([]);
					that.getView().setModel(oSelVenPANMdl, "vendorPAN");
				}
				//that.getView().setModel(new JSONModel(data.resp.vendorPans), "vendorPAN");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				oSelVenPANMdl.setData([]);
				that.getView().setModel(oSelVenPANMdl, "vendorPAN");
			});
		},

		onPAN: function () {
			if (!this.VenGSTIN) {
				this.VenGSTIN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.APModule.VendorGSTINF4", this);
				this.getView().addDependent(this.VenGSTIN);
			}
			var oSelVenPANMdl = new JSONModel();
			var that = this,
				searchInfo = {
					"req": {
						"vendorPan": this.Panarr,
						"entityId": $.sap.entityID
					}
				};
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getNonCompalintVGstin.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				oSelVenPANMdl.setSizeLimit(data.resp.vendorGstins.length);
				oSelVenPANMdl.setData(data.resp.vendorGstins);
				that.getView().setModel(oSelVenPANMdl, "vendorGSTIN");
				//that.getView().setModel(new JSONModel(data.resp.vendorGstins), "vendorGSTIN");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onLoadFy: function () {
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
					} else {
						that.getView().setModel(new JSONModel([]), "oFyModel");
						that.getView().getModel("oFyModel").refresh(true);
					}
					that.onPressGo('C');
				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "oFyModel");
				that.getView().getModel("oFyModel").refresh(true);
			});
		},

		onLoadTimeStamp: function (flag) {
			var that = this;
			var FY = flag ? this.getView().getModel("oFyModel").getProperty("/finYears/0/fy") : this.byId("idFiYear").getSelectedKey();
			var Gstr2APath = "/aspsapapi/getVendorRtngTimeStamps.do?financialYear=" + FY + "&entityId=" + $.sap.entityID;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var time = JSON.parse(data).resp;
					this.byId("UploadTimeId").setText(time.uploadStatus ? (time.uploadStatus + ":" + time.uploadTime) : "");
					this.byId("TimeSid").setText(time.retFilingStatus ? (time.retFilingStatus + ": " + time.retFilingTime) : "");
					this.byId("freqTime").setText(time.retFrequencyStatus ? (time.retFrequencyStatus + ": " + time.retFrequencyTime) : "");
				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();

			});
		},

		onUpload: function () {
			var oUploader = this.byId("FileUploadId");
			if (oUploader.getValue() === "") {
				sap.m.MessageBox.information("Please select file to upload", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			sap.ui.core.BusyIndicator.show(0);
			oUploader.setUploadUrl("/aspsapapi/uploadVendorDueDateMaster.do");
			oUploader.setAdditionalData($.sap.entityID);
			oUploader.upload();
		},

		onUploadComplete: function (oEvent) {
			var that = this;
			this.onLoadTimeStamp();
			sap.ui.core.BusyIndicator.hide();
			var oRespnse = oEvent.getParameters().responseRaw;
			var status = JSON.parse(oRespnse);
			if (status.hdr.status === "S") {
				this.getView().byId("FileUploadId").setValue("");
				sap.m.MessageBox.success(status.resp, {
					styleClass: "sapUiSizeCompact"

				});
				//that.onVenUpldRefresh();
			} else {
				sap.m.MessageBox.error(status.resp, {
					styleClass: "sapUiSizeCompact"
				});
				//that.onVenUpldRefresh();
			}
		},

		onDDM: function () {
			var oReqExcelPath = "/aspsapapi/downloadVendorDueDateReport.do?entityId=" + $.sap.entityID;
			window.open(oReqExcelPath);
		},

		onPressGo: function (type) {
			this.onLoadTimeStamp(type === 'C');
			var that = this;
			var FY = (type === 'C' ? this.getView().getModel("oFyModel").getProperty("/finYears/0/fy") : this.byId("idFiYear").getSelectedKey());
			var RetT = this.byId("idRetType").getSelectedKey();
			var RepT = this.byId("idViewType").getSelectedKey();
			this.onReturnTypeChange(RetT);
			var key;
			if (RetT === "GSTR13B") {
				key = "GSTR1,GSTR3B";
			} else {
				key = RetT;
			}

			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": this.byId("idPageN").getSelectedKey()
				},
				"req": {
					"source": "my",
					"fy": FY,
					"returnType": key,
					"viewType": RepT,
					"entityId": $.sap.entityID,
					"vendorGstins": this.removeAll(this.byId("idVenGstin").getSelectedKeys()),
					"vendorPans": []
				}
			};
			// this.GSTINarr = [];
			//this.Panarr = [];
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getVendorComplianceRating.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._nicPagination(data.hdr);
				var odata = data.resp;
				that.getView().setModel(new JSONModel(data), "isChan");
				if (odata !== undefined) {
					var arry = [];
					for (var i = 0; i < odata.length; i++) {
						var L1 = odata[i];
						for (var a = 0; a < odata[i].monthWiseStatusCombination.length; a++) {
							var all = odata[i].monthWiseStatusCombination[a];
							var month = all.month;
							if (month.slice(0, 2) === "04") {
								L1.AprilStatus1Date = all.gstr1ReturnFilingDate;
								L1.AprilStatus3Date = all.gstr3BReturnFilingDate;
								L1.AprilStatus1 = all.gstr1Status;
								L1.AprilStatus3 = all.gstr3bStatus;
								L1.AprilisFiledOnTime = all.isFiledOnTime;
								L1.AprilisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "05") {
								L1.MayStatus1Date = all.gstr1ReturnFilingDate;
								L1.MayStatus3Date = all.gstr3BReturnFilingDate;
								L1.MayStatus1 = all.gstr1Status;
								L1.MayStatus3 = all.gstr3bStatus;
								L1.MayisFiledOnTime = all.isFiledOnTime;
								L1.MayisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "06") {
								L1.JuneStatus1Date = all.gstr1ReturnFilingDate;
								L1.JuneStatus3Date = all.gstr3BReturnFilingDate;
								L1.JuneStatus1 = all.gstr1Status;
								L1.JuneStatus3 = all.gstr3bStatus;
								L1.JuneisFiledOnTime = all.isFiledOnTime;
								L1.JuneisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "07") {
								L1.JulyStatus1Date = all.gstr1ReturnFilingDate;
								L1.JulyStatus3Date = all.gstr3BReturnFilingDate;
								L1.JulyStatus1 = all.gstr1Status;
								L1.JulyStatus3 = all.gstr3bStatus;
								L1.JulyisFiledOnTime = all.isFiledOnTime;
								L1.JulyisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "08") {
								L1.AugStatus1Date = all.gstr1ReturnFilingDate;
								L1.AugStatus3Date = all.gstr3BReturnFilingDate;
								L1.AugStatus1 = all.gstr1Status;
								L1.AugStatus3 = all.gstr3bStatus;
								L1.AugisFiledOnTime = all.isFiledOnTime;
								L1.AugisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "09") {
								L1.SepStatus1Date = all.gstr1ReturnFilingDate;
								L1.SepStatus3Date = all.gstr3BReturnFilingDate;
								L1.SepStatus1 = all.gstr1Status;
								L1.SepStatus3 = all.gstr3bStatus;
								L1.SepisFiledOnTime = all.isFiledOnTime;
								L1.SepisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "10") {
								L1.OctStatus1Date = all.gstr1ReturnFilingDate;
								L1.OctStatus3Date = all.gstr3BReturnFilingDate;
								L1.OctStatus1 = all.gstr1Status;
								L1.OctStatus3 = all.gstr3bStatus;
								L1.OctisFiledOnTime = all.isFiledOnTime;
								L1.OctisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "11") {
								L1.NovStatus1Date = all.gstr1ReturnFilingDate;
								L1.NovStatus3Date = all.gstr3BReturnFilingDate;
								L1.NovStatus1 = all.gstr1Status;
								L1.NovStatus3 = all.gstr3bStatus;
								L1.NovisFiledOnTime = all.isFiledOnTime;
								L1.NovisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "12") {
								L1.DecStatus1Date = all.gstr1ReturnFilingDate;
								L1.DecStatus3Date = all.gstr3BReturnFilingDate;
								L1.DecStatus1 = all.gstr1Status;
								L1.DecStatus3 = all.gstr3bStatus;
								L1.DecisFiledOnTime = all.isFiledOnTime;
								L1.DecisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "01") {
								L1.JanStatus1Date = all.gstr1ReturnFilingDate;
								L1.JanStatus3Date = all.gstr3BReturnFilingDate;
								L1.JanStatus1 = all.gstr1Status;
								L1.JanStatus3 = all.gstr3bStatus;
								L1.JanisFiledOnTime = all.isFiledOnTime;
								L1.JanisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "02") {
								L1.FebStatus1Date = all.gstr1ReturnFilingDate;
								L1.FebStatus3Date = all.gstr3BReturnFilingDate;
								L1.FebStatus1 = all.gstr1Status;
								L1.FebStatus3 = all.gstr3bStatus;
								L1.FebisFiledOnTime = all.isFiledOnTime;
								L1.FebisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "03") {
								L1.MarStatus1Date = all.gstr1ReturnFilingDate;
								L1.MarStatus3Date = all.gstr3BReturnFilingDate;
								L1.MarStatus1 = all.gstr1Status;
								L1.MarStatus3 = all.gstr3bStatus;
								L1.MarisFiledOnTime = all.isFiledOnTime;
								L1.MarisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
						}
						arry.push(L1);
					}

					that.getView().setModel(new JSONModel(arry), "VenComR");
				} else {
					that.getView().setModel(new JSONModel([]), "VenComR");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onRatingCriteria: function () {
			if (!this._oDialoggstr53) {
				this._oDialoggstr53 = sap.ui.xmlfragment("com.ey.digigst.fragments.APModule.MyRating", this);
				this.getView().addDependent(this._oDialoggstr53);
			}
			this._oDialoggstr53.open();
			this.onTabRc();
		},

		onCancel: function () {
			this.onTabRc();
			this.onLoadTimeStamp();
			this._oDialoggstr53.close();
		},

		onTabRc: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getVendorRatingCriteria.do?entityId=" + $.sap.entityID + "&source=my";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json",
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						sap.ui.getCore().byId("TimeId2").setText("Last Updated on -" + data.resp.createdOn);
						that.getView().setModel(new JSONModel(data.resp.rtngCriteriaList), "VendorRatingCriteria");
						that.getView().getModel("VendorRatingCriteria").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "VendorRatingCriteria");
						that.getView().getModel("VendorRatingCriteria").refresh(true);
					}
				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "VendorRatingCriteria");
				that.getView().getModel("VendorRatingCriteria").refresh(true);
			});
		},

		OnChanges: function (key) {
			var data1 = this.getView().getModel("VendorRatingCriteria").getData();
			if (key == "gstr1_1") {

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[0].toDay !== undefined) {
					if (!data1[0].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[0].toDay = 1;
					}
				}
				data1[0].toDay = Number(data1[0].toDay);

				if (Number(data1[0].toDay) > 29) {
					MessageBox.error("To Day cannot be greater than 29");
					data1[0].toDay = 1;
				}
				if (Number(data1[0].fromDay) > Number(data1[0].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[0].toDay = 1;
				}

				data1[1].fromDay = data1[0].toDay + 1;
				if (data1[1].fromDay > data1[1].toDay) {
					data1[1].toDay = data1[1].fromDay;
					data1[2].fromDay = data1[1].toDay + 1;
					if (data1[2].fromDay > data1[2].toDay) {
						data1[2].toDay = data1[2].fromDay;
					}
				}

			} else if (key == "gstr1_2") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[1].toDay !== undefined) {
					if (!data1[1].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[1].toDay = Number(data1[1].fromDay);
					}
				}
				data1[1].toDay = Number(data1[1].toDay);
				if (Number(data1[1].toDay) > 30) {
					MessageBox.error("To Day cannot be greater than 30");
					data1[1].toDay = Number(data1[1].fromDay);
				}
				if (Number(data1[1].fromDay) > Number(data1[1].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[1].toDay = Number(data1[1].fromDay);
				}
				data1[2].fromDay = data1[1].toDay + 1;
				if (data1[2].fromDay > data1[2].toDay) {
					data1[2].toDay = data1[2].fromDay;
				}
			} else if (key == "gstr1_3") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[2].toDay !== undefined) {
					if (!data1[2].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[2].toDay = Number(data1[2].fromDay);
					}
				}
				data1[2].toDay = Number(data1[2].toDay);
				if (Number(data1[2].toDay) > 31) {
					MessageBox.error("To Day cannot be greater than 31");
					data1[2].toDay = Number(data1[2].fromDay);
				}
				if (Number(data1[2].fromDay) > Number(data1[2].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[2].toDay = Number(data1[2].fromDay);
				}
				// data1[2].fromDay = data1[1].toDay + 1;
			} else if (key == "gstr3B_1") {

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[9].toDay !== undefined) {
					if (!data1[9].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[9].toDay = 1;
					}
				}
				data1[9].toDay = Number(data1[9].toDay);
				if (Number(data1[9].toDay) > 29) {
					MessageBox.error("To Day cannot be greater than 29");
					data1[9].toDay = 1;
				}
				if (Number(data1[9].fromDay) > Number(data1[9].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[9].toDay = 1;
				}

				data1[10].fromDay = data1[9].toDay + 1;
				if (data1[10].fromDay > data1[10].toDay) {
					data1[10].toDay = data1[10].fromDay;
					data1[11].fromDay = data1[10].toDay + 1;
					if (data1[11].fromDay > data1[11].toDay) {
						data1[11].toDay = data1[11].fromDay;
					}
				}
			} else if (key == "gstr3B_2") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[10].toDay !== undefined) {
					if (!data1[10].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[10].toDay = Number(data1[10].fromDay);
					}
				}
				data1[10].toDay = Number(data1[10].toDay);
				if (Number(data1[10].toDay) > 30) {
					MessageBox.error("To Day cannot be greater than 30");
					data1[10].toDay = Number(data1[10].fromDay);
				}
				if (Number(data1[10].fromDay) > Number(data1[10].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[10].toDay = Number(data1[10].fromDay);
				}
				data1[11].fromDay = data1[10].toDay + 1;
				if (data1[11].fromDay > data1[11].toDay) {
					data1[11].toDay = data1[11].fromDay;
				}
			} else if (key == "gstr3B_3") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[11].toDay !== undefined) {
					if (!data1[11].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[11].toDay = Number(data1[11].fromDay);
					}
				}
				data1[11].toDay = Number(data1[11].toDay);
				if (Number(data1[11].toDay) > 31) {
					MessageBox.error("To Day cannot be greater than 31");
					data1[11].toDay = Number(data1[11].fromDay);
				}
				if (Number(data1[11].fromDay) > Number(data1[11].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[11].toDay = Number(data1[11].fromDay);
				}
			} else if (key == "gstr1_A_1") {

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[4].toDay !== undefined) {
					if (!data1[4].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[4].toDay = 1;
					}
				}
				data1[4].toDay = Number(data1[4].toDay);

				if (Number(data1[4].toDay) > 28) {
					MessageBox.error("To Day cannot be greater than 28");
					data1[4].toDay = 1;
				}
				if (Number(data1[4].fromDay) > Number(data1[4].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[0].toDay = 1;
				}

				data1[5].fromDay = data1[4].toDay + 1;
				if (data1[5].fromDay > data1[5].toDay) {
					data1[5].toDay = data1[5].fromDay;
					data1[6].fromDay = data1[5].toDay + 1;
					if (data1[6].fromDay > data1[6].toDay) {
						data1[6].toDay = data1[6].fromDay;
						data1[7].fromDay = data1[6].toDay + 1;
					}
				}

			} else if (key == "gstr1_A_2") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[5].toDay !== undefined) {
					if (!data1[5].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[5].toDay = Number(data1[5].fromDay);
					}
				}
				data1[5].toDay = Number(data1[5].toDay);
				if (Number(data1[5].toDay) > 29) {
					MessageBox.error("To Day cannot be greater than 29");
					data1[5].toDay = Number(data1[5].fromDay);
				}
				if (Number(data1[5].fromDay) > Number(data1[5].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[5].toDay = Number(data1[5].fromDay);
				}
				data1[6].fromDay = data1[5].toDay + 1;
				if (data1[6].fromDay > data1[6].toDay) {
					data1[6].toDay = data1[6].fromDay;
					data1[7].fromDay = data1[6].toDay + 1;
				}
			} else if (key == "gstr1_A_3") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[6].toDay !== undefined) {
					if (!data1[6].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[6].toDay = Number(data1[6].fromDay);
					}
				}
				data1[6].toDay = Number(data1[6].toDay);
				if (Number(data1[6].toDay) > 30) {
					MessageBox.error("To Day cannot be greater than 30");
					data1[6].toDay = Number(data1[6].fromDay);
				}
				if (Number(data1[6].fromDay) > Number(data1[6].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[6].toDay = Number(data1[6].fromDay);
				}
				data1[7].fromDay = data1[6].toDay + 1;
			} else if (key == "gstr3B_A_1") {

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[13].toDay !== undefined) {
					if (!data1[13].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[13].toDay = 1;
					}
				}
				data1[13].toDay = Number(data1[13].toDay);
				if (Number(data1[13].toDay) > 28) {
					MessageBox.error("To Day cannot be greater than 28");
					data1[13].toDay = 1;
				}
				if (Number(data1[13].fromDay) > Number(data1[13].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[13].toDay = 1;
				}

				data1[14].fromDay = data1[13].toDay + 1;
				if (data1[14].fromDay > data1[14].toDay) {
					data1[14].toDay = data1[14].fromDay;
					data1[15].fromDay = data1[14].toDay + 1;
					if (data1[15].fromDay > data1[15].toDay) {
						data1[15].toDay = data1[15].fromDay;
						data1[16].fromDay = data1[15].toDay + 1;
					}
				}
			} else if (key == "gstr3B_A_2") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[14].toDay !== undefined) {
					if (!data1[14].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[14].toDay = Number(data1[14].fromDay);
					}
				}
				data1[14].toDay = Number(data1[14].toDay);
				if (Number(data1[14].toDay) > 29) {
					MessageBox.error("To Day cannot be greater than 29");
					data1[14].toDay = Number(data1[14].fromDay);
				}
				if (Number(data1[14].fromDay) > Number(data1[14].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[14].toDay = Number(data1[14].fromDay);
				}
				data1[15].fromDay = data1[14].toDay + 1;
				if (data1[15].fromDay > data1[15].toDay) {
					data1[15].toDay = data1[15].fromDay;
					data1[16].fromDay = data1[15].toDay + 1;
				}
			} else if (key == "gstr3B_A_3") {
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[15].toDay !== undefined) {
					if (!data1[15].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						data1[15].toDay = Number(data1[15].fromDay);
					}
				}
				data1[15].toDay = Number(data1[15].toDay);
				if (Number(data1[15].toDay) > 30) {
					MessageBox.error("To Day cannot be greater than 30");
					data1[15].toDay = Number(data1[15].fromDay);
				}
				if (Number(data1[15].fromDay) > Number(data1[15].toDay)) {
					MessageBox.error("To Day cannot be less than From Day");
					data1[15].toDay = Number(data1[15].fromDay);
				}
				data1[16].fromDay = data1[15].toDay + 1;
			}

		},

		onSaveChanges: function () {
			var data1 = this.getView().getModel("VendorRatingCriteria").getData();
			for (var j = 0; j < data1.length; j++) {
				if (data1[j].fromDay === "" || data1[j].fromDay === "0" || data1[j].fromDay === "00") {
					MessageBox.error("From Day cannot be Empty or Zero");
					return;
				}
				if (data1[j].toDay === "" || data1[j].toDay === "0" || data1[j].toDay === "00") {
					MessageBox.error("To Day cannot be Empty or Zero");
					return;
				}
				if (data1[j].rating === "") {
					MessageBox.error("Rating cannot be Empty");
					return;
				}

				if (Number(data1[j].fromDay) > 31) {
					MessageBox.error("From Day cannot be greater than 31");
					return;
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[j].fromDay !== undefined) {
					if (!data1[j].fromDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						return;
					}
				}

				if (j !== 7 && j !== 16) {
					if (Number(data1[j].fromDay) > Number(data1[j].toDay)) {
						MessageBox.error("From Day cannot be greater than To Day");
						return;
					}
				}

				if (j !== 7 && j !== 16) {
					if (Number(data1[j].toDay) > 31) {
						MessageBox.error("To Days can not be greater than 31");
						return;
					}
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[j].toDay !== undefined) {
					if (!data1[j].toDay.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						return;
					}
				}

				if (Number(data1[j].rating) > 5) {
					MessageBox.error("Rating can not be greater than 5");
					return;
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (data1[j].rating !== undefined) {
					if (!data1[j].rating.toString().match(reg)) {
						MessageBox.error("Negative values and alphabets are not allowed");
						return;
					}
				}
			}
			var reqest = {
				"req": {
					"entityId": $.sap.entityID,
					"source": "my",
					"rtngCriteriaList": data1
				}
			};
			var that = this;
			$.ajax({
				method: "POST",
				url: "/aspsapapi/saveVendorRatingCriteria.do",
				contentType: "application/json",
				data: JSON.stringify(reqest)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					that.onTabRc();
					that.onLoadTimeStamp();
					sap.m.MessageBox.success(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onReturnFilling: function () {
			var selectedreturnType = this.getView().byId("idRetType").getSelectedKey();
			var SelectedIndicesgstr1 = this.getView().byId("GSTR1Id").getSelectedIndices();
			var SelectedIndicesgstr2 = this.getView().byId("GSTR3Id").getSelectedIndices();
			var SelectedIndicesgstr3 = this.getView().byId("GSTR13B").getSelectedIndices();
			if (selectedreturnType === "GSTR1" && SelectedIndicesgstr1.length <= 0) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}
			if (selectedreturnType === "GSTR3B" && SelectedIndicesgstr2.length <= 0) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}
			if (selectedreturnType === "GSTR13B" && SelectedIndicesgstr3.length <= 0) {
				MessageBox.alert("Please select at least one GSTIN");
				return;
			}
			var FY = this.byId("idFiYear").getSelectedKey();
			var vendorGstins;
			if (this.vendorGstinArr !== undefined && this.vendorGstinArr.length !== 0) {
				vendorGstins = this.vendorGstinArr;
			} else {
				vendorGstins = this.GSTINarr;
			}
			var reqest = {
				"req": {
					"financialYear": FY,
					// "vendorGstins": this.GSTINarr,
					"vendorGstins": vendorGstins,
					"complianceType": "MyCompliance"
				}
			};
			var that = this;
			$.ajax({
				method: "POST",
				url: "/aspsapapi/initateGetVendorFilingStatus.do",
				contentType: "application/json",
				data: JSON.stringify(reqest)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					that.byId("TimeSid").setText(data.resp.status + ":" + data.resp.initiatedOn);
					sap.m.MessageBox.success("Return Filing Status Successfully Submitted", {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.error(data.resp, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onFilingFrequency: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"financialYear": this.byId("idFiYear").getSelectedKey(),
					"complianceType": "MyCompliance"
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
						this.byId("freqTime").setText(data.resp.status + ": " + data.resp.freqTime);
						MessageBox.success(data.resp.message);
					} else {
						MessageBox.error(data.resp.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		downloadTabData: function () {
			var retType = this.byId("idRetType").getSelectedKey(),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"fy": this.byId("idFiYear").getSelectedKey(),
						"returnType": (retType === "GSTR13B" ? "GSTR1,GSTR3B" : retType),
						"vendorGstins": this.removeAll(this.byId("idVenGstin").getSelectedKeys()),
						"vendorPans": [],
						"source": "my"
					}
				};
			this.reportDownload(payload, "/aspsapapi/getVendorRatingTableReport.do");
		},

		onMenuItemPress: function (oEvt) {
			var retType = oEvt.getParameter("item").getKey();
			if (retType === "tableData") {
				this.downloadTabData();
				return;
			}
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"fy": this.byId("idFiYear").getSelectedKey(),
					"viewType": this.byId("idViewType").getSelectedKey(),
					"reportType": retType,
					"vendorGstins": this.removeAll(this.byId("idVenGstin").getSelectedKeys()),
					"vendorPans": [],
					"source": "my"
				}
			};
			this.reportDownload1(payload, "/aspsapapi/downloadVendorRatingAsyncReport.do");
		},

		onReturnTypeChange: function (key) {
			//var key = oEvt.getSource().getSelectedKey();
			if (key === "GSTR1") {
				this.byId("GSTR1Id").setVisible(true);
				this.byId("GSTR3Id").setVisible(false);
				this.byId("GSTR13B").setVisible(false);
			} else if (key === "GSTR3B") {
				this.byId("GSTR1Id").setVisible(false);
				this.byId("GSTR3Id").setVisible(true);
				this.byId("GSTR13B").setVisible(false);
			} else {
				this.byId("GSTR1Id").setVisible(false);
				this.byId("GSTR3Id").setVisible(false);
				this.byId("GSTR13B").setVisible(true);
			}
			//this.onPressGo();
		},

		nicCredentialTabl: function (vPageNo) {
			var that = this;
			var FY = this.byId("idFiYear").getSelectedKey();
			var RetT = this.byId("idRetType").getSelectedKey();
			var RepT = this.byId("idViewType").getSelectedKey();
			var key;
			if (RetT === "GSTR13B") {
				key = "GSTR1,GSTR3B";
			} else {
				key = RetT;
			}

			var payload = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": this.byId("idPageN").getSelectedKey()
				},
				"req": {
					"source": "my",
					"fy": FY,
					"returnType": key,
					"viewType": RepT,
					"entityId": $.sap.entityID,
					"vendorGstins": this.removeAll(this.byId("idVenGstin").getSelectedKeys()),
					"vendorPans": []
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getVendorComplianceRating.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._nicPagination(data.hdr);
				that.getView().setModel(new JSONModel(data), "isChan");
				var odata = data.resp;
				if (odata !== undefined) {
					var arry = [];
					for (var i = 0; i < odata.length; i++) {
						var L1 = odata[i];
						for (var a = 0; a < odata[i].monthWiseStatusCombination.length; a++) {
							var all = odata[i].monthWiseStatusCombination[a];
							var month = all.month;
							if (month.slice(0, 2) === "04") {
								L1.AprilStatus1Date = all.gstr1ReturnFilingDate;
								L1.AprilStatus3Date = all.gstr3BReturnFilingDate;
								L1.AprilStatus1 = all.gstr1Status;
								L1.AprilStatus3 = all.gstr3bStatus;
								L1.AprilisFiledOnTime = all.isFiledOnTime;
								L1.AprilisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "05") {
								L1.MayStatus1Date = all.gstr1ReturnFilingDate;
								L1.MayStatus3Date = all.gstr3BReturnFilingDate;
								L1.MayStatus1 = all.gstr1Status;
								L1.MayStatus3 = all.gstr3bStatus;
								L1.MayisFiledOnTime = all.isFiledOnTime;
								L1.MayisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "06") {
								L1.JuneStatus1Date = all.gstr1ReturnFilingDate;
								L1.JuneStatus3Date = all.gstr3BReturnFilingDate;
								L1.JuneStatus1 = all.gstr1Status;
								L1.JuneStatus3 = all.gstr3bStatus;
								L1.JuneisFiledOnTime = all.isFiledOnTime;
								L1.JuneisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "07") {
								L1.JulyStatus1Date = all.gstr1ReturnFilingDate;
								L1.JulyStatus3Date = all.gstr3BReturnFilingDate;
								L1.JulyStatus1 = all.gstr1Status;
								L1.JulyStatus3 = all.gstr3bStatus;
								L1.JulyisFiledOnTime = all.isFiledOnTime;
								L1.JulyisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "08") {
								L1.AugStatus1Date = all.gstr1ReturnFilingDate;
								L1.AugStatus3Date = all.gstr3BReturnFilingDate;
								L1.AugStatus1 = all.gstr1Status;
								L1.AugStatus3 = all.gstr3bStatus;
								L1.AugisFiledOnTime = all.isFiledOnTime;
								L1.AugisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "09") {
								L1.SepStatus1Date = all.gstr1ReturnFilingDate;
								L1.SepStatus3Date = all.gstr3BReturnFilingDate;
								L1.SepStatus1 = all.gstr1Status;
								L1.SepStatus3 = all.gstr3bStatus;
								L1.SepisFiledOnTime = all.isFiledOnTime;
								L1.SepisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "10") {
								L1.OctStatus1Date = all.gstr1ReturnFilingDate;
								L1.OctStatus3Date = all.gstr3BReturnFilingDate;
								L1.OctStatus1 = all.gstr1Status;
								L1.OctStatus3 = all.gstr3bStatus;
								L1.OctisFiledOnTime = all.isFiledOnTime;
								L1.OctisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "11") {
								L1.NovStatus1Date = all.gstr1ReturnFilingDate;
								L1.NovStatus3Date = all.gstr3BReturnFilingDate;
								L1.NovStatus1 = all.gstr1Status;
								L1.NovStatus3 = all.gstr3bStatus;
								L1.NovisFiledOnTime = all.isFiledOnTime;
								L1.NovisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "12") {
								L1.DecStatus1Date = all.gstr1ReturnFilingDate;
								L1.DecStatus3Date = all.gstr3BReturnFilingDate;
								L1.DecStatus1 = all.gstr1Status;
								L1.DecStatus3 = all.gstr3bStatus;
								L1.DecisFiledOnTime = all.isFiledOnTime;
								L1.DecisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "01") {
								L1.JanStatus1Date = all.gstr1ReturnFilingDate;
								L1.JanStatus3Date = all.gstr3BReturnFilingDate;
								L1.JanStatus1 = all.gstr1Status;
								L1.JanStatus3 = all.gstr3bStatus;
								L1.JanisFiledOnTime = all.isFiledOnTime;
								L1.JanisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "02") {
								L1.FebStatus1Date = all.gstr1ReturnFilingDate;
								L1.FebStatus3Date = all.gstr3BReturnFilingDate;
								L1.FebStatus1 = all.gstr1Status;
								L1.FebStatus3 = all.gstr3bStatus;
								L1.FebisFiledOnTime = all.isFiledOnTime;
								L1.FebisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "03") {
								L1.MarStatus1Date = all.gstr1ReturnFilingDate;
								L1.MarStatus3Date = all.gstr3BReturnFilingDate;
								L1.MarStatus1 = all.gstr1Status;
								L1.MarStatus3 = all.gstr3bStatus;
								L1.MarisFiledOnTime = all.isFiledOnTime;
								L1.MarisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
						}
						arry.push(L1);
					}

					that.getView().setModel(new JSONModel(arry), "VenComR");
				} else {
					that.getView().setModel(new JSONModel([]), "VenComR");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		////////Pagination/////////////
		_nicPagination: function (header) {
			var vCountD = this.getView().byId("idPageN")._getSelectedItemText();
			var pageNumber = Math.ceil(header.totalCount / vCountD);
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

		handleValueHelp: function () {
			this.VendorPan.open();
			// this.vendorPAN(); /*Arun*/
		},
		/*code added by Arun on 7/11/2021*/
		onSelectionConsolMatch: function (oEvent) {
			this.vendorGstinArr = [];
			// if(this.vendorGstinArr !== undefined){

			// var oView = this.getView();
			var oList = oEvent.getSource().getModel("VenComR").getData();
			// var selectedIndices = this.byId("GSTR1Id").getSelectedIndices();
			var arrIndices = oEvent.getSource().getSelectedIndices();
			if (arrIndices.length === 1) {
				var gstin = oList[arrIndices].vendorGstin;
				this.vendorGstinArr.push(gstin);
			} else if (arrIndices.length > 1)

				for (var j = 0; j < arrIndices.length; j++) {
					var index = arrIndices[j];
					var selectedGstinValue = oList[index].vendorGstin;
					this.vendorGstinArr.push(selectedGstinValue);
				}
				// }

		},
		/*code ended by Arun on 7/11/2021*/
		onClose: function (oEvent) {
			this.VendorPan.close();
			this.Panarr = [];
			var Pan1 = [];
			var aRegPAN = this.byId("NonselectDialog").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.Panarr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				};
				Pan1.push(obj);
			}
			this.getView().setModel(new JSONModel(Pan1), "Token");
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

		onSearchGstinsPAN: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("NonselectDialog");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("NonselectDialog").removeSelections();
		},

		handleValueHelp1: function () {
			this.VenGSTIN.open();
		},

		onCloseGSTIN: function (oEvent) {
			this.VenGSTIN.close();
			this.GSTINarr = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("NonselectDialogGSTIN").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.GSTINarr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				};
				GSTIN1.push(obj);
			}
			this.getView().setModel(new JSONModel(GSTIN1), "TokenGSTN");
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
			// this.getView().byId("NonselectDialogGSTIN").removeSelections();
		},

		onPNChange: function (oEvt) {

			var that = this;
			var FY = this.byId("idFiYear").getSelectedKey();
			var RetT = this.byId("idRetType").getSelectedKey();
			var RepT = this.byId("idViewType").getSelectedKey();
			var key;
			if (RetT === "GSTR13B") {
				key = "GSTR1,GSTR3B";
			} else {
				key = RetT;
			}

			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": oEvt.getSource().getSelectedKey()
				},
				"req": {
					"source": "my",
					"fy": FY,
					"returnType": key,
					"viewType": RepT,
					"entityId": $.sap.entityID,
					"vendorGstins": this.removeAll(this.byId("idVenGstin").getSelectedKeys()),
					"vendorPans": []
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getVendorComplianceRating.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._nicPagination(data.hdr);
				var odata = data.resp;
				that.getView().setModel(new JSONModel(data), "isChan");
				if (odata !== undefined) {
					var arry = [];
					for (var i = 0; i < odata.length; i++) {
						var L1 = odata[i];
						for (var a = 0; a < odata[i].monthWiseStatusCombination.length; a++) {
							var all = odata[i].monthWiseStatusCombination[a];
							var month = all.month;
							if (month.slice(0, 2) === "04") {
								L1.AprilStatus1Date = all.gstr1ReturnFilingDate;
								L1.AprilStatus3Date = all.gstr3BReturnFilingDate;
								L1.AprilStatus1 = all.gstr1Status;
								L1.AprilStatus3 = all.gstr3bStatus;
								L1.AprilisFiledOnTime = all.isFiledOnTime;
								L1.AprilisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "05") {
								L1.MayStatus1Date = all.gstr1ReturnFilingDate;
								L1.MayStatus3Date = all.gstr3BReturnFilingDate;
								L1.MayStatus1 = all.gstr1Status;
								L1.MayStatus3 = all.gstr3bStatus;
								L1.MayisFiledOnTime = all.isFiledOnTime;
								L1.MayisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "06") {
								L1.JuneStatus1Date = all.gstr1ReturnFilingDate;
								L1.JuneStatus3Date = all.gstr3BReturnFilingDate;
								L1.JuneStatus1 = all.gstr1Status;
								L1.JuneStatus3 = all.gstr3bStatus;
								L1.JuneisFiledOnTime = all.isFiledOnTime;
								L1.JuneisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "07") {
								L1.JulyStatus1Date = all.gstr1ReturnFilingDate;
								L1.JulyStatus3Date = all.gstr3BReturnFilingDate;
								L1.JulyStatus1 = all.gstr1Status;
								L1.JulyStatus3 = all.gstr3bStatus;
								L1.JulyisFiledOnTime = all.isFiledOnTime;
								L1.JulyisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "08") {
								L1.AugStatus1Date = all.gstr1ReturnFilingDate;
								L1.AugStatus3Date = all.gstr3BReturnFilingDate;
								L1.AugStatus1 = all.gstr1Status;
								L1.AugStatus3 = all.gstr3bStatus;
								L1.AugisFiledOnTime = all.isFiledOnTime;
								L1.AugisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "09") {
								L1.SepStatus1Date = all.gstr1ReturnFilingDate;
								L1.SepStatus3Date = all.gstr3BReturnFilingDate;
								L1.SepStatus1 = all.gstr1Status;
								L1.SepStatus3 = all.gstr3bStatus;
								L1.SepisFiledOnTime = all.isFiledOnTime;
								L1.SepisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "10") {
								L1.OctStatus1Date = all.gstr1ReturnFilingDate;
								L1.OctStatus3Date = all.gstr3BReturnFilingDate;
								L1.OctStatus1 = all.gstr1Status;
								L1.OctStatus3 = all.gstr3bStatus;
								L1.OctisFiledOnTime = all.isFiledOnTime;
								L1.OctisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "11") {
								L1.NovStatus1Date = all.gstr1ReturnFilingDate;
								L1.NovStatus3Date = all.gstr3BReturnFilingDate;
								L1.NovStatus1 = all.gstr1Status;
								L1.NovStatus3 = all.gstr3bStatus;
								L1.NovisFiledOnTime = all.isFiledOnTime;
								L1.NovisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "12") {
								L1.DecStatus1Date = all.gstr1ReturnFilingDate;
								L1.DecStatus3Date = all.gstr3BReturnFilingDate;
								L1.DecStatus1 = all.gstr1Status;
								L1.DecStatus3 = all.gstr3bStatus;
								L1.DecisFiledOnTime = all.isFiledOnTime;
								L1.DecisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "01") {
								L1.JanStatus1Date = all.gstr1ReturnFilingDate;
								L1.JanStatus3Date = all.gstr3BReturnFilingDate;
								L1.JanStatus1 = all.gstr1Status;
								L1.JanStatus3 = all.gstr3bStatus;
								L1.JanisFiledOnTime = all.isFiledOnTime;
								L1.JanisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "02") {
								L1.FebStatus1Date = all.gstr1ReturnFilingDate;
								L1.FebStatus3Date = all.gstr3BReturnFilingDate;
								L1.FebStatus1 = all.gstr1Status;
								L1.FebStatus3 = all.gstr3bStatus;
								L1.FebisFiledOnTime = all.isFiledOnTime;
								L1.FebisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
							if (month.slice(0, 2) === "03") {
								L1.MarStatus1Date = all.gstr1ReturnFilingDate;
								L1.MarStatus3Date = all.gstr3BReturnFilingDate;
								L1.MarStatus1 = all.gstr1Status;
								L1.MarStatus3 = all.gstr3bStatus;
								L1.MarisFiledOnTime = all.isFiledOnTime;
								L1.MarisFiledOnTime3 = all.isGstr3BFiledOnTime;
							}
						}
						arry.push(L1);
					}

					that.getView().setModel(new JSONModel(arry), "VenComR");
				} else {
					that.getView().setModel(new JSONModel([]), "VenComR");
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onnonDetFullScreen: function (oEvt) {
			var tab = this.byId("idRetType").getSelectedKey(),
				id;
			if (tab === "GSTR1") {
				id = "GSTR1Id";
			} else if (tab === "GSTR3B") {
				id = "GSTR3Id";
			} else {
				id = "GSTR13B";
			}
			if (oEvt === "open") {
				this.byId("closebutnon").setVisible(true);
				this.byId("openbutnon").setVisible(false);
				this.byId("nonId").setFullScreen(true);
				this.byId(id).setVisibleRowCount(20);
				this.byId("fileuId").setVisible(false);
			} else {
				this.byId("closebutnon").setVisible(false);
				this.byId("openbutnon").setVisible(true);
				this.byId("nonId").setFullScreen(false);
				this.byId(id).setVisibleRowCount(8);
				this.byId("fileuId").setVisible(true);
			}
		}

	});

});