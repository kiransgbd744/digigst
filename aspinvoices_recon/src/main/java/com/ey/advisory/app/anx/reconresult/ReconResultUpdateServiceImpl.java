package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ReconResultUpdateServiceImpl")
public class ReconResultUpdateServiceImpl  implements ReconResultUpdateService {

	@Autowired
	@Qualifier("ReconResultUpdateDaoImpl")
	ReconResultUpdateDao reconUpdateDao;

	public int updateReconUserActions(ReconResultUpdateReqDto reqDto) {


		List<ReconResultUpdateLevelReqDto> reconReqLevel = reqDto
				.getReportType();

		int resCount = 0;

		for (ReconResultUpdateLevelReqDto reqUpLevel : reconReqLevel) {
			String taxPeriod = reqUpLevel.getTaxPeriod();
			String ReportName = reqUpLevel.getReportName();
			
			List<ReconResultUpdateComDetailsDto> commList = reqUpLevel
					.getCommonDetails();
			for (ReconResultUpdateComDetailsDto cmmList : commList) {
				//String UserAction = cmmList.getUserAction();
				String userAct = cmmList.getUserAction();
				String UserAction =  userAct.replace(" ", "").equals("") ?
						null : cmmList.getUserAction();
				List<ReconResultUpdateInnerDetDto> detailsList = cmmList
						.getDetails();
				List<String> a2KeyList = new ArrayList<String>();
				List<String> prKeyList = new ArrayList<String>();
				List<BigInteger> reconIds = new ArrayList<BigInteger>();
				List<String> gstinsList = new ArrayList<String>();
				
			

				for (ReconResultUpdateInnerDetDto innerDetails : detailsList) {

					if (!(innerDetails.getA2key() == null
							|| innerDetails.getA2key().isEmpty())) {
						a2KeyList.add(innerDetails.getA2key());

					}

					if (!(innerDetails.getPrkey() == null
							|| innerDetails.getPrkey().isEmpty())) {
						prKeyList.add(innerDetails.getPrkey());

					}

					if (innerDetails.getReconLinkId() != null) {
						reconIds.add(innerDetails.getReconLinkId());
					}

					if (!(innerDetails.getGstin() == null
							|| innerDetails.getGstin().isEmpty())) {
						gstinsList.add(innerDetails.getGstin());

					}
	

				}

				resCount = resCount + reconUpdateDao.updateReconUserActionsInDB(
						taxPeriod, ReportName, UserAction, a2KeyList, prKeyList,
						reconIds, gstinsList);

			}

		}
		return resCount;

	}
}
