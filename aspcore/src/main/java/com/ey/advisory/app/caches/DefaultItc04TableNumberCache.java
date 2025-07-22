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

import com.ey.advisory.admin.data.entities.master.Itc04TableNumberEntity;
import com.ey.advisory.admin.data.repositories.master.Itc04TableNumberRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("DefaultItc04TableNumberCache")
public class DefaultItc04TableNumberCache implements Itc04TableNumberCache {

	@Autowired
	@Qualifier("Itc04TableNumberRepository")
	private Itc04TableNumberRepository itc04tableNumRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, Itc04TableNumberEntity> tableNumberMap = 
			new HashMap<String, Itc04TableNumberEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		tableNumberMap = loadAllStates();
	}

	private Map<String, Itc04TableNumberEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<Itc04TableNumberEntity> findAll = itc04tableNumRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (Itc04TableNumberEntity docTypeobj : findAll) {
					tableNumberMap.put(docTypeobj.getTableNumber(), docTypeobj);

				}
			}
			return tableNumberMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of tableNumbers. " + "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findTableNumber(String tableNumber) {

		return tableNumberMap.containsKey(tableNumber) ? 1 : 0;
	}

}
