package com.ey.advisory.app.services.jobs.gstr6;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6B2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6.GetGstr6B2baItemEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baInvoiceData;
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
@Service("Gstr6B2baDataParserImpl")
public class Gstr6B2baDataParserImpl implements Gstr6B2baGetDataParser {

	@Override
	public List<GetGstr6B2baHeaderEntity> parseB2baGetData(Gstr6GetInvoicesReqDto dto, String apiResp,String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr6B2baHeaderEntity> headerList = new ArrayList<>();

		try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2BA).getAsJsonArray();

			Type listType = new TypeToken<List<Gstr6B2baDto>>() {
			}.getType();

			// List of Header and Item Data
			List<Gstr6B2baDto> baseEntity = gson.fromJson(respObject, listType);
			if (CollectionUtils.isNotEmpty(baseEntity)) {
				for (Gstr6B2baDto eachInv : baseEntity) {

					for (Gstr6B2baInvoiceData eachInvData : eachInv.getInv()) {

						BigDecimal taxRate = BigDecimal.ZERO;
						BigDecimal taxValue = BigDecimal.ZERO;
						BigDecimal igstAmt = BigDecimal.ZERO;
						BigDecimal cgstAmt = BigDecimal.ZERO;
						BigDecimal sgstAmt = BigDecimal.ZERO;
						BigDecimal cessAmt = BigDecimal.ZERO;
						List<GetGstr6B2baItemEntity> lineItems = new ArrayList<>();

						if (eachInvData.getItms() != null) {

							for (Gstr6Items b2baItems : eachInvData.getItms()) {

								Gstr6ItemDetails b2baItem = b2baItems.getItmdet();
								// New Item Entity
								GetGstr6B2baItemEntity item = new GetGstr6B2baItemEntity();
								/**
								 * B2BAItemDetails data
								 */

								// item.setDerTaxPeriod(dto.getReturnPeriod());
								if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
									item.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
								}
								item.setTaxRate(b2baItem.getRate());
								item.setItmMo(b2baItems.getNum());
								item.setTaxableValue(b2baItem.getTaxableValue());
								item.setIgstAmt(b2baItem.getIgstAmount());
								item.setCgstAmt(b2baItem.getCgstAmount());
								item.setSgstAmt(b2baItem.getSgstAmount());
								item.setCessAmt(b2baItem.getCessAmount());

								lineItems.add(item);

								// Header Amounts count
								if (item.getTaxRate() != null) {
									taxRate = taxRate.add(item.getTaxRate());
								}
								if (item.getTaxableValue() != null) {
									taxValue = taxValue.add(item.getTaxableValue());
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
							GetGstr6B2baHeaderEntity header = new GetGstr6B2baHeaderEntity();
							/**
							 * Input data
							 */
							header.setGstin(dto.getGstin());
							header.setTaxPeriod(dto.getReturnPeriod());
							if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
								header.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
							}
							header.setBatchId(dto.getBatchId());

							/**
							 * B2BAInvoices data
							 */
							header.setFlag(eachInvData.getFlag());
							header.setChksum(eachInvData.getChksum());
							header.setDocNum(eachInvData.getInum());
							String docDate = eachInvData.getIdt();
							if (docDate != null) {
								header.setDocDate(LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
							}
							header.setDocAmt(eachInvData.getVal());

							header.setOrgDocNum(eachInvData.getOinum());
							String orgInvDate = eachInvData.getOidt();
							if (orgInvDate != null) {
								header.setOrgDocDate(LocalDate.parse(orgInvDate, DateUtil.SUPPORTED_DATE_FORMAT2));
							}
							

							

							header.setUploadedBy(eachInvData.getUpdby());
							header.setPos(eachInvData.getPos());
							header.setOpd(eachInvData.getOpd());

							// taxes at header level by summing the item values
							//header.set(taxRate);
							header.setTaxableValue(taxValue);
							header.setIgstAmt(igstAmt);
							header.setCgstAmt(cgstAmt);
							header.setSgstAmt(sgstAmt);
							header.setCessAmt(cessAmt);
							header.setBatchId(batchId);
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(LocalDateTime.now());
							header.setCreatedOn(convertNow);
							header.setCreatedBy("SYSTEM");
							header.setCfs(eachInv.getCfs());
							header.setCtin(eachInv.getCgstin());
							

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
			String msg = "failed to parse Gstr6 B2BA response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return headerList;
	}

}
