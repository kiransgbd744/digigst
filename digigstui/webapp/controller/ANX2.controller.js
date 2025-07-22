var oEntityMain;
sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/anx2/anx2Data",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/m/MessageToast"
], function (BaseController, anx2Data, Formatter, JSONModel, MessageBox, Filter, FilterOperator, MessageToast) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ANX2", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onInit: function () {
			var oShow = {
				"count": true,
				"taxValue": true,
				"TotalTax": true,
				"enablecount": false,
				"enabletaxValue": false,
				"enableTotalTax": false,
				"digigst": true,
				"gstn": true,
				"diff": true,
				"enabledigigst": false,
				"enablegstn": false,
				"enablediff": false
			};
			var oModel = new sap.ui.model.json.JSONModel(oShow);
			this.getView().setModel(oModel, "showing");
			var that = this;
			var vDate = new Date();

			this.byId("idfgiTaxPeriod").setMaxDate(vDate);
			this.byId("idfgiTaxPeriod").setDateValue(vDate);

			this.byId("idfgiTaxPeriodD").setMaxDate(vDate);
			this.byId("idfgiTaxPeriodD").setDateValue(vDate);

			this.byId("idGetfgiTaxPeriod").setMaxDate(vDate);
			this.byId("idGetfgiTaxPeriod").setDateValue(vDate);

			this.byId("idGetSaveTaxPeriod").setMaxDate(vDate);
			this.byId("idGetSaveTaxPeriod").setDateValue(vDate);

			this.byId("idSDGetfgiTaxPeriod").setMaxDate(vDate);
			this.byId("idSDGetfgiTaxPeriod").setDateValue(vDate);

			this.byId("iVenSummaryPerioed").setMaxDate(vDate);
			this.byId("iVenSummaryPerioed").setDateValue(vDate);

			this.byId("idfgiTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idfgiTaxPeriod").$().find("input").attr("readonly", true);
				}
			});
			this.byId("idfgiTaxPeriodD").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idfgiTaxPeriodD").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idGetfgiTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idGetfgiTaxPeriod").$().find("input").attr("readonly", true);
				}
			});
			this.byId("idSDGetfgiTaxPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idSDGetfgiTaxPeriod").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idInitiateReconPeriod").setMaxDate(vDate);
			this.byId("idInitiateReconPeriod").setDateValue(vDate);
			this.byId("idInitiateReconPeriod").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriod").$().find("input").attr("readonly", true);
				}
			});

			//this.getOwnerComponent().getRouter().getRoute("ANX2").attachPatternMatched(this._onRouteMatched, this);
			//Added by Vinay 05-12-2019//
			this.byId("reconSumDateID").setMaxDate(vDate);
			this.byId("reconSumDateID").setDateValue(vDate);

			this.byId("ReconsumaryDateID").setMaxDate(vDate);
			this.byId("ReconsumaryDateID").setDateValue(vDate);

			this.byId("ReconsumaryDateID1").setMaxDate(vDate);
			this.byId("ReconsumaryDateID1").setDateValue(vDate);

			//BOC RR Consolidated changes by Rakesh 16-01-2020//
			//####### Consolidated Matching (Date) Filter Bar #######
			this.byId("dtConsld").setMaxDate(vDate);
			this.byId("dtConsld").setDateValue(vDate);

			//############# Exact Match (Date) Filter Bar #####
			this.byId("ExactDateId").setMaxDate(vDate);
			this.byId("ExactDateId").setDateValue(vDate);

			//#############  Matched Upto Tolerance (Date) Filter Bar #####
			this.byId("MUTDateId").setMaxDate(vDate);
			this.byId("MUTDateId").setDateValue(vDate);

			//#############  Document Type Mismatch (Date) Filter Bar #####
			this.byId("DTMDateId").setMaxDate(vDate);
			this.byId("DTMDateId").setDateValue(vDate);

			//##############   Document Date Mismatch (Date) Filter Bar ####
			this.byId("DDMDateId").setMaxDate(vDate);
			this.byId("DDMDateId").setDateValue(vDate);

			//######### Value Mismatch (Date) Filter Bar #######
			this.byId("ValMDateId").setMaxDate(vDate);
			this.byId("ValMDateId").setDateValue(vDate);

			//######### POS Mismatch (Date) Filter Bar #######
			this.byId("POSMDateId").setMaxDate(vDate);
			this.byId("POSMDateId").setDateValue(vDate);

			//######### (DTTPC) Multi Mismatch (Date) Filter Bar #######
			this.byId("MultiMDateId").setMaxDate(vDate);
			this.byId("MultiMDateId").setDateValue(vDate);

			//######### Fuzzy Mismatch (Date) Filter Bar #######
			this.byId("FuzzyMDateId").setMaxDate(vDate);
			this.byId("FuzzyMDateId").setDateValue(vDate);

			//######### Addition in ANX-2 (Date) Filter Bar #######
			this.byId("AddtnANX2DateId").setMaxDate(vDate);
			this.byId("AddtnANX2DateId").setDateValue(vDate);

			//######### Addition in PR (Date) Filter Bar #######
			this.byId("AddtnPRDateId").setMaxDate(vDate);
			this.byId("AddtnPRDateId").setDateValue(vDate);

			//######### Force Match (Date) Filter Bar-> Force Matched Tab #######
			this.byId("FMForceMDateId").setMaxDate(vDate);
			this.byId("FMForceMDateId").setDateValue(vDate);

			//######### Force Match (Date) Filter Bar-> Additional Entries Tab #######
			this.byId("idFMAdnlEntrsDate").setMaxDate(vDate);
			this.byId("idFMAdnlEntrsDate").setDateValue(vDate);

			//######### Vendor Summay - Recon Summary #######
			this.byId("ReconSummDateId").setMaxDate(vDate);
			this.byId("ReconSummDateId").setDateValue(vDate);

			//######### Vendor Summay - Recon Response #######
			this.byId("ReconRespDateId").setMaxDate(vDate);
			this.byId("ReconRespDateId").setDateValue(vDate);
			//this._bindDefaultData();
			//this.oConsldModel = new sap.ui.model.json.JSONModel("model/reconResult.json");
			this.byId("iVenSummaryPerioed1").setMaxDate(vDate);
			this.byId("iVenSummaryPerioed1").setDateValue(vDate);

			this.byId("ReconSummDateId").addEventDelegate({
				onAfterRendering: function () {
					that.byId("ReconSummDateId").$().find("input").attr("readonly", true);
				}
			});

			this.byId("ReconRespDateId").addEventDelegate({
				onAfterRendering: function () {
					that.byId("ReconRespDateId").$().find("input").attr("readonly", true);
				}
			});

		},

		_onRouteMatched: function (oEvent) {
			var oName = oEvent.getParameter("name");
			var oContextPath = oEvent.getParameter("arguments").contextPath;
			if (oName === "ANX2") {

			} else {

			}
		},
		//=================================================================================================
		//---------------- Code Start By Rakesh for ANX-2 -------------------------------------------------
		//=================================================================================================	

		fnPressDownInit: function () {
			this.getView().byId("iMoreIniat").setVisible(false);
			this.getView().byId("iLessIniat").setVisible(true);

			this.getView().byId("idvbox1").setVisible(false);
			this.getView().byId("idvbox2").setVisible(false);
			this.getView().byId("idGrid1").setVisible(false);
			this.getView().byId("idGrid2").setVisible(false);

		},

		fnPressRightInit: function () {
			this.getView().byId("iLessIniat").setVisible(false);
			this.getView().byId("iMoreIniat").setVisible(true);

			this.getView().byId("idvbox1").setVisible(true);
			this.getView().byId("idvbox2").setVisible(true);
			this.getView().byId("idGrid1").setVisible(true);
			this.getView().byId("idGrid2").setVisible(true);
		},

		////////////////////////////////////////////////////////////////////////////////////////////
		////////////////Table Visible true/false - Data for Recon//////////////////////////////////
		fnPressDownInitR: function () {
			this.getView().byId("iMoreIniatR").setVisible(false);
			this.getView().byId("id2APRSummaryTable1").setVisible(false);
			this.getView().byId("iLessIniatR").setVisible(true);
		},

		fnPressRightInitR: function () {
			this.getView().byId("iLessIniatR").setVisible(false);
			this.getView().byId("id2APRSummaryTable1").setVisible(true);
			this.getView().byId("iMoreIniatR").setVisible(true);
		},

		////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////// User click on Request ID wise tab//////////////////////////////////////
		onPressRequestIDwise: function (oEvent) {
			this.getView().byId("idRequestIDwisePage").setVisible(true);
			this.getView().byId("idSplitDtl").setVisible(false);
			//this.uName = this.getOwnerComponent().getModel("UserInfo").oData.groupName;
			var oReqWiseInfo = {
				"userName": "SYSTEM"
			};

			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/aspsapapi/getAnx2ReportRequestStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "ReqWiseData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//added by Vinay 11-12-2019//
		onRefreshRequestIDwise: function () {
			this.onPressRequestIDwise();
		},

		////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////  Click on No.of GSTIN's column of Request Id Wise Table/////////////
		onPressGSTIN: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("ReqWiseData").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData").getObject().requestId;
			for (var i = 0; i < TabData.resp.requestDetails.length; i++) {
				if (reqId === TabData.resp.requestDetails[i].requestId) {
					gstins.push(TabData.resp.requestDetails[i].gstins);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx2.Popover", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins");
			this._oGstinPopover.openBy(oButton);
		},

		/*	onCloseDialog: function () {
				this._oGstinDialog.close();
			},*/

		////////////////////////////////////////////////////////////////////////////////////////////
		// User click on Back Button of Request ID wise 
		onPressRequestIDwiseBack: function () {
			this.getView().byId("idRequestIDwisePage").setVisible(false);
			this.getView().byId("idSplitDtl").setVisible(true);
		},

		////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////Initiate Matching//////////////////////////////////////////
		fnIntiniateBtnPress: function () {
			var that = this;
			//var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			MessageBox.confirm("Are you sure you want to Initiate Matching", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that.onInitiateMatch();
					}
				}
			});

		},

		onInitiateMatch: function () {
			var oPath = [];
			var aGSTIN = [];
			var oView = this.getView();
			var oModelData = oView.getModel("GSTIN").getData();
			oPath = oView.byId("idInitiateReconList").getSelectedContextPaths();
			/*for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}*/
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			var infoReports = [];
			if (this.byId("oIdPoteMatch").getSelected()) {
				infoReports.push(this.byId("oIdPoteMatch").getText());
			}
			if (this.byId("oIReconSummary").getSelected()) {
				infoReports.push(this.byId("oIReconSummary").getText());
			}
			if (this.byId("oIdVendorStat").getSelected()) {
				infoReports.push(this.byId("oIdVendorStat").getText());
			}
			if (this.byId("oIdDropOut").getSelected()) {
				infoReports.push(this.byId("oIdDropOut").getText());
			}

			var oIntiData = {
				"entityId": Number($.sap.entityID),
				"gstins": aGSTIN, //"18ABCDE1234P2Z1"], 
				"infoReports": infoReports,
				"taxPeriod": this.byId("idInitiateReconPeriod").getValue()
			};

			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			var oIniPath = "/aspsapapi/anx2InitiateMatching.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					MessageBox.confirm("Please click on Request ID wise Link", {
						title: "Initiate Matching Successfully done"
					});
					oIniModel.setData(data);
					oIniView.setModel(oIniModel, "IniData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onConfigExtractPress: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData").getObject().requestId;
			var oReqExcelPath = "/aspsapapi/downloadDocument.do?configId=" + this.oReqId + "";
			window.open(oReqExcelPath);
		},

		/*	onConfigExtractPress: function (oEvt) {
				this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData").getObject().requestId;

				var oExcelDwnInfo = {
					//"req": {
					"configId": this.oReqId
						//}
				};

				var oExcelModel = new JSONModel();
				var oExcelView = this.getView();
				var oReqExcelPath = "/aspsapapi/downloadDocument.do"; //"?configId=20199000031 

			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			var oIniPath = "/aspsapapi/anx2InitiateMatching.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					sap.m.MessageBox.confirm("Please click on Request ID wise Link", {
						title: "Initiate Matching Successfully done"
					});
					oIniModel.setData(data);
					oIniView.setModel(oIniModel, "IniData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

			},*/
		//=================================================================================================
		//---------------- Code End By Rakesh for ANX-2 -------------------------------------------------
		//=================================================================================================	

		//=================================================================================================
		//---------------- Code Start By Sarvamangla for ANX-2 --------------------------------------------
		//=================================================================================================	

		onMenuItemPressAnx2down: function (oEvent) {
			var vSelectedKey = oEvent.getParameter("item").getKey();
			var vTaxPeriod = this.getView().byId("idfgiTaxPeriod").getValue();
			if (vTaxPeriod == "") {
				vTaxPeriod = null;
			}
			var url = "";
			var postPayload = {
				"req": {
					"type": "",
					"entityId": [$.sap.entityID],
					"taxPeriod": vTaxPeriod,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idfgiGSINTComboMain").getSelectedKeys()
							// "Plant": this.oView.byId("slPlant").getSelectedKeys(),
							// "PC": this.oView.byId("slProfitCtr").getSelectedKeys(),
							// "D": this.oView.byId("slDivision").getSelectedKeys(),
							// "L": this.oView.byId("slLocation").getSelectedKeys(),
							// "PO": this.oView.byId("slPurcOrg").getSelectedKeys(),
							// "UD1": this.oView.byId("slUserAccess1").getSelectedKeys(),
							// "UD2": this.oView.byId("slUserAccess2").getSelectedKeys(),
							// "UD3": this.oView.byId("slUserAccess3").getSelectedKeys(),
							// "UD4": this.oView.byId("slUserAccess4").getSelectedKeys(),
							// "UD5": this.oView.byId("slUserAccess5").getSelectedKeys(),
							// "UD6": this.oView.byId("slUserAccess6").getSelectedKeys()
					}
				}
			};

			if (vSelectedKey === "CurrentPeriod") {
				postPayload.req.type = "prSummaryProcessedCurrent";
				url = "/aspsapapi/downloadPRReports.do";
			}

			if (url != "") {
				this.excelDownload(postPayload, url);
			}

		},

		//this method for select all and remove all GSTIN from initiate recon GSTIN list add by Ram on 11-12-2019
		onSelectallGSTIN: function (oEvent) {
			//eslint-disable-line
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList").selectAll();
			}
			this.onInitiateRecon();
		},

		onChangeSegmentStatus: function (oEvent) {
			//eslint-disable-line

			var selSeg = oEvent.getSource().getSelectedKey();

			if (selSeg === "LSS") {
				this.getView().byId("idtitle1").setVisible(false);
				this.getView().byId("idtittle2").setVisible(true);
				this.getView().byId("idlastScuess").setVisible(true);
				this.getView().byId("idgetVtable").setVisible(false);

			} else {

				this.getView().byId("idtitle1").setVisible(true);
				this.getView().byId("idtittle2").setVisible(false);
				this.getView().byId("idlastScuess").setVisible(false);
				this.getView().byId("idgetVtable").setVisible(true);
			}

		},

		OnpressGetStatus: function (oEvent) {
			//eslint-disable-line

			// var vEntitySucess = this.getView().byId("idGetANX22AEntity").getSelectedKey();
			// var vTPeriodSucess = this.getView().byId("idGetfgiTaxPeriod").getValue();

			var postDataAnx2Status = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": "112017"
				}
			};
			this.getAnx2StatusSummaryDataAPI(postDataAnx2Status);

			if (!this._oDialogSucessStatus) {
				this._oDialogSucessStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx2.StatusGetAnx2", this);
				this.getView().addDependent(this._oDialogSucessStatus);
			}
			this._oDialogSucessStatus.open();

		},
		onCloseDialog: function () {
			this._oDialogSucessStatus.close();
			// sap.ui.getCore().byId("dDifference").destroy();

		},

		getAnx2StatusSummaryDataAPI: function (postDataAnx2Status) {
			// eslint-disable-line
			var oView = this.getView();
			var that = this;
			var oReqWiseVenApiInfo = "/aspsapapi/getAnx2DetailStatus.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWiseVenApiInfo,
					contentType: "application/json",
					data: JSON.stringify(postDataAnx2Status)
				}).done(function (data, status, jqXHR) {

					sap.ui.core.BusyIndicator.hide();
					that.getVendorSummaryDataFinal(data);
					var oAnx2Sucess = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oAnx2Sucess, "Anx2Sucess");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},

		////////////Vendor Summary Function for Changing the Segment Button///////////
		onSelectionChangeVenSum: function (oEvent) {

			if (oEvent.getSource().getSelectedKey() === "PRSummary") {
				this.byId("PRSummID").setVisible(true);
				this.byId("A2SummID").setVisible(false);
				this.byId("RecSummID").setVisible(false);
				this.byId("RecRespID").setVisible(false);
				this.byId("Ret1ID").setVisible(false);

				this.byId("PRsummFilterID").setVisible(true);
				this.byId("A2SummFilterID").setVisible(false);
				this.byId("RecSummFilterId").setVisible(false);
				this.byId("RecRespFilterId").setVisible(false);
				this.byId("RETFilterId").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "ANX2Summary") {
				this.byId("PRSummID").setVisible(false);
				this.byId("A2SummID").setVisible(true);
				this.byId("RecSummID").setVisible(false);
				this.byId("RecRespID").setVisible(false);
				this.byId("Ret1ID").setVisible(false);

				this.byId("PRsummFilterID").setVisible(false);
				this.byId("A2SummFilterID").setVisible(true);
				this.byId("RecSummFilterId").setVisible(false);
				this.byId("RecRespFilterId").setVisible(false);
				this.byId("RETFilterId").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "ReconSummary") {
				this.byId("PRSummID").setVisible(false);
				this.byId("A2SummID").setVisible(false);
				this.byId("RecSummID").setVisible(true);
				this.byId("RecRespID").setVisible(false);
				this.byId("Ret1ID").setVisible(false);

				this.byId("PRsummFilterID").setVisible(false);
				this.byId("A2SummFilterID").setVisible(false);
				this.byId("RecSummFilterId").setVisible(true);
				this.byId("RecRespFilterId").setVisible(false);
				this.byId("RETFilterId").setVisible(false);
				this.onReconSummaryTabBind();
			} else if (oEvent.getSource().getSelectedKey() === "ReconResponse") {
				this.byId("PRSummID").setVisible(false);
				this.byId("A2SummID").setVisible(false);
				this.byId("RecSummID").setVisible(false);
				this.byId("RecRespID").setVisible(true);
				this.byId("Ret1ID").setVisible(false);
				this.byId("PRsummFilterID").setVisible(false);
				this.byId("A2SummFilterID").setVisible(false);
				this.byId("RecSummFilterId").setVisible(false);
				this.byId("RecRespFilterId").setVisible(true);
				this.byId("RETFilterId").setVisible(false);
				this.onRecRespGO();
			} else if (oEvent.getSource().getSelectedKey() === "ImpactonRET1") {
				this.byId("PRSummID").setVisible(false);
				this.byId("A2SummID").setVisible(false);
				this.byId("RecSummID").setVisible(false);
				this.byId("RecRespID").setVisible(false);
				this.byId("Ret1ID").setVisible(true);
				this.byId("PRsummFilterID").setVisible(false);
				this.byId("A2SummFilterID").setVisible(false);
				this.byId("RecSummFilterId").setVisible(false);
				this.byId("RecRespFilterId").setVisible(false);
				this.byId("RETFilterId").setVisible(true);

			}
		},

		handleIconTabBarSelect: function (oEvent) {
			debugger;
			if (oEvent.getParameters().selectedKey === "PRSummary") {
				this.getPRSProcessedData("");
				this.fnMoreLessPressFinalPR(false);
			} else if (oEvent.getParameters().selectedKey === "GETANX22A") {
				this.getGet2aProcessedData();
			} else if (oEvent.getParameters().selectedKey === "InitiateRecon") {
				this.onPressGoForGSTIN();
			} else if (oEvent.getParameters().selectedKey === "ReconResults") {
				this.onPressGoForGSTINRR();
			} else if (oEvent.getParameters().selectedKey === "RET1Impact") {

			} else if (oEvent.getParameters().selectedKey === "ANX2Summary") {
				this.getPRSSaveAnx2Data("");

			} else if (oEvent.getParameters().selectedKey === "VendorSummary") {
				this.onPressVendorSummaryGO();
			} else if (oEvent.getParameters().selectedKey === "VendorCommunication") {

			} else if (oEvent.getParameters().selectedKey === "CompareResults") {

			} else if (oEvent.getParameters().selectedKey === "ReconReports") {

				///////////added ny Vinay 03-12-19///////////////	
			} else if (oEvent.getParameters().selectedKey === "ReconResponse1") {
				//this.onSummaryEntityChange();
				this.ReconResponseData();
			} else if (oEvent.getParameters().selectedKey === "ReconSummary") {
				this.onReconSummGO();
			} else if (oEvent.getParameters().selectedKey === "ReconResult") { //added by Rakesh 17-01-2020////
				if (this.consGStin !== undefined) {
					this.getView().byId("idRRConsGstins").setSelectedKeys(this.consGStin);
					var oMultiGstinInfo = {
						req: {
							"entityId": $.sap.entityID,
							"taxPeriod": this.getView().byId("dtConsld").getValue(),
							"gstins": this.getView().byId("idRRConsGstins").getSelectedKeys()
						}
					};
					this._rePortType(oMultiGstinInfo);
				} else {
					this.onSelcChgeGstin();
				}

				if (this.ExactGSTIN != undefined) {
					this.byId("idRRExctMatchGstins").setSelectedKeys(this.ExactGSTIN);
				}

				if (this.MUTGSTIN != undefined) {
					this.byId("idRRMatchUTGstins").setSelectedKeys(this.MUTGSTIN);

				}

				if (this.DTMGSTIN != undefined) {
					this.byId("idRRDTMGstins").setSelectedKeys(this.DTMGSTIN);
				}

				if (this.DDMGSTIN != undefined) {
					this.byId("idRRDDMGstins").setSelectedKeys(this.DDMGSTIN);
				}

				if (this.ValMGSTIN != undefined) {
					this.byId("idRRValMGstins").setSelectedKeys(this.ValMGSTIN);
				}

				if (this.POSMGSTIN != undefined) {
					this.byId("idRRPOSMGstins").setSelectedKeys(this.POSMGSTIN);
				}

				if (this.MultiMGSTIN != undefined) {
					this.byId("idRRMultiMGstins").setSelectedKeys(this.MultiMGSTIN);
				}

				if (this.FuzyMGSTIN != undefined) {
					this.byId("idRRFuzzyMGstins").setSelectedKeys(this.FuzyMGSTIN);
				}

				if (this.FMForceGSTIN != undefined) {
					this.byId("idRRFMForceMGstins").setSelectedKeys(this.FMForceGSTIN);
				}

				if (this.FMAddnlGSTIN != undefined) {
					this.byId("idFMAdnlEntrsGstins").setSelectedKeys(this.FMAddnlGSTIN);
				}

				if (this.AddtnANX2GSTIN != undefined) {
					this.byId("idRRAddtnANX2Gstins").setSelectedKeys(this.AddtnANX2GSTIN);
				}

				if (this.AddtnPRGSTIN != undefined) {
					this.byId("idRRAddtnPRGstins").setSelectedKeys(this.AddtnPRGSTIN);
				}

				this.onRRConsoldGO();
			}
		},

		/*===========================================================*/
		/*========= File Upload  ====================================*/
		/*===========================================================*/
		handleUploadPress: function (oEvent) {
			var oFileUploader = this.byId("ucGetFileUpload");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.setUploadUrl("/aspsapapi/anx2GetAnx2FileUploadDocuments.do");
			oFileUploader.upload();
		},

		/*===========================================================*/
		/*========= Upload Complete =================================*/
		/*===========================================================*/
		handleUploadComplete: function (oEvent) {
			//eslint-disable-line
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("ucGetFileUpload").setValue(null);
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				MessageBox.error(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			}
		},

		onPressSelectionEYGSTN: function (oEvent) {
			//eslint-disable-line
			var oHeader = this.getView().byId("idGettabRet11mm").getModel("ANX2Header").getData();
			var oTableData = this.getView().getModel("Anx2SummaryDetails").getData();
			var oData = {};
			oData.resp = oTableData;

			if (oEvent.getSource().getSelectedKey() === "EYSummary") {
				this.getANX2HeaderSummaryData(oData);
				this.getView().byId("iMore").setVisible(true);
				this.getView().byId("iLess").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "GSTNSummary") {
				oHeader.threeA.Total.taxableValue = oTableData.GSTIN.taxableValue;
				oHeader.threeA.Total.igst = oTableData.GSTIN.igst;
				oHeader.threeA.Total.cgst = oTableData.GSTIN.cgst;
				oHeader.threeA.Total.sgst = oTableData.GSTIN.sgst;
				oHeader.threeA.Total.cess = oTableData.GSTIN.cess;
				oHeader.ISD.Total.taxableValue = oTableData.GSTIN.isdTaxableValue;
				oHeader.ISD.Total.igst = oTableData.GSTIN.isdIgst;
				oHeader.ISD.Total.cgst = oTableData.GSTIN.isdCgst;
				oHeader.ISD.Total.sgst = oTableData.GSTIN.isdSgst;
				oHeader.ISD.Total.cess = oTableData.GSTIN.isdCess;
				this.fnMoreLessPressFinal(false);
				this.getView().byId("iMore").setVisible(false);
				this.getView().byId("iLess").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Difference") {
				this.getANX2HeaderSummaryData(oData);
				var oHeader = this.getView().byId("idGettabRet11mm").getModel("ANX2Header").getData();
				oHeader.threeA.Total.taxableValue -= oTableData.GSTIN.taxableValue;
				oHeader.threeA.Total.igst -= oTableData.GSTIN.igst;
				oHeader.threeA.Total.cgst -= oTableData.GSTIN.cgst;
				oHeader.threeA.Total.sgst -= oTableData.GSTIN.sgst;
				oHeader.threeA.Total.cess -= oTableData.GSTIN.cess;
				oHeader.ISD.Total.taxableValue -= oTableData.GSTIN.isdTaxableValue;
				oHeader.ISD.Total.igst -= oTableData.GSTIN.isdIgst;
				oHeader.ISD.Total.cgst -= oTableData.GSTIN.isdCgst;
				oHeader.ISD.Total.sgst -= oTableData.GSTIN.isdSgst;
				oHeader.ISD.Total.cess -= oTableData.GSTIN.isdCess;
				this.fnMoreLessPressFinal(false);
				this.getView().byId("iMore").setVisible(false);
				this.getView().byId("iLess").setVisible(false);
			}

			this.getView().byId("idGettabRet11mm").getModel("ANX2Header").refresh();
		},

		onSelectionChange: function (oEvent) {
			//eslint-disable-line
			var vId = oEvent.getSource().getId();

			if (vId.includes("idfgiEntity")) {
				var oEntity = this.getView().byId("idfgiEntity").getSelectedKey();
				this._getDataSecurity(oEntity, "idfbPRSummary");

			} else if (vId.includes("idfgiDEntity")) {
				var oEntitySum = this.getView().byId("idfgiDEntity").getSelectedKey();
				this._getDataSecurity(oEntitySum, "idfbPRSummaryD");

			} else if (vId.includes("idGetANX22AEntity")) {
				oEntitySum = this.getView().byId("idGetANX22AEntity").getSelectedKey();
				this._getDataSecurity(oEntitySum, "idfbGETANX2");

			} else if (vId.includes("idSDGetANX22AEntity")) {
				oEntitySum = this.getView().byId("idSDGetANX22AEntity").getSelectedKey();
				this._getDataSecurity(oEntitySum, "idfbGETANX2");

			} else if (vId.includes("slVenSummaryEntity")) {
				oEntitySum = this.getView().byId("slVenSummaryEntity").getSelectedKey();
				this._getDataSecurity(oEntitySum);
			} else if (vId.includes("ReconSumEntityID")) {
				//oEntitySum = this.getView().byId("ReconSumEntityID").getSelectedKey();
				//this._getDataSecurity(oEntitySum, "reconsumId");
			}
		},

		onPressAdaptFilters: function (oEvent) {
			//var aData;
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx2.AdaptAnx2", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			/*if (oEvent.getSource().getId().includes("bAdaptFilter")) {
				aData = this.getView().getModel("DataSecurity");

			} else {
				aData = this.getView().byId("reconsumId").getModel("DataSecurity");

			}
			// var oDropDown = new sap.ui.model.json.JSONModel(aData);
			this.getView().byId("dAdapt").setModel(aData, "DropDown");*/
			this._oAdpatFilter.open();
		},

		onPressFilterApply: function (oEvent) {
			var oText = this.getView().byId("idPRSummaryMainTitle").getText();
			if (oText === "Processed Data") {
				this.onPressFilterApplyGetPR();
				this.onPressReconSummGo();
			} else {
				this.onPressFilterApplyGetSummaryDetails();
			}
			this._oAdpatFilter.close();
		},

		onPressFilterClose: function (oEvent) {
			this._oAdpatFilter.close();
		},

		onSearch: function (oEvent) {
			var oView = this.getView();
			var vId = oEvent.getSource().getId();
			if (vId.includes("idButPRSummary")) {
				var oText = oView.byId("idPRSummaryMainTitle").getText();
				if (oText === "Processed Data") {
					this.getPRSProcessedData("");
				} else {
					this.getPRSummaryDetails();
				}
			} else if (vId.includes("idfbGETANX2")) {
				this.getGet2aProcessedData();
			} else if (vId.includes("idSDfbGETANX2")) {
				this.getGet2aSummaryDetails();
			} else if (vId.includes("idRWISearch")) {
				this.RWIonPressGO();
			}
		},

		getPRSProcessedData: function (oEntity) {

			var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"docType": this.oView.byId("idComcoDocType").getSelectedKeys(),
					"recordType": this.oView.byId("idComcoTableType").getSelectedKeys(),
					"fromDate": null,
					"toDate": null,
					"data": [],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idfgiGSINTComboMain").getSelectedKeys(),
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getPRSProcessedDataFinal(postData);
		},

		getPRSProcessedDataFinal: function (postData) {

			var oView = this.getView();

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getPRSProcessedData.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oPRSProcessedData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oPRSProcessedData, "PRSProcessedData");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getPRSProcessedData : Error");
				});
			});
		},

		onPressFilterApplyGetPR: function (oEvent) {
			//eslint-disable-line
			var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"docType": this.oView.byId("idComcoDocType").getSelectedKeys(),
					"recordType": this.oView.byId("idComcoTableType").getSelectedKeys(),
					"fromDate": null,
					"toDate": null,
					"data": [],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idfgiGSINTComboMain").getSelectedKeys(),
						"Plant": this.oView.byId("slPlant").getSelectedKeys(),
						"PC": this.oView.byId("slProfitCtr").getSelectedKeys(),
						"D": this.oView.byId("slDivision").getSelectedKeys(),
						"L": this.oView.byId("slLocation").getSelectedKeys(),
						"PO": this.oView.byId("slPurcOrg").getSelectedKeys(),
						"UD1": this.oView.byId("slUserAccess1").getSelectedKeys(),
						"UD2": this.oView.byId("slUserAccess2").getSelectedKeys(),
						"UD3": this.oView.byId("slUserAccess3").getSelectedKeys(),
						"UD4": this.oView.byId("slUserAccess4").getSelectedKeys(),
						"UD5": this.oView.byId("slUserAccess5").getSelectedKeys(),
						"UD6": this.oView.byId("slUserAccess6").getSelectedKeys()
					}
				}
			};

			this.getPRSProcessedDataFinal(postData);
		},
		onPressFilterApplyGetSummaryDetails: function (oEvent) {
			//eslint-disable-line
			var oTaxPeriod = this.oView.byId("idfgiTaxPeriodD").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"docType": this.oView.byId("idComcoDocTypeIn").getSelectedKeys(),
					"recordType": this.oView.byId("idComcoTableTypeIn").getSelectedKeys(),
					"fromDate": null,
					"toDate": null,
					"data": [],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idfgiGSINTComboMainD").getSelectedKeys(),
						"Plant": this.oView.byId("slPlant").getSelectedKeys(),
						"PC": this.oView.byId("slProfitCtr").getSelectedKeys(),
						"D": this.oView.byId("slDivision").getSelectedKeys(),
						"L": this.oView.byId("slLocation").getSelectedKeys(),
						"PO": this.oView.byId("slPurcOrg").getSelectedKeys(),
						"UD1": this.oView.byId("slUserAccess1").getSelectedKeys(),
						"UD2": this.oView.byId("slUserAccess2").getSelectedKeys(),
						"UD3": this.oView.byId("slUserAccess3").getSelectedKeys(),
						"UD4": this.oView.byId("slUserAccess4").getSelectedKeys(),
						"UD5": this.oView.byId("slUserAccess5").getSelectedKeys(),
						"UD6": this.oView.byId("slUserAccess6").getSelectedKeys()
					}
				}
			};

			this.getPRSDetail(postData);
			//	this.clearAdpatFilter();
		},

		handleLinkPressGSTINMainBack: function (oEvent) {
			this.oView.byId("idPREntity").setVisible(true);
			this.oView.byId("idPRGSTIN").setVisible(false);
			this.oView.byId("idPRGSTIN1").setVisible(false);
			this.oView.byId("idfbPRSummary").setVisible(true);
			this.oView.byId("idfbPRSummaryD").setVisible(false);
			this.oView.byId("idPRSummaryMainNavBack").setVisible(false);
			this.oView.byId("idPRSummaryMainTitle").setText("Processed Data");
		},

		handleLinkPressGSTINMain: function (oEvent) {
			//eslint-disable-line
			var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var aData = [];
			var a = oEvent.getSource().getText();
			aData.push(a);
			this.oView.byId("idfgiTaxPeriodD").setValue(oTaxPeriod);
			// var oEntity = this.oView.byId("idfgiEntity").getSelectedKey();
			// this.oView.byId("idfgiDEntity").setSelectedKey(oEntity);
			// this._getDataSecurity(oEntity, "idfbPRSummaryD");
			this.oView.byId("idfgiGSINTComboMainD").setSelectedKeys(aData);
			this.oView.byId("idPREntity").setVisible(false);
			this.oView.byId("idPRGSTIN").setVisible(true);
			this.oView.byId("idPRGSTIN1").setVisible(true);
			this.oView.byId("idfbPRSummary").setVisible(false);
			this.oView.byId("idfbPRSummaryD").setVisible(true);
			this.oView.byId("idPRSummaryMainNavBack").setVisible(true);
			this.oView.byId("idPRSummaryMainTitle").setText("Review Summary");

			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"docType": [],
					"recordType": [],
					"fromDate": null,
					"toDate": null,
					"data": [],
					"dataSecAttrs": {
						"GSTIN": aData,
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getPRSDetail(postData);
		},

		getPRSDetail: function (postData) {
			//eslint-disable-line
			var that = this;
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getPRSDetail.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oPRSDetail = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oPRSDetail, "PRSDetail");
					that.getHeaderSummaryData(data);
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getPRSDetail : Error");
				});
			});
		},

		getHeaderSummaryData: function (data) {
			//eslint-disable-line
			var aPRHeaderData = this._DataReturn();
			for (var i = 0; i < data.resp.length; i++) {
				if (data.resp[i].table === "5-ISD") {
					aPRHeaderData.ISD.Total.taxableValue += data.resp[i].taxableValue;
					aPRHeaderData.ISD.Total.igst += data.resp[i].IGST;
					aPRHeaderData.ISD.Total.cgst += data.resp[i].CGST;
					aPRHeaderData.ISD.Total.sgst += data.resp[i].SGST;
					aPRHeaderData.ISD.Total.cess += data.resp[i].Cess;
					for (var j = 0; j < data.resp[i].items.length; j++) {
						if (data.resp[i].items[j].table === "Invoice") {
							aPRHeaderData.ISD.INV.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.ISD.INV.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.ISD.INV.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.ISD.INV.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.ISD.INV.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Credit Note") {
							aPRHeaderData.ISD.CR.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.ISD.CR.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.ISD.CR.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.ISD.CR.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.ISD.CR.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Invoice-Amendment") {
							aPRHeaderData.ISD.INVA.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.ISD.INVA.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.ISD.INVA.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.ISD.INVA.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.ISD.INVA.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Credit Note-Amendment") {
							aPRHeaderData.ISD.CRA.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.ISD.CRA.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.ISD.CRA.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.ISD.CRA.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.ISD.CRA.cess += data.resp[i].items[j].Cess;

						}
					}
				} else {

					aPRHeaderData.threeA.Total.taxableValue += data.resp[i].taxableValue;
					aPRHeaderData.threeA.Total.igst += data.resp[i].IGST;
					aPRHeaderData.threeA.Total.cgst += data.resp[i].CGST;
					aPRHeaderData.threeA.Total.sgst += data.resp[i].SGST;
					aPRHeaderData.threeA.Total.cess += data.resp[i].Cess;
					for (var j = 0; j < data.resp[i].items.length; j++) {
						if (data.resp[i].items[j].table === "Invoice") {
							aPRHeaderData.threeA.INV.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.INV.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.INV.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.INV.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.INV.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Credit Note") {
							aPRHeaderData.threeA.CR.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.CR.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.CR.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.CR.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.CR.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Debit Note") {
							aPRHeaderData.threeA.DR.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.DR.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.DR.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.DR.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.DR.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Invoice-Amendment") {
							aPRHeaderData.threeA.INVA.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.INVA.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.INVA.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.INVA.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.INVA.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Credit Note-Amendment") {
							aPRHeaderData.threeA.CRA.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.CRA.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.CRA.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.CRA.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.CRA.cess += data.resp[i].items[j].Cess;

						} else if (data.resp[i].items[j].table === "Debit Note-Amendment") {
							aPRHeaderData.threeA.DRA.taxableValue += data.resp[i].items[j].taxableValue;
							aPRHeaderData.threeA.DRA.igst += data.resp[i].items[j].IGST;
							aPRHeaderData.threeA.DRA.cgst += data.resp[i].items[j].CGST;
							aPRHeaderData.threeA.DRA.sgst += data.resp[i].items[j].SGST;
							aPRHeaderData.threeA.DRA.cess += data.resp[i].items[j].Cess;

						}
					}
				}
			}
			var oPRHeader = new sap.ui.model.json.JSONModel(aPRHeaderData);
			this.getView().byId("idPRGSTIN").setModel(oPRHeader, "PRHeader");
		},

		getANX2HeaderSummaryData: function (data) {
			//eslint-disable-line
			var aANX2HeaderData = this._DataReturn();
			for (var i = 0; i < data.resp.table.length; i++) {
				if (data.resp.table[i].table === "5-ISD") {
					aANX2HeaderData.ISD.Total.taxableValue += data.resp.table[i].taxableValue;
					aANX2HeaderData.ISD.Total.igst += data.resp.table[i].igst;
					aANX2HeaderData.ISD.Total.cgst += data.resp.table[i].cgst;
					aANX2HeaderData.ISD.Total.sgst += data.resp.table[i].sgst;
					aANX2HeaderData.ISD.Total.cess += data.resp.table[i].cess;
					for (var j = 0; j < data.resp.table[i].items.length; j++) {
						if (data.resp.table[i].items[j].table === "Invoice") {
							aANX2HeaderData.ISD.INV.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.ISD.INV.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.ISD.INV.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.ISD.INV.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.ISD.INV.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Credit Note") {
							aANX2HeaderData.ISD.CR.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.ISD.CR.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.ISD.CR.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.ISD.CR.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.ISD.CR.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Invoice-Amendment") {
							aANX2HeaderData.ISD.INVA.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.ISD.INVA.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.ISD.INVA.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.ISD.INVA.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.ISD.INVA.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Credit Note-Amendment") {
							aANX2HeaderData.ISD.CRA.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.ISD.CRA.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.ISD.CRA.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.ISD.CRA.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.ISD.CRA.cess += data.resp.table[i].items[j].cess;

						}
					}
				} else if (data.resp.table[i].table === "3G-Deemed Exports" || data.resp.table[i].table === "3B-B2B" || data.resp.table[i].table ===
					"3E-SEZWP" || data.resp.table[i].table === "3F-SEZWOP") {
					aANX2HeaderData.threeA.Total.taxableValue += data.resp.table[i].taxableValue;
					aANX2HeaderData.threeA.Total.igst += data.resp.table[i].igst;
					aANX2HeaderData.threeA.Total.cgst += data.resp.table[i].cgst;
					aANX2HeaderData.threeA.Total.sgst += data.resp.table[i].sgst;
					aANX2HeaderData.threeA.Total.cess += data.resp.table[i].cess;
					for (var j = 0; j < data.resp.table[i].items.length; j++) {
						if (data.resp.table[i].items[j].table === "Invoice") {
							aANX2HeaderData.threeA.INV.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.INV.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.INV.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.INV.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.INV.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Credit Note") {
							aANX2HeaderData.threeA.CR.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.CR.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.CR.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.CR.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.CR.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Debit Note") {
							aANX2HeaderData.threeA.DR.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.DR.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.DR.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.DR.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.DR.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Invoice-Amendment") {
							aANX2HeaderData.threeA.INVA.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.INVA.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.INVA.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.INVA.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.INVA.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Credit Note-Amendment") {
							aANX2HeaderData.threeA.CRA.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.CRA.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.CRA.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.CRA.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.CRA.cess += data.resp.table[i].items[j].cess;

						} else if (data.resp.table[i].items[j].table === "Debit Note-Amendment") {
							aANX2HeaderData.threeA.DRA.taxableValue += data.resp.table[i].items[j].taxableValue;
							aANX2HeaderData.threeA.DRA.igst += data.resp.table[i].items[j].igst;
							aANX2HeaderData.threeA.DRA.cgst += data.resp.table[i].items[j].cgst;
							aANX2HeaderData.threeA.DRA.sgst += data.resp.table[i].items[j].sgst;
							aANX2HeaderData.threeA.DRA.cess += data.resp.table[i].items[j].cess;

						}
					}
				}
			}
			var oANX2Header = new sap.ui.model.json.JSONModel(aANX2HeaderData);
			this.getView().byId("idGettabRet11mm").setModel(oANX2Header, "ANX2Header");
		},

		OnOpenStateAnx2D: function (oEvent) {

			var aData = oEvent.getSource().getBinding().getModel("Anx2SummaryDetails").getData();
			var vModel = oEvent.getSource().getBinding().getPath().split("/")[1];

			var vRowCount = 6;
			var oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded;
			for (var groupId in oGroupId) {
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[vModel][aPath[1]].items.length;
			}
			if (vRowCount > 10) {
				vRowCount = 10;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);

		},

		getPRSummaryDetails: function (oEvent) {
			//eslint-disable-line
			var oTaxPeriod = this.oView.byId("idfgiTaxPeriodD").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": oTaxPeriod,
					"docType": this.oView.byId("idComcoDocTypeIn").getSelectedKeys(),
					"recordType": this.oView.byId("idComcoTableTypeIn").getSelectedKeys(),
					"fromDate": null,
					"toDate": null,
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idfgiGSINTComboMainD").getSelectedKeys(),
						"Plant": [],
						"PC": [],
						"D": [],
						"L": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getPRSDetail(postData);
		},

		onSelectCheckBoxCol1t: function (oEvent) {
			anx2Data.onSelectCheckBoxCol1t(oEvent, this);
		},
		onSelectCheckBoxCol2t: function (oEvent) {
			anx2Data.onSelectCheckBoxCol2t(oEvent, this);
		},

		onSelectCheckBoxCol1: function (oEvent) {
			anx2Data.onSelectCheckBoxCol1(oEvent, this);
		},
		onSelectCheckBoxCol2: function (oEvent) {
			anx2Data.onSelectCheckBoxCol2(oEvent, this);
		},

		onSelectionChangeGSTINm: function (oEvent) {
			if (oEvent.getSource().getSelectedKey() === "Total") {
				this.oView.byId("tabRet1m").setVisible(false);
				this.oView.byId("tabRet11m").setVisible(true);
				this.oView.byId("idHBoxShowing1").setVisible(true);
				this.oView.byId("idHBoxShowing2").setVisible(true);
				this.oView.byId("idHBoxShowing3").setVisible(true);
				this.oView.byId("idHBoxShowing4").setVisible(true);
				this.oView.byId("idHBoxShowing5").setVisible(true);

			} else {
				this.oView.byId("tabRet1m").setVisible(true);
				this.oView.byId("tabRet11m").setVisible(false);
				this.oView.byId("idHBoxShowing1").setVisible(true);
				this.oView.byId("idHBoxShowing2").setVisible(true);
				this.oView.byId("idHBoxShowing3").setVisible(true);
				this.oView.byId("idHBoxShowing4").setVisible(true);
				this.oView.byId("idHBoxShowing5").setVisible(true);
			}
		},

		getGet2aProcessedData: function (oEvent) {
			var oTaxPeriod = this.oView.byId("idGetfgiTaxPeriod").getValue();
			// var oEntity = this.oView.byId("idGetANX22AEntity").getSelectedKey();
			// this._getDataSecurity(oEntity, "idfbGETANX2");
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idGetfgiGSINTComboMain").getSelectedKeys()
					},
					// "gstin": this.oView.byId("idGetfgiGSINTComboMain").getSelectedKeys(),
					"taxPeriod": oTaxPeriod,
					"docType": this.oView.byId("idMultiDoctype").getSelectedKeys(),
					"recordType": this.oView.byId("idMultiTabType").getSelectedKeys(),
					"data": []
				}
			};

			this.getGet2aProcessedDataFinal(postData);
		},

		getGet2aProcessedDataFinal: function (postData) {
			var oView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGet2aProcessedData.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oGet2aProcessedData = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oGet2aProcessedData, "Get2aProcessedData");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGet2aProcessedData : Error");
				});
			});
		},
		onPressGoForGSTIN: function (oEvent) {
			var oView = this.getView();
			var that = this;
			var oRetPeriod = this.oView.byId("idInitiateReconPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}

			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"taxPeriod": oRetPeriod
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					//url: "/aspsapapi/getGstinsForAnx2EntityId.do",
					url: "/aspsapapi/getDataForAnnx2ReconSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oGSTIN = new JSONModel(data.resp.det);
						oView.setModel(oGSTIN, "GSTIN");
						oView.byId("idInitiateReconList").selectAll();
						that.onInitiateRecon();
					} else {
						var oGSTIN1 = new JSONModel([]);
						oView.setModel(oGSTIN1, "GSTIN");
						oView.setModel(oGSTIN1, "InitiateRecon");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "GSTIN");
					oView.setModel(oGSTIN2, "InitiateRecon");
					//MessageBox.error("getGstinsForAnx2EntityId : Error");
				});
			});
		},

		onSelectionChange1: function (oEvent) {
			this.onInitiateRecon();
		},

		onInitiateRecon: function (oEvent) {
			// eslint-disable-line
			var oView = this.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTIN").getData();
			var oRetPeriod = oView.byId("idInitiateReconPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idInitiateReconList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oInitiateRecon = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oInitiateRecon, "InitiateRecon");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					// "gstin": aGSTIN,
					"dataSecAttrs": {
						"GSTIN": aGSTIN
					},
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/initiateRecon.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oInitiateRecon = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oInitiateRecon, "InitiateRecon");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("initiateRecon : Error");
				});
			});
		},

		//=================================================================================================
		//---------------- Code start By sarvmangla for ANX-2 ------------------------------------------------------
		//=================================================================================================

		// onPressSelectionblock: function (oEvent) {
		// 	 //eslint-disable-line	
		// 	var vText = this.getView().byId("idDiffSelect").getSelectedKey("");
		// 	var vDataDiff = this.getView().getModel("Anx2SummaryDetails").getData();
		// 	var vDataDiffEY = this.getView().byId("idGettabRet11mm").getModel("ANX2Header").getData();
		// 	var data = {};
		// 	data.resp = vDataDiff;

		// 	if (vText === "DemoEntity1") {
		// 		vDataDiffEY.threeA.Total.taxableValue = vDataDiff.GSTIN.taxableValue;
		// 		vDataDiffEY.threeA.Total.igst = vDataDiff.GSTIN.igst;
		// 		vDataDiffEY.threeA.Total.cgst = vDataDiff.GSTIN.cgst;
		// 		vDataDiffEY.threeA.Total.sgst = vDataDiff.GSTIN.sgst;
		// 		vDataDiffEY.threeA.Total.cess = vDataDiff.GSTIN.cess;

		// 		this.fnMoreLessPressFinal(false);
		// 		this.getView().byId("iMore").setVisible(false);
		// 		this.getView().byId("iLess").setVisible(false);

		// 	} else if (vText === "DemoEntity") {
		// 		this.getANX2HeaderSummaryData(data);
		// 		this.getView().byId("iMore").setVisible(true);
		// 		this.getView().byId("iLess").setVisible(false);
		// 		this.fnMoreLessPressFinal(false);

		// 	} else {
		// 		this.getANX2HeaderSummaryData(data);
		// 		var vDataDiffEY = this.getView().byId("idGettabRet11mm").getModel("ANX2Header").getData();
		// 		vDataDiffEY.threeA.Total.taxableValue -= vDataDiff.GSTIN.taxableValue;
		// 		vDataDiffEY.threeA.Total.igst -= vDataDiff.GSTIN.igst;
		// 		vDataDiffEY.threeA.Total.cgst -= vDataDiff.GSTIN.cgst;
		// 		vDataDiffEY.threeA.Total.sgst -= vDataDiff.GSTIN.sgst;
		// 		vDataDiffEY.threeA.Total.cess -= vDataDiff.GSTIN.cess;
		// 		this.fnMoreLessPressFinal(false);
		// 		this.getView().byId("iMore").setVisible(false);
		// 		this.getView().byId("iLess").setVisible(false);

		// 	}

		// 	this.getView().byId("idGettabRet11mm").getModel("ANX2Header").refresh();

		// },
		onPressVendorSummaryGO: function () {
			// eslint-disable-line
			var vGetButtonDetail = this.getView().byId("id_VendorSegButtonPress").getSelectedKey();
			var oReqWiseVenInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this.byId("iVenSummaryPerioed").getValue(),
					"vendorPan": [],
					"vendorGstin": [],
					"data": ["Current Period"],
					"dataSecAttrs": {
						"GSTIN": this.byId("slVenSummaryGstin").getSelectedKeys() //["11SUPEY1234A111"]
					}
				}
			};
			if (vGetButtonDetail.includes("PRSummary")) {
				this.getVendorSummaryDataAPI(oReqWiseVenInfo);
			} else {
				this.getVendorANX2SummaryDataAPI(oReqWiseVenInfo);

			}

		},

		getVendorSummaryData: function () {

			var oReqWiseVenInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": "092019",
					"vendorPan": [],
					"vendorGstin": [],
					"data": ["Current Period"],
					"dataSecAttrs": {
						"GSTIN": ["11SUPEY1234A111"]
					}
				}
			};
			this.getVendorSummaryDataAPI(oReqWiseVenInfo);
		},

		getVendorANX2SummaryDataAPI: function (oReqWiseVenInfo) {
			// eslint-disable-line
			// this.getView().byId("idRequestIDwisePage").setVisible(true);
			// this.getView().byId("idSplitDtl").setVisible(false);
			var oView = this.getView();
			var that = this;
			var oReqWiseVenApiInfo = "/aspsapapi/getVenorANX2Sumary.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWiseVenApiInfo,
					contentType: "application/json",
					data: JSON.stringify(oReqWiseVenInfo)
				}).done(function (data, status, jqXHR) {

					sap.ui.core.BusyIndicator.hide();
					that.getVendorSummaryDataFinal(data, "ANX2");
					// var oVenSummary = new sap.ui.model.json.JSONModel(data);
					// oView.setModel(oVenSummary, "VenSummary");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},

		// getVendorANX2SummaryDataFinal: function (data) {
		// 	 // eslint-disable-line
		// 	var oView = this.getView();
		// 	var retArr = [];
		// 	var curL1Obj = {}; // the current level1 object.
		// 	var curL2Obj = {}; // the current level2 object.
		// 	var curL3Obj = {}; // the current level3 object.
		// 	for (var i = 0; i < data.resp.length; i++) {
		// 		var ele = data.resp[i];
		// 		var lvl = ele.level; // Get the level of the cur Obj.
		// 		if (lvl === "L1") {
		// 			curL1Obj = ele;
		// 			retArr.push(curL1Obj);
		// 			curL1Obj.items = [];
		// 		}
		// 		if (lvl === "L2") {
		// 			// delete ele.vendorPAN;
		// 			// delete ele.vendorName;
		// 			curL2Obj = ele;
		// 			curL1Obj.items.push(curL2Obj);
		// 			curL2Obj.items = [];
		// 		}
		// 		if (lvl === "L3") {
		// 			// delete ele.vendorPAN;
		// 			// delete ele.vendorName;
		// 			// delete ele.GSTIN;
		// 			curL3Obj = ele;
		// 			curL2Obj.items.push(ele);
		// 			curL3Obj.items = [];
		// 		}
		// 		if (lvl === "L4") {
		// 			// delete ele.vendorPAN;
		// 			// delete ele.vendorName;
		// 			// delete ele.GSTIN;
		// 			// delete ele.tableType;
		// 			curL3Obj.items.push(ele);
		// 		}
		// 	}

		// 	var oVenANX2Summary = new sap.ui.model.json.JSONModel(retArr);
		// 	oView.setModel(oVenANX2Summary, "VenANX2Summary");

		// },

		getVendorSummaryDataAPI: function (oReqWiseVenInfo) {
			// eslint-disable-line
			// this.getView().byId("idRequestIDwisePage").setVisible(true);
			// this.getView().byId("idSplitDtl").setVisible(false);
			var oView = this.getView();
			var that = this;
			var oReqWiseVenApiInfo = "/aspsapapi/getVenorPRSumary.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWiseVenApiInfo,
					contentType: "application/json",
					data: JSON.stringify(oReqWiseVenInfo)
				}).done(function (data, status, jqXHR) {

					sap.ui.core.BusyIndicator.hide();
					that.getVendorSummaryDataFinal(data, "PR");
					// var oVenSummary = new sap.ui.model.json.JSONModel(data);
					// oView.setModel(oVenSummary, "VenSummary");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},

		getVendorSummaryDataFinal: function (data, type) {
			var oView = this.getView();
			var retArr = [];
			var curL1Obj = {}; // the current level1 object.
			var curL2Obj = {}; // the current level2 object.
			var curL3Obj = {}; // the current level3 object.
			for (var i = 0; i < data.resp.length; i++) {
				var ele = data.resp[i];
				var lvl = ele.level; // Get the level of the cur Obj.
				if (lvl === "L1") {
					curL1Obj = ele;
					retArr.push(curL1Obj);
					curL1Obj.items = [];
				}
				if (lvl === "L2") {
					delete ele.vendorPAN;
					delete ele.vendorName;
					curL2Obj = ele;
					curL1Obj.items.push(curL2Obj);
					curL2Obj.items = [];
				}
				if (lvl === "L3") {
					delete ele.vendorPAN;
					delete ele.vendorName;
					delete ele.GSTIN;
					curL3Obj = ele;
					curL2Obj.items.push(ele);
					curL3Obj.items = [];
				}
				if (lvl === "L4") {
					delete ele.vendorPAN;
					delete ele.vendorName;
					delete ele.GSTIN;
					delete ele.tableType;
					curL3Obj.items.push(ele);
				}
			}
			if (type === 'PR') {
				var oVenSummary = new sap.ui.model.json.JSONModel(retArr);
				oView.setModel(oVenSummary, "VenSummary");
			} else if (type === 'ANX2') {
				var oVenAnx2Summary = new sap.ui.model.json.JSONModel(retArr);
				oView.setModel(oVenAnx2Summary, "VenANX2Summary");

			}

		},

		clearAdpatFilter: function (oEvent) {

			//eslint-disable-line
			if (this.byId("dAdapt")) {
				this.oView.byId("slPlant").setSelectedKeys(null);
				this.oView.byId("slProfitCtr").setSelectedKeys(null);
				this.oView.byId("slDivision").setSelectedKeys(null);
				this.oView.byId("slLocation").setSelectedKeys(null);
				this.oView.byId("slPurcOrg").setSelectedKeys(null);
				this.oView.byId("slUserAccess1").setSelectedKeys(null);
				this.oView.byId("slUserAccess2").setSelectedKeys(null);
				this.oView.byId("slUserAccess3").setSelectedKeys(null);
				this.oView.byId("slUserAccess4").setSelectedKeys(null);
				this.oView.byId("slUserAccess5").setSelectedKeys(null);
				this.oView.byId("slUserAccess6").setSelectedKeys(null);
			}
			var oText = this.getView().byId("idPRSummaryMainTitle").getText();
			var oDataEntity = this.getView().getModel("EntityModel").getData();
			var vDate = new Date();
			if (oText === "Processed Data") {

				// this.getView().byId("idfgiEntity").setSelectedKey(oDataEntity[0].entityId);
				// this._getDataSecurity(oDataEntity[0].entityId, "idfbPRSummary");
				this.byId("idfgiTaxPeriod").setMaxDate(vDate);
				this.byId("idfgiTaxPeriod").setDateValue(vDate);
				this.getView().byId("idfgiGSINTComboMain").clearSelection();
				this.getView().byId("idComcoTableType").clearSelection();
				this.getView().byId("idComcoDocType").clearSelection();
				this.getView().byId("iddropdata").clearSelection();
				this.getView().byId("iddropdata").setSelectedKeys("CP");
				this.getPRSProcessedData(oDataEntity[0].entityId);

			} else {
				// this.getView().byId("idfgiDEntity").setSelectedKey(oDataEntity[0].entityId);
				// this._getDataSecurity(oDataEntity[0].entityId, "idfbPRSummaryD");
				this.byId("idfgiTaxPeriodD").setMaxDate(vDate);
				this.byId("idfgiTaxPeriodD").setDateValue(vDate);
				this.getView().byId("idDataFilter").clearSelection();
				this.getView().byId("idComcoTableTypeIn").clearSelection();
				this.getView().byId("idComcoDocTypeIn").clearSelection();
				this.getView().byId("idDataFilter").setSelectedKeys("CP");
				var aData = [];
				var a = oEvent.getSource().getText();
				aData.push(a);
				this.oView.byId("idfgiGSINTComboMainD").setSelectedKeys(aData);
				this.getPRSummaryDetails();

			}
		},
		clearAdpatFilterAnx2: function (oEvent) {
			//eslint-disable-line
			var oText = this.getView().byId("idGetPRSummaryMainTitle").getText();
			var oDataEntity = this.getView().getModel("EntityModel").getData();
			var vDate = new Date();
			if (oText === "GSTN Data") {
				// this.getView().byId("idGetANX22AEntity").setSelectedKey(oDataEntity[0].entityId);
				// this._getDataSecurity(oDataEntity[0].entityId, "idfbGETANX2");
				this.byId("idGetfgiTaxPeriod").setMaxDate(vDate);
				this.byId("idGetfgiTaxPeriod").setDateValue(vDate);
				this.getView().byId("idGetfgiGSINTComboMain").clearSelection();
				this.getView().byId("iddropdata1").clearSelection();

				this.getView().byId("idMultiTabType").clearSelection();
				this.getView().byId("idMultiDoctype").clearSelection();

				this.getView().byId("iddropdata1").setSelectedKeys("CP");
				this.getGet2aProcessedData();

			} else {
				// this.getView().byId("idSDGetANX22AEntity").setSelectedKey(oDataEntity[0].entityId);
				// this._getDataSecurity(oDataEntity[0].entityId, "idfbGETANX2");
				this.byId("idSDGetfgiTaxPeriod").setMaxDate(vDate);
				this.byId("idSDGetfgiTaxPeriod").setDateValue(vDate);
				this.getView().byId("idSDGetfgiGSINTComboMain").clearSelection();
				this.getView().byId("iddropdata2").clearSelection();
				this.getView().byId("idMultiTabTypIn").clearSelection();
				this.getView().byId("idMultiDoctypeIn").clearSelection();
				this.getView().byId("iddropdata2").setSelectedKeys("CP");
				this.getGet2aSummaryDetails();

			}
		},

		handleLinkPressGETANX2Back: function (oEvent) {
			this.oView.byId("idGetPREntity").setVisible(true);
			this.oView.byId("idGetPRGSTIN").setVisible(false);
			// this.oView.byId("idGetPRSW").setVisible(false);
			this.oView.byId("idfbGETANX2").setVisible(true);
			this.oView.byId("idSDfbGETANX2").setVisible(false);
			this.oView.byId("idGetPRSummaryMainNavBack").setVisible(false);
			this.oView.byId("idGetPRSummaryMainTitle").setText("GSTN Data");

		},

		handleLinkPressGetGSTINMain: function (oEvent) {
			//eslint-disable-line

			var oTaxPeriod = this.oView.byId("idGetfgiTaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var aData = [];
			var a = oEvent.getSource().getText();
			aData.push(a);
			this.oView.byId("idSDGetfgiTaxPeriod").setValue(oTaxPeriod);
			// var oEntityID = this.oView.byId("idGetANX22AEntity").getSelectedKey();
			// this.oView.byId("idSDGetANX22AEntity").setSelectedKey(oEntityID);
			// this._getDataSecurity(oEntityID, "idfbGETANX2");
			this.oView.byId("idSDGetfgiGSINTComboMain").setSelectedKeys(aData);
			this.oView.byId("idGetPREntity").setVisible(false);
			this.oView.byId("idGetPRGSTIN").setVisible(true);
			// this.oView.byId("idGetPRSW").setVisible(false);
			this.oView.byId("idfbGETANX2").setVisible(false);
			this.oView.byId("idSDfbGETANX2").setVisible(true);
			this.oView.byId("idGetPRSummaryMainNavBack").setVisible(true);
			this.oView.byId("idGetPRSummaryMainTitle").setText("Review Summary");
			var postData = {

				"req": {
					"entityId": [$.sap.entityID],
					// "gstins": aData,
					"dataSecAttrs": {
						"GSTIN": aData
					},
					"taxPeriod": oTaxPeriod,
					"docType": null,
					"recordType": null,
					"data": []
				}
			};
			this.getAnx2SummaryDetails(postData);
			this.fnMoreLessPressFinal(false);
		},

		getGet2aSummaryDetails: function (oEvent) {
			var oTaxPeriod = this.oView.byId("idSDGetfgiTaxPeriod").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var postData = {
				"req": {
					"entityId": [$.sap.entityID],
					"dataSecAttrs": {
						"GSTIN": this.oView.byId("idSDGetfgiGSINTComboMain").getSelectedKeys()
					},
					// "gstins": this.oView.byId("idSDGetfgiGSINTComboMain").getSelectedKeys(),
					"docType": this.oView.byId("idMultiTabTypIn").getSelectedKeys(),
					"recordType": this.oView.byId("idMultiDoctypeIn").getSelectedKeys(),
					"taxPeriod": oTaxPeriod,
					"data": []
				}
			};
			this.getAnx2SummaryDetails(postData);
			this.fnMoreLessPressFinal(false);
		},

		fnMoreLessPress: function (oEvent) {
			//eslint-disable-line

			if (oEvent.getSource().getId().includes("iMore")) {
				var flag = true;
			} else {
				flag = false;
			}
			this.byId("iMore").setVisible(!flag);
			this.byId("iLess").setVisible(flag);
			this.fnMoreLessPressFinal(flag);
		},
		onPressExpandCollapse: function (oEvent) {
			//eslint-disable-line
			if (oEvent.getSource().getId().includes("idPRExpandIcon")) {
				var flag = true;
			} else {
				flag = false;
			}
			this.byId("idPRExpandIcon").setVisible(!flag);
			this.byId("idPRCollapseIcon").setVisible(flag);
			this.fnMoreLessPressFinalPR(flag);

		},
		fnMoreLessPressFinalPR: function (flag) {
			var objBlock = {
				"Expand": flag
			};
			this.byId("idPRExpandIcon").setVisible(!flag);
			this.byId("idPRCollapseIcon").setVisible(flag);
			var oJsondata = new sap.ui.model.json.JSONModel(objBlock);
			this.byId("idPRGSTIN").setModel(oJsondata, "BlockDataPR");
		},

		fnMoreLessPressFinal: function (flag) {
			//eslint-disable-line

			var objBlock = {
				"Expand": flag
			};
			this.byId("iMore").setVisible(!flag);
			this.byId("iLess").setVisible(flag);
			var oJsondata = new sap.ui.model.json.JSONModel(objBlock);
			this.byId("idGettabRet11mm").setModel(oJsondata, "BlockData");
		},
		getAnx2SummaryDetails: function (postData) {
			//eslint-disable-line
			var oView = this.getView();
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnx2SummaryDetails.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oAnx2SummaryDetails = new sap.ui.model.json.JSONModel(data.resp);
					oView.setModel(oAnx2SummaryDetails, "Anx2SummaryDetails");
					that.getANX2HeaderSummaryData(data);
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAnx2SummaryDetails : Error");
					var oAnx2SummaryDetails = new sap.ui.model.json.JSONModel(null);
					oView.setModel(oAnx2SummaryDetails, "Anx2SummaryDetails");
				});
			});
		},

		onGetANX2: function (oEvent) {
			//eslint-disable-line
			var oView = this.getView();
			var that = this;
			var tab = oView.byId("idGetPREntityTable");
			var sItems = tab.getSelectedIndices();
			var oTabData = oView.getModel("Get2aProcessedData").getData().resp;
			var returnPeriod = oView.byId("idGetfgiTaxPeriod").getValue();
			var aGSTIN = oView.byId("idGetfgiGSINTComboMain").getSelectedKeys();
			if (aGSTIN.length === 0) {
				MessageBox.information("Select at least one record");
				return;
			}

			if (returnPeriod === "") {
				returnPeriod = null;
			}
			var postData = [];
			for (var i = 0; i < aGSTIN.length; i++) {
				postData.push({
					"gstin": aGSTIN[i],
					"rtnprd": returnPeriod
				});
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Anx2GstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Anx2GstnGetSection : Error");
				});
			});
		},

		onPressRequestIDwiseGetSummary: function () {

			this.oView.byId("idGetsummaryDynamic1").setVisible(false);
			this.oView.byId("idGetsummaryDynamic2").setVisible(true);
			var oTaxPeriod = this.oView.byId("idGetfgiTaxPeriod").getValue();
			this.oView.byId("idRWItaxPeriod").setValue(oTaxPeriod);

			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"req": {
					"requestId": "",
					"taxPeriod": oTaxPeriod,
					"initiatedBy": [],
					"initiationDateTime": "",
					"completionDateTime": "",
					"dataSecAttrs": {
						"GSTIN": [],
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"SO": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getRequestWiseID(postData);
			this.getRequestWiseIDF4(postData);
		},

		onChangeRWITaxPeriod: function () {
			var oTaxPeriod = this.oView.byId("idRWItaxPeriod").getValue();

			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"req": {
					"requestId": "",
					"taxPeriod": oTaxPeriod,
					"initiatedBy": [],
					"initiationDateTime": "",
					"completionDateTime": "",
					"dataSecAttrs": {
						"GSTIN": [],
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"SO": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getRequestWiseIDF4(postData);
		},

		onPressRequestIDwiseGetSummaryBack: function () {
			this.oView.byId("idGetsummaryDynamic1").setVisible(true);
			this.oView.byId("idGetsummaryDynamic2").setVisible(false);
		},

		RWIonPressGO: function () {
			var oTaxPeriod = this.oView.byId("idRWItaxPeriod").getValue();

			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}

			var postData = {
				"req": {
					"requestId": this.oView.byId("idRWIrequestId").getValue(),
					"taxPeriod": oTaxPeriod,
					"initiatedBy": this.oView.byId("idRWInitiatedBy").getSelectedKeys(),
					"initiationDateTime": "",
					"completionDateTime": "",
					"dataSecAttrs": {
						"GSTIN": [],
						"PC": [],
						"Plant": [],
						"D": [],
						"L": [],
						"SO": [],
						"PO": [],
						"UD1": [],
						"UD2": [],
						"UD3": [],
						"UD4": [],
						"UD5": [],
						"UD6": []
					}
				}
			};
			this.getRequestWiseID(postData);

		},

		getRequestWiseID: function (postData) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnx2DetailsByRequestId.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.bindRequestWiseID(data);
					// MessageBox.success(data.resp);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAnx2DetailsByRequestId : Error");
				});
			});
		},

		getRequestWiseIDF4: function (postData) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnx2DetailsByRequestId.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oRequestIDF4 = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oRequestIDF4, "RequestIDF4");
					// MessageBox.success(data.resp);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAnx2DetailsByRequestId : Error");
				});
			});
		},

		bindRequestWiseID: function (data) {
			var oData = [];
			if (data.resp.length > 0) {
				for (var i = 0; i < data.resp.length; i++) {
					if (data.resp[i].requestId != "") {
						data.resp[i].gstinCount = data.resp[i].gstin.length;
						oData.push(data.resp[i]);
					}
				}
			}
			if (oData.length > 0) {
				var oRequestID = new sap.ui.model.json.JSONModel(oData);
				this.getView().setModel(oRequestID, "RequestID");
			} else {
				var oRequestID = new sap.ui.model.json.JSONModel();
				this.getView().setModel(oRequestID, "RequestID");
			}

		},

		onPressGSTINRWI: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("RequestID").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("RequestID").getObject().requestId;
			for (var i = 0; i < TabData.length; i++) {
				if (reqId === TabData[i].requestId) {
					// gstins.push(TabData[i].gstins);
					gstins.push({
						"gstin": "123"
					});

				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx2.Popover", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins");
			this._oGstinPopover.openBy(oButton);
		},

		// fnMoreLessPressFinal: function (flag) {
		// 	// this.byId("iLess").setVisible(flag);
		// 	this.byId("iLess1").setVisible(flag);
		// 	this.byId("oHboxGstn").setVisible(flag);
		// 	this.byId("iHboxGET2").setVisible(flag);
		// 	this.byId("iHboxGET2Inv").setVisible(flag);
		// 	this.byId("iHboxGET2CR").setVisible(flag);
		// 	this.byId("oHboxGET2PRR").setVisible(flag);
		// 	this.byId("oHboxGET2PRA").setVisible(flag);
		// 	this.byId("oHboxGET2PR1").setVisible(flag);
		// 	this.byId("oHboxGETPr").setVisible(flag);
		// 	this.byId("oHboxGET2PR").setVisible(flag);
		// 	this.byId("oHboxGET2PR2").setVisible(flag);
		// 	this.byId("iHboxGET23").setVisible(flag);
		// 	this.byId("oHboxDiff").setVisible(flag);
		// 	this.byId("iHboxGstn").setVisible(flag);
		// 	this.byId("iHboxDiff").setVisible(flag);
		// },

		getPRSSaveAnx2Data: function (oEntity) {
			debugger;

			// var oTaxPeriod = this.oView.byId("idGetSaveTaxPeriod").getValue();
			// if (oTaxPeriod === "") {
			// 	oTaxPeriod = null;
			// }
			var postData = {

				"req": {
					"entityId": [24],
					"taxPeriod": "012019",
					"dataSecAttrs": {
						"GSTIN": ["11SUPEY1234A110"]
					}
				}

				// "req": {
				// 	"entityId": [$.sap.entityID],
				// 	"taxPeriod": oTaxPeriod,
				// 	"dataSecAttrs": {
				// 		"GSTIN": this.oView.byId("idfgiGSINTComb").getSelectedKeys(),
				// 		"Plant": [],
				// 		"PC": [],
				// 		"D": [],
				// 		"L": [],
				// 		"PO": [],
				// 		"UD1": [],
				// 		"UD2": [],
				// 		"UD3": [],
				// 		"UD4": [],
				// 		"UD5": [],
				// 		"UD6": []
				// 	}
				// }
			};
			this.getPRSSaveAnx2DataFinal(postData);
		},

		getPRSSaveAnx2DataFinal: function (postData) {
			debugger;

			var oView = this.getView();

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/saveAnx2ProcessedData.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					var oPRSSaveAnx2Data = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oPRSSaveAnx2Data, "PRSSaveAnx2Data");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("saveAnx2ProcessedData : Error");
				});
			});
		},

		handleLinkPressGSTINANX2Sum: function () {
			debugger;

			// this.oView.byId("idANX2SummaryTitle").setText("Review Summary");
			// this.oView.byId("idANX2SummaryTitle1").setText("Review Summary");
			// this.oView.byId("idANX2SummaryBack").setVisible(true);
			// this.oView.byId("idANX2SummaryBack1").setVisible(true);
			// this.oView.byId("idEntityPanelANX2").setVisible(false);
			// this.oView.byId("idEntityPanelANX21").setVisible(false);
			// this.oView.byId("idPanelANX2Summary").setVisible(true);
			// this.oView.byId("idPanelANX2Summary1").setVisible(true);
			// this.oView.byId("idStatementPanelANX2").setVisible(false);
			// this.oView.byId("idStatementPanelANX21").setVisible(false);

			this.oView.byId("idANX2SummaryTitle12").setText("Review Summary");
			this.oView.byId("idANX2SummaryTitle12").setText("Review Summary");
			this.oView.byId("idANX2SummaryBack12").setVisible(true);
			// this.oView.byId("idANX2SummaryBack12").setVisible(true);
			this.oView.byId("idEntityPanelANX212").setVisible(false);
			// this.oView.byId("idEntityPanelANX21").setVisible(false);
			this.oView.byId("idPanelANX2Summary12").setVisible(true);
			this.oView.byId("idPanelANX2Summary1").setVisible(true);
			this.oView.byId("idStatementPanelANX212").setVisible(false);
			this.oView.byId("idStatementPanelANX21").setVisible(false);

		},

		handleLinkPressANX2SummaryBack: function () {

			// this.oView.byId("idANX2SummaryTitle").setText("Entity");
			// this.oView.byId("idANX2SummaryBack").setVisible(false);
			// this.oView.byId("idEntityPanelANX2").setVisible(true);
			// this.oView.byId("idPanelANX2Summary").setVisible(false);
			// this.oView.byId("idStatementPanelANX2").setVisible(false);
			// this.oView.byId("idANX2SummaryTitle1").setText("Entity");
			// this.oView.byId("idANX2SummaryBack1").setVisible(false);
			// this.oView.byId("idEntityPanelANX21").setVisible(true);
			// this.oView.byId("idPanelANX2Summary1").setVisible(false);
			// this.oView.byId("idStatementPanelANX21").setVisible(false);

			this.oView.byId("idANX2SummaryTitle12").setText("");
			this.oView.byId("idANX2SummaryBack12").setVisible(false);
			this.oView.byId("idEntityPanelANX212").setVisible(true);
			this.oView.byId("idPanelANX2Summary12").setVisible(false);
			this.oView.byId("idStatementPanelANX212").setVisible(false);
			this.oView.byId("idANX2SummaryTitle12").setText("");
			this.oView.byId("idANX2SummaryBack12").setVisible(false);
			this.oView.byId("idEntityPanelANX212").setVisible(true);
			this.oView.byId("idPanelANX2Summary12").setVisible(false);
			this.oView.byId("idStatementPanelANX212").setVisible(false);
		},

		//=================================================================================================
		//---------------- Code End By sarvmangla for ANX-2 ------------------------------------------------------
		//=================================================================================================

		// //=================================================================================================
		// //---------------- Code Start By Ram for ANX-2 ----------------------------------------------------
		// //=================================================================================================
		// handleIconTabBarSelect: function (oEvent) {
		// 	if (oEvent.getParameters().selectedKey === "PRSummary") {
		// 		this.getPRSProcessedData("");
		// 	} else if (oEvent.getParameters().selectedKey === "GETANX22A") {
		// 		this.getGet2aProcessedData();
		// 	} else if (oEvent.getParameters().selectedKey === "InitiateRecon") {
		// 		this.onPressGoForGSTIN();
		// 	} else if (oEvent.getParameters().selectedKey === "ReconResults") {
		// 		this.onPressGoForGSTINRR();
		// 	} else if (oEvent.getParameters().selectedKey === "RET1Impact") {

		// 	} else if (oEvent.getParameters().selectedKey === "ANX2Summary") {

		// 	} else if (oEvent.getParameters().selectedKey === "VendorSummary") {

		// 	} else if (oEvent.getParameters().selectedKey === "VendorCommunication") {

		// 	} else if (oEvent.getParameters().selectedKey === "CompareResults") {

		// 	} else if (oEvent.getParameters().selectedKey === "ReconReports") {

		// 	}
		// },

		// onSelectionChangeReconResults: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var vId = oEvent.getSource().getId();
		// 	if (vId.includes("idSBMainReconResult")) {
		// 		anx2Data.onSelectionChangeReconResultMain(oEvent, this);
		// 	} else if (vId.includes("idSBSDRRTabReconResult")) {
		// 		anx2Data.onSelectionChangeSDRRTab(oEvent, this);
		// 	} else if (vId.includes("idSBSDTabReconResult")) {
		// 		anx2Data.onSelectionChangeSDTab(oEvent, this);
		// 	} else if (vId.includes("idSBSDGDMTabReconResult")) {
		// 		anx2Data.onSelectionChangeSDGDMTab(oEvent, this);
		// 	} else if (vId.includes("idSBSDPMTabReconResult")) {
		// 		anx2Data.onSelectionChangeSDPMTab(oEvent, this);
		// 	} else if (vId.includes("idSBSDA2ATabReconResult")) {
		// 		anx2Data.onSelectionChangeSDA2ATab(oEvent, this);
		// 	} else if (vId.includes("idSBSDAPRTabReconResult")) {
		// 		anx2Data.onSelectionChangeSDAPRTab(oEvent, this);
		// 	}
		// },

		// onSelectCheckBoxCol1t: function (oEvent) {
		// 	anx2Data.onSelectCheckBoxCol1t(oEvent, this);
		// },
		// onSelectCheckBoxCol2t: function (oEvent) {
		// 	anx2Data.onSelectCheckBoxCol2t(oEvent, this);
		// },

		// onSelectCheckBoxCol1: function (oEvent) {
		// 	anx2Data.onSelectCheckBoxCol1(oEvent, this);
		// },
		// onSelectCheckBoxCol2: function (oEvent) {
		// 	anx2Data.onSelectCheckBoxCol2(oEvent, this);
		// },

		// onInitiateRecon: function (oEvent) {
		// 	anx2Data.onInitiateRecon(oEvent, this);
		// },

		// onAbsoluteMatchDetails: function (oEvent) {
		// 	anx2Data.onAbsoluteMatchDetails(oEvent, this);
		// },

		// onReconResultsSummary: function (oEvent) {
		// 	anx2Data.onReconResultsSummary(oEvent, this);
		// },

		// onReconResultMismatch: function (oEvent) {
		// 	anx2Data.onReconResultMismatch(oEvent, this);
		// },

		// onPotentialMatchDetails: function (oEvent) {
		// 	anx2Data.onPotentialMatchDetails(oEvent, this);
		// },

		// onPotentialMatchSummary: function (oEvent) {
		// 	anx2Data.onPotentialMatchSummary(oEvent, this);
		// },

		// onMisMatchSummary: function (oEvent) {
		// 	anx2Data.onMisMatchSummary(oEvent, this);
		// },

		// bindData: function (oEvent) {
		// 	var oView = this.getView();
		// 	var data = [{
		// 		"gstin": "32SSSKL8363S1ZF",
		// 		"status": "Success",
		// 		"date": "17-05-2019 : 04:11:00"
		// 	}, {
		// 		"gstin": "33GSPTN0481G1ZB",
		// 		"status": "Success",
		// 		"date": "17-05-2019 : 04:11:00"
		// 	}];
		// 	var oGSTIN = new sap.ui.model.json.JSONModel(data);
		// 	oView.setModel(oGSTIN, "GSTIN");
		// },
		// onSelectionChange: function (oEvent) {
		// 	this.onReconResultsSummary();
		// 	this.onAbsoluteMatchDetails();
		// 	this.onReconResultMismatch();
		// 	this.onPotentialMatchDetails();
		// 	this.onPotentialMatchSummary();
		// 	this.onMisMatchSummary();
		// },

		// onSelectionChange1: function (oEvent) {
		// 	this.onInitiateRecon();
		// },

		// // onPressRRAction: function (oEvent) {
		// // 	anx2Data.onPressRRAction(oEvent, this);
		// // },

		// onPressRRAction: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oView = this.getView();
		// 	var vId = oEvent.getSource().getId();
		// 	if (vId.includes("idActionBut1")) {
		// 		var aDataSelectedIndices = this.oView.byId("idABDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "ABSOLUTE";
		// 		var oAction = "CLAIM_ITC_AS_PER_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);

		// 	} else if (vId.includes("idActionBut2")) {
		// 		var aDataSelectedIndices = this.oView.byId("idABDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "ABSOLUTE";
		// 		var oAction = "CLAIM_ITC_AS_PER_PR";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);

		// 	} else if (vId.includes("idActionBut3")) {
		// 		var aDataSelectedIndices = this.oView.byId("idABDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "ABSOLUTE";
		// 		var oAction = "PENDING_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idActionBut4")) {
		// 		var aDataSelectedIndices = this.oView.byId("idABDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "ABSOLUTE";
		// 		var oAction = "REJECT_A2_AND_PR";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idActionBut6")) {
		// 		var aDataSelectedIndices = this.oView.byId("idABDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "ABSOLUTE";
		// 		var oAction = "REJECT_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idGDMActionBut1")) {
		// 		var aDataSelectedIndices = this.oView.byId("idGDMDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "MISMATCH";
		// 		var oAction = "CLAIM_ITC_AS_PER_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idGDMActionBut2")) {
		// 		var aDataSelectedIndices = this.oView.byId("idGDMDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "MISMATCH";
		// 		var oAction = "CLAIM_ITC_AS_PER_PR";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idGDMActionBut3")) {
		// 		var aDataSelectedIndices = this.oView.byId("idGDMDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "MISMATCH";
		// 		var oAction = "PENDING_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idGDMActionBut4")) {
		// 		var aDataSelectedIndices = this.oView.byId("idGDMDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "MISMATCH";
		// 		var oAction = "REJECT_A2_AND_PR";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	} else if (vId.includes("idGDMActionBut6")) {
		// 		var aDataSelectedIndices = this.oView.byId("idGDMDetail").getSelectedIndices();
		// 		if (aDataSelectedIndices.length === 0) {
		// 			MessageBox.information("Please Select at least on records");
		// 			return;
		// 		}
		// 		var aData = oView.getModel("ReconResult").getData().resp;
		// 		var oMatchingType = "MISMATCH";
		// 		var oAction = "REJECT_A2";
		// 		this.onExecuteComputeAction(aDataSelectedIndices, aData, oMatchingType, oAction);
		// 	}
		// },

		// onExecuteComputeAction: function (aDataSelectedIndices, aData, oMatchingType, oAction) {
		// 	var reqData = {
		// 		"req": {
		// 			"reconComputeList": []
		// 		}
		// 	};

		// 	for (var i = 0; i < aDataSelectedIndices.length; i++) {
		// 		var j = aDataSelectedIndices[i];
		// 		j = Number(j);
		// 		reqData.req.reconComputeList.push({
		// 			"a2InvoiceKey": aData[j].twoAInvoiceKey,
		// 			"prInvoiceKey": aData[j].prInvoiceKey,
		// 			"matchingType": oMatchingType,
		// 			"action": oAction
		// 		});
		// 	}
		// 	var a, oCont = this;
		// 	var jsonForSearch = JSON.stringify(reqData);
		// 	var executeComputeAction = "/aspsapapi/executeComputeAction.do";
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: executeComputeAction,
		// 			contentType: "application/json",
		// 			data: jsonForSearch
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			if (data.hdr.status === "E") {
		// 				MessageBox.error(data.resp);
		// 			} else {
		// 				MessageBox.success(data.resp);
		// 				if (oMatchingType === "ABSOLUTE") {
		// 					oCont.onReconResultsSummary();
		// 				} else if (oMatchingType === "MISMATCH") {
		// 					oCont.onMisMatchSummary();
		// 				}
		// 			}

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("executeComputeAction : Error");
		// 		});
		// 	});

		// },

		// onSearch: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oView = this.getView();
		// 	var vId = oEvent.getSource().getId();
		// 	if (vId.includes("idButPRSummary")) {
		// 		var oText = oView.byId("idPRSummaryMainTitle").getText();
		// 		if (oText === "Processed Data") {
		// 			this.getPRSProcessedData("");
		// 		} else {
		// 			this.getPRSummaryDetails();
		// 		}
		// 	} else if (vId.includes("idfbGETANX2")) {
		// 		this.getGet2aProcessedData();
		// 	} else if (vId.includes("idSDfbGETANX2")) {
		// 		this.getGet2aSummaryDetails();
		// 	}
		// },

		// getPRSProcessedData: function (oEntity) {
		// 	var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
		// 	if (oEntity === "") {
		// 		oEntity = this.oView.byId("idfgiEntity").getSelectedKey();
		// 	}
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [oEntity],
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": [],
		// 			"recordType": [],
		// 			"fromDate": null,
		// 			"toDate": null,
		// 			"dataSecAttrs": {
		// 				"GSTIN": this.oView.byId("idfgiGSINTComboMain").getSelectedKeys(),
		// 				"Plant": [],
		// 				"PC": [],
		// 				"D": [],
		// 				"L": [],
		// 				"PO": [],
		// 				"UD1": [],
		// 				"UD2": [],
		// 				"UD3": [],
		// 				"UD4": [],
		// 				"UD5": [],
		// 				"UD6": []
		// 			}
		// 		}
		// 	};

		// 	this.getPRSProcessedDataFinal(postData);
		// },

		// getPRSProcessedDataFinal: function (postData) {
		// 	var oView = this.getView();

		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getPRSProcessedData.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			if (data.resp.length === 0) {
		// 				MessageBox.information("No Data");
		// 			}
		// 			var oPRSProcessedData = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oPRSProcessedData, "PRSProcessedData");

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getPRSProcessedData : Error");
		// 		});
		// 	});
		// },

		// onPressAdaptFilters: function () {
		// 	if (!this._oAdpatFilter) {
		// 		this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.anx2.AdaptAnx2", this);
		// 		this.getView().addDependent(this._oAdpatFilter);
		// 	}
		// 	this._oAdpatFilter.open();
		// },

		// onPressFilterApply: function (oEvent) {
		// 	var oText = this.getView().byId("idPRSummaryMainTitle").getText();
		// 	if (oText === "Processed Data") {
		// 		this.onPressFilterApplyGetPR();
		// 	} else {
		// 		this.onPressFilterApplyGetSummaryDetails();
		// 	}
		// 	this._oAdpatFilter.close();
		// },

		// onPressFilterApplyGetPR: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}

		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idfgiEntity").getSelectedKey()],
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": this.oView.byId("idComcoDocType").getSelectedKeys(),
		// 			"recordType": this.oView.byId("idComcoTableType").getSelectedKeys(),
		// 			"fromDate": null,
		// 			"toDate": null,
		// 			"dataSecAttrs": {
		// 				"GSTIN": this.oView.byId("idfgiGSINTComboMain").getSelectedKeys(),
		// 				"Plant": this.oView.byId("slPlant").getSelectedKeys(),
		// 				"PC": this.oView.byId("slProfitCtr").getSelectedKeys(),
		// 				"D": this.oView.byId("slDivision").getSelectedKeys(),
		// 				"L": this.oView.byId("slLocation").getSelectedKeys(),
		// 				"PO": this.oView.byId("slPurcOrg").getSelectedKeys(),
		// 				"UD1": this.oView.byId("slUserAccess1").getSelectedKeys(),
		// 				"UD2": this.oView.byId("slUserAccess2").getSelectedKeys(),
		// 				"UD3": this.oView.byId("slUserAccess3").getSelectedKeys(),
		// 				"UD4": this.oView.byId("slUserAccess4").getSelectedKeys(),
		// 				"UD5": this.oView.byId("slUserAccess5").getSelectedKeys(),
		// 				"UD6": this.oView.byId("slUserAccess6").getSelectedKeys()
		// 			}
		// 		}
		// 	};

		// 	this.getPRSProcessedDataFinal(postData);
		// },

		// onPressFilterApplyGetSummaryDetails: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oTaxPeriod = this.oView.byId("idfgiTaxPeriodD").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idfgiEntityD").getSelectedKey()],
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": [],
		// 			"recordType": [],
		// 			"fromDate": null,
		// 			"toDate": null,
		// 			"dataSecAttrs": {
		// 				"GSTIN": this.oView.byId("idfgiGSINTComboMainD").getSelectedKeys(),
		// 				"Plant": this.oView.byId("slPlant").getSelectedKeys(),
		// 				"PC": this.oView.byId("slProfitCtr").getSelectedKeys(),
		// 				"D": this.oView.byId("slDivision").getSelectedKeys(),
		// 				"L": this.oView.byId("slLocation").getSelectedKeys(),
		// 				"PO": this.oView.byId("slPurcOrg").getSelectedKeys(),
		// 				"UD1": this.oView.byId("slUserAccess1").getSelectedKeys(),
		// 				"UD2": this.oView.byId("slUserAccess2").getSelectedKeys(),
		// 				"UD3": this.oView.byId("slUserAccess3").getSelectedKeys(),
		// 				"UD4": this.oView.byId("slUserAccess4").getSelectedKeys(),
		// 				"UD5": this.oView.byId("slUserAccess5").getSelectedKeys(),
		// 				"UD6": this.oView.byId("slUserAccess6").getSelectedKeys()
		// 			}
		// 		}
		// 	};

		// 	this.getanx2prReviewSummary(postData);
		// 	this.getPRSDetail(postData);
		// 	//	this.clearAdpatFilter();
		// },

		// onPressFilterClose: function (oEvent) {
		// 	// 			 //eslint-disable-line
		// 	this._oAdpatFilter.close();
		// },

		// getGet2aProcessedData: function (oEvent) {
		// 	var oTaxPeriod = this.oView.byId("idGetfgiTaxPeriod").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idGetANX22AEntity").getSelectedKey()],
		// 			"gstin": this.oView.byId("idGetfgiGSINTComboMain").getSelectedKeys(),
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": null
		// 		}
		// 	};

		// 	this.getGet2aProcessedDataFinal(postData);
		// },
		// getGet2aProcessedDataFinal: function (postData) {
		// 	var oView = this.getView();
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getGet2aProcessedData.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oGet2aProcessedData = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oGet2aProcessedData, "Get2aProcessedData");
		// 			sap.ui.core.BusyIndicator.hide();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getGet2aProcessedData : Error");
		// 		});
		// 	});
		// },

		// handleLinkPressGSTINMainBack: function (oEvent) {
		// 	this.oView.byId("idPREntity").setVisible(true);
		// 	this.oView.byId("idPRGSTIN").setVisible(false);
		// 	this.oView.byId("idPRSW").setVisible(false);
		// 	this.oView.byId("idfbPRSummary").setVisible(true);
		// 	this.oView.byId("idfbPRSummaryD").setVisible(false);
		// 	this.oView.byId("idPRSummaryMainNavBack").setVisible(false);
		// 	this.oView.byId("idPRSummaryMainTitle").setText("Processed Data");
		// },

		// handleLinkPressGSTINMain: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oTaxPeriod = this.oView.byId("idfgiTaxPeriod").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var aData = [];
		// 	var a = oEvent.getSource().getText();
		// 	aData.push(a);
		// 	this.oView.byId("idfgiTaxPeriodD").setValue(oTaxPeriod);
		// 	var oEntity = this.oView.byId("idfgiEntity").getSelectedKey();
		// 	this.oView.byId("idfgiEntityD").setSelectedKey(oEntity);
		// 	this.oView.byId("idfgiGSINTComboMainD").setSelectedKeys(aData);
		// 	this.oView.byId("idPREntity").setVisible(false);
		// 	this.oView.byId("idPRGSTIN").setVisible(true);
		// 	this.oView.byId("idPRSW").setVisible(false);
		// 	this.oView.byId("idfbPRSummary").setVisible(false);
		// 	this.oView.byId("idfbPRSummaryD").setVisible(true);
		// 	this.oView.byId("idPRSummaryMainNavBack").setVisible(true);
		// 	this.oView.byId("idPRSummaryMainTitle").setText("Review Summary");

		// 	var postData = {
		// 		"req": {
		// 			"entityId": [oEntity],
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": [],
		// 			"recordType": [],
		// 			"fromDate": null,
		// 			"toDate": null,
		// 			"dataSecAttrs": {
		// 				"GSTIN": aData,
		// 				"Plant": [],
		// 				"PC": [],
		// 				"D": [],
		// 				"L": [],
		// 				"PO": [],
		// 				"UD1": [],
		// 				"UD2": [],
		// 				"UD3": [],
		// 				"UD4": [],
		// 				"UD5": [],
		// 				"UD6": []
		// 			}
		// 		}
		// 	};

		// 	this.getanx2prReviewSummary(postData);
		// 	this.getPRSDetail(postData);
		// },
		// getPRSummaryDetails: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oTaxPeriod = this.oView.byId("idfgiTaxPeriodD").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idfgiEntityD").getSelectedKey()],
		// 			"taxPeriod": oTaxPeriod,
		// 			"docType": [],
		// 			"recordType": [],
		// 			"fromDate": null,
		// 			"toDate": null,
		// 			"dataSecAttrs": {
		// 				"GSTIN": this.oView.byId("idfgiGSINTComboMainD").getSelectedKeys(),
		// 				"Plant": [],
		// 				"PC": [],
		// 				"D": [],
		// 				"L": [],
		// 				"PO": [],
		// 				"UD1": [],
		// 				"UD2": [],
		// 				"UD3": [],
		// 				"UD4": [],
		// 				"UD5": [],
		// 				"UD6": []
		// 			}
		// 		}
		// 	};

		// 	this.getanx2prReviewSummary(postData);
		// 	this.getPRSDetail(postData);
		// },

		// handleLinkPressGETANX2Back: function (oEvent) {
		// 	this.oView.byId("idGetPREntity").setVisible(true);
		// 	this.oView.byId("idGetPRGSTIN").setVisible(false);
		// 	// this.oView.byId("idGetPRSW").setVisible(false);
		// 	this.oView.byId("idfbGETANX2").setVisible(true);
		// 	this.oView.byId("idSDfbGETANX2").setVisible(false);
		// 	this.oView.byId("idGetPRSummaryMainNavBack").setVisible(false);
		// 	this.oView.byId("idGetPRSummaryMainTitle").setText("GSTN Data");

		// },

		// handleLinkPressGetGSTINMain: function (oEvent) {
		// 	 //eslint-disable-line

		// 	var oTaxPeriod = this.oView.byId("idGetfgiTaxPeriod").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var aData = [];
		// 	var a = oEvent.getSource().getText();
		// 	aData.push(a);
		// 	var oEntityID = this.oView.byId("idGetANX22AEntity").getSelectedKey();
		// 	this.oView.byId("idSDGetANX22AEntity").setSelectedKey(oEntityID);
		// 	this.oView.byId("idSDGetfgiGSINTComboMain").setSelectedKeys(aData);
		// 	this.oView.byId("idGetPREntity").setVisible(false);
		// 	this.oView.byId("idGetPRGSTIN").setVisible(true);
		// 	// this.oView.byId("idGetPRSW").setVisible(false);
		// 	this.oView.byId("idfbGETANX2").setVisible(false);
		// 	this.oView.byId("idSDfbGETANX2").setVisible(true);
		// 	this.oView.byId("idGetPRSummaryMainNavBack").setVisible(true);
		// 	this.oView.byId("idGetPRSummaryMainTitle").setText("Review Summary");
		// 	var postData = {
		// 		"req": {
		// 			"entity": [this.oView.byId("idGetANX22AEntity").getSelectedKey()],
		// 			"gstin": aData,
		// 			"taxPeriod": oTaxPeriod
		// 		}
		// 	};
		// 	this.getanx2reviewSummary(postData);
		// 	this.getAnx2SummaryDetails(postData);
		// },

		// getGet2aSummaryDetails: function (oEvent) {
		// 	var oTaxPeriod = this.oView.byId("idSDGetfgiTaxPeriod").getValue();
		// 	if (oTaxPeriod === "") {
		// 		oTaxPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entity": [this.oView.byId("idSDGetANX22AEntity").getSelectedKey()],
		// 			"gstin": this.oView.byId("idSDGetfgiGSINTComboMain").getSelectedKeys(),
		// 			"taxPeriod": oTaxPeriod
		// 		}
		// 	};
		// 	this.getanx2reviewSummary(postData);
		// 	this.getAnx2SummaryDetails(postData);
		// },

		// getanx2reviewSummary: function (postData) {
		// 	var oView = this.getView();
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getanx2reviewSummary.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oanx2reviewSummary = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oanx2reviewSummary, "anx2reviewSummary");
		// 			sap.ui.core.BusyIndicator.hide();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getanx2reviewSummary : Error");
		// 			var oanx2reviewSummary = new sap.ui.model.json.JSONModel(null);
		// 			oView.setModel(oanx2reviewSummary, "anx2reviewSummary");
		// 		});
		// 	});
		// },

		// getAnx2SummaryDetails: function (postData) {
		// 	var oView = this.getView();
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getAnx2SummaryDetails.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oAnx2SummaryDetails = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oAnx2SummaryDetails, "Anx2SummaryDetails");
		// 			that.getANX2HeaderSummaryData(data)
		// 			sap.ui.core.BusyIndicator.hide();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getAnx2SummaryDetails : Error");
		// 			var oAnx2SummaryDetails = new sap.ui.model.json.JSONModel(null);
		// 			oView.setModel(oAnx2SummaryDetails, "Anx2SummaryDetails");
		// 		});
		// 	});
		// },

		// onSelectionChangeGSTINm: function (oEvent) {
		// 	if (oEvent.getSource().getSelectedKey() === "Total") {
		// 		this.oView.byId("tabRet1m").setVisible(false);
		// 		this.oView.byId("tabRet11m").setVisible(true);
		// 		this.oView.byId("idHBoxShowing1").setVisible(true);
		// 		this.oView.byId("idHBoxShowing2").setVisible(true);
		// 		this.oView.byId("idHBoxShowing3").setVisible(true);
		// 		this.oView.byId("idHBoxShowing4").setVisible(true);
		// 		this.oView.byId("idHBoxShowing5").setVisible(true);

		// 	} else {
		// 		this.oView.byId("tabRet1m").setVisible(true);
		// 		this.oView.byId("tabRet11m").setVisible(false);
		// 		this.oView.byId("idHBoxShowing1").setVisible(true);
		// 		this.oView.byId("idHBoxShowing2").setVisible(true);
		// 		this.oView.byId("idHBoxShowing3").setVisible(true);
		// 		this.oView.byId("idHBoxShowing4").setVisible(true);
		// 		this.oView.byId("idHBoxShowing5").setVisible(true);
		// 	}
		// },

		// onSelectionChangeGetGSTINm: function (oEvent) {
		// 	if (oEvent.getSource().getSelectedKey() === "Total") {
		// 		this.oView.byId("idGettabRet1m").setVisible(false);
		// 		this.oView.byId("idGettabRet11m").setVisible(true);

		// 	} else {
		// 		this.oView.byId("idGettabRet1m").setVisible(true);
		// 		this.oView.byId("idGettabRet11m").setVisible(false);
		// 	}
		// },

		// getanx2prReviewSummary: function (postData) {
		// 	var oView = this.getView();
		// 	 //eslint-disable-line
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getanx2prReviewSummary.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oanx2prReviewSummary = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oanx2prReviewSummary, "anx2prReviewSummary");
		// 			sap.ui.core.BusyIndicator.hide();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getanx2prReviewSummary : Error");
		// 			var oanx2prReviewSummary = new sap.ui.model.json.JSONModel(null);
		// 			oView.setModel(oanx2prReviewSummary, "anx2prReviewSummary");
		// 		});
		// 	});
		// },

		// getPRSDetail: function (postData) {
		// 	var oView = this.getView();
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getPRSDetail.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oPRSDetail = new sap.ui.model.json.JSONModel(data);
		// 			oView.setModel(oPRSDetail, "PRSDetail");
		// 			sap.ui.core.BusyIndicator.hide();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getPRSDetail : Error");
		// 		});
		// 	});
		// },

		// onGetANX2: function (oEvent) {
		// 	 //eslint-disable-line
		// 	var oView = this.getView();
		// 	var that = this;
		// 	var tab = oView.byId("idGetPREntityTable");
		// 	var sItems = tab.getSelectedIndices();
		// 	var oTabData = oView.getModel("Get2aProcessedData").getData().resp;
		// 	var returnPeriod = oView.byId("idGetfgiTaxPeriod").getValue();
		// 	if (returnPeriod === "") {
		// 		returnPeriod = null;
		// 	}
		// 	var postData = [];
		// 	for (var i = 0; i < sItems.length; i++) {
		// 		postData.push({
		// 			"gstin": oTabData[0].gstin,
		// 			"rtnprd": returnPeriod
		// 		});
		// 	}
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/anx2GstnGetJob.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.success(data.resp);

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("anx2GstnGetJob : Error");
		// 		});
		// 	});
		// },

		// getDataSecurityForUser: function (oEvent) {
		// 	var oView = this.getView();
		// 	var that = this;

		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getDataSecurityForUser.do",
		// 			contentType: "application/json",
		// 			// data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			if (data.resp.length === 0) {
		// 				return;
		// 				MessageBox.information("Data is not available for this user");
		// 			}
		// 			var oEntity = new JSONModel(data);
		// 			oEntity.setSizeLimit(2000);
		// 			oView.setModel(oEntity, "entity");
		// 			oEntityMain = data.resp[0].entityId;
		// 			that.bindDataSecurityForUser(oEntityMain);
		// 			that.bindDataSecurityForGetANX2(oEntityMain);
		// 			that.getPRSProcessedData(oEntityMain);

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getDataSecurityForUser : Error");
		// 		});
		// 	});
		// },

		// getDataSecurityForUser1: function (oEvent) {
		// 	var oView = this.getView();
		// 	var that = this;

		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getAnxDataSecEntities.do",
		// 			contentType: "application/json",
		// 			// data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			 //eslint-disable-line

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getDataSecurityForUser : Error");
		// 		});
		// 	});
		// },

		// getDataSecurityForUser2: function (oEvent) {
		// 	var oView = this.getView();
		// 	var that = this;
		// 	var postData = {
		// 		"req": {
		// 			"entityId": "14"
		// 		}
		// 	};

		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/getAnxDataSecApplAttr.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			 //eslint-disable-line

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("getDataSecurityForUser : Error");
		// 		});
		// 	});
		// },

		// bindDataSecurityForUser: function (entityID) {
		// 	 //eslint-disable-line
		// 	var oView = this.getView();
		// 	var data = oView.getModel("entity").getData();
		// 	var oLen = data.resp.length;
		// 	// var aData = [];
		// 	for (var i = 0; i < oLen; i++) {
		// 		if (data.resp[i].entityId === entityID) {
		// 			var aData = data.resp[i];
		// 			break;
		// 		}
		// 	}
		// 	var oDropDown = new JSONModel();
		// 	oDropDown.setData(aData);
		// 	oView.setModel(oDropDown, "DropDown");

		// },

		// bindDataSecurityForGetANX2: function (entityID) {
		// 	 //eslint-disable-line
		// 	var oView = this.getView();
		// 	var data = oView.getModel("entity").getData();
		// 	var oLen = data.resp.length;
		// 	// var aData = [];
		// 	for (var i = 0; i < oLen; i++) {
		// 		if (data.resp[i].entityId === entityID) {
		// 			var aData = data.resp[i];
		// 			break;
		// 		}
		// 	}
		// 	var oDropDownGetANX2 = new JSONModel();
		// 	oDropDownGetANX2.setData(aData);
		// 	oView.setModel(oDropDownGetANX2, "DropDownGetANX2");
		// },

		// onPressGoForGSTIN: function (oEvent) {
		// 	var oView = this.getView();
		// 	var that = this;
		// 	var oRetPeriod = this.oView.byId("idInitiateReconPeriod").getValue();
		// 	if (oRetPeriod === "") {
		// 		oRetPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idInitiateReconEntity").getSelectedKey()],
		// 			"returnPeriod": oRetPeriod
		// 		}
		// 	};
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/listGSTINForRecon.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oGSTIN = new JSONModel(data);
		// 			oView.setModel(oGSTIN, "GSTIN");
		// 			oView.byId("idInitiateReconList").selectAll();
		// 			that.onInitiateRecon();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("listGSTINForRecon : Error");
		// 		});
		// 	});
		// },

		// onPressGoForGSTINRR: function (oEvent) {
		// 	var oView = this.getView();
		// 	var that = this;
		// 	var oRetPeriod = this.oView.byId("idRRPeriod").getValue();
		// 	if (oRetPeriod === "") {
		// 		oRetPeriod = null;
		// 	}
		// 	var postData = {
		// 		"req": {
		// 			"entityId": [this.oView.byId("idRREntity").getSelectedKey()],
		// 			"returnPeriod": oRetPeriod
		// 		}
		// 	};
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "POST",
		// 			url: "/aspsapapi/listGSTINForRecon.do",
		// 			contentType: "application/json",
		// 			data: JSON.stringify(postData)
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			var oGSTINRR = new JSONModel(data);
		// 			oView.setModel(oGSTINRR, "GSTINRR");
		// 			oView.byId("idReconResultList").selectAll();
		// 			that.onAbsoluteMatchDetails();
		// 			that.onReconResultsSummary();
		// 			that.onReconResultMismatch();
		// 			that.onPotentialMatchDetails();
		// 			that.onPotentialMatchSummary();
		// 			that.onMisMatchSummary();

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			MessageBox.error("listGSTINForRecon : Error");
		// 		});
		// 	});
		// },

		// //=================================================================================================
		// //---------------- Code End By Ram for ANX-2 ------------------------------------------------------
		// //=================================================================================================

		// /**
		//  * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		//  * (NOT before the first rendering! onInit() is used for that one!).
		//  * @memberOf com.ey.digigst.view.ANX2
		//  */
		// //	onBeforeRendering: function() {
		// //
		// //	},

		// /**
		//  * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		//  * This hook is the same one that SAPUI5 controls get after being rendered.
		//  * @memberOf com.ey.digigst.view.ANX2
		//  */
		onAfterRendering: function () {
			// this.getView().byId("idIconTab").setSelectedKey("ANX1");
			// this.bindData();
			// var oEntity = this.getView().byId("idfgiEntity").getSelectedKey();
			// this._getDataSecurity(oEntity, "idfbPRSummary");
			this.getPRSProcessedData("");
			this.fnMoreLessPressFinalPR(false);
			this.onReconSummGO();
			this.ReconResponseData();
		},

		// clearAdpatFilter: function (oEvent) {
		// 	 //eslint-disable-line
		// 	if (this.byId("dAdapt")) {
		// 		this.oView.byId("slPlant").setSelectedKeys(null);
		// 		this.oView.byId("slProfitCtr").setSelectedKeys(null);
		// 		this.oView.byId("slDivision").setSelectedKeys(null);
		// 		this.oView.byId("slLocation").setSelectedKeys(null);
		// 		this.oView.byId("slPurcOrg").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess1").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess2").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess3").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess4").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess5").setSelectedKeys(null);
		// 		this.oView.byId("slUserAccess6").setSelectedKeys(null);
		// 	}

		// 	var oDataEntity = this.getView().getModel("entity").getData();
		// 	this.getView().byId("idfgiEntity").setSelectedKey(oDataEntity.resp[0].entityId);
		// 	this.getView().byId("idfgiGSINTComboMain").clearSelection();
		// 	this.getView().byId("idfgiTaxPeriod").setValue("");
		// 	this.getView().byId("idComcoTableType").clearSelection();
		// 	this.getView().byId("idComcoDocType").clearSelection();
		// 	this.getPRSProcessedData(oEntityMain);

		// 	// var oDataEntity = this.getView().getModel("entity").getData();
		// 	this.getView().byId("idGetANX22AEntity").setSelectedKey(oDataEntity.resp[0].entityId);
		// 	this.getView().byId("idGetfgiGSINTComboMain").clearSelection();
		// 	this.getView().byId("idGetfgiTaxPeriod").setValue("");
		// 	this.getView().byId("idMultiDoctype").clearSelection();
		// 	this.getView().byId("idfgiEntityD").setSelectedKey(oDataEntity.resp[0].entityId);
		// 	this.getView().byId("idfgiGSINTComboMainD").clearSelection();
		// 	this.getView().byId("idfgiTaxPeriodD").setValue("");
		// 	this.getView().byId("idSDGetfgiGSINTComboMain").clearSelection();
		// 	this.getView().byId("idSDGetfgiTaxPeriod").setValue("");
		// 	// this.handleLinkPressGSTINMain();
		// 	// this.getPRSProcessedDataFinal();
		// }

		// onCloseANX2Filter: function () {
		// 	this._oDialogANX2Filter.close();
		// },

		_DataReturn: function () {
			return {
				"threeA": {
					"Total": {
						"type": "Total",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"INV": {
						"type": "INV",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"CR": {
						"type": "CR",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"DR": {
						"type": "DR",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"INVA": {
						"type": "INVA",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"CRA": {
						"type": "CRA",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"DRA": {
						"type": "DRA",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					}
				},
				"ISD": {
					"Total": {
						"type": "Total",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"INV": {
						"type": "INV",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"CR": {
						"type": "CR",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"INVA": {
						"type": "INVA",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					},
					"CRA": {
						"type": "CRA",
						"taxableValue": 0,
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					}
				}
			};
		},

		///////////added by Vinay 03-12-19///////////////

		onSelectReconRespGstin: function (oEvt) {
			this.ReconRespGstin = oEvt.getSource().getSelectedKeys();
		},

		//////////////Added by Vinay Kodam/////////////////
		/////Recon Response Go Button Function for Summary and Details/////////
		ReconResponseData: function () {
			this.byId("summGSTINEntityID").setSelectedKeys(this.ReconRespGstin);
			var vGetKey = this.byId("typeID").getSelectedKeys();
			var vData = {
				"A2": false,
				"PR": false,
				"PRA": false
			};
			if (vGetKey.length === 0) {
				vData.A2 = true;
				vData.PR = true;
				vData.PRA = true;

			}
			for (var i = 0; i <= vGetKey.length; i++) {
				if (vGetKey[i] === "All") {
					vData.A2 = true;
					vData.PR = true;
					vData.PRA = true;
					break;

				} else if (vGetKey[i] === "A2") {
					vData.A2 = true;

				} else if (vGetKey[i] === "PR Tax") {
					vData.PR = true;

				} else if (vGetKey[i] === "PR Available") {
					vData.PRA = true;

				}
			}
			this.getView().setModel(new JSONModel(vData), "visiSummAnx2");

			var GstnInfo = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("ReconsumaryDateID").getValue(),
				"gstin": this.byId("summGSTINEntityID").getSelectedKeys(),
				"tableType": this.selectedKey === undefined ? [] : this.selectedKey,
				"docType": this.selectedKey1 === undefined ? [] : this.selectedKey1
			};
			this.ResponseData(GstnInfo);
		},

		/////Added By Vinay Kodam 07-02-2020////////
		/////Recon Response Clear Button Function for Summary and Details/////////
		onClearFilterRecResp: function () {
			//var oEntity = this.getView().getModel("EntityModel").getData()[0].entityId;
			//this.byId("summEntityID").setSelectedKey(oEntity);
			//this._getDataSecurity(oEntity, "summaryfilterID");
			var vDate = new Date();
			this.byId("ReconsumaryDateID").setMaxDate(vDate);
			this.byId("ReconsumaryDateID").setDateValue(vDate);
			this.byId("summGSTINEntityID").setSelectedKeys([]);
			this.byId("DocTypeID").setSelectedKeys([]);
			this.byId("typeID").setSelectedKeys([]);
			var GstnInfo = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("ReconsumaryDateID").getValue(),
				"gstin": this.byId("summGSTINEntityID").getSelectedKeys(),
				"tableType": [],
				"docType": []
			};
			this.ResponseData(GstnInfo);
		},

		/////Added By Vinay Kodam ////////
		/////Recon Response Table Binding Function for Summary and Details/////////
		ResponseData: function (GstnInfo) {
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var GstnsList = "/aspsapapi/getReconRespDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(GstnInfo)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						oTaxReGstinModel.setData(data);
						oTaxReGstnView.setModel(oTaxReGstinModel, "ReconResponce");
					} else {
						oTaxReGstinModel.setData([]);
						oTaxReGstnView.setModel(oTaxReGstinModel, "ReconResponce");
					}
				}).fail(function (jqXHR, status, err) {
					oTaxReGstinModel.setData([]);
					oTaxReGstnView.setModel(oTaxReGstinModel, "ReconResponce");
				});
			});
		},

		//Added by Vinay 05-12-2019//
		onSelectionChangeRRERRecon: function (oEvt) {
			var selName = oEvt.getSource().getSelectedKey();
			if (selName === "Summary") {
				//this.byId("summaryfilterID").setVisible(true);
				//this.byId("filterDetail").setVisible(false);
				this.byId("idRREntity1").setVisible(false);
				this.byId("idRREntitysum").setVisible(true);
				this.byId("id_TypeFilter").setVisible(false);
			} else {
				//this.byId("summaryfilterID").setVisible(false);
				//this.byId("filterDetail").setVisible(true);
				this.byId("idRREntity1").setVisible(true);
				this.byId("idRREntitysum").setVisible(false);
				this.byId("id_TypeFilter").setVisible(true);
			}
		},

		onEntityChange2: function (GstnInfo) {
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var GstnsList = "/aspsapapi/getGSTINsForEntity.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(GstnInfo)
				}).done(function (data, status, jqXHR) {
					oTaxReGstinModel.setData(data);
					oTaxReGstnView.setModel(oTaxReGstinModel, "GstnLst1");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//Added by Vinay 16-12-2019//
		onReconSummGO: function () {
			var Request = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("reconSumDateID").getValue()
				}
			};
			this.onReconSumGSTINS(Request);
		},

		onReconSumGSTINS: function (Request) {
			var that = this;
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var GstnsList = "/aspsapapi/getDataForAnnx2ReconSummary.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						oTaxReGstinModel.setData(data.resp.det);
						oTaxReGstnView.setModel(oTaxReGstinModel, "GstnLstData");
						oTaxReGstnView.byId("gstinTab").selectAll();
						//var oEntitySum = that.getView().byId("ReconSumEntityID").getSelectedKey();
						//that._getDataSecurity(oEntitySum, "reconsumId");
						var gstin = [];
						for (var i = 0; i < data.resp.det.length; i++) {
							gstin.push(data.resp.det[i].gstin);
						}
						var RequestsummaryData = {
							"req": {
								"entityId": $.sap.entityID,
								"taxPeriod": that.byId("reconSumDateID").getValue(),
								"gstins": gstin,
								"profitCentres": [],
								"plants": [],
								"divisions": [],
								"locations": [],
								"purchaseOrgs": [],
								"userAccess1": [],
								"userAccess2": [],
								"userAccess3": [],
								"userAccess4": [],
								"userAccess5": [],
								"userAccess6": []
							}
						};
						that.RequestsummData(RequestsummaryData);
					} else {
						oTaxReGstinModel.setData([]);
						oTaxReGstnView.setModel(oTaxReGstinModel, "GstnLstData");
						oTaxReGstnView.setModel(oTaxReGstinModel, "ReconSummaryDetails");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oTaxReGstinModel.setData([]);
					oTaxReGstnView.setModel(oTaxReGstinModel, "GstnLstData");
					oTaxReGstnView.setModel(oTaxReGstinModel, "ReconSummaryDetails");
				});
			});
		},

		onPressReconSummGo: function () {
			var RequestsummaryData = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("reconSumDateID").getValue(),
					"gstins": this.byId("idfgiGSINTComboMain").getSelectedKeys(),
					"profitCentres": this.byId("slProfitCtr").getSelectedKeys(),
					"plants": this.byId("slPlant").getSelectedKeys(),
					"divisions": this.byId("slDivision").getSelectedKeys(),
					"locations": this.byId("slLocation").getSelectedKeys(),
					"purchaseOrgs": this.byId("slPurcOrg").getSelectedKeys(),
					"userAccess1": this.byId("slUserAccess1").getSelectedKeys(),
					"userAccess2": this.byId("slUserAccess2").getSelectedKeys(),
					"userAccess3": this.byId("slUserAccess3").getSelectedKeys(),
					"userAccess4": this.byId("slUserAccess4").getSelectedKeys(),
					"userAccess5": this.byId("slUserAccess5").getSelectedKeys(),
					"userAccess6": this.byId("slUserAccess6").getSelectedKeys()
				}
			};
			this.RequestsummData(RequestsummaryData);
		},

		RequestsummData: function (RequestsummaryData) {
			var that = this;
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var GstnsList = "/aspsapapi/Anx2ReconSummaryDetails.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(RequestsummaryData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var matchArray = [],
						misMatchArray = [],
						ProbableMatch = [],
						AdditionalEntries = [],
						ForcedMatch = [],
						/////////////////
						Anx2CountMatch = 0,
						Anx2perCountMatch = 0,
						AnxtaxvalueCountMatch = 0,
						AnxtotaltaxCountMatch = 0,
						Pr2CountMatch = 0,
						Pr2perCountMatch = 0,
						PrtaxvalueCountMatch = 0,
						PrtotaltaxCountMatch = 0,
						TotalAvailableTaxCountMatch = 0,
						///////////////
						Anx2CountMismatch = 0,
						Anx2perMismatch = 0,
						AnxtaxvalueMismatch = 0,
						AnxtotaltaxMismatch = 0,
						Pr2CountMismatch = 0,
						Pr2perMismatch = 0,
						PrtaxvalueMismatch = 0,
						PrtotaltaxMismatch = 0,
						TotalAvailableTaxMismatch = 0,
						////////////////////
						Anx2CountProbableMatch = 0,
						Anx2pertProbableMatch = 0,
						AnxtaxvalueProbableMatch = 0,
						AnxtotaltaxProbableMatch = 0,
						Pr2CountProbableMatch = 0,
						Pr2perProbableMatch = 0,
						PrtaxvalueProbableMatch = 0,
						PrtotaltaxProbableMatch = 0,
						TotalAvailableProbableMatch = 0,
						///////////////////////
						Anx2CountAdditionalEntries = 0,
						Anx2pertAdditionalEntries = 0,
						AnxtaxvalueAdditionalEntries = 0,
						AnxtotaltaxAdditionalEntries = 0,
						Pr2CountAdditionalEntries = 0,
						Pr2perAdditionalEntries = 0,
						PrtaxvalueAdditionalEntries = 0,
						PrtotaltaxAdditionalEntries = 0,
						TotalAvailableAdditionalEntries = 0,
						///////////////////////
						Anx2CountForcedMatch = 0,
						Anx2pertForcedMatch = 0,
						AnxtaxvalueForcedMatch = 0,
						AnxtotaltaxForcedMatch = 0,
						Pr2CountForcedMatch = 0,
						Pr2perForcedMatch = 0,
						PrtaxvalueForcedMatch = 0,
						PrtotaltaxForcedMatch = 0,
						TotalAvailableForcedMatch = 0;
					for (var i = 0; i < data.resp.det.length; i++) {
						data.resp.det[i].state = "Information";
						data.resp.det[i].IconDownload = true;
						data.resp.det[i].SuggestedRes = true;
						if (data.resp.det[i].category === "Match") {
							Anx2CountMatch += data.resp.det[i].anx2count;
							Anx2perCountMatch += data.resp.det[i].anx2per;
							AnxtaxvalueCountMatch += data.resp.det[i].anxtaxvalue;
							AnxtotaltaxCountMatch += data.resp.det[i].anxtotaltax;
							Pr2CountMatch += data.resp.det[i].pr2count;
							Pr2perCountMatch += data.resp.det[i].pr2per;
							PrtaxvalueCountMatch += data.resp.det[i].prtaxvalue;
							PrtotaltaxCountMatch += data.resp.det[i].prtotaltax;
							TotalAvailableTaxCountMatch += data.resp.det[i].totalAvailableTax;
							matchArray.push(data.resp.det[i]);
						} else if (data.resp.det[i].category === "Mismatches") {
							Anx2CountMismatch += data.resp.det[i].anx2count;
							Anx2perMismatch += data.resp.det[i].anx2per;
							AnxtaxvalueMismatch += data.resp.det[i].anxtaxvalue;
							AnxtotaltaxMismatch += data.resp.det[i].anxtotaltax;
							Pr2CountMismatch += data.resp.det[i].pr2count;
							Pr2perMismatch += data.resp.det[i].pr2per;
							PrtaxvalueMismatch += data.resp.det[i].prtaxvalue;
							PrtotaltaxMismatch += data.resp.det[i].prtotaltax;
							TotalAvailableTaxMismatch += data.resp.det[i].totalAvailableTax;
							misMatchArray.push(data.resp.det[i]);
						} else if (data.resp.det[i].category === "Probable Match") {
							Anx2CountProbableMatch += data.resp.det[i].anx2count;
							Anx2pertProbableMatch += data.resp.det[i].anx2per;
							AnxtaxvalueProbableMatch += data.resp.det[i].anxtaxvalue;
							AnxtotaltaxProbableMatch += data.resp.det[i].anxtotaltax;
							Pr2CountProbableMatch += data.resp.det[i].pr2count;
							Pr2perProbableMatch += data.resp.det[i].pr2per;
							PrtaxvalueProbableMatch += data.resp.det[i].prtaxvalue;
							PrtotaltaxProbableMatch += data.resp.det[i].prtotaltax;
							TotalAvailableProbableMatch += data.resp.det[i].totalAvailableTax;
							ProbableMatch.push(data.resp.det[i]);
						} else if (data.resp.det[i].category === "Forced Match") {
							Anx2CountForcedMatch += data.resp.det[i].anx2count;
							Anx2pertForcedMatch += data.resp.det[i].anx2per;
							AnxtaxvalueForcedMatch += data.resp.det[i].anxtaxvalue;
							AnxtotaltaxForcedMatch += data.resp.det[i].anxtotaltax;
							Pr2CountForcedMatch += data.resp.det[i].pr2count;
							Pr2perForcedMatch += data.resp.det[i].pr2per;
							PrtaxvalueForcedMatch += data.resp.det[i].prtaxvalue;
							PrtotaltaxForcedMatch += data.resp.det[i].prtotaltax;
							TotalAvailableForcedMatch += data.resp.det[i].totalAvailableTax;
							ForcedMatch.push(data.resp.det[i]);
						} else if (data.resp.det[i].category === "Additional Entries") {
							Anx2CountAdditionalEntries += data.resp.det[i].anx2count;
							Anx2pertAdditionalEntries += data.resp.det[i].anx2per;
							AnxtaxvalueAdditionalEntries += data.resp.det[i].anxtaxvalue;
							AnxtotaltaxAdditionalEntries += data.resp.det[i].anxtotaltax;
							Pr2CountAdditionalEntries += data.resp.det[i].pr2count;
							Pr2perAdditionalEntries += data.resp.det[i].pr2per;
							PrtaxvalueAdditionalEntries += data.resp.det[i].prtaxvalue;
							PrtotaltaxAdditionalEntries += data.resp.det[i].prtotaltax;
							TotalAvailableAdditionalEntries += data.resp.det[i].totalAvailableTax;
							AdditionalEntries.push(data.resp.det[i]);
						}
					}

					var response1 = {
						"response": [{
							"particulars": "Match",
							"IconDownload": false,
							"SuggestedRes": false,
							"anx2count": Anx2CountMatch,
							"anx2per": Anx2perCountMatch,
							"anxtaxvalue": AnxtaxvalueCountMatch,
							"anxtotaltax": AnxtotaltaxCountMatch,
							"pr2count": Pr2CountMatch,
							"pr2per": Pr2perCountMatch,
							"prtaxvalue": PrtaxvalueCountMatch,
							"prtotaltax": PrtotaltaxCountMatch,
							"totalAvailableTax": TotalAvailableTaxCountMatch,
							"category": matchArray
						}, {
							"particulars": "Mismatches",
							"IconDownload": false,
							"SuggestedRes": false,
							"anx2count": Anx2CountMismatch,
							"anx2per": Anx2perMismatch,
							"anxtaxvalue": AnxtaxvalueMismatch,
							"anxtotaltax": AnxtotaltaxMismatch,
							"pr2count": Pr2CountMismatch,
							"pr2per": Pr2perMismatch,
							"prtaxvalue": PrtaxvalueMismatch,
							"prtotaltax": PrtotaltaxMismatch,
							"totalAvailableTax": TotalAvailableTaxMismatch,
							"category": misMatchArray
						}, {
							"particulars": "Probable Match",
							"IconDownload": false,
							"SuggestedRes": false,
							"anx2count": Anx2CountProbableMatch,
							"anx2per": Anx2pertProbableMatch,
							"anxtaxvalue": AnxtaxvalueProbableMatch,
							"anxtotaltax": AnxtotaltaxProbableMatch,
							"pr2count": Pr2CountProbableMatch,
							"pr2per": Pr2perProbableMatch,
							"prtaxvalue": PrtaxvalueProbableMatch,
							"prtotaltax": PrtotaltaxProbableMatch,
							"totalAvailableTax": TotalAvailableProbableMatch,
							"category": ProbableMatch
						}, {
							"particulars": "Forced Match",
							"IconDownload": false,
							"SuggestedRes": false,
							"anx2count": Anx2CountForcedMatch,
							"anx2per": Anx2pertForcedMatch,
							"anxtaxvalue": AnxtaxvalueForcedMatch,
							"anxtotaltax": AnxtotaltaxForcedMatch,
							"pr2count": Pr2CountForcedMatch,
							"pr2per": Pr2perForcedMatch,
							"prtaxvalue": PrtaxvalueForcedMatch,
							"prtotaltax": PrtotaltaxForcedMatch,
							"totalAvailableTax": TotalAvailableForcedMatch,
							"category": ForcedMatch
						}, {
							"particulars": "Additional Entries",
							"IconDownload": false,
							"SuggestedRes": false,
							"anx2count": Anx2CountAdditionalEntries,
							"anx2per": Anx2pertAdditionalEntries,
							"anxtaxvalue": AnxtaxvalueAdditionalEntries,
							"anxtotaltax": AnxtotaltaxAdditionalEntries,
							"pr2count": Pr2CountAdditionalEntries,
							"pr2per": Pr2perAdditionalEntries,
							"prtaxvalue": PrtaxvalueAdditionalEntries,
							"prtotaltax": PrtotaltaxAdditionalEntries,
							"totalAvailableTax": TotalAvailableAdditionalEntries,
							"category": AdditionalEntries
						}]
					};
					oTaxReGstinModel.setData(response1.response);
					oTaxReGstnView.setModel(oTaxReGstinModel, "ReconSummaryDetails");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onSelection: function (oEvt) {
			var selItems = oEvt.getSource().getSelectedIndices();
			var data = this.getView().getModel("GstnLstData").getData();
			var gstin = [];
			for (var i = 0; i < selItems.length; i++) {
				gstin.push(data[selItems[i]].gstin);
			}
			if (gstin.length !== 0) {
				var RequestsummaryData = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": this.byId("reconSumDateID").getValue(),
						"gstins": gstin
					}
				};
				this.RequestsummData(RequestsummaryData);
			} else {
				var oTaxReGstinModel = new JSONModel();
				oTaxReGstinModel.setData([]);
				this.getView().setModel(oTaxReGstinModel, "ReconSummaryDetails");
			}
		},

		onSearchGstins: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("gstinTab");
			var oBinding = oList.getBinding();
			oBinding.filter(aFilter);
		},

		onExpandFirstLevel: function () {
			this.byId("userResponceID").setVisible(true);
			this.byId("suggestedrespID").setVisible(true);
			var oTreeTable = this.byId("id_TreeRecon");
			oTreeTable.expandToLevel(1);
			this.getView().byId("id_TreeRecon").setVisibleRowCount(20);
		},

		onCollapseAll: function () {
			var oTreeTable = this.byId("id_TreeRecon");
			oTreeTable.collapseAll();
			this.getView().byId("id_TreeRecon").setVisibleRowCount(6);
			this.byId("userResponceID").setVisible(false);
			this.byId("suggestedrespID").setVisible(false);
		},

		toggleOpenState1: function (oEvt) {
			if (oEvt.getParameters().expanded === true) {
				this.byId("userResponceID").setVisible(true);
				this.byId("suggestedrespID").setVisible(true);
				this.getView().byId("id_TreeRecon").setVisibleRowCount(14);
			}
		},

		onPressParticulars: function (oEvent) {
			var oText = oEvent.getSource().getText();
			if (oText === "Exact Match") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("ExactMatch");
				this.onSelectionChangeReconResultFinal1("ExactMatch");

			} else if (oText === "Match with Tolerance") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("MatchuptoTolerance");
				this.onSelectionChangeReconResultFinal1("MatchuptoTolerance");
			} else if (oText === "Document date,Taxable Value, Tax amount/ POS and above Combinations") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("DTTPC");
				this.onSelectionChangeReconResultFinal1("DTTPC");
			} else if (oText === "Doc Type Mismatch") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("DocTypeMismatch");
				this.onSelectionChangeReconResultFinal1("DocTypeMismatch");
			} else if (oText === "Doc Date Mismatch") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("DocDateMismatch");
				this.onSelectionChangeReconResultFinal1("DocDateMismatch");
			} else if (oText === "Value Mismatch") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("ValueMismatch");
				this.onSelectionChangeReconResultFinal1("ValueMismatch");
			} else if (oText === "Probable - I") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("ProbableI");
				this.onSelectionChangeReconResultFinal1("ProbableI");
			} else if (oText === "Probable - II") {
				this.oView.byId("idActionSB").setVisible(true);
				this.oView.byId("idInformationSB").setVisible(false);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Action");
				this.oView.byId("idActionSB").setSelectedKey("ProbableII");
				this.onSelectionChangeReconResultFinal1("ProbableII");
			} else if (oText === "Potential - I") {
				this.oView.byId("idActionSB").setVisible(false);
				this.oView.byId("idInformationSB").setVisible(true);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Information");
			} else if (oText === "Potential - II (Dynamic)") {
				this.oView.byId("idActionSB").setVisible(false);
				this.oView.byId("idInformationSB").setVisible(true);
				this.oView.byId("iditbAnnexure2").setSelectedKey("ReconResult");
				this.oView.byId("idActionInformation").setSelectedKey("Information");
			}

		},

		onSummaryTableTypeChange: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All")) {
				this.All6 = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKey = oEvt.getSource().getSelectedKeys();
				this.selectedKey.shift();
			} else if (this.All6 === "A") {
				this.All6 = "";
				this.selectedKey = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKey = SelKey;
			}
		},

		onSummaryTableTypeChange1: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All") && (SelKey.length === 1 || SelKey.length === 7) && (this.All === undefined || this.All !== "A")) {
				this.All = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKey1 = oEvt.getSource().getSelectedKeys();
				this.selectedKey1.shift();
			} else if (SelKey.includes("All") && SelKey.length !== 7 && SelKey.length !== 1) {
				this.All = "A";
				oEvt.getSource().setSelectedKeys(SelKey);
				this.selectedKey1 = oEvt.getSource().getSelectedKeys();
				this.selectedKey1.shift();
			} else if (this.All === "A") {
				this.All = "";
				this.selectedKey1 = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKey1 = SelKey;
			}
		},

		onSummaryTableTypeChange2: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All") && (SelKey.length === 1 || SelKey.length === 4) && (this.All1 === undefined || this.All1 !== "A")) {
				this.All1 = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKey2 = oEvt.getSource().getSelectedKeys();
				this.selectedKey2.shift();
			} else if (SelKey.includes("All") && SelKey.length !== 4 && SelKey.length !== 1) {
				this.All1 = "A";
				oEvt.getSource().setSelectedKeys(SelKey);
				this.selectedKey2 = oEvt.getSource().getSelectedKeys();
				this.selectedKey2.shift();
			} else if (this.All1 === "A") {
				this.All1 = "";
				this.selectedKey2 = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKey2 = SelKey;
			}
		},
		//////////////////
		onSelectionTypeChange: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All") && (SelKey.length === 1 || SelKey.length === 5) && (this.All2 === undefined || this.All2 !== "A")) {
				this.All2 = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKeyDeep = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep.shift();
			} else if (SelKey.includes("All") && SelKey.length !== 5 && SelKey.length !== 1) {
				this.All2 = "A";
				oEvt.getSource().setSelectedKeys(SelKey);
				this.selectedKeyDeep = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep.shift();
			} else if (this.All2 === "A") {
				this.All2 = "";
				this.selectedKeyDeep = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKeyDeep = SelKey;
			}
		},

		onSelectionTypeChange1: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All") && (SelKey.length === 1 || SelKey.length === 7) && (this.AllD === undefined || this.AllD !== "A")) {
				this.AllD = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKeyDeep1 = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep1.shift();
			} else if (SelKey.includes("All") && SelKey.length !== 7 && SelKey.length !== 1) {
				this.AllD = "A";
				oEvt.getSource().setSelectedKeys(SelKey);
				this.selectedKeyDeep1 = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep1.shift();
			} else if (this.AllD === "A") {
				this.AllD = "";
				this.selectedKeyDeep1 = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKeyDeep1 = SelKey;
			}
		},

		onSelectionTypeChange2: function (oEvt) {
			var SelKey = oEvt.getSource().getSelectedKeys();
			if (SelKey.includes("All") && (SelKey.length === 1 || SelKey.length === 4) && (this.All4 === undefined || this.All4 !== "A")) {
				this.All4 = "A";
				oEvt.getSource().setSelectedItems(oEvt.getSource().getItems());
				this.selectedKeyDeep2 = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep2.shift();
			} else if (SelKey.includes("All") && SelKey.length !== 4 && SelKey.length !== 1) {
				this.All4 = "A";
				oEvt.getSource().setSelectedKeys(SelKey);
				this.selectedKeyDeep2 = oEvt.getSource().getSelectedKeys();
				this.selectedKeyDeep2.shift();
			} else if (this.All4 === "A") {
				this.All4 = "";
				this.selectedKeyDeep2 = [];
				oEvt.getSource().setSelectedKeys([]);
			} else {
				this.selectedKeyDeep2 = SelKey;
			}
		},

		//added by Vinay 23-12-2019//
		handleLinkPressRRGSTIN12: function (oEvt) {
			var gstin = oEvt.getSource().getText();
			this.byId("idRREntity1").setVisible(false);
			this.byId("idRREntitysum").setVisible(false);
			this.byId("idRRSummaryReport1").setVisible(true);
			this.byId("idEntityLevelBack1").setVisible(true);
			this.byId("idEntityLevelTitle1").setText("Review Summary");
			this.byId("summaryfilterID").setVisible(false);
			this.byId("filterDetail").setVisible(true);
			this.gstin = oEvt.getSource().getText();
			this.byId("EntityID1").setSelectedKey($.sap.entityID);
			var Date = this.byId("ReconsumaryDateID").getValue();
			this.byId("ReconsumaryDateID1").setValue(Date);
			//var TabType = this.byId("tableTypeID").getSelectedKeys();
			//this.byId("detailTabTypeID").setSelectedKeys(TabType);
			var DocType = this.byId("DocTypeID").getSelectedKeys();
			this.byId("detailDocTypeID").setSelectedKeys(DocType);
			var Type = this.byId("typeID").getSelectedKeys();
			this.byId("detailTypeID").setSelectedKeys(Type);
			//var oEntitySum = this.getView().byId("EntityID1").getSelectedKey();
			//this._getDataSecurity(oEntitySum, "filterDetail");
			this.byId("GSTINEntityID1").setSelectedKey(gstin);
			var RRGstnInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("ReconsumaryDateID").getValue(),
					"tableType": this.selectedKey === undefined ? [] : this.selectedKey,
					"docType": this.selectedKey1 === undefined ? [] : this.selectedKey1,
					"type": this.selectedKey2 === undefined ? [] : this.selectedKey2
				}
			};
			this.onPressSummGSTIN(RRGstnInfo);
		},

		handleLinkPressReconResBack1: function () {
			var Key = this.byId("idSelectionChangeRRER").getSelectedKey();
			this.byId("idEntityLevelTitle1").setText("Entity Level");
			this.byId("summaryfilterID").setVisible(true);
			this.byId("filterDetail").setVisible(false);
			if (Key === "Summary") {
				this.byId("idRREntity1").setVisible(false);
				this.byId("idRREntitysum").setVisible(true);
				this.byId("idRRSummaryReport1").setVisible(false);
				this.byId("idEntityLevelBack1").setVisible(false);
			} else {
				this.byId("idRREntity1").setVisible(true);
				this.byId("idRREntitysum").setVisible(false);
				this.byId("idRRSummaryReport1").setVisible(false);
				this.byId("idEntityLevelBack1").setVisible(false);
			}
		},

		handleLinkPressRRGSTIN1: function (oEvt) {
			var gstin = oEvt.getSource().getText();
			this.byId("idRREntity1").setVisible(false);
			this.byId("idRREntitysum").setVisible(false);
			this.byId("idRRSummaryReport1").setVisible(true);
			this.byId("idEntityLevelBack1").setVisible(true);
			this.byId("idEntityLevelTitle1").setText("Review Summary");
			this.byId("summaryfilterID").setVisible(false);
			this.byId("filterDetail").setVisible(true);
			this.byId("EntityID1").setSelectedKey($.sap.entityID);
			var Date = this.byId("ReconsumaryDateID").getValue();
			this.byId("ReconsumaryDateID1").setValue(Date);
			//var TabType = this.byId("tableTypeID").getSelectedKeys();
			//this.byId("detailTabTypeID").setSelectedKeys(TabType);
			var DocType = this.byId("DocTypeID").getSelectedKeys();
			this.byId("detailDocTypeID").setSelectedKeys(DocType);
			var Type = this.byId("typeID").getSelectedKeys();
			this.byId("detailTypeID").setSelectedKeys(Type);
			//var oEntitySum = this.getView().byId("summEntityID").getSelectedKey();
			//this._getDataSecurity(oEntitySum, "summaryfilterID");
			this.byId("GSTINEntityID1").setSelectedKey(gstin);
			var RRGstnInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("ReconsumaryDateID").getValue(),
					"tableType": this.selectedKey === undefined ? [] : this.selectedKey,
					"docType": this.selectedKey1 === undefined ? [] : this.selectedKey1,
					"type": this.selectedKey2 === undefined ? [] : this.selectedKey2
				}
			};
			this.onPressSummGSTIN(RRGstnInfo);
		},

		onPressSummGSTIN: function (RRGstnInfo) {
			var that = this;
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var RRGstnsList = "/aspsapapi/getReconResponseReviewSummary.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RRGstnsList,
					contentType: "application/json",
					data: JSON.stringify(RRGstnInfo)
				}).done(function (data, status, jqXHR) {
					//that._getDataSecurity(that.gstin, "filterDetail");
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					//	if (data.resp.length !== 0) {
					for (var i = 0; i < data.resp.length; i++) {
						var ele = data.resp[i];
						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							ele.docType = "";
							ele.exactMatch = "";
							ele.matchUpToTolerance = "";
							ele.valueMismatch = "";
							ele.posMismatch = "";
							ele.docDateMismatch = "";
							ele.multiMismatch = "";
							ele.docTypeMismatch = "";
							ele.probable1 = "";
							ele.peobable2 = "";
							ele.fuzzyMatch = "";
							ele.addtionalAnx2 = "";
							ele.addtionalPR = "";
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							ele.tableType = "";
							ele.exactMatch = "";
							ele.matchUpToTolerance = "";
							ele.valueMismatch = "";
							ele.posMismatch = "";
							ele.docDateMismatch = "";
							ele.multiMismatch = "";
							ele.docTypeMismatch = "";
							ele.probable1 = "";
							ele.peobable2 = "";
							ele.fuzzyMatch = "";
							ele.addtionalAnx2 = "";
							ele.addtionalPR = "";
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							ele.tableType = "";
							ele.docType = "";
							curL2Obj.level3.push(ele);
						}
					}
					oTaxReGstinModel.setData(retArr);
					oTaxReGstnView.setModel(oTaxReGstinModel, "SummDetailGstin");
					//	}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onReconRespLinkData: function () {
			var RRGstnInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": this.byId("GSTINEntityID1").getSelectedKey(),
					"taxPeriod": this.byId("ReconsumaryDateID1").getValue(),
					"tableType": this.selectedKeyDeep,
					"docType": this.selectedKeyDeep1,
					"type": this.selectedKeyDeep2
				}
			};
			this.onPressSummGSTIN(RRGstnInfo);
		},

		/////////added by Vinay Kodam ////////////
		/////////On click of Recon Response GSTIN in Table function//////////
		onClearFilterRecRespLink: function () {
			//var entity = this.byId("summEntityID").getSelectedKey();
			this.byId("EntityID1").setSelectedKey($.sap.entityID);
			//this._getDataSecurity(entity, "filterDetail");
			this.byId("GSTINEntityID1").setSelectedKey(this.gstin);
			var Date = this.byId("ReconsumaryDateID").getValue();
			this.byId("ReconsumaryDateID1").setValue(Date);
			var DocType = this.byId("DocTypeID").getSelectedKeys();
			this.byId("detailDocTypeID").setSelectedKeys(DocType);
			var Type = this.byId("typeID").getSelectedKeys();
			this.byId("detailTypeID").setSelectedKeys(Type);
			this.byId("detailTabTypeID").setSelectedKeys([]);
			var RRGstnInfo = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},

				"req": {
					"gstin": this.byId("GSTINEntityID1").getSelectedKey(),
					"taxPeriod": this.byId("ReconsumaryDateID1").getValue(),
					"tableType": [],
					"docType": this.byId("detailDocTypeID").getSelectedKeys(),
					"type": this.byId("detailTypeID").getSelectedKeys()
				}
			};
			this.onPressSummGSTIN(RRGstnInfo);
		},

		/////// vendor summary Changes 27-12-19 by Vinay //////////////////

		onSelectGSTINs: function (oEvt) {
			var that = this;
			var Request = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.getView().byId("ReconSummDateId").getValue(),
				"gstins": this.getView().byId("ReconSummGSTNId").getSelectedKeys()
			};
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var oTaxReGstinModel1 = new JSONModel();
			var oTaxReGstnView1 = this.getView();
			var RRGstnsList = "/aspsapapi/getSupplierFilterForEntity.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RRGstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "E") {
						oTaxReGstinModel.setData([]);
						oTaxReGstnView.setModel(oTaxReGstinModel, "VendorDetalils");
						oTaxReGstinModel1.setData([]);
						that.newArray1 = [];
						oTaxReGstnView1.setModel(oTaxReGstinModel1, "VendorDetalils1");
					} else {
						that.data = data.resp.det.sgstins;
						oTaxReGstinModel.setData(data.resp.det.cPans);
						oTaxReGstnView.setModel(oTaxReGstinModel, "VendorDetalils");
						oTaxReGstinModel1.setData(data.resp.det.sgstins);
						that.newArray1 = data.resp.det.sgstins;
						oTaxReGstnView1.setModel(oTaxReGstinModel1, "VendorDetalils1");
					}
				}).fail(function (jqXHR, status, err) {});
			});

		},

		onRecRespGSTIN: function (oEvt) {
			var that = this;
			var Request = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.getView().byId("ReconRespDateId").getValue(),
				"gstins": this.getView().byId("ReconRespGSTNId").getSelectedKeys()
			};
			var oTaxReGstinModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			var oTaxReGstinModel1 = new JSONModel();
			var oTaxReGstnView1 = this.getView();
			var RRGstnsList = "/aspsapapi/getSupplierFilterForEntity.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: RRGstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "E") {
						oTaxReGstinModel.setData([]);
						oTaxReGstnView.setModel(oTaxReGstinModel, "VendorDetalilsRecResp");
						oTaxReGstinModel1.setData([]);
						that.newArray1 = [];
						oTaxReGstnView1.setModel(oTaxReGstinModel1, "VendorDetalilsRecResp1");
					} else {
						that.data1 = data.resp.det.sgstins;
						oTaxReGstinModel.setData(data.resp.det.cPans);
						oTaxReGstnView.setModel(oTaxReGstinModel, "VendorDetalilsRecResp");
						oTaxReGstinModel1.setData(data.resp.det.sgstins);
						that.newArray1 = data.resp.det.sgstins;
						oTaxReGstnView1.setModel(oTaxReGstinModel1, "VendorDetalilsRecResp1");
					}
				}).fail(function (jqXHR, status, err) {});
			});

		},

		///////BOC Recon Result Screen changes by Rakesh on 15-01-2020 //////////////////
		//Segment Buttons - Action and  Information//
		onSelectionActionInformation: function (oEvent) {
			var actInfoSB = oEvent.getSource().getSelectedKey();
			if (actInfoSB === "Action") {
				/*this.getView().byId("idReconResuCon").setVisible(true);*/
				/*this.getView().byId("idconsolidatedDetail").setVisible(false);*/
			} else if (actInfoSB === "Information") {
				/*this.getView().byId("idReconResuCon").setVisible(false);*/
				/*this.getView().byId("idconsolidatedDetail").setVisible(true);*/

			}
		},

		onSelectionChangeReconResultFinal: function (oEvt) {
			var oSelectedKey = oEvt.getSource().getSelectedKey();
			this.onSelectionChangeReconResultFinal1(oSelectedKey);
		},

		onSelectionChangeReconResultFinal1: function (oSelectedKey) {
			var taxPeriod = this.getView().byId("dtConsld").getValue();
			var Gstns = this.oView.byId("idRRConsGstins").getSelectedKeys();
			var RRdocType = this.getView().byId("DocTypeIDConslM").getSelectedKeys();
			if (oSelectedKey === "ConsolidatedMatching") {
				this.getView().byId("idConsoleMatch").setVisible(true);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(true);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.byId("idConsldReptType").setSelectedKeys(this.ConslRetnRT); //Retain Retun type in Segment Button
				this.onSelctnChgRRConsol();
			} else if (oSelectedKey === "ExactMatch") {
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(true);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////////Filters//////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(true);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("ExactDateId").setValue(taxPeriod);
				this.byId("DocTypeIDExctM").setSelectedKeys(RRdocType);
				if (this.oExactMDate !== undefined) {
					var a = new Date(this.oExactMDate);
					this.byId("ExactDateId").setDateValue(a);
				}
				if (this.exactKey !== undefined) {
					this.byId("idRRExctMatchGstins").setSelectedKey(this.ExactGSTIN);
				} else {
					this.ExactGSTIN = Gstns;
				}

				if (this.ExactDocTyp !== undefined) {
					this.byId("DocTypeIDExctM").setSelectedKeys(this.ExactDocTyp);
				}

				this.onExactMatchGo();

			} else if (oSelectedKey === "MatchuptoTolerance") {
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(true);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(true);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("MUTDateId").setValue(taxPeriod);

				if (this.oMUTMDate !== undefined) {
					var mutDate = new Date(this.oMUTMDate);
					this.byId("MUTDateId").setDateValue(mutDate);
				}
				if (this.MUTKey !== undefined) {
					this.byId("idRRMatchUTGstins").setSelectedKeys(this.MUTGSTIN);
				} else {
					this.MUTGSTIN = Gstns;
				}

				this.byId("DocTypeIDMUTM").setSelectedKeys(RRdocType);
				if (this.MUTDocType !== undefined) {
					this.byId("DocTypeIDMUTM").setSelectedKeys(this.MUTDocType);
				}
				this.onMatchUptoGo();

			} else if (oSelectedKey === "DocTypeMismatch") {
				this.getView().byId("idDocTypMisMatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(true);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("DTMDateId").setValue(taxPeriod);

				if (this.oDTypMDate !== undefined) {
					var dtpDate = new Date(this.oDTypMDate);
					this.byId("DTMDateId").setDateValue(dtpDate);
				}
				if (this.DTypKey !== undefined) {
					this.byId("idRRDTMGstins").setSelectedKeys(this.DTMGSTIN);
				} else {
					this.DTMGSTIN = Gstns;
				}

				this.byId("DocTypeIDDTM").setSelectedKeys(RRdocType);
				if (this.DTMDocType !== undefined) {
					this.byId("DocTypeIDDTM").setSelectedKeys(this.DTMDocType);
				}

				this.onDocTypeMisGo();

			} else if (oSelectedKey === "DocDateMismatch") {
				this.getView().byId("idDocDateMisMatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(true);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("DDMDateId").setValue(taxPeriod);

				if (this.oDocDMDate !== undefined) {
					var a = new Date(this.oDocDMDate);
					this.byId("DDMDateId").setDateValue(a);
				}
				if (this.DDMKey !== undefined) {
					this.byId("idRRDDMGstins").setSelectedKeys(this.oDocDMDate);
				} else {
					this.DDMGSTIN = Gstns;
				}

				this.byId("DocTypeIDDDM").setSelectedKeys(RRdocType);
				if (this.DDMDocType !== undefined) {
					this.byId("DocTypeIDDDM").setSelectedKeys(this.DDMDocType);
				}
				this.onDocDateMisGo();

			} else if (oSelectedKey === "ValueMismatch") {
				this.getView().byId("idValueMismatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(true);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("ValMDateId").setValue(taxPeriod);

				if (this.oValMDate !== undefined) {
					var a = new Date(this.oValMDate);
					this.byId("ValMDateId").setDateValue(a);
				}
				if (this.ValueMKey !== undefined) {
					this.byId("idRRValMGstins").setSelectedKeys(this.ValMGSTIN);
				} else {
					this.ValMGSTIN = Gstns;
				}

				this.byId("DocTypeIDValM").setSelectedKeys(RRdocType);
				if (this.ValDocType !== undefined) {
					this.byId("DocTypeIDValM").setSelectedKeys(this.ValDocType);
				}
				this.onValueMisGo();

			} else if (oSelectedKey === "POSMismatch") {
				this.getView().byId("idPOSMismatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(true);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("POSMDateId").setValue(taxPeriod);

				if (this.oPOSMDate !== undefined) {
					var a = new Date(this.oPOSMDate);
					this.byId("POSMDateId").setDateValue(a);
				}
				if (this.POSMKey !== undefined) {
					this.byId("idRRPOSMGstins").setSelectedKeys(this.POSMGSTIN);
				} else {
					this.POSMGSTIN = Gstns;
				}

				this.byId("DocTypeIDPOS").setSelectedKeys(RRdocType);
				if (this.POSDocType !== undefined) {
					this.byId("DocTypeIDPOS").setSelectedKeys(this.POSDocType);
				}
				this.onPOSMisGo();

			} else if (oSelectedKey === "DTTPC") {
				this.getView().byId("idMultiMisMatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(true);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("MultiMDateId").setValue(taxPeriod);

				if (this.oMultiMDate !== undefined) {
					var a = new Date(this.oMultiMDate);
					this.byId("MultiMDateId").setDateValue(a);
				}
				if (this.MultiMKey !== undefined) {
					this.byId("idRRMultiMGstins").setSelectedKeys(this.MultiMGSTIN);
				} else {
					this.MultiMGSTIN = Gstns;
				}

				this.byId("DocTypeIDMultiM").setSelectedKeys(RRdocType);
				if (this.MultiMDocType !== undefined) {
					this.byId("DocTypeIDMultiM").setSelectedKeys(this.MultiMDocType);
				}
				this.onMultiMisGo();

			} else if (oSelectedKey === "FuzzyMatch") {
				this.getView().byId("idFuzzyMatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(true);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("FuzzyMDateId").setValue(taxPeriod);
				if (this.oFuzzyMDate !== undefined) {
					var a = new Date(this.oFuzzyMDate);
					this.byId("FuzzyMDateId").setDateValue(a);
				}
				if (this.FuzyMKey !== undefined) {
					this.byId("idRRFuzzyMGstins").setSelectedKeys(this.FuzyMGSTIN);
				} else {
					this.FuzyMGSTIN = Gstns;
				}

				this.byId("DocTypeIDFuzy").setSelectedKeys(RRdocType);
				if (this.FuzyDocType !== undefined) {
					this.byId("DocTypeIDFuzy").setSelectedKeys(this.FuzyDocType);
				}
				this.onFuzzyMisGo();

			} else if (oSelectedKey === "ForceMatch") {
				this.getView().byId("idForceMatch").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(true);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);

				this.getView().byId("FMForceMDateId").setValue(taxPeriod);
				if (this.oForceMDate !== undefined) {
					var a = new Date(this.oForceMDate);
					this.byId("FMForceMDateId").setDateValue(a);
				}
				if (this.FMForceMKey !== undefined) {
					this.byId("idRRFMForceMGstins").setSelectedKeys(this.FMForceGSTIN);
				} else {
					this.FMForceGSTIN = Gstns;
				}

				this.byId("DocTypeIDFMF").setSelectedKeys(RRdocType);
				if (this.FMMDocType !== undefined) {
					this.byId("DocTypeIDFMF").setSelectedKeys(this.FMMDocType);
				}

				this.getView().byId("idFMAdnlEntrsDate").setValue(taxPeriod);
				if (this.oFMAddlnDate !== undefined) {
					var a = new Date(this.oFMAddlnDate);
					this.byId("idFMAdnlEntrsDate").setDateValue(a);
				}
				if (this.FMAddnlGSTINKey !== undefined) {
					this.byId("idFMAdnlEntrsGstins").setSelectedKeys(this.FMAddnlGSTIN);
				} else {
					this.FMAddnlGSTIN = Gstns;
				}

				this.byId("DocTypeIDFMAE").setSelectedKeys(RRdocType);
				if (this.FMAdnlDocType !== undefined) {
					this.byId("DocTypeIDFMAE").setSelectedKeys(this.FMAdnlDocType);
				}

				this.onFMForceMatcdGo();
				this.onFMAdnlEntrsGo();

			} else if (oSelectedKey === "AdditioninANX2") {
				this.getView().byId("idAdditioninANX2").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninPR").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(true);
				this.byId("idAddtnPRfltrBar").setVisible(false);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("AddtnANX2DateId").setValue(taxPeriod);
				if (this.oAddnlANX2Date !== undefined) {
					var a = new Date(this.oAddnlANX2Date);
					this.byId("AddtnANX2DateId").setDateValue(a);
				}
				if (this.AdnlANX2Key !== undefined) {
					this.byId("idRRAddtnANX2Gstins").setSelectedKeys(this.AddtnANX2GSTIN);
				} else {
					this.AddtnANX2GSTIN = Gstns;
				}

				this.byId("DocTypeIDANX2").setSelectedKeys(RRdocType);
				if (this.AdnlANX2DocType !== undefined) {
					this.byId("DocTypeIDANX2").setSelectedKeys(this.AdnlANX2DocType);
				}
				this.onAddtnANX2Go();

			} else if (oSelectedKey === "AdditioninPR") {
				this.getView().byId("idAdditioninPR").setVisible(true);
				this.getView().byId("idConsoleMatch").setVisible(false);
				this.getView().byId("idExactMatch").setVisible(false);
				this.getView().byId("idMatchUptoTolrnce").setVisible(false);
				this.getView().byId("idDocTypMisMatch").setVisible(false);
				this.getView().byId("idDocDateMisMatch").setVisible(false);
				this.getView().byId("idValueMismatch").setVisible(false);
				this.getView().byId("idPOSMismatch").setVisible(false);
				this.getView().byId("idMultiMisMatch").setVisible(false);
				this.getView().byId("idFuzzyMatch").setVisible(false);
				this.getView().byId("idForceMatch").setVisible(false);
				this.getView().byId("idAdditioninANX2").setVisible(false);
				//////////Filters/////////////
				this.byId("idConsldfltrBar").setVisible(false);
				this.byId("idMatchfltrBar").setVisible(false);
				this.byId("idMatchUptofltrBar").setVisible(false);
				this.byId("idDocTyMisfltrBar").setVisible(false);
				this.byId("idDocDateMisfltrBar").setVisible(false);
				this.byId("idValueMisMfltrBar").setVisible(false);
				this.byId("idPOSMisMfltrBar").setVisible(false);
				this.byId("idMultiMisMfltrBar").setVisible(false);
				this.byId("idFuzzyMisMfltrBar").setVisible(false);
				this.byId("idFMForceMfltrBar").setVisible(false);
				this.byId("idAddtnANX2fltrBar").setVisible(false);
				this.byId("idAddtnPRfltrBar").setVisible(true);
				this.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.getView().byId("AddtnPRDateId").setValue(taxPeriod);
				if (this.oAddnlPRDate !== undefined) {
					var a = new Date(this.oAddnlPRDate);
					this.byId("AddtnPRDateId").setDateValue(a);
				}
				if (this.AdnlPRKey !== undefined) {
					this.byId("idRRAddtnPRGstins").setSelectedKeys(this.AddtnPRGSTIN);
				} else {
					this.AddtnPRGSTIN = Gstns;
				}

				this.byId("DocTypeIDPR").setSelectedKeys(RRdocType);
				if (this.AdnlPRDocType !== undefined) {
					this.byId("DocTypeIDPR").setSelectedKeys(this.AdnlPRDocType);
				}
				this.onAddtnInPRGo();
			}
		},

		//on click of Force Match Tab(Force Matched & Additional Entries)
		onSelectionForcematch: function (oEvent) {
			var taxPeriod = this.getView().byId("dtConsld").getValue();
			var Gstns = this.oView.byId("idRRConsGstins").getSelectedKeys();
			var RRdocType = this.getView().byId("DocTypeIDConslM").getSelectedKeys();
			if (oEvent.getSource().getSelectedKey() === "Matched") {
				this.getView().byId("FMForceMDateId").setValue(taxPeriod);
				if (this.oForceMDate !== undefined) {
					var a = new Date(this.oForceMDate);
					this.byId("FMForceMDateId").setDateValue(a);
				}
				if (this.FMForceMKey !== undefined) {
					this.byId("idRRFMForceMGstins").setSelectedKeys(this.FMForceGSTIN);
				} else {
					this.FMForceGSTIN = Gstns;
				}

				this.byId("DocTypeIDFMF").setSelectedKeys(RRdocType);
				if (this.FMMDocType !== undefined) {
					this.byId("DocTypeIDFMF").setSelectedKeys(this.FMMDocType);
				}
				this.oView.byId("idFMForceMatchTab").setVisible(true);
				this.oView.byId("idFMAddtnlEntrsTab").setVisible(false);
				this.oView.byId("idFMAddtnlANX2").setVisible(false);
				this.oView.byId("idFMForceMfltrBar").setVisible(true);
				this.oView.byId("idFMAdnlEntrsfltrBar").setVisible(false);
				this.onFMForceMatcdGo();
			} else {
				this.getView().byId("idFMAdnlEntrsDate").setValue(taxPeriod);
				if (this.oFMAddlnDate !== undefined) {
					var a = new Date(this.oFMAddlnDate);
					this.byId("idFMAdnlEntrsDate").setDateValue(a);
				}
				if (this.FMAddnlGSTINKey !== undefined) {
					this.byId("idFMAdnlEntrsGstins").setSelectedKeys(this.FMAddnlGSTIN);
				} else {
					this.FMAddnlGSTIN = Gstns;
				}

				this.byId("DocTypeIDFMAE").setSelectedKeys(RRdocType);
				if (this.FMAdnlDocType !== undefined) {
					this.byId("DocTypeIDFMAE").setSelectedKeys(this.FMAdnlDocType);
				}
				this.oView.byId("idFMForceMatchTab").setVisible(false);
				this.oView.byId("idFMAddtnlEntrsTab").setVisible(true);
				this.oView.byId("idFMAddtnlANX2").setVisible(true);
				this.oView.byId("idFMForceMfltrBar").setVisible(false);
				this.oView.byId("idFMAdnlEntrsfltrBar").setVisible(true);
				this.onFMAdnlEntrsGo();
			}
		},

		//on click of swipe Toggle button(RR Consolidated Match)
		ontogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("blacklayout2").setVisible(true);
				this.byId("blacklayout1").setVisible(false);

			} else {
				this.byId("blacklayout2").setVisible(false);
				this.byId("blacklayout1").setVisible(true);

			}
		},

		//on click of swipe Toggle button(RR Exact Match)
		onExactTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("ExactBlkLayout").setVisible(false);
				this.byId("ExactBlkLayout1").setVisible(true);
			} else {
				this.byId("ExactBlkLayout").setVisible(true);
				this.byId("ExactBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Match Upto Tolerence)
		onMatchUptoTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("MatchUptoBlkLayout").setVisible(false);
				this.byId("MatchUptoBlkLayout1").setVisible(true);
			} else {
				this.byId("MatchUptoBlkLayout").setVisible(true);
				this.byId("MatchUptoBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Document Type Mismatch)
		onDocTypeMisTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("DocTyMisBlkLayout").setVisible(false);
				this.byId("DocTyMisBlkLayout1").setVisible(true);
			} else {
				this.byId("DocTyMisBlkLayout").setVisible(true);
				this.byId("DocTyMisBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Document Date Mismatch)
		onDocDateMisTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("DocDateMisBlkLayout").setVisible(false);
				this.byId("DocDateMisBlkLayout1").setVisible(true);
			} else {
				this.byId("DocDateMisBlkLayout").setVisible(true);
				this.byId("DocDateMisBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Value Mismatch)
		onValueMisTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("ValMisBlkLayout").setVisible(false);
				this.byId("ValMisBlkLayout1").setVisible(true);
			} else {
				this.byId("ValMisBlkLayout").setVisible(true);
				this.byId("ValMisBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR POS Mismatch)
		onPOSMisTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("POSMisBlkLayout").setVisible(false);
				this.byId("POSMisBlkLayout1").setVisible(true);
			} else {
				this.byId("POSMisBlkLayout").setVisible(true);
				this.byId("POSMisBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Multi Mismatch)
		onMultiMisTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("MultiMisBlkLayout").setVisible(false);
				this.byId("MultiMisBlkLayout1").setVisible(true);
			} else {
				this.byId("MultiMisBlkLayout").setVisible(true);
				this.byId("MultiMisBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Fuzzy Match)
		onFuzzyMatchTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("FuzzyMatchBlkLayout").setVisible(false);
				this.byId("FuzzyMatchBlkLayout1").setVisible(true);
			} else {
				this.byId("FuzzyMatchBlkLayout").setVisible(true);
				this.byId("FuzzyMatchBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Force Match FM)
		onFuzzyMatchFMTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("FuzzyMatchFMBlkLayout").setVisible(false);
				this.byId("FuzzyMatchFMBlkLayout1").setVisible(true);
			} else {
				this.byId("FuzzyMatchFMBlkLayout").setVisible(true);
				this.byId("FuzzyMatchFMBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Force Match AE)
		onFuzzyMatchAETogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("FuzzyMatchAEBlkLayout").setVisible(false);
				this.byId("FuzzyMatchAEBlkLayout1").setVisible(true);
			} else {
				this.byId("FuzzyMatchAEBlkLayout").setVisible(true);
				this.byId("FuzzyMatchAEBlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Addition ANX-2)
		onAddA2TogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("AddA2BlkLayout").setVisible(false);
				this.byId("AddA2BlkLayout1").setVisible(true);
			} else {
				this.byId("AddA2BlkLayout").setVisible(true);
				this.byId("AddA2BlkLayout1").setVisible(false);
			}
		},

		//on click of swipe Toggle button(RR Addition PR)
		onAddPRTogglePress: function (oEvt) {
			if (oEvt.getSource().getPressed()) {
				this.byId("AddPRBlkLayout").setVisible(false);
				this.byId("AddPRBlkLayout1").setVisible(true);
			} else {
				this.byId("AddPRBlkLayout").setVisible(true);
				this.byId("AddPRBlkLayout1").setVisible(false);
			}
		},

		//----------------------------------------------------------------
		//on Selection of Entity List Gstins list will be dispalyed(RR Consolidated Match Tab)
		onSelctnChgRRConsol: function (oEvent) {
			if (this.consGStin !== undefined) {
				this.getView().byId("idRRConsGstins").setSelectedKeys(this.consGStin);
				var oMultiGstinInfo = {
					req: {
						"entityId": $.sap.entityID,
						"taxPeriod": this.getView().byId("dtConsld").getValue(),
						"gstins": this.getView().byId("idRRConsGstins").getSelectedKeys()
					}
				};
				this._rePortType(oMultiGstinInfo);

			} else {
				this.onSelcChgeGstin();
			}
		},

		handleChange: function () {
			this.consGStin = undefined;
			this.oExactMDate = undefined;
			this.exactKey = undefined;
			this.oMUTMDate = undefined;
			this.MUTKey = undefined;
			this.oDTypMDate = undefined;
			this.DTypKey = undefined;
			this.oDocDMDate = undefined;
			this.oValMDate = undefined;
			this.ValueMKey = undefined;
			this.oPOSMDate = undefined;
			this.POSMKey = undefined;
			this.oMultiMDate = undefined;
			this.MultiMKey = undefined;
			this.oFuzzyMDate = undefined;
			this.FuzyMKey = undefined;
			this.oForceMDate = undefined;
			this.FMForceMKey = undefined;
			this.oFMAddlnDate = undefined;
			this.oAddnlANX2Date = undefined;
			this.AdnlANX2Key = undefined;
			this.oAddnlPRDate = undefined;
			this.AdnlPRKey = undefined;
			this.ExactDocTyp = undefined;
			this.MUTDocType = undefined;
			this.DTMDocType = undefined;
			this.DDMDocType = undefined;
			this.ValDocType = undefined;
			this.POSDocType = undefined;
			this.MultiMDocType = undefined;
			this.FuzyDocType = undefined;
			this.FMMDocType = undefined;
			this.AdnlANX2DocType = undefined;
			this.AdnlPRDocType = undefined;
			this.oFMAddlnDate = undefined;
			this.FMAddnlGSTINKey = undefined;
			this.FMAdnlDocType = undefined;

		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Exact Match Tab)
		/*onSelctnChgRRMatch: function () {
			this.ExactGSTIN = [];
			this.getView().byId("idRRExctMatchGstins").setSelectedKeys([]);
			this.oExactMEnty = this.getView().byId("idRRMatchEnty").getSelectedKey();
			this._getDataSecurity(this.oExactMEnty, "idMatchfltrBar");
		},*/

		onExactDate: function (oEvt) {
			this.oExactMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Match Up To Tolerance Tab)
		/*onSelctnChgRRMUT: function () {
			this.oMatchUpToEnty = this.getView().byId("idRRMatchUTEnty").getSelectedKey();
			this._getDataSecurity(this.oMatchUpToEnty, "idMatchUptofltrBar");
			this.MUTGSTIN = [];
		},*/

		onMUTDate: function (oEvt) {
			this.oMUTMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Document Type MisMatch Tab)
		/*	onSelChgDTypMisMtch: function () {
				this.oDocTypMEnty = this.getView().byId("idRRDTMEnty").getSelectedKey();
				this._getDataSecurity(this.oDocTypMEnty, "idDocTyMisfltrBar");
				this.DTMGSTIN = [];
			},*/

		onDTypMDate: function (oEvt) {
			this.oDTypMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Document Date MisMatch Tab)
		/*onSelChgDDateMisMtch: function () {
			this.oDocDateMEnty = this.getView().byId("idRRDDMEnty").getSelectedKey();
			this._getDataSecurity(this.oDocDateMEnty, "idDocDateMisfltrBar");
			this.DDMGSTIN = [];
		},*/

		onDocDMDate: function (oEvt) {
			this.oDocDMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Value MisMatch Tab)
		/*onSelChgValMisMtch: function () {
			this.oValueMEnty = this.getView().byId("idRRValMEnty").getSelectedKey();
			this._getDataSecurity(this.oValueMEnty, "idValueMisMfltrBar");
			this.ValMGSTIN = [];
		},*/

		onValMDate: function (oEvt) {
			this.oValMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR POS MisMatch Tab)
		/*	onSelChgPOSMisMtch: function () {
				this.oPOSMEnty = this.getView().byId("idRRPOSMEnty").getSelectedKey();
				this._getDataSecurity(this.oPOSMEnty, "idPOSMisMfltrBar");
				this.POSMGSTIN = [];
			},*/

		onPOSMDate: function (oEvt) {
			this.oPOSMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR (DTTPC) Multi MisMatch Tab)
		/*onSelChgMulMisMtch: function () {
			this.oMultiMEnty = this.getView().byId("idRRMultiMEnty").getSelectedKey();
			this._getDataSecurity(this.oMultiMEnty, "idMultiMisMfltrBar");
			this.MultiMGSTIN = [];
		},*/

		onMultiMDate: function (oEvt) {
			this.oMultiMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Fuzzy Match Tab)
		/*	onSelChgFuzzyMtch: function () {
				this.oFuzzyMEnty = this.getView().byId("idRRFuzzyMEnty").getSelectedKey();
				this._getDataSecurity(this.oFuzzyMEnty, "idFuzzyMisMfltrBar");
				this.FuzyMGSTIN = [];
			},*/

		onFuzzyMDate: function (oEvt) {
			this.oFuzzyMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Force Match Tab - Force Matched)
		/*onSelChgFMForceMtch: function () {
			this.oFMForcMatchMEnty = this.getView().byId("idRRFMForceMEnty").getSelectedKey();
			this._getDataSecurity(this.oFMForcMatchMEnty, "idFMForceMfltrBar");
			this.FMForceGSTI = [];
		},*/

		onForceMDate: function (oEvt) {
			this.oForceMDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Force Match Tab - Additional Entries)
		/*onSelChgFMAdnlEntrs: function () {
			this.oFMAddnlEEnty = this.getView().byId("idFMAdnlEntrsEnty").getSelectedKey();
			this._getDataSecurity(this.oFMAddnlEEnty, "idFMAdnlEntrsfltrBar");
			this.FMAddnlGSTIN = [];
		},*/

		onFMAddlnDate: function (oEvt) {
			this.oFMAddlnDate = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Addition in ANX-2 Tab)
		/*onSelChgAddtnANX2: function () {
			this.oAddnlANX2MEnty = this.getView().byId("idRRAddtnANX2Enty").getSelectedKey();
			this._getDataSecurity(this.oAddnlANX2MEnty, "idAddtnANX2fltrBar");
			this.AddtnANX2GSTIN = [];
		},
*/
		onAddnlANX2Date: function (oEvt) {
			this.oAddnlANX2Date = oEvt.getSource()._getSelectedDate();
		},

		//on Selection of Entity List Gstins list will be dispalyed(RR Addition in PR Tab)
		/*onSelChgAddtnPR: function () {
			this.oAddnlPREnty = this.getView().byId("idRRAddtnPREnty").getSelectedKey();
			this._getDataSecurity(this.oAddnlPREnty, "idAddtnPRfltrBar");
			this.AddtnPRGSTIN = [];
		},*/

		onAddnlPRDate: function (oEvt) {
			this.oAddnlPRDate = oEvt.getSource()._getSelectedDate();
		},

		//----------------------------------------------------------------

		//on Selection of Entity List(RR Consolidated Match GSTIN)
		onSelcChgeGstin: function (oEvt) {
			if (oEvt !== undefined) {
				this.consGStin = undefined;
				this.oExactMDate = undefined;
				this.exactKey = undefined;
				this.oMUTMDate = undefined;
				this.MUTKey = undefined;
				this.oDTypMDate = undefined;
				this.DTypKey = undefined;
				this.oDocDMDate = undefined;
				this.oValMDate = undefined;
				this.ValueMKey = undefined;
				this.oPOSMDate = undefined;
				this.POSMKey = undefined;
				this.oMultiMDate = undefined;
				this.MultiMKey = undefined;
				this.oFuzzyMDate = undefined;
				this.FuzyMKey = undefined;
				this.oForceMDate = undefined;
				this.FMForceMKey = undefined;
				this.oFMAddlnDate = undefined;
				this.oAddnlANX2Date = undefined;
				this.AdnlANX2Key = undefined;
				this.oAddnlPRDate = undefined;
				this.AdnlPRKey = undefined;
				this.ExactDocTyp = undefined;
				this.MUTDocType = undefined;
				this.DTMDocType = undefined;
				this.DDMDocType = undefined;
				this.ValDocType = undefined;
				this.POSDocType = undefined;
				this.MultiMDocType = undefined;
				this.FuzyDocType = undefined;
				this.FMMDocType = undefined;
				this.AdnlANX2DocType = undefined;
				this.AdnlPRDocType = undefined;
				this.oFMAddlnDate = undefined;
				this.FMAddnlGSTINKey = undefined;
				this.FMAdnlDocType = undefined;
			}
			var oSelGstin = this.getView().byId("idRRConsGstins").getSelectedKeys();
			var finalRetTypeGSTN;
			if (oSelGstin.length !== 0) {
				finalRetTypeGSTN = this.getView().byId("idRRConsGstins").getSelectedKeys();
				this.consGStin = oSelGstin;
			} else {
				var oDataSeclctGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oDataSeclctGstinsArr = [];
				for (var i = 0; i < oDataSeclctGstins.length; i++) {
					oDataSeclctGstinsArr.push(oDataSeclctGstins[i].value);
					finalRetTypeGSTN = oDataSeclctGstinsArr;
				}
			}

			var oMultiGstinInfo = {
				req: {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("dtConsld").getValue(),
					"gstins": finalRetTypeGSTN //this.getView().byId("idRRConsGstins").getSelectedKeys()
				}
			};
			this._rePortType(oMultiGstinInfo);
		},

		_rePortType: function (oMultiGstinInfo) {
			var that = this;
			this.oConsldMdl = new JSONModel();
			var oConsldView = this.getView();
			var oConsldRTPath = "/aspsapapi/getReconResultReportType.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oConsldRTPath,
					contentType: "application/json",
					data: JSON.stringify(oMultiGstinInfo)
				}).done(function (data, status, jqXHR) {
					that.oConsldMdl.setData(data);
					oConsldView.setModel(that.oConsldMdl, "ReportTypeList");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//on Click of Go Button- Table Binding(RR Consolidated Match)
		onRRConsoldGO: function () {

			var oConSelGstn = this.getView().byId("idRRConsGstins").getSelectedKeys();
			var finalGSTN;
			if (oConSelGstn.length !== 0) {
				finalGSTN = this.getView().byId("idRRConsGstins").getSelectedKeys();
			} else {
				var oDataSecGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oDataSecGstinsArr = [];
				for (var i = 0; i < oDataSecGstins.length; i++) {
					oDataSecGstinsArr.push(oDataSecGstins[i].value);
					finalGSTN = oDataSecGstinsArr;
				}
			}

			//Selection of Single or Multiple Return Types From Filter ComboBox 
			var oReptType = this.byId("idConsldReptType").getSelectedItems().length;
			var oReptTypeArr = [];
			for (var j = 0; j < oReptType; j++) {
				oReptTypeArr.push(this.byId("idConsldReptType").getSelectedItems()[j].getKey());
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("dtConsld").getValue(),
					"gstin": finalGSTN, //this.getView().byId("idRRConsGstins").getSelectedKeys(),
					"tableType": this.getView().byId("idConsldTabType").getSelectedKeys(),
					"reportType": oReptTypeArr, //oRetTypes//this.getView().byId("idConsldReptType").getSelectedKeys()
					"docType": this.getView().byId("DocTypeIDConslM").getSelectedKeys()
				}
			};
			this.onRRConsldTable(oRRConsldGoInfo);
		},

		onRRConsldTable: function (oRRConsldGoInfo) {
			this.onSelcChgeGstin();
			this.byId("idConsldReptType").setSelectedKeys(this.ConslRetnRT); //Retain Report Type
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idverticalRes");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("idConsolidaTable").setModel(oRRConsldMdl, "ConsldTablData");
				}).fail(function (jqXHR, status, err) {});
			});

		},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on Exact Match Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onExactMatchGo: function () {
			this.byId("idRRExctMatchGstins").setSelectedKeys(this.ExactGSTIN);
			var oSelExMtchGstn = this.byId("idRRExctMatchGstins").getSelectedKeys();
			var oFinalExMGstn;
			if (oSelExMtchGstn.length !== 0) {
				oFinalExMGstn = this.byId("idRRExctMatchGstins").getSelectedKeys();
			} else {
				var oExactDSGStins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oExctDSGStinsArr = [];
				for (var j = 0; j < oExactDSGStins.length; j++) {
					oExctDSGStinsArr.push(oExactDSGStins[j].value);
					oFinalExMGstn = oExctDSGStinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("ExactDateId").getValue(),
					"gstin": oFinalExMGstn, //this.getView().byId("idRRExctMatchGstins").getSelectedKeys(),
					"tableType": this.getView().byId("ExactTabType").getSelectedKeys(),
					"reportType": ["Exact Match"],
					"docType": this.getView().byId("DocTypeIDExctM").getSelectedKeys()
				}
			};
			this.onExactMatch(oRRConsldGoInfo);
		},

		onExactMatch: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idverticalResExact");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("ExactMatchTabId").setModel(oRRConsldMdl, "ExactMatchTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on  Match Upto Tolerence Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onMatchUptoGo: function () {
			this.byId("idRRMatchUTGstins").setSelectedKeys(this.MUTGSTIN);
			var oSelMUTGstin = this.byId("idRRMatchUTGstins").getSelectedKeys();
			var oFinalMUTGstin;
			if (oSelMUTGstin.length !== 0) {
				oFinalMUTGstin = this.byId("idRRMatchUTGstins").getSelectedKeys();
			} else {
				var oMUTDSGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oMUTDSGstinsArr = [];
				for (var i = 0; i < oMUTDSGstins.length; i++) {
					oMUTDSGstinsArr.push(oMUTDSGstins[i].value);
					oFinalMUTGstin = oMUTDSGstinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("MUTDateId").getValue(),
					"gstin": oFinalMUTGstin, //oMUTDSGstinsArr, //this.getView().byId("idRRMatchUTGstins").getSelectedKeys(),
					"tableType": this.getView().byId("MUTTabType").getSelectedKeys(),
					"reportType": ["Match upto Tolerance"],
					"docType": this.getView().byId("DocTypeIDMUTM").getSelectedKeys()
				}
			};
			this.onMatchUT(oRRConsldGoInfo);
		},

		onMatchUT: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idverticalResMatchUpto");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("MatchUptoTolTabId").setModel(oRRConsldMdl, "MatchUptoTolTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on  Doc Type Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onDocTypeMisGo: function () {
			this.byId("idRRDTMGstins").setSelectedKeys(this.DTMGSTIN);
			var oSelDTypGstns = this.byId("idRRDTMGstins").getSelectedKeys();
			var oFinalDTDSGstns;
			if (oSelDTypGstns.length !== 0) {
				oFinalDTDSGstns = this.byId("idRRDTMGstins").getSelectedKeys();
			} else {
				var oDtaTypMDSGstns = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oDtaTypMDSGstnsArr = [];
				for (var i = 0; i < oDtaTypMDSGstns.length; i++) {
					oDtaTypMDSGstnsArr.push(oDtaTypMDSGstns[i].value);
					oFinalDTDSGstns = oDtaTypMDSGstnsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("DTMDateId").getValue(),
					"gstin": oFinalDTDSGstns, //oDtaTypMDSGstnsArr, //this.getView().byId("idRRDTMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("DTMTabType").getSelectedKeys(),
					"reportType": ["Document Type Mismatch"],
					"docType": this.getView().byId("DocTypeIDDTM").getSelectedKeys()
				}
			};
			this.onDocTypeMis(oRRConsldGoInfo);
		},

		onDocTypeMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idDocTyMisBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("DocTypeMisTabId").setModel(oRRConsldMdl, "DocTypeMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  Doc Date Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onDocDateMisGo: function () {
			this.byId("idRRDDMGstins").setSelectedKeys(this.DDMGSTIN);
			var oSelDDMGstin = this.byId("idRRDDMGstins").getSelectedKeys();
			var oFinalDDMDSGstin;
			if (oSelDDMGstin.length !== 0) {
				oFinalDDMDSGstin = this.byId("idRRDDMGstins").getSelectedKeys();
			} else {
				var oDDMDSGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oDDMDSGstinsArr = [];
				for (var i = 0; i < oDDMDSGstins.length; i++) {
					oDDMDSGstinsArr.push(oDDMDSGstins[i].value);
					oFinalDDMDSGstin = oDDMDSGstinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("DDMDateId").getValue(),
					"gstin": oFinalDDMDSGstin, //oDDMDSGstinsArr, //this.getView().byId("idRRDDMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("DDMTabType").getSelectedKeys(),
					"reportType": ["Document Date Mismatch"],
					"docType": this.getView().byId("DocTypeIDDDM").getSelectedKeys()
				}
			};
			this.onDocDateMis(oRRConsldGoInfo);
		},

		onDocDateMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idDocDateMisBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("DocDateMisTabId").setModel(oRRConsldMdl, "DocDateMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  Value Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onValueMisGo: function () {

			this.byId("idRRValMGstins").setSelectedKeys(this.ValMGSTIN);
			var oSelValDSGstin = this.byId("idRRValMGstins").getSelectedKeys();
			var oFinalValGstins;
			if (oSelValDSGstin.length !== 0) {
				oFinalValGstins = this.byId("idRRValMGstins").getSelectedKeys();
			} else {
				var oValDSGStins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oValDSGStinsArr = [];
				for (var i = 0; i < oValDSGStins.length; i++) {
					oValDSGStinsArr.push(oValDSGStins[i].value);
					oFinalValGstins = oValDSGStinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("ValMDateId").getValue(),
					"gstin": oFinalValGstins, //oValDSGStinsArr, //this.getView().byId("idRRValMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("ValMTabType").getSelectedKeys(),
					"reportType": ["Value Mismatch"],
					"docType": this.getView().byId("DocTypeIDValM").getSelectedKeys()
				}
			};
			this.onValueMis(oRRConsldGoInfo);
		},

		onValueMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idValueMisBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("ValueMisTabId").setModel(oRRConsldMdl, "ValueMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  POS Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onPOSMisGo: function () {

			this.byId("idRRPOSMGstins").setSelectedKeys(this.POSMGSTIN);
			var oSelPOSMGstins = this.byId("idRRPOSMGstins").getSelectedKeys();
			var oFinalPOSMGstins;
			if (oSelPOSMGstins.length !== 0) {
				oFinalPOSMGstins = this.byId("idRRPOSMGstins").getSelectedKeys();
			} else {
				var oPOSDSGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oPOSDSGstinsArr = [];
				for (var i = 0; i < oPOSDSGstins.length; i++) {
					oPOSDSGstinsArr.push(oPOSDSGstins[i].value);
					oFinalPOSMGstins = oPOSDSGstinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("POSMDateId").getValue(),
					"gstin": oFinalPOSMGstins, //this.getView().byId("idRRPOSMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("POSMTabType").getSelectedKeys(),
					"reportType": ["POS Mismatch"],
					"docType": this.getView().byId("DocTypeIDPOS").getSelectedKeys()
				}
			};
			this.onPOSMis(oRRConsldGoInfo);
		},

		onPOSMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idPOSMisBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("POSMisTabId").setModel(oRRConsldMdl, "POSMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  POS Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onMultiMisGo: function () {
			this.byId("idRRMultiMGstins").setSelectedKeys(this.MultiMGSTIN);
			var oSelMulMGstns = this.byId("idRRMultiMGstins").getSelectedKeys();
			var oFinalMultiMGstns;
			if (oSelMulMGstns.length !== 0) {
				oFinalMultiMGstns = this.byId("idRRMultiMGstins").getSelectedKeys();
			} else {
				var oMultiMDSGstns = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oMultiMDSGstnArr = [];
				for (var i = 0; i < oMultiMDSGstns.length; i++) {
					oMultiMDSGstnArr.push(oMultiMDSGstns[i].value);
					oFinalMultiMGstns = oMultiMDSGstnArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("MultiMDateId").getValue(),
					"gstin": oFinalMultiMGstns, //this.getView().byId("idRRMultiMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("MultiMTabType").getSelectedKeys(),
					"reportType": ["Multi-Mismatch"],
					"docType": this.getView().byId("DocTypeIDMultiM").getSelectedKeys()
				}
			};
			this.onMultiMis(oRRConsldGoInfo);
		},

		onMultiMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idMultiMisBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("MultiMisTabId").setModel(oRRConsldMdl, "MultiMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  Fuzzy Mismatch Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onFuzzyMisGo: function () {
			this.byId("idRRFuzzyMGstins").setSelectedKeys(this.FuzyMGSTIN);
			var oSelFuzyMGstins = this.byId("idRRFuzzyMGstins").getSelectedKeys();
			var oFinalFuzyMGstin;
			if (oSelFuzyMGstins.length !== 0) {
				oFinalFuzyMGstin = this.byId("idRRFuzzyMGstins").getSelectedKeys();
			} else {
				var oFuzyDSGstins = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oFuzyDSGstinsArr = [];
				for (var i = 0; i < oFuzyDSGstins.length; i++) {
					oFuzyDSGstinsArr.push(oFuzyDSGstins[i].value);
					oFinalFuzyMGstin = oFuzyDSGstinsArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("FuzzyMDateId").getValue(),
					"gstin": oFinalFuzyMGstin, //this.getView().byId("idRRFuzzyMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("FuzzyMTabType").getSelectedKeys(),
					"reportType": ["Fuzzy Match"],
					"docType": this.getView().byId("DocTypeIDFuzy").getSelectedKeys()
				}
			};
			this.onFuzzyMis(oRRConsldGoInfo);
		},

		onFuzzyMis: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idFuzzyMatchBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("FuzzyMisTabId").setModel(oRRConsldMdl, "FuzzyMisTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on   Adduition in ANX-2 Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onAddtnANX2Go: function () {
			this.byId("idRRAddtnANX2Gstins").setSelectedKeys(this.AddtnANX2GSTIN);
			var oSelAdanx2Gstin = this.byId("idRRAddtnANX2Gstins").getSelectedKeys();
			var oFinalAdanx2Gstin;
			if (oSelAdanx2Gstin.length !== 0) {
				oFinalAdanx2Gstin = this.byId("idRRAddtnANX2Gstins").getSelectedKeys();
			} else {
				var oAdanx2DSGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oAdanx2DSGstinArr = [];
				for (var i = 0; i < oAdanx2DSGstin.length; i++) {
					oAdanx2DSGstinArr.push(oAdanx2DSGstin[i].value);
					oFinalAdanx2Gstin = oAdanx2DSGstinArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("AddtnANX2DateId").getValue(),
					"gstin": oFinalAdanx2Gstin, //this.getView().byId("idRRAddtnANX2Gstins").getSelectedKeys(),
					"tableType": this.getView().byId("idAddtnANX2TabType").getSelectedKeys(),
					"reportType": ["Addition in ANX-2"],
					"docType": this.getView().byId("DocTypeIDANX2").getSelectedKeys()
				}
			};
			this.onAddtnInANX2(oRRConsldGoInfo);
		},

		onAddtnInANX2: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFunAddnlANX2(data.resp.det, "idAddA2Blk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("AddtnANX2TabId").setModel(oRRConsldMdl, "AddtnANX2TabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onAddtnInPRGo: function () {
			this.byId("idRRAddtnPRGstins").setSelectedKeys(this.AddtnPRGSTIN);
			var oSelAdPRGstin = this.byId("idRRAddtnPRGstins").getSelectedKeys();
			var oFinalPRGstin;
			if (oSelAdPRGstin.length !== 0) {
				oFinalPRGstin = this.byId("idRRAddtnPRGstins").getSelectedKeys();
			} else {
				var oAdPRDSGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oAdPRDSGstinArr = [];
				for (var i = 0; i < oAdPRDSGstin.length; i++) {
					oAdPRDSGstinArr.push(oAdPRDSGstin[i].value);
					oFinalPRGstin = oAdPRDSGstinArr;
				}
			}

			var oRRConsldGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("AddtnPRDateId").getValue(),
					"gstin": oFinalPRGstin, //this.getView().byId("idRRAddtnPRGstins").getSelectedKeys(),
					"tableType": this.getView().byId("idAddtnPRTabTyp").getSelectedKeys(),
					"reportType": ["Addition in PR"],
					"docType": this.getView().byId("DocTypeIDPR").getSelectedKeys()
				}
			};
			this.onAddtnInPR(oRRConsldGoInfo);
		},

		onAddtnInPR: function (oRRConsldGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRConsldGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idAddPRBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("AddtnPRTabId").setModel(oRRConsldMdl, "AddtnPRTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user click on  Force Match(Force Matched) Seagment Button 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onFMForceMatcdGo: function () {
			this.byId("idRRFMForceMGstins").setSelectedKeys(this.FMForceGSTIN);
			var oSelFMGstin = this.byId("idRRFMForceMGstins").getSelectedKeys();
			var oFinalFMGstin;
			if (oSelFMGstin.length !== 0) {
				oFinalFMGstin = this.byId("idRRFMForceMGstins").getSelectedKeys();
			} else {
				var oFMDSGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oFMDSGstinArr = [];
				for (var i = 0; i < oFMDSGstin.length; i++) {
					oFMDSGstinArr.push(oFMDSGstin[i].value);
					oFinalFMGstin = oFMDSGstinArr;
				}
			}

			var oRRForceMGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("FMForceMDateId").getValue(),
					"gstin": oFinalFMGstin, //this.getView().byId("idRRFMForceMGstins").getSelectedKeys(),
					"tableType": this.getView().byId("idFMForceMTabType").getSelectedKeys(),
					"reportType": ["Force Match"],
					"docType": this.getView().byId("DocTypeIDFMF").getSelectedKeys()
				}
			};
			this.onForceMatched(oRRForceMGoInfo);
		},

		onForceMatched: function (oRRForceMGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRForceMGoInfo)
				}).done(function (data, status, jqXHR) {
					that.CalculationFun(data.resp.det, "idForceMatchBlk");
					oRRConsldMdl.setData(data.resp.det);
					that.byId("idFMForceMTable").setModel(oRRConsldMdl, "FMForceMTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onFMAdnlEntrsGo: function () {
			this.byId("idFMAdnlEntrsGstins").setSelectedKeys(this.FMAddnlGSTIN);
			var oSelFMAdGstin = this.byId("idFMAdnlEntrsGstins").getSelectedKeys();
			var oFinalFMAdGstin;
			if (oSelFMAdGstin.length !== 0) {
				oFinalFMAdGstin = this.byId("idFMAdnlEntrsGstins").getSelectedKeys();
			} else {
				var oFMAdDSGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
				var oFMAdDSGstinArr = [];
				for (var i = 0; i < oFMAdDSGstin.length; i++) {
					oFMAdDSGstinArr.push(oFMAdDSGstin[i].value);
					oFinalFMAdGstin = oFMAdDSGstinArr;
				}
			}

			var oRRForceMGoInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("idFMAdnlEntrsDate").getValue(),
					"gstin": oFinalFMAdGstin, //this.getView().byId("idFMAdnlEntrsGstins").getSelectedKeys(),
					"tableType": this.getView().byId("idAdnlEntrsTabTyp").getSelectedKeys(),
					"reportType": ["Addition in PR"],
					"docType": this.getView().byId("DocTypeIDFMAE").getSelectedKeys()
				}
			};
			this.onFMAddtnlEntrs(oRRForceMGoInfo);

			var oRRForceMGoInfo1 = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.getView().byId("idFMAdnlEntrsDate").getValue(),
					"gstin": oFinalFMAdGstin, //this.getView().byId("idFMAdnlEntrsGstins").getSelectedKeys(),
					"tableType": this.getView().byId("idAdnlEntrsTabTyp").getSelectedKeys(),
					"reportType": ["Addition in ANX-2"],
					"docType": this.getView().byId("DocTypeIDFMAE").getSelectedKeys()
				}
			};
			this.onFMAddtnlEntrs1(oRRForceMGoInfo1);
		},

		onFMAddtnlEntrs: function (oRRForceMGoInfo) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRForceMGoInfo)
				}).done(function (data, status, jqXHR) {
					that.PRData = data.resp.det;
					that.CalculationFunForceMatch(data.resp.det, that.A2Data, "idForceMatchAEBlk");
					for (var i = 0; i < data.resp.det.length; i++) {
						data.resp.det[i].sno = i + 1;
					}
					oRRConsldMdl.setData(data.resp.det);
					that.byId("idFMAdtnlEntrsHBox").setModel(oRRConsldMdl, "FMAdtnlEntrsTablData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onFMAddtnlEntrs1: function (oRRForceMGoInfo1) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getDocumentSummReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(oRRForceMGoInfo1)
				}).done(function (data, status, jqXHR) {
					that.A2Data = data.resp.det;
					that.CalculationFunForceMatch(that.PRData, data.resp.det, "idForceMatchAEBlk");
					for (var i = 0; i < data.resp.det.length; i++) {
						data.resp.det[i].sno = i + 1;
					}
					oRRConsldMdl.setData(data.resp.det);
					that.byId("idFMAdtnlEntrsHBox").setModel(oRRConsldMdl, "FMAdtnlEntrsTablData1");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPRTab: function (oEvt) {
			this.oForceMModelArry = [];
			var oTabledata = this.getView().byId("idFMAdtnlEntrsTabl").getModel("FMAdtnlEntrsTablData").getData();
			var path = oEvt.getSource().getSelectedIndex();
			if (path >= 0) {
				this.oForceMModelArry.push(oTabledata[path]);
				this.CalculationFunForceMatch(this.oForceMModelArry, this.oForceMModelArry1, "idForceMatchAEBlk");
			} else {
				this.oForceMModelArry = [];
				this.oForceMModelArry = undefined;
				this.CalculationFunForceMatch(this.PRData, this.A2Data, "idForceMatchAEBlk");
			}
			if (this.oForceMModelArry1 !== undefined && this.oForceMModelArry !== undefined) {
				if (!this._FMoDialog) {
					this._FMoDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.anx2.RRForceMatch", this);
				}
				var oReqWiseModel1 = new JSONModel();
				oReqWiseModel1.setData(this.oForceMModelArry);
				sap.ui.getCore().byId("idTab1AdnlEnt").setModel(oReqWiseModel1, "oForceMModelArry");
				var oReqWiseModel = new JSONModel();
				oReqWiseModel.setData(this.oForceMModelArry1);
				sap.ui.getCore().byId("idTab2AdnlEnt").setModel(oReqWiseModel, "oForceMModelArry1");
				this._FMoDialog.open();
			}

		},

		onANX2Tab: function (oEvt) {
			this.oForceMModelArry1 = [];
			var oTabledata = this.getView().byId("idFMAddtnlANX2").getModel("FMAdtnlEntrsTablData1").getData();
			var path = oEvt.getSource().getSelectedIndex();
			if (path >= 0) {
				this.oForceMModelArry1.push(oTabledata[path]);
				this.CalculationFunForceMatch(this.oForceMModelArry, this.oForceMModelArry1, "idForceMatchAEBlk");
			} else {
				this.oForceMModelArry1 = [];
				this.oForceMModelArry1 = undefined;
				this.CalculationFunForceMatch(this.PRData, this.A2Data, "idForceMatchAEBlk");
			}
			if (this.oForceMModelArry !== undefined && this.oForceMModelArry1 !== undefined) {
				if (!this._FMoDialog) {
					this._FMoDialog = sap.ui.xmlfragment("com.ey.digigst.fragments.anx2.RRForceMatch", this);
				}
				var oReqWiseModel1 = new JSONModel();
				oReqWiseModel1.setData(this.oForceMModelArry);
				sap.ui.getCore().byId("idTab1AdnlEnt").setModel(oReqWiseModel1, "oForceMModelArry");
				var oReqWiseModel = new JSONModel();
				oReqWiseModel.setData(this.oForceMModelArry1);
				sap.ui.getCore().byId("idTab2AdnlEnt").setModel(oReqWiseModel, "oForceMModelArry1");
				this._FMoDialog.open();
			}
		},

		onCloseDialogFM: function () {
			this._FMoDialog.close();
			sap.ui.getCore().byId("hboxID").setVisible(false);
		},

		onforcematRecPress: function () {
			this._FMoDialog.open();
		},

		onFragForceMatch: function () {
			var that = this;
			sap.ui.getCore().byId("hboxID").setVisible(true);
			var forcematchData = {
				"req": {
					"linkIdList": [{
						"prReconLinkId": that.oForceMModelArry[0].reconLinkId,
						"a2ReconLinkId": that.oForceMModelArry1[0].reconLinkId
					}]
				}

				/*"req": {
					"reportType": [{
						"reportName": "",
						"taxPeriod": this.getView().byId("idFMAdnlEntrsDate").getValue(),
						"commonDetails": [{
							"userAction": "",
							"details": [{
								"reconLinkId": 1,
								"prkey": that.oForceMModelArry[0].prKey,
								"a2key": that.oForceMModelArry1[0].a2Key,
								"gstin": ""
							}]
						}]
					}]
				}*/
			};
			this.onforcematchData(forcematchData);
		},

		onforcematchData: function (forcematchData) {
			var that = this;
			var oRRConsldPath = "/aspsapapi/updateReportType.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(forcematchData)
				}).done(function (data, status, jqXHR) {
					that.byId("idFMAdtnlEntrsTabl").setSelectedIndex(-1);
					that.byId("idFMAddtnlANX2").setSelectedIndex(-1);
					that.onFMAdnlEntrsGo();
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressFragForceMatch: function () {},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on Icon Tab Bar 
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		CalculationFun: function (Data, id) {
			var TotalAmounts = [];
			var
				retImpactA10Igst = 0,
				retImpactA10Cgst = 0,
				retImpactA10Sgst = 0,
				retImpactA10Cess = 0,
				retImpactA11Igst = 0,
				retImpactA11Cgst = 0,
				retImpactA11Sgst = 0,
				retImpactA11Cess = 0,
				retImpactB2Igst = 0,
				retImpactB2Cgst = 0,
				retImpactB2Sgst = 0,
				retImpactB2Cess = 0,
				retImpactB3Igst = 0,
				retImpactB3Cgst = 0,
				retImpactB3Sgst = 0,
				retImpactB3Cess = 0,

				anx2Invigst = 0,
				anx2DRigst = 0,
				anx2CRigst = 0,
				anx2Invcgst = 0,
				anx2DRcgst = 0,
				anx2CRcgst = 0,
				anx2Invsgst = 0,
				anx2DRsgst = 0,
				anx2CRsgst = 0,
				anx2Invcess = 0,
				anx2DRcess = 0,
				anx2CRcess = 0,

				prInvigst = 0,
				prDRigst = 0,
				prCRigst = 0,
				prInvcgst = 0,
				prDRcgst = 0,
				prCRcgst = 0,
				prInvsgst = 0,
				prDRsgst = 0,
				prCRsgst = 0,
				prInvcess = 0,
				prDRcess = 0,
				prCRcess = 0,

				prInvAvlIgst = 0,
				prDRAvlIgst = 0,
				prCRAvlIgst = 0,
				prInvAvlCgst = 0,
				prDRAvlCgst = 0,
				prCRAvlCgst = 0,
				prInvAvlSgst = 0,
				prDRAvlSgst = 0,
				prCRAvlSgst = 0,
				prInvAvlCess = 0,
				prDRAvlCess = 0,
				prCRAvlCess = 0;

			for (var i = 0; i < Data.length; i++) {
				// A2 Tax // PR Tax & // PR Avail
				if (Data[i].prdocType !== " ") {
					if (Data[i].prdocType === "INV") {
						anx2Invigst += Data[i].anx2igst;
						anx2Invcgst += Data[i].anx2cgst;
						anx2Invsgst += Data[i].anx2sgst;
						anx2Invcess += Data[i].anx2cess;
						prInvigst += Data[i].prigst;
						prInvcgst += Data[i].prcgst;
						prInvsgst += Data[i].prsgst;
						prInvcess += Data[i].prcess;
						prInvAvlIgst += Data[i].prAvailableIgst;
						prInvAvlCgst += Data[i].prAvailableCgst;
						prInvAvlSgst += Data[i].prAvailableSgst;
						prInvAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].prdocType === "DR") {
						anx2DRigst += Data[i].anx2igst;
						anx2DRcgst += Data[i].anx2cgst;
						anx2DRsgst += Data[i].anx2sgst;
						anx2DRcess += Data[i].anx2cess;
						prDRigst += Data[i].prigst;
						prDRcgst += Data[i].prcgst;
						prDRsgst += Data[i].prsgst;
						prDRcess += Data[i].prcess;
						prDRAvlIgst += Data[i].prAvailableIgst;
						prDRAvlCgst += Data[i].prAvailableCgst;
						prDRAvlSgst += Data[i].prAvailableSgst;
						prDRAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].prdocType === "CR") {
						anx2CRigst += Data[i].anx2igst;
						anx2CRcgst += Data[i].anx2cgst;
						anx2CRsgst += Data[i].anx2sgst;
						anx2CRcess += Data[i].anx2cess;
						prCRigst += Data[i].prigst;
						prCRcgst += Data[i].prcgst;
						prCRsgst += Data[i].prsgst;
						prCRcess += Data[i].prcess;
						prCRAvlIgst += Data[i].prAvailableIgst;
						prCRAvlCgst += Data[i].prAvailableCgst;
						prCRAvlSgst += Data[i].prAvailableSgst;
						prCRAvlCess += Data[i].prAvailableCess;
					}
				} else {
					if (Data[i].anx2docType === "INV") {
						anx2Invigst += Data[i].anx2igst;
						anx2Invcgst += Data[i].anx2cgst;
						anx2Invsgst += Data[i].anx2sgst;
						anx2Invcess += Data[i].anx2cess;
						prInvigst += Data[i].prigst;
						prInvcgst += Data[i].prcgst;
						prInvsgst += Data[i].prsgst;
						prInvcess += Data[i].prcess;
						prInvAvlIgst += Data[i].prAvailableIgst;
						prInvAvlCgst += Data[i].prAvailableCgst;
						prInvAvlSgst += Data[i].prAvailableSgst;
						prInvAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].anx2docType === "DR") {
						anx2DRigst += Data[i].anx2igst;
						anx2DRcgst += Data[i].anx2cgst;
						anx2DRsgst += Data[i].anx2sgst;
						anx2DRcess += Data[i].anx2cess;
						prDRigst += Data[i].prigst;
						prDRcgst += Data[i].prcgst;
						prDRsgst += Data[i].prsgst;
						prDRcess += Data[i].prcess;
						prDRAvlIgst += Data[i].prAvailableIgst;
						prDRAvlCgst += Data[i].prAvailableCgst;
						prDRAvlSgst += Data[i].prAvailableSgst;
						prDRAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].anx2docType === "CR") {
						anx2CRigst += Data[i].anx2igst;
						anx2CRcgst += Data[i].anx2cgst;
						anx2CRsgst += Data[i].anx2sgst;
						anx2CRcess += Data[i].anx2cess;
						prCRigst += Data[i].prigst;
						prCRcgst += Data[i].prcgst;
						prCRsgst += Data[i].prsgst;
						prCRcess += Data[i].prcess;
						prCRAvlIgst += Data[i].prAvailableIgst;
						prCRAvlCgst += Data[i].prAvailableCgst;
						prCRAvlSgst += Data[i].prAvailableSgst;
						prCRAvlCess += Data[i].prAvailableCess;
					}
				}
				retImpactA10Igst += Data[i].retImpactA10Igst;
				retImpactA10Cgst += Data[i].retImpactA10Cgst;
				retImpactA10Sgst += Data[i].retImpactA10Sgst;
				retImpactA10Cess += Data[i].retImpactA10Cess;
				retImpactA11Igst += Data[i].retImpactA11Igst;
				retImpactA11Cgst += Data[i].retImpactA11Cgst;
				retImpactA11Sgst += Data[i].retImpactA11Sgst;
				retImpactA11Cess += Data[i].retImpactA11Cess;
				retImpactB2Igst += Data[i].retImpactB2Igst;
				retImpactB2Cgst += Data[i].retImpactB2Cgst;
				retImpactB2Sgst += Data[i].retImpactB2Sgst;
				retImpactB2Cess += Data[i].retImpactB2Cess;
				retImpactB3Igst += Data[i].retImpactB3Igst;
				retImpactB3Cgst += Data[i].retImpactB3Cgst;
				retImpactB3Sgst += Data[i].retImpactB3Sgst;
				retImpactB3Cess += Data[i].retImpactB3Cess;
			}
			//////////////////A2 Tax Total////////////////////
			var A2Igst = Number(anx2Invigst) + Number(anx2DRigst) - Number(anx2CRigst);
			var A2Cgst = Number(anx2Invcgst) + Number(anx2DRcgst) - Number(anx2CRcgst);
			var A2Sgst = Number(anx2Invsgst) + Number(anx2DRsgst) - Number(anx2CRsgst);
			var A2Cess = Number(anx2Invcess) + Number(anx2DRcess) - Number(anx2CRcess);
			var A2TaxTotal = Number(A2Igst) + Number(A2Cgst) + Number(A2Sgst) + Number(A2Cess);

			/////////////////PR Tax Total/////////////////////
			var PRIgst = Number(prInvigst) + Number(prDRigst) - Number(prCRigst);
			var PRCgst = Number(prInvcgst) + Number(prDRcgst) - Number(prCRcgst);
			var PRSgst = Number(prInvsgst) + Number(prDRsgst) - Number(prCRsgst);
			var PRCess = Number(prInvcess) + Number(prDRcess) - Number(prCRcess);
			var PRTaxTotal = Number(PRIgst) + Number(PRCgst) + Number(PRSgst) + Number(PRCess);

			////////////////PR Avail Total////////////////////
			var PRAvlTxIgst = Number(prInvAvlIgst) + Number(prDRAvlIgst) - Number(prCRAvlIgst);
			var PRAvlTxCgst = Number(prInvAvlCgst) + Number(prDRAvlCgst) - Number(prCRAvlCgst);
			var PRAvlTxSgst = Number(prInvAvlSgst) + Number(prDRAvlSgst) - Number(prCRAvlSgst);
			var PRAvlTxCess = Number(prInvAvlCess) + Number(prDRAvlCess) - Number(prCRAvlCess);
			var PRAvailTaxTotal = Number(PRAvlTxIgst) + Number(PRAvlTxCgst) + Number(PRAvlTxSgst) + Number(PRAvlTxCess);

			///////////////RET-1 A10 Tax Total////////////////
			var RA10TaxTotal = Number(retImpactA10Igst) + Number(retImpactA10Cgst) + Number(retImpactA10Sgst) + Number(retImpactA10Cess);

			///////////////RET-1 A11 Tax Total////////////////
			var RA11TaxTotal = Number(retImpactA11Igst) + Number(retImpactA11Cgst) + Number(retImpactA11Sgst) + Number(retImpactA11Cess);

			///////////////RET-1 B2 Tax Total////////////////
			var RB2TaxTotal = Number(retImpactB2Igst) + Number(retImpactB2Cgst) + Number(retImpactB2Sgst) + Number(retImpactB2Cess);

			///////////////RET-1 B3 Tax Total////////////////
			var RB3TaxTotal = Number(retImpactB3Igst) + Number(retImpactB3Cgst) + Number(retImpactB3Sgst) + Number(retImpactB3Cess);

			//A2 Tax - PR Tax//
			var A2TPRTIgst = Number(A2Igst) - Number(PRIgst);
			var A2TPRTCgst = Number(A2Cgst) - Number(PRCgst);
			var A2TPRTSgst = Number(A2Sgst) - Number(PRSgst);
			var A2TPRTCess = Number(A2Cess) - Number(PRCess);

			///PR Tax - PR Aval ////////////////
			var PRTPRAIgst = Number(PRIgst) - Number(PRAvlTxIgst);
			var PRTPRACgst = Number(PRCgst) - Number(PRAvlTxCgst);
			var PRTPRASgst = Number(PRSgst) - Number(PRAvlTxSgst);
			var PRTPRACess = Number(PRCess) - Number(PRAvlTxCess);

			////A2 Tax - PR Aval/////////////////////
			var A2TPRAIgst = Number(A2Igst) - Number(PRAvlTxIgst);
			var A2TPRACgst = Number(A2Cgst) - Number(PRAvlTxCgst);
			var A2TPRASgst = Number(A2Sgst) - Number(PRAvlTxSgst);
			var A2TPRACess = Number(A2Cess) - Number(PRAvlTxCess);

			///////////////////A2 Tax - PR Tax Total////////////////
			var A2TPRTTaxTotal = Number(A2TPRTIgst) + Number(A2TPRTCgst) + Number(A2TPRTSgst) + Number(A2TPRTCess);

			//////////////////PR Tax - PR Aval Tax Total////////////
			var PRTPRATaxTotal = Number(PRTPRAIgst) + Number(PRTPRACgst) + Number(PRTPRASgst) + Number(PRTPRACess);

			//////////////////A2 Tax - PR Aval Tax Total/////////////
			var A2TPRATaxTotal = Number(A2TPRAIgst) + Number(A2TPRACgst) + Number(A2TPRASgst) + Number(A2TPRACess);

			var obj = {
				"anx2igst": A2Igst.toFixed(2), //anx2igst.toFixed(2),
				"anx2cgst": A2Cgst.toFixed(2), //anx2cgst.toFixed(2),
				"anx2sgst": A2Sgst.toFixed(2), //anx2sgst.toFixed(2),
				"anx2cess": A2Cess.toFixed(2), //anx2cess.toFixed(2),
				"prigst": PRIgst.toFixed(2),
				"prcgst": PRCgst.toFixed(2),
				"prsgst": PRSgst.toFixed(2),
				"prcess": PRCess.toFixed(2),
				"prAvailableIgst": PRAvlTxIgst.toFixed(2), //prAvailableIgst.toFixed(2),
				"prAvailableCgst": PRAvlTxCgst.toFixed(2), //prAvailableCgst.toFixed(2),
				"prAvailableSgst": PRAvlTxSgst.toFixed(2), //prAvailableSgst.toFixed(2),
				"prAvailableCess": PRAvlTxCess.toFixed(2), //prAvailableCess.toFixed(2),
				"retImpactA10Igst": retImpactA10Igst.toFixed(2),
				"retImpactA10Cgst": retImpactA10Cgst.toFixed(2),
				"retImpactA10Sgst": retImpactA10Sgst.toFixed(2),
				"retImpactA10Cess": retImpactA10Cess.toFixed(2),
				"retImpactA11Igst": retImpactA11Igst.toFixed(2),
				"retImpactA11Cgst": retImpactA11Cgst.toFixed(2),
				"retImpactA11Sgst": retImpactA11Sgst.toFixed(2),
				"retImpactA11Cess": retImpactA11Cess.toFixed(2),
				"retImpactB2Igst": retImpactB2Igst.toFixed(2),
				"retImpactB2Cgst": retImpactB2Cgst.toFixed(2),
				"retImpactB2Sgst": retImpactB2Sgst.toFixed(2),
				"retImpactB2Cess": retImpactB2Cess.toFixed(2),
				"retImpactB3Igst": retImpactB3Igst.toFixed(2),
				"retImpactB3Cgst": retImpactB3Cgst.toFixed(2),
				"retImpactB3Sgst": retImpactB3Sgst.toFixed(2),
				"retImpactB3Cess": retImpactB3Cess.toFixed(2),
				/////////A2 Tax - PR Tax////////
				"A2TPRTIgst": A2TPRTIgst.toFixed(2),
				"A2TPRTCgst": A2TPRTCgst.toFixed(2),
				"A2TPRTSgst": A2TPRTSgst.toFixed(2),
				"A2TPRTCess": A2TPRTCess.toFixed(2),
				////////PR Tax - PR Aval ///////
				"PRTPRAIgst": PRTPRAIgst.toFixed(2),
				"PRTPRACgst": PRTPRACgst.toFixed(2),
				"PRTPRASgst": PRTPRASgst.toFixed(2),
				"PRTPRACess": PRTPRACess.toFixed(2),
				////////A2 Tax - PR Aval////////
				"A2TPRAIgst": A2TPRAIgst.toFixed(2),
				"A2TPRACgst": A2TPRACgst.toFixed(2),
				"A2TPRASgst": A2TPRASgst.toFixed(2),
				"A2TPRACess": A2TPRACess.toFixed(2),

				/////////////////Totals////////
				"A2TaxTotal": A2TaxTotal.toFixed(2),
				"PRTaxTotal": PRTaxTotal.toFixed(2),
				"PRAvailTaxTotal": PRAvailTaxTotal.toFixed(2),
				"RA10TaxTotal": RA10TaxTotal.toFixed(2),
				"RA11TaxTotal": RA11TaxTotal.toFixed(2),
				"RB2TaxTotal": RB2TaxTotal.toFixed(2),
				"RB3TaxTotal": RB3TaxTotal.toFixed(2),
				"A2TPRTTaxTotal": A2TPRTTaxTotal.toFixed(2),
				"PRTPRATaxTotal": PRTPRATaxTotal.toFixed(2),
				"A2TPRATaxTotal": A2TPRATaxTotal.toFixed(2)
			};
			TotalAmounts.push(obj);
			var oRRConsldMdl = new JSONModel();
			oRRConsldMdl.setData(TotalAmounts[0]);
			this.getView().byId(id).setModel(oRRConsldMdl, "TotalAmounts");
		},
		CalculationFunForceMatch: function (Data, Data1, id) {
			var TotalAmounts = [];
			var
				retImpactA10Igst = 0,
				retImpactA10Cgst = 0,
				retImpactA10Sgst = 0,
				retImpactA10Cess = 0,
				retImpactA11Igst = 0,
				retImpactA11Cgst = 0,
				retImpactA11Sgst = 0,
				retImpactA11Cess = 0,
				retImpactB2Igst = 0,
				retImpactB2Cgst = 0,
				retImpactB2Sgst = 0,
				retImpactB2Cess = 0,
				retImpactB3Igst = 0,
				retImpactB3Cgst = 0,
				retImpactB3Sgst = 0,
				retImpactB3Cess = 0,

				anx2Invigst = 0,
				anx2DRigst = 0,
				anx2CRigst = 0,
				anx2Invcgst = 0,
				anx2DRcgst = 0,
				anx2CRcgst = 0,
				anx2Invsgst = 0,
				anx2DRsgst = 0,
				anx2CRsgst = 0,
				anx2Invcess = 0,
				anx2DRcess = 0,
				anx2CRcess = 0,

				prInvigst = 0,
				prDRigst = 0,
				prCRigst = 0,
				prInvcgst = 0,
				prDRcgst = 0,
				prCRcgst = 0,
				prInvsgst = 0,
				prDRsgst = 0,
				prCRsgst = 0,
				prInvcess = 0,
				prDRcess = 0,
				prCRcess = 0,

				prInvAvlIgst = 0,
				prDRAvlIgst = 0,
				prCRAvlIgst = 0,
				prInvAvlCgst = 0,
				prDRAvlCgst = 0,
				prCRAvlCgst = 0,
				prInvAvlSgst = 0,
				prDRAvlSgst = 0,
				prCRAvlSgst = 0,
				prInvAvlCess = 0,
				prDRAvlCess = 0,
				prCRAvlCess = 0;
			//////PR Data//////////
			if (Data !== undefined) {
				for (var i = 0; i < Data.length; i++) {
					if (Data[i].prdocType === "INV") {
						prInvigst += Data[i].prigst;
						prInvcgst += Data[i].prcgst;
						prInvsgst += Data[i].prsgst;
						prInvcess += Data[i].prcess;
						prInvAvlIgst += Data[i].prAvailableIgst;
						prInvAvlCgst += Data[i].prAvailableCgst;
						prInvAvlSgst += Data[i].prAvailableSgst;
						prInvAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].prdocType === "DR") {
						prDRigst += Data[i].prigst;
						prDRcgst += Data[i].prcgst;
						prDRsgst += Data[i].prsgst;
						prDRcess += Data[i].prcess;
						prDRAvlIgst += Data[i].prAvailableIgst;
						prDRAvlCgst += Data[i].prAvailableCgst;
						prDRAvlSgst += Data[i].prAvailableSgst;
						prDRAvlCess += Data[i].prAvailableCess;
					} else if (Data[i].prdocType === "CR") {
						prCRigst += Data[i].prigst;
						prCRcgst += Data[i].prcgst;
						prCRsgst += Data[i].prsgst;
						prCRcess += Data[i].prcess;
						prCRAvlIgst += Data[i].prAvailableIgst;
						prCRAvlCgst += Data[i].prAvailableCgst;
						prCRAvlSgst += Data[i].prAvailableSgst;
						prCRAvlCess += Data[i].prAvailableCess;
					}

					retImpactA10Igst += Data[i].retImpactA10Igst;
					retImpactA10Cgst += Data[i].retImpactA10Cgst;
					retImpactA10Sgst += Data[i].retImpactA10Sgst;
					retImpactA10Cess += Data[i].retImpactA10Cess;
					retImpactA11Igst += Data[i].retImpactA11Igst;
					retImpactA11Cgst += Data[i].retImpactA11Cgst;
					retImpactA11Sgst += Data[i].retImpactA11Sgst;
					retImpactA11Cess += Data[i].retImpactA11Cess;
					retImpactB2Igst += Data[i].retImpactB2Igst;
					retImpactB2Cgst += Data[i].retImpactB2Cgst;
					retImpactB2Sgst += Data[i].retImpactB2Sgst;
					retImpactB2Cess += Data[i].retImpactB2Cess;
					retImpactB3Igst += Data[i].retImpactB3Igst;
					retImpactB3Cgst += Data[i].retImpactB3Cgst;
					retImpactB3Sgst += Data[i].retImpactB3Sgst;
					retImpactB3Cess += Data[i].retImpactB3Cess;
				}
			}
			////A2 Data///////////
			if (Data1 !== undefined) {
				for (var j = 0; j < Data1.length; j++) {
					// A2 Tax // PR Tax & // PR Avail
					if (Data1[j].anx2docType === "INV") {
						anx2Invigst += Data1[j].anx2igst;
						anx2Invcgst += Data1[j].anx2cgst;
						anx2Invsgst += Data1[j].anx2sgst;
						anx2Invcess += Data1[j].anx2cess;
					} else if (Data1[j].anx2docType === "DR") {
						anx2DRigst += Data1[j].anx2igst;
						anx2DRcgst += Data1[j].anx2cgst;
						anx2DRsgst += Data1[j].anx2sgst;
						anx2DRcess += Data1[j].anx2cess;
					} else if (Data1[j].anx2docType === "CR") {
						anx2CRigst += Data1[j].anx2igst;
						anx2CRcgst += Data1[j].anx2cgst;
						anx2CRsgst += Data1[j].anx2sgst;
						anx2CRcess += Data1[j].anx2cess;
					}
				}
			}

			if (Data !== undefined && Data1 !== undefined) {
				//////////////////A2 Tax Total////////////////////
				var A2Igst = Number(anx2Invigst) + Number(anx2DRigst) - Number(anx2CRigst);
				var A2Cgst = Number(anx2Invcgst) + Number(anx2DRcgst) - Number(anx2CRcgst);
				var A2Sgst = Number(anx2Invsgst) + Number(anx2DRsgst) - Number(anx2CRsgst);
				var A2Cess = Number(anx2Invcess) + Number(anx2DRcess) - Number(anx2CRcess);
				var A2TaxTotal = Number(A2Igst) + Number(A2Cgst) + Number(A2Sgst) + Number(A2Cess);

				/////////////////PR Tax Total/////////////////////
				var PRIgst = Number(prInvigst) + Number(prDRigst) - Number(prCRigst);
				var PRCgst = Number(prInvcgst) + Number(prDRcgst) - Number(prCRcgst);
				var PRSgst = Number(prInvsgst) + Number(prDRsgst) - Number(prCRsgst);
				var PRCess = Number(prInvcess) + Number(prDRcess) - Number(prCRcess);
				var PRTaxTotal = Number(PRIgst) + Number(PRCgst) + Number(PRSgst) + Number(PRCess);

				////////////////PR Avail Total////////////////////
				var PRAvlTxIgst = Number(prInvAvlIgst) + Number(prDRAvlIgst) - Number(prCRAvlIgst);
				var PRAvlTxCgst = Number(prInvAvlCgst) + Number(prDRAvlCgst) - Number(prCRAvlCgst);
				var PRAvlTxSgst = Number(prInvAvlSgst) + Number(prDRAvlSgst) - Number(prCRAvlSgst);
				var PRAvlTxCess = Number(prInvAvlCess) + Number(prDRAvlCess) - Number(prCRAvlCess);
				var PRAvailTaxTotal = Number(PRAvlTxIgst) + Number(PRAvlTxCgst) + Number(PRAvlTxSgst) + Number(PRAvlTxCess);

				///////////////RET-1 A10 Tax Total////////////////
				var RA10TaxTotal = Number(retImpactA10Igst) + Number(retImpactA10Cgst) + Number(retImpactA10Sgst) + Number(retImpactA10Cess);

				///////////////RET-1 A11 Tax Total////////////////
				var RA11TaxTotal = Number(retImpactA11Igst) + Number(retImpactA11Cgst) + Number(retImpactA11Sgst) + Number(retImpactA11Cess);

				///////////////RET-1 B2 Tax Total////////////////
				var RB2TaxTotal = Number(retImpactB2Igst) + Number(retImpactB2Cgst) + Number(retImpactB2Sgst) + Number(retImpactB2Cess);

				///////////////RET-1 B3 Tax Total////////////////
				var RB3TaxTotal = Number(retImpactB3Igst) + Number(retImpactB3Cgst) + Number(retImpactB3Sgst) + Number(retImpactB3Cess);

				//A2 Tax - PR Tax//
				var A2TPRTIgst = Number(A2Igst) - Number(PRIgst);
				var A2TPRTCgst = Number(A2Cgst) - Number(PRCgst);
				var A2TPRTSgst = Number(A2Sgst) - Number(PRSgst);
				var A2TPRTCess = Number(A2Cess) - Number(PRCess);

				///PR Tax - PR Aval ////////////////
				var PRTPRAIgst = Number(PRIgst) - Number(PRAvlTxIgst);
				var PRTPRACgst = Number(PRCgst) - Number(PRAvlTxCgst);
				var PRTPRASgst = Number(PRSgst) - Number(PRAvlTxSgst);
				var PRTPRACess = Number(PRCess) - Number(PRAvlTxCess);

				////A2 Tax - PR Aval/////////////////////
				var A2TPRAIgst = Number(A2Igst) - Number(PRAvlTxIgst);
				var A2TPRACgst = Number(A2Cgst) - Number(PRAvlTxCgst);
				var A2TPRASgst = Number(A2Sgst) - Number(PRAvlTxSgst);
				var A2TPRACess = Number(A2Cess) - Number(PRAvlTxCess);

				///////////////////A2 Tax - PR Tax Total////////////////
				var A2TPRTTaxTotal = Number(A2TPRTIgst) + Number(A2TPRTCgst) + Number(A2TPRTSgst) + Number(A2TPRTCess);

				//////////////////PR Tax - PR Aval Tax Total////////////
				var PRTPRATaxTotal = Number(PRTPRAIgst) + Number(PRTPRACgst) + Number(PRTPRASgst) + Number(PRTPRACess);

				//////////////////A2 Tax - PR Aval Tax Total/////////////
				var A2TPRATaxTotal = Number(A2TPRAIgst) + Number(A2TPRACgst) + Number(A2TPRASgst) + Number(A2TPRACess);

				var obj = {
					"anx2igst": A2Igst.toFixed(2), //anx2igst.toFixed(2),
					"anx2cgst": A2Cgst.toFixed(2), //anx2cgst.toFixed(2),
					"anx2sgst": A2Sgst.toFixed(2), //anx2sgst.toFixed(2),
					"anx2cess": A2Cess.toFixed(2), //anx2cess.toFixed(2),
					"prigst": PRIgst.toFixed(2),
					"prcgst": PRCgst.toFixed(2),
					"prsgst": PRSgst.toFixed(2),
					"prcess": PRCess.toFixed(2),
					"prAvailableIgst": PRAvlTxIgst.toFixed(2), //prAvailableIgst.toFixed(2),
					"prAvailableCgst": PRAvlTxCgst.toFixed(2), //prAvailableCgst.toFixed(2),
					"prAvailableSgst": PRAvlTxSgst.toFixed(2), //prAvailableSgst.toFixed(2),
					"prAvailableCess": PRAvlTxCess.toFixed(2), //prAvailableCess.toFixed(2),
					"retImpactA10Igst": retImpactA10Igst.toFixed(2),
					"retImpactA10Cgst": retImpactA10Cgst.toFixed(2),
					"retImpactA10Sgst": retImpactA10Sgst.toFixed(2),
					"retImpactA10Cess": retImpactA10Cess.toFixed(2),
					"retImpactA11Igst": retImpactA11Igst.toFixed(2),
					"retImpactA11Cgst": retImpactA11Cgst.toFixed(2),
					"retImpactA11Sgst": retImpactA11Sgst.toFixed(2),
					"retImpactA11Cess": retImpactA11Cess.toFixed(2),
					"retImpactB2Igst": retImpactB2Igst.toFixed(2),
					"retImpactB2Cgst": retImpactB2Cgst.toFixed(2),
					"retImpactB2Sgst": retImpactB2Sgst.toFixed(2),
					"retImpactB2Cess": retImpactB2Cess.toFixed(2),
					"retImpactB3Igst": retImpactB3Igst.toFixed(2),
					"retImpactB3Cgst": retImpactB3Cgst.toFixed(2),
					"retImpactB3Sgst": retImpactB3Sgst.toFixed(2),
					"retImpactB3Cess": retImpactB3Cess.toFixed(2),
					/////////A2 Tax - PR Tax////////
					"A2TPRTIgst": A2TPRTIgst.toFixed(2),
					"A2TPRTCgst": A2TPRTCgst.toFixed(2),
					"A2TPRTSgst": A2TPRTSgst.toFixed(2),
					"A2TPRTCess": A2TPRTCess.toFixed(2),
					////////PR Tax - PR Aval ///////
					"PRTPRAIgst": PRTPRAIgst.toFixed(2),
					"PRTPRACgst": PRTPRACgst.toFixed(2),
					"PRTPRASgst": PRTPRASgst.toFixed(2),
					"PRTPRACess": PRTPRACess.toFixed(2),
					////////A2 Tax - PR Aval////////
					"A2TPRAIgst": A2TPRAIgst.toFixed(2),
					"A2TPRACgst": A2TPRACgst.toFixed(2),
					"A2TPRASgst": A2TPRASgst.toFixed(2),
					"A2TPRACess": A2TPRACess.toFixed(2),

					/////////////////Totals////////
					"A2TaxTotal": A2TaxTotal.toFixed(2),
					"PRTaxTotal": PRTaxTotal.toFixed(2),
					"PRAvailTaxTotal": PRAvailTaxTotal.toFixed(2),
					"RA10TaxTotal": RA10TaxTotal.toFixed(2),
					"RA11TaxTotal": RA11TaxTotal.toFixed(2),
					"RB2TaxTotal": RB2TaxTotal.toFixed(2),
					"RB3TaxTotal": RB3TaxTotal.toFixed(2),
					"A2TPRTTaxTotal": A2TPRTTaxTotal.toFixed(2),
					"PRTPRATaxTotal": PRTPRATaxTotal.toFixed(2),
					"A2TPRATaxTotal": A2TPRATaxTotal.toFixed(2)
				};
				TotalAmounts.push(obj);
				var oRRConsldMdl = new JSONModel();
				oRRConsldMdl.setData(TotalAmounts[0]);
				this.getView().byId(id).setModel(oRRConsldMdl, "TotalAmounts1");
			}
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user Selects any perticular Row in Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionConsolMatch: function (OEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = OEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("idConsolidaTable").getModel("ConsldTablData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idverticalRes");
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user Selects any perticular Row in Exact Match Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionExactMatch: function (OEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = OEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("ExactMatchTabId").getModel("ExactMatchTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idverticalResExact");
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user Selects any perticular Row in  Match Upto Tolerence Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionMatchUptoTol: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("MatchUptoTolTabId").getModel("MatchUptoTolTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;

			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idverticalResMatchUpto");
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user Selects any perticular Row in  Doc type Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionDocTypeMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("DocTypeMisTabId").getModel("DocTypeMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idDocTyMisBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Doc Date Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionDDMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("DocDateMisTabId").getModel("DocDateMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idDocDateMisBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Value Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionValMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("ValueMisTabId").getModel("ValueMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idValueMisBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  POS Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionPOSMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("POSMisTabId").getModel("POSMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idPOSMisBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Multi Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionMultiMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("MultiMisTabId").getModel("MultiMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idMultiMisBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Fuzzy Mismatch Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionFuzzyMis: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("FuzzyMisTabId").getModel("FuzzyMisTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idFuzzyMatchBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Addition in ANX-2 Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionAddtnANX2: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("AddtnANX2TabId").getModel("AddtnANX2TabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFunAddnlANX2(finalData, "idAddA2Blk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Addition in PR Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionAddtnPR: function (oEvt) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvt.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("AddtnPRTabId").getModel("AddtnPRTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idAddPRBlk");
		},

		/**
		 * Added by Rakesh Bommidi
		 * Called when user Selects any perticular Row in  Force Match(Force Matched Tab) Table
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onSelectionFMForceM: function (oEvent) {
			var SelArr = [];
			var finalData;
			var SelDataIndex = oEvent.getSource().getSelectedIndices();
			var tabModel = this.getView().byId("idFMForceMTable").getModel("FMForceMTabData").getData();
			if (SelDataIndex.length === 1) {
				SelArr.push(tabModel[SelDataIndex]);
				finalData = SelArr;
			} else if (SelDataIndex.length === 0) {
				finalData = tabModel;
			} else {
				for (var i = 0; i < SelDataIndex.length; i++) {
					SelArr.push(tabModel[SelDataIndex[i]]);
					finalData = SelArr;
				}
			}
			this.CalculationFun(finalData, "idForceMatchBlk");
		},

		////////
		//Accept A2, Accept A2 & ITC PR Avial, Accept A2 & ITC PR Tax ..etc. Segment Buttons
		/// BOC by Rakesh on 22.01.2010
		onPressReconResultAction: function (oEvent) {
			var that = this;
			var oConsTableIndex = this.byId("idConsolidaTable").getSelectedIndices();
			if (oConsTableIndex.length === 0) {
				MessageToast.show("Please select atleast one record for details");
				return;
			}
			var oActionData = oEvent.getSource().getText();
			var oMessage = "Do you want to " + oEvent.getSource().getText() + " ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.oSaveUserActnData(oActionData);
					}
				}
			});

		},

		oSaveUserActnData: function (oActionData) {
			var oConslTabData = this.byId("idConsolidaTable").getModel("ConsldTablData").getData();
			var oConslSelIndx = this.byId("idConsolidaTable").getSelectedIndices();
			switch (oActionData) {
			case "Accept A2 & ITC A2":
				var vUserAction = "A1";
				break;
			case "Accept A2 & ITC PR available":
				vUserAction = "A2";
				break;
			case "Accept A2 & ITC PR Tax":
				vUserAction = "A3";
				break;
			case "Pending ANX2":
				vUserAction = "P1";
				break;
			case "Reject ANX2":
				vUserAction = "R1";
				break;
			case "Reject A2 and ITC PR available": //"Reject A2 and ITC PR acailable": //
				vUserAction = "R1U1";
				break;
			case "Reject A2 and ITC PR Tax":
				vUserAction = "R1U2";
				break;
			case "Provisinal ITC PR available":
				vUserAction = "U1";
				break;
			case "Provisinal ITC PR Tax":
				vUserAction = "U2";
				break;
			}
			for (var i = 0; i < oConslSelIndx.length; i++) {
				oConslTabData[oConslSelIndx[i]].userAction = vUserAction;
			}

			/*var oCopyTablData1 = new sap.ui.model.json.JSONModel(oConslTabData);
			this.byId("idConsolidaTable").setModel(oCopyTablData1, "ConsldTablData");*/
			this.byId("idConsolidaTable").getModel("ConsldTablData").refresh(true);
			this.byId("idConsolidaTable").setSelectedIndex(-1);
		},

		/// EOC by Rakesh on 22.01.2010
		//Copy 
		onMenuItemPressCopy: function (oEvt) {
			var oCopyTablIndx = this.byId("idConsolidaTable").getSelectedIndices();
			if (oCopyTablIndx.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}

			var oCopyTablData = this.byId("idConsolidaTable").getModel("ConsldTablData").getData();
			var oCopyArray = [];
			var oCopySelKey = oEvt.getParameters().item.getKey();
			/*	switch (oCopySelKey) {
				case "Accept A2 & ITC A2":
					var oCopyUserAction = "A1";
					break;
				case "Accept A2 & ITC PR available":
					oCopyUserAction = "A2";
					break;
				}*/

			for (var i = 0; i < oCopyTablIndx.length; i++) {
				if (oCopySelKey === "suggest") {
					var oCopyUserAction = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
					switch (oCopyUserAction) {
					case "Accept A2 & ITC A2":
						var vUserAction = "A1";
						break;
					case "Accept A2 & ITC PR available":
						vUserAction = "A2";
						break;
					case "Accept A2 & ITC PR Tax":
						vUserAction = "A3";
						break;
					case "Pending ANX2":
						vUserAction = "P1";
						break;
					case "Reject ANX2":
						vUserAction = "R1";
						break;
					case "Reject A2 and ITC PR available":
						vUserAction = "R1U1";
						break;
					case "Reject A2 and ITC PR Tax":
						vUserAction = "R1U2";
						break;
					case "ITC PR available of earlier tax period":
						vUserAction = "A1U1";
						break;
					case "ITC PR tax of earlier tax period":
						vUserAction = "A4";
						break;
					case "Provisional ITC PR available":
						vUserAction = "U1";
						break;
					case "Provisional ITC PR Tax":
						vUserAction = "U2";
						break;
					}

					oCopyTablData[oCopyTablIndx[i]].userAction = oCopyUserAction;
					oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
				} else if (oCopySelKey === "previousPR") {
					//for (var i = 0; i < oCopyTablIndx.length; i++) {
					var oCopyUserAction = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
					oCopyTablData[oCopyTablIndx[i]].userAction = oCopyUserAction;
					oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
					//}
				} else if (oCopySelKey === "previousA2") {
					//for (var i = 0; i < oCopyTablIndx.length; i++) {
					var oCopyObj2 = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
					oCopyTablData[oCopyTablIndx[i]].userAction = oCopyObj2;
					oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
					//}
				}
			}
			this.byId("idConsolidaTable").getModel("ConsldTablData").refresh(true);
			this.byId("idConsolidaTable").setSelectedIndex(-1);
			/*var oCopyTablData1 = new sap.ui.model.json.JSONModel(oCopyTablData);
			this.byId("idConsolidaTable").setModel(oCopyTablData1, "ConsldTablData");*/

		},

		////########### Update Functionality on 24.01.2020
		onPrsSaveRRChanges: function (oEvt) {
			var oTaxPeriod = this.byId("dtConsld").getValue();
			var oConslTabIndices = this.byId("idConsolidaTable").getSelectedIndices();
			var oConslTabData = this.byId("idConsolidaTable").getModel("ConsldTablData").getData();
			if (oConslTabIndices.length === 0) {
				MessageBox.information("Please Select atleast one record");
				return;
			}

			var oPayload = [];
			var oCommonDetails = [];
			var details = [];
			//eslint-disable-line
			for (var j = 0; j < oConslTabIndices.length; j++) {
				var oRepType = oConslTabData[oConslTabIndices[j]].reportType;
				var oUsAction = oConslTabData[oConslTabIndices[j]].userAction;

				for (var k = 0; k < oPayload.length; k++) {
					if (oPayload[k].reportName === oRepType) {
						for (var m = 0; m < oPayload[k].commonDetails.length; m++) {
							if (oPayload[k].commonDetails[m].userAction === oUsAction) {
								oPayload[k].commonDetails[m].details.push({
									"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
									"prKey": oConslTabData[oConslTabIndices[j]].prKey,
									"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
									"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
								});
								break;
							}
						}
						if (m === oPayload[k].commonDetails.length) {
							var oCommDetails = {
								"userAction": oUsAction,
								"details": [{
									"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
									"prKey": oConslTabData[oConslTabIndices[j]].prKey,
									"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
									"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
								}]
							};
							oPayload[k].commonDetails.push(oCommDetails);
						}
						break;
					}
				}
				if (k === oPayload.length) {
					var oDummyObj_CommonDetails = {
						"reportName": oRepType,
						"taxPeriod": oTaxPeriod,
						"commonDetails": [{
							"userAction": oUsAction,
							"details": []
						}]
					};
					oDummyObj_CommonDetails.commonDetails[0].details.push({
						"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
						"prKey": oConslTabData[oConslTabIndices[j]].prKey,
						"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
						"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
					});
					// oDummyObj_CommonDetails.userAction = oUsAction;
					// oDummyObj_CommonDetails.details = [];
					// oDummyObj_CommonDetails.details.push({
					// 	"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
					// 	"prKey": oConslTabData[oConslTabIndices[j]].prKey,
					// 	"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
					// 	"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
					// });
					oPayload.push(oDummyObj_CommonDetails);
				}
			}

			// this.oSavePayload = {
			// 	req: {
			// 		"reportType": [{
			// 			"reportName": oRepType, //oReptName, //"Exact Match",
			// 			"taxPeriod": oTaxPeriod, //"02019",
			// 			"commonDetails": oCommonDetails
			// 				/*[{
			// 				"userAction": oUsActionData, //oConsUserActionArr, //"Action1",
			// 				"details": oCommonDetails
			// 					/*[{
			// 						"reconLinkId": "23232",
			// 						"prKey": "23232",
			// 						"a2Key": "23232",
			// 						"gstin": "11SUPEY1103165" //(Recipient gstins)
			// 					}]
			// 			}] */
			// 		}]
			// 	}
			// };

			var oSavePayload = {
				"req": {
					"reportType": oPayload
				}
			};

			this._saveChange(oSavePayload);
		},

		_saveChange: function (oSavePayload) {
			var that = this;
			MessageBox.confirm("Are you sure, You want to Submit", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that._saveRespChang(oSavePayload);
					}
				}
			});

		},

		_saveRespChang: function (oSavePayload) {
			var that = this;
			var oConsldRTPath = "/aspsapapi/updateReconResultAction.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oConsldRTPath,
					contentType: "application/json",
					data: JSON.stringify(oSavePayload)
				}).done(function (data, status, jqXHR) {
					that.onRRConsoldGO();
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//////  Document Type Menu
		onRRDocTypCHange: function (oEvt) {
			this.ExactDocTyp = undefined;
			this.MUTDocType = undefined;
			this.DTMDocType = undefined;
			this.DDMDocType = undefined;
			this.ValDocType = undefined;
			this.POSDocType = undefined;
			this.MultiMDocType = undefined;
			this.FuzyDocType = undefined;
			this.FMMDocType = undefined;
			this.AdnlANX2DocType = undefined;
			this.AdnlPRDocType = undefined;
			this.FMAdnlDocType = undefined;
		},
		//////on Change of Exact Match GSTIN //////////////
		onExactGSTIN: function (oEvt) {
			this.exactKey = "a";
			this.ExactGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngExactM: function (oEvt) {
			this.ExactDocTyp = oEvt.getSource().getSelectedKeys();
		},

		onPressExactMatchAction: function (oEvent) {
			this.onPressAction(oEvent, "ExactMatchTabId", "ExactMatchTabData");
		},

		onMenuItemPressExactCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "ExactMatchTabId", "ExactMatchTabData");
		},

		onPrsSaveExactChanges: function () {
			this.onPrsAllChanges("ExactDateId", "ExactMatchTabId", "ExactMatchTabData", "Exact Match");
		},

		onMenuItemExactOtherResponce: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "ExactMatchTabId", "ExactMatchTabData");
		},

		////////////////////Match Upto Tolerence///////////////

		////////////on Select GSTN/////////////
		onMUTGSTN: function (oEvt) {
			this.MUTKey = "MUT";
			this.MUTGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypeChangeMUT: function (oEvt) {
			this.MUTDocType = oEvt.getSource().getSelectedKeys();
		},

		//////////on Press User Action Buttons/////////////
		onPressMUTAction: function (oEvent) {
			this.onPressAction(oEvent, "MatchUptoTolTabId", "MatchUptoTolTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItemMUTPressCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "MatchUptoTolTabId", "MatchUptoTolTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveMUTChanges: function () {
			this.onPrsAllChanges("MUTDateId", "MatchUptoTolTabId", "MatchUptoTolTabData", "Match upto Tolerence");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItemMUTOtherResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "MatchUptoTolTabId", "MatchUptoTolTabData");
		},

		//////////////////Document Type Mismatch///////////////////////

		onDTMGstn: function (oEvt) {
			this.DTypKey = "DTM";
			this.DTMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngDTM: function (oEvt) {
			this.DTMDocType = oEvt.getSource().getSelectedKeys();
		},
		onPressDTMAction: function (oEvent) {
			this.onPressAction(oEvent, "DocTypeMisTabId", "DocTypeMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItemDTMPressCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "DocTypeMisTabId", "DocTypeMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveDTMChanges: function () {
			this.onPrsAllChanges("DTMDateId", "DocTypeMisTabId", "DocTypeMisTabData", "Document Type Mismatch");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItemPressDTMotherResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "DocTypeMisTabId", "DocTypeMisTabData");
		},

		//////////////////Document Date Mismatch/////////////////////

		onDDMGstn: function (oEvt) {
			this.DDMKey = "DDM";
			this.DDMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngDDm: function (oEvt) {
			this.DDMDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressDDMAction: function (oEvent) {
			this.onPressAction(oEvent, "DocDateMisTabId", "DocDateMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItemPressCopyDDM: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "DocDateMisTabId", "DocDateMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveDDMChanges: function () {
			this.onPrsAllChanges("DDMDateId", "DocDateMisTabId", "DocDateMisTabData", "Document Date Mismatch");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItemPressDDMotherResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "DocDateMisTabId", "DocDateMisTabData");
		},

		//////////////////Value Mismatch Tab/////////////////////
		onVMGstn: function (oEvt) {
			this.ValueMKey = "VM";
			this.ValMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypeChngValM: function (oEvt) {
			this.ValDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressValueMatchAction: function (oEvent) {
			this.onPressAction(oEvent, "ValueMisTabId", "ValueMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItemPressValMisCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "ValueMisTabId", "ValueMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveValMisChanges: function () {
			this.onPrsAllChanges("ValMDateId", "ValueMisTabId", "ValueMisTabData", "Value Mismatch");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItemPressValMisOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "ValueMisTabId", "ValueMisTabData");
		},

		//////////////////POS Mismatch Tab/////////////////////
		onPOSMGstn: function (oEvt) {
			this.POSMKey = "PM";
			this.POSMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngPOS: function (oEvt) {
			this.POSDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressPOSMisMAction: function (oEvent) {
			this.onPressAction(oEvent, "POSMisTabId", "POSMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItmPOSMisPrsCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "POSMisTabId", "POSMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSavePOSMisMChanges: function () {
			this.onPrsAllChanges("POSMDateId", "POSMisTabId", "POSMisTabData", "POS Mismatch");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsPOSMisOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "POSMisTabId", "POSMisTabData");
		},

		//////////////////Multi Mismatch Tab/////////////////////
		onMultiMGstn: function (oEvt) {
			this.MultiMKey = "MM";
			this.MultiMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngMultiM: function (oEvt) {
			this.MultiMDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressMultiMisAction: function (oEvent) {
			this.onPressAction(oEvent, "MultiMisTabId", "MultiMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItmPrsMultiMisCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "MultiMisTabId", "MultiMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveMultiMisMChanges: function () {
			this.onPrsAllChanges("MultiMDateId", "MultiMisTabId", "MultiMisTabData", "Multi-Mismatch");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsMultiMisOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "MultiMisTabId", "MultiMisTabData");
		},

		//////////////////Fuzzy Mismatch Tab/////////////////////
		onFuzyGstn: function (oEvt) {
			this.FuzyMKey = "FM";
			this.FuzyMGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngFuzzy: function (oEvt) {
			this.FuzyDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressFuzzyMAction: function (oEvent) {
			this.onPressAction(oEvent, "FuzzyMisTabId", "FuzzyMisTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItmPresFuzyMCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "FuzzyMisTabId", "FuzzyMisTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveFuzyMChanges: function () {
			this.onPrsAllChanges("FuzzyMDateId", "FuzzyMisTabId", "FuzzyMisTabData", "Fuzzy Match");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItemPressFuzyMOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "FuzzyMisTabId", "FuzzyMisTabData");
		},

		//////////////////Force Match Tab(Force Matched)/////////////////////
		onFMForceGstn: function (oEvt) {
			this.FMForceMKey = "FMF";
			this.FMForceGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngFMMatch: function (oEvt) {
			this.FMMDocType = oEvt.getSource().getSelectedKeys();
		},

		onPrsFMForcedAction: function (oEvent) {
			this.onPressAction(oEvent, "idFMForceMTable", "FMForceMTabData");
		},

		/*///////////on Press Copy Menu Button//////////////
		onMenuItmPresFuzyMCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "idFMForceMTable", "FuzzyMisTabData");
		},*/

		//////////////On Press Save Changes/////////////
		onPrsSaveFMForcedChanges: function () {
			this.onPrsAllChanges("FMForceMDateId", "idFMForceMTable", "FMForceMTabData", "Force Match");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsFMForcedOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "idFMForceMTable", "FMForceMTabData");
		},

		//////////////////Force Match Tab(Addtional Entries)/////////////////////
		onFMAddnlGstn: function (oEvt) {
			this.FMAddnlGSTIN = oEvt.getSource().getSelectedKeys();
			this.FMAddnlGSTINKey = "FAK";
		},

		onDocTypChngAdnlEntrs: function (oEvt) {
			this.FMAdnlDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressFMAddtnlAction: function (oEvent) {
			this.onPressAction(oEvent, "idFMAdtnlEntrsTabl", "FMAdtnlEntrsTablData");
		},

		///////////on Press Copy Menu Button//////////////
		//onMenuItmPresFuzyMCopy: function (oEvt) {
		//	this.onMenuItemPressAllCopy(oEvt, "idFMAdtnlEntrsTabl", "FuzzyMisTabData");
		//},

		//////////////On Press Save Changes/////////////
		onPrsSaveFMAddtnlChanges: function () {
			this.onPrsAllChanges("idFMAdnlEntrsDate", "idFMAdtnlEntrsTabl", "FMAdtnlEntrsTablData", "Additional Entries");
		},

		//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsFMAddtnlOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "idFMAdtnlEntrsTabl", "FMForceMTabData");
		},

		//////////////////Addition in ANX2/////////////////////
		onAddtnANX2lGstn: function (oEvt) {
			this.AdnlANX2Key = "ANX2";
			this.AddtnANX2GSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngAdnlANX2: function (oEvt) {
			this.AdnlANX2DocType = oEvt.getSource().getSelectedKeys();
		},

		onPressAddtnANX2Action: function (oEvent) {
			this.onPressAction(oEvent, "AddtnANX2TabId", "AddtnANX2TabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItmPrsAddtnANX2Copy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "AddtnANX2TabId", "AddtnANX2TabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveAddtnANX2Changes: function () {
			this.onPrsAllChanges("AddtnANX2DateId", "AddtnANX2TabId", "AddtnANX2TabData", "Addition in ANX-2");
		},

		/*//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsFMAddtnlOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "AddtnANX2TabId", "AddtnANX2TabData");
		},*/

		//////////////////Addition in PR/////////////////////
		onAddtnPRGstn: function (oEvt) {
			this.AdnlPRKey = "PR";
			this.AddtnPRGSTIN = oEvt.getSource().getSelectedKeys();
		},

		onDocTypChngAdnlPR: function (oEvt) {
			this.AdnlPRDocType = oEvt.getSource().getSelectedKeys();
		},

		onPressAddtnPRAction: function (oEvent) {
			this.onPressAction(oEvent, "AddtnPRTabId", "AddtnPRTabData");
		},

		///////////on Press Copy Menu Button//////////////
		onMenuItemPressAddtnPRCopy: function (oEvt) {
			this.onMenuItemPressAllCopy(oEvt, "AddtnPRTabId", "AddtnPRTabData");
		},

		//////////////On Press Save Changes/////////////
		onPrsSaveAddtnPRChanges: function () {
			this.onPrsAllChanges("AddtnPRDateId", "AddtnPRTabId", "AddtnPRTabData", "Addition in PR");
		},

		/*//////////////on Press Other Response Menu Button ////////////////////
		onMenuItmPrsFMAddtnlOthrResp: function (oEvt) {
			this.onMenuItemOtherResponce(oEvt, "AddtnPRTabId", "AddtnPRTabData");
		},*/

		onPressAction: function (oEvent, Id, AliasName) {
			var that = this;
			var oConsTableIndex = this.byId(Id).getSelectedIndices();
			if (oConsTableIndex.length === 0) {
				MessageToast.show("Please select atleast one record for details");
				return;
			}
			var oActionData = oEvent.getSource().getText();
			var oMessage = "Do you want to " + oEvent.getSource().getText() + " ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.oSaveUserActnDataExact(oActionData, Id, AliasName);
					}
				}
			});

		},

		oSaveUserActnDataExact: function (oActionData, Id, AliasName) {
			var oConslTabData = this.byId(Id).getModel(AliasName).getData();
			var oConslSelIndx = this.byId(Id).getSelectedIndices();
			var vUserAction;
			switch (oActionData) {
			case "Accept A2 & ITC A2":
				vUserAction = "A1";
				break;
			case "Accept A2 & ITC PR available":
				vUserAction = "A2";
				break;
			case "Accept A2 & ITC PR Tax":
				vUserAction = "A3";
				break;
			case "Pending ANX2":
				vUserAction = "P1";
				break;
			case "Reject ANX2":
				vUserAction = "R1";
				break;
			case "Reject A2 and ITC PR available": //"Reject A2 and ITC PR acailable": //
				vUserAction = "R1U1";
				break;
			case "Reject A2 and ITC PR Tax":
				vUserAction = "R1U2";
				break;
			case "Provisinal ITC PR available":
				vUserAction = "U1";
				break;
			case "Provisinal ITC PR Tax":
				vUserAction = "U2";
				break;
			}
			for (var i = 0; i < oConslSelIndx.length; i++) {
				oConslTabData[oConslSelIndx[i]].userAction = vUserAction;
			}

			this.byId(Id).getModel(AliasName).refresh(true);
			this.byId(Id).setSelectedIndex(-1);
		},

		/*onMenuItemOtherResponce: function (oEvt, Id, Aliasname) {
			var oCopyTablIndx = this.byId(Id).getSelectedIndices();
			if (oCopyTablIndx.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}

			var oCopyTablData = this.byId(Id).getModel(Aliasname).getData();
			//var oCopyArray = [];
			var oCopySelKey = oEvt.getParameters().item.getKey();
			//	for (var i = 0; i < oCopyTablIndx.length; i++) {
			//	if (oCopySelKey === "suggest") {
			//var oCopyUserAction = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
			switch (oCopySelKey) {
			case "Accept A2 & ITC A2":
				var vUserAction = "A1";
				break;
			case "Accept A2 & ITC PR available":
				vUserAction = "A2";
				break;
			case "Accept A2 & ITC PR Tax":
				vUserAction = "A3";
				break;
			case "Pending ANX2":
				vUserAction = "P1";
				break;
			case "Reject ANX2":
				vUserAction = "R1";
				break;
			case "Reject A2 and ITC PR available":
				vUserAction = "R1U1";
				break;
			case "Reject A2 and ITC PR Tax":
				vUserAction = "R1U2";
				break;
			case "ITC PR available of earlier tax period":
				vUserAction = "A1U1";
				break;
			case "ITC PR tax of earlier tax period":
				vUserAction = "A4";
				break;
			case "Provisional ITC PR available":
				vUserAction = "U1";
				break;
			case "Provisional ITC PR Tax":
				vUserAction = "U2";
				break;
			}
			for (var i = 0; i < oCopyTablIndx.length; i++) {
				oCopyTablData[oCopyTablIndx[i]].userAction = vUserAction;
				//	oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
			}
			this.byId(Id).getModel(Aliasname).refresh(true);
			this.byId(Id).setSelectedIndex(-1);
		},*/
		onMenuItemOtherResponce: function (oEvt, Id, Aliasname) {
			var that = this;
			var oOthRespTablIndx = this.byId(Id).getSelectedIndices();
			if (oOthRespTablIndx.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}
			var oActionData = oEvt.getParameters().item.getKey();
			var oActionText = oEvt.getParameters().item.getText();
			var oMessage = "Do you want to " + oActionText + " ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.onMenuItemOtherResponce1(oActionData, Id, Aliasname);
					}
				}
			});

		},

		onMenuItemOtherResponce1: function (oEvt, Id, Aliasname) {

			var oOthRespTablData = this.byId(Id).getModel(Aliasname).getData();
			var oUserActionArray = [];
			var oCopySelKey = oEvt;
			//	for (var i = 0; i < oCopyTablIndx.length; i++) {
			//	if (oCopySelKey === "suggest") {
			//var oCopyUserAction = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
			var oOthRespTablIndx = this.byId(Id).getSelectedIndices();
			for (var i = 0; i < oOthRespTablIndx.length; i++) {
				switch (oCopySelKey) {
				case "Accept A2 & ITC A2":
					var vUserAction = "A1";
					break;
				case "Accept A2 & ITC PR available":
					vUserAction = "A2";
					break;
				case "Accept A2 & ITC PR Tax":
					vUserAction = "A3";
					break;
				case "Pending ANX2":
					vUserAction = "P1";
					break;
				case "Reject ANX2":
					vUserAction = "R1";
					break;
				case "Reject A2 and ITC PR available":
					vUserAction = "R1U1";
					break;
				case "Reject A2 and ITC PR Tax":
					vUserAction = "R1U2";
					break;
				case "ITC PR available of earlier tax period":
					vUserAction = "A1U1";
					break;
				case "ITC PR tax of earlier tax period":
					vUserAction = "A1U2";
					break;
				case "Accept anx 2 of earlier tax period":
					vUserAction = "A4";
					break;
				case "Provisional ITC PR available":
					vUserAction = "U1";
					break;
				case "Provisional ITC PR Tax":
					vUserAction = "U2";
					break;
				}
				if (oCopySelKey === "ITC PR available of earlier tax period") {
					var oUserAction = vUserAction; //oCopyTablData[oCopyTablIndx[i]].userAction;
					//oCopyTablData[oCopyTablIndx[i]].userAction = vUserAction;
					oOthRespTablData[oOthRespTablIndx[i]].userAction = oUserAction;
					oUserActionArray.push(oOthRespTablData[oOthRespTablIndx[i]].userAction);
				} else if (oCopySelKey === "ITC PR tax of earlier tax period") {
					var oITCPRErlrTP = vUserAction; //oCopyTablData[oCopyTablIndx[i]].userAction;
					oOthRespTablData[oOthRespTablIndx[i]].userAction = oITCPRErlrTP;
					//oUserActionArray.push(oOthRespTablData[oOthRespTablIndx[i]].userAction);
				} else if (oCopySelKey === "Accept anx 2 of earlier tax period") {
					var oAcptAnx2ETP = vUserAction; //oCopyTablData[oCopyTablIndx[i]].userAction;
					oOthRespTablData[oOthRespTablIndx[i]].userAction = oAcptAnx2ETP;
					//	oUserActionArray.push(oOthRespTablData[oOthRespTablIndx[i]].userAction);
				} else if (oCopySelKey === "entityLevel") {
					var oClearErlr = "";
					oOthRespTablData[oOthRespTablIndx[i]].userAction = oClearErlr;
					//oUserActionArray.push(oOthRespTablData[oOthRespTablIndx[i]].userAction);
				}
			}
			this.byId(Id).getModel(Aliasname).refresh(true);
			this.byId(Id).setSelectedIndex(-1);
		},

		onMenuItemPressAllCopy: function (oEvt, Id, Aliasname) {
			var oCopyTablIndx = this.byId(Id).getSelectedIndices();
			if (oCopyTablIndx.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}

			var oCopyTablData = this.byId(Id).getModel(Aliasname).getData();
			var oCopyArray = [];
			var oCopySelKey = oEvt.getParameters().item.getKey();
			var vUserAction;
			for (var i = 0; i < oCopyTablIndx.length; i++) {
				var oCopyUserAction = oCopyTablData[oCopyTablIndx[i]].suggestedResponse;
				switch (oCopyUserAction) {
				case "Accept A2 & ITC A2":
					vUserAction = "A1";
					break;
				case "Accept A2 & ITC PR available":
					vUserAction = "A2";
					break;
				case "Accept A2 & ITC PR Tax":
					vUserAction = "A3";
					break;
				case "Pending ANX2":
					vUserAction = "P1";
					break;
				case "Reject ANX2":
					vUserAction = "R1";
					break;
				case "Reject A2 and ITC PR available":
					vUserAction = "R1U1";
					break;
				case "Reject A2 and ITC PR Tax":
					vUserAction = "R1U2";
					break;
				case "ITC PR available of earlier tax period":
					vUserAction = "A1U1";
					break;
				case "ITC PR tax of earlier tax period":
					vUserAction = "A4";
					break;
				case "Provisional ITC PR available":
					vUserAction = "U1";
					break;
				case "Provisional ITC PR Tax":
					vUserAction = "U2";
					break;
				}
				if (oCopySelKey === "suggest") {
					oCopyTablData[oCopyTablIndx[i]].userAction = vUserAction;
					//oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
				} else if (oCopySelKey === "previousPR") {
					var oCopyUserActionPR = oCopyTablData[oCopyTablIndx[i]].prPreviousResponse;
					oCopyTablData[oCopyTablIndx[i]].userAction = oCopyUserActionPR;
					//oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);
					/*oCopyTablData[oCopyTablIndx[i]].userAction = prPreviousResponse;
					oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);*/
				} else if (oCopySelKey === "previousA2") {
					var oCopyUserActionA2 = oCopyTablData[oCopyTablIndx[i]].anx2pPreviousResponse;
					oCopyTablData[oCopyTablIndx[i]].userAction = oCopyUserActionA2;
					/*	oCopyTablData[oCopyTablIndx[i]].userAction = vUserAction;
						oCopyArray.push(oCopyTablData[oCopyTablIndx[i]].userAction);*/
				}
			}
			this.byId(Id).getModel(Aliasname).refresh(true);
			this.byId(Id).setSelectedIndex(-1);
		},

		onPrsAllChanges: function (dateId, TabId, AliasName, ReportType) {
			var oTaxPeriod = this.byId(dateId).getValue();
			var oConslTabIndices = this.byId(TabId).getSelectedIndices();
			var oConslTabData = this.byId(TabId).getModel(AliasName).getData();
			if (oConslTabIndices.length === 0) {
				MessageBox.information("Please Select atleast one record");
				return;
			}
			var oPayload = [];
			var oCommonDetails = [];
			var details = [];
			for (var j = 0; j < oConslTabIndices.length; j++) {
				var oRepType = ReportType;
				var oUsAction = oConslTabData[oConslTabIndices[j]].userAction;

				for (var k = 0; k < oPayload.length; k++) {
					if (oPayload[k].reportName === oRepType) {
						for (var m = 0; m < oPayload[k].commonDetails.length; m++) {
							if (oPayload[k].commonDetails[m].userAction === oUsAction) {
								oPayload[k].commonDetails[m].details.push({
									"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
									"prKey": oConslTabData[oConslTabIndices[j]].prKey,
									"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
									"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
								});
								break;
							}
						}
						if (m === oPayload[k].commonDetails.length) {
							var oCommDetails = {
								"userAction": oUsAction,
								"details": [{
									"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
									"prKey": oConslTabData[oConslTabIndices[j]].prKey,
									"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
									"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
								}]
							};
							oPayload[k].commonDetails.push(oCommDetails);
						}
						break;
					}
				}
				if (k === oPayload.length) {
					var oDummyObj_CommonDetails = {
						"reportName": oRepType,
						"taxPeriod": oTaxPeriod,
						"commonDetails": [{
							"userAction": oUsAction,
							"details": []
						}]
					};
					oDummyObj_CommonDetails.commonDetails[0].details.push({
						"reconLinkId": oConslTabData[oConslTabIndices[j]].reconLinkId,
						"prKey": oConslTabData[oConslTabIndices[j]].prKey,
						"a2Key": oConslTabData[oConslTabIndices[j]].a2Key,
						"gstin": oConslTabData[oConslTabIndices[j]].recipientGstin
					});
					oPayload.push(oDummyObj_CommonDetails);
				}
			}
			var oSavePayload = {
				"req": {
					"reportType": oPayload
				}
			};

			this._saveChangeAll(oSavePayload, ReportType);
		},

		_saveChangeAll: function (oSavePayload, ReportType) {
			var that = this;
			MessageBox.confirm("Are you sure, You want to Submit", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that._saveRespChangAll(oSavePayload, ReportType);
					}
				}
			});

		},

		_saveRespChangAll: function (oSavePayload, ReportType) {
			var that = this;
			var oConsldRTPath = "/aspsapapi/updateReconResultAction.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oConsldRTPath,
					contentType: "application/json",
					data: JSON.stringify(oSavePayload)
				}).done(function (data, status, jqXHR) {
					MessageBox.success("Records Updated Successfully");
					if (ReportType === "Exact Match") {
						that.onExactMatchGo();
					} else if (ReportType === "Match upto Tolerence") {
						that.onMatchUptoGo();
					} else if (ReportType === "Document Type Mismatch") {
						that.onDocTypeMisGo();
					} else if (ReportType === "Document Date Mismatch") {
						that.onDocDateMisGo();
					} else if (ReportType === "Value Mismatch") {
						that.onValueMisGo();
					} else if (ReportType === "POS Mismatch") {
						that.onPOSMisGo();
					} else if (ReportType === "Multi-Mismatch") {
						that.onMultiMisGo();
					} else if (ReportType === "Fuzzy Match") {
						that.onFuzzyMisGo();
					} else if (ReportType === "Force Match") {
						that.onFMForceMatcdGo();
					} else if (ReportType === "Additional Entries") {
						that.onFMAdnlEntrsGo();
					} else if (ReportType === "Addition in ANX-2") {
						that.onAddtnANX2Go();
					} else if (ReportType === "Addition in PR") {
						that.onAddtnInPRGo();
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onRetainConslReptType: function (oEvt) {
			this.ConslRetnRT = oEvt.getSource().getSelectedKeys();
		},

		onClearFilterConslM: function () {
			var vDate = new Date();
			this.byId("dtConsld").setMaxDate(vDate);
			this.byId("dtConsld").setDateValue(vDate);
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRConsGstins").setSelectedKeys(Gstns);
			this.byId("idConsldTabType").setSelectedKeys([]);
			this.byId("idConsldReptType").setSelectedKeys([]);
			this.onRRConsoldGO();
		},

		onClearFilterExactM: function () {
			this.oExactMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("ExactDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRExctMatchGstins").setSelectedKeys(Gstns);
			this.byId("ExactTabType").setSelectedKeys([]);
			this.ExactGSTIN = Gstns;
			this.onExactMatchGo();
		},

		onClearFilterMUT: function () {
			this.oMatchUpToEnty = undefined;
			this.oMUTMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("MUTDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRMatchUTGstins").setSelectedKeys(Gstns);
			this.byId("MUTTabType").setSelectedKeys([]);
			this.MUTGSTIN = Gstns;
			this.onMatchUptoGo();
		},

		onClearFilterDTM: function () {
			this.oDocTypMEnty = undefined;
			this.oDTypMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("DTMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRDTMGstins").setSelectedKeys(Gstns);
			this.byId("DTMTabType").setSelectedKeys([]);
			this.DTMGSTIN = Gstns;
			this.onDocTypeMisGo();

		},

		onClearFilterDDM: function () {
			this.oDocDateMEnty = undefined;
			this.oDocDMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("DDMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRDDMGstins").setSelectedKeys(Gstns);
			this.byId("DDMTabType").setSelectedKeys([]);
			this.DDMGSTIN = Gstns;
			this.onDocDateMisGo();

		},

		onClearFilterValueM: function () {
			this.oValueMEnty = undefined;
			this.oValMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("ValMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRValMGstins").setSelectedKeys(Gstns);
			this.byId("ValMTabType").setSelectedKeys([]);
			this.ValMGSTIN = Gstns;
			this.onValueMisGo();

		},
		onClearFilterPOSM: function () {
			this.oPOSMEnty = undefined;
			this.oPOSMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("POSMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRPOSMGstins").setSelectedKeys(Gstns);
			this.byId("POSMTabType").setSelectedKeys([]);
			this.POSMGSTIN = Gstns;
			this.onPOSMisGo();

		},
		onClearFilterMultiMisM: function () {
			this.oMultiMEnty = undefined;
			this.oMultiMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("MultiMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRMultiMGstins").setSelectedKeys(Gstns);
			this.byId("MultiMTabType").setSelectedKeys([]);
			this.MultiMGSTIN = Gstns;
			this.onMultiMisGo();
		},

		onClearFilterFuzzyM: function () {
			this.oFuzzyMEnty = undefined;
			this.oFuzzyMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("FuzzyMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRFuzzyMGstins").setSelectedKeys(Gstns);
			this.byId("FuzzyMTabType").setSelectedKeys([]);
			this.FuzyMGSTIN = Gstns;
			this.onFuzzyMisGo();
		},

		onClearFilterForceM: function () {
			this.oFMForcMatchMEnty = undefined;
			this.oForceMDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("FMForceMDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRFMForceMGstins").setSelectedKeys(Gstns);
			this.byId("idFMForceMTabType").setSelectedKeys([]);
			this.FMForceGSTIN = Gstns;
			this.onFMForceMatcdGo();
		},

		onClearFilterForceMAddnl: function () {
			this.oFMAddnlEEnty = undefined;
			this.oFMAddlnDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("idFMAdnlEntrsDate").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idFMAdnlEntrsGstins").setSelectedKeys(Gstns);
			this.byId("idAdnlEntrsTabTyp").setSelectedKeys([]);
			this.FMAddnlGSTIN = Gstns;
			this.onFMAdnlEntrsGo();
		},

		onClearFilterAddnlANX2: function () {
			this.oAddnlANX2MEnty = undefined;
			this.oAddnlANX2Date = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("AddtnANX2DateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRAddtnANX2Gstins").setSelectedKeys(Gstns);
			this.byId("idAddtnANX2TabType").setSelectedKeys([]);
			this.AddtnANX2GSTIN = Gstns;
			this.onAddtnANX2Go();
		},

		onClearFilterAddnlPR: function () {
			this.oAddnlPREnty = undefined;
			this.oAddnlPRDate = undefined;
			var oConslDate = this.byId("dtConsld").getDateValue();
			this.byId("AddtnPRDateId").setDateValue(new Date(oConslDate));
			var Gstns = this.byId("idRRConsGstins").getSelectedKeys();
			this.byId("idRRAddtnPRGstins").setSelectedKeys(Gstns);
			this.byId("idAddtnPRTabTyp").setSelectedKeys([]);
			this.AddtnPRGSTIN = Gstns;
			this.onAddtnInPRGo();
		},

		CalculationFunAddnlANX2: function (Data, id) {
			var TotalAmounts = [];
			var
				retImpactA10Igst = 0,
				retImpactA10Cgst = 0,
				retImpactA10Sgst = 0,
				retImpactA10Cess = 0,
				retImpactA11Igst = 0,
				retImpactA11Cgst = 0,
				retImpactA11Sgst = 0,
				retImpactA11Cess = 0,
				retImpactB2Igst = 0,
				retImpactB2Cgst = 0,
				retImpactB2Sgst = 0,
				retImpactB2Cess = 0,
				retImpactB3Igst = 0,
				retImpactB3Cgst = 0,
				retImpactB3Sgst = 0,
				retImpactB3Cess = 0,

				anx2Invigst = 0,
				anx2DRigst = 0,
				anx2CRigst = 0,
				anx2Invcgst = 0,
				anx2DRcgst = 0,
				anx2CRcgst = 0,
				anx2Invsgst = 0,
				anx2DRsgst = 0,
				anx2CRsgst = 0,
				anx2Invcess = 0,
				anx2DRcess = 0,
				anx2CRcess = 0,

				prInvigst = 0,
				prDRigst = 0,
				prCRigst = 0,
				prInvcgst = 0,
				prDRcgst = 0,
				prCRcgst = 0,
				prInvsgst = 0,
				prDRsgst = 0,
				prCRsgst = 0,
				prInvcess = 0,
				prDRcess = 0,
				prCRcess = 0,

				prInvAvlIgst = 0,
				prDRAvlIgst = 0,
				prCRAvlIgst = 0,
				prInvAvlCgst = 0,
				prDRAvlCgst = 0,
				prCRAvlCgst = 0,
				prInvAvlSgst = 0,
				prDRAvlSgst = 0,
				prCRAvlSgst = 0,
				prInvAvlCess = 0,
				prDRAvlCess = 0,
				prCRAvlCess = 0;

			for (var i = 0; i < Data.length; i++) {
				// A2 Tax // PR Tax & // PR Avail
				if (Data[i].anx2docType === "INV") {
					anx2Invigst += Data[i].anx2igst;
					anx2Invcgst += Data[i].anx2cgst;
					anx2Invsgst += Data[i].anx2sgst;
					anx2Invcess += Data[i].anx2cess;
					prInvigst += Data[i].prigst;
					prInvcgst += Data[i].prcgst;
					prInvsgst += Data[i].prsgst;
					prInvcess += Data[i].prcess;
					prInvAvlIgst += Data[i].prAvailableIgst;
					prInvAvlCgst += Data[i].prAvailableCgst;
					prInvAvlSgst += Data[i].prAvailableSgst;
					prInvAvlCess += Data[i].prAvailableCess;
				} else if (Data[i].anx2docType === "DR") {
					anx2DRigst += Data[i].anx2igst;
					anx2DRcgst += Data[i].anx2cgst;
					anx2DRsgst += Data[i].anx2sgst;
					anx2DRcess += Data[i].anx2cess;
					prDRigst += Data[i].prigst;
					prDRcgst += Data[i].prcgst;
					prDRsgst += Data[i].prsgst;
					prDRcess += Data[i].prcess;
					prDRAvlIgst += Data[i].prAvailableIgst;
					prDRAvlCgst += Data[i].prAvailableCgst;
					prDRAvlSgst += Data[i].prAvailableSgst;
					prDRAvlCess += Data[i].prAvailableCess;
				} else if (Data[i].anx2docType === "CR") {
					anx2CRigst += Data[i].anx2igst;
					anx2CRcgst += Data[i].anx2cgst;
					anx2CRsgst += Data[i].anx2sgst;
					anx2CRcess += Data[i].anx2cess;
					prCRigst += Data[i].prigst;
					prCRcgst += Data[i].prcgst;
					prCRsgst += Data[i].prsgst;
					prCRcess += Data[i].prcess;
					prCRAvlIgst += Data[i].prAvailableIgst;
					prCRAvlCgst += Data[i].prAvailableCgst;
					prCRAvlSgst += Data[i].prAvailableSgst;
					prCRAvlCess += Data[i].prAvailableCess;
				}

				retImpactA10Igst += Data[i].retImpactA10Igst;
				retImpactA10Cgst += Data[i].retImpactA10Cgst;
				retImpactA10Sgst += Data[i].retImpactA10Sgst;
				retImpactA10Cess += Data[i].retImpactA10Cess;
				retImpactA11Igst += Data[i].retImpactA11Igst;
				retImpactA11Cgst += Data[i].retImpactA11Cgst;
				retImpactA11Sgst += Data[i].retImpactA11Sgst;
				retImpactA11Cess += Data[i].retImpactA11Cess;
				retImpactB2Igst += Data[i].retImpactB2Igst;
				retImpactB2Cgst += Data[i].retImpactB2Cgst;
				retImpactB2Sgst += Data[i].retImpactB2Sgst;
				retImpactB2Cess += Data[i].retImpactB2Cess;
				retImpactB3Igst += Data[i].retImpactB3Igst;
				retImpactB3Cgst += Data[i].retImpactB3Cgst;
				retImpactB3Sgst += Data[i].retImpactB3Sgst;
				retImpactB3Cess += Data[i].retImpactB3Cess;
			}
			//////////////////A2 Tax Total////////////////////
			var A2Igst = Number(anx2Invigst) + Number(anx2DRigst) - Number(anx2CRigst);
			var A2Cgst = Number(anx2Invcgst) + Number(anx2DRcgst) - Number(anx2CRcgst);
			var A2Sgst = Number(anx2Invsgst) + Number(anx2DRsgst) - Number(anx2CRsgst);
			var A2Cess = Number(anx2Invcess) + Number(anx2DRcess) - Number(anx2CRcess);
			var A2TaxTotal = Number(A2Igst) + Number(A2Cgst) + Number(A2Sgst) + Number(A2Cess);

			/////////////////PR Tax Total/////////////////////
			var PRIgst = Number(prInvigst) + Number(prDRigst) - Number(prCRigst);
			var PRCgst = Number(prInvcgst) + Number(prDRcgst) - Number(prCRcgst);
			var PRSgst = Number(prInvsgst) + Number(prDRsgst) - Number(prCRsgst);
			var PRCess = Number(prInvcess) + Number(prDRcess) - Number(prCRcess);
			var PRTaxTotal = Number(PRIgst) + Number(PRCgst) + Number(PRSgst) + Number(PRCess);

			////////////////PR Avail Total////////////////////
			var PRAvlTxIgst = Number(prInvAvlIgst) + Number(prDRAvlIgst) - Number(prCRAvlIgst);
			var PRAvlTxCgst = Number(prInvAvlCgst) + Number(prDRAvlCgst) - Number(prCRAvlCgst);
			var PRAvlTxSgst = Number(prInvAvlSgst) + Number(prDRAvlSgst) - Number(prCRAvlSgst);
			var PRAvlTxCess = Number(prInvAvlCess) + Number(prDRAvlCess) - Number(prCRAvlCess);
			var PRAvailTaxTotal = Number(PRAvlTxIgst) + Number(PRAvlTxCgst) + Number(PRAvlTxSgst) + Number(PRAvlTxCess);

			///////////////RET-1 A10 Tax Total////////////////
			var RA10TaxTotal = Number(retImpactA10Igst) + Number(retImpactA10Cgst) + Number(retImpactA10Sgst) + Number(retImpactA10Cess);

			///////////////RET-1 A11 Tax Total////////////////
			var RA11TaxTotal = Number(retImpactA11Igst) + Number(retImpactA11Cgst) + Number(retImpactA11Sgst) + Number(retImpactA11Cess);

			///////////////RET-1 B2 Tax Total////////////////
			var RB2TaxTotal = Number(retImpactB2Igst) + Number(retImpactB2Cgst) + Number(retImpactB2Sgst) + Number(retImpactB2Cess);

			///////////////RET-1 B3 Tax Total////////////////
			var RB3TaxTotal = Number(retImpactB3Igst) + Number(retImpactB3Cgst) + Number(retImpactB3Sgst) + Number(retImpactB3Cess);

			//A2 Tax - PR Tax//
			var A2TPRTIgst = Number(A2Igst) - Number(PRIgst);
			var A2TPRTCgst = Number(A2Cgst) - Number(PRCgst);
			var A2TPRTSgst = Number(A2Sgst) - Number(PRSgst);
			var A2TPRTCess = Number(A2Cess) - Number(PRCess);

			///PR Tax - PR Aval ////////////////
			var PRTPRAIgst = Number(PRIgst) - Number(PRAvlTxIgst);
			var PRTPRACgst = Number(PRCgst) - Number(PRAvlTxCgst);
			var PRTPRASgst = Number(PRSgst) - Number(PRAvlTxSgst);
			var PRTPRACess = Number(PRCess) - Number(PRAvlTxCess);

			////A2 Tax - PR Aval/////////////////////
			var A2TPRAIgst = Number(A2Igst) - Number(PRAvlTxIgst);
			var A2TPRACgst = Number(A2Cgst) - Number(PRAvlTxCgst);
			var A2TPRASgst = Number(A2Sgst) - Number(PRAvlTxSgst);
			var A2TPRACess = Number(A2Cess) - Number(PRAvlTxCess);

			///////////////////A2 Tax - PR Tax Total////////////////
			var A2TPRTTaxTotal = Number(A2TPRTIgst) + Number(A2TPRTCgst) + Number(A2TPRTSgst) + Number(A2TPRTCess);

			//////////////////PR Tax - PR Aval Tax Total////////////
			var PRTPRATaxTotal = Number(PRTPRAIgst) + Number(PRTPRACgst) + Number(PRTPRASgst) + Number(PRTPRACess);

			//////////////////A2 Tax - PR Aval Tax Total/////////////
			var A2TPRATaxTotal = Number(A2TPRAIgst) + Number(A2TPRACgst) + Number(A2TPRASgst) + Number(A2TPRACess);

			var obj = {
				"anx2igst": A2Igst.toFixed(2), //anx2igst.toFixed(2),
				"anx2cgst": A2Cgst.toFixed(2), //anx2cgst.toFixed(2),
				"anx2sgst": A2Sgst.toFixed(2), //anx2sgst.toFixed(2),
				"anx2cess": A2Cess.toFixed(2), //anx2cess.toFixed(2),
				"prigst": PRIgst.toFixed(2),
				"prcgst": PRCgst.toFixed(2),
				"prsgst": PRSgst.toFixed(2),
				"prcess": PRCess.toFixed(2),
				"prAvailableIgst": PRAvlTxIgst.toFixed(2), //prAvailableIgst.toFixed(2),
				"prAvailableCgst": PRAvlTxCgst.toFixed(2), //prAvailableCgst.toFixed(2),
				"prAvailableSgst": PRAvlTxSgst.toFixed(2), //prAvailableSgst.toFixed(2),
				"prAvailableCess": PRAvlTxCess.toFixed(2), //prAvailableCess.toFixed(2),
				"retImpactA10Igst": retImpactA10Igst.toFixed(2),
				"retImpactA10Cgst": retImpactA10Cgst.toFixed(2),
				"retImpactA10Sgst": retImpactA10Sgst.toFixed(2),
				"retImpactA10Cess": retImpactA10Cess.toFixed(2),
				"retImpactA11Igst": retImpactA11Igst.toFixed(2),
				"retImpactA11Cgst": retImpactA11Cgst.toFixed(2),
				"retImpactA11Sgst": retImpactA11Sgst.toFixed(2),
				"retImpactA11Cess": retImpactA11Cess.toFixed(2),
				"retImpactB2Igst": retImpactB2Igst.toFixed(2),
				"retImpactB2Cgst": retImpactB2Cgst.toFixed(2),
				"retImpactB2Sgst": retImpactB2Sgst.toFixed(2),
				"retImpactB2Cess": retImpactB2Cess.toFixed(2),
				"retImpactB3Igst": retImpactB3Igst.toFixed(2),
				"retImpactB3Cgst": retImpactB3Cgst.toFixed(2),
				"retImpactB3Sgst": retImpactB3Sgst.toFixed(2),
				"retImpactB3Cess": retImpactB3Cess.toFixed(2),
				/////////A2 Tax - PR Tax////////
				"A2TPRTIgst": A2TPRTIgst.toFixed(2),
				"A2TPRTCgst": A2TPRTCgst.toFixed(2),
				"A2TPRTSgst": A2TPRTSgst.toFixed(2),
				"A2TPRTCess": A2TPRTCess.toFixed(2),
				////////PR Tax - PR Aval ///////
				"PRTPRAIgst": PRTPRAIgst.toFixed(2),
				"PRTPRACgst": PRTPRACgst.toFixed(2),
				"PRTPRASgst": PRTPRASgst.toFixed(2),
				"PRTPRACess": PRTPRACess.toFixed(2),
				////////A2 Tax - PR Aval////////
				"A2TPRAIgst": A2TPRAIgst.toFixed(2),
				"A2TPRACgst": A2TPRACgst.toFixed(2),
				"A2TPRASgst": A2TPRASgst.toFixed(2),
				"A2TPRACess": A2TPRACess.toFixed(2),

				/////////////////Totals////////
				"A2TaxTotal": A2TaxTotal.toFixed(2),
				"PRTaxTotal": PRTaxTotal.toFixed(2),
				"PRAvailTaxTotal": PRAvailTaxTotal.toFixed(2),
				"RA10TaxTotal": RA10TaxTotal.toFixed(2),
				"RA11TaxTotal": RA11TaxTotal.toFixed(2),
				"RB2TaxTotal": RB2TaxTotal.toFixed(2),
				"RB3TaxTotal": RB3TaxTotal.toFixed(2),
				"A2TPRTTaxTotal": A2TPRTTaxTotal.toFixed(2),
				"PRTPRATaxTotal": PRTPRATaxTotal.toFixed(2),
				"A2TPRATaxTotal": A2TPRATaxTotal.toFixed(2)
			};
			TotalAmounts.push(obj);
			var oRRConsldMdl = new JSONModel();
			oRRConsldMdl.setData(TotalAmounts[0]);
			this.getView().byId(id).setModel(oRRConsldMdl, "TotalAmounts");
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on Go Button in Recon Summary(Vendor Summary)
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onReconSummaryTabBind: function (oEvt) {
			if (oEvt === undefined) {
				this.onSelectGSTINs();
				this.RecSummDate = undefined;
			} else if (oEvt !== undefined && this.RecSummDate !== undefined) {
				this.RecSummDate = undefined;
				this.onSelectGSTINs();
			}

			/*if (this.VenRecSummPan !== undefined) {
				this.getView().byId("ReconSumPanId").setSelectedKeys(this.VenRecSummPan);
			}
			if (this.VenRecSummGstin !== undefined) {
				this.getView().byId("ReconSummGstnId").setSelectedKeys(this.VenRecSummGstin);
			}*/

			var onReconSummaryTabData = {
				req: {
					"entityId": $.sap.entityID.toString(),
					"taxPeriod": this.getView().byId("ReconSummDateId").getValue(),
					"gstins": this.getView().byId("ReconSummGSTNId").getSelectedKeys(),
					"vendorPan": this.getView().byId("ReconSumPanId").getSelectedKeys(),
					"vendorGstin": this.getView().byId("ReconSummGstnId").getSelectedKeys(),
					"data": []
				}
			};
			this.onVenReconTabBind(onReconSummaryTabData);
		},

		onVenReconTabBind: function (onReconSummaryTabData) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getAnx2VendorReconSummary.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(onReconSummaryTabData)
				}).done(function (data, status, jqXHR) {
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					for (var i = 0; i < data.resp.length; i++) {
						var ele = data.resp[i];
						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							delete ele.vendorPan;
							delete ele.rating;
							delete ele.docType;
							delete ele.vendorName;
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							delete ele.vendorPan;
							delete ele.rating;
							delete ele.gstin;
							delete ele.vendorName;
							curL2Obj.level3.push(ele);
						}
					}
					oRRConsldMdl.setData(retArr);
					that.byId("idVendorSummaryTable3").setModel(oRRConsldMdl, "onReconSummaryTabBind");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		handleChangeRecSummDate: function (oEvt) {
			this.RecSummDate = oEvt;
			//this.VenRecSummPan = [];
			//this.VenRecSummGstin = [];
		},

		handleChangeRecRespDate: function (oEvt) {
			this.RecRespDate = oEvt;
			//this.VenRecRespPan = [];
			//this.VenRecRespGstin = [];
		},

		onClearFilterRecsummVS: function () {
			var vDate = new Date();
			this.byId("ReconSummDateId").setMaxDate(vDate);
			this.byId("ReconSummDateId").setDateValue(vDate);
			this.byId("ReconSummGSTNId").setSelectedKeys([]);
			this.byId("ReconSumPanId").setSelectedKeys([]);
			this.byId("ReconSummGstnId").setSelectedKeys([]);
			this.onReconSummaryTabBind();
		},

		/**
		 * Added by Vinay Kodam
		 * Called when user click on Go Button in Recon Response(Vendor Summary)
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		onRecRespGO: function (oEvt) {
			if (oEvt === undefined) {
				this.onRecRespGSTIN();
				this.RecRespDate = undefined;
			} else if (oEvt !== undefined && this.RecRespDate !== undefined) {
				this.RecRespDate = undefined;
				this.onRecRespGSTIN();
			}

			/*if (this.VenRecRespPan !== undefined) {
				this.getView().byId("ReconRespVendorPANId").setSelectedKeys(this.VenRecRespPan);
			}
			if (this.VenRecRespGstin !== undefined) {
				this.getView().byId("RecRespVenGSTINID").setSelectedKeys(this.VenRecRespGstin);
			}*/

			var onReconRespTabData = {
				req: {
					"entityId": $.sap.entityID.toString(),
					"taxPeriod": this.getView().byId("ReconRespDateId").getValue(),
					"gstins": this.getView().byId("ReconRespGSTNId").getSelectedKeys(),
					"vendorPan": this.getView().byId("ReconRespVendorPANId").getSelectedKeys(),
					"vendorGstin": this.getView().byId("RecRespVenGSTINID").getSelectedKeys(),
					"data": []
				}
			};
			this.onVenReconRespTabBind(onReconRespTabData);
		},

		onVenReconRespTabBind: function (onReconRespTabData) {
			var that = this;
			var oRRConsldMdl = new JSONModel();
			var oRRConsldPath = "/aspsapapi/getAnx2VendorReconResponse.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRRConsldPath,
					contentType: "application/json",
					data: JSON.stringify(onReconRespTabData)
				}).done(function (data, status, jqXHR) {
					var retArr = [];
					var curL1Obj = {}; // the current level1 object.
					var curL2Obj = {}; // the current level2 object.
					for (var i = 0; i < data.resp.length; i++) {
						var ele = data.resp[i];
						var lvl = ele.level; // Get the level of the cur Obj.
						if (lvl === "L1") {
							curL1Obj = ele;
							retArr.push(curL1Obj);
							curL1Obj.level2 = [];
						}
						if (lvl === "L2") {
							delete ele.vendorPan;
							delete ele.rating;
							delete ele.docType;
							delete ele.vendorName;
							curL2Obj = ele;
							curL1Obj.level2.push(curL2Obj);
							curL2Obj.level3 = [];
						}
						if (lvl === "L3") {
							delete ele.vendorPan;
							delete ele.rating;
							delete ele.gstin;
							delete ele.vendorName;
							curL2Obj.level3.push(ele);
						}
					}
					oRRConsldMdl.setData(retArr);
					that.byId("idVendorSummaryTable4").setModel(oRRConsldMdl, "onReconRespTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onClearFilterRecRespVS: function () {
			var vDate = new Date();
			this.byId("ReconRespDateId").setMaxDate(vDate);
			this.byId("ReconRespDateId").setDateValue(vDate);
			this.byId("ReconRespGSTNId").setSelectedKeys([]);
			this.byId("ReconRespVendorPANId").setSelectedKeys([]);
			this.byId("RecRespVenGSTINID").setSelectedKeys([]);
			this.onRecRespGO();
		},

		onVenRSPanChange: function (evt) {
			//this.VenRecSummPan = evt.getSource().getSelectedKeys();
			var RespnseSumGstinsModel = new JSONModel();
			var RespnseSumGstinsView = this.getView();
			var arr = [],
				obj = {};
			var selItems = evt.getSource().getSelectedItems(); //[0].getText();
			var cgstins = this.data;
			if (selItems.length !== 0) {
				for (var k = 0; k < selItems.length; k++) {
					for (var i = 0; i < cgstins.length; i++) {
						var a = cgstins[i].gstin;
						var cgstins1 = a.slice(2, 12);
						if (selItems[k].getText() === cgstins1) {
							obj = cgstins[i];
							arr.push(obj);
						}
					}
				}
				RespnseSumGstinsModel.setData(arr);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "VendorDetalils1");
			} else {
				RespnseSumGstinsModel.setData(this.newArray1);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "VendorDetalils1");
			}
		},

		onVenRSGstinChange: function (evt) {
			this.VenRecSummGstin = evt.getSource().getSelectedKeys();
		},

		onVenReRsGstinChange: function (evt) {
			//	this.VenRecRespGstin = evt.getSource().getSelectedKeys();
			this.VenRecRespGstin = this.byId("RecRespVenGSTINID").getSelectedKeys();
		},

		onVenReRsPanChange: function (evt) {
			this.VenRecRespPan = evt.getSource().getSelectedKeys();
			var RespnseSumGstinsModel = new JSONModel();
			var RespnseSumGstinsView = this.getView();
			var arr = [],
				obj = {};
			var selItems = evt.getSource().getSelectedItems(); //[0].getText();
			var cgstins = this.data1;
			if (selItems.length !== 0) {
				for (var k = 0; k < selItems.length; k++) {
					for (var i = 0; i < cgstins.length; i++) {
						var a = cgstins[i].gstin;
						var cgstins1 = a.slice(2, 12);
						if (selItems[k].getText() === cgstins1) {
							obj = cgstins[i];
							arr.push(obj);
						}
					}
				}
				RespnseSumGstinsModel.setData(arr);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "VendorDetalilsRecResp1");
			} else {
				RespnseSumGstinsModel.setData(this.newArray1);
				RespnseSumGstinsView.setModel(RespnseSumGstinsModel, "VendorDetalilsRecResp1");
			}
		},

		onPrsAddtnANX2ActionClrErlr: function (oEvent) {
			var that = this;
			var oTablIndxA2 = this.byId("AddtnANX2TabId").getSelectedIndices();
			if (oTablIndxA2.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}
			var oActionData = oEvent.getSource().getText();
			var oMessage = "Do you want to " + oEvent.getSource().getText() + " ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.onPrsAddtnANX2ActionClrErlr1(oActionData);
					}
				}
			});
		},

		onPrsAddtnANX2ActionClrErlr1: function (oActionData) {
			var oTablIndxA2 = this.byId("AddtnANX2TabId").getSelectedIndices();
			var oTablDataA2 = this.byId("AddtnANX2TabId").getModel("AddtnANX2TabData").getData();
			for (var i = 0; i < oTablIndxA2.length; i++) {
				oTablDataA2[oTablIndxA2[i]].userAction = " ";
			}
			this.byId("AddtnANX2TabId").getModel("AddtnANX2TabData").refresh(true);
			this.byId("AddtnANX2TabId").setSelectedIndex(-1);
		},

		onPrsAddtnPRActionClrErlr: function (oEvent) {
			var that = this;
			var oTablIndxPR = this.byId("AddtnPRTabId").getSelectedIndices();
			if (oTablIndxPR.length === 0) {
				MessageBox.information("Please Select at least one record");
				return;
			}
			var oActionDataPR = oEvent.getSource().getText();
			var oMessage = "Do you want to " + oEvent.getSource().getText() + " ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.onPrsAddtnPRActionClrErlr11(oActionDataPR);
					}
				}
			});
		},

		onPrsAddtnPRActionClrErlr11: function (oActionDataPR) {
			var oTablIndxPR = this.byId("AddtnPRTabId").getSelectedIndices();
			var oTablDataPR = this.byId("AddtnPRTabId").getModel("AddtnPRTabData").getData();
			for (var i = 0; i < oTablIndxPR.length; i++) {
				oTablDataPR[oTablIndxPR[i]].userAction = " ";
			}
			this.byId("AddtnPRTabId").getModel("AddtnPRTabData").refresh(true);
			this.byId("AddtnPRTabId").setSelectedIndex(-1);
		},
		onSelectCheckBoxsaveCol1t: function (oEvent) {

			var oVisiModel = this.getView().getModel("showing"),
				oVisiData = oVisiModel.getData();

			if (oVisiData.count && !oVisiData.taxValue && !oVisiData.TotalTax) {
				oVisiData.enablecount = true;

			} else if (!oVisiData.count && oVisiData.taxValue && !oVisiData.TotalTax) {
				oVisiData.enabletaxValue = true;

			} else if (!oVisiData.count && !oVisiData.taxValue && oVisiData.TotalTax) {
				oVisiData.enableTotalTax = true;

			} else {
				oVisiData.enablecount = false;
				oVisiData.enabletaxValue = false;
				oVisiData.enableTotalTax = false;
			}
			oVisiModel.refresh(true);

		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.ANX2
		 */
		//	onExit: function() {
		//
		//	}

	});

});