package com.ey.advisory.app.services.jobs.gstr6;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6CdnHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6.GetGstr6CdnItemEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItemDetails;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
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
@Service("Gstr6CdnDataParserImpl")
public class Gstr6CdnDataParserImpl implements Gstr6CdnGetDataParser {

	@Autowired
	private GstnApi gstnapi;

	@Override
	public List<GetGstr6CdnHeaderEntity> parseCdnGetData(
			Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6CdnHeaderEntity> headerList = new ArrayList<>();

		try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.CDN).getAsJsonArray();

			Type listType = new TypeToken<List<Gstr6CdnDto>>() {
			}.getType();

			// List of Header and Item Data
			List<Gstr6CdnDto> baseEntity = gson.fromJson(respObject, listType);
			if (CollectionUtils.isNotEmpty(baseEntity)) {
				for (Gstr6CdnDto eachInv : baseEntity) {

					for (Gstr6CdnNtData eachInvData : eachInv.getNt()) {

						BigDecimal taxRate = BigDecimal.ZERO;
						BigDecimal taxValue = BigDecimal.ZERO;
						BigDecimal igstAmt = BigDecimal.ZERO;
						BigDecimal cgstAmt = BigDecimal.ZERO;
						BigDecimal sgstAmt = BigDecimal.ZERO;
						BigDecimal cessAmt = BigDecimal.ZERO;
						List<GetGstr6CdnItemEntity> lineItems = new ArrayList<>();

						if (eachInvData.getItms() != null) {

							for (Gstr6Items cdnItems : eachInvData.getItms()) {

								Gstr6ItemDetails cdItem = cdnItems.getItmdet();
								// New Item Entity
								GetGstr6CdnItemEntity item = new GetGstr6CdnItemEntity();
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
								item.setIgstAmt(cdItem.getIgstAmount());
								item.setCgstAmt(cdItem.getCgstAmount());
								item.setSgstAmt(cdItem.getSgstAmount());
								item.setCessAmt(cdItem.getCessAmount());

								lineItems.add(item);

								// Header Amounts count
								if (item.getTaxRate() != null) {
									taxRate = taxRate.add(item.getTaxRate());
								}
								if (item.getTaxableValue() != null) {
									taxValue = taxValue
											.add(item.getTaxableValue());
								}
								if (item.getIgstAmt() != null) {

									igstAmt = igstAmt.add(item.getIgstAmt());
								}
								if (item.getCgstAmt() != null) {

									cgstAmt = cgstAmt.add(item.getCgstAmt());
								}
								if (item.getSgstAmt() != null) {

									sgstAmt = sgstAmt.add(item.getSgstAmt());
								}

								if (item.getCessAmt() != null) {
									cessAmt = cessAmt.add(item.getCessAmt());
								}

							}
							// New Header Entity
							GetGstr6CdnHeaderEntity header = new GetGstr6CdnHeaderEntity();
							/**
							 * Input data
							 */
							header.setGstin(dto.getGstin());
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
							header.setDocAmount(eachInvData.getVal());
							header.setFlag(eachInvData.getFlag());
							header.setChksum(eachInvData.getChksum());
							if (eachInvData.getNtty() != null) {
								if (eachInvData.getNtty()
										.equalsIgnoreCase("C")) {
									header.setNoteType("CR");
								} else {
									header.setNoteType("DR");
								}
							}
							header.setDocNum(eachInvData.getInum());
							String docDate = eachInvData.getIdt();
							if (!Strings.isNullOrEmpty(docDate)) {
								header.setDocDate(LocalDate.parse(docDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							header.setUploadedBy(eachInvData.getUpdby());
							header.setPos(eachInvData.getPos());

							// taxes at header level by summing the item values
							// header.setTaxRate(taxRate);
							header.setTaxableValue(taxValue);
							header.setIgstAmt(igstAmt);
							header.setCgstAmt(cgstAmt);
							header.setSgstAmt(sgstAmt);
							header.setCessAmt(cessAmt);

							// if
							// (gstnapi.isDelinkingEligible(APIConstants.GSTR6.toUpperCase()))
							// {
							header.setPos(eachInvData.getPos());
							header.setdFlag(eachInvData.getDelinkStatus());

							// }

							header.setNoteNum(eachInvData.getNtnum());
							String noteDate = eachInvData.getNtdt();
							if (!Strings.isNullOrEmpty(noteDate)) {
								header.setNoteDate(LocalDate.parse(noteDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							header.setBatchId(batchId);
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							header.setCreatedOn(convertNow);
							header.setCreatedBy("SYSTEM");
							header.setCtin(eachInv.getCgstin());
							header.setAction("N");

							header.setLineItems(lineItems);
							lineItems.forEach(item -> {
								item.setDocument(header);
							});

							headerList.add(header);
						}
					}
				}

			}

		} catch (Exception ex) {
			String msg = "failed to parse Gstr6 CDN response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return headerList;
	}

}
