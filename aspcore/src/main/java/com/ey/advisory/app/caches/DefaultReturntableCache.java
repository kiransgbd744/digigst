package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.ReturnTableEntity;
import com.ey.advisory.admin.data.repositories.master.ReturnTableRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("DefaultReturntableCache")
@Slf4j
public class DefaultReturntableCache implements ReturnTableCache {
	@Autowired
	@Qualifier("ReturnTableRepository")
	private ReturnTableRepository rtatecodeRepository;
	
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, ReturnTableEntity> returnTableCode 
	                    = new HashMap<String, ReturnTableEntity>();
	
	@PostConstruct
	private void init() {	 
		// Access the StateRepository and load all 
		returnTableCode = loadAllReturnTables(); 
	}
	
	private Map<String, ReturnTableEntity> loadAllReturnTables() { 
		
		try {
			// From the repository load all states and add to the map.
			List<ReturnTableEntity> findAll=rtatecodeRepository.findAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(ReturnTableEntity returnTables:findAll){
					returnTableCode.put(returnTables.getReturnTable(), returnTables);
				}
			}
			return returnTableCode;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	@Override
	public int findReturnTable(String returnTable) {
		return returnTableCode.containsKey(returnTable) ? 1 : 0;
	}

}
