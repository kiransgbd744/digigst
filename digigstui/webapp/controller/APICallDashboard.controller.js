sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, Formatter, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.APICallDashboard", {
		formatter: Formatter,

		onInit: function () {
			this.oSelectedKey = "GSTR1";
			this.getOwnerComponent().getRouter().getRoute("APICallDashboard").attachPatternMatched(this._onRouteMatched, this);
			this.payload = [];
			this.taxPeriod = [];
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("APICallDashboard");
			this._gstr1DefaultValue();
			this.getView().setModel(new JSONModel({
				"value": false
			}), "switchvisible");

			this.aliasName = "GSTR1Table";
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getAllFy.do",
					contentType: "application/json",
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						oModel.setProperty("/", data.resp);
						this.onPrsSearchGstr1Go(data.resp.finYears[0].fy);
					}
					this.getView().setModel(oModel, "oFyModel");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "oFyModel");
				}.bind(this));
		},

		_gstr1DefaultValue: function () {
			this.getView().setModel(new JSONModel({
				"April": false,
				"May": false,
				"June": false,
				"July": false,
				"Aug": false,
				"Sep": false,
				"Oct": false,
				"Nov": false,
				"Dec": false,
				"Jan": false,
				"Feb": false,
				"Mar": false,
				"Q1Check1": false,
				"Q2Check1": false,
				"Q3Check1": false,
				"Q4Check1": false
			}), "Gstr1Property");
		},

		onGstr1Change: function (oevent) {
			var vKey = oevent.getSource().getSelectedKey();
			this.byId("dtFinYearGstr1").setSelectedKey(vKey);
		},

		onAPICallChangeSegment: function (oEvt) {
			this._gstr1DefaultValue();
			this.payload = [];
			this.taxPeriod = [];
			var oSelectedKey = oEvt.getSource().getSelectedKey();
			this.oSelectedKey = oSelectedKey;
			this.byId("idApiCallSwitch").setVisible(true);
			this.byId("id_ApiCallSwitchTxt").setVisible(true);
			[
				"idChartGstr1", "idChartGstr1A", "idChartGstr2A", "idChartGstr3B", "idChartTdsTcs", "idChartGstr7",
				"idChartGstr8", "idChartGstr9", "idChartGstr8A", "idChartGstr6", "iditc04"
			].forEach(function (id) {
				this.byId(id).setVisible(false);
			}.bind(this));

			switch (oSelectedKey) {
			case "GSTR1":
				this.byId("idChartGstr1").setVisible(true);
				break;
			case "GSTR1A":
				this.byId("idChartGstr1A").setVisible(true);
				break;
			case "GSTR2A":
				this.byId("idChartGstr2A").setVisible(true);
				break;
			case "GSTR3B":
				this.byId("id_ApiCallSwitchTxt").setVisible(false);
				this.byId("idApiCallSwitch").setVisible(false);
				this.byId("idChartGstr3B").setVisible(true);
				break;
			case "TDS":
				this.byId("id_ApiCallSwitchTxt").setVisible(false);
				this.byId("idApiCallSwitch").setVisible(false);
				this.byId("idChartTdsTcs").setVisible(true);
				break;
			case "GSTR7":
				this.byId("id_ApiCallSwitchTxt").setVisible(true);
				this.byId("idApiCallSwitch").setVisible(true);
				this.byId("idChartGstr7").setVisible(true);
				break;
			case "GSTR8":
				this.byId("idChartGstr8").setVisible(true);
				this.byId("idApiCallSwitch").setVisible(false);
				this.byId("id_ApiCallSwitchTxt").setVisible(false);
				break;
			case "GSTR9":
				this.byId("idChartGstr9").setVisible(true);
				this.byId("idApiCallSwitch").setVisible(false);
				this.byId("id_ApiCallSwitchTxt").setVisible(false);
				break;
			case "GSTR8A":
				this.byId("idChartGstr8A").setVisible(true);
				break;
			case "GSTR6":
				this.byId("idChartGstr6").setVisible(true);
				break;
			case "ITC04":
				this.byId("iditc04").setVisible(true);
				this.byId("idApiCallSwitch").setVisible(false);
				this.byId("id_ApiCallSwitchTxt").setVisible(false);
				break;
			}
			this.onPrsSearchGstr1Go();
		},

		onChngeAPICallSwith: function (oEvt) {
			var oAPISwitch = oEvt.getSource().getState();
			if (oAPISwitch === false) {
				this.getView().getModel("switchvisible").getData().value = false;
				this.onPrsSearchGstr1Go();
			} else if (oAPISwitch === true) {
				this.getView().getModel("switchvisible").getData().value = true;
			}
			this.getView().getModel("switchvisible").refresh(true);
		},

		onPrsSearchGstr1Go: function (fy) {
			switch (this.getView().byId("idActionInformationDistGstr6").getSelectedKey()) {
			case "ITC04":
				this.aliasName = "ITC04Table";
				this._getItcApiDashboard(fy, "ITC04", "ITC04Table", "iditc04Tab");
				break;
			case "TDS":
				this.aliasName = "TdsTcsTable";
				this._getApiDashboardStatus(fy, "GSTR2X", "TdsTcsTable", "idTableGstrTdsTcs");
				break;
			case "GSTR1":
				this.aliasName = "GSTR1Table";
				this._getApiDashboardStatus(fy, "GSTR1", "GSTR1Table", "idTableGstr1");
				break;
			case "GSTR1A":
				this.aliasName = "GSTR1ATable";
				this._getApiDashboardStatus(fy, "GSTR1A", "GSTR1ATable", "idTableGstr1A");
				break;
			case "GSTR2A":
				this.aliasName = "GSTR2ATable";
				this._getApiDashboardStatus(fy, "GSTR2A", "GSTR2ATable", "idTableGstr2A");
				break;
			case "GSTR3B":
				this.aliasName = "GSTR3BTable";
				this._getApiDashboardStatus(fy, "GSTR3B", "GSTR3BTable", "idTableGstr3B");
				break;
			case "GSTR6":
				this.aliasName = "GSTR6Table";
				this._getApiDashboardStatus(fy, "GSTR6", "GSTR6Table", "idTableGstr6");
				break;
			case "GSTR8":
				this.aliasName = "GSTR8Table";
				this._getApiDashboardStatus(fy, "GSTR8", "GSTR8Table", "idTableGstr8");
				break;
			case "GSTR7":
				this.aliasName = "GSTR7Table";
				this._getApiDashboardStatus(fy, "GSTR7", "GSTR7Table", "idTableGstr7");
				break;
			case "GSTR8A":
				this.aliasName = "GSTR8ATable";
				this._getACDStatusOnFy("GSTR8A", "GSTR8ATable", "idTableGstr8A");
				break;
			case "GSTR9":
				this.aliasName = "GSTR9Table";
				this._getACDStatusOnFy("GSTR9", "GSTR9Table", "id_Gstr9Tab");
				break;
			}
		},

		_getACDStatusOnFy: function (retType, modelName, id) {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"fy": this.byId("dtFinYearGstr1").getSelectedKey(),
					"returnType": retType
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			this.byId(id).setSelectedIndex(-1);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getACDStatusOnFy.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						oModel.setData(data.resp);
					}
					this.getView().setModel(oModel, modelName);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), modelName);
				}.bind(this));
		},

		_getItcApiDashboard: function (fy, retType, modelName, id) {
			var aIdx = this.byId("dtFinYearGstr1").getSelectedIndex(),
				key = this.getView().getModel("oFyModel").getProperty("/finYears/" + aIdx + "/key"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"fy": (this.byId("dtFinYearGstr1").getSelectedKey() === "" ? fy : this.byId("dtFinYearGstr1").getSelectedKey()),
						"returnType": retType
					}
				};
			if (key === "2021") {
				this.byId("col1").setVisible(true);
				this.byId("col2").setVisible(true);
				this.byId("col3").setVisible(false);
				this.byId("col4").setVisible(false);
				this.byId("col5").setVisible(false);
				this.byId("col6").setVisible(true);
			} else if (+key < 2021) {
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
			sap.ui.core.BusyIndicator.show(0);
			this.byId(id).setSelectedIndex(-1);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getApiDashboardStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var arry = [];
					if (data.hdr.status === "S") {
						var odata = data.resp.apiGstinDetails;
						for (var i = 0; i < odata.length; i++) {
							var itemArr = odata[i].taxPeriodDetails;
							for (var j = 0; j < itemArr.length; j++) {
								var timep = itemArr[j].taxPeriod,
									Status = itemArr[j].status,
									intiatedOnDt = itemArr[j].initiatedOn,
									successSec = itemArr[j].successSections,
									failedSec = itemArr[j].failedSections,
									inPrgrsSec = itemArr[j].inProgressSections,
									reptStatus = itemArr[j].reportStatus,
									initiatedSections = itemArr[j].initiatedSections,
									sucWithOutDtaSec = itemArr[j].successWithNoDataSections;
								if (timep.slice(0, 2) === "13") {
									odata[i].Q1timep = timep;
									odata[i].Q1Mar = Status;
									odata[i].initiatedOnQ1 = intiatedOnDt;
									odata[i].initiatedSectionsQ1 = initiatedSections;
									odata[i].successSectionsQ1 = successSec;
									odata[i].failedSectionsQ1 = failedSec;
									odata[i].inPrgrsQ1 = inPrgrsSec;
									odata[i].reptStatusQ1 = reptStatus;
									odata[i].sucWithOutDtaSecQ1 = sucWithOutDtaSec;
									odata[i].Q1Check = false;
								} else if (timep.slice(0, 2) === "14") {
									odata[i].Q2timep = timep;
									odata[i].Q2Mar = Status;
									odata[i].initiatedOnQ2 = intiatedOnDt;
									odata[i].initiatedSectionsQ2 = initiatedSections;
									odata[i].successSectionsQ2 = successSec;
									odata[i].failedSectionsQ2 = failedSec;
									odata[i].inPrgrsQ2 = inPrgrsSec;
									odata[i].reptStatusQ2 = reptStatus;
									odata[i].sucWithOutDtaSecQ2 = sucWithOutDtaSec;
									odata[i].Q2Check = false;
								} else if (timep.slice(0, 2) === "15") {
									odata[i].Q3timep = timep;
									odata[i].Q3Mar = Status;
									odata[i].initiatedOnQ3 = intiatedOnDt;
									odata[i].initiatedSectionsQ3 = initiatedSections;
									odata[i].successSectionsQ3 = successSec;
									odata[i].failedSectionsQ3 = failedSec;
									odata[i].inPrgrsQ3 = inPrgrsSec;
									odata[i].reptStatusQ3 = reptStatus;
									odata[i].sucWithOutDtaSecQ3 = sucWithOutDtaSec;
									odata[i].Q3Check = false;
								} else if (timep.slice(0, 2) === "16") {
									odata[i].Q4timep = timep;
									odata[i].Q4Mar = Status;
									odata[i].initiatedOnQ4 = intiatedOnDt;
									odata[i].initiatedSectionsQ4 = initiatedSections;
									odata[i].successSectionsQ4 = successSec;
									odata[i].failedSectionsQ4 = failedSec;
									odata[i].inPrgrsQ4 = inPrgrsSec;
									odata[i].reptStatusQ4 = reptStatus;
									odata[i].sucWithOutDtaSecQ4 = sucWithOutDtaSec;
									odata[i].Q4Check = false;
								} else if (timep.slice(0, 2) === "17") {
									odata[i].H1timep = timep;
									odata[i].H1Mar = Status;
									odata[i].initiatedOnH1 = intiatedOnDt;
									odata[i].initiatedSectionsH1 = initiatedSections;
									odata[i].successSectionsH1 = successSec;
									odata[i].failedSectionsH1 = failedSec;
									odata[i].inPrgrsH1 = inPrgrsSec;
									odata[i].reptStatusH1 = reptStatus;
									odata[i].sucWithOutDtaSecH1 = sucWithOutDtaSec;
									odata[i].H1Check = false;
								} else if (timep.slice(0, 2) === "18") {
									odata[i].H2timep = timep;
									odata[i].H2Mar = Status;
									odata[i].initiatedOnH2 = intiatedOnDt;
									odata[i].initiatedSectionsH2 = initiatedSections;
									odata[i].successSectionsH2 = successSec;
									odata[i].failedSectionsH2 = failedSec;
									odata[i].inPrgrsH2 = inPrgrsSec;
									odata[i].reptStatusH2 = reptStatus;
									odata[i].sucWithOutDtaSecH2 = sucWithOutDtaSec;
									odata[i].H2Check = false;
								}
							}
							arry.push(odata[i]);
						}
					}
					this.getView().setModel(new JSONModel(arry), modelName);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), modelName);
				}.bind(this));
		},

		_getApiDashboardStatus: function (fy, retType, modelName, id) {
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"fy": (this.byId("dtFinYearGstr1").getSelectedKey() === "" ? fy : this.byId("dtFinYearGstr1").getSelectedKey()),
					"returnType": retType
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			this.byId(id).setSelectedIndex(-1);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getApiDashboardStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var arry = [];
					if (data.hdr.status === "S") {
						var odata = data.resp.apiGstinDetails;
						for (var i = 0; i < odata.length; i++) {
							var itemArr = odata[i].taxPeriodDetails;
							for (var j = 0; j < itemArr.length; j++) {
								var timep = itemArr[j].taxPeriod,
									Status = itemArr[j].status,
									intiatedOnDt = itemArr[j].initiatedOn,
									successSec = itemArr[j].successSections,
									failedSec = itemArr[j].failedSections,
									inPrgrsSec = itemArr[j].inProgressSections,
									reptStatus = itemArr[j].reportStatus,
									initiatedSections = itemArr[j].initiatedSections,
									sucWithOutDtaSec = itemArr[j].successWithNoDataSections;
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
								} else if (timep.slice(0, 2) === "13") {
									odata[i].Q1timep = timep;
									odata[i].Q1Mar = Status;
									odata[i].initiatedOnQ1 = intiatedOnDt;
									odata[i].initiatedSectionsQ1 = initiatedSections;
									odata[i].successSectionsQ1 = successSec;
									odata[i].failedSectionsQ1 = failedSec;
									odata[i].inPrgrsQ1 = inPrgrsSec;
									odata[i].reptStatusQ1 = reptStatus;
									odata[i].sucWithOutDtaSecQ1 = sucWithOutDtaSec;
									odata[i].Q1Check = false;
								} else if (timep.slice(0, 2) === "14") {
									odata[i].Q2timep = timep;
									odata[i].Q2Mar = Status;
									odata[i].initiatedOnQ2 = intiatedOnDt;
									odata[i].initiatedSectionsQ2 = initiatedSections;
									odata[i].successSectionsQ2 = successSec;
									odata[i].failedSectionsQ2 = failedSec;
									odata[i].inPrgrsQ2 = inPrgrsSec;
									odata[i].reptStatusQ2 = reptStatus;
									odata[i].sucWithOutDtaSecQ2 = sucWithOutDtaSec;
									odata[i].Q2Check = false;
								} else if (timep.slice(0, 2) === "15") {
									odata[i].Q3timep = timep;
									odata[i].Q3Mar = Status;
									odata[i].initiatedOnQ3 = intiatedOnDt;
									odata[i].initiatedSectionsQ3 = initiatedSections;
									odata[i].successSectionsQ3 = successSec;
									odata[i].failedSectionsQ3 = failedSec;
									odata[i].inPrgrsQ3 = inPrgrsSec;
									odata[i].reptStatusQ3 = reptStatus;
									odata[i].sucWithOutDtaSecQ3 = sucWithOutDtaSec;
									odata[i].Q3Check = false;
								} else if (timep.slice(0, 2) === "16") {
									odata[i].Q4timep = timep;
									odata[i].Q4Mar = Status;
									odata[i].initiatedOnQ4 = intiatedOnDt;
									odata[i].initiatedSectionsQ4 = initiatedSections;
									odata[i].successSectionsQ4 = successSec;
									odata[i].failedSectionsQ4 = failedSec;
									odata[i].inPrgrsQ4 = inPrgrsSec;
									odata[i].reptStatusQ4 = reptStatus;
									odata[i].sucWithOutDtaSecQ4 = sucWithOutDtaSec;
									odata[i].Q4Check = false;
								}
							}
							arry.push(odata[i]);
						}
					}
					this.getView().setModel(new JSONModel(arry), modelName);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), modelName);
				}.bind(this));
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

		APIText: function (val) {
			if (val === undefined || val === "" || val === null) {
				return "NOT INITIATED";
			} else {
				return val;
			}
		},

		onDownloadApril: function (oEvent) {
			var modelAprl, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") {
				reptType = "GSTR7";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelAprl = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelAprl.gstin + "&taxPeriod=" + modelAprl.Apriltimep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadMay: function (oEvent) {
			var modelMay, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelMay = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMay.gstin + "&taxPeriod=" + modelMay.Maytimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadJune: function (oEvent) {
			var modelJune, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelJune = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelJune.gstin + "&taxPeriod=" + modelJune.Junetimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadJuly: function (oEvent) {
			var modelJuly, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelJuly = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelJuly.gstin + "&taxPeriod=" + modelJuly.Julytimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadAug: function (oEvent) {
			var modelAug, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelAug = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelAug.gstin + "&taxPeriod=" + modelAug.Augtimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadSept: function (oEvent) {
			var modelSept, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelSept = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelSept.gstin + "&taxPeriod=" + modelSept.Septtimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadOct: function (oEvent) {
			var modelOct, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelOct = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelOct.gstin + "&taxPeriod=" + modelOct.Octtimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadNov: function (oEvent) {
			var modelNov, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelNov = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelNov.gstin + "&taxPeriod=" + modelNov.Novtimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadDec: function (oEvent) {
			var modelDec, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelDec = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelDec.gstin + "&taxPeriod=" + modelDec.Dectimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadJan: function (oEvent) {
			var modelJan, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelJan = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelJan.gstin + "&taxPeriod=" + modelJan.Jantimep +
				"&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadFeb: function (oEvent) {
			var model, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				model = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				model = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + model.gstin + "&taxPeriod=" + model.Febtimep + "&returnType=" +
				reptType +
				"";
			window.open(oExclDwnldPath);
		},

		onDownloadMar: function (oEvent) {
			var modelMar, reptType;
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				reptType = "GSTR1";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR1Table").getObject();
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR2ATable").getObject();
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR3BTable").getObject();
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR8ATable").getObject();
			} else if (segkey === "GSTR7") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR7";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR7Table").getObject();
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("GSTR6Table").getObject();
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				modelMar = oEvent.getSource().getEventingParent().getBindingContext("TdsTcsTable").getObject();
			}
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.Martimep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadQ1: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.Q1timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadQ2: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.Q2timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadQ3: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.Q3timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadQ4: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.Q4timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadH1: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.H1timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		onDownloadH2: function (oEvent) {
			var reptType = "ITC04";
			var modelMar = oEvent.getSource().getEventingParent().getBindingContext("ITC04Table").getObject();
			var oExclDwnldPath = "/aspsapapi/downloadMonthlyReport.do?gstin=" + modelMar.gstin + "&taxPeriod=" + modelMar.H2timep +
				"&returnType=" + reptType + "";
			window.open(oExclDwnldPath);
		},

		Q1Select: function (oEvt) {
			var Tp = "13";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].Q1Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].Q1Check = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].Q1Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		Q1Select1: function (oEvt) {
			var Tp = "13";
			this.SingleSelect(oEvt, Tp);
		},

		Q2Select: function (oEvt) {
			var Tp = "14";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].Q2Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].Q2Check = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].Q2Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		Q2Select1: function (oEvt) {
			var Tp = "14";
			this.SingleSelect(oEvt, Tp);
		},

		Q3Select: function (oEvt) {
			var Tp = "15";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].Q3Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].Q3Check = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].Q3Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		Q3Select1: function (oEvt) {
			var Tp = "15";
			this.SingleSelect(oEvt, Tp);
		},

		Q4Select: function (oEvt) {
			var Tp = "16";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].Q4Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].Q4Check = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].Q4Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index == -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		Q4Select1: function (oEvt) {
			var Tp = "16";
			this.SingleSelect(oEvt, Tp);
		},

		H1Select: function (oEvt) {
			var Tp = "17";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].H1Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].H1Check = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].H1Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		H1Select1: function (oEvt) {
			var Tp = "17";
			this.SingleSelect(oEvt, Tp);
		},

		H2Select: function (oEvt) {
			var Tp = "17";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].H2Check = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].H2Check = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].H2Check = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		H2Select1: function (oEvt) {
			var Tp = "18";
			this.SingleSelect(oEvt, Tp);
		},

		AprilSelect: function (oEvt) {
			debugger;
			var Tp = "04";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (this.aliasName == "GSTR9Table") {
				data = this.getView().getModel(this.aliasName).getData().apiGstinDetails;
			}
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					for (var i = 0; i < data.length; i++) {
						data[i].AprilCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].AprilCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].AprilCheck = false;
				}
				debugger;
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						debugger;
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		AprilSelect1: function (oEvt) {
			var Tp = "04";
			this.SingleSelect(oEvt, Tp);
		},

		MaySelect: function (oEvt) {
			var Tp = "05";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (this.aliasName == "GSTR9Table") {
				data = this.getView().getModel(this.aliasName).getData().apiGstinDetails;
			}
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					/*this.taxPeriod.push(Tp);*/
					for (var i = 0; i < data.length; i++) {
						data[i].MayCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp]
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].MayCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].MayCheck = false;
				}
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[0].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}

				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		MaySelect1: function (oEvt) {
			var Tp = "05";
			this.SingleSelect(oEvt, Tp);
		},

		JuneSelect: function (oEvt) {
			var Tp = "06";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (this.aliasName == "GSTR9Table") {
				data = this.getView().getModel(this.aliasName).getData().apiGstinDetails;
			}
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].JuneCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].JuneCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].JuneCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		JuneSelect1: function (oEvt) {
			var Tp = "06";
			this.SingleSelect(oEvt, Tp);
		},

		JulySelect: function (oEvt) {
			var Tp = "07";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (this.aliasName == "GSTR9Table") {
				data = this.getView().getModel(this.aliasName).getData().apiGstinDetails;
			}
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].JulyCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].JulyCheck = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].JulyCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}

				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		JulySelect1: function (oEvt) {
			var Tp = "07";
			this.SingleSelect(oEvt, Tp);
		},

		AugSelect: function (oEvt) {
			var Tp = "08";
			var data = [];
			data = this.getView().getModel(this.aliasName).getData();
			if (this.aliasName == "GSTR9Table") {
				data = this.getView().getModel(this.aliasName).getData().apiGstinDetails;
			}
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].AugCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].AugCheck = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].AugCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		AugSelect1: function (oEvt) {
			var Tp = "08";
			this.SingleSelect(oEvt, Tp);
		},

		SepSelect: function (oEvt) {
			var Tp = "09";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].SepCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].SepCheck = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].SepCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		SepSelect1: function (oEvt) {
			var Tp = "09";
			this.SingleSelect(oEvt, Tp);
		},

		OctSelect: function (oEvt) {
			var Tp = "10";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].OctCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].OctCheck = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].OctCheck = false;
				}
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		OctSelect1: function (oEvt) {
			var Tp = "10";
			this.SingleSelect(oEvt, Tp);
		},

		NovSelect: function (oEvt) {
			var Tp = "11";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].NovCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].NovCheck = true;
					}

					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

					//this.payload[0].taxPeriod.push(Tp);
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].NovCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		NovSelect1: function (oEvt) {
			var Tp = "11";
			this.SingleSelect(oEvt, Tp);
		},

		DecSelect: function (oEvt) {
			var Tp = "12";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].DecCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].DecCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].DecCheck = false;
				}

				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		DecSelect1: function (oEvt) {
			var Tp = "12";
			this.SingleSelect(oEvt, Tp);
		},

		JanSelect: function (oEvt) {
			var Tp = "01";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].JanCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].JanCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;

				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].JanCheck = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		JanSelect1: function (oEvt) {
			var Tp = "01";
			this.SingleSelect(oEvt, Tp);
		},

		FebSelect: function (oEvt) {
			var Tp = "02";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].FebCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].FebCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].FebCheck = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		FebSelect1: function (oEvt) {
			var Tp = "02";
			this.SingleSelect(oEvt, Tp);
		},

		MarSelect: function (oEvt) {
			var Tp = "03";
			var data = this.getView().getModel(this.aliasName).getData();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					//this.taxPeriod.push(Tp);
					for (var i = 0; i < data.length; i++) {
						data[i].MarCheck = true;
						var obj = {
							"gstin": data[i].gstin,
							"taxPeriod": [Tp] //this.taxPeriod
						};
						this.payload.push(obj);
					}
				} else {
					for (var k = 0; k < data.length; k++) {
						data[k].MarCheck = true;
					}
					var oData = $.extend(true, [], this.payload);
					for (var j = 0; j < oData.length; j++) {
						oData[j].taxPeriod.push(Tp);
					}
					this.payload = oData;
				}
			} else {
				for (var j = 0; j < data.length; j++) {
					data[j].MarCheck = false;
				}
				/*var index = this.payload[0].taxPeriod.indexOf(Tp);
				this.payload[0].taxPeriod.splice(index, 1);*/
				for (var j = 0; j < this.payload.length; j++) {
					if (this.payload[j].taxPeriod.length > 1) {
						var index = this.payload[j].taxPeriod.indexOf(Tp);
						if (index !== -1) {
							this.payload[j].taxPeriod.splice(index, 1);
						}
					} else {
						this.payload = [];
					}
				}
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		MarSelect1: function (oEvt) {
			var Tp = "03";
			this.SingleSelect(oEvt, Tp);
		},

		SingleSelect: function (oEvt, Tp) {
			debugger;
			var SelItem = oEvt.getSource().getEventingParent().getBindingContext(this.aliasName).getObject();
			if (oEvt.getSource().getSelected()) {
				if (this.payload.length === 0) {
					this.taxPeriod.push(Tp);
					var obj = {
						"gstin": SelItem.gstin,
						"taxPeriod": [Tp] //this.taxPeriod
					};
					this.payload.push(obj);

				} else {
					var index = -1;
					for (var h = 0; h < this.payload.length; h++) {
						if (this.payload[h].gstin === SelItem.gstin) {
							index = h; //Returns element position, so it exists
							break;
						}
					}
					if (index === -1) {
						this.payload.push({
							"gstin": SelItem.gstin,
							"taxPeriod": [Tp]
						});
					} else {
						this.payload[index].taxPeriod.push(Tp);
					}
				}
			} else {
				var oData = $.extend(true, [], this.payload);
				for (var j = 0; j < oData.length; j++) {
					if (oData[j].taxPeriod.length === 1) {
						if (oData[j].gstin === SelItem.gstin) {
							oData.splice(j, 1);
						}
					} else {
						if (oData[j].gstin === SelItem.gstin) {
							var index1 = oData[j].taxPeriod.indexOf(Tp);
							if (index1 !== -1) {
								oData[j].taxPeriod.splice(index1, 1);
							}
						}
					}
				}
				this.payload = oData;
			}
			this.getView().getModel(this.aliasName).refresh(true);
		},

		onSelectionGstn: function (oEvt) {
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey(); // Added by chaithra on 13/4/2021
			if (["GSTR8A", "GSTR9"].includes(segkey)) {
				var vId = (segkey == "GSTR8A" ? "idTableGstr8A" : "id_Gstr9Tab"),
					modelName = (segkey == "GSTR8A" ? "GSTR8ATable" : "GSTR9Table"),
					aTabData = this.getModel(modelName).getProperty("/apiGstinDetails"),
					aIndex = this.byId(vId).getSelectedIndices();
				this.payload = aIndex.map(function (e) {
					return aTabData[e].gstin;
				}.bind(this));
				return;
			}
			var Tp = [];
			if (segkey == "ITC04") {
				var Q1 = this.byId("col1").getVisible();
				var Q2 = this.byId("col2").getVisible();
				var Q3 = this.byId("col3").getVisible();
				var Q4 = this.byId("col4").getVisible();
				var H1 = this.byId("col5").getVisible();
				var H2 = this.byId("col6").getVisible();
				if (Q1) {
					Tp.push("13");
				}
				if (Q2) {
					Tp.push("14");
				}
				if (Q3) {
					Tp.push("15");
				}
				if (Q4) {
					Tp.push("16");
				}
				if (H1) {
					Tp.push("17");
				}
				if (H2) {
					Tp.push("18");
				}
			} else {
				Tp = ["04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03"];
			}
			var TablIndx = oEvt.getSource().getSelectedIndices();
			var TablData = this.getView().getModel(this.aliasName).getData();
			var vRowIndex = oEvt.getParameters().rowIndex;
			if (oEvt.getParameters().selectAll !== undefined) {
				this.payload = [];
				this.getView().getModel("Gstr1Property").getData().April = true;
				this.getView().getModel("Gstr1Property").getData().May = true;
				this.getView().getModel("Gstr1Property").getData().June = true;
				this.getView().getModel("Gstr1Property").getData().July = true;
				this.getView().getModel("Gstr1Property").getData().Aug = true;
				this.getView().getModel("Gstr1Property").getData().Sep = true;
				this.getView().getModel("Gstr1Property").getData().Oct = true;
				this.getView().getModel("Gstr1Property").getData().Nov = true;
				this.getView().getModel("Gstr1Property").getData().Dec = true;
				this.getView().getModel("Gstr1Property").getData().Jan = true;
				this.getView().getModel("Gstr1Property").getData().Feb = true;
				this.getView().getModel("Gstr1Property").getData().Mar = true;
				this.getView().getModel("Gstr1Property").getData().Q1Check1 = true;
				this.getView().getModel("Gstr1Property").getData().Q2Check1 = true;
				this.getView().getModel("Gstr1Property").getData().Q3Check1 = true;
				this.getView().getModel("Gstr1Property").getData().Q4Check1 = true;
				for (var j = 0; j < TablIndx.length; j++) {
					TablData[TablIndx[j]].AprilCheck = true;
					TablData[TablIndx[j]].MayCheck = true;
					TablData[TablIndx[j]].JuneCheck = true;
					TablData[TablIndx[j]].JulyCheck = true;
					TablData[TablIndx[j]].AugCheck = true;
					TablData[TablIndx[j]].SepCheck = true;
					TablData[TablIndx[j]].OctCheck = true;
					TablData[TablIndx[j]].NovCheck = true;
					TablData[TablIndx[j]].DecCheck = true;
					TablData[TablIndx[j]].JanCheck = true;
					TablData[TablIndx[j]].FebCheck = true;
					TablData[TablIndx[j]].MarCheck = true;
					TablData[TablIndx[j]].lsFlag = true;
					TablData[TablIndx[j]].Q1Check = true;
					TablData[TablIndx[j]].Q2Check = true;
					TablData[TablIndx[j]].Q3Check = true;
					TablData[TablIndx[j]].Q4Check = true;
					TablData[TablIndx[j]].H1Check = true;
					TablData[TablIndx[j]].H2Check = true;
				}
				for (var l = 0; l < TablData.length; l++) {
					this.payload.push({
						"gstin": TablData[l].gstin,
						"taxPeriod": Tp
					});
				}
			} else if (vRowIndex === -1) {
				this.payload = [];
				this.getView().getModel("Gstr1Property").getData().April = false;
				this.getView().getModel("Gstr1Property").getData().May = false;
				this.getView().getModel("Gstr1Property").getData().June = false;
				this.getView().getModel("Gstr1Property").getData().July = false;
				this.getView().getModel("Gstr1Property").getData().Aug = false;
				this.getView().getModel("Gstr1Property").getData().Sep = false;
				this.getView().getModel("Gstr1Property").getData().Oct = false;
				this.getView().getModel("Gstr1Property").getData().Nov = false;
				this.getView().getModel("Gstr1Property").getData().Dec = false;
				this.getView().getModel("Gstr1Property").getData().Jan = false;
				this.getView().getModel("Gstr1Property").getData().Feb = false;
				this.getView().getModel("Gstr1Property").getData().Mar = false;
				this.getView().getModel("Gstr1Property").getData().Q1Check1 = false;
				this.getView().getModel("Gstr1Property").getData().Q2Check1 = false;
				this.getView().getModel("Gstr1Property").getData().Q3Check1 = false;
				this.getView().getModel("Gstr1Property").getData().Q4Check1 = false;
				this.getView().getModel("Gstr1Property").getData().H1Check1 = false;
				this.getView().getModel("Gstr1Property").getData().H2Check1 = false;
				for (var k = 0; k < TablData.length; k++) {
					TablData[k].AprilCheck = false;
					TablData[k].MayCheck = false;
					TablData[k].JuneCheck = false;
					TablData[k].JulyCheck = false;
					TablData[k].AugCheck = false;
					TablData[k].SepCheck = false;
					TablData[k].OctCheck = false;
					TablData[k].NovCheck = false;
					TablData[k].DecCheck = false;
					TablData[k].JanCheck = false;
					TablData[k].FebCheck = false;
					TablData[k].MarCheck = false;
					TablData[k].lsFlag = false;
					TablData[k].Q1Check = false;
					TablData[k].Q2Check = false;
					TablData[k].Q3Check = false;
					TablData[k].Q4Check = false;
					TablData[k].H1Check = false;
					TablData[k].H2Check = false;
				}
			} else {
				var oSelectedFlag;
				if (TablData[vRowIndex].lsFlag) {
					oSelectedFlag = false;
				} else {
					oSelectedFlag = true;
				}
				TablData[vRowIndex].AprilCheck = oSelectedFlag;
				TablData[vRowIndex].MayCheck = oSelectedFlag;
				TablData[vRowIndex].JuneCheck = oSelectedFlag;
				TablData[vRowIndex].JulyCheck = oSelectedFlag;
				TablData[vRowIndex].AugCheck = oSelectedFlag;
				TablData[vRowIndex].SepCheck = oSelectedFlag;
				TablData[vRowIndex].OctCheck = oSelectedFlag;
				TablData[vRowIndex].NovCheck = oSelectedFlag;
				TablData[vRowIndex].DecCheck = oSelectedFlag;
				TablData[vRowIndex].JanCheck = oSelectedFlag;
				TablData[vRowIndex].FebCheck = oSelectedFlag;
				TablData[vRowIndex].MarCheck = oSelectedFlag;
				TablData[vRowIndex].lsFlag = oSelectedFlag;
				TablData[vRowIndex].Q1Check = oSelectedFlag;
				TablData[vRowIndex].Q2Check = oSelectedFlag;
				TablData[vRowIndex].Q3Check = oSelectedFlag;
				TablData[vRowIndex].Q4Check = oSelectedFlag;
				TablData[vRowIndex].H1Check = oSelectedFlag;
				TablData[vRowIndex].H2Check = oSelectedFlag;
				if (oSelectedFlag) {
					this.payload.push({
						"gstin": TablData[vRowIndex].gstin,
						"taxPeriod": Tp
					});
				} else {
					if (this.payload.length === 1) {
						this.payload = [];
					} else {
						for (var i = 0; i < this.payload.length; i++) {
							if (TablData[vRowIndex].gstin === this.payload[i].gstin) {
								this.payload.splice(i, 1);
							}
						}
					}
				}
			}
			this.getView().getModel("Gstr1Property").refresh(true);
			this.getView().getModel(this.aliasName).refresh(true);
		},

		onGetStatus: function () {
			if (this.payload.length === 0) {
				MessageBox.information("Please select at least one GSTIN");
				return;
			}
			var segkey = this.getView().byId("idActionInformationDistGstr6").getSelectedKey();
			if (segkey === "GSTR1") {
				var reptType = "GSTR1";
				var id = "idTableGstr1";
			}
			if (segkey === "GSTR1A") {
				var reptType = "GSTR1A";
				var id = "idTableGstr1A";
			} else if (segkey === "GSTR2A") {
				reptType = "GSTR2A";
				id = "idTableGstr2A";
			} else if (segkey === "GSTR3B") {
				reptType = "GSTR3B";
				id = "idTableGstr3B";
			} else if (segkey === "GSTR8A") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR8A";
				id = "idTableGstr8A";
			} else if (segkey === "GSTR8") {
				reptType = "GSTR8";
				id = "idTableGstr8";
			} else if (segkey === "GSTR9") { // Added by chaithra on 12/05/2021 =======//
				reptType = "GSTR9";
				id = "idTableGstr9";
			} else if (segkey === "GSTR7") {
				reptType = "GSTR7";
				id = "idTableGstr7";
			} else if (segkey === "GSTR6") {
				reptType = "GSTR6";
				id = "idTableGstr6";
			} else if (segkey === "ITC04") {
				reptType = "ITC04";
				id = "iditc04Tab";
			} else if (segkey === "TDS") {
				reptType = "GSTR2X";
				this.aliasName = "TdsTcsTable";
			}
			MessageBox.alert('Do you want to Initiate Get Call for ' + (reptType !== "GSTR8A" ? reptType : "Table-8A") + '?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						this.onGetStatus1(reptType, id);
					}
				}.bind(this)
			});
		},

		onGetStatus1: function (reptType, id) {
			if (!this._oDialogbulkSaveStats) {
				this._oDialogbulkSaveStats = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.BulkSave", this);
				this.getView().addDependent(this._oDialogbulkSaveStats);
			}
			if (reptType == "GSTR9") {
				this._initiateGstr9GetCall(reptType);
				return;
			}
			if (reptType == "GSTR8A") {
				this._initiateTable8AGetCall(reptType);
				return;
			}
			var payload = {
				"req": this.payload,
				"returnType": reptType,
				"fy": this.byId("dtFinYearGstr1").getSelectedKey()
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGetCall.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._oDialogbulkSaveStats.open();
						this.payload = [];
						this.taxPeriod = [];
					} else {
						MessageBox.error("Get Status Failed");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_initiateGstr9GetCall: function (retType) {
			var aTabData = this.getModel("GSTR9Table").getProperty("/apiGstinDetails"),
				aIndex = this.byId("id_Gstr9Tab").getSelectedIndices(),
				gstins = aIndex.map(function (e) {
					return aTabData[e].gstin;
				}),
				payload = {
					"req": {
						"gstins": gstins,
						"returnType": retType,
						"fy": this.byId("dtFinYearGstr1").getSelectedKey()
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGstr9GetCall.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._oDialogbulkSaveStats.open();
					} else {
						MessageBox.error("Get Status Failed");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_initiateTable8AGetCall: function (retType) {
			var aTabData = this.getModel("GSTR8ATable").getProperty("/apiGstinDetails"),
				aIndex = this.byId("idTableGstr8A").getSelectedIndices(),
				gstins = aIndex.map(function (e) {
					return aTabData[e].gstin;
				}),
				payload = {
					"req": {
						"gstins": gstins,
						"returnType": retType,
						"fy": this.byId("dtFinYearGstr1").getSelectedKey()
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGstr8AGetCall.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._oDialogbulkSaveStats.open();
					} else {
						MessageBox.error("Get Status Failed");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseDialogBulkSave: function () {
			this._oDialogbulkSaveStats.close();
			this.onPrsSearchGstr1Go();
		},

		GSTR1AAPIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR1A");
		},

		GSTR1APIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR1");
		},

		Gstr3BAPIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR3B");
		},

		Gstr2AAPIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR2A");
		},

		GSTR2xAPIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR2X");
		},

		//============ Added by chaithra for GSTR 8A =================//
		GSTR8APIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR8A");
		},
		GSTR9APIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR9");
		},
		ITC04Downld: function () {
			this.onMenuItemPressAPIDownld("ITC04");
		},
		GSTR7APIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR7");
		},

		GSTR6APIDownld: function () {
			this.onMenuItemPressAPIDownld("GSTR6");
		},

		//============ Code ended by chaithra on 12/5/2021 ===========//

		onMenuItemPressAPIDownld: function (segkey) {
			var that = this;
			if (["GSTR1", "GSTR1A", "GSTR3B", "GSTR8A", "GSTR8", "GSTR9", "GSTR7", "ITC04", "GSTR6", "GSTR2X"].includes(segkey)) {
				if (this.payload.length === 0) {
					MessageBox.information("Please select at least one GSTIN");
					return;
				}
				if (segkey === "GSTR1") {
					var id = "idTableGstr1";
				} else if (segkey === "GSTR1A") {
					var id = "idTableGstr1A";
				} else if (segkey === "GSTR3B") {
					id = "idTableGstr3B";
				} else if (segkey === "GSTR7") {
					id = "idTableGstr7";
				} else if (segkey === "GSTR6") {
					id = "idTableGstr6";
				} else if (segkey === "ITC04") {
					id = "iditc04";
				} else if (segkey === "GSTR2X") {
					id = "idTableGstrTdsTcs";
				} else if (segkey === "GSTR8") {
					id = "idTableGstr8";
				} else if (["GSTR8A", "GSTR9"].includes(segkey)) {
					var vId = (segkey == "GSTR8A" ? "idTableGstr8A" : "id_Gstr9Tab"),
						modelName = (segkey == "GSTR8A" ? "GSTR8ATable" : "GSTR9Table"),
						aTabData = this.getModel(modelName).getProperty("/apiGstinDetails"),
						aIndex = this.byId(vId).getSelectedIndices(),
						aGstin = aIndex.map(function (e) {
							return {
								"gstin": aTabData[e].gstin
							};
						}),
						payload = {
							"req": aGstin,
							"returnType": (segkey === "GSTR8A" ? "Table-8A" : segkey),
							"fy": this.byId("dtFinYearGstr1").getSelectedKey()
						};
					this.reportDownload(payload, "/aspsapapi/downloadSelectedReports.do");
					this.onPrsSearchGstr1Go();
					return;
				}
				var FinalData = {
					"req": this.payload,
					"returnType": segkey,
					"fy": this.byId("dtFinYearGstr1").getSelectedKey()
				};

				this.reportDownload(FinalData, "/aspsapapi/downloadSelectedReports.do");
				this.payload = [];
				this.taxPeriod = [];
				this.onPrsSearchGstr1Go();
				return;
			} else if (segkey === "GSTR2A") {
				id = "idTableGstr2A";
				var gstinArr = [];
				var TablIndx = this.byId(id).getSelectedIndices();
				var TablData = this.getView().getModel(this.aliasName).getData();
				if (TablIndx.length === 0) {
					MessageBox.information("Please select at least one GSTIN");
					return;
				}

				for (var i = 0; i < TablIndx.length; i++) {
					gstinArr.push(TablData[TablIndx[i]].gstin);
				}
				var payload = {
					"req": {
						"gstin": gstinArr,
						"returnType": segkey,
						"fy": this.byId("dtFinYearGstr1").getSelectedKey()
					}
				};
				this.excelDownload(payload, "/aspsapapi/downloadGetCallReport.do");
				this.payload = [];
				this.taxPeriod = [];
				this.onPrsSearchGstr1Go();
			}
		},

		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext(this.aliasName).getObject();
			if (oValue1.authStatus !== "I") {
				return;
			}
			var vGstin = oValue1.gstin;
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": vGstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressCancel: function () {
			this._dAuthToken.close();
		},

		onPressYes: function () {
			// debugger; //eslint-disable-line
			var aData = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getData(),
				that = this,
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			that.byId("dAuthTokenConfirmation").setBusy(true);
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GenerateOTP", this);
				this.getView().addDependent(this._dGenerateOTP);
			}
			this.byId("dVerifyAuthToken").setModel(new JSONModel(searchInfo.req), "OtpProperty");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getOtp.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				that.byId("dAuthTokenConfirmation").setBusy(false);
				that._dAuthToken.close();
				if (data.resp.status === "S") {
					var oModel = that.byId("dVerifyAuthToken").getModel("OtpProperty"),
						oData = oModel.getData();
					oData.verify = false;
					oData.otp = null;
					oData.resendOtp = false;
					oModel.refresh(true);
					that._dGenerateOTP.open();
				} else {
					MessageBox.error("OTP Generation Failed. Please Try Again", { // Modified by Bharat Gupta on 05.02.2020
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				that.byId("dAuthTokenConfirmation").setBusy(false);
			});
		},

		onPressResendOTP: function () {
			// debugger; //eslint-disable-line
			var oOtpModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				oOtpData = oOtpModel.getData(),
				that = this,
				searchInfo = {
					"req": {
						"gstin": oOtpData.gstin
					}
				};
			oOtpData.resendOtp = false;
			oOtpModel.refresh(true);

			this.byId("dVerifyAuthToken").setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getOtp.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				that.byId("dVerifyAuthToken").setBusy(false);
				var oModel = that.byId("dVerifyAuthToken").getModel("OtpProperty"),
					oData = oModel.getData();
				oData.verify = false;
				oData.otp = null;
				if (data.resp.status === "S") {
					oData.resendOtp = true;
					that._dGenerateOTP.open();
				} else {
					oData.resendOtp = false;
					MessageBox.error("OTP Generation Failed. Please Try Again", {
						styleClass: "sapUiSizeCompact"
					});
				}
				oModel.refresh(true);
			}).fail(function (jqXHR, status, err) {
				that.byId("dVerifyAuthToken").setBusy(false);
			});
		},

		validateOTP: function (oEvent) {
			// debugger; //eslint-disable-line
			var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				value = oEvent.getSource().getValue();

			value = value.replace(/[^\d]/g, "");
			if (value.length === 6) {
				oModel.getData().verify = true;
			} else {
				oModel.getData().verify = false;
			}
			oModel.refresh(true);
			oEvent.getSource().setValue(value);
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		onPressVerifyOTP: function () {
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getData(),
				that = this,
				searchInfo = {
					"req": {
						"gstin": oData.gstin,
						"otpCode": oData.otp
					}
				};
			this.byId("dVerifyAuthToken").setBusy(true);

			$.ajax({
				method: "POST",
				url: "/aspsapapi/getAuthToken.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				that.byId("dVerifyAuthToken").setBusy(false);
				that._dGenerateOTP.close();
				if (data.resp.status === "S") {
					MessageBox.success("OTP is  Matched", {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					MessageBox.error("OTP is Not Matched", {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				that.byId("dVerifyAuthToken").setBusy(false);
				that._dGenerateOTP.close();
			});
		},

		Gstr1Reports: function () {
			var payload = {
				"req": this.payload,
				"returnType": "GSTR1"
			};
			this.reportDownload(payload, "/aspsapapi/downloadSelectedReports.do");
		},
		onGet8aUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuGetGstr8a").setValue();
				sap.m.MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				if (sResponse.hdr.message) {
					sap.m.MessageBox.error(sResponse.hdr.message, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.error(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}
		},
		onUploadGet8a: function () {
			var oBundle = this.getResourceBundle(),
				// aGstin = this.byId("slGet2aProcessGstin").getSelectedKeys(),
				oUploader = this.byId("fuGetGstr8a"),
				vIdx = this.byId("rgbFileType").getSelectedIndex();

			if (oUploader.getValue() === "") {
				sap.m.MessageBox.information(oBundle.getText("gstr2aSelectFile"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			switch (vIdx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/gstr8aFileUploadDocuments.do");
				break;
			case 1:
				// aGstin = this.removeAll(aGstin);
				// if (aGstin.length !== 1) {
				// 	sap.m.MessageBox.information(oBundle.getText("gstr2aUploadWarning"), {
				// 		styleClass: "sapUiSizeCompact"
				// 	});
				// 	return;
				// }
				// oUploader.setUploadUrl("/aspsapapi/jsonInput.do");
				// oUploader.setAdditionalData(aGstin[0]);
				// break;
			}
			oUploader.upload();
		},
	});
});