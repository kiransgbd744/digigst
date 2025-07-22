/**
 * 
 *//*
package com.ey.advisory.app.services.jobs.anx1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.Anx1GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.anx1.TokenReponseDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

*//**
 * @author Hemasundar.J
 *
 *//*
@Service("TokenReponseParserImpl")
public class TokenReponseParserImpl implements TokenReponseParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TokenReponseParserImpl.class);

	public TokenReponseDto parseB2bData(Anx1GetInvoicesReqDto dto,
			String apiResp) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject respObject = (new JsonParser()).parse(apiResp).
					getAsJsonObject();
			TokenReponseDto tokenDto = gson.fromJson(respObject,
					TokenReponseDto.class);
			return tokenDto;
		} catch (Exception ex) {
			String msg = "Failed to parse Token Response.";
			LOGGER.error(msg, ex);
			return null;
		}
	}
	public Boolean isTokenResponse(String apiResp) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject respObject = (new JsonParser()).parse(apiResp).
					getAsJsonObject();
			TokenReponseDto tokenDto = gson.fromJson(respObject,
					TokenReponseDto.class);
			
			return tokenDto.getToken() != null ? true : false;
		} catch (Exception ex) {
			String msg = "Failed to parse Token Response.";
			LOGGER.error(msg, ex);
			return false;
		}
	}
}
*/