package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.InwardDocTypeMasterEntity;
import com.ey.advisory.admin.data.repositories.master.InwardDocTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultInwardDocTypeCache")
@Slf4j
public class DefaultInwardDocTypeCache implements DocTypeCache {


	@Autowired
	@Qualifier("InwardDocTypeRepository")
	private InwardDocTypeRepository docTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, InwardDocTypeMasterEntity> docTypeMap 
	                    = new HashMap<String, InwardDocTypeMasterEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		docTypeMap = loadAllStates();
	}
	
	private Map<String, InwardDocTypeMasterEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<InwardDocTypeMasterEntity> findAll=docTypeRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(InwardDocTypeMasterEntity docTypeobj:findAll){
					docTypeMap.put(docTypeobj.getDocType(), docTypeobj);
				}
			}
			return docTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of DocTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findDocType(String docType) {
		
		return docTypeMap.containsKey(docType) ? 1 : 0;
	}
	


}
