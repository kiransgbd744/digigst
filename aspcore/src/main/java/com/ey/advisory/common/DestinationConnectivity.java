/**
 * 
 */
package com.ey.advisory.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cloudfoundry.identity.client.UaaContext;
import org.cloudfoundry.identity.client.UaaContextFactory;
import org.cloudfoundry.identity.client.token.GrantType;
import org.cloudfoundry.identity.client.token.TokenRequest;
import org.cloudfoundry.identity.uaa.oauth.token.CompositeAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.NodeList;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpBatchJsonPayloadsEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.ErpInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpBatchJsonPayloadsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.common.cf.ConnectivityCredentials;
import com.ey.advisory.app.common.cf.DestinationConfig;
import com.ey.advisory.app.common.cf.DestinationCredentials;
import com.ey.advisory.app.common.cf.DestinationServiceResponse;
import com.ey.advisory.app.common.cf.VCAPServices;
import com.ey.advisory.app.common.cf.XsuaaCredentials;
import com.ey.advisory.app.data.repositories.client.AutoRecon2AERPMetaRepository;
import com.ey.advisory.core.dto.EinvEwbDto;
import com.ey.advisory.core.dto.GSTR2aAutoReconRevIntgResponseDto;
import com.ey.advisory.core.springconfig.TrustStoreLoader;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.cloud.account.TenantContext;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import lombok.extern.slf4j.Slf4j;
/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("DestinationConnectivity")
public class DestinationConnectivity {

	@Autowired
	private AnxErpBatchHandler anxErpBatchHandler;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private GSTNDetailRepository gstinRepo;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpBatchJsonPayloadsRepository erpBatchJsonPayloadsRepo;

	@Autowired
	private ErpInfoEntityRepository erpInfoRepo;

	@Autowired
	private AutoRecon2AERPMetaRepository meta2aprRepo;

	@Autowired
	@Qualifier("TrustStoreLoader")
	private TrustStoreLoader trustStoreLoader;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	private static final String ON_PREMISE_PROXY = "OnPremise";
	private static final String DESTINATION_CONFIG_HTTP_PATH = "destination-configuration/v1/destinations/";
	private static final String VCAP_SERVICES = "VCAP_SERVICES";

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

