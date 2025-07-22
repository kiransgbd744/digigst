package com.ey.advisory.app.caches;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultHsnCache")
public class DefaultHsnCache implements HsnCache {

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnRep;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, String> hsnMap = new ConcurrentHashMap<>();

	private Set<String> hsnTwoCharSet = new HashSet<>();
	private Set<String> hsnFourCharSet = new HashSet<>();
	private Set<String> hsnSixCharSet = new HashSet<>();

	@PostConstruct
	private void init() {
		List<HsnOrSacMasterEntity> totalHsnList = loadAll();
		for (HsnOrSacMasterEntity obj : totalHsnList) {
			String hsnsacStr = obj.getHsnSac();
			hsnMap.put(hsnsacStr, obj.getDescription());
			if (hsnsacStr.length() > 1)
				hsnTwoCharSet.add(hsnsacStr.substring(0, 2));
			if (hsnsacStr.length() > 3)
				hsnFourCharSet.add(hsnsacStr.substring(0, 4));
			if (hsnsacStr.length() > 5)
				hsnSixCharSet.add(hsnsacStr.substring(0, 6));

		}

	}

	private List<HsnOrSacMasterEntity> loadAll() {
		try {
			// From the repository load all rates.
			// Use the find all method to get a list of hsn entities.

			return hsnRep.findAll();

		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of ports. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findhsn(String hsn) {
		int length = hsn.length();
		if (length == 2)
			return hsnTwoCharSet.contains(hsn) ? 1 : 0;
		else if (length == 4)
			return hsnFourCharSet.contains(hsn) ? 1 : 0;
		else if (length == 6)
			return hsnSixCharSet.contains(hsn) ? 1 : 0;
		else
			return hsnMap.containsKey(hsn) ? 1 : 0;
	}

	@Override
	public boolean isValidHSN(String hsnCode) {
		if (hsnMap.isEmpty())
			return false;
		return hsnMap.containsKey(hsnCode);
	}

	@Override
	public String findHsnDescription(String hsnCode) {

		return hsnMap.get(hsnCode);
	}
}