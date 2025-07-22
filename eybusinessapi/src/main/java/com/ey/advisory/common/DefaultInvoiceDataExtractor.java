package com.ey.advisory.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.google.gson.JsonObject;

@Component("DefaultInvoiceDataExtractor")
public class DefaultInvoiceDataExtractor implements InvoiceDataExecutor {

	@Autowired
	private RestInvoiceDataExtractor restBcAPISAPExec;

	@Autowired
	private SoapInvoiceDataExtractor soapBcAPISAPExec;

	@Override
	public JsonObject createCloudJson(List<ERPRequestLogEntity> logEntity) {

		if (logEntity.get(0).getReqType().equalsIgnoreCase("REST")) {
			return restBcAPISAPExec.createCloudJson(logEntity);

		} else {
			return soapBcAPISAPExec.createCloudJson(logEntity);
		}
	}
}
