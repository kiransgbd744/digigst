sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/UIComponent"
], function (Controller, UIComponent) {
	"use strict";

	return Controller.extend("com.ey.onboarding.controller.BaseController", {

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

		emailValidation: function (oEven) {
			var emailvalue = oEven.mParameters.value;
			var mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
			if (emailvalue.match(mailformat)) {
				oEven.getSource().setValueState(sap.ui.core.ValueState.None);
				return true;
			} else {
				oEven.getSource().setValueState(sap.ui.core.ValueState.Error);
				oEven.getSource().setValue("");
				alert("You have entered an invalid email address!");
				return false;
			}
		},

		phoneValidation: function (oEven) {
			var emailvalue = oEven.mParameters.value;
			var phoneno = /^\d{10}$/;
			if (emailvalue.match(phoneno)) {
				oEven.getSource().setValueState(sap.ui.core.ValueState.None);
				return true;
			} else {
				oEven.getSource().setValueState(sap.ui.core.ValueState.Error);
				oEven.getSource().setValue("");
				alert("You have entered an invalid phone number!");
				return false;
			}
		},

		excelDownload: function (reqData, url) {
			// 			debugger; //eslint-disable-line
			var that = this;
			this.getView().setBusy(true);
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
			}).done(function (e, t, a) {
				that.getView().setBusy(false);
				if (e.size === 0) {
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
			}).fail(function (e, t, a) {
				that.getView().setBusy(false);
			});
		},

		onSelectionChangeMCB: function (oEvent) {
			debugger;
			var oSelectedKeys = oEvent.getSource().getSelectedKeys();
			var oFlag = false;
			if (oSelectedKeys.length > 0) {
				for (var i = 0; i < oSelectedKeys.length; i++) {
					if (oSelectedKeys[i] === "All") {
						oFlag = true;
					}
				}
			}
			if (oFlag === true) {
				var oLen = oSelectedKeys.length
				if (oSelectedKeys[oLen - 1] === 'All') {
					oEvent.getSource().setSelectedItems(oEvent.getSource().getItems());
				} else if (oEvent.getSource().getItems().length - 1 === oSelectedKeys.length) {
					for (var i = 0; i < oSelectedKeys.length; i++) {
						if (oSelectedKeys[i] === 'All') {
							oSelectedKeys.splice(i, 1);
						}
					}
					oEvent.getSource().setSelectedKeys(oSelectedKeys);
				}
			} else {
				if (oEvent.getSource().getItems().length - 1 === oSelectedKeys.length) {
					oEvent.getSource().setSelectedItems(null);
				} else {
					oEvent.getSource().setSelectedKeys(oSelectedKeys);
				}

			}
		},

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
		 * Convenience method to display Server Message
		 * @public
		 * @param {object} reqObj Request Object
		 */
		_serverMessage: function (reqObj) {
			MessageBox.error(reqObj.responseText);
		}
	});
});