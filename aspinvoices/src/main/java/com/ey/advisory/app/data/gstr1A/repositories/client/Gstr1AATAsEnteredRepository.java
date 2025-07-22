package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1AATAsEnteredRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AATAsEnteredRepository
		extends JpaRepository<Gstr1AAsEnteredAREntity, Long>,
		JpaSpecificationExecutor<Gstr1AAsEnteredAREntity> {

}
