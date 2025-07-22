package com.ey.advisory.einv.app.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.repositories.client.EINVNICUserRepository;

@Component("ValidChannelAndValidEndUserBuilder")
public class ValidChannelAndValidEndUserBuilder
		implements APIEINVChannelAndEndUserBuilder {

	@Autowired
	private EINVNICUserRepository einvNICUserRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public NICAPIEndUser buildEndUser(APIParams params) {
		String apiProvider = CommonUtil.getApiProviderEnum(params).name();
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		EINVNICUser nicUser = einvNICUserRepository
				.findByGstinAndIdentifier(gstin, apiProvider);
		if (nicUser == null) {
			String errMsg = String.format(
					"GSTIN User Details for '%s' are not configured", gstin);
			throw new APIException(errMsg);
		}
		String nicUserName = nicUser.getNicUserName();
		String nicPassword = nicUser.getNicPassword();
		return new NICAPIEndUser(gstin, nicUserName, nicPassword, null, null);
	}

	@Override
	public EYNICChannel buildChannel(APIParams params) {
		String apiProvider = CommonUtil.getApiProvider(params);
		Map<String, Config> gspConfig = configManager
				.getConfigs(apiProvider, "nic.gsp");
		return new EYNICChannel(gspConfig.get("nic.gsp.client.id").getValue(),
				gspConfig.get("nic.gsp.client.secret").getValue(), "");
	}

}
