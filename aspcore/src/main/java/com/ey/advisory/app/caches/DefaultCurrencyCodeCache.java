package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.CurrencyCodeEntity;
import com.ey.advisory.admin.data.repositories.master.CurrencyCodeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultCurrencyCodeCache")
@Slf4j
public class DefaultCurrencyCodeCache implements CurrencyCodeCache {


	@Autowired
	@Qualifier("CurrencyCodeRepository")
	private CurrencyCodeRepository currencyRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, CurrencyCodeEntity> subSupplyTypeMap 
	                    = new HashMap<String, CurrencyCodeEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the DocTypeRepository and load all 
		subSupplyTypeMap = loadAllStates();
	}
	
	private Map<String, CurrencyCodeEntity> loadAllStates() {
		
		try {
			// From the repository load all DocTypes and add to the map.
			List<CurrencyCodeEntity> findAll=currencyRepository.FindAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(CurrencyCodeEntity countryobj:findAll){
					
					subSupplyTypeMap.put(countryobj.getCode(), countryobj);
				}
			}
			return subSupplyTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of currency code. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int findCurrencyCode(String country) {
		
		return subSupplyTypeMap.containsKey(country) ? 1 : 0;
	}
	
	


}
