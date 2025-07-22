package com.ey.advisory.common;

import java.util.List;

import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.google.gson.JsonObject;

public interface InvoiceDataExecutor {
	JsonObject createCloudJson(List<ERPRequestLogEntity> restPayloads);
}
