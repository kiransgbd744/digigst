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

import com.ey.advisory.admin.data.entities.master.Itc04JwTypeEntity;
import com.ey.advisory.admin.data.repositories.master.Itc04JwTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("DefaultItc04JwTypeCache")
public class DefaultItc04JwTypeCache implements Itc04JwTypeCache {

	@Autowired
	@Qualifier("Itc04JwTypeRepository")
	private Itc04JwTypeRepository itc04JwTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, Itc04JwTypeEntity> jwTypeMap = new HashMap<String, Itc04JwTypeEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		jwTypeMap = loadAllStates();
	}

	private Map<String, Itc04JwTypeEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<Itc04JwTypeEntity> findAll = itc04JwTypeRepository.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (Itc04JwTypeEntity docTypeobj : findAll) {
					jwTypeMap.put(docTypeobj.getJwType(), docTypeobj);

				}
			}
			return jwTypeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of jwType. " + "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findJwType(String jwType) {

		return jwTypeMap.containsKey(jwType) ? 1 : 0;
	}

}
