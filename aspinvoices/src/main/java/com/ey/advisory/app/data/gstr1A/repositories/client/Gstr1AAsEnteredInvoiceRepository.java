package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1AAsEnteredInvoiceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AAsEnteredInvoiceRepository
		extends JpaRepository<Gstr1AAsEnteredInvEntity, Long>,
		JpaSpecificationExecutor<Gstr1AAsEnteredInvEntity> {

}
