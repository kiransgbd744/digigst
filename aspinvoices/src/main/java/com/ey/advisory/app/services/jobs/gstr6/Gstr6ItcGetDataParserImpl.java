package com.ey.advisory.app.services.jobs.gstr6;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6ItcDetailsEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItcDetails;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItcDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Slf4j
@Service("Gstr6ItcGetDataParserImpl")
public class Gstr6ItcGetDataParserImpl implements Gstr6ItcGetDataParser {

	@Override
	public List<GetGstr6ItcDetailsEntity> parseItcGetData(
			Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<GetGstr6ItcDetailsEntity> entities = new ArrayList<>();

		if (apiResp == null) {
			return null;
		}
		JsonObject respObject = (new JsonParser()).parse(apiResp)
				.getAsJsonObject();

		Gstr6ItcDto gstr6ItcDto = gson.fromJson(respObject, Gstr6ItcDto.class);

		entities.addAll(convertItcDetailsIntoEntity(gstr6ItcDto, batchId));
		entities.addAll(convertElgItcDetailsIntoEntity(gstr6ItcDto, batchId));
		entities.addAll(convertInElgItcDetailsIntoEntity(gstr6ItcDto, batchId));
		entities.addAll(convertIsdDetailsIntoEntity(gstr6ItcDto, batchId));

		return entities;
	}

	private Collection<? extends GetGstr6ItcDetailsEntity> convertIsdDetailsIntoEntity(
			Gstr6ItcDto gstr6ItcDto, Long batchId) {
		List<GetGstr6ItcDetailsEntity> entities = Lists.newArrayList();
		Gstr6ItcDetails isdItcDetails = gstr6ItcDto.getIsdItcCross();
		if (isdItcDetails != null) {
			GetGstr6ItcDetailsEntity elgItcEntity = new GetGstr6ItcDetailsEntity();
			elgItcEntity.setIsdItcCross(true);
			elgItcEntity.setCessAmt(isdItcDetails.getCessAmount());
			elgItcEntity.setIgstAmt(isdItcDetails.getIgstAmount());
			elgItcEntity.setCgstAmt(isdItcDetails.getCgstAmount());
			elgItcEntity.setSgstAmt(isdItcDetails.getSgstAmount());

			elgItcEntity.setIgstAmtCgst(isdItcDetails.getIamtc());
			elgItcEntity.setIgstAmtSgst(isdItcDetails.getIamts());
			elgItcEntity.setCgstAmtIgst(isdItcDetails.getCamti());
			elgItcEntity.setSgstAmtIgst(isdItcDetails.getSamti());

			elgItcEntity.setCessAmt(isdItcDetails.getCessAmount());
			elgItcEntity.setSgstAmtSgst(isdItcDetails.getSamts());
			elgItcEntity.setCgstAmtCgst(isdItcDetails.getCamtc());
			elgItcEntity.setIgstAmtIgst(isdItcDetails.getIamti());

			elgItcEntity.setBatchId(batchId);
			elgItcEntity.setCreatedBy(APIConstants.SYSTEM);
			elgItcEntity.setTaxPeriod(gstr6ItcDto.getRetPeriod());
			if (gstr6ItcDto.getRetPeriod() != null
					&& gstr6ItcDto.getRetPeriod().length() > 0) {
				elgItcEntity.setDerTaxPeriod(GenUtil
						.convertTaxPeriodToInt(gstr6ItcDto.getRetPeriod()));
			}
			elgItcEntity.setGstin(gstr6ItcDto.getGstin());
			elgItcEntity.setModifiedOn(LocalDateTime.now());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			elgItcEntity.setCreatedOn(convertNow);

			entities.add(elgItcEntity);
		}
		return entities;
	}

	private Collection<? extends GetGstr6ItcDetailsEntity> convertInElgItcDetailsIntoEntity(
			Gstr6ItcDto gstr6ItcDto, Long batchId) {
		List<GetGstr6ItcDetailsEntity> entities = Lists.newArrayList();
		Gstr6ItcDetails inelgItcDetails = gstr6ItcDto.getInelgItc();
		if (inelgItcDetails != null) {
			GetGstr6ItcDetailsEntity elgItcEntity = new GetGstr6ItcDetailsEntity();
			elgItcEntity.setInElgItc(true);
			if (inelgItcDetails.getDes() != null) {
				elgItcEntity.setDes(BigDecimal
						.valueOf(Long.valueOf(inelgItcDetails.getDes())));
			}
			elgItcEntity.setIgstAmt(inelgItcDetails.getIgstAmount());
			elgItcEntity.setCgstAmt(inelgItcDetails.getCgstAmount());
			elgItcEntity.setSgstAmt(inelgItcDetails.getSgstAmount());
			elgItcEntity.setCessAmt(inelgItcDetails.getCessAmount());
			elgItcEntity.setBatchId(batchId);
			elgItcEntity.setCreatedBy(APIConstants.SYSTEM);
			elgItcEntity.setTaxPeriod(gstr6ItcDto.getRetPeriod());
			if (gstr6ItcDto.getRetPeriod() != null
					&& gstr6ItcDto.getRetPeriod().length() > 0) {
				elgItcEntity.setDerTaxPeriod(GenUtil
						.convertTaxPeriodToInt(gstr6ItcDto.getRetPeriod()));
			}
			elgItcEntity.setGstin(gstr6ItcDto.getGstin());
			elgItcEntity.setModifiedOn(LocalDateTime.now());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			elgItcEntity.setCreatedOn(convertNow);

			entities.add(elgItcEntity);
		}
		return entities;
	}

