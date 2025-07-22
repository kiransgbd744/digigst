sap.ui.define([
	"sap/m/library",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/model/json/JSONModel"
], function(MobileLibrary, MessageBox, MessageToast, Filter, FilterOperator, JSONModel) {
	"use strict";

	return {
		_addColumns: function(oData, oView) {
			var aColumn = [];
			var oBundle = oView.getOwnerComponent().getModel("i18n").getResourceBundle();
			for (var vField in oData[0]) {
				if (vField === "section" || vField === "tableSection") {
					continue;
				}
				var oColumn = new sap.m.Column({
					header: [
						new sap.m.Label({
							text: oBundle.getText(vField)
						})
					]
				});
				if (vField !== "Type") {
					oColumn.setHAlign("End");
				}
				if (vField === "records") {
					oColumn.setWidth("5rem");
				}
				aColumn.push(oColumn);
			}
			return aColumn;
		},

		_addTemplates: function(vTab, oData, oView) {
			var oColumnListItem = new sap.m.ColumnListItem();
			for (var vField in oData[0]) {
				if (vField === "section" || vField === "tableSection") {
					continue;
				}
				if (vTab === "Diff" && vField === "Type") {
					var oHBox = new sap.m.HBox();
					var oIcon = new sap.ui.core.Icon({
						src: "sap-icon://{=${" + vField + "}==='+'?'add':'less'}",
						visible: "{=(${" + vField + "}==='+' || ${" + vField + "}==='-')?true:false}",
						press: function(oEvent) {
							var vId = oEvent.getSource().getId().split("-")[1];
							var oObject = oEvent.getSource().getBindingContext().getObject();
							if (oObject.Type === "+") {
								oObject.Type = "-";
								var aFilter = new Filter("Type", FilterOperator.Contains, "");
							} else if (oObject.Type === "-") {
								oObject.Type = "+";
								aFilter = new Filter("Type", FilterOperator.EQ, "+");
							}
							oEvent.getSource().getBindingContext().getModel().refresh(true);
							sap.ui.getCore().byId(vId).getBinding("items").filter([aFilter]);
						}
					});
					oHBox.addItem(oIcon);
					oHBox.addItem(
						new sap.m.Text({
							text: "{" + vField + "}",
							visible: "{=(${" + vField + "}==='+' || ${" + vField + "}==='-')?false:true}"
						})
					);
					oColumnListItem.addCell(oHBox);
					continue;
				} else {
					oColumnListItem.addCell(
						new sap.m.Text({
							text: "{" + vField + "}"
						})
					);
				}
			}
			return oColumnListItem;
		},

		_addHeaderToolbar: function(vTab, vField, oData, oView) {
			var vSection = "";
			var oBundle = oView.getOwnerComponent().getModel("i18n").getResourceBundle();
			var oToolbar = new sap.m.Toolbar();
			oToolbar.addContent(
				new sap.m.Title({
					text: oBundle.getText(vField)
				})
			);
			for (var i = 0; i < oData.length; i++) {
				vSection += oData[i].tableSection;
			}
			oToolbar.addContent(
				new sap.m.Text({
					text: " (Section " + vSection + ")"
				}).addStyleClass("sapUiTinyMarginEnd")
			);
			oToolbar.addContent(new sap.m.ToolbarSpacer());
			oToolbar.addContent(
				new sap.m.Button({
					icon: "sap-icon://download"
				})
			);
			return oToolbar;
		},

		_SummaryEY: function(oData, oView) {
			for (var i = 0; i < oData.length; i++) {
				var oEYData = [],
					oFilter = "";
				oEYData = $.extend(true, {}, oData[i]);
				var oConsData = {
					"SlNo": "",
					"GST": "",
					"items": [{}]
				};
				for (var vField in oEYData.items[0]) {
					oConsData.items[0][vField] = "";
				}

				if (oData[i].items.length > 1) {
					oConsData.SlNo = "C";
					oConsData.GST = oData[i].GST;
					oConsData.items[0].Section = "All";

					for (var j = 0; j < oData[i].items.length; j++) {
						oConsData.items[0].TaxableValue = (oConsData.items[0].TaxableValue === "" ? 0 : parseInt(oConsData.items[0].TaxableValue, 10)) + (
							oData[i].items[j].TaxableValue === "" ? 0 : parseInt(oData[i].items[j].TaxableValue, 10));
						oConsData.items[0].TaxPayble = (oConsData.items[0].TaxPayble === "" ? 0 : parseInt(oConsData.items[0].TaxPayble, 10)) + (oData[i].items[
							j].TaxPayble === "" ? 0 : parseInt(oData[i].items[j].TaxPayble, 10));
						oConsData.items[0].InvValue = (oConsData.items[0].InvValue === "" ? 0 : parseInt(oConsData.items[0].InvValue, 10)) + (oData[i].items[
							j].InvValue === "" ? 0 : parseInt(oData[i].items[j].InvValue, 10));

						oConsData.items[0].IGST = (oConsData.items[0].IGST === "" ? 0 : parseInt(oConsData.items[0].IGST, 10)) + (oData[i].items[j].IGST ===
							"" ? 0 : parseInt(oData[i].items[j].IGST, 10));
						oConsData.items[0].CGST = (oConsData.items[0].CGST === "" ? 0 : parseInt(oConsData.items[0].CGST, 10)) + (oData[i].items[j].CGST ===
							"" ? 0 : parseInt(oData[i].items[j].CGST, 10));
						oConsData.items[0].SGST = (oConsData.items[0].SGST === "" ? 0 : parseInt(oConsData.items[0].SGST, 10)) + (oData[i].items[j].SGST ===
							"" ? 0 : parseInt(oData[i].items[j].SGST, 10));
						oConsData.items[0].CESS = (oConsData.items[0].CESS === "" ? 0 : parseInt(oConsData.items[0].CESS, 10)) + (oData[i].items[j].CESS ===
							"" ? 0 : parseInt(oData[i].items[j].CESS, 10));
					}
					oEYData.items.unshift(oConsData.items[0]);
					oFilter = new Filter("Section", FilterOperator.EQ, "All");
				}
				var oTable = new sap.m.Table({
					backgroundDesign: "Transparent",
					headerToolbar: this._addHeaderToolbar("EY", oEYData, oView),
					columns: this._addColumns(oEYData, oView),
					items: {
						path: "/",
						template: this._addTemplates("EY", oEYData, oView)
					}
				}).addStyleClass("sapUiSmallMarginBottom");
				oTable.setModel(new JSONModel(oEYData.items));
				oView.byId("idSummaryPanel").addContent(oTable);
				if (oFilter) {
					oTable.getBinding("items").filter([oFilter]);
				}
			}
		},

		_summaryGSTN: function(oData, oView) {
		    oView.byId("idSummaryGSTN").removeAllContent();
			for (var i = 0; i < oData.length; i++) {
				var oGSTNData = [];
				for (var vField in oData[i]) {
					oGSTNData = $.extend(true, [], oData[i][vField]);
				}
				var oTable = new sap.m.Table({
					backgroundDesign: "Transparent",
					headerToolbar: this._addHeaderToolbar("GSTN", vField, oGSTNData, oView),
					columns: this._addColumns(oGSTNData, oView),
					items: {
						path: "/",
						template: this._addTemplates("GSTN", oGSTNData, oView)
					}
				}).addStyleClass("sapUiSmallMarginBottom");
				oTable.setModel(new JSONModel(oGSTNData));
				oView.byId("idSummaryGSTN").addContent(oTable);
			}
		},

		_summaryDiff: function(oData, oView) {
			var oBundle = oView.getOwnerComponent().getModel("i18n").getResourceBundle();
			oView.byId("idDiffPanel").removeAllContent();
			for (var i = 0; i < oData.length; i++) {
				var oDiffData = [],
					vId = oData[i].Return + "Diff";
				oDiffData = $.extend(true, {}, oData[i]);

				if (!sap.ui.getCore().byId(vId)) {
				// 	var vSection = "";
				// 	for (var j = 0; i < oData.length; i++) {
				// 		vSection += oData.items[j].tableSection;
				// 	}
					var oTable = new sap.m.Table(vId, {
						backgroundDesign: "Transparent",
						headerToolbar: new sap.m.Toolbar({
							content: [
							    new sap.m.Title({
									text: oBundle.getText(oData[i].Return)
								}),
								new sap.m.Text({
									text: "(Section " + oData[i].section + ")"
								})
							]
						}),
						columns: this._addColumns(oDiffData.items, oView),
						items: {
							path: "/",
							template: this._addTemplates("Diff", oDiffData.items, oView)
						}
					}).addStyleClass("sapUiSmallMarginBottom");
				} else {
					oTable = sap.ui.getCore().byId(vId);
				}
				oTable.setModel(new JSONModel(oDiffData.items));
				oView.byId("idDiffPanel").addContent(oTable);

				var oFilter = new Filter("Type", FilterOperator.EQ, "+");
				oTable.getBinding("items").filter([oFilter]);
			}
		}
	};
});