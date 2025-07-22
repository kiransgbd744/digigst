package com.ey.advisory.app.services.jobs.gstr6a;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EinvGstinClientEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnItemEntity;
import com.ey.advisory.app.data.repositories.client.EinvClientGstinRepository;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aCdnDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aCdnInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItemDetails;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItems;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.google.common.base.Strings;
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
@Service("Gstr6aCdnDataParserImpl")
public class Gstr6aCdnDataParserImpl implements Gstr6aCdnGetDataParser {

	private static final String DOC_KEY_JOINER = "|";
	
	@Autowired
	private GstnApi gstnapi;

	@Autowired
	private EinvClientGstinRepository einvClientGstinRepo;

	@Override
	public List<GetGstr6aStagingCdnHeaderEntity> parseCdnGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6aStagingCdnHeaderEntity> headerList = new ArrayList<>();

		try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.CDN).getAsJsonArray();

			Type listType = new TypeToken<List<Gstr6aCdnDto>>() {
			}.getType();

			// List of Header and Item Data
			List<Gstr6aCdnDto> baseEntity = gson.fromJson(respObject, listType);

			List<EinvGstinClientEntity> einvEligiblegstins = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(baseEntity)) {
				for (Gstr6aCdnDto eachInv : baseEntity) {
					boolean isFlag = true;
					for (Gstr6aCdnInvoiceData eachInvData : eachInv.getNt()) {

						List<GetGstr6aStagingCdnItemEntity> lineItems = new ArrayList<>();

						if (eachInvData.getItms() != null) {

							for (Gstr6aItems cdnItems : eachInvData.getItms()) {

								Gstr6aItemDetails cdItem = cdnItems.getItmdet();
								// New Item Entity
								GetGstr6aStagingCdnItemEntity item = new GetGstr6aStagingCdnItemEntity();
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
							GetGstr6aStagingCdnHeaderEntity header = new GetGstr6aStagingCdnHeaderEntity();
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
							 * CDNInvoices data
							 */
							header.setSgstAmt(eachInvData.getSamt());
							header.setTaxableValue(eachInvData.getTxval());
							header.setInvValue(eachInvData.getVal());
							header.setCgstAmt(eachInvData.getCamt());
							header.setDocNum(eachInvData.getInum());
							header.setIgstAmt(eachInvData.getIamt());
							header.setCessAmt(eachInvData.getCsamt());
							header.setCfs(eachInvData.getCfs());
							String counterPeriod = eachInvData.getCfp();
							if (counterPeriod != null) {
								header.setCfp(counterPeriod);
							}
							header.setNoteType(eachInvData.getNtty());
							String docDate = eachInvData.getIdt();
							if (docDate != null) {
								header.setDocDate(LocalDate.parse(docDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
								header.setInvDate(LocalDate.parse(docDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							header.setChksum(eachInvData.getChksum());

							// if
							// (gstnapi.isDelinkingEligible(APIConstants.GSTR6A.toUpperCase()))
							// {
							header.setPos(eachInvData.getPos());
							header.setdFlag(eachInvData.getDelinkStatus());

							// }
							header.setpGst(eachInvData.getPgst());
							header.setNoteNum(eachInvData.getNt_num());
							header.setNoteNumber(eachInvData.getNt_num());
							header.setInvType(eachInvData.getNtty());
							header.setSuppInvVal(eachInvData.getVal());
							String noteDate = eachInvData.getNtdt();
							if (noteDate != null) {
								header.setNoteDate(LocalDate.parse(noteDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							header.setCreatedOn(convertNow);
							header.setCreatedBy("SYSTEM");
							// header.setCfs(eachInv.getCfs());
							header.setCtin(dto.getGstin());
							
							header.setIrnNumber(eachInvData.getIrn());
							String irnDate = eachInvData.getIrngendate();
							if(irnDate != null){
								header.setIrnDate(LocalDate.parse(irnDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}
							header.setIrnSourceType(eachInvData.getSourceType());
							
							String invKey = generateKey(eachInvData.getNtdt(), header.getCtin(), header.getGstin(),
									eachInvData.getNtty(), eachInvData.getNt_num());
							
							header.setInvKey(invKey);

							header.setLineItems(lineItems);
							lineItems.forEach(item -> {
								item.setHeader(header);
							});

							if ((!Strings.isNullOrEmpty(eachInvData.getIrn())
									&& isFlag)) {
								EinvGstinClientEntity entity = new EinvGstinClientEntity();
								entity.setGstin(dto.getGstin());
								entity.setCreatedDate(LocalDateTime.now());
								entity.setSource("6A");
								entity.setPan(dto.getGstin().substring(2, 12));
								try {
									einvClientGstinRepo.save(entity);
								} catch (DataIntegrityViolationException ex) {

									if (LOGGER.isWarnEnabled()) {
										LOGGER.warn(
												"Gstin already exists in einvoive "
														+ "applicability client table for "
														+ "GSTR6A for section B2B : {}",
												dto.getGstin());
									}
								}
								isFlag = false;
							}

							headerList.add(header);
						}
					}
				}
			}

		}catch (DataIntegrityViolationException ex) {

			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(
						"Gstin already exists in einvoive applicability client table : {}",
						dto.getGstin());
			}
		}
		catch (Exception ex) {
			String msg = "failed to parse Gstr6a CDN response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}

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
