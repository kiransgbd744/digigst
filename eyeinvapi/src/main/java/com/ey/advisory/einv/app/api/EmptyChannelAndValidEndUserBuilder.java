package com.ey.advisory.einv.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.repositories.client.EINVNICUserRepository;

@Component("EmptyChannelAndValidEndUserBuilder")
public class EmptyChannelAndValidEndUserBuilder
		implements APIEINVChannelAndEndUserBuilder {

	@Autowired
	private EINVNICUserRepository einvNICUserRepository;

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
		String clientId = nicUser.getClientId();
		String clientSecret = nicUser.getClientSecret();
		return new NICAPIEndUser(gstin, nicUserName, nicPassword, clientId,
				clientSecret);
	}

	@Override
	public EYNICChannel buildChannel(APIParams params) {
		return new EYNICChannel();
	}

}
