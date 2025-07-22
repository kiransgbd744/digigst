package com.ey.advisory.app.services.recon.jobs.anx2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.anx2.Anx2Reconciliation2aDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository
@Transactional
public class Anx2Reconciliation2aImpl implements Anx2Reconciliation2a {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2Reconciliation2aImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void call2aProc(String jsonReq, String groupCode) {
		
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Anx2Reconciliation2aDto dto = gson.fromJson(requestObject,
					Anx2Reconciliation2aDto.class);
			String proc = null;
			if(dto.getSection().equalsIgnoreCase(APIConstants.B2B)) {
				 proc = APIConstants.PROC_2A_B2B;
			}else if(dto.getSection().equalsIgnoreCase(APIConstants.SEZWOP)) {
				proc = APIConstants.PROC_2A_SEZWOP;
			}else if(dto.getSection().equalsIgnoreCase(APIConstants.SEZWP)) {
				proc = APIConstants.PROC_2A_SEZWP;
			}else if(dto.getSection().equalsIgnoreCase(APIConstants.DE)) {
				proc = APIConstants.PROC_2A_DE;
			}
			if (dto.getBatchId() != null && proc != null) {
				LOGGER.debug("2a recon Proc {} call started.", proc);
				TenantContext.setTenantId(groupCode);
				LOGGER.info("groupCode {} is set", groupCode);
				StoredProcedureQuery st = entityManager
						.createStoredProcedureQuery(proc);
				// set parameters
				st.registerStoredProcedureParameter("IP_A2_BID", Long.class,
						ParameterMode.IN);
				st.setParameter("IP_A2_BID", dto.getBatchId());
				st.execute();
			}
		} catch (Exception e) {
			LOGGER.error("Error While Executing Procedure {}", e);
		}
	}

}
