package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Ravindra V S
 *
 */
@Service("EwbVsItc04InitiateReconServiceImpl")
public class EwbVsItc04InitiateReconServiceImpl
		implements EwbVsItc04InitiateReconService {

	@Autowired
	@Qualifier("EwbVsItc04InitiateReconDaoImpl")
	private EwbVsItc04InitiateReconDao initiateReconcileDao;

	@Override
	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromTaxPeriod, String toTaxPeriod, String fy,
			String criteria, List<String> addReport) {

		String status = initiateReconcileDao.createReconcileData(gstins,
				entityId, fromTaxPeriod, toTaxPeriod, fy, criteria, addReport);

		return status;
	}

}
