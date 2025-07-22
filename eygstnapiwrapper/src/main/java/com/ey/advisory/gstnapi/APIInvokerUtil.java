/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("APIInvokerUtil")
@Slf4j
public class APIInvokerUtil {


	private APIInvokerUtil() {
	}

	public static String getResultById(Long id) {
		
		StringBuilder response = new StringBuilder();
		Reader r;
		try {
			APIResponseRepository respRepo = StaticContextHolder.getBean
					("APIResponseRepository", APIResponseRepository.class);
			APIResponseEntity respEntity = respRepo.getResponseById(id);
			Clob respClob =respEntity.getResponseJson();
			r = respClob.getCharacterStream();
			
			int ch;
			while ((ch = r.read()) != -1) {
				response.append("" + (char) ch);
			}
			
		} catch (SQLException | IOException e) {
			LOGGER.error("Exception Occured While Response Conversion "
					+ "clob to json",e);
			throw new AppException(e);
		}catch(Exception e){
			LOGGER.error(e.getLocalizedMessage(),e);
		}
		return response.toString();
	}

	public static List<String> getResultsByIds(List<Long> ids) {
		APIResponseRepository respRepo = StaticContextHolder
				.getBean(APIResponseRepository.class);
		List<Clob> respClobList = respRepo.getResponseListByIds(ids);
		List<String> respList = new ArrayList<>();
		respClobList.forEach(o -> {
			StringBuilder response = new StringBuilder();
			Reader r;
			try {
				r = o.getCharacterStream();
				
				int ch;
				while ((ch = r.read()) != -1) {
					response.append("" + (char) ch);
				}
				respList.add(response.toString());
			} catch (SQLException | IOException e) {
				LOGGER.error("Exception Occured While Response Conversion "
						+ "clob to json",e);
				throw new AppException(e);
			}
		});
		return respList;
		}
	
	public static APIInvocationError createErrorObject(String errormessage, 
			String execErrorCode, String extErrorCode, Exception ex) {
		APIError error = new APIError(execErrorCode, errormessage);
		return new APIInvocationError(extErrorCode , error, ex);
		
	}
		
	public static String resolveProcessingTokenUrl(String url) {
		if (url.contains(GstnApiWrapperConstants.EINVOICE_IDENTIFIER)) {
			url = url.replace(
			        GstnApiWrapperConstants.EINVOICE_IDENTIFIER_TO_BE_REPLACED,
			        GstnApiWrapperConstants.EINVOICE_IDENTIFIER_REPLACED_WITH);
		} else {
			url = GstnApiWrapperConstants.GATEWAY_TOKEN_URL_PREFIX + url;
		}

		return url;
	}
		
	
}