	private Collection<? extends GetGstr6ItcDetailsEntity> convertElgItcDetailsIntoEntity(
			Gstr6ItcDto gstr6ItcDto, Long batchId) {
		List<GetGstr6ItcDetailsEntity> entities = Lists.newArrayList();
		Gstr6ItcDetails elgItcDetails = gstr6ItcDto.getElgItc();
		if (elgItcDetails != null) {
			GetGstr6ItcDetailsEntity elgItcEntity = new GetGstr6ItcDetailsEntity();
			elgItcEntity.setElgItc(true);
			if (elgItcDetails.getDes() != null) {
				elgItcEntity.setDes(BigDecimal
						.valueOf(Long.valueOf(elgItcDetails.getDes())));
			}
			elgItcEntity.setIgstAmt(elgItcDetails.getIgstAmount());
			elgItcEntity.setCgstAmt(elgItcDetails.getCgstAmount());
			elgItcEntity.setSgstAmt(elgItcDetails.getSgstAmount());
			elgItcEntity.setCessAmt(elgItcDetails.getCessAmount());
			elgItcEntity.setBatchId(batchId);
			elgItcEntity.setCreatedBy(APIConstants.SYSTEM);
			elgItcEntity.setTaxPeriod(gstr6ItcDto.getRetPeriod());
			if (gstr6ItcDto.getRetPeriod() != null
					&& gstr6ItcDto.getRetPeriod().length() > 0) {
				elgItcEntity.setDerTaxPeriod(GenUtil
						.convertTaxPeriodToInt(gstr6ItcDto.getRetPeriod()));
			}
			elgItcEntity.setGstin(gstr6ItcDto.getGstin());
			elgItcEntity.setModifiedOn(LocalDateTime.now());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			elgItcEntity.setCreatedOn(convertNow);

			entities.add(elgItcEntity);
		}
		return entities;
	}

	private Collection<? extends GetGstr6ItcDetailsEntity> convertItcDetailsIntoEntity(
			Gstr6ItcDto gstr6ItcDto, long batchId) {
		List<GetGstr6ItcDetailsEntity> entities = Lists.newArrayList();
		Gstr6ItcDetails itcDetails = gstr6ItcDto.getTotalItc();
		if (itcDetails != null) {
			GetGstr6ItcDetailsEntity itcDetailsEntity = new GetGstr6ItcDetailsEntity();
			itcDetailsEntity.setTotalItc(true);
			if (itcDetails.getDes() != null) {
				itcDetailsEntity.setDes(
						BigDecimal.valueOf(Long.valueOf(itcDetails.getDes())));
			}
			itcDetailsEntity.setIgstAmt(itcDetails.getIgstAmount());
			itcDetailsEntity.setCgstAmt(itcDetails.getCgstAmount());
			itcDetailsEntity.setSgstAmt(itcDetails.getSgstAmount());
			itcDetailsEntity.setCessAmt(itcDetails.getCessAmount());
			itcDetailsEntity.setGoLive(itcDetails.getGoLiveFlag());
			itcDetailsEntity.setBatchId(batchId);
			itcDetailsEntity.setCreatedBy(APIConstants.SYSTEM);
			itcDetailsEntity.setTaxPeriod(gstr6ItcDto.getRetPeriod());
			if (gstr6ItcDto.getRetPeriod() != null
					&& gstr6ItcDto.getRetPeriod().length() > 0) {
				itcDetailsEntity.setDerTaxPeriod(GenUtil
						.convertTaxPeriodToInt(gstr6ItcDto.getRetPeriod()));
			}
			itcDetailsEntity.setGstin(gstr6ItcDto.getGstin());
			itcDetailsEntity.setModifiedOn(LocalDateTime.now());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			itcDetailsEntity.setCreatedOn(convertNow);

			entities.add(itcDetailsEntity);
		}
		return entities;
	}

}
