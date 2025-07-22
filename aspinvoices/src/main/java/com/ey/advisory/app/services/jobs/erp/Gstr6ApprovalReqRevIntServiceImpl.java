package com.ey.advisory.app.services.jobs.erp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.Gstr6ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6ApprovalRequestHeaderDto;
import com.ey.advisory.common.DestinationConnectivity;

@Service("Gstr6ApprovalReqRevIntServiceImpl")
public class Gstr6ApprovalReqRevIntServiceImpl
		implements Gstr6ApprovalReqRevIntService {

	@Autowired
	private DestinationConnectivity destinationConn;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ApprovalReqRevIntServiceImpl.class);

	public Integer pushToERP(final Gstr6ApprovalRequestHeaderDto headerDto,
			Gstr6ApprovalRequestDto reqDto, AnxErpBatchEntity batch,
			String destinationName) {

		Integer responseCode = 0;
		String xml = null;
		try {
			//responseCode = destinationConn.post(destinationName, xml, batch);
		} catch (Exception e) {

		}
		return responseCode;
	}

}
