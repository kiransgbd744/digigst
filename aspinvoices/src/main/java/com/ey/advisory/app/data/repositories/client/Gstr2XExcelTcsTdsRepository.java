package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("Gstr2XExcelTcsTdsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2XExcelTcsTdsRepository
		extends JpaRepository<Gstr2XExcelTcsTdsEntity, Long> {
	
}
