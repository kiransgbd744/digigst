package com.ey.advisory.gstin.manualtest;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SaveGSTR1Data {

	public static void main(String[] args) {
		try{
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials
			 = new UsernamePasswordCredentials("Sai.Pakanati", "XXXXX");
			provider.setCredentials(AuthScope.ANY, credentials);
			HttpClient client = HttpClientBuilder.create().disableRedirectHandling().setDefaultCredentialsProvider(provider).build();
	        HttpHost proxy = new HttpHost("r2inblrubci13pxy01.mea.ey.net", 8080, "http");
	         RequestConfig config = RequestConfig.custom()
	                 .setProxy(proxy)
	                 .build();
			String sek = "fRyDxslYeHu4atRRlAhf64V7l8/LPwjzEH1coqKGay8Qbp6I26gU2tynPPCDVKa6";
			String authToken = "6f35ad10fc3344bb8159a7c2174cba91";
			String appKey = "2+SsHUYqYHW4kh9qx6Wf7zBDh98laYFb7ARM+8OugWQ=";
			String skStr = CryptoUtilMock.encodeBase64String(CryptoUtilMock.decrypt(sek,
					CryptoUtilMock.decodeBase64StringTOByte(appKey)));
			System.out.println("sk is " + skStr);
			byte[] sk = CryptoUtilMock.decrypt(sek,
					CryptoUtilMock.decodeBase64StringTOByte(appKey));
			String data = "{\"gstin\":\"33GSPTN0481G1ZA\",\"fp\":\"112018\",\"gt\":3782969.01,\"cur_gt\":3782969.01,\"b2b\":[{\"ctin\":\"33GSPTN0482G1Z9\",\"inv\":[{\"inum\":\"S008400\",\"idt\":\"24-10-2018\",\"val\":729248.16,\"pos\":\"06\",\"rchrg\":\"N\",\"inv_typ\":\"R\",\"diff_percent\":0.65,\"itms\":[{\"num\":1,\"itm_det\":{\"rt\":5,\"txval\":10000,\"iamt\":325,\"csamt\":500}}]}]}]}";
			String base64Data = CryptoUtilMock.encodeBase64String(data.getBytes());
			String encryptedData = CryptoUtilMock
					.encodeBase64String(CryptoUtilMock.encryptData(base64Data, sk));
			String hmac = CryptoUtilMock.hmacSHA256(base64Data, sk);
			
			String saveDataJson = "{\"action\":\"RETSAVE\"," + "\"data\":\"" + encryptedData
					+ "\", \"hmac\":\"" + hmac + "\"}";
			System.out.println("Request Body is in" + saveDataJson);
			HttpPut httpPut = new HttpPut("https://api.eygsp.in/digigst/taxpayerapi/v1.1/returns/gstr1");
			httpPut.setHeader("Content-Type", "application/json");
			httpPut.setHeader("digigst_username", "abhijeet.singh@devldaps.eygsp.in");
			httpPut.setHeader("api_key", "l7xx9e8666eada1e4d7eb79223f49466e860");
			httpPut.setHeader("api_secret", "383ac288ff024ebaa9ef2d44b26fccc6");
			httpPut.setHeader("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKU1V6STFOaUlzSW5nMWRDSTZJbWxDYWt3eFVtTnhlbWhwZVRSbWNIaEplR1JhY1c5b1RUSlpheUlzSW10cFpDSTZJbWxDYWt3eFVtTnhlbWhwZVRSbWNIaEplR1JhY1c5b1RUSlpheUo5LmV5SmhkV1FpT2lKb2RIUndjem92TDJkemNGTmxjblpwWTJWekx5SXNJbWx6Y3lJNkltaDBkSEJ6T2k4dmMzUnpMbmRwYm1SdmQzTXVibVYwTHprd09XWmxOemt4TFROa01UUXROR1EzT1MxaU5URmlMVEJqWW1JNU16WmpNamhsWWk4aUxDSnBZWFFpT2pFMU1qYzVNall6TkRNc0ltNWlaaUk2TVRVeU56a3lOak0wTXl3aVpYaHdJam94TlRJM09UTXdNVGd6TENKaFkzSWlPaUl4SWl3aVlXbHZJam9pV1RKa1oxbE9RbG8zZFRoMkt6aEpOVEZQYjBjM05IcHVNemRyVURrM1RWZElNalJXTWs1cmR6ZFhha0k0Wlhac1dsWlpRU0lzSW1GdGNpSTZXeUp3ZDJRaVhTd2lZWEJ3YVdRaU9pSmhOV000TlRrd01TMHlPVEEyTFRRek5UQXRZV1UxTUMwMU1UQXhZMkkwTmprMllqVWlMQ0poY0hCcFpHRmpjaUk2SWpFaUxDSm1ZVzFwYkhsZmJtRnRaU0k2SWxOcGJtZG9JaXdpWjJsMlpXNWZibUZ0WlNJNklrRmlhR2xxWldWMElpd2lhWEJoWkdSeUlqb2lNVEEwTGpJeE1TNDRPQzQwTnlJc0ltNWhiV1VpT2lKQlltaHBhbVZsZENCVGFXNW5hQ0lzSW05cFpDSTZJbUU0TVRZNFl6ZzVMV05rT1RBdE5HUTJaaTFpT0RJeExXUXdNV00wT0RJNVlUTTVZeUlzSW5OamNDSTZJblZ6WlhKZmFXMXdaWEp6YjI1aGRHbHZiaUlzSW5OMVlpSTZJbEpuYlZCV2QzaENWemRPTkROdVoyZFpjbEJRTW1GWFJsTkNTa1JJTWtScFpGcDJNMDQ0VDBkQ1JqZ2lMQ0owYVdRaU9pSTVNRGxtWlRjNU1TMHpaREUwTFRSa056a3RZalV4WWkwd1kySmlPVE0yWXpJNFpXSWlMQ0oxYm1seGRXVmZibUZ0WlNJNkltRmlhR2xxWldWMExuTnBibWRvUUdWNVozTndMbWx1SWl3aWRYQnVJam9pWVdKb2FXcGxaWFF1YzJsdVoyaEFaWGxuYzNBdWFXNGlMQ0oxZEdraU9pSnVTR1pZVWtrdGFrWkZkVUY0ZUc5a1pWQlpWa0ZCSWl3aWRtVnlJam9pTVM0d0luMC5YVTFPODdtdTk1Z3Z0NkVJdGRlbjdQc1NrUnNtVVMxQWtuSHItdXZHbU9PUzlNUXhvM2U5dWQzNnpiYURQdnEzVGpXYzlsVTlaOVl1WV8waVpucEtnenk5d0NoYTJ6NDlEQnhQNHlSWHNvWUd3MWg3X05rekpib1QwaGpscGVhem40M294NnJjUlhfMzc1OU1aQktEb1ZGdkdQZTY0d0hqTmlXYlpFaFZmTUFzS2NRODJQRjl0dHgzN0xlUWFiWXEzUUhPN0ljUWRPaWtZMjJWSTlkcEpYQmdCajlzOGMxX2V6Yi1jeEU4SG9jWkh0WHhsb2xaYUFXeEdRLVVRUDlyYXM0a0pNalcyTExqZDhfbEJVdVcwX2RSS19jWGxsd3czOHozWi1jWF8yUngtZndvcWNhWjNSNjRPOTBKcVMtTHBfRl9rQzhRd2d5MEhqWG00cVg0SlEiLCJpYXQiOjE1Mjc5MjY2NDN9.lqAfo_V3CVil1QW16gi1ZbOSjCsTJZOYMbizUzOozCY");
			httpPut.setHeader("ip-usr", "52.172.209.208");
			httpPut.setHeader("state-cd", "33");
			httpPut.setHeader("txn", "returns");
			httpPut.setHeader("username", "EnY.TN.1");
			httpPut.setHeader("gstin", "33GSPTN0481G1ZA");
			httpPut.setHeader("auth-token", authToken);
			httpPut.setHeader("ret_period", "112018");
			httpPut.setConfig(config);
			
			StringEntity saveDataEntity = new StringEntity(saveDataJson);
			httpPut.setEntity(saveDataEntity);
			
			HttpResponse saveDataResponse = client.execute(httpPut);
			
			String saveResponseString = EntityUtils.toString(saveDataResponse.getEntity());
			
			System.out.println(saveResponseString);
			
			JsonParser parser = new JsonParser();
			JsonObject jsonObjectAuth = 
					parser.parse(saveResponseString).getAsJsonObject();
			String statusCode = jsonObjectAuth.get("status_cd").getAsString();
			if(!"1".equals(statusCode)) {
				System.out.println("Api call is failed with error response");
				return ;
			}
			String encryptedResponse = jsonObjectAuth.get("data").getAsString();
			String rek = jsonObjectAuth.get("rek").getAsString();
			//String hmacReceived = jsonObjectAuth.get("hmac").getAsString();
			byte[] apiek = CryptoUtilMock.getapiEK(rek, sk);
			String decryptedResponse = CryptoUtilMock.getJsonData(encryptedResponse, apiek);
			System.out.println("Final decrypted response is " + decryptedResponse);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

}
