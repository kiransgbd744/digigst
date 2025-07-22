package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("SalesRegisterInitiateReconServiceImpl")
public class SalesRegisterInitiateReconServiceImpl
		implements SalesRegisterInitiateReconService {

	@Autowired
	@Qualifier("SalesRegisterInitiateReconDaoImpl")
	private SalesRegisterInitiateReconDao initiateReconcileDao;

	
	@Override
	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, String criteria) {

		/**
		 * insert data in parent and child table with status as recon_request
		 * update status as recon_initiated in parent table and trigger stored
		 * proc 1 if success invoke Stored proc 2 and generate excel sheet and
		 * upload in doc repo and update path in parent table
		 **/
		
		
		
		String status = initiateReconcileDao.createReconcileData(gstins,
				entityId, fromReturnPeriod, toReturnPeriod);

		return status;
	}

}
