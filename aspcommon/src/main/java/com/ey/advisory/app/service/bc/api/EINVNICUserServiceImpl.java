package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstindto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.repositories.client.EINVNICUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EINVNICUserServiceImpl implements EINVNICUserService {

	@Autowired
	@Qualifier("EINVNICUserRepository")
	private EINVNICUserRepository einvNICUserRepo;

	@Override
	public List<EINVNICUser> getAllEINVNICUsers(List<String> gstins) {
		List<EINVNICUser> eInvList = new ArrayList<EINVNICUser>();
		try {
			eInvList = einvNICUserRepo.findByGstinIn(gstins);
			if (eInvList == null || eInvList.isEmpty()) {
				String errMsg = String.format(
						"There are no EINV Users onboarded for a group %s",
						TenantContext.getTenantId());
				LOGGER.debug(errMsg);
				//throw new AppException(errMsg);
			}
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the EINV USers", ee);
			throw new AppException(ee);

		}
		return eInvList;
	}

	@Override
	public int updateEINVNICUser(EINVNICUser einvNICUser) {
		int updatedResult = 0;
		try {
			EINVNICUser persistedEntity = einvNICUserRepo
					.findByGstinAndIdentifier(einvNICUser.getGstin(), 
							einvNICUser.getIdentifier());
			if (persistedEntity == null) {
				einvNICUser.setUpdatedOn(LocalDateTime.now());
				einvNICUserRepo.save(einvNICUser);
				updatedResult++;
			} else {
				persistedEntity.setNicUserName(einvNICUser.getNicUserName());
				persistedEntity.setNicPassword(einvNICUser.getNicPassword());
				if (einvNICUser.getClientId() != null
						&& einvNICUser.getClientSecret() != null) {
					persistedEntity.setClientId(einvNICUser.getClientId());
					persistedEntity
							.setClientSecret(einvNICUser.getClientSecret());
				}
				persistedEntity.setUpdatedBy(einvNICUser.getUpdatedBy());
				persistedEntity.setUpdatedOn(LocalDateTime.now());
				einvNICUserRepo.save(persistedEntity);
				updatedResult++;
			}
			LOGGER.debug("saved EINVuser {}", einvNICUser);
			if (updatedResult < 1) {
				String errMsg = String.format("Coudn't update the EINV User %s",
						TenantContext.getTenantId());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (AppException ee) {
			LOGGER.error("Exception while updating the EINVNIC User", ee);
		}
		return updatedResult;
	}

	@Override
	public List<Gstindto> getDistinctEINVGstin() {
		List<Gstindto> einvGstinList = null;
		try {
			einvGstinList = einvNICUserRepo.getDistinctGstin();
		} catch (AppException ee) {
			LOGGER.error("Exception while getting the EWB Gstin", ee);
			throw new AppException(ee);
		}
		return einvGstinList;

	}

	@Override
	public EINVNICUser getEinvUserByGstin(String gstin) {
		EINVNICUser eInvUser = null;
		try {
			eInvUser = einvNICUserRepo.findByGstin(gstin);
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the EINV USers", ee);
			throw new AppException(ee);

		}
		return eInvUser;
	}

}
