/**
 * 
 */
package com.ey.advisory.ewb.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.clientBusiness.ErpBusinessBatchJsonPayloadsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.ewb.data.entities.clientBusiness.ErpBusinessBatchJsonPayloadsEntity;
import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

/**
 * @author Hemasundar.J
 *
 */
@Service("DestinationConnectivityBusiness")
public class DestinationConnectivityBusiness {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DestinationConnectivityBusiness.class);

	@Autowired
	private ErpBusinessBatchJsonPayloadsRepository erpBatchJsonPayloadsRepo;

	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	private static final String ON_PREMISE_PROXY = "OnPremise";

	private com.sap.cloud.account.TenantContext getSAPCloudTenantContext() {

		try {
			InitialContext ctx = new InitialContext();
			return (com.sap.cloud.account.TenantContext) ctx
					.lookup("java:comp/env/TenantContext");
		} catch (NamingException e) {
			LOGGER.error(e.getMessage());
			return null;
		}

	}

	private Proxy getProxy(String proxyType) {
		Proxy proxy = Proxy.NO_PROXY;
		String proxyHost = null;
		String proxyPort = null;

		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = System.getenv("HC_OP_HTTP_PROXY_PORT");
		} else {
			// Get proxy for internet destinations
			proxyHost = System.getProperty("https.proxyHost");
			proxyPort = System.getProperty("https.proxyPort");
		}

		if (proxyPort != null && proxyHost != null) {
			int proxyPortNumber = Integer.parseInt(proxyPort);
			proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxyHost, proxyPortNumber));
		}

		return proxy;
	}

	private void injectHeader(HttpURLConnection urlConnection,
			String proxyType) {
		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Insert header for on-premise connectivity with the consumer
			// account name
			TenantContext tenantContext = getSAPCloudTenantContext();
			if (tenantContext != null) {
				urlConnection.setRequestProperty(
						"SAP-Connectivity-ConsumerAccount",
						tenantContext.getTenant().getAccount().getId());
			}
		}
	}
	private void copyStream(InputStream inStream, OutputStream outStream)
			throws IOException {
		byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.flush();
	}
	public final Integer post(String destinationName, String xml,
			Long batchId) {

		HttpURLConnection urlConnection = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination name {} calling started.",
						destinationName);
			}
			// Look up the connectivity configuration API
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");

			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration
					.getConfiguration(destinationName);

			if (destConfiguration == null) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error(String.format("Destination %s is not found.",
							destinationName));
				}
				return null;
			}
			// Get the destination URL
			String endPoint = destConfiguration.getProperty("URL");
			URL url = new URL(endPoint);
			String proxyType = destConfiguration.getProperty("ProxyType");
			Proxy proxy = getProxy(proxyType);
			urlConnection = (HttpURLConnection) url.openConnection(proxy);
			String encoding = Base64.getEncoder().encodeToString(
					(destConfiguration.getProperty("User").toUpperCase() + ":" +
							 destConfiguration.getProperty("Password"))
									.getBytes());
			LOGGER.error("Destination UserName : {} , Password : {}",
					destConfiguration.getProperty("User").toUpperCase(),
					destConfiguration.getProperty("Password"));
			LOGGER.error("Authorization after encoding {} : Basic ", encoding);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Authorization",
					"Basic " + encoding);

			if (destConfiguration
					.getProperty("CloudConnectorLocationId") != null) {
				urlConnection.setRequestProperty(
						"SAP-Connectivity-SCC-Location_ID", destConfiguration
								.getProperty("CloudConnectorLocationId"));
			}
			urlConnection.setRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			injectHeader(urlConnection, proxyType);
			InputStream targetStream = new ByteArrayInputStream(xml.getBytes());
			DataOutputStream outstream = new DataOutputStream(
					urlConnection.getOutputStream());
			copyStream(targetStream, outstream);
			String respStatus = urlConnection.getResponseMessage();
			String response = null;
			int respCode = urlConnection.getResponseCode();
			if (respCode == HttpURLConnection.HTTP_OK) { // success
				LOGGER.info("Request got success");
				BufferedReader in = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String inputLine;
				StringBuilder respnse = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					respnse.append(inputLine);
				}
				in.close();
				// print result
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(respnse.toString());
				}
				response = respnse.toString();
			} else {
				response = urlConnection.getResponseMessage();
				LOGGER.error("Request Failed with status code {} and status {}",
						respCode, respStatus);
			}
			dumpErpRequestPayload(batchId, xml, response, respCode, null);
			return respCode;
		} catch (Exception e) {
			// Connectivity operation failed
			String errorMessage = "Connectivity operation failed.";
			LOGGER.error(errorMessage, e.getMessage());
			dumpErpRequestPayload(batchId, xml, null, 0, e.getMessage());
		}
		return null;
	}

	private void dumpErpRequestPayload(Long batchId, String reqXml,
			String respXml, Integer httpCode, String errMsg) {
		try {

			ErpBusinessBatchJsonPayloadsEntity entity = new ErpBusinessBatchJsonPayloadsEntity();

			entity.setBatchId(batchId);
			entity.setRevReqPayload(reqXml);
			entity.setRevRespPayload(respXml);
			entity.setHttpCode(httpCode);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			entity.setIsDelete(false);
			entity.setRevErrMsg(errMsg);
			erpBatchJsonPayloadsRepo.save(entity);

		} catch (Exception e) {
			String msg = String.format("Exception occured %s", e);
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}
	}

}
