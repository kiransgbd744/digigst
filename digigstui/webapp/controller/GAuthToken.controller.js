sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/export/Spreadsheet",
	"sap/ui/export/library",
	"sap/m/MessageBox",
], function (Controller, JSONModel, Filter, FilterOperator, Spreadsheet, exportLibrary, MessageBox) {
	"use strict";

	var EdmType = exportLibrary.EdmType;
	return Controller.extend("com.ey.digigst.controller.GAuthToken", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GAuthToken
		 */
		onInit: function () {
			this._bindFilter();
			this.getOwnerComponent().getRouter().getRoute("GAuthToken").attachPatternMatched(this._onRouteMatched, this);
		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.GAuthToken
		 */
		//	onAfterRendering: function() {
		//
		//	},

		_bindFilter: function () {
			this.getView().setModel(new JSONModel({
				"entityIds": [],
				"gstins": [],
				"status": ""
			}), "FilterModel");
		},

		_onRouteMatched: function (oEvent) {
			if (this.getRouter().getHashChanger().getHash() === "GroupAuthToken") {
				this._getAllAuthToken();
			}
		},

		_getAllAuthToken: function (flag) {
			return new Promise(function (resolve, reject) {
				var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
					payload = {
						"req": {
							"entityIds": this.removeAll(oFilterData.entityIds),
							"gstin": this.removeAll(oFilterData.gstins)
						}
					};
				if (!payload.req.entityIds.length) {
					var oEntity = this.getOwnerComponent().getModel("entityAll").getProperty("/"),
						aEntity = oEntity.map(function (e) {
							return e.entityId;
						});
					payload.req.entityIds = this.removeAll(aEntity);
				}
				if (!flag && this._checkPayloadMatch(payload.req)) {
					this._filterAuthTokenData(oFilterData.status);
					return;
				};
				sap.ui.core.BusyIndicator.show(0);
				this.getView().setModel(new JSONModel([]), "GroupAuthToken");
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getAuthTokenDetailsForGroup.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "GroupAuthToken");
						this._filterAuthTokenData(oFilterData.status);
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}.bind(this));
		},

		_checkPayloadMatch: function (payload) {
			var oModel = this.getView().getModel("AuthTokenPayload"),
				flag = true;

			if (oModel) {
				var oData = oModel.getProperty("/");
				if (payload.entityIds.length !== oData.entityIds.length || payload.gstin.length !== oData.gstin.length) {
					flag = false;
				} else {
					payload.entityIds.forEach(function (e) {
						if (!oData.entityIds.includes(e)) {
							flag = false;
						}
					});
					payload.gstin.forEach(function (e) {
						if (!oData.gstin.includes(e)) {
							flag = false;
						}
					});
				}
			} else {
				flag = false;
			}
			if (!flag) {
				this.getView().setModel(new JSONModel(payload), "AuthTokenPayload");
			}
			return flag;
		},

		_filterAuthTokenData: function (status) {
			var oBinding = this.byId("tabAuthToken").getBinding("rows"),
				aFilter = [
					new Filter("status", FilterOperator.Contains, status)
				];
			oBinding.filter(aFilter);
		},

		onChangeEntity: function (oEvent) {
			this.selectAll(oEvent);
			var aEntity = this.getView().getModel("FilterModel").getProperty("/entityIds"),
				payload = {
					"req": {
						"entityIds": this.removeAll(aEntity)
					}
				};
			this.getView().getModel("FilterModel").setProperty("/gstins", []);
			if (!aEntity.length) {
				this.getView().setModel(new JSONModel([]), "GstinModel");
				return;
			}
			sap.ui.core.BusyIndicator.show(0);
			this.getView().setModel(new JSONModel([]), "GstinModel");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getAllGstins.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					data.resp.gstinInfo.sort(function (a, b) {
						if (a.gstin < b.gstin) return -1;
						if (a.gstin > b.gstin) return 1;
						return 0;
					});
					if (data.resp.gstinInfo.length) {
						data.resp.gstinInfo.unshift({
							"gstin": "All"
						});
					}
					var oModel = new JSONModel(data.resp.gstinInfo);
					oModel.setSizeLimit(data.resp.gstinInfo.length);
					this.getView().setModel(oModel, "GstinModel");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onClearAuthToken: function () {
			this._bindFilter();
			this._getAllAuthToken();
		},

		onSearchAuthToken: function () {
			this._getAllAuthToken();
		},

		onGenerateAuthToken: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("GroupAuthToken").getObject();
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": obj.gstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressYes: function () {
			var payload = {
				"req": this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getProperty("/")
			};
			this.byId("dAuthTokenConfirmation").setBusy(true);
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GenerateOTP", this);
				this.getView().addDependent(this._dGenerateOTP);
			}
			this.byId("dVerifyAuthToken").setModel(new JSONModel(payload.req), "OtpProperty");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getOtp.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
					this._dAuthToken.close();
					if (data.resp.status !== "S") {
						MessageBox.error("OTP Generation Failed. Please Try Again", {
							styleClass: "sapUiSizeCompact"
						});
						return;
					}
					var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty");
					oModel.setProperty("/verify", false);
					oModel.setProperty("/otp", null);
					oModel.setProperty("/resendOtp", false);
					oModel.refresh(true);
					this._dGenerateOTP.open();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
				}.bind(this));
		},

		onPressCancel: function () {
			this._dAuthToken.close();
		},

		onPressVerifyOTP: function () {
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getProperty("/"),
				payload = {
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
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
					if (data.resp.status === "S") {
						MessageBox.success("OTP is  Matched", {
							styleClass: "sapUiSizeCompact"
						});
						this._getAllAuthToken(true);
					} else {
						MessageBox.error("OTP is Not Matched", {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
				}.bind(this));
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		onDownloadReport: function () {
			var oData = this._getLedgerDataSource(),
				data = [{
					sheet: "AuthToken",
					cols: this._getLedgerColumns(),
					rows: oData
				}];
			if (!oData.length) {
				MessageBox.error("No data to download");
				return;
			}
			this.xlsxTabDataDownload("Auth Token Status_" + this.getDateTimeStamp() + ".xlsx", data);
		},

		_getLedgerDataSource: function () {
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				oToken = this.getView().getModel("GroupAuthToken").getProperty("/"),
				oData = oToken.filter(function (e) {
					return e.status.includes(oFilterData.status);
				});
			oData.forEach(function (e) {
				e.status = (e.status === "A" ? "Active" : "Inactive");
			});
			oData.sort(function (a, b) {
				if (a.entityName < b.entityName) return -1;
				if (a.entityName > b.entityName) return 1;
				if (a.gstin < b.gstin) return -1;
				if (a.gstin > b.gstin) return 1;
				return 0;
			});
			return oData;
		},

		_getLedgerColumns: function () {
			return [{
				key: "entityName",
				header: "Entity Name",
				width: 25
			}, {
				key: "gstin",
				header: "GSTIN",
				width: 18
			}, {
				key: "mobileNo",
				header: "Mobile Number",
				width: 14
			}, {
				key: "email",
				header: "Email Address",
				width: 35
			}, {
				key: "status",
				header: "Auth Token Status",
				width: 16
			}];
		}
	});
});