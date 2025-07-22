/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid.Khan
 *
 */
@Slf4j
public class UrlProcessingBlockImpl {
	private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
	private static final String CONTENT_TYPE_APP_JSON = "application/json";
	private static final String CONTENT_TYPE_APP_OCTET_STREAM = "application/octet-stream";
	private static final String CONTENT_TYPE_APP_X_GZIP = "application/x-gzip";
	private static final String CONTENT_TYPE_APP_MULTIPART_GZIP = "miltipart/x-gzip";

	public void processUrl(String url, String ek, Long requestId,
			GZIPInputStream is, TarArchiveInputStream ti, InputStream insStream,
			Map<String, String> headerMap) {
		try {
			LOGGER.debug("URL is {}", url);
			HttpClient httpClient = StaticContextHolder
					.getBean("GSTNHttpClient", HttpClient.class);
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(30000).setConnectionRequestTimeout(30000)
					.setSocketTimeout(60000).build();
			HttpGet request = new HttpGet(url);
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
			request.setConfig(config);
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				LOGGER.debug("URL Response is Success - {}", url);
				Header hdr = response.getFirstHeader(CONTENT_TYPE_HEADER_NAME);
				String contentType = hdr.getValue();
				if (contentType.contains(CONTENT_TYPE_APP_X_GZIP)
						|| CONTENT_TYPE_APP_MULTIPART_GZIP
								.equalsIgnoreCase(contentType)
						|| CONTENT_TYPE_APP_OCTET_STREAM
								.equalsIgnoreCase(contentType)) {
					insStream = response.getEntity().getContent();
					
					is = new GZIPInputStream(insStream);
					ti = new TarArchiveInputStream(is);
					TarArchiveEntry currentEntry = ti.getNextTarEntry();
					BufferedReader br = null;
					while (currentEntry != null) {
						br = new BufferedReader(new InputStreamReader(ti));
						String line = "";
						String decryptedResp = null;
						while ((line = br.readLine()) != null) {

							if (LOGGER.isDebugEnabled()) {
								String logMsg = String.format(
										"The ek used for decryption is : '%s'",
										ek);
								LOGGER.debug(logMsg);
							}
							
							if (url.contains(GstnApiWrapperConstants.EINVOICE_IDENTIFIER))
							{
								decryptedResp = decryptResponse(line, ek);
							}
							else
							{
							decryptedResp = decryptResponse(
									line.substring(1, line.length() - 1), ek);
							}
						}
						if (decryptedResp != null)
							saveResponse(decryptedResp, requestId);
						currentEntry = ti.getNextTarEntry();
					}
				} else if (contentType.contains(CONTENT_TYPE_APP_JSON)) {
					String resp = EntityUtils.toString(response.getEntity());
					LOGGER.error("GSTN has returned invalid content while"
							+ " downloading the url {}", resp);
					throw new AppException(
							"Exception while processing the url");
				} else {
					LOGGER.warn(
							"GSTN has returned unexpected content "
									+ "type while downloading the url {}",
							contentType);
					throw new AppException(
							"Exception while processing the url");
				}
			} else {
				throw new AppException("failed for url " + url + "");
			}
		} catch (Exception e) {
			throw new AppException("Exception while processing the url", e);
		}

	}

	private String decryptResponse(String encrResp, String ek) {

		// Decrypt the data using the 'rek' and 'sk'. This method internally
		// decrypts the rek and gets the rk, using 'sk' as the key. Then it
		// uses the 'rk' to decrypt the 'data' and return the content.
		String resp = CryptoUtils.decryptResponseJson(encrResp, ek);
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Success Response for ek {}", ek);
			LOGGER.info(msg);
		}
		return resp;

	}

	private void saveResponse(String decryptedResp, Long requestId) {
		try {
			APIResponseRepository respRepo = StaticContextHolder.getBean(
					"APIResponseRepository", APIResponseRepository.class);
			Clob responseClob = new javax.sql.rowset.serial.SerialClob(
					decryptedResp.toCharArray());
			APIResponseEntity responseEntity = new APIResponseEntity(requestId,
					responseClob, GstnApiWrapperConstants.SUCCESS,
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now(),
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now());
			respRepo.save(responseEntity);
		} catch (Exception e) {
			String msg = "Exception while persisting Token URL Data";
			LOGGER.error(msg, e);
		}

	}

}
