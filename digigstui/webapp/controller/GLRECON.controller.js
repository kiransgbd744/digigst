sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/m/MessageBox",
	"com/ey/digigst/util/Formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/Dialog",
	"sap/m/Button"
], function (BaseController, MessageBox, Formatter, JSONModel, Dialog, Button) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.GLRECON", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.GLRECON
		 */
		onInit: function () {
			var vDate = new Date();
			this.byId("GLtaxDate").setMaxDate(vDate);
			this.byId("GLtaxDate").setDateValue(vDate);
			this._bindScreenProperty();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				// var oData = this.getView().getModel("userPermission").getData().respData.dataSecurity.gstin.filter(function(obj){
				// 	return obj.value !== "All";
				// });
				// this.getView().setModel(new JSONModel(oData),"oGSTIN");
				// this.getView().getModel("oGSTIN").refresh(true);
				this.onLoadGSTINlist();
				if (this.getView().byId("idDpGLSummary").getVisible() == true) {
					this.onGoSummary();
				}
			}
		},

		onLoadGSTINlist: function () {
			var that = this;
			var PayLoad = {
				// "req":{
				"entityId": parseInt($.sap.entityID),
				"dataType": that.byId("id_DataType").getSelectedKey()
					// }
			};
			var Url = "/aspsapapi/getGlReconGstinsList.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: Url,
					contentType: "application/json",
					data: JSON.stringify(PayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					data.resp.gstinDetails.unshift({
						"gstin": "All"
					});
					that.getView().setModel(new JSONModel(data), "oGSTIN");
					that.getView().getModel("oGSTIN").refresh(true);

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		_bindScreenProperty: function () {
			// debugger; //eslint-disable-line
			var oPrProperty = {
				"GLdataFullScreen": false,
				"summaryFullScreen": false,
				"DigiGSTFullScreen": false,
				"GLReconFullScreen": false
			};
			this.getView().setModel(new JSONModel(oPrProperty), "PrProperty");
		},
		onDataTypeChange: function (oevt) {
			var that = this;
			this.onLoadGSTINlist();
			that.getView().byId("GSTINEntityID").removeSelectedKeys([]);
		},
		onGstr2FullScreen: function (action) {
			// debugger; //eslint-disable-line
			var oPropModel = this.getView().getModel("PrProperty"),
				oPropData = oPropModel.getData();

			if (action === "openGLdata") {
				oPropData.GLdataFullScreen = true;
				this.byId("idCcGLdata").setFullScreen(true);
				this.byId("id_GldataTab").setVisibleRowCount(26);

			} else if (action === "closeGLdata") {
				oPropData.GLdataFullScreen = false;
				this.byId("idCcGLdata").setFullScreen(false);
				this.byId("id_GldataTab").setVisibleRowCount(14);

			} else if (action === "openSummary") {
				oPropData.summaryFullScreen = true;
				this.byId("idCcSummary").setFullScreen(true);
				this.byId("id_GlSummaryTab").setVisibleRowCount(26);

			} else if (action === "closeSummary") {
				oPropData.summaryFullScreen = false;
				this.byId("idCcSummary").setFullScreen(false);
				this.byId("id_GlSummaryTab").setVisibleRowCount(14);

			} else if (action === "openDigiGST") {
				oPropData.DigiGSTFullScreen = true;
				this.byId("idCcDigiGST").setFullScreen(true);
				this.byId("idDigiGSTTab").setVisibleRowCount(26);

			} else if (action === "closeDigiGST") {
				oPropData.DigiGSTFullScreen = false;
				this.byId("idCcDigiGST").setFullScreen(false);
				this.byId("idDigiGSTTab").setVisibleRowCount(14);

			} else if (action === "openGLRecon") {
				oPropData.GLReconFullScreen = true;
				this.byId("idCcGLRecon").setFullScreen(true);
				this.byId("id_GLReconTab").setVisibleRowCount(26);

			} else if (action === "closeGLRecon") {
				oPropData.GLReconFullScreen = false;
				this.byId("idCcGLRecon").setFullScreen(false);
				this.byId("id_GLReconTab").setVisibleRowCount(14);
			}
			oPropModel.refresh(true);
		},
		//============== GL processed Summary =================//
		onPressGLGstin: function (oEvt) {
			var that = this;

			that.getView().byId("idDpGLSummary").setVisible(false);
			that.getView().byId("id_GlTabBar").setVisible(true);
			that.getView().byId("id_GlTabBar").setSelectedKey("GLData");
			var contextPath = oEvt.getSource().getBindingContext("GLSummary").getPath();
			that.selectedGstin = that.getView().getModel("GLSummary").getObject(contextPath).gstins;
			var vDate = new Date();
			that.byId("idGlDataTaxPeriod").setMaxDate(vDate);
			that.byId("idGlDataTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
			that.byId("id_GLDataDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
			that.getView().byId("idGlDataGstn").setSelectedKey(that.selectedGstin);
			that.loadGlData();
		},
		handleIconTabBarSelect: function (oEvt) {
			var that = this;
			var vKey = oEvt.getSource().getSelectedKey();
			var vDate = new Date();
			if (vKey == "DigiGST") {
				that.byId("idDigiGSTTaxPeriod").setMaxDate(vDate);
				// that.byId("idDigiGSTTaxPeriod").setDateValue(vDate);
				that.byId("idDigiGSTTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
				that.byId("id_DigiGSTDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
				that.getView().byId("idDigiGSTGstn").setSelectedKey(that.selectedGstin);
				that.loadDigiGSTData();
			} else if (vKey == "GLRecon") {
				that.byId("idReconGLTaxPeriod").setMaxDate(vDate);
				// that.byId("idReconGLTaxPeriod").setDateValue(vDate);
				that.byId("idReconGLTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
				that.byId("id_ReconGLDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
				that.getView().byId("idReconGSTGstn").setSelectedKey(that.selectedGstin);
				that.loadGLReconData();
			} else if (vKey == "GLData") {
				that.byId("idGlDataTaxPeriod").setMaxDate(vDate);
				// that.byId("idGlDataTaxPeriod").setDateValue(vDate);
				that.byId("idGlDataTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
				that.byId("id_GLDataDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
				that.getView().byId("idGlDataGstn").setSelectedKey(that.selectedGstin);
				that.loadGlData();
			}
		},
		onPressBackGlSummary: function () {
			var that = this;
			that.getView().byId("idDpGLSummary").setVisible(true);
			that.getView().byId("id_GlTabBar").setVisible(false);
			that.onGoSummary();
		},
		onPressGLSumComputeData: function () {
			var that = this;
			var aIndex = that.byId("id_GlSummaryTab").getSelectedIndices();
			var aTabData = that.byId("id_GlSummaryTab").getModel("GLSummary").getData().resp;
			if (aIndex.length === 0) {
				sap.m.MessageBox.information("Select atleast one record to Compute GL Data - DigiGST", {
					styleClass: "sapUiSizeCompact"
				});
				return;
			} else {
				var gstins = [];
				for (var i = 0; i < aIndex.length; i++) {
					gstins.push(aTabData[aIndex[i]].gstins);
				}
				var PayLoad = {
					"req": {
						"gstins": gstins,
						"taxPeriod": that.byId("GLtaxDate").getValue(),
						"dataType": that.byId("id_DataType").getSelectedKey()
					}
				};
				MessageBox.show(
					"Do you want to 'Compute GL Data - DigiGST'?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								sap.ui.core.BusyIndicator.show(0);
								var Url = "/aspsapapi/computeGlData.do";
								$(document).ready(function ($) {
									$.ajax({
										method: "POST",
										url: Url,
										contentType: "application/json",
										data: JSON.stringify(PayLoad)
									}).done(function (data, status, jqXHR) {
										sap.ui.core.BusyIndicator.hide();
										if (data.hdr.status == "S") {
											MessageBox.success(data.resp);
											that.byId("id_GlSummaryTab").clearSelection(true);
											that.onGoSummary();
										} else {
											MessageBox.error(data.resp);
										}
									}).fail(function (jqXHR, status, err) {
										sap.ui.core.BusyIndicator.hide();
									});
								});
							}
						}
					});

			}

		},
		onPressGLSumInitRecon: function () {
			var that = this;
			var aIndex = that.byId("id_GlSummaryTab").getSelectedIndices();
			var aTabData = that.byId("id_GlSummaryTab").getModel("GLSummary").getData().resp;
			if (aIndex.length === 0) {
				sap.m.MessageBox.information(
					"Select atleast one record to compute differential between GL data uploaded and GL data computed by DigiGST", {
						styleClass: "sapUiSizeCompact"
					});
				return;
			} else {
				var gstins = [];
				for (var i = 0; i < aIndex.length; i++) {
					gstins.push(aTabData[aIndex[i]].gstins);
				}
				var PayLoad = {
					"req": {
						"gstins": gstins,
						"taxPeriod": that.byId("GLtaxDate").getValue(),
						"dataType": that.byId("id_DataType").getSelectedKey()
					}
				};
				MessageBox.show(
					"Do you want to compute differential between GL data uploaded and GL data computed by DigiGST?", {
						icon: MessageBox.Icon.INFORMATION,
						title: "Information",
						actions: [MessageBox.Action.YES, MessageBox.Action.NO],
						onClose: function (oAction) {
							if (oAction === "YES") {
								sap.ui.core.BusyIndicator.show(0);
								var Url = "/aspsapapi/initiateGlData.do";
								$(document).ready(function ($) {
									$.ajax({
										method: "POST",
										url: Url,
										contentType: "application/json",
										data: JSON.stringify(PayLoad)
									}).done(function (data, status, jqXHR) {
										sap.ui.core.BusyIndicator.hide();
										if (data.hdr.status == "S") {
											MessageBox.success(data.resp);
											that.byId("id_GlSummaryTab").clearSelection(true);
											that.onGoSummary();
										} else {
											MessageBox.error(data.resp);
										}
									}).fail(function (jqXHR, status, err) {
										sap.ui.core.BusyIndicator.hide();
									});
								});
							}
						}
					});
			}
		},

		onPressGLSumDelete: function () {
			MessageBox.show(
				"Do you want to Delete the GL data uploaded?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},

		onClearMainFilter: function (oevt) {
			var that = this;
			that.getView().byId("GSTINEntityID").setSelectedKeys([]);
			that.getView().byId("id_DataType").setSelectedKeys([]);
			var vDate = new Date();
			this.byId("GLtaxDate").setMaxDate(vDate);
			this.byId("GLtaxDate").setDateValue(vDate);
		},

		onGoSummary: function () {
			var that = this;
			var oView = this.getView();
			var aGstin = that.byId("GSTINEntityID").getSelectedKeys();
			var oPayLoad = {
				"req": {
					"entityId": $.sap.entityID,
					"gstins": aGstin.includes("All") ? [] : aGstin,
					// "gstins"		:["29AAAPH9357H000"],
					"taxPeriod": that.byId("GLtaxDate").getValue(),
					"dataType": that.byId("id_DataType").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/glSummaryData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.length != 0) {
						// for(var i=0; i<data.resp.length; i++){
						// 	data.resp[i].diffAmount =  Number(data.resp[i].amount) - Number(data.resp[i].digiAmount);                                  
						// }
						that.getView().setModel(new JSONModel(data), "GLSummary");
						that.getView().getModel("GLSummary").refresh(true);
					} else {
						MessageBox.information("No data is available");
						that.getView().setModel(new JSONModel(data), "GLSummary");
						that.getView().getModel("GLSummary").refresh(true);
					}

				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onPressDownloadReport: function (oEvent, parameter, view) {
			debugger;
			var Url = "";
			var that = this;
			var PayLoad = {};
			var gstins = [];
			switch (parameter.getKey()) {
			case "GlUploaded":
				Url = "/aspsapapi/getGLProcessedRecordsByGstin.do";
				break;
			case "GlcomputedDigiGSTl":
				Url = "/aspsapapi/getDigiGstComputedGlData.do";
				break;
			case "GlReconReport":
				Url = "/aspsapapi/getGlLnkRecon.do";
				break;
			}
			if (view == "S") {
				var aIndex = that.byId("id_GlSummaryTab").getSelectedIndices();
				var aTabData = that.byId("id_GlSummaryTab").getModel("GLSummary").getData().resp;
				if (aIndex.length === 0) {
					sap.m.MessageBox.information("Select atleast one record to download report", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				} else {
					for (var i = 0; i < aIndex.length; i++) {
						gstins.push(aTabData[aIndex[i]].gstins);
					}
					var dataType = "";
					// if(that.byId("id_DataType").getSelectedKey() == "I"){
					// 	dataType = "INWARD";
					// }else{
					dataType = that.byId("id_DataType").getSelectedKey();
					// }
					PayLoad = {
						"req": {
							"gstins": gstins,
							"taxPeriod": that.byId("GLtaxDate").getValue(),
							"dataType": dataType,
							"entityId": $.sap.entityID
						}
					};
				}
			} else if (view == "G") {
				if (that.byId("idGlDataGstn").getSelectedKey() != "") {
					gstins.push(that.byId("idGlDataGstn").getSelectedKey());
					var dataType = "";
					// if(that.byId("id_GLDataDataTy").getSelectedKey() == "I"){
					// 	dataType = "INWARD";
					// }else{
					dataType = that.byId("id_GLDataDataTy").getSelectedKey();
					// }
					PayLoad = {
						"req": {
							"gstins": gstins,
							"taxPeriod": that.byId("idGlDataTaxPeriod").getValue(),
							"dataType": dataType,
							"entityId": $.sap.entityID
						}
					};
				} else {
					sap.m.MessageBox.information("Select gstn to download report", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			} else if (view == "D") {
				if (that.byId("idDigiGSTGstn").getSelectedKey() != "") {
					gstins.push(that.byId("idDigiGSTGstn").getSelectedKey());
					var dataType = "";
					// if(that.byId("id_ReconGLDataTy").getSelectedKey() == "I"){
					// 	dataType = "INWARD";
					// }else{
					dataType = that.byId("id_ReconGLDataTy").getSelectedKey();
					// }
					PayLoad = {
						"req": {
							"gstins": gstins,
							"taxPeriod": that.byId("idDigiGSTTaxPeriod").getValue(),
							"dataType": dataType,
							"entityId": $.sap.entityID
						}
					};
				} else {
					sap.m.MessageBox.information("Select gstn to download report", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			} else if (view == "R") {
				if (that.byId("idReconGSTGstn").getSelectedKey() !== "") {
					gstins.push(that.byId("idReconGSTGstn").getSelectedKey());
					var dataType = "";
					// if(that.byId("id_ReconGLDataTy").getSelectedKey() == "I"){
					// 	dataType = "INWARD";
					// }else{
					dataType = that.byId("id_ReconGLDataTy").getSelectedKey();
					// }
					PayLoad = {
						"req": {
							"gstins": gstins,
							"taxPeriod": that.byId("idReconGLTaxPeriod").getValue(),
							"dataType": dataType,
							"entityId": $.sap.entityID
						}
					};
				} else {
					sap.m.MessageBox.information("Select gstn to download report", {
						styleClass: "sapUiSizeCompact"
					});
					return;
				}
			}
			this.excelDownload(PayLoad, Url);
		},
		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("GLSummary").getObject();
			if (oValue1.auth !== "I") {
				return;
			}
			var vGstin = oValue1.gstins;
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": vGstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},
		onPressYes: function () {
			// debugger; //eslint-disable-line
			var aData = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getData(),
				that = this,
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			that.byId("dAuthTokenConfirmation").setBusy(true);
			if (!this._dGenerateOTP) {
				this._dGenerateOTP = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.GenerateOTP", this);
				this.getView().addDependent(this._dGenerateOTP);
			}
			this.byId("dVerifyAuthToken").setModel(new JSONModel(searchInfo.req), "OtpProperty");
			$.ajax({
				method: "POST",
				url: "/aspsapapi/getOtp.do",
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				that.byId("dAuthTokenConfirmation").setBusy(false);
				that._dAuthToken.close();
				if (data.resp.status === "S") {
					var oModel = that.byId("dVerifyAuthToken").getModel("OtpProperty"),
						oData = oModel.getData();
					oData.verify = false;
					oData.otp = null;
					oData.resendOtp = false;
					oModel.refresh(true);
					that._dGenerateOTP.open();
				} else {
					MessageBox.error("OTP Generation Failed. Please Try Again", { // Modified by Bharat Gupta on 05.02.2020
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				that.byId("dAuthTokenConfirmation").setBusy(false);
			});
		},
		onPressVerifyOTP: function () {
			var oData = this.byId("dVerifyAuthToken").getModel("OtpProperty").getData(),
				that = this,
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
			}).done(function (data, status, jqXHR) {
				that.byId("dVerifyAuthToken").setBusy(false);
				that._dGenerateOTP.close();
				if (data.resp.status === "S") {
					MessageBox.success("OTP is  Matched", {
						styleClass: "sapUiSizeCompact"
					});
				} else {
					MessageBox.error("OTP is Not Matched", {
						styleClass: "sapUiSizeCompact"
					});
				}
			}).fail(function (jqXHR, status, err) {
				that.byId("dVerifyAuthToken").setBusy(false);
				that._dGenerateOTP.close();
			});
		},
		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},
		//=========================== GL Data ===================================//

		onPressGlDataEdit: function () {
			MessageBox.show(
				"Do you want to edit the selected data?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onPressGlDataSave: function () {
			MessageBox.show(
				"Do you want to save changes?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onPressGLDataCompute: function () {
			MessageBox.show(
				"Do you want to 'Compute GL Data - DigiGST'?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onPressGLDataInitRecon: function () {
			MessageBox.show(
				"Do you want to Compute Differential between GL data uploaded and GL data computed by DigiGST?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},

		onPressGLDataDelete: function () {
			MessageBox.show(
				"Do you want to Delete the GL data uploaded?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onGoGLData: function () {
			this.loadGlData();
		},
		loadGlData: function () {
			var that = this;
			var oView = this.getView();
			var oPayLoad = {
				"req": {
					"entityId": $.sap.entityID,
					"gstin": that.getView().byId("idGlDataGstn").getSelectedKey(),
					// "gstin"		:"29AAAPH9357H000",
					"taxPeriod": that.byId("idGlDataTaxPeriod").getValue(),
					"dataType": that.byId("id_GLDataDataTy").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGlReconProcessedData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "oGLDataModel");
					that.getView().getModel("oGLDataModel").refresh(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		onClearGLDataFilter: function (oevt) {
			var that = this;
			that.byId("idGlDataTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
			that.byId("id_GLDataDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
			that.getView().byId("idGlDataGstn").setSelectedKey(that.selectedGstin);
		},
		//=================================== DigiGST Compute ================================//		
		onPressDigiGSTCompute: function () {
			MessageBox.show(
				"Do you want to 'Compute GL Data - DigiGST'?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onPressDigiGSTInitRecon: function () {
			MessageBox.show(
				"Do you want to Compute Differential between GL data uploaded and GL data computed by DigiGST?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onClearDigiGST: function (oevt) {
			var that = this;
			that.byId("idDigiGSTTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
			that.byId("id_DigiGSTDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
			that.getView().byId("idDigiGSTGstn").setSelectedKey(that.selectedGstin);
		},
		onDigiGSTGo: function () {
			this.loadDigiGSTData();
		},
		loadDigiGSTData: function () {
			var that = this;
			var oView = this.getView();

			var oPayLoad = {
				"req": {
					// "entityId"		: $.sap.entityID,
					"gstin": that.getView().byId("idDigiGSTGstn").getSelectedKey(),
					// "gstin"		:"29AAAPH9357H000",
					"taxPeriod": that.byId("idDigiGSTTaxPeriod").getValue(),
					"dataType": that.byId("id_DigiGSTDataTy").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGlDigiCompute.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "oDigiGSTModel");
					that.getView().getModel("oDigiGSTModel").refresh(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},
		//=========================== GL Compute Differential =======================//
		onPressGLReconInitRecon: function () {
			MessageBox.show(
				"Do you want to Compute Differential between GL data uploaded and GL data computed by DigiGST?", {
					icon: MessageBox.Icon.INFORMATION,
					title: "Information",
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							//
						}
					}
				});
		},
		onPressCancel: function () {
			this._dAuthToken.close();
		},
		onClearGLRecon: function (oevt) {
			var that = this;
			that.byId("idReconGLTaxPeriod").setDateValue(that.byId("GLtaxDate").getDateValue());
			that.byId("id_ReconGLDataTy").setSelectedKey(that.byId("id_DataType").getSelectedKey());
			that.getView().byId("idReconGSTGstn").setSelectedKey(that.selectedGstin);
		},
		onGLReconGo: function () {
			this.loadGLReconData();
		},
		loadGLReconData: function () {
			var that = this;
			var oView = this.getView();
			var oPayLoad = {
				"req": {
					// "entityId"		: $.sap.entityID,
					"gstin": that.getView().byId("idReconGSTGstn").getSelectedKey(),
					// "gstin"		:"29AAAPH9357H000",
					"taxPeriod": that.byId("idReconGLTaxPeriod").getValue(),
					"dataType": that.byId("id_ReconGLDataTy").getSelectedKey()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getGlLnkRecondData.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(oPayLoad)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.getView().setModel(new JSONModel(data), "oGLReconModel");
					that.getView().getModel("oGLReconModel").refresh(true);
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

	});

});