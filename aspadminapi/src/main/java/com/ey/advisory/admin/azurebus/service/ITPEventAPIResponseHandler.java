package com.ey.advisory.admin.azurebus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.dashboard.homeOld.DashBoardHOReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component
public class ITPEventAPIResponseHandler {

	ImmutableMap<String, String> immutableErrorMap = ImmutableMap.of("DIGI102",
			"No Record found for the provided Inputs", "DIGI103",
			"Invalid API Request Parameters", "GEN501",
			"Missing Mandatory Params");

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	EntityInfoDetailsRepository entityInfo;

	@Autowired
	@Qualifier("ITPEventLandingDashboardResponseServiceImpl")
	ITPEventLandingDashboardResponseService ITPEventLandingDashboardResponseService;

	@Autowired
	@Qualifier("ITPEventEinvoiceDashboardResponseServiceImpl")
	ITPEventEinvoiceDashboardResponseService ITPEventEinvResponseService;

	public void messageHandler(ServiceBusReceivedMessageContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Integer id = null;

		try {
			String inputString = context.getMessage().getBody().toString();
			LOGGER.debug("message {} ", inputString);

			JsonObject obj = JsonParser.parseString(inputString)
					.getAsJsonObject().get("req").getAsJsonObject();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			String category = requestDto.getCategory();

			String groupCode = requestDto.getGroupCode();

			TenantContext.setTenantId(groupCode);

			id = requestDto.getId();

			if (Strings.isNullOrEmpty(category)
					|| Strings.isNullOrEmpty(groupCode)
					|| Strings.isNullOrEmpty(requestDto.getEntityPan())
					|| Strings.isNullOrEmpty(requestDto.getTaxPeriod())
					|| requestDto.getId() == null) {
				sendApiErrorResponse(gson, "Missing Mandatory Params", id, obj);
				context.complete();
				return;
			}

			List<EntityInfoEntity> entityIdList = entityInfo
					.findByPanAndIsDeleteFalse(
							requestDto.getEntityPan().toUpperCase());

			if (entityIdList.isEmpty()) {
				sendApiErrorResponse(gson, "Invalid Pan In Request Parameters",
						id, obj);
				LOGGER.error("Invalid Pan In Request Parameters");
			} else {

				if ("Return_Status".equalsIgnoreCase(category)
						|| "Recon_Summary_2B".equalsIgnoreCase(category)
						|| "Outward_Supply".equalsIgnoreCase(category)
						|| "Recon_Summary_2A".equalsIgnoreCase(category)
						|| "Recon_Summary_2A/PR".equalsIgnoreCase(category)) {
					if ("Return_Status".equalsIgnoreCase(category)) {
						ServiceBusSenderClient senderClient = createSenderClient();
						String retrnStspair = ITPEventLandingDashboardResponseService
								.getReturnStatus(entityIdList.get(0).getId(),
										requestDto.getTaxPeriod(),
										requestDto.getId(), obj);

						sendMessage(senderClient, retrnStspair);
					} else if ("Recon_Summary_2B".equalsIgnoreCase(category)) {
						ServiceBusSenderClient senderClient = createSenderClient();
						String retrnStspair = ITPEventLandingDashboardResponseService
								.getReconSumary2BPR(entityIdList.get(0).getId(),
										requestDto.getTaxPeriod(),
										requestDto.getId(), obj);

						sendMessage(senderClient, retrnStspair);
					} else if ("Recon_Summary_2A".equalsIgnoreCase(category)
							|| ("Recon_Summary_2A/PR"
									.equalsIgnoreCase(category))) {

						ServiceBusSenderClient senderClient = createSenderClient();
						String retrnStspair = ITPEventLandingDashboardResponseService
								.getReconSumary2A(entityIdList.get(0).getId(),
										requestDto.getTaxPeriod(),
										requestDto.getId(), obj);

						sendMessage(senderClient, retrnStspair);
					} else if ("Outward_Supply".equalsIgnoreCase(category)) {
						ServiceBusSenderClient senderClient = createSenderClient();
						String retrnStspair = ITPEventLandingDashboardResponseService
								.getOutwardSupply(entityIdList.get(0).getId(),
										requestDto.getTaxPeriod(),
										requestDto.getId(), obj);

						sendMessage(senderClient, retrnStspair);
					}
				} else if ("Einv_Distribution".equalsIgnoreCase(category)
						|| "Einv_Status".equalsIgnoreCase(category)
						|| "Einv_Gen_Trends".equalsIgnoreCase(category)
						|| "Einv_Error_Trends".equalsIgnoreCase(category)
						|| "Einv_Summary".equalsIgnoreCase(category)) {

					List<String> gstins = ITPEventEinvResponseService
							.getSuppGstins(entityIdList.get(0).getId());
					if (gstins.isEmpty()) {
						LOGGER.error("No Data Found");
						sendEinwrdErrorResponse(gson, "No Data Found", id, obj);
					} else {
						if ("Einv_Distribution".equalsIgnoreCase(category)) {
							ServiceBusSenderClient senderClient = createSenderClient();
							String retrnStspair = ITPEventEinvResponseService
									.getEinvDist(obj, gstins,
											entityIdList.get(0).getId());

							sendMessage(senderClient, retrnStspair);
						} else if ("Einv_Status".equalsIgnoreCase(category)) {
							ServiceBusSenderClient senderClient = createSenderClient();
							String retrnStspair = ITPEventEinvResponseService
									.getEinvSts(obj, gstins,
											entityIdList.get(0).getId());

							sendMessage(senderClient, retrnStspair);
						} else if ("Einv_Gen_Trends"
								.equalsIgnoreCase(category)) {

							ServiceBusSenderClient senderClient = createSenderClient();
							String retrnStspair = ITPEventEinvResponseService
									.getEinvGenTrends(obj, gstins,
											entityIdList.get(0).getId());

							sendMessage(senderClient, retrnStspair);
						} else if ("Einv_Error_Trends"
								.equalsIgnoreCase(category)) {
							ServiceBusSenderClient senderClient = createSenderClient();
							String retrnStspair = ITPEventEinvResponseService
									.getEinvErrorTrends(obj, gstins,
											entityIdList.get(0).getId());
							sendMessage(senderClient, retrnStspair);
						} else if ("Einv_Summary".equalsIgnoreCase(category)) {
							ServiceBusSenderClient senderClient = createSenderClient();
							String retrnStspair = ITPEventEinvResponseService
									.getEinvSummry(obj, gstins,
											entityIdList.get(0).getId());
							sendMessage(senderClient, retrnStspair);
						}
					}

				} else {
					sendApiErrorResponse(gson, immutableErrorMap.get("DIGI103"),
							id, obj);
					LOGGER.error("Invalid API Request Parameters");

				}
			}
			context.complete();
		} catch (Exception e) {
			LOGGER.error(
					"Exception while retrieving the Details, Hence abandoning the Message.",
					e);
			context.abandon();
		} finally {
			TenantContext.clearTenant();
		}
	}

