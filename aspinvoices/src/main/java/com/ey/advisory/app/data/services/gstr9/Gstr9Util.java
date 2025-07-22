package com.ey.advisory.app.data.services.gstr9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTR9Constants;

/**
 * 
 * @author Saif.S
 *
 */
@Component
public class Gstr9Util {

	private static Map<String, String> NATURE_SUP_MAP = null;

	private static Map<String, List<String>> SECTION_MAP = null;

	static {
		Map<String, String> map = new HashMap<>();

		map.put(GSTR9Constants.Table_4A, "B2C");
		map.put(GSTR9Constants.Table_4B, "B2B");
		map.put(GSTR9Constants.Table_4C, "EXP");
		map.put(GSTR9Constants.Table_4D, "SEZ");
		map.put(GSTR9Constants.Table_4E, "DEEMED");
		map.put(GSTR9Constants.Table_4F, "AT");
		map.put(GSTR9Constants.Table_4G, "RCHRG");
		map.put(GSTR9Constants.Table_4H, "SUBTOTAL_AG");
		map.put(GSTR9Constants.Table_4I, "CR_NT");
		map.put(GSTR9Constants.Table_4J, "DR_NT");
		map.put(GSTR9Constants.Table_4K, "AMD_POS");
		map.put(GSTR9Constants.Table_4L, "AMD_NEG");
		map.put(GSTR9Constants.Table_4M, "SUB_TOTALIL");
		map.put(GSTR9Constants.Table_4N, "SUB_ADV");
		
		map.put(GSTR9Constants.Table_5A, "Zero rated supply");
		map.put(GSTR9Constants.Table_5B, "SEZ");
		map.put(GSTR9Constants.Table_5C, "RCHRG");
		map.put(GSTR9Constants.Table_5D, "Exempted");
		map.put(GSTR9Constants.Table_5E, "Nil Rated");
		map.put(GSTR9Constants.Table_5F, "Non-GST");
		map.put(GSTR9Constants.Table_5G, "SUB_TOTALAF");
		map.put(GSTR9Constants.Table_5H, "CR_NT");
		map.put(GSTR9Constants.Table_5I, "DR_NT");
		map.put(GSTR9Constants.Table_5J, "AMD_POS");
		map.put(GSTR9Constants.Table_5K, "AMD_NEG");
		map.put(GSTR9Constants.Table_5L, "SUB_TOTALHK");
		map.put(GSTR9Constants.Table_5M, "Turnover");
		map.put(GSTR9Constants.Table_5N, "Total_TurnOver");

		map.put(GSTR9InwardConstants.Table_6A,
				"TAOfITaxCreditAvailedFromGSTR3B");
		map.put(GSTR9InwardConstants.Table_6B1, "InwardSuppliesInputs");
		map.put(GSTR9InwardConstants.Table_6B2, "InwardSuppliesCapitalGoods");
		map.put(GSTR9InwardConstants.Table_6B3, "InwardSppliesInputServices");
		map.put(GSTR9InwardConstants.Table_6C1,
				"InwardSuppliesUnRegPerRevInputs");
		map.put(GSTR9InwardConstants.Table_6C2,
				"InwardSuppliesUnRegPerCapitalGoods");
		map.put(GSTR9InwardConstants.Table_6C3,
				"InwardSuppliesUnRegPerInputServices");
		map.put(GSTR9InwardConstants.Table_6D1,
				"InwardSupplierRegPerRevInputs");
		map.put(GSTR9InwardConstants.Table_6D2,
				"InwardSuppliesRegPerRevInputsCapitalGoods");
		map.put(GSTR9InwardConstants.Table_6D3,
				"InwardSuppliesRegPerRevInputsInputServices");
		map.put(GSTR9InwardConstants.Table_6E1, "ImportOfGoodsIncSezInput");
		map.put(GSTR9InwardConstants.Table_6E2, "ImportOfGoodsIncSezCapital");
		map.put(GSTR9InwardConstants.Table_6F, "ImportOfGoodsExcludeSez");
		map.put(GSTR9InwardConstants.Table_6G, "InputTaxCR");
		map.put(GSTR9InwardConstants.Table_6H, "ITCClaimedUnderProvAct");
		map.put(GSTR9InwardConstants.Table_6I, "Sub-totBToH");
		map.put(GSTR9InwardConstants.Table_6J, "DifferenceA-I");
		map.put(GSTR9InwardConstants.Table_6K, "TranCreditTRAN-1");
		map.put(GSTR9InwardConstants.Table_6L, "TranCreditTRAN-2");
		map.put(GSTR9InwardConstants.Table_6M, "othrITCRev");
		map.put(GSTR9InwardConstants.Table_6N, "Sub-totKTo M");
		map.put(GSTR9InwardConstants.Table_6O, "TotItcAvail");
		map.put(GSTR9InwardConstants.Table_7A, "AsPerRule37");
		map.put(GSTR9InwardConstants.Table_7B, "AsPerRule39");
		map.put(GSTR9InwardConstants.Table_7C, "AsPerRule42");
		map.put(GSTR9InwardConstants.Table_7D, "AsPerRule43");
		map.put(GSTR9InwardConstants.Table_7E, "AsPerSection17(5)");
		map.put(GSTR9InwardConstants.Table_7F, "ReversalTRAN-Icredit");
		map.put(GSTR9InwardConstants.Table_7G, "ReversalTRAN-IIcredit");
		map.put(GSTR9InwardConstants.Table_7H, "OtherReversals");
		map.put(GSTR9InwardConstants.Table_7I, "TotalITCReversed");
		map.put(GSTR9InwardConstants.Table_7J, "NetITCAvailForUtilization");
		map.put(GSTR9InwardConstants.Table_8A, "ItcAsPerGSTR-2A");
		map.put(GSTR9InwardConstants.Table_8B, "ItcAsPerSumTotal6(B)and6(H)");
		map.put(GSTR9InwardConstants.Table_8C, "ItcInwardSuppliesCFYAvailedNY");
		map.put(GSTR9InwardConstants.Table_8D, "Difference[A-(B+C)]");
		map.put(GSTR9InwardConstants.Table_8E, "ItcAvailableButNotAvailed");
		map.put(GSTR9InwardConstants.Table_8F, "ItcAvailableButIneligible");
		map.put(GSTR9InwardConstants.Table_8G, "IgstPaidOnImportOfGoods");
		map.put(GSTR9InwardConstants.Table_8H,
				"IGstCreditAvailedOnImportOfGoods");
		map.put(GSTR9InwardConstants.Table_8I, "Difference(G-H)");
		map.put(GSTR9InwardConstants.Table_8J,
				"ItcAvailableButNotAvailedOnImportOfGoods");
		map.put(GSTR9InwardConstants.Table_8K, "TotaltcToBelapsedCFY");

		map.put(Gstr9TaxPaidConstants.Table_9A, "TaxpaidIgst");
		map.put(Gstr9TaxPaidConstants.Table_9B, "TaxpaidCgst");
		map.put(Gstr9TaxPaidConstants.Table_9C, "TaxpaidSgst");
		map.put(Gstr9TaxPaidConstants.Table_9D, "TaxpaidCess");
		map.put(Gstr9TaxPaidConstants.Table_9E, "TaxpaidInterest");
		map.put(Gstr9TaxPaidConstants.Table_9F, "TaxpaidLatefee");
		map.put(Gstr9TaxPaidConstants.Table_9G, "TaxpaidPenalty");
		map.put(Gstr9TaxPaidConstants.Table_9H, "TaxpaidOther");
		map.put(Gstr9PyTransInCyConstants.Table_10, "Supp/TaxDeclared+DR");
		map.put(Gstr9PyTransInCyConstants.Table_11, "Supp/TaxReduced-CR");
		map.put(Gstr9PyTransInCyConstants.Table_12, "AvailedItcRevCFY");
		map.put(Gstr9PyTransInCyConstants.Table_13, "AvailedItcRevPFY");
		
		map.put(GSTR9Constants.Table_14A, "DifferentialIgst");
		map.put(GSTR9Constants.Table_14B, "DifferentialCgst");
		map.put(GSTR9Constants.Table_14C, "DifferentialSgst");
		map.put(GSTR9Constants.Table_14D, "DifferentialCess");
		map.put(GSTR9Constants.Table_14E, "DifferentialInterest");
		
		map.put(GSTR9Constants.Table_15A, "TotRefundClaimed");
		map.put(GSTR9Constants.Table_15B, "TotRefundSanctioned");
		map.put(GSTR9Constants.Table_15C, "TotRefundRejected");
		map.put(GSTR9Constants.Table_15D, "TotRefundPending");
		map.put(GSTR9Constants.Table_15E, "TotDemandOfTax");
		map.put(GSTR9Constants.Table_15F, "TotTaxPaidInRespectOfE");
		map.put(GSTR9Constants.Table_15G, "TotDemandPendingOutOfE");
		
		map.put(GSTR9Constants.Table_16A, "SuppRecvFrmCompTaxpayers");
		map.put(GSTR9Constants.Table_16B, "DeemedSupply-143");
		map.put(GSTR9Constants.Table_16C, "AprvdGoodsSentNtReturned");
		
		NATURE_SUP_MAP = Collections.unmodifiableMap(map);
	}

	public static String getNatureOfSuppliesForSubSection(String subSection) {
		return NATURE_SUP_MAP.get(subSection);
	}

	static {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("outward", Arrays.asList("4", "5"));
		map.put("inward", Arrays.asList("6", "7", "8"));
		map.put("taxpaid", Arrays.asList("9"));
		map.put("pyincy", Arrays.asList("10", "11", "12", "13"));
		map.put("difftax", Arrays.asList("14"));
		map.put("deeref", Arrays.asList("15"));
		map.put("compsale", Arrays.asList("16"));
		map.put("hsnoutward", Arrays.asList("17"));
		map.put("hsninward", Arrays.asList("18"));

		SECTION_MAP = Collections.unmodifiableMap(map);
	}

	public static List<String> getSectionListbyTabName(String tabName) {
		if (SECTION_MAP.containsKey(tabName)) {
			return SECTION_MAP.get(tabName);
		}
		return new ArrayList<String>();
	}

}
