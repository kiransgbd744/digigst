package com.ey.advisory.app.services.jobs.itc04;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Itc04InvoicesEntity;
import com.ey.advisory.app.docs.dto.itc04.Itc04Dto;
import com.ey.advisory.app.docs.dto.itc04.Itc04ItemsDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04M2jwDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5ADto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5BDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5CDto;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
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
@Service("Itc04InvoicesDataParserImpl")
public class Itc04InvoicesDataParserImpl implements Itc04InvoicesDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<Itc04InvoicesEntity> parseItc04GetData(
			Itc04GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Itc04InvoicesEntity> entities = Lists.newArrayList();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}

			Itc04Dto responseList = gson.fromJson(asJsonObject, Itc04Dto.class);

			List<Itc04M2jwDto> m2jw = responseList.getM2jw();

			List<Itc04Table5ADto> table5a = responseList.getTable5a();

			List<Itc04Table5BDto> table5b = responseList.getTable5b();

			List<Itc04Table5CDto> table5c = responseList.getTable5c();

			if (CollectionUtils.isNotEmpty(m2jw)) {
				AtomicInteger itemNumber = new AtomicInteger(1);
				AtomicReference<String> challanNumber = new AtomicReference<String>(
						null);
				m2jw.forEach(m2j -> {
					List<Itc04ItemsDto> docListItems = m2j.getItc04ItemsDto();

					if (CollectionUtils.isEmpty(docListItems)) {
						Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

						entity.setGstin(dto.getGstin());
						entity.setReturnPeriod(dto.getReturnPeriod());
						String retPeriod = dto.getReturnPeriod();

						String s1 = retPeriod.substring(2, 6);
						String s2 = retPeriod.substring(0, 2);
						String s3 = s1 + s2;
						Integer s4 = Integer.valueOf(s3);
						entity.setqDerReturnPeriod(s4);

						entity.setJwStateCode(m2j.getJwStcd());
						entity.setChallanNum(m2j.getChNum());
						entity.setChallanDate(m2j.getChDate());
						entity.setCreatedBy(APIConstants.SYSTEM);
						entity.setTableNum("4");
						entity.setModifiedOn(LocalDateTime.now());
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entities.add(entity);
					} else {
						docListItems.forEach(itcDto -> {
							Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

							entity.setGstin(dto.getGstin());
							entity.setReturnPeriod(dto.getReturnPeriod());
							String retPeriod = dto.getReturnPeriod();

							String s1 = retPeriod.substring(2, 6);
							String s2 = retPeriod.substring(0, 2);
							String s3 = s1 + s2;
							Integer s4 = Integer.valueOf(s3);
							entity.setqDerReturnPeriod(s4);

							entity.setJwStateCode(m2j.getJwStcd());
							entity.setChallanDate(m2j.getChDate());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setTableNum("4");
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);
							if (itcDto.getGoodsType().equalsIgnoreCase("7b")) {
								entity.setGoodsType("CG");
							} else {
								entity.setGoodsType("IG");
							}
							entity.setProductDesc(itcDto.getDesc());
							entity.setItmUqc(itcDto.getUqc());
							entity.setItmQty(itcDto.getQty());
							entity.setTaxValue(itcDto.getTxval());
							entity.setCgst(itcDto.getTxc());
							entity.setSgst(itcDto.getTxs());
							entity.setIgst(itcDto.getTxi());
							entity.setCess(itcDto.getTxcs());

							if (challanNumber.get() != null

									&& challanNumber.get()
											.equals(m2j.getChNum())) {
								entity.setItemNum(itemNumber.get() + 1);
							} else {
								entity.setItemNum(1);
							}
							challanNumber.set(m2j.getChNum());
							entity.setChallanNum(challanNumber.get());

							entities.add(entity);
						});
					}
				});
			}

			if (CollectionUtils.isNotEmpty(table5a)) {
				AtomicInteger itemNumber = new AtomicInteger(1);
				AtomicReference<String> challanNumber = new AtomicReference<String>(
						null);

				table5a.forEach(m2j -> {

					List<Itc04ItemsDto> docListItems = m2j.getItc04ItemsDto();

					if (CollectionUtils.isEmpty(docListItems)) {
						Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

						entity.setGstin(dto.getGstin());
						entity.setReturnPeriod(dto.getReturnPeriod());
						String retPeriod = dto.getReturnPeriod();

						String s1 = retPeriod.substring(2, 6);
						String s2 = retPeriod.substring(0, 2);
						String s3 = s1 + s2;
						Integer s4 = Integer.valueOf(s3);
						entity.setqDerReturnPeriod(s4);

						entity.setJwGstin(m2j.getCtin());
						entity.setJwStateCode(m2j.getJwStCd());
						entity.setCreatedBy(APIConstants.SYSTEM);
						entity.setTableNum("5A");
						entity.setModifiedOn(LocalDateTime.now());
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entities.add(entity);
					} else {
						docListItems.forEach(itcDto -> {
							Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

							entity.setGstin(dto.getGstin());
							entity.setReturnPeriod(dto.getReturnPeriod());
							String retPeriod = dto.getReturnPeriod();

							String s1 = retPeriod.substring(2, 6);
							String s2 = retPeriod.substring(0, 2);
							String s3 = s1 + s2;
							Integer s4 = Integer.valueOf(s3);
							entity.setqDerReturnPeriod(s4);

							entity.setJwGstin(m2j.getCtin());
							entity.setJwStateCode(m2j.getJwStCd());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setTableNum("5A");
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);

							entity.setProductDesc(itcDto.getDesc());
							entity.setItmUqc(itcDto.getUqc());
							entity.setItmQty(itcDto.getQty());
							entity.setOrgChallanNum(itcDto.getOchnum());
							entity.setOrgChallanDate(itcDto.getOchdt());
							entity.setJw2ChallanDate(itcDto.getJw2Chdate());
							entity.setNatureJw(itcDto.getNatjw());
							entity.setLossesUqc(itcDto.getLwuqc());
							entity.setLossesQty(itcDto.getLwqty());
							entity.setItmQty(itcDto.getQty());
							entity.setInvNum(itcDto.getInum());
							entity.setInvDate(itcDto.getIdt());
							entity.setTaxValue(itcDto.getTxval());
							entity.setCgst(itcDto.getTxc());
							entity.setSgst(itcDto.getTxs());
							entity.setIgst(itcDto.getTxi());
							entity.setCess(itcDto.getTxcs());

							if (challanNumber.get() != null
									&& itcDto.getJw2Chnum() != null
									&& itcDto.getJw2Chnum()
											.equals(challanNumber.get())) {
								entity.setItemNum(itemNumber.get() + 1);
							} else {
								entity.setItemNum(1);
							}

							challanNumber.set(itcDto.getJw2Chnum());
							entity.setJw2ChallanNum(challanNumber.get());

							entities.add(entity);
						});
					}
				});
			}

			if (CollectionUtils.isNotEmpty(table5b)) {
				AtomicInteger itemNumber = new AtomicInteger(1);

				AtomicReference<String> challanNumber = new AtomicReference<String>(
						null);

				table5b.forEach(m2j -> {

					List<Itc04ItemsDto> docListItems = m2j.getItc04ItemsDto();

					if (CollectionUtils.isEmpty(docListItems)) {
						Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

						entity.setGstin(dto.getGstin());
						entity.setReturnPeriod(dto.getReturnPeriod());
						String retPeriod = dto.getReturnPeriod();

						String s1 = retPeriod.substring(2, 6);
						String s2 = retPeriod.substring(0, 2);
						String s3 = s1 + s2;
						Integer s4 = Integer.valueOf(s3);
						entity.setqDerReturnPeriod(s4);

						entity.setJwGstin(m2j.getCtin());
						entity.setJwStateCode(m2j.getJwStCd());
						entity.setCreatedBy(APIConstants.SYSTEM);
						entity.setTableNum("5B");
						entity.setModifiedOn(LocalDateTime.now());
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entities.add(entity);
					} else {
						docListItems.forEach(itcDto -> {
							Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

							entity.setGstin(dto.getGstin());
							entity.setReturnPeriod(dto.getReturnPeriod());
							String retPeriod = dto.getReturnPeriod();

							String s1 = retPeriod.substring(2, 6);
							String s2 = retPeriod.substring(0, 2);
							String s3 = s1 + s2;
							Integer s4 = Integer.valueOf(s3);
							entity.setqDerReturnPeriod(s4);

							entity.setJwGstin(m2j.getCtin());
							entity.setJwStateCode(m2j.getJwStCd());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setTableNum("5B");
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);

							entity.setProductDesc(itcDto.getDesc());
							entity.setItmUqc(itcDto.getUqc());
							entity.setItmQty(itcDto.getQty());
							entity.setOrgChallanNum(itcDto.getOchnum());
							entity.setOrgChallanDate(itcDto.getOchdt());
							entity.setJw2ChallanDate(itcDto.getJw2Chdate());
							entity.setNatureJw(itcDto.getNatjw());
							entity.setLossesUqc(itcDto.getLwuqc());
							entity.setLossesQty(itcDto.getLwqty());
							entity.setInvNum(itcDto.getInum());
							entity.setInvDate(itcDto.getIdt());
							entity.setTaxValue(itcDto.getTxval());
							entity.setCgst(itcDto.getTxc());
							entity.setSgst(itcDto.getTxs());
							entity.setIgst(itcDto.getTxi());
							entity.setCess(itcDto.getTxcs());

							if (challanNumber != null
									&& itcDto.getJw2Chnum() != null
									&& itcDto.getJw2Chnum()
											.equals(challanNumber.get())) {
								entity.setItemNum(itemNumber.get() + 1);
							} else {
								entity.setItemNum(1);
							}
							challanNumber.set(itcDto.getJw2Chnum());
							entity.setJw2ChallanNum(challanNumber.get());
							entities.add(entity);
						});
					}
				});
			}

			if (CollectionUtils.isNotEmpty(table5c)) {

				AtomicInteger itemSerial = new AtomicInteger(1);

				AtomicReference<String> challanNumber = new AtomicReference<String>(
						null);

				table5c.forEach(m2j -> {

					List<Itc04ItemsDto> docListItems = m2j.getItc04ItemsDto();

					if (CollectionUtils.isEmpty(docListItems)) {
						Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

						entity.setGstin(dto.getGstin());
						entity.setReturnPeriod(dto.getReturnPeriod());
						String retPeriod = dto.getReturnPeriod();

						String s1 = retPeriod.substring(2, 6);
						String s2 = retPeriod.substring(0, 2);
						String s3 = s1 + s2;
						Integer s4 = Integer.valueOf(s3);
						entity.setqDerReturnPeriod(s4);

						entity.setJwGstin(m2j.getCtin());
						entity.setJwStateCode(m2j.getJwStCd());
						entity.setCreatedBy(APIConstants.SYSTEM);
						entity.setTableNum("5C");
						entity.setModifiedOn(LocalDateTime.now());
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entities.add(entity);
					} else {
						docListItems.forEach(itcDto -> {
							Itc04InvoicesEntity entity = new Itc04InvoicesEntity();

							entity.setGstin(dto.getGstin());
							entity.setReturnPeriod(dto.getReturnPeriod());
							String retPeriod = dto.getReturnPeriod();

							String s1 = retPeriod.substring(2, 6);
							String s2 = retPeriod.substring(0, 2);
							String s3 = s1 + s2;
							Integer s4 = Integer.valueOf(s3);
							entity.setqDerReturnPeriod(s4);

							entity.setJwGstin(m2j.getCtin());
							entity.setJwStateCode(m2j.getJwStCd());
							entity.setCreatedBy(APIConstants.SYSTEM);
							entity.setTableNum("5C");
							entity.setModifiedOn(LocalDateTime.now());
							LocalDateTime convertNow = EYDateUtil
									.toUTCDateTimeFromLocal(
											LocalDateTime.now());
							entity.setCreatedOn(convertNow);

							entity.setProductDesc(itcDto.getDesc());
							entity.setItmUqc(itcDto.getUqc());
							entity.setItmQty(itcDto.getQty());
							entity.setOrgChallanNum(itcDto.getOchnum());
							entity.setOrgChallanDate(itcDto.getOchdt());
							entity.setJw2ChallanDate(itcDto.getJw2Chdate());
							entity.setNatureJw(itcDto.getNatjw());
							entity.setLossesUqc(itcDto.getLwuqc());
							entity.setLossesQty(itcDto.getLwqty());
							entity.setInvDate(itcDto.getIdt());
							entity.setTaxValue(itcDto.getTxval());
							entity.setCgst(itcDto.getTxc());
							entity.setSgst(itcDto.getTxs());
							entity.setIgst(itcDto.getTxi());
							entity.setCess(itcDto.getTxcs());

							if (challanNumber != null
									&& itcDto.getInum() != null
									&& itcDto.getInum()
											.equals(challanNumber.get())) {
								entity.setItemNum(itemSerial.get() + 1);

							} else {

								entity.setItemNum(1);
							}

							challanNumber.set(itcDto.getInum());
							entity.setInvNum(challanNumber.get());

							entities.add(entity);
						});
					}
				});
			}
		} catch (Exception e) {
			String msg = "Failed to Parse Itc04  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	public static void main(String[] args) {
		AtomicInteger serial = new AtomicInteger(1);
		String s1 = "DLVP OO1";
		System.out.println(1);
		String s2 = "DLVP OO1";

		if (s1.equals(s1)) {
			System.out.println(serial.incrementAndGet());

		}
		if (s1.equals(s1)) {
			System.out.println(serial.incrementAndGet());

		}
		// String s2=s1.replaceAll("[^0-9]", "");

	}

}
