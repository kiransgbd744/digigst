package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.asprecon.ImsGstnCountRepository;
import com.ey.advisory.app.service.ims.ImsGstnCountEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("ImsCountDataParserImpl")
@Transactional(value = "clientTransactionManager")
public class ImsCountDataParserImpl implements ImsCountDataParser {

	@Autowired
	private ImsGstnCountRepository imsGstnCountRepository;

	private static final List<String> GETIMS_GOODS_TYPES = ImmutableList
			.copyOf(Arrays.asList(APIConstants.IMS_COUNT_TYPE_ALL_OTH,
					APIConstants.IMS_COUNT_TYPE_INV_SUPP_ISD,
					APIConstants.IMS_COUNT_TYPE_IMP_GDS));
	@Override
	public void parseImsCountData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId, String jsonString) {
		try{
			
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET IMS Count inside  ImsCountDataParserImpl for batch id {} ",
					batchId);
		}
		
		if(Strings.isNullOrEmpty(jsonString))
		{
	
			String apiResp = null;
			if(Strings.isNullOrEmpty(jsonString))
			{
			apiResp = APIInvokerUtil.getResultById(resultIds.get(0));
			
			}else
			{
				apiResp = jsonString;
			}
			
		
				JsonObject respObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				Gson gson = new Gson();

				GetImsCountGoodsTypeDtlsDto reqDto = gson.fromJson(respObject,
						GetImsCountGoodsTypeDtlsDto.class);
				
				List<ImsGstnCountEntity> countEntity = parseImsCountsData(dto,
						reqDto, dto.getType(), batchId, dto.getGroupcode());
				
				imsGstnCountRepository.setDeleteTrueByGstinAndType(dto.getGstin(),dto.getType());
				
				imsGstnCountRepository.saveAll(countEntity);
		
		}else
		{
			String apiResp = null;
				apiResp = jsonString;
			
			if (GETIMS_GOODS_TYPES.contains(dto.getType())) {

				JsonObject respObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				Gson gson = new Gson();

				GetImsCountGoodsTypeDtlsDto reqDto = gson.fromJson(respObject,
						GetImsCountGoodsTypeDtlsDto.class);
				
				List<ImsGstnCountEntity> countEntity = parseImsCountsData(dto,
						reqDto, dto.getType(), batchId, dto.getGroupcode());
				
				imsGstnCountRepository.setDeleteTrueByGstinAndType(dto.getGstin(),dto.getType());
				
				imsGstnCountRepository.saveAll(countEntity);
			}

		}
		}catch(Exception ex)
		{
			LOGGER.error(" exception while parsing ims count api {} ",ex);
			throw new AppException(ex);
		}
	}

	private List<ImsGstnCountEntity> parseImsCountsData(
			Gstr1GetInvoicesReqDto dto,
			GetImsCountGoodsTypeDtlsDto sectionDtlDto, String type,
			Long batchId, String groupcode) {

		List<ImsGstnCountEntity> gstinCountList = new ArrayList<>();

		if (sectionDtlDto.getAllOther() != null) {
			List<ImsGstnCountEntity> entity = createEntityFromDto(
					sectionDtlDto.getAllOther(), type, batchId, groupcode, dto);
			gstinCountList.addAll(entity);
		}
		if (sectionDtlDto.getInvSuppIsd() != null) {
			List<ImsGstnCountEntity> entity = createEntityFromDto(
					sectionDtlDto.getInvSuppIsd(), type, batchId, groupcode,
					dto);
			gstinCountList.addAll(entity);
		}

		if (sectionDtlDto.getImpGds() != null) {
			List<ImsGstnCountEntity> entity = createEntityFromDto(
					sectionDtlDto.getImpGds(), type, batchId, groupcode, dto);
			gstinCountList.addAll(entity);
		}
		return gstinCountList;
	}

	private static List<ImsGstnCountEntity> createEntityFromDto(
			GetImsCountDtlsDto countDtls, String type, Long batchId,
			String groupcode, Gstr1GetInvoicesReqDto dto) {

		List<ImsGstnCountEntity> entitylist = new ArrayList<>();

		if (countDtls.getB2bcn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("CN",countDtls.getB2bcn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}

		//need for all the countdtls variables
		if (countDtls.getB2bdn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("DN",countDtls.getB2bdn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2ba() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("B2BA",countDtls.getB2ba(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2b() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("B2B",countDtls.getB2b(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getEcom() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ECOM",countDtls.getEcom(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getEcoma() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ECOMA",countDtls.getEcoma(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2bdna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("DNA",countDtls.getB2bdna(), entity, type, batchId,
					groupcode, dto);
			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2bcna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("CNA",countDtls.getB2bcna(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsd() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISD",countDtls.getIsd(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsda() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDA",countDtls.getIsda(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsdcn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDCN",countDtls.getIsdcn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsdcna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDCNA",countDtls.getIsdcna(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpg() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPG",countDtls.getImpg(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpga() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGA",countDtls.getImpga(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpgsez() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGSEZ",countDtls.getImpgsez(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpgseza() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGSEZA",countDtls.getImpgseza(), entity, type, batchId,
					groupcode, dto);
			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}


		return entitylist;
	}

	private static  void mapSectionToEntity(String sectionName, GetImsCountSectionDtlsDto section,
			ImsGstnCountEntity entity, String type, Long batchId,
			String groupcode, Gstr1GetInvoicesReqDto dto) {
		// need to check null of all the attributes at first place with or
		// condition and then set the values
		if(section.getAccept()!=null || section.getNoAction()!=null || section.getPending()!=null || section.getReject()!=null) {
			entity.setGstin(dto.getGstin());
			entity.setGoodsType(type);
			entity.setSection(sectionName);
			entity.setIsDelete(false);
			entity.setBatchId(batchId);
			entity.setCreatedBy(groupcode);
			entity.setCreatedOn(LocalDateTime.now());

			entity.setGstnNoAction(section.getNoAction() != null
					? Integer.parseInt(section.getNoAction()) : 0);
			// complete all other gstn attributes
			entity.setGstnAccepted(section.getAccept() != null
					? Integer.parseInt(section.getAccept()) : 0);
			entity.setGstnPending(section.getPending() != null
					? Integer.parseInt(section.getPending()) : 0);
			entity.setGstnRejected(section.getReject() != null
					? Integer.parseInt(section.getReject()) : 0);
			//entity set total as the sum of all the above attributes
			entity.setGstnTotal(entity.getGstnNoAction() + entity.getGstnAccepted()
					+ entity.getGstnPending() + entity.getGstnRejected());
			entity.setGstin(dto.getGstin());
		}else
		{
			return;
		}

		
	}

public static void main(String[] args) {
		
		String apiResp = "{\n  \"all_oth\": {\n    \"b2bcn\": {\n      \"noaction\": 12,\n      \"reject\": 1,\n      \"pending\": 0,\n      \"accept\": 1\n    },\n    \"b2bdn\": {\n      \"noaction\": 6,\n      \"reject\": 1,\n      \"pending\": 0,\n      \"accept\": 0\n    },\n    \"b2ba\": {\n      \"noaction\": 0,\n      \"reject\": 0,\n      \"pending\": 0,\n      \"accept\": 0\n    },\n    \"b2b\": {\n      \"noaction\": 13,\n      \"reject\": 0,\n      \"pending\": 1,\n      \"accept\": 1\n    },\n    \"ecom\": {\n      \"noaction\": 11,\n      \"reject\": 0,\n      \"pending\": 0,\n      \"accept\": 0\n    },\n    \"ttl_cnt\": 52,\n    \"ecoma\": {\n      \"noaction\": 3,\n      \"reject\": 0,\n      \"pending\": 0,\n      \"accept\": 0\n    },\n    \"b2bdna\": {\n      \"noaction\": 2,\n      \"reject\": 0,\n      \"pending\": 0,\n      \"accept\": 0\n    },\n    \"b2bcna\": {\n      \"noaction\": 0,\n      \"reject\": 0,\n      \"pending\": 0,\n      \"accept\": 0\n    }\n  },\n  \"inv_supp_isd\": {\n    \"ttl_cnt\": 101,\n    \"isd\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"isda\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"isdcn\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"isdcna\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    }\n  },\n  \"imp_gds\": {\n    \"ttl_cnt\": 60,\n    \"impg\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"impga\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"impgsez\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    },\n    \"impgseza\": {\n      \"noaction\": 20,\n      \"accept\": 10,\n      \"reject\": 2,\n      \"pending\": 1\n    }\n  }\n}";
		
		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		dto.setType("ALL_OTH");
		dto.setGstin(" abc");
		
		if (GETIMS_GOODS_TYPES.contains(dto.getType())) {

			JsonObject respObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			Gson gson = new Gson();

			GetImsCountGoodsTypeDtlsDto reqDto = gson.fromJson(respObject,
					GetImsCountGoodsTypeDtlsDto.class);
			
			/*List<ImsGstnCountEntity> countEntity = parseImsCountsData(dto,
					reqDto, dto.getType(), 100L, dto.getGroupcode());*/
		/*	System.out.println(countEntity);*/
	}
}
}
	

