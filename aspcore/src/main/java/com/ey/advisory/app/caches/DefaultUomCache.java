package com.ey.advisory.app.caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.UomMasterEntity;
import com.ey.advisory.admin.data.repositories.master.UomRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultUomCache")
public class DefaultUomCache implements UomCache {

	@Autowired
	@Qualifier("UomRepositoryMaster")
	private UomRepository uomRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, UomMasterEntity> uomMap = new HashMap<String, UomMasterEntity>();
	private Map<String, UomMasterEntity> uomDescMap = new HashMap<String, UomMasterEntity>();
	private Map<String, UomMasterEntity> uomMergeDescMap = new HashMap<String, UomMasterEntity>();
	private Map<String, String> uomCodeDescMap = new HashMap<String, String>();
	private Map<String, String> uomDescCodeMergeMap = new HashMap<String, String>();


	@PostConstruct
	private void init() {
		// Access the uomRepository and load all
		List<UomMasterEntity> loadAllUom = loadAllUom();
		if (loadAllUom != null && !loadAllUom.isEmpty()) {
			loadAllUom.forEach(uom -> {
				// add to all three maps with the rate as the key and the
				// entity as the value.

				uomMap.put(uom.getUqc(), uom);
				uomDescMap.put(uom.getUqcDesc().toUpperCase(), uom);
				uomMergeDescMap.put(
						uom.getUqc() + "-" + uom.getUqcDesc().toUpperCase(),
						uom);
				uomCodeDescMap.put(uom.getUqcDesc(), uom.getUqc());
				uomDescCodeMergeMap.put(
						uom.getUqc() + "-" + uom.getUqcDesc().toUpperCase(),
						uom.getUqc());

			});
		}
	}

	private List<UomMasterEntity> loadAllUom() {

		try {
			// From the repository load all uom and add to the map.
			List<UomMasterEntity> findAll = uomRepository.findAll();

			return findAll;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of uom. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int finduom(String uom) {

		return uomMap.containsKey(uom) ? 1 : 0;
	}

	public int finduomDesc(String Desc) {

		return uomDescMap.containsKey(Desc) ? 1 : 0;
	}

	public int finduomMergeDesc(String Desc) {

		return uomMergeDescMap.containsKey(Desc) ? 1 : 0;
	}

	public Map<String, String> uQcDescAndCodemap() {
		return uomCodeDescMap;

	}

	public Map<String, String> uQcDesc() {
		return uomDescCodeMergeMap;

	}

	public List<UomMasterEntity> getUomList() {
		return new ArrayList<>(uomMap.values());
	}
}
