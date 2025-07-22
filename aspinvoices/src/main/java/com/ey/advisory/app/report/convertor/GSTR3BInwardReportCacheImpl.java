/**
 * 
 */
package com.ey.advisory.app.report.convertor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;
import com.ey.advisory.core.async.repositories.master.MasterErrorCatalogEntityRepository;

import jakarta.annotation.PostConstruct;

/**
 * @author vishal.verma
 *
 */
@Component("GSTR3BInwardReportCacheImpl")
public class GSTR3BInwardReportCacheImpl implements GSTR3BInwardReportCache{
	
	private Map<String, String> hsnMap = new ConcurrentHashMap<>();

	@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	MasterErrorCatalogEntityRepository masterErrorCatalogRepo;

	@PostConstruct
	public void init() {

		List<MasterErrorCatalogEntity> entityList = masterErrorCatalogRepo.findAll();
		hsnMap = entityList.stream()
				.collect(Collectors.toMap(o -> o.getErrorCode(), o -> o.getErrorDesc(), (o1, o2) -> o2, TreeMap::new));

	}
	
	@Override
	public String findAspInfoDescription(String errorCode) {

		return hsnMap.get(errorCode);
	}
}
