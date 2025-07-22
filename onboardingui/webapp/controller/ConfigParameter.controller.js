sap.ui.define([
	'com/ey/onboarding/controller/BaseController',
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	'sap/m/MessageBox',
	'sap/m/Token',
	'sap/ui/export/Spreadsheet',
	"sap/m/Dialog",
	"sap/m/Button",
	"sap/m/List",
	"sap/m/StandardListItem",
], function (BaseController, Controller, JSONModel, MessageBox, Token, Spreadsheet, Dialog, Button, List, StandardListItem) {
	"use strict";

	return BaseController.extend("com.ey.onboarding.controller.ConfigParameter", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.onboarding.view.ConfigParameter
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("ConfigParameter").attachPatternMatched(this._onRouteMatched, this);
			this.getView().setModel(new JSONModel({
				"Q31": false,
				"OQ31": false,
				"Q51": false
			}), "Display");
		},

		_onRouteMatched: function (oEvent) {
			if (oEvent.getParameter("name") == "ConfigParameter") {
				this.getELRegistration();
				this._bindProperty();
				this.defultBind();
			}
		},

		_bindProperty: function () {
			this.getView().setModel(new JSONModel({
				"subGeneral": false,
				"subEntity": false,
				"subGroup": false
			}), "Property");
		},

		handleIconTabBarSelect: function (oEvent) {
			var key = oEvent.getParameters().selectedKey;
			switch (key) {
			case "General":
				this.byId("idEntityGeneral").setSelectedKey($.sap.entityId);
				this.getGeneral(true);
				break;
			case "InwardOutward":
				this.byId("idEntityInOut").setSelectedKey($.sap.entityId);
				sap.ui.core.BusyIndicator.show(0);
				Promise.all([
						this.getInward(),
						this.getOutward()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					})
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error(err, {
							styleClass: "sapUiSizeCompact"
						});
					});
				break;
			case "GroupLevel":
				this._getGroupLevelQuestion();
				break;
			case "DMSConfig":
				this.getDMSData();
				break;
			default:
				this.defultBind();
			}
		},

		getELRegistration: function (oEvent) {
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode
				}
			};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/getElRegistration.do",
					contentType: "application/json",
					data: JSON.stringify(postData)
				})
				.done(function (data, status, jqXHR) {
					data.resp = _.sortBy(data.resp, "entityId");
					var oEntity = new JSONModel(data);
					oEntity.setSizeLimit(data.resp.length);
					this.getView().setModel(oEntity, "entity");
					if (!$.sap.entityId) {
						$.sap.entityId = data.resp[0].entityId;
					}
					this.getView().byId("idEntityGeneral").setSelectedKey($.sap.entityId);
					Promise.all([
							this.getGeneral(),
							this.getInward(),
							this.getOutward()
						])
						.then(function (values) {
							sap.ui.core.BusyIndicator.hide();
						})
						.catch(function (err) {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error(err, {
								styleClass: "sapUiSizeCompact"
							});
						});
					this.defultBind();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("getElRegistration", {
						styleClass: "sapUiSizeCompact"
					});
				}.bind(this));
		},

		onSelectionChange: function (oEvent) {
			var vId = oEvent.getSource().getId();
			if (vId.includes("idEntityGeneral")) {
				$.sap.entityId = this.byId("idEntityGeneral").getSelectedKey();
				this.getGeneral(true);

			} else if (vId.includes("idEntityInOut")) {
				$.sap.entityId = this.byId("idEntityInOut").getSelectedKey();
				Promise.all([
						this.getInward(),
						this.getOutward()
					])
					.then(function (values) {
						sap.ui.core.BusyIndicator.hide();
					})
					.catch(function (err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error(err, {
							styleClass: "sapUiSizeCompact"
						});
					});
			} else {
				this.defultBind();
			}
		},

		getInward: function (oEvent) {
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode,
					"entityId": $.sap.entityId,
					"type": "inward"
				}
			};
			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getConfigPrmt.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						this.bindInward(data);
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						var oErr = JSON.parse(jqXHR.responseText);
						this.getView().setModel(new JSONModel(null), "Inward");
						reject(jqXHR.status + " : " + oErr.hdr.message);
					}.bind(this));
			}.bind(this));
		},

		bindInward: function (data) {
			var oPropModel = this.getView().getModel("Property");
			var oView = this.getView();
			var oCount = 0;
			var oCount1 = 97;
			var oCountI50 = 96
			var oFlag = false;
			var oTabData = [];
			var oData = {
				"resp": []
			};
			oPropModel.setProperty("/subEntity", false);
			oPropModel.refresh(true);
			for (var i = 0; i < data.resp.length; i++) {
				if (data.resp[i].ques === "Select Timestamp?") {
					if (data.resp[i].get2AHour === "" || data.resp[i].get2AHour === undefined) {
						data.resp[i].get2AHour = "03";
					}
					if (data.resp[i].get2Amin === "" || data.resp[i].get2Amin === undefined) {
						data.resp[i].get2Amin = "30";
					}
				}
				if (data.resp[i].keyType === "TB" && data.resp[i].quesCode === "I14") {
					if (data.resp[i].answerDesc === undefined) {
						data.resp[i].answerDesc = "";
					}
					data.resp[i].edit = false;
					oTabData.push({
						"answerDesc": data.resp[i].answerDesc,
						"reportType": data.resp[i].ques.split(" # ")[0],
						"aspSuggested": data.resp[i].ques.split(" # ")[1],
						"items": data.resp[i].items,
						"id": data.resp[i].id,
						"edit": data.resp[i].edit,
						"questId": data.resp[i].questId,
					});
					oFlag = true;
					if ((i + 1) == data.resp.length) {
						oCount += 1;
						oData.resp.push({
							"sno": oCount,
							"ques": "EY ASP Suggested Responses basis the Report Type?",
							"items": oTabData,
							"keyType": "TB",
							"quesCode": "I14"
						});
						oFlag = false;

					}
				} else if (data.resp[i].keyType === "SR") {
					if (data.resp[i].quesCode === "I26") {
						if (data.resp[i].ques === "Select GetCall Frequency?") {
							data.resp[i].sno = String.fromCharCode(97);
							data.resp[i].edit = false;
							if (data.resp[i].selected === 3) {
								this.getView().getModel("Display").getData().Q31 = true;
							} else if (data.resp[i].selected !== 3) {
								this.getView().getModel("Display").getData().Q31 = false;
							}
						}
						if (data.resp[i].ques === "Select Timestamp?") {
							data.resp[i].sno = String.fromCharCode(98);
							data.resp[i].edit = false;
						}

					} else if (data.resp[i].quesCode === "I50") {
						++oCountI50
						data.resp[i].sno = String.fromCharCode(oCountI50);
						data.resp[i].edit = false;
						if (data.resp[i].ques.includes("Select GetCall Frequency? ( Last updated timestamp")) {
							data.resp[i].questIdFlag = true
							if (data.resp[i].selected === 4) {
								this.getView().getModel("Display").getData().Q51 = true;
							} else if (data.resp[i].selected !== 4) {
								this.getView().getModel("Display").getData().Q51 = false;
							}
						} else {
							data.resp[i].questIdFlag = false
						}

					} else if (data.resp[i].quesCode === "I36") {
						if (data.resp[i].ques === "6A get call frequency?") {
							data.resp[i].sno = String.fromCharCode(97);
							data.resp[i].edit = false;
							if (data.resp[i].selected === 1) {
								this.getView().getModel("Display").getData().Q36 = true;
							} else {
								this.getView().getModel("Display").getData().Q36 = false;
							}
						}

					} else if (data.resp[i].quesCode === "I27") {
						data.resp[i].sno = String.fromCharCode(97);
						data.resp[i].edit = false;

					} else if (data.resp[i].quesCode === "I51") {
						data.resp[i].sno = String.fromCharCode(97);
						data.resp[i].edit = false;

					} else if (data.resp[i].quesCode === "I28") {
						data.resp[i].sno = String.fromCharCode(97);
						data.resp[i].edit = false;

					} else if (data.resp[i].quesCode === "I36") {
						data.resp[i].sno = String.fromCharCode(97);
						data.resp[i].edit = false;
					} else if (data.resp[i].quesCode === "I44") {
						data.resp[i].sno = String.fromCharCode(97);
						data.resp[i].edit = false;
					} else {
						data.resp[i].sno = String.fromCharCode(oCount1);
						data.resp[i].edit = false;
						oCount1++;
					}

					if (data.resp[i].answerDesc === undefined) {
						data.resp[i].answerDesc = "";

					}
					oData.resp.push(data.resp[i]);
					oFlag = false;
					this.getView().getModel("Display").refresh(true);
				} else {
					if (oFlag === true) {
						oCount += 1;
						oData.resp.push({
							"sno": oCount,
							"edit": false,
							"ques": "EY ASP Suggested Responses basis the Report Type?",
							"items": oTabData,
							"keyType": "TB",
							"quesCode": "I14"
						});
						oFlag = false;
					}
					oCount += 1;
					data.resp[i].sno = oCount;
					data.resp[i].edit = false;
					if (data.resp[i].answerDesc === undefined) {
						data.resp[i].answerDesc = "";
					}
					oData.resp.push(data.resp[i]);
				}
			}
			this.subQuestion(oData);
			var oInward = new JSONModel(oData);
			oInward.setSizeLimit(2000);
			this.getView().setModel(oInward, "Inward");
		},

		subQuestion: function (aData) {
			for (var i = 0; i < aData.resp.length; i++) {
				if (aData.resp[i].quesCode === "I18" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI18 = true;
					} else {
						var flagI18 = false;
					}
				}

				if (aData.resp[i].quesCode === "I26" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI26 = true;
					} else {
						var flagI26 = false;
					}
				}

				if (aData.resp[i].quesCode === "I50" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI50 = true;
					} else {
						var flagI50 = false;
					}
				}

				if (aData.resp[i].quesCode === "I28" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI28 = true;
					} else {
						var flagI28 = false;
					}
				}

				if (aData.resp[i].quesCode === "I27" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI27 = true;
					} else {
						var flagI27 = false;
					}
				}
				if (aData.resp[i].quesCode === "I51" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI51 = true;
					} else {
						var flagI51 = false;
					}
				}

				if (aData.resp[i].quesCode === "I34" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI36 = true;
					} else {
						var flagI36 = false;
					}
				}
				if (aData.resp[i].quesCode === "I44" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI44 = true;
					} else {
						var flagI44 = false;
					}
				}
			}

			for (var j = 0; j < aData.resp.length; j++) {
				if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I18") {
					aData.resp[j].oSRVisible = flagI18;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I26") {
					aData.resp[j].oSRVisible = flagI26;
					// if (aData.resp[j].selected === 3 && aData.resp[j].ques === "Select GetCall Frequency?") {
					// 	this.getView().getModel("Display").getData().Q31 = true;
					// 	this.getView().getModel("Display").refresh(true);
					// }
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I50") {
					aData.resp[j].oSRVisible = flagI50;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I28") {
					aData.resp[j].oSRVisible = flagI28;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I27") {
					aData.resp[j].oSRVisible = flagI27;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I36") {
					aData.resp[j].oSRVisible = flagI36;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I44") {
					aData.resp[j].oSRVisible = flagI44;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "I51") {
					aData.resp[j].oSRVisible = flagI51;
				}
			}

			var oInward = new JSONModel(aData);
			oInward.setSizeLimit(2000);
			this.getView().setModel(oInward, "Inward");
		},

		onSelectRBG: function (quesId) {
			var oModel = this.getView().getModel("General"),
				aData = oModel.getProperty("/");

			aData.resp.forEach(function (e) {
				if (e.id == quesId) {
					e.changeFlag = true;
				}
			});
			oModel.refresh(true);
		},

		onSelectRBO: function (ques, index, quesId, quesCode) {
			var aData = this.getView().getModel("Outward").getData();
			for (var i = 0; i < aData.resp.length; i++) {
				if (aData.resp[i].id == quesId) {
					aData.resp[i].changeFlag = true;
				}
			}
			if (quesCode === "O31") {
				if (index === 3) {
					this.getView().getModel("Display").getData().OQ31 = true;
				} else {
					this.getView().getModel("Display").getData().OQ31 = false;
				}
			}
			this.getView().getModel("Display").refresh(true);
			this.getView().getModel("Outward").refresh(true);
			this.subQuestionOut(aData);
		},

		onChangesOutward: function (quesID) {
			var oModel = this.getView().getModel("Outward"),
				aData = oModel.getProperty("/");

			aData.resp.forEach(function (e) {
				if (e.id == quesID) {
					e.changeFlag = true;
				}
			});
			oModel.refresh(true);
		},

		onSelectRBI: function (ques, index, quesID, quesCode) {
			var oDispModel = this.getView().getModel("Display"),
				oModel = this.getView().getModel("Inward"),
				aData = oModel.getProperty("/");
			aData.resp.forEach(function (e) {
				if (e.id == quesID) {
					e.changeFlag = true;
				}
			})
			if (quesCode === "I26") {
				oDispModel.setProperty("/Q31", (index === 3));
			}
			if (quesCode === "I50") {
				oDispModel.setProperty("/Q51", (index === 4));
			}
			if (quesCode === "I36") {
				oDispModel.setProperty("/Q36", (index === 1));
			}
			oDispModel.refresh(true);
			oModel.refresh(true);
			// var aData = this.getView().getModel("Inward").getData();
			this.subQuestion(aData);
		},

		onChangesInward: function (oEvent) {
			var oContext = oEvent.getSource().getBindingContext("Inward"),
				aData = oContext.getModel().getProperty("/resp"),
				obj = oContext.getObject();

			obj.changeFlag = true;
			if (obj.quesCode === "I13" && obj.keyType === "T") {
				var vTolerance = aData.find(function (e) {
					return (e.quesCode === "I13" && e.keyType === "R");
				});
				if (vTolerance.selected && !+obj.answerDesc) {
					obj.answerDesc = 1;
				}
			}
			oContext.getModel().refresh(true);
		},

		onChangesOutward: function (quesId) {
			var oModel = this.getView().getModel("Outward"),
				aData = oModel.getProperty("/resp"),
				obj = aData.find(function (e) {
					return (e.id === quesId);
				});
			if (obj) {
				obj.changeFlag = true;
			}
			oModel.refresh(true);
		},

		onChangesInwardI14: function (quesId) {
			var oModel = this.getView().getModel("Inward"),
				aData = oModel.getProperty("/resp"),
				obj = aData.find(function (e) {
					return (e.quesCode === "I14");
				});

			if (obj) {
				var oItem = obj.items.find(function (e) {
					return e.id == quesId;
				});
				if (oItem) {
					oItem.changeFlag = true;
				}
			}
			oModel.refresh(true);
		},

		onSelectRB2: function (quesCode, index, quesId) {
			if (quesCode === "Do you want DigiGST to auto compute invoice series for GSTR1 ?") {
				var aData = this.getView().getModel("Outward").getData();
				this.subQuestionOut(aData);
			} else {
				if (index === 3) {
					this.getView().getModel("Display").getData().Q31 = true;
				} else {
					this.getView().getModel("Display").getData().Q31 = false;
				}
				this.getView().getModel("Display").refresh(true);
				var aData = this.getView().getModel("Inward").getData();
				this.subQuestion(aData);
			}
		},

		getOutward: function (oEvent) {
			var that = this;
			var oView = this.getView();
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode,
					"entityId": $.sap.entityId,
					"type": "outward"
				}
			};

			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getConfigPrmt.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						// data.resp = _.sortBy(data.resp, "id");
						var count = 0
						for (var i = 0; i < data.resp.length; i++) {
							count++
							data.resp[i].sno = count;
							data.resp[i].edit = false;

							if (data.resp[i].ques.indexOf("# Note") > -1) {
								data.resp[i].note = data.resp[i].ques.split(" # ")[1];
								data.resp[i].ques = data.resp[i].ques.split(" # ")[0];
								data.resp[i].noteflag = true;
							} else {
								data.resp[i].noteflag = false;
							}

							if (data.resp[i].answerDesc === undefined) {
								data.resp[i].answerDesc = "";
							}

							if (data.resp[i].keyType === "M" || data.resp[i].keyType === "M7") {
								data.resp[i].items = {};
								if (data.resp[i].answerDesc == "") {
									data.resp[i].items.A = false;
									data.resp[i].items.B = false;
									data.resp[i].items.C = false;
									data.resp[i].items.D = false;
									data.resp[i].items.E = false;
									data.resp[i].items.F = false;
									data.resp[i].items.G = false;
								} else {
									var aAns = data.resp[i].answerDesc.split("*");
									if (aAns.indexOf("A") > -1) {
										data.resp[i].items.A = true;
									} else {
										data.resp[i].items.A = false;
									}

									if (aAns.indexOf("B") > -1) {
										data.resp[i].items.B = true;
									} else {
										data.resp[i].items.B = false;
									}

									if (aAns.indexOf("C") > -1) {
										data.resp[i].items.C = true;
									} else {
										data.resp[i].items.C = false;
									}

									if (aAns.indexOf("D") > -1) {
										data.resp[i].items.D = true;
									} else {
										data.resp[i].items.D = false;
									}
									if (aAns.indexOf("E") > -1) {
										data.resp[i].items.E = true;
									} else {
										data.resp[i].items.E = false;
									}
									if (aAns.indexOf("F") > -1) {
										data.resp[i].items.F = true;
									} else {
										data.resp[i].items.F = false;
									}
									if (aAns.indexOf("G") > -1) {
										data.resp[i].items.G = true;
									} else {
										data.resp[i].items.G = false;
									}
								}
							} else if (data.resp[i].keyType === "SR") {
								if (data.resp[i].quesCode === "O31") {
									data.resp[i].sno = String.fromCharCode(97);
									data.resp[i].edit = false
									count--;
									if (data.resp[i].selected === 3) {
										that.getView().getModel("Display").getData().OQ31 = true;
									} else {
										that.getView().getModel("Display").getData().OQ31 = false;
									}
								} else if (data.resp[i].quesCode === "O44") {
									data.resp[i].sno = String.fromCharCode(97);
									data.resp[i].edit = false;

								}
							}
						}
						that.subQuestionOut(data);
						var oOutward = new JSONModel(data);
						oOutward.setSizeLimit(2000);
						oView.setModel(oOutward, "Outward");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						var oErr = JSON.parse(jqXHR.responseText);
						oView.setModel(new JSONModel(null), "Outward");
						reject(jqXHR.status + " : " + oErr.hdr.message);
					}.bind(this));
			}.bind(this));
		},

		subQuestionOut: function (aData) {
			for (var i = 0; i < aData.resp.length; i++) {
				if (aData.resp[i].quesCode === "O31" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagI18 = true;
					} else {
						var flagI18 = false;
					}
				}
				if (aData.resp[i].quesCode === "O44" && aData.resp[i].keyType === "R") {
					if (aData.resp[i].selected === 0) {
						var flagO44 = true;
					} else {
						var flagO44 = false;
					}
				}
			}

			for (var j = 0; j < aData.resp.length; j++) {
				if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "O31") {
					aData.resp[j].oSRVisible = flagI18;
				} else if (aData.resp[j].keyType === "SR" && aData.resp[j].quesCode === "O44") {
					aData.resp[j].oSRVisible = flagO44;
				}
			}

			var oInward = new JSONModel(aData);
			oInward.setSizeLimit(2000);
			this.getView().setModel(oInward, "Outward");
		},

		OutwardRadio: function () {
			var aData = this.getView().getModel("Outward").getData();
			this.subQuestionOut(aData);
		},

		onSubmitInputOutput: function (oEvent) {
			MessageBox.information("Do you want to submit the changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "YES") {
						this.onSubmitInputOutputFinal()
					}
				}.bind(this)
			})
		},

		onSubmitInputOutputFinal: function (oEvent) {
			// var aInwardData = undefined;
			var aInwardData = this.getView().getModel("Inward").getData().resp;
			var aOutwardData = this.getView().getModel("Outward").getData().resp;
			var oEntity = this.getView().byId("idEntityInOut").getSelectedKey();
			var answerCode;
			var type = "Other"
			var postData = {
				"req": []
			};
			if (aInwardData != undefined) {
				for (var i = 0; i < aInwardData.length; i++) {
					if (aInwardData[i].changeFlag) {
						if (aInwardData[i].ques === "What will be the Taxable value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward   " + '"' +
									"What will be the Taxable value Tolerance Limit  for initiating the Reconciliation." +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What will be the IGST value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward     " + '"' +
									"What will be the IGST value Tolerance Limit  for initiating the Reconciliation." +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What will be the CGST value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward     " + '"' +
									"What will be the CGST value Tolerance Limit  for initiating the Reconciliation." +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What will be the SGST value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward      " + '"' +
									"What will be the SGST value Tolerance Limit  for initiating the Reconciliation" +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What will be the CESS value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward     " + '"' +
									"What will be the CESS value Tolerance Limit  for initiating the Reconciliation." +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What will be the TOTAL TAX value Tolerance Limit  for initiating the Reconciliation") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward    " + '"' +
									"What will be the TOTAL TAX value Tolerance Limit  for initiating the Reconciliation." +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques === "What is the maximum percentage for calculating the Permissible ITC under section 36(4)") {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward     " + '"' +
									"What is the maximum percentage for calculating the Permissible ITC under section 36(4)" +
									'"');
								return;
							}
						}
						if (aInwardData[i].ques ===
							"GSTR 3B : Tax period consideration while computing GSTR 3B for : Table 4A (2) - Import of Services and Table 4A (3) - Reverse Charge other than IMPG & IMPS"
						) {
							if (aInwardData[i].answerDesc == "") {
								MessageBox.error(
									"Please enter value for Inward      " + '"' +
									"GSTR 3B : Tax period consideration while computing GSTR 3B for : Table 4A (2) - Import of Services and Table 4A (3) - Reverse Charge other than IMPG & IMPS" +
									'"');
								return;
							}
						}
					}
					if (aInwardData[i].quesCode === "I14") {
						for (var j = 0; j < aInwardData[i].items.length; j++) {
							if (aInwardData[i].items[j].changeFlag) {
								postData.req.push({
									"groupCode": $.sap.groupCode,
									"entityId": oEntity,
									"id": aInwardData[i].items[j].id,
									"questId": aInwardData[i].items[j].questId,
									"keyType": aInwardData[i].keyType,
									"answerCode": aInwardData[i].items[j].answerDesc
								});
							}
						}

					} else if (aInwardData[i].ques === "Select GetCall Frequency?" && aInwardData[i].quesCode === "I26") {
						if (aInwardData[i].selected === 3) {
							var k = i - 1;
							if (aInwardData[k].selected === 0) {
								if (aInwardData[i].answerDesc == "") {
									MessageBox.error("Please enter value for Inward   " + '"' + "Select GetCall Frequency?" + '"');
									return;
								}
							}
							var res = String.fromCharCode(aInwardData[i].selected + 65) + "*" + aInwardData[i].answerDesc;
						} else {
							if (aInwardData[i].answerDesc == "") {
								aInwardData[i].answerDesc = "0"
							}
							var res = String.fromCharCode(aInwardData[i].selected + 65);
						}

						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					} else if (aInwardData[i].ques.includes("Select GetCall Frequency? ( Last updated timestamp") && aInwardData[i].quesCode ===
						"I50") {
						if (aInwardData[i].selected === 4) {
							var k = i - 1;
							if (aInwardData[k].selected === 0) {
								if (aInwardData[i].answerDesc == "") {
									MessageBox.error("Please enter value for Inward   " + '"' + "Select GetCall Frequency?" + '"');
									return;
								}
							}
							var res = String.fromCharCode(aInwardData[i].selected + 65) + "*" + aInwardData[i].answerDesc;
						} else {
							if (aInwardData[i].answerDesc == "") {
								aInwardData[i].answerDesc = "0"
							}
							var res = String.fromCharCode(aInwardData[i].selected + 65);
						}
						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					} else if (aInwardData[i].ques === "Select Timestamp?") {
						if (aInwardData[i].answerDesc == "") {
							aInwardData[i].answerDesc = "0"
						}

						var res = aInwardData[i].get2AHour + ":" + aInwardData[i].get2Amin;
						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					} else if (aInwardData[i].ques === "6A get call frequency?") {
						if (aInwardData[i].answerDesc == '\x00') {
							aInwardData[i].answerDesc = "";
						} else if (aInwardData[i].answerDesc == " ") {
							aInwardData[i].answerDesc = ""
						}
						var k = i - 1;
						if (aInwardData[k].selected === 0) {
							if (aInwardData[i].selected == 1 && aInwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Inward   " + '"' + "6A get call frequency?" + '"');
								return;
							}
						}
						if (aInwardData[i].selected == 1) {
							var res = String.fromCharCode(aInwardData[i].selected + 65) + "*" + aInwardData[i].answerDesc;
						} else {
							var res = String.fromCharCode(aInwardData[i].selected + 65);
						}

						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					} else if (aInwardData[i].keyType === "RT") {
						if (aInwardData[i].answerDesc == "") {
							aInwardData[i].answerDesc = "0"
						}
						var res = String.fromCharCode(aInwardData[i].selected + 65) + "*" + aInwardData[i].answerDesc;
						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					} else if (aInwardData[i].ques === "Set percentage for Doc no match in logical match recon report type") {
						if (aInwardData[i].changeFlag) {
							var Desc = aInwardData[i].answerDesc;
							if (Desc !== "") {
								var reg = /^[0-9]+$/;
								if (!Desc.toString().match(reg)) {
									MessageBox.error("Nagative or Decimal values are not allowed for Inward   " + '"' +
										"Set percentage for Doc no match in logical match recon report type" + '"');
									return;
								}
								if (Desc.length > 2) {
									MessageBox.error("Data should only be between 1-99 for Inward   " + '"' +
										"Set percentage for Doc no match in logical match recon report type" + '"');
									return;
								}
								if (Desc === "0" || Desc === "00") {
									MessageBox.error("Data should only be between 1-99 for Inward   " + '"' +
										"Set percentage for Doc no match in logical match recon report type" + '"');
									return;
								}
							} else {
								MessageBox.error("Please enter the value for Inward   " + '"' +
									"Set percentage for Doc no match in logical match recon report type" + '"');
								return;
							}

							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": aInwardData[i].answerDesc
							});
						}

					} else {
						if (aInwardData[i].answerDesc != "") {
							var res = aInwardData[i].answerDesc;
						} else {
							var res = String.fromCharCode(aInwardData[i].selected + 65);
							if (res == '\x00') {
								res = "";
							}
						}
						if (aInwardData[i].changeFlag) {
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aInwardData[i].id,
								"questId": aInwardData[i].questId,
								"keyType": aInwardData[i].keyType,
								"answerCode": res
							});
						}
					}
				}
			}

			if (aOutwardData != undefined) {
				for (var i = 0; i < aOutwardData.length; i++) {
					if (aOutwardData[i].changeFlag) {
						if (aOutwardData[i].ques === "Markup distance Percentage needs to be added to the calculated distance ?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"Markup distance Percentage needs to be added to the calculated distance ?" + '"');
								return;
							}
						}

						if (aOutwardData[i].ques === "What will be the IGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"What will be the IGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?" + '"');
								return;
							}
						}

						if (aOutwardData[i].ques === "What will be the CGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"What will be the CGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?" + '"');
								return;
							}
						}

						if (aOutwardData[i].ques === "What will be the SGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"What will be the SGST Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?" + '"');
								return;
							}
						}

						if (aOutwardData[i].ques === "What will be the CESS Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"What will be the CESS Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?" + '"');
								return;
							}
						}

						if (aOutwardData[i].ques ===
							"What will be the Taxable value Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?") {
							if (aOutwardData[i].answerDesc == "") {
								MessageBox.error("Please enter value for Outward   " + '"' +
									"What will be the Taxable value Tolerance Limit for initiating 3Way Recon(Einv Vs EWB Vs GSTR-1)?" + '"');
								return;
							}
						}

						if (aOutwardData[i].keyType === "M" || aOutwardData[i].keyType === "M7") {
							var res = "";
							if (aOutwardData[i].items.A === true) {
								if (res == "") {
									res = "A";
								} else {
									res = res + "*A";
								}
							}
							if (aOutwardData[i].items.B === true) {
								if (res == "") {
									res = "B";
								} else {
									res = res + "*B";
								}
							}
							if (aOutwardData[i].items.C === true) {
								if (res == "") {
									res = "C";
								} else {
									res = res + "*C";
								}
							}
							if (aOutwardData[i].items.D === true) {
								if (res == "") {
									res = "D";
								} else {
									res = res + "*D";
								}
							}
							if (aOutwardData[i].items.E === true) {
								if (res == "") {
									res = "E";
								} else {
									res = res + "*E";
								}
							}
							if (aOutwardData[i].items.F === true) {
								if (res == "") {
									res = "F";
								} else {
									res = res + "*F";
								}
							}
							if (aOutwardData[i].items.G === true) {
								if (res == "") {
									res = "G";
								} else {
									res = res + "*G";
								}
							}
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aOutwardData[i].id,
								"questId": aOutwardData[i].questId,
								"keyType": aOutwardData[i].keyType,
								"answerCode": res
							});
						} else if (aOutwardData[i].ques === "Invoice Series to be computed by DigiGST , basis below options" && aOutwardData[i].quesCode ===
							"O31") {

							if (aOutwardData[i].selected === 3) {
								var k = i - 1;
								if (aOutwardData[k].selected === 0) {
									if (aOutwardData[i].answerDesc == "") {
										MessageBox.error("Please enter value for Outward   " + '"' +
											"Invoice Series to be computed by DigiGST , basis below options" + '"');
										return;
									}
								}
								var res = String.fromCharCode(aOutwardData[i].selected + 65) + "*" + aOutwardData[i].answerDesc;
							} else {
								if (aOutwardData[i].answerDesc == "") {
									aOutwardData[i].answerDesc = "0"
								}
								var res = String.fromCharCode(aOutwardData[i].selected + 65);
							}
							if (aOutwardData[i].changeFlag) {
								postData.req.push({
									"groupCode": $.sap.groupCode,
									"entityId": oEntity,
									"id": aOutwardData[i].id,
									"questId": aOutwardData[i].questId,
									"keyType": aOutwardData[i].keyType,
									"answerCode": res
								});
							}
						} else {
							if (aOutwardData[i].answerDesc != "") {
								var res = aOutwardData[i].answerDesc;
							} else {
								var res = String.fromCharCode(aOutwardData[i].selected + 65);
								if (res == '\x00') {
									res = "";
								}
							}
							postData.req.push({
								"groupCode": $.sap.groupCode,
								"entityId": oEntity,
								"id": aOutwardData[i].id,
								"questId": aOutwardData[i].questId,
								"keyType": aOutwardData[i].keyType,
								"answerCode": res
							});
						}
					}
				}
			}
			if (postData.req.length > 0) {
				this.onUpdateConfigPrmt(postData, type);
			} else {
				MessageBox.information("We don't have any changes");
			}
		},

		onUpdateConfigPrmt: function (payload, type) {
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/updateConfigPrmt.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.status === "S") {
						var oPropModel = this.getView().getModel("Property");
						MessageBox.success(data.resp.message, {
							styleClass: "sapUiSizeCompact"
						});
						if (type === "General") {
							// this.getView().byId("idSubmitGeneral").setEnabled(false);
							this.getGeneral(true);
						} else {
							Promise.all([
									this.getInward(),
									this.getOutward()
								])
								.then(function (values) {
									sap.ui.core.BusyIndicator.hide();
								})
								.catch(function (err) {
									sap.ui.core.BusyIndicator.hide();
									MessageBox.error(err, {
										styleClass: "sapUiSizeCompact"
									});
								});
						}
						oPropModel.setProperty("/subGeneral", false);
						oPropModel.setProperty("/subEntity", false);
						oPropModel.refresh(true);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					MessageBox.error("Error", {
						styleClass: "sapUiSizeCompact"
					});
				});
		},

		onAddrow: function () {
			var oModel = this.getView().getModel("ApiLimit"),
				data = oModel.getProperty("/");
			data.push({
				"Sno": (data.length + 1),
				"fromDate": "",
				"toDate": "",
				"fy": "2021-22",
				"limit": "",
				"edit": true
			});
			oModel.refresh(true);
		},

		defultBind: function () {
			var that = this;
			this.GSTR3BModel = new JSONModel();
			this.data0 = [];
			this.GSTR3BModel.setData(this.data0);
			this.getView().setModel(this.GSTR3BModel, "ApiLimit");
			that.getView().setBusy(true);
			$(document).ready(function ($) {
				$.ajax({
						method: "GET",
						url: "/SapOnboarding/getLimits.do",
						contentType: "application/json",
					})
					.done(function (data, status, jqXHR) {
						that.getView().setBusy(false);
						if (data.hdr.status === "S") {
							for (var i = 0; i < data.resp.limits.length; i++) {
								data.resp.limits[i].Sno = i + 1;
								data.resp.limits[i].edit = false;
								that.data0.push(data.resp.limits[i]);
								that.GSTR3BModel.setData(that.data0);
								that.getView().setModel(that.GSTR3BModel, "ApiLimit");
							}
						}
					})
					.fail(function (jqXHR, status, err) {
						that.getView().setBusy(false);
					});
			});
		},

		onEditRows: function () {
			var tab = this.getView().byId("idTableERP");
			var oJSONModel = tab.getModel("ApiLimit").getData();

			for (var i = 0; i < oJSONModel.length; i++) {
				oJSONModel[i].edit = true;
			}
			this.getView().getModel("ApiLimit").refresh(true);
		},

		onDeleteRows: function (oEvt) {
			var that = this;
			var path = oEvt.getSource().getParent().getBindingContext("ApiLimit").getPath();
			var index = parseInt(path.substring(path.lastIndexOf('/') + 1));
			MessageBox.alert("Do you want to Delete Selected Line Item!", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = that.getView().getModel("ApiLimit").getData();
						model.splice(index, 1);
						that.getView().getModel("ApiLimit").refresh(true);
						var vSlNo = 0;
						for (var i = 0; i < model.length; i++) {
							vSlNo = vSlNo + 1;
							model[i].Sno = vSlNo;
						}
						that.getView().getModel("ApiLimit").refresh(true);
					}
				}
			});
		},

		onSubmitAPI: function () {
			var tab = this.getView().byId("idTableERP"),
				data = this.getView().getModel("ApiLimit").getData();
			for (var j = 0; j < data.length; j++) {
				if (data[j].fromDate === "" || data[j].fromDate === "0" || data[j].fromDate === "00") {
					MessageBox.error("From Day cannot be Empty or Zero");
					return;
				}
				if (data[j].toDate === "" || data[j].toDate === "0" || data[j].toDate === "00") {
					MessageBox.error("To Day cannot be Empty or Zero");
					return;
				}
				if (data[j].limit === "") {
					MessageBox.error("Limit cannot be Empty");
					return;
				}

				if (Number(data[j].limit) < 1000) {
					MessageBox.error("Limit cannot be Less than 1000");
					return;
				}

				if (Number(data[j].fromDate) > 31) {
					MessageBox.error("From Day cannot be greater than 31");
					return;
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].fromDate.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}

				if (Number(data[j].fromDate) > Number(data[j].toDate)) {
					MessageBox.error("From Day cannot be greater than To Day");
					return;
				}

				if (Number(data[j].toDate) > 31) {
					MessageBox.error("To Day cannot be greater than 31");
					return;
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].toDate.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}

				if (Number(data[j].limit) > 100000) {
					MessageBox.error("Limit can not be greater than 100000");
					return;
				}

				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].limit.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
			}

			var postData = {
				"limits": []
			}

			for (var j = 0; j < data.length; j++) {
				postData.limits.push({
					"fromDate": data[j].fromDate,
					"toDate": data[j].toDate,
					"limit": data[j].limit
				});
			}
			var that = this;
			var jsonForSearch = JSON.stringify(postData);
			var saveUserInfo = "/SapOnboarding/saveLimits.do";
			sap.ui.core.BusyIndicator.show(0);
			$(document).ready(function ($) {
				$.ajax({
						method: "POST",
						url: saveUserInfo,
						contentType: "application/json",
						data: jsonForSearch
					})
					.done(function (data, status, jqXHR) {
						if (data.hdr.status == 'S') {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.success(data.resp);
							var oJSONModel = that.getView().getModel("ApiLimit").getData();
							for (var i = 0; i < oJSONModel.length; i++) {
								oJSONModel[i].edit = false;
							}
							that.getView().getModel("ApiLimit").refresh(true);
						} else {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error(data.resp);
						}
					})
					.fail(function (jqXHR, status, err) {
						sap.ui.core.BusyIndicator.hide();
						MessageBox.error("Error");
					});
			});
		},

		dateChangeFrom: function (oEvt) {
			var data = this.getView().getModel("ApiLimit").getData();
			for (var j = 0; j < data.length; j++) {
				if (Number(data[j].fromDate) > 31) {
					MessageBox.error("From Day cannot be greater than 31");
					return;
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].fromDate.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
				if (data[j].toDate !== "") {
					if (Number(data[j].fromDate) > Number(data[j].toDate)) {
						MessageBox.error("From Day cannot be greater than To Day");
						return;
					}
				}
			}
		},

		dateChangeTo: function () {
			var data = this.getView().getModel("ApiLimit").getData();
			for (var j = 0; j < data.length; j++) {
				if (Number(data[j].toDate) > 31) {
					MessageBox.error("To Day cannot be greater than 31");
					return;
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].toDate.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
			}
		},

		limitChange: function () {
			var data = this.getView().getModel("ApiLimit").getData();
			for (var j = 0; j < data.length; j++) {
				if (Number(data[j].limit) < 1000) {
					MessageBox.error("Limit cannot be Less than 1000");
					return;
				}
				if (Number(data[j].limit) > 100000) {
					MessageBox.error("Limit cannot be greater than 100000");
					return;
				}
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!data[j].limit.toString().match(reg)) {
					MessageBox.error("Negative values and alphabets are not allowed");
					return;
				}
			}
		},

		onEditInputOutput: function () {
			var oPropModel = this.getView().getModel("Property"),
				oOutModel = this.getView().getModel("Outward"),
				oData = oOutModel.getProperty("/resp");

			oData.forEach(function (e) {
				e.edit = true;
			})
			oOutModel.refresh(true)

			var oInModel = this.getView().getModel("Inward"),
				oData = oInModel.getProperty("/resp");

			oData.forEach(function (e) {
				e.edit = true;
				if (e.quesCode === "I14") {
					e.items.forEach(function (l) {
						l.edit = true;
					});
				}
			});
			oPropModel.setProperty("/subEntity", true);
			oInModel.refresh(true);
			oPropModel.refresh(true);
		},

		/**************************************************************
		 * General
		 **************************************************************/
		createContentGeneral: function (sId, oContext) {
			var oUIControl = new sap.m.VBox(),
				obj = oContext.getObject(),
				oControl;

			oUIControl.addItem(new sap.m.HBox({
				items: [
					new sap.m.Label({
						text: "{General>sno}.",
						textAlign: "End",
						design: "Bold",
						width: "2rem"
					}).addStyleClass("sapUiTinyMarginEnd"),
					new sap.m.Label({
						text: "{General>ques}",
						design: "Bold",
						wrapping: true
					})
				]
			}));
			switch (oContext.getProperty("keyType")) {
			case "R":
				oControl = new sap.m.RadioButtonGroup({
					enabled: "{General>edit}",
					selectedIndex: "{General>selected}",
					columns: 4
				}).bindAggregation("buttons", "General>items", function (sSubId, oSubContext) {
					return new sap.m.RadioButton({
						text: "{General>answerDesc}"
					});
				}, true);
				break;
			case "T":
				oControl = new sap.m.Input({
					enabled: "{General>edit}",
					type: "{General>type}",
					// change: "onSelectRBG(${General>id})",
					maxLength: "{General>maxLength}",
					value: "{General>answerDesc}",
					width: "20rem"
				});
				break;
			case "D":
				oControl = new sap.m.DatePicker({
					enabled: "{General>edit}",
					// change: "onSelectRBG(${General>id})",
					value: "{General>answerDesc}",
					valueFormat: "MMyyyy",
					displayFormat: "MMM yyyy",
					width: "20rem"
				});
				break;
			}
			if (oContext.getProperty("keyType") !== "M7") {
				oUIControl.addStyleClass("sapUiSmallMarginBottom")
				oUIControl.addItem(new sap.m.HBox({
					items: [
						new sap.m.Label({
							text: "",
							width: "2rem"
						}).addStyleClass("sapUiTinyMarginEnd"),
						oControl
					]
				}));
			}
			return oUIControl;
		},

		getGeneral: function (flag) {
			var postData = {
				"req": {
					"groupCode": $.sap.groupCode,
					"entityId": $.sap.entityId,
					"type": "general"
				}
			};
			return new Promise(function (resolve, reject) {
				sap.ui.core.BusyIndicator.show(0);
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getConfigPrmt.do",
						contentType: "application/json",
						data: JSON.stringify(postData)
					})
					.done(function (data, status, jqXHR) {
						sap.ui.core.BusyIndicator.hide();
						var oPropModel = this.getView().getModel("Property");
						oPropModel.setProperty("/subGeneral", false);
						oPropModel.refresh(true);

						data.resp.forEach(function (el, i) {
							el.sno = i + 1;
							el.edit = false;
							el.answerDesc = el.answerDesc || "";

							if (el.keyType == "T") {
								if (el.quesCode == "G16") {
									el.type = "Text";
									el.maxLength = 255;
								} else if (el.quesCode == "G17") {
									el.type = "Text";
									el.maxLength = 99;
								} else {
									el.type = "Number";
									el.maxLength = 4;
								}
							}
						});
						var oGeneral = new JSONModel(data),
							oData = $.extend(true, [], data.resp);
						oGeneral.setSizeLimit(data.resp.length);
						this.getView().setModel(oGeneral, "General");
						this.getView().setModel(new JSONModel(oData), "GeneralConfig");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						var oErr = JSON.parse(jqXHR.responseText);
						if (flag) {
							sap.ui.core.BusyIndicator.hide();
							MessageBox.error(jqXHR.status + " : " + oErr.hdr.message, {
								styleClass: "sapUiSizeCompact"
							});
						}
						this.getView().setModel(new JSONModel(null), "General");
						reject(jqXHR.status + " : " + oErr.hdr.message);
					}.bind(this));
			}.bind(this));
		},

		onEditGeneral: function () {
			var oPropModel = this.getView().getModel("Property"),
				oModel = this.getView().getModel("General"),
				oData = oModel.getProperty("/resp");

			oData.forEach(function (e) {
				e.edit = true;
			});
			oModel.refresh(true);
			oPropModel.setProperty("/subGeneral", true);
			oPropModel.refresh(true);
		},

		onSubmitGeneral: function () {
			MessageBox.information("Do you want to submit the changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction == "YES") {
						this.onSubmitGeneralFinal()
					}
				}.bind(this)
			});
		},

		_validateGeneralField: function (payload, data, oConfig, entity) {
			var obj = oConfig.find(function (item) {
				return item.questId === data.questId;
			});
			switch (data.keyType) {
			case "R":
				var flag = (data.selected !== obj.selected);
				break;
			case "T":
				flag = (data.answerDesc !== obj.answerDesc);
				break;
			case "M":
				flag = this._arraysMatch(data.items, obj.items);
				break;
			case "D":
				flag = (data.answerDesc !== obj.answerDesc);
				break;
			case "SR":
				if (data.hasOwnProperty("get2AHour")) {
					flag = (data.get2AHour !== obj.get2AHour || data.get2Amin !== obj.get2Amin);
				} else {
					flag = (data.selected !== obj.selected);
				}
				break;
			}
			if (flag) {
				payload.push({
					"id": data.id,
					"groupCode": $.sap.groupCode,
					"entityId": entity,
					"questId": data.questId,
					"keyType": data.keyType,
					"answerCode": data.answerDesc ? data.answerDesc : String.fromCharCode(data.selected + 65)
				});
			}
		},

		onSubmitGeneralFinal: function () {
			var aGeneralData = this.getView().getModel("General").getProperty("/resp"),
				oConfig = this.getView().getModel("GeneralConfig").getProperty("/"),
				oEntity = this.getView().byId("idEntityGeneral").getSelectedKey(),
				oData = $.extend(true, [], aGeneralData),
				payload = {
					"req": []
				};

			aGeneralData.forEach(function (e) {
				var flag = false;
				if (e.quesCode == "G14" && e.selected == 1) {
					oData.forEach(function (d) {
						if (["G16", "G17", "G18", "G20"].includes(d.quesCode) && !d.answerDesc) {
							flag = true;
						}
					});
					if (flag) {
						MessageBox.information(
							"For generation of B2C QR is selected Dynamic QR(unsigned) below Questions are mandatory: " +
							"\n 13. Payee Address \n 14. Payee Name \n 15. Payee Merchant Code \n 17. QR code Expire Time"
						);
						return;
					}
				}
				this._validateGeneralField(payload.req, e, oConfig, oEntity);
			}.bind(this));
			if (payload.req.length) {
				this.onUpdateConfigPrmt(payload, "General");
			} else {
				MessageBox.information("We don't have any changes");
			}
		},

		/**************************************************************
		 * Group Level Configuration
		 **************************************************************/
		_getGroupLevelQuestion: function () {
			return new Promise(function (resolve, reject) {
				var payload = {
					"req": {
						"groupCode": $.sap.groupCode,
						"type": "group"
					}
				};
				this.getView().setBusy(true);
				$.ajax({
						method: "POST",
						url: "/SapOnboarding/getGroupConfigPrmt.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHRa) {
						this._bindGroupLevel(data);
						this.getView().setBusy(false);
						resolve(data.resp);
					}.bind(this))
					.fail(function (error) {
						this.getView().setBusy(false);
						reject(JSON.parse(error.responseText));
					}.bind(this));
			}.bind(this));
		},

		_bindGroupLevel: function (data) {
			var aQues = [],
				oProp = {};

			data.resp.forEach(function (e, i) {
				var obj = aQues.find(function (q) {
					return q.quesCode === e.quesCode;
				});
				e.edit = false;
				if (e.keyType === "M") {
					var aAnsDesc = (e.answerDesc || "").split('*');
					e.items = this._getSubMultiSelect(obj.quesCode);
					e.items.forEach(function (f) {
						f.select = aAnsDesc.includes(f.key);
					});
				}
				if (!obj) {
					e.sno = aQues.length + 1;
					e.children = [];
					aQues.push(e);
				} else {
					e.sno = String.fromCharCode(97 + obj.children.length);
					obj.children.push(e);
				}
			}.bind(this));
			var oData = $.extend(true, [], data.resp);
			this.getView().setModel(new JSONModel({
				"quesList": aQues,
				"prop": oProp
			}), "GroupLevel");
			this.getView().setModel(new JSONModel(oData), "GrpLevelConfig");
			this.getView().getModel("Property").setProperty("/subGroup", false);
		},

		_getSubMultiSelect: function (quesCd) {
			if (quesCd === "G36") {
				return [{
					key: "A",
					text: "Consolidated 2BvsPR",
					edit: false
				}, {
					key: "B",
					text: "AIM Auto Recon 2AvsPR Report",
					edit: false
				}, {
					key: "C",
					text: "Consolidated 2AvsPR",
					edit: false
				}];
			}
		},

		createContentGrpLvl: function (sId, oContext) {
			var oUIControl = new sap.m.VBox().addStyleClass("sapUiSmallMarginBottom"),
				obj = oContext.getObject(),
				oControl;

			oUIControl.addItem(new sap.m.HBox({
				items: [
					new sap.m.Label({
						text: "{GroupLevel>sno}.",
						textAlign: "End",
						design: "Bold",
						width: "2rem"
					}).addStyleClass("sapUiTinyMarginEnd"),
					new sap.m.Label({
						text: "{GroupLevel>ques}",
						design: "Bold",
						wrapping: true
					})
				]
			}));
			switch (oContext.getProperty("keyType")) {
			case "R":
				oControl = new sap.m.RadioButtonGroup({
					enabled: "{GroupLevel>edit}",
					selectedIndex: "{GroupLevel>selected}",
					columns: 5
				}).bindAggregation("buttons", "GroupLevel>items", function (sSubId, oSubContext) {
					return new sap.m.RadioButton({
						text: "{GroupLevel>answerDesc}"
					});
				}, true);
				break;
			case "T":
				oControl = new sap.m.Input({
					enabled: "{GroupLevel>edit}",
					type: "{GroupLevel>type}",
					change: "onSelectRBG(${GroupLevel>id})",
					maxLength: "{GroupLevel>maxLength}",
					value: "{GroupLevel>answerDesc}",
					width: "20rem"
				});
				break;
			case "D":
				oControl = new sap.m.DatePicker({
					enabled: "{GroupLevel>edit}",
					change: "onSelectRBG(${GroupLevel>id})",
					value: "{GroupLevel>answerDesc}",
					valueFormat: "MMyyyy",
					displayFormat: "MMyyyy",
					width: "20rem"
				});
				break;
			}
			oUIControl.addItem(new sap.m.HBox({
				items: [
					new sap.m.Label({
						text: "",
						width: "2rem"
					}).addStyleClass("sapUiTinyMarginEnd"),
					oControl
				]
			}));
			if (obj.children.length) {
				oUIControl.addItem(this._createSubContentGrpLvl());
			}
			return oUIControl;
		},

		_createSubContentGrpLvl: function () {
			var oVBox = new sap.m.VBox({
				visible: "{=!${GroupLevel>selected}}"
			});
			oVBox.bindAggregation("items", {
				path: "GroupLevel>children",
				factory: function (sId, oContext) {
					var obj = oContext.getObject(),
						oVBox = new sap.m.VBox(),
						oHBox = new sap.m.HBox({
							items: [
								new sap.m.Label({
									text: "{GroupLevel>sno}.",
									textAlign: "End",
									width: "3.5rem"
								}),
								new sap.m.Label({
									text: "{GroupLevel>ques}",
									wrapping: true
								})
							]
						}).addStyleClass("sapUiTinyMarginTop");
					oVBox.addItem(oHBox);

					switch (obj.keyType) {
					case "SR":
						var oControl = new sap.m.HBox({
							items: [
								new sap.m.RadioButtonGroup({
									enabled: "{GroupLevel>edit}",
									selectedIndex: "{GroupLevel>selected}",
									columns: 5
								}).bindAggregation("buttons", "GroupLevel>items", function (sSubId, oSubContext) {
									return new sap.m.RadioButton({
										text: "{GroupLevel>answerDesc}"
									});
								}, true)
							]
						});
						if (obj.ques === "Select Timestamp?") {
							this._setTimestamp(oControl);

						} else if (["G43", "G44"].includes(obj.quesCode)) {
							var oInput = new sap.m.Input({
								enabled: "{GroupLevel>edit}",
								visible: "{=${GroupLevel>selected}!==0}",
								value: "{GroupLevel>answerDesc}",
								width: "10rem"
							}).addStyleClass("sapUiSmallMarginBegin");
							oControl.addItem(oInput);

						} else {
							var obj1 = obj.items.find(function (e) {
								return e.answerDesc === "Custom Dates";
							});
							if (obj1) {
								var oInput = new sap.m.Input({
									enabled: "{GroupLevel>edit}",
									visible: "{=${GroupLevel>selected}===4}",
									value: "{GroupLevel>answerDesc}",
									width: "10rem"
								}).addStyleClass("sapUiSmallMarginBegin");
								oControl.addItem(oInput);
							}
						}
						break;
					case "M":
						var oControl = new sap.m.HBox()
							.bindAggregation("items", "GroupLevel>items", function (sId, oContext) {
								var sParentPath = oContext.getPath().split("/").slice(0, -2).join("/"),
									oItem = oContext.getObject();
								return new sap.m.CheckBox({
									enabled: "{=${GroupLevel>edit}}",
									text: "{GroupLevel>text}",
									selected: "{GroupLevel>select}",
									select: function (oEvent) {
										console.log(oEvent);
									}
								}).addStyleClass("sapUiTinyMarginEnd");
							}, true);
						break;
					case "T":
						oControl = new sap.m.Input({
							enabled: "{GroupLevel>edit}",
							type: "{GroupLevel>type}",
							change: "onSelectRBG(${GroupLevel>id})",
							maxLength: "{GroupLevel>maxLength}",
							value: "{GroupLevel>answerDesc}",
							width: "20rem"
						});
						break;
					}
					oVBox.addItem(new sap.m.HBox({
						items: [
							new sap.m.Label({
								text: "",
								width: "3.5rem"
							}).addStyleClass("sapUiTinyMarginEnd"),
							oControl
						]
					}));
					return oVBox;
				}.bind(this)
			});
			return oVBox;
		},

		_setTimestamp: function (oControl) {
			oControl.addItem(
				new sap.m.Label({
					text: "Hours"
				}).addStyleClass("sapUiTinyMarginTop sapUiTinyMarginEnd")
			);
			oControl.addItem(
				new sap.m.Select({
					enabled: "{GroupLevel>edit}",
					selectedKey: "{GroupLevel>get2AHour}",
					width: "5rem"
				}).bindAggregation("items", "TimeData>/timeHours", function (sSubId, oSubContext) {
					return new sap.ui.core.Item({
						key: "{TimeData>text}",
						text: "{TimeData>text}"
					});
				})
			);
			oControl.addItem(
				new sap.m.Label({
					text: "Minutes"
				}).addStyleClass("sapUiTinyMarginTop sapUiTinyMarginEnd sapUiSmallMarginBegin")
			);
			oControl.addItem(
				new sap.m.Select({
					enabled: "{GroupLevel>edit}",
					selectedKey: "{GroupLevel>get2Amin}",
					width: "5rem"
				}).bindAggregation("items", "TimeData>/timeMinutes", function (sSubId, oSubContext) {
					return new sap.ui.core.Item({
						key: "{TimeData>text}",
						text: "{TimeData>text}"
					});
				})
			);
		},

		onEditGroupLevel: function () {
			var oPropModel = this.getView().getModel("Property"),
				oModel = this.getView().getModel("GroupLevel"),
				oData = oModel.getProperty("/quesList");

			oData.forEach(function (e) {
				e.edit = true;
				e.children.forEach(function (c) {
					c.edit = true;
					if (c.items) {
						c.items.forEach(function (item) {
							item.edit = true;
						});
					}
				});
			});
			oModel.refresh(true)
			oPropModel.setProperty("/subGroup", true);
			oPropModel.refresh(true);
		},

		_arraysMatch: function (arr1, arr2) {
			var flag = false;
			arr1.forEach(function (e, i) {
				flag = flag || (e.select !== arr2[i].select);
			});
			return flag;
		},

		_getGrpLevelAnswer: function (data) {
			switch (data.keyType) {
			case "R":
				return String.fromCharCode(data.selected + 65);
			case "T":
				return data.answerDesc;
			case "M":
				var ansCode = data.items.filter(function (item) {
					return item.select === true;
				}).map(function (item) {
					return item.key;
				});
				return ansCode.join('*');
			case "SR":
				if (data.hasOwnProperty("get2AHour")) {
					return data.get2AHour + ":" + data.get2Amin;

				} else if (["G43", "G44"].includes(data.quesCode)) {
					var ansDesc = (data.selected ? ("*" + data.answerDesc) : "");
					return (String.fromCharCode(data.selected + 65) + ansDesc);
				} else {
					var obj = data.items.find(function (e) {
							return e.answerDesc === "Custom Dates";
						}),
						ansDesc = (data.selected === data.items.length - 1) && obj ? ("*" + data.answerDesc) : "";
					return (String.fromCharCode(data.selected + 65) + ansDesc);
				}
			}
		},

		_validateField: function (payload, data, oConfig) {
			var obj = oConfig.find(function (q) {
				return q.questId === data.questId;
			});
			switch (data.keyType) {
			case "R":
				var flag = (data.selected !== obj.selected);
				break;
			case "T":
				flag = (data.answerDesc !== obj.answerDesc);
				break;
			case "M":
				flag = this._arraysMatch(data.items, obj.items);
				break;
			case "D":
				flag = "";
				break;
			case "SR":
				if (data.hasOwnProperty("get2AHour")) {
					flag = (data.get2AHour !== obj.get2AHour || data.get2Amin !== obj.get2Amin);
				} else {
					flag = (data.selected !== obj.selected) || (data.answerDesc !== obj.answerDesc);
				}
				break;
			}
			if (flag) {
				payload.push({
					"id": data.id,
					"groupCode": $.sap.groupCode,
					"keyType": data.keyType,
					"questId": data.questId,
					"answerCode": this._getGrpLevelAnswer(data)
				});
			}
		},

		onSubmitGroupLevel: function () {
			MessageBox.information("Do you want to submit the changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var oConfig = this.getView().getModel("GrpLevelConfig").getProperty("/"),
							oData = this.getView().getModel("GroupLevel").getProperty("/quesList"),
							payload = {
								"req": []
							};
						oData.forEach(function (e) {
							this._validateField(payload.req, e, oConfig);
							if (e.children.length && !e.selected) {
								e.children.forEach(function (child) {
									this._validateField(payload.req, child, oConfig);
								}.bind(this));
							}
						}.bind(this));
						if (!payload.req.length) {
							MessageBox.warning("No changes found to save.", {
								styleClass: "sapUiSizeCompact"
							});
							return;
						}
						this.getView().setBusy(true);
						$.ajax({
								method: "POST",
								url: "/SapOnboarding/updateGroupConfigPrmt.do",
								contentType: "application/json",
								data: JSON.stringify(payload)
							})
							.done(function (data, status, jqXHR) {
								this.getView().setBusy(false);
								if (data.resp.status === "S") {
									MessageBox.success(data.resp.message);
									this._getGroupLevelQuestion();
								} else {
									MessageBox.error(data.resp.message);
								}
							}.bind(this))
							.fail(function () {
								this.getView().setBusy(false);
							}.bind(this));
					}
				}.bind(this)
			});
		},

		/**************************************************************
		 * Reset Configuratin Parameter to default
		 **************************************************************/
		onResetConfig: function (type) {
			var payload = {
				"req": {
					"groupCode": $.sap.groupCode,
					"type": type,
					"entityId": (type !== "Group" ? $.sap.entityId : '0')
				}
			};
			this.getView().setBusy(true);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/resetConfigPrmt.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					if (data.hdr.status === "S") {
						MessageBox.success("Configuration Parameters resetted to default");
						switch (type) {
						case "General":
							this.getGeneral()
								.finally(function (values) {
									this.getView().setBusy(false);
								}.bind(this));
							break;
						case "Entity":
							Promise.all([
									this.getInward(),
									this.getOutward()
								])
								.finally(function (values) {
									this.getView().setBusy(false);
								}.bind(this));
							break;
						case "Group":
							this._getGroupLevelQuestion();
							break;
						}
					} else {
						MessageBox.error("Failed to Reset");
						this.getView().setBusy(false);
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setBusy(false);
				}.bind(this));
		},

		/**************************************************************
		 * DMS Configuration
		 **************************************************************/
		getDMSData: function () {
			this.getView().byId("idSubmitDMS").setEnabled(false);
			var payload = {
				"req": {
					"groupCode": $.sap.groupCode,
					"type": "DMS"
				}
			};
			this.getView().setBusy(true);
			$.ajax({
					method: "POST",
					url: "/SapOnboarding/getDmsConfigPrmt.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHRa) {
					this._bindDMSData(data);
					this.getView().setBusy(false);
				}.bind(this))
				.fail(function (error) {
					this.getView().setBusy(false);
				}.bind(this));
		},

		_bindDMSData: function (data) {
			var oTabData = [];
			var oData = {
				"resp": []
			};
			for (var i = 0; i < data.resp.length; i++) {
				if (data.resp[i].keyType === "R") {
					data.resp[i].sno = i + 1;
					data.resp[i].edit = false;
					oData.resp.push(data.resp[i])
				} else {
					var Username = "",
						userPassword = "";
					if (data.resp[i].answerDesc) {
						Username = data.resp[i].answerDesc.split('*')[0];
						userPassword = data.resp[i].answerDesc.split('*')[1];
					}
					oTabData.push({
						"groupName": $.sap.groupName,
						"groupCode": $.sap.groupCode,
						"Username": Username,
						"userPassword": userPassword,
						"edit": false

					})
					data.resp[i].sno = String.fromCharCode(97);
					data.resp[i].items = oTabData;
					data.resp[i].oSRVisible = data.resp[0].selected === 0 ? true : false;
					oData.resp.push(data.resp[i]);
				}

			}
			this.getView().setModel(new JSONModel(oData), "dmsconfig");

		},

		onEditDMS: function () {
			this.getView().byId("idSubmitDMS").setEnabled(true);

			var oModel = this.getView().getModel("dmsconfig"),
				oData = oModel.getProperty("/");

			oData.resp.forEach(function (e) {
				e.edit = true;
				if (e.keyType === "T") {
					e.items.forEach(function (l) {
						l.edit = true;
					});
				}
			});
			oModel.refresh(true)
		},

		onSubmitDMS: function () {
			MessageBox.information("Do you want to submit the changes?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {

						var oDMS = this.getView().getModel("dmsconfig").getProperty("/resp"),
							payload = {
								"req": [{
									"id": oDMS[0].id,
									"groupCode": oDMS[1].items[0].groupCode,
									"keyType": oDMS[0].keyType,
									"questId": oDMS[0].questId,
									"answerCode": String.fromCharCode(oDMS[0].selected + 65),
									"userName": oDMS[1].items[0].Username,
									"password": oDMS[1].items[0].userPassword
								}]
							}
						this.getView().setBusy(true);
						$.ajax({
								method: "POST",
								url: "/SapOnboarding/updateDmsConfigPrmt.do",
								contentType: "application/json",
								data: JSON.stringify(payload)
							})
							.done(function (data, status, jqXHR) {
								this.getView().setBusy(false);
								if (data.resp.status === "S") {
									MessageBox.success(data.resp.message);
									this.getDMSData();
								} else {
									MessageBox.error(data.resp.message);
								}
							}.bind(this))
							.fail(function () {
								this.getView().setBusy(false);
							}.bind(this));
					}
				}.bind(this)
			});
		},

		onFetchRules: function () {
			var oView = this.getView();
			$.ajax({
					method: "GET",
					url: "/SapOnboarding/getFetchRules.do",
					contentType: "application/json"
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.status = "S") {
						MessageBox.success(data.resp.message);
					} else {
						MessageBox.error(data.resp.message);
					}
				})
				.fail(function (jqXHR, status, err) {
					var oResp = JSON.parse(jqXHR.responseText);
					MessageBox.error(oResp.hdr.message);
					sap.ui.core.BusyIndicator.hide();
				});
		},

		onShowRules: function () {
			var oView = this.getView();
			$.ajax({
					method: "GET",
					url: "/SapOnboarding/getViewRules.do",
					contentType: "application/json"
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.resp.status = "S") {
						data.rules.sort(function (a, b) {
							return a.ruleNo - b.ruleNo;
						});
						var oModel = new JSONModel(data.rules);
						oModel.setSizeLimit(data.rules.length);
						oView.setModel(oModel, "Rules");
						this._onOpenDialog(data);
					} else {
						oView.setModel(new JSONModel(null), "Rules");
					}

				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					oView.setModel(new JSONModel(null), "Rules");
				}.bind(this));
		},

		_onOpenDialog: function (data) {
			var oView = this.getView();

			var oModel = new JSONModel({
				rules: data.rules
			});

			// Create a Dialog dynamically
			if (!this._oDialog) {
				this._oDialog = new Dialog({
					busyIndicatorDelay: 0,
					title: "View Rules",
					content: new List({
						items: {
							path: "/rules",
							template: new StandardListItem({
								title: "{ruleNo}",
								description: "{ruleName}"
							})
						}
					}),
					buttons: [
						new Button({
							text: "Close",
							press: function () {
								this._oDialog.close();
							}.bind(this)
						})
					]
				}).addStyleClass("sapUiSizeCompact");

				// Set Model to Dialog Content
				this._oDialog.setModel(oModel);

				// Add Dialog to the View
				oView.addDependent(this._oDialog);
			}

			// Open Dialog
			this._oDialog.open();
		},

		onSelectDMS: function (ques, index, quesID, quesCode) {
			var oModel = this.getView().getModel("dmsconfig").getProperty("/");
			oModel.resp[1].oSRVisible = oModel.resp[0].selected === 0 ? true : false;
			oModel.refresh(true);
		},
	});
})