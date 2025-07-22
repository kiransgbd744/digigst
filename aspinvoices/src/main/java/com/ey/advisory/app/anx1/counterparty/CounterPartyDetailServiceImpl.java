/**
 * 
 */
package com.ey.advisory.app.anx1.counterparty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("CounterPartyDetailServiceImpl")
public class CounterPartyDetailServiceImpl
		implements CounterPartyDetailService {

	@Autowired
	@Qualifier("CounterPartyInfoDaoImpl")
	CounterPartyInfoDao counterPartyInfoDao;

	@Override
	public List<CounterPartyDetailsDto> getAllCounterPartyDetails(
			List<String> sgstinsList, String taxPeriod) {

		if(LOGGER.isDebugEnabled()){
			String msg = "Begin CounterPartyDetailServiceImpl"
					+ ".getAllCounterPartyDetails ,calling counterPartyInfoDao"
					+ "to get List<CounterPartyInfoDto>";
			LOGGER.debug(msg);
		}
		/*The third argument is null b'coz its suppose to be the list of Doc 
		  types and we don't require to pass Doc types for this service, 
		  but the DAO layer expect Doc types as well, which is required for 
		  other services.  
		*/		
		List<CounterPartyInfoDto> response = counterPartyInfoDao
				.getAllCounterPartyInfo(taxPeriod, sgstinsList, null, null);

		Collector<CounterPartyInfoDto, ?, CounterPartyTotalsDto> collector3 = 
				Collectors
				.reducing(new CounterPartyTotalsDto(), cpi -> convertDto(cpi),
						(cpt1, cpt2) -> add(cpt1, cpt2));

		Collector<CounterPartyInfoDto, ?, Map<String, CounterPartyTotalsDto>> 
		collector2 = Collectors.groupingBy(e -> e.getAction(), collector3);

		Collector<CounterPartyInfoDto, ?, Map<String, Map<String,
				CounterPartyTotalsDto>>> collector1 = Collectors
				.groupingBy(e -> e.getTableSection(), collector2);

		Map<String, Map<String, CounterPartyTotalsDto>> map = response.stream()
				.collect(collector1);

		Map<String, CounterPartyDetailsDto> retMap = Arrays
				.asList("3B", "3E", "3F", "3G").stream()
				.map(e -> new CounterPartyDetailsDto(e)).collect(Collectors
						.toMap(o -> o.getType(), Function.identity()));

		Map<String, CounterPartyDetailsDto> mapFromDb = map.entrySet().stream()
				.collect(Collectors.toMap(es -> es.getKey(),
						es -> createDetailsDto(es.getKey(), es.getValue())));

		retMap.putAll(mapFromDb);
		if(LOGGER.isDebugEnabled()){
			String msg = "End CounterPartyDetailServiceImpl"
					+ ".getAllCounterPartyDetails "
					+ ",List<CounterPartyDetailsDto> >";
			LOGGER.debug(msg);
		}

		return new ArrayList<CounterPartyDetailsDto>(retMap.values());
	}

	private CounterPartyTotalsDto convertDto(CounterPartyInfoDto cpi) {
		CounterPartyTotalsDto cpt = new CounterPartyTotalsDto();
		cpt.setCnt(cpi.getCnt());
		cpt.setTaxableValue(cpi.getTaxableValue());
		cpt.setTotalTax(cpi.getTaxPayable());
		return cpt;
	}

	private CounterPartyTotalsDto add(CounterPartyTotalsDto cpt1,
			CounterPartyTotalsDto cpt2) {
		return new CounterPartyTotalsDto(
				cpt1.getTaxableValue().add(cpt2.getTaxableValue()),
				cpt1.getTotalTax().add(cpt2.getTotalTax()),
				cpt1.getCnt() + cpt2.getCnt());
	}

	private CounterPartyDetailsDto createDetailsDto(String tableSection,
			Map<String, CounterPartyTotalsDto> map) {

		final CounterPartyTotalsDto total = new CounterPartyTotalsDto();

		CounterPartyTotalsDto pTotals = map.getOrDefault("P", total);
		CounterPartyTotalsDto rTotals = map.getOrDefault("R", total);
		CounterPartyTotalsDto aTotals = map.getOrDefault("A", total);
		CounterPartyTotalsDto uTotals = map.getOrDefault("U", total);
		CounterPartyTotalsDto nTotals = map.getOrDefault("N", total);
		CounterPartyTotalsDto nsTotals = map.getOrDefault("NS", total);
		CounterPartyTotalsDto sTotals = map.getOrDefault("S", total);

		CounterPartyDetailsDto dto = new CounterPartyDetailsDto(tableSection,
				nsTotals.getCnt(), nsTotals.getTaxableValue(),
				nsTotals.getTotalTax(), sTotals.getCnt(),
				sTotals.getTaxableValue(), sTotals.getTotalTax(),
				aTotals.getCnt(), aTotals.getTaxableValue(),
				aTotals.getTotalTax(), rTotals.getCnt(),
				rTotals.getTaxableValue(), rTotals.getTotalTax(),
				pTotals.getCnt(), pTotals.getTaxableValue(),
				pTotals.getTotalTax(), uTotals.getCnt(),
				uTotals.getTaxableValue(), uTotals.getTotalTax(),
				nTotals.getCnt(), nTotals.getTaxableValue(),
				nTotals.getTotalTax());

		return dto;

	}

}
