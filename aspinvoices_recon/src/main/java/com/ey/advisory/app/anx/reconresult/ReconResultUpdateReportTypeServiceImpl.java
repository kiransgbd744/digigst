package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ReconResultUpdateReportTypeServiceImpl")
public class ReconResultUpdateReportTypeServiceImpl
		implements ReconResultUpdateReportTypeService {

	@Autowired
	@Qualifier("ReconResultUpdateReportTypeDaoImpl")
	private ReconResultUpdateReportTypeDao linkA2PR;

	@Override
	public int updateReconReportType(ReconResultUpdateReportTypeReqDto reqDto) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Invoking ReconResultUpdateReportTypeService";
			LOGGER.debug(msg);
		}
		
		int count = 0;
		List<UpdateInnerDto> commList = reqDto.getLinkIdList();

		BigInteger a2ReconLinkId = BigInteger.ZERO;
		BigInteger prReconLinkId = BigInteger.ZERO;

		for (UpdateInnerDto cmmList : commList) {

			if ((cmmList.getA2ReconLinkId()) != null) {
				a2ReconLinkId = cmmList.getA2ReconLinkId();
			}
			if ((cmmList.getPrReconLinkId()) != null) {
				prReconLinkId = cmmList.getPrReconLinkId();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoking updateReconReportType() method %s, %s",
							a2ReconLinkId, prReconLinkId);
					LOGGER.debug(msg);
				}

				 count = linkA2PR.updateReconReportType(a2ReconLinkId,
						prReconLinkId);
				
			}
		}
		return count;

	}
}
