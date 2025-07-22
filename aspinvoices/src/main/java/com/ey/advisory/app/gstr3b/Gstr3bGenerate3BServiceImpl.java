package com.ey.advisory.app.gstr3b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3bGenerateStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3bDigiStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("Gstr3bGenerate3BServiceImpl")
public class Gstr3bGenerate3BServiceImpl implements Gstr3bGenerate3BService {

	@Autowired
	@Qualifier("Gstr3bGenerate3BDaoImpl")
	private Gstr3bGenerate3BDao dao;

	@Autowired
	@Qualifier("Gstr3bDigiStatusRepository")
	private Gstr3bDigiStatusRepository gstr3bDigiStatusRepository;

	@Override
	public String getGstr3bGenerateList(String taxPeriod, String gstin, Long entityId)
			throws AppException {
		List<String> statusList = new ArrayList<String>();
		statusList.add("Initiated");
		statusList.add("InProgress");
		gstr3bDigiStatusRepository.softDeleteActiveRecord(gstin, taxPeriod);
		Gstr3bGenerateStatusEntity gstr3bEntity = gstr3bDigiStatusRepository
				.findByGstinAndTaxPeriodAndIsActiveAndStatusIn(gstin, taxPeriod, true,statusList);
		
		Long id;
		if(gstr3bEntity != null){
			id = gstr3bEntity.getId();
			gstr3bDigiStatusRepository.updateRecordById(id, "InProgress",
					LocalDateTime.now());
			
		} else{
			LOGGER.debug(gstin);
			LOGGER.debug(taxPeriod);
//		gstr3bDigiStatusRepository.softDeleteRecord(gstin, taxPeriod);
		String userName = SecurityContext.getUser()
				.getUserPrincipalName();
			Gstr3bGenerateStatusEntity entity = new Gstr3bGenerateStatusEntity();
			entity.setCreatedBy(userName);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setIsActive(true);
			entity.setGstin(gstin);
			entity.setTaxPeriod(taxPeriod);
			entity.setModifiedBy(userName);
			entity.setModifiedOn(LocalDateTime.now());
			entity.setCalculationType("DigiGst");
			entity.setStatus("Initiated");
			
			entity = gstr3bDigiStatusRepository.save(entity);
			id = entity.getId();
		}
		String status = dao.getGstnDtoList(gstin, taxPeriod,id, entityId);

		return status;
	}

}
