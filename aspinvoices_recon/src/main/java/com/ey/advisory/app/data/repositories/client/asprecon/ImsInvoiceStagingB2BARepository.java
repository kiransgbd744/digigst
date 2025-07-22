package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BAEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsInvoiceStagingB2BARepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsInvoiceStagingB2BARepository
		extends JpaRepository<ImsInvoiceStagingB2BAEntity, Long>,
		JpaSpecificationExecutor<ImsInvoiceStagingB2BAEntity> {

	
}
