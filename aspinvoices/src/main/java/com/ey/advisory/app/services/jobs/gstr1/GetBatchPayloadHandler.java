/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr1;

import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ey.advisory.app.data.entities.simplified.client.GetBatchJsonPayloadsEntity;
import com.ey.advisory.app.data.repositories.client.GetBatchJsonPayloadsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GetBatchPayloadHandler")
public class GetBatchPayloadHandler {

	@Autowired
	private GetBatchJsonPayloadsRepository batchPayloadRepository;

	public void dumpGetResponsePayload(String groupCode, String gstin,
			String returnPeriod, Long batchId, String requestObject,
			String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Get json payload %s ", requestObject);
			LOGGER.debug(msg);
		}
		if (requestObject == null || StringUtils.isEmpty(requestObject)) {
			LOGGER.error("Response is not valid Json Payload");
			return;
		}

		Clob responseClob = null;
		try {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			responseClob = new javax.sql.rowset.serial.SerialClob(
					requestObject.toCharArray());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Response clob %s ",
						responseClob != null ? responseClob.toString()
								: "Null Gsnt Get json");
				LOGGER.debug(msg);
			}
			TenantContext.setTenantId(groupCode);
			GetBatchJsonPayloadsEntity entity = new GetBatchJsonPayloadsEntity();
			entity.setGstin(gstin);
			entity.setTaxPeriod(returnPeriod);
			entity.setBatchId(batchId);
			entity.setJsonPayload(responseClob);
			entity.setCreatedOn(now);
			entity.setCreatedBy(userName);
			batchPayloadRepository.save(entity);
		} catch (SQLException e) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Exception occured %s", e);
				LOGGER.error(msg);
			}
			throw new AppException(e.getMessage(), e);
		}
	}
}
