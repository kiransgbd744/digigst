/**
 * 
 */
package com.ey.advisory.ewb.common;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

/**
 * @author Khalid1.Khan
 *
 */
public class EyEwbCommonUtil {

	private EyEwbCommonUtil() {
	}

	public static String convertDocType(String docType) {
		String docTypeValue = null;

		switch (docType) {
		case "INV":
			docTypeValue = "INV";
			break;
		case "BIL":
			docTypeValue = "BOS";
			break;
		case "BOE":
			docTypeValue = "BOE";
			break;
		case "CHL":
			docTypeValue = "DLC";
			break;
		case "OTH":
			docTypeValue = "OTH";
			break;
		default:
			break;

		}
		return docTypeValue;

	}

	public static String getDocType(String docType) {
		String docTypeValue = null;

		switch (docType) {
		case "INV":
			docTypeValue = "INV";
			break;
		case "BOS":
			docTypeValue = "BIL";
			break;
		case "BOE":
			docTypeValue = "BOE";
			break;
		case "DLC":
			docTypeValue = "CHL";
			break;
		case "OTH":
			docTypeValue = "OTH";
			break;
		default:
			break;

		}
		return docTypeValue;

	}

	public static Integer getFromPinocode(Integer dispatcherPincode,
			Integer supplierPincode, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("SHP"))) {
			return supplierPincode;
		}

		return dispatcherPincode;
	}

	public static String getFromAdd1(String dispatcherBuildingNumber,
			String supplierBuildingNumber, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("SHP"))) {
			return supplierBuildingNumber;
		}
		return dispatcherBuildingNumber;
	}

	public static String getFromAdd2(String dispatcherBuildingName,
			String supplierBuildingName, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("SHP"))) {
			return supplierBuildingName;
		}
		return dispatcherBuildingName;
	}

	public static String getFromPlace(String dispatcherLocation,
			String supplierLocation, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("SHP"))) {
			return supplierLocation;
		}

		return dispatcherLocation;
	}

	public static String getActFromStateCode(String dispatcherStateCode,
			String supplierStateCode, String docCategory) {
		if (dispatcherStateCode != null
				&& "96".equalsIgnoreCase(dispatcherStateCode))
			dispatcherStateCode = "99";
		if (supplierStateCode != null
				&& "96".equalsIgnoreCase(supplierStateCode))
			supplierStateCode = "99";
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("SHP"))) {
			return supplierStateCode != null ? supplierStateCode : null;
		}
		return dispatcherStateCode != null ? dispatcherStateCode : null;
	}

	public static String getToAdd1(String shipToBuildingNumber,
			String customerBuildingNumber, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("DIS"))) {
			return customerBuildingNumber;
		}
		return shipToBuildingNumber;
	}

	public static String getToAdd2(String shipToBuildingName,
			String customerBuildingName, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("DIS"))) {
			return customerBuildingName;
		}
		return shipToBuildingName;
	}

	public static Integer getToPinocode(Integer shipToPinCode,
			Integer customerPinCode, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("DIS"))) {
			return customerPinCode;
		}
		return shipToPinCode;
	}

	public static String getToPlace(String shipToLocation,
			String customerLocation, String docCategory) {
		if (docCategory != null && (docCategory.equalsIgnoreCase("REG")
				|| docCategory.equalsIgnoreCase("DIS"))) {
			return customerLocation;
		}
		return shipToLocation;
	}

	public static String getActToStateCode(String shipToStateCode,
			String customerStateCode, String docCategory) {
		if (shipToStateCode != null && "96".equalsIgnoreCase(shipToStateCode))
			shipToStateCode = "99";
		if (customerStateCode != null
				&& "96".equalsIgnoreCase(customerStateCode))
			customerStateCode = "99";
		if (shipToStateCode == null && docCategory != null
				&& (docCategory.equalsIgnoreCase("REG")
						|| docCategory.equalsIgnoreCase("DIS"))) {
			return customerStateCode != null ? customerStateCode : null;
		}
		return shipToStateCode != null ? shipToStateCode : null;
	}

	public static String getSubSupplyType(String subSupplyType) {

		String supTypeValue = null;

		switch (subSupplyType) {
		case "TAX":
			supTypeValue = "1";
			break;
		case "IMP":
			supTypeValue = "2";
			break;
		case "EXP":
			supTypeValue = "3";
			break;
		case "JWK":
			supTypeValue = "4";
			break;
		case "FOU":
			supTypeValue = "5";
			break;
		case "JWR":
			supTypeValue = "6";
			break;
		case "SR":
			supTypeValue = "7";
			break;
		case "OTH":
			supTypeValue = "8";
			break;
		case "SKD":
			supTypeValue = "9";
			break;
		case "LNS":
			supTypeValue = "10";
			break;
		case "UNK":
			supTypeValue = "11";
			break;
		case "EXB":
			supTypeValue = "12";
			break;
		default:
			break;

		}
		return supTypeValue;

	}

	public static String getTransactionType(String docCategory) {

		String transactionTypeValue = null;

		switch (docCategory) {
		case "REG":
			transactionTypeValue = "1";
			break;
		case "DIS":
			transactionTypeValue = "3";
			break;
		case "SHP":
			transactionTypeValue = "2";
			break;
		case "CMB":
			transactionTypeValue = "4";
			break;
		default:
			break;

		}
		return transactionTypeValue;

	}

	public static String getDocCateDesc(String transactionType) {

		String docCategDesc = null;

		switch (transactionType) {
		case "1":
			docCategDesc = "REG";
			break;
		case "2":
			docCategDesc = "SHP";
			break;
		case "3":
			docCategDesc = "DIS";
			break;
		case "4":
			docCategDesc = "CMB";
			break;
		default:
			break;

		}
		return docCategDesc;

	}

	public static String getTransMode(String transMode) {

		String transactionTypeValue = null;
		if (StringUtils.isNumeric(transMode))
			return transMode;
		if (transMode == null)
			transMode = "";

		switch (transMode.toUpperCase()) {
		case "ROAD":
			transactionTypeValue = "1";
			break;
		case "RAIL":
			transactionTypeValue = "2";
			break;
		case "AIR":
			transactionTypeValue = "3";
			break;
		case "SHIP":
			transactionTypeValue = "4";
			break;
		case "SHIP/ROAD CUM SHIP":
			transactionTypeValue = "4";
			break;
		case "INTRANSIT":
			transactionTypeValue = "5";
			break;
		default:
			break;

		}
		return transactionTypeValue;

	}

	public static String getFromGstin(String sgstin, String transactionType) {
		if ("I".equalsIgnoreCase(transactionType)
				&& Strings.isNullOrEmpty(sgstin))
			return "URP";
		return sgstin;
	}

	public static String getToGstin(String cgstin, String transactionType) {
		if ("O".equalsIgnoreCase(transactionType)
				&& Strings.isNullOrEmpty(cgstin))
			return "URP";
		return cgstin;
	}

	public static String getTransModeDesc(String transMode) {
		String transactionTypeValue = null;

		if (transMode == null)
			transMode = "";

		switch (transMode) {
		case "1":
			transactionTypeValue = "ROAD";
			break;
		case "2":
			transactionTypeValue = "RAIL";
			break;
		case "3":
			transactionTypeValue = "AIR";
			break;
		case "4":
			transactionTypeValue = "SHIP";
			break;
		case "5":
			transactionTypeValue = "INTRANSIT";
			break;
		default:
			break;

		}
		return transactionTypeValue;
	}

	public static String getstateCode(String stateCode) {
		if (stateCode == null || !"96".equalsIgnoreCase(stateCode))
			return stateCode;
		else
			return "99";
	}

}
