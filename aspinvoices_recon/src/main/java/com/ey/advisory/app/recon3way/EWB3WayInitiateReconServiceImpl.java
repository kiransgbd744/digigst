package com.ey.advisory.app.recon3way;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author vishal.verma
 *
 */
@Service("EWB3WayInitiateReconServiceImpl")
public class EWB3WayInitiateReconServiceImpl
		implements EWB3WayInitiateReconService {

	@Autowired
	@Qualifier("EWB3WayInitiateReconDaoImpl")
	private EWB3WayInitiateReconDao initiateReconcileDao;

	
	@Override
	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, String criteria,
			String gstr1Type, String eInvType, String gewbType, List<String> addReport ) {

		/**
		 * insert data in parent and child table with status as recon_request
		 * update status as recon_initiated in parent table and trigger stored
		 * proc 1 if success invoke Stored proc 2 and generate excel sheet and
		 * upload in doc repo and update path in parent table
		 **/
		
		
		
		String status = initiateReconcileDao.createReconcileData(gstins,
				entityId, fromReturnPeriod, toReturnPeriod, criteria ,
				gstr1Type, eInvType, gewbType, addReport);

		return status;
	}

}
