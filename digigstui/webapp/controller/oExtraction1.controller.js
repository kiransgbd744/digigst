sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.oExtraction1", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.ey.digigst.view.Extraction
		 */
		onInit: function () {
			this.getOwnerComponent().getRouter().getRoute("Extraction").attachPatternMatched(this._onRouteMatched, this);
			this.arr = [];
			this.bindarr = [];
			this.tabNameArr = [];
		},

		_onRouteMatched: function (oEvent) {
			var oName = oEvent.getParameter("name");
			var oContextPath = oEvent.getParameter("arguments").contextPath;
			if (oName == "onboarding") {
				this.getView().byId("idIconTabBar").setSelectedKey(oContextPath);
				//this.getAllAPI();
				var appClass = "VF";
				this.onChange1(appClass);
			} else {
				this.getView().byId("idIconTabBar").setSelectedKey("FileUpload");
				// this.getAllAPI();
			}
		},

		onChange: function () {
			var appClass = this.byId("appId").getSelectedKey();
			this.onChange1(appClass);
		},

		onChange1: function (appClass) {
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/ODATAPOC/TablenameSet?$filter=Applclass eq " + "'" + appClass + "'" + "&$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "TabName");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onTabSelect: function (oEvt) {
			debugger;
			//var index = oEvt.getSource().getBindingContextPath().split("/")[1];
			//this.arr1 = [];
			if (this.arr1 !== undefined && this.arr1.length !== 0) {
				this.arr.push(this.arr1.toString());
			}

			if (this.arr2 !== undefined) {
				for (var i = 0; i < this.arr2.length; i++) {
					var obj = {
						"Tabname": this.arr2[i].Tabname,
						"Fieldname": this.arr2[i].Fieldname
					};
					this.bindarr.push(obj);
				}
			}

			this.arr1 = [];
			this.arr2 = [];
			var name = oEvt.getSource().getSelectedItem().getTitle();
			/*if (!this.tabNameArr.includes(name)) {
				this.tabNameArr.push({
					"Tabname": name
				});
			}*/
			var index = -1;
			for (var h = 0; h < this.tabNameArr.length; h++) {
				if (this.tabNameArr[h].Tabname === name) {
					index = h; //Returns element position, so it exists
					break;
				}
			}
			if (index === -1) {
				this.tabNameArr.push({
					"Tabname": name
				});
			}
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/ODATAPOC/TablefieldsSet?$format=json&$filter=Tabname eq " + "'" + name + "'";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "TabName1");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		handleIconTabBarSelect: function (oEvent) {
			var key = oEvent.getParameters().selectedKey;
			if (key === "DefineMapping") {
				this.onChange();
			} else if (key === "CustomEntity") {
				this.EntityName();
				var obj = this.getPocObject();
				this.getView().setModel(new JSONModel(obj), "PostSrv");
			} else if (key === "ExtractionMetadata") {
				this.billType();
				this.appType();
			} else if (key === "SelectEvent") {

			} else if (key === "DesignExtraction") {

			} else if (key === "ExtractionResult") {
				this.extractionResult();
				this.extractionResult1();
			}
		},

		appType: function () {
			debugger;
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/ODATADIGIGST/SectionSet?$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "SectionSet");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		EntityName: function () {
			debugger;
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/oDataPostSrv/Query_CodenameSet?$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "EntityName");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressGoForCE: function () {
			var name = this.byId("ENid").getSelectedKey();
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/oDataPostSrv/Query_TabSet?$filter=Qcode eq" + "'" + name + "'" + "&$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					debugger;
					oReqWiseModel.setData(data.d.results[0]);
					oReqWiseView.setModel(oReqWiseModel, "PostSrv");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onPressClearForCE: function () {
			this.byId("ENid").setSelectedKey("");
			var oModel = this.getView().getModel("PostSrv").getData();
			oModel.Qcode = "";
			oModel.Qselect = "";
			oModel.Qfrom = "";
			oModel.Qwhere = "";
			oModel.Qrelated = "";
			oModel.Qrelationship = "";
			this.getView().getModel("PostSrv").refresh(true);
		},

		billType: function () {
			debugger;
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var that = this;
			var oReqWisePath = "/ODATAPOC/BillingTypesSet?$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "BillingType");
					/*var oModel = that.getView().getModel("PostSrv").getData();
					var data1 = data.d.results;
					oModel.Qcode = data1.Qcode;
					oModel.Qselect = data1.Qselect;
					oModel.Qfrom = data1.Qfrom;
					oModel.Qwhere = data1.Qwhere;
					that.getView().getModel("PostSrv").refresh(true);*/

				}).fail(function (jqXHR, status, err) {});
			});
		},

		extractionResult: function () {
			var that = this;
			var req = {
				"req": {
					"entityName": this.byId("TempId").getValue()
				}
			};

			var oReqWisePath = "/aspsapapi/getEinvoice.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					var data1 = JSON.stringify(data);
					that.byId("code").setValue(data1);
				}).fail(function (jqXHR, status, err) {});
			});
		},

		extractionResult1: function () {
			var that = this;
			var req = {
				"req": {
					"entityName": this.byId("TempId").getValue()
				}
			};

			var oReqWisePath = "/aspsapapi/getXmlPayload.do";
			$(document).ready(function ($) {
				$.ajax({
					method: "POST",
					url: oReqWisePath,
					contentType: "application/json",
					data: JSON.stringify(req)
				}).done(function (data, status, jqXHR) {
					var data1 = JSON.stringify(data);
					that.byId("code1").setValue(data1);
				}).fail(function (jqXHR, status, err) {});
			});
		},

		getPocObject: function () {
			return {
				"Qcode": "",
				"Qselect": "",
				"Qfrom": "",
				"Qwhere": "",
				"Qrelated": "",
				"Qrelationship": "",
				"Qfilter": "",
				"Description": ""
			};
		},

		onSubmitCode: function () {
			var oModel = this.getView().getModel("PostSrv"),
				oData = oModel.getData(),
				that = this;
			var oData1 = {
				"d": oData
			};
			debugger;
			$.ajax({
					"type": "POST",
					"url": "/oDataPostSrv/Query_TabSet",
					"data": JSON.stringify(oData1),
					"headers": {
						"Content-Type": "application/json"
							//"X-CSRF-Token": "ielhFl_BMez69naV-fAz_w=="
					}
				})
				.done(function (data, status, jqXHR) {
					sap.m.MessageBox.success("Query Saved Successfully");
					oData = that.getPocObject();
					oModel.refresh(true);
				})
				.fail(function (jqXHR, status, error) {
					debugger; //eslint-disable-line
				});
		},

		onChangeDS: function (oEvt) {
			var key = oEvt.getSource().getSelectedKey();
			if (key === "DynamicSQL") {
				this.byId("formId").setVisible(true);
				this.byId("idList").setVisible(false);
			} else {
				this.byId("formId").setVisible(false);
				this.byId("idList").setVisible(true);
				this.onChange2();
			}
		},

		onChange2: function () {
			var appClass = this.byId("eyAppId").getSelectedKey();
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			var oReqWisePath = "/ODATADIGIGST/TablenameSet?$filter=Eyapp eq " + "'" + appClass + "'" + "&$format=json";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "TabName12");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onSubmitTab: function (oEvt) {
			debugger;
			var arr = [],
				that = this;
			var tabNames = this.byId("idList").getSelectedItems();
			for (var i = 0; i < tabNames.length; i++) {
				arr.push(tabNames[i].getTitle());
			}
			var req = {
				"d": {
					"Template": "EY_SALE",
					"Tablenames": arr.toString()
				}
			};

			$.ajax({
					"type": "POST",
					"url": "/ODATAPOC/TableselectSet",
					"data": JSON.stringify(req),
					"headers": {
						"Content-Type": "application/json",
						"X-CSRF-Token": "RSWcpqnJNXmsYM6ESGYAgA=="
					}
				})
				.done(function (data, status, jqXHR) {
					sap.m.MessageBox.success("Query Saved Successfully");
					//that.byId("idList").setSelectedIndex(-1);
				})
				.fail(function (jqXHR, status, error) {
					debugger; //eslint-disable-line
				});
		},

		onSubmit: function () {
			debugger;
			var arr = [],
				that = this;
			var tabName = this.byId("idList").getSelectedItem().getTitle();
			var Attributes = this.byId("idTable").getSelectedItems();
			var token = null;
			for (var i = 0; i < Attributes.length; i++) {
				arr.push(Attributes[i].getBindingContext("TabName123").getObject().Fieldname);
			}
			var req = {
				"d": {
					"Attributes": arr.toString(),
					"Template": this.byId("DNId").getValue(),
					"Tablenames": tabName
				}
			};

			$.ajax({
					"type": "POST",
					"url": "/ODATADIGIGST/TableselectSet",
					"data": JSON.stringify(req),
					//"Content-Type": "application/json",

					"headers": {
						"Content-Type": "application/json",
						"X-CSRF-Token": "Fetch"
					}
				})
				.done(function (data, status, jqXHR) {
					sap.m.MessageBox.success("Query Saved Successfully");
					//that.byId("idTable").getSelectedItems(-1);
				})
				.fail(function (jqXHR, status, error) {
					debugger; //eslint-disable-line
				});
		},

		onListPress: function (oEvt) {
			//var index = oEvt.getSource().getBindingContextPath().split("/")[1];
			var name = oEvt.getSource().getSelectedItem().getTitle();
			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			//var oReqWisePath = "/ODATADIGIGST/TablefieldsSet?$filter=Tabname eq " + "'" + name + "'" + "?$format=json?$format=json";
			//var oReqWisePath = "/ODATADIGIGST/TablefieldsSet?$filter= Tabname eq" + "'" + name + "'" + "&?$format=json";
			var oReqWisePath = "/ODATADIGIGST/TablefieldsSet?$format=json&$filter=Tabname eq " + "'" + name + "'";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel, "TabName123");
				}).fail(function (jqXHR, status, err) {});
			});
		},

		onTabPress: function (oEvt) {
			debugger;
			//var sel = oEvt.getSource().getSelectedItems()[0].getBindingContext("TabName1").getObject();
			this.arr1 = [];
			this.arr2 = [];
			var Attributes = oEvt.getSource().getSelectedItems();
			for (var i = 0; i < Attributes.length; i++) {
				this.obj = {
					"Tabname": Attributes[i].getBindingContext("TabName1").getObject().Tabname,
					"Fieldname": Attributes[i].getBindingContext("TabName1").getObject().Fieldname
				};
				this.arr2.push(this.obj);
				this.arr1.push(Attributes[i].getBindingContext("TabName1").getObject().Tabname + "~" + Attributes[i].getBindingContext("TabName1")
					.getObject().Fieldname);
			}
		},

		onNext: function () {
			if (this.arr1 !== undefined && this.arr1.length !== 0) {
				this.arr.push(this.arr1.toString());
			}

			if (this.arr2 !== undefined) {
				for (var i = 0; i < this.arr2.length; i++) {
					var obj = {
						"Tabname": this.arr2[i].Tabname,
						"Fieldname": this.arr2[i].Fieldname
					};
					this.bindarr.push(obj);
				}
			}

			var oReqWiseModel = new JSONModel();
			var oReqWiseView = this.getView();
			oReqWiseModel.setData(this.tabNameArr);
			oReqWiseView.setModel(oReqWiseModel, "TabDet");

			this.arr1 = [];
			this.arr2 = [];
			this.byId("idSplitDtl2A").setVisible(false);
			this.byId("idSplit").setVisible(true);

			var oReqWiseModel5 = new JSONModel();
			var oReqWisePath = "/ODATAPOC/TablefieldsSet?$format=json&$filter=Tabname eq " + "'" + this.tabNameArr[0].Tabname + "'";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel5.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel5, "FirstTabName");
				}).fail(function (jqXHR, status, err) {});
			});

			var oReqWiseModel6 = new JSONModel();
			var oReqWisePath1 = "/ODATAPOC/TablefieldsSet?$format=json&$filter=Tabname eq " + "'" + this.tabNameArr[1].Tabname + "'";
			$(document).ready(function ($) {
				$.ajax({
					method: "GET",
					url: oReqWisePath1,
					contentType: "application/json"
						//data: JSON.stringify(payload)
				}).done(function (data, status, jqXHR) {
					oReqWiseModel6.setData(data.d.results);
					oReqWiseView.setModel(oReqWiseModel6, "secondTabName");
				}).fail(function (jqXHR, status, err) {});
			});

			this.data = [{
				"TN1": "",
				"Where": "",
				"TN2": ""
			}];
			this.oReqWiseModel1 = new JSONModel();
			this.oReqWiseModel1.setData(this.data);
			oReqWiseView.setModel(this.oReqWiseModel1, "TabDetails");
		},

		onPressBack: function () {
			this.byId("idSplitDtl2A").setVisible(true);
			this.byId("idSplit").setVisible(false);
		},

		onPressAddRecords: function () {
			var newRec = {
				"TN1": "",
				"Where": "",
				"TN2": ""
			};
			this.data.push(newRec);
			this.oReqWiseModel1.setData(this.data);
			this.getView().setModel(this.oReqWiseModel1, "TabDetails");
			//this.getView().setModel(stateData, "getAllState");
			this.getView().getModel("TabDetails").refresh(true);
		},

		onNext1: function () {
			debugger;
			var arr = [];
			var data = this.getView().getModel("TabDetails").getData();
			for (var i = 0; i < data.length; i++) {
				arr.push(this.tabNameArr[1].Tabname + "~" + data[i].TN2 + " " + data[i].Where + " " + this.tabNameArr[0].Tabname + "~" + data[
					i].TN1);
			}
			var str = "on (" + arr.toString() + ")";
			var join = this.byId("JoinId").getSelectedKey();
			var final = this.tabNameArr[0].Tabname + " " + join + " " + this.tabNameArr[1].Tabname + " " + str;
			var oData1 = {
				"d": {
					"Qcode": this.byId("EntityId").getValue(),
					"Qselect": this.arr.toString(),
					"Qfrom": final,
					"Qwhere": this.byId("whereId").getValue(),
					"Qrelated": this.byId("RelEyEntity").getValue(),
					"Qrelationship": this.byId("RelToEyEntity").getValue()
				}
			};
			debugger;
			$.ajax({
					"type": "POST",
					"url": "/oDataPostSrv/Query_TabSet",
					"data": JSON.stringify(oData1),
					"headers": {
						"Content-Type": "application/json"
							//"X-CSRF-Token": "ielhFl_BMez69naV-fAz_w=="
					}
				})
				.done(function (data, status, jqXHR) {
					sap.m.MessageBox.success("Query Saved Successfully");
				})
				.fail(function (jqXHR, status, error) {
					debugger; //eslint-disable-line
				});
		}

	});

});