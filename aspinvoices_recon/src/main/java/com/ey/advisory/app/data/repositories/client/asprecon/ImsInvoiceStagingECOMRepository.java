package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsInvoiceStagingECOMEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsInvoiceStagingECOMRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsInvoiceStagingECOMRepository
		extends JpaRepository<ImsInvoiceStagingECOMEntity, Long>,
		JpaSpecificationExecutor<ImsInvoiceStagingECOMEntity> {

	
}
