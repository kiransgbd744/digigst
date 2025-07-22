sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/UIComponent",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/util/Storage",
	"com/ey/digigst/util/Formatter",
	"com/ey/digigst/libs/exceljs.min"
], function (Controller, UIComponent, JSONModel, MessageBox, MessageToast, Storage, Formatter) {
	"use strict";

	return Controller.extend("com.ey.digigst.util.BaseController", {
		formatter: Formatter,

		/**
		 * Convenience method for accessing the router.
		 * @public
		 * @returns {sap.ui.core.routing.Router} the router for this component
		 */
		getRouter: function () {
			return UIComponent.getRouterFor(this);
		},

		/**
		 * Convenience method for getting the view model by name.
		 * @public
		 * @param {string} [sName] the model name
		 * @returns {sap.ui.model.Model} the model instance
		 */
		getModel: function (sName) {
			return this.getView().getModel(sName);
		},

		/**
		 * Convenience method for setting the view model.
		 * @public
		 * @param {sap.ui.model.Model} oModel the model instance
		 * @param {string} sName the model name
		 * @returns {sap.ui.mvc.View} the view instance
		 */
		setModel: function (oModel, sName) {
			return this.getView().setModel(oModel, sName);
		},

		/**
		 * Convenience method getting resource bundle
		 * @public
		 * @returns {Object} resource bundle instance
		 */
		getResourceBundle: function () {
			return this.getOwnerComponent().getModel("i18n").getResourceBundle();
		},

		getDateTimeStamp: function () {
			var d = new Date();
			return ("" + d.getFullYear() + (d.getMonth() + 1).toString().padStart(2, 0) + d.getDate().toString().padStart(2, 0) + "T" +
				d.getHours().toString().padStart(2, 0) + d.getMinutes().toString().padStart(2, 0) + d.getSeconds().toString().padStart(2, 0));
		},

		validateValue: function (value) {
			return !value && value !== 0 ? null : value;
		},

		/**
		 * Convenience method formatting date
		 * @public
		 * @param {Date} vDate Date to format
		 * @returns {Date} Formatter date
		 */
		_formatDate: function (date) {
			var vDate = typeof (date) === "object" ? date : new Date(date),
				dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
					pattern: "yyyy-MM-dd"
				});
			return dateFormat.format(vDate);
		},

		/**
		 * Convenience method formatting period
		 * @public
		 * @param {vDate} vDate Date to format
		 * @returns {Date} Formatter date
		 */
		_formatPeriod: function (vDate) {
			var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
				pattern: "ddMMyyyy"
			});
			var vPeriod = dateFormat.format(vDate);
			return vPeriod.substr(2, 6);
		},

		/**
		 * Called to get Entity and Data Security attributes for user
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 */
		getDataSecurity: function () {
			// debugger; //eslint-disable-line
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnxDataSecurityAttributes.do", //getDataSecurityForUser.do",
					contentType: "application/json",
					async: false
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getOwnerComponent().setModel(new JSONModel(data.resp), "UserSecurity");
					var oStorage = new Storage(Storage.Type.session, "digiGst");
					oStorage.put("userSecurity", data.resp);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Convenience method for back navigation
		 * @public
		 */
		myNavBack: function () {
			var oHistory = History.getInstance();
			var oPrevHash = oHistory.getPreviousHash();
			if (oPrevHash !== undefined) {
				window.history.go(-1);
			} else {
				this.getRouter().navTo("masterSettings", {}, true);
			}
		},

		/**
		 * Convenience method for content density
		 * @public
		 * @returns {string} theme
		 */
		getContentDensityClass: function () {
			if (!this._sContentDensityClass) {
				if (!sap.ui.Device.support.touch) {
					this._sContentDensityClass = "sapUiSizeCompact";
				} else {
					this._sContentDensityClass = "sapUiSizeCozy";
				}
			}
			return this._sContentDensityClass;
		},

		/**
		 * Convenience method for getting entity list 
		 * @public
		 */
		getEntity: function () {
			// debugger; //eslint-disable-line
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnxDataSecEntities.do",
					contentType: "application/json",
					async: false
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = this.getOwnerComponent().getModel("EntityModel");
					oModel.setData(data.resp);
					oModel.refresh(true);
					var oStorage = new Storage(Storage.Type.session, "digiGst");
					oStorage.put("entity", data.resp);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Called to get Data Security attribute for entities
		 * @public
		 * @param {int} vEntity Entity id
		 * @param {string} id Gstin id
		 */
		_getDataSecurity: function (vEntity, id) {
			// debugger; //eslint-disable-line
			if (!id) {
				var oView = this.getView();
			} else {
				oView = this.byId(id);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAnxDataSecApplAttr.do",
					contentType: "application/json",
					async: false,
					data: JSON.stringify({
						"req": {
							"entityId": vEntity
						}
					})
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					var oModel = oView.getModel("DataSecurity");
					if (!oModel) {
						oView.setModel(new JSONModel(data.resp), "DataSecurity");
					} else {
						oModel.setData(data.resp);
						oModel.refresh(true);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					oView.setBusy(false);
				});
		},

		/**
		 * Convenience method to select/deselect items in multicombobox
		 * Developed by: Bharat Gupta on 06.02.2020
		 * @public
		 * @param {Object} evt Eventing object
		 */
		selectAll: function (evt) {
			var vKey = evt.getParameter("changedItem").getKey(),
				vStats = evt.getParameter("selected");

			if (vKey.toLocaleLowerCase() === "all") {
				evt.getSource().setSelectedItems(vStats ? evt.getSource().getItems() : null);

			} else {
				var aSelectedKey = evt.getSource().getSelectedKeys(),
					aKeys = aSelectedKey.map(function (value) {
						return value.toLowerCase();
					}),
					vIdx = aKeys.indexOf("all");
				if (vIdx > -1) {
					aSelectedKey.splice(vIdx, 1);
				}
				if (aSelectedKey.length === evt.getSource().getItems().length - 1) {
					evt.getSource().setSelectedItems(evt.getSource().getItems());
				} else {
					evt.getSource().setSelectedKeys(aSelectedKey);
				}
			}
		},

		/**
		 * Convenience method to remove all from array
		 * Developed by: Bharat Gupta on 01.04.2020
		 * @public
		 * @param {Array} array Array
		 * @return {Array} Returning array without all
		 */
		removeAll: function (array) {
			if (array.includes("all") || array.includes("All") || array.includes("ALL")) {
				var idx = array.findIndex(function (e) {
					return (e.toLowerCase() === "all");
				});
				array.splice(idx, 1);
			}
			return array;
		},

		/**
		 * Convenience method to set Date and readonly property
		 * @public
		 * @param {string} id		Date Picker Id
		 * @param {Object} date		Date
		 * @param {Object} maxDt	Max Date
		 * @param {Object} minDt	Min Date
		 */
		_setDateProperty: function (id, date, maxDt, minDt) {
			if (minDt) {
				this.byId(id).setMinDate(minDt);
			}
			if (maxDt) {
				this.byId(id).setMaxDate(maxDt);
			}
			if (date) {
				this.byId(id).setDateValue(date);
			}
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		/**
		 * Convenience method to set readonly property
		 * @public
		 * @param {string} id DatePicker id
		 */
		setReadonly: function (id) {
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}.bind(this)
			});
		},

		_setFromDateProperty: function (id, date, maxDt) {
			this.byId(id).setMaxDate(maxDt);
			this.byId(id).setDateValue(date);
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		_setToDateProperty: function (id, date, maxDt, minDt) {
			this.byId(id).setMinDate(minDt);
			this.byId(id).setMaxDate(maxDt);
			this.byId(id).setDateValue(date);
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		},

		/**
		 * Method called to open dialog to display Respective Gstin & Message
		 * @public
		 * @param {Object} data Message Object
		 */
		_displayMsgList: function (data, view) {
			if (!this._dMsgList) {
				this._dMsgList = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.MsgList", this);
				this.getView().addDependent(this._dMsgList);
			}
			this._dMsgList.open();
			this._dMsgList.setModel(new JSONModel(data), "MessageList");
			this._dMsgList.setModel(new JSONModel({
				"view": view || null
			}), "MsgView");
		},

		/**
		 * Method called to close dialog
		 */
		onCloseMsgList: function () {
			this._dMsgList.close();
		},

		/**
		 * Convenience method to display Information Message
		 * @public
		 * @param {string} vMsg Message
		 */
		_infoMessage: function (vMsg) {
			MessageBox.information(vMsg, {
				styleClass: "sapUiSizeCompact"
			});
		},

		/**
		 * Convenience method to display Server Message
		 * @public
		 * @param {string} status Server Status
		 */
		_serverMessage: function (status) {
			var oBundle = this.getResourceBundle();
			if (status === 500) {
				this._infoMessage(oBundle.getText("status_500"));

			} else if (status === 503) {
				this._infoMessage(oBundle.getText("status_503"));

			} else if (status === 504) {
				this._infoMessage(oBundle.getText("status_504"));
			}
		},

		/**
		 * @public
		 * @param {Object} reqData payload data to download excel file
		 * @param {string} url Url for ajax call
		 */
		excelDownload: function (reqData, url, flag) {
			var that = this;
			// sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
					method: "POST",
					url: url,
					dataType: "binary",
					processData: false,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
					if (!flag && (!e || e.size < 50 || e.size < 200)) {
						sap.m.MessageBox.information("No data available to download", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
					var o = new Blob([e], {
						type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var r = "";
					var i = a.getResponseHeader("Content-Disposition");
					if (i && i.indexOf("inline") === -1) {
						var s = /filename[^;=\n]*=(.*)/;
						var l = s.exec(i);
						if (l !== null && l[1]) {
							r = l[1].replace(/['"]/g, "");
						}
					}
					if (!r) {
						r = "MyFileName.xlsx";
					}
					var d = document.createElement("a");
					d.href = window.URL.createObjectURL(o);
					d.download = r;
					d.setAttribute("visibility", "hidden");
					document.body.appendChild(d); //eslint-disable-line
					d.click();
				})
				.fail(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		excelDownload1: function (reqData, url) {
			// 			debugger; //eslint-disable-line
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
					method: "POST",
					url: url,
					dataType: "binary",
					processData: false,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
					/*if (e.size === 0) {
						sap.m.MessageBox.information("No data available to download", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}*/
					var o = new Blob([e], {
						type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var r = "";
					var i = a.getResponseHeader("Content-Disposition");
					if (i && i.indexOf("inline") === -1) {
						var s = /filename[^;=\n]*=(.*)/;
						var l = s.exec(i);
						if (l !== null && l[1]) {
							r = l[1].replace(/\s/g, ''); //r = l[1].replace(/['"]/g, "");
						}
					}
					if (!r) {
						r = "MyFileName.xlsx";
					}
					var d = document.createElement("a");
					d.href = window.URL.createObjectURL(o);
					d.download = r;
					d.setAttribute("visibility", "hidden");
					document.body.appendChild(d); //eslint-disable-line
					d.click();
				})
				.fail(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
				});
		},

		/**
		 * Method called to generate async Request id to download report
		 * Modified by Bharat Gupta - 09.06.2020
		 * @public
		 * @param {Object} reqData payload data to download excel file
		 * @param {string} url Url for ajax call
		 */
		reportDownload: function (reqData, url) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						sap.m.MessageBox.success("Initiated Request ID : " + oData.resp.id +
							"\n For Report Type : " + oData.resp.reportType +
							"\n Navigate to \"Reports >> Request Reports\"", {
								styleClass: "sapUiSizeCompact"
							});
					} else {
						sap.m.MessageBox.error(oData.hdr.message || oData.resp, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		reportDownloadReversal: function (reqData, url) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						sap.m.MessageBox.success("Initiated Request ID : " + oData.resp.id +
							"\n For Report Type : " + oData.resp.reportType +
							"\n For Data Type : " + oData.resp.dataType +
							"\n Navigate to \"Reports >> Request Reports\"", {
								styleClass: "sapUiSizeCompact"
							});
					} else {
						sap.m.MessageBox.error(oData.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		reportDownload1: function (reqData, url) {
			debugger;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						sap.m.MessageBox.success("Initiated Request ID : " + oData.resp.id +
							"\n For Report Type : " + oData.resp.reportType +
							"\n Navigate to \"Reports >> Request Reports\"", {
								styleClass: "sapUiSizeCompact"
							});
					} else {
						sap.m.MessageBox.error(oData.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		reportDownload2: function (reqData, url) {
			debugger;
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: url,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (data, status, jqXHR) {
					var oData = typeof (data) !== "object" ? JSON.parse(data) : data;
					sap.ui.core.BusyIndicator.hide();
					if (oData.hdr.status === "S") {
						sap.m.MessageBox.success("Initiated Request ID : " + oData.resp["Async report id :"] +
							"\n Navigate to \"Reports >> Request Reports\"");
					} else {
						sap.m.MessageBox.error(oData.hdr.message, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		alphaNumeric: function (oEvent) {
			// debugger; //eslint-disable-line
			var value = oEvent.getSource().getValue();
			if (/[^a-zA-Z0-9]/.test(value)) {
				oEvent.getSource().setValue(value.substr(0, value.length - 1));
			}
		},

		alphaNumericCaps: function (oEvent) {
			this.alphaNumeric(oEvent);
			var value = oEvent.getSource().getValue();
			if (value) {
				oEvent.getSource().setValue(value.toUpperCase());
			}
		},

		/**
		 * Convenience method to validating Alpha-Numeric with special character (like: -,/)
		 * @public
		 * @param {Object} oEvent Eventing object
		 */
		alphaNumberSpecial: function (oEvent) {
			// debugger; //eslint-disable-line
			var value = oEvent.getSource().getValue();
			if (/[^a-zA-Z0-9\-\/]/.test(value)) {
				oEvent.getSource().setValue(value.substr(0, value.length - 1));
			}
		},

		/**
		 * Convenience method to validate positive Interger number
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		positiveInteger: function (oEvent) {
			// debugger; //eslint-disable-line
			var value = oEvent.getSource().getValue();
			value = value.replace(/[^\d]/g, "");
			oEvent.getSource().setValue(value);
		},

		/**
		 * Convenience method to validate number upto two decimal value
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		positiveDecimal: function (oEvent) {
			// debugger; //eslint-disable-line
			var value = oEvent.getSource().getValue(),
				index = value.indexOf(".");
			value = value.replace(/[^\d\.]/g, "");

			if (!/^[\d]{0,15}[\.]?[\d]{0,2}$/.test(value)) {
				value = value.substr(0, value.length - 1);

			} else if (!/^[\d]{0,15}$/.test(value) && index === -1) {
				value = value.substr(0, 15);
			}
			oEvent.getSource().setValue(value);
		},

		/**
		 * Convenience method to validate amount
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
		anyDecimalValue: function (oEvent) {
			var value = oEvent.getSource().getValue(),
				index = value.indexOf("."),
				flag = (value[0] === '-');
			value = value.replace(/[^\d\.]/g, "");

			if (!/^[\-]?[\d]{0,15}[\.]?[\d]{0,2}$/.test(value) && index !== -1) {
				value = value.substr(0, value.length - 1);

			} else if (!/^[\-]?[\d]{0,15}$/.test(value) && index === -1) {
				value = value.substr(0, value.length - 1);
			}
			value = (flag ? '-' : '') + value;
			oEvent.getSource().setValue(value);
		},

		/**
		 * Convenience method to Boolean field in character format (like: Y / N)
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 * @param {Object} oEvent Eventing object
		 */
		booleanChar: function (oEvent) {
			debugger; //eslint-disable-line
			var value = oEvent.getSource().getValue();
			if (!/(Y|N|y|n)/.test(value)) {
				oEvent.getSource().setValue(null);
				return;
			}
			oEvent.getSource().setValue(value.toUpperCase());
		},

		/**
		 * Called to get GSTN Save Status data for Anx1, Anx1a, Ret1 & Ret1a
		 * Develped by: Bharat Gupta on 20.01.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 * @param {Object} payload Payload object
		 */
		getSaveStauts: function (payload) {
			var oModel = this.getView().getModel("GstinSaveModel");
			if (oModel) {
				oModel.setData(null);
				oModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGstnSaveStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					for (var i = 0; i < data.resp.length; i++) {
						data.resp[i].sno = i + 1;
					}
					this.getView().setModel(new JSONModel(data.resp), "GstinSaveModel");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Called to get Difference data for Anx1, Anx1a, Ret1 & Ret1a
		 * Developed by: Bharat Gupta on 21.01.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 * @param {Object} payload Payload object
		 */
		getDifferenceData: function (payload) {
			var oDiffModel = this.getView().getModel("Difference");
			if (oDiffModel) {
				oDiffModel.setData(null);
				oDiffModel.refresh(true);
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDiffStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp), "Difference");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		/**
		 * Method called for Confirmation to Generate OTP
		 * Develped by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 * @param {Object} gstin Gstin
		 */
		confirmGenerateOTP: function (gstin) {
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.otp.ConfirmationOTP", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.getView().setModel(new JSONModel({
				"gstin": gstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		/**
		 * Method called to close Confirmation to Generate OTP
		 * Develped by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 */
		onCloseGenerateOTP: function () {
			this._dAuthToken.close();
		},

		/**
		 * Method called to Close Verify OTP Popup
		 * Develped by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 */
		onCloseVerifyOTP: function () {
			this._dGenerateOTP.close();
		},

		/**
		 * Method called to Generate OTP and open Verify OTP Popup
		 * Develped by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @public
		 */
		onGenerateOTP: function () {
			// debugger; //eslint-disable-line
			var aData = this.getView().getModel("AuthTokenGstin").getData(),
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			this.byId("dAuthTokenConfirmation").setBusy(true);
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.otp.GenerateOTP", this);
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
					this.byId("dAuthTokenConfirmation").setBusy(false);
					this._dAuthToken.close();
					if (data.resp.status === "S") {
						var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
							oData = oModel.getData();
						oData.verify = false;
						oData.otp = null;
						oData.resendOtp = false;
						oModel.refresh(true);
						this._dGenerateOTP.open();
					} else {
						MessageBox.error(this.getResourceBundle().getText("otpGeneFailed"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
				}.bind(this));
		},

		/**
		 * Method called to resend OTP
		 * Developed by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @private
		 */
		onPressResendOTP: function () {
			// debugger; //eslint-disable-line
			var oOtpModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				oOtpData = oOtpModel.getData(),
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
					this.byId("dVerifyAuthToken").setBusy(false);
					var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
						oData = oModel.getData();
					oData.verify = false;
					oData.otp = null;
					if (data.resp.status === "S") {
						oData.resendOtp = true;
						this._dGenerateOTP.open();
					} else {
						oData.resendOtp = false;
						MessageBox.error(this.getResourceBundle().getText("otpGeneFailed"), {
							styleClass: "sapUiSizeCompact"
						});
					}
					oModel.refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
				}.bind(this));
		},

		/**
		 * Method called to Verigy OTP
		 * Developed by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @private
		 */
		onPressVerifyOTP: function () {
			// debugger; //eslint-disable-line
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getData(),
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
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
					if (data.resp.status === "S") {
						MessageBox.success(this.getResourceBundle().getText("otpMatched"), {
							styleClass: "sapUiSizeCompact",
							onClose: function () {
								this.refreshData();
							}.bind(this)
						});
					} else {
						MessageBox.error(this.getResourceBundle().getText("otpNotMatched"), {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
				}.bind(this));
		},

		/**
		 * Method called to validate OTP
		 * Modified by: Bharat Gupta on 21.04.2020
		 * @memberOf com.ey.digigst.util.BaseController
		 * @private
		 * @param {Object} oEvent Eventing object
		 */
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

		//======================== Added by chaithra for csv file download =========================//
		csvDownload: function (reqData, url) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
					method: "POST",
					url: url,
					dataType: "binary",
					processData: false,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
					if (e.size === 0) {
						sap.m.MessageBox.information("No data available to download", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
					var o = new Blob([e], {
						type: e.type
					});
					var r = "";
					var i = a.getResponseHeader("Content-Disposition");
					if (i && i.indexOf("inline") === -1) {
						var s = /filename[^;=\n]*=(.*)/;
						var l = s.exec(i);
						if (l !== null && l[1]) {
							r = l[1].replace(/['"]/g, "");
						}
					}
					if (!r) {
						r = "MyFileName.xlsx";
					}
					var d = document.createElement("a");
					d.href = window.URL.createObjectURL(o);
					d.download = r;
					d.setAttribute("visibility", "hidden");
					document.body.appendChild(d); //eslint-disable-line
					d.click();
				}.bind(this))
				.fail(function (e, t, a) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},
		//================ Added by chaithra on 2/2/2021 ======================//
		excelDownload2: function (reqData, url) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
					method: "GET",
					url: url,
					dataType: "binary",
					processData: false,
					contentType: "application/json"
						// data: JSON.stringify(reqData)
				})
				.done(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
					if (e.size < 50 || e.size < 100) {
						sap.m.MessageBox.information("No data available to download", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
					var o = new Blob([e], {
						type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var r = "";
					var i = a.getResponseHeader("Content-Disposition");
					if (i && i.indexOf("inline") === -1) {
						var s = /filename[^;=\n]*=(.*)/;
						var l = s.exec(i);
						if (l !== null && l[1]) {
							r = l[1].replace(/['"]/g, "");
						}
					}
					if (!r) {
						r = "MyFileName.xlsx";
					}
					var d = document.createElement("a");
					d.href = window.URL.createObjectURL(o);
					d.download = r;
					d.setAttribute("visibility", "hidden");
					document.body.appendChild(d); //eslint-disable-line
					d.click();
				}.bind(this))
				.fail(function (e, t, a) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		pdfDownload: function (reqData, url, flag) {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			$.ajaxTransport("+binary", function (e, t, n) { //eslint-disable-line
				if (window.FormData && (e.dataType && e.dataType === "binary" || e.data && (window.ArrayBuffer && e.data instanceof window.ArrayBuffer ||
						window.Blob && e.data instanceof Blob))) {
					return {
						send: function (t, n) { //eslint-disable-line
							var a = new XMLHttpRequest, //eslint-disable-line
								o = e.url,
								r = e.type,
								i = e.async || true,
								s = e.responseType || "blob",
								l = e.data || null,
								d = e.username || null,
								u = e.password || null;
							a.addEventListener("load", function () {
								var t = {}; //eslint-disable-line
								t[e.dataType] = a.response;
								n(a.status, a.statusText, t, a.getAllResponseHeaders());
							});
							a.open(r, o, i, d, u);
							for (var c in t) { //eslint-disable-line
								a.setRequestHeader(c, t[c]);
							}
							a.responseType = s;
							a.send(l);
						},
						abort: function () {
							n.abort();
						}
					};
				}
			});
			$.ajax({
					method: "POST",
					url: url,
					dataType: "binary",
					processData: false,
					contentType: "application/json",
					data: JSON.stringify(reqData)
				})
				.done(function (e, t, a) {
					sap.ui.core.BusyIndicator.hide();
					if (!flag && (!e || e.size < 50 || e.size < 200)) {
						sap.m.MessageBox.information("No Data available to generate PDF", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
					var o = new Blob([e], {
						type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var r = "";
					var i = a.getResponseHeader("Content-Disposition");
					if (i && i.indexOf("inline") === -1) {
						var s = /filename[^;=\n]*=(.*)/;
						var l = s.exec(i);
						if (l !== null && l[1]) {
							r = l[1].replace(/['"]/g, "");
						}
					}
					if (!r) {
						r = "MyFileName.xlsx";
					}
					var d = document.createElement("a");
					d.href = window.URL.createObjectURL(o);
					d.download = r;
					d.setAttribute("visibility", "hidden");
					document.body.appendChild(d); //eslint-disable-line
					d.click();
				})
				.fail(function (e, t, a) {
					debugger;
					sap.ui.core.BusyIndicator.hide();
				});
		},

		downloadZipAll: function (zipName, files) {
			var oZipFile = new JSZip(),
				oFileSaver = saveAs,
				promises = files.map(function (obj) {
					return this.loadFile(obj.filePath, obj.fileName);
				}.bind(this));

			Promise.all(promises)
				.then(function (values) {
					values.forEach(function (e) {
						oZipFile.file(e.fileName, e.fileData);
					});
					return oZipFile.generateAsync({
						type: "blob"
					});
				})
				.then(function (blob) {
					saveAs(blob, zipName);
				})
				.catch(function (error) {
					console.error('There was an error: ', error);
				});
		},

		/**
		 * Method called to get file in binary format
		 */
		loadFile: function (url, fileName) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						url: url,
						xhrFields: {
							responseType: 'blob'
						}
					})
					.done(function (data) {
						resolve({
							fileName: fileName,
							fileData: data
						});
					})
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			});
		},

		xlsxTabDataDownload: function (fileName, data) {
			var workbook = new ExcelJS.Workbook();
			data.forEach(function (e) {
				var worksheet = workbook.addWorksheet(e.sheet);
				// Specify the headers and make them bold
				worksheet.columns = e.cols;
				// Add the data rows
				e.rows.forEach(function (r) {
					var row = worksheet.addRow(r);
					if (r.styles && r.styles.type === "row") {
						row.eachCell(function (cell) {
							cell.font = {
								bold: true
							}
						});
					}
					row.eachCell({
						includeEmpty: true
					}, function (cell, colNumber) {
						// Apply right alignment to all cells except the first and last
						if (colNumber > 1 && colNumber < row.cellCount) {
							cell.alignment = {
								horizontal: 'right'
							};
						}
					});
				});
				// Make header row bold
				worksheet.getRow(1).font = {
					bold: true
				};
				worksheet.getRow(1).alignment = {
					vertical: 'middle',
					horizontal: 'center'
				};
				worksheet.getRow(1).eachCell(function (cell) {
					cell.fill = {
						type: 'pattern',
						pattern: 'solid',
						fgColor: {
							argb: 'D9E1F2'
						}
					};
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
				});
			});

			workbook.xlsx.writeBuffer().then(function (buffer) {
					var blob = new Blob([buffer], {
						type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var link = document.createElement('a');
					link.href = window.URL.createObjectURL(blob);
					link.download = fileName;
					document.body.appendChild(link); // Required for FF
					link.click();
					document.body.removeChild(link);
				})
				.catch(function (error) {
					console.error('Error writing Excel file:', error);
				});
		},

		_jsonDownload: function (data, fileName, flag) {
			// Create a Blob from the JSON data
			var blob = new Blob([JSON.stringify(JSON.parse(data))], {
				"type": "application/json"
			});

			var url = URL.createObjectURL(blob),
				link = document.createElement('a'),
				d = new Date();

			if (flag) {
				var timeStamp = (d.getDate() < 10 ? '0' : '') + d.getDate() + (d.getMonth() < 9 ? '0' : '') + (d.getMonth() + 1) + d.getFullYear() +
					"T" + d.getHours() + d.getMinutes() + d.getSeconds();
			}
			link.href = url;
			link.download = fileName + (!timeStamp ? '' : timeStamp) + ".json";

			document.body.appendChild(link);

			sap.ui.getCore().attachInit(function () {
				link.click(); // This triggers the download
				document.body.removeChild(link); // Removes the link from the DOM after download is triggered
			});
		},

		_getExcelTimeStamp: function () {
			var date = new Date(),
				dd = date.getDate().toString().padStart(2, 0),
				mm = (date.getMonth() + 1).toString().padStart(2, 0),
				hrs = date.getHours().toString().padStart(2, '0'),
				min = date.getMinutes().toString().padStart(2, '0'),
				sec = date.getSeconds().toString().padStart(2, '0'),
				ms = date.getMilliseconds().toString().padStart(3, '0');

			return date.getFullYear() + mm + dd + 'T' + hrs + min + sec + ms;
		},

		onMultiInputChange: function (oEvent) {
			var oMultiInput = oEvent.getSource();
			var sValue = oMultiInput.getValue().trim();

			// Check if there's remaining value to convert into a token
			if (sValue) {
				var oToken = new sap.m.Token({
					key: sValue,
					text: sValue
				});
				oMultiInput.addToken(oToken);
				oMultiInput.setValue(""); // Clear the input after tokenization
			}
		},

		throttle: function (func, limit) {
			var inThrottle;
			var context = this; // Save the context outside of the returned function
			return function () {
				var args = arguments; // Use the arguments object in ES5
				if (!inThrottle) {
					func.apply(context, args);
					inThrottle = true;
					setTimeout(function () {
						inThrottle = false;
					}, limit);
				}
			};
		}
	});
});