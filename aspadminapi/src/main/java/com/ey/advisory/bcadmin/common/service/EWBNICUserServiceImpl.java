package com.ey.advisory.bcadmin.common.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstindto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("BcAdminEWBNICUserServiceImpl")
public class EWBNICUserServiceImpl implements EWBNICUserService {

	@Autowired
	@Qualifier("EWBNICUserRepository")
	private EWBNICUserRepository ewbNICUserRepo;

	@Override
	public List<EWBNICUser> getAllEWBNICUsers() {
		List<EWBNICUser> ewdList = null;
		try {
			ewdList = ewbNICUserRepo.findAll();
			if (ewdList == null || ewdList.isEmpty()) {
				String errMsg = String.format(
						"There are no EWB Users onboarded for a group %s",
						TenantContext.getTenantId());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the EWB Users", ee);
		}
		return ewdList;
	}

	@Override
	public int updateEWBNICUser(EWBNICUser ewbNICUser) {
		int updatedRows = 0;
		try {
			EWBNICUser persistedEntity = ewbNICUserRepo
					.findByGstin(ewbNICUser.getGstin());
			if (persistedEntity == null) {
				ewbNICUser.setUpdatedOn(LocalDateTime.now());
				ewbNICUserRepo.save(ewbNICUser);
				updatedRows++;
			} else {
				persistedEntity.setNicUserName(ewbNICUser.getNicUserName());
				persistedEntity.setNicPassword(ewbNICUser.getNicPassword());
				persistedEntity.setUpdatedBy(ewbNICUser.getUpdatedBy());
				persistedEntity.setUpdatedOn(LocalDateTime.now());
				ewbNICUserRepo.save(persistedEntity);
				updatedRows++;
			}
			LOGGER.info("saved EWB user ", updatedRows);
			if (updatedRows < 1) {
				String errMsg = String.format("Coudn't update the EWB User %s",
						TenantContext.getTenantId());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (AppException ee) {
			LOGGER.error("Exception while updating the EWBNIC User", ee);
		}
		return updatedRows;
	}

	@Override
	public List<Gstindto> getDistinctEWBGstin() {
		List<Gstindto> ewbGstinList = null;
		try {
			ewbGstinList = ewbNICUserRepo.getGstins();
		} catch (AppException ee) {
			LOGGER.error("Exception while getting the EWB Gstin", ee);
		}
		return ewbGstinList;
	}

}

