package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.EInvoiceSupplyTypeEntity;
import com.ey.advisory.admin.data.repositories.master.EInvoiceSupplyTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultEInvoiceSupplyTypeCache")
public class DefaultEInvoiceSupplyTypeCache implements EInvoiceSupplyTypeCache {

	@Autowired
	@Qualifier("EInvoiceSupplyTypeRepository")
	private EInvoiceSupplyTypeRepository invoiceSupplyTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, EInvoiceSupplyTypeEntity> supplyTypeMap 
	      = new HashMap<String, EInvoiceSupplyTypeEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		supplyTypeMap = loadAllStates();
	}

	private Map<String, EInvoiceSupplyTypeEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<EInvoiceSupplyTypeEntity> findAll = invoiceSupplyTypeRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (EInvoiceSupplyTypeEntity docTypeobj : findAll) {
					supplyTypeMap.put(docTypeobj.getInput(), docTypeobj);

				}
			}
			return supplyTypeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of supplyTypeTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public int findSupplyType(String supplyType) {

		return supplyTypeMap.containsKey(supplyType) ? 1 : 0;
	}

}
