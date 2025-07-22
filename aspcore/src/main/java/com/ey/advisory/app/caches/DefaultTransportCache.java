package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.TransportEntity;
import com.ey.advisory.admin.data.repositories.master.TransPortRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultTransportCache")
@Slf4j
public class DefaultTransportCache implements TransportCache {


	@Autowired
	@Qualifier("TransPortRepository")
	private TransPortRepository transportRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, TransportEntity> transportMap 
	                    = new HashMap<String, TransportEntity>();
	
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		transportMap = loadAllStates();
		
	}
	
	private Map<String, TransportEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<TransportEntity> findAll=transportRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(TransportEntity countryobj:findAll){
					
					transportMap.put(countryobj.getInput(), countryobj);
				}
			}
			return transportMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of Tranport. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findTransport(String transport) {
		
		return transportMap.containsKey(transport) ? 1 : 0;
	}
	
	


}
