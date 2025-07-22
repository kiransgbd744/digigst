package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("Gstr9InOutwardExcelRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9InOutwardExcelRepository
		extends JpaRepository<Gstr9OutwardInwardAsEnteredEntity, Long> {

}
