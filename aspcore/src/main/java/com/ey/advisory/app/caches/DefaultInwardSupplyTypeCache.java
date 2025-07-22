package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.InwardSupplyTypeMasterEntity;
import com.ey.advisory.admin.data.repositories.master.InwardSupplyTypeMasterRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultInwardSupplyTypeCache")
public class DefaultInwardSupplyTypeCache implements SupplyTypeCache {

	@Autowired
	@Qualifier("InwardSupplyTypeMasterRepository")
	private InwardSupplyTypeMasterRepository supplyTypeMasterRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, InwardSupplyTypeMasterEntity> supplyTypeMap 
	= new HashMap<String, InwardSupplyTypeMasterEntity>();

	@PostConstruct
	private void init() {
		// Access the SupplyTypeRepository and load all
		supplyTypeMap = loadAllStates();
	}

	private Map<String, InwardSupplyTypeMasterEntity> loadAllStates() {

		try {
			// From the repository load all supplyTypes and add to the map.
			List<InwardSupplyTypeMasterEntity> findAll = supplyTypeMasterRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (InwardSupplyTypeMasterEntity supplyTypeobj : findAll) {
					supplyTypeMap.put(supplyTypeobj.getSupplyType(),
							supplyTypeobj);
				}
			}
			return supplyTypeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of SupplyTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findSupplyType(String supplyType) {
		if (supplyTypeMap == null || supplyTypeMap.isEmpty()) {
			init();
		}
		return supplyTypeMap.containsKey(supplyType) ? 1 : 0;
	}

}
