package com.ey.advisory.app.data.services.gstr9;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9PyTransInCyDashboardDto;

/**
 * 
 * @author Saif.S
 *
 */
@Component
public class Gstr9PyTransInCyUtil {

	public Map<String, Gstr9PyTransInCyDashboardDto> getGstr9PyTransInCyDashboardMap() {
		Map<String, Gstr9PyTransInCyDashboardDto> map = new LinkedHashMap<>();
		map.put(Gstr9PyTransInCyConstants.Table_10,
				new Gstr9PyTransInCyDashboardDto(
						Gstr9PyTransInCyConstants.Table_10,
						"Supp/TaxDeclared+DR"));
		map.put(Gstr9PyTransInCyConstants.Table_11,
				new Gstr9PyTransInCyDashboardDto(
						Gstr9PyTransInCyConstants.Table_11,
						"Supp/TaxReduced-CR"));
		map.put(Gstr9PyTransInCyConstants.Table_12,
				new Gstr9PyTransInCyDashboardDto(
						Gstr9PyTransInCyConstants.Table_12,
						"AvailedItcRevCFY"));
		map.put(Gstr9PyTransInCyConstants.Table_13,
				new Gstr9PyTransInCyDashboardDto(
						Gstr9PyTransInCyConstants.Table_13,
						"AvailedItcRevPFY"));
		return map;
	}
}
