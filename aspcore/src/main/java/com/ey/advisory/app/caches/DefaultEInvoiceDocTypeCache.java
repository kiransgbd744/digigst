package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.EInvoiceDocTypeEntity;
import com.ey.advisory.admin.data.repositories.master.EInvoiceDocTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("DefaultEInvoiceDocTypeCache")
public class DefaultEInvoiceDocTypeCache implements EInvoiceDocTypeCache {

	@Autowired
	@Qualifier("EInvoiceDocTypeRepository")
	private EInvoiceDocTypeRepository invoicedocTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, EInvoiceDocTypeEntity> docTypeMap 
	                    = new HashMap<String, EInvoiceDocTypeEntity>();
	
	@PostConstruct
	private void init() {	
		// Access the StateRepository and load all 
		docTypeMap = loadAllStates();
	}
	
	private Map<String, EInvoiceDocTypeEntity> loadAllStates() {
		
		try {
			// From the repository load all states and add to the map.
			List<EInvoiceDocTypeEntity> findAll=invoicedocTypeRepository.findAll();
			if(findAll!=null && !findAll.isEmpty()){
				for(EInvoiceDocTypeEntity docTypeobj:findAll){
					docTypeMap.put(docTypeobj.getInput(), docTypeobj);
					
				}
			}
			return docTypeMap;
		} catch(Exception ex) {
			String msg = "Error occurred while loading the list of docTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public int finddocType(String docType) {
		
		return docTypeMap.containsKey(docType) ? 1 : 0;
	}
	
}
