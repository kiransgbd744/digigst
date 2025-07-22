package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.TestEwbUploadEntity;

/**
 * @author vishal.Verma
 *
 */
@Repository("TestEwbUploadRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface TestEwbUploadRepository
		extends JpaRepository<TestEwbUploadEntity, Long>,
		JpaSpecificationExecutor<TestEwbUploadEntity> {

	
}
