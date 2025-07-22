sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/model/json/JSONModel"
], function (MobileLibrary, MessageBox, MessageToast, Filter, FilterOperator, JSONModel) {
	"use strict";

	return {
		_formatRetPeriod: function (vValue) {
			var vDate = vValue.substr(2, 4) + "-" + vValue.substr(0, 2) + "-01";
			return new Date(vDate);
		},

		/*=====================================================================*/
		/*========== Get GSTIN Details ========================================*/
		/*=====================================================================*/
		_getGstinSaveSubmit: function (controller) {
			var that = this;
			if (controller.selectKey === "SaveToGSTN") {
				var aEntity = controller.byId("slEntitySave").getSelectedKeys();
				var aGstins = controller.byId("slGstinSave").getSelectedKeys();
				var vFromDate = controller.byId("drsPeriodSave").getDateValue();
				var vToDate = controller.byId("drsPeriodSave").getSecondDateValue();
			} else {
				aEntity = controller.byId("slEntityFile").getSelectedKeys();
				aGstins = controller.byId("slGstinFile").getSelectedKeys();
				vFromDate = controller.byId("drsPeriodFile").getDateValue();
				vToDate = controller.byId("drsPeriodFile").getSecondDateValue();
			}
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			var oModel = controller.byId("tabFileGstn").getModel("SubmitFile");
			if (oModel) {
				oModel.setData(null);
			}
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getGstr1SaveSubmitGstins.do",
				data: JSON.stringify({
					"hdr": {
						"pageNo": "",
						"pageSize": ""
					},
					"req": {
						"entityId": aEntity,
						"gstin": aGstins,
						"retPeriodFrom": controller._formatPeriod(vFromDate).substr(2, 6),
						"retPeriodTo": controller._formatPeriod(vToDate).substr(2, 6)
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				oModel = new JSONModel(data.resp);
				if (controller.selectKey === "SaveToGSTN") {
					controller.byId("tabSaveGstn").setModel(oModel, "SaveToGSTN");
					if (data.resp.length > 0) {
						setInterval(function () {
							that._getGstnReviewStatus(controller);
						}, 120000);
					} else {
						clearInterval();
					}
				} else {
					controller.byId("tabFileGstn").setModel(oModel, "SubmitFile");
				}
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*=====================================================================*/
		/*=====================================================================*/
		/*=====================================================================*/
		_pressGstnLink: function (oEvent, controller) {
			var that = this;
			var aEntity = [];
			if (oEvent.getSource().getModel("SaveToGSTN")) {
				var vModel = "SaveToGSTN";
			} else if (oEvent.getSource().getModel("SubmitFile")) {
				vModel = "SubmitFile";
			}
			that.oObject = oEvent.getSource().getBindingContext(vModel).getObject();
			controller.byId("idIconTabBar").setSelectedKey("Summary");
			controller.byId("drsTaxPeriod").setDateValue(that._formatRetPeriod(that.oObject.returnPeriod));
			controller.byId("drsTaxPeriod").setSecondDateValue(that._formatRetPeriod(that.oObject.returnPeriod));
			aEntity.push(that.oObject.entityId);
			if (aEntity.length > 0) {
				controller.byId("slSummEntity").setSelectedKeys(aEntity);
				controller.getView().setBusy(true);
				$.ajax({
					method: "POST",
					url: "/aspsapapi/gstins.do",
					data: JSON.stringify({
						"req": {
							"entityId": aEntity
						}
					}),
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					controller.getView().setBusy(false);
					controller.byId("mcbSummaryGSTIN").setModel(new JSONModel(data), "SuppGstinModel");
					controller.byId("mcbSummaryGSTIN").setSelectedKeys(that.oObject.gstin);
					var Summary = sap.ui.require("com/ey/digigst/util/gstr1/Summary");
					Summary._reviewSummary(controller);
				}).fail(function (jqXHR, status, err) {
					controller.getView().setBusy(false);
				});
			}
		},

		/*=====================================================================*/
		/*=====================================================================*/
		/*=====================================================================*/
		_onAuthTokenStatus: function (oEvent, controller) {
			var oObject = oEvent.getSource().getEventingParent().getParent().getBindingContext("SaveToGSTN").getObject();
			if (oObject.authToken !== "Active") {
				var oDialog = new sap.m.Dialog({
					title: "Auth Token",
					type: "Message",
					content: [
						new sap.m.Label({
							text: "Enter OTP",
							labelFor: "inGstnOTP"
						}),
						new sap.m.Input("inGstnOTP", {
							width: "100%"
						})
					],
					beginButton: new sap.m.Button({
						text: "OK",
						press: function () {
							oDialog.close();
						}
					}),
					endButton: new sap.m.Button({
						text: "Cancel",
						press: function () {
							oDialog.close();
						}
					}),
					afterClose: function () {
						oDialog.destroy();
					}
				}).addStyleClass("sapUiSizeCompact");
				oDialog.open();
			}
		},

		/*=====================================================================*/
		/*======== Call to Submit data to GSTN ================================*/
		/*=====================================================================*/
		_saveToGstn: function (oEvent, controller) {
			var that = this;
			var vIndices = controller.byId("tabSaveGstn").getSelectedIndices();
			if (vIndices.length === 0) {
				MessageToast.show("Please select atleast one record");
				return;
			}
			MessageBox.confirm("Are you sure, you want to save selected records?", {
				initialFocus: sap.m.MessageBox.Action.CANCEL,
				styleClass: "sapUiSizeCompact",
				onClose: function (oAction) {
					if (oAction === MessageBox.Action.OK) {
						that._callSaveToGstnJob(controller);
					}
				}
			});
		},

		_searchInfo: function (controller) {
			var vIndices = controller.byId("tabSaveGstn").getSelectedIndices();
			var aData = controller.byId("tabSaveGstn").getModel("SaveToGSTN").getData();
			var searchInfo = {
				"hdr": {
					"pageNo": "",
					"pageSize": ""
				},
				"req": {
					"gstins": [],
					"retPeriods": []
				}
			};
			for (var i = 0; i < vIndices.length; i++) {
				searchInfo.req.gstins.push(aData[vIndices[i]].gstin);
				searchInfo.req.retPeriods.push(aData[vIndices[i]].returnPeriod);
			}
			return searchInfo;
		},

		_callSaveToGstnJob: function (controller) {
			var oSearchInfo = this._searchInfo(controller);
			controller.getView().setBusy(true);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1SaveToGstnJob.do",
				data: JSON.stringify(oSearchInfo),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				controller.getView().setBusy(false);
				if (data.hdr.status === "S") {
					MessageBox.success("Save initiated for selected(active) GSTINs. Please review after 15 minutes.", {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					MessageBox.error("Save failed for selected(active) GSTINs.");
				}
			}).fail(function (jqXHR, status, err) {
				controller.getView().setBusy(false);
			});
		},

		/*=====================================================================*/
		/*======== Background Call to update Save Status ======================*/
		/*=====================================================================*/
		_getGstnReviewStatus: function (controller) {
			var aData = [];
			this.model = controller.byId("tabSaveGstn").getModel("SaveToGSTN");
			var oData = this.model.getData();
			for (var i = 0; i < oData.length; i++) {
				aData.push(oData[i]);
			}
			for (i = 0; i < aData.length; i++) {
				this._callSaveGSTN(aData[i]);
			}
		},

		_callSaveGSTN: function (aData) {
			var that = this;
			$.ajax({
				method: "POST",
				url: "/aspsapapi/saveToGstnStatus.do",
				data: JSON.stringify({
					"hdr": {
						"pageNo": "",
						"pageSize": ""
					},
					"req": {
						"gstin": aData.gstin,
						"retPeriod": aData.returnPeriod
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				var oData = that.model.getData();
				for (var i = 0; i < oData.length; i++) {
					if (oData[i].gstin === data.resp.gstin && oData[i].returnPeriod === data.resp.retPeriod) {
						oData[i].statusCode = data.resp.statusCode;
						oData[i].saveStatus = data.resp.saveStatus;
					}
				}
				that.model.refresh(true);
			}).fail(function (jqXHR, status, err) {});
		}
	};
});