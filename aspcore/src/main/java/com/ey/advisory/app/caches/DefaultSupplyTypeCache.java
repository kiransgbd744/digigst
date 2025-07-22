package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.SupplyTypeMasterEntity;
import com.ey.advisory.admin.data.repositories.master.SupplyTypeMasterRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultSupplyTypeCache")
public class DefaultSupplyTypeCache implements SupplyTypeCache{


	@Autowired
	@Qualifier("SupplyTypeRepository")
	private SupplyTypeMasterRepository supplyTypeMasterRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, SupplyTypeMasterEntity> supplyTypeMap 
	                    = new HashMap<String, SupplyTypeMasterEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the SupplyTypeRepository and load all 
		supplyTypeMap = loadAllStates();
	}
	
	private Map<String, SupplyTypeMasterEntity> loadAllStates() {
		
		try {
			// From the repository load all supplyTypes and add to the map.
			List<SupplyTypeMasterEntity> findAll=supplyTypeMasterRepository.findAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(SupplyTypeMasterEntity supplyTypeobj:findAll){
					supplyTypeMap.put(supplyTypeobj.getSupplyType(), supplyTypeobj);
				}
			}
			return supplyTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of SupplyTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findSupplyType(String supplyType) {
		if(supplyTypeMap == null || supplyTypeMap.isEmpty()) {
			init();
		}
		return supplyTypeMap.containsKey(supplyType) ? 1 : 0;
	}
	


}
