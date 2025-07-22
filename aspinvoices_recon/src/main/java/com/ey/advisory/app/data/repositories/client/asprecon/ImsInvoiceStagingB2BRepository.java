package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsInvoiceStagingB2BRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsInvoiceStagingB2BRepository
		extends JpaRepository<ImsInvoiceStagingB2BEntity, Long>,
		JpaSpecificationExecutor<ImsInvoiceStagingB2BEntity> {

	
}
