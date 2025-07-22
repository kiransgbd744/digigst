/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aErpIsdInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aErpIsdInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aErpIsdInvoicesRepository extends CrudRepository<GetGstr2aErpIsdInvoicesHeaderEntity, Long> {

	// Curently isDelete is not added in table
	@Modifying
	@Query("UPDATE GetGstr2aErpIsdInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.cgstin = :cgstin AND b.returnPeriod = :returnPeriod")
	void softlyDeleteIsdHeader(@Param("cgstin") String cgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr2aErpIsdInvoicesHeaderEntity where gstin=:sgstin and cgstin=:cgstin and documentDate=:invDate and documentNumber=:invNum and isdDocumentType=:invType")
	List<GetGstr2aErpIsdInvoicesHeaderEntity> findByInvoiceKey(
			@Param("sgstin") String gstin, @Param("cgstin") String cgstin,
			@Param("invDate") LocalDate documentDate,
			@Param("invNum") String documentNumber,
			@Param("invType") String isdDocumentType);

	@Query("SELECT COUNT(*) FROM GetGstr2aErpIsdInvoicesHeaderEntity "
			+ "WHERE gstin=:gstin AND returnPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);


	@Modifying
	@Query("UPDATE GetGstr2aErpIsdInvoicesHeaderEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds) ")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds);
}