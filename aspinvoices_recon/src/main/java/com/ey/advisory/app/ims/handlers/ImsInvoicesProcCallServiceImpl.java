package com.ey.advisory.app.ims.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("ImsInvoicesProcCallServiceImpl")
@Transactional(value = "clientTransactionManager")
public class ImsInvoicesProcCallServiceImpl
		implements ImsInvoicesProcCallService {

	@Autowired
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void procCall(Gstr1GetInvoicesReqDto dto, Long batchId) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET IMS Invoices inside  ImsInvoicesProcCallServiceImpl for batch id {} ",
						batchId);
			}

		Long count = 0L;
			// get the proc names using dto.get type and run the stored
			// procedure proc call using entityManager
			String[] procCallSequences = procCallSequences(dto.getType());

			count = callStoredProcedure(procCallSequences[0], dto.getGstin(),
					batchId);
			if (count == 0) {
				LOGGER.info(
						"No records found for the given GSTIN {} and type {} to be updated",
						dto.getGstin(), dto.getType());
				return;
			} else {
				count = callStoredProcedure(procCallSequences[1],
						dto.getGstin(), batchId);
				if (count == 0) {
					LOGGER.info(
							"No records found for the given GSTIN {} and type {} to be updated",
							dto.getGstin(), dto.getType());
					return;
				} else {

					count = callStoredProcedure(procCallSequences[2],
							dto.getGstin(), batchId);
					if (count == 0) {
						LOGGER.info(
								"No records found for the given GSTIN {} and type {} to be updated",
								dto.getGstin(), dto.getType());
						return;
					}
				}

			}

		} catch (Exception ex) {
			LOGGER.error(" Error occured while executing the stored procs {} ",
					ex);
			throw new AppException(ex);
		}

	}

	// need a method which return the 3 procCall names for the given type
	// without using tuple
	private String[] procCallSequences(String type) {

		String proc1 = null, proc2 = null, proc3 = null;

		switch (type) {
		// need procCall the cases for all the types of invoices
		case APIConstants.IMS_TYPE_B2B:
			proc1 = "USP_GETIMS_B2B_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_B2B";
			proc3 = "USP_GETIMS_B2B_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_B2BA:
			proc1 = "USP_GETIMS_B2BA_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_B2BA";
			proc3 = "USP_GETIMS_B2BA_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_CN:
			proc1 = "USP_GETIMS_CN_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_CN";
			proc3 = "USP_GETIMS_CN_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_CNA:
			proc1 = "USP_GETIMS_CNA_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_CNA";
			proc3 = "USP_GETIMS_CNA_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_DN:
			proc1 = "USP_GETIMS_DN_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_DN";
			proc3 = "USP_GETIMS_DN_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_DNA:
			proc1 = "USP_GETIMS_DNA_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_DNA";
			proc3 = "USP_GETIMS_DNA_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_ECOM:
			proc1 = "USP_GETIMS_ECOM_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_ECOM";
			proc3 = "USP_GETIMS_ECOM_TO_ARCHIVE";
			break;
		case APIConstants.IMS_TYPE_ECOMA:
			proc1 = "USP_GETIMS_ECOMA_DUP_CHK";
			proc2 = "USP_GETIMS_PSD_UPD_ECOMA";
			proc3 = "USP_GETIMS_ECOMA_TO_ARCHIVE";
			break;

		}
		// returning the procCall names
		return new String[] { proc1, proc2, proc3 };
	}

	public Long callStoredProcedure(String procedureName, String param1,
			Long param2) {
		try {
			StoredProcedureQuery storedProcedure = entityManager
					.createStoredProcedureQuery(procedureName);

			// Set parameters
			storedProcedure.registerStoredProcedureParameter(
					"IP_RECIPIENT_GSTIN", String.class, ParameterMode.IN);
			storedProcedure.registerStoredProcedureParameter("IP_BATCH_ID",
					Long.class, ParameterMode.IN);

			storedProcedure.setParameter("IP_RECIPIENT_GSTIN", param1);
			storedProcedure.setParameter("IP_BATCH_ID", param2);
			Long count = 0L;

			if (procedureName.contains("DUP_CHK")) {
				// Execute stored procedure
				count = (Long) storedProcedure.getSingleResult();
			} else {

				String response = (String) storedProcedure.getSingleResult();
				if ("SUCCESS".equalsIgnoreCase(response)) {
					count = 0L;
				}

			}
			return count;
		} catch (Exception e) {
			LOGGER.error(" exception while executing the proc {} ",
					procedureName);
			throw new AppException(e);
		}
	}

}
