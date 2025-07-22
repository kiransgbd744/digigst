package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAtProcEntity;
/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr1AAtAtaProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AAtAtaProcRepository extends CrudRepository<Gstr1AAtProcEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr1AAtProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);

	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.adAmt),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM Gstr1AAtProcEntity doc WHERE "
			+ "doc.gstnBifurcation IN('AT') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getAt(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.adAmt),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM Gstr1AAtProcEntity doc WHERE "
			+ "doc.gstnBifurcation IN('ATA') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getAta(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	
	@Modifying
	@Query("UPDATE Gstr1AAtProcEntity doc SET doc.isSaved=true, doc.isError = false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE Gstr1AAtProcEntity doc SET doc.isError=true, doc.isSaved=false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
		
}
