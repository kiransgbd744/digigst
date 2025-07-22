package com.ey.advisory.app.services.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.GSTConstants;

@Component
public class Utility {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);

	@Autowired
	private DocRepository docRepository;
	
	private static final ThreadLocal<Map<String, SimpleDateFormat>> 
	threadLocal = new ThreadLocal<Map<String, SimpleDateFormat>>() {};

	public boolean checkAggInvoiceThreshold(OutwardTransDocument document,
			int value) {

		BigDecimal aggInvoiceValue = document.getDocAmount();
		if (GSTConstants.CR.equalsIgnoreCase(document.getDocType())
				&& aggInvoiceValue != null) {
			aggInvoiceValue = document.getDocAmount().abs();
		}

		BigDecimal aggInvoice = aggInvoiceValue == null ? new BigDecimal(0)
				: aggInvoiceValue;

		return (aggInvoice.compareTo(new BigDecimal(value)) > 0);
	}

	public boolean isB2CLInvoice(OutwardTransDocument document) {
		String orgDocNo = document.getPreceedingInvoiceNumber();
		LocalDate orgDocDate = document.getPreceedingInvoiceDate();
		String docType = "INV";
		String sgtin = document.getSgstin();
		List<OutwardTransDocument> doc = docRepository
				.findByDocNoAndDocDateAndDocTypeAndSgstin(orgDocNo,
						orgDocDate, docType, sgtin);
		return doc != null && !doc.isEmpty();
	}

	public boolean isB2CLAInvoice(OutwardTransDocument document) {
		String orgDocNo = document.getPreceedingInvoiceNumber();
		LocalDate orgDocDate = document.getPreceedingInvoiceDate();
		String cgstin = document.getCgstin();
		String docType = document.getDocType();
		String sgtin = document.getSgstin();
		List<OutwardTransDocument> doc = docRepository
				.findByDocNoAndDocDateAndCgstinAndDocTypeAndSgstin(orgDocNo,
						orgDocDate, cgstin, docType, sgtin);
		return doc != null && !doc.isEmpty();
	}
	
	public static boolean isPreviousCutoffDate(String taxPeriod,
			String cutoffDate) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(getDateByPattern(taxPeriod));
		cal2.setTime(getDateByPattern(cutoffDate));

		if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
				&& (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) || cal1
						.get(Calendar.MONTH) < cal2.get(Calendar.MONTH))) {
			return true;
		}

		return false;
	} 
	
	public static Date getDateByPattern(String dateStr) {
		Map<String, SimpleDateFormat> patternMap = threadLocal.get();
		if (patternMap == null || patternMap.isEmpty()) {
				initValues();	
				patternMap = threadLocal.get();
		}
		for (String key : patternMap.keySet()) {
			if (dateStr.length() == key.length()) {				
				try {
					return patternMap.get(key).parse(dateStr);
				} catch (ParseException parseException) {
					LOGGER.error("KEY : "+key);
					LOGGER.error(parseException.getMessage(), parseException);
				}
			}
		}
		throw new RuntimeException("Unknown Date Format for " + dateStr);
	} 
	
	public static void initValues() {
		Map<String, SimpleDateFormat> mapDatePatterns = 
				new HashMap<String, SimpleDateFormat>();
		mapDatePatterns.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"));
		mapDatePatterns.put("yyyy-MM-dd'T'HH:mm:ss",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		mapDatePatterns.put("yyyy-MM-dd HH:mm:ss",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapDatePatterns.put("yyyy-MM-dd'T'HH:mm:ss'Z'",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		mapDatePatterns.put("MMyyyy", new SimpleDateFormat("MMyyyy"));
		mapDatePatterns.put("MM.yyyy", new SimpleDateFormat("MM.yyyy"));
		mapDatePatterns.put("MM-yyyy", new SimpleDateFormat("MM-yyyy"));

		// for 2017-09-19T00:00:00
		mapDatePatterns.put("yyyy-MM-ddTHH:mm:ss",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")); 
		
		mapDatePatterns.put("yyyy-MM-ddTHH:mm:ss'Z'",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

		threadLocal.set(mapDatePatterns);
	}

}
