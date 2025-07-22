sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.imInwardJSON", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.imInwardJSON
		 */
		onInit: function () {

		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.imInwardJSON
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.imInwardJSON
		 */
		onAfterRendering: function () {
			var oModel = this.getView().getModel("IsdGstin");
			if (oModel) {
				var gstin = oModel.getProperty("/0/gstin");
			}
			this.getView().setModel(new JSONModel({
				"gstin": gstin || null,
				"irn": null
			}), "Property");
			this._bindJsonIrn();
		},

		_bindJsonIrn: function () {
			this.getView().setModel(new JSONModel({
				id: null,
				irn: null,
				irnSts: null,
				ackNo: null,
				ackDate: null,
				createdOn: null,
				payload: null,
				cnlDate: null,
				iss: null,
				errCode: null,
				errMsg: null
			}), "JsonIRN");
		},

		onSearchIRN: function () {
			var obj = this.getView().getModel("Property").getProperty("/");
			if (!obj.irn || obj.irn.length < 64) {
				MessageBox.error("Incorrect IRN");
				this._bindJsonIrn();
				return;
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getInwardIrnSearchData.do",
					contentType: "application/json",
					data: JSON.stringify({
						"req": {
							"gstin": obj.gstin,
							"irn": obj.irn
						}
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "E") {
						MessageBox.error(data.resp.message);
					}
					this.getView().setModel(new JSONModel(data.resp), "JsonIRN");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDownloadJSON: function () {
			var oData = this.getView().getModel("JsonIRN").getProperty("/payload");
			// Create a Blob from the JSON data
			var blob = new Blob([JSON.stringify(JSON.parse(oData))], {
				"type": "application/json"
			});

			var url = URL.createObjectURL(blob),
				link = document.createElement('a'),
				d = new Date();

			link.href = url;
			link.download = "JSON_" + (d.getDate() < 10 ? '0' : '') + d.getDate() + (d.getMonth() < 9 ? '0' : '') + (d.getMonth() + 1) + d.getFullYear() +
				"T" + d.getHours() + d.getMinutes() + d.getSeconds() + ".json";
			document.body.appendChild(link);

			sap.ui.getCore().attachInit(function () {
				link.click(); // This triggers the download
				document.body.removeChild(link); // Removes the link from the DOM after download is triggered
			});
		},

		formatStatus: function (stats) {
			if (!stats) {
				return null;
			}
			return (["ACT", "Active"].includes(stats) ? "Active" : "Active");
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.imInwardJSON
		 */
		//	onExit: function() {
		//
		//	}

	});

});