	private Proxy getProxy(VCAPServices vcapServices, String proxyType) {
		Proxy proxy = Proxy.NO_PROXY;
		String proxyHost = null;
		String proxyPort = null;

		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			ConnectivityCredentials connCreds = vcapServices
					.getConnectivityServices().get(0).getCredentials();
			proxyHost = connCreds.getOnpremiseProxyHost();
			proxyPort = connCreds.getOnpremiseProxyHttpPort();
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

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Proxt Type: {}, Proxy Host: {}, Proxy Port: {}",
					proxyType, proxyHost, proxyPort);
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
	/**
	 * This method is meant to push the given XML data to ERP system through the
	 * SAP Destination and SAP Clound-Connector
	 * 
	 * @param destinationName
	 * @param xml
	 * @param batch
	 * @return
	 */
	private final Integer post(String destinationName, String xml,
			AnxErpBatchEntity batch, String sourceType) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERP_PUSH,
				PerfamanceEventConstants.DESTINATION_CALL_START,
				PerfamanceEventConstants.DestinationConnectivity,
				PerfamanceEventConstants.post, null);

		String respStatus = null;
		int respCode = 0;
		InputStream inputStream = null;
		HttpURLConnection urlConnection = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination name {} calling started.",
						destinationName);
			}
			// Look up the connectivity configuration API
			String vcapJson = System.getenv(VCAP_SERVICES);
			if (Strings.isNullOrEmpty(vcapJson)) {
				LOGGER.warn("VCAP_SERVICES variable not found. "
						+ "DB Configurations will not be loaded.");
			}
			LOGGER.debug("vcap Json {} ", vcapJson);

			VCAPServices vcapServices = null;
			try {
				vcapServices = new Gson().fromJson(vcapJson,
						VCAPServices.class);
			} catch (JsonParseException ex) {
				LOGGER.error(String.format("VCAP_SERVICES Parsing Failed!! "
						+ "VCAP_SERVICES value = %s", vcapJson), ex);
			}

			DestinationConfig destConfiguration = getDestinationConfiguration(
					vcapServices, destinationName);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("destConfiguration: {}", destConfiguration);
			}

			if (destConfiguration == null) {
				LOGGER.error(
						"Fetching configuration for Destination {} failed.",
						destinationName);
				return null;
			}
			// Get the destination URL
			String endPoint = destConfiguration.getUrl();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Connecting to virtual URL {}", endPoint);
			}
			URL url = new URL(endPoint);
			String proxyType = destConfiguration.getProxyType();
			Proxy proxy = getProxy(vcapServices, proxyType);
			String user = destConfiguration.getUser();
			String password = destConfiguration.getPassword();
			urlConnection = (HttpURLConnection) url.openConnection(proxy);

			if (sourceType != null && "NONSAP".equalsIgnoreCase(sourceType)) {
				// // Non Sap system
				HttpsURLConnection httpsUrlCon = null;
				httpsUrlCon = (HttpsURLConnection) url.openConnection(proxy);
				httpsUrlCon.setRequestMethod("POST");
				httpsUrlCon.setDoOutput(true);
				if (!Strings.isNullOrEmpty(destConfiguration.getCloudConnectorLocationId())) {
					httpsUrlCon.setRequestProperty(
							"SAP-Connectivity-SCC-Location_ID",
							destConfiguration
									.getCloudConnectorLocationId());
				}
				httpsUrlCon.setRequestProperty("Content-Type",
						"text/xml; charset=utf-8");

				httpsUrlCon.setRequestProperty("api_key",
						destConfiguration.getUser());
				httpsUrlCon.setRequestProperty("api_secret",
						destConfiguration.getPassword());
				KeyStore trustStore = trustStoreLoader.loadTrustStore();
				SSLSocketFactory sslSocketFactory = createSslSocketFactory(
						trustStore);
				httpsUrlCon.setSSLSocketFactory(sslSocketFactory);

				injectHeader(httpsUrlCon, proxyType);
				InputStream targetStream = new ByteArrayInputStream(
						xml.getBytes());
				DataOutputStream outstream = new DataOutputStream(
						httpsUrlCon.getOutputStream());
				copyStream(targetStream, outstream);

				respStatus = httpsUrlCon.getResponseMessage();
				respCode = httpsUrlCon.getResponseCode();
				inputStream = httpsUrlCon.getInputStream();

			} else {
				// Sap system
				urlConnection = (HttpURLConnection) url.openConnection(proxy);
				urlConnection = setConnectivityProxyAuthorization(vcapServices,
						urlConnection);
				String encoding = Base64.getEncoder().encodeToString(
						(user.toUpperCase() + ":" + password).getBytes());
				LOGGER.error("Destination UserName : {} , Password : {}",
						user.toUpperCase(), password);
				LOGGER.error("Authorization after encoding {} : Basic ",
						encoding);
				if (!Strings.isNullOrEmpty(destConfiguration.getCloudConnectorLocationId())) {
					urlConnection.setRequestProperty(
							"SAP-Connectivity-SCC-Location_ID",
							destConfiguration
									.getCloudConnectorLocationId());
				}
				
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Authorization",
						"Basic " + encoding);
				urlConnection.setRequestProperty("Content-Type",
						"text/xml; charset=utf-8");
				
				injectHeader(urlConnection, proxyType);
				InputStream targetStream = new ByteArrayInputStream(
						xml.getBytes());
				DataOutputStream outstream = new DataOutputStream(
						urlConnection.getOutputStream());
				copyStream(targetStream, outstream);
				respStatus = urlConnection.getResponseMessage();
				respCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();
			}
			if (respCode == HttpURLConnection.HTTP_OK) { // success
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request got success");
				}
				BufferedReader in = new BufferedReader(
						new InputStreamReader(inputStream));
				String inputLine;
				StringBuilder respnse = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					respnse.append(inputLine);
				}
				in.close();
				// print result
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(respnse != null ? respnse.toString() : null);
				}
				batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
						respStatus, true,
						respnse != null ? respnse.toString() : null, null);
				try {
					if ("GSTR2A_AUTORECON_REV_INTG"
							.equalsIgnoreCase(destinationName)) {
						GSTR2aAutoReconRevIntgResponseDto dto = null;
						if (respnse != null) {
							dto = convertXmlToDto(respnse.toString());
							if (dto != null) {
								meta2aprRepo
										.updateErpPushStatus("SUCCESS",
												dto.getReceivedChnkRec() != null
														? dto.getReceivedChnkRec()
														: 0,
												dto.getControlId() != null
														? Long.valueOf(
																dto.getControlId())
														: 0,
												dto.getGstin(),
												dto.getChunkId() != null
														? Integer.valueOf(
																dto.getChunkId())
														: 0);
							}
						}
					}
				} catch (Exception ee) {
					LOGGER.error("Error while updating 2apr request", ee);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Request Failed with status code {} and status {}",
							respCode, respStatus);
				}
				batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
						respStatus, false, null, null);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.ERP_PUSH,
					PerfamanceEventConstants.DESTINATION_CALL_END,
					PerfamanceEventConstants.DestinationConnectivity,
					PerfamanceEventConstants.post, null);

			return respCode;
		} catch (Exception e) {
			// Connectivity operation failed
			batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
					respStatus, false, null, e.toString());
			String errorMessage = "DestinationConnectivity operation failed.";
			LOGGER.error(errorMessage, e.getMessage());
			throw new AppException(errorMessage, e);
		}
		// return null;
	}

	/**
	 * This method is meant for forming final XML payload by adding start/end
	 * ROOT tags(Header + footer) tags dynamically reading from
	 * SCENARIO_PEMISSION table and to do push the XML data to ERP system
	 * through the SAP Destination and SAP Clound-Connector
	 * 
	 * @param dto
	 * @param className
	 * @param batch
	 * @return
	 * @throws Exception
	 */
	public int pushToErp(JaxbXmlFormatter dto, String className,
			AnxErpBatchEntity batch) throws Exception {

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERP_PUSH,
				PerfamanceEventConstants.PUSH_TO_ERP_START,
				PerfamanceEventConstants.DestinationConnectivity,
				PerfamanceEventConstants.pushToErp, null);

		String jobType = batch.getJobType();
		Long erpId = batch.getErpId();
		Long scenarioId = batch.getScenarioId();

		if (jobType == null || erpId == null || scenarioId == null
				|| dto == null) {
			LOGGER.error(
					"Required params jobType {}, erpId {}, "
							+ "scenarioId {}, jsonDto {} are missing",
					jobType, erpId, scenarioId, dto);
			return 0;
		}
		String header = "";
		String footer = "";
		String destinationName = batch.getDestinationName();
		String gstin = batch.getGstin();
		String sourceType = null;
		if (gstin != null && !gstin.isEmpty()
				&& ERPConstants.BACKGROUND_BASED_JOB
						.equalsIgnoreCase(jobType)) {
			// Assuming it as periodic job
			GSTNDetailEntity gstinInfo = gstinRepo
					.findByGstinAndIsDeleteFalse(gstin);
			if (gstinInfo == null) {
				LOGGER.error("No Active Gstin found in onboarding gstin {}",
						gstin);
				return 0;
			}
			Long gstinId = gstinInfo.getId();
			// try {
			ErpScenarioPermissionEntity scenarioPermision = erpScenPermissionRepo
					.findByScenarioIdAndGstinIdAndErpIdAndIsDeleteFalse(
							scenarioId, gstinId, erpId);
			header = scenarioPermision.getStartRootTag();
			footer = scenarioPermision.getEndRootTag();
			if (destinationName == null || destinationName.isEmpty()) {
				destinationName = scenarioPermision.getDestName();
			}
			sourceType = scenarioPermision.getSourceType();
		} else {
			// Assuming it as Event based job
			ErpEventsScenarioPermissionEntity scenarioPermision = erpEventsScenPermissionRepo
					.findByScenarioIdAndErpIdAndIsDeleteFalse(scenarioId,
							erpId);
			header = scenarioPermision.getStartRootTag();
			footer = scenarioPermision.getEndRootTag();
			if (destinationName == null || destinationName.isEmpty()) {
				destinationName = scenarioPermision.getDestName();
			}
			sourceType = scenarioPermision.getSourceType();
		}

		JaxbXmlFormatter bean = StaticContextHolder.getBean(className,
				JaxbXmlFormatter.class);
		String xml = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBContext context = JAXBContext.newInstance(bean.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(dto, out);
		xml = out.toString();

		if (xml != null && xml.length() > 0) {
			xml = xml.substring(xml.indexOf('\n') + 1);
		}
		// final payload using header and footer.
		if (xml != null) {
			xml = header + xml + footer;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fro class name {} XML headers with data{}", className,
					xml);
		}
		if (xml != null && destinationName != null) {
			// Store the XML payload into a table
			dumpErpRequestPayload(batch.getId(), xml, batch.getGroupcode(),
					batch.getCreatedBy());
			// send the the XML payload to ERP through destination
			Integer post = post(destinationName, xml, batch, sourceType);

			return post;
		}

		/*
		 * } catch (Exception e) { LOGGER.error(e.getMessage()); throw new
		 * AppException(e.getMessage(), e); }
		 */

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERP_PUSH,
				PerfamanceEventConstants.PUSH_TO_ERP_END,
				PerfamanceEventConstants.DestinationConnectivity,
				PerfamanceEventConstants.pushToErp, null);
		return 0;
	}

	private void dumpErpRequestPayload(Long batchId, String requestObject,
			String groupCode, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Erp json payload %s ", requestObject);
			LOGGER.debug(msg);
		}
		if (requestObject == null || StringUtils.isEmpty(requestObject)) {
			LOGGER.error("Response is not valid Json Payload");
			return;
		}

		Clob responseClob = null;
		try {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			responseClob = new javax.sql.rowset.serial.SerialClob(
					requestObject.toCharArray());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Response clob %s ",
						responseClob != null ? responseClob.toString()
								: "Null Erp push json");
				LOGGER.debug(msg);
			}

			ErpBatchJsonPayloadsEntity entity = new ErpBatchJsonPayloadsEntity();

			entity.setBatchId(batchId);
			entity.setJsonPayload(responseClob);
			entity.setCreatedOn(now);
			entity.setCreatedBy(userName);
			erpBatchJsonPayloadsRepo.save(entity);

		} catch (SQLException e) {
			String msg = String.format("Exception occured %s", e);
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}
	}

	private static SSLSocketFactory createSslSocketFactory(KeyStore trustStore)
			throws GeneralSecurityException {
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);
		TrustManager[] trustManagers = tmf.getTrustManagers();

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustManagers, null);
		return sslContext.getSocketFactory();
	}

	public int pushXmlSftpData(JaxbXmlFormatter dto, String className,
			AnxErpBatchEntity batch, String endPoint, String fileName) {
		Integer httpStatusCd = 0;
		String apiResp = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("In  pushXmlSftpData");
			}

			JaxbXmlFormatter bean = StaticContextHolder.getBean(className,
					JaxbXmlFormatter.class);
			String xml = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext.newInstance(bean.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dto, out);
			xml = out.toString();

			ErpInfoEntity erpinfo = erpInfoRepo
					.getEntityByErpId(batch.getErpId());
			String userName = erpinfo.getUser();
			String password = erpinfo.getPass();

			if (xml != null) {
				// Store the XML payload into a table
				dumpErpRequestPayload(batch.getId(), xml, batch.getGroupcode(),
						batch.getCreatedBy());

				String encoding = Base64.getEncoder()
						.encodeToString((userName + ":" + password).getBytes());

				HttpPost httpPost = new HttpPost(endPoint);
				httpPost.setHeader("Authorization", "Basic " + encoding);
				httpPost.setHeader("Content-Type", "text/xml");
				httpPost.setHeader("fileName", fileName);
				StringEntity entity = new StringEntity(xml);
				httpPost.setEntity(entity);
				HttpResponse resp = httpClient.execute(httpPost);
				httpStatusCd = resp.getStatusLine().getStatusCode();
				apiResp = EntityUtils.toString(resp.getEntity());

				if (httpStatusCd == HttpURLConnection.HTTP_OK) { // success
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Request got success with {} ",
								httpStatusCd);
					}
				} else {

					LOGGER.error(
							"Request Failed with status code {} and status {}",
							httpStatusCd, apiResp);

				}

			}

			return httpStatusCd;
		} catch (Exception e) {
			// Connectivity operation failed
			anxErpBatchHandler.updateErpBatch(batch, httpStatusCd, "Failed",
					false, null, e.toString());
			String errorMessage = "DestinationConnectivity operation failed.";
			LOGGER.error(errorMessage, e.getMessage());
			return httpStatusCd;

		}

	}

	public int pushToSftp(List<EinvEwbDto> dto, String className,
			AnxErpBatchEntity batch, Long fileId, String fileNameinTable,
			String endPoint) throws Exception {

		Gson gson = GsonUtil.newSAPGsonInstance();

		String jobType = batch.getJobType();
		Long erpId = batch.getErpId();
		Long scenarioId = batch.getScenarioId();

		String fileName = fileNameinTable.substring(33);

		if (jobType == null || erpId == null || scenarioId == null
				|| dto == null) {
			LOGGER.error(
					"Required params jobType {}, erpId {}, "
							+ "scenarioId {}, jsonDto {} are missing",
					jobType, erpId, scenarioId, dto);
			return 0;
		}
		String sourceType = null;
		String userName = null;
		String password = null;

		ErpInfoEntity erpinfo = erpInfoRepo.getEntityByErpId(erpId);

		sourceType = erpinfo.getSourceType();
		userName = erpinfo.getUser();
		password = erpinfo.getPass();

		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("req", respBody);
		String resp1 = resp.toString();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Json headers with data{}", resp1);
		}
		if (resp1 != null) {
			// Store the XML payload into a table
			dumpErpRequestPayload(batch.getId(), resp1, batch.getGroupcode(),
					batch.getCreatedBy());
			// send the the json payload to ERP through destination
			Integer post = postToSftp(resp1, batch, sourceType, userName,
					password, endPoint, fileName, fileId);

			return post;
		}
		return 0;
	}

	private final Integer postToSftp(String resp, AnxErpBatchEntity batch,
			String sourceType, String userName, String password,
			String endPoint, String fileName, Long fileId) {

		String respStatus = null;
		int respCode = 0;
		InputStream inputStream = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sftp connection started.");
			}
			URL url = new URL(endPoint);
			HttpsURLConnection httpsUrlCon = null;
			httpsUrlCon = (HttpsURLConnection) url.openConnection();
			httpsUrlCon.setRequestMethod("POST");
			httpsUrlCon.setDoOutput(true);
			httpsUrlCon.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");

			String encoding = Base64.getEncoder()
					.encodeToString((userName + ":" + password).getBytes());

			httpsUrlCon.setRequestProperty("Authorization",
					"Basic " + encoding);
			httpsUrlCon.setRequestProperty("fileName", fileName);
			InputStream targetStream = new ByteArrayInputStream(
					resp.getBytes());
			DataOutputStream outstream = new DataOutputStream(
					httpsUrlCon.getOutputStream());
			copyStream(targetStream, outstream);

			respStatus = httpsUrlCon.getResponseMessage();
			respCode = httpsUrlCon.getResponseCode();
			inputStream = httpsUrlCon.getInputStream();

			// return statusCode;
			if (respCode == HttpURLConnection.HTTP_OK) { // success
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request got success");
				}
				BufferedReader in = new BufferedReader(
						new InputStreamReader(inputStream));
				String inputLine;
				StringBuilder respnse = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					respnse.append(inputLine);
				}
				in.close();
				// print result
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(respnse != null ? respnse.toString() : null);
				}

				// apiResponse has not sent, setting to null for time being
				batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
						respStatus, true, null, null);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Request Failed with status code {} and status {}",
							respCode, respStatus);
				}
				batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
						respStatus, false, null, null);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.ERP_PUSH,
					PerfamanceEventConstants.DESTINATION_CALL_END,
					PerfamanceEventConstants.DestinationConnectivity,
					PerfamanceEventConstants.post, null);
			return respCode;
		} catch (Exception e) {
			// Connectivity operation failed
			batch = anxErpBatchHandler.updateErpBatch(batch, respCode,
					respStatus, false, null, e.toString());
			gstr1FileStatusRepository.updateChildCreatedFlagToFalse(fileId);

			String errorMessage = "DestinationConnectivity operation failed.";
			LOGGER.error(errorMessage, e.getMessage());
			throw new AppException(errorMessage, e);
		}
	}

	private GSTR2aAutoReconRevIntgResponseDto convertXmlToDto(String resp) {
		try {

			SOAPMessage message = MessageFactory.newInstance()
					.createMessage(new MimeHeaders(), new ByteArrayInputStream(
							resp.getBytes(Charset.forName("UTF-8"))));
			SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
			SOAPBody soapBody = soapEnv.getBody();
			NodeList returnList = soapBody.getChildNodes();
			NodeList innerResultList = returnList.item(0).getChildNodes();

			Source xmlSource = new DOMSource(innerResultList.item(0));

			Unmarshaller unmarshaller = JAXBContext
					.newInstance(GSTR2aAutoReconRevIntgResponseDto.class)
					.createUnmarshaller();
			return unmarshaller.unmarshal(xmlSource,
					GSTR2aAutoReconRevIntgResponseDto.class).getValue();

		} catch (Exception e) {
			LOGGER.error("Error while unmarshalling ERP response", e);
			return null;
		}

	}
	
	private DestinationConfig getDestinationConfiguration(
			VCAPServices vcapServices, String destinationName) {

		DestinationConfig destinationConfig = null;
		DestinationCredentials destinationCredentials = vcapServices
				.getDestinationServices().get(0).getCredentials();
		XsuaaCredentials xsuaaCredentials = vcapServices.getXsuaServices()
				.get(0).getCredentials();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("clientid: {}", destinationCredentials.getClientid());
			LOGGER.debug("clientsecret: {}",
					destinationCredentials.getClientsecret());
			LOGGER.debug("xsuaa URL: {}", xsuaaCredentials.getUrl());
		}

		try {
			String jwtToken = getToken(xsuaaCredentials.getUrl(),
					destinationCredentials.getClientid(),
					destinationCredentials.getClientsecret());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination Service JWT : {}", jwtToken);
			}

			String destnServiceUrl = destinationCredentials.getUri() + "/"
					+ DESTINATION_CONFIG_HTTP_PATH + destinationName;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination Service URL : {}", destnServiceUrl);
			}

			HttpGet request = new HttpGet(destnServiceUrl);
			request.addHeader("Authorization", "Bearer " + jwtToken);

			String destinationJson = null;
			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient
							.execute(request)) {

				// Get HttpResponse Status
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response status: {}",
							response.getStatusLine());
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					destinationJson = EntityUtils.toString(entity);
					LOGGER.debug("destination Json Response : {}",
							destinationJson);
				}
			}

			if (!Strings.isNullOrEmpty(destinationJson)) {
				try {
					DestinationServiceResponse serviceResponse = new Gson()
							.fromJson(destinationJson,
									DestinationServiceResponse.class);
					destinationConfig = serviceResponse.getDestinationConfig();

					return destinationConfig;
				} catch (JsonParseException ex) {
					LOGGER.error(String.format(
							"Destination JSON Parsing Failed!! "
									+ "Destination value = %s",
							destinationJson), ex);
				}
			} else {
				LOGGER.error("Destination JSON is either NULL or empty!! "
						+ "Destination JSON : {}", destinationJson);
			}

		} catch (Exception e) {
			String errorMessage = "Destination fetch opetration failed";
			LOGGER.error(errorMessage, e);
			return null;

		}
		return destinationConfig;

	}

	private HttpURLConnection setConnectivityProxyAuthorization(
			VCAPServices vcapServices, HttpURLConnection urlConnection)
			throws URISyntaxException {

		// get value of "clientid" and "clientsecret" from the environment
		// variables
		LOGGER.info("Getting connectivity details from VCAP variable");

		ConnectivityCredentials connCreds = vcapServices
				.getConnectivityServices().get(0).getCredentials();

		String clientid = connCreds.getClientid();
		String clientsecret = connCreds.getClientsecret();
		String tokenServiceUrl = connCreds.getTokenServiceUrl();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("clientid: {}, clientsecret: {}, tokenServiceUrl: {}",
					clientid, clientsecret, tokenServiceUrl);
		}

		LOGGER.info("Making request to UAA to retrieve access token");

		String accessToken = getToken(tokenServiceUrl, clientid, clientsecret);

		urlConnection.setRequestProperty("Proxy-Authorization",
				"Bearer " + accessToken);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Proxy-Authorization Token: {}", accessToken);
		}

		LOGGER.info("Proxy-Authorization header set");

		return urlConnection;
	}

	private String getToken(String xsuaaUrl, String clientid,
			String clientsecret) throws URISyntaxException {

		UaaContextFactory factory = UaaContextFactory.factory(new URI(xsuaaUrl))
				.authorizePath("/oauth/authorize").tokenPath("/oauth/token");
		TokenRequest tokenRequest = factory.tokenRequest();
		tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
		tokenRequest.setClientId(clientid);
		tokenRequest.setClientSecret(clientsecret);
		UaaContext xsuaaContext = factory.authenticate(tokenRequest);
		CompositeAccessToken accessToken = xsuaaContext.getToken();

		return accessToken.toString();

	}

}
