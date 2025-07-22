package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.DocTypeMasterEntity;
import com.ey.advisory.admin.data.repositories.master.DocTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultDocTypeCache")
@Slf4j
public class DefaultDocTypeCache implements DocTypeCache {


	@Autowired
	@Qualifier("DocTypeRepositoryMaster")
	private DocTypeRepository docTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, DocTypeMasterEntity> docTypeMap 
	                    = new HashMap<String, DocTypeMasterEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		docTypeMap = loadAllStates();
	}
	
	private Map<String, DocTypeMasterEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<DocTypeMasterEntity> findAll=docTypeRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(DocTypeMasterEntity docTypeobj:findAll){
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
