package com.ey.advisory.gstin.manualtest;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class AuthTest {

	public static void main(String[] args) throws Exception {
		try{
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials
			 = new UsernamePasswordCredentials("nikhil.duseja", "Thismonth@1995");
			provider.setCredentials(AuthScope.ANY, credentials);
			HttpClient client = HttpClientBuilder.create().disableRedirectHandling().setDefaultCredentialsProvider(provider).build();
	        HttpHost proxy = new HttpHost("ingssweb.ey.net", 8080, "http");
	         RequestConfig config = RequestConfig.custom()
	                 .setProxy(proxy)
	                 .build();
	         
			HttpPost httpPost = new HttpPost("https://api.eygsp.in/commonapi/v0.2/authenticate");
			//HttpPost httpPost = new HttpPost("https://devapi.gst.co.in/taxpayerapi/v0.2/authenticate");
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("digigst_username", "commonapiuser");
			String password = "Api@706com";
			String encryptedPassword = CryptoUtilMock.encrypt(password.getBytes());
			httpPost.setHeader("password",encryptedPassword);
			httpPost.setHeader("action","ACCESSTOKEN");
			httpPost.setHeader("api_key", "l7xxc7fa4929e61b48c09f5d8111f6d39f10");
			httpPost.setHeader("api_secret", "17f8ce50012f46b1bb038733dd17c177");
			httpPost.setHeader("client_id", "aspAdmin");
			httpPost.setHeader("client_secret", "Password1");
			
			//httpPost.setConfig(config);
			
			String appkey = CryptoUtilMock.generateSecureKey();
			System.out.println("App Key is " + appkey);
			String encryptedAppkey = CryptoUtilMock.generateEncAppkey(CryptoUtilMock
						.decodeBase64StringTOByte(appkey));
			
			String otpJson = "{\"action\":\"ACCESSTOKEN\",\"username\" : \"commonapiuser\",\"password\" :\""+ encryptedPassword + "\",\"app_key\":\"" + encryptedAppkey
					+ "\"}";
			StringEntity otpEntity = new StringEntity(otpJson);
			httpPost.setEntity(otpEntity);
			httpPost.setHeader("Accept", "*/*");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse response = client.execute(httpPost);
			String otpResponseJsonString = EntityUtils.toString(response.getEntity());;
			System.out.println("Response is " + otpResponseJsonString);
			
			/*String otp = "102466";
			String encryptedOtp = CryptoUtilMock.encryptEK(otp.getBytes(),
					CryptoUtilMock.decodeBase64StringTOByte(appkey));
			String authTokenJson = "{\"action\":\"AUTHTOKEN\",\"username\" : \"eny.tn.1\"," + "\"app_key\":\"" + encryptedAppkey
					+ "\", \"otp\":\"" + encryptedOtp + "\"}";
			
			String reqJson = "{\"action\":\"ACCESSTOKEN\",\"username\":\"bouser2\",\"password\":\"kAzgNZ8O1WnnJKKAJSKAasdakw\","
			+ "\"app_key\":\" exSQrNDwWm2UwIu8zYZZC+1u8Nrwg2Xg6+JByqBVJ4Pfcb0eoK7BFP8Zv/zQtdS+AOhNWrtSAOo4NhnsxqmXerF5eMxal86UB0tQgnfgEP6ODRz94l4bqJvW7ZcUpA3uexfQE6K3wYh6uNq2VkrDpig3EPPNBqU6untAVkdygjrY8+ssBcVOeJA/nAmI2BQGvuo/F/VwL3rb4xnp0sYG+44oAA7CP5QWwtaGoWOv0lwNwZZG+s4+rHhHSuUfFpyZ1vqubblMKrEuXy4ndlmd4gafW1lcNVEEznr5yhEY1L2N0JVu2wkbBgGZMb6N0xorEzU9FQiX5PK6ZWkoJBQD4A==\"}";
			
			
			System.out.println(authTokenJson);
			StringEntity authTokenEntity = new StringEntity(authTokenJson);
			httpPost.setEntity(authTokenEntity);
			
			HttpResponse authTokenresponse = client.execute(httpPost);
			
			// TODO: Need to test the code after changing from fasterxml
			// JSON to Google gson library.
			
			String responseJsonString = EntityUtils.toString(authTokenresponse.getEntity());
			
			System.out.println(responseJsonString);*/
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
}