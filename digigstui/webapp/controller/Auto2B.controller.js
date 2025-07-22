sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, Formatter, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Auto2B", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Auto2B
		 */
		onInit: function () {
			
		},

		onAfterRendering: function () {
			this.onLoadFy();
			this.onPressGo();
		},

		GstrStatus: function (iStatus) {
			if (iStatus === "FAILED" || iStatus === "NOT_INITIATED") {
				return "Error";
			} else if (iStatus === "SUCCESS" || iStatus === "SUCCESS_WITH_NO_DATA") {
				return "Success";
			} else if (iStatus === "INITIATED") {
				return "Information";
			} else {
				return "Warning"; //"Information";
			}
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
						//that.EntityTabBind();
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

		onPressGo: function () {
			var APICallGstr1Info = {
				"req": {
					"entityId": $.sap.entityID,
					"fy": this.byId("Fy2b").getSelectedKey(),
					"returnType": "GSTR2B"
				}
			};
			var that = this;
			var oGstr2AModel = new JSONModel();
			var oGstr2AView = this.getView();
			var Gstr2APath = "/aspsapapi/gstr2bAutoGetCallStatus.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(APICallGstr1Info)
				}).done(function (data, status, jqXHR) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.byId("timeId").setText("Auto Scheduled Date and Time :" + data.resp.scheduledDateAndTime);
						var odata = data.resp.apiGstinDetails;
						this.arry = [];
						for (var i = 0; i < odata.length; i++) {
							var itemArr = odata[i].taxPeriodDetails;
							for (var j = 0; j < itemArr.length; j++) {
								var timep = itemArr[j].taxPeriod;
								var Status = itemArr[j].status;
								var intiatedOnDt = itemArr[j].initiatedOn;
								var successSec = itemArr[j].successSections;
								var failedSec = itemArr[j].failedSections;
								var inPrgrsSec = itemArr[j].inProgressSections;
								var reptStatus = itemArr[j].reportStatus;
								var initiatedSections = itemArr[j].initiatedSections;
								var sucWithOutDtaSec = itemArr[j].successWithNoDataSections;
								if (timep.slice(0, 2) === "04") {
									odata[i].Apriltimep = timep;
									odata[i].statusAprl = Status;
									odata[i].initiatedOnApril = intiatedOnDt;
									odata[i].initiatedSectionsAprl = initiatedSections;
									odata[i].successSectionsAprl = successSec;
									odata[i].failedSectionsAprl = failedSec;
									odata[i].inPrgrsAprl = inPrgrsSec;
									odata[i].reptStatusAprl = reptStatus;
									odata[i].sucWithOutDtaSecAprl = sucWithOutDtaSec;
									odata[i].AprilCheck = false;
								} else if (timep.slice(0, 2) === "05") {
									odata[i].Maytimep = timep;
									odata[i].statusMay = Status;
									odata[i].initiatedOnMay = intiatedOnDt;
									odata[i].initiatedSectionsMay = initiatedSections;
									odata[i].successSectionsMay = successSec;
									odata[i].failedSectionsMay = failedSec;
									odata[i].inPrgrsMay = inPrgrsSec;
									odata[i].reptStatusMay = reptStatus;
									odata[i].sucWithOutDtaSecMay = sucWithOutDtaSec;
									odata[i].MayCheck = false;
								} else if (timep.slice(0, 2) === "06") {
									odata[i].Junetimep = timep;
									odata[i].statusJune = Status;
									odata[i].initiatedSectionsJune = initiatedSections;
									odata[i].initiatedOnJune = intiatedOnDt;
									odata[i].successSectionsJune = successSec;
									odata[i].failedSectionsJune = failedSec;
									odata[i].inPrgrsJune = inPrgrsSec;
									odata[i].reptStatusJune = reptStatus;
									odata[i].sucWithOutDtaSecJune = sucWithOutDtaSec;
									odata[i].JuneCheck = false;
								} else if (timep.slice(0, 2) === "07") {
									odata[i].Julytimep = timep;
									odata[i].statusJuly = Status;
									odata[i].initiatedOnJuly = intiatedOnDt;
									odata[i].initiatedSectionsJuly = initiatedSections;
									odata[i].successSectionsJuly = successSec;
									odata[i].failedSectionsJuly = failedSec;
									odata[i].inPrgrsJuly = inPrgrsSec;
									odata[i].reptStatusJuly = reptStatus;
									odata[i].sucWithOutDtaSecJuly = sucWithOutDtaSec;
									odata[i].JulyCheck = false;
								} else if (timep.slice(0, 2) === "08") {
									odata[i].Augtimep = timep;
									odata[i].statusAug = Status;
									odata[i].initiatedOnAug = intiatedOnDt;
									odata[i].initiatedSectionsAug = initiatedSections;
									odata[i].successSectionsAug = successSec;
									odata[i].failedSectionsAug = failedSec;
									odata[i].inPrgrsAug = inPrgrsSec;
									odata[i].reptStatusAug = reptStatus;
									odata[i].sucWithOutDtaSecAug = sucWithOutDtaSec;
									odata[i].AugCheck = false;
								} else if (timep.slice(0, 2) === "09") {
									odata[i].Septtimep = timep;
									odata[i].statusSept = Status;
									odata[i].initiatedOnSept = intiatedOnDt;
									odata[i].initiatedSectionsSept = initiatedSections;
									odata[i].successSectionsSept = successSec;
									odata[i].failedSectionsSept = failedSec;
									odata[i].inPrgrsSept = inPrgrsSec;
									odata[i].reptStatusSept = reptStatus;
									odata[i].sucWithOutDtaSecSept = sucWithOutDtaSec;
									odata[i].SepCheck = false;
								} else if (timep.slice(0, 2) === "10") {
									odata[i].Octtimep = timep;
									odata[i].statusOct = Status;
									odata[i].initiatedOnOct = intiatedOnDt;
									odata[i].initiatedSectionsOct = initiatedSections;
									odata[i].successSectionsOct = successSec;
									odata[i].failedSectionsOct = failedSec;
									odata[i].inPrgrsOct = inPrgrsSec;
									odata[i].reptStatusOct = reptStatus;
									odata[i].sucWithOutDtaSecOct = sucWithOutDtaSec;
									odata[i].OctCheck = false;
								} else if (timep.slice(0, 2) === "11") {
									odata[i].Novtimep = timep;
									odata[i].statusNov = Status;
									odata[i].initiatedOnNov = intiatedOnDt;
									odata[i].initiatedSectionsNov = initiatedSections;
									odata[i].successSectionsNov = successSec;
									odata[i].failedSectionsNov = failedSec;
									odata[i].inPrgrsNov = inPrgrsSec;
									odata[i].reptStatusNov = reptStatus;
									odata[i].sucWithOutDtaSecNov = sucWithOutDtaSec;
									odata[i].NovCheck = false;
								} else if (timep.slice(0, 2) === "12") {
									odata[i].Dectimep = timep;
									odata[i].statusDec = Status;
									odata[i].initiatedOnDec = intiatedOnDt;
									odata[i].initiatedSectionsDec = initiatedSections;
									odata[i].successSectionsDec = successSec;
									odata[i].failedSectionsDec = failedSec;
									odata[i].inPrgrsDec = inPrgrsSec;
									odata[i].reptStatusDec = reptStatus;
									odata[i].sucWithOutDtaSecDec = sucWithOutDtaSec;
									odata[i].DecCheck = false;
								} else if (timep.slice(0, 2) === "01") {
									odata[i].Jantimep = timep;
									odata[i].statusJan = Status;
									odata[i].initiatedOnJan = intiatedOnDt;
									odata[i].initiatedSectionsJan = initiatedSections;
									odata[i].successSectionsJan = successSec;
									odata[i].failedSectionsJan = failedSec;
									odata[i].inPrgrsJan = inPrgrsSec;
									odata[i].reptStatusJan = reptStatus;
									odata[i].sucWithOutDtaSecJan = sucWithOutDtaSec;
									odata[i].JanCheck = false;
								} else if (timep.slice(0, 2) === "02") {
									odata[i].Febtimep = timep;
									odata[i].statusFeb = Status;
									odata[i].initiatedOnFeb = intiatedOnDt;
									odata[i].initiatedSectionsFeb = initiatedSections;
									odata[i].successSectionsFeb = successSec;
									odata[i].failedSectionsFeb = failedSec;
									odata[i].inPrgrsFeb = inPrgrsSec;
									odata[i].reptStatusFeb = reptStatus;
									odata[i].sucWithOutDtaSecFeb = sucWithOutDtaSec;
									odata[i].FebCheck = false;
								} else if (timep.slice(0, 2) === "03") {
									odata[i].Martimep = timep;
									odata[i].statusMar = Status;
									odata[i].initiatedOnMar = intiatedOnDt;
									odata[i].initiatedSectionsMar = initiatedSections;
									odata[i].successSectionsMar = successSec;
									odata[i].failedSectionsMar = failedSec;
									odata[i].inPrgrsMar = inPrgrsSec;
									odata[i].reptStatusMar = reptStatus;
									odata[i].sucWithOutDtaSecMar = sucWithOutDtaSec;
									odata[i].MarCheck = false;
								}
							}
							this.arry.push(odata[i]);
						}
						oGstr2AModel.setData(this.arry);
						oGstr2AView.setModel(oGstr2AModel, "GSTR2BTable");
					} else {
						var oGSTIN1 = new JSONModel([]);
						oGstr2AView.setModel(oGSTIN1, "GSTR2BTable");
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				var oGSTIN1 = new JSONModel([]);
				oGstr2AView.setModel(oGSTIN1, "GSTR2BTable");
			});
		}
	});
});