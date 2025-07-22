sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/json/JSONModel"
], function (MobileLibrary, MessageBox, MessageToast, JSONModel) {
	"use strict";

	return {

		/*====================================================================================*/
		/*==================== Start Recon Results ===========================================*/
		/*====================================================================================*/
		onSelectionChangeReconResultMain: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "ReconReport") {
				that.oView.byId("idReconResultTableCC1").setVisible(true);
				that.oView.byId("idReconResultTableCC2").setVisible(false);
				that.oView.byId("idReconResultTableCC3").setVisible(false);
				that.oView.byId("idReconResultTableCC4").setVisible(false);
				that.oView.byId("idReconResultTableCC5").setVisible(false);
				that.oView.byId("idReconResultTableCC6").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "AbsoluteMatched") {
				that.oView.byId("idReconResultTableCC1").setVisible(false);
				that.oView.byId("idReconResultTableCC2").setVisible(true);
				that.oView.byId("idReconResultTableCC3").setVisible(false);
				that.oView.byId("idReconResultTableCC4").setVisible(false);
				that.oView.byId("idReconResultTableCC5").setVisible(false);
				that.oView.byId("idReconResultTableCC6").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "Mismatch") {
				that.oView.byId("idReconResultTableCC1").setVisible(false);
				that.oView.byId("idReconResultTableCC2").setVisible(false);
				that.oView.byId("idReconResultTableCC3").setVisible(true);
				that.oView.byId("idReconResultTableCC4").setVisible(false);
				that.oView.byId("idReconResultTableCC5").setVisible(false);
				that.oView.byId("idReconResultTableCC6").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "PotentailMatch") {
				that.oView.byId("idReconResultTableCC1").setVisible(false);
				that.oView.byId("idReconResultTableCC2").setVisible(false);
				that.oView.byId("idReconResultTableCC3").setVisible(false);
				that.oView.byId("idReconResultTableCC4").setVisible(true);
				that.oView.byId("idReconResultTableCC5").setVisible(false);
				that.oView.byId("idReconResultTableCC6").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "Additional2A") {
				that.oView.byId("idReconResultTableCC1").setVisible(false);
				that.oView.byId("idReconResultTableCC2").setVisible(false);
				that.oView.byId("idReconResultTableCC3").setVisible(false);
				that.oView.byId("idReconResultTableCC4").setVisible(false);
				that.oView.byId("idReconResultTableCC5").setVisible(true);
				that.oView.byId("idReconResultTableCC6").setVisible(false);
			} else if (oEvent.getSource().getSelectedKey() === "AdditionalPR") {
				that.oView.byId("idReconResultTableCC1").setVisible(false);
				that.oView.byId("idReconResultTableCC2").setVisible(false);
				that.oView.byId("idReconResultTableCC3").setVisible(false);
				that.oView.byId("idReconResultTableCC4").setVisible(false);
				that.oView.byId("idReconResultTableCC5").setVisible(false);
				that.oView.byId("idReconResultTableCC6").setVisible(true);
			}
		},

		onSelectionChangeSDTab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idABSummary").setVisible(true);
				that.oView.byId("idActionButCheckBox1").setVisible(true);
				that.oView.byId("idActionButCheckBox2").setVisible(true);

				that.oView.byId("idABDetail").setVisible(false);
				that.oView.byId("idActionBut1").setVisible(false);
				that.oView.byId("idActionBut2").setVisible(false);
				that.oView.byId("idActionBut3").setVisible(false);
				that.oView.byId("idActionBut4").setVisible(false);
				that.oView.byId("idActionBut5").setVisible(false);
				that.oView.byId("idActionBut6").setVisible(false);
				that.oView.byId("idActionBut7").setVisible(false);
				that.oView.byId("idActionBut8").setVisible(false);
				that.oView.byId("idActionBut9").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idABSummary").setVisible(false);
				that.oView.byId("idActionButCheckBox1").setVisible(false);
				that.oView.byId("idActionButCheckBox2").setVisible(false);

				that.oView.byId("idABDetail").setVisible(true);
				that.oView.byId("idActionBut1").setVisible(true);
				that.oView.byId("idActionBut2").setVisible(true);
				that.oView.byId("idActionBut3").setVisible(true);
				that.oView.byId("idActionBut4").setVisible(true);
				that.oView.byId("idActionBut5").setVisible(true);
				that.oView.byId("idActionBut6").setVisible(true);
				that.oView.byId("idActionBut7").setVisible(true);
				that.oView.byId("idActionBut8").setVisible(true);
				that.oView.byId("idActionBut9").setVisible(true);

			}
		},

		onSelectionChangeSDGDMTab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idGDMSummary").setVisible(true);
				that.oView.byId("idGDMDetail").setVisible(false);
				that.oView.byId("idGDMActionBut1").setVisible(false);
				that.oView.byId("idGDMActionBut2").setVisible(false);
				that.oView.byId("idGDMActionBut3").setVisible(false);
				that.oView.byId("idGDMActionBut4").setVisible(false);
				that.oView.byId("idGDMActionBut5").setVisible(false);
				that.oView.byId("idGDMActionBut6").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idGDMSummary").setVisible(false);
				that.oView.byId("idGDMDetail").setVisible(true);
				that.oView.byId("idGDMActionBut1").setVisible(true);
				that.oView.byId("idGDMActionBut2").setVisible(true);
				that.oView.byId("idGDMActionBut3").setVisible(true);
				that.oView.byId("idGDMActionBut4").setVisible(true);
				that.oView.byId("idGDMActionBut5").setVisible(true);
				that.oView.byId("idGDMActionBut6").setVisible(true);

			}
		},

		onSelectionChangeSDPMTab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idPMSummary").setVisible(true);
				that.oView.byId("idPMDetail").setVisible(false);
				that.oView.byId("idPMActionBut1").setVisible(false);
				that.oView.byId("idPMActionBut2").setVisible(false);
				that.oView.byId("idPMActionBut3").setVisible(false);
				that.oView.byId("idPMActionBut4").setVisible(false);
				that.oView.byId("idPMActionBut5").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idPMSummary").setVisible(false);
				that.oView.byId("idPMDetail").setVisible(true);
				that.oView.byId("idPMActionBut1").setVisible(true);
				that.oView.byId("idPMActionBut2").setVisible(true);
				that.oView.byId("idPMActionBut3").setVisible(true);
				that.oView.byId("idPMActionBut4").setVisible(true);
				that.oView.byId("idPMActionBut5").setVisible(true);

			}
		},

		onSelectionChangeSDA2ATab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idA2ASummary").setVisible(true);
				that.oView.byId("idA2ADetail").setVisible(false);
				that.oView.byId("idA2AActionBut1").setVisible(false);
				that.oView.byId("idA2AActionBut2").setVisible(false);
				that.oView.byId("idA2AActionBut3").setVisible(false);
				that.oView.byId("idA2AActionBut4").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idA2ASummary").setVisible(false);
				that.oView.byId("idA2ADetail").setVisible(true);
				that.oView.byId("idA2AActionBut1").setVisible(true);
				that.oView.byId("idA2AActionBut2").setVisible(true);
				that.oView.byId("idA2AActionBut3").setVisible(true);
				that.oView.byId("idA2AActionBut4").setVisible(true);

			}
		},

		onSelectionChangeSDAPRTab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idAPRSummary").setVisible(true);
				that.oView.byId("idAPRDetail").setVisible(false);
				that.oView.byId("idAPRActionBut1").setVisible(false);
				that.oView.byId("idAPRActionBut2").setVisible(false);
				that.oView.byId("idAPRActionBut3").setVisible(false);
				that.oView.byId("idAPRActionBut4").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idAPRSummary").setVisible(false);
				that.oView.byId("idAPRDetail").setVisible(true);
				that.oView.byId("idAPRActionBut1").setVisible(true);
				that.oView.byId("idAPRActionBut2").setVisible(true);
				that.oView.byId("idAPRActionBut3").setVisible(true);
				that.oView.byId("idAPRActionBut4").setVisible(true);

			}
		},

		onSelectionChangeSDRRTab: function (oEvent, that) {
			if (oEvent.getSource().getSelectedKey() === "Summary") {
				that.oView.byId("idRRSummary").setVisible(true);
				that.oView.byId("idRRDetail").setVisible(false);

				that.oView.byId("idRRActionBut1").setVisible(true);
				that.oView.byId("idRRActionBut2").setVisible(true);
				that.oView.byId("idRRActionBut3").setVisible(true);
				that.oView.byId("idRRActionBut4").setVisible(false);

			} else if (oEvent.getSource().getSelectedKey() === "Detail") {
				that.oView.byId("idRRSummary").setVisible(false);
				that.oView.byId("idRRDetail").setVisible(true);

				that.oView.byId("idRRActionBut1").setVisible(false);
				that.oView.byId("idRRActionBut2").setVisible(false);
				that.oView.byId("idRRActionBut3").setVisible(false);
				that.oView.byId("idRRActionBut4").setVisible(true);
			}
		},

		onSelectCheckBoxCol1RR: function (oEvent) {
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("idABTableCol1").setVisible(true);
				this.oView.byId("idABTableCol2").setVisible(true);
				this.oView.byId("idABTableCol3").setVisible(true);
				this.oView.byId("idABTableCol4").setVisible(true);
				this.oView.byId("idABTableCol5").setVisible(true);
			} else {
				this.oView.byId("idABTableCol1").setVisible(false);
				this.oView.byId("idABTableCol2").setVisible(false);
				this.oView.byId("idABTableCol3").setVisible(false);
				this.oView.byId("idABTableCol4").setVisible(false);
				this.oView.byId("idABTableCol5").setVisible(false);
			}
		},

		onSelectCheckBoxCol2RR: function (oEvent) {
			if (oEvent.getParameters().selected === true) {
				this.oView.byId("idABTableCol6").setVisible(true);
				this.oView.byId("idABTableCol7").setVisible(true);
				this.oView.byId("idABTableCol8").setVisible(true);
				this.oView.byId("idABTableCol9").setVisible(true);
				this.oView.byId("idABTableCol10").setVisible(true);
			} else {
				this.oView.byId("idABTableCol6").setVisible(false);
				this.oView.byId("idABTableCol7").setVisible(false);
				this.oView.byId("idABTableCol8").setVisible(false);
				this.oView.byId("idABTableCol9").setVisible(false);
				this.oView.byId("idABTableCol10").setVisible(false);
			}
		},

		onPressAdditionalReconReportsLink: function () {
			this.oView.byId("iditbAnnexure2").setSelectedKey("AdditionalReconResults");
		},

		onSelectCheckBoxCol1t: function (oEvent, that) {
			if (oEvent.getParameters().selected === true) {
				that.oView.byId("idPREntityTableCol6t").setVisible(true);
				that.oView.byId("idPREntityTableCol7t").setVisible(true);
				that.oView.byId("idPREntityTableCol8t").setVisible(true);
				that.oView.byId("idPREntityTableCol9t").setVisible(true);
				that.oView.byId("idPREntityTableCol10t").setVisible(true);

				that.oView.byId("idPREntityTableCol6td").setVisible(true);
				that.oView.byId("idPREntityTableCol7td").setVisible(true);
				that.oView.byId("idPREntityTableCol8td").setVisible(true);
				that.oView.byId("idPREntityTableCol9td").setVisible(true);
				that.oView.byId("idPREntityTableCol10td").setVisible(true);
			} else {
				that.oView.byId("idPREntityTableCol6t").setVisible(false);
				that.oView.byId("idPREntityTableCol7t").setVisible(false);
				that.oView.byId("idPREntityTableCol8t").setVisible(false);
				that.oView.byId("idPREntityTableCol9t").setVisible(false);
				that.oView.byId("idPREntityTableCol10t").setVisible(false);

				that.oView.byId("idPREntityTableCol6td").setVisible(false);
				that.oView.byId("idPREntityTableCol7td").setVisible(false);
				that.oView.byId("idPREntityTableCol8td").setVisible(false);
				that.oView.byId("idPREntityTableCol9td").setVisible(false);
				that.oView.byId("idPREntityTableCol10td").setVisible(false);
			}
		},

		onSelectCheckBoxCol2t: function (oEvent, that) {
			if (oEvent.getParameters().selected === true) {
				that.oView.byId("idPREntityTableCol11t").setVisible(true);
				that.oView.byId("idPREntityTableCol12t").setVisible(true);
				that.oView.byId("idPREntityTableCol13t").setVisible(true);
				that.oView.byId("idPREntityTableCol14t").setVisible(true);
				that.oView.byId("idPREntityTableCol15t").setVisible(true);

				that.oView.byId("idPREntityTableCol11td").setVisible(true);
				that.oView.byId("idPREntityTableCol12td").setVisible(true);
				that.oView.byId("idPREntityTableCol13td").setVisible(true);
				that.oView.byId("idPREntityTableCol14td").setVisible(true);
				that.oView.byId("idPREntityTableCol15td").setVisible(true);
			} else {
				that.oView.byId("idPREntityTableCol11t").setVisible(false);
				that.oView.byId("idPREntityTableCol12t").setVisible(false);
				that.oView.byId("idPREntityTableCol13t").setVisible(false);
				that.oView.byId("idPREntityTableCol14t").setVisible(false);
				that.oView.byId("idPREntityTableCol15t").setVisible(false);

				that.oView.byId("idPREntityTableCol11td").setVisible(false);
				that.oView.byId("idPREntityTableCol12td").setVisible(false);
				that.oView.byId("idPREntityTableCol13td").setVisible(false);
				that.oView.byId("idPREntityTableCol14td").setVisible(false);
				that.oView.byId("idPREntityTableCol15td").setVisible(false);
			}
		},

		onSelectCheckBoxCol1: function (oEvent, that) {
			if (oEvent.getParameters().selected === true) {
				that.oView.byId("idPREntityTableCol6").setVisible(true);
				that.oView.byId("idPREntityTableCol7").setVisible(true);
				that.oView.byId("idPREntityTableCol8").setVisible(true);
				that.oView.byId("idPREntityTableCol9").setVisible(true);
				that.oView.byId("idPREntityTableCol10").setVisible(true);
			} else {
				that.oView.byId("idPREntityTableCol6").setVisible(false);
				that.oView.byId("idPREntityTableCol7").setVisible(false);
				that.oView.byId("idPREntityTableCol8").setVisible(false);
				that.oView.byId("idPREntityTableCol9").setVisible(false);
				that.oView.byId("idPREntityTableCol10").setVisible(false);
			}
		},

		onSelectCheckBoxCol2: function (oEvent, that) {
			if (oEvent.getParameters().selected === true) {
				that.oView.byId("idPREntityTableCol11").setVisible(true);
				that.oView.byId("idPREntityTableCol12").setVisible(true);
				that.oView.byId("idPREntityTableCol13").setVisible(true);
				that.oView.byId("idPREntityTableCol14").setVisible(true);
				that.oView.byId("idPREntityTableCol15").setVisible(true);
			} else {
				that.oView.byId("idPREntityTableCol11").setVisible(false);
				that.oView.byId("idPREntityTableCol12").setVisible(false);
				that.oView.byId("idPREntityTableCol13").setVisible(false);
				that.oView.byId("idPREntityTableCol14").setVisible(false);
				that.oView.byId("idPREntityTableCol15").setVisible(false);
			}
		},

		onInitiateRecon: function (oEvent, that) {

			var oView = that.getView();
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
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstin": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/initiateRecon.do";
			sap.ui.core.BusyIndicator.show();
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

		onAbsoluteMatchDetails: function (oEvent, that) {
		
			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oReconResultsSummary = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oReconResultsSummary, "ReconResultsSummary");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstins": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/getAbsoluteMatchDetails.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oReconResult = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oReconResult, "ReconResult");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAbsoluteMatchDetails : Error");
				});
			});
		},

		onReconResultsSummary: function (oEvent, that) {

			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oReconResultsSummary = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oReconResultsSummary, "ReconResultsSummary");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstin": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var ReconResultsSummary = "/aspsapapi/getAbsoluteMatchSummary.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ReconResultsSummary,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oReconResultsSummary = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oReconResultsSummary, "ReconResultsSummary");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getAbsoluteMatchSummary : Error");
				});
			});
		},

		onReconResultMismatch: function (oEvent, that) {

			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oMismatchDetails = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oMismatchDetails, "MismatchDetails");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstins": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var MismatchDetails = "/aspsapapi/getMismatchDetails.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: MismatchDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oMismatchDetails = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oMismatchDetails, "MismatchDetails");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getMismatchDetails : Error");
				});
			});
		},

		onMisMatchSummary: function (oEvent, that) {

			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oMisMatchSummary = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oMisMatchSummary, "MisMatchSummary");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstin": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var MismatchDetails = "/aspsapapi/getMisMatchSummary.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: MismatchDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oMisMatchSummary = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oMisMatchSummary, "MisMatchSummary");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getMisMatchSummary : Error");
				});
			});
		},

		onPotentialMatchSummary: function (oEvent, that) {

			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oPotentialMatchSummary = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oPotentialMatchSummary, "PotentialMatchSummary");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstin": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var MismatchDetails = "/aspsapapi/getPotentialMatchSummary.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: MismatchDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oPotentialMatchSummary = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oPotentialMatchSummary, "PotentialMatchSummary");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getPotentialMatchSummary : Error");
				});
			});
		},
		onPotentialMatchDetails: function (oEvent, that) {

			var oView = that.getView();
			var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTINRR").getData();
			var oRetPeriod = oView.byId("idRRPeriod").getValue();
			if (oRetPeriod === "") {
				oRetPeriod = null;
			}
			oPath = oView.byId("idReconResultList").getSelectedContextPaths();
			if (oPath.length === 0) {
				var oPotentialMatchDetails = new sap.ui.model.json.JSONModel(null);
				oView.setModel(oPotentialMatchDetails, "PotentialMatchDetails");
				return;
			}
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[2];
				aGSTIN.push(oModelData.resp[j].cgstin);
			}
			var postData = {
				"req": {
					"gstins": aGSTIN,
					"returnPeriod": oRetPeriod
				}
			};
			var jsonForSearch = JSON.stringify(postData);
			var MismatchDetails = "/aspsapapi/getPotentialMatchDetails.do";
			sap.ui.core.BusyIndicator.show();
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: MismatchDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oPotentialMatchDetails = new sap.ui.model.json.JSONModel(data);
					oView.setModel(oPotentialMatchDetails, "PotentialMatchDetails");
					sap.ui.core.BusyIndicator.hide();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getPotentialMatchDetails : Error");
				});
			});
		},

	};
});