package com.ey.advisory.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.repositories.master.GstnAPIGroupConfigRepository;
@Component("APIDBPersistenceManagerImpl")
public class APIDBPersistenceManagerImpl implements APIPersistenceManager {
	
	@Autowired
	@Qualifier("GstnAPIGstinConfigRepository")
	private GstnAPIGstinConfigRepository apiGstinConfigRepo;
	
	@Autowired
	@Qualifier("GstnAPIGroupConfigRepository")	
	private GstnAPIGroupConfigRepository apiGrpConfigRepo;
	
	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Override
	public GstnAPIAuthInfo loadAPIAuthInfo(String gstin, String providerName) {
		return gstinAPIAuthInfoRepository.findByGstinAndProviderName(gstin, providerName);
	}

	@Override
	public GstnAPIGstinConfig loadAPIGStinConfig(String gstin) {
		return apiGstinConfigRepo.findByGstin(gstin);
	}

	@Override
	public GstnAPIGroupConfig loadAPIGroupConfig(String groupCode) {
		return apiGrpConfigRepo.findByGroupCode(groupCode);
	}

	@Override
	public GstnAPIAuthInfo saveAPIAuthInfo(GstnAPIAuthInfo apiAuthInfo) {
		return gstinAPIAuthInfoRepository.save(apiAuthInfo);
	}

}
