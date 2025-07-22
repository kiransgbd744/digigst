package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailHistoryEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorVGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailHistoryRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.vendorcomm.dto.AzureEMailDto;
import com.ey.advisory.app.vendorcomm.dto.VendorAzureEmailCommDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is the actual 'Runnable' task that's submitted to the thread from
 * the 'Executor Thread Pool'.This class trigger the vendor email for each chunk
 * and updates the Vendor table with email status accordingly
 * 
 * @author Sai.Pakanati
 *
 */
@Slf4j
@AllArgsConstructor
public class EmailExecTask implements Runnable {

	private AzureEMailDto emailDto;

	private String resourceUrl;

	private String groupCode;

	@Override
	public void run() {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		TenantContext.setTenantId(groupCode);
		String emailStatus = null;
		VendorReqVendorGstinRepository vComRepo = StaticContextHolder.getBean(
				"VendorReqVendorGstinRepository",
				VendorReqVendorGstinRepository.class);
		NonCompVendorVGstinRepository ncVComRepo = StaticContextHolder.getBean(
				"NonCompVendorVGstinRepository",
				NonCompVendorVGstinRepository.class);
		VendorEmailHistoryRepository vendrHistryRepository = StaticContextHolder
				.getBean("VendorEmailHistoryRepository",
						VendorEmailHistoryRepository.class);
		HttpClient httpClient = StaticContextHolder
				.getBean("InternalHttpClient", HttpClient.class);
		try {
			long requestId = emailDto.getVendorDetails().get(0).getRequestID();
			List<String> vendorList = emailDto.getVendorDetails().stream()
					.map(VendorAzureEmailCommDto::getVendorGstin)
					.collect(Collectors.toCollection(ArrayList::new));

			// history list
			List<VendorEmailHistoryEntity> historyList = vendrHistryRepository
					.findByRequestId(requestId);
			List<VendorAzureEmailCommDto> venList = emailDto.getVendorDetails();
			List<String> gstinList = new ArrayList<>();
			List<VendorAzureEmailCommDto> vendrList = new ArrayList<>();

			HttpPost httpPost = new HttpPost(resourceUrl);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-TENANT-ID", groupCode);
			StringEntity entity = new StringEntity(gson.toJson(emailDto));
			httpPost.setEntity(entity);
			HttpResponse resp = httpClient.execute(httpPost);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				JsonObject reqJson = reqObject.get("hdr").getAsJsonObject();
				String respStatus = reqJson.get("status").getAsString();

				if (respStatus.equalsIgnoreCase("S")) {
					emailStatus = "SENT";
				} else {
					emailStatus = "FAILED";
				}
				if (emailDto.getSource().equals("VCOM")) {
					if (historyList.isEmpty()
							&& emailStatus.equalsIgnoreCase("SENT")) {

						List<VendorEmailHistoryEntity> finalVenList = convertToEntity(
								venList, emailDto.getReconType());
						vendrHistryRepository.saveAll(finalVenList);

					} else if (!historyList.isEmpty()) {

						Pair<List<VendorAzureEmailCommDto>, List<String>> p1 = gstinListAndAdditionList(
								historyList, venList);
						gstinList = p1.getValue1();
						vendrList = p1.getValue0();
						if (!gstinList.isEmpty()) {
							if (emailStatus.equalsIgnoreCase("SENT"))
								vendrHistryRepository.updateEmailSentStatus(
										requestId, gstinList, emailStatus);
							else
								vendrHistryRepository.updateEmailFailureStatus(
										requestId, gstinList, emailStatus);
						}
						if (!vendrList.isEmpty()
								&& emailStatus.equalsIgnoreCase("SENT")) {
							List<VendorEmailHistoryEntity> finalVenList = convertToEntity(
									vendrList, emailDto.getReconType());
							vendrHistryRepository.saveAll(finalVenList);
						}/* else {
							vComRepo.updateEmailStatus(requestId, vendorList,
									emailStatus, LocalDateTime.now());
						}*/

					}
					vComRepo.updateEmailStatus(requestId, vendorList,
							emailStatus, LocalDateTime.now());
				} else {
					ncVComRepo.updateEmailStatus(requestId, vendorList,
							emailStatus, LocalDateTime.now());
				}
			} else {
				emailStatus = "FAILED";
				if (emailDto.getSource().equals("VCOM")) {
					if (!historyList.isEmpty()) {
						Pair<List<VendorAzureEmailCommDto>, List<String>> p1 = gstinListAndAdditionList(
								historyList, venList);
						gstinList = p1.getValue1();
						vendrList = p1.getValue0();

						vendrHistryRepository.updateEmailFailureStatus(
								requestId, gstinList, emailStatus);
					}
					vComRepo.updateEmailStatus(requestId, vendorList,
							emailStatus, LocalDateTime.now());
				} else {
					ncVComRepo.updateEmailStatus(requestId, vendorList,
							emailStatus, LocalDateTime.now());
				}
				LOGGER.error("Recieved error response from azure:{}", apiResp);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking Azure Vendor Email", ex);
		} finally {
			TenantContext.clearTenant();
		}
	}

	private List<VendorEmailHistoryEntity> convertToEntity(
			List<VendorAzureEmailCommDto> venList, String reconType) {
		List<VendorEmailHistoryEntity> finalListEmail = new ArrayList<>();

		venList.forEach(o -> {
			VendorEmailHistoryEntity obj = new VendorEmailHistoryEntity();
			obj.setReconType(reconType);
			obj.setRequestId(o.getRequestID());
			obj.setVendorGstin(o.getVendorGstin());
			obj.setVendrPrimryEmail(o.getVendPrimEmailId());
			obj.setVendrSecEmail(String.join(",", o.getSecondaryEmailIds()));
			obj.setRecipientEmail(String.join(",", o.getRecipientEmailIds()));

			obj.setVendrTotalSecEmail(
					String.join(",", o.getTotalSecEmailIds()));
			obj.setTotalRecipEmail(String.join(",", o.getTotalRecpEmailIds()));

			obj.setEmailStatus("SENT");
			obj.setCreatedOn(LocalDateTime.now());
			obj.setUpdatedBy(o.getCreatedBy());
			obj.setVendorContactNumber(o.getVendorContactNumber());
			obj.setVendorName(o.getVendorName());
			obj.setCounter(1);
			finalListEmail.add(obj);
		});
		return finalListEmail;
	}

	private Pair<List<VendorAzureEmailCommDto>, List<String>> gstinListAndAdditionList(
			List<VendorEmailHistoryEntity> historyList,
			List<VendorAzureEmailCommDto> venList) {
		List<VendorAzureEmailCommDto> finalList = new ArrayList<>();
		List<String> gstinList = new ArrayList<>();
		Map<String, List<VendorEmailHistoryEntity>> histryMap = historyList
				.stream().collect(Collectors
						.groupingBy(VendorEmailHistoryEntity::getVendorGstin));
		venList.forEach(eachObj -> {
			if (histryMap.containsKey(eachObj.getVendorGstin())) {
				gstinList.add(eachObj.getVendorGstin());
			} else {
				finalList.add(eachObj);
			}
		});

		return new Pair<>(finalList,gstinList);
	}
}
