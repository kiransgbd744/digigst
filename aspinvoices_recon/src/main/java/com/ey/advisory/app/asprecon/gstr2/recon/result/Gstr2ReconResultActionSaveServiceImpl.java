package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2ReconResultRespPsdEntity;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2ReconResponseUploadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2ReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Link2APRrepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconResponseUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconResultActionSaveServiceImpl")
public class Gstr2ReconResultActionSaveServiceImpl
		implements Gstr2ReconResultActionSaveService {

	@Autowired
	@Qualifier("Gstr2Link2APRrepository")
	private Gstr2Link2APRrepository repo;
	
	@Autowired
	@Qualifier("Gstr2ReconResultPsdRespDaoImpl")
	private Gstr2ReconResultPsdRespDaoImpl psdDao;
	
	@Autowired
	@Qualifier("Gstr2ReconResultRespPsdRepository")
	private Gstr2ReconResultRespPsdRepository psdRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconResultSTGDaoImpl")
	private Gstr2ReconResultSTGDaoImpl stgRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconResponseUploadRepository")
	private Gstr2ReconResponseUploadRepository stagingRepo;
	
	
	@Override
	public int saveData(Gstr2ReconResultActionListDto dtoList) {
		int count = 0;
		String gstr3BTaxPeriod = null;
		String forceMatch = null;
		String preReportType = null;
		Integer preReportTypeId = null;
	
		try {
			 
			for (Gstr2ReconResultActionDto list : dtoList.getReconIds()) {

				Long a2Id = list.getA2Id();
				Long prId = list.getPrId();
				Long reconLinkId = list.getReconLinkId();
				String userAction = list.getActionTaken();
				List<Gstr2ReconResultRespPsdEntity> psdData = null;
				List<Gstr2ReconResponseUploadEntity> stgData = null;
				
				String currReportType = repo
						.findCurrentReportType(reconLinkId);
				preReportType = repo
						.findPreviousReportType(reconLinkId);
				
				if (userAction.equalsIgnoreCase(APIConstants.RESPONSE_3B)) {
					gstr3BTaxPeriod = repo.findPrTaxPeriod(reconLinkId);
					forceMatch = APIConstants.LOCK;
					
					LOGGER.debug(
							"Inside Gstr2ReconResultActionSaveServiceImpl"
								+ ".saveData() method params %s action, %d a2Id, "
									+ "%d prId, %d reconLinkId,  {} ",
							userAction, a2Id, prId, reconLinkId);
					count = count
							+ repo.updateCount(reconLinkId, gstr3BTaxPeriod,
									forceMatch, userAction, currReportType, 
									APIConstants.forceMatch_GSTR3B);
					
					stgData = stgRepo.getStgData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,userAction);
					
					// inserting data in TBL_STG_RECON_RESPONSE
					stagingRepo.saveAll(stgData);
					
					//getting batchId from Staging table
					String batchID = stgData.get(0).getBatchID();
					
					//getting Staging TableID
					Long stgID = stagingRepo.findStgId(prId.toString(), 
							a2Id.toString(), batchID.toString());
					
					psdData = psdDao.getPsdData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,stgID);
					

				} else if (userAction
						.equalsIgnoreCase(APIConstants.FORCE_MATCH)) {
					forceMatch = APIConstants.LOCK;
					userAction = APIConstants.FORCE_MATCH;
					
					LOGGER.debug(
							"Inside Gstr2ReconResultActionSaveServiceImpl"
									+ ".saveData() method params %s action, "
									+ "%d a2Id, %d prId, %d reconLinkId,  {} ",
							userAction, a2Id, prId, reconLinkId);
					count = count
							+ repo.updateCount(reconLinkId, gstr3BTaxPeriod,
									forceMatch, userAction, currReportType, 
									APIConstants.forceMatch_GSTR3B);
					
					stgData = stgRepo.getStgData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,userAction);
					
					// inserting data in TBL_STG_RECON_RESPONSE
					stagingRepo.saveAll(stgData);
					
					//getting batchId from Staging table
					String batchID = stgData.get(0).getBatchID();
					
					//getting Staging TableID
					Long stgID = stagingRepo.findStgId(prId.toString(), 
							a2Id.toString(), batchID.toString());
					
					psdData = psdDao.getPsdData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,stgID);
					
				} else if(userAction
						.equalsIgnoreCase(APIConstants.UnLock)) {
					userAction = APIConstants.UnLock;
					
					LOGGER.debug(
							"Inside Gstr2ReconResultActionSaveServiceImpl"
								+ ".saveData() method params %s action, "
								+ "%d a2Id, %d prId, %d reconLinkId,  {} ",
							userAction, a2Id, prId, reconLinkId);
					
					Map<String, Integer> map = getReportTypeIdMap();
					
					if(map.containsKey(preReportType)){
						preReportTypeId = map.get(preReportType);
					}
					
					count = count + repo.updateNoActionCount(reconLinkId,
							userAction, preReportType, preReportTypeId);
					
					stgData = stgRepo.getStgData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,userAction);
					
					// inserting data in TBL_STG_RECON_RESPONSE
					stagingRepo.saveAll(stgData);
					
					//getting batchId from Staging table
					String batchID = stgData.get(0).getBatchID();
					
					//getting Staging TableID
					Long stgID = stagingRepo.findStgId(prId.toString(), 
							a2Id.toString(), batchID.toString());
					
					psdData = psdDao.getPsdData(prId, a2Id, reconLinkId,
							forceMatch, gstr3BTaxPeriod,stgID);
					 preReportType = repo.findPreviousReportType(reconLinkId);
					
				}
				
				// updating TBL_RECON_RESP_PSD
				if (prId != null) {
					psdRepo.updateTimeStampIdPR(prId, LocalDateTime.now());
				} if(a2Id != null) {
					psdRepo.updateTimeStampId2A(a2Id, LocalDateTime.now());
				}
				// inserting data in TBL_RECON_RESP_PSD
				psdRepo.saveAll(psdData);
			}

		} catch (Exception ex) {
			String msg = String.format(
					"exception occured while updating user response", ex);
			LOGGER.error(msg);
			throw new AppException(ex);
		}

		return count;
	}
	
	private Map<String, Integer> getReportTypeIdMap() {

		Map<String, Integer> map = new HashMap<>();
		map.put("Exact Match", 1);
		map.put("Match With Tolerance", 2);
		map.put("Value Mismatch", 3);
		map.put("POS Mismatch", 4);
		map.put("Doc Date Mismatch", 5);
		map.put("DOC Typ Mismatch", 6);
		map.put("Doc No Mismatch I", 7);
		map.put("Multi-Mismatch", 8);
		map.put("Potential-I", 9);
		map.put("Doc No Mismatch II", 10);
		map.put("Potential-II", 11);
		map.put("Logical", 12);
		map.put("Addition in 2A", 13);
		map.put("Addition in PR", 14);
		map.put("Dropped 2A_6A Records Report", 15);
		map.put("Dropped PR Records Report", 16);
		map.put("ForceMatch/GSTR3B", 17);

		return map;

	}

}
