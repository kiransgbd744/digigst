/**
 * 
 */
package com.ey.advisory.app.anx2.initiaterecon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.gstr2.userdetails.EntityService;

/**
 * @author Arun.KA
 *
 */
@Component("GstinsForEntityServiceImpl")
public class GstinsForEntityServiceImpl implements GstinsForEntityService {

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("Anx2FetchInfoDaoImpl")
	Anx2FetchInfoDao anx2FetchInfo;

	@Override
	public List<InitiateReconFetchGstinsInfoDto> getGstinsInfo
					(long entityId, String taxPeriod) {
		
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap 
		= DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(
						Arrays.asList(entityId),
						inwardSecurityAttributeMap);
		List<String> sgstinsList = dataSecurityAttrMap.get("GSTIN");
		if(CollectionUtils.isEmpty(sgstinsList))
			throw new AppException("User Does not have any gstin");

		Map<String, String> stateNamesMap = entityService
				.getStateNames(sgstinsList);
		
		List<Anx2FetchInfoDto> anx2FetchInfoList = anx2FetchInfo
				.findAnx2FetchInfoByGstins(sgstinsList, taxPeriod);
		
		Map<String, Anx2FetchInfoDto> anx2FetchInfoMap = anx2FetchInfoList
				.stream().collect(Collectors.toMap(o -> o.getGstin(), Function
						.identity()));
		
		
		List<InitiateReconFetchGstinsInfoDto> resp = sgstinsList.stream()
				.map(obj -> new InitiateReconFetchGstinsInfoDto(
						obj , 
						stateNamesMap.get(obj),
						anx2FetchInfoMap.get(obj) != null ? 
								anx2FetchInfoMap.get(obj)
								.getLastFetchStatus() : "",
						anx2FetchInfoMap.get(obj) != null ? 
								anx2FetchInfoMap.get(obj)
								.getLastFetchDate() : null))
				.collect(Collectors.toList());
		
		return resp;
	}

}
