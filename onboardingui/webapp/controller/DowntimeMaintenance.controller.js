sap.ui.define([
	"sap/ui/core/mvc/Controller",
	'sap/m/MessageBox',
], function (Controller, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.onboarding.controller.DowntimeMaintenance", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.onboarding.view.DowntimeMaintenance
		 */
		onInit: function () {
			var oFromDateTimePicker = this.getView().byId("fromDateTimePicker");
			oFromDateTimePicker.attachBrowserEvent("keydown", function (oEvent) {
				oEvent.preventDefault(); // Prevent typing
			});

			var oToDateTimePicker = this.getView().byId("toDateTimePicker");
			oToDateTimePicker.attachBrowserEvent("keydown", function (oEvent) {
				oEvent.preventDefault(); // Prevent typing
			});
		},
		
		onFromDateChange: function (oEvent) {
			var oView = this.getView();
			var oFromDateTimePicker = oView.byId("fromDateTimePicker");
			var oToDateTimePicker = oView.byId("toDateTimePicker");
			var oDowntimeSubmit = oView.byId("idDowntimeSubmit");

			var sFromDateTime = oFromDateTimePicker.getDateValue(); // Get the Date object
			var sToDateTime = oToDateTimePicker.getDateValue(); // Get the Date object

			// Validate if both From and To dates are set
			if (sToDateTime && sFromDateTime && sFromDateTime > sToDateTime) {
				oDowntimeSubmit.setEnabled(false);
				sap.m.MessageToast.show("Invalid Date/Time Range!");
				oFromDateTimePicker.setValueState("Error");
				oToDateTimePicker.setValueState("Error");
			} else {
				if (sToDateTime && sFromDateTime) {
					oDowntimeSubmit.setEnabled(true)
				}
				oFromDateTimePicker.setValueState("None");
				oToDateTimePicker.setValueState("None");
			}
		},

		onToDateChange: function (oEvent) {
			this.onFromDateChange(); // Reuse validation logic
		},
		onClearPress: function () {
			// Get the view
			var oView = this.getView();
			// Clear Input Field
			oView.byId("idMessage").setValue("");
			// Clear DatePicker
			oView.byId("fromDateTimePicker").setDateValue(null);
			// Clear TimePicker
			oView.byId("toDateTimePicker").setDateValue(null);
			oView.byId("idDowntimeSubmit").setEnabled(false);
			var Payload = {
				"req": {
					"startTime": null,
					"endTime": null,
					"downTimemsg": "",
					"flag": false
				}
			}

			this.onSubmitAPI(Payload);
		},
		onSubmit: function () {
			var that = this; // Preserve context for use in callback
			var oView = this.getView();
			var vMessage = oView.byId("idMessage").getValue();
			var vFromDateTime = oView.byId("fromDateTimePicker").getValue();
			var vToDateTime = oView.byId("toDateTimePicker").getValue();

			// Validation for mandatory fields
			if (!vFromDateTime) {
				sap.m.MessageBox.information("Start Date Time is Mandatory");
				return;
			}
			if (!vToDateTime) {
				sap.m.MessageBox.information("End Date Time is Mandatory");
				return;
			}
			if (!vMessage) {
				sap.m.MessageBox.information("Message is Mandatory");
				return;
			}

			// Constructing Payload
			var Payload = {
				"req": {
					"startTime": vFromDateTime,
					"endTime": vToDateTime,
					"downTimemsg": vMessage.trim(),
					"flag": true
				}
			};

			// Confirmation dialog
			sap.m.MessageBox.show("Do you want to submit the data?", {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				title: "Confirmation",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === sap.m.MessageBox.Action.YES) {
						that.onSubmitAPI(Payload);
					}
				}
			});
		},

		onSubmitAPI: function (Payload) {
			var that = this;
			this.getView().setBusy(false);
			$.ajax({
				method: "POST",
				url: "/SapOnboarding/updateDowntimeConfig.do",
				contentType: "application/json",
				data: JSON.stringify(Payload)
			}).done(function (data, status, jqXHR) {
				that.getView().setBusy(false);
				if (data.hdr.status === "S") {
					var oText = new sap.ui.core.HTML({
						content: "<div>" + data.resp.downTimeMsg + "</div>" // Render raw HTML
					});
					sap.m.MessageBox.success(oText, {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					sap.m.MessageBox.error(data.resp.downTimeMsg, {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				that.getView().setBusy(false);
			});
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.onboarding.view.DowntimeMaintenance
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.onboarding.view.DowntimeMaintenance
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.onboarding.view.DowntimeMaintenance
		 */
		//	onExit: function() {
		//
		//	}

	});

});