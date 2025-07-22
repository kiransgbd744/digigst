/**
 * 
 */
package com.ey.advisory.einv.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Khalid1.Khan
 *
 */
public class EyEInvCommonUtil {

	public static String deriveTransactionCateg(String supType,
			String category) {
		final List<String> supTypes = ImmutableList.of("EXPT", "EXPWT", "SEZWP",
				"SEZWOP", "SEDWP", "SEDWOP", "DXP");
		return supTypes.contains(supType) ? "EXP" : category;
	}

	public static String getBuyerGstin(String cgstin, String supplyType) {
		if (Strings.isNullOrEmpty(cgstin) && supplyType.equalsIgnoreCase("EXPT")
				|| supplyType.equalsIgnoreCase("EXPWT"))
			return "URP";
		return cgstin;
	}

	public static Pair<String, String> deriveExportCategoryAndPayFlag(
			String supType) {
		Pair<String, String> catAndPayPair = null;
		switch (supType) {
		case "EXPT":
			catAndPayPair = new Pair<>("DIR", "Y");
			break;
		case "EXPWT":
			catAndPayPair = new Pair<>("DIR", "N");
			break;
		case "SEZWP":
			catAndPayPair = new Pair<>("SEZ", "Y");
			break;
		case "SEZWOP":
			catAndPayPair = new Pair<>("SEZ", "N");
			break;
		case "SEDWP":
			catAndPayPair = new Pair<>("SED", "Y");
			break;
		case "SEDWOP":
			catAndPayPair = new Pair<>("SED", "N");
			break;
		case "DXP":
			catAndPayPair = new Pair<>("DEM", "Y");
			break;
		default:
			break;

		}
		return catAndPayPair;

	}

	public static String convertDocType(String docType) {
		if ("CR".equalsIgnoreCase(docType))
			return "CRN";
		else if ("DR".equalsIgnoreCase(docType))
			return "DBN";
		else
			return docType;
	}

	public static String getStateNameByStateCode(String stateCode) {
		Map<String, String> stateNameMap = new HashMap<>();
		stateNameMap.put("01", "JAMMU AND KASHMIR");
		stateNameMap.put("1", "JAMMU AND KASHMIR");
		stateNameMap.put("02", "HIMACHAL PRADESH");
		stateNameMap.put("2", "HIMACHAL PRADESH");
		stateNameMap.put("03", "PUNJAB");
		stateNameMap.put("3", "PUNJAB");
		stateNameMap.put("04", "CHANDIGARH");
		stateNameMap.put("4", "CHANDIGARH");
		stateNameMap.put("05", "UTTARAKHAND");
		stateNameMap.put("5", "UTTARAKHAND");
		stateNameMap.put("06", "HARYANA");
		stateNameMap.put("6", "HARYANA");
		stateNameMap.put("07", "DELHI");
		stateNameMap.put("7", "DELHI");
		stateNameMap.put("08", "RAJASTHAN");
		stateNameMap.put("8", "RAJASTHAN");
		stateNameMap.put("09", "UTTAR PRADESH");
		stateNameMap.put("9", "UTTAR PRADESH");
		stateNameMap.put("10", "BIHAR");
		stateNameMap.put("11", "SIKKIM");
		stateNameMap.put("12", "ARUNACHAL PRADESH");
		stateNameMap.put("13", "NAGALAND");
		stateNameMap.put("14", "MANIPUR");
		stateNameMap.put("15", "MIZORAM");
		stateNameMap.put("16", "TRIPURA");
		stateNameMap.put("17", "MEGHALAYA");
		stateNameMap.put("18", "ASSAM");
		stateNameMap.put("19", "WEST BENGAL");
		stateNameMap.put("20", "JHARKHAND");
		stateNameMap.put("21", "ORISSA");
		stateNameMap.put("22", "CHHATTISGARH");
		stateNameMap.put("23", "MADHYA PRADESH");
		stateNameMap.put("24", "GUJARAT");
		stateNameMap.put("25", "DAMAN AND DIU");
		stateNameMap.put("26", "DADAR AND NAGAR HAVELI");
		stateNameMap.put("27", "MAHARASTRA");
		stateNameMap.put("29", "KARNATAKA");
		stateNameMap.put("30", "GOA");
		stateNameMap.put("31", "LAKSHADWEEP");
		stateNameMap.put("32", "KERALA");
		stateNameMap.put("33", "TAMIL NADU");
		stateNameMap.put("34", "PUDUCHERRY");
		stateNameMap.put("35", "ANDAMAN AND NICOBAR");
		stateNameMap.put("36", "TELANGANA");
		stateNameMap.put("37", "ANDHRA PRADESH");
		stateNameMap.put("38", "LADAKH");

		return stateNameMap.get(stateCode);
	}

