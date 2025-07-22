package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6IsdaDetailsEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDetailsDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDocListItems;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdElglstDto;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6IsdaDataParserImpl")
public class Gstr6IsdaDataParserImpl implements Gstr6IsdaGetDataParser {

	@Override
	public List<GetGstr6IsdaDetailsEntity> parseIsdaGetData(
			Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6IsdaDetailsEntity> entities = new ArrayList<>();

		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			/*
			 * respObject = asJsonObject.get(APIConstants.ISDA) != null ?
			 * asJsonObject.get(APIConstants.ISD).getAsJsonArray() : null; if
			 * (respObject == null) { return null; }
			 * 
			 * Type listType = new TypeToken<List<Gstr6IsdDetailsDto>>() {
			 * }.getType();
			 */
			Gstr6IsdDetailsDto gstr6IsdDetailsDto = gson.fromJson(asJsonObject,
					Gstr6IsdDetailsDto.class);

			List<Gstr6IsdElglstDto> elglstDtos = gstr6IsdDetailsDto.getElglst();
			List<Gstr6IsdElglstDto> inElglstDtos = gstr6IsdDetailsDto
					.getInelglst();

			if (CollectionUtils.isNotEmpty(elglstDtos)) {
				elglstDtos.forEach(elgstDto -> {
					GetGstr6IsdaDetailsEntity entity = new GetGstr6IsdaDetailsEntity();

					entity.setUserType(elgstDto.getTyp());
					entity.setCptyp(elgstDto.getCpty());
					entity.setRcptyp(elgstDto.getRcpty());
					entity.setStateCode(elgstDto.getStatecd());
					entity.setRstateCode(elgstDto.getRstatecd());
					entity.setElgIndicator("E");
					List<Gstr6IsdDocListItems> docListItems = elgstDto
							.getDoclst();
					if (CollectionUtils.isEmpty(docListItems)) {
						entities.add(entity);
					} else {
						docListItems.forEach(lstItm -> {
							entity.setCptyp(lstItm.getCpty());
							entity.setStateCode(lstItm.getStatecd());
							entity.setChksum(lstItm.getChkSum());
							String docType = lstItm.getIsdDocty();
							String isdDocType = docType == "ISDCNURA" ? "RCR"
									: (docType == "ISDCNA" ? "RCR"
											: (docType == "ISDA" ? "RNV"
													: (docType == "ISDURA"
															? "RNV"
															: docType)));
							entity.setIsdDocType(isdDocType);
							entity.setRdocNum(lstItm.getRdocnum());
							String rdocDate = lstItm.getRdocdt();
							if (!Strings.isNullOrEmpty(rdocDate)) {
								entity.setRdocDate(LocalDate.parse(rdocDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}
							entity.setOrgDocNum(lstItm.getOdocnum());

							String odocDate = lstItm.getOdocdt();
							if (!Strings.isNullOrEmpty(odocDate)) {
								entity.setOrgDocDate(LocalDate.parse(odocDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setRcrdrDocNum(lstItm.getRcrdnum());

							String rcrdDate = lstItm.getRcrddt();
							if (!Strings.isNullOrEmpty(rcrdDate)) {
								entity.setRcrdrDocDate(LocalDate.parse(rcrdDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setOcrdrDocNum(lstItm.getOcrdnum());

							String ocrdDate = lstItm.getOcrddt();
							if (!Strings.isNullOrEmpty(ocrdDate)) {
								entity.setOcrdrDocDate(LocalDate.parse(ocrdDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setIgstAmtIgst(lstItm.getIamti());
							entity.setIgstAmtSgst(lstItm.getIamts());
							entity.setIgstAmtCgst(lstItm.getIamtc());
							entity.setSgstAmtSgst(lstItm.getSamts());
							entity.setSgstAmtIgst(lstItm.getSamti());
							entity.setCgstAmtIgst(lstItm.getCamti());
							entity.setCgstAmtCgst(lstItm.getCamtc());
							entity.setCessAmt(lstItm.getCsamt());
							entity.setGstin(dto.getGstin());
							entity.setTaxPeriod(dto.getReturnPeriod());
							if (dto.getReturnPeriod() != null
									&& dto.getReturnPeriod().length() > 0) {
								entity.setDerTaxPeriod(
										GenUtil.convertTaxPeriodToInt(
												dto.getReturnPeriod()));
							}

							entity.setBatchId(dto.getBatchId());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);

							entities.add(entity);
						});
					}
				});

			}

			if (CollectionUtils.isNotEmpty(inElglstDtos)) {
				inElglstDtos.forEach(inElgstDto -> {
					GetGstr6IsdaDetailsEntity entity = new GetGstr6IsdaDetailsEntity();

					entity.setUserType(inElgstDto.getTyp());
					entity.setCptyp(inElgstDto.getCpty());
					entity.setRcptyp(inElgstDto.getRcpty());
					entity.setStateCode(inElgstDto.getStatecd());
					entity.setRstateCode(inElgstDto.getRstatecd());
					entity.setElgIndicator("IE");
					List<Gstr6IsdDocListItems> docListItems = inElgstDto
							.getDoclst();
					if (CollectionUtils.isEmpty(docListItems)) {
						entities.add(entity);
					} else {
						docListItems.forEach(lstItm -> {

							entity.setCptyp(lstItm.getCpty());
							entity.setStateCode(lstItm.getStatecd());
							entity.setChksum(lstItm.getChkSum());
							String docType = lstItm.getIsdDocty();
							String isdDocType = docType == "ISDCNURA" ? "RCR"
									: (docType == "ISDCNA" ? "RCR"
											: (docType == "ISDA" ? "RNV"
													: (docType == "ISDURA"
															? "RNV"
															: docType)));
							entity.setIsdDocType(isdDocType);
							entity.setRdocNum(lstItm.getRdocnum());
							String rdocDate = lstItm.getRdocdt();
							if (!Strings.isNullOrEmpty(rdocDate)) {
								entity.setRdocDate(LocalDate.parse(rdocDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}
							entity.setOrgDocNum(lstItm.getOdocnum());

							String odocDate = lstItm.getOdocdt();
							if (!Strings.isNullOrEmpty(odocDate)) {
								entity.setOrgDocDate(LocalDate.parse(odocDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setRcrdrDocNum(lstItm.getRcrdnum());

							String rcrdDate = lstItm.getRcrddt();
							if (!Strings.isNullOrEmpty(rcrdDate)) {
								entity.setRcrdrDocDate(LocalDate.parse(rcrdDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setOcrdrDocNum(lstItm.getOcrdnum());

							String ocrdDate = lstItm.getOcrddt();
							if (!Strings.isNullOrEmpty(ocrdDate)) {
								entity.setOcrdrDocDate(LocalDate.parse(ocrdDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							entity.setIgstAmtIgst(lstItm.getIamti());
							entity.setIgstAmtSgst(lstItm.getIamts());
							entity.setIgstAmtCgst(lstItm.getIamtc());
							entity.setSgstAmtSgst(lstItm.getSamts());
							entity.setSgstAmtIgst(lstItm.getSamti());
							entity.setCgstAmtIgst(lstItm.getCamti());
							entity.setCgstAmtCgst(lstItm.getCamtc());
							entity.setCessAmt(lstItm.getCsamt());
							entity.setGstin(dto.getGstin());
							entity.setTaxPeriod(dto.getReturnPeriod());
							if (dto.getReturnPeriod() != null
									&& dto.getReturnPeriod().length() > 0) {
								entity.setDerTaxPeriod(
										GenUtil.convertTaxPeriodToInt(
												dto.getReturnPeriod()));
							}

							entity.setBatchId(dto.getBatchId());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);

							entities.add(entity);
						});
					}
				});

			}

		} catch (Exception e) {
			String msg = "Failed to Parse Isda  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}
}
