sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/core/mvc/Controller",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/core/util/Export",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/m/Button",
	"sap/m/Dialog"
], function (BaseController, Controller, formatter, JSONModel, MessageBox, MessageToast, Export, ExportTypeCSV, Button, Dialog) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.DRC01C", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.drc01c
		 */
		onInit: function () {
			var that = this;
			var vDate = new Date();
			this.byId("idFromtaxPeriodDRC").setMaxDate(vDate);
			this.byId("idFromtaxPeriodDRC").setDateValue(vDate);
			this.byId("idFromtaxPeriodDRC").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idFromtaxPeriodDRC").$().find("input").attr("readonly", true);
				}
			});
		},
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onPressClear();

				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.getReasonMaster(),
						this.getdrc01cData()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this), function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		onPressDiffLink: function (oEvt, data) {
			var oButton = oEvt.getSource();
			if (!this._diffAmount) {
				this._diffAmount = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.diffAmount", this);
				this.getView().addDependent(this._diffAmount);
			}
			this._diffAmount.setModel(new JSONModel(data), "DiffData");
			this._diffAmount.openBy(oButton);
		},

		onPressClear: function () {
			this.byId("idPGstinDRC").setSelectedKeys([]);
			this.byId("idDifferenceDRC").setSelectedKey(null);
			this.byId("idFromtaxPeriodDRC").setDateValue(new Date());
		},

		onSearch: function () {
			this.getdrc01cData();
		},

		getReasonMaster: function () {
			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "GET",
					url: "/aspsapapi/getAllDrc01cReasons.do",
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					data.resp.reasons.sort(function (a, b) {
						return a.reasonCode.slice(-2) - b.reasonCode.slice(-2);
					});
					// if (data.resp.reasons.length > 0) {
					// 	data.resp.reasons.unshift({
					// 		"reasonCode": 'ALL',
					// 		"reasonDesc": 'ALL',
					// 	});
					// }
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data), "Reason");
					resolve(data);
				}.bind(this)).fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel(null), "Reason");
					sap.ui.core.BusyIndicator.hide();
					reject(err);
				}.bind(this));
			}.bind(this));
		},

		getReasonDesc: function (value) {
			debugger;
			if (value === null) {
				return null
			}
			var oData = this.getView().getModel("Reason").getProperty("/resp/reasons");

			function findObjectById(obj) {
				return obj.reasonCode === value;
			}
			var result = oData.filter(findObjectById);
			return result[0].reasonDesc
		},

		getdrc01cData: function () {
			var that = this;

			var aGstin = this.byId("idPGstinDRC").getSelectedKeys();
			var payload = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": aGstin.includes("All") ? [] : aGstin,
					"taxPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
					"refId": this.byId("idDifferenceDRC").getSelectedKey()
				}
			};
			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/getDrc01cDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					// data.resp.drcDetails.forEach(function (item) {
					// 	item.edit = false;
					// 	item.reasonUsr.sort(function (a, b) {
					// 		return a.slice(-2) - b.slice(-2);
					// 	});
					// });
					that.getView().setModel(new JSONModel(data), "ProessData");
					resolve(data);
				}.bind(this)).fail(function (jqXHR, status, err) {
					that.getView().setModel(new JSONModel(null), "ProessData");
					sap.ui.core.BusyIndicator.hide();
					reject(err);
				}.bind(this));
			}.bind(this));
		},

		onPressReasonDiff: function (data, gstin, refid) {
			this.reasonGstin = gstin;
			// this.currentRefid = refid;

			if (!this._oDialogReasonDif) {
				this._oDialogReasonDif = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.drc01cReasonDiff",
					this);
				this.getView().addDependent(this._oDialogReasonDif);
			}
			if (data.length > 0) {
				var oData = {
					"resp": data
				}
			} else {
				var oData = {
					"resp": [{
						"id": null,
						"readonCode": null,
						"explanation": null
					}]
				}
			}

			var oProcessData = new sap.ui.model.json.JSONModel(oData.resp);
			this.getView().setModel(oProcessData, "ReasonDiff");

			this._oDialogReasonDif.open();
		},

		onSaveReasonDialog: function (flag) {
			if (flag === "S") {
				this.OnSaveReason("S")
			} else {
				this._oDialogReasonDif.close();
			}
		},

		OnSaveReason: function (flag) {
			debugger;
			var oModel = this.getView().getModel("ReasonDiff");
			var itemlist = oModel.getProperty("/"),
				comp = new Set();

			for (var i = 0; i < itemlist.length; i++) {
				if (itemlist[i].readonCode === null) {
					MessageBox.error("Please select unique reason code");
					return;
				}
			}

			var duplicates = itemlist.filter(function (item) {
				if (comp.has(item.readonCode)) {
					return true;
				}
				comp.add(item.readonCode);
				return false;
			});

			if (duplicates.length) {
				MessageBox.error("Please select unique reason code");
				return;
			}
			if (flag === "S" && itemlist.length === 0) {
				MessageBox.error("Please add atleast one record");
				return;
			}

			var payload = {
				"req": {
					"reasons": [{
						"gstin": this.reasonGstin,
						"taxPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
						"reasonUsr": itemlist
					}]
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/saveDrc01cUserReasons.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					this.getdrc01cData();
					if (flag === "S") {
						this._oDialogReasonDif.close();
						MessageBox.success(data.resp);
					} else {
						MessageBox.success("Records Deleted Successfully");
					}

				} else {
					MessageBox.error(data.resp);
				}
			}.bind(this)).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			}.bind(this));

		},

		onAddReasonRow: function () {
			var oData = this.getView().getModel("Reason").getProperty("/resp/reasons");
			var oModel = this.getView().getModel("ReasonDiff");
			var itemlist = oModel.getProperty("/");
			if (itemlist.length < oData.length) {
				var emptyObject = {
					"id": null,
					"readonCode": null,
					"explanation": null
				};
				itemlist.push(emptyObject);
				oModel.setProperty("/", itemlist);
			} else {
				MessageBox.information("Maximum " + oData.length + " records are Allowed");
			}
		},

		OnDeleteReasonRow: function (oEvent) {
			debugger;
			var tab = this.getView().byId("tabReasonDiff");
			var oTableData = tab.getModel("ReasonDiff").getData();
			var vflag = false;
			var aContexts = tab.getSelectedContexts();
			if (aContexts.length === 0) {
				MessageBox.error("Select atleast one record");
				return;
			}
			for (var i = aContexts.length - 1; i >= 0; i--) {
				var oThisObj = aContexts[i].getObject();
				if (oThisObj.id != null) {
					vflag = true;
				}
				var index = $.map(oTableData, function (obj, index) {
					if (obj === oThisObj) {
						return index;
					}
				})
				oTableData.splice(index, 1);
			}

			tab.getModel("ReasonDiff").setData(oTableData);
			tab.removeSelections(true);
			if (vflag) {
				this.OnSaveReason("D");
			} else {
				MessageBox.success("Records Deleted Successfully");
			}

		},

		onPressAddDiff: function (data, gstin, refid) {
			this.currentGstin = gstin;
			this.currentRefid = refid;

			if (!this._oDialogAddDif) {
				this._oDialogAddDif = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.drc01cAddDiff", this);
				this.getView().addDependent(this._oDialogAddDif);
			}
			if (data.length > 0) {
				var oData = {
					"resp": data
				}
			} else {
				var oData = {
					"resp": [{
						"drc03arn": "",
						"challanNo": "",
						"challanDate": "",
						"igst": 0,
						"cgst": 0,
						"sgst": 0,
						"cess": 0
					}]
				}
			}

			var oProcessData = new sap.ui.model.json.JSONModel(oData.resp);
			this.getView().setModel(oProcessData, "AddDiff");

			this._oDialogAddDif.open();
		},

		onSaveDialog: function (flag) {
			if (flag === "S") {
				this.onSaveChallan()
			} else {
				this._oDialogAddDif.close();
			}
		},

		onSaveChallan: function () {
			debugger;
			var oModel = this.getView().getModel("AddDiff");
			var itemlist = oModel.getProperty("/");
			if (itemlist.length === 0) {
				MessageBox.error("Add atleast one record");
				return;
			}

			var payload = {
				"req": {
					"gstin": this.currentGstin,
					"taxPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
					"refid": this.currentRefid,
					"differentialDetails": []
				}
			}
			for (var i = 0; i < itemlist.length; i++) {
				if (itemlist[i].drc03arn === "") {
					MessageBox.error("DCR03 ARN is mandatory ");
					return;
				}
				if (itemlist[i].challanNo === "") {
					MessageBox.error("Challan No. is mandatory ");
					return;
				}
				if (itemlist[i].challanDate === "") {
					MessageBox.error("Challan Date is mandatory ");
					return;
				}

				payload.req.differentialDetails.push({
					"drcArnNo": itemlist[i].drc03arn,
					"challanNo": itemlist[i].challanNo,
					"challanDateStr": itemlist[i].challanDate,
					"igst": itemlist[i].igst,
					"cgst": itemlist[i].cgst,
					"sgst": itemlist[i].sgst,
					"cess": itemlist[i].cess
				});
			}
			if (payload.req.differentialDetails.length === 2) {
				if (payload.req.differentialDetails[0].drcArnNo === payload.req.differentialDetails[1].drcArnNo) {
					MessageBox.error("DCR03 ARN value should not be same, Please provide different value");
					return;
				}
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/saveDrc01cDifferentialDtls.do",
				contentType: "application/json",
				data: JSON.stringify(payload)
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					MessageBox.success(data.resp);
					this._oDialogAddDif.close();
					this.getdrc01cData()
				} else {
					MessageBox.error(data.resp);
				}
			}.bind(this)).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
				if (jqXHR.responseJSON.hdr.status === "E") {
					MessageBox.error(jqXHR.responseJSON.resp);
				}
			}.bind(this));
		},

		onAddRow: function () {
			var oModel = this.getView().getModel("AddDiff");
			var itemlist = oModel.getProperty("/");
			// var vSNO = itemlist.length + 1;
			if (itemlist.length < 2) {
				var emptyObject = {
					"drc03arn": "",
					"challanNo": "",
					"challanDate": "",
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				};

				itemlist.push(emptyObject);
				oModel.setProperty("/", itemlist);
			} else {
				MessageBox.information("Maximum 2 records are Allowed");
			}

		},

		OnEditReason: function () {
			var tab = this.getView().byId("iddrc01cTable");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ProessData");
			var itemlist1 = oJSONModel.getProperty("/");
			if (sItems.length === 0) {
				sap.m.MessageBox.warning("Select atleast one record");
				return;
			}
			for (var i = 0; i < sItems.length; i++) {
				itemlist1.resp.drcDetails[sItems[i]].edit = true;
			}
			var oUserInfo = new JSONModel();
			oUserInfo.setData(itemlist1);
			this.getView().setModel(oUserInfo, "ProessData");
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onFetchDRC01: function () {
			var that = this;
			var tab = this.getView().byId("iddrc01cTable");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ProessData");
			var itemlist1 = oJSONModel.getProperty("/");
			if (sItems.length === 0) {
				sap.m.MessageBox.warning("Select atleast one record");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < sItems.length; i++) {
				aGstin.push(itemlist1.resp.drcDetails[sItems[i]].gstin);
			}

			var postData = {
				"req": {
					"retPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
					"gstinList": aGstin,
					"frmtyp": "DRC01C"
				}
			};
			var aMockMessages = [];
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/fetchDrcDetails.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
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
							that.getView().setModel(new JSONModel(aMockMessages), "Msg");
							that.onDialogPress("Fetch DRC01C Details");
						}
					} else {
						MessageBox.error(data.hdr.message);
					}

				}.bind(this)).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					if (jqXHR.responseJSON.hdr.status === "E") {
						MessageBox.error(jqXHR.responseJSON.hdr.message);
					} else {
						MessageBox.error("fetchDrcDetails : Error");
					}

				}.bind(this));
			});
		},

		onFileDRC01PartB: function () {
			var tab = this.getView().byId("iddrc01cTable");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ProessData");
			var itemlist1 = oJSONModel.getProperty("/");
			if (sItems.length === 0) {
				sap.m.MessageBox.warning("Select atleast one record");
				return;
			}
			var aGstin = [];
			for (var i = 0; i < sItems.length; i++) {
				aGstin.push(itemlist1.resp.drcDetails[sItems[i]].gstin);
			}

			var postData = {
				"req": {
					"taxPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
					"gstins": aGstin
				}
			};

			var aMockMessages = [];
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/drcPartCSaveBulk.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
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
							this.getView().setModel(new JSONModel(aMockMessages), "Msg");
							this.onDialogPress("Save PartB to GSTN");
						}
					} else {
						MessageBox.error(data.hdr.message);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					if (jqXHR.responseJSON.hdr.status === "E") {
						MessageBox.error(jqXHR.responseJSON.hdr.message);
					} else {
						MessageBox.error("drcPartBSaveBulk : Error");
					}
				}.bind(this));
		},

		OnDeleteRow: function (oEvent) {
			var tab = this.getView().byId("tabAddDiff");
			var oTableData = tab.getModel("AddDiff").getData();
			var aContexts = tab.getSelectedContexts();
			if (aContexts.length === 0) {
				MessageBox.error("Select atleast one record");
				return;
			}
			for (var i = aContexts.length - 1; i >= 0; i--) {
				var oThisObj = aContexts[i].getObject();
				var index = $.map(oTableData, function (obj, index) {
					if (obj === oThisObj) {
						return index;
					}
				})
				oTableData.splice(index, 1);
			}

			tab.getModel("AddDiff").setData(oTableData);
			tab.removeSelections(true);
		},

		onPressFilingStatus: function (gstin) {
			if (!this._oDialogFilingStatus) {
				this._oDialogFilingStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.FilingStatus", this);
				this.getView().addDependent(this._oDialogFilingStatus);
				this.byId("FStp").setMaxDate(new Date());
			}

			this.byId("FsGstin").setSelectedKey(gstin)
			this.byId("FStp").setValue(this.byId("idFromtaxPeriodDRC").getValue())

			this._oDialogFilingStatus.open();
			this.getFilingStatus();
		},

		onDialogCloseFilingStatus: function () {
			this._oDialogFilingStatus.close();
		},

		getFilingStatus: function (oPayload) {
			debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": this.byId("FStp").getValue()
				}
			}
			var that = this,
				oModelFS = that.byId("FSid").getModel("TableFS");

			if (oModelFS) {
				oModelFS.setData(null);
				oModelFS.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getDrc01CFilingStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.details.length; i++) {
					data.resp.details[i].sno = i + 1;
				}
				that.byId("FSid").setModel(new JSONModel(data.resp), "TableFS");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onPressSaveStatus: function (gstin) {
			if (!this._oDialogSaveStatus) {
				this._oDialogSaveStatus = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.SaveStatus", this);
				this.getView().addDependent(this._oDialogSaveStatus);
				this.byId("dtSaveStats").setMaxDate(new Date());
			}
			this.byId("slSaveStatsGstin").setSelectedKey(gstin)
			this.byId("dtSaveStats").setValue(this.byId("idFromtaxPeriodDRC").getValue())
			this._oDialogSaveStatus.open();

			this.getSaveStatus();
		},

		onDialogCloseSaveStatus: function () {
			this._oDialogSaveStatus.close();
		},
		getSaveStatus: function () {
			debugger; //eslint-disable-line
			var oPayload = {
				"req": {
					"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
					"taxPeriod": this.byId("dtSaveStats").getValue()
				}
			}
			var that = this,
				oModel = that.byId("dSaveStatus").getModel("SaveStatus");

			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getDrc01CSaveStatus.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.details.length; i++) {
					data.resp.details[i].sno = i + 1;
				}
				that.byId("dSaveStatus").setModel(new JSONModel(data.resp), "SaveStatus");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		onDownloadReport: function () {
			debugger //eslint-disable-line
			var oSelectedItem = this.getView().byId("iddrc01cTable").getSelectedIndices();
			var oModelForTab1 = this.byId("iddrc01cTable").getModel("ProessData").getData();
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Please Select atleast one GSTIN");
				return;
			}
			var oTaxPeriod = this.oView.byId("idFromtaxPeriodDRC").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var aGstin = [];
			for (var i = 0; i < oSelectedItem.length; i++) {
				aGstin.push(oModelForTab1.resp.drcDetails[oSelectedItem[i]].gstin);
			}
			var postData = {
				"req": {
					"entityId": [
						Number($.sap.entityID)
					],
					"taxPeriod": this.byId("idFromtaxPeriodDRC").getValue(),
					"dataSecAttrs": {
						"GSTIN": aGstin
					}
				}
			};
			var url = "/aspsapapi/downloadDrc01cReport.do";
			this.reportDownload(postData, url);
		},

		onPayDiffrential: function (urlPart, newTab) {
			sap.m.URLHelper.redirect('https://www.gst.gov.in/', true);
			// sap.m.URLHelper.redirect("excel/Raw_File_239DFs.xlsx", true);
		},

		onDialogPress: function (title) {
			var that = this;
			if (!this.pressDialog) {
				this.pressDialog = new Dialog({
					title: title,
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
							that.getdrc01cData()
						}.bind(this)
					})
				});
				this.getView().addDependent(this.pressDialog);
			}
			this.pressDialog.setTitle(title);
			this.pressDialog.open();
		},

		onActivateAuthToken: function (gstin, authToken) {
			// debugger; //eslint-disable-line
			if (authToken === "I") {
				this.confirmGenerateOTP(gstin);
			}
		},

		onSaveStatusDownload: function (createdOn, dataType) {
			// var section = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().section;
			// var createdOn = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().createdOn;
			var req = {
				"req": {
					"gstin": this.byId("slSaveStatsGstin").getSelectedKey(),
					"taxPeriod": this.byId("dtSaveStats").getValue(),
					"createdOn": createdOn,
					"dataType": dataType
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr3bDrc01cJsonDownloadDocument.do";
			this.excelDownload(req, oReqExcelPath);
		},

		onFilingStatusDownload: function (createdOn, dataType) {
			// var section = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().section;
			// var createdOn = oEvt.getSource().getParent().getBindingContext("SaveStatus").getObject().createdOn;
			var req = {
				"req": {
					"gstin": this.byId("FsGstin").getSelectedKey(),
					"taxPeriod": this.byId("FStp").getValue(),
					"createdOn": createdOn,
					"dataType": dataType
				}
			};

			var oReqExcelPath = "/aspsapapi/gstr3bDrc01cJsonDownloadDocument.do";
			this.excelDownload(req, oReqExcelPath, true);
		},

		onSignFile: function (key) {
			// var key = oEvt.getParameter("item").getKey();
			if (key === "DSC") {
				this.onSignFileDSC();
				this.DSCEVCflag = "DSC"
			} else {
				this.onSignFileDSC();
				this.DSCEVCflag = "EVC"
			}
		},
		onSignFileDSC: function () {
			var oSelectedItem = this.getView().byId("iddrc01cTable").getSelectedIndices();
			var oModelForTab1 = this.byId("iddrc01cTable").getModel("ProessData").getData();
			if (oSelectedItem.length == 0) {
				sap.m.MessageBox.warning("Please Select atleast one GSTIN");
				return;
			} else if (oSelectedItem.length > 1) {
				sap.m.MessageBox.warning("Please Select only one GSTIN");
				return;
			}
			this.taxPeriod = this.oView.byId("idFromtaxPeriodDRC").getValue();
			if (this.taxPeriod === "") {
				this.taxPeriod = null;
			}
			debugger;
			this.selectedGstin = oModelForTab1.resp.drcDetails[oSelectedItem[0]].gstin;
			this.authState = oModelForTab1.resp.drcDetails[oSelectedItem[0]].authStatus;
			this.refId = oModelForTab1.resp.drcDetails[oSelectedItem[0]].refid;

			var that = this;
			if (this.authState === "I") {
				MessageBox.show(
					"Auth token is inactive for selected GSTIN, please activate and retry.", {
						icon: MessageBox.Icon.WARNING,
						title: "Warning"
					});
				return;
			}

			if (!this._oDialogSaveStats56) {
				this._oDialogSaveStats56 = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drc01c.SignandFile", this);
				this.getView().addDependent(this._oDialogSaveStats56);
			}

			var Request = {
				"req": {
					"gstin": this.selectedGstin
				}
			};

			var BulkGstinSaveModel = new JSONModel();
			var GstnsList = "/aspsapapi/get3BSignAndFilePanDetails.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {

					that._oDialogSaveStats56.open();
					if (that.DSCEVCflag === "DSC") {
						data.resp.header = "Please select the authorised signatory for which DSC is attached for initiating filing of DRC01C"
					} else {
						data.resp.header = "Please select the authorised signatory for which EVC is attached for initiating filing of DRC01C"
					}
					BulkGstinSaveModel.setData(data.resp);
					that.getView().setModel(BulkGstinSaveModel, "SignandFile");

				}).fail(function (jqXHR, status, err) {});
			});
		},

		onSaveClose: function () {
			this._oDialogSaveStats56.close();
		},

		onSaveSign: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			if (sel === null) {
				MessageBox.error("Please select an Authorised signatory for initiating filing");
				return;
			}

			var that = this;
			MessageBox.show(
				"Do you want to proceed with filing DRC01C?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							if (that.DSCEVCflag === "DSC") {
								that.onSaveSign2();
							} else {
								that.onSaveSignEVCConformation();
							}
						}
					}
				});

		},

		onSaveSign2: function () {
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var gstin;
			if (this.byId("linkGSTID").getSelectedKey() === "") {
				gstin = this.gstin;
			} else {
				gstin = this.byId("linkGSTID").getSelectedKey();
			}

			var Request = {
				"req": {
					"gstin": gstin,
					"taxPeriod": this.byId("linkDate").getValue(),
					"pan": selItem
				}
			};

			var GstnsList = "/aspsapapi/GSTR3BSignAndFileStage1.do";
			this.excelDownload(Request, GstnsList);
			this._oDialogSaveStats56.close();
			var that = this;
			MessageBox.show(
				"GSTR3B filing is initiated, click on filing status to view the status", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					onClose: function (oAction) {
						that.onSearch();
					}
				});
		},

		onSaveSignEVCConformation: function () {
			debugger;
			var sel = this.byId("idTableSignFile").getSelectedItem();
			var selItem = sel.getCells()[1].getText();
			var date = this.taxPeriod;
			var taxPeriod = date.slice(0, 2) + "/" + date.slice(2, 6);
			var Request = {
				"req": {
					"gstin": this.selectedGstin,
					"taxPeriod": taxPeriod,
					"pan": selItem
				}
			};
			this.ReqPayload = Request;
			if (!this._oDialogSaveStatsConfirm) {
				this._oDialogSaveStatsConfirm = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr3b.drc01c.SignandFileConformation", this);
				this.getView().addDependent(this._oDialogSaveStatsConfirm);
			}
			this.getView().setModel(new JSONModel(Request.req), "confirmval");
			this._oDialogSaveStats56.close();
			this._oDialogSaveStatsConfirm.open();
		},
		onPopupCancel: function () {
			this._oDialogSaveStatsConfirm.close();
		},

		onPopupConfirm: function () {
			var that = this;
			var RequestPayload = {
				"req": {
					"gstin": this.ReqPayload.req.gstin,
					"taxPeriod": this.taxPeriod,
					"pan": this.ReqPayload.req.pan,
					"refId": (this.refId === "No Ref ID" ? null : this.refId),
					"returnType": "drc01C"
				}
			};

			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: "/aspsapapi/evcSignAndFileStage1.do",
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var sts = data.hdr;
					if (sts !== undefined) {
						if (sts.status === "S") {
							that._getSignandFileOTP(data)
							that.signId = data.resp.signId
							that._oDialogSaveStatsConfirm.close();
						} else {
							MessageBox.error(data.resp);
						}
					} else {
						MessageBox.error(JSON.parse(data).hdr.message);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		_getSignandFileOTP: function (data) {
			if (!this._oDialogSignandFileOTP) {
				this._oDialogSignandFileOTP = sap.ui.xmlfragment(this.getView().getId(),
					"com.ey.digigst.fragments.gstr3b.drc01c.SignandFileOTP", this);
				this.getView().addDependent(this._oDialogSignandFileOTP);
			}
			this.byId("otpValue").setValue();
			this._oDialogSignandFileOTP.open();
		},
		onPopupOTPCancel: function () {
			this._oDialogSignandFileOTP.close();
		},

		onPopupOTPSign: function () {
			debugger;
			var otp = this.byId("otpValue").getValue();
			if (otp === null) {
				MessageBox.error("Please add OTP");
				return;
			}

			var that = this;
			var RequestPayload = {
				"req": {
					"signId": this.signId,
					"otp": otp,
				}
			}

			var GstnsList = "/aspsapapi/evcSignAndFileStage2.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(RequestPayload)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data === true) {
						MessageBox.success("Return Filing has been initiated successfully");
					} else {
						MessageBox.error(data.resp);
					}
					that._oDialogSignandFileOTP.close();
					that.getdrc01cData()

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});

		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.drc01c
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.drc01c
		 */

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.drc01c
		 */
		//	onExit: function() {
		//
		//	}

	});

});