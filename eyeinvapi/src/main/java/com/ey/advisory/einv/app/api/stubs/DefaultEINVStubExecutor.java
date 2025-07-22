package com.ey.advisory.einv.app.api.stubs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.util.Calendar;

@Component("DefaultEINVStubExecutor")
public class DefaultEINVStubExecutor implements APIExecutor {

	private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm:ss a");

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
		if (api.equals(APIIdentifiers.GET_EINV_AUTH_TOKEN)) {
			respJson = (JsonObject) parser
					.parse("{\r\n" + "\"status\":\"1\",\r\n"
							+ "\"authtoken\":\"a30WKqvWdLMkPH6M5V9X4AY\",\r\n"
							+ "\"sek\":\"crdHoP73uRaLwSsg4o8RZCHgVrfydvF2K5IW3+kc"
							+ "/rI5SqOVJ52Thf1yCI4j\"\r\n" + "}\r\n" + "");
		} else if (api.equals(APIIdentifiers.GENERATE_EINV)) {
			respJson.addProperty("AckNo", generateRandom(11));
			respJson.addProperty("AckDt", API_DATE_FORMAT.format(sysDate));
			respJson.addProperty("Irn",
					"e2948668b7126f1e27240fcec2e28d891347120b4445f39156a28b9fdc8be4b8");
			respJson.addProperty("Status", "ACT");
			respJson.addProperty("SignedInvoice", getSignedInvoice());
			respJson.addProperty("SignedQRCode", getQrCode());

		} else if (api.equals(APIIdentifiers.CANCEL_EINV)) {
			respJson.addProperty("Irn",
					"e2948668b7126f1e27240fcec2e28d891347120b4445f39156a28b9fdc8be4b8");
			respJson.addProperty("CancelDate",
					API_DATE_FORMAT.format(sysDate).toString());

		}
		resp.setResponse(respJson.toString());
		return resp;
	}

	private String getQrCode() {

		return "eyJhbGciOiJSUzI1NiIsImtpZCI6IjExNUY0NDI2NjE3QTc5MzhCRTFCQTA2REJFRTkxQTQyNzU4NEVEQUIiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJFVjlFSm1GNmVUaS1HNkJ0dnVrYVFuV0U3YXMifQ.eyJkYXRhIjoie1wiU2VsbGVyR3N0aW5cIjpcIjI5QVdHUFY3MTA3QjFaMVwiLFwiQnV5ZXJHc3RpblwiOlwiMzJESVVQUDExNzVHMVoxXCIsXCJEb2NOb1wiOlwiMVwiLFwiRG9jVHlwXCI6XCJJTlZcIixcIkRvY0R0XCI6XCIwMS8wMi8yMDIwXCIsXCJUb3RJbnZWYWxcIjo1LFwiSXRlbUNudFwiOjIsXCJNYWluSHNuQ29kZVwiOlwiMTAwMVwiLFwiSXJuXCI6XCJlMjk0ODY2OGI3MTI2ZjFlMjcyNDBmY2VjMmUyOGQ4OTEzNDcxMjBiNDQ0NWYzOTE1NmEyOGI5ZmRjOGJlNGI4XCJ9IiwiaXNzIjoiTklDIn0.X5ahZ55ltNTVmM8AteRwvUBBrSwIKGCuMvAz5XbcnC8I5RCmmkGAIxiBY6KZbMLz2XpzD5UN0pXxSEx-dNvzo6UbHD_hcBzY_llHHWobch-5F0YDa49iw6KQPfDnPPSJ23qn1b5sowUnVB58U8_Hd91ADb_DgIYgzNc7-clv1QMnRsYC3XR4PWPyHI2dtwJyVxVQKWsOYG5NJnLPRpiB6FbdVLk-23YSH-l-wlzPOkZLIkkruKKGUGwQc6BD6311c48ZVHY3J98f5DMEgNAvlhbdI5l6s2lvPgus1LixEItMZK4lXIcLLtYCQ26ZjDBbRYl1hEw_DEuK3v0FXERs3g";

	}

	private String getSignedInvoice() {
		return "eyJhbGciOiJSUzI1NiIsImtpZCI6IjExNUY0NDI2NjE3QTc5MzhCRTFCQTA2REJFRTkxQTQyNzU4NEVEQUIiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJFVjlFSm1GNmVUaS1HNkJ0dnVrYVFuV0U3YXMifQ.eyJkYXRhIjoie1wiQWNrTm9cIjoxNjEwMDAyODY3NyxcIkFja0R0XCI6XCIyMDIwLTAyLTI5IDE1OjIwOjAwXCIsXCJJcm5cIjpcImUyOTQ4NjY4YjcxMjZmMWUyNzI0MGZjZWMyZTI4ZDg5MTM0NzEyMGI0NDQ1ZjM5MTU2YTI4YjlmZGM4YmU0YjhcIixcIlZlcnNpb25cIjpcIjEuMDBcIixcIlRyYW5EdGxzXCI6e1wiVGF4U2NoXCI6XCJHU1RcIixcIlN1cFR5cFwiOlwiQjJCXCIsXCJSZWdSZXZcIjpcIk5cIn0sXCJEb2NEdGxzXCI6e1wiVHlwXCI6XCJJTlZcIixcIk5vXCI6XCIxXCIsXCJEdFwiOlwiMDEvMDIvMjAyMFwifSxcIlNlbGxlckR0bHNcIjp7XCJHc3RpblwiOlwiMjlBV0dQVjcxMDdCMVoxXCIsXCJMZ2xObVwiOlwiVFJBREVSU1wiLFwiVHJkTm1cIjpcIlZpa2FzZXhwb3J0c1wiLFwiQWRkcjFcIjpcIkJBTkdBTE9SRVwiLFwiQWRkcjJcIjpcIkJBTkdBTE9SRVwiLFwiTG9jXCI6XCJCTkdcIixcIlBpblwiOlwiNTYwMDYwXCIsXCJTdGF0ZVwiOlwiS0FSTkFUQUtBXCIsXCJQaFwiOlwiOTczODk3MTk3MFwiLFwiRW1cIjpcInZpa2FzQGdtYWlsLmNvbVwifSxcIkJ1eWVyRHRsc1wiOntcIkdzdGluXCI6XCIzMkRJVVBQMTE3NUcxWjFcIixcIkxnbE5tXCI6XCJuYW1lXCIsXCJUcmRObVwiOlwidHJhZGVyc1wiLFwiUG9zXCI6XCIyOVwiLFwiQWRkcjFcIjpcImFkZHIxXCIsXCJBZGRyMlwiOlwiYWRkcjJcIixcIkxvY1wiOlwiYmFuZ2xvcmVcIixcIlBpblwiOlwiNjczNTIyXCIsXCJQaFwiOlwiOTczODk3MTk3MFwiLFwiRW1cIjpcIlZJS0FTQEdNQUlMLkNPTVwifSxcIkRpc3BEdGxzXCI6e1wiTm1cIjpcIm5hbWVcIixcIkFkZHIxXCI6XCJhZHJlc3NzXCIsXCJBZGRyMlwiOlwiYWRkcmVzc1wiLFwiTG9jXCI6XCJsb2N0aW9uXCIsXCJQaW5cIjpcIjU2MDYwMFwiLFwiU3RjZFwiOlwiMDFcIn0sXCJTaGlwRHRsc1wiOntcIkdzdGluXCI6XCIyOUFBQkNLMTg4NEExWlFcIixcIkxnbE5tXCI6XCJsZWdhbFwiLFwiVHJkTm1cIjpcInRyYWRlXCIsXCJBZGRyMVwiOlwiYWRyZXNzXCIsXCJBZGRyMlwiOlwiYWRkcmVzc1wiLFwiTG9jXCI6XCJsb2NhdGlvblwiLFwiUGluXCI6XCI1NjAwNjBcIixcIlN0Y2RcIjpcIjEwXCJ9LFwiSXRlbUxpc3RcIjpbe1wiSXRlbU5vXCI6MSxcIlNsTm9cIjpcIjFcIixcIklzU2VydmNcIjpcIk5cIixcIlByZERlc2NcIjpcInN0ZWVsXCIsXCJIc25DZFwiOlwiMTAwMVwiLFwiQmFyY2RlXCI6XCI3ODhcIixcIlF0eVwiOlwiMlwiLFwiRnJlZVF0eVwiOlwiOFwiLFwiVW5pdFwiOlwiQkFMXCIsXCJVbml0UHJpY2VcIjoxMDAsXCJUb3RBbXRcIjo1NDIsXCJEaXNjb3VudFwiOjQ1LFwiUHJlVGF4VmFsXCI6NSxcIkFzc0FtdFwiOjcsXCJHc3RSdFwiOjUsXCJJZ3N0QW10XCI6NSxcIkNnc3RBbXRcIjo1LFwiU2dzdEFtdFwiOjUsXCJDZXNSdFwiOjUsXCJDZXNBbXRcIjo1LFwiQ2VzTm9uQWR2bEFtdFwiOjUsXCJTdGF0ZUNlc1J0XCI6NSxcIlN0YXRlQ2VzQW10XCI6NSxcIlN0YXRlQ2VzTm9uQWR2bEFtdFwiOjUsXCJPdGhDaHJnXCI6NSxcIlRvdEl0ZW1WYWxcIjo1fSx7XCJJdGVtTm9cIjoyLFwiU2xOb1wiOlwiMVwiLFwiSXNTZXJ2Y1wiOlwiTlwiLFwiUHJkRGVzY1wiOlwic3RlZWxcIixcIkhzbkNkXCI6XCIxMDAxXCIsXCJCYXJjZGVcIjpcIjc4OFwiLFwiUXR5XCI6XCIyXCIsXCJGcmVlUXR5XCI6XCI4XCIsXCJVbml0XCI6XCJCQUxcIixcIlVuaXRQcmljZVwiOjEwMCxcIlRvdEFtdFwiOjU0MixcIkRpc2NvdW50XCI6NDUsXCJQcmVUYXhWYWxcIjo1LFwiQXNzQW10XCI6NyxcIkdzdFJ0XCI6NSxcIklnc3RBbXRcIjo1LFwiQ2dzdEFtdFwiOjUsXCJTZ3N0QW10XCI6NSxcIkNlc1J0XCI6NSxcIkNlc0FtdFwiOjUsXCJDZXNOb25BZHZsQW10XCI6NSxcIlN0YXRlQ2VzUnRcIjo1LFwiU3RhdGVDZXNBbXRcIjo1LFwiU3RhdGVDZXNOb25BZHZsQW10XCI6NSxcIk90aENocmdcIjo1LFwiVG90SXRlbVZhbFwiOjV9XSxcIlZhbER0bHNcIjp7XCJBc3NWYWxcIjozLFwiQ2dzdFZhbFwiOjMsXCJTZ3N0VmFsXCI6MyxcIklnc3RWYWxcIjozLFwiQ2VzVmFsXCI6MyxcIlN0Q2VzVmFsXCI6NSxcIlJuZE9mZkFtdFwiOjUsXCJUb3RJbnZWYWxcIjo1fSxcIkV4cER0bHNcIjp7XCJTaGlwQk5vXCI6XCIxMjNcIixcIlNoaXBCRHRcIjpcIjEyLzAyLzIwMjBcIixcIlBvcnRcIjpcIjEyXCIsXCJSZWZDbG1cIjpcIk5cIixcIkZvckN1clwiOlwiMTIzXCIsXCJDbnRDb2RlXCI6XCIxMlwifSxcIkV3YkR0bHNcIjp7XCJUcmFuc0lkXCI6XCIyOUFXR1BWNzEwN0IxWjFcIixcIlRyYW5zTmFtZVwiOlwiVklLQVNFWFBPUlRTXCIsXCJUcmFuc01vZGVcIjpcIjJcIixcIkRpc3RhbmNlXCI6XCIxMDBcIixcIlRyYW5zRG9jTm9cIjpcIjJcIixcIlRyYW5zRG9jRHRcIjpcIjAyLzAyLzIwMjBcIixcIlZlaE5vXCI6XCJLQTEyMzQ1NlwiLFwiVmVoVHlwZVwiOlwiMlwifX0iLCJpc3MiOiJOSUMifQ.T-kMNvk0-uGz_2XUIqQwoalgjiCCyMPpjY__05R2EVvrkYqiS1sIa_IHj-6u8iZ1tQPeFq4eG-Be1CZuXO8-0Dgtb_6ozyBJFICkba4YUJycnDdbxpkH2KxEWgtUghxcAsKffEByTkZpuSNLV863c9aF1hmTlhLMN6l2cuCE7Pd2x0S8h1kHZhX1y-EycRsbO0awD686vgO9JpVa9OqeQSavvnHLFjJhURqDA3vICQeZLdob7_GFEKDFrTITP9M-nK7PXibNrlATUNtZifXc7WrjDz3gwrnwLrHofR2Y9fJQI8Cr14t-KuDxeeKs0OeXVJomoovj-S_f_uCKI8u6QA";
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
