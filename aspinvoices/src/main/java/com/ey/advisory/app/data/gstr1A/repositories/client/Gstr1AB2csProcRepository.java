package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AB2csProcEntity;

/**
 * This class is responsible for B2cs summary related repository operations.
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr1AB2csProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AB2csProcRepository extends CrudRepository<Gstr1AB2csProcEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr1AB2csProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = :modifiedOn WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids, 
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM Gstr1AB2csProcEntity doc WHERE "
			+ "doc.gstnBifurcation in('B2CS') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getB2cs(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
		
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM Gstr1AB2csProcEntity doc WHERE "
			+ "doc.gstnBifurcation in('B2CSA') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getB2csa(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	
	@Modifying
	@Query("UPDATE Gstr1AB2csProcEntity doc SET doc.isSaved=true,doc.modifiedOn = "
			+ ":modifiedOn, doc.isError=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE Gstr1AB2csProcEntity doc SET doc.isError=true, doc.modifiedOn = "
			+ ":modifiedOn, doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	
	
	
	
}
