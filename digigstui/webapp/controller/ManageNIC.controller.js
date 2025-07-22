sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, Filter, FilterOperator, MessageBox) {
	"use strict";

	var vCount = 10;
	return BaseController.extend("com.ey.digigst.controller.ManageNIC", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.ManageNIC
		 */
		onInit: function () {

		},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.ey.digigst.view.ManageNIC
		 */
		onAfterRendering: function () {
			
			var hash = this.getRouter().getHashChanger().getHash();
			if (hash === "Others/ManageNIC" && this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this.nicCredentialTabl();
			}
		},

		nicCredentialTabl: function (vPageNo) {
			
			var onicCrdMdl = new JSONModel(),
				onicPath = "/aspsapapi/getNICUsers.do",
				that = this,
				searchInfo = {
					"hdr": {
						"pageNum": (vPageNo ? vPageNo - 1 : 0),
						"pageSize": vCount
					},
					"req": {
						"entityId": $.sap.entityID
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: onicPath,
				contentType: "application/json",
				data: JSON.stringify(searchInfo)
			}).done(function (data, status, jqXHR) {
				// var oSrNo = data.resp;
				sap.ui.core.BusyIndicator.hide();
				if (data.hdr.status === "E") {
					MessageBox.error(data.resp);
					that.getView().byId("idTableGenNIC").setModel(new JSONModel(null), "nicCrdTabData");
				} else {
					for (var i = 0; i < data.resp.nicDetails.length; i++) {
						data.resp.nicDetails[i].edit = false;
					}
					that._nicPagination(data.hdr);
					onicCrdMdl.setData(data.resp.nicDetails);
					that.getView().byId("idTableGenNIC").setModel(onicCrdMdl, "nicCrdTabData");
					that.getView().byId("idTableGenNIC").setSelectedIndex(-1);
				}

			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		////////Pagination/////////////
		_nicPagination: function (header) {
			//  //eslint-disable-line
			var pageNumber = Math.ceil(header.totalCount / vCount);
			this.byId("txtPageNo").setText("/ " + pageNumber);
			this.byId("inPageNo").setValue(header.pageNum + 1);
			if (pageNumber === 0 || pageNumber === 1) {
				this.byId("inPageNo").setValue(pageNumber);
				this.byId("inPageNo").setEnabled(false);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnNext").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			} else if (this.byId("inPageNo").getValue() === "" || this.byId("inPageNo").getValue() === "0") {
				this.byId("inPageNo").setValue(1);
				this.byId("inPageNo").setEnabled(true);
				this.byId("btnPrev").setEnabled(false);
				this.byId("btnFirst").setEnabled(false);
			} else {
				this.byId("inPageNo").setEnabled(true);
			}
			var vPageNo = parseInt(this.byId("inPageNo").getValue(), 10);
			if (pageNumber > 1 && pageNumber !== vPageNo) {
				this.byId("btnNext").setEnabled(true);
				this.byId("btnLast").setEnabled(true);
			} else {
				this.byId("btnNext").setEnabled(false);
				this.byId("btnLast").setEnabled(false);
			}
			this.byId("btnPrev").setEnabled(vPageNo > 1 ? true : false);
			this.byId("btnFirst").setEnabled(vPageNo > 1 ? true : false);
		},

		onPressPagination: function (oEvent) {
			//  //eslint-disable-line
			var vValue = parseInt(this.byId("inPageNo").getValue(), 10),
				vIdBtn = oEvent.getSource().getId();
			if (vIdBtn.includes("btnPrev")) {
				vValue -= 1;
				if (vValue === 1) {
					this.byId("btnPrev").setEnabled(false);
				}
				if (!this.byId("btnNext").getEnabled()) {
					this.byId("btnNext").setEnabled(true);
				}
			} else if (vIdBtn.includes("btnNext")) {
				var vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
				vValue += 1;
				if (vValue > 1) {
					this.byId("btnPrev").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnNext").setEnabled(false);
				}
			} else if (vIdBtn.includes("btnFirst")) {
				vValue = 1;
				if (vValue === 1) {
					this.byId("btnFirst").setEnabled(false);
				}
				if (!this.byId("btnLast").getEnabled()) {
					this.byId("btnLast").setEnabled(true);
				}
				//this.byId("btnFirst").setEnabled(true);
				//this.byId("btnFirst").setValue(vPageNo);
			} else if (vIdBtn.includes("btnLast")) {
				vValue = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);

				if (vValue > 1) {
					this.byId("btnFirst").setEnabled(true);
				}
				if (vValue === vPageNo) {
					this.byId("btnLast").setEnabled(false);
				}
				//this.byId("btnLast").setEnabled(true);
			} else {
				vPageNo = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			}
			this.byId("inPageNo").setValue(vValue);
			this.nicCredentialTabl(vValue);
		},

		onSubmitPagination: function () {
			//  //eslint-disable-line
			var vPageNo = this.byId("inPageNo").getValue(),
				pageNumber = parseInt(this.byId("txtPageNo").getText().split(" ")[1], 10);
			vPageNo = parseInt(vPageNo, 10) > 0 ? vPageNo : 1;
			if (vPageNo > pageNumber) {
				vPageNo = pageNumber;
			}
			this.nicCredentialTabl(vPageNo);
		},

		onPrsRowChnge: function (oEvt) {

			var oModel = this.getView().byId("idTableGenNIC").getModel("nicCrdTabData"),
				vIdx = oEvt.getParameters().rowIndex,
				oData = oModel.getData().nicDetails[vIdx];

			if (vIdx != -1 && oEvt.getSource().getSelectedIndex() === -1) {
				oData.editEInv = oData.editEwb = false;
			} else if (vIdx != -1) {
				oData.editEInv = true;
				oData.editEwb = !this.byId("cbCopyNicCred").getSelected();
			}
			oModel.refresh(true);
		},

		onCopyDataInv: function (oEvt, type) {
			var obj = oEvt.getSource().getBindingContext("nicCrdTabData").getObject(),
				vStatus = this.byId("cbCopyNicCred").getSelected();

			if (type === "invUsr") {
				this.alphaNumeric(oEvt);
			} else if (type === "invPwd") {
				this.validatePassword(oEvt);
			} else {
				this.alphaNumeric(oEvt);
			}

			if (vStatus && type === "invUsr") {
				obj.ewbUserName = obj.einvUserName;
			} else if (vStatus && type === "invPwd") {
				obj.ewbPassword = obj.einvPassword;
			} else if (vStatus && type === "einvClientId") {
				obj.ewbClientId = obj.einvClientId;
			} else if (vStatus && type === "einvClientSecret") {
				obj.ewbClientSecret = obj.einvClientSecret;
			}

		},

		onSavChnges: function () {
			var that = this;
			var tab = this.getView().byId("idTableGenNIC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length === 0) {
				sap.m.MessageBox.warning("Select atleast one record");
				return;
			}

			MessageBox.alert("Do you want to Save the changes?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onSaveNIC();
					}
				}
			});
		},

		onSaveNIC: function () {
			debugger;
			var oSelectedItem = this.getView().byId("idTableGenNIC").getSelectedIndices();
			var oModelForTab1 = this.byId("idTableGenNIC").getModel("nicCrdTabData").getData(),
				vStatus = this.byId("cbCopyNicCred").getSelected(),
				that = this;

			var postData = {
				"req": {
					"nicDetails": []
				}
			};
			oSelectedItem.forEach(function (e) {
				postData.req.nicDetails.push(oModelForTab1[e])
			});
			
			var oSavePath = "/aspsapapi/updateNICUsers.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oSavePath,
					contentType: "application/json",
					data: JSON.stringify(postData)
				}).done(function (data, status, jqXHR) {
					var sts = data.hdr;
					if (sts !== undefined) {
						if (sts.status === "S") {
							MessageBox.success("Credentials has been updated successfully");
							that.nicCredentialTabl();
						} else {
							MessageBox.error("Error Occured While Saving the Changes");
						}
					} else {
						MessageBox.error("Error Occured While Saving the Changes");
					}
				}).fail(function (jqXHR, status, err) {
					MessageBox.error("Error Occured While Saving the Changes");
				});
			});
		},
		onEdit: function () {
			var that = this;
			var tab = this.getView().byId("idTableGenNIC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}

			MessageBox.alert("Do you want to make changes to the credentials?", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Information",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						that.onEditFinal();
					}
				}
			});
		},

		onEditFinal: function () {
			debugger
			var tab = this.getView().byId("idTableGenNIC");
			var sItems = tab.getSelectedIndices();
			var oJSONModel = tab.getModel("nicCrdTabData");
			var itemlist1 = oJSONModel.getProperty("/");

			for (var i = 0; i < sItems.length; i++) {
				itemlist1[sItems[i]].edit = true;
			}
			oJSONModel.setProperty("/", itemlist1)
			oJSONModel.refresh();
			for (var i = 0; i < sItems.length; i++) {
				tab.addSelectionInterval(sItems[i], sItems[i]);
			}

		},

		onCopyCredentials: function (screen) {
			var tab = this.getView().byId("idTableGenNIC");
			var sItems = tab.getSelectedIndices();
			if (sItems.length == 0) {
				sap.m.MessageBox.warning("Select at least one record");
				return;
			}
			var data = {
				"screenFlag": screen
			};
			this.getView().setModel(new JSONModel(data), "screen");
			this.screenFlag = screen;

			if (!this._oDialogCopy) {
				this._oDialogCopy = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.others.CopyNICData", this);
				this.getView().addDependent(this._oDialogCopy);
			}
			this.byId("rbgCopyType").setSelectedIndex(0);
			// this.byId("rbgCopyType1").setSelectedIndex(0);
			this._oDialogCopy.open();

		},

		onCloseDialogCopy: function (oEvent) {
			this._oDialogCopy.close();
			this.byId("rbgCopyType").setSelectedIndex(0);
			// this.byId("rbgCopyType1").setSelectedIndex(0);
		},

		onDialogCopySubmit: function (oEvent) {
			//esint-disable-line
			this._oDialogCopy.close();
			var tab = this.getView().byId("idTableGenNIC");
			var sItems = tab.getSelectedIndices();
			var oData = tab.getModel("nicCrdTabData").getProperty("/");
			var aSelectedIdx = this.byId("rbgCopyType").getSelectedIndex();
			var copyNICFlag = null;
			if (this.screenFlag === "NIC") {
				copyNICFlag = aSelectedIdx === 0 ? "E-Invoice to E-WayBill" : (aSelectedIdx === 1 ? "E-WayBill to E-Invoice" : null);
			} else {
				copyNICFlag = aSelectedIdx === 0 ? "E-Invoice to E-WayBill_IRP" : (aSelectedIdx === 1 ? "E-WayBill to E-Invoice_IRP" : null);
			}
			var Request = {
				"req": {
					"nicDetails": [],
					"copyNICFlag": copyNICFlag
				}
			};

			sItems.forEach(function (e) {
				Request.req.nicDetails.push(oData[e])
			});
			var that = this;
			sap.ui.core.BusyIndicator.show(0);
			var GstnsList = "/aspsapapi/copyNICUsers.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: GstnsList,
					contentType: "application/json",
					data: JSON.stringify(Request)
				}).done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide();
					if (data.hdr.status === "S") {
						MessageBox.success(data.resp);
						that.nicCredentialTabl();
					} else {
						MessageBox.error(data.resp);
					}
				}).fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
				});
			});
		},

		alphaNumeric: function (oEvent) {
			var value = oEvent.getSource().getValue();
			value = value.replace(/\s/g, ""); //(/[^\d]/g, "");
			oEvent.getSource().setValue(value);
		},

		alphaNumberSpecial: function (oEvent) {
			//  //eslint-disable-line
			var value = oEvent.getSource().getValue();
			if (/[^a-zA-Z0-9\-\/]/.test(value)) {
				oEvent.getSource().setValue(value.substr(0, value.length - 1));
			}
		},

		validatePassword: function (oEvent) {
			var value = oEvent.getSource().getValue();
			value = value.replace(/\s/g, ""); //(/[^\d]/g, "");
			oEvent.getSource().setValue(value);
		},

		filterGlobally: function (oEvent) {
			var sQuery = oEvent.getParameter("query");
			this._oGlobalFilter = null;

			if (sQuery) {
				this._oGlobalFilter = new Filter([
					new Filter("gstin", FilterOperator.Contains, sQuery)
				], false);
			}
			this.byId("idTableGenNIC").getBinding().filter(this._oGlobalFilter, "nicCrdTabData");
		},

		onActivateAuthToken: function (gstin, authToken) {
			//  //eslint-disable-line
			if (authToken === "Inactive") {
				this.confirmGenerateOTP(gstin);
			}
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.ey.digigst.view.ManageNIC
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.ey.digigst.view.ManageNIC
		 */
		//	onExit: function() {
		//
		//	}

	});

});