package com.ey.advisory.common.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ERPReqRespLogHelper {

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	public void logRequest(String reqData, String identifier) {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
		if (logEntity != null && reqData != null) {
			try {
				logEntity.setNicReqPayload(reqData);
				logEntity.setIdentifer(identifier);
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Req Payload Log",
						ex);
			}
		}
	}

	public void logSwitchIdent(String switchIden) {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
		if (switchIden != null) {
			try {
				logEntity.setSwitchIdentifer(switchIden);
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Req Payload Log",
						ex);
			}
		}
	}

	public void logAckNum(String ackNum) {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
		if (!Strings.isNullOrEmpty(ackNum)) {
			try {
				logEntity.setAckNum(ackNum);
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Req Payload Log",
						ex);
			}
		}
	}

	public void updateResponsePayload(String payload, boolean nicStatus) {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				logEntity.setNicResPayload(payload);
				logEntity.setNicStatus(nicStatus);
				logEntity.setNicRespTimestamp(LocalDateTime.now());
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the EWB Response Payload Log",
					ex);
		}
	}

	public void updateNicErrRespPayload(String payload) {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				logEntity.setNicErrRespPayload(payload);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the EWB Response Payload Log",
					ex);
		}
	}

	public void updateNICReqRespTimeStamp(String operationType,
			boolean isRetry) {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				if (!isRetry) {
					if (operationType.equals("Request")) {
						logEntity.setNicReqTimestamp(LocalDateTime.now());
					} else {
						logEntity.setNicRawRespTimestamp(LocalDateTime.now());
					}
				} else {
					if (operationType.equals("Request")) {
						logEntity.setDuplicate(true);
						logEntity.setNicRetryReqTimestamp(LocalDateTime.now());
					} else {
						logEntity.setNicRetryRawRespTimestamp(
								LocalDateTime.now());
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the EWB Response Payload Log",
					ex);
		}
	}

	public void updateisDuplicate(boolean isDuplicate) {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				if (isDuplicate) {
					logEntity.setDuplicate(true);
				}
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the EWB Response Payload Log",
					ex);
		}
	}

	public void updateNICTimeTakenInSec(Long timeinSec) {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				logEntity.setTimeTakenbyNICSecs(timeinSec);
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while updating the EWB Response Payload Log",
					ex);
		}
	}

	
	public static void main(String[] args) {
		try {
			ERPRequestLogEntity request = new ERPRequestLogEntity();
			request.setId(123L);
			request.setReqUrl("https://api.example.com");
			request.setQryParams("param1=value1&param2=value2");
			request.setPayloadId("payload123");
			request.setCompanyCode("COMP123");
			request.setCheckSum("checksum123");
			request.setReqType("POST");
			request.setReqPayload("{ \"key\": \"value\" }");
			request.setNicReqPayload("{ \"nic\": \"request\" }");
			request.setNicResPayload("{ \"nic\": \"response\" }");
			request.setNicStatus(true);
			request.setErpTimestamp(LocalDateTime.now());
			request.setCloudTimestamp(LocalDateTime.now());
			request.setNicRespTimestamp(LocalDateTime.now());
			request.setNicReqTimestamp(LocalDateTime.now());
			request.setNicRawRespTimestamp(LocalDateTime.now());
			request.setNicRetryReqTimestamp(LocalDateTime.now());
			request.setNicRetryRawRespTimestamp(LocalDateTime.now());
			request.setDuplicate(false);
			request.setTimeTakenbyNICSecs(120L);
			request.setBatchId("batch123");
			request.setApiType("GST");
			request.setDocType("Invoice");
			request.setSGstin("123456789012345");
			request.setDocNum("INV001");
			request.setIrnNum("IRN123456");
			request.setAckNum("ACK123");
			request.setAckDate(LocalDateTime.now());
			request.setEwbNo("EWB123");
			request.setEwbDate(LocalDateTime.now());
			request.setSourceId("source123");
			request.setIdentifer("id123");
			request.setSwitchIdentifer("switch123");
			LoggerIdContext.setLoggerId(request);

			Gson gson = GsonUtil.newSAPGsonInstance();
			LOGGER.debug("Payload {}",
					(gson.toJson(LoggerIdContext.getLoggerId())));
			
			System.out.println(gson.toJson(LoggerIdContext.getLoggerId()));

			
			Gson gsonw = GsonUtil.newSAPGsonInstance();
			ERPRequestLogEntity logEntity = gsonw.fromJson(gson.toJson(LoggerIdContext.getLoggerId()),
					ERPRequestLogEntity.class);
			
			System.out.println(logEntity);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void saveLogEntity() {
		try {
			ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
			if (logEntity != null) {
				logAdvRepo.save(logEntity);
				if (LOGGER.isDebugEnabled()) {
					Gson gson = GsonUtil.gsonInstanceBcapi();
					logEntity.setNicErrRespPayload(
							gson.toJson(LoggerIdContext.getLoggerId()));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Saving the Final Log Entity", ex);

		} finally {
			LoggerIdContext.clearLoggerId();
		}
	}

	public void logAppMessage(String docNo, String irnNo, String ewbNo,
			String message) {
		if (!LOGGER.isDebugEnabled()) {
			return;
		}

		if (docNo != null) {
			LOGGER.debug("{} {} Group is {}", message, docNo,
					TenantContext.getTenantId());
		} else if (irnNo != null) {
			LOGGER.debug("{} {} Group is {}", message, irnNo,
					TenantContext.getTenantId());

		} else {
			LOGGER.debug("{} {} Group is {}", message, ewbNo,
					TenantContext.getTenantId());
		}
	}

	public void logAdditionalParams(String sGstin, String docType,
			String docNum, boolean docTypeReq, boolean docNoReq) {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
		if (Strings.isNullOrEmpty(sGstin))
			throw new AppException("Supplier GSTIN is mandatory.");
		if (docTypeReq && Strings.isNullOrEmpty(docType)) {
			throw new AppException("DocType is mandatory.");
		}
		if (docNoReq && Strings.isNullOrEmpty(docNum)) {
			throw new AppException("DocNum is mandatory.");
		}
		if (logEntity != null) {
			try {
				logEntity.setSGstin(sGstin);
				logEntity.setDocType(docType);
				logEntity.setDocNum(docNum);
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Req Payload Log",
						ex);
			}
		}
	}

	public void logInvDtlsParams(String irnNo, LocalDateTime ackDt,
			String ewbNo, LocalDateTime ewbDate) {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();

		if (logEntity != null) {
			try {
				logEntity.setIrnNum(irnNo);
				logEntity.setAckDate(ackDt);
				logEntity.setEwbNo(ewbNo);
				logEntity.setEwbDate(ewbDate);
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Log InvDtls Params",
						ex);
			}
		}
	}

	public String getSource() {
		ERPRequestLogEntity logEntity = LoggerIdContext.getLoggerId();
		if (logEntity != null) {
			try {
				return logEntity.getIdentifer();
			} catch (Exception ex) {
				LOGGER.error("Exception while updating the Source.", ex);
				return null;
			}
		} else {
			return null;
		}
	}

	private String callReqLogApi(String respObject) {
		try {
			Map<String, Config> regionconfigMap = configManager
					.getConfigs("BCAPI", "alternate.region", "DEFAULT");

			String alternateRegionUrl = regionconfigMap
					.containsKey("alternate.region.url")
							? regionconfigMap.get("alternate.region.url")
									.getValue()
							: "SapApiRequest";

			if ("SapApiRequest".equalsIgnoreCase(alternateRegionUrl)) {
				return "SapApiRequest";
			}

			HttpPost httpPost = new HttpPost(alternateRegionUrl);
			StringEntity params = new StringEntity(respObject);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setHeader("groupCode", TenantContext.getTenantId());
			httpPost.setEntity(params);
			HttpResponse resp = httpClient.execute(httpPost);
			String apiResponse = EntityUtils.toString(resp.getEntity());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Response Received from Cloud for BatchId {} is {}",
						respObject, apiResponse);
			}
			return apiResponse;
		} catch (Exception e) {
			String msg = "Exception occured while saving the Req Log";
			LOGGER.error(msg, e);
			return msg;
		}
	}

	public void saveLogEntity(ERPRequestLogEntity logEntity) {
		try {
			if (logEntity != null) {
				logAdvRepo.save(logEntity);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Saving the Final Log Entity", ex);
		}
	}

}
