/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstr3BConstants;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component
public class Gstr3bUtil {
	

	public Map<String, Gstr3BGstinDashboardDto> getGstr3BGstinDashboardMap() {
		
		Map<String, Gstr3BGstinDashboardDto> map = new LinkedHashMap<>();
		map.put(Gstr3BConstants.Table3_1, new Gstr3BGstinDashboardDto("L1",
				false, Gstr3BConstants.Table3_1, "TORCIS"));
		map.put(Gstr3BConstants.Table3_1_A, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_A, "OTS1"));
		map.put(Gstr3BConstants.Table3_1_B, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_B, "OTS2"));
		map.put(Gstr3BConstants.Table3_1_C, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_C, "OTS3"));
		map.put(Gstr3BConstants.Table3_1_D, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_D, "IS"));
		map.put(Gstr3BConstants.Table3_1_E, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_E, "NON_GST"));
		// 3.1.1
		map.put(Gstr3BConstants.Table3_1_1, new Gstr3BGstinDashboardDto("L1",
				false, Gstr3BConstants.Table3_1_1, "DOSN"));
		map.put(Gstr3BConstants.Table3_1_1_A, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_1_A, "TSOEO"));
		map.put(Gstr3BConstants.Table3_1_1_B, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_1_1_B, "TSMRP"));
		
		map.put(Gstr3BConstants.Table3_2, new Gstr3BGstinDashboardDto("L1",
				false, Gstr3BConstants.Table3_2, "ISS"));
		map.put(Gstr3BConstants.Table3_2_A, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_2_A, "SMTURP"));
		map.put(Gstr3BConstants.Table3_2_B, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_2_B, "SMTCTP"));
		map.put(Gstr3BConstants.Table3_2_C, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table3_2_C, "SMTUINH"));

		map.put(Gstr3BConstants.Table4, new Gstr3BGstinDashboardDto("L1", false,
				Gstr3BConstants.Table4, "E_ITC"));
		map.put(Gstr3BConstants.Table4A, new Gstr3BGstinDashboardDto("L2",
				false, Gstr3BConstants.Table4A, "ITC_Avail"));
		map.put(Gstr3BConstants.Table4A1, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4A1, "IOG"));
		map.put(Gstr3BConstants.Table4A2, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4A2, "IOS"));
		map.put(Gstr3BConstants.Table4A3, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4A3, "ISLTR"));
		map.put(Gstr3BConstants.Table4A4, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4A4, "ISFISD"));
		map.put(Gstr3BConstants.Table4A5, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4A5, "AO_ITC"));

		map.put(Gstr3BConstants.Table4B, new Gstr3BGstinDashboardDto("L2",
				false, Gstr3BConstants.Table4B, "ITC_R"));
		map.put(Gstr3BConstants.Table4B1, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4B1, "AP42&43"));
		map.put(Gstr3BConstants.Table4B2, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4B2, "IR_OTHERS"));
		map.put(Gstr3BConstants.Table4C, new Gstr3BGstinDashboardDto("L2", true,
				Gstr3BConstants.Table4C, "NET_ITC_AVAIL"));
		map.put(Gstr3BConstants.Table4D, new Gstr3BGstinDashboardDto("L2",
				false, Gstr3BConstants.Table4D, "I_ITC"));
		map.put(Gstr3BConstants.Table4D1, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4D1, "APS17"));
		map.put(Gstr3BConstants.Table4D2, new Gstr3BGstinDashboardDto("L3",
				true, Gstr3BConstants.Table4D2, "II_OTHERS"));
		map.put(Gstr3BConstants.Table5, new Gstr3BGstinDashboardDto("L1", false,
				Gstr3BConstants.Table5, "ENANGST"));
		map.put(Gstr3BConstants.Table5A, new Gstr3BGstinDashboardDto("L2", true,
				Gstr3BConstants.Table5A, "FSUC"));
		map.put(Gstr3BConstants.Table5B, new Gstr3BGstinDashboardDto("L2", true,
				Gstr3BConstants.Table5B, "NGST_SUPPLY"));
		map.put(Gstr3BConstants.Table5_1, new Gstr3BGstinDashboardDto("L1",
				false, Gstr3BConstants.Table5_1, "IALF"));
		map.put(Gstr3BConstants.Table5_1A, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table5_1A, "INTEREST"));
		map.put(Gstr3BConstants.Table5_1B, new Gstr3BGstinDashboardDto("L2",
				true, Gstr3BConstants.Table5_1B, "LATE_FEES"));

		map.put(Gstr3BConstants.Table7_1, new Gstr3BGstinDashboardDto("L1",
				true, Gstr3BConstants.Table7_1, "PPLIA"));

		return map;
	}

	/*
	 * The Below method is responsible for parsing the gstn response and
	 * creating a map of table section and its corresponding json object
	 */

	public Map<String, JsonObject> parseJsonResponse(String gstnResponse) {
		Map<String, JsonObject> map = new LinkedHashMap<>();
		
		Map<String, JsonObject> map_4A = new HashMap<String, JsonObject>();
		Map<String, JsonObject> map_4B = new HashMap<String, JsonObject>();
		Map<String, JsonObject> map_4D = new HashMap<String, JsonObject>();
		
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject osup_det = null;
			JsonObject osup_zero = null;
			JsonObject osup_nil_exmp = null;
			JsonObject isup_rev = null;
			JsonObject osup_nongst = null;
			JsonObject unreg_details = null;
			JsonObject comp_details = null;
			JsonObject uin_details = null;
			JsonObject itc_avl1 = null;
			JsonObject itc_avl2 = null;
			JsonObject itc_avl3 = null;
			JsonObject itc_avl4 = null;
			JsonObject itc_avl5 = null;
			JsonObject itc_rev1 = null;
			JsonObject itc_rev2 = null;
			JsonObject itc_net = null;
			JsonObject itc_inelg1 = null;
			JsonObject itc_inelg2 = null;
			JsonObject isup_details1 = null;
			JsonObject isup_details2 = null;
			JsonObject intr_details = null;
			JsonObject ltfee_details = null;
			// 3.3 changes
			// JsonObject eco_dtls = null;
			JsonObject eco_sup = null;
			JsonObject eco_reg_sup = null;

			// 7.1
			JsonObject liab_brkupDtls = null;

			JsonObject suppDetails = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("sup_details");
			if (suppDetails != null) {
				osup_det = suppDetails.has("osup_det")
						? suppDetails.getAsJsonObject("osup_det") : null;
				osup_zero = suppDetails.has("osup_zero")
						? suppDetails.getAsJsonObject("osup_zero") : null;
				osup_nil_exmp = suppDetails.has("osup_nil_exmp")
						? suppDetails.getAsJsonObject("osup_nil_exmp") : null;
				isup_rev = suppDetails.has("isup_rev")
						? suppDetails.getAsJsonObject("isup_rev") : null;
				osup_nongst = suppDetails.has("osup_nongst")
						? suppDetails.getAsJsonObject("osup_nongst") : null;
			}
			JsonObject inter_sup = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("inter_sup");
			if (inter_sup != null) {
				JsonArray unreg_details_arr = inter_sup
						.getAsJsonArray("unreg_details");
				
				JsonObject ob1 = new JsonObject();
				if (unreg_details_arr != null) {
				ob1.addProperty("txval", BigDecimal.ZERO);
				ob1 = addJsonArrayObjects(ob1, unreg_details_arr);
				}
				unreg_details = ob1;
				JsonArray comp_details_arr = inter_sup
						.getAsJsonArray("comp_details");
				JsonObject ob2 = new JsonObject();
				if (comp_details_arr != null) {
					ob2.addProperty("txval", BigDecimal.ZERO);
					ob2 = addJsonArrayObjects(ob2, comp_details_arr);
				}
				comp_details = ob2;

				JsonArray uin_details_arr = inter_sup
						.getAsJsonArray("uin_details");
				JsonObject ob3 = new JsonObject();
				if (uin_details_arr != null) {
					ob3.addProperty("txval", BigDecimal.ZERO);
					ob3 = addJsonArrayObjects(ob3, uin_details_arr);
				}
				uin_details = ob3;
			}

			// 3.3
			JsonObject ecoDtls = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("eco_dtls");
			if (ecoDtls != null) {
				eco_sup = ecoDtls.has("eco_sup")
						? ecoDtls.getAsJsonObject("eco_sup") : null;
				eco_reg_sup = ecoDtls.has("eco_reg_sup")
						? ecoDtls.getAsJsonObject("eco_reg_sup") : null;
			}

			JsonObject itc_elg = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("itc_elg");
			if (itc_elg != null) {
				JsonArray itc_avl = itc_elg.getAsJsonArray("itc_avl");
				if (itc_avl != null) {

					itc_avl1 = itc_avl.get(0).getAsJsonObject();
					String type1 = itc_avl1.get("ty").getAsString();
					getElgITCMapping(type1, itc_avl1,map_4A);
					
					if (itc_avl.get(1) != null) {
						itc_avl2 = itc_avl.get(1).getAsJsonObject();
					String type2 = itc_avl2.get("ty").getAsString();
					getElgITCMapping(type2, itc_avl2,map_4A);
					}
					
					if (itc_avl.get(2) != null) {
						itc_avl3 = itc_avl.get(2).getAsJsonObject();
					String type3 = itc_avl3.get("ty").getAsString();
					getElgITCMapping(type3, itc_avl3,map_4A);
					}
					
					if (itc_avl.get(3) != null) {
						itc_avl4 = itc_avl.get(3).getAsJsonObject();
					String type4 = itc_avl4.get("ty").getAsString();
					getElgITCMapping(type4, itc_avl4,map_4A);
					}
					
					if (itc_avl.get(4) != null) {
						itc_avl5 = itc_avl.get(4).getAsJsonObject();
					String type5 = itc_avl5.get("ty").getAsString();
					getElgITCMapping(type5, itc_avl5,map_4A);
					}
				}

				JsonArray itc_rev = itc_elg.getAsJsonArray("itc_rev");
				if (itc_rev != null) {
					itc_rev1 = itc_rev.get(0).getAsJsonObject();
					String type1 = itc_rev1.get("ty").getAsString();
					getElgITCRevMapping(type1, itc_rev1,map_4B);
					
					if (itc_rev.get(1) != null)
						itc_rev2 = itc_rev.get(1).getAsJsonObject();
					String type2 = itc_rev2.get("ty").getAsString();
					getElgITCRevMapping(type2, itc_rev2,map_4B);
				}

				itc_net = itc_elg.getAsJsonObject("itc_net");

				JsonArray itc_inelg = itc_elg.getAsJsonArray("itc_inelg");
				if (itc_inelg != null && itc_inelg.size() > 0) {
					itc_inelg1 = itc_inelg.get(0).getAsJsonObject();
					String type1 = itc_inelg1.get("ty").getAsString();
					getElgITCInElgMapping(type1, itc_inelg1, map_4D);
					
					if (itc_inelg.size() > 1) {
					if (itc_inelg.get(1) != null)
						itc_inelg2 = itc_inelg.get(1).getAsJsonObject();
					String type2 = itc_inelg2.get("ty").getAsString();
					getElgITCInElgMapping(type2, itc_inelg2,map_4D);
					}
				}
			}
			JsonObject inward_sup = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("inward_sup");
			if (inward_sup != null) {
				JsonArray isup_details = inward_sup
						.getAsJsonArray("isup_details");
				isup_details1 = isup_details.get(0).getAsJsonObject();
				if (isup_details.get(1) != null)
					isup_details2 = isup_details.get(1).getAsJsonObject();
			}

			JsonObject intr_ltfee = (JsonObject) jsonParser.parse(gstnResponse)
					.getAsJsonObject().get("intr_ltfee");
			if (intr_ltfee != null) {
				intr_details = intr_ltfee.has("intr_details")
						? intr_ltfee.getAsJsonObject("intr_details") : null;
				ltfee_details = intr_ltfee.has("ltfee_details")
						? intr_ltfee.getAsJsonObject("ltfee_details") : null;
			}

			JsonArray saveLiaBrk = (JsonArray) jsonParser.parse(gstnResponse)
					.getAsJsonObject().getAsJsonArray("liab_breakup");
			if (saveLiaBrk != null) {
				JsonObject ob1 = new JsonObject();
				ob1 = addJsonArrayObjectsForSavePst(ob1, saveLiaBrk);
				liab_brkupDtls = ob1;
			}

			map.put(Gstr3BConstants.Table3_1_A, osup_det);
			map.put(Gstr3BConstants.Table3_1_B, osup_zero);
			map.put(Gstr3BConstants.Table3_1_C, osup_nil_exmp);
			map.put(Gstr3BConstants.Table3_1_D, isup_rev);
			map.put(Gstr3BConstants.Table3_1_E, osup_nongst);
			map.put(Gstr3BConstants.Table3_2_A, unreg_details);
			map.put(Gstr3BConstants.Table3_2_B, comp_details);
			map.put(Gstr3BConstants.Table3_2_C, uin_details);

			map.put(Gstr3BConstants.Table3_1_1_A, eco_sup);
			map.put(Gstr3BConstants.Table3_1_1_B, eco_reg_sup);

			//changes for us 76660
			map.put(Gstr3BConstants.Table4A1, map_4A.get("IMPG"));
			map.put(Gstr3BConstants.Table4A2, map_4A.get("IMPS"));
			map.put(Gstr3BConstants.Table4A3, map_4A.get("ISRC"));
			map.put(Gstr3BConstants.Table4A4, map_4A.get("ISD"));
			map.put(Gstr3BConstants.Table4A5, map_4A.get("OTH"));
			
			map.put(Gstr3BConstants.Table4B1, map_4B.get("RUL"));
			map.put(Gstr3BConstants.Table4B2, map_4B.get("OTH"));
			
			map.put(Gstr3BConstants.Table4C, itc_net);
			
			map.put(Gstr3BConstants.Table4D1,  map_4D.get("RUL"));
			map.put(Gstr3BConstants.Table4D2,  map_4D.get("OTH"));
			
			map.put(Gstr3BConstants.Table5A, isup_details1);
			map.put(Gstr3BConstants.Table5B, isup_details2);
			map.put(Gstr3BConstants.Table5_1A, intr_details);
			map.put(Gstr3BConstants.Table5_1B, ltfee_details);
			map.put(Gstr3BConstants.Table7_1, liab_brkupDtls);
			return map;
		} catch (AppException ex) {
			LOGGER.error(gstnResponse);
			LOGGER.error(ex.getMessage(), ex);
			return map;

		} catch (Exception ex) {
			LOGGER.error(gstnResponse);
			LOGGER.error(ex.getMessage(), ex);
			return map;

		}
	}

	public JsonObject addJsonArrayObjects(JsonObject obj, JsonArray arr) {
		for (int i = 0; i < arr.size(); i++) {
			JsonObject ob = arr.get(i).getAsJsonObject();
			obj.addProperty("txval",
					ob.get("txval").getAsBigDecimal()
							.add(obj.has("txval")
									? obj.get("txval").getAsBigDecimal()
									: BigDecimal.ZERO));
			obj.addProperty("iamt",
					ob.get("iamt").getAsBigDecimal()
							.add(obj.has("iamt")
									? obj.get("iamt").getAsBigDecimal()
									: BigDecimal.ZERO));
		}
		return obj;
	}

	public JsonObject addJsonArrayObjectsForSavePst(JsonObject obj,
			JsonArray arr) {
		for (int i = 0; i < arr.size(); i++) {
			JsonObject ob = arr.get(i).getAsJsonObject()
					.getAsJsonObject("liability");
			obj.addProperty("camt",
					ob.get("camt").getAsBigDecimal()
							.add(obj.has("camt")
									? obj.get("camt").getAsBigDecimal()
									: BigDecimal.ZERO));
			obj.addProperty("iamt",
					ob.get("iamt").getAsBigDecimal()
							.add(obj.has("iamt")
									? obj.get("iamt").getAsBigDecimal()
									: BigDecimal.ZERO));
			obj.addProperty("samt",
					ob.get("samt").getAsBigDecimal()
							.add(obj.has("samt")
									? obj.get("samt").getAsBigDecimal()
									: BigDecimal.ZERO));

			obj.addProperty("csamt",
					ob.get("csamt").getAsBigDecimal()
							.add(obj.has("csamt")
									? obj.get("csamt").getAsBigDecimal()
									: BigDecimal.ZERO));

		}
		return obj;
	}

	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3BExcemptNilNonGstnMap() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table5A, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table5A, "FSUC"));
		map.put(Gstr3BConstants.Table5B, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table5B, "NGST_SUPPLY"));
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4_1() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();
		map.put(Gstr3BConstants.Table4_A_1_1, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1, "G2BCTax"));
		map.put(Gstr3BConstants.Table4_A_1_1_A, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_A, "G2BCTax_a"));
		map.put(Gstr3BConstants.Table4_A_1_1_B, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_B, "G2BCTax_b"));
		map.put(Gstr3BConstants.Table4_A_1_1_C, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_C, "G2BCTax_c"));
		map.put(Gstr3BConstants.Table4_A_1_2, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_2, "ITCRTP"));
		return map;
	}

	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4_3() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();
		map.put(Gstr3BConstants.Table4_A_3_1, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1, "ISLRP"));
		map.put(Gstr3BConstants.Table4_A_3_1_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a, "APG3B"));
		
		map.put(Gstr3BConstants.Table4_A_3_1_a_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_a, "APG3B_A"));
		map.put(Gstr3BConstants.Table4_A_3_1_a_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_b, "APG3B_B"));
		map.put(Gstr3BConstants.Table4_A_3_1_a_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_c, "APG3B_C"));
		
		map.put(Gstr3BConstants.Table4_A_3_1_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_b, "RRTP"));
		map.put(Gstr3BConstants.Table4_A_3_2, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_2, "ISLURP"));
		return map;
	}
	

	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4_4() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_A_4_1, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_1, "APPPR"));
		map.put(Gstr3BConstants.Table4_A_4_2, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_2, "APG2BARU"));
		map.put(Gstr3BConstants.Table4_A_4_2_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_2_a, "APG2B"));
		map.put(Gstr3BConstants.Table4_A_4_2_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_2_b, "ITCR"));
		map.put(Gstr3BConstants.Table4_A_4_3, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_3, "DITCG6"));
		
		map.put(Gstr3BConstants.Table4_A_4_4, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_4_4, "DITCG6_4"));
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_1() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_1_1_1_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_1_1_1_a, "APG2B"));
		map.put(Gstr3BConstants.Table4_a_1_1_1_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_1_1_1_b, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_1_1_1_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_1_1_1_c, "ALG2B"));
		return map;
	}
	
	//new
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_1_B() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_A_1_1_B_A, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_B_A, "APG2B_B"));
		map.put(Gstr3BConstants.Table4_A_1_1_B_B, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_B_B, "TLG2B_B"));
		map.put(Gstr3BConstants.Table4_A_1_1_B_C, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_1_1_B_C, "ALG2B_B"));
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_12() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_1_1_2_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_1_1_2_a, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_1_1_2_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_1_1_2_b, "ALG2B"));
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_31() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_3_3_1_a_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_3_3_1_a_a, "APG2B"));
		map.put(Gstr3BConstants.Table4_a_3_3_1_a_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_3_3_1_a_b, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_3_3_1_a_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_3_3_1_a_c, "ALG2B"));
	
		return map;
	}
	
	//new 
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_31_B() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_A_3_1_a_b_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_b_a, "APG2B_B"));
		map.put(Gstr3BConstants.Table4_A_3_1_a_b_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_b_b, "TLG2B_B"));
		map.put(Gstr3BConstants.Table4_A_3_1_a_b_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_A_3_1_a_b_c, "ALG2B_B"));
	
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_31b() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_3_3_1_b_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_3_3_1_b_a, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_3_3_1_b_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_3_3_1_b_b, "ALG2B"));
		return map;
	}
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_42a() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_4_4_2_a_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_4_4_2_a_a, "APG2B"));
		map.put(Gstr3BConstants.Table4_a_4_4_2_a_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_4_4_2_a_b, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_4_4_2_a_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_4_4_2_a_c, "ALG2B"));
	
		return map;
	}
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_42b() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_4_4_2_b_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_4_4_2_b_a, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_4_4_2_b_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_4_4_2_b_b, "ALG2B"));
		return map;
	}
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_51b() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_5_5_1_b_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_5_5_1_b_a, "APG2B"));
		map.put(Gstr3BConstants.Table4_a_5_5_1_b_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_5_5_1_b_b, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_5_5_1_b_c, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_5_5_1_b_c, "ALG2B"));
	
		return map;
	}
	
	public Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4A_51c() {
		Map<String, Gstr3BExcemptNilNonGstnDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table4_a_5_5_2_c_a, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_5_5_2_c_a, "TLG2B"));
		map.put(Gstr3BConstants.Table4_a_5_5_2_c_b, new Gstr3BExcemptNilNonGstnDto(
				Gstr3BConstants.Table4_a_5_5_2_c_b, "ALG2B"));
		return map;
	}

	public Map<String, Gstr3BInterStateSuppliesDto> gstr3BInterStateSuppliesMap() {
		Map<String, Gstr3BInterStateSuppliesDto> map = new LinkedHashMap<>();

		map.put(Gstr3BConstants.Table3_2_A, new Gstr3BInterStateSuppliesDto(
				Gstr3BConstants.Table3_2_A, "SMTURP"));

		map.put(Gstr3BConstants.Table3_2_B, new Gstr3BInterStateSuppliesDto(
				Gstr3BConstants.Table3_2_B, "SMTCTP"));

		map.put(Gstr3BConstants.Table3_2_C, new Gstr3BInterStateSuppliesDto(
				Gstr3BConstants.Table3_2_C, "SMTUINH"));
		return map;
	}

	public static ImmutableMap<String, String> getsupplyTypeDescByTableName() {

		ImmutableMap<String, String> map = ImmutableMap
				.<String, String>builder()
				.put(Gstr3BConstants.Table3_1,
						"Tax on Outward and Reverse Charge Inward Supplies")
				.put(Gstr3BConstants.Table3_1_A,
						"Outward Taxable Supplies (Other than zero rated, nil rated and exempted)")
				.put(Gstr3BConstants.Table3_1_B,
						"Outward Taxable Supplies (Zero Rated)")
				.put(Gstr3BConstants.Table3_1_C,
						"Other Outward Supplies (Nil Rated, Exempted)")
				.put(Gstr3BConstants.Table3_1_D,
						"Inward Supplies (Liable to Reverse Charge)")
				.put(Gstr3BConstants.Table3_1_E, "Non-GST Outward Supplies")
				.put(Gstr3BConstants.Table3_1_1, "Details of Supplies notified under section 9(5)")
				.put(Gstr3BConstants.Table3_1_1_A, "Taxable supplies on which E-com operator pays tax u/s 9(5)")
				.put(Gstr3BConstants.Table3_1_1_B, "Taxable supplies made by registered person through E-com operator")
				.put(Gstr3BConstants.Table3_2, "Inter-State Supplies")
				.put(Gstr3BConstants.Table3_2_A,
						"Supplies made to Unregistered Persons")
				.put(Gstr3BConstants.Table3_2_B,
						"Supplies made to Composition Taxable Persons")
				.put(Gstr3BConstants.Table3_2_C, "Supplies made to UIN holders")
				.put(Gstr3BConstants.Table4, "Eligible ITC")
				.put(Gstr3BConstants.Table4A,
						"ITC Available (Whether in full or part)")
				.put(Gstr3BConstants.Table4A1, "Import of Goods")
				.put(Gstr3BConstants.Table4A2, "Import of Service")
				.put(Gstr3BConstants.Table4A3,
						"Inward Supplies liable to Reverse Charge (Other than 1 & 2 above)")
				.put(Gstr3BConstants.Table4A4, "Inward Supplies from ISD")
				.put(Gstr3BConstants.Table4A5, "All other ITC")
				.put(Gstr3BConstants.Table4B, "ITC Reversed")
				.put(Gstr3BConstants.Table4B1,
						"As per rules 42 & 43 of CGST Rules")
				.put(Gstr3BConstants.Table4B2, "Others")
				.put(Gstr3BConstants.Table4C, "Net ITC Available (A)-(B)")
				.put(Gstr3BConstants.Table4D, "Ineligible ITC")
				.put(Gstr3BConstants.Table4D1, "As per Section 17(5)")
				.put(Gstr3BConstants.Table4D2, "Others")
				.put(Gstr3BConstants.Table5,
						"Exempt, Nil & Non-GST Inward Supplies")
				.put(Gstr3BConstants.Table5A,
						"From a Supplier under composition scheme, Exempt & Nil Rated Supply")
				.put(Gstr3BConstants.Table5B, "Non GST Supply")
				.put(Gstr3BConstants.Table5_1, "Interest & Late Fee Payable")
				.put(Gstr3BConstants.Table5_1A, "Interest")
				.put(Gstr3BConstants.Table5_1B, "Late Fees").build();
		return map;
	}
	
	private void getElgITCMapping(String type, JsonObject obj,
			Map<String, JsonObject> map_4A) {

		map_4A.put(type, obj);

	}

	private void getElgITCRevMapping(String type, JsonObject obj, 
			Map<String, JsonObject> map_4B) {

		map_4B.put(type, obj);

	}

	private void getElgITCInElgMapping(String type, JsonObject obj, 
			Map<String, JsonObject> map_4D) {

		map_4D.put(type, obj);

	}

}
 