package com.ey.advisory.app.services.jobs.gstr6;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6B2bHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6.GetGstr6B2bItemEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItemDetails;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
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
@Service("Gstr6B2bDataParserImpl")
public class Gstr6B2bDataParserImpl implements Gstr6B2bGetDataParser {

	@Override
	public List<GetGstr6B2bHeaderEntity> parseB2bGetData(
			Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6B2bHeaderEntity> headerList = new ArrayList<>();

		try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.B2B).getAsJsonArray();

			Type listType = new TypeToken<List<Gstr6B2bDto>>() {
			}.getType();

			// List of Header and Item Data
			List<Gstr6B2bDto> baseEntity = gson.fromJson(respObject, listType);
			if (CollectionUtils.isNotEmpty(baseEntity)) {
				for (Gstr6B2bDto eachInv : baseEntity) {

					for (Gstr6B2bInvoiceData eachInvData : eachInv.getInv()) {

						BigDecimal taxRate = BigDecimal.ZERO;
						BigDecimal taxValue = BigDecimal.ZERO;
						BigDecimal igstAmt = BigDecimal.ZERO;
						BigDecimal cgstAmt = BigDecimal.ZERO;
						BigDecimal sgstAmt = BigDecimal.ZERO;
						BigDecimal cessAmt = BigDecimal.ZERO;
						List<GetGstr6B2bItemEntity> lineItems = new ArrayList<>();

						if (eachInvData.getItms() != null) {

							for (Gstr6Items b2bItems : eachInvData.getItms()) {

								Gstr6ItemDetails b2bItem = b2bItems.getItmdet();
								// New Item Entity
								GetGstr6B2bItemEntity item = new GetGstr6B2bItemEntity();
								/**
								 * B2BItemDetails data
								 */

								// item.setReturnPeriod(dto.getReturnPeriod());
								if (dto.getReturnPeriod() != null
										&& dto.getReturnPeriod().length() > 0) {
									item.setDerTaxPeriod(
											GenUtil.convertTaxPeriodToInt(
													dto.getReturnPeriod()));
								}
								item.setTaxRate(b2bItem.getRate());
								item.setItmMo(b2bItems.getNum());
								item.setTaxableValue(b2bItem.getTaxableValue());
								item.setIgstAmt(b2bItem.getIgstAmount());
								item.setCgstAmt(b2bItem.getCgstAmount());
								item.setSgstAmt(b2bItem.getSgstAmount());
								item.setCessAmt(b2bItem.getCessAmount());

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
							GetGstr6B2bHeaderEntity header = new GetGstr6B2bHeaderEntity();
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
							 * B2BInvoices data
							 */
							header.setFlag(eachInvData.getFlag());
							header.setChksum(eachInvData.getChksum());
							header.setDocNum(eachInvData.getInum());
							String docDate = eachInvData.getIdt();
							if (docDate != null) {
								header.setDocDate(LocalDate.parse(docDate,
										DateUtil.SUPPORTED_DATE_FORMAT2));
							}

							header.setDocAmt(eachInvData.getVal());

							header.setPos(eachInvData.getPos());

							header.setUploadedBy(eachInvData.getUpdby());

							// taxes at header level by summing the item values
							// header.setTax(taxRate);
							header.setTaxableValue(taxValue);
							header.setIgstAmt(igstAmt);
							header.setCgstAmt(cgstAmt);
							header.setSgstAmt(sgstAmt);
							header.setCessAmt(cessAmt);
							header.setBatchId(batchId);
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							header.setCreatedOn(convertNow);
							header.setCreatedBy("SYSTEM");
							header.setCfs(eachInv.getCfs());
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
			String msg = "failed to parse Gstr6 B2B response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return headerList;
	}

}
