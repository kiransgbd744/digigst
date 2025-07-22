package com.ey.advisory.app.gstr1.einv;

import java.math.BigInteger;

/**
 * 
 * @author Siva Reddy
 *
 */

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EinvReconRespGSTINEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconRespConfigRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconRespGSTINRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconSummRespReportRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Service("EinvSummaryResponseServiceImpl")
@Slf4j
public class EinvSummaryResponseServiceImpl
		implements EinvSummaryResponseService {

	@Autowired
	@Qualifier("EinvReconRespGSTINRepository")
	private EinvReconRespGSTINRepository einvReconRespGSTINRepo;

	@Autowired
	@Qualifier("EinvReconRespConfigRepository")
	private EinvReconRespConfigRepository einvReconRespConfigRepo;

	@Autowired
	@Qualifier("EinvReconSummRespReportRepository")
	private EinvReconSummRespReportRepository einvReconSummRespReptRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void saveEinvSummaryReport(String configId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside saveEinvSummaryReport  for config Id "
					+ configId;
			LOGGER.debug(msg);
		}
		try {

			einvReconRespConfigRepo.updateCompletedOnandStatus(
					Long.valueOf(configId), "InProgress", null);

			List<EinvReconRespGSTINEntity> einvReconRespGSTINEntity = einvReconRespGSTINRepo
					.findByConfigId(Long.valueOf(configId));

			List<String> gstinList = einvReconRespGSTINEntity.stream()
					.map(EinvReconRespGSTINEntity::getGstin)
					.collect(Collectors.toList());

			int updatedRecords = einvReconRespGSTINRepo
					.updateActiveExistingRecords(gstinList,
							einvReconRespGSTINEntity.get(0).getReturnPeriod());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Updated Existing Active Records {} for"
								+ " config Id {}  in EinvReconRespGSTINEntity",
						updatedRecords, configId);
			}

			einvReconRespGSTINRepo.updateStatusandErrorDesc(
					Long.valueOf(configId), null, "InProgress");

			StoredProcedureQuery chunkEinvRecSummProc = entityManager
					.createNamedStoredProcedureQuery(
							"insertEinvRecSumChunkData");

			chunkEinvRecSummProc.setParameter("P_RECON_RESP_CONFIG_ID",
					Long.valueOf(configId));
			chunkEinvRecSummProc.setParameter("P_CHUNK_SPILIT_VAL",
					Integer.valueOf(10000));

			BigInteger chunkEinvRecSummProcResp = (BigInteger) chunkEinvRecSummProc
					.getSingleResult();

			Integer chunkEinvRecSummProcRespInt = chunkEinvRecSummProcResp
					.intValue();
			LOGGER.debug("Response chunkEinvRecSummProc of {}",
					chunkEinvRecSummProcRespInt);

			if (chunkEinvRecSummProcRespInt == 0) {
				String msg = String.format(
						"ChunkEinvRecSummProc is failed for configId %s",
						configId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			LOGGER.debug(
					"ChunkEinv Summary Proc has returned Successfully, Hence Processing to chunk response");

			for (int i = 1; i <= chunkEinvRecSummProcRespInt; i++) {

				Integer chunkVal = Integer.valueOf(i);
				String einvRecSummRespProcResp = callEInvRecSummRespProc(
						configId, chunkVal);
				if (einvRecSummRespProcResp.equalsIgnoreCase("SUCCESS")) {
					LOGGER.debug(
							"EinvRecSummRespProc(Proc2) data loaded successfully for ConfigId {} and ChunkVal {}",
							configId, chunkVal);
				} else {
					String msg = String.format(
							"EinvRecSummRespProc(Proc2) is failed for ChunkVal %s and ConfigId %s",
							chunkVal, configId);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}

			eInvReconConfigandGSTINStatus(null, "Completed", configId,
					LocalDateTime.now());

		} catch (Exception e) {
			String msg = "Failed in Einv Summary Response Report.";
			LOGGER.error(msg, e);
			eInvReconConfigandGSTINStatus(e.getMessage(), "Failed", configId,
					LocalDateTime.now());
			throw new AppException(msg, e);
		}
	}

	private void eInvReconConfigandGSTINStatus(String errMsg, String status,
			String configId, LocalDateTime completedOn) {

		einvReconRespConfigRepo.updateCompletedOnandStatus(
				Long.valueOf(configId), status, completedOn);
		einvReconRespGSTINRepo.updateStatusandErrorDesc(Long.valueOf(configId),
				errMsg, status);

	}

	// private List<String> callChunkMapTBLandGenRepId(String configId) {
	//
	// try {
	// String queryStr = "SELECT REPORT_TYPE FROM TBL_CHUNK_MAP_DATA"
	// + " WHERE REPORT_DOWNLOAD_ID=:configId";
	//
	// Query q = entityManager.createNativeQuery(queryStr);
	//
	// q.setParameter("configId", Long.valueOf(configId));
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "executing query for the TBL_CHUNK_MAP_DATA :: configId "
	// + configId);
	// }
	// @SuppressWarnings("unchecked")
	// List<String> einvchunkMapList = q.getResultList();
	//
	// if (einvchunkMapList.isEmpty()) {
	// LOGGER.error(
	// "Could not Find Records in Chunk Table for ConfigId {}",
	// configId);
	// return null;
	// }
	//
	// return einvchunkMapList;
	// } catch (Exception e) {
	// throw new AppException(e);
	// }
	// }

	// private EinvRecSumRecChunkMapDataDto convertToDto(Object[] arr) {
	//
	// EinvRecSumRecChunkMapDataDto dto = new EinvRecSumRecChunkMapDataDto();
	// if (arr[2] != null) {
	// dto.setReportType(Integer.valueOf(arr[2].toString()));
	// }
	// return dto;
	// }

	private String callEInvRecSummRespProc(String configId, Integer reportId) {

		LOGGER.debug(
				"Calling EInvRecSummRespProc for ConfigId {} and ReportType {}",
				configId, reportId);
		StoredProcedureQuery einvRecSummRespProc = entityManager
				.createNamedStoredProcedureQuery("eInvRecSummResp");

		einvRecSummRespProc.setParameter("P_RECON_RESP_CONFIG_ID",
				Long.valueOf(configId));

		einvRecSummRespProc.setParameter("P_CHUNK_DATA", reportId);

		String einvRecSummRespProcResp = (String) einvRecSummRespProc
				.getSingleResult();

		return einvRecSummRespProcResp;

	}

}
