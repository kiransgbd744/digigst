package com.ey.advisory.gstnapi.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultGSTNAuthTokenService")
public class DefaultGSTNAuthTokenService implements GSTNAuthTokenService {

	@Autowired
	@Qualifier("GstinAPIAuthInfoRepository")
	GstinAPIAuthInfoRepository authInfoRepo;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	/**
	 * For the specified gstins, get a Map of GSTIN-status, where GSTIN is the
	 * input GSTIN and status is "A" if the auth token expiry status is 
	 * available in the DB and is active; and "I" otherwise.
	 */
	@Override
	public Map<String, String> getAuthTokenStatusForGstins(
			List<String> gstins) {
		
		Config config = configManager.getConfig("PERFORMANCE", "apis.to.stub",
				TenantContext.getTenantId());
		boolean useStubs = config != null && config.getValue() != null
				? Arrays.asList(config.getValue().split(",")).contains(
						APIIdentifiers.GET_AUTH_TOKEN)
				: Boolean.FALSE;
		if (useStubs) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Stubbing the list of AuthToken statuses "
								+ "for %d GSTINS: [%s]",
						gstins.size(), StringUtils.join(gstins, ", "));
				LOGGER.debug(msg);
			}
			return gstins.stream().distinct()
					.collect(Collectors.toMap(Function.identity(), e -> "A"));

		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Getting the list of AuthToken statuses "
					+ "for %d GSTINS: [%s]", gstins.size(), 
					StringUtils.join(gstins, ", "));
			LOGGER.debug(msg);
		}
		
		// Get the current date to perform expiry check.  
		Date curDate = new Date();
		
		// Get the list of GstnAPIAuthInfo objects from the DB, for the 
		// specified GSTINs. All the GSTINs specified as input may not be 
		// available in the return list. Only those GSTINs for which expiry
		// details are stored in the DB will be available.
		List<GstnAPIAuthInfo> authInfos = authInfoRepo.findByGstins(gstins,
				APIProviderEnum.GSTN.name());  
		
		// Create a function capturing the current  date value (for expiry
		// computation)
		Function<GstnAPIAuthInfo, String> valMapper = 
				o -> (isExpired(curDate, o) ? "I" : "A");

		// Build a map from the auth token info stored in the DB. This map
		// will contain only the GSTINs for which authtoken expiry details
		// is available in the DB. All the input GSTINs may not be available
		// in this map.
		Map<String, String> authInfoMap = authInfos.stream()
				.collect(Collectors.toMap(
						GstnAPIAuthInfo::getGstin, valMapper));		

		
		// Create a map that contains all the GSTINs provided as input, where
		// the status for the GSTINs not in the DB are set as "I".
		Map<String, String> retMap = gstins.stream().distinct().collect(Collectors.toMap(
				Function.identity(), e -> authInfoMap.getOrDefault(e, "I")));
		
		if (LOGGER.isDebugEnabled()) {
			// The mapToString function is called assuming that the map will
			// not be too big.
			String msg = String.format("Obtained the list of AuthToken "
					+ "statuses for %d GSTINS: [%s]", 
					gstins.size(),
					GenUtil.mapToString(retMap));
			LOGGER.debug(msg);
		}
		
		return retMap;
	}
	
	private boolean isExpired(Date curDate, GstnAPIAuthInfo authInfo) {
		if(authInfo.getGstnTokenExpiryTime() == null)
			return true;
		return curDate.compareTo(authInfo.getGstnTokenExpiryTime()) >= 0;
	}
	
	/**
	 * Return the AuthToken status for a single GSTIN. If the auth token status
	 * is availabe in the DB and is active, "A" is returned; 
	 * otherwise "I" is returned.
	 * 
	 */
	public String getAuthTokenStatusForGstin(String gstin) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Getting the Auth Token Status "
					+ "for the GSTIN %s", gstin);
			LOGGER.debug(msg);
		}		
		
		String status = getAuthTokenStatusForGstins(
				ImmutableList.<String>of(gstin)).get(gstin);
		
		String retVal = status == null ? "I" : status;
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Auth Token Status "
					+ "for the GSTIN %s is '%s'", gstin, retVal);
			LOGGER.debug(msg);
		}		
		
		return retVal;
	}
	
	/**
	 * Get the list of statuses for the specified auth tokens as a List. The
	 * order of the status in the return list will be exactly same as the
	 * GSTINs in the input list. Also, the no. of elements in the return list
	 * is exactly same as that of the input list.
	 */
	@Override
	public List<String> getAuthTokenStatuses(List<String> gstins) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Getting the list of AuthToken statuses "
					+ "for %d GSTINS: [%s]", gstins.size(), 
					StringUtils.join(gstins, ", "));
			LOGGER.debug(msg);
		}
		
		Map<String, String> map = getAuthTokenStatusForGstins(gstins);
		List<String> retList = gstins.stream().collect(
				Collectors.mapping(e -> map.getOrDefault(e, "I"), 
						Collectors.toList()));
	
		if (LOGGER.isDebugEnabled()) {
			// The mapToString function is called assuming that the map will
			// not be too big.
			String msg = String.format("Obtained the list of AuthToken "
					+ "statuses for %d GSTINS: [%s]", 
					gstins.size(),
					StringUtils.join(retList, ", "));
			LOGGER.debug(msg);
		}	
		
		return retList;
	}
	
	@Override
	public Map<String, String> getAuthTokenStatusForGroup() {
		
		
		// Get the current date to perform expiry check.  
		Date curDate = new Date();
		
		// Get the list of GstnAPIAuthInfo objects from the DB, for the 
		// specified GSTINs. All the GSTINs specified as input may not be 
		// available in the return list. Only those GSTINs for which expiry
		// details are stored in the DB will be available.
		List<GstnAPIAuthInfo> authInfos = authInfoRepo.findAllByProviderNameAndGroupCode(
				APIProviderEnum.GSTN.name(), TenantContext.getTenantId());  
		
		// Create a function capturing the current  date value (for expiry
		// computation)
		Function<GstnAPIAuthInfo, String> valMapper = 
				o -> (isExpired(curDate, o) ? "I" : "A");

		// Build a map from the auth token info stored in the DB. This map
		// will contain only the GSTINs for which authtoken expiry details
		// is available in the DB. All the input GSTINs may not be available
		// in this map.
		Map<String, String> authInfoMap = authInfos.stream()
				.collect(Collectors.toMap(
						GstnAPIAuthInfo::getGstin, valMapper));		

		Set<String> gstins= authInfoMap.keySet();
		// Create a map that contains all the GSTINs provided as input, where
		// the status for the GSTINs not in the DB are set as "I".
		Map<String, String> retMap = gstins.stream().distinct().collect(Collectors.toMap(
				Function.identity(), e -> authInfoMap.getOrDefault(e, "I")));
		
		if (LOGGER.isDebugEnabled()) {
			// The mapToString function is called assuming that the map will
			// not be too big.
			String msg = String.format("Obtained the list of AuthToken "
					+ "statuses for %d GSTINS: [%s]", 
					gstins.size(),
					GenUtil.mapToString(retMap));
			LOGGER.debug(msg);
		}
		
		return retMap;
	}
}
