package com.ey.advisory.app.data.services.gstr9;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidDashboardDto;

/**
 * 
 * @author Saif.S
 *
 */
@Component
public class Gstr9TaxPaidUtil {

	public static Map<String, Gstr9TaxPaidDashboardDto> getGstr9TaxPaidDashboardMap() {
		Map<String, Gstr9TaxPaidDashboardDto> map = new LinkedHashMap<>();
		map.put(Gstr9TaxPaidConstants.Table_9A, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9A, "TaxpaidIgst"));
		map.put(Gstr9TaxPaidConstants.Table_9B, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9B, "TaxpaidCgst"));
		map.put(Gstr9TaxPaidConstants.Table_9C, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9C, "TaxpaidSgst"));
		map.put(Gstr9TaxPaidConstants.Table_9D, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9D, "TaxpaidCess"));
		map.put(Gstr9TaxPaidConstants.Table_9E, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9E, "TaxpaidInterest"));
		map.put(Gstr9TaxPaidConstants.Table_9F, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9F, "TaxpaidLatefee"));
		map.put(Gstr9TaxPaidConstants.Table_9G, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9G, "TaxpaidPenalty"));
		map.put(Gstr9TaxPaidConstants.Table_9H, new Gstr9TaxPaidDashboardDto(
				Gstr9TaxPaidConstants.Table_9H, "TaxpaidOther"));
		return map;
	}

}
