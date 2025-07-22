package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author vishal.verma
 *
 */
@Service("Gstr2InitiateMatchingReconAPCheckServiceImpl")
public class Gstr2InitiateMatchingReconAPCheckServiceImpl
		implements Gstr2InitiateMatchingReconAPCheckService {

	@Autowired
	@Qualifier("Gstr2InitiateMatchingReconAPCheckDaoImpl")
	private Gstr2InitiateMatchingReconAPCheckDao initiateReconcileDao;

	@Override
	public String initiatReconcile(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A,String toTaxPeriodPR, 
			String fromTaxPeriodPR, String toDocDate, String fromDocDate, 
			List<String> addlReportsList, String reconType, 
			Boolean mandatoryReports) {

		/**
		 * insert data in parent and child table with status as recon_request
		 * update status as recon_initiated in parent table and trigger stored
		 * proc 1 if success invoke Stored proc 2 and generate excel sheet and
		 * upload in doc repo and update path in parent table
		 **/
		String status = initiateReconcileDao.createReconcileData(gstins,
				entityId, toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
				fromTaxPeriodPR, toDocDate, fromDocDate, addlReportsList,
				 reconType,  mandatoryReports);

		return status;
	}

}
