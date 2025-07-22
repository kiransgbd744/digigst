/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */

@Slf4j
@Service("ReconResultReportTypeServiceImpl")
public class ReconResultReportTypeServiceImpl
		implements ReconResultReportTypeService {
	
	@Autowired
	@Qualifier("ReconResultReportTypeDaoImpl")
	ReconResultReportTypeDao reconResultReportDao;

	@Override
	public List<String> getReconResultReportNames(int taxPeriod,
			List<String> gstins) {
		List<String> reportType = reconResultReportDao
				.findReportTypeForGstins(taxPeriod, gstins);
		
		if (CollectionUtils.isEmpty(reportType)) {
			String msg = String.format("No Recon Report Type Intiated for "
					+ "following Gstins");
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return reportType;
	}

}
