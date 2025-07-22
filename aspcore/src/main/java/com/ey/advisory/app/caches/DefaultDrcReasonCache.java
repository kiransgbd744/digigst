package com.ey.advisory.app.caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.DRCReasonMasterEntity;
import com.ey.advisory.admin.data.repositories.master.DRCReasonMasterRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("DefaultDrcReasonCache")
public class DefaultDrcReasonCache implements DRCReasonCache {

	@Autowired
	@Qualifier("DRCReasonMasterRepository")
	private DRCReasonMasterRepository drcReasonMasterRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, DRCReasonMasterEntity> reasonCodeMap = new HashMap<String, DRCReasonMasterEntity>();

	@PostConstruct
	private void init() {
		reasonCodeMap = loadAllReasons();
	}

	private Map<String, DRCReasonMasterEntity> loadAllReasons() {

		try {
			// From the repository load all states and add to the map.
			List<DRCReasonMasterEntity> findAll = drcReasonMasterRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (DRCReasonMasterEntity reasonEntity : findAll) {
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

	public List<DRCReasonMasterEntity> getReasonsList() {
		return new ArrayList<>(reasonCodeMap.values());
	}

	@Override
	public String getReasonDescription(String reasonCode) {
		DRCReasonMasterEntity entity = reasonCodeMap.get(reasonCode);
		return entity.getReasonDesc();
	}

}
