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
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.BCAPIOutwardPayloadErrorItemDto;
import com.ey.advisory.app.docs.dto.erp.OutwardDocErrorsDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorItemDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;
import com.ey.advisory.core.async.repositories.master.MasterErrorCatalogEntityRepository;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("BCAPIPaylodRevereseFeedServiceImpl")
public class BCAPIPaylodRevereseFeedServiceImpl
		implements BCAPIPaylodRevereseFeedService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailsRepo;

	@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	private MasterErrorCatalogEntityRepository errRepo;

	private static final String OUTWARD = "OUTWARD";
	
	@Autowired
	private CommonUtility commonUtility;

	public List<BCAPIOutwardPayloadErrorItemDto> getObjectPayload(
			String payloadId) {
		String type="OUTWARD";
		List<BCAPIOutwardPayloadErrorItemDto> itemDtos = new ArrayList<>();
		List<Object[]> objs = docRepository
				.bcApiErrorDocsForRevIntegrationByPayloadId(payloadId);
		objs.forEach(obj -> {
			BCAPIOutwardPayloadErrorItemDto itemDto = new BCAPIOutwardPayloadErrorItemDto();
			Long id = obj[0] != null ? new Long(String.valueOf(obj[0])) : null;
			String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
			String accVoucherDate = obj[19] != null ? String.valueOf(obj[19]) : null;
			List<Object[]> objects = gstnDetailsRepo
					.getEntityNameAndEntityPan(gstin);
			objects.forEach(object -> {
				itemDto.setEntityName(
						object[0] != null ? String.valueOf(object[0]) : null);
				itemDto.setEntityPan(
						object[1] != null ? String.valueOf(object[1]) : null);
				itemDto.setEntityId(object[3] != null
						? String.valueOf(object[3]) : null);
			});
			String docDate = obj[4] != null ? String.valueOf(obj[4]) : null;
			itemDto.setSupplierGstin(
					obj[1] != null ? String.valueOf(obj[1]) : null);
			itemDto.setDocNo(obj[2] != null ? String.valueOf(obj[2]) : null);
			itemDto.setDocType(obj[3] != null ? String.valueOf(obj[3]) : null);
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
			String errorCode = obj[15] != null ? String.valueOf(obj[15]) : null;
			if (errorCode != null && !errorCode.trim().isEmpty()) {
				String[] erCodes = errorCode.split(",");
				errorCodeList = Arrays.asList(erCodes);
			}
			OutwardDocErrorsDto outwardDocErrorsDto = new OutwardDocErrorsDto();
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
					errorCodeDescs = errRepo.errorDescsForErrorCodeAndTableType(
							errorCodeList, OUTWARD);
					if (errorCodeDescs != null && !errorCodeDescs.isEmpty()) {
						Map<String, String> orgDocKeyMap = new HashMap<>();

						errorCodeDescs.forEach(errorCodeDesc -> {
							orgDocKeyMap.put(errorCodeDesc.getErrorCode(),
									errorCodeDesc.getErrorDesc());
						});

						errorCodeList.forEach(itemErrorCode -> {
							String erroDesc = orgDocKeyMap.get(itemErrorCode);
							OutwardErrorItemDto child = setOutwardItemDetail(
									itemErrorObj, itemErrorCode, erroDesc);
							items.add(child);
						});
					}
				}
			}
			outwardDocErrorsDto.setErrors(items);
			itemDto.setOutwardDocErrorsDto(outwardDocErrorsDto);
			itemDtos.add(itemDto);
		});
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
	
}
