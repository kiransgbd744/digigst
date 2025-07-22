package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2Config2bprDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2ReconResponseConfigListServiceImpl")
public class Gstr2ReconResponseConfigListServiceImpl implements Gstr2ReconResponseConfigListService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2Config2bprDto> getAllNonAPConfigId(String entityId,
			Integer fromTaxPeriodInt, Integer toTaxPeriodInt,
			String reconType) {
		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_RECON_SUMMRY_REQUEST");

		storedProc.registerStoredProcedureParameter("P_ENTITY_ID",
				Integer.class, ParameterMode.IN);
		storedProc.setParameter("P_ENTITY_ID", Integer.parseInt(entityId));
		storedProc.registerStoredProcedureParameter("P_FROM_RET_PERIOD",
				Integer.class, ParameterMode.IN);
		storedProc.setParameter("P_FROM_RET_PERIOD", fromTaxPeriodInt);
		storedProc.registerStoredProcedureParameter("P_TO_PERIOD_END",
				Integer.class, ParameterMode.IN);
		storedProc.setParameter("P_TO_PERIOD_END", toTaxPeriodInt);
		storedProc.registerStoredProcedureParameter("P_RECON_TYPE",
				String.class, ParameterMode.IN);
		storedProc.setParameter("P_RECON_TYPE", reconType);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing chunking proc"
							+ " USP_EWB_DOWN_RPRT_INS_CHUNK: '%s'",
					entityId.toString());
			LOGGER.debug(msg);
		}

		List<Long> records = storedProc.getResultList();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("no of records after proc call {} ",
					records.size());
		}
		List<Gstr2Config2bprDto> configDtoList = new ArrayList<>();
		if (records != null && !records.isEmpty()) {

			for(Long record: records){
				Gstr2Config2bprDto dto = new Gstr2Config2bprDto();
				dto.setConfigId(BigInteger.valueOf(record));
				configDtoList.add(dto);
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Recon Type and row count: '%s' - '%s'", reconType,
						configDtoList.size());
				LOGGER.debug(msg);
			}
		}
		return configDtoList;
	}
	
}
