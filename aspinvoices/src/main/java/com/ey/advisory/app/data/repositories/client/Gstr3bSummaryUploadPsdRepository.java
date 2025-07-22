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

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr3bSummaryUploadPsdRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bSummaryUploadPsdRepository
		extends JpaRepository<Gstr3BGstinAspUserInputEntity, Long>,
		JpaSpecificationExecutor<Gstr3BGstinAspUserInputEntity> {

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);
//
//	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdTrue(Long fileId);
//
//	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdTrueAndIsActiveTrue(
//			Long fileId);
//
//	public List<Reversal180DaysPSDEntity> findByIsPsdTrueAndIsActiveTrue();
//
//	public List<Reversal180DaysPSDEntity> findByFileIdAndIsPsdFalseAndIsActiveTrue(
//			Long fileId);
}
