/**
 * 
 */
package com.ey.advisory.app.dms;

import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.HttpUriRequest;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.PerformRetryException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author vishal.verma
 *
 */
@Component("DmsUtils")
public class DmsUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final int RETRY_COUNT = 2;

	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public static String extractErrorMessage(String apiResp) {
		if (apiResp == null || apiResp.isEmpty()) {
			return "Empty response from API";
		}
		try {
			JsonNode rootNode = objectMapper.readTree(apiResp);

			if (rootNode.has("success") && rootNode.get("success").isBoolean()
					&& !rootNode.get("success").asBoolean()) {
				// Extract "message" field if available
				if (rootNode.has("message")
						&& rootNode.get("message").isTextual()) {
					return rootNode.get("message").asText();
				}
			}
		} catch (Exception e) {
			return "Error parsing response in apiResp of DMS";
		}

		return "";
	}

	/*
	 * public Pair<String, Integer> execute(HttpUriRequest req, Map<String,
	 * Object> context) throws Exception { int retryCount = 0; Pair<String,
	 * Integer> pair = null; String reqUrl = req.getURI().toString(); while
	 * (true) { try { if (LOGGER.isDebugEnabled())
	 * LOGGER.debug("Hitting GSTN API {} for {} time", reqUrl, (retryCount +
	 * 1)); return executeApi(req, context); } catch (PerformRetryException ex)
	 * { retryCount++; pair = ex.getRespPair(); LOGGER.error(
	 * "We have received Invalid Response from GSTN," + " Response is {}," +
	 * " Hence About retry GSTN API {} for {} time", pair.getValue0(), reqUrl,
	 * (retryCount + 1));
	 * 
	 * 
	 * if (pair.getValue0().contains("API plan limit exceeded")) {
	 * 
	 * eyConfigRepo.updateValueOnKey( PublicApiConstants.END_POINT_KEY,
	 * PublicApiConstants.ALANKIT);
	 * 
	 * }
	 * 
	 * if (pair.getValue0().contains("API plan limit exceeded") || retryCount >=
	 * RETRY_COUNT) { break; } } } return pair; }
	 */
}
