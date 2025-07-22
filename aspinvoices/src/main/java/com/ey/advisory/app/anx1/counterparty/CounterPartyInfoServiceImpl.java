package com.ey.advisory.app.anx1.counterparty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("CounterPartyInfoServiceImpl")
public class CounterPartyInfoServiceImpl implements CounterPartyInfoService {


	@Autowired
	@Qualifier("CounterPartyInfoDaoImpl")
	CounterPartyInfoDao counterPartyInfoDao;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;
	
	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;
	
	
	@Autowired
	@Qualifier("Anx1FetchInfoServiceImpl")
	Anx1FetchInfoService anx1FetchInfoService;

	@Override
	public List<CounterPartyInfoResponseDto> getCounterPartyInfo(
			List<String> sgstinsList, String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()) {
			String str = sgstinsList.stream().collect(Collectors.joining(", "));
			String msg = String.format(
					"Getting the CounterPartyInfo for GSTINs: [%s], "
					+ "TaxPeriod: %s", str, taxPeriod);
			LOGGER.debug(msg);
		}
		/*The third argument is null b'coz its suppose to be the list of Doc 
		  types and we don't require to pass Doc types for this service, 
		  but the DAO layer expect Doc types as well, which is required for 
		  other services.  
		*/		

		List<CounterPartyInfoDto> result = counterPartyInfoDao
				.getAllCounterPartyInfo(taxPeriod, sgstinsList, null, null);

		Collector<CounterPartyInfoDto, ?, BigDecimal> reducer = Collectors
				.reducing(BigDecimal.ZERO, cpi -> cpi.getTaxableValue(),
						(b1, b2) -> b1.add(b2));
		Collector<CounterPartyInfoDto, ?, Map<String, BigDecimal>> grouper =
				Collectors.groupingBy(cpi -> cpi.getAction(), reducer);

		Map<String, Map<String, BigDecimal>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getSgstin(), grouper));
		

		if (LOGGER.isDebugEnabled()) {
			String msg = "Obtained the map: "
					+ "Map<String, Map<String, BigDecimal>> after collecting.";
			LOGGER.debug(msg);
		}
		
		List<CounterPartyInfoResponseDto> resp = map.entrySet().stream()
				.map(e -> { 
					String gstin = e.getKey();
					Map<String, BigDecimal> amtMap = e.getValue();
					return new CounterPartyInfoResponseDto(gstin,
							amtMap.getOrDefault("NS", BigDecimal.ZERO),
							amtMap.getOrDefault("S", BigDecimal.ZERO),
							amtMap.getOrDefault("A", BigDecimal.ZERO),
							amtMap.getOrDefault("R", BigDecimal.ZERO),
							amtMap.getOrDefault("P", BigDecimal.ZERO),
							amtMap.getOrDefault("U", BigDecimal.ZERO),
							amtMap.getOrDefault("N", BigDecimal.ZERO));
				}).collect(Collectors.toList());
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Obtained the list of response objects (without the "
					+ "authToken, status and Date info.)";
			LOGGER.debug(msg);
		}
		
		Map<String, String> authTokenStatusMap = authTokenService
				.getAuthTokenStatusForGstins(sgstinsList);
		
		Map<String, String> stateNamesMap = 
				entityService.getStateNames(sgstinsList);
		
		Map<String, Anx1FetchInfoDto> anx1FetchInfoMap = 
				anx1FetchInfoService.getAnx1FetchInfoForGstins(
						sgstinsList, taxPeriod);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Obtained the AuthTokenMap, "
					+ "StateNamesMap and Anx1FetchInfoMap";
			LOGGER.debug(msg);
		}
		
		resp.stream().forEach(obj -> {
			obj.setAuthTokenStatus(authTokenStatusMap.get(obj.getGstin()));
			obj.setStateName(stateNamesMap.get(obj.getGstin()));
			Anx1FetchInfoDto anx1Obj = anx1FetchInfoMap.get(obj.getGstin());
			obj.setLastAnnx1FetchStatus(
					anx1Obj != null ? anx1Obj.getLastFetchStatus() : null);
			obj.setLastAnnx1FetchDate(
					anx1Obj != null ? anx1Obj.getLastFetchDate() : null);
			
		});
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Before returning the response. Obtained the final "
					+ "list of CounterPartyInfoResponseDto objects.";
			LOGGER.debug(msg);
		}
		
		// setting default value for rest gstins
		List<String> resGstin = resp.stream().map(o -> o.getGstin())
				.collect(Collectors.toList());

		List<String> emptyGstinList = sgstinsList;

		emptyGstinList.removeAll(resGstin);

		if (!emptyGstinList.isEmpty()) {
			for (String gstin : emptyGstinList) {
				CounterPartyInfoResponseDto obj = new CounterPartyInfoResponseDto();
				obj.setAccepted(BigDecimal.ZERO);
				obj.setGstin(gstin);
				obj.setLastAnnx1FetchDate(null);
				obj.setNotSavedTotal(BigDecimal.ZERO);
				obj.setNoAction(BigDecimal.ZERO);
				obj.setPending(BigDecimal.ZERO);
				obj.setRejected(BigDecimal.ZERO);
				obj.setSavedToGstnTotal(BigDecimal.ZERO);
				obj.setUnlock(BigDecimal.ZERO);
				obj.setLastAnnx1FetchStatus(null);
				obj.setAuthTokenStatus(authTokenStatusMap.get(obj.getGstin()));
				obj.setStateName(stateNamesMap.get(obj.getGstin()));
				resp.add(obj);
			}
		}

		return resp;

	}

}
