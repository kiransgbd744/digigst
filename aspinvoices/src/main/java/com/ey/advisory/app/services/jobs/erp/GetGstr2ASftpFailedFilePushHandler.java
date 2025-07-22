package com.ey.advisory.app.services.jobs.erp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpSFTPBatchIdLogEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpSFTPBatchIdLogRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.common.AppException;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Slf4j
@Service("GetGstr2ASftpFailedFilePushHandler")
public class GetGstr2ASftpFailedFilePushHandler {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AnxErpSFTPBatchIdLogRepository batchSftpRepo;

	@Autowired
	@Qualifier("Get2ASFTPConsoForSectionDaoImpl")
	private Get2ASFTPConsoForSectionDao sftpDaoImpl;

	@Autowired
	@Qualifier("Get2ASFTPConsoForSectionServiceImpl")
	private Get2ASFTPConsoForSectionService sftpService;

	@Autowired
	private GetAnx1BatchRepository anx1BatchRepo;

	public void handleFailedBatches(String groupCode) {

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Failed batches handler starting block ");
			}

			List<AnxErpSFTPBatchIdLogEntity> failedErpBatchIds = batchSftpRepo
					.findTop50BySftpPushStatusInAndIsDeleteFalseOrderByIdAsc(
							Arrays.asList("FAILED", "NOT_INITIATED"));

			if (!Collections.isEmpty(failedErpBatchIds)) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("failedERPBatchIds size {}",
							failedErpBatchIds.size());
				}
				Integer respCode = 0;
				int erpBatchIds = 0;
				Long batchId = 0L;
				int fileIndex = 0;
				Long erpBatchIdsSftp = 0L;

				try {

					Map<Long, List<AnxErpSFTPBatchIdLogEntity>> failedBatchesMap = failedErpBatchIds
							.stream().collect(Collectors.groupingBy(o -> o.getBatchId()));

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("failedBatchesMap {} and groupCode {}",
								failedBatchesMap, groupCode);
					}

					for (Map.Entry<Long, List<AnxErpSFTPBatchIdLogEntity>> entry : failedBatchesMap
							.entrySet()) {
						List<AnxErpSFTPBatchIdLogEntity> failedBatchIdslist = entry
								.getValue();

						batchId = entry.getKey();
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("batchId {} and groupCode {}",
									batchId, groupCode);
						}
						erpBatchIds = batchSftpRepo
								.countByBatchIdAndIsDeleteFalse(batchId);

						for (AnxErpSFTPBatchIdLogEntity erpBatchIdEntity : failedBatchIdslist) {

							erpBatchIdsSftp = erpBatchIdEntity.getErpBatchId();

							fileIndex = Integer
									.parseInt(erpBatchIdEntity.getFileIndex());

							List<Object[]> objs = sftpDaoImpl
									.findSFTPGet2AConsoForSection(
											erpBatchIdEntity.getGstin(),
											erpBatchIdEntity.getTaxPeriod(),
											erpBatchIdEntity.getSection(),
											erpBatchIdEntity.getBatchId(),
											erpBatchIdEntity.getErpBatchId());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("erpbatchIds {} and groupCode {}",
										erpBatchIdEntity.getErpBatchId(),
										groupCode);
							}

							if (objs != null && !objs.isEmpty()) {
								List<ConsolidatedGstr2ADto> finalDto = sftpService
										.getConsolidatedItemList(
												erpBatchIdEntity.getSection(),
												objs);

								String fileName = sftpService
										.createSftpFileName(
												erpBatchIdEntity.getGstin(),
												erpBatchIdEntity.getFileIndex(),
												String.valueOf(erpBatchIds),
												erpBatchIdEntity.getTaxPeriod(),
												erpBatchIdEntity.getSection())
										+ ".csv";

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Reverse push started for SFTP {} "
													+ "and erpId {} and itemSize {}",
											fileName, erpBatchIdEntity.getId(),
											finalDto.size());
								}

								respCode = sftpService
										.uploadToSftpServer(fileName, finalDto);

								batchSftpRepo.updateSftpPushStatus(
										erpBatchIdEntity.getErpBatchId(),
										"SUCCESS", batchId);

							}
						}
						String status = (fileIndex) + "/" + (erpBatchIds);
						anx1BatchRepo.updateSftpStatus(status, batchId);

					}
				} catch (Exception ex) {
					anx1BatchRepo.updateSftpErrorStatus(
							(fileIndex - 1) + "/" + (erpBatchIds), batchId,
							StringUtils.truncate(ex.getMessage(), 2048));

					batchSftpRepo.updateSftpPushStatus(erpBatchIdsSftp,
							"FAILED", batchId);
					LOGGER.error(
							"Exception Occured in SFTP monitoring job: {} ",
							ex);
					throw new AppException(
							"Exception Occured in SFTP monitroing job: {}", ex);

				}

			} else {
				LOGGER.error(
						"No SFTP getGstr2A Failed batches for groupcode {}",
						groupCode);
				return;
			}

		} catch (Exception ex) {
			LOGGER.error("Exception Occured in SFTP monitoring job: {} ", ex);
			throw new AppException(
					"Exception Occured: in SFTP monitoring job {}", ex);
		}
	}
}
