package com.ey.advisory.app.services.proceedtofile;

import org.javatuples.Quartet;

import com.ey.advisory.app.docs.dto.ProceedFileDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr6ProceedToFileService {

	public String findProceedToFileStatus(String reqObject, ProceedFileDto dto);

	public Quartet<String, String, String, String> getLatestStatusAndTime(String gstin,
			String retPeriod, String returnType);

}
