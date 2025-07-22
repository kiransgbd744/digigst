package com.ey.advisory.bcadmin.common.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.bcadmin.common.dto.NICCredentialDto;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;

@Component("BcAdminNICUserService")
public class NICUserService {

	public List<NICCredentialDto> getNICCredentials(List<EINVNICUser> einvNIC,
			List<EWBNICUser> ewbNIC) {
		List<NICCredentialDto> nicCredentials = new ArrayList<NICCredentialDto>();
		Set<String> gstins = new HashSet<String>();
		einvNIC.forEach(obj -> gstins.add(obj.getGstin()));
		ewbNIC.forEach(obj -> gstins.add(obj.getGstin()));

		for (String gstin : gstins) {
			NICCredentialDto nicCredentialDto = new NICCredentialDto();
			EINVNICUser einvNICUser = getEinvUser(einvNIC, gstin);
			EWBNICUser ewbNICUser = getEwbUser(ewbNIC, gstin);
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
			if (ewbNICUser != null) {
				nicCredentialDto.setEwbUserName(ewbNICUser.getNicUserName());
				nicCredentialDto.setEwbPassword(ewbNICUser.getNicPassword());

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

}