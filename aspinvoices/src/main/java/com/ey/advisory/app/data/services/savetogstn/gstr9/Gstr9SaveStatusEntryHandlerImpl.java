package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("Gstr9SaveStatusEntryHandlerImpl")
public class Gstr9SaveStatusEntryHandlerImpl
		implements Gstr9SaveStatusEntryHandler {

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Override
	public void createGstr9SaveStatusEntry(String taxPeriod, String gstin,
			String refId, String status, String filePath, String groupCode,
			String request, String response) {

		Gstr9SaveStatusEntity entity = new Gstr9SaveStatusEntity();
		TenantContext.setTenantId(groupCode);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					request.toCharArray());
		} catch (SerialException e) {
			String msg = "Error occured while serilizing the Gstr9 request payload";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} catch (SQLException e) {
			String msg = "SQL exception occured while converting Gstr9 request payloa"
					+ "d to Clob.";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setStatus(status);
		entity.setRefId(refId);
		entity.setErrorCount(0);
		entity.setFilePath(filePath);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setUpdatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setSaveRequestPayload(reqClob);
		entity.setSaveResponsePayload(response);
		entity = gstr9SaveStatusRepository.save(entity);
	}
}
