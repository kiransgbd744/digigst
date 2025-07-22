package com.ey.advisory.app.inward.einvoice;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailPayloadEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnDtlPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author kiran s
 *
 */
@Slf4j
@Service("InwardGetIrnJsonDataParserImpl")
@Transactional(value = "clientTransactionManager")
public class InwardGetIrnJsonDataParserImpl
		implements
			InwardGetIrnListDataParser {

	@Autowired
	private GetIrnListingRepository getIrnListRepo;
	@Autowired
	GetIrnDtlPayloadRepository irnDtlPayloadRepo;

	private static final List<String> GETIRN_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.INV_TYPE_B2B, APIConstants.INV_TYPE_SEZWP,
			APIConstants.INV_TYPE_SEZWOP, APIConstants.INV_TYPE_DXP,
			APIConstants.INV_TYPE_EXPWP, APIConstants.INV_TYPE_EXPWOP);

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public List<String> parseIrnListData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {

		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		List<String> incremIrnList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GETIRN inside  InwardGetIrnListDataParserImpl for batch id {} ",
					batchId);
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("API Response for ResultId {}: {}", id, apiResp);
		    }
			if (GETIRN_SUPPLY_TYPES.contains(dto.getType())) {
				incremIrnList.addAll(parseIrnJsonData(dto, apiResp,
						dto.getType(), batchId, dto.getGroupcode()));
			}

		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GETIRN InwardGetIrnListDataParserImpl parsing completed for batch id {} ",
					batchId);
		}

		return incremIrnList;

	}

	public List<String> parseIrnJsonData(Gstr1GetInvoicesReqDto dto,
			String apiResp, String type, Long batchId, String groupcode) {
		try {

			JsonObject respObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			Gson gson = new Gson();

			GetIrnJsonDtlsDto reqDto = gson.fromJson(respObject,
					GetIrnJsonDtlsDto.class);
			JsonArray dataArray = respObject.getAsJsonArray("data");
			
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Api response received is: {}", dataArray);
			}
			Map<String, GetIrnListEntity> irnListRespSts = new HashMap<>();
			List<String> incremtlIrnList = new ArrayList<>();
			List<GetIrnListEntity> irnDtls = new ArrayList<>();

			List<Long> updateIds = new ArrayList<>();
			List<Long> updateBatchIds = new ArrayList<>();
			List<GetIrnDetailPayloadEntity> entityList = new ArrayList<>();

		
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GETIRN inside  InwardGetIrnJsonDataParserImpl for batch id {},  irnListRespSts {} ",
						batchId, irnListRespSts);
			}

			

			for (GetIrnJsonDtlsRespDto irnJsonDto : reqDto.getData()) {
				
				List<GetIrnListEntity> irnListEntity = getIrnListRepo
						.findByCusGstinAndMonYear(dto.getGstin(),
								dto.getReturnPeriod());//how single gstin and return period we will get

				irnListRespSts = irnListEntity.stream()
						.collect(Collectors.toMap(
								o -> o.getIrn() + "-" + o.getIrnSts(),
								Function.identity()));

				
				
				String cusGstin = dto.getGstin();
				//String suppGstin = "Test"; // Assuming dto has suppGstin
				String month = dto.getReturnPeriod();

				convertToListingEntity(irnJsonDto, null, cusGstin, irnDtls,
						batchId, updateIds, irnListRespSts, month,
						incremtlIrnList, updateBatchIds, groupcode);
			}
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Saving {} IRN details to the repository.", irnDtls.size());
			}
			getIrnListRepo.saveAll(irnDtls);
			
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Save completed {} IRN details to the repository.", irnDtls.size());
			}

			for (JsonElement element : dataArray) { // Process only incremental
													// payloads
				JsonObject irnObj = element.getAsJsonObject();

				String irnStatusCombo = irnObj.get("Irn").getAsString() + "-"
						+ irnObj.get("Status").getAsString();
				LOGGER.debug("Generated irnStatusCombo: {}", irnStatusCombo);
				// Check if the IRN exists in incremtlIrnList
				if (incremtlIrnList.contains(irnStatusCombo)) {
					String[] irnLi = irnStatusCombo.split("-");

					if (irnLi.length > 1) {
						String irn = irnLi[0];
						String status = irnLi[1];

						String jsonString = gson.toJson(irnObj);
						Clob payload = GenUtil.convertStringToClob(jsonString);

						GetIrnDetailPayloadEntity irnDetailPayload = new GetIrnDetailPayloadEntity();
						irnDetailPayload.setIrn(irn);
						irnDetailPayload.setIrnSts(status);
						irnDetailPayload.setPayload(payload);
						irnDetailPayload.setBtchId(batchId);
						irnDetailPayload.setCreatedOn(LocalDateTime.now());

						entityList.add(irnDetailPayload);
					}
				}
			}
			irnDtlPayloadRepo.saveAll(entityList);

			if (!Collections.isEmpty(updateBatchIds)) {
				getIrnListRepo.updateBatchId(updateBatchIds, batchId);
			}

			return incremtlIrnList;
		} catch (Exception ex) {
			String msg = "Exception occured in InwardGetIrnListDataParserImpl";
			LOGGER.error(ex.getLocalizedMessage());
			throw new AppException(ex, msg);

		}
	}

	public void convertToListingEntity(GetIrnJsonDtlsRespDto irnDtlDto,
			String suppGstin, String cusGstin, List<GetIrnListEntity> irnDtls,
			Long batchId, List<Long> updateIds,
			Map<String, GetIrnListEntity> irnListRespSts, String month,
			List<String> incremtlIrnList, List<Long> updateBatchIds,
			String groupCode) {

		String irnSts = irnDtlDto.getIrn() + "-" + irnDtlDto.getStatus();
		if (irnListRespSts.containsKey(irnSts)) {

			if ("Failed".equalsIgnoreCase(
					irnListRespSts.get(irnSts).getGetIrnDetSts())
					|| "NOT_INITIATED".equalsIgnoreCase(
							irnListRespSts.get(irnSts).getGetIrnDetSts())) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GETIRN inside  InwardGetIrnListDataParserImpl failed irn {} ",
							irnDtlDto.getIrn());
				}

				incremtlIrnList
						.add(irnDtlDto.getIrn() + "-" + irnDtlDto.getStatus());
				updateBatchIds.add(irnListRespSts.get(irnSts).getId());
			}

		} else {

			entityList(irnDtlDto, suppGstin, cusGstin, irnDtls, batchId, month,
					incremtlIrnList, groupCode);
		}

	}

	public void entityList(GetIrnJsonDtlsRespDto irnDtlDto, String suppGstin,
			String cusGstin, List<GetIrnListEntity> irnDtls, Long batchId,
			String month, List<String> incremtlIrnList, String groupCode) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"inside try block start -->InwardGetIrnJsonDataParserImpl entityListmethod  for batch id {}  "
								+ " and irn {}",
						batchId, irnDtlDto.getIrn());
			}
			incremtlIrnList
					.add(irnDtlDto.getIrn() + "-" + irnDtlDto.getStatus());

			GetIrnListEntity dto = new GetIrnListEntity();
			dto.setCusGstin(cusGstin);
			dto.setSuppGstin(suppGstin);
			dto.setAckNo(
					irnDtlDto.getAckNo() != null ? irnDtlDto.getAckNo() : null);
			dto.setAckDate(!Strings.isNullOrEmpty(irnDtlDto.getAckDt())
					? LocalDateTime.parse(irnDtlDto.getAckDt(), formatter)
					: null);
			dto.setIrn(irnDtlDto.getIrn() != null ? irnDtlDto.getIrn() : null);
			// signed invoice
			// signed qr code
			dto.setSignedInvoice(irnDtlDto.getSignedInvoice() != null
					? irnDtlDto.getSignedInvoice()
					: null);
			dto.setSignedQRCode(irnDtlDto.getSignedQRCode() != null
					? irnDtlDto.getSignedQRCode()
					: null);

			dto.setIrnSts(irnDtlDto.getStatus() != null
					? irnDtlDto.getStatus()
					: null);

			dto.setEwbNum(irnDtlDto.getEwbNo() != null
					? String.valueOf(irnDtlDto.getEwbNo())
					: null);
			dto.setEwbDate(!Strings.isNullOrEmpty(irnDtlDto.getEwbDt())
					? LocalDateTime.parse(irnDtlDto.getEwbDt(), formatter)
					: null);
			dto.setEwbValidTill(
					!Strings.isNullOrEmpty(irnDtlDto.getEwbValidTill())
							? LocalDateTime.parse(irnDtlDto.getEwbValidTill(),
									formatter)
							: null);
			dto.setRemarks(irnDtlDto.getRemarks() != null
					? irnDtlDto.getRemarks()
					: null);
			dto.setCanDate(!Strings.isNullOrEmpty(irnDtlDto.getCnldt())
					? LocalDateTime.parse(irnDtlDto.getCnldt(), formatter)
					: null);
			dto.setCancellationReason(irnDtlDto.getCnlRsn() != null
					? irnDtlDto.getCnlRsn()
					: null);

			dto.setCancellationRem(irnDtlDto.getCnlRem() != null
					? irnDtlDto.getCnlRem()
					: null);
			dto.setBatchId(batchId);
			dto.setMonYear(month);
			dto.setGetIrnDetSts("NOT_INITIATED");
			dto.setDerivdMonYear(month.substring(2, 6) + month.substring(0, 2));
			dto.setCreatedBy(groupCode);
			dto.setCreatedOn(LocalDateTime.now());
			dto.setGetIrnDetIniOn(LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"inside try block end -->InwardGetIrnJsonDataParserImpl entityListmethod  for batch id {}  "
								+ " and irn {}",
						batchId, irnDtlDto.getIrn());
			}
			irnDtls.add(dto);
		}

		catch (Exception e) {
			LOGGER.error(
					"Exception in InwardGetIrnJsonDataParserImpl entityList method for batch id {} and irn {}",
					batchId, irnDtlDto.getIrn(), e);
		}

	}
}
