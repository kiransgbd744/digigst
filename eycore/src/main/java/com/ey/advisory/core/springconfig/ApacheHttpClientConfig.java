package com.ey.advisory.core.springconfig;

import static com.ey.advisory.core.springconfig.HttpClientType.EXTERNAL;
import static com.ey.advisory.core.springconfig.HttpClientType.INTERNAL;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

@Configuration
@EnableTransactionManagement
public class ApacheHttpClientConfig {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApacheHttpClientConfig.class);

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	@Autowired
	@Qualifier("TrustStoreLoader")
	private TrustStoreLoader trustStoreLoader;

	private static final String GSTN_PROXY_URL = 
			"api.gstn.global.proxy_url";

	private static final String GSTN_PROXY_USERNAME = 
			"api.gstn.global.proxy_username";

	private static final String GSTN_PROXY_PWD = 
			"api.gstn.global.proxy_password";

	private static final String GSTN_PROXY_FLAG = 
			"api.gstn.global.is_proxy_req";

	private static final String INTERNAL_PROXY_URL = 
			"proxy.internal.proxy_url";

	private static final String INTERNAL_PROXY_USERNAME = 
			"proxy.internal.proxy_username";

	private static final String INTERNAL_PROXY_PWD = 
			"proxy.internal.proxy_password";

	private static final String INTERNAL_PROXY_FLAG = 
			"proxy.internal.is_proxy_req";

	@Bean("GSTNHttpClient")
	public HttpClient getGSTNHttpClient() {
		return buildHttpClient(EXTERNAL);
	}
	
	@Bean("InternalHttpClient")
	public HttpClient getEYInternalHttpClient() {
		return buildHttpClient(INTERNAL);
	}	
	
	private HttpClient buildHttpClient(HttpClientType type) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loading the HTTP Client for Type: '%s'", 
					type.toString()));
		}
		
		// Firstly, load the required Trust store.		
		KeyStore trustStore = trustStoreLoader.loadTrustStore();
		
		// Then, build the pooling connection manager with the trust store
		// loaded.
		PoolingHttpClientConnectionManager connMngr = 
				buildConnectionMngr(trustStore);
		
		// Load the proxy details. The first boolean represents whether the
		// proxy is required.
		Quartet<Boolean, String, String, String> proxyDet = 
				loadProxyDetails(type);
		
		HttpClient httpClient = null;
		if (!proxyDet.getValue0()) {
			httpClient = HttpClients.custom().setConnectionManager(connMngr)
					.build();
		} else {

			// Build the credentials provider for the proxy and the route to the
			// proxy.
			Pair<CredentialsProvider, HttpRoutePlanner> proxyAuthAndRoute = 
					buildProxyAuthAndRoute(proxyDet);
			CredentialsProvider provider = proxyAuthAndRoute.getValue0();
			HttpRoutePlanner routePlanner = proxyAuthAndRoute.getValue1();

			// If the proxy requires authentication, then build an
			// HttpClient with the credentials provider.
			if (proxyAuthAndRoute.getValue0() != null) {
				httpClient = HttpClients.custom().setConnectionManager(connMngr)
						.setDefaultCredentialsProvider(provider)
						.setRoutePlanner(routePlanner).build();
			} else {

				// Otherwise, build an HttpClient without the credentials
				// provider.
				httpClient = HttpClients.custom().setConnectionManager(connMngr)
						.setRoutePlanner(routePlanner).build();
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loaded the HTTP Client for Type: '%s'", 
					type.toString()));
		}
		return httpClient;
	}
	
	/**
	 * Create a Pooling Connection Manager configured with the specified 
	 * trust store.
	 * 
	 * @param trustStore The trust store to load.
	 * 
	 * @return The Pooling Http Client Connection Manager with the specified
	 * 		custom trust store configured.
	 */
	private PoolingHttpClientConnectionManager buildConnectionMngr(
			KeyStore trustStore) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading the Pooling Connection Manager "
					+ "with the specified trust store");
		}
		
		SSLContext context = null;
		try {
			context = SSLContexts.custom()
				.loadTrustMaterial(trustStore, null)
				.build();			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Loaded the SSLContext");
			}
		} catch(KeyStoreException | KeyManagementException | 
				NoSuchAlgorithmException ex) {
			String msg = "Error while building the SSLContext to "
					+ "load the PoolingHttpConnectionManager";
			// Rethrow the exception here, so that the server wouldn't startup.
			throw new AppException(msg, ex);
		}
		
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				context, new DefaultHostnameVerifier());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loadedthe SSLConnectionSocketFactory "
					+ "with the Default Host Name Verifier.");
		}
		Registry<ConnectionSocketFactory> registry = RegistryBuilder
				.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory())
				.register("https", sslsf)
				.build();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loaded the Registry that stores "
					+ "SocketConnectionFactory objects for "
					+ "different protocol schemes.");
		}
		
		// The Pooling Http Connection Manager should be built with the 
		// registry containing the relevant socket factories. This is very 
		// important, as the HttpClientBuilder will ignore any socket factory
		// if a connectoin manager is set. This will lead to the use of 
		// the default trust store, instead of the configured trust store, as 
		// the trust store is a part of the SSLContext configured within the
		// socket connection factory. Hence, this step is very important if 
		// we need to override the default jdk trust store with a trust store
		// loaded from a custom file.
		PoolingHttpClientConnectionManager connMngr = 
				new PoolingHttpClientConnectionManager(registry);
		connMngr.setDefaultMaxPerRoute(500);
		connMngr.setMaxTotal(500);		
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loaded the Pooling Connection Manager "
					+ "with the specified trust store");
		}
		
		return connMngr;
	}
	
	private Quartet<Boolean, String, String, String> 
						loadProxyDetails(HttpClientType hct) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loading proxy details from Global "
					+ "Config for type: '%s'", hct));			
		}
		boolean isExt = (hct == EXTERNAL);
		
		// Load the configuration map based on the type of HttpClient that we
		// have to load.
		String configCateg = isExt ? "GSTNAPI" : "EYInternal";
		String configKeyPrefix = isExt ? "api" : "proxy";		
		Map<String, Config> configMap = configManager.getConfigs(configCateg,
				configKeyPrefix);				
		
		// Check if a proxy is required (based on the configuration)
		String proxyFlag = isExt ? GSTN_PROXY_FLAG : INTERNAL_PROXY_FLAG;
		boolean isProxyReq = Boolean
				.parseBoolean(configMap.get(proxyFlag).getValue());
		
		if (!isProxyReq && LOGGER.isDebugEnabled()) {
			LOGGER.debug("Porxy is not required... "
					+ "Skipping the loading of other proxy configs!!");
		}
		
		// If a proxy is not required, then return that information with all
		// the other proxy details set to null.
		if (!isProxyReq) return new Quartet<>(false, null, null, null);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Proxy is required... Loading other proxy configs!!");
		}
		
		// If proxy is required, then load the Proxy URL, Proxy User Name
		// and Proxy Password.
		String urlConfKey = isExt ? GSTN_PROXY_URL : INTERNAL_PROXY_URL;
		String userConfKey = isExt ? 
				GSTN_PROXY_USERNAME : INTERNAL_PROXY_USERNAME;
		String userPwdKey = isExt ? GSTN_PROXY_PWD : INTERNAL_PROXY_PWD;
				
		String proxyUrl = configMap.containsKey(urlConfKey) ? 
				configMap.get(urlConfKey).getValue() : "";
		String proxyUserName = configMap.containsKey(userConfKey) ? 
				configMap.get(userConfKey).getValue() : "";
		String proxyPassword = configMap.containsKey(userPwdKey) ? 
				configMap.get(userPwdKey).getValue() : "";		
		
		if (LOGGER.isDebugEnabled()) {
			// Not printing password.
			LOGGER.debug(String.format("Proxy Details Loaded.. URL = [%s] and "
					+ "Proxy User: [%s]", proxyUrl, proxyUserName));
		}
		
		// return the proxy details.
		return new Quartet<>(true, proxyUrl, proxyUserName, proxyPassword);
	}
	
	private Pair<CredentialsProvider, HttpRoutePlanner> 
				buildProxyAuthAndRoute(
						Quartet<Boolean, String, String, String> proxyDetails) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading Credentials Provider and "
					+ "RoutePlanner for the proxy...");
		}

		
		boolean isProxyReq = proxyDetails.getValue0();		
		String proxyUrl = proxyDetails.getValue1();
		String userName = proxyDetails.getValue2();
		String password = proxyDetails.getValue3();
		
		if (!isProxyReq && LOGGER.isDebugEnabled()) {
			LOGGER.debug("Skipping CredentialsProvider and RoutePlanner "
					+ "Loading... as proxy is not required!!");
		}
		
		// If proxy is not required, then return null for everything.
		if (!isProxyReq) return new Pair<>(null, null);
		
		// Otherwise, build the credentials provider and the route planner.
		URL url;
		try {
			url = new URL(proxyUrl);
		} catch (MalformedURLException ex) {
			// Re-throw the exception so that the server wouldn't start.
			throw new AppException(String.format(
					"Invalid Proxy URL configured in Config. URL = [%s]", 
					proxyUrl), ex);
		}
		HttpHost proxy = new HttpHost(
				url.getHost(), url.getPort(), url.getProtocol());
		DefaultProxyRoutePlanner routePlanner =  
				new DefaultProxyRoutePlanner(proxy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loaded the Default Proxy RoutePlanner "
					+ "for the Proxy URL: [%s]", proxyUrl));
		}
		CredentialsProvider provider = null;
		if (userName != null && !userName.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Loading the Credentials Provider. "
						+ "Proxy User name = [%s]", userName));
			}
			provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials = 
					new UsernamePasswordCredentials(userName, password);
			provider.setCredentials(AuthScope.ANY, credentials);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Loaded the Credentials Provider. "
						+ "Proxy User name = [%s]", userName));
			}			
		}		
		
		return new Pair<>(provider, routePlanner);
	}

}
