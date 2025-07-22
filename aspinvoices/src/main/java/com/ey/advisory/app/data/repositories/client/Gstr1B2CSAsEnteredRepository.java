package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("Gstr1B2CSAsEnteredRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1B2CSAsEnteredRepository
		extends JpaRepository<Gstr1AsEnteredB2csEntity, Long>,
		JpaSpecificationExecutor<Gstr1AsEnteredB2csEntity> {
}
