package com.ey.advisory.app.caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultStateCache")
public class DefaultStateCache implements StateCache {

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository StatecodeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, StateCodeInfoEntity> stateCodeMap = new HashMap<String, StateCodeInfoEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		stateCodeMap = loadAllStates();
	}

	private Map<String, StateCodeInfoEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<StateCodeInfoEntity> findAll = StatecodeRepository.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (StateCodeInfoEntity stateCode : findAll) {
					stateCodeMap.put(stateCode.getStateCode(), stateCode);
				}
			}
			return stateCodeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findStateCode(String stateCode) {

		return stateCodeMap.containsKey(stateCode) ? 1 : 0;
	}

	public String getStateName(String stateCode) {

		StateCodeInfoEntity entity = stateCodeMap.get(stateCode);
		return entity.getStateName();
	}

	public List<StateCodeInfoEntity> getStateCodeList() {
		return new ArrayList<>(stateCodeMap.values());
	}
}
