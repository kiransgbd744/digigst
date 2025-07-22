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

import com.ey.advisory.service.days.revarsal180.Reversal180DaysStageEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Reversal180DaysStageRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Reversal180DaysStageRepository
		extends JpaRepository<Reversal180DaysStageEntity, Long>,
		JpaSpecificationExecutor<Reversal180DaysStageEntity> {
	
	@Modifying
	@Query("Update Reversal180DaysStageEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
		int updateIsDeleteFlag(
				@Param("docKeyList")List<String> docKeyList);
	
	public List<Reversal180DaysStageEntity> findByFileId(Long fileId);

}
