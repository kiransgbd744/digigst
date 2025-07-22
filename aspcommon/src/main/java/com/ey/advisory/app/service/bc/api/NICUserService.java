package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NICUserService {

	public List<NICCredentialDto> getNICCredentials(List<EINVNICUser> einvNIC,
			List<EWBNICUser> ewbNIC, Map<String, String> authTokenStatusMap,
			Map<String, String> satateName, List<String> gstins,
			Map<String, Config> configMap, Map<String, String> regType) {
		List<NICCredentialDto> nicCredentials = new ArrayList<NICCredentialDto>();

		List<EINVNICUser> irpEinv = einvNIC.stream()
				.filter(o -> ("EYEINV").equalsIgnoreCase(o.getIdentifier()))
				.collect(Collectors.toList());
		
		List<EINVNICUser> nicEinv= einvNIC.stream()
				.filter(o -> !("EYEINV").equalsIgnoreCase(o.getIdentifier()))
				.collect(Collectors.toList());

		for (String gstin : gstins) {
			NICCredentialDto nicCredentialDto = new NICCredentialDto();
			EINVNICUser einvNICUser = getEinvUser(nicEinv, gstin);
			EINVNICUser einvNICUserIrp = getEinvUser(irpEinv, gstin);
			EWBNICUser ewbNICUser = getEwbUser(ewbNIC, gstin);

//			nicCredentialDto = getNICCredentialByGstin(einvNICUser, ewbNICUser);

			nicCredentialDto.setGstin(gstin);
			nicCredentialDto.setActiveIRP(getActiveIRP(configMap, gstin));
			nicCredentialDto.setAuthToken(authTokenStatusMap.get(gstin));
			nicCredentialDto.setState(satateName.get(gstin));
			nicCredentialDto.setRegType(regType.get(gstin));

			if (einvNICUser != null) {
				nicCredentialDto.setEinvUserName(einvNICUser.getNicUserName());
				nicCredentialDto.setEinvPassword(einvNICUser.getNicPassword());
				if (einvNICUser.getClientId() != null
						&& einvNICUser.getClientSecret() != null) {
					nicCredentialDto.setEinvClientId(einvNICUser.getClientId());
					nicCredentialDto
							.setEinvClientSecret(einvNICUser.getClientSecret());
				}
			}
			if (einvNICUserIrp != null) {
				nicCredentialDto.setEinvUserNameIRP(einvNICUserIrp.getNicUserName());
				nicCredentialDto.setEinvPasswordIRP(einvNICUserIrp.getNicPassword());
				if (einvNICUserIrp.getClientId() != null
						&& einvNICUserIrp.getClientSecret() != null) {
					nicCredentialDto.setEinvClientIdIRP(einvNICUserIrp.getClientId());
					nicCredentialDto
							.setEinvClientSecretIRP(einvNICUserIrp.getClientSecret());
				}
			}
			
			if (ewbNICUser != null) {
				nicCredentialDto.setEwbUserName(ewbNICUser.getNicUserName());
				nicCredentialDto.setEwbPassword(ewbNICUser.getNicPassword());
				if (ewbNICUser != null) {
					nicCredentialDto.setEwbClientId(ewbNICUser.getClientId());
					nicCredentialDto
							.setEwbClientSecret(ewbNICUser.getClientSecret());

				}
			}
			
			if (ewbNICUser != null) {
				nicCredentialDto.setEwbUserNameIRP(ewbNICUser.getNicUserName());
				nicCredentialDto.setEwbPasswordIRP(ewbNICUser.getNicPassword());
				if (ewbNICUser != null) {
					nicCredentialDto.setEwbClientIdIRP(ewbNICUser.getClientId());
					nicCredentialDto
							.setEwbClientSecretIRP(ewbNICUser.getClientSecret());

				}
			}
			nicCredentialDto.setGstin(gstin);
			nicCredentials.add(nicCredentialDto);
		}

		return nicCredentials;
	}

	private EINVNICUser getEinvUser(List<EINVNICUser> einvList, String gstin) {
		EINVNICUser einvnicUser = null;
		for (EINVNICUser user : einvList) {
			if (user.getGstin().equals(gstin)) {
				einvnicUser = user;
				break;
			}
		}
		return einvnicUser;

	}
	
	private EWBNICUser getEwbUser(List<EWBNICUser> ewbList, String gstin) {
		EWBNICUser ewbNicUser = null;
		for (EWBNICUser user : ewbList) {
			if (user.getGstin().equals(gstin)) {
				ewbNicUser = user;
				break;
			}
		}
		return ewbNicUser;

	}

	public NICCredentialDto getNICCredentialByGstin(EINVNICUser einvNICUser,
			EWBNICUser ewbNICUser) {
		NICCredentialDto nicCredentialDto = new NICCredentialDto();
		try {

			if (einvNICUser != null) {
				nicCredentialDto.setEinvUserName(einvNICUser.getNicUserName());
				nicCredentialDto.setEinvPassword(einvNICUser.getNicPassword());
				nicCredentialDto.setGstin(einvNICUser.getGstin());
				if (einvNICUser.getClientId() != null
						&& einvNICUser.getClientSecret() != null) {
					nicCredentialDto.setEinvClientId(einvNICUser.getClientId());
					nicCredentialDto
							.setEinvClientSecret(einvNICUser.getClientSecret());
				}
			}
			if (ewbNICUser != null) {
				nicCredentialDto.setEwbUserName(ewbNICUser.getNicUserName());
				nicCredentialDto.setEwbPassword(ewbNICUser.getNicPassword());
				nicCredentialDto.setGstin(ewbNICUser.getGstin());
			}
		} catch (Exception ee) {
			LOGGER.error("exception ", ee);
		}
		return nicCredentialDto;
	}

	private String getActiveIRP(Map<String, Config> configMap, String gstin) {

		Config config = configMap.get("einv.source.enabled." + gstin);
		if (config != null) {
			
			if(config.getValue().equalsIgnoreCase("EYEINV")) {
				return "IRP";
			}
			return "NIC";
		}
		else {

			return "NIC";
		}
	}

}