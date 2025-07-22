package com.ey.advisory.app.data.repositories.client.asprecon;

import java.math.BigDecimal;
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

import com.ey.advisory.app.data.entities.client.asprecon.SaveRcmOpeningBalEntity;

@Repository("SaveRcmOpeningBalRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SaveRcmOpeningBalRepository
		extends JpaRepository<SaveRcmOpeningBalEntity, Long>,
		JpaSpecificationExecutor<SaveRcmOpeningBalEntity> {

	@Modifying
	@Query("UPDATE SaveRcmOpeningBalEntity s SET "
			+ "s.gstin = :gstin, s.saveStatus = :saveStatus,s.igst = :igst, "
			+ "s.cgst = :cgst, s.sgst = :sgst,s.cess = :cess,s.initiatedOn = :initiatedOn, "
			+ "s.completedOn = :completedOn,s.errMsg = :errMsg,s.isActive = :isActive "
			+ "WHERE s.id = :id")
	void updateSaveRcmOpeningBal(
			@Param("gstin") String gstin,
			@Param("saveStatus") String saveStatus,
			@Param("igst") BigDecimal igst,
			@Param("cgst") BigDecimal cgst,
			@Param("sgst") BigDecimal sgst,
			@Param("cess") BigDecimal cess,
			@Param("initiatedOn") LocalDateTime initiatedOn,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("errMsg") String errMsg,
			@Param("isActive") Boolean isActive,
			@Param("id") Long id );
	
	@Modifying
	@Query("UPDATE SaveRcmOpeningBalEntity s SET s.isActive = false WHERE s.gstin = :gstin AND s.isActive = true")
	void softDeleteByGstin(@Param("gstin") String gstin);


	List<SaveRcmOpeningBalEntity> findByGstinInAndIsActiveTrue(
			List<String> gstinList);

	List<SaveRcmOpeningBalEntity> findBySaveStatus(String saveStatus);

	List<SaveRcmOpeningBalEntity> findByInitiatedOnBefore(
			LocalDateTime initiatedBefore);

}
