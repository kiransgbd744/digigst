package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arun.KA
 *
 */

@Component("InitiateReconcileServiceImpl")
@Transactional(value = "clientTransactionManager",
	propagation = Propagation.REQUIRED)
public class InitiateReconcileServiceImpl implements InitiateReconcileService {

	@Autowired
	@Qualifier("InitiateReconcileDaoImpl")
	private InitiateReconcileDao initiateReconcileDao;

	@Override
	public String initiatReconcile(List<String> gstins,
			List<String> infoReports, String taxPeriod, Long entityId) {


		/** insert data in parent and child table with status as recon_request
		 *update status as recon_initiated in parent table and trigger stored
		 *proc 1 if success invoke Stored proc 2 and generate excel sheet and
		 *upload in doc repo and update path in parent table
		**/
		String status = initiateReconcileDao.createReconcileData(gstins,
				infoReports, taxPeriod, entityId);

		return status;
	}

}
