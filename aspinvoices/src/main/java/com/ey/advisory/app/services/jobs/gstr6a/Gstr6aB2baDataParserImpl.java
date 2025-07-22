package com.ey.advisory.app.services.jobs.gstr6a;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baItemEntity;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aB2baDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aB2baInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItemDetails;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aItems;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
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
@Service("Gstr6aB2baDataParserImpl")
public class Gstr6aB2baDataParserImpl implements Gstr6aB2baGetDataParser {
	
	
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public List<GetGstr6aStagingB2baHeaderEntity> parseB2baGetData(
			Gstr6aGetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6aStagingB2baHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.B2BA).getAsJsonArray();

		Type listType = new TypeToken<List<Gstr6aB2baDto>>() {
		}.getType();

		// List of Header and Item Data
		List<Gstr6aB2baDto> baseEntity = gson.fromJson(respObject, listType);
		if (CollectionUtils.isNotEmpty(baseEntity)) {
			for (Gstr6aB2baDto eachInv : baseEntity) {

				for (Gstr6aB2baInvoiceData eachInvData : eachInv.getInv()) {

					List<GetGstr6aStagingB2baItemEntity> lineItems = new ArrayList<>();

					if (eachInvData.getItms() != null) {

						for (Gstr6aItems b2bItems : eachInvData.getItms()) {

							Gstr6aItemDetails b2bItem = b2bItems.getItmdet();
							// New Item Entity
							GetGstr6aStagingB2baItemEntity item = new GetGstr6aStagingB2baItemEntity();
							/**
							 * B2BItemDetails data
							 */

							// item.setDerTaxPeriod(dto.getReturnPeriod());
							if (dto.getReturnPeriod() != null
									&& dto.getReturnPeriod().length() > 0) {
								item.setDerTaxPeriod(
										GenUtil.convertTaxPeriodToInt(
												dto.getReturnPeriod()));
							}
							item.setTaxRate(b2bItem.getRate());
							item.setItmMo(b2bItems.getNum());
							item.setTaxableValue(b2bItem.getTaxableValue());
							item.setDocAmt(eachInvData.getVal());
							item.setIgstAmt(b2bItem.getIgstAmount());
							item.setCgstAmt(b2bItem.getCgstAmount());
							item.setSgstAmt(b2bItem.getSgstAmount());
							item.setCessAmt(b2bItem.getCessAmount());

							lineItems.add(item);

						}
						// New Header Entity
						GetGstr6aStagingB2baHeaderEntity header = new GetGstr6aStagingB2baHeaderEntity();
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
						 * B2BInvoices data
						 */
						header.setTaxableValue(eachInvData.getVal());
						header.setSgstAmt(eachInvData.getSamt());
						header.setOrgDocNum(eachInvData.getOinum());
						header.setDocType(eachInvData.getInvType());
						header.setTaxableValue(eachInvData.getTxval());
						header.setDocNum(eachInvData.getInum());

						header.setDocType(eachInvData.getInvType());
						header.setInvNum(eachInvData.getInum());
						header.setInvValue(eachInvData.getVal());
						header.setInvType(eachInvData.getInvType());
						header.setPos(eachInvData.getPos());
						String docDate = eachInvData.getIdt();
						if (docDate != null) {
							header.setDocDate(LocalDate.parse(docDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
							header.setInvDate(LocalDate.parse(docDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
						}

						header.setRevCharge(eachInvData.getRchrg());
						String orgInvDate = eachInvData.getOidt();
						if (orgInvDate != null) {
							header.setOrgDocDate(LocalDate.parse(orgInvDate,
									DateUtil.SUPPORTED_DATE_FORMAT2));
						}

						header.setChksum(eachInvData.getChksum());
						header.setTaxableValue(eachInvData.getTxval());
						header.setDocAmt(eachInvData.getVal());
						header.setIgstAmt(eachInvData.getIamt());
						header.setCgstAmt(eachInvData.getCamt());
						header.setSgstAmt(eachInvData.getSamt());
						header.setCessAmt(eachInvData.getCsamt());
						header.setCfs(eachInvData.getCfs());
						String counterPeriod = eachInvData.getCfp();
						if (counterPeriod != null) {
							header.setCfp(counterPeriod);
						}
						header.setBatchId(batchId);
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						header.setCreatedOn(convertNow);
						header.setCreatedBy("SYSTEM");
						// header.setCfs(eachInv.getCfs());
						header.setCtin(dto.getGstin());

						header.setLineItems(lineItems);
						
						String invKey = generateKey(docDate, header.getCtin(), header.getGstin(),
								header.getDocType(),header.getDocNum());
						
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
		 * "failed to parse Gstr6a B2B response"; LOGGER.error(msg, ex); throw
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
