package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.PortCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.master.PortCodeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultPortCache")
@Slf4j
public class DefaultPortCache implements PortCache{
	
	@Autowired
	@Qualifier("PortCodeRepositoryMaster")
	private PortCodeRepository PortCodeRepository;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, PortCodeInfoEntity> portMap 
	                      =new HashMap<String, PortCodeInfoEntity>();
	

	@PostConstruct
	private void init() {
		List<PortCodeInfoEntity> portcode = loadAllRates();
		if(portcode!=null && !portcode.isEmpty()){
		portcode.forEach(port -> {
			// add to all three maps with the rate as the key and the
			// entity as the value.
			portMap.put(port.getPortCode(), port);
		});
	}
}
	private List<PortCodeInfoEntity> loadAllRates() {
		try {
			// From the repository load all rates.
			// Use the find all method to get a list of Rate entities.
			
			//List<RateMasterEntity> rates = new ArrayList<>();
			
			
			
			List<PortCodeInfoEntity> portcode=PortCodeRepository.findAll();
			
			return portcode;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of ports. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findPortCode(String port) {
		
		return portMap.containsKey(port) ? 1 : 0;
	}
	

	

	
}
