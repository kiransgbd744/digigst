package com.ey.advisory.app.caches;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.RateMasterEntity;
import com.ey.advisory.admin.data.repositories.master.RateRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultRateCache")
@Slf4j
public class DefaultRateCache implements RateCache {
	
	@Autowired
	@Qualifier("RateRepositoryMaster")
	private RateRepository rateRepository;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<BigDecimal, RateMasterEntity> cgstMap 
	                          = new HashMap<BigDecimal, RateMasterEntity>();
	private Map<BigDecimal, RateMasterEntity> igstMap 
	                           = new HashMap<BigDecimal,RateMasterEntity>();
	private Map<BigDecimal, RateMasterEntity> sgstMap 
	                           = new HashMap<BigDecimal,RateMasterEntity>();

	@PostConstruct
	private void init() {
		List<RateMasterEntity> rates = loadAllRates();
		if(rates!=null && !rates.isEmpty()){
		rates.forEach(rate -> {
			// add to all three maps with the rate as the key and the
			// entity as the value.
			
					cgstMap.put(rate.getCgst(), rate);
					sgstMap.put(rate.getSgst(), rate);
					igstMap.put(rate.getIgst(), rate);
			
		});
		}
	}

	private List<RateMasterEntity> loadAllRates() {
		try {
			// From the repository load all rates.
			// Use the find all method to get a list of Rate entities.
			
			//List<RateMasterEntity> rates = new ArrayList<>();

			
			List<RateMasterEntity> rates=rateRepository.findAll();
			
			return rates;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of rates. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findByIgst(BigDecimal igst) {
		
		return igstMap.containsKey(igst.setScale(3,BigDecimal.ROUND_HALF_UP)) ? 1 : 0;
	}

	public int findByCgst(BigDecimal cgst) {
		
		return cgstMap.containsKey(cgst.setScale(3,BigDecimal.ROUND_HALF_UP)) ? 1 : 0;
	}

	public int findBySgst(BigDecimal sgst) {
		
		return sgstMap.containsKey(sgst.setScale(3,BigDecimal.ROUND_HALF_UP)) ? 1 : 0;

	}
}
