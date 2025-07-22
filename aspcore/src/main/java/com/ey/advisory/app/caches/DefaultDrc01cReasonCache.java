package com.ey.advisory.app.caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.DRC01CReasonMasterEntity;
import com.ey.advisory.admin.data.repositories.master.DRC01CReasonMasterRepo;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("DefaultDrc01cReasonCache")
public class DefaultDrc01cReasonCache implements DRCReasonCache {

	@Autowired
	@Qualifier("DRC01CReasonMasterRepo")
	private DRC01CReasonMasterRepo drcReasonMasterRepo;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read will happen from this Map.
	 */
	private Map<String, DRC01CReasonMasterEntity> reasonCodeMap = new HashMap<String, DRC01CReasonMasterEntity>();

	@PostConstruct
	private void init() {
		reasonCodeMap = loadAllReasons();
	}

	private Map<String, DRC01CReasonMasterEntity> loadAllReasons() {

		try {
			// From the repository load all states and add to the map.
			List<DRC01CReasonMasterEntity> findAll = drcReasonMasterRepo
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (DRC01CReasonMasterEntity reasonEntity : findAll) {
					reasonCodeMap.put(reasonEntity.getReasonCode(),
							reasonEntity);
				}
			}
			return reasonCodeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of Reason. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public List<DRC01CReasonMasterEntity> getReasonsList() {
		return new ArrayList<>(reasonCodeMap.values());
	}

	@Override
	public String getReasonDescription(String reasonCode) {
		DRC01CReasonMasterEntity entity = reasonCodeMap.get(reasonCode);
		return entity.getReasonDesc();
	}

}