	public static String getSupplyType(String supplyType, String cgstin) {
		supplyType = supplyType.toUpperCase();
		String supplytypeValue = null;

		if (supplyType.equalsIgnoreCase("DTA") && cgstin != null
				&& cgstin.length() == 15)
			supplytypeValue = "B2B";

		if (supplyType.equals("TAX") || supplyType.equals("NIL")
				|| supplyType.equals("NON") || supplyType.equals("EXT"))
			supplytypeValue = "B2B";

		switch (supplyType) {
		case "SEZWP":
			supplytypeValue = "SEZWP";
			break;
		case "SEZWOP":
			supplytypeValue = "SEZWOP";
			break;
		case "EXPT":
			supplytypeValue = "EXPWP";
			break;
		case "EXPWT":
			supplytypeValue = "EXPWOP";
			break;
		case "DXP":
			supplytypeValue = "DEXP";
			break;
		default:
			break;

		}
		return supplytypeValue;

	}

	public static BigDecimal getGstRate(BigDecimal igstRate,
			BigDecimal cgstRate, BigDecimal sgstRate) {
		if (cgstRate == null)
			cgstRate = BigDecimal.ZERO;
		if (igstRate == null)
			igstRate = BigDecimal.ZERO;
		if (sgstRate == null)
			sgstRate = BigDecimal.ZERO;
		return cgstRate.add(sgstRate).add(igstRate);
	}

	public static BigDecimal getValueCesVal(
			BigDecimal invoiceCessAdvaloremAmount,
			BigDecimal invoiceCessSpecificAmount) {
		if (invoiceCessAdvaloremAmount == null)
			invoiceCessAdvaloremAmount = BigDecimal.ZERO;
		if (invoiceCessSpecificAmount == null)
			invoiceCessSpecificAmount = BigDecimal.ZERO;
		return invoiceCessAdvaloremAmount.add(invoiceCessSpecificAmount);

	}

	public static BigDecimal getStCessVal(BigDecimal invoiceStateCessAmount,
			BigDecimal invStateCessSpecificAmt) {
		if (invoiceStateCessAmount == null)
			invoiceStateCessAmount = BigDecimal.ZERO;
		if (invStateCessSpecificAmt == null)
			invStateCessSpecificAmt = BigDecimal.ZERO;
		return invoiceStateCessAmount.add(invStateCessSpecificAmt);
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
		case "INTRANSIT":
			transactionTypeValue = "5";
			break;
		default:
			break;

		}
		return transactionTypeValue;

	}

	public static Integer getFromPinocode(Integer dispatcherPincode,
			Integer supplierPincode, String docCategory) {
		if (dispatcherPincode == null) {
			return supplierPincode;
		}

		return dispatcherPincode;
	}

	public static Integer getToPinocode(Integer shipToPinCode,
			Integer customerPinCode, String docCategory) {
		if (shipToPinCode == null) {
			return customerPinCode;
		}
		return shipToPinCode;
	}

	public static String getstateCode(String stateCode) {
		if (stateCode == null || !"99".equalsIgnoreCase(stateCode))
			return stateCode;
		else
			return "96";
	}

	public static Quartet<Boolean, Boolean, Boolean, Boolean> eligibleAddresstobeNIC(
			String docCategory, Boolean isAddressSuppReq) {

		if (!isAddressSuppReq) {
			return new Quartet<Boolean, Boolean, Boolean, Boolean>(true, true,
					true, true);
		}
		if (docCategory.equals("DIS")) {
			return new Quartet<Boolean, Boolean, Boolean, Boolean>(true, true,
					true, false);
		} else if (docCategory.equals("SHP")) {
			return new Quartet<Boolean, Boolean, Boolean, Boolean>(true, true,
					false, true);
		} else if (docCategory.equals("REG")) {
			return new Quartet<Boolean, Boolean, Boolean, Boolean>(true, true,
					false, false);
		} else {
			return new Quartet<Boolean, Boolean, Boolean, Boolean>(true, true,
					true, true);
		}

	}

}
