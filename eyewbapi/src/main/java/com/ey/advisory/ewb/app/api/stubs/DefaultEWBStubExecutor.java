package com.ey.advisory.ewb.app.api.stubs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.app.api.EWBAPIExecutor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.util.Calendar;

@Component("DefaultEWBStubExecutor")
public class DefaultEWBStubExecutor implements APIExecutor {

	@Autowired
	@Qualifier("EWBAPIExecutor")
	EWBAPIExecutor ewbAPIExecutor;

	private final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm:ss a");

	private final SimpleDateFormat API_DATE_FORMAT1 = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		String api = params.getApiAction();
		APIResponse resp = new APIResponse();
		JsonObject respJson = new JsonObject();
		JsonParser parser = new JsonParser();
		Calendar cal = Calendar.getInstance();
		Date sysDate = new Date();
		cal.setTime(sysDate);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 10);
		if (api.equals(APIIdentifiers.GET_AUTH_TOKEN)) {
			respJson = (JsonObject) parser
					.parse("{\r\n" + "\"status\":\"1\",\r\n"
							+ "\"authtoken\":\"a30WKqvWdLMkPH6M5V9X4AY\",\r\n"
							+ "\"sek\":\"crdHoP73uRaLwSsg4o8RZCHgVrfydvF2K5IW3+kc"
							+ "/rI5SqOVJ52Thf1yCI4j\"\r\n" + "}\r\n" + "");
		} else if (api.equals(APIIdentifiers.GENERATE_EWB)) {
			respJson.addProperty("ewayBillNo", generateRandom(12));
			respJson.addProperty("ewayBillDate",
					API_DATE_FORMAT.format(sysDate));
			respJson.addProperty("validUpto",
					API_DATE_FORMAT1.format(cal.getTime()));
			respJson.addProperty("alert", "");

		} else if (api.equals(APIIdentifiers.CANCEL_EWB)) {
			JsonObject reqDataJson = (JsonObject) parser.parse(reqData);
			respJson.addProperty("ewayBillNo",
					reqDataJson.get("ewbNo").getAsString());
			respJson.addProperty("cancelDate", API_DATE_FORMAT.format(sysDate));
		} else if (api.equals(APIIdentifiers.REJECT_EWB)) {
			JsonObject reqDataJson = (JsonObject) parser.parse(reqData);
			respJson.addProperty("ewayBillNo",
					reqDataJson.get("ewbNo").getAsString());
			respJson.addProperty("ewbRejectedDate",
					API_DATE_FORMAT.format(sysDate));
		} else if (api.equals(APIIdentifiers.UPDATE_VEHICLE_DETAILS)) {
			respJson.addProperty("vehUpdDate", API_DATE_FORMAT.format(sysDate));
			respJson.addProperty("validUpto",
					API_DATE_FORMAT1.format(cal.getTime()));
		} else if (api.equals(APIIdentifiers.EXTEND_VEHICLE_DETAILS)) {
			JsonObject reqDataJson = (JsonObject) parser.parse(reqData);
			respJson.addProperty("ewayBillNo",
					reqDataJson.get("ewbNo").getAsString());
			respJson.addProperty("updatedDate",
					API_DATE_FORMAT.format(sysDate));
			respJson.addProperty("validUpto",
					API_DATE_FORMAT1.format(cal.getTime()));
		} else if (api.equals(APIIdentifiers.GENERATE_CONSOLIDATED_EWB)) {
			respJson.addProperty("cEwbNo", generateRandom(12));
			respJson.addProperty("cEwbDate",
					API_DATE_FORMAT1.format(cal.getTime()));
		} else if (api.equals(APIIdentifiers.UPDATE_TRANSPORTER)) {
			JsonObject reqDataJson = (JsonObject) parser.parse(reqData);
			respJson.addProperty("transporterId",
					reqDataJson.get("transporterId").getAsString());
			respJson.addProperty("transUpdateDate",
					API_DATE_FORMAT1.format(cal.getTime()));
		} else if (api.equals(APIIdentifiers.GET_EWB)) {
			respJson = (JsonObject) parser.parse(
					"{\n\"ewbNo\": \"171001696869\",\n        \"ewayBillDate\": \"28/03/2020 03:04:00 PM\",\n        \"genMode\": \"API\",\n        \"userGstin\": \"29ABYPR4788F1ZJ\",\n        \"supplyType\": \"O\",\n        \"subSupplyType\": \"1  \",\n        \"docType\": \"INV\",\n        \"docNo\": \"123447998\",\n        \"docDate\": \"12/03/2020\",\n        \"fromGstin\": \"29ABYPR4788F1ZJ\",\n        \"fromTrdName\": \"DigiGST\",\n        \"fromAddr1\": \"\",\n        \"fromAddr2\": \"\",\n        \"fromPlace\": \"Bengaluru\",\n        \"fromPincode\": \"560001\",\n        \"fromStateCode\": \"29\",\n        \"toGstin\": \"29AROPR0865P1ZP\",\n        \"toTrdName\": \"\",\n        \"toAddr1\": \"\",\n        \"toAddr2\": \"\",\n        \"toPlace\": \"Banglore\",\n        \"toPincode\": \"560001\",\n        \"toStateCode\": \"29\",\n        \"totalValue\": 0,\n        \"totInvValue\": 7000.00,\n        \"cgstValue\": 0,\n        \"sgstValue\": 0,\n        \"igstValue\": 0,\n        \"cessValue\": 0,\n        \"transporterId\": \"01BQSPA3829E1Z6\",\n        \"transporterName\": \"01BQSPA3829E1Z6\",\n        \"status\": \"ACT\",\n        \"actualDist\": 15,\n        \"noValidDays\": 1,\n        \"validUpto\": \"29/03/2020 11:59:00 PM\",\n        \"extendedTimes\": 0,\n        \"rejectStatus\": \"N\",\n        \"actFromStateCode\": \"29\",\n        \"actToStateCode\": \"29\",\n        \"vehicleType\": \"R\",\n        \"transactionType\": 1,\n        \"otherValue\": 0,\n        \"cessNonAdvolValue\": 0,\n        \"itemList\": [\n            {\n                \"itemNo\": 1,\n                \"productId\": \"0\",\n                \"productName\": \"Earphone\",\n                \"productDesc\": \"Earphone\",\n                \"hsnCode\": \"84212300\",\n                \"quantity\": 1.00,\n                \"qtyUnit\": \"PCS\",\n                \"cgstRate\": 9.000,\n                \"sgstRate\": 9.000,\n                \"igstRate\": 0,\n                \"cessRate\": 0,\n                \"cessNonAdvol\": 0,\n                \"taxableAmount\": 7000.00\n            }\n        ],\n        \"VehiclListDetails\": [\n            {\n                \"updMode\": \"API\",\n                \"vehicleNo\": \"\",\n                \"fromPlace\": \"Bengaluru\",\n                \"fromState\": \"29\",\n                \"tripshtNo\": \"0\",\n                \"userGSTINTransin\": \"29ABYPR4788F1ZJ\",\n                \"enteredDate\": \"28/03/2020 03:04:00 PM\",\n                \"transMode\": \"2\",\n                \"transDocNo\": \"1234\",\n                \"transDocDate\": \"12/03/2020 12:00:00 AM\",\n                \"groupNo\": \"0\"\n            }\n        ]\n    }");
		}
		resp.setResponse(respJson.toString());
		return resp;
	}

	public static String generateRandom(int length) {
		Random random = new Random();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return new String(digits);
	}
}
