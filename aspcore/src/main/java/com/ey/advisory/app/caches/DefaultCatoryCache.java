package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.CatogeryEntity;
import com.ey.advisory.admin.data.repositories.master.CatoryRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultCatoryCache")
@Slf4j
public class DefaultCatoryCache implements CategoryCache {


	@Autowired
	@Qualifier("CatoryRepository")
	private CatoryRepository catogeryRepository;
	
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, CatogeryEntity> catogeryMap 
	                    = new HashMap<String, CatogeryEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		catogeryMap = loadAllStates();
	}
	
	private Map<String, CatogeryEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<CatogeryEntity> findAll=catogeryRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(CatogeryEntity countryobj:findAll){
					
					catogeryMap.put(countryobj.getExceptedInputs(), countryobj);
				}
			}
			return catogeryMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of category. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findCategory(String catogery) {
		
		return catogeryMap.containsKey(catogery) ? 1 : 0;
	}
	
	


}
