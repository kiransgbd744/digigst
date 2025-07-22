/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.services.anx1.Anx1ProcessedRecordsFetchService;
import com.ey.advisory.app.data.views.client.Anx1ProcessedScreenDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsRespDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1ProcessedScreenDownloadDaoImpl")
public class Anx1ProcessedScreenDownloadDaoImpl
		implements Anx1ProcessedScreenDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1ProcessedRecordsFetchService")
	Anx1ProcessedRecordsFetchService anx1ProcessedRecordsFetchService;
	

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;


	@Override
	public List<Anx1ProcessedScreenDto> getProcessedScreenDownload(
			Anx1ProcessedRecordsReqDto anx1ProcessedSummery) {

		List<Anx1ProcessedRecordsFinalRespDto> respDtos = anx1ProcessedRecordsFetchService
				.find(anx1ProcessedSummery, "ANX1");

		List<Anx1ProcessedScreenDto> processedsumDtoList = new ArrayList<>();

		for (Anx1ProcessedRecordsFinalRespDto resdto : respDtos) {

			Anx1ProcessedScreenDto res = new Anx1ProcessedScreenDto();
				String gstin = res.getGSTIN();	
			/*	String stateCode = gstin.substring(0, 2);*/
				/*String stateName = statecodeRepository
						.findStateNameByCode(stateCode);*/
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(gstin);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						res.setRegistrationType("");
					} else {
						res.setRegistrationType(regTypeName.toUpperCase());
					}
				} else {
					res.setRegistrationType("");
				}

				if (resdto.getInType()
						.equalsIgnoreCase(DownloadReportsConstant.INWARD)) {
				res.setSaveStatus(resdto.getStatus());
				res.setDateTime(resdto.getTimeStamp());
				res.setTransactionType(resdto.getInType());
				res.setCount(resdto.getInCount());
				res.setTaxableValue(resdto.getInSupplies());
				res.setIgst(resdto.getInIgst());
				res.setCgst(resdto.getInCgst());
				res.setSgst(resdto.getInSgst());
				res.setCess(resdto.getInCess());
				processedsumDtoList.add(res);
			}
			if (resdto.getOutType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				
				/*res.setGSTIN(resdto.getGstin());
				res.setStateCode(resdto.getState());
				res.setStateName(resdto.getState());
				res.setRegistrationType(resdto.getRegType());*/
				res.setSaveStatus(resdto.getStatus());
				res.setDateTime(resdto.getTimeStamp());
				res.setTransactionType(resdto.getOutType());
				res.setCount(resdto.getOutCount());
				res.setTaxableValue(resdto.getOutSupplies());
				res.setIgst(resdto.getOutIgst());
				res.setCgst(resdto.getOutCgst());
				res.setSgst(resdto.getOutSgst());
				res.setCess(resdto.getOutCess());
				processedsumDtoList.add(res);
			}
		}
		return processedsumDtoList;
		}

		

	}

