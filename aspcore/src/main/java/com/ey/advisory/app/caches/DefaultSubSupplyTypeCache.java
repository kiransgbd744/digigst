package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.SubSupplyTypeEntity;
import com.ey.advisory.admin.data.repositories.master.SubSupplyTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultSubSupplyTypeCache")
@Slf4j
public class DefaultSubSupplyTypeCache implements SubSupplyTypeCache {


	@Autowired
	@Qualifier("SubSupplyTypeRepository")
	private SubSupplyTypeRepository subSupplyTypeRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, SubSupplyTypeEntity> subSupplyTypeMap 
	                    = new HashMap<String, SubSupplyTypeEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		subSupplyTypeMap = loadAllStates();
	}
	
	private Map<String, SubSupplyTypeEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<SubSupplyTypeEntity> findAll=subSupplyTypeRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(SubSupplyTypeEntity docTypeobj:findAll){
					subSupplyTypeMap.put(docTypeobj.getExceptedInput(), docTypeobj);
				}
			}
			return subSupplyTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of SubSupplyTYpes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findSubSupplyType(String docType) {
		
		return subSupplyTypeMap.containsKey(docType) ? 1 : 0;
	}
	
	


}
