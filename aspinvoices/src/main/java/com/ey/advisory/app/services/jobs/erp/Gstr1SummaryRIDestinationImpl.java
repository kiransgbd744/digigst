package com.ey.advisory.app.services.jobs.erp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Gstr1DocSumJsonToXml;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("gstr1SummaryRIDestinationImpl")
public class Gstr1SummaryRIDestinationImpl
		implements Gstr1SummaryRIDestination {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryRIDestinationImpl.class);

	private static final String ON_PREMISE_PROXY = "OnPremise";
	// HttpServletResponse response = null;
	HttpURLConnection urlConnection = null;

	@Override
	public ResponseEntity<String> pushToErp(JsonObject json,
			String destination) {

		String xml = null, data = null;
		try {
			if (destination == null) {
				destination = "GSTR1SummaryAPI";
			}
			if (json != null) {
				Gson gson = GsonUtil.newSAPGsonInstance();
				Gstr1DocSumJsonToXml dto = gson.fromJson(json,
						Gstr1DocSumJsonToXml.class);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBContext context = JAXBContext
						.newInstance(Gstr1DocSumJsonToXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(dto, out);
				data = out.toString();
			}
			/*
			 * StringBuilder sb = new StringBuilder(); sb.
			 * append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">"
			 * + " <soapenv:Header/> " + "<soapenv:Body> ");
			 * 
			 * data = data.substring(data.indexOf('\n')+1); sb.append(data);
			 * sb.append("</soapenv:Body> " + "</soapenv:Envelope>"); String xml
			 * = sb.toString() ;
			 */
			if (data != null && data.length() > 0) {
				xml = data.substring(data.indexOf('\n') + 1);
			}
			if (xml != null && xml.length() > 0) {

				// Static xml data for testing purpose.
				xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<Gstr1DocSummary><gstin>33GSPTN0481G1ZA</gstin><ret_period>072018</ret_period>"
						+ "<chksum>7e110eba090bc0e0c840da5d9c177037771783cf85537a064f67d623f988fff1</chksum>"
						+ "<sec_sum><data><sec_nm>CDNUR</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax>"
						+ "<ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst>"
						+ "<ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued>"
						+ "<ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued>"
						+ "<ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt>"
						+ "</data><data><sec_nm>EXPA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax>"
						+ "<ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val>"
						+ "<ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled>"
						+ "<net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt>"
						+ "<ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>DOC_ISSUE</sec_nm><ttl_rec>0</ttl_rec>"
						+ "<ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst>"
						+ "<ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued>"
						+ "<ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued>"
						+ "<ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt>"
						+ "</data><data><sec_nm>TXPDA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess>"
						+ "<ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>HSN</sec_nm><ttl_rec>1</ttl_rec><ttl_tax>3359300</ttl_tax><ttl_igst>318885</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>3678185</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued>"
						+ "<ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>EXP</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>CDNURA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst>"
						+ "<ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>NIL</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt>"
						+ "<ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>CDNRA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>CDNR</sec_nm><ttl_rec>2</ttl_rec><ttl_tax>2000</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>180</ttl_sgst><ttl_cgst>180</ttl_cgst><ttl_val>2360</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued>"
						+ "<ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>B2CL</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data>"
						+ "<data><sec_nm>B2CSA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>B2CS</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled>"
						+ "<net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>AT</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>ATA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst>"
						+ "<ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>TXPD</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>B2BA</sec_nm>"
						+ "<ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>B2CLA</sec_nm><ttl_rec>0</ttl_rec><ttl_tax>0</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>0</ttl_sgst><ttl_cgst>0</ttl_cgst><ttl_val>0</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt>"
						+ "<ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data><data><sec_nm>B2B</sec_nm><ttl_rec>1</ttl_rec><ttl_tax>200000</ttl_tax><ttl_igst>0</ttl_igst><ttl_sgst>18000</ttl_sgst><ttl_cgst>18000</ttl_cgst><ttl_val>236000</ttl_val><ttl_cess>0</ttl_cess><ttl_doc_issued>0</ttl_doc_issued><ttl_doc_cancelled>0</ttl_doc_cancelled><net_doc_issued>0</net_doc_issued><ttl_expt_amt>0</ttl_expt_amt><ttl_ngsup_amt>0</ttl_ngsup_amt><ttl_nilsup_amt>0</ttl_nilsup_amt></data></sec_sum></Gstr1DocSummary>";

			}

			// Look up the connectivity configuration API
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");

			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration
					.getConfiguration(destination);
			if (destConfiguration == null) {
				/*
				 * response.sendError(HttpServletResponse.
				 * SC_INTERNAL_SERVER_ERROR, String.format(
				 * "Destination %s is not found. Hint: Make sure to have the destination configured."
				 * , destinationName));
				 */
				LOGGER.error("destConfiguration", destConfiguration);
				return null;
			}

			// Get the destination URL
			String value = destConfiguration.getProperty("URL");
			URL url = new URL(value);

			String proxyType = destConfiguration.getProperty("ProxyType");
			Proxy proxy = getProxy(proxyType);

			urlConnection = (HttpURLConnection) url.openConnection(proxy);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type",
					"application/xml; charset=utf-8");

			urlConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					urlConnection.getOutputStream());
			wr.writeBytes(xml);
			wr.flush();
			wr.close();
			String responseStatus = urlConnection.getResponseMessage();
			System.out.println(responseStatus);

			int responseCode = urlConnection.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String inputLine;
				StringBuffer respons = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					respons.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(respons.toString());
			} else {
				System.out.println("POST request not worked");
			}
			// Insert the required header in the request for on-premise
			// destinations
			// injectHeader(urlConnection, proxyType);

			// Copy content from the incoming response to the outgoing response
			// InputStream instream = urlConnection.getInputStream();
			// OutputStream outstream = response.getOutputStream();
			// copyStream(instream, outstream);
		} catch (IOException io) {
			io.printStackTrace();
		} catch (Exception e) {
			// Connectivity operation failed
			String errorMessage = "Connectivity operation failed with reason: "
					+ e.getMessage() + ". See "
					+ "logs for details. Hint: Make sure to have an HTTP proxy configured in your "
					+ "local environment in case your environment uses "
					+ "an HTTP proxy for the outbound Internet "
					+ "communication.";
			LOGGER.error("Connectivity operation failed", e);
			/*
			 * try {
			 * response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			 * errorMessage); } catch (IOException e1) { // TODO Auto-generated
			 * catch block e1.printStackTrace(); }
			 */
		}
		return null;
	}

	private Proxy getProxy(String proxyType) {
		String proxyHost = null;
		int proxyPort;
		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			LOGGER.error("proxyHost", proxyHost);
			proxyPort = Integer
					.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));
			LOGGER.error("proxyPort", proxyPort);
		} else {
			// Get proxy for internet destinations
			proxyHost = System.getProperty("http.proxyHost");
			proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
			LOGGER.error("proxyHost", proxyHost);
			LOGGER.error("proxyPort", proxyPort);
		}
		return new Proxy(Proxy.Type.HTTP,
				new InetSocketAddress(proxyHost, proxyPort));
	}

}