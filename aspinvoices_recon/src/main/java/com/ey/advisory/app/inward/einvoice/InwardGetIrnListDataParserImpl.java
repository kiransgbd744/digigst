package com.ey.advisory.app.inward.einvoice;

import java.time.LocalDate;
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

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("InwardGetIrnListDataParserImpl")
@Transactional(value = "clientTransactionManager")
public class InwardGetIrnListDataParserImpl
		implements InwardGetIrnListDataParser {

	@Autowired
	private GetIrnListingRepository getIrnListingRepo;

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
			if (GETIRN_SUPPLY_TYPES.contains(dto.getType())) {
				incremIrnList.addAll(parseIrnListData(dto, apiResp,
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

	public List<String> parseIrnListData(Gstr1GetInvoicesReqDto dto,
			String apiResp, String type, Long batchId, String groupcode) {
		try {

			JsonObject respObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			Gson gson = new Gson();

			GetIrnListDtlsDto reqDto = gson.fromJson(respObject,
					GetIrnListDtlsDto.class);

			Map<String, GetIrnListEntity> irnListRespSts = new HashMap<>();

			List<GetIrnListEntity> irnListEntity = getIrnListingRepo
					.findByCusGstinAndMonYear(dto.getGstin(),
							dto.getReturnPeriod());
			
			irnListRespSts = irnListEntity.stream()
					.collect(Collectors.toMap(
							o -> o.getIrn() + "-" + o.getIrnSts(),
							Function.identity()));
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GETIRN inside  InwardGetIrnListDataParserImpl for batch id {},  irnListRespSts {} ",
						batchId, irnListRespSts);
			}
			
			List<String> incremtlIrnList = new ArrayList<>();
			List<GetIrnListEntity> irnDtls = new ArrayList<>();

			List<Long> updateIds = new ArrayList<>();
			List<Long> updateBatchIds = new ArrayList<>();

			for (GetIrnCtinListDtlsDto irnListDto : reqDto.getIrnList()) {
				String cusGstin = dto.getGstin();
				String suppGstin = irnListDto.getSuppGstin();
				String month = dto.getReturnPeriod();
				for (GetIrnDtlsRespDto irnDtlDto : irnListDto.getIrnDtl()) {
					convertToListingEntity(irnDtlDto, suppGstin, cusGstin,
							irnDtls, batchId, updateIds, irnListRespSts, month,
							incremtlIrnList, updateBatchIds, groupcode);
				}
			}
			getIrnListingRepo.saveAll(irnDtls);
			
			if(!Collections.isEmpty(updateBatchIds))
			{
				getIrnListingRepo.updateBatchId(updateBatchIds, batchId);
			}
			
			return incremtlIrnList;

		} catch (Exception ex) {
			String msg = "Exception occured in InwardGetIrnListDataParserImpl";
			LOGGER.error(ex.getLocalizedMessage());
			throw new AppException(ex,msg);

		}
	}

	public void convertToListingEntity(GetIrnDtlsRespDto irnDtlDto,
			String suppGstin, String cusGstin, List<GetIrnListEntity> irnDtls,
			Long batchId, List<Long> updateIds,
			Map<String, GetIrnListEntity> irnListRespSts, String month,
			List<String> incremtlIrnList, List<Long> updateBatchIds, String groupCode) {
		String irnSts = irnDtlDto.getIrn()+"-"+
				irnDtlDto.getIrnSts();
		if (irnListRespSts.containsKey(irnSts)) {

			if ("Failed".equalsIgnoreCase(
					irnListRespSts.get(irnSts).getGetIrnDetSts())|| "NOT_INITIATED".equalsIgnoreCase(
							irnListRespSts.get(irnSts).getGetIrnDetSts()))
			{	
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GETIRN inside  InwardGetIrnListDataParserImpl failed irn {} ",
							irnDtlDto.getIrn());
				}
			
				
				incremtlIrnList.add(irnDtlDto.getIrn()+"-"+irnDtlDto.getIrnSts());
				updateBatchIds.add(irnListRespSts.get(irnSts).getId());
			}

		} else {

			entityList(irnDtlDto, suppGstin, cusGstin, irnDtls, batchId, month,
					incremtlIrnList, groupCode);
			}

	}

	public void entityList(GetIrnDtlsRespDto irnDtlDto, String suppGstin,
			String cusGstin, List<GetIrnListEntity> irnDtls, Long batchId,
			String month, List<String> incremtlIrnList, String groupCode) {

		// saving incremental irns
		incremtlIrnList.add(irnDtlDto.getIrn()+"-"+irnDtlDto.getIrnSts());

		GetIrnListEntity dto = new GetIrnListEntity();
		dto.setCusGstin(cusGstin);
		dto.setSuppGstin(suppGstin);
		dto.setDocNum(
				irnDtlDto.getDocNum() != null ? irnDtlDto.getDocNum() : null);
		dto.setDocDate(!Strings.isNullOrEmpty(irnDtlDto.getDocDt()) ? LocalDate
				.parse(irnDtlDto.getDocDt(), formatter1).atStartOfDay() : null);
		String docType= irnDtlDto.getDocTyp();
		if("CRN".equalsIgnoreCase(docType)){
			docType="CR";
		}else if("DBN".equalsIgnoreCase(docType)){
			docType="DR";
		}
		dto.setDocTyp(
				docType != null ? docType : null);
		
		String suppTyp = irnDtlDto.getSuppTyp();
		if("DEXP".equalsIgnoreCase(suppTyp)){
			suppTyp="DXP";
		}
		dto.setSuppType(
				suppTyp != null ? suppTyp : null);
		dto.setTotInvAmt(irnDtlDto.getTotInvAmt()!=null?irnDtlDto.getTotInvAmt():null);
		dto.setIrn(irnDtlDto.getIrn() != null ? irnDtlDto.getIrn() : null);
		dto.setIrnSts(
				irnDtlDto.getIrnSts() != null ? irnDtlDto.getIrnSts() : null);
		dto.setAckNo(
				irnDtlDto.getAckNo() != null ? irnDtlDto.getAckNo() : null);
		dto.setAckDate(!Strings.isNullOrEmpty(irnDtlDto.getAckDt())
				? LocalDateTime.parse(irnDtlDto.getAckDt(), formatter) : null);
		dto.setEwbNum(irnDtlDto.getEwbNo() != null
				? String.valueOf(irnDtlDto.getEwbNo()) : null);
		dto.setEwbDate(!Strings.isNullOrEmpty(irnDtlDto.getEwbDt())
				? LocalDateTime.parse(irnDtlDto.getEwbDt(), formatter) : null);
		dto.setCanDate(!Strings.isNullOrEmpty(irnDtlDto.getCnlDt())
				? LocalDateTime.parse(irnDtlDto.getCnlDt(), formatter) : null);
		dto.setBatchId(batchId);
		dto.setMonYear(month);
		dto.setGetIrnDetSts("NOT_INITIATED");
		dto.setDerivdMonYear(month.substring(2, 6) + month.substring(0, 2));
		dto.setCreatedBy(groupCode);
		dto.setCreatedOn(LocalDateTime.now());
		dto.setGetIrnDetIniOn(LocalDateTime.now());
		irnDtls.add(dto);

	}
}
