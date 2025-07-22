package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.ProcessSbmitPorcDaoImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Service("ProcessSubmitProcCallService")
public class ProcessSubmitProcCallService {

	@Autowired
	@Qualifier("ProcessSbmitPorcDaoImpl")
	ProcessSbmitPorcDaoImpl procCallImpl;
	
	public String getInitiateReconProcCall(Annexure1SummaryReqDto req) {

		String msg = null;
		
//		String taxPeriodReq = req.getTaxPeriod();
			String taxPeriodFrom = req.getTaxPeriodFrom();
			String taxPeriodTo = req.getTaxPeriodTo();

			int derivedRetPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			int derivedRetPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);

			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		
			String gstin = null;

			List<String> gstinList = null;
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstin = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}

				}
			}
			
		
		int count = procCallImpl.getProcubmitProcCall(gstinList,derivedRetPeriodFrom,derivedRetPeriodTo);
		
		if (count > 0) {
			msg = "Recon Initiated Successfully";
		} else {
			msg = "Invalid Gstin";
		}
		return msg;
		
	}
	
}
