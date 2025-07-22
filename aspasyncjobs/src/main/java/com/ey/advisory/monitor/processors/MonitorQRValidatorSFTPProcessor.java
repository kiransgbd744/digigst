package com.ey.advisory.monitor.processors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.qrvspdf.QRValidatorSFTPHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */
@Slf4j
@Component("MonitorQRValidatorSFTPProcessor")
public class MonitorQRValidatorSFTPProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private QRValidatorSFTPHandler srFileArrivalHandler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (StringUtils.isEmpty(group.getSftpUserName())
					|| StringUtils.isEmpty(group.getSftpPassword())) {
				LOGGER.error(
						"SFTP UserName/Password is not COnfigured for Group {},"
								+ " Hence not Monitoring QRValidator SFTP Folder",
						group.getGroupCode());
				return;
			}
			srFileArrivalHandler.sftpFilesDownload();
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
