sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
], function (BaseController, Formatter, JSONModel, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.crossitc", {
		formatter: Formatter,

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.crossitc
		 */
		onInit: function () {
			var that = this,
				vDate = new Date();
			this.byId("idTaxPerioditc").setMaxDate(vDate);
			this.byId("idTaxPerioditc").setDateValue(vDate);
			this.byId("idTaxPerioditc").addEventDelegate({
				onAfterRendering: function () {
					that.byId("idTaxPerioditc").$().find("input").attr("readonly", true);
				}
			});
		},

		onPressGoGstr6DetrProcess: function () {
			this._getProcessData();
		},

		_getProcessData: function () {
			debugger; //eslint-disable-line
			var that = this;
			var oTaxPeriod = this.getView().byId("idTaxPerioditc").getValue();
			if (oTaxPeriod === "") {
				oTaxPeriod = null;
			}
			var aGstin = this.getView().byId("idGetitcGstin").getSelectedKeys();

			var oPayload =
				// {
				// 	"req": {
				// 		"entityId": [
				// 			32
				// 		],
				// 		"taxPeriod": "092018",
				// 		"dataSecAttrs": {
				// 			"GSTIN": ["33ABOPS9546G1Z3"]
				// 		}
				// 	}
				// };

				{
					"req": {
						"entityId": [$.sap.entityID],
						"taxPeriod": oTaxPeriod,
						"dataSecAttrs": {
							"GSTIN": aGstin.includes("All") ? [] : aGstin,
						}
					}
				};

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr6CrossItcScreen.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				for (var i = 0; i < data.resp.length; i++) {
					// data.resp.verticalData[i].sNo = i + 1;
					data.resp[i].edit = false;
				}
				that.getView().setModel(new JSONModel(data.resp), "ProcessedRecord");
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		OnEditUserData: function () {
			debugger;
			var tab = this.getView().byId("tabitcFragment");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ProcessedRecord");
			var itemlist1 = oJSONModel.getProperty("/");

			if (sItems.length == 0) {
				sap.m.MessageBox.information("Please select atleast one record", {
					styleClass: "sapUiSizeCompact"
				});
			}

			for (var i = 0; i < sItems.length; i++) {

				itemlist1[sItems[i]].edit = true;
			}

			var oProcessedRecord = new JSONModel();
			oProcessedRecord.setData(itemlist1);
			this.getView().setModel(oProcessedRecord, "ProcessedRecord");
			// this.getView().refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}
		},

		onPressSaveChanges: function () {
			var that = this;
			var tab = this.getView().byId("tabitcFragment");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("ProcessedRecord");
			var itemlist1 = oJSONModel.getProperty("/");
			var oPayload = {
				"req": []
			};

			if (sItems.length == 0) {
				sap.m.MessageBox.information("Please select atleast one record", {
					styleClass: "sapUiSizeCompact"
				});
			}

			for (var i = 0; i < sItems.length; i++) {
				oPayload.req.push({
					"docKey": itemlist1[sItems[i]].docKey,
					"taxPeriod": itemlist1[sItems[i]].taxPeriod,
					"isdGstin": itemlist1[sItems[i]].isdGstin,
					"digigstComputeStatus": "",
					"digigstComputeTimestamp": "",
					"digigstIgstigst": itemlist1[sItems[i]].digigstIgstigst,
					"digigstSgstigst": itemlist1[sItems[i]].digigstSgstigst,
					"digigstCgstigst": itemlist1[sItems[i]].digigstCgstigst,
					"digigstSgstSgst": itemlist1[sItems[i]].digigstSgstSgst,
					"digigstIgstSgst": itemlist1[sItems[i]].digigstIgstSgst,
					"digigstCgstCgst": itemlist1[sItems[i]].digigstCgstCgst,
					"digigstIgstCgst": itemlist1[sItems[i]].digigstIgstCgst,
					"digigstCesscess": itemlist1[sItems[i]].digigstCesscess,
					"userIgstigst": itemlist1[sItems[i]].userIgstigst,
					"userSgstigst": itemlist1[sItems[i]].userSgstigst,
					"userCgstigst": itemlist1[sItems[i]].userCgstigst,
					"userSgstSgst": itemlist1[sItems[i]].userSgstSgst,
					"userIgstSgst": itemlist1[sItems[i]].userIgstSgst,
					"userCgstCgst": itemlist1[sItems[i]].userCgstCgst,
					"userIgstCgst": itemlist1[sItems[i]].userIgstCgst,
					"userCesscess": itemlist1[sItems[i]].userCesscess
				})
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr6CrossItcSaveData.do",
				data: JSON.stringify(oPayload),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "S") {
					MessageBox.success("User Edited Changes Saved Successfully");
					that._getProcessData();
				} else {
					MessageBox.error(data.hdr.message);
				}
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		TaxableValueChange: function (oEvent) {
			debugger;
			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!oEvent.getSource().getValue().match(reg)) {
				MessageBox.error("Negative values and alphabets are not allowed");
				oEvent.getSource().setValue(null)
				return;
			}
			// // var regex = /^[0-9]*$/;
			// if (oEvent.getSource().getValue() === "" || !oEvent.getSource().getValue().match(/^[0-9]*$/)) {
			// 	oEvent.getSource().setValue(null) 
			// } else {
			// }
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.crossitc
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.crossitc
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this._getProcessData();
				this.glbEntityId = $.sap.entityID;
			}
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.crossitc
		 */
		//	onExit: function() {
		//
		//	}

	});

});