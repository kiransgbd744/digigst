sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.initiateReconB", {

		onInit: function () {
			var that = this,
				vDate = new Date(),
				date = new Date(),
				date1 = new Date(),
				vPeriod = new Date(date.getFullYear(), date.getMonth(), 1),
				vEmailDateVC = new Date();

			var vDate20 = new Date();
			vDate20.setDate(vDate20.getDate() - 19);

			date.setDate(date.getDate() - 9);
			date1.setMonth(date1.getMonth() - 1);
			vPeriod.setDate(1);
			var object = {
				"Label": "2A/6A",
				"TAX": true,
				"DOC": false,
				"Label2": "2A/6A"
			};
			that.getView().setModel(new JSONModel(object), "Display");

			var oData = {
				"mandat": true,
				"ExactMatch": true,
				"MatchWithTolerance": true,
				"ValueMismatch": true,
				"POSMismatch": true,
				"DocDateMismatch": true,
				"DocTypeMismatch": true,
				"DocNoMismatchI": true,
				"MultiMismatch": true,
				"AdditioninPR": true,
				"Additionin2B": true
			};
			that.getView().setModel(new JSONModel(oData), "Mandat");

			this.byId("InDateFrom").setMaxDate(vDate);
			this.byId("InDateFrom").setDateValue(vDate20);
			this.byId("InDateFrom").addEventDelegate({
				onAfterRendering: function () {
					that.byId("InDateFrom").$().find("input").attr("readonly", true);
				}
			});

			this.byId("InDateTo").setMinDate(vDate20);
			this.byId("InDateTo").setMaxDate(vDate);
			this.byId("InDateTo").setDateValue(vDate);
			this.byId("InDateTo").addEventDelegate({
				onAfterRendering: function () {
					that.byId("InDateTo").$().find("input").attr("readonly", true);
				}
			});
			/*code ended by Arun on 30/11/2021*/
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

			/*this.byId("idInitiateReconPeriodTax12A1").setMinDate(vPeriod);*/
			this.byId("idInitiateReconPeriodTax12A1").setMaxDate(vDate);
			this.byId("idInitiateReconPeriodTax12A1").setDateValue(vDate);
			this.byId("idInitiateReconPeriodTax12A1").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idInitiateReconPeriodTax12A1").$().find("input").attr("readonly", true);
				}
			});

			this.ReportArr = [];
			var oModel = new JSONModel(jQuery.sap.getModulePath("com.ey.digigst.model", "/reconResult.json"));
			this.getView().setModel(oModel, "ReportDownload");
		},

		onRequestId: function () {
			var payload = {
				"entityId": $.sap.entityID,
				"initiationDateFrom": this.byId("InDateFrom").getValue(),
				"initiationDateTo": this.byId("InDateTo").getValue()
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getgstr2RequestIds.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				data.resp.requestDetails.unshift({
					requestId: "All"
				});
				var oModel = new JSONModel(data.resp.requestDetails);
				oModel.setSizeLimit(data.resp.requestDetails.length);
				that.getView().setModel(oModel, "getgstr2RequestIds");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		/*code added by Arun on 30/11/2021*/
		handleChangeF: function (oEvent) {
			// this.onPressRequestIDwise2A();
			var toDate = this.byId("InDateTo").getDateValue(),
				fromDate = this.byId("InDateFrom").getDateValue();
			if (fromDate > toDate) {
				//MessageBox.error("Tax Period From  can not be greter then Tax Period To");
				this.byId("InDateTo").setDateValue(oEvent.getSource().getDateValue());
				this.byId("InDateTo").setMinDate(oEvent.getSource().getDateValue());
			} else {
				this.byId("InDateTo").setMinDate(oEvent.getSource().getDateValue());
			}
			this.onRequestId();
		},

		handleChangeT: function (oEvent) {
			this.onRequestId();
		},

		/*code ended by Arun on 30/11/2021*/
		onUserId: function () {
			var payload = {
				"entityId": $.sap.entityID,
				"screenName": "initiateRecon"
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getgstr2UserNames.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				data.resp.requestDetails.unshift({
					userName: "All"
				});
				that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2UserNames");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onUserEmails: function () {
			var payload = {
				"entityId": $.sap.entityID
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getgstr2EmailIds.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				data.resp.requestDetails.unshift({
					email: "All"
				});
				that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2EmailIds");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onPressGoForGSTIN2A();
				var oPropIMS = this.getOwnerComponent().getModel("GroupPermission").getProperty("/");
				this.byId("id_2aIMSReport").setVisible(!!oPropIMS.GR1 && !!oPropIMS.GR4);
			}
		},

		onPressGoForGSTIN2A: function (oEvent) {
			this.byId("searchId").setValue("");
			var selKey = this.byId("idDateRange2A").getSelectedKey();
			if (selKey === "Tax Perioid") {
				var date, date1;
				if (this.getView().getModel("isYdtFlag") !== undefined) {
					if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp) {
						date = this.byId("idInitiateReconPeriodTax12A").getValue();
						date1 = this.byId("idInitiateReconPeriodTax2A").getValue();
					} else {
						date = this.byId("idInitiateReconPeriodTax12A1").getValue();
						date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
					}
				} else {
					date = this.byId("idInitiateReconPeriodTax12A1").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
				}
				var postData = {
					"req": {
						"entityId": Number($.sap.entityID),
						"toTaxPeriod": this.byId("idInitiateReconPeriodTax12A").getValue(),
						"fromTaxPeriod": this.byId("idInitiateReconPeriodTax2A").getValue(),
						"toTaxPeriod2A": date,
						"fromTaxPeriod2A": date1,
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
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDataForGatr2ReconSummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp.det), "GSTIN2A");
						this.getView().byId("idInitiateReconList2A").selectAll();
						this.getView().byId("checkboxID").setSelected(true);
						this.onInitiateRecon();
					} else {
						this.getView().setModel(new JSONModel([]), "GSTIN2A");
						this.getView().setModel(new JSONModel([]), "InitiateRecon2A");
					}
					this.getView().setModel(new JSONModel({
						"text": data.resp.optedAns
					}), "OptedAnswer");

				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel([]), "GSTIN2A");
					this.getView().setModel(new JSONModel([]), "InitiateRecon2A");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
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
		onRecontypeChange: function (oEvent) {
			var oPropIMS = this.getOwnerComponent().getModel("GroupPermission"),
				oModel = this.getView().getModel("Display"),
				key = oEvent.getSource().getSelectedKey(),
				aField = [
					"ForceMatchID", "SRCPid", "SRTPid", "SGSRid", "SPSRid", "RGTPWRid", "RGTPWRid2", "RGWRid", "VGTPWR", "VGWRid", "VPTPWRid",
					"VPWRid", "VGWDRid", "VPWDRid", "CDNI2Aid", "CDNIPRid", "TSis", "PRid", "LCid", "amdTrcRpt2b", "RCid", "DprId", "Dpr2aId", "Impg",
					"Nt", "id_PotentialI", "id_DocNoMismatch", "id_DocNoDocDateMismatch", "id_Potential", "id_LogicalMatch"
				];

			aField.forEach(function (f) {
				var obj = this.byId(f);
				if (obj) {
					obj.setSelected(false);
				}
			}.bind(this));
			this.byId("LCid").setVisible(true);
			this.byId("D2bId").setVisible(false);
			this.byId("RGTPWRid2").setVisible(false);
			this.byId("amdTrcRpt2b").setVisible(false);
			this.byId("cbImsPending").setVisible(false);
			this.byId("id_2aIMSReport").setVisible(false);

			this.ReportArr = [];
			if (key === "2APR") {
				oModel.setProperty("/Label", "2A/6A");
				this.byId("Nt").setVisible(true);
				this.byId("Dpr2aId").setVisible(true);
				this.byId("Impg").setEnabled(true);
				this.byId("PRid").setEnabled(true);
				this.byId("id_ISDMatching").setVisible(true);
				this.getView().getModel("isYdtFlag").getData().resp = false;
				this.byId("id_IMSReport").setVisible(false);
				this.getView().getModel("isYdtFlag").refresh(true);
				if (oPropIMS.getProperty("/GR1") && oPropIMS.getProperty("/GR4")) {
					this.byId("id_2aIMSReport").setVisible(true);
				}
			} else if (key === "EINVPR") {
				oModel.setProperty("/Label", 'Inward E-Inv');
				oModel.setProperty("/TAX", true);
				oModel.setProperty("/DOC", false);
				this.byId("idDateRange2A").setSelectedKey('Tax Perioid');
				this.byId("Impg").setEnabled(true);
				this.byId("PRid").setEnabled(true);
				this.byId("Nt").setVisible(true);
				this.byId("Dpr2aId").setVisible(true);
				this.byId("id_ISDMatching").setVisible(false);
				this.byId("id_DocNoMismatch").setSelected(false);
				this.byId("id_IMSReport").setVisible(false);
				this.getView().setModel(new JSONModel({
					"resp": true
				}), "isYdtFlag");

			} else {
				oModel.setProperty("/Label", "2B");
				this.byId("Impg").setEnabled(true);
				this.byId("PRid").setEnabled(false);
				this.byId("Nt").setVisible(false);
				this.byId("D2bId").setVisible(true);
				this.byId("LCid").setVisible(false);
				this.byId("Dpr2aId").setVisible(false);
				this.byId("RGTPWRid2").setVisible(true);
				this.byId("amdTrcRpt2b").setVisible(true);
				this.byId("id_ISDMatching").setVisible(true);

				if (oPropIMS.getProperty("/GR1") && oPropIMS.getProperty("/GR2")) {
					this.byId("cbImsPending").setVisible(true);
					this.byId("cbImsPending").setSelected(false);
					this.byId("id_IMSReport").setVisible(true);
					this.byId("id_IMSReport").setEnabled(true);
					this.byId("id_IMSReport").setSelected(false);
				}

				// this.byId("id_DocNoMismatch").setSelected(true);
				this.YTDflag();
				if (this.getView().getModel("isYdtFlag") !== undefined) {
					if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp) {
						this.byId("Impg").setSelected(false);
						this.byId("idInitiateReconPeriodTax2A1").setDateValue(this.byId("idInitiateReconPeriodTax2A").getDateValue());
						this.byId("idInitiateReconPeriodTax12A1").setDateValue(this.byId("idInitiateReconPeriodTax12A").getDateValue());
						this.byId("idInitiateReconPeriodTax12A1").setDisplayFormat("MMM yyyy");
					}
				}
			}
			oModel.refresh(true);
			this.onPressGoForGSTIN2A();
			this.mandatoryCheckSelct();
		},
		//======================= code ended by chaithra on 2/11/2020 =====================================//

		YTDflag: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/isYdtFlag.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.resp) {
					that.getView().getModel("Display").setProperty("/TAX", true);
					that.getView().getModel("Display").setProperty("/DOC", false);
					that.byId("idDateRange2A").setSelectedKey('Tax Perioid');
				}

				that.getView().setModel(new JSONModel(data), "isYdtFlag");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},
		/**
		 * on Click of Initiate Matching
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private
		 * @param trigerd Click on Initiate Matching
		 */
		fnIntiniateBtnPress2A: function () {
			var selItems = this.byId("idInitiateReconList2A").getSelectedContextPaths();
			if (!selItems.length) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						this.onInitiateMatch();
					}
				}.bind(this)
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
				/*this.byId("FuzztMId").setSelected(true);*/
				this.byId("ForceMatchID").setSelected(true);
				this.byId("SRCPid").setSelected(true);
				this.byId("SRTPid").setSelected(true);
				this.byId("SGSRid").setSelected(true);
				this.byId("SPSRid").setSelected(true);
				this.byId("RGTPWRid").setSelected(true);
				this.byId("RGTPWRid2").setSelected(true);
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
					//this.byId("ForceMatchID").setSelected(false);
					this.byId("LCid").setSelected(false);
					this.byId("Impg").setSelected(true);
					this.byId("PRid").setSelected(false);
					if (this.getView().getModel("isYdtFlag") !== undefined) {
						if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp) {
							this.byId("Impg").setSelected(false);
						}
					}
					this.ReportArr.push("Force_Match_Records", "Summary_CalendarPeriod_Records", "Summary_TaxPeriod_Record",
						"Supplier_GSTIN_Summary_Records", "Supplier_PAN_Summary_Records", "Recipient_GSTIN_Period_Wise_Record",
						"Recipient_GSTIN_Period_Wise_Record_II", "Recipient_GSTIN_Wise_Records", "Vendor_GSTIN_Period_Wise_Records",
						"Vendor_GSTIN_Wise_Records", "Vendor_PAN_Period_Wise_Records", "Vendor_PAN_Wise_Records", "CRD-INV_Ref_Reg_GSTR_2B_Records",
						"CRD-INV_Ref_Reg_PR_Records", "Vendor_Records_GSTIN", "Vendor_Records_PAN", "GSTR_2B_Time_Stamp_Report",
						"Reverse_Charge_Register", "Dropped_PR_Records_Report");

					if (this.getView().byId("Impg").getSelected()) {
						var selKey = "Consolidated IMPG Report";
						this.ReportArr.push(selKey);
					}
				}
				if (this.getView().byId("id_DocNoMismatch").getSelected()) {
					var selKey = "Doc No Mismatch II";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_DocNoDocDateMismatch").getSelected()) {
					var selKey = "Doc No & Doc Date Mismatch";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_PotentialI").getSelected()) {
					var selKey = "Potential-I";
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
			} else {
				this.byId("ForceMatchID").setSelected(false);
				this.byId("SRCPid").setSelected(false);
				this.byId("SRTPid").setSelected(false);
				this.byId("SGSRid").setSelected(false);
				this.byId("SPSRid").setSelected(false);
				this.byId("RGTPWRid").setSelected(false);
				this.byId("RGTPWRid2").setSelected(false);
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
				if (this.getView().byId("id_DocNoDocDateMismatch").getSelected()) {
					var selKey = "Doc No & Doc Date Mismatch";
					this.ReportArr.push(selKey);
				}
				if (this.getView().byId("id_PotentialI").getSelected()) {
					var selKey = "Potential-I";
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

		onInitiateRecondownload: function (oEvent) {
			var oView = this.getView();
			if (oEvent.getSource().getSelectedKey() === "INITIATEREPORTS") {
				oView.byId("tabSumm1").setVisible(true);
				oView.byId("tabSumm").setVisible(false);
			} else {
				oView.byId("tabSumm1").setVisible(false);
				oView.byId("tabSumm").setVisible(true);
			}
		},

		onSelectImsPending: function (oEvent) {
			var flag = oEvent.getParameter('selected');
			this.byId("id_IMSReport").setEnabled(!flag);
			this.byId("id_IMSReport").setSelected(flag);
			this._addRemoveReport("Consolidated PR 2B Report IMS", flag);
		},

		checkSelct: function (oEvt) {
			var selText = oEvt.getSource().getText();
			switch (selText) {
			case "Fuzzy Match":
				var selKey = "Fuzzy_Match_Records";
				break;
			case "Force Match":
				selKey = "Force_Match_Records";
				break;
			case "Summary Report Calendar Period":
				selKey = "Summary_CalendarPeriod_Records";
				break;
			case "Summary Report Tax Period":
				selKey = "Summary_TaxPeriod_Record";
				break;
			case "Supplier GSTIN Summary Report":
				selKey = "Supplier_GSTIN_Summary_Records";
				break;
			case "Supplier PAN Summary Report":
				selKey = "Supplier_PAN_Summary_Records";
				break;
			case "Recipient GSTIN Tax Period Wise Report":
				selKey = "Recipient_GSTIN_Period_Wise_Record";
				break;
			case "Recipient GSTIN Tax Period Wise Report II":
				selKey = "Recipient_GSTIN_Period_Wise_Record_II";
				break;
			case "Recipient GSTIN Wise Report":
				selKey = "Recipient_GSTIN_Wise_Records";
				break;
			case "Vendor GSTIN Tax Period Wise Report":
				selKey = "Vendor_GSTIN_Period_Wise_Records";
				break;
			case "Vendor GSTIN Wise Report":
				selKey = "Vendor_GSTIN_Wise_Records";
				break;
			case "Vendor PAN Tax Period Wise Report":
				selKey = "Vendor_PAN_Period_Wise_Records";
				break;
			case "Vendor PAN Wise Report":
				selKey = "Vendor_PAN_Wise_Records";
				break;
			case "CR/DR-Invoice Reference Register- GSTR 2A/6A":
				selKey = "CRD-INV_Ref_Reg_GSTR_2A_Records";
				break;
			case "CR/DR-Invoice Reference Register- PR":
				selKey = "CRD-INV_Ref_Reg_PR_Records";
				break;
			case "Vendor GSTIN Wise Detailed Report":
				selKey = "Vendor_Records_GSTIN";
				break;
			case "Vendor PAN Wise Detailed Report":
				selKey = "Vendor_Records_PAN";
				break;
			case "GSTR 2A/6A Time Stamp Report":
				selKey = "GSTR_2A_Time_Stamp_Report";
				break;
			case "Consolidated PR Register":
				selKey = "Consolidated_PR_Register";
				break;
			case "Reverse Charge Register":
				selKey = "Reverse_Charge_Register";
				break;
			case "Dropped PR Records Report":
				selKey = "Dropped_PR_Records_Report";
				break;
			case "Dropped 2A/6A Records Report":
				selKey = "Dropped 2A_6A Records Report";
				break;
			case "Doc No Mismatch II":
				selKey = "Doc No Mismatch II";
				break;
			case "Doc No & Doc Date Mismatch":
				selKey = "Doc No & Doc Date Mismatch";
				break;
			case "Potential-II":
				selKey = "Potential-II";
				break;
			case "Potential-I":
				selKey = "Potential-I";
				break;
			case "Logical Match":
				selKey = "Logical Match";
				break;
			case "ISD Matching Report":
				selKey = "ISD Matching Report";
				break;
			case "Consolidated 2A/6AvsPR + IMS Report":
				selKey = "Consolidated PR 2A Report IMS";
				break;
			case "Consolidated 2BvsPR + IMS Report":
				selKey = "Consolidated PR 2B Report IMS";
				break;
			case "Imports/SEZG Matching Report":
				selKey = "Consolidated IMPG Report";
				break;
			case "ITC Tracking Report":
				selKey = "ITC Tracking Report";
				break;
			case "CR/DR-Invoice Reference Register- GSTR 2B":
				selKey = "CRD-INV_Ref_Reg_GSTR_2B_Records";
				break;
			case "GSTR 2B Time Stamp Report":
				selKey = "GSTR_2B_Time_Stamp_Report";
				break;
			case "Dropped 2B Records Report":
				selKey = "Dropped 2B Records Report";
				break;
			case "Locked CFS-N and Amended Records":
			case "GSTR 2B Amendment Track Report":
				selKey = "Locked_CFS_N_Amended_Records";
				break;
			case "Exact Match":
				selKey = "Exact Match";
				break;
			case "Match With Tolerance":
				selKey = "Match With Tolerance";
				break
			case "Value Mismatch":
				selKey = "Value Mismatch";
				break;
			case "POS Mismatch":
				selKey = "POS Mismatch";
				break;
			case "Doc Date Mismatch":
				selKey = "Doc Date Mismatch";
				break;
			case "Doc Type Mismatch":
				selKey = "Doc Type Mismatch";
				break;
			case "Doc No Mismatch I":
				selKey = "Doc No Mismatch I";
				break;
			case "Multi-Mismatch":
				selKey = "Multi-Mismatch";
				break;
			case "Addition in PR":
				selKey = "Addition in PR";
				break;
			case "Addition in 2B":
				selKey = "Addition in 2B";
				break;
			case "Addition in 2A/6A":
				selKey = "Addition in 2A_6A";
				break;
			}
			this._addRemoveReport(selKey, oEvt.getParameter("selected"));
		},

		_addRemoveReport: function (key, flag) {
			var mandatoryData = [
				"Exact Match", "Match With Tolerance", "Value Mismatch", "POS Mismatch", "POS Mismatch", "Doc Date Mismatch",
				"Doc Type Mismatch", "Doc No Mismatch I", "Multi-Mismatch", "Addition in PR", "Addition in 2B", "Addition in 2A_6A"
			];
			if (flag) {
				this.ReportArr.push(key);
			} else {
				if (mandatoryData.includes(key)) {
					this.getView().getModel("Mandat").setProperty("/mandat", false);
					this.getView().getModel("Mandat").refresh(true);
				}
				var idx = this.ReportArr.findIndex(function (e) {
					return e === key;
				});
				this.ReportArr.splice(idx, 1);
			}
		},

		mandatoryCheckSelct: function () {
			var vMandatory = this.byId("idCheckMandatory").getSelected(),
				oData = {
					"mandat": vMandatory,
					"ExactMatch": vMandatory,
					"MatchWithTolerance": vMandatory,
					"ValueMismatch": vMandatory,
					"POSMismatch": vMandatory,
					"DocDateMismatch": vMandatory,
					"DocTypeMismatch": vMandatory,
					"DocNoMismatchI": vMandatory,
					"MultiMismatch": vMandatory,
					"AdditioninPR": vMandatory,
					"Additionin2B": vMandatory
				};
			this.getView().setModel(new JSONModel(oData), "Mandat");
		},

		mandatoryCheckSelctData: function () {
			var oMandateData = this.getView().getModel("Mandat").getData();
			if (oMandateData.ExactMatch) {
				if (!this.ReportArr.includes("Exact Match")) {
					this.ReportArr.push("Exact Match");
				}
			}
			if (oMandateData.MatchWithTolerance) {
				if (!this.ReportArr.includes("Match With Tolerance")) {
					this.ReportArr.push("Match With Tolerance");
				}
			}
			if (oMandateData.ValueMismatch) {
				if (!this.ReportArr.includes("Value Mismatch")) {
					this.ReportArr.push("Value Mismatch");
				}
			}
			if (oMandateData.POSMismatch) {
				if (!this.ReportArr.includes("POS Mismatch")) {
					this.ReportArr.push("POS Mismatch");
				}
			}
			if (oMandateData.DocDateMismatch) {
				if (!this.ReportArr.includes("Doc Date Mismatch")) {
					this.ReportArr.push("Doc Date Mismatch");
				}
			}
			if (oMandateData.DocTypeMismatch) {
				if (!this.ReportArr.includes("Doc Type Mismatch")) {
					this.ReportArr.push("Doc Type Mismatch");
				}
			}
			if (oMandateData.DocNoMismatchI) {
				if (!this.ReportArr.includes("Doc No Mismatch I")) {
					this.ReportArr.push("Doc No Mismatch I");
				}
			}
			if (oMandateData.MultiMismatch) {
				if (!this.ReportArr.includes("Multi-Mismatch")) {
					this.ReportArr.push("Multi-Mismatch");
				}
			}
			if (oMandateData.AdditioninPR) {
				if (!this.ReportArr.includes("Addition in PR")) {
					this.ReportArr.push("Addition in PR");
				}
			}
			if (oMandateData.Additionin2B) {
				if (this.getView().getModel("Display").getData().Label == "2B") {
					if (!this.ReportArr.includes("Addition in 2B")) {
						this.ReportArr.push("Addition in 2B");
					}
				} else if (this.getView().getModel("Display").getData().Label == "2A/6A") {
					if (!this.ReportArr.includes("Addition in 2A_6A")) {
						this.ReportArr.push("Addition in 2A_6A");
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
			var oView = this.getView(),
				oModelData = oView.getModel("GSTIN2A").getData(),
				oPath = oView.byId("idInitiateReconList2A").getSelectedItems(),
				selKey = this.byId("idDateRange2A").getSelectedKey(),
				date, date1, path,
				aGSTIN = oPath.map(function (path) {
					var idx = path.getBindingContext('GSTIN2A').getPath().split('/')[1];
					return oModelData[idx].gstin;
				});

			if (this.getView().getModel("isYdtFlag")) {
				if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getProperty("/resp") &&
					this.getView().byId("id_DocNoMismatch").getSelected()
				) {
					this.ReportArr.push("Doc No Mismatch II");
					var result = [];
					this.ReportArr.forEach(function (item) {
						if (result.indexOf(item) < 0) {
							result.push(item);
						}
					});
					this.ReportArr = result;
				}
			}
			if (this.getView().getModel("isYdtFlag") !== undefined) {
				if (this.ReportArr !== undefined && this.byId("idReconType").getSelectedKey() != "EINVPR" &&
					this.getView().getModel("isYdtFlag").getProperty("/resp")
				) {
					date = this.byId("idInitiateReconPeriodTax12A").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A").getValue();
				} else {
					date = this.byId("idInitiateReconPeriodTax12A1").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
				}
			} else {
				date = this.byId("idInitiateReconPeriodTax12A1").getValue();
				date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
			}

			if (this.byId("idReconType").getSelectedKey() === "2APR") {
				path = "/aspsapapi/gstr2InitiateMatchingAPRecon.do";

			} else if (this.byId("idReconType").getSelectedKey() === "EINVPR") {
				path = "/aspsapapi/gstr2InitiateMatching.do";
				var result1 = [];
				this.ReportArr.forEach(function (item) {
					if (result1.indexOf(item) < 0) {
						result1.push(item);
					}
				});
				this.ReportArr = result1;
			} else {
				path = "/aspsapapi/gstr2InitiateMatching.do";
				this.ReportArr.push("Dropped 2B Records Report");
				var result1 = [];
				this.ReportArr.forEach(function (item) {
					if (result1.indexOf(item) < 0) {
						result1.push(item);
					}
				});
				this.ReportArr = result1;
			}
			var oMandateData = oView.getModel("Mandat").getProperty("/mandat"),
				vConsolidate = this.byId("id_Consolidated_PR").getSelected();
			if (vConsolidate) {
				var Consolidated = this.byId("id_Consolidated_PR").getText();
				if (Consolidated.includes("2A/6A")) {
					Consolidated = Consolidated.replace("2A/6A", "2A_6A");
				}
				this.ReportArr.push(Consolidated);

				var result2 = [];
				this.ReportArr.forEach(function (item) {
					if (result2.indexOf(item) < 0) {
						result2.push(item);
					}
				});
				this.ReportArr = result2;
			} else {
				for (var i = 0; i < this.ReportArr.length; i++) {
					if (this.ReportArr[i] === this.byId("id_Consolidated_PR").getText()) {
						this.ReportArr.splice(i, 1)
					}
				}
			}
			if (oMandateData === false && this.ReportArr.length === 0) {
				MessageBox.error("Please select at least one report")
				return;
			}

			this.mandatoryCheckSelctData();
			var reconType = this.byId("idReconType").getSelectedKey(),
				oIntiData = {
					"entityId": Number($.sap.entityID),
					"gstins": aGSTIN,
					"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
					"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
					"toTaxPeriod2A": selKey === "Tax Perioid" ? date : "", // Added by chaithra on 2/11/2020
					"fromTaxPeriod2A": selKey === "Tax Perioid" ? date1 : "", // Added by chaithra on 2/11/2020
					"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
					"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
					"addlReports": this.ReportArr === undefined ? [] : this.ReportArr,
					"reconType": reconType,
					"imsPendingInclude": (reconType === "2BPR" && this.byId("cbImsPending").getSelected()),
					"mandatory": oMandateData
				};

			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			$.ajax({
					method: "POST",
					url: path,
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				})
				.done(function (data, status, jqXHR) {
					if (data.resp.status == "Auto 3B Locking is in progress, please initiate recon after sometime") {
						MessageBox.information(data.resp.status);
					} else if (
						data.resp.status.message === "Auto 3B Locking is in progress, please initiate recon after sometime" ||
						data.resp.status.message === "Auto IMS action based on Auto recon parameters is in progress, Please try after sometime."
					) {
						MessageBox.information(data.resp.status.message);
					} else {
						MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
							title: "Initiate Matching Successfully done"
						});
						oIniModel.setData(data);
						oIniView.setModel(oIniModel, "IniData");
					}
				})
				.fail(function (jqXHR, status, err) {});
		},

		onPressRequestIDwise2AFilter: function (oEvent) {
			this.getView().byId("idRequestIDwisePage2A").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			var oReqWiseInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"requestId": this.removeAll(this.byId("Reqid").getSelectedKeys()),
					"reconType": this.byId("idReconType12").getSelectedKey(),
					"initiationDateFrom": this.byId("InDateFrom").getValue(),
					"initiationDateTo": this.byId("InDateTo").getValue(),
					"initiationByUserId": this.removeAll(this.byId("Userid").getSelectedKeys()),
					"initiationByUserEmailId": this.removeAll(this.byId("Emailid").getSelectedKeys()),
					"reconStatus": this.byId("idReconType123").getSelectedKey()
				}
			};

			$.ajax({
					method: "POST",
					url: "/aspsapapi/getgstr2ReportRequestStatusFilter.do",
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				})
				.done(function (data, status, jqXHR) {
					data.resp.forEach(function (e) {
						if (e.toDocDate !== undefined && e.fromDocDate !== undefined) {
							e.toDocDate = e.toDocDate.split(" ")[0];
							e.fromDocDate = e.fromDocDate.split(" ")[0];
						}
						e.isItcRejOpted = e.isItcRejOpted || null;
					});
					this.getView().setModel(new JSONModel(data), "ReqWiseData2A");
				}.bind(this))
				.fail(function (jqXHR, status, err) {});
		},

		onPressRequestIDwise2A: function (oEvent) {
			this.onPressRequestIDwise2AFilter();
			this.onRequestId();
			this.onUserId();
			this.onUserEmails();
		},
		// Arun added code on 11/11/2021
		onPressClearRequestIDwise2A: function () {
			var vDate20 = new Date();
			vDate20.setDate(vDate20.getDate() - 19);
			this.byId("InDateFrom").setDateValue(vDate20);
			this.byId("InDateFrom").setMaxDate(new Date());
			this.byId("InDateTo").setDateValue(new Date());
			this.byId("InDateTo").setMinDate(vDate20);
			this.byId("InDateTo").setMaxDate(new Date());

			this.getView().byId("Reqid").setSelectedKeys([]);
			this.getView().byId("idReconType12").setSelectedKey();
			// this.getView().byId("InDateFrom").setValue(null);
			// this.getView().byId("InDateTo").setValue(null);
			this.getView().byId("Userid").setSelectedKeys([]);
			this.getView().byId("Emailid").setSelectedKeys([]);
			this.getView().byId("idReconType123").setSelectedKey();
			this.onPressRequestIDwise2A();
			// this.getAllGSTIN();
		},

		/**
		 * click on Request Id Link
		 * Developed by: vinay Kodam 29.03.2020
		 * @memberOf com.ey.digigst.view.GSTR2
		 * @private 
		 * @param send request for Request Id Link
		 */
		onPressRequestIDwise2A1: function (oEvent) {
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
		onSearchGstins1: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
			}
			// filter binding
			var oList = this.getView().byId("idInitiateReconList2A");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("idInitiateReconList2A").removeSelections();
			this.byId("checkboxID").setSelected(false);
			/*this.onInitiateRecon();*/
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				this.getView().byId("idInitiateReconList2A").selectAll();
				this.onInitiateRecon();
			}
			//this.onInitiateRecon();
			var oInitiateRecon1 = new sap.ui.model.json.JSONModel([]);
			this.getView().setModel(oInitiateRecon1, "InitiateRecon2A");
		},

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
			var oView = this.getView();
			//======== Added by chaithra for requestidwise refresh issue on 21/1/2021 ========//
			this.getView().byId("idRequestIDwisePage2A").setVisible(false);
			this.getView().byId("idSplitDtl2A").setVisible(true);
			//========  Code ended by chaithra on 21/1/2021 ========//
			//var oPath = [];
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTIN2A").getData();
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				/*var j = oPath[i].split('/')[1];*/
				aGSTIN.push(oModelData[j].gstin);
			}

			if (aGSTIN.length === 0) {
				var oGSTIN2 = new JSONModel([]);
				oView.setModel(oGSTIN2, "InitiateRecon2A");
				return;
			}

			var date, date1;
			if (this.getView().getModel("isYdtFlag") !== undefined) {
				if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp) {
					date = this.byId("idInitiateReconPeriodTax12A").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A").getValue();
				} else {
					date = this.byId("idInitiateReconPeriodTax12A1").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
				}
			} else {
				date = this.byId("idInitiateReconPeriodTax12A1").getValue();
				date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
			}

			var selKey = this.byId("idDateRange2A").getSelectedKey(),
				postData = {
					"req": {
						"type": "",
						"entityId": [$.sap.entityID],
						"dataSecAttrs": {
							"GSTIN": aGSTIN
						},
						"gstin": aGSTIN,
						"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
						"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
						"toTaxPeriod2A": selKey === "Tax Perioid" ? date : "",
						"fromTaxPeriod2A": selKey === "Tax Perioid" ? date1 : "",
						"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
						"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
						"reconType": this.byId("idReconType").getSelectedKey(),
						"isPendingOpted": (this.byId("cbImsPending").getSelected() ? 'Yes' : 'No')
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
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2InitiateRecon.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().byId("idintable2A").setVisibleRowCount(data.resp.length + 1);
					this.getView().setModel(new JSONModel(data.resp), "InitiateRecon2A");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "InitiateRecon2A");
				}.bind(this));
		},

		onIntiateReconFullScreen: function (oEvt) {
			var data = this.getView().getModel("InitiateRecon2A").getProperty("/");
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
		fnPressDownInit: function () {
			this.getView().byId("iMoreIniat2A").setVisible(false);
			this.getView().byId("iLessIniat2A").setVisible(true);
			this.getView().byId("idvbox12A").setVisible(false);
			this.getView().byId("idGrid12A").setVisible(false);
			this.getView().byId("idGrid2").setVisible(false);
			this.getView().byId("idvbox2").setVisible(false);
			this.getView().byId("idMandatory").setVisible(false);
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
			this.getView().byId("idMandatory").setVisible(true);
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
			this.repName = oEvt.getSource().getBindingContext("ReportDownload").getObject().path;
			var request = {
				"req": {
					//"configId": this.oReqId.toString(),
					"filePath": this.repName.toString(),
					"reconType": this.reconType
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr2ReconReportDownload.do";
			this.excelDownload(request, oReqExcelPath);
		},

		onFragDownload1: function (oEvt) {
			this.repName = oEvt.getSource().getBindingContext("ReportDownload1").getObject().path;
			var request = {
				"req": {
					//"configId": this.oReqId.toString(),
					"filePath": this.repName.toString(),
					"reconType": this.reconType
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr2ReconReportDownload.do";
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
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.NewReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			if (oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().status ===
				"REPORT_GENERATION_FAILED") {
				this.byId("repId").setVisible(true);
			} else {
				this.byId("repId").setVisible(false);
			}

			var oIntiData = {
				"req": {
					"configId": this.oReqId,
					"reconType": this.reconType
				}
			};

			var Model = new JSONModel();
			var that = this;
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
						/*Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");*/
						var retArr = [];
						var curL1Obj = {}; // the current level1 object.
						var curL2Obj = {}; // the current level2 object.
						for (var i = 0; i < data.resp.transactionalRecords.length; i++) {
							var ele = data.resp.transactionalRecords[i];
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
						View.setModel(oGSTIN, "ReportDownload");
						that.byId("tabSumm1").setVisibleRowCount(data.resp.summaryRecords.length);
						var oGSTIN = new JSONModel(data.resp.summaryRecords);
						View.setModel(oGSTIN, "ReportDownload1");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "ReportDownload");
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "ReportDownload1");
					}
				}).fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "ReportDownload");
				});
			});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onRG: function () {
			var oView = this.getView();
			var Request = {
				"req": {
					"configId": this.oReqId
				}
			};
			var ELDetails = "/aspsapapi/gstr2ReconReportReGenerate.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: ELDetails,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(data.resp);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.success(JSON.parse(jqXHR.responseText).resp);
					/*var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "InitiateRecon2A");*/
					//MessageBox.error("initiateRecon : Error");
				});
			});
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
			var ReconType = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().reconType;
			for (var i = 0; i < TabData.resp.length; i++) {
				if (reqId === TabData.resp[i].requestId) {
					gstins.push(TabData.resp[i].gstins);
				}
			}
			var oReqWiseModel1 = new JSONModel();
			oReqWiseModel1.setData(gstins[0]);

			var oButton = oEvt.getSource();
			if (ReconType === "2BPR" || ReconType === "EINVPR") {
				if (!this._oGstinPopover1) {
					this._oGstinPopover1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2B", this);
					this.getView().addDependent(this._oGstinPopover1);
				}

				this._oGstinPopover1.setModel(oReqWiseModel1, "gstins2A");
				this._oGstinPopover1.openBy(oButton);
			} else {
				if (!this._oGstinPopover) {
					this._oGstinPopover = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.Popover2A", this);
					this.getView().addDependent(this._oGstinPopover);
				}

				this._oGstinPopover.setModel(oReqWiseModel1, "gstins2A");
				this._oGstinPopover.openBy(oButton);
			}
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
			if (this.getView().getModel("isYdtFlag") !== undefined) {
				if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp && this.byId("idReconType").getSelectedKey() !=
					"EINVPR") {
					this.byId("idInitiateReconPeriodTax2A1").setDateValue(this.byId("idInitiateReconPeriodTax2A").getDateValue());
					this.byId("idInitiateReconPeriodTax12A1").setDateValue(this.byId("idInitiateReconPeriodTax12A").getDateValue());
					this.byId("idInitiateReconPeriodTax12A1").setDisplayFormat("MMM yyyy");
				}
			}
		},

		onToTaxPeriodChange: function () {
			if (this.getView().getModel("isYdtFlag") !== undefined) {
				if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp && this.byId("idReconType").getSelectedKey() !=
					"EINVPR") {
					//this.byId("idInitiateReconPeriodTax2A1").setDateValue(this.byId("idInitiateReconPeriodTax2A").getDateValue());
					this.byId("idInitiateReconPeriodTax12A1").setDateValue(this.byId("idInitiateReconPeriodTax12A").getDateValue());
					this.byId("idInitiateReconPeriodTax12A1").setDisplayFormat("MMM yyyy");
				}
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

			var date, date1;
			if (this.getView().getModel("isYdtFlag") !== undefined) {
				if (this.ReportArr !== undefined && this.getView().getModel("isYdtFlag").getData().resp) {
					date = this.byId("idInitiateReconPeriodTax12A").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A").getValue();
				} else {
					date = this.byId("idInitiateReconPeriodTax12A1").getValue();
					date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
				}
			} else {
				date = this.byId("idInitiateReconPeriodTax12A1").getValue();
				date1 = this.byId("idInitiateReconPeriodTax2A1").getValue();
			}

			var selKey = this.byId("idDateRange2A").getSelectedKey(),
				postData = {
					"req": {
						"dataSecAttrs": {
							"GSTIN": aGSTIN
						},
						"gstin": aGSTIN,
						"toTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax12A").getValue() : "",
						"fromTaxPeriod": selKey === "Tax Perioid" ? this.byId("idInitiateReconPeriodTax2A").getValue() : "",
						"toTaxPeriod2A": selKey === "Tax Perioid" ? date : "", // Added by chaithra on 2/11/2020
						"fromTaxPeriod2A": selKey === "Tax Perioid" ? date1 : "", // Added by chaithra on 2/11/2020
						"fromDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod2A").getDateValue()) : "",
						"toDocDate": selKey === "Document Date" ? this._formatDate(this.byId("idInitiateReconPeriod12A").getDateValue()) : "",
						"reconType": this.byId("idReconType").getSelectedKey(),
						"isPendingOpted": (this.byId("cbImsPending").getSelected() ? 'Yes' : 'No')
					}
				};
			this.excelDownload(postData, "/aspsapapi/gstr2InitiateReconDownloadReport.do");
		},

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
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					onClose: function (oActionSuccess) {
						if (oActionSuccess === "YES") {
							sap.ui.core.BusyIndicator.show(0);
							$.ajax({
									method: "POST",
									url: "/aspsapapi/deleteExcludedVendor.do",
									contentType: "application/json",
									data: JSON.stringify(PayLoad)
								})
								.done(function (data, status, jqXHR) {
									sap.ui.core.BusyIndicator.hide();
									if (data.hdr.status == "S") {
										MessageBox.success("Selected Vendors are deleted and same will be included in reconciliation process");
										that.onLoadVendorList();
									} else {
										MessageBox.information("While deleting selected vendors having issue");
										that.onLoadVendorList();
									}

								})
								.fail(function (jqXHR, status, err) {
									sap.ui.core.BusyIndicator.hide();
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

		onExpandPI: function (oEvent) {
			if (oEvent.getSource().getId().includes("expand")) {
				this.byId("tabSumm").expandToLevel(1);
			} else {
				this.byId("tabSumm").collapseAll();
			}
		},

		onInitiateRec2AReconType: function (oEvent, optAns, imsOpt) {
			if (!this._dReconType) {
				this._dReconType = new sap.m.Popover({
					showHeader: false,
					content: [
						new sap.m.Text({
							text: "{/infoMsg}"
						}).addStyleClass("sapUiTinyMargin")
					],
				});
			}
			var infoMsg = (optAns === 'Yes' ? "GSTR 2B rejected records are included in recon" :
				"GSTR 2B rejected records are not included in recon");
			infoMsg += "\n" + (imsOpt ? "IMS Active Pending records are included in recon" :
				"IMS Active Pending records are not included in recon");

			this._dReconType.setModel(new JSONModel({
				"infoMsg": infoMsg
			}));
			this._dReconType.openBy(oEvent.getSource());
		}
	});
});