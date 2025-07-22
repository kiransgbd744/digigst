package com.ey.advisory.app.services.gstr8;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BSummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNSummaryEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ASummaryDetailsRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr8aRepositoryHandler")
public class Gstr8aRepositoryHandler {

	@Autowired
	private Gstr8aProcessUtil gstr8aProcessUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	Gstr8ASummaryDetailsRepository gstr8ASummaryDetailsRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;
	
	@Autowired
	Gstr8AB2BSummaryDetailsRepository b2bRepo;
	
	@Autowired
	Gstr8AB2BASummaryDetailsRepository b2baRepo;
	
	@Autowired
	Gstr8ACDNSummaryDetailsRepository cdnRepo;
	
	@Autowired
	Gstr8ACDNASummaryDetailsRepository cdnaRepo;
	
	public Pair<Long, Integer> processUserB2BWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId, String fy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<Gstr8AGetB2BSummaryEntity> b2bList = gstr8aProcessUtil
				.convertB2bWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId, fy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					b2bList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		b2bRepo.softDelete(cgstin, fy);
		stopWatch.stop();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BWorkSheetData ->Records ready for insert/update list size [{}]",
					b2bList.size());
		}
		stopWatch.start("Insert/update B2b data");
		b2bRepo.saveAll(b2bList);
		stopWatch.stop();
		boolean isTokenResp = false;
		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
				isTokenResp);

		gstinGetStatusRepo.updateGetGstnStatus(true, APIConstants.SUCCESS,
				LocalDateTime.now(), null, cgstin, taxPeriod,
				APIConstants.GSTR8A, APIConstants.GSTR8A);
		return new Pair<>(0L,
				b2bList.size());
	}

	public Pair<Long, Integer> processUserB2BAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId, String fy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<Gstr8AGetB2BASummaryEntity> b2baList = gstr8aProcessUtil
				.convertB2baWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId, fy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}]",
					b2baList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		b2baRepo.softDelete(cgstin, fy);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BAWorkSheetData ->Records ready for insert/update list size [{}]",
					b2baList.size());
		}
		stopWatch.start("Insert/update B2ba data");
		b2baRepo.saveAll(b2baList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		boolean isTokenResp = false;
		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
				isTokenResp);

		gstinGetStatusRepo.updateGetGstnStatus(true, APIConstants.SUCCESS,
				LocalDateTime.now(), null, cgstin, taxPeriod,
				APIConstants.GSTR8A, APIConstants.GSTR8A);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>( 0L,
				b2baList.size());
	}
	
	public Pair<Long, Integer> processUserCDNRAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId, String fy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<Gstr8AGetCDNASummaryEntity> cdnraList = gstr8aProcessUtil
				.convertCdnraWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId, fy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}]",
					cdnraList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		cdnaRepo.softDelete(cgstin, fy);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"processCNDRAWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnraList.size());
		}
		stopWatch.start("Insert/update cdnra data");
		cdnaRepo.saveAll(cdnraList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		boolean isTokenResp = false;
		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
				isTokenResp);

		gstinGetStatusRepo.updateGetGstnStatus(true, APIConstants.SUCCESS,
				LocalDateTime.now(), null, cgstin, taxPeriod,
				APIConstants.GSTR8A, APIConstants.GSTR8A);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(0L,
				cdnraList.size());
	}

	public Pair<Long, Integer> processUserCDNRWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId, String fy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<Gstr8AGetCDNSummaryEntity> cdnList = gstr8aProcessUtil
				.convertCdnrWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId, fy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}]",
					cdnList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		cdnRepo.softDelete(cgstin, fy);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCNDRWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnList.size());
		}
		stopWatch.start("Insert/update cdnr data");
		cdnRepo.saveAll(cdnList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		boolean isTokenResp = false;
		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
				isTokenResp);

		gstinGetStatusRepo.updateGetGstnStatus(true, APIConstants.SUCCESS,
				LocalDateTime.now(), null, cgstin, taxPeriod,
				APIConstants.GSTR8A, APIConstants.GSTR8A);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(0L,
				cdnList.size());
	}

	public static Map<String, String> createIrnMap(List<String> listIrn) {
        Map<String, String> irnMap = new HashMap<>();

        for (String irnStatus : listIrn) {
            String[] parts = irnStatus.split("-");
            if (parts.length == 2) {
                String irn = parts[0];
                String irnSts = parts[1];
                irnMap.put(irn, irnSts);
            }
        }

        return irnMap;
    }

}
