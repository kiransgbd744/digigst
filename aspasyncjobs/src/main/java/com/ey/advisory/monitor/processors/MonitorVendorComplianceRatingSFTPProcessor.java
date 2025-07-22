package com.ey.advisory.monitor.processors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.returns.compliance.service.VendorComplianceRatingSFTPHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("MonitorVendorComplianceRatingSFTPProcessor")
@Slf4j
public class MonitorVendorComplianceRatingSFTPProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private VendorComplianceRatingSFTPHandler srFileArrivalHandler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (StringUtils.isEmpty(group.getSftpUserName())
					|| StringUtils.isEmpty(group.getSftpPassword())) {
				LOGGER.error(
						"SFTP UserName/Password is not COnfigured for Group {},"
								+ " Hence not Monitoring SFTP Folder",
						group.getGroupCode());
				return;
			}
			srFileArrivalHandler.sftpFilesDownload();
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}
}