package com.ey.advisory.app.anx1.counterparty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("Anx1FetchInfoServiceImpl")
public class Anx1FetchInfoServiceImpl implements Anx1FetchInfoService {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(Anx1FetchInfoServiceImpl.class);

	@Autowired
	@Qualifier("Anx1FetchInfoDaoImpl")
	Anx1FetchInfoDao anx1FetchInfoDao;

	@Override
	public Map<String, Anx1FetchInfoDto> getAnx1FetchInfoForGstins(
			List<String> gstins, String taxPeriod) {
		
		if(LOGGER.isDebugEnabled()){
			String str = gstins.stream().collect(Collectors.joining(", "));
			String msg = String.format(
					"Getting the CounterPartyInfo for GSTINs: [%s], "
					+ "TaxPeriod: %s", str, taxPeriod);
			LOGGER.debug(msg);
		}

		List<Anx1FetchInfoDto> anx1FetchInfoList = anx1FetchInfoDao
				.findAnx1FetchInfoByGstins(gstins, taxPeriod);

		Map<String, Anx1FetchInfoDto> anx1FetchInfoMap = anx1FetchInfoList
				.stream().collect(Collectors.toMap(o -> o.getGstin(), Function
						.identity()));

		return anx1FetchInfoMap;
	}

}
