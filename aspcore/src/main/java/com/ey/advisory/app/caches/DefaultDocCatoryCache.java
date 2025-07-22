package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.DocCatogeryEntity;
import com.ey.advisory.admin.data.repositories.master.DocCatoryRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultDocCatoryCache")
@Slf4j
public class DefaultDocCatoryCache implements DocCategoryCache {


	@Autowired
	@Qualifier("DocCatoryRepository")
	private DocCatoryRepository catoryRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, DocCatogeryEntity> docCatogeryMap 
	                    = new HashMap<String, DocCatogeryEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		docCatogeryMap = loadAllStates();
		
	}
	
	private Map<String, DocCatogeryEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<DocCatogeryEntity> findAll=catoryRepository.FindAll();
			
			if(findAll!=null && !findAll.isEmpty()){
				for(DocCatogeryEntity countryobj:findAll){
					
					docCatogeryMap.put(countryobj.getExceptedInputs(), countryobj);
				}
			}
			return docCatogeryMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of catogery. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findDocCategory(String docCatogery) {
		
		return docCatogeryMap.containsKey(docCatogery) ? 1 : 0;
	}
	
	


}
