package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("DefaultNatureOfDocCache")
@Slf4j
public class DefaultNatureOfDocCache implements NatureOfDocCache {

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, NatureOfDocEntity> natureDocTypeCode = new HashMap<>();

	private Map<Integer, NatureOfDocEntity> natureOfDoc = new HashMap<>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		natureDocTypeCode = loadAllNatureDocTypes();
		natureOfDoc = loadAllNatureOfDocs();
	}

	private Map<Integer, NatureOfDocEntity> loadAllNatureOfDocs() {
		try {
			// From the repository load all states and add to the map.
			List<NatureOfDocEntity> findAll = natureDocTypeRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (NatureOfDocEntity stateCode : findAll) {
					Integer serialNumber = stateCode.getId().intValue();
					natureOfDoc.put(serialNumber, stateCode);
				}
			}
			return natureOfDoc;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private Map<String, NatureOfDocEntity> loadAllNatureDocTypes() {
		try {
			// From the repository load all states and add to the map.
			List<NatureOfDocEntity> findAll = natureDocTypeRepo.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (NatureOfDocEntity stateCode : findAll) {
					String natureOfDoc = stateCode.getNatureDocType()
							.replaceAll("\\s", "").toUpperCase();
					natureDocTypeCode.put(natureOfDoc, stateCode);
				}
			}
			return natureDocTypeCode;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findDocType(String docType) {
		return natureDocTypeCode.containsKey(docType.toUpperCase()) ? 1 : 0;
	}

	@Override
	public NatureOfDocEntity findNatureOfDoc(Integer serialNumber) {
		return natureOfDoc.get(serialNumber);
	}

}
