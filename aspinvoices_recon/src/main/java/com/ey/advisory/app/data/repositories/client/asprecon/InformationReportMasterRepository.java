/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.InformationReportMasterEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("InformationReportMasterRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface InformationReportMasterRepository
		extends JpaRepository<InformationReportMasterEntity, Integer>,
		JpaSpecificationExecutor<InformationReportMasterEntity> {

	@Query("SELECT b.infoReportId from InformationReportMasterEntity b WHERE "
			+ " b.infoReportName in (:infoReportName)")
	public List<Integer> findByName(
			@Param("infoReportName") List<String> infoReportName);
}
