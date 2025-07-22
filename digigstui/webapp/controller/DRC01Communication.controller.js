sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.DRC01Communication", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.DRC01Communication
		 */
		onInit: function () {
			this._initializeFilter();
			this.setModel(new JSONModel(this._getSchuduleFreqData()), "ScheduleFrequency");
			this.setModel(new JSONModel(this._getViewEmailData()), "ViewEmail");
			this._setReadOnly("drcCommDtFr");
			this._setReadOnly("drcCommDtTo");
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.DRC01Communication
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.DRC01Communication
		 */
		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;

				this._initializeFilter();
				// this.getModel("FilterModel").setProperty("/selectedKey", "drcCommunication");
				this.onSearchDrcComm();
			}
		},

		handleIconTabBarSelect: function (oEvent) {
			var key = this.getModel("FilterModel").getProperty("/selectedKey");
			switch (key) {
			case "drcCommunication":
				this.onSearchDrcComm();
				break;
			case "scheduleFrequency":
				this._getScheduleFrequency();
				break;
			case "viewEmail":
				break;
			}
		},

		onChangeDateValue: function (oEvent) {
			var oModel = this.getModel("FilterModel"),
				toDate = oModel.getProperty("/toDate"),
				minDate = oEvent.getSource().getDateValue();

			oModel.setProperty("/minDate", minDate);
			if (minDate > new Date(toDate.substr(2), +toDate.substr(0, 2) - 1, 1)) {
				oModel.setProperty("/toDate", oModel.getProperty("/frDate"));
			}
			oModel.refresh(true);
		},

		_initializeFilter: function () {
			var date = new Date(),
				vPeriod = date.getFullYear() + (date.getMonth() + 1).toString().padStart(2, 0);

			this.setModel(new JSONModel({
				"selectedKey": "drcCommunication",
				"commType": "DRC01B",
				"frDate": vPeriod,
				"toDate": vPeriod,
				"gstin": [],
				"emailType": [],
				"maxDate": date,
				"minDate": new Date(vPeriod.substr(0, 4), +vPeriod.substr(4) - 1, 1)
			}), "FilterModel");
		},

		onClearDrcComm: function () {
			this._initializeFilter();
			this.onSearchDrcComm();
		},

		onSearchDrcComm: function () {
			var oFilter = this.getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"commType": oFilter.commType,
						"fromTaxPeriod": oFilter.frDate,
						"toTaxPeriod": oFilter.toDate,
						"gstins": this.removeAll(oFilter.gstin),
						"emailType": this.removeAll(oFilter.emailType)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDrc01CommDetails.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.setModel(new JSONModel(data.resp), "DrcCommunication");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onDownloadDrc01Comm: function (reqId) {
			this.excelDownload2(null, "/aspsapapi/downloadDRC01Report.do?requestId=" + reqId);
		},

		_getScheduleFrequency: function () {
			var payload = {
				"req": {
					"entityId": $.sap.entityID
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			this.setModel(new JSONModel(this._getSchuduleFreqData()), "ScheduleFrequency");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDrc01ReminderFrequency.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.setModel(new JSONModel(this._getScheduleFreqData(data.resp)), "ScheduleFrequency");
					this._bindComboBoxFilter();
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_bindComboBoxFilter: function (data) {
			var oData = this.getModel("ScheduleFrequency").getProperty("/"),
				aRow = this.byId("tabScheduleFreq").getRows();
			aRow.forEach(function (oRow, i) {
				if (oData[i].reminder1) {
					var oComboBox = oRow.getCells()[2],
						oBinding = oComboBox.getBinding("items");
					oBinding.filter(new sap.ui.model.Filter("text", sap.ui.model.FilterOperator.GT, oData[i].reminder1));
				}
				if (oData[i].reminder2) {
					var oComboBox1 = oRow.getCells()[3],
						oBinding1 = oComboBox1.getBinding("items");
					oBinding1.filter(new sap.ui.model.Filter("text", sap.ui.model.FilterOperator.GT, oData[i].reminder2));
				}
			});
		},

		onChangeScheduleFrequency: function (oEvent, type) {
			var oContext = oEvent.getSource().getBindingContext("ScheduleFrequency"),
				key = oEvent.getSource().getSelectedKey(),
				obj = oContext.getObject();

			switch (type) {
			case "Rem2":
				var oComboBox = oEvent.getSource().getParent().getCells()[2];
				obj.enableRem2 = !!obj.reminder1;
				obj.enableRem3 = !!obj.reminder1 && !!obj.reminder2;
				obj.reminder2 = !obj.reminder1 ? null : obj.reminder2;
				obj.reminder3 = !obj.reminder1 ? null : obj.reminder3;
				break;
			case "Rem3":
				oComboBox = oEvent.getSource().getParent().getCells()[3];
				obj.enableRem3 = !!obj.reminder2;
				obj.reminder3 = !obj.reminder2 ? null : obj.reminder3;
				break;
			}
			var oBinding = oComboBox.getBinding("items");
			oBinding.filter(new sap.ui.model.Filter("text", sap.ui.model.FilterOperator.GT, key));
			oContext.getModel().refresh(true);
		},

		_getScheduleFreqData: function (data) {
			var arr = ['drc01b', 'drc01c'],
				resp = arr.map(function (a) {
					var obj = {
						"communicationType": a.toUpperCase(),
						"reminder1": +data[a + "Freq"][0] || null,
						"reminder2": +data[a + "Freq"][1] || null,
						"reminder3": +data[a + "Freq"][2] || null
					};
					obj.enableRem2 = !!obj.reminder1;
					obj.enableRem3 = !!obj.reminder2;
					return obj;
				});
			return resp;
		},

		onSaveResetFrequency: function (action) {
			var oData = this.getModel("ScheduleFrequency").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"drc01bFreq": [0, 0, 0],
						"drc01cFreq": [0, 0, 0]
					}
				};
			if (action === "save") {
				oData.forEach(function (e) {
					var obj = payload.req[e.communicationType.toLowerCase() + "Freq"] = [];
					if (e.reminder1) {
						obj.push(+e.reminder1);
					}
					if (e.reminder2) {
						obj.push(+e.reminder2);
					}
					if (e.reminder3) {
						obj.push(+e.reminder3);
					}
				});
			}

			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/saveDrc01ReminderFrequency.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						var msg = (action === "save" ? "Scheduled Reminder frequency" : "Reset Reminder frequency");
						this._getScheduleFrequency();
						MessageBox.success(msg, {
							styleClass: "sapUiSizeCompact"
						});
					}
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onIntimationEmail: function (commType) {
			if (!this._dEmailContent) {
				this._dEmailContent = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drcComm.EmailContent", this);
				this.getView().addDependent(this._dEmailContent);
			}
			var obj = {
				"title": commType,
				"to": "Selected Recipient E-Mail ID",
				"cc": "Selected CC E-Mail ID",
				"subject": this._getEmailSubject('Initiate:' + commType),
				"content": this._getEmailContent('I', commType),
				"notes": this._getEmailNotes(),
				"height": "18rem"
			};
			this._dEmailContent.setModel(new JSONModel(obj), "EmailContent");
			this._dEmailContent.open();
		},

		onReminderEmail: function (commType) {
			if (!this._dEmailContent) {
				this._dEmailContent = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr3b.drcComm.EmailContent", this);
				this.getView().addDependent(this._dEmailContent);
			}
			var obj = {
				"title": commType,
				"to": "Selected Recipient E-Mail ID",
				"cc": "Selected CC E-Mail ID",
				"subject": this._getEmailSubject('Reminder:' + commType),
				"content": this._getEmailContent('R', commType),
				"notes": this._getEmailNotes(),
				"height": "24rem"
			};
			this._dEmailContent.setModel(new JSONModel(obj), "EmailContent");
			this._dEmailContent.open();
		},

		_getEmailSubject: function (commType) {
			switch (commType) {
			case "Initiate:DRC01B":
				return "DRC01B - Difference in Output Liability reported in GSTR-1 vs GSTR-3B for GSTIN - XXXX";
			case "Initiate:DRC01C":
				return "DRC01C - Difference in Input Tax Credit reported in GSTR-3B vs GSTR-2B for GSTIN - XXXX";
			case "Reminder:DRC01B":
				return "Reminder # - DRC01B - Difference in Output Liability reported in GSTR-1 vs GSTR-3B for GSTIN - XXXX";
			case "Reminder:DRC01C":
				return "Reminder # - DRC01C - Difference in Input Tax Credit reported in GSTR-3B vs GSTR-2B for GSTIN - XXXX";
			}
		},

		_getEmailContent: function (type, commType) {
			var content = "Dear Sir/Madam,\n\n" + (type === "R" ? 'Gentle reminder.\n\n' : '');

			if (commType === "DRC01B") {
				content += "Considering the Notification No.26/2022 - Central Tax dated 26th December 2022, issued by Ministry of Finance," +
					" please note that we have observed a difference between Output liability declared as part of GSTR-1 vis-a-vis liability declared as part of GSTR-3B for GSTIN XXXX for the return period XXXX.";
			} else {
				content +=
					"Considering the Notification No.38/2023 - Central Tax dated 4th August 2023, issued by Ministry of Finance," +
					" please note that we have observed a difference between Input Tax Credit reported in GSTR-3B vis-a-vis Input Tax Credit available in GSTR-2B for GSTIN for the return period XXXX."
			}
			content += "\nPlease find attached the details of aforesaid differences as part of intimation in Part - A of " + commType + ".";
			content +=
				'\n\nWould request you to please refer the same and provide the relevant reasons for such differences as response in Part-B of ' +
				commType + ' by navigating to DigiGST application.';
			content += '\n\nNavigation - Login to DigiGST > Home Page > Click on "Returns" from the Menu > Click on "GSTR-3B" > Click on "' +
				commType + '"';

			if (type === "R") {
				content += "\n\nThis is a reminder e-mail for providing reasons for difference in";
				switch (commType) {
				case "DRC01B":
					content += " output liability reported in GSTR-1 vs GSTR-3B as communicated in DRC01B Intimation."
					break;
				case "DRC01C":
					content += " Input Tax Credit reported in GSTR-3B vs GSTR-2B as communicated in DRC01C Intimation."
					break;
				}
				content += " Please ignore if reasons are already Filed.";
			}
			content += "\n\nRegards,\nEY DigiGST";
			return content;
		},

		_getEmailNotes: function () {
			return "***This is an auto - generated email. Please do not reply to this email.\n" +
				"IMPORTANT: This e-mail and any files transmitted with it are for the sole use of the intended recipient(s) and may contain confidential and privileged information." +
				" If you are not the intended recipient, please destroy all copies and the original message." +
				" Any unauthorized review, use, disclosure, dissemination, forwarding, printing or copying of this email or any action taken in reliance on this e-mail is strictly prohibited and may be unlawful."
		},

		onCloseEmail: function () {
			this._dEmailContent.close();
		},

		_setReadOnly: function (controlId) {
			this.byId(controlId).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		_getSchuduleFreqData: function () {
			return [{
				"communicationType": "DRC01B",
				"enableRem2": false,
				"enableRem3": false
			}, {
				"communicationType": "DRC01C",
				"enableRem2": false,
				"enableRem3": false
			}];
		},

		_getViewEmailData: function () {
			return [{
				"communicationType": "DRC01B"
			}, {
				"communicationType": "DRC01C"
			}];
		},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.DRC01Communication
		 */
		//	onExit: function() {
		//
		//	}
	});
});