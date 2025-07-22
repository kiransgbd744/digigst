sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel"
], function (Controller, JSONModel) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.Feedback", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Feedback
		 */
		onInit: function () {
			var oData = this._getFeedbackMockupData();
			this.getView().setModel(new JSONModel(oData), "UserFeedback");
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.Feedback
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.Feedback
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.Feedback
		 */
		//	onExit: function() {
		//
		//	}

		userFeedbackFactory: function (sId, oContext) {
			var oUIControl;
			return oUIControl;
		},

		_getFeedbackMockupData: function () {
			return {
				"results": [{
					"quesCode": "Q1",
					"ques": "On a scale of 1 through 5, 1 being the lowest and 5 being the highest, please rate DigiGST on the following",
					"questId": 1,
					"keyType": "RA",
					"items": [{
						"quesCode": "Q1",
						"ques": "Overall Experience",
						"questId": 2,
						"keyType": "SRA",
						"answerDesc": "3"
					}, {
						"quesCode": "Q1",
						"ques": "Ease of Use",
						"questId": 3,
						"keyType": "SRA",
						"answerDesc": "5"
					}, {
						"quesCode": "Q1",
						"ques": "Satisfaction with support from EY",
						"questId": 4,
						"keyType": "SRA",
						"answerDesc": "2"
					}, {
						"quesCode": "Q1",
						"ques": "Product is updated to reflect Regulatory Changes",
						"questId": 4,
						"keyType": "SRA",
						"answerDesc": "1"
					}, {
						"quesCode": "Q1",
						"ques": "The new User Interface (UI)",
						"questId": 4,
						"keyType": "SRA",
						"answerDesc": "4"
					}]
				}, {
					"quesCode": "Q2",
					"ques": "What is your favourite feature of DigiGST?",
					"questId": 5,
					"keyType": "TA",
					"answerDesc": "70"
				}, {
					"quesCode": "Q3",
					"ques": "Please list out all the pain points/areas where EY can improve DigiGST or Are there any features you would like to see implemented by EY to improve your DigiGST user experience? you may attach a pdf of less 2mb.",
					"questId": 6,
					"keyType": "TA",
					"answerDesc": "70",
					"fileUpload": true
				}, {
					"quesCode": "Q4",
					"ques": "Please select the latest features added to DigiGST in the last year that you know about and use.",
					"questId": 7,
					"keyType": "M",
					"items": [{
						text: "ITC-04 return filing"
					}, {
						text: "Role based access for E-invoice / Return Module"
					}, {
						text: "TAX/NIL/NON/EXT bifurcation to respective tables of GSTR 1 basis the supply type against line items"
					}, {
						text: "Report for Exports transactions without shipping bill details"
					}, {
						text: "Late reported invoices"
					}, {
						text: "GSTR-2B attributes in GSTR 2A vs PR reconciliation reports"
					}, {
						text: "Imports reconciliation"
					}, {
						text: "Exclude vendors / records from reconciliation"
					}, {
						text: "Daily recon module with payment blocking"
					}, {
						text: "E-invoice QR code validator for vendor invoices"
					}],
					"answerDesc": "A*B"
				}, {
					"quesCode": "Q5",
					"ques": "Do you need extra training or help in using the above mentioned features? if Yes, please provide an email that we can connect you on.",
					"questId": 8,
					"keyType": "R",
					"items": [{
						"answerCode": "A",
						"answerDesc": "Yes"
					}, {
						"answerCode": "B",
						"answerDesc": "NO"
					}],
					"answerDesc": "abc@gmail.com"
				}, {
					"quesCode": "Q6",
					"ques": "Would you like to recognize anyone from EY for their support?",
					"questId": 10,
					"keyType": "R",
					"items": [{
						"answerCode": "A",
						"answerDesc": "Yes"
					}, {
						"answerCode": "B",
						"answerDesc": "NO"
					}],
					"answerDesc": "abc@gmail.com"
				}]
			};
		}
	});

});