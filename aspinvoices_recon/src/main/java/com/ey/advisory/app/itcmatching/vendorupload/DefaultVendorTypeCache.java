package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.VendorTypeMasterEntity;
import com.ey.advisory.admin.data.repositories.master.VendorTypeMasterRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultVendorTypeCache")
public class DefaultVendorTypeCache implements VendorTypeCache {

	@Autowired
	@Qualifier("VendorTypeMasterRepository")
	private VendorTypeMasterRepository vendorTypeMasterRepo;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read will happen from this Map.
	 */
	private Map<String, String> vendorTypeMap = new HashMap<String, String>();
	private Map<String, String> hsnMap = new HashMap<String, String>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		vendorTypeMap = loadAllVendorTypes();
		hsnMap = loadAllHsns();
	}

	private Map<String, String> loadAllVendorTypes() {

		try {
			// From the repository load all states and add to the map.
			List<VendorTypeMasterEntity> findAll = vendorTypeMasterRepo
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (VendorTypeMasterEntity vendor : findAll) {
					vendorTypeMap.put(vendor.getCode(), vendor.getVendorType());

				}
			}
			return vendorTypeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of VendorTypes Master. Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private Map<String, String> loadAllHsns() {

		try {
			// From the repository load all states and add to the map.
			List<VendorTypeMasterEntity> findAll = vendorTypeMasterRepo
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (VendorTypeMasterEntity vendor : findAll) {
					hsnMap.put(vendor.getCode() + vendor.getHsn(),
							vendor.getVendorType());

				}
			}
			return hsnMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of VendorTypes Master. Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public boolean isVendorType(String vendorType) {
		return vendorTypeMap.containsKey(vendorType);
	}

	@Override
	public String findVendorType(String vendorType) {
		return vendorTypeMap.get(vendorType);
	}

	@Override
	public boolean isHSN(String hsn) {
		return hsnMap.containsKey(hsn);
	}
}
