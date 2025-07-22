sap.ui.define([
	"com/ey/digigst/util/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/Fragment",
	"sap/m/MessageBox"
], function (Controller, JSONModel, Fragment, MessageBox) {
	"use strict";

	return Controller.extend("com.ey.digigst.controller.SupplierIMS", {
		onInit: function () {
			var obj = {
				"title": "Supplier IMS Get Call",
				"navBack": false,
				"btnSegment": "IMSGetCall",
				"btnSubSegment": "summary",
				"imsSummTab": "entity",
				"imsSummEntityFullScreen": false,
				"imsSummEntityRows": 10,
				"imsSummGstnFullScreen": false,
				"imsSummGstnRows": 10,
				"getIMSFullScreen": false,
				"getImsCallRows": 10,
				"gstnSummDate": this._getDate(),
				"pageNo": 0,
				"pgTotal": 0,
				"pgSize": 10,
				"ePageNo": false,
				"bFirst": false,
				"bPrev": false,
				"bNext": false,
				"bLast": false,
			};
			this.getView().setModel(new JSONModel(obj), "ViewProperty");
			this.setReadonly("dSumEntityPeriod");
			this.setReadonly("dSumGstnPeriod");
			this.setReadonly("dtFromDate");
			this.setReadonly("dtToDate");
			this._bindFilterModel();
		},

		onAfterRendering: function () {
			if (this.glbEntityId !== $.sap.entityID) {
				this.glbEntityId = $.sap.entityID;
				this._bindFilterModel();
				this._getAllFy();
			} else {
				var oData = this.getView().getModel("oFyModel").getProperty("/finYears");
				this._bindFilterModel();
				this.getView().getModel("FilterModel").setProperty("/getTaxPeriod", oData[0].fy);
				this.onSegmentBtnChange();
			}
		},

		_bindFilterModel: function () {
			var today = new Date();
			// 	date = new Date();
			// date.setMonth(date.getMonth() - 1);
			var obj = {
				"getGstins": [],
				"getTaxPeriod": null,
				"sumEntityGstin": [],
				"sumEntityPeriod": (today.getMonth() + 1).toString().padStart(2, 0) + today.getFullYear(),
				"sumEntityRetType": "",
				"sumEntityTabType": [],
				"sumGstnGstin": [],
				"sumGstnPeriod": null,
				"recordGstin": [],
				"recordFrPeriod": today,
				"recordToPeriod": today,
				"recordRetType": [],
				"recordTabType": [],
				"recordActionType": 0,
				"maxDate": today
			};
			this.getView().setModel(new JSONModel(obj), "FilterModel");
		},

		_getAllFy: function () {
			sap.ui.core.BusyIndicator.show(0);
			$.get("/aspsapapi/getAllFy.do")
				.done(function (data, status, jqXHR) {
					var oModel = new JSONModel([]);
					if (data.hdr.status === "S") {
						oModel.setProperty("/", data.resp);
						this.getView().getModel("FilterModel").setProperty("/getGstins", []);
						this.getView().getModel("FilterModel").setProperty("/getTaxPeriod", data.resp.finYears[0].fy);
						this.onSegmentBtnChange();
					}
					this.getView().setModel(oModel, "oFyModel");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "oFyModel");
				}.bind(this));
		},

		onSegmentBtnChange: function () {
			var oModel = this.getView().getModel("ViewProperty"),
				btn = oModel.getProperty("/btnSegment");

			oModel.setProperty("/navBack", false);
			switch (btn) {
			case "IMSGetCall":
				oModel.setProperty("/title", "Supplier IMS Get call");
				oModel.setProperty("/btnSubSegment", "summary");
				this.onSearchSuppImsGetCall();
				break;
			case "IMSSummary":
				oModel.setProperty("/title", "Supplier IMS Summary");
				oModel.setProperty("/btnSubSegment", "summary");
				oModel.setProperty("/imsSummTab", "entity");
				this.onSearchSummary();
				break;
			case "IMSRecords":
				oModel.setProperty("/title", "Supplier IMS Records View");
				this.onPressImsRecords();
				break;
			}
			oModel.refresh(true);
		},

		/************************************************************************
		 * Supplier IMS Get Call
		 ************************************************************************/
		_gstr1DefaultValue: function () {
			var aMonth = this._getMonth(),
				obj = {};
			aMonth.forEach(function (m) {
				obj[m] = false;
			});
			this.getView().setModel(new JSONModel(obj), "ImsProperty");
		},

		onClearSuppImsGetCall: function () {
			var oFyData = this.getView().getModel("oFyModel").getProperty("/finYears"),
				oModel = this.getView().getModel("FilterModel");

			oModel.setProperty("/getGstins", []);
			oModel.setProperty("/getTaxPeriod", oFyData[0].fy);
			oModel.refresh(true);
			this.onSearchSuppImsGetCall();
		},

		onSearchSuppImsGetCall: function () {
			this._gstr1DefaultValue();
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"fy": oFilterData.getTaxPeriod,
						"gstins": oFilterData.getGstins
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			this.byId("tabImsGetCall").setSelectedIndex(-1);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/getSupplierImsStatus.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oDetData = this._formatSuppImsSummaryData(data.resp);
					this.getView().setModel(new JSONModel(oDetData), "SupplierImsGetCall");
					this.getView().setModel(new JSONModel({
						"getCallFreqOpted": data.resp.getCallFreqOpted,
						"noOfTaxPeriodsForGetCall": data.resp.noOfTaxPeriodsForGetCall
					}), "CallFrequencyOpted");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide();
					this.getView().setModel(new JSONModel([]), "SupplierImsGetCall");
				}.bind(this));
		},

		_formatSuppImsSummaryData: function (data) {
			return data.apiGstinDetails.map(function (item) {
				var obj = {
					gstin: item.gstin,
					authStatus: item.authStatus,
					stateName: item.stateName,
					registrationType: item.registrationType
				};

				item.taxPeriodDetails.forEach(function (el) {
					var month = this._getMonth(+el.taxPeriod.substr(0, 2) - 1);
					obj[month + 'InitiatedOn'] = el.initiatedOn;
					obj[month + 'Status'] = el.status;
					obj[month + 'Section'] = [];
					obj[month + "Check"] = false;

					var sections = [{
						data: el.failedSections,
						state: "Error"
					}, {
						data: el.successSections,
						state: "Success"
					}, {
						data: el.inProgressSections,
						state: "Progress"
					}, {
						data: el.successWithNoDataSections,
						state: "None"
					}];

					sections.forEach(function (section) {
						if (section.data) {
							var sectionArray = section.data.split(' ');
							sectionArray.forEach(function (sec) {
								obj[month + 'Section'].push({
									section: sec,
									state: section.state
								});
							});
						}
					});
				}, this);

				return obj;
			}, this);
		},

		onRowSelectionIms: function (oEvent) {
			var oModel = this.getView().getModel("SupplierImsGetCall"),
				oPropModel = this.getView().getModel("ImsProperty"),
				oPropData = oPropModel.getProperty("/"),
				oData = oModel.getProperty("/"),
				vRowIdx = oEvent.getParameter("rowIndex"),
				aMonth = this._getMonth();

			if (oEvent.getParameters().selectAll || vRowIdx === -1) {
				var flag = oEvent.getParameters().selectAll || false;
				aMonth.forEach(function (m) {
					oPropData[m] = flag;
					oData.forEach(function (el) {
						el[m + "Check"] = flag;
					});
				});
			} else {
				var aIndex = this.byId("tabImsGetCall").getSelectedIndices(),
					flag = aIndex.includes(vRowIdx);
				aMonth.forEach(function (m) {
					oData[vRowIdx][m + "Check"] = flag;
					oPropData[m] = (aIndex.length === oData.length);
				});
			}
			oPropModel.refresh(true);
			oModel.refresh(true);
		},

		onSelectColMonth: function (oEvent, month) {
			var oModel = this.getView().getModel("SupplierImsGetCall"),
				oData = oModel.getProperty("/"),
				flag = oEvent.getParameter("selected");

			oData.forEach(function (el) {
				el[month + "Check"] = flag;
			});
			oModel.refresh(true);
		},

		onSelectMonth: function (month) {
			var oData = this.getView().getModel("SupplierImsGetCall").getProperty("/"),
				oPropModel = this.getView().getModel("ImsProperty"),
				flag = true;

			oData.forEach(function (el) {
				flag = flag && el[month + "Check"];
			});
			oPropModel.setProperty("/" + month, flag);
			oPropModel.refresh(true);
		},

		_getMonth: function (month) {
			var aMonth = ['jan', 'feb', 'mar', 'apr', 'may', 'june', 'july', 'aug', 'sep', 'oct', 'nov', 'dec'];
			if (typeof (month) === 'number') {
				return aMonth[month];
			}
			return aMonth;
		},

		_formatSection: function (sections) {
			if (!sections || !sections.length) {
				return '<div></div>';
			}
			var htmlContent = "<div style='width: 100%; white-space: normal; overflow-wrap: break-word; display: block;'>";
			sections.forEach(function (section) {
				var tooltip = "";
				switch (section.state) {
				case "Success":
					var color = "#228B22";
					break;
				case "Error":
					color = "#B22222";
					break;
				case "Progress":
					color = "#9400D3";
					break;
				default:
					color = "#FF8C00";
					tooltip = "No Data";
				}
				htmlContent += "<span style='color:" + color + ";font-size:14px;margin-right:5px;white-space: nowrap;' title='" + tooltip + "'>" +
					section.section + "</span>";
			});
			htmlContent += "</div>";
			return htmlContent;
		},

		onPressGetSupplierIMS: function () {
			MessageBox.confirm('Do you want to initiate Get Supplier IMS Records?', {
				icon: MessageBox.Icon.QUESTION,
				title: "Confirmation",
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === MessageBox.Action.YES) {
						this._getCallSupplierIMS();
					}
				}.bind(this)
			});
		},

		_getCallSupplierIMS: function () {
			// Get the payload
			var payload = this._getPayload();

			// Check if there are any periods selected
			if (payload.req.length === 0) {
				MessageBox.information("Please select at least one Period.");
				return;
			}

			// Create the dialog if it doesn't exist
			if (!this._oDialogbulkSave) {
				this._oDialogbulkSave = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.ims.BulkSave", this);
				this.getView().addDependent(this._oDialogbulkSave);
			}

			// Set the view to busy state
			sap.ui.core.BusyIndicator.show(0);

			// Make the AJAX call
			$.ajax({
					method: "POST",
					url: "/aspsapapi/initiateSupplierImsGetCall.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					sap.ui.core.BusyIndicator.hide(); // Reset busy state
					if (data.hdr.status === "S") {
						// If the call is successful, set the model and open the dialog
						this.getView().byId("bulkSaveID").setModel(new JSONModel(data.resp), "BulkGstinSaveModel");
						this._oDialogbulkSave.open();
					} else {
						// Show an error message if the status is not successful
						MessageBox.error("Get Status Failed: " + (data.resp.message || "Unknown error occurred."));
					}
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					sap.ui.core.BusyIndicator.hide(); // Reset busy state
					// Show an error message if the AJAX call fails
					MessageBox.error("Error occurred while initiating the call: " + (err || "Unknown error."));
				}.bind(this));
		},

		onCloseDialogImsGetCall: function () {
			this._oDialogbulkSave.close();
			this.onSearchSuppImsGetCall();
		},

		_getPayload: function () {
			var data = this.getView().getModel("SupplierImsGetCall").getData();
			var oFilterData = this.getView().getModel("FilterModel").getProperty("/");
			// Create the payload
			var payload = {
				"req": [],
				"fy": oFilterData.getTaxPeriod // Set the financial year as needed
			};

			// Iterate through the data to build the payload
			data.forEach(function (item) {
				var taxPeriods = [];

				// Check which months have check set to true
				if (item.aprCheck) taxPeriods.push("04"); // April
				if (item.mayCheck) taxPeriods.push("05"); // May
				if (item.juneCheck) taxPeriods.push("06"); // June
				if (item.julyCheck) taxPeriods.push("07"); // July
				if (item.augCheck) taxPeriods.push("08"); // August
				if (item.sepCheck) taxPeriods.push("09"); // September
				if (item.octCheck) taxPeriods.push("10"); // October
				if (item.novCheck) taxPeriods.push("11"); // November
				if (item.decCheck) taxPeriods.push("12"); // December
				if (item.janCheck) taxPeriods.push("01"); // January
				if (item.febCheck) taxPeriods.push("02"); // February
				if (item.marCheck) taxPeriods.push("03"); // March

				// Only add to the payload if there are tax periods
				if (taxPeriods.length > 0) {
					payload.req.push({
						"gstin": item.gstin,
						"taxPeriod": taxPeriods
					});
				}
			});

			return payload;
		},

		onDownloadGetCallReport: function (key) {
			switch (key) {
			case "tableData":
				this._downloadTableData();
				break;
			default:
				var oData = this.getView().getModel("SupplierImsGetCall").getProperty("/"),
					aMonth = this._getMonth(),
					payload = {
						"type": "imsGetCall",
						"finYear": this.getView().getModel('FilterModel').getProperty('/getTaxPeriod'),
						"req": [],
						"reportTypes": (key === 'All' ? ["A", "R", "P", "N"] : [key])
					};
				oData.forEach(function (el) {
					var obj = {
						"gstin": el.gstin,
						"taxPeriod": []
					};
					aMonth.forEach(function (m, idx) {
						if (el[m + "Check"]) {
							obj.taxPeriod.push((idx + 1).toString().padStart(2, 0));
						}
					});
					if (obj.taxPeriod.length) {
						payload.req.push(obj);
					}
				});
				if (!payload.req.length) {
					MessageBox.information("Please select at least one Period.");
					return;
				}
				this.reportDownload(payload, "/aspsapapi/getSupplierImsConsolidatedReport.do");
			}
		},

		_downloadTableData: function () {
			var oData = this.getView().getModel("SupplierImsGetCall").getProperty("/"),
				oFilter = this.getView().getModel('FilterModel').getProperty('/'),
				headers = [
					[
						'GSTIN', 'April', 'May', 'June', 'July', 'August', 'September',
						'October', 'November', 'December', 'January', 'February', 'March'
					]
				];
			var mappedData = oData.map(function (item) {
				return [
					item.gstin,
					this._formatImsSummaryData(item.aprStatus, item.aprInitiatedOn),
					this._formatImsSummaryData(item.mayStatus, item.mayInitiatedOn),
					this._formatImsSummaryData(item.juneStatus, item.juneInitiatedOn),
					this._formatImsSummaryData(item.julyStatus, item.julyInitiatedOn),
					this._formatImsSummaryData(item.augStatus, item.augInitiatedOn),
					this._formatImsSummaryData(item.sepStatus, item.sepInitiatedOn),
					this._formatImsSummaryData(item.octStatus, item.octInitiatedOn),
					this._formatImsSummaryData(item.novStatus, item.novInitiatedOn),
					this._formatImsSummaryData(item.decStatus, item.decInitiatedOn),
					this._formatImsSummaryData(item.janStatus, item.janInitiatedOn),
					this._formatImsSummaryData(item.febStatus, item.febInitiatedOn),
					this._formatImsSummaryData(item.marStatus, item.marInitiatedOn)
				];
			}.bind(this));
			this._downloadGetCallExcelFile(headers, mappedData);
		},

		_downloadGetCallExcelFile: function (headers, data) {
			var workbook = new ExcelJS.Workbook(),
				sheet = workbook.addWorksheet('Supplier IMS Get Call');

			this._getCallExcelHeaderStyle(headers, sheet);
			sheet.columns = this._getGetCallColumnWidth();

			// Add mapped data to the sheet starting from the sixth row
			data.forEach(function (rowData, index) {
				var row = sheet.getRow(index + 2);
				row.values = rowData;
				row.height = 37;

				row.eachCell({
					includeEmpty: true
				}, function (cell, index) {
					cell.font = {
						size: 10
					};
					cell.alignment = {
						vertical: (index === 1 ? 'bottom' : 'top'),
						horizontal: 'center',
						wrapText: true
					};
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
				});
			});

			// Write to a buffer
			workbook.xlsx.writeBuffer().then(function (buffer) {
					// Trigger the download
					var blob = new Blob([buffer], {
						type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
					});
					var url = URL.createObjectURL(blob);
					var anchor = document.createElement('a');
					anchor.href = url;
					anchor.download = 'Supplier IMS_GetCallData_' + this._getExcelTimeStamp() + '.xlsx';
					anchor.click();
					// Clean up
					URL.revokeObjectURL(url);
				}.bind(this))
				.catch(function (error) {
					console.error('Error writing Excel file:', error);
				});
		},

		_getCallExcelHeaderStyle: function (headers, sheet) {
			headers.forEach(function (row, rowIndex) {
				var headerRow = sheet.getRow(rowIndex + 1);
				headerRow.values = row;
				headerRow.font = {
					name: 'Calibri',
					size: 11,
					bold: true
				};
				this._getCallStyleHeaderRow(headerRow);
			}.bind(this));
		},

		_getCallStyleHeaderRow: function (headerRow) {
			headerRow.eachCell(function (cell) {
				cell.alignment = {
					vertical: 'bottom',
					horizontal: 'center'
				};
				cell.border = {
					top: {
						style: 'thin'
					},
					left: {
						style: 'thin'
					},
					bottom: {
						style: 'thin'
					},
					right: {
						style: 'thin'
					}
				};
			});
		},

		_getGetCallColumnWidth: function () {
			return [{
				key: 'A',
				width: 18
			}, {
				key: 'B',
				width: 18
			}, {
				key: 'C',
				width: 18
			}, {
				key: 'D',
				width: 18
			}, {
				key: 'E',
				width: 18
			}, {
				key: 'F',
				width: 18
			}, {
				key: 'G',
				width: 18
			}, {
				key: 'H',
				width: 18
			}, {
				key: 'I',
				width: 18
			}, {
				key: 'J',
				width: 18
			}, {
				key: 'K',
				width: 18
			}, {
				key: 'L',
				width: 18
			}, {
				key: 'M',
				width: 18
			}];
		},

		onFullScreenGetCall: function (type) {
			var oModel = this.getView().getModel("ViewProperty");
			this.byId("idGetSuppIms").setFullScreen(type === 'O');

			oModel.setProperty("/getIMSFullScreen", type === 'O');
			oModel.setProperty("/getImsCallRows", (type === 'O') ? 15 : 10);
			oModel.refresh(true);
		},

		/************************************************************************
		 * Supplier IMS Summary
		 ************************************************************************/
		_getReturnTypes: function (retType) {
			if (retType === 'All' || retType === "") {
				return ["GSTR1", "GSTR1A"];
			} else if (retType) {
				return [retType];
			} else {
				return [];
			}
		},

		onClearSummary: function () {
			var oModel = this.getView().getModel("FilterModel"),
				today = new Date();

			oModel.setProperty("/sumEntityGstin", []);
			oModel.setProperty("/sumEntityPeriod", (today.getMonth() + 1).toString().padStart(2, 0) + today.getFullYear());
			oModel.setProperty("/sumEntityRetType", "");
			oModel.setProperty("/sumEntityTabType", []);
			oModel.refresh(true);
			this.onSearchSummary();
		},

		onSearchSummary: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				payload = {
					"req": {
						"entityId": $.sap.entityID,
						"gstins": this.removeAll(oFilter.sumEntityGstin),
						"returnPeriod": oFilter.sumEntityPeriod,
						"tableTypes": this.removeAll(oFilter.sumEntityTabType),
						"returnTypes": this._getReturnTypes(oFilter.sumEntityRetType)
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			Promise.all([
					this._getEntitySummaryData(payload),
					this._getEntityDetailsSummary(payload)
				])
				.then(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.catch(function (err) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getEntitySummaryData: function (payload) {
			return new Promise(function (resolve, reject) {
				this.byId("tabEntitySummary").setSelectedIndex(-1);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSupplierImsSummaryEntityLvl.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "EnitySummSupplierIms");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "EnitySummSupplierIms");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getEntityDetailsSummary: function (payload) {
			return new Promise(function (resolve, reject) {
				this.byId("tabDetSummary").setSelectedIndex(-1);
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSupplierImsDetailEntityLvl.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						this.getView().setModel(new JSONModel(data.resp), "DetailSummSupplierIms");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "DetailSummSupplierIms");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onBackImsSummary: function () {
			var oModel = this.getView().getModel("ViewProperty");
			oModel.setProperty("/imsSummTab", "entity");
			oModel.setProperty("/navBack", false);
			oModel.refresh(true);
		},

		onExpandCollapseImsSummary: function (type) {
			if (type === 'E') {
				this.byId("tabImsSummGstn").expandToLevel(1);
			} else {
				this.byId("tabImsSummGstn").collapseAll();
			}
		},

		onImsSummEntityFullscreen: function (type) {
			var oModel = this.getView().getModel("ViewProperty");
			this.byId("idSummSuppIms").setFullScreen(type === 'O');

			oModel.setProperty("/imsSummEntityFullScreen", type === 'O');
			oModel.setProperty("/imsSummEntityRows", (type === 'O') ? 20 : 10);
			oModel.refresh(true);
		},

		onImsSummGstnFullscreen: function (type) {
			var oModel = this.getView().getModel("ViewProperty");
			this.byId("ccImsSummGstn").setFullScreen(type === 'O');

			oModel.setProperty("/imsSummGstnFullScreen", type === 'O');
			oModel.setProperty("/imsSummGstnRows", (type === 'O') ? 20 : 10);
			oModel.refresh(true);
		},

		onPressImsSummaryGstn: function (oEvent) {
			var oModel = this.getView().getModel("ViewProperty"),
				oFilter = this.getView().getModel("FilterModel"),
				obj = oEvent.getSource().getBindingContext("EnitySummSupplierIms").getObject();

			oModel.setProperty("/imsSummTab", "gstn");
			oModel.setProperty("/navBack", true);
			oModel.refresh(true);

			oFilter.setProperty("/sumGstnGstin", obj.gstin);
			oFilter.setProperty("/sumGstnPeriod", oFilter.getProperty("/sumEntityPeriod"));
			oFilter.refresh(true);
			sap.ui.core.BusyIndicator.show(0);
			this._getGstnSummaryData()
				.finally(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		onClearGstnSummary: function () {
			var oGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin"),
				oModel = this.getView().getModel("FilterModel"),
				today = new Date();

			oModel.setProperty("/sumGstnGstin", oGstin[0].value);
			oModel.setProperty("/sumGstnPeriod", oModel.getProperty("/sumEntityPeriod"));
			oModel.refresh(true);
			this.onSearchGstnSummary();
		},

		onSearchGstnSummary: function () {
			sap.ui.core.BusyIndicator.show(0);
			this._getGstnSummaryData()
				.finally(function (values) {
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_getGstnSummaryData: function () {
			return new Promise(function (resolve, reject) {
				var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
					payload = {
						"req": {
							"entityId": $.sap.entityID,
							"gstin": oFilter.sumGstnGstin,
							"returnPeriod": oFilter.sumGstnPeriod
						}
					};
				$.ajax({
						method: "POST",
						url: "/aspsapapi/getSupplierImsSummaryGstinLvl.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						var oData = this._formatSupplierGstnData(data.resp);
						this.getView().setModel(new JSONModel(oData), "GstnImsSummary");
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						this.getView().setModel(new JSONModel([]), "GstnImsSummary");
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_formatSupplierGstnData: function (data) {
			var itemTypes = ["Accepted", "Rejected", "Pending", "No Action"],
				createObject = function (type, data, gstnData) {
					var obj = {
						"type": type,
						"sectionType": data.sectionType,
						"tableType": (type === "P" ? data.sectionType : data.sectionType.split("_")[1]),
						"imsCount": data.count,
						"imsTaxableValue": data.totalTaxableValue,
						"imsTotalTax": data.totalTax,
						"imsIgst": data.igst,
						"imsCgst": data.cgst,
						"imsSgst": data.sgst,
						"imsCess": data.cess
					};
					if (type === "P") {
						obj.gstr1and1ACount = gstnData.count;
						obj.gstr1and1ATaxableValue = gstnData.totalTaxableValue;
						obj.gstr1and1ATotalTax = gstnData.totalTax;
						obj.gstr1and1AIgst = gstnData.igst;
						obj.gstr1and1ACgst = gstnData.cgst;
						obj.gstr1and1ASgst = gstnData.sgst;
						obj.gstr1and1ACess = gstnData.cess;
						obj.items = [];
					}
					return obj;
				},
				oData = [];

			data.supplierImsData.forEach(function (item) {
				var isParent = !itemTypes.some(function (status) {
					return item.sectionType.includes(status);
				});

				if (isParent) {
					var obj = data.gstr1And1AData.find(function (el) {
						return (el.sectionType === item.sectionType);
					});
					oData.push(createObject("P", item, obj));
				} else {
					var section = item.sectionType.split("_")[0],
						obj = oData.find(function (el) {
							return el.tableType === section;
						});
					if (obj) {
						obj.items.push(createObject("C", item));
					}
				}
			});
			return oData;
		},

		onDiffImsSummaryGstn: function (oEvent) {
			if (!this._imsSummDiff) {
				this._imsSummDiff = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.ims.Difference", this);
				this.getView().addDependent(this._imsSummDiff);
			}
			var oData = this.getView().getModel("GstnImsSummary").getProperty("/"),
				diffData = oData.map(function (el) {
					return {
						"section": (el.sectionType),
						"count": (el.imsCount - el.gstr1and1ACount),
						"taxableValue": (el.imsTaxableValue - el.gstr1and1ATaxableValue),
						"totalTax": (el.imsTotalTax - el.gstr1and1ATotalTax),
						"igst": (el.imsIgst - el.gstr1and1AIgst),
						"cgst": (el.imsCgst - el.gstr1and1ACgst),
						"sgst": (el.imsSgst - el.gstr1and1ASgst),
						"cess": (el.imsCess - el.gstr1and1ACess)
					};
				});
			this._imsSummDiff.setModel(new JSONModel(diffData), "ImsSummaryDiff");
			this._imsSummDiff.open();
		},

		onCloseImsSummGstn: function () {
			this._imsSummDiff.close();
		},

		onGetGstr1n1ASummary: function (type) {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/");
			if (type === "E") {
				var oData = this.getView().getModel("EnitySummSupplierIms").getProperty("/"),
					aIndex = this.byId("tabEntitySummary").getSelectedIndices();
				if (!aIndex.length) {
					MessageBox.alert("Please select at least one GSTIN");
					return;
				}
				var gstins = aIndex.map(function (idx) {
						return oData[idx].gstin;
					}),
					taxPeriod = oFilter.sumEntityPeriod;
			} else {
				gstins = [oFilter.sumGstnGstin];
				taxPeriod = oFilter.sumGstnPeriod;
			}
			MessageBox.information("Do you want to Fetch GSTN Data?", {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (sAction) {
					if (sAction === "YES") {
						var payload = {
							"req": {
								"retPeriod": taxPeriod,
								"gstinList": gstins
							}
						};
						sap.ui.core.BusyIndicator.show(0);
						Promise.all([
								this._getGstr1Summary(payload),
								this._getGstr1aSummary(payload)
							])
							.then(function (values) {
								if (!this._dGetSummaryMsg) {
									this.__dGetSummaryMsg = Fragment.load({
											"id": this.getView().getId(),
											"name": "com.ey.digigst.fragments.gstr1.BulkSave",
											"controller": this
										})
										.then(function (oDialog) {
											this.getView().addDependent(oDialog);
											this._dGetSummaryMsg = oDialog;
											this._dGetSummaryMsg.setTitle("Fetch GSTR1 Summary Status");
											this._displayGetSummaryMsg(values);
										}.bind(this));
								} else {
									this._displayGetSummaryMsg(values);
								}
							}.bind(this))
							.catch(function (err) {
								console.log(err);
							})
							.finally(function () {
								sap.ui.core.BusyIndicator.hide();
							}.bind(this));
					}
				}.bind(this)
			});
		},

		_displayGetSummaryMsg: function (values) {
			var finalArray = [],
				mergedMap = {},
				addToMap = function (resp) {
					resp.forEach(function (item) {
						if (!mergedMap[item.gstin]) {
							mergedMap[item.gstin] = [];
						}
						if (mergedMap[item.gstin].indexOf(item.msg) === -1) {
							mergedMap[item.gstin].push(item.msg);
						}
					});
				};
			values.forEach(function (el) {
				addToMap(el.resp);
			});
			for (var gstin in mergedMap) {
				if (mergedMap.hasOwnProperty(gstin)) {
					finalArray.push({
						gstin: gstin,
						msg: mergedMap[gstin].join('\n')
					});
				}
			}
			this.getView().byId("bulkSaveID").setModel(new JSONModel(finalArray), "BulkGstinSaveModel");
			this._dGetSummaryMsg.open();
		},

		_getGstr1Summary: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/fetchGstr1BulkGetSumm.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		_getGstr1aSummary: function (payload) {
			return new Promise(function (resolve, reject) {
				$.ajax({
						method: "POST",
						url: "/aspsapapi/fetchGstr1ABulkGetSumm.do",
						contentType: "application/json",
						data: JSON.stringify(payload)
					})
					.done(function (data, status, jqXHR) {
						resolve(data);
					}.bind(this))
					.fail(function (jqXHR, status, err) {
						reject(err);
					}.bind(this));
			}.bind(this));
		},

		onCloseDialogBulkSave: function () {
			this._dGetSummaryMsg.close();
		},

		onDownloadImsSummReport: function (type, key) {
			switch (key) {
			case "tableData":
				this._downloadImsSummaryReport();
				break;
			case "detailReport":
				this._downloadDetailedSummReport(type);
				break;
			default:
				if (type === "E") {
					this._downloadEntitySummaryReport(key);
				} else {
					this._downloadGstinSummaryReport(key);
				}
			}
		},

		_downloadDetailedSummReport: function (type) {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/");
			if (type === "E") {
				var oData = this.getView().getModel("EnitySummSupplierIms").getProperty("/"),
					aIndex = this.byId('tabEntitySummary').getSelectedIndices(),
					payload = {
						"req": {
							"type": "summaryEntityLevel",
							"reportType": "Detailed Summary Report",
							"gstins": [],
							"returnPeriod": oFilter.sumEntityPeriod,
							"returnTypes": this._getReturnTypes(oFilter.sumEntityRetType),
							"tableType": (oFilter.sumEntityTabType.length ?
								this.removeAll(oFilter.sumEntityTabType) : ["B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA"]),
						}
					};
				if (aIndex.length) {
					payload.req.gstins = aIndex.map(function (idx) {
						return oData[idx].gstin;
					});
				} else if (oFilter.sumEntityGstin.length) {
					payload.req.gstins = this.removeAll(oFilter.sumEntityGstin)
				} else {
					var oGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin");
					payload.req.gstins = oGstin.map(function (el) {
						return el.value;
					});
				}
			} else {
				var payload = {
					"req": {
						"type": "summaryGstinLevel",
						"reportType": "Detailed Summary Report",
						"returnPeriod": oFilter.sumGstnPeriod,
						"gstins": [oFilter.sumGstnGstin]
					}
				};
			}
			this.reportDownload(payload, "/aspsapapi/getSupplierImsDetailedSummaryReport.do");
		},

		_downloadEntitySummaryReport: function (key) {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.getView().getModel("EnitySummSupplierIms").getProperty("/"),
				aIndex = this.byId('tabEntitySummary').getSelectedIndices(),
				payload = {
					"type": "summaryEntityLevel",
					"returnPeriod": oFilter.sumEntityPeriod,
					"gstins": [],
					"tableType": (oFilter.sumEntityTabType.length ?
						this.removeAll(oFilter.sumEntityTabType) : ["B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA"]),
					"returnTypes": this._getReturnTypes(oFilter.sumEntityRetType),
					"reportTypes": (key === 'All' ? ["A", "R", "P", "N"] : [key]),
					"finYear": oFilter.getTaxPeriod
				};
			if (!aIndex.length) {
				sap.m.MessageBox.warning("Select at least one GSTIN");
				return;
			}
			payload.gstins = aIndex.map(function (idx) {
				return oData[idx].gstin;
			});
			// } else if (oFilter.sumEntityGstin.length) {
			// 	payload.gstins = this.removeAll(oFilter.sumEntityGstin)
			// } else {
			// 	var oGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin");
			// 	payload.gstins = oGstin.map(function (el) {
			// 		return el.value;
			// 	});
			// }
			this.reportDownload(payload, "/aspsapapi/getSupplierImsConsolidatedReport.do");
		},

		_downloadGstinSummaryReport: function (key) {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.getView().getModel("GstnImsSummary").getProperty("/"),
				payload = {
					"type": "summaryGstinLevel",
					"returnPeriod": oFilter.sumGstnPeriod,
					"gstins": [oFilter.sumGstnGstin],
					"reportTypes": (key === 'All' ? ["A", "R", "P", "N"] : [key]),
					"finYear": oFilter.getTaxPeriod
				};
			this.reportDownload(payload, "/aspsapapi/getSupplierImsConsolidatedReport.do");
		},

		_downloadImsSummaryReport: function () {
			var oData = this.getView().getModel("DetailSummSupplierIms").getProperty("/"),
				taxPeriod = this.getView().getModel('FilterModel').getProperty('/sumEntityPeriod'),
				headers = [
					[
						'GSTIN', 'Return Period', 'Get IMS Count',
						'Total Records', '', '',
						'Accepted Records', '', '',
						'Pending Records', '', '',
						'Rejected Records', '', '',
						'No Action Records', '', '',
						'Difference with GSTN', 'GSTR-1 Summary Status', 'GSTR-1A Summary Status'
					],
					[
						'', '', '',
						'Count', 'Total Taxable Value', 'Total Tax',
						'Count', 'Total Taxable Value', 'Total Tax',
						'Count', 'Total Taxable Value', 'Total Tax',
						'Count', 'Total Taxable Value', 'Total Tax',
						'Count', 'Total Taxable Value', 'Total Tax',
						'', '', ''
					]
				];
			var mappedData = oData.map(function (item) {
				return [
					item.gstin,
					taxPeriod,
					this._formatImsSummaryData(item.getCountStatus, item.getCountStatusDateTime),
					item.totalRecords.count || '',
					item.totalRecords.totalTaxableValue || '',
					item.totalRecords.totalTax || '',
					item.acceptedRecords.count || '',
					item.acceptedRecords.totalTaxableValue || '',
					item.acceptedRecords.totalTax || '',
					item.pendingRecords.count || '',
					item.pendingRecords.totalTaxableValue || '',
					item.pendingRecords.totalTax || '',
					item.rejectedRecords.count || '',
					item.rejectedRecords.totalTaxableValue || '',
					item.rejectedRecords.totalTax || '',
					item.noActionRecords.count || '',
					item.noActionRecords.totalTaxableValue || '',
					item.noActionRecords.totalTax || '',
					(item.differenceWithGstr1And1A ? 'Matched' : 'Not matched'),
					this._formatImsSummaryData(item.gstr1Summary.status, item.gstr1Summary.dateTime),
					this._formatImsSummaryData(item.gstr1aSummary.status, item.gstr1aSummary.dateTime)
				];
			}.bind(this));
			this._downloadSummaryExcelFile(headers, mappedData);
		},

		_formatImsSummaryData: function (status, timestamp) {
			return status + (!timestamp ? '' : '\n' + timestamp);
		},

		_downloadSummaryExcelFile: function (headers, data) {
			var workbook = new ExcelJS.Workbook(), // Create a new workbook
				sheet = workbook.addWorksheet('Supplier IMS Summary'); // Add a sheet

			this._excelHeaderStyle(headers, sheet);
			sheet.columns = this._getColumnWidth();

			// Add mapped data to the sheet starting from the sixth row
			data.forEach(function (rowData, index) {
				var row = sheet.getRow(index + 3); // Starting from row 6
				row.values = rowData;
				row.height = 37;

				row.eachCell({
					includeEmpty: true
				}, function (cell) {
					cell.font = {
						size: 10
					};
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
				});
				[2, 3, 19, 20, 21].forEach(function (cellIndex) {
					var cell = row.getCell(cellIndex);
					cell.alignment = {
						vertical: 'bottom',
						horizontal: 'center',
						wrapText: true
					};
				});
			});

			// Write to a buffer
			workbook.xlsx.writeBuffer().then(function (buffer) {
					// Trigger the download
					var blob = new Blob([buffer], {
						type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
					});
					var url = URL.createObjectURL(blob);
					var anchor = document.createElement('a');
					anchor.href = url;
					anchor.download = 'Supplier IMS_TabledataSummarylevel_' + this._getExcelTimeStamp() + '.xlsx';
					anchor.click();
					// Clean up
					URL.revokeObjectURL(url);
				}.bind(this))
				.catch(function (error) {
					console.error('Error writing Excel file:', error);
				});
		},

		_excelHeaderStyle: function (headers, sheet) {
			headers.forEach(function (row, rowIndex) {
				var headerRow = sheet.getRow(rowIndex + 1);
				headerRow.values = row;
				headerRow.font = {
					name: 'Calibri',
					size: 11,
					bold: true
				};
				this._styleHeaderRow(headerRow);
			}.bind(this));
			// Merge cells for the main headers
			var mergeCells = ['A1:A2', 'B1:B2', 'C1:C2', 'D1:F1', 'G1:I1', 'J1:L1', 'M1:O1', 'P1:R1', 'S1:S2', 'T1:T2', 'U1:U2'];
			mergeCells.forEach(function (range) {
				sheet.mergeCells(range);
			});
		},

		_styleHeaderRow: function (headerRow) {
			headerRow.eachCell(function (cell) {
				cell.alignment = {
					vertical: 'bottom',
					horizontal: 'center'
				};
				cell.border = {
					top: {
						style: 'thin'
					},
					left: {
						style: 'thin'
					},
					bottom: {
						style: 'thin'
					},
					right: {
						style: 'thin'
					}
				};
			});
		},

		_getColumnWidth: function () {
			return [{
				key: 'A',
				width: 18
			}, {
				key: 'B',
				width: 13
			}, {
				key: 'C',
				width: 18
			}, {
				key: 'D',
				width: 6
			}, {
				key: 'E',
				width: 17
			}, {
				key: 'F',
				width: 9
			}, {
				key: 'G',
				width: 6
			}, {
				key: 'H',
				width: 17
			}, {
				key: 'I',
				width: 9
			}, {
				key: 'J',
				width: 6
			}, {
				key: 'K',
				width: 17
			}, {
				key: 'L',
				width: 9
			}, {
				key: 'M',
				width: 6
			}, {
				key: 'N',
				width: 17
			}, {
				key: 'O',
				width: 9
			}, {
				key: 'P',
				width: 6
			}, {
				key: 'Q',
				width: 17
			}, {
				key: 'R',
				width: 9
			}, {
				key: 'S',
				width: 19
			}, {
				key: 'T',
				width: 21
			}, {
				key: 'U',
				width: 22
			}];
		},

		/************************************************************************
		 * Supplier IMS Summary
		 ************************************************************************/
		onClearIMSRecords: function () {
			var oModel = this.getView().getModel("FilterModel"),
				today = new Date();
			// date = new Date();
			// date.setMonth(date.getMonth() - 1);

			oModel.setProperty("/recordGstin", []);
			oModel.setProperty("/recordFrPeriod", today);
			oModel.setProperty("/recordToPeriod", today);
			oModel.setProperty("/recordRetType", []);
			oModel.setProperty("/recordTabType", []);
			oModel.setProperty("/recordActionType", 0);
			oModel.refresh(true);
			this.onPressImsRecords();
		},

		onPressImsRecords: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oPropData = this.getView().getModel("ViewProperty").getProperty("/"),
				payload = {
					"hdr": {
						"pageNum": (oPropData.pageNo || 1) - 1,
						"pageSize": +oPropData.pgSize
					},
					"req": {
						"entityId": $.sap.entityID,
						"gstins": this.removeAll(oFilter.recordGstin),
						"fromRetPeriod": this._formatPeriod(oFilter.recordFrPeriod),
						"toRetPeriod": this._formatPeriod(oFilter.recordToPeriod),
						"tableTypes": this.removeAll(oFilter.recordTabType),
						"returnTypes": this.removeAll(oFilter.recordRetType),
						"actionType": this._getActionType(oFilter.recordActionType, "S")
					}
				};
			sap.ui.core.BusyIndicator.show(0);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/supplierImsRecordScreenData.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					this.getView().setModel(new JSONModel(data.resp), "SuppImsRecords");
					this._countPagination(data.hdr);
					sap.ui.core.BusyIndicator.hide();
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this.getView().setModel(new JSONModel([]), "SuppImsRecords");
					sap.ui.core.BusyIndicator.hide();
				}.bind(this));
		},

		_countPagination: function (hdr) {
			var oModel = this.getView().getModel("ViewProperty"),
				vTotal = Math.ceil(hdr.totalCount / oModel.getProperty("/pgSize")),
				vPageNo = (vTotal ? hdr.pageNum + 1 : 0);

			oModel.setProperty("/pageNo", vPageNo);
			oModel.setProperty("/pgTotal", vTotal);
			oModel.setProperty("/ePageNo", vTotal > 1);
			oModel.setProperty("/bFirst", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bPrev", vTotal > 1 && vPageNo > 1);
			oModel.setProperty("/bNext", vTotal > 1 && vPageNo < vTotal);
			oModel.setProperty("/bLast", vTotal > 1 && vPageNo < vTotal);
			oModel.refresh(true);
		},

		onPressPagination: function (btn) {
			var oModel = this.getView().getModel("ViewProperty");
			switch (btn) {
			case 'F':
				oModel.setProperty("/pageNo", 1);
				break;
			case 'P':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") - 1);
				break;
			case 'N':
				oModel.setProperty("/pageNo", oModel.getProperty("/pageNo") + 1);
				break;
			case 'L':
				oModel.setProperty("/pageNo", oModel.getProperty("/pgTotal"));
				break;
			}
			oModel.refresh(true);
			this.onPressImsRecords();
		},

		onSubmitPagination: function (type) {
			var oModel = this.getView().getModel("ViewProperty");
			if (type === "S") {
				oModel.setProperty("/pageNo", 1);
				oModel.refresh(true);
			}
			this.onPressImsRecords();
		},

		onSupplierImsTrail: function (oEvent) {
			if (!this._oActionTrail) {
				this._oActionTrail = sap.ui.xmlfragment(this.getView().getId(), "com.ey.digigst.fragments.gstr1.ims.ActionTrail", this);
				this.getView().addDependent(this._oActionTrail);
			}
			var obj = oEvent.getSource().getBindingContext('SuppImsRecords').getObject(),
				payload = {
					"req": {
						"docKey": obj.docKey,
						"tableType": obj.tableType,
						"returnType": obj.returnType.replace('-', '')
					}
				};
			this._oActionTrail.setModel(new JSONModel({
				"header": [{
					"docNumber": obj.docNumber,
					"docDate": obj.docDate,
					"recipienGstin": obj.gstinRecipient
				}],
				"resp": []
			}), "ImsActionTrail");
			this._oActionTrail.setBusy(true);
			$.ajax({
					method: "POST",
					url: "/aspsapapi/supplierImsRecordTrailPopup.do",
					contentType: "application/json",
					data: JSON.stringify(payload)
				})
				.done(function (data, status, jqXHR) {
					var oModel = this._oActionTrail.getModel("ImsActionTrail");
					data.resp.forEach(function (el, i) {
						el.sno = i + 1;
					}.bind(this));
					oModel.setProperty("/resp", data.resp);
					oModel.refresh(true);
					this._oActionTrail.setBusy(false);
				}.bind(this))
				.fail(function (jqXHR, status, err) {
					this._oActionTrail.setModel(new JSONModel({}), "ImsActionTrail");
					this._oActionTrail.setBusy(false);
				}.bind(this));
			this._oActionTrail.openBy(oEvent.getSource());
		},

		onCloseActionGstin: function () {
			this._oActionTrail.close();
		},

		onChangeDateValue: function (oEvent) {
			var oModel = this.getView().getModel("FilterModel"),
				oData = oModel.getProperty("/");

			if (oData.recordFrPeriod > oData.recordToPeriod) {
				oData.recordToPeriod = new Date(oData.recordFrPeriod);
			}
			oModel.refresh(true);
		},

		onDownloadImsReportView: function () {
			var oFilter = this.getView().getModel("FilterModel").getProperty("/"),
				oData = this.getView().getModel("SuppImsRecords").getProperty("/"),
				aIndex = this.byId('tabRecordsView').getSelectedIndices(),
				payload = {
					"type": "imsRecords",
					"gstins": [],
					"returnTypes": (oFilter.recordRetType.length ? this.removeAll(oFilter.recordRetType) : ["GSTR1", "GSTR1A"]),
					"tableType": (oFilter.recordTabType.length ?
						this.removeAll(oFilter.recordTabType) : ["B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA"]),
					"fromPeriod": this._formatPeriod(oFilter.recordFrPeriod),
					"toPeriod": this._formatPeriod(oFilter.recordToPeriod),
					"actionType": this._getActionType(oFilter.recordActionType, "D"),
					"reportTypes": this._getActionType(oFilter.recordActionType, "D"),
					"finYear": oFilter.getTaxPeriod
				};
			if (aIndex.length) {
				payload.gstins = aIndex.map(function (idx) {
					return oData[idx].gstinSupplier;
				});
			} else if (oFilter.recordGstin.length) {
				payload.gstins = this.removeAll(oFilter.recordGstin)
			} else {
				var oGstin = this.getOwnerComponent().getModel("DataPermission").getProperty("/respData/dataSecurity/gstin");
				payload.gstins = oGstin.map(function (el) {
					return el.value;
				});
			}
			this.reportDownload(payload, "/aspsapapi/getSupplierImsConsolidatedReport.do");
		},

		onDownloadTableDataImsRecord: function () {
			var oData = this.getView().getModel("SuppImsRecords").getProperty("/"),
				oFilter = this.getView().getModel('FilterModel').getProperty('/'),
				headers = [
					[
						'IMS Action',
						// 'IMS Remarks',
						'Return Period', 'Supplier GSTIN', 'Supplier Name', 'Table Type', 'Document Type',
						// 'Supply Type',
						'Recipient GSTIN', 'Recipient Name', 'Document Number', 'Document Date', 'Taxable Value', 'Total Tax', 'IGST', 'CGST', 'SGST',
						'CESS', 'Invoice Value',
						// 'Reverse Charge', 'POS', 'Source', 'IRN', 'IRN Date',
						'Return Type', 'GSTR-1 Filing status',
						'Recipient GSTR-3B Filing Status', 'Estimated GSTR-3B Period', 'Original Document Number', 'Original Document Date', 'Checksum'
					]
				];
			var mappedData = oData.map(function (item) {
				return [
					this._getActionDesc(item.actionGstn),
					item.returnPeriod,
					item.gstinSupplier,
					item.supplierName,
					item.tableType,
					item.docType,
					item.gstinRecipient,
					item.recipientName,
					item.docNumber,
					this._getDate(item.docDate),
					item.taxableValue,
					item.totalTax,
					item.igst,
					item.cgst,
					item.sgst,
					item.cess,
					item.invoiceValue,
					item.returnType,
					item.gstr1FillingStatus,
					item.gstrRecipient3BStatus,
					item.estimatedGstr3BPeriod,
					item.orgDocNumber,
					this._getDate(item.orgDocDate),
					item.checkSum,
				];
			}.bind(this));
			this._downloadRecordsExcelFile(headers, mappedData);
		},

		_downloadRecordsExcelFile: function (headers, data) {
			var workbook = new ExcelJS.Workbook(), // Create a new workbook
				sheet = workbook.addWorksheet('Supplier IMS Records View'); // Add a sheet

			this._imsRecordHeaderStyle(headers, sheet);
			sheet.columns = this._getRecordColumnWidth();

			// Add mapped data to the sheet starting from the sixth row
			data.forEach(function (rowData, index) {
				var row = sheet.getRow(index + 2);
				row.values = rowData;

				row.eachCell({
					includeEmpty: true
				}, function (cell) {
					cell.font = {
						size: 10
					};
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
				});
			});

			// Write to a buffer
			workbook.xlsx.writeBuffer().then(function (buffer) {
					// Trigger the download
					var blob = new Blob([buffer], {
						type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
					});
					var url = URL.createObjectURL(blob);
					var anchor = document.createElement('a');
					anchor.href = url;
					anchor.download = 'Supplier IMS_RecordsViewData_' + this._getExcelTimeStamp() + '.xlsx';
					anchor.click();
					// Clean up
					URL.revokeObjectURL(url);
				}.bind(this))
				.catch(function (error) {
					console.error('Error writing Excel file:', error);
				});
		},

		_imsRecordHeaderStyle: function (headers, sheet) {
			headers.forEach(function (row, rowIndex) {
				var headerRow = sheet.getRow(rowIndex + 1);
				headerRow.values = row;
				headerRow.eachCell(function (cell) {
					cell.alignment = {
						vertical: 'bottom',
						horizontal: 'center'
					};
					cell.border = {
						top: {
							style: 'thin'
						},
						left: {
							style: 'thin'
						},
						bottom: {
							style: 'thin'
						},
						right: {
							style: 'thin'
						}
					};
					cell.font = {
						name: 'Calibri',
						size: 11,
						bold: true
					};
				});
			}.bind(this));
		},

		_getRecordColumnWidth: function () {
			return [{
				key: 'A',
				width: 11
			}, {
				key: 'B',
				width: 13
			}, {
				key: 'C',
				width: 17
			}, {
				key: 'D',
				width: 30
			}, {
				key: 'E',
				width: 10
			}, {
				key: 'F',
				width: 14
			}, {
				key: 'G',
				width: 17
			}, {
				key: 'H',
				width: 30
			}, {
				key: 'I',
				width: 17
			}, {
				key: 'J',
				width: 13
			}, {
				key: 'K',
				width: 13
			}, {
				key: 'L',
				width: 13
			}, {
				key: 'M',
				width: 13
			}, {
				key: 'N',
				width: 13
			}, {
				key: 'O',
				width: 13
			}, {
				key: 'P',
				width: 13
			}, {
				key: 'Q',
				width: 13
			}, {
				key: 'R',
				width: 11
			}, {
				key: 'S',
				width: 17
			}, {
				key: 'T',
				width: 27
			}, {
				key: 'U',
				width: 23
			}, {
				key: 'V',
				width: 24
			}, {
				key: 'W',
				width: 21
			}, {
				key: 'X',
				width: 30
			}];
		},

		statusClr: function (value) {
			if (value) {
				switch (value.toLowerCase()) {
				case "saved":
				case "partially saved":
				case "partially success":
				case "partial success":
					return "Warning";
				case "submitted":
				case "success":
				case "success no data":
					return "Success";
				case "in progress":
					return "Information";
				case "failed":
					return "Error";
				default:
					return "None";
				}
			}
			return "None";
		},

		_getActionType: function (action, type) {
			switch (action) {
			case 0:
				var sCode = "ALL";
				break;
			case 1:
				sCode = "N";
				break;
			case 2:
				sCode = "A";
				break;
			case 3:
				sCode = "R";
				break;
			case 4:
				sCode = "P";
				break;
			case 5:
				sCode = "NE";
				break;
			case 6:
				sCode = "R3B";
				break;
			}
			if (type === "S") {
				return sCode;
			}
			return (sCode === "ALL" ? ["A", "R", "P", "N"] : [sCode]);
		},

		_getActionDesc: function (action) {
			switch (action) {
			case 'A':
				return "Accepted";
			case 'R':
				return "Rejected";
			case 'P':
				return "Pending";
			case 'N':
				return "No Action";
			case 'NE':
				return "Not Eligible";
			case 'R3B':
				return "Rejected - GSTR-3B Liability";
			}
		},

		_getDate: function (date) {
			var dt = (date ? new Date(date) : new Date()),
				d = ('' + dt.getDate()),
				m = (dt.getMonth() + 1).toString();
			return d.padStart(2, 0) + '-' + m.padStart(2, 0) + '-' + dt.getFullYear();
		},

		_formatFreqCall: function (value) {
			var aAns = value.split('*'),
				sValue = "";

			switch (aAns[0]) {
			case "A":
				sValue = "Daily";
				break;
			case "B":
				sValue = "Weekly";
				break;
			case "C":
				sValue = "Fortnightly";
				break;
			case "D":
				sValue = "Monthly";
				break;
			}
			if (aAns[1]) {
				sValue += " (" + aAns[1] + ")";
			}
			return sValue;
		}
	});
});