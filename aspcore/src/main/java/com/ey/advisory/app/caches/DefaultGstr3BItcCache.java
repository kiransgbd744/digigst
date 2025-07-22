package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.Gstr3BItcEntityMaster;
import com.ey.advisory.admin.data.repositories.master.Gstr3bItcMasterRepo;
import com.ey.advisory.app.caches.ehcache.EhcacheGstr3bItc;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("DefaultGstr3BItcCache")
@Slf4j
public class DefaultGstr3BItcCache implements EhcacheGstr3bItc {

	@Autowired
	@Qualifier("Gstr3bItcMasterRepo")
	private Gstr3bItcMasterRepo gstr3bItcMasterRepo;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, Gstr3BItcEntityMaster> gstr3bItcCode = new HashMap<>();
	private Map<Integer, Gstr3BItcEntityMaster> 
	                                     gstr3bItcDesCode = new HashMap<>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all 
		gstr3bItcCode = loadAllGstr3bItcs();
		gstr3bItcDesCode = loadDesForGstr3bs();
	} 

	private Map<Integer, Gstr3BItcEntityMaster> loadDesForGstr3bs() {
		try {
			// From the repository load all states and add to the map.
			List<Gstr3BItcEntityMaster> findAll = gstr3bItcMasterRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) { 
				for (Gstr3BItcEntityMaster serialNumber : findAll) {
					Integer serNo = serialNumber.getSerialNo();
					gstr3bItcDesCode.put(serNo,serialNumber);
				}
			}
			return gstr3bItcDesCode;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private Map<String, Gstr3BItcEntityMaster> loadAllGstr3bItcs() {
		try {
			// From the repository load all states and add to the map.
			List<Gstr3BItcEntityMaster> findAll = gstr3bItcMasterRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) { 
				for (Gstr3BItcEntityMaster description : findAll) {
					String des = description.getDescription().replaceAll("\\s", ""); 
					gstr3bItcCode.put(des,description);
				}
			}
			return gstr3bItcCode;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findDesCription(String description) {
		return gstr3bItcCode.containsKey(description) ? 1 : 0;
	}

	@Override
	public Gstr3BItcEntityMaster findGstr3Bitc(Integer serialNumber) {
		return gstr3bItcDesCode.get(serialNumber);
	}
}
