package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.UserActionMasterEntity;
import com.ey.advisory.admin.data.repositories.master.UserActionRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Slf4j
@Component("DefaultTcsTdsActionSavedCache")
public class DefaultTcsTdsActionSavedCache implements TcsTdsActionSavedCache {

	@Autowired
	@Qualifier("UserActionRepository")
	private UserActionRepository userActionRepository; 

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, UserActionMasterEntity> category = new HashMap<>(); 

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		category = loadAllNatureDocTypes(); 
	}

	private Map<String, UserActionMasterEntity> loadAllNatureDocTypes() {
		try {
			// From the repository load all states and add to the map.
			List<UserActionMasterEntity> findAll = userActionRepository.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (UserActionMasterEntity stateCode : findAll) {
					String natureOfDoc = stateCode.getCategory()
							.replaceAll("\\s", "").toUpperCase();
					category.put(natureOfDoc, stateCode);
				}
			}
			return category;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of user actions. "
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