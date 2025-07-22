sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/json/JSONModel",
	"sap/m/Popover"
], function (Controller, MessageBox, JSONModel, Popover) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.B2CN", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.B2CN
		 */
		onInit: function () {
			// this.getOwnerComponent().getRouter().attachRoutePatternMatched(this._onRouteMatched, this);
			var Model = new JSONModel();
			var radioSelection = {
				"G": false,
				"P": true,
				"PC": true
			};
			Model.setData(radioSelection);
			this.getView().setModel(Model, "Visible");
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.gstinBind();
				this.plantBind();
				this.profitCenterBind();
				this.defultBind();
				this.defultBind1();
			}
		},

		gstinBind: function () {
			var that = this;
			var getAllGstin = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/getGstnbyEntityId.do?entityId=" + $.sap.entityID;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList1,
					contentType: "application/json",
					//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						getAllGstin.setData(data.resp.gstindata);
						that.getView().setModel(getAllGstin, "getAllGstin");
					} else {
						getAllGstin.setData([]);
						that.getView().setModel(getAllGstin, "getAllGstin");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					getAllGstin.setData([]);
					that.getView().setModel(getAllGstin, "getAllGstin");
				});
			});
		},

		plantBind: function () {
			var that = this;
			var getAllGstin = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/getplantCodebyEntityId.do?entityId=" + $.sap.entityID;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList1,
					contentType: "application/json",
					//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						getAllGstin.setData(data.resp.plantdata);
						that.getView().setModel(getAllGstin, "getAllPlant");
					} else {
						getAllGstin.setData([]);
						that.getView().setModel(getAllGstin, "getAllPlant");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					getAllGstin.setData([]);
					that.getView().setModel(getAllGstin, "getAllPlant");
				});
			});
		},

		profitCenterBind: function () {
			var that = this;
			var getAllGstin = new JSONModel();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/getprofitCenterbyEntityId.do?entityId=" + $.sap.entityID;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList1,
					contentType: "application/json"
						//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						getAllGstin.setData(data.resp.profitCenterData);
						that.getView().setModel(getAllGstin, "getProfitCenter");
					} else {
						getAllGstin.setData([]);
						that.getView().setModel(getAllGstin, "getProfitCenter");
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					getAllGstin.setData([]);
					that.getView().setModel(getAllGstin, "getProfitCenter");
				});
			});
		},

		defultBind: function () {
			var that = this;
			this.GSTR3BModel = new JSONModel();
			this.data0 = [];
			this.GSTR3BModel.setData(this.data0);
			this.getView().setModel(this.GSTR3BModel, "ServiceOptions");
			var option = this.byId("RB").getSelectedIndex();
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/getB2COnBoardingParams.do?entityId=" + $.sap.entityID + "&" + "option=" + option;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList,
					contentType: "application/json",
					//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						that.byId("labId").setText(data.resp.data[0].createdOn);
						for (var i = 0; i < data.resp.data.length; i++) {
							that.data0.push(data.resp.data[i]);
							that.GSTR3BModel.setData(that.data0);
							that.getView().setModel(that.GSTR3BModel, "ServiceOptions");
						}
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		defultBind1: function () {
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/getB2CAmtConfigParams.do?entityId=" + $.sap.entityID;
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: GstnsList1,
					contentType: "application/json"
						//data: JSON.stringify(SelectedData)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					that.Status = data;
					if (data.hdr.status === "S") {
						that.byId("labId1").setText(data.resp.data.createdOn);
						that.byId("saveID").setEnabled(true);
						that.byId("RB32").setEditable(true);
						if (data.resp.data.identifier === "percent") {
							that.byId("payId1").setValue(data.resp.data.value);
							that.byId("payId").setValue("");
							that.byId("RB32").setSelectedIndex(1);
							that.byId("payId1").setVisible(true);
						} else {
							that.byId("payId").setValue(data.resp.data.value);
							that.byId("payId1").setValue("");
							that.byId("RB32").setSelectedIndex(0);
							that.byId("payId").setVisible(true);
						}
					} else {
						that.byId("payId").setValue("");
						that.byId("payId1").setValue("");
						that.byId("saveID").setEnabled(false);
						that.byId("RB32").setEditable(false);
						that.byId("payId").setVisible(false);
						that.byId("payId1").setVisible(false);
						that.byId("RB32").setSelectedIndex(-1);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		radioSelect: function (oEvt) {
			var data = this.getView().getModel("Visible").getData();
			var Index = oEvt.getSource().getSelectedIndex();
			if (Index === 0) {
				data.G = false;
				data.P = true;
				data.PC = true;
				this.byId("option1").setVisible(true);
				this.byId("option2").setVisible(false);
				this.byId("option3").setVisible(false);
			} else if (Index === 1) {
				data.G = true;
				data.P = true;
				data.PC = false;
				this.byId("option1").setVisible(false);
				this.byId("option2").setVisible(true);
				this.byId("option3").setVisible(false);
			} else if (Index === 2) {
				data.G = true;
				data.P = false;
				data.PC = true;
				this.byId("option1").setVisible(false);
				this.byId("option2").setVisible(false);
				this.byId("option3").setVisible(true);
			}
			this.getView().getModel("Visible").refresh(true);
			this.defultBind();
		},

		onAddrow: function () {
			var newRec = {
				"entityId": $.sap.entityID,
				"entityName": $.sap.entityName,
				"gstin": "",
				"plantCode": "",
				"profitCentre": "",
				"transMode": "",
				"payeeAddress": "",
				"payeeName": "",
				"payeeMerCode": "",
				"transQRMed": "",
				"qrExpiry": "",
				"paymentInfo": "I"
			};
			this.data0.push(newRec);
			this.GSTR3BModel.setData(this.data0);
			this.getView().setModel(this.GSTR3BModel, "ServiceOptions");
			this.getView().getModel("ServiceOptions").refresh(true);
		},

		onGstinChange: function (oEvt) {
			oEvt.getSource().getParent().getParent().getModel("ServiceOptions").setProperty(oEvt.getSource().getParent().getBindingContext(
				"ServiceOptions").sPath + "/gstin", oEvt.getSource().getSelectedKey());
			this.getView().getModel("ServiceOptions").refresh(true);
		},

		onPlantChange: function (oEvt) {
			/*var tabData = this.getView().getModel("ServiceOptions").getData();
			var option = this.byId("RB").getSelectedIndex();
			var text = oEvt.getSource().getSelectedItem().getText();
			if (tabData.length > 1) {
				if (option === 1) {
					for (var i = 0; i < tabData.length; i++) {
						if (tabData[i + 1] !== undefined) {
							if (tabData[i].gstin === tabData[i + 1].gstin && tabData[i].plantCode === text) {
								MessageBox.error("Please select different PlantCode");
								oEvt.getSource().setSelectedKey("");
								oEvt.getSource().getSelectedItem().setText("");
								return;
							}
						}
					}
				}
			}*/

			oEvt.getSource().getParent().getParent().getModel("ServiceOptions").setProperty(oEvt.getSource().getParent().getBindingContext(
				"ServiceOptions").sPath + "/plantCode", oEvt.getSource().getSelectedKey());
			this.getView().getModel("ServiceOptions").refresh(true);

		},

		onProfitCenterChange: function (oEvt) {
			/*var tabData = this.getView().getModel("ServiceOptions").getData();
			var option = this.byId("RB").getSelectedIndex();
			var text = oEvt.getSource().getSelectedItem().getText();
			if (tabData.length > 1) {
				if (option === 0) {
					for (var i = 0; i < tabData.length; i++) {
						if (tabData[i + 1] !== undefined) {
							if (tabData[i].plantCode === tabData[i + 1].plantCode && tabData[i].profitCentre === text) {
								MessageBox.error("Please select different Profit Centre");
								oEvt.getSource().setSelectedKey("");
								oEvt.getSource().getSelectedItem().setText("");
								return;
							}
						}
					}
				} else if (option === 2) {
					for (var j = 0; j < tabData.length; j++) {
						if (tabData[j + 1] !== undefined) {
							if (tabData[j].gstin === tabData[j + 1].gstin && tabData[j].profitCentre === text) {
								MessageBox.error("Please select different Profit Centre");
								oEvt.getSource().setSelectedKey("");
								oEvt.getSource().getSelectedItem().setText("");
								return;
							}
						}
					}
				}
			}*/

			oEvt.getSource().getParent().getParent().getModel("ServiceOptions").setProperty(oEvt.getSource().getParent().getBindingContext(
				"ServiceOptions").sPath + "/profitCenter", oEvt.getSource().getSelectedKey());
			this.getView().getModel("ServiceOptions").refresh(true);
		},

		onSubmit: function () {
			var tabData = this.getView().getModel("ServiceOptions").getData();
			var option = this.byId("RB").getSelectedIndex();
			var data = [];
			for (var i = 0; i < tabData.length; i++) {
				if (tabData[i].transMode === "") {
					MessageBox.error("Please Select Transaction Initiation Mode");
					return;
				}
				if (tabData[i].transMode === "") {
					MessageBox.error("Please Select Transaction Initiation Mode");
					return;
				}
				if (tabData[i].payeeAddress === "") {
					MessageBox.error("Please Enter Payee Address");
					return;
				}
				if (tabData[i].payeeName === "") {
					MessageBox.error("Please Enter Payee Name");
					return;
				}
				if (tabData[i].payeeMerCode === "") {
					MessageBox.error("Please Enter Payee Marchant Code");
					return;
				}
				if (tabData[i].transQRMed === "") {
					MessageBox.error("Please Select Transaction QR Medium");
					return;
				}

				var val = tabData[i].qrExpiry;
				var reg = /^[0-9]*\.?[0-9]*$/;
				if (!val.match(reg)) {
					MessageBox.error("Negative values are not allowed");
					return;
				}

				var obj = {
					"option": option,
					"entityId": $.sap.entityID,
					"gstin": tabData[i].gstin === "" ? null : tabData[i].gstin,
					"plantCode": tabData[i].plantCode === "" ? null : tabData[i].plantCode,
					"profitCentre": tabData[i].profitCentre === "" ? null : tabData[i].profitCentre,
					"transMode": tabData[i].transMode,
					"payeeAddress": tabData[i].payeeAddress,
					"payeeName": tabData[i].payeeName,
					"payeeMerCode": tabData[i].payeeMerCode,
					"transQRMed": tabData[i].transQRMed,
					"qrExpiry": tabData[i].qrExpiry === "0" ? "" : tabData[i].qrExpiry,
					"paymentInfo": tabData[i].paymentInfo
				};
				data.push(obj);
			}
			var Request = {
				"req": {
					"option": option,
					"entityId": $.sap.entityID,
					"data": data
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/saveB2CQROnboardingParams.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList1,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (JSON.parse(data).hdr.status === "S") {
						MessageBox.success(JSON.parse(data).resp);
						that.defultBind();
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		onDelete: function (oEvt) {
			debugger;
			var that = this;
			var path = oEvt.getSource().getParent().getBindingContext("ServiceOptions").getPath();
			var index = parseInt(path.substring(path.lastIndexOf('/') + 1));
			MessageBox.alert("Do you want to Delete Selected Line Item!", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var model = that.getView().getModel("ServiceOptions").getData();
						model.splice(index, 1);
						that.getView().getModel("ServiceOptions").refresh(true);
					}
				}
			});
		},

		radioSelect1: function (oEvt) {
			var Index = oEvt.getSource().getSelectedIndex();
			if (Index === 0) {
				this.byId("checkId").setEditable(true);
				this.byId("idSOSubmit").setEnabled(false);
			} else {
				this.byId("checkId").setEditable(false);
				this.byId("idSOSubmit").setEnabled(true);
				this.byId("checkId").setSelected(false);
			}
		},

		onCheck: function (oEvt) {
			if (oEvt.getSource().getSelected()) {
				this.byId("idSOSubmit").setEnabled(true);
			} else {
				this.byId("idSOSubmit").setEnabled(false);
			}
		},

		press1: function () {
			var link = this.byId("press1Id");
			var oPopover = new sap.m.Popover({
				title: " This data field is dependent on the business of the merchant ",
				contentWidth: "50rem"
			});
			oPopover.openBy(link);
		},

		press2: function () {
			var link = this.byId("press2Id");
			var oPopover = new sap.m.Popover({
				title: " This field defines Payee UPI Id (Virtual Payment Address)",
				contentWidth: "50rem"
			});
			oPopover.openBy(link);
		},

		press3: function () {
			var link = this.byId("press3Id");
			var oPopover = new sap.m.Popover({
				title: "This field defines Payee name associated with UPI ID (VPA).Payee name should be same as the merchant name used during registration of VPA with the bank",
				placement: "Bottom",
				contentWidth: "100rem"
			});
			oPopover.openBy(link);
		},

		press4: function () {
			var link = this.byId("press4Id");
			var oPopover = new sap.m.Popover({
				title: "This is the code assigned by acquiring bank to the merchant",
				contentWidth: "50rem"
			});
			oPopover.openBy(link);
		},

		press5: function () {
			var link = this.byId("press5Id");
			var oPopover = new sap.m.Popover({
				title: "Depends upon the mode of sharing the QR code with the customer, like, from an Application, Point of Sales machine, etc",
				placement: "Bottom",
				contentWidth: "80rem"
			});
			oPopover.openBy(link);
		},

		press6: function () {
			var link = this.byId("press6Id");
			var oPopover = new sap.m.Popover({
				title: "This defines expiry time, post what time payments are not be accepted",
				placement: "Left",
				contentWidth: "50rem"
			});
			oPopover.openBy(link);
		},

		press7: function () {
			var link = this.byId("press7Id");
			var oPopover = new sap.m.Popover({
				title: "Basis selection – Invoice Value / Balance amount will be considered as Amount Payable in DQR",
				placement: "Left",
				contentWidth: "50rem"
			});
			oPopover.openBy(link);
		},

		onPrivacyNotice: function () {
			sap.m.URLHelper.redirect("excel/Privacy_Notice_EY_DigiGST_QR_Code.pdf", true);
		},

		//// Amount Ediatable 12-04-21///
		on1Radio: function (oEvt) {
			var Index = oEvt.getSource().getSelectedIndex();
			if (Index === 0) {
				this.byId("RB32").setEditable(true);
			} else {
				this.byId("RB32").setEditable(false);
				this.byId("payId").setVisible(false);
				this.byId("payId1").setVisible(false);
				this.byId("saveID").setEnabled(false);
				this.byId("RB32").setSelectedIndex(-1);
			}

			if (this.Status.hdr.status === "S") {
				this.byId("saveID").setEnabled(true);
				this.byId("RB32").setEditable(true);
				if (this.Status.resp.data.identifier === "percent") {
					this.byId("payId1").setValue(this.Status.resp.data.value);
					this.byId("payId").setValue("");
					this.byId("RB32").setSelectedIndex(1);
					this.byId("payId1").setVisible(true);
				} else {
					this.byId("payId").setValue(this.Status.resp.data.value);
					this.byId("payId1").setValue("");
					this.byId("RB32").setSelectedIndex(0);
					this.byId("payId").setVisible(true);
				}
			}
		},

		on2Radio: function (oEvt) {
			var Index = oEvt.getSource().getSelectedIndex();
			this.byId("saveID").setEnabled(true);
			if (Index === 0) {
				this.byId("payId").setVisible(true);
				this.byId("payId1").setVisible(false);
			} else {
				this.byId("payId").setVisible(false);
				this.byId("payId1").setVisible(true);
			}
		},

		onSubmit1: function () {
			var option = this.byId("RB32").getSelectedIndex();
			var option1 = this.byId("RB31").getSelectedIndex();
			var val, identifier, mam;
			if (option === 0) {
				val = this.byId("payId").getValue();
				identifier = "amount";
			} else if (option === 1) {
				val = this.byId("payId1").getValue();
				identifier = "percent";
			}

			if (option1 === 0) {
				mam = "Y";
			} else {
				mam = "N";
			}

			var reg = /^[0-9]*\.?[0-9]*$/;
			if (!val.match(reg) || val === "0") {
				MessageBox.error("Zero or negative values are not allowed");
				return;
			}

			if (val === "") {
				MessageBox.error("Please Enter the Amount or Percentage");
				return;
			}

			var val2 = val.toString();
			var val1 = val2.split(".");
			if (val1[1] !== undefined) {
				if (val1[1] !== "" && val1[1].length > 2) {
					MessageBox.error("More than 2 decimals are not allowed");
					return;
				}
			}

			var val12 = this.byId("payId1").getValue();
			if (identifier === "percent") {
				if (Number(val12) > 100) {
					MessageBox.error("Value can't be more than 100%");
					return;
				}
			}

			var Request = {
				"req": {
					"entityId": $.sap.entityID,
					"identifier": identifier,
					"value": val,
					"isMamOpted": mam
				}
			};

			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList1 = "/aspsapapi/saveB2CQRAmtConfigParams.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList1,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (JSON.parse(data).hdr.status === "S") {
						MessageBox.success(JSON.parse(data).resp);
						that.defultBind1();
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

	});
});