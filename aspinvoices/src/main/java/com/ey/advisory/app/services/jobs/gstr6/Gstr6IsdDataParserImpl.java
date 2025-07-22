package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6IsdDetailsEntity;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6IsdDataParserImpl")
public class Gstr6IsdDataParserImpl implements Gstr6IsdGetDataParser {

	@Override
	public List<GetGstr6IsdDetailsEntity> parseIsdGetData(
			Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		// JsonArray respObject = null;
		List<GetGstr6IsdDetailsEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}

			Gstr6IsdDetailsDto gstr6IsdDetailsDto = gson.fromJson(asJsonObject,
					Gstr6IsdDetailsDto.class);

			List<Gstr6IsdElglstDto> elglstDtos = gstr6IsdDetailsDto.getElglst();
			List<Gstr6IsdElglstDto> inElglstDtos = gstr6IsdDetailsDto
					.getInelglst();

			if (CollectionUtils.isNotEmpty(elglstDtos)) {
				elglstDtos.forEach(elgstDto -> {

					List<Gstr6IsdDocListItems> docListItems = elgstDto
							.getDoclst();
					if (CollectionUtils.isEmpty(docListItems)) {
						GetGstr6IsdDetailsEntity entity = new GetGstr6IsdDetailsEntity();

						entity.setCptyp(elgstDto.getCpty());
						entity.setUserType(elgstDto.getTyp());
						entity.setStateCode(elgstDto.getStatecd());
						entity.setElgIndicator("E");
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
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entities.add(entity);
					} else {
						docListItems.forEach(lstItm -> {
							GetGstr6IsdDetailsEntity entity = new GetGstr6IsdDetailsEntity();

							entity.setCptyp(elgstDto.getCpty());
							entity.setUserType(elgstDto.getTyp());
							entity.setStateCode(elgstDto.getStatecd());
							entity.setElgIndicator("E");
							entity.setChksum(lstItm.getChkSum());
							String doctype = lstItm.getIsdDocty();
							String isdDoctype = doctype == "ISD" ? "INV"
									: (doctype == "ISDUR" ? "INV"
											: (doctype == "ISDCN" ? "CR"
													: (doctype == "ISDCNUR"
															? "CR" : doctype)));
							entity.setIsdDocType(isdDoctype);
							
							String docDate = lstItm.getDocdt();
							String crdrnum = lstItm.getCrdnum().trim();
							String crdrDate = lstItm.getCrddt();

							if (isdDoctype.equalsIgnoreCase("ISDCN")) {
								entity.setCrdrDocNum(lstItm.getDocnum());
								entity.setDocNum(crdrnum);
								if (!Strings.isNullOrEmpty(docDate)) {
									entity.setCrdrDocDate(LocalDate.parse(
											docDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
								if (!Strings.isNullOrEmpty(crdrDate)) {
									entity.setDocDate(LocalDate.parse(crdrDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
							} else {
								entity.setDocNum(lstItm.getDocnum());
								entity.setCrdrDocNum(crdrnum);
								if (!Strings.isNullOrEmpty(docDate)) {
									entity.setDocDate(LocalDate.parse(docDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
								if (!Strings.isNullOrEmpty(crdrDate)) {
									entity.setCrdrDocDate(LocalDate.parse(
											crdrDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
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

					List<Gstr6IsdDocListItems> docListItems = inElgstDto
							.getDoclst();
					if (CollectionUtils.isEmpty(docListItems)) {
						GetGstr6IsdDetailsEntity entity = new GetGstr6IsdDetailsEntity();

						entity.setCptyp(inElgstDto.getCpty());
						entity.setUserType(inElgstDto.getTyp());
						entity.setStateCode(inElgstDto.getStatecd());
						entity.setElgIndicator("IE");
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
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);
						entities.add(entity);
					} else {
						docListItems.forEach(lstItm -> {
							GetGstr6IsdDetailsEntity entity = new GetGstr6IsdDetailsEntity();
							entity.setCptyp(inElgstDto.getCpty());
							entity.setUserType(inElgstDto.getTyp());
							entity.setStateCode(inElgstDto.getStatecd());
							entity.setElgIndicator("IE");
							entity.setChksum(lstItm.getChkSum());
							String docType = lstItm.getIsdDocty();
							String isdDocType = docType == "ISD" ? "INV"
									: (docType == "ISDUR" ? "INV"
											: (docType == "ISDCN" ? "CR"
													: (docType == "ISDCNUR"
															? "CR" : docType)));
							entity.setIsdDocType(isdDocType);

							String docDate = lstItm.getDocdt();
							String crdrnum = lstItm.getCrdnum().trim();
							String crdrDate = lstItm.getCrddt();

							if (isdDocType.equalsIgnoreCase("ISDCN")) {
								entity.setCrdrDocNum(lstItm.getDocnum());
								entity.setDocNum(crdrnum);
								if (!Strings.isNullOrEmpty(docDate)) {
									entity.setCrdrDocDate(LocalDate.parse(
											docDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
								if (!Strings.isNullOrEmpty(crdrDate)) {
									entity.setDocDate(LocalDate.parse(crdrDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
							} else {
								entity.setDocNum(lstItm.getDocnum());
								entity.setCrdrDocNum(crdrnum);
								if (!Strings.isNullOrEmpty(docDate)) {
									entity.setDocDate(LocalDate.parse(docDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
								if (!Strings.isNullOrEmpty(crdrDate)) {
									entity.setCrdrDocDate(LocalDate.parse(
											crdrDate,
											DateUtil.SUPPORTED_DATE_FORMAT2));
								}
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
			String msg = "Failed to Parse Isd  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}
	
	public static void main(String[] args) {
		String crdr = "         ";
		String cdr = crdr;
		System.out.println(cdr);
	}

}