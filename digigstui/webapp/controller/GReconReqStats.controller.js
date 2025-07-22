sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.GReconReqStats", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GReconReqStats
		 */
		onInit: function () {
			var vDate = new Date(),
				vFrDt = new Date();
			vFrDt.setDate(vFrDt.getDate() - 19);

			this._bindFilter();
			this._setDateProperty("frInitiateDt", vFrDt, vDate, null);
			this._setDateProperty("toInitiateDt", vDate, vDate, vFrDt);
			this.getOwnerComponent().getRouter().getRoute("GReconReqStats").attachPatternMatched(this._onRouteMatched, this);
		},

		_bindFilter: function () {
			var vDate = new Date(),
				vFrDt = new Date();
			vFrDt.setDate(vFrDt.getDate() - 19);

			this.getView().setModel(new JSONModel({
				"frDate": this._formatDate(vFrDt),
				"toDate": this._formatDate(vDate),
				"requestId": [],
				"reconType": "",
				"entityIds": [$.sap.entityID],
				"userIds": [],
				"emaildIds": [],
				"reconStats": ""
			}), "FilterModel");
		},

		_onRouteMatched: function (oEvent) {
			var hash = this.getRouter().getHashChanger().getHash();
			if (hash === "GReconReqStats") {
				var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
					payload = {
						"entityIds": this.removeAll(oFilterData.entityIds)
					};
				Promise.all([
						this._getRequestIds(),
						this._getReconRequestStats(true),
						this._getUserId(payload),
						this._getUserEmails(payload)
					])
					.then(function (values) {
						oFilterModel.setProperty("/requestId", []);
						oFilterModel.setProperty("/userIds", []);
						oFilterModel.setProperty("/emaildIds", []);
						oFilterModel.refresh(true);
						sap.ui.core.BusyIndicator.hide();
					}.bind(this))
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
					}.bind(this));
			}
		},

		onChangeDate: function (type) {
			if (type === "F") {
				var frDate = this.byId("frInitiateDt").getDateValue(),
					toDate = this.byId("toInitiateDt").getDateValue();

				if (frDate > toDate) {
					this.byId("toInitiateDt").setDateValue(frDate);
				}
				this.byId("toInitiateDt").setMinDate(frDate);
			}
			this._getRequestIds(true);
		},

		_getRequestIds: function (flag) {
			return new Promise(function (resolve, reject) {
				var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
					aEntity = this.getOwnerComponent().getModel("entityAll").getProperty("/"),
					payload = {
						"entityIds": this.removeAll(oFilterData.entityIds),
						"initiationDateFrom": oFilterData.frDate,
						"initiationDateTo": oFilterData.toDate
					};
				if (!payload.entityIds.length) {
					payload.entityIds = this.removeAll(aEntity.map(function (e) {
						return ('' + e.entityId);
					}));
				}
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2RequestIdsForGroup.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.requestDetails.unshift({
							requestId: "All"
						});
						var oModel = new JSONModel(data.resp.requestDetails);
						oModel.setSizeLimit(data.resp.requestDetails.length);
						this.getView().setModel(oModel, "RequestIds");
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onSelectEntity: function (oEvent) {
			this.selectAll(oEvent);
			var oFilterModel = this.getView().getModel("FilterModel"),
				payload = {
					"entityIds": this.removeAll(oFilterModel.getProperty("/entityIds"))
				},
				apiList = [this._getRequestIds()];

			if (!payload.entityIds.length) {
				this.getView().setModel(new JSONModel([]), "UserNames");
				this.getView().setModel(new JSONModel([]), "UserEmailIds");
			} else {
				apiList.push(this._getUserId(payload));
				apiList.push(this._getUserEmails(payload));
			}
			Promise.all(apiList)
				.then(function (values) {
					oFilterModel.setProperty("/requestId", []);
					oFilterModel.setProperty("/userIds", []);
					oFilterModel.setProperty("/emaildIds", []);
					oFilterModel.refresh(true);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getUserId: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2UserNamesForGroup.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.requestDetails.unshift({
							userName: "All"
						});
						this.getView().setModel(new JSONModel(data.resp.requestDetails), "UserNames");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		_getUserEmails: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2EmailIdsForGroup.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						data.resp.requestDetails.unshift({
							email: "All"
						});
						this.getView().setModel(new JSONModel(data.resp.requestDetails), "UserEmailIds");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					});
			}.bind(this));
		},

		onClearReconReqStats: function () {
			this._bindFilter();
			this._getReconRequestStats();
		},

		onSearchReconReqStats: function () {
			this._getReconRequestStats();
		},

		_getReconRequestStats: function (flag) {
			return new Promise(function (resolve, reject) {
				var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
					aEntity = this.getOwnerComponent().getModel("entityAll").getProperty("/"),
					payload = {
						"req": {
							"entityIds": this.removeAll(oFilterData.entityIds),
							"initiationDateFrom": oFilterData.frDate,
							"initiationDateTo": oFilterData.toDate,
							"requestId": this.removeAll(oFilterData.requestId),
							"reconType": oFilterData.reconType,
							"initiationByUserId": this.removeAll(oFilterData.userIds),
							"initiationByUserEmailId": this.removeAll(oFilterData.emaildIds),
							"reconStatus": oFilterData.reconStats
						}
					};
				if (!payload.req.entityIds.length) {
					payload.req.entityIds = this.removeAll(aEntity.map(function (e) {
						return ('' + e.entityId);
					}));
				}
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getgstr2ReportRequestStatusFilterForGroup.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "ReconReqStats");
						if (!flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (!flag) {
							sap.ui.core.BusyIndicator.hide();
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPressGstinNo: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReconReqStats").getObject();
			if (["2BPR", "EINVPR"].includes(obj.reconType)) {
				if (!this._popover2BPR) {
					this._popover2BPR = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.Popover2BPR", this);
					this.getView().addDependent(this._popover2BPR);
				}

				this._popover2BPR.setModel(new JSONModel(obj.gstins), "GstinList");
				this._popover2BPR.openBy(oEvent.getSource());
			} else {
				if (!this._popover2APR) {
					this._popover2APR = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.Popover2APR", this);
					this.getView().addDependent(this._popover2APR);
				}
				this._popover2APR.setModel(new JSONModel(obj.gstins), "GstinList");
				this._popover2APR.openBy(oEvent.getSource());
			}
		},

		onDownloadReconReqStats: function (oEvent) {
			var obj = oEvent.getSource().getBindingContext("ReconReqStats").getObject(),
				payload = {
					"req": {
						"configId": obj.requestId,
						"reconType": obj.reconType
					}
				};
			if (!this._getRptDownload) {
				this._getRptDownload = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.general.ReportDownload", this);
				this.getView().addDependent(this._getRptDownload);
			}
			Object.assign(obj, {
				"segment": "transaction"
			});
			this._getRptDownload.setModel(new JSONModel(obj), "PropReport");
			this._getDownloadRptData(payload);
			this._getRptDownload.open();
		},

		onCloseRptDownload: function () {
			this._getRptDownload.close();
		},

		_getDownloadRptData: function (payload) {
			this._getRptDownload.setBusy(true);
			this._getRptDownload.setModel(new JSONModel({
				"transactionalRecords": [],
				"summaryRecords": []
			}), "ReportDownload");
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2DownloadIdWise.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = this._getRptDownload.getModel("ReportDownload"),
						oTrans = oModel.getProperty("/transactionalRecords");
					if (data.hdr.status === "S") {
						var curL1Obj = {}, // the current level1 object.
							curL2Obj = {}; // the current level2 object.

						data.resp.transactionalRecords.forEach(function (e) {
							if (e.level === "L1") {
								curL1Obj = e;
								curL1Obj.level2 = [];
								oTrans.push(curL1Obj);
							}
							if (e.level === "L2") {
								curL2Obj = e;
								curL2Obj.level3 = [];
								curL1Obj.level2.push(curL2Obj);
							}
							if (e.level === "L3") {
								curL2Obj.level3.push(e);
							}
						});
						oModel.setProperty("/summaryRecords", data.resp.summaryRecords);
						this.byId("tabSummary").setVisibleRowCount(data.resp.summaryRecords.length || 5);
					}
					oModel.refresh(true);
					this._getRptDownload.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._getRptDownload.setBusy(false);
				}.bind(this));
		},

		onExpandCollapse: function (type) {
			if (type === "E") {
				this.byId("tabTrans").expandToLevel(1);
			} else {
				this.byId("tabTrans").collapseAll();
			}
		},

		onGenerateRpt: function () {
			var reqId = this._getRptDownload.getModel("PropReport").getProperty("/requestId"),
				payload = {
					"req": {
						"configId": reqId
					}
				};
			this._getRptDownload.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr2ReconReportReGenerate.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					MessageBox.success(data.resp);
					this._getRptDownload.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					MessageBox.success(JSON.parse(jqXHR.responseText).resp);
					this._getRptDownload.setBusy(false);
				}.bind(this));
		},

		onReportDownload: function (path) {
			var reconType = this._getRptDownload.getModel("PropReport").getProperty("/reconType"),
				payload = {
					"req": {
						"filePath": path,
						"reconType": reconType
					}
				};
			this.excelDownload(payload, "/aspsapapi/gstr2ReconReportDownload.do");
		},

		_setDateProperty: function (id, date, maxDt, minDt) {
			this.byId(id).setMinDate(minDt);
			this.byId(id).setMaxDate(maxDt);
			this.byId(id).setDateValue(date);
			this.byId(id).addEventDelegate({
				onAfterRendering: function (oEvent) {
					oEvent.srcControl.$().find("input").attr("readonly", true);
				}
			});
		}
	});
});