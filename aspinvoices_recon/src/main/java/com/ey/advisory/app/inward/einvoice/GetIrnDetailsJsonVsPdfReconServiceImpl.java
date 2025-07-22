
package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailPayloadEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnDtlPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ItemDto;
import com.ey.advisory.einv.dto.PrecDocument;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetIrnDetailsJsonVsPdfReconServiceImpl")
public class GetIrnDetailsJsonVsPdfReconServiceImpl
		implements GetIrnDetailsJsonVsPdfReconService {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gSTNAuthTokenService;

	@Autowired
	@Qualifier("GetIrnListingRepository")
	public GetIrnListingRepository listingRepo;

	@Autowired
	@Qualifier("GetIrnDtlPayloadRepository")
	private GetIrnDtlPayloadRepository payloadRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public JsonValidatorRespDto getIrnforRecon(String irn, String groupCode, String recipientGsString) {

		try {
			JsonValidatorRespDto jsonRespDto = new JsonValidatorRespDto();
	/*		String apiResp = "{\"data\":{\"AckNo\": 112010000002315,\"AckDt\": \"2020-08-05 15:18:00\",\"Irn\": \"11f8ef701fe294d4a14aad0b12457e62775d0fdc41a0acf05b74fbb2ddc47acb\",\"SignedInvoice\":\"eyJhbGciOiJSUzI1NiIsImtpZCI6IjExNUY0NDI2NjE3QTc5MzhCRTFCQTA2REJFRTkxQTQyNzU4NEVEQUIiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJFVjlFSm1GNmVUaS1HNkJ0dnVrYVFuV0U3YXMifQ.eyJkYXRhIjoie1wiQWNrTm9cIjoxMTIwMTAwMDAwMDIzMTUsXCJBY2tEdFwiOlwiMjAyMC0wOC0wNSAxNToxODowMFwiLFwiSXJuXCI6XCIxMWY4ZWY3MDFmZTI5NGQ0YTE0YWFkMGIxMjQ1N2U2Mjc3NWQwZmRjNDFhMGFjZjA1Yjc0ZmJiMmRkYzQ3YWNiXCIsXCJWZXJzaW9uXCI6XCIxLjAxXCIsXCJUcmFuRHRsc1wiOntcIlRheFNjaFwiOlwiR1NUXCIsXCJTdXBUeXBcIjpcIkIyQlwiLFwiUmVnUmV2XCI6XCJOXCIsXCJJZ3N0T25JbnRyYVwiOlwiWVwifSxcIkRvY0R0bHNcIjp7XCJUeXBcIjpcIklOVlwiLFwiTm9cIjpcIkRELTIwMjAwODA0LTlcIixcIkR0XCI6XCIwNC8wOC8yMDIwXCJ9LFwiU2VsbGVyRHRsc1wiOntcIkdzdGluXCI6XCIzN0FSWlBUNDM4NFExTVRcIixcIkxnbE5tXCI6XCIgQUJDIGNvbXBhbnkgcHZ0IGx0ZFwiLFwiVHJkTm1cIjpcInZpa2FzXCIsXCJBZGRyMVwiOlwiVEUgaFwiLFwiQWRkcjJcIjpcImFiY1wiLFwiTG9jXCI6XCJCYW5nYWxvcmVcIixcIlBpblwiOjUxNTMxMSxcIlN0Y2RcIjpcIjM3XCIsXCJQaFwiOlwiOTczODk3MTk3MFwiLFwiRW1cIjpcInZpa2FzQGdtYWlsLmNvbVwifSxcIkJ1eWVyRHRsc1wiOntcIkdzdGluXCI6XCIxMUFBQUNUMzkwNEYxWlpcIixcIkxnbE5tXCI6XCJYWVogY29tcGFueSBwdnQgbHRkXCIsXCJQb3NcIjpcIjM3XCIsXCJBZGRyMVwiOlwiN3RoIGJsb2NrLCBrdXZlbXB1IGxheW91dFwiLFwiTG9jXCI6XCJHQU5ESElOQUdBUlwiLFwiUGluXCI6NzM3MTAxLFwiU3RjZFwiOlwiMTFcIn0sXCJEaXNwRHRsc1wiOntcIk5tXCI6XCJuYW1lIG9mIHRoZSBjb21wYW55IGZyb20gd2hpY2ggZ29vZHMgZGlzcGF0Y2hlZFwiLFwiQWRkcjFcIjpcImFkZHJlc3NcIixcIkFkZHIyXCI6XCJCYW5nYWxvcmVcIixcIkxvY1wiOlwia3prXCIsXCJQaW5cIjo2OTA1MTMsXCJTdGNkXCI6XCIzMlwifSxcIlNoaXBEdGxzXCI6e1wiR3N0aW5cIjpcIjMyRElVUFAxMTc1RzFaMVwiLFwiTGdsTm1cIjpcInNoaXAgdHJhZGVcIixcIlRyZE5tXCI6XCJ2aWthc1wiLFwiQWRkcjFcIjpcInNoaXAgYiBub1wiLFwiQWRkcjJcIjpcIkJhbmdhbG9yZVwiLFwiTG9jXCI6XCJCYW5nYWxvcmVcIixcIlBpblwiOjY5MDUxMyxcIlN0Y2RcIjpcIjMyXCJ9LFwiSXRlbUxpc3RcIjpbe1wiSXRlbU5vXCI6MSxcIlNsTm9cIjpcIjFcIixcIklzU2VydmNcIjpcIk5cIixcIlByZERlc2NcIjpcIlN0ZWVsXCIsXCJIc25DZFwiOlwiMTAwMVwiLFwiUXR5XCI6MTAsXCJVbml0XCI6XCJCQUdcIixcIlVuaXRQcmljZVwiOjIwMC4wMCxcIlRvdEFtdFwiOjIwMDAuMDAsXCJEaXNjb3VudFwiOjEwLFwiQXNzQW10XCI6MTk5MC4wMCxcIkdzdFJ0XCI6MTIuMDAsXCJJZ3N0QW10XCI6MjM4LjgsXCJDZ3N0QW10XCI6MCxcIlNnc3RBbXRcIjowLFwiQ2VzUnRcIjo1LFwiQ2VzQW10XCI6OTkuNSxcIkNlc05vbkFkdmxBbXRcIjoxMCxcIlN0YXRlQ2VzUnRcIjoxMixcIlN0YXRlQ2VzQW10XCI6MjM4LjgwLFwiU3RhdGVDZXNOb25BZHZsQW10XCI6NSxcIk90aENocmdcIjoxMCxcIlRvdEl0ZW1WYWxcIjoyNTkyLjF9LHtcIkl0ZW1Ob1wiOjIsXCJTbE5vXCI6XCIyXCIsXCJJc1NlcnZjXCI6XCJOXCIsXCJQcmREZXNjXCI6XCJTdGVlbFwiLFwiSHNuQ2RcIjpcIjEwMDFcIixcIlF0eVwiOjEwLFwiVW5pdFwiOlwiQkFHXCIsXCJVbml0UHJpY2VcIjoyMDAuMDAsXCJUb3RBbXRcIjoyMDAwLjAwLFwiRGlzY291bnRcIjoxMCxcIkFzc0FtdFwiOjE5OTAuMDAsXCJHc3RSdFwiOjEyLjAwLFwiSWdzdEFtdFwiOjIzOC44LFwiQ2dzdEFtdFwiOjAsXCJTZ3N0QW10XCI6MCxcIkNlc1J0XCI6NSxcIkNlc0FtdFwiOjk5LjUsXCJDZXNOb25BZHZsQW10XCI6MTAsXCJTdGF0ZUNlc1J0XCI6MTIsXCJTdGF0ZUNlc0FtdFwiOjIzOC44MCxcIlN0YXRlQ2VzTm9uQWR2bEFtdFwiOjUsXCJPdGhDaHJnXCI6MTAsXCJUb3RJdGVtVmFsXCI6MjU5Mi4xfSx7XCJJdGVtTm9cIjozLFwiU2xOb1wiOlwiM1wiLFwiSXNTZXJ2Y1wiOlwiTlwiLFwiUHJkRGVzY1wiOlwiU3RlZWxcIixcIkhzbkNkXCI6XCIxMDAxXCIsXCJRdHlcIjoxMCxcIlVuaXRcIjpcIkJBR1wiLFwiVW5pdFByaWNlXCI6MjAwLjAwLFwiVG90QW10XCI6MjAwMC4wMCxcIkRpc2NvdW50XCI6MTAsXCJBc3NBbXRcIjoxOTkwLjAwLFwiR3N0UnRcIjoxMi4wMCxcIklnc3RBbXRcIjoyMzguOCxcIkNnc3RBbXRcIjowLFwiU2dzdEFtdFwiOjAsXCJDZXNSdFwiOjUsXCJDZXNBbXRcIjo5OS41LFwiQ2VzTm9uQWR2bEFtdFwiOjEwLFwiU3RhdGVDZXNSdFwiOjEyLFwiU3RhdGVDZXNBbXRcIjoyMzguODAsXCJTdGF0ZUNlc05vbkFkdmxBbXRcIjo1LFwiT3RoQ2hyZ1wiOjEwLFwiVG90SXRlbVZhbFwiOjI1OTIuMX1dLFwiVmFsRHRsc1wiOntcIkFzc1ZhbFwiOjU5NzAuMCxcIkNnc3RWYWxcIjowLFwiU2dzdFZhbFwiOjAsXCJJZ3N0VmFsXCI6NzE2LjQsXCJDZXNWYWxcIjozMjguNSxcIlN0Q2VzVmFsXCI6NzMxLjQsXCJSbmRPZmZBbXRcIjowLFwiVG90SW52VmFsXCI6Nzc3Ni4zfSxcIlJlZkR0bHNcIjp7XCJJbnZSbVwiOlwiMTIzXCIsXCJQcmVjRG9jRHRsc1wiOlt7XCJJbnZOb1wiOlwiQUJDXCIsXCJJbnZEdFwiOlwiMDIvMDIvMjAyMFwiLFwiT3RoUmVmTm9cIjpcIjEyQVwifV0sXCJDb250ckR0bHNcIjpbe1wiUmVjQWR2UmVmclwiOlwiMTIzXCIsXCJSZWNBZHZEdFwiOlwiMTIvMDIvMjAyMFwiLFwiVGVuZFJlZnJcIjpcImFiY1wiLFwiQ29udHJSZWZyXCI6XCJhYmNcIixcIkV4dFJlZnJcIjpcImFiY1wiLFwiUHJvalJlZnJcIjpcImFiY1wiLFwiUE9SZWZyXCI6XCJhYmNcIixcIlBPUmVmRHRcIjpcIjEyLzAyLzIwMjBcIn1dfSxcIkFkZGxEb2NEdGxzXCI6W3tcIlVybFwiOlwiaHR0cHM6Ly9laW52LWFwaXNhbmRib3gubmljLmluL2dzdGNvcmVfdGVzdC92MS4wMS9pbnZvaWNlXCIsXCJEb2NzXCI6XCJ2aWthc1wiLFwiSW5mb1wiOlwidmlrYXNcIn1dLFwiRXdiRHRsc1wiOntcIlRyYW5zSWRcIjpcIjEyQVdHUFY3MTA3QjFaMVwiLFwiVHJhbnNOYW1lXCI6XCJYWVogRVhQT1JUU1wiLFwiVHJhbnNNb2RlXCI6XCIxXCIsXCJEaXN0YW5jZVwiOjEwMCxcIlRyYW5zRG9jTm9cIjpcIkRPQzAxXCIsXCJUcmFuc0RvY0R0XCI6XCIwNC8wOC8yMDIwXCIsXCJWZWhOb1wiOlwia2ExMjM0NTZcIixcIlZlaFR5cGVcIjpcIlJcIn19IiwiaXNzIjoiTklDIn0.oesnTXdXgOEeRjYr6bRQ-_Ks-bnIpwtj7Zx8phzfjL6vsfuGqBokILz6ai0NHFKRxiX_bTLrgrWmwXyBdEFmt88myf4n-NP5JvwqFx4OIf0gYMFTKGLx4AQsxwXER836FDxyS33K_7Erkm7_yHsITR5sBkYrZYOWimYl5cgh4EFN2mEq0B8oIp9pSXAU2RGvuirV6Rnl902sWj1Zv_2UK8e9C7cS7maeuFvEgAHrwBjxqLVvRGDz93oRVgQcavhdNTmBr8LQo2yRQkwtZKCMY_NGDsIoJx3orAKEUE7D1RbAM6xh-uxGOlqxur50826y0sk6OuG2WB9K5g5gumxIpg\",\"SignedQRCode\":\"eyJhbGciOiJSUzI1NiIsImtpZCI6IjExNUY0NDI2NjE3QTc5MzhCRTFCQTA2REJFRTkxQTQyNzU4NEVEQUIiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJFVjlFSm1GNmVUaS1HNkJ0dnVrYVFuV0U3YXMifQ.eyJkYXRhIjoie1wiU2VsbGVyR3N0aW5cIjpcIjM3QVJaUFQ0Mzg0UTFNVFwiLFwiQnV5ZXJHc3RpblwiOlwiMTFBQUFDVDM5MDRGMVpaXCIsXCJEb2NOb1wiOlwiREQtMjAyMDA4MDQtOVwiLFwiRG9jVHlwXCI6XCJJTlZcIixcIkRvY0R0XCI6XCIwNC8wOC8yMDIwXCIsXCJUb3RJbnZWYWxcIjo3Nzc2LjMsXCJJdGVtQ250XCI6MyxcIk1haW5Ic25Db2RlXCI6XCIxMDAxXCIsXCJJcm5cIjpcIjExZjhlZjcwMWZlMjk0ZDRhMTRhYWQwYjEyNDU3ZTYyNzc1ZDBmZGM0MWEwYWNmMDViNzRmYmIyZGRjNDdhY2JcIn0iLCJpc3MiOiJOSUMifQ.fya8oD85f2_K8pDWSf8N94_T24O1lA9OPpIuUwk14el_r1lhL13OFxGkklhiewSMUom8DvO9JKu4jjz2l5farRTJhiBWJ43EtEky2SLzRhJf23JYW_6PyLErYL2RTzv2PlZ75eXIBZzPkxc2erCx61T50oHmExLgl1Q6HclvgiQUAVxysq1VFv96zEZVH8I0xDNqjdvqdtsW74ZHqzpV28kDIvuyV4Z5j3bR39GE6YKMetext_x3bJ4Wt4F1z3DOzfUjuKGdEjP0fTSwNg1RpiDoH4wcaMP7RJgtbQYXn4j3YoppCEw916AmbihiT2gSODPn04vhCbBecI7oOZvxpw\",\"Status\": \"CNL\",\"EwbNo\": 191008688443,\"EwbDt\": \"2020-08-05 15:18:00\"},\"RequestDate\": \"2020-10-16 13:50:00\"}";
			jsonRespDto = findTheIrnJsonRespDetails(apiResp,irn);
		*/	
			LOGGER.debug(" IRN {} ",irn);
			
			GetIrnListEntity entity = listingRepo.getByIrnNum(irn);

			if (entity != null) {
				// TODO fetch the values and convert in dto

				GetIrnDetailPayloadEntity payloadEntity = payloadRepo
						.findByIrnAndIrnStatus(irn, entity.getIrnSts());
				
				LOGGER.error(" fetching payload ");
				
				jsonRespDto = findTheIrnJsonRespDetails(
						GenUtil.convertClobtoString(payloadEntity.getPayload()),
						irn);

			} else {
				//pass the recipientGstin in below method

				jsonRespDto = fetchIrnDetailsFrmGstnCall(irn,groupCode, recipientGsString);

			}
			
			return jsonRespDto;
		} catch (Exception ex) {
			String msg = "Error while fetching irn details ";
			LOGGER.error(msg + irn);
			throw new AppException(ex);

		}
	}

	private JsonValidatorRespDto fetchIrnDetailsFrmGstnCall(String irn,
			String groupCode, String recipientGsString) {

		JsonValidatorRespDto apiResp = null;
		Map<String, String> retMap = gSTNAuthTokenService
				.getAuthTokenStatusForGroup();

		String activeGstins = retMap.entrySet().stream()
				.filter(entry -> "A".equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);

		if (activeGstins == null || activeGstins.isEmpty()) {
			throw new AppException("Auth Tokens for all GSTINs are inactive. "
					+ "Please activate Auth Token for any one GSTIN and retry.");
		}
		if (activeGstins.contains(recipientGsString)) {

			apiResp = getIrnDtls(irn, groupCode, recipientGsString);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Irn Search " + " Response %s from irnNum  %s :",
						apiResp, irn);
				LOGGER.debug(msg);
			}
		}
		return apiResp;
	}

	private JsonValidatorRespDto getIrnDtls(String irn, String groupCode,
			String activeGstin) {

		JsonValidatorRespDto dto = new JsonValidatorRespDto();

		Gson gson = new Gson();

		APIParam param1 = new APIParam("irn", irn);
		APIParam param2 = new APIParam("gstin", activeGstin);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GET_IRN_DTL, param1, param2);

		APIResponse apiResponse = apiExecutor.execute(params, null);

		LOGGER.debug("apiResponse --> {} for irn {} and for groupCode {} ",
				apiResponse, irn, groupCode);

		if (apiResponse.isSuccess()) {

			String apiResp = apiResponse.getResponse();

			dto = findTheIrnJsonRespDetails(apiResp, irn);

		} else {
			String msg = apiResponse.getError().getErrorCode()
					+ apiResponse.getError().getErrorDesc();
			dto.setErrMsg(msg);
		}

		return dto;

	}

	private BigDecimal calculateSumm(BigDecimal a, BigDecimal b, BigDecimal c,
			BigDecimal d) {

		return (a.add(b.add(c.add(d))));

	}

	private BigDecimal[] calculateCessValue(EinvoiceRequestDto respDto,
			BigDecimal[] arr1, List<String> hsnCode, List<BigDecimal> quantity,
			List<BigDecimal> unitPrice, List<BigDecimal> itemAmt)

	{
		if (respDto.getItemList() != null) {
			BigDecimal maxAmount = null;
			String hsnWithMaxAmount = null;
		
			for (ItemDto item : respDto.getItemList()) {
				
				arr1[0] = arr1[0].add(item.getCesAmt() != null
						? item.getCesAmt() : BigDecimal.ZERO);
				arr1[1] = arr1[1].add(item.getCesNonAdvlAmt() != null
						? item.getCesNonAdvlAmt() : BigDecimal.ZERO);
				arr1[2] = arr1[2].add(item.getStateCesAmt() != null
						? item.getStateCesAmt() : BigDecimal.ZERO);
				arr1[3] = arr1[3].add(item.getStateCesNonAdvlAmt() != null
						? item.getStateCesNonAdvlAmt() : BigDecimal.ZERO);
				
				if (item.getHsnCd() != null) {

					BigDecimal currentAmount = item.getTotAmt().abs();
					String currentHsnNumber = item.getHsnCd();
					if (currentAmount != null && currentHsnNumber != null) {
						if (maxAmount == null
								|| currentAmount.compareTo(maxAmount) > 0) {
							maxAmount = currentAmount;
							hsnWithMaxAmount = currentHsnNumber;
						}
					}
					
				}
				if (item.getQty() != null) {
					quantity.add(item.getQty());
				}
				if (item.getUnitPrice() != null) {
					unitPrice.add(item.getUnitPrice());
				}
				if (item.getTotAmt() != null) {
					itemAmt.add(item.getTotAmt());
				}
			}
			hsnCode.add(hsnWithMaxAmount);
		}

		return arr1;
	}

	private JsonValidatorRespDto findTheIrnJsonRespDetails(String apiResp,
			String irn) {

		JsonValidatorRespDto dto = new JsonValidatorRespDto();

		Gson gson = new Gson();
		
		JsonObject reqObj = null;
		
		if(JsonParser.parseString(apiResp).getAsJsonObject()
				.has("data"))
		{	
		 reqObj = JsonParser.parseString(apiResp).getAsJsonObject()
				.get("data").getAsJsonObject();
		}
		else
		{
		reqObj = JsonParser.parseString(apiResp).getAsJsonObject();
		}
		
		dto.setIrnJsonPayload(reqObj.toString());
		GetIrnDtlsRespDto respDto = gson.fromJson(reqObj,
				GetIrnDtlsRespDto.class);

		Claims claims = JwtParserUtility.getJwtBodyWithoutSignature(
				reqObj.get("SignedInvoice").getAsString());

		Gson gsn = GsonUtil.gsonInstanceWithEWBDateFormat();
		
		
		String claimResp = gson.toJson(claims);
		
/*		String claimResp = "{\"AckNo\":112010000002315,\"AckDt\":\"2020-08-05 15:18:00\",\"Irn\":\"11f8ef701fe294d4a14aad0b12457e62775d0fdc41a0acf05b74fbb2ddc47acb\",\"Version\":\"1.01\",\"TranDtls\":{\"TaxSch\":\"GST\",\"SupTyp\":\"B2B\",\"RegRev\":\"N\",\"IgstOnIntra\":\"Y\"},\"DocDtls\":{\"Typ\":\"INV\",\"No\":\"DD-20200804-9\",\"Dt\":\"04/08/2020\"},\"SellerDtls\":{\"Gstin\":\"37ARZPT4384Q1MT\",\"LglNm\":\" ABC company pvt ltd\",\"TrdNm\":\"vikas\",\"Addr1\":\"TE h\",\"Addr2\":\"abc\",\"Loc\":\"Bangalore\",\"Pin\":515311,\"Stcd\":\"37\",\"Ph\":\"9738971970\",\"Em\":\"vikas@gmail.com\"},\"BuyerDtls\":{\"Gstin\":\"11AAACT3904F1ZZ\",\"LglNm\":\"XYZ company pvt ltd\",\"Pos\":\"37\",\"Addr1\":\"7th bloc\",\"Loc\":\"GANDHINAGAR\",\"Pin\":737101,\"Stcd\":\"11\"},\"DispDtls\":{\"Nm\":\"name of the company from which goods dispatched\",\"Addr1\":\"address\",\"Addr2\":\"Bangalore\",\"Loc\":\"kzk\",\"Pin\":690513,\"Stcd\":\"32\"},\"ShipDtls\":{\"Gstin\":\"32DIUPP1175G1Z1\",\"LglNm\":\"ship trade\",\"TrdNm\":\"vikas\",\"Addr1\":\"ship b no\",\"Addr2\":\"Bangalore\",\"Loc\":\"Bangalore\",\"Pin\":690513,\"Stcd\":\"32\"},\"ItemList\":[{\"ItemNo\":1,\"SlNo\":\"1\",\"IsServc\":\"N\",\"PrdDesc\":\"Steel\",\"HsnCd\":\"1001\",\"Qty\":10,\"Unit\":\"BAG\",\"UnitPrice\":200.00,\"TotAmt\":2000.00,\"Discount\":10,\"AssAmt\":1990.00,\"GstRt\":12.00,\"IgstAmt\":238.8,\"CgstAmt\":0,\"SgstAmt\":0,\"CesRt\":5,\"CesAmt\":99.5,\"CesNonAdvlAmt\":10,\"StateCesRt\":12,\"StateCesAmt\":238.80,\"StateCesNonAdvlAmt\":5,\"OthChrg\":10,\"TotItemVal\":2592.1},{\"ItemNo\":2,\"SlNo\":\"2\",\"IsServc\":\"N\",\"PrdDesc\":\"Steel\",\"HsnCd\":\"1001\",\"Qty\":10,\"Unit\":\"BAG\",\"UnitPrice\":200.00,\"TotAmt\":2000.00,\"Discount\":10,\"AssAmt\":1990.00,\"GstRt\":12.00,\"IgstAmt\":238.8,\"CgstAmt\":0,\"SgstAmt\":0,\"CesRt\":5,\"CesAmt\":99.5,\"CesNonAdvlAmt\":10,\"StateCesRt\":12,\"StateCesAmt\":238.80,\"StateCesNonAdvlAmt\":5,\"OthChrg\":10,\"TotItemVal\":2592.1},{\"ItemNo\":3,\"SlNo\":\"3\",\"IsServc\":\"N\",\"PrdDesc\":\"Steel\",\"HsnCd\":\"1001\",\"Qty\":10,\"Unit\":\"BAG\",\"UnitPrice\":200.00,\"TotAmt\":2000.00,\"Discount\":10,\"AssAmt\":1990.00,\"GstRt\":12.00,\"IgstAmt\":238.8,\"CgstAmt\":0,\"SgstAmt\":0,\"CesRt\":5,\"CesAmt\":99.5,\"CesNonAdvlAmt\":10,\"StateCesRt\":12,\"StateCesAmt\":238.80,\"StateCesNonAdvlAmt\":5,\"OthChrg\":10,\"TotItemVal\":2592.1}],\"ValDtls\":{\"AssVal\":5970.0,\"CgstVal\":0,\"SgstVal\":0,\"IgstVal\":716.4,\"CesVal\":328.5,\"StCesVal\":731.4,\"RndOffAmt\":0,\"TotInvVal\":7776.3},\"RefDtls\":{\"InvRm\":\"123\",\"PrecDocDtls\":[{\"InvNo\":\"ABC\",\"InvDt\":\"02/02/2020\",\"OthRefNo\":\"12A\"}],\"ContrDtls\":[{\"RecAdvRefr\":\"123\",\"RecAdvDt\":\"12/02/2020\",\"TendRefr\":\"abc\",\"ContrRefr\":\"abc\",\"ExtRefr\":\"abc\",\"ProjRefr\":\"abc\",\"PORefr\":\"abc\",\"PORefDt\":\"12/02/2020\"}]},\"EwbDtls\":{\"TransId\":\"12AWGPV7107B1Z1\",\"TransName\":\"XYZ EXPORTS\",\"TransMode\":\"1\",\"Distance\":100,\"TransDocNo\":\"DOC01\",\"TransDocDt\":\"04/08/2020\",\"VehNo\":\"ka123456\",\"VehType\":\"R\"}}";
		
		Gson gsn = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject requestObject = JsonParser.parseString(claimResp)
				.getAsJsonObject();*/

		String requestObject = JsonParser.parseString(claimResp)
				.getAsJsonObject().get("data").getAsString();

		EinvoiceRequestDto respSignedDto = gsn.fromJson(requestObject,
				EinvoiceRequestDto.class);

		dto.setRecipientGstin(respSignedDto.getBuyerDtls().getGstin());
		dto.setDocType(respSignedDto.getDocDtls().getTyp() != null
				? respSignedDto.getDocDtls().getTyp() : null);
		dto.setDocNum(respSignedDto.getDocDtls().getNo() != null
				? respSignedDto.getDocDtls().getNo() : null);
		dto.setIrn(irn);

		dto.setDocDate(respSignedDto.getDocDtls().getDt() != null
				? respSignedDto.getDocDtls().getDt().toString() : null);

		dto.setIrnDate(!Strings.isNullOrEmpty(respDto.getAckDt())
				? LocalDateTime.parse(respDto.getAckDt(), formatter).plusSeconds(00)
				: null);

		dto.setSupplierGstin(respSignedDto.getSellerDtls().getGstin() != null
				? respSignedDto.getSellerDtls().getGstin() : null);

		dto.setTotalInvValue(respSignedDto.getValDtls().getTotInvVal() != null
				? respSignedDto.getValDtls().getTotInvVal() : BigDecimal.ZERO);

		dto.setIrnStatus("ACT".equalsIgnoreCase(respDto.getIrnSts()) ? "Active"
				: "Cancelled");

		dto.setIrnCanDate(!Strings.isNullOrEmpty(respDto.getCnlDt())
				? LocalDateTime.parse(respDto.getCnlDt(), formatter)
				: null);

		dto.setTaxableValue(respSignedDto.getValDtls().getAssVal() != null
				? respSignedDto.getValDtls().getAssVal() : BigDecimal.ZERO);
		BigDecimal igst = respSignedDto.getValDtls().getIgstVal() != null
				? respSignedDto.getValDtls().getIgstVal() : BigDecimal.ZERO;

		BigDecimal sgst = respSignedDto.getValDtls().getSgstVal() != null
				? respSignedDto.getValDtls().getSgstVal() : BigDecimal.ZERO;

		BigDecimal cgst = respSignedDto.getValDtls().getCgstVal() != null
				? respSignedDto.getValDtls().getCgstVal() : BigDecimal.ZERO;
		
				
		List<String> puchrOder = new ArrayList<>();
		
		if(respSignedDto.getRefDtls()!=null)
		{
			if(respSignedDto.getRefDtls().getPrecDocDtls()!=null && !respSignedDto.getRefDtls().getPrecDocDtls().isEmpty())
			{
				for(PrecDocument precDoc : respSignedDto.getRefDtls().getPrecDocDtls())
				{
					puchrOder.add(precDoc.getOthRefNo());
				}
			}
		}
		
		
		dto.setPurchaseOrderNo((!puchrOder.isEmpty()?String.join("|", puchrOder):null));

		dto.setIGst(igst);
		dto.setSGst(sgst);
		dto.setCGst(cgst);

		dto.setRcm(respSignedDto.getTranDtls().getRegRev() != null
				? respSignedDto.getTranDtls().getRegRev() : "N");

		dto.setPos(respSignedDto.getBuyerDtls().getPos() != null
				? respSignedDto.getBuyerDtls().getPos() : null);

		BigDecimal arr[] = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO };

		List<String> hsnBarCode = new ArrayList<>();
		List<BigDecimal> quantity = new ArrayList<>();
		List<BigDecimal> unitPrice = new ArrayList<>();
		List<BigDecimal> itmAmount = new ArrayList<>();

		arr = calculateCessValue(respSignedDto, arr, hsnBarCode, quantity,
				unitPrice, itmAmount);

		BigDecimal invoiceCessAdvaloremAmount = arr[0];
		BigDecimal invoiceCessSpecificAmount = arr[1];
		BigDecimal invoiceStateCessAdvaloremAmount = arr[2];
		BigDecimal invoiceStateCessSpecificAmount = arr[3];

		BigDecimal invoiceCessAmt = calculateSumm(invoiceCessAdvaloremAmount,
				invoiceCessSpecificAmount, invoiceStateCessAdvaloremAmount,
				invoiceStateCessSpecificAmount);

		dto.setCess(invoiceCessAmt);

		dto.setTotalTax(calculateSumm(invoiceCessAmt, igst, cgst, sgst));

		dto.setItemCnt(respSignedDto.getItemList() != null
				? respSignedDto.getItemList().size() : 0);

		dto.setMainHsnCode(hsnBarCode);
		dto.setQuantity(quantity);
		dto.setUnitPrice(unitPrice);
		dto.setLineItemAmt(itmAmount);
		

		return dto;
	}
}
