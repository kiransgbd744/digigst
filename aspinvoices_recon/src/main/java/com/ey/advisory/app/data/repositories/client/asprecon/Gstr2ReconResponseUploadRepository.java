/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2ReconResponseUploadEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconResponseUploadRepository")
@Transactional(value = "clientTransactionManager", propagation = 
							Propagation.REQUIRED)

public interface Gstr2ReconResponseUploadRepository
		extends JpaRepository<Gstr2ReconResponseUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconResponseUploadEntity> {

	@Query("Select MAX(id) from Gstr2ReconResponseUploadEntity WHERE "
			+ " batchID = :batchID AND iDPR = :iDPR AND iD2A = :iD2A")
	public Long findStgId(@Param("iDPR") String iDPR,
			@Param("iD2A") String iD2A, @Param("batchID") String batchID);

}
