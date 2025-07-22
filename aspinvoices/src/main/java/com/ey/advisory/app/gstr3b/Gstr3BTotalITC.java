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
@Component("Gstr3BTotalITC")
public class Gstr3BTotalITC {

	@Autowired
	@Qualifier("Gstr3BGstinDashboardDaoImpl")
	private Gstr3BGstinDashboardDaoImpl dashBoardDao;

	public Map<String, BigDecimal> getTotalItc(List<String> gstin,
			String taxPeriod) {

		List<Gstr3BSummaryDto> userInputList = dashBoardDao
				.getAllGstinUserInputDtoList(gstin, taxPeriod);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR3B Entity Dashboard, TotalITC"
					+ " gstnsList %s, taxPeriod %s, DBresponse %s",
					gstin, taxPeriod, userInputList );
			LOGGER.debug(msg);
		}

		Map<String, List<Gstr3BSummaryDto>> map = userInputList.stream()
				.collect(Collectors
						.groupingBy(o -> o.getGstin() + "" + o.getTaxPeriod()));
		if (LOGGER.isDebugEnabled()) {
		String msg = String.format("In GSTR3B Entity Dashboard, "
				+ "TotalITC creating Map  %s ", map);
		LOGGER.debug(msg);
		}
		
		Map<String, BigDecimal> m2 = new HashMap<>();
		map.forEach((k, v) -> {
			m2.put(k, calculateItc(v));
		});
		
		if (LOGGER.isDebugEnabled()) {
		String msg = String.format("In GSTR3B Entity Dashboard, "
				+ "TotalITC before return %s", m2);
		LOGGER.debug(msg);
		}
		return m2;

	}

	private BigDecimal calculateItc(List<Gstr3BSummaryDto> userInputList) {

		List<BigDecimal> i4AList = null;
		
			i4AList = userInputList.stream()
					.filter(o -> o.getSectionName()
							.equalsIgnoreCase(Gstr3BConstants.Table4C)) 
					.map(o -> o.getCess().add(o.getCgst()).add(o.getIgst())
							.add(o.getSgst()))
					.collect(Collectors.toList());

		
		if (!i4AList.isEmpty()) {
			
			return i4AList.stream().reduce(BigDecimal::add).get();
		}
		return BigDecimal.ZERO;
	}
}
