package com.ey.advisory.app.data.services.gstr9;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9GstinInOutwardDashBoardDTO;
import com.google.common.base.Strings;

/**
 * 
 * @author Jithendra.B
 *
 */
@Component
public class Gstr9InwardUtil {
	public Map<String, Gstr9GstinInOutwardDashBoardDTO> getGstr3BGstinDashboardMap() {
		Map<String, Gstr9GstinInOutwardDashBoardDTO> map = new LinkedHashMap<>();

		map.put(GSTR9InwardConstants.Table_6,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6, true));
		map.put(GSTR9InwardConstants.Table_6A,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6A, false));
		map.put(GSTR9InwardConstants.Table_6B,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6B, true));

		map.put(GSTR9InwardConstants.Table_6B1,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6B1, false));
		map.put(GSTR9InwardConstants.Table_6B2,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6B2, false));
		map.put(GSTR9InwardConstants.Table_6B3,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6B3, false));
		map.put(GSTR9InwardConstants.Table_6B4,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6B4, false));

		map.put(GSTR9InwardConstants.Table_6C,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6C, true));

		map.put(GSTR9InwardConstants.Table_6C1,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6C1, false));
		map.put(GSTR9InwardConstants.Table_6C2,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6C2, false));
		map.put(GSTR9InwardConstants.Table_6C3,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6C3, false));
		map.put(GSTR9InwardConstants.Table_6C4,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6C4, false));

		map.put(GSTR9InwardConstants.Table_6D,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6D, true));

		map.put(GSTR9InwardConstants.Table_6D1,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6D1, false));
		map.put(GSTR9InwardConstants.Table_6D2,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6D2, false));
		map.put(GSTR9InwardConstants.Table_6D3,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6D3, false));
		map.put(GSTR9InwardConstants.Table_6D4,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6D4, false));
		map.put(GSTR9InwardConstants.Table_6CD,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6CD, false));

		map.put(GSTR9InwardConstants.Table_6E,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6E, true));

		map.put(GSTR9InwardConstants.Table_6E1,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6E1, false));
		map.put(GSTR9InwardConstants.Table_6E2,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6E2, false));
		map.put(GSTR9InwardConstants.Table_6E3,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6E3, false));

		map.put(GSTR9InwardConstants.Table_6F,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6F, false));
		map.put(GSTR9InwardConstants.Table_6G,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6G, false));
		map.put(GSTR9InwardConstants.Table_6H,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6H, false));
		map.put(GSTR9InwardConstants.Table_6I,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6I, false));
		map.put(GSTR9InwardConstants.Table_6J,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6J, false));
		map.put(GSTR9InwardConstants.Table_6K,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6K, false));
		map.put(GSTR9InwardConstants.Table_6L,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6L, false));
		map.put(GSTR9InwardConstants.Table_6M,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6M, false));
		map.put(GSTR9InwardConstants.Table_6N,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6N, false));
		map.put(GSTR9InwardConstants.Table_6O,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_6O, false));
		map.put(GSTR9InwardConstants.Table_7,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7, true));
		map.put(GSTR9InwardConstants.Table_7A,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7A, false));
		map.put(GSTR9InwardConstants.Table_7B,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7B, false));
		map.put(GSTR9InwardConstants.Table_7C,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7C, false));
		map.put(GSTR9InwardConstants.Table_7D,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7D, false));
		map.put(GSTR9InwardConstants.Table_7CD,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7CD, false));

		map.put(GSTR9InwardConstants.Table_7E,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7E, false));
		map.put(GSTR9InwardConstants.Table_7F,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7F, false));
		map.put(GSTR9InwardConstants.Table_7G,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7G, false));
		map.put(GSTR9InwardConstants.Table_7H,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7H, false));
		map.put(GSTR9InwardConstants.Table_7I,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7I, false));
		map.put(GSTR9InwardConstants.Table_7J,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_7J, false));
		map.put(GSTR9InwardConstants.Table_8,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8, true));
		map.put(GSTR9InwardConstants.Table_8A,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8A, false));
		map.put(GSTR9InwardConstants.Table_8B,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8B, false));
		map.put(GSTR9InwardConstants.Table_8C,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8C, false));
		map.put(GSTR9InwardConstants.Table_8D,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8D, false));
		map.put(GSTR9InwardConstants.Table_8E,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8E, false));
		map.put(GSTR9InwardConstants.Table_8F,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8F, false));
		map.put(GSTR9InwardConstants.Table_8G,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8G, false));
		map.put(GSTR9InwardConstants.Table_8H,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8H, false));
		map.put(GSTR9InwardConstants.Table_8I,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8I, false));
		map.put(GSTR9InwardConstants.Table_8J,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8J, false));
		map.put(GSTR9InwardConstants.Table_8K,
				new Gstr9GstinInOutwardDashBoardDTO(
						GSTR9InwardConstants.Table_8K, false));
		return map;
	}

	public Map<String, String> getItcTypeMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(GSTR9InwardConstants.Table_6B1, "ip");
		map.put(GSTR9InwardConstants.Table_6B2, "cg");
		map.put(GSTR9InwardConstants.Table_6B3, "is");

		map.put(GSTR9InwardConstants.Table_6C1, "ip");
		map.put(GSTR9InwardConstants.Table_6C2, "cg");
		map.put(GSTR9InwardConstants.Table_6C3, "is");

		map.put(GSTR9InwardConstants.Table_6D1, "ip");
		map.put(GSTR9InwardConstants.Table_6D2, "cg");
		map.put(GSTR9InwardConstants.Table_6D3, "is");

		map.put(GSTR9InwardConstants.Table_6E1, "ip");
		map.put(GSTR9InwardConstants.Table_6E2, "cg");

		return map;
	}

	public static String getTaxperiodFromFY(String fy) {

		String taxPeriod = null;
		if (!Strings.isNullOrEmpty(fy)) {
			int year = Integer.parseInt(fy.split("-")[0]);
			year = year + 1;
			taxPeriod = "03" + String.valueOf(year);
		}

		return taxPeriod;
	}

}
