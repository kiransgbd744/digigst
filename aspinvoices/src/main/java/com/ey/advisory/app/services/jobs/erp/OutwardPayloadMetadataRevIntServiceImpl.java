package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.InwardPayloadEntity;
import com.ey.advisory.app.data.entities.client.OutwardPayloadEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.InwardPayloadRepository;
import com.ey.advisory.app.data.repositories.client.OutwardPayloadRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorItemDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorItemsDto;
import com.ey.advisory.app.docs.dto.erp.OutwardPayloadErrorItemDto;
import com.ey.advisory.app.docs.dto.erp.PayloadBusMesgDto;
import com.ey.advisory.app.docs.dto.erp.PayloadErrorInfoMesgDto;
import com.ey.advisory.app.docs.dto.erp.PayloadErrorInfoMesgItemDto;
import com.ey.advisory.app.docs.dto.erp.PayloadMetaDataDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;
import com.ey.advisory.core.async.repositories.master.MasterErrorCatalogEntityRepository;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Service("OutwardPayloadMetadataRevIntServiceImpl")
public class OutwardPayloadMetadataRevIntServiceImpl
		implements OutwardPayloadMetadataRevIntService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("OutwardPayloadRepository")
	private OutwardPayloadRepository outwardPayloadRepository;

	@Autowired
	@Qualifier("InwardPayloadRepository")
	private InwardPayloadRepository inwardPayloadRepo;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepo;

	@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	private MasterErrorCatalogEntityRepository errRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailsRepo;

	@Autowired
	private CommonUtility commonUtility;

