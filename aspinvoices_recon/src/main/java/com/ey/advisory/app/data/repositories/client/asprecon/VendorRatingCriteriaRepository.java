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

import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;

/**
 * 
 * @author Jithendra.B
 *
 */
@Repository("VendorRatingCriteriaRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface VendorRatingCriteriaRepository
		extends JpaRepository<VendorRatingCriteriaEntity, Long>,
		JpaSpecificationExecutor<VendorRatingCriteriaEntity> {

	@Modifying
	@Query("UPDATE VendorRatingCriteriaEntity SET isDelete = true , "
			+ " updatedOn = CURRENT_TIMESTAMP , updatedBy =:updatedBy  "
			+ " WHERE isDelete = false and entityId=:entityId and source=:source")
	int softDeleteActiveRatings(@Param("entityId") Long entityId,
			@Param("updatedBy") String updatedBy,
			@Param("source") String source);

	public List<VendorRatingCriteriaEntity> findByEntityIdAndIsDeleteFalseAndSource(
			Long entityId, String source);

	List<VendorRatingCriteriaEntity> findByEntityIdAndReturnTypeInAndIsDeleteFalseAndSource(
			Long entityId, List<String> returnType, String source);

	List<VendorRatingCriteriaEntity> findByEntityIdAndReturnTypeAndIsDeleteFalseAndSource(
			Long entityId, String returnType, String source);
}
