/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.client.HttpClient;
import org.javatuples.Pair;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.ProcessingStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Slf4j
public class ExecTokenTask implements Runnable {

	HttpClient httpClient = StaticContextHolder.getBean("GSTNHttpClient",
			HttpClient.class);

	private ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap;
	private CountDownLatch latch;
	private String url;
	private String ek;
	private String sk;
	private Long requestId;
	private Map<String, String> headerMap;

	public ExecTokenTask(
			ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap,
			CountDownLatch latch, String url, String ek, String sk,
			Long requestId, Map<String, String> headerMap) {
		super();
		this.tasksMap = tasksMap;
		this.latch = latch;
		this.url = url;
		this.ek = ek;
		this.sk = sk;
		this.requestId = requestId;
		this.headerMap = headerMap;
	}

	@Override
	public void run() {
		GZIPInputStream is = null;
		TarArchiveInputStream ti = null;
		InputStream insStream = null;
		TenantContext.setTenantId(headerMap.get("groupCode"));
		try {
			url = APIInvokerUtil.resolveProcessingTokenUrl(url);
			int retryCount = GstnApiWrapperConstants.URL_RETRY_COUNT;
			while (retryCount-- > 0) {
				try {
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Trying URL {} for {} time ", url, retryCount);
					}
					UrlProcessingBlockImpl urlBlock = new UrlProcessingBlockImpl();
					urlBlock.processUrl(
							url, ek, requestId, is, ti, insStream, headerMap);
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Retry {} is processed successfully ", retryCount);
					}
					tasksMap.put(url, new Pair<ProcessingStatus, Exception>(
							ProcessingStatus.COMPLETED, null));
				} catch (Exception e) {
					if(retryCount == 1)
						throw new Exception("Exception while processing the url"
								, e);
					continue;
				}
				break;
			}
		} catch (Exception ex) {
			// Set the status of the job as failed in the concurrent hash map.
			// Capture the exception for later logging.
			tasksMap.put(url, new Pair<ProcessingStatus, Exception>(
					ProcessingStatus.FAILED, ex));
			String msg = String.format(
					"MultiTenantTaskProcessor for "
							+ "processing message  for url: '%s' " + "failed!!",
					url);
			LOGGER.error(msg, ex);

		} finally {
			latch.countDown();
			TenantContext.clearTenant();
			closeResources(is, ti, insStream);
		}

	}

	private void closeResources(GZIPInputStream is, TarArchiveInputStream ti,
			InputStream insStream) {

		try {
			if (ti != null)
				ti.close();
			if (is != null)
				is.close();
			if (insStream != null)
				insStream.close();
		} catch (IOException e) {
			String msg = "Exception while logging the resources";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

}
