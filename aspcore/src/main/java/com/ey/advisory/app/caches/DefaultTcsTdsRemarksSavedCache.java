package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.TcsTdsRemarksMasterEntity;
import com.ey.advisory.admin.data.repositories.master.TcsTdsRemarksMasterRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("DefaultTcsTdsRemarksSavedCache")
public class DefaultTcsTdsRemarksSavedCache implements TcsTdsRemarksSavedCache {

	@Autowired
	@Qualifier("TcsTdsRemarksMasterRepository")
	private TcsTdsRemarksMasterRepository tcsTdsRemarksMasterRepo; 

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, TcsTdsRemarksMasterEntity> category = new HashMap<>(); 

	@PostConstruct
	private void init() {
		// Access and load all
		category = loadAllNatureDocTypes(); 
	}

	private Map<String, TcsTdsRemarksMasterEntity> loadAllNatureDocTypes() {
		try {
			// From the repository load all states and add to the map.
			List<TcsTdsRemarksMasterEntity> findAll = tcsTdsRemarksMasterRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (TcsTdsRemarksMasterEntity stateCode : findAll) {
					String natureOfDoc = stateCode.getExpectedValue()
							.replaceAll("\\s", "").toUpperCase();
					category.put(natureOfDoc, stateCode);
				}
			}
			return category;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of remarks. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findActionSavedAt(String actionSavedAtuser) { 
		return category.containsKey(actionSavedAtuser.toUpperCase()) ? 1 : 0;
	}

}