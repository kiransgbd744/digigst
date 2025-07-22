/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.days.revarsal180.Reversal180DaysPSDEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Reversal180DaysPSDRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Reversal180DaysPSDRepository
		extends JpaRepository<Reversal180DaysPSDEntity, Long>,
		JpaSpecificationExecutor<Reversal180DaysPSDEntity> {

	@Modifying
	@Query("Update Reversal180DaysPSDEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdTrue(Long fileId);

	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdTrueAndIsActiveTrue(
			Long fileId);

	public List<Reversal180DaysPSDEntity> findByIsPsdTrueAndIsActiveTrue();

	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdFalseAndIsActiveTrue(
			Long fileId);
	
	@Modifying
	@Query("Update Reversal180DaysPSDEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	public void updateIsDelete(@Param("docKeyList") List<String> docKeyList);
	
	@Query("SELECT entity.docKey from Reversal180DaysPSDEntity entity "
			+ "where  entity.actionType <> 'CAN' "
			+ "and entity.isActive=true and entity.docKey IN (:docKey)")
	List<String> getActiveCanDocKeys(@Param("docKey") List<String> docKey);
	
	public List<Reversal180DaysPSDEntity> findByPayloadIdAndIsPsdFalse(String payloadId);
}
