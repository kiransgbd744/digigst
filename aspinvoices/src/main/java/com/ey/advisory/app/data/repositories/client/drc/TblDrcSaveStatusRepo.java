
package com.ey.advisory.app.data.repositories.client.drc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc.TblDrcSaveStatus;

/**
 * 
 * @author Harsh
 *
 */

@Repository("TblDrcSaveStatusRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrcSaveStatusRepo
		extends JpaRepository<TblDrcSaveStatus, Long>,
		JpaSpecificationExecutor<TblDrcSaveStatus> {

	public List<TblDrcSaveStatus> findByGstinInAndTaxPeriodAndIsActiveTrue(List<String> gstins,
			String taxperiod);
	
	public TblDrcSaveStatus findByGstinAndTaxPeriodAndIsActiveTrue(String gstin,
			String taxperiod);
	
	@Query("SELECT e FROM TblDrcSaveStatus e WHERE gstin = :gstin AND taxPeriod= :taxPeriod AND createdOn= :createdOn")
	public TblDrcSaveStatus findByGstinAndTaxPeriodAndCreatedOn(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("createdOn") LocalDateTime createdOn);
	

	@Query("select count(e) from TblDrcSaveStatus e where"
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status = :status")
	public Long findByGstinAndTaxPeriodAndStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);
	
	@Modifying
	@Query("update TblDrcSaveStatus set status =:status  where  id =:id ")
	void updateStatus(@Param("status") String status, @Param("id") Long id);
	
	@Modifying
	@Query("update TblDrcSaveStatus set status =:status, "
			+ "saveResponsePayload =:saveResponsePayload  where  id =:id ")
	void updateStatus(@Param("status") String status, @Param("id") Long id,
			@Param("saveResponsePayload") String saveResponsePayload);

	@Modifying
	@Query("Update TblDrcSaveStatus SET isActive = false  WHERE "
			+ "taxPeriod =:taxPeriod AND gstin =:gstin")
		void updateActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);

}
