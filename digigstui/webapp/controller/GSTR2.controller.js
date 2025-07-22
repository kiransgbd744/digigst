sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/core/util/Export",
	"sap/m/Button",
	"sap/m/Dialog",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/m/Token"
], function (BaseController, Formatter, JSONModel, MessageBox, Filter, FilterOperator, Export, Button, Dialog, ExportTypeCSV, Token) {
	"use strict";

	var vCount = 10;
	var vCount1 = 100;
	return BaseController.extend("com.ey.digigst.controller.GSTR2", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTR2
		 */
		onInit: function () {
			var that = this,
				vDate = new Date(),
				date = new Date(),
				date1 = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1),
				vEmailDateVC = new Date();

			date.setDate(date.getDate() - 9);
			date1.setMonth(date1.getMonth() - 1);
			vPeriod.setDate(1);
			this.VMGSTINarr = [];
			this.GSTINarr = [];
			this.Panarr = [];
			// Added by Bharat Gupta - 01.04.2020
			this.bindPrDefaultValue(date, vPeriod); // PR Summary Default Values
			this.bindGetGstr2aValue(); // Get GSTR-2A Default Values

			this._bindPrSummaryProperty(); // PR Summary Property
			this._bindGetGstr2aProperty(); // Get GSTR-2A Property
			this._bindGstr2bSummaryProperty();
			// End of Code by Bharat

			//========= Added by chaithra on 02/11/2020 ============//
			var object = {
				"Label": "2A/6A",
				"TAX": true,
				"DOC": false,
				"Label2": "2A/6A",
				"LabelVc": "2A/6A",
				"LabelVc1": "2A",
				"LabelRR": "2A/6A",
				"LabelRR1": "2A",
				"Criteria": "Criteria",
				"RRVis": true,
				"RRIVis": true,
				"ITCVis": true,
				"LabelRRe": "2A/6A",
				"LabelRRe1": "2A",
				"A2": false,
				"PR": true,
				"SendAll": true,
				"sbGstr2B": "getGstr2b",
				"swRecon": false,
				"inPageNoRRL": 0,
				"pgTotalRRL": 0,
				"pgSizeRRL": 10,
				"ePageNoRRL": false,
				"bFirstRRL": false,
				"bPrevRRL": false,
				"bNextRRL": false,
				"bLastRRL": false,
				"inPageNoRR": 0,
				"pgTotalRR": 0,
				"pgSizeRR": 10,
				"ePageNoRR": false,
				"bFirstRR": false,
				"bPrevRR": false,
				"bNextRR": false,
				"bLastRR": false,
				"imsResponse": false
			};
			this.getView().setModel(new JSONModel(object), "Display");

			if (!this.ReconRspAdd) {
				this.ReconRspAdd = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRAdditionalFilter", this);
				this.getView().addDependent(this.ReconRspAdd);
			}

			if (!this.ReconRspAdd2AB) {
				this.ReconRspAdd2AB = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRAdditionalFilter2AB", this);
				this.getView().addDependent(this.ReconRspAdd2AB);
			}

			this.byId("idEmailVenPAN").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idEmailVenPAN").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idEmailVenGstn").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idEmailVenGstn").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idVUVendrGstn").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idVUVendrGstn").$().find("input").attr("readonly", true);
				}
			});

			//========= code ended by chaithra =====================//

			this.gsr2bDefaultValue();
			this.byId("idInitiateReconPeriod2A").setMinDate(new Date("2017", "05", "01"));
			//this.byId("idInitiateReconPeriod2A").setMinDate(date1);
			this.byId("idInitiateReconPeriod2A").setMaxDate(vDate);
			this.byId("idInitiateReconPeriod2A").setDateValue(vPeriod);
			this.byId("idInitiateReconPeriod2A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriod2A").$().find("input").attr("readonly", true);
				}

			});

			//this.byId("idInitiateReconPeriod12A").setMinDate(new Date("2017", "05", "01"));
			this.byId("idInitiateReconPeriod12A").setMinDate(vPeriod);
			this.byId("idInitiateReconPeriod12A").setMaxDate(vDate);
			this.byId("idInitiateReconPeriod12A").setDateValue(vDate);
			this.byId("idInitiateReconPeriod12A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriod12A").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idInitiateReconPeriodTax2A").setMinDate(new Date("2017", "05", "01"));
			this.byId("idInitiateReconPeriodTax2A").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax2A").setDateValue(vPeriod);
			this.byId("idInitiateReconPeriodTax2A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax2A").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idInitiateReconPeriodTax12A").setMinDate(vPeriod);
			this.byId("idInitiateReconPeriodTax12A").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax12A").setDateValue(vDate);
			this.byId("idInitiateReconPeriodTax12A").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax12A").$().find("input").attr("readonly", true);
				}
			});

			//=============================================================================================================================================//			
			//=========== 2A Tax period  added by chaithra on 2/11/2020===============//
			//=============================================================================================================================================//
			this.byId("idInitiateReconPeriodTax2A1").setMinDate(new Date("2017", "05", "01"));
			this.byId("idInitiateReconPeriodTax2A1").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax2A1").setDateValue(vPeriod);
			this.byId("idInitiateReconPeriodTax2A1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax2A1").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idInitiateReconPeriodTax12A1").setMinDate(vPeriod);
			this.byId("idInitiateReconPeriodTax12A1").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax12A1").setDateValue(vDate);
			this.byId("idInitiateReconPeriodTax12A1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax12A1").$().find("input").attr("readonly", true);
				}
			});

			//=============================================================================================================================================//
			//=========== code ended by chaithra on 2/11/2020	=====================//
			//=============================================================================================================================================//
			this.byId("idRSFrom").setMaxDate(vDate);
			this.byId("idRSFrom").setDateValue(vPeriod);
			this.byId("idRSFrom").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idRSFrom").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idRSTo").setMinDate(vPeriod);
			this.byId("idRSTo").setMaxDate(vDate);
			this.byId("idRSTo").setDateValue(vDate);
			this.byId("idRSTo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idRSTo").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idEmailFrmDate").setMaxDate(vDate);
			this.byId("idEmailFrmDate").setDateValue(vPeriod);
			this.byId("idEmailFrmDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idEmailFrmDate").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idEmailToDate").setMinDate(vPeriod);
			this.byId("idEmailToDate").setMaxDate(vDate);
			this.byId("idEmailToDate").setDateValue(vDate);
			this.byId("idEmailToDate").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idEmailToDate").$().find("input").attr("readonly", true);
				}
			});

			// Set Readonly and value for  Recon Result Date Picker
			this._setReadonlyDate("dtConsldFrom", vPeriod, null, vDate);
			this._setReadonlyDate("dtConsld1To", vDate, vPeriod, vDate);
			this._setReadonlyDate("dtDocFrom", null, null, vDate);
			this._setReadonlyDate("dtDocTo", null, null, vDate);
			this._setReadonlyDate("dFrPeriod3b", null, null, vDate);
			this._setReadonlyDate("dToPeriod3b", null, null, vDate);
			this._setTokenValidator("reconDocnumber");
			this._setTokenValidator("iAccVocNo");
			this._setTokenValidator("iVendorGstin");
			this._setTokenValidator("reconDocnumber1s");
			this._setTokenValidator("reconDocnumber1ss");
			this._setTokenValidator("idERRVenPAN");
			this._setTokenValidator("idERRVenPAN1");
			this._setTokenValidator("idRRVenGstn");
			this._setTokenValidator("idRRVenGstn1");

			this.byId("idInitiateReconPeriodTax2A_From").setDateValue(vPeriod);
			this.byId("idInitiateReconPeriodTax2A_From").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax2A_From").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax2A_From").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idInitiateReconPeriodTax2A_To").setMinDate(vPeriod);
			this.byId("idInitiateReconPeriodTax2A_To").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax2A_To").setDateValue(vDate);
			this.byId("idInitiateReconPeriodTax2A_To").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax2A_To").$().find("input").attr("readonly", true);
				}
			});

			// this.byId("dtPI").setMinDate(new Date("2019", "12", "01")); //vDate
			this.byId("dtPI").setMaxDate(new Date("2021", "11", "01"));
			this.byId("dtPI").setDateValue(new Date("2021", "11", "01")); //vPeriod
			this.byId("dtPI").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtPI").$().find("input").attr("readonly", true);
				}
			});

			// this.byId("dtPITo").setMinDate(new Date("2021", "12", "01"));//vPeriod
			this.byId("dtPITo").setMaxDate(new Date("2021", "11", "01")); //vDate
			this.byId("dtPITo").setDateValue(new Date("2021", "11", "01")); //vDate
			this.byId("dtPITo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtPITo").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtPIDet").setMaxDate(vDate);
			this.byId("dtPIDet").setDateValue(vPeriod);
			this.byId("dtPIDet").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtPIDet").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtPIDetTo").setMinDate(vPeriod);
			this.byId("dtPIDetTo").setMaxDate(vDate);
			this.byId("dtPIDetTo").setDateValue(vDate);
			this.byId("dtPIDetTo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtPIDetTo").$().find("input").attr("readonly", true);
				}
			});

			this.byId("gstr2bDateFrom").setMaxDate(vDate);
			this.byId("gstr2bDateFrom").setDateValue(vPeriod);
			this.byId("gstr2bDateFrom").addEventDelegate({
				onAfterRendering: function () {
					that.byId("gstr2bDateFrom").$().find("input").attr("readonly", true);
				}
			});

			this.byId("gstr2bDateTo").setMinDate(vPeriod);
			this.byId("gstr2bDateTo").setMaxDate(vDate);
			this.byId("gstr2bDateTo").setDateValue(vDate);
			this.byId("gstr2bDateTo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("gstr2bDateTo").$().find("input").attr("readonly", true);
				}
			});

			this.byId("FromDateDet").setMaxDate(vDate);
			this.byId("FromDateDet").setDateValue(vPeriod);
			this.byId("FromDateDet").addEventDelegate({
				onAfterRendering: function () {
					that.byId("FromDateDet").$().find("input").attr("readonly", true);
				}
			});

			//this.byId("ToDateDet").setMinDate(vPeriod);
			this.byId("ToDateDet").setMaxDate(vDate);
			this.byId("ToDateDet").setDateValue(vDate);
			this.byId("ToDateDet").addEventDelegate({
				onAfterRendering: function () {
					that.byId("ToDateDet").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idVRdate").setMaxDate(vDate);

			this.byId("dtConsldFrom1").setMaxDate(vDate);
			this.byId("dtConsldFrom1").setDateValue(vPeriod);
			this.byId("dtConsldFrom1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtConsldFrom1").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtConsld1To1").setMinDate(vPeriod);
			this.byId("dtConsld1To1").setMaxDate(vDate);
			this.byId("dtConsld1To1").setDateValue(vDate);
			this.byId("dtConsld1To1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtConsld1To1").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtConsldFrom1s").setMaxDate(vDate);
			this.byId("dtConsldFrom1s").setDateValue(vPeriod);
			this.byId("dtConsldFrom1s").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtConsldFrom1s").$().find("input").attr("readonly", true);
				}
			});

			this.byId("dtConsld1To1s").setMinDate(vPeriod);
			this.byId("dtConsld1To1s").setMaxDate(vDate);
			this.byId("dtConsld1To1s").setDateValue(vDate);
			this.byId("dtConsld1To1s").addEventDelegate({
				onAfterRendering: function () {
					that.byId("dtConsld1To1s").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idFromDD2AB").setMaxDate(vDate);
			// this.byId("idFromDD2AB").setDateValue(vPeriod);
			this.byId("idFromDD2AB").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idFromDD2AB").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idToDD2AB").setMinDate(vPeriod);
			this.byId("idToDD2AB").setMaxDate(vDate);
			// this.byId("idToDD2AB").setDateValue(vDate);
			this.byId("idToDD2AB").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idToDD2AB").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idFromDD").setMaxDate(vDate);
			// this.byId("idFromDD").setDateValue(vPeriod);
			this.byId("idFromDD").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idFromDD").$().find("input").attr("readonly", true);
				}
			});

			this.byId("idToDD").setMinDate(vPeriod);
			this.byId("idToDD").setMaxDate(vDate);
			// this.byId("idToDD").setDateValue(vDate);
			this.byId("idToDD").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idToDD").$().find("input").attr("readonly", true);
				}
			});

			this.ReportArr = [];
			this.Gstr2BCheck = [];
			// Added by Sarvmangla
			var oShow = {
				"check": false
			};
			var oModel = new sap.ui.model.json.JSONModel(oShow);
			this.getView().setModel(oModel, "showing");
		},

		_setReadonlyDate: function (id, date, minDate, maxDate) {
			this.byId(id).setDateValue(date);
			this.byId(id).setMinDate(minDate);
			this.byId(id).setMaxDate(maxDate);
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
		},

		_setTokenValidator: function (id) {
			this.byId(id).addValidator(function (args) {
				var text = args.text.trim();
				return new Token({
					key: text,
					text: text
				});
			});
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTR2
		 */
		onBeforeRendering: function () {
			this.byId("idIconTabBar").setSelectedKey("PrSummary");
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				this.clearVendorData();

				this.onLoadFy()
					.then(function () {
						this._onRouteMatched();
					}.bind(this))
					.catch(function (err) {
						console.log(err);
					}.bind(this));
				this.onSelectIconTabBar("PrSummary");
				this.getAllGSTIN();
				var object = {
					"aprFlag": false,
					"mayFlag": false,
					"junFlag": false,
					"julFlag": false,
					"augFlag": false,
					"sepFlag": false,
					"octFlag": false,
					"novFlag": false,
					"decFlag": false,
					"janFlag": false,
					"febFlag": false,
					"marFlag": false
				};
				this.getView().setModel(new JSONModel(object), "month");
			} else {
				this._onRouteMatched();
			}
		},

		_onRouteMatched: function () {
			var key = this.getView().getModel("ReturnKey").getProperty("/key");
			switch (key) {
			case "Gstr2a":
				this.byId("idIconTabBar").setSelectedKey("Gstr2a");
				this.onSelectIconTabBar("Gstr2a");
				break;
			case "Gstr2B":
				this.byId("idIconTabBar").setSelectedKey("Gstr2B");
				this.onSelectIconTabBar("Gstr2B");
				break;
			case "InitiateRecon2A":
				this.byId("idIconTabBar").setSelectedKey("InitiateRecon2A");
				this.onSelectIconTabBar("InitiateRecon2A");
				break;
			}
		},

		clearVendorData: function () {

			if (this.VendorPan) {
				this.getView().removeDependent(this.VendorPan);
				this.VendorPan.destroy();
				this.VendorPan = null;
			}
			this.Panarr = [];
			this.getView().setModel(new JSONModel([]), "Token");
			this.getView().setModel(new JSONModel([]), "TokenGSTN");

			if (this.VRPan) {
				this.getView().removeDependent(this.VRPan);
				this.VRPan.destroy();
				this.VRPan = null;
			}
			this.Panarrvr = [];
			this.getView().setModel(new JSONModel([]), "Tokenvr");
			this.getView().setModel(new JSONModel([]), "TokenGSTNvr");

			if (this.VenVMGSTIN) {
				this.getView().removeDependent(this.VenVMGSTIN)
				this.VenVMGSTIN.destroy();
				this.VenVMGSTIN = null;
			}
			this.VMGSTINarr = [];
			this.getView().setModel(new JSONModel([]), "TokenGSTNVM");
		},

		onChangeCriteria: function (oEvent) {
			var oModel = this.getView().getModel("Display");
			if (this.getView().byId("idCriteria").getSelectedKey() === "PRtaxperiod") {
				oModel.getData().PR = true;
				oModel.getData().A2 = false;
			} else if (this.getView().byId("idCriteria").getSelectedKey() === "2Ataxperiod") {
				oModel.getData().PR = false;
				oModel.getData().A2 = true;
			} else {
				oModel.getData().PR = true;
				oModel.getData().A2 = true;
			}
			oModel.refresh(true);
		},

		onRRGSTIN: function () {
			var oView = this.getView();
			var that = this;
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID)
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstinList.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oGstinModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						data.resp.unshift({
							"gstin": "All"
						});
						oGstinModel.setProperty("/", data.resp);
						oGstinModel.setSizeLimit(data.resp.length);
					}
					oView.setModel(oGstinModel, "ReconResultGstin");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oView.setModel(new JSONModel([]), "ReconResultGstin");
				});
			});
		},

		//=============== Fy Model Binding ===========================//
		onLoadFy: function () {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "GET",
						url: "/aspsapapi/getAllFy.do",
						contentType: "application/json",
					}).done(function (data, status, jqXHR) {
						var oModel = new JSONModel([]);
						if (data.hdr.status === "S") {
							oModel.setProperty("/", data.resp);
						}
						this.getView().setModel(oModel, "oFyModel");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "oFyModel");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onLoadFy2B: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "GET",
					url: "/aspsapapi/getAllFyFor2B.do",
					contentType: "application/json",
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp), "oFyModel2B");
						this._getGstr2b1(data.resp.finYears[0].fy);
						this._getGenerateGstr2b(this.byId("sGenerate2bFY").getSelectedKey() || data.resp.finYears[0].fy);
						this._bindGenerateGstr2b(this.byId("sGenerate2bFY").getSelectedKey() || data.resp.finYears[0].fy);
						this._get2bLinkAmt(this.byId("sLinkAmt2bFY").getSelectedKey() || data.resp.finYears[0].fy);
					} else {
						this.getView().setModel(new JSONModel([]), "oFyModel2B");
					}
					this.getView().getModel("oFyModel2B").refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "oFyModel2B");
					this.getView().getModel("oFyModel2B").refresh(true);
				}.bind(this));
		},

		getAllGSTIN: function () {
			var aGSTIN = [];
			var aRegGSTIN = this.getOwnerComponent().getModel("DataPermission").getData().respData.dataSecurity.gstin;
			for (var i = 0; i < aRegGSTIN.length; i++) {
				aGSTIN.push(aRegGSTIN[i]);
			}
			var aISDGSTIN = this.getOwnerComponent().getModel("ISDGstin").getData();
			for (var j = 0; j < aISDGSTIN.length; j++) {
				aGSTIN.push(aISDGSTIN[j]);
			}
			var oData = $.extend(true, [], aGSTIN);
			oData.unshift({
				"value": "All"
			});
			var oAllGstin = new JSONModel(oData),
				oIsdRegGstin = new JSONModel(aGSTIN);

			oAllGstin.setSizeLimit(oData.length);
			oIsdRegGstin.setSizeLimit(aGSTIN.length);
			this.getOwnerComponent().setModel(oIsdRegGstin, "isdregGstin");
			this.getOwnerComponent().setModel(oAllGstin, "allisdregGstin");
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GSTR2
		 */
		// 		onExit: function () {
		// 
		// 		}

		/**
		 * Binding Default values and ReadOnly Property to PR Summary Screen property
		 * Developed by: Bharat Gupta 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {date} date Date value
		 * @param {date} period Date value
		 */
		bindPrDefaultValue: function (date, period) {
			var that = this,
				aFields = [
					"dtPrProcessFrom", "dtPrProcessTo", "dpPrProcessFrom", "dpPrProcessTo",
					"dtPrSummaryFrom", "dtPrSummaryTo", "dpPrSummaryFrom", "dpPrSummaryTo"
				];
			aFields.forEach(function (field) {
				that.byId(field).setMaxDate(new Date());
				if (field.includes("From")) {
					if (field.includes("dpPr")) {
						that.byId(field).setDateValue(period);
					} else {
						that.byId(field).setDateValue(date);
					}

				} else {
					that.byId(field).setDateValue(new Date());
					if (field.includes("dpPr")) {
						that.byId(field).setMinDate(period);
					} else {
						that.byId(field).setMinDate(date);
					}
				}
				that.byId(field).addEventDelegate({
					onAfterRendering: function () {
						that.byId(field).$().find("input").attr("readonly", true);
					}
				});
			});
		},

		gsr2bDefaultValue: function () {
			var gstr2BProperty = {
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
				"Feb": false
			};
			this.getView().setModel(new JSONModel(gstr2BProperty), "2BProperty");
			this.getView().setModel(new JSONModel({
				"apr": false,
				"may": false,
				"jun": false,
				"jul": false,
				"aug": false,
				"sep": false,
				"oct": false,
				"nov": false,
				"dec": false,
				"jan": false,
				"feb": false,
				"mar": false,
			}), "GenGstr2B");
		},

		/**
		 * Binding PR Summary Property value
		 * Developed by: Bharat Gupta 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_bindPrSummaryProperty: function () {
			var oPrProperty = {
				"process": true,
				"summary": false,
				"processDate": false,
				"summaryDate": false,
				"processTaxPayable": true,
				"processCrEligible": true,
				"summaryTaxPayable": true,
				"summaryCrEligible": true,
				"processFullScreen": false,
				"summaryFullScreen": false,
				"selectedGstin": null
			};
			this.getView().setModel(new JSONModel(oPrProperty), "PrProperty");
		},

		_bindGstr2bSummaryProperty: function () {
			var obj = {
				"entityAvailableItc": true,
				"entityNonAvailableItc": true,
				"entityRejectedItc": true,
				"gstnAvailableItc": true,
				"gstnNonAvailableItc": true,
				"gstnRejectedItc": true
			};
			this.getView().setModel(new JSONModel(obj), "Gstr2bSummProperty");
		},

		/**
		 * Binding Default values and ReadOnly Property to Get GSTR-2A
		 * Developed by: Bharat Gupta 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		bindGetGstr2aValue: function () {
			var today = new Date(),
				vPeriod = new Date(today.getFullYear(), today.getMonth(), 1);

			this._setDateProperty("dtGet2aPTaxperiodFrom", vPeriod, today);
			this._setDateProperty("dtGet2aPTaxperiodTo", today, today, vPeriod);

			this._setDateProperty("dtGet2aSTaxperiodFrom", vPeriod, today);
			this._setDateProperty("dtGet2aSTaxperiodTo", today, today, vPeriod);
		},

		/**
		 * Binding Get GSTR-2A Property value
		 * Developed by: Bharat Gupta 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_bindGetGstr2aProperty: function () {
			var oPrProperty = {
				"process": true,
				"summary": false,
				"processFullScreen": false,
				"summaryFullScreen": false,
				"selectedGstin": null
			};
			this.getView().setModel(new JSONModel(oPrProperty), "Get2aProp");
		},

		/**
		 * Developed by: Bharat Gupta 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} key IconTabBar Selected key
		 */
		onSelectIconTabBar: function (key) {
			switch (key) {
			case "PrSummary": // PR Summary
				this._getProcessPrSummary();
				break;
			case "Gstr2a": // Get GSTR-2A
				this.getGstr2ANewProcessSumData(); //Sarvmangla code
				this.getGstr2a2bLinkStats();
				break;
			case "Gstr2asummary": // Get GSTR-2A
				this._getGstr2aProcess();
				break;
			case "Gstr2B": // Get GSTR-2B
				this.onLoadFy2B();
				//this._getGstr2b();
				break;
			case "Gstr2BSummary": // Get GSTR-2B Summary
				this._getGstr2bSummary();
				break;
			case "InitiateRecon2A": // Initaite Recon
				this.onPressGoForGSTIN2A();
				break;
			case "ReconSummary": // Recon Summary
				this.onVisibleConfig();
				//this.onConfigid();
				//this.onPressReconSummary();
				break;
			case "ReconResult": // Recon Results
				var recResKey = this.byId("sRecResultCriteria").getSelectedKey();
				this.onReconResultCriteria(recResKey);
				this.byId("sTaxPeriodBase").setSelectedKey("Both");
				this.byId("idRRGstins").setSelectedKeys([]);
				this._getReconResult();
				this.onRRGSTIN();
				break;
			case "PermissibleITC": // PermissibleITC
				this.byId("labID").setText("Tax Amount as per GSTR 2A");
				this.byId("labId1").setText("Tax Amount as per GSTR 2A");
				this.summaryPermissible();
				break;
			case "ReconResponse": // Ret-1 Impact
				var recResKey = this.byId("sRecResCriteria").getSelectedKey();
				this.onReconResponseCriteria(recResKey);
				this.onRRGSTIN();
				this.onEmailVenPAN();
				this.onFilterReconResp();
				this.onReconResults();
				this.onReconResults2AB();
				break;
			case "VendorSummary": // Vendor Summary
				break;
			case "VendorCommunication": // Vendor Communication
				this.onVenUpldRefresh(); //BOL Rakesh on 06.10.2020
				//this.byId("idsbFileStatus").setSelectedKey("VendorFileStatus");
				this.byId("idEmailRecptGstn").setSelectedKeys([]);
				//this.byId("searchId1").setValue();
				this.getView().setModel(new JSONModel([]), "Token");
				//this.byId("searchIdGSTIN").setValue();
				this.getView().setModel(new JSONModel([]), "TokenGSTN");
				//this.byId("idEmailVenPAN").setSelectedKeys([]);
				//this.byId("idEmailVenGstn").setSelectedKeys([]);
				this.byId("idEmailVenName").setSelectedKeys([]);
				this.byId("idEmailVenCode").setSelectedKeys([]);
				this.byId("idReptType").setSelectedKeys([]);
				this.onPrsRecpintGSTN();
				this.onEmailVenPAN();
				this.onPrsVenGstin();
				this.onPrsSearchVenMstr();
				this.onPrsRecpintPAN();
				break;
			case "CompareResults": // Compare Results
				break;
			case "ReconReports": // Recon Reports
				break;
			}
		},

		/**
		 * Method Called when Go button in FilterBar is press to get data from api
		 * Developed by: Bharat Gupta - 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} type Process type which invoked this method
		 */
		onSearch: function (type) {
			switch (type) {
			case "PrProcess":
				this._getProcessPrSummary();
				break;
			case "PrSummary":
				this._getPrReviewSummary();
				break;
			case "Get2aProcess":
				this._getGstr2aProcess();
				break;
			case "Get2aSummary":
				this._get2aReviewSummary();
				break;
			}
		},

		/**
		 * Method called to navigate back to Processed Screen
		 * Developed by: Bharat Gupta - 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} page Page from which back button triggered
		 */
		onGstr2Back: function (page) {
			switch (page) {
			case "PrSummary":
				var vModel = "PrProperty";
				break;
			case "GetGstr2a":
				vModel = "Get2aProp";
				break;
			}
			var oModel = this.getView().getModel(vModel),
				oData = oModel.getData();
			oData.process = true;
			oData.summary = false;
			oData.selectedGstin = null;
			oModel.refresh(true);
		},

		/*=====================================================================================*/
		/*======== PR Summary =================================================================*/
		/*=====================================================================================*/
		/**
		 * Method called to toggle b/w Date and Period range on Criteria selection change
		 * Developed by: Bharat Gupta - 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} property Property Field
		 * @param {boolean} flag Boolean value to bind in Pr Property field
		 */
		changeGstr2Criteria: function (property, flag) {
			var oModel = this.getView().getModel("PrProperty");
			oModel.getData()[property] = flag;
			oModel.refresh(true);
		},

		/**
		 * Called to get Payload for additional filter value
		 * Developed by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} search Object of SearchInfo
		 * @param {string} vPage  Dynamic page ID
		 * @retrun
		 */
		_getOtherFilters: function (search, vPage) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items;

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg) {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			// if (oDataSecurity.salesOrg) {
			// 	search.SO = this.byId("slSalesOrg").getSelectedKeys();
			// }
			// if (oDataSecurity.distChannel) {
			// 	search.DC = this.byId("slDistrChannel").getSelectedKeys();
			// }
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("slUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("slUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("slUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("slUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("slUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("slUserAccess6").getSelectedKeys();
			}
			return;
		},

		/**
		 * Method called to get Payload for PR Summary Process records
		 * Developed by: Bharat Gupta - 01.04.2020
		 * Modified by: Bharat Gupta - 30.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @return {Object} Payload
		 */
		_getPrProcessPayload: function () {
			var aGstin = this.byId("slPrProcessGstin").getSelectedKeys(),
				aTabType = this.byId("slPrProcessTableType").getSelectedKeys(),
				aDocType = this.byId("slPrProcessDocType").getSelectedKeys(),
				aDocCateg = this.byId("slPrProcessDataCateg").getSelectedKeys(),
				vCriteria = this.byId("slPrProcessCriteria").getSelectedKey() || "taxPeriod",
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"docRecvFrom": (vCriteria === "docDate" ? this.byId("dtPrProcessFrom").getValue() : null),
						"docRecvTo": (vCriteria === "docDate" ? this.byId("dtPrProcessTo").getValue() : null),
						"taxPeriodFrom": (vCriteria === "taxPeriod" ? this.byId("dpPrProcessFrom").getValue() : null),
						"taxPeriodTo": (vCriteria === "taxPeriod" ? this.byId("dpPrProcessTo").getValue() : null),
						"tableType": aTabType.includes("all") ? [] : aTabType,
						"docType": aDocType.includes("all") ? [] : aDocType,
						"docCategory": aDocCateg.includes("all") ? [] : aDocCateg,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			var oPrPayload = $.extend({}, oPayload.req);
			oPrPayload.criteria = vCriteria;
			this.byId("dpGstr2PrProcessed").setModel(new JSONModel(oPrPayload), "PrPayload");
			return oPayload;
		},

		/**
		 * Method called to get Process PR Summary data
		 * Developed by: Bharat Gupta - 06.04.2020
		 * Modified by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_getProcessPrSummary: function () {
			var oModel = this.byId("dpGstr2PrProcessed").getModel("PrProcessed"),
				payload = this._getPrProcessPayload(),
				that = this;

			if (this.byId("dAdapt")) {
				this._getOtherFilters(payload.req.dataSecAttrs, "dpGstr2PrProcessed");
			}
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2ProcessPrSummary.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that.byId("dpGstr2PrProcessed").setModel(new JSONModel(data.resp), "PrProcessed");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method called when user click on Gstin to get details of Gstin and Period
		 * Developed by: Bharat Gupta - 01.04.2020
		 * Modified by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} gstin Selected Gstin
		 */
		onPressGstr2PrSummary: function (gstin) {

			var oPrPayload = this.byId("dpGstr2PrProcessed").getModel("PrPayload").getData(),
				oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData(),
				oAdaptFilter = {};

			oPropData.process = false;
			oPropData.summary = true;
			oPropData.selectedGstin = gstin;

			this.byId("slPrSummaryCriteria").setSelectedKey(oPrPayload.criteria);
			if (oPrPayload.criteria === "taxPeriod") {
				var vFrom = oPrPayload.taxPeriodFrom,
					vTo = oPrPayload.taxPeriodTo;

				oPropData.summaryDate = false;
				this.byId("dpPrSummaryFrom").setDateValue(new Date(vFrom.substr(2) + "-" + vFrom.substr(0, 2) + "-01"));
				this.byId("dpPrSummaryTo").setMinDate(new Date(vFrom.substr(2) + "-" + vFrom.substr(0, 2) + "-01"));
				this.byId("dpPrSummaryTo").setDateValue(new Date(vTo.substr(2) + "-" + vTo.substr(0, 2) + "-01"));
			} else {
				oPropData.summaryDate = true;
				this.byId("dtPrSummaryFrom").setDateValue(new Date(oPrPayload.docRecvFrom));
				this.byId("dtPrSummaryTo").setMinDate(new Date(oPrPayload.docRecvFrom));
				this.byId("dtPrSummaryTo").setDateValue(new Date(oPrPayload.docRecvTo));
			}
			if (this.byId("dAdapt")) {
				this._getOtherFilters(oAdaptFilter, "dpGstr2PrProcessed");
				this.byId("dpGstr2PrSummary").setModel(new JSONModel(oAdaptFilter), "AdaptFilter");
			}
			this.byId("slPrSummaryGstin").setSelectedKeys(gstin);
			this.byId("slPrSummaryDataCateg").setSelectedKeys(oPrPayload.docCategory);
			oPropModel.refresh(true);
			this._getPrReviewSummary();
		},

		/**
		 * Method return PR Review Summary Payload to get data from api call
		 * Developed by: Bharat Gupta - 13.04.2020
		 * Modified by: Bharat Gupta - 30.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @return {Object} Payload
		 */
		_getPrSummaryPayload: function () {
			var vCriteria = this.byId("slPrSummaryCriteria").getSelectedKey(),
				aGstin = this.byId("slPrSummaryGstin").getSelectedKeys(),
				dDataCateg = this.byId("slPrSummaryDataCateg").getSelectedKeys(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"docRecvFrom": (vCriteria === "docDate" ? this.byId("dtPrSummaryFrom").getValue() : null),
						"docRecvTo": (vCriteria === "docDate" ? this.byId("dtPrSummaryTo").getValue() : null),
						"taxPeriodFrom": (vCriteria === "taxPeriod" ? this.byId("dpPrSummaryFrom").getValue() : null),
						"taxPeriodTo": (vCriteria === "taxPeriod" ? this.byId("dpPrSummaryTo").getValue() : null),
						"docCategory": dDataCateg.includes("All") ? [] : [],
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			var oPrPayload = $.extend({}, oPayload.req);
			oPrPayload.criteria = this.byId("slPrSummaryCriteria").getSelectedKey();
			this.byId("dpGstr2PrSummary").setModel(new JSONModel(oPrPayload), "SummaryPayload");
			return oPayload;
		},

		/**
		 * Method called to get PR Review Summary data from Database table
		 * Developed by: Bharat Gupta - 13.04.2020
		 * Modified by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_getPrReviewSummary: function () {
			var oModel = this.byId("tabPrSummary").getModel("PrSummary"),
				oPayload = this._getPrSummaryPayload(),
				that = this;

			if (this.byId("dAdapt")) {
				this._getOtherFilters(oPayload.req.dataSecAttrs, "dpGstr2PrSummary");
			}
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2ReviewPrSummary.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				that._bindPrSummarData(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Developed by: Bharat Gupta - 13.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @return {Object} json object for PR Review summary
		 */
		_jsonPrSummary: function () {
			return {
				"tableSection": null,
				"table": null,
				"count": 0,
				"invoiceValue": 0,
				"taxableValue": 0,
				"taxPayable": 0,
				"taxPayableIgst": 0,
				"taxPayableCgst": 0,
				"taxPayableSgst": 0,
				"taxPayableCess": 0,
				"crEligibleTotal": 0,
				"crEligibleIgst": 0,
				"crEligibleCgst": 0,
				"crEligibleSgst": 0,
				"crEligibleCess": 0,
				"items": []
			};
		},

		/**
		 * Method to format data and bind it to output table
		 * Developed by: Bharat Gupta - 13.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} data Response object
		 */
		_bindPrSummarData: function (data) {
			var aFields = ["b2b", "b2ba", "cdn", "cdna", "isd", "isda", "imports", "imports-a", "rcurd", "rcurda", "rcmadv"],
				oSummary = [];

			aFields.forEach(function (f) {
				var oSummaryJson = this._jsonPrSummary();
				data[f].forEach(function (item) {
					if (item.docType === "TOTAL" || ["b2b", "b2ba"].includes(f)) {
						this._bindSummaryTotal(oSummaryJson, item);
					} else {
						var obj = $.extend({}, item);
						obj.tableSection = item.docType;
						oSummaryJson.items.push(obj);
					}
				}.bind(this));
				oSummary.push(oSummaryJson);
			}.bind(this));

			this.byId("tabPrSummary").setModel(new JSONModel(oSummary), "PrSummary");
		},

		/**
		 * Method called to move total object to output object
		 * Developed by: Bharat Gupta - 13.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} obj Output object
		 * @param {Object} data Reponse data
		 */
		_bindSummaryTotal: function (obj, data) {
			obj.tableSection = data.table;
			obj.table = data.table;
			obj.count = data.count;
			obj.invoiceValue = data.invoiceValue;
			obj.taxableValue = data.taxableValue;
			obj.taxPayable = data.taxPayable;
			obj.taxPayableIgst = data.taxPayableIgst;
			obj.taxPayableCgst = data.taxPayableCgst;
			obj.taxPayableSgst = data.taxPayableSgst;
			obj.taxPayableCess = data.taxPayableCess;
			obj.crEligibleTotal = data.crEligibleTotal;
			obj.crEligibleIgst = data.crEligibleIgst;
			obj.crEligibleCgst = data.crEligibleCgst;
			obj.crEligibleSgst = data.crEligibleSgst;
			obj.crEligibleCess = data.crEligibleCess;
		},

		/**
		 * Method called when user click on button to see/close full screen
		 * Developed by: Bharat Gupta - 13.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} action Action button
		 */
		onGstr2FullScreen: function (action) {
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();

			if (action === "openProcess") {
				oPropData.processFullScreen = true;
				this.byId("idCcPrProcess").setFullScreen(true);
				this.byId("tabPrProcess").setVisibleRowCount(22);

			} else if (action === "closeProcess") {
				oPropData.processFullScreen = false;
				this.byId("idCcPrProcess").setFullScreen(false);
				this.byId("tabPrProcess").setVisibleRowCount(8);

			} else if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("idCcPrSummary").setFullScreen(true);
				this.byId("tabPrSummary").expandToLevel(1);
				this.byId("tabPrSummary").setVisibleRowCount(24);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("idCcPrSummary").setFullScreen(false);
				this.byId("tabPrSummary").collapseAll();
				this.byId("tabPrSummary").setVisibleRowCount(10);
			}
			oPropModel.refresh(true);
		},

		/**
		 * Method called when user change from date to set To date Minimun date
		 * Developed by: Bharat Gupta - 13.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {string} iToDate ToDate Id
		 */
		onChangePrDate: function (oEvent, iToDate) {
			var vDate = oEvent.getSource().getDateValue(),
				vToDate = this.byId(iToDate).getDateValue();

			this.byId(iToDate).setMinDate(vDate);
			if (vDate > vToDate) {
				this.byId(iToDate).setDateValue(vDate);
			}
		},

		/**
		 * Method called to format Table Type in PR Review Summary Screen
		 * Developed by: Bharat Gupta 17.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} vValue Value to format
		 * @return {string} Formatted value
		 */
		_prSummaryTables: function (vValue) {
			var oBundle = this.getResourceBundle();
			if (vValue) {
				return oBundle.getText(vValue);
			}
			return vValue;
		},

		/**
		 * Method called to Expand / Collapse All records in table
		 * Developed by: Bharat Gupta 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} action Action on Table data
		 */
		onPrExpandCollapseAll: function (action) {
			if (action === "E") {
				this.byId("tabPrSummary").expandToLevel(1);
			} else {
				this.byId("tabPrSummary").collapseAll();
			}
		},

		/**
		 * Developed by: Bharat Gupta 09.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {Object} item Selected Report type to download
		 * @param {string} view View 
		 */
		onDownloadPrSummaryReport: function (oEvent, item, view) {

			var oBundle = this.getResourceBundle(),
				aReportType = ["gstr2Process"],
				oPayload = {
					"req": {
						"type": item.getKey(),
						"entityId": [$.sap.entityID],
						"docDateFrom": null,
						"docDateTo": null,
						"taxPeriodFrom": null,
						"taxPeriodTo": null,
						"tableType": [],
						"docType": [],
						"docCategory": [],
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			var aGSTIN = [],
				fromTax = "",
				toTax = "";
			if (view === "P") {
				var aIndex = this.byId("tabPrProcess").getSelectedIndices(),
					oData = this.byId("tabPrProcess").getModel("PrProcessed").getData(),
					oPrPayload = this.byId("dpGstr2PrProcessed").getModel("PrPayload").getData();

				if (aIndex.length === 0) {
					MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				var vTableType = oPrPayload.tableType.toString().toUpperCase();
				oPayload.req.tableType = !vTableType ? [] : vTableType.split(",");
				oPayload.req.docType = oPrPayload.docType;
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndex[i]].gstin);
					aGSTIN.push(oData[aIndex[i]].gstin);
				}
				fromTax = this.byId("dpPrProcessFrom").getValue();
				toTax = this.byId("dpPrProcessTo").getValue();
			} else {
				oPrPayload = this.byId("dpGstr2PrSummary").getModel("SummaryPayload").getData();
				oPayload.req.dataSecAttrs = oPrPayload.dataSecAttrs;
				fromTax = this.byId("dpPrSummaryFrom").getValue();
				toTax = this.byId("dpPrProcessTo").getValue();
				aGSTIN.push(oPrPayload.dataSecAttrs.GSTIN[0]);
			}
			oPayload.req.docDateFrom = (oPrPayload.criteria === "docDate" ? oPrPayload.docRecvFrom : null);
			oPayload.req.docDateTo = (oPrPayload.criteria === "docDate" ? oPrPayload.docRecvTo : null);
			oPayload.req.taxPeriodFrom = (oPrPayload.criteria === "taxPeriod" ? oPrPayload.taxPeriodFrom : null);
			oPayload.req.taxPeriodTo = (oPrPayload.criteria === "taxPeriod" ? oPrPayload.taxPeriodTo : null);
			oPayload.req.docCategory = oPrPayload.docCategory;

			if (aReportType.includes(item.getKey())) {
				var vUrl = "/aspsapapi/downloadgstr2ProcessedCsvReports.do";
				oPayload.req.dataType = "Inward";
				oPayload.req.reportCateg = (view === "P" ? "ProcessedSummary" : "ReviewSummary");
				oPayload.req.docType = oPayload.req.docType;
				oPayload.req.tableType = oPayload.req.tableType;
				oPayload.req.docCategory = oPayload.req.docCategory;
				this.reportDownload(oPayload, vUrl);
			} else if (item.getKey() === "gstr2AspError") {
				var vUrl = "/aspsapapi/downloadGstr2ConsolidatedAspErrorCsvReport.do";
				oPayload.req.dataType = "Inward";
				oPayload.req.type = "Consolidated_DigiGST_Error_Report";
				oPayload.req.reportCateg = (view === "P" ? "ProcessedSummary" : "ReviewSummary");
				oPayload.req.docType = oPayload.req.docType;
				oPayload.req.tableType = oPayload.req.tableType;
				oPayload.req.docCategory = oPayload.req.docCategory;
				this.reportDownload(oPayload, vUrl);
			} else if (item.getKey() === "gstr2Tagging") {
				// var vUrl = "/aspsapapi/downloadGstr2ReconTypeTaggingReports.do";
				oPayload.req.dataType = "Inward";
				oPayload.req.type = "Processed_Records_Recon_Tagging";
				oPayload.req.reportCateg = (view === "P" ? "ProcessedSummary" : "ReviewSummary");
				oPayload.req.docType = oPayload.req.docType;
				oPayload.req.tableType = oPayload.req.tableType;
				oPayload.req.docCategory = oPayload.req.docCategory;
				this.oPayload = oPayload;
				this.getGstr2Tagging();
			} else if (item.getKey() === "itcRep") {
				var req = {
					"req": {
						"entityId": [$.sap.entityID],
						"gstin": aGSTIN,
						"fromTaxPeriod": fromTax,
						"toTaxPeriod": toTax
					}
				};
				vUrl = "/aspsapapi/gmrInwardSummaryDownloadReport.do";
				this.excelDownload(req, vUrl);
			} else {
				vUrl = "/aspsapapi/gstr2PRReportsDownload.do";
				this.excelDownload(oPayload, vUrl);
			}
		},

		getGstr2Tagging: function () {
			if (!this._ReconReportTypeFilter) {
				this._ReconReportTypeFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReconReportTypeFilter",
					this);
				this.getView().addDependent(this._ReconReportTypeFilter);
			}
			var oRepType = this._ReportTypeFilterData();
			var oMultiComboBox = this.byId("idReportTypeFil");
			oMultiComboBox.setModel(new JSONModel(oRepType), "ReportType");
			var aKeys = oRepType.map(function (item) {
				return item.key;
			});
			oMultiComboBox.setSelectedKeys(aKeys);

			this._ReconReportTypeFilter.open();
		},
		onReconReportTypeFilterClose: function (flag) {

			if (flag === "Apply") {
				var vUrl = "/aspsapapi/downloadGstr2ReconTypeTaggingReports.do";
				this.oPayload.req.reconType = this.byId("idReconTypeFil").getSelectedKey();
				this.oPayload.req.reconReportType = this.removeAll(this.byId("idReportTypeFil").getSelectedKeys());
				this.reportDownload(this.oPayload, vUrl);
				this._ReconReportTypeFilter.close();
			} else {
				this._ReconReportTypeFilter.close();
			}
		},

		_ReportTypeFilterData: function () {
			return [{
				key: "All",
				text: "All"
			}, {
				key: "Exact Match",
				text: "Exact Match"
			}, {
				key: "Match With Tolerance",
				text: "Match with Tolerance"
			}, {
				key: "Value Mismatch",
				text: "Value Mismatch"
			}, {
				key: "POS Mismatch",
				text: "POS Mismatch"
			}, {
				key: "Doc Date Mismatch",
				text: "Doc Date Mismatch"
			}, {
				key: "Doc Type Mismatch",
				text: "Doc Type Mismatch"
			}, {
				key: "Doc No Mismatch I",
				text: "Doc No Mismatch I"
			}, {
				key: "Doc No Mismatch II",
				text: "Doc No Mismatch II"
			}, {
				key: "Doc No & Doc Date Mismatch",
				text: "Doc No & Doc Date Mismatch"
			}, {
				key: "Multi-Mismatch",
				text: "Multi-Mismatch"
			}, {
				key: "Potential-I",
				text: "Potential I"
			}, {
				key: "Potential-II",
				text: "Potential II"
			}, {
				key: "Logical Match",
				text: "Logical Match"
			}, {
				key: "Addition in PR",
				text: "Addition in PR"
			}, {
				key: "Import-Match",
				text: "Import-Match"
			}, {
				key: "Import-Mismatch",
				text: "Import-Mismatch"
			}, {
				key: "Import-Addition in PR",
				text: "Import-Addition in PR"
			}, {
				key: "ISD-Exact Match",
				text: "ISD-Exact Match"
			}, {
				key: "ISD-Match With Tolerance",
				text: "ISD-Match with Tolerance"
			}, {
				key: "ISD-Value Mismatch",
				text: "ISD-Value Mismatch"
			}, {
				key: "ISD-POS Mismatch",
				text: "ISD-POS Mismatch"
			}, {
				key: "ISD-Doc Date Mismatch",
				text: "ISD-Doc Date Mismatch"
			}, {
				key: "ISD-Doc Type Mismatch",
				text: "ISD-Doc Type Mismatch"
			}, {
				key: "ISD-Doc No Mismatch I",
				text: "ISD-Doc No Mismatch I"
			}, {
				key: "ISD-Doc No Mismatch II",
				text: "ISD-Doc No Mismatch II"
			}, {
				key: "ISD-Doc No & Doc Date Mismatch",
				text: "ISD-Doc No & Doc Date Mismatch"
			}, {
				key: "ISD-Multi-Mismatch",
				text: "ISD-Multi-Mismatch"
			}, {
				key: "ISD-Potential-I",
				text: "ISD-Potential I"
			}, {
				key: "ISD-Potential-II",
				text: "ISD-Potential II"
			}, {
				key: "ISD-Logical Match",
				text: "ISD-Logical Match"
			}, {
				key: "ISD-Addition in PR",
				text: "ISD-Addition in PR"
			}, {
				key: "Locked-Force Match",
				text: "Locked-Force Match"
			}, {
				key: "Locked-3B Response",
				text: "Locked-3B Response"
			}, {
				key: "Not Applicable",
				text: "Not Applicable"
			}, {
				key: "Excluded from Recon",
				text: "Excluded from Recon"
			}, {
				key: "Dropped from Recon",
				text: "Dropped from Recon"
			}, {
				key: "Not Participated",
				text: "Not Participated"
			}];
		},

		onErrorCorrection: function (view) {
			var oFilterData = {
				"req": {
					"dataType": "inward",
					"criteria": null,
					"docDateFrom": null,
					"docDateTo": null,
					"taxPeriodFrom": null,
					"taxPeriodTo": null,
					"navType": "GSTR2",
					"dataOriginType": null,
					"type": "E",
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};

			if (view === "P") {
				var oPayload = this.byId("dpGstr2PrProcessed").getModel("PrPayload").getData();
			} else {
				oPayload = this.byId("dpGstr2PrSummary").getModel("SummaryPayload").getData();
			}
			oFilterData.req.criteria = oPayload.criteria;
			oFilterData.req.taxPeriodFrom = oPayload.criteria === "taxPeriod" ? oPayload.taxPeriodFrom : null;
			oFilterData.req.taxPeriodTo = oPayload.criteria === "taxPeriod" ? oPayload.taxPeriodTo : null;
			oFilterData.req.docDateFrom = oPayload.criteria === "docDate" ? oPayload.docRecvFrom : null;
			oFilterData.req.docDateTo = oPayload.criteria === "docDate" ? oPayload.docRecvTo : null;
			oFilterData.req.dataSecAttrs = oPayload.dataSecAttrs;
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvManageNew");
		},

		/**
		 * Method called to Reset filter value to default
		 * Developed by: Bharat Gupta 30.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} view Clear button from view
		 */
		onPressClearPR: function (view) {
			var oModel = this.getView().getModel("PrProperty"),
				date = new Date();
			date.setDate(date.getDate() - 9);

			if (view === "Process") {
				oModel.getData().processDate = false;
				this.byId("slPrProcessCriteria").setSelectedKey("taxPeriod");
				this.byId("slPrProcessGstin").setSelectedKeys();
				this.byId("slPrProcessTableType").setSelectedKeys();
				this.byId("slPrProcessDocType").setSelectedKeys();
				this.byId("slPrProcessDataCateg").setSelectedKeys();

				this.byId("dtPrProcessFrom").setDateValue(date); // Document Date
				this.byId("dtPrProcessTo").setDateValue(new Date());
				this.byId("dtPrProcessTo").setMinDate(date);

				this.byId("dpPrProcessFrom").setDateValue(date); // Tax Period
				this.byId("dpPrProcessTo").setDateValue(new Date());
				this.byId("dpPrProcessTo").setMinDate(date);
				this._getProcessPrSummary();
			} else {
				oModel.getData().summaryDate = false;
				this.byId("slPrSummaryCriteria").setSelectedKey("taxPeriod");
				this.byId("slPrSummaryGstin").setSelectedKeys(oModel.getData().selectedGstin);
				this.byId("slPrSummaryDataCateg").setSelectedKeys();

				this.byId("dtPrSummaryFrom").setDateValue(date); // Document Date
				this.byId("dtPrSummaryTo").setDateValue(new Date());
				this.byId("dtPrSummaryTo").setMinDate(date);

				this.byId("dpPrSummaryFrom").setDateValue(date); // Tax Period
				this.byId("dpPrSummaryTo").setDateValue(new Date());
				this.byId("dpPrSummaryTo").setMinDate(date);
				this._getPrReviewSummary();
			}
			oModel.refresh(true);
			if (this.byId("dAdapt")) {
				this.onClearFilter();
			}
		},

		/**
		 * Method called to open Adapt Filter
		 * Developed by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} view Source View for Adapt filter
		 */
		onPrSummAdaptFilters: function (view) {
			if (!this._oAdpatFilter) {
				this._oAdpatFilter = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.AdaptFilter", this);
				this.getView().addDependent(this._oAdpatFilter);
			}
			if (view === "P") {
				var oAdaptModel = this.byId("dpGstr2PrProcessed").getModel("AdaptFilter");
			} else {
				oAdaptModel = this.byId("dpGstr2PrSummary").getModel("AdaptFilter");
			}
			if (oAdaptModel) {
				var aAdaptData = oAdaptModel.getData();
				this.byId("slProfitCtr").setSelectedKeys(aAdaptData.PC);
				this.byId("slPlant").setSelectedKeys(aAdaptData.Plant);
				this.byId("slDivision").setSelectedKeys(aAdaptData.D);
				this.byId("slLocation").setSelectedKeys(aAdaptData.L);
				this.byId("slPurcOrg").setSelectedKeys(aAdaptData.PO);
				// this.byId("slSalesOrg").setSelectedKeys(aAdaptData.S);
				// this.byId("slDistrChannel").setSelectedKeys(aAdaptData.distrChannel);
				this.byId("slUserAccess1").setSelectedKeys(aAdaptData.UD1);
				this.byId("slUserAccess2").setSelectedKeys(aAdaptData.UD2);
				this.byId("slUserAccess3").setSelectedKeys(aAdaptData.UD3);
				this.byId("slUserAccess4").setSelectedKeys(aAdaptData.UD4);
				this.byId("slUserAccess5").setSelectedKeys(aAdaptData.UD5);
				this.byId("slUserAccess6").setSelectedKeys(aAdaptData.UD6);
			}
			this.byId("dAdapt").setModel(new JSONModel({
				"view": view
			}), "PropAdapt");
			this._oAdpatFilter.open();
		},

		/**
		 * Method called to when user close Adpat filter
		 * Developed by: Bharat Gupta - 29.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} action Action by User
		 */
		onPressFilterClose: function (action) {
			this._oAdpatFilter.close();
			if (action === "apply") {
				var oAdaptFilter = {};
				if (this.byId("dpGstr2PrProcessed").getVisible()) {
					this._getOtherFilters(oAdaptFilter, "dpGstr2PrProcessed");
					this.getView().byId("dpGstr2PrProcessed").setModel(new JSONModel(oAdaptFilter), "AdaptFilter");
					this._getProcessPrSummary();

				} else if (this.byId("dpGstr2PrSummary").getVisible()) {
					this._getOtherFilters(oAdaptFilter, "dpGstr2PrSummary");
					this.getView().byId("dpGstr2PrSummary").setModel(new JSONModel(oAdaptFilter), "AdaptFilter");
					this._getPrReviewSummary();
				}
			}
		},

		/**
		 * Method called to clear Adapt filter
		 * Developed by: Bharat Gupta - 19.08.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onClearFilter: function () {
			var oPropAdapt = this.byId("dAdapt").getModel("PropAdapt").getData();
			if (oPropAdapt.view) {
				var oAdaptModel = this.byId("dpGstr2PrProcessed").getModel("AdaptFilter");
			} else {
				oAdaptModel = this.byId("dpGstr2PrSummary").getModel("AdaptFilter");
			}
			var aAdaptData = oAdaptModel.getData();
			aAdaptData.PC = null;
			aAdaptData.Plant = null;
			aAdaptData.D = null;
			aAdaptData.L = null;
			aAdaptData.PO = null;
			aAdaptData.UD1 = null;
			aAdaptData.UD2 = null;
			aAdaptData.UD3 = null;
			aAdaptData.UD4 = null;
			aAdaptData.UD5 = null;
			aAdaptData.UD6 = null;
			oAdaptModel.refresh(true);

			this.byId("slProfitCtr").setSelectedKeys([]);
			this.byId("slPlant").setSelectedKeys([]);
			this.byId("slDivision").setSelectedKeys([]);
			this.byId("slLocation").setSelectedKeys([]);
			this.byId("slPurcOrg").setSelectedKeys([]);
			this.byId("slUserAccess1").setSelectedKeys([]);
			this.byId("slUserAccess2").setSelectedKeys([]);
			this.byId("slUserAccess3").setSelectedKeys([]);
			this.byId("slUserAccess4").setSelectedKeys([]);
			this.byId("slUserAccess5").setSelectedKeys([]);
			this.byId("slUserAccess6").setSelectedKeys([]);
		},

		/*=====================================================================================*/
		/*======== Get GSTR-2A ================================================================*/
		/*=====================================================================================*/
		/**
		 * Called to Oopen/Close table in Full Screen
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} action Action to open/close
		 */
		onGet2aFullScreen: function (action) {
			var oPropModel = this.getView().getModel("Get2aProp"),
				oPropData = oPropModel.getData();

			if (action === "openProcess") {
				oPropData.processFullScreen = true;
				this.byId("idCcGet2aProcess").setFullScreen(true);
				this.byId("tab2aProcess").setVisibleRowCount(22);

			} else if (action === "closeProcess") {
				oPropData.processFullScreen = false;
				this.byId("idCcGet2aProcess").setFullScreen(false);
				this.byId("tab2aProcess").setVisibleRowCount(8);

			} else if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("idCcGet2aSummary").setFullScreen(true);
				this.byId("tabGet2aSummary").expandToLevel(1);
				this.byId("tabGet2aSummary").setVisibleRowCount(20);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("idCcGet2aSummary").setFullScreen(false);
				this.byId("tabGet2aSummary").collapseAll();
				this.byId("tabGet2aSummary").setVisibleRowCount(9);
			}
			oPropModel.refresh(true);
		},

		/**
		 * Method called to get Payload for PR Summary Process records
		 * Developed by: Bharat Gupta - 01.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} value Request type (i.e. P/S)
		 * @return {Object} Payload structure
		 */
		_getGstr2aPayload: function (value) {
			if (value === "P") {
				var vTaxPeriodFrom = "dtGet2aPTaxperiodFrom",
					vTaxPeriodTo = "dtGet2aPTaxperiodTo",
					vGstin = "slGet2aProcessGstin",
					vTabType = "slGet2aProcessTabType",
					vDocType = "slGet2aProcessDocType";
				var aGstin = this.byId(vGstin).getSelectedKeys();
			} else {
				var vTaxPeriodFrom = "dtGet2aSTaxperiodFrom",
					vTaxPeriodTo = "dtGet2aSTaxperiodTo",
					// vTaxPeriod = "dtGet2aSummary";
					vGstin = "slGet2aSummaryGstin",
					vTabType = "slGet2aSummaryTabType",
					vDocType = "slGet2aSummaryDocType";

				var aGstin = [this.byId(vGstin).getSelectedKey()];
			}

			// aTabType = this.removeAll(this.byId(vTabType).getSelectedKeys()),
			var aTabType = this.byId(vTabType).getSelectedKeys(),
				aDocType = this.byId(vDocType).getSelectedKeys(),
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"fromPeriod": this.byId(vTaxPeriodFrom).getValue(),
						"toPeriod": this.byId(vTaxPeriodTo).getValue(),
						// "taxPeriod": this.byId(vTaxPeriod).getValue(),
						"tableType": aTabType.includes("all") ? [] : aTabType,
						"docType": aDocType.includes("all") ? [] : aDocType,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin
						}
					}
				};
			return oPayload;
		},

		/**
		 * Method call to get Gstr2a Process data
		 * Developed by: Bharat Gupta - 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_getGstr2aProcess: function () {
			var oModel = this.byId("dpGetGstr2aProcess").getModel("Gstr2aProcessed"),
				oPayload = this._getGstr2aPayload("P"),
				that = this;

			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			this.byId("dpGetGstr2aProcess").setModel(new JSONModel(oPayload.req), "Get2aPayload");

			that.byId("dpGetGstr2aProcess").setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2aProcessData.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				that.byId("dpGetGstr2aProcess").setBusy(false);
				that.byId("dpGetGstr2aProcess").setModel(new JSONModel(data.resp), "Gstr2aProcessed");
			}).fail(function (jqXHR, status, err) {
				that.byId("dpGetGstr2aProcess").setBusy(false);
			});
		},

		/**
		 * Method called to get flag object for Section wise Get Gstr2a
		 * Developed by: Bharat Gupta - 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {boolean} flag Flag value
		 * @return {Object} Section wise structure
		 */
		_getGstr2aCheckStats: function (flag) {
			return {
				"b2b": flag,
				"b2ba": flag,
				"cdn": flag,
				"cdna": flag,
				"isd": flag,
				"isda": false,
				"select": flag,
				"partial": false
			};
		},

		_getTaxPeriod: function (month) {
			var aMonth = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"],
				FY = this.getView().byId("dtFinYearGstrNew").getSelectedKey(),
				idx = aMonth.indexOf(month) + 1,
				FYA = FY.split("-")[0];

			if (idx < 4) {
				return ('' + idx).padStart(2, '0') + (+FYA + 1);
			} else {
				return ('' + idx).padStart(2, '0') + FYA;
			}
			return cTP;
		},

		/**
		 * Method called to open popup message to get section wise Gstr2a
		 * Developed by: Bharat Gupta - 07.04.2020
		 * changes done by Sarvmangla.
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onChangeDateValue: function (oEvent) {
			var vDatePicker;
			if (oEvent.getSource().getId().includes("dtGet2aPTaxperiodFrom")) {
				vDatePicker = "dtGet2aPTaxperiodTo";
			} else if (oEvent.getSource().getId().includes("dtGet2aSTaxperiodFrom")) {
				vDatePicker = "dtGet2aSTaxperiodTo";
			} else if (oEvent.getSource().getId().includes("gstr2bDateFrom")) {
				vDatePicker = "gstr2bDateTo";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate("01"));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			this.byId(vDatePicker).setMinDate(fromDate);
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
		},

		onPressProcessStatusGO: function () {
			var gstin = this.byId("id_gstinPopupget2").getText();
			var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": oTaxPeriod,
					"gstin": [gstin]
				}
			};
			this.postpayloadgstr2 = payload;
			this.getGstrget2AASucessStatusDataFinal(payload);
			this.vPSFlag = "P";
			this._oDialog.open();
		},

		onPressGetGstr2a: function (oEvent, flag, gstin, month) {
			if (!this._oDialog) {
				this._oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ProcessStatus", this);
				this.getView().addDependent(this._oDialog);
			}
			if (flag === "P") {
				var oSelectedItem = this.getView().byId("tab2aProcess").getSelectedIndices();
				var oModelForTab1 = this.byId("tab2aProcess").getModel("Gstr2aProcessed").getData();
				if (oSelectedItem.length == 0) {
					MessageBox.warning("Select at least one record");
					return;
				}
				var oData = {
					"P": true
				};
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				this.byId("id_TaxProcessGstr2").setValue(this.getView().byId("dtGet2aPTaxperiodFrom").getValue());
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var aGstin = [];
				for (var i = 0; i < oSelectedItem.length; i++) {
					aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);

				}
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": aGstin
					}
				};
				this.vPSFlag = "P";

			} else if (flag === "PS") {
				var oData = {
					"P": false
				};
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				var minDate = this.oView.byId("dtGet2aPTaxperiodFrom").getDateValue();
				minDate = new Date(minDate.setDate("01"));
				this.byId("id_TaxProcessGstr2").setMinDate(minDate);
				var maxDate = this.oView.byId("dtGet2aPTaxperiodTo").getDateValue();
				maxDate = new Date(maxDate.setDate("30"));
				this.byId("id_TaxProcessGstr2").setMaxDate(maxDate);
				this.byId("id_TaxProcessGstr2").setValue(this.oView.byId("dtGet2aPTaxperiodFrom").getValue());
				this.byId("id_gstinPopupget2").setText(gstin);
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": [gstin]
					}
				};
				this.vPSFlag = "P";

			} else if (flag === "2A") {
				var oData = {
					"P": false
				};
				var vTaxPeriod = this._getTaxPeriod(month);
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				this.byId("id_TaxProcessGstr2").setValue(vTaxPeriod);
				this.byId("id_gstinPopupget2").setText(gstin);
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": vTaxPeriod,
						"gstin": [gstin]
					}
				};
				this.vPSFlag = "2A";

			} else {
				var oData = {
					"P": false
				};
				var oModel = this.getView().setModel(new JSONModel(oData), "visiSummPopup");
				this.byId("id_TaxProcessGstr2").setValue(this.oView.byId("dtGet2aSummary").getValue());
				this.byId("id_gstinPopupget2").setText(this.oView.byId("slGet2aSummaryGstin").getSelectedKey());
				var oTaxPeriod = this.oView.byId("dtGet2aSummary").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var payload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oTaxPeriod,
						"gstin": [this.getView().byId("slGet2aSummaryGstin").getSelectedKey()]
					}
				};
				this.vPSFlag = "S";
			}
			this.postpayloadgstr2 = payload;
			this.getGstrget2AASucessStatusDataFinal(payload);
			this._oDialog.open();
		},

		onGstr2aStatsFailed: function (oEvent, type) {
			var payload = {
				"req": {
					"gstin": this.byId("id_gstinPopupget2").getText(),
					"taxPeriod": this.byId("id_TaxProcessGstr2").getValue(),
					"returnType": "GSTR2A",
					"section": type
				}
			};
			this._oDialog.setBusy(true);
			if (!this._msgPopover) {
				this._msgPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.MsgPopover", this);
				this.getView().addDependent(this._msgPopover);
			}
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getCallFailureErrMsg.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._msgPopover.setModel(new JSONModel(data.resp), "PopoverMsg");
					this._msgPopover.openBy(oEvent.getSource());
					this._oDialog.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oDialog.setBusy(false);
				}.bind(this));
		},

		onCloseProcDialog: function () {
			if (this.vPSFlag == "2A") {
				this.getGstr2ANewProcessSumData();
			} else {
				this._getGstr2aProcess();
			}
			this._oDialog.close();
		},

		getGstrget2AASucessStatusDataFinal: function (payload) {
			var oView = this.getView();
			var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2aSuccessStatus.do",
					contentType: "application/json",
					data: JSON.stringify(that.postpayloadgstr2)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					}
					that.bindGstrget2ADetailStatus(data);
					// var oGstr6ASucessData = new sap.ui.model.json.JSONModel(data);
					// oView.setModel(oGstr6ASucessData, "Gstr6ASucess");

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getGstr2aSuccessStatus : Error");
				});
			});
		},

		bindGstrget2ADetailStatus: function (data) {
			this._showing(false);
			var oView = this.getView();
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = false;
				data.resp.lastCall[i].b2baFlag = false;
				data.resp.lastCall[i].cdnFlag = false;
				data.resp.lastCall[i].cdnaFlag = false;
				data.resp.lastCall[i].isdFlag = false;
				data.resp.lastCall[i].isdaFlag = false;
				data.resp.lastCall[i].impgFlag = false;
				data.resp.lastCall[i].impgsezFlag = false;
				data.resp.lastCall[i].ecomFlag = false;
				data.resp.lastCall[i].ecomaFlag = false;
				data.resp.lastCall[i].amendAttriFlag = false;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = false;
				data.resp.lastSuccess[i].b2baFlag = false;
				data.resp.lastSuccess[i].cdnFlag = false;
				data.resp.lastSuccess[i].cdnaFlag = false;
				data.resp.lastSuccess[i].isdFlag = false;
				data.resp.lastSuccess[i].isdaFlag = false;
				data.resp.lastSuccess[i].impgFlag = false;
				data.resp.lastSuccess[i].impgsezFlag = false;
				data.resp.lastCall[i].ecomFlag = false;
				data.resp.lastCall[i].ecomaFlag = false;
				data.resp.lastSuccess[i].amendAttriFlag = false;

			}
			var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
			oView.setModel(oGstr1ASucessData, "Gstr2Get2ASucess");
		},

		onSelectAllCheckGet2A: function (oEvent) {
			var oSelectedFlag = oEvent.getSource().getSelected(),
				data = this.getView().getModel("Gstr2Get2ASucess").getProperty("/");

			if (oSelectedFlag) {
				this.byId("idgetVtablegstr6progstr2").selectAll();
				this.byId("idgetStatusgstr2").selectAll();
			} else {
				this.byId("idgetVtablegstr6progstr2").setSelectedKeys([]);
				this.byId("idgetStatusgstr2").setSelectedKeys([]);
			}
			for (var i = 0; i < data.resp.lastCall.length; i++) {
				data.resp.lastCall[i].b2bFlag = oSelectedFlag;
				data.resp.lastCall[i].b2baFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnFlag = oSelectedFlag;
				data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
				data.resp.lastCall[i].isdFlag = oSelectedFlag;
				data.resp.lastCall[i].isdaFlag = oSelectedFlag;
				data.resp.lastCall[i].impgFlag = oSelectedFlag;
				data.resp.lastCall[i].impgsezFlag = oSelectedFlag;
				data.resp.lastCall[i].ecomFlag = oSelectedFlag;
				data.resp.lastCall[i].ecomaFlag = oSelectedFlag;
				data.resp.lastCall[i].amendAttriFlag = oSelectedFlag;
			}
			for (var i = 0; i < data.resp.lastSuccess.length; i++) {
				data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
				data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
				data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].isdFlag = oSelectedFlag;
				data.resp.lastSuccess[i].isdaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].impgFlag = oSelectedFlag;
				data.resp.lastSuccess[i].impgsezFlag = oSelectedFlag;
				data.resp.lastCall[i].ecomFlag = oSelectedFlag;
				data.resp.lastCall[i].ecomaFlag = oSelectedFlag;
				data.resp.lastSuccess[i].amendAttriFlag = oSelectedFlag;
			}
			// var oGstr1ASucessData = new sap.ui.model.json.JSONModel(data);
			this.getView().getModel("Gstr2Get2ASucess").refresh();
		},

		/**
		 * Method called to change status of Checkbox on select of Tri-state checkbox
		 * Developed by: Bharat Gupta - 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onSelectGstr2aTriState: function () {
			var oModel = this.byId("dGetGstr2a").getModel("CheckStats"),
				oData = oModel.getData();
			oModel.setData(this._getGstr2aCheckStats(oData.select));
			oModel.refresh(true);
		},

		/**
		 * Method called to change status of Tri-state Checkbox on section of checkbox
		 * Developed by: Bharat Gupta - 07.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onSelectGstr2aCheckBox: function () {
			var oModel = this.byId("dGetGstr2a").getModel("CheckStats"),
				oData = oModel.getData(),
				selectFlag = false,
				partialFlag = true;

			for (var field in oData) {
				if (field === "select" || field === "partial") {
					continue;
				}
				selectFlag = selectFlag || oData[field];
				partialFlag = partialFlag && oData[field];
			}
			oData.select = selectFlag;
			oData.partial = !partialFlag;
			oModel.refresh(true);
		},

		/**
		 * Method called to Get GSTR-2A All/Failed Data based on selected section
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} action Action to get All/Failed Get Gstr-2A data
		 */
		onCloseGet2a: function (action) {
			if (action === "close") {
				this._getGstr2a.close();
				return;
			}
			var aIndices = this.byId("tab2aProcess").getSelectedIndices(),
				oCheckStats = this.byId("dGetGstr2a").getModel("CheckStats").getData(),
				oGet2aData = this.byId("tab2aProcess").getModel("Gstr2aProcessed").getData(),
				aSection = [],
				that = this,
				oPayload = {
					"req": []
				},
				oData = {
					"gstin": null,
					"ret_period": this.byId("dtGet2aProcess").getValue(),
					"gstr2aSections": [],
					"isFailed": (action === "allGet2a" ? false : true)
				};

			for (var field in oCheckStats) {
				if (oCheckStats[field] && field !== "select" && field !== "partial") {
					aSection.push(field.toUpperCase());
				}
			}
			if (aIndices.length > 0) {
				for (var i = 0; i < aIndices.length; i++) {
					var obj = $.extend(true, {}, oData);
					obj.gstin = oGet2aData[aIndices[i]].gstin;
					obj.gstr2aSections = aSection;
					oPayload.req.push(obj);
				}
			} else {
				var oItems = this.byId("slGet2aProcessGstin").getItems();
				for (i = 0; i < oItems.length; i++) {
					if (oItems[i].getKey() === "All") {
						continue;
					}
					obj = $.extend(true, {}, oData);
					obj.gstin = oItems[i].getKey();
					obj.gstr2aSections = aSection;
					oPayload.req.push(obj);
				}
			}
			that.byId("dpGetGstr2aProcess").setBusy(true);
			that._getGstr2a.close();
			$.ajax({
				method: "POST",
				url: "/aspsapapi/Gstr2aGstnGetSection.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				that.byId("dpGetGstr2aProcess").setBusy(false);
				if (data.hdr.status === "S") {
					that._getGstr2aProcess();
					MessageBox.success("Get initiated for selected(active) GSTINs.", {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					that._msgTabDialog(data.resp);
				}
			}).fail(function (jqXHR, status, err) {
				that.byId("dpGetGstr2aProcess").setBusy(false);
			});
		},

		/**
		 * Method called to Show Response Gstin and msg as popup
		 * Developed by: Bharat Gupta - 02.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} data Response Object
		 */
		_msgTabDialog: function (data) {
			if (!this._oMsgTable) {
				this._oMsgTable = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.TabMessage", this);
				this.getView().addDependent(this._oMsgTable);
			}
			this._oMsgTable.setModel(new JSONModel(data), "MessageTable");
			this._oMsgTable.open();
		},

		/**
		 * Method called to Close Message Dialog
		 * Developed by: Bharat Gupta - 02.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} data Response Object
		 */
		onCloseMessageTab: function () {
			this._oMsgTable.close();
		},

		/**
		 * Method called when user click search button to get GSTR-2A summary data
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} gstin Selected Gstin
		 */
		onGetGstr2aSummary: function (gstin) {
			var oGet2aData = this.byId("dpGetGstr2aProcess").getModel("Get2aPayload").getData(),
				oPropModel = this.getView().getModel("Get2aProp"),
				oPropData = oPropModel.getData(),
				vFrom = oGet2aData.taxPeriod;

			oPropData.process = false;
			oPropData.summary = true;
			oPropData.selectedGstin = gstin;
			// this.byId("dtGet2aSummary").setDateValue(new Date(vFrom.substr(2) + "-" + vFrom.substr(0, 2) + "-01"));

			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			var fromDate = this.byId("dtGet2aPTaxperiodFrom").getDateValue();
			fromDate = new Date(fromDate.setDate("01"));
			this.byId("dtGet2aSTaxperiodFrom").setMaxDate(new Date());
			this.byId("dtGet2aSTaxperiodTo").setMinDate(fromDate);
			this.byId("dtGet2aSTaxperiodTo").setMaxDate(new Date());
			this.byId("dtGet2aSTaxperiodFrom").setValue(this.byId("dtGet2aPTaxperiodFrom").getValue());
			this.byId("dtGet2aSTaxperiodTo").setValue(this.byId("dtGet2aPTaxperiodTo").getValue());

			this.byId("slGet2aSummaryGstin").setSelectedKey(gstin);
			this.byId("slGet2aSummaryTabType").setSelectedKeys(oGet2aData.tableType);
			this.byId("slGet2aSummaryDocType").setSelectedKeys(oGet2aData.docType);
			oPropModel.refresh(true);
			this._get2aReviewSummary();
		},

		/**
		 * Method called to get GSTR-2A Summary data from DB
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_get2aReviewSummary: function () {
			var oModel = this.byId("tabGet2aSummary").getModel("Get2aSummary"),
				oPayload = this._getGstr2aPayload("S"),
				that = this;
			this.byId("dpGetGstr2aSummary").setModel(new JSONModel(oPayload.req), "Get2aSummPayload");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			that.byId("dpGetGstr2aSummary").setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2aReviewSummaryData.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				that.byId("dpGetGstr2aSummary").setBusy(false);
				that._bindGet2aSummary(data.resp);
			}).fail(function (jqXHR, status, err) {
				that.byId("dpGetGstr2aSummary").setBusy(false);
			});
		},

		/**
		 * Method called to get JSON object to convert data in Summary object
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @return {Object} Get2a Summary object
		 */
		_jsonGet2aSummary: function () {
			return {
				"tableSection": null,
				"taxDocType": null,
				"table": null,
				"count": 0,
				"invoiceValue": 0,
				"taxableValue": 0,
				"taxPayble": 0,
				"igst": 0,
				"cgst": 0,
				"sgst": 0,
				"cess": 0,
				"items": []
			};
		},

		/**
		 * Method called to convert data to Hierarchical JSON object to bind in summary table
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} data Response object
		 */
		_bindGet2aSummary: function (data) {
			var aFields = ["b2b", "b2ba", "ecom", "ecoma", "cdn", "cdna", "isd", "isda", "impg", "impgsez", "amdImpg"],
				oSummary = [];

			aFields.forEach(function (e) {
				var oSummaryJson = this._jsonGet2aSummary(),
					aData = data[e];

				if (!aData.length) {
					oSummaryJson.tableSection = e;
				}
				aData.forEach(function (item) {
					if (item.taxDocType === "total" || ["b2b", "b2ba", "ecom", "ecoma"].includes(e)) {
						this._bindGet2aTotal(oSummaryJson, item);
					} else {
						var obj = $.extend({}, item);
						obj.tableSection = item.taxDocType;
						oSummaryJson.items.push(obj);
					}
				}.bind(this));
				oSummary.push(oSummaryJson);
			}.bind(this));
			this.byId("tabGet2aSummary").setModel(new JSONModel(oSummary), "Get2aSummary");
		},

		/**
		 * Method called to move Total data to summay table
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} obj Summary object
		 * @param {Object} data Response object
		 */
		_bindGet2aTotal: function (obj, data) {
			obj.tableSection = data.table;
			obj.taxDocType = data.taxDocType;
			obj.table = data.table;
			obj.count = data.count;
			obj.invoiceValue = data.invoiceValue;
			obj.taxableValue = data.taxableValue;
			obj.taxPayble = data.taxPayble;
			obj.igst = data.igst;
			obj.cgst = data.cgst;
			obj.sgst = data.sgst;
			obj.cess = data.cess;
		},

		/**
		 * Method called to Expand/Collapse Summary table
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} value Action Expand/collapse
		 */
		onPressGet2aExpandable: function (value) {
			if (value === "expand") {
				var flag = this.byId("idCcGet2aSummary").getFullScreen();
				this.byId("tabGet2aSummary").setVisibleRowCount(flag ? 20 : 11);
				this.byId("tabGet2aSummary").expandToLevel(1);
			} else {
				this.byId("tabGet2aSummary").setVisibleRowCount(11);
				this.byId("tabGet2aSummary").collapseAll();
			}
		},

		/**
		 * Method called when user select file type to change set upload file type
		 * Develped by: Bharat Gupta - 29.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {number} idx Radio button index
		 */
		onSelectFileType: function (idx) {
			var oUploader = this.byId("fuGetGstr2a");
			switch (idx) {
			case 0:
				oUploader.setFileType("xlsx,xlsm,xls,csv");
				break;
			case 1:
				oUploader.setFileType("txt,json");
				break;
			}
			oUploader.setValue(null);
		},

		/**
		 * Method called to upload File
		 * Develped by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onUploadGet2a: function () {
			var oBundle = this.getResourceBundle(),
				aGstin = this.byId("slGet2aProcessGstin").getSelectedKeys(),
				oUploader = this.byId("fuGetGstr2a"),
				vIdx = this.byId("rgbFileType").getSelectedIndex();

			if (oUploader.getValue() === "") {
				MessageBox.information(oBundle.getText("gstr2aSelectFile"), {
					styleClass: "sapUiSizeCompact"
				});
				return;
			}
			switch (vIdx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/gstr2aB2bFileUploadDocuments.do");
				break;
			case 1:
				aGstin = this.removeAll(aGstin);
				if (aGstin.length !== 1) {
					MessageBox.information(oBundle.getText("gstr2aUploadWarning"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				oUploader.setUploadUrl("/aspsapapi/jsonInput.do");
				oUploader.setAdditionalData(aGstin[0]);
				break;
			}
			oUploader.upload();
		},

		/** 	
		 * Method called when file upload complete
		 * Developed by: Bharat Gupta - 19.04.2020
		 * Modified by: Bharat Gupta - 22.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onGet2aUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuGetGstr2a").setValue();
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				if (sResponse.hdr.message) {
					MessageBox.error(sResponse.hdr.message, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					MessageBox.error(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}
		},

		/**
		 * Method called to get Section wise status for Get GSTR-2A Status
		 * Developed by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @return {Object} Section Status object
		 */
		_sectionStatus: function () {
			return {
				"segment": "all",
				"allSelect": false,
				"allPartial": false,
				"b2bStats": false,
				"b2bPartial": false,
				"b2baStats": false,
				"b2baPartial": false,
				"cdnStats": false,
				"cdnPartial": false,
				"cdnaStats": false,
				"cdnaPartial": false,
				"isdStats": false,
				"isdPartial": false,
				"isdaStats": false,
				"isdaPartial": false
			};
		},

		/**
		 * Method called to open Get GSTR-2A Status Popup
		 * Develped by: Bharat Gupta - 28.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} gstin Selected Gstin
		 */
		onPressGet2aStatus: function (gstin) {
			this.vGstinGet2A = gstin;
			this.onPressGet2aStatusFinal();
		},

		onPressGet2aStatusFinal: function () {
			var gstin = this.vGstinGet2A;
			if (!this._getGstr2aStatus) {
				this._getGstr2aStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.GetGstr2aStatus", this);
				this.getView().addDependent(this._getGstr2aStatus);
			}
			this._getGstr2aStatus.open();
			var oGet2aPayload = this.byId("dpGetGstr2aProcess").getModel("Get2aPayload").getData(),
				oSecStats = this._sectionStatus(),
				aGstin = oGet2aPayload.dataSecAttrs.gstin,
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": oGet2aPayload.taxPeriod,
						"gstin": [gstin]
					}
				};
			// if (!aGstin) {
			// 	var oGet2aData = this.byId("tab2aProcess").getModel("Gstr2aProcessed").getData();
			// 	for (var i = 0; i < oGet2aData.length; i++) {
			// 		oPayload.req.gstin.push(oGet2aData[i].gstin);
			// 	}
			// }
			this.byId("dGet2aStatus").setModel(new JSONModel(oPayload), "Get2aStautsPayload");
			this.byId("dGet2aStatus").setModel(new JSONModel(oSecStats), "Get2aStatsProp");
			this._getAllGet2aData();
		},

		onChangeGet2aSegment: function () {
			var oPropData = this.byId("dGet2aStatus").getModel("Get2aStatsProp").getData();
			if (oPropData.segment === "all") {
				this._getAllGet2aData();
			} else {
				this._getFailedGet2aData();
			}
		},

		/**
		 * Method Called to get All GSTR-2A data
		 * Develped by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_getAllGet2aData: function () {
			var that = this,
				oPayload = this.byId("dGet2aStatus").getModel("Get2aStautsPayload").getData(),
				oGet2aStats = this.byId("dGet2aStatus").getModel("Get2aStauts");

			if (oGet2aStats) {
				oGet2aStats.setData(null);
				oGet2aStats.refresh(true);
			}
			that.byId("dGet2aStatus").setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2aDetailStatus.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				that.byId("dGet2aStatus").setBusy(false);
				that._bindGet2aStatus(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method Called to get Only Failed GSTR-2A data
		 * Develped by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		_getFailedGet2aData: function () {
			var that = this,
				oPayload = this.byId("dGet2aStatus").getModel("Get2aStautsPayload").getData(),
				oGet2aStats = this.byId("dGet2aStatus").getModel("Get2aStauts");

			if (oGet2aStats) {
				oGet2aStats.setData(null);
				oGet2aStats.refresh(true);
			}
			that.byId("dGet2aStatus").setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr2aFailedStatus.do",
				contentType: "application/json",
				data: JSON.stringify(oPayload)
			}).done(function (data, status, jqXHR) {
				that.byId("dGet2aStatus").setBusy(false);
				that._bindGet2aStatus(data.resp);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/**
		 * Method Called to bind Data to GSTR-2A Status dialog
		 * Develped by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} data Response object
		 */
		_bindGet2aStatus: function (data) {
			for (var i = 0; i < data.length; i++) {
				data[i].b2bFlag = false;
				data[i].b2baFlag = false;
				data[i].cdnFlag = false;
				data[i].cdnaFlag = false;
				data[i].isdFlag = false;
				data[i].isdaFlag = false;
				data[i].impgFlag = false;
				data[i].impgsezFlag = false;
				data[i].ecomFlag = false;
				data[i].ecomaFlag = false;
				data[i].importFlag = false;
				data[i].lsFlag = false;
			}
			this._showing2A(false);
			this.byId("dGet2aStatus").setModel(new JSONModel(data), "Get2aStauts");
		},

		/**
		 * Method called when check Select All checkbox in GSTR-2A Status dialog
		 * Develped by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {boolean} flag Checkbox status
		 */
		onGet2aSelectAll: function (flag) {
			var oStatsModel = this.byId("dGet2aStatus").getModel("Get2aStauts"),
				oPropModel = this.byId("dGet2aStatus").getModel("Get2aStatsProp"),
				oStatsData = oStatsModel.getData(),
				oPropData = oPropModel.getData();

			for (var field in oPropData) {
				if (field.includes("isda")) {
					continue;
				}
				if (field.includes("Partial")) {
					oPropData[field] = false;
					continue;
				}
				oPropData[field] = flag;
			}
			for (var i = 0; i < oStatsData.length; i++) {
				var obj = oStatsData[i];
				for (field in obj) {
					if (field.includes("Select")) {
						obj[field] = flag;
					}
				}
			}
			oPropModel.refresh(true);
			oStatsModel.refresh(true);
		},

		/**
		 * Method called to close Get GSTR-2A Status Popup
		 * Develped by: Bharat Gupta - 28.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 */
		onCloseGet2aStatus: function () {
			this._getGstr2aProcess();
			this._getGstr2aStatus.close();
		},

		/**
		 * Method called to reset filter value to default
		 * Develped by: Bharat Gupta - 30.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} view Clear button from view
		 */
		onPressClearGet2a: function (view) {
			var vDate = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1);

			if (view === "Process") {
				this._setDateProperty("dtGet2aPTaxperiodFrom", vPeriod, vDate);
				this._setDateProperty("dtGet2aPTaxperiodTo", vDate, vDate, vPeriod);

				this.byId("slGet2aProcessGstin").setSelectedKeys();
				this.byId("slGet2aProcessTabType").setSelectedKeys();
				this.byId("slGet2aProcessDocType").setSelectedKeys();
				this._getGstr2aProcess();
			} else {
				var oData = this.getView().getModel("Get2aProp").getData();

				this._setDateProperty("dtGet2aSTaxperiodFrom", vPeriod, vDate);
				this._setDateProperty("dtGet2aSTaxperiodTo", vDate, vDate, vPeriod);

				this.byId("slGet2aSummaryGstin").setSelectedKey(oData.selectedGstin);
				this.byId("slGet2aSummaryTabType").setSelectedKeys();
				this.byId("slGet2aSummaryDocType").setSelectedKeys();
				this._get2aReviewSummary();
			}
		},

		/**
		 * Method called to Expand / Collapse selected row
		 * Develped by: Bharat Gupta - 02.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		onToggleGet2A: function (oEvent) {
			var aData = oEvent.getSource().getBinding().getModel("Get2aSummary").getData(),
				oGroupId = oEvent.getSource().getBinding()._mTreeState.expanded,
				vRowCount = 9;

			for (var groupId in oGroupId) { //eslint-disable-line
				var aPath = groupId.split("/");
				if (aPath.length < 3) {
					continue;
				}
				vRowCount += aData[aPath[1]].items.length;
			}
			oEvent.getSource().setVisibleRowCount(vRowCount);
		},

		/**
		 * Method called to format Get GSTR-2A table section
		 * Developed by: Bharat Gupta - 19.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} vValue Value to formatted
		 * @return {string} Formatted Value
		 */
		_get2aSummaryTables: function (vValue) {
			var oBundle = this.getResourceBundle(),
				vText = oBundle.getText("2a_" + vValue);

			if (vText) {
				return vText;
			}
			return vValue;
		},

		/**
		 * Method Called to format Section Status
		 * Develped by: Bharat Gupta - 04.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {string} value Value to format
		 * @return {string} vState State
		 */
		statusFormat: function (value) {
			switch (value) {
			case "Success":
				var vState = "Success";
				break;
			case "Failed":
			case "NOT INITIATED":
				vState = "Error";
				break;
			case "INITIATED":
				vState = "Information";
				break;
			case "Partailly Success":
				vState = "Warning";
				break;
			default:
				vState = "None";
				break;
			}
			return vState;
		},

		/**
		 * Method called to Downlaod Get GSTR-2A Report
		 * Developed by: Bharat Gupta - 18.-07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param {Object} oEvent Eventing object
		 * @param {Object} item Selected Report type to download
		 * @param {string} view View 
		 */
		onDownloadGet2aReport: function (oEvent, item, view) {
			var oBundle = this.getResourceBundle(),
				// aReportType = ["getGstr2A"],
				oPayload = {
					"req": {
						"entityId": [$.sap.entityID],
						"fromPeriod": null,
						"toPeriod": null,
						"tableType": [],
						"docType": [],
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (view === "P") {
				var aIndex = this.byId("tab2aProcess").getSelectedIndices(),
					oData = this.byId("tab2aProcess").getModel("Gstr2aProcessed").getData(),
					o2aPayload = this.byId("dpGetGstr2aProcess").getModel("Get2aPayload").getData();

				if (aIndex.length === 0) {
					MessageBox.information(oBundle.getText("msgMin1RcrdDownload"), {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
				for (var i = 0; i < aIndex.length; i++) {
					oPayload.req.dataSecAttrs.GSTIN.push(oData[aIndex[i]].gstin);
				}
			} else if (view === "S") {
				o2aPayload = this.byId("dpGetGstr2aSummary").getModel("Get2aSummPayload").getData();
				oPayload.req.dataSecAttrs = o2aPayload.dataSecAttrs;
			} else {
				o2aPayload = this.byId("dGet2aStatus").getModel("Get2aStautsPayload").getData().req;
				oPayload.req.dataSecAttrs.GSTIN = o2aPayload.gstin;
			}
			// oPayload.req.taxPeriod = o2aPayload.taxPeriod;
			oPayload.req.fromPeriod = o2aPayload.fromPeriod;
			oPayload.req.toPeriod = o2aPayload.toPeriod;
			oPayload.req.tableType = !o2aPayload.tableType ? [] : o2aPayload.tableType;
			oPayload.req.docType = !o2aPayload.docType ? [] : o2aPayload.docType;
			// this.excelDownload(oPayload, "/aspsapapi/gstr2AReportsDownload.do");
			this.reportDownload(oPayload, "/aspsapapi/asyncgstr2AConsolidatedReports.do");
		},

		//////////////////////////Start/////////////////////////////////////
		////////////////////Get GSTR-2B Vinay Kodam/////////////////////////
		onSelectSbGstr2b: function (oEvent) {
			// 
		},

		_getGstr2b1: function (fy) {
			var Gstr2B = {
				"req": {
					"enityId": $.sap.entityID,
					"fy": fy
				}
			};
			this._getGstr2b3(Gstr2B);
		},

		_getGstr2b: function () {
			var Gstr2B = {
				"req": {
					"enityId": $.sap.entityID,
					"fy": this.byId("dtFinYearGstr123").getSelectedKey()
				}
			};
			this._getGstr2b3(Gstr2B);
		},

		_getGstr2b3: function (Gstr2B) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getGSTR2bDashboard.do",
					contentType: "application/json",
					data: JSON.stringify(Gstr2B)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var odata = data.resp;
					this.arry = [];
					for (var i = 0; i < odata.length; i++) {
						var itemArr = odata[i].taxPeriodDetails;
						if (itemArr !== undefined) {
							for (var j = 0; j < itemArr.length; j++) {
								var timep = itemArr[j].taxPeriod;
								var intiatedOnDt = itemArr[j].initiatedOn;
								var Status = itemArr[j].reportStatus;
								var downloadbleFlag = itemArr[j].flag;
								var filePath = itemArr[j].filePath;
								var errorMsg = itemArr[j].errorMsg;
								if (timep.slice(0, 2) === "04") {
									odata[i].Apriltimep = timep;
									odata[i].statusAprl = Status;
									odata[i].initiatedOnApril = intiatedOnDt;
									odata[i].filePathAprl = filePath;
									odata[i].downloadbleFlagAprl = downloadbleFlag;
									odata[i].AprilerrorMsg = errorMsg;
									odata[i].AprilCheck = false;
								} else if (timep.slice(0, 2) === "05") {
									odata[i].Maytimep = timep;
									odata[i].statusMay = Status;
									odata[i].initiatedOnMay = intiatedOnDt;
									odata[i].filePathMay = filePath;
									odata[i].downloadbleFlagMay = downloadbleFlag;
									odata[i].MayterrorMsg = errorMsg;
									odata[i].MayCheck = false;
								} else if (timep.slice(0, 2) === "06") {
									odata[i].Junetimep = timep;
									odata[i].statusJune = Status;
									odata[i].initiatedOnJune = intiatedOnDt;
									odata[i].filePathJune = filePath;
									odata[i].downloadbleFlagJune = downloadbleFlag;
									odata[i].JuneerrorMsg = errorMsg;
									odata[i].JuneCheck = false;
								} else if (timep.slice(0, 2) === "07") {
									odata[i].Julytimep = timep;
									odata[i].statusJuly = Status;
									odata[i].initiatedOnJuly = intiatedOnDt;
									odata[i].filePathJuly = filePath;
									odata[i].downloadbleFlagJuly = downloadbleFlag;
									odata[i].JulyerrorMsg = errorMsg;
									odata[i].JulyCheck = false;
								} else if (timep.slice(0, 2) === "08") {
									odata[i].Augtimep = timep;
									odata[i].statusAug = Status;
									odata[i].initiatedOnAug = intiatedOnDt;
									odata[i].filePathAug = filePath;
									odata[i].downloadbleFlagAug = downloadbleFlag;
									odata[i].AugerrorMsg = errorMsg;
									odata[i].AugCheck = false;
								} else if (timep.slice(0, 2) === "09") {
									odata[i].Septtimep = timep;
									odata[i].statusSept = Status;
									odata[i].initiatedOnSept = intiatedOnDt;
									odata[i].filePathSept = filePath;
									odata[i].downloadbleFlagSept = downloadbleFlag;
									odata[i].SepterrorMsg = errorMsg;
									odata[i].SepCheck = false;
								} else if (timep.slice(0, 2) === "10") {
									odata[i].Octtimep = timep;
									odata[i].statusOct = Status;
									odata[i].initiatedOnOct = intiatedOnDt;
									odata[i].filePathOct = filePath;
									odata[i].downloadbleFlagOct = downloadbleFlag;
									odata[i].OcterrorMsg = errorMsg;
									odata[i].OctCheck = false;
								} else if (timep.slice(0, 2) === "11") {
									odata[i].Novtimep = timep;
									odata[i].statusNov = Status;
									odata[i].initiatedOnNov = intiatedOnDt;
									odata[i].filePathNov = filePath;
									odata[i].downloadbleFlagNov = downloadbleFlag;
									odata[i].NoverrorMsg = errorMsg;
									odata[i].NovCheck = false;
								} else if (timep.slice(0, 2) === "12") {
									odata[i].Dectimep = timep;
									odata[i].statusDec = Status;
									odata[i].initiatedOnDec = intiatedOnDt;
									odata[i].filePathDec = filePath;
									odata[i].downloadbleFlagDec = downloadbleFlag;
									odata[i].DecterrorMsg = errorMsg;
									odata[i].DecCheck = false;
								} else if (timep.slice(0, 2) === "01") {
									odata[i].Jantimep = timep;
									odata[i].statusJan = Status;
									odata[i].initiatedOnJan = intiatedOnDt;
									odata[i].filePathJan = filePath;
									odata[i].downloadbleFlagJan = downloadbleFlag;
									odata[i].JanerrorMsg = errorMsg;
									odata[i].JanCheck = false;
								} else if (timep.slice(0, 2) === "02") {
									odata[i].Febtimep = timep;
									odata[i].statusFeb = Status;
									odata[i].initiatedOnFeb = intiatedOnDt;
									odata[i].filePathFeb = filePath;
									odata[i].downloadbleFlagFeb = downloadbleFlag;
									odata[i].FeberrorMsg = errorMsg;
									odata[i].FebCheck = false;
								} else if (timep.slice(0, 2) === "03") {
									odata[i].Martimep = timep;
									odata[i].statusMar = Status;
									odata[i].initiatedOnMar = intiatedOnDt;
									odata[i].filePathMar = filePath;
									odata[i].downloadbleFlagMar = downloadbleFlag;
									odata[i].MarerrorMsg = errorMsg;
									odata[i].MarCheck = false;
								}
							}
						}
						this.arry.push(odata[i]);
					}
					this.getView().setModel(new JSONModel(this.arry), "GSTR2BTable");
					this.byId("gstr2BTabid").clearSelection();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onAprilCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData(),
				Fy = this.byId("dtFinYearGstr123").getSelectedKey(),
				Tp = "04";

			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].AprilCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (selction === undefined) {
						for (var i = 0; i < data.length; i++) {
							data[i].AprilCheck = false;
						}
					}
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onMayCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData(),
				Fy = this.byId("dtFinYearGstr123").getSelectedKey(),
				Tp = "05";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].MayCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].MayCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onJuneCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "06";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JuneCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JuneCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onJulyCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "07";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JulyCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JulyCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onAugCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "08";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].AugCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].AugCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onSepCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "09";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].SepCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].SepCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onOctCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "10";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].OctCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].OctCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onNovCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "11";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].NovCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].NovCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onDecCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "12";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].DecCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (selction === undefined) {
						for (var i = 0; i < data.length; i++) {
							data[i].DecCheck = false;
						}
					}
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onJanCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "01";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JanCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].JanCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onFebCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "02";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].FebCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].FebCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onMarCheckBox: function (oEvt, selction) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var Fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var Tp = "03";
			if (oEvt.getSource().getSelected()) {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].MarCheck = true;
					}
				}
				if (!this.Gstr2BCheck.includes(Tp)) {
					this.Gstr2BCheck.push(Tp);
				}
				//this.Gstr2BCheck.push(Tp);
			} else {
				if (selction === undefined) {
					for (var i = 0; i < data.length; i++) {
						data[i].MarCheck = false;
					}
				}
				for (var ap = 0; ap < this.Gstr2BCheck.length; ap++) {
					if (this.Gstr2BCheck[ap] === Tp) {
						this.Gstr2BCheck.splice(ap, 1);
					}
				}
			}
			this.getView().getModel("GSTR2BTable").refresh(true);
		},

		onSelectionGstr2B: function (oEvent) {
			var oModel = this.getView().getModel("GSTR2BTable"),
				oPropModel = this.getView().getModel("2BProperty"),
				oPropData = oPropModel.getProperty("/"),
				oData = oModel.getProperty("/"),
				vRowIdx = oEvent.getParameter("rowIndex"),
				aMonth = ["April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"]; // Define the months

			// Check if select all or if the row index is -1 (indicating a deselect)
			if (oEvent.getParameters().selectAll || vRowIdx === -1) {
				var flag = oEvent.getParameters().selectAll || false; // Determine if we are selecting or deselecting
				aMonth.forEach(function (m, i) {
					oPropData[m] = flag; // Set the property for the month
					oData.forEach(function (el) {
						el[m + "Check"] = flag; // Update each record's check status
					});
				});
			} else {
				var aIndex = this.getView().byId("gstr2BTabid").getSelectedIndices(), // Get selected indices
					flag = aIndex.includes(vRowIdx); // Check if the current row index is selected
				aMonth.forEach(function (m, i) {
					oData[vRowIdx][m + "Check"] = flag; // Update the specific row's check status
					oPropData[m] = (aIndex.length === oData.length); // Check if all rows are selected for the month
				}.bind(this));
			}

			// Refresh the models to reflect changes
			oPropModel.refresh(true);
			oModel.refresh(true);
		},

		onInitiateGstr2B: function () {
			var that = this;
			MessageBox.confirm("Do you want to Initiate Get Gstr-2B?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "YES") {
						that.onInitiateGstr2B1();
					}
				}
			});
		},

		onInitiateGstr2B1: function () {
			var aMonth = ["April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"],
				oData = this.getView().getModel("GSTR2BTable").getProperty("/"),
				TablIndx = this.byId("gstr2BTabid").getSelectedIndices(),
				oProperty = this.getView().getModel("2BProperty").getProperty("/"),
				payload = {
					"req": {
						"gstins": [],
						"month": [],
						"fy": this.byId("dtFinYearGstr123").getSelectedKey()
					}
				};

			aMonth.forEach(function (m, i) {
				if (oProperty[m]) {
					var mm = (i === 8 ? 12 : (i + 4) % 12);
					payload.req.month.push(mm.toString().padStart(2, 0));
				}
			});
			if (!TablIndx.length) {
				MessageBox.information("Please select at least one GSTIN");
				return;
			}
			if (!payload.req.month.length) {
				MessageBox.information("Please Select at least one Month");
				return;
			}
			payload.req.gstins = TablIndx.map(function (idx) {
				return oData[idx].gstin;
			});

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2BResp.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var arr = ["Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"],
						oModel = this.getView().getModel("2BProperty");

					arr.forEach(function (e) {
						oModel.setProperty("/" + e, false);
					});
					oModel.refresh(true);
					this.Gstr2BCheck = [];
					if (data.hdr.status === "S") {
						MessageBox.success("GSTR2B Get call initiated successfully", {
							styleClass: "sapUiSizeCompact"
						});
						setTimeout(function () {
							this._getGstr2b();
						}.bind(this), 5000);
					} else {
						MessageBox.error(data.hdr.message);
						sap.ui.core.BusyIndicator.hide();
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onDownloadApril: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathAprl);
		},
		onDownloadMay: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathMay);
		},
		onDownloadJune: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathJune);
		},
		onDownloadJuly: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathJuly);
		},
		onDownloadAug: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathAug);
		},
		onDownloadSep: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathSept);
		},
		onDownloadOct: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathOct);
		},
		onDownloadNov: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathNov);
		},
		onDownloadDec: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathDec);
		},
		onDownloadJan: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathJan);
		},
		onDownloadFeb: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathFeb);
		},
		onDownloadMar: function (oEvent) {
			var model = oEvent.getSource().getEventingParent().getBindingContext("GSTR2BTable").getObject();
			this.DownloadFile(model.filePathMar);
		},

		DownloadFile: function (filePath) {
			var oReqExcelPath = "/aspsapapi/gstr2BDownloadFile.do?filePath=" + filePath;
			window.open(oReqExcelPath);
		},

		_getPayload: function (reportType) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			var fy = this.byId("dtFinYearGstr123").getSelectedKey();
			var payload = {
				"req": [],
				"reportType": reportType,
				"finYear": fy
			};

			// Iterate through the data to build the payload
			data.forEach(function (item) {
				var taxPeriods = [];

				// Check which months have check set to true
				if (item.AprilCheck) taxPeriods.push("04"); // April
				if (item.MayCheck) taxPeriods.push("05"); // May
				if (item.JuneCheck) taxPeriods.push("06"); // June
				if (item.JulyCheck) taxPeriods.push("07"); // July
				if (item.AugCheck) taxPeriods.push("08"); // August
				if (item.SepCheck) taxPeriods.push("09"); // September
				if (item.OctCheck) taxPeriods.push("10"); // October
				if (item.NovCheck) taxPeriods.push("11"); // November
				if (item.DecCheck) taxPeriods.push("12"); // December
				if (item.JanCheck) taxPeriods.push("01"); // January
				if (item.FebCheck) taxPeriods.push("02"); // February
				if (item.MarCheck) taxPeriods.push("03"); // March

				// Only add to the payload if there are tax periods
				if (taxPeriods.length > 0) {
					payload.req.push({
						"gstin": item.gstin,
						"taxPeriod": taxPeriods
					});
				}
			});

			return payload;
		},

		onMenuItemPressGSTR2B: function (oEvt) {
			var reportType = oEvt.getParameter("item").getKey();
			var payload = this._getPayload(reportType);

			if (payload.req.length === 0) {
				MessageBox.information("Please select at least one Period.");
				return;
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2BReports.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = this.getView().getModel("2BProperty"),
						aMonth = ["April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"];

					aMonth.forEach(function (m) {
						oModel.setProperty("/" + m, false);
					});
					oModel.refresh(true);

					this.Gstr2BCheck = [];
					if (data.hdr.status === "S") {
						this._getGstr2b();
						MessageBox.success("Success, please check Request status", {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onGstr2BRequestStatus: function () {
			this.byId("idDynmcPageGstr2B").setVisible(false);
			this.byId("idRequestStatus2B").setVisible(true);
			/*var oReqWiseInfo = {
				"entityId": $.sap.entityID.toString()
			};*/

			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/aspsapapi/getGstr2BReportRequestStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: [{}]
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "ReqWiseData2B");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPress2Brequest: function () {
			this.byId("idDynmcPageGstr2B").setVisible(true);
			this.byId("idRequestStatus2B").setVisible(false);
		},

		onDwnldTablData: function () {
			var Gstr2B = {
				"req": {
					"entityId": $.sap.entityID,
					"fy": this.byId("dtFinYearGstr123").getSelectedKey()
				}
			};
			var path = "/aspsapapi/gstr2TimeStampReport.do";
			this.excelDownload(Gstr2B, path);
		},

		onRefreshRequestIDwise2B: function () {
			this.onGstr2BRequestStatus();
		},

		onConfigExtractPress2B: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getBindingContext("ReqWiseData2B").getObject().reqId;
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.Async.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			var oData = {
				"visFlag": false
			}
			this.getView().setModel(new JSONModel(oData), "visFlagModel");

			var oIntiData = {
				"req": {
					"requestId": this.oReqId
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/generateGstr2bDownloadIdWise.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "DownloadDocument");
					}
				}).fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "DownloadDocument");
				});
			});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onDownloadPress: function (oEvt) {
			var oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("DownloadDocument").getObject().requestId;
			var oReqExcelPath = "/aspsapapi/downloadGstr2BReport.do?requestId=" + oReqId
			window.open(oReqExcelPath);
		},

		gstr2BGstin: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("ReqWiseData2B").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2B").getObject().reqId;
			for (var i = 0; i < TabData.resp.requestDetails.length; i++) {
				if (reqId === TabData.resp.requestDetails[i].reqId) {
					gstins.push(TabData.resp.requestDetails[i].gstinList);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},

		gstr2BTaxPeriod: function (oEvt) {
			var taxPeriodList = [];
			var TabData = this.getView().getModel("ReqWiseData2B").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2B").getObject().reqId;
			for (var i = 0; i < TabData.resp.requestDetails.length; i++) {
				if (reqId === TabData.resp.requestDetails[i].reqId) {
					taxPeriodList.push(TabData.resp.requestDetails[i].taxPeriodList);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(taxPeriodList[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover1) {
				this._oGstinPopover1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2BTaxPeriod", this);
				this.getView().addDependent(this._oGstinPopover1);
			}
			this._oGstinPopover1.setModel(oReqWiseModel1, "gstins2B");
			this._oGstinPopover1.openBy(oButton);
		},

		onGstr2BFullScreen: function (oEvt) {
			var data = this.getView().getModel("GSTR2BTable").getData();
			if (oEvt === "open") {
				this.byId("closebut2B").setVisible(true);
				this.byId("openbut2B").setVisible(false);
				this.byId("G2B").setFullScreen(true);
				this.byId("gstr2BTabid").setVisibleRowCount(22);
			} else {
				this.byId("closebut2B").setVisible(false);
				this.byId("openbut2B").setVisible(true);
				this.byId("G2B").setFullScreen(false);
				this.byId("gstr2BTabid").setVisibleRowCount(10);
			}
		},

		onSelectFyGenerate2b: function () {
			var fy = this.byId("sGenerate2bFY").getSelectedKey();
			this._bindGenerateGstr2b(fy);
		},

		_bindGenerateGstr2b: function (fy) {
			var aMonth = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"],
				sFyear = +fy.split('-')[0],
				vToFy = new Date(sFyear + 1, 2, 31, 23, 59, 59),
				vFrFy = new Date(sFyear, 3),
				today = new Date(),
				y = today.getFullYear(),
				m = today.getMonth(),
				d = today.getDate(),
				obj = {};

			var isWithinFY = (today >= vFrFy && today <= vToFy);
			aMonth.forEach(function (month, i) {
				var fYear = (i < 3 ? sFyear + 1 : sFyear),
					monthEnable = isWithinFY ? ((!m ? 12 : m) - i === 1 ? !(d < 14) : (new Date(y, m) > new Date(fYear, i))) : true;
				obj[month + "Enable"] = monthEnable;
				obj[month + "Select"] = false;
			});
			this.getView().setModel(new JSONModel(obj), "GenGstr2bCheck");
		},

		onFilterGenerate2B: function () {
			var vFy = this.byId("sGenerate2bFY").getSelectedKey();
			this._getGenerateGstr2b(vFy);
		},

		_getGenerateGstr2b: function (fy) {
			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						"method": "POST",
						"url": "/aspsapapi/gstr2bRegenerationDashboard.do",
						"contentType": "application/json",
						data: JSON.stringify({
							"req": {
								"enityId": $.sap.entityID,
								"fy": fy
							}
						})
					})
					.done(function (data, status, jqXHR) {
						this._bindGenerate2B(data);
						sap.ui.core.BusyIndicator.hide();
						resolve(data);
					}.bind(this))
					.fail(function () {
						sap.ui.core.BusyIndicator.hide();
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getGenerate2bMonths: function (m) {
			var aMonth = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
			if (!m) {
				return aMonth;
			}
			return aMonth[+m - 1];
		},

		_bindGenerate2B: function (data) {
			var aMonth = this._getGenerate2bMonths(),
				oData = [];
			data.resp.forEach(function (item) {
				var obj = {
					"authStatus": item.authStatus,
					"gstin": item.gstin,
					"stateName": item.stateName
				};
				if (item.taxPeriodDetails) {
					item.taxPeriodDetails.forEach(function (e) {
						var m = +e.taxPeriod.substr(0, 2),
							mm = aMonth[m - 1];

						obj[mm + "IsReGenerate2b"] = e.isReGenerate2b;
						obj[mm + "ReportStatus"] = e.reportStatus;
						obj[mm + "InitiatedOn"] = e.initiatedOn;
						obj[mm + "ErrorMsg"] = e.errorMsg;
						obj[mm + "Check"] = false;
					});
				}
				oData.push(obj);
			});
			this.getView().setModel(new JSONModel(oData), "GenerateGstr2B");
		},

		onGen2bRowSelectionChange: function (oEvent) {
			var oModel = this.getView().getModel("GenerateGstr2B"),
				oColModel = this.getView().getModel("GenGstr2bCheck"),
				oColData = oColModel.getProperty("/"),
				aMonth = this._getGenerate2bMonths(),
				oData = oModel.getProperty("/");

			if (oEvent.getParameter("selectAll") || oEvent.getParameter("rowIndex")) {
				var vSelectAll = oEvent.getParameter("selectAll") || false;
				aMonth.forEach(function (m) {
					oColData[m + "Select"] = (oColData[m + "Enable"] && vSelectAll);
					oData.forEach(function (e) {
						if (e.hasOwnProperty(m + "Check")) {
							e[m + "Check"] = vSelectAll;
						}
					});
				});
			}
			oColModel.refresh(true);
			oModel.refresh(true);
		},

		onSelectGen2bCols: function (oEvent, mm) {
			var oModel = this.getView().getModel("GenerateGstr2B"),
				vMonth = this._getGenerate2bMonths(mm),
				oData = oModel.getProperty("/");

			oData.forEach(function (e) {
				if (e.hasOwnProperty(vMonth + "Check")) {
					e[vMonth + "Check"] = (e[vMonth + "IsReGenerate2b"] && oEvent.getParameter('selected'));
				}
			}.bind(this));
			oModel.refresh(true);
		},

		onGenerateGstr2B: function () {
			var oSelect = this.getView().getModel("GenGstr2bCheck").getProperty("/"),
				oColData = this.getView().getModel("GenGstr2bCheck").getProperty("/"),
				oData = this.getView().getModel("GenerateGstr2B").getProperty("/"),
				aIdx = this.byId("tabGenerate2B").getSelectedIndices(),
				vFy = this.byId("sGenerate2bFY").getSelectedKey(),
				aMonth = this._getGenerate2bMonths(),
				payload = {
					"req": []
				};
			if (!aIdx.length) {
				MessageBox.warning("Select at least one GSTIN");
				return;
			}
			aIdx.forEach(function (e) {
				aMonth.forEach(function (m, i) {
					var field = m + "Check";
					if (oColData[m + "Select"] && oData[e].hasOwnProperty(field) && oData[e][field] && oData[e][m + "IsReGenerate2b"]) {
						var obj = payload.req.find(function (p) {
							return p.gstin === oData[e].gstin
						});
						if (!obj) {
							payload.req.push({
								"gstin": oData[e].gstin,
								"finYear": vFy,
								"month": [(i + 1).toString().padStart(2, 0)]
							});
						} else {
							var mm = (i + 1).toString().padStart(2, 0);
							obj.month.push(mm);
						}
					}
				});
			});
			if (!payload.req.length) {
				MessageBox.warning("Please select Return Period.");
				return;
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					"method": "POST",
					"url": "/aspsapapi/initiateGstr2bRegenerateApi.do",
					"contentType": "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (!this._msgList) {
						this._msgList = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.MsgList", this);
						this.getView().addDependent(this._msgList);
					}
					this._msgList.setTitle("Generate GSTR-2B Status");
					this._msgList.setModel(new JSONModel(data.resp), "MessageList");
					this._msgList.setState(data.hdr.status === "S" ? 'Success' : 'Error');
					this._msgList.open();
					setTimeout(function () {
						this.onFilterGenerate2B();
					}.bind(this), 5000);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onCloseMsgList: function () {
			this._msgList.close();
		},

		onFilter2bLinkAmt: function () {
			var vFy = this.byId("sLinkAmt2bFY").getSelectedKey();
			this._get2bLinkAmt(vFy);
		},

		_get2bLinkAmt: function (fy) {
			var payload = {
				"req": {
					"entityId": [$.sap.entityID],
					"financialYear": fy
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2bLinkingData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var aMonth = this._getGenerate2bMonths(),
						aData = data.resp.map(function (e) {
							var obj = {
								gstin: e.gstin,
								stateName: e.stateName,
								authToken: e.authToken
							};
							e.item.forEach(function (item) {
								var mm = aMonth[+item.taxPeriod.substr(0, 2) - 1];
								obj[mm + "Linked"] = item.linked;
								obj[mm + "NotLinked"] = item.notLinked;
								obj[mm + "LinkStatus"] = item.linkingStatus;
								obj[mm + "LastUpdatedOn"] = item.lastUpdatedOn;
								obj[mm + "Check"] = false;
							});
							return obj;
						});
					this.getView().setModel(new JSONModel(aData), "Gstr2bLinkAmt");
					this.byId("tab2bLinkAmt").clearSelection();
					sap.ui.core.BusyIndicator.hide();
					this._bindGstr2bLinkAmt(fy);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onSelectFyLinkAmt: function () {
			var fy = this.byId("sLinkAmt2bFY").getSelectedKey();
			this._bindGstr2bLinkAmt(fy);
		},

		_bindGstr2bLinkAmt: function (fy) {
			var aMonth = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"],
				sFyear = +fy.split('-')[0],
				vToFy = new Date(sFyear + 1, 2, 31, 23, 59, 59),
				vFrFy = new Date(sFyear, 3),
				today = new Date(),
				y = today.getFullYear(),
				m = today.getMonth(),
				d = today.getDate(),
				obj = {};

			var isWithinFY = (today >= vFrFy && today <= vToFy);
			aMonth.forEach(function (month, i) {
				var fYear = (i < 3 ? sFyear + 1 : sFyear),
					monthEnable = !isWithinFY ? true : ((!m ? 12 : m) - i === 1 ? true : (new Date(y, m) > new Date(fYear, i)));
				obj[month + "Enable"] = monthEnable;
				obj[month + "Select"] = false;
			});
			this.getView().setModel(new JSONModel(obj), "LinkAmtGstr2bCheck");
		},

		onLinkAmtRowSelectionChange: function (oEvent) {
			var oModel = this.getView().getModel("Gstr2bLinkAmt"),
				oColModel = this.getView().getModel("LinkAmtGstr2bCheck"),
				oColData = oColModel.getProperty("/"),
				aMonth = this._getGenerate2bMonths(),
				oData = oModel.getProperty("/");

			if (oEvent.getParameter("selectAll") || oEvent.getParameter("rowIndex")) {
				var vSelectAll = oEvent.getParameter("selectAll") || false;
				aMonth.forEach(function (m) {
					oColData[m + "Select"] = (oColData[m + "Enable"] && vSelectAll);
					oData.forEach(function (e) {
						if (e.hasOwnProperty(m + "Check")) {
							e[m + "Check"] = vSelectAll;
						}
					});
				});
			}
			oColModel.refresh(true);
			oModel.refresh(true);
		},

		onGstr2bLinkAmtCol: function (oEvent, mm) {
			var flag = oEvent.getParameter('selected'),
				oModel = this.getView().getModel("Gstr2bLinkAmt"),
				vMonth = this._getGenerate2bMonths(mm),
				oData = oModel.getProperty("/");

			oData.forEach(function (e) {
				if (e.hasOwnProperty(vMonth + "Check")) {
					e[vMonth + "Check"] = flag;
				}
			}.bind(this));
			oModel.refresh(true);
		},

		_gstr2bLinkAmtState: function (type, value) {
			if (!value) {
				return "None";
			}
			return type === "S" ? "Success" : "Error";
		},

		onInitiate2bLinkAmt: function () {
			var oData = this.getView().getModel("Gstr2bLinkAmt").getProperty("/"),
				fYear = this.byId("sLinkAmt2bFY").getSelectedKey(),
				aMonth = this._getGenerate2bMonths(),
				frYear = fYear.split("-")[0],
				payload = {
					"req": [],
					"fy": fYear
				};

			oData.forEach(function (e) {
				var obj = {
					"gstin": e.gstin,
					"taxPeriod": []
				};
				aMonth.forEach(function (m, i) {
					if (e[m + "Check"]) {
						obj.taxPeriod.push((i + 1).toString().padStart(2, '0') + frYear);
					}
				});
				payload.req.push(obj);
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGstr2bLinking.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp.status);
						this._get2bLinkAmt(fYear);
					} else {
						sap.ui.core.BusyIndicator.hide();
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDownload2bLinkAmt: function (oEvt, type) {
			var aIndex = this.byId("tab2bLinkAmt").getSelectedIndices(),
				oData = this.getView().getModel("Gstr2bLinkAmt").getProperty("/");
			if (!aIndex.length) {
				MessageBox.information("Please select at least one GSTIN");
				return;
			}

			// if (this.Gstr2BCheck.length === 0) {
			// 	MessageBox.information("Please Select at least one Month");
			// 	return;
			// }

			var payload = {
				"req": {
					"gstins": this.saveData,
					"month": this.Gstr2BCheck,
					"reportType": oEvt.getParameter("item").getKey(),
					"fy": this.byId("sLinkAmt2bFY").getSelectedKey()
				}
			};

			// var that = this;
			// sap.ui.core.BusyIndicator.show(0);
			// $.ajax({
			// 		method: "POST",
			// 		url: "/aspsapapi/getGstr2BReports.do",
			// 		contentType: "application/json",
			// 		data: JSON.stringify(FinalData)
			// 	})
			// 	.done(function (data, status, jqXHR) {
			// 		sap.ui.core.BusyIndicator.hide();
			// 		that.getView().getModel("2BProperty").getData().April = false;
			// 		that.getView().getModel("2BProperty").getData().May = false;
			// 		that.getView().getModel("2BProperty").getData().June = false;
			// 		that.getView().getModel("2BProperty").getData().July = false;
			// 		that.getView().getModel("2BProperty").getData().Aug = false;
			// 		that.getView().getModel("2BProperty").getData().Sep = false;
			// 		that.getView().getModel("2BProperty").getData().Oct = false;
			// 		that.getView().getModel("2BProperty").getData().Nov = false;
			// 		that.getView().getModel("2BProperty").getData().Dec = false;
			// 		that.getView().getModel("2BProperty").getData().Jan = false;
			// 		that.getView().getModel("2BProperty").getData().Feb = false;
			// 		that.getView().getModel("2BProperty").getData().Mar = false;
			// 		that.getView().getModel("2BProperty").refresh(true);
			// 		that.Gstr2BCheck = [];
			// 		if (data.hdr.status === "S") {
			// 			that._getGstr2b();
			// 			MessageBox.success("Success, please check Request status", {
			// 				styleClass: "sapUiSizeCompact"
			// 			});
			// 		} else {
			// 			MessageBox.error(data.hdr.message);
			// 		}
			// 	})
			// 	.fail(function (jqXHR, status, err) {
			// 		sap.ui.core.BusyIndicator.hide();
			// 	});
		},

		_get2bLinkAmtStatus: function (value) {
			if (value && value.includes("Success")) {
				return "Success";
			} else if (value && value.includes("Failed")) {
				return "Error";
			} else if (value && value.includes("In-Progress")) {
				return "Information";
			} else {
				return "None";
			}
		},

		//////////////////////////End///////////////////////////////////////
		////////////////////Get GSTR-2B Vinay Kodam/////////////////////////

		////////////////////////////Start///////////////////////////////////
		////////////////////Get GSTR-2B Summary Vinay Kodam/////////////////

		onSelectFileType1: function (idx) {
			var oUploader = this.byId("fuGetGstr2b");
			switch (idx) {
			case 0:
				oUploader.setFileType("xlsx,xlsm,xls,csv");
				break;
			case 1:
				oUploader.setFileType("txt,json");
				break;
			}
			oUploader.setValue(null);
		},

		onUploadGet2b: function () {
			var oBundle = this.getResourceBundle(),
				aGstin = this.byId("GSTINEntityID2bS").getSelectedKeys(),
				oUploader = this.byId("fuGetGstr2b"),
				vIdx = this.byId("rgbFileType1").getSelectedIndex();

			if (oUploader.getValue() === "") {
				MessageBox.information("Please select file to Upload");
				return;
			}
			switch (vIdx) {
			case 0:
				oUploader.setUploadUrl("/aspsapapi/gstr2bB2bFileUploadDocuments.do");
				break;
			case 1:
				aGstin = this.removeAll(aGstin);
				if (aGstin.length !== 1) {
					MessageBox.information("Please select one GSTIN to upload file");
					return;
				}
				oUploader.setUploadUrl("/aspsapapi/jsonInput.do");
				oUploader.setAdditionalData(aGstin[0]);
				break;
			}
			oUploader.upload();
		},

		//////2B Summary File Upload////////
		onGet2bUploadComplete: function (oEvent) {
			sap.ui.core.BusyIndicator.hide();
			var sResponse = JSON.parse(oEvent.getParameter("responseRaw"));
			if (sResponse.hdr.status === "S") {
				this.getView().byId("fuGetGstr2b").setValue();
				MessageBox.success(sResponse.resp.message, {
					styleClass: "sapUiSizeCompact"
				});
			} else {
				if (sResponse.hdr.message) {
					MessageBox.error(sResponse.hdr.message, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					MessageBox.error(sResponse.resp.message, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}
		},

		_getGstr2bSummary: function () {
			var gstin = [];
			var aGstin = this.byId("GSTINEntityID2bS").getSelectedKeys();
			var allGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
			if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				for (var i = 0; i < allGstin.length; i++) {
					gstin.push(allGstin[i].value);
				}
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			var Gstr2B = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": aGstin.length === 0 ? gstin : aGstin,
					"toTaxPeriod": this.byId("gstr2bDateTo").getValue(),
					"fromTaxPeriod": this.byId("gstr2bDateFrom").getValue()
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2BSummary.do",
					contentType: "application/json",
					data: JSON.stringify(Gstr2B)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._calculateTotalTax(data.resp);
					this.getView().setModel(new JSONModel(data.resp), "GSTR2BSummTable");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onSelect2bSummaryEntity: function (oEvent) {
			var oModel = this.getView().getModel("GSTR2BSummTable"),
				oData = oModel.getProperty("/");

			this._calculateTotalTax(oData);
			oModel.refresh(true);
		},

		_calculateTotalTax: function (data) {
			var oProp = this.getView().getModel("Gstr2bSummProperty").getProperty("/");
			data.forEach(function (e) {
				e.totalTaxIgst = e.totalTaxCgst = e.totalTaxSgst = e.totalTaxCess = 0;
				e.totalTaxIgst += (oProp.entityAvailableItc ? e.availItcIgst : 0) + (oProp.entityNonAvailableItc ? e.nonAvailItcIgst : 0) +
					(oProp.entityRejectedItc ? e.rejectedItcIgst : 0);
				e.totalTaxCgst += (oProp.entityAvailableItc ? e.availItcCgst : 0) + (oProp.entityNonAvailableItc ? e.nonAvailItcCgst : 0) +
					(oProp.entityRejectedItc ? e.rejectedItcCgst : 0);
				e.totalTaxSgst += (oProp.entityAvailableItc ? e.availItcSgst : 0) + (oProp.entityNonAvailableItc ? e.nonAvailItcSgst : 0) +
					(oProp.entityRejectedItc ? e.rejectedItcSgst : 0);
				e.totalTaxCess += (oProp.entityAvailableItc ? e.availItcCess : 0) + (oProp.entityNonAvailableItc ? e.nonAvailItcCess : 0) +
					(oProp.entityRejectedItc ? e.rejectedItcCess : 0);
			}.bind(this));
		},

		onPressGstr2bGstin: function (oEvt) {
			var vDate = new Date(),
				fromDate = this.byId("gstr2bDateFrom").getDateValue(),
				aGstin = oEvt.getSource().getText();

			vDate.setDate(vDate.getDate() - 9);
			fromDate = new Date(fromDate.setDate("01"));

			this.byId("dpGstr2bsum").setVisible(false);
			this.byId("dpGstr3bSummary").setVisible(true);
			this.byId("gstr2BDetId").setSelectedKey(aGstin);

			this.byId("FromDateDet").setMaxDate(new Date());
			this.byId("FromDateDet").setValue(this.byId("gstr2bDateFrom").getValue());

			this.byId("ToDateDet").setMinDate(fromDate);
			this.byId("ToDateDet").setMaxDate(new Date());
			this.byId("ToDateDet").setValue(this.byId("gstr2bDateTo").getValue());

			var Gstr2B = {
				"req": {
					"gstin": this.byId("gstr2BDetId").getSelectedKey(),
					"toTaxPeriod": this.byId("ToDateDet").getValue(),
					"fromTaxPeriod": this.byId("FromDateDet").getValue()
				}
			};
			this._getGstr2bSummaryDetails(Gstr2B);
		},

		onPressGstr2bGo: function () {
			var Gstr2B = {
				"req": {
					"gstin": this.byId("gstr2BDetId").getSelectedKey(),
					"toTaxPeriod": this.byId("ToDateDet").getValue(),
					"fromTaxPeriod": this.byId("FromDateDet").getValue()
				}
			};
			this._getGstr2bSummaryDetails(Gstr2B);
		},

		_getGstr2bSummaryDetails: function (Gstr2B) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstr2BDetails.do",
					contentType: "application/json",
					data: JSON.stringify(Gstr2B)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._cal2bSummaryTotalBanner(data.resp.total);
					this.getView().setModel(new JSONModel(data.resp.total), "GSTR2BDetHeader");
					this._bindGet2bSummary(data.resp);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		_cal2bSummaryTotalBanner: function (data) {
			// var oProp = this.getView().getModel("Gstr2bSummProperty").getProperty("/");
			data.TotalTax = (data.totalTaxIgst + data.totalTaxCgst + data.totalTaxSgst + data.totalTaxCess).toFixed(2);
			data.AvailItcTotalTax = (data.availItcIgst + data.availItcCgst + data.availItcSgst + data.availItcCess).toFixed(2);
			data.NonAvailItcTotalTax = (data.nonAvailItcIgst + data.nonAvailItcCgst + data.nonAvailItcSgst + data.nonAvailItcCess).toFixed(2);
			data.rejectedItcTotalTax = (data.rejectedItcIgst + data.rejectedItcCgst + data.rejectedItcSgst + data.rejectedItcCess).toFixed(2);
		},

		_bindGet2bSummary: function (data) {
			var aTabType = ["itcSuppliesFromRegPerson", "inwardSuppliesFromIsd", "inwardSuppliesRevChrg", "importOfGoods", "others"],
				aField = [
					"taxablevalue", "totalTaxIgst", "totalTaxCgst", "totalTaxSgst", "totalTaxCess",
					"availItcIgst", "availItcCgst", "availItcSgst", "availItcCess",
					"nonAvailItcIgst", "nonAvailItcCgst", "nonAvailItcSgst", "nonAvailItcCess",
					"rejectedItcCess", "rejectedItcCgst", "rejectedItcIgst", "rejectedItcSgst"
				],
				oSummary = [];

			aTabType.forEach(function (tab) {
				var oSummaryJson = {
					"items": []
				};
				data[tab].forEach(function (e) {
					var obj = $.extend({}, e);
					switch (tab) {
					case "itcSuppliesFromRegPerson":
						oSummaryJson.tableName = "Part A - ITC Available.\nITC - Supplies from registered persons (other than RCM)";
						break;
					case "inwardSuppliesFromIsd":
						oSummaryJson.tableName = "Inward Supplies from ISD";
						break;
					case "inwardSuppliesRevChrg":
						oSummaryJson.tableName = "Inward Supplies liable for reverse charge";
						break;
					case "importOfGoods":
						oSummaryJson.tableName = "Import of Goods";
						break;
					case "others":
						oSummaryJson.tableName = "Part B - ITC Reversal.\nOthers";
						break;
					}
					aField.forEach(function (f) {
						oSummaryJson[f] = (oSummaryJson[f] || 0) + e[f];
					});
					obj.tableName = e.tableName;
					oSummaryJson.items.push(obj);
				}.bind(this));
				oSummary.push(oSummaryJson);
			}.bind(this));
			this._calGstr2bSummaryGstinTotal(oSummary);
			this.byId("tabGstr2bDet").setModel(new JSONModel(oSummary), "GSTR2BDetTable");
		},

		_calGstr2bSummaryGstinTotal: function (data) {
			var oProp = this.getView().getModel("Gstr2bSummProperty").getProperty("/");
			data.forEach(function (l1) {
				this._cal2bSummGstinTotalTax(l1, oProp);
				l1.items.forEach(function (l2) {
					this._cal2bSummGstinTotalTax(l2, oProp);
				}.bind(this));
			}.bind(this));
		},

		_cal2bSummGstinTotalTax: function (data, oProp) {
			data.totalTaxIgst = data.totalTaxCgst = data.totalTaxSgst = data.totalTaxCess = 0;
			data.totalTaxIgst += (oProp.gstnAvailableItc ? data.availItcIgst : 0) +
				(oProp.gstnNonAvailableItc ? data.nonAvailItcIgst : 0) + (oProp.gstnRejectedItc ? data.rejectedItcIgst : 0);
			data.totalTaxCgst += (oProp.gstnAvailableItc ? data.availItcCgst : 0) +
				(oProp.gstnNonAvailableItc ? data.nonAvailItcCgst : 0) + (oProp.gstnRejectedItc ? data.rejectedItcCgst : 0);
			data.totalTaxSgst += (oProp.gstnAvailableItc ? data.availItcSgst : 0) +
				(oProp.gstnNonAvailableItc ? data.nonAvailItcSgst : 0) + (oProp.gstnRejectedItc ? data.rejectedItcSgst : 0);
			data.totalTaxCess += (oProp.gstnAvailableItc ? data.availItcCess : 0) +
				(oProp.gstnNonAvailableItc ? data.nonAvailItcCess : 0) + (oProp.gstnRejectedItc ? data.rejectedItcCess : 0);
		},

		onSelect2bSummaryGstin: function () {
			var oModel = this.byId("tabGstr2bDet").getModel("GSTR2BDetTable"),
				oData = oModel.getProperty("/");

			this._calGstr2bSummaryGstinTotal(oData);
			oModel.refresh(true);
		},

		_bindGet2bTotal: function (obj, data) {
			obj.tableName = data.tableName;
			obj.panCount = data.panCount;
			obj.gstinCount = data.gstinCount;
			obj.count = data.count;
			obj.taxablevalue = data.taxablevalue;
			obj.totalTaxIgst = data.totalTaxIgst;
			obj.totalTaxCgst = data.totalTaxCgst;
			obj.totalTaxSgst = data.totalTaxSgst;
			obj.totalTaxCess = data.totalTaxCess;
			obj.availItcIgst = data.availItcIgst;
			obj.availItcCgst = data.availItcCgst;
			obj.availItcSgst = data.availItcSgst;
			obj.availItcCess = data.availItcCess;
			obj.nonAvailItcIgst = data.nonAvailItcIgst;
			obj.nonAvailItcCgst = data.nonAvailItcCgst;
			obj.nonAvailItcSgst = data.nonAvailItcSgst;
			obj.nonAvailItcCess = data.nonAvailItcCess;
		},

		onPressBackGstr2b: function () {
			this.byId("dpGstr2bsum").setVisible(true);
			this.byId("dpGstr3bSummary").setVisible(false);
		},

		onMenuItemPressGSTR2BSumm: function (oEvt) {
			var key = oEvt.getParameters().item.getKey();
			var selItems = this.byId("gstrTabId").getSelectedIndices();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			var data = this.getView().getModel("GSTR2BSummTable").getData();
			var gstin = [];
			for (var i = 0; i < selItems.length; i++) {
				gstin.push(data[selItems[i]].gstin);
			}
			var Gstr2B = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": gstin,
					"toTaxPeriod": this.byId("gstr2bDateTo").getValue(),
					"fromTaxPeriod": this.byId("gstr2bDateFrom").getValue()
				}
			};
			var oReqExcelPath;
			if (key === "Entity level table summary") {
				oReqExcelPath = "/aspsapapi/gstr2BSuammaryReport.do";
			} else {
				oReqExcelPath = "/aspsapapi/gstr2DetailsReport.do";
			}
			this.excelDownload(Gstr2B, oReqExcelPath);
		},

		onMenuItemPressGSTR2BDet: function () {
			var Gstr2B = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": [this.byId("gstr2BDetId").getSelectedKey()],
					"toTaxPeriod": this.byId("ToDateDet").getValue(),
					"fromTaxPeriod": this.byId("FromDateDet").getValue()
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr2DetailsReport.do";
			this.excelDownload(Gstr2B, oReqExcelPath);
		},

		// tabData: function () {
		// 	var data = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
		// 	var gstin = [];
		// 	for (var i = 0; i < data.length; i++) {
		// 		gstin.push(data[i].value);
		// 	}
		// 	var Gstr2B = {
		// 		"req": {
		// 			"entityId": $.sap.entityID,
		// 			"gstin": gstin,
		// 			"toTaxPeriod": this.byId("gstr2bDateTo").getValue(),
		// 			"fromTaxPeriod": this.byId("gstr2bDateFrom").getValue()
		// 		}
		// 	};
		// 	var oReqExcelPath = "/aspsapapi/gstr2BSuammaryReport.do";
		// 	this.excelDownload(Gstr2B, oReqExcelPath);
		// },

		tableDataGstr2bSummary: function () {
			var oProp = this.getView().getModel('Gstr2bSummProperty').getProperty('/'),
				oData = this.getView().getModel('GSTR2BSummTable').getProperty('/'),
				headers = this._getExcelHeader(oProp),
				mappedData = oData.map(function (item) {
					var arr = [
						item.gstin,
						item.status + ' ' + item.getGstr2bStatus,
						item.vendorPanCount + '/' + item.vendorGstinCount,
						item.count,
						item.totalTaxIgst,
						item.totalTaxCgst,
						item.totalTaxSgst,
						item.totalTaxCess
					];
					if (oProp.entityAvailableItc) {
						Array.prototype.push.apply(arr, [item.availItcIgst, item.availItcCgst, item.availItcSgst, item.availItcCess]);
					}
					if (oProp.entityNonAvailableItc) {
						Array.prototype.push.apply(arr, [item.nonAvailItcIgst, item.nonAvailItcCgst, item.nonAvailItcSgst, item.nonAvailItcCess]);
					}
					if (oProp.entityRejectedItc) {
						Array.prototype.push.apply(arr, [item.rejectedItcIgst, item.rejectedItcCgst, item.rejectedItcSgst, item.rejectedItcCess]);
					}
					return arr;
				});
			// Trigger the download (call this function when you want to download the file)
			this.downloadExcelFile(headers, mappedData, oProp);
		},

		// Define the headers
		_getExcelHeader: function (property) {
			var frDate = this._formatPeriod(this.byId("gstr2bDateFrom").getDateValue()),
				toDate = this._formatPeriod(this.byId("gstr2bDateTo").getDateValue()),
				oHeader = [
					['Tax Period From', frDate, '', 'Tax Period To', toDate],
					['', '', '', '', 'Total Tax', '', '', ''],
					['Recipient GSTIN', 'Get GSTR-2B Status', 'Vendor PAN/GSTIN Count', 'Count', 'IGST', 'CGST', 'SGST', 'Cess']
				];

			['entityAvailableItc', 'entityNonAvailableItc', 'entityRejectedItc'].forEach(function (key) {
				if (property[key]) {
					var f = key.replace('entity', '').replace('Itc', ' ITC').replace('Non', 'Non ');
					oHeader[1].push(f, '', '', '');
					oHeader[2].push('IGST', 'CGST', 'SGST', 'Cess');
				}
			});
			return oHeader;
		},

		downloadExcelFile: function (headers, data, oProperty) {
			var workbook = new ExcelJS.Workbook(), // Create a new workbook
				sheet = workbook.addWorksheet('Entity Level Summary'); // Add a sheet

			this._excelHeaderStyle(headers, sheet, oProperty);
			sheet.columns = this._getColumnWidth();

			// Add mapped data to the sheet starting from the sixth row
			data.forEach(function (rowData, index) {
				var row = sheet.getRow(index + 6); // Starting from row 6
				row.values = rowData;
				row.getCell(2).alignment = {
					horizontal: 'center'
				};
			});

			// Write to a buffer
			workbook.xlsx.writeBuffer().then(function (buffer) {
					// Trigger the download
					var blob = new Blob([buffer], {
						type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
					});
					var url = URL.createObjectURL(blob);
					var anchor = document.createElement('a');
					anchor.href = url;
					anchor.download = '_GSTR 2B Entity Level Summary Report_' + this._getExcelTimeStamp() + '.xlsx';
					anchor.click();

					// Clean up
					URL.revokeObjectURL(url);
				}.bind(this))
				.catch(function (error) {
					console.error('Error writing Excel file:', error);
				});
		},

		// Apply headers to the sheet
		_excelHeaderStyle: function (headers, sheet, prop) {
			headers.forEach(function (row, rowIndex) {
				var headerRow = sheet.getRow(rowIndex + (rowIndex ? 3 : 2));
				headerRow.values = row;
				headerRow.font = {
					name: 'Calibri',
					size: 11
				};
				if (rowIndex) {
					this._styleHeaderRow(headerRow);
				}
			}.bind(this));
			// Merge cells for the main headers
			sheet.mergeCells('E4', 'H4');
			if (prop.entityAvailableItc || prop.entityNonAvailableItc || prop.entityRejectedItc) {
				sheet.mergeCells('I4', 'L4');
			}
			if ((prop.entityAvailableItc && prop.entityNonAvailableItc) || (prop.entityNonAvailableItc && prop.entityRejectedItc) ||
				prop.entityAvailableItc && prop.entityRejectedItc) {
				sheet.mergeCells('M4', 'P4');
			}
			if (prop.entityAvailableItc && prop.entityNonAvailableItc && prop.entityRejectedItc) {
				sheet.mergeCells('Q4', 'T4');
			}
		},

		_styleHeaderRow: function (headerRow) {
			headerRow.eachCell(function (cell, colNumber) {
				cell.alignment = {
					vertical: 'middle',
					horizontal: 'center'
				};
				if (cell.row !== 4 || colNumber >= 5) {
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
					cell.font = {
						name: 'Calibri',
						size: 11,
						bold: (cell.row === 4)
					};
					cell.fill = {
						type: 'pattern',
						pattern: 'solid',
						fgColor: {
							argb: (cell.row === 4 ? 'FFFCE4D6' : 'FFD9E1F2')
						}
					};
				}
			});
		},

		_getColumnWidth: function () {
			return [{
				key: 'A',
				width: 18
			}, {
				key: 'B',
				width: 42
			}, {
				key: 'C',
				width: 23
			}, {
				key: 'D',
				width: 15
			}];
		},

		on2BDetFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebut2B1").setVisible(true);
				this.byId("openbut2B1").setVisible(false);
				this.byId("on2BTab").setFullScreen(true);
				this.byId("tabGstr2bDet").setVisibleRowCount(22);
			} else {
				this.byId("closebut2B1").setVisible(false);
				this.byId("openbut2B1").setVisible(true);
				this.byId("on2BTab").setFullScreen(false);
				this.byId("tabGstr2bDet").setVisibleRowCount(7);
			}
		},

		onExpand2B: function (oEvent) {
			if (oEvent.getSource().getId().includes("expand2B")) {
				this.byId("tabGstr2bDet").expandToLevel(1);
			} else {
				this.byId("tabGstr2bDet").collapseAll();
			}
		},

		onPressClear2B: function () {
			var vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			vPeriod.setDate(1);
			this.byId("gstr2bDateFrom").setMaxDate(vDate);
			this.byId("gstr2bDateFrom").setDateValue(vPeriod);
			this.byId("gstr2bDateTo").setMinDate(vPeriod);
			this.byId("gstr2bDateTo").setMaxDate(vDate);
			this.byId("gstr2bDateTo").setDateValue(vDate);

			this.byId("GSTINEntityID2bS").setSelectedKeys([]);
			this._getGstr2bSummary();
		},

		onClearFilter2B: function () {
			var vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			vPeriod.setDate(1);
			this.byId("FromDateDet").setMaxDate(vDate);
			this.byId("FromDateDet").setDateValue(vPeriod);
			this.byId("ToDateDet").setMinDate(vPeriod);
			this.byId("ToDateDet").setMaxDate(vDate);
			this.byId("ToDateDet").setDateValue(vDate);
			this.onPressGstr2bGo();
		},

		handleChange2B: function (oevt) {
			var toDate = this.byId("gstr2bDateTo").getDateValue(),
				fromDate = this.byId("gstr2bDateFrom").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("gstr2bDateTo").setDateValue(oevt.getSource().getDateValue());
				this.byId("gstr2bDateTo").setMinDate(oevt.getSource().getDateValue());
				this.byId("ToDateDet").setDateValue(oevt.getSource().getDateValue());
				this.byId("ToDateDet").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("gstr2bDateTo").setMinDate(oevt.getSource().getDateValue());
				this.byId("ToDateDet").setMinDate(oevt.getSource().getDateValue());
			}
		},

		handleChange2BD: function (oevt) {
			var toDate = this.byId("ToDateDet").getDateValue(),
				fromDate = this.byId("FromDateDet").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("ToDateDet").setDateValue(oevt.getSource().getDateValue());
				this.byId("ToDateDet").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("ToDateDet").setMinDate(oevt.getSource().getDateValue());
			}
		},

		//////////////////////////End///////////////////////////////////////
		////////////////////Get GSTR-2B Summary Vinay Kodam/////////////////////////

		/*=====================================================================================*/
		/*======== Initiate Recon =============================================================*/
		/*=====================================================================================*/
		/**
		 * To Fetch GSTIN Data Request
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param GSTIN Data Request
		 */
		onPressGoForGSTIN2A: function (oEvent) {
			/*	var vDate = new Date(),
					date1 = new Date();
				date1.setMonth(date1.getMonth() - 1);
				this.byId("idInitiateReconPeriodTax12A").setMinDate(date1);
				this.byId("idInitiateReconPeriodTax12A").setMaxDate(vDate);
				this.byId("idInitiateReconPeriodTax12A").setDateValue(vDate);*/
			this.byId("searchId").setValue("");
			var oView = this.getView();
			var that = this;
			var postData;
			var selKey = this.byId("idDateRange2A").getSelectedKey();
			if (selKey === "Tax Perioid") {
				postData = {
					"req": {
						"entityId": Number($.sap.entityID),
						"toTaxPeriod": this.byId("idInitiateReconPeriodTax12A").getValue(),
						"fromTaxPeriod": this.byId("idInitiateReconPeriodTax2A").getValue(),
						"toTaxPeriod2A": this.byId("idInitiateReconPeriodTax12A1").getValue(), // Added by chaithra on 2/11/2020
						"fromTaxPeriod2A": this.byId("idInitiateReconPeriodTax2A1").getValue(), //Added by chaithra on 2/11/2020
						"fromDocDate": "",
						"toDocDate": "",
						"reconType": this.byId("idReconType").getSelectedKey()
					}
				};
			} else {
				postData = {
					"req": {
						"entityId": Number($.sap.entityID),
						"toTaxPeriod": "",
						"fromTaxPeriod": "",
						"fromDocDate": this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()),
						"toDocDate": this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()),
						"reconType": this.byId("idReconType").getSelectedKey()
					}
				};
			}
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getDataForGatr2ReconSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						//oView.byId("idInitiateReconList2A").setVisibleRowCount(data.resp.det.length);
						var oGSTIN = new JSONModel(data.resp.det);
						oGSTIN.setSizeLimit(data.resp.det.length);
						oView.setModel(oGSTIN, "GSTIN2A");
						oView.byId("checkboxID").setSelected(true);
						oView.byId("idInitiateReconList2A").selectAll();
						that.onInitiateRecon();
					} else {
						var oGSTIN1 = new JSONModel([]);
						oView.setModel(oGSTIN1, "GSTIN2A");
						oView.setModel(oGSTIN1, "InitiateRecon2A");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "GSTIN2A");
					oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
			});
		},

		/**
		 * change function Date Range Drop Down
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param trigerd when change date range value
		 */
		onDateRangeChange: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			var that = this;
			if (key === "Document Date") {
				that.getView().getModel("Display").getData().DOC = true;
				that.getView().getModel("Display").getData().TAX = false;
				// this.byId("vBox1ID2A").setVisible(true);
				// this.byId("vBox2ID2A").setVisible(false);
			} else {
				that.getView().getModel("Display").getData().DOC = false;
				that.getView().getModel("Display").getData().TAX = true;
				// this.byId("vBox1ID2A").setVisible(false);
				// this.byId("vBox2ID2A").setVisible(true);
			}
			that.getView().getModel("Display").refresh(true);
		},
		//================= change funtion Recon type Drop down added by chaithra on 2/11/2020 ============//
		onRecontypeChange: function (oEvt) {
			this.ReportArr = [];
			this.byId("allId").setSelected(false);
			this.byId("ForceMatchID").setSelected(false);
			this.byId("SRCPid").setSelected(false);
			this.byId("SRTPid").setSelected(false);
			this.byId("SGSRid").setSelected(false);
			this.byId("SPSRid").setSelected(false);
			this.byId("RGTPWRid").setSelected(false);
			this.byId("RGWRid").setSelected(false);
			this.byId("VGTPWR").setSelected(false);
			this.byId("VGWRid").setSelected(false);
			this.byId("VPTPWRid").setSelected(false);
			this.byId("VPWRid").setSelected(false);
			this.byId("VGWDRid").setSelected(false);
			this.byId("VPWDRid").setSelected(false);
			this.byId("CDNI2Aid").setSelected(false);
			this.byId("CDNIPRid").setSelected(false);
			this.byId("TSis").setSelected(false);
			this.byId("PRid").setSelected(false);
			this.byId("LCid").setSelected(false);
			this.byId("RCid").setSelected(false);
			this.byId("DprId").setSelected(false);
			this.byId("Dpr2aId").setSelected(false);
			this.byId("Impg").setSelected(false);
			this.byId("Nt").setSelected(false);
			var key = oEvt.getSource().getSelectedKey();
			var that = this;
			if (key === "2APR") {
				that.getView().getModel("Display").getData().Label = "2A/6A";
				that.byId("Nt").setVisible(true);
				that.byId("Dpr2aId").setVisible(true);
				//======= Added by chaithra on 28/12/2020 for GSTR 2B/PR =============//
				that.byId("ForceMatchID").setEnabled(true);
				that.byId("LCid").setEnabled(true);
				that.byId("Impg").setEnabled(true);
				that.byId("PRid").setEnabled(true);
				//========= Code ended by chaithra on 28/12/2020 =====================//
				// this.byId("vBox1ID2A").setVisible(true);
				// this.byId("vBox2ID2A").setVisible(false);
			} else {
				that.getView().getModel("Display").getData().Label = "2B";
				that.byId("Nt").setVisible(false);
				that.byId("Dpr2aId").setVisible(false);
				//======= Added by chaithra on 28/12/2020 for GSTR 2B/PR =============//
				that.byId("ForceMatchID").setEnabled(false);
				that.byId("LCid").setEnabled(false);
				that.byId("Impg").setEnabled(false);
				that.byId("PRid").setEnabled(false);
				//========= Code ended by chaithra on 28/12/2020 =====================//
				// this.byId("vBox1ID2A").setVisible(false);
				// this.byId("vBox2ID2A").setVisible(true);
			}
			that.getView().getModel("Display").refresh(true);
			this.onPressGoForGSTIN2A();
		},
		//======================= code ended by chaithra on 2/11/2020 =====================================//
		/**
		 * on Click of Initiate Matching
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param trigerd Click on Initiate Matching
		 */
		fnIntiniateBtnPress2A: function () {
			var that = this;
			var selItems = this.byId("idInitiateReconList2A").getSelectedContextPaths();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			//var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that.onInitiateMatch();
					}
				}
			});

		},

		/**
		 * on Click of Initiate Matching check box
		 * Developed by: vinay Kodam 29.04.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param trigerd Click on Initiate Matching GSTN check box
		 */
		onSelectallGSTIN: function (oEvent) {
			//eslint-disable-line
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList2A").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList2A").selectAll();
			}
			this.onInitiateRecon();
		},

		checkSelctAll: function (oEvt) {

			if (oEvt.getSource().getSelected()) {
				this.ReportArr = [];
				if (this.getView().byId("id_DocNoMismatch").getSelected()) {
					var selKey = "Doc No Mismatch II";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_Potential").getSelected()) {
					var selKey = "Potential-II";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_LogicalMatch").getSelected()) {
					var selKey = "Logical Match";
					this.ReportArr.push(selKey);
				}
				/*this.byId("FuzztMId").setSelected(true);*/
				this.byId("ForceMatchID").setSelected(true);
				this.byId("SRCPid").setSelected(true);
				this.byId("SRTPid").setSelected(true);
				this.byId("SGSRid").setSelected(true);
				this.byId("SPSRid").setSelected(true);
				this.byId("RGTPWRid").setSelected(true);
				this.byId("RGWRid").setSelected(true);
				this.byId("VGTPWR").setSelected(true);
				this.byId("VGWRid").setSelected(true);
				this.byId("VPTPWRid").setSelected(true);
				this.byId("VPWRid").setSelected(true);
				this.byId("VGWDRid").setSelected(true);
				this.byId("VPWDRid").setSelected(true);
				this.byId("CDNI2Aid").setSelected(true);
				this.byId("CDNIPRid").setSelected(true);
				this.byId("TSis").setSelected(true);
				this.byId("PRid").setSelected(true);
				this.byId("LCid").setSelected(true);
				this.byId("RCid").setSelected(true);
				this.byId("DprId").setSelected(true);
				this.byId("Dpr2aId").setSelected(true);
				this.byId("Impg").setSelected(true); // Added by chaithra on 12/11/2020 
				if (this.byId("idReconType").getSelectedKey() === "2APR") {
					this.byId("Nt").setSelected(true);
					this.ReportArr.push("ITC Tracking Report");
				}
				/*"Fuzzy_Match_Records",*/
				if (this.byId("idReconType").getSelectedKey() == "2APR") {
					this.ReportArr.push("Force_Match_Records", "Summary_CalendarPeriod_Records", "Summary_TaxPeriod_Record",
						"Supplier_GSTIN_Summary_Records", "Supplier_PAN_Summary_Records", "Recipient_GSTIN_Period_Wise_Record",
						"Recipient_GSTIN_Wise_Records", "Vendor_GSTIN_Period_Wise_Records", "Vendor_GSTIN_Wise_Records",
						"Vendor_PAN_Period_Wise_Records", "Vendor_PAN_Wise_Records", "CRD-INV_Ref_Reg_GSTR_2A_Records",
						"CRD-INV_Ref_Reg_PR_Records", "Vendor_Records_GSTIN", "Vendor_Records_PAN", "GSTR_2A_Time_Stamp_Report",
						"Consolidated_PR_Register", "Locked_CFS_N_Amended_Records", "Reverse_Charge_Register", "Consolidated IMPG Report",
						"Dropped_PR_Records_Report", "Dropped 2A_6A Records Report");
				} else {
					this.byId("ForceMatchID").setSelected(false);
					this.byId("LCid").setSelected(false);
					this.byId("Impg").setSelected(false);
					this.byId("PRid").setSelected(false);
					this.ReportArr.push("Summary_CalendarPeriod_Records", "Summary_TaxPeriod_Record",
						"Supplier_GSTIN_Summary_Records", "Supplier_PAN_Summary_Records", "Recipient_GSTIN_Period_Wise_Record",
						"Recipient_GSTIN_Wise_Records", "Vendor_GSTIN_Period_Wise_Records", "Vendor_GSTIN_Wise_Records",
						"Vendor_PAN_Period_Wise_Records", "Vendor_PAN_Wise_Records", "CRD-INV_Ref_Reg_GSTR_2B_Records",
						"CRD-INV_Ref_Reg_PR_Records", "Vendor_Records_GSTIN", "Vendor_Records_PAN", "GSTR_2B_Time_Stamp_Report",
						"Reverse_Charge_Register", "Dropped_PR_Records_Report");
				}
			} else {
				this.byId("ForceMatchID").setSelected(false);
				this.byId("SRCPid").setSelected(false);
				this.byId("SRTPid").setSelected(false);
				this.byId("SGSRid").setSelected(false);
				this.byId("SPSRid").setSelected(false);
				this.byId("RGTPWRid").setSelected(false);
				this.byId("RGWRid").setSelected(false);
				this.byId("VGTPWR").setSelected(false);
				this.byId("VGWRid").setSelected(false);
				this.byId("VPTPWRid").setSelected(false);
				this.byId("VPWRid").setSelected(false);
				this.byId("VGWDRid").setSelected(false);
				this.byId("VPWDRid").setSelected(false);
				this.byId("CDNI2Aid").setSelected(false);
				this.byId("CDNIPRid").setSelected(false);
				this.byId("TSis").setSelected(false);
				this.byId("PRid").setSelected(false);
				this.byId("LCid").setSelected(false);
				this.byId("RCid").setSelected(false);
				this.byId("DprId").setSelected(false);
				this.byId("Dpr2aId").setSelected(false);
				this.byId("Impg").setSelected(false); // Added by chaithra on 12/11/2020
				this.byId("Nt").setSelected(false);
				this.ReportArr = [];
				if (this.getView().byId("id_DocNoMismatch").getSelected()) {
					var selKey = "Doc No Mismatch II";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_Potential").getSelected()) {
					var selKey = "Potential-II";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_LogicalMatch").getSelected()) {
					var selKey = "Logical Match";
					this.ReportArr.push(selKey);
				}
			}

		},

		checkSelct: function (oEvt) { //eslint-disable-line

			var selText = oEvt.getSource().getText();
			var selKey;
			if (selText === "Fuzzy Match") {
				selKey = "Fuzzy_Match_Records";
			} else if (selText === "Force match - 3B lock") {
				selKey = "Force_Match_Records";
			} else if (selText === "Summary Report Calendar Period") {
				selKey = "Summary_CalendarPeriod_Records";
			} else if (selText === "Summary Report Tax Period") {
				selKey = "Summary_TaxPeriod_Record";
			} else if (selText === "Supplier GSTIN Summary Report") {
				selKey = "Supplier_GSTIN_Summary_Records";
			} else if (selText === "Supplier PAN Summary Report") {
				selKey = "Supplier_PAN_Summary_Records";
			} else if (selText === "Recipient GSTIN Tax Period Wise Report") {
				selKey = "Recipient_GSTIN_Period_Wise_Record";
			} else if (selText === "Recipient GSTIN Wise Report") {
				selKey = "Recipient_GSTIN_Wise_Records";
			} else if (selText === "Vendor GSTIN Tax Period Wise Report") {
				selKey = "Vendor_GSTIN_Period_Wise_Records";
			} else if (selText === "Vendor GSTIN Wise Report") {
				selKey = "Vendor_GSTIN_Wise_Records";
			} else if (selText === "Vendor PAN Tax Period Wise Report") {
				selKey = "Vendor_PAN_Period_Wise_Records";
			} else if (selText === "Vendor PAN Wise Report") {
				selKey = "Vendor_PAN_Wise_Records";
			} else if (selText === "CR/DR-Invoice Reference Register- GSTR 2A/6A") {
				selKey = "CRD-INV_Ref_Reg_GSTR_2A_Records";
			} else if (selText === "CR/DR-Invoice Reference Register- PR") {
				selKey = "CRD-INV_Ref_Reg_PR_Records";
			} else if (selText === "Vendor GSTIN Wise Detailed Report") {
				selKey = "Vendor_Records_GSTIN";
			} else if (selText === "Vendor PAN Wise Detailed Report") {
				selKey = "Vendor_Records_PAN";
			} else if (selText === "GSTR 2A/6A Time Stamp Report") {
				selKey = "GSTR_2A_Time_Stamp_Report";
			} else if (selText === "Consolidated PR Register") {
				selKey = "Consolidated_PR_Register";
			} else if (selText === "Locked CFS N Amended Records") {
				selKey = "Locked_CFS_N_Amended_Records";
			} else if (selText === "Reverse Charge Register") {
				selKey = "Reverse_Charge_Register";
			} else if (selText === "Dropped PR Records Report") {
				selKey = "Dropped_PR_Records_Report";
			} else if (selText === "Dropped 2A 6A Records Report") {
				selKey = "Dropped 2A_6A Records Report";
			} else if (selText === "Doc No Mismatch II") {
				selKey = "Doc No Mismatch II";
			} else if (selText === "Potential-II") {
				selKey = "Potential-II";
			} else if (selText === "Logical Match") {
				selKey = "Logical Match";
			} else if (selText === "IMPG") { // Added by chaithra on 12/11/2020
				selKey = "Consolidated IMPG Report";
			} else if (selText === "ITC Tracking Report") {
				selKey = "ITC Tracking Report";
			} else if (selText === "CR/DR-Invoice Reference Register- GSTR 2B") {
				selKey = "CRD-INV_Ref_Reg_GSTR_2B_Records";
			} else if (selText === "GSTR 2B Time Stamp Report") {
				selKey = "GSTR_2B_Time_Stamp_Report"
			}
			if (oEvt.getSource().getSelected()) {
				this.ReportArr.push(selKey);
			} else {
				for (var i = 0; i < this.ReportArr.length; i++) {
					if (this.ReportArr[i] === selKey) {
						this.ReportArr.splice(i, 1);
					}
				}
			}
		},

		/**
		 * Request for Initiate Matching
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private Request for Initiate Matching
		 * @param 
		 */
		onInitiateMatch: function () {
			var oView = this.getView();
			//var oPath = [];
			var aGSTIN = [];

			var oModelData = oView.getModel("GSTIN2A").getData();
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				/*var j = oPath[i].split('/')[1];*/
				aGSTIN.push(oModelData[j].gstin);
			}
			/*oPath = oView.byId("idInitiateReconList2A").getSelectedContextPaths();

			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}*/

			/*var selItems = oView.byId("idInitiateReconList2A").getSelectedIndices();
			var data = this.getView().getModel("GSTIN2A").getData();
			var gstin = [];
			for (var i = 0; i < selItems.length; i++) {
				gstin.push(data[selItems[i]].gstin);
			}*/

			var selKey = this.byId("idDateRange2A").getSelectedKey();
			var oIntiData = {
				"entityId": Number($.sap.entityID),
				"gstins": aGSTIN,
				"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
				"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
				"toTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A1").getValue() : "", // Added by chaithra on 2/11/2020
				"fromTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A1").getValue() : "", // Added by chaithra on 2/11/2020
				"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
				"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
				"addlReports": this.ReportArr === undefined ? [] : this.ReportArr,
				"reconType": this.byId("idReconType").getSelectedKey()
			};

			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			var oIniPath = "/aspsapapi/gstr2InitiateMatching.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
						title: "Initiate Matching Successfully done"
					});
					oIniModel.setData(data);
					oIniView.setModel(oIniModel, "IniData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * click on Request Id Link
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param send request for Request Id Link
		 */
		onPressRequestIDwise2A: function (oEvent) {

			this.getView().byId("idRequestIDwisePage2A").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			//this.uName = this.getOwnerComponent().getModel("UserInfo").oData.groupName;
			var oReqWiseInfo = {
				"entityId": $.sap.entityID.toString()
			};

			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			//var oReqWisePath = "/aspsapapi/getAnx2ReportRequestStatus.do";
			var oReqWisePath = "/aspsapapi/getgstr2ReportRequestStatus.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				}).done(function (data, status, jqXHR) {
					for (var i = 0; i < data.resp.requestDetails.length; i++) {
						if (data.resp.requestDetails[i].toDocDate !== undefined && data.resp.requestDetails[i].fromDocDate !== undefined) {
							data.resp.requestDetails[i].toDocDate = data.resp.requestDetails[i].toDocDate.split(" ")[0];
							data.resp.requestDetails[i].fromDocDate = data.resp.requestDetails[i].fromDocDate.split(" ")[0];
						}
					}

					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "ReqWiseData2A");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		/**
		 * click on Refresh button
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param click on Refresh button
		 */
		onRefreshRequestIDwise2A: function () {
			this.onPressRequestIDwise2A();
		},

		/**
		 * click on Back button on Request Id wise screen
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param click on Refresh button
		 */
		onPressRequestIDwiseBack2A: function () {
			this.getView().byId("idRequestIDwisePage2A").setVisible(false);
			this.getView().byId("idSplitDtl2A").setVisible(true);
		},

		/**
		 * trigerd when change selection of GSTIN,s
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onSelectionChange1: function (oEvent) {
			this.onInitiateRecon(oEvent);
		},

		/**
		 * trigerd when enter value in search
		 * Developed by: vinay Kodam 01.05.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		// onSearchGstins1: function (oEvent) {
		// 	var aFilter = [];
		// 	var sQuery = oEvent.getSource().getValue();
		// 	if (sQuery) {
		// 		aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
		// 	}
		// 	// filter binding
		// 	var oList = this.getView().byId("idInitiateReconList2A");
		// 	var oBinding = oList.getBinding("items");
		// 	oBinding.filter(aFilter);
		// 	this.getView().byId("idInitiateReconList2A").removeSelections();
		// 	this.byId("checkboxID").setSelected(false);
		// 	/*this.onInitiateRecon();*/
		// 	if (sQuery === "") {
		// 		this.byId("checkboxID").setSelected(true);
		// 		this.getView().byId("idInitiateReconList2A").selectAll();
		// 		this.onInitiateRecon();
		// 	}
		// 	//this.onInitiateRecon();
		// 	var oInitiateRecon1 = new sap.ui.model.json.JSONModel([]);
		// 	this.getView().setModel(oInitiateRecon1, "InitiateRecon2A");
		// },

		/*updatefinished: function (oevt) {
			this.path = this.byId("idInitiateReconList2A").getSelectedContextPaths();
		},*/

		/**
		 * Data for Recon Table Bind
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onInitiateRecon: function (oEvt) {
			//eslint-disable-line
			//var serchval = this.byId("searchId").getValue();
			var oView = this.getView();
			//======== Added by chaithra for requestidwise refresh issue on 21/1/2021 ========//
			this.getView().byId("idRequestIDwisePage2A").setVisible(false);
			this.getView().byId("idSplitDtl2A").setVisible(true);
			//========  Code ended by chaithra on 21/1/2021 ========//
			//var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTIN2A").getData();
			//oPath = oView.byId("idInitiateReconList2A").getSelectedContextPaths();
			//oPath = oView.byId("idInitiateReconList2A").getSelectedItem().getBindingContext("GSTIN2A").sPath;
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				/*var j = oPath[i].split('/')[1];*/
				aGSTIN.push(oModelData[j].gstin);
			}

			var selKey = this.byId("idDateRange2A").getSelectedKey();
			var postData = {
				"req": {
					"type": "",
					"entityId": [$.sap.entityID],
					"dataSecAttrs": {
						"GSTIN": aGSTIN
					},
					"gstin": aGSTIN,
					"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
					"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
					"toTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A1").getValue() : "", // Added by chaithra on 2/11/2020
					"fromTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A1").getValue() : "", //Added by chaithra on 2/11/2020
					"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
					"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
					"reconType": this.byId("idReconType").getSelectedKey()
				}

			};
			this.onInitiateRecon1(postData);
		},

		/**
		 * Data for Recon Table Bind
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onInitiateRecon1: function (postData) {
			// eslint-disable-line
			var oView = this.getView();
			var jsonForSearch = JSON.stringify(postData);
			var ELDetails = "/aspsapapi/gstr2InitiateRecon.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: jsonForSearch
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					oView.byId("idintable2A").setVisibleRowCount(data.resp.length + 1);
					var oInitiateRecon1 = new sap.ui.model.json.JSONModel(data.resp);
					oView.setModel(oInitiateRecon1, "InitiateRecon2A");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "InitiateRecon2A");
					//MessageBox.error("initiateRecon : Error");
				});
			});
		},

		/**
		 * click on Arrow mark of Report Selction
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		fnPressDownInit: function () {
			this.getView().byId("iMoreIniat2A").setVisible(false);
			this.getView().byId("iLessIniat2A").setVisible(true);
			this.getView().byId("idvbox12A").setVisible(false);
			this.getView().byId("idGrid12A").setVisible(false);
			this.getView().byId("idGrid2").setVisible(false);
			this.getView().byId("idvbox2").setVisible(false);

		},

		onIntiateReconFullScreen: function (oEvt) {
			var data = this.getView().getModel("InitiateRecon2A").getData()[0].items;
			if (oEvt === "open") {
				this.byId("closebut").setVisible(true);
				this.byId("openbut").setVisible(false);
				this.byId("oninreconTab").setFullScreen(true);
				this.byId("idintable2A").setVisibleRowCount(22);
			} else {
				this.byId("closebut").setVisible(false);
				this.byId("openbut").setVisible(true);
				this.byId("oninreconTab").setFullScreen(false);
				this.byId("idintable2A").setVisibleRowCount(data.length + 1);
			}
		},

		/**
		 * click on Arrow mark of Report Selction
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		fnPressRightInit: function () {
			this.getView().byId("iLessIniat2A").setVisible(false);
			this.getView().byId("iMoreIniat2A").setVisible(true);
			this.getView().byId("idvbox12A").setVisible(true);
			this.getView().byId("idGrid12A").setVisible(true);
			this.getView().byId("idGrid2").setVisible(true);
			this.getView().byId("idvbox2").setVisible(true);
		},

		/**
		 * click on Arrow mark of Data for Recon
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		fnPressDownInitR: function () {
			this.getView().byId("iMoreIniatR2A").setVisible(false);
			this.getView().byId("idintable2A").setVisible(false);
			this.getView().byId("iLessIniatR2A").setVisible(true);
		},

		/**
		 * click on Arrow mark of Data for Recon
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		fnPressRightInitR: function () {
			this.getView().byId("iLessIniatR2A").setVisible(false);
			this.getView().byId("idintable2A").setVisible(true);
			this.getView().byId("iMoreIniatR2A").setVisible(true);
		},

		onConfigExtractPress2A: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			var request = {
				"req": {
					"configId": this.oReqId,
					"reportType": ""
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr2DownloadDocument.do";
			this.excelDownload(request, oReqExcelPath);
		},

		onFragDownload: function (oEvt) {
			this.repName = oEvt.getSource().getBindingContext("DownloadDocument").getObject().reportName;
			var request = {
				"req": {
					"configId": this.oReqId.toString(),
					"reportType": this.repName.toString(),
					"reconType": this.reconType
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr2DownloadDocument.do";
			this.excelDownload(request, oReqExcelPath);
		},

		/**
		 * click on Download button on Request id wise Screen
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onConfigExtractPress2A1: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			this.reconType = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().reconType;
			/*var oReqExcelPath = "/aspsapapi/gstr2DownloadDocument.do?configId=" + this.oReqId + "";
			window.open(oReqExcelPath);*/
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			var oIntiData = {
				"req": {
					"configId": this.oReqId,
					"reconType": this.reconType
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var oIniPath = "/aspsapapi/gstr2DownloadIdWise.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "DownloadDocument");
					}
				}).fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "DownloadDocument");
				});
			});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		/**
		 * click on No.of GSTIN coliumn  on Request id wise Screen
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onPressGSTIN: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("ReqWiseData2A").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			for (var i = 0; i < TabData.resp.requestDetails.length; i++) {
				if (reqId === TabData.resp.requestDetails[i].requestId) {
					gstins.push(TabData.resp.requestDetails[i].gstins);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},

		/**
		 * click on Change of From Date
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onFromDateChange: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriod12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriod2A").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("From Date can not be greter then To Date");
				this.byId("idInitiateReconPeriod12A").setDateValue(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriod12A").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idInitiateReconPeriod12A").setMinDate(oevt.getSource().getDateValue());
			}
		},

		/**
		 * click on Change of From Tax Period
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param 
		 */
		onFromTaxPeriodChange: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriodTax12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("idInitiateReconPeriodTax12A").setDateValue(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriodTax12A").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idInitiateReconPeriodTax12A").setMinDate(oevt.getSource().getDateValue());
			}
		},
		//================== on click on change of 2A From Tax Period  developed by chaithra on 2/11/2020 ===========================================//
		onFromTaxPeriodChange2A: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriodTax12A1").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A1").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("idInitiateReconPeriodTax12A1").setDateValue(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriodTax12A1").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idInitiateReconPeriodTax12A1").setMinDate(oevt.getSource().getDateValue());
			}
		},

		//============================================ code ended by chaithra on 2/11/2020 =========================================================//

		onExcelPress: function () {
			var path = "/aspsapapi/gstr2InitiateReconDownloadReport.do";
			var oView = this.getView();
			//var oPath = [];
			var aGSTIN = [];

			var oModelData = oView.getModel("GSTIN2A").getData();
			/*oPath = oView.byId("idInitiateReconList2A").getSelectedContextPaths();

			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}*/
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				/*var j = oPath[i].split('/')[1];*/
				aGSTIN.push(oModelData[j].gstin);
			}

			var selKey = this.byId("idDateRange2A").getSelectedKey();
			var postData = {
				"req": {
					//	"entityId": [$.sap.entityID],
					"dataSecAttrs": {
						"GSTIN": aGSTIN
					},
					"gstin": aGSTIN,
					"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
					"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
					"toTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A1").getValue() : "", // Added by chaithra on 2/11/2020
					"fromTaxPeriod2A": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A1").getValue() : "", // Added by chaithra on 2/11/2020
					"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
					"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
					"reconType": this.byId("idReconType").getSelectedKey()
				}
			};
			this.excelDownload(postData, path);
		},

		//////////////////////////End/////////////////////////////////////
		////////////////////Intiate Recon Screen Vinay Kodam////////////////

		//////////////////////////Start/////////////////////////////////////
		////////////////////Recon Summary Screen Vinay Kodam////////////////

		onRSFromDateChange: function (oevt) {
			var toDate = this.byId("idRSTo").getDateValue(),
				fromDate = this.byId("idRSFrom").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("From Date can not be greter then To Date");
				this.byId("idRSTo").setDateValue(oevt.getSource().getDateValue());
				this.byId("idRSTo").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idRSTo").setMinDate(oevt.getSource().getDateValue());
			}

			if (model.resp === true) {
				if (this.byId("idReconTypeSummary").getSelectedKey() === "2BPR") {
					if (this.byId("idSummaryBase").getSelectedKey() === "RequestIDBase") {
						this.onConfigid();
					} else {
						this.onReconSummaryGSTIN1();
					}
				} else {
					this.onConfigid();
				}
			} else {
				this.onReconSummaryGSTIN1();
			}
		},

		onRSToDateChange: function () {
			var model = this.getView().getModel("onVisible").getData();
			if (model.resp === true) {
				if (this.byId("idReconTypeSummary").getSelectedKey() === "2BPR") {
					if (this.byId("idSummaryBase").getSelectedKey() === "RequestIDBase") {
						this.onConfigid();
					} else {
						this.onReconSummaryGSTIN1();
					}
				} else {
					this.onConfigid();
				}
			} else {
				this.onReconSummaryGSTIN1();
			}
		},

		onVisibleConfig: function () {

			var that = this;
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWiseInfo = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			var oReqWisePath = "/aspsapapi/checkForAP.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "onVisible");
					var key = that.byId("idReconTypeSummary").getSelectedKey();
					if (key === "2APR") {
						that.onReconSummaryGSTIN1();
					} else {
						that.byId("idSummaryBase").setSelectedKey("RequestIDBase");
						data.resp = true;
						that.getView().getModel("onVisible").refresh(true);
						that.onConfigid();
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onConfigid: function () {
			debugger;

			var oReqWiseInfo = {
				"entityId": $.sap.entityID.toString(),
				"toTaxPeriod": this.byId("idRSTo").getValue(),
				"fromTaxPeriod": this.byId("idRSFrom").getValue(),
				"reconType": this.byId("idReconTypeSummary").getSelectedKey()
			};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2GetConfigIds.do",
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				})
				.done(function (data, status, jqXHR) {
					debugger;
					var config = data.resp.configIdList;
					data.resp.configIdList.sort(function (a, b) {
						return a.configId - b.configId;
					});
					var oReqWiseModel = new JSONModel(data.resp);
					oReqWiseModel.setSizeLimit(config.length);
					this.getView().setModel(oReqWiseModel, "ConfigId");
					if (config.length) {
						var vConfigId = config.pop().configId.toString(),
							gstindata = {
								"req": {
									"configId": vConfigId,
								}
							};
						this.onReconSummaryGSTIN(gstindata);
						this.byId("idConfig").setSelectedKey(vConfigId);
					} else {
						this.getView().setModel(new JSONModel([]), "ReconSummaryDetails1");
						this.getView().setModel(new JSONModel([]), "ReconSummaryDetails");
						
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onChangeConfig: function () {
			var aGSTIN = [];
			var oModelData = this.getView().getModel("ReconSummaryDetails1").getData();
			var oPath = this.byId("idReconSumInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.ReconSummaryDetails1.sPath.split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			var gstindata = {
				"req": {
					"configId": this.byId("idConfig").getSelectedKey().toString(),
				}
			};
			this.onReconSummaryGSTIN(gstindata);

			var postData = {
				"req": {
					"gstin": aGSTIN,
					"configId": this.byId("idConfig").getSelectedKey().toString(),
					"returnPeriod": [],
					"reconType": this.byId("idReconTypeSummary").getSelectedKey()
				}
			};
			var url = "/aspsapapi/gstr2ReconSummary.do";
			this.onPressReconSummary1(postData, url);
		},

		onSelectGstin: function () {

			var aGSTIN = [];
			var oModelData = this.getView().getModel("ReconSummaryDetails1").getData();
			var oPath = this.byId("idReconSumInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.ReconSummaryDetails1.sPath.split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			if (aGSTIN.length === 0) {
				MessageBox.information("Please select atleast one GSTIN");
				return;
			}

			var model = this.getView().getModel("onVisible").getData();
			if (this.byId("idReconTypeSummary").getSelectedKey() === "2BPR") {
				if (this.byId("idSummaryBase").getSelectedKey() === "RequestIDBase") {
					var postData = {
						"req": {
							"gstin": aGSTIN,
							"configId": this.byId("idConfig").getSelectedKey().toString(),
							"returnPeriod": [],
							"reconType": this.byId("idReconTypeSummary").getSelectedKey()
						}
					};

					var url = "/aspsapapi/gstr2ReconSummary.do";
					this.onPressReconSummary1(postData, url);
				} else {
					var postData = {
						"req": {
							"configId": 0,
							"gstins": aGSTIN,
							"reconType": this.byId("idReconTypeSummary").getSelectedKey(),
							"toTaxPeriod": this.byId("idRSTo").getValue(),
							"fromTaxPeriod": this.byId("idRSFrom").getValue(),
							"fromTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_From").getValue(),
							"toTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_To").getValue(),
							"criteria": this.byId("idCriteria").getSelectedKey().replace('2A', '2B')
						}
					};
					var url = "/aspsapapi/gstr2BPRTaxReconSummary.do";
					this.onPressReconSummary1(postData, url);
				}
			} else {
				var config;
				if (model.resp === true) {
					config = this.byId("idConfig").getSelectedKey().toString();
				} else {
					config = 0;
				}
				var postData = {
					"req": {
						"configId": config,
						"gstins": aGSTIN,
						"reconType": this.byId("idReconTypeSummary").getSelectedKey(),
						"toTaxPeriod": this.byId("idRSTo").getValue(),
						"fromTaxPeriod": this.byId("idRSFrom").getValue(),
						"fromTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_From").getValue(),
						"toTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_To").getValue(),
						"criteria": this.byId("idCriteria").getSelectedKey()
					}
				};
				var url = "/aspsapapi/gstr2APAndNonAPReconSummary.do";
				this.onPressReconSummary1(postData, url);
			}
		},

		onReconSummaryGSTIN: function (gstindata) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2GetGstinforReconSummary.do",
					contentType: "application/json",
					data: JSON.stringify(gstindata)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp), "ReconSummaryDetails1");
						var postData = {
							"req": {
								"gstin": [],
								"configId": this.byId("idConfig").getSelectedKey().toString(),
								"returnPeriod": [],
								"reconType": this.byId("idReconTypeSummary").getSelectedKey()
							}
						};
						var url = "/aspsapapi/gstr2ReconSummary.do";
						this.onPressReconSummary1(postData, url);
					} else {
						var oGSTIN12 = new JSONModel([]);
						this.getView().setModel(oGSTIN12, "ReconSummaryDetails1");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					this.getView().setModel(oGSTIN2, "ReconSummaryDetails1");
				}.bind(this));
		},

		onReconSummaryGSTIN1: function (gstindata) {
			var oView = this.getView();
			var that = this;
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID)
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstinList.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oGSTIN = new JSONModel(data.resp);
						oView.setModel(oGSTIN, "ReconSummaryDetails1");
						var postData = {
							"req": {
								"gstin": [],
								"configId": "",
								"returnPeriod": [],
								"reconType": that.byId("idReconTypeSummary").getSelectedKey()
							}
						};
						var url = "/aspsapapi/gstr2ReconSummary.do";
						that.onPressReconSummary1(postData, url);
					} else {
						var oGSTIN1 = new JSONModel([]);
						oView.setModel(oGSTIN1, "ReconSummaryDetails1");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "ReconSummaryDetails1");
				});
			});
		},

		onSearchGstinsRS: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("idReconSumInitiateReconList2A");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("idReconSumInitiateReconList2A").removeSelections();
			/*this.byId("checkboxID").setSelected(false);*/
			/*this.onInitiateRecon();*/
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				//this.getView().byId("idReconSumInitiateReconList2A").selectAll();
				this.onChangeConfig();
			}
			//this.onInitiateRecon();
			//var oInitiateRecon1 = new sap.ui.model.json.JSONModel([]);
			//this.getView().setModel(oInitiateRecon1, "ReconSummaryDetails1");
		},

		onSelectallGSTINRS: function (oEvt) {
			if (oEvt.getSource().getSelected()) {
				this.getView().byId("idReconSumInitiateReconList2A").selectAll();
			} else {
				this.getView().byId("idReconSumInitiateReconList2A").removeSelections();
			}
			//this.onSelectGstin();
		},

		/**
		 * To Fetch GSTIN Data Request
		 * Developed by: vinay Kodam 11.07.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param GSTIN Data Request
		 */
		onPressReconSummary1: function (postData, url) {
			var oView = this.getView();
			//var that = this;

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						//oView.byId("idInitiateReconList2A").setVisibleRowCount(data.resp.det.length);
						//var oGSTIN1 = new JSONModel(data.resp.requestDetails);
						//oView.setModel(oGSTIN1, "ReconSummaryDetails1");
						//oView.byId("checkboxRSID").setSelected(true);
						//oView.byId("idReconSumInitiateReconList2A").selectAll();
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.requestDetails.reconSummaryDto.length; i++) {
							var ele = data.resp.requestDetails.reconSummaryDto[i];
							var lvl = ele.level; // Get the level of the cur Obj.
							if (lvl === "L1") {
								curL1Obj = ele;
								retArr.push(curL1Obj);
								curL1Obj.level2 = [];
							}
							if (lvl === "L2") {
								curL2Obj = ele;
								curL1Obj.level2.push(curL2Obj);
								curL2Obj.level3 = [];
							}
							if (lvl === "L3") {
								curL2Obj.level3.push(ele);
							}
						}
						var oGSTIN = new JSONModel(retArr);
						oView.setModel(oGSTIN, "ReconSummaryDetails");
						//that.onInitiateRecon();
					} else {
						var oGSTIN12 = new JSONModel([]);
						oView.setModel(oGSTIN12, "ReconSummaryDetails");
						//oView.setModel(oGSTIN1, "InitiateRecon2A");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "ReconSummaryDetails");
					//oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
			});
		},

		onDownloadSummary: function () {
			var url = "/aspsapapi/gstr2ReconSummaryDownloadReport.do";
			var aGSTIN = [];
			var oModelData = this.getView().getModel("ReconSummaryDetails1").getData();
			var oPath = this.byId("idReconSumInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.ReconSummaryDetails1.sPath.split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			var model = this.getView().getModel("onVisible").getData();
			if (this.byId("idReconTypeSummary").getSelectedKey() === "2BPR") {

				if (this.byId("idSummaryBase").getSelectedKey() === "RequestIDBase") {
					var postData = {
						"req": {
							"entityId": Number($.sap.entityID),
							"gstins": aGSTIN,
							"configId": this.byId("idConfig").getSelectedKey().toString(),
							"returnPeriod": [],
							"reconType": this.byId("idReconTypeSummary").getSelectedKey()
						}
					};
				} else {
					var postData = {
						"req": {
							"configId": 0,
							"entityId": Number($.sap.entityID),
							"gstins": aGSTIN,
							"returnPeriod": [],
							"reconType": this.byId("idReconTypeSummary").getSelectedKey(),
							"toTaxPeriod": this.byId("idRSTo").getValue(),
							"fromTaxPeriod": this.byId("idRSFrom").getValue(),
							"fromTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_From").getValue(),
							"toTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_To").getValue(),
							"criteria": this.byId("idCriteria").getSelectedKey().replace('2A', '2B')
						}
					};
				}

			} else {
				var config;
				if (model.resp === true) {
					config = this.byId("idConfig").getSelectedKey().toString();
				} else {
					config = 0;
				}
				var postData = {
					"req": {
						"entityId": Number($.sap.entityID),
						"configId": config,
						"gstins": aGSTIN,
						"reconType": this.byId("idReconTypeSummary").getSelectedKey(),
						"toTaxPeriod": this.byId("idRSTo").getValue(),
						"fromTaxPeriod": this.byId("idRSFrom").getValue(),
						"fromTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_From").getValue(),
						"toTaxPeriod_2A": this.byId("idInitiateReconPeriodTax2A_To").getValue(),
						"criteria": this.byId("idCriteria").getSelectedKey()
					}
				};
			}
			this.excelDownload(postData, url);
		},

		onExpandRS: function (oEvent) {
			if (oEvent.getSource().getId().includes("expand")) {
				this.byId("id_TreeRecon").expandToLevel(1);
			} else {
				this.byId("id_TreeRecon").collapseAll();
			}
		},

		onRSDetFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebutRS").setVisible(true);
				this.byId("openbutRS").setVisible(false);
				this.byId("id_reconaction").setFullScreen(true);
				this.byId("id_TreeRecon").setVisibleRowCount(26);
			} else {
				this.byId("closebutRS").setVisible(false);
				this.byId("openbutRS").setVisible(true);
				this.byId("id_reconaction").setFullScreen(false);
				this.byId("id_TreeRecon").setVisibleRowCount(10);
			}
		},

		//////////////////////////End/////////////////////////////////////
		////////////////////Recon Summary Screen Vinay Kodam////////////////

		/*************************************************************************************************
		 * Recon Result Screen functionality
		 *************************************************************************************************/
		handleChangeRR: function (oEvt, type) {
			switch (type) {
			case "T":
				var idFr = "dtConsldFrom",
					idTo = "dtConsld1To";
				break;
			case "D":
				idFr = "dtDocFrom";
				idTo = "dtDocTo";
				break;
			case "T3b":
				idFr = "dFrPeriod3b";
				idTo = "dToPeriod3b";
				break;
			}
			var toDate = this.byId(idTo).getDateValue(),
				frDate = this.byId(idFr).getDateValue(),
				value = oEvt.getSource().getDateValue();

			if (!!toDate && frDate > toDate) {
				this.byId(idTo).setDateValue(value);
				this.byId(idTo).setMinDate(value);
			} else {
				this.byId(idTo).setMinDate(value);
			}
		},

		onFromTaxPeriodChange2A_RS: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriodTax2A_To").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A_From").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("idInitiateReconPeriodTax2A_To").setMinDate(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriodTax2A_To").setDateValue(oevt.getSource().getDateValue());

			} else {
				this.byId("idInitiateReconPeriodTax2A_To").setMinDate(oevt.getSource().getDateValue());
			}
			this.onConfigid();
		},

		reportType: function () {
			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			var oIniPath = "/aspsapapi/gstr2GetReportTypeList.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: [{}]
				}).done(function (data, status, jqXHR) {
					oIniModel.setData(data.resp.reportTypeList);
					data.resp.reportTypeList.unshift({
						reportType: "All"
					})
					oIniView.setModel(oIniModel, "reportTypeList");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		_getRegularReportData: function (key, text) {
			return [{
				key: "Exact Match",
				text: "Exact Match"
			}, {
				key: "Match With Tolerance",
				text: "Match with Tolerance"
			}, {
				key: "Value Mismatch",
				text: "Value Mismatch"
			}, {
				key: "POS Mismatch",
				text: "POS Mismatch"
			}, {
				key: "Doc Date Mismatch",
				text: "Doc Date Mismatch"
			}, {
				key: "Doc Type Mismatch",
				text: "Doc Type Mismatch"
			}, {
				key: "Doc No Mismatch I",
				text: "Doc No Mismatch I"
			}, {
				key: "Doc No Mismatch II",
				text: "Doc No Mismatch II"
			}, {
				key: "Doc No & Doc Date Mismatch",
				text: "Doc No & Doc Date Mismatch"
			}, {
				key: "Multi-Mismatch",
				text: "Multi-Mismatch"
			}, {
				key: "Potential-I",
				text: "Potential I"
			}, {
				key: "Potential-II",
				text: "Potential II"
			}, {
				key: "Logical Match",
				text: "Logical Match"
			}, {
				key: "Addition in PR",
				text: "Addition in PR"
			}, {
				key: "Addition in " + key,
				text: "Addition in " + text
			}];
		},

		_getImportReportData: function (key, text) {
			return [{
				key: "Exact Match IMPG",
				text: "Import - Match"
			}, {
				key: "Mismatch IMPG",
				text: "Import - Mismatch"
			}, {
				key: "Addition in PR IMPG",
				text: "Import - Addition in PR"
			}, {
				key: "Addition in " + key + " IMPG",
				text: "Import - Addition in " + text
			}];
		},

		onReconResultCriteria: function (key) {
			var oGrpPermision = this.getOwnerComponent().getModel("GroupPermission").getProperty("/"),
				aItems = this.byId("fbRecResult").getFilterGroupItems(),
				oModel = this.getView().getModel("Display"),
				obj = oModel.getProperty("/"),
				flag = (key === "Regular" && !!oGrpPermision.GR1 && (obj.LabelRR === "2B" ? !!oGrpPermision.GR2 : !!oGrpPermision.GR4));

			if (key === "Import") {
				var oRepType = this._getImportReportData(obj.LabelRR1, obj.LabelRR);
			} else {
				oRepType = this._getRegularReportData(obj.LabelRR1, obj.LabelRR);
				if (key === "ISD") {
					var idx = oRepType.findIndex(function (e) {
						return e.key === "POS Mismatch";
					});
					oRepType.splice(idx, 1);
				}
			}
			oModel.setProperty("/imsResponse", flag);
			oModel.refresh(true);
			oRepType.unshift({
				key: "All",
				text: "All"
			});
			oRepType.push({
				key: "Force Match",
				text: "Locked - Force Match"
			});
			oRepType.push({
				key: "3B Response",
				text: "Locked - 3B Response"
			});
			this.byId("idConsldReptType").setModel(new JSONModel(oRepType), "ReportType");
			this.byId("idConsldReptType").setSelectedKeys(null);
			aItems[8].setLabel(key === "Import" ? "From BOE Date" : "From Document Date");
			aItems[9].setLabel(key === "Import" ? "To BOE Date" : "To Document Date");
			aItems[10].setLabel(key === "Import" ? "BOE No." : "Document No");
		},

		onChangeSegmentBtn: function (oEvent) {
			var oModel = this.getView().getModel("Display"),
				vPageNo = !oModel.getProperty("/swRecon") ? oModel.getProperty("/inPageNoRRL") : oModel.getProperty("/inPageNoRR");

			this._getReconResult(vPageNo);
		},

		onReconResults: function () {
			this._getReconResult();
		},

		_getReconResult: function (vPageNo) {
			var oModel = this.getView().getModel("Display"),
				vId = (!oModel.getProperty("/swRecon") ? "idConsldReptType" : "iUnlockReportType"),
				aDocToken = this.byId("reconDocnumber").getTokens(),
				accVocNo = this.byId("iAccVocNo").getTokens(),
				vendorPan = this.byId("iVendorPAN").getValue(),
				vendorGstin = this.byId("iVendorGstin").getTokens(),
				reconType = this.byId("idReconTypeRR").getSelectedItem().getKey(),
				payload = {
					"hdr": {
						"pageNum": (!vPageNo ? 0 : vPageNo - 1),
						"pageSize": (!oModel.getProperty("/swRecon") ? oModel.getProperty("/pgSizeRRL") : oModel.getProperty("/pgSizeRR"))
					},
					"req": {
						"entityId": $.sap.entityID,
						"reconType": reconType,
						"gstins": this.removeAll(this.byId("idRRGstins").getSelectedKeys()),
						"taxPeriodBase": this.byId("sTaxPeriodBase").getSelectedKey().replace('/', ''),
						"toTaxPeriod": this.byId("dtConsld1To").getValue(),
						"fromTaxPeriod": this.byId("dtConsldFrom").getValue(),
						"reconCriteria": this.byId("sRecResultCriteria").getSelectedKey(),
						"reportType": this.removeAll(this.byId(vId).getSelectedKeys()),
						"fromDocDate": this.byId("dtDocFrom").getValue(),
						"toDocDate": this.byId("dtDocTo").getValue(),
						"docType": this.removeAll(this.byId("DocTypeIDConslM").getSelectedKeys()),
						"identifier": (!oModel.getProperty("/swRecon") ? "SINGLE" : "MULTI"),
						"docNumberList": aDocToken.map(function (e) {
							return e.getKey();
						}),
						"accVoucherNums": accVocNo.map(function (e) {
							return e.getKey();
						}),
						"vndrPans": !vendorPan ? [] : [vendorPan.trim()],
						"vndrGstins": vendorGstin.map(function (e) {
							return e.getKey();
						}),
						"frmTaxPrd3b": this.byId("dFrPeriod3b").getValue(),
						"toTaxPrd3b": this.byId("dToPeriod3b").getValue()
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResult.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.byId("idReconRTable").clearSelection();
					this.getView().setModel(new JSONModel(data), "visReport");
					var key = this.byId("sRecResultCriteria").getSelectedKey(),
						oReconResModel = new JSONModel([]);

					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
					} else if (data.resp !== "No Records Found") {
						oReconResModel.setData(data.resp.reconResponse);
						oReconResModel.setSizeLimit(data.resp.reconResponse.length);
					}
					this.getView().setModel(new JSONModel(data.resp.summary || {}), "ReconResultHeader");
					if (!this.getView().getModel("Display").getProperty("/swRecon")) {
						this.getView().setModel(oReconResModel, "ConsldTablData");
						this._nicPaginationRR(data.hdr);
						this.byId("recDocNo").setVisible((key !== "Import"));
						this.byId("recDocDate").setVisible((key !== "Import"));
						this.byId("recBoeNo").setVisible((key === "Import"));
						this.byId("recBoeDate").setVisible((key === "Import"));
					} else {
						this.getView().setModel(new JSONModel(this._multiUnlockData(data.resp.reconResponse, key)), "UnlockReconResult");
						this._uplockReconPagination(data.hdr);
						this.byId("recMuDocNo").setVisible((key !== "Import"));
						this.byId("recMuDocDate").setVisible((key !== "Import"));
						this.byId("recMuBoeNo").setVisible((key === "Import"));
						this.byId("recMuBoeDate").setVisible((key === "Import"));
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error(jqXHR.responseJSON.resp, {
						styleClass: "sapUiSizeCompact"
					});
				});
		},

		_nicPaginationRR: function (header) {
			var oModel = this.getView().getModel("Display"),
				vTotal = Math.ceil((header.totalCount || 0) / oModel.getProperty("/pgSizeRRL")),
				vPageNo = (vTotal ? header.pageNum + 1 : 0);

			oModel.setProperty("/inPageNoRRL", vPageNo);
			oModel.setProperty("/pgTotalRRL", vTotal);
			oModel.setProperty("/ePageNoRRL", vTotal > 1);
			oModel.setProperty("/bFirstRRL", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrevRRL", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNextRRL", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLastRRL", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		_uplockReconPagination: function (header) {
			var oModel = this.getView().getModel("Display"),
				vTotal = Math.ceil(header.totalCount / oModel.getProperty("/pgSizeRR")),
				vPageNo = (vTotal ? header.pageNum + 1 : 0);

			oModel.setProperty("/inPageNoRR", vPageNo);
			oModel.setProperty("/pgTotalRR", vTotal);
			oModel.setProperty("/ePageNoRR", vTotal > 1);
			oModel.setProperty("/bFirstRR", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrevRR", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNextRR", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLastRR", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		_multiUnlockData: function (data, key) {
			var optMultiResp = this.getView().getModel("visReport").getProperty("/optedMultiResponse"),
				aBatchId = [],
				oData = [];

			if (data === "No Records Found") {
				return oData;
			}
			data.forEach(function (item) {
				if (!aBatchId.includes(item.batchId)) {
					var obj = {
						"batchId": item.batchId,
						"gstins": [],
						"vendorGstins": [],
						"docTypes": [],
						"docNumbers": [],
						"docDates": [],
						"boeNumbers": [],
						"boeDates": [],
						"totalTax": [],
						"igst": [],
						"cgst": [],
						"sgst": [],
						"cess": [],
						"accVoucherNo": [],
						"reason": [],
						"reconLinkIds": [],
						"visiMore": false
					};
					oData.push(obj);
					aBatchId.push(item.batchId);
				} else {
					obj = oData[aBatchId.indexOf(item.batchId)];
				}
				if (obj.gstins.length < 2) {
					obj.gstins.push({
						"gstin": item.gstin,
						"gstin2A": item.gstin2A,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.vendorGstins.push({
						"vendorGstin": item.vendorGstin,
						"vendorGstin2A": item.vendorGstin2A,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.docTypes.push({
						"docTypePR": item.docTypePR,
						"docType2A": item.docType2A,
						"isDocTypeMatch": item.isDocTypeMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.docNumbers.push({
						"docNumberPR": item.docNumberPR,
						"docNumber2A": item.docNumber2A,
						"isDocNumberMatch": item.isDocNumberMatch,
						"visi": this._getMUnlockVisibility(item, key)
					});
					obj.docDates.push({
						"docDatePR": item.docDatePR,
						"docDate2A": item.docDate2A,
						"isDocDateMatch": item.isDocDateMatch,
						"visi": this._getMUnlockVisibility(item, key)
					});
					obj.boeNumbers.push({
						"boeNoPR": item.boeNoPR,
						"boeNo2A": item.boeNo2A,
						"isBoeNoMatch": item.isBoeNoMatch,
						"visi": this._getMUnlockVisibility(item, key)
					});
					obj.boeDates.push({
						"boeDatePR": item.boeDatePR,
						"boeDate2A": item.boeDate2A,
						"isBoeDateMatch": item.isBoeDateMatch,
						"visi": this._getMUnlockVisibility(item, key)
					});
					obj.totalTax.push({
						"totalTaxPR": item.totalTaxPR,
						"totalTax2A": item.totalTax2A,
						"isTotalTaxMatch": item.isTotalTaxMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.igst.push({
						"igstPR": item.igstPR,
						"igst2A": item.igst2A,
						"isIgstMatch": item.isIgstMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.cgst.push({
						"cgstPR": item.cgstPR,
						"cgst2A": item.cgst2A,
						"isCgstMatch": item.isCgstMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.sgst.push({
						"sgstPR": item.sgstPR,
						"sgst2A": item.sgst2A,
						"isSgstMatch": item.isSgstMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.cess.push({
						"cessPR": item.cessPR,
						"cess2A": item.cess2A,
						"isCessMatch": item.isCessMatch,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.accVoucherNo.push({
						"acVocNo": item.accVoucherNo,
						"visi": this._getMUnlockVisibility(item)
					});
					obj.reason.push({
						"mismatchReason": item.mismatchReason,
						"visi": this._getMUnlockVisibility(item)
					});
					if (!obj.responseTaken) {
						obj.responseTaken = item.responseTaken;
					}
					if (!obj.respRemarks) {
						obj.respRemarks = item.respRemarks;
					}
					obj.reconLinkIds.push({
						"reconLinkId": item.reconLinkId
					});
				} else {
					obj.visiMore = true;
				}
			}.bind(this));
			return oData;
		},

		_getMUnlockVisibility: function (obj, key) {
			if (key === 'Import') {
				return (!!obj.gstin2A && !!obj.gstin) || (!!obj.vendorGstin2A && !!obj.vendorGstin) ||
					(!!obj.boeNo2A && !!obj.boeNoPR) || (!!obj.totalTax2A && !!obj.totalTaxPR);
			}
			return (!!obj.gstin2A && !!obj.gstin) || (!!obj.vendorGstin2A && !!obj.vendorGstin) ||
				(!!obj.docNumber2A && !!obj.docNumberPR) || (!!obj.totalTax2A && !!obj.totalTaxPR);
		},

		_displayControl: function (type, value1, value2, value3) {
			switch (type) {
			case "V":
				return !!value1;
			case "T":
				return (value1 ? !value1 && !value2 : value2 && !value1);
			case "T1":
				return (value1 ? !value1 && !value3 : (value3 ? value3 && !value1 : !value1 && !value2));
			}
		},

		onViewMoreReconResult: function (oEvent) {
			if (!this._recResultMulti) {
				this._recResultMulti = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReconResultUnlockMore", this);
				this.getView().addDependent(this._recResultMulti);
			}
			var obj = oEvent.getSource().getBindingContext("UnlockReconResult").getObject(),
				oData = this.getView().getModel("visReport").getProperty("/resp"),
				aData = oData.filter(function (e) {
					return e.batchId === obj.batchId;
				});
			this._recResultMulti.setModel(new JSONModel({
				"reconCriteria": this.byId("sRecResultCriteria").getSelectedKey(),
				"data": aData
			}), "ReconMultiUnlock");
			this._recResultMulti.open();
		},

		onRecResultMultiClose: function () {
			this._recResultMulti.close();
		},

		onPaginationUnlockRR: function (btn) {
			var oModel = this.getView().getModel("Display");
			switch (btn) {
			case 'F':
				oModel.setProperty("/inPageNoRR", 1);
				break;
			case 'P':
				oModel.setProperty("/inPageNoRR", oModel.getProperty("/inPageNoRR") - 1);
				break;
			case 'N':
				oModel.setProperty("/inPageNoRR", oModel.getProperty("/inPageNoRR") + 1);
				break;
			case 'L':
				oModel.setProperty("/inPageNoRR", oModel.getProperty("/pgTotalRR"));
				break;
			}
			oModel.refresh(true);
			this._getReconResult(+oModel.getProperty("/inPageNoRR"));
		},

		onSubmitPaginationUnlock: function () {
			var pageNo = this.getView().getModel("Display").getProperty("/inPageNoRR");
			this._getReconResult(+pageNo);
		},

		onPaginationLockRR: function (btn) {
			var oModel = this.getView().getModel("Display");
			switch (btn) {
			case 'F':
				oModel.setProperty("/inPageNoRRL", 1);
				break;
			case 'P':
				oModel.setProperty("/inPageNoRRL", oModel.getProperty("/inPageNoRRL") - 1);
				break;
			case 'N':
				oModel.setProperty("/inPageNoRRL", oModel.getProperty("/inPageNoRRL") + 1);
				break;
			case 'L':
				oModel.setProperty("/inPageNoRRL", oModel.getProperty("/pgTotalRRL"));
				break;
			}
			oModel.refresh(true);
			this._getReconResult(+oModel.getProperty("/inPageNoRRL"));
		},

		onSubmitPaginationRR: function () {
			var pageNo = this.getView().getModel("Display").getProperty("/inPageNoRRL");
			this._getReconResult(+pageNo);
		},

		onRequestStatus: function () {
			var oReqWiseInfo = {
				"req": {
					"entityId": $.sap.entityID.toString(),
					"identifier": (!this.getView().getModel("Display").getProperty("/swRecon") ? "SINGLE" : "MULTI")
				}
			};
			this.byId("idDynmcPagReconResult").setVisible(false);
			this.byId("idRequestStatus2A").setVisible(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2RequestStatusData.do",
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data), "ReqWiseData2A1");
					} else {
						this.getView().setModel(new JSONModel(), "ReqWiseData2A1");
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel(), "ReqWiseData2A1");
				}.bind(this));
		},

		onBulkRespDateChange: function (oEvent, type) {
			if (type === "wo3B") {
				var oModel = this.dBulkWo3bLock.getModel("ForceMatch");
			} else {
				var oModel = this.dBulkWith3bLock.getModel("ForceMatch");
			};
			var frDate = oEvent.getSource().getDateValue(),
				toDate = oModel.getProperty("/toDate");

			if (frDate > toDate) {
				oModel.setProperty("/toDate", new Date(frDate));
			}
			oModel.refresh(true);
		},

		onPressGSTIN1: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("ReqWiseData2A1").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A1").getObject().requestId;
			for (var i = 0; i < TabData.resp.requestDetails.length; i++) {
				if (reqId === TabData.resp.requestDetails[i].requestId) {
					gstins.push(TabData.resp.requestDetails[i].gstins);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},

		onConfigExtractPress2A12: function (oEvt) {
			var obj = oEvt.getSource().getBindingContext("ReqWiseData2A1").getObject(),
				oIntiData = {
					"req": {
						"filePath": obj.errFilePath,
						"fileId": obj.fileId
					}
				};
			this.oReqId = obj.reqId;
			this.reportDownload(oIntiData, "/aspsapapi/getRequestStatusErrorFile.do")
		},

		onPressRRrequest: function () {
			this.byId("idDynmcPagReconResult").setVisible(true);
			this.byId("idRequestStatus2A").setVisible(false);
		},

		_getBulkRespReportType: function (key, criteria, obj) {
			if (criteria === "Import") {
				var oRepType = this._getImportReportData(obj.LabelRR1, obj.LabelRR);
			} else {
				oRepType = this._getRegularReportData(obj.LabelRR1, obj.LabelRR);
				if (criteria === "ISD") {
					var idx = oRepType.findIndex(function (e) {
						return e.key === "POS Mismatch";
					});
					oRepType.splice(idx, 1);
				}
			}
			if (key === "3BResponse") {
				oRepType.forEach(function (e) {
					e.enable = true;
				});
			} else if (key === "IMSResponse") {
				oRepType.push({
					key: "Force Match",
					text: "Locked - Force Match"
				});
				oRepType.push({
					key: "3B Response",
					text: "Locked - 3B Response"
				});
			}
			oRepType.forEach(function (e) {
				e.select = 'T';
			});
			return oRepType;
		},

		onPressBulkResponse: function (oEvt) {
			var key = oEvt.getParameter("item").getKey(),
				obj = this.getView().getModel("Display").getProperty("/"),
				criteria = this.byId("sRecResultCriteria").getSelectedKey();

			switch (key) {
			case "ForcedMatch":
				this._bulkRespWithout3bLock(key, criteria, obj);
				break;
			case "3BResponse":
				this._bulkRespWith3bLock(key, criteria, obj);
				break;
			case "UNLOCK":
				this._bulkRespUnlock(criteria);
				break;
			case "IMSResponse":
				this._bulkImsResponseAction(key, criteria, obj);
				break;
			}
		},

		_bulkRespWithout3bLock: function (key, criteria, obj) {
			var aRepType = this._getBulkRespReportType(key, criteria, obj),
				reconType = this.byId("idReconTypeRR").getSelectedKey(),
				taxBase = this.byId("sTaxPeriodBase").getSelectedKey(),
				gstin = this.byId("idRRGstins").getSelectedKeys(),
				vDate = new Date();

			if (!this.dBulkWo3bLock) {
				this.dBulkWo3bLock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.BulkResForcedMatch", this);
				this.getView().addDependent(this.dBulkWo3bLock);
				this.setReadonly("dtFr3bLockWo");
				this.setReadonly("dtTo3bLockWo");
				this._setTokenValidator("iWo3bLockVenGstin");
			}
			this.byId("idBRFReptType").setModel(new JSONModel(aRepType), "FmReportType");
			this.byId("idBRFReptType").setValueState("None");
			this.byId("iWo3bLockVenGstin").removeAllTokens();

			this.dBulkWo3bLock.setModel(new JSONModel({
				"reconType": reconType,
				"taxBase": taxBase,
				"frDate": this.byId("dtConsldFrom").getDateValue(),
				"toDate": this.byId("dtConsld1To").getDateValue(),
				"vendorPan": null,
				"gstin": gstin,
				"reconCriteria": criteria,
				"reportType": [],
				"mismatchReason": [],
				"eMismatch": false,
				"remarks": null,
				"maxDate": vDate
			}), "ForceMatch");
			this.dBulkWo3bLock.open();
		},

		_bulkRespWith3bLock: function (key, criteria, obj) {
			var aRepType = this._getBulkRespReportType(key, criteria, obj),
				reconType = this.byId("idReconTypeRR").getSelectedKey(),
				taxBase = this.byId("sTaxPeriodBase").getSelectedKey(),
				gstin = this.byId("idRRGstins").getSelectedKeys(),
				vDate = new Date();

			if (!this.dBulkWith3bLock) {
				this.dBulkWith3bLock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.BulkRes3BResponse", this);
				this.getView().addDependent(this.dBulkWith3bLock);
				this.setReadonly("dtFrW3bLock");
				this.setReadonly("dtToW3bLock");
				this._setTokenValidator("iW3bLockVenGstin");
			}

			if (reconType === "2B_PR" && criteria !== "Import" && this.getView().getModel("visReport").getProperty("/optionOpted") === "A") {
				var obj = aRepType.find(function (e) {
					return e.key === "Addition in PR";
				});
				obj.enable = false;
			}
			this.byId("idBR3ReptType").setModel(new JSONModel(aRepType), "Be3bReportType");
			this.byId("idBR3ReptType").setValueState("None");
			this.byId("iW3bLockVenGstin").removeAllTokens();

			this.dBulkWith3bLock.setModel(new JSONModel({
				"reconType": reconType,
				"taxBase": taxBase,
				"frDate": this.byId("dtConsldFrom").getDateValue(),
				"toDate": this.byId("dtConsld1To").getDateValue(),
				"vendorPan": null,
				"gstin": gstin,
				"reconCriteria": criteria,
				"reportType": [],
				"mismatchReason": [],
				"eMismatch": false,
				"taxPeriod3b": null,
				"remarks": null,
				"maxDate": vDate
			}), "ForceMatch");
			this.dBulkWith3bLock.open();
		},

		_bulkRespUnlock: function (criteria) {
			var reconType = this.byId("idReconTypeRR").getSelectedKey(),
				taxBase = this.byId("sTaxPeriodBase").getSelectedKey(),
				frDate = this.byId("dtConsldFrom").getDateValue(),
				toDate = this.byId("dtConsld1To").getDateValue(),
				gstin = this.byId("idRRGstins").getSelectedKeys(),
				vDate = new Date();

			if (!this.dBulkRespUnlock) {
				this.dBulkRespUnlock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.BulkResUnlock", this);
				this.getView().addDependent(this.dBulkRespUnlock);
				this.setReadonly("frUnlock");
				this.setReadonly("toUnlock");
				this.byId("frUnlock").setMaxDate(vDate);
				this.byId("toUnlock").setMaxDate(vDate);
				this._setTokenValidator("iUnlockVenGstin");
			}
			this.byId("frUnlock").setDateValue(frDate);
			this.byId("toUnlock").setDateValue(toDate);
			this.byId("toUnlock").setMinDate(frDate);

			this.byId("sTaxBaseUnlock").setSelectedKey(taxBase);
			this.byId("iUnlockVenPan").setValue("");
			this.byId("iUnlockVenGstin").removeAllTokens();

			this.byId("idUNReconType").setSelectedKey(reconType);
			this.byId("idBRFUNGstins").setSelectedKeys(gstin);
			this.byId("sUnlockCriteria").setSelectedKey(criteria);
			this.byId("idBRUNReptType").setSelectedKeys([]);
			this.dBulkRespUnlock.open();
		},

		onBulkRRClose: function (oEvt) {
			this.dBulkWo3bLock.close();
		},

		onBulk3BClose: function (oEvt) {
			this.dBulkWith3bLock.close();
		},

		onBulkUnlockClose: function (oEvt) {
			this.dBulkRespUnlock.close();
		},

		onSaveRR: function () {
			var TablIndx = this.byId("idReconRTable").getSelectedIndices();
			if (TablIndx.length === 0) {
				MessageBox.information("Please select at least one GSTIN");
				return;
			}
			var saveData = [];
			var TablData = this.getView().getModel("ConsldTablData").getData();
			for (var i = 0; i < TablIndx.length; i++) {
				var obj = {
					"a2Id": TablData[TablIndx[i]].a2Id,
					"prId": TablData[TablIndx[i]].prId,
					"actionTaken": TablData[TablIndx[i]].actionTaken,
					"reconLinkId": TablData[TablIndx[i]].reconLinkId
				};
				saveData.push(obj);
			}

			var FinalData = {
				"req": {
					"resp": "save",
					"reconIds": saveData
				}
			};
			this.saveChang(FinalData);
		},

		onFMSave: function () {
			var oFilter = this.dBulkWo3bLock.getModel("ForceMatch").getProperty("/"),
				aToken = this.byId("iWo3bLockVenGstin").getTokens(),
				payload = {
					"req": {
						"entityId": ('' + $.sap.entityID),
						"reconCriteria": oFilter.reconCriteria,
						"reconType": oFilter.reconType,
						"taxPeriodBase": oFilter.taxBase,
						"gstins": this.removeAll(oFilter.gstin),
						"fromTaxPeriod": oFilter.frDate.getFullYear() + (oFilter.frDate.getMonth() + 1).toString().padStart(2, 0),
						"toTaxPeriod": oFilter.toDate.getFullYear() + (oFilter.toDate.getMonth() + 1).toString().padStart(2, 0),
						"reportTypes": oFilter.reportType,
						"responseRemarks": oFilter.remarks,
						"vendorPans": !oFilter.vendorPan ? [] : [oFilter.vendorPan.trim()],
						"indentifier": "Force",
						"mismatchReason": [],
						"taxPeriodGstr3b": ""
					}
				};
			if (!oFilter.gstin.length) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			} else if (!oFilter.reportType.length) {
				MessageBox.error("Please select Report Type");
				return;
			}
			if ((oFilter.reportType.includes("Doc Date Mismatch") || oFilter.reportType.includes("Doc No & Doc Date Mismatch"))) {
				if (!oFilter.mismatchReason.length) {
					MessageBox.error("Please select Mismatch Reason");
					return;
				} else {
					payload.req.mismatchReason = oFilter.mismatchReason;
				}
			}
			payload.req.vendorGstins = aToken.map(function (e) {
				return e.getText();
			});
			this._postBulkResponse(payload, this.dBulkWo3bLock);
		},

		onRepTypeSelection: function (oEvent, model) {
			var aRepType = oEvent.getSource().getSelectedKeys(),
				oModel = oEvent.getSource().getModel(model),
				oData = oModel.getProperty('/');

			if (aRepType.length > 5) {
				var key = oEvent.getParameter("changedItem").getKey(),
					vIndex = aRepType.indexOf(key);

				aRepType.splice(vIndex, 1);
				oEvent.getSource().setSelectedKeys(aRepType);
				return;
			}
			if (aRepType.length === 5) {
				oData.forEach(function (item) {
					if (!aRepType.includes(item.key)) {
						item.select = 'F';
					}
				});
				oEvent.getSource().setValueStateText("Max 5 report types are allowed to select at one time");
				oEvent.getSource().setValueState("Warning");
			} else {
				oEvent.getSource().setValueStateText(null);
				oEvent.getSource().setValueState("None");
				oData.forEach(function (item) {
					item.select = 'T';
				});
			}
			oModel.refresh(true);

			var oModelFM = oEvent.getSource().getModel('ForceMatch');
			oModelFM.setProperty("/eMismatch", (aRepType.includes("Doc Date Mismatch") || aRepType.includes("Doc No & Doc Date Mismatch")));
			oModelFM.setProperty("/mismatchReason",
				(aRepType.includes("Doc Date Mismatch") || aRepType.includes("Doc No & Doc Date Mismatch") ? ["Document Date (Same Month)",
					"Document Date (Different Month)"
				] : []))
			oModelFM.refresh(true);
		},

		onBulk3BResSave: function () {
			var oFilter = this.dBulkWith3bLock.getModel("ForceMatch").getProperty("/"),
				aToken = this.byId("iW3bLockVenGstin").getTokens(),
				payload = {
					"req": {
						"entityId": ('' + $.sap.entityID),
						"reconType": oFilter.reconType,
						"taxPeriodBase": oFilter.taxBase,
						"fromTaxPeriod": oFilter.frDate.getFullYear() + (oFilter.frDate.getMonth() + 1).toString().padStart(2, 0),
						"toTaxPeriod": oFilter.toDate.getFullYear() + (oFilter.toDate.getMonth() + 1).toString().padStart(2, 0),
						"gstins": this.removeAll(oFilter.gstin),
						"reconCriteria": oFilter.reconCriteria,
						"reportTypes": oFilter.reportType,
						"vendorPans": (!oFilter.vendorPan ? [] : [oFilter.vendorPan.trim()]),
						"taxPeriodGstr3b": this._formatPeriod(oFilter.taxPeriod3b),
						"responseRemarks": oFilter.remarks,
						"mismatchReason": [],
						"indentifier": "3B"
					}
				};

			if (!oFilter.gstin.length) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			} else if (!oFilter.reportType.length) {
				MessageBox.error("Please select Report Type");
				return;
			} else if (!oFilter.taxPeriod3b) {
				MessageBox.error("Please select Tax Period for GSTR-3B");
				return;
			}
			if ((oFilter.reportType.includes("Doc Date Mismatch") || oFilter.reportType.includes("Doc No & Doc Date Mismatch"))) {
				if (!oFilter.mismatchReason.length) {
					MessageBox.error("Please select Mismatch Reason");
					return;
				} else {
					payload.req.mismatchReason = oFilter.mismatchReason;
				}
			}
			payload.req.vendorGstins = aToken.map(function (e) {
				return e.getText();
			});
			this._postBulkResponse(payload, this.dBulkWith3bLock);
		},

		onBulkUNSave: function () {
			var gstins = this.byId("idBRFUNGstins").getSelectedKeys(),
				aToken = this.byId("iUnlockVenGstin").getTokens(),
				vendorPan = this.byId("iUnlockVenPan").getValue(),
				reportTypes = this.byId("idBRUNReptType").getSelectedKeys(),
				payload = {
					"req": {
						"entityId": $.sap.entityID.toString(),
						"reconCriteria": this.byId("sUnlockCriteria").getSelectedKey(),
						"reconType": this.byId("idUNReconType").getSelectedKey(),
						"gstins": gstins,
						"toTaxPeriod": this.byId("toUnlock").getValue(),
						"fromTaxPeriod": this.byId("frUnlock").getValue(),
						"reportTypes": reportTypes,
						"responseRemarks": "",
						"taxPeriodGstr3b": "",
						"taxPeriodBase": this.byId("sTaxBaseUnlock").getSelectedKey().replace('/', ''),
						"vendorPans": !vendorPan ? [] : [vendorPan.trim()],
						"indentifier": (this.getModel("Display").getProperty("/swRecon") ? "BulkUnlock" : "Unlock")
					}
				};
			if (!gstins.length) {
				MessageBox.error("Please select atleast one GSTIN");
				return;
			} else if (!reportTypes) {
				MessageBox.error("Please select Report Type");
				return;
			}
			payload.req.vendorGstins = aToken.map(function (e) {
				return e.getText();
			});
			if (!this.getModel("Display").getProperty("/swRecon")) {
				this._postBulkResponse(payload, this.dBulkRespUnlock);
			} else {
				this._submitMultiUnlockBulk(payload, this.dBulkRespUnlock);
			}
		},

		_bulkImsResponseAction: function (key, criteria, obj) {
			if (!this._bulkImsResponse) {
				this._bulkImsResponse = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.BulkImsResponse", this);
				this.getView().addDependent(this._bulkImsResponse);
				this.byId("dBuldFrImsResp").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}.bind(this)
				});
				this.byId("dBuldToImsResp").addEventDelegate({
					onAfterRendering: function (oEvent) {
						oEvent.srcControl.$().find("input").attr("readonly", true);
					}.bind(this)
				});
			}
			var aRepType = this._getBulkRespReportType(key, criteria, obj),
				vDate = new Date(),
				obj = {
					"reconCriteria": criteria,
					"reconType": this.byId("idReconTypeRR").getSelectedKey(),
					"gstins": this.byId("idRRGstins").getSelectedKeys(),
					"docType": this.byId("DocTypeIDConslM").getSelectedKeys(),
					"reportType": "",
					"imsResponseRemarks": null,
					"frDate": this.byId("dtConsldFrom").getDateValue(),
					"toDate": this.byId("dtConsld1To").getDateValue(),
					"maxDate": new Date()
				};
			this._bulkImsResponse.setModel(new JSONModel(aRepType), "FmReportType");
			this._bulkImsResponse.setModel(new JSONModel(obj), "BulkImsResp");
			this._bulkImsResponse.open();
		},

		onChangeImsResponseDate: function (oEvent) {
			var oModel = this._bulkImsResponse.getModel("BulkImsResp"),
				frDate = oEvent.getSource().getDateValue(),
				toDate = oModel.getProperty("/toDate");

			if (frDate > oModel.getProperty("/toDate")) {
				oModel.setProperty("/toDate", frDate);
			}
			oModel.refresh(true);
		},

		_formatBulkPeriod: function (date) {
			return date.getFullYear() + (date.getMonth() + 1).toString().padStart(2, 0);
		},

		onBulkImsResponse: function (sAction) {
			var obj = this._bulkImsResponse.getModel("BulkImsResp").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"reconCriteria": obj.reconCriteria,
						"reconType": obj.reconType,
						"gstins": this.removeAll(obj.gstins),
						"fromTaxPeriod": this._formatBulkPeriod(obj.frDate),
						"toTaxPeriod": this._formatBulkPeriod(obj.toDate),
						"reportTypes": [obj.reportType],
						"docType": obj.docType,
						"indentifier": "IMS",
						"imsUserResponse": sAction,
						"imsResponseRemarks": obj.imsResponseRemarks
					}
				};
			this._postBulkResponse(payload, this._bulkImsResponse);
		},

		_postBulkResponse: function (payload, oDialog) {
			oDialog.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/reqBulkResponse.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					oDialog.setBusy(false);
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
						this.onReconResults();
						oDialog.close();
					} else if (data.hdr.status === "E" && data.resp.message) {
						MessageBox.error(data.resp.message);
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					oDialog.setBusy(false);
				});
		},

		onCloseBulkImsResp: function () {
			this._bulkImsResponse.close();
		},

		saveChang: function (FinalData) {
			var that = this;
			var oConsldRTPath = "/aspsapapi/gstr2SaveActionReconResult.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oConsldRTPath,
					contentType: "application/json",
					data: JSON.stringify(FinalData)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						that.onReconResults();
						MessageBox.success("Data Saved successfully", {
							styleClass: "sapUiSizeCompact"
						});
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressClearRR: function () {
			var vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1),
				vId = (!this.getView().getModel("Display").getProperty("/swRecon") ? "idConsldReptType" : "iUnlockReportType");

			vPeriod.setDate(1);
			this.byId("sTaxPeriodBase").setSelectedKey("Both");
			this.byId("dtConsldFrom").setMaxDate(vDate);
			this.byId("dtConsldFrom").setDateValue(vPeriod);
			this.byId("dtConsld1To").setMinDate(vPeriod);
			this.byId("dtConsld1To").setMaxDate(vDate);
			this.byId("dtConsld1To").setDateValue(vDate);

			this.byId("dtDocFrom").setDateValue(null);
			this.byId("dtDocTo").setDateValue(null);
			this.byId("reconDocnumber").removeAllTokens();
			this.byId("idRRGstins").setSelectedKeys([]);
			this.byId(vId).setSelectedKeys([]);
			this.byId("DocTypeIDConslM").setSelectedKeys([]);
			this.byId("iAccVocNo").removeAllTokens();
			this.byId("iVendorPAN").setValue(null);
			this.byId("iVendorGstin").removeAllTokens();
			this.byId("dFrPeriod3b").setDateValue(null);
			this.byId("dToPeriod3b").setDateValue(null);
			this.byId("sRecResultCriteria").setSelectedKey("Regular");
			this._getReconResult();
		},

		onRRDetFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebutRR").setVisible(true);
				this.byId("openbutRR").setVisible(false);
				this.byId("idConsoleMatch").setFullScreen(true);
				this.byId("idReconRTable").setVisibleRowCount(13);
			} else {
				this.byId("closebutRR").setVisible(false);
				this.byId("openbutRR").setVisible(true);
				this.byId("idConsoleMatch").setFullScreen(false);
				this.byId("idReconRTable").setVisibleRowCount(7);
			}
		},

		onRRRSDetFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebutRRRS").setVisible(true);
				this.byId("openbutRRRS").setVisible(false);
				this.byId("RRRS").setFullScreen(true);
				this.byId("RRRSTabId").setVisibleRowCount(20);
			} else {
				this.byId("closebutRRRS").setVisible(false);
				this.byId("openbutRRRS").setVisible(true);
				this.byId("RRRS").setFullScreen(false);
				this.byId("RRRSTabId").setVisibleRowCount(13);
			}
		},

		onSelectionConsolMatch: function (oEvt) {
			this.RRData = [];
			var TablIndx = oEvt.getSource().getSelectedIndices();
			var TablData = this.getView().getModel("ConsldTablData").getData();
			var vRowIndex = oEvt.getParameters().rowIndex;
			for (var i = 0; i < TablIndx.length; i++) {
				this.RRData.push(TablData[TablIndx[i]].mismatchReason);
			}

			if (this.RRData.length === 1) {
				if (this.byId("idReconTypeRR").getSelectedKey() === "2B_PR" &&
					(this.RRData[0] === "Addition in PR" || this.RRData[0] === "Addition in PR IMPG") &&
					(this.getView().getModel("visReport").getData().optionOpted === "B")
				) {
					this.byId("With3BLock").setEnabled(false);
				} else {
					this.byId("With3BLock").setEnabled(true);
				}
			} else {
				this.byId("With3BLock").setEnabled(true);
			}
		},

		onResponse: function (oEvt) {
			var key = oEvt.getParameter("item").getKey();
			if (key === "3BResponse") {
				if (this.RRData && this.RRData.length > 1) {
					if (this.RRData.includes("Addition in PR") && this.byId("idReconTypeRR").getSelectedKey() === "2B_PR" &&
						this.getView().getModel("visReport").getData().optionOpted === "A") {
						MessageBox.error("Please UnSelect the Addition in PR ");
						return;
					}
				}
			}
			var aIndices = (!this.getModel("Display").getProperty("/swRecon") ?
				this.byId("idReconRTable").getSelectedIndices() : this.byId("tabMultiUnlock").getSelectedIndices());
			if (aIndices.length === 0) {
				MessageBox.error("Kindly select atleast one record.");
				return;
			}

			switch (key) {
			case "ForcedMatch":
				if (!this.dWo3bLock) {
					this.dWo3bLock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRRsponse", this);
					this.getView().addDependent(this.dWo3bLock);
				}
				this.byId("RMid").setValue();
				this.dWo3bLock.open();
				break;
			case "3BResponse":
				if (!this.dWith3bLock) {
					this.dWith3bLock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRBulk", this);
					this.getView().addDependent(this.dWith3bLock);
				}
				var vDate = new Date();
				this.byId("tpid").setMaxDate(vDate);
				if (aIndices.length > 1) {
					this.getView().getModel("Display").getData().RRVis = false;
					this.getView().getModel("Display").getData().ITCVis = false;
					this.byId("ATAid").setValue();
					this.byId("AIid").setValue();
					this.byId("ACid").setValue();
					this.byId("ASid").setValue();
					this.byId("ACEid").setValue();
					this.byId("ITCid").setValue(null);
				} else {
					this.getView().getModel("Display").getData().RRVis = true;

					var data = this.byId("idReconRTable").getModel("ConsldTablData").getData()[aIndices];
					if (data.itcReversal === undefined) {
						this.getView().getModel("Display").getData().ITCVis = false;
						this.byId("ITCid").setValue(null);
					} else {
						this.getView().getModel("Display").getData().ITCVis = true;
						this.byId("ITCid").setValue(data.itcReversal);
					}
					this.byId("AIid").setValue(data.avalIgst);
					this.byId("ACid").setValue(data.avalCgst);
					this.byId("ASid").setValue(data.avalSgst);
					this.byId("ACEid").setValue(data.avalCess);

					this.byId("ATAid").setValue();
				}
				this.getView().getModel("Display").refresh(true);
				this.byId("tpid").setValue();
				this.dWith3bLock.open();
				break;
			case "UNLOCK":
				MessageBox.confirm("Do you want to proceed with the response 'Unlock' for selected records?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oActionSuccess) {
						if (oActionSuccess === "YES") {
							if (!this.getModel("Display").getProperty("/swRecon")) {
								this.responseSubmit("Unlock", this.getView());
							} else {
								this._multiUnlockSubmit();
							}
						}
					}.bind(this)
				});
				break;
			case "IMSResponse":
				this._imsResponseAction();
				break;
			}
		},

		onWo3bLockClose: function () {
			this.dWo3bLock.close();
		},

		onWo3bLockSave: function () {
			this.responseSubmit("Force", this.dWo3bLock, "RMid");
		},

		onWith3bLockClose: function () {
			this.dWith3bLock.close();
		},

		onWith3bLockSave: function () {
			var aIndices = this.byId("idReconRTable").getSelectedIndices();
			if (aIndices.length === 1) {
				var data = this.byId("idReconRTable").getModel("ConsldTablData").getData()[aIndices];
				var igst = this.byId("AIid").getValue();
				var cgst = this.byId("ACid").getValue();
				var sgst = this.byId("ASid").getValue();
				var cess = this.byId("ACEid").getValue();
				// if (igst > data.avalIgst) {
				// 	MessageBox.error("IGST can not be greter that Avilable IGST");
				// 	return;
				// }

				// if (cgst > data.avalCgst) {
				// 	MessageBox.error("CGST can not be greter that Avilable CGST");
				// 	return;
				// }

				// if (sgst > data.avalSgst) {
				// 	MessageBox.error("SGST can not be greter that Avilable SGST");
				// 	return;
				// }

				// if (cess > data.avalCess) {
				// 	MessageBox.error("Cess can not be greter that Avilable Cess");
				// 	return;
				// }
			}
			this.responseSubmit("3B", this.dWith3bLock, "ATAid");
		},

		responseSubmit: function (indentifier, oDialog, id) {
			var aTabData = this.getView().getModel("ConsldTablData").getProperty("/"),
				aIndices = this.byId("idReconRTable").getSelectedIndices(),
				vItcReversal = (this.byId("ITCid") ? (this.byId("ITCid").getValue() || null) : ""),
				reconLinkIds = aIndices.map(function (e) {
					return aTabData[e].reconLinkId;
				}),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"responseRemarks": this.byId(id) === undefined ? "" : this.byId(id).getValue(),
						"reconType": this.getView().byId("idReconTypeRR").getSelectedItem().getKey(),
						"avaiIgst": (indentifier === "3B" ? this.byId("AIid").getValue() : ""),
						"avaiCgst": (indentifier === "3B" ? this.byId("ACid").getValue() : ""),
						"avaiSgst": (indentifier === "3B" ? this.byId("ASid").getValue() : ""),
						"avaiCess": (indentifier === "3B" ? this.byId("ACEid").getValue() : ""),
						"taxPeriodGstr3b": (indentifier === "3B" ? this.byId("tpid").getValue() : ""),
						"itcReversal": (indentifier === "3B" ? vItcReversal : ""),
						"reconLinkId": reconLinkIds,
						"indentifier": indentifier
					}
				}

			oDialog.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2ReconResultResponse.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					oDialog.setBusy(false);
					if (data.hdr.status === "S") {
						if (["Force", "3B"].includes(indentifier)) {
							oDialog.close();
						}
						MessageBox.success(data.resp);
						this.onReconResults();
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					oDialog.setBusy(false);
				}.bind(this));
		},

		_multiUnlockSubmit: function () {
			var aTabData = this.getView().getModel("UnlockReconResult").getProperty("/"),
				aAllData = this.getView().getModel("visReport").getProperty("/resp"),
				aIndices = this.byId("tabMultiUnlock").getSelectedIndices(),
				aReconLink = [];

			aIndices.forEach(function (idx) {
				var aData = aAllData.filter(function (item) {
					return item.batchId === aTabData[idx].batchId;
				});
				aData.forEach(function (item) {
					aReconLink.push(item.reconLinkId);
				});
			});
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"responseRemarks": "",
					"avaiIgst": !this.byId("AIid") ? "" : this.byId("AIid").getValue(),
					"avaiCgst": !this.byId("ACid") ? "" : this.byId("ACid").getValue(),
					"avaiSgst": !this.byId("ASid") ? "" : this.byId("ASid").getValue(),
					"avaiCess": !this.byId("ACEid") ? "" : this.byId("ACEid").getValue(),
					"reconType": this.getView().byId("idReconTypeRR").getSelectedItem().getKey(),
					"taxPeriodGstr3b": "",
					"reconLinkId": aReconLink,
					"indentifier": "Unlock"
				}
			};
			this._submitMultiUnlockBulk(payload);
		},

		_submitMultiUnlockBulk: function (payload, dialog) {
			if (dialog) {
				dialog.setBusy(true);
			} else {
				sap.ui.core.BusyIndicator.show(0);
			}
			$.ajax({
					"method": "POST",
					"url": "/aspsapapi/gstr2ReconResultUnlock.do",
					"data": JSON.stringify(payload),
					"contentType": "application/json",
				})
				.done(function (data, status, jqXhr) {
					if (dialog) {
						this.onBulkUnlockClose();
						dialog.setBusy(false);
					} else {
						sap.ui.core.BusyIndicator.hide();
					}
					this._getReconResult();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
					} else if (data.hdr.status === "E" && data.resp.message) {
						MessageBox.error(data.resp.message);
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (err) {
					if (dialog) {
						dialog.setBusy(false);
					} else {
						sap.ui.core.BusyIndicator.hide();
					}
				}.bind(this));
		},

		_imsResponseAction: function () {
			if (!this._imsResponse) {
				this._imsResponse = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ImsResponseAction", this);
				this.getView().addDependent(this._imsResponse);
			}
			this._imsResponse.setModel(new JSONModel({
				"imsResponseRemarks": null
			}), "ImsResponse");
			this._imsResponse.open();
		},

		onImsResponse: function (sAction) {
			var aTabData = this.getView().getModel("ConsldTablData").getProperty("/"),
				aIndices = this.byId("idReconRTable").getSelectedIndices(),
				reconLinkIds = aIndices.map(function (idx) {
					return aTabData[idx].reconLinkId;
				}),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"reconType": this.getView().byId("idReconTypeRR").getSelectedItem().getKey(),
						"reconLinkId": reconLinkIds,
						"indentifier": "IMS",
						"imsUserResponse": sAction,
						"imsResponseRemarks": this._imsResponse.getModel("ImsResponse").getProperty("/imsResponseRemarks")
					}
				}
			this._imsResponse.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2ReconResultResponse.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this._imsResponse.setBusy(false);
					if (data.hdr.status === "S") {
						this._imsResponse.close();
						MessageBox.success(data.resp);
						this.byId("idReconRTable").clearSelection();
						this._getReconResult();
					} else {
						MessageBox.error(data.resp);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._imsResponse.setBusy(false);
				}.bind(this));
		},

		onCloseImsResp: function () {
			this._imsResponse.close();
		},

		onPageNumberRR: function (oEvt) {
			var vId = (!this.getView().getModel("Display").getProperty("/swRecon") ? "idConsldReptType" : "iUnlockReportType"),
				oIntiData = {
					"hdr": {
						"pageNum": 0,
						"pageSize": Number(oEvt.getSource().getSelectedKey())
					},
					"req": {
						"entityId": $.sap.entityID.toString(),
						"reconType": this.getView().byId("idReconTypeRR").getSelectedItem().getKey(),
						"gstins": this.removeAll(this.getView().byId("idRRGstins").getSelectedKeys()),
						"taxPeriodBase": this.byId("sTaxPeriodBase").getSelectedKey().replace('/', ''),
						"toTaxPeriod": this.byId("dtConsld1To").getValue(),
						"fromTaxPeriod": this.byId("dtConsldFrom").getValue(),
						"reportType": this.removeAll(this.getView().byId(vId).getSelectedKeys()),
						"fromDocDate": this.byId("dtDocFrom").getValue(),
						"toDocDate": this.byId("dtDocTo").getValue(),
						"docNumber": this.byId("reconDocnumber").getValue(),
						"docType": this.removeAll(this.getView().byId("DocTypeIDConslM").getSelectedKeys()),
						"identifier": (!this.getView().getModel("Display").getProperty("/swRecon") ? "SINGLE" : "MULTI")
					}
				};

			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResult.do",
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						return;
					}
					var oIniModel = new JSONModel(data.resp);
					oIniModel.setSizeLimit(data.resp.length);
					this.getView().setModel(oIniModel, "ConsldTablData");
					this.getView().setModel(new JSONModel(data), "visReport");
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onSubmitPagination: function () {
			//eslint-disable-line
			var vPageNo = this.byId("inPageNoN").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTab1(vPageNo);
		},

		onDownloadReconResult: function () {
			var flag = this.getView().getModel("Display").getProperty("/swRecon"),
				vId = (!flag ? "idConsldReptType" : "iUnlockReportType"),
				vTab = (!flag ? "idReconRTable" : "tabMultiUnlock"),
				aIndex = this.byId(vTab).getSelectedIndices(),
				aDocToken = this.byId("reconDocnumber").getTokens(),
				accVocNo = this.byId("iAccVocNo").getTokens(),
				vendorPan = this.byId("iVendorPAN").getValue(),
				vendorGstin = this.byId("iVendorGstin").getTokens(),
				payload = {
					"hdr": {
						"pageNum": 0,
						"pageSize": 10
					},
					"req": {
						"entityId": $.sap.entityID.toString(),
						"reconType": this.byId("idReconTypeRR").getSelectedKey(),
						"gstins": this.removeAll(this.getView().byId("idRRGstins").getSelectedKeys()),
						"taxPeriodBase": this.byId("sTaxPeriodBase").getSelectedKey().replace('/', ''),
						"toTaxPeriod": this.byId("dtConsld1To").getValue(),
						"fromTaxPeriod": this.byId("dtConsldFrom").getValue(),
						"reconCriteria": this.byId("sRecResultCriteria").getSelectedKey(),
						"reportType": this.removeAll(this.getView().byId(vId).getSelectedKeys()),
						"fromDocDate": this.byId("dtDocFrom").getValue(),
						"toDocDate": this.byId("dtDocTo").getValue(),
						"docType": this.removeAll(this.getView().byId("DocTypeIDConslM").getSelectedKeys()),
						"docNumberList": [],
						"accVoucherNums": [],
						"vndrPans": !vendorPan ? [] : [vendorPan.trim()],
						"vndrGstins": vendorGstin.map(function (e) {
							return e.getKey();
						}),
						"frmTaxPrd3b": this.byId("dFrPeriod3b").getValue(),
						"toTaxPrd3b": this.byId("dToPeriod3b").getValue()
					}
				};
			if (aIndex.length) {
				var oData = this.getView().getModel(!flag ? "ConsldTablData" : "UnlockReconResult").getProperty("/");
				aIndex.forEach(function (e) {
					payload.req.docNumberList.push(oData[e].docNumberPR);
					if (oData[e].accVoucherNo) {
						payload.req.accVoucherNums.push(oData[e].accVoucherNo);
					}
				});
			} else {
				payload.req.docNumberList = aDocToken.map(function (e) {
					return e.getKey();
				});
				payload.req.accVoucherNums = accVocNo.map(function (e) {
					return e.getKey();
				});
			}
			this.reportDownload(payload, "/aspsapapi/gstr2ReconResultReportDownload.do");
		},

		//////////////////////////End/////////////////////////////////////
		////////////////////Recon Result Screen Vinay Kodam////////////////

		//////////////////////////Start/////////////////////////////////////
		////////////////////Permissible ITC Screen  Vinay Kodam////////////////

		onRecontypeChange1: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			if (key === "2A_PR") {
				this.byId("labID").setText("Tax Amount as per GSTR 2A");
				this.byId("labId1").setText("Tax Amount as per GSTR 2A");
			} else {
				this.byId("labID").setText("Tax Amount as per GSTR 2B");
				this.byId("labId1").setText("Tax Amount as per GSTR 2B");
			}
		},

		ItcPermGo: function () {
			var key = this.byId("segId").getSelectedKey();
			if (key === "Summary") {
				this.summaryPermissible();
			} else {
				this.DetailPermissible();
			}
		},

		onChangeSegmentPerITC: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			if (key === "Summary") {
				this.byId("TabId").setVisible(true);
				this.byId("Tab2Id").setVisible(false);
				this.summaryPermissible();
			} else {
				this.byId("TabId").setVisible(false);
				this.byId("Tab2Id").setVisible(true);
				this.DetailPermissible();
			}
		},

		handleChangePISumm: function (oevt) {
			var toDate = this.byId("dtPITo").getDateValue(),
				fromDate = this.byId("dtPI").getDateValue();
			// var hardVal = "122021";
			// var	fromDate = this.byId("dtPI").setMaxDate(hardVal);
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");

				this.byId("dtPITo").setDateValue(oevt.getSource().getDateValue());
				this.byId("dtPITo").setMinDate(oevt.getSource().getDateValue());

				// this.byId("dtPITo").setDateValue("122021");
				// this.byId("dtPITo").setMinDate("122021");

				/*	this.byId("dtPIDetTo").setDateValue(oevt.getSource().getDateValue());
					this.byId("dtPIDetTo").setMinDate(oevt.getSource().getDateValue());*/
			} else {

				this.byId("dtPITo").setMinDate(oevt.getSource().getDateValue());
				// this.byId("dtPITo").setMinDate("122021");
				//this.byId("dtPIDetTo").setMinDate(oevt.getSource().getDateValue());
			}
		},

		summaryPermissible: function () {
			var gstin = [];
			var aGstin = this.byId("idPIGstins").getSelectedKeys();
			var allGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
			if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				for (var i = 0; i < allGstin.length; i++) {
					gstin.push(allGstin[i].value);
				}
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			var Request = {
				"req": {
					"gstinList": aGstin.length === 0 ? gstin : aGstin,
					"entityId": $.sap.entityID.toString(),
					"fromTaxPeriod": this.byId("dtPI").getValue(),
					"toTaxPeriod": this.byId("dtPITo").getValue(),
					"docType": this.byId("DocTypeIDPI").getSelectedKeys(),
					// "reconType": this.byId("idReconTypePI").getSelectedKey()
					"reconType": "2A_PR"
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var that = this;
			var oIniPath = "/aspsapapi/getPermissibleITC10PercentSummary.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oIniPath,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					that.onLoadPer();
					Model.setData(data.resp);
					View.setModel(Model, "summaryPermissible");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		DetailPermissible: function () {
			var gstin = [];
			var aGstin = this.byId("idPIGstins").getSelectedKeys();
			var allGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
			if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				for (var i = 0; i < allGstin.length; i++) {
					gstin.push(allGstin[i].value);
				}
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			var Request = {
				"req": {
					"gstinList": aGstin.length === 0 ? gstin : aGstin,
					"entityId": $.sap.entityID.toString(),
					"fromTaxPeriod": this.byId("dtPI").getValue(),
					"toTaxPeriod": this.byId("dtPITo").getValue(),
					"docType": this.byId("DocTypeIDPI").getSelectedKeys(),
					"reconType": "2A_PR"
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			var that = this;
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getPermissibleITC10PercentDetails.do",
				contentType: "application/json",
				data: JSON.stringify(Request)
			}).done(function (data, status, jqXHR) {
				that.onLoadPer();
				Model.setData(data.resp.itcTenPercGstinDetails);
				View.setModel(Model, "DetailPermissible");
			}).fail(function (jqXHR, status, err) {});
		},

		onLoadPer: function () {
			var that = this;
			var Gstr2APath = "/aspsapapi/getEligibleCreditPercentage.do?entityId=" + $.sap.entityID;
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: Gstr2APath,
					contentType: "application/json",
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.getView().setModel(new JSONModel(data.resp), "onLoadPer");
						that.getView().getModel("onLoadPer").refresh(true);
					} else {
						that.getView().setModel(new JSONModel([]), "onLoadPer");
						that.getView().getModel("onLoadPer").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {});
				sap.ui.core.BusyIndicator.hide();
				that.getView().setModel(new JSONModel([]), "onLoadPer");
				that.getView().getModel("onLoadPer").refresh(true);
			});
		},

		onMenuItemPressSavePI: function (oEvt) {
			var gstin = [];
			var aGstin = this.byId("idPIGstins").getSelectedKeys();
			var allGstin = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin;
			if (aGstin.length === 0) {
				if (allGstin[0].value === "All") {
					allGstin.shift();
				}
				for (var i = 0; i < allGstin.length; i++) {
					gstin.push(allGstin[i].value);
				}
			} else {
				if (aGstin[0] === "All") {
					aGstin.shift();
				}
			}
			var key = oEvt.getParameter("item").getKey();
			if (key === "Download Summary level") {
				var url = "/aspsapapi/permissibleITC10pReport.do";
				var Request = {
					"req": {
						"entityId": $.sap.entityID.toString(),
						"gstinList": aGstin.length === 0 ? gstin : aGstin,
						"fromTaxPeriod": this.byId("dtPI").getValue(),
						"toTaxPeriod": this.byId("dtPITo").getValue(),
						"docType": this.byId("DocTypeIDPI").getSelectedKeys(),
						// "reconType": this.byId("idReconTypePI").getSelectedKey()
						"reconType": "2A_PR"
					}
				};
				this.excelDownload(Request, url);
			}
		},

		onPressClearPI: function () {
			var vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			vPeriod.setDate(1);
			this.byId("dtPI").setMaxDate(vDate);
			this.byId("dtPI").setDateValue(vPeriod);

			this.byId("dtPITo").setMinDate(vPeriod);
			this.byId("dtPITo").setMaxDate(vDate);
			this.byId("dtPITo").setDateValue(vDate);

			this.byId("DocTypeIDPI").setSelectedKeys([]);
			this.summaryPermissible();
			this.DetailPermissible();
		},

		onPressClearPIDetail: function () {
			var vDate = new Date(),
				date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			vPeriod.setDate(1);
			this.byId("dtPIDet").setMaxDate(vDate);
			this.byId("dtPIDet").setDateValue(vPeriod);

			this.byId("dtPIDetTo").setMinDate(vPeriod);
			this.byId("dtPIDetTo").setMaxDate(vDate);
			this.byId("dtPIDetTo").setDateValue(vDate);

			this.byId("idPIGstinsDet").setSelectedKeys([]);
			this.byId("DocTypeIDPIDet").setSelectedKeys([]);
			this.DetailPermissible();
		},

		onPITCFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebutPITC").setVisible(true);
				this.byId("openbutPITC").setVisible(false);
				this.byId("TabId").setFullScreen(true);
				this.byId("sumTab").setVisibleRowCount(22);
			} else {
				this.byId("closebutPITC").setVisible(false);
				this.byId("openbutPITC").setVisible(true);
				this.byId("TabId").setFullScreen(false);
				this.byId("sumTab").setVisibleRowCount(9);
			}
		},

		onPITCDFullScreen: function (oEvt) {
			if (oEvt === "open") {
				this.byId("closebutPITCD").setVisible(true);
				this.byId("openbutPITCD").setVisible(false);
				this.byId("Tab2Id").setFullScreen(true);
				this.byId("detTab").setVisibleRowCount(22);
			} else {
				this.byId("closebutPITCD").setVisible(false);
				this.byId("openbutPITCD").setVisible(true);
				this.byId("Tab2Id").setFullScreen(false);
				this.byId("detTab").setVisibleRowCount(9);
			}
		},

		onExpandPI: function (oEvent) {
			if (oEvent.getSource().getId().includes("expandPI")) {
				this.byId("detTab").expandToLevel(1);
			} else {
				this.byId("detTab").collapseAll();
			}
		},

		//////////////////////////End/////////////////////////////////////
		////////////////////Permissible ITC Screen  Vinay Kodam////////////////

		// code added by Sarvmangla for Get2A Popup
		onChangeSegmentProcessStatus: function (oEvent) {
			var key = oEvent.getSource().getSelectedKey();
			this.byId("idgettitleGstr2").setVisible(key === "LCS");
			this.byId("idgetVtablegstr6progstr2").setVisible(key === "LCS");
			this.byId("idGetSucessTitleGstr2").setVisible(key === "LSS");
			this.byId("idgetStatusgstr2").setVisible(key === "LSS");
		},

		onPressGetGstr2Btn: function (oEvent) {
			//eslint-disable-line
			var oBtProcess = this.byId("idProcessStatusBtnGstr2").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = this.byId("idgetVtablegstr6progstr2").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetVtablegstr6progstr2").getModel("Gstr2Get2ASucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {

					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastCall[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr2aSections": []

					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr2aSections.push("b2b");

					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr2aSections.push("b2ba");

					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr2aSections.push("cdn");

					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr2aSections.push("cdna");

					}
					if (oModelForTab1.lastCall[i].isdFlag) {
						postData.req[i].gstr2aSections.push("isd");

					}
					if (oModelForTab1.lastCall[i].isdaFlag) {
						postData.req[i].gstr2aSections.push("isda");

					}
					if (oModelForTab1.lastCall[i].impgFlag) {
						postData.req[i].gstr2aSections.push("impg");

					}
					if (oModelForTab1.lastCall[i].impgsezFlag) {
						postData.req[i].gstr2aSections.push("impgsez");

					}
					if (oModelForTab1.lastCall[i].ecomFlag) {
						postData.req[i].gstr2aSections.push("ecom");

					}
					if (oModelForTab1.lastCall[i].ecomaFlag) {
						postData.req[i].gstr2aSections.push("ecoma");

					}
					if (oModelForTab1.lastCall[i].amendAttriFlag) {
						postData.req[i].gstr2aSections.push("amdhist");

					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr2aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					MessageBox.warning("Select at least one Section");
					return;

				}
				this.getSectionGstr2AButtonIntrigrationFinal(postData);

			} else {
				var oSelectedItem = this.byId("idgetStatusgstr2").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetStatusgstr2").getModel("Gstr2Get2ASucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {

					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						"ret_period": oTaxPeriod,
						"gstr2aSections": []

					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr2aSections.push("b2b");

					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr2aSections.push("b2ba");

					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr2aSections.push("cdn");

					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr2aSections.push("cdna");

					}
					if (oModelForTab1.lastSuccess[i].isdFlag) {
						postData.req[i].gstr2aSections.push("isd");

					}
					if (oModelForTab1.lastSuccess[i].isdaFlag) {
						postData.req[i].gstr2aSections.push("isda");

					}
					if (oModelForTab1.lastSuccess[i].impgFlag) {
						postData.req[i].gstr2aSections.push("impg");

					}
					if (oModelForTab1.lastSuccess[i].impgsezFlag) {
						postData.req[i].gstr2aSections.push("impgsez");

					}
					if (oModelForTab1.lastSuccess[i].ecomFlag) {
						postData.req[i].gstr2aSections.push("ecom");

					}
					if (oModelForTab1.lastSuccess[i].ecomaFlag) {
						postData.req[i].gstr2aSections.push("ecoma");

					}
					if (oModelForTab1.lastSuccess[i].amendAttriFlag) {
						postData.req[i].gstr2aSections.push("amdhist");

					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr2aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					MessageBox.warning("Select at least one Section");
					return;

				}

				this.getSectionGstr2AButtonIntrigrationFinal(postData);

			}

		},
		getSectionGstr2AButtonIntrigrationFinal: function (postData) {
			//eslint-disable-line
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr2aGstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {

							aMockMessages.push({
								type: "Success",
								title: data.resp[i].gstin,
								gstin: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}
					}

					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr1GstnGetSection : Error");
				});
			});
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get GSTR-2A Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.onPressGetGstr2a("", that.vPSFlag);
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}

			this.pressDialog.open();
		},

		onPressProcessSumBtnGstr2: function (oEvent) {
			var oBtProcess = this.byId("idProcessStatusBtnGstr2").getSelectedKey();
			if (oBtProcess === "LCS") {
				var oSelectedItem = this.byId("idgetVtablegstr6progstr2").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetVtablegstr6progstr2").getModel("Gstr2Get2ASucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {

					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastCall.length; i++) {
					postData.req.push({
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastCall[i].gstin,
						taxPeriod: oTaxPeriod,
						gstr2aSections: []

					});
					if (oModelForTab1.lastCall[i].b2bFlag) {
						postData.req[i].gstr2aSections.push("B2B");

					}
					if (oModelForTab1.lastCall[i].b2baFlag) {
						postData.req[i].gstr2aSections.push("B2BA");

					}
					if (oModelForTab1.lastCall[i].cdnFlag) {
						postData.req[i].gstr2aSections.push("CDN");

					}
					if (oModelForTab1.lastCall[i].cdnaFlag) {
						postData.req[i].gstr2aSections.push("CDNA");

					}
					if (oModelForTab1.lastCall[i].isdFlag) {
						postData.req[i].gstr2aSections.push("ISD");

					}
					if (oModelForTab1.lastCall[i].isdaFlag) {
						postData.req[i].gstr2aSections.push("ISDA");

					}
					if (oModelForTab1.lastCall[i].impgFlag) {
						postData.req[i].gstr2aSections.push("IMPG");

					}
					if (oModelForTab1.lastCall[i].impgsezFlag) {
						postData.req[i].gstr2aSections.push("IMPGSEZ");
					}
					if (oModelForTab1.lastCall[i].ecomFlag) {
						postData.req[i].gstr2aSections.push("ECOM");
					}
					if (oModelForTab1.lastCall[i].ecomaFlag) {
						postData.req[i].gstr2aSections.push("ECOMA");
					}
					if (oModelForTab1.lastCall[i].amendAttriFlag) {
						postData.req[i].gstr2aSections.push("IMPG_SEZ_AMD");

					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr2aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					MessageBox.warning("Select at least one Section");
					return;

				}
				var url = "/aspsapapi/gstr2aGetDownloads.do";
				// this.excelDownload(postData, url);
				this.reportDownload(postData, url);

			} else {
				var oSelectedItem = this.byId("idgetStatusgstr2").getSelectedIndices();
				var oModelForTab1 = this.byId("idgetStatusgstr2").getModel("Gstr2Get2ASucess").getData().resp;
				// if (oSelectedItem.length == 0) {
				// 	MessageBox.warning("Select at least one record");
				// 	return;
				// }
				var oTaxPeriod = this.byId("id_TaxProcessGstr2").getValue();
				if (oTaxPeriod === "") {
					oTaxPeriod = null;
				}
				var postData = {

					"req": []
				};

				for (var i = 0; i < oModelForTab1.lastSuccess.length; i++) {
					postData.req.push({
						"entityId": $.sap.entityID,
						"gstin": oModelForTab1.lastSuccess[i].gstin,
						taxPeriod: oTaxPeriod,
						gstr2aSections: []

					});
					if (oModelForTab1.lastSuccess[i].b2bFlag) {
						postData.req[i].gstr2aSections.push("B2B");

					}
					if (oModelForTab1.lastSuccess[i].b2baFlag) {
						postData.req[i].gstr2aSections.push("B2BA");

					}
					if (oModelForTab1.lastSuccess[i].cdnFlag) {
						postData.req[i].gstr2aSections.push("CDN");

					}
					if (oModelForTab1.lastSuccess[i].cdnaFlag) {
						postData.req[i].gstr2aSections.push("CDNA");

					}
					if (oModelForTab1.lastSuccess[i].isdFlag) {
						postData.req[i].gstr2aSections.push("ISD");

					}
					if (oModelForTab1.lastSuccess[i].isdaFlag) {
						postData.req[i].gstr2aSections.push("ISDA");

					}
					if (oModelForTab1.lastSuccess[i].impgFlag) {
						postData.req[i].gstr2aSections.push("IMPG");

					}
					if (oModelForTab1.lastSuccess[i].impgsezFlag) {
						postData.req[i].gstr2aSections.push("IMPGSEZ");

					}
					if (oModelForTab1.lastSuccess[i].ecomFlag) {
						postData.req[i].gstr2aSections.push("ECOM");

					}
					if (oModelForTab1.lastSuccess[i].ecomaFlag) {
						postData.req[i].gstr2aSections.push("ECOMA");

					}
					if (oModelForTab1.lastSuccess[i].amendAttriFlag) {
						postData.req[i].gstr2aSections.push("IMPG_SEZ_AMD");

					}

				}
				for (var i = postData.req.length - 1; i >= 0; i--) {
					if (postData.req[i].gstr2aSections.length === 0) {
						postData.req.splice(i, 1);
					}
				}
				if (postData.req.length === 0) {
					MessageBox.warning("Select at least one Section");
					return;

				}
				var url = "/aspsapapi/gstr2aGetDownloads.do";
				// this.excelDownload(postData, url);
				this.reportDownload(postData, url);

			}
		},
		onSelectAllCheckHeader: function (oEvent, field) {
			//eslint-disable-line
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.getView().getModel("Gstr2Get2ASucess").getData();
			var oBtProcess = this.byId("idProcessStatusBtnGstr2").getSelectedKey();
			// field = JSON.parse(field);
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastCall[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastCall[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastCall[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
						break;
					case "isdFlag":
						data.resp.lastCall[i].isdFlag = oSelectedFlag;
						break;
					case "isdaFlag":
						data.resp.lastCall[i].isdaFlag = oSelectedFlag;
						break;
					case "impgFlag":
						data.resp.lastCall[i].impgFlag = oSelectedFlag;
						break;
					case "impgsezFlag":
						data.resp.lastCall[i].impgsezFlag = oSelectedFlag;
						break;
					case "ecomFlag":
						data.resp.lastCall[i].ecomFlag = oSelectedFlag;
						break;
					case "ecomaFlag":
						data.resp.lastCall[i].ecomaFlag = oSelectedFlag;
						break;
					case "amendAttriFlag":
						data.resp.lastCall[i].amendAttriFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					switch (field) {
					case "b2bFlag":
						data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
						break;
					case "b2baFlag":
						data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
						break;
					case "cdnFlag":
						data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
						break;
					case "cdnaFlag":
						data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
						break;
					case "isdFlag":
						data.resp.lastSuccess[i].isdFlag = oSelectedFlag;
						break;
					case "isdaFlag":
						data.resp.lastSuccess[i].isdaFlag = oSelectedFlag;
						break;
					case "impgFlag":
						data.resp.lastSuccess[i].impgFlag = oSelectedFlag;
						break;
					case "impgsezFlag":
						data.resp.lastSuccess[i].impgsezFlag = oSelectedFlag;
						break;
					case "ecomFlag":
						data.resp.lastSuccess[i].ecomFlag = oSelectedFlag;
						break;
					case "ecomaFlag":
						data.resp.lastSuccess[i].ecomaFlag = oSelectedFlag;
						break;
					case "amendAttriFlag":
						data.resp.lastSuccess[i].amendAttriFlag = oSelectedFlag;
						break;
					default:
						// code block
					}
				}
			}
			this.getView().getModel("Gstr2Get2ASucess").refresh();
		},

		onRowSelectionChange2A: function (oEvent) {
			var vSelectAll = !!oEvent.getParameter("selectAll"),
				vRowIndex = oEvent.getParameter("rowIndex");

			if (vSelectAll || vRowIndex === -1) {
				var object = {
					"aprFlag": vSelectAll,
					"mayFlag": vSelectAll,
					"junFlag": vSelectAll,
					"julFlag": vSelectAll,
					"augFlag": vSelectAll,
					"sepFlag": vSelectAll,
					"octFlag": vSelectAll,
					"novFlag": vSelectAll,
					"decFlag": vSelectAll,
					"janFlag": vSelectAll,
					"febFlag": vSelectAll,
					"marFlag": vSelectAll
				};
				this.getView().setModel(new JSONModel(object), "month");
			}
		},

		onRowSelectionChange: function (oEvent) {
			var data = this.getView().getModel("Gstr2Get2ASucess").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			var oBtProcess = this.byId("idProcessStatusBtnGstr2").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlag(true);
				this._showing(true);
			} else if (vRowIndex === -1) {
				this._setFlag(false);
				this._showing(false);
			} else {

				if (oBtProcess === "LCS") {
					if (data.resp.lastCall[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}

					data.resp.lastCall[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].isdFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].isdaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].impgFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].impgsezFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].ecomFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].ecomaFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].amendAttriFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				} else {
					if (data.resp.lastSuccess[vRowIndex].lsFlag) {
						var oSelectedFlag = false;
					} else {
						var oSelectedFlag = true;
					}
					data.resp.lastSuccess[vRowIndex].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].isdFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].isdaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].impgFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].impgsezFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].ecomFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].ecomaFlag = oSelectedFlag;
					data.resp.lastSuccess[vRowIndex].amendAttriFlag = oSelectedFlag;
					data.resp.lastCall[vRowIndex].lsFlag = oSelectedFlag;
				}
			}

			this.getView().getModel("Gstr2Get2ASucess").refresh();
		},
		_setFlag: function (oSelectedFlag) {
			var data = this.getView().getModel("Gstr2Get2ASucess").getData();
			var oBtProcess = this.byId("idProcessStatusBtnGstr2").getSelectedKey();
			if (oBtProcess === "LCS") {
				for (var i = 0; i < data.resp.lastCall.length; i++) {
					data.resp.lastCall[i].b2bFlag = oSelectedFlag;
					data.resp.lastCall[i].b2baFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnFlag = oSelectedFlag;
					data.resp.lastCall[i].cdnaFlag = oSelectedFlag;
					data.resp.lastCall[i].isdFlag = oSelectedFlag;
					data.resp.lastCall[i].isdaFlag = oSelectedFlag;
					data.resp.lastCall[i].impgFlag = oSelectedFlag;
					data.resp.lastCall[i].impgsezFlag = oSelectedFlag;
					data.resp.lastCall[i].ecomFlag = oSelectedFlag;
					data.resp.lastCall[i].ecomaFlag = oSelectedFlag;
					data.resp.lastCall[i].amendAttriFlag = oSelectedFlag;
					data.resp.lastCall[i].lsFlag = oSelectedFlag;
				}

			} else {
				for (var i = 0; i < data.resp.lastSuccess.length; i++) {
					data.resp.lastSuccess[i].b2bFlag = oSelectedFlag;
					data.resp.lastSuccess[i].b2baFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnFlag = oSelectedFlag;
					data.resp.lastSuccess[i].cdnaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].isdFlag = oSelectedFlag;
					data.resp.lastSuccess[i].isdaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].impgFlag = oSelectedFlag;
					data.resp.lastSuccess[i].impgsezFlag = oSelectedFlag;
					data.resp.lastSuccess[i].ecomFlag = oSelectedFlag;
					data.resp.lastSuccess[i].ecomaFlag = oSelectedFlag;
					data.resp.lastSuccess[i].amendAttriFlag = oSelectedFlag;
					data.resp.lastSuccess[i].lsFlag = oSelectedFlag;

				}
			}
		},

		_showing: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.b2baFlag = oSelectedFlag;
			oData.cdnFlag = oSelectedFlag;
			oData.cdnaFlag = oSelectedFlag;
			oData.isdFlag = oSelectedFlag;
			oData.isdaFlag = oSelectedFlag;
			oData.impgFlag = oSelectedFlag;
			oData.impgsezFlag = oSelectedFlag;
			oData.ecomFlag = oSelectedFlag;
			oData.ecomaFlag = oSelectedFlag;
			oData.amendAttriFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},

		onRowSelectionChangeget2A: function (oEvent) {
			//eslint-disable-line
			// var that = this.getView();
			var data = this.byId("dGet2aStatus").getModel("Get2aStauts").getData();
			var vRowIndex = oEvent.getParameters().rowIndex;
			// var oBtProcess = sap.ui.getCore().byId("idProcessStatusGet2ABtn").getSelectedKey();
			var vSelectAll = oEvent.getParameters().selectAll;

			if (vSelectAll) {
				this._setFlagget2A(true);
				this._showing2A(true);
			} else if (vRowIndex === -1) {
				this._setFlagget2A(false);
				this._showing2A(false);
			} else {

				// if (oBtProcess === "all") {
				if (data[vRowIndex].lsFlag) {
					var oSelectedFlag = false;
				} else {
					var oSelectedFlag = true;
				}

				data[vRowIndex].b2bFlag = oSelectedFlag;
				data[vRowIndex].b2baFlag = oSelectedFlag;
				data[vRowIndex].cdnFlag = oSelectedFlag;
				data[vRowIndex].cdnaFlag = oSelectedFlag;
				data[vRowIndex].isdFlag = oSelectedFlag;
				data[vRowIndex].isdaFlag = oSelectedFlag;
				data[vRowIndex].impgFlag = oSelectedFlag;
				data[vRowIndex].impgsezFlag = oSelectedFlag;
				data[vRowIndex].ecomFlag = oSelectedFlag;
				data[vRowIndex].ecomaFlag = oSelectedFlag;
				data[vRowIndex].importFlag = oSelectedFlag;
				data[vRowIndex].lsFlag = oSelectedFlag;
			}

			this.byId("dGet2aStatus").getModel("Get2aStauts").refresh();
		},
		_setFlagget2A: function (oSelectedFlag) {
			var data = this.byId("dGet2aStatus").getModel("Get2aStauts").getData();
			for (var i = 0; i < data.length; i++) {
				data[i].b2bFlag = oSelectedFlag;
				data[i].b2baFlag = oSelectedFlag;
				data[i].cdnFlag = oSelectedFlag;
				data[i].cdnaFlag = oSelectedFlag;
				data[i].isdFlag = oSelectedFlag;
				data[i].isdaFlag = oSelectedFlag;
				data[i].impgFlag = oSelectedFlag;
				data[i].impgsezFlag = oSelectedFlag;
				data[i].ecomFlag = oSelectedFlag;
				data[i].ecomaFlag = oSelectedFlag;
				data[i].importFlag = oSelectedFlag;
				data[i].lsFlag = oSelectedFlag;
			}

		},
		_showing2A: function (oSelectedFlag) {
			var oData = this.getView().getModel("showing").getData();
			oData.b2bFlag = oSelectedFlag;
			oData.b2baFlag = oSelectedFlag;
			oData.cdnFlag = oSelectedFlag;
			oData.cdnaFlag = oSelectedFlag;
			oData.isdFlag = oSelectedFlag;
			oData.isdaFlag = oSelectedFlag;
			oData.impgFlag = oSelectedFlag;
			oData.impgsezFlag = oSelectedFlag;
			oData.ecomFlag = oSelectedFlag;
			oData.ecomaFlag = oSelectedFlag;
			oData.importFlag = oSelectedFlag;
			oData.check = oSelectedFlag;
			this.getView().getModel("showing").refresh();
		},
		onCheckGet2aSection: function (oEvent, field) {
			//eslint-disable-line
			var oSelectedFlag = oEvent.getSource().getSelected();
			var data = this.byId("dGet2aStatus").getModel("Get2aStauts").getData();
			for (var i = 0; i < data.length; i++) {
				switch (field) {
				case "b2bFlag":
					data[i].b2bFlag = oSelectedFlag;
					break;
				case "b2baFlag":
					data[i].b2baFlag = oSelectedFlag;
					break;
				case "cdnFlag":
					data[i].cdnFlag = oSelectedFlag;
					break;
				case "cdnaFlag":
					data[i].cdnaFlag = oSelectedFlag;
					break;
				case "isdFlag":
					data[i].isdFlag = oSelectedFlag;
					break;
				case "isdaFlag":
					data[i].isdaFlag = oSelectedFlag;
					break;
				case "impgFlag":
					data[i].impgFlag = oSelectedFlag;
					break;
				case "impgsezFlag":
					data[i].impgsezFlag = oSelectedFlag;
					break;
				case "ecomFlag":
					data[i].ecomFlag = oSelectedFlag;
					break;
				case "ecomaFlag":
					data[i].ecomaFlag = oSelectedFlag;
					break;
				case "importFlag":
					data[i].importFlag = oSelectedFlag;
					break;
				default:
					// code block
				}
			}
			// }
			this.byId("dGet2aStatus").getModel("Get2aStauts").refresh();
		},

		onSelChngVComFileStatus1: function (oEvent) {
			var that = this;
			var vId = oEvent.getSource().getSelectedKey();
			if (vId === "FileUpload") {
				this.oView.byId("idVenUpldTabl").setVisible(true);
				this.oView.byId("idVenUpldTabl2").setVisible(false);
				this.onVenUpldRefresh();
			} else if (vId === "ApiPush") {
				this.oView.byId("idVenUpldTabl").setVisible(false);
				this.oView.byId("idVenUpldTabl2").setVisible(true);
				this.onVenApiPush();
			}
		},

		//############ BOC of Vendor Communication by Rakesh on 30.09.2020
		onSelChngVComFileStatus: function (oEvent) {
			var that = this;
			//this.byId("idEmailRecptGstn").setSelectedKeys([]);
			var vId = oEvent.getSource().getSelectedKey();
			if (vId === "VendorFileStatus") {
				//this.oView.byId("idFileStatusChartCon").setVisible(true);
				this.oView.byId("idVenMstrDwnldChartCon").setVisible(false);
				this.oView.byId("idEmailChartCon").setVisible(false);
				//Tables 
				this.oView.byId("idVenUpldTabl").setVisible(true);
				this.oView.byId("idMasterVendor").setVisible(false);
				this.oView.byId("toolId").setVisible(true);
				//Filter Bar

				this.oView.byId("idheaderVendor").setVisible(false);
				this.oView.byId("idFltrBarVendrResp").setVisible(false);
				this.oView.byId("idVR").setVisible(false);
				this.oView.byId("fbGenReptHboxR").setVisible(false);
				//this.onVenUpldRefresh();
				var vId = this.byId("idsbFileStatus11").getSelectedKey();
				if (vId === "FileUpload") {
					this.oView.byId("idVenUpldTabl").setVisible(true);
					this.oView.byId("idVenUpldTabl2").setVisible(false);
					this.onVenUpldRefresh();
				} else if (vId === "ApiPush") {
					this.oView.byId("idVenUpldTabl").setVisible(false);
					this.oView.byId("idVenUpldTabl2").setVisible(true);
					this.onVenApiPush();
				}
				//this.oView.byId("idFltrBarVendrUpld").setVisible(true);
				//this.oView.byId("idFltrBarVendrMstr").setVisible(false);
				//this.oView.byId("idFltrBarVendrEmail").setVisible(false);
				//this.oView.byId("fbVenMstrHbox").setVisible(false);
				//this.oView.byId("fbGenReptHbox").setVisible(false);

				//this.oView.byId("UploadCollection").setVisible(true);
			} else if (vId === "VendorMaster") {
				//this.oView.byId("idFileStatusChartCon").setVisible(false);
				this.oView.byId("idVenMstrDwnldChartCon").setVisible(true);
				this.oView.byId("idEmailChartCon").setVisible(false);
				//Filter Bar
				//this.oView.byId("idFltrBarVendrUpld").setVisible(false);
				this.oView.byId("idFltrBarVendrEmail").setVisible(false);
				this.oView.byId("idFltrBarVendrMstr").setVisible(true);
				this.oView.byId("fbVenMstrHbox").setVisible(true);
				this.oView.byId("fbGenReptHbox").setVisible(false);
				this.oView.byId("idheaderVendor").setVisible(true);
				this.oView.byId("toolId").setVisible(false);
				//Tables
				this.oView.byId("idVenUpldTabl").setVisible(false);
				this.oView.byId("idMasterVendor").setVisible(true);
				this.oView.byId("idVenUpldTabl2").setVisible(false);

				this.oView.byId("idFltrBarVendrResp").setVisible(false);
				this.oView.byId("idVR").setVisible(false);
				this.oView.byId("fbGenReptHboxR").setVisible(false);

				this.onPrsSearchVenMstr();
				this.onPrsRecpintPAN();
				//this.oView.byId("UploadCollection").setVisible(false);
			} else if (vId === "Email") {
				//this.oView.byId("idFileStatusChartCon").setVisible(false);
				this.oView.byId("idVenMstrDwnldChartCon").setVisible(false);
				this.oView.byId("idEmailChartCon").setVisible(true);
				//Filter Bar
				//this.oView.byId("idFltrBarVendrUpld").setVisible(false);
				this.oView.byId("idFltrBarVendrMstr").setVisible(false);
				this.oView.byId("idFltrBarVendrEmail").setVisible(true);
				this.oView.byId("fbVenMstrHbox").setVisible(false);
				this.oView.byId("fbGenReptHbox").setVisible(true);
				this.oView.byId("idheaderVendor").setVisible(true);
				//Tables
				this.oView.byId("idVenUpldTabl").setVisible(false);
				this.oView.byId("idMasterVendor").setVisible(false);
				this.oView.byId("idVenUpldTabl2").setVisible(false);
				this.oView.byId("toolId").setVisible(false);

				this.oView.byId("idFltrBarVendrResp").setVisible(false);
				this.oView.byId("idVR").setVisible(false);
				this.oView.byId("fbGenReptHboxR").setVisible(false);
				this.onEmailGenRtable();
			} else if (vId === "VendorResponse") {
				this.oView.byId("idVenMstrDwnldChartCon").setVisible(false);
				this.oView.byId("idEmailChartCon").setVisible(false);
				this.oView.byId("idVR").setVisible(true);
				//Filter Bar
				this.oView.byId("idFltrBarVendrMstr").setVisible(false);
				this.oView.byId("idFltrBarVendrEmail").setVisible(false);
				this.oView.byId("fbVenMstrHbox").setVisible(false);
				this.oView.byId("fbGenReptHbox").setVisible(false);

				this.oView.byId("idFltrBarVendrResp").setVisible(true);
				this.oView.byId("fbGenReptHboxR").setVisible(true);
				this.oView.byId("idheaderVendor").setVisible(true);
				//Tables
				this.oView.byId("idVenUpldTabl").setVisible(false);
				this.oView.byId("idMasterVendor").setVisible(false);
				this.oView.byId("idVenUpldTabl2").setVisible(false);
				this.oView.byId("toolId").setVisible(false);
				this.onVendorResponse();
				// this.onVRPAN();
			}
		},

		//#############   File Upload -> Vendor Upload Tab
		onUpload: function () {
			var oFileUploader = this.byId("cpFileUpld");
			if (oFileUploader.getValue() === "") {
				sap.m.MessageToast.show("Please select file to upload");
				return;
			}

			oFileUploader.setUploadUrl("/aspsapapi/vendorfileUpload.do");
			sap.ui.core.BusyIndicator.show(0);
			oFileUploader.upload();

		},

		onUploadComplete: function (oEvent) {
			var that = this;
			sap.ui.core.BusyIndicator.hide();
			var oRespnse = oEvent.getParameters().responseRaw;
			var status = JSON.parse(oRespnse);
			if (status.hdr.status === "S") {
				this.getView().byId("cpFileUpld").setValue("");
				/*MessageBox.confirm("Are you sure you want to Upload the file?", {
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					onClose: function (oActionSuccess) {
						if (oActionSuccess === "OK") {
							that.onVenUpldRefresh();
						}
					}
				});*/
				MessageBox.success(status.resp.message, {
					styleClass: "sapUiSizeCompact"

				});
				that.onVenUpldRefresh();
			} else {
				MessageBox.error(status.resp.message, {
					styleClass: "sapUiSizeCompact"
						//that.onVenUpldRefresh();
				});
				that.onVenUpldRefresh();
			}
		},

		//Table for NIC get Call
		onVenUpldRefresh: function (vPageNo) {
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/getVendorUploadedInfo.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": vCount
					},
					"req": {
						"vendorUpload": "VENDOR_UPLOAD"
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: onicPath,
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					// var oSrNo = data.resp;
					sap.ui.core.BusyIndicator.hide();
					/*for (var i = 0; i < data.resp.nicDetails.length; i++) {
					    data.resp.nicDetails[i].editEInv = false;
					    data.resp.nicDetails[i].editEwb = false;
					}*/
					that._nicPagination(data.hdr);
					/*onicCrdMdl.setData(data.resp);
					that.getView().byId("idTableGenNIC").setModel(onicCrdMdl, "nicCrdTabData");*/
					var oSrNo = data.resp.VendorUploadedStatus;
					for (var i = 0; i < oSrNo.length; i++) {
						oSrNo[i].SrNumber = i + 1;
					}
					onicCrdMdl.setData(data);
					that.getView().setModel(onicCrdMdl, "venUpldTabData");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		////////Pagination/////////////
		_nicPagination: function (header) {

			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNoVC").setText("/ " + pageNumber);
			this.byId("inPageNoVC").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoVC").setValue(pageNumber);
				this.byId("inPageNoVC").setEnabled(false);
				this.byId("btnPrevVC").setEnabled(false);
				this.byId("btnNextVC").setEnabled(false);
				this.byId("btnFirstVC").setEnabled(false);
				this.byId("btnLastVC").setEnabled(false);
			} else if (this.byId("inPageNoVC").getValue() === "" || this.byId("inPageNoVC").getValue() === "0") {
				this.byId("inPageNoVC").setValue(1);
				this.byId("inPageNoVC").setEnabled(true);
				this.byId("btnPrevVC").setEnabled(false);
				this.byId("btnFirstVC").setEnabled(false);
			} else {
				this.byId("inPageNoVC").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoVC").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextVC").setEnabled(true);
				this.byId("btnLastVC").setEnabled(true);
			} else {
				this.byId("btnNextVC").setEnabled(false);
				this.byId("btnLastVC").setEnabled(false);
			}
			this.byId("btnPrevVC").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstVC").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPaginationVC: function (oEvent) {

			var vValue = parseInt(this.byId("inPageNoVC").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevVC")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevVC").setEnabled(false);
				}
				if (!this.byId("btnNextVC").getEnabled()) {
					this.byId("btnNextVC").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextVC")) {
				var vPageNo = parseInt(this.byId("txtPageNoVC").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevVC").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextVC").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstVC")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstVC").setEnabled(false);
				}
				if (!this.byId("btnLastVC").getEnabled()) {
					this.byId("btnLastVC").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastVC")) {
				vValue = parseInt(this.byId("txtPageNoVC").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstVC").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastVC").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoVC").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoVC").setValue(vValue);
			this.onVenUpldRefresh(vValue);
		},

		onSubmitPaginationVC: function () {

			var vPageNo = this.byId("inPageNoVC").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoVC").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onVenUpldRefresh(vPageNo);
		},

		//Refresh the Vendor Upload table- 03.12.2020
		onVenUpldRefrsh: function () {
			this.onVenUpldRefresh();
		},

		onVenUpldDwndTempt1: function () {
			sap.m.URLHelper.redirect("model/VendorTypeMaster.xlsx", true);
		},

		onVenUpldDwndTempt: function () {
			sap.m.URLHelper.redirect("model/VendorDataTemplate.xlsx", true);
			// var oModel = new sap.ui.model.json.JSONModel("model/VCDwnldTemplt.json");
			// sap.ui.getCore().setModel(oModel, "oModel");
			// /*	var oModel = new sap.ui.model.json.JSONModel();
			// 	//var oVUpldTablData = this.byId("idVenUpldTabl").getModel("venUpldTabData").getData();
			// 	var oVUpldTablData = this.getView().getModel("venUpldTabData").getData().resp;
			// 	oModel.setData(oVUpldTablData);*/

			// var oExport = new Export({
			// 	exportType: new ExportTypeCSV({
			// 		separatorChar: "\t",
			// 		mimeType: "application/vnd.ms-excel",
			// 		charset: "utf-8",
			// 		fileExtension: "xls"
			// 	}),

			// 	models: oModel,

			// 	rows: {
			// 		path: "/items"
			// 	},
			// 	columns: [{
			// 		name: "RecipientPAN",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorGSTIN",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorCode",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorName",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorPrimaryE-MailID",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorPrimaryContactNumber",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorE-Mail-ID1",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorE-Mail-ID2",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorE-Mail-ID3",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "VendorE-Mail-ID4",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "RecipientE-Mail-ID1",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "RecipientE-Mail-ID2",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "RecipientE-Mail-ID3",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "RecipientE-Mail-ID4",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "RecipientE-Mail-ID5",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}, {
			// 		name: "Action",
			// 		template: {
			// 			content: ""
			// 		}
			// 	}]
			// });
			// oExport.saveFile().catch(function (oError) {
			// 	MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			// }).then(function () {
			// 	oExport.destroy();
			// });
		},

		//############# Error File Download -> Vendor Upload
		onPrsFileStatusErrDownload: function (oEvent) {
			/*//var strNum = this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()].startOfUploadTime;
			var strNum = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().startOfUploadTime;
			var oArySplt = strNum.split("-");
			var oFull = oArySplt[0] + oArySplt[1] + oArySplt[2];
			//var oId = this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()].id;
			var oId = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().id;
			var obatchId = oFull.concat(oId);
			var oErrorRecrd = "errorrecords";
			//var obatchId = this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()];
			//this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()].id
			//getVendorMasterErrorReport.do?batchId=20201005809404&flagofRecord=errorrecords
			var oExclDwnldPath = "/aspsapapi/getVendorMasterErrorReport.do?batchId=" + obatchId + "&flagofRecord=" + oErrorRecrd + "";
			window.open(oExclDwnldPath);*/
			var oErrorFlagId = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().id;
			var oVenUpldErrPayload = {
				//"req": {
				"fileId": oErrorFlagId,
				"flagofRecord": "errorrecords"
					//}
			};
			var vUrlErr = "/aspsapapi/getVendorMasterErrorReport.do";
			this.excelDownload(oVenUpldErrPayload, vUrlErr);
		},

		onPrsFileStatusInformDwnld: function (oEvent) {
			/*	//var strNum = this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()].startOfUploadTime;
				var strNum = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().startOfUploadTime;
				var oArySplt = strNum.split("-");
				var oFull = oArySplt[0] + oArySplt[1] + oArySplt[2];
				//var oId = this.getView().getModel("venUpldTabData").getData().resp[oEvent.getSource().getParent().getParent().getIndex()].id;
				var oId = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().id;
				var obatchId = oFull.concat(oId);
				var oInformRecrd = "informationrecords";
				var oExclDwnldPath = "/aspsapapi/getVendorMasterErrorReport.do?batchId=" + obatchId + "&flagofRecord=" + oInformRecrd + "";
				window.open(oExclDwnldPath);*/
			var oInfoFlagId = oEvent.getSource().getEventingParent().getParent().getBindingContext("venUpldTabData").getObject().id;
			var oVenUpldPayload = {
				//"req": {
				"fileId": oInfoFlagId,
				"flagofRecord": "informationrecords"
					//}
			};
			var vUrl = "/aspsapapi/getVendorMasterErrorReport.do";
			this.excelDownload(oVenUpldPayload, vUrl);
		},

		onPageNumberChange: function (oEvent) {
			this.getView().byId("idReconRTable").setVisibleRowCount(parseInt(oEvent.getSource().getSelectedItem().getText()));
			//sap.m.MessageToast.show("test");
		},

		//#####################		Tab2- Vendor Master	#####################################################
		onPrsRecpintPAN: function () {
			var oReciPANPayload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			var oRecpintPANMdl = new JSONModel();
			var oRecpintPANView = this.getView();
			var oRecpintPANPath = "/aspsapapi/getRecipientPanInfo.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRecpintPANPath,
					contentType: "application/json",
					data: JSON.stringify(oReciPANPayload)
				}).done(function (data, status, jqXHR) {
					data.resp.unshift({
						gstin: "All"
					})
					oRecpintPANMdl.setData(data.resp);
					oRecpintPANView.setModel(oRecpintPANMdl, "recipientPanData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPrsVenGstin: function () {

			if (!this.VenVMGSTIN) {
				this.VenVMGSTIN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VendorMGSTINF4", this);
				this.getView().addDependent(this.VenVMGSTIN);
			}
			var oVenGstnPayload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};

			var that = this;
			var oVenGStnMdl = new JSONModel();
			var oVenGStnView = this.getView();
			var oVenGStnPath = "/aspsapapi/getVendorGstInfo.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oVenGStnPath,
					contentType: "application/json",
					data: JSON.stringify(oVenGstnPayload)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var oData = $.extend(true, {}, data);
						oVenGStnMdl.setSizeLimit(data.resp.length);
						oVenGStnMdl.setData(data.resp);
						oVenGStnView.setModel(oVenGStnMdl, "venGstinDataMain");
						that.pageNumber = 1;
						that.pageSize = 1000; // Show 5 items per page
						that.updatePaginatedModel();

					} else {
						oVenGStnMdl.setData([]);
						oVenGStnView.setModel(oVenGStnMdl, "venGstinData");
					}
				}.bind(this)).fail(function (jqXHR, status, err) {
					oVenGStnMdl.setData([]);
					oVenGStnView.setModel(oVenGStnMdl, "venGstinData");
				});
			});
		},

		onPrsSearchVenMstr: function (vPageNo) {

			var that = this;
			/*var selItems = this.byId("idVUVendrGstn").getSelectedIndices();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}*/
			/*var oRecipientPan = this.byId("idVURecptPAN").getSelectedItems();
			var oRecipientPanArr = [];
			for (var i = 0; i < oRecipientPan.length; i++) {
				oRecipientPanArr.push(this.byId("idVURecptPAN").getSelectedItems()[i].getKey());
			}

			var oVendorGstin = this.byId("idVUVendrGstn").getSelectedItems();
			var oVendorGstinArr = [];
			for (var j = 0; j < oVendorGstin.length; j++) {
				oVendorGstinArr.push(this.byId("idVUVendrGstn").getSelectedItems()[j].getKey());
			}*/
			if (typeof vPageNo === "object") {
				vPageNo = 0;
				//vPageNo = Number(this.byId("inPageNo").getValue());
			}
			var onVenMstrInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
					"pageSize": vCount1,
				},
				"req": {
					"entityId": $.sap.entityID,
					"recipientPan": this.removeAll(this.getView().byId("idVURecptPAN").getSelectedKeys()), //oRecipientPanArr, //
					"vendorGstin": this.removeAll(this.VMGSTINarr) //oVendorGstinArr //
				}
			};

			var onVenMstrModel = new JSONModel();
			var onVenMstrView = this.getView();
			var onVenMstrPath = "/aspsapapi/getVendorMasterData.do";

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: onVenMstrPath,
					contentType: "application/json",
					data: JSON.stringify(onVenMstrInfo)
				}).done(function (data, status, jqXHR) {
					that._venMstrPagination(data.hdr);
					onVenMstrModel.setData(data.resp.ewbMasterData);
					//onVenMstrView.setModel(onVenMstrModel, "venMstrTabData");
					that.getView().byId("idMasterVendor").setModel(onVenMstrModel, "venMstrTabData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		////////BO Pagination/////////////  --- 12.11.20202
		_venMstrPagination: function (header) {

			var pageNumber = Math.ceil(header.totalCount / vCount1);
			this.byId("txtPageNo").setText("/ " + pageNumber);
			this.byId("inPageNo").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNo").setValue(pageNumber);
				this.byId("inPageNo").setEnabled(false);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnNext").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			} else if (this.byId("inPageNo").getValue() === "" || this.byId("inPageNo").getValue() === "0") {
				this.byId("inPageNo").setValue(1);
				this.byId("inPageNo").setEnabled(true);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
			} else {
				this.byId("inPageNo").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNo").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNext").setEnabled(true);
				this.byId("btnLast").setEnabled(true);
			} else {
				this.byId("btnNext").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			}
			this.byId("btnPrev").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirst").setEnabled(vPageNo > 1 ? true : false);
		},

		onVendorMasterPagination: function (oEvent) {
			var that = this;

			var vValue = parseInt(this.byId("inPageNo").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrev")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrev").setEnabled(false);
				}
				if (!this.byId("btnNext").getEnabled()) {
					this.byId("btnNext").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNext")) {
				var vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrev").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNext").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirst")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirst").setEnabled(false);
				}
				if (!this.byId("btnLast").getEnabled()) {
					this.byId("btnLast").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnLast")) {
				vValue = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirst").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLast").setEnabled(false);
				}
			} else {
				vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			}
			this.byId("inPageNo").setValue(vValue);
			this.onPrsSearchVenMstr(vValue);
		},

		onSubmitPagination: function () {

			var vPageNo = this.byId("inPageNo").getValue(),
				pageNumber = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onPrsSearchVenMstr(vPageNo);
		},
		//////// EO Pagination/////////////  --- 12.11.2020

		onPressRefreshBtn: function () {
			this.getGstrget2AASucessStatusDataFinal();
		},
		onPressRefreshBtn2A: function () {
			this.onPressGet2aStatusFinal();
		},

		/*	//////// Email BO Pagination/////////////  --- 12.11.20202
			_venMstrPaginationFr: function (header) {
				
				var pageNumber = Math.ceil(header.totalCount / vCount1);
				this.byId("txtPageNo").setText("/ " + pageNumber);
				this.byId("inPageNo").setValue(header.pageNum + 1);
				if (pageNumber === 0 || pageNumber === 1) {
					this.byId("inPageNo").setValue(pageNumber);
					this.byId("inPageNo").setEnabled(false);
					this.byId("btnPrev").setEnabled(false);
					this.byId("btnNext").setEnabled(false);
					this.byId("btnFirst").setEnabled(false);
					this.byId("btnLast").setEnabled(false);
				} else if (this.byId("inPageNo").getValue() === "" || this.byId("inPageNo").getValue() === "0") {
					this.byId("inPageNo").setValue(1);
					this.byId("inPageNo").setEnabled(true);
					this.byId("btnPrev").setEnabled(false);
					this.byId("btnFirst").setEnabled(false);
				} else {
					this.byId("inPageNo").setEnabled(true);
				}
				var vPageNo = parseInt(this.byId("inPageNo").getValue(), 10);
				if (pageNumber > 1 && pageNumber !== vPageNo) {
					this.byId("btnNext").setEnabled(true);
					this.byId("btnLast").setEnabled(true);
				} else {
					this.byId("btnNext").setEnabled(false);
					this.byId("btnLast").setEnabled(false);
				}
				this.byId("btnPrev").setEnabled(vPageNo > 1 ? true : false);
				this.byId("btnFirst").setEnabled(vPageNo > 1 ? true : false);
			},

			onPressPaginationVCF: function (oEvent) {
				var that = this;
				
				var vValue = parseInt(this.byId("inPageNo").getValue(), 10),
					vIdBtn = oEvent.getSource().getId();
				if (vIdBtn.includes("btnPrev")) {
					vValue -= 1;
					if (vValue === 1) {
						this.byId("btnPrev").setEnabled(false);
					}
					if (!this.byId("btnNext").getEnabled()) {
						this.byId("btnNext").setEnabled(true);
					}
				} else if (vIdBtn.includes("btnNext")) {
					var vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
					vValue += 1;
					if (vValue > 1) {
						this.byId("btnPrev").setEnabled(true);
					}
					if (vValue === vPageNo) {
						this.byId("btnNext").setEnabled(false);
					}
				} else if (vIdBtn.includes("btnFirst")) {
					vValue = 1;
					if (vValue === 1) {
						this.byId("btnFirst").setEnabled(false);
					}
					if (!this.byId("btnLast").getEnabled()) {
						this.byId("btnLast").setEnabled(true);
					}
				} else if (vIdBtn.includes("btnLast")) {
					vValue = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);

					if (vValue > 1) {
						this.byId("btnFirst").setEnabled(true);
					}
					if (vValue === vPageNo) {
						this.byId("btnLast").setEnabled(false);
					}
				} else {
					vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
				}
				this.byId("inPageNo").setValue(vValue);
				this.onPrsSearchVenMstr(vValue);
			},

			onSubmitPaginationVCF: function () {
				
				var vPageNo = this.byId("inPageNo").getValue(),
					pageNumber = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
				vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
				if (vPageNo > pageNumber) {
					vPageNo = pageNumber;
				}
				this.onPrsSearchVenMstr(vPageNo);
			},*/
		//////// EO Pagination/////////////  --- 12.11.2020

		handleChangeVC: function (oevt) {
			var toDate = this.byId("idEmailToDate").getDateValue(),
				fromDate = this.byId("idEmailFrmDate").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("idEmailToDate").setDateValue(oevt.getSource().getDateValue());
				this.byId("idEmailToDate").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idEmailToDate").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onVenMstrSearch: function (oEvt) {
			// var t = oEvt.getSource().getValue();
			// this.byId("idMasterVendor").getBinding("rows").filter([new sap.ui.model.Filter([new sap.ui.model.Filter(
			// 	"recipientPAN", sap
			// 	.ui.model.FilterOperator
			// 	.Contains,
			// 	t), new sap.ui.model.Filter("vendorGstin", sap.ui.model.FilterOperator.Contains, t)], false)]);
			var oTable = this.byId("idMasterVendor");
			var searchText = oEvt.getSource().getValue();
			var filters = [];
			if (searchText.trim() != '') {
				var filter1 = new sap.ui.model.Filter({
					path: "recipientPAN",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter2 = new sap.ui.model.Filter({
					path: "vendorGstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter3 = new sap.ui.model.Filter({
					path: "vendorCode",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter4 = new sap.ui.model.Filter({
					path: "vendorName",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter5 = new sap.ui.model.Filter({
					path: "vendPrimEmailId",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter6 = new sap.ui.model.Filter({
					path: "vendorContactNumber",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter7 = new sap.ui.model.Filter({
					path: "vendorEmailId1",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter8 = new sap.ui.model.Filter({
					path: "vendorEmailId2",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter9 = new sap.ui.model.Filter({
					path: "vendorEmailId3",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter10 = new sap.ui.model.Filter({
					path: "vendorEmailId4",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter11 = new sap.ui.model.Filter({
					path: "recipientEmailId1",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter12 = new sap.ui.model.Filter({
					path: "recipientEmailId2",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter13 = new sap.ui.model.Filter({
					path: "recipientEmailId3",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter14 = new sap.ui.model.Filter({
					path: "recipientEmailId4",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter15 = new sap.ui.model.Filter({
					path: "recipientEmailId5",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter16 = new sap.ui.model.Filter({
					path: "emailId2",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter16 = new sap.ui.model.Filter({
					path: "emailId2",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter17 = new sap.ui.model.Filter({
					path: "vendorGstin",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter18 = new sap.ui.model.Filter({
					path: "vendorType",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter19 = new sap.ui.model.Filter({
					path: "hsn",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter20 = new sap.ui.model.Filter({
					path: "vendorRiskCategory",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter21 = new sap.ui.model.Filter({
					path: "vendorPaymentTerms",
					operator: sap.ui.model.FilterOperator.EQ,
					value1: searchText
				});
				var filter22 = new sap.ui.model.Filter({
					path: "vendorRemarks",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter23 = new sap.ui.model.Filter({
					path: "approvalStatus",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				var filter24 = new sap.ui.model.Filter({
					path: "excludeVendorRemarks",
					operator: sap.ui.model.FilterOperator.Contains,
					value1: searchText
				});
				filters = [filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8, filter9, filter10, filter11, filter12,
					filter13, filter14, filter15, filter16, filter17, filter18, filter19, filter20, filter21, filter22, filter23, filter24
				];
				var finalFilter = new sap.ui.model.Filter({
					filters: filters,
					and: false
				});
				oTable.getBinding("rows").filter(finalFilter, sap.ui.model.FilterType.Application);
			} else {
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}

		},

		/// Download Table Data
		onDwnldTablDataVM: function () {
			var oDwnldTabDataVMReq = {
				"req": {
					"entityId": $.sap.entityID,
					"recipientPan": this.removeAll(this.getView().byId("idVURecptPAN").getSelectedKeys()), //oRecipientPanArr, //
					"vendorGstin": this.removeAll(this.VMGSTINarr) //oVendorGstinArr //
				}
			};
			var oReqExcelVMPath = "/aspsapapi/getVendorMasterReport.do";
			this.excelDownload(oDwnldTabDataVMReq, oReqExcelVMPath);
		},
		//########### EOC of Vendor Communication by Rakesh on 30.09.2020
		//########### BOC -Email-- Vendor Communication by Rakesh on 19.11.2020
		// ############ Genarate Report Table
		onPrsGenerateReport: function () { //onPrsGenerateReport: function (value) {

			var that = this;
			var oRecptGstn = this.byId("idEmailRecptGstn").getSelectedItems();
			if (oRecptGstn.length > 0 && oRecptGstn[0].getText() === "All") {
				oRecptGstn.shift();
			}
			var oRecptGstnArr = [];
			for (var i = 0; i < oRecptGstn.length; i++) {
				oRecptGstnArr.push({
					gstin: oRecptGstn[i].getText(),
					gstin: oRecptGstn[i].getText()
				});
			}
			var oVendorGstinArr = [];
			for (var j = 0; j < this.GSTINarr.length; j++) {
				oVendorGstinArr.push({
					gstin: this.GSTINarr[j],
				});
			}

			var oVendorPansArr = [];
			for (var k = 0; k < this.Panarr.length; k++) {
				oVendorPansArr.push({
					gstin: this.Panarr[k],
				});
			}

			var oEmailRepTypData = this.byId("idReptType").getSelectedKeys();
			//var oEmailReptTyp = this.byId("idReptType").getSelectedKey();
			if (oEmailRepTypData.length === 0) {
				MessageBox.information("Please select atleast one Report Type");
				return;
			}

			if (oEmailRepTypData.length > 0 && oEmailRepTypData[0] === "All") {
				oEmailRepTypData.shift();
			}
			var oEmailRepTypArr = [];
			for (var j = 0; j < oEmailRepTypData.length; j++) {
				oEmailRepTypArr.push({
					reportType: oEmailRepTypData[j]
				});
			}
			var oEmailGenReptInfo = {
				"req": {
					"recipientGstins": oRecptGstnArr, //this.removeAll(this.byId("idEmailRecptGstn").getSelectedKeys()), //aGstin.includes("All") ? [] : aGstin, //,aGstin.length === 0 ? gstin : aGstin 
					"vendorGstins": oVendorGstinArr, //this.removeAll(this.byId("idEmailVenGstn").getSelectedKeys()),
					"vendorPans": oVendorPansArr,
					"reportTypes": oEmailRepTypArr,
					"fromTaxPeriod": this.byId("idEmailFrmDate").getValue(),
					"toTaxPeriod": this.byId("idEmailToDate").getValue(),
					"entityId": $.sap.entityID,
					"reconType": this.byId("idReconType45").getSelectedKey()
				}
			};
			/*var oEmailGenReptInfo = {
				"req": {
					"recipientGstins": this.removeAll(this.byId("idEmailRecptGstn").getSelectedKeys()), //aGstin.includes("All") ? [] : aGstin, //oRecptGstnArr,aGstin.length === 0 ? gstin : aGstin 
					"vendorGstins": this.removeAll(this.byId("idEmailVenGstn").getSelectedKeys()), //oVendorGstinArr,
					"reportTypes": this.removeAll(this.byId("idReptType").getSelectedKeys()), //oEmailRepTypArr
					"fromTaxPeriod": this.byId("idEmailFrmDate").getValue(),
					"toTaxPeriod": this.byId("idEmailToDate").getValue()
				}
			};*/

			var oEmailGenRModel = new JSONModel(),
				oGstr2AView = this.getView(),
				Gstr2APath = "/aspsapapi/generateVendorReportRequest.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Gstr2APath,
					contentType: "application/json",
					data: JSON.stringify(oEmailGenReptInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						//oView.byId("idInitiateReconList2A").setVisibleRowCount(data.resp.det.length);
						//var oGSTIN = new JSONModel(data.resp.det);
						//oView.setModel(oGSTIN, "GSTIN2A");
						that.byId("idReptType").setSelectedKeys([]);
						that.onEmailGenRtable();
					} else {
						MessageBox.information("No Data");
						//var oGSTIN1 = new JSONModel([]);
						//oView.setModel(oGSTIN1, "GSTIN2A");
						//oView.setModel(oGSTIN1, "InitiateRecon2A");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					// var oGSTIN2 = new JSONModel([]);
					// oView.setModel(oGSTIN2, "GSTIN2A");
					// oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
			});
		},
		// ############ Genarate Report Table
		onEmailGenRtable: function () {
			var ReconType;
			if (this.byId("idReconType45").getSelectedKey() === "") {
				ReconType = "2A_PR";
			} else {
				ReconType = this.byId("idReconType45").getSelectedKey();
			}
			var oEmailGenRModel = new JSONModel();
			var oGenRepView = this.getView();
			//var Gstr2APath = "/aspsapapi/getVendorRequestDetails.do";
			var req = {
				"req": {
					"reconType": ReconType
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getVendorRequestDetails.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						oEmailGenRModel.setData(data.resp.vendorData);
						oGenRepView.setModel(oEmailGenRModel, "EmailTableData");
					} else {
						var oGSTIN1 = new JSONModel([]);
						oGenRepView.setModel(oGSTIN1, "EmailTableData");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					// var oGSTIN2 = new JSONModel([]);
					// oView.setModel(oGSTIN2, "GSTIN2A");
					// oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
			});
		},

		//Refresh the Email tab- Genarate Report Table
		onPrsEmailGenRfrsh: function () {
			this.onEmailGenRtable();
		},

		//Email tab- Based on Request id Email popup will be dispalyed
		onEmailReqIdPress: function (oEvt) {
			var that = this;
			if (!this._oDialogRefund) {
				this._oDialogRefund = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr2.ITCMatchEmail", this);
				this.getView().addDependent(this._oDialogRefund);
			}
			this._oDialogRefund.open();
			var data = this.getView().getModel("Display").getData().LabelVc;
			sap.ui.getCore().byId("subjectId").setText("Subject: " + $.sap.entityName + " - Matching Input Tax Credit" +
				" - Vendor GSTIN: XXXXX");
			sap.ui.getCore().byId("textId").setValue(
				"Dear Sir/Madam,\n\n Under GST law, it is mandatory that inward records on which Input Tax Credit (ITC) is to be availed are reflected in GSTR-" +
				data +
				" of recipient. As a part of GST compliance procedure, we have matched the records appearing in purchase register with the records reported by you in your GSTR-1 (Form GSTR-" +
				data +
				" as made available to us by GSTN).\n\nWe have provided the below link to view all matching results for your kind perusal. You are requested to take necessary action against required transactions with appropriate remarks within the timelines. \n\nSteps to follow:\n\n •	Click on the below link (valid for 30 days) to view and download the reconciled records (authentication page will be opened).\n\n https://devvc.eyasp.in/vcapp/otpGeneration?reqId=Z51xpwYN9Zxc2ep3JKIIEOFLn4HF/MReXFkNhX+NlH5j7+R0qoZxu3D14rA4IMeD1FFu77/v9Df3UzobMjdI1A==&&source=Exj6TILMbVMBGQWE/HGEXA== \n\n•	OTP will be received over Email ID.\n\n •	Enter OTP and click on Verify\n\n •	Post authentication, reconciled records will be displayed on screen to take necessary actions against each records.\n\n • Click on the instructions for detailed steps and response upload. \n\nRegards.\n\n EY DigiGST "
			);
			sap.ui.getCore().byId("textId1").setValue(
				"***This is an auto - generated email.Please do not reply to this email.Reply and query can be sent to the above mentioned recipient 's Email IDs" +
				"***" +
				"\nIMPORTANT: This e-mail and any files transmitted with it are for the sole use of the intended recipient(s) and may contain confidential and privileged information. If you are not the intended recipient, please destroy all copies and the original message. Any unauthorized review, use, disclosure, dissemination, forwarding, printing or copying of this email or any action taken in reliance on this e-mail is strictly prohibited and may be unlawful."
			);

			sap.ui.getCore().byId("idVenGSTNEmail").setFilterFunction(function (sTerm, oItem) {
				// A case-insensitive 'string contains' filter
				return oItem.getText().match(new RegExp(sTerm, "i")) || oItem.getKey().match(new RegExp(sTerm, "i"));
			});
			sap.ui.getCore().byId("idVenNameEmail").setFilterFunction(function (sTerm, oItem) {
				// A case-insensitive 'string contains' filter
				return oItem.getText().match(new RegExp(sTerm, "i")) || oItem.getKey().match(new RegExp(sTerm, "i"));
			});
			//sap.ui.getCore().byId("textId1").addStyleClass("cl_colorRed");
			this.oReqId = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId;
			this.onEmailReqIdPress1();
		},

		onReset: function () {
			sap.ui.getCore().byId("idVenGSTNEmail").setSelectedKey();
			this.onFilterEmail();
		},

		onEmailReqIdPress1: function (vPageNo) {

			var that = this;
			//var oReqId = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId;
			/*var oEmailReqIdInfo = {
				"req": {
					"requestID": "79" //this.oReqId //"3402" "79"
				}
			};*/
			if (vPageNo === undefined) {
				vPageNo = 0;
				//vPageNo = Number(this.byId("inPageNo").getValue());
			}
			var oEmailReqIdInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
					"pageSize": vCount1,
				},
				"req": {
					"requestID": this.oReqId, //"3402" "79"
					"entityId": $.sap.entityID,
					"reconType": this.byId("idReconType45").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var oRecpintPANMdl = new JSONModel();
			var oRecpintPANView = this.getView();
			var oRecpintPANPath = "/aspsapapi/getVendorEmailCommunicationDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRecpintPANPath,
					contentType: "application/json",
					data: JSON.stringify(oEmailReqIdInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = that.getView().getModel("Display");
					if (data.latestStatus === "COMPLETED") {
						oModel.getData().SendAll = true;
					} else {
						oModel.getData().SendAll = false;
					}
					that.getView().getModel("Display").refresh(true);
					sap.ui.getCore().byId("idtablerefundEmail").clearSelection();
					sap.ui.getCore().byId("idtablerefundEmail").setVisibleRowCount(data.resp.length);
					oRecpintPANMdl.setData(data);
					//oRecpintPANView.setModel(oRecpintPANMdl, "emailCommPopupData");
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
					that._venMstrPaginationFr(data.hdr);
					//that.oEmailPopupVen();
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		//////// Email BO Pagination vinay/////////////  --- 24.12.20202
		_venMstrPaginationFr: function (header) {
			//eslint-disable-line
			var core = sap.ui.getCore();
			var pageNumber = Math.ceil(header.totalCount / vCount1);
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

		onPressPaginationVCF: function (oEvent) {
			var that = this;

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
					core.byId("btnNextVCF").setEnabled(false);
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
			this.onEmailReqIdPress1(vValue);
		},

		onSubmitPaginationVCF: function () {

			var vPageNo = core.byId("inPageNoVCF").getValue(),
				pageNumber = parseInt(core.byId("txtPageNoVCF").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onEmailReqIdPress1(vPageNo);
		},
		//////// EO Pagination/////////////  --- 24.12.2020

		///Email popup function
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
				})
				var oRecipientEmailIds = oTableData.resp[i].recipientEmailIds;
				oRecipientEmailIds.unshift({
					emailId: "All"
				})
				if (oSelrow.length !== 0) {
					oSelrow[i].getCells()[3].setModel(new JSONModel(oSecondaryEmailIds), "venSecEmailsCombo");
					oSelrow[i].getCells()[4].setModel(new JSONModel(oRecipientEmailIds), "venRecpntEmailsCombo");
				}
				/*for (var j = 0; j < oTableData.resp[i].secondaryEmailIds.length; j++) {
					arr.push({
						"emailId": oTableData.resp[i].secondaryEmailIds[j].emailId
					});
				}*/
				/*var json = new JSONModel();
				json.setData(arr);
				sap.ui.getCore().byId("idtablerefundEmail").setModel(json, "venSecEmailsCombo1");*/
			}
		},

		onCloseEmailOtp: function () {
			this._oDialogRefund.close();
			this._oDialogRefund.destroy();
			this._oDialogRefund = null;
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
					"ReconType": this.byId("idReconType45").getSelectedKey(),
					"VendorDetails": []
				}
			};
			for (var x = 0; x < emailSelectedTableIndices.length; x++) {
				var tempObj = {
					"RcpntEmail": [],
					"VndrSecndEmail": [],
					"totalSecEmail": [],
					"totalRecpEmail": []

				};
				tempObj.VndrContactNo = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].vendorContactNumber;
				tempObj.VcomRequestID = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].requestID;
				tempObj.VGSTIN = emailTableRows[emailSelectedTableIndices[x]].getCells()[0].getText();
				tempObj.vendorName = emailTableRows[emailSelectedTableIndices[x]].getCells()[1].getText();
				tempObj.VndrPrmryEmail = emailTableRows[emailSelectedTableIndices[x]].getCells()[2].getText();
				tempObj.EmailStatus = emailTableRows[emailSelectedTableIndices[x]].getCells()[5].getText();
				var vendorSecDropdown = emailTableRows[emailSelectedTableIndices[x]].getCells()[3].getSelectedItems();
				var receiptEmailDropdown = emailTableRows[emailSelectedTableIndices[x]].getCells()[4].getSelectedItems();

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
				if (emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].secondaryEmailIds[0].emailId ===
					"All") {
					emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].secondaryEmailIds.shift();
				}
				if (emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].recipientEmailIds[0].emailId ===
					"All") {
					emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].recipientEmailIds.shift();
				}

				var totalSecEmail = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].secondaryEmailIds
				var totalRecpEmail = emailTableID.getModel("emailCommPopupData").getData().resp[emailSelectedTableIndices[x]].recipientEmailIds;

				for (var h = 0; h < totalSecEmail.length; h++) {
					tempObj.totalSecEmail.push(totalSecEmail[h].emailId);
				}

				for (var h = 0; h < totalRecpEmail.length; h++) {
					tempObj.totalRecpEmail.push(totalRecpEmail[h].emailId);
				}

				emailPayload.req.VendorDetails.push(tempObj);
			}
			var oEmailpath = "/aspsapapi/postVendorEmailCommDetails.do"
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
						//sap.ui.getCore().byId("linkEntityID1").setSelectedKeys();
						MessageBox.success("Email has been successfully Initiated");
						/*, {
													actions: [MessageBox.Action.OK],
													onClose: function (oActionSuccess) {
														if (oActionSuccess === "OK") {
															if (!that._ITCdAuthToken) {
																that._ITCdAuthToken = sap.ui.xmlfragment("com.ey.digigst.fragments.gstr2.ITCMatchAuthToken", that);
																that.getView().addDependent(that._ITCdAuthToken);
															}
															that._ITCdAuthToken.open();
														}
													}
												}*/

					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPreEmailOtpSendAll: function () {
			var that = this
			MessageBox.confirm("Email will be triggered to all email ids. Do you want to initiate for all email ids?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "YES") {
						that.onPreEmailOtpSendAllFinal();

					}
				}
			});
		},

		onPreEmailOtpSendAllFinal: function () {
			var emailPayload = {
				"req": {
					"requestID": this.oReqId,
					"entityId": $.sap.entityID,
					"reconType": this.byId("idReconType45").getSelectedKey()
				}
			};

			var oEmailpath = "/aspsapapi/getAndPostVendorEmailCommDetails.do"
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oEmailpath,
					contentType: "application/json",
					data: JSON.stringify(emailPayload)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						MessageBox.success("Email has been successfully initiated for all selected email ids");
						var oModel = that.getView().getModel("Display");
						oModel.getData().SendAll = false;
						that.getView().getModel("Display").refresh(true);
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressCancel: function () {
			this._ITCdAuthToken.close();
		},

		// Email POPUP Drop downs
		onFilterEmail: function (oEvt) {
			var oTable = sap.ui.getCore().byId("idtablerefundEmail");
			//var searchText = oEvt.getSource().getValue();
			// var searchText = oEvt.getSource().getSelectedKeys()[0];
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
				this.onEmailReqIdPress1();
				//oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		onRefrshEmailDD: function (flag) {
			sap.ui.getCore().byId("idVenGSTNEmail").setSelectedKey("");
			sap.ui.getCore().byId("idVenNameEmail").setSelectedKey("");
			sap.ui.getCore().byId("linkEntityID55").setSelectedKey("");
			if (flag === "R") {
				var vValue = parseInt(sap.ui.getCore().byId("inPageNoVCF").getValue(), 10);
				this.onEmailReqIdPress1(vValue);
			}
			// this.onEmailReqIdPress1();
		},

		////Report download
		onDownloadReport: function (oEvt) {
			var oReqId = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId
			var oReqExcelPath = "/aspsapapi/downloadVendorReport.do?requestId=" + oReqId + "";
			window.open(oReqExcelPath);
		},

		////////////////////// Email Segment Button /////////////////////////////////
		// ### Filters- Vendor Recipient  GSTIN
		onPrsRecpintGSTN: function () {
			var oReciPANPayload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			var oRecpintPANMdl = new JSONModel();
			var oRecpintPANView = this.getView();
			var oRecpintPANPath = "/aspsapapi/getRecipientGstin.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oRecpintPANPath,
					contentType: "application/json",
					data: JSON.stringify(oReciPANPayload)
				}).done(function (data, status, jqXHR) {
					data.resp.unshift({
						gstin: "All"
					})
					oRecpintPANMdl.setData(data.resp);
					oRecpintPANMdl.setSizeLimit(data.resp.length);
					oRecpintPANView.setModel(oRecpintPANMdl, "recipientGstinData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//Filter- Vendor PAN Data- 
		onEmailVenPAN: function () {

			// if (!this.VendorPan) {
			// 	this.VendorPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VendorPANF4", this);
			// 	this.getView().addDependent(this.VendorPan);
			// }

			// if (!this.VendorPanRR) {
			// 	this.VendorPanRR = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRVendorPANF4", this);
			// 	this.getView().addDependent(this.VendorPanRR);
			// }

			// if (!this.VendorPanRR2AB) {
			// 	this.VendorPanRR2AB = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRVendorPANF42AB", this);
			// 	this.getView().addDependent(this.VendorPanRR2AB);
			// }

			// this.byId("searchId1").setValue();
			// this.byId("searchId1RR").setValue();
			// this.byId("searchId1RR2AB").setValue();
			var oVenEmailPANPayload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			var oSelVenPANMdl = new JSONModel();
			var oSelVenPANView = this.getView();
			var that = this;
			//var oSelVenPANPath = "/aspsapapi/getVendorPan.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getVendorPanDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oVenEmailPANPayload)
				}).done(function (data, status, jqXHR) {
					oSelVenPANMdl.setSizeLimit(data.resp.vendorPans.length);
					oSelVenPANMdl.setData(data.resp);
					// oSelVenPANView.setModel(oSelVenPANMdl, "venPANData");
					oSelVenPANView.setModel(oSelVenPANMdl, "venPANDataRR");
					oSelVenPANView.setModel(oSelVenPANMdl, "venPANDataRR2AB");

					oSelVenPANView.setModel(oSelVenPANMdl, "venPANDataMain");

				}).fail(function (jqXHR, status, err) {});
			});
		},

		onVRPAN: function () {
			if (!this.VRPan) {
				this.VRPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VRPANF4", this);
				this.getView().addDependent(this.VRPan);
			}
			this.byId("searchIdVRP").setValue();
			var oVenEmailPANPayload = {
				"req": {
					"entityId": $.sap.entityID,
				}
			};
			var oSelVenPANMdl = new JSONModel();
			var oSelVenPANView = this.getView();
			//var oSelVenPANPath = "/aspsapapi/getVendorPan.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getVendorPanDetails.do",
					contentType: "application/json",
					data: JSON.stringify(oVenEmailPANPayload)
				}).done(function (data, status, jqXHR) {
					oSelVenPANMdl.setSizeLimit(data.resp.vendorPans.length);
					oSelVenPANMdl.setData(data.resp);
					oSelVenPANView.setModel(oSelVenPANMdl, "vrPANData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		//Filter- Based on Vendor PAN - Vendor Gstin data will be displyed 
		onPrsEmailVenPAN: function () {

			if (!this.VenGSTIN) {
				this.VenGSTIN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VendorGSTINF4", this);
				this.getView().addDependent(this.VenGSTIN);
			}

			if (!this.VenGSTINRR) {
				this.VenGSTINRR = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRVendorGSTINF4", this);
				this.getView().addDependent(this.VenGSTINRR);
			}
			this.byId("searchIdGSTIN").setValue();
			this.byId("searchIdGSTINRR").setValue();
			var oSelVenPANMdl = new JSONModel();
			var oSelVenPANView = this.getView();
			if (this.Panarr.length === 0) {
				oSelVenPANView.byId("selectDialogGSTIN").setModel(new JSONModel(null), "EmailvenGstinData");
				oSelVenPANView.byId("selectDialogGSTINRR").setModel(new JSONModel(null), "EmailvenGstinDataRR");
			} else {
				var oVenPANPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorPan": this.removeAll(this.Panarr)
					}
				};

				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getVendorGstin.do",
						contentType: "application/json",
						data: JSON.stringify(oVenPANPayload)
					}).done(function (data, status, jqXHR) {
						if (Array.isArray(data.resp.vendorGstins)) {
							oSelVenPANMdl.setSizeLimit(data.resp.vendorGstins.length);
							oSelVenPANMdl.setData(data.resp);
							oSelVenPANView.byId("selectDialogGSTIN").setModel(oSelVenPANMdl, "EmailvenGstinData");
							oSelVenPANView.byId("selectDialogGSTINRR").setModel(oSelVenPANMdl, "EmailvenGstinDataRR");
						}
					}).fail(function (jqXHR, status, err) {});
				});
			}
		},

		onPrsEmailVenPAN2AB: function () {

			if (!this.VenGSTINRR2AB) {
				this.VenGSTINRR2AB = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.RRVendorGSTINF42AB", this);
				this.getView().addDependent(this.VenGSTINRR2AB);
			}

			this.byId("searchIdGSTINRR2AB").setValue();

			//var oSelVenPAN = this.byId("idEmailVenPAN").getSelectedItems();
			var oSelVenPANMdl = new JSONModel();
			var oSelVenPANView = this.getView();
			if (this.Panarr2AB.length === 0) {
				oSelVenPANView.byId("selectDialogGSTINRR2AB").setModel(new JSONModel(null), "EmailvenGstinDataRR2AB");

			} else {
				var oVenPANPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorPan": this.removeAll(this.Panarr2AB)
					}
				};

				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getVendorGstin.do",
						contentType: "application/json",
						data: JSON.stringify(oVenPANPayload)
					}).done(function (data, status, jqXHR) {
						if (Array.isArray(data.resp.vendorGstins)) {
							oSelVenPANMdl.setSizeLimit(data.resp.vendorGstins.length);
							oSelVenPANMdl.setData(data.resp);
							oSelVenPANView.byId("selectDialogGSTINRR2AB").setModel(oSelVenPANMdl, "EmailvenGstinDataRR2AB");
						}
					}).fail(function (jqXHR, status, err) {});
				});
			}
		},

		//Filter- Based on Vendor PAN - Vendor Gstin data will be displyed 
		onPrsVenPAN: function () {
			if (!this.VrGSTIN) {
				this.VrGSTIN = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VrGSTINF4", this);
				this.getView().addDependent(this.VrGSTIN);
			}
			this.byId("searchIdvrG").setValue();
			//var oSelVenPAN = this.byId("idEmailVenPAN").getSelectedItems();
			if (this.Panarrvr.length === 0) {
				this.byId("selectDialogGSTINvr").setModel(new JSONModel([{
					gstin: ""
				}]), "EmailvenGstinDatavr");
			} else {
				var oVenPANPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorPan": this.removeAll(this.Panarrvr)
					}
				};
				var oSelVenPANMdl = new JSONModel();
				var oSelVenPANView = this.getView();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getVendorGstin.do",
						contentType: "application/json",
						data: JSON.stringify(oVenPANPayload)
					}).done(function (data, status, jqXHR) {
						if (Array.isArray(data.resp.vendorGstins)) {
							oSelVenPANMdl.setSizeLimit(data.resp.vendorGstins.length);
							oSelVenPANMdl.setData(data.resp);
							oSelVenPANView.byId("selectDialogGSTINvr").setModel(oSelVenPANMdl, "EmailvenGstinDatavr");
						}
					}).fail(function (jqXHR, status, err) {});
				});
			}
		},

		//Filter- Based on Vendor GSTIN - Vendor Name data will be displyed 
		onPrsEmailVenGSTIN: function () {
			//var oSelVenGstin = this.byId("idEmailVenGstn").getSelectedItems();
			if (this.GSTINarr.length === 0) {
				this.byId("idEmailVenName").setModel(new JSONModel([]), "vendorNameData");
				this.byId("idEmailVenCode").setModel(new JSONModel([]), "vendorCodeData");
			} else {
				/*var oSelVenNameArr = [];
				for (var i = 0; i < oSelVenGstin.length; i++) {
					oSelVenNameArr.push(oSelVenGstin[i].getKey());
				}*/
				var oVenNamePayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstIn": this.removeAll(this.GSTINarr)
					}
				};
				var oSelVenNameMdl = new JSONModel();
				var oSelVenNameView = this.getView();
				var that = this;
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getVendorName.do",
						contentType: "application/json",
						data: JSON.stringify(oVenNamePayload)
					}).done(function (data, status, jqXHR) {
						if (Array.isArray(data.resp)) {
							that.byId("idEmailVenCode").setModel(new JSONModel({
								vendorCode: ""
							}), "vendorCodeData");
							data.resp.unshift({
									vendorName: "All"
								})
								/*	for (var i = 0; i < data.resp.length; i++) {
										data.resp[i].vendorKey = i;
									}*/
							oSelVenNameMdl.setSizeLimit(data.resp.length);
							oSelVenNameMdl.setData(data.resp);
							oSelVenNameView.byId("idEmailVenName").setModel(oSelVenNameMdl, "vendorNameData");
						}
					}).fail(function (jqXHR, status, err) {});
				});
			}
		},

		RecGstinPress: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("EmailTableData").getData();
			var reqId = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId;
			for (var i = 0; i < TabData.length; i++) {
				if (reqId === TabData[i].requestId) {
					gstins.push(TabData[i].recipientGstins);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover) {
				this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
				this.getView().addDependent(this._oGstinPopover);
			}
			this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
			this._oGstinPopover.openBy(oButton);
		},

		RepReqPress: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("EmailTableData").getData();
			var reqId = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId;
			for (var i = 0; i < TabData.length; i++) {
				if (reqId === TabData[i].requestId) {
					gstins.push(TabData[i].reportTypes);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (!this._oGstinPopover12) {
				this._oGstinPopover12 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.PopoverVC", this);
				this.getView().addDependent(this._oGstinPopover12);
			}
			this._oGstinPopover12.setModel(oReqWiseModel1, "gstinsVC");
			this._oGstinPopover12.openBy(oButton);
		},

		//Filter- Based on Vendor GSTIN and Vendor Name - Vendor Code data will be displyed 
		onPrsEmailVenGstinName: function () {
			//var oSelVenGstin = this.byId("idEmailVenGstn").getSelectedItems();
			var oSelVenname = this.byId("idEmailVenName").getSelectedItems();
			if (this.GSTINarr.length === 0 || oSelVenname.length === 0) {
				this.byId("idEmailVenCode").setModel(new JSONModel({
					vendorCode: ""
				}), "vendorCodeData");
			} else {
				//this.oSelVenGstin = oEvent.getSource().getSelectedItems();
				/*var oSelVenGSTArr = [];
				for (var i = 0; i < oSelVenGstin.length; i++) {
					oSelVenGSTArr.push(oSelVenGstin[i].getKey());
				}*/

				//this.oSelVenGstin = oEvent.getSource().getSelectedItems();
				var oSelVennameArr = [];
				for (var i = 0; i < oSelVenname.length; i++) {
					oSelVennameArr.push(oSelVenname[i].getKey());
				}

				var oVenCodePayload = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstIn": this.removeAll(this.GSTINarr),
						"vendorName": this.removeAll(oSelVennameArr)
					}
				};
				var oSelVenCodeMdl = new JSONModel();
				var oSelVenCodeView = this.getView();
				$(document).ready(function ($) {
					$.ajax({
						method: "POST",
						url: "/aspsapapi/getVendorCode.do",
						contentType: "application/json",
						data: JSON.stringify(oVenCodePayload)
					}).done(function (data, status, jqXHR) {
						if (Array.isArray(data.resp)) {
							data.resp.unshift({
								vendorCode: "All"
							})
							oSelVenCodeMdl.setSizeLimit(data.resp.length);
							oSelVenCodeMdl.setData(data.resp);
							oSelVenCodeView.byId("idEmailVenCode").setModel(oSelVenCodeMdl, "vendorCodeData");
						}
					}).fail(function (jqXHR, status, err) {});
				});
			}
		},

		selectAllEmail: function (evt) {

			var vKey = evt.getParameter("changedItem").getKey(),
				vStats = evt.getParameter("selected");

			if (vKey.toLocaleLowerCase() === "all") {
				evt.getSource().setSelectedItems(vStats ? evt.getSource().getItems() : null);

			} else {
				var aSelectedKey = evt.getSource().getSelectedKeys(),
					aKeys = aSelectedKey.map(function (value) {
						return value.toLowerCase();
					}),
					vIdx = aKeys.indexOf("all");
				if (vIdx > -1) {
					aSelectedKey.splice(vIdx, 1);
				}
				if (aSelectedKey.length === evt.getSource().getItems().length - 1) {
					evt.getSource().setSelectedItems(evt.getSource().getItems());
				} else if (aSelectedKey.length === 0) {
					evt.getSource().setSelectedKeys(aSelectedKey);
				} else {
					evt.getSource().setSelectedItems(evt.getSource().getSelectedItems());
					//evt.getSource().setSelectedKeys(aSelectedKey);
				}
			}
		},
		onSelectChange: function (oEvent) {

			var arr = [],
				arr1 = [];
			var selectedIndices = oEvent.getSource().getSelectedIndices();

			if (oEvent.getParameters().selectAll === undefined && selectedIndices.length === 0) {
				for (var i = 0; i < oEvent.getSource().getRows().length; i++) {
					oEvent.getSource().getRows()[i].getCells()[3].setSelectedKeys("");
					oEvent.getSource().getRows()[i].getCells()[4].setSelectedKeys("");
				}
				/*oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[3].setSelectedKeys("");
				oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[4].setSelectedKeys("");*/
				this.onRefrshEmailDD('S');
			} else {
				oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[3].setSelectedKeys("");
				oEvent.getSource().getRows()[oEvent.getParameters().rowIndex].getCells()[4].setSelectedKeys("");
			}

			/*	if (!selectedIndices.includes(oEvent.getParameters().rowIndex) && oEvent.getParameters().selectAll === undefined) {
					return;
				}*/

			for (var index = 0; index < selectedIndices.length; index++) {
				//oEvent.getSource().getRows()[selectedIndices[index]].getCells()[3].setEnabled(true);
				//oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].setEnabled(true);
				var secData = oEvent.getSource().getRows()[selectedIndices[index]].getCells()[3].getModel("emailCommPopupData").getData().resp[
					selectedIndices[index]].secondaryEmailIds;
				//secData.shift();
				for (var i = 0; i < secData.length; i++) {
					arr.push(secData[i].emailId);
				}

				var cerData = oEvent.getSource().getRows()[selectedIndices[index]].getCells()[3].getModel("emailCommPopupData").getData().resp[
					selectedIndices[index]].recipientEmailIds;
				//cerData.shift();
				for (var j = 0; j < cerData.length; j++) {
					arr1.push(cerData[j].emailId);
				}
				oEvent.getSource().getRows()[selectedIndices[index]].getCells()[3].setSelectedKeys(arr);
				oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].setSelectedKeys(arr1);
				//oEvent.getSource().getRows()[selectedIndices[index]].getCells()[3].setModel(new JSONModel(secData), "sec");
				//oEvent.getSource().getRows()[selectedIndices[index]].getCells()[4].setModel(new JSONModel(cerData), "one");
			}
		},
		//########### EOC -Email-- Vendor Communication by Rakesh on 19.11.2020

		//########### code Started by sarvmangla for Gstr 2A new Screen 27.11.2020

		onDownloadnewGet2aReport: function (oEvent, item, view) {
			// if (view === "P") {
			var oSelectedItem = this.getView().byId("idgetVtablegstr6progstr2New").getSelectedIndices();
			var oModelForTab1 = this.byId("idgetVtablegstr6progstr2New").getModel("Gstr2ANewPRSumData").getData().resp;
			if (oSelectedItem.length == 0) {
				MessageBox.warning("Select at least one GSTN");
				return;
			}
			var aMonth = [];
			var oMonthData = this.getView().getModel("month").getData();

			if (oMonthData.aprFlag) {
				aMonth.push("04");
			}
			if (oMonthData.mayFlag) {
				aMonth.push("05");
			}
			if (oMonthData.junFlag) {
				aMonth.push("06");
			}
			if (oMonthData.julFlag) {
				aMonth.push("07");
			}
			if (oMonthData.augFlag) {
				aMonth.push("08");
			}
			if (oMonthData.sepFlag) {
				aMonth.push("09");
			}
			if (oMonthData.octFlag) {
				aMonth.push("10");
			}
			if (oMonthData.novFlag) {
				aMonth.push("11");
			}
			if (oMonthData.decFlag) {
				aMonth.push("12");
			}
			if (oMonthData.janFlag) {
				aMonth.push("01");
			}
			if (oMonthData.febFlag) {
				aMonth.push("02");
			}
			if (oMonthData.marFlag) {
				aMonth.push("03");
			}
			var postData = {
				"req": {
					"type": item.getKey(),
					"entityId": [$.sap.entityID],
					"fy": this.byId("dtFinYearGstrNew").getSelectedKey(),
					"month": aMonth,
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			}

			for (var i = 0; i < oSelectedItem.length; i++) {
				postData.req.dataSecAttrs.GSTIN.push(oModelForTab1[oSelectedItem[i]].gstin);
			}

			// this.excelDownload(postData, "/aspsapapi/gstr2ACompleteReportsDownload.do");
			this.reportDownload(postData, "/aspsapapi/gstr2ACompleteReportsDownload.do");
		},
		onPressGetGstr2anew: function (view) {
			var oSelectedItem = this.getView().byId("idgetVtablegstr6progstr2New").getSelectedIndices();
			var oModelForTab1 = this.byId("idgetVtablegstr6progstr2New").getModel("Gstr2ANewPRSumData").getData().resp;
			if (oSelectedItem.length == 0) {
				MessageBox.warning("Select at least one GSTN");
				return;
			}

			var aMonth = [];
			var oMonthData = this.getView().getModel("month").getData();

			if (oMonthData.aprFlag) {
				aMonth.push("04");
			}
			if (oMonthData.mayFlag) {
				aMonth.push("05");
			}
			if (oMonthData.junFlag) {
				aMonth.push("06");
			}
			if (oMonthData.julFlag) {
				aMonth.push("07");
			}
			if (oMonthData.augFlag) {
				aMonth.push("08");
			}
			if (oMonthData.sepFlag) {
				aMonth.push("09");
			}
			if (oMonthData.octFlag) {
				aMonth.push("10");
			}
			if (oMonthData.novFlag) {
				aMonth.push("11");
			}
			if (oMonthData.decFlag) {
				aMonth.push("12");
			}
			if (oMonthData.janFlag) {
				aMonth.push("01");
			}
			if (oMonthData.febFlag) {
				aMonth.push("02");
			}
			if (oMonthData.marFlag) {
				aMonth.push("03");
			}

			var postData = {
				"req": []
			};
			if (aMonth.length == 0) {
				MessageBox.warning("Select at least one Tax Period");
				return;
			}

			for (var i = 0; i < oSelectedItem.length; i++) {
				postData.req.push({
					"gstin": oModelForTab1[oSelectedItem[i]].gstin,
					"finYear": this.byId("dtFinYearGstrNew").getSelectedKey(),
					"month": aMonth
				});

				// if (aMonth.length == 0) {
				// 	postData.req.push({
				// 		"gstin": oModelForTab1[oSelectedItem[i]].gstin,
				// 		"finYear": this.byId("dtFinYearGstrNew").getSelectedKey(),
				// 		"month": ""
				// 	});
				// } else {
				// 	for (var j = 0; j < aMonth.length; j++) {
				// 		postData.req.push({
				// 			"gstin": oModelForTab1[oSelectedItem[i]].gstin,
				// 			"finYear": this.byId("dtFinYearGstrNew").getSelectedKey(),
				// 			"month": aMonth[j]
				// 		});
				// 	}
				// }

				// postData.req.push({
				// 	"gstin": "33GSPTN0481G1ZA",
				// 	"finYear": "2017-18",
				// 	"month": "04"

				// 	// "taxPeriod": "",
				// 	// "gstin": oModelForTab1[oSelectedItem[i]].gstin,
				// 	// "fromPeriod": this.byId("idPFromtaxPeriod2A").getValue(),
				// 	// "toPeriod": this.byId("idPTotaxPeriod2A").getValue()
				// });
			}
			this.vPSFlag = "P";
			// } else {

			// }
			this.getGstr2AGstnGetSection(postData);

		},

		getGstr2AGstnGetSection: function (postData) {
			var oView = this.getView();
			var that = this;
			var aMockMessages = [];

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/Gstr2aGstnGetSection.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						for (var i = 0; i < data.resp.length; i++) {

							aMockMessages.push({
								type: 'Success',
								title: data.resp[i].gstin,
								gstin: data.resp[i].msg,
								active: true,
								icon: "sap-icon://message-success",
								highlight: "Success",
								info: "Success"
							});

						}
					}

					that.getView().setModel(new JSONModel(aMockMessages), "Msg");
					that.onDialogPress();

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Gstr2aGstnGetSection.do : Error");
				});
			});
		},

		onDialogPress: function () {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get GSTR-2A Status",
					content: new sap.m.List({
						items: {
							path: "Msg>/",
							template: new sap.m.StandardListItem({
								title: "{Msg>title}",
								description: "{Msg>gstin}",
								icon: "{Msg>icon}",
								highlight: "{Msg>highlight}",
								// info: "{Msg>info}",
								// infoState: "{Msg>highlight}",
								infoStateInverted: true
							})
						}
					}),
					endButton: new Button({
						text: "Close",
						press: function () {
							this.pressDialog.close();
							that.clearMonth()
							if (that.vPSFlag === "P") {
								that.getGstr2ANewProcessSumData();
							} else {
								// that._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		clearMonth: function () {
			var object = {
				"aprFlag": false,
				"mayFlag": false,
				"junFlag": false,
				"julFlag": false,
				"augFlag": false,
				"sepFlag": false,
				"octFlag": false,
				"novFlag": false,
				"decFlag": false,
				"janFlag": false,
				"febFlag": false,
				"marFlag": false,
			};
			this.getView().setModel(new JSONModel(object), "month");
		},

		getGstr2ANewProcessSumData: function () {
			return new Promise(function (resolve, reject) {
				var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys(),
					postData = {
						"req": {
							"entity": $.sap.entityID,
							"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
							"dataSecAttrs": {
								"GSTIN": aGstin.includes("All") ? [] : this.removeAll(aGstin)
							}
						}
					};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr2aProcess.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						if (!data.resp.length) {
							MessageBox.information("No Data");
							this.getView().setModel(new JSONModel(null), "Gstr2ANewPRSumData");
						} else {
							data.resp.sort(function (a, b) {
								return (a.gstin < b.gstin ? -1 : (a.gstin > b.gstin ? 1 : 0));
							});
							this.getView().setModel(new JSONModel(data), "Gstr2ANewPRSumData");
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel(null), "Gstr2ANewPRSumData");
						console.log("getGstr2aProcess: ", err);
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		getGstr2a2bLinkStats: function () {
			return new Promise(function (resolve, reject) {
				var aGstin = this.byId("slGet2aProcessGstinNew").getSelectedKeys(),
					postData = {
						"req": {
							"entityId": $.sap.entityID,
							"financialYear": this.oView.byId("dtFinYearGstrNew").getSelectedKey(),
							"dataSecAttrs": {
								"GSTIN": this.removeAll(aGstin)
							}
						}
					};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getGstr2a2bLinkingStatus.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status === "E") {
							this.setModel(new JSONModel([]), "Gstr2a2bLink");
							MessageBox.error(data.hdr.message);
							return;
						}
						this._bindGstr2a2bLinkStats(data);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_bindGstr2a2bLinkStats: function (data) {
			var aMonth = ["jan", "feb", "march", "april", "may", "june", "july", "aug", "sep", "oct", "nov", "dec"],
				aData = data.resp.apiGstinDetails.map(function (item) {
					var obj = {
						"gstin": item.gstin,
						"stateName": item.stateName,
						"authStatus": item.authStatus,
						"registrationType": item.registrationType
					};
					item.taxPeriodDetails.forEach(function (e) {
						var m = aMonth[+e.taxPeriod.substr(0, 2) - 1];
						obj[m + "Status"] = e.status;
						obj[m + "Timestamp"] = e.initiatedOn;
						obj[m + "L"] = e.linkedCount;
						obj[m + "NL"] = e.notLinkedCount;
						obj[m + "NF"] = e.gstr1NotFiledCount;
						obj[m + "CntVisi"] = (e.linkedCount === "0" || +e.linkedCount > 0 || e.notLinkedCount === "0" || +e.notLinkedCount > 0 ||
							e.gstr1NotFiledCount === "0" || +e.gstr1NotFiledCount > 0);
					});
					return obj;
				});
			this.setModel(new JSONModel(aData), "Gstr2a2bLink");
		},

		onSbChangeGstr2A: function (key) {
			this.byId("idCcGet2aProcessNew").setVisible(key === "getCall");
			this.byId("tabGstr2a2bLinkStats").setVisible(key === "linkStats");
		},

		onSearch2AnewGo: function () {
			var key = this.byId("sbGstr2a").getSelectedKey();
			switch (key) {
			case "getCall":
				this.getGstr2ANewProcessSumData();
				break;
			case "linkStats":
				this.getGstr2a2bLinkStats();
				break;
			}
		},

		//########### code ended by sarvmangla for Gstr 2A new Screen 27.11.2020
		//============================== Added by chaithra for Recon Summary Recon Type ========================//
		onRecontypeChangeSummary: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			var model = this.getView().getModel("onVisible").getData();
			var that = this;
			if (key === "2APR") {
				that.getView().getModel("Display").getData().Label2 = "2A/6A";
				that.getView().getModel("Display").getData().Criteria = "Criteria";
				this.onVisibleConfig();
			} else if (key === "EINVPR") {
				that.getView().getModel("Display").getData().Label2 = "Inward E-Inv";
				model.resp = true;
				this.getView().getModel("onVisible").refresh(true);
			} else {
				that.getView().getModel("Display").getData().Label2 = "2B";
				that.getView().getModel("Display").getData().Criteria = "Tax Period Base";
				model.resp = true;
				this.getView().getModel("onVisible").refresh(true);
				this.getView().byId("idSummaryBase").setSelectedKey("idSummaryBase");

			}
			that.getView().getModel("Display").refresh(true);
			if (model.resp === true || key === "2BPR") {
				debugger;
				this.onConfigid();
			} else {
				this.onReconSummaryGSTIN1();
			}
		},

		onChangeSummaryBase: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			var model = this.getView().getModel("onVisible").getData();
			if (key === "RequestIDBase") {
				model.resp = true;
				var oModelDisplay = this.getView().getModel("Display");
				oModelDisplay.getData().PR = true;
				oModelDisplay.getData().A2 = true;
				this.getView().byId("idCriteria").setSelectedKey("2APRtaxperiod")
				oModelDisplay.refresh(true);
				this.onConfigid();
			} else {
				this.onReconSummaryGSTIN1();
				model.resp = false;
			}
			this.getView().getModel("onVisible").refresh(true);
		},

		onRecontypeChange12: function (key) {
			this.byId("idReptType").setSelectedKeys([]);
			if (key.getSource().getSelectedKey() === "2A_PR") {
				this.getView().getModel("Display").getData().LabelVc = "2A/6A";
				this.getView().getModel("Display").getData().LabelVc1 = "2A";
			} else {
				this.getView().getModel("Display").getData().LabelVc = "2B";
				this.getView().getModel("Display").getData().LabelVc1 = "2B";
			}
			this.getView().getModel("Display").refresh(true);
		},

		//############################### Exlude Vendors ############################//
		//======================= Added by chaithra on 08/03/2021 ===================//
		fnExcludeVendor: function () {
			this.getView().byId("idExcludeVendor").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			this.onLoadVendorFilterList();
			this.onLoadVendorList();
		},
		onLoadVendorFilterList: function () {
			var that = this;
			var oView = this.getView();
			var oPayLoad = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getExcludedVendors.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length != 0) {
						data.resp.sort(function (a, b) {
							if (a.vendorGstin < b.vendorGstin) {
								return -1;
							} else if (a.vendorGstin > b.vendorGstin) {
								return 1;
							} else {
								return 0;
							}
						});
						data.resp.unshift({
							vendorGstin: "All"
						});
						that.getView().byId("id_VendorGStn").setModel(new JSONModel(data), "oVendorGstin");
						that.getView().byId("id_VendorGStn").getModel("oVendorGstin").refresh(true);
					} else {
						that.getView().byId("id_VendorGStn").setModel(new JSONModel(data), "oVendorGstin");
						that.getView().byId("id_VendorGStn").getModel("oVendorGstin").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onLoadVendorList: function () {
			var that = this;
			var oView = this.getView();
			var aGstin = that.byId("id_VendorGStn").getSelectedKeys();
			var oPayLoad = {
				"req": {
					"entityId": $.sap.entityID,
					"vendorGstin": aGstin.includes("All") ? [] : aGstin
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getVendorExclusionData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length != 0) {
						data.resp.vendorExcludedData.sort(function (a, b) {
							if (a.vendorGstin < b.vendorGstin) {
								return -1;
							} else if (a.vendorGstin > b.vendorGstin) {
								return 1;
							} else {
								return 0;
							}
						});
						that.getView().byId("idTabExcludeVendr").setModel(new JSONModel(data), "oExcludeVendors");
						that.getView().byId("idTabExcludeVendr").getModel("oExcludeVendors").refresh(true);
					} else {
						MessageBox.information("No data is available");
						that.getView().byId("idTabExcludeVendr").setModel(new JSONModel(data), "oExcludeVendors");
						that.getView().byId("idTabExcludeVendr").getModel("oExcludeVendors").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		fnDeleteVendrMastr: function () {
			var that = this;
			var aIndices = that.byId("idTabExcludeVendr").getSelectedIndices();
			var aTabData = that.byId("idTabExcludeVendr").getModel("oExcludeVendors").getData().resp.vendorExcludedData;
			if (aIndices.length === 0) {
				MessageBox.error("Select atleast one Vendor GSTIN");
				return;
			} else {
				var gstins = [];
				for (var i = 0; i < aIndices.length; i++) {
					gstins.push(aTabData[aIndices[i]].vendorGstin);
				}
				var PayLoad = {
					"req": {
						"entityId": $.sap.entityID,
						"vendorGstin": gstins,
					}
				};
				MessageBox.confirm("Do you want to Delete selected Vendors from 'Exlude Vendor GSTIN' master ?", {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oActionSuccess) {
						if (oActionSuccess === "YES") {
							sap.ui.core.BusyIndicator.show(0);
							var GstnsList = "/aspsapapi/deleteExcludedVendor.do";
							$(document).ready(function ($) {
								$.ajax({
									method: "POST",
									url: GstnsList,
									contentType: "application/json",
									data: JSON.stringify(PayLoad)
								}).done(function (data, status, jqXHR) {
									sap.ui.core.BusyIndicator.hide();
									if (data.hdr.status == "S") {
										MessageBox.success("Selected Vendors are deleted and same will be included in reconciliation process");
										that.onLoadVendorList();
									} else {
										MessageBox.information("While deleting selected vendors having issue");
										that.onLoadVendorList();
									}

								}).fail(function (jqXHR, status, err) {
									sap.ui.core.BusyIndicator.hide();
								});
							});
						}
					}
				});
			}
		},

		fnDownldVendrMastr: function () {
			var oDwnldTabDataVMReq = {
				"req": {
					"entityId": $.sap.entityID,
					"vendorGstin": this.removeAll(this.getView().byId("id_VendorGStn").getSelectedKeys()) //oVendorGstinArr //
				}
			};
			var oReqExcelVMPath = "/aspsapapi/getVendorExclusionReport.do";
			this.excelDownload(oDwnldTabDataVMReq, oReqExcelVMPath);
		},

		onExcludeVendorSearch: function (oEvt) {
			var oTable = this.byId("idTabExcludeVendr");
			var searchText = oEvt.getSource().getValue();
			var filters = [];
			if (searchText.trim() != '') {
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
					path: "excludeVendorRemarks",
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
				oTable.getBinding("rows").filter([], sap.ui.model.FilterType.Application);
			}
		},

		onPressExcludeVendorSrch: function () {
			this.onLoadVendorList();
		},

		onPressExcldVendorBack: function () {
			this.getView().byId("idExcludeVendor").setVisible(false);
			this.getView().byId("idSplitDtl2A").setVisible(true);
		},

		//======================= Ended by chaithra on 08/03/2021 ===================//
		//############################ Exclude Vendors ##############################//

		handleValueHelp: function () {

			if (!this.VendorPan) {
				this.VendorPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VendorPANF4", this);
				this.getView().addDependent(this.VendorPan);
				this.getView().setModel(new JSONModel({
					pageNumber: 1,
					pageSize: 1000
				}), "DialogProperty");
				this.panUpdatePaginatedModel({
					pageNumber: 1,
					pageSize: 1000
				});
				this.byId("searchId1").setValue();
			}
			this.VendorPan.open();

		},

		handleValueHelpRR: function () {
			this.VendorPanRR.open();
		},

		handleValueHelpRR2AB: function () {
			this.VendorPanRR2AB.open();
		},

		handleValueHelpvr: function () {

			if (!this.VRPan) {
				this.VRPan = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.VRPANF4", this);
				this.getView().addDependent(this.VRPan);

				this.VRPan.setModel(new JSONModel({
					pageNumber: 1,
					pageSize: 1000
				}), "vrDialogProperty");
				this.vrUpdatePaginatedModel({
					pageNumber: 1,
					pageSize: 1000
				});
				this.byId("searchIdVRP").setValue();
			}
			this.VRPan.open();

		},

		onCloseVendorPanPopup: function (oEvent) {

			this.Panarr = [];
			var Pan1 = [];
			var aRegPAN = this.byId("selectDialog").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.Panarr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				Pan1.push(obj);
			}
			var oModel = new JSONModel();
			oModel.setSizeLimit(Pan1.length);
			oModel.setData(Pan1);
			this.VendorPan.close();
			this.getView().setModel(oModel, "Token");
			this.onPrsEmailVenPAN();
			this.getView().setModel(new JSONModel([]), "TokenGSTN");
			this.byId("VGCheckId").setSelected(false);
		},

		onCloseRR: function (oEvent) {
			this.VendorPanRR.close();
			this.Panarr = [];
			var Pan1 = [];
			var aRegPAN = this.byId("selectDialogRR").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.Panarr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				Pan1.push(obj);
			}
			this.getView().setModel(new JSONModel(Pan1), "TokenRR");
			this.onPrsEmailVenPAN();
			this.getView().setModel(new JSONModel([]), "TokenGSTNRR");
			this.byId("VGCheckIdRR").setSelected(false);
		},

		onCloseRR2AB: function (oEvent) {
			this.VendorPanRR2AB.close();
			this.Panarr2AB = [];
			var Pan1 = [];
			var aRegPAN = this.byId("selectDialogRR2AB").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.Panarr2AB.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				Pan1.push(obj);
			}
			this.getView().setModel(new JSONModel(Pan1), "TokenRR2AB");
			this.onPrsEmailVenPAN2AB();
			this.getView().setModel(new JSONModel([]), "TokenGSTNRR2AB");
			this.byId("VGCheckIdRR2AB").setSelected(false);
		},

		onCloseVR: function (oEvent) {
			this.VRPan.close();
			this.Panarrvr = [];
			var Pan1 = [];
			var aRegPAN = this.byId("selectDialogvr").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.Panarrvr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				Pan1.push(obj);
			}
			var oModel = new JSONModel();
			oModel.setSizeLimit(Pan1.length);
			oModel.setData(Pan1);
			this.getView().setModel(oModel, "Tokenvr");
			this.onPrsVenPAN();
			this.getView().setModel(new JSONModel([]), "TokenGSTNvr");
			this.byId("VGCheckIdvr").setSelected(false);
		},

		onSelect: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialog").removeSelections();
			} else {
				this.byId("selectDialog").selectAll();
			}
		},

		onSelectRR: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogRR").removeSelections();
			} else {
				this.byId("selectDialogRR").selectAll();
			}
		},

		onSelectRR2AB: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogRR2AB").removeSelections();
			} else {
				this.byId("selectDialogRR2AB").selectAll();
			}
		},

		onSelectvr: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogvr").removeSelections();
			} else {
				this.byId("selectDialogvr").selectAll();
			}
		},

		onSearchGstinsPAN2AB: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogRR2AB");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogvr").removeSelections();
		},

		onSearchGstinsPANRR: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogRR");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogvr").removeSelections();
		},

		onSearchGstinsVendorPAN1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialog");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogvr").removeSelections();
		},
		onSearchGstinsVendorPAN: function (oEvent) {
			var sQuery = oEvent.getParameter("newValue").toLowerCase();

			// Get the original JSON data
			var oDataMain = this.getView().getModel("venPANDataMain").getProperty("/vendorPans");
			var oModel = this.getView().getModel("venPANData");
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

			this.byId("PANCheckId").setSelected(false);
			var aFullData = this.getView().getModel("venPANDataMain").getProperty("/vendorPans");
			// oProp = oDialog.getModel("DialogProperty").getProperty('/');

			// Calculate Pagination
			var iStartIndex = (oProp.pageNumber - 1) * oProp.pageSize;
			var iEndIndex = iStartIndex + oProp.pageSize;
			var aPaginatedData = aFullData.slice(iStartIndex, iEndIndex);

			// Set a New JSON Model for Paginated Data
			var oPaginatedModel = new JSONModel();
			oPaginatedModel.setSizeLimit(aPaginatedData.length);
			oPaginatedModel.setData(aPaginatedData);
			this.getView().setModel(oPaginatedModel, "venPANData");

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
			var totalItems = this.getView().getModel("venPANDataMain").getProperty("/vendorPans").length;
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

		onSearchGstinsPAN: function (oEvent) {
			var sQuery = oEvent.getParameter("newValue").toLowerCase();

			// Get the original JSON data
			var oDataMain = this.getView().getModel("venPANDataMain").getProperty("/vendorPans");
			var oModel = this.getView().getModel("vrPANData");
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
		vrUpdatePaginatedModel: function (oProp) {

			this.byId("PANCheckIdvr").setSelected(false);
			var aFullData = this.getView().getModel("venPANDataMain").getProperty("/vendorPans");
			// oProp = oDialog.getModel("DialogProperty").getProperty('/');

			// Calculate Pagination
			var iStartIndex = (oProp.pageNumber - 1) * oProp.pageSize;
			var iEndIndex = iStartIndex + oProp.pageSize;
			var aPaginatedData = aFullData.slice(iStartIndex, iEndIndex);

			// Set a New JSON Model for Paginated Data
			var oPaginatedModel = new JSONModel();
			oPaginatedModel.setSizeLimit(aPaginatedData.length);
			oPaginatedModel.setData(aPaginatedData);
			this.getView().setModel(oPaginatedModel, "vrPANData");

			var totalPage = aFullData.length / oProp.pageSize;
			if (aFullData.length % oProp.pageSize) {
				totalPage = totalPage + 1;
			}
			var tatal = oProp.pageNumber + " / " + parseInt(totalPage);
			this.byId("idVRTotal").setText(tatal);
		},

		// Load Next Page
		onVRLoadMore: function (oEvent) {

			var pageData = oEvent.getSource().getModel("vrDialogProperty").getProperty("/");
			var totalItems = this.getView().getModel("venPANDataMain").getProperty("/vendorPans").length;
			var totalPages = Math.ceil(totalItems / pageData.pageSize);

			if (pageData.pageNumber < totalPages) {
				pageData.pageNumber++;
				this.vrUpdatePaginatedModel(pageData);
			}
		},

		// Load Previous Page
		onVRLoadPrevious: function (oEvent) {

			var pageData = oEvent.getSource().getModel("vrDialogProperty").getProperty("/");
			if (pageData.pageNumber > 1) {
				pageData.pageNumber--;
				this.vrUpdatePaginatedModel(pageData);
			}
		},

		handleValueHelp1: function () {
			this.VenGSTIN.open();
			this.onPrsEmailVenPAN();
		},

		handleValueHelpRR1: function () {
			this.VenGSTINRR.open();
		},

		handleValueHelpRR12AB: function () {
			this.VenGSTINRR2AB.open();
		},

		handleValueHelpvrgstin: function () {
			this.VrGSTIN.open();
			this.onPrsVenPAN();
		},

		onCloseGSTIN: function (oEvent) {
			this.VenGSTIN.close();
			this.GSTINarr = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("selectDialogGSTIN").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.GSTINarr.push(aRegPAN[i].getTitle());
				var obj = {
					"gstin": aRegPAN[i].getTitle()
				}
				GSTIN1.push(obj);
			}
			var oModel = new JSONModel();
			oModel.setSizeLimit(GSTIN1.length);
			oModel.setData(GSTIN1);
			this.getView().setModel(oModel, "TokenGSTN");
			this.onPrsEmailVenGSTIN();
		},

		onCloseGSTINRR: function (oEvent) {
			this.VenGSTINRR.close();
			this.GSTINarr = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("selectDialogGSTINRR").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.GSTINarr.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				GSTIN1.push(obj);
			}
			this.getView().setModel(new JSONModel(GSTIN1), "TokenGSTNRR");
		},

		onCloseGSTINRR2AB: function (oEvent) {
			this.VenGSTINRR2AB.close();
			this.GSTINarr2AB = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("selectDialogGSTINRR2AB").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.GSTINarr2AB.push(aRegPAN[i].getTitle());
				var obj = {
					"pan": aRegPAN[i].getTitle()
				}
				GSTIN1.push(obj);
			}
			this.getView().setModel(new JSONModel(GSTIN1), "TokenGSTNRR2AB");
		},

		onCloseGSTINvr: function (oEvent) {
			this.VrGSTIN.close();
			this.GSTINarrvr = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("selectDialogGSTINvr").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.GSTINarrvr.push(aRegPAN[i].getTitle());
				var obj = {
					"gstin": aRegPAN[i].getTitle()
				}
				GSTIN1.push(obj);
			}
			var oModel = new JSONModel();
			oModel.setSizeLimit(GSTIN1.length);
			oModel.setData(GSTIN1);
			this.getView().setModel(oModel, "TokenGSTNvr");
		},

		onSelectGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogVMGSTIN").removeSelections();
			} else {
				this.byId("selectDialogVMGSTIN").selectAll();
			}
		},

		onSelectGSTINE: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogGSTIN").removeSelections();
			} else {
				this.byId("selectDialogGSTIN").selectAll();
			}
		},

		onSelectGSTINERR: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogGSTINRR").removeSelections();
			} else {
				this.byId("selectDialogGSTINRR").selectAll();
			}
		},

		onSelectGSTINERR2AB: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogGSTINRR2AB").removeSelections();
			} else {
				this.byId("selectDialogGSTINRR2AB").selectAll();
			}
		},

		onSelectGSTINVr: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogGSTINvr").removeSelections();
			} else {
				this.byId("selectDialogGSTINvr").selectAll();
			}
		},

		onSearchGstins1Vendor: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogGSTIN");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogGSTINvr").removeSelections();
		},

		onSearchGstins1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogGSTINvr");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogGSTINvr").removeSelections();
		},

		onSearchGstins1RR: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogGSTINRR");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogGSTINvr").removeSelections();
		},

		handleValueHelpVM: function () {
			this.VenVMGSTIN.open();
		},

		onCloseVMGSTIN: function (oEvent) {

			this.VenVMGSTIN.close();
			this.VMGSTINarr = [];
			var GSTIN1 = [];
			var aRegPAN = this.byId("selectDialogVMGSTIN").getSelectedItems();
			for (var i = 0; i < aRegPAN.length; i++) {
				this.VMGSTINarr.push(aRegPAN[i].getTitle());
				var obj = {
					"gstin": aRegPAN[i].getTitle()
				}
				GSTIN1.push(obj);
			}
			var oModel = new JSONModel();
			oModel.setSizeLimit(GSTIN1.length);
			oModel.setData(GSTIN1);
			this.getView().setModel(oModel, "TokenGSTNVM");
		},

		onSelectGSTINVM: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.byId("selectDialogVMGSTIN").removeSelections();
			} else {
				this.byId("selectDialogVMGSTIN").selectAll();
			}
		},

		onSearchGstinsVM1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("vendorGstin", FilterOperator.Contains, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("selectDialogVMGSTIN");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			// this.getView().byId("selectDialogVMGSTIN").removeSelections();
		},

		onSearchGstinsVM: function (oEvent) {

			var sQuery = oEvent.getParameter("newValue").toLowerCase();

			// Get the original JSON data
			var oDataMain = this.getView().getModel("venGstinDataMain").getProperty("/");
			var oModel = this.getView().getModel("venGstinData");
			var aData = oModel.getProperty("/"); // Original Data

			// If no query, reset table to original data
			if (!sQuery) {
				oModel.setProperty("/", oDataMain);
				return;
			}

			// Filter the data manually
			var aFilteredData = oDataMain.filter(function (oItem) {
				return oItem.vendorGstin.toLowerCase().includes(sQuery);
			});

			// Set the new filtered data to the model
			oModel.setProperty("/", aFilteredData);
		},
		updatePaginatedModel: function () {

			var oModel = this.getView().getModel("venGstinDataMain");
			var aFullData = oModel.getProperty("/");

			// Calculate Pagination
			var iStartIndex = (this.pageNumber - 1) * this.pageSize;
			var iEndIndex = iStartIndex + this.pageSize;
			var aPaginatedData = aFullData.slice(iStartIndex, iEndIndex);

			// Set a New JSON Model for Paginated Data
			var oPaginatedModel = new JSONModel();
			oPaginatedModel.setSizeLimit(aPaginatedData.length);
			oPaginatedModel.setData(aPaginatedData);
			this.getView().setModel(oPaginatedModel, "venGstinData");

			var totalPage = aFullData.length / this.pageSize;
			if (aFullData.length % this.pageSize) {
				totalPage = totalPage + 1;
			}
			var tatal = this.pageNumber + " / " + parseInt(totalPage)
			this.byId("idtotal").setText(tatal);

		},

		// Load Next Page
		onLoadMore: function () {
			var oModel = this.getView().getModel("venGstinDataMain");
			var totalItems = oModel.getProperty("/").length;
			var totalPages = Math.ceil(totalItems / this.pageSize);

			if (this.pageNumber < totalPages) {
				this.pageNumber++;
				this.updatePaginatedModel();
			}
		},

		// Load Previous Page
		onLoadPrevious: function () {
			if (this.pageNumber > 1) {
				this.pageNumber--;
				this.updatePaginatedModel();
			}
		},

		pressDR: function (oEvt) {
			var oButton = oEvt.getSource();
			if (!this._oGstinPopover9) {
				this._oGstinPopover9 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.PopoverVCE", this);
				this.getView().addDependent(this._oGstinPopover9);
			}
			this._oGstinPopover9.openBy(oButton);
		},
		pressEmail: function (oEvt) {
			var oButton = oEvt.getSource();
			if (!this._oGstinPopover126) {
				this._oGstinPopover126 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.PopoverVCE1", this);
				this.getView().addDependent(this._oGstinPopover126);
			}
			this._oGstinPopover126.openBy(oButton);
		},

		onVenApiPushRe: function () {
			this.onVenApiPush();
		},

		onVenApiPush: function (vPageNo) {
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/vendorApiStatus.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": vCount
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: onicPath,
					contentType: "application/json",
					data: JSON.stringify(searchInfo)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that._nicPagination1(data.hdr);
					var oSrNo = data.resp;
					for (var i = 0; i < oSrNo.length; i++) {
						oSrNo[i].SrNumber = i + 1;
					}
					onicCrdMdl.setData(data);
					that.getView().setModel(onicCrdMdl, "vendorApiStatus");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		////////Pagination/////////////
		_nicPagination1: function (header) {
			var pageNumber = Math.ceil((header.totalCount || 0) / vCount);
			this.byId("txtPageNoVC1").setText("/ " + pageNumber);
			this.byId("inPageNoVC1").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoVC1").setValue(pageNumber);
				this.byId("inPageNoVC1").setEnabled(false);
				this.byId("btnPrevVC1").setEnabled(false);
				this.byId("btnNextVC1").setEnabled(false);
				this.byId("btnFirstVC1").setEnabled(false);
				this.byId("btnLastVC1").setEnabled(false);
			} else if (this.byId("inPageNoVC1").getValue() === "" || this.byId("inPageNoVC1").getValue() === "0") {
				this.byId("inPageNoVC1").setValue(1);
				this.byId("inPageNoVC1").setEnabled(true);
				this.byId("btnPrevVC1").setEnabled(false);
				this.byId("btnFirstVC1").setEnabled(false);
			} else {
				this.byId("inPageNoVC1").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoVC1").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextVC1").setEnabled(true);
				this.byId("btnLastVC1").setEnabled(true);
			} else {
				this.byId("btnNextVC1").setEnabled(false);
				this.byId("btnLastVC1").setEnabled(false);
			}
			this.byId("btnPrevVC1").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstVC1").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPaginationVC1: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoVC1").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevVC1")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevVC1").setEnabled(false);
				}
				if (!this.byId("btnNextVC1").getEnabled()) {
					this.byId("btnNextVC1").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextVC")) {
				var vPageNo = parseInt(this.byId("txtPageNoVC1").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevVC1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextVC1").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstVC1")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstVC1").setEnabled(false);
				}
				if (!this.byId("btnLastVC1").getEnabled()) {
					this.byId("btnLastVC1").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLastVC1")) {
				vValue = parseInt(this.byId("txtPageNoVC1").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstVC1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastVC1").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNoVC1").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoVC1").setValue(vValue);
			this.onVenApiPush(vValue);
		},

		onSubmitPaginationVC1: function () {
			var vPageNo = this.byId("inPageNoVC1").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoVC1").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onVenApiPush(vPageNo);
		},

		onPrsFileStatusErrDownload1: function (oEvent) {
			var oErrorFlagId = oEvent.getSource().getEventingParent().getParent().getBindingContext("vendorApiStatus").getObject().refId;
			var oVenUpldErrPayload = {
				"refId": oErrorFlagId,
				"flagofRecord": "errorrecords"
			};
			var vUrlErr = "/aspsapapi/getVendorMasterErrorReport.do";
			this.excelDownload(oVenUpldErrPayload, vUrlErr);
		},

		onPrsFileStatusInformDwnld1: function (oEvent) {
			var oInfoFlagId = oEvent.getSource().getEventingParent().getParent().getBindingContext("vendorApiStatus").getObject().refId;
			var oVenUpldPayload = {
				"refId": oInfoFlagId,
				"flagofRecord": "informationrecords"
			};
			var vUrl = "/aspsapapi/getVendorMasterErrorReport.do";
			this.excelDownload(oVenUpldPayload, vUrl);
		},

		onVendorResponse: function (vPageNo) {
			this.getView().setModel(new JSONModel([]), "Tokenvr");
			if (this.byId("selectDialogGSTINvr") !== undefined) {
				this.getView().byId("selectDialogGSTINvr").setModel(new JSONModel([]), "EmailvenGstinDatavr");
				this.getView().setModel(new JSONModel([]), "TokenGSTNvr");
				this.getView().byId("selectDialogGSTINvr").getModel("EmailvenGstinDatavr").refresh(true);
			}
			this.byId("idVRdate").setValue(null);
			this.byId("idReptTypeR").setSelectedKeys();
			var that = this;
			this.identifier = "E";
			if (typeof vPageNo === "object") {
				vPageNo = 0;
			}
			var onVenMstrInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
					"pageSize": vCount1,
				},
				"req": {
					"identifier": "E"
				}
			};
			this.onVendorResponse12(onVenMstrInfo);
		},

		onVendorResponseGo: function (vPageNo) {
			var that = this;
			var oVendorPanArr = [];
			if (this.Panarrvr !== undefined) {
				for (var j = 0; j < this.Panarrvr.length; j++) {
					oVendorPanArr.push({
						gstin: this.Panarrvr[j],
					});
				}
			}

			var oVendorGstinArr = [];
			if (this.GSTINarrvr !== undefined) {
				for (var j = 0; j < this.GSTINarrvr.length; j++) {
					oVendorGstinArr.push({
						gstin: this.GSTINarrvr[j],
					});
				}
			}
			if (typeof vPageNo === "object") {
				vPageNo = 0;
			}
			this.identifier = "F";
			var onVenMstrInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
					"pageSize": vCount1,
				},
				"req": {
					"identifier": "F",
					"entityId": Number($.sap.entityID),
					"vendorPans": oVendorPanArr,
					"vendorGstins": oVendorGstinArr,
					"status": this.byId("idReptTypeR").getSelectedKeys(),
					"respDate": this.byId("idVRdate").getValue(),
				}
			};
			this.onVendorResponse12(onVenMstrInfo);
		},

		onVendorResp: function (oEvt, vPageNo) {
			this.byId("idsbFileStatus").setSelectedKey("VendorResponse");
			this.oView.byId("idVenMstrDwnldChartCon").setVisible(false);
			this.oView.byId("idEmailChartCon").setVisible(false);
			this.oView.byId("idVR").setVisible(true);
			//Filter Bar
			this.oView.byId("idFltrBarVendrMstr").setVisible(false);
			this.oView.byId("idFltrBarVendrEmail").setVisible(false);
			this.oView.byId("fbVenMstrHbox").setVisible(false);
			this.oView.byId("fbGenReptHbox").setVisible(false);

			this.oView.byId("idFltrBarVendrResp").setVisible(true);
			this.oView.byId("fbGenReptHboxR").setVisible(true);
			this.oView.byId("idheaderVendor").setVisible(true);
			//Tables
			this.oView.byId("idVenUpldTabl").setVisible(false);
			this.oView.byId("idMasterVendor").setVisible(false);
			this.oView.byId("idVenUpldTabl2").setVisible(false);
			this.oView.byId("toolId").setVisible(false);
			this.getView().setModel(new JSONModel([]), "Tokenvr");
			if (this.byId("selectDialogGSTINvr") !== undefined) {
				this.getView().byId("selectDialogGSTINvr").setModel(new JSONModel([]), "EmailvenGstinDatavr");
				this.getView().setModel(new JSONModel([]), "TokenGSTNvr");
				this.getView().byId("selectDialogGSTINvr").getModel("EmailvenGstinDatavr").refresh(true);
			}
			this.byId("idVRdate").setValue(null);
			this.byId("idReptTypeR").setSelectedKeys();
			// this.onVRPAN();
			//var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("EmailTableData").getObject().filePath;
			this.filePath = oEvt.getSource().getEventingParent().getBindingContext("EmailTableData").getObject().requestId; //"481"; //
			if (typeof vPageNo === "object") {
				vPageNo = 0;
			}
			this.identifier = "R";
			var onVenMstrInfo = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
					"pageSize": vCount1,
				},
				"req": {
					"identifier": "R",
					"reqId": this.filePath
				}
			};
			this.onVendorResponse12(onVenMstrInfo);
		},

		onRefresh: function (vPageNo) {
			if (this.identifier === "R") {
				if (typeof vPageNo === "object") {
					vPageNo = 0;
				}
				var onVenMstrInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0), //vPageNo - 1, //(vPageNo ? 0 : -1),
						"pageSize": vCount1,
					},
					"req": {
						"identifier": "R",
						"reqId": this.filePath
					}
				};
				this.onVendorResponse12(onVenMstrInfo);
			} else if (this.identifier === "E") {
				this.onVendorResponse();
			} else if (this.identifier === "F") {
				this.onVendorResponseGo();
			}
		},

		onVendorResponse12: function (onVenMstrInfo) {
			var onVenMstrModel = new JSONModel();
			var onVenMstrView = this.getView();
			var onVenMstrPath = "/aspsapapi/vendorResponseRequestData.do";
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: onVenMstrPath,
					contentType: "application/json",
					data: JSON.stringify(onVenMstrInfo)
				}).done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						that._venMstrPaginationVR(data.hdr);
						onVenMstrModel.setData(data.resp);
						that.getView().setModel(onVenMstrModel, "VendorResp");
					} else {
						that.getView().setModel(new JSONModel([]), "VendorResp");
						that.getView().getModel("VendorResp").refresh(true);
					}
				}).fail(function (jqXHR, status, err) {});
			});
		},

		////////BO Pagination/////////////  --- 12.11.20202
		_venMstrPaginationVR: function (header) {
			var pageNumber = Math.ceil(header.totalCount / vCount1);
			this.byId("txtPageNoVR").setText("/ " + pageNumber);
			this.byId("inPageNoVR").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoVR").setValue(pageNumber);
				this.byId("inPageNoVR").setEnabled(false);
				this.byId("btnPrevVR").setEnabled(false);
				this.byId("btnNextVR").setEnabled(false);
				this.byId("btnFirstVR").setEnabled(false);
				this.byId("btnLastVR").setEnabled(false);
			} else if (this.byId("inPageNoVR").getValue() === "" || this.byId("inPageNoVR").getValue() === "0") {
				this.byId("inPageNoVR").setValue(1);
				this.byId("inPageNoVR").setEnabled(true);
				this.byId("btnPrevVR").setEnabled(false);
				this.byId("btnFirstVR").setEnabled(false);
			} else {
				this.byId("inPageNoVR").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoVR").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextVR").setEnabled(true);
				this.byId("btnLastVR").setEnabled(true);
			} else {
				this.byId("btnNextVR").setEnabled(false);
				this.byId("btnLastVR").setEnabled(false);
			}
			this.byId("btnPrevVR").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstVR").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPaginationVR: function (oEvent) {
			var that = this;
			var vValue = parseInt(this.byId("inPageNoVR").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevVR")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevVR").setEnabled(false);
				}
				if (!this.byId("btnNextVR").getEnabled()) {
					this.byId("btnNextVR").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextVR")) {
				var vPageNo = parseInt(this.byId("txtPageNoVR").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevVR").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextVR").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstVR")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstVR").setEnabled(false);
				}
				if (!this.byId("btnLastVR").getEnabled()) {
					this.byId("btnLastVR").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnLastVR")) {
				vValue = parseInt(this.byId("txtPageNoVR").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstVR").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastVR").setEnabled(false);
				}
			} else {
				vPageNo = parseInt(this.byId("txtPageNoVR").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoVR").setValue(vValue);
			this.onPrsSearchVenMstr(vValue);
		},

		onSubmitPaginationVR: function () {
			var vPageNo = this.byId("inPageNoVR").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoVR").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.onPrsSearchVenMstr(vPageNo);
		},

		onDownloadReportRV: function (oEvt) {
			var filePath = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().respDowldPath;
			var reqId = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().reqId;
			var vendrGstin = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().vendorGstin;
			var REQ = {
				"req": {
					"filePath": filePath,
					"identifier": "Resp",
					"reqId": reqId,
					"vendrGstin": vendrGstin
				}
			}
			var vUrl = "/aspsapapi/vendr2wayRespFileDownload.do";
			this.excelDownload(REQ, vUrl);
			this.onRefresh();
		},

		onDownloadReportDRR: function (oEvt) {
			var filePath = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().totalRespDowldPath;
			var reqId = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().reqId;
			var vendrGstin = oEvt.getSource().getEventingParent().getBindingContext("VendorResp").getObject().vendorGstin;
			var REQ = {
				"req": {
					"filePath": filePath,
					"identifier": "",
					"reqId": reqId,
					"vendrGstin": vendrGstin
				}
			}
			var vUrl = "/aspsapapi/vendr2wayRespFileDownload.do";
			this.excelDownload(REQ, vUrl);
			this.onRefresh();
		},

		onRecontypeChangeRR: function (oEvent) {
			var oGrpPermision = this.getOwnerComponent().getModel("GroupPermission").getProperty("/"),
				oModel = this.getView().getModel("Display"),
				recKey = this.byId("sRecResultCriteria").getSelectedKey(),
				key = oEvent.getSource().getSelectedKey();

			oModel.setProperty("/LabelRR", (key === "2A_PR" ? "2A/6A" : "2B"));
			oModel.setProperty("/LabelRR1", (key === "2A_PR" ? "2A" : "2B"));
			oModel.setProperty("/imsResponse", recKey === "Regular" && !!oGrpPermision.GR1 &&
				(key === "2B_PR" ? !!oGrpPermision.GR2 : !!oGrpPermision.GR4));
			oModel.refresh(true);
			this.onReconResultCriteria(recKey);
		},

		onRecontypeChangeRRe: function (oEvent) {
			var oModel = this.getView().getModel("Display"),
				recKey = this.byId("sRecResCriteria").getSelectedKey(),
				key = oEvent.getSource().getSelectedKey();

			oModel.setProperty("/LabelRRe", (key === "2A_PR" ? "2A/6A" : "2B"));
			oModel.setProperty("/LabelRRe1", (key === "2A_PR" ? "2A" : "2B"));
			oModel.refresh(true);
			this.onReconResponseCriteria(recKey);
		},

		XLDownload: function () {
			var oCashTabData = this.getView().getModel("VendorResp").getData();
			var oCashTabModel = new sap.ui.model.json.JSONModel();
			oCashTabModel.setData(oCashTabData);
			var oExport = new Export({
				exportType: new ExportTypeCSV({
					separatorChar: "\t",
					mimeType: "application/vnd.ms-excel",
					charset: "utf-8",
					fileExtension: "xls"
				}),

				models: oCashTabModel,

				rows: {
					path: "/"
				},

				columns: [{
					name: "Date",
					template: {
						content: "{date}"
					}
				}, {
					name: "Vendor PAN",
					template: {
						content: "{vendorPan}"
					}
				}, {
					name: "Vendor GSTIN",
					template: {
						content: "{vendorGstin}"
					}
				}, {
					name: "Vendor Name",
					template: {
						content: "{vendorName}"
					}
				}, {
					name: "Request Id",
					template: {
						content: "{reqId}"
					}
				}, {
					name: "Total Records",
					template: {
						content: "{totalRec}"
					}
				}, {
					name: "Tax Period",
					template: {
						content: "{taxPeriod}"
					}
				}, {
					name: "Response Records",
					template: {
						content: "{respRecords}"
					}
				}, {
					name: "Status",
					template: {
						content: "{status}"
					}
				}]
			});
			oExport.saveFile().catch(function (oError) {
				MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});
		},

		onPressAdFilter: function () {
			var key = this.byId("sRecResCriteria").getSelectedKey(),
				aItem = this.byId("fbRespPR").getFilterGroupItems();
			if (key === "Import") {
				aItem[1].setLabel("From BOE Date")
				aItem[2].setLabel("To BOE Date")
			} else {
				aItem[1].setLabel("From Doc Date");
				aItem[2].setLabel("To Doc Date");
			}
			this.ReconRspAdd.open();
			this.pos();
		},

		onPressFilterCloseRR: function () {
			this.ReconRspAdd.close();
		},

		onPressAdFilter2AB: function () {
			var key = this.byId("sRecResCriteria").getSelectedKey(),
				aItem = this.byId("fbResp2AB").getFilterGroupItems();
			if (key === "Import") {
				aItem[1].setLabel("From BOE Date")
				aItem[2].setLabel("To BOE Date")
			} else {
				aItem[1].setLabel("From Doc Date");
				aItem[2].setLabel("To Doc Date");
			}
			this.ReconRspAdd2AB.open();
			this.pos1();
		},

		onPressFilterCloseRR2AB: function () {
			this.ReconRspAdd2AB.close();
		},

		_getRespRegularReport: function (key, text) {
			return [{
				key: "All",
				text: "All"
			}, {
				key: "Value Mismatch",
				text: "Value Mismatch"
			}, {
				key: "POS Mismatch",
				text: "POS Mismatch"
			}, {
				key: "Doc Date Mismatch",
				text: "Doc Date Mismatch"
			}, {
				key: "Doc Type Mismatch",
				text: "Doc Type Mismatch"
			}, {
				key: "Doc No Mismatch I",
				text: "Doc No Mismatch I"
			}, {
				key: "Doc No Mismatch II",
				text: "Doc No Mismatch II"
			}, {
				key: "Doc No & Doc Date Mismatch",
				text: "Doc No & Doc Date Mismatch"
			}, {
				key: "Multi-Mismatch",
				text: "Multi-Mismatch"
			}, {
				key: "Potential-I",
				text: "Potential I"
			}, {
				key: "Potential-II",
				text: "Potential II"
			}, {
				key: "Logical Match",
				text: "Logical Match"
			}, {
				key: "Addition in " + key,
				text: "Addition in " + text
			}];
		},

		_getRespImportReport: function (key, text) {
			return [{
				key: "All",
				text: "All"
			}, {
				key: "Mismatch IMPG",
				text: "Import - Mismatch"
			}, {
				key: "Addition in " + key + " IMPG",
				text: "Import - Addition in " + text
			}];
		},

		onReconResponseCriteria: function (key) {
			var obj = this.getView().getModel("Display").getProperty("/"),
				aItemPr = this.byId("fbRecRespPR").getFilterGroupItems(),
				aItem2AB = this.byId("fbRecResp2AB").getFilterGroupItems();
			if (key === "Import") {
				var oPrRepType = this._getRespImportReport("PR", "PR"),
					oRepType = this._getRespImportReport(obj.LabelRRe1, obj.LabelRRe);
			} else {
				oPrRepType = this._getRespRegularReport("PR", "PR");
				oRepType = this._getRespRegularReport(obj.LabelRRe1, obj.LabelRRe);
				if (key === "ISD") {
					var idx = oPrRepType.findIndex(function (e) {
						return e.key === "POS Mismatch";
					});
					oPrRepType.splice(idx, 1);
					oRepType.splice(idx, 1);
				}
			}
			this.byId("idConsldReptTypeRR").setModel(new JSONModel(oPrRepType), "ReportType");
			this.byId("idConsldReptTypeRR").setSelectedKeys(null);
			this.byId("idConsldReptTypeRR11").setModel(new JSONModel(oRepType), "RespReportType");
			this.byId("idConsldReptTypeRR11").setSelectedKeys(null);
			aItemPr[5].setLabel(key === "Import" ? "BOE No." : 'Doc Number');
			aItem2AB[5].setLabel(key === "Import" ? "BOE No." : 'Doc Number');
			this.onClearRecRespPR();
			this.onClearRecResp2A();
		},

		onFilterReconResp: function () {
			var req = {
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getLastReconTimeStamp.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data), "LastReconTimeStamp");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onClearRecRespPR: function () {
			var date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			this.byId("idRRGstins1").setSelectedKeys(null);
			this.byId("dtConsldFrom1").setDateValue(vPeriod);
			this.byId("dtConsld1To1").setMinDate(vPeriod);
			this.byId("dtConsld1To1").setDateValue(date);
			this.byId("idERRVenPAN").removeAllTokens();
			this.byId("idRRVenGstn").removeAllTokens();
			this.byId("reconDocnumber1s").removeAllTokens();
			this.byId("idConsldReptTypeRR").setSelectedKeys(null);
			this.byId("DocTypeIDRR").setSelectedKeys(null);
			this.byId("idFromDD").setValue(null);
			this.byId("idToDD").setValue(null);
			this.byId("pos").setSelectedKeys(null);
			this.onRecResults();
		},

		onRecResults: function () {
			var key = this.byId("sRecResCriteria").getSelectedKey(),
				oToken = this.byId("reconDocnumber1s").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});
			oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});
			oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1").getValue(),
					"reconCriteria": key,
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": "PR",
					"pos": this.removeAll(this.getView().byId("pos").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR").getSelectedKeys()),
					"toDocDate": this.byId("idToDD").getValue(),
					"fromDocDate": this.byId("idFromDD").getValue()
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						this.getView().setModel(new JSONModel([]), "getReconResponse");
					} else {
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse");
					}
					this._nicPagination(data.hdr);
					this.getView().byId("idReconRTable1").clearSelection();
					this.byId("clPrDocNo").setVisible(key !== "Import");
					this.byId("clPrDocDate").setVisible(key !== "Import");
					this.byId("clPrBoeNo").setVisible(key === "Import");
					this.byId("clPrBoeDate").setVisible(key === "Import");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		_nicPagination: function (header) {
			var vCount = this.getView().byId("idPageN").getSelectedKey(),
				pageNumber = Math.ceil((header.totalCount || 0) / vCount);
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
			} else if (vIdBtn.includes("btnLastN")) {
				vValue = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN").setEnabled(false);
				}
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN").setValue(vValue);
			this.nicCredentialTabl1(vValue);
		},

		onSubmitPagination: function () {
			var vPageNo = this.byId("inPageNoN").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl1(vPageNo);
		},

		//Table for NIC get Call
		nicCredentialTabl1: function (vPageNo) {
			var oToken = this.byId("reconDocnumber1s").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});
			oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});
			oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1").getValue(),
					"reconCriteria": this.byId("sRecResCriteria").getSelectedKey(),
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": "PR",
					"pos": this.removeAll(this.getView().byId("pos").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR").getSelectedKeys()),
					"toDocDate": this.byId("idToDD").getValue(),
					"fromDocDate": this.byId("idFromDD").getValue()
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						this.getView().setModel(new JSONModel([]), "getReconResponse");
					} else {
						data.resp.forEach(function (item) {
							item.radio1 = false;
						});
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse");
					}
					this.byId("idReconRTable1").setSelectedIndex(-1);
					this._nicPagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		onPNChange: function (oEvt, vPageNo) {
			var oToken = this.byId("reconDocnumber1s").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});
			var oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});
			var oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1").getValue(),
					"reconCriteria": this.byId("sRecResCriteria").getSelectedKey(),
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": "PR",
					"pos": this.removeAll(this.getView().byId("pos").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR").getSelectedKeys()),
					"toDocDate": this.byId("idToDD").getValue(),
					"fromDocDate": this.byId("idFromDD").getValue()
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						this.getView().setModel(new JSONModel([]), "getReconResponse");
					} else {
						data.resp.forEach(function (item) {
							item.radio1 = false;
						});
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse");
					}
					this._nicPagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		onClearRecResp2A: function () {
			var date = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1);
			this.byId("idRRGstins1s").setSelectedKeys(null);
			this.byId("dtConsldFrom1s").setDateValue(vPeriod);
			this.byId("dtConsld1To1s").setMinDate(vPeriod);
			this.byId("dtConsld1To1s").setDateValue(date);
			this.byId("idERRVenPAN1").removeAllTokens();
			this.byId("idRRVenGstn1").removeAllTokens();
			this.byId("reconDocnumber1ss").removeAllTokens();
			this.byId("idConsldReptTypeRR11").setSelectedKeys(null);
			this.byId("DocTypeIDRR2AB").setSelectedKeys(null);
			this.byId("idFromDD2AB").setValue(null);
			this.byId("idToDD2AB").setValue(null);
			this.byId("slDivision2AB").setSelectedKeys(null);
			this.onReconResults2AB();
		},

		onReconResults2AB: function () {
			var key = this.byId("sRecResCriteria").getSelectedKey(),
				oToken = this.byId("reconDocnumber1ss").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN1").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn1").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});

			oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});

			oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": 0,
					"pageSize": Number(this.getView().byId("idPageN1").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1s").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1s").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1s").getValue(),
					"reconCriteria": key,
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR11").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": this.getView().getModel("Display").getData().LabelRRe1,
					"pos": this.removeAll(this.getView().byId("slDivision2AB").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR2AB").getSelectedKeys()),
					"toDocDate": this.byId("idToDD2AB").getValue(),
					"fromDocDate": this.byId("idFromDD2AB").getValue()
				}
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						this.getView().setModel(new JSONModel([]), "getReconResponse2");
					} else {
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse2");
					}
					this._nicPagination1(data.hdr);
					this.getView().byId("idReconRTable2").clearSelection();
					this.byId("clAbDocNo").setVisible(key !== "Import");
					this.byId("clAbDocDate").setVisible(key !== "Import");
					this.byId("clAbBoeNo").setVisible(key === "Import");
					this.byId("clAbBoeDate").setVisible(key === "Import");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		_nicPagination1: function (header) {
			var vCount = this.getView().byId("idPageN1").getSelectedKey();
			var pageNumber = Math.ceil((header.totalCount || 0) / vCount);
			this.byId("txtPageNoN1").setText("/ " + pageNumber);
			this.byId("inPageNoN1").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNoN1").setValue(pageNumber);
				this.byId("inPageNoN1").setEnabled(false);
				this.byId("btnPrevN1").setEnabled(false);
				this.byId("btnNextN1").setEnabled(false);
				this.byId("btnFirstN1").setEnabled(false);
				this.byId("btnLastN1").setEnabled(false);
			} else if (this.byId("inPageNoN1").getValue() === "" || this.byId("inPageNoN1").getValue() === "0") {
				this.byId("inPageNoN1").setValue(1);
				this.byId("inPageNoN1").setEnabled(true);
				this.byId("btnPrevN1").setEnabled(false);
				this.byId("btnFirstN1").setEnabled(false);
			} else {
				this.byId("inPageNoN1").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNoN1").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNextN1").setEnabled(true);
				this.byId("btnLastN1").setEnabled(true);
			} else {
				this.byId("btnNextN1").setEnabled(false);
				this.byId("btnLastN1").setEnabled(false);
			}
			this.byId("btnPrevN1").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirstN1").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination1: function (oEvent) {
			var vValue = parseInt(this.byId("inPageNoN1").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrevN1")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrevN1").setEnabled(false);
				}
				if (!this.byId("btnNextN1").getEnabled()) {
					this.byId("btnNextN1").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNextN1")) {
				var vPageNo = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrevN1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNextN1").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirstN1")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirstN1").setEnabled(false);
				}
				if (!this.byId("btnLastN1").getEnabled()) {
					this.byId("btnLastN1").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnLastN1")) {
				vValue = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirstN1").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLastN1").setEnabled(false);
				}
			} else {
				vPageNo = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
			}
			this.byId("inPageNoN1").setValue(vValue);
			this.nicCredentialTabl2(vValue);
		},

		onSubmitPagination1: function () {
			var vPageNo = this.byId("inPageNoN1").getValue(),
				pageNumber = parseInt(this.byId("txtPageNoN1").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl2(vPageNo);
		},

		nicCredentialTabl2: function (vPageNo) {
			var oToken = this.byId("reconDocnumber1ss").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN1").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn1").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});
			oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});
			oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN1").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1s").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1s").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1s").getValue(),
					"reconCriteria": this.byId("sRecResCriteria").getSelectedKey(),
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR11").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": this.getView().getModel("Display").getData().LabelRRe1,
					"pos": this.removeAll(this.getView().byId("slDivision2AB").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR2AB").getSelectedKeys()),
					"toDocDate": this.byId("idToDD2AB").getValue(),
					"fromDocDate": this.byId("idFromDD2AB").getValue()
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
						this.getView().setModel(new JSONModel([]), "getReconResponse2");
					} else {
						data.resp.forEach(function (item) {
							item.radio2 = false;
						});
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse2");
					}
					this.byId("idReconRTable2").setSelectedIndex(-1);
					this._nicPagination1(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		onPNChange1: function (oEvent, vPageNo) {
			var oToken = this.byId("reconDocnumber1ss").getTokens(),
				oVendorPanArr = this.byId("idERRVenPAN1").getTokens(),
				oVendorGstinArr = this.byId("idRRVenGstn1").getTokens(),
				aToken = oToken.map(function (e) {
					return e.getKey();
				});
			oVendorPanArr = oVendorPanArr.map(function (e) {
				return e.getKey();
			});
			oVendorGstinArr = oVendorGstinArr.map(function (e) {
				return e.getKey();
			});

			var postData = {
				"hdr": {
					"pageNum": (vPageNo ? vPageNo - 1 : 0),
					"pageSize": Number(this.getView().byId("idPageN1").getSelectedKey())
				},
				"req": {
					"entityId": $.sap.entityID.toString(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1s").getSelectedKeys()),
					"toTaxPeriod": this.byId("dtConsld1To1s").getValue(),
					"fromTaxPeriod": this.byId("dtConsldFrom1s").getValue(),
					"reconCriteria": this.byId("sRecResCriteria").getSelectedKey(),
					"reportType": this.removeAll(this.getView().byId("idConsldReptTypeRR11").getSelectedKeys()),
					"docNumber": aToken,
					"vendorGstins": oVendorGstinArr,
					"vendorPans": oVendorPanArr,
					"type": this.getView().getModel("Display").getData().LabelRRe1,
					"pos": this.removeAll(this.getView().byId("slDivision2AB").getSelectedKeys()),
					"docType": this.removeAll(this.getView().byId("DocTypeIDRR2AB").getSelectedKeys()),
					"toDocDate": this.byId("idToDD2AB").getValue(),
					"fromDocDate": this.byId("idFromDD2AB").getValue()
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2getReconResponse.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.hdr.message);
					} else {
						data.resp.forEach(function (item) {
							item.radio2 = false;
						});
						this.getView().setModel(new JSONModel(data.resp), "getReconResponse2");
					}
					this._nicPagination1(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var obj = JSON.parse(jqXHR.responseText);
					MessageBox.error("Recon Response: " + obj.hdr.message);
				}.bind(this));
		},

		handleChangeRR1: function (oevt) {
			var toDate = this.byId("dtConsld1To1").getDateValue(),
				fromDate = this.byId("dtConsldFrom1").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("dtConsld1To1").setDateValue(oevt.getSource().getDateValue());
				this.byId("dtConsld1To1").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("dtConsld1To1").setMinDate(oevt.getSource().getDateValue());
			}
		},

		handleChangeRR2: function (oevt) {
			var toDate = this.byId("dtConsld1To1s").getDateValue(),
				fromDate = this.byId("dtConsldFrom1s").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("dtConsld1To1s").setDateValue(oevt.getSource().getDateValue());
				this.byId("dtConsld1To1s").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("dtConsld1To1s").setMinDate(oevt.getSource().getDateValue());
			}
		},

		handleChangeDD: function (oevt) {
			var toDate = this.byId("idToDD").getDateValue(),
				fromDate = this.byId("idFromDD").getDateValue();
			if (fromDate > toDate) {
				this.byId("idToDD").setDateValue(oevt.getSource().getDateValue());
			}
			this.byId("idToDD").setMinDate(oevt.getSource().getDateValue());
		},

		handleChangeDD2AB: function (oevt) {
			var toDate = this.byId("idToDD2AB").getDateValue(),
				fromDate = this.byId("idFromDD2AB").getDateValue();
			if (fromDate > toDate) {
				this.byId("idToDD2AB").setDateValue(oevt.getSource().getDateValue());
			}
			this.byId("idToDD2AB").setMinDate(oevt.getSource().getDateValue());
		},

		pos: function () {
			var that = this;
			var req = {
				"req": {
					"entityId": $.sap.entityID.toString(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1").getSelectedKeys()),
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2GetPos.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.unshift({
						pos: "All"
					})
					var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oGstr6DistrCrElfData, "gstr2GetPos");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		pos1: function () {
			var that = this;
			var req = {
				"req": {
					"entityId": $.sap.entityID.toString(),
					"gstins": this.removeAll(this.getView().byId("idRRGstins1s").getSelectedKeys()),
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2GetPos.do",
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.unshift({
						pos: "All"
					})
					var oGstr6DistrCrElfData = new sap.ui.model.json.JSONModel(data.resp);
					that.getView().setModel(oGstr6DistrCrElfData, "gstr2GetPos2AB");
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onTab1: function (oEvt) {
			this.Tab1 = oEvt.getSource().getBindingContext("getReconResponse").getObject();

			var oData = {
				"isHideFlag": this.Tab1.isHideFlag
			}
			var oDataHide = new sap.ui.model.json.JSONModel(oData);
			this.getView().setModel(oDataHide, "HideForceMatch");
		},

		onTab2: function (oEvt) {
			this.Tab2 = oEvt.getSource().getBindingContext("getReconResponse2").getObject();
		},

		_getForceMatchLock: function (source) {
			return {
				avalCess: "",
				avalCgst: "",
				avalIgst: "",
				avalSgst: "",
				igst: 0,
				cgst: 0,
				cess: 0,
				sgst: 0,
				invoiceVale: 0,
				taxablevalue: 0,
				totalTax: 0,
				docNumber: "",
				docType: "",
				docdate: "",
				gstin: "",
				pos: "",
				rcmFlag: "",
				reconLinkId: "",
				reportType: "",
				returnPeriod: "",
				source: source,
				vendorGstin: ""
			};
		},

		_calForceMatch: function (aData, aIndex, obj, data) {
			aIndex.forEach(function (e) {
				obj.igst = obj.igst + +data[e].igst;
				obj.cgst = obj.cgst + +data[e].cgst;
				obj.cess = obj.cess + +data[e].cess;
				obj.sgst = obj.sgst + +data[e].sgst;
				obj.invoiceVale = obj.invoiceVale + +data[e].invoiceVale;
				obj.taxablevalue = obj.taxablevalue + +data[e].taxablevalue;
				obj.totalTax = obj.totalTax + +data[e].totalTax;
				aData.push(data[e]);
			});
			aData.push(obj);
		},

		onRecResponse: function (oEvt) {
			var ReconType = this.getView().byId("idReconTypeRR1").getSelectedKey(),
				oTab1Index = this.getView().byId("idReconRTable1").getSelectedIndices(),
				oTab2Index = this.getView().byId("idReconRTable2").getSelectedIndices(),
				aTab1Data = this.getView().byId("idReconRTable1").getModel("getReconResponse").getProperty("/"),
				aTab2Data = this.getView().byId("idReconRTable2").getModel("getReconResponse2").getProperty("/"),
				Tab1 = this._getForceMatchLock("PR Total"),
				Tab2 = this._getForceMatchLock(ReconType === '2A_PR' ? '2A/6A Total' : '2B Total'),
				Tab3 = this._getForceMatchLock(ReconType === '2A_PR' ? 'Difference(PR - 2A/6A)' : 'Difference(PR - 2B)'),
				aData = [];

			if (!oTab1Index.length && !oTab2Index.length) {
				MessageBox.error("Kindly select atleast one record");
				return;
			}
			this._calForceMatch(aData, oTab1Index, Tab1, aTab1Data);
			this._calForceMatch(aData, oTab2Index, Tab2, aTab2Data);

			Tab3.igst = (+Tab1.igst - +Tab2.igst).toFixed(2);
			Tab3.cgst = (+Tab1.cgst - +Tab2.cgst).toFixed(2);
			Tab3.cess = (+Tab1.cess - +Tab2.cess).toFixed(2);
			Tab3.sgst = (+Tab1.sgst - +Tab2.sgst).toFixed(2);
			Tab3.invoiceVale = (+Tab1.invoiceVale - +Tab2.invoiceVale).toFixed(2);
			Tab3.taxablevalue = (+Tab1.taxablevalue - +Tab2.taxablevalue).toFixed(2);
			Tab3.totalTax = (+Tab1.totalTax - +Tab2.totalTax).toFixed(2);
			aData.push(Tab3);
			this.getView().setModel(new JSONModel(aData), "Response");

			var recCriteria = this.byId("sRecResCriteria").getSelectedKey(),
				key = oEvt.getParameter("item").getKey();

			switch (key) {
			case "ForcedMatch":
				if (!this.ReconRsp) {
					this.ReconRsp = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReconRspnsewithout3Block", this);
					this.getView().addDependent(this.ReconRsp);
				}
				this.ReconRsp.open();
				this.ReconRsp.setModel(new JSONModel({
					"criteria": recCriteria
				}), "RecRespProperty");
				this.byId("idReconRTableUNLock1").setVisibleRowCount(aData.length);
				this.byId("RMidRER").setValue("");
				break;
			case "3BResponse":
				if (!this.dReconWith3bLock) {
					this.dReconWith3bLock = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReconRspnsewith3Block",
						this);
					this.getView().addDependent(this.dReconWith3bLock);
				}
				this.dReconWith3bLock.open();
				this.byId("tpid9").setMaxDate(new Date());
				this.dReconWith3bLock.setModel(new JSONModel({
					"criteria": recCriteria
				}), "RecRespProperty");
				this.byId("idReconRTable3bLock").setVisibleRowCount(aData.length);
				this.byId("RMidRER9").setValue("");

				this.byId("AIid9").setValue(oTab1Index.length === 1 ? aTab1Data[oTab1Index[0]].avalIgst : '');
				this.byId("ACid9").setValue(oTab1Index.length === 1 ? aTab1Data[oTab1Index[0]].avalCgst : '');
				this.byId("ASid9").setValue(oTab1Index.length === 1 ? aTab1Data[oTab1Index[0]].avalSgst : '');
				this.byId("ACEid9").setValue(oTab1Index.length === 1 ? aTab1Data[oTab1Index[0]].avalCess : '');
				this.byId("ITCid9").setValue(oTab1Index.length === 1 ? aTab1Data[oTab1Index[0]].itcReversal : '');
				var oModel = this.getView().getModel("Display");
				oModel.setProperty("/RRIVis", oTab1Index.length === 1);
				oModel.refresh(true);
				break;
			}
		},

		onChangeTaxPeriod3B: function (oEvent) {
			oEvent.getSource().setValueState("None");
		},

		onWithout3BSave: function () {
			this.onRecRSave("Force", "RMidRER");
		},

		onWithout3BSaveClose: function () {
			this.ReconRsp.close();
		},

		onwith3Brec: function () {
			this.onRecRSave("3B", "RMidRER9");
		},

		onwith3BrecClose: function () {
			this.dReconWith3bLock.close();
		},

		onRecRSave: function (indentifier, id) {
			if (!this.Bulk) {
				this.Bulk = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.BulkSave", this);
				this.getView().addDependent(this.Bulk);
			}
			var vITCid9 = (this.byId("ITCid9") && this.byId("ITCid9").getValue() ? this.byId("ITCid9").getValue() : null);
			if (indentifier === "3B" && !this.byId("tpid9").getValue()) {
				MessageBox.error("Please select Tax Period for GSTR-3B");
				this.byId("tpid9").setValueState("Error");
				this.byId("tpid9").setValueStateText("Select Tax Period for GSTR-3B");
				return;
			}
			var request = {
				"req": {
					"entityId": $.sap.entityID.toString(),
					"indentifier": indentifier,
					"taxPeriodGstr3b": this.byId("tpid9") === undefined ? "" : this.byId("tpid9").getValue(),
					"reconCriteria": this.byId("sRecResCriteria").getSelectedKey(),
					"reconType": this.byId("idReconTypeRR1").getSelectedKey(),
					"responseRemarks": this.byId(id) === undefined ? "" : this.byId(id).getValue(),
					"avaiIgst": this.byId("AIid9") === undefined ? "" : this.byId("AIid9").getValue(),
					"avaiCgst": this.byId("ACid9") === undefined ? "" : this.byId("ACid9").getValue(),
					"avaiSgst": this.byId("ASid9") === undefined ? "" : this.byId("ASid9").getValue(),
					"avaiCess": this.byId("ACEid9") === undefined ? "" : this.byId("ACEid9").getValue(),
					"itcReversal": vITCid9,
					"respList": []
				}
			};

			var aModelData = this.getView().getModel("Response").getData();
			aModelData.forEach(function (e) {
				if (["PR", "2A/6A", "2B"].includes(e.source)) {
					var obj = {
						"docNumber": e.docNumber,
						"rcmFlag": e.rcmFlag,
						"reconLinkId": e.reconLinkId,
						"returnPeriod": e.returnPeriod,
						"source": e.source,
						"reportType": e.reportType
					};
					if (["2A/6A", "2B"].includes(e.source)) {
						obj.cfs = e.cfs;
					}
					request.req.respList.push(obj);
				}
			});

			if (indentifier === "3B") {
				this.dReconWith3bLock.setBusy(true);
			} else if (indentifier === "Force") {
				this.ReconRsp.setBusy(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2ReconResponseButton.do",
					contentType: "application/json",
					data: JSON.stringify(request)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						if (indentifier === "3B") {
							this.dReconWith3bLock.close();
						} else if (indentifier === "Force") {
							this.ReconRsp.close();
						}
						MessageBox.success(data.resp, {
							title: "Recon Response Action",
							onClose: function () {
								this.onRecResults();
								this.onReconResults2AB();
							}.bind(this)
						});
					} else {
						if (data.resp.errorDesc !== undefined) {
							this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
							this.Bulk.open();
							this.Bulk.setModel(new JSONModel({
								"criteria": request.req.reconCriteria
							}), "RespProperty")
							if (indentifier === "3B") {
								this.dReconWith3bLock.close();
							} else if (indentifier === "Force") {
								this.ReconRsp.close();
							}
						} else {
							MessageBox.error(data.resp);
						}
					}
					if (indentifier === "3B") {
						this.dReconWith3bLock.setBusy(false);
					} else if (indentifier === "Force") {
						this.ReconRsp.setBusy(false);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					if (indentifier === "3B") {
						this.dReconWith3bLock.setBusy(false);
					} else if (indentifier === "Force") {
						this.ReconRsp.setBusy(false);
					}
				}.bind(this));
		},

		onCloseDialogBulkSave: function () {
			this.Bulk.close();
		},

		enableGstr2ALink: function (value) {
			if (["Failed", "Success_with_no_data"].includes(value)) {
				return true;
			}
			return false;
		},

		visiGstr2ACheckbox: function (value) {
			if (!value || ["Failed", "Success_with_no_data", "-"].includes(value)) {
				return false;
			}
			return true;
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive" || authToken === "I") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onTokenUpdate: function (oEvent, type) {
			var oMultiInput = oEvent.getSource();
			var aTokens = oMultiInput.getTokens();

			// If the number of tokens exceeds 30, remove the excess tokens
			if (aTokens.length > 30) {
				// Slice the array to keep only the first 30 tokens
				var aTokensToKeep = aTokens.slice(0, 30);

				// Remove all tokens and then add back only the first 30 tokens
				oMultiInput.removeAllTokens();
				aTokensToKeep.forEach(function (oToken) {
					oMultiInput.addToken(oToken);
				});

				// Optionally, show a message to the user
				MessageBox.error("Maximum 30 Vendor " + type + " are allowed.");
				// sap.m.MessageToast.show("Only the first 30 tokens are kept.");
			}
		}

	});
});