sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, Controller, formatter, JSONModel, MessageBox, MessageToast, Button, Dialog) {

	"use strict";
	return BaseController.extend("com.ey.digigst.controller.GSTR9", {
		formatter: formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR9
		 */
		onInit: function () {
			this._bindScreenProperty();
			this._VisibleColumns();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				var oDataType = this.getOwnerComponent().getModel("Returns").getData();
				this.getView().setModel(new JSONModel(oDataType), "oGSTR9Model");
				var oRateOfTax = this.getOwnerComponent().getModel("DropDown").getData().RateofTax;
				this.getView().setModel(new JSONModel(oRateOfTax), "oRateofTaxModel");
				var oReqWiseModel = new JSONModel();
				var oUqc = this.getOwnerComponent().getModel("DropDown").getData().UQC;
				oReqWiseModel.setSizeLimit(oUqc.length);
				oReqWiseModel.setData(oUqc);
				this.getView().setModel(oReqWiseModel, "oUqcModel");
				this.onLoadFy();
				this.onPressBackGlSummary();
			}
		},

		//=============== Fy Model Binding ===========================//
		onLoadFy: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getAllFy.do",
					contentType: "application/json",
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp), "oFyModel");
						this.getView().byId("gstr9FY").setSelectedKey(data.resp.finYears[0].fy);
						this.EntityTabBind();
					} else {
						sap.ui.core.BusyIndicator.hide();
						this.getView().setModel(new JSONModel([]), "oFyModel");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "oFyModel");
				}.bind(this));
		},

		//=============== Expand/collide Table view ==================//		
		_bindScreenProperty: function () {
			var oPrProperty = {
				"InFullScreen": false,
				"OutFullScreen": false,
				"TaxFullScreen": false,
				"DiffFullScreen": false,
				"PYFullScreen": false,
				"DemullScreen": false,
				"CompFullScreen": false,
				"HSNFullScreen": false
			};
			this.getView().setModel(new JSONModel(oPrProperty), "PrProperty");
		},

		//=================================================================================//
		//======================= Added by chaithra on 28/4/2021 ==========================//
		//======================= start of Entity level screen ============================//
		onLoadGSTINlist: function () {
			var PayLoad = {
				"entityId": parseInt($.sap.entityID)
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getGstnbyEntityId.do",
					contentType: "application/json",
					data: PayLoad
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oData = $.extend(true, {}, data);
					data.resp.gstindata.unshift({
						"gstin": "All"
					});
					this.getView().setModel(new JSONModel(data), "oGSTINList");
					this.getView().setModel(new JSONModel(data), "oGSTINList1");
					this.getView().getModel("oGSTINList").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		// onLoadGSTINlist1: function () {
		// 	var that = this;
		// 	var PayLoad = {
		// 		"entityId": parseInt($.sap.entityID)
		// 	};
		// 	var Url = "/aspsapapi/getGstnbyEntityId.do";
		// 	sap.ui.core.BusyIndicator.show(0);
		// 	$(document).ready(function ($) {
		// 		$.ajax({
		// 			method: "GET",
		// 			url: Url,
		// 			contentType: "application/json",
		// 			data: PayLoad
		// 		}).done(function (data, status, jqXHR) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 			that.getView().setModel(new JSONModel(data), "oGSTINList1");
		// 			that.getView().getModel("oGSTINList").refresh(true);

		// 		}).fail(function (jqXHR, status, err) {
		// 			sap.ui.core.BusyIndicator.hide();
		// 		});
		// 	});
		// },

		EntityTabBind: function () {
			var allGstin = this.getView().getModel("userPermission").getProperty("/respData/dataSecurity/gstin"),
				aGstin = this.byId("GSTINEntityID").getSelectedKeys();
			if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				var gstin = allGstin.map(function (e) {
					return e.value;
				});
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			if (!this.getView().byId("gstr9FY").getSelectedKey()) {
				return;
			}
			var PayLoad = {
				"req": {
					"listGstins": aGstin.length === 0 ? gstin : aGstin,
					"fy": this.getView().byId("gstr9FY").getSelectedKey(),
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr9Summary.do",
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "SummTab");
					this.getView().getModel("SummTab").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		//==================================================================================//
		//========================= Ended by chaithra on 28/4/2021 =========================//
		//========================= end of entity level screen =============================//
		//=================== Tab button press handle ==============================//
		_confirmDataLoss: function (oButton, vChanged) {
			MessageBox.confirm("Do you want to continue without saving data?", {
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.YES) {
						vChanged = false;
						this.onChangeButton(oButton, vChanged);
					}
				}.bind(this)
			});
		},

		fnMenuItemPress: function (oEvent) {
			var oData = this.getView().getModel("oVisiTab").getProperty("/"),
				aDataKeys = ["Out", "In", "IRev", "Tax", "PY", "Diff", "DemR", "Comp", "OHsn", "IHsn"],
				oButton = oEvent.getSource(),
				aChangeFlags = [
					"OutChanged", "Inchanged", "ORchanged", "TaxpChanged", "PYchanged", "Diffchanged",
					"DeRefchanged", "CompDeemedchanged", "HsnOutchanged", "HsnInchanged"
				],
				vChanged = false;

			aDataKeys.forEach(function (f, i) {
				if (oData[f] && this[aChangeFlags[i]]) {
					vChanged = true;
					this._confirmDataLoss(oButton, vChanged);
				}
			}.bind(this));

			if (!vChanged) {
				this.onChangeButton(oButton, vChanged);
			}
		},

		onChangeButton: function (oButton, vChanged) {
			var oData = this.getView().getModel("oVisiTab").getProperty("/"),
				aField = [
					"idOutward", "idInward", "idTaxPaid", "idPYTransaction", "idDiffTax", "idDemandRef", "idComp", "idHSN"
				];

			for (var key in oData) {
				if (oData.hasOwnProperty(key) && key !== "Footer") {
					oData[key] = false;
				}
			}
			oData.Footer = true;
			aField.forEach(function (f) {
				this.getView().byId(f).removeStyleClass("HomeCSS");
				this.getView().byId(f).removeStyleClass("btnClr3");
			}, this);

			if (oButton.getId().indexOf("idOutward") > -1) {
				this.getView().byId("idOutward").addStyleClass("HomeCSS");
				this.getView().byId("idOutward").addStyleClass("btnClr3");

				oData.Out = true;
				this.OutwardTabBind();
			} else if (oButton.getId().indexOf("idInward") > -1) {
				this.getView().byId("idInward").addStyleClass("HomeCSS");
				this.getView().byId("idInward").addStyleClass("btnClr3");

				oData.In = true;
				this.loadInwardData();
			} else if (oButton.getId().indexOf("idTaxPaid") > -1) {
				oData.Tax = true;
				this.getView().byId("idTaxPaid").addStyleClass("HomeCSS");
				this.getView().byId("idTaxPaid").addStyleClass("btnClr3");

				this.TaxPaidTabBind();
			} else if (oButton.getId().indexOf("idPYTransaction") > -1) {
				oData.PY = true;
				this.getView().byId("idPYTransaction").addStyleClass("HomeCSS");
				this.getView().byId("idPYTransaction").addStyleClass("btnClr3");

				this.PYTabBind();
			} else if (oButton.getId().indexOf("idDiffTax") > -1) {
				oData.Diff = true;
				this.getView().byId("idDiffTax").addStyleClass("HomeCSS");
				this.getView().byId("idDiffTax").addStyleClass("btnClr3");

				this.onloadDiffTax();
			} else if (oButton.getId().indexOf("idDemandRef") > -1) {
				oData.DemR = true;
				this.getView().byId("idDemandRef").addStyleClass("HomeCSS");
				this.getView().byId("idDemandRef").addStyleClass("btnClr3");

				this.onloadDemandRefTax();
			} else if (oButton.getId().indexOf("idComp") > -1) {
				oData.Comp = true;
				this.getView().byId("idComp").addStyleClass("HomeCSS");
				this.getView().byId("idComp").addStyleClass("btnClr3");

				this.onloadCompDmdTax();
			} else if (oButton.getId().indexOf("idHSN") > -1) {
				oData.HSN = true;
				this.getView().byId("idHSN").addStyleClass("HomeCSS");
				this.getView().byId("idHSN").addStyleClass("btnClr3");

				this.onLoadHSNSummary();
			}
			this.onStatusTime();
			this.getView().getModel("oVisiTab").refresh();
		},

		//================ Model setting for Visibility of Table columns based on check box ==============//
		_VisibleColumns: function () {
			var obj = {
				"OPr": true,
				"O3B": true,
				"OGc": true,
				"OU": true,
				"OGa": true,
				"OD": true,
				"IPr": true,
				"I3B": true,
				"IGc": true,
				"IU": true,
				"IGa": true,
				"ID": true,
				"TPr": false,
				"T3B": true,
				"TGc": true,
				"TU": true,
				"TGa": true,
				"TD": true,
				"PPr": true,
				"P3B": true,
				"PGc": false,
				"PU": true,
				"PGa": true,
				"PD": true,
				"DPr": false,
				"D3B": true,
				"DGc": false,
				"DU": true,
				"DGa": true,
				"DD": true,
				"RPr": false,
				"R3B": true,
				"RGc": false,
				"RU": true,
				"RGa": true,
				"RD": true,
				"CPr": false,
				"C3B": true,
				"CGc": false,
				"CU": true,
				"CGa": true,
				"CD": true,
				"HPr": true,
				"H3B": false,
				"HGc": false,
				"HU": true,
				"HGa": true,
				"HD": true
			};
			this.getView().setModel(new JSONModel(obj), "Visi");
			this.getView().getModel("Visi").refresh(true);

			var obj1 = {
				"Out": true,
				"In": false,
				"IRev": false,
				"Tax": false,
				"PY": false,
				"Diff": false,
				"DemR": false,
				"Comp": false,
				"HSN": false,
				"OHsn": false,
				"IHsn": false,
				"OIHSN": "Outward",
				"Footer": true
			};
			this.getView().setModel(new JSONModel(obj1), "oVisiTab");
			this.getView().getModel("oVisiTab").refresh(true);

			this.getView().byId("idOutward").addStyleClass("HomeCSS");
			this.getView().byId("idOutward").addStyleClass("btnClr3");
		},

		onPressGstr3bGstin: function (oEvt) {
			this.getView().byId("idOutward").addStyleClass("HomeCSS");
			this.getView().byId("idOutward").addStyleClass("btnClr3");
			this.getView().byId("idInward").removeStyleClass("HomeCSS");
			this.getView().byId("idTaxPaid").removeStyleClass("HomeCSS");
			this.getView().byId("idPYTransaction").removeStyleClass("HomeCSS");
			this.getView().byId("idDiffTax").removeStyleClass("HomeCSS");
			this.getView().byId("idDemandRef").removeStyleClass("HomeCSS");
			this.getView().byId("idComp").removeStyleClass("HomeCSS");
			this.getView().byId("idHSN").removeStyleClass("HomeCSS");
			this.getView().byId("idInward").removeStyleClass("btnClr3");
			this.getView().byId("idTaxPaid").removeStyleClass("btnClr3");
			this.getView().byId("idPYTransaction").removeStyleClass("btnClr3");
			this.getView().byId("idDiffTax").removeStyleClass("btnClr3");
			this.getView().byId("idDemandRef").removeStyleClass("btnClr3");
			this.getView().byId("idComp").removeStyleClass("btnClr3");
			this.getView().byId("idHSN").removeStyleClass("btnClr3");
			this.getView().byId("dpGstr9").setVisible(false);
			this.getView().byId("dpGstr9Gstin").setVisible(true);
			this.getView().byId("idGstinDetail").setText(oEvt.getSource().getText());
			this.getView().byId("idFyGstinDetail").setText(this.getView().byId("gstr9FY").getSelectedItem().getText());
			this.onStatusTime();
			var obj1 = {
				"Out": true,
				"In": false,
				"IRev": false,
				"Tax": false,
				"PY": false,
				"Diff": false,
				"DemR": false,
				"Comp": false,
				"HSN": false,
				"OHsn": false,
				"IHsn": false,
				"Footer": true
			};
			this.getView().setModel(new JSONModel(obj1), "oVisiTab");
			this.getView().getModel("oVisiTab").refresh(true);
			this.OutwardTabBind();
			this.authState = oEvt.getSource().getParent().getParent().getBindingContext("SummTab").getObject().auth;
		},

		onPressBackGlSummary: function () {
			this.getView().byId("dpGstr9").setVisible(true);
			this.getView().byId("dpGstr9Gstin").setVisible(false);
			var oData = this.getView().getModel("oVisiTab").getData();
			oData.Out = true;
			oData.In = false;
			oData.IRev = false;
			oData.Tax = false;
			oData.PY = false;
			oData.Diff = false;
			oData.DemR = false;
			oData.Comp = false;
			oData.HSN = false;
			oData.OHsn = false;
			oData.IHsn = false;
			oData.Footer = true;
			this.getView().getModel("oVisiTab").refresh(true);
			this.onLoadGSTINlist();
			// this.onLoadGSTINlist1();
			this.EntityTabBind();
		},

		//=========================== column visibility change ============================//
		showingCheck: function (evt) {
			var vId = evt.getSource().getId(),
				vSelect = evt.getSource().getSelected(),
				oModel = this.getView().getModel("Visi"),
				oData = oModel.getProperty("/");

			var oIdPropMap = {
				"OutDigiProID": "OPr",
				"OutDigi3BID": "O3B",
				"OutGSTID": "OGc",
				"OutUserID": "OU",
				"OutGSTNAvaiID": "OGa",
				"OutDiffID": "OD",
				"InDigiProID": "IPr",
				"InDigi3BID": "I3B",
				"InGSTID": "IGc",
				"InUserID": "IU",
				"InGSTNAvaiID": "IGa",
				"InDiffID": "ID",
				"TaxDigiProID": "TPr",
				"TaxDigi3BID": "T3B",
				"TaxGSTID": "TGc",
				"TaxUserID": "TU",
				"TaxGSTNAvaiID": "TGa",
				"TaxDiffID": "TD",
				"PYDigiProID": "PPr",
				"PYDigi3BID": "P3B",
				"PYGSTID": "PGc",
				"PYUserID": "PU",
				"PYGSTNAvaiID": "PGa",
				"PYDiffID": "PD",
				"DiffDigiProID": "DPr",
				"DiffDigi3BID": "D3B",
				"DiffGSTID": "DGc",
				"DiffUserID": "DU",
				"DiffGSTNAvaiID": "DGa",
				"DiffDiffID": "DD",
				"DRDigiProID": "RPr",
				"DRDigi3BID": "R3B",
				"DRGSTID": "RGc",
				"DRUserID": "RU",
				"DRGSTNAvaiID": "RGa",
				"DRDiffID": "RD",
				"CompDigiProID": "CPr",
				"CompDigi3BID": "C3B",
				"CompGSTID": "CGc",
				"CompUserID": "CU",
				"CompGSTNAvaiID": "CGa",
				"CompDiffID": "CD",
				"HSNDigiProID": "HPr",
				"HSNDigi3BID": "H3B",
				"HSNGSTID": "HGc",
				"HSNUserID": "HU",
				"HSNGSTNAvaiID": "HGa",
				"HSNDiffID": "HD"
			};
			for (var key in oIdPropMap) {
				if (oIdPropMap.hasOwnProperty(key) && vId.indexOf(key) !== -1) {
					oData[oIdPropMap[key]] = vSelect;
					break;
				}
			}
			oModel.refresh(true);
		},
		//==================== Expand and collapse full screen =====================//

		onGstr9FullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();

			if (action === "openOut") {
				oPropData.OutFullScreen = true;
				this.byId("idCcOut").setFullScreen(true);
				this.byId("idOutwardTab").setVisibleRowCount(16);

			} else if (action === "closeOut") {
				oPropData.OutFullScreen = false;
				this.byId("idCcOut").setFullScreen(false);
				this.byId("idOutwardTab").setVisibleRowCount(8);

			} else if (action === "openIn") {
				oPropData.InFullScreen = true;
				this.byId("idCcIn").setFullScreen(true);
				this.byId("idInwardTab").setVisibleRowCount(16);

			} else if (action === "closeIn") {
				oPropData.InFullScreen = false;
				this.byId("idCcIn").setFullScreen(false);
				this.byId("idInwardTab").setVisibleRowCount(8);

			} else if (action === "openTax") {
				oPropData.TaxFullScreen = true;
				this.byId("idCcTax").setFullScreen(true);
				this.byId("idTaxPaidTab").setVisibleRowCount(15);

			} else if (action === "closeTax") {
				oPropData.TaxFullScreen = false;
				this.byId("idCcTax").setFullScreen(false);
				this.byId("idTaxPaidTab").setVisibleRowCount(8);

			} else if (action === "openPY") {
				oPropData.PYFullScreen = true;
				this.byId("idCcPY").setFullScreen(true);
				this.byId("idPYTransCYTab").setVisibleRowCount(15);

			} else if (action === "closePY") {
				oPropData.PYFullScreen = false;
				this.byId("idCcPY").setFullScreen(false);
				this.byId("idPYTransCYTab").setVisibleRowCount(8);

			} else if (action === "openDiff") {
				oPropData.DiffFullScreen = true;
				this.byId("idCcDiff").setFullScreen(true);
				this.byId("idDiffTaxTab").setVisibleRowCount(15);

			} else if (action === "closeDiff") {
				oPropData.DiffFullScreen = false;
				this.byId("idCcDiff").setFullScreen(false);
				this.byId("idDiffTaxTab").setVisibleRowCount(8);

			} else if (action === "openDemd") {
				oPropData.DemullScreen = true;
				this.byId("idCcDemdR").setFullScreen(true);
				this.byId("idDemandRefundTab").setVisibleRowCount(15);

			} else if (action === "closeDemd") {
				oPropData.DemullScreen = false;
				this.byId("idCcDemdR").setFullScreen(false);
				this.byId("idDemandRefundTab").setVisibleRowCount(8);

			} else if (action === "openComp") {
				oPropData.CompFullScreen = true;
				this.byId("idCcComp").setFullScreen(true);
				this.byId("idCompositionTab").setVisibleRowCount(15);

			} else if (action === "closeComp") {
				oPropData.CompFullScreen = false;
				this.byId("idCcComp").setFullScreen(false);
				this.byId("idCompositionTab").setVisibleRowCount(8);

			} else if (action === "openHSN") {
				oPropData.HSNFullScreen = true;
				this.byId("idCcHSN").setFullScreen(true);
				this.byId("idHSNDetailTab").setVisibleRowCount(15);

			} else if (action === "closeHSN") {
				oPropData.HSNFullScreen = false;
				this.byId("idCcHSN").setFullScreen(false);
				this.byId("idHSNDetailTab").setVisibleRowCount(8);

			}
			this.getView().getModel("PrProperty").refresh(true);
		},

		onPressHSNSupply: function (oEvent) {
			var oModel = this.getView().getModel("oVisiTab"),
				oData = oModel.getData();

			oData.HSN = false;
			oData.IHsn = false;
			oData.OHsn = true;
			oData.Footer = false;

			if (oEvent.getSource().getText() == "HSN Outward") {
				oData.OIHSN = "Outward";
				this.onLoadHSNOutward();
			} else if (oEvent.getSource().getText() == "HSN Inward") {
				oData.OIHSN = "Inward";
				this.onLoadHSNOutward();
			}
			oModel.refresh(true);
		},

		onPressBackHSN: function (oEvnt) {
			var id = oEvnt.getSource().getId();
			var oData = this.getView().getModel("oVisiTab").getData();
			var that = this;
			if (id.includes("idBackOutward")) {
				if (that.HsnOutchanged) {
					MessageBox.confirm("Do you want to continue without saving data?", {
						title: "Confirm",
						actions: [sap.m.MessageBox.Action.YES,
							sap.m.MessageBox.Action.NO
						],
						onClose: function (oAction) {
							if (oAction == "YES") {
								oData.HSN = true;
								oData.IHsn = false;
								oData.OHsn = false;
								oData.Footer = true;
								that.onLoadHSNSummary();
								that.getView().getModel("oVisiTab").refresh(true);
							}
						},
					});
				} else {
					oData.HSN = true;
					oData.IHsn = false;
					oData.OHsn = false;
					oData.Footer = true;
					that.onLoadHSNSummary();
					that.getView().getModel("oVisiTab").refresh(true);
				}

			} else if (id.includes("idBackInward")) {
				if (that.HsnInchanged) {
					MessageBox.confirm("Do you want to continue without saving data?", {
						title: "Confirm",
						actions: [sap.m.MessageBox.Action.YES,
							sap.m.MessageBox.Action.NO
						],
						onClose: function (oAction) {
							if (oAction == "YES") {
								oData.HSN = true;
								oData.IHsn = false;
								oData.OHsn = false;
								oData.Footer = true;
								that.onLoadHSNSummary();
								that.getView().getModel("oVisiTab").refresh(true);
							}
						},
					});
				} else {
					oData.HSN = true;
					oData.IHsn = false;
					oData.OHsn = false;
					oData.Footer = true;
					that.onLoadHSNSummary();
					that.getView().getModel("oVisiTab").refresh(true);
				}

			} else if (id.includes("idBackOtherReverse")) {
				if (that.ORchanged) {
					MessageBox.confirm("Do you want to continue without saving data?", {
						title: "Confirm",
						actions: [sap.m.MessageBox.Action.YES,
							sap.m.MessageBox.Action.NO
						],
						onClose: function (oAction) {
							if (oAction == "YES") {
								oData.IRev = false;
								oData.In = true;
								oData.Footer = true;
								that.loadInwardData();
								that.getView().getModel("oVisiTab").refresh(true);
							}
						},
					});
				} else {
					oData.IRev = false;
					oData.In = true;
					oData.Footer = true;
					that.loadInwardData();
				}
			}
			this.getView().getModel("oVisiTab").refresh(true);
		},

		onPressOthersReverse: function () {
			var oData = this.getView().getModel("oVisiTab").getData();
			oData.IRev = true;
			oData.In = false;
			oData.Footer = false;
			this.getView().getModel("oVisiTab").refresh(true);
			this.loadOtherReverseData();
		},
		//=========================================Added by chaithra on 26/4/2021========================================//
		//===================		Start of  Inward Tab ============================================//
		//==========================================================================================//
		loadInwardData: function () {
			this.Inchanged = false;
			var payload = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr9InwardDashboardData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status != "E") {
						this._bindInwardTab(data);
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_bindInwardTab: function (data) {
			var aData = data.resp.gstr9InwardDashboard;
			aData.forEach(function (e) {
				var aField = [
						"userInputIgst", "userInputCgst", "userInputSgst", "userInputCess",
						"autoCalIgst", "autoCalCgst", "autoCalSgst", "autoCalCess", "getCompIgst", "getCompCgst", "getCompSgst", "getCompCess"
					],
					secAdd = [],
					secSubs = [];
				e.inputIgst = e.inputCgst = e.inputSgst = e.inputCess = true;
				e.edit = false;
				if ([
						"6A", "6B4", "6C4", "6CD", "6D4", "6E3", "6I", "6J", "6N", "6O",
						"7CD", "7H", "7I", "7J", "8A", "8B", "8D", "8H", "8I", "8J", "8K"
					].includes(e.subSection)) {
					e.inputIgst = e.inputCgst = e.inputSgst = e.inputCess = false;
				}
				if (["6E1", "6E2", "6F"].includes(e.subSection)) {
					e.userInputCgst = e.userInputSgst = "";
					e.inputCgst = e.inputSgst = false;
				}
				if (["6K", "6L", "7F", "7G"].includes(e.subSection)) {
					e.userInputIgst = e.userInputCess = "";
					e.inputIgst = e.inputCess = false;
				}
				this._getAddSubsSection(e.subSection, secAdd, secSubs);
				if (["6B4", "6C4", "6D4", "6CD", "6E3", "7CD"].includes(e.subSection)) {
					Array.prototype.push.apply(aField, ["gstinIgst", "gstinCgst", "gstinSgst", "gstinCess"]);
					this._addTotalSupplies(aField, e, aData, secAdd, secSubs);

				} else if (["6I", "6J", "6N", "6O", "7I", "7J", "8B", "8D", "8H", "8I", "8K"].includes(e.subSection)) {
					this._addTotalSupplies(aField, e, aData, secAdd, secSubs);
				}
				if (!["6", "6B", "6C", "6D", "6E", "7", "8"].includes(e.subSection)) {
					// e.diffTaxableValue = (+e.userInputTaxableVal - +e.gstinTaxableVal).toFixed(2);
					e.diffIgst = (+e.userInputIgst - +e.gstinIgst).toFixed(2);
					e.diffCgst = (+e.userInputCgst - +e.gstinCgst).toFixed(2);
					e.diffSgst = (+e.userInputSgst - +e.gstinSgst).toFixed(2);
					e.diffCess = (+e.userInputCess - +e.gstinCess).toFixed(2);
				}
			}.bind(this));
			this.getView().setModel(new JSONModel(data.resp), "oInDataModel");
			this.getView().getModel("oInDataModel").refresh(true);
		},

		_getAddSubsSection: function (section, arrAdd, arrSubs) {
			switch (section) {
			case "6B4":
				var secAdd = ["6B1", "6B2", "6B3"],
					secSubs = [];
				break;
			case "6C4":
				secAdd = ["6C1", "6C2", "6C3"];
				break;
			case "6D4":
				secAdd = ["6D1", "6D2", "6D3"];
				break;
			case "6CD":
				secAdd = ["6C4", "6D4"];
				break;
			case "6E3":
				secAdd = ["6E1", "6E2"];
				break;
			case "6I":
				secAdd = ["6B4", "6CD", "6E3", "6F", "6G", "6H"];
				break;
			case "6J":
				secAdd = ["6I"], secSubs = ["6A"];
				break;
			case "6N":
				secAdd = ["6K", "6L", "6M"];
				break;
			case "6O":
				secAdd = ["6I", "6N"];
				break;
			case "7CD":
				secAdd = ["7C", "7D"];
				break;
			case "7I":
				secAdd = ["7A", "7B", "7C", "7D", "7E", "7F", "7G", "7H"];
				break;
			case "7J":
				secAdd = ["6O"], secSubs = ["7I"];
				break;
			case "8B":
				secAdd = ["6B4", "6H"];
				break;
			case "8D":
				secAdd = ["8A"], secSubs = ["8B", "8C"];
				break;
			case "8H":
				secAdd = ["6E3"];
				break;
			case "8I":
				secAdd = ["8G"], secSubs = ["8H"];
				break;
			case "8J":
				secAdd = ["8I"];
				break;
			case "8K":
				secAdd = ["8E", "8F", "8J"];
				break;
			}
			Array.prototype.push.apply(arrAdd, secAdd);
			Array.prototype.push.apply(arrSubs, secSubs);
		},

		_addTotalSupplies: function (aField, obj, data, secAdd, secSubs) {
			var aAddSupplies = data.filter(function (item) {
					return secAdd.includes(item.subSection)
				}),
				aSubsSupplies = data.filter(function (item) {
					return secSubs.includes(item.subSection)
				});
			aField.forEach(function (fName) {
				var vTotal = 0;
				aAddSupplies.forEach(function (e) {
					vTotal += +e[fName];
				});
				aSubsSupplies.forEach(function (e) {
					vTotal -= +e[fName];
				});
				obj[fName] = +(vTotal.toFixed(2));
			});
		},

		//================= On click on Inward Edit user button ===================//		
		onEditInward: function () {
			MessageBox.confirm("Do you want to Edit User Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						var oModel = this.getView().getModel("oInDataModel"),
							aData = oModel.getProperty("/gstr9InwardDashboard");
						aData.forEach(function (e) {
							e.edit = true;
						})
						oModel.refresh(true);
					}
				}.bind(this)
			});
		},

		//======== Inward IGST change ========//
		onChangeInwardInput: function (oEvt, fName) {
			this.Inchanged = true;
			var oContext = oEvt.getSource().getBindingContext("oInDataModel"),
				aData = oContext.getModel().getProperty("/gstr9InwardDashboard"),
				object = oContext.getObject(),
				val = oEvt.getSource().getValue().toString(),
				val1 = val.split(".");
			if (!val.match(/^-?[0-9]\d*(\.\d+)?$/)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			if (val1[0].length > 17) {
				MessageBox.error("More than 17 digits are not allowed");
				return;
			}
			if (val1[1] && val1[1].length > 2) {
				MessageBox.error("More than 2 decimals are not allowed");
				return;
			}
			object[fName] = Number(val);
			aData.forEach(function (e) {
				var secAdd = [],
					secSubs = [];
				this._getAddSubsSection(e.subSection, secAdd, secSubs);
				if (["6B4", "6C4", "6D4", "6CD", "6E3", "6I", "6J", "6N", "6O", "7CD", "7I", "7J", "8B", "8D", "8H", "8I", "8J", "8K"]
					.includes(e.subSection)) {
					this._addTotalSupplies([fName], e, aData, secAdd, secSubs);
				}
				e.diffIgst = (+e.userInputIgst - +e.gstinIgst).toFixed(2);
				e.diffCgst = (+e.userInputCgst - +e.gstinCgst).toFixed(2);
				e.diffSgst = (+e.userInputSgst - +e.gstinSgst).toFixed(2);
				e.diffCess = (+e.userInputCess - +e.gstinCess).toFixed(2);
			}.bind(this));
			oContext.getModel().refresh();
		},
		/*
		//======== Inward CGST change =========//
		InCgstChange: function (oEvt) {
			var that = this;
			that.Inchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInDataModel");
			var oBject = oView.getModel("oInDataModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			// var reg = /^-?[0-9]\d*(\.\d+)?$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCgst = Number(val);
			var aData = that.getView().getModel("oInDataModel").getData().gstr9InwardDashboard;
			var vNum6I = 0;
			var vNum6J = 0;
			var vNum6N = 0;
			var vNumg6A = 0;
			var vNum7I = 0;
			var vNum6O = 0;
			var vNum6B = 0;
			var vNum6H = 0;
			var vNum8A = 0;
			var vNum8B = 0;
			var vNum8C = 0;
			var vNum8G = 0;
			var vNum8H = 0;
			var vNum8I = 0;
			var vNum8J = 0;
			var vNum8K = 0;
			var vNum8E = 0;
			var vNum8F = 0;
			var vNum6C = 0;
			var vNum6D = 0;
			var vNum6E = 0;
			var vNum7C = 0;
			var vNum7D = 0;
			for (var i = 0; i < aData.length; i++) {
				//========== section 6 calculation =============//
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" ||
					aData[i].subSection == "6B3" || aData[i].subSection == "6C1" ||
					aData[i].subSection == "6C2" || aData[i].subSection == "6C3" ||
					aData[i].subSection == "6D1" || aData[i].subSection == "6D2" ||
					aData[i].subSection == "6D3" || aData[i].subSection == "6E1" ||
					aData[i].subSection == "6E2" || aData[i].subSection == "6F" ||
					aData[i].subSection == "6G" || aData[i].subSection == "6H") {
					vNum6I = (Number(vNum6I) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "6A") {
					vNumg6A = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "6I") {
					aData[i].userInputCgst = Number(vNum6I).toFixed(2);
				}
				if (aData[i].subSection == "6J") {
					aData[i].userInputCgst = (Number(vNum6I) - Number(vNumg6A)).toFixed(2);
				}
				if (aData[i].subSection == "6K" || aData[i].subSection == "6L" || aData[i].subSection == "6M") {
					vNum6N = (Number(vNum6N) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "6N") {
					aData[i].userInputCgst = Number(vNum6N).toFixed(2);
				}
				if (aData[i].subSection == "6O") {
					aData[i].userInputCgst = (Number(vNum6I) + Number(vNum6N)).toFixed(2);
					vNum6O = aData[i].userInputCgst;
				}
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" || aData[i].subSection == "6B3") {
					vNum6B = (Number(vNum6B) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "6C1" || aData[i].subSection == "6C2" || aData[i].subSection == "6C3") {
					vNum6C = (Number(vNum6C) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "6D1" || aData[i].subSection == "6D2" || aData[i].subSection == "6D3") {
					vNum6D = (Number(vNum6D) + Number(aData[i].userInputCgst)).toFixed(2);
				
				if (aData[i].subSection == "6E1" || aData[i].subSection == "6E2") {
					vNum6E = (Number(vNum6E) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "6B4") {
					aData[i].userInputCgst = vNum6B;
				}
				if (aData[i].subSection == "6C4") {
					aData[i].userInputCgst = vNum6C;
				}
				if (aData[i].subSection == "6D4") {
					aData[i].userInputCgst = vNum6D;
				}
				if (aData[i].subSection == "6CD") {
					aData[i].userInputCgst = (Number(vNum6C) + Number(vNum6D)).toFixed(2);
				}
				if (aData[i].subSection == "6E3") {
					aData[i].userInputCgst = vNum6E;
				}
				if (aData[i].subSection == "6H") {
					vNum6H = Number(aData[i].userInputCgst).toFixed(2);
				}
				//====== section 7 calculation =====//
				if (aData[i].subSection == "7A" || aData[i].subSection == "7B" ||
					aData[i].subSection == "7C" || aData[i].subSection == "7D" ||
					aData[i].subSection == "7E" || aData[i].subSection == "7F" ||
					aData[i].subSection == "7G" || aData[i].subSection == "7H") {
					vNum7I = (Number(vNum7I) + Number(aData[i].userInputCgst)).toFixed(2);
				}
				if (aData[i].subSection == "7C") {
					vNum7C = Number(aData[i].userInputCgst).toFixed(2)
				}
				if (aData[i].subSection == "7D") {
					vNum7D = Number(aData[i].userInputCgst).toFixed(2)
				}
				if (aData[i].subSection == "7CD") {
					aData[i].userInputCgst = (Number(vNum7C) + Number(vNum7D)).toFixed(2);
				}
				if (aData[i].subSection == "7I") {
					aData[i].userInputCgst = Number(vNum7I).toFixed(2);
				}
				if (aData[i].subSection == "7J") {
					aData[i].userInputCgst = (Number(vNum6O) - Number(vNum7I)).toFixed(2);
				}
				//======== section 8 calculation ========/
				if (aData[i].subSection == "8A") {
					vNum8A = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8B") {
					aData[i].userInputCgst = (Number(vNum6B) + Number(vNum6H)).toFixed(2);
					vNum8B = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8C") {
					vNum8C = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8D") {
					aData[i].userInputCgst = (Number(vNum8A) - (Number(vNum8B) + Number(vNum8C))).toFixed(2);
				}
				if (aData[i].subSection == "8E") {
					vNum8E = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8F") {
					vNum8F = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8G") {
					vNum8G = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8H") {
					// aData[i].userInputIgst = (Number(aData[15].userInputIgst) + Number(aData[16].userInputIgst)).toFixed(2);
					// aData[i].userInputCess = (Number(aData[15].userInputCess) + Number(aData[16].userInputCess)).toFixed(2);
					vNum8H = Number(aData[i].userInputCgst).toFixed(2);
				}
				if (aData[i].subSection == "8I") {
					vNum8I = (Number(vNum8G) - Number(vNum8H)).toFixed(2);
					aData[i].userInputCgst = Number(vNum8I).toFixed(2);
				}
				if (aData[i].subSection == "8J") {
					vNum8J = Number(vNum8I).toFixed(2);
					aData[i].userInputCgst = Number(vNum8J).toFixed(2);
				}
				if (aData[i].subSection == "8K") {
					vNum8K = (Number(vNum8E) + Number(vNum8F) + Number(vNum8J)).toFixed(2);
					aData[i].userInputCgst = Number(vNum8K).toFixed(2);
				}
			}
			oView.getModel("oInDataModel").refresh();
			that.IncalcDifference();
		},
		//======== Inward SGST change ========//
		InSgstChange: function (oEvt) {
			var that = this;
			that.Inchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInDataModel");
			var oBject = oView.getModel("oInDataModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputSgst = Number(val);
			var aData = that.getView().getModel("oInDataModel").getData().gstr9InwardDashboard;
			var vNum6I = 0;
			var vNum6J = 0;
			var vNum6N = 0;
			var vNumg6A = 0;
			var vNum7I = 0;
			var vNum6O = 0;
			var vNum6B = 0;
			var vNum6H = 0;
			var vNum8A = 0;
			var vNum8B = 0;
			var vNum8C = 0;
			var vNum8G = 0;
			var vNum8H = 0;
			var vNum8I = 0;
			var vNum8J = 0;
			var vNum8K = 0;
			var vNum8E = 0;
			var vNum8F = 0;
			var vNum6C = 0;
			var vNum6D = 0;
			var vNum6E = 0;
			var vNum7C = 0;
			var vNum7D = 0;
			for (var i = 0; i < aData.length; i++) {
				//========== section 6 calculation =============//
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" ||
					aData[i].subSection == "6B3" || aData[i].subSection == "6C1" ||
					aData[i].subSection == "6C2" || aData[i].subSection == "6C3" ||
					aData[i].subSection == "6D1" || aData[i].subSection == "6D2" ||
					aData[i].subSection == "6D3" || aData[i].subSection == "6E1" ||
					aData[i].subSection == "6E2" || aData[i].subSection == "6F" ||
					aData[i].subSection == "6G" || aData[i].subSection == "6H") {
					vNum6I = (Number(vNum6I) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6A") {
					vNumg6A = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "6I") {
					aData[i].userInputSgst = Number(vNum6I).toFixed(2);
				}
				if (aData[i].subSection == "6J") {
					aData[i].userInputSgst = (Number(vNum6I) - Number(vNumg6A)).toFixed(2);
				}
				if (aData[i].subSection == "6K" || aData[i].subSection == "6L" || aData[i].subSection == "6M") {
					vNum6N = (Number(vNum6N) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6N") {
					aData[i].userInputSgst = Number(vNum6N).toFixed(2);
				}
				if (aData[i].subSection == "6O") {
					aData[i].userInputSgst = (Number(vNum6I) + Number(vNum6N)).toFixed(2);
					vNum6O = aData[i].userInputSgst;
				}
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" || aData[i].subSection == "6B3") {
					vNum6B = (Number(vNum6B) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6C1" || aData[i].subSection == "6C2" || aData[i].subSection == "6C3") {
					vNum6C = (Number(vNum6C) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6D1" || aData[i].subSection == "6D2" || aData[i].subSection == "6D3") {
					vNum6D = (Number(vNum6D) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6E1" || aData[i].subSection == "6E2") {
					vNum6E = (Number(vNum6E) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "6B4") {
					aData[i].userInputSgst = vNum6B;
				}
				if (aData[i].subSection == "6C4") {
					aData[i].userInputSgst = vNum6C;
				}
				if (aData[i].subSection == "6CD") {
					aData[i].userInputSgst = (Number(vNum6C) + Number(vNum6D)).toFixed(2);
				}
				if (aData[i].subSection == "6D4") {
					aData[i].userInputSgst = vNum6D;
				}
				if (aData[i].subSection == "6E3") {
					aData[i].userInputSgst = vNum6E;
				}
				if (aData[i].subSection == "6H") {
					vNum6H = Number(aData[i].userInputSgst).toFixed(2);
				}
				//====== section 7 calculation =====//
				if (aData[i].subSection == "7A" || aData[i].subSection == "7B" ||
					aData[i].subSection == "7C" || aData[i].subSection == "7D" ||
					aData[i].subSection == "7E" || aData[i].subSection == "7F" ||
					aData[i].subSection == "7G" || aData[i].subSection == "7H") {
					vNum7I = (Number(vNum7I) + Number(aData[i].userInputSgst)).toFixed(2);
				}
				if (aData[i].subSection == "7C") {
					vNum7C = Number(aData[i].userInputSgst).toFixed(2)
				}
				if (aData[i].subSection == "7D") {
					vNum7D = Number(aData[i].userInputSgst).toFixed(2)
				}
				if (aData[i].subSection == "7CD") {
					aData[i].userInputSgst = (Number(vNum7C) + Number(vNum7D)).toFixed(2);
				}
				if (aData[i].subSection == "7I") {
					aData[i].userInputSgst = Number(vNum7I).toFixed(2);
				}
				if (aData[i].subSection == "7J") {
					aData[i].userInputSgst = (Number(vNum6O) - Number(vNum7I)).toFixed(2);
				}
				//======== section 8 calculation ========/
				if (aData[i].subSection == "8A") {
					vNum8A = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8B") {
					aData[i].userInputSgst = (Number(vNum6B) + Number(vNum6H)).toFixed(2);
					vNum8B = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8C") {
					vNum8C = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8D") {
					aData[i].userInputSgst = (Number(vNum8A) - (Number(vNum8B) + Number(vNum8C))).toFixed(2);
				}
				if (aData[i].subSection == "8E") {
					vNum8E = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8F") {
					vNum8F = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8G") {
					vNum8G = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8H") {
					// aData[i].userInputIgst = (Number(aData[15].userInputIgst) + Number(aData[16].userInputIgst)).toFixed(2);
					// aData[i].userInputCess = (Number(aData[15].userInputCess) + Number(aData[16].userInputCess)).toFixed(2);
					vNum8H = Number(aData[i].userInputSgst).toFixed(2);
				}
				if (aData[i].subSection == "8I") {
					vNum8I = (Number(vNum8G) - Number(vNum8H)).toFixed(2);
					aData[i].userInputSgst = Number(vNum8I).toFixed(2);
				}
				if (aData[i].subSection == "8J") {
					vNum8J = Number(vNum8I).toFixed(2);
					aData[i].userInputSgst = Number(vNum8J).toFixed(2);
				}
				if (aData[i].subSection == "8K") {
					vNum8K = (Number(vNum8E) + Number(vNum8F) + Number(vNum8J)).toFixed(2);
					aData[i].userInputSgst = Number(vNum8K).toFixed(2);
				
			}
			oView.getModel("oInDataModel").refresh();
			that.IncalcDifference();
		},
		//======== Inward Cess change ========//
		InCessChange: function (oEvt) {
			var that = this;
			that.Inchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInDataModel");
			var oBject = oView.getModel("oInDataModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCess = Number(val);
			var aData = that.getView().getModel("oInDataModel").getData().gstr9InwardDashboard;
			var vNum6I = 0;
			var vNum6J = 0;
			var vNum6N = 0;
			var vNumg6A = 0;
			var vNum7I = 0;
			var vNum6O = 0;
			var vNum6B = 0;
			var vNum6H = 0;
			var vNum8A = 0;
			var vNum8B = 0;
			var vNum8C = 0;
			var vNum8G = 0;
			var vNum8H = 0;
			var vNum8I = 0;
			var vNum8J = 0;
			var vNum8K = 0;
			var vNum8E = 0;
			var vNum8F = 0;
			var vNum6C = 0;
			var vNum6D = 0;
			var vNum6E = 0;
			var vNum7C = 0;
			var vNum7D = 0;
			for (var i = 0; i < aData.length; i++) {
				//========== section 6 calculation =============//
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" ||
					aData[i].subSection == "6B3" || aData[i].subSection == "6C1" ||
					aData[i].subSection == "6C2" || aData[i].subSection == "6C3" ||
					aData[i].subSection == "6D1" || aData[i].subSection == "6D2" ||
					aData[i].subSection == "6D3" || aData[i].subSection == "6E1" ||
					aData[i].subSection == "6E2" || aData[i].subSection == "6F" ||
					aData[i].subSection == "6G" || aData[i].subSection == "6H") {
					vNum6I = (Number(vNum6I) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6A") {
					vNumg6A = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "6I") {
					aData[i].userInputCess = Number(vNum6I).toFixed(2);
				}
				if (aData[i].subSection == "6J") {
					aData[i].userInputCess = (Number(vNum6I) - Number(vNumg6A)).toFixed(2);
				}
				if (aData[i].subSection == "6K" || aData[i].subSection == "6L" || aData[i].subSection == "6M") {
					vNum6N = (Number(vNum6N) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6N") {
					aData[i].userInputCess = Number(vNum6N).toFixed(2);
				}
				if (aData[i].subSection == "6O") {
					aData[i].userInputCess = (Number(vNum6I) + Number(vNum6N)).toFixed(2);
					vNum6O = aData[i].userInputCess;
				}
				if (aData[i].subSection == "6B1" || aData[i].subSection == "6B2" || aData[i].subSection == "6B3") {
					vNum6B = (Number(vNum6B) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6C1" || aData[i].subSection == "6C2" || aData[i].subSection == "6C3") {
					vNum6C = (Number(vNum6C) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6D1" || aData[i].subSection == "6D2" || aData[i].subSection == "6D3") {
					vNum6D = (Number(vNum6D) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6E1" || aData[i].subSection == "6E2") {
					vNum6E = (Number(vNum6E) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "6B4") {
					aData[i].userInputCess = vNum6B;
				}
				if (aData[i].subSection == "6C4") {
					aData[i].userInputCess = vNum6C;
				}
				if (aData[i].subSection == "6D4") {
					aData[i].userInputCess = vNum6D;
				}
				if (aData[i].subSection == "6CD") {
					aData[i].userInputCess = (Number(vNum6C) + Number(vNum6D)).toFixed(2);
				}
				if (aData[i].subSection == "6E3") {
					aData[i].userInputCess = vNum6E;
				}
				if (aData[i].subSection == "6H") {
					vNum6H = Number(aData[i].userInputCess).toFixed(2);
				}
				//====== section 7 calculation =====//
				if (aData[i].subSection == "7A" || aData[i].subSection == "7B" ||
					aData[i].subSection == "7C" || aData[i].subSection == "7D" ||
					aData[i].subSection == "7E" || aData[i].subSection == "7F" ||
					aData[i].subSection == "7G" || aData[i].subSection == "7H") {
					vNum7I = (Number(vNum7I) + Number(aData[i].userInputCess)).toFixed(2);
				}
				if (aData[i].subSection == "7C") {
					vNum7C = Number(aData[i].userInputCess).toFixed(2)
				}
				if (aData[i].subSection == "7D") {
					vNum7D = Number(aData[i].userInputCess).toFixed(2)
				}
				if (aData[i].subSection == "7CD") {
					aData[i].userInputCess = (Number(vNum7C) + Number(vNum7D)).toFixed(2);
				}
				if (aData[i].subSection == "7I") {
					aData[i].userInputCess = Number(vNum7I).toFixed(2);
				}
				if (aData[i].subSection == "7J") {
					aData[i].userInputCess = (Number(vNum6O) - Number(vNum7I)).toFixed(2);
				}
				//======== section 8 calculation ========/
				if (aData[i].subSection == "8A") {
					vNum8A = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8B") {
					aData[i].userInputCess = (Number(vNum6B) + Number(vNum6H)).toFixed(2);
					vNum8B = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8C") {
					vNum8C = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8D") {
					aData[i].userInputCess = (Number(vNum8A) - (Number(vNum8B) + Number(vNum8C))).toFixed(2);
				}
				if (aData[i].subSection == "8E") {
					vNum8E = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8F") {
					vNum8F = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8G") {
					vNum8G = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8H") {
					// aData[i].userInputIgst = (Number(aData[15].userInputIgst) + Number(aData[16].userInputIgst)).toFixed(2);
					aData[i].userInputCess = (Number(aData[18].userInputCess) + Number(aData[19].userInputCess)).toFixed(2);
					vNum8H = Number(aData[i].userInputCess).toFixed(2);
				}
				if (aData[i].subSection == "8I") {
					vNum8I = (Number(vNum8G) - Number(vNum8H)).toFixed(2);
					aData[i].userInputCess = Number(vNum8I).toFixed(2);
				}
				if (aData[i].subSection == "8J") {
					vNum8J = Number(vNum8I).toFixed(2);
					aData[i].userInputCess = Number(vNum8J).toFixed(2);
				}
				if (aData[i].subSection == "8K") {
					vNum8K = (Number(vNum8E) + Number(vNum8F) + Number(vNum8J)).toFixed(2);
					aData[i].userInputCess = Number(vNum8K).toFixed(2);
				}
			}
			oView.getModel("oInDataModel").refresh();
			that.IncalcDifference();
		},
		//======== Inward Calculate Difference after user edit =====//
		IncalcDifference: function () {
			var oView = this.getView();
			var that = this;
			var aData = oView.getModel("oInDataModel").getData().gstr9InwardDashboard;
			for (var i = 0; i < aData.length; i++) {
				//========  Difference calculation =====//
				aData[i].diffTaxableValue = aData[i].userInputTaxableVal - aData[i].gstinTaxableVal;
				aData[i].diffIgst = aData[i].userInputIgst - aData[i].gstinIgst;
				aData[i].diffCgst = aData[i].userInputCgst - aData[i].gstinCgst;
				aData[i].diffSgst = aData[i].userInputSgst - aData[i].gstinSgst;
				aData[i].diffCess = aData[i].userInputCess - aData[i].gstinCess;
			}
			oView.getModel("oInDataModel").refresh();
		}, */

		onPressSaveChangesIn9: function () {
			var that = this;
			var oView = this.getView();
			if (that.Inchanged) {
				var Data = oView.getModel("oInDataModel").getData().gstr9InwardDashboard;
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection !== "6" && Data[j].subSection !== "6B" && Data[j].subSection !== "6C" && Data[j].subSection !== "6D" &&
						Data[j].subSection !== "6E" && Data[j].subSection !== "7" && Data[j].subSection !== "8" &&
						Data[j].subSection !== "6I" && Data[j].subSection !== "6J" && Data[j].subSection !== "6N" &&
						Data[j].subSection !== "6O" && Data[j].subSection !== "7I" && Data[j].subSection !== "7J" &&
						Data[j].subSection !== "8B" && Data[j].subSection !== "8D" && Data[j].subSection !== "8I" &&
						Data[j].subSection !== "8J" && Data[j].subSection !== "8K" && Data[j].subSection !== "6CD" &&
						Data[j].subSection !== "7CD") {
						///////Taxvalue//////
						var val = Data[j].userInputTaxableVal.toString();
						var val1 = val.split(".");
						if (val1[1] !== undefined) {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1[1] !== "" && val1[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var reg = /^[0-9]*\.?[0-9]*$/;
						var reg = /^-?[0-9]\d*(\.\d+)?$/;
						if (val != "") {
							if (!val.match(reg)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////IGST//////////
						var valIgst = Data[j].userInputIgst.toString();
						var val1Igst = valIgst.split(".");
						if (val1Igst[1] !== undefined) {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Igst[1] !== "" && val1Igst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regIgst = /^[0-9]*\.?[0-9]*$/;
						var regIgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valIgst != "") {
							if (!valIgst.match(regIgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////CGST//////////
						var valCgst = Data[j].userInputCgst.toString();
						var val1Cgst = valCgst.split(".");
						if (val1Cgst[1] !== undefined) {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cgst[1] !== "" && val1Cgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCgst = /^[0-9]*\.?[0-9]*$/;
						var regCgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCgst != "") {
							if (!valCgst.match(regCgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////SGST//////////
						var valSgst = Data[j].userInputSgst.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valSgst != "") {
							if (!valSgst.match(regSgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////Cess//////////
						var valCess = Data[j].userInputCess.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
					}

				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesIn91();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}
		},
		onPressSaveChangesIn91: function () {
			var that = this;
			var oView = this.getView();
			var Data = oView.getModel("oInDataModel").getData().gstr9InwardDashboard;
			var gstr9InwardDashboard = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "6" && Data[j].subSection !== "6B" && Data[j].subSection !== "6C" && Data[j].subSection !== "6D" &&
					Data[j].subSection !== "6E" && Data[j].subSection !== "7" && Data[j].subSection !== "8" &&
					Data[j].subSection !== "6I" && Data[j].subSection !== "6J" && Data[j].subSection !== "6N" &&
					Data[j].subSection !== "6O" && Data[j].subSection !== "7I" && Data[j].subSection !== "7J" &&
					Data[j].subSection !== "8B" && Data[j].subSection !== "8D" && Data[j].subSection !== "8I" &&
					Data[j].subSection !== "8J" && Data[j].subSection !== "8K" && Data[j].subSection !== "6B4" &&
					Data[j].subSection !== "6C4" && Data[j].subSection !== "6D4" && Data[j].subSection !== "6E3" &&
					Data[j].subSection !== "6A" && Data[j].subSection !== "8A" && Data[j].subSection !== "7H") {
					var famPenDet = {
						subSection: Data[j].subSection,
						taxableVal: Data[j].userInputTaxableVal === "" ? "0" : Data[j].userInputTaxableVal.toString(),
						igst: Data[j].userInputIgst === "" ? "0" : Data[j].userInputIgst.toString(),
						cgst: Data[j].userInputCgst === "" ? "0" : Data[j].userInputCgst.toString(),
						sgst: Data[j].userInputSgst === "" ? "0" : Data[j].userInputSgst.toString(),
						cess: Data[j].userInputCess === "" ? "0" : Data[j].userInputCess.toString()
					};
					gstr9InwardDashboard.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "SAVE",
					"userInputList": gstr9InwardDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9UserInputData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data saved successfully");
						that.loadInwardData();
						that.onStatusTime();
					} else if (sts.status === "E") {
						MessageBox.error(data.resp);
						// MessageBox.success("Data saved successfully");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		loadOtherReverseData: function () {
			var that = this;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"subSection": "7H"
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			that.ORchanged = false;
			var GstnsList = "/aspsapapi/getGstr9Inward7HPopUpData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status != "E") {
						var aData = data.resp.gstr9Inward7HData;
						var SlNo = 0;
						for (var k = 0; k < data.resp.gstr9Inward7HData.length; k++) {
							SlNo = SlNo + 1;
							aData[k].SlNo = SlNo;
							aData[k].edit = false;
							aData[k].old = true;
							aData[k].statep = null;
							aData[k].stateig = null;
							aData[k].statecg = null;
							aData[k].statesg = null;
							aData[k].statec = null;
						};
						that.getView().setModel(new JSONModel(data.resp.gstr9Inward7HData), "oInOtherRevModel");
						that.getView().getModel("oInOtherRevModel").refresh();
					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onEditOtherRev: function () {
			var that = this;
			var aData = that.getView().getModel("oInOtherRevModel").getData();
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							for (var i = 0; i < aData.length; i++) {
								aData[i].edit = true;
							}
							that.getView().getModel("oInOtherRevModel").refresh(true);
						}
					}
				});
		},
		onAddOtherRev: function () {
			var that = this;
			that.ORchanged = true;
			var data = that.getView().getModel("oInOtherRevModel").getData();
			var vSlNo = data.length + 1;
			var oBject = {
				"SlNo": vSlNo,
				"subSection": "7H",
				"taxableVal": "",
				"igst": "0",
				"cgst": "0",
				"sgst": "0",
				"cess": "0",
				"particulers": "",
				"edit": true,
				"old": false,
				"statep": null,
				"stateig": null,
				"statecg": null,
				"statesg": null,
				"statec": null
			};
			data.push(oBject);
			that.getView().getModel("oInOtherRevModel").refresh(true);
		},
		onDelOtherRev: function (oEvt) {
			var that = this;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var oBject = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			MessageBox.alert('Do you want to delete selected data', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						if (oBject.old == true) {
							that.ORchanged = true;
						}
						var v1 = vPath.sPath;
						var index = v1.split("/", 2);
						oView.getModel("oInOtherRevModel").getData().splice(index[1], 1);
						var vSlNo = 0;
						for (var i = 0; i < oView.getModel("oInOtherRevModel").getData().length; i++) {
							vSlNo = vSlNo + 1;
							oView.getModel("oInOtherRevModel").getData()[i].SlNo = vSlNo;
						}
						oView.getModel("oInOtherRevModel").refresh(true);
					} else {
						return;
					}
				}
			});

		},
		InORIgstChange: function (oEvt) {
			var that = this;
			that.ORchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var oBject = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.igst = val;
			oBject.stateig = null;
			oView.getModel("oInOtherRevModel").refresh(true);
		},
		InORCgstChange: function (oEvt) {
			var that = this;
			that.ORchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var oBject = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cgst = val;
			oBject.statecg = null;
			oView.getModel("oInOtherRevModel").refresh(true);
		},
		InORSgstChange: function (oEvt) {
			var that = this;
			that.ORchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var oBject = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.sgst = val;
			oBject.statesg = null;
			oView.getModel("oInOtherRevModel").refresh(true);
		},
		InORCessChange: function (oEvt) {
			var that = this;
			that.ORchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var oBject = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cess = val;
			oBject.statec = null;
			oView.getModel("oInOtherRevModel").refresh(true);
		},
		onChangeParticular: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.ORchanged = true;
			var vPath = oEvt.getSource().getBindingContext("oInOtherRevModel");
			var object = oView.getModel("oInOtherRevModel").getObject(vPath.sPath);
			object.statep = null;
			oView.getModel("oInOtherRevModel").refresh(true);
		},
		onPressSaveChangesInOR: function () {
			var that = this;
			var oView = this.getView();
			if (that.ORchanged) {
				var Data = oView.getModel("oInOtherRevModel").getData();
				for (var j = 0; j < Data.length; j++) {

					//////////IGST//////////
					var valIgst = Data[j].igst.toString();
					var val1Igst = valIgst.split(".");
					if (val1Igst[1] !== undefined) {
						if (val1Igst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
						if (val1Igst[1] !== "" && val1Igst[1].length > 2) {
							MessageBox.error("More than 2 decimals are not allowed");
							return;
						}
					} else {
						if (val1Igst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
					}
					// var regIgst = /^[0-9]*\.?[0-9]*$/;
					var regIgst = /^-?[0-9]\d*(\.\d+)?$/;
					if (!valIgst.match(regIgst)) {
						MessageBox.error("Alphabets and special characters are not allowed");
						return;
					}
					//////////CGST//////////
					var valCgst = Data[j].cgst.toString();
					var val1Cgst = valCgst.split(".");
					if (val1Cgst[1] !== undefined) {
						if (val1Cgst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
						if (val1Cgst[1] !== "" && val1Cgst[1].length > 2) {
							MessageBox.error("More than 2 decimals are not allowed");
							return;
						}
					} else {
						if (val1Cgst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
					}
					// var regCgst = /^[0-9]*\.?[0-9]*$/;
					var regCgst = /^-?[0-9]\d*(\.\d+)?$/;
					if (!valCgst.match(regCgst)) {
						MessageBox.error("Alphabets and special characters are not allowed");
						return;
					}
					//////////SGST//////////
					var valSgst = Data[j].sgst.toString();
					var val1Sgst = valSgst.split(".");
					if (val1Sgst[1] !== undefined) {
						if (val1Sgst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
						if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
							MessageBox.error("More than 2 decimals are not allowed");
							return;
						}
					} else {
						if (val1Sgst[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
					}
					// var regSgst = /^[0-9]*\.?[0-9]*$/;
					var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
					if (!valSgst.match(regSgst)) {
						MessageBox.error("Alphabets and special characters are not allowed");
						return;
					}
					//////////Cess//////////
					var valCess = Data[j].cess.toString();
					var val1Cess = valCess.split(".");
					if (val1Cess[1] !== undefined) {
						if (val1Cess[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
						if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
							MessageBox.error("More than 2 decimals are not allowed");
							return;
						}
					} else {
						if (val1Cess[0].length > 17) {
							MessageBox.error("More than 17 digits are not allowed");
							return;
						}
					}
					// var regCess = /^[0-9]*\.?[0-9]*$/;
					var regCess = /^-?[0-9]\d*(\.\d+)?$/;
					if (!valCess.match(regCess)) {
						MessageBox.error("Alphabets and special characters are not allowed");
						return;
					}

				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesInOR1();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}
		},
		onPressSaveChangesInOR1: function () {
			var that = this;
			var oView = this.getView();
			var Data = oView.getModel("oInOtherRevModel").getData();
			var gstr9InwardDashboard = [];
			var vError = false;
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].particulers === "") {
					vError = true;
					Data[j].statep = "Error";
				}
				if (Data[j].igst === "" && Number(Data[j].igst) !== 0) {
					vError = true;
					Data[j].stateig = "Error";
				}
				if (Data[j].cgst === "" && Number(Data[j].cgst) !== 0) {
					vError = true;
					Data[j].statecg = "Error";
				}
				if (Data[j].sgst === "" && Number(Data[j].sgst) !== 0) {
					vError = true;
					Data[j].statesg = "Error";
				}
				if (Data[j].cess === "" && Number(Data[j].cess) !== 0) {
					vError = true;
					Data[j].statec = "Error";
				}
				var famPenDet = {
					subSection: Data[j].subSection,
					taxableVal: Data[j].taxableVal === "" ? "0" : Data[j].taxableVal.toString(),
					igst: Data[j].igst === "" ? "0" : Data[j].igst.toString(),
					cgst: Data[j].cgst === "" ? "0" : Data[j].cgst.toString(),
					sgst: Data[j].sgst === "" ? "0" : Data[j].sgst.toString(),
					cess: Data[j].cess === "" ? "0" : Data[j].cess.toString(),
					particulers: Data[j].particulers
				};
				gstr9InwardDashboard.push(famPenDet);

			}
			if (vError) {
				oView.getModel("oInOtherRevModel").refresh();
				MessageBox.error("Please enter all the values");
				return;
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "7H_SAVE",
					"userInputList": gstr9InwardDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9UserInputData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data saved successfully");
						that.loadOtherReverseData();
					} else if (sts.status === "E") {
						MessageBox.error(data.resp);
						// MessageBox.success("Data saved successfully");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onCancelOtherRev: function () {
			var that = this;
			var oView = this.getView();
			// var vPath = oEvt.getSource().getBindingContext("oHSNOutModel");
			// var oData = oView.getModel("oHSNOutModel").getData();
			MessageBox.alert('Do you want to cancel?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.loadOtherReverseData();
					} else {
						return;
					}
				}
			});
		},
		//================================ended by chaithra on 26/4/ 2021==========================================//
		//======================== end of Inward Tab =============================================================//
		//=======================================================================================================//
		onUpdateGSTN: function () {
			var that = this;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getUpdateGstnData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table bind Function=======================//
		_addTotalOutwardSupplies: function (fName, data, secAdd, secSubs) {
			var vTotal = 0,
				aAddSupplies = data.filter(function (item) {
					return secAdd.includes(item.subSection)
				}),
				aSubsSupplies = data.filter(function (item) {
					return secSubs.includes(item.subSection)
				});
			aAddSupplies.forEach(function (e) {
				vTotal += +e[fName];
			});
			aSubsSupplies.forEach(function (e) {
				vTotal -= +e[fName];
			});
			return vTotal.toFixed(2);
		},

		OutwardTabBind: function () {
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};

			this.OutChanged = false;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getOutwardTabDetails.do",
					contentType: "application/json",
					data: JSON.stringify(Request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var Data = data.resp.gstr9OutwardDashboard;
					Data.forEach(function (e) {
						e.edit9 = e.taxVal9 = e.igst9 = e.cgst9 = e.sgst9 = e.cess9 = false;

						if (["4C", "4D"].includes(e.subSection)) {
							e.cgst9 = e.sgst9 = true;
							e.userInputCgst = e.userInputSgst = "";
						}
						if (["4", "4H", "4M", "4N"].includes(e.subSection)) {
							e.taxVal9 = true;
							e.igst9 = e.cgst9 = e.sgst9 = e.cess9 = true;
						}
						if (["5A", "5B", "5C", "5C1", "5D", "5E", "5F", "5H", "5I", "5J", "5K"].includes(e.subSection)) {
							e.igst9 = e.cgst9 = e.sgst9 = e.cess9 = true;
							e.userInputIgst = e.userInputCgst = e.userInputSgst = e.userInputCess = "";
						}
						if (["5", "5G", "5L", "5M", "5N"].includes(e.subSection)) {
							e.taxVal9 = true;
							e.igst9 = e.cgst9 = e.sgst9 = e.cess9 = true;
							e.userInputIgst = e.userInputCgst = e.userInputSgst = e.userInputCess = "";
						}
						switch (e.subSection) {
						case "4H":
							var aAdd = ["4A", "4B", "4C", "4D", "4E", "4F", "4G", "4G1"],
								aSubs = [];
							break;
						case "4M":
							aAdd = ["4I", "4J", "4K", "4L"], aSubs = [];
							break;
						case "4N":
							aAdd = ["4H", "4M"], aSubs = [];
							break;
						case "5G":
							aAdd = ["5A", "5B", "5C", "5C1", "5D", "5E", "5F"], aSubs = [];
							break;
						case "5L":
							aAdd = ["5H", "5I", "5J", "5K"], aSubs = [];
							break;
						case "5M":
							aAdd = ["5G", "5L"], aSubs = [];
							break;
						case "5N":
							aAdd = ["4N", "5M"], aSubs = ["4G", "4G1"];
							break;
						}
						if (["4H", "4M", "4N", "5N"].includes(e.subSection)) {
							e.userInputTaxableVal = this._addTotalOutwardSupplies("userInputTaxableVal", Data, aAdd, aSubs);
							e.userInputIgst = this._addTotalOutwardSupplies("userInputIgst", Data, aAdd, aSubs);
							e.userInputCgst = this._addTotalOutwardSupplies("userInputCgst", Data, aAdd, aSubs);
							e.userInputSgst = this._addTotalOutwardSupplies("userInputSgst", Data, aAdd, aSubs);
							e.userInputCess = this._addTotalOutwardSupplies("userInputCess", Data, aAdd, aSubs);

						} else if (["5G", "5L", "5M"].includes(e.subSection)) {
							e.userInputTaxableVal = this._addTotalOutwardSupplies("userInputTaxableVal", Data, aAdd, aSubs);
							e.userInputIgst = e.userInputCgst = e.userInputSgst = e.userInputCess = "";
						}

						if (["4H", "4M", "4N", "5G", "5L", "5M", "5N"].includes(e.subSection)) {
							e.autoCalTaxableVal = this._addTotalOutwardSupplies("autoCalTaxableVal", Data, aAdd, aSubs);
							e.autoCalIgst = this._addTotalOutwardSupplies("autoCalIgst", Data, aAdd, aSubs);
							e.autoCalCgst = this._addTotalOutwardSupplies("autoCalCgst", Data, aAdd, aSubs);
							e.autoCalSgst = this._addTotalOutwardSupplies("autoCalSgst", Data, aAdd, aSubs);
							e.autoCalCess = this._addTotalOutwardSupplies("autoCalCess", Data, aAdd, aSubs);

							e.getCompTaxableVal = this._addTotalOutwardSupplies("getCompTaxableVal", Data, aAdd, aSubs);
							e.getCompIgst = this._addTotalOutwardSupplies("getCompIgst", Data, aAdd, aSubs);
							e.getCompCgst = this._addTotalOutwardSupplies("getCompCgst", Data, aAdd, aSubs);
							e.getCompSgst = this._addTotalOutwardSupplies("getCompSgst", Data, aAdd, aSubs);
							e.getCompCess = this._addTotalOutwardSupplies("getCompCess", Data, aAdd, aSubs);
						}
						if (["4", "5"].includes(e.subSection)) {
							e.userInputTaxableVal = e.userInputSgst = e.userInputIgst = e.userInputCgst = e.userInputCess = "";
						} else {
							e.diffenceTaxableVal = (+e.userInputTaxableVal - +e.gstinTaxableVal).toFixed(2);
							e.diffenceIGSTVal = (+e.userInputIgst - +e.gstinIgst).toFixed(2);
							e.diffenceCGSTVal = (+e.userInputCgst - +e.gstinCgst).toFixed(2);
							e.diffenceSGSTVal = (+e.userInputSgst - +e.gstinSgst).toFixed(2);
							e.diffenceCESSVal = (+e.userInputCess - +e.gstinCess).toFixed(2);
						}
					}.bind(this));
					this.getView().setModel(new JSONModel(Data), "Gstr9Out");
					this.getView().getModel("Gstr9Out").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},
		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table bind Function=======================//
		//=============================END================================//

		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Edit Inputs Function================//
		onEditOutward: function () {
			var that = this;
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.onEditOutward1();
						}
					}
				});
		},

		onEditOutward1: function () {
			var Data = this.getView().getModel("Gstr9Out").getData();
			for (var j = 0; j < Data.length; j++) {
				Data[j].edit9 = true;
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},
		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Edit Inputs Function================//
		//=============================END================================//

		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Inputs Change Event Function========//
		TaxableValueChange9: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.OutChanged = true;
			var vPath = oEvt.getSource().getBindingContext("Gstr9Out");
			var oBject = oView.getModel("Gstr9Out").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;

			var reg = /^-?[0-9]\d*(\.\d+)?$/; // Added by chaithra to allow both positive and negative values
			if (!val.match(reg)) {
				MessageBox.error("Alphabets are not allowed");
				return;
			}
			oBject.userInputTaxableVal = val;
			var Data = this.getView().getModel("Gstr9Out").getData();
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection === "4H") {
					Data[j].userInputTaxableVal = (Number(Data[1].userInputTaxableVal) + Number(Data[2].userInputTaxableVal) + Number(Data[3].userInputTaxableVal) +
						Number(Data[4].userInputTaxableVal) + Number(Data[5].userInputTaxableVal) + Number(Data[6].userInputTaxableVal) +
						Number(Data[7].userInputTaxableVal) + Number(Data[8].userInputTaxableVal)).toFixed(2);
				}

				if (Data[j].subSection === "4M") {
					var i4 = Number(Data[10].userInputTaxableVal);
					var j4 = Number(Data[11].userInputTaxableVal);
					var k4 = Number(Data[12].userInputTaxableVal);
					var l4 = Number(Data[13].userInputTaxableVal);
					var m4 = (-(i4) + (j4) + (k4) - (l4)).toFixed(2);
					if (m4 == "-0.00") {
						Data[j].userInputTaxableVal = "0.00";
					} else {
						Data[j].userInputTaxableVal = m4;
					}
				}

				if (Data[j].subSection === "4N") {
					Data[j].userInputTaxableVal = (Number(Data[9].userInputTaxableVal) + Number(Data[14].userInputTaxableVal)).toFixed(2);
				}

				if (Data[j].subSection === "5G") {

					Data[j].userInputTaxableVal = (Number(Data[17].userInputTaxableVal) + Number(Data[18]
							.userInputTaxableVal) +
						Number(Data[19].userInputTaxableVal) + Number(Data[20].userInputTaxableVal) + Number(Data[21].userInputTaxableVal) +
						Number(Data[22].userInputTaxableVal) + Number(Data[23].userInputTaxableVal)).toFixed(
						2);
				}

				if (Data[j].subSection === "5L") {
					Data[j].userInputTaxableVal = (-Number(Data[25].userInputTaxableVal) + Number(Data[26].userInputTaxableVal) + Number(Data[27].userInputTaxableVal) -
						Number(Data[28].userInputTaxableVal)).toFixed(2);
				}

				if (Data[j].subSection === "5M") {
					Data[j].userInputTaxableVal = (Number(Data[24].userInputTaxableVal) + Number(Data[29].userInputTaxableVal)).toFixed(2);
				}

				if (Data[j].subSection === "5N") {
					Data[j].userInputTaxableVal = (Number(Data[15].userInputTaxableVal) + Number(Data[30].userInputTaxableVal) - Number(Data[7].userInputTaxableVal) -
							Number(Data[8].userInputTaxableVal))
						.toFixed(2);
				}

				Data[j].diffenceTaxableVal = (Number(Data[j].userInputTaxableVal) - Number(Data[j].gstinTaxableVal)).toFixed(2);
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},

		IGSTChange9: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.OutChanged = true;
			var Data = this.getView().getModel("Gstr9Out").getData();
			var vPath = oEvt.getSource().getBindingContext("Gstr9Out");
			var oBject = oView.getModel("Gstr9Out").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputIgst = val;
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection === "4H") {
					Data[j].userInputIgst = (Number(Data[1].userInputIgst) + Number(Data[2].userInputIgst) + Number(Data[3].userInputIgst) +
						Number(Data[4].userInputIgst) + Number(Data[5].userInputIgst) + Number(Data[6].userInputIgst) +
						Number(Data[7].userInputIgst) + Number(Data[8].userInputIgst)).toFixed(2);
				}

				if (Data[j].subSection === "4M") {
					var i4 = Number(Data[10].userInputIgst);
					var j4 = Number(Data[11].userInputIgst);
					var k4 = Number(Data[12].userInputIgst);
					var l4 = Number(Data[13].userInputIgst);
					var m4 = (-(i4) + (j4) + (k4) - (l4)).toFixed(2);
					if (m4 == "-0.00") {
						Data[j].userInputIgst = "0.00";
					} else {
						Data[j].userInputIgst = m4;
					}
				}

				if (Data[j].subSection === "4N") {
					Data[j].userInputIgst = (Number(Data[9].userInputIgst) + Number(Data[14].userInputIgst)).toFixed(2);
				}

				// if (Data[j].subSection === "5G") {
				// 	Data[j].userInputIgst = (Number(Data[16].userInputIgst) + Number(Data[17].userInputIgst) + Number(Data[18].userInputIgst) +
				// 		Number(Data[19].userInputIgst) + Number(Data[20].userInputIgst) + Number(Data[21].userInputIgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5L") {
				// 	Data[j].userInputIgst = (Number(Data[23].userInputIgst) + Number(Data[24].userInputIgst) + Number(Data[25].userInputIgst) +
				// 		Number(Data[26].userInputIgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5M") {
				// 	Data[j].userInputIgst = (Number(Data[22].userInputIgst) + Number(Data[27].userInputIgst)).toFixed(2);
				// }

				if (Data[j].subSection === "5N") {
					Data[j].userInputIgst = (Number(Data[15].userInputIgst) + Number(Data[30].userInputIgst) - Number(Data[7].userInputIgst) -
							Number(Data[8].userInputIgst))
						.toFixed(2);
				}

				Data[j].diffenceIGSTVal = (Number(Data[j].userInputIgst) - Number(Data[j].gstinIgst)).toFixed(2);
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},

		CGSTChange9: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.OutChanged = true;
			var Data = this.getView().getModel("Gstr9Out").getData();
			var vPath = oEvt.getSource().getBindingContext("Gstr9Out");
			var oBject = oView.getModel("Gstr9Out").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCgst = val;
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection === "4H") {
					Data[j].userInputCgst = (Number(Data[1].userInputCgst) + Number(Data[2].userInputCgst) + Number(Data[5].userInputCgst) + Number(
						Data[6].userInputCgst) + Number(Data[7].userInputCgst) + Number(Data[8].userInputCgst)).toFixed(2);
				}

				if (Data[j].subSection === "4M") {
					var i4 = Number(Data[10].userInputCgst);
					var j4 = Number(Data[11].userInputCgst);
					var k4 = Number(Data[12].userInputCgst);
					var l4 = Number(Data[13].userInputCgst);
					var m4 = (-(i4) + (j4) + (k4) - (l4)).toFixed(2);
					if (m4 == "-0.00") {
						Data[j].userInputCgst = "0.00";
					} else {
						Data[j].userInputCgst = m4;
					}

				}

				if (Data[j].subSection === "4N") {
					Data[j].userInputCgst = (Number(Data[9].userInputCgst) + Number(Data[14].userInputCgst)).toFixed(2);
				}

				// if (Data[j].subSection === "5G") {
				// 	Data[j].userInputCgst = (Number(Data[16].userInputCgst) + Number(Data[17].userInputCgst) + Number(Data[18].userInputCgst) +
				// 		Number(Data[19].userInputCgst) + Number(Data[20].userInputCgst) + Number(Data[21].userInputCgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5L") {
				// 	Data[j].userInputCgst = (Number(Data[23].userInputCgst) + Number(Data[24].userInputCgst) + Number(Data[25].userInputCgst) +
				// 		Number(Data[26].userInputCgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5M") {
				// 	Data[j].userInputCgst = (Number(Data[22].userInputCgst) + Number(Data[27].userInputCgst)).toFixed(2);
				// }

				if (Data[j].subSection === "5N") {
					Data[j].userInputCgst = (Number(Data[15].userInputCgst) + Number(Data[30].userInputCgst) - Number(Data[7].userInputCgst) -
							Number(Data[8].userInputCgst))
						.toFixed(2);
				}

				Data[j].diffenceCGSTVal = (Number(Data[j].userInputCgst) - Number(Data[j].gstinCgst)).toFixed(2);
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},

		SGSTChange9: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.OutChanged = true;
			var Data = this.getView().getModel("Gstr9Out").getData();
			var vPath = oEvt.getSource().getBindingContext("Gstr9Out");
			var oBject = oView.getModel("Gstr9Out").getObject(vPath.sPath);

			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputSgst = val;
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection === "4H") {
					Data[j].userInputSgst = (Number(Data[1].userInputSgst) + Number(Data[2].userInputSgst) + Number(Data[5].userInputSgst) + Number(
						Data[6].userInputSgst) + Number(Data[7].userInputSgst) + Number(Data[8].userInputSgst)).toFixed(2);
				}

				if (Data[j].subSection === "4M") {
					var i4 = Number(Data[10].userInputSgst);
					var j4 = Number(Data[11].userInputSgst);
					var k4 = Number(Data[12].userInputSgst);
					var l4 = Number(Data[13].userInputSgst);
					var m4 = (-(i4) + (j4) + (k4) - (l4)).toFixed(2);
					if (m4 == "-0.00") {
						Data[j].userInputSgst = "0.00";
					} else {
						Data[j].userInputSgst = m4;
					}

				}

				if (Data[j].subSection === "4N") {
					Data[j].userInputSgst = (Number(Data[9].userInputSgst) + Number(Data[14].userInputSgst)).toFixed(2);
				}

				// if (Data[j].subSection === "5G") {
				// 	Data[j].userInputSgst = (Number(Data[16].userInputSgst) + Number(Data[17].userInputSgst) + Number(Data[18].userInputSgst) +
				// 		Number(Data[19].userInputSgst) + Number(Data[20].userInputSgst) + Number(Data[21].userInputSgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5L") {
				// 	Data[j].userInputSgst = (Number(Data[23].userInputSgst) + Number(Data[24].userInputSgst) + Number(Data[25].userInputSgst) +
				// 		Number(Data[26].userInputSgst)).toFixed(2);
				// }

				// if (Data[j].subSection === "5M") {
				// 	Data[j].userInputSgst = (Number(Data[22].userInputSgst) + Number(Data[27].userInputSgst)).toFixed(2);
				// }

				if (Data[j].subSection === "5N") {
					Data[j].userInputSgst = (Number(Data[15].userInputSgst) + Number(Data[30].userInputSgst) - Number(Data[7].userInputSgst) -
						Number(Data[8].userInputSgst)).toFixed(2);
				}

				Data[j].diffenceSGSTVal = (Number(Data[j].userInputSgst) - Number(Data[j].gstinSgst)).toFixed(2);
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},

		CESSChange9: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.OutChanged = true;
			var Data = this.getView().getModel("Gstr9Out").getData();
			var vPath = oEvt.getSource().getBindingContext("Gstr9Out");
			var oBject = oView.getModel("Gstr9Out").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCess = val;
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection === "4H") {
					Data[j].userInputCess = (Number(Data[1].userInputCess) + Number(Data[2].userInputCess) + Number(Data[3].userInputCess) +
						Number(Data[4].userInputCess) + Number(Data[5].userInputCess) + Number(Data[6].userInputCess) +
						Number(Data[7].userInputCess) +
						Number(Data[8].userInputCess)).toFixed(2);
				}

				if (Data[j].subSection === "4M") {
					var i4 = Number(Data[10].userInputCess);
					var j4 = Number(Data[11].userInputCess);
					var k4 = Number(Data[12].userInputCess);
					var l4 = Number(Data[13].userInputCess);
					var m4 = (-(i4) + (j4) + (k4) - (l4)).toFixed(2);
					if (m4 == "-0.00") {
						Data[j].userInputCess = "0.00";
					} else {
						Data[j].userInputCess = m4;
					}
				}

				if (Data[j].subSection === "4N") {
					Data[j].userInputCess = (Number(Data[9].userInputCess) + Number(Data[14].userInputCess)).toFixed(2);
				}

				// if (Data[j].subSection === "5G") {
				// 	Data[j].userInputCess = (Number(Data[16].userInputCess) + Number(Data[17].userInputCess) + Number(Data[18].userInputCess) +
				// 		Number(Data[19].userInputCess) + Number(Data[20].userInputCess) + Number(Data[21].userInputCess)).toFixed(2);
				// }

				// if (Data[j].subSection === "5L") {
				// 	Data[j].userInputCess = (Number(Data[23].userInputCess) + Number(Data[24].userInputCess) + Number(Data[25].userInputCess) +
				// 		Number(Data[26].userInputCess)).toFixed(2);
				// }

				// if (Data[j].subSection === "5M") {
				// 	Data[j].userInputCess = (Number(Data[22].userInputCess) + Number(Data[27].userInputCess)).toFixed(2);
				// }

				if (Data[j].subSection === "5N") {
					Data[j].userInputCess = (Number(Data[15].userInputCess) + Number(Data[30].userInputCess) - Number(Data[7].userInputCess) -
							Number(Data[8].userInputCess))
						.toFixed(2);
				}

				Data[j].diffenceCESSVal = (Number(Data[j].userInputCess) - Number(Data[j].gstinCess)).toFixed(2);
			}
			this.getView().getModel("Gstr9Out").refresh(true);
		},
		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Inputs Change Event Function========//
		//=============================END================================//

		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Save Changes Function===============//
		onPressSaveChangesOut9: function () {
			var that = this;
			var Data = this.getView().getModel("Gstr9Out").getData();
			if (that.OutChanged) {
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection !== "4" && Data[j].subSection !== "5" && Data[j].subSection !== "4H" && Data[j].subSection !== "4M" &&
						Data[
							j].subSection !== "4N" && Data[j].subSection !== "5G" && Data[j].subSection !== "5L" && Data[j].subSection !== "5M" &&
						Data[j].subSection !== "5N") {
						///////Taxvalue//////
						var val = Data[j].userInputTaxableVal.toString();
						var val1 = val.split(".");
						if (val1[1] !== undefined) {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1[1] !== "" && val1[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var reg = /^[0-9]*\.?[0-9]*$/;
						var reg = /^-?[0-9]\d*(\.\d+)?$/;
						if (!val.match(reg)) {
							MessageBox.error("Alphabets and special characters are not allowed");
							return;
						}
						//////////IGST//////////
						var valIgst = Data[j].userInputIgst.toString();
						var val1Igst = valIgst.split(".");
						if (val1Igst[1] !== undefined) {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Igst[1] !== "" && val1Igst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regIgst = /^[0-9]*\.?[0-9]*$/;
						var regIgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valIgst != "") {
							if (!valIgst.match(regIgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////CGST//////////
						var valCgst = Data[j].userInputCgst.toString();
						var val1Cgst = valCgst.split(".");
						if (val1Cgst[1] !== undefined) {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cgst[1] !== "" && val1Cgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCgst = /^[0-9]*\.?[0-9]*$/;
						var regCgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCgst != "") {
							if (!valCgst.match(regCgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////SGST//////////
						var valSgst = Data[j].userInputSgst.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valSgst != "") {
							if (!valSgst.match(regSgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////Cess//////////
						var valCess = Data[j].userInputCess.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
					}
				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesOut91();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}
		},

		onPressSaveChangesOut91: function () {
			var Data = this.getView().getModel("Gstr9Out").getData();
			var gstr9OutwardDashboard = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "4" && Data[j].subSection !== "5" && Data[j].subSection !== "4H" && Data[j].subSection !== "4M" &&
					Data[
						j].subSection !== "4N" && Data[j].subSection !== "5G" && Data[j].subSection !== "5L" && Data[j].subSection !== "5M" &&
					Data[j].subSection !== "5N") {
					var famPenDet = {
						subSection: Data[j].subSection,
						userInputTaxableVal: Data[j].userInputTaxableVal === "" ? "0" : Data[j].userInputTaxableVal.toString(),
						userInputIgst: Data[j].userInputIgst === "" ? "0" : Data[j].userInputIgst.toString(),
						userInputCgst: Data[j].userInputCgst === "" ? "0" : Data[j].userInputCgst.toString(),
						userInputSgst: Data[j].userInputSgst === "" ? "0" : Data[j].userInputSgst.toString(),
						userInputCess: Data[j].userInputCess === "" ? "0" : Data[j].userInputCess.toString()
					};
					gstr9OutwardDashboard.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "SAVE",
					"gstr9OutwardDashboard": gstr9OutwardDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveOutwardTabDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success(data.resp);
						that.OutwardTabBind();
						that.onStatusTime();
					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//================= Added By Vinay Kodam 22-04-2021=================//
		//===============Outward Table Save Changes Function===============//
		//=============================END================================//

		//================= Added By Vinay Kodam 23-04-2021=================//
		//===============Outward Table Clear User Data Changes Function====//
		onClearUserDataGlobal: function () {
			var that = this;
			MessageBox.show(
				"Do you want to Clear User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.onClearUserDataGlobal1();
						}
					}
				});
		},

		onClearUserDataGlobal1: function () {
			var Tab = this.getView().getModel("oVisiTab").getData(),
				TabName;
			if (Tab.Out === true) {
				TabName = "outward";
			} else if (Tab.In === true) {
				TabName = "inward";
			} else if (Tab.Tax === true) {
				TabName = "taxpaid";
			} else if (Tab.PY === true) {
				TabName = "pyincy";
			} else if (Tab.Diff === true) {
				TabName = "difftax";
			} else if (Tab.DemR === true) {
				TabName = "deeref";
			} else if (Tab.Comp === true) {
				TabName = "compsale";
			} else if (Tab.OHsn === true) {
				if (Tab.OIHSN === "Outward") {
					TabName = "hsnoutward";
				} else {
					TabName = "hsninward";
				}
			} else if (Tab.IHsn === true) {
				TabName = "hsninward";
			}
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"tabName": TabName
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/clearTabData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success(data.resp);
						/*if (TabName == "inward") {
							that.loadInwardData();
						}*/
						if (Tab.Out === true) {
							that.OutwardTabBind();
						} else if (Tab.In === true) {
							that.loadInwardData();
						} else if (Tab.Tax === true) {
							that.TaxPaidTabBind();
						} else if (Tab.PY === true) {
							that.PYTabBind();
						} else if (TabName == "difftax") {
							that.onloadDiffTax();
						} else if (TabName == "deeref") {
							that.onloadDemandRefTax();
						} else if (TabName == "compsale") {
							that.onloadCompDmdTax();
						} else if (TabName == "hsninward") {
							that.onLoadHSNOutward();
						} else if (TabName == "hsnoutward") {
							that.onLoadHSNOutward();
						}

					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},
		//================= Added By Vinay Kodam 23-04-2021=================//
		//===============Outward Table Clear User Data Changes Function====//
		//=============================END================================//

		//================= Added By Vinay Kodam 23-04-2021=================//
		//====================Tax Paid Table Bind Function=================//
		TaxPaidTabBind: function () {
			var that = this;
			that.TaxpChanged = false;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			var GSTR9TPModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9TaxPaidData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var Data = data.resp.gstr9TaxPaidDashboardData;
					for (var j = 0; j < Data.length; j++) {
						Data[j].editTP9 = false;
						Data[j].visiTP9 = false;
						Data[j].diffenceTaxableValTp = (Number(Data[j].userInputTaxableVal) - Number(Data[j].gstinTaxableVal)).toFixed(2);
					}
					Data.unshift({
						"subSection": "9",
						"visiTP9": true
					});
					GSTR9TPModel.setData(Data);
					oTaxReGstnView.setModel(GSTR9TPModel, "GSTR9TPModel");
					oTaxReGstnView.getModel("GSTR9TPModel").refresh(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onEditTp: function () {
			var that = this;
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.onEditTp1();
						}
					}
				});
		},

		onEditTp1: function () {
			var Data = this.getView().getModel("GSTR9TPModel").getData();
			for (var j = 0; j < Data.length; j++) {
				Data[j].editTP9 = true;
			}
			this.getView().getModel("GSTR9TPModel").refresh(true);
		},

		TaxableValueChangeTp: function (oEvt) {
			var that = this;
			var oView = this.getView();
			that.TaxpChanged = true;
			var Data = this.getView().getModel("GSTR9TPModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9TPModel");
			var oBject = oView.getModel("GSTR9TPModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputTaxableVal = val;
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceTaxableValTp = (Number(Data[j].userInputTaxableVal) - Number(Data[j].gstinTaxableVal)).toFixed(2);
			}
			oView.getModel("GSTR9TPModel").refresh(true);
		},

		onPressSaveChangesTp: function () {
			var that = this;
			var Data = this.getView().getModel("GSTR9TPModel").getData();
			if (that.TaxpChanged) {
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection !== "9") {
						///////Taxvalue//////
						var val = Data[j].userInputTaxableVal.toString();
						var val1 = val.split(".");
						if (val1[1] !== undefined) {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1[1] !== "" && val1[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var reg = /^[0-9]*\.?[0-9]*$/;
						var reg = /^-?[0-9]\d*(\.\d+)?$/;
						if (!val.match(reg)) {
							MessageBox.error("Alphabets and special characters are not allowed");
							return;
						}
					}
				}
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesTp1();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}

		},

		onPressSaveChangesTp1: function () {
			var Data = this.getView().getModel("GSTR9TPModel").getData();
			var userInputs = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "9") {
					var famPenDet = {
						subSection: Data[j].subSection,
						taxableVal: Data[j].userInputTaxableVal === "" ? "0" : Data[j].userInputTaxableVal.toString(),
					};
					userInputs.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"userInputs": userInputs
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9TaxPaidData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data Saved Successfully");
						that.TaxPaidTabBind();
						that.onStatusTime();
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//========================Tax Paid Function========================//
		//=============================END================================//

		//=============================== Added by chaithra on 26/4/2021 ===============================//
		//=============================== HSN Details =================================================//
		//============================================================================================//
		onLoadHSNSummary: function () {
			var that = this;
			that.HSNchanged = false;
			var Request = {
				// "req" : {
				"gstin": this.getView().byId("idGstinDetail").getText(),
				"fy": this.getView().byId("idFyGstinDetail").getText()
					// }
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9HsnDetailsSummary.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList,
					// contentType: "application/json",
					data: Request,
					dataType: "json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status == "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].diffCount = data.resp[i].digiPrcount - data.resp[i].gstr9Summcount;
							data.resp[i].difftotalQty = data.resp[i].digiPrtotalQty - data.resp[i].gstr9SummQty;
							data.resp[i].diffTaxableVal = (Number(data.resp[i].digiPrTaxableVal) - Number(data.resp[i].gstr9SummTaxableVal)).toFixed(
								2);
							data.resp[i].diffIgst = (Number(data.resp[i].digiPrIgst) - Number(data.resp[i].gstr9SummIgst)).toFixed(2);
							data.resp[i].diffCgst = (Number(data.resp[i].digiPrCgst) - Number(data.resp[i].gstr9SummCgst)).toFixed(2);
							data.resp[i].diffSgst = (Number(data.resp[i].digiPrSgst) - Number(data.resp[i].gstr9SummSgst)).toFixed(2);
							data.resp[i].diffCess = (Number(data.resp[i].digiPrCess) - Number(data.resp[i].gstr9SummCess)).toFixed(2);
						}
						that.getView().setModel(new JSONModel(data.resp), "oHSNSumModel");
						that.getView().getModel("oHSNSumModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//===========================================================================//
		//====================== Start of HSN outward ==============================//
		//=========================================================================//
		onLoadHSNOutward: function (table) {

			var that = this;
			that.HsnOutchanged = false;
			var oData = this.getView().getModel("oVisiTab").getData();
			var Request = {
				"gstin": this.getView().byId("idGstinDetail").getText(),
				"fy": this.getView().byId("idFyGstinDetail").getText(),
				"type": oData.OIHSN === "Outward" ? "O" : "I"
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9HsnOutwardInward.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList,
					data: Request,
					dataType: "json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status == "S") {
						var vSlNo = 0;
						for (var i = 0; i < data.resp.length; i++) {
							vSlNo = vSlNo + 1;
							data.resp[i].SlNo = vSlNo;
							data.resp[i].edit = false;
							data.resp[i].visi = false;

						}
						that.getView().setModel(new JSONModel(data.resp), "oHSNOutModel");
						that.getView().getModel("oHSNOutModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onHSNOutwardAdd: function () {
			var that = this;
			that.HsnOutchanged = true;
			var data = that.getView().getModel("oHSNOutModel").getData();
			var vSlNo = data.length + 1;
			var object = {
				"SlNo": vSlNo,
				"edit": true,
				"visi": true,
				"hsn_sc": null,
				"uqc": null,
				"rt": 0,
				"processeddesc": null,
				"processedqty": null,
				"processedtxval": 0,
				"isprocessedconcesstional": "",
				"processedigst": 0,
				"processedcgst": 0,
				"processedsgst": 0,
				"processedcess": 0,
				"userdesc": null,
				"userqty": null,
				"usertxval": 0,
				"isuserconcesstional": "",
				"userigst": 0,
				"usercgst": 0,
				"usersgst": 0,
				"usercess": 0
			};
			data.push(object);
			that.getView().getModel("oHSNOutModel").refresh(true);
		},
		onCancelOutward: function () {
			var that = this;
			var oView = this.getView();
			MessageBox.alert('Do you want to cancel?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onLoadHSNOutward();

					} else {
						return;
					}
				}
			});
		},
		onEditHSNOutward: function () {
			//eslint-disable-line
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("idHSNOutwardTab").getSelectedIndices(),
				oHsnModel = this.getView().getModel("oHSNOutModel"),
				oHsnData = oHsnModel.getData();

			if (aIndex.length === 0) {
				sap.m.MessageBox.information(oBundle.getText("msgEditSel1record"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				MessageBox.show(
					"Do you want to Edit User Data?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								for (var i = 0; i < aIndex.length; i++) {
									oHsnData[aIndex[i]].edit = true;
								}
								oHsnModel.refresh(true);
							}
						}
					});
			}
		},
		onChangeHsnValue: function (oEvent) {
			oEvent.getSource().setValueState("None");
		},
		_getHsnSavePayload: function (data, type) { //eslint-disable-line
			if (type === "User") {
				return {
					"hsn_sc": data.hsn_sc,
					"rt": data.rt === "" ? "0" : data.rt,
					"uqc": data.uqc || "OTH",
					"desc": data.userdesc,
					"qty": data.userqty || 0,
					"isconcesstional": data.isuserconcesstional,
					"txval": data.usertxval || 0,
					"iamt": data.userigst || 0,
					"camt": data.usercgst || 0,
					"samt": data.usersgst || 0,
					"csamt": data.usercess || 0
				};
			} else {
				return {
					"hsn_sc": data.hsn_sc,
					"uqc": data.uqc || "OTH",
					"rt": data.rt || 0,
					"desc": data.processeddesc,
					"qty": data.processedqty || 0,
					"isconcesstional": data.isprocessedconcesstional,
					"txval": data.processedtxval || 0,
					"iamt": data.processedigst || 0,
					"camt": data.processedcgst || 0,
					"samt": data.processedsgst || 0,
					"csamt": data.processedcess || 0,
				};
			}

		},

		onDeleteHSNOutward: function () {
			var that = this;
			MessageBox.alert('Do you want to Delete selected data?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onDeleteHSNOutwardFinal();
					} else {
						return;
					}
				}
			});
		},
		onDeleteHSNOutwardFinal: function () {
			//eslint-disable-line
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("idHSNOutwardTab").getSelectedIndices(),
				oHsnModel = this.getView().getModel("oHSNOutModel"),
				oHsnData = oHsnModel.getData(),
				that = this,
				flag = true,
				oPayload = [];
			if (aIndex.length == 0) {
				sap.m.MessageBox.error("Please select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < aIndex.length; i++) {
				if (!oHsnData[aIndex[i]].hsn_sc) {
					flag = false;
					oHsnData[aIndex[i]].hsn_scState = "Error";
					oHsnData[aIndex[i]].hsn_scStateText = oBundle.getText("msgMandatory");
				}

				// if (!oHsnData[aIndex[i]].rt.toString()) {
				// 	flag = false;
				// 	oHsnData[aIndex[i]].rtState = "Error";
				// 	oHsnData[aIndex[i]].rtStateText = oBundle.getText("msgMandatory");
				// }

				// if (!oHsnData[aIndex[i]].uqc) {
				// 	flag = false;
				// 	oHsnData[aIndex[i]].uqcState = "Error";
				// 	oHsnData[aIndex[i]].uqcStateText = oBundle.getText("msgMandatory");
				// }
				//  && oHsnData[aIndex[i]].visi
				if (flag) {
					oPayload.push(this._getHsnSavePayload(oHsnData[aIndex[i]], 'User'));

				}
			}
			if (flag && oPayload.length > 0) {
				var oData = this.getView().getModel("oVisiTab").getData();

				var request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText(),
						"type": oData.OIHSN === "Outward" ? "O" : "I",
						"gstr9Table17ListDto": oPayload
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/deleteGstr9HSNInwardAndOutwardData.do",
						data: JSON.stringify(request),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts.status === "S") {
							MessageBox.success(data.resp);
							that.onLoadHSNOutward();
							that.onStatusTime();
						} else if (sts.status === "E") {
							MessageBox.error("Invalid HSN");
							// that.setErrorHSNOutward(data.resp);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			} else {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oHsnModel.refresh(true);
			}
		},
		onSaveHSNOutward: function () {
			var that = this;
			MessageBox.alert('Do you want to Save Changes?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onSaveHSNOutwardFinal();
					} else {
						return;
					}
				}
			});
		},
		onSaveHSNOutwardFinal: function () {
			//eslint-disable-line
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("idHSNOutwardTab").getSelectedIndices(),
				oHsnModel = this.getView().getModel("oHSNOutModel"),
				oHsnData = oHsnModel.getData(),
				that = this,
				flag = true,
				oPayload = [];
			if (aIndex.length == 0) {
				sap.m.MessageBox.error("Please select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}

			for (var i = 0; i < aIndex.length; i++) {
				if (!oHsnData[aIndex[i]].hsn_sc) {
					flag = false;
					oHsnData[aIndex[i]].hsn_scState = "Error";
					oHsnData[aIndex[i]].hsn_scStateText = oBundle.getText("msgMandatory");
				}

				if (!oHsnData[aIndex[i]].rt.toString()) {
					flag = false;
					oHsnData[aIndex[i]].rtState = "Error";
					oHsnData[aIndex[i]].rtStateText = oBundle.getText("msgMandatory");
				}

				if (!oHsnData[aIndex[i]].uqc) {
					flag = false;
					oHsnData[aIndex[i]].uqcState = "Error";
					oHsnData[aIndex[i]].uqcStateText = oBundle.getText("msgMandatory");
				}
				//  && oHsnData[aIndex[i]].visi
				if (flag) {
					oPayload.push(this._getHsnSavePayload(oHsnData[aIndex[i]], 'User'));

				}
			}
			if (flag && oPayload.length > 0) {
				var oData = this.getView().getModel("oVisiTab").getData();

				var request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText(),
						"type": oData.OIHSN === "Outward" ? "O" : "I",
						"gstr9Table17ListDto": oPayload
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/saveGstr9HSNInwardAndOutwardData.do",
						data: JSON.stringify(request),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts.status === "S") {
							MessageBox.success(data.resp);
							that.onLoadHSNOutward();
							that.onStatusTime();
						} else if (sts.status === "E") {
							MessageBox.error("Invalid HSN");
							// that.setErrorHSNOutward(data.resp);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			} else {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oHsnModel.refresh(true);
			}
		},

		onMoveHsnComputedToUser: function () {
			var that = this;
			MessageBox.alert('Do you want to Copy DigiGST Processed Data to DigiGST User Edited data?', {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onMoveHsnComputedToUserFinal();
					} else {
						return;
					}
				}
			});
		},

		onMoveHsnComputedToUserFinal: function () {
			//eslint-disable-line
			var oBundle = this.getResourceBundle(),
				aIndex = this.byId("idHSNOutwardTab").getSelectedIndices(),
				oHsnModel = this.getView().getModel("oHSNOutModel"),
				oHsnData = oHsnModel.getData(),
				that = this,
				flag = true,
				oPayload = [];
			if (aIndex.length == 0) {
				sap.m.MessageBox.error("Please select at least one record", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			for (var i = 0; i < aIndex.length; i++) {
				if (!oHsnData[aIndex[i]].hsn_sc) {
					flag = false;
					oHsnData[aIndex[i]].hsn_scState = "Error";
					oHsnData[aIndex[i]].hsn_scStateText = oBundle.getText("msgMandatory");
				}

				// if (!oHsnData[aIndex[i]].rt.toString()) {
				// 	flag = false;
				// 	oHsnData[aIndex[i]].rtState = "Error";
				// 	oHsnData[aIndex[i]].rtStateText = oBundle.getText("msgMandatory");
				// }

				// if (!oHsnData[aIndex[i]].uqc) {
				// 	flag = false;
				// 	oHsnData[aIndex[i]].uqcState = "Error";
				// 	oHsnData[aIndex[i]].uqcStateText = oBundle.getText("msgMandatory");
				// }
				//  && oHsnData[aIndex[i]].visi
				if (flag) {
					oPayload.push(this._getHsnSavePayload(oHsnData[aIndex[i]], 'Comp'));

				}
			}
			if (flag && oPayload.length > 0) {
				var oData = this.getView().getModel("oVisiTab").getData();

				var request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText(),
						"type": oData.OIHSN === "Outward" ? "O" : "I",
						"gstr9Table17ListDto": oPayload
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/copyGstr9HSNInwardAndOutwardData.do",
						data: JSON.stringify(request),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts.status === "S") {
							MessageBox.success(data.resp);
							that.onLoadHSNOutward();
							that.onStatusTime();
						} else if (sts.status === "E") {
							MessageBox.error("Invalid HSN");
							// that.setErrorHSNOutward(data.resp);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
			} else {
				sap.m.MessageBox.error(oBundle.getText("errorOccured"), {
					styleClass: "sapUiSizeCompact"
				});
				oHsnModel.refresh(true);
			}
		},

		//==================================================================================//
		//================================ end of HSN Outward =============================//
		//================================================================================//

		//=============================== Start by chaithra on 27/4/2021 ==============================//
		//================================= Differential Tax =========================================//
		//===========================================================================================//
		onloadDiffTax: function () {
			var that = this;
			that.Diffchanged = false;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9DiffTax.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request),
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status == "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
							data.resp[i].diffTaxableVal = (Number(data.resp[i].userInputTaxPayble) - Number(data.resp[i].gstinTaxPayble)).toFixed(2);
							data.resp[i].diffTaxPaid = (Number(data.resp[i].userInputTaxPaid) - Number(data.resp[i].gstinTaxPaid)).toFixed(2);
						}
						if (data.resp.length != 0) {
							data.resp.unshift({
								"subSection": "14"
							});
						}
						that.getView().setModel(new JSONModel(data.resp), "oDiffTaxModel");
						that.getView().getModel("oDiffTaxModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		///////////////// on press edit button //////////////
		onEditDiffTax: function () {
			var that = this;
			var aData = that.getView().getModel("oDiffTaxModel").getData();
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							for (var i = 0; i < aData.length; i++) {
								aData[i].edit = true;
							}
							that.getView().getModel("oDiffTaxModel").refresh(true);
						}
					}
				});
		},

		////////////////// on change Diff tax payble /////////////
		onChangeDiffPay: function (oEvt) {
			var that = this;
			that.Diffchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDiffTaxModel");
			var oBject = oView.getModel("oDiffTaxModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputTaxPayble = val;
			oBject.diffTaxableVal = (Number(oBject.userInputTaxPayble) - Number(oBject.gstinTaxPayble)).toFixed(2);
			that.getView().getModel("oDiffTaxModel").refresh();
		},

		///////////////// on change Diff tax Paid ///////////////
		onChangeDiffPaid: function (oEvt) {
			var that = this;
			that.Diffchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDiffTaxModel");
			var oBject = oView.getModel("oDiffTaxModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputTaxPaid = val;
			oBject.diffTaxPaid = (Number(oBject.userInputTaxPaid) - Number(oBject.gstinTaxPaid)).toFixed(2);
			that.getView().getModel("oDiffTaxModel").refresh();
		},

		//////////////// on save Diff tax /////////////////////
		onPressSaveChangesDiff9: function () {
			var that = this;
			var oView = this.getView();
			if (that.Diffchanged) {
				var Data = oView.getModel("oDiffTaxModel").getData();
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection != "14") {
						//////////Tax Payable//////////
						var valSgst = Data[j].userInputTaxPayble.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (!valSgst.match(regSgst)) {
							MessageBox.error("Alphabets and special characters are not allowed");
							return;
						}
						//////////Tax Paid//////////
						var valCess = Data[j].userInputTaxPaid.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (!valCess.match(regCess)) {
							MessageBox.error("Alphabets and special characters are not allowed");
							return;
						}
					}
				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesDiff91();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}

		},
		onPressSaveChangesDiff91: function () {
			var that = this;
			var oView = this.getView();
			var Data = oView.getModel("oDiffTaxModel").getData();
			var gstr9DiffTaxDashboard = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "14") {
					var famPenDet = {
						section: "14",
						subSection: Data[j].subSection,
						taxPayable: Data[j].userInputTaxPayble === "" ? "0" : Data[j].userInputTaxPayble.toString(),
						taxPaid: Data[j].userInputTaxPaid === "" ? "0" : Data[j].userInputTaxPaid.toString()
					};
					gstr9DiffTaxDashboard.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "SAVE",
					"userInputList": gstr9DiffTaxDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9DiffTaxUserInputData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data saved successfully");
						that.onloadDiffTax();
						that.onStatusTime();
					} else if (sts.status === "E") {
						MessageBox.error(data.resp);
						// MessageBox.success("Data saved successfully");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		//===================================== ended by chaithra on 27/4/2021 =======================//
		//======================================= Differential Tax ===================================//
		//============================================================================================//
		//=============================== Start by chaithra on 27/4/2021 ==============================//
		//================================= Demands and Refunds ======================================//
		//===========================================================================================//
		onloadDemandRefTax: function () {
			var that = this;
			that.DeRefchanged = false;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9DemandsAndRefunds.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request),
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status == "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
							data.resp[i].interestvisi = false;
							data.resp[i].penaltyvisi = false;
							data.resp[i].latefeevisi = false;
							data.resp[i].diffIgst = (Number(data.resp[i].igstUserInput) - Number(data.resp[i].igstGstn)).toFixed(2);
							data.resp[i].diffCgst = (Number(data.resp[i].cgstUserInput) - Number(data.resp[i].cgstGstn)).toFixed(2);
							data.resp[i].diffSgst = (Number(data.resp[i].sgstUserInput) - Number(data.resp[i].sgstGstn)).toFixed(2);
							data.resp[i].diffCess = (Number(data.resp[i].cessUserInput) - Number(data.resp[i].cessGstn)).toFixed(2);
							data.resp[i].diffInterest = (Number(data.resp[i].interestUserInput) - Number(data.resp[i].interestGstn)).toFixed(2);
							data.resp[i].diffPenalty = (Number(data.resp[i].penaltyUserInput) - Number(data.resp[i].penaltyGstn)).toFixed(2);
							data.resp[i].diffLatefee = (Number(data.resp[i].lateFeeUserInput) - Number(data.resp[i].lateFeeGstn)).toFixed(2);
							if (data.resp[i].subSection == "15A" || data.resp[i].subSection == "15B" ||
								data.resp[i].subSection == "15C" || data.resp[i].subSection == "15D") {
								data.resp[i].interestvisi = true;
								data.resp[i].penaltyvisi = true;
								data.resp[i].latefeevisi = true;
								data.resp[i].interestUserInput = "";
								data.resp[i].penaltyUserInput = "";
								data.resp[i].lateFeeUserInput = "";
								// data.resp[i].diffInterest	   = "";
								// data.resp[i].diffPenalty	   = "";
								// data.resp[i].diffLatefee	   = "";
							}
						}
						if (data.resp.length != 0) {
							data.resp.unshift({
								"subSection": "15",
								"interestvisi": true,
								"penaltyvisi": true,
								"latefeevisi": true
							});
						}
						that.getView().setModel(new JSONModel(data.resp), "oDemandRefModel");
						that.getView().getModel("oDemandRefModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		///////////////// on press edit button //////////////
		onEditDemRef: function () {
			var that = this;
			var aData = that.getView().getModel("oDemandRefModel").getData();
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							for (var i = 0; i < aData.length; i++) {
								aData[i].edit = true;
							}
							that.getView().getModel("oDemandRefModel").refresh(true);
						}
					}
				});
		},

		////////////////// on change  IGST /////////////
		onChangeDRIgst: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.igstUserInput = val;
			oBject.diffIgst = (Number(oBject.igstUserInput) - Number(oBject.igstGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change CGST ///////////////
		onChangeDRCgst: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cgstUserInput = val;
			oBject.diffCgst = (Number(oBject.cgstUserInput) - Number(oBject.cgstGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change SGST ///////////////
		onChangeDRSgst: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.sgstUserInput = val;
			oBject.diffSgst = (Number(oBject.sgstUserInput) - Number(oBject.sgstGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change Cess ///////////////
		onChangeDRCess: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cessUserInput = val;
			oBject.diffCess = (Number(oBject.cessUserInput) - Number(oBject.cessGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change  Interest///////////////
		onChangeDRInterest: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.interestUserInput = val;
			oBject.diffInterest = (Number(oBject.interestUserInput) - Number(oBject.interestGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change Penalty ///////////////
		onChangeDRPenalty: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.penaltyUserInput = val;
			oBject.diffPenalty = (Number(oBject.penaltyUserInput) - Number(oBject.penaltyGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		///////////////// on change Latefee ///////////////
		onChangeDRLatefee: function (oEvt) {
			var that = this;
			that.DemdRefchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oDemandRefModel");
			var oBject = oView.getModel("oDemandRefModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.lateFeeUserInput = val;
			oBject.diffLatefee = (Number(oBject.lateFeeUserInput) - Number(oBject.lateFeeGstn)).toFixed(2);
			that.getView().getModel("oDemandRefModel").refresh();
		},

		//////////////// on save Diff tax /////////////////////
		onPressSaveChangesDR9: function () {
			var that = this;
			var oView = this.getView();
			if (that.DemdRefchanged) {
				var Data = oView.getModel("oDemandRefModel").getData();
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection != "15") {
						////////// IGST //////////
						var valSgst = Data[j].igstUserInput.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valSgst != "") {
							if (!valSgst.match(regSgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// CGST //////////
						var valCess = Data[j].cgstUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// SGST //////////
						var valCess = Data[j].sgstUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}

						////////// Cess //////////
						var valCess = Data[j].cessUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}

						////////// Interest //////////
						var valCess = Data[j].interestUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// Penalty //////////
						var valCess = Data[j].penaltyUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// LateFee //////////
						var valCess = Data[j].lateFeeUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
					}
				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesDR91();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}

		},
		onPressSaveChangesDR91: function () {
			var that = this;
			var oView = this.getView();
			var Data = oView.getModel("oDemandRefModel").getData();
			var gstr9DemRefDashboard = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "15") {
					var famPenDet = {
						section: "15",
						subSection: Data[j].subSection,
						// interest: Data[j].interestUserInput === "" ? "0" : Data[j].interestUserInput.toString(),
						// lateFee: Data[j].penaltyUserInput === "" ? "0" : Data[j].penaltyUserInput.toString(),
						// penalty: Data[j].lateFeeUserInput === "" ? "0" : Data[j].lateFeeUserInput.toString(),
						igst: Data[j].igstUserInput === "" ? "0" : Data[j].igstUserInput.toString(),
						cgst: Data[j].cgstUserInput === "" ? "0" : Data[j].cgstUserInput.toString(),
						sgst: Data[j].sgstUserInput === "" ? "0" : Data[j].sgstUserInput.toString(),
						cess: Data[j].cessUserInput === "" ? "0" : Data[j].cessUserInput.toString()
					};
					gstr9DemRefDashboard.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "SAVE",
					"userInputList": gstr9DemRefDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9DemandAndRefundUserInputData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data saved successfully");
						that.onloadDemandRefTax();
						that.onStatusTime();
					} else if (sts.status === "E") {
						MessageBox.error(data.resp);
						// MessageBox.success("Data saved successfully");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		//===================================== ended by chaithra on 27/4/2021 =======================//
		//======================================= Demands and Refunds ================================//
		//============================================================================================//

		//=============================== Start by chaithra on 27/4/2021 ==============================//
		//================================= Composition and Deemed ======================================//
		//===========================================================================================//
		onloadCompDmdTax: function () {
			var that = this;
			that.CompDeemedchanged = false;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getCompositionDeemedSupply.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request),
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status == "S") {
						for (var i = 0; i < data.resp.length; i++) {
							data.resp[i].edit = false;
							data.resp[i].diffIgst = (Number(data.resp[i].igstUserInput) - Number(data.resp[i].igstGstn)).toFixed(2);
							data.resp[i].diffCgst = (Number(data.resp[i].cgstUserInput) - Number(data.resp[i].cgstGstn)).toFixed(2);
							data.resp[i].diffSgst = (Number(data.resp[i].sgstUserInput) - Number(data.resp[i].sgstGstn)).toFixed(2);
							data.resp[i].diffCess = (Number(data.resp[i].cessUserInput) - Number(data.resp[i].cessGstn)).toFixed(2);
							data.resp[i].diffTaxVal = (Number(data.resp[i].taxableValueUserInput) - Number(data.resp[i].taxableValueGstn)).toFixed(2);
							if (data.resp[i].subSection == "16A") {
								data.resp[i].inputIgst = false;
								data.resp[i].inputCgst = false;
								data.resp[i].inputSgst = false;
								data.resp[i].inputCess = false;
							}

						}
						if (data.resp.length != 0) {
							data.resp.unshift({
								"subSection": "16",
								"inputIgst": false,
								"inputCgst": false,
								"inputSgst": false,
								"inputCess": false
							});
						}
						that.getView().setModel(new JSONModel(data.resp), "oCompDeemdModel");
						that.getView().getModel("oCompDeemdModel").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		///////////////// on press edit button //////////////
		onEditCompDeemed: function () {
			var that = this;
			var aData = that.getView().getModel("oCompDeemdModel").getData();
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							for (var i = 0; i < aData.length; i++) {
								aData[i].edit = true;
							}
							that.getView().getModel("oCompDeemdModel").refresh(true);
						}
					}
				});
		},

		////////////////// on change  IGST /////////////
		onChangeCDIgst: function (oEvt) {
			var that = this;
			that.CompDeemedchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oCompDeemdModel");
			var oBject = oView.getModel("oCompDeemdModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.igstUserInput = val;
			oBject.diffIgst = (Number(oBject.igstUserInput) - Number(oBject.igstGstn)).toFixed(2);
			that.getView().getModel("oCompDeemdModel").refresh();
		},

		///////////////// on change CGST ///////////////
		onChangeCDCgst: function (oEvt) {
			var that = this;
			that.CompDeemedchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oCompDeemdModel");
			var oBject = oView.getModel("oCompDeemdModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cgstUserInput = val;
			oBject.diffCgst = (Number(oBject.cgstUserInput) - Number(oBject.cgstGstn)).toFixed(2);
			that.getView().getModel("oCompDeemdModel").refresh();
		},

		///////////////// on change SGST ///////////////
		onChangeCDSgst: function (oEvt) {
			var that = this;
			that.CompDeemedchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oCompDeemdModel");
			var oBject = oView.getModel("oCompDeemdModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.sgstUserInput = val;
			oBject.diffSgst = (Number(oBject.sgstUserInput) - Number(oBject.sgstGstn)).toFixed(2);
			that.getView().getModel("oCompDeemdModel").refresh();
		},

		///////////////// on change Cess ///////////////
		onChangeCDCess: function (oEvt) {
			var that = this;
			that.CompDeemedchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oCompDeemdModel");
			var oBject = oView.getModel("oCompDeemdModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.cessUserInput = val;
			oBject.diffCess = (Number(oBject.cessUserInput) - Number(oBject.cessGstn)).toFixed(2);
			that.getView().getModel("oCompDeemdModel").refresh();
		},

		///////////////// on change Taxable Value ///////////////
		onChangeCDTaxValue: function (oEvt) {
			var that = this;
			that.CompDeemedchanged = true;
			var oView = this.getView();
			var vPath = oEvt.getSource().getBindingContext("oCompDeemdModel");
			var oBject = oView.getModel("oCompDeemdModel").getObject(vPath.sPath);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				//oEvt.getSource().setValue(Number(val).toString());
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.taxableValueUserInput = val;
			oBject.diffTaxVal = (Number(oBject.taxableValueUserInput) - Number(oBject.taxableValueGstn)).toFixed(2);
			that.getView().getModel("oCompDeemdModel").refresh();
		},

		//////////////// on save Diff tax /////////////////////
		onPressSaveChangesCD9: function () {
			var that = this;
			var oView = this.getView();
			if (that.CompDeemedchanged) {
				var Data = oView.getModel("oCompDeemdModel").getData();
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].subSection != "16") {
						////////// IGST //////////
						var valSgst = Data[j].igstUserInput.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valSgst != "") {
							if (!valSgst.match(regSgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// CGST //////////
						var valCess = Data[j].cgstUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// SGST //////////
						var valCess = Data[j].sgstUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// Cess //////////
						var valCess = Data[j].cessUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						////////// Taxable Value //////////
						var valCess = Data[j].taxableValueUserInput.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
					}
				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesCD91();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}

		},
		onPressSaveChangesCD91: function () {
			var that = this;
			var oView = this.getView();
			var Data = oView.getModel("oCompDeemdModel").getData();
			var gstr9CompDeemedDashboard = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].subSection !== "16") {
					var famPenDet = {
						section: "16",
						subSection: Data[j].subSection,
						taxableValue: Data[j].taxableValueUserInput === "" ? "0" : Data[j].taxableValueUserInput.toString(),
						igst: Data[j].igstUserInput === "" ? "0" : Data[j].igstUserInput.toString(),
						cgst: Data[j].cgstUserInput === "" ? "0" : Data[j].cgstUserInput.toString(),
						sgst: Data[j].sgstUserInput === "" ? "0" : Data[j].sgstUserInput.toString(),
						cess: Data[j].cessUserInput === "" ? "0" : Data[j].cessUserInput.toString()
					};
					gstr9CompDeemedDashboard.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"status": "SAVE",
					"userInputList": gstr9CompDeemedDashboard
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9CompDeemedSuppUserInputData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data saved successfully");
						that.onloadCompDmdTax();
						that.onStatusTime();
					} else if (sts.status === "E") {
						MessageBox.error(data.resp);
						// MessageBox.success("Data saved successfully");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		//===================================== ended by chaithra on 27/4/2021 =======================//
		//======================================= Demands and Refunds ================================//
		//============================================================================================//
		onAutoComputePress: function (oevt) {
			var vKey = oevt.getParameter("item").getKey();
			if (vKey == "GSTN") {
				var that = this;
				var Request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText()
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				var GstnsList = "/aspsapapi/getAutoCalcGstnData.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					}).done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success(data.resp);
						} else {
							MessageBox.error(data.resp);
						}
					}).fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
				});
			} else if (vKey == "GSTR13B") {
				var that = this;
				var Request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText()
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				var GstnsList = "/aspsapapi/gstr9ComputeGstr1vs3B.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					}).done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success(data.resp);
						} else {
							MessageBox.error(data.resp);
						}
					}).fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
				});
			} else {
				var that = this;
				var Request = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText()
					}
				};
				sap.ui.core.BusyIndicator.show(0);
				var GstnsList = "/aspsapapi/gstr9ComputeDigiGst.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					}).done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						if (data.hdr.status === "S") {
							MessageBox.success(data.resp);
						} else {
							MessageBox.error(data.resp);
						}
					}).fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					});
				});
			}
		},
		//================= Added By Vinay Kodam 23-04-2021=================//
		//====================PY Table Bind Function=================//
		PYTabBind: function () {
			var that = this;
			that.PYchanged = false;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			var GSTR9TPModel = new JSONModel();
			var oTaxReGstnView = this.getView();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9PyTransCyData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var Data = data.resp.gstr9PyTransCyDashboardData;
					for (var j = 0; j < Data.length; j++) {
						Data[j].editPY9 = false;
						Data[j].diffenceTaxableVal = (Number(Data[j].userInputTaxableVal) - Number(Data[j].gstinTaxableVal)).toFixed(2);
						Data[j].diffenceIGSTVal = (Number(Data[j].userInputIgst) - Number(Data[j].gstinIgst)).toFixed(2);
						Data[j].diffenceCGSTVal = (Number(Data[j].userInputCgst) - Number(Data[j].gstinCgst)).toFixed(2);
						Data[j].diffenceSGSTVal = (Number(Data[j].userInputSgst) - Number(Data[j].gstinSgst)).toFixed(2);
						Data[j].diffenceCESSVal = (Number(Data[j].userInputCess) - Number(Data[j].gstinCess)).toFixed(2);
						Data[j].InputTaxValue = false;
						Data[j].InputIgst = false;
						Data[j].InputCgst = false;
						Data[j].InputSgst = false;
						Data[j].InputCess = false;
						if (Data[j].section == "12" || Data[j].section == "13") {
							Data[j].InputTaxValue = true;
						}
					}
					var Data1 = that.getView().getModel("Gstr9Out").getData();
					var oBject = Data1.find(function (obj) {
						return obj.subSection === "5N";
					})
					Data.push({
						"section": "10B",
						"editPY9": true,
						"InputTaxValue": true,
						"InputIgst": true,
						"InputCgst": true,
						"InputSgst": true,
						"InputCess": true,

						"diffenceTaxableVal": (Number(oBject.diffenceTaxableVal) + Number(Data[0].diffenceTaxableVal) - Number(Data[1].diffenceTaxableVal))
							.toFixed(2),
						"diffenceIGSTVal": (Number(oBject.diffenceIGSTVal) + Number(Data[0].diffenceIGSTVal) - Number(Data[1].diffenceIGSTVal)).toFixed(
							2),
						"diffenceCGSTVal": (Number(oBject.diffenceCGSTVal) + Number(Data[0].diffenceCGSTVal) - Number(Data[1].diffenceCGSTVal)).toFixed(
							2),
						"diffenceSGSTVal": (Number(oBject.diffenceSGSTVal) + Number(Data[0].diffenceSGSTVal) - Number(Data[1].diffenceSGSTVal)).toFixed(
							2),
						"diffenceCESSVal": (Number(oBject.diffenceCESSVal) + Number(Data[0].diffenceCESSVal) - Number(Data[1].diffenceCESSVal)).toFixed(
							2),

						"userInputTaxableVal": (Number(oBject.userInputTaxableVal) + Number(Data[0].userInputTaxableVal) - Number(Data[1].userInputTaxableVal))
							.toFixed(2),
						"userInputIgst": (Number(oBject.userInputIgst) + Number(Data[0].userInputIgst) - Number(Data[1].userInputIgst)).toFixed(
							2),
						"userInputCgst": (Number(oBject.userInputCgst) + Number(Data[0].userInputCgst) - Number(Data[1].userInputCgst)).toFixed(
							2),
						"userInputSgst": (Number(oBject.userInputSgst) + Number(Data[0].userInputSgst) - Number(Data[1].userInputSgst)).toFixed(
							2),
						"userInputCess": (Number(oBject.userInputCess) + Number(Data[0].userInputCess) - Number(Data[1].userInputCess)).toFixed(
							2),

						"gstinTaxableVal": (Number(oBject.gstinTaxableVal) + Number(Data[0].gstinTaxableVal) - Number(Data[1].gstinTaxableVal)).toFixed(
							2),
						"gstinIgst": (Number(oBject.gstinIgst) + Number(Data[0].gstinIgst) - Number(Data[1].gstinIgst)).toFixed(2),
						"gstinCgst": (Number(oBject.gstinCgst) + Number(Data[0].gstinCgst) - Number(Data[1].gstinCgst)).toFixed(2),
						"gstinSgst": (Number(oBject.gstinSgst) + Number(Data[0].gstinSgst) - Number(Data[1].gstinSgst)).toFixed(2),
						"gstinCess": (Number(oBject.gstinCess) + Number(Data[0].gstinCess) - Number(Data[1].gstinCess)).toFixed(2),

						"digiComputeTaxableVal": (Number(oBject.digiComputeTaxableVal) + Number(Data[0].digiComputeTaxableVal) - Number(Data[1].digiComputeTaxableVal))
							.toFixed(
								2),
						"digiComputeIgst": (Number(oBject.digiComputeIgst) + Number(Data[0].digiComputeIgst) - Number(Data[1].digiComputeIgst)).toFixed(
							2),
						"digiComputeCgst": (Number(oBject.digiComputeCgst) + Number(Data[0].digiComputeCgst) - Number(Data[1].digiComputeCgst)).toFixed(
							2),
						"digiComputeSgst": (Number(oBject.digiComputeSgst) + Number(Data[0].digiComputeSgst) - Number(Data[1].digiComputeSgst)).toFixed(
							2),
						"digiComputeCess": (Number(oBject.digiComputeCess) + Number(Data[0].digiComputeCess) - Number(Data[1].digiComputeCess)).toFixed(
							2),

						"computedTaxableVal": (Number(oBject.getCompTaxableVal) + Number(Data[0].computedTaxableVal) - Number(Data[1].computedTaxableVal))
							.toFixed(2),
						"computedIgst": (Number(oBject.getCompIgst) + Number(Data[0].computedIgst) - Number(Data[1].computedIgst)).toFixed(2),
						"computedCgst": (Number(oBject.getCompCgst) + Number(Data[0].computedCgst) - Number(Data[1].computedCgst)).toFixed(2),
						"computedSgst": (Number(oBject.getCompSgst) + Number(Data[0].computedSgst) - Number(Data[1].computedSgst)).toFixed(2),
						"computedCess": (Number(oBject.getCompCess) + Number(Data[0].computedCess) - Number(Data[1].computedCess)).toFixed(2)

					});
					if (Data.length != 0) {
						Data.unshift({
							"section": "10A",
							"InputTaxValue": true,
							"InputIgst": true,
							"InputCgst": true,
							"InputSgst": true,
							"InputCess": true
						});
					}
					GSTR9TPModel.setData(Data);
					oTaxReGstnView.setModel(GSTR9TPModel, "GSTR9PYModel");
					oTaxReGstnView.getModel("GSTR9PYModel").refresh(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onEditPY: function () {
			var that = this;
			var aData = that.getView().getModel("GSTR9PYModel").getData();
			MessageBox.show(
				"Do you want to Edit User Data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							for (var i = 0; i < aData.length; i++) {
								aData[i].editPY9 = true;
							}
							that.getView().getModel("GSTR9PYModel").refresh(true);
						}
					}
				});
		},

		TaxableValueChangePY: function (oEvt) {
			var that = this;
			that.PYchanged = true;
			var oView = this.getView();
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9PYModel");
			var oBject = oView.getModel("GSTR9PYModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputTaxableVal = val;
			var Data1 = that.getView().getModel("Gstr9Out").getData();
			var oBject1 = Data1.find(function (obj) {
				return obj.subSection === "5N";
			});
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceTaxableVal = (Number(Data[j].userInputTaxableVal) - Number(Data[j].gstinTaxableVal)).toFixed(2);
			}
			Data[5].userInputTaxableVal = (Number(oBject1.userInputTaxableVal) + Number(Data[1].userInputTaxableVal) - Number(Data[2].userInputTaxableVal))
				.toFixed(2);
			oView.getModel("GSTR9PYModel").refresh(true);
		},

		IGSTChangPY: function (oEvt) {
			var that = this;
			that.PYchanged = true;
			var oView = this.getView();
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9PYModel");
			var oBject = oView.getModel("GSTR9PYModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputIgst = val;
			var Data1 = that.getView().getModel("Gstr9Out").getData();
			var oBject1 = Data1.find(function (obj) {
				return obj.subSection === "5N";
			});
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceIGSTVal = (Number(Data[j].userInputIgst) - Number(Data[j].gstinIgst)).toFixed(2);
			}
			Data[5].userInputIgst = (Number(oBject1.userInputIgst) + Number(Data[1].userInputIgst) - Number(Data[2].userInputIgst)).toFixed(2);
			oView.getModel("GSTR9PYModel").refresh(true);
		},

		CGSTChangePY: function (oEvt) {
			var that = this;
			that.PYchanged = true;
			var oView = this.getView();
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9PYModel");
			var oBject = oView.getModel("GSTR9PYModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCgst = val;
			var Data1 = that.getView().getModel("Gstr9Out").getData();
			var oBject1 = Data1.find(function (obj) {
				return obj.subSection === "5N";
			});
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceCGSTVal = (Number(Data[j].userInputCgst) - Number(Data[j].gstinCgst)).toFixed(2);
			}
			Data[5].userInputCgst = (Number(oBject1.userInputCgst) + Number(Data[1].userInputCgst) - Number(Data[2].userInputCgst)).toFixed(2);
			oView.getModel("GSTR9PYModel").refresh(true);
		},

		SGSTChangePY: function (oEvt) {
			var that = this;
			that.PYchanged = true;
			var oView = this.getView();
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9PYModel");
			var oBject = oView.getModel("GSTR9PYModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputSgst = val;
			var Data1 = that.getView().getModel("Gstr9Out").getData();
			var oBject1 = Data1.find(function (obj) {
				return obj.subSection === "5N";
			});
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceSGSTVal = (Number(Data[j].userInputSgst) - Number(Data[j].gstinSgst)).toFixed(2);
			}
			Data[5].userInputSgst = (Number(oBject1.userInputSgst) + Number(Data[1].userInputSgst) - Number(Data[2].userInputSgst)).toFixed(2);
			oView.getModel("GSTR9PYModel").refresh(true);
		},

		CESSChangePY: function (oEvt) {
			var that = this;
			that.PYchanged = true;
			var oView = this.getView();
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var vPath = oEvt.getSource().getBindingContext("GSTR9PYModel");
			var oBject = oView.getModel("GSTR9PYModel").getObject(vPath.sPath);
			//var index = oEvt.getSource().getParent().getParent().getBindingContext("Gstr9Out").sPath.slice(1, 2);
			var val = oEvt.getSource().getValue().toString();
			var val1 = val.split(".");
			if (val1[1] !== undefined) {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
				} else {
					oEvt.getSource().setValue(Number(val1[0]) + "." + val1[1].slice(0, 2));
				}
			} else {
				if (val1[0].length > 17) {
					MessageBox.error("More than 17 digits are not allowed");
					return;
				}
			}
			// var reg = /^[0-9]*\.?[0-9]*$/;
			var reg = /^-?[0-9]\d*(\.\d+)?$/;
			if (!val.match(reg)) {
				MessageBox.error("Alphabets and special characters are not allowed");
				return;
			}
			oBject.userInputCess = val;
			var Data1 = that.getView().getModel("Gstr9Out").getData();
			var oBject1 = Data1.find(function (obj) {
				return obj.subSection === "5N";
			});
			for (var j = 0; j < Data.length; j++) {
				Data[j].diffenceCESSVal = (Number(Data[j].userInputCess) - Number(Data[j].gstinCess)).toFixed(2);
			}
			Data[5].userInputCess = (Number(oBject1.userInputCess) + Number(Data[1].userInputCess) - Number(Data[2].userInputCess)).toFixed(2);
			oView.getModel("GSTR9PYModel").refresh(true);
		},

		onPressSaveChangesPY: function () {
			var that = this;
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			if (that.PYchanged) {
				for (var j = 0; j < Data.length; j++) {
					if (Data[j].section !== "10A" && Data[j].section !== "10B") {
						///////Taxvalue//////
						if (Data[j].userInputTaxableVal != undefined) {
							var val = Data[j].userInputTaxableVal.toString();
							var val1 = val.split(".");
							if (val1[1] !== undefined) {
								if (val1[0].length > 17) {
									MessageBox.error("More than 17 digits are not allowed");
									return;
								}
								if (val1[1] !== "" && val1[1].length > 2) {
									MessageBox.error("More than 2 decimals are not allowed");
									return;
								}
							} else {
								if (val1[0].length > 17) {
									MessageBox.error("More than 17 digits are not allowed");
									return;
								}
							}
							// var reg = /^[0-9]*\.?[0-9]*$/;
							var reg = /^-?[0-9]\d*(\.\d+)?$/;
							if (val != "") {
								if (!val.match(reg)) {
									MessageBox.error("Alphabets and special characters are not allowed");
									return;
								}
							}
						}

						var valIgst = Data[j].userInputIgst.toString();
						var val1Igst = valIgst.split(".");
						if (val1Igst[1] !== undefined) {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Igst[1] !== "" && val1Igst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Igst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regIgst = /^[0-9]*\.?[0-9]*$/;
						var regIgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valIgst != "") {
							if (!valIgst.match(regIgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////CGST//////////
						var valCgst = Data[j].userInputCgst.toString();
						var val1Cgst = valCgst.split(".");
						if (val1Cgst[1] !== undefined) {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cgst[1] !== "" && val1Cgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCgst = /^[0-9]*\.?[0-9]*$/;
						var regCgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCgst != "") {
							if (!valCgst.match(regCgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////SGST//////////
						var valSgst = Data[j].userInputSgst.toString();
						var val1Sgst = valSgst.split(".");
						if (val1Sgst[1] !== undefined) {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Sgst[1] !== "" && val1Sgst[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Sgst[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regSgst = /^[0-9]*\.?[0-9]*$/;
						var regSgst = /^-?[0-9]\d*(\.\d+)?$/;
						if (valSgst != "") {
							if (!valSgst.match(regSgst)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
						//////////Cess//////////
						var valCess = Data[j].userInputCess.toString();
						var val1Cess = valCess.split(".");
						if (val1Cess[1] !== undefined) {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
							if (val1Cess[1] !== "" && val1Cess[1].length > 2) {
								MessageBox.error("More than 2 decimals are not allowed");
								return;
							}
						} else {
							if (val1Cess[0].length > 17) {
								MessageBox.error("More than 17 digits are not allowed");
								return;
							}
						}
						// var regCess = /^[0-9]*\.?[0-9]*$/;
						var regCess = /^-?[0-9]\d*(\.\d+)?$/;
						if (valCess != "") {
							if (!valCess.match(regCess)) {
								MessageBox.error("Alphabets and special characters are not allowed");
								return;
							}
						}
					}
				}
				var that = this;
				MessageBox.alert('Do you want to Save Changes?', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (sAction) {
						if (sAction === "YES") {
							that.onPressSaveChangesPY1();
						} else {
							return;
						}
					}
				});
			} else {
				MessageBox.error('Atleast edit one user data to save changes', {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.OK],
					onClose: function (sAction) {}
				});
			}
		},

		onPressSaveChangesPY1: function () {
			var Data = this.getView().getModel("GSTR9PYModel").getData();
			var userInputs = [];
			for (var j = 0; j < Data.length; j++) {
				if (Data[j].section !== "10A" && Data[j].section !== "10B") {
					var famPenDet = {
						section: Data[j].section,
						taxableVal: Data[j].userInputTaxableVal === "" ? "0" : Data[j].userInputTaxableVal == undefined ? undefined : Data[j].userInputTaxableVal
							.toString(),
						igst: Data[j].userInputIgst === "" ? "0" : Data[j].userInputIgst.toString(),
						cgst: Data[j].userInputCgst === "" ? "0" : Data[j].userInputCgst.toString(),
						sgst: Data[j].userInputSgst === "" ? "0" : Data[j].userInputSgst.toString(),
						cess: Data[j].userInputCess === "" ? "0" : Data[j].userInputCess.toString()
					};
					userInputs.push(famPenDet);
				}
			}
			var request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText(),
					"userInputs": userInputs
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/saveGstr9PyTransCyData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts.status === "S") {
						MessageBox.success("Data Saved Successfully");
						that.PYTabBind();
						that.onStatusTime();
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//========================== on click of Save to GSTN button ==================//
		onPressSaveToGSTN: function () {
			var that = this;
			MessageBox.confirm("Do you want to initiate Save to GSTN ?", {
				title: "Confirm",
				actions: [sap.m.MessageBox.Action.YES,
					sap.m.MessageBox.Action.NO
				],
				onClose: function (oAction) {
					if (oAction == "NO") {
						return;
					} else if (oAction == "YES") {
						if (that.authState === "I") {
							MessageBox.show(
								"Auth token is inactive for selected GSTIN, please activate and retry.", {
									icon: MessageBox.Icon.WARNING,
									title: "Warning"
								});
							return;
						}
						if (that.flag) {
							var Request = {
								"req": {
									"gstin": that.getView().byId("idGstinDetail").getText(),
									"fy": that.getView().byId("idFyGstinDetail").getText()
								}
							};
							sap.ui.core.BusyIndicator.show(0);
							var GstnsList = "/aspsapapi/gstr9SaveToGstn.do";
							$(document).ready(function ($) {
								$.ajax({
									method: "POST",
									url: GstnsList,
									contentType: "application/json",
									data: JSON.stringify(Request)
								}).done(function (data, status, jqXHR) {
									sap.ui.core.BusyIndicator.hide();
									if (data.hdr.status === "S") {
										MessageBox.success(data.resp);
									} else {
										MessageBox.error(data.resp);
									}
									that.onStatusTime();
								}).fail(function (jqXHR, status, err) {
									sap.ui.core.BusyIndicator.hide();
								});
							});
						} else {
							MessageBox.error("Zero eligible documents found to perform Gstr9 Save to Gstn");
							return;
						}
					}
				},
			});

		},
		//========================== on click of Save Status button ==================//
		onSaveStatus9: function () {
			if (!this._oDialogSaveStats9) {
				this._oDialogSaveStats9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr9.Save9", this);
				this.getView().addDependent(this._oDialogSaveStats9);
			}
			this.byId("GSTINSaveID9").setSelectedKey(this.byId("idGstinDetail").getText());
			this.byId("gstr9SaveFY").setSelectedKey(this.byId("idFyGstinDetail").getText());
			this.onSaveOkay();
			this._oDialogSaveStats9.open();
		},

		onCloseDialogSave: function () {
			this._oDialogSaveStats9.close();
		},

		onSaveOkay: function () {
			var that = this;
			var Request = {
				"req": {
					"gstin": this.byId("GSTINSaveID9").getSelectedKey(),
					"fy": this.byId("gstr9SaveFY").getSelectedKey(),
				}
			};
			var GstinSaveModel = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9SaveStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					for (var i = 0; i < data.resp.details.length; i++) {
						data.resp.details[i].sNo = i + 1;
					}
					GstinSaveModel.setData(data.resp);
					that.getView().byId("idTableSave9").setModel(GstinSaveModel, "GstinSaveModel9");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onSaveStatusDownload: function (oEvt) {
			this.oReqId = oEvt.getSource().getParent().getBindingContext("GstinSaveModel9").getObject().id;
			var oReqExcelPath = "/aspsapapi/downloadGstr9SaveResponse.do?id=" + this.oReqId + "";
			window.open(oReqExcelPath);
		},

		onStatusTime: function () {
			var that = this;
			var Request = {
				"req": {
					"gstin": this.getView().byId("idGstinDetail").getText(),
					"fy": this.getView().byId("idFyGstinDetail").getText()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGstr9StatusTimeStamps.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.flag = data.resp.isDataAvlble;
					that.byId("saveSId").setText(data.resp.saveToGstnTimeStamp);
					that.byId("updateId").setText(data.resp.updateGstnTimeStamp);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//==========================================================================================================//
		//================================= Download Report GSTIN level ===========================================//
		//========================================================================================================//
		onDownloadReport: function (oEvt) {
			var key = oEvt.getParameters().item.getKey();
			if (key === "A") {
				var oEntityPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText()
					}
				};
				var vUrl = "/aspsapapi/gstr9ReportDownload.do";
				this.excelDownload(oEntityPayload, vUrl);
			} else {
				var oEntityPayload1 = {
					"req": {
						"gstin": this.getView().byId("idGstinDetail").getText(),
						"fy": this.getView().byId("idFyGstinDetail").getText()
					}
				};
				var vUrl = "/aspsapapi/getDumpReportsDetails.do";
				this.reportDownload(oEntityPayload1, vUrl);
			}
		},
		//========================================================================================================//
		//====================================== Download PDF ===================================================//
		//======================================================================================================//
		onDownloadPDF: function (view, flag) {
			var gstin = this.getView().byId("idGstinDetail").getText();
			var fy = this.getView().byId("idFyGstinDetail").getText();
			if (this.flag) {
				// var oReqExcelPath = "/aspsapapi/generateGstr9SummaryPDFReport.do?gstin=" + gstin + "&fy=" + fy + "&isDigigst=" + flag + "";
				// window.open(oReqExcelPath);

				var oPayload = {
					"req": {
						"gstin": gstin,
						"fy": fy,
						"isDigigst": flag
					}
				};
				this.excelDownload(oPayload, "/aspsapapi/generateGstr9SummaryPDFReport.do");
			} else {
				MessageBox.error("No data available to download");
				return;
			}
		},
		//=============================================================================================================//
		//============================== Copy data to User input =====================================================//
		//===========================================================================================================//
		onCopyData: function () {
			if (!this._oDialogCopy) {
				this._oDialogCopy = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr9.CopyData", this);
				this.getView().addDependent(this._oDialogCopy);
			}
			this._oDialogCopy.open();
		},
		onCloseDialogCopy: function (oEvent) {
			this._oDialogCopy.close();
		},
		onDialogCopySubmit: function (oEvent) {
			//esint-disable-line
			var that = this;
			var vIndex = this.byId("rbgCopyType").getSelectedIndex();
			this._oDialogCopy.close();
			if (vIndex == 0) {
				var Request = {
					"req": {
						"gstin": that.getView().byId('idGstinDetail').getText(),
						"fy": that.getView().byId('idFyGstinDetail').getText()
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				var GstnsList = "/aspsapapi/gstr9CopyCompData.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					}).done(function (data, status, jqXHR) {

						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts !== undefined) {
							if (sts.status === "S") {
								MessageBox.success(data.resp);
								var oData = that.getView().getModel("oVisiTab").getData();
								if (oData.Out == true) {
									that.OutwardTabBind();
								} else if (oData.In == true) {
									that.loadInwardData();
								} else if (oData.Tax == true) {
									that.TaxPaidTabBind();
								} else if (oData.PY == true) {
									that.PYTabBind();
								} else if (oData.Diff == true) {
									that.onloadDiffTax();
								} else if (oData.DemR == true) {
									that.onloadDemandRefTax();
								} else if (oData.Comp == true) {
									that.onloadCompDmdTax();
								} else if (oData.HSN == true) {
									that.onLoadHSNSummary();
								}
							} else if (sts.status === "E") {
								MessageBox.error(data.resp);

							}
						} else {
							MessageBox.error(JSON.parse(data).hdr.message);
						}
					}).fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						//MessageBox.error("Error Occured While Saving the Changes");
					});
				});
			} else {
				var Request = {
					"req": {
						"gstin": that.getView().byId('idGstinDetail').getText(),
						"fy": that.getView().byId('idFyGstinDetail').getText()
					}
				};

				sap.ui.core.BusyIndicator.show(0);
				var GstnsList = "/aspsapapi/gstr9CopyAutoCompData.do";
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: GstnsList,
						contentType: "application/json",
						data: JSON.stringify(Request)
					}).done(function (data, status, jqXHR) {

						sap.ui.core.BusyIndicator.hide();
						var sts = data.hdr;
						if (sts !== undefined) {
							if (sts.status === "S") {
								MessageBox.success(data.resp);
								var oData = that.getView().getModel("oVisiTab").getData();
								if (oData.Out == true) {
									that.OutwardTabBind();
								} else if (oData.In == true) {
									that.loadInwardData();
								} else if (oData.Tax == true) {
									that.TaxPaidTabBind();
								} else if (oData.PY == true) {
									that.PYTabBind();
								} else if (oData.Diff == true) {
									that.onloadDiffTax();
								} else if (oData.DemR == true) {
									that.onloadDemandRefTax();
								} else if (oData.Comp == true) {
									that.onloadCompDmdTax();
								} else if (oData.HSN == true) {
									that.onLoadHSNSummary();
								}
							} else if (sts.status === "E") {
								MessageBox.error(data.resp);

							}
						} else {
							MessageBox.error(JSON.parse(data).hdr.message);
						}
					}).fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						//MessageBox.error("Error Occured While Saving the Changes");
					});
				});
			}
		},

		onPressClear9: function () {
			this.byId("gstr9FY").setSelectedKey("2020");
			this.byId("GSTINEntityID").setSelectedKeys();
			this.EntityTabBind();
		},
		//========================== OTP generation ============================//
		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("SummTab").getObject();
			if (oValue1.auth !== "I") {
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
		onPressYes: function () {
			//  //eslint-disable-line
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
		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},
		onPressCancel: function () {
			this._dAuthToken.close();
		},
		//============================ end of OTP Generation =============================//
	});
});