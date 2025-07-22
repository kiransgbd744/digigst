/*package com.ey.advisory.app.services.approvalstatus;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

@Component("ApprovalRI")
public class RequestApprovalRI {

	@Resource
	private TenantContext tenantContextRI;
	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RequestApprovalRI.class);

	private static final String ON_PREMISE_PROXY = "OnPremise";

	public String reverseIntegration(String xml, String destinationName) {

		if (xml == null || destinationName == null) {
			return new String();
		}
		HttpURLConnection urlConnection = null;
		// destinationName = "ZZGSTR1_APRROVAL_API";
		try {
			// Look up the connectivity configuration API
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");

			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration
					.getConfiguration(destinationName);

			if (destConfiguration == null) {
				LOGGER.error(String.format(
						"Destination %s is not found. Hint:"
								+ " Make sure to have the destination configured.",
						destinationName));
				return String.format(
						"Destination %s is not found. Hint:"
								+ " Make sure to have the destination configured.",
						destinationName);
			}
			String endPoint = destConfiguration.getProperty("URL");
			URL url = new URL(endPoint);
			String proxyType = destConfiguration.getProperty("ProxyType");
			Proxy proxy = getProxy(proxyType);
			urlConnection = (HttpURLConnection) url.openConnection(proxy);
			String user = destConfiguration.getProperty("User") ;
			String pass = destConfiguration.getProperty("Password") ;
			String encoding = Base64.getEncoder()
					.encodeToString((user + ":"	+ pass).getBytes());
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Authorization",
					"Basic " + encoding);
			urlConnection.setRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			injectHeader(urlConnection, proxyType);
			InputStream targetStream = new ByteArrayInputStream(xml.getBytes());
			DataOutputStream outstream = new DataOutputStream(
					urlConnection.getOutputStream());
			copyStream(targetStream, outstream);
			String responseStatus = urlConnection.getResponseMessage();
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				LOGGER.info("Request got success");
				BufferedReader in = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return response.toString();

			} else {
				LOGGER.info("Request not worked {}", responseCode,
						responseStatus);
				return String.format("Request not worked with responseCode %s",
						responseCode);
			}

		} catch (Exception e) {
			LOGGER.error("Connectivity operation failed", e);
			return new String("Connectivity operation failed");
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
//			String id = tenantContextRI.getTenant().getAccount().getId();
		//	TenantContext
			String id = "hf5695423" ;
			urlConnection.setRequestProperty("SAP-Connectivity-ConsumerAccount",
					id);
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




}
*/