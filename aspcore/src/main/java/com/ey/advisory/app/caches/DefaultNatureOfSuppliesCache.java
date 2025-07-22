package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfSupEntity;
import com.ey.advisory.admin.data.repositories.master.NatureOfSuppliesRepo;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("DefaultNatureOfSuppliesCache")
@Slf4j
public class DefaultNatureOfSuppliesCache implements NatureOfSupCache {

	@Autowired
	@Qualifier("NatureOfSuppliesRepo")
	private NatureOfSuppliesRepo natureOfSuppliesRepo;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, NatureOfSupEntity> natureSupplies = new HashMap<>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		natureSupplies = loadAllNatureDocTypes();
	}

	private Map<String, NatureOfSupEntity> loadAllNatureDocTypes() {
		try {
			// From the repository load all states and add to the map.
			List<NatureOfSupEntity> findAll = natureOfSuppliesRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (NatureOfSupEntity supplies : findAll) {
					String subSections = supplies.getSubSection().toUpperCase();
					natureSupplies.put(subSections, supplies);
				}
			}
			return natureSupplies;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public NatureOfSupEntity findNatureOfSupp(String section) {
		return natureSupplies.get(section);
	}

}
