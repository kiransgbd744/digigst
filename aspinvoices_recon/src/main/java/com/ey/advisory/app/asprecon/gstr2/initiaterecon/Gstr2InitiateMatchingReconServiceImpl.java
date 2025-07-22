package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author vishal.verma
 *
 */
@Service("Gstr2InitiateMatchingReconServiceImpl")
public class Gstr2InitiateMatchingReconServiceImpl
		implements Gstr2InitiateMatchingReconService {

	@Autowired
	@Qualifier("Gstr2InitiateMatchingReconDaoImpl")
	private Gstr2InitiateMatchingReconDao initiateReconcileDao;

	@Override
	public String initiatReconcile(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A,String toTaxPeriodPR, 
			String fromTaxPeriodPR, String toDocDate, String fromDocDate, 
			List<String> addlReportsList, String reconType, Boolean mandatoryReports) {

		/**
		 * insert data in parent and child table with status as recon_request
		 * update status as recon_initiated in parent table and trigger stored
		 * proc 1 if success invoke Stored proc 2 and generate excel sheet and
		 * upload in doc repo and update path in parent table
		 **/
		String status = initiateReconcileDao.createReconcileData(gstins,
				entityId, toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
				fromTaxPeriodPR, toDocDate, fromDocDate, addlReportsList,
				 reconType, mandatoryReports);

		return status;
	}

}
