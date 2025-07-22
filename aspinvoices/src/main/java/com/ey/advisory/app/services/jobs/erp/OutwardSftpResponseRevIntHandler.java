/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.SftpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.app.docs.dto.erp.OutwardSftpRequestDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.EinvEwbDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service(value = "OutwardSftpResponseRevIntHandler")
public class OutwardSftpResponseRevIntHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSftpResponseRevIntHandler.class);

	@Autowired
	@Qualifier("OutwardSftpResponseRevIntServiceImpl")
	private OutwardSftpResponseRevIntServiceImpl serviceImpl;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("OutwardSftpResponseRevIntInprogressCount")
	private OutwardSftpResponseRevIntInprogressCount revIntInprogressCount;

	@Autowired
	private SftpScenarioPermissionRepository sftpScenPermissionRepo;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	Integer respCode = 0;

	public Integer getOutwardSftpResp(final OutwardSftpRequestDto dto)
			throws Exception {

		String groupCode = TenantContext.getTenantId();
		Long scenoId = dto.getScenarioId();
		Long erpId = dto.getErpId();
		List<EinvEwbDto> resp = null;
		Long fileId = dto.getFileId();

		SftpScenarioPermissionEntity sftpsenarioPermission = sftpScenPermissionRepo
				.findByErpIdScenId(scenoId, erpId);
		String endPoint = sftpsenarioPermission.getEndPointURI();
		String fileName = gstr1FileStatusRepository.getFileName(fileId);
		if (sftpsenarioPermission != null) {
			if ("SFTP"
					.equalsIgnoreCase(sftpsenarioPermission.getSourceType())) {

				Integer totalCount = revIntInprogressCount
						.getInprogressCount(fileId);

				if (totalCount > 0) {
					String msg = "File under process";
					LOGGER.error(msg);
					throw new Exception(msg);
				}

				resp = serviceImpl.getEinvEwbDetails(fileId);

				long batchSize = 0;
				if (resp != null) {
					batchSize = resp.size();
					AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
							groupCode, null, null, null, scenoId, batchSize,
							null, ERPConstants.BACKGROUND_BASED_JOB, erpId,
							null, APIConstants.SYSTEM.toUpperCase());

					respCode = destinationConn.pushToSftp(resp, "EinvEwbDto",
							batch, fileId, fileName, endPoint);

					if (respCode == 200) {
						gstr1FileStatusRepository
								.updateRevIntFlagToTrue(fileId);
					} else {
						gstr1FileStatusRepository
								.updateChildCreatedFlagToFalse(fileId);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("No Data found for outwardSftpResponse");
					}
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"There is no eligible sourceType outwardSftpResponse");
				LOGGER.debug(logMsg);
			}
		}
		return respCode;
	}
}
