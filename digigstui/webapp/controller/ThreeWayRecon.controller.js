sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator"
], function (BaseController, JSONModel, Formatter, MessageBox, Filter, FilterOperator) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.ThreeWayRecon", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ThreeWayRecon
		 */

		onInit: function () {
			var object = {
					"Label": "2A/6A",
					"TAX": true,
					"DOC": false,
					"Label2": "2A/6A"
				},
				vDate = new Date(),
				date1 = new Date(),
				vPeriod = new Date(vDate.getFullYear(), vDate.getMonth(), 1),
				vDate1 = new Date(vDate.getFullYear(), vDate.getUTCMonth(), vDate.getDate() - 2),
				vDate2 = new Date();

			date1.setDate(date1.getDate() - 90);
			vDate2 = new Date(vDate2.setDate(vDate2.getDate() - 1));

			var vPeriod3 = new Date(date1.getFullYear(), date1.getMonth(), 1);
			this.getView().setModel(new JSONModel(object), "Display");

			this._setDateProperty("idInitiateReconPeriodTax2A", date1, vDate, new Date("2017", "05", "01"));
			this._setDateProperty("idInitiateReconPeriodTax12A", vDate, vDate, vPeriod3);

			this._setDateProperty("idInitiateReconPeriod2A", date1, vDate, new Date("2017", "05", "01"));
			this._setDateProperty("idInitiateReconPeriod12A", vDate, vDate, date1);

			this._setDateProperty("InDateFrom", null, vDate, new Date("2017", "05", "01"));
			this._setDateProperty("InDateTo", null, vDate, date1);

			this._setDateProperty("gstr3bDate1", vPeriod, vDate1, new Date("2017", "05", "01"));
			this._setDateProperty("gstr3bDate2", vDate2, vDate2, vPeriod);
		},

		onBeforeRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onRequestId();
				this.onUserEmails();
				this.onUserId();
			}
		},

		onAfterRendering: function () {
			if (this.glbEntityId1 !== $.sap.entityID) {
				this.glbEntityId1 = $.sap.entityID;
				this.onPressGoForGSTIN2A();
				this.onPressClearEWB();
			}
		},

		TableBinding: function () {
			var aGstin = this.getView().byId("GSTINEntityID").getSelectedKeys();
			var EWBInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": this.removeAll(aGstin),
					"criteria": this.byId("idReconType12").getSelectedKey(),
					"fromdate": this.byId("gstr3bDate1").getValue(),
					"toDate": this.byId("gstr3bDate2").getValue()
				}
			};
			var EWBTableModel = new JSONModel();
			var EWBTableView = this.getView();
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEWBRequestStatusSummaryFilter.do",
					contentType: "application/json",
					data: JSON.stringify(EWBInfo)
				})
				.done(function (data, status, jqXHR) {

					EWBTableModel.setData(data);
					EWBTableView.setModel(EWBTableModel, "EWB");
				})
				.fail(function (jqXHR, status, err) {});
		},

		onMenuItemPressdownload: function (oEvt) {
			var key = oEvt.getParameters().item.getKey();
			var oSelectedItem = this.getView().byId("gstrTabId").getSelectedIndices();
			var oModelForTab1 = this.byId("gstrTabId").getModel("EWB").getData().resp;
			if (oSelectedItem.length == 0) {
				MessageBox.warning("Please select atleast one GSTIN");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
			}

			var postPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": aGstin,
					"criteria": this.byId("idReconType12").getSelectedKey(),
					"fromdate": this.byId("gstr3bDate1").getValue(),
					"toDate": this.byId("gstr3bDate2").getValue()
				}
			};
			var url;
			if (key === "EWBData") {
				url = "/aspsapapi/getEWBDownloadFile.do";
				this.AsyncReportDownload(postPayload, url);
				return;
			} else if (key === "EWB_Statu_Report") {
				delete postPayload.req.criteria;
				url = "/aspsapapi/getEwableStatusReport.do";
				this.excelDownload(postPayload, url);
				return;
			} else {
				url = "/aspsapapi/getEWBDetailedReportDownload.do";
				this.AsyncReportDownload(postPayload, url);
				return;
			}
		},

		onPressEWBData: function (oEvt) {
			var that = this;
			var oSelectedItem = this.getView().byId("gstrTabId").getSelectedIndices();
			var oModelForTab1 = this.byId("gstrTabId").getModel("EWB").getData().resp;
			if (oSelectedItem.length == 0) {
				MessageBox.warning("Please select atleast one GSTIN");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				aGstin.push(oModelForTab1[oSelectedItem[i]].gstin);
			}
			var postPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": aGstin,
					"fromdate": this.byId("gstr3bDate1").getValue(),
					"toDate": this.byId("gstr3bDate2").getValue()
				}
			};
			var oMessage = "Do you want to initiate the request  ?";
			MessageBox.information(oMessage, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "OK") {
						that.onGetEWBData(postPayload);
					}
				}
			});
		},

		onGetEWBData: function (postPayload) {
			var that = this;
			var Model = new JSONModel();
			var View = this.getView();
			$.ajax({
					method: "POST",
					url: "/aspsapapi/manualEwbGetCall.do",
					contentType: "application/json",
					data: JSON.stringify(postPayload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.information(data.resp);
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("manualEwbGetCall" + err);
				});
		},

		onSearchEWB: function (oEvent) {
			this.TableBinding();
		},

		onPressClearEWB: function (oEvent) {
			this.byId("gstr3bDate1").setDateValue(new Date());
			this.byId("gstr3bDate2").setDateValue(new Date());
			this.byId("GSTINEntityID").setSelectedKeys([]);
			this.byId("idReconType12").setSelectedKey([]);
			this.TableBinding();
		},

		onFromTaxPeriodChange: function (oEvent) {
			if (oEvent.getSource().getId().includes("idInitiateReconPeriodTax2A")) {
				var vDatePicker = "idInitiateReconPeriodTax12A";
			} else if (oEvent.getSource().getId().includes("idInitiateReconPeriodTax12A")) {
				vDatePicker = "idInitiateReconPeriodTax12A";
			}
			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}
			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate)
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		handleChangeF: function (oEvent) {
			var fromDate = this.byId("InDateFrom").getDateValue();
			var toDate = this.byId("InDateTo").getDateValue();

			if (fromDate > toDate) {
				this.byId("InDateTo").setDateValue(oEvent.getSource().getDateValue());
				this.byId("InDateTo").setMinDate(oEvent.getSource().getDateValue());
			} else {
				this.byId("InDateTo").setMinDate(oEvent.getSource().getDateValue());
			}
		},

		onRequestId: function () {
			var payload = {
				"entityId": $.sap.entityID
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEWB3WayRequestIds.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.requestDetails.unshift({
						requestId: "All"
					});
					that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2RequestIds");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onUserId: function () {
			if ($.sap.entityID === "" || $.sap.entityID === undefined) {
				return;
			}
			var payload = {
				"entityId": $.sap.entityID
			};
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEWB3WayUserNames.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.requestDetails.unshift({
						userName: "All"
					});
					that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2UserNames");
				})
				.fail(function (jqXHR, status, err) {
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
					url: "/aspsapapi/getEWB3WayEmailIds.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.requestDetails.unshift({
						email: "All"
					});
					that.getView().setModel(new JSONModel(data.resp.requestDetails), "getgstr2EmailIds");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		AsyncReportDownload: function (postPayload, url) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(postPayload)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						MessageBox.success("Initiated Request ID : " + oData.resp.id +
							"\n Navigate to \"Reports >> Request Reports\"");
					} else {
						MessageBox.error(oData.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onInitiPress: function () {
			if (!this._oDialog1) {
				this._oDialog1 = sap.ui.xmlfragment("com.ey.digigst.fragments.3wayRecon.InitiateRec", this);
				this.getView().addDependent(this._oDialog1);
			}
			this._oDialog1.open();
		},

		onRecontypeChange: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			var that = this;
			if (key === "BillDate") {
				this.getView().byId("idEWBData").setVisible(true);
				this.getView().byId("DOWNREPORTS").addItem(new sap.m.MenuItem({
					"text": "Get EWB Status Report",
					"key": "EWB_Statu_Report",
				}));
			}
			if (key === "DocumentDate") {
				this.getView().byId("idEWBData").setVisible(false);
				for (var i = 0; i <= this.getView().byId("DOWNREPORTS").getItems().length; i++) {
					if (this.getView().byId("DOWNREPORTS").getItems()[i].getText() === "Get EWB Status Report") {
						this.getView().byId("DOWNREPORTS").removeItem(i);
					}
				}
			}
			if (key === "DocDateWise") {
				that.getView().getModel("Display").getData().DOC = true;
				that.getView().getModel("Display").getData().TAX = false;
			} else {
				that.getView().getModel("Display").getData().DOC = false;
				that.getView().getModel("Display").getData().TAX = true;
			}
			that.getView().getModel("Display").refresh(true);
		},

		onCloseDialog: function () {
			this._oDialog1.close();
		},

		onCloseDialogBulkSave1: function () {
			this._oDialog12.close();
		},

		onReqPress: function () {
			this.byId("idSplitDtl2A").setVisible(false);
			this.byId("idRequestIDwisePage2A").setVisible(true);
			this.onRequestId();
			this.onUserEmails();
			this.onUserId();
			this.onPressRequestIDwise2A();
		},

		onPressRequestIDwiseBack: function () {
			this.byId("idSplitDtl2A").setVisible(true);
			this.byId("idRequestIDwisePage2A").setVisible(false);
		},

		onPressGoForGSTIN2A: function (oEvent) {
			this.byId("searchId").setValue("");
			var selKey = this.byId("idReconType").getSelectedKey();
			var oView = this.getView();
			var that = this;
			var postData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"toReturnPeriod": selKey === "ReturnPeriodWise" ? this.byId("idInitiateReconPeriodTax12A").getValue() : this.byId(
						"idInitiateReconPeriod12A").getValue(),
					"fromReturnPeriod": selKey === "ReturnPeriodWise" ? this.byId("idInitiateReconPeriodTax2A").getValue() : this.byId(
						"idInitiateReconPeriod2A").getValue(),
					"criteria": this.byId("idReconType").getSelectedKey()
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDataForRecon3WaySummary.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						var oGSTIN = new JSONModel(data.resp.det);
						oView.setModel(oGSTIN, "GSTIN2A");
						oView.byId("checkboxID").setSelected(true);
						oView.byId("idInitiateReconList2A").selectAll();
						that.onInitiateRecon();
					} else {
						var oGSTIN1 = new JSONModel([]);
						oView.setModel(oGSTIN1, "GSTIN2A");
						oView.setModel(oGSTIN1, "InitiateRecon2A");
					}
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "GSTIN2A");
					oView.setModel(oGSTIN2, "InitiateRecon2A");
				});
		},

		onInitiateRecon: function (oEvt) {
			var oView = this.getView();
			this.getView().byId("idRequestIDwisePage2A").setVisible(false);
			this.getView().byId("idSplitDtl2A").setVisible(true);
			var aGSTIN = [];
			var oModelData = oView.getModel("GSTIN2A").getData();
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				aGSTIN.push(oModelData[j].gstin);
			}

			if (aGSTIN.length === 0) {
				sap.m.MessageBox.warning("Please Select at least one Supplier Gstin");
				return;
			}

			var criteria;
			if (this.byId("idReconType").getSelectedKey() === "") {
				criteria = "ReturnPeriodWise"
			} else {
				criteria = this.byId("idReconType").getSelectedKey();
			}
			var postData = {
				"req": {
					"entityId": [Number($.sap.entityID)],
					"dataSecAttrs": {
						"GSTIN": aGSTIN
					},
					"gstin": aGSTIN,
					"toTaxPeriod": criteria === "ReturnPeriodWise" ? this.byId("idInitiateReconPeriodTax12A").getValue() : this.byId(
						"idInitiateReconPeriod12A").getValue(),
					"fromTaxPeriod": criteria === "ReturnPeriodWise" ? this.byId("idInitiateReconPeriodTax2A").getValue() : this.byId(
						"idInitiateReconPeriod2A").getValue(),
					"criteria": criteria
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
			var oView = this.getView();
			var jsonForSearch = JSON.stringify(postData);
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/ewb3WaySummaryInitiateRecon.do",
					contentType: "application/json",
					data: jsonForSearch
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oInitiateRecon1 = new sap.ui.model.json.JSONModel(data.resp);
					var omodellength = oInitiateRecon1.setSizeLimit(oInitiateRecon1.oData.length);
					oView.setModel(oInitiateRecon1, "GSTR3B");
				})
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					var oGSTIN2 = new JSONModel([]);
					oView.setModel(oGSTIN2, "GSTR3B");
				});
		},

		onInitiPress1: function () {
			var that = this;
			var selItems = this.byId("idInitiateReconList2A").getSelectedContextPaths();
			if (selItems.length === 0) {
				MessageBox.error("Please Select GSTIN");
				return;
			}
			MessageBox.confirm("Are you sure you want to Initiate Matching?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that.onInitiPress12();
					}
				}
			});
		},

		onSearchGstins: function (oEvent) {
			var aFilter = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery) {
				aFilter.push(new Filter("gstin", FilterOperator.StartsWith, sQuery));
			}
			var oList = this.getView().byId("idInitiateReconList2A");
			var oBinding = oList.getBinding("items");
			oBinding.filter(aFilter);
			this.getView().byId("idInitiateReconList2A").removeSelections();
			this.byId("checkboxID").setSelected(false);
			if (sQuery === "") {
				this.byId("checkboxID").setSelected(true);
				this.getView().byId("idInitiateReconList2A").selectAll();
				this.onSelectionChange1();
			}
			var oInitiateRecon1 = new sap.ui.model.json.JSONModel([]);
			this.getView().setModel(oInitiateRecon1, "GSTR3B");
		},

		onSelectallGSTIN: function (oEvent) {
			if (oEvent.getSource().getSelected() === false) {
				this.getView().byId("idInitiateReconList2A").removeSelections();
			} else {
				this.getView().byId("idInitiateReconList2A").selectAll();
			}
			this.onInitiateRecon();
		},

		onSelectionChange1: function (oEvent) {
			this.onInitiateRecon(oEvent);
		},

		onInitiateMatch: function () {
			if (!this._oGstinPopover7) {
				this._oGstinPopover7 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.3wayRecon.InitiateRec", this);
				this.getView().addDependent(this._oGstinPopover7);
			}
			this._oGstinPopover7.open();
		},

		onInitiPress12: function () {
			var oView = this.getView();
			var aGSTIN = [];

			var oModelData = oView.getModel("GSTIN2A").getData();
			var oPath = oView.byId("idInitiateReconList2A").getSelectedItems();
			for (var i = 0; i < oPath.length; i++) {
				var j = oPath[i].oBindingContexts.GSTIN2A.sPath.split('/')[1];
				/*var j = oPath[i].split('/')[1];*/
				aGSTIN.push(oModelData[j].gstin);
			}

			var selKey = this.byId("idReconType").getSelectedKey();
			var oIntiData = {
				"req": {
					"entityId": Number($.sap.entityID),
					"gstins": aGSTIN,
					"toReturnPeriod": selKey === "ReturnPeriodWise" ?
						this.byId("idInitiateReconPeriodTax12A").getValue() : this.byId("idInitiateReconPeriod12A").getValue(),
					"fromReturnPeriod": selKey === "ReturnPeriodWise" ?
						this.byId("idInitiateReconPeriodTax2A").getValue() : this.byId("idInitiateReconPeriod2A").getValue(),
					"criteria": this.byId("idReconType").getSelectedKey(),
					"gstr1Type": "FileUpload",
					"eInvType": "GetGstin",
					"gewbType": "FileUpload",
					"addReport": ["DropOutRecords"]
				}
			};

			var oIniModel = new JSONModel();
			var oIniView = this.getView();
			var that = this;
			$.ajax({
					method: "POST",
					url: "/aspsapapi/EWB3WayInitiateRecon.do",
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				})
				.done(function (data, status, jqXHR) {
					that._oGstinPopover7.close();
					MessageBox.information("Please click on Request ID wise Link to download the Recon Reports", {
						title: "Initiate Matching Successfully done"
					});
					oIniModel.setData(data);
					oIniView.setModel(oIniModel, "IniData");
				})
				.fail(function (jqXHR, status, err) {});
		},

		onCloseDialogI: function () {
			this._oGstinPopover7.close();
		},

		onPressRequestIDwise2A: function (oEvent) {
			this.getView().byId("idRequestIDwisePage2A").setVisible(true);
			this.getView().byId("idSplitDtl2A").setVisible(false);
			var oReqWiseInfo = {
				"req": {
					"entityId": $.sap.entityID,
					"requestId": this.removeAll(this.byId("Reqid").getSelectedKeys()),
					//"reconType": this.byId("idReconType12").getSelectedKey(),
					"initiationDateFrom": this.byId("InDateFrom").getValue(),
					"initiationDateTo": this.byId("InDateTo").getValue(),
					"initiationByUserId": this.removeAll(this.byId("Userid").getSelectedKeys()),
					"initiationByUserEmailId": this.removeAll(this.byId("Emailid").getSelectedKeys()),
					"reconStatus": this.byId("idReconType123").getSelectedKey()
				}
			};

			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getEWB3WayReportRequestStatusFilter.do",
					contentType: "application/json",
					data: JSON.stringify(oReqWiseInfo)
				})
				.done(function (data, status, jqXHR) {
					for (var i = 0; i < data.resp.length; i++) {
						if (data.resp[i].toDocDate !== undefined && data.resp[i].fromDocDate !== undefined) {
							data.resp[i].toDocDate = data.resp[i].toDocDate.split(" ")[0];
							data.resp[i].fromDocDate = data.resp[i].fromDocDate.split(" ")[0];
						}
					}

					oReqWiseModel.setData(data);
					oReqWiseView.setModel(oReqWiseModel, "ReqWiseData2A");
				})
				.fail(function (jqXHR, status, err) {});
		},

		onPressGSTIN: function (oEvt) {
			var gstins = [];
			var TabData = this.getView().getModel("ReqWiseData2A").getData();
			var reqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			for (var i = 0; i < TabData.resp.length; i++) {
				if (reqId === TabData.resp[i].requestId) {
					gstins.push(TabData.resp[i].gstins);
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

		onConfigExtractPress2A1: function (oEvt) {
			this.oReqId = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().requestId;
			this.reconType = oEvt.getSource().getEventingParent().getParent().getBindingContext("ReqWiseData2A").getObject().reconType;
			if (!this._getGstr2a1) {
				this._getGstr2a1 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr2.ReportDownload", this);
				this.getView().addDependent(this._getGstr2a1);
			}

			var oIntiData = {
				"req": {
					"configId": this.oReqId,
					//"reconType": this.reconType
				}
			};

			var Model = new JSONModel();
			var View = this.getView();
			$.ajax({
					method: "POST",
					url: "/aspsapapi/ewb3WayDownloadIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(oIntiData)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						Model.setData(data.resp);
						View.setModel(Model, "DownloadDocument");
					} else {
						var oGSTIN12 = new JSONModel([]);
						View.setModel(oGSTIN12, "DownloadDocument");
					}
				})
				.fail(function (jqXHR, status, err) {
					var oGSTIN12 = new JSONModel([]);
					View.setModel(oGSTIN12, "DownloadDocument");
				});
			this._getGstr2a1.open();
		},

		onCloseDialogDow: function () {
			this._getGstr2a1.close();
		},

		onPressClearRequestIDwise2A: function () {
			this.getView().byId("Reqid").setSelectedKeys([]);
			this.getView().byId("InDateFrom").setValue(null);
			this.getView().byId("InDateTo").setValue(null);
			this.getView().byId("Userid").setSelectedKeys([]);
			this.getView().byId("Emailid").setSelectedKeys([]);
			this.getView().byId("idReconType123").setSelectedKey();
			this.onPressRequestIDwise2A();
		},

		onFragDownload: function (oEvt) {
			this.repName = oEvt.getSource().getBindingContext("DownloadDocument").getObject().reportName;
			var url = "/aspsapapi/eWB3WayDownloadDocument.do?configId=" + this.oReqId + "&reportType=" + this.repName;
			window.open(url);
		},

		onFromDateChange: function (oevt) {
			var toDate = this.byId("gstr3bDate2").getDateValue(),
				fromDate = this.byId("gstr3bDate1").getDateValue();
			if (fromDate > toDate) {
				this.byId("gstr3bDate2").setDateValue(oevt.getSource().getDateValue());
				this.byId("gstr3bDate2").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("gstr3bDate2").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onFromDateChangeInt: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriodTax12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriodTax2A").getDateValue();
			if (fromDate > toDate) {
				this.byId("idInitiateReconPeriodTax12A").setDateValue(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriodTax12A").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idInitiateReconPeriodTax12A").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onFromDateChangeInt1: function (oevt) {
			var toDate = this.byId("idInitiateReconPeriod12A").getDateValue(),
				fromDate = this.byId("idInitiateReconPeriod2A").getDateValue();
			if (fromDate > toDate) {
				this.byId("idInitiateReconPeriod12A").setDateValue(oevt.getSource().getDateValue());
				this.byId("idInitiateReconPeriod12A").setMinDate(oevt.getSource().getDateValue());
			} else {
				this.byId("idInitiateReconPeriod12A").setMinDate(oevt.getSource().getDateValue());
			}
		},

		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("EWB").getObject();
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
				})
				.done(function (data, status, jqXHR) {
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
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dAuthTokenConfirmation").setBusy(false);
				});
		},

		onPressResendOTP: function () {
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
				})
				.done(function (data, status, jqXHR) {
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
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dVerifyAuthToken").setBusy(false);
				});
		},

		validateOTP: function (oEvent) {
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
				})
				.done(function (data, status, jqXHR) {
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
				})
				.fail(function (jqXHR, status, err) {
					that.byId("dVerifyAuthToken").setBusy(false);
					that._dGenerateOTP.close();
				});
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		}
	});
});