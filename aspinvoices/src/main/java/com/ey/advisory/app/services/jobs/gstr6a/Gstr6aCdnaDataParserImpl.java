package com.ey.advisory.app.services.jobs.gstr6a;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaItemEntity;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aCdnaDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aCdnaInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItemDetails;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItems;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6aCdnaDataParserImpl")
public class Gstr6aCdnaDataParserImpl implements Gstr6aCdnaGetDataParser {
	
	private static final String DOC_KEY_JOINER = "|";

	@Autowired
	private GstnApi gstnapi;

	@Override
	public List<GetGstr6aStagingCdnaHeaderEntity> parseCdnaGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6aStagingCdnaHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.CDNA).getAsJsonArray();

		Type listType = new TypeToken<List<Gstr6aCdnaDto>>() {
		}.getType();

		// List of Header and Item Data
		List<Gstr6aCdnaDto> baseEntity = gson.fromJson(respObject, listType);
		if (CollectionUtils.isNotEmpty(baseEntity)) {
			for (Gstr6aCdnaDto eachInv : baseEntity) {

				for (Gstr6aCdnaInvoiceData eachInvData : eachInv.getNt()) {

					List<GetGstr6aStagingCdnaItemEntity> lineItems = new ArrayList<>();

					if (eachInvData.getItms() != null) {

						for (Gstr6aItems cdnItems : eachInvData.getItms()) {

							Gstr6aItemDetails cdItem = cdnItems.getItmdet();
							// New Item Entity
							GetGstr6aStagingCdnaItemEntity item = new GetGstr6aStagingCdnaItemEntity();
							/**
							 * CDNItemDetails data
							 */

							// item.setReturnPeriod(dto.getReturnPeriod());
							if (dto.getReturnPeriod() != null
									&& dto.getReturnPeriod().length() > 0) {
								item.setDerTaxPeriod(
										GenUtil.convertTaxPeriodToInt(
												dto.getReturnPeriod()));
							}
							item.setTaxRate(cdItem.getRate());
							item.setItmMo(cdnItems.getNum());
							item.setTaxableValue(cdItem.getTaxableValue());
							item.setInvValue(eachInvData.getVal());
							item.setIgstAmt(cdItem.getIgstAmount());
							item.setCgstAmt(cdItem.getCgstAmount());
							item.setSgstAmt(cdItem.getSgstAmount());
							item.setCessAmt(cdItem.getCessAmount());

							lineItems.add(item);

						}
						// New Header Entity
						GetGstr6aStagingCdnaHeaderEntity header = new GetGstr6aStagingCdnaHeaderEntity();
						/**
						 * Input data
						 */
						header.setGstin(eachInv.getCtin());
						header.setTaxPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null
								&& dto.getReturnPeriod().length() > 0) {
							header.setDerTaxPeriod(
									GenUtil.convertTaxPeriodToInt(
											dto.getReturnPeriod()));
						}
						header.setBatchId(dto.getBatchId());

						/**
						 * CDNAInvoices data
						 */
						header.setInvValue(eachInvData.getVal());
						header.setSgstAmt(eachInvData.getSamt());
						header.setTaxableValue(eachInvData.getTxval());
						header.setCgstAmt(eachInvData.getCamt());
						header.setDocNum(eachInvData.getInum());
						header.setIgstAmt(eachInvData.getIamt());
						header.setCessAmt(eachInvData.getCsamt());
						header.setCfs(eachInvData.getCfs());
						header.setInvNum(eachInvData.getInum());
						String counterPeriod = eachInvData.getCfp();
						if (counterPeriod != null) {
							header.setCfp(counterPeriod);
						}

						String docDate = eachInvData.getIdt();
						if (docDate != null) {
							header.setDocDate(LocalDate.parse(docDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
							header.setInvDate(LocalDate.parse(docDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
						}

						header.setNoteType(eachInvData.getNtty());

						header.setChksum(eachInvData.getChksum());

						// if
						// (gstnapi.isDelinkingEligible(APIConstants.GSTR6A.toUpperCase()))
						// {
						header.setPos(eachInvData.getPos());
						header.setdFlag(eachInvData.getDelinkStatus());

						// }
						header.setpGst(eachInvData.getPgst());
						header.setNoteNum(eachInvData.getNt_num());

						String noteDate = eachInvData.getNtdt();
						if (noteDate != null) {
							header.setNoteDate(LocalDate.parse(noteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
						}
						header.setOrgNoteNum(eachInvData.getOnt_num());

						String orgNoteDate = eachInvData.getOntdt();
						if (orgNoteDate != null) {
							header.setOrgNoteDate(LocalDate.parse(orgNoteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
						}

						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						header.setCreatedOn(convertNow);
						header.setCreatedBy("SYSTEM");
						// header.setCfs(eachInv.getCfs());
						header.setCtin(dto.getGstin());

						header.setLineItems(lineItems);
						
						String invKey = generateKey(eachInvData.getNtdt(), header.getCtin(), header.getGstin(),
								header.getNoteType(), eachInvData.getNt_num());
						
						header.setInvKey(invKey);
						
						lineItems.forEach(item -> {
							item.setHeader(header);
						});

						headerList.add(header);
					}
				}
			}
		}

		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr6a CDNA response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
	
	private String generateKey(String docDate,String cgstin,String sgstin, String docType, String docNo ) {
		
		 String finYear = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));
		 
		return new StringJoiner(DOC_KEY_JOINER)
				.add(finYear)				
				.add(cgstin)
				.add(sgstin)
				.add(docType)
				.add(docNo)
				.toString();
	}

}
