package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.RecipientTypeEntity;
import com.ey.advisory.admin.data.repositories.master.RecipientTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultRecipientTypeCache")
public class DefaultRecipientTypeCache implements RecipientTypeCache {


	@Autowired
	@Qualifier("RecipientTypeRepositoryMaster")
	private RecipientTypeRepository recipientTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, RecipientTypeEntity> RecTypeMap 
	                    = new HashMap<String, RecipientTypeEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		RecTypeMap = loadAll();
	}
	
	private Map<String, RecipientTypeEntity> loadAll() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<RecipientTypeEntity> findAll=recipientTypeRepository.findAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(RecipientTypeEntity RecTypeobj:findAll){
					RecTypeMap.put(RecTypeobj.getRecipientType(), RecTypeobj);
				}
			}
			return RecTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of RecipientTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findRecType(String RecType) {
		
		return RecTypeMap.containsKey(RecType) ? 1 : 0;
	}
	


}
