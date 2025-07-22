/**
 * 
 */
package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.Itc04ReturnPeriodEntity;
import com.ey.advisory.admin.data.repositories.master.Itc04ReturnPeriodRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("DefaultItc04ReturnPeriodCache")
public class DefaultItc04ReturnPeriodCache implements Itc04ReturnPeriodCache {

	@Autowired
	@Qualifier("Itc04ReturnPeriodRepository")
	private Itc04ReturnPeriodRepository itc04ReturnPeriodRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, Itc04ReturnPeriodEntity> retPeriodMap = new HashMap<String, Itc04ReturnPeriodEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		retPeriodMap = loadAllStates();
	}

	private Map<String, Itc04ReturnPeriodEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<Itc04ReturnPeriodEntity> findAll = itc04ReturnPeriodRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (Itc04ReturnPeriodEntity docTypeobj : findAll) {
					retPeriodMap.put(docTypeobj.getReturnPeriod(), docTypeobj);

				}
			}
			return retPeriodMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of tableNumbers. " + "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findRetunPeriod(String retPeriod) {

		return retPeriodMap.containsKey(retPeriod) ? 1 : 0;
	}

}
