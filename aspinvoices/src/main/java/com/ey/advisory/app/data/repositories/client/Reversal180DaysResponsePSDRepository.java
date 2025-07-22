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

import com.ey.advisory.service.days.revarsal180.Reversal180DaysResponsePSDEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("Reversal180DaysResponsePSDRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Reversal180DaysResponsePSDRepository
		extends JpaRepository<Reversal180DaysResponsePSDEntity, Long>,
		JpaSpecificationExecutor<Reversal180DaysResponsePSDEntity> {

	@Modifying
	@Query("Update Reversal180DaysResponsePSDEntity SET isDelete = true,updatedOn = CURRENT_TIMESTAMP,updatedBy =:updatedBy "
			+ "   WHERE  docKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList,
			@Param("updatedBy") String updatedBy);

	public List<Reversal180DaysResponsePSDEntity> findByIsPsdTrueAndIsDeleteFalse();
}
