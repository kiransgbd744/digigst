sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox",
	"sap/m/Button",
	"sap/m/Dialog"
], function (Controller, JSONModel, Fragment, MessageBox, Button, Dialog) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GSTNotices", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GSTNotices
		 */
		onInit: function () {
			var obj = {
				"btnSubSegment": "summary",
				"title": "GST Notice Analyzer",
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 10,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false
			};
			this.getView().setModel(new JSONModel(obj), "ViewProperty");
			this._bindDefaultData();

			this.getOwnerComponent().getRouter().getRoute("GSTNotices").attachPatternMatched(this._onRouteMatched, this);
		},
		_onRouteMatched: function (oEvent) {

			var oHashChanger = this.getRouter().getHashChanger(),
				oName = oEvent.getParameter("name");

			oHashChanger.setHash("GSTNotices");
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._getAllGstin();
				this.bindFilterModel();
				this.onSegmentBtnChange("summary");
			}
		},

		_getAllGstin: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstinList.do",
					data: JSON.stringify({
						"req": {
							"entityId": [$.sap.entityID],
							"src": "GST_NOTICE"
						}
					}),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "GstinModel");
				}.bind(this))
				.fail(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		// Function to format date to MMyyyy
		formatTaxPeriod: function (date) {
			var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month is 0-based, so add 1
			var year = date.getFullYear();
			return month + year; // Return in MMyyyy format
		},

		// Function to format date to dd-MM-yyyy
		formatDate: function (date) {
			var day = date.getDate().toString().padStart(2, '0'); // Pad with leading zero if needed
			var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month is 0-based, so add 1
			var year = date.getFullYear();
			return day + '-' + month + '-' + year; // Return in dd-MM-yyyy format
		},

		bindFilterModel: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			var pfilerData = {
				"gstins": [],
				"selectionCriteria": "Datewise", //or "Date wise"
				"fromTaxPeriod": this.formatTaxPeriod(vDate),
				"toTaxPeriod": this.formatTaxPeriod(new Date()),
				"fromDate": this.formatDate(vDate),
				"toDate": this.formatDate(new Date())
			}
			this.getView().setModel(new JSONModel(pfilerData), "pfilter");

			var sfilerData = {
				"gstins": "",
				"selectionCriteria": "Datewise", //or "Date wise"
				"fromTaxPeriod": null,
				"toTaxPeriod": null,
				"fromDate": null,
				"toDate": null
			}
			this.getView().setModel(new JSONModel(sfilerData), "sfilter");
		},

		_bindDefaultData: function () {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			this._setFromDateProperty("iFromDate", vDate, new Date());
			this._setToDateProperty("iToDate", new Date(), new Date(), vDate);
			this._setFromDateProperty("idPFromtaxPeriod", vDate, new Date());
			this._setToDateProperty("idPTotaxPeriod", new Date(), new Date(), vDate);

			this._setFromDateProperty("iSFromDate");
			this._setToDateProperty("iSToDate");
			this._setFromDateProperty("idSFromtaxPeriod");
			this._setToDateProperty("idSTotaxPeriod");
		},

		onChangeDateValue: function (oEvent) {
			if (oEvent.getSource().getId().includes("idPFromtaxPeriod")) {
				var vDatePicker = "idPTotaxPeriod";
			} else if (oEvent.getSource().getId().includes("idSFromtaxPeriod")) {
				vDatePicker = "idSTotaxPeriod";
			} else if (oEvent.getSource().getId().includes("iFromDate")) {
				vDatePicker = "iToDate";
			} else if (oEvent.getSource().getId().includes("iSFromDate")) {
				vDatePicker = "iSToDate";
			}

			if (oEvent.getSource().getValueFormat() === "MMyyyy") {
				var fromDate = oEvent.getSource().getDateValue();
				fromDate = new Date(fromDate.setDate('01'));
			} else {
				var fromDate = oEvent.getSource().getDateValue();
			}

			var toDate = this.byId(vDatePicker).getDateValue();
			if (fromDate > toDate) {
				this.byId(vDatePicker).setDateValue(fromDate);
			}
			this.byId(vDatePicker).setMinDate(fromDate);
		},

		onBackImsSummary: function () {
			this.onSegmentBtnChange("summary")
		},

		onPressGSTIN: function (oEvent) {

			var obj = oEvent.getSource().getBindingContext("ProcessedRecord").getObject();
			var oPData = this.getView().getModel("pfilter").getData();
			var sModel = this.getView().getModel("sfilter");

			var fromPeriod = this.byId("idPFromtaxPeriod").getDateValue();
			if (fromPeriod) {
				fromPeriod.setDate(1); // Set to the first day of the month
				this.byId("idSFromtaxPeriod").setMaxDate(new Date());
				this.byId("idSTotaxPeriod").setMaxDate(new Date());
				this.byId("idSTotaxPeriod").setMinDate(fromPeriod);
			}

			var fromDate = this.byId("iFromDate").getDateValue();
			if (fromDate) {
				fromDate = new Date(fromDate);
				this.byId("iSFromDate").setMaxDate(new Date());
				this.byId("iSToDate").setMaxDate(new Date());
				this.byId("iSToDate").setMinDate(fromDate);
			}

			sModel.setProperty("/gstins", obj.gstin);
			sModel.setProperty("/selectionCriteria", oPData.selectionCriteria);
			sModel.setProperty("/fromTaxPeriod", oPData.fromTaxPeriod);
			sModel.setProperty("/toTaxPeriod", oPData.toTaxPeriod);
			sModel.setProperty("/fromDate", oPData.fromDate);
			sModel.setProperty("/toDate", oPData.toDate);

			sModel.refresh(true);

			this.onSegmentBtnChange("details");
		},

		onSegmentBtnChange: function (btn) {
			var oModel = this.getView().getModel("ViewProperty");
			switch (btn) {
			case "summary":
				oModel.setProperty("/title", "GST Notice Analyzer");
				oModel.setProperty("/btnSubSegment", "summary");
				this.onSearchSummary();
				break;
			case "details":
				oModel.setProperty("/title", "List of GST Notice & Responses");
				oModel.setProperty("/btnSubSegment", "details");
				this.onSearchDetails();
				break;
			}
			oModel.refresh(true);
		},
		onSearch: function (flag) {
			if (flag == "P") {
				this.onSearchSummary()
			} else {
				this.onSearchDetails();
			}

		},
		onSearchSummary: function () {
			var oData = this.getView().getModel("pfilter").getData();
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID, // Replace with actual entityId if dynamic
					"gstins": oData.gstins,
					"fromTaxPeriod": oData.fromTaxPeriod,
					"toTaxPeriod": oData.toTaxPeriod,
					"fromDate": oData.fromDate,
					"toDate": oData.toDate,
					"selectionCriteria": oData.selectionCriteria === 'TaxPeriod' ? "Tax Period" : "Date wise"
				}
			};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/fetchNoticeSummary.do",
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				})
				.done(function (data) {
					sap.ui.core.BusyIndicator.hide();

					var data = JSON.parse(data)
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp.notices), "ProcessedRecord");
					} else {
						this.getView().setModel(new JSONModel([]), "ProcessedRecord");
					}
				}.bind(this))
				.fail(function () {
					sap.ui.core.BusyIndicator.hide();
					sap.m.MessageBox.error("Failed to fetch summary data.");
				});
		},
		onSearchDetails: function () {
			var oData = this.getView().getModel("sfilter").getData(),
				oPropData = this.getView().getModel("ViewProperty").getProperty("/"),
				oPayload = {
					"hdr": {
						"pageNum": (oPropData.pageNo || 1) - 1,
						"pageSize": +oPropData.pgSize

					},
					"req": {
						"entityId": $.sap.entityID, // Replace with actual entityId if dynamic
						"gstins": [oData.gstins],
						"fromTaxPeriod": oData.fromTaxPeriod,
						"toTaxPeriod": oData.toTaxPeriod,
						"fromDate": oData.fromDate,
						"toDate": oData.toDate,
						"selectionCriteria": oData.selectionCriteria === 'TaxPeriod' ? "Tax Period" : "Date Wise"
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/fetchGstinNoticeSummary.do",
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				})
				.done(function (data) {

					sap.ui.core.BusyIndicator.hide();
					var data = JSON.parse(data)
					if (data.hdr.status === "S") {
						this.getView().setModel(new JSONModel(data.resp.notices), "Details");
						this._countPagination(data.hdr);
					} else {
						this.getView().setModel(new JSONModel([]), "Details");
						sap.m.MessageBox.information("No Data Found");
					}
				}.bind(this))
				.fail(function () {
					sap.ui.core.BusyIndicator.hide();
					sap.m.MessageBox.error("Failed to fetch summary data.");
				});
		},
		_countPagination: function (hdr) {
			var oModel = this.getView().getModel("ViewProperty"),
				vTotal = Math.ceil(hdr.totalCount / oModel.getProperty("/pgSize")),
				vPageNo = (vTotal ? hdr.pageNum + 1 : 0);

			oModel.setProperty("/pageNo", vPageNo);
			oModel.setProperty("/pgTotal", vTotal);
			oModel.setProperty("/ePageNo", vTotal > 1);
			oModel.setProperty("/bFirst", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onPressPagination: function (btn) {
			var oModel = this.getView().getModel("ViewProperty");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo", 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo", oModel.getProperty("/pgTotal"));
				break;
			}
			oModel.refresh(true);
			this.onSearchDetails();
		},

		onSubmitPagination: function (type) {
			var oModel = this.getView().getModel("ViewProperty");
			if (type === "S") {
				oModel.setProperty("/pageNo", 1);
				oModel.refresh(true);
			}
			this.onSearchDetails();
		},

		onPressStatus: function () {
			var odata = [{
				"api1Status": "Success",
				"api1TimeStamp": "2023-10-01T10:00:00Z",
				"api2Status": "In Progress",
				"api2TimeStamp": "2023-10-02T11:00:00Z",
				"api3Status": "Failed",
				"api3TimeStamp": "2023-10-03T12:00:00Z"
			}]
			this.getView().setModel(new JSONModel(odata), "GetCallStatus");

			if (!this._oDialogGetCallStatus) {
				this._oDialogGetCallStatus = sap.ui.xmlfragment("com.ey.digigst.fragments.GSTNotices.GetCallStatus", this);
				this.getView().addDependent(this._oDialogGetCallStatus);

			}

			this._oDialogGetCallStatus.open();

		},

		onCloseACSDialog: function () {
			this._oDialogGetCallStatus.close();
		},

		onDownloadDetailed: function (key) {

			if (key == "P") {
				var oModelForTab = this.byId("tabGSTNProcess").getModel("ProcessedRecord").getProperty("/"),
					oSelectedItem = this.getView().byId("tabGSTNProcess").getSelectedIndices();

				if (oSelectedItem.length == 0) {
					sap.m.MessageBox.warning("Please Select at least one Gstin");
					return;
				}

				var aGstin = oSelectedItem.map(function (e) {
					return oModelForTab[e].gstin;
				});

				var oData = this.getView().getModel("pfilter").getData();
				var oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": aGstin,
						"selectionCriteria": oData.selectionCriteria === 'TaxPeriod' ? "TaxPeriod wise" : "Date wise", //or "Date wise"
						"fromTaxPeriod": oData.fromTaxPeriod,
						"toTaxPeriod": oData.toTaxPeriod,
						"fromDate": oData.fromDate,
						"toDate": oData.toDate,
						"reportType": "Detailed Report",
					}
				};
			} else {
				var oData = this.getView().getModel("sfilter").getData();
				var oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": [oData.gstins],
						"selectionCriteria": oData.selectionCriteria === 'TaxPeriod' ? "TaxPeriod wise" : "Date wise", //or "Date wise"
						"fromTaxPeriod": oData.fromTaxPeriod,
						"toTaxPeriod": oData.toTaxPeriod,
						"fromDate": oData.fromDate,
						"toDate": oData.toDate,
						"reportType": "Detailed Report",
					}
				};
			}

			this.reportDownload(oPayload, "/aspsapapi/downloadNoticeDetailedReport.do");
		},

		FetchNotices: function () {
			var that = this;
			var oModelForTab = this.byId("tabGSTNProcess").getModel("ProcessedRecord").getProperty("/"),
				oSelectedItem = this.getView().byId("tabGSTNProcess").getSelectedIndices();

			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Please Select at least one Gstin");
				return;
			}

			var aGstin = oSelectedItem.map(function (e) {
				return oModelForTab[e].gstin;
			});
			var postData = {
				"req": {
					"gstins": aGstin
				}
			};
			MessageBox.confirm("Are you sure you want to Initiate GST Notices?", {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (oActionSuccess) {
					if (oActionSuccess === "OK") {
						that.onInitiateGstNotices(postData);
					}
				}
			});

		},

		onInitiateGstNotices: function (postData) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateGstNotices.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					var aMockMessages = [];
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length === 0) {
						MessageBox.information("No Data");
					} else {
						if (data.hdr.status === "S") {
							MessageBox.information(data.resp[0].msg, {
								title: "Initiate GST Notices Successfully done"
							});
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

							this.getView().setModel(new JSONModel(aMockMessages), "Msg");
							this.onDialogPress();
						}
					}

				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onDialogPress: function () {
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: "Get Status",
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
							if (this.vPSFlag === "P") {
								this._getProcessedData();
							} else {
								this._getSummaryData();
							}
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.open();
		},

		SetFrequency: function () {

			if (!this._oDialogSetFrequency) {
				this._oDialogSetFrequency = sap.ui.xmlfragment("com.ey.digigst.fragments.GSTNotices.SetFrequency", this);
				this.getView().addDependent(this._oDialogSetFrequency);

			}

			this._oDialogSetFrequency.open();

		},

		onCancelPress: function () {
			this._oDialogSetFrequency.close();
		},

		onPressClear: function (screen, flag) {

			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);
			var pModel = this.getView().getModel("pfilter");
			var sModel = this.getView().getModel("sfilter");

			switch (screen) {
			case "P":

				pModel.setProperty("/gstins", []);
				pModel.setProperty("/selectionCriteria", "Datewise");

				this._setFromDateProperty("iFromDate", vDate, new Date());
				this._setToDateProperty("iToDate", new Date(), new Date(), vDate);
				this._setFromDateProperty("idPFromtaxPeriod", vDate, new Date());
				this._setToDateProperty("idPTotaxPeriod", new Date(), new Date(), vDate);
				pModel.refresh(true);
				this.onSearchSummary();

				break;
			case "S":

				sModel.setProperty("/selectionCriteria", "Datewise");

				this._setFromDateProperty("iSFromDate", vDate, new Date());
				this._setToDateProperty("iSToDate", new Date(), new Date(), vDate);
				this._setFromDateProperty("idSFromtaxPeriod", vDate, new Date());
				this._setToDateProperty("idSTotaxPeriod", new Date(), new Date(), vDate);
				sModel.refresh(true);
				this.onSearchDetails();

				break;
			}
		},

		onActivateAuthToken: function (gstin, authToken) {
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.GSTNotices
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GSTNotices
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GSTNotices
		 */
		//	onExit: function() {
		//
		//	}

	});

});