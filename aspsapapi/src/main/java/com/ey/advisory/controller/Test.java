package com.ey.advisory.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.dto.DummyDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class Test {

	public static void main(String[] args) throws SerialException, SQLException  {
		
		try {
			File t1 = new File("C:\\Users\\KG712ZX\\OneDrive - EY\\Desktop\\AlterScript.txt");
			
			
			File temp = Files.createTempDirectory("TempDirectory").toFile();
			File downloadFiles = new File(temp.getAbsolutePath()+"\\"+"Downloaded files"); 
			downloadFiles.mkdirs();
			
			FileUtils.copyFile(t1, downloadFiles);
			
			System.out.println(temp.getAbsolutePath());
			System.out.println(downloadFiles.getAbsolutePath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		String jsonString = "{\"req\":{\"entityName\":\"EY_SALE\",\"payload\":{\"header\":{\"documenttype\":\"INV\",\"suppliergstin\":\"29ABYPR4788F1ZJ\"}}}}";
		
		JsonObject obj = new JsonParser().parse(jsonString)
				.getAsJsonObject();
		
		DummyDto dummyDto = gson.fromJson(obj.get("req"),
				DummyDto.class);
		
		String payload = StringEscapeUtils.unescapeJava(dummyDto.getPayload().toString());
		
		System.out.println(payload);
		
		// String response = generateEinvoiceRequest.dumpEinvRequest(jsonString);
		
		Clob reqClob = new javax.sql.rowset.serial.SerialClob(
				payload.toCharArray());
		
		System.out.println(reqClob);
		
		/*String response = "No Records Found";
		System.out.println(gson.toJsonTree(response));
		
		String ii= "{\"header\":\"{\"companycode\":\"GS01\",\"documenttype\":\"INV\",\"suppliergstin\":\"29ABYPR4788F1ZJ\",\"documentnumber\":\"0090005007\",\"documentdate\":\"2020-12-03\"}\"}";

		
		
	    System.out.println(obj);
	    // a\tb\n\"c\"

	    String out = StringEscapeUtils.unescapeJava(ii);

	    System.out.println(out);*/
		
		/*JsonObject jsonResp = (new JsonParser().parse(response))
				.getAsJsonObject();
		System.out.println(jsonResp);
		JsonElement respBody = gson.toJsonTree(jsonResp);
		
		System.out.println(respBody);*/
		jasperTest();
		
		List<String> list = Arrays.asList("02AFSF","05DFGKDG","01SGHFDAHS");
		System.out.println(list);
		Collections.sort(list);
		System.out.println(list);
		
		String monthName = "aug";
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(inputFormat.parse(monthName));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
		String format = outputFormat.format(cal.getTime());
		
		String tax = "01" + format + "2019";
		
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("ddMMyyyy");
		LocalDate returnPeriod = LocalDate.parse(tax, formatter);
		LocalDate returnPeriod1 = returnPeriod.with(TemporalAdjusters.lastDayOfMonth());
		
		
		System.out.println(returnPeriod1);
		
		/*
		
		//String obj = "123456E8912";
		//String obj = "1.2345678912E10";
		String obj = "1.23457E+16";
		
		
		if (obj.toString().matches("[0-9.-]*Ee[0-9.-]*")) {
			BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
			docNoDecimalFormat = new BigDecimal(obj.toString());
			Long supplierPhoneLong = docNoDecimalFormat.longValue();
			 System.out.println("value  ::"+ String.valueOf(supplierPhoneLong));
			System.out.println("Mahesh Written");
		}
	
		if (obj.toString()
				.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {
			BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
			docNoDecimalFormat = new BigDecimal(obj.toString());
			Long supplierPhoneLong = docNoDecimalFormat.longValue();
			 System.out.println("value  :"+ String.valueOf(supplierPhoneLong));
			//object = String.valueOf(supplierPhoneLong);
			System.out.println("Sai Written");
		}
	
	*/}

	private static void jasperTest() {
		
		try{
		JasperPrint jasperPrint = null;
		//String source = "jasperReports/summaryewbprint.jrxml";
		String source = "jasperReports/detailedewbprint.jrxml";
		Map<String, Object> parameters = new HashMap<>();
		
		
		File file = ResourceUtils.getFile("classpath:" + source);

		JasperReport jasperReport = JasperCompileManager
				.compileReport(file.toString());
		jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
				new JREmptyDataSource());
		}catch(Exception e){
		//	e.printStackTrace();
		}
		
	}

}
