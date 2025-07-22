package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("gstr3BSaveStatusEntryHandlerImpl")
@Slf4j
public class Gstr3BSaveStatusEntryHandlerImpl
		implements Gstr3BSaveStatusEntryHandler {

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	Gstr3BSaveStatusRepository gstr3BSaveStatusRepository;

	@Override
	public void createGstr3BSaveStatusEntry(String taxPeriod, String gstin,
			String refId, String status, String filePath, String groupCode,
			String request, String response, String apiAction) {

		Gstr3BSaveStatusEntity entity = new Gstr3BSaveStatusEntity();
		TenantContext.setTenantId(groupCode);

		Clob reqClob = null;
		try {
			if (!Strings.isNullOrEmpty(request)) {
				reqClob = new javax.sql.rowset.serial.SerialClob(
						request.toCharArray());
			}
		} catch (SerialException e) {
			String msg = "Error occured while serilizing the 3B request payload";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} catch (SQLException e) {
			String msg = "SQL exception occured while converting 3B request payloa"
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
		entity.setApiAction(apiAction);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setUpdatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setSaveRequestPayload(reqClob);
		entity.setSaveResponsePayload(response);
		entity = gstr3BSaveStatusRepository.save(entity);
	}

}
