package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailStatusEntity;


@Repository("VendorEmailStatusRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorEmailStatusRepository
		extends JpaRepository<VendorEmailStatusEntity, Long>,
		JpaSpecificationExecutor<VendorEmailStatusEntity> {

	/*@Query("Select VendorEmailStatusEntity from VendorEmailStatusEntity VendorEmailStatusEntity where "
			+ " requestId =:requestId"
			+ " entityId =:entityId"
			+ " reconType =:reconType"
			+ " status =:status")
	public VendorEmailStatusEntity findByAll(
			@Param("requestId") Long requestId,
			@Param("entityId") Long entityId,
			@Param("reconType") String reconType,
			@Param("status") String status);
*/
	
	@Modifying
	@Query("UPDATE VendorEmailStatusEntity SET status =:status "
			+ " WHERE id =:id ")
	public void updateStatus(@Param("id") Long id,
			@Param("status") String status);
	
	@Modifying
	@Query("UPDATE VendorEmailStatusEntity SET  isDelete = true "
			+ " where isDelete = false and entityId =:entityId and requestId =:requestId")
	public void softlyDeleteRequestIds( @Param("requestId") Long requestId,
			                            @Param("entityId") Long entityId);
	
	@Query("select status from VendorEmailStatusEntity where "
			+ " entityId =:entityId and requestId =:requestId and isDelete = false")
	public String getLatestRequestId
	( @Param("requestId") Long requestId,
	  @Param("entityId") Long entityId);
	
	
	/*@Modifying
	@Query("UPDATE VendorEmailStatusEntity SET status =:status, isdelete = false, "
			+ " WHERE id =:id ")
	public void updateSuccessStatus(@Param("id") Long id,
			@Param("status") String status);
	*/
	
}