/*	@Autowired
	@Qualifier("EntityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;*/

	private static final String OUTWARD = "OUTWARD";
	private static final String INWARD = "INWARD";

	public PayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId) {

		PayloadMetaDataDto metaDataDto = new PayloadMetaDataDto();

		PayloadBusMesgDto busMsgDto = getPayloadBusMesg(type, payloadId);
		metaDataDto.setDto(busMsgDto);

		List<OutwardPayloadErrorItemDto> outwardPayloadItemDtos = getObjectPayload(
				type, payloadId);

		if (outwardPayloadItemDtos == null
				|| outwardPayloadItemDtos.isEmpty()) {
			return metaDataDto;
		}
		List<PayloadErrorInfoMesgItemDto> itemDtos = new ArrayList<>();
		for (OutwardPayloadErrorItemDto outwardPayloadItemDto : outwardPayloadItemDtos) {
			PayloadErrorInfoMesgItemDto itemDto = new PayloadErrorInfoMesgItemDto();
			itemDto.setEntityName(outwardPayloadItemDto.getEntityName());
			itemDto.setEntityPan(outwardPayloadItemDto.getEntityPan());
			itemDto.setCompanycode(outwardPayloadItemDto.getCompanycode());
			itemDto.setAccVcherNo(outwardPayloadItemDto.getAccVcherNo());
			itemDto.setDocDate(outwardPayloadItemDto.getDocDate());
			itemDto.setDocNo(outwardPayloadItemDto.getDocNo());
			itemDto.setDocType(outwardPayloadItemDto.getDocType());
			itemDto.setFiscalyear(outwardPayloadItemDto.getFiscalyear());
			itemDto.setId(outwardPayloadItemDto.getId());
			itemDto.setPayloadId(outwardPayloadItemDto.getPayloadId());
			itemDto.setReceivedDate(outwardPayloadItemDto.getReceivedDate());
			itemDto.setSupplierGstin(outwardPayloadItemDto.getSupplierGstin());
			itemDto.setErrors(outwardPayloadItemDto.getErrors());
			itemDtos.add(itemDto);
		}
		PayloadErrorInfoMesgDto msgDto = new PayloadErrorInfoMesgDto();
		msgDto.setItems(itemDtos);
		metaDataDto.setErrorMsgDto(msgDto);

		return metaDataDto;
	}

	private PayloadBusMesgDto getPayloadBusMesg(String type, String payloadId) {
		PayloadBusMesgDto msgDto = new PayloadBusMesgDto();

		if (OUTWARD.equalsIgnoreCase(type)) {
			OutwardPayloadEntity entity = outwardPayloadRepository
					.getOutwardPayload(payloadId);

			if (entity != null) {
				msgDto.setErrorCount(entity.getErrorCount() != null
						? entity.getErrorCount() : 0);
				msgDto.setModifiedOn(EYDateUtil
						.toISTDateTimeFromUTC(entity.getModifiedOn()));
				msgDto.setPayloadId(entity.getPayloadId());
				msgDto.setProcessCount((entity.getTotalCount() != null
						? entity.getTotalCount() : 0)
						- (entity.getErrorCount() != null
								? entity.getErrorCount() : 0));
				msgDto.setStatus(entity.getStatus());
				msgDto.setTotalCount(entity.getTotalCount());
				if (entity.getStatus().equalsIgnoreCase(APIConstants.P)
						|| entity.getStatus()
								.equalsIgnoreCase(APIConstants.PE)) {
					LOGGER.debug("MessageInfo Not Req");
				} else {
					msgDto.setMessageInfo(entity.getJsonErrorResponse());
				}
				msgDto.setPushType(entity.getPushType());
			}
		} else if (INWARD.equalsIgnoreCase(type)) {
			InwardPayloadEntity entity = inwardPayloadRepo
					.getInwardPayload(payloadId);
			if (entity != null) {
				msgDto.setErrorCount(entity.getErrorCount() != null
						? entity.getErrorCount() : 0);
				msgDto.setModifiedOn(EYDateUtil
						.toISTDateTimeFromUTC(entity.getModifiedOn()));
				msgDto.setPayloadId(entity.getPayloadId());
				msgDto.setProcessCount((entity.getTotalCount() != null
						? entity.getTotalCount() : 0)
						- (entity.getErrorCount() != null
								? entity.getErrorCount() : 0));
				msgDto.setStatus(entity.getStatus());
				msgDto.setTotalCount(entity.getTotalCount());
				if (entity.getStatus().equalsIgnoreCase(APIConstants.P)
						|| entity.getStatus()
								.equalsIgnoreCase(APIConstants.PE)) {
					LOGGER.debug("MessageInfo Not Req");
				} else {
					msgDto.setMessageInfo(entity.getJsonErrorResponse());
				}
				msgDto.setPushType(entity.getPushType());
			}
		}
		return msgDto;
	}

	private List<OutwardPayloadErrorItemDto> getObjectPayload(String type,
			String payloadId) {
		List<OutwardPayloadErrorItemDto> itemDtos = new ArrayList<>();
		if (OUTWARD.equalsIgnoreCase(type)) {
			List<Object[]> objs = docRepository
					.aspErrorDocsForRevIntegrationByPayloadId(payloadId);
			objs.forEach(obj -> {
				OutwardPayloadErrorItemDto itemDto = new OutwardPayloadErrorItemDto();
				String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
				String docDate = obj[4] != null ? String.valueOf(obj[4]) : null;
				String accVoucherDate = obj[11] != null ? String.valueOf(obj[11]) : null;
				List<Object[]> objects = gstnDetailsRepo
						.getEntityNameAndEntityPan(gstin);
				objects.forEach(object -> {
					itemDto.setEntityName(object[0] != null
							? String.valueOf(object[0]) : null);
					itemDto.setEntityPan(object[1] != null
							? String.valueOf(object[1]) : null);
					itemDto.setEntityId(object[3] != null
							? String.valueOf(object[3]) : null);
				});
				Long id = obj[0] != null ? new Long(String.valueOf(obj[0]))
						: null;
				itemDto.setId(obj[0] != null ? String.valueOf(obj[0]) : null);
				itemDto.setSupplierGstin(
						obj[1] != null ? String.valueOf(obj[1]) : null);
				itemDto.setDocNo(
						obj[2] != null ? String.valueOf(obj[2]) : null);
				itemDto.setDocType(
						obj[3] != null ? String.valueOf(obj[3]) : null);
				long entityid = Long.parseLong(itemDto.getEntityId());
				String ansfromques = commonUtility.getAnsFromQue1(entityid,
						"Fiscal Year to be computed basis ?",type);
				if(ansfromques==null)
				{
					ansfromques="A";
				}
				
				if ("B".equalsIgnoreCase(ansfromques)) {
					docDate=accVoucherDate;
				}
								
				itemDto.setDocDate(docDate);
				itemDto.setCompanycode(
						obj[8] != null ? String.valueOf(obj[8]) : null);
				itemDto.setFiscalyear(commonUtility.setFiscalYear(gstin, docDate,type));
				itemDto.setAccVcherNo(
						obj[10] != null ? String.valueOf(obj[10]) : null);
				itemDto.setPayloadId(
						obj[11] != null ? String.valueOf(obj[11]) : null);
				itemDto.setReceivedDate(
						obj[12] != null ? String.valueOf(obj[12]) : null);

				List<String> errorCodeList = new ArrayList<>();
				String errorCode = obj[15] != null ? String.valueOf(obj[15])
						: null;
				if (errorCode != null && !errorCode.trim().isEmpty()) {
					String[] erCodes = errorCode.split(",");
					errorCodeList = Arrays.asList(erCodes);
				}

				List<OutwardErrorItemDto> items = new ArrayList<>();
				List<MasterErrorCatalogEntity> errorCodeDescs = new ArrayList<>();
				if (errorCodeList != null && !errorCodeList.isEmpty()) {
					errorCodeDescs = errRepo.errorDescsForErrorCodeAndTableType(
							errorCodeList, OUTWARD);
					if (errorCodeDescs != null && !errorCodeDescs.isEmpty()) {
						Map<String, String> orgDocKeyMap = new HashMap<>();

						orgDocKeyMap = errorCodeDescs.stream()
								.collect(Collectors.toMap(
										MasterErrorCatalogEntity::getErrorCode,
										MasterErrorCatalogEntity::getErrorDesc));

						for (String itemErrorCode : errorCodeList) {
							String erroDesc = orgDocKeyMap.get(itemErrorCode);
							OutwardErrorItemDto child = setOutwardHeaderDetail(
									itemErrorCode, erroDesc);
							items.add(child);
						}
					}
				}

				List<Object[]> itemErrorObjs = docRepository
						.aspErrorDocItemForDocHeaderId(id);
				for (Object[] itemErrorObj : itemErrorObjs) {
					String itemErrCode = itemErrorObj[1] != null
							? String.valueOf(itemErrorObj[1]) : null;

					if (itemErrCode != null && !itemErrCode.trim().isEmpty()) {
						String[] itemErrCodes = itemErrCode.split(",");
						errorCodeList = Arrays.asList(itemErrCodes);
					}

					if (errorCodeList != null && !errorCodeList.isEmpty()) {
						errorCodeDescs = errRepo
								.errorDescsForErrorCodeAndTableType(
										errorCodeList, OUTWARD);
						if (errorCodeDescs != null
								&& !errorCodeDescs.isEmpty()) {
							Map<String, String> orgDocKeyMap = new HashMap<>();

							errorCodeDescs.forEach(errorCodeDesc -> {
								orgDocKeyMap.put(errorCodeDesc.getErrorCode(),
										errorCodeDesc.getErrorDesc());
							});

							errorCodeList.forEach(itemErrorCode -> {
								String erroDesc = orgDocKeyMap
										.get(itemErrorCode);
								OutwardErrorItemDto child = setOutwardItemDetail(
										itemErrorObj, itemErrorCode, erroDesc);
								items.add(child);
							});
						}
					}
				}
				OutwardErrorItemsDto errorItemsDto = new OutwardErrorItemsDto();
				errorItemsDto.setErrors(items);
				itemDto.setErrors(errorItemsDto);
				itemDtos.add(itemDto);
			});
		} else if (INWARD.equalsIgnoreCase(type)) {

			List<Object[]> objs = inwardTransDocRepo
					.aspErrorDocsForRevIntegrationByPayloadId(payloadId);
			InwardPayloadEntity inwardEntity = inwardPayloadRepo
					.getInwardPayload(payloadId);

			objs.forEach(obj -> {
				String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
				String docDate = obj[4] != null ? String.valueOf(obj[4]) : null;
				String accVoucherDate = obj[17] != null ? String.valueOf(obj[17]) : null;
				OutwardPayloadErrorItemDto itemDto = new OutwardPayloadErrorItemDto();
				List<Object[]> objects = gstnDetailsRepo
						.getEntityNameAndEntityPan(gstin);
				objects.forEach(object -> {
					itemDto.setEntityName(object[0] != null
							? String.valueOf(object[0]) : null);
					itemDto.setEntityPan(object[1] != null
							? String.valueOf(object[1]) : null);
					itemDto.setEntityId(object[3] != null
							? String.valueOf(object[3]) : null);
					
				});
				Long id = obj[0] != null ? new Long(String.valueOf(obj[0]))
						: null;
				itemDto.setId(obj[0] != null ? String.valueOf(obj[0]) : null);
				itemDto.setSupplierGstin(
						obj[1] != null ? String.valueOf(obj[1]) : null);
				itemDto.setDocNo(
						obj[2] != null ? String.valueOf(obj[2]) : null);
				itemDto.setDocType(
						obj[3] != null ? String.valueOf(obj[3]) : null);
				long entityid = Long.parseLong(itemDto.getEntityId());
				String ansfromques = commonUtility.getAnsFromQue1(entityid,
						"Fiscal Year to be computed basis ?",type);
				if(ansfromques==null)
				{
					ansfromques="A";
				}
				
				if ("B".equalsIgnoreCase(ansfromques)) {
					docDate=accVoucherDate;
				}
				itemDto.setDocDate(docDate);
				itemDto.setFiscalyear(commonUtility.setFiscalYear(gstin, docDate,type));
				itemDto.setPayloadId(
						obj[10] != null ? String.valueOf(obj[10]) : null);

				itemDto.setReceivedDate(
						obj[11] != null ? String.valueOf(obj[11]) : null);
				itemDto.setAccVcherNo(
						obj[12] != null ? String.valueOf(obj[12]) : null);
				if (inwardEntity != null) {
					itemDto.setCompanycode(inwardEntity.getCompanyCode());
				}
				List<String> errorCodeList = new ArrayList<>();
				String errorCode = obj[14] != null ? String.valueOf(obj[14])
						: null;

				if (errorCode != null && !errorCode.trim().isEmpty()) {
					String[] erCodes = errorCode.split(",");
					errorCodeList = Arrays.asList(erCodes);
				}

				List<OutwardErrorItemDto> items = new ArrayList<>();
				if (errorCodeList != null && !errorCodeList.isEmpty()) {
					List<MasterErrorCatalogEntity> errorCodeDescs = errRepo
							.errorDescsForErrorCodeAndTableType(errorCodeList,
									INWARD);
					if (errorCodeDescs != null && !errorCodeDescs.isEmpty()) {
						Map<String, String> orgDocKeyMap = new HashMap<>();
						orgDocKeyMap = errorCodeDescs.stream()
								.collect(Collectors.toMap(
										MasterErrorCatalogEntity::getErrorCode,
										MasterErrorCatalogEntity::getErrorDesc));

						for (String itemErrorCode : errorCodeList) {
							String erroDesc = orgDocKeyMap.get(itemErrorCode);
							OutwardErrorItemDto child = setOutwardHeaderDetail(
									itemErrorCode, erroDesc);
							items.add(child);
						}
					}
				}

				List<Object[]> itemErrorObjs = inwardTransDocRepo
						.aspErrorDocItemForDocHeaderIdFromInward(id);
				for (Object[] itemErrorObj : itemErrorObjs) {
					String itemErrCode = itemErrorObj[1] != null
							? String.valueOf(itemErrorObj[1]) : null;
					if (itemErrCode != null && !itemErrCode.trim().isEmpty()) {
						String[] itemErrCodes = itemErrCode.split(",");
						errorCodeList = Arrays.asList(itemErrCodes);
					}
					if (errorCodeList != null && !errorCodeList.isEmpty()) {
						List<MasterErrorCatalogEntity> errorCodeDescs = errRepo
								.errorDescsForErrorCodeAndTableType(
										errorCodeList, INWARD);
						if (errorCodeDescs != null
								&& !errorCodeDescs.isEmpty()) {
							Map<String, String> orgDocKeyMap = new HashMap<>();
							errorCodeDescs.forEach(errorCodeDesc -> {
								orgDocKeyMap.put(errorCodeDesc.getErrorCode(),
										errorCodeDesc.getErrorDesc());
							});
							errorCodeList.forEach(itemErrorCode -> {
								String erroDesc = orgDocKeyMap
										.get(itemErrorCode);
								OutwardErrorItemDto child = setOutwardItemDetail(
										itemErrorObj, itemErrorCode, erroDesc);
								items.add(child);
							});
						}
					}
				}
				OutwardErrorItemsDto errors = new OutwardErrorItemsDto();
				errors.setErrors(items);
				itemDto.setErrors(errors);
				itemDtos.add(itemDto);
			});
		}
		return itemDtos;
	}

	private OutwardErrorItemDto setOutwardHeaderDetail(String errorCode,
			String errDesc) {

		OutwardErrorItemDto itm = new OutwardErrorItemDto();
		itm.setErrorCode(errorCode);
		itm.setErrorDesc(errDesc);
		return itm;
	}

	private OutwardErrorItemDto setOutwardItemDetail(Object[] arr,
			String errorCode, String errDesc) {

		OutwardErrorItemDto itm = new OutwardErrorItemDto();
		itm.setItemNo(
				arr[0] != null ? Long.parseLong(String.valueOf(arr[0])) : null);
		itm.setErrorCode(errorCode);
		itm.setErrorDesc(errDesc);
		return itm;
	}
	
	
	


	/*private String setFiscalYear(String gstin, String docDate) {
		String finYear = null;
		try {
			if (!Strings.isNullOrEmpty(gstin)
					&& !Strings.isNullOrEmpty(docDate)) {
				GSTNDetailEntity gstnDetailEntity = gstnDetailsRepo
						.findByGstinAndIsDeleteFalse(gstin);
				String ansfromques = commonUtility.getAnsFromQue(
						gstnDetailEntity.getEntityId(),
						"Fiscal year to be followed in ERP ?");
				LocalDate docDateLoc = LocalDate.parse(docDate);
				if (ansfromques.equalsIgnoreCase("A")) {
					finYear = GenUtil.getFinYearJanToDec(docDateLoc);
				}
				if (ansfromques.equalsIgnoreCase("B")) {
					finYear = GenUtil.getFinYear(docDateLoc);
				}
					if (ansfromques.equalsIgnoreCase("C")) {
						finYear = GenUtil.getFinYearJulToJune(docDateLoc);
					}
				
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected error while retriving fiscal year",
					e);
		}

		return !Strings.isNullOrEmpty(finYear) ? finYear.substring(0, 4) : null;
	}*/
}
