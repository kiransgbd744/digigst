sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/viz/ui5/format/ChartFormatter",
	"sap/viz/ui5/api/env/Format",
	"sap/ui/Device",
	"com/ey/digigst/util/Formatter",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, ChartFormatter, Format, Device, Formatter, MessageBox) {
	"use strict";

	return BaseController.extend("com.ey.digigst.controller.Home", {
		formatter: Formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Home
		 */

		onInit: function () {
			this.getView().setModel(new JSONModel({
				"gstin": true,
				"division": false,
				"plant": false,
				"colVisi": true
			}), "Visi");

			this.getOwnerComponent().getRouter().getRoute("Home").attachPatternMatched(this._onRouteMatched, this);
			var vDate = new Date();
			this.byId("demoEntiyId").setText($.sap.entityName);

			this.byId("iDate").setMaxDate(vDate);
			this.byId("iDate").setDateValue(vDate);

			this.byId("iDateold").setMaxDate(vDate);
			this.byId("iDateold").setDateValue(vDate);

			var oVizFrame = this.oVizFrame = this.getView().byId("vfAnx1");
			oVizFrame.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#D2691E"],
					drawingEffect: "glossy"
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: true
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}
				}
			});

			//###############    Outward Supply - GSTR-1 vs GSTR-3B(Taxable Value)  ################
			var oVizFrame1 = this.oVizFrame = this.getView().byId("vfAnx1old11");
			Format.numericFormatter(ChartFormatter.getInstance());
			var formatPattern = ChartFormatter.DefaultPattern;
			oVizFrame1.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#BEBEBE"],
					drawingEffect: "glossy"
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: false
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}

				}
			});

			var oTooltip = new sap.viz.ui5.controls.VizTooltip({});
			oTooltip.connect(oVizFrame1.getVizUid());
			oTooltip.setFormatString(formatPattern.CURRENCY);

			//###############    Outward Supply - GSTR-1 vs GSTR-3B(Total Tax)  ################
			var oVizFrame2 = this.oVizFrame = this.getView().byId("vfAnx1old1");
			Format.numericFormatter(ChartFormatter.getInstance());
			var formatPattern1 = ChartFormatter.DefaultPattern;
			oVizFrame2.setVizProperties({
				plotArea: {
					dataLabel: {
						visible: true
					},
					colorPalette: ["#BEBEBE"],
					drawingEffect: "glossy"
				},
				title: {
					visible: false
				},
				legend: {
					visible: false
				},
				valueAxis: {
					title: {
						visible: false
					}
				},
				categoryAxis: {
					title: {
						visible: false
					}

				}
			});
			var oTooltip1 = new sap.viz.ui5.controls.VizTooltip({});
			oTooltip1.connect(oVizFrame2.getVizUid());
			oTooltip1.setFormatString(formatPattern1.CURRENCY);

			this.getView().setModel(new JSONModel({
				"Label": "2A"
			}), "Display");
		},

		_onRouteMatched: function () {
			this.onNewPressGO();
			var obj = {
				"fullScreen": false,
				"tabSize": 8
			};
			this.getView().setModel(new JSONModel(obj), "DashProperty");
			//this.onPrsGOHome();
			//this.onReconDetails();
		},

		onSearch3B1: function () {
			var payload = {
				"hdr": {
					"pageNum": 0,
					"pageSize": 50
				},
				"req": {
					"entityId": +this.byId("EntityID").getSelectedKey(),
					"taxPeriod": this.byId("gstr3bDate").getValue(),
					"gstins": this.byId("GSTINEntityID").getSelectedKeys()
				}
			};
			this.TableBinding(payload);
		},

		onEntityChange: function (oEvt) {
			var payload = {
				"entityId": +oEvt.getSource().getSelectedKey()
			};
			this.onEntityChange1(payload);
		},

		onPressRefresh: function () {
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("iDateold").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/landingDashboardRefreshButton.do",
					contentType: "application/json",
					data: JSON.stringify(oPayload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp, {
							styleClass: "sapUiSizeCompact",
							onClose: function (oAction) {
								if (oAction === MessageBox.Action.OK) {
									this._getOldOutwardStatusDetails(oPayload);
								}
							}.bind(this)
						});
					} else {
						MessageBox.error(data.resp, {
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onEntityChange1: function (payload) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getGSTINsForEntity.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data), "GstnLst");
				}.bind(this))
				.fail(function (jqXHR, status, err) {

				});
		},

		onEntityChange2: function (oEvent) {
			var vKey = oEvent.getSource().getSelectedKey();
			var aData = this.getOwnerComponent().getModel("DropDown").getData().GSTIN[vKey];
			this.byId("slGstin").setModel(new sap.ui.model.json.JSONModel(aData), "GstinModel");
		},

		onSelectionChange: function (oEvent) {
			if (oEvent.getSource().getId().includes("sbReconDet")) {
				if (oEvent.getSource().getSelectedKey().includes("recon")) {
					var vFlag = true;
					this.byId("textId").setVisible(true);
					this.byId("textId1").setVisible(false);
				} else {
					vFlag = false;
					this.byId("textId").setVisible(false);
					this.byId("textId1").setVisible(true);
				}
				this.byId("iReconGrid").setVisible(vFlag);
				this.byId("iRespGrid").setVisible(!vFlag);
			}
		},

		onButtonPress: function (oEvent) {
			if (oEvent.getSource().getId().includes("bTableAnx1")) {
				this.byId("bTableAnx1").setVisible(false);
				this.byId("bChartAnx1").setVisible(true);
				this.byId("bchartpercntage").setVisible(false);

				this.byId("vfAnx1").setVisible(false);
				this.byId("iPrAnx1").setVisible(false);
				this.byId("linegrid").setVisible(true);
			} else {
				this.byId("bTableAnx1").setVisible(true);
				this.byId("bchartpercntage").setVisible(true);
				this.byId("bChartAnx1").setVisible(false);
				this.byId("vfAnx1").setVisible(true);
				this.byId("iPrAnx1").setVisible(true);
				this.byId("linegrid").setVisible(false);
			}
		},

		onPressGenerateOTP: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("HomeOldTableData").getObject();
			if (oValue1.status !== "I") {
				return;
			}
			var vGstin = oValue1.supplierGstin;
			if (!this._dAuthToken) {
				this._dAuthToken = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.AuthToken", this);
				this.getView().addDependent(this._dAuthToken);
			}
			this.byId("dAuthTokenConfirmation").setModel(new JSONModel({
				"gstin": vGstin
			}), "AuthTokenGstin");
			this._dAuthToken.open();
		},

		onPressCancel: function () {
			this._dAuthToken.close();
		},

		onPressYes: function () {
			var aData = this.byId("dAuthTokenConfirmation").getModel("AuthTokenGstin").getData(),
				searchInfo = {
					"req": {
						"gstin": aData.gstin
					}
				};
			this.byId("dAuthTokenConfirmation").setBusy(true);
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
				})
				.done(function (data, status, jqXHR) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
					this._dAuthToken.close();
					if (data.resp.status === "S") {
						var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty");
						oModel.setProperty("/verify", false);
						oModel.setProperty("/otp", null);
						oModel.setProperty("/resendOtp", false);
						oModel.refresh(true);
						this._dGenerateOTP.open();
					} else {
						MessageBox.error("OTP Generation Failed. Please Try Again", { // Modified by Bharat Gupta on 05.02.2020
							styleClass: "sapUiSizeCompact"
						});
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dAuthTokenConfirmation").setBusy(false);
				}.bind(this));
		},

		onPressResendOTP: function () {
			var oOtpModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				oOtpData = oOtpModel.getProperty("/"),
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
					var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty");
					oModel.setProperty("/verify", false);
					oModel.setProperty("/otp", null);
					if (data.resp.status === "S") {
						oModel.setProperty("/resendOtp", true);
						this._dGenerateOTP.open();
					} else {
						oModel.setProperty("/resendOtp", false);
						MessageBox.error("OTP Generation Failed. Please Try Again", {
							styleClass: "sapUiSizeCompact"
						});
					}
					oModel.refresh(true);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.byId("dVerifyAuthToken").setBusy(false);
				}.bind(this));
		},

		validateOTP: function (oEvent) {
			var oModel = this.byId("dVerifyAuthToken").getModel("OtpProperty"),
				value = oEvent.getSource().getValue();

			value = value.replace(/[^\d]/g, "");
			oModel.setProperty("/verify", (value.length === 6));
			oModel.refresh(true);
			oEvent.getSource().setValue(value);
		},

		onPressCloseOTP: function () {
			this._dGenerateOTP.close();
		},

		onPressVerifyOTP: function () {
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
				}).done(function (data, status, jqXHR) {
					this.byId("dVerifyAuthToken").setBusy(false);
					this._dGenerateOTP.close();
					if (data.resp.status === "S") {
						MessageBox.success("OTP is  Matched", {
							styleClass: "sapUiSizeCompact"
						});
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

		onCloseOnboardingConfig: function () {
			this.byId("dConfig").close();
		},

		onOnboardingConfig: function () {
			if (!this.dConfig) {
				this.dConfig = this.loadFragment({
					name: "com.ey.digigst.fragments.general.OnboardingConfig"
				});
			}
			this.dConfig.then(function (oDialog) {
				oDialog.open();
				this.byId("dConfig").setBusy(true);
				this.byId("dConfig").setModel(new JSONModel([]), "ConfigQuestion");
				var payload = {
					"req": {
						"groupCode": $.sap.GroupCode,
						"entityId": $.sap.entityID,
						"type": "inward"
					}
				};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getConfigPrmt.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						this._bindConfigQuestion(data.resp);
						this.byId("dConfig").setBusy(false);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.byId("dConfig").setBusy(false);
					}.bind(this));
			}.bind(this));
		},

		_bindConfigQuestion: function (data) {
			var aQues = [
					"I1", "I2", "I13", "I20", "I25", "I26", "I27", "I28", "I29", "I30", "I31",
					"I35", "I34", "I36", "I33", "I37", "I38", "I44", "I45", "I51", "I50", "I53"
				],
				idx = 0,
				aData = [],
				oData = data.filter(function (e) {
					return aQues.includes(e.quesCode.split('-')[0]);
				})
				.sort(function (a, b) {
					return aQues.indexOf(a.quesCode.split('-')[0]) - aQues.indexOf(b.quesCode.split('-')[0]);
				});
			oData.forEach(function (e, i, arr) {
				var obj = {
					keyType: e.keyType,
					quesCode: e.quesCode,
					questId: e.questId,
					ques: e.ques,
					selected: e.selected,
					sequenceId: e.sequenceId,
					items: []
				};
				switch (obj.keyType) {
				case "R":
					obj.answer = e.items[e.selected];
					break;
				case "SR":
					if (["I26", "I50"].includes(e.quesCode) && e.hasOwnProperty("get2AHour")) {
						obj.answer = {
							"answerDesc": (e.get2AHour + ":" + e.get2Amin)
						};
					} else {
						obj.answer = e.items[e.selected];
						if (e.hasOwnProperty("answerDesc")) {
							obj.answer.answerDesc += ": " + e.answerDesc;
						}
					}
					break;
				case "T":
					obj.answer = e.answerDesc;
					break;
				}
				var obj1 = aData.find(function (d) {
					return d.quesCode === (e.quesCode.split('-')[0] === "I36" ? "I34" : e.quesCode.split('-')[0]);
				});
				if (obj1 && ["SR"].includes(obj.keyType)) {
					obj.quesId = String.fromCharCode(97 + obj1.items.length);
					obj1.items.push(obj);
				} else {
					obj.quesId = ++idx;
					aData.push(obj);
				}
			}.bind(this));
			this.byId("dConfig").setModel(new JSONModel(aData), "ConfigQuestion");
		},

		createContentInward: function (sId, oContext) {
			var oUIControl = new sap.m.VBox().addStyleClass("sapUiSmallMarginBottom"),
				obj = oContext.getObject();

			oUIControl.addItem(new sap.m.HBox({
				items: [
					new sap.m.Label({
						text: "{ConfigQuestion>quesId}.",
						textAlign: "End",
						width: "3rem"
					}).addStyleClass("feedbackFont sapUiTinyMarginEnd"),
					new sap.m.Label({
						text: "{ConfigQuestion>ques}",
						wrapping: true
					}).addStyleClass("feedbackFont")
				]
			}));
			var oHBox = new sap.m.HBox({
				items: [
					new sap.m.Label({
						text: "Ans:",
						design: "Bold",
						textAlign: "End",
						width: "5.5rem"
					}).addStyleClass("feedbackFont")
				]
			}).addStyleClass("sapUiTinyMarginTop");

			switch (oContext.getProperty("keyType")) {
			case "R":
				oHBox.addItem(new sap.m.Label({
					text: "{ConfigQuestion>answer/answerDesc}"
				}).addStyleClass("feedbackFont sapUiTinyMarginBegin"));
				break;
			case "T":
				oHBox.addItem(new sap.m.Label({
					text: "{ConfigQuestion>answer}"
				}).addStyleClass("feedbackFont sapUiTinyMarginBegin"));
				break;
			}
			oUIControl.addItem(oHBox);
			if (obj.items.length && obj.answer && 'answerDesc' in obj.answer && obj.answer.answerDesc.toLowerCase() === "yes") {
				oUIControl.addItem(this._createSubConfigQues());
			}
			return oUIControl;
		},

		_createSubConfigQues: function () {
			var oVBox = new sap.m.VBox();
			oVBox.bindAggregation("items", {
				path: "ConfigQuestion>items",
				factory: function (sId, oContext) {
					var oVBox = new sap.m.VBox(),
						oHBox = new sap.m.HBox({
							items: [
								new sap.m.Label({
									text: "{ConfigQuestion>quesId}.",
									textAlign: "End",
									width: "3.5rem"
								}).addStyleClass("feedbackFont sapUiMediumMarginBegin sapUiTinyMarginEnd"),
								new sap.m.Label({
									text: "{ConfigQuestion>ques}",
									wrapping: true
								}).addStyleClass("feedbackFont")
							]
						}).addStyleClass("sapUiTinyMarginTop"),
						oBoxAns = new sap.m.HBox({
							items: [
								new sap.m.Label({
									text: "Ans:",
									design: "Bold",
									textAlign: "End",
									width: "6rem"
								}).addStyleClass("feedbackFont sapUiMediumMarginBegin sapUiTinyMarginEnd"),
								new sap.m.Text({
									text: "{ConfigQuestion>answer/answerDesc}"
								}).addStyleClass("feedbackFont")
							]
						}).addStyleClass("sapUiTinyMarginTop");
					oVBox.addItem(oHBox);
					oVBox.addItem(oBoxAns);
					return oVBox;
				}
			});
			return oVBox;
		},

		onPressGstinValidator: function (oEvt) {
			var text = oEvt.getSource().getText(),
				oRoot = this.getOwnerComponent().getRootControl(),
				vRoute = "bOthers",
				aBtn = [
					"bHome", "bNewStatus", "bNewManage", "bLedger", "bEWB", "bAPICallDB",
					"APModule", "mbReturns", "mbReports", "bOthers", "mbSACDashboard"
				];

			switch (text) {
			case "Auth Token":
				var oSelectedKey = "ManageAuthToken";
				break;
			case "GSTIN Validator":
				oSelectedKey = "GSTINValidator";
				break;
			case "Return Status":
				oSelectedKey = "VenRetFilngStatus";
				break;
				vRoute = null;
			case "Common Credit Reversal":
				oSelectedKey = "Creditreversal";
				break;
			case "TDS/TCS Credit":
				oSelectedKey = "TDSTCS";
				break;
			case "Non Compliant Vendors":
				oSelectedKey = "NonCompliantVendors";
				break;
			case "GL Recon":
				oSelectedKey = "GLRecon";
				break;
			case "Manage NIC/IRP Credentials":
				oSelectedKey = "ManageNIC";
				break;
			}

			if (text === "E-Invoice vs GSTR-1") {
				vRoute = "mbReturns";
				this.getRouter().navTo("Returns", {
					contextPath: "GSTR1",
					key: "gstr1PvsI"
				});
			} else {
				this.getRouter().navTo("Others", {
					contextPath: oSelectedKey
				});
			}
			if (vRoute) {
				aBtn.forEach(function (btn) {
					oRoot.byId(btn).removeStyleClass("HomeCSS");
				}.bind(this));
				oRoot.byId(vRoute).addStyleClass("HomeCSS");
			}
		},

		onpressRetStatusItems: function (oEvt) {
			var text = oEvt.getSource().getText();
			$.sap.Date = this.byId("iDateold").getDateValue();
			var oSelectedKey;
			if (text === "GSTR-1") {
				oSelectedKey = "GSTR1";
			} else if (text === "GSTR-1A") {
				oSelectedKey = "GSTR1A";
			} else if (text === "GSTR-3B") {
				oSelectedKey = "GSTR3B";
			} else if (text === "GSTR-6") {
				oSelectedKey = "GSTR6";
			} else if (text === "GSTR-9") {
				oSelectedKey = "GSTR9";
			} else if (text === "GSTR-7") {
				oSelectedKey = "GSTR7";
			} else if (text === "ITC-04" + " " + "(" + this.getView().getModel("HomeOldFilterData1").getData().yearDivison + ")") {
				oSelectedKey = "ITC04";
			} else {
				return;
			}
			this.getRouter().navTo("Returns", {
				contextPath: oSelectedKey,
				key: "RS"
			});
		},

		handleLinkPressGSTINMain: function (oEvent) {
			var oValue1 = oEvent.getSource().getBindingContext("HomeOldTableData").getObject().regType;
			$.sap.DashGSTIN = oEvent.getSource().getText();
			$.sap.Date = this.byId("iDateold").getDateValue();
			var oSelectedKey;
			if (oValue1 === "REGULAR") {
				oSelectedKey = "GSTR1";
			} else if (oValue1 === "ISD") {
				oSelectedKey = "GSTR6";
			} else {
				return;
			}
			this.getRouter().navTo("Returns", {
				contextPath: oSelectedKey,
				key: "DashBoard"
			});
		},

		_summarySearchInfo: function (vGstin) {
			var oSearchInfo = {
				"req": {
					"entityId": [$.sap.entityID],
					"taxPeriod": this._formatPeriod(this.byId("dtSummary").getDateValue()),
					"dataSecAttrs": {
						"GSTIN": []
					}
				}
			};
			if (vGstin) {
				oSearchInfo.req.dataSecAttrs.GSTIN.push(vGstin);
			} else {
				oSearchInfo.req.dataSecAttrs.GSTIN = this.byId("slSummaryGstin").getSelectedKeys();
			}
			return oSearchInfo;
		},

		onSearch: function () {
			var searchInfo = this._summarySearchInfo();
			if (this.byId("dAdapt")) {
				this._getOtherFilters(searchInfo.req.dataSecAttrs, "dpGstr1Summary");
			}
			this._getProcessSummary(searchInfo);
		},

		/**
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 * @param {Object} searchInfo Search Payload get data from tables
		 */
		_getProcessSummary: function (searchInfo) {
			$.ajax({
					method: "POST",
					url: "/aspsapapi/gstr1SummaryScreen.do",
					data: JSON.stringify(searchInfo),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this._bindSummaryData(data);
					this.byId("dpGstr1Summary").setModel(new JSONModel(data.resp), "ProcessSummary");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
			this._approvalStatus();
		},

		_bindSummaryData: function (data) {
			var oGstrSummary = {
				"ASP": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				},
				"GSTN": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				},
				"DIFF": {
					"totalTax": 0,
					"igst": 0,
					"cgst": 0,
					"sgst": 0,
					"cess": 0
				}
			};
			if (data.resp.outward.length > 0) {
				for (var i = 0; i < data.resp.outward.length; i++) {
					oGstrSummary.ASP.totalTax += data.resp.outward[i].aspTaxPayble;
					oGstrSummary.ASP.igst += data.resp.outward[i].aspIgst;
					oGstrSummary.ASP.cgst += data.resp.outward[i].aspCgst;
					oGstrSummary.ASP.sgst += data.resp.outward[i].aspSgst;
					oGstrSummary.ASP.cess += data.resp.outward[i].aspCess;
					oGstrSummary.GSTN.totalTax += data.resp.outward[i].gstnTaxPayble;
					oGstrSummary.GSTN.igst += data.resp.outward[i].gstnIgst;
					oGstrSummary.GSTN.cgst += data.resp.outward[i].gstnCgst;
					oGstrSummary.GSTN.sgst += data.resp.outward[i].gstnSgst;
					oGstrSummary.GSTN.cess += data.resp.outward[i].gstnCess;
					oGstrSummary.DIFF.totalTax += data.resp.outward[i].diffTaxPayble;
					oGstrSummary.DIFF.igst += data.resp.outward[i].diffIgst;
					oGstrSummary.DIFF.cgst += data.resp.outward[i].diffCgst;
					oGstrSummary.DIFF.sgst += data.resp.outward[i].diffSgst;
					oGstrSummary.DIFF.cess += data.resp.outward[i].diffCess;
				}
			}
			this.byId("id_BlockLayoutgstr1").setModel(new JSONModel(oGstrSummary), "gstr1Summary");
		},

		onPressBack: function () {
			this.byId("dashVBoxId").setVisible(true);
			this.byId("dpGstr1Summary").setVisible(false);
		},

		/**
		 * Called to get Status for Appraval Request
		 * Developed by: Bharat Gupta on 13.12.2019
		 * @memberOf com.ey.digigst.view.GSTR1
		 * @private
		 */
		_approvalStatus: function () {
			var oPayload = {
				"req": {
					"gstin": this.byId("slSummaryGstin").getSelectedKeys()[0],
					"returnPeriod": this.byId("dtSummary").getValue()
				}
			};
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getApprovalStatus.do",
					data: JSON.stringify(oPayload),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp === undefined) {
						this.byId("txtReqSent").setText("");
						this.byId("txtStatus").setText("");
					} else {
						switch (data.resp.status) {
						case 0:
							var vStatus = "Approval Pending",
								styleCls = "statsPending";
							break;
						case 1:
							vStatus = "Approved";
							styleCls = "statsApprove";
							break;
						case 2:
							vStatus = "Rejected";
							styleCls = "statsReject";
							break;
						}
						var arrInitiateDt = data.resp.initiatedOn.split("T");
						this.byId("txtStatus").removeStyleClass("statsPending");
						this.byId("txtStatus").removeStyleClass("statsApprove");
						this.byId("txtStatus").removeStyleClass("statsReject");
						this.byId("txtStatus").setText(vStatus);
						if (data.resp.approvedOn) {
							arrInitiateDt = data.resp.approvedOn.split("T");
							this.byId("txtReqSent").setText(": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
						} else {
							arrInitiateDt = data.resp.initiatedOn.split("T");
							this.byId("txtReqSent").setText(": " + Formatter.dateFormat(arrInitiateDt[0]) + " " + arrInitiateDt[1].substr(0, 8));
						}
						this.byId("txtStatus").addStyleClass(styleCls);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		FilterApply: function (reqHomeInfo) {
			var outSupTot = 0,
				outTaxTot = 0,
				inwSupTot = 0,
				taxAmtTot = 0,
				avbCredTot = 0,
				cashLedTot = 0,
				credTot = 0,
				libLedTot = 0;
			var oHomePath = "/aspsapapi/getDasboardDetails.do";
			this.homeModel = new JSONModel();
			var that = this;
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oHomePath,
					contentType: "application/json",
					data: JSON.stringify(reqHomeInfo)
				}).done(function (data, status, jqXHR) {
					if (data.resp !== undefined) {
						for (var j = 0; j < data.resp.supplyStatus.length; j++) {
							if (data.resp.supplyStatus[j].outSup !== undefined) {
								outSupTot += Number(data.resp.supplyStatus[j].outSup);
							}
							if (data.resp.supplyStatus[j].outTax !== undefined) {
								outTaxTot += Number(data.resp.supplyStatus[j].outTax);
							}
							if (data.resp.supplyStatus[j].inwSup !== undefined) {
								inwSupTot += Number(data.resp.supplyStatus[j].inwSup);
							}

							if (data.resp.supplyStatus[j].taxAmt !== undefined) {
								taxAmtTot += Number(data.resp.supplyStatus[j].taxAmt);
							}
							if (data.resp.supplyStatus[j].avbCred !== undefined) {
								avbCredTot += Number(data.resp.supplyStatus[j].avbCred);
							}
							if (data.resp.supplyStatus[j].cashLed !== undefined) {
								cashLedTot += Number(data.resp.supplyStatus[j].cashLed);
							}
							if (data.resp.supplyStatus[j].crdLed !== undefined) {
								credTot += Number(data.resp.supplyStatus[j].crdLed);
							}
							if (data.resp.supplyStatus[j].libLed !== undefined) {
								libLedTot += Number(data.resp.supplyStatus[j].libLed);
							}
						}

						data.resp.supplyStatus.push({
							"gstin": "",
							"outSup": outSupTot,
							"outTax": outTaxTot,
							"inwSup": inwSupTot,
							"taxAmt": taxAmtTot,
							"avbCred": avbCredTot,
							"cashLed": cashLedTot,
							"crdLed": credTot,
							"libLed": libLedTot
						});
					}
					that.homeModel.setData(data.resp);
					that.getView().setModel(that.homeModel, "HomeFilterData");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressClear: function (oEvent) {
			var vDate = new Date();
			vDate.setDate(vDate.getDate() - 9);

			if (this.byId("dAdapt")) {
				this.getView().byId("idDateRange").setValue("");
				this.getView().byId("slProfitCtr").setSelectedKeys([]);
				this.getView().byId("slPlant").setSelectedKeys([]);
				this.getView().byId("slDivision").setSelectedKeys([]);
				this.getView().byId("slLocation").setSelectedKeys([]);
				this.getView().byId("slSalesOrg").setSelectedKeys([]);
				this.getView().byId("slPurcOrg").setSelectedKeys([]);
				this.getView().byId("slDistrChannel").setSelectedKeys([]);
				this.getView().byId("slUserAccess1").setSelectedKeys([]);
				this.getView().byId("slUserAccess2").setSelectedKeys([]);
				this.getView().byId("slUserAccess3").setSelectedKeys([]);
				this.getView().byId("slUserAccess4").setSelectedKeys([]);
				this.getView().byId("slUserAccess5").setSelectedKeys([]);
				this.getView().byId("slUserAccess6").setSelectedKeys([]);
			}
			this.byId("slSummaryGstin").setSelectedKeys(this.obj);
			this.byId("dtSummary").setDateValue(vDate);
			this.onSearch();
		},

		onNewPressGO: function () {
			this.returnStatusBind();
			this.returnStatusPeriodBind();
		},

		returnStatusPeriodBind: function () {
			var data = {
				"year": null,
				"quater": null
			};
			var vMonth = this.byId("iDateold").getValue().slice(0, 2),
				vYear = this.byId("iDateold").getValue().slice(2, 6);

			if (["01", "02", "03"].includes(vMonth)) {
				var vYear1 = +vYear - 1;
				data.year = (vYear1 + "-" + +vYear);
				data.quater = "Q4";
			} else {
				var vYear1 = +this.byId("iDateold").getValue().slice(4, 6) + 1;
				data.year = (+vYear + "-" + vYear1);
				if (["04", "05", "06"].includes(vMonth)) {
					data.quater = "Q1";
				} else if (["07", "08", "09"].includes(vMonth)) {
					data.quater = "Q2";
				} else {
					data.quater = "Q3";
				}
			}
			this.getView().setModel(new sap.ui.model.json.JSONModel(data), "Period");
		},

		returnStatusBind: function () {
			this.byId("demoEntiyId").setText($.sap.entityName);
			var oPayload = {
				"req": {
					"entityId": $.sap.entityID,
					"taxPeriod": this.byId("iDateold").getValue()
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this.on2A2B(),
					this._getOldReturnComplianceStatus(oPayload),
					this._getOldReturnStatus(oPayload),
					this._getOldOutwardStatusDetails(oPayload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getOldReturnComplianceStatus: function (oPayload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getDashBoardHomeOldReturnComplianceStatus.do",
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					}).done(function (data, status, jqXHR) {
						if (data.resp !== undefined && data.resp.det.supplyDetailsDto.length !== 0) {
							var oData = data.resp.det.supplyDetailsDto,
								obj = {
									"Total": "",
									//"supplierGstin": "",
									"outwardSupply": 0,
									"totalTax": 0,
									"iGST": 0,
									"cGST": 0,
									"sGST": 0,
									"cESS": 0,
									"cashLedger": 0,
									"creditLedger": 0,
									"liabilityLedger": 0
								};
							oData.forEach(function (e) {
								e.Total = "1";
								if (e.outwardSupply) {
									obj.outwardSupply += +e.outwardSupply;
								}
								if (e.totalTax) {
									obj.totalTax += +e.totalTax;
								}
								if (e.iGST) {
									obj.iGST += +e.iGST;
								}
								if (e.cGST) {
									obj.cGST += +e.cGST;
								}
								if (e.sGST) {
									obj.sGST += +e.sGST;
								}
								if (e.cESS) {
									obj.cESS += +e.cESS;
								}
								if (e.cashLedger) {
									obj.cashLedger += +e.cashLedger;
								}
								if (e.creditLedger) {
									obj.creditLedger += +e.creditLedger;
								}
								if (e.liabilityLedger) {
									obj.liabilityLedger += +e.liabilityLedger;
								}
							});
							oData.push(obj);
						}
						this.getView().setModel(new JSONModel(data.resp.det), "HomeOldTableData");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getOldReturnStatus: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getDashBoardHomeOldReturnStatus.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						var oData = $.extend(true, {}, data.resp.det);
						this.getView().setModel(new JSONModel(oData), "HomeOldFilterData");
						this.getView().setModel(new JSONModel(data.resp), "HomeOldFilterData1");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getOldOutwardStatusDetails: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getDashBoardHomeOldOutwardStatusDetails.do",
						data: JSON.stringify(payload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						data.enabled = ["COMPLETED", "FAILED"].includes(data.latestStatus);
						data.tooltip = ["COMPLETED", "FAILED"].includes(data.latestStatus) ? "Refresh" : data.latestStatus;
						this.getView().setModel(new JSONModel(data), "HomeOldOutwardStatusDetails");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onButtonPress1: function (oEvt) {
			if (oEvt.getSource().getState() === false) {
				this.byId("vfAnx1old11").setVisible(true);
				this.byId("vfAnx1old1").setVisible(false);
				//this.byId("bChartAnx1old1").setVisible(true);
				//this.byId("bTableAnx1old1").setVisible(false);
				this.byId("vfAnx1old112").setVisible(true);
				this.byId("idOutwardTotal").setVisible(false);
			} else {
				this.byId("vfAnx1old11").setVisible(false);
				this.byId("vfAnx1old1").setVisible(true);
				//this.byId("bChartAnx1old1").setVisible(false);
				//this.byId("bTableAnx1old1").setVisible(true);
				this.byId("vfAnx1old112").setVisible(false);
				this.byId("idOutwardTotal").setVisible(true);
			}
		},

		on2A2B: function (oEvt) {
			var vState = this.byId("id2a2b").getState(),
				oModel = this.getView().getModel("Display"),
				flag = !!oEvt,
				oPayload = {
					"req": {
						"entityId": $.sap.entityID,
						"taxPeriod": this.byId("iDateold").getValue()
					}
				};
			var vUrl = (!vState ? "/aspsapapi/getDashBoardHomeOldReconSummary.do" : "/aspsapapi/getDashBoardHomeReconSummary2bpr.do");
			oModel.setProperty("/Label", (!vState ? "2A" : "2B"));
			oModel.refresh(true);
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: vUrl,
						data: JSON.stringify(oPayload),
						contentType: "application/json"
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp.det), "HomeOldReconSummary");
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
							return;
						}
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
							return;
						}
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onPrsGOHome: function () {
			this.onReconDetails();
			var oHomeEnty = $.sap.entityID;
			var oHomeRetPerd = this.byId("iDate").getValue();

			var Date, profitCentres, plants, divisions, locations, salesOrgs, distributionChannels, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6;
			if (this.byId("idDateRange") !== undefined) {
				//Date = this.byId("idDateRange").getDateValue();
				profitCentres = this.byId("slProfitCtr").getSelectedKeys();
				plants = this.byId("slPlant").getSelectedKeys();
				divisions = this.byId("slDivision").getSelectedKeys();
				locations = this.byId("slLocation").getSelectedKeys();
				salesOrgs = this.byId("slSalesOrg").getSelectedKeys();
				distributionChannels = this.byId("slDistrChannel").getSelectedKeys();
				userAccess1 = this.byId("slUserAccess1").getSelectedKeys();
				userAccess2 = this.byId("slUserAccess2").getSelectedKeys();
				userAccess3 = this.byId("slUserAccess3").getSelectedKeys();
				userAccess4 = this.byId("slUserAccess4").getSelectedKeys();
				userAccess5 = this.byId("slUserAccess5").getSelectedKeys();
				userAccess6 = this.byId("slUserAccess6").getSelectedKeys();
			} else {
				//Date = null;
				profitCentres = [];
				plants = [];
				divisions = [];
				locations = [];
				salesOrgs = [];
				distributionChannels = [];
				userAccess1 = [];
				userAccess2 = [];
				userAccess3 = [];
				userAccess4 = [];
				userAccess5 = [];
				userAccess6 = [];
			}

			var cPsearchInfo = {
				//"GSTIN": gstnarr,
				"PC": profitCentres,
				"Plants": plants,
				"D": divisions,
				"L": locations,
				"SO": salesOrgs,
				"DC": distributionChannels,
				"UD1": userAccess1,
				"UD2": userAccess2,
				"UD3": userAccess3,
				"UD4": userAccess4,
				"UD5": userAccess5,
				"UD6": userAccess6
			};

			var reqHomeInfo = {
				//	"req": {
				"entityId": oHomeEnty,
				"taxPeriod": oHomeRetPerd,
				"dataSecAttrs": cPsearchInfo
					//				};
			};
			this.FilterApply(reqHomeInfo);
		},

		onChangeMenu: function (oEvent) {
			var vKey = oEvent.getParameters().item.getKey();
			if (oEvent.getSource().getParent().getId().includes("bCompliance")) {
				var oModel = this.getView().getModel("Visi");
				switch (vKey) {
				case "G":
					var vText = "GSTIN",
						vStatusTitle = "Return Compliance Status";
					oModel.getData().gstin = true;
					oModel.getData().division = false;
					oModel.getData().plant = false;
					oModel.getData().colVisi = true;
					break;
				case "D":
					vText = "Division";
					vStatusTitle = "Status";
					oModel.getData().gstin = false;
					oModel.getData().division = true;
					oModel.getData().plant = false;
					oModel.getData().colVisi = false;
					break;
				case "P":
					vText = "Plant";
					vStatusTitle = "Status";
					oModel.getData().gstin = false;
					oModel.getData().division = false;
					oModel.getData().plant = true;
					oModel.getData().colVisi = false;
					break;
				}
				this.byId("lStatus").setText(vStatusTitle);
				this.byId("bCompliance").setText(vText);
				oModel.refresh(true);
			} else if (oEvent.getSource().getParent().getId().includes("bRupees")) {
				switch (vKey) {
				case "A":
					vText = "Rupees in Absolute";
					var aDataAb = this.getOwnerComponent().getModel("Data").getData();
					this.getView().setModel(new sap.ui.model.json.JSONModel(aDataAb), "Data");
					break;
				case "L":
					vText = "Rupees in Lakhs";
					var complain = [];
					var aData = this.getOwnerComponent().getModel("Data").getData();
					var compliance = aData.compliancelakhs;
					complain.compliance = compliance;
					this.getView().setModel(new sap.ui.model.json.JSONModel(complain), "Data");
					break;
				case "C":
					vText = "Rupees in Crores";
					var complain2 = [];
					var aDatac = this.getOwnerComponent().getModel("Data").getData();
					var compliance = aDatac.complianceCrores;
					complain2.compliance = compliance;
					this.getView().setModel(new sap.ui.model.json.JSONModel(complain2), "Data");
					break;
				case "M":
					vText = "Rupees in Millions";
					var complain3 = [];
					var aDataM = this.getOwnerComponent().getModel("Data").getData();
					var compliance = aDataM.complianceMill;
					complain3.compliance = compliance;
					this.getView().setModel(new sap.ui.model.json.JSONModel(complain3), "Data");
					break;
				case "B":
					vText = "Rupees in Billions";
					var complain4 = [];
					var aDataB = this.getOwnerComponent().getModel("Data").getData();
					var compliance = aDataB.complianceBill;
					complain4.compliance = compliance;
					this.getView().setModel(new sap.ui.model.json.JSONModel(complain4), "Data");
					break;
				}
				this.byId("bRupees").setText(vText);
			}
		},

		onPrsHomeAdaptFilter: function (oEvt) {
			if (!this._oDialogANX1Filter) {
				this._oDialogANX1Filter = sap.ui.xmlfragment("com.ey.digigst.fragments.anx1.AdaptAnx1", this);
				this.getView().addDependent(this._oDialogANX1Filter);
			}
			var oModel = this.byId("idHomeAdaptFiltr").getModel("DataSecurity");
			sap.ui.getCore().byId("dAdapt").setModel(oModel, "DataSecurity");
			this._oDialogANX1Filter.open();
		},

		onPressFilterClose: function (oEvt) {
			var homeFiltr = oEvt.getSource().getText();
			if (homeFiltr === "Cancel") {
				this._oDialogANX1Filter.close();
				return;
			} else if (homeFiltr === "Apply") {
				this.onPrsGOHome();
				this._oDialogANX1Filter.close();
			}
		},

		onReconDetails: function () {
			var Request = {
				"entityId": $.sap.entityID,
				"taxPeriod": this.byId("iDate").getValue()
			};
			this.onReconDetails1(Request);
		},

		onReconDetails1: function (Request) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getDashboardReconDetails.do",
					data: JSON.stringify(Request),
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel(data.resp.det), "ReconDetails");
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onPressGstr1GetDetails: function () {
			if (!this._getGstr1Detail) {
				this._getGstr1Detail = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.GetGstr1Details", this);
				this.getView().addDependent(this._getGstr1Detail);
			}
			this.byId("dGetDetails").setModel(new JSONModel(this._getGstr1DetailCheckStats(true)), "CheckStats");
			this._getGstr1Detail.open();
		},

		_getGstr1DetailCheckStats: function (flag) {
			return {
				"b2b": flag,
				"b2ba": flag,
				"b2cl": flag,
				"b2cla": flag,
				"b2cs": flag,
				"b2csa": flag,
				"cdnr": flag,
				"cdnra": flag,
				"cdnur": flag,
				"cdnura": flag,
				"exp": flag,
				"expa": flag,
				"nilNon": flag,
				"hsn": flag,
				"docSeries": flag,
				"atTxpd": flag,
				"ataTxpda": flag,
				"select": flag,
				"partial": false
			};
		},

		onCloseGetDetails: function (action) {
			this._getGstr1Detail.close();
			if (action === "ok") {}
		},

		onPressErrorCorrection: function (oEvent) {
			var oFilterData = {
				"dataOriginType": "A",
				"dataType": "gstr1",
				"criteria": "RETURN_DATE_SEARCH",
				"status": "E",
				"validation": "BV"
			};

			if (oEvent.getSource().getId().includes("bErrorCorr")) {
				oFilterData.segment = oEvent.getParameter("item").getKey();
				// oFilterData.entity = this.byId("slEntity").getSelectedKey();
				oFilterData.gstin = this.byId("slGstin").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("dtProcessed").getDateValue();
				if (this.byId("dAdapt")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpProcessRecord");
				}
			} else {
				oFilterData.segment = oEvent.getParameter("item").getKey();
				// oFilterData.entity = this.byId("slSummaryEntity").getSelectedKey();
				oFilterData.gstin = this.byId("slSummaryGstin").getSelectedKeys();
				oFilterData.fromDate = oFilterData.toDate = this.byId("dtSummary").getDateValue();
				if (this.byId("dAdapt")) {
					oFilterData.dataSecAttrs = [];
					this._getOtherFilters(oFilterData.dataSecAttrs, "dpGstr1Summary");
				}
			}
			this.getOwnerComponent().setModel(new JSONModel(oFilterData), "FilterInvoice");
			this.getRouter().navTo("InvoiceManage");
		},

		_getOtherFilters: function (search, vPage) {
			var oDataSecurity = this.getOwnerComponent().getModel("userPermission").getData().respData.dataSecurity.items;

			if (oDataSecurity.profitCenter) {
				search.PC = this.byId("slProfitCtr").getSelectedKeys();
			}
			if (oDataSecurity.plant) {
				search.Plant = this.byId("slPlant").getSelectedKeys();
			}
			if (oDataSecurity.division) {
				search.D = this.byId("slDivision").getSelectedKeys();
			}
			if (oDataSecurity.location) {
				search.L = this.byId("slLocation").getSelectedKeys();
			}
			if (oDataSecurity.purchOrg) {
				search.PO = this.byId("slPurcOrg").getSelectedKeys();
			}
			if (oDataSecurity.salesOrg) {
				search.SO = this.byId("slSalesOrg").getSelectedKeys();
			}
			if (oDataSecurity.distChannel) {
				search.DC = this.byId("slDistrChannel").getSelectedKeys();
			}
			if (oDataSecurity.userAccess1) {
				search.UD1 = this.byId("slUserAccess1").getSelectedKeys();
			}
			if (oDataSecurity.userAccess2) {
				search.UD2 = this.byId("slUserAccess2").getSelectedKeys();
			}
			if (oDataSecurity.userAccess3) {
				search.UD3 = this.byId("slUserAccess3").getSelectedKeys();
			}
			if (oDataSecurity.userAccess4) {
				search.UD4 = this.byId("slUserAccess4").getSelectedKeys();
			}
			if (oDataSecurity.userAccess5) {
				search.UD5 = this.byId("slUserAccess5").getSelectedKeys();
			}
			if (oDataSecurity.userAccess6) {
				search.UD6 = this.byId("slUserAccess6").getSelectedKeys();
			}
			return;
		},

		onpressMasters: function () {
			var oRoot = this.getOwnerComponent().getRootControl(),
				aBtn = [
					"bHome", "bNewStatus", "bNewManage", "bLedger", "bEWB", "bAPICallDB",
					"APModule", "mbReturns", "mbReports", "bOthers", "mbSACDashboard"
				];
			aBtn.forEach(function (btn) {
				oRoot.byId(btn).removeStyleClass("HomeCSS");
			}.bind(this));
			this.getRouter().navTo("Masters");
		},

		onpressTS: function () {
			this.getRouter().navTo("TaskInbox");
		},

		onRetComplFullScreen: function (type) {
			var oPropModel = this.getView().getModel("DashProperty");
			oPropModel.setProperty("/fullScreen", type === "O");
			oPropModel.setProperty("/tabSize", (type === "O" ? 19 : 8));
			oPropModel.refresh(true);
		}
	});
});