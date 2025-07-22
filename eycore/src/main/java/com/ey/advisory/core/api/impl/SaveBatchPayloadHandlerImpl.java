/**
 * 
 */
package com.ey.advisory.core.api.impl;

import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("SaveBatchPayloadHandlerImpl")
public class SaveBatchPayloadHandlerImpl implements SaveBatchPayloadHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Override
	public String dumpSaveRequestPayload(String groupCode, Long id,
			String requestObject) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Save request payload %s ",
					requestObject);
			LOGGER.debug(msg);
		}
		TenantContext.setTenantId(groupCode);
		Optional<Gstr1SaveBatchEntity> findById = batchSaveStatusRepository
				.findById(id);
		Gstr1SaveBatchEntity entity = null;
		if (findById.isPresent()) {
			entity = findById.get();
		} else {
			LOGGER.error("{} is Not valid batch ID, Gsnt SAVE req", id);
			return "Failed";
		}
		Clob responseClob = null;
		try {
		    responseClob = new javax.sql.rowset.serial.SerialClob(
		            requestObject.toCharArray());

		    if (LOGGER.isDebugEnabled() && responseClob != null) {
		        String msg = String.format("Response clob: %s", responseClob);
		        LOGGER.debug(msg);
		    }
		    entity.setSaveRequestPayload(responseClob);
		    entity.setModifiedOn(LocalDateTime.now());
		    batchSaveStatusRepository.save(entity);

		} catch (SQLException e) {
		    String msg = String.format(
		            "Exception occurred while processing request: %s",
		            e.getMessage());
		    LOGGER.error(msg, e); // Log the exception with the stack trace
		}
		return entity.getId() != null ? "Success" : "Failed";
	}

	@Override
	public String dumpSaveResponsePayload(String groupCode, Long id,
			String requestObject) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Save response payload %s ",
					requestObject);
			LOGGER.debug(msg);
		}
		TenantContext.setTenantId(groupCode);
		Optional<Gstr1SaveBatchEntity> findById = batchSaveStatusRepository
				.findById(id);
		Gstr1SaveBatchEntity entity = null;
		if (findById.isPresent()) {
			entity = findById.get();
		} else {
			LOGGER.error("{} is Not valid batch ID, Gsnt SAVE resp", id);
			return "Failed";
		}
		Clob responseClob = null;
		try {
		    responseClob = new javax.sql.rowset.serial.SerialClob(
		            requestObject.toCharArray());

		    if (LOGGER.isDebugEnabled() && responseClob != null) {
		        LOGGER.debug("Response clob: {}", responseClob.toString());
		    } else if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Response clob: Null Gsnt SAVE resp data");
		    }
		    entity.setSaveResponsePayload(responseClob);
		    entity.setModifiedOn(LocalDateTime.now());
		    batchSaveStatusRepository.save(entity);

		} catch (SQLException e) {
		    LOGGER.error(
		            "Exception occurred while processing SAVE response data",
		            e);
		}
		return entity.getId() != null ? "Success" : "Failed";
	}

	@Override
	public String dumpGetReturnStatusResponsePayload(String groupCode, Long id,
			String requestObject) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Get ReturnStatus response payload %s ",
					requestObject);
			LOGGER.debug(msg);
		}
		TenantContext.setTenantId(groupCode);
		Optional<Gstr1SaveBatchEntity> findById = batchSaveStatusRepository
				.findById(id);
		Gstr1SaveBatchEntity entity = null;
		if (findById.isPresent()) {
			entity = findById.get();
		} else {
			LOGGER.error("{} is Not valid batch ID, Gsnt GET resp", id);
			return "Failed";
		}
		Clob responseClob = null;
		try {
		    responseClob = new javax.sql.rowset.serial.SerialClob(requestObject.toCharArray());

		    if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Response clob: {}", responseClob.toString());
		    }

		    entity.setGetResponsePayload(responseClob);
		    entity.setModifiedOn(LocalDateTime.now());
		    batchSaveStatusRepository.save(entity);
		    
		} catch (SQLException e) {
		    LOGGER.error("Exception occurred while processing GET response data", e);
		}
		return entity.getId() != null ? "Success" : "Failed";
	}

}
