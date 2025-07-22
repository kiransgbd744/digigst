package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.Gstr3BConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BTotalLiabilities")
public class Gstr3BTotalLiabilities {

	@Autowired
	@Qualifier("Gstr3BGstinDashboardDaoImpl")
	private Gstr3BGstinDashboardDaoImpl dashBoardDao;

	public Map<String, BigDecimal> getTotalLiabilities(List<String> gstin,
			String taxPeriod) {

		List<Gstr3BSummaryDto> userInputList = dashBoardDao
				.getAllGstinUserInputDtoList(gstin, taxPeriod);
		

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR3B Entity Dashboard, "
					+ "TotalLiabilities"
					+ " gstnsList %s, taxPeriod %s, DBresponse %s", 
					 gstin, taxPeriod, userInputList);
			LOGGER.debug(msg);
		}


		Map<String, List<Gstr3BSummaryDto>> map = userInputList.stream()
				.collect(Collectors
						.groupingBy(o -> o.getGstin() + "" + o.getTaxPeriod()));
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR3B Entity Dashboard, "
					+ "TotalLiabilities creating Map  %s ", map);
			LOGGER.debug(msg);
			}

		Map<String, BigDecimal> m2 = new HashMap<>();
		map.forEach((k, v) -> {
			m2.put(k, calculateLiabilities(v));
		});
	
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR3B Entity Dashboard, "
					+ "TotalLiabilities before return %s", m2);
			LOGGER.debug(msg);
		}
		return m2;

	}

	private BigDecimal calculateLiabilities(
			List<Gstr3BSummaryDto> userInputList) {

		List<BigDecimal> uList = userInputList.stream()
				.filter(o -> o.getSectionName()
						.equalsIgnoreCase(Gstr3BConstants.Table3_1_A)
						|| o.getSectionName()
								.equalsIgnoreCase(Gstr3BConstants.Table3_1_B)
						|| o.getSectionName()
								.equalsIgnoreCase(Gstr3BConstants.Table3_1_C)
						|| o.getSectionName()
								.equalsIgnoreCase(Gstr3BConstants.Table3_1_D)
						|| o.getSectionName()
								.equalsIgnoreCase(Gstr3BConstants.Table5_1A)
						|| o.getSectionName()
								.equalsIgnoreCase(Gstr3BConstants.Table5_1B))
				.map(o -> o.getCess().add(o.getCgst()).add(o.getIgst())
						.add(o.getSgst()))
				.collect(Collectors.toList());
		if (!uList.isEmpty())
			return uList.stream().reduce(BigDecimal::add).get();

		return BigDecimal.ZERO;
	}
}
