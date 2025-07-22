package com.ey.advisory.app.services.vendorcomm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailHistoryEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailHistoryRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.vendorcomm.dto.VendorReportDownloadDto;
import com.ey.advisory.app.vendorcomm.dto.VendorResponseDataDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Vendor2wayserviceImpl {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	VendorCommRequestRepository vendrCommRepo;

	@Autowired
	@Qualifier("VendorEmailHistoryRepository")
	VendorEmailHistoryRepository vendrEmailHstRepo;

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	VendorReqVendorGstinRepository vendorReqVendorGstinRepo;

	@Autowired
	@Qualifier("VendorCom2WayDaoImpl")
	VendorCom2WayDao vendor2wayDao;

	public String vendorReportReqId(Long reqId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			Map<String, Config> configMap = configManager
					.getConfigs("VENDOR_COMM", "azure.sendReqId", "DEFAULT");

			String tenantCode = TenantContext.getTenantId();
			String url = configMap.get("azure.sendReqId").getValue();

			vendrCommRepo.updatePrepStatus(reqId,
					ReconStatusConstants.VENDR_REQID_INITIATED);

			HttpPost httpPost = new HttpPost(url);
			JsonObject obj = new JsonObject();
			obj.addProperty("configId", reqId.toString());
			obj.addProperty("source", "CF");

			httpPost.setHeader("X-TENANT-ID", tenantCode);

			StringEntity entity = new StringEntity(gson.toJson(obj).toString());
			httpPost.setEntity(entity);
			HttpResponse resp = httpClient.execute(httpPost);

			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {

				LOGGER.debug(
						"Received successfull response from Vcom app while "
								+ "data preparation, the response is {}",
						apiResp);
			} else {
				LOGGER.error(
						"Received failure response from Vcom app while "
								+ "data preparation, the response is {}",
						apiResp);
			}
			return apiResp;

		} catch (Exception ex) {
			JsonObject respObj = new JsonObject();
			vendrCommRepo.updatePrepStatus(reqId,
					ReconStatusConstants.VENDR_REQID_FAILED);
			LOGGER.error("Exception while calling saveReqIdApi ", ex);
			respObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respObj.add("resp", ewbGson.toJsonTree(ex.getMessage()));
			return respObj.toString();
		}
	}

	@SuppressWarnings("null")
	public Pair<List<VendorResponseDataDto>, Integer> getVendorRespData(
			VendorReportDownloadDto jsonDto, int pageNum, int pageLimit) {

		Pair<List<VendorResponseDataDto>, Integer> vendrResponse = null;
		int totalRec = 0;
		try {
			List<VendorResponseDataDto> vendrResp = new ArrayList<>();
			Long requestId = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside getvendorRespData with identifier : %s",
						jsonDto.getIdentifier());
				LOGGER.debug(msg);
			}

			switch (jsonDto.getIdentifier()) {
			case "E":
				requestId = vendrCommRepo.getLastReqId();
			case "R":
				if (requestId == null) {
					requestId = Long.valueOf(jsonDto.getReqId());
				}
				List<VendorEmailHistoryEntity> emailHistryEntity = vendrEmailHstRepo
						.findByRequestId(requestId);

				if (emailHistryEntity == null || emailHistryEntity.isEmpty())
					return null;

				List<String> vendGstin = emailHistryEntity.stream()
						.map(VendorEmailHistoryEntity::getVendorGstin)
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Vendor GSTIN fetched from History table {}",
							vendGstin);
				}
				Pageable pageReq = PageRequest.of(pageNum, pageLimit,
						Direction.DESC, "id");

				List<VendorReqVendorGstinEntity> venReqList = vendorReqVendorGstinRepo
						.findByVendorGstinInAndIsRespTrueAndRequestId(vendGstin,
								requestId, pageReq);

				if (venReqList.isEmpty())
					return null;
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Vendor RequestId {} based entity fetched{} ",requestId,
							venReqList);
				}
				VendorCommRequestEntity vendrComEntity = vendrCommRepo
						.findByRequestId(requestId);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("venReqList {} for reqId {}", venReqList,
							requestId);
				}

				String taxPeriod = getMonthNameFromNumber(
						vendrComEntity.getFromTaxPeriod().toString()) + " - "
						+ getMonthNameFromNumber(
								vendrComEntity.getToTaxPeriod().toString());

				/*
				 * List<Object[]> respdata = vendrCommRepo
				 * .getResponseData(vendGstin, requestId);
				 */ totalRec = vendorReqVendorGstinRepo
						.findCountByVendorGstinInAndIsRespTrueAndRequestId(
								vendGstin, requestId);
				vendrResp = convertToDto(venReqList, emailHistryEntity,
						requestId, taxPeriod);

				return new Pair<>(vendrResp, totalRec);
			/*
			 * vendrResponse.setAt0(vendrResp); vendrResponse.setAt1(totalRec);
			 */

			case "F":
				vendrResponse = vendor2wayDao.getVendrData(jsonDto, pageNum,
						pageLimit);
				break;
			}

		} catch (Exception e) {
			String msg = "Exception in getVendorRespData serviceimpl class";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		}
		return vendrResponse;
	}

	private List<VendorResponseDataDto> convertToDto(
			List<VendorReqVendorGstinEntity> respData,
			List<VendorEmailHistoryEntity> histryEntity, Long reqId,
			String taxPeriod) {
		List<VendorResponseDataDto> vendrResp = new ArrayList<>();

		Map<String, VendorEmailHistoryEntity> histryMap = histryEntity.stream()
				.collect(Collectors.toMap(
						VendorEmailHistoryEntity::getVendorGstin,
						Function.identity()));

		for (VendorReqVendorGstinEntity obj : respData) {
			VendorResponseDataDto dto = new VendorResponseDataDto();

			VendorEmailHistoryEntity entity = histryMap
					.get(obj.getVendorGstin());
			dto.setDate(EYDateUtil.fmtDate(
					EYDateUtil.toISTDateTimeFromUTC(obj.getUpdatedOn())).substring(0,10));
			dto.setVendorPan(obj.getVendorGstin().substring(2, 12));
			dto.setVendorGstin(obj.getVendorGstin());
			dto.setVendorName(entity.getVendorName());
			dto.setReqId(reqId.toString());
			dto.setTotalRec(obj.getTotalRec().toString());
			dto.setTaxPeriod(taxPeriod);
			dto.setRespRecords(obj.getRespRecordsCnt().toString());

			String respfilePath = obj.getRespFilePath();

			if (respfilePath == null || respfilePath.isEmpty()) {
				dto.setRespDownld(false);
				dto.setRespDowldPath(null);
			} else {
				dto.setRespDownld(true);
				dto.setRespDowldPath(respfilePath);
			}

			String totalRespfilePath = obj.getTotFilePath();

			if (totalRespfilePath == null || totalRespfilePath.isEmpty()) {
				dto.setTotRespDownld(false);
				dto.setTotalRespDowldPath(null);
			} else {
				dto.setTotRespDownld(true);
				dto.setTotalRespDowldPath(totalRespfilePath);
			}

			dto.setStatus(obj.isRead() ? "Read" : "Unread");
			vendrResp.add(dto);
		}
		return vendrResp;
	}

	private String getMonthNameFromNumber(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString + " " + taxPeriod.substring(4, 6);
	}

}
