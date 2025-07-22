sap.ui.define([], function () {
	"use strict";

	return {

		/////////CounterParty Link
		status: function (val) {
			if (val === "" || val === null) {
				return "Not Initiated";
			} else {
				return val;
			}
		},

		itc04TaxPeriod: function (val) {
			var taxPeriod = val.split("/")[0];
			var year = val.split("/")[1];
			var value = "";
			if (taxPeriod === "13") {
				value = "Q1/" + year;
				return value;
			} else if (taxPeriod === "14") {
				value = "Q2/" + year;
				return value;
			} else if (taxPeriod === "15") {
				value = "Q3/" + year;
				return value;
			} else if (taxPeriod === "16") {
				value = "Q4/" + year;
				return value;
			} else if (taxPeriod === "17") {
				value = "H1/" + year;
				return value;
			} else if (taxPeriod === "18") {
				value = "H2/" + year;
				return value;
			}
		},

		reconcolor: function (flag) {
			if (flag === false) {
				return "Error";
			} else {
				return "None";
			}

		},
		reconcolorPR: function (flag) {
			if (flag === false) {
				return "Error";
			} else {
				return "Information";
			}

		},

		icon: function (val) {
			if (val === "PE" || val === "ER") {
				return true;
			} else {
				return false;
			}
		},

		iconFS: function (val) {
			if (val === "Failed" || val === "FAILED") {
				return true;
			} else {
				return false;
			}
		},

		matchedPercent1: function (val) {
			if (val !== undefined) {
				return val.toFixed(2);
			} else {
				return 0;
			}
		},

		matchedPercent2: function (val) {
			if (val !== undefined && val !== null) {
				return val.toFixed(2);
			} else {
				return 0;
			}
		},

		/**
		 * Developed by: Bharat Gupta on 27.03.2020
		 * Called to format number value to Indian Numbering system
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} res Rusult format number
		 */
		numberFormat: function (value) {
			if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
				var flag = false,
					s = typeof (value) === "number" ? value.toString() : value;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var lastThree = s.substring(s.length - 3),
					otherNumbers = s.substring(0, s.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree;
				return res;
			}
			return value;
		},

		/**
		 * Developed by: Bharat Gupta
		 * Called to format amount value in Indian currency format
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} res
		 */
		amountValue: function (value) {
			if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
				var flag = false,
					s = typeof (value) === "number" ? value.toFixed(2) : value;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var y = s.split(".")[0],
					dec = !s.split(".")[1] ? "00" : s.split(".")[1],
					lastThree = y.substring(y.length - 3),
					otherNumbers = y.substring(0, y.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
				return res;
			}
			return value;
		},

		eComCountFormat: function (value) {
			if (!value && value !== 0) {
				return "NA";
			}
			return value;
		},

		eComAmountFormat: function (value1, value2) {
			if (["15(i)", "15(ii)", "15(iii)", "15(iv)", "15A.1.a", "15A.1.b", "15A.2.a", "15A.2.b"].includes(value1)) {
				if (!value2 && value2 !== 0) {
					return "NA";
				}
			}
			return value2;
		},

		/**
		 * Developed by: Bharat Gupta - 22.11.2020
		 * Called to format Quantity value in decmial 3 digit
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} value Value to be formatted
		 * @return {string} res
		 */
		quntyValue: function (value) {
			if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
				var flag = false,
					s = typeof (value) === "number" ? value.toFixed(3) : value;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var y = s.split(".")[0],
					dec = !s.split(".")[1] ? "000" : s.split(".")[1],
					lastThree = y.substring(y.length - 3),
					otherNumbers = y.substring(0, y.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
				return res;
			}
			return value;
		},

		amountValue1: function (vValue, val1) {
			if (val1 !== "E_ITC") {
				if (!isNaN(vValue) && vValue !== null && vValue !== 0 && vValue !== "" && vValue !== "0.00") {
					var x = vValue;
					x = x.toString();
					var y = x.split(".")[0];
					var dec = x.split(".")[1];
					if (dec === undefined) {
						var DecValue = "." + "00";
					} else {
						if (dec.length === 1) {
							dec = dec + "0";
						}
						DecValue = "." + dec.substr(0, 2);
					}
					var lastThree = y.substring(y.length - 3);
					var otherNumbers = y.substring(0, y.length - 3);
					if (otherNumbers !== "") {
						lastThree = "," + lastThree;
					}
					var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + DecValue;
					return res;
				} else if (vValue === 0.00 || vValue === 0) {
					return "";
				}
				return vValue;
			} else {
				return "";
			}
		},

		optionOpted: function (val) {
			if (val === "A") {
				return "Basic QR Code Validation";
			} else if (val === "B") {
				return "PDF vs QR Code";
			} else if (val === "C") {
				return "PDF vs Inward E-invoice JSON";
			}
		},

		Particulars: function (val) { //eslint-disable-line
			if (val === "P") {
				return "Pending";
			} else if (val === "A") {
				return "Accept";
			} else if (val === "R") {
				return "Reject";
			} else if (val === "S") {
				return "Saved";
			} else if (val === "NS") {
				return "Not Saved";
			} else if (val === "U") {
				return "Unlock";
			} else if (val === "N") {
				return "No Action";
			} else if (val === "PROCESSED") {
				return "PROCESSED";
			}
		},

		einvStatus: function (val) { //eslint-disable-line

			if (val == "1") {
				return "Not Opted";
			} else if (val == "2") {
				return "Not Applicable";
			} else if (val == "3") {
				return "Pending (No Action)";
			} else if (val == "11") {
				return "Pending (Error)";
			} else if (val == "4") {
				return "Generation Error";
			} else if (val == "5") {
				return "Generated";
			} else if (val == "6") {
				return "Cancelled";
			} else if (val == "7") {
				return "ASP Error";
			} else if (val == "10") {
				return "Pushed to NIC";
			} else if (val == "13") {
				return "Generated (as provided by user)";
			} else if (val == "14") {
				return "Generated – Available for cancellation";
			} else if (val == "23") {
				return "IRN Generated In ERP";
			} else if (val == "24") {
				return "IRN Not Generated In ERP";
			}
		},

		ewbStatus: function (val) { //eslint-disable-line
			if (val == "12") {
				return "Not Opted";
			} else if (val == "1") {
				return "Not Applicable";
			} else if (val == "2") {
				return "Pending (No Action)";
			} else if (val == "4") {
				return "Part-A Generated";
			} else if (val == "6") {
				return "Cancelled";
			} else if (val == "7") {
				return "Discarded";
			} else if (val == "8") {
				return "Rejected";
			} else if (val == "5") {
				return "EWB Active";
			} else if (val == "9") {
				return "Expired";
			} else if (val == "10") {
				return "Pushed to NIC";
			} else if (val == "11") {
				return "ASP Error";
			} else if (val == "3") {
				return "Generation Error";
			} else if (val == "13") {
				return "Generated (as provided by user)";
			} else if (val == "14") {
				return "Pending (Error)";
			} else if (val == "23") {
				return "EWB Generated in ERP";
			} else if (val == "24") {
				return "EWB Not Generated in ERP";
			}
		},

		ewbPoint: function (val) { //eslint-disable-line
			if (val == "2") {
				return "ASP Processeing";
			} else if (val == "5") {
				return "Generate EWB";
			} else if (val == "12") {
				return "Update Part-B";
			} else if (val == "18") {
				return "Extend";
			} else if (val == "15") {
				return "Transporter";
			} else if (val == "9") {
				return "Cancelled";
			} else {
				return val;
			}
		},

		AciveValue: function (status) {
			if (status === "A") {
				return "Active";
			} else {
				return "Inactive";
			}
		},

		isDeleteStatus: function (status) {
			if (status == false) {
				return "Active";
			} else {
				return "Inactive";
			}
		},

		Tabletype: function (val) { //eslint-disable-line
			if (val === "3E") {
				return "SEZ with Tax";
			} else if (val === "3F") {
				return "SEZ without Tax";
			} else if (val === "3B") {
				return "B2B";
			} else if (val === "3G") {
				return "Deemed Exports";
			} else if (val === "3C") {
				return "EXPT";
			} else if (val === "3D") {
				return "EXPWT";
			}
		},

		/**
		 * Developed by Bharat Gupta
		 * Called to format date in 'dd-mm-yyyy' format
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {Date} vDate date to be format
		 * @return {string} vDate
		 */
		dateFormat: function (vDate) {
			if (vDate) {
				var aDate = vDate.split("-");
				return (aDate[2] + "-" + aDate[1] + "-" + aDate[0]);
			}
			return vDate;
		},

		TransMode: function (val) { //eslint-disable-line
			if (val == "1") {
				return "ROAD";
			} else if (val == "2") {
				return "RAIL";
			} else if (val == "3") {
				return "AIR";
			} else if (val == "4") {
				return "SHIP";
			} else if (val == "5") {
				return "INTRANSIT";
			}
		},

		/**
		 * Developed by Bharat Gupta
		 * Called to format period in 'MMM yyyy' format
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vValue Period to format
		 * @return {string} vValue
		 */
		periodFormat: function (vValue) {
			if (vValue) {
				var oMonth = {
					"01": "Jan",
					"02": "Feb",
					"03": "Mar",
					"04": "Apr",
					"05": "May",
					"06": "Jun",
					"07": "Jul",
					"08": "Aug",
					"09": "Sep",
					"10": "Oct",
					"11": "Nov",
					"12": "Dec"
				};
				var vPeriod = oMonth[vValue.substr(0, 2)];
				return vPeriod + " " + vValue.substr(2, 4);
			}
			return vValue;
		},

		/**
		 * Developed by Bharat Gupta
		 * Called to get File Type
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vFile file type
		 * @return {string} fileType
		 */
		fileType: function (vFile) {
			var fileType;
			switch (vFile) {
			case "RAW":
				fileType = "Raw File";
				break;
			case "TABLE3H3I":
				fileType = "Reverse Charge & Import Services";
				break;
			case "TABLE4":
				fileType = "Supplies through E-commerce";
				break;
			case "EINVOICE_RECON":
				fileType = "E-Invoice Recon";
				break;
			case "GLUPLOAD":
				fileType = "GL Upload (Summary)";
				break;
			case "180_days_Reversal":
				fileType = "180 Days Payment Reference";
				break;
			case "RECIPIENT_MASTER":
				fileType = "Recipient Master";
				break;
			case "REV_180DAYS_RESPONSE":
				fileType = "180 Days Reversal Response";
				break;
			default:
				fileType = vFile;
			}
			return fileType;
		},

		gstr1vs3bSupplies: function (vValue) { //eslint-disable-line
			var vTab;
			switch (vValue) {
			case "A1":
				vTab = "Table 3.1(a) Outward Taxable Supplies (other than zero rated, nil rated & exempted)";
				break;
			case "A2":
				vTab = "Table 3.1(b) Outward taxable supplies (zero rated)";
				break;
			case "A3":
				vTab = "Table 3.1.1.a Taxable supplies on which electronic commerce operator pays tax u/s 9(5)";
				break;
			case "B1":
				vTab = "Supplies made to registered person on forward charge (B2B - 4A, 6B, 6C)";
				break;
			case "B2":
				vTab = "Supplies made to registered person on reverse charge  (B2B - 4B)";
				break;
			case "B3":
				vTab = "Supplies made to unregistered person (B2CL - 5)";
				break;
			case "B4":
				vTab = "Supplies made to other unregistered person (B2CS - 7)";
				break;
			case "B5":
				vTab = "Exports (6A)";
				break;
			case "B6":
				vTab = "CR/DR issued against supplies reported in B1 & B2 (CDNR - 9B)";
				break;
			case "B7":
				vTab = "CR/DR issued against supplies reported in B3 & B5 (CDNUR - 9B)";
				break;
			case "B8-B9":
				vTab = "Advance Received Less Advance Adjusted (11)";
				break;
			case "B10":
				vTab = "Amendment of supplies reported in B1 & B2 of previous tax periods (B2BA - 9A)";
				break;
			case "B11":
				vTab = "Amendment of supplies reported in B3 of previous tax periods (B2CLA - 9A)";
				break;
			case "B12":
				vTab = "Amendment of supplies reported in B4 of previous tax periods (B2CSA - 10)";
				break;
			case "B13":
				vTab = "Amendment of supplies reported in B5 of previous tax periods (EXPA - 9A)";
				break;
			case "B14":
				vTab = "Amendment of supplies reported in B6 of previous tax periods (CDNRA - 9C)";
				break;
			case "B15":
				vTab = "Amendment of supplies reported in B7 of previous tax periods (CDNURA - 9C)";
				break;
			case "B16-B17":
				vTab =
					"Amendment of advance received reported in B8 of previous tax periods Less Amendment of advance adjusted reported in B9 of previous tax periods (11)";
				break;
			case "B18":
				vTab = "Supplies made to other unregistered person (ECOM - 15)";
				break;
			case "B19":
				vTab = "Amendment of Supplies reported in B18 of Previous tax periods (ECOMA-15)";
				break;
			default:
				vTab = vValue;
			}
			return vTab;
		},

		formatCategory1415: function (vValue) { //eslint-disable-line
			switch (vValue) {
			case "14":
				var vTab = "Supplies made through e-Commerce 14";
				break;
			case "14(i)":
				vTab = "14 (i) – Supplies attracting u/s 52";
				break;
			case "14(ii)":
				vTab = "14 (ii) – Supplies attracting u/s 9(5)";
				break;
			case "14A":
				vTab = "Amendment of Supplies made through e-Commerce 14A";
				break;
			case "14A(i)":
				vTab = "14A (i) – Supplies attracting u/s 52";
				break;
			case "14A(ii)":
				vTab = "14A (ii) – Supplies attracting u/s 9(5)";
				break;
			case "15":
				vTab = "E-com - Supplies made through e-Commerce 15";
				break;
			case "15(i)":
				vTab = "Registered supplier to Registered Recipient 15(i)";
				break;
			case "15(ii)":
				vTab = "Registered supplier to Unregistered Recipient 15(ii)";
				break;
			case "15(iii)":
				vTab = "Unregistered supplier to Registered Recipient 15(iii)";
				break;
			case "15(iv)":
				vTab = "Unregistered supplier to Unregistered Recipient 15(iv)";
				break;
			case "15A(i)":
				vTab = "E-com - Amendment of Supplies made through e-Commerce 15A(I)";
				break;
			case "15A.1.a":
				vTab = "Registered supplier to Registered Recipient 15A.1.a";
				break;
			case "15A.1.b":
				vTab = "Unregistered supplier to Registered Recipient 15A.1.b";
				break;
			case "15A(ii)":
				vTab = "E-com - Amendment of Supplies made through e-Commerce 15A(II)";
				break;
			case "15A.2.a":
				vTab = "Registered supplier to Unregistered Recipient 15A.2.a";
				break;
			case "15A.2.b":
				vTab = "Unregistered supplier to Unregistered Recipient 15A.2.b";
				break;
			default:
				vTab = vValue;
			}
			return vTab;
		},

		formatCategory1415Bold: function (value) { //eslint-disable-line
			if (["14", "14A", "15", "15A(i)", "15A(ii)"].includes(value)) {
				return "Bold";
			}
			return "Standard";
		},

		formatCategory1415Value: function (value) { //eslint-disable-line
			return true; //!["14", "14A"].includes(value);
		},

		/**
		 * Developed by Bharat Gupta
		 * Called to get Table Section Description in Review Summary
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vValue Value to be get description
		 * @return {string} vTab
		 */
		sectionFormat: function (vValue) { //eslint-disable-line
			var vTab;
			switch (vValue) {
			case "b2c":
			case "3A":
				vTab = "B2C (3A)";
				break;
			case "b2b":
			case "3B":
				vTab = "B2B (3B)";
				break;
			case "expt":
			case "3C":
				vTab = "Export with Tax (3C)";
				break;
			case "expwt":
			case "3D":
				vTab = "Export without Tax (3D)";
				break;
			case "sezt":
			case "3E":
				vTab = "SEZ with tax (3E)";
				break;
			case "sezwt":
			case "3F":
				vTab = "SEZ without Tax (3F)";
				break;
			case "deemExpt":
			case "3G":
				vTab = "Deemed Export (3G)";
				break;
			case "rev":
			case "3H":
				vTab = "Reverse Charge (3H)";
				break;
			case "imps":
			case "3I":
				vTab = "Import of Services (3I)";
				break;
			case "impg":
			case "3J":
				vTab = "Import of Goods (3J)";
				break;
			case "impgSez":
			case "3K":
				vTab = "Import of Goods SEZ (3K)";
				break;
			case "ecom":
			case "4":
				vTab = "Supplies through Ecom";
				break;
			case "INV":
				vTab = "Invoice";
				break;
			case "SLF":
				vTab = "Self Invoice";
				break;
			case "DR":
				vTab = "Debit Note";
				break;
			case "CR":
				vTab = "Credit Note";
				break;
			case "RNV":
				vTab = "Invoice - Amendments";
				break;
			case "RDR":
				vTab = "Debit Note - Amendments";
				break;
			case "RCR":
				vTab = "Credit Note - Amendments";
				break;
			default:
				vTab = vValue;
			}
			return vTab;
		},

		sectionFormatgstr7: function (vValue) { //eslint-disable-line
			switch (vValue) {
			case "Table-3":
				return "Table-3 (Original Details)";
				break;
			case "Table-4":
				return "Table-4 (Amendment Details)";
				break;
			}
		},

		// 		_visibility: function (vValue) {
		// 			if (vValue === 0 || vValue === "" || !vValue) {
		// 				return false;
		// 			} else {
		// 				return true;
		// 			}
		// 		},

		/**
		 * Developed by Bharat Gupta
		 * Called to format Status
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vValue Value to be Formatted
		 * @return {string} vStatus
		 */
		formatState: function (vValue) {
			var vStats;
			switch (vValue) {
			case "ERR":
				vStats = "Error";
				break;
			case "INFO":
				vStats = "Information";
				break;
			default:
				vStats = "None";
			}
			return vStats;
		},

		/**
		 * Developed by Bharat Gupta
		 * Called to get Status Description of Status
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vValue Value to be get description
		 * @return {string} vStatus
		 */
		_requestStatus: function (vValue) {
			var vStatus;
			switch (vValue) {
			case 1:
				vStatus = "Sent for Approval";
				break;
			case 2:
				vStatus = "Approved";
				break;
			case 3:
				vStatus = "Rejected";
				break;
			default:
				vStatus = "";
			}
			return vStatus;
		},

		// 		_summType: function (vValue) {
		// 			var vType;
		// 			switch (vValue) {
		// 			case "ey":
		// 				vType = "EY";
		// 				break;
		// 			case "gstn":
		// 				vType = "GSTN";
		// 				break;
		// 			case "diff":
		// 				vType = "Diff";
		// 				break;
		// 			case "INV":
		// 				vType = "Invoice";
		// 				break;
		// 			case "DR":
		// 				vType = "Debit Note";
		// 				break;
		// 			case "CR":
		// 				vType = "Credit Note";
		// 				break;
		// 			case "RNV":
		// 				vType = "Invoice - Amendments";
		// 				break;
		// 			case "RDR":
		// 				vType = "Debit Note - Amendments";
		// 				break;
		// 			case "RCR":
		// 				vType = "Credit Note - Amendments";
		// 				break;
		// 			default:
		// 				vType = vValue;
		// 			}
		// 			return vType;
		// 		},

		// 		_docTypeSNo: function (vValue) {
		// 			var sNo;
		// 			switch (vValue) {
		// 			case "INV":
		// 				sNo = 1;
		// 				break;
		// 			case "DR":
		// 				sNo = 2;
		// 				break;
		// 			case "CR":
		// 				sNo = 3;
		// 				break;
		// 			case "RNV":
		// 				sNo = 4;
		// 				break;
		// 			case "RDR":
		// 				sNo = 5;
		// 				break;
		// 			case "RCR":
		// 				sNo = 6;
		// 				break;
		// 			}
		// 			return sNo;
		// 		},

		/**
		 * Developed by Bharat Gupta
		 * Called to change the text color of Status
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} vValue Value to be formatted
		 * @return {string} vText
		 */
		IMSText: function (vValue) {
			var vText = "";
			switch (vValue) {
			case "A":
				vText = "Accepted";
				break;
			case "R":
				vText = "Rejected";
				break;
			case "P":
				vText = "Pending";
				break;
			case "N":
				vText = "No Action";
				break;
			default:
				vText = "";
			}
			return vText;
		},

		IMSStatus: function (vValue) {
			var vText = "";
			switch (vValue) {
			case "A":
				vText = "Success";
				break;
			case "R":
				vText = "Error";
				break;
			case "P":
				vText = "Warning";
				break;
			case "N":
				vText = "Information";
				break;
			default:
				vText = "Information";
			}
			return vText;
		},

		statusClr: function (vValue) {
			var vText = "";
			switch (vValue) {
			case "saved":
			case "Saved":
			case "SAVED":
				vText = "Warning";
				break;
			case "Partially Saved":
			case "PARTIALLY SAVED":
			case "PARTIAL SUCCESS":
			case "Partially Success":
				vText = "Error";
				break;
			case "Submitted":
			case "SUBMITTED":
				vText = "Success";
				break;
			case "In Progress":
			case "IN PROGRESS":
				vText = "Information";
				break;
			case "FAILED":
			case "Failed":
				vText = "Error";
				break;
			case "Success No data":
			case "SUCCESS":
			case "Success":
				vText = "Success";
				break;
			default:
				vText = "None";
			}
			return vText;
		},

		statusCH: function (vValue) {
			var vText = "";
			switch (vValue) {
			case "Initiated":
			case "InProgress":
				vText = "Warning";
				break;
			case "Not Filed":
				vText = "Error";
				break;
			case "Submitted":
			case "SUBMITTED":
			case "Filed":
			case "FILED":
				vText = "Success";
				break;
			case "In Progress":
			case "IN PROGRESS":
				vText = "Information";
				break;
			default:
				vText = "None";
			}
			return vText;
		},

		status3B: function (val) {
			if (val === "P") {
				return "SUCCESS";
			} else if (val === "ER") {
				return "FAILED";
			} else {
				return val;
			}
		},

		statusClrEWB: function (val) {
			if (val === "COMPLETED") {
				return "Success";
			} else if (val === "INPROGRESS") {
				return "Warning";
			} else {
				return "Error";
			}
		},

		statusNotices: function (vValue) {
			var vText = "";
			switch (vValue) {
			case "In Progress":
			case "Pending":
				vText = "Warning";
				break;
			case "Partially Success":
				vText = "Information";
				break;
			case "Submitted":
			case "SUBMITTED":
			case "Success No data":
			case "SUCCESS":
			case "Success":
			case "Responded":
				vText = "Success";
				break;
				break;
			case "FAILED":
			case "Failed":
			case "Overdue":
				vText = "Error";
				break;
			default:
				vText = "None";
			}
			return vText;
		},

		tableType: function (val) { //eslint-disable-line
			if (val === "3.1(a)" || val === "3.2(a)" || val === "3.1.1(a)" || val === "4(a)" || val === "5(a)" || val === "5.1(a)") {
				return "(a)";
			} else if (val === "3.1(b)" || val === "3.2(b)" || val === "3.1.1(b)" || val === "4(b)" || val === "5(b)" || val === "5.1(b)") {
				return "(b)";
			} else if (val === "3.1(c)" || val === "3.2(c)" || val === "4(c)") {
				return "(c)";
			} else if (val === "3.1(d)" || val === "4(d)") {
				return "(d)";
			} else if (val === "3.1(e)") {
				return "(e)";
			} else if (val === "4(a)(1)" || val === "4(b)(1)" || val === "4(d)(1)") {
				return "(1)";
			} else if (val === "4(a)(2)" || val === "4(b)(2)" || val === "4(d)(2)") {
				return "(2)";
			} else if (val === "4(a)(3)") {
				return "(3)";
			} else if (val === "4(a)(4)") {
				return "(4)";
			} else if (val === "4(a)(5)") {
				return "(5)";
			} else {
				return val;
			}
		},

		exmpsubSectionName: function (val) { //eslint-disable-line
			if (val === "FSUC") {
				return "From a Supplier under composition scheme, Exempt & Nil Rated Supply";
			} else if (val === "NGST_SUPPLY") {
				return "Non GST Supply";
			}
		},

		supplyTypeDesc: function (val) { //eslint-disable-line
			if (val === "TORCIS") {
				return "Tax on Outward & Reverse Charge Inward Supplies";
			} else if (val === "ISS") {
				return "Inter-State Supplies";
			} else if (val === "E_ITC") {
				return "Eligible ITC";
			} else if (val === "ENANGST") {
				return "Exempt, Nil & Non-GST Inward Supplies";
			} else if (val === "IALF") {
				return "Interest & Late Fee Payable";
			} else if (val === "OTS1") {
				return "Outward Taxable Supplies (Other than zero rated, nil rated & exempted)";
			} else if (val === "OTS2") {
				return "Outward Taxable Supplies (Zero Rated)";
			} else if (val === "OTS3") {
				return "Other Outward Supplies (Nil Rated, Exempted)";
			} else if (val === "IS") {
				return "Inward Supplies (Liable to Reverse Charge)";
			} else if (val === "NON_GST") {
				return "Non-GST Outward Supplies";
			} else if (val === "SMTURP") {
				return "Supplies made to Unregistered Persons";
			} else if (val === "SMTCTP") {
				return "Supplies made to Composition Taxable Persons";
			} else if (val === "SMTUINH") {
				return "Supplies made to UIN holders";
			} else if (val === "ITC_Avail") {
				return "ITC Available (Whether in full or part)";
			} else if (val === "IOG") {
				return "Import of Goods";
			} else if (val === "IOS") {
				return "Import of Service";
			} else if (val === "ISLTR") {
				return "Inward Supplies liable to Reverse Charge (Other than 1 & 2 above)";
			} else if (val === "ISFISD") {
				return "Inward Supplies from ISD";
			} else if (val === "AO_ITC") {
				return "All other ITC";
			} else if (val === "ITC_R") {
				return "ITC Reversed";
			} else if (val === "AP42&43") {
				return "As per rules 38, 42 and 43 of CGST Rules and Section 17(5)";
			} else if (val === "IR_OTHERS") {
				return "Others";
			} else if (val === "NET_ITC_AVAIL") {
				return "Net ITC Available (A)-(B)";
			} else if (val === "I_ITC") {
				return "Other Details";
			} else if (val === "APS17") {
				return "ITC reclaimed which was reversed under Table 4(B)(2) in earlier tax period";
			} else if (val === "II_OTHERS") {
				return "Ineligible ITC under section 16(4) and ITC restricted due to PoS provisions";
			} else if (val === "ENANGST") {
				return "Exempt, Nil & Non-GST Inward Supplies";
			} else if (val === "FSUC") {
				return "From a Supplier under composition scheme, Exempt & Nil Rated Supply";
			} else if (val === "NGST_SUPPLY") {
				return "Non GST Supply";
			} else if (val === "IALF") {
				return "Interest & Late Fee Payable";
			} else if (val === "INTEREST") {
				return "Interest";
			} else if (val === "LATE_FEES") {
				return "Late Fees";
			} else if (val === "DOSN") {
				return "Details of Supplies notified under section 9(5)";
			} else if (val === "TSOEO") {
				return "Taxable supplies on which E-com operator pays tax u/s 9(5)";
			} else if (val === "TSMRP") {
				return "Taxable supplies made by registered person through E-com operator";
			} else if (val === "PPLIA") {
				return "Past period liability";
			}
		},

		state: function (val, val1) {
			if ((val === "" || val === undefined) && (val1 === "" || val1 === undefined)) {
				return "";
			} else {
				return val + " - " + val1;
			}
		},

		//added by Vinay on 04-12-2019//
		calcualtion: function (val1, val2) {
			if (val1 !== "" && val2 !== "") {
				return Number(val1) - Number(val2);
			} else {
				return 0;
			}
		},

		Gstr2BStatus: function (iStatus) {
			if (iStatus) {
				switch (iStatus.toUpperCase()) {
				case "FAILED":
					return "Error";
				case "SUCCESS":
				case "SUCCESS_NO NEW RECORDS":
					return "Success";
				default:
					return "Warning";
				}
			}
		},

		NCVStatus: function (iStatus) {
			if (iStatus === "NOT_COMPLIANT") {
				return "Error";
			} else if (iStatus === "COMPLIANT") {
				return "Success";
			} else {
				return "Warning"; //"Information";
			}
		},

		//added by Rakesh on 06-12-2019//
		HomeoutOf: function (val1, val2) {
			if (val1 !== null && val2 !== null) {
				return val1 + " out of " + val2;
			} else {
				return 0;
			}
		},

		HomeoutOf1: function (val1) {
			if (val1 !== null) {
				return "out of" + val1;
			} else {
				return 0;
			}
		},

		MicroChart: function (Val) {
			if (Val !== null || Val !== "") {
				return Val;
			} else {
				return 0;
			}
		},

		MicroChart1: function (Val) {
			if (Val === 0) {
				return 1;
			} else {
				return Val;
			}
		},

		HomeTAbValue: function (vValue, val1) {
			//if (val1 !== "E_ITC") {
			if (!isNaN(vValue) && vValue !== null && vValue !== "") {
				var x = vValue;
				x = x.toString();
				var y = x.split(".")[0];
				var dec = x.split(".")[1];
				if (dec === undefined) {
					var DecValue = "." + "00";
				} else {
					if (dec.length === 1) {
						dec = dec + "0";
					}
					DecValue = "." + dec.substr(0, 2);
				}
				var lastThree = y.substring(y.length - 3);
				var otherNumbers = y.substring(0, y.length - 3);
				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + DecValue;
				return res;
				//} 
				//return vValue;
			} else {
				return "0.00";
			}
			return vValue;
		},

		/**
		 * Developed by Vinay Kodam
		 * Called to change Description for Recon Response
		 * @memberOf com.ey.digigst.util.Formatter
		 * @private
		 * @param {stritn} val Value to be formatted
		 * @return {string} val
		 */
		ResponceDesc: function (val) {
			if (val === "A1") {
				return "Accept A2 & ITC A2";
			} else if (val === "A2") {
				return "Accept A2 & ITC PR available";
			} else if (val === "A3") {
				return "Accept A2 & ITC PR Tax";
			} else if (val === "P1") {
				return "Pending ANX2";
			} else if (val === "R1") {
				return "Reject ANX2";
			} else if (val === "R1U1") {
				return "Reject A2 & ITC PR available";
			} else if (val === "R1U2") {
				return "Reject A2 & ITC PR Tax";
			} else if (val === "A1U1") {
				return "ITC PR available of earlier tax period";
			} else if (val === "A1U2") {
				return "ITC PR tax of earlier tax period";
			} else if (val === "A4") {
				return "Accept anx 2 of earlier tax period";
			} else if (val === "U1") {
				return "Provisional ITC PR available";
			} else if (val === "U2") {
				return "Provisional ITC PR Tax";
			} else {
				return val;
			}
		},

		month: function (val) {
			if (val !== undefined && val !== null) {
				var fromtaxyear = val.toString().slice(0, 4);
				var fromtaxmonth = val.toString().slice(4, 6);
				var vStats;
				switch (fromtaxmonth) {
				case "01":
					vStats = "Jan";
					break;
				case "02":
					vStats = "Feb";
					break;
				case "03":
					vStats = "Mar";
					break;
				case "04":
					vStats = "Apr";
					break;
				case "05":
					vStats = "May";
					break;
				case "06":
					vStats = "Jun";
					break;
				case "07":
					vStats = "Jul";
					break;
				case "08":
					vStats = "Aug";
					break;
				case "09":
					vStats = "Sep";
					break;
				case "10":
					vStats = "Oct";
					break;
				case "11":
					vStats = "Nov";
					break;
				case "12":
					vStats = "Dec";
					break;
				}
				return vStats + " " + fromtaxyear;
			} else {
				return val;
			}
		},
		month1: function (val) {
			if (val !== undefined && val !== null) {
				var fromtaxmonth = val.toString().slice(0, 2);
				var fromtaxyear = val.toString().slice(2, 6);
				var vStats;
				switch (fromtaxmonth) {
				case "01":
					vStats = "Jan";
					break;
				case "02":
					vStats = "Feb";
					break;
				case "03":
					vStats = "Mar";
					break;
				case "04":
					vStats = "Apr";
					break;
				case "05":
					vStats = "May";
					break;
				case "06":
					vStats = "Jun";
					break;
				case "07":
					vStats = "Jul";
					break;
				case "08":
					vStats = "Aug";
					break;
				case "09":
					vStats = "Sep";
					break;
				case "10":
					vStats = "Oct";
					break;
				case "11":
					vStats = "Nov";
					break;
				case "12":
					vStats = "Dec";
					break;
				}
				return vStats + " " + fromtaxyear;
			} else {
				return val;
			}
		},

		monthReconSummary: function (val) {
			if (val !== undefined && val !== null) {
				if (!val.includes("-")) {
					var fromtaxyear = val.toString().slice(0, 4);
					var fromtaxmonth = val.toString().slice(4, 6);
					var vStats;
					switch (fromtaxmonth) {
					case "01":
						vStats = "Jan";
						break;
					case "02":
						vStats = "Feb";
						break;
					case "03":
						vStats = "Mar";
						break;
					case "04":
						vStats = "Apr";
						break;
					case "05":
						vStats = "May";
						break;
					case "06":
						vStats = "Jun";
						break;
					case "07":
						vStats = "Jul";
						break;
					case "08":
						vStats = "Aug";
						break;
					case "09":
						vStats = "Sep";
						break;
					case "10":
						vStats = "Oct";
						break;
					case "11":
						vStats = "Nov";
						break;
					case "12":
						vStats = "Dec";
						break;
					}
					return vStats + " " + fromtaxyear;
				} else {
					return val;
				}
			} else {
				return val;
			}
		},

		dateFormatRR: function (vDate) {
			if (vDate && vDate !== " ") {
				var aDate = vDate.split("-");
				return (aDate[2] + "-" + aDate[1] + "-" + aDate[0]);
			}
			return vDate;
		},

		section: function (val) {
			if (val === "Inv") {
				return "B2B";
			} else if (val === "InvA") {
				return "B2B Amendment";
			} else if (val === "CRA Note") {
				return "CR Note Amendment";
			} else if (val === "DRA Note") {
				return "DR Note Amendment";
			} else {
				return val;
			}
		},

		statusReplace: function (a) {
			if (a !== undefined && a !== null) {
				return a.replace(/_/g, ' ');
			}
		},

		ActionTaken: function (val, val1) {
			if (val === "3B Response") {
				if (val1 === undefined) {
					return val;
				} else {
					return val + "-" + val1;
				}
			} else if (val === undefined & val1 !== undefined) {
				return "3B Response" + "-" + val1;
			} else {
				return val;
			}
		},

		//added by Rakesh on 17-08-2020 --- GSTR-3B Auto Calculate//
		calcualtion1: function (val1) {
			if (val1 === 0) {
				return " ";
			} else {
				return val1;
			}
		},

		//added by Rakesh on 18-08-2020 -- Liability Button- GSTR-3B Auto Calculate//
		tableDesc: function (val) { //eslint-disable-line
			switch (val) {
			case "tx":
				return "Tax";
			case "intr":
				return "Interest";
			case "fee":
				return "Late fee";
			case "currMonthUtil":
				return "Current Month Utilization";
			case "clsBal":
				return "Closing Balance";
			}
		},

		docType: function (val) {
			if (val === "R") {
				return "INV";
			} else if (val === "C") {
				return "CR";
			} else if (val === "D") {
				return "DR";
			}
		},
		formatITC04TableType: function (val) {
			if (val === "M2JW (Section 4)") {
				return "Goods sent - Manufacturer to Job Worker (4)";
			} else if (val === "JW2M (Section 5A)") {
				return "Goods received back - Job Worker to Manufacturer (5A)";
			} else if (val === "OtherJW2M (Section 5B)") {
				return "Goods received back - Other Job Worker to Manufacturer (5B)";
			} else if (val === "M2JWSoldfromJW (Section 5C)") {
				return "Goods sold from Job Worker Premises (5C)";
			}
		},
		Gstr2BAction: function (val) {
			switch (val) {
			case "ALL":
				return "GSTR-2B(Complete Data)";
			case "ITC":
				return "GSTR-2B(Available ITC Data)";
			case "NITC":
				return "GSTR-2B(Non Available ITC Data)";
			case "REJ":
				return "GSTR 2B (Rejected ITC Data)";
			case "ALL_NL":
				return "GSTR-2B(Amount Not Linked Data)";
			}
		},
		count: function (val, val1) {
			if (val === undefined && val1 === undefined) {
				return "";
			} else {
				return val + "/" + val1;
			}
		},

		DelVis: function (val, val1) {
			if (val === undefined && val1 !== undefined) {
				return false;
			} else if (val !== undefined && val1 === undefined) {
				return false;
			} else {
				return true;
			}
		},
		emailStatus: function (val, val1) {
			if (val === undefined && val1 === undefined) {
				return "";
			} else {
				return val + "/" + val1;
			}
		},
		//================ Added by chaithra on 27/11/2020 =================//
		TimeStamp: function (value) {
			if (value) {
				return value.replace("T", " : ");
			}
		},
		amountValue2: function (value) {
			if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
				var flag = false,
					s = typeof (value) === "number" ? value.toFixed(2) : value;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var y = s.split(".")[0],
					dec = !s.split(".")[1] ? "00" : s.split(".")[1],
					lastThree = y.substring(y.length - 3),
					otherNumbers = y.substring(0, y.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
				return res;
			} else if (value === null || value === 0 || value === "" || value === undefined) {
				return "0.00";
			} else {
				return value;
			}
		},
		Count1: function (value) {
			if (value === null || value === 0 || value === "" || value === undefined) {
				return "0";
			} else {
				return value;
			}
		},
		GlComputeStatus: function (value) {
			if (value === "" || value === undefined) {
				return "Not Computed";
			} else {
				return value;
			}
		},
		GlInitiateStatus: function (value) {
			if (value === "" || value === undefined) {
				return "Not Initiated";
			} else {
				return value;
			}
		},

		// vendor upload Seg Button- Vendor Communication
		iconVisibleErr: function (err, fileSts) {
			if (err === 0 || fileSts === "Uploaded") {
				return false;
			} else {
				return true;
			}
		},
		iconVisibleInfrm: function (infor, infoStatus) {
			if (infor === 0 || infoStatus === "Uploaded") {
				return false;
			} else {
				return true;
			}
		},
		RegisterType: function (value) {
			if (value === "REGULAR") {
				return "";
			} else {
				return value;
			}
		},
		emailstatus: function (val, val1) {
			if (val === "SENT") {
				return val + "(" + val1 + ")";
			} else {
				return val;
			}
		},

		//============================ GSTR 9 ===================================//
		//============================= Inward Tab ==============================//
		amountValue3: function (section, value) {
			if (section != "4" && section != "5" && section != "6" && section != "6B" &&
				section != "6C" && section != "6D" && section != "6E" &&
				section != "7" && section != "8" && section != "10A" &&
				section != "9" && section != "14" && section != "15" &&
				section != "16") {
				if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
					var flag = false,
						s = typeof (value) === "number" ? value.toFixed(2) : value;

					if (s[0] === "-") {
						flag = true;
						s = s.substr(1);
					}
					var y = s.split(".")[0],
						dec = !s.split(".")[1] ? "00" : s.split(".")[1],
						lastThree = y.substring(y.length - 3),
						otherNumbers = y.substring(0, y.length - 3);

					if (otherNumbers !== "") {
						lastThree = "," + lastThree;
					}
					var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
					// var res1 = Number(res).toFixed(2);
					return res;
				}
				if (value === 0) {
					var res = Number(value).toFixed(2);
					return res;
				}
				return value;
			} else {
				return "";
			}
		},
		InSupply: function (section) {
			if (section == "6") {
				return "Details of ITC availed during the financial year";
			} else if (section == "6A") {
				return "Total amount of input tax credit availed through FORM GSTR-3B (Sum total of table 4A of FORM GSTR-3B)";
			} else if (section == "6B") {
				return "Inward supplies (other than imports & inward supplies liable to reverse charge but includes services received from SEZs)";
			} else if (section == "6B1") {
				return "Inputs";
			} else if (section == "6B2") {
				return "Capital Goods";
			} else if (section == "6B3") {
				return "Input Services";
			} else if (section == "6B4") {
				return "Sub Total (6B1 + 6B2 + 6B3)";
			} else if (section == "6C") {
				return "Inward supplies received from unregistered persons liable to reverse charge  (other than B above) on which tax is paid & ITC availed";
			} else if (section == "6C1") {
				return "Inputs";
			} else if (section == "6C2") {
				return "Capital Goods";
			} else if (section == "6C3") {
				return "Input Services";
			} else if (section == "6C4") {
				return "Sub Total (6C1 + 6C2 + 6C3)";
			} else if (section == "6D") {
				return "Inward supplies received from registered persons liable to reverse charge (other than B above) on which tax is paid & ITC availed";
			} else if (section == "6D1") {
				return "Inputs";
			} else if (section == "6D2") {
				return "Capital Goods";
			} else if (section == "6D3") {
				return "Input Services";
			} else if (section == "6D4") {
				return "Sub Total (6D1 + 6D2 + 6D3)";
			} else if (section == "6CD") {
				return "Total of Table 6C & 6D";
			} else if (section == "6E") {
				return "Import of goods (including supplies from SEZ)";
			} else if (section == "6E1") {
				return "Inputs";
			} else if (section == "6E2") {
				return "Capital Goods";
			} else if (section == "6E3") {
				return "Sub Total (6E1 + 6E2)";
			} else if (section == "6F") {
				return "Import of services (excluding inward supplies from SEZs)";
			} else if (section == "6G") {
				return "Input Tax  credit received from ISD";
			} else if (section == "6H") {
				return "Amount of ITC reclaimed (other than B above) under the provisions of the Act";
			} else if (section == "6I") {
				return "Sub-total (B to H above)";
			} else if (section == "6J") {
				return "Difference (I - A) above";
			} else if (section == "6K") {
				return "Transition Credit through TRAN-1 (including revisions if any)";
			} else if (section == "6L") {
				return "Transition Credit through TRAN-2";
			} else if (section == "6M") {
				return "Any other ITC availed but not specified above";
			} else if (section == "6N") {
				return "Sub-total (K to M above)";
			} else if (section == "6O") {
				return "Total ITC availed (I + N) above";
			} else if (section == "7") {
				return "Details of ITC Reversed &  Ineligible ITC for the financial year";
			} else if (section == "7A") {
				return "As per Rule 37";
			} else if (section == "7B") {
				return "As per Rule 39";
			} else if (section == "7C") {
				return "As per Rule 42";
			} else if (section == "7D") {
				return "As per Rule 43";
			} else if (section == "7CD") {
				return "Total of Table 7C & 7D";
			} else if (section == "7E") {
				return "As per section 17(5)";
			} else if (section == "7F") {
				return "Reversal of TRAN-I credit";
			} else if (section == "7G") {
				return "Reversal of TRAN-II credit";
			} else if (section == "7H") {
				return "Other reversals";
			} else if (section == "7I") {
				return "Total ITC Reversed (Sum of A to H above)";
			} else if (section == "7J") {
				return "Net ITC Available for Utilization (6O - 7I)";
			} else if (section == "8") {
				return "Other ITC related information";
			} else if (section == "8A") {
				return "ITC as per GSTR-2A (Table 3 & 5 thereof)";
			} else if (section == "8B") {
				return "ITC as per sum total 6(B) & 6(H)  above";
			} else if (section == "8C") {
				return "ITC on inward supplies (other than imports & inward supplies liable to reverse charge but includes services received from SEZs)received during FY but availed in next FY upto specified period";
			} else if (section == "8D") {
				return "Difference [A-(B+C)]";
			} else if (section == "8E") {
				return "ITC available but not availed";
			} else if (section == "8F") {
				return "ITC available but ineligible";
			} else if (section == "8G") {
				return "IGST paid  on import of goods (including supplies from SEZ)";
			} else if (section == "8H") {
				return "IGST credit availed on import of goods (as per 6(E) above)";
			} else if (section == "8I") {
				return "Difference (G-H)";
			} else if (section == "8J") {
				return "ITC available but not availed on import of goods (Equal to I)";
			} else if (section == "8K") {
				return "Total ITC to be lapsed in current financial year (E + F + J)";
			} else if (section == "HSNOUTWARD") {
				return "HSN Outward";
			} else if (section == "HSNINWARD") {
				return "HSN Inward";
			} else if (section == "14") {
				return "Differential tax paid on account of declaration in 10 & 11 above";
			} else if (section == "14A") {
				return "Integrated Tax";
			} else if (section == "14B") {
				return "Central Tax";
			} else if (section == "14C") {
				return "State/UT Tax";
			} else if (section == "14D") {
				return "Cess";
			} else if (section == "14E") {
				return "Interest";
			} else if (section == "15") {
				return "Particulars of Demands and Refunds";
			} else if (section == "15A") {
				return "Total Refund claimed";
			} else if (section == "15B") {
				return "Total Refund sanctioned";
			} else if (section == "15C") {
				return "Total Refund Rejected";
			} else if (section == "15D") {
				return "Total Refund Pending";
			} else if (section == "15E") {
				return "Total demand of taxes";
			} else if (section == "15F") {
				return "Total taxes paid in respect of E above";
			} else if (section == "15G") {
				return "Total demands pending out of E above";
			} else if (section == "16") {
				return "Information on supplies received from composition taxpayers, deemed supply under section 143 and goods sent on approval basis";
			} else if (section == "16A") {
				return "Supplies received from Composition taxpayers";
			} else if (section == "16B") {
				return "Deemed supply under section 143";
			} else if (section == "16C") {
				return "Goods sent on approval basis but not returned";
			}

		},
		inSection: function (sec) {
			if (sec == "6I" || sec == "6J" || sec == "7I" || sec == "7J" ||
				sec == "6N" || sec == "6O" || sec == "8B" || sec == "8D" ||
				sec == "8I" || sec == "8J" || sec == "8K" || sec == "6" ||
				sec == "8" || sec == "6B" || sec == "6C" || sec == "6D" ||
				sec == "6E" || sec == "7" || sec == "14" || sec == "15" ||
				sec == "16" || sec == "8H" || sec == "6B4" || sec == "6C4" ||
				sec == "6D4" || sec == "6E3" || sec == "6CD" || sec == "7CD") {
				return "Bold";
			} else {
				return "Standard";
			}
		},
		InVisiHeader: function (sec, head) {
			if (sec == "7H" || head == true) {
				return Boolean(false);
			} else {
				return Boolean(true);
			}
		},
		InNonVisiHeader: function (sec, head) {
			if (sec !== "7H" || head !== true) {
				return Boolean(false);
			} else {
				return Boolean(true);
			}
		},
		Nos: function (val) { //eslint-disable-line
			if (val === "4") {
				return "Details of advances, inwards and outward supplies made during the financial year on which tax is payable";
			} else if (val === "4A") {
				return "Supplies made to un-registered persons (B2C) ";
			} else if (val === "4B") {
				return "Supplies made to registered persons (B2B)";
			} else if (val === "4C") {
				return "Zero rated supply (Export) on payment of tax (except supplies to SEZs)";
			} else if (val === "4D") {
				return "Supply to SEZs on payment of tax";
			} else if (val === "4E") {
				return "Deemed Exports";
			} else if (val === "4F") {
				return "Advances on which tax has been paid but invoice has not been issued (not covered under (A) to (E) above)";
			} else if (val === "4G") {
				return "Inward supplies on which tax is to be paid on reverse charge basis";
			} else if (val === "4G1") {
				return "Supplies on which e-commerce operator is required to pay tax as per section 9(5) (including amendments, if any) [E-commerce operator to report]";
			} else if (val === "4H") {
				return "Sub-total (A to G1 above)";
			} else if (val === "4I") {
				return "Credit Notes issued in respect of transactions specified in (B) to (E) above (-)";
			} else if (val === "4J") {
				return "Debit Notes issued in respect of transactions specified in (B) to (E) above (+)";
			} else if (val === "4K") {
				return "Supplies / tax declared through Amendments (+)";
			} else if (val === "4L") {
				return "Supplies / tax reduced through Amendments (-)	";
			} else if (val === "4M") {
				return "Sub-total (I to L above)";
			} else if (val === "4N") {
				return "Supplies and advances on which tax is to be paid (H + M) above";
			} else if (val === "5") {
				return "Details of Outward supplies made during the financial year on which tax is not payable";
			} else if (val === "5A") {
				return "Zero rated supply (Export) without payment of tax";
			} else if (val === "5B") {
				return "Supply to SEZs without payment of tax";
			} else if (val === "5C") {
				return "Supplies on which tax is to be paid by recipient on reverse charge basis";
			} else if (val === "5C1") {
				return "Supplies on which tax is to be paid by e-commerce operators as per section 9(5) [Supplier to report]";
			} else if (val === "5D") {
				return "Exempted";
			} else if (val === "5E") {
				return "Nil Rated";
			} else if (val === "5F") {
				return "Non-GST supply (includes 'no supply')";
			} else if (val === "5G") {
				return "Sub-total (A to F above)";
			} else if (val === "5H") {
				return "Credit Notes issued in respect of transactions specified in A to F above (-)";
			} else if (val === "5I") {
				return "Debit Notes issued in respect of transactions specified in A to F above (+)";
			} else if (val === "5J") {
				return "Supplies declared through Amendments (+)";
			} else if (val === "5K") {
				return "Supplies reduced through Amendments (-)";
			} else if (val === "5L") {
				return "Sub-Total (H to K above)";
			} else if (val === "5M") {
				return "Turnover on which tax is not to be paid  (G + L) above";
			} else if (val === "5N") {
				return "Total Turnover (including advances) (4N + 5M - 4G - 4G1) above";
			}
		},

		sectionBold: function (section) {
			if (section === "4" || section === "4H" || section === "4M" || section === "4N" || section === "5" || section === "5G" || section ===
				"5L" || section === "5M" || section === "5N" || section === "10B" || section == "10A") {
				return "Bold";
			} else {
				return "Standard";
			}
		},

		TpNos: function (val) {
			if (val === "9") {
				return "Tax Paid";
			} else if (val === "9A") {
				return "Integrated Tax";
			} else if (val === "9B") {
				return "Central Tax";
			} else if (val === "9C") {
				return "State/UT Tax";
			} else if (val === "9D") {
				return "Cess";
			} else if (val === "9E") {
				return "Interest";
			} else if (val === "9F") {
				return "Late fee";
			} else if (val === "9G") {
				return "Penalty";
			} else if (val === "9H") {
				return "Other";
			}
		},

		NosPY: function (val) {
			if (val === "10") {
				return "Supplies / tax declared through Amendments (+) (net of debit notes)";
			} else if (val === "11") {
				return "Supplies / tax reduced through Amendments (-) (net of credit notes)";
			} else if (val === "12") {
				return "Reversal of ITC availed during previous financial year";
			} else if (val === "13") {
				return "ITC availed for the previous financial year";
			} else if (val === "10A") {
				return "Particulars of the transactions for the financial year declared in returns of the next financial year till the specified period";
			} else if (val === "10B") {
				return "Total Turnover (5N+10-11)";
			}
		},

		PYSection: function (val) {
			if (val == "10A" || val == "10B" || val == "6CD" || val == "7CD") {
				return "";
			} else {
				return val;
			}
		},
		DiffTaxSection: function (sec) {
			if (sec == "14A") {
				return "14A";
			}
			if (sec == "14B") {
				return "14B";
			}
			return sec;
		},
		matchedPercent3: function (val) {
			if (val !== undefined && val !== null && val !== "") {
				return (Math.round(val * 100) / 100).toFixed(2)
					// return Number(val).toFixed(2);
			} else {
				return 0;
			}
		},
		PYSectionTY: function (val1, val2) {
			if (val1 == "12" || val1 == "13") {
				return false;
			} else {
				return val2;
			}
		},
		// ============================= SAC Dashboard ====================================//
		Monthaxis: function (val) {
			if (val) {
				var Split1 = val.substring(0, 2);
				var Split2 = val.substring(2, 6);
				var return1 = "";
				if (Split1 == "01") {
					return "JAN";
				} else if (Split1 == "02") {
					return "FEB";
				} else if (Split1 == "03") {
					return "MAR";
				} else if (Split1 == "04") {
					return "APR";
				} else if (Split1 == "05") {
					return "MAY";
				} else if (Split1 == "06") {
					return "JUN";
				} else if (Split1 == "07") {
					return "JUL";
				} else if (Split1 == "08") {
					return "AUG";
				} else if (Split1 == "09") {
					return "SEP";
				} else if (Split1 == "10") {
					return "OCT";
				} else if (Split1 == "11") {
					return "NOV";
				} else if (Split1 == "12") {
					return "DEC";
				}
			}
		},
		MonthCheckBox: function (val) {
			if (val) {
				return true;
			} else {
				return false;
			}
		},
		Denomination: function (value1, value2) {
			if (!isNaN(value2) && value2 !== null && value2 !== 0 && value2 !== "") {
				var vFlagd = "";
				if (value1 == "K") {
					value2 = (Number(value2) / 1000);
					vFlagd = value1;

				} else if (value1 == "L") {
					value2 = (Number(value2) / 100000);
					vFlagd = 'Lac';

				} else if (value1 == "C") {
					value2 = (Number(value2) / 10000000);
					vFlagd = "Cr"

				} else if (value1 == "M") {
					value2 = (Number(value2) / 1000000);

				} else if (value1 == "B") {
					value2 = (Number(value2) / 1000000000);
				}
				var flag = false,
					s = typeof (value2) === "number" ? value2.toFixed(2) : value2;

				if (s[0] === "-") {
					flag = true;
					s = s.substr(1);
				}
				var y = s.split(".")[0],
					dec = !s.split(".")[1] ? "00" : s.split(".")[1],
					lastThree = y.substring(y.length - 3),
					otherNumbers = y.substring(0, y.length - 3);

				if (otherNumbers !== "") {
					lastThree = "," + lastThree;
				}
				var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
				return res;
			}
			if (value2 === 0) {
				var res = Number(value2).toFixed(2);
				return res;
			}
		},

		TransactionType: function (value) {
			if (value === "Others") {
				return "NIL/NON/EXT";
			} else {
				return value;
			}
		},
		// NumberFormat1: function (value) {

		// 	if (!isNaN(value) && value !== null && value !== 0 && value !== "") {
		// 		var flag = false,
		// 			s = typeof (value) === "number" ? value.toFixed(2) : value;

		// 		if (s[0] === "-") {
		// 			flag = true;
		// 			s = s.substr(1);
		// 		}
		// 		var y = s.split(".")[0],
		// 			dec = !s.split(".")[1] ? "00" : s.split(".")[1],
		// 			lastThree = y.substring(y.length - 3),
		// 			otherNumbers = y.substring(0, y.length - 3);

		// 		if (otherNumbers !== "") {
		// 			lastThree = "," + lastThree;
		// 		}
		// 		var res = (flag ? "-" : "") + otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + "." + dec;
		// 		// var res1 = Number(res).toFixed(2);
		// 		return res;
		// 	}
		// 	if (value === 0) {
		// 		var res = Number(value).toFixed(2);
		// 		return res;
		// 	}
		// 	return value;
		// },

		// Added by chaithra for recon type 07/07/2021 ======//
		ReconType: function (value) {
			if (value == "2BPR") {
				return '2B vs PR';
			} else if (value == "AUTO_2APR") {
				return 'Auto 2A/6A vs PR';
			} else if (value == "EINVPR") {
				return 'Inward Einv vs PR';
			} else {
				return '2A/6A vs PR';
			}
		},

		PreReconSummaryDes: function (val) { //eslint-disable-line
			if (val === "INV_B2B") {
				return "B2B Supplies (Other Than SEZ & Deemed Exports)";
			} else if (val === "INV_SEZ") {
				return "Supplies to SEZ";
			} else if (val === "INV_DE") {
				return "Deemed Exports";
			} else if (val === "INV_EXP") {
				return "Exports";
			} else if (val === "C_B2B") {
				return "B2B Supplies (Other Than SEZ & Deemed Exports)";
			} else if (val === "C_SEZ") {
				return "Supplies to SEZ";
			} else if (val === "C_DE") {
				return "Deemed Exports";
			} else if (val === "C_EXP") {
				return "Exports";
			} else if (val === "D_B2B") {
				return "B2B Supplies (Other Than SEZ & Deemed Exports)";
			} else if (val === "D_SEZ") {
				return "Supplies to SEZ";
			} else if (val === "D_DE") {
				return "Deemed Exports";
			} else if (val === "D_EXP") {
				return "Exports";
			} else if (val === "CAN_I") {
				return "Invoice";
			} else if (val === "CAN_C") {
				return "Credit Notes";
			} else if (val === "CAN_D") {
				return "Debit Notes";
			} else if (val === "DEL_I") {
				return "Invoice";
			} else if (val === "DEL_C") {
				return "Credit Notes";
			} else if (val === "DEL_D") {
				return "Debit Notes";
			} else {
				return val;
			}
		},

		editable: function (val) {
			if (val === "Addition in PR" || val === "Addition in 2A") {
				return false;
			} else {
				return true;
			}
		},

		IMSEditable: function (val) {
			if (val === "Match(Imports)" || val === "Mismatch(Imports)") {
				return false;
			} else {
				return true;
			}
		},

		editableRR: function (val) {
			if (val === "Potential-I" || val === "Potential-II" || val === "Logical Match" || val === "Doc No & Doc Date Mismatch") {
				return true;
			} else {
				return false;
			}
		},

		editable1: function (val) {
			if (val === "Force Match Report" || val === "GSTR-3B Locked Records") {
				return false;
			} else {
				return true;
			}
		},

		statusVCR: function (val, val1) {
			if (val === "FILED" && val1 === true) {
				return "Success";
			} else if (val === "FILED" && val1 === false) {
				return "Information";
			} else if (val === "NOT FILED") {
				return "Error";
			} else {
				return "Warning";
			}
		},

		IncData: function (val) {
			if (val === "Modified") {
				return "Modified Records";
			} else if (val === "Deleted") {
				return "Deleted Records";
			} else {
				return val;
			}
		},

		sectionName: function (val) {
			if (val === "4(a)(5)(b)(1)") {
				return "4(a)(5)(b)";
			} else if (val === "4(a)(5)(b)(2)") {
				return "4(a)(5)(c)";
			} else if (val === "4(a)(5)(c)(1)") {
				return "4(a)(5)(d)";
			} else if (val === "4(a)(5)(d)") {
				return "4(a)(5)(e)";
			} else if (val === "4(a)(5)(e)") {
				return "4(a)(5)(f)";
			} else if (val === "4(a)(5)(b)") {
				return "";
			} else if (val === "4(a)(5)(c)") {
				return "";
			} else {
				return val;
			}
		},

		sectionNameOR: function (val) {
			if (val === "4(b)(2)(b)") {
				return "1";
			} else if (val === "4(b)(2)(c)") {
				return "2";
			} else if (val === "4(b)(2)") {
				return "";
			} else if (val === "4(b)(2)(3)") {
				return "3";
			} else if (val === "4(b)(2)(3)(a)") {
				return "(a)";
			} else if (val === "4(b)(2)(3)(b)") {
				return "(b)";
			}
		},

		subSectionNameOR: function (val) {
			switch (val) {
			case "IR_OTHERS":
				return "Total";
			case "OR_RFU":
				return "As per Reversal file upload";
			case "OR_180RRFU":
				return "As per 180 days reversal file upload responses";
			case "OR_175":
				return "Ineligible ITC other than section 17(5)";
			case "OR_175_4A2_4":
				return "Table 4(A)(2), 4(A)(3) Unregistered RCM";
			case "OR_175_4A1_4":
				return "Table 4(A)(1), 4(A)(3) Registered RCM, 4(A)(4)";
			case "OR_175_4A5":
				return "Table 4(A)(5)";
			case "IOG":
				return "IOG";
			case "CTP":
			case "G2BCTax":
			case "APG3B":
				return "Current tax period";
			case "G2BCTax_a":
				return "As per Processed PR";
			case "G2BCTax_b":
				return "As per Get GSTR-2B for current tax period";
			case "G2BCTax_c":
				return "As per GSTR-2BvsPR 3B Response";
			case "ITCRTP":
				return "ITC reclaimed which was reversed in earlier tax period";
			case "ISLRC":
				return "ISLRC";
			case "ISLRP":
				return "Inward supplies liable to reverse charge from registered person";
			case "APG3B_A":
				return "As per Processed PR";
			case "APG3B_B":
				return "As per Get GSTR-2B for current tax period";
			case "APG3B_C":
				return "As per GSTR-2BvsPR 3B Response";
			case "RRTP":
				return "ITC reclaimed which was reversed in earlier tax period";
			case "ISLURP":
				return "Inward supplies liable to reverse charge from Un-registered person";
			case "ISISD":
				return "ISISD";
			case "APPPR":
				return "As per Processed PR";
			case "APG2BARU":
				return "As per Get GSTR-2B and Response Upload";
			case "APG2B":
				return "As per Get GSTR-2B for current tax period";
			case "ITCR":
				return "ITC reclaimed which was reversed in earlier tax period";
			case "DITCG6":
				return "Distributed ITC through GSTR 6 (DigiGST computed)";
			case "DITCG6_4":
				return "As per GSTR-2BvsPR 3B Response";
			case "APPPR":
				return "As per Processed PR"
			case "APGG2B":
				return "As per Get GSTR-2B"
			case "APG2B3BR":
				return "As per GSTR-2BvsPR 3B Response"
			case "APG2A3BR":
				return "As per GSTR-2AvsPR 3B Response"
			case "ITCTP":
				return "ITC reclaimed which was reversed in earlier tax period"
			case "ITCRF":
				return "ITC Reversal File (Re-availment of Credit)"
			case "180DRU":
				return "180 Day Response upload (Re-availment of credit)"
			case "APRRU":
				return "As per Recon Response upload file"
			}
		},

		subSectionNameIcon: function (val) {
			switch (val) {
			case "APG2B":
			case "APG2B_B":
				return "As per Get 2B (Total 2B Tax Values)";
				break;
			case "TLG2B":
			case "TLG2B_B":
				return "Locked 2B vs PR  (Total 2B Tax Values)";
				break;
			case "ALG2B":
			case "ALG2B_B":
				return "Locked 2B vs PR  (Available Tax Values)";
				break;
			}
		},

		validation4D1: function (val) {
			if (val === "GET_4D1(A)") {
				return "Closing Balance of Electronics Credit Reversal and Re-Claimed Statement After last filed GSTR 3B (A) ";
			} else if (val === "4(b)(2)") {
				return "ITC Reversed under 4B2 (B)";
			} else if (val === "4(d)(1)") {
				return "ITC Reclaimed under 4D1 (C)";
			} else {
				return val
			}
		},

		subSectionName3842: function (val) {
			if (val === "total_4b1") {
				return "Total";
			} else if (val === "rule38_42_43") {
				return "As per rules 38, 42 and 43 of CGST Rules";
			} else if (val === "Ineligible_ITC_175") {
				return "Ineligible ITC as per section 17(5)";
			} else if (val === "Table_4(A)(2)") {
				return "Table 4(A)(2), 4(A)(3) Unregistered RCM";
			} else if (val === "Table_4(A)(1)") {
				return "Table 4(A)(1), 4(A)(3) Registered RCM, 4(A)(4)";
			} else if (val === "Table_4(A)(5)") {
				return "Table 4(A)(5)";
			}
		},

		SectionName3842: function (val) {
			if (val === "4(b)(1)") {
				return "";
			} else if (val === "4(b)(1)(1)") {
				return "1";
			} else if (val === "4(b)(1)(2)") {
				return "2";
			} else if (val === "4(b)(1)(2)(a)") {
				return "(a)";
			} else if (val === "4(b)(1)(2)(b)") {
				return "(b)";
			} else {
				return val;
			}
		},

		drc01bPartB: function (val) {
			if (val === "EXLIA01") {
				return "Excess liability paid in earlier form GSTR-3B";
			} else if (val === "INCR102") {
				return "GSTR-1 filed with incorrect details and will be amended in next period";
			} else if (val === "TXMIS03") {
				return "Some transactions of earlier tax period which could not be declared in the FORM GSTR-1/IFF of... ";
			} else if (val === "MISAD04") {
				return "Mistake in reporting of advances received and adjusted against invoices";
			} else if (val === "OTHER05") {
				return "";
			} else {
				return val;
			}
		},

		GLStatus: function (val, val1) {
			if (val === "Not Initiated") {
				return val;
			} else {
				return val + "/" + val1;
			}
		},

		GLStatus1: function (val, val1) {
			if (val === val1) {
				return "Success";
			} else if (val !== val1) {
				return "Error";
			}
		},

		GLIcon: function (val, val1) {
			if (val === val1) {
				return "sap-icon://accept";
			} else if ((val !== val1) && (val !== "Not Initiated")) {
				return "sap-icon://error";
			} else if (val === "Not Initiated") {
				return "";
			}
		},

		AutolockVis: function (ReportType, HDR) {
			if (ReportType === "Force Match Report" || HDR === "L1" || ReportType === "GSTR-3B Locked Records") {
				return false;
			} else {
				return true;
			}
		},

		ERT: function (ReportType, HDR) {
			if (HDR === "L1" || ReportType === "Addition in PR" || ReportType === "Addition in 2A") {
				return false;
			} else {
				return true;
			}
		},

		RecRespLabel: function (val1) {
			if (val1 === "Difference(PR - 2A/6A)" || val1 === "PR Total" || val1 === "2A/6A Total") {
				return "Bold";
			} else {
				return "Standard";
			}
		},

		RecResp: function (val, val1) {
			if ((val > 0 || val < 0) && val1 === "Difference(PR - 2A/6A)") {
				return "Error";
			} else {
				return "None";
			}
		}
	};
});