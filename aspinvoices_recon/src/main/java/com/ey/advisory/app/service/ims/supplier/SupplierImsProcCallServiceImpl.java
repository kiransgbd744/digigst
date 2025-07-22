package com.ey.advisory.app.service.ims.supplier;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.ims.handlers.ImsInvoicesProcCallService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Service("SupplierImsProcCallServiceImpl")
@Transactional(value = "clientTransactionManager")
public class SupplierImsProcCallServiceImpl
		implements ImsInvoicesProcCallService {

	@Autowired
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void procCall(Gstr1GetInvoicesReqDto dto, Long batchId) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET IMS Invoices inside  SupplierImsProcCallServiceImpl for batch id {} ",
						batchId);
			}

			BigInteger count = BigInteger.ZERO;
			String[] procCallSequences = procCallSequences(dto.getSection(),
					dto.getReturnType());

			count = callStoredProcedure(procCallSequences[0], dto.getGstin(),
					batchId);
			if (count == BigInteger.ZERO) {
				LOGGER.info(
						"No records found for the given GSTIN {} and type {} to be updated",
						dto.getGstin(), dto.getType());
				return;
			} else {
				count = callStoredProcedure(procCallSequences[1],
						dto.getGstin(), batchId);
				if (count == BigInteger.ZERO) {
					LOGGER.info(
							"No records found for the given GSTIN {} and type {} to be updated",
							dto.getGstin(), dto.getType());
					return;
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
	private String[] procCallSequences(String type, String imsReturnTpye) {

		String proc1 = null, proc2 = null;

		if (imsReturnTpye.equalsIgnoreCase("GSTR1")) {

			switch (type) {
			// need procCall the cases for all the types of invoices
			case APIConstants.IMS_TYPE_B2B:
				proc1 = "USP_GETIMS_GSTR1_B2B_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_B2B_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_B2BA:
				proc1 = "USP_GETIMS_GSTR1_B2BA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_B2BA_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_CDNR:
				proc1 = "USP_GETIMS_GSTR1_CDNR_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_CDNR_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_CDNRA:
				proc1 = "USP_GETIMS_GSTR1_CDNRA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_CDNRA_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_ECOM:
				proc1 = "USP_GETIMS_GSTR1_ECOM_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_ECOM_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_ECOMA:
				proc1 = "USP_GETIMS_GSTR1_ECOMA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_ECOMA_TO_ARCHIVE";
				break;
			}

		} else {

			switch (type) {
			// need procCall the cases for all the types of invoices
			case APIConstants.IMS_TYPE_B2B:
				proc1 = "USP_GETIMS_GSTR1A_B2B_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1A_B2B_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_B2BA:
				proc1 = "USP_GETIMS_GSTR1A_B2BA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1_B2BA_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_CDNR:
				proc1 = "USP_GETIMS_GSTR1A_CDNR_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1A_CDNR_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_CDNRA:
				proc1 = "USP_GETIMS_GSTR1A_CDNRA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1A_CDNRA_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_ECOM:
				proc1 = "USP_GETIMS_GSTR1A_ECOM_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1A_ECOM_TO_ARCHIVE";
				break;
			case APIConstants.IMS_TYPE_ECOMA:
				proc1 = "USP_GETIMS_GSTR1A_ECOMA_DUP_CHK";
				proc2 = "USP_GETIMS_GSTR1A_ECOMA_TO_ARCHIVE";
				break;
			}

		}

		// returning the procCall names
		return new String[] { proc1, proc2 };
	}

	public BigInteger callStoredProcedure(String procedureName, String param1,
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
			BigInteger count = BigInteger.ZERO;

			if (procedureName.contains("DUP_CHK")) {
				// Execute stored procedure
				count = (BigInteger) storedProcedure.getSingleResult();
			} else {

				String response = (String) storedProcedure.getSingleResult();
				if ("SUCCESS".equalsIgnoreCase(response)) {
					count = BigInteger.ONE;
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
