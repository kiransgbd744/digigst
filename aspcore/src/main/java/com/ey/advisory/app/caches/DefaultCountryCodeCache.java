package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.CountryEntity;
import com.ey.advisory.admin.data.repositories.master.countryCodeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultCountryCodeCache")
@Slf4j
public class DefaultCountryCodeCache implements CountryCodeCache {


	@Autowired
	@Qualifier("countryCodeRepository")
	private countryCodeRepository countryRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, CountryEntity> countryCodeMap 
	                    = new HashMap<String, CountryEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		countryCodeMap = loadAllStates();
		
	}
	
	private Map<String, CountryEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<CountryEntity> findAll=countryRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(CountryEntity countryobj:findAll){
					
					countryCodeMap.put(countryobj.getCode(), countryobj);
				}
			}
			return countryCodeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of country code. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findCountryCode(String country) {
		
		return countryCodeMap.containsKey(country) ? 1 : 0;
	}
	
	


}
