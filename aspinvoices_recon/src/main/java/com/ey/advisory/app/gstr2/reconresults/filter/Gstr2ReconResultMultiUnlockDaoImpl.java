package com.ey.advisory.app.gstr2.reconresults.filter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.ReportDao;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprPsdErrInfoReportDwnldService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReconStatusConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2ReconResultMultiUnlockDaoImpl")
public class Gstr2ReconResultMultiUnlockDaoImpl
		implements Gstr2ReconResultMultiUnlockDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("ReportDaoImpl")
	private ReportDao error2APRDao;
	

	@Autowired
	@Qualifier("GSTR2bprPsdErrInfoReportDwnldServiceImpl")
	private GSTR2bprPsdErrInfoReportDwnldService error2BPRDao;
	

	@Override
	public void unlockResponseAndErrorFileCrea(Long batchId, String reconType,
			Long fileId) {

		if (reconType.equalsIgnoreCase("2A_PR")) {
			StoredProcedureQuery storedProc = null;
			String response = null;
			try {
				storedProc = entityManager.createStoredProcedureQuery(
						"USP_AUTO_2APR_RECON_RESULT_MULTIUNLOCKUI");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"About to execute Recon SP USP_AUTO_2APR_RECON_RESULT_MULTIUNLOCKUI with ConfigId :%s",
							batchId.toString());
					LOGGER.debug(msg);
				}

				storedProc.registerStoredProcedureParameter("IN_BATCHID",
						Long.class, ParameterMode.IN);

				storedProc.setParameter("IN_BATCHID", batchId);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(
						"USP_AUTO_2APR_RECON_RESULT_MULTIUNLOCKUI" + " :: " + response);

				if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format("Batch Id is '%s', Response "
							+ "from USP_AUTO_2APR_RECON_RESULT_MULTIUNLOCKUI did not "
							+ "return success," + " Hence updating to Failed",
							batchId.toString());
					LOGGER.error(msg);
					throw new AppException(msg);
				}else {
					storedProc = entityManager.createStoredProcedureQuery(
							"USP_AUTO_2APR_RECON_RESP_MULTIUNLOCK");

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"About to execute Recon SP with ConfigId :%s",
								batchId.toString());
						LOGGER.debug(msg);
					}

					storedProc.registerStoredProcedureParameter("VAR_BATCHID",
							Long.class, ParameterMode.IN);

					storedProc.setParameter("VAR_BATCHID", batchId);

					response = (String) storedProc.getSingleResult();

					LOGGER.debug("USP_AUTO_2APR_RECON_RESP_MULTIUNLOCK" + " :: " + response);

					if (ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response) || "Success with Errors".equalsIgnoreCase(response)) {

						return;
					}
					else
					{
						String msg = String.format(
								"Batch Id is '%s', Response "
										+ "from USP_AUTO_2APR_RECON_RESP_MULTIUNLOCK did not "
										+ "return success,"
										+ " Hence updating to Failed",
								batchId.toString());
						LOGGER.error(msg);
						throw new AppException(msg);
						
					}
					
				}

			} catch (Exception ex) {
				String msg = String.format(
						"Batch Id is '%s', result result processing failed",
						batchId.toString());
				LOGGER.error(msg, ex);
				fileStatusRepository.updateFailedStatus(batchId,
						StringUtils.truncate(ex.getLocalizedMessage(), 4000),
						"FAILED");
				throw new AppException(msg, ex);
			}
		} else {
			StoredProcedureQuery storedProc = null;
			String response = null;
			try {
				storedProc = entityManager.createStoredProcedureQuery(
						"USP_2BPR_RECON_RESULT_MULTIUNLOCKUI");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"About to execute Recon SP USP_2BPR_RECON_RESULT_MULTIUNLOCKUI with ConfigId :%s",
							batchId.toString());
					LOGGER.debug(msg);
				}

				storedProc.registerStoredProcedureParameter("IN_BATCHID",
						Long.class, ParameterMode.IN);

				storedProc.setParameter("IN_BATCHID", batchId);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(
						"USP_2BPR_RECON_RESULT_MULTIUNLOCKUI" + " :: " + response);

				if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format("Batch Id is '%s', Response "
							+ "from USP_2BPR_RECON_RESULT_MULTIUNLOCKUI did not "
							+ "return success," + " Hence updating to Failed",
							batchId.toString());
					LOGGER.error(msg);
					throw new AppException(msg);
				}else {
					storedProc = entityManager.createStoredProcedureQuery(
							"USP_2BPR_RECON_RESP_MULTIUNLOCK");

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"About to execute Recon SP with ConfigId :%s",
								batchId.toString());
						LOGGER.debug(msg);
					}

					storedProc.registerStoredProcedureParameter("VAR_BATCHID",
							Long.class, ParameterMode.IN);

					storedProc.setParameter("VAR_BATCHID", batchId);

					response = (String) storedProc.getSingleResult();

					LOGGER.debug("USP_2BPR_RECON_RESP_MULTIUNLOCK" + " :: " + response);

					if (ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response) || "Success with Errors".equalsIgnoreCase(response)) {

						return;
					}
					else
					{
						String msg = String.format(
								"Batch Id is '%s', Response "
										+ "from USP_2BPR_RECON_RESP_MULTIUNLOCK did not "
										+ "return success,"
										+ " Hence updating to Failed",
								batchId.toString());
						LOGGER.error(msg);
						throw new AppException(msg);
						
					}
					
				}

			} catch (Exception ex) {
				String msg = String.format(
						"Batch Id is '%s', result result processing failed",
						batchId.toString());
				LOGGER.error(msg, ex);
				fileStatusRepository.updateFailedStatus(batchId,
						StringUtils.truncate(ex.getLocalizedMessage(), 4000),
						"FAILED");
				throw new AppException(msg, ex);
			}
		}
	}
	
}
