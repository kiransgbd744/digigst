package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryDto;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryMasterDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr2BPRReconSummaryServiceImpl")
public class Gstr2BPRReconSummaryServiceImpl
		implements Gstr2BPRReconSummaryService {

	@Autowired
	@Qualifier("Gstr2BPRReconSummaryDaoImpl")
	private Gstr2BPRReconSummaryDao dao;

	@Override
	public Gstr2ReconSummaryMasterDto getReconSummary(Long configId,
			List<String> gstin, String toTaxPeriod, String fromTaxPeriod,
			String reconType, String toTaxPeriod_A2, String fromTaxPeriod_A2,
			String criteria) {

		Gstr2ReconSummaryMasterDto resp = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Inside Gstr2ReconSummaryServiceImpl.getReconSummary()"
							+ "method{} configId %d : ",
					configId);
		}

		try {

			List<Gstr2ReconSummaryDto> reconsummaryDto = dao.findReconSummary(
					configId, gstin, GenUtil.convertTaxPeriodToInt(toTaxPeriod),
					GenUtil.convertTaxPeriodToInt(fromTaxPeriod), reconType,
					GenUtil.convertTaxPeriodToInt(toTaxPeriod_A2),
					GenUtil.convertTaxPeriodToInt(fromTaxPeriod_A2), criteria);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"findReconSummary() method{} configId "
								+ " %d, reconsummaryDto %s : ",
						configId, reconsummaryDto);
				LOGGER.debug(msg);
			}

			resp = new Gstr2ReconSummaryMasterDto(reconsummaryDto, null, null,
					null, null, null);

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured : findReconSummary()  configId %d ",
					configId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Before return Gstr2BPRReconSummaryServiceImpl"
					+ ".getReconSummary() method{}");
		}

		return resp;

	}

}
