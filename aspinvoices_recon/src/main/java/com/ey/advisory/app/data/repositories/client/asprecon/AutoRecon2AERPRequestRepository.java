package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2AERPRequestEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("AutoRecon2AERPRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface AutoRecon2AERPRequestRepository
		extends JpaRepository<AutoRecon2AERPRequestEntity, Long>,
		JpaSpecificationExecutor<AutoRecon2AERPRequestEntity> {

	public List<AutoRecon2AERPRequestEntity> findByReconConfigIDAndGstinStartsWithAndStatusIn(
			Long configId, String gstin, List<String> status);

	@Query("SELECT e from AutoRecon2AERPRequestEntity e "
			+ "WHERE e.reconConfigID =:configId AND (e.gstin =:gstin OR "
			+ "(SUBSTRING(e.gstin,3,10) =:gstin and e.gstin like '%Addition%')) "
			+ "AND status In (:status)")
	public List<AutoRecon2AERPRequestEntity> findRequestId(
			@Param("configId") Long configId, @Param("gstin") String gstin,
			@Param("status") List<String> status);

	@Modifying
	@Query("update AutoRecon2AERPRequestEntity set status =:status, updatedOn = CURRENT_TIMESTAMP where "
			+ "gstin =:gstin and reconConfigID =:configId")
	public void updateStatus(@Param("configId") Long configId,
			@Param("gstin") String gstin, @Param("status") String status);


	public List<AutoRecon2AERPRequestEntity> findByGstinAndReconConfigIDLessThanAndStatusIn(
			String gstin, Long configId, List<String> statusList);

	public List<AutoRecon2AERPRequestEntity> findByStatusIn(List<String> status);

}
