/**
 * 
 */
package com.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.web.client.RestTemplate;

/**
 * @author QD194RK
 *
 */
public class NetClientGet {

	public static void main(String[] args) {
		try {

			RestTemplate var = new RestTemplate();
			//var.getForObject("https://api.exchangeratesapi.io/latest?symbols=INR,GBP&base=EUR", new JsonObject());
			
			URL url = new URL(
					"https://api.exchangeratesapi.io/latest?symbols=INR,GBP&base=EUR");// your uri
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			System.out.println(conn);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException(
						"Failed : HTTP Error code : " + conn.getResponseCode());
			}
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Exception in NetClientGet:- " + e);
		}
	}
}