	private ServiceBusSenderClient createSenderClient() {
		return new ServiceBusClientBuilder()
				.connectionString(
						commonUtility.getProp("azureBus.connection.string"))
				.sender()
				.queueName(commonUtility.getProp("azureBus.api.resp.queue"))
				.buildClient();
	}

	private void sendMessage(ServiceBusSenderClient senderClient,
			String messageContent) {
		ServiceBusMessage message = new ServiceBusMessage(messageContent);
		senderClient.sendMessage(message);
		senderClient.close();
	}

	private void sendApiErrorResponse(Gson gson, String errorMessage,
			Integer id, JsonObject obj) {

		JsonObject resp = new JsonObject();
		resp.add("hdr",
				new Gson().toJsonTree(new APIRespDto("E", errorMessage)));
		resp.add("id", gson.toJsonTree(id));
		resp.add("requestParams", obj);
		
		LOGGER.error(resp.toString());
		// eventResponseDto.setAppType(appType);
		sendMessage(createSenderClient(), resp.toString());
	}

	private void sendEinwrdErrorResponse(Gson gson, String errorMessage,
			Integer id, JsonObject obj) {

		JsonObject resp = new JsonObject();
		resp.add("hdr",
				new Gson().toJsonTree(new APIRespDto("E", errorMessage)));

		JsonObject resp1 = new JsonObject();
		resp1.addProperty("status", "Failed");
		resp1.addProperty("message", errorMessage);
		resp1.addProperty("id", id);
		resp1.add("requestParams", obj);

		resp.add("resp", resp1);

		// eventResponseDto.setAppType(appType);
		
		LOGGER.error(resp.toString());
		sendMessage(createSenderClient(), resp.toString());
	}

	public static void main(String[] args) {

		String connectionString = "Endpoint=sb://ci-dv-dms-sb01.servicebus.windows.net;SharedAccessKeyName=AccessKey;SharedAccessKey=6EwDtkLu6sgjpTnapseUfZ4XGM4/jiMxY+ASbPGIwfY=;EntityPath=sapneo";

		String queueName = "sapneo";

		ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
				.connectionString(connectionString).sender()
				.queueName(queueName).buildClient();
		//AAAPH9357H
		ServiceBusMessage message = new ServiceBusMessage(
				"{\"req\":{\"entityPan\":\"AAAPH9357H\",\"taxPeriod\":\"042023\",\"category\":\"Recon_Summary_2B\",\"groupCode\":\"y8nvcqp4f9\",\"id\":1234}}");
		// message.setMessageId(UUID.randomUUID().toString());
		System.out.println(message.getBody().toString());
		senderClient.sendMessage(message);

		senderClient.close();

		System.out.println("End of the method.");
	}
	
	/*public static void main(String[] args) {
		String s = "{\"req\":{\"entityPan\":\"AAAPH9357H\",\"taxPeriod\":\"042021\",\"category\":\"Einv_Distribution\",\"groupCode\":\"y8nvcqp4f9\",\"id\":1234}}";
		
		Gson gson = new Gson();
		
		JsonObject obj = JsonParser.parseString(s)
				.getAsJsonObject().get("req").getAsJsonObject();

		DashBoardHOReqDto requestDto = gson.fromJson(obj,
				DashBoardHOReqDto.class);
		
		List<EntityInfoEntity> entityIdList = entityInfo
				.findByPanAndIsDeleteFalse(
						requestDto.getEntityPan().toUpperCase());
		
		List<String> gstins = Arrays.asList("29AAAPH9357H000");
				String retrnStspair = ITPEventEinvResponseService
						.getEinvDist(obj, gstins,Long.valueOf("32"));

	}*/
	
				
}
