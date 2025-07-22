sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
], function (JSONModel, MessageBox, MessageToast) {
	"use strict";

	return {
		_outwardSummary: function (that) {
			that.byId("panelOutwardSupply").removeAllContent();
			var oController = this;
			var aEntity = that.byId("slOutEntity").getSelectedKeys();
			var aGstin = that.byId("slOutGstin").getSelectedKeys();
			var vFromDate = (that._formatPeriod(that.byId("outDrsPeriod").getDateValue())).substr(2, 6);
			var vToDate = (that._formatPeriod(that.byId("outDrsPeriod").getSecondDateValue())).substr(2, 6);
			if (vFromDate === "") {
				MessageToast.show("Please select Tax Period");
				return;
			}
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
				method: "POST",
				url: "/aspsapapi/gstr1Summary.do",
				data: JSON.stringify({
					"req": {
						"entityId": aEntity,
						"sgstins": aGstin,
						"fromTaxPeriod": vFromDate,
						"toTaxPeriod": vToDate
					}
				}),
				contentType: "application/json"
			}).done(function (data, status, jqXHR) {
				sap.ui.core.BusyIndicator.hide();
				var aData = oController._summaryJSON(data.resp, that);
				that.getView().setModel(new JSONModel(aData), "OutSuppSummary");
				oController._bindOutwardSupply(aData, "", that);
			}).fail(function (jqXHR, status, err) {
				sap.ui.core.BusyIndicator.hide();
			});
		},

		_summaryJSON: function (data, that) {
			var aData = [];
			var oBundle = that.getResourceBundle();
			for (var vField in data) {
				aData[vField] = [];
				for (var vCol in data[vField]) {
					var oSummJson = {
						type: oBundle.getText(vCol),
						tableSection: "",
						records: 0,
						invValue: 0,
						taxableValue: 0,
						taxPayble: 0,
						igst: 0,
						cgst: 0,
						sgst: 0,
						cess: 0,
						items: []
					};
					if (vCol === "ey") {
						if (data[vField][vCol].length > 1) {
							for (var i = 0; i < data[vField][vCol].length; i++) {
								data[vField][vCol][i].type = data[vField][vCol][i].tableSection;
								oSummJson.items.push(data[vField][vCol][i]);
								oSummJson.records += data[vField][vCol][i].records;
								oSummJson.invValue += data[vField][vCol][i].invValue;
								oSummJson.taxableValue += data[vField][vCol][i].taxableValue;
								oSummJson.taxPayble += data[vField][vCol][i].taxPayble;
								oSummJson.igst += data[vField][vCol][i].igst;
								oSummJson.cgst += data[vField][vCol][i].cgst;
								oSummJson.sgst += data[vField][vCol][i].sgst;
								oSummJson.cess += data[vField][vCol][i].cess;
							}
						} else if (data[vField][vCol].length === 1) {
							oSummJson.tableSection = data[vField][vCol][0].tableSection;
							oSummJson.records = data[vField][vCol][0].records;
							oSummJson.invValue = data[vField][vCol][0].invValue;
							oSummJson.taxableValue = data[vField][vCol][0].taxableValue;
							oSummJson.taxPayble = data[vField][vCol][0].taxPayble;
							oSummJson.igst = data[vField][vCol][0].igst;
							oSummJson.cgst = data[vField][vCol][0].cgst;
							oSummJson.sgst = data[vField][vCol][0].sgst;
							oSummJson.cess = data[vField][vCol][0].cess;
						}
					} else {
						oSummJson.tableSection = "";
						oSummJson.records = data[vField][vCol].records;
						oSummJson.invValue = data[vField][vCol].invValue;
						oSummJson.taxableValue = data[vField][vCol].taxableValue;
						oSummJson.taxPayble = data[vField][vCol].taxPayble;
						oSummJson.igst = data[vField][vCol].igst;
						oSummJson.cgst = data[vField][vCol].cgst;
						oSummJson.sgst = data[vField][vCol].sgst;
						oSummJson.cess = data[vField][vCol].cess;
					}
					aData[vField].push(oSummJson);
				}
			}
			return aData;
		},

		_bindOutwardSupply: function (data, vValue, that) {
			var oBundle = that.getOwnerComponent().getModel("i18n").getResourceBundle();
			if (vValue === "") {
				var vCount = 3;
			} else {
				vCount = 1;
			}
			for (var vField in data) {
				if (sap.ui.getCore().byId(vField)) {
					var oTable = sap.ui.getCore().byId(vField);
					oTable.setVisibleRowCount(vCount);
				} else {
					oTable = new sap.ui.table.TreeTable(vField, {
						extension: new sap.m.Toolbar({
							content: [
								new sap.m.Title({
									text: oBundle.getText(vField)
								})
								// 				new sap.m.Text({
								// 					text: "(Section " + oBundle.getText(vField + "Sec") + ")"
								// 				}),
								// 				new sap.m.ToolbarSpacer(),
								// 				new sap.ui.core.Icon({
								// 					src: "sap-icon://download",
								// 					press: this.onDownloadSummary
								// 				})
							]
						}),
						visibleRowCount: vCount,
						enableColumnReordering: false,
						expandFirstLevel: false,
						selectionMode: sap.ui.table.SelectionMode.Single,
						selectionBehavior: sap.ui.table.SelectionBehavior.RowOnly,
						toggleOpenState: function (oEvent) {
							if (oEvent.getParameters().expanded) {
								var aData = oEvent.getSource().getBinding().getModel("Summary").getData();
								var vPath = oEvent.getSource().getBinding().getPath();
								var vLength = oEvent.getSource().getVisibleRowCount() + aData[(vPath.split("/")[1])][0].items.length;
								if (vLength < 10) {
									oEvent.getSource().setVisibleRowCount(vLength);
								} else {
									oEvent.getSource().setVisibleRowCount(10);
								}
								oEvent.getSource().setProperty("expandFirstLevel", true);
							} else {
								var vKey = oEvent.getSource().getEventingParent().getHeaderToolbar().getContent()[2].getSelectedKey();
								if (vKey === "") {
									oEvent.getSource().setVisibleRowCount(3);
								} else {
									oEvent.getSource().setVisibleRowCount(1);
								}
								oEvent.getSource().setProperty("expandFirstLevel", false);
							}
						}
					}).addStyleClass("sapUiSmallMarginBottom");

					for (var vCol in data[vField][0]) {
						if (vCol === "items") {
							continue;
						}
						oTable.addColumn(new sap.ui.table.Column({
							label: new sap.m.Label({
								text: oBundle.getText(vCol),
								tooltip: oBundle.getText(vCol),
								textAlign: "Center",
								design: "Bold",
								width: "100%"
							}),
							template: vCol === "type" ? this._tableTemplate(vCol, that) : new sap.m.Text({
								text: "{Summary>" + vCol + "}"
							}),
							hAlign: (vCol === "type" || vCol === "tableSection") ? "Begin" : "End",
							visible: vCol === "tableSection" ? false : true
						}));
					}
					oTable.bindRows({
						path: "Summary>/" + vField + "/"
					});
				}
				var oModel = new JSONModel(data);
				oTable.setModel(oModel, "Summary");
				that.byId("panelOutwardSupply").addContent(oTable);
			}
		},

		_tableTemplate: function (vCol, that) {
			var oHBox = new sap.m.HBox();
			var oController = this;
			oController.that = that;
			oHBox.addItem(new sap.m.Link({
				text: "{Summary>" + vCol + "}",
				press: function (oEvent) {
					oController.onPresssSummLink(oEvent, oController.that);
				},
				visible: "{=(${Summary>" + vCol + "}==='Diff')?false:true}"
			}));
			oHBox.addItem(new sap.m.Text({
				text: "{Summary>" + vCol + "}",
				visible: "{=(${Summary>" + vCol + "}==='Diff')?true:false}"
			}));
			return oHBox;
		},

		/*===============================================================*/
		/*===============================================================*/
		/*===============================================================*/
		_filterChange: function (oEvent, that) {
			var vKey = oEvent.getSource().getSelectedKey();
			var data = this._filterData(that.getView().getModel("OutSuppSummary").getData(), vKey);
			this._bindOutwardSupply(data, vKey, that);
		},

		_filterData: function (data, vValue) {
			var aData = [];
			for (var vField in data) {
				aData[vField] = [];
				for (var i = 0; i < data[vField].length; i++) {
					if (vValue === "" || data[vField][i].type === vValue) {
						aData[vField].push(data[vField][i]);
					}
				}
			}
			return aData;
		}
	};
});