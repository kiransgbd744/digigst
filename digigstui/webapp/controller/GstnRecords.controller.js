sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/Token"
], function (Controller, JSONModel, Token) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GstnRecords", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GstnRecords
		 */
		onInit: function () {
			this._setReadonly("taxPeriod");
			this._setReadonly("docDate");
			this._setTokenValidator("iDocNo");
			this._bindDefaultData();
			this._bindProperty();
			this.getOwnerComponent().getRouter().getRoute("GstnRecords").attachPatternMatched(this._onRouteMatched, this);
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.onSearch();
			}
		},

		_onRouteMatched: function (oEvent) {
			var oHashChanger = this.getRouter().getHashChanger();
			oHashChanger.setHash("GstnRecords");
		},

		_bindDefaultData: function () {
			var today = new Date();
			this.getView().setModel(new JSONModel({
				"gstin": null,
				"ReturnPeriod": (today.getMonth() < 9 ? '0' : '') + (today.getMonth() + 1) + today.getFullYear(),
				"tableType": "B2B",
				"supplyType": null,
				"docType": null,
				"docNums": [],
				"docDate": null,
				"RecipientGstin": null,
				"maxDate": today,
				"docMaxDate": today,
				"docMinDate": new Date(today.getFullYear(), today.getMonth())
			}), "FilterModel");
		},

		_bindProperty: function () {
			this.getView().setModel(new JSONModel({
				"pgTotal": 0,
				"pageNo": 0,
				"pgSize": 100,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false
			}), "Property");
		},

		onChangeTaxPeriod: function (oEvent) {
			var oModel = this.getView().getModel("FilterModel"),
				frPeriod = oEvent.getSource().getValue(),
				date = new Date(frPeriod.substr(2), +frPeriod.substr(0, 2));

			date = new Date(date.setDate(date.getDate() - 1));
			if (date > new Date()) {
				date = new Date();
			}
			oModel.setProperty("/docMinDate", new Date(frPeriod.substr(2), +frPeriod.substr(0, 2) - 1));
			oModel.setProperty("/docMaxDate", date);
			oModel.setProperty("/docDate", null);
			oModel.refresh(true);
		},

		onClear: function () {
			this._bindDefaultData();
			this.onSearch();
		},

		onSearch: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oPropData = this.getView().getModel("Property").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (!oPropData.pageNo ? 0 : +oPropData.pageNo - 1),
						"pageSize": +oPropData.pgSize
					},
					"req": {
						"entityId": [$.sap.entityID],
						"supplyType": oFilterData.supplyType || null,
						"tableType": oFilterData.tableType,
						"docNums": [],
						"ReturnPeriod": oFilterData.ReturnPeriod,
						"docType": oFilterData.docType || null,
						"docDate": oFilterData.docDate || null,
						"RecipientGstin": oFilterData.RecipientGstin || null,
						"dataSecAttrs": {
							"GSTIN": []
						}
					}
				};
			if (oFilterData.gstin) {
				payload.req.dataSecAttrs.GSTIN.push(oFilterData.gstin);
			}
			payload.req.docNums = oFilterData.docNums.map(function (e) {
				return e.key;
			});
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstinSavedDocSearch.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "GstinRecords");
					this._pagination(data.hdr);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_pagination: function (hdr) {
			var oModel = this.getView().getModel("Property"),
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

		onTokenUpdate: function (oEvent) {
			var oModel = this.getModel("FilterModel"),
				oData = oModel.getProperty("/docNums");
			switch (oEvent.getParameter("type")) {
			case "added":
				var aTokens = oEvent.getParameter("addedTokens");
				aTokens.map(function (oToken) {
					oData.push({
						key: oToken.getKey(),
						text: oToken.getText()
					});;
				});
				break;
			case "removed":
				aTokens = oEvent.getParameter("removedTokens");
				aTokens.forEach(function (oToken) {
					oData = oData.filter(function (oContext) {
						return oContext.key !== oToken.getKey();
					});
				});
				break;
			}
			oModel.setProperty("/docNums", oData);
			oModel.refresh(true);
		},

		_setReadonly: function (id) {
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
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
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.GstnRecords
		 */
		//	onExit: function() {
		//
		//	}

	});

});