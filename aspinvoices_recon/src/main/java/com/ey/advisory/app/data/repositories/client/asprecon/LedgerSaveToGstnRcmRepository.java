package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.LedgerSaveToGstnRcmEntity;

@Repository("LedgerSaveToGstnRcmRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface LedgerSaveToGstnRcmRepository
		extends JpaRepository<LedgerSaveToGstnRcmEntity, Long> {

	LedgerSaveToGstnRcmEntity findByGstinAndLedgerTypeAndIsActiveTrue(
			String gstin, String ledgerType);

	@Modifying
	@Query("UPDATE LedgerSaveToGstnRcmEntity e SET e.isActive = false WHERE e.gstin IN :gstins AND e.ledgerType = :ledgerType AND e.isActive = true")
	void softDeleteByGstinListAndLedgerType(
			@Param("gstins") List<String> gstins,
			@Param("ledgerType") String ledgerType);

	@Modifying
	@Query("UPDATE LedgerSaveToGstnRcmEntity e SET e.status = :status WHERE e.gstin = :gstin AND e.isActive = true")
	void updateStatusByGstin(@Param("status") String status,
			@Param("gstin") String gstin);
	
	@Modifying
	@Query("UPDATE LedgerSaveToGstnRcmEntity e SET e.status = :status, e.completedOn = :completedOn, e.ackNum = :ackNum, e.errmsg = :errmsg WHERE e.id = :id AND e.isActive = true")
	void updateStatusById(
	    @Param("status") String status,
	    @Param("completedOn") LocalDateTime completedOn,
	    @Param("ackNum") String ackNum,
	    @Param("errmsg") String errmsg,
	    @Param("id") Long id
	);
	
	  @Query("SELECT e FROM LedgerSaveToGstnRcmEntity e WHERE e.gstin IN :gstinList AND e.isActive = true")
	    List<LedgerSaveToGstnRcmEntity> findActiveByGstinList(@Param("gstinList") List<String> gstinList);


	List<LedgerSaveToGstnRcmEntity> findByGstinInAndIsActiveTrue(
			List<String> gstinList);
	
	@Query("SELECT e FROM LedgerSaveToGstnRcmEntity e WHERE e.gstin = :gstin AND e.isActive = true")
	LedgerSaveToGstnRcmEntity findByGstinAndIsActiveTrue(@Param("gstin") String gstin);

	/*@Query("SELECT e FROM LedgerSaveToGstnRcmEntity e WHERE e.gstin = :gstin AND e.isActive = true AND e.ledgerType = :ledgerType")
	LedgerSaveToGstnRcmEntity findByGstinAndIsActiveTrueAndLedgerType(@Param("gstin") String gstin, @Param("ledgerType") String ledgerType);
*/
	@Query("SELECT e FROM LedgerSaveToGstnRcmEntity e WHERE e.gstin IN :gstins AND e.isActive = true AND e.ledgerType = :ledgerType")
	List<LedgerSaveToGstnRcmEntity> findByGstinListAndIsActiveTrueAndLedgerType(@Param("gstins") List<String> gstins, @Param("ledgerType") String ledgerType);

	
	//to get single result
	@Query("SELECT e FROM LedgerSaveToGstnRcmEntity e WHERE e.gstin = :gstin AND e.isActive = true AND e.ledgerType = :ledgerType")
	LedgerSaveToGstnRcmEntity findByGstinAndIsActiveTrueAndLedgerTp(@Param("gstin") String gstin, @Param("ledgerType") String ledgerType);

}
