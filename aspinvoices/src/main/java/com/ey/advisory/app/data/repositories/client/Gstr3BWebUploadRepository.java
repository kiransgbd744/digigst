package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.gstr3b.Gstr3BWebUploadEntity;

/**
 * 
 * @author vishal.verma
 *
 */

@Repository("Gstr3BWebUploadRepository")
public interface Gstr3BWebUploadRepository
		extends JpaRepository<Gstr3BWebUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr3BWebUploadEntity> {

}
