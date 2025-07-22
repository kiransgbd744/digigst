package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr1AsEnterTxpdRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AsEnterTxpdRepository
		extends JpaRepository<Gstr1AsEnteredTxpdFileUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr1AsEnteredTxpdFileUploadEntity> {

}
